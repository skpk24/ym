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
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import org.ofbiz.product.product.ProductContentWrapper;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.store.*;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.product.product.ProductWorker;
import java.text.NumberFormat;

//either optProduct, optProductId or productId must be specified
product = request.getAttribute("optProduct");
if(!product){
	product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId",request.getAttribute("optProductId")));
}
optProductId = request.getAttribute("optProductId");
Originalquantity = request.getAttribute("Originalquantity");
if(UtilValidate.isEmpty(Originalquantity))
Originalquantity = request.getParameter("Originalquantity");
if(UtilValidate.isNotEmpty(Originalquantity))
context.Originalquantity=Originalquantity;
productId = product?.productId ?: optProductId ?: request.getAttribute("productId");
if(UtilValidate.isEmpty(productId))
	productId = request.getParameter("optProductId");
	
	
	GenericValue product = ProductWorker.getParentProduct(productId, delegator);
	    if(UtilValidate.isNotEmpty(product))
	    {
	    	productId = product.getString("productId");
	    }
	
	
webSiteId = CatalogWorker.getWebSiteId(request);
catalogId = CatalogWorker.getCurrentCatalogId(request);
cart = ShoppingCartEvents.getCartObject(request);
productStore = null;
productStoreId = null;
facilityId = null;
if (cart.isSalesOrder()) {
    productStore = ProductStoreWorker.getProductStore(request);
    productStoreId = productStore.productStoreId;
    context.productStoreId = productStoreId;
    context.productStoreId = productStoreId;
    facilityId = productStore.inventoryFacilityId;
}
autoUserLogin = session.getAttribute("autoUserLogin");
userLogin = session.getAttribute("userLogin");

context.remove("daysToShip");
context.remove("averageRating");
context.remove("numRatings");
context.remove("totalPrice");

// get the product entity
if (!product && productId) {
    product = delegator.findByPrimaryKeyCache("Product", [productId : productId]);
	
}
if (product) {
    //if order is purchase then don't calculate available inventory for product.
    if (cart.isSalesOrder()) {
    if(!"Y".equals(product.isVirtual))
    	context.isStoreInventoryAvailable = org.ofbiz.product.store.ProductStoreWorker.isStoreInventoryAvailable(request, product, java.math.BigDecimal.ONE);
	else
		context.isStoreInventoryAvailable = true;
    } else {
       supplierProducts = delegator.findByAndCache("SupplierProduct", [productId : product.productId], ["-availableFromDate"]);
       supplierProduct = EntityUtil.getFirst(supplierProducts);
       if (supplierProduct?.standardLeadTimeDays != null) {
           standardLeadTimeDays = supplierProduct.standardLeadTimeDays;
           daysToShip = standardLeadTimeDays + 1;
           context.daysToShip = daysToShip;
       }
    }
    // make the productContentWrapper
    productContentWrapper = new ProductContentWrapper(product, request);
    context.productContentWrapper = productContentWrapper;
}

if (product) {
	context.productStore = ProductStoreWorker.getProductStore(request);

    // get the product price
    if (cart.isSalesOrder()) {
        // sales order: run the "calculateProductPrice" service
        priceContext = [product : product, currencyUomId : cart.getCurrency(),
                autoUserLogin : autoUserLogin, userLogin : userLogin];
        priceContext.webSiteId = webSiteId;
        priceContext.prodCatalogId = catalogId;
        priceContext.productStoreId = productStoreId;
        priceContext.agreementId = cart.getAgreementId();
        priceContext.partyId = cart.getPartyId();  // IMPORTANT: otherwise it'll be calculating prices using the logged in user which could be a CSR instead of the customer
        priceContext.checkIncludeVat = "Y";
        priceMap = dispatcher.runSync("calculateProductPrice", priceContext);

        context.price = priceMap;
    } else {
        // purchase order: run the "calculatePurchasePrice" service
        priceContext = [product : product, currencyUomId : cart.getCurrency(),
                partyId : cart.getPartyId(), userLogin : userLogin];
        priceMap = dispatcher.runSync("calculatePurchasePrice", priceContext);

        context.price = priceMap;
    }
    
    context.mainProducts = mainProducts;
}

// an example of getting features of a certain type to show
String br=product.productName;
int one  =  br.indexOf("-");
String part1=null;
String part2=null;

if(one > 0){
String[] parts = br.split("-");
 part1 = parts[0]; 
 part2 = parts[1];
 context.br = part1;
 context.br1 = part2;
}else{
context.br = product.productName;
context.br1 = "";
}
//context.br=splitResult[0];

context.product = product;


context.ingProdId  = productId;
from = request.getAttribute("from");
 if(UtilValidate.isNotEmpty(request.getAttribute("optProductId")))
{
	context.ingProdId  = request.getAttribute("mainProdId");
	assocProduct = org.ofbiz.product.product.ProductServices.findAllAssocRecipeIngs(delegator, dispatcher, request.getAttribute("optProductId"),true);
	 context.assocProducts = assocProduct
 }
else if(UtilValidate.isNotEmpty(from))
{
	context.assocProducts = org.ofbiz.product.product.ProductServices.findAllAssocRecipeIngs(delegator, dispatcher, productId,false);
}


