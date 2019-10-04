  
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
  
  
  
  
  
  /*String description =request.getParameter("description");
  String action=request.request.getParameter("action");
  print("radha\n\n\n\n"+parameters);
  String taxId=request.getParameter("taxId");
        String taxTypeId =request.getParameter("taxTypeId");
        String geoId = request.getParameter("geoId");
        String taxName= request.getParameter("taxName");
  
      
       
         if(taxTypeId!=null && action.equals("create"))
         {
          EntityExpr ee3 = EntityCondition.makeCondition("taxTypeId", EntityOperator.EQUALS,taxTypeId);
         List list2= delegator.findList("PayrollTaxType",ee3,null,null,null,false);
          
          if(list2==null || list2.size() == 0 )
          {
          
     
                 GenericValue taxType=delegator.makeValue("PayrollTaxType");
                 taxType.put("taxTypeId",taxTypeId);
                 taxType.put("description",description);
                 GenericValue v3=delegator.create(taxType);
        
     }
     
        	GenericValue tax=delegator.makeValue("PayrollTax");
		    tax.put("taxId", delegator.getNextSeqId("PayrollTax"));
			tax.put("taxTypeId", taxTypeId);
			tax.put("taxName", taxName);
			tax.put("geoId",geoId);
			tax.put("description", description);
			GenericValue ta=delegator.create(tax);
}
if(request.getParameter("taxId")!=null && action.equals("update"))
{


 GenericValue party = delegator.findByPrimaryKey("PayrollTax", [taxId : taxId]);
   if(description!=null)
    party.set("description", description);
    
  if(taxTypeId!=null)
    party.set("taxTypeId", taxTypeId);
         
if(taxName!=null)
    party.set("taxName", taxName);

if(geoId!=null)
    party.set("geoId",geoId);
     party.store();
}*/


String description =request.getParameter("description");
  String action=request.request.getParameter("action");
  String invoiceItemTypeId=request.request.getParameter("invoiceItemTypeId");
   String glAccountType=request.request.getParameter("glAccountType");
  String geoId=request.request.getParameter("geoId");
    print("radha\n\n\n\n"+parameters);
    
    
    
    if(action.equals("Create"))
         {
         print("checking\n\n\n\n");
          GenericValue v2=delegator.makeValue("InvoiceItemType");
                 v2.put("invoiceItemTypeId",invoiceItemTypeId);
                 v2.put("parentTypeId","PAYROL_TAXES");
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
        