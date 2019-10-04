<div class="homeleft">
	  		<#if preAvail>
	  			<a href="javascript:showBestDealProducts('${bestDealPrevIndex?if_exists?default(-1)}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
	  		<#else>
	  			<img src="/multiflex/left_arrow.png" alt=""/>
	  		</#if>
	  		</div>
			<#assign count=1/>
			<div class="homedeal">
			<ul>
			 <#list productCategoryMembers as productCategoryMember>
			 	<li style="height:180px;width:193px; position:relative">
				<#if productCategoryMember?exists>
					<#assign price = allPriceMap.get(productCategoryMember.productId)?if_exists />
				    <#-- variable setup -->
				    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",productCategoryMember.productId))?if_exists>
				    
				    <#if productCategory?has_content>
				    	<#assign prodCategory = productCategory.get(0)?if_exists>
		                <#assign productCategoryId = prodCategory.get("productCategoryId")>
		            </#if>
		            
		            <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,productCategoryMember.productId)/>
				    <#assign productUrl><@ofbizCatalogUrl productId=productCategoryMember.productId currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
				    
				    <#assign product = delegator.findOne("Product",{"productId":productCategoryMember.productId}, false)?if_exists>
				    
				     <#assign smallImageUrl = ""/>
				     <#if product?has_content>
				    	<#assign smallImageUrl = product.get("smallImageUrl")?if_exists>
				    </#if>	
				   
				    <#if ((price.listPrice?exists && price.price?exists) && (price.price?double < price.listPrice?double))>
	               
	                  		<#assign priceSaved = price.listPrice?double - price.price?double>
	                  		<#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
	                    	<#--${uiLabelMap.OrderSave}: <span class="basePrice"><span class="WebRupee">&#8377;</span>&nbsp;${priceSaved} (${percentSaved?int}%)</span>-->
	                	
	                	<div class="discount-${percentSaved?int?if_exists} discountmain" style="left:135px;top:0; position:absolute"></div>
	                	
	                	
                  	</#if>
				    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
				    <#assign productIndexUrl = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getProductIndexUrl(request,productCategoryMember.productId)/>
				     <a href="${productUrl?if_exists}${productIndexUrl?if_exists}"><img src="<@ofbizContentUrl>${smallImageUrl}</@ofbizContentUrl>" alt="${product.productName?if_exists}" height="140px" width="176px"/></a><br/>
				     <a href="${productUrl?if_exists}${productIndexUrl?if_exists}">${product.productName?if_exists}</a><br/>
				<#else>
					&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br/>
				</#if>
				
				<#assign count = count + 1 />
				</li>
			</#list>
			</ul>
			</div>
			<div class="homeleft">
			<#if nextAvail>
				<a href="javascript:showBestDealProducts('${bestDealNextIndex?if_exists?default(1)}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
			<#else>
	  			<img src="/multiflex/right_arrow.png" alt=""/>
			</#if>
			</div>