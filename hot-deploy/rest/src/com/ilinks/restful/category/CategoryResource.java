package com.ilinks.restful.category;



import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityTypeUtil;

import org.ofbiz.product.store.ProductStoreWorker;

import org.ofbiz.entity.GenericValue;
import com.ilinks.restful.common.*;
import com.ilinks.restful.util.*;


@Path("/category")
public class CategoryResource extends OFBizRestfulBase{
    private final String CATEGORY_ERROR_RETRIEVAL = UtilProperties.getPropertyValue(Constant.PROPERTY_FILE_NAME, "CATEGORY_ERROR_RETRIEVAL");
    private final String CATEGORY_CATALOG_NAME = UtilProperties.getPropertyValue(Constant.PROPERTY_FILE_NAME, "CATEGORY_CATALOG_NAME");
    private final String CATEGORY_INFO = UtilProperties.getPropertyValue(Constant.PROPERTY_FILE_NAME, "CATEGORY_INFO");
    
    
    private List<GenericValue> categoryList = null;
    private List<GenericValue> list = null;
    private List<String> invalidAttributes = null;
    private Set<String> selectFields = null;
    private Set<String> selectPcFields = null;
    GenericValue value = null;
    
    /**
    private GenericValue value;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
    
    public CategoryResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        categoryList = FastList.newInstance();
        invalidAttributes = UtilMisc.toList("lastUpdatedStamp", "createdTxStamp", "lastUpdatedTxStamp");
//        invalidAttributes.add("createdStamp");
//        invalidAttributes.add("createdTxStamp");
        
        selectFields = UtilMisc.toSet("productCategoryId", "parentProductCategoryId", "fromDate", "thruDate", "sequenceNum");
        selectPcFields = UtilMisc.toSet("productCategoryId", "productCategoryTypeId", "primaryParentCategoryId", "categoryName", "description", "longDescription", "categoryImageUrl", "linkOneImageUrl");
        selectPcFields.add("linkTwoImageUrl");
        selectPcFields.add("detailScreen");
        selectPcFields.add("showInSelect");
    }
    
    @GET
    @Produces("application/json")
    /**
     * 
     * @param callbackFunction
     * @param catalogId
     * @return all the categories related to a catalog
     */
    public Response getCatalogCategory(@QueryParam("jsoncallback") String callbackFunction , @QueryParam("catalogId") String catalogId){
        entityCondition = EntityCondition.makeCondition
                        (EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, "PCCT_BROWSE_ROOT"), EntityOperator.AND, EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, catalogId));
        try{
            list = delegator.findList("ProdCatalogCategory",entityCondition,null,null,Constant.readonly,true);
            list = EntityUtil.filterByDate(list);
            
            value = EntityUtil.getFirst(list);
            if(value != null){
                jsonMap.put("catalogName",  value.getRelatedOne("ProdCatalog").getString("catalogName"));
                jsonMap.put("categoryId", value.getString("productCategoryId"));
                value = value.getRelatedOne("ProductCategory");
                entityCondition  = EntityCondition.makeCondition("parentProductCategoryId",EntityOperator.EQUALS,value.getString("productCategoryId"));
                list = delegator.findList("ProductCategoryRollup",entityCondition,selectFields,UtilMisc.toList("sequenceNum ASC"),Constant.readonly,true);
                list = EntityUtil.getRelated("CurrentProductCategory", list);
                //RestfulHelper.removeAttribute(list, invalidAttributes);
            }
        } 
        catch (GenericEntityException gee) {
            Debug.logError(gee, CATEGORY_ERROR_RETRIEVAL, MODULE);
            return Response.serverError().entity(gee.toString()).build();
        }

        if(UtilValidate.isNotEmpty(list)){
            jsonMap.put("hasCategory", Boolean.TRUE);
            jsonMap.put("category", list);
        }
        else{
            jsonMap.put("hasCategory", Boolean.FALSE);
            jsonMap.put("catalogId", catalogId);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/info")
    /**
     * 
     * @param callbackFunction
     * @param categoryID
     * @return info related to a category (images, description)
     */
    public Response getCategoryInfo(@QueryParam("jsoncallback") String callbackFunction , @QueryParam("categoryId") String categoryId){
        try{
            List<GenericValue> catValue = delegator.findList("ProductCategory",EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", categoryId)),selectPcFields,null,Constant.readonly, true);
            //RestfulHelper.removeAttribute(value, invalidAttributes);
            if(UtilValidate.isNotEmpty(catValue)){
            	jsonMap.put("categoryInfo",EntityUtil.getFirst(catValue));
            }
        }
        catch (Exception e){
            jsonMap.put(ERROR, ERROR);
            Debug.logError(e, String.format(CATEGORY_INFO, categoryId), MODULE);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/products")
    public Response getProducts(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("categoryId") String categoryId, @QueryParam("storeId") String storeId, @QueryParam("catalogId") String catalogId, @QueryParam("viewIndex") String viewIndex, @QueryParam("viewSize") String viewSize){
    	
    	String defaultViewSize = UtilProperties.getPropertyValue("widget", "widget.form.defaultViewSize", "20");
    	Map catResult = null;
        try{
        	Map andMap = UtilMisc.toMap("productCategoryId", categoryId);
        	andMap.put("viewIndexString", viewIndex);
        	andMap.put("viewSizeString", viewSize);
        	andMap.put("defaultViewSize", Integer.parseInt(defaultViewSize));
        	andMap.put("limitView", true);
        	andMap.put("prodCatalogId", catalogId);
        	andMap.put("checkViewAllow", true);
        	andMap.put("orderByFields", UtilMisc.toList("sequenceNum", "productId", "introductionDate"));
        	
        	catResult = dispatcher.runSync("getProductCategoryAndLimitedMembers", andMap);
        	
        	// Prevents out of stock product to be displayed on site
        	List<GenericValue> productCategoryMembers = (List)catResult.get("productCategoryMembers");
        	GenericValue productStore = ProductStoreWorker.getProductStore(storeId, delegator);
        	if(UtilValidate.isNotEmpty(productStore)) {
        	    if("N".equals(productStore.getString("showOutOfStockProducts"))) {
        	        List productsInStock = FastList.newInstance();
        	        for(GenericValue productCategoryMember : productCategoryMembers){
        	            GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productCategoryMember.getString("productId")), true);
        	            boolean isMarketingPackage = EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG");
        	            if (isMarketingPackage) {
        	                Map resultOutput = dispatcher.runSync("getMktgPackagesAvailable", UtilMisc.toMap("productId", productCategoryMember.getString("productId")));
        	                int availableInventory = Integer.parseInt((String)resultOutput.get("availableToPromiseTotal"));
        	                if(availableInventory > 0) { 
        	                    productsInStock.add(productCategoryMember);
        	                }
        	            } else {
        	                List<GenericValue> facilities = delegator.findList("ProductFacility", EntityCondition.makeCondition(UtilMisc.toMap("productId", productCategoryMember.getString("productId"))), null, null, null, false);
        	                BigDecimal availableInventory = BigDecimal.ZERO;
        	                if (UtilValidate.isNotEmpty(facilities)) {
        	                	for(GenericValue facility : facilities){ 
        	                		BigDecimal lastInventoryCount = facility.getBigDecimal("lastInventoryCount");
        	                        if (lastInventoryCount != null) {
        	                            availableInventory.add(lastInventoryCount);
        	                        }
        	                    }
        	                    if (availableInventory.compareTo(BigDecimal.ZERO) == 1) {
        	                        productsInStock.add(productCategoryMember);
        	                    }
        	                }
        	            }
        	        }
        	        catResult.put("productCategoryMembers", productsInStock);
        	    } else {
        	        catResult.put("productCategoryMembers", productCategoryMembers);
        	    }
        	}
            
        }catch (Exception gee) {
            Debug.logError(gee, "Unable To Find Products ", MODULE);
            return Response.serverError().entity(gee.toString()).build();
        }
        
        try{
	        if(UtilValidate.isNotEmpty(catResult)){
	            jsonMap.put("products", catResult);
	            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
	            return Response.ok(jsonStr).type("application/json").build();
	        }
	
	        if(UtilValidate.isEmpty(catResult)){
	            return Response.serverError().entity("Unable To Get Products").build();
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }

        // shouldn't ever get here ... should we?
        throw new RuntimeException("Invalid ");
    }
    
    
    @GET
    @Produces("application/json")
    @Path("/subcategory")
    /**
     * 
     * @param callbackFunction
     * @param categoryID
     * @return one subcategory directly underneath the given category
     */
    public Response getSubCategory(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("categoryId") String categoryId){
        try{
            entityCondition  = EntityCondition.makeCondition("parentProductCategoryId",EntityOperator.EQUALS,categoryId);
            list = delegator.findList("ProductCategoryRollup",entityCondition,UtilMisc.toSet("productCategoryId","sequenceNum"),UtilMisc.toList("sequenceNum ASC"),null,true);
            list = EntityUtil.filterByDate(list);
            for(GenericValue val : list){
                entityCondition  = EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,val.getString("productCategoryId"));
                value = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId",val.getString("productCategoryId")));
                value = EntityUtil.getFirst(delegator.findList("ProductCategory", entityCondition, UtilMisc.toSet("productCategoryId","categoryName","description","categoryImageUrl","linkOneImageUrl","linkTwoImageUrl"), UtilMisc.toList("description ASC"), null, true));
                categoryList.add(value);
            }
        }
        catch (GenericEntityException gee) {
            Debug.logError(gee, CATEGORY_ERROR_RETRIEVAL, MODULE);
            return Response.serverError().entity(gee.toString()).build();
        }
        if(UtilValidate.isNotEmpty(categoryList)){
            jsonMap.put("hasCategory", Boolean.TRUE);
            jsonMap.put("category", categoryList);
        }
        else{
            jsonMap.put("hasCategory", Boolean.FALSE);
            jsonMap.put("categoryID", categoryId);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    @GET
    @Produces("application/json")
    @Path("/allsubcategory")
    
    /**
     * 
     * @param callbackFunction
     * @param categoryID
     * @return all related subcategories for a given category
     */
   
    public Response getAllSubCategory(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("categories") String categories){
        JSONObject json = null;
        LinkedList<GenericValue> workingList = null;
        List<EntityCondition> conditionList = null;
        try{
            json = (JSONObject)JSONSerializer.toJSON(categories);
            JSONArray categoryArray = json.getJSONArray("categories");
            
            conditionList = FastList.newInstance();
            for(int index = 0; index < categoryArray.size(); index++){
                JSONObject category = categoryArray.getJSONObject(index);
                conditionList.add(EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,category.getString("categoryID")));
            }
            if(conditionList.size() > 1){
                entityCondition = EntityCondition.makeCondition(conditionList, EntityJoinOperator.OR);
            }
            else{
                entityCondition = EntityCondition.makeCondition(conditionList);
            }
            //entityCondition  = EntityCondition.makeCondition("parentProductCategoryId",EntityOperator.EQUALS,categoryID);
            //list = delegator.findList("ProductCategoryRollup",entityCondition,UtilMisc.toSet("productCategoryId","sequenceNum", "parentCategoryId"),UtilMisc.toList("sequenceNum ASC"),null,true);
            list = delegator.findList("ProductCategoryRollup",entityCondition,UtilMisc.toSet("productCategoryId","sequenceNum"),UtilMisc.toList("sequenceNum ASC"),null,true);
            list = EntityUtil.filterByDate(list);
            
            jsonMap.put("categoryInfo",this.getCatalogName(list));
            categoryList.addAll(list);
            //this.getCategoryCatalogNames(list);
            
            list = EntityUtil.getRelated("ChildProductCategoryRollup",list);
            workingList = new LinkedList<GenericValue>(list);
            
            while(!workingList.isEmpty()){
                value = workingList.pop();
                categoryList.add(value);
                entityCondition = EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,value.getString("productCategoryId"));
                list = delegator.findList("ProductCategoryRollup",entityCondition,UtilMisc.toSet("productCategoryId","sequenceNum"),UtilMisc.toList("sequenceNum ASC"),null,true);
                list = EntityUtil.filterByDate(list);
                list = EntityUtil.getRelated("ChildProductCategoryRollup",list);
                workingList.addAll(list);
            }
            
            //RestfulHelper.removeAttribute(categoryList, invalidAttributes);
            jsonMap.put("category", categoryList); 
        }
        catch(Exception e){
            jsonMap.put(ERROR, ERROR);
            Debug.logError(e, "Category " + categories + " " + e.getClass().getName() + " " + e.getMessage(), MODULE);
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
    private List<Object> getCatalogName(List<GenericValue> categoryRollUp) throws Exception{
        List<Object> list = FastList.newInstance();
        for(GenericValue value : categoryRollUp){
            list.add(this.getCatalogName(value));
        }
        return list;
    }
    private  Map<Object,Object> getCatalogName(GenericValue categoryRollUp) throws Exception{
        Map<Object,Object> map = FastMap.newInstance();
        try{
            GenericValue value = categoryRollUp.getRelatedOne("CurrentProductCategory");
            map.put("categoryID", value.getString("productCategoryId"));
            map.put("categoryDescription", value.getString("description"));
            map.put("categoryName", value.getString("categoryName"));
            value = value.getRelatedOne("PrimaryParentProductCategory");
            value = EntityUtil.getFirst( value.getRelatedCache("ProdCatalogCategory"));
            value = value.getRelatedOne("ProdCatalog");
            map.put("catalogId", value.getString("prodCatalogId"));
            map.put("catalogName", value.getString("catalogName"));
        }
        catch (Exception e){
            throw new Exception(String.format(CATEGORY_CATALOG_NAME,categoryRollUp.getString("productCategoryId")) , e);
        }
        return map;
    }
}