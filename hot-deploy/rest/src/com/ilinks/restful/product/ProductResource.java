package com.ilinks.restful.product;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.product.product.ProductWorker;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import com.ilinks.restful.util.RestfulHelper;
import com.ilinks.restful.common.*;
import org.ofbiz.base.util.UtilProperties;


@Path("/products")

public class ProductResource extends OFBizRestfulBase{   
	private ShoppingCart cart;
    /**
    private GenericValue value;
    @Context
    private HttpServletRequest request;
    @Context
    private HttpServletResponse response;
    **/
    public ProductResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        cart = null;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    /**
    @Path("/{productId}")
    public Response getProduct(@QueryParam("jsoncallback") String callbackFunction, @PathParam("productId") String productId){
    **/
    public Response getProduct(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("productId") String productId){
        
        try{
            List<EntityCondition> orConditionList = FastList.newInstance();
            List<EntityCondition> andConditionList = FastList.newInstance();
            
            orConditionList.add(EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
            orConditionList.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS, null));
            EntityCondition orCondition = EntityCondition.makeCondition(orConditionList, EntityOperator.OR);
            
            andConditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
            andConditionList.add(EntityCondition.makeCondition(orCondition));
            EntityCondition andCondition = EntityCondition.makeCondition(andConditionList,EntityOperator.AND);
            
            List<GenericValue> productList = delegator.findList("Product", andCondition,
                    UtilMisc.toSet("largeImageUrl","brandName","productName","longDescription","smallImageUrl", "isVirtual","productWeight","productId"), null, Constant.readonly, true);
            
            if(UtilValidate.isEmpty(productList)){
                // product not found
                jsonMap.put(properties.getProperty("ERROR","error"), "Unable To Find Product " + productId);
                jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
                return Response.ok(jsonStr).type("application/json").build();
            }
            
            
            GenericValue primaryProduct = EntityUtil.getFirst(productList);
            List<Object> productInfoList = FastList.newInstance();
            Map<Object,Object> productMap = null;
            List<GenericValue> productFeatureList = null;
            List<GenericValue> priceList = null;
            List<GenericValue> associationList = null;
            if(ProductWorker.isVirtual(delegator, productId)){
                associationList = getVariants(primaryProduct);
                associationList = EntityUtil.filterByDate(associationList);
                for(GenericValue association : associationList){
                    productMap = FastMap.newInstance();
                    GenericValue variant = association.getRelatedOne("AssocProduct");
                    productFeatureList = variant.getRelatedCache("ProductFeatureAppl");
                    productFeatureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "PRODUCT_VARIATION"),productFeatureList);
                    GenericValue feature  = EntityUtil.getFirst(productFeatureList);
                    productMap.put("feature", feature.getString("description"));
                    priceList = variant.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                    priceList = EntityUtil.filterByDate(priceList);
                    GenericValue price = EntityUtil.getFirst(priceList);
                    productMap.put("price", price.getDouble("price"));
                    productMap.put("productId", variant.getString("productId"));
                  
                    productMap.put("productWeight", variant.getDouble("productWeight"));
                    productMap.put("sequenceNumber", association.getLong("sequenceNum"));
                    productInfoList.add(productMap);
                }
            }
            else{
                // standalone
                productMap = FastMap.newInstance();
                productFeatureList = EntityUtil.getRelatedCache("ProductFeatureAppl",productList);
                productFeatureList = EntityUtil.getRelatedByAndCache("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", "STAND_ALONE"),productFeatureList);
                GenericValue feature = EntityUtil.getFirst(productFeatureList);
                productMap.put("feature", feature.getString("description"));
                priceList = primaryProduct.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                priceList = EntityUtil.filterByDate(priceList);
                GenericValue price = EntityUtil.getFirst(priceList);
                productMap.put("price", price.getDouble("price"));
                productMap.put("productId", productId);
                productMap.put("productWeight", primaryProduct.getDouble("productWeight"));
                productMap.put("sequenceNumber", 1);
                productInfoList.add(productMap);
            }
            /**
             * product ingredients
             */
            List<GenericValue> productContentList = primaryProduct.getRelatedByAndCache("ProductContent", UtilMisc.toMap("productContentTypeId", "INGREDIENTS"));
            productContentList = EntityUtil.filterByDate(productContentList);
            GenericValue productContent = EntityUtil.getFirst(productContentList);
            
            if(productContent != null){
                GenericValue content = productContent.getRelatedOneCache("Content");
                GenericValue electronicText = delegator.findByPrimaryKeyCache("ElectronicText", UtilMisc.toMap("dataResourceId",content.getString("dataResourceId")));
                jsonMap.put("ingredients", electronicText.getString("textData"));  
            }
            else{
                jsonMap.put("ingredients", "ingredients");  
            }
            
            // you may also like association
            associationList = getYouMayAlsoLike(primaryProduct);
            List<Object> youMayAlsoLikeList = FastList.newInstance();
            for(GenericValue association : associationList){
                productMap = FastMap.newInstance();
                GenericValue product = association.getRelatedOneCache("AssocProduct");
                
                priceList = product.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                priceList = EntityUtil.filterByDate(priceList);
                GenericValue price = EntityUtil.getFirst(priceList);
                productMap.put("price", price.getDouble("price"));
                productMap.put("productId", product.getString("productId"));
                productMap.put("sequenceNumber", association.getLong("sequenceNum"));
                
                if(ProductWorker.getVariantVirtualId(product) != null){
                    product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                }
                productMap.put("smallImageURL", product.getString("smallImageUrl"));
                productMap.put("productName", product.getString("productName"));
                productMap.put("parentProductID", product.getString("productId"));
                productMap.put("brandName", product.getString("brandName"));
                youMayAlsoLikeList.add(productMap);
            }
            youMayAlsoLikeList.addAll(getDefaultYouMayAlsoLike());
            jsonMap.put("youMayAlsoLike", youMayAlsoLikeList);
            
            
            
            
            /**
             * also bought together association
             * 
             */
            
            associationList = getAlsoBoughtTogether(primaryProduct);
            List<Object> alsoBoughtTogetherList = FastList.newInstance();
            for(GenericValue association : associationList){
                boolean isVariant = true;
                GenericValue product = association.getRelatedOneCache("AssocProduct");
                if(ProductWorker.getVariantVirtualId(product) == null){
                    // standalone or virtual
                    if(ProductWorker.isVirtual(delegator, product.getString("productId"))){
                        // virtual
                        // get the first variant
                        List<GenericValue> variantAssociations = this.getVariants(product);
                        List<GenericValue> variantProducts = EntityUtil.getRelatedCache("AssocProduct", variantAssociations);
                        if(UtilValidate.isNotEmpty(variantProducts)){
                          product = EntityUtil.getFirst(variantProducts);
                          if(!ProductWorker.isSellable(product)){
                              // unable to sale product
                              continue;
                          }
                        }
                        else{
                            // no product variant
                            continue;
                        }
                    }
                    else{
                        // standalone
                        isVariant = false;
                    }
                }
                
                productMap = FastMap.newInstance();
                priceList = product.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
                priceList = EntityUtil.filterByDate(priceList);
                GenericValue price = EntityUtil.getFirst(priceList);
                productMap.put("price", price.getDouble("price"));
                productMap.put("productId", product.getString("productId"));
                productMap.put("sequenceNumber", association.getLong("sequenceNum"));
                if(isVariant){
                    // get parent product 
                    product  = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                }
                productMap.put("smallImageURL", product.getString("smallImageUrl"));
                productMap.put("productName", product.getString("productName"));
                productMap.put("parentProductID", product.getString("productId"));
                productMap.put("brandName", product.getString("brandName"));
                alsoBoughtTogetherList.add(productMap);
            }
            
            /**
             * filter also bought together based on brand, feature
             */
            
            List<GenericValue> primaryFeatureList = primaryProduct.getRelatedByAndCache("ProductFeatureAppl", UtilMisc.toMap("productFeatureApplTypeId","OPTIONAL_FEATURE"));
            primaryFeatureList = EntityUtil.filterByDate(primaryFeatureList);
            
            // add brand the features to filter against
            GenericValue brand = delegator.makeValue("ProductFeatureAppl");
            brand.set("productFeatureId", primaryProduct.getString("brandName"));
            primaryFeatureList.add(brand);
            primaryFeatureList = EntityUtil.getFieldListFromEntityList(primaryFeatureList, "productFeatureId",true);
            
            List<Object> alsoBoughtFiltered = FastList.newInstance();
            alsoBoughtTogetherList.addAll(getDefaultFrequentlyBroughTogether());
            if(alsoBoughtTogetherList.size() > 3){
                for(Object obj :  alsoBoughtTogetherList){
                    boolean matchingFeature = false;
                    int featureSize = primaryFeatureList.size();
                    Map<String,String> map = (Map<String,String>) obj;
                    
                    
                    GenericValue product = ProductWorker.findProduct(delegator, map.get("productId"));
                    
                    List<Object> primaryTempList = FastList.newInstance();
                    
                    for(int count = 0 ; count < featureSize; count++){
                        // need the same number of items in list to use the Collections.copy
                        primaryTempList.add(new Object());
                    }
                  
                    Collections.copy(primaryTempList, primaryFeatureList);
                    productFeatureList.clear();
                    
                    if(ProductWorker.getVariantVirtualId(product) != null){
                        // variant
                        product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                    }
                    
                    productFeatureList = product.getRelatedByAndCache("ProductFeatureAppl", UtilMisc.toMap("productFeatureApplTypeId","OPTIONAL_FEATURE"));
                    brand = delegator.makeValue("ProductFeatureAppl");
                    brand.set("productFeatureId", product.getString("brandName"));
                    productFeatureList.add(brand);
                    productFeatureList = EntityUtil.filterByDate(productFeatureList);
                    productFeatureList = EntityUtil.getFieldListFromEntityList(productFeatureList, "productFeatureId",true);
                    
                    primaryTempList.removeAll(productFeatureList);
                    if(primaryTempList.size() == 0){
                        // match all features
                        matchingFeature  = true;
                    }
                    else if (primaryTempList.size() == 1 && primaryTempList.size() < featureSize){
                        //matching all but 1
                        matchingFeature  = true;
                    }
                    else if (primaryTempList.size() == 2 && primaryTempList.size() < featureSize){
                        // matching all but 2
                        matchingFeature  = true;
                    }
                    else if (primaryTempList.size() == 3 && primaryTempList.size() < featureSize){
                        // matching all but 3
                        matchingFeature  = true;
                    }
                    else{
                        // dont add
                    }
                    if(matchingFeature){
                        alsoBoughtFiltered.add(obj);
                    }
                    
                }
            }
           
            alsoBoughtTogetherList.removeAll(alsoBoughtFiltered);
           
            
            if(alsoBoughtFiltered.size() > 3){
                // only carry 3 products
                alsoBoughtFiltered = alsoBoughtFiltered.subList(0, 2);
            }
            else{
                // fill in the rest of the alsoBoughtFiltered items
                int size = 3 - alsoBoughtFiltered.size();
                if(alsoBoughtFiltered.size() >= size){
                    alsoBoughtFiltered.addAll(alsoBoughtTogetherList.subList(0, size));
                }
                else{
                    // less than 3 products
                    alsoBoughtFiltered.addAll(alsoBoughtTogetherList);
                }
                
            }
            jsonMap.put("alsoBoughtTogether", alsoBoughtFiltered);
            
            
            List<GenericValue> attributeList = primaryProduct.getRelatedCache("ProductAttribute");
            for(GenericValue attribute : attributeList){
                // logo, autoship, etc
                jsonMap.put(attribute.getString("attrName"), attribute.getString("attrValue"));   
            }
            
            for(String key : primaryProduct.keySet()){
                // virtual/standAlone (brands, url, product description, etc)
                jsonMap.put(key, primaryProduct.get(key));   
            }
            jsonMap.put("products", productInfoList);
        }
        catch (Exception e) {
            Debug.logError(e, e.getClass().getName() + " product id " + productId + " "  + e.getMessage(), MODULE);
            jsonMap.clear();
            jsonMap.put(properties.getProperty("ERROR","error"),  e.getMessage() + " " + productId);
            //return Response.serverError().entity(e.toString()).build();
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
       
        return Response.ok(jsonStr).type("application/json").build();
    }
    
    private List<GenericValue> getAlsoBoughtTogether(GenericValue product) throws Exception {
        List<GenericValue>  list =  getAssociation(product, "ALSO_BOUGHT");
        
        return list;
    }
    private List<GenericValue> getYouMayAlsoLike(GenericValue product) throws Exception {
        List<GenericValue> list =  getAssociation(product, "PRODUCT_UPGRADE");
        return list;
    }
    private List<GenericValue> getVariants(GenericValue product) throws Exception {
        List<GenericValue> list =  getAssociation(product, "PRODUCT_VARIANT");
        return list;
    }
    private List<GenericValue>  getAssociation(GenericValue product, String associationType) throws Exception{
        
        List<GenericValue> list = product.getRelatedCache("MainProductAssoc", UtilMisc.toMap("productAssocTypeId",associationType), UtilMisc.toList("sequenceNum ASC"));
        list = EntityUtil.filterByDate(list);
        return list;
    }
    private List<Object>  getDefaultFrequentlyBroughTogether() throws Exception{
        List<Object> productList = FastList.newInstance();
        List<GenericValue> list = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, UtilMisc.toList("761715000128","879349005041","718122578191")), null, null, null, false);
        
        
        int count = 0;
        for(GenericValue product : list){
           Map<String,Object> productMap = FastMap.newInstance();
            
            List<GenericValue> priceList = product.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
            priceList = EntityUtil.filterByDate(priceList);
            GenericValue price = EntityUtil.getFirst(priceList);
            productMap.put("price", price.getDouble("price"));
            productMap.put("productId", product.getString("productId"));
            productMap.put("sequenceNumber",++count);
            
            if(ProductWorker.getVariantVirtualId(product) != null){
                product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
            }
            productMap.put("smallImageURL", product.getString("smallImageUrl"));
            productMap.put("productName", product.getString("productName"));
            productMap.put("parentProductID", product.getString("productId"));
            productMap.put("brandName", product.getString("brandName"));
            productList.add(productMap);
        }
        return productList;
	}
    
	private List<Object>  getDefaultYouMayAlsoLike() throws Exception{
        List<Object> productList = FastList.newInstance();
        List<GenericValue> list = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, UtilMisc.toList("769949620112","745158200505","83765411975","018065054524","852009002550")), null, null, null, false);
       
        
        int count = 0;
        for(GenericValue product : list){
           Map<String,Object> productMap = FastMap.newInstance();
            
            List<GenericValue> priceList = product.getRelatedByAndCache("ProductPrice", UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE"));
            priceList = EntityUtil.filterByDate(priceList);
            GenericValue price = EntityUtil.getFirst(priceList);
            productMap.put("price", price.getDouble("price"));
            productMap.put("productId", product.getString("productId"));
            productMap.put("sequenceNumber",++count);
            
            if(ProductWorker.getVariantVirtualId(product) != null){
                product = ProductWorker.getParentProduct(product.getString("productId"), delegator);
            }
            productMap.put("smallImageURL", product.getString("smallImageUrl"));
            productMap.put("productName", product.getString("productName"));
            productMap.put("parentProductID", product.getString("productId"));
            productMap.put("brandName", product.getString("brandName"));
            productList.add(productMap);
        }
        return productList;
	}
	
	/*
	public static Map getProductPrice(String productId){
		super.initRequestAndDelegator();
        try{
        	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), true);
            cart = ShoppingCartEvents.getCartObject(request);
            // get the product price
	       if (cart.isSalesOrder()) {
	           // sales order: run the "calculateProductPrice" service
	           Map priceContext = UtilMisc.toMap("product", product, "currencyUomId", cart.getCurrency(), name3, value3, name4, value4), ,
	                   autoUserLogin : autoUserLogin, userLogin : userLogin];
	           priceContext.webSiteId = webSiteId;
	           priceContext.prodCatalogId = catalogId;
	           priceContext.productStoreId = productStoreId;
	           priceContext.agreementId = cart.getAgreementId();
	           priceContext.partyId = cart.getPartyId();  // IMPORTANT: otherwise it'll be calculating prices using the logged in user which could be a CSR instead of the customer
	           priceContext.checkIncludeVat = "Y";
	           priceMap = dispatcher.runSync("calculateProductPrice", priceContext);
	
	           context.price = priceMap;
	       } else {
	           // purchase order: run the "calculatePurchasePrice" service
	           priceContext = [product : product, currencyUomId : cart.getCurrency(),
	                   partyId : cart.getPartyId(), userLogin : userLogin];
	           priceMap = dispatcher.runSync("calculatePurchasePrice", priceContext);
	
	           context.price = priceMap;
	       }
	
	       // get aggregated product totalPrice
	       if ("AGGREGATED".equals(product.productTypeId)||"AGGREGATED_SERVICE".equals(product.productTypeId)) {
	           configWrapper = ProductConfigWorker.getProductConfigWrapper(productId, cart.getCurrency(), request);
	           if (configWrapper) {
	               configWrapper.setDefaultConfig();
	               context.totalPrice = configWrapper.getTotalPrice();
	           }
	       }
        }catch(Exception e){
        	e.printStackTrace();
        }
        
	       return null;
	}
	*/
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/verify")
    public Response getProduct(@QueryParam("jsoncallback") String callbackFunction){
        
        try{
        	jsonMap.put("message", "success");
        }
        catch (Exception e) {
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
       
        return Response.ok(jsonStr).type("application/json").build();
    }
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@Path("/getuid")
    public Response getUID(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("productId") String productId, @QueryParam("mobileNumber") String mobileNumber){
        
        try{
        	String uId = RandomStringUtils.random(6, true, true);
        	jsonMap.put("uid", uId);
        	
        	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
        	if(UtilValidate.isNotEmpty(product)){
        		product.clear();
        		product = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "MOBIL_NUMBER"), false);
        		if(UtilValidate.isNotEmpty(product)){
        			product.set("attrValue", mobileNumber);
        			product.store();
        		}else{
        			product = delegator.makeValue("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "MOBIL_NUMBER", "attrValue", mobileNumber));
        			product.create();
        		}
        		product.clear();
        		
        		product = delegator.findOne("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "UID"), false);
        		if(UtilValidate.isNotEmpty(product)){
        			product.set("attrValue", uId);
        			product.store();
        		}else{
        			product = delegator.makeValue("ProductAttribute", UtilMisc.toMap("productId", productId, "attrName", "UID", "attrValue", uId));
        			product.create();
        		}
        	}
        	
        	Map result = dispatcher.runSync("sendSMS", UtilMisc.toMap("mobileNumbers", mobileNumber, "smsMessage", " Product Id : "+productId+" , Code : "+uId ));
        	
        	Debug.log("\n\n result == "+result+"\n\n");
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally{
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
       
        return Response.ok(jsonStr).type("application/json").build();
    }
}