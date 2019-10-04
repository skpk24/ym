${virtualJavaScript?if_exists}

<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
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
            setVariantPrice(sku,formName);
            
            setAddProductNames(skuName,sku,formName);

            // check for amount box
            toggleAmt(checkAmtReq(sku));
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
    function addItems(product_ids,add_product_id, formName) { 
		var value= document.getElementById(add_product_id).value;
        if( value == 'NULL' || value == ""){
           	alert("Please select pack size");
        }else{
         
        	if( product_ids != '' && document.getElementById('productNameDescrip_'+ product_ids) != '' 
        		&& document.getElementById('variant_product_name'+product_ids) != ''){  
	  			if(document.getElementById('productNameDescrip_'+ product_ids).innerHTML != '' ){
	  				add_product_name  = document.getElementById('productNameDescrip_'+ product_ids).innerHTML; 
	  				add_product_name = add_product_name + document.getElementById('variant_product_name'+product_ids).innerHTML;
	  				formName.add_product_name.value = add_product_name;
	  			} 
  			} 
        
			addItem(formName );
		}
    }
    
    function addItem(formName) {  
    
    var add_product_id= formName.add_product_id.value;
    var add_product_name = formName.add_product_name.value;    
    var quantity= formName.quantity.value;
    if(quantity > 20){
    alert("Please select quantity less  than 20");
    }else{
    var add_category_id= formName.add_category_id.value;
    var clearSearch= formName.clearSearch.value;
       if (formName.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       }else {
         var  param = 'add_product_id=' + add_product_id + '&quantity=' + quantity+'&add_category_id='+add_category_id;
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
	         return;
          }else{ 
          	  cart_top_notification(add_product_name);
              document.getElementById('microcart').innerHTML=data;    
          } 
         },
         complete:  function() {  
         },
         error: function(data) {
         }
    	});
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
 
 
</script>

<script>
	$(document).ready(function(){		
		jQuery('#accordion').accordion({
        active: 1,
            header: "h1",
            navigation: true,
            collapsible: true,
            autoHeight: false
           
        });
	});
</script>

<script type="text/javascript">
        /* <![CDATA[ */
       $(function() {
            
<#assign countmainacc = 0>
 <#if categoryIdList?exists>
           <#list categoryIdList as paramIds>
          <#assign paramId = paramIds.split(",")/>
          <#list paramId as x>
			  <#assign catIdd = x/>
			  
			 <#-- <#assign category = delegator.findByAnd("ProductCategoryMember", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", catIdd))?if_exists>-->
				<#assign productIDss = Static["org.ofbiz.product.product.ProductWorker"].getProductIDs(delegator,catIdd)?if_exists/>
				
				    
<#if productIDss?has_content>
            jQuery('#UIPViewModel_ActiveAccordion${countmainacc}').click(
                function() {
                	//alert("hi");
                    var window_top = $(window).scrollTop();
                    
                    //var div_top = $(this).offset().top; 
                    var div_top =0;
                    
                        
                        
                            $('html, body').animate({scrollTop:div_top}, 0);
                           // alert("hi==="+div_top+"====window_top==="+window_top);
                
            });
            
        <#assign countmainacc = countmainacc+1>
</#if></#list></#list></#if>

        });
        /* ]]> */
    </script>
<#--
 </div>
    <div style="float:right; width:180px;">
	${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#quickshopforrender")}
</div>
--><#assign countmainacc = 0>
 <#if categoryIdList?exists>
<div id="accordion">
        <#list categoryIdList as paramIds>
          <#assign paramId = paramIds.split(",")/>
          <#list paramId as x>
			  <#assign catIdd = x/>
			  
			 <#-- <#assign category = delegator.findByAnd("ProductCategoryMember", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", catIdd))?if_exists>-->
				<#assign productIDss = Static["org.ofbiz.product.product.ProductWorker"].getProductIDs(delegator, catIdd)?if_exists/>
				
				    
<#if productIDss?has_content>

<#assign countmain = 0>
<#assign numCol = 4>
      <#assign numCol = numCol?number>
      <#assign tabCol = 1>

		<div class="screenlet" style="margin-bottom: 10px;width: 81.4%;float: right;margin-left: 10px;" >
					<h1 style="margin: 0;background: #CDCEAF;padding: 15px 6px 15px 5px;" id="UIPViewModel_ActiveAccordion${countmainacc}">
					<#assign countmainacc = countmainacc+1>
						<#assign categoryName = delegator.findByPrimaryKey("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", catIdd))?if_exists>
								<#if categoryName?has_content>	
									${categoryName.categoryName?if_exists}
								</#if>	
					</h1>
			     <div <#if categoryImageUrl?has_content></#if>
			        class="productsummary-container<#if (numCol?int > 1)> matrix</#if>">
			        <#if (numCol?int > 1)>
			        <table>
			        
			        </#if>
			        <#list productIDss as pop>
			            <#if (tabCol?int = 1)><tr></#if>
			                  <td>
			  			${setRequestAttribute("optProductId", pop)}
			  			${setRequestAttribute("listIndex", pop_index)}
			  			${setRequestAttribute("formNamePrefix","")}
			            ${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#productsummary")}
			         
			            </td>
			             <#if (tabCol?int = numCol)></tr></#if>
			               <#assign tabCol = tabCol+1><#if (tabCol?int > numCol)><#assign tabCol = 1></#if>
			             <#assign countmain = countmain+1>
			        </#list>
			         <#if (numCol?int > 1)>
			            </table>
			         </#if>
			      </div>
      	</div>
</#if>


</#list>
        </#list>
        
</div>      
   </#if>
<div style="float:right; width:180px;">
	${screens.render("component://ecommerce/widget/ecomclone/CommonScreens.xml#rightcatbar")}
</div>
