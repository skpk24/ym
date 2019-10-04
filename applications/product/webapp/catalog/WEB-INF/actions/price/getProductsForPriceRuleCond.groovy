import org.ofbiz.entity.condition.*

if(parameters.productCategoryId)
{
	context.productCategoryMembers = org.ofbiz.product.category.CategoryWorker.getAllVAriantAndNormalProducts(delegator,dispatcher, parameters.productCategoryId);
}else
	context.productCategoryMembers = null;

context.condOperEnums = delegator.findList("Enumeration", EntityCondition.makeCondition([enumTypeId : 'PROD_PRICE_COND']), null, ['sequenceId'], null, true);
