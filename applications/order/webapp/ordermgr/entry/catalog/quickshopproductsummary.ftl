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
function addItempp(add_product_id) {

    var quantity=document.getElementById('prodQty'+add_product_id).value;
     //alert("quantity="+quantity);
     
     if (add_product_id == '') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       }else {
           var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity;
                      jQuery.ajax({url: '/control/additem',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#minicart').html(data);
           jQuery.ajax({url: '/control/findprodwgt',
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	         document.getElementById('outputs').innerHTML = data;
	         ShowDialog(false);
	         },
        error: function(data) {
        }
    	});
    	//setTimeout(function() {
     // location.reload();
   // }, 1000);
         },
         complete:  function() { 
         //var minitotal = document.getElementById('minitotal').value;
          //var miniquantity = document.getElementById('miniquantity').value;
          // $('#microCartTotal').text(minitotal);
          //$('#microCartQuantity').text(miniquantity);
          
          var minitotal = document.getElementById('abcxyz').innerHTML;
          var miniquantity = document.getElementById('miniquantityA').value;
           $('#microCartTotal').text(minitotal);
           document.getElementById('microCartQuantity').innerHTML=miniquantity;
           document.getElementById('checkoutdis').style.display="block";
          document.getElementById('abcxyzhref').href="/control/showcart";
          
         },
        error: function(data) {
        }
    	});
         
      }
    }
    function changeQty(p_id){
    	var product_Id = p_id+'abc'+p_id;
    	var quantity=document.getElementById(product_Id).value;
    	document.getElementById('prodQty'+p_id).value = quantity;
    }
    
    function ShowDialog(modal)
   {
      $("#overlay").show();
      $("#dialog").fadeIn(300);

      if (modal)
      {
         $("#overlay").unbind("click");
      }
      else
      {
         $("#overlay").click(function (e)
         {
            HideDialog();
         });
      }
      setTimeout(function() {
        $("#overlay").hide(),
      $("#dialog").fadeOut(300)
    }, 3000);
   }
   
   function HideDialog()
   {
      $("#overlay").hide();
      $("#dialog").fadeOut(300);
   }
    function isNumberKey(evt)
      	{
         	var charCode = (evt.which) ? evt.which : event.keyCode
         	if (charCode > 31 && (charCode < 48 || charCode > 57))
            	return false;
         		return true;
      	}
      	
                           function chgQty0(delta,id) {
                           var txtbox = document.getElementById(id);
                     var value = txtbox.value;
                                  qty = parseFloat(value) + delta;
                                  if ((delta > 0) && (value == "")) {
                                         if (delta < 1.0) {
                                                delta = 1.0;
                                         }
                                         qty = delta;
                                  } else if (isNaN(qty) || (qty < 0)) {
                                         // document.getElementById(id).value = "0";
                                         return;
                                  } else if ((qty > 0) && (qty < 1.0) && (delta < 0)) {
                                         document.getElementById(id).value = "0";
                                         return;
                                   } else if ((qty > 0) && (qty < 1.0) && (delta >= 0)) {
                                         qty = 1.0;
                                  } 
                                  qty = Math.floor( (qty-1.0)/1.0 )*1.0  + 1.0;
                                  document.getElementById(id).value = qty;
                           }
</script>

<a href="javascript:chgQty0(-1.0,'qty${product.productId?if_exists}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus.png" alt=""/></a>
<a href="javascript:chgQty0(1.0,'qty${product.productId?if_exists}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add.png" alt=""/></a>
<#if product?exists>
	<#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString(delegator,categoryId)/>
    <#-- variable setup -->
    <#assign productUrl><@ofbizCatalogUrl productId=product.productId previousCategoryId=crumbs/></#assign>
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
	          	<div>
	            	<a href="${productUrl}" class="linktext">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</a>
	            	<strong><span id="product_id_display${product.productId}"> </span></strong>
	          	</div>
	          	<div><#--${productContentWrapper.get("DESCRIPTION")?if_exists}--><#if daysToShip?exists>&nbsp;-&nbsp;${uiLabelMap.ProductUsuallyShipsIn} <b>${daysToShip}</b> ${uiLabelMap.CommonDays}!</#if></div>
	          	<#-- Display category-specific product comments -->
	          	<#if prodCatMem?exists && prodCatMem.comments?has_content>
	          		<div>${prodCatMem.comments}</div>
	          	</#if>
	          	<#if sizeProductFeatureAndAppls?has_content>
	            	<div style="height:30px; width:250px; text-align:center; overflow:hidden;">
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
	          	  <div style="color:#ff0000 !important;"  class="priceview" >
	          	<div style="color:#006e37 !important; padding:5px" id="detailprice${product.productId}">
	              	<#--<b>${product.productId?if_exists}</b><br/>-->
	                <#if totalPrice?exists>
	                  	<div>${uiLabelMap.ProductAggregatedPrice}: <span class='basePrice'><@ofbizCurrency amount=totalPrice isoCode=totalPrice.currencyUsed/></span></div>
	                <#else>
	                	<#if price.competitivePrice?exists && price.price?exists && price.price?double < price.competitivePrice?double>
	                  		${uiLabelMap.ProductCompareAtPrice}: <span class='basePrice'><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed/></span>
	                	</#if>
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  	<span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><del><span class="${priceStyle}" id="variant_listprice_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.listPrice?if_exists}</span></del><br/>
	                	</#if>
	                	<b>
		                  	<#if price.isSale?exists && price.isSale>
		                    <span class="salePrice">${uiLabelMap.OrderOnSale}!</span>
		                    	<#assign priceStyle = "salePrice">
		                  	<#else>
		                    	<#assign priceStyle = "regularPrice">
		                  	</#if>
	                  		<#if (price.price?default(0) > 0 && product.requireAmount?default("N") == "N")>
	                    		<#--${uiLabelMap.OrderYourPrice}-->Our Price: <#if "Y" = product.isVirtual?if_exists> ${uiLabelMap.CommonFrom} </#if><span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_price_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.price?if_exists}</span>
	                  		</#if>
	                	</b>
	                	<#if price.listPrice?exists && price.price?exists && price.price?double < price.listPrice?double>
	                  		<#assign priceSaved = price.listPrice?double - price.price?double>
	                  		<#assign percentSaved = (priceSaved?double / price.listPrice?double) * 100>
	                    	${uiLabelMap.OrderSave}:<span class="${priceStyle}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle}" id="variant_discountprice_display${product.productId}"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${priceSaved?if_exists}</span><br/>
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
	    	<span id="tempSave${product.productId}" style="color: rgb(255, 0, 0);display:none !important;">
	            (${uiLabelMap.OrderSave}:<span class="${priceStyle?if_exists}">
	            <span class="WebRupee" style="color: rgb(255, 0, 0);">&#8377;</span>&nbsp;</span>
	            <span class="${priceStyle?if_exists}" id="tempSavePrice${product.productId}" style="color: rgb(255, 0, 0);"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>--></span>)<br/>
	       	 <span id="tempDiscount${product.productId}" ></span>
	        </span>
     	</div>
	 
          	</div>
          	
          	
          	<div class="productbuy" >
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
          			<#if productStoreId?has_content>
          				<#if !(productStoreId.visualThemeId?has_content  && productStoreId.visualThemeId == "MULTIFLEX")>
          					<#if product.isVirtual?has_content && "Y" == product.isVirtual>
         						<form method="post" action="" name="addform${product.productId}">
          							<#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
               							<#if variantTree?exists && (variantTree.size() &gt; 0)>
               								
                							<#list featureSet as currentType>
						                  		<div style="padding:10px">
						                     		<select name="FT${currentType}" class="ListboxWidthfix" onchange="javascript:getList(this.name, (this.selectedIndex), (this.selectedIndex),'${product.productId}');">
							                  			<option>${featureTypes.get(currentType)}</option>
							                		</select>
						                  		</div>
						                	</#list>
						                	<#if inventoryMap?exists>
						                	  <#list inventoryMap.keySet() as inventoryMapTemp>
								<input type="hidden" name="${inventoryMapTemp}" id="Inventory${inventoryMapTemp}" value="${inventoryMap.get(inventoryMapTemp)}"/>
								</#list>
								</#if>
						                	
                							<span id="product_uom"></span>
                							<input type="hidden" name="product_id" value="${product.productId}"/>
                							<input type="hidden" name="add_product_id" value="NULL" id="abc${product.productId}"/>
                 							<input type="text" class="inputBox" size="3" name="quantity" value="1"/>
                 							<input type="hidden" name="clearSearch" value="Y"/>
                 							<div id="addstock${product.productId?if_exists}">
                 								<a href="javascript:addItems('abc${product.productId}', addform${product.productId})" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
              									&nbsp;
              								</div>
                 							<div id="outstock${product.productId?if_exists}" style="display:none;">
                 									<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
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
          <#if productStoreId?has_content> 
           <#if !((productStoreId).visualThemeId?has_content  && productStoreId.visualThemeId == "MULTIFLEX")>
          	<#if !(requestAttributes.buynow?has_content && requestAttributes.buynow == "N")>
            <form method="post" action="<@ofbizUrl>additem1</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}form" style="margin: 0;">
              <input type="hidden" name="add_product_id" id="add_product_id" value="${product.productId}"/>
              <input type="hidden" name="prodQty" id="prodQty${product.productId}" value="1"/>
              <input type="text" style="width:40px;" size="5" class="inputBox" onKeyup="return changeQty('${product.productId}');" onKeyPress = "return isNumberKey(event);" name="quantity" id="${product.productId}abc${product.productId}" value="1"/>
              <input type="hidden" name="clearSearch" value="N"/>
             <#--a href="javascript:addItem(the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}form)" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;-->
                 <a href="javascript:addItempp('${product.productId}')" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
            </form>
              <#if prodCatMem?exists && prodCatMem.quantity?exists && 0.00 < prodCatMem.quantity?double>
                <form method="post" action="<@ofbizUrl>additem</@ofbizUrl>" name="the${requestAttributes.formNamePrefix?if_exists}${requestAttributes.listIndex?if_exists}defaultform" style="margin: 0;">
                  <input type="hidden" name="add_product_id" value="${prodCatMem.productId?if_exists}"/>
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
<#if variantTree?exists && 0 &lt; variantTree.size()>

     <script type="text/javascript">eval("list" + "${featureOrderFirst}${product.productId}" + "()");</script>
</#if>
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
<script>
 		window.onload=getList('FTNET_WEIGHT', '0', '0','${product.productId}' );
    </script>