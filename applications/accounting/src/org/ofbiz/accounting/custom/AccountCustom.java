package org.ofbiz.accounting.custom;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.LocalDispatcher;
public class AccountCustom {
	
	public static String getAccounts(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String organizationPartyId = request.getParameter("organizationPartyId");
		EntityWhereString makeWhere  =EntityWhereString.makeConditionWhere("category='H' and gl_account_id in (SELECT o.gl_account_id FROM gl_account_organization o WHERE organization_party_id='"+organizationPartyId+"')");
		
		try
		{
		  PrintWriter out = response.getWriter();
		  List  <GenericValue>accounts= (List  <GenericValue>) delegator.findList("GlAccount", makeWhere.freeze(), UtilMisc.toSet("glAccountId","accountName","category"), UtilMisc.toList("accountName"), null, true);
		  Debug.log("\n\n accounts="+accounts);
		  request.setAttribute("accounts", accounts);
		  request.setAttribute("orgPartyId", organizationPartyId);
		  // out.print(accounts);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
//TODO get sub accounts 
	public static List  getSubAccounts(HttpServletRequest request, String parentAcId , String companyId)
	{
		Debug.log("\n\n get Sub Accounts parentAcId="+parentAcId+"\t companyId"+companyId);
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String organizationPartyId =companyId;
		EntityWhereString makeWhere  =EntityWhereString.makeConditionWhere(" parent_gl_account_id="+parentAcId+" and gl_account_id in (SELECT o.gl_account_id FROM gl_account_organization o WHERE organization_party_id='"+organizationPartyId+"')");
		 List  <GenericValue>accounts = null;
		try
		{
		
		  accounts= (List  <GenericValue>) delegator.findList("GlAccount", makeWhere.freeze(), UtilMisc.toSet("glAccountId","accountName","category"), UtilMisc.toList("accountName"), null, true);
		  Debug.log("\n\n sub accounts="+accounts);
		  request.setAttribute("accounts", accounts);
		  // out.print(accounts);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return accounts;
	}
	
	
	//TODO get total debit and credit transaction detail of particular accounts....
	
	public static Map  getAcTransDetail(HttpServletRequest request, String accountId , String companyId)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String organizationPartyId =companyId;
		EntityWhereString makeWhere  =EntityWhereString.makeConditionWhere("gl_account_id='"+accountId+"'  and ORGANIZATION_PARTY_ID='"+companyId+"'");
		 List  <GenericValue>accounts = null;
		 HashMap hm = new HashMap();
		try
		{   
			
			java.math.BigDecimal bigD;
			 
			 double  debit = 0.0;
		      double credit = 0.0;
			  accounts= (List  <GenericValue>) delegator.findList("AcctgTransEntry", makeWhere.freeze(), UtilMisc.toSet("amount","debitCreditFlag"), null, null, false);
			  if(accounts!=null)
			  {
				Iterator  <GenericValue>it = (Iterator  <GenericValue>)accounts.iterator();
				while(it.hasNext())
				{
					GenericValue acctg= it.next();
					if("D".equals((String)acctg.get("debitCreditFlag")))
							{
						     bigD= (java.math.BigDecimal)acctg.get("amount");
						     debit = debit +bigD.doubleValue();
							}
					if("C".equals((String)acctg.get("debitCreditFlag")))
					{
						bigD= (java.math.BigDecimal)acctg.get("amount");
						credit = credit +bigD.doubleValue();
					}	
				}
			  }
			  hm.put("C", credit);
			  hm.put("D", debit);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return hm;
		
	}
	
	
	
	public static String createInvoice(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		
		try
		{
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		  context.put("userLogin", userLogin);
		  context.put("invoiceDate",java.sql.Timestamp.valueOf(request.getParameter("invoiceDate")));
		  context.put("dueDate",java.sql.Timestamp.valueOf(request.getParameter("dueDate")));
		  context.put("currencyUomId",request.getParameter("currencyUomId"));
		  context.put("partyIdFrom",request.getParameter("partyIdFrom"));
		  context.put("description",request.getParameter("description"));
		  context.put("statusId",request.getParameter("statusId"));
		  context.put("invoiceTypeId",request.getParameter("invoiceTypeId"));
		  context.put("partyId",request.getParameter("partyId"));
		  context.put("invoiceMessage",request.getParameter("invoiceMessage"));
		  Debug.log("\n\n create invoice ="+context);
		  Map result = dispatcher.runSync("createInvoice", context);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  Debug.log("\n\n################ result="+result);
		  // out.print(accounts);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
//TODO create createInvoiceItem
	public static String createInvoiceItem(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		String returnMessage="";
		try
		{
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		 
		  
		  context.put("invoiceId",request.getParameter("invoiceId"));
		  context.put("productId",request.getParameter("productId"));
		  context.put("invoiceItemTypeId",request.getParameter("invoiceItemTypeId"));
		  context.put("quantity",request.getParameter("qua)ntity"));
		  context.put("amount",request.getParameter("amount"));
		  context.put("description",request.getParameter("description"));
		  context.put("userLogin", userLogin);
		  Debug.log("\n\n############### context="+context);
		  Map result = dispatcher.runSync("createInvoiceItem", context);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  returnMessage="success";
		}catch(Exception e)
		{
			returnMessage="error";
			//e.printStackTrace();
		}
		return returnMessage;
	}
	
	//TODO create updateInvoiceItem
	public static String updateInvoiceItem(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		String returnMessage="";
		try
		{ 
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		  context.put("invoiceId",request.getParameter("invoiceId"));
		  context.put("invoiceItemSeqId",request.getParameter("invoiceItemSeqId"));
		  context.put("productId",request.getParameter("productId"));
		  context.put("invoiceItemTypeId",request.getParameter("invoiceItemTypeId"));
		  context.put("quantity",request.getParameter("quantity"));
		  context.put("amount",request.getParameter("amount"));
		  context.put("description",request.getParameter("description"));
		  context.put("userLogin", userLogin);
		  Debug.log("\n\n############### updateInvoiceItem context="+context);
		  Map result = dispatcher.runSync("updateInvoiceItem", context);
		  Debug.log("\n\n result = "+result);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  returnMessage="success";
		}catch(Exception e)
		{
			returnMessage="error";
			//e.printStackTrace();
		}
		return returnMessage;
	}
//TODO remove invoice itme. 
	public static String removeInvoiceItem(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		String returnMessage="";
		try
		{ 
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		  context.put("invoiceId",request.getParameter("invoiceId"));
		  context.put("invoiceItemSeqId",request.getParameter("invoiceItemSeqId"));
		 
		  context.put("userLogin", userLogin);
		  Debug.log("\n\n############### removeInvoiceItem context="+context);
		  Map result = dispatcher.runSync("removeInvoiceItem", context);
		  Debug.log("\n\n result = "+result);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  returnMessage="success";
		}catch(Exception e)
		{
			returnMessage="error";
			//e.printStackTrace();
		}
		return returnMessage;
	}
	
	public static String createInvoiceAr(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		
		try
		{
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		  context.put("userLogin", userLogin);
		  context.put("invoiceDate",java.sql.Timestamp.valueOf(request.getParameter("invoiceDate")));
		  context.put("dueDate",java.sql.Timestamp.valueOf(request.getParameter("dueDate")));
		  context.put("currencyUomId",request.getParameter("currencyUomId"));
		  context.put("partyIdFrom",request.getParameter("partyIdFrom"));
		  context.put("description",request.getParameter("description"));
		  context.put("statusId",request.getParameter("statusId"));
		  context.put("invoiceTypeId",request.getParameter("invoiceTypeId"));
		  context.put("partyId",request.getParameter("partyId"));
		  context.put("invoiceMessage",request.getParameter("invoiceMessage"));
		  Map result = dispatcher.runSync("createInvoice", context);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  Debug.log("\n\n################ result="+result);
		  // out.print(accounts);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
	
//TODO set invoice status  here
	public static String  setInvoiceStatus(HttpServletRequest request, HttpServletResponse response)
	{
		//GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ setInvoiceStatus="+userLogin);
		
		try
		{
		  PrintWriter out = response.getWriter();
		  Map context = new HashMap();
		  context.put("userLogin", userLogin);
		  context.put("invoiceId",request.getParameter("invoiceId"));
		  context.put("statusId",request.getParameter("statusId"));
		  Map result = dispatcher.runSync("setInvoiceStatus", context);
		  Debug.log("\n\n################### set invoice status ="+result);
		}catch(Exception e)
		{
			
		}
		
		return "success";	
	}
	
	public static String createPaymentAr(HttpServletRequest request, HttpServletResponse response)
	{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
		Debug.log("\n\n################ userLogin="+userLogin);
		
		try
		{
		  PrintWriter out = response.getWriter();
		  String amt=request.getParameter("amount");
		  BigDecimal amount = new BigDecimal(amt);
		  //System.out.println("checkin \n\n\n"+request.getParameter("paymentMethodTypeId"));

	 Map<String, Object> createPaymentcontext = new HashMap<String, Object>();
		    createPaymentcontext.put("paymentTypeId", request.getParameter("paymentTypeId"));
		    
		    createPaymentcontext.put("paymentMethodTypeId",request.getParameter("paymentMethodTypeId"));
		    createPaymentcontext.put("partyIdTo",request.getParameter("partyIdTo"));
		    createPaymentcontext.put("partyIdFrom", request.getParameter("partyIdFrom"));
		    createPaymentcontext.put("statusId", request.getParameter("statusId"));
		    createPaymentcontext.put("amount",amount);
		    createPaymentcontext.put("userLogin", userLogin);
		    Map<String, Object> result = dispatcher.runSync("createPayment", createPaymentcontext);
		  List l= new ArrayList();
		  l.add(result);
		  request.setAttribute("result", l);
		  Debug.log("\n\n################ result="+result);
		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
	}
	
	
	//TODO  change  Quote status 9243249419
	public static String setQuoteStatus(HttpServletRequest request, HttpServletResponse response)
	{
		String returnMessage="";
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		try{
			GenericValue quote = delegator.findByPrimaryKey("Quote", UtilMisc.toMap("quoteId", request.getParameter("quoteId")));
			quote.put("statusId", request.getParameter("statusId"));
			quote.store();
			returnMessage="success";
			
		}catch(Exception e)
		{
			returnMessage="error";
		}
		
		return returnMessage;
	}
	
	  
    

}
