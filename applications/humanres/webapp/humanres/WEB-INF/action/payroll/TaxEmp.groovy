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
  List mainAndExprsCon =[];
        mainAndExprsCon.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PAYROL_TAXES"));
        mainAndExprsCon.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, "IN")); 
 
ItemTypes = delegator.findList("InvoiceItemType",EntityCondition.makeCondition(mainAndExprsCon, EntityOperator.AND),null,null,null, false);
 
context.ItemTypes=ItemTypes;
partyId=parameters.partyId;
context.partyId=partyId;
Double totalTax=0.0;
List item=[];
Double default1=0.0;


if(parameters.action!=null)
{
String partyId=parameters.partyId;
List costValue=parameters.costValue;
print("partyId\n\n\n\n\n"+partyId);
print("costValue\n\n\n\n\n"+costValue);
print("ItemTypes\n\n\n\n\n"+ItemTypes.description);
for(int i=0;i<ItemTypes.size();i++)
{
	item=ItemTypes.invoiceItemTypeId;

}
     List mainAndExprs =[];
        mainAndExprs.add(EntityCondition.makeCondition("attrName", EntityOperator.IN, item));
        mainAndExprs.add(EntityCondition.makeCondition("employeeId", EntityOperator.EQUALS, partyId));
       list2 = delegator.findList("EmplSalAttribute", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND),null,null, null, false);
        
       print("the condn list"+list2.size())
       print("mainAndExprs"+mainAndExprs);
         if(list2.size() == 0 )
          {
  GenericValue empsal=delegator.makeValue("EmplSalAttribute");
  for(int i=0;i<ItemTypes.size();i++)
   {
          if(!costValue[i].equals(""))
          {
          totalTax=Double.parseDouble(costValue[i])+totalTax;
          }
          
      empsal.put("employeeId",partyId);
      empsal.put("attrName",item[i]);
       if(!costValue[i].equals(""))
      empsal.put("attrValue",Double.parseDouble(costValue[i]));
      else
       empsal.put("attrValue",default1);
      empsal.put("attrType","PAYROL_TAXES");
      GenericValue empsalary=delegator.create(empsal);  
             
                 
    }
 }
    else
    {
    for(int i=0;i<ItemTypes.size();i++)
   	{
          if(!costValue[i].equals(""))
          {
          totalTax=Double.parseDouble(costValue[i])+totalTax;
          }
          
      if(costValue[i]!=null)
      {
      list2.get(i).set("attrValue",Double.parseDouble(costValue[i]));
      list2.get(i).store();
      
      }
     }
    }
  
  GenericValue party = delegator.findByPrimaryKey("EmployeeSalary",[employeeId :partyId]);
     print("the party value"+party)
     
     party.set("totalTax",totalTax);
     party.store();
  
  
  }
  
    
    
    
  
  
