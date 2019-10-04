<#-- ${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#mainpromotext")} -->
<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists>
<#assign CategoryListSub1 = Static["org.ofbiz.product.catalog.CatalogWorker"].getBannerCategory(request)?if_exists>
  <div class="homecategory">
 	<#if (CategoryListSub1?size > 0)>
 		<#assign groupKeys = CategoryListSub1.keySet()>
 		 <#assign globalCount = 1>
 		<#list  groupKeys as groupKey>
 		<#assign CategoryListSub2 = CategoryListSub1.get(groupKey)>
		<div class="outside-screenlet-header">
				<div <#if groupKey?has_content>class="boxhead_new"</#if>>
					 <span>${groupKey?if_exists}</span> 
				</div>
		 </div>
		<div class="displaycat">
		<#assign counter =1>
		<#if CategoryListSub2?has_content>
			<#list CategoryListSub2 as CategoryListSub3>
 				<#if CategoryListSub3?exists>
	 				<div class="
	 				<#if CategoryListSub3_has_next>
		 				<#if globalCount gt 1>
		 				sub_displaycat1
		 				<#else>
		 				sub_displaycat
		 				</#if>
	 				<#else>
		 				<#if globalCount gt 1>
			 				sub_displaycatlast1
			 				<#else>
			 				sub_displaycatlast
			 			</#if>
	 				</#if>">
						<div style="text-align:center; padding:10px 0 0 0">${CategoryListSub3.categoryName?if_exists}</div>
						<div style="text-align:center; margin:0 auto; ">
						<#if CategoryListSub3.bannerLinkUrl?has_content>
						<a href="${CategoryListSub3.bannerLinkUrl?default('#')}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}">
						<img src="<#if CategoryListSub3.bannerImageUrl?has_content>${CategoryListSub3.bannerImageUrl?default('/images/defaultImage.jpg')}</#if>" width="<#if counter==2>266px<#else>266px</#if>" height="195px"/></a>
						<#else>
						<img src="<#if CategoryListSub3.bannerImageUrl?has_content>${CategoryListSub3.bannerImageUrl?default('/images/defaultImage.jpg')}</#if>" width="266px" height="195px"/>
						</#if>
						</div>
					</div>
				</#if>	
				<#assign counter =counter+1>
			</#list>
				
		</#if>
		</div>
		 <#assign globalCount =globalCount +  1>
		</#list>
	</#if>
	${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#bestDealProducts")}
	 ${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#bestSellingCategory")}
	
</div>
<div style="float:right;width:180px;margin-right:-8px;margin-bottom:15px;">
${screens.render("component://ecommerce/widget/ecomclone/CommonScreens.xml#rightbar")}
</div>
