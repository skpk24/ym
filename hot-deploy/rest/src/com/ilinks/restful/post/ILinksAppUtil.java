package com.ilinks.restful.post;


import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastMap;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.condition.*;

import javolution.util.FastList;

import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.*;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppinglist.ShoppingListEvents;
import org.ofbiz.party.contact.ContactHelper;

//import com.google.android.gcm.server.Message;
//import com.google.android.gcm.server.Result;
//import com.google.android.gcm.server.Sender;

public class ILinksAppUtil {
	
	public static final String module = ILinksAppUtil.class.getName();
	public static final GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
	public static final LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default", delegator);
	public static final String resource = "SecurityextUiLabels";
	public static final String resourceError = "PartyErrorUiLabels";
	private static EntityCondition dateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	public static final String[] roles = {"BILL_TO_CUSTOMER", "END_USER_CUSTOMER", "PLACING_CUSTOMER", "SHIP_TO_CUSTOMER", "CUSTOMER"};
	public static final String UDAILY_PERSISTANT_PRD_LIST_NAME = "udaily-auto-save-products";
	public static final EntityFindOptions readonly = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	
	
	/**
     * Creates a Person.
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPerson(Map<String, Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Timestamp now = UtilDateTime.nowTimestamp();
        List<GenericValue> toBeStored = FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");
        String description = (String) context.get("description");

        // if specified partyId starts with a number, return an error
        if (UtilValidate.isNotEmpty(partyId) && partyId.matches("\\d+")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "party.id_is_digit", locale));
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "party.id_generation_failure", locale));
            }
        }

        // check to see if party object exists, if so make sure it is PERSON type party
        GenericValue party = null;

        try {
            party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party != null) {
            if (!"PERSON".equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.create.party_exists_not_person_type", locale)); 
            }
        } else {
            // create a party if one doesn't already exist with an initial status from the input
            String statusId = (String) context.get("statusId");
            if (statusId == null) {
                statusId = "PARTY_ENABLED";
            }
            Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "description", description, "createdDate", now, "lastModifiedDate", now, "statusId", statusId);
            String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
            if (!UtilValidate.isEmpty(preferredCurrencyUomId)) {
                newPartyMap.put("preferredCurrencyUomId", preferredCurrencyUomId);
            }
            String externalId = (String) context.get("externalId");
            if (!UtilValidate.isEmpty(externalId)) {
                newPartyMap.put("externalId", externalId);
            }
            if (userLogin != null) {
                newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            }
            party = delegator.makeValue("Party", newPartyMap);
            toBeStored.add(party);

            // create the status history
            GenericValue statusRec = delegator.makeValue("PartyStatus",
                    UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
            toBeStored.add(statusRec);
        }

        GenericValue person = null;

        try {
            person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (person != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.person_exists", locale)); 
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        person.setNonPKFields(context);
        toBeStored.add(person);
        
        
    	String paContactMechId = delegator.getNextSeqId("ContactMech");
		Map fieldValues = new HashMap();
		fieldValues.put("contactMechId", paContactMechId+"");
		fieldValues.put("contactMechTypeId", "EMAIL_ADDRESS");
		fieldValues.put("infoString",context.get("emailId") );
		toBeStored.add(delegator.makeValue("ContactMech", fieldValues));
		
		fieldValues.clear();
		
		fieldValues.put("contactMechId", paContactMechId+"");
		fieldValues.put("partyId", partyId);
		fieldValues.put("fromDate", UtilDateTime.nowTimestamp());
		toBeStored.add(delegator.makeValue("PartyContactMech", fieldValues));
		
		fieldValues.clear();
		fieldValues.put("contactMechId", paContactMechId+"");
		fieldValues.put("partyId", partyId);
		fieldValues.put("fromDate", UtilDateTime.nowTimestamp());
		fieldValues.put("contactMechPurposeTypeId", "PRIMARY_EMAIL");
		toBeStored.add(delegator.makeValue("PartyContactMechPurpose", fieldValues));

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            e.printStackTrace();
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.db_error", new Object[] { e.getMessage() }, locale)); 
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
	
    /** Creates a UserLogin
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> createUserLogin(Map<String, Object> context, Delegator delegator) {
        Map<String, Object> result = FastMap.newInstance();
        List<String> errorMessageList = FastList.newInstance();
        Locale locale = (Locale) context.get("locale");

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = (String) context.get("emailId");
        String partyId = (String) context.get("partyId");
        String currentPassword = (String) context.get("password");
        String enabled = (String) context.get("enabled");
        String passwordHint = (String) context.get("passwordHint");
        String requirePasswordChange = (String) context.get("requirePasswordChange");
        String externalAuthId = (String) context.get("externalAuthId");
        String mobDeviceId = (String) context.get("mobDeviceId");
        String errMsg = null;

        //checkNewPassword(null, null, currentPassword, currentPasswordVerify, passwordHint, errorMessageList, true, locale);

        GenericValue userLoginToCreate = delegator.makeValue("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        userLoginToCreate.set("externalAuthId", externalAuthId);
        userLoginToCreate.set("passwordHint", passwordHint);
        userLoginToCreate.set("enabled", enabled);
        userLoginToCreate.set("requirePasswordChange", requirePasswordChange);
        //userLoginToCreate.set("currentPassword", useEncryption ? HashCrypt.cryptUTF8(getHashType(), null, currentPassword) : currentPassword);
        String ency = useEncryption ? HashCrypt.getDigestHash(currentPassword, getHashType()) : currentPassword;
        //useEncryption ? HashCrypt.getDigestHash(password, getHashType()) : password;
        //System.out.println("\n***********************************\n ency == "+ency+"\n***********************************\n");
//        System.out.println("\n1111111111111111111111111111\n useEncryption == "+useEncryption+"\n1111111111111111111111111111\n");
//        System.out.println("\n1111111111111111111111111111\n HashCrypt.getDigestHash(password, getHashType()) == "+HashCrypt.getDigestHash(currentPassword, getHashType())+"\n1111111111111111111111111111\n");
//        System.out.println("\n1111111111111111111111111111\n currentPassword == "+currentPassword+"\n1111111111111111111111111111\n");
//        System.out.println("\n1111111111111111111111111111\n ency == "+ency+"\n1111111111111111111111111111\n");
        userLoginToCreate.set("currentPassword", ency);
        userLoginToCreate.set("firstName", context.get("firstName"));
        userLoginToCreate.set("lastName", context.get("lastName"));
        userLoginToCreate.set("emailId", context.get("emailId"));
        userLoginToCreate.set("mobileNumber", context.get("mobileNumber"));
        userLoginToCreate.set("uniqCode", context.get("uniqCode"));
        userLoginToCreate.set("mobDeviceId", mobDeviceId);
        userLoginToCreate.set("imei", context.get("imei"));
        
        try {
            userLoginToCreate.set("partyId", partyId);
        } catch (Exception e) {
            // Will get thrown in framework-only installation 
            Debug.logInfo(e, "Exception thrown while setting UserLogin partyId field: ", module);
        }

        try {
            EntityCondition condition = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.EQUALS, EntityFunction.UPPER(userLoginId));
            if (UtilValidate.isNotEmpty(delegator.findList("UserLogin", condition, null, null, null, false))) {
                Map<String, String> messageMap = UtilMisc.toMap("userLoginId", userLoginId);
                errorMessageList.add(UtilProperties.getPropertyValue(resource,"loginservices.could_not_create_login_user_with_ID_exists"));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"loginservices.could_not_create_login_user_read_failure", messageMap, locale);
            errorMessageList.add(errMsg);
        }

        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        try {
            userLoginToCreate.create();
            //createUserLoginPasswordHistory(delegator,userLoginId, currentPassword);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "", module);
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"loginservices.could_not_create_login_user_write_failure", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    
    public static List<Map<String, Object>> getProducts(Object productId, Map paramMap){
    	List<GenericValue> product = null; 
    	List<Map<String, Object>> newPrdList = new ArrayList();
    	Map result = FastMap.newInstance();
    	EntityFindOptions readonly = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
    	//readonly.setFetchSize(20);
    	int totalListSize = 0;
    	int lowIndex = 0;
		int highIndex = 0;
		int indexNumbers = 0;
		int viewIndex = 0;
		try {
			viewIndex = Integer.parseInt((String) paramMap.get("VIEW_INDEX"));
		} catch (NumberFormatException e) {
			try {
				viewIndex = Integer.parseInt(UtilProperties.getPropertyValue("pagination.properties", "paginate.default.page"));
			} catch (NumberFormatException ex) {
				viewIndex = Integer.parseInt(UtilProperties.getPropertyValue("restful.properties", "paginate.default.page"));
			}
		}
		result.put("viewIndex", Integer.valueOf(viewIndex));
		int viewSize = Integer.parseInt(UtilProperties.getPropertyValue("restful.properties", "paginate.default.size"));
		try {
			viewSize = Integer.parseInt((String) paramMap.get("VIEW_SIZE"));
		} catch (NumberFormatException e) {
			try {
				viewSize = Integer.parseInt(UtilProperties.getPropertyValue("restful.properties", "paginate.default.size"));
			} catch (NumberFormatException ex) {
				viewSize = Integer.parseInt(UtilProperties.getPropertyValue("restful.properties", "paginate.default.size"));
			}
		}
		result.put("viewSize", Integer.valueOf(viewSize));
		EntityListIterator products = null;
    	try{
    		EntityCondition entityCondition = null;
	    	Set fields = UtilMisc.toSet("productId", "internalName", "brandName", "description", "longDescription", "productName");
	        fields.add("basePrice");
	        fields.add("inventoryAtp");
	        fields.add("listPrice");
	        fields.add("discountPer");
	        fields.add("discounted");
	        fields.add("topRating");
	        fields.add("isVirtual");
	        fields.add("primaryProductCategoryId");
	        fields.add("mSmallImageUrl");
	        fields.add("mLargeImageUrl");
	        if(productId instanceof String){
	        	if(UtilValidate.isEmpty(paramMap.get("getParent")) || ((String)paramMap.get("getParent")).equalsIgnoreCase("Y")){
		        	GenericValue prd = delegator.findOne("Product", true, UtilMisc.toMap("productId", productId));
		        	if(prd.getString("isVariant").equalsIgnoreCase("Y")){
		        		List<GenericValue> virtualProductAssocs = delegator.findByAndCache("ProductAssoc", UtilMisc.toMap("productIdTo", productId, "productAssocTypeId", "PRODUCT_VARIANT"), UtilMisc.toList("-fromDate"));
		                virtualProductAssocs = EntityUtil.filterByDate(virtualProductAssocs);
		                if(UtilValidate.isNotEmpty(virtualProductAssocs)){
		                	productId = EntityUtil.getFirst(virtualProductAssocs).getString("productId");
		                }
		        	}
	        	}
	        	entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("productId", productId));
	        }
	        if(productId instanceof List){
	        	entityCondition = EntityCondition.makeCondition("productId", EntityOperator.IN, productId);
	        }
	        
	        if(UtilValidate.isNotEmpty(paramMap.get("filter")) && ((String)paramMap.get("filter")).equalsIgnoreCase("Y")){
	        	entityCondition = EntityCondition.makeCondition(entityCondition, EntityOperator.AND,  EntityCondition.makeCondition(filterCond(paramMap)));
	        }
	        
	       // Debug.log("\n\n entityCondition ================== "+entityCondition+"\n\n");
	        
	        TransactionUtil.begin();
	        products = delegator.find("Product", entityCondition,null, fields, null, readonly);
	        TransactionUtil.commit();
	        
	        if(UtilValidate.isNotEmpty(paramMap.get("catalogId")) && ((String)paramMap.get("catalogId")).equalsIgnoreCase("YOU_DAILY")){
	        	products.last();
	        	viewSize = products.currentIndex();
	        	result.put("viewSize", Integer.valueOf(viewSize));
	        	products.first();
	        }
		        Map<String, Object> paginateResult = UIPagination.paginate(products, viewIndex, viewSize, resource, module);
				Integer low = (Integer) paginateResult.get("lowIndex");
				lowIndex = low.intValue();
				Integer high = (Integer) paginateResult.get("highIndex");
				highIndex = high.intValue();
				product = (List) paginateResult.get("recordList");
				Integer size = (Integer) paginateResult.get("listSize");
				totalListSize = size.intValue();
				Integer index = (Integer) paginateResult.get("indexNumbers");
				indexNumbers = index.intValue();
	        
	        
	        //List<GenericValue> virtuals = EntityUtil.filterByAnd(product, UtilMisc.toMap("isVirtual", "Y"));
	        if(UtilValidate.isNotEmpty(product)){
	        	Map<String, Object> prd = null;
	        	for(int i = 0; i < product.size(); i++){
	        		//gv = product.get(i);
	        		if(!((String)product.get(i).get("isVirtual")).equalsIgnoreCase("Y")){
	        			newPrdList.add(product.get(i).getAllFields());
	        			continue;
	        		}
	        		prd = (Map<String, Object>)product.get(i).getAllFields();
	        		//Debug.log("\n\n prd == "+prd+"\n\n");
	        		prd.put("variants",getVarients((GenericValue)product.get(i)));
	        		newPrdList.add(prd);
	        		//product.get(i).set("longDescription", getVarients(product.get(i)));
	        		//product.add(i, gv);
	        	}
	        }
	        
	        //result.put("products", newPrdList);
	        if(productId instanceof List){
				result.put("totalListSize", totalListSize);
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
				result.put("indexNumbers", Integer.valueOf(indexNumbers));
				newPrdList.add(result);
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return newPrdList;
    }
    
    private static List getVarients(GenericValue gv) throws Exception{
    	List<Map<String, Object>> variants = new ArrayList();
    	ObjectMapper mapper = new ObjectMapper();
    	Set fields = UtilMisc.toSet("productId", "internalName", "description", "longDescription", "productName");
        fields.add("basePrice");
        fields.add("inventoryAtp");
        fields.add("listPrice");
        fields.add("discountPer");
        fields.add("discounted");
        fields.add("topRating");
    	Map response = dispatcher.runSync("getVariantCombinations", UtilMisc.toMap("productId", gv.getString("productId")));
		List<Map> featureCombi = (ArrayList) response.get("featureCombinations");
		Map<String, Object> gv1 = null;
		for(Map val : featureCombi){
			if(UtilValidate.isNotEmpty(val.get("existingVariantProductIds")) && UtilValidate.isNotEmpty(((List)val.get("existingVariantProductIds")).get(0))){
				gv1 = EntityUtil.getFirst(delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("productId", ((List)val.get("existingVariantProductIds")).get(0))), fields, null, readonly, true)).getAllFields();
				gv1.put("feature", ((GenericValue)((List)val.get("curProductFeatureAndAppls")).get(0)).getString("description"));
				variants.add(gv1);
			}
		}
		//return mapper.writeValueAsString(variants);
		return variants;
    }
    
    private static List filterCond(Map paramMap){
    	String refineByPrice = ((String) paramMap.get("priceRange"));
        String refineByBrand = ((String) paramMap.get("brandNames"));
        String excludeOutOfStock = ((String) paramMap.get("Availability"));
        List mainCondList = FastList.newInstance();
    	try{
    		if("Y".equals(excludeOutOfStock)){
    			mainCondList.add(EntityCondition.makeCondition("inventoryAtp", EntityOperator.GREATER_THAN_EQUAL_TO, BigDecimal.ONE));
    		}
    	
	    	if(UtilValidate.isNotEmpty(refineByBrand)){
	 			String filtByBrand[] = refineByBrand.split(",");
	 			//refineByBrandList = Arrays.asList(filtByBrand);
	 			List newCond = FastList.newInstance();
	 			if(UtilValidate.isNotEmpty(filtByBrand))
	 				for(String filtBybr : filtByBrand)
	 					if(UtilValidate.isNotEmpty(filtBybr)){
	 						newCond.add(EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, filtBybr));
	 					}
	 			mainCondList.add(EntityCondition.makeCondition(newCond, EntityOperator.OR));
	    	}
	    	
	    	//refineByPrice
            if(UtilValidate.isNotEmpty(refineByPrice)){
      			String filtByPrice[] = refineByPrice.split(",");
      			//refineByPriceList = Arrays.asList(filtByPrice);
      			List priceCondList = FastList.newInstance();
      			List priceCond = FastList.newInstance();
      			for(String filtByPr : filtByPrice){
      				filtByPr = filtByPr.trim();
      				if(UtilValidate.isNotEmpty(filtByPr)){
	      				if("Less than Rs 20".equals(filtByPr)){
	      					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(20)));
	      				}else if("Rs 21 to 50".equals(filtByPr)) {
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(21)));
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(50)));
	      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
	      				}else if("Rs 51 to 100".equals(filtByPr)) {
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(51)));
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(100)));
	      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
	      				}else if("Rs 101 to 200".equals(filtByPr)){
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(101)));
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(200)));
	      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
	      				}else if("Rs 201 to 500".equals(filtByPr)) {
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(201)));
	      					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(500)));
	      					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
	      					priceCond.clear();
	      				}else if("More than Rs 501".equals(filtByPr)) {
	      					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(500)));
	      				}
      				}
      			}
      			mainCondList.add(EntityCondition.makeCondition(priceCondList, EntityOperator.OR));
              }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return mainCondList;
    }

    
    public static Map getRelatedCategoriesRet(Delegator delegator, String attributeName, String parentId, boolean limitView, boolean excludeEmpty, boolean recursive) {

        List<GenericValue> rollups = null;
        try {
            rollups = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(UtilMisc.toMap("parentProductCategoryId", parentId)), UtilMisc.toSet("productCategoryId", "categoryName"),  UtilMisc.toList("sequenceNum"), readonly, true);
            if (limitView) {
                rollups = EntityUtil.filterByDate(rollups, true);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        Map category = new LinkedHashMap();
        Map temp = new LinkedHashMap();
        Map tmp = null;
        if (rollups != null) {
            // Debug.log("Rollup size: " + rollups.size(), module);
            for (GenericValue parent: rollups) {
                // Debug.log("Adding child of: " + parent.getString("parentProductCategoryId"), module);
                GenericValue cv = null;

                try {
                    cv = parent.getRelatedOneCache("CurrentProductCategory");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
                if (cv != null) {
                    if (excludeEmpty) {
                        if (!isCategoryEmpty(cv)) {
                            //Debug.log("Child : " + cv.getString("productCategoryId") + " is not empty.", module);
                            //categories.add((Map)parent);
                            category.put(parent.getString("productCategoryId"), UtilMisc.toMap("name", parent.getString("categoryName")));
                            if (recursive) {
                                //categories.addAll(getRelatedCategoriesRet(delegator, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive));
                            	temp = (Map) category.get(parent.getString("productCategoryId"));
                            	tmp = getRelatedCategoriesRet(delegator, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive);
                            	if(UtilValidate.isNotEmpty(tmp)){
	                            	temp.put("child", tmp);
	                            	category.put(parent.getString("productCategoryId"), temp);
                            	}
                            }
                        }
                    } else {
                        category.put(parent.getString("productCategoryId"), UtilMisc.toMap("name", parent.getString("categoryName")));
                        if (recursive) {
                        	temp = (Map) category.get(parent.getString("productCategoryId"));
                        	tmp = getRelatedCategoriesRet(delegator, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive);
                        	if(UtilValidate.isNotEmpty(tmp)){
	                        	temp.put("child", tmp);
	                        	category.put(parent.getString("productCategoryId"), temp);
                        	}
                        }
                    }
                }
            }
        }
        return category;
    }
    
    public static boolean isCategoryEmpty(GenericValue category) {
        boolean empty = true;
        long members = categoryMemberCount(category);
        //Debug.log("Category : " + category.get("productCategoryId") + " has " + members  + " members", module);
        if (members > 0) {
            empty = false;
        }

        if (empty) {
            long rollups = categoryRollupCount(category);
            //Debug.log("Category : " + category.get("productCategoryId") + " has " + rollups  + " rollups", module);
            if (rollups > 0) {
                empty = false;
            }
        }

        return empty;
    }

    public static long categoryMemberCount(GenericValue category) {
        if (category == null) return 0;
        Delegator delegator = category.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByCondition("ProductCategoryMember", buildCountCondition("productCategoryId", category.getString("productCategoryId")), null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return count;
    }
    
    private static EntityCondition buildCountCondition(String fieldName, String fieldValue) {
        // List<EntityCondition> orCondList = FastList.newInstance();
         List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
         orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
         orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
         EntityCondition orCond = EntityCondition.makeCondition(orCondList, EntityOperator.OR);

       //  List<EntityCondition> andCondList = FastList.newInstance();
         List<EntityCondition> andCondList = new ArrayList<EntityCondition>();
         andCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()));
         andCondList.add(EntityCondition.makeCondition(fieldName, EntityOperator.EQUALS, fieldValue));
         andCondList.add(orCond);
         EntityCondition andCond = EntityCondition.makeCondition(andCondList, EntityOperator.AND);

         return andCond;
     }

    public static long categoryRollupCount(GenericValue category) {
        if (category == null) return 0;
        Delegator delegator = category.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByCondition("ProductCategoryRollup", buildCountCondition("parentProductCategoryId", category.getString("productCategoryId")), null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return count;
    }
    
    
    /** Update a UserLogin
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> updateUserLogin(Map<String, Object> context) {
    	Map<String, Object> result = FastMap.newInstance();
    	try{
    		String userLoginId = (String) context.get("emailId");
    		String mobileNumber = (String) context.get("mobileNumber");
    		String uniqCode = (String) context.get("uniqCode");
    		
    		Debug.log("\n\n sdfdf == "+ EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "mobileNumber", mobileNumber, "uniqCode", uniqCode))+"\n\n");
    		List<GenericValue> userLogin = delegator.findList("UserLogin", EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", userLoginId, "mobileNumber", mobileNumber, "uniqCode", uniqCode)), null, null, null, false);
    		
    		Debug.log("\n\n userLogin == "+userLogin+"\n\n");
    		if(UtilValidate.isNotEmpty(userLogin)){
    			GenericValue gv = userLogin.get(0);
    			gv.set("enabled", "Y");
    			gv.set("uniqCode", null);
    			gv.store();
    			result = ServiceUtil.returnSuccess();
    		}else{
    			result = ServiceUtil.returnError("User Not found or OTP is wrong");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    
    /** Add product to auto save cart shopping list
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static String addToCart(Map<String, Object> context) {
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
		String shoppingListId = (String) context.get("shoppingListId");
		String shoppingListTypeId = (String) context.get("shoppingListTypeId");
		String storeId = (String) context.get("storeId");
		//boolean allowPromo = ((Boolean) context.get("allowPromo")).booleanValue();
		boolean append = ((Boolean) context.get("append")).booleanValue();
		List<Map<String,Object>> items = (List<Map<String,Object>>)context.get("items");
		List listItems = null;
    	try{
    		//Debug.log("\n\n items === "+items+"\n\n");
    		if (UtilValidate.isEmpty(shoppingListId)) {
                // create a new shopping list
                Map<String, Object> newListResult = null;
                try {
                    newListResult = dispatcher.runSync("createShoppingList", UtilMisc.<String, Object>toMap("userLogin", userLogin, "productStoreId", storeId, "partyId", userLogin.getString("partyId"), "shoppingListTypeId", shoppingListTypeId, "currencyUom", "INR"));
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems creating new ShoppingList", module);
                }

                // check for errors
                if (ServiceUtil.isError(newListResult)) {
                    Debug.logError( ServiceUtil.getErrorMessage(newListResult), module);
                }

                // get the new list id
                if (newListResult != null) {
                    shoppingListId = (String) newListResult.get("shoppingListId");
                }

                // if no list was created throw an error
                if (shoppingListId == null || shoppingListId.equals("")) {
                	Debug.logError("Problems creating new ShoppingList, might be required parameters are missing.", module);
                }
            } else if (!append) {
                try {
                    ShoppingListEvents.clearListInfo(delegator, shoppingListId);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
            }
    		Debug.log("\n\n items.size() === "+items.size()+"\n\n");
    		String quantityPurchased = null;
    		Map<String, Object> serviceResult = null, product = null;
    		Map<String, Object> ctx = null;
    		for (int i = 0; i < items.size(); i++) {
    			serviceResult = null;
    			product = items.get(i);
    			BigDecimal quantity = null;
                try {
                	//UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
                	if(product.get("quantity") instanceof BigDecimal){
                		quantity = (BigDecimal)product.get("quantity");
                	}else{
                		quantity = new BigDecimal((String)product.get("quantity"));
                	}
                	
                    ctx = UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", product.get("productId"), "quantity", quantity);
                    if(UtilValidate.isNotEmpty(product.get("quantityPurchased"))){
                    	ctx.put("quantityPurchased", product.get("quantityPurchased"));
                    }
                    Debug.log("\n\n ctx === "+ctx+"\n\n");
                    serviceResult = dispatcher.runSync("createShoppingListItem", ctx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems creating ShoppingList item entity", module);
                }
                Debug.log("\n\n serviceResult === "+serviceResult+"\n\n");
                // check for errors
                if (ServiceUtil.isError(serviceResult)) {
                    Debug.logError(ServiceUtil.getErrorMessage(serviceResult), module);
                }
                ctx.clear();
                updateQty(shoppingListId, (String)product.get("productId"), userLogin);
            }
    		listItems = delegator.findList("ShoppingListItem", EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", shoppingListId)), UtilMisc.toSet("productId"), null, readonly, false);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	//updateQty(shoppingListId, userLogin);
    	return listItems.size()+"";
    }
    
    public static int updateQty(String shoppingListId, String productId, GenericValue userLogin){
    	List<GenericValue> listItems = null;
    	try{
    		Debug.log("\n\n shoppingListId== "+shoppingListId+" , productId = "+productId+" \n\n");
    		EntityCondition entityCondition = null;
    		GenericValue shoppingList = delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", shoppingListId));
        	if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
        		entityCondition = EntityCondition.makeCondition("parentShoppingListId", EntityOperator.EQUALS, shoppingList.getString("parentShoppingListId"));
        		List<GenericValue> slt = delegator.findList("ShoppingList", entityCondition, UtilMisc.toSet("shoppingListId"), null, readonly, false);
        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("shoppingListId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(slt, "shoppingListId", true)),
        								EntityOperator.AND,
        								EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)
        							);
        		listItems = delegator.findList("ShoppingListItem", entityCondition, UtilMisc.toSet("shoppingListId", "productId","quantity"), null, readonly, false);
        		//shoppingList = delegator.findOne("ShoppingList", false,  UtilMisc.toMap("shoppingListId", shoppingList.getString("parentShoppingListId")));
        		BigDecimal quantity = BigDecimal.ZERO;
        		Map ctx = null;//UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", product.get("productId"), "quantity", quantity);
        		GenericValue item = null;
        		for(GenericValue gv : listItems){
        			Debug.log("\n ********************* \n111 quantity == "+quantity+"\n ********************* \n");
        			quantity = quantity.add(gv.getBigDecimal("quantity"));
        		}
        		
        		ctx = UtilMisc.<String, Object>toMap("shoppingListId", shoppingList.getString("parentShoppingListId"), "productId", productId);
    			item = EntityUtil.getFirst(delegator.findList("ShoppingListItem", EntityCondition.makeCondition(ctx), null, null, null, false));
    			
    			
    			BigDecimal quantityPurchased = BigDecimal.ZERO;
    			if(UtilValidate.isNotEmpty(item)){
    				quantityPurchased = item.getBigDecimal("quantityPurchased");
    				Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
        			Debug.log("\n ********************* \n quantityPurchased == "+quantityPurchased+"\n ********************* \n");
        			Debug.log("\n ********************* \n quantityPurchased.compareTo(quantity) == "+quantityPurchased.compareTo(quantity)+"\n ********************* \n");
    				if(quantityPurchased.compareTo(BigDecimal.ZERO) == 0 || quantity.compareTo(BigDecimal.ZERO) == 0){
    					quantity = BigDecimal.ZERO;
    				}else if (quantityPurchased.compareTo(quantity) == 0){
    					quantity = BigDecimal.ZERO;
    				}else if (quantityPurchased.compareTo(quantity) == 1){
    					quantity = quantityPurchased.subtract(quantity);
    				}else if (quantityPurchased.compareTo(quantity) == -1){
    					quantity = quantity.subtract(quantityPurchased);
    				}
    			}
    			Debug.log("\n ********************* \n new quantity == "+quantity+"\n ********************* \n");
//    			Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
//    			Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
    			
    			if(quantity.compareTo(new BigDecimal("-1")) == 1){
	    			item.set("quantity", quantity);
	    			item.store();
    			}
        		
        		ctx.clear();
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(UtilValidate.isEmpty(listItems)){
    		return 0;
    	}else{
    		return listItems.size();
    	}
    }
    
    
    public static int updateQtyRemove(String shoppingListId, String productId, GenericValue userLogin){
    	List<GenericValue> listItems = null;
    	try{
    		Debug.log("\n\n shoppingListId== "+shoppingListId+" , productId = "+productId+" \n\n");
    		EntityCondition entityCondition = null;
    		GenericValue shoppingList = delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", shoppingListId));
        	if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
        		entityCondition = EntityCondition.makeCondition("parentShoppingListId", EntityOperator.EQUALS, shoppingList.getString("parentShoppingListId"));
        		List<GenericValue> slt = delegator.findList("ShoppingList", entityCondition, UtilMisc.toSet("shoppingListId"), null, readonly, false);
        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("shoppingListId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(slt, "shoppingListId", true)),
        								EntityOperator.AND,
        								EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId)
        							);
        		listItems = delegator.findList("ShoppingListItem", entityCondition, UtilMisc.toSet("shoppingListId", "productId","quantity"), null, readonly, false);
        		//shoppingList = delegator.findOne("ShoppingList", false,  UtilMisc.toMap("shoppingListId", shoppingList.getString("parentShoppingListId")));
        		BigDecimal quantity = BigDecimal.ZERO;
        		Map ctx = null;//UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", product.get("productId"), "quantity", quantity);
        		GenericValue item = null;
        		for(GenericValue gv : listItems){
        			quantity = quantity.add(gv.getBigDecimal("quantity"));
        		}
        		
        		ctx = UtilMisc.<String, Object>toMap("shoppingListId", shoppingList.getString("parentShoppingListId"), "productId", productId);
    			item = EntityUtil.getFirst(delegator.findList("ShoppingListItem", EntityCondition.makeCondition(ctx), null, null, null, false));
    			
    			
    			BigDecimal quantityPurchased = BigDecimal.ZERO;
    			if(UtilValidate.isNotEmpty(item)){
    				quantityPurchased = item.getBigDecimal("quantityPurchased");
    				Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
        			Debug.log("\n ********************* \n quantityPurchased == "+quantityPurchased+"\n ********************* \n");
        			Debug.log("\n ********************* \n quantityPurchased.compareTo(quantity) == "+quantityPurchased.compareTo(quantity)+"\n ********************* \n");
    				if(quantityPurchased.compareTo(BigDecimal.ZERO) == 0){
    					quantity = BigDecimal.ZERO;
    				}else if(quantity.compareTo(BigDecimal.ZERO) == 0){
    					quantity = quantityPurchased;
    				}else if (quantityPurchased.compareTo(quantity) == 0){
    					quantity = BigDecimal.ZERO;
    				}else if (quantityPurchased.compareTo(quantity) == 1){
    					quantity = quantityPurchased.subtract(quantity);
    				}else if (quantityPurchased.compareTo(quantity) == -1){
    					quantity = quantity.subtract(quantityPurchased);
    				}
    			}
    			Debug.log("\n ********************* \n new quantity == "+quantity+"\n ********************* \n");
//    			Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
//    			Debug.log("\n ********************* \n quantity == "+quantity+"\n ********************* \n");
    			
    			if(quantity.compareTo(new BigDecimal("-1")) == 1){
	    			item.set("quantity", quantity);
	    			item.store();
    			}
        		
        		ctx.clear();
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(UtilValidate.isEmpty(listItems)){
    		return 0;
    	}else{
    		return listItems.size();
    	}
    }
    
    
    public static int updateQtyR(GenericValue shoppingList){
    	List<GenericValue> listItems = null;
    	try{
    		EntityCondition entityCondition = null;
    		//GenericValue shoppingList = delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", shoppingListId));
        	if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
        		entityCondition = EntityCondition.makeCondition("shoppingListId", EntityOperator.EQUALS, shoppingList.getString("parentShoppingListId"));
        		List<GenericValue> slt = delegator.findList("ShoppingList", entityCondition, UtilMisc.toSet("shoppingListId"), null, readonly, false);
        		
        		shoppingList = EntityUtil.getFirst(slt);
        		
        		if(UtilValidate.isNotEmpty(shoppingList)){
        			listItems = shoppingList.getRelated("ShoppingListItem");
        			for(GenericValue gv : listItems){
        				gv.set("quantityPurchased", gv.getBigDecimal("quantity"));
        				gv.store();
        			}
        		}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(UtilValidate.isEmpty(listItems)){
    		return 0;
    	}else{
    		return listItems.size();
    	}
    }
    
    public static int updateQtyOrder(GenericValue item, BigDecimal qty){
    	try{
        	if(UtilValidate.isNotEmpty(item)){
    			//Map ctx = UtilMisc.<String, Object>toMap("shoppingListId", shoppingListId, "productId", productId);
    			//GenericValue item = EntityUtil.getFirst(delegator.findList("ShoppingListItem", EntityCondition.makeCondition(ctx), null, null, null, false));
        		BigDecimal actQty = item.getBigDecimal("quantity");
        		qty = actQty.add(qty);
    			if(qty.compareTo(new BigDecimal("-1")) == 1){
	    			item.set("quantity", qty);
	    			item.set("quantityPurchased", qty);
	    			item.store();
    			}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return 0;
    }
	
    public static int updateQty(String shoppingListId, GenericValue userLogin){
    	List<GenericValue> listItems = null;
    	try{
    		GenericValue shoppingList = delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", shoppingListId));
        	if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
        		listItems = delegator.findList("ShoppingListItem", EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", shoppingListId)), UtilMisc.toSet("productId","quantity"), null, readonly, false);
        		//shoppingList = delegator.findOne("ShoppingList", false,  UtilMisc.toMap("shoppingListId", shoppingList.getString("parentShoppingListId")));
        		BigDecimal quantity = BigDecimal.ZERO;
        		Map ctx = null;//UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", product.get("productId"), "quantity", quantity);
        		GenericValue item = null;
        		for(GenericValue gv : listItems){
        			quantity = gv.getBigDecimal("quantity");
        			ctx = UtilMisc.<String, Object>toMap("shoppingListId", shoppingList.getString("parentShoppingListId"), "productId", gv.getString("productId"));
        			item = EntityUtil.getFirst(delegator.findList("ShoppingListItem", EntityCondition.makeCondition(ctx), null, null, readonly, false));
        			if(UtilValidate.isNotEmpty(item)){
        				quantity = item.getBigDecimal("quantityPurchased").subtract(gv.getBigDecimal("quantity"));
        			}else{
        				Debug.log("\n\n ShoppingListItem not found : \n\n");
        				continue;
        			}
        			
        			item.set("quantity", quantity);
        			item.store();
            		ctx.clear();
        		}
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(UtilValidate.isEmpty(listItems)){
    		return 0;
    	}else{
    		return listItems.size();
    	}
    }
    
    public static String getHashType() {
        String hashType = UtilProperties.getPropertyValue("security.properties", "password.encrypt.hash.type");

        if (UtilValidate.isEmpty(hashType)) {
            Debug.logWarning("Password encrypt hash type is not specified in security.properties, use SHA", module);
            hashType = "SHA";
        }

        return hashType;
    }
    
    public static String getNewPassword(){
    	String passwordToSend = null;
    	boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
    	if (useEncryption) {
            // password encrypted, can't send, generate new password and email to user
            passwordToSend = RandomStringUtils.randomAlphanumeric(Integer.parseInt(UtilProperties.getPropertyValue("security", "password.length.min", "5")));
            if ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.lowercase"))){
                passwordToSend=passwordToSend.toLowerCase();
            }
            //supposedUserLogin.set("currentPassword", HashCrypt.cryptUTF8(LoginServices.getHashType(), null, passwordToSend));
            //supposedUserLogin.set("passwordHint", "Auto-Generated Password");
            //if ("true".equals(UtilProperties.getPropertyValue("security.properties", "password.email_password.require_password_change"))){
               // supposedUserLogin.set("requirePasswordChange", "Y");
           // }
        } 
    	
    	return passwordToSend;
    }
	
    
    /*
     *  {	
      "storeId": "9000",
      "PaymentMethod": "Credit  Card / Debit Card / Net Banking",
      "username": "s.k.pradeepkumar@gmail.com",
      "slotId":"SLOT1",
      "userLoginId": "abc@gmail.com",
      "BillingAddress": {
        "BillingFirstname": "TestFN",
        "BillingLastname": "TestLN",
        "BillingAddress1": "address122",
        "BillingAddress2": "address2222",
        "BillingCity": "Bangalore",
        "BillingPostcode": "560085",
        "BillingCountry": "IN",
        "BillingState": "IN-KA",
        "BillingContact": "5345435345435"
      },
      "ShippingAddress": {
        "ShippingFirstname": "TestSFN",
        "ShippingLastname": "TestSLN",
        "ShippingAddress1": "ShippingStreet_1222",
        "ShippingAddress2": "ShippingStreet_2333",
        "ShippingCity": "Bangalore",
        "ShippingPostcode": "3453454",
        "ShippingCountry": "IN",
        "ShippingState": "IN-KA",
        "ShippingContact": "34534534543"
      },
      "Subtotal": "100",
      "DiscountAmount": "10",
      "TaxAmount": "0",
      "ShippingAmount": "0.0",
      "GrandTotal": "98.00",
      "LineItemCount": "1",
      "PG_TransactionId": "2342423424",
      "PG_Code": "EBS",
      "Comments": "Comments",
      "CurrencyCode": "INR",
      "Mag_Order_Status": "processing",
      "OrderItems": {
        "Item": [
          {
            "SKU": "10000",
            "Quantity": "1",
            "UnitPrice": "100",
            "TaxAmount": "5",
            "DiscountAmount": "2",
            "RowTotal": "1"
          }
        ]
      }
   	} 
     * 
     */
    
    public static Map createOrder(Map paramMap) throws Exception {
    	Map result = ServiceUtil.returnSuccess();
    	try{
	    	GenericValue userLogin = (GenericValue)paramMap.get("userLogin");//delegator.findOne("UserLogin",true, UtilMisc.toMap("userLoginId", paramMap.get("username")));
	    	
//	    	if(UtilValidate.isEmpty(userLogin)){ 
//	    		return ServiceUtil.returnError("User not found");
//	    	}
	    	String storeId = (String)paramMap.get("storeId");
	        Map<String, Object> ctx = UtilMisc.toMap("partyId", userLogin.getString("partyId"), "orderTypeId", "SALES_ORDER", "currencyUom", "INR", "productStoreId", storeId);
	        
	        String contactMechId = (String)paramMap.get("shippingAddressId");
	        if(UtilValidate.isEmpty(contactMechId)){
	        	contactMechId = userLogin.getString("contactMechId");
	        }
	        
	      //  List<GenericValue> orderPaymentInfo = FastList.newInstance();
	        List<GenericValue> orderPaymentInfo = new ArrayList<GenericValue>();
	        GenericValue orderContactMech = delegator.makeValue("OrderContactMech", UtilMisc.toMap("contactMechId", contactMechId, "contactMechPurposeTypeId", "BILLING_LOCATION"));
	        orderPaymentInfo.add(orderContactMech);
	        //EXT_COD or EXT_ICICI_FULL
	        GenericValue orderPaymentPreference = delegator.makeValue("OrderPaymentPreference", UtilMisc.toMap("paymentMethodTypeId", (String)paramMap.get("paymentMethod"),
	                "statusId", "PAYMENT_NOT_AUTH", "overflowFlag", "N", "maxAmount", new BigDecimal((String)paramMap.get("grandTotal"))));
	        orderPaymentInfo.add(orderPaymentPreference);
	        ctx.put("orderPaymentInfo", orderPaymentInfo);
	        
	        
	        
	        //List<GenericValue> orderItemShipGroupInfo = FastList.newInstance();
	        List<GenericValue> orderItemShipGroupInfo = new ArrayList<GenericValue>();
	        orderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
	        orderItemShipGroupInfo.add(orderContactMech);
	
	        GenericValue orderItemShipGroup = delegator.makeValue("OrderItemShipGroup", UtilMisc.toMap("carrierPartyId", "Company", "contactMechId",  paramMap.get("shippingAddressId"), "isGift", "N",
	                "shipGroupSeqId", "00001", "shipmentMethodTypeId", "LOCAL_DELIVERY"));
	        orderItemShipGroupInfo.add(orderItemShipGroup);
	        
	        List<GenericValue> orderItems = new ArrayList<GenericValue>();
	        List<HashMap> items = (ArrayList)((Map)paramMap.get("orderItems")).get("Item");
	        int nextItemSeq = 1;
	        for(Map item : items){
	        	String orderItemSeqId = UtilFormatOut.formatPaddedNumber(nextItemSeq, 5);
		        GenericValue orderItemShipGroupAssoc = delegator.makeValue("OrderItemShipGroupAssoc", UtilMisc.toMap("orderItemSeqId", orderItemSeqId, "quantity", new BigDecimal((String)item.get("quantity")), "shipGroupSeqId", "00001"));
		        orderItemShipGroupInfo.add(orderItemShipGroupAssoc);
		        
		        GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item.get("productId")), true);
		        
		        
		        GenericValue orderItem = delegator.makeValue("OrderItem", UtilMisc.toMap("orderItemSeqId", orderItemSeqId, "orderItemTypeId", "PRODUCT_ORDER_ITEM", "prodCatalogId", item.get("prodCatalogId"), "productCategoryId", item.get("productCategoryId"), "productId",  item.get("productId"), "quantity",   new BigDecimal((String)item.get("quantity")), "selectedAmount", BigDecimal.ZERO));
		        orderItem.set("isPromo", "N");
		        orderItem.set("isModifiedPrice", "N");
		        orderItem.set("unitPrice", new BigDecimal((String)item.get("unitPrice")));
		        orderItem.set("unitListPrice", new BigDecimal((String)item.get("unitListPrice")));
		        orderItem.set("statusId", "ITEM_CREATED");
		        orderItem.set("itemDescription", product.getString("productName"));
		
		        orderItems.add(orderItem);
		        nextItemSeq++;
	        }
	
	        GenericValue orderAdjustment = null;
	        orderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "SHIPPING_CHARGES", "shipGroupSeqId", "00001", "amount", BigDecimal.ZERO));
	        //orderItemShipGroupInfo.add(orderAdjustment);
	        
	
	        ctx.put("orderItemShipGroupInfo", orderItemShipGroupInfo);
	
	        //List<GenericValue> orderAdjustments = FastList.newInstance();
	        if(UtilValidate.isNotEmpty(paramMap.get("discountAmount"))){
//		        List<GenericValue> orderAdjustments = new ArrayList<GenericValue>();
//		        orderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT", "productPromoActionSeqId", "01", "productPromoId", "9011", "productPromoRuleId", "01", "amount", new BigDecimal(-3.84)));
//		        orderAdjustments.add(orderAdjustment);
//		        ctx.put("orderAdjustments", orderAdjustments);
	        }
	        ctx.put("orderAdjustments", UtilMisc.toList(orderAdjustment));
	        ctx.put("orderItems", orderItems);
	
	       // List<GenericValue> orderTerms = FastList.newInstance();
	        List<GenericValue> orderTerms = new ArrayList<GenericValue>();
	        ctx.put("orderTerms", orderTerms);
	
	        GenericValue OrderContactMech = delegator.makeValue("OrderContactMech", new HashMap());
	        OrderContactMech.set("contactMechPurposeTypeId", "SHIPPING_LOCATION");
	        OrderContactMech.set("contactMechId", paramMap.get("shippingAddressId"));
	       // List<GenericValue> orderContactMechs = FastList.newInstance();
	        List<GenericValue> orderContactMechs = new ArrayList<GenericValue>();
	        orderContactMechs.add(OrderContactMech);
	
	        ctx.put("placingCustomerPartyId", userLogin.getString("partyId"));
	        ctx.put("endUserCustomerPartyId", userLogin.getString("partyId"));
	        ctx.put("shipToCustomerPartyId", userLogin.getString("partyId"));
	        ctx.put("billToCustomerPartyId", userLogin.getString("partyId"));
	        ctx.put("billFromVendorPartyId", "Company");
	        
	        if(UtilValidate.isNotEmpty(paramMap.get("slotId"))){
		        ctx.put("slotId", paramMap.get("slotId"));
		        ctx.put("slot", paramMap.get("slotId"));
	        }
	        if(UtilValidate.isNotEmpty(paramMap.get("deliveryDate"))){
	        	if(paramMap.get("deliveryDate") instanceof Integer){
	        		ctx.put("deliveryDate", UtilDateTime.getDayStart(UtilDateTime.getTimestamp((Integer)paramMap.get("deliveryDate"))));
	        	}else if(paramMap.get("deliveryDate") instanceof String){
	        		ctx.put("deliveryDate", UtilDateTime.getDayStart(UtilDateTime.getTimestamp(Long.parseLong((String)paramMap.get("deliveryDate")))));
	        	}else{
	        		ctx.put("deliveryDate", UtilDateTime.getDayStart(UtilDateTime.getTimestamp((Long)paramMap.get("deliveryDate"))));
	        	}
	        }
	        if(UtilValidate.isNotEmpty(paramMap.get("pG_TransactionId"))){
	        	ctx.put("transactionId", (String)paramMap.get("pG_TransactionId"));
	        }
	        
	        if(UtilValidate.isNotEmpty(paramMap.get("orderName"))){
	        	ctx.put("orderName", (String)paramMap.get("orderName"));
	        }
	
	        ctx.put("userLogin", userLogin);
	        result = dispatcher.runSync("storeOrder", ctx);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return result;
    }
    
    public static List slotSelection(Map paramMap) throws IOException, ParseException, GenericEntityException {
    		List listOption=new ArrayList();  
   			Map<String,Double> maxDelivery = new HashMap<String,Double>();
   			Map<String,String> blockedDays  = new HashMap<String,String>();
   			GenericValue userLogin = (GenericValue) paramMap.get("userLogin");
   			String currentmMechId = (String)paramMap.get("contactMechId");
  			boolean orderPresent = false;
   			
  			
  			//SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
  			//Date d = (Date) sdf.parse(request.getParameter("deliveryDate"));
  			Timestamp deliveryDate1 = UtilDateTime.toTimestamp((String)paramMap.get("date")+" 00:00:00");
  			Timestamp delivery=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
   			List orderType= new ArrayList();
  			Map listCount=new HashMap();
  			
  			List<GenericValue> orderslotType = CheckOutEvents.getAllSlots(delegator);
  			Map slotTiming = FastMap.newInstance();
  			if(!UtilValidate.isEmpty(orderslotType)){
  				for(int i=0;i<orderslotType.size();i++){
  					GenericValue gv = orderslotType.get(i);
  					orderType.add(i,gv.get("slotType"));
   					maxDelivery.put((String) gv.get("slotType"),gv.getDouble("maxDelivery"));
   					slotTiming.put(gv.getString("slotType"), gv.getString("slotTiming"));
   					blockedDays.put((String) gv.get("slotType"),gv.getString("blockDays"));
  				}
  			}
  			for(int t=0;t<orderType.size();t++){
  				listCount.put(orderType.get(t),0);
  			}
  				 
  			List<EntityCondition> promoConditions = new ArrayList<EntityCondition>();
		    promoConditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.EQUALS,deliveryDate1));
 			promoConditions.add(EntityCondition.makeCondition("orderId",EntityOperator.NOT_EQUAL,null));
 			promoConditions.add(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"));
  			List<GenericValue> ordersList1= delegator.findList("OrderSlot", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), null, UtilMisc.toList("orderId","slotStatus"), null, false);
 			
  			List<EntityCondition> CondnList=new ArrayList<EntityCondition>();
			CondnList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,userLogin.getString("partyId")));
			CondnList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,deliveryDate1));
 			CondnList.add(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,currentmMechId));
 			
   			////////////////////////////
 			if(UtilValidate.isNotEmpty(ordersList1)){
			for(int t=0;t<orderType.size();t++){
				List temp = EntityUtil.filterByAnd(ordersList1, UtilMisc.toMap("slotType",orderType.get(t)));
				if(!UtilValidate.isEmpty(temp)){
					listCount.put(orderType.get(t),temp.size());
					}
				}
 			}
 			
			List orderIds =   EntityUtil.getFieldListFromEntityList(ordersList1, "orderId", true); 
			List<GenericValue> orderValues = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIds),UtilMisc.toSet("orderId","statusId"),null, null, false);
			List temp1 = EntityUtil.filterByCondition(ordersList1, EntityCondition.makeCondition(CondnList,EntityOperator.AND));
			
			if(UtilValidate.isNotEmpty(temp1)){
				CondnList.clear();
				CondnList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,EntityUtil.getFieldListFromEntityList(temp1, "orderId", true)));
				CondnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ORDER_APPROVED"));
 				if(UtilValidate.isNotEmpty(EntityUtil.filterByCondition(orderValues, EntityCondition.makeCondition(CondnList,EntityOperator.AND)))){
					orderPresent = true; 
				} else{
					orderPresent = false;
				}
			} else{
				orderPresent = false;
			}

			for(int i=0;i<orderslotType.size();i++){
            	 Double timing =  (Double) maxDelivery.get(orderType.get(i));
         			Integer  o =  (Integer) listCount.get(orderType.get(i));
  				if(o.intValue()<timing.intValue())
  					listOption.add(orderType.get(i));	
  			}
 
   			String buffer="";
   			List slot = new ArrayList();
   			List slots = new ArrayList();
  			if(listOption.size()>=1  && !orderPresent ){
	  			for (int i=0;i<listOption.size();i++) {
	  				//String f=listOption.get(i)+"_"+deliveryDate1;
	  				String slotTxt = (String) listOption.get(i);
	  				String blockString = blockedDays.get(slotTxt);
	  				////////////////////
 	  				boolean blocked = false;
		  			  if(UtilValidate.isNotEmpty(blockString)){
						  try{
							  String[]  blockDayArray  = blockString.split(","); 
							 		if(UtilValidate.isNotEmpty(blockDayArray)){
 							 			SimpleDateFormat parseDate  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							 			for(int k= 0 ; k <blockDayArray.length;k++){
 							 				String bit = blockDayArray[k];
							 				String[] bitTimings  = bit.split("~~");
											Timestamp start = null;
											Timestamp end = null;
											if (UtilValidate.isNotEmpty(bitTimings[0])&& UtilValidate.isNotEmpty(bitTimings[1])) {
												Date from = (Date) parseDate.parse(bitTimings[0]);
												start = new Timestamp(from.getTime());
												Date d1 = (Date) parseDate.parse(bitTimings[1]);
												end = new Timestamp(d1.getTime());
	 											if( deliveryDate1.after(start)  && deliveryDate1.before(end) ){
												blocked = true;
												break;											
											}
										}
							 		}					
							 	}	 
						  	}catch(Exception e){
						  		blocked = false;
						  }
					  }
 	  				//////////////////////
		  			if(!blocked){	  				
		  				//String optionsTxt = "<input type='radio' id='dayOption' name='dayOption' value='"+f+"'>"+slotTxt;
		  				slot.add(UtilMisc.toMap("key", listOption.get(i), "status", "Available", "timing", slotTiming.get(listOption.get(i))));
		  				//buffer=buffer.concat(" " +optionsTxt);
		  			}
	  				}
	  			//buffer=buffer+"</select></td>"; 
  			}else{
  				buffer="No Slot is Avaiable";
  			}
  			
  			slots.add(UtilMisc.toMap(deliveryDate1+"", slot));
  			
  			
  		return slots;
      	
      }
    
    public static Map orderDetail(Map paramMap, List result){
    	Map addnl = null;
    	try{
    		EntityCondition enityCondition = EntityCondition.makeCondition(
    											EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, paramMap.get("orderId")),
    											EntityOperator.AND,
    											EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
    		GenericValue gv = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", paramMap.get("orderId")), false);
    		GenericValue slotGv = EntityUtil.getFirst(delegator.findByAnd("OrderSlotType", UtilMisc.toMap("slotType", gv.getString("slot"))));
    		//result = gv.getRelated("OrderItem");
    		//Debug.log("\n ============================== \n slotGv == "+slotGv+"\n ============================== \n");
    		addnl = UtilMisc.toMap("grandTotal", gv.getString("grandTotal"),"deliveryDate", gv.getString("deliveryDate"), "orderId", gv.getString("orderId"), "orderDate", gv.getString("orderDate"));
    		if(UtilValidate.isNotEmpty(slotGv)){
    			addnl.put("slot", slotGv.getString("slotTiming"));
    		}
    		//Debug.log("\n ============================== \n gv == "+gv+"\n ============================== \n");
    		//Debug.log("\n ============================== \n userLogin == "+(GenericValue)paramMap.get("userLogin")+"\n ============================== \n");
    		if(UtilValidate.isNotEmpty(gv)){
    			List slitems = ILinksAppUtil.getParentShoppingListItems(gv, delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", gv.getString("createdBy"))));
    			Set field = UtilMisc.toSet("orderId", "orderItemSeqId", "productId", "prodCatalogId", "productCategoryId", "isPromo", "quantity", "unitPrice");
    			field.add("unitListPrice");		
    			field.add("itemDescription");	     
    			field.add("statusId");		     
    			field.add("parentProductCategoryId");
    			List<GenericValue> items = delegator.findList("OrderItem", enityCondition, field, null, readonly, false);
    			addnl.put("itemsCount", items.size());
    			//Debug.log("\n ============================== \n items == "+items+"\n ============================== \n");
    			String autoSaveListId = null;
    			if((UtilValidate.isEmpty(paramMap.get("getOrders")) || !("Y".equalsIgnoreCase((String)paramMap.get("getOrders"))))
    					&& gv.getString("productStoreId").equalsIgnoreCase("UDAILY")
    					&& (!gv.getString("statusId").equalsIgnoreCase("ORDER_REJECTED") &&	!gv.getString("statusId").equalsIgnoreCase("ORDER_CANCELLED"))){
    				GenericValue userLogin = (GenericValue)paramMap.get("userLogin");
    				autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, (GenericValue)paramMap.get("userLogin"), gv.getString("productStoreId"), "SLT_UDAILY_COUPON", UDAILY_PERSISTANT_PRD_LIST_NAME);
    				userLogin.put("shoppingListId", autoSaveListId);
    				userLogin.store();
    			}
    			Map<String, Object> item = null;
    			Debug.log("\n ============================== \n items == "+items+"\n ============================== \n");
    			for(int i = 0; i < items.size(); i++){
    				item = items.get(i).getAllFields();
    				//Debug.log("\n ============================== \n item == "+item+"\n ============================== \n");
    				gv = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.get("productId")));
    				item.put("productName", gv.getString("productName"));
    				item.put("mSmallImageUrl", gv.getString("mSmallImageUrl"));
    				Debug.log("\n ,,,,,,,,,,,,,,,,,, slitems == "+slitems+"\n /................. \n");
    				if(UtilValidate.isNotEmpty(slitems)){
    					GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", item.get("productId")))));
    					if(UtilValidate.isNotEmpty(slitem)){
    						item.put("couponQty", slitem.getString("quantity"));
    					}
    				}
    				
    				result.add(item);
//    				if(UtilValidate.isNotEmpty(autoSaveListId)){
//    					createOrUpdateUdailyCart(UtilMisc.toMap("shoppingListId", autoSaveListId, "userLogin",(GenericValue)paramMap.get("userLogin"), "storeId", gv.getString("productStoreId"), "quantity", item.get("quantity"), "productId", gv.getString("productId")));
//    				}
    			}
    			
    			items.clear();
    			field.clear();
    			field.add("paymentMethodTypeId");
    			enityCondition = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, paramMap.get("orderId"));
    			items = delegator.findList("OrderPaymentPreference", enityCondition, field, null, readonly, false);
    			//Debug.log("\n ============================== \n items 222 == "+items+"\n ============================== \n");
    			addnl.putAll(items.get(0).getAllFields());
    			
    			items.clear();
    			enityCondition = EntityCondition.makeCondition(
									EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, paramMap.get("orderId")),
										EntityOperator.AND,
									EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
    			items = delegator.findList("OrderContactMech", enityCondition, UtilMisc.toSet("orderId", "contactMechPurposeTypeId", "contactMechId"), null, readonly, false);
    			
    			if(UtilValidate.isNotEmpty(items)){
    				gv.clear();
    				gv = EntityUtil.getFirst(items);
    				if(UtilValidate.isNotEmpty(gv)){
    					addnl.put("shippingAddress", delegator.findOne("PostalAddress", true, UtilMisc.toMap("contactMechId", gv.getString("contactMechId"))).getAllFields());
    				}
    			}
    			addnl.put("referalSavings", "100");
    			//result.add(addnl);
    			
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return addnl;
    }
    
    public static void addCouponProductsToCart(Map paramMap){
    	try{
    		GenericValue gv = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId",(String) paramMap.get("orderId")));
    		if(UtilValidate.isEmpty(gv) || !(gv.getString("productStoreId").equalsIgnoreCase("UDAILY"))){
    			return;
    		}
    		String storeId = gv.getString("productStoreId");
    		String autoSaveListId = null;
			if(gv.getString("productStoreId").equalsIgnoreCase("UDAILY") && (!gv.getString("statusId").equalsIgnoreCase("ORDER_REJECTED") &&	!gv.getString("statusId").equalsIgnoreCase("ORDER_CANCELLED"))){
				GenericValue userLogin = (GenericValue)paramMap.get("userLogin");
				autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, (GenericValue)paramMap.get("userLogin"), storeId, "SLT_UDAILY_COUPON", UDAILY_PERSISTANT_PRD_LIST_NAME);
				userLogin.put("shoppingListId", autoSaveListId);
				userLogin.store();
			}
			
			List slitems = ILinksAppUtil.getParentShoppingListItems(gv, delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId", gv.getString("createdBy"))));
			Set field = UtilMisc.toSet("orderId", "orderItemSeqId", "productId", "prodCatalogId", "productCategoryId", "isPromo", "quantity", "unitPrice");
			field.add("unitListPrice");		
			field.add("itemDescription");	     
			field.add("statusId");		     
			field.add("parentProductCategoryId");
			
			EntityCondition enityCondition = EntityCondition.makeCondition(
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, paramMap.get("orderId")),
					EntityOperator.AND,
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
			List<GenericValue> items = delegator.findList("OrderItem", enityCondition, field, null, readonly, false);
			Map<String, Object> item = null;
			GenericValue product = null;
			for(int i = 0; i < items.size(); i++){
				item = items.get(i).getAllFields();
				//Debug.log("\n ============================== \n item == "+item+"\n ============================== \n");
				product = delegator.findOne("Product", false, UtilMisc.toMap("productId", item.get("productId")));
				Debug.log("\n ,,,,,,,,,,,,,,,,,, slitems == "+slitems+"\n /................. \n");
				if(UtilValidate.isNotEmpty(slitems)){
					GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", item.get("productId")))));
					if(UtilValidate.isNotEmpty(slitem)){
						item.put("couponQty", slitem.getString("quantity"));
					}
				}
				
				if(UtilValidate.isNotEmpty(autoSaveListId)){
					createOrUpdateUdailyCart(UtilMisc.toMap("shoppingListId", autoSaveListId, "userLogin",(GenericValue)paramMap.get("userLogin"), "storeId", storeId, "quantity", item.get("quantity"), "productId", product.getString("productId")));
				}
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static void createOrUpdateUdailyCart(Map paramMap){
    	try{
    		List pcm = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toMap("productId", paramMap.get("productId"))), null, null, readonly, true);
			GenericValue prodAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", EntityCondition.makeCondition(UtilMisc.toMap("productAssocTypeId", "PRODUCT_SUBSTITUTE", "productId", paramMap.get("productId"))), UtilMisc.toSet("productIdTo"), null, readonly, true));
			
			//BigDecimal quantityPurchased = BigDecimal.ZERO;
			
			List<GenericValue> shoppingListItems = null;
			if(UtilValidate.isNotEmpty(prodAssoc)){
				Debug.log("\n\n createOrUpdateUdailyCart cond == "+EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", (String)paramMap.get("shoppingListId"), "productId", prodAssoc.getString("productIdTo")))+"\n\n");
				shoppingListItems = delegator.findList("ShoppingListItem", EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", (String)paramMap.get("shoppingListId"), "productId", prodAssoc.getString("productIdTo"))), null, null, readonly, false);
			}
    		if(UtilValidate.isEmpty(shoppingListItems)){
    			//create the shopping list items
//    			Debug.log("\n\n prodAssoc === "+prodAssoc+"\n\n");
    			BigDecimal total = (BigDecimal)paramMap.get("quantity");
    			if(UtilValidate.isNotEmpty(prodAssoc)){
					GenericValue gv = EntityUtil.getFirst(pcm);
					paramMap.put("append", true);
					if(UtilValidate.isNotEmpty(gv) && UtilValidate.isNotEmpty(gv.getString("quantity"))){
						total = total.multiply(gv.getBigDecimal("quantity")) ;
					}
					Debug.log("\n\n 1111111111111 total === "+total+"\n\n");
					
					paramMap.put("items", UtilMisc.toList(UtilMisc.toMap("productId", prodAssoc.getString("productIdTo"),"quantity", total, "quantityPurchased", total)));
					ILinksAppUtil.addToCart(paramMap);
    			}
    		}else{
    			//update the shopping list items
    			GenericValue item = EntityUtil.getFirst(shoppingListItems);
    			BigDecimal qty = item.getBigDecimal("quantityPurchased");
    			
    			BigDecimal total = (BigDecimal)paramMap.get("quantity");
    			if(UtilValidate.isNotEmpty(prodAssoc)){
					GenericValue gv = EntityUtil.getFirst(pcm);
					paramMap.put("append", true);
					if(UtilValidate.isNotEmpty(gv) && UtilValidate.isNotEmpty(gv.getString("quantity"))){
						total = total.multiply(gv.getBigDecimal("quantity")) ;
						//quantityPurchased = total.multiply(gv.getBigDecimal("quantity")) ;
					}
					//quantityPurchased = total.add(qty);
					Debug.log("\n\n  ELSE  1111111111111 qty === "+qty+"\n\n");
					Debug.log("\n\n  ELSE  1111111111111 total === "+total+"\n\n");
					//Debug.log("\n\n  ELSE  1111111111111 quantityPurchased === "+quantityPurchased+"\n\n");
					paramMap.put("items", UtilMisc.toList(UtilMisc.toMap("productId", prodAssoc.getString("productIdTo"),"quantity", total, "quantityPurchased", total)));
					ILinksAppUtil.addToCart(paramMap);
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public static String getAutoSaveListId(Delegator delegator, LocalDispatcher dispatcher, String partyId, GenericValue userLogin, String productStoreId, String shoppingListTypeId, String listName) throws GenericEntityException, GenericServiceException {
    	return getAutoSaveListId(delegator, dispatcher, partyId, userLogin, productStoreId, shoppingListTypeId, listName, null);
    }
    
    /**
     * Finds or creates a specialized (auto-save) shopping list used to record shopping bag contents between user visits.
     */
    public static String getAutoSaveListId(Delegator delegator, LocalDispatcher dispatcher, String partyId, GenericValue userLogin, String productStoreId, String shoppingListTypeId, String listName, String shoppingListId) throws GenericEntityException, GenericServiceException {
 
        if (partyId == null && userLogin != null) {
            partyId = userLogin.getString("partyId");
        }
        
        String autoSaveListId = null;
        // TODO: add sorting, just in case there are multiple...
        Map<String, Object> findMap = UtilMisc.<String, Object>toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", shoppingListTypeId, "listName", listName);
        if(UtilValidate.isNotEmpty(shoppingListId)){
        	findMap.put("shoppingListId", shoppingListId.replace("/", "")+userLogin.getString("partyId"));
        }
        
        List<GenericValue> existingLists = delegator.findByAnd("ShoppingList", findMap);
        Debug.logInfo("Finding existing auto-save shopping list with:  \nfindMap: " + findMap + "\nlists: " + existingLists, module);

        GenericValue list = null;
        if (existingLists != null && !existingLists.isEmpty()) {
            list = EntityUtil.getFirst(existingLists);
            autoSaveListId = list.getString("shoppingListId");
        }
        
         if (list == null && dispatcher != null && userLogin != null) {
           Map<String, Object> listFields = UtilMisc.<String, Object>toMap("userLogin", userLogin, "productStoreId", productStoreId, "shoppingListTypeId", shoppingListTypeId, "listName", listName);
           if(UtilValidate.isNotEmpty(shoppingListId)){
        	   listFields.put("shoppingListId", shoppingListId.replace("/", "")+userLogin.getString("partyId"));
        	   listFields.put("lastOrderedDate", UtilDateTime.toTimestamp(shoppingListId+" 00:00:00"));
           }
           
           if(UtilValidate.isNotEmpty(userLogin.getString("shoppingListId"))){
        	   if(UtilValidate.isNotEmpty(delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", userLogin.getString("shoppingListId"))))){
        		   listFields.put("parentShoppingListId", userLogin.getString("shoppingListId"));
        	   }
           }
           Map<String, Object> newListResult = dispatcher.runSync("createShoppingList", listFields);

            if (newListResult != null) {
                autoSaveListId = (String) newListResult.get("shoppingListId");
            }
        }

        return autoSaveListId;
    }
    
    public static String getShoppingListId(Map paramMap, GenericValue userLogin){
    	String autoSaveListId = null;
    	try{
    		if(UtilValidate.isNotEmpty((String)paramMap.get("storeId")) && !((String)paramMap.get("storeId")).equalsIgnoreCase("UDAILY")){
    			autoSaveListId = ShoppingListEvents.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"));
    			Debug.log("\n\n YOUMART autoSaveListId == "+autoSaveListId+"\n\n");
    		}else{
    			if(UtilValidate.isNotEmpty(paramMap.get("cartType")) && ((String)paramMap.get("cartType")).equalsIgnoreCase("coupon")){
    				autoSaveListId = ShoppingListEvents.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"));
    				Debug.log("\n\n COUPON autoSaveListId == "+autoSaveListId+"\n\n");
    			}else if(UtilValidate.isNotEmpty(paramMap.get("cartType")) && ((String)paramMap.get("cartType")).equalsIgnoreCase("product")){
					autoSaveListId = ILinksAppUtil.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"), "SLT_UDAILY_COUPON", ILinksAppUtil.UDAILY_PERSISTANT_PRD_LIST_NAME);
					Debug.log("\n\n PRODUCT autoSaveListId == "+autoSaveListId+"\n\n");
    			}else{
    				if(UtilValidate.isNotEmpty(paramMap.get("scheduledDate"))){
    					//String listId = ((String)paramMap.get("scheduledDate")).replace("/", "");
    					autoSaveListId = ILinksAppUtil.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"), "SLT_UDAILY_PRODUCT", ILinksAppUtil.UDAILY_PERSISTANT_PRD_LIST_NAME, (String)paramMap.get("scheduledDate"));
    					Debug.log("\n\n scheduledDate autoSaveListId == "+autoSaveListId+"\n\n");
    				}else{
    					autoSaveListId = ILinksAppUtil.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"), "SLT_UDAILY_PRODUCT", ILinksAppUtil.UDAILY_PERSISTANT_PRD_LIST_NAME);
    					Debug.log("\n\n autoSaveListId == "+autoSaveListId+"\n\n");
    				}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return autoSaveListId;
    }
    
    
    /**
     *  Email the password for the userLoginId specified in the request object.
     *
     * @param request The HTTPRequest object for the current request
     * @param response The HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     */
    public static boolean emailPassword(Map paramMap) {
        String defaultScreenLocation = "component://securityext/widget/EmailSecurityScreens.xml#PasswordEmail";
       
        String productStoreId = (String) paramMap.get("storeId");

        String errMsg = null;

        Map<String, String> subjectData = new HashMap<String, String>(); //FastMap.newInstance();
        subjectData.put("productStoreId", productStoreId);

        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));

        String userLoginId = (String) paramMap.get("username");
        subjectData.put("userLoginId", userLoginId);

        if ((userLoginId != null) && ("true".equals(UtilProperties.getPropertyValue("security.properties", "username.lowercase")))) {
            userLoginId = userLoginId.toLowerCase();
        }

//        if (UtilValidate.isEmpty(userLoginId)) {
//            // the password was incomplete
//            errMsg = UtilProperties.getMessage(resource, "loginevents.username_was_empty_reenter", UtilHttp.getLocale(request));
//            request.setAttribute("_ERROR_MESSAGE_", errMsg);
//            return "error";
//        }

        GenericValue supposedUserLogin = null;
        String passwordToSend = null;

        try {
            supposedUserLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
           
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
           e.printStackTrace();
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
            errMsg = UtilProperties.getPropertyValue("SecurityextUiLabels", "loginevents.no_primary_email_address_set_contact_customer_service");
            Debug.logError(errMsg, passwordToSend);
        }

        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", "PRDS_PWD_RETRIEVE");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        if (productStoreEmail == null) {
            errMsg = UtilProperties.getPropertyValue("SecurityextUiLabels", "loginevents.problems_with_configuration_contact_customer_service");
            Debug.logError(errMsg, passwordToSend);
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        // set the needed variables in new context
        Map<String, Object> bodyParameters = new HashMap<String, Object>(); // FastMap.newInstance();
        bodyParameters.put("useEncryption", Boolean.valueOf(useEncryption));
        bodyParameters.put("password", UtilFormatOut.checkNull(passwordToSend));
        bodyParameters.put("locale", Locale.getDefault());
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
        
        

        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
            

            if (ModelService.RESPOND_ERROR.equals(result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                errMsg = UtilProperties.getMessage("SecurityextUiLabels", "loginevents.error_unable_email_password_contact_customer_service_errorwas", messageMap, Locale.getDefault());
                Debug.logError(errMsg, passwordToSend);
                //return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logWarning(e, "", module);
            errMsg = UtilProperties.getMessage("SecurityextUiLabels", "loginevents.error_unable_email_password_contact_customer_service", Locale.getDefault());
            Debug.logError(errMsg, passwordToSend);
            //return "error";
        }

        // don't save password until after it has been sent
        if (useEncryption) {
            try {
                supposedUserLogin.store();
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "", module);
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.toString());
                errMsg = UtilProperties.getMessage(resource, "loginevents.error_saving_new_password_email_not_correct_password", messageMap, Locale.getDefault());
                Debug.logError(errMsg, passwordToSend);
                //return "error";
            }
        }

        if (useEncryption) {
            errMsg = UtilProperties.getMessage("SecurityextUiLabels", "loginevents.new_password_createdandsent_check_email", Locale.getDefault());
            Debug.logError(errMsg, passwordToSend);
        } else {
            errMsg = UtilProperties.getMessage("SecurityextUiLabels", "loginevents.new_password_sent_check_email", Locale.getDefault());
            Debug.logError(errMsg, passwordToSend);
        }
        return true;
    }
    
    
    public static EntityCondition searchCondition(String removeSelectedFlag, String showOutOfStockInSearch, String excludeOutOfStock){
    	removeSelectedFlag = removeSelectedFlag.trim();
		
		String fieldValueLike = removeSelectedFlag+ "%";
        String fieldValue = removeSelectedFlag;
        
		List cond = new ArrayList();
		
		List condList =	new ArrayList();
		
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER(fieldValueLike.toUpperCase())));
		
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		condList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER("%"+fieldValue.toUpperCase()+"%")));
		
		/*condList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("isVirtual",EntityOperator.EQUALS,"Y"),
												EntityOperator.OR,
												EntityCondition.makeCondition(
													EntityCondition.makeCondition("isVirtual",EntityOperator.EQUALS,"N"),
													EntityOperator.AND,
													EntityCondition.makeCondition("isVariant",EntityOperator.EQUALS,"N"))
				
				
				));*/
		
		if(UtilValidate.isNotEmpty(removeSelectedFlag)){
   		  String []searchStrings = removeSelectedFlag.split(" ");
		  List conditionList = null;
		  List newConditionList = new ArrayList();
		  if(UtilValidate.isNotEmpty(searchStrings))
		  for(String searchString : searchStrings)
		  {
			  if(UtilValidate.isNotEmpty(searchString))
			  {
				  conditionList = new ArrayList();
				  searchString = "%"+searchString.trim() +"%";
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("longDescription"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ingredients"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("nutritionalFacts"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productKeywords"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"),EntityOperator.LIKE,EntityFunction.UPPER(searchString.toUpperCase())));
				  
				  newConditionList.add(EntityCondition.makeCondition(conditionList, EntityOperator.OR));
			  }
		  }
		  condList.add(EntityCondition.makeCondition(newConditionList, EntityOperator.AND));
  	  	}
		
		List fromDateCond = new ArrayList();
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
    	List thruDateCond = new ArrayList();
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(condList,EntityOperator.OR));
		//cond.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
		//cond.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
		
		List salesDiscontinuationDateCond = new ArrayList();
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
		cond.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
		
		cond.add(EntityCondition.makeCondition("isVirtual",EntityOperator.NOT_EQUAL,"Y"));
		
		if("Y".equals(excludeOutOfStock) || !"Y".equalsIgnoreCase(showOutOfStockInSearch))
			cond.add(EntityCondition.makeCondition("inventoryAtp",EntityOperator.GREATER_THAN_EQUAL_TO,BigDecimal.ONE));
		
		EntityCondition condition = EntityCondition.makeCondition(cond,EntityOperator.AND);
		
		return condition;
    }
	
	public static List<Map<String,Object>> getAddress(String partyId){
		List list = new ArrayList();
		try{
			EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),EntityOperator.AND, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "SHIPPING_LOCATION"));
			list = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), UtilMisc.toSet("contactMechId"), null, readonly, true);
			if(UtilValidate.isNotEmpty(list)){
				entityCondition = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "contactMechId", true));
				Set fields = UtilMisc.toSet("toName", "attnName", "address1", "address2", "city", "state", "country", "postalCode");
				fields.add("contactNumber");
				fields.add("contactMechId");
				fields.add("flatNo");
				fields.add("area");
				list = delegator.findList("PostalAddress", entityCondition, fields, null, readonly, true);
				
//				entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),EntityOperator.AND, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_MOBILE"));
//				result = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), UtilMisc.toSet("contactMechId"), null, readonly, true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	public static Map updateAddress(Map paramMap){
		Map result = new HashMap();
		try{
			if(UtilValidate.isNotEmpty(paramMap)){
				String partyId = (String)paramMap.get("partyId");
				paramMap.remove("username");
				paramMap.remove("type");
				if(UtilValidate.isNotEmpty(paramMap.get("contactMechId"))){
					paramMap.remove("partyId");
					delegator.store(delegator.makeValue("PostalAddress", paramMap));
					result.put("contactMechId",paramMap.get("contactMechId"));
				}else{
					
					result.put("contactMechId",createContactAddress(paramMap));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static String createContactAddress(Map paramMap){
		String contactMechId = null;
		String partyId = (String)paramMap.get("partyId");
		try{
			paramMap.remove("partyId");
			LinkedList toBeStored = new LinkedList();
			contactMechId = delegator.getNextSeqId("ContactMech");
			Map fieldValues = new HashMap();
			fieldValues.put("contactMechId",contactMechId);
			fieldValues.put("contactMechTypeId","POSTAL_ADDRESS");
			GenericValue entityGV = delegator.makeValue("ContactMech", fieldValues);
			toBeStored.add(entityGV);
			//add purpuse and assign to party
			fieldValues.clear();
			fieldValues.put("contactMechId",contactMechId);
			fieldValues.put("partyId",partyId);
			fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
			entityGV = delegator.makeValue("PartyContactMech", fieldValues);
			toBeStored.add(entityGV);
	
			fieldValues.put("contactMechPurposeTypeId","SHIPPING_LOCATION");
			entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
			toBeStored.add(entityGV);
			paramMap.put("contactMechId", contactMechId);
			entityGV = delegator.makeValue("PostalAddress", paramMap);
			toBeStored.add(entityGV);
			delegator.storeAll(toBeStored);
		}catch(Exception e){
			e.printStackTrace();
		}
		return contactMechId;
	}
	
	//Update order Fields
	public static String setOrderFields(String orderId, Map orderFields){
		if(UtilValidate.isEmpty(orderId)){
			return "error";
		}
		GenericValue orderHeader = null;
		try{
			orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
		}catch(GenericEntityException e){
			e.printStackTrace();
		}
		if(UtilValidate.isNotEmpty(orderHeader)) {
			Set set = orderFields.entrySet();
			Iterator i = set.iterator();
			while(i.hasNext()) {
				Map.Entry entry = (Map.Entry)i.next();
				orderHeader.set(entry.getKey()+"", entry.getValue());
			}
			System.out.println("\n-----------------------------------------------------------\n orderHeader === "+orderHeader+"\n-----------------------------------------------------------\n");
			try{
				orderHeader.store();
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
		}
		return "success";
	}
	
	//Create customer method
	public static Map storeParty(Map paramMap){
		GenericDelegator delegator =(GenericDelegator) DelegatorFactory.getDelegator("default");
		Debug.logInfo("paramMap == " + paramMap, module);
		//store data in temporaray table 
		String partyId = null;
		String CustomerId = (String) paramMap.get("CustomerId");
		Map response = new HashMap();
		List toBeStored = new LinkedList();
		List errorList = new ArrayList();
		
		if(UtilValidate.isEmpty(CustomerId)){
			return returnError(response, UtilMisc.toList("CustomerId is missing"));
		}
		GenericValue party = null;
		try{
			party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", CustomerId));
		}catch(GenericEntityException e){
			return returnError(response, UtilMisc.toList(e.getMessage()));
		}
		
		if(UtilValidate.isNotEmpty(party)){
			return returnError(response, UtilMisc.toList("Customer already exists, CustomerId = "+CustomerId));
		}
		
		try{
			partyId = CustomerId;
			Debug.logInfo("partyId == " + partyId, module);
			Map fieldValues = new HashMap();
			fieldValues.put("partyId",partyId);
			fieldValues.put("partyTypeId","PERSON");
			fieldValues.put("statusId","PARTY_ENABLED");
			GenericValue entityGV = delegator.makeValue("Party", fieldValues);
			toBeStored.add(entityGV);
			
			//Add the party to the class
			fieldValues = new HashMap();
			fieldValues.put("partyId",partyId);
			fieldValues.put("partyClassificationGroupId","MAGENTO_CUSTOMERS");
			fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
			toBeStored.add(delegator.makeValue("PartyClassification", fieldValues));
			
			//create person
			if(UtilValidate.isNotEmpty(paramMap.get("CustomerName"))){
				fieldValues = new HashMap();
				fieldValues.put("partyId", partyId+"");
				fieldValues.put("firstName", paramMap.get("CustomerName"));
				entityGV = delegator.makeValue("Person", fieldValues);
				toBeStored.add(entityGV);
			}else{
    			return returnError(response, UtilMisc.toList("CustomerName is missing"));
			}
			
			if(UtilValidate.isNotEmpty(CustomerId)){
				fieldValues = new HashMap();
				fieldValues.put("userLoginId",partyId);
				fieldValues.put("currentPassword",null);
				fieldValues.put("partyId",partyId);
				fieldValues.put("enabled","Y");
				entityGV = delegator.makeValue("UserLogin", fieldValues);
				toBeStored.add(entityGV);
			}else{
    			return returnError(response, UtilMisc.toList("CustomerId is missing"));
			}
			
			//add partyRole as CUSTOMER
			for(int i = 0; i < roles.length; i++){
				fieldValues = new HashMap();
				fieldValues.put("partyId",partyId);
				fieldValues.put("roleTypeId",roles[i]);
				entityGV= delegator.makeValue("PartyRole", fieldValues);
				toBeStored.add(entityGV);
			}

			//email id
			if(UtilValidate.isNotEmpty(paramMap.get("CustomerEmail"))){
				String contactMechId = delegator.getNextSeqId("ContactMech");
				fieldValues = new HashMap();
				fieldValues.put("contactMechId",contactMechId+"");
				fieldValues.put("contactMechTypeId","EMAIL_ADDRESS");
				fieldValues.put("infoString",paramMap.get("CustomerEmail"));
				entityGV = delegator.makeValue("ContactMech", fieldValues);
				toBeStored.add(entityGV);
				//add purpuse and assign to party
				fieldValues = new HashMap();
				fieldValues.put("contactMechId",contactMechId+"");
				fieldValues.put("partyId",partyId+"");
				fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
				entityGV = delegator.makeValue("PartyContactMech", fieldValues);
				toBeStored.add(entityGV);
	
				fieldValues.put("contactMechPurposeTypeId","PRIMARY_EMAIL");
				entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
				toBeStored.add(entityGV);
			}else{
    			return returnError(response, UtilMisc.toList("CustomerEmail is missing"));
			}
			
			//billing address and phone number		
			if(UtilValidate.isNotEmpty(paramMap.get("BillingAddress"))){
				createBillingAddress(paramMap, toBeStored, partyId);
			}else{
    			return returnError(response, UtilMisc.toList("BillingAddress is missing"));
			}
			
			//shippping info
			if(UtilValidate.isNotEmpty(paramMap.get("ShippingAddress"))){
				createShippingAddress(paramMap, toBeStored, partyId);
			}else{
    			return returnError(response, UtilMisc.toList("ShippingAddress is missing"));
			}
			
			//add attributes
			if(UtilValidate.isNotEmpty(paramMap.get("Attributes"))){
				Map<String, String> attrMap = (Map) paramMap.get("Attributes");
				for (Map.Entry<String, String> entry : attrMap.entrySet()) {
				    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				    fieldValues = new HashMap();
					fieldValues.put("partyId", partyId+"");
					fieldValues.put("attrName", entry.getKey());
					fieldValues.put("attrValue", entry.getValue());
					entityGV = delegator.makeValue("PartyAttribute", fieldValues);
					toBeStored.add(entityGV);
				}
			}	
			
			// store the changes
            if(toBeStored.size() > 0) {
                try {
                	TransactionUtil.begin();
                    delegator.storeAll(toBeStored);
                    TransactionUtil.commit();
                } catch(GenericEntityException e) {
                	TransactionUtil.rollback();
        			return returnError(response, UtilMisc.toList(e.getMessage()));
                }catch (Exception e) {
                    TransactionUtil.rollback();
        			return returnError(response, UtilMisc.toList(e.getMessage()));
                }
            }

		}catch(Exception pce) {
			pce.printStackTrace();
			response.put("partyId",partyId);
			return returnError(response, UtilMisc.toList(pce.getMessage()));
		}
		//done
		response.put("partyId",partyId);
		return returnSuccess(response, UtilMisc.toList("Customer Data created successfully"));
	}

	//Update customer details
	public static Map updateParty(Map paramMap){
		Debug.logInfo("paramMap == " + paramMap, module);
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		//store data in temporaray table 
		String partyId = null;
		String CustomerId = (String) paramMap.get("CustomerId");
		Map response = new HashMap();
		List toBeStored = new LinkedList();
		List errorList = new ArrayList();
		
		if(UtilValidate.isEmpty(CustomerId)){
			return returnError(response, UtilMisc.toList("CustomerId is missing"));
		}
		GenericValue party = null;
		try{
			party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", CustomerId));
		}catch(GenericEntityException e){
			return returnError(response, UtilMisc.toList(e.getMessage()));
		}
		
		if(UtilValidate.isEmpty(party)){
			return returnError(response, UtilMisc.toList("Customer doesn't exists, CustomerId = "+CustomerId));
		}
		
		try{
			partyId = CustomerId;
			Debug.logInfo("partyId == " + partyId, module);
			Map fieldValues = new HashMap();
			GenericValue entityGV = null;
			
			//update person
			if(UtilValidate.isNotEmpty(paramMap.get("CustomerName"))){
				entityGV = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
				if(entityGV != null){					
					entityGV.put("firstName", paramMap.get("CustomerName"));
					delegator.store(entityGV);
				}
			}
			
			//update email id
			if(UtilValidate.isNotEmpty(paramMap.get("CustomerEmail"))){
				List entityGVList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechPurposeTypeId","PRIMARY_EMAIL") );
				if(entityGVList != null && entityGVList.size()>0){
					entityGV = (GenericValue)entityGVList.get(0);
					if(entityGV != null){
						entityGV = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", entityGV.get("contactMechId")));
						entityGV.put("infoString",paramMap.get("CustomerEmail"));
						delegator.store(entityGV);
					}
				}
			}
			
			//update billing address and phone number		
			if(UtilValidate.isNotEmpty(paramMap.get("BillingAddress"))){
				updateBillingAddress(paramMap, partyId);
			}
			
			//shippping info
			if(UtilValidate.isNotEmpty(paramMap.get("ShippingAddress"))){
				updateShippingAddress(paramMap, partyId);
			}
			//add attributes
			if(UtilValidate.isNotEmpty(paramMap.get("Attributes"))){
				Map<String, String> attrMap = (Map) paramMap.get("Attributes");
				for (Map.Entry<String, String> entry : attrMap.entrySet()) {
				    Debug.logInfo("Key = " + entry.getKey() + ", Value = " + entry.getValue(), module);
					List entityAttrGVList = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId", partyId,"attrName",entry.getKey()) );
					if(UtilValidate.isNotEmpty(entityAttrGVList) && UtilValidate.isNotEmpty(entityAttrGVList.get(0))){
						entityGV = (GenericValue) entityAttrGVList.get(0);
						entityGV.put("attrValue",entry.getValue());
						delegator.store(entityGV);
					}				 
				}
			}	
		}catch(Exception pce) {
			pce.printStackTrace();
			response.put("partyId",partyId);
			return returnError(response, UtilMisc.toList(pce.getMessage()));
		}
		//done
		response.put("partyId",partyId);
		return returnSuccess(response, UtilMisc.toList("Customer Data updated successfully"));
	}

	private static GenericValue getGeoInfo(String geoName){
		GenericValue geoInfo = null;
		try{
			List cond = new ArrayList();
			EntityConditionList isConditionList = null;
			if(UtilValidate.isNotEmpty(geoName)){
				geoName = geoName.trim();
				cond.add(EntityCondition.makeCondition("geoCode", EntityOperator.EQUALS, geoName.toUpperCase()));
				cond.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "COUNTRY"));
			}

			List entityList = delegator.findList("Geo", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, false);
			if(entityList.size() > 0){
				geoInfo = (GenericValue)entityList.get(0);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		Debug.logInfo("geoInfo == " + geoInfo.get("geoId"), module);		
		return geoInfo;
	}

	private static String getSupplierId(String supplier){
		
		GenericValue personGV = null;
		try{
			GenericDelegator delegator =(GenericDelegator) DelegatorFactory.getDelegator("default");
			List isCondition = new ArrayList();
		
			EntityConditionList isConditionList = null;

			if(supplier != null && !"".equalsIgnoreCase(supplier.trim())){
				supplier = supplier.trim();
				isCondition.add(EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, supplier.toLowerCase()));
				isCondition.add(EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, supplier.toUpperCase()));
				isCondition.add(EntityCondition.makeCondition("firstName", EntityOperator.EQUALS, supplier));
			}

			List entityList = delegator.findList("Person", EntityCondition.makeCondition(isCondition, EntityOperator.OR), null, null, null, false);
			if(entityList.size()>0){ personGV =(GenericValue) entityList.get(0);}

			//create new
			if(personGV == null){
				String partyId = delegator.getNextSeqId("Party");
				System.out.println("partyId"+partyId);
				Map fieldValues = new HashMap();
				fieldValues.put("partyId",partyId);
				fieldValues.put("partyTypeId","PERSON");
				fieldValues.put("statusId","PARTY_ENABLED");
				personGV = delegator.makeValue("Party", fieldValues);
				delegator.create(personGV);
	
				//create person
				fieldValues = new HashMap();
				fieldValues.put("partyId", partyId+"");
				fieldValues.put("firstName", supplier);
				GenericValue entityGV = delegator.makeValue("Person", fieldValues);
				delegator.create(entityGV);
	
				//add partyRole as CUSTOMER
				fieldValues = new HashMap();
				fieldValues.put("partyId", partyId);
				fieldValues.put("roleTypeId", "SUPPLIER");
				entityGV = delegator.makeValue("PartyRole", fieldValues);
				delegator.create(entityGV);
			}

		}catch(Exception pce) {

		}
		 return personGV.getString("partyId");
	}
	
	private static Map createBillingAddress(Map paramMap, List toBeStored, String partyId){
		Map response = new HashMap();
		Map fieldValues = null;
		GenericValue entityGV = null;
		
		Map billingAddress = (HashMap)paramMap.get("BillingAddress");
		String paContactMechId = delegator.getNextSeqId("ContactMech");
		fieldValues = new HashMap();
		fieldValues.put("contactMechId",paContactMechId+"");
		fieldValues.put("contactMechTypeId","POSTAL_ADDRESS");
		entityGV = delegator.makeValue("ContactMech", fieldValues);
		toBeStored.add(entityGV);
		
		fieldValues = new HashMap();
		fieldValues.put("contactMechId",paContactMechId+"");
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingFirstname"))){
			fieldValues.put("toName", billingAddress.get("BillingFirstname"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingFirstname is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingLastname"))){
			fieldValues.put("attnName", billingAddress.get("BillingLastname"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingLastname is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingStreet_1"))){
			fieldValues.put("address1", billingAddress.get("BillingStreet_1"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingStreet_1 is missing"));
			return response;
		}
		fieldValues.put("address2", billingAddress.get("BillingStreet_2"));
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingCity"))){
			fieldValues.put("city", billingAddress.get("BillingCity"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingCity is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingPostcode"))){
			fieldValues.put("postalCode", billingAddress.get("BillingPostcode"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingPostcode is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingContact"))){
			fieldValues.put("contactNumber",  billingAddress.get("BillingContact"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingContact is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingRegion"))){
			fieldValues.put("stateProvinceGeoId", ((String)billingAddress.get("BillingRegion")).trim());
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingRegion is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingCountry"))){
			GenericValue geoInfo = (GenericValue) getGeoInfo((String)billingAddress.get("BillingCountry"));
			if(UtilValidate.isNotEmpty(geoInfo)){
				fieldValues.put("countryGeoId", geoInfo.get("geoId"));
			}
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("BillingCountry is missing"));
			return response;
		}
		entityGV = delegator.makeValue("PostalAddress", fieldValues);
		toBeStored.add(entityGV);
	
		//add purpuse and assign to party
		List<GenericValue> contactMechs = null;
		try{
			contactMechs = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
		}catch(GenericEntityException e){
			e.printStackTrace();
		}
		Debug.logInfo("contactMechs -------------------------  "+contactMechs.size(), module);
		if(UtilValidate.isNotEmpty(contactMechs)){
			updateBillingAddress(paramMap, partyId);
		}else{
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", paContactMechId+"");
			fieldValues.put("partyId", partyId+"");
			fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
			entityGV = delegator.makeValue("PartyContactMech", fieldValues);
			toBeStored.add(entityGV);

			fieldValues.put("contactMechPurposeTypeId","BILLING_LOCATION");
			entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
			toBeStored.add(entityGV);
		}
		
		//Billing_contact
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingContact"))){
			String contactMechId = delegator.getNextSeqId("ContactMech");
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", contactMechId+"");
			fieldValues.put("contactMechTypeId", "TELECOM_NUMBER");
			entityGV = delegator.makeValue("ContactMech", fieldValues);
			toBeStored.add(entityGV);
			
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", contactMechId+"");
			fieldValues.put("contactNumber", billingAddress.get("BillingContact"));
			
			entityGV = delegator.makeValue("TelecomNumber", fieldValues);
			toBeStored.add(entityGV);
			
		
			//add purpuse and assign to party
			List<GenericValue> contactMechTele = null;
			try{
				contactMechTele = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_BILLING"));
			}catch(GenericEntityException e){
				e.printStackTrace();
			}
			Debug.logInfo("contactMechTele11 -------------------------  "+contactMechTele.size(), module);
			if(UtilValidate.isEmpty(contactMechTele)){
				fieldValues = new HashMap();
				fieldValues.put("contactMechId", contactMechId+"");
				fieldValues.put("partyId", partyId+"");
				fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
				entityGV = delegator.makeValue("PartyContactMech", fieldValues);
				toBeStored.add(entityGV);
	
				fieldValues.put("contactMechPurposeTypeId","PHONE_BILLING");
				entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
				toBeStored.add(entityGV);
			}
		}
		
		return UtilMisc.toMap("contactMechId", paContactMechId);
	}

	private static Map updateBillingAddress(Map paramMap, String partyId){
	   Map response = new HashMap();
	   Map billingAddress = (HashMap)paramMap.get("BillingAddress");
	   GenericValue entityGV = null;	
	   try{
		   List entityGVList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechPurposeTypeId","BILLING_LOCATION") );
		   if(UtilValidate.isNotEmpty(entityGVList)){
				entityGV = EntityUtil.getFirst(entityGVList);
				if(UtilValidate.isNotEmpty(entityGV)){
					entityGV = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", entityGV.get("contactMechId")));
					delegator.store(getBillingGenericValue(billingAddress, entityGV));
				}
			}		
		
			//Billing_contact
			if(UtilValidate.isNotEmpty(billingAddress.get("BillingContact"))){
				entityGVList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechPurposeTypeId","PHONE_BILLING") );
				if(UtilValidate.isNotEmpty(entityGVList)){
					entityGV = (GenericValue)entityGVList.get(0);
					if(UtilValidate.isNotEmpty(entityGV)){
						entityGV = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", entityGV.get("contactMechId")));
						if(UtilValidate.isNotEmpty(billingAddress.get("BillingContact"))){
							entityGV.put("contactNumber", billingAddress.get("BillingContact"));
						}
						delegator.store(entityGV);
					}
				}		
			}
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return response;
	}
	
	private static GenericValue getBillingGenericValue(Map billingAddress, GenericValue entityGV){
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingFirstname"))){
			entityGV.put("toName", billingAddress.get("BillingFirstname"));
		}

		if(UtilValidate.isNotEmpty(billingAddress.get("BillingLastname"))){
			entityGV.put("attnName", billingAddress.get("BillingLastname"));
		}

		if(UtilValidate.isNotEmpty(billingAddress.get("BillingStreet_1"))){
			entityGV.put("address1", billingAddress.get("BillingStreet_1"));
		}

		if(UtilValidate.isNotEmpty(billingAddress.get("BillingStreet_2"))){
			entityGV.put("address2", billingAddress.get("BillingStreet_2"));
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingCity"))){
			entityGV.put("city", billingAddress.get("BillingCity"));
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingPostcode"))){
			entityGV.put("postalCode", billingAddress.get("BillingPostcode"));
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingContact"))){
			entityGV.put("contactNumber",  billingAddress.get("BillingContact"));
		}

		if(UtilValidate.isNotEmpty(billingAddress.get("BillingRegion"))){
			entityGV.put("stateProvinceGeoId", ((String)billingAddress.get("BillingRegion")).trim());
		}
		
		if(UtilValidate.isNotEmpty(billingAddress.get("BillingCountry"))){
			GenericValue geoInfo = (GenericValue) getGeoInfo((String)billingAddress.get("BillingCountry"));
			if(UtilValidate.isNotEmpty(geoInfo)){
				entityGV.put("countryGeoId", geoInfo.get("geoId"));
			}
		}
		return entityGV;
	}

	//Create shipping address method
	private static Map createShippingAddress(Map paramMap, List toBeStored, String partyId){
		Map response = new HashMap();
		Map fieldValues = null;
		GenericValue entityGV = null;
		
		Map shippingAddress = (HashMap) paramMap.get("ShippingAddress");
		
		String paContactMechId = delegator.getNextSeqId("ContactMech");
		fieldValues = new HashMap();
		fieldValues.put("contactMechId", paContactMechId+"");
		fieldValues.put("contactMechTypeId", "POSTAL_ADDRESS");
		entityGV = delegator.makeValue("ContactMech", fieldValues);
		toBeStored.add(entityGV);
		
		fieldValues = new HashMap();
		fieldValues.put("contactMechId", paContactMechId+"");
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingFirstname"))){
			fieldValues.put("toName", shippingAddress.get("ShippingFirstname"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingFirstname is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingLastname"))){
			fieldValues.put("attnName", shippingAddress.get("ShippingLastname"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingLastname is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingStreet_1"))){
			fieldValues.put("address1", shippingAddress.get("ShippingStreet_1"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingStreet_1 is missing"));
			return response;
		}
		fieldValues.put("address2", shippingAddress.get("ShippingStreet_2"));
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingCity"))){
			fieldValues.put("city", shippingAddress.get("ShippingCity"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingCity is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingPostcode"))){
			fieldValues.put("postalCode", shippingAddress.get("ShippingPostcode"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingCity is missing"));
			return response;
		}
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingContact"))){
			fieldValues.put("contactNumber", shippingAddress.get("ShippingContact"));
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingContact is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingRegion"))){
			fieldValues.put("stateProvinceGeoId", ((String)shippingAddress.get("ShippingRegion")).trim());
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingRegion is missing"));
			return response;
		}
		
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingCountry"))){
			GenericValue geoInfo =(GenericValue)getGeoInfo((String)shippingAddress.get("ShippingCountry"));
			if(UtilValidate.isNotEmpty(geoInfo)){
				fieldValues.put("countryGeoId", geoInfo.get("geoId"));
			}
		}else{
			response.put("status","ERROR");
			response.put("msg",UtilMisc.toList("ShippingCountry is missing"));
			return response;
		}
	
		entityGV = delegator.makeValue("PostalAddress", fieldValues);
		toBeStored.add(entityGV);
	
		//add purpose and assign to party
		
		List<GenericValue> contactMechs = null;
		try{
			contactMechs = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "SHIPPING_LOCATION"));
		}catch(GenericEntityException e){
			e.printStackTrace();
		}
		Debug.logInfo("contactMechs shipping -------------------------  "+contactMechs.size(), module);
		if(UtilValidate.isNotEmpty(paramMap)){
			updateShippingAddress(paramMap, partyId);
		}else{
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", paContactMechId+"");
			fieldValues.put("partyId", partyId+"");
			fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
			entityGV = delegator.makeValue("PartyContactMech", fieldValues);
			toBeStored.add(entityGV);
	
			fieldValues.put("contactMechPurposeTypeId","SHIPPING_LOCATION");
			entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
			toBeStored.add(entityGV);
		}
		//ShippingContact
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingContact"))){
			String contactMechId = delegator.getNextSeqId("ContactMech");
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", contactMechId+"");
			fieldValues.put("contactMechTypeId", "TELECOM_NUMBER");
			entityGV = delegator.makeValue("ContactMech", fieldValues);
			toBeStored.add(entityGV);
			
			fieldValues = new HashMap();
			fieldValues.put("contactMechId", contactMechId+"");
			fieldValues.put("contactNumber", shippingAddress.get("ShippingContact"));
			
			entityGV = delegator.makeValue("TelecomNumber", fieldValues);
			toBeStored.add(entityGV);
		
			//add purpose and assign to party
			List<GenericValue> contactMechTele = null;
			try{
				contactMechTele = delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("contactMechPurposeTypeId", "PHONE_SHIPPING"));
			}catch(GenericEntityException e){
				e.printStackTrace();
			}
			Debug.logInfo("contactMechs shipping -------------------------  "+contactMechTele.size(), module);
			if(UtilValidate.isEmpty(contactMechTele)){
				fieldValues = new HashMap();
				fieldValues.put("contactMechId", contactMechId+"");
				fieldValues.put("partyId", partyId+"");
				fieldValues.put("fromDate",UtilDateTime.nowTimestamp());
				entityGV = delegator.makeValue("PartyContactMech", fieldValues);
				toBeStored.add(entityGV);
	
				fieldValues.put("contactMechPurposeTypeId","PHONE_SHIPPING");
				entityGV = delegator.makeValue("PartyContactMechPurpose", fieldValues);
				toBeStored.add(entityGV);
			}
		}
		
		return UtilMisc.toMap("contactMechId", paContactMechId);
	}

	private static Map updateShippingAddress(Map paramMap, String partyId){
	   Map response = new HashMap();
	   Map shippingAddress = (HashMap) paramMap.get("ShippingAddress");
	   GenericValue entityGV = null;	
	   try{
		   List<GenericValue> entityGVList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechPurposeTypeId","SHIPPING_LOCATION") );
		   
		   if(UtilValidate.isNotEmpty(entityGVList)){
			   entityGV = (GenericValue)entityGVList.get(0);
			   if(UtilValidate.isNotEmpty(entityGV)){
				   entityGV = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", entityGV.get("contactMechId")));
				   delegator.store(getShippingGenericValue(shippingAddress, entityGV));
				}
			}
		
		   //Billing_contact
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingContact"))){
			   entityGVList = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId,"contactMechPurposeTypeId","PHONE_SHIPPING") );
			   if(UtilValidate.isNotEmpty(entityGVList)){
				   entityGV = (GenericValue)entityGVList.get(0);
				   if(UtilValidate.isNotEmpty(entityGV)){
					   entityGV = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", entityGV.get("contactMechId")));
					   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingContact"))){
						   entityGV.put("contactNumber", shippingAddress.get("ShippingContact"));
					   }
					   delegator.store(entityGV);
				   }
			   }		
		   }
	   }
	   catch(Exception e) {
		   e.printStackTrace();
	   }
	   return response;
	}
	
	private static GenericValue getShippingGenericValue(Map shippingAddress, GenericValue entityGv){
		if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingFirstname"))){
			entityGv.put("toName", shippingAddress.get("ShippingFirstname"));
		   }

		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingLastname"))){
			   entityGv.put("attnName", shippingAddress.get("ShippingLastname"));
		   }

		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingStreet_1"))){
			   entityGv.put("address1", shippingAddress.get("ShippingStreet_1"));
		   }
		   
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingStreet_2"))){
			   entityGv.put("address2", shippingAddress.get("ShippingStreet_2"));
		   }
			
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingCity"))){
			   entityGv.put("city", shippingAddress.get("ShippingCity"));
		   }
			
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingPostcode"))){
			   entityGv.put("postalCode", shippingAddress.get("ShippingPostcode"));
		   }

		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingContact"))){
			   entityGv.put("contactNumber", shippingAddress.get("ShippingContact"));
		   }
			
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingRegion"))){
			   entityGv.put("stateProvinceGeoId", ((String)shippingAddress.get("ShippingRegion")).trim());
		   }
			
		   if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingCountry"))){
			   GenericValue geoInfo =(GenericValue)getGeoInfo((String)shippingAddress.get("ShippingCountry"));
			   if(UtilValidate.isNotEmpty(geoInfo)){
				   entityGv.put("countryGeoId", geoInfo.get("geoId"));
			   }
		   }
		   
		   return entityGv;
	}
	
	public static Map chooseShippingMethod(Map paramMap){
		Map response = new HashMap();
		try{
			String aramexShipmentMethodTypeId = UtilProperties.getPropertyValue("magento.properties", "opentaps.aramex.shipmentMethodTypeId", "ARAMEX");
			String blueDartShipmentMethodTypeId = UtilProperties.getPropertyValue("magento.properties", "opentaps.bluedart.shipmentMethodTypeId", "BLUE_DART");
			String carrierPartyId = UtilProperties.getPropertyValue("magento.properties", "opentaps.default.carrierPartyId", "DemoCarrier");
		   
			Map shippingAddress = (HashMap) paramMap.get("ShippingAddress");
			String paymentMeth = (String) paramMap.get("PaymentMethod");
			String postalCode= "";
		   
			if(UtilValidate.isNotEmpty(shippingAddress.get("ShippingPostcode"))){
				postalCode = (String)shippingAddress.get("ShippingPostcode");
			}
			
			//check condition to check for ARAMEX , BLUE_DART
			boolean shipUsingBlueDart = false;
		   
			if(UtilValidate.isNotEmpty(postalCode)){
				shipUsingBlueDart = checkAvailableBlueDart(paymentMeth, postalCode);
			}
		   
			if(shipUsingBlueDart){
				response.put("shipmentMethodTypeId", blueDartShipmentMethodTypeId);
				response.put("carrierPartyId", carrierPartyId);
			}else{
				response.put("shipmentMethodTypeId", aramexShipmentMethodTypeId);
				response.put("carrierPartyId", carrierPartyId);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}	
	
	public static String calculateRateShippingMethod(String orderId,String shipmentMethodTypeId){
		String response ="";
		try{
			String carrierPartyId = null;
			//check condition to check for ARAMEX , BLUE_DART
		   String shipmentMethodTypeId_Ar = UtilProperties.getPropertyValue("magento.properties", "opentaps.aramex.shipmentMethodTypeId", "ARAMEX");
		   String shipmentMethodTypeId_BD = UtilProperties.getPropertyValue("magento.properties", "opentaps.bluedart.shipmentMethodTypeId", "BLUE_DART");
		   String carrierPartyIdPro = UtilProperties.getPropertyValue("magento.properties", "opentaps.default.carrierPartyId", "DemoCarrier");
	
		   if(shipmentMethodTypeId_BD.equalsIgnoreCase(shipmentMethodTypeId)){
			 //selected one is   BLUE_DART
			   //check for availablty
			   response="Available";
		   }
		   
		   if(shipmentMethodTypeId_Ar.equalsIgnoreCase(shipmentMethodTypeId)){
			   //selected one is   ARAMEX
			   response="Not Available";
		   }
		}catch(Exception e){
		   e.printStackTrace();
	   }
	   return response;
	}
	
	public static boolean checkAvailableBlueDart(String paymentMeth, String postalCode){
		List<GenericValue> availableList = new ArrayList();  
		postalCode = postalCode.trim();
		paymentMeth = paymentMeth.trim();
		
		Debug.logInfo("checkAvailableBlueDart = ", module);
		Debug.logInfo("paymentMeth = "+paymentMeth, module);
		Debug.logInfo("postalCode = "+postalCode, module);
		Debug.logInfo("checkAvailableBlueDart = ", module);
		
		try{
			if(paymentMeth.equalsIgnoreCase("Cash")){
				availableList = delegator.findByAnd("PostPaidPinCode", UtilMisc.toMap("cpincode", postalCode));
			}else{
				availableList = delegator.findByAnd("PrePaidPinCode", UtilMisc.toMap("cpincode", postalCode));
			}
		}catch(Exception e){
			e.printStackTrace();   
		}
		
		Debug.logInfo("checkAvailableBlueDart = ", module);
		Debug.logInfo("availableList = "+availableList, module);
		Debug.logInfo("availableList.size() = "+availableList.size(), module);
		Debug.logInfo("checkAvailableBlueDart = ", module);
		   
		if(UtilValidate.isNotEmpty(availableList) && (availableList.size() > 0)){
			return true;
		}else{
			return false;
		}
	}	
	
	public static Map getShippingPrintDetail(String shipmentId){
		Map response = new HashMap();
		Debug.logInfo(" ==================================== ", module);
		Debug.logInfo(" getShippingPrintDetail shipmentId = "+shipmentId, module);
		Debug.logInfo(" ==================================== ", module);
		try{
			GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));   
			String shipOrderId = "";
        	// retrieve orderId associated with the shipment
            shipOrderId = shipment.getString("primaryOrderId");
            
		    List orderBilling = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", shipOrderId));
            String  invoiceId = null;
            if(orderBilling.size() >0){
            	GenericValue orderBillingGv = (GenericValue)orderBilling.get(0);
            	invoiceId = orderBillingGv.getString("invoiceId");
            }
                         	
            
            List orderItemShipGrp = delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", shipOrderId));
            String shipmentMethodTypeId =null;
            if(orderItemShipGrp.size() >0){
            	GenericValue orderItemShipGrpGv = (GenericValue)orderItemShipGrp.get(0);
            	shipmentMethodTypeId = orderItemShipGrpGv.getString("shipmentMethodTypeId");
            }
            
            List shipmentRoutes = delegator.findByAnd("ShipmentPackageRouteSeg",UtilMisc.toMap("shipmentId",shipmentId,"shipmentPackageSeqId", "00001"));
            String trackingCode =null;
            if(shipmentRoutes.size() >0){
            	GenericValue shipmentRoutesGv = (GenericValue)shipmentRoutes.get(0);
            	trackingCode = shipmentRoutesGv.getString("trackingCode");
            }
            
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", shipOrderId));
            List<GenericValue> paymentPref = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", shipOrderId));
            if(paymentPref.size() > 0){
            	GenericValue paymentPrefGv =(GenericValue)paymentPref.get(0);
            	response.putAll(paymentPrefGv);
            }
            
            response.put("invoiceId",invoiceId);
            response.put("orderDate",orderHeader.getString("orderDate"));
            response.put("trackingCode",trackingCode);
            response.put("shipOrderId",shipOrderId);
            response.put("shipmentMethodTypeId",shipmentMethodTypeId);
            
		}catch(Exception e) {
			e.printStackTrace();
		} 	
		return response;
	}			
	
	public static Map getOrderTaxPrices(String orderId){
		Map response = new HashMap();
		Debug.logInfo(" ==================================== ", module);
		Debug.logInfo(" getShippingPrintDetail orderId = "+orderId, module);
		Debug.logInfo(" ==================================== ", module);
		try{
			 GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
			 OrderReadHelper orderReadHelper = new OrderReadHelper(orderHeader);
			 List orderItems = orderReadHelper.getOrderItems();
			 List orderAdjustments = orderReadHelper.getAdjustments();
			 BigDecimal grandTotal = orderReadHelper.getOrderGrandTotal(orderItems, orderAdjustments);
			 
			 BigDecimal grandDiscount =  orderReadHelper.getOrderAdjustmentsTotal(orderItems,orderAdjustments);
             response.put("orderId",orderId);
             
             String productStoreId = orderHeader.getString("productStoreId");
             GenericValue productStoreGv = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
             
             GenericValue shippingAddress = orderReadHelper.getShippingAddress();
             
             GenericValue  stateGeo = delegator.findByPrimaryKey("Geo", UtilMisc.toMap("geoId", shippingAddress.get("stateProvinceGeoId")));
             
         	try{
         		boolean isVatTax = true;
    			double cstTax = 0;
    			double vatTax = 0;
    			String cstDesc = "";
    			String vatDesc = "";
    			
    			if(UtilValidate.isNotEmpty(stateGeo) && !"IN-KA".equalsIgnoreCase(stateGeo.getString("geoId"))){
    				//add state level tax
    				if(UtilValidate.isNotEmpty(stateGeo.get("cstPer"))){    				
        				cstTax = stateGeo.getDouble("cstPer").doubleValue();
        				cstDesc = stateGeo.getString("cstDesc");
        				if(UtilValidate.isEmpty(cstDesc) && UtilValidate.isNotEmpty(productStoreGv)){
        					cstDesc = productStoreGv.getString("cstDesc");
        				}
        			}else if(productStoreGv != null && UtilValidate.isNotEmpty(productStoreGv.get("cstPer"))){    				
	        			cstTax = productStoreGv.getDouble("cstPer").doubleValue();
	        			cstDesc = productStoreGv.getString("cstDesc");
        			}        
    				
    				isVatTax = false;
    			}else{
    				if(productStoreGv != null && UtilValidate.isNotEmpty(productStoreGv.get("vatPer"))){    				
        				vatTax = productStoreGv.getDouble("vatPer").doubleValue();
        				vatDesc = productStoreGv.getString("vatDesc");
        			}
    			}
    			
    			response.put("isVatTax", isVatTax);
    			response.put("cstDesc", cstDesc);
    			response.put("vatDesc", vatDesc);
    			
    			//double grndTotal = 455.00;
    			double grndTotal = grandTotal.doubleValue();
    			double discount = grandDiscount.doubleValue();
    			
    			if(grndTotal < 0){
    				grndTotal = -grndTotal;
    			}
    			
//    			double subtotal1 = roundTwoDecimals(grndTotal/(100+cstTax)*100,"#.##");
//    			double subtotal2 = roundTwoDecimals(grndTotal/(100+vatTax)*100,"#.##");
    			
    			double subtotal1 = 0.0;
    			double subtotal2 = 0.0;
    			
    			double deductTax = grndTotal - subtotal1; 
    			double deductTaxVat = grndTotal - subtotal2; 
    			double subtotal = grndTotal - deductTax - deductTaxVat;
    			double deductTotalTax = (deductTax + deductTaxVat);
    			
    			//int numberofItem = item.size();
    			int numberofItem = orderItems.size();    			
    			
    			double deductPerItemPrice = 0;
    			
    			if(deductTotalTax > 0){
    				deductPerItemPrice = roundTwoDecimals((deductTotalTax/numberofItem),"#.##");
    			}
    			double addPerItemPrice = 0;
    			
    			if(discount > 0){
    				addPerItemPrice = roundTwoDecimals((discount/numberofItem),"#.##");
    			}else{
    				discount = -discount;
    			}
    			Debug.logInfo("cstTax =="+cstTax, module);
    			Debug.logInfo("vatTax =="+vatTax, module);

    			Debug.logInfo("deductPerItemPrice =="+deductPerItemPrice, module);
    			Debug.logInfo("addPerItemPrice =="+addPerItemPrice, module);
    			
    			Debug.logInfo("===========================================================", module);
    			int qtyTotal = 0;
    			for(int i = 0; i < orderItems.size(); i++){
    				GenericValue orderItem  = (GenericValue)orderItems.get(i);
    				BigDecimal itemSubotal =  orderReadHelper.getOrderItemSubTotal(orderItem);   	
    				BigDecimal itemAdjustment =  orderReadHelper.getOrderItemAdjustmentsTotal(orderItem); 
    				
    				//double itemPrice = Double.parseDouble((String)item.get(i));
    				String orderItemSeqId = orderItem.getString("orderItemSeqId");
    				double itemPrice = itemSubotal.doubleValue();
    				double itemDiscount = itemAdjustment.doubleValue();
//    				double displayItemPrice = (itemPrice - deductPerItemPrice);
    				double displayItemPrice = itemPrice;
    				
    				//double displayOrginalItemPrice = roundTwoDecimals(displayItemPrice/(100+vatTax)*100,"#.##");
    				double displayOrginalItemPrice = displayItemPrice;
    				//unit price will be added with discount
    				displayOrginalItemPrice = displayOrginalItemPrice - itemDiscount;
    				
    				double vat = (displayItemPrice - displayOrginalItemPrice);
    				if(displayOrginalItemPrice < 0){
    					displayOrginalItemPrice = -displayOrginalItemPrice;
        			}
    				if(displayItemPrice < 0){
    					displayItemPrice = -displayItemPrice;
        			}
    				Debug.logInfo("displayOrginalItemPrice =="+displayOrginalItemPrice+"===itemDiscount"+itemDiscount+"=========="+displayItemPrice, module);
    				
    				double itemTax = 0.0;roundTwoDecimals((displayOrginalItemPrice * vatTax)/100,"#.##");
    				
    				if(isVatTax){
    					itemTax = roundTwoDecimals((displayItemPrice * vatTax)/100,"#.##");
    					displayOrginalItemPrice = displayOrginalItemPrice - itemTax;
    				}else{
    					itemTax = roundTwoDecimals((displayItemPrice * cstTax)/100,"#.##");
    					displayOrginalItemPrice = displayOrginalItemPrice - itemTax;
    				}
    				
    				Map data = new HashMap();
    				data.put("itemPrice", itemPrice);    				
    				data.put("displayOrginalItemPrice", displayOrginalItemPrice);
    				data.put("itemDiscount", itemDiscount);
    				data.put("displayItemPrice", displayItemPrice);
    				data.put("vat", vat);    
    				data.put("itemTax", itemTax);  
    				
    				response.put(orderItemSeqId, data);
    				qtyTotal++;
    			}
    			
    			response.put("qtyTotal", qtyTotal);
    			Debug.logInfo("===========================================================", module);
    			double subtotalBfrDidcount = (subtotal- discount);
    			if(subtotalBfrDidcount < 0){
    				subtotalBfrDidcount = -subtotalBfrDidcount;
    			}
    			if(subtotal < 0){
    				subtotal = -subtotal;
    			}
    			Debug.logInfo("subtotal == "+subtotalBfrDidcount, module);
    			Debug.logInfo("discount == "+discount, module);	
    			Debug.logInfo("subtotal == "+(subtotal), module);
    			Debug.logInfo("deductTax CST == "+deductTax, module);
    			Debug.logInfo("deductTax VAT == "+deductTaxVat, module);
    			Debug.logInfo("grndTotal == "+grndTotal, module);
    			
    			
    			response.put("subtotalBfrDidcount", subtotalBfrDidcount);
    			response.put("discount", discount);
    			
    			response.put("subtotal", subtotal);
    			response.put("deductTax", deductTax);
    			response.put("deductTaxVat", deductTaxVat);    		
    			
    			response.put("grndTotal", grndTotal);
    			
    			response.put("vatTax", vatTax);    			
    			response.put("cstTax", cstTax);
    			
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
            
		}catch(Exception e) {
			e.printStackTrace();
		} 	
		return response;
	}		
	
	public static double roundTwoDecimals(double d,String frmt) {
    	DecimalFormat twoDForm = new DecimalFormat(frmt);
    	return Double.valueOf(twoDForm.format(d));
	}	
	
	//Replacing special chars like ',",<,>,& from the special codes
	public static String replaceSpecialChars(String str){
		if(UtilValidate.isEmpty(str)){
			return null;
		}
		str = str.replace(":singlequot:", "'").replace(":quot:", "\"").replace(":amp:", "&").replace(":lt:", "<").replace(":gt:", ">");
		
		return str;
	}
	
	//Method to assign product to category
	public static void assignToCategory(String productId, List toStore){
		if(UtilValidate.isNotEmpty(productId)){
			GenericValue productCategory = null;
			try{
				productCategory = delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", "MPSCATALOG1"));
			}catch(GenericEntityException e){
				e.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(productCategory)){
				List<GenericValue> pcm = null;
				try{
					pcm = delegator.findByAnd("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", "MPSCATALOG1"));
				}catch(GenericEntityException e){
					e.printStackTrace();
				}
				if(UtilValidate.isEmpty(pcm)){
					GenericValue productCategoryMember = delegator.makeValue("ProductCategoryMember", UtilMisc.toMap("productId", productId, "productCategoryId", "MPSCATALOG1", "fromDate", UtilDateTime.nowTimestamp()));
					toStore.add(productCategoryMember);
				}
			}
		}
	}
	
	//Method to create product attributes.
	public static void createProductAttributes(Map paramMap, String productId, List toBeStored){
		if(UtilValidate.isNotEmpty(paramMap.get("Attributes"))){
			Map<String, String> attrMap = (Map) paramMap.get("Attributes");
			for (Map.Entry<String, String> entry : attrMap.entrySet()) {
				Map input = FastMap.newInstance();
				input.put("productId", productId+"");
				input.put("attrName",entry.getKey());
				input.put("attrValue", UtilValidate.isNotEmpty(entry.getValue())?replaceSpecialChars((String)entry.getValue()):"");
				GenericValue entityGV = delegator.makeValue("ProductAttribute", input);
				toBeStored.add(entityGV);
			}
		}
	}
	
	// create the product price
	public static void creteProductPrice(String productId, String price, List toBeStored){
		if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(price)){
			Map input = FastMap.newInstance();
			input.put("productId", productId);
			input.put("productPriceTypeId", "DEFAULT_PRICE");
			input.put("productPricePurposeId", "PURCHASE");
			input.put("currencyUomId", "INR");
			input.put("productStoreGroupId", "_NA_");
			input.put("fromDate", UtilDateTime.nowTimestamp());
			input.put("price", new BigDecimal(price));
			GenericValue productPriceGv = delegator.makeValue("ProductPrice", input);
			toBeStored.add(productPriceGv);
		}
	}
	
	/* Track Util */
	public static  String trackingDataCSV(HttpServletRequest request, HttpServletResponse response){
		try{
			List registrations = new ArrayList();
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			
			List dateCondiList = new ArrayList();
			dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			dateCondiList.add(EntityCondition.makeCondition("shipmentTypeId", EntityOperator.EQUALS, "SALES_SHIPMENT" ));
			dateCondiList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "SHIPMENT_PACKED" ));
			//EntityConditionList dateCondition = new EntityConditionList(dateCondiList, EntityOperator.AND);

			List mainExprs = new ArrayList();
			mainExprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
			
			//EntityConditionList mainCondition = new EntityConditionList(mainExprs, EntityOperator.AND);
			registrations = delegator.findList("Shipment",EntityCondition.makeCondition(mainExprs, EntityOperator.AND),null,UtilMisc.toList("createdStamp DESC"), null, true );

			response.setContentType("application/excel");
			response.setHeader("Content-disposition","attachment;filename=trackingData.csv");
			StringBuffer data = new StringBuffer();

			data.append("Awbno,	OrderNo, Company,	Name,	Address1,	Address2,	Address3,	PinCode,	Phone,	Mobile,	Weight,	Amount,	Quantity,	Cmdt");
			data.append("\n");
			if(registrations!=null && registrations.size()>0) {
				Iterator registrationsItr = registrations.iterator();
				while(registrationsItr.hasNext()) {
					GenericValue gv = (GenericValue) registrationsItr.next();
	                List<GenericValue> shipmentRoutes = delegator.findByAnd("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", gv.get("shipmentId"), "shipmentPackageSeqId", "00001"));
	                GenericValue shipmentRoutesGv = shipmentRoutes.get(0);
	                List shipmentItems = delegator.findByAnd("ShipmentItem", UtilMisc.toMap("shipmentId", gv.get("shipmentId")));
	                GenericValue shippingAddress = gv.getRelatedOne("DestinationPostalAddress");
	                GenericValue primaryOrderHeader = gv.getRelatedOne("PrimaryOrderHeader");
	                
					data.append(shipmentRoutesGv.get("trackingCode")+ ",");
					data.append(primaryOrderHeader.getString("orderId") + ",");
					data.append(" "+ ",");
					data.append(shippingAddress.getString("toName") + ",");
					data.append(shippingAddress.getString("address1") + ",");
					data.append(shippingAddress.getString("address2") + ",");
					data.append(" " + ",");
					data.append(shippingAddress.getString("postalCode") + ",");
					data.append(" " + ",");
					data.append(shippingAddress.getString("contactNumber") + ",");
					data.append(" " + ",");
					data.append(primaryOrderHeader.get("grandTotal") + ",");
					data.append(shipmentItems.size()+ ",");
					data.append(" ");
					data.append("\n");
				}
			}
			
			OutputStream out = response.getOutputStream();
			out.write(data.toString().getBytes());
			out.flush();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return "success";
	}	
	
	//Returns an Error response map
	private static Map returnError(Map response, List errorMessage){
		response.put("status","ERROR");
		response.put("msg",errorMessage);
		return response;
	}
	
	//Returns an Success response map
	private static Map returnSuccess(Map response, List errorMessage){
		response.put("status","OK");
		response.put("msg",errorMessage);
		return response;
	}
	
	
//	public static java.sql.Timestamp toTimestamp(String date, String time, String timeZone) {
//        java.util.Date newDate = UtilDateTime.toDate(date, time);
//        DateFormat istFormat = new SimpleDateFormat();
//        TimeZone istTime = TimeZone.getTimeZone("IST");
//        istFormat.setTimeZone(gmtTime);
//        String istDate = istFormat.format(newDate);
//        if (newDate != null) {
//            return new java.sql.Timestamp(newDate.getTime());
//        } else {
//            return null;
//        }
//    }
	
	public static String toIstTimestampString(Timestamp timestamp) {
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("IST"));
        return df.format(timestamp);
    }
	
	public static String toGmtTimestampString(Timestamp timestamp) {
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(timestamp);
    }
	
	public static Map getCart(Map paramMap){
		GenericValue shoppingList = (GenericValue) paramMap.get("shoppingList");
		Map returnMap = new HashMap();
		try{
			List<GenericValue> shoppingListItems = delegator.findList("ShoppingListItem", EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", shoppingList.getString("shoppingListId"))), null, null, readonly, false);
			//Debug.log("\n ============================== \n shoppingList == "+shoppingList+"\n ============================== \n");
			Map cartSummary = FastMap.newInstance();
			List<Map<String, Object>> result = new ArrayList();
			returnMap.put("savings", getSavings(paramMap));
            //Debug.log("\n ============================== \n shoppingListItems == "+shoppingListItems+"\n ============================== \n");
            if (shoppingListItems == null) {
                shoppingListItems = new ArrayList<GenericValue>();
            }else{
            	List slitems = getParentShoppingListItems(shoppingList.getString("shoppingListId"), (GenericValue) paramMap.get("userLogin"));
            	Map product = null;
            	double cartTotal = 0.0;
            	BigDecimal total = null;
            	for(GenericValue gv : shoppingListItems){
            		//Debug.log("\n ============================== \n productId == "+ILinksAppUtil.getProducts(gv.getString("productId"), paramMap)+"\n ============================== \n");
            		product = ILinksAppUtil.getProducts(gv.getString("productId"), UtilMisc.toMap("getParent", "N")).get(0);
            		product.put("quantity", gv.getString("quantity"));
            		total = ((BigDecimal)product.get("basePrice")).multiply(gv.getBigDecimal("quantity"));
            		
            		
            		if(UtilValidate.isNotEmpty(slitems)){
            			GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", gv.getString("productId")))));
            			product.put("couponQty", slitem.getString("quantity"));
    				}
            		//Debug.log("\n ============================== \n total == "+total+"\n ============================== \n");
            		cartTotal += total.doubleValue();
            		
            		result.add(product);
            	}
            	returnMap.put("result", result);
            	cartSummary.put("cartTotal", cartTotal);
            	cartSummary.put("discount", "0.0");
            	cartSummary.put("cartCount", shoppingListItems.size()+"");
            	cartSummary.put("youmartSavings", "0.0");
            	cartSummary.putAll((Map)returnMap.get("savings"));
            	returnMap.put("cartSummary", cartSummary);
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return returnMap;
	}
	
	
	public static Map getSavings(Map paramMap){
		Map savings = FastMap.newInstance();
		try{
			GenericValue userLogin = (GenericValue)paramMap.get("userLogin");
			if (UtilValidate.isNotEmpty(userLogin)) {
				BigDecimal leftloyaltyPoints = new BigDecimal(0);
				BigDecimal totalloyaltyPoints = new BigDecimal(0);
				GenericValue loyaltyPointsData = delegator.findByPrimaryKey("LoyaltyPoint", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
				if(loyaltyPointsData != null){
					leftloyaltyPoints = new BigDecimal(loyaltyPointsData.getString("loyaltyPoint"));
					savings.put("loyaltyPoint", leftloyaltyPoints);
					//context.leftloyaltyPoints=leftloyaltyPoints;
				}
				List<GenericValue> partyBillValue  = delegator.findList("BillingAccountRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")), null, null, null, false);
				if (UtilValidate.isNotEmpty(partyBillValue)) {
					for (GenericValue partyBill : partyBillValue) {
						EntityCondition barFindCond = EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, partyBill.getString("billingAccountId")),
							EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + "Credit Account for Loyalty #") +userLogin.getString("partyId")+"%"))), EntityOperator.AND);
						List<GenericValue> billingAccountRoleList = delegator.findList("BillingAccount", barFindCond, null, null, null, false);
						if (UtilValidate.isNotEmpty(billingAccountRoleList)) {
							BigDecimal leftBalance = OrderReturnServices.getBillingAccountBalance(partyBill.getString("billingAccountId"), dispatcher.getDispatchContext());
							totalloyaltyPoints = (leftBalance.multiply(new BigDecimal("200"))).add(leftloyaltyPoints);
							if(UtilValidate.isNotEmpty(totalloyaltyPoints)){
								savings.put("loyaltyPoint", totalloyaltyPoints);
							}
							savings.put("leftBalance", leftBalance);
							//context.totalloyaltyPoints=totalloyaltyPoints;
							//context.leftBalance=leftBalance;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Debug.log("\n\n savings === "+savings+"\n\n");
		return savings;
	}

	// Invite Friend Email Notification
    public static Map<String,String> inviteAFriend(GenericValue userLogin,Map<String,Object> parameterMap,Locale locale) {
    	
	    String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#TellFriend";
        String emailType = "PRDS_TELL_FRIEND";
        String token = RandomStringUtils.random(10, true, true);
        Map<String,String> inviteFriendMap = new HashMap<String,String>();
        String productStoreId = (String)parameterMap.get("productStoreId");
        String loyaltyRefId = delegator.getNextSeqId("LoyaltyReference");
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting",
                    UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
        } catch (GenericEntityException e) {
			Debug.logError(e, "Unable to get product store email setting for tell-a-friend: "+ e.toString(), module);
			inviteFriendMap.put("errorCode", "error");
			inviteFriendMap.put("refToken", null);
			return inviteFriendMap;
        }
        if (productStoreEmail == null) {
        	String errMsg = "Could not find tell a friend [" + emailType + "] email settings for the store [" + productStoreId + "]";
			Debug.logError(errMsg, module);
			inviteFriendMap.put("errorCode", "error");
			inviteFriendMap.put("refToken", null);
			return inviteFriendMap;
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }

        //Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        Map<String, Object> bodyParameters = new HashMap<String, Object>(); 
        bodyParameters.put("loyaltyRefId",loyaltyRefId);
        bodyParameters.put("pageUrl","http://www.youmart.in/control/home");//paramMap.get("pageUrl")+"&uniqrefId="+loyaltyRefId);
        bodyParameters.put("locale", locale.getDefault());
        bodyParameters.put("userLogin", userLogin);
        bodyParameters.put("sendFrom",  parameterMap.get("username"));
        bodyParameters.put("toName",  parameterMap.get("friendName"));
       
        Map<String, Object> context = new HashMap<String, Object>(); //FastMap.newInstance();
        Map<String, Object> loyalcontext = new HashMap<String, Object>(); //FastMap.newInstance();
        context.put("bodyScreenUri", bodyScreenLocation);
        context.put("bodyParameters", bodyParameters);
        context.put("sendTo", parameterMap.get("emailTo"));
        context.put("contentType", productStoreEmail.get("contentType"));
        context.put("sendFrom", parameterMap.get("username"));
        
        context.put("sendCc", productStoreEmail.get("ccAddress"));
        context.put("sendBcc", productStoreEmail.get("bccAddress"));
        context.put("subject", productStoreEmail.getString("subject"));  
        
        Debug.log("\n\n ******************Invite Friend Email Context is ::=== "+context+"\n\n");
        
        try {
        	if(UtilValidate.isNotEmpty(parameterMap.get("emailTo")) && !("").equals(((String)parameterMap.get("emailTo")).trim())){
            dispatcher.runAsync("sendMailFromScreen", context);
            }
            
            GenericValue createLoyaltyReference = delegator.makeValue("LoyaltyReference");
      		 
            createLoyaltyReference.set("loyaltyRefId", bodyParameters.get("loyaltyRefId"));
            createLoyaltyReference.set("refByPartyId", bodyParameters.get("sendFrom"));
            createLoyaltyReference.set("refToPartyName", parameterMap.get("friendName"));
            createLoyaltyReference.set("refToPartyId", parameterMap.get("emailTo"));
            createLoyaltyReference.set("refToPartyNumber", parameterMap.get("mobileNo"));
            createLoyaltyReference.set("msgByParty", parameterMap.get("toMessage"));
            delegator.create(createLoyaltyReference);
            GenericValue createInviteReference = delegator.makeValue("InviteFriendReference");
      		 
            createInviteReference.set("loyaltyRefId", bodyParameters.get("loyaltyRefId"));
            createInviteReference.set("refByPartyId", bodyParameters.get("sendFrom"));
            createInviteReference.set("refToPartyName", parameterMap.get("friendName"));
            createInviteReference.set("refToPartyId", parameterMap.get("emailTo"));
            createInviteReference.set("refToPartyNumber", parameterMap.get("mobileNo"));
            createInviteReference.set("productStoreId", parameterMap.get("productStoreId"));
            createInviteReference.set("refToken", token);

            delegator.create(createInviteReference);
            loyalcontext.put("sendByParty", bodyParameters.get("sendFrom"));
            loyalcontext.put("sendToParty", parameterMap.get("emailTo"));
            loyalcontext.put("sendToMobile", parameterMap.get("mobileNo"));
            loyalcontext.put("sendToName", parameterMap.get("friendName"));

            loyalcontext.put("loyaltyRefId", loyaltyRefId);
            if(UtilValidate.isNotEmpty(parameterMap.get("mobileNo")) && !("").equals(((String)parameterMap.get("mobileNo")).trim())){
            dispatcher.runAsync("sendReferLoyaltySmsService", loyalcontext);
            }
        } catch (GenericServiceException e) {
        	 String errMsg = "Problem sending mail: " + e.toString();
			 Debug.logError(e, errMsg, module);
			 
			 inviteFriendMap.put("errorCode", "error");
			 inviteFriendMap.put("refToken", null);
			 return inviteFriendMap;
        }
        catch (GenericEntityException ex) {
            String errMsg = "Problem sending mail: " + ex.toString();
            Debug.logError(ex, errMsg, module);
            
            inviteFriendMap.put("errorCode", "error");
			inviteFriendMap.put("refToken", null);
			return inviteFriendMap;
        }
        String msg = "Successfully invited a friend . An email send to your friend .";
        inviteFriendMap.put("errorCode", "success");
		inviteFriendMap.put("refToken", token);
		return inviteFriendMap;
    }
	
    public static void updatePayment(Map paramMap){
    	try{
    		Debug.log("\n\n paramMap ==================== "+paramMap+"\n\n");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    public static String addProductToOrder(Map paramMap){
    	ObjectMapper mapper = new ObjectMapper();
    	GenericValue product = (GenericValue)paramMap.get("product");
    	GenericValue userLogin = (GenericValue)paramMap.get("userLogin");
    	Debug.log("\n\n ****************** paramMap ****************** "+paramMap.get("productId")+"\n\n");
    	Debug.log("\n\n ****************** paramMap ******************quantity "+paramMap.get("quantity")+"\n\n");
    	
    	try{
    		GenericValue item = null;
    		EntityCondition eCond = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"), "productId", paramMap.get("productId"))),
											EntityOperator.AND,
											EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
    		
    		List<GenericValue> items = delegator.findList("OrderItem", eCond, null, UtilMisc.toList("orderItemSeqId DESC"), null, false);
    		if(UtilValidate.isNotEmpty(items)){
        		//return mapper.writeValueAsString(UtilMisc.toMap("response", UtilMisc.toMap("status", "ERROR", "msg", "Product already exists, please update the quantity.")));
    			item = EntityUtil.getFirst(items);
    			if(UtilValidate.isNotEmpty(item)){
    				BigDecimal qty = item.getBigDecimal("quantity");
    				qty = qty.add((BigDecimal)paramMap.get("quantity"));
    				item.set("quantity", qty);
    				item.store();
    			}
    			return "success";
    		}
    		GenericValue order = EntityUtil.getFirst((List)paramMap.get("orders"));
    		item = EntityUtil.getFirst(delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"))), null, UtilMisc.toList("orderItemSeqId DESC"), null, false));
    		int nextItemSeq = Integer.parseInt(item.getString("orderItemSeqId"));
    		String orderItemSeqId = UtilFormatOut.formatPaddedNumber(++nextItemSeq, 5);
//    		for(GenericValue gv : items){
//    			System.out.println("\n\n  items === "+gv.getString("orderItemSeqId"));
//    			nextItemSeq = Integer.parseInt(gv.getString("orderItemSeqId"));
//    		}
    		item.set("productId", paramMap.get("productId"));
    		item.set("quantity", (BigDecimal)paramMap.get("quantity"));
    		item.set("orderItemSeqId", orderItemSeqId+"");
    		String statusId = null;
    		if(order.getString("statusId").equalsIgnoreCase("ORDER_CREATED")){
    			statusId = "ITEM_CREATED";
    		}else if(order.getString("statusId").equalsIgnoreCase("ORDER_APPROVED")){
    			statusId = "ITEM_APPROVED";
    		}else{
    			statusId = "ITEM_COMPLETED";
    		}
    		if(!(item.getString("statusId").equalsIgnoreCase("ITEM_CREATED") || item.getString("statusId").equalsIgnoreCase("ITEM_APPROVED"))){
    			item.set("statusId", "ITEM_APPROVED");
    		}
    		item.set("unitPrice", product.getBigDecimal("basePrice"));
    		item.set("unitListPrice", product.getBigDecimal("basePrice"));
    		item.set("itemDescription", product.getString("productName"));
    		item.create();
    		//)
    		//System.out.println("\n\n  orderItemSeqId === "+orderItemSeqId);
    		String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
            GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
            orderStatus.set("orderId", item.getString("orderId"));
            orderStatus.set("statusId", statusId);
            orderStatus.set("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.set("orderItemSeqId", orderItemSeqId+"");
            orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
            orderStatus.create();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "success";
    }
    
    private static void validate100cr(String field, Map response){
    	if(UtilValidate.isNotEmpty(field) && field.length() > 100){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Email Id is longer than the expected."));
		}
    }
    
    public static List getParentShoppingListItems(GenericValue order, GenericValue userLogin){
    	List items = null;
    	try{
    		if(UtilValidate.isEmpty(order)){
    			Debug.log("Order is empty");
    			return null;
    		}
    		
    		if(UtilValidate.isEmpty(userLogin)){
    			Debug.log("userLogin is empty");
    			return null;
    		}
    		
    		if(UtilValidate.isEmpty(order.getString("deliveryDate"))){
    			Debug.log("deliveryDate is empty");
    			return null;
    		}
    		Timestamp deliveryDate = order.getTimestamp("deliveryDate");
    		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.getString("partyId"), "lastOrderedDate", deliveryDate, "productStoreId", "UDAILY"));
    		
    		//Debug.log("\n\n entityCondition --- "+entityCondition+"\n\n");
    		List shl = delegator.findList("ShoppingList", entityCondition, UtilMisc.toSet("shoppingListId","parentShoppingListId"), null, readonly, false);
    		//Debug.log("\n\n shl --- "+shl+"\n\n");
    		if(UtilValidate.isNotEmpty(shl)){
    			GenericValue shoppingList = EntityUtil.getFirst(shl);
    			if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
            		shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingList.getString("parentShoppingListId")), false);
	    			if(UtilValidate.isNotEmpty(shoppingList)){
	    				items = shoppingList.getRelated("ShoppingListItem");
	    			}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	//Debug.log("\n\n items --- "+items+"\n\n");
    	return items;
    }
    
    
    public static List getParentShoppingListItems(String shoppingListId, GenericValue userLogin){
    	List items = null;
    	try{
    		if(UtilValidate.isEmpty(shoppingListId)){
    			Debug.log("shoppingListId is empty");
    			return null;
    		}
    		
    		if(UtilValidate.isEmpty(userLogin)){
    			Debug.log("userLogin is empty");
    			return null;
    		}
    		
    		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.getString("partyId"), "shoppingListId", shoppingListId, "productStoreId", "UDAILY"));
    		
    		Debug.log("\n\n entityCondition --- "+entityCondition+"\n\n");
    		List shl = delegator.findList("ShoppingList", entityCondition, UtilMisc.toSet("shoppingListId","parentShoppingListId"), null, readonly, false);
    		Debug.log("\n\n shl --- "+shl+"\n\n");
    		if(UtilValidate.isNotEmpty(shl)){
    			GenericValue shoppingList = EntityUtil.getFirst(shl);
    			if(UtilValidate.isNotEmpty(shoppingList) && UtilValidate.isNotEmpty(shoppingList.getString("parentShoppingListId"))){
            		shoppingList = delegator.findOne("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingList.getString("parentShoppingListId")), false);
	    			if(UtilValidate.isNotEmpty(shoppingList)){
	    				items = shoppingList.getRelated("ShoppingListItem");
	    			}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	Debug.log("\n\n items --- "+items+"\n\n");
    	return items;
    }
    
    
    public static String sendNotify(HttpServletRequest request, HttpServletResponse response) {
    	/*
    	Result result = null;
    	System.out.println("\n\n sendNotify   \n\n");
    	String regId = "";
    	String MESSAGE_KEY = "message";
    	try {
    		regId = request.getParameter("regId");
			String userMessage = request.getParameter("message");
			Sender sender = new Sender(UtilProperties.getPropertyValue("restful.properties", "google.push.notification.server.key"));
			Message message = new Message.Builder().timeToLive(30).delayWhileIdle(true).addData(MESSAGE_KEY, userMessage).build();
			System.out.println("regId: " + regId);
			result = sender.send(message, regId, 1);
			request.setAttribute("pushStatus", result.toString());
			System.out.println("\n\n result -==---    "+result.toString()+"\n\n");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			request.setAttribute("pushStatus", "RegId required: " + ioe.toString());
			return "error";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
			return "error";
		}
		*/
    	return "success";
    }
    
    public static Map<String, Object> notify(DispatchContext dctx, Map<String, ? extends Object> context) {
        try{
        	/*
        	Result result = null;
        	System.out.println("\n\n sendNotify   \n\n");
        	String MESSAGE_KEY = "message";
    		
			String userMessage = (String) context.get("message");
			Sender sender = new Sender(UtilProperties.getPropertyValue("restful.properties", "google.push.notification.server.key"));
			Message message = new Message.Builder().timeToLive(30).delayWhileIdle(true).addData(MESSAGE_KEY, userMessage).build();
			
			EntityCondition entityCondition = EntityCondition.makeCondition("mobDeviceId", EntityOperator.NOT_EQUAL, null);
			List<GenericValue> userLogins = delegator.findList("UserLogin", entityCondition, UtilMisc.toSet("mobDeviceId"), null, readonly, true);
			
			for(GenericValue gv : userLogins){
				System.out.println("regId: " + gv.getString("mobDeviceId"));
				result = sender.send(message, gv.getString("mobDeviceId"), 1);
				System.out.println("\n\n result -==---    "+result.toString()+"\n\n");
			}
			userLogins.clear();
			*/
			//results.put("result", result.toString());
        }catch (Exception e){
            //results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable Expire Shopping List" + context + "\n" + e.getMessage(), module);
        }
        return ServiceUtil.returnSuccess();
    }
    
    /*
    public static Map<String, Object>  supplierReport(){
    	try{
    		Timestamp now = UtilDateTime.nowTimestamp();
    		Timestamp todayStart = UtilDateTime.getDayStart(now);
    		Timestamp todayEnd = UtilDateTime.getDayEnd(now);
    		EntityCondition dateCondition =  EntityCondition.makeCondition( EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"),
    						EntityOperator.AND,
	    				EntityCondition.makeCondition(
	    				EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, todayStart), 
	    					EntityOperator.AND, 
	    				EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, todayEnd)));
    		List<GenericValue> orders = delegator.findList("OrderHeader", dateCondition, null, null, readonly, false);
    		List supRep = FastList.newInstance();
    		if(UtilValidate.isNotEmpty(orders)){
    			List orderIds = EntityUtil.getFieldListFromEntityList(orders, "orderId", true);
    			if(UtilValidate.isNotEmpty(orderIds)){
    				List cond = FastList.newInstance();
    				EntityCondition entityCondition = null;
    				
    				
    				if (cond != null) {
    				    EntityListIterator eli = null;
    				    try {
    				    	DynamicViewEntity dve = new DynamicViewEntity();
    				        dve.addMemberEntity("OH", "OrderHeader");
    				        dve.addMemberEntity("OI", "OrderItem");
    				        dve.addViewLink("OH", "OI", Boolean.FALSE, UtilMisc.toList(new ModelKeyMap("orderId", "orderId")));
    				        dve.addAlias("OI", "productId", null, null, null, Boolean.TRUE, null);
    				        dve.addAlias("OI", "itemDescription", null, null, null, null, null);
    				        dve.addAlias("OI", "unitPrice", null, null, null, null, null);
    				        dve.addAlias("OI", "quantity", null, null, null, null, null);
    				        dve.addAlias("OI", "quantityCount", "quantity", null, null, null, "count");
    				        dve.addAlias("OI", "quantityCount", "unitPrice", null, null, null, "mul");
    				        EntityCondition havingCond = EntityCondition.makeCondition("quantityCount", EntityOperator.GREATER_THAN, Long.valueOf(0));
    				        // do the lookup
    				        eli = delegator.findListIteratorByCondition(dve, null, havingCond, UtilMisc.toList("brandName","brandNameCount"), UtilMisc.toList("orderId"), readonly);

    				        List brandNames = eli.getCompleteList();
    				        eli.close();
    				        
    				    } catch (GenericEntityException e) {
    				        Debug.logError(e, module);
    				    } finally {
    				        if (eli != null) {
    				            try {
    				                eli.close();
    				            } catch (GenericEntityException e) {
    				                Debug.logWarning(e, e.getMessage(), module);
    				            }
    				        }
    				    }
    				}
    				
    				List<GenericValue> items = delegator.findList("OrderItem", entityCondition, null, null, readonly, false);
    				for(GenericValue gv : items){
    					//supRep.add()
    				}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	 return null;
    }
    */
    
	public static  String reportToSupplier(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try{
    		String fromDate = request.getParameter("minDate");
    		String thruDate = request.getParameter("maxDate");
    		Date date = new Date();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Timestamp start = null;
    		Timestamp end = null;
    		String slotType = request.getParameter("slotType");
    		try{
    			if(!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}

    			if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    						
    			if (UtilValidate.isEmpty(thruDate)){
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    			if (UtilValidate.isEmpty(fromDate)){
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    			}
    			if (!UtilValidate.isEmpty(fromDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start = UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    			}
    			
    			if (!UtilValidate.isEmpty(thruDate)) {
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		

    		String query = "SELECT OI.PRODUCT_ID, OI.ITEM_DESCRIPTION, OI.UNIT_PRICE, SUM(OI.QUANTITY), (OI.UNIT_PRICE * SUM(OI.QUANTITY)) AS TOTAL FROM ORDER_HEADER OH LEFT JOIN ORDER_ITEM OI ON OH.ORDER_ID=OI.ORDER_ID WHERE OH.STATUS_ID='ORDER_APPROVED' AND (OI.STATUS_ID='ITEM_APPROVED' OR OI.STATUS_ID='ITEM_CREATED') AND OH.DELIVERY_DATE BETWEEN '"+start+"' AND '"+end+"' AND PRODUCT_STORE_ID='UDAILY' GROUP BY PRODUCT_ID";
          
    		//List<GenericValue> prdFeatures = delegator.findList("ProductFeatureAndAppl", EntityCondition.makeCondition("productId",EntityOperator.IN, productIds), UtilMisc.toSet("productId", "description"), null, readonly, true);
    		
    		DecimalFormat df = new DecimalFormat("###.##");
    		response.setContentType("application/excel");
    		
    		response.setHeader("Content-disposition","attachment;filename=PurchaseSummary.csv");
    		StringBuffer data = new StringBuffer();

    		data.append("\n");
    		data.append("#--------------------------------------------------------------");
    		data.append("\n");
    		
    		
    		data.append("Date Range  : " + start + " To " +  end);
    		data.append("\n");
    		data.append("#--------------------------------------------------------------" );
    		data.append("\n");
    		data.append("\n");
    		data.append(" Sl.No ,PRODUCT ID, PRODUCT NAME, SIZE, UNIT COST, QUANTITY,  TOTAL");
    		data.append("\n");
    		data.append("\n");
    		
    		double totalAmount = 1 ;
    		conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
    	    PreparedStatement ps = conn.prepareStatement(query);
    	    ResultSet rs = ps.executeQuery();
    	    int slNumber = 0;
    	    while (rs.next()) {
    	    	//totalCash=rs.getBigDecimal(1);
    	    	data.append("\""+slNumber+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(1)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(2)+"\"");
				data.append(",");
				
				GenericValue prdFeature = EntityUtil.getFirst(delegator.findList("ProductFeatureAndAppl", EntityCondition.makeCondition("productId",EntityOperator.EQUALS, rs.getString(1)), UtilMisc.toSet("productId", "description"), null, readonly, true));
				if(UtilValidate.isNotEmpty(prdFeature)){
					data.append("\""+prdFeature.getString("description")+"\"");
					data.append(",");
				}else{
					data.append("\""+rs.getString(2)+"\"");
					data.append(",");
				}
				
				data.append("\""+rs.getString(3)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(4)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(5)+"\"");
				data.append(",");
				
				totalAmount += rs.getDouble(5);
				data.append("\n");
				slNumber++;
    	    }
    	    
    	    data.append("\n");
			data.append("\n");
			
			data.append(" ,,,,,Total=,"+totalAmount);
    	    
    		OutputStream out = response.getOutputStream();
    		out.write(data.toString().getBytes());
    		out.flush();
    		request.setAttribute("fromDateStr", start);
    		request.setAttribute("thruDateStr", end);
    		
    		ps.close();
    	    rs.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}finally{
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}
    	
    	return "success";
    }	
	
	public static  String youDailyDispatchReport(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try{
    		String fromDate = request.getParameter("minDate");
    		String thruDate = request.getParameter("maxDate");
    		Date date = new Date();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Timestamp start = null;
    		Timestamp end = null;
    		try{
    			if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}

    			if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    						
    			if (UtilValidate.isEmpty(thruDate)){
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    			if (UtilValidate.isEmpty(fromDate)){
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    			}
    			if (!UtilValidate.isEmpty(fromDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    			}
    			
    			if (!UtilValidate.isEmpty(thruDate)) {
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		

    		String query = "SELECT OH.DELIVERY_DATE, UL.AREA, PA.ADDRESS1, PA.ADDRESS2, UL.FLAT_NO, P.FIRST_NAME,P.LAST_NAME, UL.MOBILE_NUMBER,OI.PRODUCT_ID, OI.ITEM_DESCRIPTION, OI.QUANTITY FROM ORDER_HEADER OH LEFT JOIN ORDER_CONTACT_MECH OCM ON OH.ORDER_ID=OCM.ORDER_ID LEFT JOIN POSTAL_ADDRESS PA ON OCM.CONTACT_MECH_ID=PA.CONTACT_MECH_ID LEFT JOIN USER_LOGIN UL ON UL.USER_LOGIN_ID=OH.CREATED_BY LEFT JOIN ORDER_ITEM OI ON OI.ORDER_ID=OH.ORDER_ID LEFT JOIN PERSON P ON P.PARTY_ID=UL.PARTY_ID WHERE OH.PRODUCT_STORE_ID='UDAILY' AND OH.DELIVERY_DATE IS NOT NULL AND (OI.STATUS_ID='ITEM_APPROVED' OR OI.STATUS_ID='ITEM_CREATED') AND OH.DELIVERY_DATE BETWEEN '"+start+"' AND '"+end+"'";
          
    		//List<GenericValue> prdFeatures = delegator.findList("ProductFeatureAndAppl", EntityCondition.makeCondition("productId",EntityOperator.IN, productIds), UtilMisc.toSet("productId", "description"), null, readonly, true);
    		
    		DecimalFormat df = new DecimalFormat("###.##");
    		response.setContentType("application/excel");
    		
    		response.setHeader("Content-disposition","attachment;filename=YouDaily_dispatch_report.csv");
    		StringBuffer data = new StringBuffer();

    		data.append("\n");
    		data.append("#--------------------------------------------------------------");
    		data.append("\n");
    		
    		
    		data.append("Date Range  : " + start + " To " +  end);
    		data.append("\n");
    		data.append("#--------------------------------------------------------------" );
    		data.append("\n");
    		data.append("\n");

    		data.append(" Sl.No ,Delivery date,	Area, Apartment, Flat no., Customer name,	Mobile no.,	Product, Quantity");
    		data.append("\n");
    		data.append("\n");
    		
    		double totalAmount = 1 ;
    		conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
    	    PreparedStatement ps = conn.prepareStatement(query);
    	    ResultSet rs = ps.executeQuery();
    	    int slNumber = 0;
    	    while (rs.next()) {
    	    	//totalCash=rs.getBigDecimal(1);
    	    	data.append("\""+slNumber+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(1)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(2)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(3)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(5)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(6)+" "+rs.getString(7)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(8)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(9)+" - "+rs.getString(10)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(11)+"\"");
				data.append(",");
				
				
				data.append("\n");
				slNumber++;
    	    }
    	    
    	    data.append("\n");
    	    
    		OutputStream out = response.getOutputStream();
    		out.write(data.toString().getBytes());
    		out.flush();
    		request.setAttribute("fromDateStr", start);
    		request.setAttribute("thruDateStr", end);
    		
    		ps.close();
    	    rs.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}finally{
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}
    	
    	return "success";
    }	
	
	public static  String packingReport(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try{
    		String fromDate = request.getParameter("minDate");
    		String thruDate = request.getParameter("maxDate");
    		String storeId = request.getParameter("storeId");
    		Date date = new Date();
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		Timestamp start = null;
    		Timestamp end = null;
    		try{
    			if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start = UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}
    			if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    			if (UtilValidate.isEmpty(thruDate)){
    				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
    			}
    			if (UtilValidate.isEmpty(fromDate)){
    				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
    			}
    			if (!UtilValidate.isEmpty(fromDate)) {
    				Date d = (Date) sdf.parse(fromDate);
    				start = UtilDateTime.getDayStart( new Timestamp(d.getTime()));
    			}
    			if (!UtilValidate.isEmpty(thruDate)) {
    				Date d1 = (Date) sdf.parse(thruDate);
    				end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		

    		String query = "SELECT OH.ORDER_ID, OI.PRODUCT_ID, OI.ITEM_DESCRIPTION, OI.QUANTITY, UL.FLAT_NO, UL.AREA, PA.ADDRESS1, PA.ADDRESS2, PA.CITY FROM ORDER_HEADER OH LEFT JOIN ORDER_CONTACT_MECH OCM ON OH.ORDER_ID=OCM.ORDER_ID LEFT JOIN POSTAL_ADDRESS PA ON PA.CONTACT_MECH_ID=OCM.CONTACT_MECH_ID LEFT JOIN USER_LOGIN UL ON OH.CREATED_BY=UL.USER_LOGIN_ID LEFT JOIN ORDER_ITEM OI ON OI.ORDER_ID=OH.ORDER_ID WHERE PRODUCT_STORE_ID='"+storeId+"'  AND OH.DELIVERY_DATE IS NOT NULL AND (OI.STATUS_ID='ITEM_APPROVED' OR OI.STATUS_ID='ITEM_CREATED') AND OH.DELIVERY_DATE BETWEEN '"+start+"' AND '"+end+"' ORDER BY AREA";
          
    		//List<GenericValue> prdFeatures = delegator.findList("ProductFeatureAndAppl", EntityCondition.makeCondition("productId",EntityOperator.IN, productIds), UtilMisc.toSet("productId", "description"), null, readonly, true);
    		
    		DecimalFormat df = new DecimalFormat("###.##");
    		response.setContentType("application/excel");
    		
    		response.setHeader("Content-disposition","attachment;filename=packingReport.csv");
    		StringBuffer data = new StringBuffer();

    		data.append("\n");
    		data.append("#--------------------------------------------------------------");
    		data.append("\n");
    		data.append("Date Range  : " + start + " To " +  end);
    		data.append("\n");
    		data.append("#--------------------------------------------------------------" );
    		
    		double totalAmount = 1 ;
    		conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
    	    PreparedStatement ps = conn.prepareStatement(query);
    	    ResultSet rs = ps.executeQuery();
    	    int slNumber = 0;
    	    String flatNo = null;
    	    while (rs.next()) {
    	    	//totalCash=rs.getBigDecimal(1);
    	    	if(flatNo == null || !flatNo.equalsIgnoreCase(rs.getString(5))){
    	    		flatNo = rs.getString(5);
    	    		data.append("#--------------------------------------------------------------");
    	    		data.append("\n");
    	    		data.append(" Area : "+rs.getString(6)+" , Flat No. : "+flatNo+", , , ");
    	    		data.append("\n");
    	    		data.append("#--------------------------------------------------------------");
    	    		
    	    		data.append("\n");
    	    		data.append(" Sl.No ,Product Id, Product Name, Quantity, Is Chilled");
    	    		data.append("\n");
    	    		data.append("\n");
    	    	}
    	    	data.append("\""+slNumber+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(2)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(3)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(4)+"\"");
				data.append(",");
				
				data.append("\"\"");
				data.append(",");
				
				data.append("\n");
				slNumber++;
    	    }
    	    
    	    data.append("\n");
    	    
    		OutputStream out = response.getOutputStream();
    		out.write(data.toString().getBytes());
    		out.flush();
    		request.setAttribute("fromDateStr", start);
    		request.setAttribute("thruDateStr", end);
    		
    		ps.close();
    	    rs.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}finally{
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}
    	
    	return "success";
    }
	
	public static  String customerData(HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		try{
    		String query = "SELECT UL.PARTY_ID, UL.FIRST_NAME, UL.LAST_NAME, PA.ADDRESS1, PA.AREA, UL.FLAT_NO, UL.MOBILE_NUMBER, UL.USER_LOGIN_ID, UL.ENABLED, UL.IS_UDAILY_ENABLED  FROM USER_LOGIN UL LEFT JOIN POSTAL_ADDRESS PA ON UL.CONTACT_MECH_ID=PA.CONTACT_MECH_ID WHERE UL.CONTACT_MECH_ID IS NOT NULL";
          
    		DecimalFormat df = new DecimalFormat("###.##");
    		response.setContentType("application/excel");
    		
    		response.setHeader("Content-disposition","attachment;filename=customerData.csv");
    		StringBuffer data = new StringBuffer();

    		data.append("\n");
    		data.append(" Sl.No ,Customer ID, Customer Name, Apartment name, Area, Block No, Flat No, Address, Mobile No, Email address, YouMart status, YouDaily status");
    		data.append("\n");
    		data.append("\n");
    		
    		double totalAmount = 1 ;
    		conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
    	    PreparedStatement ps = conn.prepareStatement(query);
    	    ResultSet rs = ps.executeQuery();
    	    int slNumber = 0;
    	    while (rs.next()) {
    	    	//totalCash=rs.getBigDecimal(1);
    	    	data.append("\""+slNumber+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(1)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(2)+" "+rs.getString(3)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(4)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(5)+"\"");
				data.append(",");
				data.append("\"\"");
				data.append(",");
				
				data.append("\""+rs.getString(6)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(7)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(8)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(9)+"\"");
				data.append(",");
				
				data.append("\""+rs.getString(10)+"\"");
				data.append(",");
				
				data.append("\n");
				slNumber++;
    	    }
    	    
    	    data.append("\n");
    	    
    		OutputStream out = response.getOutputStream();
    		out.write(data.toString().getBytes());
    		out.flush();
    		
    		ps.close();
    	    rs.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}finally{
    		try {
    			if(conn != null){
    				conn.close();
    			}
			} catch (SQLException se) {
				se.printStackTrace();
			}	
    	}
    	
    	return "success";
    }
}