package com.ilinks.restful.checkout;


import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
//import org.ofbiz.ecommerce.checkout.EcommerceCheckoutEvents;
import org.ofbiz.entity.GenericValue;
import javax.servlet.http.HttpSession;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;

@Path("/checkout")
public class CheckOutResource extends OFBizRestfulBase{
    private ShoppingCart cart;
    private final String EMPTY_CART;
    private String UNABLE_TO_INTIALIZE_CART;
    private String CHECK_PAYMENT_ERROR;
    /**
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
    public CheckOutResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        cart = null;
        EMPTY_CART = properties.getProperty("EMPTY_CART");
        UNABLE_TO_INTIALIZE_CART = properties.getProperty("UNABLE_TO_INTIALIZE_CART");
        UNABLE_TO_INTIALIZE_CART = properties.getProperty("UNABLE_TO_INTIALIZE_CART");
        CHECK_PAYMENT_ERROR = properties.getProperty("CHECK_PAYMENT_ERROR");
    }
    
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkout(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        try{
            cart = ShoppingCartEvents.getCartObject(request);
            Debug.logError("=====> Start Cart Order ID " + cart.getOrderId(), MODULE);
            if("error".equals(CheckOutEvents.cartNotEmpty(request, response))){
                jsonMap.put(ERROR, "Cart Is Empty");
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            /**
             * need to include a check for new payment and shipping address
             */
            
            
            if("error".equals(CheckOutEvents.setCheckOutOptions(request, response))){
                // add gc and cc as a payment method to the cart
                jsonMap.put(ERROR, "Unable To Add Payment Method");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
           
            if ("error".equals(CheckOutEvents.checkPaymentMethods(request, response))) {
                // make sure the cc + gc can cover the amount
                // does not process the cc
                jsonMap.put(ERROR, "Unable To Verify Payment Method");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            // need to add userLogin to cart
            HttpSession session = request.getSession();
            GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
            cart.setUserLogin(userLogin, dispatcher);
            if ("error".equals(CheckOutEvents.createOrder(request, response))) {
                jsonMap.put(ERROR, "Unable To Create Order");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            /**
             * really not needed, black list is not set up
             * always success
             */
            String result = CheckOutEvents.checkOrderBlacklist(request, response);
            if ("failed".equals(result)) {
              CheckOutEvents.failedBlacklistCheck(request, response);
            }
            if ("error".equals(result)) {
                jsonMap.put(ERROR, "Order Has Been BlackListed");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            if (!CheckOutEvents.processPayment(request, response).equals("success")) {
                jsonMap.put(ERROR, "Unable To Process Payment");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            /**
             * gc as promotion -- approve order with zero balance
             */
            /*
            if ("error".equals(EcommerceCheckoutEvents.approveOrderOnZeroTotal(request, response))) {
                jsonMap.put(ERROR, "Unable To Generate GC AS Promotion " + cart.getOrderId());
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            
            
            if ("error".equals(EcommerceCheckoutEvents.createAutoship(request, response))) {
                jsonMap.put(ERROR, "Unable To Create AutoShip List");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
            }
            */
            
            /**
             * set default shipping/billing address and payment method
             */
            String partyID = userLogin.getString("partyId");
            Map<String,Object> context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("defaultShipAddr", request.getParameter("shipping_contact_mech_id"));
            context.put("defaultPayMeth",request.getParameter("checkOutPaymentId"));
            context.put("defaultBillAddr",cart.getBillingAddress().getString("contactMechId"));
            context.put("productStoreId",cart.getProductStoreId());
            context.put("partyId", partyID);
            Debug.logError("context " + context, MODULE);
            dispatcher.runAsync("setPartyProfileDefaults", context);
            /*
            if ("error".equals(EcommerceCheckoutEvents.createReferAFriendPayment(request, response))) {
                jsonMap.put(ERROR, "Unable To Create Refer A Friend");
                if(request.getAttribute("_ERROR_MESSAGE_") != null){
                    Debug.logError(request.getAttribute("_ERROR_MESSAGE_") + "", MODULE);
                }
            }
           */
            //EcommerceCheckoutEvents.storeUTMSourceOnCheckout(request, response);
            // stores gc balance within order attribute
            //EcommerceCheckoutEvents.storeOrderData(request, response);
            
            /**
             * initial freq will be stored in the comments
             */
           
            for(ShoppingCartItem item : cart.items()){
                GenericValue orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", cart.getOrderId(), "orderItemSeqId", item.getOrderItemSeqId()), false);
                //orderItem.set("comments",item.getFrequency().replace("w", ""));
                orderItem.store();
            }
            
            
            /**
             * save fedex shipping cost in attribute
             */
            context.clear();
            /**
            GenericValue shippingAddress = cart.getShippingAddress();
            context.put("orderID", cart.getOrderId());
            context.put("address", shippingAddress.getString("address1"));
            context.put("city", shippingAddress.getString("city"));
            context.put("state",  shippingAddress.getString("stateProvinceGeoId"));
            context.put("postalCode",shippingAddress.getString("postalCode"));
            context.put("country", "US");
            BigDecimal totalShippingWeight = BigDecimal.ZERO;
            for(ShoppingCartItem item : cart.items()){
                totalShippingWeight = totalShippingWeight.add(item.getWeight()); 
            }
            context.put("weight", totalShippingWeight.toString());
            dispatcher.runAsync("calculateStoreShippingCost", context);
           **/
            /**
            try{
                
                String SHIPMENT_PROPERTIES = "shipment.properties";
                String accountNumber = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.accountNbr");
                String meterNumber = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.meterNumber");
                String fedExKey= UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.userCredential.key");
                String password = UtilProperties.getPropertyValue(SHIPMENT_PROPERTIES, "shipment.fedex.access.userCredential.password");
                String shipperAddress = "31260 Cedar Valley Drive";
                String shipperCity = "Westlake Village";
                String shipperState = "CA";
                String shipperPostalCode = "91362";
                

                //FedexShippingRate rate = new FedexShippingRate(accountNumber, meterNumber, fedExKey, password,shipperAddress, shipperCity,shipperState,shipperPostalCode,"US");
                //RequestedShipment requestedShipment = rate.getRequestShipment();
                //context.clear();
                //GenericValue shippingAddress = cart.getShippingAddress();
                //context.put("orderID", cart.getOrderId());
                //context.put("address", shippingAddress.getString("address1"));
                //context.put("city", shippingAddress.getString("city"));
                //context.put("state",  shippingAddress.getString("stateProvinceGeoId"));
                //context.put("postalCode",shippingAddress.getString("postalCode"));
                //context.put("country", "US");
                
                //Party recipient =  rate.createRecipientAddress(shippingAddress.getString("address1"), shippingAddress.getString("city"), shippingAddress.getString("stateProvinceGeoId"), shippingAddress.getString("postalCode"), "US");
                
                //BigDecimal totalShippingWeight = BigDecimal.ZERO;
                for(ShoppingCartItem item : cart.items()){
                    totalShippingWeight = totalShippingWeight.add(item.getWeight()); 
                }
                //context.put("weight", totalShippingWeight.toString());
                
                //requestedShipment.setRecipient(recipient);
                //rate.setWeight(totalShippingWeight.toString());
                
                //Map<Object,Object> charges = rate.getShippingRate();
                
                //GenericValue orderAttribute = delegator.makeValue("OrderAttribute");
                
                //orderAttribute.set("orderId", cart.getOrderId());
                //orderAttribute.set("attrName", "totalShipingCost");
                //orderAttribute.set("attrValue", charges.get("totalNetCharges").toString());
                //orderAttribute.create();
                
                //orderAttribute = delegator.makeValue("OrderAttribute");
                //orderAttribute.set("orderId", cart.getOrderId());
                //orderAttribute.set("attrName", "totalShippingSurCharges");
                //orderAttribute.set("attrValue", charges.get("totalSurCharges").toString());
                //orderAttribute.create();
                
                //Debug.log("---------> before calculateStoreShippingCost " + context);
                //dispatcher.runAsync("calculateStoreShippingCost", context);
                //Debug.log("---------> after calculateStoreShippingCost");
            }
            catch (Exception e){
                Debug.logError("Unable To Find Calculate FedEx Shipping Cost " + e.getMessage(), MODULE);
            }
            **/
            Map<String, Object> serviceContext = FastMap.newInstance();
            serviceContext.put("orderId", cart.getOrderId());
            serviceContext.put("userLogin", userLogin);
            dispatcher.runAsync("sendOrderConfirmation", serviceContext);
           
            jsonMap.put("orderID", cart.getOrderId());
            ShoppingCartEvents.clearCart(request, response);
            //EcommerceCheckoutEvents.clearCheckoutParameters(request, response);
            
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Check Out");
            Debug.logError(e, e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/isCartEmpty")
    public Response isCartEmpty(@QueryParam("jsoncallback") String callbackFunction){
        super.initRequestAndDelegator();
        cart = ShoppingCartEvents.getCartObject(request);
        if (cart != null && UtilValidate.isNotEmpty(cart.items())) {
            jsonMap.put(SUCCESS, SUCCESS);
        } else {
            jsonMap.put(ERROR,EMPTY_CART);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/initializeCart")
    public Response initiallizeCart(
            @QueryParam("jsoncallback") String callbackFunction){
        try{
            super.initRequestAndDelegator();
            if(CheckOutEvents.setCheckOutOptions(request, response).equals(ERROR)){
                jsonMap.put(ERROR, UNABLE_TO_INTIALIZE_CART);
            }
            else{
                jsonMap.put(SUCCESS, SUCCESS);
            }
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        catch(Exception e){
            jsonMap.put(ERROR, UNABLE_TO_INTIALIZE_CART);
            Debug.logError(e, e.getMessage(), MODULE);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/checkPayment")
    public Response checkPayment(
            @QueryParam("jsoncallback") String callbackFunction){
        try{
            super.initRequestAndDelegator();
            if(CheckOutEvents.checkPaymentMethods(request, response).equals(ERROR)){
                jsonMap.put(ERROR, CHECK_PAYMENT_ERROR);
            }
            else{
                jsonMap.put(SUCCESS, SUCCESS);
            }
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        catch(Exception e){
            jsonMap.put(ERROR, CHECK_PAYMENT_ERROR);
            Debug.logError(e, e.getMessage(), MODULE);
        }
        return Response.ok(jsonStr).type(MediaType.APPLICATION_JSON).build();
    }
}