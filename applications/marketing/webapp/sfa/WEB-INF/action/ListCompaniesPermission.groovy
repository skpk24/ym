import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;


groupName = requestParameters.firstName
userLoginId = userLogin.userLoginId

userLoginAndSecurityGroup= [];
userLoginAndSecurityGroup = delegator.findList("UserLoginAndSecurityGroup",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false);
groupIds = [] ;
groupIds = userLoginAndSecurityGroup.groupId ;
boolean check = groupIds.contains("FULLADMIN");

securityConditionList = [];
securityConditions = [] 

			securityConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"SALES_ASSIGN"))
			securityConditions.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS,"PROSPECT_LEAD"))
			
			if(!UtilValidate.isEmpty(groupName)){
				securityConditions.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE,"%"+groupName+"%"));
			}
			
			
			if(!check){
				securityOrCondition = []
				userLoginPartyId = delegator.findByPrimaryKey("UserLogin",[userLoginId:userLoginId]);
				securityOrCondition.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId));
				securityOrCondition.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,userLoginPartyId.partyId));
				securityOrConditions = EntityCondition.makeCondition(securityOrCondition,EntityOperator.OR);
				securityConditions.add(securityOrConditions)
			}
			
securityConditions.add(EntityCondition.makeCondition("partyStatusId",EntityOperator.NOT_EQUAL,"SFA_CLOSED"))
securityConditions = EntityCondition.makeCondition(securityConditions,EntityOperator.AND);

EntityListIterator PartyRelationshipAndDetail = delegator.find("PartyRelationshipAndDetail",securityConditions,null,null,null,null);
context.listIt = PartyRelationshipAndDetail
