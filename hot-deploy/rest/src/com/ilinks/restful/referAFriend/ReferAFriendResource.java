package com.ilinks.restful.referAFriend;


import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;

@Path("/referAFriend")
public class ReferAFriendResource extends OFBizRestfulBase{
    public ReferAFriendResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
    }
    
    
    
    @GET
    @Produces("application/json")
    public Response getReferAFriendInfo(@QueryParam("jsoncallback") String callbackFunction,
            @QueryParam("startDate") String startDateMilliSeconds, @QueryParam("endDate") String endDateMilliSeconds){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            FastMap<String, Object> context = FastMap.newInstance(); 
            
            Timestamp startDate = UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
            Timestamp endDate = UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
            if(startDateMilliSeconds != null){
                startDate = UtilDateTime.getTimestamp(startDateMilliSeconds);
            }
            
            if(endDateMilliSeconds != null){
                endDate = UtilDateTime.getTimestamp(endDateMilliSeconds);
            }
            jsonMap.put("startDate", UtilFormatOut.formatDate(startDate));
            jsonMap.put("endDate",UtilFormatOut.formatDate(endDate));
            endDate = UtilDateTime.getNextDayStart(endDate);
            context.put("userLogin", userLogin);
            context.put("startDate", startDate);
            context.put("endDate",endDate);
            
            Map<String,Object> results = dispatcher.runSync("retrieveReferAFriendInfo", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Retrive Refer A Friend List");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
            jsonMap.put("referAFriendList", results.get("referAFriendList"));
            jsonMap.put("trackingCode", results.get("trackingCode"));
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Retrive Refer A Friend List");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
   
    
    
    @GET
    @Produces("application/json")
    @Path("/send")
    public Response sendReferAFriendEmail(@QueryParam("jsoncallback") String callbackFunction,
            @QueryParam("name") String name, @QueryParam("email") String email,  @QueryParam("trackingCode") String trackingCode){
        super.initRequestAndDelegator();
        try{
            GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
            
            Map<String,Object> context = FastMap.newInstance();
            String productStoreID = ProductStoreWorker.getProductStoreId(request);
            context.put("productStoreID", productStoreID);
            context.put("userLogin", userLogin);
            context.put("sendToName", name);
            context.put("sendTo",email);
            context.put("trackingCode",trackingCode);
            
            Map<String,Object> results = dispatcher.runSync("sendReferAFriendEmail", context);
            if(ServiceUtil.isError(results)){
                jsonMap.put(ERROR, "Unable To Send Refer A Friend Email");
                Debug.logError(results.get(ModelService.ERROR_MESSAGE) + "", MODULE);
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, "Unable To Send Refer A Friend Email");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
}