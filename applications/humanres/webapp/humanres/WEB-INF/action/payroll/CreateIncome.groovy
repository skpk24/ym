  
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
  
  
  
  
  
  String description =request.getParameter("description");
  String action=request.request.getParameter("action");
  String invoiceItemTypeId=request.request.getParameter("invoiceItemTypeId");
   String glAccountType=request.request.getParameter("glAccountType");
  String geoId=request.request.getParameter("geoId");
    print("radha\n\n\n\n"+parameters);
  //String incomeId=request.getParameter("incomeId");
  
        //String incomeTypeId =request.getParameter("incomeTypeId");
        
        //String unitOfMeasure = request.getParameter("unitOfMeasure");
        //String gainOf= request.getParameter("gainOf");
  		//String glAccountTypeId= request.getParameter("glAccountType");
      
       
        /* if(invoiceItemTypeId!=null && action.equals("create"))
         {
          EntityExpr ee3 = EntityCondition.makeCondition("incomeTypeId", EntityOperator.EQUALS,incomeTypeId);
         List list2= delegator.findList("IncomeType",ee3,null,null,null,false);
          
          if(list2==null || list2.size() == 0 )
          {
          
     
                 GenericValue v2=delegator.makeValue("IncomeType");
                 v2.put("incomeTypeId",incomeTypeId);
                 v2.put("description",description);
                 GenericValue v3=delegator.create(v2);
        
     }
     
        	GenericValue income=delegator.makeValue("Income");
		    income.put("incomeId", delegator.getNextSeqId("Income"));
			income.put("incomeTypeId", incomeTypeId);
			income.put("unitOfMeasure", unitOfMeasure);
			income.put("gainOf",gainOf);
			income.put("description", description);
			
			income.put("glAccountTypeId",glAccountTypeId);
			
			GenericValue incomeV=delegator.create(income);
			
}


if(request.getParameter("incomeId")!=null && action.equals("update"))
{


 GenericValue party = delegator.findByPrimaryKey("Income",[incomeId :incomeId]);
   if(description!=null)
    party.set("description", description);
    
  if(incomeTypeId!=null)
    party.set("incomeTypeId", incomeTypeId);
         
if(unitOfMeasure!=null)
    party.set("unitOfMeasure", unitOfMeasure);

if(gainOf!=null)
    party.set("gainOf",gainOf);
    if(glAccountTypeId!=null)
    party.set("glAccountTypeId",glAccountTypeId);
     party.store();
     
   
     
}*/


if(action.equals("Create"))
         {
         print("checking\n\n\n\n");
          GenericValue v2=delegator.makeValue("InvoiceItemType");
                 v2.put("invoiceItemTypeId",invoiceItemTypeId);
                 v2.put("parentTypeId","PAYROL_EARN_HOURS");
                 v2.put("description",description);
                  v2.put("defaultGlAccountId",glAccountType);
                   v2.put("geoId",geoId);
                   v2.put("hasTable","N");
                  GenericValue v3=delegator.create(v2);
			}
if(action.equals("Update"))
{


 GenericValue party = delegator.findByPrimaryKey("InvoiceItemType",[invoiceItemTypeId :invoiceItemTypeId]);
   if(description!=null)
    party.set("description", description);
    
  if(glAccountType!=null)
    party.set("defaultGlAccountId", glAccountType);
         


if(geoId!=null)
    party.set("geoId",geoId);
    
     party.store();
     
   
     
}


        