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
package org.ofbiz.order.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import javolution.util.FastList;
//import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.DeliveryBoyVechileManagement;
import org.ofbiz.order.OrderManagerEvents;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.product.ProductPromoWorker;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.party.party.PartyServices;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.ibm.icu.util.Calendar;
import java.util.Enumeration;
/**
 * Order Processing Services
 */

public class OrderServices {

    public static final String module = OrderServices.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String resourceProduct = "ProductUiLabels";

    /*public static Map<String, String> salesAttributeRoleMap = FastMap.newInstance();
    public static Map<String, String> purchaseAttributeRoleMap = FastMap.newInstance();*/
    public static Map<String, String> salesAttributeRoleMap = new HashMap<String, String>();
    public static Map<String, String> purchaseAttributeRoleMap = new HashMap<String, String>();
    
    static {
        salesAttributeRoleMap.put("placingCustomerPartyId", "PLACING_CUSTOMER");
        salesAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        salesAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        salesAttributeRoleMap.put("shipToCustomerPartyId", "SHIP_TO_CUSTOMER");
        salesAttributeRoleMap.put("endUserCustomerPartyId", "END_USER_CUSTOMER");

        purchaseAttributeRoleMap.put("billToCustomerPartyId", "BILL_TO_CUSTOMER");
        purchaseAttributeRoleMap.put("billFromVendorPartyId", "BILL_FROM_VENDOR");
        purchaseAttributeRoleMap.put("shipFromVendorPartyId", "SHIP_FROM_VENDOR");
        purchaseAttributeRoleMap.put("supplierAgentPartyId", "SUPPLIER_AGENT");
    }
    public static final int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    public static final int orderDecimals = UtilNumber.getBigDecimalScale("order.decimals");
    public static final int orderRounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");
    public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(taxDecimals, taxRounding);


    private static boolean hasPermission(String orderId, GenericValue userLogin, String action, Security security, Delegator delegator) {
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String orderTypeId = orh.getOrderTypeId();
        String partyId = null;
        GenericValue orderParty = orh.getEndUserParty();
        if (UtilValidate.isEmpty(orderParty)) {
            orderParty = orh.getPlacingParty();
        }
        if (UtilValidate.isNotEmpty(orderParty)) {
            partyId = orderParty.getString("partyId");
        }
        boolean hasPermission = hasPermission(orderTypeId, partyId, userLogin, action, security);
        if (!hasPermission) {
            GenericValue placingCustomer = null;
            try {
                Map<String, Object> placingCustomerFields = UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                Debug.logError("Could not select OrderRoles for order " + orderId + " due to " + e.getMessage(), module);
            }
            hasPermission = (placingCustomer != null);
        }
        return hasPermission;
    }

    private static boolean hasPermission(String orderTypeId, String partyId, GenericValue userLogin, String action, Security security) {
        boolean hasPermission = security.hasEntityPermission("ORDERMGR", "_" + action, userLogin);
        if (!hasPermission) {
            if (orderTypeId.equals("SALES_ORDER")) {
                if (security.hasEntityPermission("ORDERMGR", "_SALES_" + action, userLogin)) {
                    hasPermission = true;
                } else {
                    // check sales agent/customer relationship
                    List<GenericValue> repsCustomers = new LinkedList<GenericValue>();
                    try {
                        repsCustomers = EntityUtil.filterByDate(userLogin.getRelatedOne("Party").getRelatedByAnd("FromPartyRelationship",
                                UtilMisc.toMap("roleTypeIdFrom", "AGENT", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId)));
                    } catch (GenericEntityException ex) {
                        Debug.logError("Could not determine if " + partyId + " is a customer of user " + userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
                    }
                    if ((repsCustomers != null) && (repsCustomers.size() > 0) && (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
                        hasPermission = true;
                    }
                    if (!hasPermission) {
                        // check sales sales rep/customer relationship
                        try {
                            repsCustomers = EntityUtil.filterByDate(userLogin.getRelatedOne("Party").getRelatedByAnd("FromPartyRelationship",
                                    UtilMisc.toMap("roleTypeIdFrom", "SALES_REP", "roleTypeIdTo", "CUSTOMER", "partyIdTo", partyId)));
                        } catch (GenericEntityException ex) {
                            Debug.logError("Could not determine if " + partyId + " is a customer of user " + userLogin.getString("userLoginId") + " due to " + ex.getMessage(), module);
                        }
                        if ((repsCustomers != null) && (repsCustomers.size() > 0) && (security.hasEntityPermission("ORDERMGR", "_ROLE_" + action, userLogin))) {
                            hasPermission = true;
                        }
                    }
                }
            } else if ((orderTypeId.equals("PURCHASE_ORDER") && (security.hasEntityPermission("ORDERMGR", "_PURCHASE_" + action, userLogin)))) {
                hasPermission = true;
            }
        }
        return hasPermission;
    }
    /** Service for creating a new order 
     * @throws GenericEntityException */
    public static Map<String, Object> createOrder(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        List<GenericValue> toBeStored = new LinkedList<GenericValue>();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Map inventoryDetail = new HashMap();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        String partyId = (String) context.get("partyId");
        String billFromVendorPartyId = (String) context.get("billFromVendorPartyId");

        // check security permissions for order:
        //  SALES ORDERS - if userLogin has ORDERMGR_SALES_CREATE or ORDERMGR_CREATE permission, or if it is same party as the partyId, or
        //                 if it is an AGENT (sales rep) creating an order for his customer
        //  PURCHASE ORDERS - if there is a PURCHASE_ORDER permission
        Map<String, Object> resultSecurity = new HashMap<String, Object>();
        boolean hasPermission = OrderServices.hasPermission(orderTypeId, partyId, userLogin, "CREATE", security);
        // final check - will pass if userLogin's partyId = partyId for order or if userLogin has ORDERMGR_CREATE permission
        // jacopoc: what is the meaning of this code block? FIXME
        if (!hasPermission) {
            partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, resultSecurity, "ORDERMGR", "_CREATE");
            if (resultSecurity.size() > 0) {
                return resultSecurity;
            }
        }

        // get the product store for the order, but it is required only for sales orders
        String productStoreId = (String) context.get("productStoreId");
        GenericValue productStore = null;
        if ((orderTypeId.equals("SALES_ORDER")) && (UtilValidate.isNotEmpty(productStoreId))) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotFindProductStoreWithID",UtilMisc.toMap("productStoreId",productStoreId),locale)  + e.toString());
            }
        }

        // figure out if the order is immediately fulfilled based on product store settings
        boolean isImmediatelyFulfilled = false;
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        successResult.put("orderTypeId", orderTypeId);

        // lookup the order type entity
        GenericValue orderType = null;
        try {
            orderType = delegator.findByPrimaryKeyCache("OrderType", UtilMisc.toMap("orderTypeId", orderTypeId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorOrderTypeLookupFailed",locale) + e.toString());
        }

        // make sure we have a valid order type
        if (orderType == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorInvalidOrderTypeWithID", UtilMisc.toMap("orderTypeId",orderTypeId), locale));
        }

        // check to make sure we have something to order
        List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));
        if (orderItems.size() < 1) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "items.none", locale));
        }

        // all this marketing pkg auto stuff is deprecated in favor of MARKETING_PKG_AUTO productTypeId and a BOM of MANUF_COMPONENT assocs
        // these need to be retrieved now because they might be needed for exploding MARKETING_PKG_AUTO
        List<GenericValue> orderAdjustments = UtilGenerics.checkList(context.get("orderAdjustments"));
        List<GenericValue> orderItemShipGroupInfo = UtilGenerics.checkList(context.get("orderItemShipGroupInfo"));
        List<GenericValue> orderItemPriceInfo = UtilGenerics.checkList(context.get("orderItemPriceInfos"));

        // check inventory and other things for each item
        List<String> errorMessages = FastList.newInstance();
        /*Map<String, BigDecimal> normalizedItemQuantities = FastMap.newInstance();
        Map<String, String> normalizedItemNames = FastMap.newInstance();
        Map<String, GenericValue> itemValuesBySeqId = FastMap.newInstance();*/
        
        Map<String, BigDecimal> normalizedItemQuantities = new HashMap<String, BigDecimal>();
        Map<String, String> normalizedItemNames = new HashMap<String, String>();
        Map<String, GenericValue> itemValuesBySeqId = new HashMap<String, GenericValue>();
        
        Iterator<GenericValue> itemIter = orderItems.iterator();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        //
        // need to run through the items combining any cases where multiple lines refer to the
        // same product so the inventory check will work correctly
        // also count quantities ordered while going through the loop
        while (itemIter.hasNext()) {
            GenericValue orderItem = itemIter.next();

            // start by putting it in the itemValuesById Map
            itemValuesBySeqId.put(orderItem.getString("orderItemSeqId"), orderItem);

            String currentProductId = orderItem.getString("productId");
//            System.out.println("\n\n\n\n currentProductId"+currentProductId);
            if (currentProductId != null) {
                // only normalize items with a product associated (ignore non-product items)
                if (normalizedItemQuantities.get(currentProductId) == null) {
                    normalizedItemQuantities.put(currentProductId, orderItem.getBigDecimal("quantity"));
                    normalizedItemNames.put(currentProductId, orderItem.getString("itemDescription"));
                } else {
                    BigDecimal currentQuantity = normalizedItemQuantities.get(currentProductId);
                    normalizedItemQuantities.put(currentProductId, currentQuantity.add(orderItem.getBigDecimal("quantity")));
                }

                try {
                    // count product ordered quantities
                    // run this synchronously so it will run in the same transaction
                    dispatcher.runSync("countProductQuantityOrdered", UtilMisc.<String, Object>toMap("productId", currentProductId, "quantity", orderItem.getBigDecimal("quantity"), "userLogin", userLogin));
                } catch (GenericServiceException e1) {
                    Debug.logError(e1, "Error calling countProductQuantityOrdered service", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCallingCountProductQuantityOrderedService",locale) + e1.toString());
                }
            }
        }

        if (!"PURCHASE_ORDER".equals(orderTypeId) && productStoreId == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorTheProductStoreIdCanOnlyBeNullForPurchaseOrders",locale));
        }

        Timestamp orderDate = (Timestamp) context.get("orderDate");
        
        Iterator<String> normalizedIter = normalizedItemQuantities.keySet().iterator();
        while (normalizedIter.hasNext()) {
            // lookup the product entity for each normalized item; error on products not found
            String currentProductId = normalizedIter.next();
            BigDecimal currentQuantity = normalizedItemQuantities.get(currentProductId);
            String itemName = normalizedItemNames.get(currentProductId);
            GenericValue product = null;

            try {
                product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", currentProductId));
            } catch (GenericEntityException e) {
                String errMsg = UtilProperties.getMessage(resource_error, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(e, errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if (product == null) {
                String errMsg = UtilProperties.getMessage(resource_error, "product.not_found", new Object[] { currentProductId }, locale);
                Debug.logError(errMsg, module);
                errorMessages.add(errMsg);
                continue;
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if introductionDate hasn't passed yet
                if (product.get("introductionDate") != null && nowTimestamp.before(product.getTimestamp("introductionDate"))) {
                    String excMsg = UtilProperties.getMessage(resource_error, "product.not_yet_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

            if ("SALES_ORDER".equals(orderTypeId)) {
                boolean salesDiscontinuationFlag = false;
                // When past orders are imported, they should be imported even if sales discontinuation date is in the past but if the order date was before it
                if (orderDate != null && product.get("salesDiscontinuationDate") != null) {
                    salesDiscontinuationFlag = orderDate.after(product.getTimestamp("salesDiscontinuationDate")) && nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"));
                } else if (product.get("salesDiscontinuationDate") != null) {
                    salesDiscontinuationFlag = nowTimestamp.after(product.getTimestamp("salesDiscontinuationDate"));    
                }
                // check to see if salesDiscontinuationDate has passed
                if (salesDiscontinuationFlag) {
                    String excMsg = UtilProperties.getMessage(resource_error, "product.no_longer_for_sale",
                            new Object[] { getProductName(product, itemName), product.getString("productId") }, locale);
                    Debug.logWarning(excMsg, module);
                    errorMessages.add(excMsg);
                    continue;
                }
            }

                
            if ("SALES_ORDER".equals(orderTypeId)) {
                // check to see if we have inventory available
            	
            	BigDecimal inventoryAtp = null;
            	if(UtilValidate.isNotEmpty(product.getString("productId")) && !product.getString("productId").startsWith("GIFTCARD"))
            	{
//            		inventoryAtp = ShoppingCartEvents.totalATP(delegator, dispatcher , product.getString("productId") , productStore);
            		inventoryAtp  =(BigDecimal) product.get("inventoryAtp");
	                if(UtilValidate.isEmpty(inventoryAtp)) inventoryAtp = BigDecimal.ZERO;
	                if (inventoryAtp.doubleValue() < 1) {
	                    String invErrMsg = UtilProperties.getMessage(resource_error, "product.out_of_stock",
	                            new Object[] { getProductName(product, itemName), currentProductId }, locale);
	                    Debug.logWarning(invErrMsg, module);
	                    errorMessages.add(invErrMsg);
	                    continue;
	                }
	                inventoryDetail.put(product.getString("productId"),product);
            	} 
            	
            }
        }

        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // the inital status for ALL order types
        String initialStatus = "ORDER_CREATED";
        successResult.put("statusId", initialStatus);
        // create the order object
        String orderId = (String) context.get("orderId");
        String orgPartyId = null;
        if (productStore != null) {
            orgPartyId = productStore.getString("payToPartyId");
        } else if (billFromVendorPartyId != null) {
            orgPartyId = billFromVendorPartyId;
        }

        if (UtilValidate.isNotEmpty(orgPartyId)) {
           // Map<String, Object> getNextOrderIdContext = FastMap.newInstance();
            Map<String, Object> getNextOrderIdContext = new HashMap<String, Object>();
            getNextOrderIdContext.putAll(context);
            getNextOrderIdContext.put("partyId", orgPartyId);
            getNextOrderIdContext.put("userLogin", userLogin);

            if ((orderTypeId.equals("SALES_ORDER")) || (productStoreId != null)) {
                getNextOrderIdContext.put("productStoreId", productStoreId);
            }
            try {
	            GenericValue order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId",orderId));
	            if(UtilValidate.isNotEmpty(order) || UtilValidate.isEmpty(orderId)){
	                try {
	                    getNextOrderIdContext = ctx.makeValidContext("getNextOrderId", "IN", getNextOrderIdContext);
	                    Map<String, Object> getNextOrderIdResult = dispatcher.runSync("getNextOrderId", getNextOrderIdContext);
	                    if (ServiceUtil.isError(getNextOrderIdResult)) {
	                        String errMsg = UtilProperties.getMessage(resource_error, 
	                                "OrderErrorGettingNextOrderIdWhileCreatingOrder", locale);
	                        return ServiceUtil.returnError(errMsg, null, null, getNextOrderIdResult);
	                    }
	                    orderId = (String) getNextOrderIdResult.get("orderId");
	                } catch (GenericServiceException e) {
	                    String errMsg = UtilProperties.getMessage(resource_error, 
	                            "OrderCaughtGenericServiceExceptionWhileGettingOrderId", locale);
	                    Debug.logError(e, errMsg, module);
	                    return ServiceUtil.returnError(errMsg);
	                }
	            }
            } catch (Exception e) {
            }
        }

        if (UtilValidate.isEmpty(orderId)) {
            // for purchase orders or when other orderId generation fails, a product store id should not be required to make an order
            orderId = delegator.getNextSeqId("OrderHeader");
        }

        String billingAccountId = (String) context.get("billingAccountId");
        if (orderDate == null) {
            orderDate = nowTimestamp;
        }

        Map<String, Object> orderHeaderMap = UtilMisc.<String, Object>toMap("orderId", orderId, "orderTypeId", orderTypeId,
                "orderDate", orderDate, "entryDate", nowTimestamp,
                "statusId", initialStatus, "billingAccountId", billingAccountId);
        orderHeaderMap.put("orderName", context.get("orderName"));
        orderHeaderMap.put("slot", context.get("slot"));
        orderHeaderMap.put("deliveryDate", context.get("deliveryDate"));
        
        /*Random r = new Random();
        String pinId=String.valueOf(r.nextInt(8999)+1000);
        int count=0;
        pinId=getPinId(delegator,pinId,count);
       
       
       orderHeaderMap.put("pinId",pinId);*/
        if (isImmediatelyFulfilled) {
            // also flag this order as needing inventory issuance so that when it is set to complete it will be issued immediately (needsInventoryIssuance = Y)
            orderHeaderMap.put("needsInventoryIssuance", "Y");
        }
        GenericValue orderHeader = delegator.makeValue("OrderHeader", orderHeaderMap);

        // determine the sales channel
        String salesChannelEnumId = (String) context.get("salesChannelEnumId");
        if ((salesChannelEnumId == null) || salesChannelEnumId.equals("UNKNWN_SALES_CHANNEL")) {
            // try the default store sales channel
            if (orderTypeId.equals("SALES_ORDER") && (productStore != null)) {
                salesChannelEnumId = productStore.getString("defaultSalesChannelEnumId");
            }
            // if there's still no channel, set to unknown channel
            if (salesChannelEnumId == null) {
                salesChannelEnumId = "UNKNWN_SALES_CHANNEL";
            }
        }
        orderHeader.set("salesChannelEnumId", salesChannelEnumId);

        if (context.get("currencyUom") != null) {
            orderHeader.set("currencyUom", context.get("currencyUom"));
        }

        if (context.get("firstAttemptOrderId") != null) {
            orderHeader.set("firstAttemptOrderId", context.get("firstAttemptOrderId"));
        }

        if (context.get("grandTotal") != null) {
            orderHeader.set("grandTotal", context.get("grandTotal"));
        }

        if (UtilValidate.isNotEmpty(context.get("visitId"))) {
            orderHeader.set("visitId", context.get("visitId"));
        }

        if (UtilValidate.isNotEmpty(context.get("internalCode"))) {
            orderHeader.set("internalCode", context.get("internalCode"));
        }

        if (UtilValidate.isNotEmpty(context.get("externalId"))) {
            orderHeader.set("externalId", context.get("externalId"));
        }

        if (UtilValidate.isNotEmpty(context.get("originFacilityId"))) {
            orderHeader.set("originFacilityId", context.get("originFacilityId"));
        }

        if (UtilValidate.isNotEmpty(context.get("productStoreId"))) {
            orderHeader.set("productStoreId", context.get("productStoreId"));
        }

        if (UtilValidate.isNotEmpty(context.get("transactionId"))) {
            orderHeader.set("transactionId", context.get("transactionId"));
        }

        if (UtilValidate.isNotEmpty(context.get("terminalId"))) {
            orderHeader.set("terminalId", context.get("terminalId"));
        }

        if (UtilValidate.isNotEmpty(context.get("autoOrderShoppingListId"))) {
            orderHeader.set("autoOrderShoppingListId", context.get("autoOrderShoppingListId"));
        }

        if (UtilValidate.isNotEmpty(context.get("webSiteId"))) {
            orderHeader.set("webSiteId", context.get("webSiteId"));
        }

        if (userLogin != null && userLogin.get("userLoginId") != null) {
            orderHeader.set("createdBy", userLogin.getString("userLoginId"));
        }

        // first try to create the OrderHeader; if this does not fail, continue.
        try {
            delegator.create(orderHeader);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create OrderHeader entity; problems with insert", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderOrderCreationFailedPleaseNotifyCustomerService",locale));
        }

        // create the order status record
        String orderStatusSeqId = delegator.getNextSeqId("OrderStatus");
        GenericValue orderStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", orderStatusSeqId));
        orderStatus.set("orderId", orderId);
        orderStatus.set("statusId", orderHeader.getString("statusId"));
        orderStatus.set("statusDatetime", nowTimestamp);
        orderStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
        toBeStored.add(orderStatus);

        // before processing orderItems process orderItemGroups so that they'll be in place for the foreign keys and what not
        List<GenericValue> orderItemGroups = UtilGenerics.checkList(context.get("orderItemGroups"));
        if (UtilValidate.isNotEmpty(orderItemGroups)) {
            Iterator<GenericValue> orderItemGroupIter = orderItemGroups.iterator();
            while (orderItemGroupIter.hasNext()) {
                GenericValue orderItemGroup = orderItemGroupIter.next();
                orderItemGroup.set("orderId", orderId);
                toBeStored.add(orderItemGroup);
            }
        }

        // set the order items
        Iterator<GenericValue> oi = orderItems.iterator();
        while (oi.hasNext()) {
            GenericValue orderItem = oi.next();
            orderItem.set("orderId", orderId);
            toBeStored.add(orderItem);
            
//            System.out.println("\n\n\n\n\n orderItem"+orderItem);

            // create the item status record
            String itemStatusId = delegator.getNextSeqId("OrderStatus");
            GenericValue itemStatus = delegator.makeValue("OrderStatus", UtilMisc.toMap("orderStatusId", itemStatusId));
            itemStatus.put("statusId", orderItem.get("statusId"));
            itemStatus.put("orderId", orderId);
            itemStatus.put("orderItemSeqId", orderItem.get("orderItemSeqId"));
            itemStatus.put("statusDatetime", nowTimestamp);
            itemStatus.set("statusUserLogin", userLogin.getString("userLoginId"));
            toBeStored.add(itemStatus);
        }

        // set the order attributes
        List<GenericValue> orderAttributes = UtilGenerics.checkList(context.get("orderAttributes"));
        if (UtilValidate.isNotEmpty(orderAttributes)) {
            Iterator<GenericValue> oattr = orderAttributes.iterator();
            while (oattr.hasNext()) {
                GenericValue oatt = oattr.next();
                oatt.set("orderId", orderId);
                toBeStored.add(oatt);
            }
        }

        // set the order item attributes
        List<GenericValue> orderItemAttributes = UtilGenerics.checkList(context.get("orderItemAttributes"));
        if (UtilValidate.isNotEmpty(orderItemAttributes)) {
            Iterator<GenericValue> oiattr = orderItemAttributes.iterator();
            while (oiattr.hasNext()) {
                GenericValue oiatt = oiattr.next();
                oiatt.set("orderId", orderId);
                toBeStored.add(oiatt);
            }
        }

        // create the order internal notes
        List<String> orderInternalNotes = UtilGenerics.checkList(context.get("orderInternalNotes"));
        if (UtilValidate.isNotEmpty(orderInternalNotes)) {
            Iterator<String> orderInternalNotesIt = orderInternalNotes.iterator();
            while (orderInternalNotesIt.hasNext()) {
                String orderInternalNote = orderInternalNotesIt.next();
                try {
                    Map<String, Object> noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                             "internalNote", "Y",
                                                                                             "note", orderInternalNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", ""), locale),
                                null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error creating internal notes while creating order: " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", e.toString()), locale));
                }
            }
        }

        // create the order public notes
        List<String> orderNotes = UtilGenerics.checkList(context.get("orderNotes"));
        if (UtilValidate.isNotEmpty(orderNotes)) {
            Iterator<String> orderNotesIt = orderNotes.iterator();
            while (orderNotesIt.hasNext()) {
                String orderNote = orderNotesIt.next();
                try {
                    Map<String, Object> noteOutputMap = dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                             "internalNote", "N",
                                                                                             "note", orderNote,
                                                                                             "userLogin", userLogin));
                    if (ServiceUtil.isError(noteOutputMap)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", ""), locale),
                            null, null, noteOutputMap);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error creating notes while creating order: " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", e.toString()), locale));
                }
            }
        }

        if (errorMessages.size() > 0) {
            return ServiceUtil.returnError(errorMessages);
        }

        // set the orderId on all adjustments; this list will include order and
        // item adjustments...
        if (UtilValidate.isNotEmpty(orderAdjustments)) {
            Iterator<GenericValue>iter = orderAdjustments.iterator();

            while (iter.hasNext()) {
                GenericValue orderAdjustment = iter.next();
                try {
                    orderAdjustment.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCouldNotGetNextSequenceIdForOrderAdjustmentCannotCreateOrder",locale));
                }

                orderAdjustment.set("orderId", orderId);
                orderAdjustment.set("createdDate", UtilDateTime.nowTimestamp());
                orderAdjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));

                if (UtilValidate.isEmpty(orderAdjustment.get("orderItemSeqId"))) {
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                if (UtilValidate.isEmpty(orderAdjustment.get("shipGroupSeqId"))) {
                    orderAdjustment.set("shipGroupSeqId", DataModelConstants.SEQ_ID_NA);
                }
                toBeStored.add(orderAdjustment);
            }
        }

        // set the order contact mechs
        List<GenericValue> orderContactMechs = UtilGenerics.checkList(context.get("orderContactMechs"));
        if (UtilValidate.isNotEmpty(orderContactMechs)) {
            Iterator<GenericValue> ocmi = orderContactMechs.iterator();

            while (ocmi.hasNext()) {
                GenericValue ocm = ocmi.next();
                ocm.set("orderId", orderId);
                toBeStored.add(ocm);
            }
        }

        // set the order item contact mechs
        List<GenericValue> orderItemContactMechs = UtilGenerics.checkList(context.get("orderItemContactMechs"));
        if (UtilValidate.isNotEmpty(orderItemContactMechs)) {
            Iterator<GenericValue> oicmi = orderItemContactMechs.iterator();

            while (oicmi.hasNext()) {
                GenericValue oicm = oicmi.next();
                oicm.set("orderId", orderId);
                toBeStored.add(oicm);
            }
        }

        // set the order item ship groups
        List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
        if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
            Iterator<GenericValue> osiInfos = orderItemShipGroupInfo.iterator();
            while (osiInfos.hasNext()) {
                GenericValue valueObj = osiInfos.next();
                valueObj.set("orderId", orderId);
                if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                    // ship group
                    if (valueObj.get("carrierRoleTypeId") == null) {
                        valueObj.set("carrierRoleTypeId", "CARRIER");
                    }
                    if (!UtilValidate.isEmpty(valueObj.getString("supplierPartyId"))) {
                        dropShipGroupIds.add(valueObj.getString("shipGroupSeqId"));
                    }
                } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                    // shipping / tax adjustment(s)
                    if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                        valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                    }
                    valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                toBeStored.add(valueObj);
            }
        }

        // set the additional party roles
        Map<String, List<String>> additionalPartyRole = UtilGenerics.checkMap(context.get("orderAdditionalPartyRoleMap"));
        if (additionalPartyRole != null) {
            for (Map.Entry<String, List<String>> entry : additionalPartyRole.entrySet()) {
                String additionalRoleTypeId = entry.getKey();
                List<String> parties = entry.getValue();
                if (parties != null) {
                    Iterator<String> apIt = parties.iterator();
                    while (apIt.hasNext()) {
                        String additionalPartyId = apIt.next();
                        toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));
                        toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", additionalPartyId, "roleTypeId", additionalRoleTypeId)));
                    }
                }
            }
        }

        // set the item survey responses
        List<GenericValue> surveyResponses = UtilGenerics.checkList(context.get("orderItemSurveyResponses"));
        if (UtilValidate.isNotEmpty(surveyResponses)) {
            Iterator<GenericValue> oisr = surveyResponses.iterator();
            while (oisr.hasNext()) {
                GenericValue surveyResponse = oisr.next();
                surveyResponse.set("orderId", orderId);
                toBeStored.add(surveyResponse);
            }
        }

        // set the item price info; NOTE: this must be after the orderItems are stored for referential integrity
        if (UtilValidate.isNotEmpty(orderItemPriceInfo)) {
            Iterator<GenericValue> oipii = orderItemPriceInfo.iterator();

            while (oipii.hasNext()) {
                GenericValue oipi = oipii.next();
                try {
                    oipi.set("orderItemPriceInfoId", delegator.getNextSeqId("OrderItemPriceInfo"));
                } catch (IllegalArgumentException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCouldNotGetNextSequenceIdForOrderItemPriceInfoCannotCreateOrder",locale));
                }

                oipi.set("orderId", orderId);
                toBeStored.add(oipi);
            }
        }

        // set the item associations
        List<GenericValue> orderItemAssociations = UtilGenerics.checkList(context.get("orderItemAssociations"));
        if (UtilValidate.isNotEmpty(orderItemAssociations)) {
            Iterator<GenericValue> oia = orderItemAssociations.iterator();
            while (oia.hasNext()) {
                GenericValue orderItemAssociation = oia.next();
                if (orderItemAssociation.get("toOrderId") == null) {
                    orderItemAssociation.set("toOrderId", orderId);
                } else if (orderItemAssociation.get("orderId") == null) {
                    orderItemAssociation.set("orderId", orderId);
                }
                toBeStored.add(orderItemAssociation);
            }
        }

        // store the orderProductPromoUseInfos
        List<GenericValue> orderProductPromoUses = UtilGenerics.checkList(context.get("orderProductPromoUses"));
        if (UtilValidate.isNotEmpty(orderProductPromoUses)) {
            Iterator<GenericValue> orderProductPromoUseIter = orderProductPromoUses.iterator();
            while (orderProductPromoUseIter.hasNext()) {
                GenericValue productPromoUse = orderProductPromoUseIter.next();
                productPromoUse.set("orderId", orderId);
                toBeStored.add(productPromoUse);
            }
        }

        // store the orderProductPromoCodes
        Set<String> orderProductPromoCodes = UtilGenerics.checkSet(context.get("orderProductPromoCodes"));
        if (UtilValidate.isNotEmpty(orderProductPromoCodes)) {
            GenericValue orderProductPromoCode = delegator.makeValue("OrderProductPromoCode");
            Iterator<String> orderProductPromoCodeIter = orderProductPromoCodes.iterator();
            while (orderProductPromoCodeIter.hasNext()) {
                orderProductPromoCode.clear();
                orderProductPromoCode.set("orderId", orderId);
                orderProductPromoCode.set("productPromoCodeId", orderProductPromoCodeIter.next());
                toBeStored.add(orderProductPromoCode);
            }
        }

        // see the attributeRoleMap definition near the top of this file for attribute-role mappings
        Map<String, String> attributeRoleMap = salesAttributeRoleMap;
        if ("PURCHASE_ORDER".equals(orderTypeId)) {
            attributeRoleMap = purchaseAttributeRoleMap;
        }
        for (Map.Entry<String, String> attributeRoleEntry : attributeRoleMap.entrySet()) {
            if (UtilValidate.isNotEmpty(context.get(attributeRoleEntry.getKey()))) {
                // make sure the party is in the role before adding
                toBeStored.add(delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
                toBeStored.add(delegator.makeValue("OrderRole", UtilMisc.toMap("orderId", orderId, "partyId", context.get(attributeRoleEntry.getKey()), "roleTypeId", attributeRoleEntry.getValue())));
            }
        }


        // set the affiliate -- This is going to be removed...
        String affiliateId = (String) context.get("affiliateId");
        if (UtilValidate.isNotEmpty(affiliateId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
                    UtilMisc.toMap("orderId", orderId, "partyId", affiliateId, "roleTypeId", "AFFILIATE")));
        }

        // set the distributor
        String distributorId = (String) context.get("distributorId");
        if (UtilValidate.isNotEmpty(distributorId)) {
            toBeStored.add(delegator.makeValue("OrderRole",
                    UtilMisc.toMap("orderId", orderId, "partyId", distributorId, "roleTypeId", "DISTRIBUTOR")));
        }

        // find all parties in role VENDOR associated with WebSite OR ProductStore (where WebSite overrides, if specified), associated first valid with the Order
        if (UtilValidate.isNotEmpty(context.get("productStoreId"))) {
            try {
                List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("roleTypeId", "VENDOR", "productStoreId", context.get("productStoreId")), UtilMisc.toList("-fromDate"));
                productStoreRoles = EntityUtil.filterByDate(productStoreRoles, true);
                GenericValue productStoreRole = EntityUtil.getFirst(productStoreRoles);
                if (productStoreRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", productStoreRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Product Store", module);
            }

        }
        if (UtilValidate.isNotEmpty(context.get("webSiteId"))) {
            try {
                List<GenericValue> webSiteRoles = delegator.findByAnd("WebSiteRole", UtilMisc.toMap("roleTypeId", "VENDOR", "webSiteId", context.get("webSiteId")), UtilMisc.toList("-fromDate"));
                webSiteRoles = EntityUtil.filterByDate(webSiteRoles, true);
                GenericValue webSiteRole = EntityUtil.getFirst(webSiteRoles);
                if (webSiteRole != null) {
                    toBeStored.add(delegator.makeValue("OrderRole",
                            UtilMisc.toMap("orderId", orderId, "partyId", webSiteRole.get("partyId"), "roleTypeId", "VENDOR")));
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error looking up Vendor for the current Web Site", module);
            }

        }

        // set the order payment info
        List<GenericValue> orderPaymentInfos = UtilGenerics.checkList(context.get("orderPaymentInfo"));
        if (UtilValidate.isNotEmpty(orderPaymentInfos)) {
            Iterator<GenericValue> oppIter = orderPaymentInfos.iterator();
            while (oppIter.hasNext()) {
                GenericValue valueObj = oppIter.next();
                valueObj.set("orderId", orderId);
                if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                    if (valueObj.get("orderPaymentPreferenceId") == null) {
                        valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                        valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                        valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    }
                    if (valueObj.get("statusId") == null) {
                        valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                    }
                }
                toBeStored.add(valueObj);
            }
        }

        // store the trackingCodeOrder entities
        List<GenericValue> trackingCodeOrders = UtilGenerics.checkList(context.get("trackingCodeOrders"));
        if (UtilValidate.isNotEmpty(trackingCodeOrders)) {
            Iterator<GenericValue> tkcdordIter = trackingCodeOrders.iterator();
            while (tkcdordIter.hasNext()) {
                GenericValue trackingCodeOrder = tkcdordIter.next();
                trackingCodeOrder.set("orderId", orderId);
                toBeStored.add(trackingCodeOrder);
            }
        }

       // store the OrderTerm entities

       List<GenericValue> orderTerms = UtilGenerics.checkList(context.get("orderTerms"));
       if (UtilValidate.isNotEmpty(orderTerms)) {
           Iterator<GenericValue> orderTermIter = orderTerms.iterator();
           while (orderTermIter.hasNext()) {
               GenericValue orderTerm = orderTermIter.next();
               orderTerm.set("orderId", orderId);
               if (orderTerm.get("orderItemSeqId") == null) {
                   orderTerm.set("orderItemSeqId", "_NA_");
               }
               toBeStored.add(orderTerm);
           }
       }

       // if a workEffortId is passed, then prepare a OrderHeaderWorkEffort value
       String workEffortId = (String) context.get("workEffortId");
       if (UtilValidate.isNotEmpty(workEffortId)) {
           GenericValue orderHeaderWorkEffort = delegator.makeValue("OrderHeaderWorkEffort");
           orderHeaderWorkEffort.set("orderId", orderId);
           orderHeaderWorkEffort.set("workEffortId", workEffortId);
           toBeStored.add(orderHeaderWorkEffort);
       }

        try {
           
            ////////////////
            Iterator<GenericValue> osiInfos = orderItemShipGroupInfo.iterator();
            while (osiInfos.hasNext()) {
                GenericValue orderItemShipGroupAssoc = osiInfos.next();
                if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
                				GenericValue orderItem = itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
  									GenericValue prod = (GenericValue) inventoryDetail.get(orderItem.getString("productId"));

		              				BigDecimal inventoryAtp = BigDecimal.ZERO;
		              				if(UtilValidate.isNotEmpty(prod) && UtilValidate.isNotEmpty(prod.get("inventoryAtp"))){
		              					inventoryAtp = (BigDecimal) prod.get("inventoryAtp");
		              					inventoryAtp = inventoryAtp.subtract(orderItemShipGroupAssoc.getBigDecimal("quantity"));
		              					prod.set("inventoryAtp",inventoryAtp);
		              					toBeStored.add(prod);
		              				}
                }			
		   }
                    
            // store line items, etc so that they will be there for the foreign key checks
            delegator.storeAll(toBeStored);         
            List<String> resErrorMessages = new LinkedList<String>();
            if(inventoryDetail.size()>0){
            try {
                Map<String, Object> sendInventoryParameters = new HashMap<String, Object>();
                  sendInventoryParameters.put("orderId", orderId);
                sendInventoryParameters.put("userLogin", userLogin);
                 dispatcher.runAsync("setInventoryInDetail", sendInventoryParameters);
                 
//                reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupInfo, dropShipGroupIds, itemValuesBySeqId,
//                        orderTypeId, productStoreId, resErrorMessages);
            } catch (GenericServiceException e) {
                return ServiceUtil.returnError(e.getMessage());
            }
      }
//            if (resErrorMessages.size() > 0) {
//                return ServiceUtil.returnError(resErrorMessages);
//            }
            
            successResult.put("orderId", orderId);
            successResult.put("createdBy",userLogin.getString("userLoginId"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem with order storage or reservations", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotCreateOrderWriteError",locale) + e.getMessage() + ").");
        }

        return successResult;
    }
    public static Map setInventoryInDetail(DispatchContext ctx, Map context) {
    	   Delegator delegator = ctx.getDelegator();
           LocalDispatcher dispatcher = ctx.getDispatcher();
           Locale locale = (Locale) context.get("locale");
           GenericValue userLogin = (GenericValue) context.get("userLogin");
           String orderId = (String) context.get("orderId");
//           List  orderItemShipGroupInfo = (List) context.get("orderItemShipGroupInfo");
//          List dropShipGroupIds = (List) context.get("dropShipGroupIds");
//           Map  itemValuesBySeqId = (Map) context.get("itemValuesBySeqId");
//           String   orderTypeId = (String) context.get("orderTypeId");
//           String  productStoreId = (String) context.get("productStoreId");
//           boolean isImmediatelyFulfilled = false;
           List resErrorMessages =  new ArrayList();
          List  tobeStored = new LinkedList();
           
//           GenericValue productStore = null;
//           if (UtilValidate.isNotEmpty(productStoreId)) {
//               try {
//                   productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
//               } catch (GenericEntityException e) {
//                   
//               }
//           }
//           if (productStore != null) {
//               isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
//           }
//
//           boolean reserveInventory = ("SALES_ORDER".equals(orderTypeId));
//           if (reserveInventory && isImmediatelyFulfilled) {
//               // don't reserve inventory if the product store has isImmediatelyFulfilled set, ie don't if in this store things are immediately fulfilled
//               reserveInventory = false;
//           }
           
           if(UtilValidate.isNotEmpty(orderId)){
        	   List<GenericValue> shipGroupAssoc = null;
	        	   try {
	        		   shipGroupAssoc =  delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					 resErrorMessages.add("Ship Group Assoc Not Found");
				}
	        	   if(UtilValidate.isNotEmpty(shipGroupAssoc)) {
	        		   for(GenericValue shipGroup : shipGroupAssoc){
	        			   GenericValue orderItem = null;
						try {
							orderItem = delegator.findByPrimaryKey("OrderItem",UtilMisc.toMap("orderId", orderId,"orderItemSeqId",shipGroup.getString("orderItemSeqId")));
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	        			   if (UtilValidate.isNotEmpty(orderItem)) {
	        				   List condition = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,orderItem.getString("productId")), EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"WebStoreWarehouse"));
			   		            List<GenericValue> inventoryItems = null;
								try {
									inventoryItems = delegator.findList("InventoryItem",EntityCondition.makeCondition(condition,EntityOperator.AND),UtilMisc.toSet("inventoryItemId","availableToPromiseTotal","comments"),UtilMisc.toList("datetimeReceived DESC"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true),false);
								} catch (GenericEntityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									 resErrorMessages.add("Order Item not found for this orderId");
								}
								 
			   		            GenericValue inventoryItem = EntityUtil.getFirst(inventoryItems);
	              					if(UtilValidate.isNotEmpty(inventoryItem)){
		              					BigDecimal availableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");	
		              					availableToPromiseTotal = availableToPromiseTotal.subtract(shipGroup.getBigDecimal("quantity"));
		              					inventoryItem.set("availableToPromiseTotal",availableToPromiseTotal);
		              					inventoryItem.set("comments","CSV_import");
		              					
		              					tobeStored.add(inventoryItem);
			              					
			              				GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
			              				inventoryItemDetail.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
			              				delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);
			              				inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
			              				inventoryItemDetail.put("quantityOnHandDiff", BigDecimal.ZERO);
			              				inventoryItemDetail.put("availableToPromiseDiff",shipGroup.getBigDecimal("quantity").negate());
			              				inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
			              				inventoryItemDetail.put("description","Order Approved,Reserve inventory");
			              				inventoryItemDetail.put("requiresEca","N"); 
			              				inventoryItemDetail.put("orderId",orderItem.getString("orderId")); 
			              				inventoryItemDetail.put("orderItemSeqId",orderItem.getString("orderItemSeqId"));
			              				
		  		              			GenericValue orderItemShipGrpInvRes = delegator.makeValue("OrderItemShipGrpInvRes");
			              				orderItemShipGrpInvRes.put("orderId", orderItem.getString("orderId"));
			              				orderItemShipGrpInvRes.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
			              				orderItemShipGrpInvRes.put("shipGroupSeqId",shipGroup.getString("shipGroupSeqId"));
			              				orderItemShipGrpInvRes.put("inventoryItemId",inventoryItem.getString("inventoryItemId"));
			              				orderItemShipGrpInvRes.put("reserveOrderEnumId","INVRO_FIFO_REC");
			              				orderItemShipGrpInvRes.put("quantity",shipGroup.getBigDecimal("quantity"));
			              				orderItemShipGrpInvRes.put("quantityNotAvailable",shipGroup.getBigDecimal("quantity"));
			              				orderItemShipGrpInvRes.put("promisedDatetime",UtilDateTime.nowTimestamp());
			              				orderItemShipGrpInvRes.put("createdDatetime",UtilDateTime.nowTimestamp());
			              				orderItemShipGrpInvRes.put("reservedDatetime",UtilDateTime.nowTimestamp());
	             				
			              				tobeStored.add(orderItemShipGrpInvRes);
			              				tobeStored.add(inventoryItemDetail);
			              				try {
											delegator.storeAll(tobeStored);
										} catch (GenericEntityException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											 String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
											 Debug.logError(e, errMsg, module);
				                             resErrorMessages.add(errMsg);
										}
		              				}
	        			   }
	        		   }
	        	   }
           }else{
        	   resErrorMessages.add("OrderId Not Found"); 
           }
//           if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
//               Iterator<GenericValue> osiInfos = orderItemShipGroupInfo.iterator();
//               while (osiInfos.hasNext()) {
//                   GenericValue orderItemShipGroupAssoc = osiInfos.next();
//                     if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
//                       if (dropShipGroupIds != null && dropShipGroupIds.contains(orderItemShipGroupAssoc.getString("shipGroupSeqId"))) {
//                           // the items in the drop ship groups are not reserved
//                           continue;
//                       }
//                       GenericValue orderItem = (GenericValue) itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
//                        String itemStatus = orderItem.getString("statusId");
//                       
//                       if ("ITEM_REJECTED".equals(itemStatus) || "ITEM_CANCELLED".equals(itemStatus) || "ITEM_COMPLETED".equals(itemStatus)) {
//                           Debug.logInfo("Order item [" + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + "] is not in a proper status for reservation", module);
//                           continue;
//                       }
//                       if (UtilValidate.isNotEmpty(orderItem.getString("productId")) && !"RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {  // ignore for rental
//                           try {
//                               // get the product of the order item
//                               GenericValue product = orderItem.getRelatedOne("Product");
//                               if (product == null) {
//                                   Debug.logError("Error when looking up product in reserveInventory service", module);
//                                   resErrorMessages.add("Error when looking up product in reserveInventory service");
//                                   continue;
//                               }
//                               if (reserveInventory) {
//                                   // for MARKETING_PKG_PICK reserve the components
//				                    List condition = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,orderItem.getString("productId")), EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"WebStoreWarehouse"));
//				   		            List<GenericValue> inventoryItems = delegator.findList("InventoryItem",EntityCondition.makeCondition(condition,EntityOperator.AND),UtilMisc.toSet("inventoryItemId","availableToPromiseTotal","comments"),UtilMisc.toList("datetimeReceived DESC"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true),false);
//				   		            GenericValue inventoryItem = EntityUtil.getFirst(inventoryItems);
//   		              				if(UtilValidate.isNotEmpty(inventoryItem)){
//   		              					BigDecimal availableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");	
//    		              					availableToPromiseTotal = availableToPromiseTotal.subtract(orderItemShipGroupAssoc.getBigDecimal("quantity"));
//    		              					inventoryItem.set("availableToPromiseTotal",availableToPromiseTotal);
//   		              					inventoryItem.set("comments","CSV_import");
//   		              					tobeStored.add(inventoryItem);
//   		              					
//   		              				GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
//   		              				inventoryItemDetail.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
//   		              				delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);
//   		              				inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
//   		              				inventoryItemDetail.put("quantityOnHandDiff", BigDecimal.ZERO);
//   		              				inventoryItemDetail.put("availableToPromiseDiff",orderItemShipGroupAssoc.getBigDecimal("quantity").negate());
//   		              				inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
//    		              				inventoryItemDetail.put("description","Order Approved,Reserve inventory");
//   		              				inventoryItemDetail.put("requiresEca","N"); 
//   		              				inventoryItemDetail.put("orderId",orderItem.getString("orderId")); 
//   		              				inventoryItemDetail.put("orderItemSeqId",orderItem.getString("orderItemSeqId"));
//   		              				
//	   		              			GenericValue orderItemShipGrpInvRes = delegator.makeValue("OrderItemShipGrpInvRes");
//		              				orderItemShipGrpInvRes.put("orderId", orderItem.getString("orderId"));
//		              				orderItemShipGrpInvRes.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
//		              				orderItemShipGrpInvRes.put("shipGroupSeqId",orderItemShipGroupAssoc.getString("shipGroupSeqId"));
//		              				orderItemShipGrpInvRes.put("inventoryItemId",inventoryItem.getString("inventoryItemId"));
//		              				orderItemShipGrpInvRes.put("reserveOrderEnumId","INVRO_FIFO_REC");
//		              				orderItemShipGrpInvRes.put("quantity",orderItemShipGroupAssoc.getBigDecimal("quantity"));
//		              				orderItemShipGrpInvRes.put("quantityNotAvailable",orderItemShipGroupAssoc.getBigDecimal("quantity"));
//		              				orderItemShipGrpInvRes.put("promisedDatetime",UtilDateTime.nowTimestamp());
//		              				orderItemShipGrpInvRes.put("createdDatetime",UtilDateTime.nowTimestamp());
//		              				orderItemShipGrpInvRes.put("reservedDatetime",UtilDateTime.nowTimestamp());
//
//
//
//	              				
//		              				tobeStored.add(orderItemShipGrpInvRes);
//   		              				tobeStored.add(inventoryItemDetail);
//   		              				}
//   		              				delegator.storeAll(tobeStored);
//                                   }
//                               } //try closes                      
//                           		catch (Exception e) {
//                               String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
//                               Debug.logError(e, errMsg, module);
//                               resErrorMessages.add(errMsg);
//                           }
//                       }//condition for checking rental item 
//                   }//checking for orderItem ship group assoc
//               }
//           }
           
            if (resErrorMessages.size() > 0) {
               return ServiceUtil.returnError(resErrorMessages);
           }else{
        	   return ServiceUtil.returnSuccess();
           }
           
 }
     	
    	
 
    
    public static void reserveInventory(Delegator delegator,
    		LocalDispatcher dispatcher,
    		GenericValue userLogin, Locale locale, List<GenericValue> orderItemShipGroupInfo, List<String> dropShipGroupIds, Map<String, GenericValue> itemValuesBySeqId, String orderTypeId, String productStoreId, List<String> resErrorMessages,Map inventoryDetail ) throws GeneralException {
        boolean isImmediatelyFulfilled = false;
        List  tobeStored = new LinkedList();
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            } catch (GenericEntityException e) {
                throw new GeneralException(UtilProperties.getMessage(resource_error, 
                        "OrderErrorCouldNotFindProductStoreWithID", 
                        UtilMisc.toMap("productStoreId", productStoreId), locale) + e.toString());
            }
        }
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        boolean reserveInventory = ("SALES_ORDER".equals(orderTypeId));
        if (reserveInventory && isImmediatelyFulfilled) {
            // don't reserve inventory if the product store has isImmediatelyFulfilled set, ie don't if in this store things are immediately fulfilled
            reserveInventory = false;
        }
  
        // START inventory reservation
        // decrement inventory available for each OrderItemShipGroupAssoc, within the same transaction
        if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
            Iterator<GenericValue> osiInfos = orderItemShipGroupInfo.iterator();
            while (osiInfos.hasNext()) {
                GenericValue orderItemShipGroupAssoc = osiInfos.next();
                  if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
                    if (dropShipGroupIds != null && dropShipGroupIds.contains(orderItemShipGroupAssoc.getString("shipGroupSeqId"))) {
                        // the items in the drop ship groups are not reserved
                        continue;
                    }
                    GenericValue orderItem = itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
                    GenericValue orderItemShipGroup = orderItemShipGroupAssoc.getRelatedOne("OrderItemShipGroup");
                    String shipGroupFacilityId = orderItemShipGroup.getString("facilityId");
                    String itemStatus = orderItem.getString("statusId");
                    
                    if ("ITEM_REJECTED".equals(itemStatus) || "ITEM_CANCELLED".equals(itemStatus) || "ITEM_COMPLETED".equals(itemStatus)) {
                        Debug.logInfo("Order item [" + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + "] is not in a proper status for reservation", module);
                        continue;
                    }
                    if (UtilValidate.isNotEmpty(orderItem.getString("productId")) &&   // only reserve product items, ignore non-product items
                            !"RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {  // ignore for rental
                        try {
                            // get the product of the order item
                            GenericValue product = orderItem.getRelatedOne("Product");
                            if (product == null) {
                                Debug.logError("Error when looking up product in reserveInventory service", module);
                                resErrorMessages.add("Error when looking up product in reserveInventory service");
                                continue;
                            }
                            if (reserveInventory) {
                                // for MARKETING_PKG_PICK reserve the components
                                if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_PICK")) { } else {
 
                      List condition = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,orderItem.getString("productId")), EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,"WebStoreWarehouse"));
		              List<GenericValue> inventoryItems = delegator.findList("InventoryItem",EntityCondition.makeCondition(condition,EntityOperator.AND),UtilMisc.toSet("inventoryItemId","availableToPromiseTotal","comments"),UtilMisc.toList("datetimeReceived DESC"),new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, 1, 1, true),false);
		              GenericValue inventoryItem = EntityUtil.getFirst(inventoryItems);
		              				if(UtilValidate.isNotEmpty(inventoryItem)){
		              					BigDecimal availableToPromiseTotal = (BigDecimal) inventoryItem.get("availableToPromiseTotal");	
//		              					System.out.println("\n\n\n\n availableToPromiseTotal"+orderItem.getString("productId") + "========="  +availableToPromiseTotal);
		              					availableToPromiseTotal = availableToPromiseTotal.subtract(orderItemShipGroupAssoc.getBigDecimal("quantity"));
//		              					System.out.println("\n Subtratc"+availableToPromiseTotal);
		              					inventoryItem.set("availableToPromiseTotal",availableToPromiseTotal);
		              					inventoryItem.set("comments","CSV_import");
		              					tobeStored.add(inventoryItem);
		              					
		              				GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
		              				inventoryItemDetail.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
		              				delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);
		              				inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
		              				inventoryItemDetail.put("quantityOnHandDiff", BigDecimal.ZERO);
		              				inventoryItemDetail.put("availableToPromiseDiff",orderItemShipGroupAssoc.getBigDecimal("quantity").negate());
		              				inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
 		              				inventoryItemDetail.put("description","Order Approved,Reserve inventory");
		              				inventoryItemDetail.put("requiresEca","N"); 
		              				inventoryItemDetail.put("orderId",orderItem.getString("orderId")); 
		              				inventoryItemDetail.put("orderItemSeqId",orderItem.getString("orderItemSeqId")); 

		              				tobeStored.add(inventoryItemDetail);
		              				GenericValue prod = (GenericValue) inventoryDetail.get(orderItem.getString("productId"));
		              				
		              				BigDecimal inventoryAtp = BigDecimal.ZERO;
		              				if(UtilValidate.isNotEmpty(prod.get("inventoryAtp"))){
		              					inventoryAtp = (BigDecimal) prod.get("inventoryAtp");
		              					inventoryAtp = inventoryAtp.subtract(orderItemShipGroupAssoc.getBigDecimal("quantity"));
		              					prod.set("inventoryAtp",inventoryAtp);
		              					tobeStored.add(prod);
		              				}
		              				delegator.storeAll(tobeStored);
		              				}
                                     
                                }
                            }  
                        } catch (Exception e) {
                            String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
                            Debug.logError(e, errMsg, module);
                            resErrorMessages.add(errMsg);
                        }
                    }
                }
            }
        }
    }

    public static void reserveInventory(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, List<GenericValue> orderItemShipGroupInfo, List<String> dropShipGroupIds, Map<String, GenericValue> itemValuesBySeqId, String orderTypeId, String productStoreId, List<String> resErrorMessages) throws GeneralException {
        boolean isImmediatelyFulfilled = false;
        GenericValue productStore = null;
        if (UtilValidate.isNotEmpty(productStoreId)) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            } catch (GenericEntityException e) {
                throw new GeneralException(UtilProperties.getMessage(resource_error, 
                        "OrderErrorCouldNotFindProductStoreWithID", 
                        UtilMisc.toMap("productStoreId", productStoreId), locale) + e.toString());
            }
        }
        if (productStore != null) {
            isImmediatelyFulfilled = "Y".equals(productStore.getString("isImmediatelyFulfilled"));
        }

        boolean reserveInventory = ("SALES_ORDER".equals(orderTypeId));
        if (reserveInventory && isImmediatelyFulfilled) {
            // don't reserve inventory if the product store has isImmediatelyFulfilled set, ie don't if in this store things are immediately fulfilled
            reserveInventory = false;
        }

        // START inventory reservation
        // decrement inventory available for each OrderItemShipGroupAssoc, within the same transaction
        if (UtilValidate.isNotEmpty(orderItemShipGroupInfo)) {
            Iterator<GenericValue> osiInfos = orderItemShipGroupInfo.iterator();
            while (osiInfos.hasNext()) {
                GenericValue orderItemShipGroupAssoc = osiInfos.next();
                if ("OrderItemShipGroupAssoc".equals(orderItemShipGroupAssoc.getEntityName())) {
                    if (dropShipGroupIds != null && dropShipGroupIds.contains(orderItemShipGroupAssoc.getString("shipGroupSeqId"))) {
                        // the items in the drop ship groups are not reserved
                        continue;
                    }
                    GenericValue orderItem = itemValuesBySeqId.get(orderItemShipGroupAssoc.get("orderItemSeqId"));
                    GenericValue orderItemShipGroup = orderItemShipGroupAssoc.getRelatedOne("OrderItemShipGroup");
                    String shipGroupFacilityId = orderItemShipGroup.getString("facilityId");
                    String itemStatus = orderItem.getString("statusId");
                    if ("ITEM_REJECTED".equals(itemStatus) || "ITEM_CANCELLED".equals(itemStatus) || "ITEM_COMPLETED".equals(itemStatus)) {
                        Debug.logInfo("Order item [" + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + "] is not in a proper status for reservation", module);
                        continue;
                    }
                    if (UtilValidate.isNotEmpty(orderItem.getString("productId")) &&   // only reserve product items, ignore non-product items
                            !"RENTAL_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) {  // ignore for rental
                        try {
                            // get the product of the order item
                            GenericValue product = orderItem.getRelatedOne("Product");
                            if (product == null) {
                                Debug.logError("Error when looking up product in reserveInventory service", module);
                                resErrorMessages.add("Error when looking up product in reserveInventory service");
                                continue;
                            }
                            if (reserveInventory) {
                                // for MARKETING_PKG_PICK reserve the components
                                if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_PICK")) {
                                    Map<String, Object> componentsRes = dispatcher.runSync("getAssociatedProducts", UtilMisc.toMap("productId", orderItem.getString("productId"), "type", "PRODUCT_COMPONENT"));
                                    if (ServiceUtil.isError(componentsRes)) {
                                        resErrorMessages.add((String)componentsRes.get(ModelService.ERROR_MESSAGE));
                                        continue;
                                    } else {
                                        List<GenericValue> assocProducts = UtilGenerics.checkList(componentsRes.get("assocProducts"));
                                        Iterator<GenericValue> assocProductsIter = assocProducts.iterator();
                                        while (assocProductsIter.hasNext()) {
                                            GenericValue productAssoc = assocProductsIter.next();
                                            BigDecimal quantityOrd = productAssoc.getBigDecimal("quantity");
                                            BigDecimal quantityKit = orderItemShipGroupAssoc.getBigDecimal("quantity");
                                            BigDecimal quantity = quantityOrd.multiply(quantityKit);
                                            Map<String, Object> reserveInput = new HashMap<String, Object>();
                                            reserveInput.put("productStoreId", productStoreId);
                                            reserveInput.put("productId", productAssoc.getString("productIdTo"));
                                            reserveInput.put("orderId", orderItem.getString("orderId"));
                                            reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                            reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                            reserveInput.put("quantity", quantity);
                                            reserveInput.put("userLogin", userLogin);
                                            reserveInput.put("facilityId", shipGroupFacilityId);
                                            Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);

                                            if (ServiceUtil.isError(reserveResult)) {
                                                String invErrMsg = "The product ";
                                                if (product != null) {
                                                    invErrMsg += getProductName(product, orderItem);
                                                }
                                                invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                                resErrorMessages.add(invErrMsg);
                                            }
                                        }
                                    }
                                } else {
                                    // reserve the product
                                    Map<String, Object> reserveInput = new HashMap<String, Object>();
                                    reserveInput.put("productStoreId", productStoreId);
                                    reserveInput.put("productId", orderItem.getString("productId"));
                                    reserveInput.put("orderId", orderItem.getString("orderId"));
                                    reserveInput.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                    reserveInput.put("shipGroupSeqId", orderItemShipGroupAssoc.getString("shipGroupSeqId"));
                                    reserveInput.put("facilityId", shipGroupFacilityId);
                                    // use the quantity from the orderItemShipGroupAssoc, NOT the orderItem, these are reserved by item-group assoc
                                    reserveInput.put("quantity", orderItemShipGroupAssoc.getBigDecimal("quantity"));
                                    reserveInput.put("userLogin", userLogin);
                                    Map<String, Object> reserveResult = dispatcher.runSync("reserveStoreInventory", reserveInput);

                                    if (ServiceUtil.isError(reserveResult)) {
                                        String invErrMsg = "The product ";
                                        if (product != null) {
                                            invErrMsg += getProductName(product, orderItem);
                                        }
                                        invErrMsg += " with ID " + orderItem.getString("productId") + " is no longer in stock. Please try reducing the quantity or removing the product from this order.";
                                        resErrorMessages.add(invErrMsg);
                                    }
                                }
                            }
                            // Reserving inventory or not we still need to create a marketing package
                            // If the product is a marketing package auto, attempt to create enough packages to bring ATP back to 0, won't necessarily create enough to cover this order.
                            if (EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.getString("productTypeId"), "parentTypeId", "MARKETING_PKG_AUTO")) {
                                // do something tricky here: run as the "system" user
                                // that can actually create and run a production run
                                GenericValue permUserLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", "system"));
                                Map<String, Object> inputMap = new HashMap<String, Object>();
                                if (UtilValidate.isNotEmpty(shipGroupFacilityId)) {
                                    inputMap.put("facilityId", shipGroupFacilityId);
                                } else {
                                    inputMap.put("facilityId", productStore.getString("inventoryFacilityId"));
                                }
                                inputMap.put("orderId", orderItem.getString("orderId"));
                                inputMap.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                                inputMap.put("userLogin", permUserLogin);
                                Map<String, Object> prunResult = dispatcher.runSync("createProductionRunForMktgPkg", inputMap);
                                if (ServiceUtil.isError(prunResult)) {
                                    Debug.logError(ServiceUtil.getErrorMessage(prunResult) + " for input:" + inputMap, module);
                                }
                            }
                        } catch (GenericServiceException e) {
                            String errMsg = "Fatal error calling reserveStoreInventory service: " + e.toString();
                            Debug.logError(e, errMsg, module);
                            resErrorMessages.add(errMsg);
                        }
                    }
                }
            }
        }
    }
  
    public static String getProductName(GenericValue product, GenericValue orderItem) {
        if (UtilValidate.isNotEmpty(product.getString("productName"))) {
            return product.getString("productName");
        } else {
            return orderItem.getString("itemDescription");
        }
    }

    /** Service for creating Loyality Point */
    public static Map generateLoyalityPoint(DispatchContext ctx, Map context) {
    GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
    String orderId = (String) context.get("orderId");
    GenericValue orderHeader = null;
    try {
        orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
    } catch (GenericEntityException e) {
        String errMsg = "ERROR: Could not set grantTotal on OrderHeader entity: " + e.toString();
        Debug.logError(e, errMsg, module);
        return ServiceUtil.returnError(errMsg);
    }
    BigDecimal currentTotal = orderHeader.getBigDecimal("grandTotal");
    int totalLoyaltyPoints = 0;
    BigDecimal loyaltyPoints = ZERO;
    BigDecimal multiplicand = new BigDecimal(2);
    BigDecimal totalOrderPay = new BigDecimal(1000);
    int loyaltyRupee = 0;
    if (UtilValidate.isNotEmpty(currentTotal)) {
    	loyaltyPoints = currentTotal.multiply(multiplicand);
    	totalLoyaltyPoints = loyaltyPoints.intValue();
    } 
    if(currentTotal.intValue() > 999)
    {
    	for (int i=1000;i<=currentTotal.intValue();i=i+1000){
    		totalLoyaltyPoints=totalLoyaltyPoints+totalOrderPay.intValue();}
    }
    
	int _numberOfOrd=0;
	BigDecimal totalOrderValue=BigDecimal.ZERO;
	String userId=null;
	userId=orderHeader.getString("createdBy");
	 if(userId!=null){
		 try {
				GenericValue loyal=delegator.findByPrimaryKey("LoyaltyPoint", UtilMisc.toMap("userLoginId", userId));
				if(loyal!=null){
				String point=loyal.getString("loyaltyPoint");
				int loyalRupee= Integer.parseInt(loyal.getString("loyaltyRupee"));
				 if(point!=null){
					 totalLoyaltyPoints=Integer.parseInt(point)+totalLoyaltyPoints;
				 }else{
					 totalLoyaltyPoints=0;
				 }
				 if(totalLoyaltyPoints > 999)
				    {
				    	for(int i=1000;i<=totalLoyaltyPoints;i=i+1000){
				    		loyaltyRupee = loyaltyRupee + 5;}
				    }
				 loyaltyRupee = loyalRupee+loyaltyRupee;
				 totalOrderValue=loyal.getBigDecimal("totalOrderAmt");
				 if(totalOrderValue!=null){
					 totalOrderValue=totalOrderValue.add(orderHeader.getBigDecimal("grandTotal"));
				 }else{
					 totalOrderValue=BigDecimal.ZERO; 
				 }
				 String totOrder=null;
				 totOrder=loyal.getString("totalOrders");
				 loyal.set("totalOrderAmt", totalOrderValue);
				 loyal.set("totalOrders", String.valueOf(Integer.parseInt(totOrder)+1));
				 loyal.set("loyaltyPoint",String.valueOf(totalLoyaltyPoints));
				 loyal.set("loyaltyRupee", String.valueOf(loyaltyRupee));
				 loyal.set("isCoupan", "N");
				 loyal.set("coupanRupee", "0");
				 loyal.set("coupanCode", "NO_CODE");
		         loyal.store();
			}else{
				 //TODO:Comming First Time
				 loyal=delegator.makeValue("LoyaltyPoint");
				 if(totalLoyaltyPoints > 999)
				    {
				    	for(int i=1000;i<=totalLoyaltyPoints;i=i+1000){
				    		loyaltyRupee = loyaltyRupee + 5;}
				    }
				 loyal.set("userLoginId", userId);
  				 loyal.set("loyaltyPoint", String.valueOf(totalLoyaltyPoints));
  				 loyal.set("loyaltyRupee", String.valueOf(loyaltyRupee));
				 loyal.set("totalOrderAmt", orderHeader.getBigDecimal("grandTotal"));
				 loyal.set("totalOrders", "1");
				 loyal.set("coupanRupee", "0");
				 loyal.set("isCoupan", "N");
				 loyal.set("coupanCode", "NO_CODE");
				 loyal.create();
			 }
 	    	} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
 	    		Debug.logError(e.getMessage(),module);
 	    		//e.printStackTrace();
			} 
	 }
	 try {
         loyalPointsToReference(delegator, ctx,context, userId);
     } catch (GeneralException e) {
         return ServiceUtil.returnError(e.getMessage());
     }
     try {
    	 createLoyaltyBillingAccount(ctx,context, userId);
     } catch (Exception e) {
         return ServiceUtil.returnError(e.getMessage());
     }
	 return ServiceUtil.returnSuccess();
    }
    
    public static void loyalPointsToReference(Delegator delegator, DispatchContext ctx,Map context, String userId) throws GeneralException {
    	int totalLoyaltyPoints = 0;
    	int totalReferrelPoints = 2000;
    	BigDecimal inviteFriendPoint=new BigDecimal(1);
    	int numberOfInvitingFriend=1;
        int loyaltyRupee = 0;
        GenericValue partyData=null;
        GenericValue userLogin=null;
        String referedByLoginId =null;
        String uniqueRefId=null;
        try {
        	userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", userId));
        	partyData = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",userLogin.getString("partyId")));
        }
        catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting the Party Data from Person Table", module);
        }
    	if (UtilValidate.isNotEmpty(partyData.getString("uniqueRefLoginId")) && !"(NULL)".equals(partyData.getString("uniqueRefLoginId"))){
   		 try {
   			uniqueRefId=partyData.getString("uniqueRefLoginId");
   			GenericValue referredby=delegator.findByPrimaryKey("LoyaltyReference", UtilMisc.toMap("loyaltyRefId", uniqueRefId));
	   			if(referredby!=null){
	   				referedByLoginId = referredby.getString("refByPartyId");
	   			}
	   			try{
   				GenericValue loyal=delegator.findByPrimaryKey("LoyaltyPoint", UtilMisc.toMap("userLoginId", referedByLoginId));
   				if(loyal!=null){
   				String point=loyal.getString("loyaltyPoint");
   				int loyalRupee= Integer.parseInt(loyal.getString("loyaltyRupee"));
   				 if(point!=null){
   					 totalLoyaltyPoints=Integer.parseInt(point)+totalReferrelPoints;
   				 }else{
   					 totalLoyaltyPoints=0;
   				 }
   				 loyaltyRupee = loyalRupee + 10;
   				 String totOrder=null;
   				 totOrder=loyal.getString("totalOrders");
   				 loyal.set("totalOrders", String.valueOf(Integer.parseInt(totOrder)));
   				 loyal.set("loyaltyPoint",String.valueOf(totalLoyaltyPoints));
   				 loyal.set("loyaltyRupee", String.valueOf(loyaltyRupee));
   				 loyal.set("isCoupan", "N");
   				 loyal.set("coupanRupee", "0");
   				 loyal.set("coupanCode", "NO_CODE");
   		         loyal.store();
   		         
   		         
   		         
   		         
   			}else{
   				 //TODO:Comming First Time
   				 loyal=delegator.makeValue("LoyaltyPoint");
   				 loyal.set("userLoginId", referedByLoginId);
     				 loyal.set("loyaltyPoint", String.valueOf(totalReferrelPoints));
     				 loyal.set("loyaltyRupee", String.valueOf(10));
   				 loyal.set("totalOrders", "0");
   				 loyal.set("coupanRupee", "0");
   				 loyal.set("isCoupan", "N");
   				 loyal.set("coupanCode", "NO_CODE");
   				 loyal.create();
   			 }
   				
   				
	   			}catch (GenericEntityException e) {
   				// TODO Auto-generated catch block
    	    		Debug.logError(e.getMessage(),module);
	   				
	   			}
	   			
	   			
	   			try{
	   				GenericValue loyal=delegator.findByPrimaryKey("InviteFriendPoint", UtilMisc.toMap("userLoginId", referedByLoginId));
	   				if(loyal!=null){
	   				String point=loyal.getString("totalInviteFriendPoints");
	   				String noOfFriend=loyal.getString("numberOfInvitingFriend");
	   				 if(point!=null){
	   					inviteFriendPoint=new BigDecimal(point).add(inviteFriendPoint);
	   				 }
	   				 
	   				if(noOfFriend!=null){
	   					numberOfInvitingFriend=Integer.parseInt(noOfFriend)+numberOfInvitingFriend;
	   				 }
	   				 loyal.set("totalInviteFriendPoints",String.valueOf(inviteFriendPoint));
	   				loyal.set("numberOfInvitingFriend",String.valueOf(numberOfInvitingFriend));
	   				 loyal.store();
	   		        }else{
	   				 //TODO:Comming First Time
	   				 loyal=delegator.makeValue("InviteFriendPoint");
	   				 loyal.set("userLoginId", referedByLoginId);
	   				loyal.set("totalInviteFriendPoints",String.valueOf(inviteFriendPoint));
	   				loyal.set("numberOfInvitingFriend",String.valueOf(numberOfInvitingFriend));
	   				 loyal.create();
	   			 }
	   				
	   				
		   			}catch (GenericEntityException e) {
	   				// TODO Auto-generated catch block
	    	    		Debug.logError(e.getMessage(),module);
		   				
		   			}
		   			
   				
   				
   				
   				
   				
   				
   				
    	    	} catch (GenericEntityException e) {
   				// TODO Auto-generated catch block
    	    		Debug.logError(e.getMessage(),module);
    	    		//e.printStackTrace();
   			} 
    	    	partyData.set("referByLoginId", "(NULL)");
    	    	partyData.set("uniqueRefLoginId", "(NULL)");
    	    	partyData.set("usedUniqueRefId", uniqueRefId);
    	    	partyData.store();
    	    	
    	    	 try {
    	    		 createLoyaltyBillingAccount(ctx,context, referedByLoginId);
    	        	 
    	         } catch (Exception e) {
    	            
    	         }
    	    	
   	 }
    	
    	
    	
    	
    	
    	
    }
    
    
    
    
    public static Map createLoyaltyBillingAccount(DispatchContext ctx, Map context,String userId) {
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	return createLoyaltyBillingAccount(dispatcher , userId);
    }
    
    /** Service for creating Loyality Point */
    public static Map createLoyaltyBillingAccount(LocalDispatcher dispatcher , String userId) {

    GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
     
    GenericValue userLogin = null;
	try {
		userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",userId));
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    BigDecimal totalLoyaltyBillAmt=BigDecimal.ZERO;
    List<GenericValue> partyBillValue = null;
    Map<String, Object> results = new HashMap<String, Object>();
    String loyalPoints= null;
    int loyalRupee = 0;
    int dividends= 1000;
    try {
    	GenericValue loyal=delegator.findByPrimaryKey("LoyaltyPoint", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
    	if(loyal!=null){
			loyalRupee= Integer.parseInt(loyal.getString("loyaltyRupee"));
			loyalPoints = loyal.getString("loyaltyPoint");
    	}
    	
    	if(loyalRupee != 0 && UtilValidate.isNotEmpty(loyalRupee)){
    		totalLoyaltyBillAmt = totalLoyaltyBillAmt.add(new BigDecimal(loyal.getString("loyaltyRupee")));
    	}
        
        partyBillValue  = delegator.findList("BillingAccountRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")), null, null, null, false);
        	if (UtilValidate.isNotEmpty(partyBillValue)) {
                for (GenericValue partyBill : partyBillValue) {
                	EntityCondition barFindCond = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, partyBill.getString("billingAccountId")),
                            EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + "Credit Account for Loyalty #") +userLogin.getString("partyId")+"%"))), EntityOperator.AND);
                	List<GenericValue> billingAccountRoleList = delegator.findList("BillingAccount", barFindCond, null, null, null, false);
                	if (UtilValidate.isNotEmpty(billingAccountRoleList)) {
                		GenericValue billingAmt=delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", partyBill.getString("billingAccountId")));
                		if(billingAmt!=null){
                			BigDecimal billingAmtRupee = totalLoyaltyBillAmt.add(new BigDecimal(billingAmt.getString("accountLimit")));
                			billingAmt.set("accountLimit", billingAmtRupee);
                			billingAmt.store();
                    	}
                		results.put("billingAccountId", partyBill.getString("billingAccountId"));
                	}else
                	{
                		Map<String, Object> input = UtilMisc.<String, Object>toMap("accountLimit", totalLoyaltyBillAmt, "description", "Credit Account for Loyalty #" + userLogin.getString("partyId"), "userLogin", userLogin);
                        input.put("accountCurrencyUomId", "INR");
                        //input.put("thruDate", UtilDateTime.nowTimestamp());
                		results = dispatcher.runSync("createBillingAccount", input);
                		if (ServiceUtil.isError(results)) return results;
                        String billingAccountId = (String) results.get("billingAccountId");
                        
                     // set the role on the account
                        input = UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", userLogin.getString("partyId"), "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
                        Map<String, Object> roleResults = dispatcher.runSync("createBillingAccountRole", input);
                        if (ServiceUtil.isError(roleResults)) {
                            Debug.logError("Error with createBillingAccountRole: " + roleResults.get(ModelService.ERROR_MESSAGE), module);
                        }
                	}
                	
                }
        }else
    	{
    		Map<String, Object> input = UtilMisc.<String, Object>toMap("accountLimit", totalLoyaltyBillAmt, "description", "Credit Account for Loyalty #" + userLogin.getString("partyId"), "userLogin", userLogin);
            input.put("accountCurrencyUomId", "INR");
            //input.put("thruDate", UtilDateTime.nowTimestamp());
    		results = dispatcher.runSync("createBillingAccount", input);
    		if (ServiceUtil.isError(results)) return results;
            String billingAccountId = (String) results.get("billingAccountId");
            
         // set the role on the account
            input = UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", userLogin.getString("partyId"), "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
            Map<String, Object> roleResults = dispatcher.runSync("createBillingAccountRole", input);
            if (ServiceUtil.isError(roleResults)) {
                Debug.logError("Error with createBillingAccountRole: " + roleResults.get(ModelService.ERROR_MESSAGE), module);
            }
    	}
        
        int remainingPoints = Integer.parseInt(loyalPoints) % dividends;
        loyal.set("loyaltyPoint",String.valueOf(remainingPoints));
        loyal.set("loyaltyRupee", "0");
        loyal.store();
        return results;
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		Debug.logError(e, "Entity error when creating BillingAccount: " + e.getMessage(), module);
		e.printStackTrace();
	}catch (GenericEntityException e) {
			// TODO Auto-generated catch block
		Debug.logError(e.getMessage(),module);
		//e.printStackTrace();
	} 
	 return ServiceUtil.returnSuccess();
    }
    
    
    //for invite a friend reference
    public static Map reducedInviteReferencePoint(DispatchContext ctx, Map context) {
        GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
        String orderId = (String) context.get("orderId");
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if(UtilValidate.isNotEmpty(orderHeader))
            {
            	List<EntityCondition> condition=new ArrayList();
            	condition.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId));
            	condition.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS,"EXT_INVITE"));
            	List<GenericValue> paymentList=delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(condition,EntityOperator.AND), null, null, null, false);
            	GenericValue gv=EntityUtil.getFirst(paymentList);
            	BigDecimal tem=new BigDecimal(0);
            	if(UtilValidate.isNotEmpty(gv))
            	{
            	 tem =(gv.getBigDecimal("maxAmount").multiply(new BigDecimal(100))).divide(orderHeader.getBigDecimal("remainingSubTotal"), 1, RoundingMode.CEILING);
            	GenericValue inviteRef=delegator.findByPrimaryKey("InviteFriendPoint", UtilMisc.toMap("userLoginId",gv.getString("createdByUserLogin")));
            	if(UtilValidate.isNotEmpty(inviteRef))
            	{
            		inviteRef.set("totalInviteFriendPoints", (new BigDecimal(inviteRef.getString("totalInviteFriendPoints")).subtract(tem)).toString());
            		inviteRef.store();
            	}
            	}
            	
            }
        } catch (GenericEntityException e) {
            String errMsg = "ERROR: Could not set grantTotal on OrderHeader entity: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return ServiceUtil.returnSuccess();
    }
        
    
    
    public static String getProductName(GenericValue product, String orderItemName) {
        if (UtilValidate.isNotEmpty(product.getString("productName"))) {
            return product.getString("productName");
        } else {
            return orderItemName;
        }
    }

    public static String determineSingleFacilityFromOrder(GenericValue orderHeader) {
        if (orderHeader != null) {
            String productStoreId = orderHeader.getString("productStoreId");
            if (productStoreId != null) {
                return ProductStoreWorker.determineSingleFacilityForStore(orderHeader.getDelegator(), productStoreId);
            }
        }
        return null;
    }

    /** Service for resetting the OrderHeader grandTotal */
    public static Map<String, Object> resetGrandTotal(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        //appears to not be used: GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            String errMsg = "ERROR: Could not set grantTotal on OrderHeader entity: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            BigDecimal currentTotal = orderHeader.getBigDecimal("grandTotal");
            BigDecimal currentSubTotal = orderHeader.getBigDecimal("remainingSubTotal");

            // get the new grand total
            BigDecimal updatedTotal = orh.getOrderGrandTotal();

            String productStoreId = orderHeader.getString("productStoreId");
            String showPricesWithVatTax = null;
            if (UtilValidate.isNotEmpty(productStoreId)) {
                GenericValue productStore = null;
                try {
                    productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
                } catch (GenericEntityException e) {
                    String errorMessage = UtilProperties.getMessage(resource_error, 
                            "OrderErrorCouldNotFindProductStoreWithID", 
                            UtilMisc.toMap("productStoreId", productStoreId), (Locale) context.get("locale")) + e.toString();
                    Debug.logError(e, errorMessage, module);
                    return ServiceUtil.returnError(errorMessage + e.getMessage() + ").");
                }
                showPricesWithVatTax  = productStore.getString("showPricesWithVatTax");
            }
            BigDecimal remainingSubTotal = ZERO;
            if (UtilValidate.isNotEmpty(productStoreId) && "Y".equalsIgnoreCase(showPricesWithVatTax)) {
                // calculate subTotal as grandTotal + taxes - (returnsTotal + shipping of all items)
                remainingSubTotal = updatedTotal.subtract(orh.getOrderReturnedTotal()).subtract(orh.getShippingTotal());
            } else {
                // calculate subTotal as grandTotal - returnsTotal - (tax + shipping of items not returned)
                remainingSubTotal = updatedTotal.subtract(orh.getOrderReturnedTotal()).subtract(orh.getOrderNonReturnedTaxAndShipping());
            }

            if (currentTotal == null || currentSubTotal == null || updatedTotal.compareTo(currentTotal) != 0 ||
                    remainingSubTotal.compareTo(currentSubTotal) != 0) {
                orderHeader.set("grandTotal", updatedTotal);
                orderHeader.set("remainingSubTotal", remainingSubTotal);
                try {
                    orderHeader.store();
                } catch (GenericEntityException e) {
                    String errMsg = "ERROR: Could not set grandTotal on OrderHeader entity: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for setting the OrderHeader grandTotal for all OrderHeaders with no grandTotal */
    public static Map<String, Object> setEmptyGrandTotals(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Boolean forceAll = (Boolean) context.get("forceAll");
        Locale locale = (Locale) context.get("locale");
        if (forceAll == null) {
            forceAll = Boolean.FALSE;
        }

        EntityCondition cond = null;
        if (!forceAll.booleanValue()) {
            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("grandTotal", EntityOperator.EQUALS, null),
                    EntityCondition.makeCondition("remainingSubTotal", EntityOperator.EQUALS, null));
            cond = EntityCondition.makeCondition(exprs, EntityOperator.OR);
        }
        Set<String> fields = UtilMisc.toSet("orderId");

        EntityListIterator eli = null;
        try {
            eli = delegator.find("OrderHeader", cond, null, fields, null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (eli != null) {
            // reset each order
            GenericValue orderHeader = null;
            while ((orderHeader = eli.next()) != null) {
                String orderId = orderHeader.getString("orderId");
                Map<String, Object> resetResult = null;
                try {
                    resetResult = dispatcher.runSync("resetGrandTotal", UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin));
                } catch (GenericServiceException e) {
                    Debug.logError(e, "ERROR: Cannot reset order totals - " + orderId, module);
                }

                if (resetResult != null && ServiceUtil.isError(resetResult)) {
                    Debug.logWarning(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotResetOrderTotals", 
                            UtilMisc.toMap("orderId",orderId,"resetResult",ServiceUtil.getErrorMessage(resetResult)), locale), module);
                }
            }

            // close the ELI
            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logInfo("No orders found for reset processing", module);
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for checking and re-calc the tax amount */
    public static Map<String, Object> recalcOrderTax(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotGetOrderHeaderEntity",locale) + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorNoValidOrderHeaderFoundForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // don't charge tax on purchase orders, better we still do.....
//        if ("PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
//            return ServiceUtil.returnSuccess();
//        }

        // Retrieve the order tax adjustments
        List<GenericValue> orderTaxAdjustments = null;
        try {
            orderTaxAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderAdjustmentTypeId", "SALES_TAX"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to retrieve SALES_TAX adjustments for order : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderUnableToRetrieveSalesTaxAdjustments",locale));
        }

        // Accumulate the total existing tax adjustment
        BigDecimal totalExistingOrderTax = ZERO;
        Iterator<GenericValue> otait = UtilMisc.toIterator(orderTaxAdjustments);
        while (otait != null && otait.hasNext()) {
            GenericValue orderTaxAdjustment = otait.next();
            if (orderTaxAdjustment.get("amount") != null) {
                totalExistingOrderTax = totalExistingOrderTax.add(orderTaxAdjustment.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
            }
        }

        // Recalculate the taxes for the order
        BigDecimal totalNewOrderTax = ZERO;
        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List<GenericValue> shipGroups = orh.getOrderItemShipGroups();
        if (shipGroups != null) {
            Iterator<GenericValue> itr = shipGroups.iterator();
            while (itr.hasNext()) {
                GenericValue shipGroup = itr.next();
                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");

                List<GenericValue> validOrderItems = orh.getValidOrderItems(shipGroupSeqId);
                if (validOrderItems != null) {
                    // prepare the inital lists
                    List<GenericValue> products = new ArrayList<GenericValue>(validOrderItems.size());
                    List<BigDecimal> amounts = new ArrayList<BigDecimal>(validOrderItems.size());
                    List<BigDecimal> shipAmts = new ArrayList<BigDecimal>(validOrderItems.size());
                    List<BigDecimal> itPrices = new ArrayList<BigDecimal>(validOrderItems.size());
                    List<BigDecimal> itQuantities = new ArrayList<BigDecimal>(validOrderItems.size());

                    // adjustments and total
                    List<GenericValue> allAdjustments = orh.getAdjustments();
                    List<GenericValue> orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments, shipGroupSeqId);
                    BigDecimal orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(validOrderItems, allAdjustments);

                    // shipping amount
                    BigDecimal orderShipping = OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true);

                    //promotions amount
                    BigDecimal orderPromotions = OrderReadHelper.calcOrderPromoAdjustmentsBd(allAdjustments);

                    // build up the list of tax calc service parameters
                    for (int i = 0; i < validOrderItems.size(); i++) {
                        GenericValue orderItem = validOrderItems.get(i);
                        String productId = orderItem.getString("productId");
                        try {
                            products.add(i, delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId)));  // get the product entity
                            amounts.add(i, OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false)); // get the item amount
                            shipAmts.add(i, OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true)); // get the shipping amount
                            itPrices.add(i, orderItem.getBigDecimal("unitPrice"));
                            itQuantities.add(i, orderItem.getBigDecimal("quantity"));
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Cannot read order item entity : " + orderItem, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderCannotReadTheOrderItemEntity",locale));
                        }
                    }

                    GenericValue shippingAddress = orh.getShippingAddress(shipGroupSeqId);
                    // no shipping address, try the billing address
                    if (shippingAddress == null) {
                        List<GenericValue> billingAddressList = orh.getBillingLocations();
                        if (billingAddressList.size() > 0) {
                            shippingAddress = billingAddressList.get(0);
                        }
                    }

                    // TODO and NOTE DEJ20070816: this is NOT a good way to determine if this is a face-to-face or immediatelyFulfilled order
                    //this should be made consistent with the CheckOutHelper.makeTaxContext(int shipGroup, GenericValue shipAddress) method
                    if (shippingAddress == null) {
                        // face-to-face order; use the facility address
                        String facilityId = orderHeader.getString("originFacilityId");
                        if (facilityId != null) {
                            GenericValue facilityContactMech = ContactMechWorker.getFacilityContactMechByPurpose(delegator, facilityId, UtilMisc.toList("SHIP_ORIG_LOCATION", "PRIMARY_LOCATION"));
                            if (facilityContactMech != null) {
                                try {
                                    shippingAddress = delegator.findByPrimaryKey("PostalAddress",
                                            UtilMisc.toMap("contactMechId", facilityContactMech.getString("contactMechId")));
                                } catch (GenericEntityException e) {
                                    Debug.logError(e, module);
                                }
                            }
                        }
                    }

                    // if shippingAddress is still null then don't calculate tax; it may be an situation where no tax is applicable, or the data is bad and we don't have a way to find an address to check tax for
                    if (shippingAddress == null) {
                        Debug.logWarning("Not calculating tax for Order [" + orderId + "] because there is no shippingAddress, and no address on the origin facility [" +  orderHeader.getString("originFacilityId") + "]", module);
                        continue;
                    }

                    // prepare the service context
                    Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("productStoreId", orh.getProductStoreId(), "itemProductList", products, "itemAmountList", amounts,
                        "itemShippingList", shipAmts, "itemPriceList", itPrices, "itemQuantityList", itQuantities, "orderShippingAmount", orderShipping);
                    serviceContext.put("shippingAddress", shippingAddress);
                    serviceContext.put("orderPromotionsAmount", orderPromotions);
                    if (orh.getBillToParty() != null) serviceContext.put("billToPartyId", orh.getBillToParty().getString("partyId"));
                    if (orh.getBillFromParty() != null) serviceContext.put("payToPartyId", orh.getBillFromParty().getString("partyId"));

                    // invoke the calcTax service
                    Map<String, Object> serviceResult = null;
                    try {
                        serviceResult = dispatcher.runSync("calcTax", serviceContext);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderProblemOccurredInTaxService",locale));
                    }

                    if (ServiceUtil.isError(serviceResult)) {
                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
                    }

                    // the adjustments (returned in order) from the tax service
                    List<GenericValue> orderAdj = UtilGenerics.checkList(serviceResult.get("orderAdjustments"));
                    List<List<GenericValue>> itemAdj = UtilGenerics.checkList(serviceResult.get("itemAdjustments"));

                    // Accumulate the new tax total from the recalculated header adjustments
                    if (UtilValidate.isNotEmpty(orderAdj)) {
                        Iterator<GenericValue> oai = orderAdj.iterator();
                        while (oai.hasNext()) {
                            GenericValue oa = oai.next();
                            if (oa.get("amount") != null) {
                                totalNewOrderTax = totalNewOrderTax.add(oa.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
                            }


                        }
                    }

                    // Accumulate the new tax total from the recalculated item adjustments
                    if (UtilValidate.isNotEmpty(itemAdj)) {
                        for (int i = 0; i < itemAdj.size(); i++) {
                            List<GenericValue> itemAdjustments = itemAdj.get(i);
                            Iterator<GenericValue> ida = itemAdjustments.iterator();
                            while (ida.hasNext()) {
                                GenericValue ia = ida.next();
                                if (ia.get("amount") != null) {
                                    totalNewOrderTax = totalNewOrderTax.add(ia.getBigDecimal("amount").setScale(taxDecimals, taxRounding));
                                }
                            }
                        }
                    }
                }
            }

            // Determine the difference between existing and new tax adjustment totals, if any
            BigDecimal orderTaxDifference = totalNewOrderTax.subtract(totalExistingOrderTax).setScale(taxDecimals, taxRounding);

            // If the total has changed, create an OrderAdjustment to reflect the fact
            if (orderTaxDifference.signum() != 0) {
                Map<String, Object> createOrderAdjContext = new HashMap<String, Object>();
                createOrderAdjContext.put("orderAdjustmentTypeId", "SALES_TAX");
                createOrderAdjContext.put("orderId", orderId);
                createOrderAdjContext.put("orderItemSeqId", "_NA_");
                createOrderAdjContext.put("shipGroupSeqId", "_NA_");
                createOrderAdjContext.put("description", "Tax adjustment due to order change");
                createOrderAdjContext.put("amount", orderTaxDifference);
                createOrderAdjContext.put("userLogin", userLogin);
                Map<String, Object> createOrderAdjResponse = null;
                try {
                    createOrderAdjResponse = dispatcher.runSync("createOrderAdjustment", createOrderAdjContext);
                } catch (GenericServiceException e) {
                    String createOrderAdjErrMsg = UtilProperties.getMessage(resource_error, 
                            "OrderErrorCallingCreateOrderAdjustmentService", locale);
                    Debug.logError(createOrderAdjErrMsg, module);
                    return ServiceUtil.returnError(createOrderAdjErrMsg);
                }
                if (ServiceUtil.isError(createOrderAdjResponse)) {
                    Debug.logError(ServiceUtil.getErrorMessage(createOrderAdjResponse), module);
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createOrderAdjResponse));
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for checking and re-calc the shipping amount */
    public static Map<String, Object> recalcOrderShipping(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotGetOrderHeaderEntity",locale) + e.getMessage());
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorNoValidOrderHeaderFoundForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        List<GenericValue> shipGroups = orh.getOrderItemShipGroups();
        if (shipGroups != null) {
            Iterator<GenericValue> i = shipGroups.iterator();
            while (i.hasNext()) {
                GenericValue shipGroup = i.next();
                String shipGroupSeqId = shipGroup.getString("shipGroupSeqId");

                if (shipGroup.get("contactMechId") == null || shipGroup.get("shipmentMethodTypeId") == null) {
                    // not shipped (face-to-face order)
                    continue;
                }

                Map<String, Object> shippingEstMap = ShippingEvents.getShipEstimate(dispatcher, delegator, orh, shipGroupSeqId);
                BigDecimal shippingTotal = null;
                if (UtilValidate.isEmpty(orh.getValidOrderItems(shipGroupSeqId))) {
                    shippingTotal = ZERO;
                    Debug.log("No valid order items found - " + shippingTotal, module);
                } else {
                    shippingTotal = UtilValidate.isEmpty(shippingEstMap.get("shippingTotal")) ? ZERO : (BigDecimal)shippingEstMap.get("shippingTotal");
                    shippingTotal = shippingTotal.setScale(orderDecimals, orderRounding);
                    Debug.log("Got new shipping estimate - " + shippingTotal, module);
                }
                if (Debug.infoOn()) {
                    Debug.log("New Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + shippingTotal, module);
                }

                BigDecimal currentShipping = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orh.getOrderItemAndShipGroupAssoc(shipGroupSeqId), orh.getAdjustments(), false, false, true);
                currentShipping = currentShipping.add(OrderReadHelper.calcOrderAdjustments(orh.getOrderHeaderAdjustments(shipGroupSeqId), orh.getOrderItemsSubTotal(), false, false, true));

                if (Debug.infoOn()) {
                    Debug.log("Old Shipping Total [" + orderId + " / " + shipGroupSeqId + "] : " + currentShipping, module);
                }

                List<String> errorMessageList = UtilGenerics.checkList(shippingEstMap.get(ModelService.ERROR_MESSAGE_LIST));
                if (errorMessageList != null) {
                    Debug.logWarning("Problem finding shipping estimates for [" + orderId + "/ " + shipGroupSeqId + "] = " + errorMessageList, module);
                    continue;
                }

                if ((shippingTotal != null) && (shippingTotal.compareTo(currentShipping) != 0)) {
                    // place the difference as a new shipping adjustment
                    BigDecimal adjustmentAmount = shippingTotal.subtract(currentShipping);
                    String adjSeqId = delegator.getNextSeqId("OrderAdjustment");
                    GenericValue orderAdjustment = delegator.makeValue("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", adjSeqId));
                    orderAdjustment.set("orderAdjustmentTypeId", "SHIPPING_CHARGES");
                    orderAdjustment.set("amount", adjustmentAmount);
                    orderAdjustment.set("orderId", orh.getOrderId());
                    orderAdjustment.set("shipGroupSeqId", shipGroupSeqId);
                    orderAdjustment.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                    orderAdjustment.set("createdDate", UtilDateTime.nowTimestamp());
                    orderAdjustment.set("createdByUserLogin", userLogin.getString("userLoginId"));
                    //orderAdjustment.set("comments", "Shipping Re-Calc Adjustment");
                    try {
                        orderAdjustment.create();
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem creating shipping re-calc adjustment : " + orderAdjustment, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderErrorCannotCreateAdjustment",locale));
                    }
                }

                // TODO: re-balance free shipping adjustment
            }
        }

        return ServiceUtil.returnSuccess();

    }

    /** Service for checking to see if an order is fully completed or canceled */
    public static Map<String, Object> checkItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        // get the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderHeader record", module);
        }
        if (orderHeader == null) {
            Debug.logError("OrderHeader came back as null", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderCannotUpdateNullOrderHeader",UtilMisc.toMap("orderId",orderId),locale));
        }

        // get the order items
        List<GenericValue> orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
            
            /* Ajaya For Packing To Work */
        	List<EntityExpr> exprs = UtilMisc.toList(
                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                    EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
        	orderItems = EntityUtil.filterByAnd(orderItems, exprs);
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get OrderItem records", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderProblemGettingOrderItemRecords", locale));
        }

        String orderHeaderStatusId = orderHeader.getString("statusId");
        String orderTypeId = orderHeader.getString("orderTypeId");

        boolean allCanceled = true;
        boolean allComplete = true;
        boolean allApproved = true;
        if (orderItems != null) {
            Iterator<GenericValue> itemIter = orderItems.iterator();
            while (itemIter.hasNext()) {
                GenericValue item = itemIter.next();
                String statusId = item.getString("statusId");
                //Debug.log("Item Status: " + statusId, module);
                if (!"ITEM_CANCELLED".equals(statusId)) {
                    //Debug.log("Not set to cancel", module);
                    allCanceled = false;
                    if (!"ITEM_COMPLETED".equals(statusId)) {
                        //Debug.log("Not set to complete", module);
                        allComplete = false;
                        if (!"ITEM_APPROVED".equals(statusId)) {
                            //Debug.log("Not set to approve", module);
                            allApproved = false;
                            break;
                        }
                    }
                }
            }

            // find the next status to set to (if any)
            String newStatus = null;
            if (allCanceled) {
                if (!"PURCHASE_ORDER".equals(orderTypeId)) {
                    newStatus = "ORDER_CANCELLED";
                }
            } else if (allComplete) {
                //newStatus = "ORDER_COMPLETED";
            	
            	/*
            	 * @Ajaya For making the order packed after packing
            	 */
            	newStatus = "ORDER_PACKED";
            } else if (allApproved) {
                boolean changeToApprove = true;

                // NOTE DEJ20070805 I'm not sure why we would want to auto-approve the header... adding at least this one exeption so that we don't have to add processing, held, etc statuses to the item status list
                // NOTE2 related to the above: appears this was a weird way to set the order header status by setting all order item statuses... changing that to be less weird and more direct
                // this is a bit of a pain: if the current statusId = ProductStore.headerApprovedStatus and we don't have that status in the history then we don't want to change it on approving the items
                if (UtilValidate.isNotEmpty(orderHeader.getString("productStoreId"))) {
                    try {
                        GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", orderHeader.getString("productStoreId")));
                        if (productStore != null) {
                            String headerApprovedStatus = productStore.getString("headerApprovedStatus");
                            if (UtilValidate.isNotEmpty(headerApprovedStatus)) {
                                if (headerApprovedStatus.equals(orderHeaderStatusId)) {
                                    Map<String, Object> orderStatusCheckMap = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", headerApprovedStatus, "orderItemSeqId", null);
                                    List<GenericValue> orderStatusList = delegator.findByAnd("OrderStatus", orderStatusCheckMap);
                                    // should be 1 in the history, but just in case accept 0 too
                                    if (orderStatusList.size() <= 1) {
                                        changeToApprove = false;
                                    }
                                }
                            }
                        }
                    } catch (GenericEntityException e) {
                        String errMsg = "Database error checking if we should change order header status to approved: " + e.toString();
                        Debug.logError(e, errMsg, module);
                        return ServiceUtil.returnError(errMsg);
                    }
                }

                if ("ORDER_SENT".equals(orderHeaderStatusId)) changeToApprove = false;
                if ("ORDER_COMPLETED".equals(orderHeaderStatusId)) {
                    if ("SALES_ORDER".equals(orderTypeId)) {
                        changeToApprove = false;
                    }
                }
                if ("ORDER_CANCELLED".equals(orderHeaderStatusId)) changeToApprove = false;

                if (changeToApprove) {
                    newStatus = "ORDER_APPROVED";
                }
            }

            // now set the new order status
            if (newStatus != null && !newStatus.equals(orderHeaderStatusId)) {
                Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", newStatus, "userLogin", userLogin);
                Map<String, Object> newSttsResult = null;
                try {
                    newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                }
                if (ServiceUtil.isError(newSttsResult)) {
                    return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                }
                if("ORDER_PACKED".equals(newStatus))
                {
                	serviceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ORDER_DISPATCHED", "userLogin", userLogin);
                    newSttsResult = null;
                    try {
                        newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                    }
                    if (ServiceUtil.isError(newSttsResult)) {
                        return ServiceUtil.returnError(ServiceUtil.getErrorMessage(newSttsResult));
                    }
                }
                
            }
        } else {
            Debug.logWarning(UtilProperties.getMessage(resource_error,
                    "OrderReceivedNullForOrderItemRecordsOrderId", UtilMisc.toMap("orderId",orderId),locale), module);
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service to cancel an order item quantity */
    public static Map<String, Object> cancelOrderItem(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        BigDecimal cancelQuantity = (BigDecimal) context.get("cancelQuantity");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
        Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));

        // debugging message info
        String itemMsgInfo = orderId + " / " + orderItemSeqId + " / " + shipGroupSeqId;

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();

        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId);
        if (orderItemSeqId != null) {
            fields.put("orderItemSeqId", orderItemSeqId);
        }
        if (shipGroupSeqId != null) {
            fields.put("shipGroupSeqId", shipGroupSeqId);
        }

        List<GenericValue> orderItemShipGroupAssocs = null;
        try {
            orderItemShipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", fields);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotGetOrderItemAssocEntity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
        }

        if (orderItemShipGroupAssocs != null) {
            Iterator<GenericValue> i = orderItemShipGroupAssocs.iterator();
            while (i.hasNext()) {
                GenericValue orderItemShipGroupAssoc = i.next();
                GenericValue orderItem = null;
                try {
                    orderItem = orderItemShipGroupAssoc.getRelatedOne("OrderItem");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }

                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                }

                BigDecimal aisgaCancelQuantity =  orderItemShipGroupAssoc.getBigDecimal("cancelQuantity");
                if (aisgaCancelQuantity == null) {
                    aisgaCancelQuantity = BigDecimal.ZERO;
                }
                BigDecimal availableQuantity = orderItemShipGroupAssoc.getBigDecimal("quantity").subtract(aisgaCancelQuantity);

                BigDecimal itemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                if (itemCancelQuantity == null) {
                    itemCancelQuantity = BigDecimal.ZERO;
                }
                BigDecimal itemQuantity = orderItem.getBigDecimal("quantity").subtract(itemCancelQuantity);
                if (availableQuantity == null) availableQuantity = BigDecimal.ZERO;
                if (itemQuantity == null) itemQuantity = BigDecimal.ZERO;

                BigDecimal thisCancelQty = null;
                if (cancelQuantity != null) {
                    thisCancelQty = cancelQuantity;
                } else {
                    thisCancelQty = availableQuantity;
                }

                if (availableQuantity.compareTo(thisCancelQty) >= 0) {
                    if (availableQuantity.compareTo(BigDecimal.ZERO) == 0) {
                        continue;  //OrderItemShipGroupAssoc already cancelled
                    }
                    orderItem.set("cancelQuantity", itemCancelQuantity.add(thisCancelQty));
                    orderItemShipGroupAssoc.set("cancelQuantity", aisgaCancelQuantity.add(thisCancelQty));

                    try {
                        List<GenericValue> toStore = UtilMisc.toList(orderItem, orderItemShipGroupAssoc);
                        delegator.storeAll(toStore);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderUnableToSetCancelQuantity", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                    }

                    //  create order item change record
                    if (!"Y".equals(orderItem.getString("isPromo"))) {
                        String reasonEnumId = null;
                        String changeComments = null;
                        if (UtilValidate.isNotEmpty(itemReasonMap)) {
                            reasonEnumId = itemReasonMap.get(orderItem.getString("orderItemSeqId"));
                        }
                        if (UtilValidate.isNotEmpty(itemCommentMap)) {
                            changeComments = itemCommentMap.get(orderItem.getString("orderItemSeqId"));
                        }

                        //Map<String, Object> serviceCtx = FastMap.newInstance();
                        Map<String, Object> serviceCtx = new HashMap<String, Object>();
                        
                        serviceCtx.put("orderId", orderItem.getString("orderId"));
                        serviceCtx.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                        serviceCtx.put("cancelQuantity", thisCancelQty);
                        serviceCtx.put("changeTypeEnumId", "ODR_ITM_CANCEL");
                        serviceCtx.put("reasonEnumId", reasonEnumId);
                        serviceCtx.put("changeComments", changeComments);
                        serviceCtx.put("userLogin", userLogin);
                        Map<String, Object> resp = null;
                        try {
                            resp = dispatcher.runSync("createOrderItemChange", serviceCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(e.getMessage());
                        }
                        if (ServiceUtil.isError(resp)) {
                            return ServiceUtil.returnError((String)resp.get(ModelService.ERROR_MESSAGE));
                        }
                    }

                    // log an order note
                    try {
                        BigDecimal quantity = thisCancelQty.setScale(1, orderRounding);
                        String cancelledItemToOrder = UtilProperties.getMessage(resource, "OrderCancelledItemToOrder", locale);
                        dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", cancelledItemToOrder +
                                orderItem.getString("productId") + " (" + quantity + ")", "internalNote", "Y", "userLogin", userLogin));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                    }

                    if (thisCancelQty.compareTo(itemQuantity) >= 0) {
                        // all items are cancelled -- mark the item as cancelled
                        Map<String, Object> statusCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId"), "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderUnableToCancelOrderLine", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    } else {
                        // reverse the inventory reservation
                        Map<String, Object> invCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItem.getString("orderItemSeqId"), "shipGroupSeqId",
                                shipGroupSeqId, "cancelQuantity", thisCancelQty, "userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("cancelOrderItemInvResQty", invCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderUnableToUpdateInventoryReservations", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
                        }
                    }
                } else {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderInvalidCancelQuantityCannotCancel", UtilMisc.toMap("thisCancelQty",thisCancelQty), locale));
                }
            }
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotCancelItemItemNotFound", UtilMisc.toMap("itemMsgInfo",itemMsgInfo), locale));
        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for changing the status on order item(s) */
    public static Map<String, Object> setItemStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String fromStatusId = (String) context.get("fromStatusId");
        String statusId = (String) context.get("statusId");
        Timestamp statusDateTime = (Timestamp) context.get("statusDateTime");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }

        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId);
        if (orderItemSeqId != null)
            fields.put("orderItemSeqId", orderItemSeqId);
        if (fromStatusId != null)
            fields.put("statusId", fromStatusId);

        List<GenericValue> orderItems = null;
        try {
            orderItems = delegator.findByAnd("OrderItem", fields);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCannotGetOrderItemEntity",locale) + e.getMessage());
        }

        if (UtilValidate.isNotEmpty(orderItems)) {
            List<GenericValue> toBeStored = new ArrayList<GenericValue>();
            Iterator<GenericValue> itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = itemsIterator.next();
                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotChangeItemStatusItemNotFound", locale));
                }
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : Status Change: [" + orderId + "] (" + orderItem.getString("orderItemSeqId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setItemStatus] : From Status : " + orderItem.getString("statusId"), module);
                if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

                if (orderItem.getString("statusId").equals(statusId)) {
                    continue;
                }

                try {
                    Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", orderItem.getString("statusId"), "statusIdTo", statusId);
                    GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

                    if (statusChange == null) {
                        Debug.logWarning(UtilProperties.getMessage(resource_error,
                                "OrderItemStatusNotChangedIsNotAValidChange", UtilMisc.toMap("orderStatusId",orderItem.getString("statusId"),"statusId",statusId), locale), module);
                        continue;
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCouldNotChangeItemStatus",locale) + e.getMessage());
                }

                orderItem.set("statusId", statusId);
                toBeStored.add(orderItem);
                if (statusDateTime == null) {
                    statusDateTime = UtilDateTime.nowTimestamp();
                }
                // now create a status change
                Map<String, Object> changeFields = new HashMap<String, Object>();
                changeFields.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
                changeFields.put("statusId", statusId);
                changeFields.put("orderId", orderId);
                changeFields.put("orderItemSeqId", orderItem.getString("orderItemSeqId"));
                changeFields.put("statusDatetime", statusDateTime);
                changeFields.put("statusUserLogin", userLogin.getString("userLoginId"));
                GenericValue orderStatus = delegator.makeValue("OrderStatus", changeFields);
                toBeStored.add(orderStatus);
            }

            // store the changes
            if (toBeStored.size() > 0) {
                try {
                    delegator.storeAll(toBeStored);
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotStoreStatusChanges", locale) + e.getMessage());
                }
            }

        }

        return ServiceUtil.returnSuccess();
    }

    /** Service for changing the status on an order header */
    public static Map<String, Object> setOrderStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        successResult.put("orderId", orderId);
        Security security = ctx.getSecurity();
        boolean hasPermission = OrderServices.hasPermission(orderId, userLogin, "UPDATE", security, delegator);
        if (!hasPermission) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderYouDoNotHavePermissionToChangeThisOrdersStatus",locale));
        }
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound", locale));
            }
            // first save off the old status
            successResult.put("oldStatusId", orderHeader.get("statusId"));
            successResult.put("orderTypeId", orderHeader.get("orderTypeId"));
            successResult.put("pinId", orderHeader.get("pinId"));
            successResult.put("orderId", orderId);

            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : From Status : " + orderHeader.getString("statusId"), module);
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderStatus] : To Status : " + statusId, module);

            if (orderHeader.getString("statusId").equals(statusId)) {
                Debug.logWarning(UtilProperties.getMessage(resource_error,
                        "OrderTriedToSetOrderStatusWithTheSameStatusIdforOrderWithId", UtilMisc.toMap("statusId",statusId,"orderId",orderId),locale),module);
                return successResult;
            }
            try {
                Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", orderHeader.getString("statusId"), "statusIdTo", statusId);
                GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);
                if (statusChange == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                            "OrderErrorCouldNotChangeOrderStatusStatusIsNotAValidChange", locale) + ": [" + statusFields.get("statusId") + "] -> [" + statusFields.get("statusIdTo") + "]");
                }
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
            }

            // update the current status
            orderHeader.set("statusId", statusId);
            orderHeader.set("fullFillDate", UtilDateTime.nowTimestamp());

            // now create a status change
            GenericValue orderStatus = delegator.makeValue("OrderStatus");
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.put("statusUserLogin", userLogin.getString("userLoginId"));

            orderHeader.store();
            orderStatus.create();
          
            successResult.put("needsInventoryIssuance", orderHeader.get("needsInventoryIssuance"));
            successResult.put("grandTotal", orderHeader.get("grandTotal"));
            //Debug.logInfo("For setOrderStatus orderHeader is " + orderHeader, module);
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotChangeOrderStatus",locale) + e.getMessage() + ").");
        }

        // release the inital hold if we are cancelled or approved
        if ("ORDER_CANCELLED".equals(statusId) || "ORDER_APPROVED".equals(statusId)) {
            OrderChangeHelper.releaseInitialOrderHold(ctx.getDispatcher(), orderId);

            // cancel any order processing if we are cancelled
            if ("ORDER_CANCELLED".equals(statusId)) {
                OrderChangeHelper.abortOrderProcessing(ctx.getDispatcher(), orderId);
            }
        }

        if ("Y".equals(context.get("setItemStatus"))) {
            String newItemStatusId = null;
            if ("ORDER_APPROVED".equals(statusId)) {
                newItemStatusId = "ITEM_APPROVED";
            } else if ("ORDER_COMPLETED".equals(statusId)) {
                newItemStatusId = "ITEM_COMPLETED";
            } else if ("ORDER_CANCELLED".equals(statusId)) {
                newItemStatusId = "ITEM_CANCELLED";
            }

            if (newItemStatusId != null) {
                try {
                    Map<String, Object> resp = dispatcher.runSync("changeOrderItemStatus", UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", newItemStatusId, "userLogin", userLogin));
                    if (ServiceUtil.isError(resp)) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId, null, null, resp);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Error changing item status to " + newItemStatusId + ": " + e.toString(), module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCouldNotChangeItemStatus", locale) + newItemStatusId + ": " + e.toString());
                }
            }
        }
        successResult.put("orderStatusId", statusId);
        
        if(UtilValidate.isNotEmpty(orderHeader) && "ORDER_DISPATCHED".equals(orderHeader.get("statusId")))
        {
        	fulfillGiftVoucher(delegator, dispatcher, orderId);
        }
        
        //Debug.logInfo("For setOrderStatus successResult is " + successResult, module);
        return successResult;
    }

    /** Service to update the order tracking number */
    public static Map<String, Object> updateTrackingNumber(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String trackingNumber = (String) context.get("trackingNumber");
        //Locale locale = (Locale) context.get("locale");

        try {
            GenericValue shipGroup = delegator.findByPrimaryKey("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId));

            if (shipGroup == null) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: No order shipment preference found!");
            } else {
                shipGroup.set("trackingNumber", trackingNumber);
                shipGroup.store();
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not set tracking number (" + e.getMessage() + ").");
        }
        return result;
    }

    /** Service to add a role type to an order */
    public static Map<String, Object> addRoleType(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        Boolean removeOld = (Boolean) context.get("removeOld");
        //Locale locale = (Locale) context.get("locale");

        if (removeOld != null && removeOld.booleanValue()) {
            try {
                delegator.removeByAnd("OrderRole", UtilMisc.toMap("orderId", orderId, "roleTypeId", roleTypeId));
            } catch (GenericEntityException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not remove old roles (" + e.getMessage() + ").");
                return result;
            }
        }

        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleTypeId);

        try {
            // first check and see if we are already there; if so, just return success
            GenericValue testValue = delegator.findByPrimaryKey("OrderRole", fields);
            if (testValue != null) {
                ServiceUtil.returnSuccess();
            } else {
                GenericValue value = delegator.makeValue("OrderRole", fields);
                delegator.create(value);
            }
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not add role to order (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service to remove a role type from an order */
    public static Map<String, Object> removeRoleType(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("partyId");
        String roleTypeId = (String) context.get("roleTypeId");
        Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "partyId", partyId, "roleTypeId", roleTypeId);
        //Locale locale = (Locale) context.get("locale");

        GenericValue testValue = null;

        try {
            testValue = delegator.findByPrimaryKey("OrderRole", fields);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not add role to order (" + e.getMessage() + ").");
            return result;
        }

        if (testValue == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            return result;
        }

        try {
            GenericValue value = delegator.findByPrimaryKey("OrderRole", fields);

            value.remove();
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "ERROR: Could not remove role from order (" + e.getMessage() + ").");
            return result;
        }
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service to email a customer with initial order confirmation */
    public static Map<String, Object> sendOrderConfirmNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_CONFIRM");
    }

    /** Service to email a customer with order changes */
    public static Map<String, Object> sendOrderCompleteNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_COMPLETE");
    }

    /** Service to email a customer with order changes */
    public static Map<String, Object> sendOrderBackorderNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_BACKORDER");
    }

    /** Service to email a customer with order changes */
    public static Map<String, Object> sendOrderChangeNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_CHANGE");
    }

    /** Service to email a customer with order payment retry results */
    public static Map<String, Object> sendOrderPayRetryNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        return sendOrderNotificationScreen(ctx, context, "PRDS_ODR_PAYRETRY");
    }

    protected static Map<String, Object> sendOrderNotificationScreen(DispatchContext dctx, Map<String, ? extends Object> context, String emailType) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        String sendTo = (String) context.get("sendTo");
        String sendCc = (String) context.get("sendCc");
        String note = (String) context.get("note");
        String screenUri = (String) context.get("screenUri");
        GenericValue temporaryAnonymousUserLogin = (GenericValue) context.get("temporaryAnonymousUserLogin");
        Locale localePar = (Locale) context.get("locale");
        if (userLogin == null) {
            // this may happen during anonymous checkout, try to the special case user
            userLogin = temporaryAnonymousUserLogin;
        }

        // prepare the order information
        //Map<String, Object> sendMap = FastMap.newInstance();
        Map<String, Object> sendMap = new HashMap<String, Object>();
        

        // get the order header and store
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting OrderHeader", module);
        }

        if (orderHeader == null) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                    "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), localePar));
        }

        if (orderHeader.get("webSiteId") == null) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                    "OrderOrderWithoutWebSite", UtilMisc.toMap("orderId", orderId), localePar));
        }

        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", orderHeader.get("productStoreId"), "emailType", emailType));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting the ProductStoreEmailSetting for productStoreId=" + orderHeader.get("productStoreId") + " and emailType=" + emailType, module);
        }
        if (productStoreEmail == null) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resourceProduct, 
                    "ProductProductStoreEmailSettingsNotValid", 
                    UtilMisc.toMap("productStoreId", orderHeader.get("productStoreId"), 
                            "emailType", emailType), localePar));
        }

        // the override screenUri
        if (UtilValidate.isEmpty(screenUri)) {
            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
            }
            sendMap.put("bodyScreenUri", bodyScreenLocation);
            String xslfoAttachScreenLocation = productStoreEmail.getString("xslfoAttachScreenLocation");
            sendMap.put("xslfoAttachScreenLocation", xslfoAttachScreenLocation);
        } else {
            sendMap.put("bodyScreenUri", screenUri);
        }

        // website
        sendMap.put("webSiteId", orderHeader.get("webSiteId"));

        OrderReadHelper orh = new OrderReadHelper(orderHeader);
        String emailString = orh.getOrderEmailString();
        if (UtilValidate.isEmpty(emailString)) {
            Debug.logInfo("Customer is not setup to receive emails; no address(s) found [" + orderId + "]", module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                    "OrderOrderWithoutEmailAddress", UtilMisc.toMap("orderId", orderId), localePar));
        }

        // where to get the locale... from PLACING_CUSTOMER's UserLogin.lastLocale,
        // or if not available then from ProductStore.defaultLocaleString
        // or if not available then the system Locale
        Locale locale = null;
        GenericValue placingParty = orh.getPlacingParty();
        GenericValue placingUserLogin = placingParty == null ? null : PartyWorker.findPartyLatestUserLogin(placingParty.getString("partyId"), delegator);
        if (locale == null && placingParty != null) {
            locale = PartyWorker.findPartyLastLocale(placingParty.getString("partyId"), delegator);
        }

        // for anonymous orders, use the temporaryAnonymousUserLogin as the placingUserLogin will be null
        if (placingUserLogin == null) {
            placingUserLogin = temporaryAnonymousUserLogin;
        }

        GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(orderHeader);
        if (locale == null && productStore != null) {
            String localeString = productStore.getString("defaultLocaleString");
            if (UtilValidate.isNotEmpty(localeString)) {
                locale = UtilMisc.parseLocale(localeString);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }

        Map<String, Object> bodyParameters = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "userLogin", placingUserLogin, "locale", locale);
        if (placingParty!= null) {
            bodyParameters.put("partyId", placingParty.get("partyId"));
        }
        bodyParameters.put("note", note);
        sendMap.put("bodyParameters", bodyParameters);
        sendMap.put("userLogin",userLogin);

        String subjectString = productStoreEmail.getString("subject");
        sendMap.put("subject", subjectString);

        sendMap.put("contentType", productStoreEmail.get("contentType"));
        sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
        sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
        sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));
        if ((sendTo != null) && UtilValidate.isEmail(sendTo)) {
            sendMap.put("sendTo", sendTo);
        } else {
            sendMap.put("sendTo", emailString);
        }
        if ((sendCc != null) && UtilValidate.isEmail(sendCc)) {
            sendMap.put("sendCc", sendCc);
        } else {
            sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
        }

        // send the notification
        Map<String, Object> sendResp = null;
        try {
            sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                    "OrderServiceExceptionSeeLogs",locale));
        }

        // check for errors
        if (sendResp != null && !ServiceUtil.isError(sendResp)) {
            sendResp.put("emailType", emailType);
        }
        if (UtilValidate.isNotEmpty(orderId)) {
            sendResp.put("orderId", orderId);
        }
        return sendResp;
    }

    /** Service to email order notifications for pending actions */
    public static Map<String, Object> sendProcessNotification(DispatchContext ctx, Map<String, ? extends Object> context) {
        //appears to not be used: Map result = new HashMap();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        String adminEmailList = (String) context.get("adminEmailList");
        String assignedToUser = (String) context.get("assignedPartyId");
        //appears to not be used: String assignedToRole = (String) context.get("assignedRoleTypeId");
        String workEffortId = (String) context.get("workEffortId");
        Locale locale = (Locale) context.get("locale");

        GenericValue workEffort = null;
        GenericValue orderHeader = null;
        //appears to not be used: String assignedEmail = null;

        // get the order/workflow info
        try {
            workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));
            String sourceReferenceId = workEffort.getString("sourceReferenceId");
            if (sourceReferenceId != null)
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", sourceReferenceId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderProblemWithEntityLookup", locale));
        }

        // find the assigned user's email address(s)
        GenericValue party = null;
        Collection<GenericValue> assignedToEmails = null;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", assignedToUser));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, 
                    "OrderProblemWithEntityLookup", locale));
        }
        if (party != null) {
            assignedToEmails = ContactHelper.getContactMechByPurpose(party, "PRIMARY_EMAIL", false);
        }

        Map<String, Object> templateData = new HashMap<String, Object>(context);
        templateData.putAll(orderHeader);
        templateData.putAll(workEffort);

        /* NOTE DEJ20080609 commenting out this code because the old OFBiz Workflow Engine is being deprecated and this was only for that
        String omgStatusId = WfUtil.getOMGStatus(workEffort.getString("currentStatusId"));
        templateData.put("omgStatusId", omgStatusId);
        */
        templateData.put("omgStatusId", workEffort.getString("currentStatusId"));

        // get the assignments
        List<GenericValue> assignments = null;
        if (workEffort != null) {
            try {
                assignments = workEffort.getRelated("WorkEffortPartyAssignment");
            } catch (GenericEntityException e1) {
                Debug.logError(e1, "Problems getting assignements", module);
            }
        }
        templateData.put("assignments", assignments);

        StringBuilder emailList = new StringBuilder();
        if (assignedToEmails != null) {
            Iterator<GenericValue> aei = assignedToEmails.iterator();
            while (aei.hasNext()) {
                GenericValue ct = aei.next();
                if (ct != null && ct.get("infoString") != null) {
                    if (emailList.length() > 1)
                        emailList.append(",");
                    emailList.append(ct.getString("infoString"));
                }
            }
        }
        if (adminEmailList != null) {
            if (emailList.length() > 1)
                emailList.append(",");
            emailList.append(adminEmailList);
        }

        // prepare the mail info
        String ofbizHome = System.getProperty("ofbiz.home");
        String templateName = ofbizHome + "/applications/order/email/default/emailprocessnotify.ftl";

        Map<String, Object> sendMailContext = new HashMap<String, Object>();
        sendMailContext.put("sendTo", emailList.toString());
        sendMailContext.put("sendFrom", "workflow@ofbiz.org"); // fixme
        sendMailContext.put("subject", "Workflow Notification");
        sendMailContext.put("templateName", templateName);
        sendMailContext.put("templateData", templateData);
        

        try {
            dispatcher.runAsync("sendGenericNotificationEmail", sendMailContext);
        } catch (GenericServiceException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderSendMailServiceFailed", locale) + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    /** Service to create an order payment preference */
    public static Map<String, Object> createPaymentPreference(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = ctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String statusId = (String) context.get("statusId");
        String paymentMethodTypeId = (String) context.get("paymentMethodTypeId");
        String paymentMethodId = (String) context.get("paymentMethodId");
        BigDecimal maxAmount = (BigDecimal) context.get("maxAmount");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String prefId = null;

        try {
            prefId = delegator.getNextSeqId("OrderPaymentPreference");
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotCreateOrderPaymentPreferenceIdGenerationFailure", locale));
        }

        Map<String, Object> fields = UtilMisc.<String, Object>toMap("orderPaymentPreferenceId", prefId, "orderId", orderId, "paymentMethodTypeId",
                paymentMethodTypeId, "paymentMethodId", paymentMethodId, "maxAmount", maxAmount);

        if (statusId != null) {
            fields.put("statusId", statusId);
        }

        try {
            GenericValue v = delegator.makeValue("OrderPaymentPreference", fields);
            v.set("createdDate", UtilDateTime.nowTimestamp());
            if (userLogin != null) {
                v.set("createdByUserLogin", userLogin.getString("userLoginId"));
            }
            delegator.create(v);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, 
                    "OrderOrderPaymentPreferencesCannotBeCreated", UtilMisc.toMap("errorString", e.getMessage()), locale));
            return ServiceUtil.returnFailure();
        }
        result.put("orderPaymentPreferenceId", prefId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /** Service to get order header information as standard results. */
    public static Map<String, Object> getOrderHeaderInformation(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order header detial", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderCannotGetOrderHeader", locale) + e.getMessage());
        }
        if (orderHeader != null) {
            Map<String, Object> result = ServiceUtil.returnSuccess();
            result.putAll(orderHeader);
            return result;
        }
        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                "OrderErrorGettingOrderHeaderInformationNull", locale));
    }

    /** Service to get the total shipping for an order. */
    public static Map<String, Object> getOrderShippingAmount(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotGetOrderInformation", locale) + e.getMessage() + ").");
        }

        Map<String, Object> result = null;
        if (orderHeader != null) {
            OrderReadHelper orh = new OrderReadHelper(orderHeader);
            List<GenericValue> orderItems = orh.getValidOrderItems();
            List<GenericValue> orderAdjustments = orh.getAdjustments();
            List<GenericValue> orderHeaderAdjustments = orh.getOrderHeaderAdjustments();
            BigDecimal orderSubTotal = orh.getOrderItemsSubTotal();

            BigDecimal shippingAmount = OrderReadHelper.getAllOrderItemsAdjustmentsTotal(orderItems, orderAdjustments, false, false, true);
            shippingAmount = shippingAmount.add(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

            result = ServiceUtil.returnSuccess();
            result.put("shippingAmount", shippingAmount);
        } else {
            result = ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                      "OrderUnableToFindOrderHeaderCannotGetShippingAmount", locale));
        }
        return result;
    }

    /** Service to get an order contact mech. */
    public static Map<String, Object> getOrderAddress(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");        
        //appears to not be used: GenericValue v = null;
        String purpose[] = { "BILLING_LOCATION", "SHIPPING_LOCATION" };
        String outKey[] = { "billingAddress", "shippingAddress" };
        GenericValue orderHeader = null;
        //Locale locale = (Locale) context.get("locale");

        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if (orderHeader != null)
                result.put("orderHeader", orderHeader);
        } catch (GenericEntityException e) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, 
                    "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            return result;
        }
        if (orderHeader == null) {
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, 
                    "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            return result;
        }
        for (int i = 0; i < purpose.length; i++) {
            try {
                GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech",
                            UtilMisc.toMap("contactMechPurposeTypeId", purpose[i])));
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");

                if (contactMech != null) {
                    result.put(outKey[i], contactMech.getRelatedOne("PostalAddress"));
                }
            } catch (GenericEntityException e) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource, 
                        "OrderOrderContachMechNotFound", UtilMisc.toMap("errorString", e.getMessage()), locale));
                return result;
            }
        }

        result.put("orderId", orderId);
        return result;
    }

    /** Service to create a order header note. */
    public static Map<String, Object> createOrderNote(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String noteString = (String) context.get("note");
        String noteName = (String) context.get("noteName");
        String orderId = (String) context.get("orderId");
        String internalNote = (String) context.get("internalNote");
        Map<String, Object> noteCtx = UtilMisc.<String, Object>toMap("note", noteString, "userLogin", userLogin, "noteName", noteName);
        Locale locale = (Locale) context.get("locale");

        try {
            // Store the note.
            Map<String, Object> noteRes = dispatcher.runSync("createNote", noteCtx);

            if (ServiceUtil.isError(noteRes))
                return noteRes;

            String noteId = (String) noteRes.get("noteId");

            if (UtilValidate.isEmpty(noteId)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderProblemCreatingTheNoteNoNoteIdReturned", locale));
            }

            // Set the order info
            Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "noteId", noteId, "internalNote", internalNote);
            GenericValue v = delegator.makeValue("OrderHeaderNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", ee.getMessage()), locale));
        } catch (GenericServiceException se) {
            Debug.logError(se, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderOrderNoteCannotBeCreated", UtilMisc.toMap("errorString", se.getMessage()), locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> allowOrderSplit(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        Locale locale = (Locale) context.get("locale");

        // check and make sure we have permission to change the order
        Security security = ctx.getSecurity();
        if (!security.hasEntityPermission("ORDERMGR", "_UPDATE", userLogin)) {
            GenericValue placingCustomer = null;
            try {
                Map<String, Object> placingCustomerFields = UtilMisc.<String, Object>toMap("orderId", orderId, "partyId", userLogin.getString("partyId"), "roleTypeId", "PLACING_CUSTOMER");
                placingCustomer = delegator.findByPrimaryKey("OrderRole", placingCustomerFields);
            } catch (GenericEntityException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCannotGetOrderRoleEntity", locale) + e.getMessage());
            }
            if (placingCustomer == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderYouDoNotHavePermissionToChangeThisOrdersStatus", locale));
            }
        }

        GenericValue shipGroup = null;
        try {
            Map<String, String> fields = UtilMisc.<String, String>toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId);
            shipGroup = delegator.findByPrimaryKey("OrderItemShipGroup", fields);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting OrderItemShipGroup for : " + orderId + " / " + shipGroupSeqId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderCannotUpdateProblemGettingOrderShipmentPreference", locale));
        }

        if (shipGroup != null) {
            shipGroup.set("maySplit", "Y");
            try {
                shipGroup.store();
            } catch (GenericEntityException e) {
                Debug.logError("Problem saving OrderItemShipGroup for : " + orderId + " / " + shipGroupSeqId, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderCannotUpdateProblemSettingOrderShipmentPreference", locale));
            }
        } else {
            Debug.logError("ERROR: Got a NULL OrderItemShipGroup", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderCannotUpdateNoAvailableGroupsToChange", locale));
        }
        return ServiceUtil.returnSuccess();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> cancelFlaggedSalesOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        //Locale locale = (Locale) context.get("locale");

        List<GenericValue> ordersToCheck = null;

        // create the query expressions
        List<EntityExpr> exprs = UtilMisc.toList(
                EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED")
       );
        EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(exprs, EntityOperator.AND);

        // get the orders
        try {
            ordersToCheck = delegator.findList("OrderHeader", ecl, null, UtilMisc.toList("orderDate"), null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order headers", module);
        }

        if (UtilValidate.isEmpty(ordersToCheck)) {
            Debug.logInfo("No orders to check, finished", module);
            return ServiceUtil.returnSuccess();
        }

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Iterator<GenericValue> i = ordersToCheck.iterator();
        while (i.hasNext()) {
            GenericValue orderHeader = i.next();
            String orderId = orderHeader.getString("orderId");
            String orderStatus = orderHeader.getString("statusId");

            if (orderStatus.equals("ORDER_CREATED")) {
                // first check for un-paid orders
                Timestamp orderDate = orderHeader.getTimestamp("entryDate");

                // need the store for the order
                GenericValue productStore = null;
                try {
                    productStore = orderHeader.getRelatedOne("ProductStore");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get ProductStore from OrderHeader", module);
                }

                // default days to cancel
                int daysTillCancel = 30;

                // get the value from the store
                if (productStore != null && productStore.get("daysToCancelNonPay") != null) {
                    daysTillCancel = productStore.getLong("daysToCancelNonPay").intValue();
                }

                if (daysTillCancel > 0) {
                    // 0 days means do not auto-cancel
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(orderDate.getTime());
                    cal.add(Calendar.DAY_OF_YEAR, daysTillCancel);
                    Date cancelDate = cal.getTime();
                    Date nowDate = new Date();
                    //Debug.log("Cancel Date : " + cancelDate, module);
                    //Debug.log("Current Date : " + nowDate, module);
                    if (cancelDate.equals(nowDate) || nowDate.after(cancelDate)) {
                        // cancel the order item(s)
                        Map<String, Object> svcCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                        try {
                            // TODO: looks like result is ignored here, but we should be looking for errors
                            dispatcher.runSync("changeOrderItemStatus", svcCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "Problem calling change item status service : " + svcCtx, module);
                        }
                    }
                }
            } else {
                // check for auto-cancel items
                List itemsExprs = new ArrayList();

                // create the query expressions
                itemsExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
                itemsExprs.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_CREATED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED")), EntityOperator.OR));
                itemsExprs.add(EntityCondition.makeCondition("dontCancelSetUserLogin", EntityOperator.EQUALS, GenericEntity.NULL_FIELD));
                itemsExprs.add(EntityCondition.makeCondition("dontCancelSetDate", EntityOperator.EQUALS, GenericEntity.NULL_FIELD));
                itemsExprs.add(EntityCondition.makeCondition("autoCancelDate", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD));

                ecl = EntityCondition.makeCondition(itemsExprs);

                List<GenericValue> orderItems = null;
                try {
                    orderItems = delegator.findList("OrderItem", ecl, null, null, null, false);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting order item records", module);
                }
                if (UtilValidate.isNotEmpty(orderItems)) {
                    Iterator<GenericValue> oii = orderItems.iterator();
                    while (oii.hasNext()) {
                        GenericValue orderItem = oii.next();
                        String orderItemSeqId = orderItem.getString("orderItemSeqId");
                        Timestamp autoCancelDate = orderItem.getTimestamp("autoCancelDate");

                        if (autoCancelDate != null) {
                            if (nowTimestamp.equals(autoCancelDate) || nowTimestamp.after(autoCancelDate)) {
                                // cancel the order item
                                Map<String, Object> svcCtx = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "statusId", "ITEM_CANCELLED", "userLogin", userLogin);
                                try {
                                    // TODO: check service result for an error return
                                    dispatcher.runSync("changeOrderItemStatus", svcCtx);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, "Problem calling change item status service : " + svcCtx, module);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> checkDigitalItemFulfillment(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        // need the order header
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "ERROR: Unable to get OrderHeader for orderId : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorUnableToGetOrderHeaderForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // get all the items for the order
        List<GenericValue> orderItems = null;
        if (orderHeader != null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get OrderItem list for orderId : " + orderId, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorUnableToGetOrderItemListForOrderId", UtilMisc.toMap("orderId",orderId), locale));
            }
        }

        // find any digital or non-product items
        List<GenericValue> nonProductItems = new ArrayList<GenericValue>();
        List<GenericValue> digitalItems = new ArrayList<GenericValue>();
        Map<GenericValue, GenericValue> digitalProducts = new HashMap<GenericValue, GenericValue>();

        if (UtilValidate.isNotEmpty(orderItems)) {
            Iterator<GenericValue> i = orderItems.iterator();
            while (i.hasNext()) {
                GenericValue item = i.next();
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to get Product from OrderItem", module);
                }
                if (product != null) {
                    GenericValue productType = null;
                    try {
                        productType = product.getRelatedOne("ProductType");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "ERROR: Unable to get ProductType from Product", module);
                    }

                    if (productType != null) {
                        String isPhysical = productType.getString("isPhysical");
                        String isDigital = productType.getString("isDigital");

                        // check for digital and finished/digital goods
                        if (isDigital != null && "Y".equalsIgnoreCase(isDigital)) {
                            // we only invoice APPROVED items
                            if ("ITEM_APPROVED".equals(item.getString("statusId"))) {
                                digitalItems.add(item);
                            }
                            if (isPhysical == null || !"Y".equalsIgnoreCase(isPhysical)) {
                                // 100% digital goods need status change
                                digitalProducts.put(item, product);
                            }
                        }
                    }
                } else {
                    String itemType = item.getString("orderItemTypeId");
                    if (!"PRODUCT_ORDER_ITEM".equals(itemType)) {
                        nonProductItems.add(item);
                    }
                }
            }
        }

        // now process the digital items
        if (digitalItems.size() > 0 || nonProductItems.size() > 0) {
            GenericValue productStore = OrderReadHelper.getProductStoreFromOrder(dispatcher.getDelegator(), orderId);
            boolean invoiceItems = true;
            if (productStore != null && productStore.get("autoInvoiceDigitalItems") != null) {
                invoiceItems = "Y".equalsIgnoreCase(productStore.getString("autoInvoiceDigitalItems"));
            }

            // single list with all invoice items
            List<GenericValue> itemsToInvoice = FastList.newInstance();
            itemsToInvoice.addAll(nonProductItems);
            itemsToInvoice.addAll(digitalItems);

            if (invoiceItems) {
                // invoice all APPROVED digital/non-product goods

                // do something tricky here: run as a different user that can actually create an invoice, post transaction, etc
                Map<String, Object> invoiceResult = null;
                try {
                    GenericValue permUserLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
                    Map<String, Object> invoiceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "billItems", itemsToInvoice, "userLogin", permUserLogin);
                    invoiceResult = dispatcher.runSync("createInvoiceForOrder", invoiceContext);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to invoice digital items", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderProblemWithInvoiceCreationDigitalItemsNotFulfilled", locale));
                } catch (GenericServiceException e) {
                    Debug.logError(e, "ERROR: Unable to invoice digital items", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderProblemWithInvoiceCreationDigitalItemsNotFulfilled", locale));
                }
                if (ModelService.RESPOND_ERROR.equals(invoiceResult.get(ModelService.RESPONSE_MESSAGE))) {
                    return ServiceUtil.returnError((String) invoiceResult.get(ModelService.ERROR_MESSAGE));
                }

                // update the status of digital goods to COMPLETED; leave physical/digital as APPROVED for pick/ship
                Iterator<GenericValue> dii = itemsToInvoice.iterator();
                while (dii.hasNext()) {
                    GenericValue productType = null;
                    GenericValue item = dii.next();
                    GenericValue product = digitalProducts.get(item);
                    boolean markComplete = false;

                    if (product != null) {
                        try {
                            productType = product.getRelatedOne("ProductType");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "ERROR: Unable to get ProductType from Product", module);
                        }
                    } else {
                        String itemType = item.getString("orderItemTypeId");
                        if (!"PRODUCT_ORDER_ITEM".equals(itemType)) {
                            markComplete = true;
                        }
                    }

                    if (product != null && productType != null) {
                        String isPhysical = productType.getString("isPhysical");
                        String isDigital = productType.getString("isDigital");

                        // we were set as a digital good; one more check and change status
                        if ((isDigital != null && "Y".equalsIgnoreCase(isDigital)) &&
                                (isPhysical == null || !"Y".equalsIgnoreCase(isPhysical))) {
                            markComplete = true;
                        }
                    }

                    if (markComplete) {
                        Map<String, Object> statusCtx = new HashMap<String, Object>();
                        statusCtx.put("orderId", item.getString("orderId"));
                        statusCtx.put("orderItemSeqId", item.getString("orderItemSeqId"));
                        statusCtx.put("statusId", "ITEM_COMPLETED");
                        statusCtx.put("userLogin", userLogin);
                        try {
                            dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "ERROR: Problem setting the status to COMPLETED : " + item, module);
                        }
                    }
                }
            }

            // fulfill the digital goods
            Map<String, Object> fulfillContext = UtilMisc.<String, Object>toMap("orderId", orderId, "orderItems", digitalItems, "userLogin", userLogin);
            Map<String, Object> fulfillResult = null;
            try {
                // will be running in an isolated transaction to prevent rollbacks
                fulfillResult = dispatcher.runSync("fulfillDigitalItems", fulfillContext, 300, true);
            } catch (GenericServiceException e) {
                Debug.logError(e, "ERROR: Unable to fulfill digital items", module);
            }
            if (ModelService.RESPOND_ERROR.equals(fulfillResult.get(ModelService.RESPONSE_MESSAGE))) {
                // this service cannot return error at this point or we will roll back the invoice
                // since payments are already captured; errors should have been logged already.
                // the response message here will be passed as an error to the user.
                return ServiceUtil.returnSuccess((String)fulfillResult.get(ModelService.ERROR_MESSAGE));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> fulfillDigitalItems(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        //appears to not be used: String orderId = (String) context.get("orderId");
        List<GenericValue> orderItems = UtilGenerics.checkList(context.get("orderItems"));
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        if (UtilValidate.isNotEmpty(orderItems)) {
            // loop through the digital items to fulfill
            Iterator<GenericValue> itemsIterator = orderItems.iterator();
            while (itemsIterator.hasNext()) {
                GenericValue orderItem = itemsIterator.next();

                // make sure we have a valid item
                if (orderItem == null) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotCheckForFulfillmentItemNotFound", locale));
                }

                // locate the Product & ProductContent records
                GenericValue product = null;
                List<GenericValue> productContent = null;
                try {
                    product = orderItem.getRelatedOne("Product");
                    if (product == null) {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                "OrderErrorCannotCheckForFulfillmentProductNotFound", locale));
                    }

                    List<GenericValue> allProductContent = product.getRelated("ProductContent");

                    // try looking up the parent product if the product has no content and is a variant
                    if (UtilValidate.isEmpty(allProductContent) && ("Y".equals(product.getString("isVariant")))) {
                        GenericValue parentProduct = ProductWorker.getParentProduct(product.getString("productId"), delegator);
                        if (allProductContent == null) {
                            allProductContent = FastList.newInstance();
                        }
                        if (parentProduct != null) {
                            allProductContent.addAll(parentProduct.getRelated("ProductContent"));
                        }
                    }

                    if (UtilValidate.isNotEmpty(allProductContent)) {
                        // only keep ones with valid dates
                        productContent = EntityUtil.filterByDate(allProductContent, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
                        Debug.logInfo("Product has " + allProductContent.size() + " associations, " +
                                (productContent == null ? "0" : "" + productContent.size()) + " has valid from/thru dates", module);
                    }
                } catch (GenericEntityException e) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                            "OrderErrorCannotGetProductEntity", locale) + e.getMessage());
                }

                // now use the ProductContent to fulfill the item
                if (UtilValidate.isNotEmpty(productContent)) {
                    Iterator<GenericValue> prodcontentIterator = productContent.iterator();
                    while (prodcontentIterator.hasNext()) {
                        GenericValue productContentItem = prodcontentIterator.next();
                        GenericValue content = null;
                        try {
                            content = productContentItem.getRelatedOne("Content");
                        } catch (GenericEntityException e) {
                            Debug.logError(e,"ERROR: Cannot get Content entity: " + e.getMessage(),module);
                            continue;
                        }

                        String fulfillmentType = productContentItem.getString("productContentTypeId");
                        if ("FULFILLMENT_EXTASYNC".equals(fulfillmentType) || "FULFILLMENT_EXTSYNC".equals(fulfillmentType)) {
                            // enternal service fulfillment
                            String fulfillmentService = (String) content.get("serviceName");
                            if (fulfillmentService == null) {
                                Debug.logError("ProductContent of type FULFILLMENT_EXTERNAL had Content with empty serviceName, can not run fulfillment", module);
                            }
                            Map<String, Object> serviceCtx = UtilMisc.<String, Object>toMap("userLogin", userLogin, "orderItem", orderItem);
                            serviceCtx.putAll(productContentItem.getPrimaryKey());
                            try {
                                Debug.logInfo("Running external fulfillment '" + fulfillmentService + "'", module);
                                if ("FULFILLMENT_EXTASYNC".equals(fulfillmentType)) {
                                    dispatcher.runAsync(fulfillmentService, serviceCtx, true);
                                } else if ("FULFILLMENT_EXTSYNC".equals(fulfillmentType)) {
                                    Map<String, Object> resp = dispatcher.runSync(fulfillmentService, serviceCtx);
                                    if (ServiceUtil.isError(resp)) {
                                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                                "OrderOrderExternalFulfillmentError", locale), null, null, resp);
                                    }
                                }
                            } catch (GenericServiceException e) {
                                Debug.logError(e, "ERROR: Could not run external fulfillment service '" + fulfillmentService + "'; " + e.getMessage(), module);
                            }
                        } else if ("FULFILLMENT_EMAIL".equals(fulfillmentType)) {
                            // digital email fulfillment
                            // TODO: Add support for fulfillment email
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                                    "OrderEmailFulfillmentTypeNotYetImplemented", locale));
                        } else if ("DIGITAL_DOWNLOAD".equals(fulfillmentType)) {
                            // digital download fulfillment

                            // Nothing to do for here. Downloads are made available to the user
                            // though a query of OrderItems with related ProductContent.
                        } else {
                            Debug.logError("Invalid fulfillment type : " + fulfillmentType + " not supported.", module);
                        }
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    /** Service to invoice service items from order*/
    public static Map<String, Object> invoiceServiceItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        OrderReadHelper orh = null;
        try {
            orh = new OrderReadHelper(delegator, orderId);
        } catch (IllegalArgumentException e) {
            Debug.logError(e, "ERROR: Unable to get OrderHeader for orderId : " + orderId, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorUnableToGetOrderHeaderForOrderId", UtilMisc.toMap("orderId",orderId), locale));
        }

        // get all the approved items for the order
        List<GenericValue> orderItems = null;
        orderItems = orh.getOrderItemsByCondition(EntityCondition.makeCondition("statusId", "ITEM_APPROVED"));

        // find any service items
        List<GenericValue> serviceItems = FastList.newInstance();
        if (UtilValidate.isNotEmpty(orderItems)) {
            for(GenericValue item : orderItems) {
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "ERROR: Unable to get Product from OrderItem", module);
                }
                if (product != null) {
                    // check for service goods
                    if ("SERVICE".equals(product.get("productTypeId"))) {
                        serviceItems.add(item);
                    }
                }
            }
        }

        // now process the service items
        if (UtilValidate.isNotEmpty(serviceItems)) {
            // Make sure there is actually something needing invoicing because createInvoiceForOrder doesn't check
            List<GenericValue> billItems = FastList.newInstance();
            for (GenericValue item : serviceItems) {
                BigDecimal orderQuantity = OrderReadHelper.getOrderItemQuantity(item);
                BigDecimal invoiceQuantity = OrderReadHelper.getOrderItemInvoicedQuantity(item);
                BigDecimal outstandingQuantity = orderQuantity.subtract(invoiceQuantity);
                if (outstandingQuantity.compareTo(ZERO) > 0) {
                    billItems.add(item);
                }
            }
            // do something tricky here: run as a different user that can actually create an invoice, post transaction, etc
            Map<String, Object> invoiceResult = null;
            try {
                GenericValue permUserLogin = ServiceUtil.getUserLogin(dctx, context, "system");
                Map<String, Object> invoiceContext = UtilMisc.toMap("orderId", orderId, "billItems", billItems, "userLogin", permUserLogin);
                invoiceResult = dispatcher.runSync("createInvoiceForOrder", invoiceContext);
            } catch (GenericServiceException e) {
                Debug.logError(e, "ERROR: Unable to invoice service items", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderProblemWithInvoiceCreationServiceItems", locale));
            }
            if (ModelService.RESPOND_ERROR.equals(invoiceResult.get(ModelService.RESPONSE_MESSAGE))) {
                return ServiceUtil.returnError((String) invoiceResult.get(ModelService.ERROR_MESSAGE));
            }

            // update the status of service goods to COMPLETED;
            for(GenericValue item : serviceItems) {
               // Map<String, Object> statusCtx = FastMap.newInstance();
                Map<String, Object> statusCtx = new HashMap<String, Object>();
                
                statusCtx.put("orderId", item.getString("orderId"));
                statusCtx.put("orderItemSeqId", item.getString("orderItemSeqId"));
                statusCtx.put("statusId", "ITEM_COMPLETED");
                statusCtx.put("userLogin", userLogin);
                try {
                    dispatcher.runSyncIgnore("changeOrderItemStatus", statusCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "ERROR: Problem setting the status to COMPLETED : " + item, module);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> addItemToApprovedOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String orderId = (String) context.get("orderId");
        String productId = (String) context.get("productId");
        String prodCatalogId = (String) context.get("prodCatalogId");
        BigDecimal basePrice = (BigDecimal) context.get("basePrice");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal amount = (BigDecimal) context.get("amount");
        Timestamp itemDesiredDeliveryDate = (Timestamp) context.get("itemDesiredDeliveryDate");
        String overridePrice = (String) context.get("overridePrice");
        String reasonEnumId = (String) context.get("reasonEnumId");
        String changeComments = (String) context.get("changeComments");
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        int shipGroupIdx = -1;
        try {
            shipGroupIdx = Integer.parseInt(shipGroupSeqId);
            shipGroupIdx--;
        } catch (NumberFormatException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (shipGroupIdx < 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShipGroupSeqIdInvalid", UtilMisc.toMap("shipGroupSeqId", shipGroupSeqId), locale));
        }

        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShoppingCartEmpty", locale));
        }

        // add in the new product
        try {
            if ("PURCHASE_ORDER".equals(cart.getOrderType())) {
                GenericValue supplierProduct = cart.getSupplierProduct(productId, quantity, dispatcher);
                ShoppingCartItem item = null;
                if (supplierProduct != null) {
                    item = ShoppingCartItem.makePurchaseOrderItem(null, productId, null, quantity, null, null, prodCatalogId, null, null, null, dispatcher, cart, supplierProduct, itemDesiredDeliveryDate, itemDesiredDeliveryDate, null);
                    cart.addItem(0, item);
                } else {
                    throw new CartItemModifyException("No supplier information found for product [" + productId + "] and quantity quantity [" + quantity + "], cannot add to cart.");
                }

                if (basePrice != null) {
                    item.setBasePrice(basePrice);
                    item.setIsModifiedPrice(true);
                }

                cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
            } else {
                ShoppingCartItem item = ShoppingCartItem.makeItem(null, productId, null, quantity, null, null, null, null, null, null, null, null, prodCatalogId, null, null, null, dispatcher, cart, null, null, null, Boolean.FALSE, Boolean.FALSE);
                if (basePrice != null && overridePrice != null) {
                    item.setBasePrice(basePrice);
                    // special hack to make sure we re-calc the promos after a price change
                    item.setQuantity(quantity.add(BigDecimal.ONE), dispatcher, cart, false);
                    item.setQuantity(quantity, dispatcher, cart, false);
                    item.setBasePrice(basePrice);
                    item.setIsModifiedPrice(true);
                }

                // set the item in the selected ship group
                item.setDesiredDeliveryDate(itemDesiredDeliveryDate);
                cart.clearItemShipInfo(item);
                cart.setItemShipGroupQty(item, item.getQuantity(), shipGroupIdx);
            }
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map<String, Object> changeMap = UtilMisc.<String, Object>toMap("itemReasonMap", UtilMisc.<String, Object>toMap("reasonEnumId", reasonEnumId),
                                        "itemCommentMap", UtilMisc.<String, Object>toMap("changeComments", changeComments));
        // save all the updated information
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, false);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // log an order note
        try {
            String addedItemToOrder = UtilProperties.getMessage(resource, "OrderAddedItemToOrder", locale);
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", addedItemToOrder +
                    productId + " (" + quantity + ")", "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    public static Map<String, Object> updateApprovedOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String orderId = (String) context.get("orderId");
        Map<String, String> overridePriceMap = UtilGenerics.checkMap(context.get("overridePriceMap"));
        Map<String, String> itemDescriptionMap = UtilGenerics.checkMap(context.get("itemDescriptionMap"));
        Map<String, String> itemPriceMap = UtilGenerics.checkMap(context.get("itemPriceMap"));
        Map<String, String> itemQtyMap = UtilGenerics.checkMap(context.get("itemQtyMap"));
        Map<String, String> itemReasonMap = UtilGenerics.checkMap(context.get("itemReasonMap"));
        Map<String, String> itemCommentMap = UtilGenerics.checkMap(context.get("itemCommentMap"));
        Map<String, String> itemAttributesMap = UtilGenerics.checkMap(context.get("itemAttributesMap"));
        Map<String, String> itemEstimatedShipDateMap = UtilGenerics.checkMap(context.get("itemShipDateMap"));
        Map<String, String> itemEstimatedDeliveryDateMap = UtilGenerics.checkMap(context.get("itemDeliveryDateMap"));
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

     // go through the item map and obtain the totals per item
        Map<String, BigDecimal> itemTotals = new HashMap<String, BigDecimal>();
        Iterator<String> i = itemQtyMap.keySet().iterator();
        while (i.hasNext()) {
            String key = i.next();
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            try {
                groupQty = new BigDecimal(quantityStr);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (groupQty.compareTo(BigDecimal.ZERO) == 0) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderItemQtyMustBePositive", locale));
            }

            String[] itemInfo = key.split(":");
            BigDecimal tally = itemTotals.get(itemInfo[0]);
            if (tally == null) {
                tally = groupQty;
            } else {
                tally = tally.add(groupQty);
            }
            itemTotals.put(itemInfo[0], tally);
        }

        // obtain a shopping cart object for updating
        ShoppingCart cart = null;
        try {
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        if (cart == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "OrderShoppingCartEmpty", locale));
        }

        // go through the item attributes map once to get a list of key names
        Set<String> attributeNames =FastSet.newInstance();
        Set<String> keys  = itemAttributesMap.keySet();
        for (String key : keys) {
            String[] attributeInfo = key.split(":");
            attributeNames.add(attributeInfo[0]);
        }

        // set the items amount/price
        Iterator<String> iai = itemTotals.keySet().iterator();
        while (iai.hasNext()) {
            String itemSeqId = iai.next();
            ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);

            if (cartItem != null) {
                BigDecimal qty = itemTotals.get(itemSeqId);
                BigDecimal priceSave = cartItem.getBasePrice();

                // set quantity
                try {
                    cartItem.setQuantity(qty, dispatcher, cart, false, false); // trigger external ops, don't reset ship groups (and update prices for both PO and SO items)
                } catch (CartItemModifyException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                Debug.log("Set item quantity: [" + itemSeqId + "] " + qty, module);

                if (cartItem.getIsModifiedPrice()) // set price
                    cartItem.setBasePrice(priceSave);

                if (overridePriceMap.containsKey(itemSeqId)) {
                    String priceStr = itemPriceMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(priceStr)) {
                        BigDecimal price = new BigDecimal("-1");
                        price = new BigDecimal(priceStr).setScale(orderDecimals, orderRounding);
                        cartItem.setBasePrice(price);
                        cartItem.setIsModifiedPrice(true);
                        Debug.log("Set item price: [" + itemSeqId + "] " + price, module);
                    }

                }

                // Update the item description
                if (itemDescriptionMap != null && itemDescriptionMap.containsKey(itemSeqId)) {
                    String description = itemDescriptionMap.get(itemSeqId);
                    if (UtilValidate.isNotEmpty(description)) {
                        cartItem.setName(description);
                        Debug.log("Set item description: [" + itemSeqId + "] " + description, module);
                    } else {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                                "OrderItemDescriptionCannotBeEmpty", locale));
                    }
                }

                // update the order item attributes
                if (itemAttributesMap != null) {
                    String attrValue = null;
                    for (String attrName : attributeNames) {
                        attrValue = itemAttributesMap.get(attrName + ":" + itemSeqId);
                        if (UtilValidate.isNotEmpty(attrName)) {
                            cartItem.setOrderItemAttribute(attrName, attrValue);
                            Debug.log("Set item attribute Name: [" + itemSeqId + "] " + attrName + " , Value:" + attrValue, module);
                        }
                    }
                }

            } else {
                Debug.logInfo("Unable to locate shopping cart item for seqId #" + itemSeqId, module);
            }
        }
        // Create Estimated Delivery dates
        for (Map.Entry<String, String> entry : itemEstimatedDeliveryDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();
            String estimatedDeliveryDate = entry.getValue();
            if (UtilValidate.isNotEmpty(estimatedDeliveryDate)) {
                Timestamp deliveryDate = Timestamp.valueOf(estimatedDeliveryDate);
                ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                cartItem.setDesiredDeliveryDate(deliveryDate);
            }
        }

        // Create Estimated ship dates
        for (Map.Entry<String, String> entry : itemEstimatedShipDateMap.entrySet()) {
            String itemSeqId =  entry.getKey();
            String estimatedShipDate = entry.getValue();
            if (UtilValidate.isNotEmpty(estimatedShipDate)) {
                Timestamp shipDate = Timestamp.valueOf(estimatedShipDate);
                ShoppingCartItem cartItem = cart.findCartItem(itemSeqId);
                cartItem.setEstimatedShipDate(shipDate);
            }

        }

        // update the group amounts
        Iterator<String> gai = itemQtyMap.keySet().iterator();
        while (gai.hasNext()) {
            String key = gai.next();
            String quantityStr = itemQtyMap.get(key);
            BigDecimal groupQty = BigDecimal.ZERO;
            try {
                groupQty = new BigDecimal(quantityStr);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            String[] itemInfo = key.split(":");
            int groupIdx = -1;
            try {
                groupIdx = Integer.parseInt(itemInfo[1]);
            } catch (NumberFormatException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            // set the group qty
            ShoppingCartItem cartItem = cart.findCartItem(itemInfo[0]);
            if (cartItem != null) {
                Debug.log("Shipping info (before) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
                cart.setItemShipGroupQty(cartItem, groupQty, groupIdx - 1);
                Debug.log("Set ship group qty: [" + itemInfo[0] + " / " + itemInfo[1] + " (" + (groupIdx-1) + ")] " + groupQty, module);
                Debug.log("Shipping info (after) for group #" + (groupIdx-1) + " [" + cart.getShipmentMethodTypeId(groupIdx-1) + " / " + cart.getCarrierPartyId(groupIdx-1) + "]", module);
            }
        }

        // save all the updated information
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, UtilMisc.<String, Object>toMap("itemReasonMap", itemReasonMap, "itemCommentMap", itemCommentMap), calcTax, false);
        } catch (GeneralException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // run promotions to handle all changes in the cart
        ProductPromoWorker.doPromotions(cart, dispatcher);

        // log an order note
        try {
            dispatcher.runSync("createOrderNote", UtilMisc.<String, Object>toMap("orderId", orderId, "note", "Updated order.", "internalNote", "Y", "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }
        
        //reserveAllForPacking((GenericDelegator)delegator, dispatcher, userLogin, orderId, "00001", "N", "INVRO_FIFO_REC", "WebStoreWarehouse");
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    public static Map<String, Object> loadCartForUpdate(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        ShoppingCart cart = null;
        Map<String, Object> result = null;
        try {
            cart = loadCartForUpdate(dispatcher, delegator, userLogin, orderId);
            result = ServiceUtil.returnSuccess();
            result.put("shoppingCart", cart);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            result = ServiceUtil.returnError(e.getMessage());
        }

        result.put("orderId", orderId);
        return result;
    }

    /*
     *  Warning: loadCartForUpdate(...) and saveUpdatedCartToOrder(...) must always
     *           be used together in this sequence.
     *           In fact loadCartForUpdate(...) will remove or cancel data associated to the order,
     *           before returning the ShoppingCart object; for this reason, the cart
     *           must be stored back using the method saveUpdatedCartToOrder(...),
     *           because that method will recreate the data.
     */
    private static ShoppingCart loadCartForUpdate(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, String orderId) throws GeneralException {
        // load the order into a shopping cart
        Map<String, Object> loadCartResp = null;
        try {
            loadCartResp = dispatcher.runSync("loadCartFromOrder", UtilMisc.<String, Object>toMap("orderId", orderId,
                                                                                  "skipInventoryChecks", Boolean.TRUE, // the items are already reserved, no need to check again
                                                                                  "skipProductChecks", Boolean.TRUE, // the products are already in the order, no need to check their validity now
                                                                                  "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(loadCartResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(loadCartResp));
        }

        ShoppingCart cart = (ShoppingCart) loadCartResp.get("shoppingCart");
        if (cart == null) {
            throw new GeneralException("Error loading shopping cart from order [" + orderId + "]");
        } else {
            cart.setOrderId(orderId);
        }

        // Now that the cart is loaded, all the data that will be re-created
        // when the method saveUpdatedCartToOrder(...) will be called, are
        // removed and cancelled:
        // - inventory reservations are cancelled
        // - promotional items are cancelled
        // - order payments are released (cancelled)
        // - offline non received payments are cancelled
        // - promotional, shipping and tax adjustments are removed

        // Inventory reservations
        // find ship group associations
        List<GenericValue> shipGroupAssocs = null;
        try {
            shipGroupAssocs = delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        // cancel existing inventory reservations
        if (shipGroupAssocs != null) {
            Iterator<GenericValue> iri = shipGroupAssocs.iterator();
            while (iri.hasNext()) {
                GenericValue shipGroupAssoc = iri.next();
                String orderItemSeqId = shipGroupAssoc.getString("orderItemSeqId");
                String shipGroupSeqId = shipGroupAssoc.getString("shipGroupSeqId");

                Map<String, Object> cancelCtx = UtilMisc.<String, Object>toMap("userLogin", userLogin, "orderId", orderId);
                cancelCtx.put("orderItemSeqId", orderItemSeqId);
                cancelCtx.put("shipGroupSeqId", shipGroupSeqId);

                Map<String, Object> cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderInventoryReservation", cancelCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // cancel promo items -- if the promo still qualifies it will be added by the cart
        List<GenericValue> promoItems = null;
        try {
            promoItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId, "isPromo", "Y"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (promoItems != null) {
            Iterator<GenericValue> pii = promoItems.iterator();
            while (pii.hasNext()) {
                GenericValue promoItem = pii.next();
                // Skip if the promo is already cancelled
                if ("ITEM_CANCELLED".equals(promoItem.get("statusId"))) {
                    continue;
                }
                Map<String, Object> cancelPromoCtx = UtilMisc.<String, Object>toMap("orderId", orderId);
                cancelPromoCtx.put("orderItemSeqId", promoItem.getString("orderItemSeqId"));
                cancelPromoCtx.put("userLogin", userLogin);
                Map<String, Object> cancelResp = null;
                try {
                    cancelResp = dispatcher.runSync("cancelOrderItemNoActions", cancelPromoCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(cancelResp)) {
                    throw new GeneralException(ServiceUtil.getErrorMessage(cancelResp));
                }
            }
        }

        // cancel exiting authorizations
        Map<String, Object> releaseResp = null;
        try {
            releaseResp = dispatcher.runSync("releaseOrderPayments", UtilMisc.<String, Object>toMap("orderId", orderId, "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (ServiceUtil.isError(releaseResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(releaseResp));
        }

        // cancel other (non-completed and non-cancelled) payments
        List<GenericValue> paymentPrefsToCancel = null;
        try {
            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_RECEIVED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_DECLINED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_SETTLED"));
            exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_REFUNDED"));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            paymentPrefsToCancel = delegator.findList("OrderPaymentPreference", cond, null, null, null, false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }
        if (paymentPrefsToCancel != null) {
            Iterator<GenericValue> oppi = paymentPrefsToCancel.iterator();
            while (oppi.hasNext()) {
                GenericValue opp = oppi.next();
                try {
                    opp.set("statusId", "PAYMENT_CANCELLED");
                    opp.store();
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
            }
        }

        // remove the adjustments
        try {
            List<EntityCondition> adjExprs = new LinkedList<EntityCondition>();
            adjExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
            List<EntityCondition> exprs = new LinkedList<EntityCondition>();
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "PROMOTION_ADJUSTMENT"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SHIPPING_CHARGES"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "SALES_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_TAX"));
            exprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.EQUALS, "VAT_PRICE_CORRECT"));
            adjExprs.add(EntityCondition.makeCondition(exprs, EntityOperator.OR));
            EntityCondition cond = EntityCondition.makeCondition(adjExprs, EntityOperator.AND);
            delegator.removeByCondition("OrderAdjustment", cond);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        return cart;
    }

    public static Map<String, Object> saveUpdatedCartToOrder(DispatchContext dctx, Map<String, ? extends Object> context) throws GeneralException {

        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        String orderId = (String) context.get("orderId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        Map<String, Object> changeMap = UtilGenerics.checkMap(context.get("changeMap"));
        Locale locale = (Locale) context.get("locale");
        Boolean deleteItems = (Boolean) context.get("deleteItems");
        Boolean calcTax = (Boolean) context.get("calcTax");
        if (calcTax == null) {
            calcTax = Boolean.TRUE;
        }

        Map<String, Object> result = null;
        try {
            saveUpdatedCartToOrder(dispatcher, delegator, cart, locale, userLogin, orderId, changeMap, calcTax, deleteItems);
            result = ServiceUtil.returnSuccess();
            //result.put("shoppingCart", cart);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            result = ServiceUtil.returnError(e.getMessage());
        }

        result.put("orderId", orderId);
        return result;
    }

    private static void saveUpdatedCartToOrder(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart,
            Locale locale, GenericValue userLogin, String orderId, Map<String, Object> changeMap, boolean calcTax,
            boolean deleteItems) throws GeneralException {
        // get/set the shipping estimates.  if it's a SALES ORDER, then return an error if there are no ship estimates
        int shipGroups = cart.getShipGroupSize();
        for (int gi = 0; gi < shipGroups; gi++) {
            String shipmentMethodTypeId = cart.getShipmentMethodTypeId(gi);
            String carrierPartyId = cart.getCarrierPartyId(gi);
            Debug.log("Getting ship estimate for group #" + gi + " [" + shipmentMethodTypeId + " / " + carrierPartyId + "]", module);
            Map<String, Object> result = ShippingEvents.getShipGroupEstimate(dispatcher, delegator, cart, gi);
            if (("SALES_ORDER".equals(cart.getOrderType())) && (ServiceUtil.isError(result))) {
                Debug.logError(ServiceUtil.getErrorMessage(result), module);
                throw new GeneralException(ServiceUtil.getErrorMessage(result));
            }

            BigDecimal shippingTotal = (BigDecimal) result.get("shippingTotal");
            if (shippingTotal == null) {
                shippingTotal = BigDecimal.ZERO;
            }
            cart.setItemShipGroupEstimate(shippingTotal, gi);
        }

        // calc the sales tax        
        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        if (calcTax) {
            try {
                coh.calcAndAddTax();
            } catch (GeneralException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }

        // get the new orderItems, adjustments, shipping info, payments and order item attributes from the cart
        List<Map<String, Object>> modifiedItems = FastList.newInstance();
        List<GenericValue> toStore = new LinkedList<GenericValue>();
        List<GenericValue> toAddList = new ArrayList<GenericValue>();
        toAddList.addAll(cart.makeAllAdjustments());
        cart.clearAllPromotionAdjustments();
        ProductPromoWorker.doPromotions(cart, dispatcher);

        // validate the payment methods
        Map<String, Object> validateResp = coh.validatePaymentMethods();
        if (ServiceUtil.isError(validateResp)) {
            throw new GeneralException(ServiceUtil.getErrorMessage(validateResp));
        }

        // handle OrderHeader fields
        String billingAccountId = cart.getBillingAccountId();
        if (UtilValidate.isNotEmpty(billingAccountId)) {
            try {
                GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                orderHeader.set("billingAccountId", billingAccountId);
                toStore.add(orderHeader);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new GeneralException(e.getMessage());
            }
        }

        toStore.addAll(cart.makeOrderItems());
        toStore.addAll(cart.makeAllAdjustments());

        String shipGroupSeqId = null;
        long groupIndex = cart.getShipInfoSize();
        if (!deleteItems) {
            for (long itr = 1; itr <= groupIndex; itr++) {
                shipGroupSeqId = UtilFormatOut.formatPaddedNumber(itr, 5);
                List<GenericValue> removeList = new ArrayList<GenericValue>();
                for (GenericValue stored: toStore) {
                    if ("OrderAdjustment".equals(stored.getEntityName())) {
                        if (("SHIPPING_CHARGES".equals(stored.get("orderAdjustmentTypeId")) ||
                                "SALES_TAX".equals(stored.get("orderAdjustmentTypeId"))) &&
                                stored.get("orderId").equals(orderId) &&
                                stored.get("shipGroupSeqId").equals(shipGroupSeqId)) {
                            // Removing objects from toStore list for old Shipping and Handling Charges Adjustment and Sales Tax Adjustment.
                            removeList.add(stored);
                        }
                        if (stored.get("comments") != null && ((String)stored.get("comments")).startsWith("Added manually by")) {
                            // Removing objects from toStore list for Manually added Adjustment.
                            removeList.add(stored);
                        }
                    }
                }
                toStore.removeAll(removeList);
            }
            for (GenericValue toAdd: toAddList) {
                if ("OrderAdjustment".equals(toAdd.getEntityName())) {
                    if (toAdd.get("comments") != null && ((String)toAdd.get("comments")).startsWith("Added manually by") && (("PROMOTION_ADJUSTMENT".equals(toAdd.get("orderAdjustmentTypeId"))) ||
                            ("SHIPPING_CHARGES".equals(toAdd.get("orderAdjustmentTypeId"))) || ("SALES_TAX".equals(toAdd.get("orderAdjustmentTypeId"))))) {
                        toStore.add(toAdd);
                    }
                }
            }
        } else {                      
            // add all the cart adjustments
            toStore.addAll(toAddList);
        }
        
        // Creating objects for New Shipping and Handling Charges Adjustment and Sales Tax Adjustment
        toStore.addAll(cart.makeAllShipGroupInfos());
        toStore.addAll(cart.makeAllOrderPaymentInfos(dispatcher));
        toStore.addAll(cart.makeAllOrderItemAttributes(orderId, ShoppingCart.FILLED_ONLY));        

        
        List<GenericValue> toRemove = FastList.newInstance();
        if (deleteItems) {
            // flag to delete existing order items and adjustments           
            try {
                toRemove.addAll(delegator.findByAnd("OrderItemShipGroupAssoc", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemContactMech", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemPriceInfo", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemAttribute", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemBilling", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemRole", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItemChange", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderAdjustment", "orderId", orderId));
                toRemove.addAll(delegator.findByAnd("OrderItem", "orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            // get the empty order item atrributes from the cart and remove them
            toRemove.addAll(cart.makeAllOrderItemAttributes(orderId, ShoppingCart.EMPTY_ONLY));
        }

        // get the promo uses and codes
        for (String promoCodeEntered : cart.getProductPromoCodesEntered()) {
            GenericValue orderProductPromoCode = delegator.makeValue("OrderProductPromoCode");                                   
            orderProductPromoCode.set("orderId", orderId);
            orderProductPromoCode.set("productPromoCodeId", promoCodeEntered);
            toStore.add(orderProductPromoCode);                                    
        }
        for (GenericValue promoUse : cart.makeProductPromoUses()) {
            promoUse.set("orderId", orderId);
            toStore.add(promoUse);
        }        
        
        List<GenericValue> existingPromoCodes = null;
        List<GenericValue> existingPromoUses = null;
        try {
            existingPromoCodes = delegator.findByAnd("OrderProductPromoCode", UtilMisc.toMap("orderId", orderId));
            existingPromoUses = delegator.findByAnd("ProductPromoUse", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        toRemove.addAll(existingPromoCodes);
        toRemove.addAll(existingPromoUses);
                        
        // set the orderId & other information on all new value objects
        List<String> dropShipGroupIds = FastList.newInstance(); // this list will contain the ids of all the ship groups for drop shipments (no reservations)
        Iterator<GenericValue> tsi = toStore.iterator();
        while (tsi.hasNext()) {
            GenericValue valueObj = tsi.next();
            valueObj.set("orderId", orderId);
            if ("OrderItemShipGroup".equals(valueObj.getEntityName())) {
                // ship group
                if (valueObj.get("carrierRoleTypeId") == null) {
                    valueObj.set("carrierRoleTypeId", "CARRIER");
                }
                if (!UtilValidate.isEmpty(valueObj.get("supplierPartyId"))) {
                    dropShipGroupIds.add(valueObj.getString("shipGroupSeqId"));
                }
            } else if ("OrderAdjustment".equals(valueObj.getEntityName())) {
                // shipping / tax adjustment(s)
                if (UtilValidate.isEmpty(valueObj.get("orderItemSeqId"))) {
                    valueObj.set("orderItemSeqId", DataModelConstants.SEQ_ID_NA);
                }
                // in order to avoid duplicate adjustments don't set orderAdjustmentId (which is the pk) if there is already one
                if (UtilValidate.isEmpty(valueObj.getString("orderAdjustmentId"))) {
                    valueObj.set("orderAdjustmentId", delegator.getNextSeqId("OrderAdjustment"));
                }
                valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
            } else if ("OrderPaymentPreference".equals(valueObj.getEntityName())) {
                if (valueObj.get("orderPaymentPreferenceId") == null) {
                    valueObj.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                    valueObj.set("createdDate", UtilDateTime.nowTimestamp());
                    valueObj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                }
                if (valueObj.get("statusId") == null) {
                    valueObj.set("statusId", "PAYMENT_NOT_RECEIVED");
                }
            } else if ("OrderItem".equals(valueObj.getEntityName()) && !deleteItems) {

                //  ignore promotion items. They are added/canceled automatically
                if ("Y".equals(valueObj.getString("isPromo"))) {
                    continue;
                }
                GenericValue oldOrderItem = null;
                try {
                    oldOrderItem = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", valueObj.getString("orderId"), "orderItemSeqId", valueObj.getString("orderItemSeqId")));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (UtilValidate.isNotEmpty(oldOrderItem)) {

                    //  Existing order item found. Check for modifications and store if any
                    String oldItemDescription = oldOrderItem.getString("itemDescription") != null ? oldOrderItem.getString("itemDescription") : "";
                    BigDecimal oldQuantity = oldOrderItem.getBigDecimal("quantity") != null ? oldOrderItem.getBigDecimal("quantity") : BigDecimal.ZERO;
                    BigDecimal oldUnitPrice = oldOrderItem.getBigDecimal("unitPrice") != null ? oldOrderItem.getBigDecimal("unitPrice") : BigDecimal.ZERO;

                    boolean changeFound = false;
                   // Map<String, Object> modifiedItem = FastMap.newInstance();
                    Map<String, Object> modifiedItem = new HashMap<String, Object>();
                    
                    if (!oldItemDescription.equals(valueObj.getString("itemDescription"))) {
                        modifiedItem.put("itemDescription", oldItemDescription);
                        changeFound = true;
                    }

                    BigDecimal quantityDif = valueObj.getBigDecimal("quantity").subtract(oldQuantity);
                    BigDecimal unitPriceDif = valueObj.getBigDecimal("unitPrice").subtract(oldUnitPrice);
                    if (quantityDif.compareTo(BigDecimal.ZERO) != 0) {
                        modifiedItem.put("quantity", quantityDif);
                        changeFound = true;
                    }
                    if (unitPriceDif.compareTo(BigDecimal.ZERO) != 0) {
                        modifiedItem.put("unitPrice", unitPriceDif);
                        changeFound = true;
                    }
                    if (changeFound) {

                        //  found changes to store
                        Map<String, String> itemReasonMap = UtilGenerics.checkMap(changeMap.get("itemReasonMap"));
                        Map<String, String> itemCommentMap = UtilGenerics.checkMap(changeMap.get("itemCommentMap"));
                        if (UtilValidate.isNotEmpty(itemReasonMap)) {
                            String changeReasonId = itemReasonMap.get(valueObj.getString("orderItemSeqId"));
                            modifiedItem.put("reasonEnumId", changeReasonId);
                        }
                        if (UtilValidate.isNotEmpty(itemCommentMap)) {
                            String changeComments = itemCommentMap.get(valueObj.getString("orderItemSeqId"));
                            modifiedItem.put("changeComments", changeComments);
                        }

                        modifiedItem.put("orderId", valueObj.getString("orderId"));
                        modifiedItem.put("orderItemSeqId", valueObj.getString("orderItemSeqId"));
                        modifiedItem.put("changeTypeEnumId", "ODR_ITM_UPDATE");
                        modifiedItems.add(modifiedItem);
                    }
                } else {

                    //  this is a new item appended to the order
                    Map<String, String> itemReasonMap = UtilGenerics.checkMap(changeMap.get("itemReasonMap"));
                    Map<String, String> itemCommentMap = UtilGenerics.checkMap(changeMap.get("itemCommentMap"));
                   // Map<String, Object> appendedItem = FastMap.newInstance();
                    Map<String, Object> appendedItem = new HashMap<String, Object>();
                    
                    if (UtilValidate.isNotEmpty(itemReasonMap)) {
                        String changeReasonId = itemReasonMap.get("reasonEnumId");
                        appendedItem.put("reasonEnumId", changeReasonId);
                    }
                    if (UtilValidate.isNotEmpty(itemCommentMap)) {
                        String changeComments = itemCommentMap.get("changeComments");
                        appendedItem.put("changeComments", changeComments);
                    }

                    appendedItem.put("orderId", valueObj.getString("orderId"));
                    appendedItem.put("orderItemSeqId", valueObj.getString("orderItemSeqId"));
                    appendedItem.put("quantity", valueObj.getBigDecimal("quantity"));
                    appendedItem.put("changeTypeEnumId", "ODR_ITM_APPEND");
                    modifiedItems.add(appendedItem);
                }
            }
        }
        
        if (Debug.verboseOn())
            Debug.logVerbose("To Store Contains: " + toStore, module);

        // remove any order item attributes that were set to empty
        try {
            delegator.removeAll(toRemove,true);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        // store the new items/adjustments/order item attributes
        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        //  store the OrderItemChange
        if (UtilValidate.isNotEmpty(modifiedItems)) {
            for (Map<String, Object> modifiendItem: modifiedItems) {
               // Map<String, Object> serviceCtx = FastMap.newInstance();
                Map<String, Object> serviceCtx = new HashMap<String, Object>();
                
                serviceCtx.put("orderId", modifiendItem.get("orderId"));
                serviceCtx.put("orderItemSeqId", modifiendItem.get("orderItemSeqId"));
                serviceCtx.put("itemDescription", modifiendItem.get("itemDescription"));
                serviceCtx.put("quantity", modifiendItem.get("quantity"));
                serviceCtx.put("unitPrice", modifiendItem.get("unitPrice"));
                serviceCtx.put("changeTypeEnumId", modifiendItem.get("changeTypeEnumId"));
                serviceCtx.put("reasonEnumId", modifiendItem.get("reasonEnumId"));
                serviceCtx.put("changeComments", modifiendItem.get("changeComments"));
                serviceCtx.put("userLogin", userLogin);
                Map<String, Object> resp = null;
                try {
                    resp = dispatcher.runSync("createOrderItemChange", serviceCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                    throw new GeneralException(e.getMessage());
                }
                if (ServiceUtil.isError(resp)) {
                    throw new GeneralException((String) resp.get(ModelService.ERROR_MESSAGE));
                }
            }
        }

        // make the order item object map & the ship group assoc list
        List<GenericValue> orderItemShipGroupAssoc = new LinkedList<GenericValue>();
        Map<String, GenericValue> itemValuesBySeqId = new HashMap<String, GenericValue>();
        Iterator<GenericValue> oii = toStore.iterator();
        while (oii.hasNext()) {
            GenericValue v = oii.next();
            if ("OrderItem".equals(v.getEntityName())) {
                itemValuesBySeqId.put(v.getString("orderItemSeqId"), v);
            } else if ("OrderItemShipGroupAssoc".equals(v.getEntityName())) {
                orderItemShipGroupAssoc.add(v);
            }
        }

        // reserve the inventory
        String productStoreId = cart.getProductStoreId();
        String orderTypeId = cart.getOrderType();
        List<String> resErrorMessages = new LinkedList<String>();
        try {
            Debug.log("Calling reserve inventory...", module);
            reserveInventory(delegator, dispatcher, userLogin, locale, orderItemShipGroupAssoc, dropShipGroupIds, itemValuesBySeqId,
                    orderTypeId, productStoreId, resErrorMessages);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw new GeneralException(e.getMessage());
        }

        if (resErrorMessages.size() > 0) {
            throw new GeneralException(ServiceUtil.getErrorMessage(ServiceUtil.returnError(resErrorMessages)));
        }
    }

    public static Map<String, Object> processOrderPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        String productStoreId = orh.getProductStoreId();

        // check if order was already cancelled / rejected
        GenericValue orderHeader = orh.getOrderHeader();
        String orderStatus = orderHeader.getString("statusId");
        if ("ORDER_CANCELLED".equals(orderStatus) || "ORDER_REJECTED".equals(orderStatus)) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resource,
                    "OrderProcessOrderPaymentsStatusInvalid", locale) + orderStatus);
        }

        // process the payments
        if (!"PURCHASE_ORDER".equals(orh.getOrderTypeId())) {
            GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
            Map<String, Object> paymentResp = null;
            try {
                Debug.log("Calling process payments...", module);
                //Debug.set(Debug.VERBOSE, true);
                paymentResp = CheckOutHelper.processPayment(orderId, orh.getOrderGrandTotal(), orh.getCurrency(), productStore, userLogin, false, false, dispatcher, delegator);
                //Debug.set(Debug.VERBOSE, false);
            } catch (GeneralException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            } catch (GeneralRuntimeException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }

            if (ServiceUtil.isError(paymentResp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderProcessOrderPayments", locale), null, null, paymentResp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    // sample test services
    public static Map<String, Object> shoppingCartTest(DispatchContext dctx, Map<String, ? extends Object> context) {
        Locale locale = (Locale) context.get("locale");
        ShoppingCart cart = new ShoppingCart(dctx.getDelegator(), "9000", "webStore", locale, "USD");
        try {
            cart.addOrIncreaseItem("GZ-1005", null, BigDecimal.ONE, null, null, null, null, null, null, null, "DemoCatalog", null, null, null, null,null,null,null,null, dctx.getDispatcher());
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
        } catch (ItemNotFoundException e) {
            Debug.logError(e, module);
        }

        try {
            dctx.getDispatcher().runAsync("shoppingCartRemoteTest", UtilMisc.toMap("cart", cart), true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> shoppingCartRemoteTest(DispatchContext dctx, Map<String, ? extends Object> context) {
        ShoppingCart cart = (ShoppingCart) context.get("cart");
        Debug.log("Product ID : " + cart.findCartItem(0).getProductId(), module);
        return ServiceUtil.returnSuccess();
    }

    /**
     * Service to create a payment using an order payment preference.
     * @return Map
     */
    public static Map<String, Object> createPaymentFromPreference(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String paymentRefNum = (String) context.get("paymentRefNum");
        String paymentFromId = (String) context.get("paymentFromId");
        String comments = (String) context.get("comments");
        Timestamp eventDate = (Timestamp) context.get("eventDate");
        Locale locale = (Locale) context.get("locale");
        if (UtilValidate.isEmpty(eventDate)) {
            eventDate = UtilDateTime.nowTimestamp();
        }
        try {
            // get the order payment preference
            GenericValue orderPaymentPreference = delegator.findByPrimaryKey("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId));
            if (orderPaymentPreference == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreated",
                        UtilMisc.toMap("orderPaymentPreferenceId", "orderPaymentPreferenceId"), locale));
            }

            // get the order header
            GenericValue orderHeader = orderPaymentPreference.getRelatedOne("OrderHeader");
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreatedWithRelatedOrderHeader", locale));
            }

            // get the store for the order.  It will be used to set the currency
            GenericValue productStore = orderHeader.getRelatedOne("ProductStore");
            if (productStore == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreatedWithRelatedProductStore", locale));
            }

            // get the partyId billed to
            if (paymentFromId == null) {
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                GenericValue billToParty = orh.getBillToParty();
                if (billToParty != null) {
                    paymentFromId = billToParty.getString("partyId");
                } else {
                    paymentFromId = "_NA_";
                }
            }

            // set the payToPartyId
            String payToPartyId = productStore.getString("payToPartyId");
            if (payToPartyId == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderPaymentCannotBeCreatedPayToPartyIdNotSet", locale));
            }

            // create the payment
            Map<String, Object> paymentParams = new HashMap<String, Object>();
            BigDecimal maxAmount = orderPaymentPreference.getBigDecimal("maxAmount");
            //if (maxAmount > 0.0) {
                paymentParams.put("paymentTypeId", "CUSTOMER_PAYMENT");
                paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId"));
                paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
                paymentParams.put("amount", maxAmount);
                paymentParams.put("statusId", "PMNT_RECEIVED");
                paymentParams.put("effectiveDate", eventDate);
                paymentParams.put("partyIdFrom", paymentFromId);
                paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
                paymentParams.put("partyIdTo", payToPartyId);
            /*}
            else {
                paymentParams.put("paymentTypeId", "CUSTOMER_REFUND"); // JLR 17/7/4 from a suggestion of Si cf. https://issues.apache.org/jira/browse/OFBIZ-828#action_12483045
                paymentParams.put("paymentMethodTypeId", orderPaymentPreference.getString("paymentMethodTypeId")); // JLR 20/7/4 Finally reverted for now, I prefer to see an amount in payment, even negative
                paymentParams.put("paymentPreferenceId", orderPaymentPreference.getString("orderPaymentPreferenceId"));
                paymentParams.put("amount", Double.valueOf(Math.abs(maxAmount)));
                paymentParams.put("statusId", "PMNT_RECEIVED");
                paymentParams.put("effectiveDate", UtilDateTime.nowTimestamp());
                paymentParams.put("partyIdFrom", payToPartyId);
                paymentParams.put("currencyUomId", productStore.getString("defaultCurrencyUomId"));
                paymentParams.put("partyIdTo", billToParty.getString("partyId"));
            }*/
            if (paymentRefNum != null) {
                paymentParams.put("paymentRefNum", paymentRefNum);
            }
            if (comments != null) {
                paymentParams.put("comments", comments);
            }
            paymentParams.put("userLogin", userLogin);
            
            return dispatcher.runSync("createPayment", paymentParams);

        } catch (GenericEntityException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        } catch (GenericServiceException ex) {
            Debug.logError(ex, "Unable to create payment using payment preference.", module);
            return(ServiceUtil.returnError(ex.getMessage()));
        }
    }

    public static Map<String, Object> massChangeApproved(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_APPROVED");
    }

    public static Map<String, Object> massCancelOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeItemStatus(dctx, context, "ITEM_CANCELLED");
    }

    public static Map<String, Object> massRejectOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeItemStatus(dctx, context, "ITEM_REJECTED");
    }

    public static Map<String, Object> massHoldOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_HOLD");
    }

    public static Map<String, Object> massProcessOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        return massChangeOrderStatus(dctx, context, "ORDER_PROCESSING");
    }

    public static Map<String, Object> massChangeOrderStatus(DispatchContext dctx, Map<String, ? extends Object> context, String statusId) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        Iterator<String> i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (orderHeader == null) {
                return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                        "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            }

           // Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("statusId", statusId);
            ctx.put("orderId", orderId);
            ctx.put("setItemStatus", "Y");
            ctx.put("userLogin", userLogin);
            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("changeOrderStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatus", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massChangeItemStatus(DispatchContext dctx, Map<String, ? extends Object> context, String statusId) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        Iterator<String> i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            GenericValue orderHeader = null;
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (orderHeader == null) {
                return ServiceUtil.returnFailure(UtilProperties.getMessage(resource, 
                        "OrderOrderNotFound", UtilMisc.toMap("orderId", orderId), locale));
            }

           // Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("statusId", statusId);
            ctx.put("orderId", orderId);
            ctx.put("userLogin", userLogin);
            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("changeOrderItemStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeItemStatus", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massQuickShipOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");
        for (Object orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            //Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("userLogin", userLogin);
            ctx.put("orderId", orderId);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("quickShipEntireOrder", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "OrderOrderQuickShipEntireOrderError", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }
    public static String pinIdQuickShipOrders(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
    	GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	//Locale locale = (Locale)  request.getAttribute("locale");
    	
    	List<GenericValue> orderIdsList = null;
        String paramValues = null;
        Enumeration parameterNames = request.getParameterNames();
        String orderId= null;
        String statusId= null;
        List<String> msgList=new ArrayList<String>();
        while (parameterNames.hasMoreElements()) {
        	Object objOri=parameterNames.nextElement();
        	String elementName=(String)objOri;
        	paramValues = request.getParameter(elementName);
        	
        	if(UtilValidate.isNotEmpty(paramValues)){
        		try {
        		orderIdsList = delegator.findByAnd("OrderHeader", UtilMisc.toMap("pinId", paramValues));
        		GenericValue orderIdsList1 = EntityUtil.getFirst(orderIdsList);	
        	
        		 if (UtilValidate.isNotEmpty(orderIdsList1)) {
        			 Object orderIds = orderIdsList1.get("orderId");
        			 Object orderStatus = orderIdsList1.get("statusId");
        			  orderId=(String)orderIds;
        			  statusId = (String)orderStatus;
        			  
        			  if(statusId.equals("ORDER_COMPLETED"))
        				  msgList.add("Already Completed : "+paramValues);
        			  
        			 if(UtilValidate.isNotEmpty(orderId) && statusId.equals("ORDER_APPROVED")){
        				 Map<String, Object> resp = null;
        	            try {
        	            	resp = dispatcher.runSync("quickShipEntireOrder", UtilMisc.toMap("orderId", orderId, "userLogin", userLogin));
        	            	String resp2 = (String)resp.get("responseMessage");
        	            	if(resp2.equals("success")){
        	            		msgList.add("Completed Successfully : "+paramValues);
        	            	}
        	            	else{
        	            		msgList.add("Can not Complete, [Check Inventory or first approve it]  : "+paramValues);
        	            	}
        	            } catch (Exception exc) {
        	                Debug.logWarning("Unable to quick ship test sales order with id [" + orderId + "] with error: " + exc.getMessage(), module);
        	            }
        	           /* if (ServiceUtil.isError(resp)) {
        	                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
        	                        "OrderOrderQuickShipEntireOrderError", locale), null, null, resp);
        	            }*/
        			 }
        		 }
        		 else
        			 msgList.add("Invalid Pin Number : "+paramValues); 
        		} catch (Exception exc) {
        			
                    Debug.logWarning("Unable to quick ship test sales order with id [" + orderId + "] with error: " + exc.getMessage(), module);
                }
        	}
             
        }
        request.setAttribute("msg",msgList);
       
        return "success";
    }
    
    
    public static String OrderFulFilled(HttpServletRequest request, HttpServletResponse response) {
    	GenericValue userLogin= (GenericValue)request.getSession().getAttribute("userLogin");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String orderIds[] = request.getParameterValues("orderId");
    	List<String> successMsgList = new ArrayList<String>();
    	List<String> errMsgList = new ArrayList<String>();
    	
    	List<GenericValue> orderFulFillDetails = new ArrayList<GenericValue>();
    	try {
	    	if(UtilValidate.isNotEmpty(orderIds))
	    	for(String orderID : orderIds)
	    	{
	    		String pinCode = request.getParameter(orderID+"pinCode");
    			if(UtilValidate.isNotEmpty(pinCode))
    			{
    				List conditionList = UtilMisc.toList(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderID),
    																			EntityCondition.makeCondition("pinId",EntityOperator.EQUALS,pinCode));
	    			List<GenericValue> orderList=delegator.findList("OrderHeader", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, null, null, false);
	    			//GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",paramValues));
	    			GenericValue orderHeader=EntityUtil.getFirst(orderList);
	    			if (UtilValidate.isNotEmpty(orderHeader)) {
		    			String orderId = orderHeader.getString("orderId");
		    			String statusId = orderHeader.getString("statusId");
		    			if("ORDER_COMPLETED".equals(statusId))
		    				errMsgList.add("Already Fulfilled : order "+orderId+"  for happy code "+pinCode);
	    			  
		    			if("ORDER_DISPATCHED".equals(statusId))
		    			{
		    				String adjustment = request.getParameter(orderId+"adjustment");
		    		    	if(UtilValidate.isEmpty(adjustment))adjustment = BigDecimal.ZERO.toString();
		    		    	if(Double.parseDouble(adjustment) < 0) adjustment = Double.parseDouble(adjustment)*(-1)+"";
		    		    	
		    		    	if(Double.parseDouble(adjustment) <= 100)
		    		    	{
		    		    		TransactionUtil.begin();
		    		        	List<GenericValue> orderFulFillDetail = orderFulFillDetails(request, delegator, userLogin, orderId, orderHeader.getString("productStoreId"), successMsgList, errMsgList);
		    		        	if(UtilValidate.isNotEmpty(orderFulFillDetail));
		    		        		//orderFulFillDetails.add(orderFulFillDetail);
		    		        	else
		    		        	{
		    		        		errMsgList.add("Problems in creating orderFulFillDetails order "+orderId+"  for happy code "+pinCode);
		    		        		try {
		    		    				TransactionUtil.rollback();
		    		    			} catch (GenericTransactionException e) {
		    		    				// TODO Auto-generated catch block
		    		    				e.printStackTrace();
		    		    			}
		    		        	}
		    		        	TransactionUtil.commit();
		    		    	}
		    		    	else
	    		        		errMsgList.add("Cann't adjust more than 100 for order Id "+orderId+"  for happy code "+pinCode);
		    			}
			    		if(!"ORDER_DISPATCHED".equals(statusId))
			    			errMsgList.add("Can not fulfill order "+orderId+"  for happy code "+pinCode +" , Order is Not Dispatched ");
	    			}
		    		else
		    			errMsgList.add("Order not found for happy code : "+pinCode);
    			}
	    	}
	    	if(orderFulFillDetails.size() > 0)
	    		delegator.storeAll(orderFulFillDetails);
    	} 
		catch (Exception exc) {
			exc.printStackTrace();
            Debug.logWarning("Unable to get order details with error: " + exc.getMessage(), module);
            try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		successMsgList.addAll(errMsgList);
    	request.setAttribute("_EVENT_MESSAGE_LIST_", successMsgList);
        
        return "success";
    }
    
    public static List<GenericValue> orderFulFillDetails(HttpServletRequest request ,Delegator delegator, 
    				GenericValue userLogin, String orderId, String productStoreId, List<String> successMsgList, List<String> errMsgList){
    	LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
    	List<GenericValue> toBeCreate = new ArrayList<GenericValue>();
    	try{
    		
	    	String happyCode = request.getParameter(orderId+"pinCode");
	    	String amtToRecv = request.getParameter(orderId+"amtToRecv");
	    	String adjustment = request.getParameter(orderId+"adjustment");
	    	String comment = request.getParameter(orderId+"comment");
	    	
	    	String shipmentId = request.getParameter(orderId+"shipmentId");
	    	String deliveryBoyId = request.getParameter(orderId+"deliveryBoyId");
	    	String vehicleNumber = request.getParameter(orderId+"vehicleNumber");
	    	
	    	if(UtilValidate.isEmpty(amtToRecv))amtToRecv = BigDecimal.ZERO.toString();
	    	if(UtilValidate.isEmpty(adjustment))adjustment = BigDecimal.ZERO.toString();
	    	
	    	BigDecimal amountToRecv = new BigDecimal(amtToRecv);
	    	BigDecimal adj = new BigDecimal(adjustment);
	    	
	    	String noOfPayment = request.getParameter("noOfPayment");
	    	if(UtilValidate.isEmpty(noOfPayment)) noOfPayment = "1";
	    	boolean flag = true;
	    	String message = "error";
	    	BigDecimal maxAdjustmentAmountForFulfill = new BigDecimal(1000);
	    	String paymentMethodTypeId = null;
	    	for(int i =1 ; i<= Integer.parseInt(noOfPayment);i++)
	    	{
	    		paymentMethodTypeId = request.getParameter(orderId+"paymentMethodTypeId"+i);
	        	String amtRecv = request.getParameter(orderId+"amtRecv"+i);
	        	if(UtilValidate.isEmpty(amtRecv))amtRecv = BigDecimal.ZERO.toString();
	        	BigDecimal amountRecved = new BigDecimal(amtRecv);
	        	
	        	if(amountRecved.doubleValue() > 0 || "EXT_ONLINE".equals(paymentMethodTypeId))
	        	{
		    		GenericValue orderFulFillDetail = delegator.makeValue("OrderFulFillDetails");
		        	
		        	orderFulFillDetail.put("orderFulFillDetailId", delegator.getNextSeqId("OrderFulFillDetails"));
		        	orderFulFillDetail.put("orderId", orderId);
		        	orderFulFillDetail.put("shipmentId", shipmentId);
		        	orderFulFillDetail.put("vehicleNumber", vehicleNumber);
		        	orderFulFillDetail.put("deliveryBoyId", deliveryBoyId);
		        	orderFulFillDetail.put("paymentMethodTypeId", paymentMethodTypeId);
		        	orderFulFillDetail.put("happyCode", happyCode);
		        	orderFulFillDetail.put("amtToRecv", amountToRecv);
		        	orderFulFillDetail.put("amountReceived", amountRecved);
		        	orderFulFillDetail.put("adjustment", adj);
		        	orderFulFillDetail.put("comment", comment);
		        	orderFulFillDetail.put("createdBy", userLogin.getString("userLoginId"));
		        	orderFulFillDetail.put("createdDate", UtilDateTime.nowTimestamp());
		        	
		        	if(Double.parseDouble(adjustment) < 0) adjustment = Double.parseDouble(adjustment)*(-1)+"";
		        	
		        	if("EXT_ONLINE".equals(paymentMethodTypeId))
		        	{
		        		message = "success";
		        	}else{
		        		message = "error";
		        	}
		        	if(!"success".equals(message))
		        	{
			        	GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
			    		if(UtilValidate.isNotEmpty(productStore) && UtilValidate.isNotEmpty(productStore.getBigDecimal("maxAdjustmentAmountForFulfill")))
			    			maxAdjustmentAmountForFulfill = productStore.getBigDecimal("maxAdjustmentAmountForFulfill");
			        	
			        	if(Double.parseDouble(adjustment) <= maxAdjustmentAmountForFulfill.doubleValue() && UtilValidate.isNotEmpty(paymentMethodTypeId))
			        	{
			        		message = OrderManagerEvents.receiveOfflinePayment(request, orderId, amtRecv, paymentMethodTypeId);
			        		
			        		if("success".equals(message) && Double.parseDouble(adjustment) != 0 && flag)
			        		{
			    	    		Map<String, Object> statusFields = 
			    	    			UtilMisc.<String, Object>toMap("comments", "Added manually by userLoginId - "+userLogin.getString("userLoginId"), 
			    	    					"orderId", orderId,"orderAdjustmentTypeId","DELIVERY_ADJUSTMENT","shipGroupSeqId","00001","description",comment,"amount",adj,"userLogin",userLogin);
			    	    		
			    	    		Map<String, Object> statusResult = dispatcher.runSync("createOrderAdjustment", statusFields);
			    				
			    				if (ServiceUtil.isSuccess(statusResult)){
			    					flag = false;
			    				}
			    				else
			    				{
			    					message = "error";
			    				}
			        		}
			        	}
		        	}
		        	toBeCreate.add(orderFulFillDetail);
	        	}
	    	}
	    	
	    	if("success".equals(message) || UtilValidate.isEmpty(paymentMethodTypeId))
	    	{
		    	if(Double.parseDouble(adjustment) <= maxAdjustmentAmountForFulfill.doubleValue())
		    	{
			    	Map<String, Object> statusFields = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ORDER_COMPLETED",
							"userLogin", userLogin);
					Map<String, Object> statusResult = dispatcher.runSync("changeOrderStatus", statusFields);
					
					if (ServiceUtil.isSuccess(statusResult)) {
						successMsgList.add("Order Fulfilled : order "+orderId+"  for happy code "+happyCode);
						
						if(UtilValidate.isNotEmpty(shipmentId))
							completeShipping(dispatcher, userLogin, shipmentId);
					}else
					{
						Debug.logError("Problems adjusting order header status for order #" + orderId, module);
						errMsgList.add("Problems adjusting order header status for order #" + orderId+" for happy code "+happyCode);
					}
		    	}else
		    		successMsgList.add("Order Payment Received : order "+orderId+"  for happy code "+happyCode);
	    	}else
	    		return null;
	    	
	    	for(GenericValue orderFulFillDetail : toBeCreate)
	    	{
	    		orderFulFillDetail.create();
	    	}
    	}catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    	return toBeCreate;
    }
    
    public static void completeShipping(LocalDispatcher dispatcher, GenericValue userLogin, String shipmentId){
    	Map<String, Object> statusFields = UtilMisc.<String, Object>toMap("shipmentId", shipmentId, "statusId", "SHIPMENT_SHIPPED", "userLogin", userLogin);
		try {
			dispatcher.runSync("updateShipment", statusFields);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Map<String, Object> massPickOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        // grouped by facility
       // Map<String, List<String>> facilityOrdersMap = FastMap.newInstance();
        Map<String, List<String>> facilityOrdersMap = new HashMap<String, List<String>>();
        

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Iterator<String> i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            List<GenericValue> invInfo = null;
            try {
                invInfo = delegator.findByAnd("OrderItemAndShipGrpInvResAndItem",
                        UtilMisc.toMap("orderId", orderId, "statusId", "ITEM_APPROVED"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (invInfo != null) {
                Iterator<GenericValue> ii = invInfo.iterator();
                while (ii.hasNext()) {
                    GenericValue inv = ii.next();
                    String facilityId = inv.getString("facilityId");
                    List<String> orderIdsByFacility = facilityOrdersMap.get(facilityId);
                    if (orderIdsByFacility == null) {
                        orderIdsByFacility = new ArrayList<String>();
                    }
                    orderIdsByFacility.add(orderId);
                    facilityOrdersMap.put(facilityId, orderIdsByFacility);
                }
            }
        }

        // now create the pick lists for each facility
        Iterator<String> fi = facilityOrdersMap.keySet().iterator();
        while (fi.hasNext()) {
            String facilityId = fi.next();
            List<String> orderIdList = facilityOrdersMap.get(facilityId);

           // Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            ctx.put("userLogin", userLogin);
            ctx.put("orderIdList", orderIdList);
            ctx.put("facilityId", facilityId);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("createPicklistFromOrders", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderPickingListCreationError", locale), null, null, resp);
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massPrintOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String screenLocation = (String) context.get("screenLocation");
        String printerName = (String) context.get("printerName");

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Iterator<String> i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
           // Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("userLogin", userLogin);
            ctx.put("screenLocation", screenLocation);
            //ctx.put("contentType", "application/postscript");
            if (UtilValidate.isNotEmpty(printerName)) {
                ctx.put("printerName", printerName);
            }
            ctx.put("screenContext", UtilMisc.toMap("orderId", orderId));

            try {
                dispatcher.runAsync("sendPrintFromScreen", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massCreateFileForOrders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String screenLocation = (String) context.get("screenLocation");

        // make the list per facility
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Iterator<String> i = orderIds.iterator();
        while (i.hasNext()) {
            String orderId = i.next();
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
           // Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("userLogin", userLogin);
            ctx.put("screenLocation", screenLocation);
            //ctx.put("contentType", "application/postscript");
            ctx.put("fileName", "order_" + orderId + "_");
            ctx.put("screenContext", UtilMisc.toMap("orderId", orderId));

            try {
                dispatcher.runAsync("createFileFromScreen", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> massCancelRemainingPurchaseOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<String> orderIds = UtilGenerics.checkList(context.get("orderIdList"));
        Locale locale = (Locale) context.get("locale");

        for (Object orderId : orderIds) {
            if (UtilValidate.isEmpty(orderId)) {
                continue;
            }
            //Map<String, Object> ctx = FastMap.newInstance();
            Map<String, Object> ctx = new HashMap<String, Object>();
            
            ctx.put("orderId", orderId);
            ctx.put("userLogin", userLogin);

            Map<String, Object> resp = null;
            try {
                resp = dispatcher.runSync("cancelRemainingPurchaseOrderItems", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderCancelRemainingPurchaseOrderItemsError", locale), null, null, resp);
            }
            try {
                resp = dispatcher.runSync("checkOrderItemStatus", ctx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
            if (ServiceUtil.isError(resp)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "OrderOrderCheckOrderItemStatusError", locale), null, null, resp);
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> checkCreateDropShipPurchaseOrders(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        // TODO (use the "system" user)
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        // TODO: skip this if there is already a purchase order associated with the sales order (ship group)

        try {
            // if sales order
            if ("SALES_ORDER".equals(orh.getOrderTypeId())) {
                // get the order's ship groups
                Iterator<GenericValue> shipGroups = orh.getOrderItemShipGroups().iterator();
                while (shipGroups.hasNext()) {
                    GenericValue shipGroup = shipGroups.next();
                    if (!UtilValidate.isEmpty(shipGroup.getString("supplierPartyId"))) {
                        // This ship group is a drop shipment: we create a purchase order for it
                        String supplierPartyId = shipGroup.getString("supplierPartyId");
                        // create the cart
                        ShoppingCart cart = new ShoppingCart(delegator, orh.getProductStoreId(), null, orh.getCurrency());
                        cart.setOrderType("PURCHASE_ORDER");
                        cart.setBillToCustomerPartyId(cart.getBillFromVendorPartyId()); //Company
                        cart.setBillFromVendorPartyId(supplierPartyId);
                        cart.setOrderPartyId(supplierPartyId);
                        // Get the items associated to it and create po
                        List<GenericValue> items = orh.getValidOrderItems(shipGroup.getString("shipGroupSeqId"));
                        if (!UtilValidate.isEmpty(items)) {
                            Iterator<GenericValue> itemsIt = items.iterator();
                            while (itemsIt.hasNext()) {
                                GenericValue item = itemsIt.next();
                                try {
                                    int itemIndex = cart.addOrIncreaseItem(item.getString("productId"),
                                                                           null, // amount
                                                                           item.getBigDecimal("quantity"),
                                                                           null, null, null, // reserv
                                                                           item.getTimestamp("shipBeforeDate"),
                                                                           item.getTimestamp("shipAfterDate"),
                                                                           null, null, null,
                                                                           null, null, null,
                                                                           null,null,null,null,null, dispatcher);
                                    ShoppingCartItem sci = cart.findCartItem(itemIndex);
                                    sci.setAssociatedOrderId(orderId);
                                    sci.setAssociatedOrderItemSeqId(item.getString("orderItemSeqId"));
                                    sci.setOrderItemAssocTypeId("DROP_SHIPMENT");
                                } catch (Exception e) {
                                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                            "OrderOrderCreatingDropShipmentsError", 
                                            UtilMisc.toMap("orderId", orderId, "errorString", e.getMessage()),
                                            locale));
                                }
                            }
                        }

                        // If there are indeed items to drop ship, then create the purchase order
                        if (!UtilValidate.isEmpty(cart.items())) {
                            // set checkout options
                            cart.setDefaultCheckoutOptions(dispatcher);
                            // the shipping address is the one of the customer
                            cart.setShippingContactMechId(shipGroup.getString("contactMechId"));
                            // associate ship groups of sales and purchase orders
                            ShoppingCart.CartShipInfo cartShipInfo = cart.getShipGroups().get(0);
                            cartShipInfo.setAssociatedShipGroupSeqId(shipGroup.getString("shipGroupSeqId"));
                            // create the order
                            CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
                            coh.createOrder(userLogin);
                        } else {
                            // if there are no items to drop ship, then clear out the supplier partyId
                            Debug.logWarning("No drop ship items found for order [" + shipGroup.getString("orderId") + "] and ship group [" + shipGroup.getString("shipGroupSeqId") + "] and supplier party [" + shipGroup.getString("supplierPartyId") + "].  Supplier party information will be cleared for this ship group", module);
                            shipGroup.set("supplierPartyId", null);
                            shipGroup.store();

                        }
                    }
                }
            }
        } catch (Exception exc) {
            // TODO: imporve error handling
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "OrderOrderCreatingDropShipmentsError", 
                    UtilMisc.toMap("orderId", orderId, "errorString", exc.getMessage()),
                    locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> updateOrderPaymentPreference(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String checkOutPaymentId = (String) context.get("checkOutPaymentId");
        String statusId = (String) context.get("statusId");
        
        try {
            GenericValue opp = delegator.findByPrimaryKey("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId));
            String paymentMethodId = null;
            String paymentMethodTypeId = null;

            // The checkOutPaymentId is either a paymentMethodId or paymentMethodTypeId
            // the original method did a "\d+" regexp to decide which is the case, this version is more explicit with its lookup of PaymentMethodType
            if (checkOutPaymentId != null) {
                List<GenericValue> paymentMethodTypes = delegator.findList("PaymentMethodType", null, null, null, null, true);
                for (Iterator<GenericValue> iter = paymentMethodTypes.iterator(); iter.hasNext();) {
                    GenericValue type = iter.next();
                    if (type.get("paymentMethodTypeId").equals(checkOutPaymentId)) {
                        paymentMethodTypeId = (String) type.get("paymentMethodTypeId");
                        break;
                    }
                }
                if (paymentMethodTypeId == null) {
                    GenericValue method = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodTypeId", paymentMethodTypeId));
                    paymentMethodId = checkOutPaymentId;
                    paymentMethodTypeId = (String) method.get("paymentMethodTypeId");
                }
            }

            Map<String, Object> results = ServiceUtil.returnSuccess();
            if (UtilValidate.isNotEmpty(statusId) && statusId.equalsIgnoreCase("PAYMENT_CANCELLED")) {
                opp.set("statusId", "PAYMENT_CANCELLED");
                opp.store();
                results.put("orderPaymentPreferenceId", opp.get("orderPaymentPreferenceId"));
            } else {
                GenericValue newOpp = (GenericValue) opp.clone();
                opp.set("statusId", "PAYMENT_CANCELLED");
                opp.store();

                newOpp.set("orderPaymentPreferenceId", delegator.getNextSeqId("OrderPaymentPreference"));
                newOpp.set("paymentMethodId", paymentMethodId);
                newOpp.set("paymentMethodTypeId", paymentMethodTypeId);
                newOpp.setNonPKFields(context);
                newOpp.create();
                results.put("orderPaymentPreferenceId", newOpp.get("orderPaymentPreferenceId"));
            }

            return results;
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    /**
     * Generates a product requirement for the total cancelled quantity over all order items for each product
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> generateReqsFromCancelledPOItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");
        String facilityId = (String) context.get("facilityId");

        try {

            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderIdNotFound", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            if (! "PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "ProductErrorOrderNotPurchaseOrder", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            // Build a map of productId -> quantity cancelled over all order items
            Map<String, Object> productRequirementQuantities = new HashMap<String, Object>();
            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem");
            Iterator<GenericValue> oiit = orderItems.iterator();
            while (oiit.hasNext()) {
                GenericValue orderItem = oiit.next();
                if (! "PRODUCT_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) continue;

                // Get the cancelled quantity for the item
                BigDecimal orderItemCancelQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("cancelQuantity"))) {
                    orderItemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                }

                if (orderItemCancelQuantity.compareTo(BigDecimal.ZERO) <= 0) continue;

                String productId = orderItem.getString("productId");
                if (productRequirementQuantities.containsKey(productId)) {
                    orderItemCancelQuantity = orderItemCancelQuantity.add((BigDecimal) productRequirementQuantities.get(productId));
                }
                productRequirementQuantities.put(productId, orderItemCancelQuantity);

            }

            // Generate requirements for each of the product quantities
            Iterator<String> cqit = productRequirementQuantities.keySet().iterator();
            while (cqit.hasNext()) {
                String productId = cqit.next();
                BigDecimal requiredQuantity = (BigDecimal) productRequirementQuantities.get(productId);
                Map<String, Object> createRequirementResult = dispatcher.runSync("createRequirement", UtilMisc.<String, Object>toMap("requirementTypeId", "PRODUCT_REQUIREMENT", "facilityId", facilityId, "productId", productId, "quantity", requiredQuantity, "userLogin", userLogin));
                if (ServiceUtil.isError(createRequirementResult)) return createRequirementResult;
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException se) {
            Debug.logError(se, module);
            return ServiceUtil.returnError(se.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    /**
     * Cancels remaining (unreceived) quantities for items of an order. Does not consider received-but-rejected quantities.
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> cancelRemainingPurchaseOrderItems(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");

        try {

            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));

            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "OrderErrorOrderIdNotFound", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            if (! "PURCHASE_ORDER".equals(orderHeader.getString("orderTypeId"))) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderNotPurchaseOrder", UtilMisc.toMap("orderId", orderId), locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem");
            Iterator<GenericValue> oiit = orderItems.iterator();
            while (oiit.hasNext()) {
                GenericValue orderItem = oiit.next();
                if (! "PRODUCT_ORDER_ITEM".equals(orderItem.getString("orderItemTypeId"))) continue;

                // Get the ordered quantity for the item
                BigDecimal orderItemQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("quantity"))) {
                    orderItemQuantity = orderItem.getBigDecimal("quantity");
                }
                BigDecimal orderItemCancelQuantity = BigDecimal.ZERO;
                if (! UtilValidate.isEmpty(orderItem.get("cancelQuantity"))) {
                    orderItemCancelQuantity = orderItem.getBigDecimal("cancelQuantity");
                }

                // Get the received quantity for the order item - ignore the quantityRejected, since rejected items should be reordered
                List<GenericValue> shipmentReceipts = orderItem.getRelated("ShipmentReceipt");
                BigDecimal receivedQuantity = BigDecimal.ZERO;
                Iterator<GenericValue> srit = shipmentReceipts.iterator();
                while (srit.hasNext()) {
                    GenericValue shipmentReceipt = srit.next();
                    if (! UtilValidate.isEmpty(shipmentReceipt.get("quantityAccepted"))) {
                        receivedQuantity = receivedQuantity.add(shipmentReceipt.getBigDecimal("quantityAccepted"));
                    }
                }

                BigDecimal quantityToCancel = orderItemQuantity.subtract(orderItemCancelQuantity).subtract(receivedQuantity);
                if (quantityToCancel.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> cancelOrderItemResult = dispatcher.runSync("cancelOrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "cancelQuantity", quantityToCancel, "userLogin", userLogin));
                if (ServiceUtil.isError(cancelOrderItemResult)) return cancelOrderItemResult;
                }

                // If there's nothing to cancel, the item should be set to completed, if it isn't already
                orderItem.refresh();
                if ("ITEM_APPROVED".equals(orderItem.getString("statusId"))) {
                    Map<String, Object> changeOrderItemStatusResult = dispatcher.runSync("changeOrderItemStatus", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId"), "statusId", "ITEM_COMPLETED", "userLogin", userLogin));
                    if (ServiceUtil.isError(changeOrderItemStatusResult)) return changeOrderItemStatusResult;
                }
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException se) {
            Debug.logError(se, module);
            return ServiceUtil.returnError(se.getMessage());
        }

        return ServiceUtil.returnSuccess();
    }

    // create simple non-product order
    public static Map<String, Object> createSimpleNonProductSalesOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        String paymentMethodId = (String) context.get("paymentMethodId");
        String productStoreId = (String) context.get("productStoreId");
        String currency = (String) context.get("currency");
        String partyId = (String) context.get("partyId");
        Map<String, BigDecimal> itemMap = UtilGenerics.checkMap(context.get("itemMap"));

        ShoppingCart cart = new ShoppingCart(delegator, productStoreId, null, locale, currency);
        try {
            cart.setUserLogin(userLogin, dispatcher);
        } catch (CartItemModifyException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        cart.setOrderType("SALES_ORDER");
        cart.setOrderPartyId(partyId);

        Iterator<String> i = itemMap.keySet().iterator();
        while (i.hasNext()) {
            String item = i.next();
            BigDecimal price = itemMap.get(item);
            try {
                cart.addNonProductItem("BULK_ORDER_ITEM", item, null, price, BigDecimal.ONE, null, null, null, dispatcher);
            } catch (CartItemModifyException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        // set the payment method
        try {
            cart.addPayment(paymentMethodId);
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // save the order (new tx)
        Map<String, Object> createResp;
        try {
            createResp = dispatcher.runSync("createOrderFromShoppingCart", UtilMisc.toMap("shoppingCart", cart), 90, true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(createResp)) {
            return createResp;
        }

        // auth the order (new tx)
        Map<String, Object> authResp;
        try {
            authResp = dispatcher.runSync("callProcessOrderPayments", UtilMisc.toMap("shoppingCart", cart), 180, true);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (ServiceUtil.isError(authResp)) {
            return authResp;
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("orderId", createResp.get("orderId"));
        return result;
    }

    // generic method for creating an order from a shopping cart
    public static Map<String, Object> createOrderFromShoppingCart(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
        GenericValue userLogin = cart.getUserLogin();

        CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> createOrder = coh.createOrder(userLogin);
        if (ServiceUtil.isError(createOrder)) {
            return createOrder;
        }
        String orderId = (String) createOrder.get("orderId");

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("shoppingCart", cart);
        result.put("orderId", orderId);
        return result;
    }

    // generic method for processing an order's payment(s)
    public static Map<String, Object> callProcessOrderPayments(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        Transaction trans = null;
        try {
            // disable transaction procesing
            trans = TransactionUtil.suspend();

            // get the cart
            ShoppingCart cart = (ShoppingCart) context.get("shoppingCart");
            GenericValue userLogin = cart.getUserLogin();
            Boolean manualHold = (Boolean) context.get("manualHold");
            if (manualHold == null) {
                manualHold = Boolean.FALSE;
            }

            if (!"PURCHASE_ORDER".equals(cart.getOrderType())) {
                String productStoreId = cart.getProductStoreId();
                GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
                CheckOutHelper coh = new CheckOutHelper(dispatcher, delegator, cart);

                // process payment
                Map<String, Object> payResp;
                try {
                    payResp = coh.processPayment(productStore, userLogin, false, manualHold.booleanValue());
                } catch (GeneralException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(e.getMessage());
                }
                if (ServiceUtil.isError(payResp)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "OrderProcessOrderPayments", locale), null, null, payResp);
                }
            }

            return ServiceUtil.returnSuccess();
        } catch (GenericTransactionException e) {
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            // resume transaction
            try {
                TransactionUtil.resume(trans);
            } catch (GenericTransactionException e) {
                Debug.logWarning(e, e.getMessage(), module);
            }
        }
    }

    /**
     * Determines the total amount invoiced for a given order item over all invoices by totalling the item subtotal (via OrderItemBilling),
     *  any adjustments for that item (via OrderAdjustmentBilling), and the item's share of any order-level adjustments (that calculated
     *  by applying the percentage of the items total that the item represents to the order-level adjustments total (also via
     *  OrderAdjustmentBilling). Also returns the quantity invoiced for the item over all invoices, to aid in prorating.
     * @param dctx DispatchContext
     * @param context Map
     * @return Map
     */
    public static Map<String, Object> getOrderItemInvoicedAmountAndQuantity(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        String orderId = (String) context.get("orderId");
        String orderItemSeqId = (String) context.get("orderItemSeqId");

        GenericValue orderHeader = null;
        GenericValue orderItemToCheck = null;
        BigDecimal orderItemTotalValue = ZERO;
        BigDecimal invoicedQuantity = ZERO; // Quantity invoiced for the target order item
        try {

            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if (UtilValidate.isEmpty(orderHeader)) {
                String errorMessage = UtilProperties.getMessage(resource_error, 
                        "OrderErrorOrderIdNotFound", context, locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }
            orderItemToCheck = delegator.findByPrimaryKey("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
            if (UtilValidate.isEmpty(orderItemToCheck)) {
                String errorMessage = UtilProperties.getMessage(resource_error,
                        "OrderErrorOrderItemNotFound", context, locale);
                Debug.logError(errorMessage, module);
                return ServiceUtil.returnError(errorMessage);
            }

            BigDecimal orderItemsSubtotal = ZERO; // Aggregated value of order items, non-tax and non-shipping item-level adjustments
            BigDecimal invoicedTotal = ZERO; // Amount invoiced for the target order item
            BigDecimal itemAdjustments = ZERO; // Item-level tax- and shipping-adjustments

            // Aggregate the order items subtotal
            List<GenericValue> orderItems = orderHeader.getRelated("OrderItem", UtilMisc.toList("orderItemSeqId"));
            Iterator<GenericValue> oit = orderItems.iterator();
            while (oit.hasNext()) {
                GenericValue orderItem = oit.next();

                // Look at the orderItemBillings to discover the amount and quantity ever invoiced for this order item
                List<GenericValue> orderItemBillings = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId")));
                Iterator<GenericValue> oibit = orderItemBillings.iterator();
                while (oibit.hasNext()) {
                    GenericValue orderItemBilling = oibit.next();
                    BigDecimal quantity = orderItemBilling.getBigDecimal("quantity");
                    BigDecimal amount = orderItemBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                    if (UtilValidate.isEmpty(invoicedQuantity) || UtilValidate.isEmpty(amount)) continue;

                    // Add the item base amount to the subtotal
                    orderItemsSubtotal = orderItemsSubtotal.add(quantity.multiply(amount));

                    // If the item is the target order item, add the invoiced quantity and amount to their respective totals
                    if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {
                        invoicedQuantity = invoicedQuantity.add(quantity);
                        invoicedTotal = invoicedTotal.add(quantity.multiply(amount));
                    }
                }

                // Retrieve the adjustments for this item
                List<GenericValue> orderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItem.get("orderItemSeqId")));
                Iterator<GenericValue> oait = orderAdjustments.iterator();
                while (oait.hasNext()) {
                    GenericValue orderAdjustment = oait.next();
                    String orderAdjustmentTypeId = orderAdjustment.getString("orderAdjustmentTypeId");

                    // Look at the orderAdjustmentBillings to discove the amount ever invoiced for this order adjustment
                    List<GenericValue> orderAdjustmentBillings = delegator.findByAnd("OrderAdjustmentBilling", UtilMisc.toMap("orderAdjustmentId", orderAdjustment.get("orderAdjustmentId")));
                    Iterator<GenericValue> oabit = orderAdjustmentBillings.iterator();
                    while (oabit.hasNext()) {
                        GenericValue orderAjustmentBilling = oabit.next();
                        BigDecimal amount = orderAjustmentBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                        if (UtilValidate.isEmpty(amount)) continue;

                        if ("SALES_TAX".equals(orderAdjustmentTypeId) || "SHIPPING_CHARGES".equals(orderAdjustmentTypeId)) {
                            if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {

                                // Add tax- and shipping-adjustment amounts to the total adjustments for the target order item
                                itemAdjustments = itemAdjustments.add(amount);
                            }
                        } else {

                            // Add non-tax and non-shipping adjustment amounts to the order items subtotal
                            orderItemsSubtotal = orderItemsSubtotal.add(amount);
                            if (orderItemSeqId.equals(orderItem.get("orderItemSeqId"))) {

                                // If the item is the target order item, add non-tax and non-shipping adjustment amounts to the invoiced total
                                invoicedTotal = invoicedTotal.add(amount);
                            }
                        }
                    }
                }
            }

            // Total the order-header-level adjustments for the order
            BigDecimal orderHeaderAdjustmentsTotalValue = ZERO;
            List<GenericValue> orderHeaderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_"));
            Iterator<GenericValue> ohait = orderHeaderAdjustments.iterator();
            while (ohait.hasNext()) {
                GenericValue orderHeaderAdjustment = ohait.next();
                List<GenericValue> orderHeaderAdjustmentBillings = delegator.findByAnd("OrderAdjustmentBilling", UtilMisc.toMap("orderAdjustmentId", orderHeaderAdjustment.get("orderAdjustmentId")));
                Iterator<GenericValue> ohabit = orderHeaderAdjustmentBillings.iterator();
                while (ohabit.hasNext()) {
                    GenericValue orderHeaderAdjustmentBilling = ohabit.next();
                    BigDecimal amount = orderHeaderAdjustmentBilling.getBigDecimal("amount").setScale(orderDecimals, orderRounding);
                    if (UtilValidate.isEmpty(amount)) continue;
                    orderHeaderAdjustmentsTotalValue = orderHeaderAdjustmentsTotalValue.add(amount);
                }
            }

            // How much of the order-level adjustments total does the target order item represent? The assumption is: the same
            //  proportion of the adjustments as of the invoiced total for the item to the invoiced total for all items. These
            //  figures don't take tax- and shipping- adjustments into account, so as to be in accordance with the code in InvoiceServices
            BigDecimal invoicedAmountProportion = ZERO;
            if (orderItemsSubtotal.signum() != 0) {
                invoicedAmountProportion = invoicedTotal.divide(orderItemsSubtotal, 5, orderRounding);
            }
            BigDecimal orderItemHeaderAjustmentAmount = orderHeaderAdjustmentsTotalValue.multiply(invoicedAmountProportion);
            orderItemTotalValue = invoicedTotal.add(orderItemHeaderAjustmentAmount);

            // Add back the tax- and shipping- item-level adjustments for the order item
            orderItemTotalValue = orderItemTotalValue.add(itemAdjustments);

        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("invoicedAmount", orderItemTotalValue.setScale(orderDecimals, orderRounding));
        result.put("invoicedQuantity", invoicedQuantity.setScale(orderDecimals, orderRounding));
        return result;
    }

    public static Map<String, Object> setOrderPaymentStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        String orderPaymentPreferenceId = (String) context.get("orderPaymentPreferenceId");
        String changeReason = (String) context.get("changeReason");
        Locale locale = (Locale) context.get("locale");
        try {
            GenericValue orderPaymentPreference = delegator.findByPrimaryKey("OrderPaymentPreference", UtilMisc.toMap("orderPaymentPreferenceId", orderPaymentPreferenceId));
            String orderId = orderPaymentPreference.getString("orderId");
            String statusUserLogin = orderPaymentPreference.getString("createdByUserLogin");
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if (orderHeader == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                        "OrderErrorCouldNotChangeOrderStatusOrderCannotBeFound", locale));
            }
            String statusId = orderPaymentPreference.getString("statusId");
            if (Debug.verboseOn()) Debug.logVerbose("[OrderServices.setOrderPaymentStatus] : Setting Order Payment Status to : " + statusId, module);
            // create a order payment status
            GenericValue orderStatus = delegator.makeValue("OrderStatus");
            orderStatus.put("statusId", statusId);
            orderStatus.put("orderId", orderId);
            orderStatus.put("orderPaymentPreferenceId", orderPaymentPreferenceId);
            orderStatus.put("statusUserLogin", statusUserLogin);
            orderStatus.put("changeReason", changeReason);

            // Check that the status has actually changed before creating a new record
            List<GenericValue> previousStatusList = delegator.findByAnd("OrderStatus", UtilMisc.toMap("orderId", orderId, "orderPaymentPreferenceId", orderPaymentPreferenceId), UtilMisc.toList("-statusDatetime"));
            GenericValue previousStatus = EntityUtil.getFirst(previousStatusList);
            if (previousStatus != null) {
                // Temporarily set some values on the new status so that we can do an equals() check
                orderStatus.put("orderStatusId", previousStatus.get("orderStatusId"));
                orderStatus.put("statusDatetime", previousStatus.get("statusDatetime"));
                if (orderStatus.equals(previousStatus)) {
                    // Status is the same, return without creating
                    return ServiceUtil.returnSuccess();
                }
            }
            orderStatus.put("orderStatusId", delegator.getNextSeqId("OrderStatus"));
            orderStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
            orderStatus.create();

        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,
                    "OrderErrorCouldNotChangeOrderStatus", locale) + e.getMessage() + ").");
        }

        return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> runSubscriptionAutoReorders(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        int count = 0;
        Map<String, Object> result = null;

        boolean beganTransaction = false;
        try {
            beganTransaction = TransactionUtil.begin();

            List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("automaticExtend", EntityOperator.EQUALS, "Y"),
                    EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null),
                    EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
            EntityCondition cond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
            EntityListIterator eli = null;
            eli = delegator.find("Subscription", cond, null, null, null, null);

            if (eli != null) {
                GenericValue subscription;
                while (((subscription = eli.next()) != null)) {

                    Calendar endDate = Calendar.getInstance();
                    endDate.setTime(UtilDateTime.nowTimestamp());
                    //check if the thruedate - cancel period (if provided) is earlier than todays date
                    int field = Calendar.MONTH;
                    if (subscription.get("canclAutmExtTime") != null && subscription.get("canclAutmExtTimeUomId") != null) {
                        if ("TF_day".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.DAY_OF_YEAR;
                        } else if ("TF_wk".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.WEEK_OF_YEAR;
                        } else if ("TF_mon".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.MONTH;
                        } else if ("TF_yr".equals(subscription.getString("canclAutmExtTimeUomId"))) {
                            field = Calendar.YEAR;
                        } else {
                            Debug.logWarning("Don't know anything about useTimeUomId [" + subscription.getString("canclAutmExtTimeUomId") + "], defaulting to month", module);
                        }

                        endDate.add(field, Integer.valueOf(subscription.getString("canclAutmExtTime")).intValue());
                    }

                    Calendar endDateSubscription = Calendar.getInstance();
                    endDateSubscription.setTime(subscription.getTimestamp("thruDate"));

                    if (endDate.before(endDateSubscription)) {
                        // nor expired yet.....
                        continue;
                    }

                    result = dispatcher.runSync("loadCartFromOrder", UtilMisc.toMap("orderId", subscription.get("orderId"), "userLogin", userLogin));
                    ShoppingCart cart = (ShoppingCart) result.get("shoppingCart");

                    // only keep the orderitem with the related product.
                    List<ShoppingCartItem> cartItems = cart.items();
                    Iterator<ShoppingCartItem> ci = cartItems.iterator();
                    while (ci.hasNext()) {
                        ShoppingCartItem shoppingCartItem = ci.next();
                        if (!subscription.get("productId").equals(shoppingCartItem.getProductId())) {
                            cart.removeCartItem(shoppingCartItem, dispatcher);
                        }
                    }

                    CheckOutHelper helper = new CheckOutHelper(dispatcher, delegator, cart);

                    // store the order
                    Map<String, Object> createResp = helper.createOrder(userLogin);
                    if (createResp != null && ServiceUtil.isError(createResp)) {
                        Debug.logError("Cannot create order for shopping list - " + subscription, module);
                    } else {
                        String orderId = (String) createResp.get("orderId");

                        // authorize the payments
                        Map<String, Object> payRes = null;
                        try {
                            payRes = helper.processPayment(ProductStoreWorker.getProductStore(cart.getProductStoreId(), delegator), userLogin);
                        } catch (GeneralException e) {
                            Debug.logError(e, module);
                        }

                        if (payRes != null && ServiceUtil.isError(payRes)) {
                            Debug.logError("Payment processing problems with shopping list - " + subscription, module);
                        }

                        // remove the automatic extension flag
                        subscription.put("automaticExtend", "N");
                        subscription.store();

                        // send notification
                        dispatcher.runAsync("sendOrderPayRetryNotification", UtilMisc.toMap("orderId", orderId));
                        count++;
                    }
                }
                eli.close();
            }

        } catch (GenericServiceException e) {
            Debug.logError("Could call service to create cart", module);
            return ServiceUtil.returnError(e.toString());
        } catch (CartItemModifyException e) {
            Debug.logError("Could not modify cart: " + e.toString(), module);
            return ServiceUtil.returnError(e.toString());
        } catch (GenericEntityException e) {
            try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error creating subscription auto-reorders", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "[Delegator] Could not rollback transaction: " + e2.toString(), module);
            }
            Debug.logError(e, "Error while creating new shopping list based automatic reorder" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "OrderShoppingListCreationError", UtilMisc.toMap("errorString", e.toString()), locale));
        } finally {
            try {
                // only commit the transaction if we started one... this will throw an exception if it fails
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not commit transaction for creating new shopping list based automatic reorder", module);
            }
        }
        return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, 
                "OrderRunSubscriptionAutoReorders", UtilMisc.toMap("count", count), locale));
    }

    public static Map<String, Object> setShippingInstructions(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String shippingInstructions = (String) context.get("shippingInstructions");
        try {
            GenericValue orderItemShipGroup = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId,"shipGroupSeqId",shipGroupSeqId)));
            orderItemShipGroup.set("shippingInstructions", shippingInstructions);
            orderItemShipGroup.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> setGiftMessage(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        String shipGroupSeqId = (String) context.get("shipGroupSeqId");
        String giftMessage = (String) context.get("giftMessage");
        try {
            GenericValue orderItemShipGroup = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroup", UtilMisc.toMap("orderId", orderId,"shipGroupSeqId",shipGroupSeqId)));
            orderItemShipGroup.set("giftMessage", giftMessage);
            orderItemShipGroup.set("isGift", "Y");
            orderItemShipGroup.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> createAlsoBoughtProductAssocs(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        // All orders with an entryDate > orderEntryFromDateTime will be processed
        Timestamp orderEntryFromDateTime = (Timestamp) context.get("orderEntryFromDateTime");
        // If true all orders ever created will be processed and any pre-existing ALSO_BOUGHT ProductAssocs will be expired
        boolean processAllOrders = context.get("processAllOrders") == null ? false : (Boolean) context.get("processAllOrders");
        if (orderEntryFromDateTime == null && !processAllOrders) {
            // No from date supplied, check to see when this service last ran and use the startDateTime
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap("statusId", "SERVICE_FINISHED", "serviceName", "createAlsoBoughtProductAssocs"));
            EntityFindOptions efo = new EntityFindOptions();
            efo.setMaxRows(1);
            try {
                GenericValue lastRunJobSandbox = EntityUtil.getFirst(delegator.findList("JobSandbox", cond, null, UtilMisc.toList("startDateTime DESC"), efo, false));
                if (lastRunJobSandbox != null) {
                    orderEntryFromDateTime = lastRunJobSandbox.getTimestamp("startDateTime");
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (orderEntryFromDateTime == null) {
                // Still null, process all orders
                processAllOrders = true;
            }
        }
        if (processAllOrders) {
            // Expire any pre-existing ALSO_BOUGHT ProductAssocs in preparation for reprocessing
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("productAssocTypeId", "ALSO_BOUGHT"),
                    EntityCondition.makeConditionDate("fromDate", "thruDate")
           ));
            try {
                delegator.storeByCondition("ProductAssoc", UtilMisc.toMap("thruDate", UtilDateTime.nowTimestamp()), cond);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        EntityListIterator eli = null;
        try {
            List<EntityExpr> orderCondList = UtilMisc.toList(EntityCondition.makeCondition("orderTypeId", "SALES_ORDER"));
            if (!processAllOrders && orderEntryFromDateTime != null) {
                orderCondList.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN, orderEntryFromDateTime));
            }
            EntityCondition cond = EntityCondition.makeCondition(orderCondList);
            eli = delegator.find("OrderHeader", cond, null, null, UtilMisc.toList("entryDate ASC"), null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (eli != null) {
            GenericValue orderHeader = null;
            while ((orderHeader = eli.next()) != null) {
                //Map<String, Object> svcIn = FastMap.newInstance();
                Map<String, Object> svcIn = new HashMap<String, Object>();
                
                svcIn.put("userLogin", context.get("userLogin"));
                svcIn.put("orderId", orderHeader.get("orderId"));
                try {
                    dispatcher.runSync("createAlsoBoughtProductAssocsForOrder", svcIn);
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                }
            }
            try {
                eli.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        return ServiceUtil.returnSuccess();
    }
    
    
    public static Map<String, Object> updateSlot(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
       String orderId = (String) context.get("orderId");
        String partyId = (String) context.get("createdBy");
        String slot=(String) context.get("slotId");
        GenericValue gv1=delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId",partyId));
        List<EntityCondition> Condn=FastList.newInstance();
        if(UtilValidate.isNotEmpty(gv1))
        Condn.add( EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,gv1.getString("partyId")));
        if(UtilValidate.isNotEmpty(slot))
        	Condn.add( EntityCondition.makeCondition("slotId",EntityOperator.EQUALS,slot));
        Condn.add( EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_REQUESTED"));
        GenericValue gv=EntityUtil.getFirst(delegator.findList("OrderSlot", EntityCondition.makeCondition(Condn,EntityOperator.AND), null, UtilMisc.toList("-createdStamp"),null,false));
        List<GenericValue> orderRoles = delegator.findByAnd("OrderRole", UtilMisc.toMap("orderId", orderId,"roleTypeId","PLACING_CUSTOMER"));
        if(UtilValidate.isNotEmpty(gv) && UtilValidate.isNotEmpty(orderRoles))
      {
        gv.set("slotStatus","SLOT_ACCEPTED");
        gv.set("orderId",orderId);
        gv.set("partyId",EntityUtil.getFirst(orderRoles).getString("partyId"));
        gv.store();
      }
        
       
      
        return ServiceUtil.returnSuccess();
        
    }
    public static Map<String, Object> updatePinId(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
       String orderId = (String) context.get("orderId");
    	GenericValue gvOrder=delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",orderId));
    	if(UtilValidate.isNotEmpty(gvOrder))
    	{
    		 GenericValue productStore = ProductStoreWorker.getProductStore(gvOrder.getString("productStoreId"), delegator);
    		// int loopCount=productStore.getInteger("pinIdLoopCount");
    		 int digit=(int)productStore.getDouble("pinIdDigit").doubleValue();
    		 String min="1";
    		 String max="9";
    		 
    		 min=StringUtils.rightPad(min,digit, '0');
    		 max=StringUtils.rightPad(max,digit, '9');
    		 
    		 	Random r = new Random();
    	        String pinId=String.valueOf(r.nextInt(Integer.parseInt(max) -Integer.parseInt(min)  + 1)+Integer.parseInt(min));
    	        int count=0;
    	        pinId=getPinId(delegator,pinId,count,gvOrder.getString("productStoreId"));
    	       gvOrder.set("pinId",pinId);
    	       gvOrder.store();
    		
    	}
    	
    	
    	return ServiceUtil.returnSuccess();
    }
 public static Map<String, Object>  updateSlotStatus(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
       String orderId = (String) context.get("orderId");
       GenericValue gv= EntityUtil.getFirst(delegator.findList("OrderSlot",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId),null,null,null,false));
        
        gv.set("slotStatus","SLOT_COMPLETED");
        gv.store();
   return ServiceUtil.returnSuccess();
        
    }
 public static Map<String, Object>  deleteOrderSlot(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException {
 	
     LocalDispatcher dispatcher = dctx.getDispatcher();
     Delegator delegator = dctx.getDelegator();
    String orderId = (String) context.get("orderId");
    GenericValue gv= EntityUtil.getFirst(delegator.findList("OrderSlot",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId),null,null,null,false));
    if(UtilValidate.isNotEmpty(gv))
    gv.remove();
     GenericValue orderGv =delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",orderId));
     if(UtilValidate.isNotEmpty(gv))
     {
     orderGv.set("slot", null);
     orderGv.set("deliveryDate", null);
     orderGv.store();
     }
     return ServiceUtil.returnSuccess();
     
 }

    public static Map<String, Object> createAlsoBoughtProductAssocsForOrder(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
        List<GenericValue> orderItems = orh.getOrderItems();
        // In order to improve efficiency a little bit, we will always create the ProductAssoc records
        // with productId < productIdTo when the two are compared.  This way when checking for an existing
        // record we don't have to check both possible combinations of productIds
        TreeSet<String> productIdSet = new TreeSet<String>();
        if (orderItems != null) {
            for (GenericValue orderItem : orderItems) {
                String productId = orderItem.getString("productId");
                if (productId != null) {
                    GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
                    if (parentProduct != null) productId = parentProduct.getString("productId");
                    productIdSet.add(productId);
                }
            }
        }
        TreeSet<String> productIdToSet = new TreeSet<String>(productIdSet);
        for (String productId : productIdSet) {
            productIdToSet.remove(productId);
            for (String productIdTo : productIdToSet) {
                EntityCondition cond = EntityCondition.makeCondition(
                        UtilMisc.toList(
                                EntityCondition.makeCondition("productId", productId),
                                EntityCondition.makeCondition("productIdTo", productIdTo),
                                EntityCondition.makeCondition("productAssocTypeId", "ALSO_BOUGHT"),
                                EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
                                EntityCondition.makeCondition("thruDate", null)
                       )
               );
                GenericValue existingProductAssoc = null;
                try {
                    // No point in using the cache because of the filterByDateExpr
                    existingProductAssoc = EntityUtil.getFirst(delegator.findList("ProductAssoc", cond, null, UtilMisc.toList("fromDate DESC"), null, false));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                try {
                    if (existingProductAssoc != null) {
                        BigDecimal newQuantity = existingProductAssoc.getBigDecimal("quantity");
                        if (newQuantity == null || newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                            newQuantity = BigDecimal.ZERO;
                        }
                        newQuantity = newQuantity.add(BigDecimal.ONE);
                        ModelService updateProductAssoc = dctx.getModelService("updateProductAssoc");
                        Map<String, Object> updateCtx = updateProductAssoc.makeValid(context, ModelService.IN_PARAM, true, null);
                        updateCtx.putAll(updateProductAssoc.makeValid(existingProductAssoc, ModelService.IN_PARAM));
                        updateCtx.put("quantity", newQuantity);
                        dispatcher.runSync("updateProductAssoc", updateCtx);
                    } else {
                       // Map<String, Object> createCtx = FastMap.newInstance();
                        Map<String, Object> createCtx = new HashMap<String, Object>();
                        createCtx.put("userLogin", context.get("userLogin"));
                        createCtx.put("productId", productId);
                        createCtx.put("productIdTo", productIdTo);
                        createCtx.put("productAssocTypeId", "ALSO_BOUGHT");
                        createCtx.put("fromDate", UtilDateTime.nowTimestamp());
                        createCtx.put("quantity", BigDecimal.ONE);
                        dispatcher.runSync("createProductAssoc", createCtx);
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, module);
                }
            }
        }

        return ServiceUtil.returnSuccess();
    }
    
    //Blocking the slot
    
    
    public static Map<String, Object> AutoBlockSlot(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        return ServiceUtil.returnSuccess();
    }
    
    public static Map<String ,Object> sendOrderSms(DispatchContext ctx, Map context) {
		Delegator delegator = ctx.getDelegator();
		String orderId = (String) context.get("orderId");
		String statusId = (String) context.get("statusId");
		List<String> orderStatus = UtilMisc.toList("ORDER_APPROVED","ORDER_DISPATCHED","ORDER_COMPLETED");
		if(UtilValidate.isNotEmpty(statusId) && orderStatus.contains(statusId))
		{
			GenericValue order = null;
			try {
				order = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId",orderId));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!isGiftCard(delegator, order) && UtilValidate.isNotEmpty(order.getString("pinId")))
			{
				String partyId = customerPartyIdFromOrder(delegator, order);
				String mobileNumber = custMobileNumberFromOrder(delegator, partyId);
				if(UtilValidate.isNotEmpty(mobileNumber) && mobileNumber.length() >= 10)
				{
					String name = PartyHelper.getPartyName(delegator, partyId, false);
					
					String message = null;
					
					if("ORDER_APPROVED".equals(statusId))
					{
						message = prepareOrderApprovedMessage(order, name);
					}else if("ORDER_DISPATCHED".equals(statusId))
					{
						String slot = order.getString("slot");
						if(UtilValidate.isNotEmpty(slot))
						{
							List<GenericValue> orderSlots = null;
							try {
								orderSlots = delegator.findList("OrderSlotType",
										EntityCondition.makeCondition("slotType",slot), UtilMisc.toSet("slotTiming"), null, null, true);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(UtilValidate.isNotEmpty(orderSlots))
							{
								GenericValue slotTim = EntityUtil.getFirst(orderSlots);
								if(UtilValidate.isNotEmpty(slotTim.getString("slotTiming")))
									slot = slotTim.getString("slotTiming");
							}
						}
						message = prepareOrderInTransitMessage(order, slot);
					}else if("ORDER_COMPLETED".equals(statusId))
					{
						message = prepareOrderFulfilledMessage(order, name);
					}
					
					Map messageData = new HashMap();
				    messageData.put("mobileNumber", mobileNumber);
				    messageData.put("message", message);
				    
				    //System.out.println("messageData       "+messageData);
				    
				    PartyServices.sendSms(messageData);
				}
			}
		}
		return ServiceUtil.returnSuccess();
	}
    public static boolean isGiftCard(Delegator delegator, GenericValue order){
    	List<GenericValue> orderItems = null;
		try {
			orderItems = delegator.findList("OrderItem", 
					EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,order.getString("orderId")), null, null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	if(UtilValidate.isNotEmpty(orderItems))
		for(GenericValue orderItem : orderItems)
		{
			GenericValue product = null;
			try {
				product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", orderItem.getString("productId")));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		 	if(UtilValidate.isNotEmpty(product) && product.getString("productTypeId").equalsIgnoreCase("DIGITAL_GOOD"))
   		 		return true;
   		}
    	return false;
    }
		
    public static String customerPartyIdFromOrder(Delegator delegator, GenericValue order){
    	String createdBy = order.getString("createdBy");
    	GenericValue userLogin = null;
		try {
			userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", createdBy));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(userLogin))
			return userLogin.getString("partyId");
		  
		return null;
    }
    public static String custMobileNumberFromOrder(Delegator delegator, String partyId){
    	//Map<String, Object> svcCtx = FastMap.newInstance();
    	Map<String, Object> svcCtx = new HashMap<String, Object>();
    	
        svcCtx.put("partyId", partyId);
        try {
        	List<GenericValue>  PartyTelecomNumbers = delegator.findByAnd("PartyAndTelecomNumber", svcCtx);
            List<GenericValue> contactMcList = EntityUtil.filterByDate(PartyTelecomNumbers);
             if (UtilValidate.isNotEmpty(contactMcList)) {
                GenericValue PartyTelecomNumber = contactMcList.get(0); // There is  only one phone number (contactMechPurposeTypeId == "PHONE_HOME")
                return PartyTelecomNumber.getString("contactNumber");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return null;
    }
    public static String prepareOrderApprovedMessage(GenericValue order, String name){
    	String message = UtilProperties.getPropertyValue("general.properties","orderapproved.processing.sms");
    	  
		if(UtilValidate.isNotEmpty(name))
			message = message.replaceAll("<Customer>", name);
	    else
	    	message = message.replaceAll("<Customer>", "");
		
		if(UtilValidate.isNotEmpty(order.getString("orderId")))
			message = message.replaceAll("<OrderId>", order.getString("orderId"));
	    else
	    	message = message.replaceAll("<OrderId>", "");
		
		if(UtilValidate.isNotEmpty(order.getString("pinId")))
			message = message.replaceAll("<pinId>", order.getString("pinId"));
	    else
	    	message = message.replaceAll("<pinId>", "");
		
		return message;
    }
    
    public static String prepareOrderInTransitMessage(GenericValue order,String slotTiming){
			String message = UtilProperties.getPropertyValue("general.properties","orderDispatched.processing.sms");
			
			if(UtilValidate.isNotEmpty(order.getString("orderId")))
				message = message.replaceAll("<OrderId>", order.getString("orderId"));
		    else
		    	message = message.replaceAll("<OrderId>", "");
			
			if(UtilValidate.isNotEmpty(slotTiming))
				message = message.replaceAll("<slotTiming>", slotTiming);
			else
				message = message.replaceAll("<slotTiming>", "");
			
			return message;
    }
    
    public static String prepareOrderFulfilledMessage(GenericValue order, String name){
		String message = UtilProperties.getPropertyValue("general.properties","orderFulFilled.processing.sms");
		
		return message;
}
    
    
    /** Service for creating favourite List */
public static Map generateFavouriteList(DispatchContext ctx, Map context) {
    Delegator delegator = ctx.getDelegator();
    LocalDispatcher dispatcher = ctx.getDispatcher();
    String orderId = (String) context.get("orderId");
    String productStoreId = (String) context.get("productStoreId");
    String partyId = null;
    String userLoginId = null;
    String shoppingListId = null;
    Map<String, Object> serviceResultCreateList = null;
    Map<String, Object> serviceResultAddItem = null;
    GenericValue userLogin = (GenericValue)context.get("userLogin");
    List<GenericValue> shoppingListDetails = null;
    if(UtilValidate.isNotEmpty(userLogin))
    {
    	partyId = userLogin.getString("partyId");
    	userLoginId =  userLogin.getString("userLoginId");
    }
    
    try {
    	if(UtilValidate.isNotEmpty(partyId)){
    	EntityConditionList condition = EntityCondition.makeCondition( UtilMisc.toList(
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
				EntityCondition.makeCondition("shoppingListTypeId", EntityOperator.EQUALS, "SLT_FAV_LIST_AUTO")
                ), EntityOperator.AND);
    	shoppingListDetails = delegator.findList("ShoppingList", condition, UtilMisc.toSet("shoppingListId"), null, null, false);
    	if (UtilValidate.isEmpty(shoppingListDetails)) {
    		Map<String, Object> ctxs = UtilMisc.<String, Object>toMap("userLogin", userLogin,"partyId", partyId, "productStoreId", "9000");
            ctxs.put("listName", "Frequent Purchase");
            ctxs.put("shoppingListTypeId", "SLT_FAV_LIST_AUTO");
            ctxs.put("description", "Frequent Purchase");
            serviceResultCreateList = dispatcher.runSync("createFavouriteList", ctxs);
            shoppingListId = (String) serviceResultCreateList.get("shoppingListId");
    	}else{
    		Iterator oiidet = shoppingListDetails.iterator();
        	while (oiidet.hasNext()) {
                GenericValue orderItemDet = (GenericValue) oiidet.next();
                shoppingListId = orderItemDet.getString("shoppingListId");
            }
    	}
    	}
    	}catch (GenericEntityException e) {
        Debug.logError(e, "Cannot get OrderItem records", module);
    }
    catch (GenericServiceException e) {
        Debug.logError(e, "Cannot get OrderItem records", module);
    }
    List<GenericValue> orderItems = null;
    List<GenericValue> items = null;
    try {
    	items = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
    	Timestamp endday=UtilDateTime.nowTimestamp();
    	Timestamp startday = UtilDateTime.addDaysToTimestamp(endday, -3);
    	Set fieldToSelect = new HashSet();
    	fieldToSelect.add("quantity");
    	fieldToSelect.add("productId");
    	List orderby = new ArrayList();
    	orderby.add("productId");
    	EntityConditionList conditions = EntityCondition.makeCondition( UtilMisc.toList(
				EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLoginId),
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"),
				EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startday),
				EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, endday)
                ), EntityOperator.AND);
    	orderItems = delegator.findList("OrderItem", conditions, fieldToSelect, orderby, null, false);
    } catch (GenericEntityException e) {
        Debug.logError(e, "Cannot get OrderItem records", module);
    }
    Map data = new HashMap();
    if (UtilValidate.isNotEmpty(items)) {
    	Iterator oi = items.iterator();
    	while (oi.hasNext()) {
            GenericValue orderItm = (GenericValue) oi.next();
            String productId = orderItm.getString("productId");
            BigDecimal quantity = orderItm.getBigDecimal("quantity");
            
            if(UtilValidate.isNotEmpty(data.get(productId)) && data.containsKey(productId)){
            	quantity = quantity.add((BigDecimal) data.get(productId));
            	data.put(productId, quantity);
            }
            else
            	data.put(productId, quantity);
        }
    }
    if (UtilValidate.isNotEmpty(orderItems)) {
    	Iterator oii = orderItems.iterator();
    	while (oii.hasNext()) {
            GenericValue orderItem = (GenericValue) oii.next();
            String productId = orderItem.getString("productId");
            BigDecimal quantity = orderItem.getBigDecimal("quantity");
            
            if(UtilValidate.isNotEmpty(data.get(productId)) && data.containsKey(productId)){
            	quantity = quantity.add((BigDecimal) data.get(productId));
            	data.put(productId, quantity);
            }
            else
            	data.put(productId, quantity);
        }
    }
    Set<String> keys = data.keySet();
    for(String key : keys){
    	BigDecimal quantity = (BigDecimal)data.get(key);
	    if (quantity.intValue() > 1)
	    {
	    	try {
	            Map<String, Object> ctxa = UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", key, "quantity", new BigDecimal(1));
	            serviceResultAddItem = dispatcher.runSync("createShoppingListItem", ctxa);
	        } catch (GenericServiceException e) {
	            Debug.logError(e, "Problems creating ShoppingList item entity", module);
	        }
	    }
    }
    
	return ServiceUtil.returnSuccess();
    }
    
    
	/*
	 * @Author Asit
	 * 
	 * Generate the coupon code for gift card .
	 * 
	 */
	
	public static Map generateCouponForGiftCard(DispatchContext ctx, Map context) {
       Delegator delegator = ctx.getDelegator();
       LocalDispatcher dispatcher = ctx.getDispatcher();
       String orderId=(String) context.get("orderId");
       GenericValue orderItemRes=null;
       try {
		List orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId),null,null,null,false);
		
		Iterator itr=orderItems.iterator();
		
		while(itr.hasNext())
		{
			orderItemRes=(GenericValue)itr.next();
		}
		String recipientEmailId=orderItemRes.getString("recipientEmailId");
		String recipientMobileNum=orderItemRes.getString("recipientMobileNum");
		String recipientName=orderItemRes.getString("recipientName");
		String recipientMessage=orderItemRes.getString("recipientMessage");
       if(UtilValidate.isNotEmpty(recipientEmailId) && recipientEmailId!=null)
       {
    	   promoCode(delegator, dispatcher, (GenericValue) context.get("userLogin") , (String) context.get("orderId"), orderItemRes);
             
       }
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
       return ServiceUtil.returnSuccess();
	}
	
	/*
	 * @Author Ajaya
	 * 
	 * Change the order status from transit to ful fill for gift vouchers
	 * 
	 */
	
	public static Map fulfillGiftVoucher(Delegator delegator, LocalDispatcher dispatcher,String  orderId) {
       GenericValue orderItemRes=null;
       try {
    		   List conditionList = new ArrayList();
        	   conditionList.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId));
        	   conditionList.add(EntityCondition.makeCondition("recipientEmailId",EntityOperator.NOT_EQUAL,null));
        	   conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"),
        			   										EntityOperator.LIKE,EntityFunction.UPPER("%GIFTCARD%".toUpperCase())));
        	   
        	   List orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(conditionList,EntityOperator.AND),
    																						UtilMisc.toSet("recipientEmailId"),null,null,false);
        	   
        	   if(UtilValidate.isNotEmpty(orderItems))
        	   {
        		   orderItemRes = EntityUtil.getFirst(orderItems);
        		   if(UtilValidate.isNotEmpty(orderItemRes) && UtilValidate.isNotEmpty(orderItemRes.getString("recipientEmailId")))
        		   {
        			    GenericValue userLogin = null;
        				try {
        				userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        				} catch (GenericEntityException e) {
        				Debug.logError(e, "Cannot get UserLogin for: system ; cannot continue", module);
        				// request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("AccountingErrorUiLabels", "payback.problemsGettingAuthenticationUser", UtilHttp.getLocale(request)));
        				// return "error";
        				}
        			   Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("orderId", orderId, "statusId", "ORDER_COMPLETED", "userLogin", userLogin);
                       Map<String, Object> newSttsResult = null;
                       try {
                           newSttsResult = dispatcher.runSync("changeOrderStatus", serviceContext);
                       } catch (GenericServiceException e) {
                           Debug.logError(e, "Problem calling the changeOrderStatus service", module);
                       }
        		   }
        	   }
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
       return ServiceUtil.returnSuccess();
	}
	
	public static void promoCode(Delegator delegator ,LocalDispatcher dispatcher ,
			GenericValue userLogin, String orderId, GenericValue orderItemRes){

		GenericValue orderHeader = null;
		List<GenericValue> productPrice =null;
		String productId=orderItemRes.getString("productId");
		BigDecimal quantity=BigDecimal.ZERO;
		BigDecimal productAmount=BigDecimal.ZERO;
		GenericValue genAmount=null;
		BigDecimal price=BigDecimal.ZERO;
		if(UtilValidate.isNotEmpty(productId) && productId.contains("GIFTCARD-"));
		{
			 quantity=orderItemRes.getBigDecimal("quantity");
			try {
				
				productPrice = delegator.findList("ProductPrice", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId), null,null,null,false);
				
				if(UtilValidate.isNotEmpty(productPrice)){
					
					Iterator itr=productPrice.iterator();
					while(itr.hasNext())
					{
						genAmount=(GenericValue)itr.next();
						
					}
					productAmount=genAmount.getBigDecimal("price");
				    price=productAmount.multiply(quantity);
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (UtilValidate.isNotEmpty(orderId)) {
		try {
		orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",orderId));
		} catch(Exception e) {
		Debug.logError(e, "Cannot get OrderHeader for : " + orderId, module);
		//e.printStackTrace();
		}
		}
		
		if(UtilValidate.isEmpty(orderHeader)) return;
		
		GenericValue orderPaymentPreference = null;
		List orderPaymentPreferences = null;
		try {
		orderPaymentPreferences = delegator.findList("OrderPaymentPreference", 
		EntityCondition.makeCondition(UtilMisc.toList(
		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"),
		EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId))),
		null,null,null,true);
		} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		Debug.logError(e1, "Cannot get OrderPaymentPreference for : " + orderId, module);
		//e1.printStackTrace();
		}
		
		if(UtilValidate.isEmpty(orderPaymentPreferences)) return;
		
		orderPaymentPreference = (GenericValue) orderPaymentPreferences.get(0);
		
		BigDecimal amount = orderPaymentPreference.getBigDecimal("maxAmount");
		
		String userLoginId="system";
		try {
		userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
		} catch (GenericEntityException e) {
		Debug.logError(e, "Cannot get UserLogin for: " + userLoginId + "; cannot continue", module);
		// request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("AccountingErrorUiLabels", "payback.problemsGettingAuthenticationUser", UtilHttp.getLocale(request)));
		// return "error";
		}
		
		String productStoreId = orderHeader.getString("productStoreId");
		if(UtilValidate.isEmpty(productStoreId)) return;
		GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
		
		Long expPartPaymentCouponAfterInDays = null;
		if(UtilValidate.isNotEmpty(productStore)){
		expPartPaymentCouponAfterInDays = productStore.getLong("expPartPaymentCouponAfterInDays");
		
		}
		
		String productPromoId = null;
		String promoName = "Gift Voucher Coupon for "+orderId;
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("promoName",promoName,"userEntered","Y","showToCustomer","N",
		"requireCode","Y","useLimitPerOrder",new Long(1),"useLimitPerCustomer",new Long(1),
		"useLimitPerPromotion",new Long(1),"userLogin",userLogin);
		Map statusResult = dispatcher.runSync("createProductPromo", statusFields);
		
		
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product Promo for # "+orderId, module);
		}
		
		if(UtilValidate.isNotEmpty(statusResult))
		productPromoId = (String) statusResult.get("productPromoId");
		
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product promo rule for # "+productPromoId, module);
		// return "error";
		}
		
		if(UtilValidate.isEmpty(productPromoId)) return;
		
		String ruleName = orderId ;
		String productPromoRuleId = null;
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoId",productPromoId,"ruleName",ruleName,
		"userLogin",userLogin);
		Map statusResult = dispatcher.runSync("createProductPromoRule", statusFields);
		
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product promo rule for # "+productPromoId, module);
		}
		
		if(UtilValidate.isNotEmpty(statusResult))
		productPromoRuleId = (String) statusResult.get("productPromoRuleId");
		
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product promo rule for # "+productPromoId, module);
		// return "error";
		}
		
		GenericValue productPromoRule = null;
		try {
		productPromoRule = delegator.findOne("ProductPromoRule", UtilMisc.toMap("productPromoId",productPromoId,"productPromoRuleId",productPromoRuleId), true);
		} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		if(UtilValidate.isEmpty(productPromoRule))return;
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoId",productPromoId,"productPromoRuleId",productPromoRuleId,
		"inputParamEnumId","PPIP_ORDER_TOTAL",
		"operatorEnumId","PPC_GTE","condValue","1","userLogin",userLogin);
		
		Map statusResult = dispatcher.runSync("createProductPromoCond", statusFields);
		
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product promo rule for # "+productPromoId, module);
		}
		
		if(UtilValidate.isNotEmpty(statusResult))
		productPromoRuleId = (String) statusResult.get("productPromoRuleId");
		
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product promo rule for # "+productPromoId, module);
		// return "error";
		}
		
		OrderReadHelper orh = new OrderReadHelper(delegator, orderId);
		String orderTypeId = orh.getOrderTypeId();
		String partyId = null;
		
		String emailId=orderPaymentPreference.getString("createdByUserLogin");
		GenericValue orderParty = orh.getEndUserParty();
		if (UtilValidate.isEmpty(orderParty)) {
		orderParty = orh.getPlacingParty();
		}
		if (UtilValidate.isNotEmpty(orderParty)) {
		partyId = orderParty.getString("partyId");
		}
		
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", "101",
		"productPromoActionEnumId","PROMO_ORDER_AMOUNT","orderAdjustmentTypeId","PROMOTION_ADJUSTMENT",
		"amount",price,"userLogin",userLogin);
		Map statusResult = dispatcher.runSync("createProductPromoAction", statusFields);
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product promo actions for # 10210", module);
		}
		
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product promo actions for # 10210", module);
		// return "error";
		}
		
		
		//productPromoCodeId
		
		GenericValue productPromoCode = null;
		String productPromoCodeId = orderId;
		
		
		Timestamp couponExpireDate = UtilDateTime.nowTimestamp();
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoCodeId",productPromoCodeId,"productPromoId",productPromoId ,
		"userEntered", "Y","requireEmailOrParty","Y","useLimitPerCode",new Long(1),"useLimitPerCustomer",new Long(1),
		"userLogin",userLogin);
		
		if(UtilValidate.isNotEmpty(expPartPaymentCouponAfterInDays) && UtilValidate.isNonnegativeInteger(expPartPaymentCouponAfterInDays+""))
		{
		int expireAfter = expPartPaymentCouponAfterInDays.intValue();
		Timestamp todaysDateStart = UtilDateTime.nowTimestamp();
		if(expireAfter != 0)
		todaysDateStart = UtilDateTime.getDayStart(todaysDateStart);
		
		statusFields.put("thruDate", UtilDateTime.addDaysToTimestamp(todaysDateStart, expireAfter));
		}
		
		Map statusResult = dispatcher.runSync("createProductPromoCode", statusFields);
		
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product promo actions for # 10210", module);
		}
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product promo actions for # 10210", module);
		// return "error";
		}
		
		try {
		productPromoCode = delegator.findOne("ProductPromoCode", UtilMisc.toMap("productPromoCodeId",productPromoCodeId), false);
		} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoCodeId", productPromoCodeId,
		"userLogin",userLogin,"emailAddress",orderItemRes.getString("recipientEmailId"));
		Map statusResult = dispatcher.runSync("createProductPromoCodeEmail", statusFields);
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems adding party to  # EX1234", module);
		}
		
		//OrderServices.sendOrderSms(dispatcher.getDelegator(), orderId);
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems adding party to  # EX1234", module);
		// return "error";
		}
		
		//GenericValue productPromoRule = productPromoRuleList.get(0);
		
		
		
		try {
		// set the status on the order header
		Map statusFields = UtilMisc.toMap("productPromoId", productPromoId,
		"productStoreId",productStoreId,"userLogin",userLogin);
		
		
		if(UtilValidate.isNotEmpty(expPartPaymentCouponAfterInDays) && UtilValidate.isNonnegativeInteger(expPartPaymentCouponAfterInDays+""))
		{
		int expireAfter = expPartPaymentCouponAfterInDays.intValue();
		Timestamp todaysDateStart = UtilDateTime.nowTimestamp();
		if(expireAfter != 0)
		todaysDateStart = UtilDateTime.getDayStart(todaysDateStart);
		
		statusFields.put("thruDate", UtilDateTime.addDaysToTimestamp(todaysDateStart, expireAfter));
		}
		
		Map statusResult = dispatcher.runSync("createProductStorePromoAppl", statusFields);
		if (statusResult.containsKey(ModelService.ERROR_MESSAGE)) {
		Debug.logError("Problems creating product store promo #"+productPromoId, module);
		}
		} catch (GenericServiceException e) {
		Debug.logError(e, "Problems creating product store promo #"+productPromoId, module);
		// return "error";
		}
		
		
		Map storeField = UtilMisc.toMap("productPromoId", productPromoId,
				"productStoreId",productStoreId,"userLogin",userLogin,"fromDate",UtilDateTime.nowTimestamp());
 		Map storeResult = null;
 		
		try{
		 storeResult = dispatcher.runSync("updateProductStorePromoAppl", storeField);
		}
		catch(GenericServiceException e){
			Debug.logError(e, "Problems Updating  product store promo #"+productPromoId, module);
		}

		mailGiftCardCoupon(delegator, dispatcher, userLogin, orderId,orderItemRes, price);
		
		}
	
	public static Map mailGiftCardCoupon(Delegator delegator , LocalDispatcher dispatcher , GenericValue userLogin , String orderId,GenericValue orderItemRes, BigDecimal price){

		String emailType = "PRDS_ODR_GIFT_CARD";
        String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#giftCardCouponMail";
        
        // get the order header and store
        GenericValue orderHeader = null;
        GenericValue personName =null;
        GenericValue genUser =null;
			
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting OrderHeader", module);
        }
        if (orderHeader == null) {
            return ServiceUtil.returnFailure("Could not find OrderHeader with ID [" + orderId + "]");
        }
        
        if(UtilValidate.isNotEmpty(orderHeader))
        {
        	try {
				genUser=delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",orderHeader.getString("createdBy")));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        try {
			personName=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",genUser.getString("partyId")));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", orderHeader.get("productStoreId"), "emailType", emailType);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }
        String bodyScreenLocation=null;
        if(UtilValidate.isNotEmpty(productStoreEmail))
        	bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }
        
        
       // Map<String, Object> bodyParameters = FastMap.newInstance();
        Map<String, Object> bodyParameters = new HashMap<String, Object>();
        bodyParameters.put("orderItemRes", orderItemRes);
        bodyParameters.put("couponCode",orderId);
        bodyParameters.put("evoucherAmt",price);
        bodyParameters.put("firstName",personName.getString("firstName"));
        bodyParameters.put("lastName",personName.getString("lastName"));
       // Map<String, Object> serviceContext = FastMap.newInstance();
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", orderItemRes.getString("recipientEmailId"));

        
     // send the notification
        Map sendResp = null;
        Locale locale = null;
        
        if (locale == null) {
            locale = Locale.getDefault();
        }
        
        try {
            dispatcher.runAsync("sendMailFromScreen", serviceContext);
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderServiceExceptionSeeLogs",locale));
        }
        
        //sendGiftCardCouponSms(delegator,orderId,personName,orderItemRes);
        String firstName = personName.getString("firstName");
		String lastName = personName.getString("lastName");
		String recipientEmailId=orderItemRes.getString("recipientEmailId");
		String recipientMobileNum=orderItemRes.getString("recipientMobileNum");
        if (UtilValidate.isNotEmpty(recipientEmailId) && UtilValidate.isNotEmpty(recipientMobileNum)) {
		    
		    String message1 = UtilProperties.getPropertyValue("general.properties","giftcard.couponcode.sms");;
		    		
		    		String newMessage1 = "";
		    		
		    		if(UtilValidate.isNotEmpty(recipientEmailId))
		    			newMessage1 = message1.replaceAll("<RecipientMailId>", recipientEmailId);
				    else
				    	newMessage1 = message1.replaceAll("<RecipientMailId>", "");
		    		
		    		if(UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName))
		    			newMessage1 = newMessage1.replaceAll("<CustomerFromName>", firstName+" "+lastName);
				    else
				    	newMessage1 = newMessage1.replaceAll("<CustomerFromName>", "");
		    		
		    		if(UtilValidate.isNotEmpty(orderId))
		    			newMessage1 = newMessage1.replaceAll("<CouponCode>", orderId);
				    else
				    	newMessage1 = newMessage1.replaceAll("<CouponCode>", "");
				    
			    	Map messageData = new HashMap();
				    messageData.put("mobileNumber", recipientMobileNum);
				   
				    messageData.put("message", newMessage1);
				    PartyServices.sendSms(messageData);
			    	
		    	}
        
        // check for errors
        if (sendResp != null && !ServiceUtil.isError(sendResp)) {
            sendResp.put("emailType", emailType);
        }
        return sendResp;
	}	
	
	public static Map sendGiftCardCouponSms(Delegator delegator,String couponCode,GenericValue personName,GenericValue orderItemRes)
	{
		String firstName = personName.getString("firstName");
		String lastName = personName.getString("lastName");
		String recipientEmailId=orderItemRes.getString("recipientEmailId");
		String recipientMobileNum=orderItemRes.getString("recipientMobileNum");
		if (UtilValidate.isNotEmpty(recipientEmailId) && UtilValidate.isNotEmpty(recipientMobileNum)) {
		    
		    String message1 = UtilProperties.getPropertyValue("general.properties","giftcard.couponcode.sms");;
		    		
		    		String newMessage1 = "";
		    		
		    		if(UtilValidate.isNotEmpty(recipientEmailId))
		    			newMessage1 = message1.replaceAll("<RecipientMailId>", recipientEmailId);
				    else
				    	newMessage1 = message1.replaceAll("<RecipientMailId>", "");
		    		
		    		if(UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName))
		    			newMessage1 = newMessage1.replaceAll("<CustomerFromName>", firstName+" "+lastName);
				    else
				    	newMessage1 = newMessage1.replaceAll("<CustomerFromName>", "");
		    		
		    		if(UtilValidate.isNotEmpty(couponCode))
		    			newMessage1 = newMessage1.replaceAll("<CouponCode>", couponCode);
				    else
				    	newMessage1 = newMessage1.replaceAll("<CouponCode>", "");

		    		Map messageData = new HashMap();
				    messageData.put("mobileNumber", recipientMobileNum);
				    messageData.put("message", newMessage1);
				    PartyServices.sendSms(messageData);
			    	
		    	}
		return ServiceUtil.returnSuccess();
	}
	public static String getPinId(Delegator delegator,String pinId,int count,String storeId) throws GenericEntityException
	{
		GenericValue productStore = ProductStoreWorker.getProductStore(storeId, delegator);
		 List<GenericValue> pinList=delegator.findList("OrderHeader", EntityCondition.makeCondition("pinId", EntityOperator.EQUALS,pinId),UtilMisc.toSet("pinId"), null, null, false);
		 
		 int loopCount=(int)productStore.getDouble("pinIdLoopCount").doubleValue();
		 int digit=(int)productStore.getDouble("pinIdDigit").doubleValue();
		 String min="1";
		 String max="9";
		 Random r = new Random();
		 min=StringUtils.rightPad(min,digit, '0');
		 max=StringUtils.rightPad(max,digit, '9');
		  if(count>=loopCount)
		  {
			 min=StringUtils.rightPad(min,digit+1, '0');
			 max=StringUtils.rightPad(max,digit+1, '9');
			 productStore.set("pinIdDigit", digit+1);
			 productStore.store();
			 pinId=String.valueOf(r.nextInt(Integer.parseInt(max) -Integer.parseInt(min)  + 1)+Integer.parseInt(min));
			 
		  }
		 if(UtilValidate.isNotEmpty(pinList) && count<loopCount)
	       {
			
	        pinId=String.valueOf(r.nextInt(Integer.parseInt(max) -Integer.parseInt(min)  + 1)+Integer.parseInt(min));
	        count++;
	        pinId=getPinId(delegator,pinId,count,storeId);
	       }
	      return pinId;
	}
	
	public static String reserveAllForPacking(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String orderId = request.getParameter("orderId");
		String shipGroupSeqId = request.getParameter("shipGroupSeqId");
		String requireInventory = request.getParameter("requireInventory");
		String reserveOrderEnumId = request.getParameter("reserveOrderEnumId");
		String facilityId = request.getParameter("facilityId");
		
		return reserveAllForPacking(delegator, dispatcher, userLogin, orderId, shipGroupSeqId, requireInventory, reserveOrderEnumId, facilityId);
	}
	public static String reserveAllForPacking(GenericDelegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String orderId,
			String shipGroupSeqId, String requireInventory, String reserveOrderEnumId, String facilityId){
		OrderReadHelper orh = new OrderReadHelper(delegator,orderId);
		
		List<GenericValue> orderItems = orh.getValidOrderItems();
		if(UtilValidate.isNotEmpty(orderItems))
		for(GenericValue orderItem : orderItems)
		{
			String orderItemSeqId = orderItem.getString("orderItemSeqId");
			List condList = new ArrayList();
			condList.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId));
			condList.add(EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS,orderItemSeqId));
			List orderItemShipGrpInvResList = null;
			try {
				orderItemShipGrpInvResList = delegator.findList("OrderItemShipGrpInvRes", EntityCondition.makeCondition(condList,EntityOperator.AND), null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(UtilValidate.isEmpty(orderItemShipGrpInvResList) || orderItemShipGrpInvResList.size() == 0)
			{
				BigDecimal shippedQuantity = orh.getItemShippedQuantity(orderItem);
				BigDecimal quantity = orderItem.getBigDecimal("quantity");
				if(UtilValidate.isEmpty(quantity)) quantity = BigDecimal.ZERO;
				BigDecimal cancelQuantity = orderItem.getBigDecimal("cancelQuantity");
				if(UtilValidate.isEmpty(cancelQuantity)) cancelQuantity = BigDecimal.ZERO;
				
				BigDecimal remainingQuantity = quantity.subtract(cancelQuantity).subtract(shippedQuantity);
				
				String productId = orderItem.getString("productId");
				
				Map context = UtilMisc.toMap("orderId", orderId, "shipGroupSeqId", shipGroupSeqId, "requireInventory", requireInventory, "reserveOrderEnumId", reserveOrderEnumId, "productId", productId);
				context.put("quantity", remainingQuantity);
				context.put("orderItemSeqId", orderItemSeqId);
				context.put("userLogin", userLogin);
				
				try {
					dispatcher.runSync("reserveProductInventory", context);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		return "success";
	}
	
	
}