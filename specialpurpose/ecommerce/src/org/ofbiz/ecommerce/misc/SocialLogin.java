package org.ofbiz.ecommerce.misc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.securityext.login.LoginEvents;


public class SocialLogin {

	
    public static final String module = SocialLogin.class.getName();    	
    public static String createFacebookAccount(HttpServletRequest request, HttpServletResponse response) {
    	Debug.logInfo("$$$$$$$$$$$$$$$$$$$$createFacebookAccount -->$$$$$$$$$$$$$$$$$$$$",module);
    	String result = new String();
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String firstName = (String) request.getParameter("firstName");
		String middleName = (String) request.getParameter("middleName");		
		String lastName = (String) request.getParameter("lastName");
		String gender = (String) request.getParameter("gender");		
		String userLoginId = (String) request.getParameter("userLoginId");
		String FBUSERID= (String)request.getParameter("FBUSERID");
		String currentPassword = "ie?j8.I!V@6?6xI";
		String currentPasswordVerify = "ie?j8.I!V@6?6xI";
		////System.out.println("\n\n######################### firstName : " + firstName);
		Debug.logInfo("Data came from facebook -->"+firstName+" :"+lastName+" :"+userLoginId+" :"+gender,module);
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId", userLoginId));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		if(userLogin!=null){
			request.setAttribute("USERNAME", userLogin.get("userLoginId") );
			request.setAttribute("PASSWORD", currentPassword );
			result = LoginEvents.storeLogin(request, response);
		    HttpSession session = request.getSession();
		    
		    session.setAttribute("FBUSERID",FBUSERID);
		    ShoppingCart cart = (ShoppingCart)session.getAttribute("shoppingCart");
		    if (cart != null) {
		        cart.setOrderPartyId((String)userLogin.get("partyId"));
		    }   
		}
		else{
    		Map serviceParams = new HashMap();
    		try {
    			serviceParams = dispatcher.runSync("createPersonAndUserLogin", UtilMisc.toMap("firstName", firstName, "middleName", middleName, "lastName", lastName, "gender", gender, "comments", "Google User" , "userLoginId",userLoginId, "currentPassword", currentPassword, "currentPasswordVerify", currentPasswordVerify, "enabled", "Y" ));
    		} catch (GenericServiceException e) {
    			Debug.logError(e.getMessage(),module);
    			//e.printStackTrace();
    		}
    		
    		userLogin = (GenericValue)serviceParams.get("newUserLogin");
		    HttpSession session = request.getSession();
		   
		    LoginWorker.doBasicLogin(userLogin, request);
            result = LoginWorker.autoLoginSet(request, response);
            session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
            String partyId = (String)serviceParams.get("partyId");
            if (cart != null) {
		        cart.setOrderPartyId(partyId);
            }
            session.setAttribute("autoName", firstName + " " + lastName);
            session.setAttribute("FBUSERID",FBUSERID);
            Debug.logInfo("Facebook login created : "+userLoginId, module);
    		try {
    			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", (String)serviceParams.get("partyId"), "roleTypeId", "CUSTOMER" , "userLogin", (GenericValue)serviceParams.get("newUserLogin") ));
    			String emailContactMechId = createPartyEmail(dispatcher, partyId, userLoginId, userLogin);
                Debug.logInfo("Party emailContactMechId : "+emailContactMechId, module);
    		} catch (GenericServiceException e) {
    			Debug.logError(e.getMessage(),module);
    			//e.printStackTrace();
    		}
        }
		Debug.logInfo("result = "+result, module);
		return result;
	} 
    
    public static String oauth2callbackGoogle(HttpServletRequest request, HttpServletResponse response) {
    	String result = new String("success");
    	try
    	{
    	//Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle -->$$$$$$$$$$$$$$$$$$$$",module);
    	
    	
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String errorMsg = request.getParameter("errorMsg");
		//Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle 2 -->$$$$$$$$$$$$$$$$$$$$",module);
		if(errorMsg!=null && errorMsg.equalsIgnoreCase("Access Denied!"))
		{
		  request.setAttribute("_ERROR_MESSAGE_", errorMsg)	;
		  return "error";
		}
		//Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle  3-->$$$$$$$$$$$$$$$$$$$$",module);
		String email = request.getParameter("email");
		String firstName=request.getParameter("given_name");
		String lastName=request.getParameter("family_name");
		String gender = request.getParameter("gender");
		String comments = "Google User";
		if(gender.equalsIgnoreCase("male"))
		{
			gender ="M";
		}else{
			gender="F";
		}
		String userLoginId = email;
		String currentPassword = "ie?j8.I!V@6?6xI";
		String currentPasswordVerify = "ie?j8.I!V@6?6xI";
		//Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle 4  -->$$$$$$$$$$$$$$$$$$$$",module);
		
		//Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle profile info -->$$$$$$$$$$$$$$$$$$$$"+email +"\t given_name="+firstName,module);
		GenericValue userLogin = null;
		try {
			userLogin = delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId", userLoginId));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		if(userLogin!=null){
			request.setAttribute("USERNAME", userLogin.get("userLoginId") );
			request.setAttribute("PASSWORD", currentPassword );
			result = LoginEvents.storeLogin(request, response);
			
		    HttpSession session = request.getSession();
		    //Debug.logInfo("$$$$$$$$$$$$$$$$$$$$   oauth2callbackGoogle 67  -->$$$$$$$$$$$$$$$$$$$$",module);
		   // session.setAttribute("FBUSERID",FBUSERID);
		    ShoppingCart cart = (ShoppingCart)session.getAttribute("shoppingCart");
		    if (cart != null) {
		        cart.setOrderPartyId((String)userLogin.get("partyId"));
		    }   
		}
		else
		{

    		Map serviceParams = new HashMap();
    		try {
    			serviceParams = dispatcher.runSync("createPersonAndUserLogin", UtilMisc.toMap("firstName", firstName, "middleName", "", "lastName", lastName, "gender", gender, "comments", comments,  "userLoginId",userLoginId, "currentPassword", currentPassword, "currentPasswordVerify", currentPasswordVerify, "enabled", "Y" ));
    		} catch (GenericServiceException e) {
    			Debug.logError(e.getMessage(),module);
    			//e.printStackTrace();
    		}
    		
    		userLogin = (GenericValue)serviceParams.get("newUserLogin");
		    HttpSession session = request.getSession();
		   
		    LoginWorker.doBasicLogin(userLogin, request);
            result = LoginWorker.autoLoginSet(request, response);
            session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
            String partyId = (String)serviceParams.get("partyId");
            if (partyId != null) {
		        try {
					GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
					if(party != null) {
						
						party.set("description", "Google User");
						party.store();
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
            }
            if (cart != null) {
		        cart.setOrderPartyId(partyId);
            }
            session.setAttribute("autoName", firstName + " " + lastName);
           // session.setAttribute("FBUSERID",FBUSERID);
            Debug.logInfo("Google  login created : "+userLoginId, module);
    		try {
    			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", (String)serviceParams.get("partyId"), "roleTypeId", "CUSTOMER" , "userLogin", (GenericValue)serviceParams.get("newUserLogin") ));
    			String emailContactMechId = createPartyEmail(dispatcher, partyId, userLoginId, userLogin);
                Debug.logInfo("Party emailContactMechId : "+emailContactMechId, module);
    		} catch (GenericServiceException e) {
    			Debug.logError(e.getMessage(),module);
    			//e.printStackTrace();
    		}
        
		}
		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
		return result;
    }
    
    public static String createPartyEmail(LocalDispatcher dispatcher, String partyId, String email, GenericValue userLogin) {
        Map<String, Object> context = new HashMap<String, Object>();
        Map<String, Object> summaryResult = new HashMap<String, Object>();
      //  Map<String, Object> summaryResult = FastMap.newInstance();
        String emailContactMechId = null;

        try {
            if (UtilValidate.isNotEmpty(email)) {
                context.clear();
                context.put("emailAddress", email);
                context.put("userLogin", userLogin);
                context.put("contactMechTypeId", "EMAIL_ADDRESS");
                summaryResult = dispatcher.runSync("createEmailAddress", context);
                emailContactMechId = (String) summaryResult.get("contactMechId");

                context.clear();
                context.put("partyId", partyId);
                context.put("contactMechId", emailContactMechId);
                context.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
                context.put("userLogin", userLogin);
                summaryResult = dispatcher.runSync("createPartyContactMech", context);
            }
        } catch (Exception e) {
            Debug.logError(e, "Failed to createPartyEmail", module);
        }
        return emailContactMechId;
    }  
}
