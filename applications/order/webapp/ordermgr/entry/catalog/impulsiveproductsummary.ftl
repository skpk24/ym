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
${virtualJavaScript?if_exists}
</#if>
<#if product?exists>
    <#-- variable setup -->
    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",product.productId))?if_exists>
    <#if productCategory?has_content>
				    	<#assign prodCategory = productCategory.get(0)?if_exists>
		                <#assign productCategoryId = prodCategory.get("productCategoryId")>
		            </#if>
		            
		            <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,product.productId)/>
				    <#assign productUrl><@ofbizCatalogUrl productId=product.productId currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
				    
				    <#assign productIndexUrl = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getProductIndexUrl(request,product.productId)/>
				    
    
    <#assign productUrl1 = productUrl + productIndexUrl/>
    <#--assign productUrl1><@ofbizCatalogUrl productId=product.productId currentCategoryId=categoryId/></#assign-->
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
    
    <div class="productsummary" style="border:none !important;" <#--onmouseover="showcom('AB${productUrl1}')" onmouseout="hidecom('AB${productUrl1}')"-->>
  		<div class="smallimage" style="text-align:center !important; width:100%;padding:5px; ">
            <a href="${productUrl1}" id="ABlink${product.productId}">
                <span id="${productInfoLinkId}" class="popup_link"><img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" style=" height:90px !important;" alt="Small Image" name="A${product.productId}"/></span>
            </a>
        </div>
        <div class="productinfo">
        	<div class="listview" style="text-align:center !important; width:100%">
	          	<div style="height:80px;">
	            	<a href="${productUrl1}" class="linktext"  id="desclink${product.productId}" title="${productContentWrapper.get("BRAND_NAME")?if_exists} ${br?if_exists} ">
	            	<div style="color:#ed670e;">${productContentWrapper.get("BRAND_NAME")?if_exists}</div>
	            	<div class="descrip" id='productNameDescrip_${product.productId}'>${br?if_exists}</div>
	            	<span style="font-size:11px;" id="variant_product_name${product.productId}"></span> 
	            	<div style="font-size:11px;" id="product_id_display${product.productId}" style="display:block">${br1?if_exists}</div>
	            	<div id="product_id_display1${product.productId}" style="display:none">${br1?if_exists}</div>
	            	</a>
	          	</div>
	          	<div><#--${productContentWrapper.get("DESCRIPTION")?if_exists}--><#if daysToShip?exists>&nbsp;-&nbsp;${uiLabelMap.ProductUsuallyShipsIn} <b>${daysToShip}</b> ${uiLabelMap.CommonDays}!</#if></div>
	          	<#-- Display category-specific product comments -->
	          	<#if prodCatMem?exists && prodCatMem.comments?has_content>
	          		<div>${prodCatMem.comments}</div>
	          	</#if>
	          	<#if sizeProductFeatureAndAppls?has_content>
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
	          	</#if>
	          	  <div style="color:#ff0000 !important;border-top:1px dashed #cccccc;margin-top:5px;" >
	          	<div style="color:#ff0000 !important; padding:5px;" id="detailprice${product.productId}">
	              	<#--<b>${product.productId?if_exists}</b><br/>-->
	                <#if totalPrice?exists>
	                  	<div>${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
	                <#else>
	                	<#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
	                  		${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice'><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
	                	</#if>
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  			<span class="${priceStyle?if_exists}" style="color:#000000 !important"><span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;</span><del><span class="${priceStyle?if_exists}" style="color:#000000 !important" ><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.listPrice?if_exists}</span></del><br/>
	                	</#if>
						<#if price.isSale?exists && price.isSale>
		                    <span class="salePrice"></span>
		                    	<#assign priceStyle = "salePrice">
		                  	<#else>
		                    	<#assign priceStyle = "regularPrice">
		                  	</#if>
	                  		<#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
	                    		<#--${uiLabelMap.OrderYourPrice}MRP:--> <#if "Y" = product.isVirtual?if_exists> </#if><span class="${priceStyle?if_exists}" style="color:#000000 !important"><span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" style="color:#000000 !important" ><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.price?if_exists}</span>
	                  		</#if>
	                	</b>
	                	
	                        <#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
                  <#assign priceSaved = price.listPrice?double - price.price?double>
                  <#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
                   Save:<span class="${priceStyle?if_exists}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" ><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${priceSaved?if_exists}</span><br/>
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
	          	
	          	<#if averageRating?exists && (averageRating?double > 0) && numRatings?exists && (numRatings?long > 2)>
	              	<div>${uiLabelMap.OrderAverageRating}: ${averageRating} (${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})</div>
	          	</#if>
          	</div>
         
         
         
         <div style=" width:190px; float:left; " ></div>
         
          		<div style="float:left; text-align:center;  ">
          				
		                  
	             </div>   	
	             	 <div style="color: rgb(255, 0, 0) ! important;width:100% !important;padding:5px" >
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
	    	<span id="tempSave${product.productId}" style="color: rgb(255, 0, 0);display:none">
	            (Save:<span class="${priceStyle?if_exists}" style="color: rgb(255, 0, 0);">
	            <span class="WebRupee" style="color: rgb(255, 0, 0);">&#8377;</span>&nbsp;</span>
	            <span class="${priceStyle?if_exists}" id="tempSavePrice${product.productId}" style="color: rgb(255, 0, 0);"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>--></span>)<br/>
	      		  <span id="tempDiscount${product.productId}"></span>
	        </span>
     	</div>
	 </div>
          	
          	
	             
          	<div class="productbuy" style="text-align:center !important; width:100%; margin-top:12px;" >
          		<#-- check to see if introductionDate hasn't passed yet -->
          		<#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
            		<div style="color: red;">${uiLabelMap.ProductNotYetAvailable}</div>
          		<#-- check to see if salesDiscontinuationDate has passed -->
          		<#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
            		<div style="color: red;">${uiLabelMap.ProductNoLongerAvailable}</div>
          			<#-- check to see if it is a rental item; will enter parameters on the detail screen-->
          		<#elseif product.productTypeId?if_exists == "ASSET_USAGE">
            		<a href="${productUrl1}" class="buttontext">${uiLabelMap.OrderMakeBooking}...</a>
	          		<#-- check to see if it is an aggregated or configurable product; will enter parameters on the detail screen-->
	          	<#elseif product.productTypeId?if_exists == "AGGREGATED">
	            	<a href="${productUrl1}" class="buttontext">${uiLabelMap.OrderConfigure}...</a>
	          		<#-- check to see if the product is a virtual product -->
	          	<#elseif product.isVirtual?exists && product.isVirtual == "Y">
          			<#if context.get("productStore")?has_content>
          				<#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
          					<#if product.isVirtual?has_content && "Y" == product.isVirtual>
         						<form method="post" action="" name="addform${product.productId}"> <input type="hidden" name="add_product_name" value="${product.productName?if_exists}" id="add_product_name"/> 
          							<#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
               							<#if variantTree?exists && (variantTree.size() &gt; 0)>
               								
                							<#list featureSet as currentType>
						                  		<div style="padding:0px 10px 0px 10px; position:absolute; top:157px; left:50px;" >
						                     		<select name="FT${currentType}" class="ListboxWidthfix" onchange="javascript:getList1(this.name, (this.selectedIndex), (this.selectedIndex),'${product.productId}');">
							                  			<#if featureTypes?has_content><option>${featureTypes.get(currentType)}</option></#if>
							                		</select>
						                  		</div>
						                	</#list>
						                	<#if inventoryMap?exists>
 												<#list inventoryMap.keySet() as inventoryMapTemp>
													<input type="hidden" name="${inventoryMapTemp?if_exists}" id="Inventory1${inventoryMapTemp?if_exists}" value="${inventoryMap.get(inventoryMapTemp)?if_exists}"/>
														</#list>
															</#if>
 										 <div style="position:absolute; top:245px; bottom:0px;">
              								<table cellpadding="0" cellspacing="0" border="0" width="100%">
												<tr>
												<td style="width:30px;"><span id="product_uom">Qty:</span></td>
		                 						<td>
		                 							 <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
		                 					    </td>
		                						<td>	
		                							<input type="hidden" name="product_id" value="${product.productId}"/>
		                							<input type="hidden" name="add_product_id" value="NULL" id="${product.productId}"/>
		                 							<input type="text" class="inputBox"  style="width:20px;" name="quantity" value="1" id ="qty${product.productId?if_exists}" onblur="productDetail('${product.productId?if_exists}','')" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');"/>
		                 							<input type="hidden" name="clearSearch" value="Y"/>
		                 						</td>
		                 						<td>
		                 							<a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
		                 						</td>
		                 						<td style="width:15px;"></td>
		                 						<td>	
		                 						
		                 						<div id="addstockP${product.productId?if_exists}" style="margin-top:5px;">
	                 						 <#assign newProductName= product.productName?if_exists/>
                                              <#assign newProductName= newProductName?replace("&#39;", "")/>
	                 						
		                 							<a href="javascript:addItems1('${product.productId}', addform${product.productId} , '${newProductName}')" class="buttontext">Add To Cart</a>
		                 						</div>
                 								<div id="outstockP${product.productId?if_exists}" style="display:none;">
                 									<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
                 								</div>
		              							</td>
                							 </tr>
                							</table>
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
            	<a href="${productUrl1}" class="buttontext">${uiLabelMap.OrderChooseAmount}...</a>
            </#if>
            </#if>
          <#else>
          <#if context.get("productStore")?has_content> 
           <#if !(context.get("productStore").visualThemeId?has_content  && context.get("productStore").visualThemeId == "MULTIFLEX")>
          	<#if !(requestAttributes.buynow?has_content && requestAttributes.buynow == "N")>
            <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="addform${product.productId}" style="margin: 0;">
              <input type="hidden" name="add_product_id" value="${product.productId}" id="${product.productId}"/>
              <input type="hidden" name="add_product_name" value="${product.productName}" id="add_product_name"/> 
               
               <div style="float:left; position:absolute; top:245px; bottom:0px;">
                <table cellpadding="0" cellspacing="0" border="0" width="100%">
					<tr>
						<td style="width:30px;">
							<span id="product_uom">Qty:</span>
						</td>
						<td>
						 	<a href="javascript:chgQty0(-1.0,'qty_${product.productId}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
 					    </td>
						<td>
  							<input type="text" class="inputBox" style="width:20px;" name="quantity" value="1" id="qty_${product.productId}"/>
						</td>
						<td>
						 	<a href="javascript:chgQty0(1.0,'qty_${product.productId}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
 					    </td>
 					    <td style="width:15px;"></td>
						<td>
							<input type="hidden" name="clearSearch" value="Y"/> 
							<div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important; width:100px;">
			              	 <#if isStoreInventoryAvailable>
				              	   <#assign newProductName= product.productName?if_exists/>
	                                <#assign newProductName= newProductName?replace("&#39;", "")/>
			             	 <a href="javascript:addItem(addform${product.productId},'${newProductName}');" class="buttontext">Add To Cart</a>&nbsp;
			             	<#else> <img src="/erptheme1/out-of-stock.png" alt="" title=""/>
			             	</#if>
			              	 </div>
						</td>
                   </tr>
                 </table>
              </div>
            </form>
              <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
                  <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
                  <input type="hidden" name="add_product_name" value="${prodCatMem.productName?if_exists}"/>
                  <input type="hidden" name="quantity" value="${prodCatMem.quantity?if_exists}"/>
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
        <div class="ProductCompareButton"  id="AB${productUrl1}">
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
    </div>
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>

<#if product.isVirtual =="Y">
<script> 
	<#if variantTree?exists && 0 &lt; variantTree.size()>
   		eval("list" + "${featureOrderFirst}${product.productId}" + "()");
   </#if>
 	window.onload=getList1('FTNET_WEIGHT', '0', '0','${product.productId}');
</script>
</#if>