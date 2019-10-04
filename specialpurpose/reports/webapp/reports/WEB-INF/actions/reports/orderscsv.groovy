import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.ofbiz.base.util.*;

partyId = request.getParameter("partyId");
orderStatusId = request.getParameter("orderStatusId");
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
	productStoreId = session.getAttribute("productStoreId");
fromDate = request.getParameter("minDate");
thruDate = request.getParameter("maxDate");

Date tempDate = null;
Timestamp dateFrom = null , dateTo =null;
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
if(!UtilValidate.isEmpty(fromDate)){
 try {
	 tempDate =  (Date)formatter.parse(fromDate);
} catch (Exception e) {
	// TODO: handle exception
} 	

 dateFrom=new Timestamp(tempDate.getTime());
}

if(!UtilValidate.isEmpty(thruDate)){
	 try {
		   tempDate =  (Date)formatter.parse(thruDate);
	} catch (Exception e) {
		// TODO: handle exception
	} 	

dateTo=new Timestamp(tempDate.getTime());
}


List exprs = new ArrayList();
exprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
if (productStoreId != null) {
	   exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}
if (partyId != null && !partyId.equals("") ) {
	
	partyUserLogins = delegator.findByCondition("UserLoginAndPartyDetails", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null);
	if(partyUserLogins!=null){
		userLogins = EntityUtil.getFieldListFromEntityList(partyUserLogins, "userLoginId", true);
	   exprs.add(EntityCondition.makeCondition("createdBy", EntityOperator.IN, userLogins));
	}
}
if (dateFrom != null && !dateFrom.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dateFrom));
}
if (dateTo != null && !dateTo.equals("") ) {
	exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, dateTo));
} 
if (orderStatusId != null && !orderStatusId.equals("") ) {
	exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, orderStatusId));
} 
exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null));               

List conditions = new ArrayList();
conditions.addAll(exprs);

orderList = delegator.findByCondition("OrderHeader", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);

List orderListNew = new ArrayList();

Iterator itr = orderList.iterator();
while(itr.hasNext()){
	Map orderInfo = new HashMap();
	GenericValue orderHeader = (GenericValue) itr.next();
	orh = org.ofbiz.order.order.OrderReadHelper.getHelper(orderHeader);
	statusItem = orderHeader.getRelatedOneCache("StatusItem");
	orderType = orderHeader.getRelatedOneCache("OrderType");
	displayParty = orh.getPlacingParty();
	fullName = "";
	try{
	displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", org.ofbiz.base.util.UtilMisc.toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin));
	fullName = displayPartyNameResult.fullName;
	}catch (Exception e) {
		//error = (String) e.getMessage();
	}
	orderInfo.put("orderId",orderHeader.getString("orderId"));
	if(displayParty != null)
		orderInfo.put("partyId",displayParty.partyId);
	else
		orderInfo.put("partyId","_NA_");
	orderInfo.put("partyName",fullName);
	if(orh != null){
	orderInfo.put("orderItemsQuantity",orh.getTotalOrderItemsQuantity());
	orderInfo.put("backOrderQuantity",orh.getOrderBackorderQuantity());
	orderInfo.put("orderReturnedQuantity",orh.getOrderReturnedQuantity());
	}else{
		orderInfo.put("orderItemsQuantity","");
		orderInfo.put("backOrderQuantity","");
		orderInfo.put("orderReturnedQuantity","");		
	}
	orderInfo.put("remainingSubTotal",orderHeader.remainingSubTotal);
	orderInfo.put("grandTotal",orderHeader.grandTotal);
	if(statusItem != null)
		orderInfo.put("status",statusItem.get("description",locale));
	else
		orderInfo.put("status",orderHeader.getString("statusId"));
	orderInfo.put("orderDate",orderHeader.getString("orderDate"));
	
	orderListNew.add(orderInfo);

}
context.OrdersList=orderListNew;
