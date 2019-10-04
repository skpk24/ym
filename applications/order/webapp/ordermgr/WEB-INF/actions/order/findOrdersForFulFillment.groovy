/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.order.*;
import org.ofbiz.entity.condition.*;


context.assignedVehicleToDeliveryBoy = org.ofbiz.order.DeliveryBoyVechileManagement.assignedVechilesToDeliveryBoy(delegator);
context.slotTypes = org.ofbiz.order.shoppingcart.CheckOutEvents.getAllSlots(delegator);

listOfCond = new java.util.ArrayList();
listOfCond.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.NOT_EQUAL, "EXT_OFFLINE"));
listOfCond.add(EntityCondition.makeCondition("isCodPayment", EntityOperator.EQUALS, "Y"));

context.paymentMethodTypes = delegator.findList("PaymentMethodType", EntityCondition.makeCondition(listOfCond,EntityOperator.AND), null, null, null, true);

vehicleNumber = parameters.vehicleNumber;
slot = parameters.slot;

listOfData = new java.util.ArrayList();
//totalAmt = java.math.BigDecimal.ZERO;

paymentDetails = new java.util.HashMap();
data = new java.util.HashMap();

listOfCond = new java.util.ArrayList();
	listOfCond.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_DISPATCHED"));
if(vehicleNumber)
	listOfCond.add(EntityCondition.makeCondition("vehicleNumber", EntityOperator.EQUALS, vehicleNumber));
if(slot)
	listOfCond.add(EntityCondition.makeCondition("slot", EntityOperator.EQUALS, slot));
    
cond = EntityCondition.makeCondition(listOfCond,EntityOperator.AND);

    org.ofbiz.entity.util.EntityFindOptions findOptions = new org.ofbiz.entity.util.EntityFindOptions();
    findOptions.setDistinct(true);
    
    orderBy = new java.util.ArrayList();
    orderBy.add("orderId DESC");
    orderList = delegator.findList("OrderHeader", cond, null, orderBy , findOptions, false);

	if(orderList)
		{
			orderList.each { orderHeader ->
			data = new java.util.HashMap();
					if(orderHeader)
					{
						orderId = orderHeader.orderId;
						orderReadHelper = new OrderReadHelper(orderHeader);
						grandTotal = orderReadHelper.getOrderGrandTotal();
							
						shipments = org.ofbiz.order.DeliveryBoyVechileManagement.orderShipments(delegator, orderId);
					    		
								statusId = orderHeader.statusId;
								data.put("deliveryBoyId",orderHeader.deliveryBoyId);
								data.put("vehicleNumber",orderHeader.vehicleNumber);
								data.put("orderId",orderId);
							    	
							    data.put("grandTotal", grandTotal);
							    data.put("amtToRecive", orderReadHelper.getCashOnDeliveryAmountToReceive());
							    
							    ecl = EntityCondition.makeCondition([
			                                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderHeader.orderId),
			                                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")],
			                                EntityOperator.AND);
			                                
							    orderPaymentPreferences = delegator.findList("OrderPaymentPreference", 
							    				EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderHeader.orderId), null, null, null, false);
							    
							    paymentList = new java.util.ArrayList();
							    paymentMethodIdList = new java.util.ArrayList();
							    isCOD = false;
							    orderPaymentPreferences.each { orderPaymentPreference ->
							    	paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType");
							    	/*
							    	paymentAmt = orderPaymentPreference.maxAmount;
							    	if(paymentDetails.containsKey(paymentMethodType.description))
							    	{
							    		amt = paymentDetails.get(paymentMethodType.description);
							    		if(amt != null && paymentAmt != null)
							    			paymentDetails.put(paymentMethodType.description,paymentAmt.add(amt));
							    	}
							    	else
							    	{
							    		paymentDetails.put(paymentMethodType.description,paymentAmt);
							    	}*/
							    	
							    	if(!paymentMethodIdList.contains(paymentMethodType.paymentMethodTypeId))
							    	{
							    		paymentList.add(paymentMethodType.description);
							    		paymentMethodIdList.add(paymentMethodType.paymentMethodTypeId)
							    		if("EXT_COD".equals(paymentMethodType.paymentMethodTypeId))
							    			isCOD = true;
							    	}
							    }
							    data.put("paymentMethod", paymentList);
							    data.put("isCOD", isCOD);
							
							if(shipments)
					    			{
					    				shipments.each { shipment ->
					    					data.put("shipmentId",shipment.shipmentId);
											data.put("deliveryBoyId",shipment.deliveryBoyId);
											data.put("vehicleNumber",shipment.vehicleNumber);
					    				}
					    			}
					 }
				if(data != null && data.size() > 0)
				listOfData.add(data);
			}
			}

context.listOfData = listOfData;
//context.paymentDetails = paymentDetails;
//context.totalAmt = totalAmt;

