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
opportunityName = requestParameters.opportunityName
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

partyIdList = []

oppCondList = [];
oppCondList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL,"SFA_CLOSED"));
		  

      if(opportunityName){
			initCond =  delegator.findList("SalesOpportunity",EntityCondition.makeCondition("opportunityName",EntityOperator.LIKE,"%"+opportunityName+"%"),null,null,null,false);
			partyIds = initCond.salesOpportunityId ;
			oppCondList.add(EntityCondition.makeCondition("opportunityName",EntityOperator.LIKE,"%"+opportunityName+"%"))
			}
	  
	  if(!UtilValidate.isEmpty(companyName)){
		  initCond =  delegator.findList("PartyRelationshipAndDetail",
			  EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%"+companyName+"%"),null,null,null,false);
		  partyIds = initCond.partyId ;
		  oppCondList.add(EntityCondition.makeCondition("leadPartyId",EntityOperator.IN,partyIds))
	  }

EntityListIterator salesOppList = delegator.find("SalesOpportunity",EntityCondition.makeCondition(oppCondList,EntityOperator.AND),null,null,null,null);
context.listIt = salesOppList ;
