package com.ilinks.restful.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import com.ilinks.restful.customer.ContactHelper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionSubSelect;
import org.ofbiz.entity.condition.EntityConditionValue;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ilinks.restful.util.RestfulHelper;

public class OrderInfoServices {
    public static final String MODULE = OrderInfoServices.class.getName();
    
    private static List<String> invalidCreditCardAttributes(){
        List<String> invalidAttributes = UtilMisc.toList("validFromDate", "consecutiveFailedAuths", "lastUpdatedTxStamp","consecutiveFailedNsf","lastFailedNsfDate","lastUpdatedStamp","lastUpdatedTxStamp","createdStamp","createdTxStamp");
        invalidAttributes.add("issueNumber");
        invalidAttributes.add("contactMechId");
        invalidAttributes.add("lastFailedAuthDate");
        return invalidAttributes;
        
    }
    
    private static List<String> invalidPostalAddressAttributes(){
        List<String> invalidAttributes = UtilMisc.toList("countyGeoId", "lastUpdatedStamp", "lastUpdatedTxStamp","createdStamp","createdTxStamp","directions","postalCodeExt","countryGeoId","geoPointId");
        invalidAttributes.add("postalCodeGeoId");
        return invalidAttributes;
    }
    private static String getFrequency(Delegator delegator, String frequency, String orderID) throws Exception {          
        if(frequency != null){
            frequency = frequency.replace('w', '\b');
        }
        else{
            frequency = "0";
            // freq from shipping list
            GenericValue shoppingList = EntityUtil.getFirst(delegator.findByAndCache("ShoppingList", UtilMisc.toMap("shoppingListTypeId","SLT_AUTO_REODR", "listName", orderID)));
            if(shoppingList != null){
                GenericValue recurrenceInfo = shoppingList.getRelatedOneCache("RecurrenceInfo");
                GenericValue recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule");
                frequency = recurrenceRule.getString("intervalNumber");
            }
        }
        return frequency;
    }
    
    private static String getFeature(GenericValue product) throws Exception {
        List<GenericValue> featureList = product.getRelatedCache("ProductFeatureAppl");
        
        featureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "PRODUCT_VARIATION"),featureList);
        GenericValue feature = null;
        if(UtilValidate.isNotEmpty(featureList)){
            feature = EntityUtil.getFirst(featureList);
        }
        else{
            featureList = product.getRelatedCache("ProductFeatureAppl");
            featureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "STAND_ALONE"),featureList);
            feature = EntityUtil.getFirst(featureList);
        }
        if(feature != null){
            return feature.getString("description");
        }
       return "";
    }
    
    public static Map<String, Object> getOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String orderID = (String)context.get("orderID");
        try{
           //GenericValue userLogin = (GenericValue) context.get("userLogin");
           //GenericValue person = userLogin.getRelatedOneCache("Person");
           GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, orderID);
           OrderReadHelper helper = OrderReadHelper.getHelper(orderHeader);
           
           List<GenericValue> paymentPreferenceList = helper.getPaymentPreferences();
           List<Object> orderItemList = FastList.newInstance();
           paymentPreferenceList = EntityUtil.filterByAnd(paymentPreferenceList, UtilMisc.toMap("paymentMethodTypeId", "CREDIT_CARD"));
           for(GenericValue paymentPreference : paymentPreferenceList){
               GenericValue creditCard = paymentPreference.getRelatedOneCache("CreditCard");
               GenericValue clone = (GenericValue)creditCard.clone();
               clone.set("cardNumber",  ContactHelper.formatCreditCard(creditCard, 4, "*"));
               RestfulHelper.removeAttribute(clone, invalidCreditCardAttributes());
               results.put("creditCard", clone);
               break;
           }
           results.put("orderDate", RestfulHelper.formatDate(orderHeader.getTimestamp("orderDate")));
           GenericValue address = helper.getShippingAddress();
           RestfulHelper.removeAttribute(address,invalidPostalAddressAttributes());
           results.put("shippingAddress",address);
           address = helper.getBillingAddress();
           RestfulHelper.removeAttribute(address,invalidPostalAddressAttributes());
           results.put("billingAddress",address);
           
           GenericValue person = helper.getPartyFromRole("BILL_TO_CUSTOMER");
           
           List<GenericValue> orderItems = helper.getOrderItems();
           
           for(GenericValue orderItem : orderItems){
               Map<Object,Object> map = FastMap.newInstance();
               map.put("price", orderItem.getBigDecimal("unitPrice"));
               map.put("description", orderItem.getString("itemDescription"));
               map.put("quantity",orderItem.getBigDecimal("quantity"));
               String frequency =  orderItem.getString("comments");
               frequency = getFrequency(delegator,frequency, orderID);
              
               map.put("frequency", frequency);
               
               GenericValue product = orderItem.getRelatedOneCache("Product");
               String feature = getFeature(product);
               map.put("feature",feature);
               if(ProductWorker.getVariantVirtualId(product) != null){
                   // for brands
                   product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
               }
               map.put("brandName",product.getString("brandName"));
               map.put("parentProductID", product.getString("productId"));
               map.put("smallImageURL", product.getString("smallImageUrl"));
               orderItemList.add(map);
           }
           
           GenericValue orderStatus = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", helper.getOrderId(), "attrName", "inProcessStatus"), true);
           if(orderStatus != null && orderStatus.getString("attrValue").equals("ORDER_PROCESSING")){
               // working on order
               results.put("orderStatus","Processing");
           }
           else{
               results.put("orderStatus",helper.getCurrentStatusString());
           }
           if(helper.getCurrentStatusString().equals("Completed")){
               // order completed
              
               results.put("completedDate", RestfulHelper.formatDate(orderHeader.getTimestamp("lastUpdatedTxStamp")));
           }
           else{
               results.put("completedDate","NA");
           }
         
           
           results.put("firstName", person.get("firstName"));
           results.put("lastName", person.get("lastName"));
           results.put("total",  helper.getOrderGrandTotal());
           results.put("orderID", orderID);
           results.put("items", orderItemList);
           results.put("tax", helper.getTaxTotal());
           results.put("adjustments",  OrderReadHelper.calcOrderPromoAdjustmentsBd(helper.getAdjustments()));
           //results.put("deliveryEstimate",  RestfulHelper.formatDate(  UtilDateTime.addDaysToTimestamp(orderHeader.getTimestamp("orderDate"), 7)));
           results.put("deliveryEstimate",  RestfulHelper.formatDate(  UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(), 7)));
           results.put("shipping", helper.getShippingTotal());
           results.put("subTotal", helper.getOrderItemsSubTotal());
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Retrieve Order" + orderID + "\n" + e.getMessage(), MODULE);
        }
        finally{
            
        }
        return results;
    }
    
    
    public static Map<String, Object> updateOrderItemInventoryInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String orderID = (String)context.get("orderId");
        try{
        	GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, orderID);
        	List<GenericValue> itemIssuances = orderHeader.getRelated("ItemIssuance");
           
        	Timestamp lastUpdatedDate = orderHeader.getTimestamp("lastUpdatedTxStamp");
            if(!isValidOrder(lastUpdatedDate)){
         	   Debug.log(String.format("inventory lastUpdatedDate %s %s", lastUpdatedDate.toString(), UtilDateTime.nowTimestamp().toString()));
         	   return results;
            }
            for(GenericValue itemIssuance : itemIssuances){
            	GenericValue orderItem = itemIssuance.getRelatedOne("OrderItem");
                GenericValue inventory = itemIssuance.getRelatedOneCache("InventoryItem");
                orderItem.set("fromInventoryItemId", inventory.get("inventoryItemId"));
                orderItem.set("unitAverageCost",inventory.get("unitCost"));
                orderItem.store();   
            }
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Update Order Item" + orderID + "\n" + e.getMessage(), MODULE);
        }
        finally{
        }
        return results;
    }
    
    public static Map<String, Object> updateOrderStatsFrequency(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        String shoppingListID = (String)context.get("shoppingListId");
        Delegator delegator = dctx.getDelegator();
        try{
            GenericValue shoppingList = EntityUtil.getFirst(delegator.findByAnd("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID)));
            GenericValue info = shoppingList.getRelatedOne("RecurrenceInfo");
            GenericValue rule = info.getRelatedOne("RecurrenceRule");
            String frequency = rule.getLong("intervalNumber").toString();
            GenericValue orderStats = delegator.findByPrimaryKey("OrderStats",UtilMisc.toMap("orderId",shoppingList.get("listName")));
            if(orderStats.get("frequency") == null || orderStats.getString("frequency").equals("0")){
                orderStats.set("frequency", frequency);
                orderStats.store();
            }
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Order Status Freq" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    
    public static Map<String, Object> createOrderStats(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = ServiceUtil.returnSuccess();
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	try{
    		EntityConditionValue subSelect = new EntityConditionSubSelect("OrderStats", "orderId", null, true, delegator);
    		List<EntityCondition> conditionList = FastList.newInstance();
    		GenericValue userLogin =  (GenericValue)context.get("userLogin");
    		conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, subSelect));
    		conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
    		conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
    		List<GenericValue> orderHeaders = delegator.findList("OrderHeader", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null,false);
    		for(GenericValue orderHeader : orderHeaders){
    			Map<String, Object> data = UtilMisc.toMap("orderId",orderHeader.getString("orderId"), "userLogin",userLogin);
    			dispatcher.runAsync("createOrderStatsRecord", data);
    		}
    	}
    	catch (Exception e){
    		Debug.logError(e, "Unable To Find Order Stats Not In Order Header" + context + "\n" + e.getMessage(), MODULE);
    	}


    	return results;
    }
    
    @SuppressWarnings("deprecation")
	public static Map<String, Object> createOrderStatsRecord(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        String orderID = (String)context.get("orderId");
        try{
           GenericValue orderStats = delegator.makeValue("OrderStats");
           GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, orderID);
           OrderReadHelper helper = new OrderReadHelper(orderHeader);
          
           if(orderHeader.get("autoOrderShoppingListId") == null){
               orderStats.set("newOrder", "Y");
           }
           else{
               orderStats.set("newOrder", "N");
           }
           
           List<GenericValue> adjustments = getPromotionAdjustments(helper.getAdjustments());
           
           GenericValue person = helper.getBillToParty();
           
           List<GenericValue> orderRoles = delegator.findByAndCache("OrderRole",UtilMisc.toMap("partyId", person.getString("partyId"), "roleTypeId","BILL_TO_CUSTOMER"));
           
           List<GenericValue> orderHeaders = EntityUtil.getRelatedByAnd("OrderHeader", UtilMisc.toMap("statusId", "ORDER_COMPLETED"), orderRoles);
           BigDecimal totalOrders = BigDecimal.valueOf((long)orderHeaders.size());
           totalOrders = totalOrders.add(BigDecimal.ONE);
           
           if(orderHeader.get("autoOrderShoppingListId") != null){
               orderStats.set("shoppingListId",orderHeader.get("autoOrderShoppingListId"));
               GenericValue shoppingList = orderHeader.getRelatedOne("AutoOrderShoppingList");
               GenericValue originalOrderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", shoppingList.get("listName")), false);
               orderStats.set("originalOrderTotal", originalOrderHeader.getBigDecimal("grandTotal"));
               orderStats.set("originalOrderId", originalOrderHeader.get("orderId"));
           }
           else{
               orderStats.set("originalOrderId",helper.getOrderId());
               GenericValue shoppingList = EntityUtil.getFirst(delegator.findByAndCache("ShoppingList", UtilMisc.toMap("shoppingListTypeId","SLT_AUTO_REODR", "listName",helper.getOrderId())));
               if(shoppingList != null){
                   orderStats.set("originalOrderId",shoppingList.get("shoppingListId"));
               }
               else{
                   orderStats.set("originalOrderId","NA");
               }
               orderStats.set("originalOrderTotal", helper.getOrderGrandTotal());
           }
           String frequency =  EntityUtil.getFirst(helper.getOrderItems()).getString("comments");
           frequency = getFrequency(delegator, frequency, helper.getOrderId());
           
           orderStats.set("promotionCode","NA");
           for(String promoCode : helper.getProductPromoCodesEntered()){
               orderStats.set("promotionCode",promoCode);
               break;
           }
           orderStats.set("promotionDescription","NA");
           for(GenericValue promotionUse : helper.getProductPromoUse()){
               GenericValue promotion = promotionUse.getRelatedOneCache("ProductPromo");
               orderStats.set("promotionDescription",promotion.get("promoText"));
           }
           
           BigDecimal totalCost = BigDecimal.ZERO;
           List<GenericValue> itemIssuances = orderHeader.getRelated("ItemIssuance");
           
           Timestamp lastUpdatedDate = orderHeader.getTimestamp("lastUpdatedTxStamp");
           if(!isValidOrder(lastUpdatedDate)){
        	   Debug.log(String.format("###### order header lastUpdatedDate %s %s", lastUpdatedDate.toString(), UtilDateTime.nowTimestamp().toString()));
        	   return results;
           }
           for(GenericValue itemIssuance : itemIssuances){
               GenericValue inventory = itemIssuance.getRelatedOneCache("InventoryItem");
               BigDecimal cost = inventory.getBigDecimal("unitCost");
               BigDecimal quantity = itemIssuance.getBigDecimal("cancelQuantity") == null ? BigDecimal.ZERO :  itemIssuance.getBigDecimal("cancelQuantity");
               quantity = itemIssuance.getBigDecimal("quantity").subtract(quantity);
               cost = cost.multiply(quantity);
               totalCost = totalCost.add(cost);
           }
           List<GenericValue> shipGroups = helper.getOrderItemShipGroups();
           
           BigDecimal shipmentWeight = BigDecimal.ZERO;
           for(GenericValue shipGroup : shipGroups){
        	   shipmentWeight = shipmentWeight.add(helper.getShippableWeight(shipGroup.getString("shipGroupSeqId")));
           }
          
           orderStats.set("orderId", helper.getOrderId());
           orderStats.set("orderDate", orderHeader.getTimestamp("orderDate"));
           orderStats.set("total", helper.getOrderGrandTotal());
           orderStats.set("discountAmount", OrderReadHelper.calcOrderPromoAdjustmentsBd(adjustments));
           orderStats.set("giftCardAmount", helper.getGiftCardPaymentPreferenceTotal());
           orderStats.set("totalOrders", totalOrders.longValue());
           orderStats.set("totalCost", totalCost);
           orderStats.set("frequency",frequency);
           orderStats.set("partyId", person.getString("partyId"));
           orderStats.set("emailAddress", helper.getOrderEmailString());
           orderStats.set("address", helper.getShippingAddress().get("address1"));
           orderStats.set("city", helper.getShippingAddress().get("city"));
           orderStats.set("state", helper.getShippingAddress().get("stateProvinceGeoId"));
           orderStats.set("postalCode", helper.getShippingAddress().get("postalCode"));
           orderStats.set("postalCode", helper.getShippingAddress().get("postalCode"));
           orderStats.set("fullName", String.format("%s %s", person.getString("firstName"), person.getString("lastName")));
           orderStats.set("orderDateMilli", orderHeader.getTimestamp("orderDate").getTime());
           orderStats.set("weight", shipmentWeight);
           orderStats.create();
        }
        catch (Exception e){
            Debug.logError(e, "Unable Create Order Stats Entry " + orderID + "\n" + e.getMessage(), MODULE);
        }
        finally{
           
        }
        return results;
    }
    
    public static Map<String, Object> orderShipmentProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        List<Map<String,Object>> productList = FastList.newInstance();
        String trackingCode = "Tracking Code";
        try{
           String orderID = (String)context.get("orderID");
           String shipmentID = (String)context.get("shipmentID");
           List<GenericValue> orderShipments = delegator.findByAnd("OrderShipment", UtilMisc.toMap("orderId", orderID,"shipmentId",shipmentID));
           
           GenericValue shipmentRouteSegment = EntityUtil.getFirst(delegator.findByAnd("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId",shipmentID), UtilMisc.toList("shipmentPackageSeqId DESC")));
           trackingCode = shipmentRouteSegment.getString("trackingCode") == null ? "Tracking Code" :  shipmentRouteSegment.getString("trackingCode");
           for(GenericValue orderShipment : orderShipments){
               Map<String,Object> map = FastMap.newInstance();
               GenericValue orderItem  = orderShipment.getRelatedOne("OrderItem");
               GenericValue product = orderItem.getRelatedOne("Product");
               String frequency = getFrequency(delegator,orderItem.getString("comments"), orderID);
               String feature = getFeature(product);
               
               map.put("productID", orderItem.get("productId"));
               map.put("quantity", orderShipment.get("quantity"));
               map.put("price", orderItem.get("unitPrice"));
               map.put("listPrice", orderItem.get("unitListPrice"));
               map.put("description", orderItem.get("itemDescription"));
               map.put("frequency", frequency);
               map.put("feature", feature);
               
               GenericValue parentProduct = ProductWorker.getParentProduct((String)orderItem.get("productId"), delegator);
               if(parentProduct != null){
                   product = parentProduct;
               }
               map.put("brandName",product.get("brandName"));
               map.put("smallImageURL", product.get("smallImageUrl"));
               map.put("parentProductID", product.get("productId"));
               productList.add(map);
           }
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Retrieve Shipment Info " + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            results.put("productList", productList);
            results.put("trackingCode", trackingCode);
        }
        return results;
    }
    
    
    public static Map<String, Object> createOrderInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = ServiceUtil.returnSuccess();
    	Delegator delegator = dctx.getDelegator();
    	String orderID = (String)context.get("orderId");
    	try{
    		GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, orderID);
    		OrderReadHelper helper = OrderReadHelper.getHelper(orderHeader);
    		GenericValue person = helper.getBillToParty();
    		List<GenericValue> orderInvResList = orderHeader.getRelated("OrderItemShipGrpInvRes");
    		
    		BigDecimal numberOfBackOrderItems = BigDecimal.ZERO;
    		String fullName = person.getString("firstName") + " " + person.getString("lastName");
    		
    		GenericValue orderInfo = delegator.findOne("OrderInfo", UtilMisc.toMap("orderId", orderID), false);
    		if(orderInfo == null){
    			String shoppingListID = orderHeader.getString("autoOrderShoppingListId") == null ? "NA" :  orderHeader.getString("autoOrderShoppingListId");
    			orderInfo = delegator.makeValue("OrderInfo");
    			orderInfo.set("orderId", orderHeader.get("orderId"));
    			orderInfo.set("orderDate", orderHeader.get("orderDate"));
    			orderInfo.set("productStoreId", orderHeader.getString("webSiteId"));
    			orderInfo.set("fullName", fullName);
    			orderInfo.set("orderDateMilli", UtilDateTime.nowTimestamp().getTime());
    			orderInfo.set("total", orderHeader.getBigDecimal("grandTotal"));
    			orderInfo.set("shoppingListId", shoppingListID);
    		}
    		for(GenericValue orderInvRes : orderInvResList ){
    			BigDecimal backOrderItems = orderInvRes.getBigDecimal("quantityNotAvailable") != null ? orderInvRes.getBigDecimal("quantityNotAvailable")  : BigDecimal.ZERO;
    			numberOfBackOrderItems = numberOfBackOrderItems.add(backOrderItems);
    		}
    		Debug.log(String.format("%s %s", orderHeader.getString("orderId"), orderHeader.getString("orderStatus")));
    		orderInfo.set("statusId", orderHeader.get("statusId"));
    		orderInfo.set("backOrder", numberOfBackOrderItems);
    		orderInfo.set("inProcessing", "N");
    		delegator.createOrStore(orderInfo);
    	}
    	catch (Exception e){
    		Debug.logError(e, "Unable To Create OrderInfo " +orderID + "\n" + e.getMessage(), MODULE);
    	}
    	return results;
    }
    
    public static Map<String, Object> updateOrderInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = ServiceUtil.returnSuccess();
    	Delegator delegator = dctx.getDelegator();
    	String orderID = (String)context.get("orderId");
    	try{
    		List<GenericValue> orderInvResList = delegator.findByAnd("OrderItemShipGrpInvRes", UtilMisc.toMap("orderId", orderID));
    		GenericValue orderInfo = delegator.findOne("OrderInfo", UtilMisc.toMap("orderId", orderID), false);
    		BigDecimal numberOfBackOrderItems = BigDecimal.ZERO;
    		
    		if(orderInfo != null){
    			for(GenericValue orderInvRes : orderInvResList ){
    				BigDecimal backOrderItems = orderInvRes.getBigDecimal("quantityNotAvailable") != null ? orderInvRes.getBigDecimal("quantityNotAvailable")  : BigDecimal.ZERO;
    				numberOfBackOrderItems = numberOfBackOrderItems.add(backOrderItems);
    			}
    			if(context.get("attrValue") != null){
    				orderInfo.set("inProcessing", "Y");
    			}
    			orderInfo.set("backOrder", numberOfBackOrderItems);
    			
    			orderInfo.store();
    		}
    	}
    	catch (Exception e){
    		Debug.logError(e, "Unable To Update OrderInfo " +orderID + "\n" + e.getMessage(), MODULE);
    	}
    	return results;
    }
    
    public static Map<String, Object> getOrderInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> results = ServiceUtil.returnSuccess();
    	Delegator delegator = dctx.getDelegator();
    	List<String> orderStatuses = null ; //UtilMisc.toList("ORDER_APPROVED", "ORDER_HOLD");
    	try{
    		List<EntityCondition> entityConditions = FastList.newInstance();
    		//orderStatuses = context.get("orderStatuses") == null ? orderStatuses : (List<String>)context.get("orderStatuses");
    		if(context.get("orderStatuses") != null){
            	orderStatuses = (List<String>)context.get("orderStatuses");
            	entityConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, orderStatuses));
            }
    		
    		if(context.get("backOrder").equals("Y")){
    			entityConditions.add(EntityCondition.makeCondition("backOrder", EntityOperator.GREATER_THAN , BigDecimal.ZERO));
    		}
    		else{
    			//entityConditions.add(EntityCondition.makeCondition("backOrder", EntityOperator.EQUALS, BigDecimal.ZERO));
    		}
    		
    		if(context.get("inProcessing").equals("Y")){
    			entityConditions.add(EntityCondition.makeCondition("inProcessing", EntityOperator.EQUALS , "Y"));
    		}
    		else{
    			//entityConditions.add(EntityCondition.makeCondition("inProcessing", EntityOperator.EQUALS , "N"));
    		}
    		
    		if(context.get("autoShip").equals("Y")){
    			entityConditions.add(EntityCondition.makeCondition("shoppingListId", EntityOperator.NOT_EQUAL,"NA"));
    		}
    		else{
    			//entityConditions.add(EntityCondition.makeCondition("shoppingListId", EntityOperator.EQUALS, "NA"));
    		}
    		
    		 EntityListIterator it = delegator.find("OrderInfo", EntityCondition.makeCondition(entityConditions,EntityOperator.AND), null, null, UtilMisc.toList("orderDate DESC"), null);
    		 List<GenericValue> orderInfo = it.getPartialList(1, 200);
    		 
    		 results.put("orderInfo", orderInfo);
    		 it.close();
    	}
    	catch (Exception e){
    		Debug.logError(e, "getOrderInfo" +context + "\n" + e.getMessage(), MODULE);
    	}
    	return results;
    }
    
    
    
    
    private static List<GenericValue> getPromotionAdjustments(List<GenericValue> adjustments){
        for(GenericValue adjustment : adjustments){
            if(adjustment.getString("productPromoId") == "PRODUCT_SALE"){
              adjustment.remove(adjustment);
            }
        }
        return adjustments;
    }
    
    private static boolean isValidOrder(Timestamp lastUpdatedDate){
    	boolean validOrder = true;
//    	long time = lastUpdatedDate.getTime() + (1000*1*60*30);
//    	long now = UtilDateTime.nowTimestamp().getTime();
    	Timestamp validDate = UtilDateTime.adjustTimestamp(lastUpdatedDate, Calendar.MINUTE, 30, TimeZone.getDefault(), Locale.getDefault());
    	if(validDate.after(UtilDateTime.nowTimestamp())){
     	  validOrder = false;
        }
    	return validOrder;
    }
}
