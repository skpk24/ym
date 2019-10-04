import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

import java.util.*
import java.sql.Timestamp
import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.product.inventory.*
import org.ofbiz.entity.util.*
import javolution.util.FastList;
/*
RecurrenceRuleForPULL=delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId","POSPULL"), false);
RecurrenceRuleInfoRuleForPULL=delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId","POSPULL"), false);

context.RecurrenceRuleForPULL=RecurrenceRuleForPULL;
context.RecurrenceRuleInfoRuleForPULL=RecurrenceRuleInfoRuleForPULL;


RecurrenceRuleForPUSH=delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId","POSPUSH"),false);
RecurrenceRuleInfoRuleForPUSH=delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId","POSPUSH"),false);

context.RecurrenceRuleForPUSH=RecurrenceRuleForPUSH;
context.RecurrenceRuleInfoRuleForPUSH=RecurrenceRuleInfoRuleForPUSH;


JobSandboxForPUSH=delegator.findOne("JobSandbox",UtilMisc.toMap("jobId","POSPUSH"),false);
JobSandboxForPULL=delegator.findOne("JobSandbox",UtilMisc.toMap("jobId","POS1PULL"),false);

context.JobSandboxForPUSH=JobSandboxForPUSH;
context.JobSandboxForPULL=JobSandboxForPULL;
*/


facilityList = delegator.findList("Facility", null, null, null, null, false);

context.facilityList = facilityList;
context.facilityId = request.getParameter("facilityId");
posTerminalId = request.getParameter("posTerminalId");
context.posTerminalId = posTerminalId;

context.posTerminalList = org.ofbiz.sync.Sync.getPosTerminals(request,response);
context.syncList = org.ofbiz.sync.Sync.getEntitySyncFacilityWise(request,response);


List storeTypes = FastList.newInstance();
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "WEB_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "MCOM_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "FBOOK_SALES_CHANNEL"));
storeTypesCond = EntityCondition.makeCondition(storeTypes, EntityOperator.OR);
productStores = delegator.findList("ProductStore", storeTypesCond, null, ["defaultSalesChannelEnumId"], null, false);

context.productStores = productStores;

session = request.getSession();
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
{
	productStoreId = session.getAttribute("productStoreId");
}
if(productStoreId == null)
{
	productStoreId = context.get("productStoreId");
}
if(productStoreId == null && productStores != null && productStores.size() > 0)
{
	if(productStores.size() > 2)
		productStoreId = productStores.get(2).getString("productStoreId");
	else
		productStoreId = productStores.get(0).getString("productStoreId");
}

session.setAttribute("productStoreId",productStoreId);

context.productStoreId = productStoreId;


