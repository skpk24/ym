package com.ilinks.restful.login;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;
import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;
//import org.apache.wink.common.annotations.Scope;
//import org.apache.wink.common.annotations.Scope.ScopeType;

//@Scope(ScopeType.SINGLETON)
@Path("/login")
public class LoginResource extends OFBizRestfulBase{
    private final String LOGIN_ERROR;
    private final String NOT_LOGIN;
    private final String GUEST_USER;
    /**
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
    public LoginResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        LOGIN_ERROR = properties.getProperty("LOGIN_PROBLEM");
        NOT_LOGIN = properties.getProperty("LOGIN_NOT_LOGIN");
        GUEST_USER = properties.getProperty("LOGIN_GUEST_NOT_ALLOWED");
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/create")
    public Response createLogin(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("loginID") String loginID, @QueryParam("password") String password, @QueryParam("hint") String hint){
        try{
            super.initRequestAndDelegator();
            
            if(!request.isSecure()){
                jsonMap.put(ERROR, "error");
                Debug.logError("Need To Use HTTPS", MODULE);
                return Response.ok("{error : error}").type("application/json").build();
            }
            
            GenericValue userLogin  = delegator.findOne("UserLogin",UtilMisc.toMap("userLoginId", loginID), true);
            
            if(loginID == null){
                jsonMap.put(ERROR, "error");
            }
            else if (userLogin == null){
                // userLogin
                Map<String,Object> context  = FastMap.newInstance();
                context.put("userLoginId", loginID);
                context.put("currentPassword", password);
                context.put("currentPasswordVerify", password);
                context.put("enabled", "Y");
                context.put("passwordHint", hint);
                boolean transStatus = TransactionUtil.begin();
                Map<String,Object> results = dispatcher.runSync("createPersonAndUserLogin", context);
                if(ServiceUtil.isSuccess(results)){
                    //LoginWorker.doBasicLogin(userLogin, request);
                    //LoginWorker.autoLoginSet(request, response);
                    // create role type
                    context.clear();
                    String parytID = (String)results.get("partyId");
                    
                    //GenericValue systemLogin = RestfulHelper.getSystemLogin(delegator);
                    userLogin = (GenericValue)results.get("newUserLogin");
                    context.put("partyId",parytID);
                    context.put("userLogin", userLogin);
                    context.put("roleTypeId", "CUSTOMER");
                    results = dispatcher.runSync("createPartyRole", context);
                    
                    if(ServiceUtil.isSuccess(results)){
                        // link email address
                        context.clear();
                        context.put("partyId",parytID);
                        context.put("emailAddress", loginID);
                        context.put("userLogin", userLogin);
                        results = dispatcher.runSync("createPartyEmailAddress", context);
                        
                        if(ServiceUtil.isSuccess(results)){
                            // contach mech 
                            context.clear();
                            context.put("partyId",parytID);
                            context.put("contactMechId", results.get("contactMechId"));
                            context.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
                            context.put("userLogin",userLogin);
                            results = dispatcher.runSync("createPartyContactMechPurpose", context);
                            HttpSession session = request.getSession();
                            ShoppingCart cart = (ShoppingCart)session.getAttribute("cart");
                            if(cart != null){
                                cart.setOrderPartyId(userLogin.getString("partyId"));
                            }
                            GenericValue person = userLogin.getRelatedOne("Person");
                            
                            String productStoreID = ProductStoreWorker.getProductStoreId(request);
                            GenericValue emailSetting = delegator.findByPrimaryKeyCache("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreID,  "emailType", "PRDS_CUST_REGISTER"));
                            Map<String,Object> bodyMap = FastMap.newInstance();
                            bodyMap.put("password", password);
                            bodyMap.put("username", loginID);
                            bodyMap.put("person", person);
                            
                            context.clear();
                            context.put("partyId", userLogin.getString("partyId"));
                            context.put("userLogin", userLogin);
                            context.put("bodyParameters", bodyMap);
                            context.put("sendTo", loginID);
                            context.put("subject", emailSetting.getString("subject"));
                            context.put("sendFrom", emailSetting.getString("fromAddress"));
                            context.put("sendCc", emailSetting.getString("ccAddress"));
                            context.put("sendBcc", emailSetting.getString("bccAddress"));
                            context.put("contentType", emailSetting.getString("contentType"));
                            context.put("emailType", emailSetting.getString("emailType"));
                            context.put("bodyScreenUri", emailSetting.getString("bodyScreenLocation"));
                            dispatcher.runAsync("sendMailFromScreen", context);
                        }
                    }
                }
                if(ServiceUtil.isError(results)){
                    TransactionUtil.rollback();
                    jsonMap.put(ERROR, "Unable To Create Login ID");
                    Debug.logError( ServiceUtil.getErrorMessage(results), MODULE);
                }
                else{
                    TransactionUtil.commit(transStatus);
                    LoginWorker.doBasicLogin(userLogin, request);
                    LoginWorker.autoLoginSet(request, response);
                }
            }else{
                jsonMap.put(ERROR, "Please Use A Different Email Address");
            }
            
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Create Login ID " + loginID);
            Debug.logError(e, e.getClass().getName() + " " + loginID + " " + e.getMessage(), MODULE);
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET  @Path("/logout")
    @Produces("application/json")
    public Response loginOut(@QueryParam("jsoncallback") String callbackFunction){
        try{
            super.initRequestAndDelegator();
            LoginWorker.logout(request, response);
            jsonMap.put(SUCCESS, SUCCESS);
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Log Out");
            Debug.logError(e, "Unable To Log Out" + e.getMessage(), MODULE);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    
    @GET
    @Produces("application/json")
    public Response login(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("loginID") String loginID, @QueryParam("password") String password){
        try{
            super.initRequestAndDelegator();
            if(isLogin()){
                jsonMap.put(SUCCESS, SUCCESS);
                GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
                GenericValue person = userLogin.getRelatedOne("Person");
                jsonMap.put("firstName", person.get("firstName"));
            }
            else if(loginID == null){
                jsonMap.put(ERROR, GUEST_USER);
            }
            else{
                HttpSession session = request.getSession();
                session.setAttribute("USERNAME", loginID);
                session.setAttribute("PASSWORD", password);
                String result = LoginWorker.login(request, response);
                if(result.equalsIgnoreCase("SUCCESS")){
                    jsonMap.put(SUCCESS, SUCCESS);
                    result = LoginWorker.checkLogin(request, response);
                    GenericValue userLogin = (GenericValue)request.getSession().getAttribute("userLogin");
                    GenericValue person = userLogin.getRelatedOne("Person");
                    jsonMap.put("firstName", person.get("firstName"));
                }
                else{
                    jsonMap.put(ERROR, String.format(LOGIN_ERROR,loginID));
                }
            }
        }
        catch(Exception e){
            jsonMap.put(ERROR, String.format(LOGIN_ERROR,loginID));
            Debug.logError(e, e.getClass().getName() + " " + loginID + " " + e.getMessage(), MODULE);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @Path("/checkLogin")
    @GET
    @Produces("application/json")
    public Response checkLogin(@QueryParam("jsoncallback") String callbackFunction){
        try{
            
            String result = LoginWorker.checkLogin(request, response);
            if(result.equalsIgnoreCase("error")){
                jsonMap.put(ERROR,NOT_LOGIN);
            }
            else{
                jsonMap.put(SUCCESS, SUCCESS);
            }
           
        }
        catch(Exception e){
            jsonMap.put(ERROR, ERROR);
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    private  boolean isLogin(){
        String result = LoginWorker.checkLogin(request, response);
        
        if(result.equalsIgnoreCase("error")){
            return false;
        }
      return true;
    }
}