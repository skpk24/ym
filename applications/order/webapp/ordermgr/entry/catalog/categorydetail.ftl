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

<div id="newdiv3" class="center">
<script type="text/javascript">
$(document).ready(function () {    
var elem=$('#containerList ul');      
	$('#viewcontrols a').on('click',function(e) {
		if ($(this).hasClass('gridview')) {
			elem.fadeOut(1000, function () {
				$('#containerList ul').removeClass('list').addClass('grid');
				$('#viewcontrols').removeClass('view-controls-list').addClass('view-controls-grid');
				$('#viewcontrols .gridview').addClass('active');
				$('#viewcontrols .listview').removeClass('active');
				elem.fadeIn(1000);
			});						
		}
		else if($(this).hasClass('listview')) {
			elem.fadeOut(1000, function () {
				$('#containerList ul').removeClass('grid').addClass('list');
				$('#viewcontrols').removeClass('view-controls-grid').addClass('view-controls-list');
				$('#viewcontrols .gridview').removeClass('active');
				$('#viewcontrols .listview').addClass('active');
				elem.fadeIn(1000);
			});									
		}
	});
});
function gridListMan(value){

if (value == "grid") {
			
				$('#containerList ul').removeClass('list').addClass('grid');
				
									
		}
	if(value == "list") {
	
			$('#containerList ul').removeClass('grid').addClass('list');
			
										
		}
		}
		
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


<#if virtualJavaScript?has_content>
	${virtualJavaScript}
</#if>
<#if productCategoryMembers?has_content>
	<div id="productIds123">
		<#assign productIds1 = "">
		<#list productCategoryMembers as productCategoryMember>
			<#assign productIds1 = productIds1+productCategoryMember.productId+",">
		</#list>
		<input type="hidden" value="${productIds1?if_exists}" id="productIds1">
	</div>
</#if>

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
              </script>
<script language="JavaScript" type="text/javascript">
function isNumberKey(evt,productId)
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


function getList(name, index, src, formName) {

var AA= window["IMG"+formName];


  currentFeatureIndex = findIndex(name);
  	 var ABC =document.getElementById('ABlink'+formName).href;
  
  	 var data = ABC.split("?")
  	 ABC = data[0];
  	// alert(data[0])
  	
	var quantity =document.getElementById('qty'+formName).value;
 		ABC=ABC+"?name="+name+"&index="+index+"&src="+src+"&quantity="+quantity;
 	
 	 
 	document.getElementById('ABlink'+formName).href =ABC;
 	document.getElementById('desclink'+formName).href =ABC;
 	 document.getElementById('rating_'+formName).href =ABC;
 	
 	
 	
        if (currentFeatureIndex == 0) {
            // set the images for the first selection
         if (AA[index] != null){
        
                if (document.images['A'+formName] != null) {
               		//alert("small====="+AA[index]);
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
            alert("selectedValue"+selectedValue)
            if (index == -1) {
              <#if featureOrderFirst?exists>
              alert("feature alert");
                var Variable1 = eval("list" + "${featureOrderFirst}" + "()");
              </#if>
            } else {
             alert("feature alert 2");
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
            //alert("sku"+skuName);
           checkInventory(sku, formName);
            // display alternative packaging dropdown
           // ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId(sku,formName);

            // set the variant price
            
            
            setAddProductNames(skuName,sku,formName);
            setVariantPrice(sku,formName);
			 
			if( document.forms['addform'+formName] && document.forms['addform'+formName].add_product_name != null){   
				var prodName  = document.getElementById('productNameDescrip_'+ formName).innerHTML; 
				document.forms['addform'+formName].add_product_name.value = prodName + skuName;  				
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
    
    function addItems(add_product_id,add_product_id1, formName) {
    
   

	var value= document.getElementById(add_product_id1).value;
            if(value == 'NULL' || value == "")
            {
            alert("Please select pack size");
            
            }else{
		addItem(formName);
}
    }
    
    function addItem(formName){
        var add_product_id= formName.add_product_id.value;
        var add_product_name = formName.add_product_name.value;
        
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
		           var  param = 'add_product_id=' + add_product_id +'&quantity=' + quantity;
		           jQuery.ajax({url: '/control/additem',
		         				data: param,
		         				type: 'post',
		         				async: true,
		         				success: function(data) {	
		         				
		         				    if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1)){
		         				        alert(data);
                                	}else{
                                	    cart_top_notification(add_product_name);
                                		document.getElementById('microcart').innerHTML=data;                                		 
                                	}
		         			    },
                                error: function(data) {
                                }    	
                   });
	    	   }
	    }
    	
    } 
    
    
    
    
    
    function addItem1(formName) {
    var add_product_id= formName.add_product_id.value;
    var quantity= formName.quantity.value;
    
    if(quantity > 20)
    {
    alert("Please select quantity less than 20"); 
    }else
    {
    
    var clearSearch= formName.clearSearch.value;
       if (formName.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       }else {
           var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity;
                      jQuery.ajax({url: '/control/additem',
         data: param,
         type: 'post',
         async: true,
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
          
           jQuery.ajax({url: '/control/findprodwgt',
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
    	
    	//setTimeout(function() {
      //location.reload();
    //}, 1000);
    }}
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
 
 function getVariantPrice(variantId){
 	
 }
 
</script>
<input type="hidden" id="filterByBrand" value="${filterByBrand?if_exists}"/>
<script>
	function favBrowser(){
	pleaseWait('Y');
		var selectedValue = document.getElementById("mySelect").value;
	  
	  var productCategoryId = document.pageSelect.category_id.value;
	 var viewSize = document.pageSelect.VIEW_SIZE.value;
			
  			var limitView = 'true';
  			 var viewIndex = document.pageSelect.VIEW_INDEX.value;
  			  var categorydetail=document.getElementById("detailScreen").value;
  			  if(categorydetail=='categorydetail'){
	  		 numCol =1;}else{
	  		 numCol=4;
	  		 }
	  		 var filterByBrand = document.getElementById("filterByBrand").value;
	   var url="/control/ajaxCategoryDetail?filterByBrand="+filterByBrand+"&filterByPrice=${parameters.filterByPrice?if_exists}&category_id=${productCategoryId?if_exists}&VIEW_SIZE="+viewSize+"&limitView="+limitView+"&filterBy="+selectedValue+"&VIEW_INDEX="+viewIndex+"&numCol="+numCol+"";
	
 jQuery.ajax({url: url,
	        data: null,
	        type: 'post',
	        async: true,
	        success: function(data) {
	       
	      
	          $('#searchResult').html(data);
	           var productIds1 = document.getElementById("productIds1").value;
	       var result = productIds1.split(",");
	       for(var i=0;i<result.length;i++)
	       {
	       		var isVitrualMethod = "isVirtual"+result[i]+ "();";
	       		var isVitrual= eval(isVitrualMethod);
	       		if(isVitrual)
	       		{
		       		var listNetFun = "listNET_WEIGHT"+result[i]+ "();";
		       		eval(listNetFun);
		       		getList('FTNET_WEIGHT', '0', '0',result[i]);
	       		}
	       }
		  },
		  complete:  function() {
		  	pleaseWait('N');
		  },
	        error: function(data) {
	            alert("Error during product filtering");
	        }
	    });   
	}
	function callDocumentByPaginate(info) {
	
	
	var ListGridVal = $('#containerList ul').attr('class');
	 
	pleaseWait('Y');

        var str = info.split('~');
       
        var checkUrl = '<@ofbizUrl>categoryAjaxFired</@ofbizUrl>';
       
        if(checkUrl.search("http"))
            var ajaxUrl = '<@ofbizUrl>categoryAjaxFired</@ofbizUrl>';
          
        else
            var ajaxUrl = '<@ofbizUrl>categoryAjaxFiredSecure</@ofbizUrl>';
         
     
        var refineByPrice = document.filterByPriceForm.filterByPrice.value;
     
        var refineByBrand = document.filterByPriceForm.filterByBrand.value;
     
        if(refineByPrice == ""){
        	checkPriceFilter();
        	refineByPrice = document.filterByPriceForm.filterByPrice.value;
        }
        if(refineByBrand == "" ){
        	checkBrandFilter();
        	refineByBrand = document.filterByPriceForm.filterByBrand.value;
        }
        
        
        
        var filterBy = document.getElementById("mySelect").value;
        
        var VIEW_SIZE = str[1];
        var VIEW_INDEX = str[2];
        if(clicked)
        {
        	VIEW_SIZE = 24;
            VIEW_INDEX = 1;
        }
        
        var excludeOutOfStock = "N";
	     if (document.getElementById("excludeOutOfStockCheckbox").checked  == 1) {
	     		excludeOutOfStock = "Y";
	     	}
	     	
	     var detailScreen = "${detailScreen?if_exists}";
        //jQuerry Ajax Request
        jQuery.ajax({
            url: ajaxUrl,
            type: 'POST',
            data: {"category_id" : str[0],"detailScreen" : detailScreen,"excludeOutOfStock" : excludeOutOfStock, "refineByPrice" : refineByPrice, "refineByBrand" : refineByBrand, "filterBy" : filterBy, "VIEW_SIZE" : VIEW_SIZE, "VIEW_INDEX" : VIEW_INDEX},
            error: function(msg) {
                alert("An error occured loading content! : " + msg);
            },
            success: function(msg) {
            
                jQuery('#newdiv3').html(msg);
            },
            complete:  function() {
           		 gridListMan(ListGridVal);
		  		pleaseWait('N');
		  	},
        });
     }
     function unCheckFilter(value){
				document.getElementById(value).checked = false;
				callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int}');
			}
			
			function clearAll(){
				var brandBox = document.getElementsByName('brandBox');
				for (var i=0; i< brandBox.length; i++) {
				   if (brandBox[i].checked) {
				     brandBox[i].checked=false;
					}
				}
				var brandBox = document.getElementsByName('brandBox');
				for (var i=0; i< brandBox.length; i++) {
				   if (brandBox[i].checked) {
				     brandBox[i].checked=false;
					}
				}
				var checkboxes = document.getElementsByName('checkBox');
				for (var i=0; i< checkboxes.length; i++) {
				     if (checkboxes[i].checked) {
				     	checkboxes[i].checked=false;
				     }
				  }
				 //document.filterByPriceForm.filterByPrice.value = "";
        		 //document.filterByPriceForm.filterByBrand.value = "";
				 callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int}');
			}
	function paginateList(viewIndex) {
	
			pleaseWait('Y');
 			var productCategoryId = document.pageSelect.category_id.value;
			var viewSize = document.pageSelect.VIEW_SIZE.value;
  			var limitView = 'true';
		
			 var selectedValue = document.getElementById("filterby").value;
	   var categorydetail=document.getElementById("detailScreen").value;
	
	   if(categorydetail=='categorydetail'){
		   numCol =1;
		   }else{
		   numCol =4;
		    }
		    var filterByBrand = document.getElementById("filterByBrand").value;
		    var url="/control/ajaxCategoryDetail?filterByPrice=${parameters.filterByPrice?if_exists}&filterByBrand="+filterByBrand+"&category_id=${productCategoryId?if_exists}&VIEW_SIZE="+viewSize+"&limitView="+limitView+"&filterBy="+selectedValue+"&VIEW_INDEX="+viewIndex+"&numCol="+numCol+"";
 	
 	
 jQuery.ajax({url: url,
	        data: null,
	        type: 'post',
	        async: true,
	        success: function(data) {
	        //document.getElementById("searchResult").innerHTML = data;
	       $('#searchResult').html(data);
	       var productIds1 = document.getElementById("productIds1").value;
	       var result = productIds1.split(",");
	       for(var i=0;i<result.length;i++)
	       {
	       		var isVitrualMethod = "isVirtual"+result[i]+ "();";
	       		var isVitrual= eval(isVitrualMethod);
	       		if(isVitrual)
	       		{
		       		var listNetFun = "listNET_WEIGHT"+result[i]+ "();";
		       		eval(listNetFun);
		       		getList('FTNET_WEIGHT', '0', '0',result[i]);
	       		}
	       }
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
</script>
<div id="catHeight">
<div id ="loading-cont" style="display:none">
<div id="pleaseWait" style=" font-size:30px; padding-top:30px; padding-left:20px"><img src="/images/loader.gif"><br/>Loading</div>
<div id="washout" style=""></div>
</div>
	<form name="pageSelect" method="get" action="<@ofbizUrl>category</@ofbizUrl>">
		<input type="hidden" name="category_id" value="${productCategoryId?if_exists}"/>
		<input type="hidden" name="filterByPrice" value="${parameters.filterByPrice?if_exists}"/>
		<#if viewSize?exists><input type='hidden' name='VIEW_SIZE' value='${viewSize}'/></#if>
		<#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'/></#if>
		<#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'/></#if>
		<input type='hidden' name='limitView' id="limitView" value="true"/>
		<input type='hidden' name='detailScreen' id="detailScreen" value='${requestParameters.detailScreen?if_exists}'/>
		<#if filterBy?exists><input type='hidden' name='filterby' id='filterby' value='${filterBy?if_exists}'/></#if>
		<#if sortSearchPrice?exists><input type='hidden' name='sortSearchPrice' value='${sortSearchPrice?if_exists}'/></#if>
		<input type='hidden' name='VIEW_INDEX' id='VIEW_INDEX'/>
	</form>
	
		
<#--macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
      <#if (viewIndexMax?int > 0)>
        <div class="product-prevnext">
            <#-- Start Page Select Drop-Down ->
            <select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;" style="padding:0px !important;">
                <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
                <#list 0..viewIndexMax as curViewNum>
                       <option value="javascript:paginateList('${curViewNum?int + 1}');">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
                </#list>
            </select>
            <#-- End Page Select Drop-Down->
            <#if (viewIndex?int > 1)> 
               <a href="javascript:paginateList('${viewIndex?int - 1}');">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex?int < listSize?int>
             | <a href="javascript:paginateList('${viewIndex?int + 1}');">${uiLabelMap.CommonNext}</a>
            </#if>
        </div>
        <#else>
         
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
        </div>    
    </#if>
</#macro--> 

<#macro paginationControls>
    <#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize - 1)?double / viewSize?double)>
    <div class="product-prevnext">
      <#if (viewIndexMax?int > 0)>
            <#-- Start Page Select Drop-Down -->
            <#-- select name="pageSelect" onchange="window.location=this[this.selectedIndex].value;">
                <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
                <#list 0..viewIndexMax as curViewNum>
                     <option value="<@ofbizCatalogAltUrl productCategoryId=productCategoryId viewSize=viewSize viewIndex=(curViewNum?int + 1)/>">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
                </#list>
            </select -->
            
            <select name="pageSelect" onchange="callDocumentByPaginate(this[this.selectedIndex].value);">
                <option value="#">${uiLabelMap.CommonPage} ${viewIndex?int } ${uiLabelMap.CommonOf} ${viewIndexMax + 1}</option>
                <#list 0..viewIndexMax as curViewNum>
                <#if viewIndex!=curViewNum + 1>
                     <option value="${productCategoryId}~${viewSize}~${curViewNum?int + 1}">${uiLabelMap.CommonGotoPage} ${curViewNum + 1}</option>
               </#if>
                </#list>
            </select>
            <#-- End Page Select Drop-Down -->
            <#if (viewIndex?int > 1)>
                <#-- a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int - 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonPrevious}</a --> |
                <a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int - 1}');" class="buttontext">${uiLabelMap.CommonPrevious}</a> |
            </#if>
            <#if ((listSize?int - viewSize?int) > 0)>
                <span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
            </#if>
            <#if highIndex?int < listSize?int>
             <#-- | <a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int + 1}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonNext}</a -->
             | <a href="javascript: void(0);" onclick="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int + 1}');" class="buttontext">${uiLabelMap.CommonNext}</a>
            </#if>
    <#else>
    	<span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
    </#if>
    </div>
    <div style="clear:both"></div>
</#macro>


<div>

<#--img src="${productCategory.categoryImageUrl?if_exists}"/-->

<div style="float:right; width:835px; position:relative; clear:both;"  class="newheightcon">
<#if productCategory?exists>
    <#assign categoryName = categoryContentWrapper.get("CATEGORY_NAME")?if_exists/>
    <#assign categoryDescription = categoryContentWrapper.get("DESCRIPTION")?if_exists/>
    <#if categoryName?has_content>
        <h1 style="background:#CDCEAF; padding:14px 5px 14px 5px; margin-top:2px">${categoryName}</h1>
        <#if refineByPriceList?has_content || refineByBrandList?has_content || (excludeOutOfStock?has_content  && excludeOutOfStock == "Y")>
<div style=" position:relative" class="bb-filter-bar">
    <span class="filter-head">Applied Selection
	<a href="javascript:clearAll();" class="linktext1233">Clear All</a></span><br/>
</#if>
	   <#if refineByPriceList?has_content>
	   		<#list refineByPriceList as refineByPrice>
	   		  <p class="filter-block">
	   			${refineByPrice?if_exists}
		   			<a href='javascript:unCheckFilter("${refineByPrice?if_exists}");' class="linktext1233">
		   				<img src="/erptheme1/close.png">
		   			</a>
	   		    </p>
	   		</#list>
	   </#if>
	   <#if refineByBrandList?has_content>
	   		<#list refineByBrandList as refineByBrand>
	   		  <p class="filter-block">
	   			${refineByBrand?if_exists}
		   			<a href='javascript:unCheckFilter("${refineByBrand?if_exists}");' class="linktext1233">
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
<#if refineByPriceList?has_content || refineByBrandList?has_content || (excludeOutOfStock?has_content  && excludeOutOfStock == "Y")>
</div>
</#if>
        <div style="float:left;">
        <#if productCategoryMembers?has_content>
        <form>
        
			<select id="mySelect" onchange="callDocumentByPaginate('${productCategoryId}~${viewSize}~${viewIndex?int}')">
				<option value="POPULAR_PRD" <#if filterBy == "POPULAR_PRD" >selected </#if> >Popularity</option>
		 	   	<option value="L_TO_H"<#if filterBy == "L_TO_H" >selected </#if> >Price Low To High</option>
		  		<option value="H_TO_L"<#if filterBy == "H_TO_L" >selected </#if> >Price High To Low</option>
		  		<option value="A_TO_Z"<#if filterBy == "A_TO_Z" >selected </#if> >A To Z</option>
		  		<option value="Z_TO_A"<#if filterBy == "Z_TO_A" >selected </#if> >Z To A</option>
			</select>
		</form>
		</#if>
        </div>
        <div style="position:absolute; top:8px; right:8px; ">
        	<a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int}/~detailScreen=categorydetail</@ofbizUrl>" tooltip="List View" class="tooltip"><img src="/erptheme1/list.png"/></a>
        	<a href="<@ofbizUrl>category/~category_id=${productCategoryId}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${viewIndex?int}/~detailScreen=categorydetailmatrix</@ofbizUrl>" tooltip="Grid View" class="tooltip"><img src="/erptheme1/grid.png"/></a>
        </div>
    </#if>
    <#if categoryDescription?has_content>
        <h1 >${categoryDescription}</h1>
    </#if>
    <#if hasQuantities?exists>
      <form method="post" action="<@ofbizUrl>addCategoryDefaults<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="thecategoryform" style='margin: 0;'>
        <input type='hidden' name='add_category_id' value='${productCategory.productCategoryId}'/>
        <#if requestParameters.product_id?exists><input type='hidden' name='product_id' value='${requestParameters.product_id}'/></#if>
        <#if requestParameters.category_id?exists><input type='hidden' name='category_id' value='${requestParameters.category_id}'/></#if>
        <#if requestParameters.VIEW_INDEX?exists><input type='hidden' name='VIEW_INDEX' value='${requestParameters.VIEW_INDEX}'/></#if>
        <#if requestParameters.SEARCH_STRING?exists><input type='hidden' name='SEARCH_STRING' value='${requestParameters.SEARCH_STRING}'/></#if>
        <#if requestParameters.SEARCH_CATEGORY_ID?exists><input type='hidden' name='SEARCH_CATEGORY_ID' value='${requestParameters.SEARCH_CATEGORY_ID}'/></#if>
        <a href="javascript:document.thecategoryform.submit()" class="buttontext"><span style="white-space: nowrap; ">${uiLabelMap.ProductAddProductsUsingDefaultQuantities}</span></a>
      </form>
    </#if>
    <#--if searchInCategory?default("Y") == "Y">
        <a href="<@ofbizUrl>advancedsearch?SEARCH_CATEGORY_ID=${productCategory.productCategoryId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ProductSearchInCategory}</a>
    </#if-->
    <#assign longDescription = categoryContentWrapper.get("LONG_DESCRIPTION")?if_exists/>
    <#assign categoryImageUrl = categoryContentWrapper.get("CATEGORY_IMAGE_URL")?if_exists/>
    <#if categoryImageUrl?string?has_content || longDescription?has_content>
      
  </#if>
</#if>

<#if productCategoryLinkScreen?has_content && productCategoryLinks?has_content>
    <div class="productcategorylink-container" >
        <#list productCategoryLinks as productCategoryLink>
            ${setRequestAttribute("productCategoryLink",productCategoryLink)}
            ${screens.render(productCategoryLinkScreen)}
        </#list>
    </div>
</#if>
<#if productCategoryMembers?has_content>
	<div style="margin-top:-31px;">
    <@paginationControls/>
    </div>
      <#assign numCol = numCol?default(1)>
      <#assign numCol = numCol?number>
      <#assign tabCol = 1>
      <#--div 
      <#if categoryImageUrl?string?has_content>
        style="position: relative;"
      </#if>
      class="productsummary-container<#if (numCol?int > 1)> matrix</#if>">
      <#if (numCol?int > 1)>
        <table style="float:left; ">
      </#if-->
       <div class="view-controls-grid" id="viewcontrols">
			<a class="gridview active"><i class="fa fa-th fa-2x"></i></a>
			<a class="listview"><i class="anil"></i></a>
		</div>
      <div id="containerList">
     
		<ul class="grid">
        <#list productCategoryMembers as productCategoryMember>
          <#if (numCol?int == 1)>
           <input type="hidden" name="tem" value="${productCategoryMember.productId}"/>
            ${setRequestAttribute("optProductId", productCategoryMember.productId)}
            ${setRequestAttribute("productCategoryMember", productCategoryMember)}
            ${setRequestAttribute("listIndex", productCategoryMember_index)}
            ${screens.render(productsummaryScreen)}
          <#else>
              <#if (tabCol?int = 1)><tr></#if>
                  <li>
                  <input type="hidden" name="tem" value="${productCategoryMember.productId}"/>
                      ${setRequestAttribute("optProductId", productCategoryMember.productId)}
                      ${setRequestAttribute("productCategoryMember", productCategoryMember)}
                      ${setRequestAttribute("listIndex", productCategoryMember_index)}
                      ${screens.render(productsummaryScreen)}
                  </li>
              <#if (tabCol?int = numCol)></tr></#if>
              <#assign tabCol = tabCol+1><#if (tabCol?int > numCol)><#assign tabCol = 1></#if>
           </#if>
        </#list>
      <#--if (numCol?int > 1)>
        </table>
      </#if-->
      </ul>
      </div>
      <div style="clear:both"></div>
    <@paginationControls/>
   
<#else>
    <hr />
     
      ${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#comingSoon")}   
    <#--div>${uiLabelMap.ProductNoProductsInThisCategory}</div-->
</#if>
 </div>
</div>
</div>
<#if productCategoryId?has_content && !(productCategoryId.equals("BUNDLE")) || productCategoryId.equals("DISCOUNTS")>

<div style="float:left; width:180px;">
     ${screens.render("component://ecommerce/widget/ecomclone/CommonScreens.xml#rightcatbar")}
</div>
</#if>
</div>

