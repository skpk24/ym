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
import org.ofbiz.order.order.OrderReturnServices;


productStoreId = ProductStoreWorker.getProductStoreId(request);
if (userLogin) {
	leftloyaltyPoints = new BigDecimal(0);
	totalloyaltyPoints = new BigDecimal(0);
	loyaltyPointsData = delegator.findByPrimaryKey("LoyaltyPoint", [userLoginId : userLogin.userLoginId]);
	if(loyaltyPointsData != null){
		leftloyaltyPoints = loyaltyPointsData.getString("loyaltyPoint");
		context.leftloyaltyPoints=leftloyaltyPoints;
	}
	partyBillValue  = delegator.findList("BillingAccountRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId), null, null, null, false);
	if (UtilValidate.isNotEmpty(partyBillValue)) {
		for (GenericValue partyBill : partyBillValue) {
			EntityCondition barFindCond = EntityCondition.makeCondition(UtilMisc.toList(
				EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, partyBill.getString("billingAccountId")),
				EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + "Credit Account for Loyalty #") +userLogin.partyId+"%"))), EntityOperator.AND);
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
}