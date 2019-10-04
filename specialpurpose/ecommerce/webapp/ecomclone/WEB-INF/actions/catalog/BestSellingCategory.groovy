import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
//import javolution.util.FastMap;
import java.util.HashMap;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

List exprList=[];
catalogId=org.ofbiz.product.catalog.CatalogWorker.getCurrentCatalogId(request);

exprList.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, catalogId));
exprList.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, "PCCT_MOST_POPULAR"));

condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
categoryList = delegator.findList("ProdCatalogCategory", condition, null, null, null, false);


bestSallingIndex = parameters.bestSallingIndex;
if(!bestSallingIndex || (Integer.parseInt(bestSallingIndex) < 0) ) bestSallingIndex = "0";

viewSize = 4;

lowIndex = Integer.parseInt(bestSallingIndex)*viewSize;

 if(UtilValidate.isNotEmpty(categoryList))
 {
	 categoryBGv=EntityUtil.getFirst(categoryList);
	 popularProducts = org.ofbiz.product.product.ProductEvents.productCategoryMembers(delegator,categoryBGv.productCategoryId);
	 
	 listSize = popularProducts.size();
	 if(listSize <= lowIndex)
	 {
	 	lowIndex = 0;
	 	bestSallingIndex = "0";
	 }
	 highIndex = lowIndex + viewSize;
	 if(lowIndex == 0) highIndex = viewSize;
	 if(listSize < highIndex)
	 {
	 	highIndex = listSize;
	 }
	 
	 popularProducts = popularProducts.subList(lowIndex,highIndex);
	 context.put("popularProducts", popularProducts);
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
 
 context.bestSallingPrevIndex = Integer.parseInt(bestSallingIndex) - 1 ;
 context.bestSallingCurrIndex = Integer.parseInt(bestSallingIndex);
 context.bestSallingNextIndex = Integer.parseInt(bestSallingIndex) + 1 ;
 
 HashMap allPriceMap =new HashMap(); //FastMap.newInstance();
 if (context.popularProducts) {
 	productStore = org.ofbiz.product.store.ProductStoreWorker.getProductStore(request);
 	it = context.popularProducts.iterator();
  	while(it.hasNext())
  	{
	  	pcm = it.next();
	  	product = pcm.getRelated("Product");
	  	if(product){
	  		product = product.get(0);
	  	}
	     priceContext = [product : product, currencyUomId : productStore.defaultCurrencyUomId];
	     priceContext.webSiteId = webSiteId;
	     priceContext.prodCatalogId = catalogId;
	     priceContext.productStoreId = productStore.productStoreId;
	     priceContext.checkIncludeVat = "Y";
	     priceMap = dispatcher.runSync("calculateProductPrice", priceContext);
	     allPriceMap.put(product.productId, priceMap);
	  }
     context.mpPriceMap = allPriceMap;
 } 
