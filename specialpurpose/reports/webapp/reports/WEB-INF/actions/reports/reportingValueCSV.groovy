import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;



        
exprs = [EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, parameters.get("salesChannelEnumId"))];
if (parameters.get("fromOrderDate")) {
	   exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, parameters.get("fromOrderDate")));
    
}
if ( parameters.get("thruOrderDate")) {
	exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, parameters.get("thruOrderDate")));

}                

List list2=new ArrayList();
List list3=new ArrayList();
List list4=new ArrayList();
List list5=new ArrayList();
List list6=new ArrayList();

List list1=new ArrayList();
list1.add("orderId");
list1.add("orderDate");
list1.add("statusId");
	 list1.add("billToPartyId");
	  list1.add("grandTotal");

	  

	  postedExprs = new ArrayList();

	  postedExprs.addAll(exprs);


postedTrans = delegator.findByCondition("OrderHeader", EntityCondition.makeCondition(postedExprs, EntityOperator.AND), list1, null);


for (Iterator iterator = postedTrans.iterator(); iterator.hasNext();) {
	GenericValue gv = (GenericValue) iterator.next();
	 
gv.statusId  = gv.getRelatedOneCache("StatusItem").get("description",locale);

orh = org.ofbiz.order.order.OrderReadHelper.getHelper(gv)
 billToParty = orh.getBillToParty();

if (billToParty!=null){
 billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", org.ofbiz.base.util.UtilMisc.toMap("partyId", billToParty.partyId, "compareDate", gv.orderDate, "userLogin", session.getAttribute("userLogin")));
 billTo = billToPartyNameResult.fullName;
 gv.billToPartyId = billTo; 

}
}
context.myList = postedTrans;


