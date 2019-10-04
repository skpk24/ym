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
${virtualJavaScript}

<script>
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
                                  if(qty==0)
                                  {
                                 document.getElementById(id).value = 1;
                                 }
                                 else
                                  document.getElementById(id).value = qty;
                           }
function getList(name, index, src, formName) {

var AA= window["IMG"+formName];
        currentFeatureIndex = findIndex(name);
        var ABC =document.getElementById('ABlink'+formName).href;
  	 var data = ABC.split("?")
  	 ABC = data[0];
  	// alert(data[0])
 		ABC=ABC+"?name="+name+"&index="+index+"&src="+src;
 	
 	//alert(ABC);
 	document.getElementById('ABlink'+formName).href =ABC;
 	document.getElementById('desclink'+formName).href =ABC;
 	 	 document.getElementById('rating_'+formName).href =ABC;
 	

         if (currentFeatureIndex == 0) {
            // set the images for the first selection
         if (AA[index] != null){
                if (document.images['A'+formName] != null) {
               
                    document.images['A'+formName].src = AA[index];
                  
                   // detailImageUrl = DET[index];
                }
         
	}

            // set the drop down index for swatch selection
            document.forms['addform'+formName].elements[name].selectedIndex = (index*1);
        }

        if (currentFeatureIndex < (OPT.length-1)) {
            // eval the next list if there are more
            var selectedValue = document.forms['addform'+formName].elements[name].options[(index*1)].value;
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
            var skuName = document.forms['addform'+formName].elements[name].options[indexSelected].text;
             checkInventory(sku, formName);
            // display alternative packaging dropdown
            //ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            
            
            // set the product ID
             
            setAddProductId(sku,formName);
				 
            // set the variant price
            try
 			 {
            setVariantPrice(sku,formName);
            setAddProductNames(skuName,sku,formName);
            }
            catch(err)
            {
        	setAddProductNames(skuName,sku,formName);
            }

            // check for amount box
           // toggleAmt(checkAmtReq(sku));
        }
    }
    
    function checkInventory(prodId,formName){
  
    		var data=document.getElementById("Inventory"+prodId).value;
    		
    	if(data!=null && data!="")
    	{
    	if(parseInt(data)>0){
    	  //alert("data"+data);
    	document.getElementById("addstock"+formName).style.display="block";
        document.getElementById("outstock"+formName).style.display="none";
        			
        		}else{
        		document.getElementById("addstock"+formName).style.display="none";
        		document.getElementById("outstock"+formName).style.display="block";
        			
        		}
        		}
        else{
        		document.getElementById("addstock"+formName).style.display="none";
        		document.getElementById("outstock"+formName).style.display="block";
        			
        		}
        
    }
    
    
     function setAddProductNames(skuName,sku,formName)
    {
   
	  
         data = skuName;
         
         var proNameDef = document.getElementById('product_id_display'+formName);
         var elem = document.getElementById('variant_product_name'+formName);
    
         if(data != ''){
          
            	var txt = document.createTextNode(data);
	            if(elem.hasChildNodes()) {
	                elem.replaceChild(txt, elem.firstChild);
	                 proNameDef.style.display = 'none';
	                 document.getElementById('product_id_display1'+formName).style.display = 'none';
	                elem.style.display = 'block';
	            }
	            else {
	                elem.appendChild(txt);
	                proNameDef.style.display = 'none';
	                document.getElementById('product_id_display1'+formName).style.display = 'none';
	                elem.style.display = 'block';
	            }
            }else{
            document.getElementById('product_id_display1'+formName).style.display = 'block';
            proNameDef.style.display = 'none';
            elem.style.display = 'none';
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
 document.getElementById("tempSave"+formName).style.display="none";
 		document.getElementById("tempListPrice"+formName).style.display="none";
  		document.getElementById("tempDefault"+formName).style.display="none";
        var  param = 'productId=' + sku;
   jQuery.ajax({url: "/control/getVariantPrice",
		  type: "POST",
		 
		   data: param,
		  dataType: "json",
		  success: function() {
		  },
		  error: function() {
		  },
		  complete:function(xml){
		var data = xml.responseText;
		 var myObject = eval('(' + data + ')');
        	
         document.getElementById("detailprice"+formName).style.display="none";
          if(myObject.result.listPrice!=null && myObject.result.listPrice!="" && myObject.result.price!=null && price!="" && myObject.result.listPrice>myObject.result.price)
         {
        document.getElementById("tempListPrice"+formName).style.display="block";
          var listprice= (Math.round((myObject.result.listPrice*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
          document.getElementById("tempList"+formName).innerHTML=listprice;
          }
         if(myObject.result.price!=null && price!="" )
         {
         document.getElementById("tempDefault"+formName).style.display="inline-block";
        var price= (Math.round((myObject.result.price*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
         document.getElementById("temdefault"+formName).innerHTML=price;
         
         }
         if(myObject.result.listPrice!=null && myObject.result.listPrice!="" && myObject.result.price!=null && price!="" && myObject.result.listPrice>myObject.result.price)
            {
          	document.getElementById("tempSave"+formName).style.display="inline-block";
            var discount=myObject.result.listPrice-myObject.result.price;
            var percentSaved = Math.round((discount / myObject.result.listPrice) * 100);
            var discountPrice= (Math.round((discount*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
          	 document.getElementById("tempSavePrice"+formName).innerHTML=discountPrice;
             newNode=document.getElementById("tempDiscount"+formName);
            	if(percentSaved!=null && percentSaved!="")
          	newNode.className = "discount-"+percentSaved+" discountmain";
            
            }
         
        }
		});
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
                      jQuery.ajax({url: '/control/additem',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
          {
             var flag_new = true;
          	 var newFlag = checkForGiftCard(data,formName);
          	 if(!newFlag)return;
          	 
          	 if(data == "Can not add more than giftcard")return;
          	 
          	 alert(data);
	         return;
          }
          $('#minicart').html(data);  
          jQuery.ajax({url: '/control/findprodwgt',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         document.getElementById('outputs').innerHTML = data;
         ShowDialog1(false);
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
         },
        error: function(data) {
        }
    	});
         
       }
    }
    
    function ShowDialog(modal)
   {
      /*$("#overlay").show();
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
    
      function showcom(pId){
		document.getElementById(pId).style.display="block";
	}
	function hidecom(pId){
		document.getElementById(pId).style.display="none";
	}
</script>
<script>
	function favBrowsers(){
		document.sortBy.submit();
	}
</script>

<div style=" position:relative;">
   <#if productIds?has_content && (listSize?int != 1)>
        <form style="float:left;" action="<@ofbizUrl>topBrandProducts</@ofbizUrl>" name="sortBy">
	        <input type="hidden" name="VIEW_SIZE" value="${viewSize}"/>
	        <input type="hidden" name="SEARCH_STRING" value="${searchString?if_exists}"/>
	        <input type="hidden" name="filterBy" id="filterBy" value="${parameters.filterBy?if_exists}"/>
	        <input type="hidden" name="filterByBrand" id="filterByBrand" value="${parameters.filterByBrand?if_exists}"/>
	        <input type="hidden" name="filterByCategory" id="filterByCategory" value="${parameters.filterByCategory?if_exists}"/>
	        <input type="hidden" name="VIEW_INDEX" value="${viewIndex}"/>
	        <input type="hidden" name="excludeOutOfStock" value="${excludeOutOfStock?if_exists}"/>
			<select id="mySelect" onchange="favBrowsers()" name="sortBy">
			  <option value="">All</option>
			  <option value="L_TO_H" <#if sortBy?has_content && sortBy = "L_TO_H">selected</#if>>Price Low To High</option>
			  <option value="H_TO_L" <#if sortBy?has_content && sortBy = "H_TO_L">selected</#if>>Price High To Low</option>
			  <option value="A_TO_Z" <#if sortBy?has_content && sortBy = "A_TO_Z">selected</#if>>A To Z</option>
			  <option value="Z_TO_A" <#if sortBy?has_content && sortBy = "Z_TO_A">selected</#if>>Z To A</option>
			  <option value="POPULAR_PRD" <#if sortBy?has_content && sortBy = "POPULAR_PRD">selected</#if>>Popularity</option>
			</select>
		</form>
	<#else>
		<form style="float:left;" action="<@ofbizUrl>topBrandProducts</@ofbizUrl>" name="sortBy">
	        <input type="hidden" name="VIEW_SIZE" value="${viewSize?if_exists}"/>
	        <input type="hidden" name="SEARCH_STRING" value="${searchString?if_exists}"/>
	        <input type="hidden" name="filterBy" id="filterBy" value="${parameters.filterBy?if_exists}"/>
	        <input type="hidden" name="filterByBrand" id="filterByBrand" value="${parameters.filterByBrand?if_exists}"/>
	        <input type="hidden" name="filterByCategory" id="filterByCategory" value="${parameters.filterByCategory?if_exists}"/>
	        <input type="hidden" name="VIEW_INDEX" value="${viewIndex?if_exists}"/>
	        <input type="hidden" name="excludeOutOfStock" value="${excludeOutOfStock?if_exists}"/>
	        <input type="hidden" name="sortBy" value="${sortBy?if_exists}"/>
		</form>
	</#if>
	
<div>

<#--#list searchConstraintStrings as searchConstraintString>
    <span><a href="<@ofbizUrl>topBrandProducts?removeConstraint=${searchConstraintString_index}&amp;clearSearch=N</@ofbizUrl>" class="buttontext">X</a>&nbsp;${searchConstraintString}</span>
</#list-->
</div>

<#--div>${uiLabelMap.CommonSortedBy}: ${searchSortOrderString}</div-->

<#--div><a href="<@ofbizUrl>advancedsearch?SEARCH_CATEGORY_ID=${(requestParameters.SEARCH_CATEGORY_ID)?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRefineSearch}</a></div-->

</div>
<#if productIds?has_content >
    <div class="product-prevnext" style="margin-top:0px !important;">
        <#-- Start Page Select Drop-Down -->
    
        <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
               <#if (viewIndexMax?int > 0)>
        <select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;">
          <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int + 1} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
          <#list 0..viewIndexMax as curViewNum>
            <option value="<@ofbizUrl>topBrandProducts?SEARCH_STRING=${searchString?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${curViewNum?int}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
          </#list>
        </select>
        <#-- End Page Select Drop-Down -->
        <b>
        <#if (viewIndex?int > 0)>
          <a href="<@ofbizUrl>topBrandProducts?SEARCH_STRING=${searchString?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int - 1}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
        </#if>
        <#if (listSize?int > 0)>
          <span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>topBrandProducts?SEARCH_STRING=${searchString?if_exists}&VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
        </#if>
        </b>
         <#else>
    	<span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
    </#if>
    </div>
</#if>
	
<#if filter?has_content || filterCategory?has_content || filterByBrand?has_content || (excludeOutOfStock?has_content  && excludeOutOfStock == "Y")>
<div style=" position:relative" class="bb-filter-bar">
    <span class="filter-head">Applied Selection
	<a href="javascript:clearAll();" class="linktext1233">Clear All</a></span><br/>
</#if>
	   <#if filter?has_content>
	   		<#list filter as filt>
	   		  <p class="filter-block">
	   			${filt?if_exists}
		   			<a href='javascript:unCheckFilter("${filt?if_exists}");' class="linktext1233">
		   				<img src="/erptheme1/close.png">
		   			</a>
	   		    </p>
	   		</#list>
	   </#if>
	   <#if filterByBrand?has_content>
	   		<#list filterByBrand as filterBrand>
	   		  <p class="filter-block">
	   			${filterBrand?if_exists}
		   			<a href='javascript:unCheckFilter("${filterBrand?if_exists}");' class="linktext1233">
		   				<img src="/erptheme1/close.png">
		   			</a>
	   		    </p>
	   		</#list>
	   </#if>
	   <#if filterCategory?has_content>
	   		<#list filterCategory as filtCategory>
	   			<#assign categoryName = Static["org.ofbiz.product.category.CategoryWorker"].getCategoryName(delegator, filtCategory, true)/>
	   			<p class="filter-block">
		   			${categoryName?if_exists}
		   			<a href="javascript:unCheckFilter('${filtCategory?if_exists}');" class="linktext1233">
		   				<img src="/erptheme1/close.png">
		   			</a>
	   			</p>
	   		</#list>
	   </#if>
	   <#if excludeOutOfStock?has_content && excludeOutOfStock == "Y">
	   		  <p class="filter-block">
	   			Exclude out of stock: Yes
		   			<a href="javascript:unCheckFilter('excludeOutOfStockCheckbox');" class="linktext1233">
		   				<img src="/erptheme1/close.png">
		   			</a>
	   		    </p>
	   </#if>
<#if filter?has_content || filterCategory?has_content || filterByBrand?has_content || (excludeOutOfStock?has_content  && excludeOutOfStock == "Y")>
</div>
</#if>

<#if !productIds?has_content>
  <h2>&nbsp;${uiLabelMap.ProductNoResultsFound}.</h2>
  
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
            ${setRequestAttribute("optProductId", productId)},
            ${setRequestAttribute("listIndex", productId_index)}
            ${screens.render(productsummaryScreen)}
            <#else>
              <#if (tabCol?int = 1)><tr></#if>
                  <td class="topbrand_review">
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
<#if productIds?has_content >
    <div class="product-prevnext">
        <#-- Start Page Select Drop-Down -->
        <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
            <#if (viewIndexMax?int > 0)>
         <select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;">
          <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int + 1} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
          <#list 0..viewIndexMax as curViewNum>
            <option value="<@ofbizUrl>topBrandProducts?VIEW_SIZE=${viewSize}&VIEW_INDEX=${curViewNum?int}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
          </#list>
        </select>
        <#-- End Page Select Drop-Down -->
        <b>
        <#if (viewIndex?int > 0)>
          <a href="<@ofbizUrl>topBrandProducts?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex?int - 1}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
        </#if>
        <#if (listSize?int > 0)>
          <span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
        </#if>
        <#if highIndex?int < listSize?int>
          | <a href="<@ofbizUrl>topBrandProducts?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&clearSearch=N&sortBy=${sortBy?if_exists}&filterBy=${parameters.filterBy?if_exists}&filterByCategory=${parameters.filterByCategory?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a>
        </#if>
        </b>
         <#else>
    	<span>${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
    </#if>
    </div>
</#if>
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
	</div>		
</div>
<#if productIds?has_content && relatedProductCategoryName?has_content>
	<div style=" position:relative">
		<h1 style="background:#9aae27; padding:14px 5px 14px 5px; margin:2px">More Products from ${relatedProductCategoryName}</h1>
	<div>
</#if>
 