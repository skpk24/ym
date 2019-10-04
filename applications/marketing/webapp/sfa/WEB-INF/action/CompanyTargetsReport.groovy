import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;

companyTargetsReportList = []


companyTargetsReportList = delegator.findList("SalesTarget",EntityCondition.makeCondition("teamTypeId",EntityOperator.EQUALS,"COMPANY_TARGET"),null,null,null,false);


context.companyTargetsReportList = companyTargetsReportList ;

companyTargetsReportLists = []
if(companyTargetsReportList){
for(j in 0..companyTargetsReportList.size()-1){
	
	gv = companyTargetsReportList.get(j);
	
	personGV = delegator.findByPrimaryKey("PartyGroup",[partyId:gv.partyId]);
	opportunityMap = [:]
	opportunityMap.put("partyId", gv.partyId)
	
	if(personGV)
	opportunityMap.put("leadName", personGV.groupName)
	else
	opportunityMap.put("leadName", "No Name")
	
	if(!UtilValidate.isEmpty(gv.getBigDecimal("targetCost"))){
	BigDecimal bd = new BigDecimal(gv.getBigDecimal("targetCost"))
	bd=bd.divide(new BigDecimal("10000"))
	opportunityMap.put("targetCost",bd.intValue())
	}
	if(UtilValidate.isEmpty( gv.getBigDecimal("targetCost").toString())){
	   opportunityMap.put("targetCost", 0)
	}
	opportunityMap.put("bestCaseCost", gv.getBigDecimal("bestCaseCost").toString())
	opportunityMap.put("closedCost", gv.getBigDecimal("closedCost").toString())
	opportunityMap.put("currencyUomId", gv.currencyUomId)
	opportunityMap.put("fromDate", gv.fromDate.toString())
	opportunityMap.put("thruDate", gv.thruDate.toString())
	companyTargetsReportLists.add(opportunityMap) ;
}
session.setAttribute("CompanyTargetsReportExcel_SFA_REPORTS", companyTargetsReportLists);

leadNames = []
targetCosts = []
currencyUomId = []
leadNames = companyTargetsReportLists.leadName
targetCosts = companyTargetsReportLists.targetCost
currencyUomId = companyTargetsReportLists.currencyUomId.get(0)

context.companyTargetsReportNames = leadNames
context.companyTargetsReportTargetCost =targetCosts
context.currencyUomId = currencyUomId;
context.listSize = companyTargetsReportList.size();
}