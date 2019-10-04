import java.text.DecimalFormat;
import java.util.ArrayList;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
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

def listist = delegator.findList("SalesOpportunityRole",   EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList), null, null, null, false)


def groupByist =  listist.groupBy{it.partyId}
def valueList = []
def keyList = []
Set uniqueKeyListSet = new HashSet();

groupByist.each() {
	key,value -> valueList.add(value.salesOpportunityId);
	keyList.add(value.partyId);

}

partyList = []
opportunityMap = [:]


for(j in 0..keyList.size-1){
	if(!UtilValidate.isEmpty( keyList.get(j).get(0) )){
		partyList.add( keyList.get(j).get(0) )
		opportunityMap.put(keyList.get(j).get(0),valueList.get(j))
		}
}



BigDecimal totAmount = BigDecimal.ZERO ;
resultList = [] ;

for(j in 0..opportunityMap.size()-1){
	def oppList = delegator.findList("SalesOpportunity",  EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN,  opportunityMap.get(keyList.get(j).get(0))), null, null, null, false)
	Map resultMap = [:] ;
	BigDecimal totalAmount = SFAUtil.sumArrayListElements(oppList.estimatedAmount);
	resultMap.put("partyId" ,keyList.get(j).get(0).toString() );
	resultMap.put("totalAmount",totalAmount.toString());
	resultMap.put("noOpp",opportunityMap.get(keyList.get(j).get(0)).size().toString());
	resultList.add(resultMap);
}
context.resultList = resultList ;


session.setAttribute("AccountsByOpportunities_SFA_REPORTS", resultList);