/*
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.*;
import java.util.*;
import org.ofbiz.entity.condition.EntityExpr;
/*
 * Finds back ordered items with complete details spanning OrderHeader, OrderItem, OrderItemShipGroup,
 * InventoryItem and OrderItemShipGrpInvRes
 */

// data for the find form
shipmentMethodTypes = delegator.findList("CarrierAndShipmentMethod", null, null, UtilMisc.toList("description"), null, false);

methodsAndCarriers = new ArrayList();
smtit = shipmentMethodTypes.iterator();
while (smtit.hasNext()) {
    smt = smtit.next();
    smMap = smt.getAllFields();
    carrierPartyName = PartyHelper.getPartyName(delegator, smt.get("partyId"), false);
    smMap.put("carrierPartyName", carrierPartyName);
    methodsAndCarriers.add(smMap);
}
context.put("shipmentMethodTypes", methodsAndCarriers);

// fields to search by
productId = parameters.get("productId");
carrierAndShipmentMethodTypeId = parameters.get("carrierAndShipmentMethodTypeId");
facilityId = parameters.get("facilityId");
statusId = request.getParameter("statusId");
statusIds = FastList.newInstance();
if (statusId == null) { //  when user first loads page
    statusIds.add("ORDER_APPROVED");
} else if ("ANY".equals(statusId)) {
    statusIds.add("ORDER_APPROVED");
    statusIds.add("ORDER_HOLD");
    statusIds.add("ORDER_CREATED");
} else {
    statusIds.add(statusId);
}

List <EntityExpr> searchConditions = FastList.newInstance();
if (!UtilValidate.isEmpty(productId)) {
    searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
}
if (!UtilValidate.isEmpty(facilityId)) {
    searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
}
if (!UtilValidate.isEmpty(carrierAndShipmentMethodTypeId)) {
    carrierPartyId = carrierAndShipmentMethodTypeId.split("\\^")[0];
    shipmentMethodTypeId = carrierAndShipmentMethodTypeId.split("\\^")[1];
    
    searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("carrierPartyId",EntityOperator.EQUALS,carrierPartyId));
    searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("shipmentMethodTypeId",EntityOperator.EQUALS,shipmentMethodTypeId));

}

fields = null;
orderBy = UtilMisc.toList("reservedDatetime", "sequenceId");

searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("quantityNotAvailable", EntityOperator.GREATER_THAN, new BigDecimal(0)));
searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("orderStatusId", EntityOperator.IN, statusIds));
searchConditions.add(org.ofbiz.entity.condition.EntityCondition.makeCondition("orderItemStatusId", EntityOperator.IN,UtilMisc.toList("ITEM_APPROVED", "ITEM_CREATED")));

allConditions = org.ofbiz.entity.condition.EntityCondition.makeCondition(searchConditions, EntityOperator.AND);
 backOrderedItem =   delegator.findList("ReservedItemDetail",org.ofbiz.entity.condition.EntityCondition.makeCondition(searchConditions, EntityOperator.AND), null, orderBy, null, false);
//iterator = delegator.findListIteratorByCondition("ReservedItemDetail", allConditions, null, fields, orderBy, new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
//backOrderedItems = iterator.getPartialList(viewIndex.intValue(), viewSize.intValue());
//iterator.last();
backOrderedItemsTotalSize = backOrderedItem.size();
context.put("backOrderedItems", backOrderedItem);
context.put("backOrderedItemsTotalSize", backOrderedItemsTotalSize);
//iterator.close();