		<div class="previous_button_showcart">
  		<#if preAvail>
  			<a href="javascript:showBestDealProducts('${bestDealPrevIndex?if_exists?default(-1)}','${ajaxCategoryId?if_exists}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
  		<#else>
  			<img src="/multiflex/left_arrow.png" alt=""/>
  		</#if>
  		</div>
		<#assign count=1/>
		<div>
		<ul style="margin-left: -20px;margin-right: 15px;">
			<#list assocProducts as mv>
		  	<li style="height: 275px; width: 200px ! important; float: left; margin: 6px; background-color: rgb(255, 255, 255); padding: 5px 10px; border: 1px solid rgb(204, 204, 204); background-image: url('/erptheme1/backgroundproduct.jpg'); background-repeat: repeat-x; background-position: left center; list-style: none outside none;" jcarouselindex="1">
			  	 ${setRequestAttribute("optProductId1", mv.productId?if_exists)}
		         <span style="border:none !important;">${screens.render(impulsiveproductsummaryScreen)}</span>
		         <#assign count = count + 1 />
			 </li>
	  	 </#list>
		</ul>
		</div>
		<div class="next_button_showcart">
		<#if nextAvail>
			<a href="javascript:showBestDealProducts('${bestDealNextIndex?if_exists?default(-1)}','${ajaxCategoryId?if_exists}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
		<#else>
  			<img src="/multiflex/right_arrow.png" alt=""/>
		</#if>
		</div>
 