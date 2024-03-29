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

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;
import javolution.util.FastMap;
import org.ofbiz.order.shoppingcart.*;


currentCatalogId=CatalogWorker.getCurrentCatalogId(request);
context.currentCatalogId=currentCatalogId;
CategoryWorker.getRelatedCategories(request, "topLevelList", CatalogWorker.getCatalogTopCategoryId(request, CatalogWorker.getCurrentCatalogId(request)), true);
curCategoryId = parameters.category_id ?: parameters.CATEGORY_ID ?: "";
request.setAttribute("curCategoryId", curCategoryId);
CategoryWorker.setTrail(request, curCategoryId);

categoryList = request.getAttribute("topLevelList");
if (categoryList) {
	catName = FastMap.newInstance();
	catName=delegator.findByPrimaryKey("ProdCatalog", [prodCatalogId : currentCatalogId]);
	context.catName = catName;
}

if (categoryList) {
    catContentWrappers = FastMap.newInstance();
    CategoryWorker.getCategoryContentWrappers(catContentWrappers, categoryList, request);
    context.catContentWrappers = catContentWrappers;
}
/*
cart = ShoppingCartEvents.getCartObject(request);
categoryIdList = new java.util.ArrayList();
if(cart != null){
	items = cart.items();
	for(item in items){
		if(!categoryIdList.contains(item.getProductCategoryId()))
			categoryIdList.add(item.getProductCategoryId());
	}
}
*/

categoryId = parameters.category_id ?: CatalogWorker.getCatalogQuickaddCategoryPrimary(request);

if(UtilValidate.isNotEmpty(categoryId)){
String [] parts = categoryId.split(",");

java.util.List data = new java.util.ArrayList();

for(part in parts){
	data.add(" "+part);
}
context.categoryIdList1 = data;
context.categoryIdList = java.util.Arrays.asList(parts);
}else{
context.categoryIdList = null;
}
