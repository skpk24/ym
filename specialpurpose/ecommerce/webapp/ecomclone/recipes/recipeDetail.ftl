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
           checkInventory(sku, formName);
            // display alternative packaging dropdown
            ajaxUpdateArea("product_uom", "<@ofbizUrl>ProductUomDropDownOnly</@ofbizUrl>", "productId=" + sku);
            // set the product ID
            setAddProductId(sku,formName);

            // set the variant price
            
            
            setAddProductNames(sku,formName);
            setVariantPrice(sku,formName);

            // check for amount box
           // toggleAmt(checkAmtReq(sku));
           
        }
        
    }
    function checkInventory(prodId,formName){
	    var  param = 'productId=' + prodId;
                      jQuery.ajax({url: "/control/isInventoryAvailableForProduct",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
        		if (data == "true"){
        			document.getElementById("addstock"+formName).style.display="block";
        			document.getElementById("outstock"+formName).style.display="none";
        			
        		}else{
        			document.getElementById("addstock"+formName).style.display="none";
        			document.getElementById("outstock"+formName).style.display="block";
        			
        		}
         },
         error: function(data) {
         }
    });
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
            var abc = "getVariantPrice"+formName+ "('" + sku + "');";
            var price = eval(abc);
            var priceParts = price.split('Rs.');
            var rs = priceParts[0];
            var priceOnly = priceParts[1];
            if(priceOnly.slice(-3) == '.00'){
            	
            	var priceParts = priceOnly.split('.00');
            	priceOnly = priceParts[0];
            	var rs1 = priceParts[1];
            }
            var txt = document.createTextNode(priceOnly);
            if(elem.hasChildNodes()) {
                elem.replaceChild(txt, elem.firstChild);
            } else {
                elem.appendChild(txt);
            }
        }
    }
    
     function setAddProductNames(sku,formName)
    {
   
	    var  param = 'productId=' + sku;
                      jQuery.ajax({url: "/control/getProductNames",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         var data = data.split('-');
         data = data[1];
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
    });
    }
    
    function addItems(add_product_id, formName) {
 		var value = document.getElementById(add_product_id).value;
		alert(" value == "+value);
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
    if(quantity > 20)
    {
    	alert("Please select quantity less than 20"); 
    }else{
    
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
         async: false,
         success: function(data) {
          if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
          {
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
 
  function addTextArea(parentCommentId,recipeManagementId){
    	if(!checkLogin())
	    	return false;
		var newText = '<form name="subComment" action="<@ofbizUrl>addComment</@ofbizUrl>">';
		newText = newText + '<input type="hidden" name="recipeId" value="'+recipeManagementId+'"></textarea>';
		newText = newText + '<input type="hidden" name="parentCommentId" value="'+parentCommentId+'"></textarea>';
		newText = newText + '<input type="hidden" name="type" value="RECIPE_COMM_TYPE_COM">';
		newText = newText + '<textarea name="message" rows="4" cols="50" style="border:1px solid #999999; display:block; margin-bottom:5px;" value=""></textarea>';
		newText = newText + '<input type="submit" value="Add"/>';
		newText = newText + '<input type="button" value="Cancel" onclick="removeTextArea('+parentCommentId+','+recipeManagementId+')"/></form>';

		document.getElementById(parentCommentId).innerHTML  += newText;
		document.getElementById(parentCommentId+""+recipeManagementId).innerHTML  = "";
	}
	function removeTextArea(parentCommentId,recipeManagementId)
	{
		alert("parentCommentId==="+parentCommentId+"===recipeManagementId=="+recipeManagementId);
		document.getElementById(parentCommentId).innerHTML  = "";
		var newText = '<a href="javascript:addTextArea(\''+parentCommentId+'\',\''+recipeManagementId+'\');" class="buttontextblue">reply</a>';
		document.getElementById(parentCommentId+""+recipeManagementId).innerHTML  += newText;
	}
	function addRating(value){
		document.getElementById("rating").value = value;
	}
</script>
<script>
	function loadAssocProduct(prodId,qty,ingProdId){
		var productId = document.getElementById("assocProd"+prodId).value;
		if(productId == "")return ;		            
	    var url="/control/recipeIngProductSummary?mainProdId="+ingProdId+"&optProductId="+productId+"&Originalquantity="+qty+"&from=recipeIng";
	    alert(" url == "+url);
 		jQuery.ajax({url: url,
	        data: null,
	        type: 'get',
	        async: true,
	        success: function(data) {
	          $('#recipeIngProd'+prodId).html(data);
		  	},
			complete:  function() {
			  	pleaseWait('N');
			},
	        error: function(data) {
	            alert("Error during product filtering");
	        }
	    });   
	}
	
	function print_specific_div_content(){
    var win = window.open('','','left=0,top=0,width=1000,height=1000,toolbar=0,scrollbars=1,status =0');

    var content = "<html>";
    content += "<body onload=\"window.print(); window.close();\">";
    content += document.getElementById("divToPrint").innerHTML ;
    content += "</body>";
    content += "</html>";
    win.document.write(content);
    win.document.close();
}
</script>
<link href="/erptheme1/star/rating_simple.css" rel="stylesheet" type="text/css">

<#--assign recipe = requestAttributes.recipe?if_exists>

<#assign recipeIngredientList = requestAttributes.recipeIngredientList?if_exists>
<#assign recipeCommentList = requestAttributes.recipeCommentList?if_exists>
<#assign receipeOfWeek = requestAttributes.receipeOfWeek?if_exists-->
<#if recipe?has_content>
	<a href="javascript:print_specific_div_content();"  class="buttontextgreen">Print</a>
	  <div   id="divToPrint" style="display:none;float:left;">
	<div style="float:left;"><img src="${recipe.recipeImgUrl?if_exists}" width="415px" height="299px"/></div>
	
	<div style="float:left;margin-left:10px;">
	<h1 class="recipe-header" style="font-size:18px !important;text-transform:uppercase;margin:0 !important;color:#ED670E !important;margin-bottom:20px;">${recipe.recipeName?if_exists} </h1><br />
	<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipe.createdBy)?if_exists>
				<div style=" float:left;"><strong>Recipe by:</strong> ${createdBy?if_exists}</div>		
				<br/>
			<#assign reviewRating = Static["org.ofbiz.recipes.RecipeEvents"].averageReviewRating(delegator,recipe.recipeManagementId)?if_exists>
			<#if reviewRating?has_content>
				<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px">
					<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:${reviewRating?if_exists}px; height:11px">
					</div><br/> 
					</div>
				
				</#if>
			<#assign reviewComment = Static["org.ofbiz.recipes.RecipeEvents"].averageReview(delegator,recipe.recipeManagementId)?if_exists>
			<#if reviewComment?has_content>
				Reviews: (${reviewComment?if_exists})
				</#if>
				<br/><br/>
				
	</div>	
	<div style="clear:both;"></div>	
	<div><h2 class="printh2">Ingredients</h2></div>
 	<#if recipeIngredientList?has_content>
	<#list recipeIngredientList as recipeIngredient>
	<div style="margin-left:90px;"><span>	${recipeIngredient.productName?if_exists}  ${recipeIngredient.quantity?if_exists}</span><br/></div>
	<div class="clear"></div>
	</#list>
	</#if>
	<div><h2 class="printh2">Method of Preparation</h2><span style="margin-left:90px;">${StringUtil.wrapString(recipe.description)?if_exists}</span></div>		
	<div><h2 class="printh2">Recommended Variations</h2><span style="margin-left:90px;"> ${StringUtil.wrapString(recipe.variations)?if_exists}</span></div>
	
	
	
 	</div>
	 
	
	  
  	<a href="javascript:popUpSmall('<@ofbizUrl>mailRecipeForm?recipeId=${recipe.recipeManagementId?if_exists}</@ofbizUrl>','mailRecipe');" class="buttontextgreen">Email this recipe</a>
	<table class="recipe-detail">
	<!--tr>
	<td style="font-size:18px;padding-bottom:10px;"><h2>Recipe Of The Week</h2></td>
	</tr-->
		<tr>
		
			<td style="padding-top:20px;">
				
				<div style="float:left; margin-bottom:15px;">
					<div style="float:left; width:413px; height:299px; overflow:hidden;">
					   <img src="${recipe.recipeImgUrl?if_exists}" width="415px" height="299px"/>
					</div>
				<div style="float:left; margin-bottom:15px;margin-left:15px;">	
				<h1 class="recipe-header" style="font-size:18px !important;text-transform:uppercase;margin:0 !important;color:#ED670E !important;">${recipe.recipeName?if_exists}<br /> </h1>
				<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipe.createdBy)?if_exists>
				<div style=" float:left;"><strong>Recipe by:</strong> ${createdBy?if_exists}</div>
				<br/>
				  
				
				<br/>
			<#assign reviewRating = Static["org.ofbiz.recipes.RecipeEvents"].averageReviewRating(delegator,recipe.recipeManagementId)?if_exists>
			<#if reviewRating?has_content>
				<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px">
					<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:${reviewRating?if_exists}px; height:11px">
					</div><br/> 
					</div>
				
				</#if>
				
				<br/>
			<#assign reviewComment = Static["org.ofbiz.recipes.RecipeEvents"].averageReview(delegator,recipe.recipeManagementId)?if_exists>
			<#if reviewComment?has_content>
				<a href="#seereview">Reviews:</a> (${reviewComment?if_exists})
				</#if>
			
				</div>
				</div>
				<br/><br/>
			</div>	
			<div style="float:right;margin-top:-79px;">
				<#if recipe.statusId =="RECIPE_OF_WEEK">
					  	<a href="displayrecipeWeek	" class="buttontext">Chef Profile</a>
				</#if>
			</div>
				<div style="clear:both;"></div>	
				
				<h2>Ingredients<br /> </h2>
				<#if recipeIngredientList?has_content>
				<#assign seqNo = 0>
					<#list recipeIngredientList as recipeIngredient>
						<#if recipeIngredient.productId?has_content>
							${setRequestAttribute("optProductId", recipeIngredient.productId)}
							${setRequestAttribute("Originalquantity", recipeIngredient.quantity)}
							${setRequestAttribute("from", "recipeIng")}
							${setRequestAttribute("seqNo", seqNo)}
							${setRequestAttribute("ingredientName", recipeIngredient.productName)}
  				            ${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#recipeIngProductSummary")}
				        
						<#else>
						<div style="border-bottom:1px dashed #CCCCCC; margin-bottom: 5px; height:auto !important; height:45px; padding: 0px; position: relative;">
							<div style=" width:300px;  position:relative; overflow:visible; text-align:left; text-overflow:normal; white-space: normal !important; font-size:11px; word-wrap:break-word !important; text-align:left !important; color: #595D0B !important;padding-bottom:15px;"> <#-- <h5>Product Description</h5><br/> -->
								${recipeIngredient.productName?if_exists} - ${recipeIngredient.quantity?if_exists}
								
							</div>
							<#-- <div style="float:left; position:relative; left:623px;  background-color:red;">  <h5>Ingredients Quantity</h5><br/> 
							</div> -->
						</div>
							
						</#if>
						<#assign seqNo = seqNo + 1>
					</#list>
				</#if>
				<#--<strong>${recipe.createdDate?if_exists}</strong><br />-->
				<br />
				<h2>Method of Preparation</h2>
				${StringUtil.wrapString(recipe.description)?if_exists}<br />
				<#if recipe.variations?has_content>
				<br />
				<h2 id="seereview">Recommended Variations</h2>
				${StringUtil.wrapString(recipe.variations)?if_exists}<br />
				</#if>
				<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipe.createdBy)?if_exists>
				<#if !createdBy?has_content>
					<#assign createdBy = recipe.createdBy>
				</#if>
				<strong><br/>
				 <h2>Add your review and comments</h2>
				 <form name="recipeComments" action="<@ofbizUrl>addComment</@ofbizUrl>" method="post">
	 				<input type="hidden" name="recipeId" value="${recipe.recipeManagementId?if_exists}">
	 				<input type="hidden" name="type" value="RECIPE_COMM_TYPE_COM">
				 	<table>
				 	    <tr>
				 			<td>Recipe Name</td>
				 			<td>:</td>
				 			<td><span style="margin-left:4px;">${recipe.recipeName?if_exists}</span></td>
				 		</tr>
				 		<tr>
				 			<td>Rating</td>
				 			<td>:</td>
				 			<td>
					 			<script type="text/javascript" src="/erptheme1/star/rating_simple.js"></script>
					 			<input name="my_input" value="" id="rating_simple2" type="hidden">
				 			</td>
				 			
				 		</tr>
				 		<input type="hidden" name="title" value="" id="title_id">
				 		<#--tr>
				 			<td>Title</td>
				 			<td>:</td>
				 			<td><input type="hidden" name="title" value="" id="title_id"></td>
				 		</tr-->
				 		<tr>
				 			<td>Message</td>
				 			<td>:</td>
				 			<td><textarea name="message" rows="4" cols="50" value="" style="border:1px solid #999999;margin-left:3px;" id="message_id"></textarea></td>
				 		</tr>
				 		<tr>
				 			<td>&nbsp;</td>
				 			<td>&nbsp;</td>
				 			<td><input type="submit" name="add" Value="Add" onclick="return reviewComment();"/></td>
				 		</tr>
				 	</table>
				 </form>
				
				<#--if recipe.statusId?has_content && "RECIPE_OF_WEEK" == recipe.statusId && recipe.allowComments?has_content && "Y" == recipe.allowComments-->
					<!--this is for viewing comments-->
					<#if recipeCommentList1?has_content>
					<div style="float:left; margin-left:20px;">					
					     <h2>Reviews: (${reviewComment?if_exists})</h2>
						 <div style="margin-top:-5px;">
							   <#assign count = 1>
								<#list recipeCommentList1 as recipeComment>
									<div class="comment">
												<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipeComment.createdBy)?if_exists>
												<#if !createdBy?has_content>
													<#assign createdBy = recipe.createdBy>
												</#if>
												<div style="border-bottom:2px solid #DEDFBF;padding:5px 0;float:left;width:700px;">
												 <#if recipeComment.rating?has_content>
											       <#assign num=recipeComment.rating?number*12> 
											       <div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px">
												<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:${num?if_exists}px; height:11px">
												</div><br/> 
												</div>												
												</#if>
												<div style="float:left;width:500px;margin:10px 0;"><strong>By:</strong><span style="color:#999999;"> ${createdBy?if_exists} - ${recipeComment.createdStamp?date?if_exists}</span></div> 
												<div style="float:left;width:550px;text-align:justify;font-weight:normal !important;">${recipeComment.message?if_exists}</div> 											    
												
												 <div class="clear"></div> 
												</div>
									</div>
										
										
										<#assign count = count+1>
								</#list> 
									</#if>
	
					
					<!--writing commends--> 
					<div class="clear"></div>
					<br/> 
					
				<#--/#if-->
				
				
			</td>
		</tr>
	</table>
	
 
	
	
	
	
	
	
	
	
	
	
	<#else>
		No Recipe found ....
</#if>


<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
<script type="text/javascript">
function reviewComment()
 {
    if(!checkLogin())
	    return false;
	    
    var rating=document.getElementById('rating_simple2').value;
 	var message=document.getElementById('message_id').value;
 	
 	
 	if(rating==null || rating=="")
 	{
 	    alert("Select the rating!");
 	    document.getElementById('title_id').focus();
 	    return false;
 	}
 	
 	if(message==null || message=="")
 	{
 	    alert("Enter the message!");
 	    document.getElementById('message_id').focus();
 	    return false;
 	}
 	 return true;
 }
 function checkLogin()
	 {
	 	<#if userLogin?has_content>
	 		return true;
	    <#else>
	    	alert("Login Please!");
	    	return false;
	    </#if>
	 }
</script>





<script language="javascript" type="text/javascript">
            function test(value){
                alert("This rating's value is "+value);
            }
            $(function() {
                
            $("#rating_simple2").webwidget_rating_simple({
								rating_star_length: '5',
								rating_initial_value: '',
								rating_function_name: '',//this is function name for click
								directory: '/erptheme1/star'
                });

            });
        </script>
 

