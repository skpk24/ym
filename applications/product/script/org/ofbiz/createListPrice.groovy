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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.CheckOutHelper;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

dispatcher = request.getAttribute("dispatcher");
delegator = request.getAttribute("delegator");

productPrice = delegator.findList("ProductPrice", null, null, null, null, true);

products = EntityUtil.getFieldListFromEntityList(productPrice, "productId", true);

it = products.iterator();
while(it.hasNext()){
	productId = it.next();
	
	defaultPrice = EntityUtil.getFirst(EntityUtil.filterByAnd(productPrice, [productId: productId, productPriceTypeId:"DEFAULT_PRICE"]));
	if(defaultPrice){
		listPrice = EntityUtil.filterByAnd(productPrice, [productId: productId, productPriceTypeId:"LIST_PRICE"]);
		if(!listPrice){
			listPriceGv = delegator.makeValue("ProductPrice", [productId : productId, productPriceTypeId: "LIST_PRICE", productPricePurposeId:"PURCHASE", currencyUomId:"INR", productStoreGroupId:"_NA_", fromDate : UtilDateTime.nowTimestamp()]);
			listPriceGv.set("price", defaultPrice.price);
			print(" listPriceGv == "+listPriceGv+"\n\n");
			listPriceGv.create();
		}
	}
}

return "success";


