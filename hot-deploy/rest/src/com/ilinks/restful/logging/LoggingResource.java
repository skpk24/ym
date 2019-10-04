package com.ilinks.restful.logging;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.webapp.stats.VisitHandler;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;
//import org.apache.wink.common.annotations.Scope;
//import org.apache.wink.common.annotations.Scope.ScopeType;

//@Scope(ScopeType.SINGLETON)
@Path("/logging")
public class LoggingResource extends OFBizRestfulBase{
    public LoggingResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
    }
    
    
    @GET
    @Produces("application/json")
    public Response log(@QueryParam("jsoncallback") String callbackFunction,  @QueryParam("url") String requestURL, @QueryParam("action") String action, @QueryParam("version") String version){
        try{
            super.initRequestAndDelegator();
            jsonMap.put(SUCCESS, SUCCESS);
            String excludePattern = UtilProperties.getPropertyValue("serverstats", "stats.exclude");
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null ||  userAgent.matches(excludePattern)){
                return Response.ok(jsonStr).type("application/json").build();
            }
            HttpSession session = request.getSession();
            if (UtilValidate.isEmpty(session.getAttribute("_WEBAPP_NAME_"))) {
                UtilHttp.setInitialRequestInfo(request);
                session.setAttribute("_CLIENT_REQUEST_",request.getRemoteHost() + requestURL);
                session.setAttribute("appVersion",version);
            }
            GenericValue visit = VisitHandler.getVisit(request.getSession());
            Map<String, Object> context = FastMap.newInstance();
            String hostName = UtilURL.fromUrlString(request.getHeader("Referer")).getHost();
            context.put("visitID", visit.getString("visitId"));
            context.put("action", action);
            context.put("requestURL", requestURL);
            context.put("serverHostName", hostName);
            context.put("referrerURL", request.getHeader("Referer"));
            context.put("webAppName",session.getAttribute("_WEBAPP_NAME_"));
            context.put("version",version);
            if(session.getAttribute("userLogin") != null){
                context.put("userLogin", session.getAttribute("userLogin"));
            }
            dispatcher.runAsync("logRequest", context);
        }
        catch(Exception e){
            //jsonMap.put(ERROR, "Unable To Log Server Request");
            //Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
           
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }   
}