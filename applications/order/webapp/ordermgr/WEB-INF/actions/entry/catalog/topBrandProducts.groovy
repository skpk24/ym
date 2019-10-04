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

import java.net.Authenticator.RequestorType;

import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.feature.*;
import org.ofbiz.product.product.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

module = "KeywordSearch.groovy";


//ProductSearchSession.processSearchParameters(parameters, request);
prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
result = org.ofbiz.order.shoppingcart.ShoppingCartEvents.topBrandProducts(request,response);

 

List brandListDetail = new ArrayList();
productStore = ProductStoreWorker.getProductStore(request);
 
productIdsNew = result.get("productIds");
if(productIdsNew != null && productIdsNew.size() > 0)
	{
		String newProdId = (String)productIdsNew.get(0);
		if(newProdId != null && !"".equals(newProdId.trim()))
			session.setAttribute("CURRENT_CATALOG_ID",org.ofbiz.product.category.CategoryWorker.getProdCatalogId(delegator,newProdId));
		else
			session.setAttribute("CURRENT_CATALOG_ID","VEGFRUIT");
	}
context.productIds = productIdsNew;
context.allProdListOfCateg = productIdsNew;
context.relatedProductList = result.get("relatedProductList");
context.relatedProductCategoryName = result.get("relatedProductCategoryName");
context.priceMap1 = result.get("priceMap");
context.categoryMap = result.get("categoryMap");
context.filterKeys = result.get("filterKeys");
context.filter = result.get("filter");
context.filterCategory = result.get("filterCategory");
context.filterBy = request.getParameter("filterBy");
context.excludeOutOfStock = result.get("excludeOutOfStock")

context.viewIndex = result.viewIndex;
context.viewSize = result.viewSize;
context.listSize = result.listSize;
context.lowIndex = result.lowIndex;
context.highIndex = result.highIndex;
context.paging = result.paging;
context.previousViewSize = result.previousViewSize;
context.searchCategory = result.searchCategory;
context.searchConstraintStrings = result.searchConstraintStrings;
context.searchSortOrderString = result.searchSortOrderString;
context.productStore = productStore;
context.sortBy = request.getParameter("sortBy");
context.searchString = session.getAttribute("SEARCH_STRING");


 