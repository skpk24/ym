import org.ofbiz.base.util.*;
import java.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import javolution.util.FastList;
import javolution.util.FastMap;




exprs1 = EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, "admin");
unpostedExprs1 = FastList.newInstance();
unpostedExprs1.add(exprs1);
earlybird1 = delegator.findList("ProductEarlyPrice1", EntityCondition.makeCondition(unpostedExprs1, EntityOperator.AND), null, null, null, false);
context.put("earlybird1",earlybird1);
