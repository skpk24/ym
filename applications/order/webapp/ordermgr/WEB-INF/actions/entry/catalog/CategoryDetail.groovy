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
* NOTE: This script is also referenced by the ecommerce's screens and
* should not contain order component's specific code.
*/

import java.util.List;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.service.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import org.ofbiz.product.category.CategoryWorker;
PrintWriter out=response.getWriter();


 productCategoryId = null;
productCategoryId = request.getAttribute("productCategoryId");
if(UtilValidate.isEmpty(productCategoryId))
{
   productCategoryId= request.getParameter("category_id");
}
if(UtilValidate.isEmpty(productCategoryId))
{
   productCategoryId= parameters.category_id;
}
  List productList = new ArrayList();
  categoryList = CategoryWorker.productList( request,  response, productCategoryId , productList);
  context.allProdListOfCateg = categoryList;

priceContext = [:];
List<String> SortByName = new ArrayList<String>();

List<String> NONpopularProductId = new ArrayList<String>();
List<String> popularProductId = new ArrayList<String>();
List<String> bothProductId = new ArrayList<String>();
HashMap<String,Double> map1 = new HashMap<String,Double>();
HashMap<String,Double> map = new HashMap<String,Double>();
List<String> priceLowToHigh = new ArrayList<String>();
List<String> sortName = new ArrayList<String>();
ValueComparator bvc =  new ValueComparator(map);
TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);

ValueComparator bvc1 =  new ValueComparator(map);
TreeMap<String,Double> sorted_map1 = new TreeMap<String,Double>(bvc1);


for(int i=0;i<categoryList.size();i++)
{
   String productId=categoryList.get(i)
   exprList = [];
	exprList.add(EntityCondition.makeCondition("totalQuantityOrdered", EntityOperator.NOT_EQUAL, null));
	exprList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
   condition = EntityCondition.makeCondition(exprList, EntityOperator.AND);
   List orderBy = UtilMisc.toList("-totalQuantityOrdered");
   productList1 = delegator.findList("ProductCalculatedInfo", condition, null, orderBy, null, false);
	if(UtilValidate.isNotEmpty(productList1))
	{
   for(GenericValue Produ : productList1)
   {
		String product = Produ.get("productId")
		popularProductId.addAll(product);
   }
	}
   else
   NONpopularProductId.addAll(productId);
   
   
	
List<GenericValue> productName = delegator.findList("Product",EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId),null,null,null,true);
GenericValue tem=EntityUtil.getFirst(productName);

for(GenericValue ProductNam :productName)
{
   String name=ProductNam.get("internalName")
   
   map1.put(ProductNam.productId, name);
}
priceContext.put("product",tem);
result = dispatcher.runSync("calculateProductPrice", priceContext);
basePrice = result.basePrice;

map.put(tem.productId, basePrice);

}

	




andExprs = FastList.newInstance();


List tempList1 =   delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.IN,popularProductId) ,null, null, null, false);
List tempList2 =   delegator.findList("ProductCategoryMember", EntityCondition.makeCondition("productId", EntityOperator.IN,NONpopularProductId) ,null, null, null, false);
List tempList3=new ArrayList();
tempList3.addAll(tempList1);
tempList3.addAll(tempList2);

//context.popularProductId=tempList;
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
context.AtoZ=AtoZ;
context.ZtoA=ZtoA;



List lowToHigh= new ArrayList();
List highToLow= new ArrayList();
List lowToHighLToH= new ArrayList();
List highToLowHToL= new ArrayList();
List dataAtoZ= new ArrayList();
List dataZtoA= new ArrayList();
sorted_map.putAll(map);
for (Integer key : sorted_map.keySet())
{
   Integer value = sorted_map.get(key);
   priceLowToHigh.add(key);
}


highToLow.addAll(priceLowToHigh);
lowToHigh.addAll(priceLowToHigh);
Collections.reverse(lowToHigh);

Iterator<String> iteratorLToH = lowToHigh.iterator();
while (iteratorLToH.hasNext()) {
   andExprsLToH = FastList.newInstance();
   //andExprsLToH.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
   andExprsLToH.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, iteratorLToH.next()));
   andCondLToH = EntityCondition.makeCondition(andExprsLToH, EntityOperator.AND);
   tempListLToH =   delegator.findList("ProductCategoryMember", andCondLToH ,null, null, null, false);
   for(GenericValue productInfo:tempListLToH){
	   lowToHighLToH.add(productInfo);
   }
}


Iterator<String> iteratorHToL = highToLow.iterator();
while (iteratorHToL.hasNext()) {
andExprsHToL = FastList.newInstance();
//andExprsHToL.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
andExprsHToL.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, iteratorHToL.next()));
andCondHToL = EntityCondition.makeCondition(andExprsHToL, EntityOperator.AND);
tempListHToL =   delegator.findList("ProductCategoryMember", andCondHToL ,null, null, null, false);
for(GenericValue productInfosHToL:tempListHToL){
	   highToLowHToL.add(productInfosHToL);
   }
}

Iterator<String> iteratorAToZ = AtoZ.iterator();
while (iteratorAToZ.hasNext()) {
andExprsAToZ = FastList.newInstance();
//andExprsAToZ.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
andExprsAToZ.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, iteratorAToZ.next()));
andCondAToZ = EntityCondition.makeCondition(andExprsAToZ, EntityOperator.AND);
tempListAToZ =   delegator.findList("ProductCategoryMember", andCondAToZ ,null, null, null, false);
for(GenericValue productInfosAToZ:tempListAToZ){
	   dataAtoZ.add(productInfosAToZ);
   }
}

Iterator<String> iteratorZToA = ZtoA.iterator();
while (iteratorZToA.hasNext()) {
andExprsZToA = FastList.newInstance();
//andExprsZToA.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
andExprsZToA.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, iteratorZToA.next()));
andCondZToA = EntityCondition.makeCondition(andExprsZToA, EntityOperator.AND);
tempListZToA =   delegator.findList("ProductCategoryMember", andCondZToA ,null, null, null, false);
for(GenericValue productInfosZToA:tempListZToA){
	   dataZtoA.add(productInfosZToA);
   }
}

/*context.highToLowHToL=highToLowHToL;
context.lowToHighLToH=lowToHighLToH;
context.popularProductId=tempList;
context.dataZtoA=dataZtoA;
context.dataAtoZ=dataAtoZ;*/


String filterBy = (String) request.getParameter("filterBy");
if("L_TO_H".equals(request.getParameter("filterBy"))){
   productCategoryMembers = lowToHighLToH;
   }
else if("H_TO_L".equals(request.getParameter("filterBy"))){
   productCategoryMembers = highToLowHToL;
   }
else if("A_TO_Z".equals(request.getParameter("filterBy")))
   productCategoryMembers = dataAtoZ;
else if("Z_TO_A".equals(request.getParameter("filterBy")))
   productCategoryMembers = dataZtoA;
else if("POPULAR_PRD".equals(request.getParameter("filterBy"))){
   productCategoryMembers = tempList3;
}




   if(UtilValidate.isEmpty(request.getParameter("filterBy")))
   {
   context.filterBy  = "POPULAR_PRD";
   productCategoryMembers = tempList3;
   }
   else
 
context.filterBy = filterBy;


context.productCategoryId = productCategoryId;

viewSize = parameters.VIEW_SIZE;



viewIndex = parameters.VIEW_INDEX;
currentCatalogId = CatalogWorker.getCurrentCatalogId(request);

// set the default view size
defaultViewSize = request.getAttribute("defaultViewSize") ?: 10;
context.defaultViewSize = defaultViewSize;

// set the limit view
limitView = request.getAttribute("limitView") ?: true;
context.limitView = limitView;
viewSize=String.valueOf(24);
// get the product category & members
andMap = [productCategoryId : productCategoryId,
	   viewIndexString : viewIndex,
	   viewSizeString : viewSize,
	   defaultViewSize : defaultViewSize,
	   limitView : limitView];
andMap.put("prodCatalogId", currentCatalogId);
andMap.put("checkViewAllow", true);

if(categoryList !=null) {
andMap.put("prodCatalogSize", categoryList.size());
}

if (context.orderByFields) {
   andMap.put("orderByFields", context.orderByFields);
} else {
   andMap.put("orderByFields", ["sequenceNum", "productId"]);
}

filterByPrice = request.getParameter("filterByPrice");
filterByBrand = request.getParameter("filterByBrand");
List<String> brandList = new ArrayList<String>();
	if(UtilValidate.isNotEmpty(filterByBrand)){
	filterByBrand = filterByBrand.replaceAll("ajaxReq", "'");
	    		String[] arrays = filterByBrand.split(",");
				for(int i=0;i<arrays.length;i++){
					 String ss = arrays[i].replace("'","");
					 ss = ss.replace("&","");
					brandList.add(ss);
					}
	}
 catResult = dispatcher.runSync("getProductCategoryAndLimitedMembers1", andMap);
productCategory = catResult.productCategory;
//productCategoryMembers = catResult.productCategoryMembers;

// Prevents out of stock product to be displayed on site
productStore = ProductStoreWorker.getProductStore(request);
if(productStore) {
   if("N".equals(productStore.showOutOfStockProducts)) {
	   productsInStock = [];
	   productCategoryMembers.each { productCategoryMember ->
		   productFacility = delegator.findOne("ProductFacility", [productId : productCategoryMember.productId, facilityId : productStore.inventoryFacilityId], true);
		   if(productFacility) {
			   if(productFacility.lastInventoryCount >= 1) {
				   productsInStock.add(productCategoryMember);
			   }
		   }
	   }
	   //context.productCategoryMembers = productsInStock;
   } else {
	   //context.productCategoryMembers = productCategoryMembers;
   }
}

      if(productCategoryMembers != null)
          {
			   
     		   data = org.ofbiz.product.category.CategoryServices.filterCategoryMembers(filterByPrice,filterByBrand,productCategoryMembers, filterBy, dispatcher ,delegator);
//			  data = org.ofbiz.product.category.CategoryServices.filterCategoryMembers(filterByPrice,productCategoryMembers,dispatcher ,delegator);
 			   context.brandName = data.brandName;
    		   context.priceMap1 = data.priceMap;
 			 context.allah =brandList;
    		   productCategoryMembers = data.productCategoryMembers;
    		   context.filter = data.filter;
    		   context.listSize = productCategoryMembers.size();
          }
if(filterByBrand)
{
filterByBrand = filterByBrand.replaceAll("'", "ajaxReq");
context.filterByBrand = filterByBrand;
}
		  
Pagination pg=new Pagination(productCategoryMembers);
List<Object> paging = pg.getPagination(catResult.viewIndex);

//context.paging = paging;
public class Pagination
{
	  public static int index=0;
		int startIndex = 0;
		int lastIndex = 0;
	  
	  static List<Object> MainList=new ArrayList();
	  List<Object> TempList=new ArrayList();
	 
	 Pagination(List<Object> productCategoryMembers)
	  {
	  this.MainList=productCategoryMembers;
	  }

	  List<Object> getPagination(viewIndex)
	  {
		 this.startIndex = 0;
		this.lastIndex = this.startIndex + viewIndex * 24;
		this.startIndex=this.lastIndex-24;
		 
	  if(this.lastIndex > MainList.size() ) {
		this.lastIndex= MainList.size();
	   }
		  for(int i= this.startIndex;i<this.lastIndex;i++)
			 {
					TempList.add(MainList.get(i));
			 }
			 return TempList;
   }
}
context.productCategoryMembers = paging;
context.productCategory = productCategory;
context.viewIndex = catResult.viewIndex;
context.viewSize = catResult.viewSize;
context.lowIndex = catResult.lowIndex;

productIds1 = "";
paging.each { productCategoryMember ->
	productIds1 = productIds1+","+(String)productCategoryMember.productId;
}

context.productIds1 = productIds1;

// categoryMem = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId),null,null,null,true);
 if(!context.listSize)
 	context.listSize = productCategoryMembers.size();
 	
context.highIndex = catResult.highIndex; 
if(context.listSize < context.highIndex) 	
	context.highIndex = context.listSize; 	
	
context.productStore = productStore;
if(UtilValidate.isNotEmpty(productStore)){
session.setAttribute("productStrore",productStore);}
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
if (UtilValidate.isNotEmpty(members)) {
   for (i = 0; i < members.size(); i++) {
	   productCategoryMember = (GenericValue) members.get(i);
	   if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {
		   context.put("hasQuantities", new Boolean(true));
		   break;
	   }
   }
}

CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(productCategory, request);
context.put("categoryContentWrapper", categoryContentWrapper);

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
