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

<#if products?has_content>
	<script>
		function mycarousel_initCallback(carousel)
		{
		    // Disable autoscrolling if the user clicks the prev or next button.
		    carousel.buttonNext.bind('click', function() {
		        carousel.startAuto(0);
		    });
		
		    carousel.buttonPrev.bind('click', function() {
		        carousel.startAuto(0);
		    });
		
		    // Pause autoscrolling if the user moves with the cursor over the clip.
		    carousel.clip.hover(function() {
		        carousel.stopAuto();
		    }, function() {
		        carousel.startAuto();
		    });
		};
	</script>
	<script type="text/javascript">
		jQuery(document).ready(function() {
		    jQuery('#mycarouse2').jcarousel({
		    	wrap: 'last',scroll:5,
        		initCallback: mycarousel_initCallback
			});
			
		});
	</script>
	<#assign count = 0 />
	<#--<h1>New Arrivals</h1>-->
	<div class="screenlet" style="margin-bottom:5px;">
		<div class="outside-screenlet-header">
			<div class="boxhead">
				${uiLabelMap.NewArrivals}
			</div>
		</div>
	
	  <ul id="mycarouse2" class="jcarousel-skin-tango" style="height:190px;">
	<#list products as pop>
		<li style="height:180px;">
		<#if pop.productId?exists>
		    <#-- variable setup -->
		    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",pop.productId))?if_exists>
		    
		    <#if productCategory?has_content>
		    	<#assign prodCategory = productCategory.get(0)?if_exists>
                <#assign productCategoryId = prodCategory.get("productCategoryId")>
            </#if>
		    <#assign productUrl = Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, pop.productId, productCategoryId, "")/>
		    
		    <#assign product = delegator.findOne("Product",{"productId":pop.productId}, false)?if_exists>
		    
		     <#assign smallImageUrl = ""/>
		     <#if product?has_content>
		    	<#assign smallImageUrl = product.get("smallImageUrl")?if_exists>
		    </#if>	
		    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
		    <#-- end variable setup -->
		      <a href="<@ofbizUrl>product?product_id=${product.productId?if_exists}</@ofbizUrl>"><img src="<@ofbizContentUrl>${smallImageUrl}</@ofbizContentUrl>" alt="${product.productName?if_exists}" height="140px" width="176px"/></a><br/>
		      <a href="<@ofbizUrl>product?product_id=${product.productId?if_exists}</@ofbizUrl>">${product.productName?if_exists}</a><br/>
		<#else>
			&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br/>
		</#if>
		<#assign count = count + 1 />
		</li>
	</#list>
	</ul>
	</div>
</#if>


