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

partyId = userLogin.partyId

securityConditions = [] 
roles = [];
roles.add("MANAGER");
roles.add("TEAMLEADER");

securityConditions.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId))
securityConditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,roles))


roleTest= [];
roleTest = delegator.findList("PartyRole",EntityCondition.makeCondition(securityConditions),null,null,null,false);

partyRoles = roleTest.roleTypeId ;
boolean roleCheck = partyRoles.contains("TEAMLEADER");


userLoginAndSecurityGroup= [];
userLoginAndSecurityGroup = delegator.findList("UserLoginAndSecurityGroup",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false);
groupIds = [] ;
groupIds = userLoginAndSecurityGroup.groupId ;
boolean check = groupIds.contains("FULLADMIN");


if(check||roleTest.size()>0){
requesturi = request.getRequestURI()
if(!roleCheck && requesturi!="/crm/control/CreateLeadTargets"){
context.bestCost = "Y"
context.viewScreen = "Y"
}
context.bestCost = "Y"
context.viewScreen = "Y"

}