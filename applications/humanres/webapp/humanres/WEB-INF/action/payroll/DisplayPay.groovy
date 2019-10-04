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
  



String employeeId =request.getParameter("employeeId");
if(employeeId!=null)
{
context.employeeId=employeeId;
 GenericValue party = delegator.findByPrimaryKey("EmployeeSalary", [employeeId : employeeId]);
           grossPay=party.get("grossPay");
           print("grossPay"+grossPay);
           netDeduction=party.get("netDeduction")
           print("netDeduction"+netDeduction);
           netPay=party.get("netPay");
           print("netPay"+netPay);
           context.grossPay=grossPay;
            context.netDeduction=netDeduction;
             context.netPay=netPay;
    
      List mainAndExprs =[];
        mainAndExprs.add(EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, "Income"));
        mainAndExprs.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId));
   
         List listValue= delegator.findList("EmplSalAttribute", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND),null,null, null, false);
            List attrName=listValue.getAt("attrName");
            context.attrName=attrName;
          print("attrName"+attrName);
            List attValue=listValue.getAt("attrValue");
             print("attrValue"+attValue);
              context.attrValue=attValue;
              context.listValue=listValue;
               List mainAndExprsDed =[];
        mainAndExprsDed.add(EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, "Deduction"));
        mainAndExprsDed.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, employeeId));
   
         List listValueDed= delegator.findList("EmplSalAttribute", EntityCondition.makeCondition(mainAndExprsDed, EntityOperator.AND),null,null, null, false);
            List attrNameDed=listValueDed.getAt("attrName");
            context.attrNameDed=attrNameDed;
          print("attrName"+attrNameDed);
            List attValueDed=listValueDed.getAt("attrValue");
             print("attValueDed"+attValueDed);
              context.attValueDed=attValueDed;
              context.listValueDed=listValueDed;
              }