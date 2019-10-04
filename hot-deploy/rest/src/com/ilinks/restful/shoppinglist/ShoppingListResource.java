package com.ilinks.restful.shoppinglist;



import java.math.BigDecimal;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;
//import org.apache.wink.common.annotations.Scope;
//import org.apache.wink.common.annotations.Scope.ScopeType;

//@Scope(ScopeType.SINGLETON)
@Path("/shoppingList")
public class ShoppingListResource extends OFBizRestfulBase{
    
    public ShoppingListResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
    }
    
    @GET
    @Produces("application/json")
    public Response getShoppingListInfo(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            
            Map<String,Object> results = dispatcher.runSync("retrieveShoppingListInfo", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Retrieve Shopping List");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
            jsonMap.put("shoppingList", results.get("shoppingListInfo"));
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Retrive Shopping List");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/update/nextOrderDate")
    
    public Response updateNextDeliveryDate(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID, @QueryParam("orderDateMilliSeconds") String orderDateMilliSeconds){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListID", shoppingListID);
            context.put("startDateTime", UtilDateTime.getTimestamp(orderDateMilliSeconds));
            Debug.log("startDateTime " +  UtilDateTime.getTimestamp(orderDateMilliSeconds));
            Map<String,Object> results = dispatcher.runSync("updateShoppingListNextOrderDate", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Change Next Delivery Date");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Change Next Delivery Date");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/shipItNow")
    
    public Response shipItNow(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListId", shoppingListID);
            Map<String,Object> results = dispatcher.runSync("createOrderFromShoppingList", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Ship It Now");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Ship It Now");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/update/frequency")
    
    public Response updateShoppingListFrequency(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID, @QueryParam("frequency") String frequency){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListID", shoppingListID);
            context.put("frequency", Long.parseLong(frequency));
            Map<String,Object> results = dispatcher.runSync("updateShoppingListFrequency", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Change Frequency");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Change Frequency");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/update/payment")
    
    public Response updateShoppingListPaymentMethod(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID, @QueryParam("paymentMethodID") String paymentMethodID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListID", shoppingListID);
            context.put("paymentMethodID", paymentMethodID);
            Map<String,Object> results = dispatcher.runSync("updateShoppingListPaymentMethod", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Update Payment Method");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Update Payment Method");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/update/address")
    
    public Response updateShoppingListAddress(@QueryParam("jsoncallback") String callbackFunction,@QueryParam("shoppingListID") String shoppingListID, @QueryParam("contactMechID") String contactMechID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListID", shoppingListID);
            context.put("contactMechID", contactMechID);
            Map<String,Object> results = dispatcher.runSync("updateShoppingListAddress", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Update Adress");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Update Address");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/update/shoppingListItem")
    
    public Response updateShoppingListItem(@QueryParam("jsoncallback") String callbackFunction,
            @QueryParam("shoppingListID") String shoppingListID, @QueryParam("quantity") String quantity,@QueryParam("seqID") String seqID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListId", shoppingListID);
            context.put("shoppingListItemSeqId", seqID);
            context.put("quantity", new BigDecimal(quantity));
            Map<String,Object> results = dispatcher.runSync("updateShoppingListItem", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Update Shopping List Quantity");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + " context " + context , MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Update Shopping List Quantity");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    @GET
    @Produces("application/json")
    @Path("/add/shoppingListItem")
    
    public Response addShoppingListItem(@QueryParam("jsoncallback") String callbackFunction,
            @QueryParam("shoppingListID") String shoppingListID, @QueryParam("quantity") String quantity,@QueryParam("productID") String productID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListId", shoppingListID);
            context.put("productId", productID);
            context.put("quantity", new BigDecimal(quantity));
            Map<String,Object> results = dispatcher.runSync("createShoppingListItem", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Add To Shopping List");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + " context " + context , MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Add To Shopping List");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    
    @GET
    @Produces("application/json")
    @Path("/delete/shoppingListItem")
    
    public Response removeShoppingListItem(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("shoppingListID") String shoppingListID, @QueryParam("seqID") String seqID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListId", shoppingListID);
            context.put("shoppingListItemSeqId", seqID);
            Map<String,Object> results = dispatcher.runSync("removeShoppingListItem", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Remove Shopping List Item");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + " context " + context , MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Remove Shopping List Item");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/delete/shoppingList")
    
    public Response removeShoppingList(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("shoppingListID") String shoppingListID){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            context.put("userLogin", userLogin);
            context.put("shoppingListID", shoppingListID);
            Map<String,Object> results = dispatcher.runSync("expireShoppingList", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Remove Shopping List");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + " context " + context , MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Remove Shopping List");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
}