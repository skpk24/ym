
import java.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

List<GenericValue> productList = null;
    	try {
    		productList = delegator.findList("Product", EntityCondition.makeCondition("productId",EntityOperator.NOT_EQUAL,null), null, null, null, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	if(UtilValidate.isNotEmpty(productList))
	for(GenericValue product : productList){
		List conditionList = new ArrayList();
	    EntityCondition condition = null;
    	if(UtilValidate.isNotEmpty(product.productId))
    	{
    			conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,product.productId));
    			conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.NOT_EQUAL,null));
    			conditionList.add(EntityCondition.makeCondition("productPriceTypeId",EntityOperator.EQUALS,"DEFAULT_PRICE"));
    			
    			
    			  List<String> orderByFields = new ArrayList();;
        		  orderByFields.add("fromDate DESC");
    			
				List<GenericValue> productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, orderByFields, null, false);
				if(productPrices.size() > 1)
				{
					for(int i =1;i<= productPrices.size()-1;i++){
						delegator.removeValue((GenericValue)productPrices.get(i))
					}
				}
				conditionList = new ArrayList();
				conditionList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,product.productId));
    			conditionList.add(EntityCondition.makeCondition("fromDate",EntityOperator.NOT_EQUAL,null));
    			conditionList.add(EntityCondition.makeCondition("productPriceTypeId",EntityOperator.EQUALS,"LIST_PRICE"));
    			
				productPrices = delegator.findList("ProductPrice", EntityCondition.makeCondition(conditionList,EntityOperator.AND), null, orderByFields, null, false);
				if(productPrices.size() > 1)
				{
					for(int i =1;i<= productPrices.size()-1;i++){
						delegator.removeValue((GenericValue)productPrices.get(i))
					}
				}
	}	
	}
	
	
	org.ofbiz.product.price.PriceServices.updateAllProductPrice(request,response);