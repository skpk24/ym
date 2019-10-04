package org.ofbiz.order.budget;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.ofbiz.entity.Delegator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.recipes.RecipeEvents;

public class BudgetManagement {

	public static final String module = BudgetManagement.class.getName();
    public static final String resource = "ProductErrorUiLabels";
	
    /**
     * Updates/adds keywords for all products
     *
     * @param request HTTPRequest object for the current request
     * @param response HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    
    
    public static String planBudget(HttpServletRequest request, HttpServletResponse response) {
    	
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		GenericValue productStore = ProductStoreWorker.getProductStore(request);
		Enumeration en=request.getParameterNames();
		BigDecimal zeros =  new BigDecimal(0);
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String budgetAmount = "";
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		try {
			while(en.hasMoreElements())
	        {
	            Object objOri=en.nextElement();
	            String categoryId=(String)objOri;
	            if(!categoryId.equalsIgnoreCase("CURRENT_CATALOG_ID"))
	            {
	            budgetAmount=request.getParameter(categoryId);
	            if(UtilValidate.isEmpty(budgetAmount))
	            {
	            	budgetAmount = "0";
	            }
	            if(UtilValidate.isNotEmpty(budgetAmount) && UtilValidate.isInteger(budgetAmount)){
	            	EntityConditionList ecl = EntityCondition.makeCondition( UtilMisc.toList(
	        				EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp())),
	        				EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault())),
	        				EntityCondition.makeCondition("partyId", userLogin.getString("partyId")),
	        				EntityCondition.makeCondition("categoryId", EntityOperator.EQUALS, categoryId)
	        		        ), EntityOperator.AND);
	            	
	            	List<GenericValue> budgetPlansList = delegator.findList("BudgetPlans", ecl, null, null, null, false);
	            	if(UtilValidate.isNotEmpty(budgetPlansList)){
	                    for(GenericValue bplansList : budgetPlansList){
	                    	bplansList.put("categoryId", categoryId);
	                    	bplansList.put("budgetAmount", new java.math.BigDecimal(budgetAmount.trim().toString()));
	                    	bplansList.store();
	                    }
	            	}else{
			            GenericValue budgetPlans =  delegator.makeValue("BudgetPlans");
			            String budgetSeqId = delegator.getNextSeqId("BudgetPlans");
			            
			            budgetPlans.put("budgetSeqId", budgetSeqId);
			            budgetPlans.put("partyId", userLogin.getString("partyId"));
			            budgetPlans.put("userLoginId", userLogin.getString("userLoginId"));
			            budgetPlans.put("categoryId", categoryId);
			            budgetPlans.put("budgetAmount", new java.math.BigDecimal(budgetAmount.trim().toString()));
			            budgetPlans.put("createdDate", UtilDateTime.nowTimestamp());
			            delegator.create(budgetPlans);
			            }}else{
			            	EntityConditionList ecl = EntityCondition.makeCondition( UtilMisc.toList(
			        				EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp())),
			        				EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault())),
			        				EntityCondition.makeCondition("partyId", userLogin.getString("partyId")),
			        				EntityCondition.makeCondition("categoryId", EntityOperator.EQUALS, categoryId)
			        		        ), EntityOperator.AND);
			            	
			            	List<GenericValue> budgetPlansList = delegator.findList("BudgetPlans", ecl, null, null, null, false);
			            	if(UtilValidate.isNotEmpty(budgetPlansList)){
			                    for(GenericValue bplansList : budgetPlansList){
			                    	bplansList.put("categoryId", categoryId);
			                    	bplansList.put("budgetAmount", new java.math.BigDecimal(budgetAmount.trim().toString()));
			                    	bplansList.store();
			                    }
			            	}else{
					            GenericValue budgetPlans =  delegator.makeValue("BudgetPlans");
					            String budgetSeqId = delegator.getNextSeqId("BudgetPlans");
					            
					            budgetPlans.put("budgetSeqId", budgetSeqId);
					            budgetPlans.put("partyId", userLogin.getString("partyId"));
					            budgetPlans.put("userLoginId", userLogin.getString("userLoginId"));
					            budgetPlans.put("categoryId", categoryId);
					            budgetPlans.put("budgetAmount", zeros);
					            budgetPlans.put("createdDate", UtilDateTime.nowTimestamp());
					            delegator.create(budgetPlans);
					            }
			            }}
	        } 
			return "success";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_"," Failed To create Budget Plans ");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
    }
    
    
    /** Service for creating Budget Plan Status List */
    public static Map generateBudgetPlanStatus(DispatchContext ctx, Map context) {
    Delegator delegator = ctx.getDelegator();
    LocalDispatcher dispatcher = ctx.getDispatcher();
    String orderId = (String) context.get("orderId");
    String productStoreId = (String) context.get("productStoreId");
    if(UtilValidate.isEmpty(productStoreId))
    {
    	productStoreId="9000";
    }
    String partyId = null;
    String userLoginId = null;
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    if(UtilValidate.isNotEmpty(userLogin))
    {
    	partyId = userLogin.getString("partyId");
    	userLoginId =  userLogin.getString("userLoginId");
    }
   
    BigDecimal budAmt = new BigDecimal(0);
	BigDecimal totBudAmtUsed = new BigDecimal(0);
	BigDecimal leftBudAmt = new BigDecimal(0);
	Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
	Timestamp startDay=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
	Map pIds =  new HashMap();
	Map exceedBudgetData =  new HashMap();
	try {
		
		EntityConditionList ecl = EntityCondition.makeCondition( UtilMisc.toList(
				EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
				EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
		        ), EntityOperator.AND);
		List orderBy = UtilMisc.toList("-createdDate");
		List <GenericValue>budgetPlansList = delegator.findList("BudgetPlans", ecl, UtilMisc.toSet("categoryId","budgetAmount"), orderBy, null, false);
		EntityConditionList ecl1 = EntityCondition.makeCondition( UtilMisc.toList(
				EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
				EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"),
				EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLoginId)
                ), EntityOperator.AND);
		List orderBy1 = UtilMisc.toList("-createdStamp");
		List <GenericValue>budgetPlansItemsList = delegator.findList("OrderItem", ecl1, UtilMisc.toSet("parentProductCategoryId","unitPrice","quantity"), orderBy1, null, false);
		
		List <GenericValue>currentOrderBudget = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), UtilMisc.toSet("parentProductCategoryId","unitPrice","quantity"), null, null, false);
		if(UtilValidate.isNotEmpty(budgetPlansItemsList)){
		Iterator <GenericValue> budPlanLst = budgetPlansItemsList.iterator();
		while (budPlanLst.hasNext())
		{
			GenericValue budgetItm = budPlanLst.next();
			if(budgetItm.get("parentProductCategoryId") !=  null)
			{
				BigDecimal amt = new BigDecimal(0);
				if(pIds.containsKey(budgetItm.get("parentProductCategoryId")))
				{
					budAmt = (BigDecimal) pIds.get(budgetItm.get("parentProductCategoryId"));
					amt = budAmt.add(budgetItm.getBigDecimal("unitPrice").multiply(budgetItm.getBigDecimal("quantity")));
				}else{
					amt = budgetItm.getBigDecimal("quantity").multiply(budgetItm.getBigDecimal("unitPrice"));
					}
				pIds.put(budgetItm.get("parentProductCategoryId"), amt);
			}
		}
		}
		if(UtilValidate.isNotEmpty(currentOrderBudget)){
		Iterator <GenericValue> currentPlanLst = currentOrderBudget.iterator();
		while (currentPlanLst.hasNext())
		{
			GenericValue currentBudgetItm = currentPlanLst.next();
			if(currentBudgetItm.get("parentProductCategoryId") !=  null)
			{
				BigDecimal amt = new BigDecimal(0);
				if(pIds.containsKey(currentBudgetItm.get("parentProductCategoryId")))
				{
					budAmt = (BigDecimal) pIds.get(currentBudgetItm.get("parentProductCategoryId"));
					amt = budAmt.add(currentBudgetItm.getBigDecimal("unitPrice").multiply(currentBudgetItm.getBigDecimal("quantity")));
				}else{
					amt = currentBudgetItm.getBigDecimal("quantity").multiply(currentBudgetItm.getBigDecimal("unitPrice"));
					}
				pIds.put(currentBudgetItm.get("parentProductCategoryId"), amt);
			}
		}
		}
		List listpIds = new LinkedList(pIds.entrySet());
		if(UtilValidate.isNotEmpty(listpIds)){
		Iterator <GenericValue> budPlansList = budgetPlansList.iterator();
		while (budPlansList.hasNext())
		{
			GenericValue budgetItm = budPlansList.next();
			
				if(budgetItm.get("categoryId") != null)
				{
					for (Iterator its = listpIds.iterator(); its.hasNext();) {
						Map.Entry entry = (Map.Entry) its.next();
						List dataLst = new ArrayList();
						if((entry.getKey()).equals(budgetItm.get("categoryId")))
						{
							
							totBudAmtUsed = (BigDecimal) entry.getValue();
							leftBudAmt = ((BigDecimal)budgetItm.get("budgetAmount")).subtract(totBudAmtUsed);
							if(leftBudAmt.intValue() < 0)
							{
								dataLst.add(budgetItm.get("budgetAmount"));
								dataLst.add(totBudAmtUsed);
								dataLst.add(leftBudAmt);
								GenericValue category = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", budgetItm.get("categoryId")));
								exceedBudgetData.put((String)category.get("categoryName"), dataLst);
							}
						}
					}
					
				}
		}
		}
	} catch (GeneralException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(UtilValidate.isNotEmpty(exceedBudgetData))
	{
		// get the ProductStore email settings
		GenericValue productStoreEmail = null;
		String emailType = "BUDGET_DETAIL_LIST";
		String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#budgetStatusMail";
		try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }
        String name = RecipeEvents.partyName(delegator, userLoginId);
        String bodyScreenLocation = null;
        if(UtilValidate.isNotEmpty(productStoreEmail))
        	bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }
       // Map<String, Object> bodyParameters = FastMap.newInstance();
        Map<String, Object> bodyParameters = new HashMap<String, Object>();
        
        bodyParameters.put("name", name);
        bodyParameters.put("exceedBudgetData", exceedBudgetData);
        String sendTo = null;
        if(UtilValidate.isNotEmpty(userLoginId))
    	    sendTo = userLoginId;
        
       // Map<String, Object> serviceContext = FastMap.newInstance();
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        if(UtilValidate.isNotEmpty(productStoreEmail))
        {
	        serviceContext.put("subject", productStoreEmail.getString("subject"));
	        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
	        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
	        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
	        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        }
        serviceContext.put("sendTo", sendTo);
        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);

            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
            }
        } catch (GenericServiceException e) {
        	Debug.logError(e, "Problem in sending mail in exceedBudgetData of BudgetManagement.java ", module);
        }
        
	}
    return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> generateBudgetPlan(DispatchContext ctx, Map context) {
    	
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        
        String orderId = (String) context.get("orderId");
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            String errMsg = "ERROR: Could not find order for orderId : "+orderId +"  " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        if(UtilValidate.isEmpty(orderHeader))
            return ServiceUtil.returnError("ERROR: Could not find order for orderId : "+orderId);
        
        String createdBy = orderHeader.getString("createdBy");
        if(UtilValidate.isEmpty(createdBy))
        	return ServiceUtil.returnError("ERROR: Could not find createdBy for orderId : "+orderId);
        
        GenericValue userLogin = null;
        try {
        	userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", createdBy));
        } catch (GenericEntityException e) {
            String errMsg = "ERROR: Could not find user login for userLoginId : "+createdBy +"  " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        
        if(UtilValidate.isEmpty(userLogin))
            return ServiceUtil.returnError("ERROR: Could not find user login for userLoginId : "+createdBy);
        
        String partyId = userLogin.getString("partyId");
        if(UtilValidate.isEmpty(partyId))
        	return ServiceUtil.returnError("ERROR: Could not find party for userLoginId : "+createdBy);
        
        
        Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
    	Timestamp startDay=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
    	try {
    		EntityConditionList ecl = EntityCondition.makeCondition( UtilMisc.toList(
    				EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
    				EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
    				EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, createdBy),
    				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
    		        ), EntityOperator.AND);
    		
    		List<GenericValue> budgetPlansList = delegator.findList("BudgetPlans", ecl, null, null, null, false);
    		if(UtilValidate.isEmpty(budgetPlansList))
    			return successResult;
    		
    		
    		EntityConditionList ecl1 = EntityCondition.makeCondition(UtilMisc.toList(
    	      		EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
    	      		EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
    	      		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED")),
    	      	EntityOperator.AND);
    	      	List orderBy1 = UtilMisc.toList("-createdStamp");
    	      	List <GenericValue>budgetPlansItemsList = delegator.findList("OrderItem", ecl1, UtilMisc.toSet("productCategoryId","unitPrice","quantity"), orderBy1, null, false);
    	    	       
    		
    	}catch (Exception e) {
			// TODO: handle exception
		}
        
    	                       	
    	
    	
        return successResult;
    }
	
}
