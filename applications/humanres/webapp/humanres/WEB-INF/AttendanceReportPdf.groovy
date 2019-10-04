package org.ofbiz.accounting;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
String fromDate = request.getParameter("minDate");
String thruDate  = request.getParameter("maxDate");
String partyId=request.getParameter("partyId");
	String deptId=request.getParameter("deptId");
	String firstName=request.getParameter("firstName");


Date date = new Date();
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
		fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
		thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
 }
 List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
 dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.GREATER_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
 dateCondiList.add(EntityCondition.makeCondition("signInTime",EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
  if(partyId!=null && partyId!="")
			 dateCondiList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		 if(deptId!=null && deptId!="")
			 dateCondiList.add(EntityCondition.makeCondition("partyGroup",EntityOperator.EQUALS,deptId));
		 if(firstName!=null && firstName!="")
		 {
			 print("the first Name\n\n\n"+firstName);
			 GenericValue v=delegator.findByPrimaryKey("Person",[partyId:firstName]);
			 firstNameReal=v.get("firstName");
			 dateCondiList.add(EntityCondition.makeCondition("partyName",EntityOperator.EQUALS,firstNameReal));
		 }
 
 EntityCondition entityCondition = EntityCondition.makeCondition(dateCondiList);
 List<GenericValue> attenList = delegator.findList("EmplAttendanceRecord", entityCondition, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
 context.attenList=attenList;
 context.reportType=reportType;
 print("the list list \n\n\n\n"+attenList);
 print "###################### ATTEN LIST ##########"+attenList;
}