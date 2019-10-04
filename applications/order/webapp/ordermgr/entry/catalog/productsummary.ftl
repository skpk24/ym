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

<#if product.isVirtual =="Y">
</#if>
 
<#if product?exists>
 <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,product.productId)/>
    <#-- variable setup -->
    <#assign productUrl><@ofbizCatalogUrl productId=product.productId currentCategoryId=categoryId  previousCategoryId=crumbs/></#assign>
    <#if requestAttributes.productCategoryMember?exists>
        <#assign prodCatMem = requestAttributes.productCategoryMember>
    </#if>
    <#assign smallImageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists>
    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg"></#if>
    <#-- end variable setup -->
    <#assign productInfoLinkId = "productInfoLink">
    <#assign productInfoLinkId = productInfoLinkId + product.productId/>
    <#assign productDetailId = "productDetailId"/>
    <#assign productDetailId = productDetailId + product.productId/>
    
   
    <#if product.isVariant =="Y">
    <#assign PRODUCTCATMEB = product.productId>
    <#assign productIndexUrl = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getProductIndexUrl(request,PRODUCTCATMEB)/>
    
    </#if>
    <div class="productsummary" onmouseover="showcom('AB${productUrl}')" onmouseout="hidecom('AB${productUrl}')">
    
  		<div class="smallimage">
            <a href="${productUrl}<#if product.isVariant =="Y"> ${productIndexUrl}</#if>" id="ABlink${product.productId}">
                <span id="${productInfoLinkId}" class="popup_link"><img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" name="A${product.productId}" alt="Small Image" style="outline:none; border:none;"/></span>
            </a>
        </div>
        
        <div class="productinfo">
        	<div class="listview" style="height:82px !important;">
	          	<div style="height:30px;" >
	            	<a href="${productUrl}<#if product.isVariant =="Y"> ${productIndexUrl}</#if>" class="linktext"  id="desclink${product.productId}" title="${productContentWrapper.get("BRAND_NAME")?if_exists} ${br?if_exists} ">
	            	<div style="color:#ed670e;">${productContentWrapper.get("BRAND_NAME")?if_exists}&nbsp;</div>
	            	<div id='productNameDescrip_${product.productId}' class="descrip">${br?if_exists}</div>
	            	<span style="font-size:11px;" id="variant_product_name${product.productId}"></span>
	            	
	            	
	            	
	            	<div style="font-size:11px;" id="product_id_display${product.productId}" style="display:block">${br1?if_exists}</div>
	            	<div id="product_id_display1${product.productId}" style="display:none">${br1?if_exists}</div>
	            	</a>
	            	<strong><span id="product_id_displays${product.productId}"> </span></strong>
	          	</div>
	          	<div><#--${productContentWrapper.get("DESCRIPTION")?if_exists}--><#if daysToShip?exists>&nbsp;-&nbsp;${uiLabelMap.ProductUsuallyShipsIn} <b>${daysToShip}</b> ${uiLabelMap.CommonDays}!</#if></div>
	          	<#-- Display category-specific product comments -->
	          	<#if prodCatMem?exists && prodCatMem.comments?has_content>
	          		<div>${prodCatMem.comments}</div>
	          	</#if>
	          	<#--if sizeProductFeatureAndAppls?has_content>
	            	<div>
		              	<#if (sizeProductFeatureAndAppls?size == 1)>
		                	${uiLabelMap.SizeAvailableSingle}:
		              	<#else>
		               	 	${uiLabelMap.SizeAvailableMultiple}:
		              	</#if>
	              		<#list sizeProductFeatureAndAppls as sizeProductFeatureAndAppl>
	                		${sizeProductFeatureAndAppl.abbrev?default(sizeProductFeatureAndAppl.description?default(sizeProductFeatureAndAppl.productFeatureId))}<#if sizeProductFeatureAndAppl_has_next>,</#if>
	              		</#list>
	            	</div>
	          	</#if-->
	         <div style="color:#ff0000 !important;"  class="priceview" >
	          	<div style="color:#ff0000 !important;"   id="detailprice${product.productId}">
	              	<#--<b>${product.productId?if_exists}</b><br/>-->
	                <#if totalPrice?exists>
	                  	<div style="color:#000000 !important">${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice' style="color:#000000 !important"><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
	                <#else>
	                	<#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
	                  		${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice' style="color:#000000 !important"><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
	                	</#if>
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                	<span class="${priceStyle?if_exists}" style="color:#000000 !important"><span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;</span><del><span class="${priceStyle?if_exists}" style="color:#000000 !important" id="variant_listprice_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.listPrice?if_exists}</span></del><br/>
	                  		
	                	</#if>
	                	<b style="color:#000000 !important">
	                		
		                  	<#if price.isSale?exists && price.isSale>
		                  <#--  <span class="salePrice">${uiLabelMap.OrderOnSale}!</span>-->
		                    	<#assign priceStyle = "salePrice">
		                  	<#else>
		                    	<#assign priceStyle = "regularPrice">
		                  	</#if>
	                  		<#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
	                    		<#--${uiLabelMap.OrderYourPrice}MRP:--> <#if "Y" = product.isVirtual?if_exists> </#if><span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_price_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.price?if_exists}</span>
	                  		</#if>
	                	</b>
	                
	                	
	                	
	                   <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
                  <#assign priceSaved = price.listPrice?double - price.price?double>
                  <#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
                  <span style="font-weight:normal;"> (${uiLabelMap.OrderSave}:<span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_discountprice_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${priceSaved?if_exists})</span></span><br/>
                    <div class="discount-${percentSaved?int?if_exists} discountmain" ></div>
                </#if>
                </#if>
                <#-- show price details ("showPriceDetails" field can be set in the screen definition) -->
                <#if (showPriceDetails?exists && showPriceDetails?default("N") == "Y")>
                    <#if price.orderItemPriceInfos?exists>
                        <#list price.orderItemPriceInfos as orderItemPriceInfo>
                            <div>${orderItemPriceInfo.description?if_exists}</div>
                        </#list>
                    </#if>
                </#if>
	          	</div>
	          	<a href="${productUrl?if_exists}" id="rating_${product.productId}">
	          	<div>
		          	<div class="productPage_review">	Review <#if averageRating?exists && (averageRating?double > 0) && numRatings?exists && (numRatings?long > 2)>
		              	(${numRatings})
		          	</#if> :</div>
	          	 	<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px;float:left;margin:5px 0;">
						<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:<#if  averageRating?exists>${(averageRating*12)?if_exists}<#else>0</#if>px; height:11px">
						</div><br/> 
			   		</div>
		   		</div>
		   		</a>
		   		<div style="clear:both;"></div>
 	      <div  style="color: rgb(255, 0, 0) ! important;height:10px;width:100% !important;" >
          	<span id="tempListPrice${product.productId}" style="color: rgb(255, 0, 0);display:none">
          		<span class="${priceStyle?if_exists}" style="color:#000000 !important">
          		<span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;
          		</span>
          		<del>
          		<span class="${priceStyle?if_exists}" style="color:#000000 !important" id="tempList${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>--></span>
          		</del>
          	</span>
	       	<span id="tempDefault${product.productId}" style="font-weight: bold;display:none">
	            <#--${uiLabelMap.OrderYourPrice}MRP:-->  
	            <#if "Y" = product.isVirtual?if_exists> <!--${uiLabelMap.CommonFrom} --></#if>
	            <span class="${priceStyle?if_exists}"><span class="WebRupee">&#8377;</span>&nbsp;
	            </span>
	            <span class="${priceStyle?if_exists}" id="temdefault${product.productId}" style="color:#000000 !important" ><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed />--></span>
	        </span>
	    	<span id="tempSave${product.productId}" class="discounted_Price_Show">
	            (${uiLabelMap.OrderSave}:<span class="${priceStyle?if_exists}">
	            <span class="WebRupee" style="color: rgb(255, 0, 0);">&#8377;</span>&nbsp;</span>
	            <span class="${priceStyle?if_exists}" id="tempSavePrice${product.productId}" style="color: rgb(255, 0, 0);"></span>)<br/>
	         <span id="tempDiscount${product.productId}"></span>
	        </span>
     	</div>
     	</div>
	
	          	
          	</div>
          	<div class="productbuy">
          		<#-- check to see if introductionDate hasn't passed yet -->
          		<#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
            		<div style="color: red;margin-top:20px !important;">${uiLabelMap.ProductNotYetAvailable}</div>
          		<#-- check to see if salesDiscontinuationDate has passed -->
          		<#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
            		<div style="color: red;margin-top:20px !important;">${uiLabelMap.ProductNoLongerAvailable}</div>
          			<#-- check to see if it is a rental item; will enter parameters on the detail screen-->
          		<#elseif product.productTypeId?if_exists == "ASSET_USAGE">
            		<a href="${productUrl}" class="buttontext">${uiLabelMap.OrderMakeBooking}...</a>
	          		<#-- check to see if it is an aggregated or configurable product; will enter parameters on the detail screen-->
	          	<#elseif product.productTypeId?if_exists == "AGGREGATED">
	            	<a href="${productUrl}" class="buttontext">${uiLabelMap.OrderConfigure}...</a>
	          		<#-- check to see if the product is a virtual product -->
	          	<#elseif product.isVirtual?exists && product.isVirtual == "Y">
          			<#if context.get("productStore")?has_content>
          				<#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
          					<#if product.isVirtual?has_content && "Y" == product.isVirtual>
         						
         						<form method="post" action="" name="addform${product.productId}">
          							<#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
               							<#if variantTree?exists && (variantTree.size() &gt; 0)>
                							
                							<#list featureSet as currentType>
						                  		<div class="weightselect" style="height:20px; display:inline;">
						                     		<select style="padding:0px !important; width:120px; margin:0px !important;" name="FT${currentType}" class="ListboxWidthfix" onchange="javascript:getList(this.name, (this.selectedIndex), this.selectedIndex,'${product.productId}');">
							                  			
							                  			<#list variantTree.keySet() as ckey>
							                  			  <#assign s = variantTree.get(ckey)>
							                  			 <#assign s1=s?string>
							                  			 <#assign s2 =  s1?substring(1,(s1?length)-1)>
							                  				<option value="${s2?if_exists}">${ckey}</option>
							                  			 </#list>
							                		</select>
						                  		</div>
						                	</#list>
						                	<#if inventoryMap?exists>
											         <#list inventoryMap.keySet() as inventoryMapTemp>
														<input type="hidden" name="${inventoryMapTemp?if_exists}" id="Inventory${inventoryMapTemp?if_exists}" value="${inventoryMap.get(inventoryMapTemp)?if_exists}"/>
													</#list>
											</#if>
						                	<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
                							<span id="product_uom"></span>
                							<input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
                							<input type="hidden" name="product_id" value="${product.productId}"/>
                							<input type="hidden" name="add_product_id" value="NULL" id="abc${product.productId}"/>
                							<input type="hidden" name="add_product_name" value="${br?if_exists}"/>
                							<input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
                 							
								            <div class="listviewtab"> 
		                 						<div style="float:left; margin-bottom:10px; position:absolute;right: 230px;" class="quantitylisttab">
		                 						<table cellpadding="0" cellspacing="0" border="0">
		                 						 <tr>
		                 							<td style="width:30px;">Qty:</td>
		                 							<td>
			                 						    <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
			                 						</td>
			                 						<td>
			                 						    <input type="text" style="width:20px;" id ="qty${product.productId?if_exists}" onblur="productDetail('${product.productId?if_exists}','')" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" class="inputBox"  name="quantity" value="1"/>
			                 						</td>
			                 						<td>
			                 						    <a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
                 									</td>
                 								 </tr>
                 								</table>
                 								<input type="hidden" name="clearSearch" value="Y"/>
                 								</div>
                 								<div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important;margin-top:15px;" class="gridadd">
                 									<div id="addstock${product.productId?if_exists}"><a href="javascript:addItems('${product.productId}','abc${product.productId}', addform${product.productId})" id="add${product.productId?if_exists}" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;</div>
                 									<div id="outstock${product.productId?if_exists}" style="display:none;">
                 									<img src="/erptheme1/out-of-stock.png" alt="" title=""/></div>
              									</div>
              								</div>
              							<#else>
                							<input type="hidden" name="add_product_id" value="NULL"/>
                							<#assign inStock = false />
             								</#if>
             						</#if>
          						</form>
          						<#-- ADD TO CART -->
         					</#if>
          				</#if>
          			</#if>	
          <#-- check to see if the product requires an amount -->
          <#elseif product.requireAmount?exists && product.requireAmount == "Y">
          	<#if context.get("productStore")?has_content> 
          	<#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
            	<a href="${productUrl}" class="buttontext">${uiLabelMap.OrderChooseAmount}...</a>
            </#if>
            </#if>
          <#else>
          <#if context.get("productStore")?has_content> 
           <#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
          	<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
          	
            <form method="post" action="<@ofbizUrl>additem1</@ofbizUrl>" name="the${product.productId}form" style="margin: 0;">
              <input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
              <input type="hidden" name="add_product_id" value="${product.productId}"/>
              <input type="hidden" name="add_product_name" value="${product.productName?if_exists}"/>
              <input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
               						
              <div class="listviewtab">
	             <div style="float:left;  position:absolute;right: 230px;" class="quantitylisttab" >
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
              
              	 <div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important;margin-top:15px;" class="gridadd">
              	
              	 <#if isStoreInventoryAvailable>
              <#assign newProductName= product.productName?if_exists/>
              <#assign newProductName= newProductName?replace("&#39;", "")/>
             	 <a href="javascript:addItem(the${product.productId}form,'${newProductName}');" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
             	  
             	<#else> <img src="/erptheme1/out-of-stock.png" alt="" title=""/>
             	</#if>
              	 </div>
              </div>
              
            </form>
            
            <#if !(requestAttributes.buynow?has_content && requestAttributes.buynow == "N")>
              <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
                  <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
                  <input type="hidden" id="qty${product.productId?if_exists}" onblur="productDetail('${product.productId?if_exists}','')" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" name="quantity" value="${prodCatMem.quantity?if_exists}"/>
                  <input type="hidden" name="clearSearch" value="N"/>
                  <a href="javascript:document.the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform.submit()" class="buttontext">${uiLabelMap.CommonAddDefault}(${prodCatMem.quantity?string.number}) ${uiLabelMap.OrderToCart}</a>
                </form>
              </#if>
              </#if>
            </#if>  
          </#if>
          </#if>
          <#--<form  style="margin-top:5px" method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">addToCompare</@ofbizUrl>" name="addToCompare${requestAttributes.listIndex?if_exists}form">
              <input type="hidden" name="productId" value="${product.productId}"/>
              <input type="hidden" name="category_id" value="${parameters.category_id?if_exists}"/>
              <input type="hidden" name="VIEW_SIZE" value="${parameters.VIEW_SIZE?if_exists}"/>
              <input type="hidden" name="VIEW_INDEX" value="${parameters.VIEW_INDEX?if_exists}"/>
              <a href="javascript:document.addToCompare${requestAttributes.listIndex?if_exists}form.submit()" class="buttontext">${uiLabelMap.ProductAddToCompare}</a>
          </form>-->
          
          
          <#--<form method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">addToCompare</@ofbizUrl>" name="addToCompare${requestAttributes.listIndex?if_exists}form">
              <input type="hidden" name="productId" value="${product.productId}"/>
          </form>
          <a href="javascript:document.addToCompare${requestAttributes.listIndex?if_exists}form.submit()" class="buttontext">${uiLabelMap.ProductAddToCompare}</a>-->
          
          
        </div>
          <#--<form method="post" action="<@ofbizUrl secure="${request.isSecure()?string}">addToCompare</@ofbizUrl>" name="addToCompare${requestAttributes.listIndex?if_exists}form">
              <input type="hidden" name="productId" value="${product.productId}"/>
          </form>
          <a href="javascript:document.addToCompare${requestAttributes.listIndex?if_exists}form.submit()" class="buttontext">${uiLabelMap.ProductAddToCompare}</a>-->
        </div>
        <div class="ProductCompareButton"  id="AB${productUrl}">
           <form   method="post" action="<@ofbizUrl>addToCompare</@ofbizUrl>" name="addToCompare${product.productId?if_exists}form">
              <input type="hidden" name="productId" value="${product.productId}"/>
              <input type="hidden" name="category_id" value="${parameters.category_id?if_exists}"/>
              <input type="hidden" name="VIEW_SIZE" value="${parameters.VIEW_SIZE?if_exists}"/>
              <input type="hidden" name="VIEW_INDEX" value="${parameters.VIEW_INDEX?if_exists}"/>
              <div class="buttontext">
	          	<a href="javascript:addToCompare1('${product.productId}','${parameters.category_id?if_exists}','${parameters.VIEW_SIZE?if_exists}','${parameters.VIEW_INDEX?if_exists}');" >
				</a>
	          </div>
           </form>
          
         </div>
    </div>
    <div class="clear"></div>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>

<#if product.isVirtual =="Y">

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
 		 window.onload=getList('FTNET_WEIGHT', '0', '0','${product.productId}' );
    </script>
</#if>


 