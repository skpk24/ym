<div class="homeleft">
<#if preAvail>
	<a href="javascript:showProductsFromCategory('${productsFromCategoryPrevIndex?if_exists?default(-1)}','${categoryId?if_exists}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
<#else>
				  			<img src="/multiflex/left_arrow.png" alt=""/>
</#if>
</div>
<div class="slideproduct">
<#assign count=1/>
<#if categoryList?exists>
  	<#list categoryList as mv>
	  	<div class="shadow" style="height:275px; width:200px !important; float:left; margin:6px; background-color:#fff;   padding:5px 10px 5px 10px; border:1px solid #cccccc;  background-image: url(/erptheme1/backgroundproduct.jpg); background-repeat:repeat-x; background-position:left; position:relative">
		  	 ${setRequestAttribute("optProductId1", mv.productId?if_exists)}
	         <div style="border:none !important;">   ${screens.render(impulsiveproductsummaryScreen)}</div>
	         <#assign count = count + 1 />
		</div>
	</#list>
</#if>	
</div>
	<div class="homeleft">
	<#if nextAvail>
		<a href="javascript:showProductsFromCategory('${productsFromCategoryNextIndex?if_exists?default(1)}','${categoryId?if_exists}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
	<#else>
							<img src="/multiflex/right_arrow.png" alt=""/>
	</#if>
	</div>

	 <div style="clear:both;"> </div> 
	 
