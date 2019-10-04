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
 * NOTE: This script is also referenced by the webpos and ecommerce's screens and
 * should not contain order component's specific code.
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.category.CategoryWorker;
productCategoryId = request.getAttribute("productCategoryId");
context.productCategoryId = productCategoryId;

viewSize = parameters.VIEW_SIZE;
viewIndex = parameters.VIEW_INDEX;
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);

// set the default view size
defaultViewSize = request.getAttribute("defaultViewSize") ?: 20;

context.defaultViewSize = defaultViewSize;

// set the limit view
limitView = request.getAttribute("limitView") ?: true;
context.limitView = limitView;
List sumList=new ArrayList();
// get the product category & members
List<GenericValue> trailCategories = CategoryWorker.getRelatedCategoriesRet(request, "trailCategories", productCategoryId, false, false, true);
if(UtilValidate.isNotEmpty(trailCategories))
{
	sumList= EntityUtil.getFieldListFromEntityList(trailCategories, "productCategoryId", true);
	
}
else
sumList.add(productCategoryId);



refineByPrice = request.getParameter("refineByPrice");
refineByBrand = request.getParameter("refineByBrand");

sortBy = request.getParameter("filterBy");
  if(sortBy == null || "".equals(sortBy.trim()))
  		sortBy  = "POPULAR_PRD";

context.filterBy  = sortBy;

// Prevents out of stock product to be displayed on site
productStore = ProductStoreWorker.getProductStore(request);

excludeOutOfStock = request.getParameter("excludeOutOfStock");
if(excludeOutOfStock == null || "".equals(excludeOutOfStock.trim()))excludeOutOfStock = "N"
context.excludeOutOfStock = excludeOutOfStock;
// get the product category & members
andMap = [productCategoryId : sumList,
        viewIndexString : viewIndex,
        viewSizeString : viewSize,
        defaultViewSize : defaultViewSize,
        limitView : limitView,
        refineByPrice : refineByPrice,
        refineByBrand : refineByBrand,
        sortBy : sortBy,
        excludeOutOfStock : excludeOutOfStock,
        productStore : productStore];
        
andMap.put("prodCatalogId", currentCatalogId);
andMap.put("checkViewAllow", true);
if (context.orderByFields) {
    andMap.put("orderByFields", context.orderByFields);
} else {
    andMap.put("orderByFields", ["sequenceNum", "productId"]);
}
catResult = dispatcher.runSync("getProductCategoryAndLimitedMembers1", andMap);

productCategoryMembers = catResult.productCategoryMembers;
productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));

if(productStore) {
    if("N".equals(productStore.showOutOfStockProducts)) {
        productsInStock = [];
        productCategoryMembers.each { productCategoryMember ->
            product = delegator.findByPrimaryKeyCache("Product", [productId : productCategoryMember.productId]);
            boolean isMarketingPackage = EntityTypeUtil.hasParentType(delegator, "ProductType", "productTypeId", product.productTypeId, "parentTypeId", "MARKETING_PKG");
            context.isMarketingPackage = (isMarketingPackage? "true": "false");
            if (isMarketingPackage) {
                resultOutput = dispatcher.runSync("getMktgPackagesAvailable", [productId : productCategoryMember.productId]);
                availableInventory = resultOutput.availableToPromiseTotal;
                if(availableInventory > 0) { 
                    productsInStock.add(productCategoryMember);
                }
            } else {
                facilities = delegator.findList("ProductFacility", EntityCondition.makeCondition([productId : productCategoryMember.productId]), null, null, null, false);
                availableInventory = 0.0;
                if (facilities) {
                    facilities.each { facility ->
                        lastInventoryCount = facility.lastInventoryCount;
                        if (lastInventoryCount != null) {
                            availableInventory += lastInventoryCount;
                        }
                    }
                    if (availableInventory > 0) {
                        productsInStock.add(productCategoryMember);
                    }
                }
            }
        }
        context.productCategoryMembers = productsInStock;
    } else {
        context.productCategoryMembers = productCategoryMembers;
    }
}
context.productCategory = productCategory;
context.viewIndex = catResult.viewIndex;
context.viewSize = catResult.viewSize;
context.lowIndex = catResult.lowIndex;
context.highIndex = catResult.highIndex;
context.listSize = catResult.listSize;


context.refineByPriceList = catResult.refineByPriceList;
context.refineByBrandList = catResult.refineByBrandList;
context.brandMap = catResult.brandMap;
context.brandList1 = catResult.brandList;
context.priceMap1 = catResult.priceMap;
context.filterKeys = ["Less than Rs 20 ","Rs 21 to 50 ","Rs 51 to 100 ","Rs 101 to 200 ","Rs 201 to 500 ","More than Rs 501 "];

// set this as a last viewed
// DEJ20070220: WHY is this done this way? why not use the existing CategoryWorker stuff?
LAST_VIEWED_TO_KEEP = 10; // modify this to change the number of last viewed to keep
lastViewedCategories = session.getAttribute("lastViewedCategories");
if (!lastViewedCategories) {
    lastViewedCategories = [];
    session.setAttribute("lastViewedCategories", lastViewedCategories);
}
lastViewedCategories.remove(productCategoryId);
lastViewedCategories.add(0, productCategoryId);
while (lastViewedCategories.size() > LAST_VIEWED_TO_KEEP) {
    lastViewedCategories.remove(lastViewedCategories.size() - 1);
}

// set the content path prefix
contentPathPrefix = CatalogWorker.getContentPathPrefix(request);
context.put("contentPathPrefix", contentPathPrefix);

// little routine to see if any members have a quantity > 0 assigned
members = context.get("productCategoryMembers");
context.allProdListOfCateg = org.ofbiz.entity.util.EntityUtil.getFieldListFromEntityList(members, "productId", true)

CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(productCategory, request);
context.put("categoryContentWrapper", categoryContentWrapper);
