import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.marketing.util.SFAUtil;



userLoginId = userLogin.userLoginId

userLoginAndSecurityGroup= [];
userLoginAndSecurityGroup = delegator.findList("UserLoginAndSecurityGroup",EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),null,null,null,false);
groupIds = [] ;
groupIds = userLoginAndSecurityGroup.groupId ;
boolean check = groupIds.contains("FULLADMIN");

securityConditionList = [];
securityConditions = []

			securityConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId",EntityOperator.EQUALS,"SALES_ASSIGN"))

			
			if(!check){
				securityOrCondition = []
				userLoginPartyId = delegator.findByPrimaryKey("UserLogin",[userLoginId:userLoginId]);
				securityOrCondition.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,userLoginId));
				securityOrCondition.add(EntityCondition.makeCondition("partyIdTo",EntityOperator.EQUALS,userLoginPartyId.partyId));
				securityOrConditions = EntityCondition.makeCondition(securityOrCondition,EntityOperator.OR);
				securityConditions.add(securityOrConditions)
			}
			
			
			
securityConditions = EntityCondition.makeCondition(securityConditions,EntityOperator.AND);
PartyRelationshipAndDetail = delegator.findList("PartyRelationshipAndDetail",securityConditions,null,null,null,false);

partyIdList = []

partyIdList = PartyRelationshipAndDetail.partyIdFrom


def statusList = delegator.findList("StatusItem",EntityCondition.makeCondition("statusTypeId",EntityOperator.EQUALS,"SFA_ERP_LEAD_STATUS"),
                                   UtilMisc.toSet("statusId"),null,null,false);
def statusListValues = [];

statusList.each {
	value-> statusListValues.add(value.statusId); 
}
							   
def listist =   delegator.findList("PartyStatus",
			EntityCondition.makeCondition([
				EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIdList),
				EntityCondition.makeCondition("statusId", EntityOperator.IN, statusListValues)
				],EntityOperator.AND),null,null,null,false)
			
			
resultList = []
for(j in 0..listist.size()-1){
	gv = (GenericValue) listist.get(j);
	String partyId = 	gv.partyId ;
	String statusDate = gv.statusDate ;
	opportunityMap = [:]
	
	
	person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",partyId))	;
	status = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId",gv.statusId))	;
	
	opportunityMap.put("statusDate",statusDate.toString())
	opportunityMap.put("status",status.description)
	opportunityMap.put("createdDate",person.createdDate.toString())
	
	if(!UtilValidate.isEmpty(person)){
		opportunityMap.put("cmpnyName",  person.firstName)
	}
	


	resultList.add(opportunityMap) ;
}

context.resultList = resultList ;
session.setAttribute("CompaniesByStatus_SFA_REPORTS", resultList);