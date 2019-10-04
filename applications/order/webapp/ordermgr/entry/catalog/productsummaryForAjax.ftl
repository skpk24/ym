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
    
    <div class="productsummary" onmouseover="showcom('AB${productUrl}')" onmouseout="hidecom('AB${productUrl}')">
  		<div class="smallimage">
            <a href="${productUrl}" id="ABlink${product.productId}">
                <span id="${productInfoLinkId}" class="popup_link"><img src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" name="A${product.productId}" alt="Small Image"/></span>
            </a>
        </div>
        
        <div class="productinfo">
        	<div class="listview">
	          	<div style="height:30px;" >
	            	<a href="${productUrl}" class="linktext"  id="desclink${product.productId}" title="${productContentWrapper.get("BRAND_NAME")?if_exists} ${br?if_exists} ">
	            	<div style="color:#ed670e;">${productContentWrapper.get("BRAND_NAME")?if_exists}</div>
	            	<div class="descrip">${br?if_exists}</div>
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
	          	<div style="color:#ff0000 !important;"  class="priceview">
	              	<#--<b>${product.productId?if_exists}</b><br/>-->
	                <#if totalPrice?exists>
	                  	<div>${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
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
	                    		<#--${uiLabelMap.OrderYourPrice}MRP:--> <#if "Y" = product.isVirtual?if_exists> </#if><span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_price_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.price?if_exists}</span><br/>
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
	                	
	                	<div class="discount-${percentSaved?int?if_exists} discountmain" style="left: 160px;top: -200px;"></div>
	                	
	                	
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
          	<div class="productbuy">
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
						                	
						                	<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
                							<span id="product_uom"></span>
                							<input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
                							<input type="hidden" name="product_id" value="${product.productId}"/>
                							<input type="hidden" name="add_product_id" value="NULL" id="abc${product.productId}"/>
                							<input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
                 							
								            <div class="listviewtab">
		                 						<div style="float:left;" class="quantitylisttab">
		                 						<table cellpadding="0" cellspacing="0" border="0">
		                 						 <tr>
		                 							<td style="width:30px;">Qty:</td>
		                 							<td>
			                 						    <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
			                 						</td>
			                 						<td>
			                 						    <input type="text" style="width:20px;" id ="qty${product.productId?if_exists}" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" class="inputBox"  name="quantity" value="1"/>
			                 						</td>
			                 						<td>
			                 						    <a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
                 									</td>
                 								 </tr>
                 								</table>
                 								<input type="hidden" name="clearSearch" value="Y"/>
                 								</div>
                 								<div style="float:right; vertical-align:right !important; text-align:right !important; padding:4px !important;">
              										<div id="addstock${product.productId?if_exists}"><a href="javascript:addItems('abc${product.productId}', addform${product.productId})" id="add${product.productId?if_exists}" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;</div>
                 									<div id="outstock${product.productId?if_exists}"style="display:none;"><a href="#" id="out${product.productId?if_exists}"><img src="/erptheme1/out-of-stock.png" alt="" title=""/></a>&nbsp;</div>
                 									
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
              <input type="hidden" name="add_category_id" value="${product.primaryProductCategoryId?if_exists}" id="category${product.productId}"/>
                 							
              <div class="listviewtab">
	             <div style="float:left;" class="quantitylisttab" >
	             	<table cellpadding="0" cellspacing="0" border="0">
					 <tr>
						<td style="width:30px;">Qty:</td>
						<td>
 						    <a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
 						</td>
	                    <td>
	                    	<input type="text" id="qty${product.productId?if_exists}" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" class="inputBox" style="width:20px;" name="quantity" value="1"/>
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
             	<#else> <a href="#"><img src="/erptheme1/out-of-stock.png" alt="" title=""/></a>
             	</#if>
              	 </div>
              </div>
            </form>
            
            <#if !(requestAttributes.buynow?has_content && requestAttributes.buynow == "N")>
              <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
                  <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
                  <input type="hidden" id="qty${product.productId?if_exists}" onkeypress="return isNumberKey(event,'${product.productId?if_exists}');" name="quantity" value="${prodCatMem.quantity?if_exists}"/>
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
<#else>
&nbsp;${uiLabelMap.ProductErrorProductNotFound}.<br />
</#if>

<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
