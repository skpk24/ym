<div class="homeleft">
<#if preAvailBrand>
	<a href="javascript:showProductsFromBrand('${productsFromBrandPrevIndex?if_exists?default(-1)}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
<#else>
				  			<img src="/multiflex/left_arrow.png" alt=""/>
</#if>
</div>
<div class="slideproduct">
	<#assign count1=1/>
  	<#list brandList as mv1>
	  	<div class="shadow" style="height:275px; width:200px !important; float:left; margin:6px; background-color:#fff;   padding:5px 10px 5px 10px; border:1px solid #cccccc;  background-image: url(/erptheme1/backgroundproduct.jpg); background-repeat:repeat-x; background-position:left;">
		  	 ${setRequestAttribute("optProductId2", mv1.productId?if_exists)}
	         <div style="border:none !important;">   ${screens.render(impulsiveproductsummaryScreen1)}</div>
	         <#assign count1 = count1 + 1 />
		 </div>
  	 </#list>
  	 </div>
		<div class="homeleft">
		<#if nextAvailBrand>
			<a href="javascript:showProductsFromBrand('${productsFromBrandNextIndex?if_exists?default(1)}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
		<#else>
									<img src="/multiflex/right_arrow.png" alt=""/>
		</#if>
		</div>

<div style="clear:both;"> </div> 