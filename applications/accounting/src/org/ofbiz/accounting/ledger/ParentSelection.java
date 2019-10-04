package org.ofbiz.accounting.ledger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;


public class ParentSelection {
	public static String parentSelection(HttpServletRequest request,
	  	HttpServletResponse response) throws GenericEntityException, IOException {
		 List<GenericValue> list=new ArrayList<GenericValue>();
		 List list2=new ArrayList();
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		
		PrintWriter out=response.getWriter();
		HashMap result=new HashMap();
		List list1=new ArrayList();

		String 	buffer="<td class=label> <span>Parent Account</span></td><td><select name='parentAccount' id='parentAccount' onchange=javascript:validate()><option value='-1'>Select</option>"; 
			EntityCondition con=EntityCondition.makeCondition("category",EntityOperator.EQUALS,"H");
	     list=delegator.findList("GlAccount", con, UtilMisc.toSet("glAccountId","accountName"), null, null, false);
		//System.out.println("ttradha"+list);
		
		//String xml ="<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
		if(list.size()>=1 ){
			
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			GenericValue gv = (GenericValue) iterator.next();
			buffer=buffer+"<option value='"+gv.getString("glAccountId")+"'>"+gv.getString("accountName")+"</option>"; 
			//xml = xml +"<AccountsRecords><mKey>"+(String)gv.getString("glAccountId")+"</mKey><mval>"+(String)gv.getString("accountName")+"</mval></AccountsRecords>";
			
			}
		//Debug.log("\n\n ##################### xml="+xml);
		// xml ="<?xml version=\"1.0\" encoding=\"utf-8\" ?><RecentTutorials><Tutorial author=\"The Reddest\"><Title>Silverlight and the Netflix API</Title> <Categories><Category>Tutorials</Category><Category>Silverlight 2.0</Category>  <Category>Silverlight</Category><Category>C#</Category> <Category>XAML</Category></Categories>  <Date>1/13/2009</Date></Tutorial><Tutorial author=\"The Fattest\"><Title>Controlling iTunes with AutoHotkey</Title><Categories><Category>Tutorials</Category><Category>AutoHotkey</Category></Categories><Date>12/12/2008</Date></Tutorial></RecentTutorials>";
	 
	    //request.setAttribute("result", list);
	   //out.print(xml);
     buffer=buffer+"</select></td>"; 
		  
		  out.println(buffer);
		}
		
	return "success";
	}

	
	public static String groupSelection(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException, IOException {
		 List<GenericValue> list=new ArrayList<GenericValue>();
		 List list2=new ArrayList();
		GenericDelegator delegator = (GenericDelegator) request
		.getAttribute("delegator");
		String buffer="";
		String buffer1;
		//System.out.println("radha");
		PrintWriter out=response.getWriter();
		HashMap result=new HashMap();
		List list1=new ArrayList();
		EntityCondition mainCond = null;
		String s=request.getParameter("parentId");
		//System.out.println("the parent\n\n\n\n"+s);

			buffer="<select name='groupAccount' id='groupAccount'><option value='-1'>Select</option>"; 
			//EntityCondition con=EntityCondition.makeCondition("category",EntityOperator.EQUALS,"G");
		//List<EntityCondition> andExprs = FastList.newInstance();
		List<EntityCondition> andExprs = new ArrayList<EntityCondition>();
		 andExprs.add(EntityCondition.makeCondition("category", EntityOperator.EQUALS, "G"));  
		 andExprs.add(EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, s));  
		 mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
	   list=delegator.findList("GlAccount",mainCond , UtilMisc.toSet("glAccountId","accountName"), null, null, false);
		//System.out.println("ttradha"+list);
		
		if(list.size()>=1 ){
			
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			GenericValue gv = (GenericValue) iterator.next();
			//result.put("key"+Integer.toString(i),(String)gv.getString("glAccountId"));
			//result.put("key"+Integer.toString(j),(String)gv.getString("accountName"));
			buffer=buffer+"<option value='"+gv.getString("glAccountId")+"'>"+gv.getString("accountName")+"</option>"; 
			
			}
		
buffer=buffer+"</select>"; 
		  
		  out.println(buffer);
		}



return "success";
	}
	
	public static String createAccount(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException, IOException {
		
		
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		
		GenericValue v1=delegator.makeValue("GlAccount");
	 String glAccountId=request.getParameter("glAccountId");
	 String glAccountTypeId=request.getParameter("accountType");
	 String glAccountClassId=request.getParameter("accountClassId");
	 String glresourceTypeId=request.getParameter("resourceType");
	 String glXbrlClassId=request.getParameter("xbrlClass");
	 String category=request.getParameter("category");
	 String parentAccount=request.getParameter("parentAccount");
	 //System.out.println("parentAccount\n\n\n\n\n\n"+parentAccount);
	 String groupAccount=request.getParameter("groupAccount");
	 //System.out.println("groupAccount\n\n\n\n\n\n"+groupAccount);
	 String glAccountName=request.getParameter("glAccountName");
	  
		v1.put("glAccountId", glAccountId);
		v1.put("glAccountTypeId", glAccountTypeId);
		v1.put("accountName", glAccountName);
		v1.put("accountCode", glAccountId);
		
		v1.put("glAccountClassId",glAccountClassId);
		v1.put("glResourceTypeId",glresourceTypeId);
		v1.put("glXbrlClassId",glXbrlClassId);
		v1.put("category", category);
		if(category.equals("H"))
		v1.put("parentGlAccountId", null);
		if(category.equals("G"))
			v1.put("parentGlAccountId", parentAccount);
		if(category.equals("A"))
		
			
			v1.put("parentGlAccountId", groupAccount);
		
		GenericValue v2=delegator.create(v1);
				
		if(v2!=null)
				return "success";
		else
			return "error";
		
		
		
		
		
		
	}
	
	
	
}
