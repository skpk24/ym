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

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;

cart = session.getAttribute("shoppingCart");
context.cart = cart;
Map m = new HashMap();
orderItems = cart.makeOrderItems();
for(GenericValue gv :orderItems){
	m.put(gv.getString("productCategoryId"), orderItems);
}

context.sampleMap=m;

context.orderItems = orderItems;



//getOrderItemsCategoryWise = OrderReadHelper.getOrderItemsCategoryWise(delegator,orderItems);

//print "##################getOrderItemsCategoryWise#########\n\n"+getOrderItemsCategoryWise+"\n\n" UtilMisc.toMap("keys",orderItems)

context.getOrderItemsCategoryWise = OrderReadHelper.getOrderItemsCategoryWise(delegator, orderItems, m);
context.orderItemLst=orderItems;

orderAdjustments = cart.makeAllAdjustments();

orderItemShipGroupInfo = cart.makeAllShipGroupInfos();
if (orderItemShipGroupInfo) {
    orderItemShipGroupInfo.each { valueObj ->
        if ("OrderAdjustment".equals(valueObj.getEntityName())) {
            // shipping / tax adjustment(s)
            orderAdjustments.add(valueObj);
        }
    }
}


showOld = "true".equals(parameters.SHOW_OLD);

partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, userLogin.partyId, showOld);
context.partyContactMechValueMaps =partyContactMechValueMaps;







context.orderAdjustments = orderAdjustments;

workEfforts = cart.makeWorkEfforts();   // if required make workefforts for rental fixed assets too.
context.workEfforts = workEfforts;

orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(orderAdjustments, null);
context.orderHeaderAdjustments = orderHeaderAdjustments;
context.orderItemShipGroups = cart.getShipGroups();
context.headerAdjustmentsToShow = OrderReadHelper.filterOrderAdjustments(orderHeaderAdjustments, true, false, false, false, false);

orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, orderAdjustments, workEfforts);
context.orderSubTotal = orderSubTotal;
context.placingCustomerPerson = userLogin?.getRelatedOne("Person");
context.paymentMethods = cart.getPaymentMethods();

paymentMethodTypeIds = cart.getPaymentMethodTypeIds();
context.paymentMethodTypess = paymentMethodTypeIds;
paymentMethodType = null;
paymentMethodTypeId = null;
if (paymentMethodTypeIds) {
    paymentMethodTypeId = paymentMethodTypeIds[0];
    paymentMethodType = delegator.findByPrimaryKey("PaymentMethodType", [paymentMethodTypeId : paymentMethodTypeId]);
    context.paymentMethodType = paymentMethodType;
}

webSiteId = CatalogWorker.getWebSiteId(request);

productStore = ProductStoreWorker.getProductStore(request);
context.productStore = productStore;

isDemoStore = !"N".equals(productStore.isDemoStore);
context.isDemoStore = isDemoStore;

payToPartyId = productStore.payToPartyId;
paymentAddress = PaymentWorker.getPaymentAddress(delegator, payToPartyId);
if (paymentAddress) context.paymentAddress = paymentAddress;


// TODO: FIXME!
/*
billingAccount = cart.getBillingAccountId() ? delegator.findByPrimaryKey("BillingAccount", [billingAccountId : cart.getBillingAccountId()]) : null;
if (billingAccount)
    context.billingAccount = billingAccount;
*/

context.customerPoNumber = cart.getPoNumber();
context.carrierPartyId = cart.getCarrierPartyId();
context.shipmentMethodTypeId = cart.getShipmentMethodTypeId();
context.shippingInstructions = cart.getShippingInstructions();
context.maySplit = cart.getMaySplit();
context.giftMessage = cart.getGiftMessage();
context.isGift = cart.getIsGift();
context.currencyUomId = cart.getCurrency();

shipmentMethodType = delegator.findByPrimaryKey("ShipmentMethodType", [shipmentMethodTypeId : cart.getShipmentMethodTypeId()]);
if (shipmentMethodType) context.shipMethDescription = shipmentMethodType.description;

orh = new OrderReadHelper(orderAdjustments, orderItems);
context.localOrderReadHelper = orh;
context.orderShippingTotal = cart.getTotalShipping();
context.orderTaxTotal = cart.getTotalSalesTax();
context.orderGrandTotal = cart.getGrandTotal();
context.billingAmt=cart.getBillingAccountAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
context.total=cart.getGrandTotal()-cart.getBillingAccountAmount();
//for invite friend
paymentAmount=cart.getInviteFriendAccount();
if(UtilValidate.isNotEmpty(paymentAmount) && !(paymentAmount.compareTo( BigDecimal.ZERO) == 0))
{
	
	paymentAmount=paymentAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
context.inviteRef=paymentAmount;
context.totalAmount=context.total-paymentAmount;
}
// nuke the event messages
request.removeAttribute("_EVENT_MESSAGE_");
