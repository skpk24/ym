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
import org.ofbiz.entity.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.category.CategoryContentWrapper;

detailScreen = "categorydetail";
productCategoryId = request.getAttribute("productCategoryId") ?: parameters.category_id;
	category = delegator.findByPrimaryKeyCache("ProductCategory", [productCategoryId : productCategoryId]);
	if (category) {
		detailScreen = parameters.detailScreen;
	    if ( !detailScreen && category.detailScreen) {
	        detailScreen = category.detailScreen;
	    }
	    context.productCategory = category;
	}

// check the catalogs template path and update
templatePathPrefix = CatalogWorker.getTemplatePathPrefix(request);
if (templatePathPrefix) {
    detailScreen = templatePathPrefix + detailScreen;
}

context.detailScreen = detailScreen;
context.productCategoryId = productCategoryId;

request.setAttribute("productCategoryId", productCategoryId);
request.setAttribute("defaultViewSize", 10);
request.setAttribute("limitView", true);
