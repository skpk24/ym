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
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import java.util.*;
import java.net.*;
import org.ofbiz.security.*;
import org.ofbiz.base.util.*;

delegator = request.getAttribute("delegator");

productList = null;
productStoreId = context.get("productStore").productStoreId;
//get the top level shopping lists for the logged in user
exprList = [EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, "PCCT_WHATS_NEW"),
            EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId)];
condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
categoryList = delegator.findList("ProductStoreCatalogCategory", condition, (HashSet) ["productCategoryId"], ["-sequenceNum"], null, false);

if(UtilValidate.isNotEmpty(categoryList))
{	
	categoryList = EntityUtil.getFieldListFromEntityList(categoryList, "productCategoryId", true);
	exprList = [EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categoryList)];
	condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	productList = delegator.findList("ProductCategoryMember", condition, (HashSet) ["productId"], ["-sequenceNum"], null, false);
}

context.put("products", productList);