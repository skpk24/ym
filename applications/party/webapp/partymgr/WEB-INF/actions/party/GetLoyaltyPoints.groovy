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

import java.lang.*;
import java.math.BigDecimal;
import java.util.*;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.ServiceDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.order.order.OrderReturnServices

partyId = parameters.partyId ? parameters.partyId : userLogin.partyId ;
userLoginId = null;
orderHeader = context.orderHeader;
if(orderHeader)
{
	userLoginId = orderHeader.createdBy;
	userLogin = delegator.findByPrimaryKey("UserLogin", [userLoginId : userLoginId]);
	if(userLogin) partyId = userLogin.partyId;
}

leftloyaltyPoints = null;
totalloyaltyPoints = null;
leftBalance =null;
orderPaymentPreferences=null;
if (partyId) {
    // get the system user
	userData = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
	if((UtilValidate.isNotEmpty(userData)) && userData.size() == 1){
		GenericValue login = EntityUtil.getFirst(userData);
		userLoginId = login.getString("userLoginId");
		loyaltyPointsData = delegator.findByPrimaryKey("LoyaltyPoint", [userLoginId : userLoginId]);
		if(loyaltyPointsData != null){
			leftloyaltyPoints = loyaltyPointsData.getString("loyaltyPoint");
			context.leftloyaltyPoints=leftloyaltyPoints;
		}
		partyBillValue  = delegator.findList("BillingAccountRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false);
		if (UtilValidate.isNotEmpty(partyBillValue)) {
			for (GenericValue partyBill : partyBillValue) {
				EntityCondition barFindCond = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, partyBill.getString("billingAccountId")),
					EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + "Credit Account for Loyalty #") +partyId+"%"))), EntityOperator.AND);
				List<GenericValue> billingAccountRoleList = delegator.findList("BillingAccount", barFindCond, null, null, null, false);
				if (UtilValidate.isNotEmpty(billingAccountRoleList)) {
					dctx = dispatcher.getDispatchContext();
					leftBalance = OrderReturnServices.getBillingAccountBalance(partyBill.getString("billingAccountId"), dctx);
					totalloyaltyPoints = (leftBalance.multiply(200)).add(new BigDecimal(leftloyaltyPoints));
					context.totalloyaltyPoints=totalloyaltyPoints;
					context.leftBalance=leftBalance;
				}
			}
		}
		ecl = EntityCondition.makeCondition([
			EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_BILLACT"),
			EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, userLoginId)],
		EntityOperator.AND);
		orderFieldsToSelect = UtilMisc.toSet("orderId", "maxAmount");
		orderPaymentPreferences = delegator.findList("OrderPaymentPreference", ecl, orderFieldsToSelect, null, null, false);
		context.orderPaymentPreferences = orderPaymentPreferences;
	}
	
	context.orderPaymentPreferences = orderPaymentPreferences;
	context.leftloyaltyPoints = leftloyaltyPoints;
	context.totalloyaltyPoints = totalloyaltyPoints;
	context.leftBalance = leftBalance;
}