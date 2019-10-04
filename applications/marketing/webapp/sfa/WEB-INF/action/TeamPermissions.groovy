import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityListIterator;


groupName = requestParameters.groupName
userLoginId = userLogin.userLoginId


userLoginAndSecurityGroup= [];
userLoginAndSecurityGroup = delegator.findList("UserLoginAndSecurityGroup",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false);
groupIds = [] ;
groupIds = userLoginAndSecurityGroup.groupId ;
boolean check = groupIds.contains("FULLADMIN");

securityConditionList = [];
securityConditions = [] 

			securityConditions.add(EntityCondition.makeCondition("groupNameLocal",EntityOperator.EQUALS,"SALES_GROUP"))
    		if(!check){
				securityConditions.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId))
			}
			if(!UtilValidate.isEmpty(groupName)){
				securityConditions.add(EntityCondition.makeCondition("groupName",EntityOperator.EQUALS,groupName))
			}

securityConditions = EntityCondition.makeCondition(securityConditions,EntityOperator.AND);

EntityListIterator partyAndGroup = delegator.find("PartyAndGroup",securityConditions,null,null,null,null);
context.partyAndGroup = partyAndGroup

