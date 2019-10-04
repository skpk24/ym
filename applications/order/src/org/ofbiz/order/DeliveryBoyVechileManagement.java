package org.ofbiz.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

public class DeliveryBoyVechileManagement{

	 public static String createVehicleDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
	        Delegator delegator = (Delegator) request.getAttribute("delegator");
	        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	        if(UtilValidate.isEmpty(userLogin))
	        {
	        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
	        	return "error";
	        }
	        
	        GenericValue vechileDetails = null;
	        String vehicleNumber = request.getParameter("vehicleNumber");
	        try {
		       	TransactionUtil.begin();
		       	vechileDetails = delegator.findOne("VehicleDetails", false, UtilMisc.toMap("vehicleNumber",vehicleNumber));
		       	
		       	String orderIdThreshold = request.getParameter("orderIdThreshold");
	       		String weightThreshold = request.getParameter("weightThreshold");
	       		if(UtilValidate.isEmpty(orderIdThreshold))orderIdThreshold = "0";
	       		if(UtilValidate.isEmpty(weightThreshold))weightThreshold = "0";
		       	
		       	if(UtilValidate.isNotEmpty(vechileDetails))
		       	{
		       		 String returnValue = createVehicleDetailsHistory(vechileDetails,userLogin.getString("userLoginId"));
		       		 if("success".equals(returnValue))
		       		 {
		       			vechileDetails.put("ownerName", request.getParameter("ownerName"));
		       			vechileDetails.put("ownerDetails", request.getParameter("ownerDetails"));
		       			vechileDetails.put("orderIdThreshold", new BigDecimal(orderIdThreshold));
			       		vechileDetails.put("weightThreshold", new BigDecimal(weightThreshold));
		       			vechileDetails.put("createdDate", UtilDateTime.nowTimestamp());
		       			vechileDetails.put("createdBy", userLogin.getString("userLoginId"));
		       			vechileDetails.store();
		       		 }
		       		 else{
		       			 request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Vehicle Details");
		       			 TransactionUtil.rollback();
		       		 }
		       	 }else
		       	 {
		       		vechileDetails = delegator.makeValue("VehicleDetails");
		       		
		       		vechileDetails.put("vehicleNumber", request.getParameter("vehicleNumber"));
		       		vechileDetails.put("ownerName", request.getParameter("ownerName"));
		       		vechileDetails.put("ownerDetails", request.getParameter("ownerDetails"));
		       		vechileDetails.put("orderIdThreshold", new BigDecimal(orderIdThreshold));
		       		vechileDetails.put("weightThreshold", new BigDecimal(weightThreshold));
		       		vechileDetails.put("createdDate", UtilDateTime.nowTimestamp());
		       		vechileDetails.put("createdBy", userLogin.getString("userLoginId"));
		       		 
		       		vechileDetails.create();
		       	 }
		       	 request.setAttribute("_ENENT_MESSAGE_", "Vechile Details Created Successfully");
		   		 TransactionUtil.commit();
	   	} catch (GenericEntityException e) {
	   		// TODO Auto-generated catch block
	   		//e.printStackTrace();
	   		request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Vehicle Details");
	   		try {
	   			TransactionUtil.rollback();
	   		} catch (GenericTransactionException e1) {
	   			// TODO Auto-generated catch block
	   			e1.printStackTrace();
	   		}
	   		return "error";
	   	}
	   	return "success";
	 }
	    
	 public static String updateVehicleDetails(HttpServletRequest request, HttpServletResponse response){
	     Delegator delegator = (Delegator) request.getAttribute("delegator");
	     GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	        if(UtilValidate.isEmpty(userLogin))
	        {
	        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
	        	return "error";
	        }
	        
	     String vehicleNumber=request.getParameter("vehicleNumber");
	     try{
	    	 TransactionUtil.begin();
		     GenericValue vechileDetails = delegator.findByPrimaryKey("VehicleDetails", UtilMisc.toMap("vehicleNumber",vehicleNumber ));
		     if (vechileDetails != null)
		     {
		    	 String orderIdThreshold = request.getParameter("orderIdThreshold");
	       		 String weightThreshold = request.getParameter("weightThreshold");
	       		 if(UtilValidate.isEmpty(orderIdThreshold))orderIdThreshold = "0";
	       		 if(UtilValidate.isEmpty(weightThreshold))weightThreshold = "0";
		       		
		    	 createVehicleDetailsHistory(vechileDetails, userLogin.getString("userLoginId"));
		    	 vechileDetails.put("ownerName", request.getParameter("ownerName"));
		    	 vechileDetails.put("ownerDetails", request.getParameter("ownerDetails"));
		    	 vechileDetails.put("orderIdThreshold", new BigDecimal(orderIdThreshold));
		       	 vechileDetails.put("weightThreshold", new BigDecimal(weightThreshold));
		    	 vechileDetails.put("createdDate", UtilDateTime.nowTimestamp());
		    	 vechileDetails.put("createdBy", userLogin.getString("userLoginId"));
		    	
		    	 vechileDetails.store();
		    	 request.setAttribute("_ENENT_MESSAGE_", "Vechile Details Created Successfully");
		    	 TransactionUtil.commit();
		     }
	     }catch (Exception e) {
			// TODO: handle exception
	    	 e.printStackTrace();
	    	 try {
				TransactionUtil.rollback();
				request.setAttribute("_ERROR_MESSAGE_", "Failed To Update Vehicle Details");
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
	     request.setAttribute("_ENENT_MESSAGE_", "Vechile Details Created Successfully");
	     return "success";
	 
	 }
	 
	 public static String createVehicleDetailsHistory(GenericValue vechileDetails, String userLoginId){
		 Delegator delegator = vechileDetails.getDelegator();
		 GenericValue vechileDetailsHistory = delegator.makeValue("VehicleDetailsHistory");
		 
		 vechileDetailsHistory.put("vehicleDetailsHistoryId", delegator.getNextSeqId("VehicleDetailsHistory"));
		 vechileDetailsHistory.put("vehicleNumber", vechileDetails.getString("vehicleNumber"));
		 vechileDetailsHistory.put("ownerName", vechileDetails.getString("ownerName"));
		 vechileDetailsHistory.put("ownerDetails", vechileDetails.getString("ownerDetails"));
		 vechileDetailsHistory.put("orderIdThreshold", vechileDetails.getBigDecimal("orderIdThreshold"));
		 vechileDetailsHistory.put("weightThreshold", vechileDetails.getBigDecimal("weightThreshold"));
		 vechileDetailsHistory.put("createdDate", vechileDetails.getTimestamp("createdDate"));
		 vechileDetailsHistory.put("updatedDate", UtilDateTime.nowTimestamp());
		 vechileDetailsHistory.put("createdBy", userLoginId);
		 vechileDetailsHistory.put("updatedBy", vechileDetails.getString("createdBy"));
		 
		 try {
			 vechileDetailsHistory.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		return "success";
	 }
	 
	
	 public static List<GenericValue> getAllAssignedVehicleToDeliveryBoy(Delegator delegator,String orderId){
	     List<GenericValue> deliveryBoyVehicleDetails = null;
	    	 try {
	    		 GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId",orderId), true);
	    		 deliveryBoyVehicleDetails = delegator.findList("DeliveryBoyVehicleDetails", 
	    				 EntityCondition.makeCondition("slot", EntityOperator.EQUALS,orderHeader.getString("slot")),
						 															UtilMisc.toSet("number","slot"), UtilMisc.toList(""), null, true);
	    	 }catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
	    return deliveryBoyVehicleDetails;
	 }
	 
	 public static String createDeliveryBoyVehicle(HttpServletRequest request, HttpServletResponse response){
	     Delegator delegator = (Delegator) request.getAttribute("delegator");
	     GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	     if(UtilValidate.isEmpty(userLogin))
	     {
	     	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
	     	return "error";
	     }
	     
	     String deliveryBoyId = request.getParameter("deliveryBoyId");
	     String vehicleNumber = request.getParameter("vehicleNumber");
	     String slot = request.getParameter("slotType");
	     String zoneGroupId = request.getParameter("zoneGroupId");
	     
		 GenericValue deliveryBoyVehicleDetails = null;
	     try {
	    	 TransactionUtil.begin();
	    	 deliveryBoyVehicleDetails = delegator.findOne("DeliveryBoyVehicleDetails", UtilMisc.toMap("vehicleNumber",vehicleNumber), false);
	    	 String deliveryBoyName = PartyHelper.getPartyName(delegator, deliveryBoyId, false);
	    	 List<GenericValue> slotList = delegator.findByAnd("OrderSlotType", UtilMisc.toMap("slotType",slot));
	    	 
	    	 GenericValue slotValue = null;
	    	 if(UtilValidate.isNotEmpty(slotList))
	    		 slotValue = EntityUtil.getFirst(slotList);

	    	 if(UtilValidate.isNotEmpty(deliveryBoyVehicleDetails))
	    	 {
	    		 String returnValue = createDeliveryBoyVehicleHistory(deliveryBoyVehicleDetails,userLogin.getString("userLoginId"));
	    		 if("success".equals(returnValue))
	    		 {
		    		 deliveryBoyVehicleDetails.put("deliveryBoyId", deliveryBoyId);
		    		 deliveryBoyVehicleDetails.put("deliveryBoyName", deliveryBoyName);
		    		 if(UtilValidate.isNotEmpty(slot))
		    		 {
			    		 deliveryBoyVehicleDetails.put("slot", slot);
			    		 deliveryBoyVehicleDetails.put("slotTiming", slotValue.getString("slotTiming"));
		    		 }
		    		 deliveryBoyVehicleDetails.put("createdDate", UtilDateTime.nowTimestamp());
		    		 deliveryBoyVehicleDetails.put("createdBy", userLogin.getString("userLoginId"));
		    		 
		    		 deliveryBoyVehicleDetails.put("zoneGroupId", zoneGroupId);
		    		 deliveryBoyVehicleDetails.put("zoneGroupName", ZoneManagement.zoneGroupName(delegator, zoneGroupId));
		    		 
		    		 deliveryBoyVehicleDetails.store();
	    		 }else{
	    			 request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Vehicle For Delivery Boy");
	    			 TransactionUtil.rollback();
	    		 }
	    	 }
	    	 else{
	    		 deliveryBoyVehicleDetails = delegator.makeValue("DeliveryBoyVehicleDetails");
	    		 
	    		 deliveryBoyVehicleDetails.put("vehicleNumber", vehicleNumber);
	    		 deliveryBoyVehicleDetails.put("deliveryBoyId", deliveryBoyId);
	    		 deliveryBoyVehicleDetails.put("deliveryBoyName", deliveryBoyName);
	    		 if(UtilValidate.isNotEmpty(slot))
	    		 {
		    		 deliveryBoyVehicleDetails.put("slot", slot);
		    		 deliveryBoyVehicleDetails.put("slotTiming", slotValue.getString("slotTiming"));
	    		 }
	    		 deliveryBoyVehicleDetails.put("createdDate", UtilDateTime.nowTimestamp());
	    		 deliveryBoyVehicleDetails.put("createdBy", userLogin.getString("userLoginId"));
	    		 
	    		 deliveryBoyVehicleDetails.put("zoneGroupId", zoneGroupId);
	    		 deliveryBoyVehicleDetails.put("zoneGroupName", ZoneManagement.zoneGroupName(delegator, zoneGroupId));
	    		 
	    		 deliveryBoyVehicleDetails.create();
	    	 }
	    	 request.setAttribute("_ENENT_MESSAGE_", "Assigned Vehicle "+vehicleNumber+" For Delivery Boy "+PartyHelper.getPartyName(delegator, deliveryBoyId, false));
			 TransactionUtil.commit();
	    	 
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Vehicle For Delivery Boy");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
	    return "success";
	 }
	 
	 
	 public static String createDeliveryBoyVehicleHistory(GenericValue deliveryBoyVehicleDetails, String userLoginId){
		 Delegator delegator = deliveryBoyVehicleDetails.getDelegator();
		 GenericValue deliveryBoyVehicleDetailsHistory = delegator.makeValue("DeliveryBoyVehicleDetailsHistory");
		 
		 deliveryBoyVehicleDetailsHistory.put("deliveryBoyVehicleDetailsHistoryId", delegator.getNextSeqId("DeliveryBoyVehicleDetailsHistory"));
		 deliveryBoyVehicleDetailsHistory.put("vehicleNumber", deliveryBoyVehicleDetails.getString("vehicleNumber"));
		 deliveryBoyVehicleDetailsHistory.put("deliveryBoyId", deliveryBoyVehicleDetails.getString("deliveryBoyId"));
		 deliveryBoyVehicleDetailsHistory.put("deliveryBoyName", deliveryBoyVehicleDetails.getString("deliveryBoyName"));
		 deliveryBoyVehicleDetailsHistory.put("slot", deliveryBoyVehicleDetails.getString("slot"));
		 deliveryBoyVehicleDetailsHistory.put("slotTiming", deliveryBoyVehicleDetails.getString("slotTiming"));
		 
		 deliveryBoyVehicleDetailsHistory.put("slot", deliveryBoyVehicleDetails.getString("zoneGroupId"));
		 deliveryBoyVehicleDetailsHistory.put("slotTiming", deliveryBoyVehicleDetails.getString("zoneGroupName"));
		 
		 deliveryBoyVehicleDetailsHistory.put("createdDate", deliveryBoyVehicleDetails.getTimestamp("createdDate"));
		 deliveryBoyVehicleDetailsHistory.put("updatedDate", UtilDateTime.nowTimestamp());
		 deliveryBoyVehicleDetailsHistory.put("createdBy", userLoginId);
		 deliveryBoyVehicleDetailsHistory.put("updatedBy", deliveryBoyVehicleDetails.getString("createdBy"));
		 
		 try {
			deliveryBoyVehicleDetailsHistory.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
		return "success";
	 }
	 
	 public static List<GenericValue> assignedVechilesToDeliveryBoy(Delegator delegator){
		 List<GenericValue> deliveryBoyVehicleDetails = null;
		 try {
			deliveryBoyVehicleDetails = delegator.findList("DeliveryBoyVehicleDetails", null, 
																		UtilMisc.toSet("vehicleNumber","deliveryBoyName","zoneGroupName"), null, null, true);
			
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return deliveryBoyVehicleDetails;
	 }
	 
	 public static String thresholdForVehicle(HttpServletRequest request, HttpServletResponse response){
		 Delegator delegator = (Delegator) request.getAttribute("delegator");
		 String vehicleNumber = request.getParameter("vehicleNumber");
		 return thresholdForVehicle(delegator, vehicleNumber);
	 }
	 public static String thresholdForVehicle(Delegator delegator, String vehicleNumber){
	
		 List<GenericValue> shipmentList = null;
		 List<GenericValue> shipmentPackageList = null;
		 try {
			 List conditionList = new ArrayList();
			 BigDecimal orderIdThreshold = null;
			 BigDecimal weightThreshold = null;
			 
			 GenericValue vehicleDetail = delegator.findOne("VehicleDetails", false, UtilMisc.toMap("vehicleNumber",vehicleNumber));
			 if(UtilValidate.isNotEmpty(vehicleDetail))
			 {
				 orderIdThreshold = vehicleDetail.getBigDecimal("orderIdThreshold");
				 weightThreshold = vehicleDetail.getBigDecimal("weightThreshold");
			 }
			 conditionList.add(EntityCondition.makeCondition("vehicleNumber",EntityOperator.EQUALS,vehicleNumber));
			 conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"SALES_SHIPMENT"));
			 conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"SHIPMENT_PACKED"));
			 conditionList.add(EntityCondition.makeCondition("vehicleNumberAssignedDate",EntityOperator.EQUALS,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
			 
			 shipmentList = delegator.findList("Shipment", 
					EntityCondition.makeCondition(conditionList,EntityOperator.AND), UtilMisc.toSet("shipmentId","primaryOrderId"), null, null, true);
			 
			 String message = "Available";
			 if(UtilValidate.isEmpty(shipmentList))
			 {
				 message = "Order Threshold "+orderIdThreshold.doubleValue()+"   ";
				 message = message+"Weight Threshold "+weightThreshold.doubleValue()+"   ";
				 return message;
			 }
				 
			 List<String> orderIds = EntityUtil.getFieldListFromEntityList(shipmentList, "primaryOrderId", true);
			 
			 if(UtilValidate.isNotEmpty(orderIdThreshold) && UtilValidate.isNotEmpty(orderIds) && 
					 									orderIds.size() >= orderIdThreshold.doubleValue())
			 {
				 message = "Order Threshold "+" Not Available "+"   ";
				 message = message+"Weight Threshold "+weightThreshold.doubleValue()+"   ";
				 return message;
			 }
			 
			 List<String> shipmentIds = EntityUtil.getFieldListFromEntityList(shipmentList, "shipmentId", true);
			 
			 shipmentPackageList = delegator.findList("ShipmentPackage", 
						EntityCondition.makeCondition("shipmentId",EntityOperator.IN,shipmentIds), UtilMisc.toSet("weight","weightUomId"), null, null, true);
			 
			 if(UtilValidate.isEmpty(shipmentPackageList))
			 {
				 message = "Order Threshold "+orderIdThreshold.doubleValue()+"   ";
				 message = message+"Weight Threshold "+weightThreshold.doubleValue()+"   ";
				 return message;
			 }
			 
			 BigDecimal totalWeight = BigDecimal.ZERO;
			 for(GenericValue shipmentPackage : shipmentPackageList){
				 BigDecimal weight = shipmentPackage.getBigDecimal("weight");
				 if(UtilValidate.isNotEmpty(weight))
					 totalWeight = totalWeight.add(weight);
			 }
			 if(UtilValidate.isNotEmpty(weightThreshold) && UtilValidate.isNotEmpty(totalWeight) && 
					 							totalWeight.doubleValue() >= weightThreshold.doubleValue())
			 {
				 message = "Order Threshold "+orderIdThreshold.doubleValue()+"   ";
				 message = message+"Weight Threshold "+" Not Available "+"   ";
				 return message;
			 }
			 if(UtilValidate.isNotEmpty(orderIdThreshold))
				 message = "Order Threshold "+(orderIdThreshold.doubleValue() - orderIds.size())+"   ";
			 if(UtilValidate.isNotEmpty(weightThreshold))
				 message = message+"Weight Threshold "+(weightThreshold.doubleValue() - totalWeight.doubleValue())+"   ";

			 return message;
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
	 }
	 
	 public static List<GenericValue> orderShipments(Delegator delegator, String primaryOrderId) throws Exception{
			 List conditionList = new ArrayList();
			 conditionList.add(EntityCondition.makeCondition("primaryOrderId",EntityOperator.EQUALS,primaryOrderId));
			 conditionList.add(EntityCondition.makeCondition("shipmentTypeId",EntityOperator.EQUALS,"SALES_SHIPMENT"));
			 conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"SHIPMENT_PACKED"));
			 
			 return delegator.findList("Shipment", 
					EntityCondition.makeCondition(conditionList,EntityOperator.AND), 
					UtilMisc.toSet("shipmentId","primaryOrderId","vehicleNumber","deliveryBoyId"), null, null, true);
			 
	 }
	 
	 public static List<GenericValue> vehicleWithThreshold(Delegator delegator){
		 List<GenericValue> deliveryBoyVehicleDetails = assignedVechilesToDeliveryBoy(delegator);
		 if(UtilValidate.isNotEmpty(deliveryBoyVehicleDetails))
		 for(GenericValue deliveryBoyVehicleDetail : deliveryBoyVehicleDetails){
			 String vehicleNumber = deliveryBoyVehicleDetail.getString("vehicleNumber");
			 deliveryBoyVehicleDetail.put("slot", thresholdForVehicle(delegator, vehicleNumber));
		 }
		return deliveryBoyVehicleDetails;
	 }
	 
}