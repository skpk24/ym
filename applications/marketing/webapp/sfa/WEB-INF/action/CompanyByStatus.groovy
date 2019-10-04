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


List companyRoles = null;
		
		
			try {
				companyRoles = delegator.findList("StatusItem",  EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PTYLEAD_ASSIGNED"), null, null, null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		
		print "\n\n\n\n\n\n  companyRoles  " + companyRoles + "\n\n\n\n\n\n"
leadNames = []
companyCount = []


		
		for (int lc = 0; lc < companyRoles.size(); lc++) {
			  Map statusMap = (Map) companyRoles.get(lc);
			  String statusId = (String) statusMap.get("statusId");
			  
			  List companySize = null;
				try {
					companySize = delegator.findList("Party", EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId), null,null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				print "\n\n\n\n\n\n  companySize  " + companySize + "\n\n\n\n\n\n"
				
			  GenericValue statusDesc = null;
			  try {
				  statusDesc = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId) );
						  } catch (GenericEntityException e) {
				// TODO: handle exception
			  }
			  leadNames.add (statusDesc.getString("description") );
		  	  companyCount.add(companySize.size())
		}

		
		
context.cmpnyByCntTargetsReportNames = leadNames
context.cmpnyByCntTargetsReportTargetCost =companyCount
