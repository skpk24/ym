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

			securityConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"CONTACT_REL"))
			securityConditions.add(EntityCondition.makeCondition("roleTypeIdFrom",EntityOperator.EQUALS,"PROSPECT_LEAD"))

			
			if(!check){
				securityOrCondition = []
				userLoginPartyId = delegator.findByPrimaryKey("UserLogin",[userLoginId:userLoginId]);
				securityOrCondition.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId));
				securityOrCondition.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,userLoginPartyId.partyId));
				securityOrConditions = EntityCondition.makeCondition(securityOrCondition,EntityOperator.OR);
				securityConditions.add(securityOrConditions)
			}
			
			if(!UtilValidate.isEmpty(companyName)){
				securityConditions.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%"+companyName+"%"));
			}
			
securityConditions = EntityCondition.makeCondition(securityConditions,EntityOperator.AND);
EntityListIterator PartyRelationshipAndDetail = delegator.find("PartyRelationshipAndDetail",securityConditions,null,null,null,null);

partyIdList = []

while(PartyRelationshipAndDetail.hasNext()){
	partyId = (String) PartyRelationshipAndDetail.next().get("partyIdFrom");
	partyIdList.add(partyId)
}


//print "\n\n\n\n\n\n  securityConditions " + securityConditions + "\n\n\n\n\n\n\n\n\n\n\n\n"
//print "\n\n\n\n\n\n  partyIdList " + partyIdList + "\n\n\n\n\n\n\n\n\n\n\n\n"



contactCond = [];
contactCond.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.IN,partyIdList));
contactCond.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"CONTACT_REL"));
if(companyName){
	contactCond.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%" + companyName + "%"));
	}

EntityListIterator contactList = delegator.find("PartyRelationshipAndDetail",EntityCondition.makeCondition(contactCond,EntityOperator.AND),null,null,null,null);

context.listIt = contactList; 

