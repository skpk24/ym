package org.ofbiz.marketing.marketing;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

//import javolution.util.FastList;
//import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class AutoEmailServices {
	
    public static final String module = AutoEmailServices.class.getName();
    public static final String resource = "PartyErrorUiLabels";

    public static Map<String, Object> sendAutoBirthdayEmails(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        //List<Object> errorMessages = FastList.newInstance();
        List<Object> errorMessages = new ArrayList<Object>();
        String errorCallingSendMailService = UtilProperties.getMessage(resource, "commeventservices.errorCallingSendMailService", locale);
        String errorInSendEmailToContactListService = UtilProperties.getMessage(resource, "commeventservices.errorInSendEmailToContactListService", locale);
        String skippingInvalidEmailAddress = UtilProperties.getMessage(resource, "commeventservices.skippingInvalidEmailAddress", locale);
        GenericValue autoMailTemplate = null;
        
        // Any exceptions thrown in this block will cause the service to return error
        EntityListIterator eli = null;
        try {
        	List<GenericValue>  allBirthdayTemplates = delegator.findByAnd("AutoMailTemplate", UtilMisc.toMap("templateType", "BIRTH_DAY_TEMPLATE"), null);
            
            if(allBirthdayTemplates != null)
            	autoMailTemplate = EntityUtil.getFirst(allBirthdayTemplates);
            if(autoMailTemplate == null)
            	return ServiceUtil.returnError("No gift templates available");
            
    		Map result = birthdayPartyDetails(delegator, autoMailTemplate.getString("daysBefore"));
    		List<Map> partyDetails = null;
    		
            if(result != null)
            partyDetails = (List) result.get("partyDetails");
            
            //System.out.println("********************************************************");
            //System.out.println("partyDetails size "+partyDetails.size());
            //System.out.println("partyDetails "+partyDetails);
            //System.out.println("********************************************************");
            
          //  Map<String, Object> sendMailParams = FastMap.newInstance();
            Map<String, Object> sendMailParams = new HashMap<String, Object>();
            
            String fromEmailId = autoMailTemplate.getString("fromEmailId");
            if(fromEmailId != null)
            	sendMailParams.put("sendFrom", fromEmailId);
            else
            	sendMailParams.put("sendFrom", "newsletters@nichesuite.com");
            sendMailParams.put("subject", autoMailTemplate.getString("subject"));
            sendMailParams.put("contentType", autoMailTemplate.getString("contentMimeTypeId"));
            sendMailParams.put("userLogin", userLogin);

            // loop through the list iterator
            for (Map details: partyDetails) {
                Debug.logInfo("email Id: " + details.get("emailId"), module);
                // Any exceptions thrown in this inner block will only relate to a single email of the list, so should
                //  only be logged and not cause the service to return an error
                try {

                    String emailAddress = (String) details.get("emailId");
                    if (UtilValidate.isEmpty(emailAddress)) continue;
                    emailAddress = emailAddress.trim();

                    if (! UtilValidate.isEmail(emailAddress)) {
                        // If validation fails, just log and skip the email address
                        Debug.logError(skippingInvalidEmailAddress + ": " + emailAddress, module);
                        errorMessages.add(skippingInvalidEmailAddress + ": " + emailAddress);
                        continue;
                    }
                    
                    String partyId = (String) details.get("partyId");
                    sendMailParams.put("sendTo", emailAddress);
                    sendMailParams.put("partyId", partyId);
                    
                    Debug.logInfo("Sending auto email to party [" + partyId + "] : " + emailAddress, module);
                    // Make the attempt to send the email to the address
                    Map<String, Object> tmpResult = null;
                    String body = autoMailTemplate.getString("templateData");
                    if(body == null) body = "Greetings from NicheSuite";
                    sendMailParams.put("body", body);
                    tmpResult = dispatcher.runSync("sendMail", sendMailParams, 360, true);

                    if (tmpResult == null || ServiceUtil.isError(tmpResult)) {
                        if (ServiceUtil.getErrorMessage(tmpResult).startsWith("[ADDRERR]")) {
                            continue;
                        } else {
                            // If the send attempt fails, just log and skip the email address
                            Debug.logError(errorCallingSendMailService + ": " + ServiceUtil.getErrorMessage(tmpResult), module);
                            errorMessages.add(errorCallingSendMailService + ": " + ServiceUtil.getErrorMessage(tmpResult));
                            continue;
                        }
                    } else {
                        // attach the parent communication event to the new event created when sending the mail
                        String thisCommEventId = (String) tmpResult.get("communicationEventId");
                        GenericValue thisCommEvent = delegator.findOne("CommunicationEvent", false, "communicationEventId", thisCommEventId);
                        if (thisCommEvent != null) {
                            
                            thisCommEvent.set("parentCommEventId", thisCommEventId);
                            thisCommEvent.set("autoMailTypeId", "BIRTHDAY_EMAIL");
                            thisCommEvent.set("partyIdFrom", "Company");
                            thisCommEvent.store();
                        }
                        String messageId = (String) tmpResult.get("messageId");
                    }

                // Don't return a service error just because of failure for one address - just log the error and continue
                } catch (GenericEntityException nonFatalGEE) {
                    Debug.logError(nonFatalGEE, errorInSendEmailToContactListService, module);
                    errorMessages.add(errorInSendEmailToContactListService + ": " + nonFatalGEE.getMessage());
                } catch (GenericServiceException nonFatalGSE) {
                    Debug.logError(nonFatalGSE, errorInSendEmailToContactListService, module);
                    errorMessages.add(errorInSendEmailToContactListService + ": " + nonFatalGSE.getMessage());
                }
            }

        } catch (GenericEntityException fatalGEE) {
            return ServiceUtil.returnError(fatalGEE.getMessage());
        } finally {
            if (eli != null) {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return errorMessages.size() == 0 ? ServiceUtil.returnSuccess() : ServiceUtil.returnError(errorMessages);
    }
	
    public static Map<String, Object> sendAutoEmailAsCommEvent(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String communicationEventId = (String) context.get("communicationEventId");
        String templateId = (String) context.get("templateId");
        String templateType = (String) context.get("templateType");
        String partyDetails = (String) context.get("partyDetails");

        Map<String, Object> result = ServiceUtil.returnSuccess();
       // List<Object> errorMessages = FastList.newInstance(); // used to keep a list of all error messages returned from sending emails to contact list
        List<Object> errorMessages = new ArrayList<Object>();
        try {
            // find the communication event and make sure that it is actually an email
            GenericValue autoMailTemplate = delegator.findByPrimaryKey("AutoMailTemplate", UtilMisc.toMap("templateId", templateId));
            if (autoMailTemplate == null) {
                String errMsg = UtilProperties.getMessage(resource,"commeventservices.communication_event_not_found_failure", locale);
                return ServiceUtil.returnError(errMsg + " " + templateId);
            }
           templateType = autoMailTemplate.getString("templateType");

            // assign some default values because required by sendmail and better not make them defaults over there
            if (UtilValidate.isEmpty(autoMailTemplate.getString("subject"))) {
            	autoMailTemplate.put("subject", " ");
            }
            if (UtilValidate.isEmpty(autoMailTemplate.getString("content"))) {
                autoMailTemplate.put("content", " ");
            }

            // prepare the email
            //Map<String, Object> sendMailParams = FastMap.newInstance();
            Map<String, Object> sendMailParams = new HashMap<String, Object>();
            
            String fromEmailId = autoMailTemplate.getString("fromEmailId");
            if(fromEmailId != null)
            	sendMailParams.put("sendFrom", fromEmailId);
            else
            	sendMailParams.put("sendFrom", "newsletters@nichesuite.com");
            sendMailParams.put("subject", autoMailTemplate.getString("subject"));
            sendMailParams.put("contentType", autoMailTemplate.getString("contentMimeTypeId"));
            sendMailParams.put("userLogin", userLogin);

            Debug.logInfo("Sending Auto Mail: " + templateId, module);

            // check for attachments
            boolean isMultiPart = false;
 
            sendMailParams.put("body", autoMailTemplate.getString("templateData"));

            // Call the sendEmailToContactList service if there's a contactListId present
          //  Map<String, Object> sendEmailToEmailListContext = FastMap.newInstance();
            Map<String, Object> sendEmailToEmailListContext = new HashMap<String, Object>();
            
            sendEmailToEmailListContext.put("partyDetails", partyDetails);
            sendEmailToEmailListContext.put("templateId", templateId);
            sendEmailToEmailListContext.put("userLogin", userLogin);
            try {
                dispatcher.runAsync("sendEmailToEmailList", sendEmailToEmailListContext);
            } catch (GenericServiceException e) {
                String errMsg = UtilProperties.getMessage(resource, "commeventservices.errorCallingSendEmailToContactListService", locale);
                Debug.logError(e, errMsg, module);
                errorMessages.add(errMsg);
                errorMessages.addAll(e.getMessageList());
            }
            
        } catch (GeneralException eez) {
            return ServiceUtil.returnError(eez.getMessage());
        }

        // If there were errors, then the result of this service should be error with the full list of messages
        if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        }
        return result;
    }
    
    public static Map<String, Object> birthdayPartyDetails(Delegator delegator, String daysBefore) {
    	
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        //Delegator delegator = dctx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
	
		String helperName = delegator.getGroupHelperName("org.ofbiz");
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            connection = ConnectionFactory.getConnection(helperName); 
            statement = connection.createStatement();
            
            int sendBefore = 1;
            	
            	try {
					sendBefore = Integer.parseInt(daysBefore);
				} catch (NumberFormatException e) {
					sendBefore = 1;
				}
            
    		Date date = new Date();
    		int day = date.getDate();
    		int month = new Integer(date.getMonth()+1);
    		int addMonth  = 0;
    		//int addDay = 1;
    		int senddate = day+sendBefore;
    		if((senddate>28) && (month==2)){
    			addMonth = 1;
    		}else if(senddate>30){
    			addMonth = 1;
    		}
    			
    		String query ="SELECT * FROM person WHERE MONTH(BIRTH_DATE) = (MONTH(CURRENT_DATE)+"+addMonth+") AND DAY(BIRTH_DATE) = (DAY(CURRENT_DATE)+"+sendBefore+");";
    		statement.execute(query);
    		rs = statement.getResultSet();
    		List<Map<String,String>> partyDetails = new ArrayList<Map<String,String>>();
    		
    		
    		while(rs != null && rs.next()){
    			
    			Map<String,String> partyDetail = new HashMap<String,String>();
    			String fullName = "";
    			String birthDate = "";
    			String partyId = "";
    			
    			fullName = rs.getString("FIRST_NAME")+" "+rs.getString("LAST_NAME");
    			birthDate = rs.getString("BIRTH_DATE");
    			partyId = rs.getString("PARTY_ID");
    			partyDetail.put("fullName",fullName);
    			partyDetail.put("birthDate", birthDate);
    			partyDetail.put("partyId", partyId);
    		
    			GenericValue partyContactMech = org.ofbiz.party.party.PartyWorker.findPartyLatestContactMech(partyId, "EMAIL_ADDRESS", delegator);
    			if(partyContactMech != null){
    			String emailId = partyContactMech.getString("infoString");
    			partyDetail.put("emailId",emailId);
    			if(emailId != null)
    			partyDetails.add(partyDetail);
    			}
    			
    		 }
    		
    		result.put("partyDetails", partyDetails);            
            
        } catch (SQLException e) {
            String errMsg = "Unable to establish a connection with the database ... Error was: " + e.toString();
            Debug.logError(e, errMsg, module);
        } catch (GenericEntityException e) {
            String errMsg = "Unable to establish a connection with the database ... Error was: " + e.toString();
            Debug.logError(e, errMsg, module);
        }finally{
        	try {
				rs.close();
				statement.close();
	    		connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
        
		return result;
    }

    public static Map<String, Object> sendAutoGiftEmails(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        GenericValue autoMailTemplate = null;
       // List<Object> errorMessages = FastList.newInstance();
        List<Object> errorMessages = new ArrayList<Object>();
        String errorCallingSendMailService = UtilProperties.getMessage(resource, "commeventservices.errorCallingSendMailService", locale);
        String errorInSendEmailToContactListService = UtilProperties.getMessage(resource, "commeventservices.errorInSendEmailToContactListService", locale);
        String skippingInvalidEmailAddress = UtilProperties.getMessage(resource, "commeventservices.skippingInvalidEmailAddress", locale);
        
        // Any exceptions thrown in this block will cause the service to return error
        EntityListIterator eli = null;
        try {
        	
        	List<GenericValue>  allGiftTemplates = delegator.findByAnd("AutoMailTemplate", UtilMisc.toMap("templateType", "GIFT_TEMPLATE"), null);
                
            if(allGiftTemplates != null)
            	autoMailTemplate = EntityUtil.getFirst(allGiftTemplates);
            if(autoMailTemplate == null)
            	return ServiceUtil.returnError("No gift templates available");
                
    		Map result = collectGiftOrdersPartyDetails(delegator, autoMailTemplate.getString("daysBefore"));
    		List<Map> partyDetails = null;
    		
            if(result != null)
            partyDetails = (List) result.get("partyDetails");

         //   Map<String, Object> sendMailParams = FastMap.newInstance();
            Map<String, Object> sendMailParams = new HashMap<String, Object>();
            
            sendMailParams.put("sendFrom", "newsletters@nichesuite.com");
            sendMailParams.put("subject", autoMailTemplate.getString("subject"));
            sendMailParams.put("contentType", autoMailTemplate.getString("contentMimeTypeId"));
            sendMailParams.put("userLogin", userLogin);

            // loop through the list iterator
            for (Map details: partyDetails) {
                Debug.logInfo("email Id: " + details.get("emailId"), module);
                try {

                    String emailAddress = (String) details.get("emailId");
                    if (UtilValidate.isEmpty(emailAddress)) continue;
                    emailAddress = emailAddress.trim();

                    if (! UtilValidate.isEmail(emailAddress)) {
                        // If validation fails, just log and skip the email address
                        Debug.logError(skippingInvalidEmailAddress + ": " + emailAddress, module);
                        errorMessages.add(skippingInvalidEmailAddress + ": " + emailAddress);
                        continue;
                    }
                    
                    sendMailParams.put("sendTo", emailAddress);
                    sendMailParams.put("partyId", (String) details.get("partyId"));
                    
                    Debug.logInfo("Sending auto email to : " + emailAddress, module);
                    // Make the attempt to send the email to the address
                    Map<String, Object> tmpResult = null;
                    String body = autoMailTemplate.getString("templateData");
                    if(body == null) body = "Greetings from NicheSuite";
                    sendMailParams.put("body", body);
                    tmpResult = dispatcher.runSync("sendMail", sendMailParams, 360, true);

                    if (tmpResult == null || ServiceUtil.isError(tmpResult)) {
                        if (ServiceUtil.getErrorMessage(tmpResult).startsWith("[ADDRERR]")) {
                            continue;
                        } else {
                            // If the send attempt fails, just log and skip the email address
                            Debug.logError(errorCallingSendMailService + ": " + ServiceUtil.getErrorMessage(tmpResult), module);
                            errorMessages.add(errorCallingSendMailService + ": " + ServiceUtil.getErrorMessage(tmpResult));
                            continue;
                        }
                    } else {
                        // attach the parent communication event to the new event created when sending the mail
                        String thisCommEventId = (String) tmpResult.get("communicationEventId");
                        GenericValue thisCommEvent = delegator.findOne("CommunicationEvent", false, "communicationEventId", thisCommEventId);
                        if (thisCommEvent != null) {
                            thisCommEvent.set("partyIdFrom", "Company");
                            thisCommEvent.set("parentCommEventId", thisCommEventId);
                            thisCommEvent.set("autoMailTypeId", "GIFT_EMAIL");
                            thisCommEvent.store();
                        }
                        String messageId = (String) tmpResult.get("messageId");
                    }

                // Don't return a service error just because of failure for one address - just log the error and continue
                } catch (GenericEntityException nonFatalGEE) {
                    Debug.logError(nonFatalGEE, errorInSendEmailToContactListService, module);
                    errorMessages.add(errorInSendEmailToContactListService + ": " + nonFatalGEE.getMessage());
                } catch (GenericServiceException nonFatalGSE) {
                    Debug.logError(nonFatalGSE, errorInSendEmailToContactListService, module);
                    errorMessages.add(errorInSendEmailToContactListService + ": " + nonFatalGSE.getMessage());
                }
            }

        } catch (GenericEntityException fatalGEE) {
            return ServiceUtil.returnError(fatalGEE.getMessage());
        } finally {
            if (eli != null) {
                try {
                    eli.close();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return errorMessages.size() == 0 ? ServiceUtil.returnSuccess() : ServiceUtil.returnError(errorMessages);
    }
	    
	public static Map<String, Object> collectGiftOrdersPartyDetails(Delegator delegator, String daysBefore) {
	    	
			Map<String, Object> result = new HashMap<String, Object>();
		
			String helperName = delegator.getGroupHelperName("org.ofbiz");
	        Connection connection = null;
	        Statement statement = null;
	        ResultSet rs = null;
	        try {
	            connection = ConnectionFactory.getConnection(helperName); 
	            statement = connection.createStatement();
	            
	            int sendBefore = 1;
            	
            	try {
					sendBefore = Integer.parseInt(daysBefore);
				} catch (NumberFormatException e) {
					sendBefore = 1;
				}
	            
	    		Date date = new Date();
	    		int day = date.getDate();
	    		int month = new Integer(date.getMonth()+1);
	    		int addMonth  = 0;
	    		int addDay = 0;
	    		int senddate = day+sendBefore;
	    		if((senddate>28) && (month==2)){
	    			addMonth = 1;
	    		}else if(senddate>30){
	    			addMonth = 1;
	    		}
	    			
	    		String query ="SELECT * FROM order_item_ship_group WHERE YEAR(CREATED_STAMP) < YEAR(CURRENT_DATE) AND MONTH(CREATED_STAMP) = (MONTH(CURRENT_DATE)+"+addMonth+") AND DAY(CREATED_STAMP) = (DAY(CURRENT_DATE)+"+sendBefore+") AND IS_GIFT='Y';";
	    		statement.execute(query);
	    		rs = statement.getResultSet();
	    		List<Map<String,String>> partyDetails = new ArrayList<Map<String,String>>();
	    		
	    		while(rs != null && rs.next()){
	    			
	    			Map<String,String> partyDetail = new HashMap<String,String>();
	    			String orderId = "";
	    			String emailId = null;
	    			
	    			orderId = rs.getString("ORDER_ID");
	    			
	    			if(orderId != null && orderId.length()>0){
	    				GenericValue partyContactMech = ContactMechWorker.getOrderContactMechValue(delegator, orderId, "ORDER_EMAIL");
	    				if(partyContactMech != null){
	    					emailId = partyContactMech.getString("infoString");
	    					if(emailId == null || emailId.length() == 0)
	    		    			continue;
	    				}
	    				
	    				GenericValue person = getOrderPersonGenericValue(delegator, orderId);
	    				if(person != null){
	    				
	    				partyDetail.put("fullName", person.getString("firstName")+" "+person.getString("lastName"));
	    				partyDetail.put("partyId", person.getString("partyId"));
	    				partyDetail.put("birthDate", person.getString("birthDate"));
	    				}else continue;
	    				
	    			}
	    			
	    			partyDetail.put("orderId", orderId);
	    			partyDetail.put("emailId",emailId);
	    			
	    			if(emailId != null)
	    			partyDetails.add(partyDetail);
	    			
	    		 }
	    		result.put("partyDetails", partyDetails); 
	    		
	        } catch (SQLException e) {
	            String errMsg = "Unable to establish a connection with the database ... Error was: " + e.toString();
	            Debug.logError(e, errMsg, module);
	        } catch (GenericEntityException e) {
	            String errMsg = "Unable to establish a connection with the database ... Error was: " + e.toString();
	            Debug.logError(e, errMsg, module);
	        }finally{
		        	try {
						rs.close();
						statement.close();
			    		connection.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	        }
	        
			return result;
	    }

	public static GenericValue getOrderPersonGenericValue(Delegator delegator, String orderId){
		
		GenericValue person = null;
		
		try {
			GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
			if (orderHeader != null){
				String createdBy = orderHeader.getString("createdBy");
				GenericValue userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", createdBy));
				if(userLogin != null){
					String partyId = userLogin.getString("partyId");
					person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
				}
				
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		
		return person;
	}
    
}