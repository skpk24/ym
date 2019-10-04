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


userLoginId = userLogin.userLoginId

userLoginAndSecurityGroup= [];
userLoginAndSecurityGroup = delegator.findList("UserLoginAndSecurityGroup",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false);
groupIds = [] ;
groupIds = userLoginAndSecurityGroup.groupId ;
boolean check = groupIds.contains("FULLADMIN");

securityConditionList = [];
securityConditions = []

			securityConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"SALES_ASSIGN"))

			
			if(!check){
				securityOrCondition = []
				userLoginPartyId = delegator.findByPrimaryKey("UserLogin",[userLoginId:userLoginId]);
				securityOrCondition.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId));
				securityOrCondition.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,userLoginPartyId.partyId));
				securityOrConditions = EntityCondition.makeCondition(securityOrCondition,EntityOperator.OR);
				securityConditions.add(securityOrConditions)
			}
			
			
			
securityConditions = EntityCondition.makeCondition(securityConditions,EntityOperator.AND);
PartyRelationshipAndDetail = delegator.findList("PartyRelationshipAndDetail",securityConditions,null,null,null,false);

partyIdList = []

partyIdList = PartyRelationshipAndDetail.partyIdFrom

contactCond = [];
contactCond.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,partyIdList));
contactCond.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"CONTACT_REL"));

contactList = delegator.findList("PartyRelationshipAndDetail",EntityCondition.makeCondition(contactCond,EntityOperator.AND),null,null,null,false);


listist =  contactList ;


def valueList = []
def keyList = []
Set uniqueKeyListSet = new HashSet();

resultList = []

for(j in 0..listist.size()-1){
	gv = (GenericValue) listist.get(j);
	String partyIdFrom = 	gv.partyIdFrom ;
	String partyIdTo = 	gv.partyIdTo ;


	opportunityMap = [:]
	person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",partyIdFrom ))	;

	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("cmpnyName", person.groupName)
		opportunityMap.put("createdDate",(person.createdDate).toString())
		status = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId",person.statusId ));
		if(status)
			opportunityMap.put("statusId", status.description )
	}

	person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyIdTo))	;

	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("contactName", person.firstName)
	}
	
	tempOppSize = delegator.findList("SalesOpportunityRole",EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyIdFrom),null,null,null,false);
	
	opportunityMap.put("noOpp", tempOppSize.size() )
	resultList.add(opportunityMap) ;
}
context.resultList = resultList ;
session.setAttribute("ContactByStatus_SFA_REPORTS", resultList);
