package com.ilinks.restful.profile;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import com.ilinks.restful.customer.ContactHelper;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;
//import org.apache.wink.common.annotations.Scope;
//import org.apache.wink.common.annotations.Scope.ScopeType;

//@Scope(ScopeType.SINGLETON)
@Path("/profile")
public class ProfileResource extends OFBizRestfulBase{
    private final String USER_LOGIN = "userLogin";
    private Map<Object,Object> map = null;
    private  List<String> invalidPostalAddressAttributes;
    private  List<String> invalidCreditCardAttributes;
    private  List<String> validOrderItemAttributes;
    public ProfileResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        //genericValue = null;
        map = FastMap.newInstance();
        invalidPostalAddressAttributes = UtilMisc.toList("countyGeoId", "lastUpdatedStamp", "lastUpdatedTxStamp","createdStamp","createdTxStamp","directions","postalCodeExt","countryGeoId","geoPointId");
        invalidPostalAddressAttributes.add("postalCodeGeoId");
        invalidCreditCardAttributes = UtilMisc.toList("validFromDate", "consecutiveFailedAuths", "lastUpdatedTxStamp","consecutiveFailedNsf","lastFailedNsfDate","lastUpdatedStamp","lastUpdatedTxStamp","createdStamp","createdTxStamp");
        invalidCreditCardAttributes.add("issueNumber");
        invalidCreditCardAttributes.add("contactMechId");
        invalidCreditCardAttributes.add("lastFailedAuthDate");
        validOrderItemAttributes = UtilMisc.toList("quantity", "unitPrice", "itemDescription","comments");
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order/all")
    public Response getAllOrder(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("duration") String duration,  @QueryParam("allOrder") boolean allOrder, @QueryParam("autoShip") boolean autoShip){
        super.initRequestAndDelegator();
        try{
            List<Object> orderList = FastList.newInstance();
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            List<GenericValue> list = delegator.findByAndCache("OrderRole", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER"), UtilMisc.toList("createdTxStamp DESC"));
            
            list = EntityUtil.getRelatedCache("OrderHeader", list);
            Timestamp now = UtilDateTime.nowTimestamp();
            List<EntityCondition> conditionList = FastList.newInstance();
            conditionList.add( EntityCondition.makeCondition("createdTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO,  UtilDateTime.addDaysToTimestamp(now, -Integer.parseInt(duration))));
            conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
            if(!allOrder){
                if(autoShip){
                    conditionList.add(EntityCondition.makeCondition("autoOrderShoppingListId", EntityOperator.NOT_EQUAL, null));
                }
                else{
                    conditionList.add(EntityCondition.makeCondition("autoOrderShoppingListId", EntityOperator.EQUALS, null));
                }
            }
            
            list = EntityUtil.filterByAnd(list, conditionList);
            //list = EntityUtil.filterByCondition(list, EntityCondition.makeCondition("createdTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO,  UtilDateTime.addDaysToTimestamp(now, -Integer.parseInt(duration))));
            //list = EntityUtil.filterByCondition(list, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
            
            for(GenericValue orderHeader : list){
                Map<Object, Object> map = FastMap.newInstance();
                OrderReadHelper helper = OrderReadHelper.getHelper(orderHeader);
                List<GenericValue> orderItemList = helper.getOrderItemsByCondition(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("ORDER_CANCELLED", "ORDER_REJECTED")));
                List<Object> itemList = FastList.newInstance();
                for(GenericValue orderItem : orderItemList){
                    Map<String,Object> orderItemMap = orderItem.getFields(validOrderItemAttributes);
                    String frequency = getFrequency((String)orderItemMap.get("comments"), helper.getOrderId());
                    // replace with freq
                    orderItemMap.remove("comments");
                    orderItemMap.put("frequency",frequency);
                    GenericValue product = orderItem.getRelatedOneCache("Product");
                    String feature = getFeature(product);
                    orderItemMap.put("feature",feature);
                    if(ProductWorker.getVariantVirtualId(product) != null){
                        // brand
                        product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                    }
                    orderItemMap.put("parentProductID", product.getString("productId"));
                    orderItemMap.put("brandName",product.getString("brandName"));
                    orderItemMap.put("smallImageURL", product.getString("smallImageUrl"));
                    itemList.add(orderItemMap);
                }
                GenericValue orderStatus = delegator.findOne("OrderAttribute", UtilMisc.toMap("orderId", helper.getOrderId(), "attrName", "inProcessStatus"), true);
                if(orderStatus != null && orderStatus.getString("attrValue").equals("ORDER_PROCESSING")){
                    // working on order
                    map.put("orderStatus","Processing");
                }
                else{
                    map.put("orderStatus",helper.getCurrentStatusString());
                }
                if(helper.getCurrentStatusString().equals("Completed")){
                    // order completed
                    map.put("completedDate", UtilFormatOut.formatDate(orderHeader.getTimestamp("lastUpdatedTxStamp")));
                }
                else{
                    map.put("completedDate","NA");
                }
                // 
                String trackingCode = "NA";
                
                if(helper.getCurrentStatusString().equalsIgnoreCase("COMPLETED")){
                    List<GenericValue> shipments = orderHeader.getRelated("PrimaryShipment");
                    for(GenericValue shipment : shipments){
                        List<GenericValue> segments = shipment.getRelated("ShipmentPackageRouteSeg");
                        for(GenericValue segment : segments){
                            if(segment.getString("trackingCode") != null){
                                trackingCode = segment.getString("trackingCode");
                                break;
                            }
                        }
                        if(!trackingCode.equals("NA")){
                            break;
                        }
                    }
                }
                map.put("orderID", helper.getOrderId());
                map.put("shipSize", helper.getShippableSizes());
                map.put("orderDate",  RestfulHelper.formatDate(orderHeader.getTimestamp("orderDate")));
                map.put("deliveryEstimate",  RestfulHelper.formatDate(UtilDateTime.addDaysToTimestamp(orderHeader.getTimestamp("orderDate"), 7)));
                map.put("total", helper.getOrderGrandTotal());
                map.put("orderItems", itemList);
                map.put("trackingCode", trackingCode);
                orderList.add(map);
            }
            
            jsonMap.put("orders", orderList);
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Retrieve All Order Info");
            Debug.logError(e, "Unable To Retrieve All Order Info " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order")
    public Response getOrder(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("orderID") String orderID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            Map<String, Object> results = dispatcher.runSync("getOrder", UtilMisc.toMap("orderID", orderID,"userLogin",userLogin));
            if(ServiceUtil.isSuccess(results)){
                jsonMap.put("creditCard", results.get("creditCard"));
                jsonMap.putAll(results);
            }
            else{
                jsonMap.put(ERROR, ServiceUtil.getErrorMessage(results));
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Retrieve Order Info " + orderID);
            Debug.logError(e, "Unable To Retrieve Order Info " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    
    
    
    
    
    
    /**
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/order2")
    public Response getOrder2(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("orderID") String orderID){
        super.initRequestAndDelegator();
        try{
            GenericValue orderHeader = OrderReadHelper.getOrderHeader(delegator, orderID);
            OrderReadHelper helper = OrderReadHelper.getHelper(orderHeader);
            List<GenericValue> paymentPreferenceList = helper.getPaymentPreferences();
            List<Object> orderItemList = FastList.newInstance();
            paymentPreferenceList = EntityUtil.filterByAnd(paymentPreferenceList, UtilMisc.toMap("paymentMethodTypeId", "CREDIT_CARD"));
            for(GenericValue paymentPreference : paymentPreferenceList){
                GenericValue creditCard = paymentPreference.getRelatedOneCache("CreditCard");
                GenericValue clone = (GenericValue)creditCard.clone();
                clone.set("cardNumber",  ContactHelper.formatCreditCard(creditCard, 4, "*"));
                RestfulHelper.removeAttribute(clone, invalidCreditCardAttributes);
                jsonMap.put("creditCard", clone);
                break;
            }
            jsonMap.put("orderDate", UtilFormatOut.formatDate(orderHeader.getTimestamp("orderDate")));
            GenericValue address = helper.getShippingAddress();
            RestfulHelper.removeAttribute(address,invalidPostalAddressAttributes);
            jsonMap.put("shippingAddress",address);
            address = helper.getBillingAddress();
            RestfulHelper.removeAttribute(address,invalidPostalAddressAttributes);
            jsonMap.put("billingAddress",address);
            List<GenericValue> orderItems = helper.getOrderItems();
            
            for(GenericValue orderItem : orderItems){
                Map<Object,Object> map = FastMap.newInstance();
                map.put("price", orderItem.getBigDecimal("unitPrice"));
                map.put("description", orderItem.getString("itemDescription"));
                map.put("quantity",orderItem.getBigDecimal("quantity"));
                String frequency =  orderItem.getString("comments");
                frequency = getFrequency(frequency, orderID);
               
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
                jsonMap.put("orderStatus","Processing");
            }
            else{
                jsonMap.put("orderStatus",helper.getCurrentStatusString());
            }
            if(helper.getCurrentStatusString().equals("Completed")){
                // order completed
                jsonMap.put("completedDate", UtilFormatOut.formatDate(orderHeader.getTimestamp("lastUpdatedTxStamp")));
            }
            else{
                jsonMap.put("completedDate","NA");
            }
            jsonMap.put("total",  helper.getOrderGrandTotal());
            jsonMap.put("orderID", orderID);
            jsonMap.put("items", orderItemList);
            jsonMap.put("tax", helper.getTaxTotal());
            jsonMap.put("adjustments",  OrderReadHelper.calcOrderPromoAdjustmentsBd(helper.getAdjustments()));
            jsonMap.put("deliveryEstimate",  UtilFormatOut.formatDate(  UtilDateTime.addDaysToTimestamp(orderHeader.getTimestamp("orderDate"), 7)));
            jsonMap.put("shipping", helper.getShippingTotal());
            jsonMap.put("subTotal", helper.getOrderItemsSubTotal());
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Retrieve Order Info " + orderID);
            Debug.logError(e, "Unable To Retrieve Order Info " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    **/
    
    @GET
    @Produces("application/json")
    @Path("/create/shipping/address")
    public Response createShippingProfile(@QueryParam("jsoncallback") String callbackFunction){
        try{
            super.initRequestAndDelegator();
            FastMap<String, Object> addressMap = FastMap.newInstance();
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            String fullName =  request.getParameter("firstName") +  " " + request.getParameter("lastName");
            GenericValue person = userLogin.getRelatedOneCache("Person");
            
            if(person.getString("firstName") == null || person.getString("lastName") == null){
                dispatcher.runAsync("updatePerson", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "firstName", request.getParameter("firstName"), "lastName", request.getParameter("lastName"), "userLogin",userLogin));
            }
            
            
            String productStoreID = ProductStoreWorker.getProductStoreId(request);
            addressMap.put("userLogin", userLogin);
            addressMap.put("address1", request.getParameter("address1"));
            addressMap.put("address2", request.getParameter("address2"));
            addressMap.put("city", request.getParameter("city"));
            addressMap.put("stateProvinceGeoId", request.getParameter("state"));
            addressMap.put("postalCode", request.getParameter("postalCode"));
            addressMap.put("countryGeoId", "USA");
            addressMap.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
            addressMap.put("locale", request.getLocale());
            addressMap.put("attnName", fullName);
            addressMap.put("toName", fullName);
            FastMap<String, Object> phoneMap = FastMap.newInstance();
            phoneMap.put("countryCode", "1");
            phoneMap.put("areaCode", request.getParameter("areaCode"));
            phoneMap.put("contactNumber", request.getParameter("phoneNumber"));
            phoneMap.put("locale", request.getLocale());
            phoneMap.put("userLogin", userLogin);
            phoneMap.put("contactMechPurposeTypeId", "PHONE_SHIPPING");
           
          
            Map<String,Object> result = RestfulHelper.createAddress(addressMap, phoneMap, productStoreID, delegator, dispatcher);
            
            if(ServiceUtil.isError(result)){
                jsonMap.put(ERROR, "Unable To Add Address");
                Debug.logError(result.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
            else{
                jsonMap.put("contactMechID", result.get("contactMechID"));
                dispatcher.runAsync("setPartyProfileDefaults", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "userLogin",userLogin,"defaultShipAddr",result.get("contactMechID"),"productStoreId",productStoreID));
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Add Shipping Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    @GET
    @Produces("application/json")
    @Path("/update/billing")
    public Response updateBillingProfile(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        try{
            
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            String productStoreID = ProductStoreWorker.getProductStoreId(request);
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("firstNameOnCard", request.getParameter("firstName"));
            context.put("lastNameOnCard", request.getParameter("lastName"));
            context.put("address1", request.getParameter("address1"));
            context.put("address2", request.getParameter("address2"));
            context.put("city", request.getParameter("city"));
            context.put("stateProvinceGeoId", request.getParameter("state"));
            context.put("postalCode", request.getParameter("postalCode"));
            context.put("countyGeoId", "USA");
            context.put("cardNumber", request.getParameter("cardNumber"));
            context.put("expireDate",  request.getParameter("expireDate"));
            context.put("cardType", request.getParameter("cardType"));
            context.put("productStoreId", productStoreID);
            context.put("areaCode", request.getParameter("areaCode"));
            context.put("contactNumber", request.getParameter("phoneNumber"));
            context.put("contactMechPurposeTypeId", "PHONE_BILLING");
            context.put("telephoneID", request.getParameter("telephoneID"));
            context.put("paymentMethodID", request.getParameter("paymentMethodID"));
            context.put("postalAddressID", request.getParameter("postalAddressID"));
            
            Map<String,Object> results = dispatcher.runSync("updateBillingProfile", context);
            
           
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Update Billing Profile");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Update Billing Profile");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    
    
    @GET
    @Produces("application/json")
    @Path("/delete/billing")
    public Response deleteBillingProfile(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        try{
            
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
          
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("postalAddressID", request.getParameter("postalAddressID"));
            context.put("telephoneID", request.getParameter("telephoneID"));
            context.put("paymentMethodID", request.getParameter("paymentMethodID"));
            
            Map<String,Object> results = dispatcher.runSync("deleteBillingInfo", context);
            
           
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Update Billing Profile");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Update Billing Profile");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/create/billing/address")
    public Response createBillingProfile(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        try{
            
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            String fullName =  request.getParameter("firstName") +  " " + request.getParameter("lastName");
            String productStoreID = ProductStoreWorker.getProductStoreId(request);
            //String expireDate = request.getParameter("expMonth") + "/" + request.getParameter("expYear");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("firstNameOnCard", request.getParameter("firstName"));
            context.put("lastNameOnCard", request.getParameter("lastName"));
            context.put("address1", request.getParameter("address1"));
            context.put("address2", request.getParameter("address2"));
            context.put("city", request.getParameter("city"));
            context.put("stateProvinceGeoId", request.getParameter("state"));
            context.put("postalCode", request.getParameter("postalCode"));
            context.put("countyGeoId", "USA");
            context.put("cardNumber", request.getParameter("cardNumber"));
            context.put("expireDate",  request.getParameter("expireDate"));
            context.put("cardType", request.getParameter("cardType"));
            context.put("locale", request.getLocale());
            context.put("productStoreId", productStoreID);
            context.put("toName", fullName);
            
            
            boolean beganTransaction = TransactionUtil.begin();
            Map<String,Object>  result = dispatcher.runSync("createCreditCardAndAddress", context);
            if(ServiceUtil.isSuccess(result)){
                String contactMechID = (String) result.get("contactMechId");
                String paymentMethodID = (String) result.get("paymentMethodId");
                jsonMap.put("contactMechID", contactMechID);
                jsonMap.put("paymentMethodID", paymentMethodID);
                FastMap<String, Object> phoneMap = FastMap.newInstance();
                phoneMap.put("countryCode", "1");
                phoneMap.put("areaCode", request.getParameter("areaCode"));
                phoneMap.put("contactNumber", request.getParameter("phoneNumber"));
                phoneMap.put("locale", request.getLocale());
                phoneMap.put("userLogin", request.getSession().getAttribute("userLogin"));
                phoneMap.put("contactMechPurposeTypeId", "PHONE_BILLING");
                result = dispatcher.runSync("createPartyTelecomNumber", phoneMap);
                if (ServiceUtil.isSuccess(result)){
                    result = dispatcher.runSync("createContactMechLink",UtilMisc.toMap("contactMechIdFrom", contactMechID,"userLogin",userLogin,"contactMechIdTo", result.get("contactMechId")));
                    if (ServiceUtil.isSuccess(result)){
                        dispatcher.runAsync("setPartyProfileDefaults", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "userLogin", userLogin,"defaultBillAddr",contactMechID,"defaultPayMeth",paymentMethodID, "productStoreId",productStoreID));
                    }
                }
            }
           
            if(ServiceUtil.isError(result)){
                jsonMap.put(ERROR, "Unable To Add Credit Card");
                Debug.logError(result.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
            else{
                TransactionUtil.commit(beganTransaction);
            }
            
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Add Credit Card");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            RestfulHelper.rollBack();
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    public Response getInfo(@QueryParam("jsoncallback") String callbackFunction){
        GenericValue login = (GenericValue)request.getSession().getAttribute(USER_LOGIN);
        if(login == null){
            jsonMap.put(ERROR, ERROR);
        }
        else{
            try{
                super.initRequestAndDelegator();
                String partyID;
                List<GenericValue> list;
                Map<String,Object> telephoneMap  = FastMap.newInstance();
                partyID = login.getString("partyId");
                GenericValue person  = login.getRelatedOneCache("Person");
                
               String productStoreID =  ProductStoreWorker.getProductStoreId(request);
                map.put("firstName", person.getString("firstName"));
                map.put("lastName", person.getString("lastName"));
                map.put("middleName", person.getString("middleName"));
                map.put("partyID", person.getString("partyId"));
                
                GenericValue partyAttribute = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", person.getString("partyId"), "attrName", "referAFriendCode"), true);
                if(partyAttribute == null){
                    map.put("referralCode", "");
                }
                else{
                    map.put("referralCode", partyAttribute.getString("attrValue"));
                }
                Collection<GenericValue> emails = org.ofbiz.party.contact.ContactHelper.getContactMech(person.getRelatedOne("Party"), "PRIMARY_EMAIL", "EMAIL_ADDRESS", false);
                
                GenericValue email = EntityUtil.getFirst(UtilMisc.toList(emails));
                map.put("email", email.getString("infoString"));
                map.put("emailContactMechID", email.getString("contactMechId"));
                jsonMap.put("person", map);
                map = FastMap.newInstance();
         
                GenericValue partyDefaultProfile = delegator.findByPrimaryKeyCache("PartyProfileDefault", UtilMisc.toMap("partyId",partyID, "productStoreId", productStoreID));
                if(partyDefaultProfile != null){
                    if(partyDefaultProfile.getString("defaultShipAddr") != null){
                        List<GenericValue> partyContactMechList = delegator.findByAndCache("PartyContactMech", UtilMisc.toMap("partyId",partyID, "contactMechId", partyDefaultProfile.getString("defaultShipAddr")),null);
                        partyContactMechList = EntityUtil.filterByDate(partyContactMechList);
                        
                        if(UtilValidate.isNotEmpty(partyContactMechList)){
                            GenericValue partyContactMech = EntityUtil.getFirst(partyContactMechList);
                            GenericValue defaultShippingAddress = partyContactMech.getRelatedOneCache("PostalAddress");
                            GenericValue telephone  = ContactHelper.getPostalAddressPhone(delegator, defaultShippingAddress, "PHONE_SHIPPING", partyID);
                            Map<String, Object> temp = FastMap.newInstance();
                            temp.put("areaCode", "XXX");
                            temp.put("phoneNumber","XXX-XXXX");
                            temp.put("telephoneContactMechID", "NA");
                            if(telephone != null){
                                temp.put("areaCode", telephone.getString("areaCode") == null ? "XXX" : telephone.getString("areaCode"));
                                temp.put("phoneNumber",telephone.getString("contactNumber") == null ? "XXX-XXXX" : telephone.getString("contactNumber"));
                                temp.put("telephoneContactMechID", telephone.getString("contactMechId"));
                            }
                            telephoneMap.put(defaultShippingAddress.getString("contactMechId"), temp);
                            RestfulHelper.removeAttribute(defaultShippingAddress, invalidPostalAddressAttributes);
                            jsonMap.put("defaultShippingAddress", defaultShippingAddress);
                        }
                    }
                    if(partyDefaultProfile.getString("defaultPayMeth") != null){
                        
                        List<GenericValue> paymentMethodList = delegator.findByAndCache("PaymentMethod", UtilMisc.toMap("partyId",partyID, "paymentMethodId", partyDefaultProfile.getString("defaultPayMeth")),null);
                        paymentMethodList = EntityUtil.filterByDate(paymentMethodList);

                        if(UtilValidate.isNotEmpty(paymentMethodList)){
                            GenericValue paymentMethod = EntityUtil.getFirst(paymentMethodList);
                            GenericValue creditCard = paymentMethod.getRelatedOneCache("CreditCard");
                            GenericValue defaultBillingAddress = creditCard.getRelatedOneCache("PostalAddress");
                            GenericValue clone = (GenericValue)creditCard.clone();
                            GenericValue telephone = ContactHelper.getBillingAddrPhone(delegator, defaultBillingAddress, partyID);
                            
                            
                            Map<String, Object> temp = FastMap.newInstance();
                            temp.put("areaCode", "XXX");
                            temp.put("phoneNumber","XXX-XXXX");
                            temp.put("telephoneContactMechID", "NA");
                            if(telephone != null){
                                temp.put("areaCode", telephone.getString("areaCode") == null ? "XXX" : telephone.getString("areaCode"));
                                temp.put("phoneNumber",telephone.getString("contactNumber") == null ? "XXX-XXXX" : telephone.getString("contactNumber"));
                                temp.put("telephoneContactMechID", telephone.getString("contactMechId"));
                            }
                            telephoneMap.put(defaultBillingAddress.getString("contactMechId"), temp);
                            clone.set("cardNumber",  ContactHelper.formatCreditCard(creditCard, 4, "*"));
                            RestfulHelper.removeAttribute(defaultBillingAddress, invalidPostalAddressAttributes);
                            RestfulHelper.removeAttribute(clone, invalidCreditCardAttributes);
                            jsonMap.put("defaultBillingAddress", defaultBillingAddress);   
                            jsonMap.put("defaultCreditCard", clone);
                        }
                    }
                }
                /**
                 * all valid shipping addresses
                 */
                GenericValue party = login.getRelatedOne("Party");
                List<GenericValue> contactMechList = (List<GenericValue>) org.ofbiz.party.contact.ContactHelper.getContactMech(party, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
                list = EntityUtil.getRelatedCache("PostalAddress", contactMechList);
                List<GenericValue> shippingAddressList = FastList.newInstance();
                for(GenericValue postalAddress : list){
                    // all shipping addresses
                    GenericValue telephone = ContactHelper.getPostalAddressPhone(delegator, postalAddress, "PHONE_SHIPPING", partyID);
                    
                    Map<String, Object> temp = FastMap.newInstance();
                    temp.put("areaCode", "XXX");
                    temp.put("phoneNumber","XXX-XXXX");
                    temp.put("telephoneContactMechID", "NA");
                    if(telephone != null){
                        temp.put("areaCode", telephone.getString("areaCode") == null ? "XXX" : telephone.getString("areaCode"));
                        temp.put("phoneNumber",telephone.getString("contactNumber") == null ? "XXX-XXXX" : telephone.getString("contactNumber"));
                        temp.put("telephoneContactMechID", telephone.getString("contactMechId"));
                    }
                    telephoneMap.put(postalAddress.getString("contactMechId"), temp);
                    RestfulHelper.removeAttribute(postalAddress, invalidPostalAddressAttributes);
                    shippingAddressList.add(postalAddress);
                }
                
                jsonMap.put("shippingAddressList", shippingAddressList);   
                if(jsonMap.get("defaultShippingAddress") == null && shippingAddressList.size() > 0){
                    // no default shipping address
                    GenericValue defaultShippingAddress = EntityUtil.getFirst(shippingAddressList);
                    jsonMap.put("defaultShippingAddress", defaultShippingAddress);
                }
                
                list = delegator.findByAndCache("PaymentMethod", UtilMisc.toMap("partyId", partyID,  "paymentMethodTypeId", "CREDIT_CARD"));
                list = EntityUtil.filterByDate(list);
                list = EntityUtil.getRelatedCache("CreditCard", list);
                List<Object> billingInfoList = FastList.newInstance();
                for(GenericValue creditCard : list){
                    map = FastMap.newInstance();
                    GenericValue billingAddress = creditCard.getRelatedOneCache("PostalAddress");
                    GenericValue clone = (GenericValue)creditCard.clone();
                    clone.set("cardNumber",  ContactHelper.formatCreditCard(creditCard, 4, "*"));
                    RestfulHelper.removeAttribute(clone, invalidCreditCardAttributes);
                    RestfulHelper.removeAttribute(billingAddress, invalidPostalAddressAttributes);
                    GenericValue telephone = ContactHelper.getBillingAddrPhone(delegator, billingAddress, partyID);
                    
                    
                    Map<String, Object> temp = FastMap.newInstance();
                    temp.put("areaCode", "XXX");
                    temp.put("phoneNumber","XXX-XXXX");
                    temp.put("telephoneContactMechID", "NA");
                    if(telephone != null){
                        temp.put("areaCode", telephone.getString("areaCode") == null ? "XXX" : telephone.getString("areaCode"));
                        temp.put("phoneNumber",telephone.getString("contactNumber") == null ? "XXX-XXXX" : telephone.getString("contactNumber"));
                        temp.put("telephoneContactMechID", telephone.getString("contactMechId"));
                    }
                    telephoneMap.put(billingAddress.getString("contactMechId"), temp);
                    map.put("creditCard", clone);
                    map.put("billingAddress",billingAddress);
                    billingInfoList.add(map);
                }
                jsonMap.put("billingInfoList", billingInfoList);
                if(jsonMap.get("defaultCreditCard") == null && list.size() > 0){
                    GenericValue creditCard = EntityUtil.getFirst(list);
                    GenericValue defaultBillingAddress = creditCard.getRelatedOneCache("PostalAddress");
                    jsonMap.put("defaultBillingAddress", defaultBillingAddress);   
                    GenericValue clone = (GenericValue)creditCard.clone();
                    clone.set("cardNumber",  ContactHelper.formatCreditCard(creditCard, 4, "*"));
                    RestfulHelper.removeAttribute(clone, invalidCreditCardAttributes);
                    RestfulHelper.removeAttribute(defaultBillingAddress, invalidPostalAddressAttributes);
                    jsonMap.put("defaultCreditCard", clone);
                }
                
                HttpSession session = request.getSession();
                if(session.getAttribute("giftCardFinAccountId") != null){
                    // gift card exists
                    map = FastMap.newInstance();
                    map.put("code", session.getAttribute("giftCardNumber"));
                    map.put("amount", session.getAttribute("giftCardBalance"));
                    map.put("description", session.getAttribute("giftCardName"));
                    map.put("productPromoCode", session.getAttribute("giftCardFinAccountId"));
                    jsonMap.put("giftCard", map);
                }
                jsonMap.put("telephoneNumbers", telephoneMap);
            }
            catch(Exception e){
                jsonMap.clear();
                jsonMap.put(ERROR, ERROR);
                Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
            }
            finally{
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
            }
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    private Map<String,Object> setPartyDefaultSetting(Map<String,Object> context) throws Exception {
        super.initRequestAndDelegator();
        GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        context.put("userLogin", userLogin);
        context.put("partyId", userLogin.getString("partyId"));
        context.put("productStoreId", ProductStoreWorker.getProductStoreId(request));
        return dispatcher.runSync("setPartyProfileDefaults",context);
        
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/default/shipping/address")
    public Response setDefaultShippingAddress(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("contactMechID") String contactMechID){
        try{
            FastMap<String, Object> context = FastMap.newInstance();
            context.put("defaultShipAddr", contactMechID);
            Map<String,Object> results = setPartyDefaultSetting(context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Set Default Shipping Address " + contactMechID);
                Debug.logError( ServiceUtil.getErrorMessage(results) + " contactMechID " + contactMechID, MODULE);   
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Set Default Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/default/billing/address")
    public Response setDefaultBillingAddress(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("contactMechID") String contactMechID){
        try{
            FastMap<String, Object> context = FastMap.newInstance();
            context.put("defaultBillAddr", contactMechID);
            Map<String,Object> results = setPartyDefaultSetting(context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Set Default Billing Address " + contactMechID);
                Debug.logError( ServiceUtil.getErrorMessage(results) + " contactMechID " + contactMechID, MODULE);   
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Set Default Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/default/billing/info")
    public Response setDefaultBillingInfo(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("contactMechID") String contactMechID,  @QueryParam("paymentID") String paymentID){
        try{
            FastMap<String, Object> context = FastMap.newInstance();
            context.put("defaultBillAddr", contactMechID);
            boolean beganTransaction = TransactionUtil.begin();
            Map<String,Object> results = setPartyDefaultSetting(context);
            if(ServiceUtil.isSuccess(results)){
                context.clear();
                context.put("defaultPayMeth", paymentID);
                results = setPartyDefaultSetting(context);
            }
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Set Default Billing Info");
                Debug.logError( ServiceUtil.getErrorMessage(results) + " context " + context, MODULE);   
            }
            else{
                TransactionUtil.commit(beganTransaction);
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Set Default Billing Info");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            RestfulHelper.rollBack();
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/update/address")
    public Response updateAddress(@QueryParam("jsoncallback") String callbackFunction){
        try{
            super.initRequestAndDelegator();
            FastMap<String, Object> addressMap = FastMap.newInstance();
            FastMap<String, Object> map = FastMap.newInstance();
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            String fullName =  request.getParameter("firstName") +  " " + request.getParameter("lastName");
            map.put("userLogin", userLogin);
            map.put("partyId", userLogin.getString("partyId"));
            map.put("contactMechId", request.getParameter("contactMechID"));
            
            
            String productStoreID = ProductStoreWorker.getProductStoreId(request);
            addressMap.put("userLogin", userLogin);
            addressMap.put("address1", request.getParameter("address1"));
            addressMap.put("address2", request.getParameter("address2"));
            addressMap.put("city", request.getParameter("city"));
            addressMap.put("stateProvinceGeoId", request.getParameter("state"));
            addressMap.put("postalCode", request.getParameter("postalCode"));
            addressMap.put("countryGeoId", "USA");
            addressMap.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
            addressMap.put("locale", request.getLocale());
            addressMap.put("attnName", fullName);
            addressMap.put("toName", fullName);
            
            FastMap<String, Object> phoneMap = FastMap.newInstance();
            phoneMap.put("countryCode", "1");
            phoneMap.put("areaCode", request.getParameter("areaCode"));
            phoneMap.put("contactNumber", request.getParameter("phoneNumber"));
            phoneMap.put("locale", request.getLocale());
            phoneMap.put("userLogin", userLogin);
            phoneMap.put("contactMechPurposeTypeId", "PHONE_SHIPPING");
            phoneMap.put("partyId", userLogin.getString("partyId"));
            
            
            Debug.logInfo(addressMap + " ", MODULE);
            Debug.logInfo(phoneMap + " ", MODULE);
            
            boolean beganTransaction = TransactionUtil.begin();
            // delete old  address
            Map<String,Object> results = dispatcher.runSync("deletePartyContactMech",map);
            if(ServiceUtil.isSuccess(results)){
                // delete old phone
                String telephoneContactMechID = request.getParameter("telephoneContactMechID");
                if(!telephoneContactMechID.equals("NA")){
                    map.put("contactMechId", telephoneContactMechID);
                    results = dispatcher.runSync("deletePartyContactMech",map);
                    if(ServiceUtil.isError(results)){
                        throw new Exception("Unable To Delete Telephone Contact Mech " + ServiceUtil.getErrorMessage(results));
                    }
                }
                TransactionUtil.commit(beganTransaction);
                results = RestfulHelper.createAddress(addressMap, phoneMap, productStoreID, delegator, dispatcher);
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Update Shipping Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            RestfulHelper.rollBack();
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    
    
    @GET
    @Produces("application/json")
    @Path("/delete/address")
    public Response deleteAddress(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("contactMechID") String contactMechID, @QueryParam("telephoneContactMechID") String telephoneContactMechID){
        try{
            super.initRequestAndDelegator();
            FastMap<String, Object> map = FastMap.newInstance();
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            map.put("userLogin", userLogin);
            map.put("partyId", userLogin.getString("partyId"));
            map.put("contactMechId", contactMechID);
            
            boolean beganTransaction = TransactionUtil.begin();
            // delete old  address
            Map<String,Object> results = dispatcher.runSync("deletePartyContactMech",map);
            if(ServiceUtil.isSuccess(results)){
                // delete old phone
                if(!telephoneContactMechID.equals("NA")){
                    map.put("contactMechId", telephoneContactMechID);
                    results = dispatcher.runSync("deletePartyContactMech",map);
                }
            }
            
            if(ServiceUtil.isSuccess(results)){
                TransactionUtil.commit(beganTransaction);
            }
            else{
                jsonMap.put(ERROR, "Unable To Delete Shipping Address");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Delete Shipping Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            RestfulHelper.rollBack();
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    @GET
    @Produces("application/json")
    @Path("/update/profile")
    public Response updateProfile(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("email") String email, @QueryParam("contactMechID") String contactMechID){
        try{
            super.initRequestAndDelegator();
            GenericValue login = (GenericValue)request.getSession().getAttribute(USER_LOGIN);
            GenericValue system = RestfulHelper.getSystemLogin(delegator);
            boolean transStatus = TransactionUtil.begin();
            Map<String, Object> results = dispatcher.runSync("updatePerson", UtilMisc.toMap("partyId", login.getString("partyId"), "userLogin",  system, "firstName", firstName, "lastName", lastName));
            if(!ServiceUtil.isSuccess(results)){
                jsonMap.put(ERROR, "Unable To Update Profile Info");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
            else{
                results = dispatcher.runSync("updatePartyEmailAddress", UtilMisc.toMap("partyId", login.getString("partyId"), "userLogin",  system, "emailAddress", email, "contactMechId", contactMechID, "contactMechTypeId", "EMAIL_ADDRESS"));
                if(ServiceUtil.isSuccess(results)){
                    TransactionUtil.commit(transStatus);
                }
                else{
                    Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
                }
            }
            RestfulHelper.rollBack();
        }
        catch (Exception e){
            RestfulHelper.rollBack();
            jsonMap.clear();
            jsonMap.put(ERROR, ERROR);
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
           
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/update/password")
    public Response updatePassword(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("password") String password, @QueryParam("currentPassword") String currentPassword){
        try{
            super.initRequestAndDelegator();
            GenericValue login = (GenericValue)request.getSession().getAttribute(USER_LOGIN);
            Map<String, Object> results = dispatcher.runSync("updatePassword", UtilMisc.toMap("currentPassword", currentPassword, "userLoginId", login.getString("userLoginId"), "userLogin", login, "newPassword", password, "newPasswordVerify", password));
            if(!ServiceUtil.isSuccess(results)){
                jsonMap.put(ERROR, "Unable To Update Password");
                Debug.logError(ServiceUtil.getErrorMessage(results), MODULE);
            }
        }
        catch (Exception e){
            jsonMap.clear();
            jsonMap.put(ERROR, ERROR);
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    private String getFrequency(String frequency, String orderID) throws Exception {          
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
    private String getFeature(GenericValue product) throws Exception {
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
}