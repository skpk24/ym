import javolution.util.FastList;
import java.util.List;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityFunction;
 Delegator delegator = (Delegator) request.getAttribute("delegator");
 
 

      
 EntityListIterator productsELI  = delegator.find("DataImportOriginalProduct",EntityCondition.makeCondition("message",EntityOperator.EQUALS,null), null, null, null, null);
	      // List<GenericValue> ProductList=productsELI.getPartialList(0,50);
	 	     	
	 	     			List ProductList = productsELI.getCompleteList();
 
 context.ProductList=ProductList;
 productsELI.close();
  if(!UtilValidate.isEmpty(request.getParameter("productName")))
        {
			
        String productName=request.getParameter("productName");
       	productvariantList= delegator.findList("DataImportOriginalProduct", EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER(productName+"%")), UtilMisc.toSet("productName","productId","brandName"), null, null, false);
		context.productvariantList=productvariantList
        context.productName=productName;
        
        }
        List exprs = FastList.newInstance();
	        exprs.add(EntityCondition.makeCondition("message", EntityOperator.NOT_EQUAL,"Success"));
	        exprs.add(EntityCondition.makeCondition("message", EntityOperator.EQUALS, null));
         EntityCondition mainCond = EntityCondition.makeCondition(exprs, EntityOperator.AND);
       long productImport=delegator.findCountByCondition("DataImportProduct", EntityCondition.makeCondition("message",EntityOperator.EQUALS,"Success"), null, null); 
       long productUpload=delegator.findCountByCondition("DataImportProduct",mainCond, null, null); 
        
       long variantImport=delegator.findCountByCondition("DataImportVariantProduct", EntityCondition.makeCondition("message",EntityOperator.EQUALS,"Success"), null, null); 
       long variantUpload=delegator.findCountByCondition("DataImportVariantProduct", mainCond, null, null);  
        
        long featureImport=delegator.findCountByCondition("DataImportProductFeature", EntityCondition.makeCondition("message",EntityOperator.EQUALS,"Success"), null, null); 
       long featureUpload=delegator.findCountByCondition("DataImportProductFeature", mainCond, null, null);

 List exprs1 = FastList.newInstance();
	        exprs1.add(EntityCondition.makeCondition("message", EntityOperator.NOT_EQUAL,"success"));
	        exprs1.add(EntityCondition.makeCondition("message", EntityOperator.EQUALS, null));
         EntityCondition mainCond1 = EntityCondition.makeCondition(exprs1, EntityOperator.AND);



		long originalImport=delegator.findCountByCondition("DataImportOriginalProduct", EntityCondition.makeCondition("message",EntityOperator.EQUALS,"success"), null, null); 
       long originalUpload=delegator.findCountByCondition("DataImportOriginalProduct", mainCond1, null, null);


        /*context.productUpload=productUpload
        context.productImport=productImport
        context.variantUpload=variantUpload
        context.variantImport=variantImport
         context.featureUpload=featureUpload
        context.featureImport=featureImport*/
        context.originalImport=originalImport
        context.originalUpload=originalUpload
        
        
        
        
        
        