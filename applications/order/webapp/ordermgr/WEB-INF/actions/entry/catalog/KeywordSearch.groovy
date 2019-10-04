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
// note: this can be run multiple times in the same request without causing problems, will check to see on its own if it has run again
ProductSearchSession.processSearchParameters(parameters, request);
prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
//result = ProductSearchSession.getProductSearchResult(request, delegator, prodCatalogId);


SEARCH_STRING_BRAND = request.getParameter("SEARCH_STRING_BRAND");


result = org.ofbiz.order.shoppingcart.ShoppingCartEvents.searchProduct(request,response);


//
//if (UtilValidate.isNotEmpty(SEARCH_STRING_BRAND)) {
//	
// 
//result = org.ofbiz.order.shoppingcart.ShoppingCartEvents.searchProductForBrand(request,response);
//}
//else{
//	SEARCH_STRING_BRAND = request.getParameter("SEARCH_STRING");
// 
//
//result = org.ofbiz.order.shoppingcart.ShoppingCartEvents.searchProduct(request,response);
//}




if(request.getParameter("searchKey") == "Category"){
allCatalogIds = CatalogWorker.getAllCatalogIds(request);
context.allCatalogIds = allCatalogIds;}
List brandListDetail = new ArrayList();
productStore = ProductStoreWorker.getProductStore(request);
if(request.getParameter("searchKey") == "Brands"){
	Connection conn=null;
	String query="SELECT DISTINCT brand_name from product where brand_name IS NOT NULL";
	try{
		 conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz"));
		PreparedStatement ps=conn.prepareStatement(query);
		ResultSet rs=ps.executeQuery();
		while (rs.next()) {
			Map brandsMap = new HashMap();
			brandsMap.put("brandName", rs.getString("brand_name"));
			brandListDetail.add(brandsMap);
		  }
	}catch (Exception e) {
		e.printStackTrace();
	}finally{
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	context.brandListDetail = brandListDetail;
}

List<String> SortByName = new ArrayList<String>();
HashMap<String,Double> map = new HashMap<String,Double>();
ValueComparator bvc =  new ValueComparator(map);
TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
List<GenericValue> sortByPrice = new ArrayList<GenericValue>();
List<GenericValue> popularProductList = new ArrayList<GenericValue>();
HashMap<String,Double> map1 = new HashMap<String,Double>();
for(String result1  : result.productIds)
{exprList = [];
	exprList.add(EntityCondition.makeCondition("totalQuantityOrdered", EntityOperator.GREATER_THAN_EQUAL_TO, 5.0));
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, result1));
	  condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
	  productList1 = delegator.findList("ProductCalculatedInfo", condition, null, ["-totalQuantityOrdered"], null, false);
	for(GenericValue popular :productList1)
	{
		String PopularProductId=popular.get("productId");
		popularProductList.addAll(PopularProductId);
		
	}
	
	productLi123=delegator.findList("Product",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,result1),null,null,null,false);
	
	for(GenericValue ids : productLi123)
	 {
		String Produc =ids.get("productId")
		priceContext = [:];
		priceContext.put("product",ids);
		result111 = dispatcher.runSync("calculateProductPrice",priceContext);
		basePrice = result111.basePrice;
		map.put(ids.productId, basePrice);
		
		String name=ids.get("internalName")
		map1.put(ids.productId, name);
	}
	
	
}
sorted_map.putAll(map);
for (Integer key : sorted_map.keySet())
{
	Integer value = sorted_map.get(key);
	sortByPrice.add(key);
}
List<String> lowToHigh= new ArrayList<String>();
List<String> highToLow= new ArrayList<String>();
highToLow.addAll(sortByPrice);
lowToHigh.addAll(sortByPrice);
Collections.reverse(lowToHigh);


Map<String, String> sortName1 = sortByComparator(map1);
Iterator iterator = sortName1.entrySet().iterator();
	while (iterator.hasNext())
	{
		Map.Entry mapEntry = (Map.Entry) iterator.next();
		SortByName.addAll(mapEntry.getKey());
	}
List<String> AtoZ= new ArrayList<String>();
List<String> ZtoA= new ArrayList<String>();
AtoZ.addAll(SortByName);
ZtoA.addAll(SortByName);
Collections.reverse(ZtoA);


if(request.getParameter("filterBy") == null || "".equals(request.getParameter("filterBy").trim())){
	context.productIds = lowToHigh;
}
if("L_TO_H".equals(request.getParameter("filterBy"))){
	context.productIds = lowToHigh;
	}
else if("H_TO_L".equals(request.getParameter("filterBy"))){
	context.productIds = highToLow;
	}
else if("A_TO_Z".equals(request.getParameter("filterBy"))){
	context.productIds = AtoZ;}
else if("Z_TO_A".equals(request.getParameter("filterBy"))){
	context.productIds = ZtoA;}
else if("POPULAR_PRD".equals(request.getParameter("filterBy"))){
	context.productIds = popularProductList;
}

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

context.brandMap = result.get("brandMap");
context.brandList = result.get("brandList");
context.filterByBrand = result.get("filterByBrand");

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




class ValueComparator implements Comparator<String> {

	Map<String, Double> base;
	public ValueComparator(Map<String, Double> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}

private static Map sortByComparator(Map unsortMap) {
	
   List list = new LinkedList(unsortMap.entrySet());

   // sort list based on comparator
   Collections.sort(list, new Comparator() {
	   public int compare(Object o1, Object o2) {
		   return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
	   }
   });

   // put sorted list into map again
		   //LinkedHashMap make sure order in which keys were inserted
   Map sortedMap = new LinkedHashMap();
   for (Iterator it = list.iterator(); it.hasNext();) {
	   Map.Entry entry = (Map.Entry) it.next();
	   sortedMap.put(entry.getKey(), entry.getValue());
   }
   return sortedMap;
}