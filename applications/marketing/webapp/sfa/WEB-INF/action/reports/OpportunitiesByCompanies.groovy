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

def listist =   delegator.findList("SalesOpportunityAndRole",EntityCondition.makeCondition("partyId",EntityOperator.IN,partyIdList),null, null, null, false)

resultList = []


for(j in 0..listist.size()-1){
	
	gv = listist.get(j);
	
	opportunityMap = [:]
	opportunityMap.put("opportunityName", gv.opportunityName)
	opportunityMap.put("projectName", gv.projectName)
	
	
	person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",gv.partyId))	;
	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("salesHead", person.firstName)
	}
   
	person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",gv.leadPartyId))	;
	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("cmpnyName", person.groupName)
	}
	opportunityMap.put("estimatedProbability", gv.estimatedProbability)
	opportunityMap.put("estimatedAmount", gv.estimatedAmount)
	
	resultList.add(opportunityMap) ;
	
	
}

context.resultList = resultList ;
session.setAttribute("OpportunitiesByCompanies_SFA_REPORTS", resultList);
