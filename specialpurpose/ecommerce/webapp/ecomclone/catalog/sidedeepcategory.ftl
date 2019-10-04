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

<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />

<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>
				
<#-- looping macro -->
	<#macro categoryList parentCategory category wrapInBox>
		<#assign categoryName = category.categoryName?if_exists>
	  	<#if catContentWrappers?exists && catContentWrappers[category.productCategoryId]?exists && catContentWrappers[category.productCategoryId].get("DESCRIPTION")?exists>
	    	<#assign categoryDescription = catContentWrappers[category.productCategoryId].get("DESCRIPTION")>
	  	<#else>
	    	<#assign categoryDescription = category.description?if_exists>
	  	</#if>
	  	<#if curCategoryId?exists && curCategoryId == category.productCategoryId>
	    	<#assign browseCategoryButtonClass = "browsecategorybuttondisabled">
	  	<#else>
	    	<#assign browseCategoryButtonClass = "browsecategorybutton">
	  	</#if>
	  	<#if wrapInBox == "Y">
	    	<div  id="sidedeepcategory" class="screenlet">
      			<div class="screenlet-title-bar">
        			<ul>
          				<li class="h3"><#if categoryDescription?has_content>${categoryDescription}<#else>${categoryName?default("")}</#if></li>
        			</ul>
        			<br class="clear"/>
      			</div>
      			<div class="screenlet-body">
        			<div class="browsecategorylist">
  		</#if>
       
			<#if parentCategory?has_content>
	            <#assign parentCategoryId = parentCategory.productCategoryId/>
	         <#else>
	            <#assign parentCategoryId = ""/>
	         </#if>
	         <a href="<@ofbizCatalogUrl currentCategoryId=category.productCategoryId previousCategoryId=parentCategoryId/>" class="${browseCategoryButtonClass}"><#if categoryName?has_content>${categoryName}<#else>${categoryDescription?default("")}</#if></a>
				<#local subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
	    		<#if subCatList?has_content>
	    			<ul class="sub_navigation">
	      				<#list subCatList as subCat> 
	          				<li><@categoryList parentCategory=category category=subCat wrapInBox="N"/></li>
	      				</#list>
	      			</ul>
	    		</#if>
	  	<#if wrapInBox == "Y">
	      			</div>
	    		</div>
	  		</div>
	  	</#if>
	</#macro>
	<#assign catalogCol = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogIdsAvailable(request)?if_exists>
	<#if requestVal.equals("/product") || requestVal.equals("/main") || requestVal.equals("/category")>
		<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists>
		<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
	<#else>
		
	</#if>
	<#if (catalogCol?size > 0)>
		<div class="catalog">
			<div class="sidedeeptitle">Category</div>
			<#assign counter = 1/>
			<#assign x = catalogCol.size()?if_exists />
			<#list catalogCol as catalogId>
				<ul><li>
				<#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
					<a href="<@ofbizUrl>main?CURRENT_CATALOG_ID=${catalogId}</@ofbizUrl>" onclick="document.CATEGORY_${catalogId}${counter}.submit()" > ${thisCatalogName}</a>
				<#if (requestVal.equals("/main")) || (requestVal.equals("/category"))>
				<#if currentCatalogId==catalogId>
					<#if topLevelList?has_content>
    					<ul class="browsecategorylist">
      						<#list topLevelList as category>
        						 <li class="browsecategorytext dropdown" style="position:relative;"><@categoryList parentCategory="" category=category wrapInBox="N"/></li>
      						</#list>
    					</ul>
					</#if>
				</#if>
				</#if>
				</li></ul>
			</#list>
		</div>
	</#if>    
	<script type="text/javascript">
		$('body').ready(function() {
			$('.dropdown').hover(function() {
				$(this).find('.sub_navigation').slideToggle(200); 
			});
		});
	</script>