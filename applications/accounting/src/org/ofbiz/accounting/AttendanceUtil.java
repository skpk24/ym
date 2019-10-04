package org.ofbiz.accounting;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

public class AttendanceUtil{

    public static final String module = AttendanceUtil.class.getName();
	public static String storeSignIn(HttpServletRequest request,
			HttpServletResponse response) {
	GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
	String note=request.getParameter("note");
	String partyId=request.getParameter("partyId");
	String status="SIGN_IN";
	//System.out.println("Request Comes#######################"+request.getParameter("partyId"));
	try{
      GenericValue Attendance=delegator.makeValue("EmplAttendanceRecord");	
      Attendance.set("Id", delegator.getNextSeqId("EmplAttendanceRecord"));
      Attendance.set("partyId", partyId);
      Attendance.set("signInNote",note);
      Attendance.set("status",status);
      Attendance.set("signInTime",UtilDateTime.nowTimestamp());
      Attendance.set("duration",BigDecimal.ZERO);
      Attendance.create();
	 }catch (Exception e) {
		e.printStackTrace();
	}
	return "success";
	}	
public static String storeSignOut(HttpServletRequest request,
			HttpServletResponse response) {
	GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
	GenericValue 	Attendance=null;
	String note=request.getParameter("note1");
    String partyId=request.getParameter("partyId");
	String status="SIGN_OUT";
		try{
			List attList=delegator.findByAnd("EmplAttendanceRecord",UtilMisc.toMap("partyId",request.getParameter("partyId"), "status", "SIGN_IN"));
			if(attList.isEmpty()){
	      Attendance=delegator.makeValue("EmplAttendanceRecord");	
	      Attendance.set("Id", delegator.getNextSeqId("EmplAttendanceRecord"));
	      Attendance.set("partyId", partyId);
	      Attendance.set("signInNote",note);
	      Attendance.set("status","SIGN_IN");
	      Attendance.set("signInTime",UtilDateTime.nowTimestamp());
	      Attendance.create();
			}else{
				Attendance=(GenericValue)attList.get(0);
				Attendance.set("signOutNote",note);
			    Attendance.set("status",status);
			    Attendance.set("signOutTime",UtilDateTime.nowTimestamp());
			    Attendance.set("duration",new BigDecimal((UtilDateTime.getInterval(Attendance.getTimestamp("signInTime"),UtilDateTime.nowTimestamp()))/1000));
			    Attendance.store();
			}
		 }catch (Exception e) {
				e.printStackTrace();
			}
		return "success";
	}	

public static String Round(double Rval) {
	BigDecimal decimal=new BigDecimal(Rval);
	Double db=decimal.doubleValue();
	db=(db/(60*60));
	decimal=new BigDecimal(db);
	decimal=decimal.setScale(4,BigDecimal.ROUND_HALF_UP);

	   return decimal.toString();
	 }


public static String getWorkedHours(String partyId,Timestamp formDate){
	BigDecimal hours=BigDecimal.ZERO;
	GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
	List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
	try{
	dateCondiList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(formDate.toString(), "Timestamp", null, null)));
	dateCondiList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
	EntityCondition entityCondition = EntityCondition.makeCondition(dateCondiList);
	List<GenericValue> attenList = delegator.findList("EmplAttendanceRecord", entityCondition, null,UtilMisc.toList("-lastUpdatedStamp"), null, false);
    if(attenList!=null && attenList.size()>0){
	 for(GenericValue gv:attenList){
		 BigDecimal hr=gv.getBigDecimal("duration");
		 if(hr!=null)
		 hours=hours.add(hr);
	   } 
	  }
	}
	catch (GeneralException e) {
	    e.printStackTrace();
	}
	if(hours!=BigDecimal.ZERO){
		Double db=hours.doubleValue();
		db=(db/(60*60));
		hours=new BigDecimal(db);
		hours=hours.setScale(4,BigDecimal.ROUND_HALF_UP);
	}
	return hours.toString();
}



public static  String attendanceReoprtCSV(HttpServletRequest request, HttpServletResponse response){
	try{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
	String partyId=request.getParameter("partyId");
	String deptId=request.getParameter("deptId");
	String firstName=request.getParameter("firstName");
	//System.out.println("the party\n\n\n\n\n\n\n\n"+partyId+deptId+firstName);
	
		String reportType = request.getParameter("reportType");
		if(reportType != null){
			
	     if(reportType.equalsIgnoreCase("Daily Attendance Report")){
			fromDate = request.getParameter("minDate");
			thruDate  = request.getParameter("maxDate");
			
			Integer intDay = new Integer(date.getDate());
			Integer intMonth = new Integer(date.getMonth()+1);
			Integer intYear = new Integer(date.getYear()+1900);
			
			String  day = intDay.toString();
			String  month = intMonth.toString();
			String  year = intYear.toString();
			if(fromDate == null)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			if(fromDate != null && fromDate.length()<19)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			
			if(thruDate == null)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			if(thruDate != null && thruDate.length()<19)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			}
		 if(reportType.equalsIgnoreCase("Weekly Attendance Report")){
			 int week;
				try {
					week = Integer.parseInt(request.getParameter("week"));
				} catch (Exception e) {
					week = 01;
					Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
				}
				
				String month = request.getParameter("month");
				String year = request.getParameter("year");
				
				if(week == 01){
					fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
					thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
				}else if(week == 02){
					fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
					thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
				}else if(week == 03){
					fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
					thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
				}else if(week == 04){
					fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
					thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
				}
			}
		 if(reportType.equalsIgnoreCase("Monthly Attendance Report")){
			 String month = request.getParameter("month");
				String year  = request.getParameter("year");
				int intfromYear, actualFromMonth;
				try {
					actualFromMonth  = Integer.parseInt( month );
					intfromYear = Integer.parseInt( year );
				} catch (Exception e) {
					Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
					actualFromMonth  = 01;
					intfromYear = 2012;
					e.printStackTrace();
				}
				int intmonth = actualFromMonth -1;
				
				GregorianCalendar calendar =new  GregorianCalendar();
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH,intmonth);
				calendar.set(Calendar.YEAR,intfromYear);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(GregorianCalendar.MINUTE, 0);
				calendar.set(GregorianCalendar.SECOND, 0);
				Debug.logInfo("###### calendar "+calendar, module);
				
				fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
				thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
		 }
		 String firstNameReal="";
		
		 List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.GREATER_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
		 dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
		 if(partyId!=null && partyId!="")
			 dateCondiList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		 if(deptId!=null && deptId!="")
			 dateCondiList.add(EntityCondition.makeCondition("partyGroup",EntityOperator.EQUALS,deptId));
		 if(firstName!=null && firstName!="")
		 {
			 //System.out.println("the first Name\n\n\n"+firstName);
			 GenericValue v=delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",firstName));
			 firstNameReal=v.getString("firstName");
			 dateCondiList.add(EntityCondition.makeCondition("partyName",EntityOperator.EQUALS,firstNameReal));
		 }
		 
		 //condition.add(new EntityExpr("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		 EntityCondition entityCondition = EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
		 List<GenericValue> attenList = delegator.findList("EmplAttendanceRecord", entityCondition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
		//System.out.println("the List of the attenList\n\n\n\n"+attenList);
		 
		 response.setContentType("application/excel");
			if(reportType != null)
				response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
			else
			response.setHeader("Content-disposition","attachment;filename=attendanceReport.csv");
			StringBuffer data = new StringBuffer();
			data.append("\n");
			data.append("\n");
			data.append("#--------------------------------------------------------------");
			data.append("\n");
			
			data.append("\"Name\"");
			data.append(',');
			data.append("\"SignIn\"");
			data.append(',');
			data.append("\"SignInNote\"");
			data.append(',');
			data.append("\"SignOut\"");
			data.append(',');
			data.append("\"SignOutNote\"");
			data.append(',');
			data.append("\"Department\"");
			data.append(',');
			data.append("Duration(Hours)");
			data.append('\n');
		 BigDecimal decimal=BigDecimal.ZERO;
		 
		for(GenericValue attendance: attenList){
			GenericValue person=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",attendance.getString("partyId")));
			String fullName="";
			if(person!=null){
				fullName=person.getString("firstName");
			}else{
				fullName=" "+fullName+person.getString("lastName");
			}
			data.append("\""+fullName+"\"");
			data.append(',');
			data.append("\""+attendance.getString("signInTime")+"\"");
			data.append(',');
			data.append("\""+attendance.getString("signInNote")+"\"");
			data.append(',');
			data.append("\""+attendance.getString("signOutTime")+"\"");
			data.append(',');
			data.append("\""+attendance.getString("signOutNote")+"\"");
			data.append(',');
			//List <GenericValue>department = delegator.findByAnd("Employment",UtilMisc.toMap("partyIdTo",attendance.getString("partyId")));
			if(attendance.getString("partyGroup")!=null){
			data.append(attendance.getString("partyGroup"));
			data.append(',');
			}else{
				data.append("No Department Assigned Yet");
				data.append(',');
			}
			
			decimal=attendance.getBigDecimal("duration");
			Double db=decimal.doubleValue();
			db=(db/(60*60));
			decimal=new BigDecimal(db);
			decimal=decimal.setScale(4,BigDecimal.ROUND_HALF_UP);
			data.append("\""+decimal.toString()+"\"");
			data.append('\n');
		}
			OutputStream out = response.getOutputStream();
			out.write(data.toString().getBytes());
			out.flush();
	  }
	}catch (Exception e) {
		e.printStackTrace();
	}

   return "success";
}



}