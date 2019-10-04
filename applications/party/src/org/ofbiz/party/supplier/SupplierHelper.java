package org.ofbiz.party.supplier;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
public class SupplierHelper {
	
	public static final String module = SupplierHelper.class.getName();
	
	//TODO find Supplier 
	public static final String findSupplier(HttpServletRequest request , HttpServletResponse response)
	{
		    Debug.log("\n\n ######################################  insite findSupplier");
		    String returnMsg="success";
		    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		  //  Map context = request.getParameterMap();
		    String supplierId =request.getParameter("supplierId");
		    String supplierName =request.getParameter("supplierName");
		    String tnCountryCode =request.getParameter("tnCountryCode");
		    String tnAreaCode =request.getParameter("tnAreaCode");
		    String tnContactNumber =request.getParameter("tnContactNumber");
		    
		    String emailAddress =request.getParameter("emailAddress");
		    String address1 =request.getParameter("address1");
		    String city =request.getParameter("city");
		    String country =request.getParameter("country");
		    String state =request.getParameter("state");
		    String postalcode =request.getParameter("postalcode");
		    List conditionList = FastList.newInstance();
		    conditionList.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"SUPPLIER"));
		    if(supplierId!=null)
		    {
		    	Debug.log("\n\n supplierId ="+supplierId);
		    	conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,supplierId));	
		    	
		    }
		    
		   
		   /*
		    if(supplierName!=null &&  supplierName!="")
		    {
		    	Debug.log("\n\n supplierName ="+supplierName);
		    	conditionList.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,supplierName+"%"));	
		    } 
		    if(emailAddress!=null)
		    {
		    	Debug.log("\n\n emailAddress ="+emailAddress);
		    	conditionList.add(EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"EMAIL_ADDRESS"));	
		    	conditionList.add(EntityCondition.makeCondition("infoString",EntityOperator.EQUALS,emailAddress));	
		    }
		   
		    if(address1!=null)
		    {
		    	conditionList.add(EntityCondition.makeCondition("paAddress1",EntityOperator.LIKE,"%"+address1+"%"));	
		    }
		    if(city!=null)
		    {
		    	conditionList.add(EntityCondition.makeCondition("paCity",EntityOperator.EQUALS,city));	
		    }
		    if(postalcode!=null)
		    {
		    	conditionList.add(EntityCondition.makeCondition("paPostalCode",EntityOperator.EQUALS,postalcode));	
		    }*/
		    EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityJoinOperator.AND);
		    try
		    {
		    	Set <String> fieldToSelect = new HashSet<String>();
		    	fieldToSelect.add("partyId");
		    	fieldToSelect.add("firstName");
		    	fieldToSelect.add("middleName");
		    	fieldToSelect.add("lastName");
		    	fieldToSelect.add("paCity");
		    	fieldToSelect.add("infoString");
		    	fieldToSelect.add("contactMechTypeId");
		    	fieldToSelect.add("paAddress1");
		    	fieldToSelect.add("paPostalCode");
		    	
		    	
		    	fieldToSelect.add("tnCountryCode");
		    	fieldToSelect.add("tnAreaCode");
		    	fieldToSelect.add("tnContactNumber");
		    	List supplierList = delegator.findList("PartyRoleAndContactMechDetail", condition, fieldToSelect, null, null, false);
		    	if(supplierList!=null)
		    	{
		    		Debug.log("\n\n supplierList size="+supplierList.size());
		    		//Debug.log("\n\n supplierList ="+supplierList);
		    	}
		    	request.setAttribute("supplierList", supplierList);
		    }catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		    
		    return returnMsg;
			    
	}
	//TODO create Supplier
	public static String  CreateSupplier(HttpServletRequest request , HttpServletResponse response)
	{
	    Debug.log("\n\n ######################################  insite CreateSupplier");
	    String returnMsg="success";
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	    GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
	    if(userLogin==null)
	    {
	    	try
	    	{
	    		userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    }
	    Locale locale = (Locale) request.getAttribute("locale");
	    String partyId = "";
	    Map serviceParams = new HashMap();
	    Map serviceResults =null;
	    
		try {
			serviceParams = dispatcher.runSync("createPerson", UtilMisc.toMap("firstName", request.getParameter("USER_FIRST_NAME"), "lastName", request.getParameter("USER_LAST_NAME"), "userLogin",userLogin ));
			if(ServiceUtil.isSuccess(serviceParams))
					{
				        partyId =(String) serviceParams.get("partyId"); 
					}
			Debug.log("\n\n ####################### ***************** partyId="+partyId);
			Debug.log("\n\n ####################### ***************** userLogin="+userLogin);
		//	serviceParams = dispatcher.runSync("createPartyGroup", UtilMisc.toMap("partyId",partyId,"groupName","Big Supplier","userLogin",userLogin));
		 	serviceParams= dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SUPPLIER", "userLogin", userLogin));
		 	
		 	 // Create primary email
		 	String primaryEmail = request.getParameter("USER_EMAIL");
            if (UtilValidate.isNotEmpty(primaryEmail)) {
                serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
                            "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", primaryEmail));
                if (ServiceUtil.isError(serviceResults)) return "error";
            }
            // Create primary web url
            String primaryWebUrl =  request.getParameter("primaryWebUrl");
            if (UtilValidate.isNotEmpty(primaryWebUrl)) {
                serviceResults = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", partyId, "userLogin", userLogin,
                            "contactMechTypeId", "WEB_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_WEB_URL", "infoString", primaryWebUrl));
                if (ServiceUtil.isError(serviceResults)) return  "error";

            }
                // Create primary telecom number
                String primaryPhoneCountryCode =  request.getParameter("USER_HOME_COUNTRY");
                String primaryPhoneAreaCode =  request.getParameter("USER_HOME_AREA");
                String primaryPhoneNumber =  request.getParameter("USER_HOME_CONTACT");
                String primaryPhoneExtension = request.getParameter("USER_HOME_EXT");
                Map input = new HashMap(); //FastMap.newInstance();
                if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
                    input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "PRIMARY_PHONE");
                    input.put("countryCode", primaryPhoneCountryCode);
                    input.put("areaCode", primaryPhoneAreaCode);
                    input.put("contactNumber", primaryPhoneNumber);
                    input.put("extension", primaryPhoneExtension);
                    serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
                    if (ServiceUtil.isError(serviceResults)) return "error";
                }
                
                // Create Main Fax Number
                String primaryFaxCountryCode =request.getParameter("USER_FAX_COUNTRY");
                String primaryFaxAreaCode = request.getParameter("USER_FAX_AREA");
                String primaryFaxNumber = request.getParameter("USER_FAX_CONTACT");
                String primaryFaxExtension = request.getParameter("USER_FAX_EXT");
                if (UtilValidate.isNotEmpty(primaryFaxNumber)) {
                    input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "FAX_NUMBER");
                    input.put("countryCode", primaryFaxCountryCode);
                    input.put("areaCode", primaryFaxAreaCode);
                    input.put("contactNumber", primaryFaxNumber);
                    input.put("extension", primaryFaxExtension);
                    serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
                    if (ServiceUtil.isError(serviceResults)) return "error";
                }  
                // Create general correspondence postal address
              //  String generalToName = request.getParameter("generalToName");
              //  String generalAttnName = request.getParameter("generalAttnName");
                String generalAddress1 = request.getParameter("USER_ADDRESS1");
                String generalAddress2 = request.getParameter("USER_ADDRESS2");
                String generalCity = request.getParameter("USER_CITY");
                String generalStateProvinceGeoId = request.getParameter("USER_STATE");
                String generalPostalCode = request.getParameter("USER_POSTAL_CODE");
                String generalCountryGeoId = request.getParameter("USER_COUNTRY");
                if (UtilValidate.isNotEmpty(generalAddress1)) {
                    input = UtilMisc.toMap("partyId", partyId, "userLogin", userLogin, "contactMechPurposeTypeId", "GENERAL_LOCATION");
                    input.put("toName", request.getParameter("USER_FIRST_NAME")+" "+request.getParameter("USER_LAST_NAME"));
                    input.put("attnName",  request.getParameter("USER_FIRST_NAME")+" "+request.getParameter("USER_LAST_NAME"));
                    input.put("address1", generalAddress1);
                    input.put("address2", generalAddress2);
                    input.put("city", generalCity);
                    input.put("stateProvinceGeoId", generalStateProvinceGeoId);
                    input.put("postalCode", generalPostalCode);
                    input.put("countryGeoId", generalCountryGeoId);
                    serviceResults = dispatcher.runSync("createPartyPostalAddress", input);
                    if (ServiceUtil.isError(serviceResults)) {
                        return "errror";
                    }
                    String contactMechId = (String) serviceResults.get("contactMechId");
                    String supplierPartyId = partyId;
                    // Make this address the SHIPPING_LOCATION, SHIP_ORIG_LOCATION, PAYMENT_LOCATION and BILLING_LOCATION
                    input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION");
                    serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                    if (ServiceUtil.isError(serviceResults)) return "error";
                    input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
                    serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                    if (ServiceUtil.isError(serviceResults)) return  "error";
                    input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PAYMENT_LOCATION");
                    serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                    if (ServiceUtil.isError(serviceResults)) return  "error";
                    input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "BILLING_LOCATION");
                    serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                    if (ServiceUtil.isError(serviceResults)) return  "error";
                }

		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
	  return returnMsg;
		
	}
//TODO view supplier 
	public static String  viewSupplier(HttpServletRequest request , HttpServletResponse response)
	{
	    Debug.log("\n\n ######################################  insite CreateSupplier");
	    String returnMsg="success";
	    GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	    LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	    GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
	    String partyId = request.getParameter("partyId");
	    if(userLogin==null)
	    {
	    	try
	    	{
	    		userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	     }
	      Locale locale = (Locale) request.getAttribute("locale");
	      
	    //view orders by supplier
	      
	   try
	   {
		   List orderList = delegator.findByAnd("OrderRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "BILL_FROM_VENDOR"));
		   List orderIdList = EntityUtil.getFieldListFromEntityList(orderList, "orderId", true);
		   List conditionList = FastList.newInstance();
		   conditionList.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIdList));
		   EntityCondition condition = EntityCondition.makeCondition(conditionList);
		    Set <String>set =new HashSet<String>();
		    set.add("orderId");
		    set.add("orderName");
		    set.add("orderDate");
		    set.add("grandTotal");
		    set.add("currencyUom");
		    List supplierOrder = delegator.findList("OrderHeader", condition , set , null, null, false);
		    List agreementList = delegator.findByAnd("Agreement", UtilMisc.toMap("partyIdTo", partyId));
		    request.setAttribute("supplierOrder", supplierOrder);
		    request.setAttribute("agreementList", agreementList);
		    Debug.log("\n\n ################################### supplier="+delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId)));
		    request.setAttribute("supplier", delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId)));
		
	   }catch(Exception e)
	   {
		   e.printStackTrace();
	   }
	   
	  
	   // view agreeement 
	   
	   
	   
	  return   returnMsg;
	  }

}
