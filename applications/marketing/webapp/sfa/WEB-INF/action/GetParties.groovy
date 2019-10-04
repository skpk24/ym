

import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*

partiesList = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "TEAM_MEM_ASSIGN"),null,null,null,false);

partyList = [];
partiesList.each{iter->
	partyMap = [:];
	
	String sTemp = (String) iter.partyIdTo ;
	grpName = delegator.findByPrimaryKey("PartySummaryCRMView",[partyId:sTemp ]);
	partyMap.put("partyId", sTemp );
	
	String name ="";
	
	if(!UtilValidate.isEmpty(grpName.groupName))  name = name+ grpName.groupName + " " ;
	if(!UtilValidate.isEmpty(grpName.firstName))  name = name+ grpName.firstName + " " ;
	partyMap.put("groupName", name +  "    ["+sTemp+"]"  );
	partyList.add(partyMap);
	}
context.partyList = partyList ;
return "success"