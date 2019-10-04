import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
if(request.getParameter("locationSearch")!=null)
{
 Delegator delegator = (Delegator) request.getAttribute("delegator");
        String helperName = delegator.getGroupHelperName("org.ofbiz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        String location=request.getParameter("locationSearch");
        List<GenericValue> locationList=delegator.findList("ZoneType", EntityCondition.makeCondition("pinCode",EntityOperator.EQUALS,location), null, null, null, false);
        if(locationList.size()>0)
        	flag="true";
        else
        	flag="false";
        context.flag=flag;
        context.location=location;
}
        
        
        
        
        