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
package org.ofbiz.accounting.thirdparty.citrusPay;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

/**
 * @author Ajaya
 * @see http://sites.google.com/site/madegoudar/
 */
public class CitrusPayServices {

    public static final String resource = "AccountingUiLabels";
    public static final String resourceErr = "AccountingErrorUiLabels";
    public static final String commonResource = "CommonUiLabels";
    public static final String module = CitrusPayServices.class.getName();
    
    
    /**
     *@author Ajaya
     */
	public static String callCitruspay(HttpServletRequest request, HttpServletResponse response) {
			
		Debug.logInfo("Started preparing order data to send citrus pay bank gateway.. ", module);
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
	    String orderId = (String) request.getAttribute("orderId");
	    if (orderId == null) orderId = (String) session.getAttribute("orderId");
	    if (orderId == null) {
	        Debug.logError("Problems getting orderId, was not found in request", module);
	        request.setAttribute("_ERROR_MESSAGE_", "<li>OrderID not found, please contact customer service.");
	        return "error";
	    }
	    
	    session.setAttribute("orderId", orderId);
	    // get the order header for total and other information
	    GenericValue orderHeader = null;
	    try {
	        orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
	    } catch (GenericEntityException e) {
	        Debug.logError(e, "Cannot not get OrderHeader from datasource", module);
	        request.setAttribute("_ERROR_MESSAGE_", "<li>Problems getting order information, please contact customer service.");
	        return "error";
	    }
	    String citrusPayBankId = getBankIdForOrder(delegator, orderId);
		
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
		/*String orderTotalString = orderHeader.getString("grandTotal");
		
		BigDecimal paybackAmtRec = OrderServices.getPaybackAmtRec(delegator, orderId);
		if(UtilValidate.isNotEmpty(paybackAmtRec))
			grandTotal = grandTotal.subtract(paybackAmtRec);
		
		orderTotalString = grandTotal+"";
		
		if(orderTotalString != null && orderTotalString.length()==0){
			orderTotalString = "0.0";
		}*/
	
		String transId = null;
		if(UtilValidate.isInteger(orderId)){
			transId = orderId.substring(4);
		}else{
			GenericValue productStore = (GenericValue) ProductStoreWorker.getProductStore(request);
			String prefix = "";
			if(productStore != null && productStore.getString("orderNumberPrefix") != null){
				prefix = productStore.getString("orderNumberPrefix");
			}
			
			try {
				GenericValue partyAcctPref = delegator.findOne("PartyAcctgPreference", false, UtilMisc.toMap("partyId", "Company"));
				if(partyAcctPref != null && partyAcctPref.getString("orderIdPrefix") != null){
					prefix = prefix+partyAcctPref.getString("orderIdPrefix");
				}
			} catch (GenericEntityException e) {
				Debug.logError(e.getMessage(), module);
			}
			
			transId = orderId.substring(prefix.length());
		}
		
		if (transId == null){
			try {
				boolean beganTransaction = false;
				beganTransaction = TransactionUtil.begin();
				GenericValue sequence = delegator.findOne("SequenceValueItem", UtilMisc.toMap("seqName", "TransactionId"), false);
				transId = sequence.getString("seqId");
				long transNo = 0;
				transNo = Long.parseLong(transId);
				transNo = transNo+1;
				sequence.set("seqId", transNo);
				delegator.store(sequence);
				TransactionUtil.commit(beganTransaction);
			} catch (GenericEntityException e1) {
				e1.printStackTrace();
			}	
			if (transId == null){
				Random rand = new Random();
				int num = rand.nextInt(100000000);
				transId = Integer.toString(num);
			}
		}
		Map<String, String> data = new HashMap<String, String>();
		data.put("transId", transId);
		data.put("orderAmount", grandTotal+"");
		data.put("citrusPayBankId", citrusPayBankId);
		
		OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
		GenericValue placingParty = orderReadHelper.getPlacingParty();
		
		String partyId = placingParty.getString("partyId");
		
		String firstName = placingParty.getString("firstName");
		String lastName = placingParty.getString("lastName");
		
		data.put("firstName", firstName);
		data.put("lastName", lastName);
		
		List<Map<String, Object>> partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
		
		if(UtilValidate.isNotEmpty(partyContactMechValueMaps))
		for(Map<String, Object> partyContactMechValueMap : partyContactMechValueMaps){
			GenericValue contactMech = (GenericValue) partyContactMechValueMap.get("contactMech");
			GenericValue contactMechType = (GenericValue) partyContactMechValueMap.get("contactMechType");
			GenericValue partyContactMech = (GenericValue) partyContactMechValueMap.get("partyContactMech");
			
			if(UtilValidate.isNotEmpty(contactMech))
			if("POSTAL_ADDRESS".equals(contactMech.getString("contactMechTypeId")))
			{
				String postalcontactId = contactMech.getString("contactMechId");
	             
				GenericValue postalAddress = (GenericValue) partyContactMechValueMap.get("postalAddress");
				if(UtilValidate.isNotEmpty(postalAddress))
				{
					String address1 = postalAddress.getString("address1");
					String city = postalAddress.getString("city");
					String postalCode = postalAddress.getString("postalCode");
					String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
					String countryGeoId = postalAddress.getString("countryGeoId");
					
					data.put("address", address1);
					data.put("city", city);
					data.put("postalCode", postalCode);
					data.put("state", stateProvinceGeoId);
					data.put("country", countryGeoId);
				}
			}else if("TELECOM_NUMBER".equals(contactMech.getString("contactMechTypeId"))){
				String telecontactId = contactMech.getString("contactMechId");
				GenericValue telecomNumber = (GenericValue) partyContactMechValueMap.get("telecomNumber");
				if(UtilValidate.isNotEmpty(telecomNumber))
				{
					String contactNumber = telecomNumber.getString("contactNumber");
					data.put("contactNumber", contactNumber);
				}
			}else if("EMAIL_ADDRESS".equals(contactMech.getString("contactMechTypeId"))){
					String email = contactMech.getString("infoString");
					data.put("email", email);
			}
		}
		System.out.println("   placingParty    "+placingParty);
		//getOrderItemShipGroups();
		
		//requestData = prepareRequestData(data);
		request.setAttribute("requestData", data);
		session.setAttribute("requestData", data);
		
		
		Debug.logInfo("requestData = "+data, module);
		Debug.logInfo("Sending data to CitrusPay payment gateway..", module);
		
		return "success";
	
	}
    
    /**
     *@author Ajaya
     */
    private static String getBankIdForOrder(GenericDelegator delegator, String orderId) {
        Debug.logVerbose("Getting Bank Id From Order preferences..", module);
        List<GenericValue> paymentPrefs = null;
        String techProcessBankId = null;
        try {
            Map<String, String> paymentFields = UtilMisc.toMap("orderId", orderId,"statusId", "PAYMENT_NOT_RECEIVED","paymentMethodTypeId","EXT_NETBANKING");
            paymentPrefs = delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "(Inside getBankIdForOrder method) Cannot get payment preferences for order #" + orderId, module);
            return "NA";
        }
        
         if (paymentPrefs.size() > 0) {
			GenericValue pref = (GenericValue) paymentPrefs.get(0);
			techProcessBankId = pref.getString("bankId");
        }
        return techProcessBankId;
    }
    
    
    /**
     *@author Ajaya
     */
    public static String citruspayNotify(HttpServletRequest request, HttpServletResponse response) {
    	
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Hashtable<String, Object> result1 = new java.util.Hashtable<String, Object>();
    	
    	try{
    		
    		String key = "300541dcbe637bc63b3999e0a7e262b80aa3d100";
			String data="";
			String txnId=request.getParameter("TxId");
			String txnStatus=request.getParameter("TxStatus"); 
			String amount=request.getParameter("amount"); 
			String pgTxnId=request.getParameter("pgTxnNo");
			String issuerRefNo=request.getParameter("issuerRefNo"); 
			String authIdCode=request.getParameter("authIdCode");
			String firstName=request.getParameter("firstName");
			String lastName=request.getParameter("lastName");
			String pgRespCode=request.getParameter("pgRespCode");
			String zipCode=request.getParameter("addressZip");
			String reqSignature=request.getParameter("signature");
 			String signature="";
			String orderId = null;
        	orderId = (String) session.getAttribute("orderId");
        	
			 Debug.logInfo("Payment Gateway signature"+reqSignature, module);

			boolean flag = true;
			if (txnId != null) {
				data += txnId;
			}
			if (txnStatus != null) {
				data += txnStatus;
			}
			if (amount != null) {
				data += amount;
			}
			if (pgTxnId != null) {
				data += pgTxnId;
			}
			if (issuerRefNo != null) {
				data += issuerRefNo;
			}
			if (authIdCode != null) {
				data += authIdCode;
			}
			if (firstName != null) {
				data += firstName;
			}
			if (lastName != null) {
				data += lastName;
			}
			if (pgRespCode != null) {
				data += pgRespCode;
			}
			if (zipCode != null) {
				data += zipCode;
			}
			com.citruspay.pg.net.RequestSignature sigGenerator = new com.citruspay.pg.net.RequestSignature(); 
			
			try {
				signature = sigGenerator.generateHMAC(data, key);
				 Debug.logInfo("Generated Signature From the input"+signature, module);
				if(reqSignature !=null && !reqSignature.equalsIgnoreCase("") &&!signature.equalsIgnoreCase(reqSignature)){
					flag = false;
				}
			}catch(Exception e){
				e.printStackTrace();	
			}
			
			if(flag){
				String TxId = request.getParameter("TxId") == null ? "" : request.getParameter("TxId");
				String TxRefNo = request.getParameter("TxRefNo") == null ? "" : request.getParameter("TxRefNo");
				if(request.getParameter("TxMsg") !=null){
					String TxMsg = request.getParameter("TxMsg");
				}else if(request.getParameter("mandatoryErrorMsg") !=null){
					String mandatoryErrorMsg = request.getParameter("mandatoryErrorMsg");
				}else if(request.getParameter("paidTxnExists") !=null){
					String paidTxnExists = request.getParameter("paidTxnExists");
				}
			}else{
				String res = "Request Signature Error";
		 }
		
			
			String email = request.getParameter("email") == null ? "" : request.getParameter("email");
			String addressStreet1 = request.getParameter("addressStreet1") == null ? "" : request.getParameter("addressStreet1");
			String addressStreet2 = request.getParameter("addressStreet2") == null ? "" : request.getParameter("addressStreet2");
			String addressCity = request.getParameter("addressCity") == null ? "" : request.getParameter("addressCity");
			String addressState = request.getParameter("addressState") == null ? "" : request.getParameter("addressState");
			String addressCountry = request.getParameter("addressCountry") == null ? "" : request.getParameter("addressCountry");
			String addressZip = request.getParameter("addressZip") == null ? "" : request.getParameter("addressZip");
			String mobileNo = request.getParameter("mobileNo") == null ? "" : request.getParameter("mobileNo");
			
		
			
			String paymentMode = request.getParameter("paymentMode") == null ? "" : request.getParameter("paymentMode");
			String TxGateway = request.getParameter("TxGateway") == null ? "" : request.getParameter("TxGateway");
			String maskedCardNumber = request.getParameter("maskedCardNumber") == null ? "" : request.getParameter("maskedCardNumber");
			String cardType = request.getParameter("cardType") == null ? "" : request.getParameter("cardType");
			String issuerCode = request.getParameter("issuerCode") == null ? "" : request.getParameter("issuerCode");
			
			
			
			
			if (userLogin == null) {
                String userLoginId = "system";
                try {
                    userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "citiEvents.problemsGettingAuthenticationUser", locale));
                    return "error";
                }
            }
			
			
			
			// attempt to start a transaction
            boolean okay = false;
            boolean beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin();
                Debug.logInfo("Transaction Details"+txnStatus, module);
                Debug.logInfo("Transaction Message"+request.getParameter("TxMsg"), module);

                 
                if (txnStatus.contains("SUCCESS")) {
                    okay = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
                    if(okay)
                    	ShoppingCartEvents.clearCart(request, response);
                } else if (txnStatus.equals("PG_REJECTED")) {
                	request.setAttribute("_EVENT_MESSAGE_", "Bank declined transaction.");
                    okay = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
                }
                /*if (okay) {
            		//set response data in request
            		request.setAttribute("authResult", result);
                    // set the payment preference
                    okay = setPaymentPreferences(delegator, dispatcher, userLogin, orderId, request);
                }*/
            } catch (Exception e) {
                String errMsg = "Error handling techprocess gateway notification";
                Debug.logError(e, errMsg, module);
                try {
                    TransactionUtil.rollback(beganTransaction, errMsg, e);
                } catch (GenericTransactionException gte2) {
                    Debug.logError(gte2, "Unable to rollback transaction", module);
                    request.setAttribute("_ERROR_MESSAGE_", "Unable to rollback transaction");
                }
            } finally {
            	Debug.logInfo("Okay Flag = "+okay, module);
                if (!okay) {
                    try {
                        TransactionUtil.rollback(beganTransaction, "Failure in processing techprocess gateway callback", null);
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
                	 if (txnStatus.contains("SUCCESS")) {
                    dispatcher.runSync("sendOrderConfirmation", emailContext);
                }
               } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems sending email confirmation", module);
                }
            }
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
        return "success";	
    }
}


