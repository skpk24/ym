package com.ilinks.restful.order;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

import javolution.util.FastMap;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;

@Path("/order")
public class OrderResource extends OFBizRestfulBase{
    public OrderResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/orderInfo")
    public Response getOrderInfo(@QueryParam("jsoncallback") String callbackFunction,  @QueryParam("orderStatus") String requestURL){
        try{
        	jsonMap.put(SUCCESS, SUCCESS);
        	//GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
        	GenericValue userLogin = RestfulHelper.getSystemLogin(delegator);
        	Map<String,Object> results = null;
            super.initRequestAndDelegator();
            String autoShip = request.getParameter("isAutoShip");
            List<String> orderStatuses = null ; //UtilMisc.toList("ORDER_APPROVED", "ORDER_HOLD");
            if(request.getParameterValues("orderStatuses") != null){
            	orderStatuses = Arrays.asList( request.getParameterValues("orderStatuses"));
            }
           
            String inProcessing = request.getParameter("inProcessing");
            String backOrder = request.getParameter("backOrder");
            Map<String,Object> context = FastMap.newInstance(); 
            context.put("autoShip",autoShip);
            context.put("orderStatuses",orderStatuses);
            context.put("inProcessing", inProcessing);
            context.put("backOrder", backOrder);
            context.put("userLogin", userLogin);
            results = dispatcher.runSync("getOrderInfo", context);
            jsonMap.putAll(results);
        }
        catch(Exception e){
            //jsonMap.put(ERROR, "Unable To Log Server Request");
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
           
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }   
}