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
<#-- variable setup and worker calls -->
<#if requestAttributes.cur_CategoryId?has_content>
<div id="sidedeepcategory" class="screenlet">
    <div class="screenlet-header">
        <div class="boxhead1">Sub Categories</div>
    </div>
  <div class="screenlet-body">
        <div class="browsecategorylist">
		    <#assign category=requestAttributes.cur_CategoryId?if_exists>
		    <#if category?has_content>
		    <#assign subCatList1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category, true)>
		    <#if subCatList1?exists>
		      <#list subCatList1 as subCat>
		        <div class="browsecategorylist">
					  <#if catContentWrappers?exists && catContentWrappers[subCat.productCategoryId]?exists && catContentWrappers[subCat.productCategoryId].get("CATEGORY_NAME")?exists>
					      <#assign categoryName = catContentWrappers[subCat.productCategoryId].get("CATEGORY_NAME")>
					  <#else>
					      <#assign categoryName = subCat.categoryName?if_exists>
					  </#if>
					  <#if catContentWrappers?exists && catContentWrappers[subCat.productCategoryId]?exists && catContentWrappers[subCat.productCategoryId].get("DESCRIPTION")?exists>
					      <#assign categoryDescription = catContentWrappers[subCat.productCategoryId].get("DESCRIPTION")>
					  <#else>
					      <#assign categoryDescription = subCat.description?if_exists>
					  </#if>
					  <#if curCategoryId?exists && curCategoryId == subCat.productCategoryId>
					      <#assign browseCategoryButtonClass = "browsecategorybuttondisabled">
					  <#else>
					      <#assign browseCategoryButtonClass = "browsecategorybutton">
					  </#if>
					 <div class="browsecategorytext">
					     <a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "",subCat.productCategoryId,category)}" class="${browseCategoryButtonClass}"><#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if></a>
					 </div>
		         </div>
		       </#list>
		     </#if>
         	</#if>
        </div>
    </div>
</div>
</#if>
