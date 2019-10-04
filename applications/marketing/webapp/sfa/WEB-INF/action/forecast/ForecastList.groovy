import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;


companyName = request.getParameter("companyName");
contactName = request.getParameter("contactName");


exprBldr = new EntityConditionBuilder();

tempList = []
conditionList = []
mainConditionList = []
companyList =[]

if(!UtilValidate.isEmpty(companyName)){
	conditionList.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%"+companyName+"%"));
}

if(!UtilValidate.isEmpty(contactName)){
	conditionList.add(EntityCondition.makeCondition("assistantName", EntityOperator.LIKE, "%"+contactName+"%"));
	
	partyNames = delegator.findList("Person",EntityCondition.makeCondition(conditionList), null, null, null, false);
	companyList =  partyNames.partyId ;
	Set s = new HashSet(companyList);
	companyList = new ArrayList(s);
	mainConditionList.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, companyList));
}

//partyNames = delegator.findList("SalesOpportunityAndRole",EntityCondition.makeCondition(conditionList), null, null, null, false);
//companyList =  partyNames.salesOpportunityId ;
//s = new HashSet(companyList);
//companyList = new ArrayList(s);
//mainConditionList.add(EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, companyList));

print "\n\n\n\n\n\n\n  mainConditionList "
context.forecastList = delegator.findList("SalesForecast",EntityCondition.makeCondition(mainConditionList), null, null, null, false);
