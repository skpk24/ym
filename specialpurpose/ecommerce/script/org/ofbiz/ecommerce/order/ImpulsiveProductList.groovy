import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.util.EntityListIterator;


delegator = request.getAttribute("delegator");
List<GenericValue> assocProducts = null;
String ajaxCategoryId=request.getParameter("ajaxCategoryId");
 


Set fieldToSelect = new HashSet();
fieldToSelect.add("productId");
if(UtilValidate.isEmpty(ajaxCategoryId)){
	ajaxCategoryId = "BestDeals";
}


///////////////////////////////////////////
bestDealIndex = parameters.bestDealIndex;
if(!bestDealIndex || (Integer.parseInt(bestDealIndex) < 0) ){
	bestDealIndex = "0";
}

int viewSize = 4;
int lowIndex = Integer.parseInt(bestDealIndex)*viewSize;
int highIndex = 0;
int listSize = 0;
//print "\n\n listSize before "+lowIndex + "   " + highIndex  
EntityListIterator eli = null;
List condition = new ArrayList();
condition.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS,ajaxCategoryId));
 condition.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
condition.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null)),EntityOperator.OR));
try{
	eli = delegator.findListIteratorByCondition("ProductCategoryMember", EntityCondition.makeCondition(condition, EntityOperator.AND), fieldToSelect, null);
	if(UtilValidate.isNotEmpty(eli)){
	//	categoryBGv=EntityUtil.getFirst(categoryList);
	//	productCategoryMembers = org.ofbiz.product.product.ProductEvents.productCategoryMembers(delegator,categoryBGv.productCategoryId);
		listSize = delegator.findCountByCondition("ProductCategoryMember",EntityCondition.makeCondition(condition, EntityOperator.AND),null,null);
		
 		 if(listSize <= lowIndex){
		 	lowIndex = 0;
		 }
		 highIndex = lowIndex + viewSize;
		 if(lowIndex == 0) highIndex = viewSize;
		 
		 if(listSize < highIndex){
		 	highIndex = listSize;
		 }
		 
//		 print "\n\n listSize inside"+lowIndex + "   " + highIndex 
		 if(lowIndex == 0){
 		 assocProducts = eli.getPartialList(lowIndex, viewSize);
		 }else{
			 assocProducts = eli.getPartialList((lowIndex + 1 ), viewSize);
		 }
		 
//		 print "\n\n assocProducts"+assocProducts
//		 print "\n complete liest " + eli.getCompleteList();
 
		 context.assocProducts = assocProducts;
//		 productCategoryMembers = productCategoryMembers.subList(lowIndex,highIndex);
//		context.put("productCategoryMembers", productCategoryMembers);
	}
}catch(Exception e){
	 Debug.log("Error Thrown from Impulsive Product List groovy"+e)
}finally{
	eli.close();
}


 if(lowIndex <= 0)
 	preAvail = false;
 else
 	preAvail = true;

 if(highIndex >= listSize)
 {
 	nextAvail = false;
 }
 else
 {
 	nextAvail = true;
 }
 	
context.preAvail = preAvail;
context.nextAvail = nextAvail;
 
 context.bestDealPrevIndex = Integer.parseInt(bestDealIndex) - 1 ;
 context.bestDealCurrIndex = Integer.parseInt(bestDealIndex);
 context.bestDealNextIndex = Integer.parseInt(bestDealIndex) + 1 ;
 context.ajaxCategoryId = ajaxCategoryId;
 
// print  "\n\n categoryId"+context.categoryId;
 
/////////////////////////////////////////



 
	
	
	
 
	

