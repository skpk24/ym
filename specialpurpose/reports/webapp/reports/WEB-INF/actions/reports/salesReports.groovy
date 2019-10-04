import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.*;
import javolution.util.FastList;

module = "salesReports.groovy";
delegator = request.getAttribute("delegator");
security = request.getAttribute("security");
// create the fromDate for calendar
fromCal = Calendar.getInstance();
fromCal.setTime(new java.util.Date());
//fromCal.set(Calendar.DAY_OF_WEEK, fromCal.getActualMinimum(Calendar.DAY_OF_WEEK));
fromCal.set(Calendar.DATE, fromCal.getActualMinimum(Calendar.DATE));
fromCal.set(Calendar.HOUR_OF_DAY, fromCal.getActualMinimum(Calendar.HOUR_OF_DAY));
fromCal.set(Calendar.MINUTE, fromCal.getActualMinimum(Calendar.MINUTE));
fromCal.set(Calendar.SECOND, fromCal.getActualMinimum(Calendar.SECOND));
fromCal.set(Calendar.MILLISECOND, fromCal.getActualMinimum(Calendar.MILLISECOND));
fromTs = new Timestamp(fromCal.getTimeInMillis());
fromStr = fromTs.toString();
fromStr = fromStr.substring(0, fromStr.indexOf('.'));
context.put("fromDateStr", fromStr);

// create the thruDate for calendar
toCal = Calendar.getInstance();
toCal.setTime(new java.util.Date());
//toCal.set(Calendar.DAY_OF_WEEK, toCal.getActualMaximum(Calendar.DAY_OF_WEEK));
toCal.set(Calendar.HOUR_OF_DAY, toCal.getActualMaximum(Calendar.HOUR_OF_DAY));
toCal.set(Calendar.MINUTE, toCal.getActualMaximum(Calendar.MINUTE));
toCal.set(Calendar.SECOND, toCal.getActualMaximum(Calendar.SECOND));
toCal.set(Calendar.MILLISECOND, toCal.getActualMaximum(Calendar.MILLISECOND));
toTs = new Timestamp(toCal.getTimeInMillis());
toStr = toTs.toString();
context.put("thruDateStr", toStr);

minDate = parameters.get("minDate");
maxDate = parameters.get("maxDate");
if(minDate == null || maxDate == null){
	if(minDate == null)
		minDate = parameters.get("fromDate");
	if(maxDate == null)
		maxDate = parameters.get("thruDate");
}
if(minDate != null){
	context.fromDate=minDate;
	}
else
{
	parameters.put("minDate", fromStr);
	context.fromDate=fromStr;
}

if(maxDate != null){
	
	context.thruDate=maxDate;
}
else
{
	parameters.put("maxDate", toStr);
	context.thruDate=toStr;
}

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
/*List storeTypes = FastList.newInstance();
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "WEB_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "MCOM_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "FBOOK_SALES_CHANNEL"));
storeTypesCond = EntityCondition.makeCondition(storeTypes, EntityOperator.OR);
productStores = delegator.findList("ProductStore", storeTypesCond, null, ["defaultSalesChannelEnumId"], null, false);
if(productStoreId == null && productStores != null && productStores.size() > 0)
{
	productStoreId = productStores.get(0).getString("productStoreId");
}
context.productStoreId = productStoreId;*/


viewReport = request.getParameter("viewReport");
context.viewReport = viewReport;