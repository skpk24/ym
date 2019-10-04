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

teamTargetsReportList = []


teamTargetsReportList = delegator.findList("SalesTarget",EntityCondition.makeCondition("teamTypeId",EntityOperator.EQUALS,"TEAM_TARGET"),null,null,null,false);


context.teamTargetsReportList = teamTargetsReportList ;

teamTargetsReportLists = []




if(teamTargetsReportList){
for(j in 0..teamTargetsReportList.size()-1){
	
	gv = teamTargetsReportList.get(j);
	
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
	teamTargetsReportLists.add(opportunityMap) ;
}


session.setAttribute("TeamTargetsReportExcel_SFA_REPORTS", teamTargetsReportLists);

leadNames = []
targetCosts = []
currencyUomId = []
leadNames = teamTargetsReportLists.leadName
targetCosts = teamTargetsReportLists.targetCost
currencyUomId = teamTargetsReportLists.currencyUomId.get(0)

context.teamTargetsReportNames = leadNames
context.teamTargetsReportTargetCost =targetCosts
context.currencyUomId = currencyUomId;

context.listSize = teamTargetsReportList.size();

}