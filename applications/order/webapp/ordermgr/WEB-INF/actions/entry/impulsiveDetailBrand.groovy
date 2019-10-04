import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import java.util.*;
import java.net.*;
import org.ofbiz.security.*;
import org.ofbiz.base.util.*;
import javax.servlet.http.HttpSession;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;
import org.ofbiz.base.util.UtilMisc;
requestParams = UtilHttp.getParameterMap(request);
delegator = request.getAttribute("delegator");
List<GenericValue> assocProducts = null;
categoryId = parameters.category_id ?: request.getAttribute("productCategoryId");

context.productStore = ProductStoreWorker.getProductStore(request);

//String categoryId=request.getParameter("productCategoryId");
product = request.getAttribute("optProduct");
optProductId = request.getAttribute("optProductId");
productId = product?.productId ?: optProductId ?: request.getAttribute("productId");
context.productId = productId;
productsFromCategoryIndex = "0";
productsFromBrandIndex = "0";

if(UtilValidate.isEmpty(productId))
productId = requestParams.product_id ?: requestParams.optProductId ?: request.getAttribute("product_id") ?: session.getAttribute("productId");
	
GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
		productNew = org.ofbiz.product.product.ProductWorker.getParentProduct(productId, delegator);
	    if(productNew != null)
	    {
		  product = productNew;
		  productId = product.productId;
		}

if(UtilValidate.isEmpty(categoryId)){
	categoryId=product.primaryProductCategoryId;
}
List variant=[];
Set fieldToSelect = new HashSet();
fieldToSelect.add("productId");

GenericValue virtual =  product;


if(UtilValidate.isNotEmpty(virtual))
{
if((virtual.isVirtual).equals("Y"))
{

	variant=delegator.findList("ProductAssoc",EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId) , UtilMisc.toSet("productIdTo"), null, null, false);
    
	}
}

List categoryList = new ArrayList();
List productCondnList = new ArrayList();
	if(UtilValidate.isNotEmpty(categoryId)){
		categoryId1=null;
	conditionList = [];
	productCondnList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
	
	
	List fromDateCond = new ArrayList();
	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    
    productCondnList.add(EntityCondition.makeCondition(fromDateCond, EntityOperator.OR));
	
	List thruDateCondList = new ArrayList();
	thruDateCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	thruDateCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
	
	productCondnList.add(EntityCondition.makeCondition(thruDateCondList, EntityOperator.OR));
	
	List salesDiscontinuationDateCond = new ArrayList();
	salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
	salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
	productCondnList.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
	
	
	
	categoryNameList = delegator.findList("ProductAndCategoryMember",EntityCondition.makeCondition(productCondnList, EntityOperator.AND) ,null, null, null, false);
	
	GenericValue gvTem=EntityUtil.getFirst(categoryNameList);
	if(UtilValidate.isNotEmpty(gvTem))
	{
		categoryId1=gvTem.get("productCategoryId");
	}

	conditionList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId1));
	conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, productId));
	if(UtilValidate.isNotEmpty(variant)){
		variant.each { lastTimePeriodHistory ->
		conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, lastTimePeriodHistory.productIdTo));
		}
	}
	
	conditionList.add(EntityCondition.makeCondition(fromDateCond, EntityOperator.OR));
	conditionList.add(EntityCondition.makeCondition(thruDateCondList, EntityOperator.OR));
	conditionList.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
	
		entityCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
		categoryList = delegator.findList("ProductAndCategoryMember",entityCondition ,fieldToSelect, null, null, false);


		productsFromCategoryIndex = parameters.productsFromCategoryIndex;
		if(!productsFromCategoryIndex || (Integer.parseInt(productsFromCategoryIndex) < 0) ) productsFromCategoryIndex = "0";
		viewSize = 4;
		lowIndex = Integer.parseInt(productsFromCategoryIndex)*viewSize;
		
		 listSize = categoryList.size();
		 if(listSize <= lowIndex)
		 {
		 	lowIndex = 0;
		 }
		 highIndex = lowIndex + viewSize;
		 if(lowIndex == 0) highIndex = viewSize;
		 if(listSize < highIndex)
		 {
		 	highIndex = listSize;
		 }
		 //context.allProdListOfCateg = EntityUtil.getFieldListFromEntityList(categoryList, "productId", true);
		 categoryList = categoryList.subList(lowIndex,highIndex);
		 
		 context.categoryList = categoryList;
		 
		 
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

context.productsFromCategoryPrevIndex = Integer.parseInt(productsFromCategoryIndex) - 1 ;
context.productsFromCategoryCurrIndex = Integer.parseInt(productsFromCategoryIndex);
context.productsFromCategoryNextIndex = Integer.parseInt(productsFromCategoryIndex) + 1 ;	
	}	
	
	
	if(UtilValidate.isNotEmpty(productId) && !productId.equals("GIFTCARD")) {
		
	
		if(UtilValidate.isNotEmpty(product))
		{
		dontSelectProductids = new ArrayList();
		categoryList.each { category ->
			dontSelectProductids.add(category.productId);
		}
		dontSelectProductids.add(productId)
			if(product.brandName)
			{
				brandList = org.ofbiz.order.shoppingcart.ShoppingCartEvents.getBrandProducts(product.brandName,null,dontSelectProductids, delegator);
			}
			else{
				brandList = null;
			}
		}
		if(brandList){
			productsFromBrandIndex = parameters.productsFromBrandIndex;
			if(!productsFromBrandIndex || (Integer.parseInt(productsFromBrandIndex) < 0) ) productsFromBrandIndex = "0";
			viewSize = 4;
			lowIndex = Integer.parseInt(productsFromBrandIndex)*viewSize;
			
			 listSize = brandList.size();
			 if(listSize <= lowIndex)
			 {
			 	lowIndex = 0;
			 }
			 highIndex = lowIndex + viewSize;
			 if(lowIndex == 0) highIndex = viewSize;
			 if(listSize < highIndex)
			 {
			 	highIndex = listSize;
			 }
			 brandList = brandList.subList(lowIndex,highIndex);
		}
		context.brandList = brandList;
		
		preAvail = false;
		nextAvail = false;
		
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
		
		context.preAvailBrand = preAvail;
		context.nextAvailBrand = nextAvail;
		
		context.productsFromBrandPrevIndex = Integer.parseInt(productsFromBrandIndex) - 1 ;
		context.productsFromBrandCurrIndex = Integer.parseInt(productsFromBrandIndex);
		context.productsFromBrandNextIndex = Integer.parseInt(productsFromBrandIndex) + 1 ;	
		
	}
