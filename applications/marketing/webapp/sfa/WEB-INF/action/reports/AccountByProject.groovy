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

def listist = delegator.findList("SalesOpportunity",   EntityCondition.makeCondition("leadPartyId", EntityOperator.IN, partyIdList), null, null, null, false)
def groupByist =  listist.groupBy{it.leadPartyId}



def valueList = []
def keyList = []
Set uniqueKeyListSet = new HashSet();

groupByist.each() {
	key,value -> valueList.add(value.leadPartyId);
	keyList.add(value.leadPartyId);
}



partyList = []


List numProj = null;
for(j in 0..keyList.size-1){
	if(!UtilValidate.isEmpty( keyList.get(j).get(0) )){
		
		opportunityMap = [:]
		opportunityMap.put("partyId", keyList.get(j).get(0).toString());
		
		partyId = keyList.get(j).get(0).toString()
		  
			person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",partyId ))	;
	 
				if(!UtilValidate.isEmpty(person)){
					opportunityMap.put("accName", person.groupName)
					opportunityMap.put("createdDate",person.createdDate)
	            }
		
			numProj = delegator.findList("SalesOpportunity", EntityCondition.makeCondition("leadPartyId",EntityOperator.EQUALS,keyList.get(j).get(0).toString()), null, null, null, false);
			if(numProj) opportunityMap.put("numProjects",numProj.size().toString())

			numProj = delegator.findList("PartyContent", EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId), null, null, null, false);
			if(numProj) opportunityMap.put("numDocuments",numProj.size().toString())
			
		    partyList.add(opportunityMap) ;
		}
}
context.resultList = partyList ;
session.setAttribute("AccountByProject_SFA_REPORTS", partyList);
