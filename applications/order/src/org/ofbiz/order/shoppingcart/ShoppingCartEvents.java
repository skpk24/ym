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
package org.ofbiz.order.shoppingcart;

import java.io.File;
import java.io.IOException;
 import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastList;
//import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
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
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
 import org.ofbiz.entity.util.EntityUtil;
 import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
 import org.ofbiz.product.product.ProductSearchSession;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.product.ProductSearchSession.ProductSearchOptions;
import org.ofbiz.product.store.ProductStoreSurveyWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.RequestHandler;

 
/**
 * Shopping cart events.
 */
public class ShoppingCartEvents {

    public static String module = ShoppingCartEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";

    public static final MathContext generalRounding = new MathContext(10);

    public static String addProductPromoCode(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        String productPromoCodeId = request.getParameter("productPromoCodeId");
        HttpSession session = request.getSession();
        String atc = (String) session.getAttribute("trackingCodeId");
        
        System.out.println("\n\n atc == "+atc+"\n\n");
        
        if(UtilValidate.isNotEmpty(productPromoCodeId) && productPromoCodeId.equals("FLT250") && (UtilValidate.isEmpty(atc) || !atc.equalsIgnoreCase("MYDALA"))){
        	request.setAttribute("_ERROR_MESSAGE_", "Promo code is not applicable");
            return "error";
        }
        
        if (UtilValidate.isNotEmpty(productPromoCodeId)) {
            String checkResult = cart.addProductPromoCode(productPromoCodeId, dispatcher);
            if (UtilValidate.isNotEmpty(checkResult)) {
                request.setAttribute("_ERROR_MESSAGE_", checkResult);
                return "error";
            }
        }
        return "success";
    }

    public static String addItemGroup(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        String groupName = (String) parameters.get("groupName");
        String parentGroupNumber = (String) parameters.get("parentGroupNumber");
        String groupNumber = cart.addItemGroup(groupName, parentGroupNumber);
        request.setAttribute("itemGroupNumber", groupNumber);
        return "success";
    }
    public static String slotSelection(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, GenericEntityException {
    	
    	List listOption=new ArrayList();  
      	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
   			PrintWriter out=response.getWriter();
   			Map<String,Double> maxDelivery = new HashMap<String,Double>();
   			Map<String,String> blockedDays  = new HashMap<String,String>();

   			
   				HttpSession session=request.getSession();
   		 		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
      			String currentmMechId=request.getParameter("mechId");
  			boolean orderPresent = false;
   			
   			
  			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  			Date d = (Date) sdf.parse(request.getParameter("deliveryDate"));
  			Timestamp deliveryDate1 = new Timestamp(d.getTime());
  			Timestamp delivery=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
   			List orderType= new ArrayList();
  			Map listCount=new HashMap();
  			
  			
  			
  			List<GenericValue> orderslotType= CheckOutEvents.getAllSlots(delegator);
  			if(!UtilValidate.isEmpty(orderslotType)){
  				for(int i=0;i<orderslotType.size();i++){
  					GenericValue gv = orderslotType.get(i);
  					orderType.add(i,gv.get("slotType"));
   					maxDelivery.put((String) gv.get("slotType"),gv.getDouble("maxDelivery"));
   					blockedDays.put((String) gv.get("slotType"),gv.getString("blockDays"));
 
  				}
  			}
  			for(int t=0;t<orderType.size();t++)
  			listCount.put(orderType.get(t),0);
  				 
  			
  			
 			 
  			
  			List<EntityCondition> promoConditions = new ArrayList<EntityCondition>();
		    promoConditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.EQUALS,deliveryDate1));
 			promoConditions.add(EntityCondition.makeCondition("orderId",EntityOperator.NOT_EQUAL,null));
 			promoConditions.add(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"));
  			List<GenericValue> ordersList1= delegator.findList("OrderSlot", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), null, UtilMisc.toList("orderId","slotStatus"), null, false);
 			
  			List<EntityCondition> CondnList=new ArrayList<EntityCondition>();
			CondnList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLogin.getString("partyId")));
			CondnList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,deliveryDate1));
 			CondnList.add(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,currentmMechId));
 			
   			////////////////////////////
 			if(UtilValidate.isNotEmpty(ordersList1)){
			for(int t=0;t<orderType.size();t++){
				List temp = EntityUtil.filterByAnd(ordersList1, UtilMisc.toMap("slotType",orderType.get(t)));
				if(!UtilValidate.isEmpty(temp)){
					listCount.put(orderType.get(t),temp.size());
					}
				}
 			}
 			
			List orderIds =   EntityUtil.getFieldListFromEntityList(ordersList1, "orderId", true); 
			List<GenericValue> orderValues = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIds),UtilMisc.toSet("orderId","statusId"),null, null, false);
			List temp1 = EntityUtil.filterByCondition(ordersList1, EntityCondition.makeCondition(CondnList,EntityOperator.AND));
			
 
 
			
			if(UtilValidate.isNotEmpty(temp1)){
				CondnList.clear();
				CondnList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(temp1, "orderId", true)));
				CondnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_APPROVED"));
 				if(UtilValidate.isNotEmpty(EntityUtil.filterByCondition(orderValues, EntityCondition.makeCondition(CondnList,EntityOperator.AND)))){
					orderPresent = true; 
				} else{
					orderPresent = false;
				}
			} else{
				orderPresent = false;
			}

			for(int i=0;i<orderslotType.size();i++){
            	 Double timing =  (Double) maxDelivery.get(orderType.get(i));
         			Integer  o =  (Integer) listCount.get(orderType.get(i));
  				if(o.intValue()<timing.intValue())
  					listOption.add(orderType.get(i));	
  			}
 
   			String buffer="";
  			if(listOption.size()>=1  && !orderPresent ){
	  			for (int i=0;i<listOption.size();i++) {
	  				String f=listOption.get(i)+"_"+deliveryDate1;
	  				String slotTxt = (String) listOption.get(i);
	  				String blockString = blockedDays.get(slotTxt);
	  				////////////////////
 	  				boolean blocked = false;
		  			  if(UtilValidate.isNotEmpty(blockString)){
						  try{
							 String[]  blockDayArray  = blockString.split(","); 
							 		if(UtilValidate.isNotEmpty(blockDayArray)){
 							 			SimpleDateFormat parseDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							 			for(int k= 0 ; k <blockDayArray.length;k++){
 							 				String bit = blockDayArray[k];
							 				String[] bitTimings  = bit.split("~~");
											Timestamp start = null;
											Timestamp end = null;
											if (UtilValidate.isNotEmpty(bitTimings[0])&& UtilValidate.isNotEmpty(bitTimings[1])) {
											Date from = (Date) parseDate.parse(bitTimings[0]);
											start = new Timestamp(from.getTime());
											Date d1 = (Date) parseDate.parse(bitTimings[1]);
											end = new Timestamp(d1.getTime());
 											if( deliveryDate1.after(start)  && deliveryDate1.before(end) ){
											blocked = true;
											break;											
											}
										}
							 		}					
							 	}	 
						  	}catch(Exception e){
						  		blocked = false;
						  }
					  }
 	  				//////////////////////
		  			if(!blocked){	  				
		  				String optionsTxt = "<input type='radio' id='dayOption' name='dayOption' value='"+f+"'>"+slotTxt;
		  				buffer=buffer.concat(" " +optionsTxt);
		  			}
	  				}
	  			buffer=buffer+"</select></td>"; 
  			}
  			else
  				buffer="No Slot is Avaiable";
  		
  		out.println(buffer);
  			
  			
  		return "success";
      	
      }
    
 public static String slotSelectionForBackOffice(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException, GenericEntityException {
	 	String currentmMechId=request.getParameter("mechId");
    	List listOption=new ArrayList();  
      	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
   		PrintWriter out=response.getWriter();
   		HttpSession session=request.getSession();
   		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
   		Map<String,Double> maxDelivery = new HashMap<String,Double>();
  		boolean orderPresent = false;
  		ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");

  	    
   			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  			Date d = (Date) sdf.parse(request.getParameter("deliveryDate"));
  			Timestamp deliveryDate1 = new Timestamp(d.getTime());
  			Timestamp delivery=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
   			List orderType= new ArrayList();
  			Map listCount=new HashMap();
  			
  			
  			
  			List<GenericValue> orderslotType= CheckOutEvents.getAllSlots(delegator);
  			if(!UtilValidate.isEmpty(orderslotType)){
  				for(int i=0;i<orderslotType.size();i++){
  					GenericValue gv = orderslotType.get(i);
  					orderType.add(i,gv.get("slotType"));
   					maxDelivery.put((String) gv.get("slotType"),gv.getDouble("maxDelivery"));
  				}
  			}
  			for(int t=0;t<orderType.size();t++)
  			listCount.put(orderType.get(t),0);
  				 
  			
  			
 			 
  			
  			List<EntityCondition> promoConditions = new ArrayList<EntityCondition>();
		    promoConditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.EQUALS,deliveryDate1));
 			promoConditions.add(EntityCondition.makeCondition("orderId",EntityOperator.NOT_EQUAL,null));
 			promoConditions.add(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"));
  			List<GenericValue> ordersList1= delegator.findList("OrderSlot", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), null, UtilMisc.toList("orderId","slotStatus"), null, false);
 			
  			List<EntityCondition> CondnList=new ArrayList<EntityCondition>();
			CondnList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,cart.getPartyId()));
			CondnList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,deliveryDate1));
 			CondnList.add(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,currentmMechId));
 			
   			////////////////////////////
 			if(UtilValidate.isNotEmpty(ordersList1)){
			for(int t=0;t<orderType.size();t++){
				List temp = EntityUtil.filterByAnd(ordersList1, UtilMisc.toMap("slotType",orderType.get(t)));
				if(!UtilValidate.isEmpty(temp)){
					listCount.put(orderType.get(t),temp.size());
					}
				}
 			}
 			
			List orderIds =   EntityUtil.getFieldListFromEntityList(ordersList1, "orderId", true); 
			List<GenericValue> orderValues = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIds),UtilMisc.toSet("orderId","statusId","slot","deliveryDate","grandTotal"),null, null, false);
			List temp1 = EntityUtil.filterByCondition(ordersList1, EntityCondition.makeCondition(CondnList,EntityOperator.AND));
			
			int cnt  = 0;
 
			String htmlPages = "";
			if(UtilValidate.isNotEmpty(temp1)){
				CondnList.clear();
				CondnList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(temp1, "orderId", true)));
				CondnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_APPROVED"));
				List<GenericValue> t =  EntityUtil.filterByCondition(orderValues, EntityCondition.makeCondition(CondnList,EntityOperator.AND));
 				if(UtilValidate.isNotEmpty(t)){
 					htmlPages = "<p> This customer already have an order pending for delivery; would you prefer this order to be merged/combined with your pending order</p>";
 					htmlPages +="<table><tr> <th style=\"text-align:left !important;\">Order Id</th><th style=\"text-align:left !important;\">Delivery Date</th><th style=\"text-align:left !important;\">Slot Type</th><th style=\"text-align:left !important;\">Grand Total</th></tr>";
 					
 					for(GenericValue gv : t){
 						htmlPages +="<tr><td> ";
 						htmlPages += "<input type='radio' id='dayOption_"+cnt+"'"  + "name='dayOption' value='"+gv.getString("slot")+"_"+deliveryDate1+"'>"+gv.getString("orderId") +"</td>"+ "<td>" + deliveryDate1 +"</td>"+"<td>"+ gv.getString("slot") +"</td>" +"<td>"+ gv.getString("grandTotal") +"</td>" ;
 						htmlPages +="</tr>";
 						cnt++;
 					}
 					htmlPages +="</table>";
 					htmlPages+="<input type='hidden' name='AjaxCount' id='AjaxCount' value='"+cnt+"'/>";
					orderPresent = true; 
				} else{
					orderPresent = false;
				}
			} else{
				orderPresent = false;
			}

 			for(int i=0;i<orderslotType.size();i++){
            	 Double timing =  (Double) maxDelivery.get(orderType.get(i));
         			Integer  o =  (Integer) listCount.get(orderType.get(i));
  				if(o.intValue()<timing.intValue())
  					listOption.add(orderType.get(i));	
  			}
 
			
  			String buffer="";
  			cnt  = 0;
  			if(listOption.size()>=1  && !orderPresent ){
	  			for (int i=0;i<listOption.size();i++) {
	  				String f=listOption.get(i)+"_"+deliveryDate1;
	  				String slotTxt = (String) listOption.get(i);
	  				String optionsTxt = "<input type='radio' id='dayOption_"+cnt+"'"  + "name='dayOption' value='"+f+"'>"+slotTxt;
	  				buffer=buffer.concat(" " +optionsTxt);	
	  				cnt++;
	  				}
	  			buffer=buffer+"</select></td>"; 
	  			buffer+="<input type='hidden' name='AjaxCount' id='AjaxCount' value='"+cnt+"'/>";
  			}
  			else
  				buffer=htmlPages;
  		
  		out.println(buffer);
  			
  			
  		return "success";
      	
      } 
    
    

    public static String addCartItemToGroup(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);
        String itemGroupNumber = (String) parameters.get("itemGroupNumber");
        String indexStr = (String) parameters.get("lineIndex");
        int index = Integer.parseInt(indexStr);
        ShoppingCartItem cartItem = cart.findCartItem(index);
        cartItem.setItemGroup(itemGroupNumber, cart);
        return "success";
    }

    /** Event to add an item to the shopping cart. */
    public static String addToCart(HttpServletRequest request, HttpServletResponse response) {
    	
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective = null;
        Map<String, Object> result = null;
        String productId = null;
        String parentProductId = null;
        String itemType = null;
        String itemDescription = null;
        String productCategoryId = null;
        String priceStr = null;
        BigDecimal price = null;
        String quantityStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String reservStartStr = null;
        String reservEndStr = null;
        Timestamp reservStart = null;
        Timestamp reservEnd = null;
        String reservLengthStr = null;
        BigDecimal reservLength = null;
        String reservPersonsStr = null;
        BigDecimal reservPersons = null;
        String accommodationMapId = null;
        String accommodationSpotId = null;
        String shipBeforeDateStr = null;
        String shipAfterDateStr = null;
        Timestamp shipBeforeDate = null;
        Timestamp shipAfterDate = null;
        
        // not used right now: Map attributes = null;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Locale locale = UtilHttp.getLocale(request);
       
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);

        String itemGroupNumber = (String) paramMap.get("itemGroupNumber");
        

        String recipientName=(String) paramMap.get("recipientName");
        String recipientMobileNum=(String) paramMap.get("recipientMobile");
        String recipientEmailId=(String) paramMap.get("recipientemail");
        String message=(String) paramMap.get("message");
       
         try {
        	 if(!"undefined".equals(recipientEmailId) && UtilValidate.isNotEmpty(recipientEmailId)) {
        		 String recipientAddressSeqId = delegator.getNextSeqId("RecipientAddress");
        	        GenericValue recipientAddress = delegator.makeValue("RecipientAddress");
        	    		recipientAddress.set("recipientAddressSeqId",recipientAddressSeqId);
        		    	recipientAddress.set("recipientName", recipientName);
        		    	recipientAddress.set("recipientMobileNum", recipientMobileNum);
        		    	recipientAddress.set("recipientEmailId",recipientEmailId);
        		    	recipientAddress.set("message",message);
        		 recipientAddress.create();
        		// request.setAttribute("_EVENT_MESSAGE_", "Recipient Address is submitted successfully!");
        	 }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        // Get shoppingList info if passed
        String shoppingListId = (String) paramMap.get("shoppingListId");
        String shoppingListItemSeqId = (String) paramMap.get("shoppingListItemSeqId");
        if (paramMap.containsKey("ADD_PRODUCT_ID")) {
            productId = (String) paramMap.remove("ADD_PRODUCT_ID");
        } else if (paramMap.containsKey("add_product_id")) {
            Object object = paramMap.remove("add_product_id");
            try {
                productId = (String) object;
            } catch (ClassCastException e) {
                List<String> productList = UtilGenerics.checkList(object);
                productId = productList.get(0);
            }
        }
        if (paramMap.containsKey("PRODUCT_ID")) {
            parentProductId = (String) paramMap.remove("PRODUCT_ID");
        } else if (paramMap.containsKey("product_id")) {
            parentProductId = (String) paramMap.remove("product_id");
        }

        Debug.logInfo("adding item product " + productId, module);
        Debug.logInfo("adding item parent product " + parentProductId, module);

        if (paramMap.containsKey("ADD_CATEGORY_ID")) {
            productCategoryId = (String) paramMap.remove("ADD_CATEGORY_ID");
        } else if (paramMap.containsKey("add_category_id")) {
            productCategoryId = (String) paramMap.remove("add_category_id");
        }
        if (productCategoryId != null && productCategoryId.length() == 0) {
            productCategoryId = null;
        }

        if(UtilValidate.isNotEmpty(productId))
        	productId = productId.trim();
        
        String productID = productId;
        if(UtilValidate.isEmpty(productID))
        	productID = parentProductId;
        
        GenericValue product1 = null;
		try {
			product1 = delegator.findOne("Product",UtilMisc.toMap("productId",productID),true);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
        if(UtilValidate.isNotEmpty(product1))
        	productCategoryId = product1.getString("primaryProductCategoryId");
        
        if(UtilValidate.isEmpty(productCategoryId)){
        	product1 = ProductWorker.getParentProduct(productID, delegator);
	        if(UtilValidate.isNotEmpty(product1))
	        	productCategoryId = product1.getString("primaryProductCategoryId");
        }
        
        if (paramMap.containsKey("ADD_ITEM_TYPE")) {
            itemType = (String) paramMap.remove("ADD_ITEM_TYPE");
        } else if (paramMap.containsKey("add_item_type")) {
            itemType = (String) paramMap.remove("add_item_type");
        }

        if (UtilValidate.isEmpty(productId)) {
            // before returning error; check make sure we aren't adding a special item type
            if (UtilValidate.isEmpty(itemType)) {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.noProductInfoPassed", locale));
                return "success"; // not critical return to same page
            }
        } else {
            try {
                String pId = ProductWorker.findProductId(delegator, productId);
                if (pId != null) {
                    productId = pId;
                }
            } catch (Throwable e) {
                Debug.logWarning(e, module);
            }
        }

        // check for an itemDescription
        if (paramMap.containsKey("ADD_ITEM_DESCRIPTION")) {
            itemDescription = (String) paramMap.remove("ADD_ITEM_DESCRIPTION");
        } else if (paramMap.containsKey("add_item_description")) {
            itemDescription = (String) paramMap.remove("add_item_description");
        }
        if (itemDescription != null && itemDescription.length() == 0) {
            itemDescription = null;
        }

        // Get the ProductConfigWrapper (it's not null only for configurable items)
        // No Configuration product in youmart
        ProductConfigWrapper configWrapper = null;
//        configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency(), request);
 //
//        if (configWrapper != null) {
//            if (paramMap.containsKey("configId")) {
//                try {
//                    configWrapper.loadConfig(delegator, (String) paramMap.remove("configId"));
//                } catch (Exception e) {
//                    Debug.logWarning(e, "Could not load configuration", module);
//                }
//            } else {
//                // The choices selected by the user are taken from request and set in the wrapper
//                ProductConfigWorker.fillProductConfigWrapper(configWrapper, request);
//            }
//            if (!configWrapper.isCompleted()) {
//                // The configuration is not valid
//                request.setAttribute("product_id", productId);
//                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.configureProductBeforeAddingToCart", locale));
//                return "product";
//            } else {
//                // load the Config Id
//                ProductConfigWorker.storeProductConfigWrapper(configWrapper, delegator);
//            }
//        }


        //Check for virtual products
        if (ProductWorker.isVirtual(delegator, productId)) {

            if ("VV_FEATURETREE".equals(ProductWorker.getProductVirtualVariantMethod(delegator, productId))) {
                // get the selected features.
                List<String> selectedFeatures = new LinkedList<String>();
                Enumeration<String> paramNames = UtilGenerics.cast(request.getParameterNames());
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    if (paramName.startsWith("FT")) {
                        selectedFeatures.add(request.getParameterValues(paramName)[0]);
                    }
                }

                // check if features are selected
                if (UtilValidate.isEmpty(selectedFeatures)) {
                    request.setAttribute("paramMap", paramMap);
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                    return "product";
                }

                String variantProductId = ProductWorker.getVariantFromFeatureTree(productId, selectedFeatures, delegator);
                if (UtilValidate.isNotEmpty(variantProductId)) {
                    productId = variantProductId;
                } else {
                    request.setAttribute("paramMap", paramMap);
                    request.setAttribute("product_id", productId);
                    request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.incompatibilityVariantFeature", locale));
                    return "product";
                }

            } else {
                request.setAttribute("paramMap", paramMap);
                request.setAttribute("product_id", productId);
                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.chooseVariationBeforeAddingToCart", locale));
                return "product";
            }
        }

        // get the override price
        if (paramMap.containsKey("PRICE")) {
            priceStr = (String) paramMap.remove("PRICE");
        } else if (paramMap.containsKey("price")) {
            priceStr = (String) paramMap.remove("price");
        }
        if (priceStr == null) {
            priceStr = "0";  // default price is 0
        }

        // get the renting data
        // In youmat all are FINISHED_PRODUCT or Digital goods
//        if ("ASSET_USAGE".equals(ProductWorker.getProductTypeId(delegator, productId))) {
//            if (paramMap.containsKey("reservStart")) {
//                reservStartStr = (String) paramMap.remove("reservStart");
//                if (reservStartStr.length() == 10) // only date provided, no time string?
//                    reservStartStr += " 00:00:00.000000000"; // should have format: yyyy-mm-dd hh:mm:ss.fffffffff
//                if (reservStartStr.length() > 0) {
//                    try {
//                        reservStart = java.sql.Timestamp.valueOf(reservStartStr);
//                    } catch (Exception e) {
//                        Debug.logWarning(e, "Problems parsing Reservation start string: "
//                                + reservStartStr, module);
//                        reservStart = null;
//                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.rental.startDate", locale));
//                        return "error";
//                    }
//                } else reservStart = null;
//            }
//
//            if (paramMap.containsKey("reservEnd")) {
//                reservEndStr = (String) paramMap.remove("reservEnd");
//                if (reservEndStr.length() == 10) // only date provided, no time string?
//                    reservEndStr += " 00:00:00.000000000"; // should have format: yyyy-mm-dd hh:mm:ss.fffffffff
//                if (reservEndStr.length() > 0) {
//                    try {
//                        reservEnd = java.sql.Timestamp.valueOf(reservEndStr);
//                    } catch (Exception e) {
//                        Debug.logWarning(e, "Problems parsing Reservation end string: " + reservEndStr, module);
//                        reservEnd = null;
//                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.rental.endDate", locale));
//                        return "error";
//                    }
//                } else reservEnd = null;
//            }
//
//            if (reservStart != null && reservEnd != null) {
//                reservLength = new BigDecimal(UtilDateTime.getInterval(reservStart, reservEnd)).divide(new BigDecimal("86400000"), generalRounding);
//            }
//
//            if (reservStart != null && paramMap.containsKey("reservLength")) {
//                reservLengthStr = (String) paramMap.remove("reservLength");
//                // parse the reservation Length
//                try {
//                    reservLength = (BigDecimal) ObjectType.simpleTypeConvert(reservLengthStr, "BigDecimal", null, locale);
//                } catch (Exception e) {
//                    Debug.logWarning(e, "Problems parsing reservation length string: "
//                            + reservLengthStr, module);
//                    reservLength = BigDecimal.ONE;
//                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderReservationLengthShouldBeAPositiveNumber", locale));
//                    return "error";
//                }
//            }
//
//            if (reservStart != null && paramMap.containsKey("reservPersons")) {
//                reservPersonsStr = (String) paramMap.remove("reservPersons");
//                // parse the number of persons
//                try {
//                    reservPersons = (BigDecimal) ObjectType.simpleTypeConvert(reservPersonsStr, "BigDecimal", null, locale);
//                } catch (Exception e) {
//                    Debug.logWarning(e, "Problems parsing reservation number of persons string: " + reservPersonsStr, module);
//                    reservPersons = BigDecimal.ONE;
//                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderNumberOfPersonsShouldBeOneOrLarger", locale));
//                    return "error";
//                }
//            }
//
//            //check for valid rental parameters
//            if (UtilValidate.isEmpty(reservStart) && UtilValidate.isEmpty(reservLength) && UtilValidate.isEmpty(reservPersons)) {
//                request.setAttribute("product_id", productId);
//                request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.enterBookingInforamtionBeforeAddingToCart", locale));
//                return "product";
//            }
//
//            //check accommodation for reservations
//            if ((paramMap.containsKey("accommodationMapId")) && (paramMap.containsKey("accommodationSpotId"))) {
//                accommodationMapId = (String) paramMap.remove("accommodationMapId");
//                accommodationSpotId = (String) paramMap.remove("accommodationSpotId");
//            }
//        }

        // get the quantity
        if (paramMap.containsKey("QUANTITY")) {
            quantityStr = (String) paramMap.remove("QUANTITY");
        } else if (paramMap.containsKey("quantity")) {
            quantityStr = (String) paramMap.remove("quantity");
        }
        if (UtilValidate.isEmpty(quantityStr)) {
            quantityStr = "1";  // default quantity is 1
        }
        
        BigDecimal qty = BigDecimal.ZERO;
        List<ShoppingCartItem> items = cart.items();
        if(UtilValidate.isNotEmpty(items))
        {
	        for(ShoppingCartItem item : items){
	        	if(item.getProductId().trim().equals(productId.trim()))
	        		qty = qty.add(item.getQuantity());
	        }
        }
        
        boolean isCartContainsGiftCard = isCartContainsGiftCard(request, response);
        if(isCartContainsGiftCard && !productId.startsWith("GIFTCARD"))
        {
			try {
				response.getWriter().print("Can't add more than giftcard");
				cart.setSlot(null);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "failToAdd";
        }
        
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        BigDecimal maxQuantityToOrder = new BigDecimal(20);
        if(UtilValidate.isNotEmpty(productStore) && UtilValidate.isNotEmpty(productStore.getBigDecimal("maxQuantityToOrder")))
        	maxQuantityToOrder = productStore.getBigDecimal("maxQuantityToOrder");
        
        GenericValue productNew = null;
		try {
			productNew = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		if(UtilValidate.isEmpty(productNew)){
			try {
				response.getWriter().print("Product Not Found");
			} catch (IOException e1) {
 				e1.printStackTrace();
			}
			return "failToAdd";
		}
		BigDecimal totalATP = BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(productNew.get("inventoryAtp")))
			totalATP = productNew.getBigDecimal("inventoryAtp");
		else
			totalATP = totalATP(request, productId);
		if(!productId.startsWith("GIFTCARD") && totalATP.doubleValue() < 1)
		{
			try {
				response.getWriter().print("Due to Limited availibility of this product, you may add only "+totalATP.intValue()+" quantity to cart");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "failToAdd";
		}	
        if(!productId.startsWith("GIFTCARD") && UtilValidate.isNotEmpty(totalATP) && totalATP.doubleValue() < (Double.parseDouble(quantityStr) + qty.doubleValue()))
        {
			try {
				response.getWriter().print("Due to Limited availibility of this product, you may add only "+totalATP.intValue()+" quantity to cart");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "failToAdd";
        }
        
        GenericValue userLogin1 = cart.getUserLogin();
        double toDaysQuantityOrderedPerProduct = 0;
        if(UtilValidate.isNotEmpty(userLogin1))
        	toDaysQuantityOrderedPerProduct = 
        						toDaysQuantityOrderedPerProduct(delegator, productId.trim(), userLogin1.getString("userLoginId")).doubleValue();
        
        if(!productId.startsWith("GIFTCARD") && (Double.parseDouble(quantityStr)+qty.doubleValue()+toDaysQuantityOrderedPerProduct) > maxQuantityToOrder.doubleValue() )
        {
        	try {
        		GenericValue product = null;
				try {
					product = ProductWorker.findProduct(delegator, productId.trim());
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*if(UtilValidate.isNotEmpty(product))
					response.getWriter().print("Can not add more than "+maxQuantityToOrder.intValue()+" items for "+product.getString("brandName")+" "+product.getString("internalName") +" per day");
				else
					response.getWriter().print("Can not add more than "+maxQuantityToOrder.intValue()+" items per day");*/
				response.getWriter().print("Can't add more than "+maxQuantityToOrder.intValue()+" quantity per day");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return "failToAdd";
        }
        // parse the price
        try {
            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
            price = null;
        }

        // parse the quantity
        try {
            quantity = (BigDecimal) ObjectType.simpleTypeConvert(quantityStr, "BigDecimal", null, locale);
            //For quantity we should test if we allow to add decimal quantity for this product an productStore : if not then round to 0
            if(! ProductWorker.isDecimalQuantityOrderAllowed(delegator, productId, cart.getProductStoreId())){
                quantity = quantity.setScale(0, UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            }
            else {
                quantity = quantity.setScale(UtilNumber.getBigDecimalScale("order.decimals"), UtilNumber.getBigDecimalRoundingMode("order.rounding"));
            }
        } catch (Exception e) {
            Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
            quantity = BigDecimal.ONE;
        }

        // get the selected amount
        String selectedAmountStr = null;
        if (paramMap.containsKey("ADD_AMOUNT")) {
            selectedAmountStr = (String) paramMap.remove("ADD_AMOUNT");
        } else if (paramMap.containsKey("add_amount")) {
            selectedAmountStr = (String) paramMap.remove("add_amount");
        }

        // parse the amount
        BigDecimal amount = null;
        if (UtilValidate.isNotEmpty(selectedAmountStr)) {
            try {
                amount = (BigDecimal) ObjectType.simpleTypeConvert(selectedAmountStr, "BigDecimal", null, locale);
            } catch (Exception e) {
                Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                amount = null;
            }
        } else {
            amount = BigDecimal.ZERO;
        }

        // check for required amount
        if ((ProductWorker.isAmountRequired(delegator, productId)) && (amount == null || amount.doubleValue() == 0.0)) {
            request.setAttribute("product_id", productId);
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource_error, "cart.addToCart.enterAmountBeforeAddingToCart", locale));
            return "product";
        }

        // get the ship before date (handles both yyyy-mm-dd input and full timestamp)
        shipBeforeDateStr = (String) paramMap.remove("shipBeforeDate");
        if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
            if (shipBeforeDateStr.length() == 10) shipBeforeDateStr += " 00:00:00.000";
            try {
                shipBeforeDate = java.sql.Timestamp.valueOf(shipBeforeDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad shipBeforeDate input: " + e.getMessage(), module);
                shipBeforeDate = null;
            }
        }

        // get the ship after date (handles both yyyy-mm-dd input and full timestamp)
        shipAfterDateStr = (String) paramMap.remove("shipAfterDate");
        if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
            if (shipAfterDateStr.length() == 10) shipAfterDateStr += " 00:00:00.000";
            try {
                shipAfterDate = java.sql.Timestamp.valueOf(shipAfterDateStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad shipAfterDate input: " + e.getMessage(), module);
                shipAfterDate = null;
            }
        }

        // check for an add-to cart survey 
        // Not Required for this youmart requirement 
//        List<String> surveyResponses = null;
//        if (productId != null) {
//            String productStoreId = ProductStoreWorker.getProductStoreId(request);
//            List<GenericValue> productSurvey = ProductStoreWorker.getProductSurveys(delegator, productStoreId, productId, "CART_ADD", parentProductId);
//            if (UtilValidate.isNotEmpty(productSurvey)) {
//                // TODO: implement multiple survey per product
//                GenericValue survey = EntityUtil.getFirst(productSurvey);
//                String surveyResponseId = (String) request.getAttribute("surveyResponseId");
//                if (surveyResponseId != null) {
//                    surveyResponses = UtilMisc.toList(surveyResponseId);
//                } else {
//                    String origParamMapId = UtilHttp.stashParameterMap(request);
//                    Map<String, Object> surveyContext = UtilMisc.<String, Object>toMap("_ORIG_PARAM_MAP_ID_", origParamMapId);
//                    GenericValue userLogin = cart.getUserLogin();
//                    String partyId = null;
//                    if (userLogin != null) {
//                        partyId = userLogin.getString("partyId");
//                    }
//                    String formAction = "/additemsurvey";
//                    String nextPage = RequestHandler.getOverrideViewUri(request.getPathInfo());
//                    if (nextPage != null) {
//                        formAction = formAction + "/" + nextPage;
//                    }
//                    ProductStoreSurveyWrapper wrapper = new ProductStoreSurveyWrapper(survey, partyId, surveyContext);
//                    request.setAttribute("surveyWrapper", wrapper);
//                    request.setAttribute("surveyAction", formAction); // will be used as the form action of the survey
//                    return "survey";
//                }
//            }
//        }
//        if (surveyResponses != null) {
//            paramMap.put("surveyResponses", surveyResponses);
//        }

        if (productStore != null) {
            String addToCartRemoveIncompat = productStore.getString("addToCartRemoveIncompat");
            String addToCartReplaceUpsell = productStore.getString("addToCartReplaceUpsell");
            try {
                if ("Y".equals(addToCartRemoveIncompat)) {
                    List<GenericValue> productAssocs = null;
                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId), EntityOperator.OR, EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId)),
                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_INCOMPATABLE")), EntityOperator.AND);
                    productAssocs = delegator.findList("ProductAssoc", cond, null, null, null, false);
                    productAssocs = EntityUtil.filterByDate(productAssocs);
                   // List<String> productList = FastList.newInstance();
                    List<String> productList = new ArrayList<String>();
                    Iterator<GenericValue> iter = productAssocs.iterator();
                    while (iter.hasNext()) {
                        GenericValue productAssoc = iter.next();
                        if (productId.equals(productAssoc.getString("productId"))) {
                            productList.add(productAssoc.getString("productIdTo"));
                            continue;
                        }
                        if (productId.equals(productAssoc.getString("productIdTo"))) {
                            productList.add(productAssoc.getString("productId"));
                            continue;
                        }
                    }
                    Iterator<ShoppingCartItem> sciIter = cart.iterator();
                    while (sciIter.hasNext()) {
                        ShoppingCartItem sci = sciIter.next();
                        if (productList.contains(sci.getProductId())) {
                            try {
                                cart.removeCartItem(sci, dispatcher);
                            } catch (CartItemModifyException e) {
                                Debug.logError(e.getMessage(), module);
                            }
                        }
                    }
                }
                if ("Y".equals(addToCartReplaceUpsell)) {
                    List<GenericValue> productList = null;
                    EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productId),
                            EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "PRODUCT_UPGRADE")), EntityOperator.AND);
                    productList = delegator.findList("ProductAssoc", cond, UtilMisc.toSet("productId"), null, null, false);
                    if (productList != null) {
                        Iterator<ShoppingCartItem> sciIter = cart.iterator();
                        while (sciIter.hasNext()) {
                            ShoppingCartItem sci = sciIter.next();
                            if (productList.contains(sci.getProductId())) {
                                try {
                                    cart.removeCartItem(sci, dispatcher);
                                } catch (CartItemModifyException e) {
                                    Debug.logError(e.getMessage(), module);
                                }
                            }
                        }
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
            }
        }
        
        // check for alternative packing
        //It is not a alternate package
//        if(ProductWorker.isAlternativePacking(delegator, productId , parentProductId)){
//            GenericValue parentProduct = null;
//            try {
//                parentProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", parentProductId));
//            } catch (GenericEntityException e) {
//                Debug.logError(e, "Error getting parent product", module);
//            }
//            BigDecimal piecesIncluded = BigDecimal.ZERO;
//            if(parentProduct != null){
//                piecesIncluded = new BigDecimal(parentProduct.getLong("piecesIncluded"));
//                quantity = quantity.multiply(piecesIncluded);
//            }
//        }

        // Translate the parameters and add to the cart
        result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId, productCategoryId,
                itemType, itemDescription, price, amount, quantity, reservStart, reservLength, reservPersons,
                accommodationMapId, accommodationSpotId,
                shipBeforeDate, shipAfterDate, configWrapper, itemGroupNumber, paramMap, parentProductId);
        
        controlDirective = processResult(result, request);
        
        // Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
        	
        	
        	
        	boolean flag = canBuyGiftCard(request);
            if(flag)
            	return "payment";
        	
            if (cart.viewCartOnAdd()) {
                return "viewcart";
            } else {
            	request.setAttribute("message123", controlDirective);
                return "success";
            }
        }
    }
    
    public static BigDecimal totalATP(HttpServletRequest request, String productId){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        return totalATP(delegator, dispatcher , productId, productStore);
    }
    	
   public static BigDecimal totalATP(Delegator delegator, LocalDispatcher dispatcher, String productId, GenericValue productStore){
    	
        
        try {
			TransactionUtil.begin();
		} catch (GenericTransactionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
    	BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
    	GenericValue product = null;
		try {
			product = delegator.findOne("Product", true, UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map inventoryAvailable = null;
    	GenericValue newProduct = ProductWorker.getParentProduct(productId, delegator);
    	if(UtilValidate.isNotEmpty(newProduct)) product = newProduct;
    	if(UtilValidate.isNotEmpty(product))
    	if(UtilValidate.isNotEmpty(product.getString("isVirtual")) && "Y".equals(product.getString("isVirtual")))
    	{
    		try {
				inventoryAvailable = dispatcher.runSync("getProductInventoryAvailable", UtilMisc.toMap("productId",productId));
			} catch (GenericServiceException e) {
	                  Debug.logError(e, "Problems getting inventory available for product "+product.getString("productId"), module);
			}

    	}
    	else
    	{
    		try {
				inventoryAvailable = dispatcher.runSync("getInventoryAvailableByFacility", 
						UtilMisc.toMap("productId",product.getString("productId"),"facilityId",productStore.getString("inventoryFacilityId")));
			} catch (GenericServiceException e) {
				Debug.logError(e, "Problems getting inventory available by facility id "+productStore.getString("facilityId")+" for product "+product.getString("productId"), module);
			}
    	}
    	try {
			TransactionUtil.commit();
		} catch (GenericTransactionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
    	if(ServiceUtil.isError(inventoryAvailable))
    		return availableToPromiseTotal;
    	if(UtilValidate.isNotEmpty(inventoryAvailable))
			availableToPromiseTotal = (BigDecimal)inventoryAvailable.get("availableToPromiseTotal");
    	
    	return availableToPromiseTotal;
    }
    
    public static boolean isCartContainsGiftCard(HttpServletRequest request , HttpServletResponse response){
    	return isCartContainsGiftCard(getCartObject(request));
    }
    
    public static boolean isCartContainsGiftCard(ShoppingCart cart){
    	
    	if(UtilValidate.isEmpty(cart))return false;
    	
    	boolean flag = false;
    	List<ShoppingCartItem> cartItems = cart.items();
    	if(UtilValidate.isEmpty(cartItems))return false;
    	for(ShoppingCartItem cartItem : cartItems)
    	{
    		if(cartItem.getProductId().startsWith("GIFTCARD"))
    			flag = true;
    	}
    	return flag;
    }
    
    public static String canBuyGiftCard(HttpServletRequest request , HttpServletResponse response){
    	try {
	    	response.getWriter().print(canBuyGiftCard(request));
	    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return "success";
    }
    public static boolean canBuyGiftCard(HttpServletRequest request){
    	ShoppingCart cart = getCartObject(request);
    	if(UtilValidate.isEmpty(cart))return false;
    	
    	boolean flag = true;
    	List<ShoppingCartItem> cartItems = cart.items();
    	for(ShoppingCartItem cartItem : cartItems)
    	{
    		if(!cartItem.getProductId().startsWith("GIFTCARD"))
    			flag = false;
    	}
    	return flag;
    }
    
    public static String canGoToPaymentPage(HttpServletRequest request , HttpServletResponse response){
    	if(canBuyGiftCard(request))
    		return "payment";
    	return "success";
    }
    
    public static BigDecimal getCategoryMaxQuantity(Delegator delegator, String categoryId){
        BigDecimal maxQty = new BigDecimal(20);
        try
        {
            if(UtilValidate.isNotEmpty(categoryId))
            {
                GenericValue productCategoryGV = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
                String valueOfQty = productCategoryGV.getString("categoryMaxQuantity");
                if(UtilValidate.isNotEmpty(valueOfQty))
                {
                    maxQty = new BigDecimal(valueOfQty);
                }
            }
        }
        catch(GenericEntityException e) { }
        return maxQty;
    }
    
    public static BigDecimal toDaysQuantityOrderedPerProduct(Delegator delegator , String productId , String userLoginId){
    	
    	List condAND =  new ArrayList();
    	
    	condAND.add(EntityCondition.makeCondition("createdBy",EntityOperator.EQUALS,userLoginId));
    	condAND.add(EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
    	condAND.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())));
    	
    	List condOR =  new ArrayList();
    	
    	condOR.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_APPROVED"));
    	condOR.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_COMPLETED"));
    	condOR.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_FULFILLED"));
    	
    	
    	List condition = new ArrayList();
    	
    	condition.add(EntityCondition.makeCondition(condAND,EntityOperator.AND));
    	condition.add(EntityCondition.makeCondition(condOR,EntityOperator.OR));
    	
    	List<GenericValue> orderList = null;
    	
    	BigDecimal quantity = BigDecimal.ZERO;
    	BigDecimal cancelQuantity = BigDecimal.ZERO;
    	try {
    		orderList = delegator.findList("OrderHeader", EntityCondition.makeCondition(condition,EntityOperator.AND),
																						UtilMisc.toSet("orderId"), null, null, false);
    		List<GenericValue> orderItemList = null;
    		for(GenericValue order : orderList){
    			
    			condition = new ArrayList();
    			condition.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,order.getString("orderId")));
    	    	condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
    	    	condition.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_APPROVED"));
    			
    			orderItemList = delegator.findList("OrderItem", EntityCondition.makeCondition(condition,EntityOperator.AND),
																		UtilMisc.toSet("quantity","cancelQuantity"), null, null, false);
    			for(GenericValue orderItem : orderItemList){
    				if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("quantity")))
    					quantity = quantity.add(orderItem.getBigDecimal("quantity"));
    				
    				if(UtilValidate.isNotEmpty(orderItem.getBigDecimal("cancelQuantity")))
    					cancelQuantity = cancelQuantity.add(orderItem.getBigDecimal("cancelQuantity"));
    			}
			}
    	} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return quantity.subtract(cancelQuantity);
    }
    
    public static String addToCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
        String orderId = request.getParameter("orderId");
        String itemGroupNumber = request.getParameter("itemGroupNumber");
        String[] itemIds = request.getParameterValues("item_id");
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        ShoppingCart cart = getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        Map<String, Object> result;
        String controlDirective;
        
        

        boolean addAll = ("true".equals(request.getParameter("add_all")));
        result = cartHelper.addToCartFromOrder(catalogId, orderId, itemIds, addAll, itemGroupNumber);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to quantity request parameter
     * for each; if no parameter for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addToCartBulk(HttpServletRequest request, HttpServletResponse response) {
        String categoryId = request.getParameter("category_id");
        ShoppingCart cart = getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Convert the params to a map to pass in
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartBulk(catalogId, categoryId, paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    public static String quickInitPurchaseOrder(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        String supplierPartyId = request.getParameter("supplierPartyId_o_0");
        
        // check the preferred currency of the supplier, if set, use that for the cart, otherwise use system defaults.
        ShoppingCart cart = null;
        try {
            GenericValue supplierParty = delegator.findOne("Party", UtilMisc.toMap("partyId", supplierPartyId), false);
            if (UtilValidate.isNotEmpty(supplierParty.getString("preferredCurrencyUomId"))) {
                cart = new WebShoppingCart(request, locale, supplierParty.getString("preferredCurrencyUomId"));
            } else {
                cart = new WebShoppingCart(request);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
        }
        
        // TODO: the code below here needs some cleanups
        String billToCustomerPartyId = request.getParameter("billToCustomerPartyId_o_0");
        if (UtilValidate.isEmpty(billToCustomerPartyId) && UtilValidate.isEmpty(supplierPartyId)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderCouldNotInitPurchaseOrder", locale));
            return "error";
        }
        String orderId = request.getParameter("orderId_o_0");
        // set the order id if supplied
        if (UtilValidate.isNotEmpty(orderId)) {
            GenericValue thisOrder = null;
            try {
                thisOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            } catch (GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
            }
            if (thisOrder == null) {
                cart.setOrderId(orderId);
            } else {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderIdAlreadyExistsPleaseChooseAnother", locale));
                return "error";
            }
        }
        cart.setBillToCustomerPartyId(billToCustomerPartyId);
        cart.setBillFromVendorPartyId(supplierPartyId);
        cart.setOrderPartyId(supplierPartyId);
        cart.setOrderId(orderId);
        String agreementId = request.getParameter("agreementId_o_0");
        if (UtilValidate.isNotEmpty(agreementId)) {
            ShoppingCartHelper sch = new ShoppingCartHelper(delegator, dispatcher, cart);
            sch.selectAgreement(agreementId);
        }

        cart.setOrderType("PURCHASE_ORDER");

        session.setAttribute("shoppingCart", cart);
        session.setAttribute("productStoreId", cart.getProductStoreId());
        session.setAttribute("orderMode", cart.getOrderType());
        session.setAttribute("orderPartyId", cart.getOrderPartyId());

        return "success";
    }

    public static String quickCheckoutOrderWithDefaultOptions(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);

        // Set the cart's default checkout options for a quick checkout
        cart.setDefaultCheckoutOptions(dispatcher);

        return "success";
    }

    /** Adds a set of requirements to the cart
     */
    public static String addToCartBulkRequirements(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Convert the params to a map to pass in
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        result = cartHelper.addToCartBulkRequirements(catalogId, paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Adds all products in a category according to default quantity on ProductCategoryMember
     * for each; if no default for a certain product in the category, or if
     * quantity is 0, do not add
     */
    public static String addCategoryDefaults(HttpServletRequest request, HttpServletResponse response) {
        String itemGroupNumber = request.getParameter("itemGroupNumber");
        String categoryId = request.getParameter("category_id");
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        ShoppingCart cart = getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        BigDecimal totalQuantity;
        Locale locale = UtilHttp.getLocale(request);

        result = cartHelper.addCategoryDefaults(catalogId, categoryId, itemGroupNumber);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            totalQuantity = (BigDecimal)result.get("totalQuantity");
            Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("totalQuantity", UtilFormatOut.formatQuantity(totalQuantity.doubleValue()));

            request.setAttribute("_EVENT_MESSAGE_",
                                  UtilProperties.getMessage(resource_error, "cart.add_category_defaults",
                                          messageMap, locale));

            return "success";
        }
    }

    /** Delete an item from the shopping cart. */
    public static String deleteFromCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        //Delegate the cart helper
        result = cartHelper.deleteFromCart(paramMap);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    /** Update the items in the shopping cart. */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = getCartObject(request);
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(null, dispatcher, cart);
        String controlDirective;
        Map<String, Object> result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        
        String removeSelectedFlag = request.getParameter("removeSelected");
        String selectedItems[] = request.getParameterValues("selectedItem");
        boolean removeSelected = ("true".equals(removeSelectedFlag) && selectedItems != null && selectedItems.length > 0);
        
        String quantity = request.getParameter("quantity");
        String prodId = request.getParameter("prodId");

        if (UtilValidate.isEmpty(quantity)) {
        	quantity = "1";  // default quantity is 1
        }
        
        BigDecimal totalATP = totalATP(request, prodId);
        if(!removeSelected && UtilValidate.isNotEmpty(prodId) && !prodId.startsWith("GIFTCARD") && UtilValidate.isNotEmpty(totalATP) && totalATP.doubleValue() < Double.parseDouble(quantity))
        {
        	try {
				response.getWriter().print("Due to Limited availibility of this product, you may add only "+totalATP.intValue()+" quantity to cart");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "failToAdd";
        }
		
        
       /* BigDecimal qty = BigDecimal.ZERO;
        List<ShoppingCartItem> items = cart.items();
        if(UtilValidate.isNotEmpty(items))
        {
	        for(ShoppingCartItem item : items){
	        	if(item.getProductId().trim().equals(prodId.trim()))
	        		qty = qty.add(item.getQuantity());
	        }
        }*/
        GenericValue userLogin1 = cart.getUserLogin();
        double toDaysQuantityOrderedPerProduct = 0;
        if(UtilValidate.isNotEmpty(userLogin1) && !removeSelected)
        	toDaysQuantityOrderedPerProduct = 
        						toDaysQuantityOrderedPerProduct(cart.getDelegator(), prodId.trim(), userLogin1.getString("userLoginId")).doubleValue();

        ////System.out.println("       quantity       "+quantity);
        ////System.out.println("       qty       "+qty);
        ////System.out.println("       toDaysQuantityOrderedPerProduct       "+toDaysQuantityOrderedPerProduct);
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        BigDecimal maxQuantityToOrder = new BigDecimal(20);
        if(UtilValidate.isNotEmpty(productStore) && UtilValidate.isNotEmpty(productStore.getBigDecimal("maxQuantityToOrder")))
        	maxQuantityToOrder = productStore.getBigDecimal("maxQuantityToOrder");
        
        
        if((!removeSelected && UtilValidate.isNotEmpty(prodId) && !prodId.startsWith("GIFTCARD") && (Double.parseDouble(quantity)+toDaysQuantityOrderedPerProduct) > maxQuantityToOrder.doubleValue() ))
        {
        	try {
        		GenericValue product = null;
				try {
					product = ProductWorker.findProduct(cart.getDelegator(), prodId.trim());
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*if(UtilValidate.isNotEmpty(product))
					response.getWriter().print("Can not add more than "+maxQuantityToOrder.intValue()+" items for "+product.getString("brandName")+" "+product.getString("internalName") +" per day");
				else
					response.getWriter().print("Can not add more than "+maxQuantityToOrder.intValue()+" items per day");*/
				response.getWriter().print("Can't add more than "+maxQuantityToOrder.intValue()+" quantity per day");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "failToAdd";
        }

        result = cartHelper.modifyCart(security, userLogin, paramMap, removeSelected, selectedItems, locale);
        controlDirective = processResult(result, request);

        //Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }
    
    /** Display items details in popup window. */
    public static String findProdWgt(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List<GenericValue> featureAppls = null;
        String output = null;
        String description = null;
        String intName = null;
        GenericValue product = null;
        String add_product_id = request.getParameter("add_product_id");
        String quantity = request.getParameter("quantity");
        try {
        	PrintWriter out = response.getWriter();
			featureAppls = dispatcher.getDelegator().findByAndCache("ProductFeatureAppl", UtilMisc.toMap("productId", add_product_id));
			List<EntityExpr> filterExprs = UtilMisc.toList(EntityCondition.makeCondition("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
            filterExprs.add(EntityCondition.makeCondition("productFeatureApplTypeId", EntityOperator.EQUALS, "SELECTABLE_FEATURE"));
            featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
            product = dispatcher.getDelegator().findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", add_product_id));
            if (product != null) {
            	intName=  product.getString("productName");
            }
            if (featureAppls != null) {
                Iterator<GenericValue> fai = featureAppls.iterator();
                while (fai.hasNext()) {
                    GenericValue appl = fai.next();
                    GenericValue productFeature = dispatcher.getDelegator().findByPrimaryKey("ProductFeature", UtilMisc.toMap("productFeatureId", appl.getString("productFeatureId")));
                    if (productFeature != null) {
                    	description = productFeature.getString("description");
                    }
                }
            }
            /*output = quantity+" piece of ";*/
            if(description != null){
            output = description+" "+intName+" successfully added to the cart";}
            else{
            output = intName+" successfully added to the cart";}
            out.print(output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Determine where to send the browser
            return "success";
    }
    
    /** Display items details in popup window. */
    public static String findProdWgtShop(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List<GenericValue> featureAppls = null;
        String output = null;
        String description = null;
        String intName = null;
        GenericValue product = null;
        String add_product_id = request.getParameter("add_product_id");
        String quantity = request.getParameter("quantity");
        try {
        	PrintWriter out = response.getWriter();
			featureAppls = dispatcher.getDelegator().findByAndCache("ProductFeatureAppl", UtilMisc.toMap("productId", add_product_id));
			List<EntityExpr> filterExprs = UtilMisc.toList(EntityCondition.makeCondition("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
            filterExprs.add(EntityCondition.makeCondition("productFeatureApplTypeId", EntityOperator.EQUALS, "SELECTABLE_FEATURE"));
            featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
            product = dispatcher.getDelegator().findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", add_product_id));
            if (product != null) {
            	intName=  product.getString("productName");
            }
            if (featureAppls != null) {
                Iterator<GenericValue> fai = featureAppls.iterator();
                while (fai.hasNext()) {
                    GenericValue appl = fai.next();
                    GenericValue productFeature = dispatcher.getDelegator().findByPrimaryKey("ProductFeature", UtilMisc.toMap("productFeatureId", appl.getString("productFeatureId")));
                    if (productFeature != null) {
                    	description = productFeature.getString("description");
                    }
                }
            }
            /*output = quantity+" piece of ";*/
            if(description != null){
            output = description+" "+intName+" successfully added to the favourite list";}
            else{
            output = intName+" successfully added to the favourite list";}
            out.print(output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //Determine where to send the browser
            return "success";
    }
    
    /** Autocomplete Product name. */
    public static String autoProductName1(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String helperName = delegator.getGroupHelperName("org.ofbiz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List li = new ArrayList();
        String result[] = null;
        Connection conn = null;
		Statement statement = null;
		String removeSelectedFlag = request.getParameter("sport");
		
	/*	
		List cond = new ArrayList();
		
		cond.add(EntityCondition.makeCondition("",EntityOperator.LIKE,""));
		cond.add(EntityCondition.makeCondition("",EntityOperator.LIKE,""));
		*/
        String query="SELECT DISTINCT product_name FROM product WHERE LOWER(product_Name) LIKE LOWER('"+removeSelectedFlag.toLowerCase()+"%') OR LOWER(internal_Name) LIKE LOWER('"+removeSelectedFlag.toLowerCase()+"%') limit 10";
        try {
			PrintWriter out = response.getWriter();
			String s[]=null;
	    	  if(removeSelectedFlag!=null && !removeSelectedFlag.equals(""))
	    	  {
		    	   conn = ConnectionFactory.getConnection(helperName);
		    	   PreparedStatement ps=conn.prepareStatement(query);
		    	    ResultSet rs=ps.executeQuery();
		    	  while(rs.next())
		          {
		              //li.add(rs.getString("product_name"));
		              out.print(rs.getString("product_name")+",");
		          }
	    	  }
	    	  String[] str = new String[li.size()];
		       Iterator it = li.iterator();
		 
		       int i = 0;
		       while(it.hasNext())
		       {
		           String p = (String)it.next();
		           str[i] = p;
		           i++;
		       }
		 
		    //jQuery related start
		       String querys = (String)request.getParameter("q");
		 
		       int cnt=1;
		       for(int j=0;j<str.length;j++)
		       {
		              out.print(str[j]+",");
		              if(cnt>=200)// How many results have to show while we are typing(auto suggestions)
		              break;
		              cnt++;
		       }
	          } 
		
		
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	          }finally{
					try {
						if(statement != null) statement.close();
						if(conn != null) conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

        //Determine where to send the browser
            return "success";
    }
    
    
    public static String autoProductName(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String querys = (String)request.getParameter("q");
        
		String removeSelectedFlag = request.getParameter("sport");
		
		double searchCharStartsFrom = 3;
		if(UtilValidate.isNotEmpty(productStore.getDouble("searchCharStartsFrom")))
			searchCharStartsFrom = productStore.getDouble("searchCharStartsFrom");

		if(UtilValidate.isEmpty(removeSelectedFlag) || removeSelectedFlag.length() <= searchCharStartsFrom)
			return "success";
		EntityCondition condition = searchCondition(removeSelectedFlag, productStore.getString("showOutOfStockInSearch"), "");

		List<GenericValue> productList = null;
        try {
			PrintWriter out = response.getWriter();
			EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
	    	productList = delegator.findList("Product", condition,null, null, findOptions, false);

	    	double startingLikeSearchDropDownSize = 10;
	    	//double likeSearchDropDownSize = 10;
	    	if(UtilValidate.isNotEmpty(productStore.getDouble("startingLikeSearchDropDownSize")))
		    	startingLikeSearchDropDownSize = productStore.getDouble("startingLikeSearchDropDownSize");

	    	//if(UtilValidate.isNotEmpty(productStore.getDouble("likeSearchDropDownSize")))
	    	  //likeSearchDropDownSize = productStore.getDouble("likeSearchDropDownSize");
	    	int prodListSize = productList.size();
	    	if(prodListSize > (int)startingLikeSearchDropDownSize)
	    		productList = productList.subList(0, (int)startingLikeSearchDropDownSize);
  
	    	String resultDataNew = "";
	    	List internalNames = new ArrayList();
	    	resultDataNew = resultDataNew+"<p id=\"searchresults\">";
	    	for(GenericValue product : productList){
		    	String productName = product.getString("internalName");
		    	String description = product.getString("description");
		    	if(UtilValidate.isEmpty(description)) description = "";
		    	
		    	if(UtilValidate.isEmpty(productName))productName = "";
		    	productName = productName.trim();
		    	if(!internalNames.contains(productName))
		    	{
		    	String brandName = product.getString("brandName");
		    	if(UtilValidate.isNotEmpty(brandName))
		    		productName=brandName+" "+productName;
		    	
		    	String label = productName;
		    	if(UtilValidate.isNotEmpty(product.getString("productKeywords")))
		    		label = label + product.getString("productKeywords");
		    	
		    	String productId = product.getString("productId");
				String productCatgId = CategoryWorker.getCategoryFromProduct(delegator, product.getString("productId"));
				/*if(UtilValidate.isEmpty(productCatgId))
				  {
					GenericValue product1 = ProductWorker.getParentProduct(product.getString("productId"), delegator);
					 if(UtilValidate.isNotEmpty(product1))
						 productCatgId = CategoryWorker.getCategoryFromProduct(delegator, product1.getString("productId"));
					 if(UtilValidate.isEmpty(productCatgId))
						 productCatgId = "";
				  }*/
				if(UtilValidate.isNotEmpty(productCatgId))
				{
		    	 String prodCateUrl = CategoryWorker.getTrailAsString(delegator, productCatgId);
		    		/* resultData = resultData+"{\"make\": \""+productName.trim()+
			    			  "\",\"id\": '"+prodCateUrl+"c_c_c_p_"+productId+"'}";*/
		    		 
		    		 String url = "/products/"+prodCateUrl+"/"+productId+getProductIndexUrl(request, productId);
		    		 
		    		 resultDataNew = resultDataNew+"<span class=\"searchDiv\">";
		    		 resultDataNew = resultDataNew+"<span class=\"searchImgDiv\">";
		    		 resultDataNew = resultDataNew+"<a href=\""+url+"\">";
		    		 resultDataNew = resultDataNew+"<img src=\""+product.getString("mediumImageUrl")+"\" alt=\"\" class=\"searchImg\" />";
		    		
		    		 BigDecimal basePrice = product.getBigDecimal("basePrice");
		    		 if(UtilValidate.isEmpty(basePrice)) basePrice = BigDecimal.ZERO;
		    		 resultDataNew = resultDataNew+"<span class=\"searchheading\">"+productName.trim()+"<span class=\"searchPrice\">&nbsp;&nbsp;<span class=\"WebRupee\">&#8377;</span>&nbsp;</span><span class=\"searchPrice\" id=\"variant_price_displays\">"+basePrice.intValue()+"</span></span>";
		    		 resultDataNew = resultDataNew+"</a></span>";
		    		/* if(description.length() > 80)
		    			 description = description.substring(0,80)+".....";
		    		 
		    		 resultDataNew = resultDataNew+"<span>"+description+"</span></a>";*/
		    		 
		    		
		    		 resultDataNew = resultDataNew+"<span class=\"searchQtyDiv\">";  
		    		
		    		 resultDataNew = resultDataNew+"  <a href=\"javascript:searchChgQty(-1.0,'quantity"+productId+"');\" class=\"search_quantity_minus\"><img src=\"/erptheme1/Minus1.png\" alt=\"\"/></a>";
		    		 resultDataNew = resultDataNew+" <input type=\"text\" style=\"width:20px; margin:0px; height:16px;\" size=\"5\" class=\"searchInputBox\" onkeypress=\"return isNumberKey(event)\" name=\"quantity"+productId+"\" id=\"quantity"+productId+"\" value=\"1\"/>";
		    		 resultDataNew = resultDataNew+"<a href=\"javascript:searchChgQty(1.0,'quantity"+productId+"');\" class=\"search_quantity_plus\"><img src=\"/erptheme1/Add1.png\" alt=\"\"/></a>";
		    		 
		    		 BigDecimal inventoryAtp = product.getBigDecimal("inventoryAtp");
		    		 if(UtilValidate.isEmpty(inventoryAtp)) inventoryAtp = BigDecimal.ZERO;
		    		 
		    		 if(inventoryAtp.doubleValue() >= 1)
		    			 resultDataNew = resultDataNew+"<a href=\"javascript:searchAddToCart('"+productId+"')\" class=\"buttontext\">Add</a>";
		    		 else
		    			 resultDataNew = resultDataNew+"<img src=\"/erptheme1/search-out-of-stock.png\" alt=\"\" title=\"\"/>";
		    		 resultDataNew = resultDataNew+"</span>";
		    		 resultDataNew = resultDataNew+"</span>";
		    	 }
		    	
		    	internalNames.add(productName);
		      }
	    	}
	    	if(prodListSize > productList.size())
	    	{
	    		resultDataNew = resultDataNew+"<a href=\"/control/keywordsearch?VIEW_SIZE=10&PAGING=Y&filterBy=&sortBy=POPULAR_PRD&filterByCategory=&SEARCH_STRING="+removeSelectedFlag.trim()+"&x=0&y=0\">";
	    		resultDataNew = resultDataNew+"<span class=\"category\">View All</span></a>";
	    	}
	    	resultDataNew = resultDataNew+"</p>";
	    	if(UtilValidate.isNotEmpty(productList) && productList.size() > 0)
	    	{
		    	GenericValue firstProduct = productList.get(0);
		    	if (UtilValidate.isNotEmpty(firstProduct)) {
		    		if(UtilValidate.isNotEmpty(firstProduct.getString("productId")) && !"GIFTCARD".equals(firstProduct.getString("productId")))
		    			request.getSession().setAttribute("CURRENT_CATALOG_ID",org.ofbiz.product.category.CategoryWorker.getProdCatalogId(delegator,firstProduct.getString("productId")));
		    	}
	    	}
    	    out.print(resultDataNew);
          
        }
	      catch (Exception ex) 
	  	  {
	           ex.printStackTrace();
	      }
	      finally
	      {
		  }
        //Determine where to send the browser
            return "success";
}
   
    public static EntityCondition searchCondition(String removeSelectedFlag, String showOutOfStockInSearch, String excludeOutOfStock){
    	removeSelectedFlag = removeSelectedFlag.trim();
		
		String fieldValueLike = removeSelectedFlag+ "%";
        String fieldValue = removeSelectedFlag;
        
		List cond = new ArrayList();
		
		List condList =	new ArrayList();
		
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		
		
		if(UtilValidate.isNotEmpty(removeSelectedFlag))
  	  	{
   		  String []searchStrings = removeSelectedFlag.split(" ");
		  List conditionList = null;
		  List newConditionList = new ArrayList();
		  if(UtilValidate.isNotEmpty(searchStrings))
		  for(String searchString : searchStrings)
		  {
			  if(UtilValidate.isNotEmpty(searchString))
			  {
				  conditionList = new ArrayList();
				  searchString = "%"+searchString.trim() +"%";
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  
				  newConditionList.add(EntityCondition.makeCondition(conditionList, EntityOperator.OR));
			  }
		  }
		  condList.add(EntityCondition.makeCondition(newConditionList, EntityOperator.AND));
  	  	}
		
		List fromDateCond = new ArrayList();
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
    	List thruDateCond = new ArrayList();
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(condList,EntityOperator.OR));
		//cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
		//cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
		
		List salesDiscontinuationDateCond = new ArrayList();
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
		
		cond.add(EntityCondition.makeCondition("isVirtual",EntityOperator.NOT_EQUAL,"Y"));
		
		if("Y".equals(excludeOutOfStock) || !"Y".equalsIgnoreCase(showOutOfStockInSearch))
			cond.add(EntityCondition.makeCondition("inventoryAtp",EntityOperator.GREATER_THAN_EQUAL_TO,BigDecimal.ONE));
		
		EntityCondition condition = EntityCondition.makeCondition(cond,EntityOperator.AND);
		
		return condition;
    }
    
    public static String autoProductNameForRecipe(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String querys = (String)request.getParameter("q");
        
		String removeSelectedFlag = request.getParameter("sport");
		double searchCharStartsFrom = 3;
		if(UtilValidate.isNotEmpty(productStore.getDouble("searchCharStartsFrom")))
			searchCharStartsFrom = productStore.getDouble("searchCharStartsFrom");

		if(UtilValidate.isEmpty(removeSelectedFlag) || removeSelectedFlag.length() < searchCharStartsFrom)
			return "success";
		
		String fieldValue = removeSelectedFlag+ "%";
        String fieldValue1 = "% " +fieldValue ;
        
		List cond = new ArrayList();
		
		List condList =	UtilMisc.toList(
				EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("searchName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValue.toUpperCase())),
				EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("searchName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValue1.toUpperCase())));
 		
 
		cond.add(EntityCondition.makeCondition(condList,EntityOperator.OR));
		
		List salesDiscontinuationDateCond = new ArrayList();
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
		
		cond.add(EntityCondition.makeCondition("searchName", EntityOperator.NOT_EQUAL, null));
		
		EntityCondition condition = EntityCondition.makeCondition(cond,EntityOperator.AND);

//		//System.out.println("\n\n condition == "+condition+"\n\n");
		
		List<GenericValue> productList = null;
        try {
			PrintWriter out = response.getWriter();
			EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
	    	productList = delegator.findList("Product", condition,UtilMisc.toSet("productId", "searchName"), UtilMisc.toList("searchName"), findOptions, false);

	    	String resultData = "<script> var carMake = [";
 	    	for(GenericValue product : productList){
 		    	String productName = product.getString("searchName");
 		    	if(resultData.contains(productName))continue;
		    	String productId = product.getString("productId");
		    	
		    	  //out.print(productName+",");
		    	resultData = resultData+"{\"make\": \""+productName.trim()+"\",\"id\": '"+productId+"'},";
		      }
 	    	List condL = UtilMisc.toList(
			EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("enteredKeyword"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValue.toUpperCase())),
			EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("alternateKeyword"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValue1.toUpperCase())));
 	    	List<GenericValue> keywords = delegator.findList("KeywordThesaurus", EntityCondition.makeCondition(condL, EntityOperator.OR),null, null, findOptions, false);
 	    	for(GenericValue keyword : keywords){
 	    		resultData = resultData+"{\"make\": \""+keyword.getString("enteredKeyword").trim()+"\",\"id\": 'none'},";
 	    	}
 	    	
 	    	 resultData = resultData.substring(0, (resultData.length() - 1));
// 	    	 resultData = resultData+"{\"make\": \""+request.getParameter("sport")+" View All\",\"id\": 'allc_c_c_p_all'}";
// 	    	//System.out.println("\n\n resultData == "+resultData+"\n\n");
 
 	    	 resultData = resultData+"];" +
		    	  		"function addlabel(row) " +
		    	  		"{var make = row.make;" +
		    	  		"if(make == '"+request.getParameter("sport")+"') make = 'View All'; " +
		    	  		"row.label = make;" +
		    	  		/////////////////////
		    	  		///////////////////////////
		    	  		"row.value = make;" +  
		    	  		//comments:- if customer says wants brand name,then assign row.desc = make
		    	  		"row.desc = make;" +  
		    	  		"}carMake.forEach(addlabel);" +
		    	  		"</script>";
	    	  out.print(resultData);
	          }catch (Exception ex){
	    	  	ex.printStackTrace();
	          }
            return "success";
    }
    
    /*
     * @Ajaya
     * For getting the index of the variant products drop down and name
     */
    public static String getProductIndex(HttpServletRequest request, HttpServletResponse response) {
         String productId = request.getParameter("productId");
        try {
			response.getWriter().print(getProductIndexUrl(request, productId));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "success";
    }
    
    /*
     * @Ajaya
     * For getting the index of the variant products drop down and name
     */
    public static String getProductIndex(HttpServletRequest request, String productId) {
         Delegator delegator = (Delegator) request.getAttribute("delegator");
         LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
         
         
          
         String productID = request.getParameter("productId");
         if(UtilValidate.isEmpty(productID))productID = productId;
         
          
         return getProductIndex(delegator, dispatcher, productID, productId);
    }
    
    /*
     * @Ajaya
     * For getting the index of the variant products drop down and name
     */
    public static String getProductIndex(Delegator delegator, LocalDispatcher dispatcher,  String productID, String productId) {
    	
//    	  System.out.println("\n\n\n not calling");
         GenericValue product = ProductWorker.getParentProduct(productId, delegator);
		 if(UtilValidate.isNotEmpty(product))productId = product.getString("productId");

		 Set featureSet= new HashSet();
		 String index = "";
		 int count = 0;
         try {
 			Map featureMap = dispatcher.runSync("getProductFeatureSet", UtilMisc.toMap("productId",productId));
			featureSet= (LinkedHashSet)featureMap.get("featureSet");
			List salesDiscontinuationDateCond = new ArrayList();
			List productCodn=new ArrayList();
			salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
			salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
			productCodn.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
	    	productCodn.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
	    	
	    	List<GenericValue> productList = delegator.findList("Product", EntityCondition.makeCondition(productCodn,EntityOperator.AND),null, null, null, true);
			if(UtilValidate.isNotEmpty(featureSet) && UtilValidate.isNotEmpty(productList))
			{
				
				
				List fromDateCond = new ArrayList();
		    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
		    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
		    	
		    	List thruDateCond = new ArrayList();
		    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
		    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
		    	
		    	List cond = new ArrayList();
				cond.add(EntityCondition.makeCondition("productIdTo", EntityOperator.EQUALS, productID));
				
				cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
				cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
				cond.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"PRODUCT_VARIANT"));
				
				List<GenericValue> productAssocList = delegator.findList("ProductAssoc", EntityCondition.makeCondition(cond,EntityOperator.AND), 
																														null, null, null, true);
				String prodId = null;
				if(UtilValidate.isNotEmpty(productAssocList)){
					prodId = ((GenericValue)productAssocList.get(0)).getString("productId");
				}
				
				if(UtilValidate.isNotEmpty(prodId))
				{
					cond = new ArrayList();
					cond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, prodId));
					
					cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
					cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
					cond.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"PRODUCT_VARIANT"));
					
					productAssocList = delegator.findList("ProductAssoc", EntityCondition.makeCondition(cond,EntityOperator.AND), 
																								null, UtilMisc.toList("sequenceNum"), null, true);
					
					if(UtilValidate.isNotEmpty(productAssocList))
					for(GenericValue productAssoc : productAssocList)
					{
						if(UtilValidate.isNotEmpty(productAssoc.getString("productIdTo")) && productAssoc.getString("productIdTo").equals(productID))
						{
							if(UtilValidate.isNotEmpty(featureSet))
							{
								List listOfNames = new ArrayList(featureSet);
								index = listOfNames.get(0)+"   P   "+count;
								return index;
							}
							
						}
						count++;
					}
				}
				
				/*Map variantTreeMap = dispatcher.runSync("getProductVariantTree", 
						UtilMisc.toMap("productId",productId, "featureOrder",featureSet, 
								"productStoreId",productStore.getString("productStoreId")));
				Map variantTree = (Map)variantTreeMap.get("variantTree");
				
				Set keySet = variantTree.keySet();
				for(Object key : keySet)
				{
					List prodIds = (List)variantTree.get(key);
					if(prodIds.contains(productID))
					{
						List listOfNames = new ArrayList(featureSet);
						index = listOfNames.get(0)+"   P   "+count;
						return index;
					}
					count++;
				}*/
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(featureSet))
		{
			count = 0;
			List listOfNames = new ArrayList(featureSet);
			index = listOfNames.get(0)+"   P   "+count;
		}
    	return index;
    }
    
    /*
     * @Ajaya
     * For getting the index of the variant products drop down and name
     */
    
    public static String getProductIndexUrl(Delegator delegator, LocalDispatcher dispatcher,  String productID, String productId) {
    	
    	 String productIndexs = getProductIndex(delegator, dispatcher, productID, productId);
    	 String url = "?name=&index=";
    	 if(UtilValidate.isNotEmpty(productIndexs))
    	 {
    		 String []productIndex = productIndexs.split("   P   ");
    		 if(UtilValidate.isNotEmpty(productIndex))
        	 {
    			 if(productIndex.length == 2)
    				 url = "?name=FT"+productIndex[0]+"&index="+productIndex[1];
    			 else if(productIndexs.length() == 1)
    				 url = "?name=FT"+productIndex[0]+"&index="+productIndex[1];
        	 }
    	 }
    	 return url;
    }
    
    
    /*
     * @Ajaya
     * For getting the index of the variant products drop down and name
     */
    public static String getProductIndexUrl(HttpServletRequest request, String productId) {
    	 String productIndexs = getProductIndex(request, productId);
    	 String url = "?name=&index=";
    	 if(UtilValidate.isNotEmpty(productIndexs))
    	 {
    		 String []productIndex = productIndexs.split("   P   ");
    		 if(UtilValidate.isNotEmpty(productIndex))
        	 {
    			 if(productIndex.length == 2)
    				 url = "?name=FT"+productIndex[0]+"&index="+productIndex[1];
    			 else if(productIndexs.length() == 1)
    				 url = "?name=FT"+productIndex[0]+"&index="+productIndex[1];
        	 }
    	 }
    	 return url;
    }
    
    
    ///////////////
    /** Autocomplete Product name. */
    public static Map<String, Object> topBrandProducts(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue productStore = ProductStoreWorker.getProductStore(request);
    	
    	int viewIndex = 0;
        int viewSize = 20;
        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;
        String paging = "Y";
        int previousViewSize = 20;

        HttpSession session = request.getSession();
        ProductSearchOptions productSearchOptions = ProductSearchSession.getProductSearchOptions(session);

       // String addOnTopProdCategoryId = productSearchOptions.getTopProductCategoryId();

        Integer viewIndexInteger = productSearchOptions.getViewIndex();
        if (viewIndexInteger != null) {
            viewIndex = viewIndexInteger.intValue();
        }

        Integer viewSizeInteger = productSearchOptions.getViewSize();
        if (viewSizeInteger != null) {
            viewSize = viewSizeInteger.intValue();
        }

        Integer previousViewSizeInteger = productSearchOptions.getPreviousViewSize();
        if (previousViewSizeInteger != null) {
            previousViewSize = previousViewSizeInteger.intValue();
        }

        String pag = productSearchOptions.getPaging();
        if (paging != null) {
            paging = pag;
        }

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;

        String fieldValueLike = request.getParameter("SEARCH_STRING");   
       
        if(UtilValidate.isNotEmpty(fieldValueLike)){
        if(fieldValueLike.contains("?")){
        	String[] f= fieldValueLike.split("\\?");
        	fieldValueLike = f[0];
         }
        }
      
        String sortBy = request.getParameter("sortBy");
        String excludeOutOfStock = request.getParameter("excludeOutOfStock");
        if(UtilValidate.isEmpty(excludeOutOfStock))
        	excludeOutOfStock = "N";
        
        if(UtilValidate.isEmpty(fieldValueLike))
        	fieldValueLike = (String) session.getAttribute("SEARCH_STRING");
        
        if(UtilValidate.isNotEmpty(fieldValueLike))
        	session.setAttribute("SEARCH_STRING", fieldValueLike);
        
        if(UtilValidate.isEmpty(fieldValueLike))
        	return new HashMap<String, Object>();
        
        
        List productIds = new ArrayList();
 		List orderBy = null;
 		
 		
		
		List<GenericValue> productList = null;
		Map<String,Integer> priceMap = new HashMap<String, Integer>();
		Map<String,Integer> categoryMap = new HashMap<String, Integer>();
		
		List filterKeys = UtilMisc.toList("Less than Rs 20 ","Rs 21 to 50 ","Rs 51 to 100 ","Rs 101 to 200 ","Rs 201 to 500 ","More than Rs 501 ");
		String filterBy = request.getParameter("filterBy");
	   	List filter = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterBy)){
	   		filter = Arrays.asList(filterBy.split(","));
	   	}
	   	
	   	String filterByCategory = request.getParameter("filterByCategory");
	   	List filterCategory = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterByCategory)){
	   		filterCategory = Arrays.asList(filterByCategory.split(","));
	   	}
	   	
         try {
        	 productList = getBrandProducts(fieldValueLike,excludeOutOfStock,null, delegator);
 	    	  
 	    	  Map<String, Object> priceContext = null;
	    	  ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
			  Map<String, Object> priceResult = null;
			  List<GenericValue> newProductList = productList;
			  productList = new ArrayList<GenericValue>();
			  for(GenericValue product : newProductList){
                  if(UtilValidate.isNotEmpty(product))
                  {
                 	  BigDecimal basePrice = product.getBigDecimal("basePrice");
                	  if(UtilValidate.isEmpty(basePrice))basePrice = BigDecimal.ZERO;
                	  if(basePrice.compareTo(new BigDecimal(21)) == -1){
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Less than Rs 20 ")))
                		  count = count+priceMap.get("Less than Rs 20 ");
                		  priceMap.put("Less than Rs 20 ", count);
                		  if(filter.contains("Less than Rs 20 "))
                			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(20)) == 1 && basePrice.compareTo(new BigDecimal(51)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 21 to 50 ")))
                			  count = count+priceMap.get("Rs 21 to 50 ");
                		  
                			  priceMap.put("Rs 21 to 50 ", count);
                			  
                			  if(filter.contains("Rs 21 to 50 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(50)) == 1 && basePrice.compareTo(new BigDecimal(101)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 51 to 100 ")))
                			  count = count+priceMap.get("Rs 51 to 100 ");
                		  
                			  priceMap.put("Rs 51 to 100 ", count);
                			  
                			  if(filter.contains("Rs 51 to 100 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(100)) == 1 && basePrice.compareTo(new BigDecimal(201)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 101 to 200 ")))
                			  count = count+priceMap.get("Rs 101 to 200 ");
                		  
                			  priceMap.put("Rs 101 to 200 ", count);
                			  
                			  if(filter.contains("Rs 101 to 200 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(200)) == 1 && basePrice.compareTo(new BigDecimal(501)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 201 to 500 ")))
                			  count = count+priceMap.get("Rs 201 to 500 ");
                		  
                			  priceMap.put("Rs 201 to 500 ", count);
                			  
                			  if(filter.contains("Rs 201 to 500 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(500)) == 1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("More than Rs 501 ")))
                			  count = count+priceMap.get("More than Rs 501 ");
                		  
                			  priceMap.put("More than Rs 501 ", count);
                			  
                			  if(filter.contains("More than Rs 501 "))
                    			  productList.add(product);
                	  }
                  }
	    	  }
	    	  if(UtilValidate.isEmpty(productList))
	    		  productList = newProductList;
	    	  
	    	  newProductList = productList;
			  productList = new ArrayList<GenericValue>();
			  
			  
			  for(GenericValue product : newProductList){
                  if(UtilValidate.isNotEmpty(product))
                  {
                	  String primaryProductCategoryId = product.getString("primaryProductCategoryId");
                	  int count = 1;
                	  if(UtilValidate.isNotEmpty(primaryProductCategoryId))
                	  {
	            		  if(UtilValidate.isNotEmpty(categoryMap.get(primaryProductCategoryId)))
	            			  count = count+categoryMap.get(primaryProductCategoryId);
	            		  categoryMap.put(primaryProductCategoryId, count);
	            		  
	            		  if(filterCategory.contains(primaryProductCategoryId))
                			  productList.add(product);
                	  }
                  }
			  }
			  
			  if(UtilValidate.isEmpty(productList))
	    		  productList = newProductList;
	    	  
	    	  
	          listSize = productList.size();
	    	  if(listSize > highIndex)
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  else
	    	  {
	    		  highIndex = listSize;
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  }
	          
	    	  if(UtilValidate.isNotEmpty(sortBy))
		  		{
		  			session.setAttribute("sortBy", sortBy);
		  			
		  			if("L_TO_H".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice ASC");
			 		else if("H_TO_L".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice DESC");
			 		else if("A_TO_Z".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName ASC");
			 		else if("Z_TO_A".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName DESC");
			 		else 
			 			orderBy = UtilMisc.toList("sequenceNum DESC");
		  			
	  				productList = EntityUtil.orderBy(productList, orderBy);
	  				
	  				productIds = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		  		}
	    	  else{
	    		  productIds = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
	    	  }
	          } 
		
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	      	  }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("productIds", productIds);
    	result.put("priceMap", priceMap);
    	result.put("categoryMap", categoryMap);
    	result.put("filterKeys", filterKeys);
    	result.put("filter", filter);
    	result.put("excludeOutOfStock", excludeOutOfStock);
    	result.put("filterCategory", filterCategory);
        result.put("viewIndex", Integer.valueOf(viewIndex));
        result.put("viewSize", Integer.valueOf(viewSize));
        result.put("listSize", Integer.valueOf(listSize));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("paging", paging);
        result.put("previousViewSize", previousViewSize);
      return result;
    }
    public static List<GenericValue> getBrandProducts(String brandName, String excludeOutOfStock, List dontSelectProductids, Delegator delegator) {
    	
    	List fromDateCond = new ArrayList();
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
    	List thruDateCond = new ArrayList();
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
    	List cond = new ArrayList();
		cond.add(EntityCondition.makeCondition(
					EntityFunction.UPPER_FIELD("brandName"),EntityOperator.EQUALS,EntityFunction.UPPER(brandName.toUpperCase())));

		cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVariant"), EntityOperator.EQUALS, "N"));
		
		if(UtilValidate.isNotEmpty(dontSelectProductids))
			cond.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, dontSelectProductids));
		
		cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
		cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
		cond.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.NOT_EQUAL,null));
		
		List salesDiscontinuationDateCond = new ArrayList();
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
		
		if("Y".equals(excludeOutOfStock))
			cond.add(EntityCondition.makeCondition("inventoryAtp", EntityOperator.GREATER_THAN_EQUAL_TO, BigDecimal.ONE));
		
		
		Set fieldToSelect = UtilMisc.toSet("productId","internalName","primaryProductCategoryId","basePrice","sequenceNum");
		
		List<GenericValue> productList = null;
	   	
         try {
	    	  productList = delegator.findList("ProductAndCategoryMember", EntityCondition.makeCondition(cond,EntityOperator.AND),fieldToSelect, null, null, false);
         }catch (Exception e) {
			// TODO: handle exception
		 }
         return productList;
    }
    
    /** Autocomplete Product name. */
    public static Map<String, Object> searchProduct(HttpServletRequest request, HttpServletResponse response) {
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue productStore = ProductStoreWorker.getProductStore(request);
    	
    	int viewIndex = 0;
        int viewSize = 24;
        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;
        String paging = "Y";
        int previousViewSize = 24;

        HttpSession session = request.getSession();
        ProductSearchOptions productSearchOptions = ProductSearchSession.getProductSearchOptions(session);

       // String addOnTopProdCategoryId = productSearchOptions.getTopProductCategoryId();

        Integer viewIndexInteger = productSearchOptions.getViewIndex();
        if (viewIndexInteger != null) {
            viewIndex = viewIndexInteger.intValue();
        }

        Integer viewSizeInteger = productSearchOptions.getViewSize();
        if (viewSizeInteger != null) {
            viewSize = viewSizeInteger.intValue();
        }

        Integer previousViewSizeInteger = productSearchOptions.getPreviousViewSize();
        if (previousViewSizeInteger != null) {
            previousViewSize = previousViewSizeInteger.intValue();
        }

        String pag = productSearchOptions.getPaging();
        if (paging != null) {
            paging = pag;
        }

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;
        
        String fieldValue = request.getParameter("SEARCH_STRING");   
       
        if(UtilValidate.isNotEmpty(fieldValue)){
        if(fieldValue.contains("?")){
        	String[] f= fieldValue.split("\\?");
        	fieldValue = f[0];
         }
        }
      
        String sortBy = request.getParameter("sortBy");
        String excludeOutOfStock = request.getParameter("excludeOutOfStock");
        if(UtilValidate.isEmpty(excludeOutOfStock))
        	excludeOutOfStock = "N";

        if(UtilValidate.isEmpty(fieldValue))
        	fieldValue = (String) session.getAttribute("SEARCH_STRING");
        
        if(UtilValidate.isNotEmpty(fieldValue))
        	session.setAttribute("SEARCH_STRING", fieldValue);
        
        if(UtilValidate.isEmpty(fieldValue))
        	return new HashMap<String, Object>();
        
        fieldValue = fieldValue.trim();
        
        List productIds = new ArrayList();

        EntityCondition condition = searchCondition(fieldValue, productStore.getString("showOutOfStockInSearch"), excludeOutOfStock);

		List<GenericValue> searchedProductList = null;
		List<GenericValue> productList2 = null;
        try {
			EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
            searchedProductList = delegator.findList("Product", condition,null, UtilMisc.toList("primaryProductCategoryId"), findOptions, false);
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
		Set fieldToSelect = UtilMisc.toSet("productId","internalName","primaryProductCategoryId","basePrice","sequenceNum","productCategoryId");
		fieldToSelect.add("brandName");
		
		List orderBy = null;
		
		List<GenericValue> productList = null;
		Map<String,Integer> priceMap = new HashMap<String, Integer>();
		Map<String,Integer> categoryMap = new HashMap<String, Integer>();
		
		Map<String,Integer> brandMap =  new HashMap<String,Integer>();
  		List<String> brandList =  new ArrayList<String>();
  		
		
		List filterKeys = UtilMisc.toList("Less than Rs 20 ","Rs 21 to 50 ","Rs 51 to 100 ","Rs 101 to 200 ","Rs 201 to 500 ","More than Rs 501 ");
		String filterBy = request.getParameter("filterBy");
	   	List<String> filter = new ArrayList<String>();
	   	if(UtilValidate.isNotEmpty(filterBy)){
	   		filter = Arrays.asList(filterBy.split(","));
	   	}
	   	
	   	String filterByCategory = request.getParameter("filterByCategory");
	   	List filterCategory = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterByCategory)){
	   		filterCategory = Arrays.asList(filterByCategory.split(","));
	   	}
	   	
	   	String filterByBrand = request.getParameter("filterByBrand");
	   	List filterBrand = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterByBrand)){
	   		filterBrand = Arrays.asList(filterByBrand.split(","));
	   	}
	   	
	   	try {
	   		  List<String> searchedProductIds = EntityUtil.getFieldListFromEntityList(searchedProductList, "productId", true);
	   		  GenericValue parentProduct = null;
	   		  List<String> parentProductIds = new ArrayList<String>();
	   		  if(UtilValidate.isNotEmpty(searchedProductIds))
	   		  for(String searchedProductId : searchedProductIds)
	   		  {
	   			parentProduct = ProductWorker.getParentProduct(searchedProductId, delegator);
	   			if(UtilValidate.isNotEmpty(parentProduct))
	   				searchedProductId = parentProduct.getString("productId");
	   			
	   			if(!parentProductIds.contains(searchedProductId))
	   				parentProductIds.add(searchedProductId);
	   		  }
	   		  
	    	  productList = delegator.findList("ProductAndCategoryMember", EntityCondition.makeCondition("productId",EntityOperator.IN,parentProductIds),fieldToSelect, orderBy, null, false);
 	    	  
	    	  for(GenericValue product : productList){
                if(UtilValidate.isNotEmpty(product))// && UtilValidate.isNotEmpty(priceResult))
                {
	              	  BigDecimal basePrice = product.getBigDecimal("basePrice");
	              	  if(UtilValidate.isEmpty(basePrice))basePrice = BigDecimal.ZERO;
	              	  if(basePrice.compareTo(new BigDecimal(21)) == -1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("Less than Rs 20 ")))
	              			  count = count+priceMap.get("Less than Rs 20 ");
	              		  
	              		  priceMap.put("Less than Rs 20 ", count);
	              	  }
	              	  else if(basePrice.compareTo(new BigDecimal(20)) == 1 && basePrice.compareTo(new BigDecimal(51)) == -1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 21 to 50 ")))
	              			  count = count+priceMap.get("Rs 21 to 50 ");
	              		  
	              			  priceMap.put("Rs 21 to 50 ", count);
	              	  }
	              	  else if(basePrice.compareTo(new BigDecimal(50)) == 1 && basePrice.compareTo(new BigDecimal(101)) == -1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 51 to 100 ")))
	              			  count = count+priceMap.get("Rs 51 to 100 ");
	              		  
	              			  priceMap.put("Rs 51 to 100 ", count);
	              	  }
	              	  else if(basePrice.compareTo(new BigDecimal(100)) == 1 && basePrice.compareTo(new BigDecimal(201)) == -1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 101 to 200 ")))
	              			  count = count+priceMap.get("Rs 101 to 200 ");
	              		  
	              			  priceMap.put("Rs 101 to 200 ", count);
	              	  }
	              	  else if(basePrice.compareTo(new BigDecimal(200)) == 1 && basePrice.compareTo(new BigDecimal(501)) == -1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 201 to 500 ")))
	              			  count = count+priceMap.get("Rs 201 to 500 ");
	              		  
	              			  priceMap.put("Rs 201 to 500 ", count);
	              	  }
	              	  else if(basePrice.compareTo(new BigDecimal(500)) == 1)
	              	  {
	              		  int count = 1;
	              		  if(UtilValidate.isNotEmpty(priceMap.get("More than Rs 501 ")))
	              			  count = count+priceMap.get("More than Rs 501 ");
	              		  
	              			  priceMap.put("More than Rs 501 ", count);
	              	  }
	              	  

                	  String primaryProductCategoryId = product.getString("productCategoryId");
                	  int count = 1;
                	  if(UtilValidate.isNotEmpty(primaryProductCategoryId))
                	  {
	            		  if(UtilValidate.isNotEmpty(categoryMap.get(primaryProductCategoryId)))
	            			  count = count+categoryMap.get(primaryProductCategoryId);
	            		  categoryMap.put(primaryProductCategoryId, count);
                	  }
                	  
  					String brandName= product.getString("brandName");
  					if(UtilValidate.isNotEmpty(brandName)){
  						brandName = brandName.trim();
  						count  = 1;
  						if(brandList.contains(brandName))
  						{
  	            			  count = count+brandMap.get(brandList.indexOf(brandName)+"");
  						}
  						else
  						{
  							brandList.add(brandName);
  						}
  						brandMap.put(brandList.indexOf(brandName)+"", count);
  					}
                }
	    	  }
	    	  
	    	  
	    	  List mainCondList = new ArrayList();
	    	  if(UtilValidate.isNotEmpty(filter))
	    	  {
      			List priceCondList = new ArrayList();
      			
      			for(String filtByPr : filter)
      			{
      				List priceCond = new ArrayList();
      				filtByPr = filtByPr.trim();
      				if(UtilValidate.isNotEmpty(filtByPr))
      				if("Less than Rs 20".equals(filtByPr))
      				{
      					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(20)));
      				}
      				else if("Rs 21 to 50".equals(filtByPr))
      				{
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(21)));
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(50)));
      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
      				}
      				else if("Rs 51 to 100".equals(filtByPr))
      				{
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(51)));
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(100)));
      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
      				}
      				else if("Rs 101 to 200".equals(filtByPr))
      				{
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(101)));
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(200)));
      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
      				}
      				else if("Rs 201 to 500".equals(filtByPr))
      				{
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(201)));
      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(500)));
      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
      				}
      				else if("More than Rs 501".equals(filtByPr))
      				{
      					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(500)));
      				}
      			}
      			mainCondList.add(EntityCondition.makeCondition(priceCondList, EntityOperator.OR));
	    	  }
	    	  
	    	  if(UtilValidate.isNotEmpty(filterBrand))
	    		  mainCondList.add(EntityCondition.makeCondition("brandName",EntityOperator.IN,filterBrand));
	    	  
	    	  if(UtilValidate.isNotEmpty(filterCategory))
	    		  mainCondList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.IN,filterCategory));
	    	  
			  productList = EntityUtil.filterByCondition(productList, EntityCondition.makeCondition(mainCondList,EntityOperator.AND));
			  
			  if(UtilValidate.isNotEmpty(sortBy))
		  		{
		  			session.setAttribute("sortBy", sortBy);
		  			
		  			if("L_TO_H".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice ASC");
			 		else if("H_TO_L".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice DESC");
			 		else if("A_TO_Z".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName ASC");
			 		else if("Z_TO_A".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName DESC");
			 		else 
			 			orderBy = UtilMisc.toList("sequenceNum DESC");
		  			
	  				productList = EntityUtil.orderBy(productList, orderBy);
		  		}
			  
	          /*listSize = productList.size();
	    	  if(listSize > highIndex)
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  else
	    	  {
	    		  highIndex = listSize;
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  }*/
	          } 
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	      	  }


	      productIds = EntityUtil.getFieldListFromEntityList(productList, "productId", true);
		  
          listSize = productIds.size();
    	  if(listSize > highIndex)
    		  productIds = productIds.subList(lowIndex, highIndex);
    	  else
    	  {
    		  highIndex = listSize;
    		  if(lowIndex > highIndex)
    		  {
    			  lowIndex = 0;
    		  	  highIndex = 24;
    		  	  viewIndex = 0;
    		  }
    			  productIds = productIds.subList(lowIndex, highIndex);
    		  
    	  }
    	  
     // ========== populate the result Map
        //Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("productIds", productIds);
        
        /*List<GenericValue> relatedProducts = null;
        String primaryCategoryName = null;
        
        if(UtilValidate.isNotEmpty(productIds) && productIds.size() == 1)
        {
        	relatedProducts = relatedProducts(delegator, (String)productIds.get(0), productStore);
        	try {
				primaryCategoryName = CategoryWorker.
						getProductPrimaryCategoryName(delegator, (String)productIds.get(0), true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        result.put("relatedProductList", relatedProducts);
    	result.put("relatedProductCategoryName", primaryCategoryName);*/
    	result.put("priceMap", priceMap);
    	result.put("categoryMap", categoryMap);
    	
    	result.put("brandMap", brandMap);
    	result.put("brandList", brandList);
    	
    	result.put("filterKeys", filterKeys);
    	result.put("filter", filter);
    	result.put("filterByBrand", filterBrand);
    	
    	result.put("excludeOutOfStock", excludeOutOfStock);
    	result.put("filterCategory", filterCategory);
        result.put("viewIndex", Integer.valueOf(viewIndex));
        result.put("viewSize", Integer.valueOf(viewSize));
        result.put("listSize", Integer.valueOf(listSize));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("paging", paging);
        result.put("previousViewSize", previousViewSize);
      return result;
    }
    
    /*
    //////////////////////////////
 
    /** Autocomplete Product name. */
    public static Map<String, Object> searchProductForBrand(HttpServletRequest request, HttpServletResponse response) {
    	
     	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue productStore = ProductStoreWorker.getProductStore(request);
    	
    	int viewIndex = 0;
        int viewSize = 20;
        int highIndex = 0;
        int lowIndex = 0;
        int listSize = 0;
        String paging = "Y";
        int previousViewSize = 20;

        HttpSession session = request.getSession();
        ProductSearchOptions productSearchOptions = ProductSearchSession.getProductSearchOptions(session);

       // String addOnTopProdCategoryId = productSearchOptions.getTopProductCategoryId();

        Integer viewIndexInteger = productSearchOptions.getViewIndex();
        if (viewIndexInteger != null) {
            viewIndex = viewIndexInteger.intValue();
        }

        Integer viewSizeInteger = productSearchOptions.getViewSize();
        if (viewSizeInteger != null) {
            viewSize = viewSizeInteger.intValue();
        }

        Integer previousViewSizeInteger = productSearchOptions.getPreviousViewSize();
        if (previousViewSizeInteger != null) {
            previousViewSize = previousViewSizeInteger.intValue();
        }

        String pag = productSearchOptions.getPaging();
        if (paging != null) {
            paging = pag;
        }

        lowIndex = viewIndex * viewSize;
        highIndex = (viewIndex + 1) * viewSize;

        String fieldValue = request.getParameter("SEARCH_STRING_BRAND");
        
         String sortBy = request.getParameter("sortBy");
        
        if(UtilValidate.isEmpty(fieldValue))
        	fieldValue = (String) session.getAttribute("SEARCH_STRING");
        
        if(UtilValidate.isNotEmpty(fieldValue))
        	session.setAttribute("SEARCH_STRING", fieldValue);
        
        if(UtilValidate.isEmpty(fieldValue))
        	return new HashMap<String, Object>();
        
        List productIds = new ArrayList();
        
		List cond = new ArrayList();
		cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)fieldValue).toUpperCase())));
//		cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.EQUALS, EntityFunction.UPPER(((String)fieldValue).toUpperCase())));
		cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVirtual"), EntityOperator.EQUALS, "N"));
		cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVariant"), EntityOperator.NOT_EQUAL, null));
		
		/*List condition1 = new ArrayList();
		condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "Y"));
		condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, null));
		
		List condition = new ArrayList();
		condition.add(EntityCondition.makeCondition(cond,EntityOperator.OR));
		condition.add(EntityCondition.makeCondition(condition1,EntityOperator.OR));
		*/
		Set fieldToSelect = UtilMisc.toSet("productId","internalName","primaryProductCategoryId");
		List orderBy = null;
		
		List<GenericValue> productList = null;
		Map<String,Integer> priceMap = new HashMap<String, Integer>();
		Map<String,Integer> categoryMap = new HashMap<String, Integer>();
		
		List filterKeys = UtilMisc.toList("Less than Rs 20 ","Rs 21 to 50 ","Rs 51 to 100 ","Rs 101 to 200 ","Rs 201 to 500 ","More than Rs 501 ");
		String filterBy = request.getParameter("filterBy");
	   	List filter = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterBy)){
	   		filter = Arrays.asList(filterBy.split(","));
	   	}
	   	String filterByCategory = request.getParameter("filterByCategory");
	   	List filterCategory = new ArrayList();
	   	if(UtilValidate.isNotEmpty(filterByCategory)){
	   		filterCategory = Arrays.asList(filterByCategory.split(","));
	   	}
        try {
	    	  productList = delegator.findList("Product", EntityCondition.makeCondition(cond,EntityOperator.AND),fieldToSelect, orderBy, null, false);
	    	  if(UtilValidate.isEmpty(productList))
	    	  {
	    			String fieldValue1 = "%" +fieldValue +"%";
	    			cond = new ArrayList();
	    			cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"), EntityOperator.LIKE, EntityFunction.UPPER(((String)fieldValue1).toUpperCase())));
//	    			cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER(((String)fieldValue1).toUpperCase())));
	    			cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVirtual"), EntityOperator.EQUALS, "N"));
	    			cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVariant"), EntityOperator.NOT_EQUAL, null));
	    			
	    			/*condition1 = new ArrayList();
	    			condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "Y"));
	    			condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, null));
	    			
	    			condition = new ArrayList();
	    			condition.add(EntityCondition.makeCondition(cond,EntityOperator.OR));
	    			condition.add(EntityCondition.makeCondition(condition1,EntityOperator.OR));
	    			*/
	    		    productList = delegator.findList("Product", EntityCondition.makeCondition(cond,EntityOperator.AND),fieldToSelect, orderBy, null, false);
	    	  }
	    	  if(UtilValidate.isEmpty(productList))
	    	  {
	    		  String []searchStrings = fieldValue.split(" ");
	    		  cond = new ArrayList();
	    		  if(UtilValidate.isNotEmpty(searchStrings))
	    		  for(String searchString : searchStrings){
	    			  if(UtilValidate.isNotEmpty(searchString))
	    			  {
	    				  searchString = "%" +searchString +"%";
		    			  cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"), EntityOperator.LIKE, EntityFunction.UPPER(((String)searchString).toUpperCase())));
//		    			  cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER(((String)searchString).toUpperCase())));
	    			  }
	    		  }
	    		    /*condition1 = new ArrayList();
	    			condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, "Y"));
	    			condition1.add(EntityCondition.makeCondition("isVariant", EntityOperator.EQUALS, null));
	    			
	    			condition = new ArrayList();
	    			condition.add(EntityCondition.makeCondition(cond,EntityOperator.OR));
	    			condition.add(EntityCondition.makeCondition(condition1,EntityOperator.OR));
	    			*/
	    		  cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVirtual"), EntityOperator.EQUALS, "N"));
    			  cond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("isVariant"), EntityOperator.NOT_EQUAL, null));
    				
	    		  productList = delegator.findList("Product", EntityCondition.makeCondition(cond,EntityOperator.AND),fieldToSelect, orderBy, null, false);
	    	  }
	    	  
	    	  Map<String, Object> priceContext = null;
	    	  ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
			  Map<String, Object> priceResult = null;
			  List<GenericValue> newProductList = productList;
			  productList = new ArrayList<GenericValue>();
			  for(GenericValue product : newProductList){
	    		    //priceContext = FastMap.newInstance();
	    		    priceContext = new HashMap();
	    		    
					priceContext.put("currencyUomId", cart.getCurrency());
					priceContext.put("product", product);
					
					priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
                  if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(priceResult))
                  {
                	  
                	  
                	  BigDecimal basePrice = (BigDecimal)priceResult.get("basePrice");
                	  
                	  if(basePrice.compareTo(new BigDecimal(21)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Less than Rs 20 ")))
                			  count = count+priceMap.get("Less than Rs 20 ");
                		  
                		  priceMap.put("Less than Rs 20 ", count);
                		  
                		  
                		  if(filter.contains("Less than Rs 20 "))
                			  productList.add(product);
                			  
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(20)) == 1 && basePrice.compareTo(new BigDecimal(51)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 21 to 50 ")))
                			  count = count+priceMap.get("Rs 21 to 50 ");
                		  
                			  priceMap.put("Rs 21 to 50 ", count);
                			  
                			  if(filter.contains("Rs 21 to 50 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(50)) == 1 && basePrice.compareTo(new BigDecimal(101)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 51 to 100 ")))
                			  count = count+priceMap.get("Rs 51 to 100 ");
                		  
                			  priceMap.put("Rs 51 to 100 ", count);
                			  
                			  if(filter.contains("Rs 51 to 100 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(100)) == 1 && basePrice.compareTo(new BigDecimal(201)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 101 to 200 ")))
                			  count = count+priceMap.get("Rs 101 to 200 ");
                		  
                			  priceMap.put("Rs 101 to 200 ", count);
                			  
                			  if(filter.contains("Rs 101 to 200 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(200)) == 1 && basePrice.compareTo(new BigDecimal(501)) == -1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 201 to 500 ")))
                			  count = count+priceMap.get("Rs 201 to 500 ");
                		  
                			  priceMap.put("Rs 201 to 500 ", count);
                			  
                			  if(filter.contains("Rs 201 to 500 "))
                    			  productList.add(product);
                	  }
                	  else if(basePrice.compareTo(new BigDecimal(500)) == 1)
                	  {
                		  int count = 1;
                		  if(UtilValidate.isNotEmpty(priceMap.get("More than Rs 501 ")))
                			  count = count+priceMap.get("More than Rs 501 ");
                		  
                			  priceMap.put("More than Rs 501 ", count);
                			  
                			  if(filter.contains("More than Rs 501 "))
                    			  productList.add(product);
                	  }
                	  	
                 	 // //System.out.println("    1     "+basePrice.compareTo(BigDecimal.ZERO));1
                 	 // //System.out.println("    2     "+basePrice.compareTo(BigDecimal.ONE));0
                 	  ////System.out.println("    3     "+basePrice.compareTo(BigDecimal.TEN));-1
                  }
	    	  }
	    	  if(UtilValidate.isEmpty(productList))
	    		  productList = newProductList;
	    	  
	    	  newProductList = productList;
			  productList = new ArrayList<GenericValue>();
			  for(GenericValue product : newProductList){
                  if(UtilValidate.isNotEmpty(product))
                  {
                	  String primaryProductCategoryId = product.getString("primaryProductCategoryId");
                	  int count = 1;
                	  if(UtilValidate.isNotEmpty(primaryProductCategoryId))
                	  {
	            		  if(UtilValidate.isNotEmpty(categoryMap.get(primaryProductCategoryId)))
	            			  count = count+categoryMap.get(primaryProductCategoryId);
	            		  categoryMap.put(primaryProductCategoryId, count);
	            		  
	            		  if(filterCategory.contains(primaryProductCategoryId))
                			  productList.add(product);
                	  }
                  }
			  }
			  if(UtilValidate.isEmpty(productList))
	    		  productList = newProductList;
	    	  
	    	  
	          listSize = productList.size();
	    	  if(listSize > highIndex)
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  else
	    	  {
	    		  highIndex = listSize;
	    		  productList = productList.subList(lowIndex, highIndex);
	    	  }
	          
	    	  if(UtilValidate.isNotEmpty(sortBy))
		  		{
		  			session.setAttribute("sortBy", sortBy);
		  			if("L_TO_H".equals(sortBy) || "H_TO_L".equals(sortBy))
		  			{
		  				Map<String, BigDecimal> productPriceMap = new HashMap<String, BigDecimal>();
		  				for(GenericValue product : productList)
		  				{
		  					//priceContext = FastMap.newInstance();
		  					priceContext = new HashMap();
		  					
		  					priceContext.put("currencyUomId", cart.getCurrency());
		  					priceContext.put("product", product);
		  					
		  					priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
		                    if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(priceResult))
		                    	productPriceMap.put(product.getString("productId"), (BigDecimal)priceResult.get("basePrice"));
		  				}
		  				
		  		        ValueComparator bvc =  new ValueComparator(productPriceMap);
		  		        TreeMap<String,BigDecimal> sorted_map = new TreeMap<String,BigDecimal>(bvc);

		  		        sorted_map.putAll(productPriceMap);
		  		        Set keys = sorted_map.keySet();
		  		        productIds = new ArrayList(keys);
		  		        if("H_TO_L".equals(sortBy))
		  		        	Collections.reverse(productIds);
		  			}
		  			else if("A_TO_Z".equals(sortBy) || "Z_TO_A".equals(sortBy))
		  			{
		  				if("A_TO_Z".equals(sortBy))
		  					orderBy = UtilMisc.toList("internalName ASC");
		  				else
		  					orderBy = UtilMisc.toList("internalName DESC");
		  				productList = EntityUtil.orderBy(productList, orderBy);
		  				
		  				for(GenericValue product : productList){
				    	    productIds.add(product.getString("productId"));
				        }
		  			}
		  			else if("POPULAR_PRD".equals(sortBy))
		  				{
		  				for(GenericValue product : productList){
				    	    productIds.add(product.getString("productId"));
				        }
		  				List exprList = new ArrayList();
		  				exprList.add(EntityCondition.makeCondition("totalQuantityOrdered", EntityOperator.GREATER_THAN_EQUAL_TO, 5.0));
		  				exprList.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));

		  				productList = delegator.findList("ProductCalculatedInfo", 
		  						  EntityCondition.makeCondition(exprList, EntityOperator.AND), null, 
		  						  UtilMisc.toList("-totalQuantityOrdered"), null, false);
		  				for(GenericValue product : productList){
				    	    productIds.add(product.getString("productId"));
				        }
		  				}
		  		}
	    	  else{
	    		  for(GenericValue product : productList){
			    	    productIds.add(product.getString("productId"));
			        }
	    	  }
	          } 
		
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	      	  }
        
     // ========== populate the result Map
       // Map<String, Object> result = FastMap.newInstance();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("productIds", productIds);
        
        /*List<GenericValue> relatedProducts = null;
        String primaryCategoryName = null;
        
        if(UtilValidate.isNotEmpty(productIds) && productIds.size() == 1)
        {
        	relatedProducts = relatedProducts(delegator, (String)productIds.get(0), productStore);
        	try {
				primaryCategoryName = CategoryWorker.
						getProductPrimaryCategoryName(delegator, (String)productIds.get(0), true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        result.put("relatedProductList", relatedProducts);
    	result.put("relatedProductCategoryName", primaryCategoryName);*/
    	result.put("priceMap", priceMap);
    	result.put("categoryMap", categoryMap);
    	result.put("filterKeys", filterKeys);
    	result.put("filter", filter);
    	result.put("filterCategory", filterCategory);
        result.put("viewIndex", Integer.valueOf(viewIndex));
        result.put("viewSize", Integer.valueOf(viewSize));
        result.put("listSize", Integer.valueOf(listSize));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("paging", paging);
        result.put("previousViewSize", previousViewSize);
      return result;
    }
    
    /*
     * Related products of searched product
     */
    
    public static List<GenericValue> relatedProducts(Delegator delegator, String productId, GenericValue productStore){
    	String  primaryProductCategoryId = null;
    	try {
    		primaryProductCategoryId = CategoryWorker.getProductPrimaryCategoryId(delegator, productId, true);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	if(UtilValidate.isEmpty(primaryProductCategoryId)) return null;
    	
    	
        List condition = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL,productId),
        		EntityCondition.makeCondition("primaryProductCategoryId",EntityOperator.EQUALS,primaryProductCategoryId));
    	
    	
    	List<GenericValue> productList = null;
    	try {
    		productList = delegator.findList("Product", 
					EntityCondition.makeCondition(condition,EntityOperator.AND),
					UtilMisc.toSet("productId"), null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(UtilValidate.isNotEmpty(productList))
    	{
    		double relatedProductsDisplaySize = 10;
    		
    		if(UtilValidate.isNotEmpty(productStore.getDouble("relatedProductsDisplaySize")))
    			relatedProductsDisplaySize = productStore.getDouble("relatedProductsDisplaySize");
    		
    		productList = productList.subList(0, (int)relatedProductsDisplaySize);
    	}
    	
    	return productList;
    }
    
    
    /*public static String autoProductNames(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String helperName = delegator.getGroupHelperName("org.ofbiz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List li = new ArrayList();
        String result[] = null;
        Connection conn = null;
		Statement statement = null;
		String removeSelectedFlag = request.getParameter("sport");
		List exprs=  new ArrayList();
		List conditionList2 = new ArrayList();
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		if(UtilValidate.isNotEmpty(removeSelectedFlag))
		{
			exprs.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE,"%"+removeSelectedFlag.toLowerCase()+"%"));
			exprs.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE,"%"+removeSelectedFlag.toUpperCase()+"%"));
			exprs.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE,"%"+removeSelectedFlag.toLowerCase()+"%"));
			exprs.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE,"%"+removeSelectedFlag.toUpperCase()+"%"));
			exprs.add(EntityCondition.makeCondition("internalName", EntityOperator.LIKE,"%"+removeSelectedFlag.toLowerCase()+"%"));
			exprs.add(EntityCondition.makeCondition("internalName", EntityOperator.LIKE,"%"+removeSelectedFlag.toUpperCase()+"%"));
		}
        try {
        	EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.OR);
        	//System.out.println("\n\n############################conditioncondition  --- "+condition+"\n\n");
        	List<GenericValue> transList1 = delegator.findList("Product", condition, UtilMisc.toSet("productName"), UtilMisc.toList("productName"), findOptions, false);
			//System.out.println("\n\n############################  --- "+transList1+"\n\n");
        	PrintWriter out = response.getWriter();
			String s[]=null;
	    	  if(transList1!=null && !transList1.equals(""))
	    	  {
		    	   Iterator resu = transList1.iterator();
		    	  while(resu.hasNext())
		          {
		    		  GenericValue orderHeader = (GenericValue) resu.next();
		              li.add(orderHeader.getString("productName"));
		          }
	    	  }
	    	  String[] str = new String[li.size()];
	    	  //System.out.println("\n\n############################ strstr --- "+li.size()+"\n\n");
		       Iterator it = li.iterator();
		 
		       int i = 0;
		       while(it.hasNext())
		       {
		           String p = (String)it.next();
		           str[i] = p;
		           i++;
		       }
		 
		    //jQuery related start
		       String querys = (String)request.getParameter("q");
		 
		       int cnt=1;
		       for(int j=0;j<str.length;j++)
		       {
		              out.print(str[j]+",");
		              if(cnt>=200)// How many results have to show while we are typing(auto suggestions)
		              break;
		              cnt++;
		       }
	          } 
		
		
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	          }finally{
					try {
						if(statement != null) statement.close();
						if(conn != null) conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

        //Determine where to send the browser
            return "success";
    }
    */
    
    
   
    public static String locationsearchAction(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String helperName = delegator.getGroupHelperName("org.ofbiz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String location=request.getParameter("locationSearch");
        List<GenericValue> locationList=delegator.findList("ZoneType", EntityCondition.makeCondition("zoneName",EntityOperator.EQUALS,location), null, null, null, false);
        if(locationList.size()>0)
        	request.setAttribute("flag","true");
        else
        	request.setAttribute("flag","false");
        
        return "success";
        
    }
    
    
    /** Autocomplete Brand name. */
    public static String autoBrandsName(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String helperName = delegator.getGroupHelperName("org.ofbiz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        List li = new ArrayList();
        String result[] = null;
        Connection conn = null;
		Statement statement = null;
		String removeSelectedFlag = request.getParameter("brand");
        String query="SELECT DISTINCT brand_name FROM product WHERE LOWER(brand_name) LIKE LOWER('%"+removeSelectedFlag.toLowerCase()+"%')";
		try {
			PrintWriter out = response.getWriter();
			String s[]=null;
	    	  if(removeSelectedFlag!=null && !removeSelectedFlag.equals(""))
	    	  {
		    	   conn = ConnectionFactory.getConnection(helperName);
		    	   PreparedStatement ps=conn.prepareStatement(query);
		    	    ResultSet rs=ps.executeQuery();
		    	  while(rs.next())
		          {
		              li.add(rs.getString("brand_name"));
		          }
	    	  }
	    	  String[] str = new String[li.size()];
		       Iterator it = li.iterator();
		 
		       int i = 0;
		       while(it.hasNext())
		       {
		           String p = (String)it.next();
		           str[i] = p;
		           i++;
		       }
		 
		    //jQuery related start
		       String querys = (String)request.getParameter("q");
		 
		       int cnt=1;
		       for(int j=0;j<str.length;j++)
		       {
		              out.print(str[j]+",");
		              if(cnt>=200)// How many results have to show while we are typing(auto suggestions)
		              break;
		              cnt++;
		       }
	          } 
		
		
	      catch (Exception ex) 
	      	  {
	               ex.printStackTrace();
	          }finally{
					try {
						if(statement != null) statement.close();
						if(conn != null) conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

        //Determine where to send the browser
            return "success";
    }

    /** Empty the shopping cart. */
    public static String clearCart(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        cart.clear();

        // if this was an anonymous checkout process, go ahead and clear the session and such now that the order is placed; we don't want this to mess up additional orders and such
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        if (userLogin != null && "anonymous".equals(userLogin.get("userLoginId"))) {
            // here we want to do a full logout, but not using the normal logout stuff because it saves things in the UserLogin record that we don't want changed for the anonymous user
            session.invalidate();
            session = request.getSession(true);

            // to allow the display of the order confirmation page put the userLogin in the request, but leave it out of the session
            request.setAttribute("temporaryAnonymousUserLogin", userLogin);

            Debug.logInfo("Doing clearCart for anonymous user, so logging out but put anonymous userLogin in temporaryAnonymousUserLogin request attribute", module);
        }
        return "success";
    }

    /** Totally wipe out the cart, removes all stored info. */
    public static String destroyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        clearCart(request, response);
        session.removeAttribute("shoppingCart");
        session.removeAttribute("orderPartyId");
        session.removeAttribute("orderMode");
        session.removeAttribute("productStoreId");
        session.removeAttribute("CURRENT_CATALOG_ID");
        return "success";
    }

    /** Gets or creates the shopping cart object */
    public static ShoppingCart getCartObject(HttpServletRequest request, Locale locale, String currencyUom) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = (ShoppingCart) request.getAttribute("shoppingCart");
        HttpSession session = request.getSession(true);
        if (cart == null) {
            cart = (ShoppingCart) session.getAttribute("shoppingCart");
        } else {
            session.setAttribute("shoppingCart", cart);
        }

        if (cart == null) {
            cart = new WebShoppingCart(request, locale, currencyUom);
            session.setAttribute("shoppingCart", cart);
        } else {
            if (locale != null && !locale.equals(cart.getLocale())) {
                cart.setLocale(locale);
            }
            if (currencyUom != null && !currencyUom.equals(cart.getCurrency())) {
                try {
                    cart.setCurrency(dispatcher, currencyUom);
                } catch (CartItemModifyException e) {
                    Debug.logError(e, "Unable to modify currency in cart", module);
                }
            }
        }
        return cart;
    }

    /** Main get cart method; uses the locale & currency from the session */
    public static ShoppingCart getCartObject(HttpServletRequest request) {
        return getCartObject(request, null, null);
    }

    public static String switchCurrentCartObject(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String cartIndexStr = request.getParameter("cartIndex");
        int cartIndex = -1;
        if (UtilValidate.isNotEmpty(cartIndexStr) && UtilValidate.isInteger(cartIndexStr)) {
            try {
                cartIndex = Integer.parseInt(cartIndexStr);
            } catch (NumberFormatException nfe) {
                Debug.logWarning("Invalid value for cart index =" + cartIndexStr, module);
            }
        }
        List<ShoppingCart> cartList = UtilGenerics.checkList(session.getAttribute("shoppingCartList"));
        if (UtilValidate.isEmpty(cartList)) {
          //  cartList = FastList.newInstance();
            cartList = new ArrayList<ShoppingCart>();
            session.setAttribute("shoppingCartList", cartList);
        }
        ShoppingCart currentCart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (currentCart != null) {
            cartList.add(currentCart);
            session.setAttribute("shoppingCartList", cartList);
            session.removeAttribute("shoppingCart");
            //destroyCart(request, response);
        }
        ShoppingCart newCart = null;
        if (cartIndex >= 0 && cartIndex < cartList.size()) {
            newCart = cartList.remove(cartIndex);
        } else {
            String productStoreId = request.getParameter("productStoreId");
            if (UtilValidate.isNotEmpty(productStoreId)) {
                session.setAttribute("productStoreId", productStoreId);
            }
            newCart = getCartObject(request);
        }
        session.setAttribute("shoppingCart", newCart);
        return "success";
    }

    public static String clearCartFromList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String cartIndexStr = request.getParameter("cartIndex");
        int cartIndex = -1;
        if (UtilValidate.isNotEmpty(cartIndexStr) && UtilValidate.isInteger(cartIndexStr)) {
            try {
                cartIndex = Integer.parseInt(cartIndexStr);
            } catch (NumberFormatException nfe) {
                Debug.logWarning("Invalid value for cart index =" + cartIndexStr, module);
            }
        }
        List<ShoppingCart> cartList = UtilGenerics.checkList(session.getAttribute("shoppingCartList"));
        if (UtilValidate.isNotEmpty(cartList) && cartIndex >= 0 && cartIndex < cartList.size()) {
            cartList.remove(cartIndex);
        }
        return "success";
    }

    /** Update the cart's UserLogin object if it isn't already set. */
    public static String keepCartUpdated(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        ShoppingCart cart = getCartObject(request);

        // if we just logged in set the UL
        if (cart.getUserLogin() == null) {
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            if (userLogin != null) {
                try {
                    cart.setUserLogin(userLogin, dispatcher);
                } catch (CartItemModifyException e) {
                    Debug.logWarning(e, module);
                }
            }
        }

        // same for autoUserLogin
        if (cart.getAutoUserLogin() == null) {
            GenericValue autoUserLogin = (GenericValue) session.getAttribute("autoUserLogin");
            if (autoUserLogin != null) {
                if (cart.getUserLogin() == null) {
                    try {
                        cart.setAutoUserLogin(autoUserLogin, dispatcher);
                    } catch (CartItemModifyException e) {
                        Debug.logWarning(e, module);
                    }
                } else {
                    cart.setAutoUserLogin(autoUserLogin);
                }
            }
        }

        // update the locale
        Locale locale = UtilHttp.getLocale(request);
        if (cart.getLocale() == null || !locale.equals(cart.getLocale())) {
            cart.setLocale(locale);
        }

        return "success";
    }

    /** For GWP Promotions with multiple alternatives, selects an alternative to the current GWP */
    public static String setDesiredAlternateGwpProductId(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String alternateGwpProductId = request.getParameter("alternateGwpProductId");
        String alternateGwpLineStr = request.getParameter("alternateGwpLine");
        Locale locale = UtilHttp.getLocale(request);

        if (UtilValidate.isEmpty(alternateGwpProductId)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpProductIdPassed", locale));
            return "error";
        }
        if (UtilValidate.isEmpty(alternateGwpLineStr)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftNoAlternateGwpLinePassed", locale));
            return "error";
        }

        int alternateGwpLine = 0;
        try {
            alternateGwpLine = Integer.parseInt(alternateGwpLineStr);
        } catch (Exception e) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotSelectAlternateGiftAlternateGwpLineIsNotAValidNumber", locale));
            return "error";
        }

        ShoppingCartItem cartLine = cart.findCartItem(alternateGwpLine);
        if (cartLine == null) {
            request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, no cart line item found for #" + alternateGwpLine + ".");
            return "error";
        }

        if (cartLine.getIsPromo()) {
            // note that there should just be one promo adjustment, the reversal of the GWP, so use that to get the promo action key
            Iterator<GenericValue> checkOrderAdjustments = UtilMisc.toIterator(cartLine.getAdjustments());
            while (checkOrderAdjustments != null && checkOrderAdjustments.hasNext()) {
                GenericValue checkOrderAdjustment = checkOrderAdjustments.next();
                if (UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoRuleId")) &&
                        UtilValidate.isNotEmpty(checkOrderAdjustment.getString("productPromoActionSeqId"))) {
                    GenericPK productPromoActionPk = delegator.makeValidValue("ProductPromoAction", checkOrderAdjustment).getPrimaryKey();
                    cart.setDesiredAlternateGiftByAction(productPromoActionPk, alternateGwpProductId);
                    if (cart.getOrderType().equals("SALES_ORDER")) {
                        org.ofbiz.order.shoppingcart.product.ProductPromoWorker.doPromotions(cart, dispatcher);
                    }
                    return "success";
                }
            }
        }

        request.setAttribute("_ERROR_MESSAGE_", "Could not select alternate gift, cart line item found for #" + alternateGwpLine + " does not appear to be a valid promotional gift.");
        return "error";
    }

    /** Associates a party to order */
    public static String addAdditionalParty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String partyId = request.getParameter("additionalPartyId");
        String roleTypeId[] = request.getParameterValues("additionalRoleTypeId");
        List<String> eventList = new LinkedList<String>();
        Locale locale = UtilHttp.getLocale(request);
        int i;

        if (UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(roleTypeId) || roleTypeId.length < 1) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPartyIdAndOrRoleTypeIdNotDefined", locale));
            return "error";
        }

        if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
            List<String> msg = UtilGenerics.checkList(request.getAttribute("_EVENT_MESSAGE_LIST_"));
            eventList.addAll(msg);
        }

        for (i = 0; i < roleTypeId.length; i++) {
            try {
                cart.addAdditionalPartyRole(partyId, roleTypeId[i]);
            } catch (Exception e) {
                eventList.add(e.getLocalizedMessage());
            }
        }

        request.removeAttribute("_EVENT_MESSAGE_LIST_");
        request.setAttribute("_EVENT_MESSAGE_LIST_", eventList);
        return "success";
    }

    /** Removes a previously associated party to order */
    public static String removeAdditionalParty(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String partyId = request.getParameter("additionalPartyId");
        String roleTypeId[] = request.getParameterValues("additionalRoleTypeId");
        List<String> eventList = new LinkedList<String>();
        Locale locale = UtilHttp.getLocale(request);
        int i;

        if (UtilValidate.isEmpty(partyId) || roleTypeId.length < 1) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPartyIdAndOrRoleTypeIdNotDefined", locale));
            return "error";
        }

        if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
            List<String> msg = UtilGenerics.checkList(request.getAttribute("_EVENT_MESSAGE_LIST_"));
            eventList.addAll(msg);
        }

        for (i = 0; i < roleTypeId.length; i++) {
            try {
                cart.removeAdditionalPartyRole(partyId, roleTypeId[i]);
            } catch (Exception e) {
                Debug.logInfo(e.getLocalizedMessage(), module);
                eventList.add(e.getLocalizedMessage());
            }
        }

        request.removeAttribute("_EVENT_MESSAGE_LIST_");
        request.setAttribute("_EVENT_MESSAGE_LIST_", eventList);
        return "success";
    }

    /**
     * This should be called to translate the error messages of the
     * <code>ShoppingCartHelper</code> to an appropriately formatted
     * <code>String</code> in the request object and indicate whether
     * the result was an error or not and whether the errors were
     * critical or not
     *
     * @param result    The result returned from the
     * <code>ShoppingCartHelper</code>
     * @param request The servlet request instance to set the error messages
     * in
     * @return one of NON_CRITICAL_ERROR, ERROR or NO_ERROR.
     */
    private static String processResult(Map<String, Object> result, HttpServletRequest request) {
        //Check for errors
        StringBuilder errMsg = new StringBuilder();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List<String> errorMsgs = UtilGenerics.checkList(result.get(ModelService.ERROR_MESSAGE_LIST));
            Iterator<String> iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
        }

        //See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }
        } else {
            return NO_ERROR;
        }
    }

    /** Assign agreement **/
    public static String selectAgreement(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String agreementId = request.getParameter("agreementId");
        Map<String, Object> result = cartHelper.selectAgreement(agreementId);
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        return "success";
    }

    /** Assign currency **/
    public static String setCurrency(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String currencyUomId = request.getParameter("currencyUomId");
        Map<String, Object> result = cartHelper.setCurrency(currencyUomId);
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        return "success";
    }

    /**
     * set the order name of the cart based on request.  right now will always return "success"
     *
     */
    public static String setOrderName(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String orderName = request.getParameter("orderName");
        cart.setOrderName(orderName);
        return "success";
    }

    /**
     * set the PO number of the cart based on request.  right now will always return "success"
     *
     */
    public static String setPoNumber(HttpServletRequest request, HttpServletResponse response) {
        ShoppingCart cart = getCartObject(request);
        String correspondingPoId = request.getParameter("correspondingPoId");
        cart.setPoNumber(correspondingPoId);
        return "success";
    }

    /**
     * Add an order term *
     */
    public static String addOrderTerm(HttpServletRequest request, HttpServletResponse response) {

        ShoppingCart cart = getCartObject(request);
        Locale locale = UtilHttp.getLocale(request);

        String termTypeId = request.getParameter("termTypeId");
        String termValueStr = request.getParameter("termValue");
        String termDaysStr = request.getParameter("termDays");
        String textValue = request.getParameter("textValue");

        GenericValue termType = null;
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        BigDecimal termValue = null;
        Long termDays = null;

        if (UtilValidate.isEmpty(termTypeId)) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderOrderTermTypeIsRequired", locale));
            return "error";
        }

        try {
            termType = delegator.findOne("TermType", UtilMisc.toMap("termTypeId", termTypeId), false);
        } catch (GenericEntityException gee) {
            request.setAttribute("_ERROR_MESSAGE_", gee.getMessage());
            return "error";
        }

        if (("FIN_PAYMENT_TERM".equals(termTypeId) && UtilValidate.isEmpty(termDaysStr)) || (UtilValidate.isNotEmpty(termType) && "FIN_PAYMENT_TERM".equals(termType.get("parentTypeId")) && UtilValidate.isEmpty(termDaysStr))) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderOrderTermDaysIsRequired", locale));
            return "error";
        }

        if (UtilValidate.isNotEmpty(termValueStr)) {
            try {
                termValue = new BigDecimal(termValueStr);
            } catch (NumberFormatException e) {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderOrderTermValueError", UtilMisc.toMap("orderTermValue", termValueStr), locale));
                return "error";
            }
        }

        if (UtilValidate.isNotEmpty(termDaysStr)) {
            try {
                termDays = Long.valueOf(termDaysStr);
            } catch (NumberFormatException e) {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "OrderOrderTermDaysError", UtilMisc.toMap("orderTermDays", termDaysStr), locale));
                return "error";
            }
        }

        removeOrderTerm(request, response);

        cart.addOrderTerm(termTypeId, termValue, termDays, textValue);

        return "success";
    }

    /**
     * Remove an order term *
     */
    public static String removeOrderTerm(HttpServletRequest request, HttpServletResponse response) {

        ShoppingCart cart = getCartObject(request);

        String termIndexStr = request.getParameter("termIndex");
        if (UtilValidate.isNotEmpty(termIndexStr)) {
            try {
                Integer termIndex = Integer.parseInt(termIndexStr);
                if (termIndex >= 0) {
                    List<GenericValue> orderTerms = cart.getOrderTerms();
                    if (orderTerms != null && orderTerms.size() > termIndex) {
                        cart.removeOrderTerm(termIndex);
                    }
                }
            } catch (NumberFormatException e) {
                Debug.logWarning(e, "Error parsing termIndex: " + termIndexStr, module);
            }
        }

        return "success";
    }

    /** Initialize order entry from a shopping list **/
    public static String loadCartFromShoppingList(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");

        String shoppingListId = request.getParameter("shoppingListId");

        ShoppingCart cart = null;
        try {
            Map<String, Object> outMap = dispatcher.runSync("loadCartFromShoppingList",
                    UtilMisc.<String, Object>toMap("shoppingListId", shoppingListId,
                    "userLogin", userLogin));
            cart = (ShoppingCart)outMap.get("shoppingCart");
        } catch (GenericServiceException exc) {
            request.setAttribute("_ERROR_MESSAGE_", exc.getMessage());
            return "error";
        }

        session.setAttribute("shoppingCart", cart);
        session.setAttribute("productStoreId", cart.getProductStoreId());
        session.setAttribute("orderMode", cart.getOrderType());
        session.setAttribute("orderPartyId", cart.getOrderPartyId());

        return "success";
    }

    /** Initialize order entry from a quote **/
    public static String loadCartFromQuote(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");

        String quoteId = request.getParameter("quoteId");

        ShoppingCart cart = null;
        try {
            Map<String, Object> outMap = dispatcher.runSync("loadCartFromQuote",
                    UtilMisc.<String, Object>toMap("quoteId", quoteId,
                            "applyQuoteAdjustments", "true",
                            "userLogin", userLogin));
            cart = (ShoppingCart) outMap.get("shoppingCart");
        } catch (GenericServiceException exc) {
            request.setAttribute("_ERROR_MESSAGE_", exc.getMessage());
            return "error";
        }

        // Set the cart's default checkout options for a quick checkout
        cart.setDefaultCheckoutOptions(dispatcher);
        // Make the cart read-only
        cart.setReadOnlyCart(true);

        session.setAttribute("shoppingCart", cart);
        session.setAttribute("productStoreId", cart.getProductStoreId());
        session.setAttribute("orderMode", cart.getOrderType());
        session.setAttribute("orderPartyId", cart.getOrderPartyId());

        return "success";
    }

    /** Initialize order entry from an existing order **/
    public static String loadCartFromOrder(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String orderId = request.getParameter("orderId");
        
        ShoppingCart cart = getCartObject(request);;

        try {
            Map<String, Object> outMap = dispatcher.runSync("loadCartFromOrder",
                                                UtilMisc.<String, Object>toMap("orderId", orderId,
                                                        "skipProductChecks", Boolean.TRUE, // the products have already been checked in the order, no need to check their validity again
                                                        "userLogin", userLogin,"cart",cart));
            if (!ServiceUtil.isSuccess(outMap)) {
                request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(outMap));
                return "error";
             }

            cart = (ShoppingCart) outMap.get("shoppingCart");

            cart.removeAdjustmentByType("SALES_TAX");
            cart.removeAdjustmentByType("VAT_TAX");
            cart.removeAdjustmentByType("VAT_PRICE_CORRECT");
            cart.removeAdjustmentByType("PROMOTION_ADJUSTMENT");
            String shipGroupSeqId = null;
            long groupIndex = cart.getShipInfoSize();
            List<GenericValue> orderAdjustmentList = new ArrayList<GenericValue>();
            List<GenericValue> orderAdjustments = new ArrayList<GenericValue>();
            orderAdjustments = cart.getAdjustments();
            try {
                orderAdjustmentList = delegator.findList("OrderAdjustment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
            for (long itr = 1; itr <= groupIndex; itr++) {
                shipGroupSeqId = UtilFormatOut.formatPaddedNumber(itr, 5);
                List<GenericValue> duplicateAdjustmentList = new ArrayList<GenericValue>();
                for (GenericValue adjustment: orderAdjustmentList) {
                    if ("PROMOTION_ADJUSTMENT".equals(adjustment.get("orderAdjustmentTypeId"))) {
                        cart.addAdjustment(adjustment);
                    }
                    if ("SALES_TAX".equals(adjustment.get("orderAdjustmentTypeId"))) {
                        if (adjustment.get("description") != null
                                    && ((String)adjustment.get("description")).startsWith("Tax adjustment due")) {
                                cart.addAdjustment(adjustment);
                            }
                        if ( adjustment.get("comments") != null
                                && ((String)adjustment.get("comments")).startsWith("Added manually by")) {
                            cart.addAdjustment(adjustment);
                        }
                    }
                }
                for (GenericValue orderAdjustment: orderAdjustments) {
                    if ("OrderAdjustment".equals(orderAdjustment.getEntityName())) {
                        if (("SHIPPING_CHARGES".equals(orderAdjustment.get("orderAdjustmentTypeId"))) &&
                                orderAdjustment.get("orderId").equals(orderId) &&
                                orderAdjustment.get("shipGroupSeqId").equals(shipGroupSeqId) && orderAdjustment.get("comments") == null) {
                            // Removing objects from list for old Shipping and Handling Charges Adjustment and Sales Tax Adjustment.
                            duplicateAdjustmentList.add(orderAdjustment);
                        }
                    }
                }
                orderAdjustments.removeAll(duplicateAdjustmentList);
            }
        } catch (GenericServiceException exc) {
            request.setAttribute("_ERROR_MESSAGE_", exc.getMessage());
            return "error";
        }

        cart.setAttribute("addpty", "Y");
        session.setAttribute("shoppingCart", cart);
        session.setAttribute("productStoreId", cart.getProductStoreId());
        session.setAttribute("orderMode", cart.getOrderType());
        session.setAttribute("orderPartyId", cart.getOrderPartyId());

        // Since we only need the cart items, so set the order id as null
        cart.setOrderId(null);
        return "success";
    }

    public static String createQuoteFromCart(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String destroyCart = request.getParameter("destroyCart");

        ShoppingCart cart = getCartObject(request);
        Map<String, Object> result = null;
        String quoteId = null;
        try {
            result = dispatcher.runSync("createQuoteFromCart",
                    UtilMisc.toMap("cart", cart,
                            "userLogin", userLogin));
            quoteId = (String) result.get("quoteId");
        } catch (GenericServiceException exc) {
            request.setAttribute("_ERROR_MESSAGE_", exc.getMessage());
            return "error";
        }
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        request.setAttribute("quoteId", quoteId);
        if (destroyCart != null && destroyCart.equals("Y")) {
            ShoppingCartEvents.destroyCart(request, response);
        }

        return "success";
    }

    public static String createCustRequestFromCart(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        String destroyCart = request.getParameter("destroyCart");

        ShoppingCart cart = getCartObject(request);
        Map<String, Object> result = null;
        String custRequestId = null;
        try {
            result = dispatcher.runSync("createCustRequestFromCart",
                    UtilMisc.toMap("cart", cart,
                            "userLogin", userLogin));
            custRequestId = (String) result.get("custRequestId");
        } catch (GenericServiceException exc) {
            request.setAttribute("_ERROR_MESSAGE_", exc.getMessage());
            return "error";
        }
        if (ServiceUtil.isError(result)) {
           request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
           return "error";
        }
        request.setAttribute("custRequestId", custRequestId);
        if (destroyCart != null && destroyCart.equals("Y")) {
            ShoppingCartEvents.destroyCart(request, response);
        }

        return "success";
    }

    /** Initialize order entry **/
    public static String initializeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        String productStoreId = request.getParameter("productStoreId");

        if (UtilValidate.isNotEmpty(productStoreId)) {
            session.setAttribute("productStoreId", productStoreId);
        }
        ShoppingCart cart = getCartObject(request);

        // TODO: re-factor and move this inside the ShoppingCart constructor
        String orderMode = request.getParameter("orderMode");
        if (orderMode != null) {
            cart.setOrderType(orderMode);
            session.setAttribute("orderMode", orderMode);
        } else {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderPleaseSelectEitherSaleOrPurchaseOrder", locale));
            return "error";
        }

        // check the selected product store
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            if (productStore != null) {

                // check permission for taking the order
                boolean hasPermission = false;
                if ((cart.getOrderType().equals("PURCHASE_ORDER")) && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_CREATE", session))) {
                    hasPermission = true;
                } else if (cart.getOrderType().equals("SALES_ORDER")) {
                    if (security.hasEntityPermission("ORDERMGR", "_SALES_CREATE", session)) {
                        hasPermission = true;
                    } else {
                        // if the user is a rep of the store, then he also has permission
                        List<GenericValue> storeReps = null;
                        try {
                            storeReps = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("productStoreId", productStore.getString("productStoreId"),
                                                            "partyId", userLogin.getString("partyId"), "roleTypeId", "SALES_REP"));
                        } catch (GenericEntityException gee) {
                            //
                        }
                        storeReps = EntityUtil.filterByDate(storeReps);
                        if (UtilValidate.isNotEmpty(storeReps)) {
                            hasPermission = true;
                        }
                    }
                }

                if (hasPermission) {
                    cart = ShoppingCartEvents.getCartObject(request, null, productStore.getString("defaultCurrencyUomId"));
                } else {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderYouDoNotHavePermissionToTakeOrdersForThisStore", locale));
                    cart.clear();
                    session.removeAttribute("orderMode");
                    return "error";
                }
                cart.setProductStoreId(productStoreId);
            } else {
                cart.setProductStoreId(null);
            }
        }

        if ("SALES_ORDER".equals(cart.getOrderType()) && UtilValidate.isEmpty(cart.getProductStoreId())) {
            request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderAProductStoreMustBeSelectedForASalesOrder", locale));
            cart.clear();
            session.removeAttribute("orderMode");
            return "error";
        }

        String salesChannelEnumId = request.getParameter("salesChannelEnumId");
        if (UtilValidate.isNotEmpty(salesChannelEnumId)) {
            cart.setChannelType(salesChannelEnumId);
        }

        // set party info
        String partyId = request.getParameter("supplierPartyId");
        cart.setAttribute("supplierPartyId", partyId);
        String originOrderId = request.getParameter("originOrderId");
        cart.setAttribute("originOrderId", originOrderId);

        if (!UtilValidate.isEmpty(request.getParameter("partyId"))) {
            partyId = request.getParameter("partyId");
        }
        String userLoginId = request.getParameter("userLoginId");
        if (partyId != null || userLoginId != null) {
            if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(userLoginId)) {
                GenericValue thisUserLogin = null;
                try {
                    thisUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
                } catch (GenericEntityException gee) {
                    //
                }
                if (thisUserLogin != null) {
                    partyId = thisUserLogin.getString("partyId");
                } else {
                    partyId = userLoginId;
                }
            }
            if (UtilValidate.isNotEmpty(partyId)) {
                GenericValue thisParty = null;
                try {
                    thisParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
                } catch (GenericEntityException gee) {
                    //
                }
                if (thisParty == null) {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderCouldNotLocateTheSelectedParty", locale));
                    return "error";
                } else {
                    cart.setOrderPartyId(partyId);
                    if ("PURCHASE_ORDER".equals(cart.getOrderType())) {
                        cart.setBillFromVendorPartyId(partyId);
                    }
                }
            } else if (partyId != null && partyId.length() == 0) {
                cart.setOrderPartyId("_NA_");
                partyId = null;
            }
        } else {
            partyId = cart.getPartyId();
            if (partyId != null && partyId.equals("_NA_")) partyId = null;
        }

        return "success";
    }

    /** Route order entry **/
    public static String routeOrderEntry(HttpServletRequest request, HttpServletResponse response) {
    
        HttpSession session = request.getSession();

        // if the order mode is not set in the attributes, then order entry has not been initialized
        if (session.getAttribute("orderMode") == null) {
            return "init";
        }

        // if the request is coming from the init page, then orderMode will be in the request parameters
      

        // orderMode is set and there is an order in progress, so go straight to the cart
        return "cart";
    }

    public static String doManualPromotions(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = getCartObject(request);
        List<GenericValue> manualPromotions = new LinkedList<GenericValue>();

        // iterate through the context and find all keys that start with "productPromoId_"
        Map<String, Object> context = UtilHttp.getParameterMap(request);
        String keyPrefix = "productPromoId_";
        for (int i = 1; i <= 50; i++) {
            String productPromoId = (String)context.get(keyPrefix + i);
            if (UtilValidate.isNotEmpty(productPromoId)) {
                try {
                    GenericValue promo = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
                    if (promo != null) {
                        manualPromotions.add(promo);
                    }
                } catch (GenericEntityException gee) {
                    request.setAttribute("_ERROR_MESSAGE_", gee.getMessage());
                    return "error";
                }
            } else {
                break;
            }
        }
        ProductPromoWorker.doPromotions(cart, manualPromotions, dispatcher);
        return "success";
    }


    public static String bulkAddProducts(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        String controlDirective = null;
        Map<String, Object> result = null;
        String productId = null;
        String productCategoryId = null;
        String quantityStr = null;
        String itemDesiredDeliveryDateStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String catalogId = CatalogWorker.getCurrentCatalogId(request);
        String itemType = null;
        String itemDescription = "";

        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        String itemGroupNumber = request.getParameter("itemGroupNumber");

        // Get shoppingList info if passed.  I think there can only be one shoppingList per request
        String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListItemSeqId = request.getParameter("shoppingListItemSeqId");

        // The number of multi form rows is retrieved
        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
                controlDirective = null;                // re-initialize each time
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id

                // get the productId
                if (paramMap.containsKey("productId" + thisSuffix)) {
                    productId = (String) paramMap.remove("productId" + thisSuffix);
                }

                if (paramMap.containsKey("quantity" + thisSuffix)) {
                    quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
                }
                if ((quantityStr == null) || (quantityStr.equals(""))) {    // otherwise, every empty value causes an exception and makes the log ugly
                    quantityStr = "0";  // default quantity is 0, so without a quantity input, this field will not be added
                }

                // parse the quantity
                try {
                    quantity = new BigDecimal(quantityStr);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
                    quantity = BigDecimal.ZERO;
                }

                // get the selected amount
                String selectedAmountStr = null;
                if (paramMap.containsKey("amount" + thisSuffix)) {
                    selectedAmountStr = (String) paramMap.remove("amount" + thisSuffix);
                }

                // parse the amount
                BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(selectedAmountStr)) {
                    try {
                        amount = new BigDecimal(selectedAmountStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                        amount = null;
                    }
                } else {
                    amount = BigDecimal.ZERO;
                }

                if (paramMap.containsKey("itemDesiredDeliveryDate" + thisSuffix)) {
                    itemDesiredDeliveryDateStr = (String) paramMap.remove("itemDesiredDeliveryDate" + thisSuffix);
                }
                // get the item type
                if (paramMap.containsKey("itemType" + thisSuffix)) {
                    itemType = (String) paramMap.remove("itemType" + thisSuffix);
                }

                if (paramMap.containsKey("itemDescription" + thisSuffix)) {
                    itemDescription = (String) paramMap.remove("itemDescription" + thisSuffix);
                }

                Map<String, Object> itemAttributes = UtilMisc.<String, Object>toMap("itemDesiredDeliveryDate", itemDesiredDeliveryDateStr);

                if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                    Debug.logInfo("Attempting to add to cart with productId = " + productId + ", categoryId = " + productCategoryId +
                            ", quantity = " + quantity + ", itemType = " + itemType + " and itemDescription = " + itemDescription, module);
                    result = cartHelper.addToCart(catalogId, shoppingListId, shoppingListItemSeqId, productId,
                                                  productCategoryId, itemType, itemDescription, null,
                                                  amount, quantity, null, null, null, null, null, null,
                                                  itemGroupNumber, itemAttributes,null);
                    // no values for price and paramMap (a context for adding attributes)
                    controlDirective = processResult(result, request);
                    if (controlDirective.equals(ERROR)) {    // if the add to cart failed, then get out of this loop right away
                        return "error";
                    }
                }
            }
        }

        // Determine where to send the browser
        return cart.viewCartOnAdd() ? "viewcart" : "success";
    }

    // request method for setting the currency, agreement, OrderId and shipment dates at once
    public static String setOrderCurrencyAgreementShipDates(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ShoppingCart cart = getCartObject(request);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        Timestamp deliveryDate=null;
        
        
        
       /* SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String data1=request.getParameter("dayOption");
    	//System.out.println("the data\n\n\n\n\n"+data1);
    	String data2[]=data1.split("_");*/
    	/*if(UtilValidate.isNotEmpty(request.getParameter("dayOption"))&& request.getParameter("dayOption").equals("SAME_DAY"))
    	{
    		
    		 deliveryDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    	}
    	if(UtilValidate.isNotEmpty(request.getParameter("dayOption"))&& request.getParameter("dayOption").equals("SECOND_DAY"))
    	{
    		
    		 deliveryDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),2);
    	}
    	if(UtilValidate.isNotEmpty(request.getParameter("dayOption"))&&request.getParameter("dayOption").equals("NEXT_DAY"))
    	{
    		 deliveryDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),1);
    	}
    	if(UtilValidate.isNotEmpty(request.getParameter("dayOption"))&&request.getParameter("dayOption").equals("ADVANCE_BOOKING"))
    	{
    		 String deliveryDate1=request.getParameter("deliveryDate");*/
    		
    		/* Date d = null;
			try {
				d = (Date) sdf.parse(data2[1]);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 deliveryDate = new Timestamp(d.getTime());
    		 
    	
    	
    	String slot= data2[0];
        */
    	 HttpSession session = request.getSession();
        String agreementId = request.getParameter("agreementId");
        String currencyUomId = request.getParameter("currencyUomId");
        String workEffortId = request.getParameter("workEffortId");
        String shipBeforeDateStr = request.getParameter("shipBeforeDate");
        String shipAfterDateStr = request.getParameter("shipAfterDate");
        String cancelBackOrderDateStr = request.getParameter("cancelBackOrderDate");
        String orderId = request.getParameter("orderId");
        String orderName = request.getParameter("orderName");
        String correspondingPoId = request.getParameter("correspondingPoId");
       
        Locale locale = UtilHttp.getLocale(request);
        Map<String, Object> result = null;

        
        
        
        
        // set the agreement if specified otherwise set the currency
        if (UtilValidate.isNotEmpty(agreementId)) {
            result = cartHelper.selectAgreement(agreementId);
        } else if (UtilValidate.isNotEmpty(currencyUomId)) {
            result = cartHelper.setCurrency(currencyUomId);
        }
        if (ServiceUtil.isError(result)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(result));
            return "error";
        }

        // set the work effort id
        cart.setWorkEffortId(workEffortId);

        // set the order id if given
        if (UtilValidate.isNotEmpty(orderId)) {
            GenericValue thisOrder = null;
            try {
                thisOrder = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
            } catch (GenericEntityException e) {
                Debug.logError(e.getMessage(), module);
            }
            if (thisOrder == null) {
                cart.setOrderId(orderId);
            } else {
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"OrderIdAlreadyExistsPleaseChooseAnother", locale));
                return "error";
            }
        }

        
      /*  ((ShoppingCart) cart).setSlot(slot);
        // set the order name
        cart.setDeliveryDate(deliveryDate);*/
        cart.setOrderName(orderName);

        // set the corresponding purchase order id
        cart.setPoNumber(correspondingPoId);

        // set the default ship before and after dates if supplied
        try {
            if (UtilValidate.isNotEmpty(shipBeforeDateStr)) {
                if (shipBeforeDateStr.length() == 10) shipBeforeDateStr += " 00:00:00.000";
                cart.setDefaultShipBeforeDate(java.sql.Timestamp.valueOf(shipBeforeDateStr));
            }
            if (UtilValidate.isNotEmpty(shipAfterDateStr)) {
                if (shipAfterDateStr.length() == 10) shipAfterDateStr += " 00:00:00.000";
                cart.setDefaultShipAfterDate(java.sql.Timestamp.valueOf(shipAfterDateStr));
            }
            if (UtilValidate.isNotEmpty(cancelBackOrderDateStr)) {
                if (cancelBackOrderDateStr.length() == 10) cancelBackOrderDateStr += " 00:00:00.000";
                cart.setCancelBackOrderDate(java.sql.Timestamp.valueOf(cancelBackOrderDateStr));
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        return "success";
    }

    public static String getConfigDetailsEvent(HttpServletRequest request, HttpServletResponse response) {

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productId = request.getParameter("product_id");
        String currencyUomId = ShoppingCartEvents.getCartObject(request).getCurrency();
        ProductConfigWrapper configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, currencyUomId, request);
        if (configWrapper == null) {
            Debug.logWarning("configWrapper is null", module);
            request.setAttribute("_ERROR_MESSAGE_", "configWrapper is null");
            return "error";
        }
        ProductConfigWorker.fillProductConfigWrapper(configWrapper, request);
        if (configWrapper.isCompleted()) {
            ProductConfigWorker.storeProductConfigWrapper(configWrapper, delegator);
            request.setAttribute("configId", configWrapper.getConfigId());
        }

        request.setAttribute("totalPrice", org.ofbiz.base.util.UtilFormatOut.formatCurrency(configWrapper.getTotalPrice(), currencyUomId, UtilHttp.getLocale(request)));
        return "success";
    }

    public static String bulkAddProductsInApprovedOrder(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        String productId = null;
        String productCategoryId = null;
        String quantityStr = null;
        String itemDesiredDeliveryDateStr = null;
        BigDecimal quantity = BigDecimal.ZERO;
        String itemType = null;
        String itemDescription = "";
        String orderId = null;
        String shipGroupSeqId = null;

        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        //FIXME can be removed ?
        // String itemGroupNumber = request.getParameter("itemGroupNumber");
        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
                if (paramMap.containsKey("productId" + thisSuffix)) {
                    productId = (String) paramMap.remove("productId" + thisSuffix);
                }
                if (paramMap.containsKey("quantity" + thisSuffix)) {
                    quantityStr = (String) paramMap.remove("quantity" + thisSuffix);
                }
                if ((quantityStr == null) || (quantityStr.equals(""))) {
                    quantityStr = "0";
                }
                try {
                    quantity = new BigDecimal(quantityStr);
                } catch (Exception e) {
                    Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
                    quantity = BigDecimal.ZERO;
                }
                String selectedAmountStr = null;
                if (paramMap.containsKey("amount" + thisSuffix)) {
                    selectedAmountStr = (String) paramMap.remove("amount" + thisSuffix);
                }
                BigDecimal amount = null;
                if (UtilValidate.isNotEmpty(selectedAmountStr)) {
                    try {
                        amount = new BigDecimal(selectedAmountStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problem parsing amount string: " + selectedAmountStr, module);
                        amount = null;
                    }
                } else {
                    amount = BigDecimal.ZERO;
                }
                if (paramMap.containsKey("itemDesiredDeliveryDate" + thisSuffix)) {
                    itemDesiredDeliveryDateStr = (String) paramMap.remove("itemDesiredDeliveryDate" + thisSuffix);
                }
                Timestamp itemDesiredDeliveryDate = null;
                if (UtilValidate.isNotEmpty(itemDesiredDeliveryDateStr)) {
                    try {
                        itemDesiredDeliveryDate = Timestamp.valueOf(itemDesiredDeliveryDateStr);
                    } catch (Exception e) {
                        Debug.logWarning(e,"Problems parsing Reservation start string: " + itemDesiredDeliveryDateStr, module);
                        itemDesiredDeliveryDate = null;
                        request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error,"shoppingCartEvents.problem_parsing_item_desiredDeliveryDate_string", locale));
                    }
                }
                if (paramMap.containsKey("itemType" + thisSuffix)) {
                    itemType = (String) paramMap.remove("itemType" + thisSuffix);
                }
                if (paramMap.containsKey("itemDescription" + thisSuffix)) {
                    itemDescription = (String) paramMap.remove("itemDescription" + thisSuffix);
                }
                if (paramMap.containsKey("orderId" + thisSuffix)) {
                    orderId = (String) paramMap.remove("orderId" + thisSuffix);
                }
                if (paramMap.containsKey("shipGroupSeqId" + thisSuffix)) {
                    shipGroupSeqId = (String) paramMap.remove("shipGroupSeqId" + thisSuffix);
                }
                if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                    Debug.logInfo("Attempting to add to cart with productId = " + productId + ", categoryId = " + productCategoryId +
                            ", quantity = " + quantity + ", itemType = " + itemType + " and itemDescription = " + itemDescription, module);
                    HttpSession session = request.getSession();
                    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
                    //Map<String, Object> appendOrderItemMap = FastMap.newInstance();
                    Map<String, Object> appendOrderItemMap = new HashMap<String, Object>();
                    
                    appendOrderItemMap.put("productId", productId);
                    appendOrderItemMap.put("quantity", quantity);
                    appendOrderItemMap.put("orderId", orderId);
                    appendOrderItemMap.put("userLogin", userLogin);
                    appendOrderItemMap.put("amount", amount);
                    appendOrderItemMap.put("itemDesiredDeliveryDate", itemDesiredDeliveryDate);
                    appendOrderItemMap.put("shipGroupSeqId", shipGroupSeqId);
                    try {
                        Map<String, Object> result = dispatcher.runSync("appendOrderItem", appendOrderItemMap);
                        request.setAttribute("shoppingCart", (ShoppingCart) result.get("shoppingCart"));
                        ShoppingCartEvents.destroyCart(request, response);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Failed to execute service appendOrderItem", module);
                        request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                        return "error";
                    }
                }
            }
        }
        request.setAttribute("orderId", orderId);
        return  "success";
    }
    public static String orderTracker(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String orderId = request.getParameter("orderId");
        
        
        if(UtilValidate.isEmpty(orderId))
        {
        	request.setAttribute("massage", "Please Enter Order Id");
        	return "error";
        }
        
        List<GenericValue> orderHeaderList = delegator.findList("OrderHeader", 
        		EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("orderId"),EntityOperator.LIKE,
        							EntityFunction.UPPER(orderId.trim().toUpperCase())), null, null, null, true);

        if(UtilValidate.isEmpty(orderHeaderList))
        {
        	request.setAttribute("massage", "Order Not Found");
        	return "error";
        }
        GenericValue orderHeader = (GenericValue)orderHeaderList.get(0);
        
        if(UtilValidate.isEmpty(orderHeader))
        {
        	request.setAttribute("massage", "Order Not Found");
        	return "error";
        }
        
    	List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId), null, null, null, false);
		GenericValue orderItemValue = null;
		if(UtilValidate.isNotEmpty(orderItems)){
			  orderItemValue  =  orderItems.get(0);
		}
		
        boolean checkSlotStatus = true; //Purpose:If it's  Gift vocuher,then no need to check slot table
       if(UtilValidate.isNotEmpty(orderItemValue)){
		GenericValue productValue = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", orderItemValue.getString("productId")));
  		 if(UtilValidate.isNotEmpty(productValue)){
 			 if(productValue.getString("productTypeId").equalsIgnoreCase("DIGITAL_GOOD")){
 				checkSlotStatus = false;
 			 }
 				 
 		 }
       }
       
   		 if(checkSlotStatus){
   			 List<GenericValue> slotTimingListTem= CheckOutEvents.getAllSlots(delegator);
   			 
   			List<GenericValue> slotTimingList=EntityUtil.filterByAnd(slotTimingListTem,UtilMisc.toMap("slotType", orderHeader.getString("slot")));
        
        if(UtilValidate.isEmpty(slotTimingList))
        {
        	request.setAttribute("massage", "Order Not Found");
        	return "error";
        }
        GenericValue slotTiming=EntityUtil.getFirst(slotTimingList);
        Date startDate = new Date(orderHeader.getTimestamp("deliveryDate").getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String delivery=formatter.format(startDate);
        request.setAttribute("deliveryDate", delivery);
        request.setAttribute("fullFillDate", orderHeader.get("fullFillDate"));
        request.setAttribute("slotTiming", slotTiming.getString("slotTiming"));
        request.setAttribute("statusId", orderHeader.getString("statusId"));
        request.setAttribute("orderId",orderId);
  		 }else{
  		request.setAttribute("massage", "This order contains gift vocuher");
        request.setAttribute("orderId",orderId);
  		 }
    return "success";
      
       
    }
    
    public static Map totalQtyAddedInCartList(HttpServletRequest request){
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	ShoppingCart cart = getCartObject(request);
    	GenericValue userLogin = cart.getUserLogin();
    	if(UtilValidate.isEmpty(userLogin))
    		return new HashMap();
    	  
        BigDecimal qty = BigDecimal.ZERO;
        List<ShoppingCartItem> items = cart.items();
        Map qtyMap = new HashMap();
        
        if(UtilValidate.isNotEmpty(items))
        {
        	int count = 1 ;
	        for(ShoppingCartItem item : items){
	        	BigDecimal quantity = item.getQuantity();
	        	BigDecimal todaysQtyOrdered = todaysQtyOrdered = toDaysQuantityOrderedPerProduct(delegator, item.getProductId(), userLogin.getString("userLoginId"));	    	    	
	    	    
	        	quantity = quantity.add(todaysQtyOrdered);

	        	 GenericValue productStore = ProductStoreWorker.getProductStore(request);
	             BigDecimal maxQuantityToOrder = new BigDecimal(20);
	             if(UtilValidate.isNotEmpty(productStore) && UtilValidate.isNotEmpty(productStore.getBigDecimal("maxQuantityToOrder")))
	             	maxQuantityToOrder = productStore.getBigDecimal("maxQuantityToOrder");
	             
	    	    if(!item.getProductId().startsWith("GIFTCARD") && quantity.doubleValue() > maxQuantityToOrder.doubleValue())
	    	    {
	    	    	String msg = null;
	    	    	GenericValue product = item.getProduct();
	    	    	if(UtilValidate.isNotEmpty(product))
	    	    		msg = " Can not add more than "+maxQuantityToOrder.intValue()+" nos. Of '"+product.getString("brandName")+" "+product.getString("internalName") +"' per day \n";
					else
						msg = " Can not add more than "+maxQuantityToOrder.intValue()+" nos. Of items per day \n";
	    	    	
	    	    	count = count++ ;
	    	    	qtyMap.put(cart.getItemIndex(item)+"____"+maxQuantityToOrder.subtract(todaysQtyOrdered).intValue(), msg);
	    	    }
	        }
        }
	    
	   return qtyMap;
    }
    public static String imageFileUpload(HttpServletRequest request,HttpServletResponse response) {
         String imageType = null;
    	String filePath = System.getProperty("ofbiz.home");
        filePath +="/framework/images/webapp/images/products/";
	         try {
	            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);    
 	            for (FileItem item : items) {
	                if (item.isFormField()) {
	                	  imageType =  item.getString();
	                	  if(UtilValidate.isEmpty(imageType)){
	                		  break; 
	                	  }
	                	  filePath +=imageType+"/"; 
  	                } else {
  	                    String fileName = item.getName();
 	                    File file = new File(filePath+fileName);
	                    file.createNewFile();
	                    item.write(file);
	                    
	                }
	            }
	        } catch (Exception  e) {
	        	Debug.logInfo("Error In saving file name", module);
	        }
       
      
    	
  	  return "success";
    }
// public static String  getJson(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException{
//    	Delegator delegator = (Delegator) request.getAttribute("delegator");
//    	List<GenericValue> gv =   delegator.findList("Product", null,UtilMisc.toSet("productId"),null,null,false);
//    	request.setAttribute("productList", gv.subList(0,5));
//    	return "success";
//}
    
}



class ValueComparator implements Comparator<String> {

    Map<String, BigDecimal> base;
    public ValueComparator(Map<String, BigDecimal> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a).doubleValue() <= base.get(b).doubleValue()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}