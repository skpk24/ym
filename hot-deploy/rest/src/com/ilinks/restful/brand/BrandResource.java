package com.ilinks.restful.brand;



import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import org.ofbiz.entity.GenericValue;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.*;


@Path("/brand")
public class BrandResource extends OFBizRestfulBase{
    private final String BRAND_POPULAR_ERROR_RETRIEVAL = UtilProperties.getPropertyValue(Constant.PROPERTY_FILE_NAME, "BRAND_POPULAR_ERROR_RETRIEVAL");
    private List<GenericValue> list = null;
    private List<Map<Object,Object>> brandList = null;
    private Map<String,Object> brand = null;
    Map<Object,Object> map = null;
    GenericValue value = null;
    
    public BrandResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
        brandList = FastList.newInstance();
        brand = FastMap.newInstance();
    }
    @Path("/popular")
    @GET
    @Produces("application/json")
    /**
     * 
     * @param callbackFunction
     * @param catalogID
     * @return all the categories related to a catalog
     */
    public Response getPopularBrand(@QueryParam("jsoncallback") String callbackFunction){
        try{
            
            TransactionUtil.begin();
            EntityListIterator it = delegator.find("ProductCalculatedInfo", null, null, null, UtilMisc.toList("totalQuantityOrdered DESC"), null);
            list = it.getPartialList(0, 50);
            list = EntityUtil.getRelatedCache("Product", list);
          
            list = EntityUtil.filterByCondition(list, EntityCondition.makeCondition("brandName",EntityOperator.NOT_EQUAL, null));
            
            for(GenericValue genericValue : list){
                brand.clear();
                brand.put("productId", genericValue.getString("productId"));
                brand.put("attrName", "brandLogo");
                
                value = delegator.findOne("ProductAttribute", brand, true);
                if(value != null){
                    map = FastMap.newInstance();
                    map.put("brandName", genericValue.getString("brandName"));
                    map.put("brandImageURL", value.getString("attrValue"));
                    if(!brandList.contains(map)){
                        brandList.add(map);
                    }  
                }
            }
           if(brandList.size() > 7){
               jsonMap.put("brandInfo", brandList.subList(0, 7));
           }
           else{
               jsonMap.put("brandInfo", brandList);
           }
            
            /**
            //jsonMap.put("brand", EntityUtil.getFieldListFromEntityList(list, "brandName", false));
            list = EntityUtil.getFieldListFromEntityList(list, "productId", false);
            entityCondition = EntityCondition.makeCondition
            (EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "brandLogo"), EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.IN, list));
            list = delegator.findList("ProductAttribute", entityCondition, null, null, null, true);
            jsonMap.put("productAttr", list);
               **/
            it.close();
            TransactionUtil.commit();
            
        } 
        catch (GenericEntityException gee) {
            Debug.logError(gee, BRAND_POPULAR_ERROR_RETRIEVAL, MODULE);
            return Response.serverError().entity(gee.toString()).build();
        }
        jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        return Response.ok(jsonStr).type("application/json").build();
    }
}