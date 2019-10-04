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
<#if productCategoryMembers?has_content>
	
	<div class="screenlet" style="margin-bottom:5px">
		<div class="outside-screenlet-header">
			<div class="boxhead">
				Best Deals
			</div>
		</div>
	  	<#-- <ul id="mycarouse4" class="jcarousel-skin-tango" style="height:190px;z-index:0; position:relative"> -->
	  	<div id="bestDealProducts">
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
					<#assign product = delegator.findOne("Product",{"productId":productCategoryMember.productId}, false)?if_exists>
				
					<#assign price = allPriceMap.get(productCategoryMember.productId)?if_exists />
				    <#-- variable setup -->
				    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",productCategoryMember.productId))?if_exists>
				    <#if product?has_content && product.primaryProductCategoryId?has_content>
				    	<#assign productCategoryId = product.primaryProductCategoryId>
				    <#else>
					    <#if productCategory?has_content>
					    	<#assign prodCategory = productCategory.get(0)?if_exists>
			                <#assign productCategoryId = prodCategory.get("productCategoryId")>
			            </#if>
		            </#if>
		            
		            <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,productCategoryMember.productId)/>
				    <#assign productUrl><@ofbizCatalogUrl productId=productCategoryMember.productId currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
				    
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
				    <#if product?exists && (product.isVariant == "Y" || product.isVirtual == "Y") >
				    <#assign productIndexUrl = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getProductIndexUrl(request,productCategoryMember.productId)/>
				    <#else>
				    <#assign productIndexUrl = "">
				    </#if>
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
		<#-- </ul> -->
		</div>
	</div>
	<script type="text/javascript">
		function showBestDealProducts(bestDealIndex){
   			var url="/control/showBestDealProductsAjax?bestDealIndex="+bestDealIndex;
			jQuery.ajax({url: url,
		        data: null,
		        type: 'post',
		        async: true,
		        success: function(data) {
	          		$('#bestDealProducts').html(data);
	  			},
				complete:  function() {
				  	pleaseWait('N');
				},
		        error: function(data) {
		            alert("Error during product filtering");
		        }
    		});   
		}
	
	</script>
</#if>
<div style="clear:both"></div>