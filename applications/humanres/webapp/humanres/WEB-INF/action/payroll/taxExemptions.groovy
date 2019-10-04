import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import java.util.*;

String action = parameters.action;
 if(action!=null && "create".equals(action))
  {
    GenericValue taxExm = delegator.makeValue("TaxExemption");
    String  taxExmId = delegator.getNextSeqId("TaxExemption");
    taxExm.put("taxExemptionId",taxExmId);
    taxExm.put("exemptionName",parameters.exemptionName);
    taxExm.put("description",parameters.description);
    taxExm.put("limit",Long.parseLong(parameters.limit));
    taxExm.put("mode",parameters.mode);
     taxExm.put("child","N");
    taxExm.create();
  }