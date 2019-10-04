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
<#assign topLevelList = requestAttributes.topLevelList?if_exists>
<#assign curCategoryId = requestAttributes.curCategoryId?if_exists>
<#-- looping macro -->
<#macro categoryList parentCategory category>
  <#if parentCategory.productCategoryId != category.productCategoryId>
    <#local previousCategoryId = parentCategory.productCategoryId />
  </#if>

  <#if (Static["org.ofbiz.product.category.CategoryWorker"].checkTrailItem(request, category.getString("productCategoryId"))) || (curCategoryId?exists && curCategoryId == category.productCategoryId)>
    <li><font color="black"> >> </font>
    <#if catContentWrappers?exists && catContentWrappers[category.productCategoryId]?exists>
      <a href="<@ofbizCatalogUrl currentCategoryId=category.productCategoryId previousCategoryId=previousCategoryId!""/>" class="<#if curCategoryId?exists && curCategoryId == category.productCategoryId>buttontextdisabled<#else>linktext</#if>">
        <#if catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")?exists>
          ${catContentWrappers[category.productCategoryId].get("CATEGORY_NAME")}
        <#elseif catContentWrappers[category.productCategoryId].get("DESCRIPTION")?exists>
          ${catContentWrappers[category.productCategoryId].get("DESCRIPTION")}
        <#else>
          ${category.description?if_exists}
        </#if>
      </a>
    </#if>
    </li>
    <#local subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", category.getString("productCategoryId"), true)>
    <#if subCatList?exists>
      <#list subCatList as subCat>
        <@categoryList parentCategory=category category=subCat/>
      </#list>
    </#if>
  </#if>
</#macro>

<div class="breadcrumbs">
 <#if category?has_content && category.productCategoryId == "GIFT_CARD">
	 <#--div class="inner-content" style="text-align:justify;">
		 <p>Thinking of what to Gift your loved one!! Gift them with an e-voucher.</p> 
		 <p>YouMart has taken an initiative which is a revolutionary way to gift your 
		 dear one with an e-voucher to ease your worry of gifting. It is an easy method 
		 for both the sender & the recipient to participate in this unique process.</p>
		 <p>You can select the amount you wish to gift & go through the payment mode to 
		 pay the relevant amount either through Credit/Debit Card or NetBanking. 
		 Once we receive the payment confirmation we will send you a confirmation email; 
		 and also simultaneously will be intimating the recipient about the gifted e-voucher 
		 with your personalized message through SMS & email. There will a coupon code sent to 
		 the recipient which will be used only once & will expire in 30 days period.</p> 
		 <p>The recipient while shopping with YouMart.in  has to enter the coupon code in 
		 the "Checkout" page to avail the benefit.</p>
		 <p>Kindly choose the e-voucher from the options & proceed for payment. 
		 All the fields are mandatory & needs to be filled for you complete the transaction:</p>
	 </div>
	 <div style="clear:both;"></div-->
<#else>
  <ul>
    <li>
      <a href="<@ofbizUrl>home</@ofbizUrl>" class="linktext">${uiLabelMap.CommonHome}</a>
    </li>
    <#--if currentCatalogId?exists && currentCatalogId?has_content>
    <li><font color="black"> » </font><a href="<@ofbizUrl>main</@ofbizUrl>" class="linktext">${catName.get("catalogName")?if_exists}</a></li>
    </#if-->
    <#-- Show the category branch -->
    <#list topLevelList as category>
      <@categoryList parentCategory=category category=category/>
    </#list>
    <#-- Show the product, if there is one -->
    <#if productContentWrapper?exists>
    <li style="color:#000000 !important;"><font color="black"> >> </font>
    	${productContentWrapper.get("BRAND_NAME")?if_exists}
    	<span id="breadcrumbsVariant_product_name" style="display:inline !important;"></span>
    	<span id="breadcrumbsVariant_product_default">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</span>
    </#if>
    </li>
  </ul>
  </#if>
</div>
<br />