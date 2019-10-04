/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.product.product;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastList;
//import javolution.util.FastMap;
import javolution.util.FastSet;
import java.util.ArrayList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.inventory.InventoryServices;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * Product Information Related Events
 */
public class ProductEvents {

    public static final String module = ProductEvents.class.getName();
    public static final String resource = "ProductErrorUiLabels";

    /**
     * Updates/adds keywords for all products
     *
     * @param request HTTPRequest object for the current request
     * @param response HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String updateAllKeywords(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String updateMode = "CREATE";
        String errMsg=null;

        String doAll = request.getParameter("doAll");

        // check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            Map<String, String> messageMap = UtilMisc.toMap("updateMode", updateMode);
            errMsg = UtilProperties.getMessage(resource,"productevents.not_sufficient_permissions", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        EntityCondition condition = null;
        if (!"Y".equals(doAll)) {
            List<EntityCondition> condList =new ArrayList<EntityCondition>(); //FastList.newInstance();
            condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("autoCreateKeywords", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("autoCreateKeywords", EntityOperator.NOT_EQUAL, "N")));
            if ("true".equals(UtilProperties.getPropertyValue("prodsearch", "index.ignore.variants"))) {
                condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("isVariant", EntityOperator.NOT_EQUAL, "Y")));
            }
            if ("true".equals(UtilProperties.getPropertyValue("prodsearch", "index.ignore.discontinued.sales"))) {
                condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp)));
            }
            condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
        } else {
            condition = EntityCondition.makeCondition(EntityCondition.makeCondition("autoCreateKeywords", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("autoCreateKeywords", EntityOperator.NOT_EQUAL, "N"));
        }


        EntityListIterator entityListIterator = null;
        int numProds = 0;
        int errProds = 0;

        boolean beganTx = false;
        try {
            // begin the transaction
            beganTx = TransactionUtil.begin(7200);
            try {
                if (Debug.infoOn()) {
                    long count = delegator.findCountByCondition("Product", condition, null, null);
                    Debug.logInfo("========== Found " + count + " products to index ==========", module);
                }
                entityListIterator = delegator.find("Product", condition, null, null, null, null);
            } catch (GenericEntityException gee) {
                Debug.logWarning(gee, gee.getMessage(), module);
                Map<String, String> messageMap = UtilMisc.toMap("gee", gee.toString());
                errMsg = UtilProperties.getMessage(resource,"productevents.error_getting_product_list", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                throw gee;
            }

            GenericValue product;
            while ((product = entityListIterator.next()) != null) {
                try {
                    KeywordIndex.indexKeywords(product, "Y".equals(doAll));
                } catch (GenericEntityException e) {
                    //errMsg = UtilProperties.getMessage(resource,"productevents.could_not_create_keywords_write", UtilHttp.getLocale(request));
                    //request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    Debug.logWarning("[ProductEvents.updateAllKeywords] Could not create product-keyword (write error); message: " + e.getMessage(), module);
                    errProds++;
                }
                numProds++;
                if (numProds % 500 == 0) {
                    Debug.logInfo("Keywords indexed for " + numProds + " so far", module);
                }
            }
        } catch (GenericEntityException e) {
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            return "error";
        } finally {
            if (entityListIterator != null) {
                try {
                    entityListIterator.close();
                } catch (GenericEntityException gee) {
                    Debug.logError(gee, "Error closing EntityListIterator when indexing product keywords.", module);
                }
            }

            // commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }

        if (errProds == 0) {
            Map<String, String> messageMap = UtilMisc.toMap("numProds", Integer.toString(numProds));
            errMsg = UtilProperties.getMessage(resource,"productevents.keyword_creation_complete_for_products", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
            return "success";
        } else {
            Map<String, String> messageMap = UtilMisc.toMap("numProds", Integer.toString(numProds));
            messageMap.put("errProds", Integer.toString(errProds));
            errMsg = UtilProperties.getMessage(resource,"productevents.keyword_creation_complete_for_products_with_errors", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
    }

    /**
     * Updates ProductAssoc information according to UPDATE_MODE parameter
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String updateProductAssoc(HttpServletRequest request, HttpServletResponse response) {
        String errMsg = "";
        List<Object> errMsgList =new ArrayList<Object>(); //FastList.newInstance();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");

        String updateMode = request.getParameter("UPDATE_MODE");

        if (updateMode == null || updateMode.length() <= 0) {
            errMsg = UtilProperties.getMessage(resource,"productevents.updatemode_not_specified", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[ProductEvents.updateProductAssoc] Update Mode was not specified, but is required", module);
            return "error";
        }

        // check permissions before moving on...
        if (!security.hasEntityPermission("CATALOG", "_" + updateMode, request.getSession())) {
            Map<String, String> messageMap = UtilMisc.toMap("updateMode", updateMode);
            errMsg = UtilProperties.getMessage(resource,"productevents.not_sufficient_permissions", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String productId = request.getParameter("PRODUCT_ID");
        String productIdTo = request.getParameter("PRODUCT_ID_TO");
        String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_ID");
        String fromDateStr = request.getParameter("FROM_DATE");
        Timestamp fromDate = null;

        try {
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)) == null) {
                Map<String, String> messageMap = UtilMisc.toMap("productId", productId);
                errMsgList.add(UtilProperties.getMessage(resource,"productevents.product_with_id_not_found", messageMap, UtilHttp.getLocale(request)));
            }
            if (delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productIdTo)) == null) {
                Map<String, String> messageMap = UtilMisc.toMap("productIdTo", productIdTo);
                errMsgList.add(UtilProperties.getMessage(resource,"productevents.product_To_with_id_not_found", messageMap, UtilHttp.getLocale(request)));
            }
        } catch (GenericEntityException e) {
            // if there is an exception for either, the other probably wont work
            Debug.logWarning(e, module);
        }

        if (UtilValidate.isNotEmpty(fromDateStr)) {
            try {
                fromDate = (Timestamp) ObjectType.simpleTypeConvert(fromDateStr, "Timestamp", null, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request), false);
            } catch (Exception e) {
                errMsgList.add("From Date not formatted correctly.");
            }
        }
        if (!UtilValidate.isNotEmpty(productId))
            errMsgList.add(UtilProperties.getMessage(resource,"productevents.product_ID_missing", UtilHttp.getLocale(request)));
        if (!UtilValidate.isNotEmpty(productIdTo))
            errMsgList.add(UtilProperties.getMessage(resource,"productevents.product_ID_To_missing", UtilHttp.getLocale(request)));
        if (!UtilValidate.isNotEmpty(productAssocTypeId))
            errMsgList.add(UtilProperties.getMessage(resource,"productevents.association_type_ID_missing", UtilHttp.getLocale(request)));
        // from date is only required if update mode is not CREATE
        if (!updateMode.equals("CREATE") && !UtilValidate.isNotEmpty(fromDateStr))
            errMsgList.add(UtilProperties.getMessage(resource,"productevents.from_date_missing", UtilHttp.getLocale(request)));
        if (errMsgList.size() > 0) {
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        }

        // clear some cache entries
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId));

        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productAssocTypeId", productAssocTypeId));
        delegator.clearCacheLine("ProductAssoc", UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

        GenericValue tempProductAssoc = delegator.makeValue("ProductAssoc", UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

        if (updateMode.equals("DELETE")) {
            GenericValue productAssoc = null;

            try {
                productAssoc = delegator.findOne(tempProductAssoc.getEntityName(), tempProductAssoc.getPrimaryKey(), false);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                productAssoc = null;
            }
            if (productAssoc == null) {
                errMsg = UtilProperties.getMessage(resource,"productevents.could_not_remove_product_association_exist", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            try {
                productAssoc.remove();
            } catch (GenericEntityException e) {
                errMsg = UtilProperties.getMessage(resource,"productevents.could_not_remove_product_association_write", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not remove product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
            return "success";
        }

        String thruDateStr = request.getParameter("THRU_DATE");
        String reason = request.getParameter("REASON");
        String instruction = request.getParameter("INSTRUCTION");
        String quantityStr = request.getParameter("QUANTITY");
        String sequenceNumStr = request.getParameter("SEQUENCE_NUM");
        Timestamp thruDate = null;
        BigDecimal quantity = null;
        Long sequenceNum = null;

        if (UtilValidate.isNotEmpty(thruDateStr)) {
            try {
                thruDate = (Timestamp) ObjectType.simpleTypeConvert(thruDateStr, "Timestamp", null, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request), false);
            } catch (Exception e) {
                errMsgList.add(UtilProperties.getMessage(resource,"productevents.thru_date_not_formatted_correctly", UtilHttp.getLocale(request)));
            }
        }
        if (UtilValidate.isNotEmpty(quantityStr)) {
            try {
                quantity = new BigDecimal(quantityStr);
            } catch (NumberFormatException e) {
                errMsgList.add(UtilProperties.getMessage(resource,"productevents.quantity_not_formatted_correctly", UtilHttp.getLocale(request)));
            }
        }
        if (UtilValidate.isNotEmpty(sequenceNumStr)) {
            try {
                sequenceNum = Long.valueOf(sequenceNumStr);
            } catch (Exception e) {
                errMsgList.add(UtilProperties.getMessage(resource,"productevents.sequenceNum_not_formatted_correctly", UtilHttp.getLocale(request)));
            }
        }
        if (errMsgList.size() > 0) {
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        }

        tempProductAssoc.set("thruDate", thruDate);
        tempProductAssoc.set("reason", reason);
        tempProductAssoc.set("instruction", instruction);
        tempProductAssoc.set("quantity", quantity);
        tempProductAssoc.set("sequenceNum", sequenceNum);

        if (updateMode.equals("CREATE")) {
            // if no from date specified, set to now
            if (fromDate == null) {
                fromDate = new Timestamp(new java.util.Date().getTime());
                tempProductAssoc.set("fromDate", fromDate);
                request.setAttribute("ProductAssocCreateFromDate", fromDate);
            }

            GenericValue productAssoc = null;

            try {
                productAssoc = delegator.findOne(tempProductAssoc.getEntityName(), tempProductAssoc.getPrimaryKey(), false);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage(), module);
                productAssoc = null;
            }
            if (productAssoc != null) {
                errMsg = UtilProperties.getMessage(resource,"productevents.could_not_create_product_association_exists", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            try {
                productAssoc = tempProductAssoc.create();
            } catch (GenericEntityException e) {
                errMsg = UtilProperties.getMessage(resource,"productevents.could_not_create_product_association_write", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not create product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
        } else if (updateMode.equals("UPDATE")) {
            try {
                tempProductAssoc.store();
            } catch (GenericEntityException e) {
                errMsg = UtilProperties.getMessage(resource,"productevents.could_not_update_product_association_write", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                Debug.logWarning("[ProductEvents.updateProductAssoc] Could not update product association (write error); message: " + e.getMessage(), module);
                return "error";
            }
        } else {
            Map<String, String> messageMap = UtilMisc.toMap("updateMode", updateMode);
            errMsg = UtilProperties.getMessage(resource,"productevents.specified_update_mode_not_supported", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        return "success";
    }

    
    public static String UpdateProductPriceAndInventory(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	String productId = request.getParameter("PRODUCT_ID");
    	String productIdTo = request.getParameter("PRODUCT_ID_TO");
    	String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_ID");
    	
    	GenericValue userLogin = (GenericValue)request.getSession(true).getAttribute("userLogin");
    	
    	String currencyUomId = request.getParameter("currencyUomId");
    	if(UtilValidate.isEmpty(currencyUomId))
    		currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
    	
    	UpdateProductPriceAndInventory(delegator, dispatcher, userLogin, null, productId, productIdTo, productAssocTypeId, currencyUomId);
    
    	return "success";
    }
    
    
    public static String UpdateProdPriceAndInventory(HttpServletRequest request, HttpServletResponse response){
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	String productId = request.getParameter("productId");
    	String productIdTo = request.getParameter("productIdTo");
    	String productAssocTypeId = request.getParameter("productAssocTypeId");
    	String currencyUomId = request.getParameter("currencyUomId");
    	
    	Timestamp salesDiscontinuationDate = null;
    	if(UtilValidate.isNotEmpty(request.getParameter("salesDiscontinuationDate")))
    		salesDiscontinuationDate = Timestamp.valueOf(request.getParameter("salesDiscontinuationDate"));
    	
    	
    	List cond = new ArrayList();
		cond.add(EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId));
		cond.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"PRODUCT_VARIANT"));
		
		List<GenericValue> productAssocList = null;
		try {
			productAssocList = delegator.findList("ProductAssoc", EntityCondition.makeCondition(cond,EntityOperator.AND), 
																					null, UtilMisc.toList("sequenceNum"), null, false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		GenericValue userLogin = (GenericValue)request.getSession(true).getAttribute("userLogin");
    	
    	if(UtilValidate.isEmpty(currencyUomId))
    		currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
    	
		
		if(UtilValidate.isNotEmpty(productAssocList))
		for(GenericValue productAssoc : productAssocList)
		{
			productAssoc.put("thruDate", salesDiscontinuationDate);
			try {
				productAssoc.store();
				
				UpdateProductPriceAndInventory(delegator, dispatcher, userLogin, salesDiscontinuationDate, productAssoc.getString("productId"), productIdTo, 
						productAssocTypeId, currencyUomId);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
			UpdateProductPriceAndInventory(delegator, dispatcher, userLogin, salesDiscontinuationDate, productId, productIdTo, 
					productAssocTypeId, currencyUomId);
    	return "success";
    }
    
    public static String UpdateProductPriceAndInventory(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin,
			Timestamp salesDiscontinuationDate, String productId, String productIdTo, String productAssocTypeId, String currencyUomId){

		if(UtilValidate.isNotEmpty(salesDiscontinuationDate) && salesDiscontinuationDate.after(UtilDateTime.nowTimestamp()))
		{
			return "success";
		}
		
		if(UtilValidate.isEmpty(currencyUomId))
			currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
		
		
		if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(productAssocTypeId) && "PRODUCT_VARIANT".equals(productAssocTypeId) 
																							&& UtilValidate.isNotEmpty(currencyUomId))
		{
			try {
				GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				
				if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(product.getString("isVirtual")) &&
																						"Y".equalsIgnoreCase(product.getString("isVirtual")))
				{
					GenericValue productAssoc = getFirstIndexProduct(delegator, productId);
					if(UtilValidate.isNotEmpty(productAssoc))
					{
						GenericValue productAss = delegator.findOne("Product", false, UtilMisc.toMap("productId", productAssoc.getString("productIdTo")));
						if(UtilValidate.isNotEmpty(productAss))
						{
							product.put("internalName", productAss.getString("internalName"));
							product.put("productName", productAss.getString("productName"));
							product.put("description", productAss.getString("description"));
							product.put("longDescription", productAss.getString("longDescription"));
							product.put("ingredients", productAss.getString("ingredients"));
							product.put("nutritionalFacts", productAss.getString("nutritionalFacts"));
							product.put("smallImageUrl", productAss.getString("smallImageUrl"));
							product.put("mediumImageUrl", productAss.getString("mediumImageUrl"));
							product.put("largeImageUrl", productAss.getString("largeImageUrl"));
							product.put("detailImageUrl", productAss.getString("detailImageUrl"));
							product.put("originalImageUrl", productAss.getString("originalImageUrl"));
							
							product.store();
						}
						productId = productAssoc.getString("productIdTo");
					}
				}
				Map<String, Object> priceResult = dispatcher.runSync("updateProductPriceInProduct", UtilMisc.toMap("productId",productId,
																									"currencyUomId","INR","userLogin",userLogin));
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InventoryServices.updateInventoryInProduct(delegator, dispatcher, productId, "WebStoreWarehouse");
		}
		
		return "success";
	}
    /**
     * @Ajaya
     * getting first index product of a variant product
     * @return the index
     */
    public static GenericValue getFirstIndexProduct(Delegator delegator, String productId) {
    	int count = 0;
    	try{
	    	List fromDateCond = new ArrayList();
	    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
	    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
	    	
	    	List thruDateCond = new ArrayList();
	    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
	    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
	    	
	    	List cond = new ArrayList();
			cond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			
			cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
			cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
			cond.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"PRODUCT_VARIANT"));
			
			
			List<GenericValue> productAssocList = delegator.findList("ProductAssoc", EntityCondition.makeCondition(cond,EntityOperator.AND), 
																								null, UtilMisc.toList("sequenceNum"), null, false);
			String prodId = null;
			if(UtilValidate.isNotEmpty(productAssocList)){
				return ((GenericValue)productAssocList.get(0));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /** Event to clear the last viewed categories */
    public static String clearLastViewedCategories(HttpServletRequest request, HttpServletResponse response) {
        // just store a new empty list in the session
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute("lastViewedCategories", new ArrayList());
        }
        return "success";
    }

    /** Event to clear the last vieweed products */
    public static String clearLastViewedProducts(HttpServletRequest request, HttpServletResponse response) {
        // just store a new empty list in the session
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute("lastViewedProducts", new ArrayList());
        }
        return "success";
    }

    /** Event to clear the last viewed history (products/categories/searchs) */
    public static String clearAllLastViewed(HttpServletRequest request, HttpServletResponse response) {
        ProductEvents.clearLastViewedCategories(request, response);
        ProductEvents.clearLastViewedProducts(request, response);
        ProductSearchSession.clearSearchOptionsHistoryList(request, response);
        return "success";
    }

    public static String updateProductQuickAdminShipping(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String variantProductId = request.getParameter("productId0");

        boolean applyToAll = (request.getParameter("applyToAll") != null);

        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                // check for variantProductId - this will mean that we have multiple ship info to update
                if (variantProductId == null) {
                    // only single product to update
                    String productId = request.getParameter("productId");
                    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                    product.set("lastModifiedDate", nowTimestamp);
                    product.setString("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
                    try {
                        product.set("productHeight", parseBigDecimalForEntity(request.getParameter("productHeight")));
                        product.set("productWidth", parseBigDecimalForEntity(request.getParameter("productWidth")));
                        product.set("productDepth", parseBigDecimalForEntity(request.getParameter("productDepth")));
                        product.set("weight", parseBigDecimalForEntity(request.getParameter("weight")));

                        // default unit settings for shipping parameters
                        product.set("heightUomId", "LEN_in");
                        product.set("widthUomId", "LEN_in");
                        product.set("depthUomId", "LEN_in");
                        product.set("weightUomId", "WT_oz");

                        BigDecimal floz = parseBigDecimalForEntity(request.getParameter("~floz"));
                        BigDecimal ml = parseBigDecimalForEntity(request.getParameter("~ml"));
                        BigDecimal ntwt = parseBigDecimalForEntity(request.getParameter("~ntwt"));
                        BigDecimal grams = parseBigDecimalForEntity(request.getParameter("~grams"));

                        List<GenericValue> currentProductFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "STANDARD_FEATURE")), true);
                        setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "VLIQ_ozUS", "AMOUNT", floz);
                        setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "VLIQ_ml", "AMOUNT", ml);
                        setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "WT_g", "AMOUNT", grams);
                        setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "WT_oz", "AMOUNT", ntwt);
                        product.store();

                    } catch (NumberFormatException nfe) {
                        String errMsg = "Shipping Dimensions and Weights must be numbers.";
                        request.setAttribute("_ERROR_MESSAGE_", errMsg);
                        Debug.logError(nfe, errMsg, module);
                        return "error";
                    }
                } else {
                    // multiple products, so use a numeric suffix to get them all
                    int prodIdx = 0;
                    int attribIdx = 0;
                    String productId = variantProductId;
                    do {
                        GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                        try {
                            product.set("productHeight", parseBigDecimalForEntity(request.getParameter("productHeight" + attribIdx)));
                            product.set("productWidth", parseBigDecimalForEntity(request.getParameter("productWidth" + attribIdx)));
                            product.set("productDepth", parseBigDecimalForEntity(request.getParameter("productDepth" + attribIdx)));
                            product.set("weight", parseBigDecimalForEntity(request.getParameter("weight" + attribIdx)));
                            BigDecimal floz = parseBigDecimalForEntity(request.getParameter("~floz" + attribIdx));
                            BigDecimal ml = parseBigDecimalForEntity(request.getParameter("~ml" + attribIdx));
                            BigDecimal ntwt = parseBigDecimalForEntity(request.getParameter("~ntwt" + attribIdx));
                            BigDecimal grams = parseBigDecimalForEntity(request.getParameter("~grams" + attribIdx));

                            List<GenericValue> currentProductFeatureAndAppls = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId, "productFeatureApplTypeId", "STANDARD_FEATURE")), true);
                            setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "VLIQ_ozUS", "AMOUNT", floz);
                            setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "VLIQ_ml", "AMOUNT", ml);
                            setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "WT_g", "AMOUNT", grams);
                            setOrCreateProdFeature(delegator, productId, currentProductFeatureAndAppls, "WT_oz", "AMOUNT", ntwt);
                            product.store();
                        } catch (NumberFormatException nfe) {
                            String errMsg = "Shipping Dimensions and Weights must be numbers.";
                            request.setAttribute("_ERROR_MESSAGE_", errMsg);
                            Debug.logError(nfe, errMsg, module);
                            return "error";
                        }
                        prodIdx++;
                        if (!applyToAll) {
                            attribIdx = prodIdx;
                        }
                        productId = request.getParameter("productId" + prodIdx);
                    } while (productId != null);
                }
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                String errMsg = "Error updating quick admin shipping settings: " + e.toString();
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                TransactionUtil.rollback(beganTransaction, errMsg, e);
                return "error";
            }
        } catch (GenericTransactionException gte) {
            String errMsg = "Error updating quick admin shipping settings: " + gte.toString();
            Debug.logError(gte, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

    /**
     * find a specific feature in a given list, then update it or create it if it doesn't exist.
     * @param delegator
     * @param productId
     * @param existingFeatures
     * @param uomId
     * @param productFeatureTypeId
     * @param numberSpecified
     * @return
     * @throws GenericEntityException
     */
    private static void setOrCreateProdFeature(Delegator delegator, String productId, List<GenericValue> currentProductFeatureAndAppls,
                                          String uomId, String productFeatureTypeId, BigDecimal numberSpecified) throws GenericEntityException {

        GenericValue productFeatureType = delegator.findByPrimaryKey("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId));
        GenericValue uom = delegator.findByPrimaryKey("Uom", UtilMisc.toMap("uomId", uomId));

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        // filter list of features to the one we'll be editing
        List<GenericValue> typeUomProductFeatureAndApplList = EntityUtil.filterByAnd(currentProductFeatureAndAppls, UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId, "uomId", uomId));

        // go through each; need to remove? do it now
        boolean foundOneEqual = false;
        for (GenericValue typeUomProductFeatureAndAppl: typeUomProductFeatureAndApplList) {
            if ((numberSpecified != null) && (numberSpecified.compareTo(typeUomProductFeatureAndAppl.getBigDecimal("numberSpecified")) == 0)) {
                foundOneEqual = true;
            } else {
                // remove the PFA...
                GenericValue productFeatureAppl = delegator.makeValidValue("ProductFeatureAppl", typeUomProductFeatureAndAppl);
                productFeatureAppl.remove();
            }
        }

        // NOTE: if numberSpecified is null then foundOneEqual will always be false, so need to check both
        if (numberSpecified != null && !foundOneEqual) {
            String productFeatureId = null;
            List<GenericValue> existingProductFeatureList = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId, "numberSpecified", numberSpecified, "uomId", uomId));
            if (existingProductFeatureList.size() > 0) {
                GenericValue existingProductFeature = existingProductFeatureList.get(0);
                productFeatureId = existingProductFeature.getString("productFeatureId");
            } else {
                // doesn't exist, so create it
                productFeatureId = delegator.getNextSeqId("ProductFeature");
                GenericValue prodFeature = delegator.makeValue("ProductFeature", UtilMisc.toMap("productFeatureId", productFeatureId, "productFeatureTypeId", productFeatureTypeId));
                if (uomId != null) {
                    prodFeature.set("uomId", uomId);
                }
                prodFeature.set("numberSpecified", numberSpecified);
                prodFeature.set("description", numberSpecified.toString() + (uom == null ? "" : (" " + uom.getString("description"))));

                // if there is a productFeatureCategory with the same id as the productFeatureType, use that category.
                // otherwise, use a default category from the configuration
                if (delegator.findByPrimaryKey("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId", productFeatureTypeId)) == null) {
                    GenericValue productFeatureCategory = delegator.makeValue("ProductFeatureCategory");
                    productFeatureCategory.set("productFeatureCategoryId", productFeatureTypeId);
                    productFeatureCategory.set("description", productFeatureType.get("description"));
                    productFeatureCategory.create();
                }
                prodFeature.set("productFeatureCategoryId", productFeatureTypeId);
                prodFeature.create();
            }

            delegator.create("ProductFeatureAppl", UtilMisc.toMap("productId", productId, "productFeatureId", productFeatureId,
                    "productFeatureApplTypeId", "STANDARD_FEATURE", "fromDate", nowTimestamp));
        }
    }

    public static String updateProductQuickAdminSelFeat(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        //GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productId = request.getParameter("productId");
        String variantProductId = request.getParameter("productId0");
        String useImagesProdId = request.getParameter("useImages");
        String productFeatureTypeId = request.getParameter("productFeatureTypeId");

        if (UtilValidate.isEmpty(productFeatureTypeId)) {
            String errMsg = "Error: please select a ProductFeature Type to add or update variant features.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            boolean beganTransaction = TransactionUtil.begin();
            try {
                GenericValue productFeatureType = delegator.findByPrimaryKey("ProductFeatureType", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId));
                if (productFeatureType == null) {
                    String errMsg = "Error: the ProductFeature Type specified was not valid and one is require to add or update variant features.";
                    request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    return "error";
                }

                // check for variantProductId - this will mean that we have multiple variants to update
                if (variantProductId != null) {
                    // multiple products, so use a numeric suffix to get them all
                    int attribIdx = 0;
                    GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
                    do {
                        GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", variantProductId));
                        String description = request.getParameter("description" + attribIdx);
                        // blank means null, which means delete the feature application
                        if ((description != null) && (description.trim().length() < 1)) {
                            description = null;
                        }

                        Set<String> variantDescRemoveToRemoveOnVirtual = FastSet.newInstance();
                        checkUpdateFeatureApplByDescription(variantProductId, variantProduct, description, productFeatureTypeId, productFeatureType, "STANDARD_FEATURE", nowTimestamp, delegator, null, variantDescRemoveToRemoveOnVirtual);
                        checkUpdateFeatureApplByDescription(productId, product, description, productFeatureTypeId, productFeatureType, "SELECTABLE_FEATURE", nowTimestamp, delegator, variantDescRemoveToRemoveOnVirtual, null);

                        // update image urls
                        if ((useImagesProdId != null) && (useImagesProdId.equals(variantProductId))) {
                            product.set("smallImageUrl", variantProduct.getString("smallImageUrl"));
                            product.set("mediumImageUrl", variantProduct.getString("mediumImageUrl"));
                            product.set("largeImageUrl", null);
                            product.set("detailImageUrl", null);
                            product.store();
                        }
                        attribIdx++;
                        variantProductId = request.getParameter("productId" + attribIdx);
                    } while (variantProductId != null);
                }

                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                String errMsg = "Error updating quick admin selectable feature settings: " + e.toString();
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                TransactionUtil.rollback(beganTransaction, errMsg, e);
                return "error";
            }
        } catch (GenericTransactionException gte) {
            String errMsg = "Error updating quick admin selectable feature settings: " + gte.toString();
            Debug.logError(gte, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

    protected static void checkUpdateFeatureApplByDescription(String productId, GenericValue product, String description,
            String productFeatureTypeId, GenericValue productFeatureType, String productFeatureApplTypeId,
            Timestamp nowTimestamp, Delegator delegator, Set<String> descriptionsToRemove, Set<String> descriptionsRemoved) throws GenericEntityException {
        if (productFeatureType == null) {
            return;
        }

        GenericValue productFeatureAndAppl = null;

        Set<String> descriptionsForThisType = FastSet.newInstance();
        List<GenericValue> productFeatureAndApplList = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId,
                "productFeatureApplTypeId", productFeatureApplTypeId, "productFeatureTypeId", productFeatureTypeId)), true);
        if (productFeatureAndApplList.size() > 0) {
            Iterator<GenericValue> productFeatureAndApplIter = productFeatureAndApplList.iterator();
            while (productFeatureAndApplIter.hasNext()) {
                productFeatureAndAppl = productFeatureAndApplIter.next();
                GenericValue productFeatureAppl = delegator.makeValidValue("ProductFeatureAppl", productFeatureAndAppl);

                // remove productFeatureAppl IFF: productFeatureAppl != null && (description is empty/null || description is different than existing)
                if (productFeatureAppl != null && (description == null || !description.equals(productFeatureAndAppl.getString("description")))) {
                    // if descriptionsToRemove is not null, only remove if description is in that set
                    if (descriptionsToRemove == null || (descriptionsToRemove != null && descriptionsToRemove.contains(productFeatureAndAppl.getString("description")))) {
                        // okay, almost there: before removing it if this is a virtual product check to make SURE this feature's description doesn't exist on any of the variants; wouldn't want to remove something we should have kept around...
                        if ("Y".equals(product.getString("isVirtual"))) {
                            boolean foundFeatureOnVariant = false;
                            // get/check all the variants
                            List<GenericValue> variantAssocs = product.getRelatedByAnd("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));
                            variantAssocs = EntityUtil.filterByDate(variantAssocs);
                            List<GenericValue> variants = EntityUtil.getRelated("AssocProduct", variantAssocs);
                            Iterator<GenericValue> variantIter = variants.iterator();
                            while (!foundFeatureOnVariant && variantIter.hasNext()) {
                                GenericValue variant = variantIter.next();
                                // get the selectable features for the variant
                                List<GenericValue> variantProductFeatureAndAppls = variant.getRelated("ProductFeatureAndAppl",
                                        UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId,
                                                "productFeatureApplTypeId", "STANDARD_FEATURE", "description", description), null);
                                if (variantProductFeatureAndAppls.size() > 0) {
                                    foundFeatureOnVariant = true;
                                }
                            }

                            if (foundFeatureOnVariant) {
                                // don't remove this one!
                                continue;
                            }
                        }

                        if (descriptionsRemoved != null) {
                            descriptionsRemoved.add(productFeatureAndAppl.getString("description"));
                        }
                        productFeatureAppl.remove();
                        continue;
                    }
                }

                // we got here, is still a valid description associated with this product
                descriptionsForThisType.add(productFeatureAndAppl.getString("description"));
            }
        }

        if (description != null && (productFeatureAndAppl == null || (productFeatureAndAppl != null && !descriptionsForThisType.contains(description)))) {
            // need to add an appl, and possibly the feature

            // see if a feature exists with the type and description specified (if doesn't exist will create later)
            String productFeatureId = null;
            List<GenericValue> existingProductFeatureList = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId, "description", description));
            if (existingProductFeatureList.size() > 0) {
                GenericValue existingProductFeature = existingProductFeatureList.get(0);
                productFeatureId = existingProductFeature.getString("productFeatureId");
            } else {
                // doesn't exist, so create it
                productFeatureId = delegator.getNextSeqId("ProductFeature");
                GenericValue newProductFeature = delegator.makeValue("ProductFeature",
                        UtilMisc.toMap("productFeatureId", productFeatureId,
                                "productFeatureTypeId", productFeatureTypeId,
                                "description", description));

                // if there is a productFeatureCategory with the same id as the productFeatureType, use that category.
                // otherwise, create a category for the feature type
                if (delegator.findByPrimaryKey("ProductFeatureCategory", UtilMisc.toMap("productFeatureCategoryId", productFeatureTypeId)) == null) {
                    GenericValue productFeatureCategory = delegator.makeValue("ProductFeatureCategory");
                    productFeatureCategory.set("productFeatureCategoryId", productFeatureTypeId);
                    productFeatureCategory.set("description", productFeatureType.get("description"));
                    productFeatureCategory.create();
                }
                newProductFeature.set("productFeatureCategoryId", productFeatureTypeId);
                newProductFeature.create();
            }

            // check to see if the productFeatureId is already attached to the virtual or variant, if not attach them...
            List<GenericValue> specificProductFeatureApplList = EntityUtil.filterByDate(delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", productId,
                    "productFeatureApplTypeId", productFeatureApplTypeId, "productFeatureId", productFeatureId)), true);

            if (specificProductFeatureApplList.size() == 0) {
                delegator.create("ProductFeatureAppl",
                        UtilMisc.toMap("productId", productId,
                                "productFeatureId", productFeatureId,
                                "productFeatureApplTypeId", productFeatureApplTypeId,
                                "fromDate", nowTimestamp));
            }
        }
    }

    public static String removeFeatureApplsByFeatureTypeId(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        //GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productId = request.getParameter("productId");
        String productFeatureTypeId = request.getParameter("productFeatureTypeId");

        try {
            GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
            // get all the variants
            List<GenericValue> variantAssocs = product.getRelatedByAnd("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"));
            variantAssocs = EntityUtil.filterByDate(variantAssocs);
            List<GenericValue> variants = EntityUtil.getRelated("AssocProduct", variantAssocs);
            for (GenericValue variant: variants) {
                // get the selectable features for the variant
                List<GenericValue> productFeatureAndAppls = variant.getRelated("ProductFeatureAndAppl", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId, "productFeatureApplTypeId", "STANDARD_FEATURE"), null);
                for (GenericValue productFeatureAndAppl: productFeatureAndAppls) {
                    GenericPK productFeatureApplPK = delegator.makePK("ProductFeatureAppl");
                    productFeatureApplPK.setPKFields(productFeatureAndAppl);
                    delegator.removeByPrimaryKey(productFeatureApplPK);
                }
            }
            List<GenericValue> productFeatureAndAppls = product.getRelated("ProductFeatureAndAppl", UtilMisc.toMap("productFeatureTypeId", productFeatureTypeId, "productFeatureApplTypeId", "SELECTABLE_FEATURE"), null);
            for (GenericValue productFeatureAndAppl: productFeatureAndAppls) {
                GenericPK productFeatureApplPK = delegator.makePK("ProductFeatureAppl");
                productFeatureApplPK.setPKFields(productFeatureAndAppl);
                delegator.removeByPrimaryKey(productFeatureApplPK);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error creating new virtual product from variant products: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

    public static String removeProductFeatureAppl(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        //Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        //GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String productId = request.getParameter("productId");
        String productFeatureId = request.getParameter("productFeatureId");

        if (UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(productFeatureId)) {
            String errMsg = "Must specify both a productId [was:" + productId + "] and a productFeatureId [was:" + productFeatureId + "] to remove the feature from the product.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        try {
            delegator.removeByAnd("ProductFeatureAppl", UtilMisc.toMap("productFeatureId", productFeatureId, "productId", productId));
        } catch (GenericEntityException e) {
            String errMsg = "Error removing product feature: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

    public static String addProductToCategories(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productId = request.getParameter("productId");
        String fromDate = request.getParameter("fromDate");
        if ((fromDate == null) || (fromDate.trim().length() == 0)) {
            fromDate = UtilDateTime.nowTimestamp().toString();
        }
        String[] categoryIds = request.getParameterValues("categoryId");
        if (categoryIds != null) {
            for (String categoryId: categoryIds) {
                try {
                    List<GenericValue> catMembs = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap(
                            "productCategoryId", categoryId,
                            "productId", productId));
                    catMembs = EntityUtil.filterByDate(catMembs);
                    if (catMembs.size() == 0) {
                        delegator.create("ProductCategoryMember",
                                UtilMisc.toMap("productCategoryId", categoryId, "productId", productId, "fromDate", fromDate));
                    }
                } catch (GenericEntityException e) {
                    String errMsg = "Error adding to category: " + e.toString();
                    request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    return "error";
                }

            }
        }
        return "success";
    }

    public static String updateProductCategoryMember(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productId = request.getParameter("productId");
        String productCategoryId = request.getParameter("productCategoryId");
        String thruDate = request.getParameter("thruDate");
        if ((thruDate == null) || (thruDate.trim().length() == 0)) {
            thruDate = UtilDateTime.nowTimestamp().toString();
        }
        try {
            List<GenericValue> prodCatMembs = delegator.findByAnd("ProductCategoryMember",
                    UtilMisc.toMap("productCategoryId", productCategoryId, "productId", productId));
            prodCatMembs = EntityUtil.filterByDate(prodCatMembs);
            if (prodCatMembs.size() > 0) {
                // there is one to modify
                GenericValue prodCatMemb = prodCatMembs.get(0);
                prodCatMemb.setString("thruDate", thruDate);
                prodCatMemb.store();
            }

        } catch (GenericEntityException e) {
            String errMsg = "Error adding to category: " + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        return "success";
    }

    public static String addProductFeatures(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productId = request.getParameter("productId");
        String productFeatureApplTypeId = request.getParameter("productFeatureApplTypeId");
        String fromDate = request.getParameter("fromDate");
        if ((fromDate == null) || (fromDate.trim().length() == 0)) {
            fromDate = UtilDateTime.nowTimestamp().toString();
        }
        String[] productFeatureIdArray = request.getParameterValues("productFeatureId");
        if (productFeatureIdArray != null && productFeatureIdArray.length > 0) {
            try {
                for (String productFeatureId: productFeatureIdArray) {
                    if (!productFeatureId.equals("~~any~~")) {
                        List<GenericValue> featureAppls = delegator.findByAnd("ProductFeatureAppl",
                                UtilMisc.toMap("productId", productId,
                                        "productFeatureId", productFeatureId,
                                        "productFeatureApplTypeId", productFeatureApplTypeId));
                        if (featureAppls.size() == 0) {
                            // no existing application for this
                            delegator.create("ProductFeatureAppl",
                                    UtilMisc.toMap("productId", productId,
                                        "productFeatureId", productFeatureId,
                                        "productFeatureApplTypeId", productFeatureApplTypeId,
                                        "fromDate", fromDate));
                        }
                    }
                }
            } catch (GenericEntityException e) {
                String errMsg = "Error adding feature: " + e.toString();
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }
        return "success";
    }

    /** Simple event to set the users initial locale and currency Uom based on website product store */
    public static String setDefaultStoreSettings(HttpServletRequest request, HttpServletResponse response) {
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore != null) {
            String currencyStr = null;
            String localeStr = null;

            HttpSession session = request.getSession();
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin != null) {
                // user login currency
                currencyStr = userLogin.getString("lastCurrencyUom");
                // user login locale
                localeStr = userLogin.getString("lastLocale");
            }

            // if currency is not set, the store's default currency is used
            if (currencyStr == null && productStore.get("defaultCurrencyUomId") != null) {
                currencyStr = productStore.getString("defaultCurrencyUomId");
            }

            // if locale is not set, the store's default locale is used
            if (localeStr == null && productStore.get("defaultLocaleString") != null) {
                localeStr = productStore.getString("defaultLocaleString");
            }

            UtilHttp.setCurrencyUom(session, currencyStr);
            UtilHttp.setLocale(request, localeStr);

        }
        return "success";
    }

    /**
     * If ProductStore.requireCustomerRole == Y then the loggedin user must be associated with the store in the customer role.
     * This event method is called from the ProductEvents.storeCheckLogin and ProductEvents.storeLogin
     *
     * @param request
     * @param response
     * @return String with response, maybe "success" or "error" if logged in user is not associated with the ProductStore in the CUSTOMER role.
     */
    public static String checkStoreCustomerRole(HttpServletRequest request, HttpServletResponse response) {
    
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore != null && userLogin != null) {
            if ("Y".equals(productStore.getString("requireCustomerRole"))) {
                List<GenericValue> productStoreRoleList = null;
                try {
                    productStoreRoleList = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.get("productStoreId"),
                            "partyId", userLogin.get("partyId"), "roleTypeId", "CUSTOMER"));
                    productStoreRoleList = EntityUtil.filterByDate(productStoreRoleList, true);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Database error finding CUSTOMER ProductStoreRole records, required by the ProductStore with ID [" + productStore.getString("productStoreId") + "]", module);
                }
                if (UtilValidate.isEmpty(productStoreRoleList)) {
                    // uh-oh, this user isn't associated...
                    String errorMsg = "The " + productStore.getString("storeName") + " [" + productStore.getString("productStoreId") + "] ProductStore requires that customers be associated with it, and the logged in user is NOT associated with it in the CUSTOMER role; userLoginId=[" + userLogin.getString("userLoginId") + "], partyId=[" + userLogin.getString("partyId") + "]";
                    Debug.logWarning(errorMsg, module);
                    request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                    session.removeAttribute("userLogin");
                    session.removeAttribute("autoUserLogin");
                    return "error";
                }
            }
        }
        return "success";
    }

    public static String tellAFriend(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String emailType = "PRDS_TELL_FRIEND";
        String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#TellFriend";

        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore == null) {
            String errMsg = "Could not send tell a friend email, no ProductStore found";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        String productStoreId = productStore.getString("productStoreId");

        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
                    UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
        } catch (GenericEntityException e) {
            String errMsg = "Unable to get product store email setting for tell-a-friend: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (productStoreEmail == null) {
            String errMsg = "Could not find tell a friend [" + emailType + "] email settings for the store [" + productStoreId + "]";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        paramMap.put("locale", UtilHttp.getLocale(request));
        paramMap.put("userLogin", session.getAttribute("userLogin"));
       
        String senderName = null;
        if(UtilValidate.isEmpty(paramMap.get("userName"))){
        GenericValue gv = (GenericValue) session.getAttribute("userLogin");
        GenericValue  userNameFrom = null;
        if(UtilValidate.isNotEmpty(gv)){
				try {
					userNameFrom = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", gv.getString("partyId")));
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      
        	}
      
        if(UtilValidate.isNotEmpty(userNameFrom)){
        	senderName = userNameFrom.getString("firstName")+ " " +  userNameFrom.getString("lastName");
        	}
        paramMap.put("senderName", senderName);
        }else{
        	 paramMap.put("senderName", paramMap.get("userName"));
        }
        
        
        Map<String, Object> context = new HashMap<String, Object>(); //FastMap.newInstance();
        context.put("bodyScreenUri", bodyScreenLocation);
        context.put("bodyParameters", paramMap);
        context.put("sendTo", paramMap.get("sendTo"));
        context.put("contentType", productStoreEmail.get("contentType"));
        context.put("sendFrom", productStoreEmail.get("fromAddress"));
        context.put("sendCc", productStoreEmail.get("ccAddress"));
        context.put("sendBcc", productStoreEmail.get("bccAddress"));
        context.put("subject", productStoreEmail.getString("subject"));
       
        
//        context.put("loyaltyRefId", paramMap.get("loyaltyRefId"));  
        

        try {
            dispatcher.runAsync("sendMailFromScreen", context);
        } catch (GenericServiceException e) {
            String errMsg = "Problem sending mail: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
      
        return "success";
    }
    
    public static String inviteAFriend(HttpServletRequest request, HttpServletResponse response) {
    	
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String emailType = "PRDS_TELL_FRIEND";
        String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#TellFriend";
        
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore == null) {
        	try {
				response.getWriter().print("Could not send tell a friend email, no ProductStore found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			Debug.logError("Could not send tell a friend email, no ProductStore found :", module);
            return "error";
        }
        String productStoreId = productStore.getString("productStoreId");
        String loyaltyRefId = delegator.getNextSeqId("LoyaltyReference");
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
                    UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
        } catch (GenericEntityException e) {
        	try {
				response.getWriter().print("Unable to get product store email setting for tell-a-friend: ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
			Debug.logError(e, "Unable to get product store email setting for tell-a-friend: "+ e.toString(), module);
            return "error";
        }
        if (productStoreEmail == null) {
        	String errMsg = "Could not find tell a friend [" + emailType + "] email settings for the store [" + productStoreId + "]";
        	try {
				response.getWriter().print(errMsg);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
			Debug.logError(errMsg, module);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        
        paramMap.put("loyaltyRefId",loyaltyRefId);
        paramMap.put("pageUrl",paramMap.get("pageUrl")+"&uniqrefId="+loyaltyRefId);
        paramMap.put("locale", UtilHttp.getLocale(request));
        paramMap.put("userLogin", session.getAttribute("userLogin"));
        paramMap.put("loyaltyRefId",  paramMap.get("loyaltyRefId"));
        paramMap.put("sendFrom",  paramMap.get("sendFrom"));
        paramMap.put("toName",  paramMap.get("userName"));
       
        GenericValue gv = (GenericValue) session.getAttribute("userLogin");
        GenericValue  userNameFrom = null;
        if(UtilValidate.isNotEmpty(gv)){
				try {
					userNameFrom = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", gv.getString("partyId")));
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      
        	}
        String senderName = null;
        if(UtilValidate.isNotEmpty(userNameFrom)){
        	senderName = userNameFrom.getString("firstName")+ " " +  userNameFrom.getString("lastName");
        	}
        paramMap.put("senderName", senderName);
         
        

        Map<String, Object> context = new HashMap<String, Object>(); //FastMap.newInstance();
        Map<String, Object> loyalcontext = new HashMap<String, Object>(); //FastMap.newInstance();
        context.put("bodyScreenUri", bodyScreenLocation);
        context.put("bodyParameters", paramMap);
        context.put("sendTo", paramMap.get("sendTo"));
        context.put("contentType", productStoreEmail.get("contentType"));
        context.put("sendFrom", paramMap.get("sendFrom"));
        
        context.put("sendCc", productStoreEmail.get("ccAddress"));
        context.put("sendBcc", productStoreEmail.get("bccAddress"));
        context.put("subject", productStoreEmail.getString("subject"));
//        context.put("loyaltyRefId", paramMap.get("loyaltyRefId"));   
        
        

        try {
            dispatcher.runAsync("sendMailFromScreen", context);
          
            
            GenericValue createLoyaltyReference = delegator.makeValue("LoyaltyReference");
      		 
            createLoyaltyReference.set("loyaltyRefId", paramMap.get("loyaltyRefId"));
            createLoyaltyReference.set("refByPartyId", paramMap.get("sendFrom"));
            createLoyaltyReference.set("refToPartyName", paramMap.get("userName"));
            createLoyaltyReference.set("refToPartyId", paramMap.get("sendTo"));
            createLoyaltyReference.set("refToPartyNumber", paramMap.get("mobileNo"));
            createLoyaltyReference.set("msgByParty", paramMap.get("message"));
            delegator.create(createLoyaltyReference);
            GenericValue createInviteReference = delegator.makeValue("InviteFriendReference");
      		 
            createInviteReference.set("loyaltyRefId", paramMap.get("loyaltyRefId"));
            createInviteReference.set("refByPartyId", paramMap.get("sendFrom"));
            createInviteReference.set("refToPartyName", paramMap.get("userName"));
            createInviteReference.set("refToPartyId", paramMap.get("sendTo"));
            createInviteReference.set("refToPartyNumber", paramMap.get("mobileNo"));
          
            delegator.create(createInviteReference);
            loyalcontext.put("sendByParty", paramMap.get("sendFrom"));
            loyalcontext.put("sendToParty", paramMap.get("sendTo"));
            loyalcontext.put("sendToMobile", paramMap.get("mobileNo"));
            loyalcontext.put("sendToName", paramMap.get("userName"));
            //done by radha for ref id
            loyalcontext.put("loyaltyRefId", paramMap.get("loyaltyRefId"));
            dispatcher.runAsync("sendReferLoyaltySmsService", loyalcontext);
  		  
        } catch (GenericServiceException e) {
        	 String errMsg = "Problem sending mail: " + e.toString();
        	try {
				response.getWriter().print(errMsg);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
			Debug.logError(e, errMsg, module);
            return "error";
        }
        catch (GenericEntityException ex) {
            String errMsg = "Problem sending mail: " + ex.toString();
            Debug.logError(ex, errMsg, module);
        	try {
				response.getWriter().print(errMsg);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
			}
            return "error";
        }
        String msg = "Successfully invited a friend . An email send to your friend .";
    	try {
			response.getWriter().print(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
		}
        return "success";
    }

    @Deprecated
    public static Double parseDoubleForEntity(String doubleString) throws NumberFormatException {
        if (doubleString == null) {
            return null;
        }
        doubleString = doubleString.trim();
        doubleString = doubleString.replaceAll(",", "");
        if (doubleString.length() < 1) {
            return null;
        }
        return Double.valueOf(doubleString);
    }

    
 

    @Deprecated
   
    
    public static List<GenericValue> getProductCompareList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object compareListObj = session.getAttribute("productCompareList");
        List<GenericValue> compareList = null;
        if (compareListObj == null) {
            compareList =new ArrayList<GenericValue>(); //FastList.newInstance();
        } else if (!(compareListObj instanceof List<?>)) {
            Debug.logWarning("Session attribute productCompareList contains something other than the expected product list, overwriting.", module);
            compareList =new ArrayList<GenericValue>();// FastList.newInstance();
        } else {
            compareList = UtilGenerics.cast(compareListObj);
        }
        return compareList;
    }

    public static String addProductToComparisonList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        String productId = request.getParameter("productId");
        GenericValue product = null;
        if (UtilValidate.isNotEmpty(productId)) {
            try {
                product = ProductWorker.findProduct(delegator, productId);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        if (product == null) {
            String errMsg = UtilProperties.getMessage(resource, "productevents.product_with_id_not_found", UtilMisc.toMap("productId", productId), UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        List<GenericValue> compareList = getProductCompareList(request);
        
        //TODO  here code to make restriction compare list Max Size
        String storeId = (String)session.getAttribute("productStoreId");
        //System.out.println("\n\n the product store id= "+storeId);
       try
       {
    	   GenericValue pstore = delegator.findByPrimaryKey("ProductStore" ,UtilMisc.toMap("productStoreId",storeId));
    	   if(UtilValidate.isNotEmpty(pstore))
    	   {
	    	   Long longsize = (Long) pstore.get("productCompareMaxSize");
	    	   if(UtilValidate.isNotEmpty(longsize))
	    	   {
		    	   int size = longsize.intValue();
		    	   if(compareList.size() == size)
		    	   {
		    		   session.setAttribute("productCompareList", compareList);
		    		   request.setAttribute("_ERROR_MESSAGE_", "You can compare maximum "+size+" products");
		    		   return "success";
		    	   }
	    	   }
    	   }
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
       }
       
       
       // End Here
        
        boolean alreadyInList = false;
        for (GenericValue compProduct : compareList) {
            if (product.getString("productId").equals(compProduct.getString("productId"))) {
                alreadyInList = true;
                break;
            }
        }
        if (!alreadyInList) {
            compareList.add(product);
        }
        session.setAttribute("productCompareList", compareList);
        String productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", request);
        String eventMsg = UtilProperties.getMessage("ProductUiLabels", "ProductAddToCompareListSuccess", UtilMisc.toMap("name", productName), UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", eventMsg);
        return "success";
    }

    public static String removeProductFromComparisonList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        String productId = request.getParameter("productId");
        GenericValue product = null;
        if (UtilValidate.isNotEmpty(productId)) {
            try {
                product = ProductWorker.findProduct(delegator, productId);
            } catch (GenericEntityException e) {
                productId =  null;
                Debug.logError(e, module);
            }
        }

        if (product == null) {
            String errMsg = UtilProperties.getMessage(resource, "productevents.product_with_id_not_found", UtilMisc.toMap("productId", productId), UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        List<GenericValue> compareList = getProductCompareList(request);
        Iterator<GenericValue> it = compareList.iterator();
        while (it.hasNext()) {
            GenericValue compProduct = it.next();
            if (product.getString("productId").equals(compProduct.getString("productId"))) {
                it.remove();
                break;
            }
        }
        session.setAttribute("productCompareList", compareList);
        String productName = ProductContentWrapper.getProductContentAsText(product, "PRODUCT_NAME", request);
        String eventMsg = UtilProperties.getMessage("ProductUiLabels", "ProductRemoveFromCompareListSuccess", UtilMisc.toMap("name", productName), UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", eventMsg);
        return "success";
    }

    public static String clearProductComparisonList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute("productCompareList", new ArrayList());
        String eventMsg = UtilProperties.getMessage("ProductUiLabels", "ProductClearCompareListSuccess", UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", eventMsg);
        return "success";
    }

    /**
     * Return nulls for empty strings, as the entity engine can deal with nulls. This will provide blanks
     * in fields where BigDecimal display. Blank meaning null, vs. 0 which means 0
     * @param bigDecimalString
     * @return a BigDecimal for the parsed value
     */
    public static BigDecimal parseBigDecimalForEntity(String bigDecimalString) throws NumberFormatException {
        if (bigDecimalString == null) {
            return null;
        }
        bigDecimalString = bigDecimalString.trim();
        bigDecimalString = bigDecimalString.replaceAll(",", "");
        if (bigDecimalString.length() < 1) {
            return null;
        }
        return new BigDecimal(bigDecimalString);
    }
    
    public static String productList(HttpServletRequest request , HttpServletResponse response)throws Exception
    {
    	
    	HttpSession session= request.getSession();
    	String categoryId=request.getParameter("categoryId");
    	String proList = "<div id='product'><select name='SEARCH_STRING' id='PRODUCT_NAME'>";
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	List productName = new ArrayList();
    	boolean flag = false;
    	String productAssocList = "";
    	String selectedProductFirst = "";
    	Set fieldToSelect = new HashSet();
		fieldToSelect.add("productId");
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List prodNameId = new ArrayList();
		
    	try{
    		PrintWriter out = response.getWriter();
    		productName = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId), fieldToSelect, null, findOptions, false);
    		Iterator itr = productName.iterator();
    		if(productName != null && productName.size() == 0){
    			proList = proList + "<option value='_NA_' >No Product exist</option></select>";
            	try {
        			
        			out.print(proList);
        		} catch (Exception e) {
        			Debug.logError(e.getMessage(),module);
        		}
                return "success";    		
        	}
    		while(itr.hasNext()){
        		GenericValue proName = (GenericValue)itr.next();
        		GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", proName.get("productId")));
        		if(!product.isEmpty()){
        			prodNameId.add(proName.get("productId"));
        		productAssocList = productAssocList + "<option value='"+product.get("productName")+"' >"+product.get("productName")+"</option>";
        	}}
    		if(flag){
    			proList = proList + selectedProductFirst + productAssocList;
        	}else{
        		proList = proList + "<option value='' >Select Product</option>";
        		proList = proList + productAssocList;
        	}
        	
        	if(proList.length() <= 105)
        		proList = proList + "<option value='_NA_'>No Product exist</option>";
        		
        	
        	proList = proList +"</select></div>";
        	out.print(proList);
    	}catch (Exception e) {
    		Debug.logError(e.getMessage(),module);
    		//e.printStackTrace();
			return "error";
		}
    	session.setAttribute("prodNameId", prodNameId);
    	return "success";
    }
    public static String productBrandsList(HttpServletRequest request , HttpServletResponse response)throws Exception
    {
    	HttpSession session= request.getSession();
    	List prodNameId = new ArrayList();
    	prodNameId=(List) session.getAttribute("prodNameId");
    	String productAssocList = "";
    	String selectedProductFirst = "";
    	boolean flag = false;
    	EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
    	List productBrandsName = new ArrayList();
    	Set fieldToSelect = new HashSet();
		fieldToSelect.add("brandName");
    	String proList = "<div id='bndselect'><select name='BRAND_NAME' id='BRAND_NAME'>";
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	
    	try{
    		PrintWriter out = response.getWriter();
    		productBrandsName = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, prodNameId), fieldToSelect, null, findOptions, false);
    		Iterator itr = productBrandsName.iterator();
    		if(productBrandsName != null && productBrandsName.size() == 0){
    			proList = proList + "<option value='_NA_' >No Brand exist</option></select>";
            	try {
        			
        			out.print(proList);
        		} catch (Exception e) {
        			Debug.logError(e.getMessage(),module);
        		}
                return "success";    		
        	}
    		while(itr.hasNext()){
        		GenericValue proName = (GenericValue)itr.next();
        		if(proName.get("brandName") != "" && proName.get("brandName") != null){
        		productAssocList = productAssocList + "<option value='"+proName.get("brandName")+"' >"+proName.get("brandName")+"</option>";
        		}}if(flag){
    			proList = proList + selectedProductFirst + productAssocList;
        	}else{
        		proList = proList + "<option value='' >Select Brand</option>";
        		proList = proList + productAssocList;
        	}
        	
        	if(proList.length() <= 105)
        		proList = proList + "<option value='_NA_'>No Brand exist</option>";
        		
        	
        	proList = proList +"</select></div>";
        	out.print(proList);
    	}catch (Exception e) {
    		Debug.logError(e.getMessage(),module);
    		//e.printStackTrace();
			return "error";
		}
    	
    	return "success";
    }
    
    
    public static String getProductNames(HttpServletRequest request , HttpServletResponse response)throws Exception
    {
    	String productId = request.getParameter("productId");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	
    	String internaleNames= null;
        try {
        	PrintWriter out = response.getWriter();
			GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
			if(variantProduct!=null){
				internaleNames = variantProduct.getString("internalName");
				out.print(internaleNames);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "success";
    }
    public static String getVariantPrice(HttpServletRequest request , HttpServletResponse response)throws Exception
    {
    	//System.out.println("going inside\n\n\n\n\n\n\n\n\n\n\n");
    	 LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	String productId = request.getParameter("productId");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    	
    	String internaleNames= null;
        try {
        	PrintWriter out = response.getWriter();
			GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
			if(variantProduct!=null){
				Map variantPriceMap = dispatcher.runSync("calculateProductPrice", UtilMisc.toMap("product", variantProduct));
				  request.setAttribute("result", variantPriceMap);
				  //System.out.println("the response\n\n\n\n\n\n"+request.getAttribute("result"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "success";
    }
    public static List<GenericValue> productCategoryMembers(Delegator delegator, String productCategoryId){
    	
        	List fromDateCond = new ArrayList();
        	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
        	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
        	
        	List thruDateCond = new ArrayList();
        	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
        	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
        	
        	List cond = new ArrayList();
    		cond.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,productCategoryId));
    		cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
    		cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
    		
    		List<GenericValue> productCategoryMembers = null;
    	   	
             try {
            	 productCategoryMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(cond,EntityOperator.AND),
            			 																				null, UtilMisc.toList("sequenceNum"), null, true);
             }catch (Exception e) {
    			// TODO: handle exception
    		 }
             return productCategoryMembers;
    }
    public static String emailARecipe(HttpServletRequest request, HttpServletResponse response) {
         HttpSession session = request.getSession();
        String recipeManagementId = request.getParameter("recipeId");  
        String sendTo = request.getParameter("sendTo");
         
         String note = request.getParameter("note");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String emailType = "RECIPE_BY_EMAIL";
         String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#RecipeMail";
          GenericValue productStore = ProductStoreWorker.getProductStore(request);
        if (productStore == null) {
            String errMsg = "Could not send Recipe Mail, no ProductStore found";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        String productStoreId = productStore.getString("productStoreId");

    
        GenericValue productStoreEmail = null;
        try { 
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
                    UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
        } catch (GenericEntityException e) {
            String errMsg = "Unable to get product store email setting for Recipe Email: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (productStoreEmail == null) {
            String errMsg = "Could not find recipe  [" + emailType + "] email settings for the store [" + productStoreId + "]";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
          bodyScreenLocation = defaultScreenLocation;
        }

 
        GenericValue recipe = null; 
        List recipeIngredientList = null;
		try {
			  recipe = delegator.findOne("RecipeManagement",
					UtilMisc.toMap("recipeManagementId",recipeManagementId), false);
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		if(UtilValidate.isNotEmpty(recipe)){
			try {
				recipeIngredientList = delegator.findList("RecipeIngredients",
				EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId), null, null, null, false);
			} catch (GenericEntityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        paramMap.put("locale", UtilHttp.getLocale(request));
        paramMap.put("userLogin", session.getAttribute("userLogin"));
        
        
        GenericValue gv = (GenericValue) session.getAttribute("userLogin");
        GenericValue  userNameFrom = null;
        if(UtilValidate.isNotEmpty(gv)){
				try {
					userNameFrom = delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId", gv.getString("partyId")));
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
      
        	}
        String senderName = null;
        if(UtilValidate.isNotEmpty(userNameFrom)){
        	senderName = userNameFrom.getString("firstName")+ " " +  userNameFrom.getString("lastName");
        	}
        paramMap.put("senderName", senderName);
        paramMap.put("recipeValue", recipe);
        paramMap.put("recipeName", recipe.getString("recipeName"));
        paramMap.put("recipeIngredientList", recipeIngredientList);
        paramMap.put("note",note);
         
       

        Map<String, Object> context = new HashMap<String, Object>(); //FastMap.newInstance();
        Map<String, Object> loyalcontext = new HashMap<String, Object>(); //FastMap.newInstance();
        context.put("bodyScreenUri", defaultScreenLocation);
        context.put("bodyParameters", paramMap);
        context.put("sendTo", sendTo);
        context.put("sendFrom", productStoreEmail.getString("fromAddress"));  
        
        context.put("contentType", productStoreEmail.get("contentType"));
         
        context.put("sendCc", productStoreEmail.get("ccAddress"));
        context.put("sendBcc", productStoreEmail.get("bccAddress"));
        context.put("subject", productStoreEmail.getString("subject"));
        

        try {
            dispatcher.runAsync("sendMailFromScreen", context);
         } catch (GenericServiceException e) {
            String errMsg = "Problem sending mail: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
             return "error";
        }
        request.setAttribute("processed" ,"Y");
         return "success";
    }
    
    public static String createSearchJSONString(HttpServletRequest request, HttpServletResponse response) {

    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String querys = (String)request.getParameter("q");
        /*if(UtilValidate.isNotEmpty(productStore))
			try {
				response.getWriter().print(productStore.getString("searchJsonString"));
				return "success";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        
		String removeSelectedFlag = request.getParameter("sport");
		
		List cond = new ArrayList();
		
		List salesDiscontinuationDateCond = new ArrayList();
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		EntityCondition condition = EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR);

		List<GenericValue> productList = null;
		List<GenericValue> productList2 = null;
		
        try {
			PrintWriter out = response.getWriter();
			EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
	    	productList = delegator.findList("Product", condition,null, UtilMisc.toList("internalName"), findOptions, false);

	    	String resultData = "<script> var carMake = [";
	    	int count = 1;
	    	List internalNames = new ArrayList();
	    	for(GenericValue product : productList){
		    	String productName = product.getString("internalName");
		    	if(UtilValidate.isNotEmpty(productName))
		    	{
			    	String brandName = product.getString("brandName");
			    	productName=brandName+" "+productName;
			    	
			    	String productId = product.getString("productId");
			    	  //out.print(productName+",");
					String productCatgId = CategoryWorker.getCategoryFromProduct(delegator, product.getString("productId"));
					if(UtilValidate.isEmpty(productCatgId))
					  {
						 GenericValue product1 = ProductWorker.getParentProduct(product.getString("productId"), delegator);
						 if(UtilValidate.isNotEmpty(product1))
							 productCatgId = CategoryWorker.getCategoryFromProduct(delegator, product1.getString("productId"));
						 if(UtilValidate.isEmpty(productCatgId))
							 productCatgId = "";
					  }
			    	 String prodCateUrl = CategoryWorker.getTrailAsString(delegator, productCatgId);
			    	 if(!internalNames.contains(productName))
			    	 {
			    		 resultData = resultData+"{\"make\": \""+productName.trim()+
				    			  "\",\"id\": '"+prodCateUrl+"c_c_c_p_"+productId+"'},";
			    		 
			    		 
			    	 }
			    	 internalNames.add(productName);
			    	  count++;
		    	}
		      }
	    	String lastCharacter = resultData.substring(resultData.length()- 1); 
	    	if(",".equals(lastCharacter))
	    		resultData = resultData.substring(0, resultData.length()- 1); 
	    	
	    	  resultData = resultData+"];" +
		    	  		"function addlabel(row) " +
		    	  		"{var make = row.make;" +
		    	  		"if(make == '"+request.getParameter("sport")+"') make = 'View All'; " +
		    	  		"row.label = make;" +
		    	  		/////////////////////
		    	  		"if(make!=null)	{"+
		    	  		"res =  make.split(\" \");"+
		    	  		"var sub = null;"+
				    	 " if(res.length >=1){"+
				    	  "for(var i=1;i<res.length;i++){"+
				    	  "if(sub==null){"+
				    	  "sub  = res[i] + \" \" ;"+
				    	  "}else{"+
				    	  "sub   = sub +  res[i] + \" \";	    	  } } } } ;"+
		    	  		///////////////////////////
		    	  		"row.value = sub;" +  
		    	  		//comments:- if customer says wants brand name,then assign row.desc = make
		    	  		"row.desc = sub;" +  
		    	  		"}carMake.forEach(addlabel);" +
		    	  		"</script>";
	    	  
	    	  productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId")));
	    	  //System.out.println("   resultData     "+resultData);
	    	  productStore.put("searchJsonString", resultData);
	          productStore.store();
	          } 
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	          }finally{
				}
        //Determine where to send the browser
            return "success";
    }
    
    public static String getProductIdFromBarCode(HttpServletRequest request, HttpServletResponse response){
    	String barcodeId = request.getParameter("barcodeId");
    	String orderId = request.getParameter("orderId");
    	String productId = null;
    	PrintWriter out = null;
    	try {
			out = response.getWriter();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		GenericValue userLogin = (GenericValue)request.getSession(true).getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			out.print("sessionExpired");
    		return "success";
		}
		
    	if(UtilValidate.isEmpty(barcodeId)){
    		if(UtilValidate.isNotEmpty(out))out.print("blankBarCode");
    		return "success";
    	}
    	barcodeId = barcodeId.trim();
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	if(UtilValidate.isEmpty(productId))
    	{
	    	List<GenericValue> productBarcodeList = null;
	    	try {
	    		productBarcodeList = delegator.findList("ProductBarcode", 
	    							EntityCondition.makeCondition("barcode",EntityOperator.EQUALS,barcodeId), UtilMisc.toSet("productId"), null, null, true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isNotEmpty(productBarcodeList))
	    	{
				GenericValue productBarcode = EntityUtil.getFirst(productBarcodeList);
				if(UtilValidate.isNotEmpty(productBarcode)){
					productId = productBarcode.getString("productId");
			    	//return "success";
				}else
				{
					out.print("productNotFound");
		    		return "success";
				}
	    	}
    	}
    	
    	if(UtilValidate.isEmpty(productId))
    	{
	    	List<GenericValue> articleNoList = null;
	    	try {
	    		articleNoList = delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue",barcodeId,"goodIdentificationTypeId","ARTICLE_ID_NO"));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isNotEmpty(articleNoList))
	    	{
				GenericValue articleNo = EntityUtil.getFirst(articleNoList);
				if(UtilValidate.isNotEmpty(articleNo)){
					productId = articleNo.getString("productId");
			    	//return "success";
				}else
				{
					out.print("productNotFound");
		    		return "success";
				}
	    	}
    	}
    	if(UtilValidate.isEmpty(productId))
    	{
	    	GenericValue product = null;
	    	try {
	    		product = delegator.findOne("Product",true,UtilMisc.toMap("productId",barcodeId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(UtilValidate.isEmpty(product)){
				if(UtilValidate.isNotEmpty(out))
				{
					out.print("productNotFound");
					return "success";
				}
	    	}else
	    		productId = barcodeId;
    	}
    	if(UtilValidate.isEmpty(productId))
    	{
    		productId = barcodeId;
    	}
    	if(UtilValidate.isNotEmpty(orderId))
    	{
	    	List condition = new ArrayList();
	    	condition.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId));
	    	condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
	    	
	    	
	    	List<EntityExpr> exprs = UtilMisc.toList(
	                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
	                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
	    	
	    	condition.add(EntityCondition.makeCondition(exprs,EntityOperator.OR));
	    	
	    	List<GenericValue> orderItems = null;
	    	try {
	    		orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(condition,EntityOperator.AND), 
	    																					UtilMisc.toSet("orderItemSeqId"), null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String ordItemSeqId_prdId = "";
			if(UtilValidate.isNotEmpty(orderItems))
			for(GenericValue orderItem : orderItems){
				ordItemSeqId_prdId = ordItemSeqId_prdId+orderItem.getString("orderItemSeqId")+"__"+productId+"++";
			}
			productId = ordItemSeqId_prdId;
    	}
    	out.print(productId);
    	return "success";
    }
}
