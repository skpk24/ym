<#if catList?has_content>
<div class="sidedeeptitle">Refine By</div> 
<div style="border:1px solid #CDCEAF; background:#FFFFFF; width:178px; overflow:hidden; -moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;">
<div style="font-size:11px; margin:8px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Category</b></div>
	<#assign cat1 = "">
	<#assign cat2 = "">
	<#assign cat3 = "">
	<#assign cat4 = "">
	<#assign count = 0>
	<#list catList as cat>
		<#if count == 0>
			<div class="refinesubcat"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId, "")}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
			<#assign cat1 = cat.productCategoryId>
		<#elseif count == 1>
			<div class="refinesubcat1" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
			<#assign cat1 = cat.productCategoryId>
		<#elseif count == 2>
			<div class="refinesubcat2" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId,cat2, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
			<#assign cat2 = cat.productCategoryId>
		<#elseif count == 3>
			<div class="refinesubcat3"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId,cat3,cat2, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
			<#assign cat3 = cat.productCategoryId>
		</#if>
		<#assign count = count+1>
	</#list>
	<#if childCatList?has_content>
	<#list childCatList as cat >
		<#if count == 0>
			<div class="refinesubcat"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId, "")}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 1>
			<div class="refinesubcat1" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 2>
			<div class="refinesubcat2" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId,cat2, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 3>
			<div class="refinesubcat3"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", cat.productCategoryId,cat3,cat2, cat1)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${cat.categoryName}</a></div>
		</#if>
	</#list>
	</#if>
	<#--list catList as cat>
		<#if count == 0>
			<div class="refinesubcat"><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
			<#assign cat1 = cat.productCategoryId>
		<#elseif count == 1>
			<div class="refinesubcat1" ><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
			<#assign cat1 = cat.productCategoryId>
		<#elseif count == 2>
			<div class="refinesubcat2" ><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
			<#assign cat2 = cat.productCategoryId>
		<#elseif count == 3>
			<div class="refinesubcat3"><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
			<#assign cat3 = cat.productCategoryId>
		</#if>
		<#assign count = count+1>
	</#list>
	<#if childCatList?has_content>
	<#list childCatList as cat >
		<#if count == 0>
			<div class="refinesubcat"><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 1>
			<div class="refinesubcat1" ><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 2>
			<div class="refinesubcat2" ><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
		<#elseif count == 3>
			<div class="refinesubcat3"><a href="javascript:callDocumentByPaginate('${cat.productCategoryId?if_exists}~${viewSize}~${viewIndex?int}')" class="atul">${cat.categoryName}</a></div>
		</#if>
	</#list>
	</#if-->
	<#-- refined By Brand Name    -->
	<div style="font-size:11px; margin:30px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Brand Name</b></div>
				<#if brandList1?has_content>
				<#assign count = 0>
						<form name="filterBy">
						<div style="height:140px; width:178px; overflow-y:auto; margin-bottom:10px;">
							<#list brandList1 as brand>
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;"><input type="checkbox" id="${brand}" name="brandBox" onclick="filter()" 
									 <#if refineByBrandList?has_content && refineByBrandList.contains(brand)>checked = "true"</#if>value="${brand}"></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">${brand}(${brandMap.get("${count}")?if_exists})</div>
								</div>	
								<br />
								<#assign count = count+1>
							</#list>
						</div>
						</form>
				</#if>
	<div style="font-size:11px; margin:0px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Price</b></div>
				
				<#if priceMap1?has_content && filterKeys?has_content>
					<#assign keys = priceMap1.keySet()/>
						<form name="filterBy">
						<div style="height:135px; width:178px; overflow-y:auto;">
							<#list filterKeys as key>
								<#if priceMap1.get('${key}')?has_content>
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;"><input type="checkbox" id="${key}" name="checkBox" onclick="filter()" 
									<#if refineByPriceList?has_content && refineByPriceList.contains('${key}')>checked = "true"</#if>value="${key}"></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">${key}(${priceMap1.get('${key}')?if_exists})</div>
								</div>	
								<br />
								</#if>
							</#list>
						</div>
						</form>
				</#if>
	
	
	
	
	<div style="font-size:11px; margin:0px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Product Availability</b></div>
						<div style="height:35px; width:178px; overflow-y:auto;">
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;">
										<input type="checkbox" id="excludeOutOfStockCheckbox" name="checkBox" onclick="filter()" 
										<#if excludeOutOfStock?has_content && excludeOutOfStock == "Y">checked = "true"</#if>
										value=""></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">Exclude out of stock</div>
								</div>	
								<br />
						</div>
	</div>
</div>
			<form name="filterByPriceForm" action="<@ofbizUrl>category</@ofbizUrl>">
				<input type="hidden" name="category_id" value="${productCategoryId?if_exists}"/>
				<input type="hidden" name="refineByPrice" value="" id="filterByPrice"/>
				<input type="hidden" name="refineByBrand" value="" id="filterByBrand"/>
			</form>
		
		
		  
		
		<script>
			var clicked = false;
			function filter(){
				var filterBy = "";
				var checkboxes = document.getElementsByName('checkBox');
				var brandBox = document.getElementsByName('brandBox');
				var bradnArray = "";
				for (var i=0; i< brandBox.length; i++) {
				   if (brandBox[i].checked) {
				     var v = brandBox[i].value;
				     if(bradnArray == "")
							bradnArray = v;
						else
							bradnArray = bradnArray+","+v;
							
					}
				}
				document.filterByPriceForm.filterByBrand.value = bradnArray;	
 				flag = true;
				for (var i=0; i< checkboxes.length; i++) {
				     if (checkboxes[i].checked) {
				     	flag = false;
				     
				        var value = checkboxes[i].value;
				        	 
						if(filterBy == "")
							filterBy = value;
						else
							filterBy = filterBy+","+value;
				     }
				  }
				  document.filterByPriceForm.filterByPrice.value = filterBy;
				if(flag)
					document.filterByPriceForm.filterByPrice.value = "";
				
				if(document.filterByPriceForm.filterByPrice.value != "" || document.filterByPriceForm.filterByBrand.value != "")
					clicked = true;
					
				if(document.getElementById("excludeOutOfStockCheckbox").checked  == 1)
					clicked = true;
					
					callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int}');
				//document.filterByPriceForm.submit();
			}
			
			function checkPriceFilter(){
				var filterBy = "";
				var checkboxes = document.getElementsByName('checkBox');
 				flag = true;
				for (var i=0; i< checkboxes.length; i++) {
				     if (checkboxes[i].checked) {
				     	flag = false;
				     
				        var value = checkboxes[i].value;
				        	 
						if(filterBy == "")
							filterBy = value;
						else
							filterBy = filterBy+","+value;
				     }
				  }
				  document.filterByPriceForm.filterByPrice.value = filterBy;
				if(flag)
					document.filterByPriceForm.filterByPrice.value = "";
			}
			function checkBrandFilter(){
				var brandBox = document.getElementsByName('brandBox');
				var bradnArray = "";
				for (var i=0; i< brandBox.length; i++) {
				   if (brandBox[i].checked) {
				     var v = brandBox[i].value;
				     if(bradnArray == "")
							bradnArray = v;
						else
							bradnArray = bradnArray+","+v;
					}
				}
				document.filterByPriceForm.filterByBrand.value = bradnArray;	
			}
			
			function unCheckFilter(value){
				document.getElementById(value).checked = false;
				filter();
			}
			
		<#--	function clearAll(){
				document.sortBy.filterBy.value = "";
				document.sortBy.filterByCategory.value = "";
				document.sortBy.submit();
			}
			-->
		</script>
</div>
<#else>

<#if productCategory?has_content>
<div class="sidedeeptitle">Refine By</div> 
 <div style="border:1px solid #8da519; background:#FFFFFF; width:178px; overflow:hidden; -moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;">
		<#assign productCategoryId = productCategory.productCategoryId>
		<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)>
		<#if productCategoryId?has_content>
			<#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", productCategoryId, true)>
		    <#if subCatList?exists>
		    <div style="font-size:11px; margin-bottom:5px; padding-left:5px;"><b>Category</b></div>
		    <div class="refinesubcat"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", productCategory.productCategoryId, "")}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${productCategory.categoryName}</a></div>
		    
		      <#list subCatList as subCat>
					<div class="refinesubcat1" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${subCat.categoryName}</a></div>
					<#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)>
						<#if subCatLists1?has_content>
							<#list subCatLists1 as subCat1>
								<div class="refinesubcat2" ><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat1.productCategoryId,subCat.productCategoryId, productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${subCat1.categoryName}</a></div>
									<#assign subCatLists2 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists2", subCat1.productCategoryId, true)>
									<#if subCatLists2?has_content>
			      							<#list subCatLists2 as subCat2>
			      								<div class="refinesubcat3"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat2.productCategoryId,subCat1.productCategoryId,subCat.productCategoryId, productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId?if_exists}&filterByPrice=${parameters.filterByPrice?if_exists}" class="atul">${subCat2.categoryName}</a></div>
			          						</#list>
									</#if>
							</#list>
						</#if>
			  </#list>
		    </#if>
		</#if>
		<#--div style="font-size:11px; margin:0px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Price<b></div>
				<#if priceMap1?has_content && filterKeys?has_content>
					<#assign keys = priceMap1.keySet()/>
						<form name="filterBy">
						<div style="height:135px; width:178px; overflow-y:auto;">
						${filterKeys}
							<#list filterKeys as key>
								<#if keys.contains('${key}')>
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;"><input type="checkbox" id="${key}" name="checkBox" onclick="filter()" 
									<#if filter?has_content && filter.contains('${key}')>checked = "true"</#if>value="${key}"></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">${key}(${priceMap1.get('${key}')})</div>
								</div>	
								<br />
								</#if>
							</#list>
						</div>
						</form>
				</#if>
			</div-->
		
		<script>
			function filter(){
				var filterBy = "";
				var checkboxes = document.getElementsByName('checkBox');
				flag = true;
				for (var i=0; i< checkboxes.length; i++) {
				     if (checkboxes[i].checked) {
				     	flag = false;
				        var value = checkboxes[i].value;
						if(filterBy == "")
							filterBy = value;
						else
							filterBy = filterBy+","+value;
						document.filterByPriceForm.filterByPrice.value = filterBy;
				     }
				  }
				if(flag)
					document.filterByPriceForm.filterByPrice.value = "";
					
				document.filterByPriceForm.submit();
			}
		</script>
</div>
</#if>
</#if>