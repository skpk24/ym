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

import java.lang.*;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;

currentCatalogId = CatalogWorker.getCurrentCatalogId(request);
categoryId = parameters.category_id ?: CatalogWorker.getCatalogQuickaddCategoryPrimary(request);

if(UtilValidate.isNotEmpty(categoryId)){
	
String [] parts = categoryId.split(",");
String p="";
List<String> p1= new ArrayList<String>();
List<String> productIDs = new ArrayList<String>();
context.noOfCategory =  parts.size();
for(int i =0 ;i< parts.size() ;i++)
{
	
	 p=parts[i];
	// p1 = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, p),null,null,null,true);
	 p1  = delegator.findByAnd("ProductCategoryMember", ["productCategoryId" : p]);
	 for(GenericValue productfea : p1)
	 {
		 String PFeatureId= productfea.get("productId");
		 productIDs.add(PFeatureId);
	 }
	 
	 
}
List<String> categoryIdList= new ArrayList<String>();
categoryIdList.add(categoryId);
context.categoryIdList = categoryIdList;

context.categoryId = categoryId;
context.productIDsp = productIDs;

context.allProdListOfCateg = productIDs;	


}


/*quickAddCategories = CatalogWorker.getCatalogQuickaddCategories(request);
context.quickAddCats = quickAddCategories;
context.categoryId = categoryId;

if (categoryId) {
   fields = [productCategoryId : categoryId, defaultViewSize : 10,
		   limitView : false, prodCatalogId : currentCatalogId, checkViewAllow : true];
   result = dispatcher.runSync("getProductCategoryAndLimitedMembers", fields);
   if (result) {
	   result.each { key, value ->
		   context[key] = value;
	   }
   }
   productCategory = delegator.findByPrimaryKey("ProductCategory", ["productCategoryId" : categoryId]);
   context.productCategory = productCategory;
}
}*/