import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

viewreport = parameters.get("viewReport");
getCSV = parameters.get("getCSV");
facilityId = parameters.get("facilityId");
 List exportResult=new ArrayList();
if(viewreport != null || getCSV != null ){

	reportType = parameters.get("reportType");
	if(reportType.equals("InventoryReport")){
		getStockReport();
	}else{
		getOutOfStockReport();
	}
	
}

getEXCEL=parameters.get("getEXCEL");
if(viewreport != null || getCSV != null ||  getEXCEL != null){

reportType = parameters.get("reportType");
if(reportType.equals("InventoryReport")){
getStockReport();
}else{
getOutOfStockReport();
}
}
public void getStockReport(){
        List inventoryResuts=new ArrayList();
		facilityId = parameters.get("facilityId");
		if(facilityId == null) facilityId = "WebStoreWarehouse";
		productId = parameters.get("productId");
		if(productId != null)
			if(!productId.equals(""))
		List productIds = new ArrayList();
		if((productId != null && productId.equals("")) || productId == null ){
		List cond = new ArrayList();
		productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
		}else{
			productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId+"%"),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
		}
	
		
		c=delegator.findByPrimaryKey("Facility",[facilityId : facilityId]);
		facilityName=c.facilityName;
		
		
		
		Iterator productIdsItr = productIds.iterator();
		while(productIdsItr.hasNext()){
			product = (GenericValue) productIdsItr.next();
			productId = (String) product.getString("productId");
			
			productName = (String) product.getString("productName");
			  resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productId, facilityId : facilityId]);
		    quantityOnHandTotal = resultOutput.quantityOnHandTotal;
		    availableToPromiseTotal = resultOutput.availableToPromiseTotal;
		    if(quantityOnHandTotal>0 || availableToPromiseTotal>0 ){
			price=delegator.findByCondition("ProductPrice",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),UtilMisc.toList("productPriceTypeId","price"),null);
		    print("price.ProductPriceTypeId\n\n\n"+price.productPriceTypeId);
		 
		    
		  
		if ((price.productPriceTypeId).contains("DEFAULT_PRICE"))
		{
		  List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
		  priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"DEFAULT_PRICE"));
		    priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		    	EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
		price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
		 
		defaultprice=price1.price;
		
		}
		else
		defaultprice="_NA_";

		if(price.productPriceTypeId.contains("WHOLESALE_PRICE"))
		{
		List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
		  priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"WHOLESALE_PRICE"));
		    priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		    	EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
		price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
		
		
		wholesale=price1.price;
		}
		else
		wholesale="_NA_";
		
		if(price.productPriceTypeId.contains("MAXIMUM_PRICE"))
		{
		List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
		  priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"MAXIMUM_PRICE"));
		  priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
		  EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
		price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
		mrp=price1.price;
		}
		else
		mrp="_NA_";
		    quantitySummary = [:];
		    quantitySummary.productId = productId;
		    
		    if(productName != null && getCSV != null)
		    productName = productName.replaceAll(",", "&");
		    quantitySummary.productName = productName;
		    quantitySummary.facilityId = facilityId;
		    quantitySummary.facilityName=facilityName;
		    quantitySummary.defaultprice=defaultprice;
		    quantitySummary.wholesale=wholesale;
		    quantitySummary.mrp=mrp;
		    print("the price list\n\n\n\n"+defaultprice+wholesale+mrp);
		    quantitySummary.totalQuantityOnHand = resultOutput.quantityOnHandTotal;
		    quantitySummary.totalAvailableToPromise = resultOutput.availableToPromiseTotal;
		    
		    inventoryResuts.add(quantitySummary);	
		    }
		}
		context.listIt = inventoryResuts;
		exportResult=inventoryResuts;
	}	
	
	public void getOutOfStockReport(){
	
		List productIds = new ArrayList();
		List inventoryResuts=new ArrayList();
		productId = parameters.get("productId");
		if((productId != null && productId.equals("")) || productId == null ){
		List cond = new ArrayList();
		productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null),null,UtilMisc.toList("productId") );
		}else{
			productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId+"%"),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
		}
		c=delegator.findByPrimaryKey("Facility",[facilityId : facilityId]);
		facilityName=c.facilityName;
		//productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null),null,UtilMisc.toList("productId") );
		Iterator productIdsItr = productIds.iterator();
		while(productIdsItr.hasNext()){
				product = (GenericValue) productIdsItr.next();
				productId = (String) product.getString("productId");
				productName = (String) product.getString("productName");
				price=delegator.findByCondition("ProductPrice",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),UtilMisc.toList("productPriceTypeId","price"),null);
		    if(price.ProductPriceTypeId=="DEFAULT_PRICE")
		{
		defaultprice=price1.price;
		}
		else
		defaultprice=price.price;
		

		if(price.ProductPriceTypeId=="WHOLESALE_PRICE")
		{
		wholesale=price.price;
		}else 
		wholesale=price.price;
		
		if(price.ProductPriceTypeId=="MAXIMUM_PRICE")
		{
		mrp=price.price;
		}else 
		mrp=price.price;
			    resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", [productId : productId, facilityId : facilityId]);
			    quantityOnHandTotal = resultOutput.quantityOnHandTotal;
			    availableToPromiseTotal = resultOutput.availableToPromiseTotal;
			    
			    
			    if(quantityOnHandTotal<=0 || availableToPromiseTotal<=0){
			    quantitySummary = [:];
			    quantitySummary.productId = productId;
			    if(productName != null && getCSV != null)
			    productName = productName.replaceAll(",", "&");
			    quantitySummary.productName = productName;
			    quantitySummary.facilityId = facilityId;
			    quantitySummary.facilityName = facilityName;
			    quantitySummary.totalQuantityOnHand = resultOutput.quantityOnHandTotal;
			    quantitySummary.totalAvailableToPromise = resultOutput.availableToPromiseTotal;
			    quantitySummary.defaultprice=defaultprice;
			    quantitySummary.facilityName=facilityName;
		    	quantitySummary.wholesale=wholesale;
		    	quantitySummary.mrp=mrp;
			    inventoryResuts.add(quantitySummary);	
			    }
		}
		context.listIt = inventoryResuts;
		exportResult=inventoryResuts;
		
	}
	if(getEXCEL)
		{
		String success=org.ofbiz.order.report.Report.exportInventoryResultToExcel(exportResult);
		}