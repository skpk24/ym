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
package org.ofbiz.accounting.thirdparty.icici;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import java.util.Set;



//import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;


import com.opus.epg.sfa.java.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.base.util.UtilDateTime;

/**
 *@author Sanganagouda
 *@see http://sites.google.com/site/madegoudar/
 * Icici Payseal Services
 */

public class IciciServices {

    public static final String resource = "AccountingUiLabels";
    public static final String resourceErr = "AccountingErrorUiLabels";
    public static final String module = IciciServices.class.getName();

    /**
     *@author Sanganagouda
     */
    private static void setMerchantIdForOrder(GenericDelegator delegator, String orderId, String merchantId) {
        Debug.logVerbose("Setting merchant id for order payment preferences..", module);
        List<GenericValue> paymentPrefs = null;
        try {
            Map<String, String> paymentFields = UtilMisc.toMap("orderId", orderId);
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment preferences for order #" + orderId, module);
        }
        List<GenericValue> valueList= new ArrayList<GenericValue>();
        if (paymentPrefs.size() > 0) {
            Iterator<GenericValue> i = paymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                pref.set("merchantId", merchantId);
                valueList.add(pref);
            }
            
            try {
				delegator.storeAll(valueList);
			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(),module);
				//e.printStackTrace();
			}
            
        }
    }    
    
    /**
     *@author Sanganagouda
     */
    private static boolean setPaymentPreferences(GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String orderId, HttpServletRequest request) {
        Debug.logVerbose("Setting payment preferences..", module);
        List<GenericValue> paymentPrefs = null;
        try {
            Map<String, String> paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment preferences for order #" + orderId, module);
            return false;
        }
        if (paymentPrefs.size() > 0) {
            Iterator<GenericValue> i = paymentPrefs.iterator();
            while (i.hasNext()) {
                GenericValue pref = (GenericValue) i.next();
                boolean okay = setPaymentPreference(dispatcher, userLogin, pref, request);
                if (!okay) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *@author Sanganagouda
     */
    @SuppressWarnings("unchecked")
	private static boolean setPaymentPreference(LocalDispatcher dispatcher, GenericValue userLogin, GenericValue paymentPreference, HttpServletRequest request) {
        Locale locale = UtilHttp.getLocale(request);
        Hashtable<String, String> oHashtable = new Hashtable<String, String>();
        oHashtable = (Hashtable<String, String>) request.getAttribute("authResult");
		
        String paymentStatus = oHashtable.get("paymentStatus");
      	Long paymentDate = new Long(System.currentTimeMillis());
        String transactionId = oHashtable.get("ePGTxnID");
        String gatewayCode = oHashtable.get("AuthIdCode");
        String rootTransRefNum = oHashtable.get("RRN");
        String gatewayFlag = oHashtable.get("RespCode");
        String gatewayMessage = oHashtable.get("Message");
        String gatewayCvResult = oHashtable.get("CVRespCode");
        String avs = oHashtable.get("CVRespCode");
        String paymentAmount = oHashtable.get("authAmount");
        if(paymentAmount == null || paymentAmount.length() == 0){
        	paymentAmount = "0.00";
        }
        if(gatewayMessage != null && gatewayMessage.length() > 0){
        	gatewayMessage = gatewayMessage.replace('+',' ');
        	gatewayMessage = gatewayMessage.replace("%2F",", ");
        	gatewayMessage = gatewayMessage.replace("%3A"," ");
        }
        
        List<GenericValue> toStore = new LinkedList<GenericValue>();
        java.sql.Timestamp authDate = null;
        try {
            authDate = new java.sql.Timestamp(paymentDate.longValue());
        } catch (Exception e) {
            Debug.logError(e, "Cannot create date from long: " + paymentDate, module);
            authDate = UtilDateTime.nowTimestamp();
        }
        paymentPreference.set("maxAmount", new BigDecimal(paymentAmount));
        if ("success".equals(paymentStatus)) {
            paymentPreference.set("statusId", "PAYMENT_AUTHORIZED");
        } else if ("cancelled".equals(paymentStatus)) {
            paymentPreference.set("statusId", "PAYMENT_CANCELLED");
        } else if ("declined".equals(paymentStatus)) {
            paymentPreference.set("statusId", "PAYMENT_DECLINED");
        } else {
            paymentPreference.set("statusId", "PAYMENT_NOT_RECEIVED");
        }
        toStore.add(paymentPreference);
        GenericDelegator delegator = (GenericDelegator) paymentPreference.getDelegator();
        // create the PaymentGatewayResponse
        String responseId = delegator.getNextSeqId("PaymentGatewayResponse");
        GenericValue response = delegator.makeValue("PaymentGatewayResponse");
        response.set("paymentGatewayResponseId", responseId);
        response.set("paymentServiceTypeEnumId", "PRDS_PAY_EXTERNAL");
        response.set("orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"));
        response.set("paymentMethodTypeId", paymentPreference.get("paymentMethodTypeId"));
        response.set("paymentMethodId", paymentPreference.get("paymentMethodId"));
        // set the auth info
        response.set("amount", new BigDecimal(paymentAmount));
        response.set("referenceNum", transactionId);
        response.set("merchantId", oHashtable.get("merchantId"));
        response.set("altReference", rootTransRefNum);
        response.set("gatewayCode", gatewayCode);
        response.set("gatewayFlag", gatewayFlag);
        response.set("gatewayMessage", gatewayMessage);
        response.set("transactionDate", authDate);
        response.set("gatewayAvsResult", avs);
        response.set("gatewayCvResult", gatewayCvResult);
        response.set("responseString", oHashtable.get("responseString"));
        toStore.add(response);
        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot set payment preference/payment info", module);
            return false;
        }
        // create a payment record too
        Map<String, Object> results = null;
        try {
            String comment = UtilProperties.getMessage(resource, "AccountingPaymentReceiveVia", locale);
            //System.out.println("    systemUserLogin   2   "+userLogin);
            results = dispatcher.runSync("createPaymentFromPreference", UtilMisc.toMap("userLogin", userLogin,
                    "orderPaymentPreferenceId", paymentPreference.get("orderPaymentPreferenceId"), "comments", comment+" ICICI payment gateway."));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Failed to execute service createPaymentFromPreference", module);
            request.setAttribute("_ERROR_MESSAGE_", "Problem in executing CreatePaymentFromPreference service, please contact customer care.");
            return false;
        }
        if ((results == null) || (results.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))) {
            Debug.logError((String) results.get(ModelService.ERROR_MESSAGE), module);
            request.setAttribute("_ERROR_MESSAGE_", (String) results.get(ModelService.ERROR_MESSAGE));
            return false;
        }
        return true;
    }

    private static String getPaymentGatewayConfigValue(GenericDelegator delegator, String paymentGatewayConfigId, String paymentGatewayConfigParameterName,
                                                       String resource, String parameterName) {
        String returnValue = "";
        if (UtilValidate.isNotEmpty(paymentGatewayConfigId)) {
            try {
                GenericValue icici = delegator.findOne("PaymentGatewayIcici", UtilMisc.toMap("paymentGatewayConfigId", paymentGatewayConfigId), false);
                if (UtilValidate.isNotEmpty(icici)) {
                    Object iciciField = icici.get(paymentGatewayConfigParameterName);
                    if (iciciField != null) {
                        returnValue = iciciField.toString().trim();
                    }
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            String value = UtilProperties.getPropertyValue(resource, parameterName);
            if (value != null) {
                returnValue = value.trim();
            }
        }
        return returnValue;
    }

    @SuppressWarnings("unused")
	private static String getPaymentGatewayConfigValue(GenericDelegator delegator, String paymentGatewayConfigId, String paymentGatewayConfigParameterName,
                                                       String resource, String parameterName, String defaultValue) {
        String returnValue = getPaymentGatewayConfigValue(delegator, paymentGatewayConfigId, paymentGatewayConfigParameterName, resource, parameterName);
        if (UtilValidate.isEmpty(returnValue)) {
            returnValue = defaultValue;
        }
        return returnValue;
    }

    /**
     *@author Sanganagouda
     *Prepares the request and sends it to ICICI Bank Gateway
     */
    public static String iciciRequest(HttpServletRequest request, HttpServletResponse response) {
    	
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String orderId = (String) request.getAttribute("orderId");
        
        if (orderId == null) {
            Debug.logError("Problems getting orderId, was not found in request", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>OrderID not found, please contact customer care.</li>");
            return "error";
        }
        request.getSession().setAttribute("orderId", orderId);
        // get the order header for total and other information
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderHeader from datasource", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems getting order information, please contact customer care.</li>");
            return "error";
        }
        String partyId = null;
        if(orderHeader != null){
        	String userLoginId =  orderHeader.getString("createdBy");
        	if(userLoginId != null && userLoginId.length()>0){
        		GenericValue userlogin = null;
				try {
					userlogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
	        		if(userlogin != null)
	            		partyId =  userlogin.getString("partyId");					
				} catch (GenericEntityException e) {
					partyId = "_NA_";
					Debug.logError(e.getMessage(),module);
					//e.printStackTrace();
				}
        	}
        }
		if(partyId == null || partyId.length()==0){
			partyId = "_NA_";
		}

        // get the telephone number to pass over
        String phoneNumber = null;
        GenericValue phoneContact = null;

        try {
        	partyId = orderHeader.getString("partyId");
            List<GenericValue> phoneContacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"));
            GenericValue firstNumber = EntityUtil.getFirst(phoneContacts);
            if(firstNumber == null){
            	 phoneContacts = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PHONE_HOME"));
            	 firstNumber = EntityUtil.getFirst(phoneContacts);
            }
            if(firstNumber != null){
            phoneContact = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", firstNumber.getString("contactMechId")));
            phoneNumber = phoneContact.getString("countryCode")+" "+phoneContact.getString("areaCode")+" "+phoneContact.getString("contactNumber");
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order email address", module);
        }        
        
        // get the email address to pass over
        String emailAddress = null;
        GenericValue emailContact = null;
        try {
        	List<GenericValue> emails = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            GenericValue firstEmail = EntityUtil.getFirst(emails);
            emailContact = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", firstEmail.getString("contactMechId")));
            emailAddress = emailContact.getString("infoString");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order email address", module);
        }

        // get the product store
        GenericValue productStore = null;
        try {
            productStore = orderHeader.getRelatedOne("ProductStore");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get ProductStore from OrderHeader", module);

        }
        if (productStore == null) {
            Debug.logError("ProductStore is null", module);
            request.setAttribute("_ERROR_MESSAGE_", "<li>Problems getting merchant configuration, please contact customer care.</li>");
            return "error";
        }

        // get the payment properties file
        GenericValue paymentConfig = ProductStoreWorker.getProductStorePaymentSetting(delegator, productStore.getString("productStoreId"), "EXT_CITI", null, true);
        String configString = null;
        if (paymentConfig != null) {
            configString = paymentConfig.getString("paymentPropertiesPath");
        }

        if (configString == null) {
            configString = "payment.properties";
        }
        
		BillToAddress oBTA 	= new BillToAddress();
		ShipToAddress oSTA 	= new ShipToAddress();
		Merchant oMerchant 	= new Merchant();
		MPIData oMPI 		= new MPIData();
		//CardInfo oCI 		= new CardInfo();
		PostLib oPostLib	= null;
		try {
			oPostLib	= new PostLib();
		} catch (Exception e) {
			Debug.logError(e.getMessage(),module);
			//e.printStackTrace();
		}
		PGReserveData oPGReserveData	= new PGReserveData();
		
		//CustomerDetails oCustomer = new CustomerDetails ();
		//MerchanDise oMerchanDise = new MerchanDise();
		SessionDetail oSessionDetail = new SessionDetail();
		//Address oHomeAddress =new Address();
		//Address oOfficeAddress =new Address();
		//AirLineTransaction oAirLineTrans= new AirLineTransaction();
    		
    	
    	String merchantId;
    	//String merchantResponse1 = (String)request.getAttribute("merchantResponse");
    	String visitId = orderHeader.getString("visitId");
    	String clientIpAddress = null;
    	
    	if(visitId != null) {
    		GenericValue visit = null;
			try {
				visit = delegator.findOne("Visit", UtilMisc.toMap("visitId", visitId), false);
			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(),module);
				//e.printStackTrace();
			}
    		if(visit != null){
    			clientIpAddress =  visit.getString("clientIpAddress");
    		}
    	}
    	if(clientIpAddress == null || clientIpAddress.length()==0)
    		clientIpAddress = "127.0.0.1";    	
    	
    	String hostedUrl = UtilProperties.getPropertyValue("payment", "payment.icici.responseUrl");
    	//String hostedUrl = "http://localhost/control/";
    	//merchantId = "00003561"; //(String)request.getAttribute("MerchantId");
    	merchantId = (String)request.getAttribute("MerchantId");
    	request.getSession().setAttribute("merchantId", merchantId);
    	
    	
    	BigDecimal grandTotal = orderHeader.getBigDecimal("grandTotal");
		BigDecimal transactionfee = (BigDecimal)request.getAttribute("transactionfee");
		if(UtilValidate.isNotEmpty(transactionfee))
			grandTotal = grandTotal.add(transactionfee);
		
		orderHeader.put("grandTotal",grandTotal);
		try {
			orderHeader.store();
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		String grandTotalString = orderHeader.getString("grandTotal");
		
		List<GenericValue> paymentPrefs = null;
        try {
            paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting order payments", module);
            Debug.logError(e, "Unable to get OrderPaymentPreference from OrderHeader", module);
        }
          grandTotalString = grandTotal+"";
        if(UtilValidate.isNotEmpty(paymentPrefs) && paymentPrefs.size() == 1)
        grandTotalString = grandTotal+"";
        else{
        List condList = new ArrayList();
        condList.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS,"EXT_ICICI_FULL"));
        condList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"PAYMENT_NOT_RECEIVED"));
       
        paymentPrefs = EntityUtil.filterByCondition(paymentPrefs, EntityCondition.makeCondition(condList,EntityOperator.AND));
       
        if (UtilValidate.isNotEmpty(paymentPrefs)) {
                 GenericValue paymentPreference = EntityUtil.getFirst(paymentPrefs);
                 if (UtilValidate.isNotEmpty(paymentPreference) && UtilValidate.isNotEmpty(paymentPreference.getBigDecimal("maxAmount"))) {
                grandTotalString = paymentPreference.getBigDecimal("maxAmount")+"";
                 }
        }
        }
		System.out.println("\n\n\n\n\n grandTotalString"+grandTotalString);
//		BigDecimal paybackAmtRec = OrderServices.getPaybackAmtRec(delegator, orderId);
//		if(UtilValidate.isNotEmpty(paybackAmtRec))
//			grandTotal = grandTotal.subtract(paybackAmtRec);
		
 		
    	
    	if(grandTotalString != null && grandTotalString.length()==0){
    		grandTotalString = "0.0";
    	}
    	oMerchant.setMerchantDetails(
    					merchantId
    					,merchantId
    					,merchantId
    					,clientIpAddress
    					//, System.currentTimeMillis()+""
    					,orderId+""
    					,orderId+""
    					, hostedUrl+"?orderId="+orderId+"&mId="+merchantId
    					, "POST"
    					,"INR"
    					,"INV123"
    					,"req.Sale"
    					, grandTotalString+""
    					,"GMT+05:30"
    					, "Ext1"
    					, "true"
    					, "Ext3"
    					, "Ext4"
    					, "Ext5"
    					);

    	Map<String, String> billAddress = getAddressFields(delegator, orderId, "BILLING_LOCATION");
    	if(UtilValidate.isEmpty(billAddress)){
    		billAddress = getAddressFields(delegator, orderId, "SHIPPING_LOCATION");
    	}
    	//System.out.println("\n============================================\n billAddress = "+billAddress+"\n============================================\n");
    	oBTA.setAddressDetails(
    			partyId+""
    			,""+billAddress.get("name")
    			,""+billAddress.get("address1")
    			,""+billAddress.get("address2")
    			,"None"
    			,""+billAddress.get("city")
    			,""+billAddress.get("stateCode")
    			,""+billAddress.get("postalCode")
    			,""+billAddress.get("country")
    			,""+emailAddress
    			);
    	
    	// billAddress = {postalCode=560095, name=null, stateCode=KA, address1=asda, country=IND, city=Bangalore}

    	Map<String, String> shipAdderss = getAddressFields(delegator, orderId, "SHIPPING_LOCATION");
    	//System.out.println("\n============================================\n shipAdderss = "+shipAdderss+"\n============================================\n");
    	oSTA.setAddressDetails(
    			""+shipAdderss.get("name")
    			,""+shipAdderss.get("company")
    			,""+shipAdderss.get("address1")+" "+shipAdderss.get("address2")
    			,""+shipAdderss.get("city")
    			,""+shipAdderss.get("stateCode")
    			,""+shipAdderss.get("postalCode")
    			,""+shipAdderss.get("country")
    			,""+emailAddress
    			);

    		oSessionDetail.setSessionDetails(request.getRemoteAddr(), //This Customer ip,merchant need to send it.
    							  getSecureCookie(request),  //cookie string
    							  request.getLocale().getCountry(),
    							  request.getLocale().getLanguage(), 
    							  request.getLocale().getVariant() ,
    							  request.getHeader ("user-agent")
    					  );
    		Debug.log("\n\n CALLING ICICI \n\n");
    		PGResponse oPGResponse = oPostLib.postSSL(oBTA,oSTA,oMerchant,oMPI,response,oPGReserveData,null,oSessionDetail,null,null);
    		Debug.log("1  ######################### oPGResponse " + oPGResponse + " #########################", module);
    		//PGResponse oPGResponse = //oPostLib.postSSL(oBTA,oSTA,oMerchant,oMPI,response,oPGReserveData,oCustomer,oSessionDetail,oAirLineTrans,null);
    		if(oPGResponse.getRedirectionUrl() != null) {
    			String strRedirectionURL = oPGResponse.getRedirectionUrl();
    			Debug.logInfo("2  ######################### strRedirectionURL " + strRedirectionURL + " #########################", module);
    			try {
					response.sendRedirect(strRedirectionURL);
				} catch (IOException e) {
					Debug.logError(e.getMessage(),module);
					//e.printStackTrace();
				}
    		}
    		else {
    			Debug.logError("3  ######################### Error encountered. Error Code : " + oPGResponse.getRespCode() + " . Message " +  oPGResponse.getRespMessage() + " #########################", module);
                request.setAttribute("_ERROR_MESSAGE_", "Error encountered. Error Code : " +oPGResponse.getRespCode() + " . Message " +  oPGResponse.getRespMessage());
                return "error";    			
    		}
    	
    	return "success";
    }

    /**
     *@author Sanganagouda
     */
    public static String getSecureCookie(HttpServletRequest request) {
        String secureCookie = null;
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) { 
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("vsc")) {
                    secureCookie = cookies[i].getValue().trim();
                    break; 
                }
            }
        }
        return secureCookie;
    }   

    /**
     *@author Sanganagouda
     */
	public static Map<String, String> getAddressFields(GenericDelegator delegator, String orderId, String addressType){
		Map<String, String> addressMap = new HashMap<String, String>();
	    // get the contact address to pass over
	    GenericValue contactAddress = null;
	    if(addressType == null || addressType.length() == 0)
	    	addressType = "SHIPPING_LOCATION";
	    
	    try {
	        List<GenericValue> addresses = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", addressType));
	        
	        if(addresses == null || addresses.size() == 0){
	        	if(addressType.equalsIgnoreCase("BILLING_LOCATION")){
	        		addressType = "SHIPPING_LOCATION";
	        		addresses = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", addressType));
	        	}else{
	        		addressType = "BILLING_LOCATION";
	        		addresses = delegator.findByAnd("OrderContactMech", UtilMisc.toMap("orderId", orderId, "contactMechPurposeTypeId", addressType));	        		
	        	}
	        }
	        
	        GenericValue contactMech = null;
	        if(addresses != null){
	        	contactMech = EntityUtil.getFirst(addresses);
	        	if(contactMech != null)
	        		contactAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.getString("contactMechId")));
	        }
	        
	    } catch (GenericEntityException e) {
	        Debug.logWarning(e, "Problems getting order contact information", module);
	    }	
		//StringBuffer address = null;
	    String country= null;
	    String city = null;
	    String geoStateCode= null;
	    if(contactAddress != null) {
	        String name = null;
	        if(contactAddress != null) {
	            if(contactAddress.get("attnName") != null && contactAddress.getString("attnName").length() > 0)
	                name = contactAddress.getString("attnName");
	            else if(contactAddress.get("toName") != null && contactAddress.getString("toName").length() > 0)
	                	name = contactAddress.getString("toName");
	        }
	        if(UtilValidate.isNotEmpty(name)){
	        	addressMap.put("name", name);
	        }
	        
	        if(UtilValidate.isEmpty(name)){
	        	try {
					GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), true);
					////System.out.println("\n============================================\n order = "+order+"\n============================================\n");
					GenericValue userLogin =  delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", order.getString("createdBy")), true);
					////System.out.println("\n============================================\n userLogin = "+userLogin+"\n============================================\n");
					GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")), true);
					////System.out.println("\n============================================\n person = "+person+"\n============================================\n");
					if(UtilValidate.isNotEmpty(person)){
						addressMap.put("name", person.getString("firstName")+" "+ person.getString("lastName"));
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
	        String address1 = new String();
	        if(contactAddress.get("address1") != null) {
	        	address1 = contactAddress.getString("address1").trim();
	        	addressMap.put("address1", address1);
	        }
	        
	        String address2 = new String();
	        if(contactAddress.get("address2") != null) {
	        	address2 = contactAddress.getString("address2").trim();
	        	addressMap.put("address2", address2);
	        }
	        
	        if(contactAddress.get("city") != null) {
	            city = contactAddress.getString("city").trim();
	            addressMap.put("city", city);
	        }else{
	        	addressMap.put("city", "Not Available");
	        }
	        
	        if(contactAddress.get("stateProvinceGeoId") != null) {
	        	//String stateProvinceGeoId = contactAddress.getString("stateProvinceGeoId").trim();
	        	geoStateCode = getStateGeoCode(contactAddress.getString("stateProvinceGeoId"), delegator);
	        	addressMap.put("stateCode", geoStateCode);
	        }else{
	        	addressMap.put("stateCode", "Not Available");
	        }
	        
	        String postalCode= null;
	        if (contactAddress.get("postalCode") != null) {
	        	postalCode = contactAddress.getString("postalCode").trim();
	        	addressMap.put("postalCode", postalCode);
	        }else{
	        	addressMap.put("postalCode", "NA");
	        }
	        
	        if (contactAddress.getString("countryGeoId") != null) {
	        	country = contactAddress.getString("countryGeoId").trim();
	        	addressMap.put("postalCode", postalCode);
		        if(country == null || country.length()==0){
		        	country = "IND";
		        }
		        addressMap.put("country", country);
	        }else{
	        	addressMap.put("country", "NA");
	        }
	    }
	    Debug.logInfo("  ######################### addressMap #########################"+addressMap, module);
		return addressMap;
	}

    /**
     *@author Sanganagouda
     *reads the response sent from the ICICI bank gateway and performs the related operations depending upon the response received.
     */
	public static String iciciPayNotify(HttpServletRequest request, HttpServletResponse response) {
		Debug.logInfo("  ######################### entered in iciciPayNotify method #########################", module);
		String merchantId = null;
		String merchantKeyPath = null;
		String orderId = null;
		javax.servlet.http.HttpSession session = request.getSession();
		
		merchantId = request.getParameter("mId");
		if(merchantId == null || merchantId.length() == 0) merchantId = (String)session.getAttribute("merchantId");
		//if(merchantId == null || merchantId.length() == 0)	merchantId = "00003561";
		
		orderId = request.getParameter("orderId");
		//orderId = (String) request.getAttribute("orderId");
		if(orderId == null || orderId.length() == 0){
			orderId = (String) session.getAttribute("orderId");
		}
		merchantKeyPath = UtilProperties.getPropertyValue("payment", "payment.icici.merchantKeyPath");
		//merchantKeyPath = "E:/New folder/nichepro/youmart/applications/accounting/icicikeys/";
		
		
	    String astrResponseMethod= request.getMethod(); 
	    String strMerchantId= merchantId;
	    String astrDirectoryPath=merchantKeyPath;
	    String astrClearData = null;	
	    String respCode =null;
	    Hashtable<String, Object> oHashtable = new java.util.Hashtable<String, Object>();
	    
        Locale locale = UtilHttp.getLocale(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        /*Map <String, Object> parametersMap = UtilHttp.getParameterMap(request);
        Set<String> keySet = parametersMap.keySet();
        Iterator<String> i = keySet.iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            String value = request.getParameter(name);
            Debug.logInfo("### Param: " + name + " => " + value, module);
        }*/
        String paymentStatus = null;
        
	    if(astrResponseMethod.equals("POST")||astrResponseMethod.equals("post")){
	    	String astrResponseData= request.getParameter("DATA"); // getting null pointer exception
	    	Debug.logInfo("  ######################### astrResponseData" + astrResponseData + " #########################", module);
			try {
				astrClearData =validateEncryptedData(astrResponseData,astrDirectoryPath,strMerchantId);
			} catch (Exception e) {
				Debug.logError(e.getMessage(),module);
				//e.printStackTrace();
			}
			
			oHashtable.put("merchantId",merchantId);
		   	java.util.StringTokenizer oStringTokenizer=new java.util.StringTokenizer(astrClearData,"&");
			while(oStringTokenizer.hasMoreElements()){
				String strData = (String)oStringTokenizer.nextElement();
				java.util.StringTokenizer oObj1=new java.util.StringTokenizer(strData,"=");
				String strKey=(String)oObj1.nextElement();
				String strValue=(String)oObj1.nextElement();
				oHashtable.put(strKey,strValue);
			}
			//request.setAttribute("authResult", oHashtable);
			Debug.log("  ######################### Response Data" + oHashtable + " #########################", module);
			if(orderId == null || orderId.length() == 0){
			orderId = (String)oHashtable.get("TxnID");
			}
			if(oHashtable != null){
				respCode=(String) oHashtable.get("RespCode");
			}
			
			int responseCode = Integer.parseInt(respCode);
			if(responseCode == 0) {
				paymentStatus = "success";
			} else {
				paymentStatus = "declined";
			}
    	}
    	oHashtable.put("responseString", astrClearData); 
	    oHashtable.put("paymentStatus", paymentStatus);
        // get the user
        if (userLogin == null) {
            String userLoginId = "system";
            try {
                userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "iciciEvents.problemsGettingAuthenticationUser", locale));
                return "error";
            }
        }
        // get the order header
        GenericValue orderHeader = null;
        if (UtilValidate.isNotEmpty(orderId)) {
            try {
                orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
                
                if (orderHeader == null) {
                    Debug.logError("Cannot get the order header for order: " + orderId, module);
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "iciciEvents.problemsGettingOrderHeader", locale));
                    return "error";
                }                
                
                String authAmount = "";
                authAmount = orderHeader.getString("grandTotal");
                oHashtable.put("authAmount",authAmount);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get the order header for order: " + orderId, module);
                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "iciciEvents.problemsGettingOrderHeader", locale));
                return "error";
            }
        } else {
            Debug.logError("ICICI payment gateway did not callback with a valid orderId!", module);
            request.setAttribute("_ERROR_MESSAGE_", "ICICI payment gateway did not callback with a valid orderId!");
            return "error";
        }
        
        String gatewayMessage = (String) oHashtable.get("Message");
        if(gatewayMessage != null){
        	gatewayMessage = gatewayMessage.replace('+',' ');
        	gatewayMessage = gatewayMessage.replace("%2F",", ");
        	gatewayMessage = gatewayMessage.replace("%3A"," ");
    		request.setAttribute("_EVENT_MESSAGE_", gatewayMessage);
        }
        
        request.setAttribute("authResult", oHashtable);
        // attempt to start a transaction
        boolean okay = true;
        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();
            setMerchantIdForOrder(delegator, orderId, merchantId);
            // authorized
            if (paymentStatus == null) {
                okay = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
            }else if ("success".equals(paymentStatus)) {
                okay = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
                if(okay)
                	ShoppingCartEvents.clearCart(request, response);
            } else if ("declined".equals(paymentStatus)) {
            	request.setAttribute("_EVENT_MESSAGE_", "Bank declined transaction, order cancelled.");
                okay = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
            }
            okay = true;
            if (okay) {
                // set the payment preference
                //okay = setPaymentPreferences(delegator, dispatcher, userLogin, orderId, request);
                GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), true);
                //System.out.println("    systemUserLogin    "+systemUserLogin);
                okay = setPaymentPreferences(delegator, dispatcher, systemUserLogin, orderId, request);
            }
        } catch (Exception e) {
            String errMsg = "Error handling ICICI payment gateway notification";
            Debug.logError(e, errMsg, module);
            try {
                TransactionUtil.rollback(beganTransaction, errMsg, e);
            } catch (GenericTransactionException gte2) {
                Debug.logError(gte2, "Unable to rollback transaction", module);
                request.setAttribute("_ERROR_MESSAGE_", "Unable to rollback transaction");
            }
        } finally {
            if (!okay) {
                try {
                    TransactionUtil.rollback(beganTransaction, "Failure in processing ICICI payment gateway callback", null);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to rollback transaction", module);
                    request.setAttribute("_ERROR_MESSAGE_", "Unable to rollback transaction");
                }
            } else {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException gte) {
                    Debug.logError(gte, "Unable to commit transaction", module);
                    request.setAttribute("_ERROR_MESSAGE_", "Unable to commit transaction");
                }
            }
        }
        if (okay) {
            // attempt to release the offline hold on the order (workflow)
            OrderChangeHelper.releaseInitialOrderHold(dispatcher, orderId);
            // call the email confirm service
            Map<String, Object> emailContext = UtilMisc.toMap("orderId", orderId, "userLogin", userLogin);
            try {
                dispatcher.runSync("sendOrderConfirmation", emailContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems sending email confirmation", module);
            }
        }
        Debug.logInfo("  ######################### exited from iciciPayNotify method #########################", module);
        return "success";
	}

    /**
     *@author Sanganagouda
     */
	@SuppressWarnings("finally")
	public static String validateEncryptedData(String astrResponseData,String astrDirectoryPath,String strMerchantId) throws Exception {
		EPGMerchantEncryptionLib oEncryptionLib = new EPGMerchantEncryptionLib();
		String astrClearData = null;
		try {
				java.io.FileInputStream oFileInputStream =  new java.io.FileInputStream(new java.io.File(astrDirectoryPath + strMerchantId+".key"));
				java.io.BufferedReader oBuffRead = new java.io.BufferedReader(new java.io.InputStreamReader(oFileInputStream));
				String strModulus = oBuffRead.readLine();
				if(strModulus == null) {
					throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
				}
				strModulus = decryptMerchantKey(strModulus, strMerchantId);
				if(strModulus == null) {
					throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
				}
				String strExponent = oBuffRead.readLine();
				if(strExponent == null) {
					throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
				}
				strExponent = decryptMerchantKey(strExponent, strMerchantId);
				if(strExponent == null) {
					throw new SFAApplicationException("Invalid credentials. Transaction cannot be processed");
				}
				astrClearData =oEncryptionLib.decryptDataWithPrivateKeyContents(astrResponseData,strModulus,strExponent);
  		}catch(Exception oEx) {
  			oEx.printStackTrace();
	  	}
		finally {
			return astrClearData;
		}
	}
 
    /**
     *@author Sanganagouda
     */
    public static String decryptMerchantKey(String astrData , String astrMerchantId) throws Exception {
		return(decryptData(astrData, (astrMerchantId+astrMerchantId).substring(0, 16)));
    }

    /**
     *@author Sanganagouda
     */
    public static String decryptData(String strData , String strKey)throws Exception {
	   	if(strData==null || strData==""){
			return null;
		}
		if(strKey==null || strKey==""){
			return null;
		}
		EPGCryptLib moEPGCryptLib = new EPGCryptLib();
		String strDecrypt=moEPGCryptLib.Decrypt(strKey, strData);
		return strDecrypt;
    }

    /**
     *@author Sanganagouda
     */
	public static String callFullPaymentRequest(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("  ######################### Full Payment Method Called  (MerchantId =  )#########################");
		String MerchantId = "96008628";//UtilProperties.getPropertyValue("payment", "merchantId.icici.fullPayment");
		//System.out.println("  ######################### Full Payment Method Called  (MerchantId = "+MerchantId+" )#########################");
		request.setAttribute("MerchantId", MerchantId);
		return iciciRequest(request, response);
	}
    
	/**
     *@author Sanganagouda
     */
	public static String call3EMIRequest(HttpServletRequest request, HttpServletResponse response) {
		String MerchantId = UtilProperties.getPropertyValue("payment", "merchantId.icici.3EMI");
		Debug.logInfo("  ######################### 3 EMI Payment Method Called (MerchantId = "+MerchantId+" )#########################", module);
		request.setAttribute("MerchantId", MerchantId);
		return iciciRequest(request, response);
	}
	
    /**
     *@author Sanganagouda
     */
	public static String call6EMIRequest(HttpServletRequest request, HttpServletResponse response) {
		String MerchantId = UtilProperties.getPropertyValue("payment", "merchantId.icici.6EMI");
		Debug.logInfo("  ######################### 6 EMI Payment Method Called (MerchantId = "+MerchantId+" )#########################", module);
		request.setAttribute("MerchantId", MerchantId);
		return iciciRequest(request, response);
	}
	
    /**
     *@author Sanganagouda
     */
	public static String call9EMIRequest(HttpServletRequest request, HttpServletResponse response) {
		String MerchantId = UtilProperties.getPropertyValue("payment", "merchantId.icici.9EMI");
		Debug.logInfo("  ######################### 9 EMI Payment Method Called (MerchantId = "+MerchantId+" )#########################", module);
		request.setAttribute("MerchantId", MerchantId);
		return iciciRequest(request, response);
	}
	
    /**
     *@author Sanganagouda
     */
	public static String call12EMIRequest(HttpServletRequest request, HttpServletResponse response) {
		String MerchantId = UtilProperties.getPropertyValue("payment", "merchantId.icici.12EMI");
		Debug.logInfo("  ######################### 12 EMI Payment Method Called (MerchantId = "+MerchantId+" )#########################", module);
		request.setAttribute("MerchantId", MerchantId);
		return iciciRequest(request, response);
	}	
	
	
	public static String getStateGeoCode(String stateGeoId, Delegator delegator) 
    {
    	String stateCode = null;
        if(UtilValidate.isNotEmpty(stateGeoId))
        {
        	try 
        	{
				GenericValue geoEntity = delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", stateGeoId));
				stateCode = geoEntity.getString("geoCode");
			} 
        	catch (GenericEntityException e) 
			{
        		Debug.logError(e.getMessage(),module);
        		//e.printStackTrace();
			}
        }
        else
        {
        	return null;
        }

        return stateCode;
    }

}
