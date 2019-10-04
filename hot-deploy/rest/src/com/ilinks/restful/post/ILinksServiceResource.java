package com.ilinks.restful.post;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.RandomStringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppinglist.ShoppingListEvents;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;


import org.codehaus.jackson.type.TypeReference;


@Path("/store")
public class ILinksServiceResource {
	
	/**
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
	public static final EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
	private EntityCondition dateCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	private ObjectMapper mapper = new ObjectMapper();
	private Map<String, Object> response = FastMap.newInstance();
	private String resource = "restful.properties";
	
	private final String SLOT1 = "SLOT1";
	private final String SLOT2 = "SLOT2";
	private final String SLOT4 = "SLOT4";
	private final String SLOT5 = "SLOT5";
	
	
	//@Context
	//HttpHeaders headers;

	public static final String module = ILinksServiceResource.class.getName();
	public static final GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
	public static final LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default", delegator);

	public String checkAuth(String username, String password, String imei, String mobDeviceId) throws Exception {

		if (username == null || password == null /*|| imei == null*/) {
			response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg", "Please provide correct imei no. ,  username and password"));
			return mapper.writeValueAsString(response);
		}
		
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", username), true);
		if (UtilValidate.isNotEmpty(userLogin)) {
			Debug.log("password " + password);
			Debug.log("currentPassword "+ userLogin.getString("currentPassword"));
			
			Map<String, Object> result = null;
	        try {
	            // get the visit id to pass to the userLogin for history
	            result = dispatcher.runSync("userLogin", UtilMisc.toMap("login.username", username, "login.password", password));
	        } catch (Exception e) {
	            response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg",UtilProperties.getPropertyValue("SecurityextUiLabels", "loginevents.following_error_occurred_during_login")));
	        }
	        
	        Debug.log("\n====================================\n result == "+result+"\n====================================\n");
	        
	        if(ServiceUtil.isSuccess(result)){
				response.put("response","success");
				userLogin = (GenericValue) result.get("userLogin");
				
				if(UtilValidate.isNotEmpty(mobDeviceId)){
					userLogin.set("mobDeviceId", mobDeviceId);
					userLogin.store();
				}
				//if(!imei.equals(userLogin.getString("imei"))){
					//response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg","Please provide valid IMEI details."));
				//}
			}else{
				response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg",ServiceUtil.getErrorMessage(result)));
			}
			
		}else{
			response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg","Please provide valid user details."));
		}
		return mapper.writeValueAsString(response);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public String authUser(String json) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = mapper.readValue(json, Map.class);
		response.put("serviceType","login");
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("serviceType", "login", "status", "ERROR", "msg", "Please send the parameters."));
		}
		return checkAuth((String)paramMap.get("username"), (String)paramMap.get("password"),(String) paramMap.get("imei"), (String)paramMap.get("mobDeviceId"));
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/logout")
	public String logout(String json) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg", "Please send the parameters."));
		}
		response.put("serviceType","logout");
		GenericValue userLogin = null;
		try{
			userLogin = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", paramMap.get("username")));
		}catch(Exception e){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","User not found."));
			return mapper.writeValueAsString(response);
		}
		
		if (userLogin != null) {
            LoginWorker.setLoggedOut(userLogin.getString("userLoginId"), delegator);
        }
		response.put("response","success");
		return mapper.writeValueAsString(response);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/createCustomer")
	public String createCustomer(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String uId = RandomStringUtils.random(4, false, true);
		try{
			paramMap = mapper.readValue(json, Map.class);
			Debug.log("\n\n paramMap === "+paramMap+"\n\n");
			response.put("serviceType","createCustomer");
			if(UtilValidate.isEmpty(paramMap)){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg", "Please send the parameters."));
			}
			/*
			if(UtilValidate.isEmpty(paramMap.get("firstName"))){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid First Name"));
			}
			
			if(UtilValidate.isEmpty(paramMap.get("lastName"))){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid Last Name"));
			}*/
			
			if(UtilValidate.isEmpty(paramMap.get("emailId")) || !UtilValidate.isEmail((String)paramMap.get("emailId"))){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid email Id"));
			}
			
			if(((String)paramMap.get("emailId")).length() > 100){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Email Id is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			if(((String)paramMap.get("firstName")).length() > 100){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","First name is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			if(((String)paramMap.get("lastName")).length() > 100){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Last name is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			
			if(((String)paramMap.get("password")).length() > 20){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Password is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			
			if(UtilValidate.isEmpty(paramMap.get("mobileNumber"))){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid Mobile Number"));
			}
			
			if(((String)paramMap.get("mobileNumber")).length() > 12){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Mobile Number is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			
			List<GenericValue> gv = delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("emailId")));
			Debug.log("\n\n gv === "+gv+"\n\n");
			if(UtilValidate.isNotEmpty(gv)){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","User already exists"));
				return mapper.writeValueAsString(response);
			}
			
			Map person = ILinksAppUtil.createPerson(UtilMisc.toMap("firstName", UtilValidate.isNotEmpty(paramMap.get("firstName"))?paramMap.get("firstName"):"", "lastName", UtilValidate.isNotEmpty(paramMap.get("lastName"))?paramMap.get("lastName"):"", "emailId", ""+paramMap.get("emailId")));
			String partyId = (String) person.get("partyId");
			if(ServiceUtil.isSuccess(person)){
				paramMap.put("partyId", partyId);
				paramMap.put("enabled", "N");
				paramMap.put("uniqCode", uId);
				person = ILinksAppUtil.createUserLogin(paramMap, delegator);
			}
			
			Debug.log("\n\n person === "+person+"\n\n");
			if(ServiceUtil.isSuccess(person)){
				response.put("response","success");
			}else{
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg",ServiceUtil.getErrorMessage(person)));
			}
			
			Map result = dispatcher.runSync("userRegistrationSmsService", UtilMisc.toMap("partyId", partyId, "phoneNo", (String)paramMap.get("mobileNumber"), "isMobile","Y", "uniqCode", uId));
			Debug.log("\n\n result == "+result+"\n\n");
		}catch(Exception e){
			e.printStackTrace();
		}
		return mapper.writeValueAsString(response);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCustomer")
	public String getCustomer(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","getCustomer");
		Set fields = UtilMisc.toSet("userLoginId", "enabled", "partyId", "firstName", "lastName", "uniqCode");
		fields.add("address1");
		fields.add("address2");
		fields.add("city");
		fields.add("state");
		fields.add("country");
		fields.add("flatNo");
		fields.add("area");
		fields.add("zipCode");
		fields.add("emailId");
		fields.add("contactMechId");
		fields.add("mobileNumber");
		fields.add("isUdailyEnabled");
		fields.add("isXpressEnabled");
		List<GenericValue> userLogin = delegator.findList("UserLogin", EntityCondition.makeCondition(UtilMisc.toMap("userLoginId", (String)paramMap.get("loginID"))), fields, null, ILinksAppUtil.readonly, false);
		Debug.log("\n\n userLogin === "+userLogin+"\n\n");
		if(UtilValidate.isNotEmpty(userLogin)){
			Map result = userLogin.get(0).getAllFields();
			paramMap.put("userLogin", userLogin.get(0));
			result.putAll(ILinksAppUtil.getSavings(paramMap));
			response.put("response",result);
		}else{
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","User Not Exists"));
		}
		
		return mapper.writeValueAsString(response);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/validateCustomer")
	public String validateCustomer(String json) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String uId = RandomStringUtils.random(6, true, true);
		
		paramMap = mapper.readValue(json, Map.class);;//processRequest(xmlDoc, "createCustomer", "Customer");
		response.put("serviceType","validateCustomer");
		if(UtilValidate.isNotEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg",null));
		}
		
		if(UtilValidate.isEmpty(paramMap.get("emailId")) || !UtilValidate.isEmail((String)paramMap.get("emailId"))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid email Id"));
		}
		
		if(UtilValidate.isEmpty(paramMap.get("mobileNumber"))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid Mobile Number"));
		}
		
		if(UtilValidate.isEmpty(paramMap.get("uniqCode"))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Enter Valid Uniq Code"));
		}
		
		Map result = ILinksAppUtil.updateUserLogin(paramMap);
		
		if(ServiceUtil.isSuccess(result)){
			response.put("response","success");
		}else{
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg",ServiceUtil.getErrorMessage(result)));
		}
		
		return mapper.writeValueAsString(response);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/updateCustomer")
	public String updateCustomer(String json) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap = mapper.readValue(json, new TypeReference<HashMap<String,Object>>(){});
		response.put("serviceType","updateCustomer");
		//store party
		if(UtilValidate.isNotEmpty(paramMap) && paramMap.get("responseMessage").equals("success") && UtilValidate.isNotEmpty(paramMap.get("dataMap"))){
			Map responseApi = ILinksAppUtil.updateParty((Map)paramMap.get("dataMap"));
			response.put("response",responseApi);
		}else{
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg",paramMap.get("errorMessageList")));
		}
		return mapper.writeValueAsString(response);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProduct")
	public String getProduct(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","getProduct");
        try{
            List<Map<String, Object>> product = ILinksAppUtil.getProducts(paramMap.get("productId"), paramMap);
            if(UtilValidate.isNotEmpty(product)){
                response.put("response", product.get(0));
            }else{
                response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Product Not Exists"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
		
		return mapper.writeValueAsString(response);
	}
	
    /**
     * 
     * @param callbackFunction
     * @param catalogId
     * @return all the categories related to a catalog
     */
	@POST
    @Produces("application/json")
	@Path("/category")
    public String getCatalogCategory(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","category");
		EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, paramMap.get("storeId")),EntityOperator.AND, EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, paramMap.get("catalogId")));
		List<GenericValue> list = null;
		LinkedHashMap categoryMap = null;
        try{
        	//Debug.log(EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition)+"");
        	list = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), null, null, findOptions, true);
        	if(UtilValidate.isNotEmpty(list)){
        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, paramMap.get("catalogId")), EntityOperator.AND, EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, paramMap.get("typeId")));
        		list = delegator.findList("ProdCatalogCategory",entityCondition,null,null,ILinksAppUtil.readonly,true);
                list = EntityUtil.filterByDate(list);
                
				GenericValue gv = EntityUtil.getFirst(list);
				if(UtilValidate.isNotEmpty(gv)){
					categoryMap = (LinkedHashMap) ILinksAppUtil.getRelatedCategoriesRet(delegator, "trailCategories", gv.getString("productCategoryId"), false, true, true);
				}
                
                /*
                entityCondition = EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "productCategoryId", true));
                list = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("sequenceNum"), findOptions, true);
                GenericValue gv = null;
                for(int i = 0; i < list.size(); i++){
                	gv = (GenericValue) list.get(i);
                	entityCondition = EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "productCategoryId", true));
                	list.get(i).put("child1", delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("productCategoryId", "categoryName"), UtilMisc.toList("sequenceNum"), findOptions, true);
                }
                */
        	}
	        if(UtilValidate.isNotEmpty(categoryMap)){
	            response.put("response", categoryMap);
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Category not found"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/products")
    public String getCategoryProducts(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		String categoryId = (String) paramMap.get("categoryId");
		String field = "productId";
		//PCCT_PROMOTIONS
		EntityCondition entityCondition = null; //EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
		List<GenericValue> list = null;
        try{
        	if(UtilValidate.isNotEmpty(paramMap.get("typeId")) && UtilValidate.isNotEmpty(paramMap.get("catalogId"))){
	        	entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, paramMap.get("catalogId")), EntityOperator.AND, EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, paramMap.get("typeId")));
	    		list = delegator.findList("ProdCatalogCategory",entityCondition,null,null,ILinksAppUtil.readonly,true);
	            list = EntityUtil.filterByDate(list);
	        	Debug.log("\n\n list ==  "+list+"\n\n");
	            GenericValue gv = null;
	            if(UtilValidate.isNotEmpty(list)){
	            	gv = EntityUtil.getFirst(list);
	            }
	            
	            if(UtilValidate.isNotEmpty(gv)){
	            	categoryId = gv.getString("productCategoryId");
	            }
        	}
            
        	//Get All the products in single category and its child categories Or Products of list of categories
        	Debug.log("\n\n categoryId === "+categoryId);
        	if(UtilValidate.isNotEmpty(categoryId)){
	        	entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, categoryId));
	    		list = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), findOptions, true);
	        	if(UtilValidate.isEmpty(list)){
	        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
	        	}else{
	        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "productCategoryId", true)));
	        	}
	        	
	        	list = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), null, null, findOptions, true);
        	}
        	response.put("serviceType","getproducts_"+categoryId);
        	
        	//Debug.log("\n\n list === "+list);
        	
        	if(UtilValidate.isNotEmpty(paramMap.get("typeId")) && UtilValidate.isNotEmpty(paramMap.get("productId"))){
        		response.put("serviceType","getproducts_"+paramMap.get("typeId"));
        		if(((String)paramMap.get("typeId")).equalsIgnoreCase("ALSO_BOUGHT")){
        			field = "productIdTo";
	        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, paramMap.get("productId")),
	        				EntityOperator.AND,
	        				EntityCondition.makeCondition("productAssocTypeId", EntityOperator.EQUALS, "ALSO_BOUGHT"));
	        		findOptions.setFetchSize(UtilProperties.getPropertyAsLong(resource, "product.also.bought.count", 5).intValue());
	        		list = delegator.findList("ProductAssoc", entityCondition, null, UtilMisc.toList("sequenceNum DESC"), findOptions, true);
        		}
        		
        		if(((String)paramMap.get("typeId")).equalsIgnoreCase("BRAND")){
        			GenericValue gv = delegator.findOne("Product", true, UtilMisc.toMap("productId", paramMap.get("productId")));
        			if(UtilValidate.isEmpty(gv)){
        				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Product Id is not valid"));
        				return mapper.writeValueAsString(response);
        			}
        			entityCondition = EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, gv.getString("brandName"));
	        		findOptions.setFetchSize(UtilProperties.getPropertyAsLong(resource, "product.same.brand.count", 5).intValue());
	        		list = delegator.findList("Product", entityCondition, UtilMisc.toSet("productId"), UtilMisc.toList("productId"), findOptions, true);
        		}
        		findOptions.setFetchSize(-1);
        	}
        	
        	
        	//Debug.log("\n\n field === "+field);
        	List<Map<String, Object>> result = null;
        	if(UtilValidate.isNotEmpty(list)){
        		if(UtilValidate.isNotEmpty(paramMap.get("typeId")) && !((String)paramMap.get("typeId")).equalsIgnoreCase("BRAND")){
        			list = EntityUtil.filterByDate(list);
        		}
        		//Debug.log("\n\n 2222 list === "+list);
        		//Enable the filter
        		if(UtilValidate.isNotEmpty(paramMap.get("priceRange")) || UtilValidate.isNotEmpty(paramMap.get("brandNames")) || UtilValidate.isNotEmpty(paramMap.get("Availability"))){
        			paramMap.put("filter", "Y");
        		}
                result = ILinksAppUtil.getProducts(EntityUtil.getFieldListFromEntityList(list, field, true), paramMap);
                //Debug.log("\n\n result === "+result);
                //Get YouDaily, Number of booklets in the single coupon product
                if(UtilValidate.isNotEmpty(paramMap.get("catalogId")) && ((String)paramMap.get("catalogId")).equalsIgnoreCase("YOU_DAILY") && UtilValidate.isNotEmpty(result)){
                	List newResult = new ArrayList();
                	GenericValue pcmGv = null;
                	Map prd = null;
                	for(int i = 0; i < result.size(); i++){
                		prd = result.get(i);
                		pcmGv = EntityUtil.getFirst(EntityUtil.filterByAnd(list, UtilMisc.toMap("productId", prd.get("productId"), "productCategoryId", categoryId)));
                		if(UtilValidate.isNotEmpty(pcmGv)){
                			prd.put("qty", pcmGv.getString("quantity"));
                		}
                		newResult.add(prd);
                	}
                	
                	result = newResult;
                }
        	}
        	
	        if(UtilValidate.isNotEmpty(result)){
	        	response.put("response", UtilMisc.toMap("status", "success", "msg", result.subList(0, (result.size() - 1)), "pagination", result.get(result.size() - 1) ));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Products not found"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        	e.printStackTrace();
        }
        
        return mapper.writeValueAsString(response);
	}

	@POST
    @Produces("application/json")
	@Path("/home")
    public String getHomePageDetails(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","home");
		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", paramMap.get("storeId"), "catalogId", paramMap.get("catalogId"), "position", paramMap.get("position")));
		List<GenericValue> list = null;
		Map home = FastMap.newInstance();
        try{
    		list = delegator.findList("BannerManagement", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("bannerImageUrl", "bannerLinkUrl","sequenceNum"), UtilMisc.toList("sequenceNum"), findOptions, true);
        	if(UtilValidate.isNotEmpty(list)){
        		home.put("banner", list);
        	}
        	
	        if(UtilValidate.isNotEmpty(home)){
	            response.put("response", home);
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Banners not found"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/tab")
    public String getUDailyList(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", paramMap.get("type")));
		List<GenericValue> list = null;
		List address = FastList.newInstance();
        try{
        	response.put("serviceType","tab"+((String)paramMap.get("type")));
    		list = delegator.findList("PartyContactMech", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("partyId", "contactMechId"), UtilMisc.toList("fromDate"), findOptions, true);
        	if(UtilValidate.isNotEmpty(list)){
        		List contactMechIds = EntityUtil.getFieldListFromEntityList(list, "contactMechId", true);
        		if(UtilValidate.isNotEmpty(contactMechIds)){
        			entityCondition = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds);
        			list = delegator.findList("PostalAddress",entityCondition, null, null, findOptions, true);
        		}
        	}
        	
        	Map postalAddrss = FastMap.newInstance();
        	for(GenericValue gv : list){
        		postalAddrss = gv.getAllFields();
        		postalAddrss.put("address1", gv.getString("toName") +" , "+gv.getString("address1"));
        		address.add(postalAddrss);
        	}
        	
	        if(UtilValidate.isNotEmpty(address)){
	            response.put("response", address);
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "You Daily Addresses not found"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/changePwd")
    public String changePassword(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","changePwd");
        try{
    		GenericValue gv = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("loginId")), true);
    		if(UtilValidate.isEmpty(gv)){
    			response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
    			return mapper.writeValueAsString(response);
    		}
    		
    		Map result = dispatcher.runSync("updatePassword", UtilMisc.toMap("userLogin", gv, "userLoginId", gv.getString("userLoginId"), "currentPassword", paramMap.get("currentPassword"), "newPassword", paramMap.get("newPassword"), "newPasswordVerify", paramMap.get("newPasswordVerify")));
        	
	        if(UtilValidate.isNotEmpty(result) && ServiceUtil.isError(result) ){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", ServiceUtil.getErrorMessage(result)));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Password is changed sucessfully"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/createOrder")
    public String createOrder(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		response.put("serviceType","createOrder");
        try{
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), false);
        		paramMap.put("userLogin", userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("storeId"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "StoreID not found"));
        		return mapper.writeValueAsString(response);
        	}
        	//Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
        	paramMap.put("userLogin", userLogin);
        	//orderName
        	List orders = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("orderName")) && ((String)paramMap.get("orderName")).equalsIgnoreCase("PRODUCT")){
	        	Timestamp deliveryDate = null;
	        	if(UtilValidate.isNotEmpty(paramMap.get("deliveryDate"))){
		        	if(paramMap.get("deliveryDate") instanceof Integer){
		        		deliveryDate = UtilDateTime.getTimestamp((Integer)paramMap.get("deliveryDate"));
		        	}else if(paramMap.get("deliveryDate") instanceof String){
		        		deliveryDate = UtilDateTime.getTimestamp(Long.parseLong((String)paramMap.get("deliveryDate")));
		        	}else{
		        		deliveryDate = UtilDateTime.getTimestamp((Long)paramMap.get("deliveryDate"));
		        	}
		        }
	        	
	        	if(UtilValidate.isNotEmpty(deliveryDate) &&  ((String)paramMap.get("storeId")).equalsIgnoreCase("UDAILY")){
	        		deliveryDate = UtilDateTime.getDayStart(deliveryDate);
	        	}
	    		String deliveryDateStr = deliveryDate.toString();
	    		Map cond = UtilMisc.toMap("deliveryDate", deliveryDate, "productStoreId", paramMap.get("storeId"), "createdBy", paramMap.get("username"),"orderName",(String)paramMap.get("orderName"));
	    		orders = delegator.findList("OrderHeader", EntityCondition.makeCondition(cond), null, null, ILinksAppUtil.readonly, false);
        	}
        	Debug.log("\n ============================== \n orders == "+orders+"\n ============================== \n");
    		if(UtilValidate.isNotEmpty(orders)){
    			paramMap.put("orders", orders);
        		GenericValue orderHeader = EntityUtil.getFirst(orders);
        		paramMap.put("orderId", orderHeader.getString("orderId"));
        		Debug.log("\n ============================== \n orders == "+orderHeader.getString("orderId")+"\n ============================== \n");
        		List<HashMap> items = (ArrayList)((Map)paramMap.get("orderItems")).get("Item");
        		paramMap.remove("orderItems");
        		Map result = FastMap.newInstance();
     	        for(Map item : items){
     	        	paramMap.put("productId",  item.get("productId"));
     	        	GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId", item.get("productId")),  false);
     	        	paramMap.put("quantity", new BigDecimal((String)item.get("quantity")));
     	        	paramMap.put("product", product);
        			//;
     	        	String temp = ILinksAppUtil.addProductToOrder(paramMap);
     	        	result.put("responseMessage", temp);
     	        }
     	       result.put("createdBy", userLogin.getString("userLoginId"));
     	       result.put("statusId", orderHeader.getString("statusId"));
     	       result.put("orderTypeId", orderHeader.getString("orderTypeId"));
     	       result.put("orderId", orderHeader.getString("orderId"));
     	       //response.put("response", UtilMisc.toMap("status", "success", "msg",result));
     	       
     	       
     	      if(((String)paramMap.get("storeId")).equalsIgnoreCase("UDAILY")){
		        	List orderItems = FastList.newInstance();
		        	Map summary = null;
		        	if(UtilValidate.isNotEmpty(result)){
		        		result.put("userLogin", paramMap.get("userLogin"));
		        		summary = ILinksAppUtil.orderDetail(result, orderItems);
		        		result.remove("userLogin");
		        	}
		            response.put("response", UtilMisc.toMap("status", "success", "msg", result, "details", orderItems, "summary", summary));
	        	}else{
	        		response.put("response", UtilMisc.toMap("status", "success", "msg", result));
	        	}
     	       
     	       return mapper.writeValueAsString(response);
        	}else{
	        	Map result = ILinksAppUtil.createOrder(paramMap);
	        	if(UtilValidate.isNotEmpty(result) && ServiceUtil.isError(result) ){
		            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", ServiceUtil.getErrorMessage(result)));
		        }else{
		        	if(((String)paramMap.get("storeId")).equalsIgnoreCase("UDAILY")){
			        	List orderItems = FastList.newInstance();
			        	Map summary = null;
			        	if(UtilValidate.isNotEmpty(result)){
			        		result.put("userLogin", paramMap.get("userLogin"));
			        		summary = ILinksAppUtil.orderDetail(result, orderItems);
			        		result.remove("userLogin");
			        	}
			            response.put("response", UtilMisc.toMap("status", "success", "msg", result, "details", orderItems, "summary", summary));
		        	}else{
		        		response.put("response", UtilMisc.toMap("status", "success", "msg", result));
		        	}
		        }
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/getOrders")
    public String getOrders(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	EntityCondition enityCondition = null;
        	List result = FastList.newInstance();
        	GenericValue gv = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		response.put("serviceType","OrderHistory"); 
        		GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        		paramMap.put("userLogin", userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
	        	if(UtilValidate.isEmpty(paramMap.get("storeId"))){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "StoreId not found"));
	        		return mapper.writeValueAsString(response);
	        	}
	        	
	        	enityCondition = EntityCondition.makeCondition(UtilMisc.toMap("createdBy", paramMap.get("username"), "productStoreId", paramMap.get("storeId")));
	        	List orderBy = null;
	        	
	        	if(UtilValidate.isNotEmpty(paramMap.get("orderName"))){
	        		enityCondition = EntityCondition.makeCondition(UtilMisc.toMap("createdBy", paramMap.get("username"), "productStoreId", paramMap.get("storeId"), "orderName", paramMap.get("orderName")));
	        	}
	        	if(UtilValidate.isEmpty(paramMap.get("storeId")) || !((String)paramMap.get("storeId")).equalsIgnoreCase("UDAILY")){
	        		orderBy = UtilMisc.toList("orderDate DESC");
	        	}else{
	        		//Add condition to get the new list of orders for the Udaily based on the status
	        		orderBy = UtilMisc.toList("deliveryDate");
	        	}
	        	result = delegator.findList("OrderHeader",enityCondition , UtilMisc.toSet("orderDate", "orderId", "grandTotal", "statusId","deliveryDate", "orderName"), orderBy, findOptions, true);
        	}
        	
        	Map addnl = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("orderId"))){
        		response.put("serviceType","OrderDetails");
        		paramMap.put("getOrders", "Y");
        		addnl = ILinksAppUtil.orderDetail(paramMap, result);
        		//result.add(gv);
        	}
        	
        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Orders not found"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result, "summary",addnl));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "errorMassage", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/activateTab")
    public String activateTab(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		
		List<GenericValue> list = null;
        try{
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), false);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.getString("partyId"), "contactMechId", paramMap.get("contactMechId")));
    		list = delegator.findList("PartyContactMech", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("partyId", "contactMechId"), UtilMisc.toList("fromDate"), findOptions, true);
    		Debug.log("\n\n entityCondition == "+entityCondition+"\n\n");
    		if(UtilValidate.isNotEmpty(list)){
        		List contactMechIds = EntityUtil.getFieldListFromEntityList(list, "contactMechId", true);
        		if(UtilValidate.isNotEmpty(contactMechIds)){
        			entityCondition = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds);
        			list = delegator.findList("PostalAddress",entityCondition, null, null, findOptions, true);
        		}
        	}
        	
        	if(UtilValidate.isNotEmpty(paramMap.get("type")) && ((String)paramMap.get("type")).equals("UDAILY")){
        		response.put("serviceType","activateUDAILY");
	        	GenericValue gv = EntityUtil.getFirst(list);
	        	
	        	Debug.log("\n\n address gv === "+gv+"\n\n");
	        	LinkedList toBeStored = new LinkedList();
	        	String paContactMechId = delegator.getNextSeqId("ContactMech");
	    		Map fieldValues = new HashMap();
	    		fieldValues.put("contactMechId", paContactMechId+"");
	    		fieldValues.put("contactMechTypeId", "POSTAL_ADDRESS");
	    		toBeStored.add(delegator.makeValue("ContactMech", fieldValues));
	    		
	    		fieldValues.clear();
	    		
	    		fieldValues.put("contactMechId", paContactMechId+"");
	    		fieldValues.put("partyId", userLogin.getString("partyId"));
	    		fieldValues.put("fromDate", UtilDateTime.nowTimestamp());
	    		toBeStored.add(delegator.makeValue("PartyContactMech", fieldValues));
	    		
	    		gv.set("toName", "");
	    		gv.set("address1", paramMap.get("flatNo")+","+gv.getString("address1"));
	    		gv.set("address2", gv.getString("area"));
	    		gv.set("state", "Karnataka");
	    		gv.set("country", "India");
	    		gv.set("address2", gv.getString("area"));
	    		gv.set("contactMechId", paContactMechId);
	    		toBeStored.add(gv);
	    		
	    		fieldValues.clear();
	    		
	    		fieldValues.put("contactMechId", paContactMechId+"");
	    		fieldValues.put("partyId", userLogin.getString("partyId"));
	    		fieldValues.put("fromDate", UtilDateTime.nowTimestamp());
	    		fieldValues.put("contactMechPurposeTypeId", "SHIPPING_LOCATION");
	    		toBeStored.add(delegator.makeValue("PartyContactMechPurpose", fieldValues));
	    		
	        	delegator.storeAll(toBeStored);
	        	
	        	userLogin.set("address1", gv.getString("address1"));
	        	userLogin.set("city", gv.getString("city"));
	        	userLogin.set("zipCode", gv.getString("postalCode"));
	        	userLogin.set("state", gv.getString("stateProvinceGeoId"));
	        	userLogin.set("country", gv.getString("countryGeoId"));
	        	userLogin.set("flatNo", paramMap.get("flatNo"));
	        	userLogin.set("area", gv.getString("area"));
	        	userLogin.set("contactMechId", gv.getString("contactMechId"));
	        	userLogin.set("isUdailyEnabled", "Y");
        	}
        	
        	if(UtilValidate.isNotEmpty(paramMap.get("type")) && ((String)paramMap.get("type")).equals("XPRESS")){
        		response.put("serviceType","activateXPRESS");
        		userLogin.set("isXpressEnabled", "Y");
        	}
        	
        	userLogin.store();
        	
	        if(UtilValidate.isNotEmpty(list)){
	            response.put("response", UtilMisc.toMap("status", "Success", "msg", "Your "+paramMap.get("type")+" account is activated"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "You Daily User not found"));
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/addToCart")
    public String addToCart(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","addToCart"); 
        	
        	EntityCondition enityCondition = null;
        	List<GenericValue> result = null;
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	//Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
        	
        	String autoSaveListId = ILinksAppUtil.getShoppingListId(paramMap, userLogin);//ShoppingListEvents.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"));
        	
        	List<Map<String,Object>> products = (List<Map<String,Object>>)paramMap.get("products");
        	if(UtilValidate.isNotEmpty(autoSaveListId)){
        		List shoppingCartItems = delegator.findList("ShoppingListItem", EntityCondition.makeCondition(UtilMisc.toMap("shoppingListId", autoSaveListId)), UtilMisc.toSet("productId", "quantity"), null, ILinksAppUtil.readonly, false);
        		if(UtilValidate.isNotEmpty(shoppingCartItems) && UtilValidate.isNotEmpty(products)){
        			products.addAll(shoppingCartItems);
        		}
        	}
        	
        	//Debug.log("\n ============================== \n autoSaveListId == "+autoSaveListId+"\n ============================== \n");
        	autoSaveListId = ILinksAppUtil.addToCart(UtilMisc.toMap("userLogin", userLogin, "shoppingListId", autoSaveListId, "shoppingListTypeId", "SLT_SPEC_PURP", "storeId", (String)paramMap.get("storeId"), "append", false, "items", (List<Map<String,Object>>)paramMap.get("products")));
        	//Debug.log("\n ============================== \n autoSaveListId 2222 == "+autoSaveListId+"\n ============================== \n");
        	if(UtilValidate.isEmpty(autoSaveListId)){
	            response.put("response", UtilMisc.toMap("status", "ERROR",  "cartType", "Youmart", "msg", "Error in saving the product in cart"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success",  "cartType", "Youmart", "msg", autoSaveListId));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR",  "cartType", "Youmart", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}

	@POST
	@Produces("application/json")
	@Path("/slot")
	public String slots(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
	    try{
	    	response.put("serviceType","slot");
	    	EntityCondition enityCondition = null;
	    	List<GenericValue> result = null;
	    	GenericValue userLogin = null;
	    	GenericValue productStore = null;
	    	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
	    		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
	    	}
	    	
	    	String currentmMechId = null;
	    	Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
	    	// get the currentMechId
	    	if(UtilValidate.isNotEmpty(paramMap.get("contactMechId"))){
	    		currentmMechId = (String)paramMap.get("contactMechId");
	    	}
	    	// get the partyId
	    	String partyId = userLogin.getString("partyId");
	    	
	    	// get the Product Store
	    	if(UtilValidate.isNotEmpty(paramMap.get("storeId"))){
	    		productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", paramMap.get("storeId")), true);
	        	if(UtilValidate.isEmpty(productStore)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Product Store not found"));
	        		return mapper.writeValueAsString(response);
	        	}
	    	}
	    	
	    	Timestamp now = UtilDateTime.nowTimestamp();
	    	
	    	
	    	int dayDiff = UtilDateTime.getIntervalInDays(UtilDateTime.getDayStart(now), UtilDateTime.toTimestamp((String)paramMap.get("date")+" 00:00:00"));
	    	Debug.log("\n------------------------------------\n date === "+UtilDateTime.toTimestamp((String)paramMap.get("date")+" 00:00:00")+"\n------------------------------------\n");
	    	Debug.log("\n------------------------------------\n UtilDateTime.getDayStart(now) === "+UtilDateTime.getDayStart(now)+"\n------------------------------------\n");
	    	Debug.log("\n------------------------------------\n dayDiff === "+dayDiff+"\n------------------------------------\n");
	    	if(dayDiff < 0){
	    		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Please enter the future date"));
        		return mapper.writeValueAsString(response);
	    	}
	    	
	    	List slots = new ArrayList();
	    	if(dayDiff == 0 || dayDiff == 1){
		    	Map mainMaptoday = new LinkedHashMap();
		    	Map mainMaptomorrow = new LinkedHashMap();
		    	
		    	int r = now.getHours();
		    	int minutes = now.getMinutes();
		    	minutes = 0;
		    	
		    	Timestamp partyDeliveryDate = null;
		
		    	//condition=request.getParameter("noCodition"); // TODO
		    	Calendar current = Calendar.getInstance();
		    	SimpleDateFormat ft = new SimpleDateFormat();
		    	Double cutOffGvtem= 0d;
				Double blockTime= 0d;
				Long slotDayOption=0l;
				Long maxInterval=0l;
				//int cutOffGv=0;
				//int blokingTime=0;
		    	if(UtilValidate.isNotEmpty(productStore)){
		    		cutOffGvtem=productStore.getDouble("cutOffTime");
		    		blockTime=productStore.getDouble("blockingTime");
		    		slotDayOption=productStore.getLong("slotDayOption");
		    		maxInterval=productStore.getLong("slotMaxInterval");
		    		//cutOffGv=(int)cutOffGvtem;
		    		//blokingTime=(int)blockTime;
		    	}
		    	
		    	//for checking order merge
		    	List CondnList=FastList.newInstance();
		    	List contactOrderList=FastList.newInstance();
		    	CondnList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
		    	CondnList.add(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"));
		    	CondnList.add(EntityCondition.makeCondition("orderId",EntityOperator.NOT_EQUAL,null));
		    	CondnList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, currentmMechId));
		    	CondnList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
		    	List<GenericValue> OrderCollision=delegator.findList("OrderSlot",EntityCondition.makeCondition(CondnList,EntityOperator.AND),null,null,null,false);
		        String name = "";
		
		        List<GenericValue> orderDetailList = new ArrayList();
		    	if(UtilValidate.isNotEmpty(OrderCollision) ){ //&& UtilValidate.isEmpty(condition) 
		    		List OrderListTem=EntityUtil.getFieldListFromEntityList(OrderCollision, "orderId", true);
		    		GenericValue GVTemp=EntityUtil.getFirst(OrderCollision);
		    		GenericValue partyNameGv=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",GVTemp.getString("partyId")));
		    		if(UtilValidate.isNotEmpty(partyNameGv.getString("firstName")) && UtilValidate.isNotEmpty(partyNameGv.getString("lastName"))){
		    			name=partyNameGv.getString("firstName")+" "+partyNameGv.getString("lastName");
		    		}else{
		    			name=partyNameGv.getString("firstName");
		    		}
		    	   //context.partyName=name;
		    	   
		    		List orderCondition=new ArrayList();
		    		List statusList =  new ArrayList();
		    		statusList.add("ORDER_APPROVED");
		    		statusList.add("ORDER_CREATED");
		    		orderCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,OrderListTem));
		    		orderCondition.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,statusList));
		    		orderDetailList=delegator.findList("OrderHeader",EntityCondition.makeCondition(orderCondition,EntityOperator.AND),null,UtilMisc.toList("orderDate DESC"),null,false);
		    	}
		    	 //Debug.log("\n ============================== \n Order Detials == "+orderDetailList+"\n ============================== \n");
		    	if(orderDetailList != null && orderDetailList.size()>0){
		    		List CondnList1= EntityUtil.getFieldListFromEntityList(orderDetailList, "slot", true);
		    		List CondnList2=EntityUtil.getFieldListFromEntityList(orderDetailList, "deliveryDate", true);
		    		Iterator<String> itrdsd = CondnList1.iterator();
		    		int count = 0 ;
		    		String slottype = null;
		    		List<GenericValue> OrderSlotType = null;
		    		GenericValue gv = null;
	    			while(itrdsd.hasNext()){ 
	    				slottype = itrdsd.next(); 
		    			if(UtilValidate.isNotEmpty(slottype)){
		    				EntityCondition cond = EntityCondition.makeCondition(EntityCondition.makeCondition("slotType",EntityOperator.EQUALS,slottype),
		    						EntityOperator.AND,EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,null));
		    				OrderSlotType = delegator.findList("OrderSlotType",cond,null,null,null,false);
		    				if(UtilValidate.isNotEmpty(OrderSlotType)){
		    					Iterator<GenericValue> itrdsd2 = OrderSlotType.iterator();
		    					while(itrdsd2.hasNext()){
		    						gv = itrdsd2.next();
		    						Integer dateOfTime=gv.getInteger("cutOffTime");
		    						String timePeriod = gv.getString("timePeriod");
		    						String orderDate = "";//(String)CondnList2.get(count);
		    						orderDate = orderDate.split(" ")[0];
		    						SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH");
		    						orderDate = orderDate+" "+ (timePeriod.equals("PM") ? (12+dateOfTime) : (dateOfTime.toString().length() != 2 ? ("0"+dateOfTime):dateOfTime) );
		    						Date deliveryDate = simpleDateFormat.parse(orderDate);
		    						Date todayDate = current.getTime();	
		    						
		    						if(todayDate.before(deliveryDate)){ 
		    							// 
		    						} else{
		    					 	//GenericEntity removeGV = orderDetailList.get(count) ;
		    					 	orderDetailList.remove(count);
		    						}
		    					} 	
		    				}
		    			}
		    			count++;
	    			}
		    	  
		    	}
		    	  
		    	// Set the Max Orders
	    		List<GenericValue> maxDelivery= CheckOutEvents.getAllSlots(delegator);
	    		List orderType=new ArrayList();
		    	List blockingDays = new ArrayList();
		    	Map slotTime = new java.util.HashMap();
		    	Map cutOffTimeSlotWise = new java.util.HashMap();
		    	Map cutOffTimeSlotWiseInMinute = new java.util.HashMap();
		
		    	List MaxDeliveryList=new ArrayList();
		    	if(UtilValidate.isNotEmpty(maxDelivery)){
		    		for(int i=0;i<maxDelivery.size();i++){
		    	  	   GenericValue gv = maxDelivery.get(i);
		    	  	   orderType.add(i,gv.get("slotType"));
		    	  	   blockingDays.add(i,gv.get("blockDays"));
		    	  	   MaxDeliveryList.add(i,gv.getDouble("maxDelivery").intValue());
		    	  	   cutOffTimeSlotWise.put(gv.get("slotType"),gv.get("cutOffTimeInHour"));
		    	  	   slotTime.put(gv.get("slotType"), gv.get("slotTiming"));
		    	  	   cutOffTimeSlotWiseInMinute.put(gv.get("slotType"),gv.get("cutOffTimeInMinute"));
		    		}
		    	}
		    	  //checking the condition
		    	if(UtilValidate.isNotEmpty(maxDelivery)){ //maxDelivery getting all the slot for the day,which they provide the service.EX:-SLOT1,SLOT2,SLOT3
		    		Map listCount = new HashMap();
		    		for(int j=0;j<slotDayOption;j++){		// for many days to get the service,this will be store setting
		    	  	   //Map tem=new LinkedHashMap();
		    			Timestamp deliveryDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),j);
	    	  		   	for(int t = 0; t<orderType.size(); t++) {
	    	  		   		listCount.put(t,0);
	    	  		   	}
	    	  		   	
	    	  		   	for(int t = 0; t < orderType.size(); t++){
		    	  		   List promoConditions = new ArrayList();
		    	  		   promoConditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.EQUALS,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),j)));
		    	  		   List slotStatus  =  new ArrayList();
		    	  		 
		    	  		   promoConditions.add(EntityCondition.makeCondition("slotStatus", EntityOperator.NOT_EQUAL,"SLOT_COMPLETED"));
		    	  		   promoConditions.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS,orderType.get(t)));
		    	   		   List ordersList1= delegator.findList("OrderSlot", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), null,UtilMisc.toList("orderId DESC"), null, false);
		    	   		   //String[] ids ={"};
		    	   		   List<String> ids = new ArrayList<String>();
		    	   		   ids.add("ORDER_APPROVED");ids.add("ORDER_COMPLETED");ids.add("ORDER_DISPATCHED");
		    	   		   List orderIds = EntityUtil.getFieldListFromEntityList(ordersList1, "orderId", true);
		    	   		   promoConditions.clear();
		    	   		   promoConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIds));
		    	   		   
		    	   		   promoConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,ids));
		    	   		   
		    	   		   List temp = delegator.findList("OrderHeader", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), UtilMisc.toSet("orderId"),null, null, false);
		    	   		   
		    	   		   List temp1 = EntityUtil.getFieldListFromEntityList(temp, "orderId", true);
		    	   		  
		    	   		   List temp2 = EntityUtil.filterByCondition(ordersList1, EntityCondition.makeCondition("orderId",EntityOperator.NOT_IN,temp1));
		    	   		  
		                   List ordersList2 = new ArrayList();
		    	   		   if(temp2.size()>0){
		    	   			   ordersList2 = EntityUtil.getFieldListFromEntityList(temp2,"contactMechId", true);
		    	   		   }else{
		    	   			   ordersList2=EntityUtil.getFieldListFromEntityList(ordersList1, "contactMechId", true);
		    	   		   }
		    	   		   if(UtilValidate.isNotEmpty(ordersList1)){
		    	   			   listCount.put(t,ordersList2.size());   
		    	   		   }
		    	   		//Debug.log("\n ============================== \n temp List 1 == "+ordersList1+"\n ============================== \n");
	    	  		   	} 
		    	  		   
		    	  	//	if(UtilValidate.isNotEmpty(condition)){/**/}
		    	  	//else  {
	    	  		   	Map tem = new LinkedHashMap();
	    	  			 for(int r1=0; r1<orderType.size(); r1++){
	    	  				Double cutOffGvNew = (Double)cutOffTimeSlotWise.get(orderType.get(r1));
	    	  				Double cutOffGvNewInMinute = (Double)cutOffTimeSlotWiseInMinute.get(orderType.get(r1));
	    	  				boolean blocked = false;
						    String blockDays = (String)blockingDays.get(r1);
						    int maxDelCount = (Integer)MaxDeliveryList.get(r1);
						    if(UtilValidate.isNotEmpty(blockDays)){
						    	try{
						    		String[] blockDay  = blockDays.split(","); 
						    		String bit = blockDay[j];
								
						    		String[] blockTimings  = bit.split("~~");
						    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						    		Timestamp start = null;
						    		Timestamp end = null;
							
									if (UtilValidate.isNotEmpty(blockTimings[0]) && UtilValidate.isNotEmpty(blockTimings[1])) {
										Date d = (Date) sdf.parse(blockTimings[0]);
										start = new Timestamp(d.getTime());
										Date d1 = (Date) sdf.parse(blockTimings[1]);
										end = new Timestamp(d1.getTime());
											
										Calendar cal = Calendar.getInstance();
										cal.setTime(now);
										cal.add(Calendar.DAY_OF_WEEK, j);
										now.setTime(cal.getTime().getTime());
											
										if( now.after(start)  && now.before(end)){
											blocked = true;
										}
									}
						    	}catch(Exception e){
						    		e.printStackTrace();
						    	}
						    }
						    if(j==0){
								if(r < cutOffGvNew && minutes <= cutOffGvNewInMinute && ((Integer)listCount.get(r1) < (Integer)MaxDeliveryList.get(r1))){
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Available", "timing", slotTime.get(orderType.get(r1))));
								}else{
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
								}
								if(blocked){
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
								}
								if("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1))){
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
								}
							}else{
								if((Integer)listCount.get(r1)< (Integer)MaxDeliveryList.get(r1)){
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Available", "timing", slotTime.get(orderType.get(r1))));
								}else{
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
							   	}
								if(j == 1 && ("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1)))){
									if(r < cutOffGvNew && minutes <= cutOffGvNewInMinute && (Integer)listCount.get(r1)< (Integer)MaxDeliveryList.get(r1)){
							 			tem.put(orderType.get(r1),UtilMisc.toMap("status", "Available", "timing", slotTime.get(orderType.get(r1))));
						 			}else{
										tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
						 			}
							 	}
								if(blocked){
									tem.put(orderType.get(r1),UtilMisc.toMap("status", "Blocked", "timing", slotTime.get(orderType.get(r1))));
								}
							}
						    		   
	    	  			}
	    	  			//for()
	    	  			List newSlot = new ArrayList();
	    	  			Map valueMap = new HashMap();
	    	  			Iterator entries = tem.entrySet().iterator();
	    	  			while (entries.hasNext()) {
	    	  			    Map.Entry entry = (Map.Entry) entries.next();
	    	  			    valueMap = (Map)entry.getValue();
	    	  			    valueMap.put("key", (String)entry.getKey());
	    	  			    newSlot.add(valueMap);
	    	  			    //System.out.println("Key = " + key + ", Value = " + valueMap);
	    	  			}
	    	  			slots.add(UtilMisc.toMap(deliveryDate+"", newSlot));
	    	  			Debug.log("\n ============================== \n "+deliveryDate+" == "+tem+"\n ============================== \n");	
	    	  		   	 
	    	  		   	
		    		} // if block closes
	    	  
		    	}
	    	}else{
	    		paramMap.remove("username");
	    		paramMap.put("userLogin", userLogin);
	    		slots = ILinksAppUtil.slotSelection(paramMap);
	    	}
	    	response.put("response", UtilMisc.toMap("status", "success", "msg", slots));          	
	    } catch (Exception e) {
	    	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
	    	e.printStackTrace();
	    }
	    
	    return mapper.writeValueAsString(response);
	}
	
	/*
	 * This method is used to validate the slot is available for the current date 
	 *
	 */
	@POST
    @Produces("application/json")
	@Path("/slotavailable")
	public String slotsavailable(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if (UtilValidate.isEmpty(paramMap)) {
			response.put("response",
					UtilMisc.toMap("status", "ERROR", "msg", "No input found"));
			return mapper.writeValueAsString(response);
		}
		if (UtilValidate.isEmpty(paramMap.get("slotId")) || UtilValidate.isEmpty(paramMap.get("sotreId"))
				|| UtilValidate.isEmpty(paramMap.get("date"))) {
			response.put("response",
					UtilMisc.toMap("status", "ERROR", "msg", "Invalid Parameters"));
			return mapper.writeValueAsString(response);
		}
	
		String slotValue = "";
		String productStoreId = "";
		String orderDate = "";
		
		if (UtilValidate.isNotEmpty(paramMap.get("storeId"))) {
			productStoreId = (String) paramMap.get("storeId");
		}
		
		if (UtilValidate.isNotEmpty(paramMap.get("slotId"))) {
			slotValue = (String) paramMap.get("slotId");
		}
		
		if (UtilValidate.isNotEmpty(paramMap.get("date"))) {
			orderDate = (String) paramMap.get("date");
		}
        
		orderDate = orderDate+" "+"00:00:00";
		
		String slotAvailableResp = "";
		try {
			response.put("serviceType", "slotavailable");
			List<GenericValue> maxDelivery = CheckOutEvents.getAllSlots(delegator);
			
			Map MaxDeliveryList = new HashMap();
			Map cutOfTimeMap = new HashMap();
			// iterate through maxDelivery
			if (!UtilValidate.isEmpty(maxDelivery)) {
				for (int i = 0; i < maxDelivery.size(); i++) {
					GenericValue gv = maxDelivery.get(i);
					MaxDeliveryList.put((String) gv.get("slotType"), gv
							.getDouble("maxDelivery").intValue());
					cutOfTimeMap.put((String) gv.get("slotType"), gv
							.getDouble("cutOffTimeInHour").intValue());
				}
			}
			
			if (!UtilValidate.isEmpty(maxDelivery)) {
				
				java.sql.Timestamp orderDeliveryDate = java.sql.Timestamp.valueOf(orderDate);
				Timestamp currentDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),0);
				if(orderDeliveryDate.before(currentDate)){
					response.put("response",
							UtilMisc.toMap("status", "ERROR", "msg", "Order Date should not be less than current date"));
					return mapper.writeValueAsString(response);
				}

				if(orderDeliveryDate.after(UtilDateTime.addDaysToTimestamp(currentDate,7))){
					response.put("response",
							UtilMisc.toMap("status", "ERROR", "msg", "Order date should not be greater then currentdate + 7 days"));
					return mapper.writeValueAsString(response);
				}
				
				List slotCheckings = new ArrayList();
				slotCheckings.add(EntityCondition.makeCondition("deliveryDate",
						EntityOperator.EQUALS, orderDeliveryDate));

				slotCheckings.add(EntityCondition.makeCondition("slotType",
						EntityOperator.EQUALS, slotValue));
				List ordersList1 = delegator.findList("OrderSlot",
						EntityCondition.makeCondition(slotCheckings,
								EntityOperator.AND), null, UtilMisc
								.toList("orderId DESC"), null, false);
				List tempSlotTypes = EntityUtil.getFieldListFromEntityList(ordersList1, "slotType", true);

				Integer slotcheck = (Integer) MaxDeliveryList.get(slotValue);
				Integer cutoftime = (Integer)cutOfTimeMap.get(slotValue);
				int currentHours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				Integer slot12cutoftime = (Integer)cutOfTimeMap.get("SLOT1");
				Debug.log("\n ============================== \n cutoftime == " + cutoftime + "\n ============================== \n");
				Debug.log("\n ============================== \n currentHours == " + currentHours + "\n ============================== \n");
				
				if(UtilDateTime.getIntervalInDays(currentDate, orderDeliveryDate)==0){
					// Where SLOT TYPE IS SLOT1 and SLOT2
					if(( slotValue.equals(SLOT1) || slotValue.equals(SLOT2) )){
							slotAvailableResp = "Not Available";
					}
					
					// FOR SLOT4 AND SLOT5
					if(( slotValue.equals(SLOT4) || slotValue.equals(SLOT5) ) && (currentHours>=cutoftime && currentHours< slot12cutoftime)){
						if (ordersList1.size() < slotcheck ) {
							slotAvailableResp = "Available";
						} else {
							slotAvailableResp = "Not Available";
						}
						
					}else{
						slotAvailableResp = "Not Available";
						Debug.log("\n ============================== \n ZERO DAYS == " + "ZERO DAYS  88" + "\n ============================== \n");
					}
					
				}else if(UtilDateTime.getIntervalInDays(currentDate, orderDeliveryDate)==1){
					if(( slotValue.equals(SLOT1) || slotValue.equals(SLOT2) ) && (currentHours<=cutoftime-1)){
						
						if (ordersList1.size() < slotcheck ) {
							slotAvailableResp = "Available";
						} else {
							slotAvailableResp = "Not Available";
						}
					}
					if(( slotValue.equals(SLOT1) || slotValue.equals(SLOT2) ) && (currentHours>cutoftime)){						
							slotAvailableResp = "Not Available";
					}
					

					if((slotValue.equals(SLOT4) || slotValue.equals(SLOT5))){
						if (ordersList1.size() < slotcheck ) {
							slotAvailableResp = "Available";
						} else {
							slotAvailableResp = "Not Available";
						}
					}
					
					
					
					
				}else if(UtilDateTime.getIntervalInDays(currentDate, orderDeliveryDate)>1){
					
					if (ordersList1.size() < slotcheck ) {
						slotAvailableResp = "Available";
					} else {
						slotAvailableResp = "Not Available";
					}
					
				}
				
			} // if block closes

			response.put("response", UtilMisc.toMap("status", "success", "msg",
					slotAvailableResp));
		} catch (Exception e) {
			response.put("response",
					UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
			e.printStackTrace();
		}

		return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/search")
    public String search(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		if(UtilValidate.isEmpty(paramMap.get("storeId"))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Store Id not found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","search"); 
        	GenericValue productStore = delegator.findOne("ProductStore", true, UtilMisc.toMap("productStoreId", paramMap.get("storeId")));
        	EntityCondition enityCondition = ILinksAppUtil.searchCondition((String)paramMap.get("queryStr"), productStore.getString("showOutOfStockInSearch"), "");
        	
        	List<GenericValue> result = delegator.findList("Product", enityCondition, UtilMisc.toSet("productId"), null, findOptions, false);;
        	
        	List list = ILinksAppUtil.getProducts(EntityUtil.getFieldListFromEntityList(result, "productId", true), paramMap);
        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "No result found"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", list.subList(0, (list.size() - 1)), "pagination", list.get(list.size() - 1) ));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/getCart")
    public String getCart(String json) throws Exception {
		String resource_error = "OrderErrorUiLabels";
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","getCart"); 
        	Map<String, Object> result = FastMap.newInstance();
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        		paramMap.put("userLogin", userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	if(UtilValidate.isEmpty(paramMap.get("storeId"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "storeId not found"));
        		return mapper.writeValueAsString(response);
        	}
        	
        	if(UtilValidate.isNotEmpty(paramMap.get("type")) && ((String)paramMap.get("type")).equalsIgnoreCase("ALL")){
        		
        		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", userLogin.getString("partyId"),"productStoreId", paramMap.get("storeId"), "shoppingListTypeId", "SLT_UDAILY_PRODUCT"));
        		List<GenericValue> shoppingList = delegator.findList("ShoppingList",EntityCondition.makeCondition(entityCondition, EntityOperator.AND, EntityCondition.makeCondition("lastOrderedDate", EntityOperator.NOT_EQUAL, null)) , UtilMisc.toSet("lastOrderedDate", "shoppingListId"), UtilMisc.toList("lastOrderedDate"), findOptions, false);
        		
        		List<GenericValue> items = delegator.findList("ShoppingListItem", EntityCondition.makeCondition("shoppingListId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(shoppingList, "shoppingListId", true)), null, null, ILinksAppUtil.readonly, false);
        		entityCondition = EntityCondition.makeCondition("shoppingListId", EntityOperator.IN,  EntityUtil.getFieldListFromEntityList(items, "shoppingListId", true));
        		shoppingList = EntityUtil.filterByCondition(shoppingList, entityCondition);
        		if(UtilValidate.isNotEmpty(shoppingList)){
            		response.put("response", UtilMisc.toMap("status", "success", "msg", EntityUtil.getFieldListFromEntityList(shoppingList, "lastOrderedDate", true)));
            		return mapper.writeValueAsString(response);
            	}
        	}
        	
        	String autoSaveListId = ILinksAppUtil.getShoppingListId(paramMap, userLogin);
        	
            GenericValue shoppingList = null;
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            if (shoppingList == null) {
            	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Cart not found"));
        		return mapper.writeValueAsString(response);
            }
            paramMap.put("shoppingList", shoppingList);
            result = ILinksAppUtil.getCart(paramMap);

        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "success",  "cartType", "Youmart", "msg", "Cart is empty"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "cartType", "Youmart", "msg", result.get("result"), "summary", result.get("cartSummary"), "savings", result.get("savings")));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR",  "cartType", "Youmart", "msg", e.getMessage()));
        	e.printStackTrace();
        }
        
        return mapper.writeValueAsString(response);
	}

	@POST
    @Produces("application/json")
	@Path("/updateCart")
    public String updateCart(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","updateCart"); 
        	
        	Map<String, Object> result = null;
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        		paramMap.put("userLogin",userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("type"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Operation type missing"));
        		return mapper.writeValueAsString(response);
        	}
        	
        	if(UtilValidate.isNotEmpty(paramMap.get("scheduledDate")) && "ALL".equalsIgnoreCase((String)paramMap.get("scheduledDate"))){
        		Debug.log("\n\n scheduledDate == ALL \n\n");
        		if(((String)paramMap.get("type")).equalsIgnoreCase("clear")){
        			Debug.log("\n\n clear == clear \n\n");
        			//Map<String, Object> findMap = UtilMisc.<String, Object>toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", shoppingListTypeId, "listName", listName)
        			EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("lastOrderedDate", EntityOperator.NOT_EQUAL, null), 
        													EntityOperator.AND,
        													EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId"))) ;
        			List<GenericValue> shlist = delegator.findList("ShoppingList", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", (String)paramMap.get("storeId")))), null, null, ILinksAppUtil.readonly, false);
        			for(GenericValue gv : shlist){
        				ILinksAppUtil.updateQtyR(gv);
        				Debug.log("\n\n gv == "+gv.getString("shoppingListId")+" \n\n");
        				ShoppingListEvents.clearListInfo(delegator, gv.getString("shoppingListId"));
        				//gv.remove();
        			}
             	}
        		response.put("response", UtilMisc.toMap("status", "success", "msg", "Cart is empty"));
        		return mapper.writeValueAsString(response);
        	}
        	String autoSaveListId = ILinksAppUtil.getShoppingListId(paramMap, userLogin);//ShoppingListEvents.getAutoSaveListId(delegator, dispatcher, null, userLogin, (String)paramMap.get("storeId"));
        	
        	Debug.log("\n\n !!!!!!!!!!!!!!!!!! autoSaveListId == "+autoSaveListId+"\n\n");
    		List<GenericValue> shoppingListItems = null;
    		GenericValue shoppingList = null;
    		shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
         	if (shoppingList == null) {
         		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Cart not found"));
        		return mapper.writeValueAsString(response);
         	}
         	
         	
         	if(((String)paramMap.get("type")).equalsIgnoreCase("update")){
	     		shoppingListItems = shoppingList.getRelatedByAnd("ShoppingListItem", UtilMisc.toMap("productId", paramMap.get("productId")));
	     		if (UtilValidate.isEmpty(shoppingListItems)) {
	         		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Product not found : Product Id = "+ paramMap.get("productId")));
	        		return mapper.writeValueAsString(response);
	         	}
	     		GenericValue gv = null;
	     		String qty = (String)paramMap.get("quantity");
	     		if(UtilValidate.isNotEmpty(qty) && UtilValidate.isInteger(qty)){
	     			gv = EntityUtil.getFirst(shoppingListItems);
	     			if(qty.equals("0")){
	     				gv.remove();
	     				ILinksAppUtil.updateQtyRemove(gv.getString("shoppingListId"), gv.getString("productId"), userLogin);
	     			}else{
	     				gv.set("quantity", new BigDecimal(qty));
	     				gv.store();
	     				ILinksAppUtil.updateQty(gv.getString("shoppingListId"), gv.getString("productId"), userLogin);
	     			}
	     		}
	     		paramMap.put("shoppingList", shoppingList);
	     		result = ILinksAppUtil.getCart(paramMap);
         	}
         	
         	if(((String)paramMap.get("type")).equalsIgnoreCase("clear")){
         		if(UtilValidate.isNotEmpty(paramMap.get("scheduledDate"))){
					GenericValue gv = delegator.findOne("ShoppingList", false, UtilMisc.toMap("shoppingListId", autoSaveListId));
	         		ILinksAppUtil.updateQtyR(gv);
         		}
         		ShoppingListEvents.clearListInfo(delegator, autoSaveListId);
         	}
         	
         	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "success", "msg", "Cart is empty"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result.get("result"), "summary", result.get("cartSummary")));
	        }
        	
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR",  "cartType", "Youmart", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/cart")
    public String cart(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		try{
        	response.put("serviceType","cartcount"); 
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	String autoSaveListId = ILinksAppUtil.getShoppingListId(paramMap, userLogin);
        	
            GenericValue shoppingList = null;
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            if (shoppingList == null) {
            	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Cart not found"));
        		return mapper.writeValueAsString(response);
            }
            
            response.put("response", UtilMisc.toMap("status", "success", "count", shoppingList.getRelated("ShoppingListItem").size()));
            
		} catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR",  "cartType", "Youmart", "msg", e.getMessage()));
        	e.printStackTrace();
        }
		return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/address")
    public String address(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","address"); 
        	
        	EntityCondition enityCondition = null;
        	List result = null;
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("type"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Operation type missing"));
        		return mapper.writeValueAsString(response);
        	}
        	
        	if(((String)paramMap.get("type")).equalsIgnoreCase("get")){
        		result = ILinksAppUtil.getAddress(userLogin.getString("partyId"));
        	}
        	
        	if(((String)paramMap.get("type")).equalsIgnoreCase("update")){
        		paramMap.put("partyId", userLogin.getString("partyId"));
        		result = UtilMisc.toList(ILinksAppUtil.updateAddress(paramMap));
        	}
        	
        	
        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in saving the address"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/rating")
    public String rating(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","rating"); 
        	
        	EntityCondition enityCondition = null;
        	GenericValue result = null;
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("type"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Operation type missing"));
        		return mapper.writeValueAsString(response);
        	}
        	
        	if(((String)paramMap.get("type")).equalsIgnoreCase("set")){
        		result = delegator.findOne("PartyAttribute", false, UtilMisc.toMap("partyId", userLogin.getString("partyId"), "attrName","RATING"));
        		if(UtilValidate.isEmpty(result)){
        			result = delegator.create("PartyAttribute", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "attrName","RATING", "attrValue", paramMap.get("rating")));
        		}else{
        			result.set("attrValue", paramMap.get("rating"));
        			result.store();
        		}
        		
        	}
        	
        	if(((String)paramMap.get("type")).equalsIgnoreCase("get")){
        		result = delegator.findOne("PartyAttribute", true, UtilMisc.toMap("partyId", userLogin.getString("partyId"), "attrName","RATING"));
        	}
        	
        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in saving the rating"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result.getAllFields()));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/notify")
    public String notify(String json) throws Exception {
        try{
        	response.put("serviceType","rating"); 
        	
        	// Push notification service comes here.
        	
        	
            response.put("response", UtilMisc.toMap("status", "success", "msg", "Welcome to YouMart.in"));
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/contactus")
    public String contactus(String json) throws Exception {
        try{
        	response.put("serviceType","rating"); 
        	
        	// Push notification service comes here. MySalesOrders
        	Map result = UtilMisc.toMap("Phone", " (080) 45 45 88 88", "EmailId", "customersupport@youmart.in");
        	
            response.put("response", UtilMisc.toMap("status", "success", "msg", result));
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/daterange")
    public String daterange(String json) throws Exception {
        try{
        	response.put("serviceType","daterange"); 
        	List date = FastList.newInstance();
        	Timestamp now = UtilDateTime.nowTimestamp();
        	date.add((now+"").split(" ")[0]);
        	String days = UtilProperties.getPropertyValue(resource, "schedule.dates", "8");
        	for(int i = 1; i < Integer.parseInt(days); i++){
        		date.add((UtilDateTime.addDaysToTimestamp(now, i)+"").split(" ")[0]);
        	}
        	
        	if(UtilValidate.isEmpty(date)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in getting the dates"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", date));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/filter")
    public String filter(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap) ){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		
		if(UtilValidate.isEmpty(paramMap.get("categoryId")) && (UtilValidate.isEmpty(paramMap.get("queryStr"))  && UtilValidate.isEmpty(paramMap.get("storeId")))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Invalid Parameters"));
			return mapper.writeValueAsString(response);
		}
		
        try{
        	List list = new ArrayList();
        	response.put("serviceType","filter"); 
        	
        	String categoryId = (String) paramMap.get("categoryId");
        	String storeId = (String) paramMap.get("storeId");
        	String queryString = (String )paramMap.get("queryStr");
        	
//        	if(UtilValidate.isEmpty(categoryId)){
//    			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Category Id is empty"));
//    			return mapper.writeValueAsString(response);
//    		}
        	EntityCondition entityCondition = null;
        	
        	if(UtilValidate.isNotEmpty(categoryId) && UtilValidate.isEmpty(storeId) && UtilValidate.isEmpty(queryString)){
	        	entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, categoryId));
	    		list = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition) , UtilMisc.toSet("productCategoryId"), UtilMisc.toList("sequenceNum"), findOptions, true);
	        	if(UtilValidate.isEmpty(list)){
	        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
	        	}else{
	        		entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "productCategoryId", true)));
	        	}
	        	response.put("serviceType","filter_"+categoryId);
	        	list = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), null, null, findOptions, true);
        	}
        	else if(UtilValidate.isEmpty(categoryId) && UtilValidate.isNotEmpty(storeId) && UtilValidate.isNotEmpty(queryString)){
        		GenericValue productStore = delegator.findOne("ProductStore", true, UtilMisc.toMap("productStoreId", paramMap.get("storeId")));
            	entityCondition = ILinksAppUtil.searchCondition((String)paramMap.get("queryStr"), productStore.getString("showOutOfStockInSearch"), "");            	
            	response.put("serviceType","filter_"+storeId);
            	//List<GenericValue> 
            	list = delegator.findList("Product", entityCondition, UtilMisc.toSet("productId"), null, findOptions, false);;
            	
            	//list = ILinksAppUtil.getProducts(EntityUtil.getFieldListFromEntityList(result, "productId", true), paramMap);
        	}
        	
        	
        	//list = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(entityCondition, EntityOperator.AND, dateCondition), null, null, findOptions, true);
        	
        	List brandNames = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(list, "productId", true)), UtilMisc.toSet("brandName"), UtilMisc.toList("brandName"), findOptions, true);
        	java.util.List filterKeys = new java.util.ArrayList();
        	filterKeys.add("Less than Rs 20 ");
        	filterKeys.add("Rs 21 to 50 ");
        	filterKeys.add("Rs 51 to 100 ");
        	filterKeys.add("Rs 101 to 200 ");
        	filterKeys.add("Rs 201 to 500 ");
        	filterKeys.add("More than Rs 501 ");
        	
        	List result = UtilMisc.toList(UtilMisc.toMap("Price Range", filterKeys),UtilMisc.toMap("Brand Names", EntityUtil.getFieldListFromEntityList(brandNames, "brandName", true)),UtilMisc.toMap("Availability", UtilMisc.toList("Available")));
        	
        	if(UtilValidate.isEmpty(result)){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in getting the dates"));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result));
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	/*
	@POST
    @Produces("application/json")
	@Path("/promotions")
	public String ItemPromotions(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if (UtilValidate.isEmpty(paramMap)) {
			response.put("response",
					UtilMisc.toMap("status", "ERROR", "msg", "No input found"));
			return mapper.writeValueAsString(response);
		}
		GenericValue userLogin = null;
    	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
    		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        	if(UtilValidate.isEmpty(userLogin)){
        		//userLogin.
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
        		return mapper.writeValueAsString(response);
        	}
    	}
    	//BigDecimal totalQuantity  = new0;
    	//if(UtilValidate.isNotEmpty(paramMap.get("quantity"))){
    	//	totalQuantity = (BigDecimal) paramMap.get("quantity");
    	//}
    	String partyId = userLogin.getString("partyId");
    	String productStoreId = (String)paramMap.get("storeId");
    	
		String productPromoCodeId ="FM10"; //TODO
		ShoppingCart cart = new ShoppingCart(delegator, productStoreId, null, Locale.getDefault(), null);
		//cart.getPro
		try {
			String autoSaveListId = ILinksAppUtil.getShoppingListId(paramMap, userLogin);
        	
            GenericValue shoppingList = null;
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            
            
			ProductPromotion productPromotion = new ProductPromotion(shoppingList, UtilMisc.toMap("partyId", partyId, "productStoreId", productStoreId, "userLogin", userLogin));
			String checkPromoCode = productPromotion.checkCanUsePromoCode(productPromoCodeId, partyId,delegator, productStoreId, Locale.getDefault());
			
			if (checkPromoCode == null) {
	            
				//ILinksAppUtil.getCart(shoppingList);
	            productPromotion.doPromotions(cart, null,dispatcher);
	            return null;
	        } else {
	            return checkPromoCode;
	        }
			
		} catch (Exception e) {
			response.put("response",
					UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
			e.printStackTrace();
		}
		response.put("response", UtilMisc.toMap("status", "success", "msg", "Promotions added successfully"));
		return mapper.writeValueAsString(response);
	}*/
	
	@POST
    @Produces("application/json")
	@Path("/resendOTP")
    public String resendOTP(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","resendOTP"); 
        	
        	EntityCondition enityCondition = null;
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	Map result = dispatcher.runSync("userRegistrationSmsService", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "phoneNo", (String)paramMap.get("mobileNumber"), "isMobile","Y", "uniqCode", userLogin.getString("uniqCode")));
    		Debug.log("\n\n result == "+result+"\n\n");
        	
        	
        	if(UtilValidate.isNotEmpty(result) && ServiceUtil.isSuccess(result)){
        		response.put("response", UtilMisc.toMap("status", "success", "msg", result));
	        }else{
	        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in sending SMS"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/forgotPassword")
    public String forgotPassword(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","forgotPassword"); 
        	
        	if(UtilValidate.isEmpty(paramMap.get("storeId"))){
    			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Store Id not found"));
    			return mapper.writeValueAsString(response);
    		}
        	if(UtilValidate.isEmpty(paramMap.get("username"))){
    			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","username not found"));
    			return mapper.writeValueAsString(response);
    		}
        	
        	EntityCondition enityCondition = null;
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isNotEmpty(ILinksAppUtil.emailPassword(paramMap))){
        		response.put("response", UtilMisc.toMap("status", "success", "msg", "Mail sent"));
	        }else{
	        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Error in sending Forgot Password email"));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/youdaily")
    public String youdaily(String json) throws Exception {
        try{
        	response.put("serviceType","daterange"); 
        	
            response.put("response", UtilMisc.toMap("status", "success", "msg", "Running short of Bread, Egg, Batter, Cheese or butter for Breakfast!! No worries... .coz YouDaily is here to get you those products you need early morning by 7am every day. YouDaily is a special initiative of YouMart where we bring you all the products that you need to consume for you breakfast & also guaranteed fresh cut vegetables, ready to be cooked to pack for your lunch boxes."));
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
	
	@POST
    @Produces("application/json")
	@Path("/updateStatus")
    public String updateStatus(String json) throws Exception {
		Debug.log("\n ============================== \n json == "+json+"\n ============================== \n");
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		List list = null;
		boolean result = false;
        try{
        	Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), false);
        		paramMap.put("userLogin", userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	if(UtilValidate.isEmpty(paramMap.get("orderId"))){
        		response.put("response",UtilMisc.toMap("status", "ERROR", "msg","OrderId is empty"));
    			return mapper.writeValueAsString(response);
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("status"))){
        		response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Status value is empty"));
    			return mapper.writeValueAsString(response);
        	}
        	
        	if(((String)paramMap.get("status")).equalsIgnoreCase("approve")){
        		ILinksAppUtil.addCouponProductsToCart(paramMap);
        		result = OrderChangeHelper.approveOrder(dispatcher, userLogin, (String)paramMap.get("orderId"), false);
        		if(result && UtilValidate.isNotEmpty(paramMap.get("transactionId"))){
        			GenericValue gv = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", paramMap.get("orderId")));
        			gv.set("transactionId", paramMap.get("transactionId"));
        			gv.store();
        		}
        		System.out.println("\n\n result == "+result+"\n\n");
        	}
        	if(((String)paramMap.get("status")).equalsIgnoreCase("reject")){
        		userLogin = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", "system"));
        		result = OrderChangeHelper.rejectOrder(dispatcher, userLogin, (String)paramMap.get("orderId"));
        	}
        	if(!result){
	            response.put("response", UtilMisc.toMap("status", "ERROR", "msg", result));
	        }else{
	            response.put("response", UtilMisc.toMap("status", "success", "msg", result));
	        }
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}

	@POST
    @Produces("application/json")
	@Path("/invitefriend")
    public String inviteFriend(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		response.put("serviceType","invitefriend");
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		try{
			if(((String)paramMap.get("username")).length() > 100){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","User name is longer than the expected."));
				return mapper.writeValueAsString(response);
			}
			
			if(UtilValidate.isEmpty(paramMap.get("username"))
					|| UtilValidate.isEmpty(paramMap.get("storeId"))){
				response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
				return mapper.writeValueAsString(response);
			}
			
			String emailTo = (null==paramMap.get("emailTo"))?"":(String)paramMap.get("emailTo");
			String mobileNo =(null==paramMap.get("mobileNo"))?"":(String)paramMap.get("mobileNo");//(String)paramMap.get("mobileNo");
			String toMessage = "";
			String friendName = "";
			//String mobileNo=(String)paramMap.get("mobileNo");
			String productStoreId = (String)paramMap.get("storeId");
        	
        	Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	Map<String, Object> mapEmail = UtilMisc.toMap("friendName",friendName,"emailTo",emailTo,"mobileNo",mobileNo,"toMessage",toMessage,"productStoreId",productStoreId,"username",paramMap.get("username"));
        	Map<String,String> messageMap = ILinksAppUtil.inviteAFriend(userLogin,mapEmail,Locale.getDefault());
        	if(null!=messageMap && !messageMap.isEmpty() && messageMap.get("errorCode").equals("error")){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", messageMap.get("refToken")));
            	return mapper.writeValueAsString(response);
        	}
        	response.put("response", UtilMisc.toMap("status", "sucess", "msg", messageMap.get("refToken")));
    		return mapper.writeValueAsString(response);
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        	return mapper.writeValueAsString(response);
        }
	}
	
	@POST
    @Produces("application/json")
	@Path("/inviteyoudailyfriend")
    public String inviteYouDailyFriend(String json) throws Exception {
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);

		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		GenericValue userLogin = null;
		if(UtilValidate.isNotEmpty(paramMap.get("username"))){
    		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        	if(UtilValidate.isEmpty(userLogin)){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
        		return mapper.writeValueAsString(response);
        	}
    	}
		response.put("serviceType","inviteyoudailyfriend");
		
		if(((String)paramMap.get("username")).length() > 100){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","User name is longer than the expected."));
			return mapper.writeValueAsString(response);
		}
		
		if(UtilValidate.isEmpty(paramMap.get("username")) ||
				UtilValidate.isEmpty(paramMap.get("aprtNo")) || //UtilValidate.isEmpty(paramMap.get("flatNo")) ||
				//UtilValidate.isEmpty(paramMap.get("emailTo")) || UtilValidate.isEmpty(paramMap.get("mobileNo")) 
				 UtilValidate.isEmpty(paramMap.get("storeId"))){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
		
		List<GenericValue> gv = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", "UDAILY", "contactMechId", paramMap.get("aprtNo")));
    	if(UtilValidate.isEmpty(gv)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","Apartment is not listed."));
			return mapper.writeValueAsString(response);
		}
    	
		
		String toMessage = "WelCome to YouMart";
		String friendName = "";
		String emailTo= null;
		String mobileNo= null;
		
	    emailTo = (null==paramMap.get("emailTo"))?"":(String)paramMap.get("emailTo");
	    mobileNo = (null==paramMap.get("mobileNo"))?"":(String)paramMap.get("mobileNo"); 
		String productStoreId = (String)paramMap.get("storeId");
        try{
        	
        	
        	Debug.log("\n ============================== \n paramMap == "+paramMap+"\n ============================== \n");
        	
        	Map<String, Object> mapEmail = UtilMisc.toMap("friendName",friendName,"emailTo",emailTo,"mobileNo",mobileNo,"toMessage",toMessage,"productStoreId",productStoreId,"username",paramMap.get("username"));
        	Map<String,String> messageMap = ILinksAppUtil.inviteAFriend(userLogin,mapEmail,Locale.getDefault());
        	if(null!=messageMap && !messageMap.isEmpty() && messageMap.get("errorCode").equals("error")){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", messageMap.get("refToken")));
            	return mapper.writeValueAsString(response);
        	}
        	response.put("response", UtilMisc.toMap("status", "sucess", "msg", messageMap.get("refToken")));
        	//response.put("response", UtilMisc.toMap("status", "sucess", "msg", "Invite the friend successfully"));
    		return mapper.writeValueAsString(response);
        } catch (Exception e) {
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Invite Friend failed"));
        	return mapper.writeValueAsString(response);
        }
	}
	
	@POST
    @Produces("application/json")
	@Path("/updateOrder")
    public String updateOrder(String json) throws Exception {
		Debug.log("\n\n json == "+json+"\n\n");
		Debug.logWarning("\n\n json == "+json+"\n\n", module);
		Map<String, Object> paramMap = mapper.readValue(json, Map.class);
		if(UtilValidate.isEmpty(paramMap)){
			response.put("response",UtilMisc.toMap("status", "ERROR", "msg","No input found"));
			return mapper.writeValueAsString(response);
		}
        try{
        	response.put("serviceType","updateOrder"); 
        	
        	GenericValue userLogin = null;
        	if(UtilValidate.isNotEmpty(paramMap.get("username"))){
        		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", paramMap.get("username")), true);
        		paramMap.put("userLogin", userLogin);
        		paramMap.put("userLogin",userLogin);
	        	if(UtilValidate.isEmpty(userLogin)){
	        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "User not found"));
	        		return mapper.writeValueAsString(response);
	        	}
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("type"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Operation type missing"));
        		return mapper.writeValueAsString(response);
        	}
        	
        	if(UtilValidate.isEmpty(paramMap.get("orderId"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "orderId missing"));
        		return mapper.writeValueAsString(response);
        	}
    		
    		if(UtilValidate.isEmpty(paramMap.get("productId"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "productId missing"));
        		return mapper.writeValueAsString(response);
        	}
    		
    		if(UtilValidate.isEmpty(paramMap.get("quantity"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "quantity missing"));
        		return mapper.writeValueAsString(response);
        	}
    		
    		if(!UtilValidate.isInteger((String)paramMap.get("quantity"))){
        		response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "quantity is invalid"));
        		return mapper.writeValueAsString(response);
        	}
    		GenericValue order = null;
    		List<GenericValue> orders = delegator.findList("OrderHeader", EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"), "createdBy", paramMap.get("username"), "productStoreId",paramMap.get("storeId"))), null, null, null, false);
    		if(UtilValidate.isEmpty(orders)){
    			response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "No orders found for the user : "+ paramMap.get("username")));
        		return mapper.writeValueAsString(response);
    		}else{
    			order = EntityUtil.getFirst(orders);
    		}
    		
    		GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId", paramMap.get("productId")),  false);
    		if(UtilValidate.isEmpty(product)){
    			response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Product not found"));
        		return mapper.writeValueAsString(response);
    		}
    		EntityCondition eCond = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"), "productId", paramMap.get("productId"))),
					EntityOperator.AND,
					EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
    		
    		List slitems = ILinksAppUtil.getParentShoppingListItems(order, userLogin);
    		BigDecimal orginalQty = BigDecimal.ZERO, newQty = BigDecimal.ZERO, quantity = BigDecimal.ZERO;
        	if("add".equalsIgnoreCase((String)paramMap.get("type"))){
        		List<GenericValue> items = delegator.findList("OrderItem", eCond, null, UtilMisc.toList("orderItemSeqId DESC"), null, false);
        		if(UtilValidate.isNotEmpty(items)){
        			response.put("response", UtilMisc.toMap("status", "ERROR", "msg", "Product already exists, please update the quantity."));
            		return mapper.writeValueAsString(response);
        		}
        		GenericValue item = EntityUtil.getFirst(delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"))), null, UtilMisc.toList("orderItemSeqId DESC"), null, false));
        		int nextItemSeq = Integer.parseInt(item.getString("orderItemSeqId"));
        		String orderItemSeqId = UtilFormatOut.formatPaddedNumber(++nextItemSeq, 5);
//        		for(GenericValue gv : items){
//        			System.out.println("\n\n  items === "+gv.getString("orderItemSeqId"));
//        			nextItemSeq = Integer.parseInt(gv.getString("orderItemSeqId"));
//        		}
        		quantity = new BigDecimal((String)paramMap.get("quantity"));
        		item.set("productId", paramMap.get("productId"));
        		item.set("quantity", quantity);
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
                
                GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", paramMap.get("productId")))));
                if(UtilValidate.isNotEmpty(slitem)){
    				ILinksAppUtil.updateQtyOrder(slitem, quantity.negate());
    			}
        		
        	}else if("update".equalsIgnoreCase((String)paramMap.get("type"))){
        		GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", paramMap.get("productId")))));
        		
        		GenericValue item = EntityUtil.getFirst(delegator.findList("OrderItem", eCond, null, null, null, false));
        		orginalQty = item.getBigDecimal("quantity");
        		newQty = new BigDecimal((String)paramMap.get("quantity"));
        		int res = orginalQty.compareTo(newQty);
        		Debug.logWarning("\n\n 11111111111  item == "+item+"\n\n", module);
        		if(res == 0){
        			//Both values are equal 
        			response.put("response", UtilMisc.toMap("status", "success"));
        			return mapper.writeValueAsString(response);
        		}else if(res == 1){
        			//First Value is greater
        			quantity = newQty.subtract(orginalQty);
        		}else if(res == -1){
        			//Second value is greater 
        			quantity = newQty.subtract(orginalQty);
        		}
    			item.set("quantity", new BigDecimal((String)paramMap.get("quantity")));
    			item.store();
    			Debug.logWarning("\n\n item == "+item+"\n\n", module);
    			if(UtilValidate.isNotEmpty(slitem)){
    				ILinksAppUtil.updateQtyOrder(slitem, quantity.negate());
    			}
        	}else if("remove".equalsIgnoreCase((String)paramMap.get("type"))){
        		List<GenericValue> items = delegator.findList("OrderItem", eCond, null, null, null, false);
        		for(GenericValue gv : items){
        			quantity = gv.getBigDecimal("quantity");
        			//result = dispatcher.runSync("cancelOrderItem", UtilMisc.toMap("orderId", paramMap.get("orderId"), "orderItemSeqId", gv.getString("orderItemSeqId"),"shipGroupSeqId", "00001","userLogin", paramMap.get("userLogin")));
        			Debug.log("\n\n sfsdf Cond === "+EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"), "statusId", gv.get("statusId"), "orderItemSeqId", gv.get("orderItemSeqId"), "statusUserLogin", gv.get("changeByUserLoginId"))));
        			GenericValue slitem = EntityUtil.getFirst(EntityUtil.filterByCondition(slitems, EntityCondition.makeCondition(UtilMisc.toMap("productId", paramMap.get("productId")))));
        			GenericValue status = EntityUtil.getFirst(delegator.findList("OrderStatus",  EntityCondition.makeCondition(UtilMisc.toMap("orderId", paramMap.get("orderId"), "statusId", gv.get("statusId"), "orderItemSeqId", gv.get("orderItemSeqId"), "statusUserLogin", gv.get("changeByUserLoginId"))), null, null, null, false));
        			Debug.log("\n\n status === "+status+"\n\n");
        			if(UtilValidate.isNotEmpty(status)){
	        			status.set("statusId", "ITEM_CANCELLED");
	        			status.store();
        			}
        			
        			gv.set("statusId", "ITEM_CANCELLED");
        			gv.store();
        			
        			if(UtilValidate.isNotEmpty(slitem)){
        				ILinksAppUtil.updateQtyOrder(slitem, quantity);
        			}
        		}
        	}
        	
            response.put("response", UtilMisc.toMap("status", "success"));
        	
        } catch (Exception e) {
        	e.printStackTrace();
        	response.put("response", UtilMisc.toMap("status", "ERROR", "msg", e.getMessage()));
        }
        
        return mapper.writeValueAsString(response);
	}
}
