/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

package org.ofbiz.securityext.login;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastMap;

import org.apache.commons.lang.RandomStringUtils;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductEvents;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.LoginWorker;

/**
 * LoginEvents - Events for UserLogin and Security handling.
 */
public class LoginEvents {

    public static final String module = LoginEvents.class.getName();
    public static final String resource = "SecurityextUiLabels";
    public static final String usernameCookieName = "OFBiz.Username";

    /**
     * Save USERNAME and PASSWORD for use by auth pages even if we start in non-auth pages.
     *
     * @param request The HTTP request object for the current JSP or Servlet request.
     * @param response The HTTP response object for the current JSP or Servlet request.
     * @return String
     */
    public static String saveEntryParams(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        HttpSession session = request.getSession();

        // save entry login parameters if we don't have a valid login object
        if (userLogin == null) {

            String username = request.getParameter("USERNAME");
            String password = request.getParameter("PASSWORD");

            if ((username != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
                username = username.toLowerCase();
            }
            if ((password != null) && ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "password.lowercase")))) {
                password = password.toLowerCase();
            }

            // save parameters into the session - so they can be used later, if needed
            if (username != null) session.setAttribute("USERNAME", username);
            if (password != null) session.setAttribute("PASSWORD", password);

        } else {
            // if the login object is valid, remove attributes
            session.removeAttribute("USERNAME");
            session.removeAttribute("PASSWORD");
        }

        return "success";
    }

    /**
     * The user forgot his/her password.  This will call showPasswordHint, emailPassword or simply returns "success" in case
     * no operation has been specified.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String MailSending(HttpServletRequest request, HttpServletResponse response)
    {

    	
 		/*javax.servlet.http.HttpSession  httpSession = (javax.servlet.http.HttpSession )request.getSession();
 		String origCaptcha = (String)httpSession.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY) ;			
 		if(request.getParameter("captchaStr").toLowerCase().equals(origCaptcha.toLowerCase())){
 		
 		}else{
 			request.setAttribute("catch_result", "Text entered doesn't match the Image text. Please re enter");
 			//error_list.add("The code you've entered does not match with the code in the image. Please re enter");
 			 return "error";
 		}*/    	
     	
         HttpSession session = request.getSession();
         LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
         GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
         String emailType = "CONT_NOTI_EMAIL";
         String isSend="Y";
         request.setAttribute("isSend", isSend);
         
         String contactListIds = request.getParameter("contactListId");
         String CommunicationId = request.getParameter("communicationEventId");
         String templateId = request.getParameter("templateId");
         String templateContain = request.getParameter("templateId");
         
      
         
         
         
         List<GenericValue> PartyList = null;
         String partyId=null;
         List<GenericValue> TemplateId = null;
         String templateCon =null;
         List<GenericValue> ContactMech = null;
         String contactMechIds =null;
         String Id=null;
         String PartyMailId=null;
         List<GenericValue> emailId = null;
         try {
			TemplateId = delegator.findList("MarketingTemplate", EntityCondition.makeCondition("templateId", EntityOperator.EQUALS, templateId), null, null, null, false);
			for(GenericValue Contain : TemplateId )
			{
				templateCon = (String) Contain.get("templateData");
			//System.out.println("  441 ###########  templateCon "+templateCon);
			}
         } catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
 		
         
         try {
			PartyList = delegator.findList("ContactListParty", EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, contactListIds), null, null, null, false);
			String partyIdFrom = request.getParameter("partyIdFrom");
			for(GenericValue ListId : PartyList )
			{
				EntityCondition exprList = null;
				 partyId = (String) ListId.get("partyId");
				List exprList1 = new ArrayList();
				exprList1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				exprList1.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
		         
		        EntityCondition mainCond = EntityCondition.makeCondition(exprList1, EntityOperator.AND);
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
				ContactMech = delegator.findList("PartyContactMechPurpose",mainCond , null, null, null, false);
				for(GenericValue contactMechId : ContactMech )
				{
					contactMechIds = (String) contactMechId.get("contactMechId");
				   emailId = delegator.findList("ContactMech",EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechIds) , null, null, null, false);
				
				for(GenericValue email : emailId )
				{
					 PartyMailId=(String)email.get("infoString");
				}
				}
								//PartyNameContactMechView
				String fromUser = request.getParameter("fromUser");
		         String fromMail = request.getParameter("fromMail");
		         String firstName = request.getParameter("firstName");
		        // String attachHtml= "component://framework/images/webapp/images/importFiles/emailTemplate/marketingEmailTemplate.html";
		         String emailAddress = request.getParameter("emailAddress");
		         if(fromMail == null || fromMail.length() == 0){
		         	fromMail = emailAddress;
		         }
		         if(fromUser == null || fromUser.length() == 0){
		        	 fromUser = firstName;
		          }
		         String note = request.getParameter("note");
		         String origCommEventId = request.getParameter("origCommEventId");
		         String partyIdTo = request.getParameter("partyIdTo");
		         String subject = request.getParameter("subject");
		         String message = request.getParameter("content");
		         
		        // String partyIdFrom = request.getParameter("partyIdFrom");
		         
		        
		         
		         String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailContactListScreens.xml#contactUsMail";
		     	Debug.logInfo("yourName..................."+fromUser, module);
		     	
		         /*GenericValue productStore = ProductStoreWorker.getProductStore(request);
		         if (productStore == null) {
		             String errMsg = "Could not send tell a friend email, no ProductStore found";
		             request.setAttribute("_ERROR_MESSAGE_", errMsg);
		             return "error";
		         }*/
		         String productStoreId = "9000";
		         String webSiteId=null;
		         
		         webSiteId = CatalogWorker.getWebSiteId(request);
		         Object autoUserLogin = request.getSession().getAttribute("autoUserLogin");
		         GenericValue productStoreEmail = null;
		         try {
		             productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
		                     UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
		         } catch (GenericEntityException e) {
		             String errMsg = "Unable to get product store email setting for contact us: " + e.toString();
		             Debug.logError(e, errMsg, module);
		             request.setAttribute("_ERROR_MESSAGE_", errMsg);
		             return "error";
		         }
		         String bodyScreenLocation = null;
		         if (productStoreEmail == null) {
		             String errMsg = "Could not find contact us [" + emailType + "] email settings for the store [" + productStoreId + "]";
		             //request.setAttribute("_ERROR_MESSAGE_", errMsg);
		             //return "error";
		             bodyScreenLocation = defaultScreenLocation;
		         }else{
		        	 bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
		         }
		         
		         Map paramMap = UtilHttp.getParameterMap(request);
		         Debug.logInfo("paramMap..................."+paramMap, module);
		         paramMap.put("locale", UtilHttp.getLocale(request));
		        // paramMap.put("userLogin", session.getAttribute("userLogin"));
		 		if (message != null) {
		 			paramMap.put("message", message);
		 		}
		 		if (fromUser != null) {
		 			paramMap.put("yourName", fromUser);
		 		}
		 		if (isSend != null) {
		 			paramMap.put("isSend", isSend);
		 		}
		 		if (partyId != null) {
		 			paramMap.put("isSend", partyId);
		 		}
		 		
		 		String contactUsEmail = UtilProperties.getMessage("general", "supportMail", request.getLocale());
		         Map context =  new HashMap(); //FastMap.newInstance();
		         context.put("bodyScreenUri", bodyScreenLocation);
		         context.put("bodyParameters", paramMap);
		         context.put("sendTo", PartyMailId);
		         context.put("contentType", productStoreEmail.get("contentType"));
		         context.put("sendFrom", partyIdFrom);
		         context.put("sendCc", productStoreEmail.get("ccAddress"));
		         context.put("sendBcc", productStoreEmail.get("bccAddress"));
		         context.put("subject", subject);
		         context.put("bodyText", templateCon);

		        //context.put("attachmentName", attachHtml);

		         
		         try {
		             dispatcher.runAsync("sendMailFromScreen", context);
		             request.setAttribute("_EVENT_MESSAGE_", "Email sent successfully");
		             
		         } catch (GenericServiceException e) {
		             String errMsg = "Problem sending mail: " + e.toString();
		             Debug.logError(e, errMsg, module);
		             request.setAttribute("_ERROR_MESSAGE_", errMsg);
		             return "error";
		         }
			}
         } catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
         
         
         return "success";
     
    }
    public static String forgotPassword(HttpServletRequest request, HttpServletResponse response) {
        if ((UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT"))) || (UtilValidate.isNotEmpty(request.getParameter("GET_PASSWORD_HINT.x")))) {
            return showPasswordHint(request, response);
        } else if ((UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD"))) || (UtilValidate.isNotEmpty(request.getParameter("EMAIL_PASSWORD.x")))) {
            return emailPassword(request, response);
        } else {
            return "success";
        }
    }

    /** Show the password hint for the userLoginId specified in the request object.
     *@param request The HTTPRequest object for the current request
     *@param response The HTTPResponse object for the current request
     *@return String specifying the exit status of this event
     */
    public static String showPasswordHint(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String userLoginId = request.getParameter("USERNAME");
        String errMsg = null;

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;

        try {
            supposedUserLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
        } catch (GenericEntityException gee) {
            Debug.logWarning(gee, "", module);
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String passwordHint = supposedUserLogin.getString("passwordHint");

        if (!UtilValidate.isNotEmpty(passwordHint)) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_password_hint_specified_try_password_emailed", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        Map<String, String> messageMap = UtilMisc.toMap("passwordHint", passwordHint);
        errMsg = UtilProperties.getMessage(resource, "loginevents.password_hint_is", messageMap, UtilHttp.getLocale(request));
        request.setAttribute("_EVENT_MESSAGE_", errMsg);
        return "success";
    }

    /**
     *  Email the password for the userLoginId specified in the request object.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static String emailPassword(HttpServletRequest request, HttpServletResponse response) {
        String defaultScreenLocation = "component://securityext/widget/EmailSecurityScreens.xml#PasswordEmail";
       
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String productStoreId = ProductStoreWorker.getProductStoreId(request);

        String errMsg = null;

        Map<String, String> subjectData = new HashMap<String, String>(); //FastMap.newInstance();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = request.getParameter("USERNAME");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

        if (!UtilValidate.isNotEmpty(userLoginId)) {
            // the password was incomplete
            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
            if (supposedUserLogin == null) {
                // the Username was not found
                errMsg = UtilProperties.getMessage(resource, "loginevents.username_not_found_reenter", UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            if (useEncryption) {
                // password encrypted, can't send, generate new password and email to user
                passwordToSend = RandomStringUtils.randomAlphanumeric(Integer.parseInt(UtilProperties.getPropertyValue("security", "password.length.min", "5")));
                supposedUserLogin.set("currentPassword", HashCrypt.getDigestHash(passwordToSend, LoginServices.getHashType()));
                supposedUserLogin.set("passwordHint", "Auto-Generated Password");
                if ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.email_password.require_password_change"))){
                    supposedUserLogin.set("requirePasswordChange", "Y");
                }
            } else {
                passwordToSend = supposedUserLogin.getString("currentPassword");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_accessing_password", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
        if (supposedUserLogin == null) {
            // the Username was not found
            Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
            errMsg = UtilProperties.getMessage(resource, "loginevents.user_with_the_username_not_found", messageMap, UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        StringBuilder emails = new StringBuilder();
        GenericValue party = null;

        try {
            party = supposedUserLogin.getRelatedOne("Party");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            party = null;
        }
        if (party != null) {
            Iterator<GenericValue> emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = emailIter.next();
                emails.append(emails.length() > 0 ? "," : "").append(email.getString("infoString"));
            }
        }

        if (!UtilValidate.isNotEmpty(emails.toString())) {
            // the Username was not found
            errMsg = UtilProperties.getMessage(resource, "loginevents.no_primary_email_address_set_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.problems_with_configuration_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        // set the needed variables in new context
        Map<String, Object> bodyParameters = new HashMap<String, Object>(); // FastMap.newInstance();
        bodyParameters.put("useEncryption", Boolean.valueOf(useEncryption));
        bodyParameters.put("password", UtilFormatOut.checkNull(passwordToSend));
        bodyParameters.put("locale", UtilHttp.getLocale(request));
        bodyParameters.put("userLogin", supposedUserLogin);
        bodyParameters.put("productStoreId", productStoreId);
        bodyParameters.put("sendTo", emails.toString());
       
        Map<String, Object> serviceContext = new HashMap<String, Object>(); //FastMap.newInstance();
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emails.toString());
        serviceContext.put("partyId", party.getString("partyId"));
        HttpSession session=request.getSession();
        
        

        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
            

            if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            errMsg = UtilProperties.getMessage(resource, "loginevents.error_unable_email_password_contact_customer_service", UtilHttp.getLocale(request));
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_saving_new_password_email_not_correct_password", messageMap, UtilHttp.getLocale(request));
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        }

        if (useEncryption) {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_createdandsent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
        } else {
            errMsg = UtilProperties.getMessage(resource, "loginevents.new_password_sent_check_email", UtilHttp.getLocale(request));
            request.setAttribute("_EVENT_MESSAGE_", errMsg);
        }
        return "success";
    }

    public static String storeCheckLogin(HttpServletRequest request, HttpServletResponse response) {
        String responseString = LoginWorker.checkLogin(request, response);
        String productCategoryId = request.getParameter("productCategoryId"); 
        if(UtilValidate.isNotEmpty(productCategoryId)){
        	request.setAttribute("productCategoryId",productCategoryId);
        }
         if ("error".equals(responseString)) {
            return responseString;
        }
        // if we are logged in okay, do the check store customer role
        return ProductEvents.checkStoreCustomerRole(request, response);
    }

    public static String storeLogin(HttpServletRequest request, HttpServletResponse response) {
        String responseString = LoginWorker.login(request, response);
        String productCategoryId = request.getParameter("productCategoryId"); 
        if(UtilValidate.isNotEmpty(productCategoryId)){
        	request.setAttribute("productCategoryId",productCategoryId);
        }
          if (!"success".equals(responseString)) {
            return responseString;
        }
        if ("Y".equals(request.getParameter("rememberMe"))) {
            setUsername(request, response);
        }
        String from = request.getParameter("from");
        request.setAttribute("from", from);
        // if we logged in okay, do the check store customer role
        return ProductEvents.checkStoreCustomerRole(request, response);
    }

    public static String getUsername(HttpServletRequest request) {
        String cookieUsername = null;
        Cookie[] cookies = request.getCookies();
        if (Debug.verboseOn()) Debug.logVerbose("Cookies:" + cookies, module);
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(usernameCookieName)) {
                    cookieUsername = cookie.getValue();
                    break;
                }
            }
        }
        return cookieUsername;
    }

    public static void setUsername(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        String domain = UtilProperties.getPropertyValue("url.properties", "cookie.domain");
        // first try to get the username from the cookie
        synchronized (session) {
            if (UtilValidate.isEmpty(getUsername(request))) {
                // create the cookie and send it back
                Cookie cookie = new Cookie(usernameCookieName, request.getParameter("USERNAME"));
                cookie.setMaxAge(60 * 60 * 24 * 365);
                cookie.setPath("/");
                cookie.setDomain(domain);
                response.addCookie(cookie);
            }
        }
    }
    
    public static String checkMobileNo(HttpServletRequest request, HttpServletResponse response){
    	
        String mobileNo=request.getParameter("mobileNo");
        String modifyPage=request.getParameter("modifyPage");
         
        GenericValue  userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
        PrintWriter out =null;
 		List condition =new ArrayList();
		List mobileNoList=new ArrayList();
		
		if(UtilValidate.isNotEmpty(mobileNo)){
		condition.add(EntityCondition.makeCondition("contactNumber",EntityOperator.EQUALS,mobileNo));
		}
         try{
         	 out =response.getWriter();
         	 mobileNoList =delegator.findList("TelecomNumber",EntityCondition.makeCondition(condition),null,null,null,false);
         	
         }
     	catch (Exception e) {
     		Debug.logError(e.getMessage(),module);
     		//e.printStackTrace();
 		}
     	
     	 if(UtilValidate.isNotEmpty(mobileNoList)){
     		 List contactNumber = EntityUtil.getFieldListFromEntityList(mobileNoList, "contactMechId", true);
      		List contactMcList = null;
			try {
				contactMcList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition("contactMechId",EntityOperator.IN,contactNumber), null,null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 contactMcList = EntityUtil.filterByDate(contactMcList);
		 if(UtilValidate.isNotEmpty(modifyPage)){
		   String partyId = userLogin.getString("partyId");
		   List temp =  EntityUtil.filterByAnd(contactMcList, UtilMisc.toMap("partyId", partyId));
		   if(contactMcList!=null && contactMcList.size()>0 && UtilValidate.isEmpty(temp)){
 			    out.print("SY");
 			}
 			else{
 				out.print("Y");
 			}
		 }else{
			  if(contactMcList!=null && contactMcList.size()>0){
	 			    out.print("SY");
	 			}
	 			else{
	 				out.print("Y");
	 			}
		 }
		   
		   
 		}
     	return "success";	
     }
    
    public static String checkEmailId(HttpServletRequest request, HttpServletResponse response){
    	
        String emailId=request.getParameter("username");
        
        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
        PrintWriter out =null;
		List userNameList=new ArrayList();
		
         try{
         	 out =response.getWriter();
         	 userNameList =delegator.findList("UserLogin",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,emailId),null,null,null,false);
         	
         }
     	catch (Exception e) {
     		Debug.logError(e.getMessage(),module);
     		//e.printStackTrace();
 		}
     	
     	 if(UtilValidate.isNotEmpty(userNameList)){
 			
     		 List userLoginId = EntityUtil.getFieldListFromEntityList(userNameList, "userLoginId", true);
 			if(userLoginId!=null && userLoginId.size()>0){
 			    out.print("SY");
 			}
 			else
 			{
 				out.print("Y");
 			}
 		}
     	return "success";	
     }
    
    public static String recipientAddress(HttpServletRequest request, HttpServletResponse response){
    	
        String recipientName=request.getParameter("recipientName");
        String recipientMobileNum=request.getParameter("recipientMobileNum");
        String recipientEmailId=request.getParameter("recipientEmailId");
        String message=request.getParameter("message");
        
        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
        String recipientAddressSeqId = delegator.getNextSeqId("RecipientAddress");
    	
        GenericValue recipientAddress = delegator.makeValue("RecipientAddress");
    		recipientAddress.set("recipientAddressSeqId",recipientAddressSeqId);
	    	recipientAddress.set("recipientName", recipientName);
	    	recipientAddress.set("recipientMobileNum", recipientMobileNum);
	    	recipientAddress.set("recipientEmailId",recipientEmailId);
	    	recipientAddress.set("message",message);
        
         try {
        	 recipientAddress.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         HttpSession session=request.getSession();
         session.setAttribute("Recipient_Email_Id",recipientEmailId );
         request.setAttribute("_EVENT_MESSAGE_", "Recipient Address is submitted successfully!");
     	return "success";	
     }
public static String contactUsEmail(HttpServletRequest request, HttpServletResponse response) {
    	
 		/*javax.servlet.http.HttpSession  httpSession = (javax.servlet.http.HttpSession )request.getSession();
 		String origCaptcha = (String)httpSession.getAttribute(nl.captcha.servlet.Constants.SIMPLE_CAPCHA_SESSION_KEY) ;			
 		if(request.getParameter("captchaStr").toLowerCase().equals(origCaptcha.toLowerCase())){
 		
 		}else{
 			request.setAttribute("catch_result", "Text entered doesn't match the Image text. Please re enter");
 			//error_list.add("The code you've entered does not match with the code in the image. Please re enter");
 			 return "error";
 		}*/    	
     	
         HttpSession session = request.getSession();
         LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
         GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
         String emailType = "CONT_NOTI_EMAIL";
         String isSend="Y";
         request.setAttribute("isSend", isSend);
         
         String fromUser = request.getParameter("fromUser");
         String fromMail = request.getParameter("fromMail");
         String firstName = request.getParameter("firstName");
         
         String emailAddress = request.getParameter("emailAddress");
         if(fromMail == null || fromMail.length() == 0){
         	fromMail = emailAddress;
         }
         if(fromUser == null || fromUser.length() == 0){
        	 fromUser = firstName;
          }
         String note = request.getParameter("note");
         String origCommEventId = request.getParameter("origCommEventId");
         String partyIdTo = request.getParameter("partyIdTo");
         String subject = request.getParameter("subject");
         
         String message = request.getParameter("content");
         String partyIdFrom = request.getParameter("partyIdFrom");
         
         String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailContactListScreens.xml#ContactUsEmailNotification";
     	Debug.logInfo("yourName..................."+fromUser, module);
     	
         GenericValue productStore = ProductStoreWorker.getProductStore(request);
         if (productStore == null) {
             String errMsg = "Could not send tell a friend email, no ProductStore found";
             request.setAttribute("_ERROR_MESSAGE_", errMsg);
             return "error";
         }
         String productStoreId = productStore.getString("productStoreId");
          
         String webSiteId=null;
         
         webSiteId = CatalogWorker.getWebSiteId(request);
         Object autoUserLogin = request.getSession().getAttribute("autoUserLogin");
         GenericValue productStoreEmail = null;
         try {
             productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
                     UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
         } catch (GenericEntityException e) {
             String errMsg = "Unable to get product store email setting for contact us: " + e.toString();
             Debug.logError(e, errMsg, module);
             request.setAttribute("_ERROR_MESSAGE_", errMsg);
             return "error";
         }
         String bodyScreenLocation = null;
         if (productStoreEmail == null) {
             String errMsg = "Could not find contact us [" + emailType + "] email settings for the store [" + productStoreId + "]";
             //request.setAttribute("_ERROR_MESSAGE_", errMsg);
             //return "error";
             bodyScreenLocation = defaultScreenLocation;
         }else{
        	 bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
         }
         
         Map paramMap = UtilHttp.getParameterMap(request);
         Debug.logInfo("paramMap..................."+paramMap, module);
         paramMap.put("locale", UtilHttp.getLocale(request));
        // paramMap.put("userLogin", session.getAttribute("userLogin"));
 		if (message != null) {
 			paramMap.put("message", message);
 		}
 		if (fromUser != null) {
 			paramMap.put("yourName", fromUser);
 		}
 		if (isSend != null) {
 			paramMap.put("isSend", isSend);
 		}
 		String sendTo = UtilProperties.getPropertyValue("general.properties","contactUs.toMail");
         Map context = new HashMap(); //FastMap.newInstance();
         context.put("bodyScreenUri", bodyScreenLocation);
         context.put("bodyParameters", paramMap);
         context.put("sendTo", sendTo);
         context.put("contentType", productStoreEmail.get("contentType"));
         context.put("sendFrom", fromMail);
         context.put("sendCc", productStoreEmail.get("ccAddress"));
         context.put("sendBcc", productStoreEmail.get("bccAddress"));
         context.put("subject", productStoreEmail.getString("subject"));

         try {
             dispatcher.runAsync("sendMailFromScreen", context);
             request.setAttribute("_EVENT_MESSAGE_", "Email sent successfully");
             
         } catch (GenericServiceException e) {
             String errMsg = "Problem sending mail: " + e.toString();
             Debug.logError(e, errMsg, module);
             request.setAttribute("_ERROR_MESSAGE_", errMsg);
             return "error";
         }
         return "success";
     } 
    
}
