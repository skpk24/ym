import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;



exprBldr = new EntityConditionBuilder();

tempList = []

partyNames = delegator.findList("PartyRelationshipAndContactMechDetailView",null, null, null, null, false);
companyList =  partyNames.onePartyIdTo ;
Set s = new HashSet(companyList);
companyList = new ArrayList(s);
tempList =   delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, companyList),null, null, null, false);

context.accountListIt = tempList ;

