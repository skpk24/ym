import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityConditionList;

payMethodTypeId="EXT_WORLDPAY";

orderId = parameters.orderId;
//print"%%%%%%%%%%%%%%%%%%%%%%myorder id is%%%%%%%%%%%%%%%%%"+orderId

mylist=delegator.findList("OrderPaymentPreference",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,orderId),null, null, null,false);
//print"@@@@@@@@@@@@@@@@@my ordpayment prf id@@@@@@@@@@@@@@@@"+mylist.orderPaymentPreferenceId;

Iterator iterator = mylist.iterator();
val=null;
while ( iterator.hasNext() ){
	      GenericValue  salec=(GenericValue) iterator.next();
	      val= salec.getString("orderPaymentPreferenceId");
	     // print("\n\n\nthis is my sales channel enum id??????????????  : "+salec.getString("orderPaymentPreferenceId"));
	      }
	   
//print"###########val value is###########"+val;

 List ptiConditionList = UtilMisc.toList(
                                new EntityExpr("orderPaymentPreferenceId", EntityOperator.EQUALS, val),
                                new EntityExpr("paymentMethodTypeId", EntityOperator.EQUALS, payMethodTypeId));
                                
  EntityCondition ptiCondition = EntityCondition.makeCondition(ptiConditionList, EntityOperator.AND);
	  
	  
	//  EntityCondition ptiCondition = new EntityConditionList(ptiConditionList, EntityOperator.AND); 
  
ourlist=delegator.findList("PaymentGatewayResponse",ptiCondition,null, null, null,false); 

Iterator iterator1 = ourlist.iterator();
reference=null;
while ( iterator1.hasNext() ){
	      GenericValue  salec=(GenericValue) iterator1.next();
	      reference= salec.getString("referenceNum");
	     // print("\n\n\nthis is my sales channel enum id??????????????  : "+salec.getString("orderPaymentPreferenceId"));
	      }
	   
print"\n\n\n!!!!!!!!!!!!!!!reference value is!!!!!!!!!!!!!!!!"+reference;

context.reference=reference;
  
                           