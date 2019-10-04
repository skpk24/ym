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
<input type="hidden" value="${msg?if_exists}" id="qtyATPerrormsg"/>
<input type="hidden" value="${indexes_qty?if_exists}" id="indexes_qty"/>
<div id="showcartremove">
<#assign Par ="">
<#if request.getParameter('categoryId')?has_content>
<#assign Par = request.getParameter('categoryId')>
</#if>
<script type="text/javascript">
        function checkCondition(){
        var inputElements = document.getElementsByClassName('text1');
        	if(document.getElementById("selectAll").checked){
        	for(var i=0; inputElements[i]; ++i){
			       inputElements[i].checked = false;
			     }
			     document.getElementById("selectAll").checked = false; 
			}else{
			
				for(var i=0; inputElements[i]; ++i){
			       inputElements[i].checked = true;
			     }
			     document.getElementById("selectAll").checked = true; 
        	}
        }
	function updatecart(index){
	var cartindex = index;
		var cartvalue = document.getElementById("update_"+index).value;
		var cartMaxQty = 20 ; 		
			if(document.getElementById("updateMax"+index) != null) {
				cartMaxQty = parseInt(document.getElementById("updateMax"+index).value);
			} 
		var prodId = document.getElementById("prod_"+index).value;
		if(cartvalue > cartMaxQty)
			{
				document.getElementById("shoppingcarterror").innerHTML = "Can not add more than "+cartMaxQty+" items";
				document.getElementById("update_"+index).value = cartMaxQty;
				return;
			}
		 
		 var  param = 'update_'+cartindex+'=' + cartvalue+'&prodId='+prodId+'&quantity='+cartvalue;
	var url= '/control/modifycartplus';
	var url1 = '/control/removeshowcart';
	if (window.location.protocol != "http:"){
	 url= '/control/modifycartpluss';
	 url1 = '/control/removeshowcarts';
	}
	

			jQuery.ajax({url: url,
							         data: param,
							         type: 'post',
							         async: true,
							         success: function(data) { 
							        
							         if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
							          {
							          	 document.getElementById("shoppingcarterror").innerHTML = data;
								         return;
							          }
							         document.getElementById("shoppingcarterror").innerHTML = "";
								     $('#microcart').html(data); 
           
           var abcvalue = 0;
           if (abcvalue ==""){
           	           abcvalue = "0.00";
           	
           }
								       jQuery.ajax({url: url1,
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	         document.getElementById('showcartremove').innerHTML = data; 
            document.getElementById('update_'+index+'').innerHTML=cartvalue;
	          
	         
	       
	         },
        error: function(data) {
        }
    	});		 
								    
          
			 			      
								         },
								       
							        error: function(data) {
							        }
							        });
		
		
		}

</script>
<script type="text/javascript">
	$(document).ready(function(){
		<#if msg?has_content>
			recalculateCart();
		</#if>
		<#if Par?has_content>
		$('#${Par}').addClass('selected');
		<#else>
		$('#BestDeals').addClass('selected');
		</#if>
	});
	
	function recalculateCart(){
		var errorMsg = document.getElementById("qtyATPerrormsg").value;
		//errorMsg = errorMsg + "\n\n so recalculating cart ";
		alert(errorMsg);
		
		var indexes_qtys = document.getElementById("indexes_qty").value;
		var res = indexes_qtys.split(",");
		for (var i=0;i<res.length;i++)
		{
			var indexes_qty = res[i]; 
			if(indexes_qty != "")
			{
				var result = indexes_qty.split("____");
				document.getElementById("update_"+result[0]).value = result[1];
				updatecart(result[0]);
			}
		}
	}
</script>
<script type="text/javascript">
//<![CDATA[
function toggle(e) {
    e.checked = !e.checked;
}
function checkToggle(e) {
    var cform = document.cartform;
    if (e.checked) {
        var len = cform.elements.length;
        var allchecked = true;
        for (var i = 0; i < len; i++) {
            var element = cform.elements[i];
            if (element.name == "selectedItem" && !element.checked) {
                allchecked = false;
            }
            cform.selectAll.checked = allchecked;
        }
    } else {
        cform.selectAll.checked = false;
    }
}
function toggleAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        if (element.name == "selectedItem" && element.checked != e.checked) {
            toggle(element);
        }
    }
}
function removeSelected(count) {
var flag = false; 
var inputElements = document.getElementsByClassName('text1');
var selectCount = 0;
for(var i=0; inputElements[i]; ++i){
      if(inputElements[i].checked){
          flag = true;
           selectCount++;
      } 
}
	if(flag){
    var cform = document.cartform;
    cform.removeSelected.value = true;
    if(count == selectCount){
    	cform.action = "/control/RemoveALL"; 
    }
    cform.submit();
    }else{
    alert("Please select the item");
    }
}
function addToList() {
    var cform = document.cartform;
    cform.action = "<@ofbizUrl>addBulkToShoppingList</@ofbizUrl>";
    cform.submit();
}
function gwAll(e) {
    var cform = document.cartform;
    var len = cform.elements.length;
    var selectedValue = e.value;
    if (selectedValue == "") {
        return;
    }

    var cartSize = ${shoppingCartSize};
    var passed = 0;
    for (var i = 0; i < len; i++) {
        var element = cform.elements[i];
        var ename = element.name;
        var sname = ename.substring(0,16);
        if (sname == "option^GIFT_WRAP") {
            var options = element.options;
            var olen = options.length;
            var matching = -1;
            for (var x = 0; x < olen; x++) {
                var thisValue = element.options[x].value;
                if (thisValue == selectedValue) {
                    element.selectedIndex = x;
                    passed++;
                }
            }
        }
    }
    if (cartSize > passed && selectedValue != "NO^") {
        showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.EcommerceSelectedGiftWrap}");
    }
    cform.submit();
}
//]]>
</script>
	
<script type="text/javascript">
//<![CDATA[
function setAlternateGwp(field) {
  window.location=field.value;
};
//]]>
</script>
		<script>
				function showCartChangeQty(delta,id,cartindex) {
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
					updatecart(cartindex);
				}
		</script>
		<script>
				function updateCarts(id,cartindex) {
				var txtbox = document.getElementById(id);
    			var value = txtbox.value;
					qty = parseFloat(value);
					if (isNaN(qty) || (qty < 0)) {
						// document.getElementById(id).value = "0";
						return;
					} else if ((qty > 0) && (qty < 1.0)) {
						document.getElementById(id).value = "0";
						return;
					} 
					qty = Math.floor( (qty-1.0)/1.0 )*1.0  + 1.0;
					document.getElementById(id).value = qty;
					updatecart(cartindex);
				}
		</script>
<#assign fixedAssetExist = shoppingCart.containAnyWorkEffortCartItems() /> <#-- change display format when rental items exist in the shoppingcart -->
<div>
 <div class="screenlet-title-bar">
            <div class="h3">${uiLabelMap.OrderShoppingCart}</div>
            <font color="red"><div id="shoppingcarterror"></div></font>
        </div>
   
        <#--<#if ((sessionAttributes.lastViewedProducts)?has_content && sessionAttributes.lastViewedProducts?size > 0)>
          <#assign continueLink = "/product?product_id=" + sessionAttributes.lastViewedProducts.get(0) />
        <#else>
          <#assign continueLink = "/main" />
        </#if>
        <a href="<@ofbizUrl>${continueLink}</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceContinueShopping}</a>
        <#if (shoppingCartSize > 0)><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="submenutext">${uiLabelMap.OrderCheckout}</a><#else><span class="submenutextrightdisabled">${uiLabelMap.OrderCheckout}</span></#if>
        ${uiLabelMap.CommonQuickAdd}-->
        
    
    <#--div>
        <div class="tabletext">
            <form method="post" action="<@ofbizUrl>additem<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="quickaddform">
                <fieldset>
                ${uiLabelMap.EcommerceProductNumber}<input type="text" class="inputBox" name="add_product_id" value="${requestParameters.add_product_id?if_exists}" />
                <#if product?exists && product.getString("productTypeId") == "ASSET_USAGE">
                    ${uiLabelMap.EcommerceStartDate}: <input type="text" class="inputBox" size="10" name="reservStart" value=${requestParameters.reservStart?default("")} />
                    ${uiLabelMap.EcommerceLength}: <input type="text" class="inputBox" size="2" name="reservLength" value=${requestParameters.reservLength?default("")} />
                    </div>
                    <div>
                    &nbsp;&nbsp;${uiLabelMap.OrderNbrPersons}: <input type="text" class="inputBox" size="3" name="reservPersons" value=${requestParameters.reservPersons?default("1")} />
                </#if>
                ${uiLabelMap.CommonQuantity}: <input type="text" class="inputBox" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" />
                <input type="submit" class="smallSubmit" value="${uiLabelMap.OrderAddToCart}" />
                </fieldset>
            </form>
        </div>
    </div-->
</div>

<#--script type="text/javascript">
//<![CDATA[
  document.quickaddform.add_product_id.focus();
//]]>
</script-->

<div>
    <div>
        <div>
            <div class="lightbuttontextdisabled" style="float:right; height:30px; position:absolute; right:0px; top:2px">
              <#--<a href="<@ofbizUrl>main</@ofbizUrl>" class="lightbuttontext">[${uiLabelMap.EcommerceContinueShopping}]</a>-->
              <#if (shoppingCartSize > 0)>
                <#--a href="javascript:document.cartform.submit();" class="submenutext">${uiLabelMap.EcommerceRecalculateCart}</a>
                <a href="<@ofbizUrl>emptycart</@ofbizUrl>" class="submenutext">${uiLabelMap.EcommerceEmptyCart}</a-->
                <a href="javascript:removeSelected(${shoppingCartSize});" class="submenutext">${uiLabelMap.EcommerceRemoveSelected}</a> 
              <#else>
                <span class="submenutextdisabled">${uiLabelMap.EcommerceRecalculateCart}</span>
                <span class="submenutextdisabled">${uiLabelMap.EcommerceEmptyCart}</span>
                <span class="submenutextdisabled">${uiLabelMap.EcommerceRemoveSelected}</span> 
              </#if>
              <#--if (shoppingCartSize > 0)><a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="submenutextright" style="text-align:right">${uiLabelMap.OrderCheckout}</a><#else><span class="submenutextrightdisabled">${uiLabelMap.OrderCheckout}</span></#if-->
            </div>
        </div>
       
    </div>
    <div style="margin-bottom:8px;">

  <#if (shoppingCartSize > 0)>
  <div style="border:1px solid #CCCCCC;">
    <form method="post" action="<@ofbizUrl>modifycart</@ofbizUrl>" name="cartform">
      <input type="hidden" name="removeSelected" value="false" />
      <table width="100%" bgcolor="#cccccc" cellspacing="0" cellpadding="5" >
        <thead>
            <tr >
              <#--<th style="background:#FFFFFF; padding:5px"></th>-->
              <th style="background:#FFFFFF; padding:5px" scope="row">${uiLabelMap.OrderProduct}</th>
              <th style="background:#FFFFFF; padding:5px" scope="row">${uiLabelMap.OrderProduct} Description</th>
              <#--if asslGiftWraps?has_content && productStore.showCheckoutGiftOptions?if_exists != "N">>
                <th scope="row">
                  <select class="selectBox" name="GWALL" onchange="javascript:gwAll(this);">
                    <option value="">${uiLabelMap.EcommerceGiftWrapAllItems}</option>
                    <option value="NO^">${uiLabelMap.EcommerceNoGiftWrap}</option>
                    <#list allgiftWraps as option>
                      <option value="${option.productFeatureId}">${option.description} : ${option.defaultAmount?default(0)}</option>
                    </#list>
                  </select>
              <#else>
                <th scope="row">&nbsp;</th>
              </#if-->
              <#if fixedAssetExist == true><td><table><tr><td class="tabletext">- ${uiLabelMap.EcommerceStartDate} -</td><td class="tabletext">- ${uiLabelMap.EcommerceNbrOfDays} -</td></tr><tr><td class="tabletext" >- ${uiLabelMap.EcommerceNbrOfPersons} -</td><td class="tabletext" >- ${uiLabelMap.CommonQuantity} -</td></tr></table></td>
              <#else><th style="background:#FFFFFF; padding:5px" scope="row">${uiLabelMap.CommonQuantity}</th></#if>
              <th style="background:#FFFFFF; padding:5px" scope="row">${uiLabelMap.EcommerceUnitPrice}(<span class="WebRupee">&#8377;</span>)</th>
              <th style="background:#FFFFFF; padding:5px" scope="row">Discount(<span class="WebRupee">&#8377;</span>)</th>
              <th style="background:#FFFFFF; padding:5px" scope="row">Total(<span class="WebRupee">&#8377;</span>)</th>
              <th style="background:#FFFFFF; padding:5px">
              <th style="background:#FFFFFF; padding:5px" scope="row" class="showcart_All"><input type="checkbox" style="display:none;"name="selectAll" id="selectAll" value="0" onclick="javascript:toggleAll(this);" /><a href="javascript:checkCondition();">Select All</a></th>
            </tr>
        </thead>
        <tbody bgcolor="#FFFFFF">
        <#assign itemsFromList = false />
        <#assign promoItems = false />
        <#assign continueLink = "/home" />
        <#list shoppingCart.items() as cartLine>

          <#assign cartLineIndex = shoppingCart.getItemIndex(cartLine) />
        
          <#assign lineOptionalFeatures = cartLine.getOptionalProductFeatures() />
          <#-- show adjustment info -->
          <#list cartLine.getAdjustments() as cartLineAdjustment>
            <!-- cart line ${cartLineIndex} adjustment: ${cartLineAdjustment} -->
          </#list>
			
          <tr id="cartItemDisplayRow_${cartLineIndex}"  bgcolor="#FFFFFF">
            <#--td>
                <#if cartLine.getShoppingListId()?exists>
                  <#assign itemsFromList = true />
                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${cartLine.getShoppingListId()}</@ofbizUrl>" class="linktext">L</a>&nbsp;&nbsp;
                <#elseif cartLine.getIsPromo()>
                  <#assign promoItems = true />
                  <a href="<@ofbizUrl>view/showcart</@ofbizUrl>" class="button">P</a>&nbsp;&nbsp;
                <#else>
                  &nbsp;
                </#if>
            </td-->
            <td>
                  <#if cartLine.getProductId()?exists>
                    <#-- product item -->
                    <#-- start code to display a small image of the product -->
                    <#if cartLine.getParentProductId()?exists>
                      <#assign parentProductId = cartLine.getParentProductId() />
                    <#else>
                      <#assign parentProductId = cartLine.getProductId() />
                    </#if>
                    <#assign smallImageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(cartLine.getProduct(), "SMALL_IMAGE_URL", locale, dispatcher)?if_exists />
                    <#if !smallImageUrl?string?has_content><#assign smallImageUrl = "/images/defaultImage.jpg" /></#if>
                    
                    
                    <#assign productCategory = delegator.findByAnd("ProductCategoryMember",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",parentProductId))?if_exists>
				    
				    <#if productCategory?has_content>
				    	<#assign prodCategory = productCategory.get(0)?if_exists>
		                <#assign productCategoryId = prodCategory.get("productCategoryId")>
		            </#if>
                    
                    <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,cartLine.getProductId())/>
				    <#assign productUrl><@ofbizCatalogUrl productId=cartLine.getProductId() currentCategoryId=productCategoryId  previousCategoryId=crumbs/></#assign>
				    <#assign productIndexUrl = cartLine.getProductUrl()?if_exists>
                    
                    <#if smallImageUrl?string?has_content>
                      <a href="${productUrl?if_exists}${productIndexUrl?if_exists}">
                        <img src="<@ofbizContentUrl>${requestAttributes.contentPathPrefix?if_exists}${smallImageUrl}</@ofbizContentUrl>" alt="Product Image"  width="50px" height="40px" />
                      </a>
                    </#if>
              </td><td>
                    <#-- end code to display a small image of the product -->
                    <#-- ${cartLineIndex} - -->
                    <input size="6" class="inputBox" type="hidden" id="prod_${cartLineIndex}" value="${cartLine.getProductId()}" />
                                       
                    <a href="${productUrl?if_exists}${productIndexUrl?if_exists}" class="linktext"><#--${cartLine.getProductId()} --->
                    
                    <#assign brandName = cartLine.getProduct()?if_exists>
                    
                    ${brandName.brandName?if_exists} ${cartLine.getName()?if_exists}</a>
                    <#-- For configurable products, the selected options are shown -->
                    <#if cartLine.getConfigWrapper()?exists>
                      <#assign selectedOptions = cartLine.getConfigWrapper().getSelectedOptions()?if_exists />
                      <#if selectedOptions?exists>
                        <div>&nbsp;</div>
                        <#list selectedOptions as option>
                          <div>
                            ${option.getDescription()}
                          </div>
                        </#list>
                      </#if>
                    </#if>

                    <#-- if inventory is not required check to see if it is out of stock and needs to have a message shown about that... -->
                    <#--assign itemProduct = cartLine.getProduct() />
                    <#assign isStoreInventoryNotRequiredAndNotAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequiredAndAvailable(request, itemProduct, cartLine.getQuantity(), false, false) />
                    <#if isStoreInventoryNotRequiredAndNotAvailable && itemProduct.inventoryMessage?has_content>
                        (${itemProduct.inventoryMessage})
                    </#if-->

                  <#else>
                    <#-- this is a non-product item -->
                    ${cartLine.getItemTypeDescription()?if_exists}: ${cartLine.getName()?if_exists}
                  </#if>

                <#if (cartLine.getIsPromo() && cartLine.getAlternativeOptionProductIds()?has_content)>
                  <#-- Show alternate gifts if there are any... -->
                  <div class="tableheadtext">${uiLabelMap.OrderChooseFollowingForGift}:</div>
                  <select name="dummyAlternateGwpSelect${cartLineIndex}" onchange="setAlternateGwp(this);" class="selectBox">
                  <option value="">- ${uiLabelMap.OrderChooseAnotherGift} -</option>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(dispatcher, delegator, alternativeOptionProductId, requestAttributes.locale) />
                    <option value="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>">${alternativeOptionName?default(alternativeOptionProductId)}</option>
                  </#list>
                  </select>
                  <#-- this is the old way, it lists out the options and is not as nice as the drop-down
                  <ul>
                  <#list cartLine.getAlternativeOptionProductIds() as alternativeOptionProductId>
                    <#assign alternativeOptionName = Static["org.ofbiz.product.product.ProductWorker"].getGwpAlternativeOptionName(delegator, alternativeOptionProductId, requestAttributes.locale) />
                    <li class="tabletext"><a href="<@ofbizUrl>setDesiredAlternateGwpProductId?alternateGwpProductId=${alternativeOptionProductId}&alternateGwpLine=${cartLineIndex}</@ofbizUrl>" class="button">Select: ${alternativeOptionName?default(alternativeOptionProductId)}</a></li>
                  </#list>
                  </ul>
                  -->
                </#if>
            </td>

            <#-- gift wrap option -->
            <#assign showNoGiftWrapOptions = false />
            <#--td >
              <#assign giftWrapOption = lineOptionalFeatures.GIFT_WRAP?if_exists />
              <#assign selectedOption = cartLine.getAdditionalProductFeatureAndAppl("GIFT_WRAP")?if_exists />
              <#if giftWrapOption?has_content>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="NO^">${uiLabelMap.EcommerceNoGiftWrap}</option>
                  <#list giftWrapOption as option>
                    <option value="${option.productFeatureId}" <#if ((selectedOption.productFeatureId)?exists && selectedOption.productFeatureId == option.productFeatureId)>selected="selected"</#if>>${option.description} : ${option.amount?default(0)}</option>
                  </#list>
                </select>
              <#elseif showNoGiftWrapOptions>
                <select class="selectBox" name="option^GIFT_WRAP_${cartLineIndex}" onchange="javascript:document.cartform.submit()">
                  <option value="">${uiLabelMap.EcommerceNoGiftWrap}</option>
                </select>
              <#else>
                &nbsp;
              </#if>
            </td-->
            <#-- end gift wrap option -->

            <td>
                <#if cartLine.getIsPromo() || cartLine.getShoppingListId()?exists>
                       <#if fixedAssetExist == true>
                       		<#if cartLine.getReservStart()?exists>
                       				<table ><tr><td>&nbsp;</td><td class="tabletext">${cartLine.getReservStart()?string("yyyy-mm-dd")}</td><td class="tabletext">${cartLine.getReservLength()?string.number}</td></tr><tr><td>&nbsp;</td><td class="tabletext">${cartLine.getReservPersons()?string.number}</td><td class="tabletext">
                       		<#else>
                           			<table ><tr><td >--</td><td>--</td></tr><tr><td>--</td><td class="tabletext">
                       		</#if>
                        		${cartLine.getQuantity()?string.number}</td></tr></table>
                    	<#else><#-- fixedAssetExist -->
                        		<#--${cartLine.getQuantity()?string.number}-->
                        		<table width="80%" style="margin-top:20px;">
                    				<tr>
                    					<td>
											<a href="javascript:showCartChangeQty(-1.0,'update_${cartLineIndex}','${cartLineIndex}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus.png" alt=""/></a>
										</td>
                        				<td>
											<input size="6" class="inputBox" type="text" id="update_${cartLineIndex}" name="update_${cartLineIndex}" onkeyup="updateCarts('update_${cartLineIndex}','${cartLineIndex}')" value="${cartLine.getQuantity()?string.number}" />
										</td>
                        				<td>	
											<a href="javascript:showCartChangeQty(1.0,'update_${cartLineIndex}','${cartLineIndex}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add.png" alt=""/></a>
                     					</td>
                    					<td style="width:20px;">&nbsp;</td>
                    				</tr>
                    			</table> 
                     </#if>
                <#else><#-- Is Promo or Shoppinglist -->
                       <#if fixedAssetExist == true><#if cartLine.getReservStart()?exists><table><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="10" name="reservStart_${cartLineIndex}" value=${cartLine.getReservStart()?string}/></td><td><input type="text" class="inputBox" size="2" name="reservLength_${cartLineIndex}" value=${cartLine.getReservLength()?string.number}/></td></tr><tr><td>&nbsp;</td><td><input type="text" class="inputBox" size="3" name="reservPersons_${cartLineIndex}" value=${cartLine.getReservPersons()?string.number} /></td><td class="tabletext"><#else>
                           <table><tr><td>--</td><td>--</td></tr><tr><td>--</td><td class="tabletext"></#if>
                        <input size="6" class="inputBox" type="text" name="update_${cartLineIndex}" value="${cartLine.getQuantity()?string.number}" /></td></tr></table>
                        
                   
                    <#else><#-- fixedAssetExist -->
                    <table width="80%" style="margin-top:20px;">
                    <tr>
                    	<td>
                    		<a href="javascript:showCartChangeQty(-1.0,'update_${cartLineIndex}','${cartLineIndex}');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus.png" alt=""/></a>
                        </td>
                        <td>    
                        	<input size="6" class="inputBox" type="text" id="update_${cartLineIndex}" name="update_${cartLineIndex}" onkeyup="updateCarts('update_${cartLineIndex}','${cartLineIndex}')" value="${cartLine.getQuantity()?string.number}" />
                        </td>
                        <td>
                        	<a href="javascript:showCartChangeQty(1.0,'update_${cartLineIndex}','${cartLineIndex}');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add.png" alt=""/></a>
	                  
	                        <#assign ProductCategoryMaxQty = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCategoryMaxQuantity(delegator,'${productCategoryId?if_exists}')>
	                      <input size="7" type="hidden" name="updateMax${cartLineIndex}" id="updateMax${cartLineIndex}" value="${ProductCategoryMaxQty?default(0)}" />
                    	</td>
                    	<td style="width:20px;">&nbsp;</td>
                    </tr>
                    </table> 
                     <div style="padding>:5px 0 0 0">
                        <#--a href="javascript:updatecart(${cartLineIndex})"><img src="/erptheme1/update.jpg" alt="" title=""/></a-->
                        </div>
                    </#if>
                </#if>
            </td>
            <td>
            <#if cartLine.getListPrice() gt 0>
             ${cartLine.getListPrice()?if_exists}<#--<@ofbizCurrency amount=cartLine.getDisplayPrice() isoCode=shoppingCart.getCurrency()/>--></td>
             <#else>
              ${cartLine.getDisplayPrice()?if_exists}
              </#if>
          <#if cartLine.getmodifyAmount()?has_content>
	          <#assign totalmodifyAmt=cartLine.getmodifyAmount()?number*cartLine.getQuantity()?number>
	         <td><#if cartLine.isPromo>0<#else>${totalmodifyAmt?if_exists}</#if> </td>
          </#if>
            <td><#if cartLine.isPromo>0<#else>${cartLine.getDisplayItemSubTotal()?if_exists}</#if><#--<@ofbizCurrency amount=cartLine.getDisplayItemSubTotal() isoCode=shoppingCart.getCurrency()/>--></td>
            <td>
             <#if shoppingCartSize == 1>
            <#assign url = "emptycart">
            <#else>
             <#assign url = "modifycart">
            </#if>
  			<div > <a href="<@ofbizUrl>${url}</@ofbizUrl>?removeSelected=true&selectedItem=${cartLineIndex}"><img src="/erptheme1/cross-grey.png" alt=""/><span style="font-weight:normal;"> Remove</span></a></div>
  			</td><td align="center" style="text-align:center;">
            <#if !cartLine.getIsPromo()><input type="checkbox" class="text1" name="selectedItem" value="${cartLineIndex}" onclick="javascript:checkToggle(this);" /><#else>&nbsp;</#if></td></td>
          </tr>
          <tr><td colspan="8"><div class="sepratorline"></div></td></tr>
        </#list>
        <#if shoppingCart.getAdjustments()?has_content>
            <tr bgcolor="#FFFFFF">
              <td align="right" colspan="6"><div style="float:right"><b>${uiLabelMap.CommonSubTotal}&nbsp; &nbsp;&nbsp;:&nbsp;</b></div></td>
              <td aligh="right" colspan="2"><#--<@ofbizCurrency amount=shoppingCart.getDisplaySubTotal() isoCode=shoppingCart.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;${shoppingCart.getDisplaySubTotal()?if_exists}</td>
            </tr>
            <#if (shoppingCart.getDisplayTaxIncluded() > 0.0)>
              <tr bgcolor="#FFFFFF">
                <td align="right" colspan="6"><b>${uiLabelMap.OrderSalesTaxIncluded}&nbsp; &nbsp;&nbsp;:&nbsp;</b></td>
                <td aligh="right" colspan="2"><@ofbizCurrency amount=shoppingCart.getDisplayTaxIncluded() isoCode=shoppingCart.getCurrency()/></td>
              </tr>
            </#if>
            <#list shoppingCart.getAdjustments() as cartAdjustment>
              <#assign adjustmentType = cartAdjustment.getRelatedOneCache("OrderAdjustmentType") />
              <tr bgcolor="#FFFFFF">
                <td  aligh="right" colspan="6">
                <div style="float:right">
                    <b>${uiLabelMap.EcommerceAdjustment} - ${adjustmentType.get("description",locale)?if_exists} &nbsp; &nbsp;
                    <#--if cartAdjustment.productPromoId?has_content><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${cartAdjustment.productPromoId}</@ofbizUrl>" class="buttontextblue">${uiLabelMap.CommonDetails}</a></#if-->
                &nbsp;:&nbsp;</b></div></td>
                <td aligh="right" colspan="2"><#assign amount1=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal())/><#--<@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(cartAdjustment, shoppingCart.getSubTotal()) isoCode=shoppingCart.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;${amount1?if_exists}</td>
              </tr>
            </#list>
        </#if>
        <#if shoppingCart.getTotalShipping()?has_content &&  (shoppingCart.getTotalShipping() > 0.0)>
        	<tr bgcolor="#FFFFFF">
	          <td align="right" colspan="6"><div style="float:right"><b>Delivery Charge &nbsp; &nbsp;&nbsp;:&nbsp;</b></div></td>
	          <td aligh="right" colspan="2"><span class="WebRupee">&#8377;</span>&nbsp;<span id="minitotalcart">${shoppingCart.getTotalShipping()?if_exists}</span></td>
	        </tr>
        </#if>
        <tr bgcolor="#FFFFFF">
          <td align="right" colspan="6"><div style="float:right"><b>${uiLabelMap.EcommerceCartTotal} &nbsp; &nbsp;&nbsp;:&nbsp;</b></div></td>
          <td aligh="right" colspan="2"><b><#--<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;<span id="minitotalcart">${shoppingCart.getDisplayGrandTotal()?if_exists}</span></b></td>
        </tr>
        <#if itemsFromList>
        <tr bgcolor="#FFFFFF">
          <td>L - ${uiLabelMap.EcommerceItemsfromShopingList}.</td>
        </tr>
        </#if>
        <#if promoItems>
        <tr bgcolor="#FFFFFF">
          <td>P - ${uiLabelMap.EcommercePromotionalItems}.</td>
        </tr>
        </#if>
        <#if !itemsFromList && !promoItems>
        </#if>
        <input type="hidden" id="shipping_instructions" name="shipping_instructions"/>
        <tr bgcolor="#FFFFFF" style="text-align:left;">
        <td colspan="8" align="left" style="text-algn:left">
	        <table width="100%" style="margin-top:20px;">
	        	<tr>
	        		<td style="float:left; width:75%;">
				        <div style="text-algn:left;  float:left" >
					        <#--if (shoppingCartSize > 0)><a href="#" onclick="history.go(-1);return false;" class="buttontextblue">Continue Shopping</a></#if-->
					       
					        <#if (shoppingCartSize > 0)><a href="<@ofbizUrl>home</@ofbizUrl>"  class="buttontextblue checkoutbutton">Continue Shopping</a></#if>
				        </div>
				    </td>
				    <td style="width: 50%;" align="center">
				    <textarea cols="69" placeholder="Enter your instructions, if any" rows="1" wrap="hard" id="shipping_instruction" name="shipping_instruction" style="height: 25px; width:100%; margin:0px !important;margin-top: -17px !important; border:2px solid #CCC !important; " class="promoCodeinputBox"></textarea>
				    </td>
	        		<td style="float:right;">
				        <div style="text-algn:right;  float:right">
				        <#if (shoppingCartSize > 0)>
				        <#--a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="buttontextblue">Proceed to Checkout</a-->
				        <#assign total = 0>
				        <#assign total = shoppingCart.getDisplayGrandTotal()?if_exists>
				        <#assign amount1 = amount1?default(0)>
				          <#if shoppingCart.getAdjustments()?has_content>
				          <#assign total = shoppingCart.getDisplaySubTotal()?default(0)>
				          </#if>
				            <a href="javascript:formCheck(document.cartform,${total},${amount1?if_exists});" class="buttontextblue checkoutbutton">Proceed to Checkout</a>
				        </#if>
				        </div>
	        		</td>
	           </tr>
	        </table>
	    </td>
	  </tr>
    </tbody>
    </table>
        <script>
        function formCheck(form,grandTotal,adjustment){
          adjustment = adjustment * -1	;
         if(grandTotal<500 && adjustment < 500 ){
         var spanTag = document.createElement("span");
        spanTag.id = "span1";
        spanTag.className ="WebRupee";
        spanTag.innerHTML = "&#8377;";
         var temp = parseFloat(500) - parseFloat(grandTotal);
         temp = temp.toFixed(2);
         alert("Add "+spanTag.innerHTML +  temp+" more to the cart to checkout");
        }else{
       document.getElementById("shipping_instructions").value =   document.getElementById("shipping_instruction").value;
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
        }
 }
        </script>
        
        <#--tr>
          <td>
              <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
              <select name="shoppingListId" class="selectBox">
                <#if shoppingLists?has_content>
                  <#list shoppingLists as shoppingList>
                    <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                  </#list>
                </#if>
                <option value="">---</option>
                <option value="">${uiLabelMap.OrderNewShoppingList}</option>
              </select>
              &nbsp;&nbsp;
              <a href="javascript:addToList();" class="button">${uiLabelMap.EcommerceAddSelectedtoList}</a>&nbsp;&nbsp;
              <#else>
               ${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/showcart</@ofbizUrl>" class="button">${uiLabelMap.CommonBeLogged}</a>
                ${uiLabelMap.OrderToAddSelectedItemsToShoppingList}.&nbsp;
              </#if>
          </td>
        </tr-->
        <#--tr>
          <td>
              <#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
              &nbsp;&nbsp;
              <a href="<@ofbizUrl>createCustRequestFromCart</@ofbizUrl>" class="button">${uiLabelMap.OrderCreateCustRequestFromCart}</a>&nbsp;&nbsp;
              &nbsp;&nbsp;
              <a href="<@ofbizUrl>createQuoteFromCart</@ofbizUrl>" class="button">${uiLabelMap.OrderCreateQuoteFromCart}</a>&nbsp;&nbsp;
              <#else>
               ${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/showcart</@ofbizUrl>" class="button">${uiLabelMap.CommonBeLogged}</a>
                ${uiLabelMap.EcommerceToOrderCreateCustRequestFromCart}.&nbsp;
              </#if>
          </td>
        </tr-->
        <#--tr>
          <td>
            <input type="checkbox" onclick="javascript:document.cartform.submit()" name="alwaysShowcart" <#if shoppingCart.viewCartOnAdd()>checked="checked"</#if>/>${uiLabelMap.EcommerceAlwaysViewCartAfterAddingAnItem}.
          </td>
        </tr-->
    </form>
    </div>
  <#else>
    <h2>${uiLabelMap.EcommerceYourShoppingCartEmpty}.</h2>
  </#if>
<#-- Copy link bar to bottom to include a link bar at the bottom too -->
    </div>
</div>
 <#if (shoppingCartSize > 0)>
<div><link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">
	 <div class="screenlet-title-bar promoCodeNewClass">
            <div><i class="fa fa-tags"></i> ${uiLabelMap.ProductPromoCodes}</div>
    </div>
    <div>
        <div class="tabletext" style="border:1px solid #CCCCCC; padding:8px;">
            <form method="post" action="<@ofbizUrl>addpromocode<#if requestAttributes._CURRENT_VIEW_?has_content>/showcart</#if></@ofbizUrl>" name="addpromocodeform">
                <input type="text" class="promoCodeinputBox" size="15" name="productPromoCodeId" value="" />
                <input type="submit" class="promoCodeinputButton" value="${uiLabelMap.OrderAddCode}" />
            </form>
            <#assign productPromoCodeIds = (shoppingCart.getProductPromoCodesEntered())?if_exists />
                <#if productPromoCodeIds?has_content>
                    
                    <ul>
                    <#list productPromoCodeIds as productPromoCodeId>
                        <li style="text-align:center;">${uiLabelMap.ProductPromoCodesEntered} ${productPromoCodeId}</li>
                    </#list>
                    </ul>
                </#if>
        </div>
    </div>
</div>

<#--<div>
	 <div class="screenlet-title-bar">
            <div class="h3">Special Instructions</div>
    </div>
    <div>
    	  <div class="tabletext" style="border:1px solid #CCCCCC; padding:8px;">
	 
	                    <textarea cols="30" rows="3" wrap="hard" id="shipping_instruction" name="shipping_instruction"></textarea> 
    </div>
</div>-->
</#if>
<#--
<#if showPromoText?exists && showPromoText>
<div>
    <div>
        <h2>${uiLabelMap.OrderSpecialOffers}</h2>
    </div>
    <div>
        <ul>
        <#list productPromos as productPromo>
            <li class="tabletext"><a href="<@ofbizUrl>showPromotionDetails?productPromoId=${productPromo.productPromoId}</@ofbizUrl>" class="linktext">[${uiLabelMap.CommonDetails}]</a>${StringUtil.wrapString(productPromo.promoText?if_exists)}</li>
        </#list>
        </ul>
        <div class="tabletext"><a href="<@ofbizUrl>showAllPromotions</@ofbizUrl>" class="button">${uiLabelMap.OrderViewAllPromotions}</a></div>
    </div>
</div>
</#if>

<#if associatedProducts?has_content>
<div>
    <div>
        <h2>${uiLabelMap.EcommerceYouMightAlsoIntrested}:</h2>
    </div>
    <div>
        <#list associatedProducts as assocProduct>
            <div>
                ${setRequestAttribute("optProduct", assocProduct)}
                ${setRequestAttribute("listIndex", assocProduct_index)}
                ${screens.render("component://ecommerce/widget/CatalogScreens.xml#productsummary")}
            </div>
        </#list>
    </div>
</div>
</#if>

<#if (shoppingCartSize?default(0) > 0)>
  ${screens.render("component://ecommerce/widget/CartScreens.xml#promoUseDetailsInline")}
</#if>
-->

<!-- Internal cart info: productStoreId=${shoppingCart.getProductStoreId()?if_exists} locale=${shoppingCart.getLocale()?if_exists} currencyUom=${shoppingCart.getCurrency()?if_exists} userLoginId=${(shoppingCart.getUserLogin().getString("userLoginId"))?if_exists} autoUserLogin=${(shoppingCart.getAutoUserLogin().getString("userLoginId"))?if_exists} -->
<#--if associatedProducts?has_content>
<div>
    <div>
        <h2>${uiLabelMap.EcommerceYouMightAlsoIntrested}:</h2>
    </div>
    <div>
        <#-- random complementary products -->
        <#--list associatedProducts as assocProduct>
            <div>
                ${setRequestAttribute("optProduct", assocProduct)}
                ${setRequestAttribute("listIndex", assocProduct_index)}
                ${screens.render("component://ecommerce/widget/CatalogScreens.xml#productsummary")}
            </div>
        </#list>
    </div>
</div>
</#if-->

		<script type="text/javascript">
//<![CDATA[
   
   

    function additemSubmit(){
        <#if product?has_content && product.productTypeId?if_exists == "ASSET_USAGE">
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
        <#if product?has_content && product.productTypeId?if_exists == "ASSET_USAGE">
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

    <#if product?has_content && product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
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
<script>
function getList(name, index, src, formName) {

        currentFeatureIndex = findIndex(name);

        if (currentFeatureIndex == 0) {
            // set the images for the first selection
            if (IMG[index] != null) {
                if (document.images['mainImage'] != null) {
                    document.images['mainImage'].src = IMG[index];
                    detailImageUrl = DET[index];
                }
            }

            // set the drop down index for swatch selection
            document.forms['addform'+formName].elements[name].selectedIndex = (index*1)+1;
        }

        if (currentFeatureIndex < (OPT.length-1)) {
            // eval the next list if there are more
            var selectedValue = document.forms['addform'+formName].elements[name].options[(index*1)+1].value;
            if (index == -1) {
              <#if featureOrderFirst?exists>
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
                var Variable1 = eval("list"+formName+ OPT[(currentFeatureIndex+1)] + selectedValue + "()");
            }
            // set the product ID to NULL to trigger the alerts
            setAddProductId('NULL');
            // set the variant price to NULL
            setVariantPrice('NULL');
        } else {
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms['addform'+formName].elements[name].selectedIndex;

            // using the selected index locate the sku
            var sku = document.forms['addform'+formName].elements[name].options[indexSelected].value;
            
            // display alternative packaging dropdown
            ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId(sku,formName);

            // set the variant price
            setVariantPrice(sku,formName);

            // check for amount box
            toggleAmt(checkAmtReq(sku));
        }
    }
    
    function toggleAmt(toggle) {
        if (toggle == 'Y') {
            changeObjectVisibility("add_amount", "visible");
        }

        if (toggle == 'N') {
            changeObjectVisibility("add_amount", "hidden");
        }
    }
    
     var detailImageUrl = null;
    function setAddProductId(name,formName) {
   
        document.forms['addform'+formName].add_product_id.value = name;
        if (document.forms['addform'+formName].quantity == null) return;
        if (name == '' || name == 'NULL' || isVirtual(name) == true) {
            document.forms['addform'+formName].quantity.disabled = true;
            var elem = document.getElementById('product_id_display'+formName);
            var txt = document.createTextNode('');
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        } else {
            document.forms['addform'+formName].quantity.disabled = false;
            var elem = document.getElementById('product_id_display'+formName);
            var txt = document.createTextNode(name);
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
    }
    function setVariantPrice(sku,formName) {
        if (sku == '' || sku == 'NULL' || isVirtual(sku) == true) {
            var elem = document.getElementById('variant_price_display'+formName);
            var txt = document.createTextNode('');
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
        else {
            var elem = document.getElementById('variant_price_display'+formName);
            var abc = "getVariantPrice"+formName;
            var price = window[abc](sku);
            var txt = document.createTextNode(price);
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
    }
    function addItems(add_product_id, formName, productName) { alert("showcart");
	var value= document.getElementById(add_product_id).value;
            if(value == 'NULL' || value == "")
            {
            alert("Please select pack size");
            
            }else{
		addItem(add_product_id, productName);
}
    }
    function addItem(prodId,productName) { alert("showcart1");
    var id= prodId.add_product_id.value;
    //invialert(id);
    var add_product_id = null;
    var quantity = null;
    var clearSearch = null;
    add_product_id= id;
    quantity= document.getElementById("qty_"+id).value;
    if( quantity > 20)
    {
    alert("Please select a quantity less than 20");
    }
    else{
    clearSearch= "N";
          if (add_product_id == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       } else {
           var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity;
                      jQuery.ajax({url: '/control/additem',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
          {
          	 alert(data);
	         return;
          }
          $('#minicart').html(data);
           jQuery.ajax({url: '<@ofbizUrl>findprodwgt</@ofbizUrl>',
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	              document.getElementById('outputs').innerHTML = data;
	              ShowDialog1(false);
	         },
        error: function(data) {
        }
    	});
    	//setTimeout(function() {
      //location.reload();
    //}, 1000);
         },
          complete:  function() { 
         
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
    }}
    }
    
     function ShowDialog(modal)
   {
      /* $("#overlay").show();
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
    */
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    cartsummary1();
    
   }
   
    function ShowDialog1(modal)
   {
      

      setTimeout(function() {
       $('#cartsummary1').show();
		 $('#inbulkorder').hide();
        
    }, 4000);
    
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    cartsummary1();
    
   }
 function HideDialog()
   {
      $("#overlay").hide();
      $("#dialog").fadeOut(300);
   }
   
     function displayProductVirtualVariantId(variantId) {
        if(variantId){
            document.addform.product_id.value = variantId;
        }else{
            document.addform.product_id.value = '';
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
    
     function isVirtual(product) {
        var isVirtual = false;
        <#if virtualJavaScript?exists>
        for (i = 0; i < VIR.length; i++) {
            if (VIR[i] == product) {
                isVirtual = true;
            }
        }
        </#if>
        return isVirtual;
    }
    
    

	

    function popupDetail(specificDetailImageUrl) {
        if( specificDetailImageUrl ) {
            detailImageUrl = specificDetailImageUrl;
        }
        else {
            var defaultDetailImage = "${firstDetailImage?default(mainDetailImageUrl?default("_NONE_"))}";
            if (defaultDetailImage == null || defaultDetailImage == "null" || defaultDetailImage == "") {
               defaultDetailImage = "_NONE_";
            }

            if (detailImageUrl == null || detailImageUrl == "null") {
                detailImageUrl = defaultDetailImage;
            }
        }

        if (detailImageUrl == "_NONE_") {
            hack = document.createElement('span');
            hack.innerHTML="${uiLabelMap.CommonNoDetailImageAvailableToDisplay}";
            showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonNoDetailImageAvailableToDisplay}");
            return;
        }
        detailImageUrl = detailImageUrl.replace(/\&\#47;/g, "/");
        popUp("<@ofbizUrl>detailImage?detail=" + detailImageUrl + "</@ofbizUrl>", 'detailImage', '600', '600');
    }

    

    function findIndex(name) {
        for (i = 0; i < OPT.length; i++) {
            if (OPT[i] == name) {
                return i;
            }
        }
        return -1;
    }

	
    
    function validate(x){
        var msg=new Array();
        msg[0]="Please use correct date format [yyyy-mm-dd]";

        var y=x.split("-");
        if(y.length!=3){ showAlert(msg[0]);return false; }
        if((y[2].length>2)||(parseInt(y[2])>31)) { showAlert(msg[0]); return false; }
        if(y[2].length==1){ y[2]="0"+y[2]; }
        if((y[1].length>2)||(parseInt(y[1])>12)){ showAlert(msg[0]); return false; }
        if(y[1].length==1){ y[1]="0"+y[1]; }
        if(y[0].length>4){ showAlert(msg[0]); return false; }
        if(y[0].length<4) {
            if(y[0].length==2) {
                y[0]="20"+y[0];
            } else {
                showAlert(msg[0]);
                return false;
            }
        }
        return (y[0]+"-"+y[1]+"-"+y[2]);
    }

    function showAlert(msg){
        showErrorAlert("${uiLabelMap.CommonErrorMessage2}", msg);
    }
</script>
		
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
</div>
</div>