import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import org.ofbiz.accounting.util.UtilAccounting;

import javolution.util.FastList;

import java.sql.Date;

/*if(parameters.thruDate!=null)

 thruDate = parameters.thruDate;
 else
 thruDate = UtilDateTime.nowTimestamp();*/
 


     andExprs = FastList.newInstance();
    if(parameters.glAccountId!=null)
    {
   glAccountId=parameters.glAccountId;
   andExprs.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
   }
   
  andExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));
  if(parameters.fromDate!=null)
  {
andExprs.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if(parameters.thruDate!=null)
  {
andExprs.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
}
andCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
print("andCond\n\n\n\n\n"+andCond)
List csvList = delegator.findList("GlAccountOrganizationAndClass", andCond, UtilMisc.toSet("glAccountId", "fromDate", "thruDate", "postedBalance","category"), UtilMisc.toList("glAccountId"), null, false);
context.csvList=csvList;

print("csv File\n\n\n\n"+csvList)


