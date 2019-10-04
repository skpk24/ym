import org.ofbiz.entity.*
import org.ofbiz.base.util.*
import java.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


productId = request.getParameter("productId");
if(productId.equals(""))
	productId = null;
//set the page parameters
Integer viewIndex = request.getParameter("viewIndex") ? Integer.valueOf(request.getParameter("viewIndex")) : 0;
context.viewIndex = viewIndex;

Integer viewSize = request.getParameter("viewSize") ? Integer.valueOf(request.getParameter("viewSize")) : 20;
context.viewSize = viewSize;

//Integer viewIndex = (Integer) parameters.get("viewIndex");
//Integer viewSize = (Integer) parameters.get("viewSize");
int lowIndex = (((viewIndex.intValue() - 1) * viewSize.intValue()) + 1);
int highIndex = viewIndex.intValue() * viewSize.intValue();
EntityListIterator eli = null;
List productList = new ArrayList();
	
try{
	
    conditions = new ArrayList();
    if(productId != null){
    	conditions.add(new EntityExpr("productId", EntityOperator.EQUALS, productId));
    }else{
    	conditions.add(new EntityExpr("productId", EntityOperator.NOT_EQUAL, null));
    	conditions.add(new EntityExpr("isVirtual", EntityOperator.EQUALS, "Y"));
    }
	
    List orderBy = UtilMisc.toList("productId");
    eli = delegator.findListIteratorByCondition("Product", new EntityConditionList(conditions, EntityOperator.AND), UtilMisc.toList("productId"), orderBy);
    
    // attempt to get the full size
    eli.last();
    productCount = eli.currentIndex();

    // get the partial list for this page
    eli.beforeFirst();
    if (productCount > viewSize.intValue()) {
        productList = eli.getPartialList(lowIndex, viewSize.intValue());
    } else if (productCount > 0) {
    	productList = eli.getCompleteList();
    }
    if (highIndex > productCount) {
        highIndex = productCount;
    }
} catch (GenericEntityException e) {
    e.printStackTrace();
} finally {
    if (eli != null) {
        try {
            eli.close();
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
}

Iterator productItr = productList.iterator();

List wanted = new ArrayList();
while(productItr.hasNext()){
	GenericValue product = (GenericValue) productItr.next();
	Map want = new HashMap();
	
	want.put("parentProductId", product.productId );	
	
	//result1 = dispatcher.runSync("getProductFeaturesByType", [productId : product.productId, productFeatureApplTypeId : 'SELECTABLE_FEATURE']);
	//context.featureTypes = result1.productFeatureTypes;
	
	result = dispatcher.runSync("getVariantCombinations", [productId : product.productId]);
	//context.featureCombinationInfos = result.featureCombinations;
	
	List variants = new ArrayList();
	List l1 = (List)result.featureCombinations;
	Iterator itr = l1.iterator();
	while(itr.hasNext()){
		temp = itr.next();
		Map variantsMap = new HashMap();		
		variantsMap.put("variantProductId",temp.existingVariantProductIds);
		//println("existingVariantProductIds"+temp.existingVariantProductIds);	
		List abc = temp.curProductFeatureAndAppls;
		
		if(temp.existingVariantProductIds.size()>0){
		
		List features = new ArrayList();
		Iterator itrA = abc.iterator();
		while(itrA.hasNext()){
			abcd  = itrA.next();
			Map feature = new HashMap();
			feature.put("productFeatureTypeId",abcd.productFeatureTypeId);
			feature.put("description",abcd.description);
			features.add(feature);
		}
		variantsMap.put("features",features);
		//println("variantsMap"+variantsMap);	
		variants.add(variantsMap);
		}
	}
	want.put("variants",variants);
	wanted.add(want);
}
context.put("wanted",wanted);