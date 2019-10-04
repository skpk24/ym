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

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
  
 
List mainAndExprsCon =[];
        mainAndExprsCon.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "PAYROL_DD_FROM_GROSS"));
        mainAndExprsCon.add(EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, "IN")); 
  
ItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(mainAndExprsCon, EntityOperator.AND), null, ["description", "description"], null, false);
context.ItemTypes=ItemTypes;
partyId = parameters.partyId;
print "\n\n #################### partyId ="+ parameters.partyId

partyId=partyId;
List item=[];
Double total=0.0;
if(parameters.action!=null)
{
String partyId=parameters.partyId;
List costValue=parameters.costValue;
print("partyId\n\n\n\n\n"+partyId);
print("costValue\n\n\n\n\n"+costValue);
print("ItemTypes\n\n\n\n\n"+ItemTypes.description);
for(int i=0;i<ItemTypes.size();i++)
{
	item=ItemTypes.description;

}
  GenericValue empsal=delegator.makeValue("EmplSalAttribute");
  for(int i=0;i<ItemTypes.size();i++)
   {
 	  if(!costValue[i].equals(""))
 	  	total=Double.parseDouble(costValue[i])+total;
      empsal.put("employeeId",partyId);
      empsal.put("attrName",item[i]);
      if(!costValue[i].equals(""))
	      empsal.put("attrValue",Double.parseDouble(costValue[i]));
	  else
	      empsal.put("attrValue",Double.parseDouble("0.0"));    
      empsal.put("attrType","Deduction");
      GenericValue empsalary=delegator.create(empsal);  
             
                 
    }
    
    
    	Double taxTotal = 0.0;
		List getList = new ArrayList();
		getList.add(EntityCondition.makeCondition("employeeId",EntityOperator.EQUALS,partyId));
		getList.add(EntityCondition.makeCondition("attrName",EntityOperator.EQUALS,"TotalTax"));
		EntityCondition condition = EntityCondition.makeCondition(getList);
		try {
			EntityListIterator list = delegator.find("EmplSalAttribute", condition, null, null, null, null);
			while(list.hasNext()){
				taxTotal = (Double) list.next().get("attrValue");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
   	total = total+taxTotal;
   	//System.out.println("###########  total "+total);
   	
   	GenericValue party = delegator.findByPrimaryKey("EmployeeSalary",[employeeId :partyId]);
    party.set("netDeduction",total);
    party.store();
    
    
    GenericValue partynetdeduction = delegator.findByPrimaryKey("EmployeeSalary",[employeeId :partyId]);
    //System.out.println("##################netIncome    "+partynetdeduction.get("grossPay")+"##################netDeduction    "+partynetdeduction.get("netDeduction"));
    Double netIncome = partynetdeduction.get("grossPay");
    Double netDeduction = partynetdeduction.get("netDeduction");
    Double netPay = netIncome-netDeduction;
    //System.out.println("##################netIncome    "+netIncome+"##################netDeduction    "+netDeduction+"##################netPay    "+netPay);
    partynetdeduction.set("netPay",netPay);
    partynetdeduction.store();
    

  }
