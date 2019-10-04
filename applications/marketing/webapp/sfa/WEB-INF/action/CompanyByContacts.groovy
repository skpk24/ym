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


cmpnyByCntTargetsReportList = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"PROSPECT_LEAD"), null, null, null, false);



leadNames = []
companyCount = []

for (int lc = 0; lc < cmpnyByCntTargetsReportList.size(); lc++) {
	GenericValue gv = (GenericValue) cmpnyByCntTargetsReportList.get(lc);
	List cond = FastList.newInstance();
	cond.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, gv.getString("partyId")));
	cond.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "CONTACT"));
	cond.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS, "CONTACT_REL"));
	

	List companySize = null;
	try {
		companySize = delegator
				.findList("PartyRelationship", EntityCondition
						.makeCondition(cond, EntityOperator.AND), null,
						null, null, false);
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  String companyId = gv.getString("partyId"); 
	  if(companyId && companySize.size()>0){
		  leadNames.add ( delegator.findByPrimaryKey("Person",[partyId:companyId]).firstName );
		  companyCount.add(companySize.size())
	  }
}

context.cmpnyByCntTargetsReportNames = leadNames
context.cmpnyByCntTargetsReportTargetCost =companyCount

