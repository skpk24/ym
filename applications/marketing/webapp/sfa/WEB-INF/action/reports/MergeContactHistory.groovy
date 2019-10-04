import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.marketing.util.SFAUtil;


def listist =   delegator.findList("PartyRelationship",
		EntityCondition.makeCondition(
		[
			EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
			EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "CONTACT"),
			EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_MERGE")
		]
		,EntityOperator.AND ),null, null, null, false)
resultList = []

for(j in 0..listist.size()-1){
	gv = (GenericValue) listist.get(j);
	String partyIdFrom = 	gv.partyIdFrom ;
	String partyIdTo = 	gv.partyIdTo ;
	String fromDate  = (gv.fromDate).toString();


	opportunityMap = [:]

	opportunityMap.put("createdDate", fromDate.toString())
	person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",partyIdFrom ))	;

	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("fromName", person.firstName)
	}

	person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyIdTo))	;

	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("toName", person.firstName)
	}


	cmpnyName =   delegator.findList("PartyRelationship",
			EntityCondition.makeCondition(
			[
				EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
				EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "CONTACT"),
				EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo)
			]
			,EntityOperator.AND ),null, null, null, false)
	if(!UtilValidate.isEmpty(cmpnyName)){
		temp = EntityUtil.getFirst(cmpnyName);
		person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",temp.partyIdFrom))	;
		
		if(!UtilValidate.isEmpty(person)){
			opportunityMap.put("cmpnyName", person.groupName)
		}
	}
	resultList.add(opportunityMap) ;
}
context.resultList = resultList ;
session.setAttribute("MergeContactHistory_SFA_REPORTS", resultList);
