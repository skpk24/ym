package com.ilinks.restful.catalog;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import java.util.List;
import java.util.Set;


import com.ilinks.restful.util.RestfulHelper;
import com.ilinks.restful.common.*;

@Path("/catalog")
public class CatalogResource extends OFBizRestfulBase{
    //private List<String> invalidAttributes = null;
	private Set<String> selectFields = null;
	public CatalogResource(){
       super();
       delegator = getDelegator();
       dispatcher = getDispatcher();
       //invalidAttributes = UtilMisc.toList("useQuickAdd", "styleSheet","contentPathPrefix","templatePathPrefix","viewAllowPermReqd", "purchaseAllowPermReqd","lastUpdatedStamp","lastUpdatedTxStamp","createdStamp");
       //invalidAttributes = UtilMisc.toList("lastUpdatedStamp","lastUpdatedTxStamp","createdStamp");
       //invalidAttributes.add("createdTxStamp");
       selectFields = UtilMisc.toSet("catalogName", "contentPathPrefix", "headerLogo", "prodCatalogId");
       selectFields.add("purchaseAllowPermReqd");
       selectFields.add("styleSheet");
       selectFields.add("templatePathPrefix");
       selectFields.add("useQuickAdd");
       selectFields.add("viewAllowPermReqd");
	}
	
    @GET
    @Produces("application/json")
    public Response getCatalog(@QueryParam("jsoncallback") String callbackFunction, @QueryParam("storeId") String storeId){
        List<GenericValue> catalogList = null;
        try{
            if(UtilValidate.isNotEmpty(storeId)){
				EntityCondition mainCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, storeId);
				List<GenericValue> psc = delegator.findList("ProductStoreCatalog",mainCond, null, null, Constant.readonly, true);
				if(UtilValidate.isNotEmpty(psc)){
					List<String> catalogIds = EntityUtil.getFieldListFromEntityList(psc, "prodCatalogId", true);
					if(UtilValidate.isNotEmpty(catalogIds)){
						mainCond = EntityCondition.makeCondition("prodCatalogId", EntityOperator.IN, catalogIds); 
						catalogList = delegator.findList("ProdCatalog", mainCond, selectFields, null, Constant.readonly, true);
					}
				}
            }
            
        }catch (GenericEntityException gee) {
            Debug.logError(gee, "Unable To Find Catalog ", MODULE);
            return Response.serverError().entity(gee.toString()).build();
        }
        
        try{
	        if(UtilValidate.isNotEmpty(catalogList)){
	            jsonMap.put("catalog", catalogList);
	            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
	            return Response.ok(jsonStr).type("application/json").build();
	        }
	
	        if(UtilValidate.isEmpty(catalogList)){
	            return Response.serverError().entity("Unable To Get Catalog").build();
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }

        // shouldn't ever get here ... should we?
        throw new RuntimeException("Invalid ");
    }
}