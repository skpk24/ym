import java.util.*;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;




// possible fields we're searching by
priority = parameters.get("priority");

print("*****************priority****" +priority);
custRequestName = parameters.get("custRequestName");
print("*****************custRequestName****" +custRequestName);

custRequestTypeId = parameters.get("custRequestTypeId");
print("*****************custRequestTypeId****" +custRequestTypeId);
custRequestCategoryId = parameters.get("custRequestCategoryId");
print("*****************custRequestCategoryId****" +custRequestCategoryId);
statusId = parameters.get("statusId");

print("*****************statusId****" +statusId);


