package com.ilinks.restful.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppinglist.ShoppingListEvents;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class ShoppingListServices {
    public static final String MODULE = ShoppingListServices.class.getName();
    
    
    public static Map<String, Object> expireShoppingList(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = null;
        Delegator delegator = dctx.getDelegator();
        try{
            String shoppingListID = (String)context.get("shoppingListID");
            GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);
            shoppingList.set("isActive", "N");
            delegator.store(shoppingList);
            results = ServiceUtil.returnSuccess();
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable Expire Shopping List" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    public static Map<String, Object> updateShoppingListAddress(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = null;
        Delegator delegator = dctx.getDelegator();
        try{
            String shoppingListID = (String)context.get("shoppingListID");
            String contactMechID  = (String)context.get("contactMechID");
            GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);    
            shoppingList.set("contactMechId", contactMechID);
            delegator.store(shoppingList);
            results = ServiceUtil.returnSuccess();
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Update Address" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    public static Map<String, Object> updateShoppingListPaymentMethod(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = null;
        Delegator delegator = dctx.getDelegator();
        try{
            String shoppingListID = (String)context.get("shoppingListID");
            String paymentMethodID  = (String)context.get("paymentMethodID");
            GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);    
            shoppingList.set("paymentMethodId", paymentMethodID);
            delegator.store(shoppingList);
            results = ServiceUtil.returnSuccess();
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Update Payment Method" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    public static Map<String, Object> updateShoppingListNextOrderDate(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = null;
        Delegator delegator = dctx.getDelegator();
        try{
            String shoppingListID = (String)context.get("shoppingListID");
            Timestamp startDateTime  = (Timestamp)context.get("startDateTime");
            GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);    
            GenericValue recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
            
            shoppingList.set("lastOrderedDate", null);
            recurrenceInfo.set("startDateTime", startDateTime);
            delegator.store(recurrenceInfo);
            delegator.store(shoppingList);
            results = ServiceUtil.returnSuccess();
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Change Next Delivery Date" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    public static Map<String, Object> updateShoppingListFrequency(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = null;
        Delegator delegator = dctx.getDelegator();
        try{
            String shoppingListID = (String)context.get("shoppingListID");
            Long frequency  = (Long)context.get("frequency");
            GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);    
            GenericValue recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
            GenericValue recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule");
            recurrenceRule.set("intervalNumber", frequency);
            Timestamp startOfDay = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
            shoppingList.set("lastOrderedDate", startOfDay);
            delegator.store(recurrenceRule);
            delegator.store(shoppingList);
            results = ServiceUtil.returnSuccess();
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Change Frequency" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    public static Map<String, Object> retrieveShoppingListInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnError("Unable To Delete Billing Profile");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Delegator delegator = dctx.getDelegator();
        List<Object> resultList = FastList.newInstance();
        try{
           
            List<GenericValue> list = delegator.findByAnd("ShoppingList", UtilMisc.toMap("partyId", userLogin.get("partyId"), "shoppingListTypeId", "SLT_AUTO_REODR", "isActive", "Y"));
            for(GenericValue shoppingList : list ){
                Timestamp lastOrderDate = shoppingList.getTimestamp("lastOrderedDate");
                Timestamp nextOrderDate = null;
                GenericValue recurrenceInfo = shoppingList.getRelatedOneCache("RecurrenceInfo");
                GenericValue recurrenceRule  = recurrenceInfo.getRelatedOneCache("RecurrenceRule");
                Long frequency = RESTfulServices.getFrequency(shoppingList);
                GenericValue postalAddress = shoppingList.getRelatedOneCache("PostalAddress");
                Map<String, Object> map  = FastMap.newInstance();
                resultList.add(map);
                
                map.put("shoppingListID", shoppingList.get("shoppingListId"));
                map.put("listName", shoppingList.get("listName"));
                map.put("promoCode", shoppingList.get("productPromoCodeId"));
                map.put("partyID", shoppingList.get("partyId"));
                map.put("paymentMethodID", shoppingList.get("paymentMethodId"));
                map.put("frequency",frequency);
                
                Debug.log("frequency " + frequency + " " + recurrenceRule.getLong("intervalNumber"));
                
                postalAddress.remove("createdTxStamp");
                postalAddress.remove("lastUpdatedStamp");
                postalAddress.remove("lastUpdatedTxStamp");
                postalAddress.remove("createdStamp");
                map.put("postalAddress",postalAddress);
                
                // next order date
                if(lastOrderDate != null){
                    Long days = recurrenceRule.getLong("intervalNumber") * 7;
                    nextOrderDate = UtilDateTime.addDaysToTimestamp(lastOrderDate, days.intValue());
                }
                else{
                    nextOrderDate = recurrenceInfo.getTimestamp("startDateTime");
                }
                map.put("nextOrderDate", UtilFormatOut.formatDate(nextOrderDate));
                List<Object> itemList = FastList.newInstance();
                map.put("shoppingListItems", itemList);
                // shopping list item
                List<GenericValue> items = shoppingList.getRelatedCache("ShoppingListItem");
                for(GenericValue item : items){
                    Map<String,Object> tempMap = FastMap.newInstance();
                    tempMap.put("quantity", item.getBigDecimal("quantity"));
                    tempMap.put("shoppingListItemPrice", item.getBigDecimal("modifiedPrice"));
                    tempMap.put("productID", item.getString("productId"));
                    tempMap.put("seqID", item.getString("shoppingListItemSeqId"));
                    GenericValue product = item.getRelatedOneCache("Product");
                    
                    List<GenericValue> priceList = product.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId","DEFAULT_PRICE"));
                    tempMap.put("productPrice", EntityUtil.getFirst(priceList).getBigDecimal("price"));
                    // get feature
                    List<GenericValue> features = product.getRelatedCache("ProductFeatureAppl");
                    GenericValue parentProduct = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                    if(parentProduct != null){
                        features = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureTypeId", "NET_WEIGHT"), features);
                    }
                    else{
                        features = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureTypeId", "STAND_ALONE_FILTER"), features);
                        parentProduct = product;
                    }
                    tempMap.put("brandName", parentProduct.getString("brandName"));
                    tempMap.put("description", parentProduct.getString("productName"));
                    tempMap.put("smallImageURL", parentProduct.getString("smallImageUrl"));
                    tempMap.put("parentProductID", parentProduct.getString("productId"));
                    tempMap.put("feature", EntityUtil.getFirst(features).getString("description"));
                    itemList.add(tempMap);
                }
                
            }
            results.clear();
        }
        catch (Exception e){
            Debug.logError(e, "Unable To Retrive Shopping List" + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            if(ServiceUtil.isError(results)){
                results = ServiceUtil.returnError("Unable To Retrive Shopping List " );
                Debug.logError(ServiceUtil.getErrorMessage(results) + " " + context, MODULE);
            }
            results.put("shoppingListInfo", resultList);
        }
        return results;
    }
    
    
    
    
    
    
    /**
     * set comments field to freq from shopping list
     * @param dctx
     * @param context
     * @return
     */
    
    public static Map<String, Object> updateFreqFromShoppingListItem(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        Long frequency = Long.parseLong("0");
        try{
           String shoppingListID = (String)context.get("shoppingListId");
           String productID = (String)context.get("productId");
           BigDecimal quantity = (BigDecimal)context.get("quantity");
           GenericValue shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListID), false);
           frequency = RESTfulServices.getFrequency(shoppingList);
           GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, shoppingList.getString("listName"));
           List<GenericValue> orderItemList = orderHeader.getRelatedByAnd("OrderItem", UtilMisc.toMap("productId", productID, "quantity", quantity));
           
           for(GenericValue orderItem : orderItemList){
               orderItem.set("comments", frequency.toString());
               orderItem.store();
           }
        }
        catch(Exception e){
            Debug.logError(ServiceUtil.getErrorMessage(results) + " " + context, MODULE);
        }
        finally{
            results.put("frequency", frequency);
        }
        return results;
    }
    
    /**
     * set comments field to freq from order header
     * @param dctx
     * @param context
     * @return
     */
    
    public static Map<String, Object> updateFreqFromOrderItem(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        Long frequency = Long.parseLong("0");
        try{
            List<GenericValue> orderItemList = delegator.findByAnd("OrderItem",  UtilMisc.toMap("orderId",context.get("orderId"), "orderItemSeqId", context.get("orderItemSeqId"),"productId", context.get("productId")));
           GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, (String)context.get("orderId"));
           GenericValue shoppingList = orderHeader.getRelatedOne("AutoOrderShoppingList");
           if(shoppingList != null){
               frequency = RESTfulServices.getFrequency(shoppingList);
           }
           for(GenericValue orderItem : orderItemList){
               orderItem.set("comments", frequency.toString());
               orderItem.store();
           }
        }
        catch(Exception e){
            Debug.logError(ServiceUtil.getErrorMessage(results) + " " + e.getMessage() + "\n" + context, MODULE);
        }
        finally{
            results.put("frequency", frequency);
        }
        return results;
    }
    /**
    private static Long getFrequency(GenericValue shoppingList){
        Long frequency = Long.parseLong("0");
        try{
            GenericValue recurrenceInfo = shoppingList.getRelatedOne("RecurrenceInfo");
            GenericValue recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule");
            frequency = recurrenceRule.getLong("intervalNumber");
        }
        catch (Exception e){
            Debug.logError(e.getMessage(), MODULE);
        }
        return frequency;
    }
    **/
}
