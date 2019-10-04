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
<#if product?exists>
<div id="recipeIngProd${product.productId?if_exists}">
</#if>
<#if product.isVirtual =="Y">


</#if>
<script type="text/javascript">
//<![CDATA[
   
   
function additemSubmit(){
        <#if product.productTypeId?if_exists == "ASSET_USAGE">
        newdatevalue = validate(document.addform.reservStart.value);
        if (newdatevalue == false) {
            document.addform.reservStart.focus();
        } else {
            document.addform.reservStart.value = newdatevalue;
            document.addform.submit();
        }
        <#else>
        document.addform.submit();
        </#if>
    }

    function addShoplistSubmit(){
    
    
        <#if product.productTypeId?if_exists == "ASSET_USAGE">
        if (document.addToShoppingList.reservStartStr.value == "") {
            document.addToShoppingList.submit();
        } else {
            newdatevalue = validate(document.addToShoppingList.reservStartStr.value);
            if (newdatevalue == false) {
                document.addToShoppingList.reservStartStr.focus();
            } else {
                document.addToShoppingList.reservStartStr.value = newdatevalue;
                // document.addToShoppingList.reservStart.value = ;
                document.addToShoppingList.reservStartStr.value.slice(0,9)+" 00:00:00.000000000";
                document.addToShoppingList.submit();
            }
        }
        <#else>
        document.addToShoppingList.submit();
        </#if>
    }

    <#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
        function checkRadioButton() {
            var block1 = document.getElementById("addCart1");
            var block2 = document.getElementById("addCart2");
            <#list featureLists as featureList>
                <#list featureList as feature>
                    <#if feature_index == 0>
                        var myList = document.getElementById("FT${feature.productFeatureTypeId}");
                         if (myList.options[0].selected == true){
                             block1.style.display = "none";
                             block2.style.display = "block";
                             return;
                         }
                        <#break>
                    </#if>
                </#list>
            </#list>
            block1.style.display = "block";
            block2.style.display = "none";
        }
    </#if>
    
   
//]]>
 </script>

<script type="text/javascript">

$(window).scroll(function(){
			var browserName=navigator.appName;
			if  ($(window).scrollTop() >= "170" ){
				if (browserName=="Microsoft Internet Explorer") {
					document.getElementById('compareproductScroll').style.position=='absolute';
					
					var Anil = $(window).scrollTop()-155;
					document.getElementById('compareproductScroll').style.top= Anil+'px' ;
					document.getElementById('compareproductScroll').style.background= '#f4f2f2';
					}
				else{
                  	document.getElementById('compareproductScroll').style.position='fixed';
					document.getElementById('compareproductScroll').style.top='0px';
               }
               }
            else{
               document.getElementById('compareproductScroll').style.position='relative';
               document.getElementById('compareproductScroll').style.top= '' ;
               document.getElementById('compareproductScroll').style.background= '';
               }
            
       });
<!--
    function displayProductVirtualId(variantId, virtualProductId, pForm) {
        if(variantId){
            pForm.product_id.value = variantId;
        }else{
            pForm.product_id.value = '';
            variantId = '';
        }
        var elem = document.getElementById('product_id_display');
        var txt = document.createTextNode(variantId);
        if(elem.hasChildNodes()) {
            elem.replaceChild(txt, elem.firstChild);
        } else {
            elem.appendChild(txt);
        }
        
        var priceElem = document.getElementById('variant_price_display');
        var price = getVariantPrice(variantId);
        var priceTxt = null;
        if(price){
            priceTxt = document.createTextNode(price);
        }else{
            priceTxt = document.createTextNode('');
        }
        
        if(priceElem.hasChildNodes()) {
            priceElem.replaceChild(priceTxt, priceElem.firstChild);
        } else {
            priceElem.appendChild(priceTxt);
        }
    }
    function showcom(pId){
		document.getElementById(pId).style.display="block";
	}
	function hidecom(pId){
		document.getElementById(pId).style.display="none";
	}
//-->
</script>
<style>
:focus {
    outline: none;
}
img{outline:none; border:none;}
</style>


<#if product?exists>  
    <#-- variable setup -->
    <#--assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,product.productId)/>
    <#assign productUrl><@ofbizCatalogUrl productId=product.productId currentCategoryId=categoryId  previousCategoryId=crumbs/></#assign-->
    
    
		    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",product.productId))?if_exists>
						    
								    <#if productCategory?has_content>
								    	<#assign prodCategory = productCategory.get(0)?if_exists>
						                <#assign productCategoryId = prodCategory.get("productCategoryId")>
						            </#if>
				            
				            <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,product.productId)/>
						    <#assign productUrl><@ofbizCatalogUrl productId=product.productId currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
						    <#assign productIndexUrl = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getProductIndexUrl(request,product.productId)/>
						     
		    
		    <#assign productUrl = productUrl+productIndexUrl/>
    
    <#--assign productUrl><@ofbizCatalogUrl productId=product.productId currentCategoryId=categoryId/></#assign-->
	    <#if requestAttributes.productCategoryMember?exists>
	        <#assign prodCatMem = requestAttributes.productCategoryMember>
	    </#if>
    <#-- end variable setup -->
    <#assign productInfoLinkId = "productInfoLink">
    <#assign productInfoLinkId = productInfoLinkId + product.productId/>
    <#assign productDetailId = "productDetailId"/>
    <#assign productDetailId = productDetailId + product.productId/>
    
    <#assign addToCartPrdId = product.productId />
    <div class="productsummary prod-bdr" style="min-height:32px !important; padding-top:1px !important;">
        <div class="productinfo">
        	<div class="listview" style="width:600px !important;">
	          	<div style="height:30px;" >  
					            	<a href="${productUrl}" class="linktext"  id="desclink${product.productId}" title="${productContentWrapper.get("BRAND_NAME")?if_exists} ${br?if_exists} ">
					            	<div style="color:#ed670e; text-align:left; width:130px !important; float:left;"> </div>
					            	<div class="descrip" style=" text-align:left;  width:335px !important;float:left">${requestAttributes.ingredientName?if_exists} - <#--${br1?if_exists}-->
							            	<span style=" overflow:visible; text-align:left; text-overflow:normal; white-space: normal !important; font-size:11px; word-wrap:break-word !important; text-align:left !important; color: #595D0B !important;" id="variant_product_name${product.productId}"> </span>
							            	${Originalquantity?if_exists}
							            	<div style="font-size:11px; display:none;" id="product_id_display${product.productId}" style="display:block">${br1?if_exists}</div>
					            	</div>
					            	<div id="product_id_display1${product.productId}" style="display:none; float:left">${br1?if_exists}</div>
					            	</a>
					            	<strong ><span style=" float:left" id="product_id_displays${product.productId}"> </span></strong>
				   	            	<#if assocProducts?has_content> 
				            		<select style="width:235px;margin-top:-9px;float:left;" name="ingProductId" id="assocProd${product.productId}_${requestAttributes.seqNo}"  onchange="javascript:setPrice(this.id,'${product.productId}','${requestAttributes.seqNo}',the${product.productId}form);">  
				            			<#if product.isVirtual?has_content && product.isVirtual != "Y"><option value="${product.productId?if_exists}" selected>${product.brandName?if_exists} - ${product.internalName?if_exists}</option></#if>
				            			<!--option value="">--</option-->
				            			<#assign cnt = 1/>
				            			<#list assocProducts as assocProduct>
					            			<#if cnt == 1 && product.isVirtual?has_content && product.isVirtual == "Y">
					            				<#assign addToCartPrdId = assocProduct.productId />
					            			</#if>
				            					<option value="${assocProduct.productId?if_exists}" <#if cnt == 1 && product.isVirtual?has_content && product.isVirtual == "Y">selected</#if>>${assocProduct.brandName?if_exists} - ${assocProduct.internalName?if_exists}</option>
				            				<#assign cnt = cnt + 1/> 
				            			</#list>
				            			<!--option value=""></option-->
				            		</select>
				            		 <div id="priceDiv${product.productId?if_exists}" style="display:none;">
				            		 <#if product.isVirtual?has_content && product.isVirtual != "Y">
				            		 <input type="text" name="price" id="unitPrice_${product.productId}_${requestAttributes.seqNo}" value="${price.basePrice}">
				            		 </#if>
				            			<#list assocProducts as assocProduct>
				            				<#assign map = assocProduct.searchName>
				            				<input type="text" name="price" id="unitPrice_${assocProduct.productId}_${requestAttributes.seqNo}" value="${map.basePrice}">
				            			</#list>
				            		</div>
				            		
					            	</#if>
					          	</div>
	          	<script>
	          	function setPrice(dropDownDiv,productId,seqNo,formId){
	  	          	var variantProductId = document.getElementById(dropDownDiv).value;
	 	          	formId.add_product_id.value = variantProductId ;
	 	          	var variantPrice = document.getElementById("unitPrice_"+variantProductId+"_"+seqNo).value;
		          	document.getElementById("variant_price_display"+productId).innerHTML= variantPrice
	  	       		//checkInventory(variantProductId,productId);
 	          	}
	          	
 	          	function setAddToCart(variantProductId){
					var mydiv = document.getElementById("aa");  
				 	var temp1 = productId;
				 	temp = "javascript:addItemToCart("+temp1+")"
				    mydiv.setAttribute("href", temp);
				}
	          	</script>
	          	
	          	<div style="color:#ff0000 !important; text-align:left; width:100px; top:-30px; left:830px;"  class="priceview">
	              	<#--<b>${product.productId?if_exists}</b><br/>-->
	                <#if totalPrice?exists>  
	                  	<div style=" float:left" >${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
	                <#else> 
	                	<#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
	                  		${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice'><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
	                	</#if>
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  		<span class="basePrice" style="margin-right:10px; color:#474646 !important;""><span class="WebRupee">&#8377;</span>&nbsp;<del>${price.listPrice?if_exists}</del> </span>
	                	</#if>
	                	<b>
	                		
		                  	<#if price.isSale?exists && price.isSale>
		                  <#--  <span class="salePrice">${uiLabelMap.OrderOnSale}!</span>-->
		                    	<#assign priceStyle = "salePrice">
		                  	<#else>
		                    	<#assign priceStyle = "regularPrice">
		                  	</#if>
	                  		<#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
	                    		<#if "Y" = product.isVirtual?if_exists> </#if><span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_price_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/-->${price.price?if_exists}</span><br/>
	                  		</#if>
	                	</b>
	                        <#if promoCodeAmount?has_content>
            		<#if promoCodeAmount.amount?has_content>
	            		<#assign disc = (promoCodeAmount.amount?number/price.price?number)*100>
	                	<a href="${productUrl}"><div class="discount-${disc} discountmain"></div></a>
                	<#else>
                		<a href="${productUrl}"><div class="discount-120 discountmain"></div></a>
                	</#if>
                <#else>  
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  		<#assign priceSaved = price.listPrice?double - price.price?double>
	                  		<#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
	                    	<span style="font-size:10px !important;">${uiLabelMap.OrderSave}: <span class="basePrice"><span class="WebRupee">&#8377;</span>&nbsp;${priceSaved} (${percentSaved?int}%)</span></span>
	                	<div class="discount-${percentSaved?int?if_exists} discountmain"></div>
                  	</#if>
                  
              			 </#if>
	                	</#if>
	                
	                <#if (showPriceDetails?exists && showPriceDetails?default("N") == "Y")>
	                    <#if price.orderItemPriceInfos?exists>
	                        <#list price.orderItemPriceInfos as orderItemPriceInfo>
	                            <div>${orderItemPriceInfo.description?if_exists}</div>
	                        </#list>
	                    </#if>
	               	</#if>
	          	</div>
	          	<#if averageRating?exists && (averageRating?double > 0) && numRatings?exists && (numRatings?long > 2)>
	              	<div>${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})</div>
	          	</#if>
          	</div>
          	<div class="productbuy" style=" margin-top:-6px !important;">
          		<#-- check to see if introductionDate hasn't passed yet -->
          		<#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
            		<div style="color: red;">${uiLabelMap.ProductNotYetAvailable}</div>
          		<#-- check to see if salesDiscontinuationDate has passed -->
          		<#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
            		<div style="color: red;">${uiLabelMap.ProductNoLongerAvailable}</div>
          			<#-- check to see if it is a rental item; will enter parameters on the detail screen-->
          		<#elseif product.productTypeId?if_exists == "ASSET_USAGE">
            		<a href="${productUrl}" class="buttontext">${uiLabelMap.OrderMakeBooking}...</a>
	          		<#-- check to see if it is an aggregated or configurable product; will enter parameters on the detail screen-->
	          	<#elseif product.productTypeId?if_exists == "AGGREGATED">
	            	<a href="${productUrl}" class="buttontext">${uiLabelMap.OrderConfigure}...</a>
	          		<#-- check to see if the product is a virtual product -->
	          	<#elseif product.isVirtual?exists && product.isVirtual == "Y">
	           <form method="post" action="<@ofbizUrl>additem1</@ofbizUrl>" name="the${product.productId}form" style="margin: 0;">
              <input type="hidden" name="add_product_id" value="${addToCartPrdId}"/>
              <input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
                 							
              <div class="listviewtab">
	             <div style="float:left; margin-bottom:10px; position:absolute; top:-5px; right:250px !important;" class="quantitylisttab" >
	             	<table cellpadding="0" cellspacing="0" border="0">
					 <tr>
						<td style="width:30px;">Qty:</td>
						<td>
 						    <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
 						</td>
	                    <td>
	                    	<input type="text" id="qty${product.productId?if_exists}" onblur="productDetail('${product.productId?if_exists}','')" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" class="inputBox" style="width:20px;" name="quantity" value="1"/>
	                    </td>
	                    <td>
 						    <a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
						</td>
					 </tr>
				  </table>
	                    <input type="hidden" name="clearSearch" value="N"/>
              	 </div>
              	 <div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important;">
				<#if isStoreInventoryAvailable>
             	<a href="javascript:addItem(the${product.productId}form)" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
             	<#else> 
             		<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
             	</#if>
             	
              	 </div>
              </div>
            </form>
	          	
	          <#else>  
         	 <#if context.get("productStore")?has_content> 
          	 <#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
          	<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
          	 <form method="post" action="<@ofbizUrl>additem1</@ofbizUrl>" name="the${product.productId}form" style="margin: 0;">
              <input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
              <input type="hidden" name="add_product_id" value="${addToCartPrdId}"/>
              <input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
                 							
              <div class="listviewtab">
	             <div style="float:left; margin-bottom:10px; position:absolute; top:-5px; right:250px !important;" class="quantitylisttab" >
	             	<table cellpadding="0" cellspacing="0" border="0">
					 <tr>
						<td style="width:30px;">Qty:</td>
						<td>
 						    <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
 						</td>
	                    <td>
	                    	<input type="text" id="qty${product.productId?if_exists}" onblur="productDetail('${product.productId?if_exists}','')" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" class="inputBox" style="width:20px;" name="quantity" value="1"/>
	                    </td>
	                    <td>
 						    <a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
						</td>
					 </tr>
				  </table>
	                    <input type="hidden" name="clearSearch" value="N"/>
              	 </div>
              	 <div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important;">
				<#if isStoreInventoryAvailable>
             	<a href="javascript:addItem(the${product.productId}form)" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
             	<#else> 
             		<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
             	</#if>
             	
              	 </div>
              </div>
            </form>
           
            
          
            </#if>  
          </#if>
          </#if>
          
          
        </div>
        </div>
        
    </div>
    <div class="clear"></div>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>
<#if variantTree?exists && 0 &lt; variantTree.size()>

     <script type="text/javascript">eval("list" + "${featureOrderFirst}${product.productId}" + "()");</script>
</#if>
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
<#if product.isVirtual =="Y">
<script>
window.onload = function() {
	 setPriceChanges();
	 
}
 
 		window.onload=getList('FTNET_WEIGHT', '0', '0','${product.productId}' );
    </script>
</#if>
<#if product?exists>
</div>
</#if>