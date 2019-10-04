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
    function addItems(add_product_id, formName) {
	var value= document.getElementById(add_product_id).value;
            if(value == 'NULL' || value == "")
            {
            alert("Please select pack size");
            
            }else{
		addItem(formName);
}
    }
    function addItem(formName) {
    var add_product_id= formName.add_product_id.value;
    var quantity= formName.quantity.value;
    var clearSearch= formName.clearSearch.value;
       if (formName.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       } else {

          
           var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity;
                       //alert(param);
                      jQuery.ajax({url: '<@ofbizUrl>additem/showcart</@ofbizUrl>',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
         {
             var flag_new = true;
          	 var newFlag = checkForGiftCard(data,formName);
          	 if(!newFlag)return;
          	 
          	 if(data == "Can't add more than giftcard")return;
          	 
          	 alert(data);
	         return;
          }
          $('#minicart').html(data);  
         },
         complete:  function() { 
         var minitotal = document.getElementById('minitotal').value;
          var miniquantity = document.getElementById('miniquantity').value;
           $('#microCartTotal').text(minitotal);
           $('#microCartQuantity').text(miniquantity);
         },
        error: function(data) {
        }
    	});
         
       }
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


<div style=" position:relative">
<h1 style="background:#9aae27; padding:14px 5px 14px 5px; margin:2px">${request.getParameter("BRAND_NAME")} Brand's Product, <span class="h2">${uiLabelMap.ProductYouSearchedFor}:</span></h1>


<br />
<div>
<#--#list searchConstraintStrings as searchConstraintString>
    <span><a href="<@ofbizUrl>keywordsearch?removeConstraint=${searchConstraintString_index}&amp;clearSearch=N</@ofbizUrl>" class="buttontext">X</a>&nbsp;${searchConstraintString}</span>
</#list-->
</div>
<br />
<#--div>${uiLabelMap.CommonSortedBy}: ${searchSortOrderString}</div-->

<#--div><a href="<@ofbizUrl>advancedsearch?SEARCH_CATEGORY_ID=${(requestParameters.SEARCH_CATEGORY_ID)?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRefineSearch}</a></div-->

<#if !productIds?has_content>
  <h2>&nbsp;${uiLabelMap.ProductNoResultsFound}.</h2>
</#if>
</div>
<#if productIds?has_content>
    <div class="product-prevnext">
        <#-- Start Page Select Drop-Down -->
        <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
        <select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;">
          <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int + 1} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
          <#list 0..viewIndexMax as curViewNum>
            <option value="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int}/~clearSearch=N</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
          </#list>
        </select>
        <#-- End Page Select Drop-Down -->
        <b>
        <#if (viewIndex?int > 0)>
          <a href="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
        </#if>
        <#if (listSize?int > 0)>
          <span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex+1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
        </#if>
        </b>
    </div>
</#if>

<#if productIds?has_content>
	 <#assign numCol = numCol?default(1)>
      <#assign numCol = numCol?number>
      <#assign tabCol = 1>
    <div class="productsummary-container <#if (numCol?int > 1)> matrix</#if>">
    	 <#if (numCol?int > 1)>
	        <table>
	      </#if>
        <#list productIds as productId> <#-- note that there is no boundary range because that is being done before the list is put in the content -->
           <#if (numCol?int == 1)>
            ${setRequestAttribute("optProductId", productId)}
            ${setRequestAttribute("listIndex", productId_index)}
            ${screens.render(productsummaryScreen)}
            <#else>
              <#if (tabCol?int = 1)><tr></#if>
                  <td>
                   ${setRequestAttribute("optProductId", productId)}
		            ${setRequestAttribute("listIndex", productId_index)}
		            ${screens.render(productsummaryScreen)}
                   </td>
              <#if (tabCol?int = numCol)></tr></#if>
              <#assign tabCol = tabCol+1><#if (tabCol?int > numCol)><#assign tabCol = 1></#if>
           </#if>
        </#list>
        <#if (numCol?int > 1)>
        </table>
      </#if>
    </div>
</#if>

<#if productIds?has_content>
    <div class="product-prevnext">
        <#-- Start Page Select Drop-Down -->
        <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
        <select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;">
          <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int + 1} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
          <#list 0..viewIndexMax as curViewNum>
            <option value="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int}/~clearSearch=N</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
          </#list>
        </select>
        <#-- End Page Select Drop-Down -->
        <b>
        <#if (viewIndex?int > 0)>
          <a href="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
        </#if>
        <#if (listSize?int > 0)>
          <span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>keywordsearch/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex+1}/~clearSearch=N</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
        </#if>
        </b>
    </div>
</#if>
