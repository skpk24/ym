import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

List paymentListId=EntityUtil.getFieldListFromEntityList(delegator.findList("OrderPaymentPreference",null,UtilMisc.toSet("paymentMethodTypeId"),null,null,false), "paymentMethodTypeId", true);
if(UtilValidate.isNotEmpty(paymentListId))
{
List paymentList=delegator.findList("PaymentMethodType",EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.IN,paymentListId),UtilMisc.toSet("paymentMethodTypeId","description"),null,null,false);
context.paymentList=paymentList;
}


List fromDateCond = new ArrayList();
	    	 fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
	    	 fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
	    	
	    	 List thruDateCond = new ArrayList();
	    	 thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
	    	 thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
	    	
	    	 List condition = new ArrayList();
		     condition.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
		     condition.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
    		 
    		 zoneList = delegator.findList("ZoneGroup",EntityCondition.makeCondition(condition,EntityOperator.AND),null, null, null, true);
			 if(UtilValidate.isNotEmpty(zoneList))
			 context.zoneList=zoneList;