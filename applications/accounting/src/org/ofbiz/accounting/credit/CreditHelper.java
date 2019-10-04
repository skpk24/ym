package org.ofbiz.accounting.credit;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;



public class CreditHelper {
	  public static final String module = CreditHelper.class.getName();
	  public final static String PropertiesFile ="general.properties";
	  
	  public static String calculatePendingCredits(HttpServletRequest request,HttpServletResponse response){
		   GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
	      //  List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        request.setAttribute("pendingCredits", transList);
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        request.setAttribute("totalPendingCredit", totalPendingCredit.toString());
	        return "success";
	  }

	  
	  public static String showCustomerWiseCredit(HttpServletRequest request,HttpServletResponse response){
		   GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
		   String customerId=request.getParameter("customerId");
	        //List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        exprs.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS,customerId));

	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        request.setAttribute("pendingCredits", transList);
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        request.setAttribute("totalPendingCredit", totalPendingCredit.toString());
	        return "success";
	  }
	  
	  
	  public static String customerCredit(HttpServletRequest request,HttpServletResponse response){
		   GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
	       HashMap<String,List<GenericValue>> customerWiseCredit=new HashMap<String, List<GenericValue>>();
           Map<String,List<GenericValue>> recivedCreditCustomerwise=new HashMap<String,List<GenericValue>>();
           Map<String,String> totalCustomerCredit=new HashMap<String, String>();
           Map<String,String> totalRecevieCustomerCredit=new HashMap<String, String>();
           Map<String,String> days=new HashMap<String, String>();
	
           try{
	    	List<GenericValue> customerList=delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId","BILL_TO_CUSTOMER"));
	    	Set<String> customerIdSet=new HashSet<String>();
	    	for(GenericValue customer:customerList){
		        
	    		//List exprs = FastList.newInstance();
	    		List exprs = new ArrayList();
		        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
		        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
		        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
		        exprs.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS,customer.getString("partyId")));
		        List orderBy = UtilMisc.toList("-createdStamp");
		        List <GenericValue>customerCreditlist = null;
		        	customerCreditlist = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
		        	if(customerCreditlist!=null && customerCreditlist.size()>0){
		            	customerWiseCredit.put(customer.getString("partyId"), customerCreditlist);
		            	customerIdSet.add(customer.getString("partyId"));
		    	        BigDecimal totalCredit=BigDecimal.ZERO;  
		    	        
		    	        for(GenericValue trs:customerCreditlist){
		    	        	totalCredit=totalCredit.add(trs.getBigDecimal("maxAmount"));
		    	        }
		    	        totalCustomerCredit.put(customer.getString("partyId"), totalCredit.toString());
		           }    
		            //exprs = FastList.newInstance();
		            exprs = new ArrayList();
			        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
			        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
			        exprs.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS,customer.getString("partyId")));
			        List orderBy1 = UtilMisc.toList("-createdStamp");
			        List <GenericValue>customerCreditRecivedlist = null;
		        try {
		        	customerCreditRecivedlist = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy1, null, false);
		            if(customerCreditRecivedlist!=null && customerCreditRecivedlist.size()>0){
		            	recivedCreditCustomerwise.put(customer.getString("partyId"),customerCreditRecivedlist);
		    	        BigDecimal totalrecivedCredit=BigDecimal.ZERO;  
		    	        
		    	        for(GenericValue trs:customerCreditRecivedlist){
		    	        	totalrecivedCredit=totalrecivedCredit.add(trs.getBigDecimal("maxAmount"));
		    	        }
		    	        totalRecevieCustomerCredit.put(customer.getString("partyId"), totalrecivedCredit.toString());

		            }
		          } catch (GenericEntityException e) {
		            Debug.logError(e, module);
		        }
	    	}// end of for loop
	    	
	    	
	    	
	    	request.setAttribute("totalRecevieCustomerCredit", totalRecevieCustomerCredit);
	    	request.setAttribute("totalCustomerCredit", totalCustomerCredit);
	    	request.setAttribute("recivedCreditCustomerwise", recivedCreditCustomerwise);
	    	request.setAttribute("customerWiseCredit", customerWiseCredit);
	    	request.setAttribute("customerIdSet", customerIdSet);
	    	int day=0;
	    	for(String cutomerId:customerIdSet){
	    		List<GenericValue> reciveList=recivedCreditCustomerwise.get(cutomerId);
	    		if(reciveList!=null && reciveList.size()>0){
	    		    List<GenericValue> creditpaid=(List<GenericValue>)customerWiseCredit.get(cutomerId);
	    		 if(creditpaid!=null && creditpaid.size()>0){
	    			 days = differenceBetweenTwoDays(cutomerId,creditpaid,days);
	    		   /* Timestamp startdate=creditpaid.get(0).getTimestamp("orderDate");
	    		    Timestamp enddate=reciveList.get(0).getTimestamp("createdDate");
	    		    if(startdate!=null && enddate!=null){
	    		    	Date start=(Date)UtilDateTime.toCalendar(startdate).getTime();   
	    		        Date end=(Date)(Date)UtilDateTime.toCalendar(enddate).getTime();
		    		   
	    		        int days1 =(int) start.getTime()/(60*60*24*1000);//find the number of days since the epoch.
	    		        int days2 =(int)end.getTime()/(60*60*24*1000);
	    		        //System.out.println("#### Satrts ##########"+start+"=="+end);
	    		        day=(days2-days1);
	    		        //System.out.println("#### DAYS ##########"+(days2-days1)+"=="+cutomerId);
	    		        if(day>=0){
	    		        	days.put(cutomerId, String.valueOf(day));
	    		        }else{
	    		        	days.put(cutomerId, String.valueOf("0"));
	    		        }
	    		    } */
	    		  }
	    		}
	    		else{
		    		List<GenericValue> creditList=customerWiseCredit.get(cutomerId);
		    		if(creditList!=null && creditList.size()>0){
		    		    List<GenericValue> credit=(List<GenericValue>)customerWiseCredit.get(cutomerId);
		    		 if(credit!=null && credit.size()>0){
		    			 days = differenceBetweenTwoDays(cutomerId,credit,days);
		    			/* 
		    			 
		    			 Timestamp startdate=credit.get(0).getTimestamp("orderDate");
		    		    Timestamp enddate=UtilDateTime.nowTimestamp();
		    		    if(startdate!=null && enddate!=null){
		    		    	
		    		    	Date start=(Date)UtilDateTime.toCalendar(startdate).getTime();   
		    		        Date end=(Date)(Date)UtilDateTime.toCalendar(enddate).getTime();
		    		        int days1 =(int) start.getTime()/(60*60*24*1000);//find the number of days since the epoch.
		    		        int days2 =(int)end.getTime()/(60*60*24*1000);
		    		        //System.out.println("#### Satrts ##########"+start+"=="+end);
		    		        day=(days1-days2);
		    		        //System.out.println("#### DAYS ##########"+(days2-days1)+"=="+cutomerId);
		    		        if(day>=0){
		    		        	days.put(cutomerId, String.valueOf(day));
		    		        }else{
		    		        	days.put(cutomerId, String.valueOf("0"));
		    		        }
		    		    }    */
		    		   }
		    		}	    			
	    		}
	    	}
	    	
	    	request.setAttribute("days", days);
		   }
		catch (Exception e) {
			e.printStackTrace();
		}
		   
		   
		   
		   return "success";
	  }

	  public static String dueCreditsByCustomer(GenericDelegator delegator,String customerId){
		    BigDecimal dueCredits=BigDecimal.ZERO;
	        //List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS,customerId));
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	       // List exprs1 = FastList.newInstance();
	        List exprs1 = new ArrayList();
	        exprs1.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS,customerId));
	        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        List orderBy1 = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList1 = null;
	        try {
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }

	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList1){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        
	        //System.out.println("######## pending #### Reciveded #######"+totalPendingCredit +"===="+totalReceivedCredit);
	        if(totalPendingCredit!=BigDecimal.ZERO && totalReceivedCredit!=BigDecimal.ZERO){
	        	dueCredits=totalPendingCredit.subtract(totalReceivedCredit);
	        }
		  return dueCredits.toString();
	  }
	  
	  
	  public static String dueCredits(GenericDelegator delegator,String orderId){
		    BigDecimal dueCredits=BigDecimal.ZERO;
	       // List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        //List exprs1 = FastList.newInstance();
	        List exprs1 = new ArrayList();
	        exprs1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
	        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        List orderBy1 = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList1 = null;
	        try {
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }

	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList1){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        
	        //System.out.println("######## pending #### Reciveded #######"+totalPendingCredit +"===="+totalReceivedCredit);
	       // if(totalPendingCredit!=BigDecimal.ZERO && totalReceivedCredit!=BigDecimal.ZERO){
	        if(totalPendingCredit!=BigDecimal.ZERO){
	        	dueCredits=totalPendingCredit.subtract(totalReceivedCredit);
	        }
		  return dueCredits.toString();
	  }
	  
	  public static String dueTotalForCustomer(GenericDelegator delegator,String orderId){
		    BigDecimal totaldueCredits=BigDecimal.ZERO;
			  
	        //List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));

	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        //List exprs1 = FastList.newInstance();
	        List exprs1 = new ArrayList();
	        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        exprs1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId));

	        List orderBy1 = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList1 = null;
	        try {
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }

	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList1){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        
	        //System.out.println("######## pending #### Reciveded #######"+totalPendingCredit +"===="+totalReceivedCredit);
	        if(totalPendingCredit!=BigDecimal.ZERO && totalReceivedCredit!=BigDecimal.ZERO){
	        	totaldueCredits=totalPendingCredit.subtract(totalReceivedCredit);
	        }
		    return totaldueCredits.toString();
	  }
	  
	  
	  public static String dueTotal(GenericDelegator delegator){
		    BigDecimal totaldueCredits=BigDecimal.ZERO;
			  
	        //List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        //List exprs1 = FastList.newInstance();
	        List exprs1 = new ArrayList();
	        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        List orderBy1 = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList1 = null;
	        try {
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }

	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList1){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        
	        //System.out.println("######## pending #### Reciveded #######"+totalPendingCredit +"===="+totalReceivedCredit);
	        if(totalPendingCredit!=BigDecimal.ZERO && totalReceivedCredit!=BigDecimal.ZERO){
	        	totaldueCredits=totalPendingCredit.subtract(totalReceivedCredit);
	        }
		    return totaldueCredits.toString();
	  }
	  
	  
	  
	  
	  public static String showReceivedCredits(HttpServletRequest request,HttpServletResponse response){
		  
		   GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
	        Set<String> orderIdSet=new HashSet<String>();
		    //List exprs = FastList.newInstance();
		    List exprs = new ArrayList();
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	        List <GenericValue>transList = null;
	        try {
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
	        request.setAttribute("receivedCredits", transList);
	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        request.setAttribute("totalReceivedCredit", totalReceivedCredit.toString());
	        
	        for(GenericValue trs:transList){
	        	orderIdSet.add(trs.getString("orderId"));
	        }
             Map<String,List<GenericValue>> recivedCreditmap=new HashMap<String,List<GenericValue>>();
            for(String orderId : orderIdSet){
	        	//List exprs1 = FastList.newInstance();
	        	List exprs1 = new ArrayList();
		        exprs1.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
		        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
		        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
		        List orderBy1 = UtilMisc.toList("-createdStamp");
		        List <GenericValue>transList1 = null;
	        try {
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	            if(transList1!=null && transList1.size()>0){
	        	  recivedCreditmap.put(orderId,transList1);
	           }
	          } catch (GenericEntityException e) {
	            Debug.logError(e, module);
	        }
           }
	        request.setAttribute("orderIdSet",UtilMisc.toList(orderIdSet));
	        request.setAttribute("recivedCreditmap",recivedCreditmap);
	        return "success";
	  }
	  
	  public static Map<String,String> differenceBetweenTwoDays(String cutomerId,List<GenericValue> credit,Map<String,String> days) throws Exception{
		    Timestamp startdate=credit.get(0).getTimestamp("orderDate");
		    Timestamp enddate=UtilDateTime.nowTimestamp();
		    int day = 0;
		    if(startdate!=null && enddate!=null){
		    	
		    	Calendar cal1 = Calendar.getInstance();
		    	cal1.setTime(credit.get(0).getTimestamp("orderDate"));
		    	
		    	Calendar cal2 = Calendar.getInstance();
		    	cal2.setTime(UtilDateTime.nowTimestamp());
		    	
		    	day=(int)((cal2.getTimeInMillis() - cal1.getTimeInMillis())/(60*60*24*1000));
		    	
		    	/*Date start=(Date)UtilDateTime.toCalendar(startdate).getTime();   
		        Date end=(Date)(Date)UtilDateTime.toCalendar(enddate).getTime();
		        int days1 =(int) start.getTime()/(60*60*24*1000);//find the number of days since the epoch.
		        int days2 =(int)end.getTime()/(60*60*24*1000);
		        //System.out.println("#### Satrts ##########"+start+"=="+end);
		        day=(days1-days2);
		        //System.out.println("#### DAYS ##########"+(days2-days1)+"=="+cutomerId);*/
		        if(day>=0){
		        	days.put(cutomerId, String.valueOf(day));
		        }else{
		        	days.put(cutomerId, String.valueOf("0"));
		        }
		    } 
		  return days;
	  }
	  
	  
}

