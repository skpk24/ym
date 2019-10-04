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
<#-- variable setup -->

       
<link href="http://netdna.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css" rel="stylesheet">    
       
<#assign price = priceMap?if_exists />
<#assign productImageList = productImageList?if_exists />
<#-- end variable setup -->
<#-- virtual product javascript -->
${virtualJavaScript?if_exists}

<script type="text/javascript">
//<![CDATA[
    var detailImageUrl = null;
    function setAddProductId(name) {
        document.addform.add_product_id.value = name;
        if (document.addform.quantity == null) return;
        if (name == '' || name == 'NULL' || isVirtual(name) == true) {
            document.addform.quantity.disabled = true;
            var elem = document.getElementById('product_id_display');
            var txt = document.createTextNode('');
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        } else {
            document.addform.quantity.disabled = false;
            //var elem = document.getElementById('product_id_display');
            //var txt = document.createTextNode(name);
            //if(elem.hasChildNodes()) {
                //elem.replaceChild(txt, elem.firstChild);
            //} 
            //else {
                //elem.appendChild(txt);
            //}
        }
    }
    function setVariantPrice(sku) {
 		document.getElementById("tempSave").style.display="none";
 		document.getElementById("tempListPrice").style.display="none";
  		document.getElementById("tempDefault").style.display="none";
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
        	
         document.getElementById("detailprice").style.display="none";
          if(myObject.result.listPrice!=null && myObject.result.listPrice!="" && myObject.result.price!=null && price!="" && myObject.result.listPrice>myObject.result.price)
         {
        document.getElementById("tempListPrice").style.display="block";
          var listprice= (Math.round((myObject.result.listPrice*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
          document.getElementById("tempList").innerHTML=listprice;
          }
         if(myObject.result.price!=null && price!="" )
         {
         document.getElementById("tempDefault").style.display="block";
        var price= (Math.round((myObject.result.price*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
         document.getElementById("temdefault").innerHTML=price;
         
         }
         if(myObject.result.listPrice!=null && myObject.result.listPrice!="" && myObject.result.price!=null && price!="" && myObject.result.listPrice>myObject.result.price)
            {
          	document.getElementById("tempSave").style.display="block";
            var discount=myObject.result.listPrice-myObject.result.price;
            var percentSaved = Math.round((discount / myObject.result.listPrice) * 100);
           var discountPrice= (Math.round((discount*Math.pow(10,2)).toFixed(1))/Math.pow(10,2)).toFixed(2);
           document.getElementById("tempSavePrice").innerHTML=discountPrice;
            newNode=document.getElementById("tempDiscount");
            	if(percentSaved!=null && percentSaved!="")
          	newNode.className = "discount-"+percentSaved+" discountmain";
            }
         
        }
		});
       
     
    }
    
    function setAddProductNames(sku)
    {
	    var  param = 'productId=' + sku;
                      jQuery.ajax({url: "/control/getProductNames",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         var proName = document.getElementById('variant_product_name');
         var proNameDef = document.getElementById('variant_product_default');
         
         var breadcrumbsProName = document.getElementById('breadcrumbsVariant_product_name');
         var breadcrumbsProNameDef = document.getElementById('breadcrumbsVariant_product_default');
         breadcrumbsProName.innerHTML = "";
         breadcrumbsProNameDef.innerHTML = "";
         if(data != ''){
         var txt = document.createTextNode(data);
         
         
         if(proName.hasChildNodes()) {
                proName.replaceChild(txt, proName.firstChild);
                proNameDef.style.display = 'none';
                proName.style.display = 'block';
                
                breadcrumbsProName.innerHTML = data;
                
            } else {
                proName.appendChild(txt);
                proNameDef.style.display = 'none';
                proName.style.display = 'block';
                
                breadcrumbsProName.innerHTML = data;
                
            }
            }else{
            proNameDef.style.display = 'block';
            proName.style.display = 'none';
            }
         }
    });
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
    function addItemOld() {
       if (document.addform.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       } else {

           if (isVirtual(document.addform.add_product_id.value)) {
               document.location = '<@ofbizUrl>product?category_id=${categoryId?if_exists}&amp;product_id=</@ofbizUrl>' + document.addform.add_product_id.value;
               return;
           } else {
               document.addform.submit();
           }
       }
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

    function toggleAmt(toggle) {
        if (toggle == 'Y') {
            changeObjectVisibility("add_amount", "visible");
        }

        if (toggle == 'N') {
            changeObjectVisibility("add_amount", "hidden");
        }
    }

    function findIndex(name) {
        for (i = 0; i < OPT.length; i++) {
            if (OPT[i] == name) {
                return i;
            }
        }
        return -1;
    }
    
   
    

    function getList(name, index, src) {
 		//alert("ssssname===="+name+"\n\n index==="+index+"\n\n src===="+src);
        currentFeatureIndex = findIndex(name);
        if (currentFeatureIndex == 0) {
            // set the images for the first selection
            if (IMG[index] != null) {
            
          
                if (document.images['mainImage'] != null) {
                    document.images['mainImage'].src = IMG[index];
                    document.getElementById("Zoomer").class="";
                    document.getElementById("Zoomer").class="MagicZoomPlus";
                    document.getElementById("Zoomer").setAttribute("href", DET[index]);
                    MagicZoomPlus.refresh("Zoomer");
                   //alert(DET[index]);
                }
            }

            // set the drop down index for swatch selection
            document.forms["addform"].elements[name].selectedIndex = (index*1);
            
        }
        if (currentFeatureIndex < (OPT.length-1)) {
            // eval the next list if there are more
            var selectedValue = document.forms["addform"].elements[name].options[(index*1)].value;
            if (index == -1) {
              <#if featureOrderFirst?exists>
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
            }
            // set the product ID to NULL to trigger the alerts
            setAddProductId('NULL');
            // set the variant price to NULL
            setVariantPrice('NULL');
        } else {
        
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms["addform"].elements[name].selectedIndex;
            // using the selected index locate the sku
            var sku = document.forms["addform"].elements[name].options[indexSelected].value;
            // display alternative packaging dropdown
            ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId(sku);

            // set the variant price
            try{
            setAddProductNames(sku);
          }
          catch(err){
          }
            setVariantPrice(sku);
            var giftcardSplit = sku.split("-");
            if(giftcardSplit.length >=1 && giftcardSplit[0] != "GIFTCARD")
            {
            	checkInventory(sku);
            }
			
            // check for amount box
            toggleAmt(checkAmtReq(sku));
        }
    }
    
    function checkInventory(prodId){
 
	   var data=document.getElementById("Inventory"+prodId).value;
	   
    	if(data!=null && data!="")
    	{
    	if(parseInt(data)>0){
    	  
        			document.getElementById("addstockP${product.productId?if_exists}").style.display="block";
        			document.getElementById("outstockP${product.productId?if_exists}").style.display="none";
        			
        		}else{
        			document.getElementById("addstockP${product.productId?if_exists}").style.display="none";
        			document.getElementById("outstockP${product.productId?if_exists}").style.display="block";
        			
        		}
         
       }  
    }
    
   
    function getListfirst(name, index, src) {
		//alert("name===="+name+"\n\n index==="+index+"\n\n src===="+src);
        currentFeatureIndex = findIndex(name);
        if (currentFeatureIndex == 0) {
            // set the images for the first selection
            if (IMG[index] != null) {
            
          
                if (document.images['mainImage'] != null) {
                    document.images['mainImage'].src = IMG[index];
                   
                    document.getElementById("Zoomer").class="";
                    document.getElementById("Zoomer").class="MagicZoomPlus";
                    document.getElementById("Zoomer").setAttribute("href", DET[index]);
                    //MagicZoomPlus.refresh("Zoomer");
                   //alert(DET[index]);
                }
            }

            // set the drop down index for swatch selection
            document.forms["addform"].elements[name].selectedIndex = (index*1);

            
        }
        if (currentFeatureIndex < (OPT.length-1)) {
            // eval the next list if there are more
            var selectedValue = document.forms["addform"].elements[name].options[(index*1)].value;
            if (index == -1) {
              <#if featureOrderFirst?exists>
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
                var Variable1 = eval("list" + OPT[(currentFeatureIndex+1)] + selectedValue + "()");
            }
            // set the product ID to NULL to trigger the alerts
            setAddProductId('NULL');
            // set the variant price to NULL
            setVariantPrice('NULL');
        } else {
       
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms["addform"].elements[name].selectedIndex;
            // using the selected index locate the sku
            var sku = document.forms["addform"].elements[name].options[indexSelected].value;
            // display alternative packaging dropdown
            ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId(sku);

            // set the variant price
            setAddProductNames(sku);
            setVariantPrice(sku);
			
			checkInventory(sku);
            // check for amount box
            toggleAmt(checkAmtReq(sku));
        }
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
        var s = document.getElementById('shoppingListId');
		var shoppingListId = s.options[s.selectedIndex].value;
		var productId = document.addToShoppingList.productId.value;
		var productStoreId = document.addToShoppingList.productStoreId.value;
		var quantity = document.addToShoppingList.quantity.value;
		var displayMessageVariant = document.getElementById('variant_product_name').innerHTML;
		var displayMessageSimple = document.getElementById('variant_product_default').innerHTML;
		var brandName = "${productContentWrapper.get("BRAND_NAME")?if_exists}";
		displayMessageVariant = displayMessageVariant.trim();
		displayMessageSimple = displayMessageSimple.trim();
		
		var originalMsg = "";
		if(displayMessageVariant=="" || displayMessageVariant == null){
			originalMsg = displayMessageSimple;
		}
		else{
			originalMsg = displayMessageVariant;
		}
		displayMessage = brandName +" "+originalMsg + " successfully added to the favourite list";
        var  parameters = 'shoppingListId=' + shoppingListId +'&productId=' + productId +'&productStoreId=' + productStoreId +'&quantity=' + quantity;
        var  paramet = 'shoppingListId=' + shoppingListId +'&add_product_id=' + productId +'&productStoreId=' + productStoreId +'&quantity=' + quantity;
        
        jQuery.ajax({url: "/control/addItemToFavouriteList",
		         data: parameters,
		         type: 'post',
		         async: false,
		         success: function() {
		           document.getElementById('outputs').innerHTML = displayMessage;
	         	   ShowDialog(false);
		         }
		    	});
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
         alert("priceElem="+priceElem);
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
//]]>
//////////////Start Code of Impulsive product summary file//////////

function getList1(name, index, src, formName) {
var AA= window["IMG"+formName];

  currentFeatureIndex = findIndex(name);
  	var ABC =document.getElementById('ABlink'+formName).href;

 
  	 var data = ABC.split("?");
  	 
  	 ABC = data[0];
  	// alert(data[0])
  	
	var quantity =document.getElementById('qty'+formName).value;
 		ABC=ABC+"?name="+name+"&index="+index+"&src="+src+"&quantity="+quantity;
 	
 	
 	document.getElementById('ABlink'+formName).href =ABC;
 	
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
            setAddProductId1('NULL');
            // set the variant price to NULL
            setVariantPrice('NULL');
        } else {
            // this is the final selection -- locate the selected index of the last selection
            var indexSelected = document.forms['addform'+formName].elements[name].selectedIndex;
			//alert("486==="+indexSelected);
            // using the selected index locate the sku
            var sku = document.forms['addform'+formName].elements[name].options[indexSelected].value;
             var skuName = document.forms['addform'+formName].elements[name].options[indexSelected].text;
            //alert("489==="+sku);
            // display alternative packaging dropdown
            checkInventory1(sku,formName);
            //ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId1(sku,formName);
             setAddProductNames1(skuName,sku,formName);
 			setVariantPrice3(sku,formName);
            // set the variant price
           
           
           
           

            // check for amount box
           // toggleAmt1(checkAmtReq(sku));
        }
    }
function setAddProductId1(name,formName) {
   
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
            //var elem = document.getElementById('product_id_display'+formName);
            //var txt = document.createTextNode(name);
            //if(elem.hasChildNodes()) {
                //elem.replaceChild(txt, elem.firstChild);
            //} 
            //else {
                //elem.appendChild(txt);
            //}
        }
    }
 function checkInventory1(prodId,productId){
	  var data=document.getElementById("Inventory1"+prodId).value;
    	if(data!=null && data!="")
    	{
    	if(parseInt(data)>0){
    	  //alert("data"+data);
        			document.getElementById("addstockP"+productId).style.display="block";
        			document.getElementById("outstockP"+productId).style.display="none";
        			
        		}else{
        			document.getElementById("addstockP"+productId).style.display="none";
        			document.getElementById("outstockP"+productId).style.display="block";
        			
        		}
        
        }
   
    }
function setAddProductNames1(skuName,sku,formName)
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
  function setVariantPrice3(sku,formName) {
   
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
            var discount=myObject.result.listPrice-myObject.result.price
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
function isNumberKey1(evt,productId)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
            
         productDetail(productId,String.fromCharCode(charCode));
         return true;
      }
function productDetail(productId,charCode){
	 var ABC =document.getElementById('ABlink'+productId).href;
  	 var data = ABC.split("&quantity");
  	 data = data[0].split("?quantity");
  	 ABC = data[0];
  	 var quantity =document.getElementById('qty'+productId).value;
  	 
 	 var ABCSPLIT = ABC.split("?");
  	 if(ABCSPLIT != null && ABCSPLIT.length>1)ABC=ABC+"&";
  	 else
  	 	ABC=ABC+"?";
 	 ABC=ABC+"quantity="+quantity+charCode;
 	 
 	 document.getElementById('ABlink'+productId).href =ABC;
 	 document.getElementById('desclink'+productId).href =ABC;
}    
 

function addItems1(add_product_id,formName,add_product_name) { 
  var value= document.getElementById(add_product_id).value;
  if(value == 'NULL' || value == ""){
     alert("Please select pack size");
  }else{
  		 
  		if(add_product_id != '' && document.getElementById('productNameDescrip_'+ add_product_id) != '' && document.getElementById('variant_product_name'+add_product_id) != ''){  
  			if(document.getElementById('productNameDescrip_'+ add_product_id).innerHTML != '' ){
  				add_product_name  = document.getElementById('productNameDescrip_'+ add_product_id).innerHTML; 
  				add_product_name = add_product_name + document.getElementById('variant_product_name'+add_product_id).innerHTML;
  			} 
  		} 
		addItem(formName,add_product_name);
  }
}
    
function addItem(formName,add_product_name) {  
   var add_product_id= formName.add_product_id.value;
   var quantity= formName.quantity.value;
 
    if(quantity > 20)
    {
      alert("Please select quantity less than 20");
    }else{
    
      var clearSearch= formName.clearSearch.value;
      if (formName.add_product_id.value == 'NULL') {
         showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
         return;
      }else {
         var  param = 'add_product_id=' + add_product_id + '&quantity=' + quantity;
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
          	 
          	 			 if(data == "Can't add more than giftcard")return;
          	 			 {
          					var dataTemplate=data.replace("Can not","Can't");
          	 				alert(dataTemplate);
          	 			}
	         		return;
          			}else{
          				if(document.getElementById('variant_product_name') != null){
          					if(document.getElementById('variant_product_name').innerHTML != ''){
          			    		add_procduct_name = document.getElementById('variant_product_name').innerHTML;  
          			    	} 
          			    }
          				cart_top_notification(add_product_name);
	                	document.getElementById('microcart').innerHTML=data;
          			}
         		},
         		complete:  function() {
         		},
        		error: function(data) {
        		}
    	});
      }
    }
}
    
      
//////////////End Code of Impulsive product summary file////////////
 </script>
<#if inventoryMap?exists>
 <#list inventoryMap.keySet() as inventoryMapTemp>
<input type="hidden" name="${inventoryMapTemp?if_exists}" id="Inventory${inventoryMapTemp?if_exists}" value="<#if inventoryMap?has_content>${inventoryMap.get(inventoryMapTemp)?if_exists}</#if>"/>
</#list>
</#if>	
<#macro showUnavailableVarients>
  <#if unavailableVariants?exists>
    <ul>
      <#list unavailableVariants as prod>
        <#assign features = prod.getRelated("ProductFeatureAppl")/>
        <li>
          <#list features as feature>
            <em>${feature.getRelatedOne("ProductFeature").description}</em><#if feature_has_next>, </#if>
          </#list>
          <span>${uiLabelMap.ProductItemOutOfStock}</span>
        </li>
      </#list>
    </ul>
  </#if>
</#macro>
 <#if product.productId?has_content && product.productId ="GIFTCARD">
 <div class="clear"></div>
<div class="inner-content">
<h3 class="Shopcar_pageHead">Gift an e-voucher</h3>
	<div class="clear"></div>
					<p style="margin:10px 0!important; text-align:justify;line-height:18px;">Thinking of what to Gift your loved one!! Gift them with an e-voucher. </p>
					<p style="margin:10px 0 !important; text-align:justify;line-height:18px;">YouMart has taken an initiative which is a revolutionary way to gift your dear one with an e-voucher to ease your worry of gifting. It is an easy method for both the sender & the recipient to participate in this unique process.</p>
					<p style="margin:10px 0 !important; text-align:justify;">You can select the amount you wish to gift & go through the payment mode to pay the relevant amount either through Credit/Debit Card or NetBanking. Once you complete the payment procedure, we will send you a confirmation email; and also simultaneously will be intimating the recipient about the gifted e-voucher with your personalized message through SMS & email.</p>
					<p style="margin:10px 0 !important; text-align:justify;">There will be a coupon code sent to the recipient which will be used only once & will expire in 365 days period. The recipient while shopping with YouMart.in  has to enter the coupon code in the "Checkout" page to avail the benefit.</p>
					<p style="margin:10px 0 25px!important; text-align:justify">Kindly choose the e-voucher from the options & proceed for payment. All the fields are mandatory & needs to be filled for you to complete the transaction:</p>
	</div>
</#if>
<br/><br/><br/>
<#if productId?exists>
${request.setAttribute("productId",productId)}
${session.setAttribute("productId",productId)}
</#if>
<div id="productdetail">
    <#assign productAdditionalImage1 = productContentWrapper.get("ADDITIONAL_IMAGE_1")?if_exists />
    <#assign productAdditionalImage2 = productContentWrapper.get("ADDITIONAL_IMAGE_2")?if_exists />
    <#assign productAdditionalImage3 = productContentWrapper.get("ADDITIONAL_IMAGE_3")?if_exists />
    <#assign productAdditionalImage4 = productContentWrapper.get("ADDITIONAL_IMAGE_4")?if_exists />
    
      <#-- Category next/previous -->
      <#--<#if category?exists>
          <div id="paginationBox">
            <#if previousProductId?exists>
              <a href="<@ofbizUrl>product/~category_id=${categoryId?if_exists}/~product_id=${previousProductId?if_exists}</@ofbizUrl>" class="buttontextblue">${uiLabelMap.CommonPrevious}</a>&nbsp;|&nbsp;
            </#if>
            <a href="<@ofbizUrl>category/~category_id=${categoryId?if_exists}</@ofbizUrl>" class="linktext">${(category.categoryName)?default(category.description)?if_exists}</a>
            <#if nextProductId?exists>
              &nbsp;|&nbsp;<a href="<@ofbizUrl>product/~category_id=${categoryId?if_exists}/~product_id=${nextProductId?if_exists}</@ofbizUrl>" class="buttontextblue">${uiLabelMap.CommonNext}</a>
            </#if>
          </div>
      </#if>-->
    <div id="productImageBox">
        <#if productImageList?has_content>
            <#-- Product image/name/price -->
            <div id="detailImageBox">
                <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists />
                <#-- remove the next two lines to always display the virtual image first (virtual images must exist) -->
                <#if firstLargeImage?has_content>
                    <#assign productLargeImageUrl = firstLargeImage />
                </#if>
                <#if productLargeImageUrl?string?has_content>
                    <a href="javascript:popupDetail();"><img id="detailImage" src="<@ofbizContentUrl variant="200">${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" name="mainImage" vspace="5" hspace="5" width="200" alt="" /></a>
                    <input type="hidden" id="originalImage" name="originalImage" value="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" />
                </#if>
                <#if !productLargeImageUrl?string?has_content>
                    <img id="detailImage" src="/images/defaultImage.jpg" name="mainImage" alt="" />
                </#if>
            </div>
            <#-- Show Image Approved -->
            <div id="additionalImageBox">
                <#if productImageList?has_content>
                    <#list productImageList as productImage>
                        <div class="additionalImage">
                            <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productImage.productImage}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productImage.productImageThumb}</@ofbizContentUrl>" vspace="5" hspace="5" alt="" /></a>
                        </div>
                    </#list>
                </#if>
            </div>
        <#else>
            <#-- Product image/name/price -->
            <div id="detailImageBox">
                <#assign productLargeImageUrl = productContentWrapper.get("LARGE_IMAGE_URL")?if_exists />
                <#assign productDetailImageUrl = productContentWrapper.get("DETAIL_IMAGE_URL")?if_exists />
                
                <#-- remove the next two lines to always display the virtual image first (virtual images must exist) -->
                <#if firstLargeImage?has_content>
                    <#assign productLargeImageUrl = firstLargeImage />
                </#if>
                <#if firstDetailImage?has_content>
			        <#assign productDetailImageUrl = firstDetailImage>
				</#if>
                <#if productLargeImageUrl?string?has_content>
                    <a href="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productDetailImageUrl?if_exists}</@ofbizContentUrl>" class="MagicZoomPlus" id="Zoomer" rel="selectors-effect-speed: 600; hotspots: hd1-spots"><img id="detailImage" src="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" name="mainImage" style="border:1px solid #ccc;" vspace="5" hspace="5" width="343" height="244" alt="" /></a>
                    <input type="hidden" id="originalImage" name="originalImage" value="<@ofbizContentUrl>${contentPathPrefix?if_exists}${productLargeImageUrl?if_exists}</@ofbizContentUrl>" />
                </#if>
                <#if !productLargeImageUrl?string?has_content>
                    <img id="detailImage" src="/images/defaultImage.jpg" name="mainImage" alt="" />
                </#if>
                
              <div>&nbsp</div>          
             
            </div>
            <div id="additionalImageBox">
                <#if productAdditionalImage1?string?has_content>
                    <div class="additionalImage">
                        <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage1}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
                    </div>
                </#if>
                <#if productAdditionalImage2?string?has_content>
                    <div class="additionalImage">
                        <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage2}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
                    </div>
                </#if>
                <#if productAdditionalImage3?string?has_content>
                    <div class="additionalImage">
                        <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage3}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
                    </div>
                </#if>
                <#if productAdditionalImage4?string?has_content>
                    <div class="additionalImage">
                        <a href="javascript:void(0);" swapDetail="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>"><img src="<@ofbizContentUrl>${productAdditionalImage4}</@ofbizContentUrl>" vspace="5" hspace="5" width="200" alt="" /></a>
                    </div>
                </#if>
            </div>
        </#if>
        
        <div id="productDetailBox">
         <h2>${productContentWrapper.get("BRAND_NAME")?if_exists}</h2>
         <#assign bName = productContentWrapper.get("BRAND_NAME")?if_exists>
          <h2><span id="variant_product_name"></span><span id="variant_product_default" style="display: block; height: 10px;" >${productContentWrapper.get("PRODUCT_NAME")?if_exists}</span></h2>
         <#--  <div style="margin-bottom:12px;">${productContentWrapper.get("DESCRIPTION")?if_exists}</div>-->
          <!--<div>${product.productId?if_exists}</div>-->
          <#-- example of showing a certain type of feature with the product -->
          <#--if sizeProductFeatureAndAppls?has_content>
            <div>
              <#if (sizeProductFeatureAndAppls?size == 1)>
                <#-- TODO : i18n>
                Size:
              <#--else>
                Sizes Available:
              </#if>
              <#list sizeProductFeatureAndAppls as sizeProductFeatureAndAppl>
                ${sizeProductFeatureAndAppl.description?default(sizeProductFeatureAndAppl.abbrev?default(sizeProductFeatureAndAppl.productFeatureId))}<#if sizeProductFeatureAndAppl_has_next>,</#if>
              </#list>
            </div>
          </#if-->
    
          <#-- for prices:
                  - if price < competitivePrice, show competitive or "Compare At" price
                  - if price < listPrice, show list price
                  - if price < defaultPrice and defaultPrice < listPrice, show default
                  - if isSale show price with salePrice style and print "On Sale!"
          -->
            <div class="detailprice" style="color:#ff0000 !important;" id="detailprice">
          <#if price.competitivePrice?exists && price.price?exists && price.price &lt; price.competitivePrice>
            <span style="float: left; margin-right: 10px;">${uiLabelMap.ProductCompareAtPrice}: <span class="basePrice"  ><@ofbizCurrency amount=price.competitivePrice isoCode=price.currencyUsed /></span></span>
          </#if>
          <#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
          <span style="float: left; margin-right: 10px;"><span class="${priceStyle?if_exists}" style="color:#000000 !important"><span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;</span><del><span class="${priceStyle?if_exists}" style="color:#000000 !important" id="list_price_display"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed/>-->${price.listPrice?if_exists}</span></del></span>
          </#if>
          <#if price.listPrice?exists && price.defaultPrice?exists && price.price?exists && price.price &lt; price.defaultPrice && price.defaultPrice &lt; price.listPrice>
            <span style="float: left; margin-right: 10px;">${uiLabelMap.ProductRegularPrice}: <span class="basePrice"><@ofbizCurrency amount=price.defaultPrice isoCode=price.currencyUsed /></span></span>
          </#if>
          <#if price.specialPromoPrice?exists>
            <div >${uiLabelMap.ProductSpecialPromoPrice}: <span class="basePrice"><@ofbizCurrency amount=price.specialPromoPrice isoCode=price.currencyUsed /></span></div>
          </#if>
        
            <strong style="color:#000000 !important">
              <#if price.isSale?exists && price.isSale>
                <span class="salePrice" ></span>
                <#assign priceStyle = "salePrice" />
              <#else>
                <#assign priceStyle = "regularPrice" />
              </#if>
              <span style="float: left; margin-right: 10px;">
                <#--${uiLabelMap.OrderYourPrice}MRP:-->  <#if "Y" = product.isVirtual?if_exists> <!--${uiLabelMap.CommonFrom} --></#if><span class="${priceStyle?if_exists}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" style="color:#000000 !important" id="variant_price_change"><#--<@ofbizCurrency amount=price.price isoCode=price.currencyUsed />-->${price.price?if_exists}</span></span>
                 <#if product.productTypeId?if_exists == "ASSET_USAGE" >
                <#if product.reserv2ndPPPerc?exists && product.reserv2ndPPPerc != 0><br /><span class="${priceStyle?if_exists}">${uiLabelMap.ProductReserv2ndPPPerc}<#if !product.reservNthPPPerc?exists || product.reservNthPPPerc == 0>${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}</#if> <@ofbizCurrency amount=product.reserv2ndPPPerc*price.price/100 isoCode=price.currencyUsed /></span></#if>
                <#if product.reservNthPPPerc?exists &&product.reservNthPPPerc != 0><br /><span class="${priceStyle?if_exists}">${uiLabelMap.ProductReservNthPPPerc} <#if !product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0>${uiLabelMap.ProductReservSecond} <#else> ${uiLabelMap.ProductReservThird} </#if> ${uiLabelMap.CommonUntil} ${product.reservMaxPersons?if_exists}, ${uiLabelMap.ProductEach}: <@ofbizCurrency amount=product.reservNthPPPerc*price.price/100 isoCode=price.currencyUsed /></span></#if>
                <#if (!product.reserv2ndPPPerc?exists || product.reserv2ndPPPerc == 0) && (!product.reservNthPPPerc?exists || product.reservNthPPPerc == 0)><br />${uiLabelMap.ProductMaximum} ${product.reservMaxPersons?if_exists} ${uiLabelMap.ProductPersons}.</#if>
                 </#if>
             </strong>
         
          
 	          <#if price.listPrice?exists && price.price?exists && price.price &lt; price.listPrice>
            <#assign priceSaved = price.listPrice - price.price />
            <#assign percentSaved = (priceSaved / price.listPrice) * 100 />
            <span style="float: left; margin-right: 10px;">
            (${uiLabelMap.OrderSave}:<span class="${priceStyle?if_exists}" style="color:#ff0000 !important;"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" id="discount_price_display" style="color:#ff0000 !important;">${priceSaved?if_exists}</span>)<br/></span>
          <div class="discount-${percentSaved?int?if_exists} discountmain" style="position: absolute; top: -80px; left: -120px;"></div>
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
    <div class="detailprice" style="color: rgb(255, 0, 0) ! important;height:10px;">
          <div id="tempListPrice" style="display: none; float: left; margin-right: 10px; color: rgb(255, 0, 0);"><span class="${priceStyle?if_exists}" style="color:#000000 !important"><span class="WebRupee" style="color:#000000 !important">&#8377;</span>&nbsp;</span><del><span class="${priceStyle?if_exists}" style="color:#000000 !important" id="tempList"></span></del></div>
       <div id="tempDefault" style="display: none; float: left; margin-right: 10px;font-weight: bold;display:none">
         <#if "Y" = product.isVirtual?if_exists> <!--${uiLabelMap.CommonFrom} --></#if><span class="${priceStyle?if_exists}"><span class="WebRupee">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" id="temdefault" style="color:#000000 !important" ></span></div>
     <div id="tempSave" style="display:none;float: left;color: rgb(255, 0, 0);display:none">
            (${uiLabelMap.OrderSave}:<span class="${priceStyle?if_exists}"><span class="WebRupee"   style="color: rgb(255, 0, 0);">&#8377;</span>&nbsp;</span><span class="${priceStyle?if_exists}" id="tempSavePrice"   style="color: rgb(255, 0, 0);"></span>)<br/>
              <span id="tempDiscount" style="position: absolute; top: -80px; left: -120px;"></span>
            </div>
    
    
     </div>
          <#-- Included quantities/pieces -->
          <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
            <div >
              ${uiLabelMap.OrderPieces}: ${product.piecesIncluded}
            </div>
          </#if>
          <#if (product.quantityIncluded?exists && product.quantityIncluded != 0) || product.quantityUomId?has_content>
            <#assign quantityUom = product.getRelatedOneCache("QuantityUom")?if_exists />
           <!-- <div>
              ${uiLabelMap.CommonQuantity}: ${product.quantityIncluded?if_exists} ${((quantityUom.abbreviation)?default(product.quantityUomId))?if_exists}
            </div>-->
          </#if>
    
          <#if (product.weight?exists && product.weight != 0) || product.weightUomId?has_content>
            <#assign weightUom = product.getRelatedOneCache("WeightUom")?if_exists />
            <div>
              ${uiLabelMap.CommonWeight}: ${product.weight?if_exists} ${((weightUom.abbreviation)?default(product.weightUomId))?if_exists}
            </div>
          </#if>
          <#if (product.productHeight?exists && product.productHeight != 0) || product.heightUomId?has_content>
            <#assign heightUom = product.getRelatedOneCache("HeightUom")?if_exists />
            <div>
              ${uiLabelMap.CommonHeight}: ${product.productHeight?if_exists} ${((heightUom.abbreviation)?default(product.heightUomId))?if_exists}
            </div>
          </#if>
          <#if (product.productWidth?exists && product.productWidth != 0) || product.widthUomId?has_content>
            <#assign widthUom = product.getRelatedOneCache("WidthUom")?if_exists />
            <div>
              ${uiLabelMap.CommonWidth}: ${product.productWidth?if_exists} ${((widthUom.abbreviation)?default(product.widthUomId))?if_exists}
            </div>
          </#if>
          <#if (product.productDepth?exists && product.productDepth != 0) || product.depthUomId?has_content>
            <#assign depthUom = product.getRelatedOneCache("DepthUom")?if_exists />
            <div>
              ${uiLabelMap.CommonDepth}: ${product.productDepth?if_exists} ${((depthUom.abbreviation)?default(product.depthUomId))?if_exists}
            </div>
          </#if>
    
          <#if daysToShip?exists>
            <div><strong>${uiLabelMap.ProductUsuallyShipsIn} ${daysToShip} ${uiLabelMap.CommonDays}!</strong></div>
          </#if>
    
          <#-- show tell a friend details only in ecommerce application -->
          
    
          <#if disFeatureList?exists && 0 &lt; disFeatureList.size()>
          <p>&nbsp;</p>
            <#list disFeatureList as currentFeature>
                <#assign disFeatureType = currentFeature.getRelatedOneCache("ProductFeatureType") />
                <div>
                    <#if disFeatureType.description?exists>${disFeatureType.get("description", locale)}<#else>${currentFeature.productFeatureTypeId}</#if>:&nbsp;${currentFeature.description}
                </div>
            </#list>
                
          </#if>
       
    	
    	  <#--if variantSample?exists && 0 &lt; variantSample.size()>
    	  More Pack Size :
            <#assign imageKeys = variantSample.keySet() />
            <#assign imageMap = variantSample />
                <#assign maxIndex = 7 />
                <#assign indexer = 0 />
                <#list imageKeys as key>
                  <#assign swatchProduct = imageMap.get(key) />
                  <#if swatchProduct?has_content && indexer &lt; maxIndex>
                    <#assign imageUrl = Static["org.ofbiz.product.product.ProductContentWrapper"].getProductContentAsText(swatchProduct, "SMALL_IMAGE_URL", request)?if_exists />
                    <#if !imageUrl?string?has_content>
                      <#assign imageUrl = productContentWrapper.get("SMALL_IMAGE_URL")?if_exists />
                    </#if>
                    <#if !imageUrl?string?has_content>
                      <#assign imageUrl = "/images/defaultImage.jpg" />
                    </#if>
                      <#--a href="javascript:getList('FT${featureOrderFirst}','${indexer}',1);"><img src="<@ofbizContentUrl variant="60">${contentPathPrefix?if_exists}${imageUrl}</@ofbizContentUrl>" width="60" height="60" alt="" /></a-->
                     
                      <#--a href="javascript:getList('FT${featureOrderFirst}','${indexer}',1);" class="linktext">${key}, </a>
                  </#if>
                  <#assign indexer = indexer + 1 />
                </#list>
                <#if (indexer > maxIndex)>
                  <div><strong>${uiLabelMap.ProductMoreOptions}</strong></div>
                </#if>
          </#if-->
    	
    	
        <div id="addItemForm" style="margin-top:13px;">
          <form method="post" action="<@ofbizUrl>additem1<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addform"  style="margin: 0;">
				        	<#assign inStock = true>
			        		<#-- Variant Selection -->
			        		<div style="float:left; margin-bottom:10px;margin-top: 15px;">
				        	<div class="detailBoxLeft" style="float:left; width:142px; margin-bottom:5px;">
						   		<#if product.isVirtual?if_exists?upper_case == "Y">
						          	<#if product.virtualVariantMethodEnum?if_exists == "VV_FEATURETREE" && featureLists?has_content>
						            	<#list featureLists as featureList>
						                	<#list featureList as feature>
							                    <#if feature_index == 0>
							                        ${feature.description}: <select id="FT${feature.productFeatureTypeId}" name="FT${feature.productFeatureTypeId}" onChange="javascript:checkRadioButton();">
							                        <option value="select" selected="selected"> select option </option>
							                    <#else>
							                        <option value="${feature.productFeatureId}">${feature.description} <#if feature.price?exists>(+ <@ofbizCurrency amount=feature.price?string isoCode=feature.currencyUomId/>)</#if></option>
							                    </#if>
							                    </select>
						               	</#list>
						            	</#list>
						            	
						              	<input type="hidden" name="product_id" value="${product.productId}"/>
						              	<input type="hidden" name="add_product_id" value="${product.productId}"/>
						            	<div id="addCart1" style="display:none;>
						              		<span style="white-space: nowrap;"><b>${uiLabelMap.CommonQuantity}:</b></span>&nbsp;
						              		<input type="text" size="5"  name="quantity" class="inputBox" value="<#if parameters.quantity?has_content>${parameters.quantity}<#else>1</#if>"/>
						              		
						              		<a href="javascript:javascript:addItemOld();" class="buttontext"><span style="white-space: nowrap;">${uiLabelMap.OrderAddToCart}</span></a>
						              		&nbsp;
						            	</div>
						            	<div id="addCart2" style="display:block;">
						              		<span style="white-space: nowrap;"><b>${uiLabelMap.CommonQuantity}:</b></span>&nbsp;
						              		<input type="text" size="5" value="<#if parameters.quantity?has_content>${parameters.quantity}<#else>1</#if>" disabled="disabled"/>
						              		<a href="javascript:alert('Please select all features first');" class="buttontext"><span style="white-space: nowrap;">${uiLabelMap.OrderAddToCart}</span></a>
						              			&nbsp;
						            	</div>
						          	</#if>
						          	<#if !product.virtualVariantMethodEnum?exists || product.virtualVariantMethodEnum == "VV_VARIANTTREE">
						           		<#if variantTree?exists && (variantTree.size() > 0)>
								            <#list featureSet as currentType>
								              <div style="clear:both; margin-right:5px;">
								                <select name="FT${currentType}" class="ListboxWidthfix" style="height:18px; padding:0px !important; width:82px;" onchange="javascript:getList(this.name, (this.selectedIndex), this.selectedIndex);">
					
								                </select>
								              </div>
								            </#list>
								            <#if categorycontent?has_content && categorycontent.contentId?has_content>
								            	<script>function openchart(){window.open("<@ofbizUrl>contentchart?contentId=${categorycontent.contentId?if_exists}</@ofbizUrl>", "", "width=510,height=600,status=no,scrollbars=yes");}</script>
								            	<a href="#" onclick="javascript:openchart();">${categorycontent.description?default(categorycontent.contentId)}</a>
								            </#if>
						            		<div style="clear:both;"></div>
						            		<input type="hidden" name="product_id" value="${product.productId}"/>
						            		<input type="hidden" name="add_product_id" value="NULL"/>
						          		<#else>
						            		<input type="hidden" name="product_id" value="${product.productId}"/>
						            		<input type="hidden" name="add_product_id" value="NULL"/>
						            		<div><b><img src="/erptheme1/out-of-stock.png" alt="" title=""/><#-- ${uiLabelMap.ProductItemOutOfStock} --> </b></div>
						            		<#assign inStock = false>
						          		</#if>
						         	</#if>
						        <#else>
						          	<input type="hidden" name="product_id" value="${product.productId}"/>
						          	<input type="hidden" name="add_product_id" value="${product.productId}"/>
						          	<#assign isStoreInventoryNotAvailable = !(Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, product, 1.0?double))>
						          	<#assign isStoreInventoryRequired = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryRequired(request, product)>
						          	<#if isStoreInventoryNotAvailable>
						            	<#if isStoreInventoryRequired>
						              		<div><b><img src="/erptheme1/out-of-stock.png" alt="" title=""/><#-- ${uiLabelMap.ProductItemOutOfStock}. --></b></div>
						              		<#assign inStock = false>
						            	<#else>
						              		<div><b>${product.inventoryMessage?if_exists}</b></div>
						            	</#if>
						      		</#if>
								</#if>
					    	</div><br/>
					    	<div style="clear:both"></div>
						    	
				        	<#-- check to see if introductionDate hasnt passed yet -->
				        	<div class="detailBoxRight">
				        		<div>
					        		<#if product.introductionDate?exists && nowTimestamp.before(product.introductionDate)>
					        			<p>&nbsp;</p>
					          			<div style="color: red;">${uiLabelMap.ProductProductNotYetMadeAvailable}.</div>
					        			<#-- check to see if salesDiscontinuationDate has passed -->
					        		<#elseif product.salesDiscontinuationDate?exists && nowTimestamp.after(product.salesDiscontinuationDate)>
					          			<div style="color: red;">${uiLabelMap.ProductProductNoLongerAvailable}.</div>
					        			<#-- check to see if the product requires inventory check and has inventory -->
					        		<#elseif product.virtualVariantMethodEnum?if_exists != "VV_FEATURETREE">
					          			<#if inStock>
								            <#if product.requireAmount?default("N") == "Y">
								              <#assign hiddenStyle = "display:block;">
								            <#else>
								              <#assign hiddenStyle = "display:none;">
								            </#if>
					            			<div id="add_amount" style="float:left; margin-right:10px;${hiddenStyle}" >
					              				<span style="white-space: nowrap;"><b>${uiLabelMap.CommonAmount}:</b></span>&nbsp;
					              				<input type="text" size="5" name="add_amount" value=""/>
					            			</div>
					            			<table style="margin:0px;">
					            			 <tr>
					            			   <#if product.productTypeId?if_exists == "ASSET_USAGE">
					            				<td>
					               				<table width="100%" border="0" cellpadding="2" cellspacing="0" bgcolor="#CCCCCC">
								                	<tr bgcolor="#FFFFFF">
									                	<td width="15%" valign="top" align="left">Start Date</td>
									                	<td width="20%" valign="top"><input class="ddmmyy" type="text" size="10" name="reservStart" value="YYYY-MM-DD" onchange="javascript:checkAssetAvailability()"/></td>
									                	<td width="6%" valign="top"><a href="javascript:call_cal_notime(document.addform.reservStart, '${nowTimestamp.toString().substring(0,10)}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a></td>
									                	<td width="20%" valign="top"></td>
									                	<td valign="top">Number of rooms</td>
									                	<td valign="top"><input type="text" size="5" class="inputBox" name="quantity" value="1" onchange="javascript:checkAssetAvailability()"/></td>
								                	</tr>
								                	<tr bgcolor="#FFFFFF">
									                	<td align="left" valign="top">End Date</td>
									                	<td valign="top"><input class="ddmmyy" type="text" size="10" name="reservEnd" value="YYYY-MM-DD" onchange="javascript:checkAssetAvailability()"/></td>
									                	<td valign="top"><a href="javascript:call_cal_notime(document.addform.reservEnd, '${nowTimestamp.toString().substring(0,10)}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a></td>
									                	<td valign="top"></td>
									                	<td valign="top">Number of persons</td>
									                	<td valign="top"><input type="text" size="4" name="reservPersons" value="2" onchange="javascript:checkAssetAvailability()"/></td>
								                	</tr>
					                			</table>
					                			
					                			<hr/>
								                <script>
								                	function checkAssetAvailability(){
								                		var productId = "${productId}";
								                		var reservStart = document.addform.reservStart.value;
								                		var quantity = document.addform.quantity.value;
								                		var reservEnd = document.addform.reservEnd.value;
								                		var reservPersons = document.addform.reservPersons.value;
								                		if(productId != "" && reservStart != "" && reservStart != "yyyy-mm-dd" && reservEnd != "" && reservEnd != "yyyy-mm-dd" && quantity != "")
								                			new Ajax.Updater("checkAssetAvailability", "<@ofbizUrl>checkAssetAvailability</@ofbizUrl>", {parameters:{productId: productId, reservStart : reservStart, quantity : quantity, reservEnd : reservEnd, reservPersons : reservPersons}});
								                	}
								                </script>
								                <span id="checkAssetAvailability" style="color:green"></span>
								                </td>
					            			<#else/>
					            			
					            			<#if product?has_content && product.productId=="GIFTCARD">
					            			<input type="hidden" style="width:20px; margin:0px; height:16px;" size="5" class="inputBox" name="quantity" id="quantityvalidatea" value="1"/>
					            			<#else>
					            			<td>
					                			Qty: &nbsp; <a href="javascript:chgQty0(-1.0,'quantityvalidatea');" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus1.png" alt=""/></a>
					                			
					                		</td>
					                		<td>	
					                			<input type="text" style="width:20px; margin:0px; height:16px;" size="5" class="inputBox" onkeypress="return isNumberKey(event)" name="quantity" id="quantityvalidatea" value="<#if parameters.quantity?has_content>${parameters.quantity}<#else>1</#if>"/>
					                		</td>
					                		<td>
					                			 <a href="javascript:chgQty0(1.0,'quantityvalidatea');" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add1.png" alt=""/></a>
					                		</td>	
					                			</#if>
					                		
					                		<div style="float:left; margin-top:10px;">
									          <#if product.productId?has_content && product.productId ="GIFTCARD">
													<table cellpadding="0" cellspacing="0" border="0">
														<tr>
															<td>Recipient Name:</td>
															<td><input type="text" name="recipientName" id="recipient_name"value=""/>*</td>
														</tr>
														<tr>
															<td>Recipient Mobile no:</td>
															<td><input type="text" name="recipientMobileNum" id="recipient_mobile" maxlength="10" value="" onkeypress="return isNumberKey(event)" onchange="addZeroPrefixGiftCard();"/>*</td>
														</tr>
														<tr>
															<td>Recipient Email id:</td> 
															<td><input type="text" name="recipientEmailId" id="recipient_email" value=""/>*</td>
														</tr>
														<tr>
															<td>Message:</td>
															<td><textarea cols="22" rows="3" name="message" id="message" style="margin-left:4px; margin-top:5px;"></textarea>*</td>
														</tr>
														<tr><td colspan="2" style="height:5px;"></td></tr>
													</table>
											  </#if>
								          </div>
													                		
					            			</#if>
								            <#-- This calls addItemOld() so that variants of virtual products cant be added before distinguishing features are selected, it should not be changed to additemSubmit() -->
								            <#if product.productTypeId?if_exists == "ASSET_USAGE">
								            	<div style="text-align:right"><a href="javascript:addItemOld()" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;</div>
												<div style="height:5px; overflow:hidden"></div>
												
											<#else>
											</tr>
											<tr><td height="5px" colspan="3"></td></tr>
											<tr>
											<td colspan="2">
											<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
                							<span id="product_uom"></span>
                							<input type="hidden" id="minitotal" value="<@ofbizCurrency amount=shoppingCart.getDisplayGrandTotal() isoCode=shoppingCart.getCurrency()/>"/>
                							<!--<input type="hidden" class="inputBox" id ="miniquantity" size="3" name="quantity" value="1"/>-->
    							   <#assign newProductName= product.productName?if_exists/>
                                    <#assign newProductName= newProductName?replace("&#39;", "")/>
    							
                							<#if product?has_content && product.productId=="GIFTCARD">
                								<div id="addstockP${product.productId?if_exists}"><a href="javascript:addItems('abc${product.productId}','${newProductName}');" class="buttontext">Buy
                								</a></div>
                							<#else>
                							<#if product.isVirtual?has_content && "Y" == product.isVirtual>
                								<div id="addstockP${product.productId?if_exists}"><a href="javascript:addItems('abc${product.productId}','${newProductName}');" class="buttontext"><#if product?has_content && product.productId=="GIFTCARD">Buy<#else>
                							${uiLabelMap.OrderAddToCart}</#if></a>&nbsp;&nbsp;</div>
                 									<div id="outstockP${product.productId?if_exists}" style="display:none;">
                 									<img src="/erptheme1/out-of-stock.png" alt="" title=""/></div>
                							<#else>
            								<#if product.inventoryAtp?has_content && (product.inventoryAtp?number >=1)>
            									<div id="addstockP${product.productId?if_exists}"><a href="javascript:addItems('abc${product.productId}','${newProductName}');" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;&nbsp;</div>
            								<#else>
            									<div id="outstockP${product.productId?if_exists}" >
                 								<img src="/erptheme1/out-of-stock.png" alt="" title=""/></div>
            								</#if>
                							</#if>
                							</#if>
                							
                							
												<#--a href="javascript:addItems('abc${product.productId}')" class="buttontext"><#if product?has_content && product.productId=="GIFTCARD">E-Voucher<#else>${uiLabelMap.OrderAddToCart}</#if></a>&nbsp;-->
											</td>
											</#if>
										
										</tr>
										</table>
										</#if>
								      	<#if requestParameters.category_id?exists>
								            <input type="hidden" name="category_id" value="${requestParameters.category_id}"/>
								     	</#if>
					        		</#if>
					       		</div>
					       	</div>
					       	<#if product.isVirtual?if_exists?upper_case == "Y">
					       		<div style="margin-top:5px; margin-bottom:5px; height:22px;">
				      				<div style="float:left; line-height:22px;font-weight:bold;">
					              		<span id="product_id_display"></span>
					            	</div>
					            	<div style="line-height:22px;font-weight:bold;">
					              	
					            	</div>
				      			</div>
			      			</#if> 
			      			</div>
			      		
				      	</form>
				      	
				      	<div style="float:left; width:100px;margin-left:-140px;">
						<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px;float:left;">
							<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:<#if  averageRating?exists>${(averageRating*12)?if_exists}<#else>0</#if>px; height:11px">
							</div><br/> 
					   	</div>
					   	<#if numRatings?exists>
					   	<div style="float:left;margin-left: 10px;margin-top: -1px;">  (${numRatings?if_exists})</div>
					   	</#if>
					   	</div>
	<div style="clear:both;">
	<#if product?has_content && product.productId=="GIFTCARD">
	<div style="position:absolute; right:0px; top:378px;">
	<#else>
	<div style="position:absolute; float:left; right:0px; top:56px;">
	</#if>
		
				      	<#if sessionAttributes.userLogin?has_content && sessionAttributes.userLogin.userLoginId != "anonymous">
            <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToFavouriteList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
              <input type="hidden" name="productId" id="productId" value="${product.productId}" />
              <input type="hidden" name="product_id" value="${product.productId}" />
              <input type="hidden" name="productStoreId" id="productStoreId" value="${productStoreId}" />
              <input type="hidden" name="reservStart" value= "" />
               <#if product.productId?has_content && (product.productId !="GIFTCARD")>
              <select name="shoppingListId" id="shoppingListId" style="height:24px; width:165px;">
                <#if shoppingLists?has_content>
                  <#list shoppingLists as shoppingList>
                    <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                  </#list>
                </#if>
                <#--option value="">---</option>
                <option value="">${uiLabelMap.OrderNewShoppingList}</option-->
              </select>
              </#if>
              
              <#if product.productTypeId?if_exists == "ASSET_USAGE">
                  &nbsp;${uiLabelMap.CommonStartDate} (yyyy-mm-dd)<input type="text" size="10" style="width:20px;" name="reservStartStr" />Number of&nbsp;days<input type="text" size="4" name="reservLength" />&nbsp;Number of&nbsp;persons<input type="text" size="4" name="reservPersons" value="1" />Qty&nbsp;<input type="text" size="5" name="quantity" value="1" />
              <#else><br/>
                <div style="margin-top:10px;">
                  <#if product.productId?has_content && (product.productId !="GIFTCARD")>
                  <input type="text" style="width:20px;" size="5" name="quantity" id="quantity" value="1" /> </#if>
                  <input type="hidden" name="reservStartStr" value= "" />
              
              </#if>
              <#if product.productId?has_content && (product.productId !="GIFTCARD")>
                  <a href="javascript:addShoplistSubmit();" class="buttontextskyblue">Add To Favourite List</a>
                  </#if>
                </div>
                
            </form>
          <#else> <br />
            <#--${uiLabelMap.OrderYouMust} <a href="<@ofbizUrl>checkLogin/showcart</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBeLogged}</a-->
            <!--${uiLabelMap.OrderToAddSelectedItemsToShoppingList}.&nbsp;-->
          </#if>
	         <#if product.productId?has_content && (product.productId !="GIFTCARD")>
				<div style="float:left; text-align:left; margin-top:20px;">
	                  <a href="javascript:popUpSmall('<@ofbizUrl>tellafriend?productId=${product.productId}</@ofbizUrl>','tellafriend');" class="buttontextgreen">${uiLabelMap.CommonTellAFriend}</a>
	            </div>
	        </#if>
        </div>
        </div>
        </div>
        </div>
          <#-- Prefill first select box (virtual products only) -->
          <#if variantTree?exists && 0 &lt; variantTree.size()>
            <script type="text/javascript">eval("list" + "${featureOrderFirst}" + "()");</script>
            
          </#if>
          <#-- Swatches (virtual products only) -->
          
      <#-- Digital Download Files Associated with this Product -->
      <#if downloadProductContentAndInfoList?has_content>
        <div id="download-files">
          <div>${uiLabelMap.OrderDownloadFilesTitle}:</div>
          <#list downloadProductContentAndInfoList as downloadProductContentAndInfo>
            <div>${downloadProductContentAndInfo.contentName?if_exists}<#if downloadProductContentAndInfo.description?has_content> - ${downloadProductContentAndInfo.description}</#if></div>
          </#list>
        </div>
      </#if>
    <div class="clear"></div>
      <#-- Long description of product -->
      <div id="long-description">
      <#--<div>${productContentWrapper.get("LONG_DESCRIPTION")?if_exists}</div>
          <div>${productContentWrapper.get("WARNINGS")?if_exists}</div>-->
      </div>
      
	<#if product.ingredients?has_content>
      	<div class="ingredients">
			${StringUtil.wrapString(product.ingredients)?if_exists}
		</div>
	</#if>
	      	
	<#if product.nutritionalFacts?has_content>
	  	<div class="nutritional_facts">
			${StringUtil.wrapString(product.nutritionalFacts)?if_exists}
		</div>
	</#if>
	  
          
      
    
      <#-- Any attributes/etc may go here -->
    
      <#-- Product Reviews -->
	         <div id="reviews">
	          <div class="recipe-detail"><h2>${uiLabelMap.OrderCustomerReviews}:</h2></div>
	     
	          <#--div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px">
					<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:<#if  averageRating?exists>${(averageRating*12)?if_exists}<#else>0</#if>px; height:11px">
					(${numRatings?if_exists}</div><br/> 
			   </div-->
			   
			     <#--if averageRating?exists && (averageRating &gt; 0) && numRatings?exists && (numRatings &gt; 1)>
	              <div>${uiLabelMap.OrderAverageRating}: ${averageRating} <#if numRatings?exists>(${uiLabelMap.CommonFrom} ${numRatings} ${uiLabelMap.OrderRatings})</#if></div>
	          	</#if-->
	     
	     
	          <#if productReviews?has_content>
	            <div id="opendiv" class="opendiv_reviews">
		            <#list productReviews as productReview>
		              <#assign postedUserLogin = productReview.getRelatedOne("UserLogin") ?if_exists/>
		              <#if postedUserLogin?has_content>
		              <#assign postedPerson = postedUserLogin.getRelatedOne("Person")?if_exists />
		              </#if>
		              <div style="border-bottom: 2px solid #DEDFBF;padding: 5px 0;float: left;width: 100%;">
				           	<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px;float:left;">
							<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:<#if productReview.productRating?exists && (productReview.productRating &gt; 0)>${(productReview.productRating*12)?if_exists}<#else>0</#if>px; height:11px">
							</div><br/> 
					   		</div>
 	          		  		  	<div style="float: left;margin: -2px 5px 0 10px;">${productReview.postedDateTime?if_exists?date?string.medium}&nbsp;</div>
		                        <div style="float:right;"><i class="fa fa-user"></i> <#if productReview.postedAnonymous?default("N") == "Y"> ${uiLabelMap.OrderAnonymous}<#else> ${postedPerson.firstName?if_exists} ${postedPerson.lastName?if_exists}&nbsp;</#if></div>
		                        <#--div><strong>${uiLabelMap.OrderRanking}: </strong>${productReview.productRating?if_exists?string}</div-->
		                        <div>&nbsp;</div>
		                        <div style="margin: 10px 5px;font-size: 12px;text-transform: capitalize;text-align: justify;">${productReview.productReview?if_exists}</div>
		               </div>    
		            </#list>
	        	</div>     
	            <div style="clear:both;"></div>
	            <div style="margin: 10px 0;">
	                <a href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductReviewThisProduct}!</a>
	            </div>
	          <#else>
	            <div style="margin-top: -16px;">${uiLabelMap.ProductProductNotReviewedYet}.</div>
	            <div style="margin: 10px 0;">
	                <a href="<@ofbizUrl>reviewProduct?category_id=${categoryId?if_exists}&amp;product_id=${product.productId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductBeTheFirstToReviewThisProduct}</a>
	            </div>
	        </div>
	      </#if> 
    <#-- Upgrades/Up-Sell/Cross-Sell
      <#macro associated assocProducts beforeName showName afterName formNamePrefix targetRequestName>
      <#assign pageProduct = product />
      <#assign targetRequest = "product" />
	      <#if targetRequestName?has_content>
	        <#assign targetRequest = targetRequestName />
	      </#if>
      <#if assocProducts?has_content>
        <h2>${beforeName?if_exists}<#if showName == "Y">${productContentWrapper.get("PRODUCT_NAME")?if_exists}</#if>${afterName?if_exists}</h2>
    
        <div class="productsummary-container">
        <#list assocProducts as productAssoc>
            <#if productAssoc.productId == product.productId>
                <#assign assocProductId = productAssoc.productIdTo />
            <#else>
                <#assign assocProductId = productAssoc.productId />
            </#if>
            <div>
              <a href="<@ofbizUrl>${targetRequest}/<#if categoryId?exists>~category_id=${categoryId}/</#if>~product_id=${assocProductId}</@ofbizUrl>" class="buttontextblue">
                ${assocProductId}
              </a>
            <#if productAssoc.reason?has_content>
              - <strong>${productAssoc.reason}</strong>
            </#if>
            </div>
          ${setRequestAttribute("optProductId", assocProductId)}
          ${setRequestAttribute("listIndex", listIndex)}
          ${setRequestAttribute("formNamePrefix", formNamePrefix)}
          <#if targetRequestName?has_content>
            ${setRequestAttribute("targetRequestName", targetRequestName)}
          </#if>
              ${screens.render(productsummaryScreen)}
          <#assign product = pageProduct />
          <#local listIndex = listIndex + 1 />
        </#list>
        </div>
    
        ${setRequestAttribute("optProductId", "")}
        ${setRequestAttribute("formNamePrefix", "")}
        ${setRequestAttribute("targetRequestName", "")}
      </#if>
    </#macro -->
    
    <#assign productValue = product />
    <#assign listIndex = 1 />
    ${setRequestAttribute("productValue", productValue)}
    <#--div id="associated-products" style="float:left; margin-top:15px; width:100%">
       
        <@associated assocProducts=alsoBoughtProducts beforeName="" showName="N" afterName="${uiLabelMap.ProductAlsoBought}" formNamePrefix="albt" targetRequestName="" />
       
        <@associated assocProducts=obsoleteProducts beforeName="" showName="Y" afterName=" ${uiLabelMap.ProductObsolete}" formNamePrefix="obs" targetRequestName="" />
        
        <@associated assocProducts=crossSellProducts beforeName="" showName="N" afterName="${uiLabelMap.ProductCrossSell}" formNamePrefix="cssl" targetRequestName="crosssell" />
         
        <@associated assocProducts=upSellProducts beforeName="${uiLabelMap.ProductUpSell} " showName="Y" afterName=":" formNamePrefix="upsl" targetRequestName="upsell" />
      
        <@associated assocProducts=obsolenscenseProducts beforeName="" showName="Y" afterName=" ${uiLabelMap.ProductObsolescense}" formNamePrefix="obce" targetRequestName="" />
    </div-->
    
    <#-- special cross/up-sell area using commonFeatureResultIds (from common feature product search) -->
    <#if comsmonFeatureResultIds?has_content>
        <h2>${uiLabelMap.ProductSimilarProducts}</h2>
    
        <div class="productsummary-container">
            <#list commonFeatureResultIds as commonFeatureResultId>
                ${setRequestAttribute("optProductId", commonFeatureResultId)}
                ${setRequestAttribute("listIndex", commonFeatureResultId_index)}
                ${setRequestAttribute("formNamePrefix", "cfeatcssl")}
                <#-- ${setRequestAttribute("targetRequestName", targetRequestName)} -->
                ${screens.render(productsummaryScreen)}
            </#list>
        </div>
    </#if>
    </div>
</div>



<#--if relatedProductList?has_content && relatedProductCategoryName?has_content>
	<div style=" position:relative">
		<h1 style="background:#9aae27; padding:14px 5px 14px 5px; margin:2px">More Products from ${relatedProductCategoryName}</h1>
	<div>
</#if>
<#if relatedProductList?has_content>
	 <#assign numCol = numCol?default(4)>
      <#assign numCol = numCol?number>
      <#assign tabCol = 1>
    <div class="productsummary-container <#if (numCol?int > 1)> matrix</#if>">
    	 <#if (numCol?int > 1)>
	        <table>
	      </#if>
        <#list relatedProductList as relatedProduct> <#-- note that there is no boundary range because that is being done before the list is put in the content >
           <#if (numCol?int == 1)>
            ${setRequestAttribute("optProductId", relatedProduct.productId)}
            ${screens.render(productsummaryScreen)}
            <#else>
              <#if (tabCol?int = 1)><tr></#if>
                  <td>
                   ${setRequestAttribute("optProductId", relatedProduct.productId)}
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
</#if-->



<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
<script type="text/javascript">
function addZeroPrefixGiftCard(){
	var phoneNo=document.getElementById('recipient_mobile');
	if(phoneNo.value.length>0){
            
	    if(checkNo(phoneNo.value)==false)
	    {
	    	alert("Your Mobile Number must be 10 to 12 digits.");
	    	phoneNo.value="";
	    	document.getElementById('recipient_mobile').focus();
	    }
	    else{
			if(phoneNo.value[0] != "0"){
				document.getElementById('recipient_mobile').value = "0"+document.getElementById('recipient_mobile').value;
			}
		}	
	}
}

  
function addItems(add_product_id,add_product_name){
    var value= document.addform.add_product_id.value;
    if(value == 'NULL' || value == ""){
      alert("Please select pack size");
    }else{
		addItemsCart(add_product_name);
	}
}    
    
function addItems_old(add_product_id) {alert(add_product_id);
	var value= document.addform.add_product_id.value;
    if(value == 'NULL' || value == ""){
      alert("Please select pack size");
    }else{
		addItemsCart();
	}
}
    
function addItemsCart(add_product_name) {
    var add_product_id= document.addform.add_product_id.value;
    if(add_product_id!="" && add_product_id.match('GIFTCARD')){
    	var flag = checkCartItems();
    	if(!flag)return flag;
    	
    	var recipientName= document.addform.recipientName;
    	var recipientMobile= document.addform.recipientMobileNum;
    	var recipientemail= document.addform.recipientEmailId;
    	var message= document.addform.message;
    	
    	var recipientNameParam= document.addform.recipientName.value;
    	var recipientMobileParam= document.addform.recipientMobileNum.value;
    	var recipientemailParam= document.addform.recipientEmailId.value;
    	var messageParam= document.addform.message.value;
    	
    	if((recipientName.value)==""){
		 	alert("Please enter recipient name!");
		 	recipientName.focus();
		 	return false; 	
		}
		if((recipientMobile.value)==""){
		 	alert("Please enter mobile number!");
		 	recipientMobile.focus();
		 	return false; 	
		}
		if (checkNo(recipientMobile.value)==false){
            alert("Mobile Number must be 10 to 12 digits!");
            recipientMobile.focus();
			return false;
		}
		
		if((recipientemail.value)==""){
		 	alert("Please enter recipient email id!");
		 	recipientemail.focus();
		 	return false; 	
		}
		if (checkEmail(recipientemail.value)==false){
            recipientemail.value=""
            alert("Invalid Email Address!");
            recipientemail.focus();
            return false;
	    }
  	    if((message.value)==""){
		 	alert("Please enter message!");
		 	message.focus();
		 	return false; 	
		}
    	var quantity= document.addform.quantity.value;
    	var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity + '&recipientName=' + recipientNameParam + '&recipientMobile=' + recipientMobileParam + '&recipientemail=' + recipientemailParam + '&message=' + messageParam;
          window.location = "/control/additem?"+param;
          return;
    }
    var quantity= document.addform.quantity.value;
    if (document.addform.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
    } else {
       var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity + '&recipientName=' + recipientNameParam + '&recipientMobile=' + recipientMobileParam + '&recipientemail=' + recipientemailParam + '&message=' + messageParam;
       jQuery.ajax({url: "/control/additem",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
	         if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
	         {
	          alert(data);
	          	 var formName = "";
	             var flag_new = true;
	          	 var newFlag = checkForGiftCard(data,formName);
	          	 
	          	 if(!newFlag)return;
	          	 
	          	 if(data == "Can't add more than giftcard") {
	          	 var dataTemplate=data.replace("Can not","Can't");
	          	 alert(dataTemplate);
	          	 	
	          	 }
	           return;
	          }else{
	          		if(document.getElementById('variant_product_name') != null ){
	          			if(document.getElementById('variant_product_name').innerHTML != ''){
	                		add_product_name = document.getElementById('variant_product_name').innerHTML; 
	                	} 
	                }
	          		cart_top_notification(add_product_name);
	                document.getElementById('microcart').innerHTML=data; 
	         }
         },
         complete:  function() { 
         },
         error: function(data) {
         }
        }); 
    }   
}
    
    
    function checkCartItems(){
    	var  param = "";
        var  flag = true;               
     jQuery.ajax({url: '/control/canBuyGiftCard',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         	if("false" == data)
         	{
         		alert("Please complete your pending order in the cart, gift an e Voucher can be bought only independently.");
         		flag = false;
         	}
         },
         complete:  function() { 
         },
         error: function(data) {
         }
    	});
    	return flag;
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
    }, 1800);
    
    
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    //cartsummary1();
    
   }
   
   function HideDialog()
   {
      $("#overlay").hide();
      $("#dialog").fadeOut(300);
   }
   <#if variantTree?exists && 0 &lt; variantTree.size()>
   <#if product.productId="GIFTCARD">
   		 window.onload=getListfirst('FTAMOUNT', '0', '0');
   <#else>
    window.onload=getListfirst('${request.getParameter('name')?if_exists}', '${request.getParameter('index')?if_exists}', '${request.getParameter('src')?if_exists}');
    </#if>
    </#if>
    
     function checkEmail(eMail) {
		if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(eMail)){
		return true;
		 }
			return false; 
		 } 
          
	function checkNo(number)
	{ 
		var number_len = number.length;
		if((number_len < 10 || number_len > 12))
		{
			return false;
		}
			return true;
	}
    
    
    
    </script>
    
    
     <script type="text/javascript" src="/erptheme1/h-slide/lib/jquery.jcarousel.min.js" async></script>	
        <link rel="stylesheet" type="text/css" href="/erptheme1/h-slide/skins/tango/skin.css" />
    
		<script type="text/javascript">
		jQuery(document).ready(function() {
		    jQuery('#mycarouse14, #mycarouse15').jcarousel();
		});
		</script>
<div id ="loading-cont" style="display:none">
<div id="pleaseWait" style=" font-size:30px; padding-top:30px; padding-left:20px;"><img src="/images/loader.gif"><br/>Loading</div>
<div id="washout" style=""></div>
</div>		
		<#if categoryList?has_content>
		<div style=" width:1024px; border-bottom:1px solid #ccc; padding-bottom:5px; margin-bottom:10px;"><h1>You may also want to Buy: </h1></div>
		
			    <div  class="screenlet" style=" border:1px dashed #ccc; width:1024px; height:275px; background-color:#fcfcfc;">
					<#-- <ul id="mycarouse14" class="jcarousel-skin-tango" style="float:left; width:940px; padding:0px 0px 20px 0px;"> -->
					<div id="productsFromCategory">
						<div class="homeleft">
					
				  		<#if preAvail>
				  			<a href="javascript:showProductsFromCategory('${productsFromCategoryPrevIndex?if_exists?default(-1)}','${categoryId?if_exists}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
				  		<#else>
				  			<img src="/multiflex/left_arrow.png" alt=""/>
				  		</#if>
				  		</div>
						<#assign count=1/>
						<div class="slideproduct">
						  	<#list categoryList as mv>
						  	<div class="shadow" style="height:275px; width:200px !important; float:left; margin:6px; background-color:#fff; padding:5px 10px 5px 10px; border:1px solid #cccccc; background-image: url(/erptheme1/backgroundproduct.jpg); background-repeat:repeat-x; background-position:left; position:relative">
							  	 ${setRequestAttribute("optProductId1", mv.productId?if_exists)}
						         <div style="border:none !important;">   ${screens.render(impulsiveproductsummaryScreen)}</div>
						         <#assign count = count + 1 />
							 </div>
							 
						  	 </#list>
						 </div>
						 <div class="homeleft"> 	
						<#if nextAvail>
							<a href="javascript:showProductsFromCategory('${productsFromCategoryNextIndex?if_exists?default(1)}','${categoryId?if_exists}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
						<#else>
							<img src="/multiflex/right_arrow.png" alt=""/>
						</#if>
						</div>
					<#-- </ul> -->
					<div style="clear:both;"> </div>
					</div>
				</div>
				 </#if>
				
		<#if brandList?has_content>
			<div style=" width:1024px; border-bottom:1px solid #ccc; padding-bottom:5px; margin:20px 0 10px 0;"><h1>More products from "${bName?if_exists}" : </h1></div>
			
				  <div class="screenlet" style=" border:1px dashed #ccc; width:1024px; height:275px; background-color:#fcfcfc;">
					<#--ul id="mycarouse15" class="jcarousel-skin-tango" style="float:left; width:940px; "-->
					<div id="productsFromBrand">
						<div class="homeleft">
				  		<#if preAvailBrand>
				  			<a href="javascript:showProductsFromBrand('${productsFromBrandPrevIndex?if_exists?default(-1)}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
				  		<#else>
				  			<img src="/multiflex/left_arrow.png" alt=""/>
				  		</#if>
				  		</div>
						<#assign count1=1/>
						<div class="slideproduct">
						  	<#list brandList as mv1>
							  	<div class="shadow" style="height:275px; width:200px !important; float:left; margin:6px; background-color:#fff;   padding:5px 10px 5px 10px; border:1px solid #cccccc;  background-image: url(/erptheme1/backgroundproduct.jpg); background-repeat:repeat-x; background-position:left; position:relative">
								  	 ${setRequestAttribute("optProductId2", mv1.productId?if_exists)}
							         <div style="border:none !important;">   ${screens.render(impulsiveproductsummaryScreen1)}</div>
							         <#assign count1 = count1 + 1 />
								 </div>
						  	 </#list>
						 </div>
						<div class="homeleft">
						<#if nextAvailBrand>
							<a href="javascript:showProductsFromBrand('${productsFromBrandNextIndex?if_exists?default(1)}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
						<#else>
							<img src="/multiflex/right_arrow.png" alt=""/>
						</#if>
						</div>
					<#-- </ul> -->
					 <div style="clear:both;"> </div> 
					 </div>
				</div>
				</#if>
				
</div>
  
<script type="text/javascript">
	function showProductsFromCategory(productsFromCategoryIndex,category_id){
   			var url="/control/showProductsFromCategoryAjax?productsFromCategoryIndex="+productsFromCategoryIndex+"&category_id="+category_id;
   			//var productId = "124657";
   			//var url="http://localhost:8080/rest/WebService/GetFeeds?productId="+productId+"&prdsFromCatIndex="+productsFromCategoryIndex;
			pleaseWait('Y');
			jQuery.ajax({url: url,
		        data: null,
		        type: 'get',
		        async: true,
		        success: function(data) {
	          		$('#productsFromCategory').html(data);
	  			},
				complete:  function() {
				  	 pleaseWait('N');
				},
		        error: function(data) {
		            alert("Error during product filtering");
		        }
    		});
		}
		
		function showProductsFromBrand(productsFromBrandIndex){
   			var url="/control/showProductsFromBrandAjax?productsFromBrandIndex="+productsFromBrandIndex;
   			//var productId = "124657";
   			//var url="http://localhost:8080/rest/WebService/GetFeeds?productId="+productId+"&prdsFromCatIndex="+productsFromCategoryIndex;
			pleaseWait('Y');
			jQuery.ajax({url: url,
		        data: null,
		        type: 'get',
		        async: true,
		        success: function(data) {
	          		$('#productsFromBrand').html(data);
	  			},
				complete:  function() {
				  	 pleaseWait('N');
				},
		        error: function(data) {
		            alert("Error during product filtering");
		        }
    		});
		}
		
		function pleaseWait(wait){
	
				if (wait == "Y") {
					var CatH =$('.newheightcon').height();;
					document.getElementById('washout').style.height=CatH+"px";
					//document.getElementById('loading-cont').style.display="block";
					$('#loading-cont').show();
					//$('#pleaseWait').show();
					
					
				}else{
					$('#loading-cont').hide();
					//$('#washout').hide();
				}
			}
 function submitRecipientForm()
 {
	recipientName = document.getElementById('recipient_name');
	recipientMobile=document.getElementById('recipient_mobile');
	recipientemail=document.getElementById('recipient_email');
	message=document.getElementById('message');
 
		 if((recipientName.value)=="")
		 {
		 	alert("Please enter recipient name!");
		 	recipientName.focus();
		 	return false; 	
		 }
		  if((recipientMobile.value)=="")
		 {
		 	alert("Please enter mobile enumber!");
		 	recipientMobile.focus();
		 	return false; 	
		 }
		 if (checkNo(recipientMobile.value)==false){
            alert("Mobile Number must be 10 to 12 digits!");
            recipientMobile.focus();
			return false;
		 }
		
		 if((recipientemail.value)=="")
		 {
		 	alert("Please enter recipient email id!");
		 	recipientemail.focus();
		 	return false; 	
		 }
		 if (checkEmail(recipientemail.value)==false){
	            recipientemail.value=""
	            alert("Invalid Email Address!");
	            recipientemail.focus();
	            return false;
	      }
			            
		  if((message.value)=="")
		 {
		 	alert("Please enter message!");
		 	message.focus();
		 	return false; 	
		 }
 }
	function checkEmail(eMail) {
	if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(eMail)){
		return true;
	 }
		return false; 
	 } 
          
	function checkNo(number)
	{ 
		var number_len = number.length;
		if((number_len < 10 || number_len > 12))
		{
			return false;
		}
			return true;
	}

	function isNumberKey(evt)
	      {
	         var charCode = (evt.which) ? evt.which : event.keyCode
	         if (charCode > 31 && (charCode < 48 || charCode > 57))
	            return false;
	              
	         return true;
	      }

</script>
    
   
    
<style>
.shadow{
border:none;
border: 1px solid #ffffff;
background:url("/erptheme1/backgroundproduct.jpg") repeat-x top 2px left;
border-radius:5px;
-moz-border-radius:5px;
}

.shadow:hover{
border: 1px solid #ccc;
box-shadow: 0 1px 12px 0 #B4B4B4;
-moz-box-shadow:0 1px 8px 0 #B4B4B4;
-webkit-box-shadow:0 1px 8px 0 #B4B4B4;
border-radius:5px;
-moz-border-radius:5px;
}
</style>