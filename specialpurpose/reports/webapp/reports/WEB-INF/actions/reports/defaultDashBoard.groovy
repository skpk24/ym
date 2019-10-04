import org.ofbiz.accounting.Reports;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;

imagelocation = Reports.viewSalesDaily(delegator, session);
context.imagelocation = imagelocation
user=userLogin.userLoginId;
print("the user\n\n\n\n\n\n"+user);
 List orderBy = UtilMisc.toList("-createdStamp");
urlList = delegator.findList("Visit", EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,user), UtilMisc.toSet("initialRequest"), orderBy, null, true);
context.urlList=urlList;
