import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

facilityList = delegator.findList("Facility", null, null, null, null, false);

context.facilityList = facilityList;
facilityId = request.getParameter("facilityId");
context.facilityId = facilityId
posTerminalId = request.getParameter("posTerminalId");
context.posTerminalId = posTerminalId;

context.posTerminalList = org.ofbiz.sync.Sync.getPosTerminals(request,response);

expanseDetails =  org.ofbiz.order.report.Report.expanseDetail(delegator,facilityId,posTerminalId,request);
context.expanseDetails =  expanseDetails;

java.math.BigDecimal total = java.math.BigDecimal.ZERO;
if(expanseDetails != null)
{
	for(GenericValue expanseDetail : expanseDetails)
	{
	String amt = expanseDetail.getString("paidAmount");
	expanseDetail.getString();
	if(amt!= null && !amt.equals("")){
		Double value1 =  Double.parseDouble(amt);
		if(value1 != null)
			total = total.add(java.math.BigDecimal.valueOf(value1));
			}
	}
}
context.total = total;

