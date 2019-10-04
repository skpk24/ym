<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
 <link href="http://netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">  
<link href='http://fonts.googleapis.com/css?family=Roboto+Slab:300' rel='stylesheet' type='text/css'>     
<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />
<#if requestVal.contains("/home") || requestVal.contains("/login") || requestVal.contains("/newcustomer") || requestVal.contains("/createcustomer") || requestVal.contains("/locationsearch") || requestVal.contains("/locationsearchAction") || requestVal.contains("/storeEmaillocation")>
<#else>
</#if>
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>
<#assign catalogCol = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogIdsAvailable(request)?if_exists>
	<#if requestVal.equals("/product") || requestVal.equals("/main") || requestVal.equals("/category")>
	<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists>
	<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
	<#elseif requestVal.equals("/keywordsearch")>
		<#assign currentCatalogId = requestParameters.SEARCH_CATALOG_ID?if_exists>
		<#if currentCatalogId?has_content && !currentCatalogId.equals("")>
			<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
		</#if>
	</#if>
	
	<#--if (catalogCol?size > 0)>
	<div class="catalog">
		<div class="sidedeeptitle">Browse Shop</div>
			<div <#if requestVal.contains("/home") || requestVal.contains("/login") || requestVal.contains("/newcustomer") || requestVal.contains("/createcustomer") || requestVal.contains("/locationsearch") || requestVal.contains("/locationsearchAction") || requestVal.contains("/storeEmaillocation")></#if> >
				<#if currentCatalogId?exists>
				<#assign CategoryList = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, currentCatalogId)?if_exists>
				<#assign CategoryListSub1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(delegator,"topLevelList", CategoryList, false, false, false)?if_exists>
				<#if CategoryListSub1?has_content>
				<#assign count =0>
			    <#list CategoryListSub1 as CategoryListSub3>
			    <#if count = 0>
			    
			    <ul class="vertical-nav"</#if>
			    <#assign pcat = CategoryListSub3.productCategoryId>
			    <#if productCategoryId?has_content && (productCategoryId ==pcat || (parameters.rootCategoryId?has_content && parameters.rootCategoryId==pcat))>
			    <#assign pcatmain = CategoryListSub3.productCategoryId>
			    </#if>
			    <#if count+1 ==CategoryListSub1.size()>
			    ></#if>
			    <#assign count =count+1>
			    </#list>
			    <#list CategoryListSub1 as CategoryListSub2>
			      <li>
			      <a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", CategoryListSub2.productCategoryId, "")}?CURRENT_CATALOG_ID=${currentCatalogId}" class="atul">
			      ${CategoryListSub2.categoryName} <i class="fa fa-angle-right fa-lg"></i></a>
				<#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", CategoryListSub2.productCategoryId, true)>
				<#if subCatList?has_content>
					<div>
					<div class="vertical-nav-column">
					<h3>${CategoryListSub2.categoryName}</h3>
					<#assign opened = true>
					<#assign subCatListCount = 0>
	 					<ul>
						<#list subCatList as subCat>
							<#if opened>
						 		<div class="LastMenuItems_Left" id="aaa">
							</#if>
							<#if subCatListCount == 10>
							 	</div>
							 		<div class="LastMenuItems_Right" id="bbb">
	 						</#if>
							<li <#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)> <#if subCatLists1?has_content> class="hasSubmenu" </#if>>
							<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}"><span class="Category_subName">${subCat.categoryName}</span> <#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)> <#if subCatLists1?has_content> <img src="/images/left_arrow.png" class="Category_subImg"/> </#if><br/></a>
							<#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)>
							<#if subCatLists1?has_content>
							
								<ul>
									<#list subCatLists1 as subCat1>
										<li class="iamLastchild"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "",subCat1.productCategoryId, subCat.productCategoryId,CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}">${subCat1.categoryName} </a>											          							
											<#assign subCatLists2 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists2", subCat1.productCategoryId, true)>
											<#if subCatLists2?has_content>
												<ul>
												   <#list subCatLists2 as subCat2>
												   <li><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat2.productCategoryId, subCat1.productCategoryId,subCat.productCategoryId,CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}">${subCat2.categoryName}</a></li>
												   </#list>
												</ul>
											</#if>
										</li>
									</#list>
								</ul>
							  </#if>
							  </li>
							  <#assign opened = false>
							  <#assign subCatListCount = subCatListCount + 1>
							    <#if subCatListCount == subCatList.size()> 
							    </div>
							    </#if>
						  </#list>
	  				      </ul>
		      	     </div>
		             </div>
			     </#if>
			  </li>
			 </#list>
			</ul>
		</#if>
		</#if>
		</div>
		</div>
		</#if-->
 