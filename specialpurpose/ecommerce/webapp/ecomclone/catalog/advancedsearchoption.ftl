<script type="text/javascript">
	function getCategoryProduct()
 		{
   var selectedBoxValue=document.advancedsearchform.SEARCH_CATEGORY_ID.value;
    var  param = 'categoryId=' + selectedBoxValue;
                      jQuery.ajax({url: "/control/getProductList",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#productselect').html(data);
         }
         
    });
   
}
</script>

<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />

<div class="">
     		<form name="advancedsearchform" method="post" action="<@ofbizUrl>keywordsearch</@ofbizUrl>">
     			<input type="hidden" name="VIEW_SIZE" value="10"/>
  				<input type="hidden" name="PAGING" value="Y"/>
  				<ul>
  					<li>
		  				<select  name="SEARCH_CATEGORY_ID" onchange="getCategoryProduct()" id="selectOpt">
		  					<option value="">Select Categories</option>
		  					<#assign catalogCol = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogIdsAvailable(request)?if_exists>
							<#if requestVal.equals("/product") || requestVal.equals("/main") || requestVal.equals("/category")>
								<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists>
								<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
							<#else>
							</#if>
							<#-- Only show if there is more than 1 (one) catalog, no sense selecting when there is only one option... -->
							<#if (catalogCol?size > 0)>
								<#assign counter = 1/>
								<#assign x = catalogCol.size()?if_exists />
								<#list catalogCol as catalogId>
									<#if catalogId?exists>
							     		<#assign CategoryList = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, catalogId)?if_exists>
							   			<#assign CategoryListSub1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(delegator,"topLevelList", CategoryList, false, false, false)?if_exists>
							        	<#if CategoryListSub1?has_content>
							        		<#list CategoryListSub1 as CategoryListSub2>
							        			<option value="${CategoryListSub2.productCategoryId}">${CategoryListSub2.categoryName}</option>
							        		</#list>
							        	</#if>
							       	</#if>
								</#list>
							</#if>
						</select>
					</li>
	     			<li><div id='productselect'><input type="text" name="SEARCH_STRING" id="SEARCH_STRING" class="main-search" onblur="if (this.value == '') { this.value = 'Search for Product'; }" onfocus="if (this.value == 'Search for Product') {this.value = ''; }" size="40"  value="${requestParameters.SEARCH_STRING?if_exists}"/></div></li>
	     			<li><input type="image" src="/erptheme1/search_submit.jpg"/></li>
     			</ul>
     		</form>
     	</div>
     	
     	
     	
  