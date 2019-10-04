import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;


firstName = request.getParameter("oneFirstName");
twoFirstName = request.getParameter("twoFirstName");

//twoPartyRelationshipTypeId = LEAD_ASSIGNED or CONTACT_REL
//



exprBldr = new EntityConditionBuilder();

tempList = []

if(!UtilValidate.isEmpty(firstName)){
partyNames = delegator.findList("PartyRelationshipAndContactMechDetailView", exprBldr.LIKE(oneFirstName: "%"+firstName+"%"), null, null, null, false);
companyList =  partyNames.onePartyIdTo ;
Set s = new HashSet(companyList);
companyList = new ArrayList(s);
//UtilMisc.toSet("partyIdFrom","partyIdTo","roleTypeIdTo")
tempList =   delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, companyList),null, null, null, false);
}


if(!UtilValidate.isEmpty(twoFirstName)){
	partyNames = delegator.findList("PartyRelationshipAndContactMechDetailView", exprBldr.LIKE(twoFirstName: "%"+twoFirstName+"%"), null, null, null, false);
	companyList =  partyNames.twoPartyId ;
	Set s = new HashSet(companyList);
	companyList = new ArrayList(s);
	tempList =   delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, companyList),null, null, null, false);
}

if(UtilValidate.isEmpty(twoFirstName)&&UtilValidate.isEmpty(firstName)){
	partyNames = delegator.findList("PartyRelationshipAndContactMechDetailView", null, null, null, null, false);
	companyList =  partyNames.twoPartyId ;
	Set s = new HashSet(companyList);
	companyList = new ArrayList(s);
	tempList =   delegator.findList("PartyRelationshipAndDetail", EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, companyList),null, null, null, false);
}
context.accountListIt = tempList ;

