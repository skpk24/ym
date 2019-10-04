package org.ofbiz.accounting.payroll;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class PayrollSetting {
	
	public static String calendarSettingWeek(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String calendarId = request.getParameter("calendarId");
		String weekDay = request.getParameter("weekDay");
		String weekExclude = request.getParameter("weekExclude");
		Debug.log("\n\n ################# calendarId ="+calendarId);
		Debug.log("\n\n ################# weekDay ="+weekDay);
		Debug.log("\n\n ################# weekExclude ="+weekExclude);
		try
		{
		  PrintWriter out = response.getWriter();
		  out.print("success");
		  GenericValue employeeCalendar = delegator.makeValue("EmployeeCalendar");	
		  if(!UtilValidate.isEmpty(calendarId))
		  {
		   employeeCalendar.set("calendarId", calendarId);
		   employeeCalendar.set("calendarTypeId", "WEEKLY_PAY");
		   employeeCalendar.set("calendarValue",weekDay );
		   employeeCalendar.store();
		  
		  }
		  else
		  {
			   calendarId = delegator.getNextSeqId("EmployeeCalendar") ;
			   employeeCalendar.set("calendarId", calendarId);
			   employeeCalendar.set("calendarTypeId", "WEEKLY_PAY");
			   employeeCalendar.set("calendarValue",weekDay );
			   employeeCalendar.create();
		  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}

	public static String calendarSettingMonth(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String calendarId = request.getParameter("calendarId");
		String monthDay = request.getParameter("monthDay");
		String monthExclude = request.getParameter("monthExclude");
		String monthDate = request.getParameter("monthDate");
		
		Debug.log("\n\n ################# calendarId ="+calendarId);
		Debug.log("\n\n ################# monthDate ="+monthDate);
		Debug.log("\n\n ################# monthDay ="+monthDay);
		Debug.log("\n\n ################# monthExclude ="+monthExclude);
		try
		{
		  PrintWriter out = response.getWriter();
		  out.print("success");
		  GenericValue employeeCalendar = delegator.makeValue("EmployeeCalendar");	
		  if(!UtilValidate.isEmpty(calendarId))
		  {
		   employeeCalendar.set("calendarId", calendarId);
		   employeeCalendar.set("calendarTypeId", "MONTHLY_PAY");
		   if(!UtilValidate.isEmpty(monthDate))
		   {
			   Debug.log("");
			   employeeCalendar.set("calendarValue",monthDate );
		   }
		   else{
			   employeeCalendar.set("calendarValue",monthDay );
		     }
		   employeeCalendar.store();
		  }
		  else
		  {
			   calendarId = delegator.getNextSeqId("EmployeeCalendar") ;
			   employeeCalendar.set("calendarId", calendarId);
			   employeeCalendar.set("calendarTypeId", "MONTHLY_PAY");
			   if(!UtilValidate.isEmpty(monthDate))
			   {
				   employeeCalendar.set("calendarValue",monthDate );
			   }
			   else{
				   employeeCalendar.set("calendarValue",monthDay );
			     }
			   employeeCalendar.create();
		  }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}



public static String getCalendarSettig(HttpServletRequest request, HttpServletResponse response)
{
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	 PrintWriter out;
	try
	{
	   out = response.getWriter();
	 
	  List<GenericValue> calendarList = delegator.findList("EmployeeCalendar", EntityCondition.makeCondition("calendarTypeId",EntityOperator.EQUALS,"MONTHLY_PAY"), null, null, null, false);
	  if(!UtilValidate.isEmpty(calendarList))
	  {
		 request.setAttribute("monthlyCalendar", calendarList.get(0));
		 
	  }
	  //TODO  get calendar weekly pay
	   calendarList = delegator.findList("EmployeeCalendar", EntityCondition.makeCondition("calendarTypeId",EntityOperator.EQUALS,"WEEKLY_PAY"), null, null, null, false);
	  if(!UtilValidate.isEmpty(calendarList))
	  {
		 request.setAttribute("weeklyCalendar", calendarList.get(0));
		 
	  }
	  calendarList = delegator.findList("EmployeeCalendar", EntityCondition.makeCondition("calendarTypeId",EntityOperator.EQUALS,"FINANCIAL_YEAR"), null, null, null, false);
	  if(!UtilValidate.isEmpty(calendarList))
	  {
		 GenericValue finYear =  calendarList.get(0); 
		 String []finValue  = ((String)finYear.get("calendarValue")).split("-");
		 request.setAttribute("fromDate", finValue[0]);
		 request.setAttribute("throDate", finValue[1]);
		 request.setAttribute("finYearcalendarId",finYear.get("calendarId") );
		 
	  }
	  out.print("success");
	}
	catch(Exception e)
	{
		e.printStackTrace();
		
	}
	 
	return "success";
}
//TODO set Financial year

public static String setFinancialYear(HttpServletRequest request, HttpServletResponse response)
{
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	PrintWriter out;
	try
	{
	   out = response.getWriter();
	   String calendarId = request.getParameter("finYearcalendarId");
	   String fromDate = request.getParameter("fromDate");
	   String throDate = request.getParameter("throDate");
	   GenericValue calendarSetting = delegator.makeValue("EmployeeCalendar");	
	   if(calendarId!=null && calendarId!="")
	   {
		   calendarSetting.put("calendarId",calendarId);
		   calendarSetting.put("calendarTypeId", "FINANCIAL_YEAR");
		   calendarSetting.put("calendarValue",fromDate+"-"+throDate);
		   calendarSetting.store();
	   }
	   else
	   {
		   calendarId = delegator.getNextSeqId("EmployeeCalendar");	
		   calendarSetting.put("calendarId",calendarId);
		   calendarSetting.put("calendarTypeId", "FINANCIAL_YEAR");
		   calendarSetting.put("calendarValue",fromDate+"-"+throDate);
		   calendarSetting.create();
	   }
	   
	   String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Personnel><Employee type=\"permanent\"><Name>Seagull</Name> <Id>3674</Id><Age>34</Age></Employee></Personnel>";

	   out.print("success");
	}
	catch(Exception e)
	{
		
	}
	return "success";
}

//TODO  get  Employee payroll detail 
public static String getEmpPayDetail(HttpServletRequest request, HttpServletResponse response)
{
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	String returnMessage ="";
	try
	{
		String partyId  =request.getParameter("partyId");
		GenericValue empSal = delegator.findByPrimaryKey("EmployeeSalary", UtilMisc.toMap("employeeId", partyId));
		if(!UtilValidate.isEmpty(empSal))
		{
		 //List  empSalAttr= delegator.	
		}
		
	}
	catch(Exception e)
	{
		
	}
	return returnMessage;
}

//TODO add employee pay detail  
public static String addEmpPayDetail(HttpServletRequest request, HttpServletResponse response)
{
	Debug.log("\n\n ########################### addEmpPayDetail ");
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	PrintWriter out;
	try
	{
	   String  quantity= request.getParameter("quantity");	
	   String  rate= request.getParameter("rate");	
	   String  pname= request.getParameter("pname");	
	   String description = request.getParameter("description");	
	   String  invoiceTypeId= request.getParameter("invoiceTypeId");	
	   String  partyId= request.getParameter("partyId");
	   String payFrequency = request.getParameter("payFrequency");
	   Debug.log("\n\n ########################### addEmpPayDetail partyId="+partyId+"\trate="+rate+"\tinvoiceTypeId"+invoiceTypeId);
	   //TODO check if employee has entry in employee salary table or not
	   GenericValue empSal = delegator.findByPrimaryKey("EmployeeSalary", UtilMisc.toMap("employeeId", partyId));
	   if(empSal!=null && empSal.size()>0)
	   {
		  GenericValue empSalAttr = delegator.makeValue("EmplSalAttribute"); 
		  empSalAttr.set("employeeId", partyId);
		  empSalAttr.set("attrName", invoiceTypeId);
		  empSalAttr.set("attrValue",Long.toString(Long.parseLong(quantity)*Long.parseLong(rate)));
		  empSalAttr.set("qunatity", quantity);
		  empSalAttr.set("rate", rate);
		  empSalAttr.set("name",pname );
		  empSalAttr.set("description",description );
		  empSalAttr.set("attrType","INCOME" );
		  empSalAttr.create();
		  
	   }
	   else{
		   empSal = delegator.makeValue("EmployeeSalary");
		   empSal.set("employeeId", partyId);
		   empSal.set("payFrequency", payFrequency);
		   empSal.create();
		   GenericValue empSalAttr = delegator.makeValue("EmplSalAttribute"); 
		  empSalAttr.set("employeeId", partyId);
		  empSalAttr.set("attrName", invoiceTypeId);
		  empSalAttr.set("attrValue",Long.toString(Long.parseLong(quantity)*Long.parseLong(rate)));
		  empSalAttr.set("qunatity", quantity);
		  empSalAttr.set("rate", rate);
		  empSalAttr.set("name",pname );
		  empSalAttr.set("description",description );
		  empSalAttr.set("attrType","INCOME" );
		  empSalAttr.create();
		   
		   
	   }
	   out = response.getWriter();
	   
	   out.print("success");
	   return "success";
	}catch(Exception e)
	{
		e.printStackTrace();
		return "success";
	}
	
}
//TODO removeEmpIncome 
public static String removeEmpIncome(HttpServletRequest request, HttpServletResponse response)
{
	Debug.log("\n\n ########################### addEmpPayDetail ");
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	PrintWriter out;
	try
	{
	   String  partyId= request.getParameter("partyId");	
	   String  attrName= request.getParameter("attrName");	
	   int i= delegator.removeByAnd("EmplSalAttribute", UtilMisc.toMap("employeeId", partyId,"attrName",attrName));
	   Debug.log("\n\n the number of row removed ="+i);
	   
	  out = response.getWriter();
	   out.print("success");
	}
	catch(Exception e)
	{
		
	}
	return "success";
}

//TODO edit Employment Detail 
public static String editEmploymentDetail(HttpServletRequest request, HttpServletResponse response)
{
	Debug.log("\n\n ###########################  edit Employment Detail  ");
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	PrintWriter out;
	try
	{
		out = response.getWriter();
	   String  partyId= request.getParameter("partyId");	
	   String  payCycle= request.getParameter("payCycle");	
	   String  paymentMethod= request.getParameter("paymentMethod");	
	   String  acName= request.getParameter("acName");	
	   String  acNumber= request.getParameter("acNumber");	
	   String  sortCode= request.getParameter("sortCode");	
	   java.util.Date joiningDate = new java.util.Date(request.getParameter("joiningDate"));
	  
	   Debug.log("\n ## payCycle="+payCycle+"\t paymentMethod="+paymentMethod);
	   GenericValue empSal = delegator.findByPrimaryKey("EmployeeSalary", UtilMisc.toMap("employeeId", partyId));
	   Debug.log("\n\n ########## empSal="+empSal+"\n\n ");
	   
	   if(empSal!= null && empSal.size()>0)
	   {
		   Debug.log("\n\n ################# insite if");
		  // empSal = delegator.makeValue("EmployeeSalary");
		   empSal.set("employeeId",partyId);
		   empSal.set("payFrequency", payCycle);
		   empSal.set("payFrequency", payCycle);
		   empSal.set("paymentMethod", paymentMethod);
		   empSal.set("accountName", acName);
		   empSal.set("accountNumber", acNumber);
		   empSal.set("startCode", sortCode);
		   empSal.set("joiningDate", new java.sql.Date(joiningDate.getYear(),joiningDate.getMonth(),joiningDate.getDate()));
		   Debug.log("\n\n ########## empSal="+empSal+"\n\n ");
		   empSal.store();
	   }else{
		   Debug.log("\n\n ################# insite else");
		   empSal = delegator.makeValue("EmployeeSalary");
		   empSal.set("employeeId", partyId);
		   empSal.set("payFrequency", payCycle);
		   empSal.set("payFrequency", payCycle);
		   
		   empSal.set("paymentMethod", paymentMethod);
		   empSal.set("acName", acName);
		   empSal.set("acNumber", acNumber);
		   empSal.set("startCode", sortCode);
		   empSal.set("joiningDate", new java.sql.Date(joiningDate.getYear(),joiningDate.getMonth(),joiningDate.getDate()));
		   empSal.create();
	   }
	   out.println("success");
	   
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return "success";
}	

//TODO testJqWidgets  
public static String testJqWidgets(HttpServletRequest request, HttpServletResponse response)
{
	Debug.log("\n\n ############################# in test jqWidgets");
    try
    {
    	List list = new ArrayList();
    	Map m1 = new HashMap();
    	m1.put("Math", 44);
    	m1.put("Physics",45);
    	m1.put("Hindi", 11);
    	m1.put("Name", "Vivek");
    	list.add(m1);
    	m1 = new HashMap();
    	m1.put("Math", 20);
    	m1.put("Physics",30);
    	m1.put("Hindi", 50);
    	m1.put("Name", "Vikash");
    	list.add(m1);
    	request.setAttribute("result", list);
    	
    	//for sending data in xml format to ajax response
    	/*String xml ="<?xml version=\"1.0\" encoding=\"utf-8\" ?><RecentTutorials><Tutorial author=\"The Reddest\"><Title>Silverlight and the Netflix API</Title> <Categories><Category>Tutorials</Category><Category>Silverlight 2.0</Category>  <Category>Silverlight</Category><Category>C#</Category> <Category>XAML</Category></Categories>  <Date>1/13/2009</Date></Tutorial><Tutorial author=\"The Fattest\"><Title>Controlling iTunes with AutoHotkey</Title><Categories><Category>Tutorials</Category><Category>AutoHotkey</Category></Categories><Date>12/12/2008</Date></Tutorial></RecentTutorials>";
    	response.setContentType("text/xml");
    	PrintWriter out = response.getWriter();
        out.print(xml);
        */
    }catch(Exception e)
    {
    	e.printStackTrace();
    }
	return "success";
}
}
