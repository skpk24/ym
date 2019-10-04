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

<script>
function removeFromCompare(form){
	     	 var url1="/control/removeFromCompareAjax?productId="+form.productId.value;
	         jQuery.ajax({url: url1,
              data: null,
              type: 'post',
              async: false,
              success: function(data) {
              $('#compareproductAjax').html(data);   
         	},
            error: function(data) {
            alert("Error during product compare");
          }
     });     
	}
function clearFromCompare(){
	 var url2="/control/clearCompareListAjax";
	     jQuery.ajax({url: url2,
              data: null,
              type: 'post',
              async: false,
              success: function(data) {
              $('#compareproductAjax').html(data);   
         	},
            error: function(data) {
            alert("Error during product compare");
          }
     });               
	 }
</script>
<#assign productCompareList = Static["org.ofbiz.product.product.ProductEvents"].getProductCompareList(request)?if_exists/>
<#if productCompareList?has_content>
	<div class="screenlet" style="position:relative;">
		<div class="screenlet-header">
	    	<div class="boxhead1">${uiLabelMap.ProductCompareProducts}</div>
					<#if request.getAttribute("message")?has_content> 
					<span style="color:red">
						${request.getAttribute("message")?if_exists}
					</span>
					</#if>
		</div>
		<div class="compareProduct">
			<#list productCompareList as product>
 				<ul>
  					<li>
  		 				<#assign smallImageUrl = ""/>
	     				<#if product?has_content>
	    					<#assign smallImageUrl = product.get("smallImageUrl")?if_exists>
	    				</#if>	
	    				<#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
      					<img src="<@ofbizContentUrl>${smallImageUrl}</@ofbizContentUrl>" width="80px" height="75px" />
    				</li>
    				<li class="compName">
      					<div>${Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(product, "PRODUCT_NAME", request)}</div>
    				</li>
    				<li>
      					<form method="post" action="<@ofbizUrl>removeFromCompare</@ofbizUrl>" name="removeFromCompare${product_index}form">
        					<input type="hidden" name="productId" value="${product.productId}"/>
      					</form>
    					<a href="javascript:removeFromCompare(document.removeFromCompare${product_index}form);"><img src="/images/close.png"/></a>
    				</li>
  				</ul>
			</#list>
			<#if productCompareList?has_content && !(productCompareList?size>=3)>
				<#assign listsize=(3-(productCompareList?size))/>
 				<#list 1..listsize as i>
     				<ul>
			       		<li>
			            	<img src="<@ofbizContentUrl>/images/ofbizOld.ico</@ofbizContentUrl>" width="80px" height="75px"/>
			          	</li>
			          	<li class="compName" style="color:#cbcbcb">
      					Add Product
    				</li>
    				<li>
    				<#if product_index?has_content>
      					<form method="post" action="<@ofbizUrl>removeFromCompare</@ofbizUrl>" name="removeFromCompare${product_index}form">
      						<#if product?has_content>
        						<input type="hidden" name="productId" value="${product.productId?if_exists}"/>
        					</#if>
      					</form>
      				</#if>
    					<a href="#"><img src="/images/close.png" style="opacity:0.4;filter:alpha(opacity=40);"/></a>
    				</li>
     				</ul>
				</#list>  
			</#if>
			<ul class="compLast">
				<li>
					
					<a href="javascript:clearFromCompare();" class="comparetext" title="Remove All">Clear List</a>
					
				</li>
			</ul>
			
		</div>
		<div class="comclear">
		<#if (productCompareList?size >= 2)> 
			<a  href="<@ofbizUrl>compareProducts</@ofbizUrl>" class="comparetext" target="_blank">Compare</a>
		</#if></div>			
					
					
	</div>
</#if>