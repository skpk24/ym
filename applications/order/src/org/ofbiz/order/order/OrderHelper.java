package org.ofbiz.order.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastList;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class OrderHelper {

	public static String addItem(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException,
			CartItemModifyException, ItemNotFoundException {
		String partyId = request.getParameter("supplierPartyId");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String productStoreId = ProductStoreWorker.getProductStoreId(request);
		
		ShoppingCart shoppingCart = (ShoppingCart) request.getSession().getAttribute("shoppingCart");
		
		if (shoppingCart == null) {
			
			String currencyUomId = ProductStoreWorker.getStoreCurrencyUomId(request);
			GenericValue productStore = ProductStoreWorker.getProductStore(request);
			if(productStore != null){
				currencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default", "USD");
			}
			shoppingCart = new ShoppingCart(delegator, (String) request.getSession().getAttribute("productStoreId"),
					request.getLocale(), currencyUomId);
			shoppingCart.setOrderType("PURCHASE_ORDER");
			request.getSession().setAttribute("shoppingCart", shoppingCart);
		}
		
		String facilityId = shoppingCart.getFacilityId();
		if(facilityId == null){
			facilityId = ProductStoreWorker.determineSingleFacilityForStore(delegator, productStoreId);
		}
		shoppingCart.setAttribute("productStoreId", productStoreId);
		shoppingCart.setAttribute("supplierPartyId", partyId);
		shoppingCart.setSupplierAgentPartyId(partyId);
		shoppingCart.setShipGroupFacilityId(0, facilityId);
		shoppingCart.setOrderPartyId(partyId);
		shoppingCart.setBillFromVendorPartyId(partyId);
		shoppingCart.setShipFromVendorPartyId(partyId);
		shoppingCart.setBillToCustomerPartyId("Company");
		shoppingCart.setShipToCustomerPartyId("Company");

		request.getSession().setAttribute("shoppingCart", shoppingCart);

		List<EntityExpr> exprs = new ArrayList<EntityExpr>();
		exprs.add(EntityCondition.makeCondition("supplierPartyId",
				EntityOperator.EQUALS, partyId));

		List<GenericValue> purchaseOrderProducts = null;
		purchaseOrderProducts = delegator.findList("PurchaseOrderProduct",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);

		for (int i = 0; i < purchaseOrderProducts.size(); i++) {
			String productId = (String) purchaseOrderProducts.get(i).get(
					"productId");
			BigDecimal quantity = (BigDecimal) purchaseOrderProducts.get(i)
					.get("quantity");
			BigDecimal lastPrice = (BigDecimal) purchaseOrderProducts.get(i)
					.get("lastPrice");

			ShoppingCartItem shoppingCartItem = ShoppingCartItem
					.makePurchaseOrderItem(i, productId, null, quantity, null,
							null, null, null, null, null, dispatcher,
							shoppingCart, null, null, null, null);
			shoppingCartItem.setBasePrice(lastPrice);
			shoppingCart.addItem(i, shoppingCartItem);
		}

		request.getSession().setAttribute("shoppingCart", shoppingCart);
		String shippingContactMechId = PartyWorker
				.findPartyLatestPostalAddress("Company", delegator).getString(
						"contactMechId");
		shoppingCart.setShippingContactMechId(0, shippingContactMechId);
		shoppingCart.setSupplierPartyId(0, partyId);

		CheckOutEvents.createOrder(request, response);
		shoppingCart.clear();
		request.getSession().removeAttribute("shoppingCart");
		delegator.removeAll(purchaseOrderProducts);
		return "success";
	}

	public static String addItemToSession(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		Map<String, Object> requestParams = UtilHttp.getParameterMap(request);

		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		HttpSession session = request.getSession();

		String supplierProductId = (String) requestParams
				.get("supplierProductId");
		String productId = (String) requestParams.get("productId");
		String quantity = (String) requestParams.get("quantity");
		String lastPrice = (String) requestParams.get("lastPrice");
		String partyId = (String) requestParams.get("partyId");
		String currencyUomId = (String) requestParams.get("currencyUomId");

		String minMaxDays = getEstimatedShippingDays(delegator, partyId);

		List<GenericValue> purchaseOrderProducts = (List<GenericValue>) session
				.getAttribute("purchaseOrderProducts");
		boolean flag = true;
		if (purchaseOrderProducts == null)
			purchaseOrderProducts = new ArrayList<GenericValue>();//FastList.newInstance();
		if (supplierProductId != null && productId != null && quantity != null
				&& partyId != null && lastPrice != null) {

			for (int i = 0; i < purchaseOrderProducts.size(); i++) {
				GenericValue purchaseOrderProduct = purchaseOrderProducts
						.get(i);
				String suppProdId = (String) purchaseOrderProduct
						.get("supplierProductId");
				BigDecimal qnty = (BigDecimal) purchaseOrderProduct
						.get("quantity");
				if (suppProdId.equalsIgnoreCase(supplierProductId)) {
					double totalQuantity = qnty.doubleValue()
							+ Double.valueOf(quantity);
					purchaseOrderProduct.set("quantity",
							BigDecimal.valueOf(totalQuantity));
					flag = false;
				}
			}

			if (flag) {
				GenericValue gv = delegator.makeValue("PurchaseOrderProduct");
				gv.put("purchaseOrderProductId",
						delegator.getNextSeqId("PurchaseOrderProduct"));
				gv.put("supplierPartyId", partyId);
				gv.put("supplierProductId", supplierProductId);
				gv.put("productId", productId);
				gv.put("quantity", BigDecimal.valueOf(Double.valueOf(quantity)));
				gv.put("currencyUomId", currencyUomId);
				gv.put("lastPrice",
						BigDecimal.valueOf(Double.valueOf(lastPrice)));
				gv.put("shippingInDays", minMaxDays);
				purchaseOrderProducts.add(gv);
			}
			session.setAttribute("purchaseOrderProducts", purchaseOrderProducts);
		}
		return "success";
	}

	public static String savePurchaseOrderProducts(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		HttpSession session = request.getSession();
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<GenericValue> purchaseOrderProducts = (List<GenericValue>) session
				.getAttribute("purchaseOrderProducts");
		session.removeAttribute("purchaseOrderProducts");
		delegator.storeAll(purchaseOrderProducts);
		return "success";
	}

	public static String getEstimatedShippingDays(GenericDelegator delegator,
			String partyId) throws GenericEntityException {

		DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
		dynamicViewEntity.addMemberEntity("ORR", "OrderRole");
		dynamicViewEntity.addAlias("ORR", "orderId");
		dynamicViewEntity.addAlias("ORR", "partyId");
		dynamicViewEntity.addAlias("ORR", "roleTypeId");
		dynamicViewEntity.addMemberEntity("OH", "OrderHeader");
		dynamicViewEntity.addAlias("OH", "statusId");
		dynamicViewEntity.addAlias("OH", "orderTypeId");
		dynamicViewEntity.addViewLink("ORR", "OH", Boolean.FALSE,
				ModelKeyMap.makeKeyMapList("orderId"));

		List<EntityExpr> entityConditionList = new ArrayList<EntityExpr>();//FastList.newInstance();
		entityConditionList.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		entityConditionList.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "SUPPLIER_AGENT"));
		entityConditionList.add(EntityCondition.makeCondition("orderTypeId",
				EntityOperator.EQUALS, "PURCHASE_ORDER"));
		entityConditionList.add(EntityCondition.makeCondition("statusId",
				EntityOperator.EQUALS, "ORDER_COMPLETED"));

		EntityCondition whereCondition = EntityCondition.makeCondition(
				entityConditionList, EntityOperator.AND);
		EntityListIterator eli = delegator.findListIteratorByCondition(
				dynamicViewEntity, whereCondition, null, null, null, null);

		List searchResults = eli.getCompleteList();
		eli.close();
		String minMaxDays = "0 - 0";

		if (searchResults.size() < 1)
			return minMaxDays;

		TreeMap minToMax = new TreeMap();
		for (int i = 0; i < searchResults.size(); i++) {
			GenericValue searchResult = (GenericValue) searchResults.get(i);
			String orderId = (String) searchResult.get("orderId");

			EntityCondition statExpr = EntityCondition.makeCondition("orderId",
					EntityOperator.EQUALS, orderId);

			EntityCondition con1 = EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, "ORDER_CREATED");
			EntityCondition con2 = EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, "ORDER_COMPLETED");

			EntityCondition con3 = EntityCondition.makeCondition(UtilMisc
					.toList(statExpr, EntityCondition.makeCondition(con1,
							EntityOperator.OR, con2)), EntityOperator.AND);
			List<GenericValue> orderStatus = delegator.findList("OrderStatus",
					con3, null, null, null, false);
			Timestamp orderCreated = null;
			Timestamp orderCompleted = null;
			EntityCondition.makeCondition("availableThruDate",
					EntityOperator.GREATER_THAN, new Timestamp(
							(new java.util.Date()).getTime()).toString());
			for (int j = 0; j < orderStatus.size(); j++) {
				GenericValue os = (GenericValue) orderStatus.get(j);
				if (((String) os.get("statusId"))
						.equalsIgnoreCase("ORDER_CREATED"))
					orderCreated = (Timestamp) os.get("statusDatetime");
				else if (((String) os.get("statusId"))
						.equalsIgnoreCase("ORDER_COMPLETED"))
					orderCompleted = (Timestamp) os.get("statusDatetime");
			}

			double diff = ((orderCompleted.getTime() - orderCreated.getTime()) / (24 * 60 * 60 * 1000));
			int days = (int) Math.ceil(diff);
			minToMax.put(days, days);
		}
		if (minToMax.size() > 0) {
			minMaxDays = minToMax.firstKey() + " - " + minToMax.lastKey();
		}

		return minMaxDays;

	}
	
	public static Map<String, String> getSerialNumberByReturnId(DispatchContext dctx, Map<?, ?> context) throws GenericEntityException {
    	Delegator delegator = dctx.getDelegator();
    	
		List<EntityExpr> entityConditionList = new ArrayList<EntityExpr>();//FastList.newInstance();
		entityConditionList.add(EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, context.get("returnId")));
		EntityConditionList<EntityExpr> whereCondition = EntityCondition.makeCondition(entityConditionList, EntityOperator.AND);
		GenericValue returnItem = delegator.find("ReturnItem", whereCondition, null, null, null, null).getCompleteList().get(0);
		String orderId = returnItem.getString("orderId");

		GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId" , orderId));
		List<GenericValue> itemIssuances = orderHeader.getRelated("ItemIssuance", null, UtilMisc.toList("shipmentId", "shipmentItemSeqId"));
		GenericValue itemIssuance = itemIssuances.get(0);
		GenericValue inventoryItem = itemIssuance.getRelatedOne("InventoryItem");

		return UtilMisc.toMap("serialNumber", inventoryItem.getString("serialNumber"));
	}
}