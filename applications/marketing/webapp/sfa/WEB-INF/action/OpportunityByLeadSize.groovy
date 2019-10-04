import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;

cmpnyByCntTargetsReportList = []

List companyRoles = null;
		

leadNames = []
companyCount = []
			try {
				companyRoles = delegator.findList("Enumeration",  EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CMPNY_SIZE"), null, UtilMisc.toList("sequenceId") , null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		for (int lc = 0; lc < companyRoles.size(); lc++) {
			  Map statusMap = (Map) companyRoles.get(lc);
			  String statusId = (String) statusMap.get("enumId");
			  List companySize = null;
				try {
					companySize = delegator.findList("SalesOpportunity", EntityCondition.makeCondition("companySize",EntityOperator.EQUALS,statusId), null,null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			  GenericValue statusDesc = null;
			  try {
				  statusDesc = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", statusId) );
						  } catch (GenericEntityException e) {
				// TODO: handle exception
			  }
						  leadNames.add(statusDesc.getString("description"))
						  companyCount.add(companySize.size())
		}
context.cmpnyByCntTargetsReportNames = leadNames
context.cmpnyByCntTargetsReportTargetCost =companyCount
		
		

