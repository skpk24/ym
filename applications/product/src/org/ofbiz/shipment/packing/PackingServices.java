/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.shipment.packing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.shipment.packing.PackingSession.ItemDisplay;

public class PackingServices {

    public static final String module = PackingServices.class.getName();
    public static final String resource = "ProductUiLabels";

    public static Map<String, Object> addPackLine(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal weight = (BigDecimal) context.get("weight");
        Integer packageSeq = (Integer) context.get("packageSeq");

        // set the instructions -- will clear out previous if now null
        String instructions = (String) context.get("handlingInstructions");
        session.setHandlingInstructions(instructions);

        // set the picker party id -- will clear out previous if now null
        String pickerPartyId = (String) context.get("pickerPartyId");
        session.setPickerPartyId(pickerPartyId);

        if (quantity == null) {
            quantity = BigDecimal.ONE;
        }

        Debug.log("OrderId [" + orderId + "] ship group [" + shipGroupSeqId + "] Pack input [" + productId + "] @ [" + quantity + "] packageSeq [" + packageSeq + "] weight [" + weight +"]", module);

        if (weight == null) {
            Debug.logWarning("OrderId [" + orderId + "] ship group [" + shipGroupSeqId + "] product [" + productId + "] being packed without a weight, assuming 0", module);
            weight = BigDecimal.ZERO;
        }

        try {
            session.addOrIncreaseLine(orderId, null, shipGroupSeqId, productId, quantity, packageSeq.intValue(), weight, false);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * <p>Create or update package lines.</p>
     * <p>Context parameters:
     * <ul>
     * <li>selInfo - selected rows</li>
     * <li>iteInfo - orderItemIds</li>
     * <li>prdInfo - productIds</li>
     * <li>pkgInfo - package numbers</li>
     * <li>wgtInfo - weights to pack</li>
     * <li>numPackagesInfo - number of packages to pack per line (>= 1, default: 1)<br/>
     * Packs the same items n times in consecutive packages, starting from the package number retrieved from pkgInfo.</li>
     * </ul>
     * </p>
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> packBulk(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Boolean updateQuantity = (Boolean) context.get("updateQuantity");
        Locale locale = (Locale) context.get("locale");
        if (updateQuantity == null) {
            updateQuantity = Boolean.FALSE;
        }

        // set the instructions -- will clear out previous if now null
        String instructions = (String) context.get("handlingInstructions");
        session.setHandlingInstructions(instructions);

        // set the picker party id -- will clear out previous if now null
        String pickerPartyId = (String) context.get("pickerPartyId");
        session.setPickerPartyId(pickerPartyId);

        Map<String, ?> selInfo = UtilGenerics.checkMap(context.get("selInfo"));
        Map<String, String> iteInfo = UtilGenerics.checkMap(context.get("iteInfo"));
        Map<String, String> prdInfo = UtilGenerics.checkMap(context.get("prdInfo"));
        Map<String, String> qtyInfo = UtilGenerics.checkMap(context.get("qtyInfo"));
        Map<String, String> pkgInfo = UtilGenerics.checkMap(context.get("pkgInfo"));
        Map<String, String> wgtInfo = UtilGenerics.checkMap(context.get("wgtInfo"));
        Map<String, String> numPackagesInfo = UtilGenerics.checkMap(context.get("numPackagesInfo"));
        Map<String, String> boxTypeInfo = UtilGenerics.checkMap(context.get("boxTypeInfo"));

        if (selInfo != null) {
            for (String rowKey: selInfo.keySet()) {
                String orderItemSeqId = iteInfo.get(rowKey);
                String prdStr = prdInfo.get(rowKey);
                if (UtilValidate.isEmpty(prdStr)) {
                    // set the productId to null if empty
                    prdStr = null;
                }

                // base package/quantity/weight strings
                String pkgStr = pkgInfo.get(rowKey);
                String qtyStr = qtyInfo.get(rowKey);
                String wgtStr = wgtInfo.get(rowKey);
                String boxType = boxTypeInfo.get(rowKey);
                session.setShipmentBoxTypeId(boxType);

                Debug.log("Item: " + orderItemSeqId + " / Product: " + prdStr + " / Quantity: " + qtyStr + " /  Package: " + pkgStr + " / Weight: " + wgtStr, module);

                // array place holders
                String[] quantities;
                String[] packages;
                String[] weights;

                // process the package array
                if (pkgStr.indexOf(",") != -1) {
                    // this is a multi-box update
                    packages = pkgStr.split(",");
                } else {
                    packages = new String[] { pkgStr };
                }

                // check to make sure there is at least one package
                if (packages == null || packages.length == 0) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "ProductPackBulkNoPackagesDefined", locale));
                }

                // process the quantity array
                if (qtyStr == null) {
                    quantities = new String[packages.length];
                    for (int p = 0; p < packages.length; p++) {
                        quantities[p] = qtyInfo.get(rowKey + ":" + packages[p]);
                    }
                    if (quantities.length != packages.length) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "ProductPackBulkPackagesAndQuantitiesDoNotMatch", locale));
                    }
                } else {
                    quantities = new String[] { qtyStr };
                }

                // process the weight array
                if (UtilValidate.isEmpty(wgtStr)) wgtStr = "0";
                weights = new String[] { wgtStr };

                for (int p = 0; p < packages.length; p++) {
                    BigDecimal quantity;
                    int packageSeq;
                    BigDecimal weightSeq;
                    try {
                        quantity = new BigDecimal(quantities[p]);
                        packageSeq = Integer.parseInt(packages[p]);
                        weightSeq = new BigDecimal(weights[p]);
                    } catch (Exception e) {
                        return ServiceUtil.returnError(e.getMessage());
                    }

                    try {
                        String numPackagesStr = numPackagesInfo.get(rowKey);
                        int numPackages = 1;
                        if (numPackagesStr != null) {
                            try {
                                numPackages = Integer.parseInt(numPackagesStr);
                                if (numPackages < 1) {
                                    numPackages = 1;
                                }
                            } catch (NumberFormatException nex) {
                            }
                        }
                        for (int numPackage=0; numPackage<numPackages; numPackage++) {
                            session.addOrIncreaseLine(orderId, orderItemSeqId, shipGroupSeqId, prdStr, quantity, packageSeq+numPackage, weightSeq, updateQuantity.booleanValue());
                        }
                    } catch (GeneralException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> incrementPackageSeq(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        int nextSeq = session.nextPackageSeq();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("nextPackageSeq", Integer.valueOf(nextSeq));
        return result;
    }

    public static Map<String, Object> clearLastPackage(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        int nextSeq = session.clearLastPackage();
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("nextPackageSeq", Integer.valueOf(nextSeq));
        return result;
    }

    public static Map<String, Object> clearPackLine(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String inventoryItemId = (String) context.get("inventoryItemId");
        String productId = (String) context.get("productId");
        Integer packageSeqId = (Integer) context.get("packageSeqId");
        Locale locale = (Locale) context.get("locale");

        PackingSessionLine line = session.findLine(orderId, orderItemSeqId, shipGroupSeqId,
                productId, inventoryItemId, packageSeqId.intValue());

        // remove the line
        if (line != null) {
            session.clearLine(line);
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPackLineNotFound", locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> clearPackAll(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        session.clearAllLines();

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> calcPackSessionAdditionalShippingCharge(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        Map<String, String> packageWeights = UtilGenerics.checkMap(context.get("packageWeights"));
        String weightUomId = (String) context.get("weightUomId");
        String shippingContactMechId = (String) context.get("shippingContactMechId");
        String shipmentMethodTypeId = (String) context.get("shipmentMethodTypeId");
        String carrierPartyId = (String) context.get("carrierPartyId");
        String carrierRoleTypeId = (String) context.get("carrierRoleTypeId");
        String productStoreId = (String) context.get("productStoreId");

        BigDecimal shippableWeight = setSessionPackageWeights(session, packageWeights);
        BigDecimal estimatedShipCost = session.getShipmentCostEstimate(shippingContactMechId, shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId, productStoreId, null, null, shippableWeight, null);
        session.setAdditionalShippingCharge(estimatedShipCost);
        session.setWeightUomId(weightUomId);

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("additionalShippingCharge", estimatedShipCost);
        return result;
    }


    public static Map<String, Object> completePack(DispatchContext dctx, Map<String, ? extends Object> context) {
        PackingSession session = (PackingSession) context.get("packingSession");
        Locale locale = (Locale) context.get("locale");
        
        // set the instructions -- will clear out previous if now null
        
        String orderId = (String) context.get("orderId");
        String instructions = (String) context.get("handlingInstructions");
        String vehicleNumber = (String) context.get("vehicleNumber");
        String partialPacking = (String) context.get("partialPacking");
        if(UtilValidate.isEmpty(partialPacking))partialPacking = "N";
        
        String pickerPartyId = (String) context.get("pickerPartyId");
        BigDecimal additionalShippingCharge = (BigDecimal) context.get("additionalShippingCharge");
        Map<String, String> packageWeights = UtilGenerics.checkMap(context.get("packageWeights"));
        String weightUomId = (String) context.get("weightUomId");
        session.setHandlingInstructions(instructions);
        session.setPickerPartyId(pickerPartyId);
        session.setAdditionalShippingCharge(additionalShippingCharge);
        session.setWeightUomId(weightUomId);
        session.setVehicleNumber(vehicleNumber);
        if("Y".equals(partialPacking))
        	session.setPartialPacking(true);
        else
        	session.setPartialPacking(false);
        
        setSessionPackageWeights(session, packageWeights);

        Boolean force = (Boolean) context.get("forceComplete");
        if (force == null) {
            force = Boolean.FALSE;
        }

        String shipmentId = null;
        try {
            shipmentId = session.complete(force);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage(), e.getMessageList());
        }

        Map<String, Object> resp;
        if ("EMPTY".equals(shipmentId)) {
            resp = ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPackCompleteNoItems", locale));
        } else {
            resp = ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, 
                    "ProductPackComplete", UtilMisc.toMap("shipmentId", shipmentId), locale));
            
            additionalShippingCharge(dctx.getDelegator(), dctx.getDispatcher(), (GenericValue) context.get("userLogin"), additionalShippingCharge, orderId, shipmentId);
        }

        resp.put("shipmentId", shipmentId);
        return resp;
    }
    
    public static void additionalShippingCharge(Delegator delegator, LocalDispatcher dispatcher, 
    												GenericValue userLogin, BigDecimal additionalShippingCharge, String orderId, String shipmentId){
        if(UtilValidate.isNotEmpty(userLogin))
        {
        	if(UtilValidate.isEmpty(additionalShippingCharge)) additionalShippingCharge = BigDecimal.ZERO;
        	
            Map shippingContext = UtilMisc.toMap("comments", "Added manually by ["+userLogin.getString("userLoginId")+"]", 
            		"orderId", orderId, "orderAdjustmentTypeId", "SHIPPING_CHARGES", "amount", additionalShippingCharge, 
            		"description", "Additional Shipping Charge at the time of packing");
            
            String shipGroupSeqId = "_NA_";
            try {
				GenericValue shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
				if(UtilValidate.isNotEmpty(shipment) && UtilValidate.isNotEmpty(shipment.getString("primaryShipGroupSeqId")))
					shipGroupSeqId = shipment.getString("primaryShipGroupSeqId");
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            
            shippingContext.put("shipGroupSeqId", shipGroupSeqId);
            shippingContext.put("userLogin", userLogin);
            
            try {
            	dispatcher.runSync("createOrderAdjustment", shippingContext);
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    public static BigDecimal setSessionPackageWeights(PackingSession session, Map<String, String> packageWeights) {
        BigDecimal shippableWeight = BigDecimal.ZERO;
        if (! UtilValidate.isEmpty(packageWeights)) {
            for (Map.Entry<String, String> entry: packageWeights.entrySet()) {
                String packageSeqId = entry.getKey();
                String packageWeightStr = entry.getValue();
                if (UtilValidate.isNotEmpty(packageWeightStr)) {
                    BigDecimal packageWeight = new BigDecimal(packageWeights.get(packageSeqId));
                    session.setPackageWeight(Integer.parseInt(packageSeqId), packageWeight);
                    shippableWeight = shippableWeight.add(packageWeight);
                } else {
                    session.setPackageWeight(Integer.parseInt(packageSeqId), null);
                }
            }
        }
        return shippableWeight;
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 1
     */
    
    public static Map<String, Object> packOrderForSlot1(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	packOrderSlotWise(delegator, dispatcher , "SLOT1");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 2
     */
    
    public static Map<String, Object> packOrderForSlot2(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	packOrderSlotWise(delegator, dispatcher , "SLOT2");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 3
     */
    
    public static Map<String, Object> packOrderForSlot3(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	packOrderSlotWise(delegator, dispatcher , "SLOT3");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 4
     */
    
    public static Map<String, Object> packOrderForSlot4(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	packOrderSlotWise(delegator, dispatcher , "SLOT4");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     *  Pack order slot wise
     *  
     */
    
    public static Map<String, Object> packOrderSlotWise(Delegator delegator , LocalDispatcher dispatcher , 
    																			String slotType) {
	    
    	List condition = UtilMisc.toList(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"),
        		EntityCondition.makeCondition("slotType",EntityOperator.EQUALS,slotType),
        		EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,
        				UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
        List<GenericValue> orderSlotList = null; 
        try {
        	orderSlotList = delegator.findList("OrderSlot", 
								EntityCondition.makeCondition(condition,EntityJoinOperator.AND), 
								null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        if(UtilValidate.isNotEmpty(orderSlotList))
        for(GenericValue orderSlot : orderSlotList){
        	String orderId = orderSlot.getString("orderId");
        	if(UtilValidate.isNotEmpty(orderId))
        	{
        		String returnedValue = packOrder(delegator, dispatcher, orderId);
        		if(UtilValidate.isNotEmpty(returnedValue) && "success".equals(returnedValue))
        		{
	        		orderSlot.put("slotStatus", "SLOT_PACKED");
	        		try {
						orderSlot.store();
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						Debug.logWarning("Could not update slot status for [" + orderId + "]  ", module);
			        	return null;
					}
        		}
        	}
        }
        return ServiceUtil.returnSuccess();
    }
    
    public static String packOrder(Delegator delegator , LocalDispatcher dispatcher , String orderId){
    	
    	GenericValue orderHeader = getOrderHeader(delegator, orderId);
        if (orderHeader == null) {
            Debug.logWarning("Could not find OrderHeader for orderId [" + orderId + "] in getProductStoreFromOrder, returning null", module);
            return null;
        }
        String orderStatus = orderHeader.getString("statusId");
        if(UtilValidate.isEmpty(orderStatus) || !"ORDER_APPROVED".equals(orderStatus))
        {
        	Debug.logWarning("Could not pack order for orderId [" + orderId + "]  order status is not approved ", module);
        	return null;
        }
        
        GenericValue productStore = getProductStoreFromOrder(orderHeader);
    	
        if (productStore == null) {
        	Debug.logError("Could not find ProductStore for orderId [" + orderId + "], cannot pack order.", module);
           // throw new IllegalArgumentException("Could not find ProductStore for orderId [" + orderId + "], cannot pack order.");
            return null;
        }
        
        GenericValue userLogin = null;
        String userLoginId="system";
        try {
            userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
            return null;
           // request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "payback.problemsGettingAuthenticationUser", locale));
          //  return "error";
        }
        
        try {
        	 // set the status on the order header
            Map statusFields = UtilMisc.toMap("orderId", orderId, "statusId", "ORDER_PACKED", "userLogin", userLogin);
            Map statusResult = dispatcher.runSync("changeOrderStatus", statusFields);
            if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
                Debug.logError("Problems changing order status to packed for #" + orderId, module);
                return null;
            }

        	//OrderServices.sendOrderSms(dispatcher.getDelegator(), orderId);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
            return null;
        }
        return "success";
       // return true;
    }
    
    

    /*
     * @Ajaya
     * Service for dispatching packed orders for slot 1
     */
    
    public static Map<String, Object> dispatchOrderForSlot1(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
    	dispatchOrderSlotWise(delegator, dispatcher , "SLOT1");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 2
     */
    
    public static Map<String, Object> dispatchOrderForSlot2(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
        dispatchOrderSlotWise(delegator, dispatcher , "SLOT2");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 3
     */
    
    public static Map<String, Object> dispatchOrderForSlot3(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
        dispatchOrderSlotWise(delegator, dispatcher , "SLOT3");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     * Service for packing approved orders for slot 4
     */
    
    public static Map<String, Object> dispatchOrderForSlot4(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
    	
        dispatchOrderSlotWise(delegator, dispatcher , "SLOT4");
    	return ServiceUtil.returnSuccess();
    }
    
    /*
     * @Ajaya
     *  Pack order slot wise
     *  
     */
    
    public static Map<String, Object> dispatchOrderSlotWise(Delegator delegator , LocalDispatcher dispatcher , 
    																			String slotType) {
        
		List condition = UtilMisc.toList(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_PACKED"),
	        				EntityCondition.makeCondition("slotType",EntityOperator.EQUALS,slotType),
	                		EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,
	                				UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
		List<GenericValue> orderSlotList = null;
	        try {
	        	orderSlotList = delegator.findList("OrderSlot",
									EntityCondition.makeCondition(condition,EntityJoinOperator.AND), 
									null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        if(UtilValidate.isNotEmpty(orderSlotList))
	        for(GenericValue orderSlot : orderSlotList){
	        	String orderId = orderSlot.getString("orderId");
	        	if(UtilValidate.isNotEmpty(orderId))
	        	{

	        		String returnedValue = dispatchOrder(delegator, dispatcher, orderId);
	        		if(UtilValidate.isNotEmpty(returnedValue) && "success".equals(returnedValue))
	        		{
		        		orderSlot.put("slotStatus", "SLOT_DISPATCHED");
		        		try {
							orderSlot.store();
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							Debug.logWarning("Could not update slot status for [" + orderId + "]  ", module);
				        	return null;
						}
	        		}
	        	}
	        }
        return null;
    }
    
    public static String dispatchOrder(Delegator delegator , LocalDispatcher dispatcher , String orderId){
    	
    	GenericValue orderHeader = getOrderHeader(delegator, orderId);
        if (orderHeader == null) {
            Debug.logWarning("Could not find OrderHeader for orderId [" + orderId + "] in getProductStoreFromOrder, returning null", module);
            return null;
        }
        String orderStatus = orderHeader.getString("statusId");
        if(UtilValidate.isEmpty(orderStatus) || !"ORDER_PACKED".equals(orderStatus))
        {
        	Debug.logWarning("Could not dispatch order for orderId [" + orderId + "]  order status is not packed ", module);
        	return null;
        }
        
        GenericValue productStore = getProductStoreFromOrder(orderHeader);
    	
        if (productStore == null) {
        	Debug.logError("Could not find ProductStore for orderId [" + orderId + "], cannot pack order.", module);
            // throw new IllegalArgumentException("Could not find ProductStore for orderId [" + orderId + "], cannot pack order.");
            return null;
        }
        
        GenericValue userLogin = null;
        String userLoginId="system";
        try {
            userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
           // request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceErr, "payback.problemsGettingAuthenticationUser", locale));
          //  return "error";
            return null;
        }
        
        try {
        	 // set the status on the order header
            Map statusFields = UtilMisc.toMap("orderId", orderId, "statusId", "ORDER_DISPATCHED", "userLogin", userLogin);
            Map statusResult = dispatcher.runSync("changeOrderStatus", statusFields);
            if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
                Debug.logError("Problems changing order status to packed for #" + orderId, module);
            }
        	//OrderServices.sendOrderSms(dispatcher.getDelegator(), orderId);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Service invocation error, status changes were not updated for order #" + orderId, module);
           // return false;
            return null;
        }
        return "success";
       // return true;
    }
    
    public static GenericValue getOrderHeader(Delegator delegator, String orderId) {
        GenericValue orderHeader = null;
        if (orderId != null && delegator != null) {
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get order header", module);
            }
        }
        return orderHeader;
    }
    
    public static GenericValue getProductStoreFromOrder(GenericValue orderHeader) {
        if (orderHeader == null) {
            return null;
        }
        Delegator delegator = orderHeader.getDelegator();
        GenericValue productStore = null;
        if (orderHeader.get("productStoreId") != null) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", orderHeader.getString("productStoreId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot locate ProductStore from OrderHeader", module);
            }
        } else {
            Debug.logError("Null header or productStoreId", module);
        }
        return productStore;
    }
    
    protected static void receiveQOHForPacking(String shipmentId,String orderId, BigDecimal quantity, String facilityId, String productId, GenericValue userLogin, LocalDispatcher dispatcher, Delegator delegator) throws GeneralException {
        // assign item to package
    	
    	if(UtilValidate.isEmpty(facilityId)) facilityId = "WebStoreWarehouse";
    	
    	BigDecimal QOH = ProductWorker.totalAvailableQOH(delegator, dispatcher, facilityId, productId, null);
    	boolean needToReceive = true;
    	if(UtilValidate.isNotEmpty(QOH) && UtilValidate.isNotEmpty(quantity))
    	{
    		if(QOH.longValue() < quantity.doubleValue())
    			quantity = quantity.subtract(QOH);
    		else
    			needToReceive = false;
    	}
    	if(!needToReceive) return;
    	
    	String comments = "receive inventory For Packing  "+orderId;
    	if(UtilValidate.isNotEmpty(shipmentId))
    		comments = comments +" with Shipment Id "+shipmentId;
    	
    	Map<String, Object> QOHMap = new HashMap<String, Object>(); //FastMap.newInstance();
    	QOHMap.put("shipmentId", shipmentId);
    	
    	QOHMap.put("quantityAccepted", quantity);
    	QOHMap.put("quantityRejected", BigDecimal.ZERO);
    	QOHMap.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
    	GenericValue product = delegator.findOne("Product", true, UtilMisc.toMap("productId", productId));
    	if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(product.getBigDecimal("invUnitCost")))
    	{
    		QOHMap.put("unitCost", product.getBigDecimal("invUnitCost"));
    	}else{
    		QOHMap.put("unitCost", BigDecimal.ZERO);
    	}
    	QOHMap.put("productId", productId);
    	QOHMap.put("facilityId", facilityId);
    	QOHMap.put("comments", comments);
    	QOHMap.put("userLogin", userLogin);
    	QOHMap.put("datetimeReceived", UtilDateTime.nowTimestamp());
    	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
    	QOHMap.put("currencyUomId", currencyUomId);
    	
    	
    	//Map<String, Object> packageResp = dispatcher.runSync("receiveInventoryProduct", QOHMap);
    	Map<String, Object> packageResp = dispatcher.runSync("receiveInventoryProductFromPacking", QOHMap);

        if (ServiceUtil.isError(packageResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(packageResp));
        }
    }
    
    public static String processBulkPackOrderNew(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
    	
    	PackingSession packSession = (PackingSession)request.getSession(true).getAttribute("packingSession");
    	String orderId = request.getParameter("orderId");
    	GenericValue order = null;
    	try {
			order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId",orderId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(UtilValidate.isEmpty(order))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Not Found for order Id "+orderId);
    		return "error";
    	}
    	
		if(UtilValidate.isEmpty(order.getString("statusId")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Status is not Approved. Cann't pack Items.");
    		return "error";
    	}
		else if("ORDER_DISPATCHED".equalsIgnoreCase(order.getString("statusId")))
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Order In Transit . Cann't pack Items.");
    		return "error";
    	}
		else if("ORDER_COMPLETED".equalsIgnoreCase(order.getString("statusId")))
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Fulfilled . Cann't pack Items.");
    		return "error";
    	}
		else if(!"ORDER_APPROVED".equalsIgnoreCase(order.getString("statusId")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Status is not Approved. Cann't pack Items.");
    		return "error";
    	}
		
		if(UtilValidate.isEmpty(packSession) || packSession.getStatus() == 0)
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Please Reload the Page");
    		return "error";
    	}
    	
    	String productIds = request.getParameter("productIds");
    	if(UtilValidate.isEmpty(productIds)) productIds = "";
    	String productIDS[] = productIds.split(" ");
    	if(UtilValidate.isEmpty(productIDS))
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Unable to receive Inventory . Products not checked properly ");
    		return "error";
    	}
    	
    	int totalPrducts = productIDS.length;

    	String facilityId = request.getParameter("facilityId");
    	String shipmentId = request.getParameter("shipmentId");
    	
    	/*if("Y".equalsIgnoreCase(order.getString("packingStarted")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Packing Already started for order Id "+orderId);
    		return "error";
    	}else
    	{
    		try {
    			order.put("packingStarted", "Y");
    			order.store();
    		} catch (GenericEntityException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	}*/
    	Map<String,Integer> products = new HashMap<String, Integer>();
    	for(int i=1;i<=totalPrducts;i++)
    	{
    		String productId = request.getParameter("prd_"+i);
    		String quantity = request.getParameter("qty_"+i);
    		if(UtilValidate.isEmpty(quantity) || !UtilValidate.isPositiveInteger(quantity)) quantity = "0";
    		if(products.containsKey(productId))
    			products.put(productId, products.get(productId)+Integer.parseInt(quantity));
    		else
    			products.put(productId, Integer.parseInt(quantity));
    	}
    	Set<String> keys = products.keySet();
    	
    	for(String productId : keys)
    	{
    		//String productId = request.getParameter("prd_"+i);
    		//String quantity = request.getParameter("qty_"+i);
    		int quantity = products.get(productId);
    		if(UtilValidate.isEmpty(quantity) || 0 == quantity)
        	{
        		request.setAttribute("_ERROR_MESSAGE_", "Quantity is ZERO for product "+productId+".Quantity should be greater than ZERO.");
        		return "error";
        	}
    		
    		try {
				receiveQOHForPacking(shipmentId, orderId, new BigDecimal(quantity), facilityId, productId, userLogin, dispatcher, delegator);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return "success";
    }
    
    /*public static String checkForPacking(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");

    	String orderId = request.getParameter("orderId");
    	GenericValue order = null;
    	try {
			order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId",orderId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(UtilValidate.isEmpty(order))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Not Found for order Id "+orderId);
    		return "error";
    	}
    	
		if(UtilValidate.isEmpty(order.getString("statusId")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Status is not Approved. Cann't pack Items.");
    		return "error";
    	}
		else if("ORDER_DISPATCHED".equalsIgnoreCase(order.getString("statusId")))
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Order In Transit . Cann't pack Items.");
    		return "error";
    	}
		else if("ORDER_COMPLETED".equalsIgnoreCase(order.getString("statusId")))
    	{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Fulfilled . Cann't pack Items.");
    		return "error";
    	}
		else if(!"ORDER_APPROVED".equalsIgnoreCase(order.getString("statusId")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Order Status is not Approved. Cann't pack Items.");
    		return "error";
    	}
		
		if("Y".equalsIgnoreCase(order.getString("packingStarted")))
		{
    		request.setAttribute("_ERROR_MESSAGE_", "Packing Already started for order Id "+orderId);
    		return "error";
    	}else
    	{
    		try {
    			order.put("packingStarted", "Y");
    			order.store();
    		} catch (GenericEntityException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	}
    	return "success";
    }*/
    
    public static List<ItemDisplay> calculateQuantityForPacking(PackingSession packingSession){
		
		 List productIds = new ArrayList();
		 List<ItemDisplay> itemDisplayListToRemove = new ArrayList<ItemDisplay>();
		 
		 Map<String,Integer> map = new HashMap<String,Integer>();
		 
		 List<ItemDisplay> itemInfos = packingSession.getItemInfos();
		 int count = 0;
		 if(UtilValidate.isNotEmpty(itemInfos))
		 for(ItemDisplay itemInfo : itemInfos)
		 {
			 GenericValue orderItem = itemInfo.getOrderItem();
			 if(UtilValidate.isNotEmpty(orderItem.getString("productId")))
				 if(productIds.contains(orderItem.getString("productId")))
				 {
					 int index = map.get(orderItem.getString("productId"));
					 itemDisplayListToRemove.add(itemInfo);
					 BigDecimal qtyToIncrease = itemInfo.getQuantity();
					 itemInfo = itemInfos.get(index);
					 itemInfo.setQuantity(itemInfo.getQuantity().add(qtyToIncrease));
				 }
				 else
				 {
					 productIds.add(orderItem.getString("productId"));
					 map.put(orderItem.getString("productId"), count);
				 }
		 }
		 
		 for(ItemDisplay itemInfo : itemDisplayListToRemove)
			 itemInfos.remove(itemInfo);
		
		return itemInfos;
	}
}
