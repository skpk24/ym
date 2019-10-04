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

<#if !sessionAttributes.userLogin?exists>
  <div class='label'> ${uiLabelMap.ProductGeneralMessage}.</div>
</#if>
<br />
<script>
	function Cata(){
		$('#cateImg').show();
		$('#mainCat').show();
		$('#catImg').hide();
		$('#mainCate').hide();
		$('#prodImg').show();
		$('#mainprod').hide();
	}
	function Cate(){
		$('#mainCate').show();
		$('#cateImg').hide();
		$('#catImg').show();
		$('#mainCat').hide();
		$('#prodImg').show();
		$('#mainprod').hide();
	}
	function Product(){
		$('#mainCate').hide();
		$('#cateImg').show();
		$('#catImg').show();
		$('#mainCat').hide();
		$('#prodImg').hide();
		$('#mainprod').show();
	}
</script>
<#if security.hasEntityPermission("CATALOG", "_VIEW", session)>
	<div class="maincat">
  		<ul id="catImg">
  			<li><img src="/bizznesstime/images/catalog.jpg" onclick="javascript:Cata()"/></li>
  			<li><h4>Catalog</h4></li>
		</ul>
		<ul id="mainCat" style="display:none;">
			<li class="label">${uiLabelMap.ProductEditCatalogWithCatalogId}:</li>
			<li>
				<form method="post" action="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" style="margin: 0;" name="EditProdCatalogForm">
			    	<input type="text" size="20" maxlength="20" name="prodCatalogId" value=""/>
			    	<input type="submit" value=" ${uiLabelMap.ProductEditCatalog}" class="smallSubmit"/>
			  	</form>
			</li>
		   	<li style="text-align:left;"><a href="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewCatalog}</a></li>
		</ul>
		<ul id="cateImg">
			  <li><img src="/bizznesstime/images/category.jpg" onclick="javascript:Cate()"/></li>
			  <li><h4>Category<h4></li>
		</ul>
		<ul id="mainCate" style="display:none;">
			<li class="label">${uiLabelMap.ProductEditCategoryWithCategoryId}:</li>
		  	<li>
			  	<form method="post" action="<@ofbizUrl>EditCategory</@ofbizUrl>" style="margin: 0;" name="EditCategoryForm">
			    	<@htmlTemplate.lookupField name="productCategoryId" id="productCategoryId" formName="EditCategoryForm" fieldFormName="LookupProductCategory"/>
			    	<input type="submit" value="${uiLabelMap.ProductEditCategory}" class="smallSubmit"/>
			  	</form>
			</li>
		  	<li style="text-align:left;"><a href="<@ofbizUrl>EditCategory</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewCategory}</a></li>
		</ul>
		<ul id="prodImg">
	 		<li><img src="/bizznesstime/images/product.jpg" onclick="javascript:Product()"/></li>
			<li><h4>Product</h4></li>
		</ul>
		<ul id="mainprod" style="display:none;">
			<li class="label">${uiLabelMap.ProductEditProductWithProductId}:</li>
		  	<li>
			  	<form method="post" action="<@ofbizUrl>EditProduct</@ofbizUrl>" style="margin: 0;" name="EditProductForm">
			    	<@htmlTemplate.lookupField name="productId" id="productId" formName="EditProductForm" fieldFormName="LookupProduct"/>
			    	<input type="submit" value=" ${uiLabelMap.ProductEditProduct}" class="smallSubmit"/>
			  	</form>
			</li>
		  	<li style="text-align:left;"><a href="<@ofbizUrl>EditProduct</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductCreateNewProduct}</a></li>
		  	<li style="text-align:left;"><a href="<@ofbizUrl>CreateVirtualWithVariantsForm</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductQuickCreateVirtualFromVariants}</a></li>
		</ul>
  	</div>
  <#--<div class="label">${uiLabelMap.ProductEditCatalogWithCatalogId}:</div>-->
  
<#--
  
  <div class="label">${uiLabelMap.ProductFindProductWithIdValue}:</div>
  <form method="post" action="<@ofbizUrl>FindProductById</@ofbizUrl>" style="margin: 0;">
    <input type="text" size="20" maxlength="20" name="idValue" value=""/>
    <input type="submit" value=" ${uiLabelMap.ProductFindProduct}" class="smallSubmit"/>
  </form>
  <br />
  <div><a href="<@ofbizUrl>UpdateAllKeywords</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductAutoCreateKeywordsForAllProducts}</a></div>
  <div><a href="<@ofbizUrl>FastLoadCache</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductFastLoadCatalogIntoCache}</a></div>
  <br />
  -->
</#if>
