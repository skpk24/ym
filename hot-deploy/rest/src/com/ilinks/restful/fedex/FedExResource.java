package com.ilinks.restful.fedex;

import java.util.Map;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.ilinks.restful.common.*;
import com.ilinks.restful.util.RestfulHelper;

@Path("/fedex")
public class FedExResource extends OFBizRestfulBase{
    public FedExResource(){
        super();
        MODULE =  this.getClass().getName();
        delegator = getDelegator();
        dispatcher = getDispatcher();
    }
    
    @Path("/labels")
    @GET
    @Produces("application/json")
    public Response log(@QueryParam("jsoncallback") String callbackFunction,  @QueryParam("orderID") String orderID){
        List<Object> list = FastList.newInstance();
        try{
            super.initRequestAndDelegator();
            jsonMap.put(SUCCESS, SUCCESS);
            //List<GenericValue> orderShipmentList = delegator.findByAndCache("OrderShipment",UtilMisc.toMap("orderId", orderID));
            EntityFindOptions findOptions = new EntityFindOptions();
            findOptions.setDistinct(true);
            List<GenericValue> orderShipmentList = delegator.findList("OrderShipment", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,  orderID), UtilMisc.toSet("shipmentId"), null,findOptions, true);
            
            for(GenericValue orderShipment : orderShipmentList){
                GenericValue shipment = orderShipment.getRelatedOneCache("Shipment");
                List<GenericValue> shipmentPackageRouteSegmentList = shipment.getRelatedCache("ShipmentPackageRouteSeg");
                for(GenericValue shipmentPackageRouteSegment : shipmentPackageRouteSegmentList){
                    Map<Object,Object> map = FastMap.newInstance();
                    map.put("orderID", orderID);
                    map.put("shipmentID", shipmentPackageRouteSegment.getString("shipmentId"));
                    map.put("packageSeqID", shipmentPackageRouteSegment.getString("shipmentPackageSeqId"));
                    map.put("trackingNumber", shipmentPackageRouteSegment.getString("trackingCode"));
                    map.put("label", new String(shipmentPackageRouteSegment.getBytes("labelImage")));
                    list.add(map);
                }
            }
          
        }
        catch(Exception e){
            jsonMap.put(ERROR, "Unable To Find Labels For " + orderID);
            Debug.logError(e, e.getClass().getName() + " " + e.getMessage(), MODULE);
           
        }
        finally{
            jsonMap.put("labels", list);
            jsonStr = RestfulHelper.createJSONString(jsonMap,callbackFunction);
        }
        return Response.ok(jsonStr).type("application/json").build();
    }   
}