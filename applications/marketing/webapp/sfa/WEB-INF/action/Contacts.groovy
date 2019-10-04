import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

import org.ofbiz.entity.cache.EntityObjectCache;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;


companyName = requestParameters.companyName
contactName = requestParameters.contactName
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
EntityListIterator PartyRelationshipAndDetail = delegator.find("PartyRelationshipAndDetail",securityConditions,null,null,null,null);

partyIdList = []

while(PartyRelationshipAndDetail.hasNext()){
	partyId = (String) PartyRelationshipAndDetail.next().get("partyIdFrom");
	partyIdList.add(partyId)
}


print "\n\n\n\n\n\n\n   securityConditions " + securityConditions + "\n\n\n\n\n\n\n\n"
contactCond = [];
if(partyIdList)
contactCond.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,partyIdList));
contactCond.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"SFA_OPP_CLOSE"));
if(contactName){
	contactCond.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%" + contactName + "%"));
	}
if(companyName){
	contactCond.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%" + companyName + "%"));
	}


contactList = delegator.findList("PartyRelationshipAndDetail",EntityCondition.makeCondition(contactCond,EntityOperator.AND),null,null,null,false);

contactListParty =   contactList.partyIdFrom


contactList = delegator.findList("SalesOpportunity",EntityCondition.makeCondition([
	                                    EntityCondition.makeCondition("leadPartyId",EntityOperator.IN,contactListParty),
										EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"SFA_CLOSED")
										],EntityOperator.AND),null,null,null,false);

listIt =  delegator.findList("PartyRelationshipAndDetail",EntityCondition.makeCondition(
	                                      [EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,contactList.leadPartyId ),
										   EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"SFA_OPP_CLOSE")
										  ],EntityOperator.AND
										  ),null,null,null,false);
									  
context.listIt =  listIt

