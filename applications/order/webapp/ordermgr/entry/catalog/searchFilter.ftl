<div style="position:relative;">
	<div class="sidedeeptitle">Refine By</div>
	<div style="border:1px solid #CDCEAF;  background:#FFFFFF; width:178px; -moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;">
		<div style="font-size:11px; margin:8px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(../erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Category<b></div>
		<#if categoryMap?has_content>
			<#assign keys = categoryMap.keySet()/>
				<form name="filterCategoryBy">
				<div style="height:150px; width:178px; overflow-y:auto; margin-bottom:10px;">
					<#list keys as key>
						<#assign categoryName = Static["org.ofbiz.product.category.CategoryWorker"].getCategoryName(delegator, key, true)/>
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;"><input type="checkbox" id="${key}" name="categoryCheckBox" onclick="filter()"
									<#if filterCategory?has_content && filterCategory.contains('${key}')>
									checked = "true"</#if>value="${key}"></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">${categoryName}(${categoryMap.get('${key}')})</div>
								</div>
								<br />
					</#list>
				</div>
				</form>
		</#if>
		<#if brandList?has_content>
		<div style="font-size:11px; margin:30px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(/erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Brand Name<b></div>
				<#assign count = 0>
						<form name="filterBy">
						<div style="height:140px; width:178px; overflow-y:auto; margin-bottom:10px;">
							<#list brandList as brand>
								<div style="float:left; width:160px;">
									<div style="float:left; width:20px; height:20px;"><input type="checkbox" id="${brand}" name="brandBox" onclick="filter()" 
									 <#if filterByBrand?has_content && filterByBrand.contains(brand)>checked = "true"</#if>value="${brand}"></div>
									<div style=" font-size:11px !important; font-weight:normal !important;">${brand}(${brandMap.get("${count}")?if_exists})</div>
								</div>	
								<br />
								<#assign count = count+1>
							</#list>
						</div>
						</form>
				</#if>
	    <div style="font-size:11px; margin:8px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(../erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Price<b></div>
		<#if priceMap1?has_content && filterKeys?has_content>
			<#assign keys = priceMap1.keySet()/>
				<form name="filterBy">
				<div style="height:150px; width:178px; overflow-y:auto;">
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
	<div style="font-size:11px; margin:8px 0 5px 0; padding:8px 5px 8px 5px; background-image:url(../erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;"><b>Product Availability<b></div>
				<form name="excludeOutOfStock">
					<div style="height:35px; width:178px; overflow-y:auto;">
							<div style="float:left; width:160px;">
								<div style="float:left; width:20px; height:20px;">
									<input type="checkbox" id="excludeOutOfStockCheckbox" name="excludeOutOfStock" onclick="filter()" 
											<#if excludeOutOfStock?has_content && excludeOutOfStock == "Y">checked = "true"</#if>
											value="Y">
								</div>
								<div style=" font-size:11px !important; font-weight:normal !important;">Exclude out of stock</div>
							</div>	
					</div>
				</form>
</div>
</div>

<script>
	function filterCategoryWise(){
		var filterByCategory = "";
		var checkboxes = document.getElementsByName('categoryCheckBox');
		flag = true;
		for (var i=0; i< checkboxes.length; i++) {
		     if (checkboxes[i].checked) {
		     	flag = false;
		        var value = checkboxes[i].value;
				if(filterByCategory == "")
					filterByCategory = value;
				else
					filterByCategory = filterByCategory+","+value;
				document.sortBy.filterByCategory.value = filterByCategory;
		     }
		  }
		if(flag)
			document.sortBy.filterByCategory.value = "";
			
			
		if(document.getElementById("excludeOutOfStockCheckbox").checked  == 1)
			document.sortBy.excludeOutOfStock.value = "Y";
		else
			document.sortBy.excludeOutOfStock.value = "N";
			
		document.sortBy.submit();
	}
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
				document.sortBy.filterBy.value = filterBy;
		     }
		  }
		 
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
				document.sortBy.filterByBrand.value = bradnArray;
		 
		if(flag)
			document.sortBy.filterBy.value = "";
			
		filterCategoryWise();
	}
	function unCheckFilter(value){
		document.getElementById(value).checked = false;
		filter();
	}
	
	function clearAll(){
		document.sortBy.filterBy.value = "";
		document.sortBy.filterByCategory.value = "";
		document.sortBy.excludeOutOfStock.value = "N";
		document.sortBy.filterByBrand.value = "";
		document.sortBy.submit();
	}
</script>