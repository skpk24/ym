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
<#--assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />
				<#if requestVal.equals("/contactus") || requestVal.equals("/AnonContactus") || requestVal.equals("/aboutus") || requestVal.equals("/joinUs") || requestVal.equals("/siteMap") || requestVal.equals("/policies") || requestVal.equals("/securitypolicy") || requestVal.equals("/mediaCenter") || requestVal.equals("/termofuse") || requestVal.equals("/faqs") || requestVal.equals("/updateFavouriteList") || requestVal.equals("/editFavouritesList") || requestVal.equals("/category") || requestVal.equals("/recipeDetail") >
					<script>
							$(window).scroll(function(){
								var browserName=navigator.appName;
								if  ($(window).scrollTop() >= "150" ){
									if (browserName=="Microsoft Internet Explorer") {
										document.getElementById('compareproductScrollcart').style.position=='absolute';
										
										var kapil = $(window).scrollTop()-200;
										document.getElementById('compareproductScrollcart').style.top= kapil+'px' ;
										document.getElementById('compareproductScrollcart').style.background= '#f4f2f2';
										}
									else{
					                  	document.getElementById('compareproductScrollcart').style.position='fixed';
										document.getElementById('compareproductScrollcart').style.top='55px';
					               }
					               }
					            else{
					               document.getElementById('compareproductScrollcart').style.position='relative';
					               document.getElementById('compareproductScrollcart').style.top= '' ;
					               document.getElementById('compareproductScrollcart').style.background= '';
					               }
					            
					       });
						</script>
			   <#elseif requestVal.equals("/keywordsearch")>
			            <#script>
							    $(window).scroll(function () {
							       var position = $("#fixed").offset();
							        $("#fixed").html(position.top);
							    });
							
						</script>
						<#else>
						<script>
							$(window).scroll(function(){
								var browserName=navigator.appName;
								if  ($(window).scrollTop() >= "390" ){
									if (browserName=="Microsoft Internet Explorer") {
										document.getElementById('compareproductScrollcart').style.position=='absolute';
										
										var kapil = $(window).scrollTop()-200;
										document.getElementById('compareproductScrollcart').style.top= kapil+'px' ;
										document.getElementById('compareproductScrollcart').style.background= '#f4f2f2';
										}
									else{
					                  	document.getElementById('compareproductScrollcart').style.position='fixed';
										document.getElementById('compareproductScrollcart').style.top='55px';
					               }
					               }
					            else{
					               document.getElementById('compareproductScrollcart').style.position='relative';
					               document.getElementById('compareproductScrollcart').style.top= '' ;
					               document.getElementById('compareproductScrollcart').style.background= '';
					               }
					            
					       });
						</script>
						
		     </#if-->
 <#assign ajayTest = "TEST">
 <#if ajayTest == "TEST1">
<script type="text/javascript">
window.onload = backupdatecart;
function backupdatecart()
{
    jQuery.ajax({url: '/control/minicart',
	         data: null,
	         type: 'post',
	         async: false,
	         success: function(data) {
					$('#minicart').html(data); 
					var miniquantity = document.getElementById('miniquantityA').value;
           			document.getElementById('microCartQuantity').innerHTML=miniquantity;
           			document.getElementById('sideCatQuantity').innerHTML=miniquantity; 
           			document.getElementById('expandSideCatQuantity').innerHTML=miniquantity; 
	      	 		var abcvalue = document.getElementById('abcxyz').innerHTML;
           			if (abcvalue ==""){
           	           abcvalue = "0.00";
           			}   
	        		document.getElementById('microCartTotal').innerHTML =abcvalue; 
	       
	         },
        error: function(data) {
        }
    	});
    	<#assign req = request.getRequestURI()>
		<#if req?has_content && req.contains("/control/AnonContactus")>
    		DrawCaptcha();
		</#if>
}


function deleteItem(boolvalue, cartindex,size) {
 var name="";
if(parseInt(size)==1){

 name= "/control/emptycart1";

}else{
name="/control/modifiedcart";
}
var  param = 'removeSelected=' + boolvalue + 
                      '&selectedItem=' + cartindex;
                      jQuery.ajax({url: name,
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#minicart').html(data); 
          var minitotal = document.getElementById('abcxyz').innerHTML;
        
          var miniquantity = document.getElementById('miniquantityA').value;
        
           $('#microCartTotal').text(minitotal);
           document.getElementById('microCartQuantity').innerHTML=miniquantity;
           //document.getElementById('checkoutdis').style.display="block";
          document.getElementById('sideCatQuantity').innerHTML=miniquantity; 
           document.getElementById('expandSideCatQuantity').innerHTML=miniquantity; 
        
         },
        error: function(data) {
        }
    	});
    }
 </script>   
 </#if>
    <#assign shoppingCartSize_Test = 0>
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>


<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>

<#assign isGiftCard = false>
<#if (shoppingCartSize > 0)> 
	<#list shoppingCart.items() as cartLine>
	<#if cartLine.getProductId().startsWith("GIFTCARD")>
		<#assign isGiftCard = true>
	</#if>
	</#list>
</#if>



<a href="<@ofbizUrl>showcart</@ofbizUrl>">
<strong id="microCartQuantity">${shoppingCartSize?default(0)}</strong> Item ,<span class="WebRupee">&#8377;</span> <span id="microCartTotal">${shoppingCart.getDisplayGrandTotal()?default(0)}</span>
</a>



<#if (shoppingCartSize_Test > 100)>
			<#-->div <#if requestVal.equals("/keywordsearch")> style="position:absolute !important; top:406px;"<#else> id="compareproductScrollcart" style="z-index:90;"</#if>-->
			<div id="minicart" class="catalog" style="background:#ffffff !important;">
			
				<#if (shoppingCartSize > 0)><#-->div class="sidedeeptitle">${uiLabelMap.OrderCartSummary}</div--><#else><div style="background:none;"></div></#if>
			    <div <#if (shoppingCartSize > 0)><#-->style="border:1px solid #8da519; background:#FFFFFF; width:178px; overflow:hidden; -moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;"--><#else></#if>>
			          <#if (shoppingCartSize > 0)>
			          <#if hidetoplinks?default("N") != "Y">
			            <ul>
			              <li><a href="<@ofbizUrl>showcart</@ofbizUrl>" class="button">${uiLabelMap.OrderViewCart}</a></li>
			              <li><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckout}</a></li>
			              <li><a href="<@ofbizUrl>quickcheckout</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckoutQuick}</a></li>
			              <li><a href="<@ofbizUrl>onePageCheckout</@ofbizUrl>" class="button">${uiLabelMap.EcommerceOnePageCheckout}</a></li>
			              <li><a href="<@ofbizUrl>googleCheckout</@ofbizUrl>" class="button">${uiLabelMap.EcommerceCartToGoogleCheckout}</a></li>
			            </ul>
			          </#if>
					<div style="float:left; width:38px;">
						<img src="/erptheme1/cartslide/cart_button_close.png" alt="bulk order" usemap="#Map" />
			    	</div>
				    <div style="float:left; border:3px solid #7a7946; background:#ffffff; min-height:200px;">		     
			          <table cellspacing="0" cellpadding="0" class="cart-summary-info">
			            <thead>
			              <tr>
			                <th style="width:15px;">${uiLabelMap.OrderQty}</th>
			                <th style="width:158px;">Product</th>
			                <th style="width:60px;">Price</th>
			                <th style="width:28px;"></th>
			               <#-- <th style="background:#FFFFFF; width:50px;">${uiLabelMap.CommonSubtotal}</th> 
			                <th style="background:#FFFFFF; width:50px;"></th>-->
			              </tr>
			            </thead>
			            
			            <tbody>
			            	<tr>
			            		<td colspan="4">
			            		<div style="height:145px; overflow-y:scroll;min-height:145px;">
			            		  <table <#if shoppingCart.items().size() gt 3> style="display:block; margin:0px !important; padding:2px; height:145px;"</#if>  width="100%" style="margin:0px !important;height:145px; ">
						            <input type="hidden" id="miniquantityA" value="${shoppingCart.getTotalQuantity()}"/>
						            <#assign count = 1>
						            <#list shoppingCart.items() as cartLine>
						              <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine) />
						              <tr <#if count%2 == 0>id="evensummary"<#else>id="oddsummary"</#if>>
						                <td style="font-size:10px !important; width:24px;"><div>${cartLine.getQuantity()?string.number}</div> </td>
						                <td style="font-size:10px !important; width:158px;">
						                  <span  style="padding-right:10px;">
						                  
						                  <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",parentProductId))?if_exists>
										    <#if productCategory?has_content>
										    	<#assign prodCategory = productCategory.get(0)?if_exists>
								                <#assign productCategoryId = prodCategory.get("productCategoryId")>
								            </#if>
						                    
						                    <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,cartLine.getProductId())/>
										    <#assign productUrl><@ofbizCatalogUrl productId=cartLine.getProductId() currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
						                    <#assign productIndexUrl = cartLine.getProductUrl()?if_exists>
						                  <#if cartLine.getProductId()?exists>
						                      <#if cartLine.getParentProductId()?exists>
						                       <#assign brandName = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", cartLine.getProductId()))>
						                          <a href="${productUrl?if_exists}${productIndexUrl?if_exists}" style="font-size:10px;" class="linktext">${brandName.brandName?if_exists} ${cartLine.getName()}</a>
						                      <#else>
						                       <#assign brandName = delegator.findByPrimaryKeyCache("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", cartLine.getProductId()))>
						                          <a href="${productUrl?if_exists}${productIndexUrl?if_exists}" style="font-size:10px;" class="linktext">${brandName.brandName?if_exists} ${cartLine.getName()}</a>
						                      </#if>
						                  <#else>
						                    <strong>${cartLine.getItemTypeDescription()?if_exists}</strong>
						                  </#if>
						                  </span>
						                </td>
						                <#-->td style="text-align:left; width:60px;">${cartLine.getDisplayPrice()?if_exists}</td-->
						                <td style="text-align:left; width:60px;"><#--<@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/>--> <span class="WebRupee">&#8377;</span>&nbsp;${cartLine.getDisplayItemSubTotal()}</td>
						                <td style="width:28px;">  <div><a href="javascript:deleteItem(true,${cartLineIndex},${shoppingCartSize})"><img src="/erptheme1/cross.png" alt=""/></a></div> </td>
						              </tr>
						              <#if cartLine.getReservStart()?exists>
						                <tr><td>&nbsp;</td><td colspan="4">(${cartLine.getReservStart()?string("yyyy-MM-dd")}, ${cartLine.getReservLength()} <#if cartLine.getReservLength() == 1>${uiLabelMap.CommonDay}<#else>${uiLabelMap.CommonDays}</#if>)</td></tr>
						              </#if>
						              
						              <#if shoppingCart.items().size() != 1 && shoppingCart.items().size() != count>
						              
						              </#if>
						              <#assign count = count + 1>
						            </#list>
						          </table>
						         </div>
						       </td>
						    </tr>
			            </tbody>
			          </table>
			          <#if !isGiftCard>
			          <table style="margin:0px !important;">
			          <tr>
			                <td>
			                   <#if hidebottomlinks?default("N") != "Y">
					              <ul style="overflow:hidden; padding:0px;">
						              <li style="text-align:right; overflow:hidden; border-bottom:none !important;"><a href="<@ofbizUrl>showcart</@ofbizUrl>" ><img src="/erptheme1/cart_left.jpg"/></a></li>
						              <#--li><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckout}</a></li>
						              <li><a href="<@ofbizUrl>quickcheckout</@ofbizUrl>" class="button">${uiLabelMap.OrderCheckoutQuick}</a></li>
						              <li><a href="<@ofbizUrl>onePageCheckout</@ofbizUrl>" class="button">${uiLabelMap.EcommerceOnePageCheckout}</a></li>
						              <li><a href="<@ofbizUrl>googleCheckout</@ofbizUrl>" class="button">${uiLabelMap.EcommerceCartToGoogleCheckout}</a></li-->
					             </ul>
					            </#if>
						        <#else>
						          <input type="hidden" id="miniquantityA" value=""/>
						       </#if>
					        </td>
			                <td colspan="3">
			                <div style="text-align:center; font-weight:bold; font-size:11px;">
			                <input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
			                  ${uiLabelMap.CommonSubtotal} : <#--<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;<span id ="abcxyz">${shoppingCart.getDisplayGrandTotal()?if_exists}</span>
			               </div>
			                </td>
			              </tr>
			          </table>
			    
			          </#if>
			       </div>
			    </div>
			</div>
			<#-->/div-->

</#if> <#if ajayTest = "TEST1">
	<div id="minicart" class="catalog" style="background:#ffffff !important;">
			
				
			    <div>
			          
			          <input type="hidden" id="miniquantityA" value="0"/>
			         
					<div style="float:left; width:38px;">
						<img src="/erptheme1/cartslide/cart_button_close.png" alt="bulk order" usemap="#Map" />
			    	</div>
				    <div style="float:left; border:3px solid #7a7946; background:#ffffff; min-height:174px;">		     
			          <table cellspacing="0" cellpadding="0" class="cart-summary-info">
			            <thead>
			              <tr>
			                <th style="width:15px;">${uiLabelMap.OrderQty}</th>
			                <th style="width:158px;">Product</th>
			                <th style="width:60px;">Price</th>
			                <th style="width:28px;"></th>
			               <#-- <th style="background:#FFFFFF; width:50px;">${uiLabelMap.CommonSubtotal}</th> 
			                <th style="background:#FFFFFF; width:50px;"></th>-->
			              </tr>
			            </thead>
			            
			            <tbody>
			            	<tr>
			            		<td colspan="4">
			            			<div style="padding:20px">Your Cart is empty </div>
						       </td>
						    </tr>
			            </tbody>
			          </table>
			          <table style="margin:0px !important;">
			          <tr>
			                <td>
			                   
					        </td>
			                <td colspan="3">
			                </td>
			              </tr>
			          </table>
			    
			          <span id ="abcxyz"></span>
			       </div>
			    </div>
			</div>
</#if>

  








