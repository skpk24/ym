<div class="screenlet-title-bar" style="margin-top:8px; background:none !important;"><h3>You might be interested :</h3> </div>
<div id="nav_exmaple">
		<ul>
			<li><a href="<@ofbizUrl>getImpulsiveProduct?ajaxCategoryId=BestDeals</@ofbizUrl>" id="BestDeals" >Best Deals</a></li>
			<li><a href="<@ofbizUrl>getImpulsiveProduct?ajaxCategoryId=WHATS-NEW</@ofbizUrl>" id="NewArrivals">New Arrivals</a></li>
			<li><a href="<@ofbizUrl>getImpulsiveProduct?ajaxCategoryId=MOST-POPULAR</@ofbizUrl>" id="MostViewed">Most Viewed</a></li>
		</ul>
</div>

<div id="content">
	<#if assocProducts?has_content>
		<script type="text/javascript">
		  function mycarousel_initCallback(carousel)
			{ 
			    // Pause autoscrolling if the user moves with the cursor over the clip.
			    carousel.clip.hover(function() 
			    {
			        carousel.stopAuto();
			    }, function() {
			        carousel.startAuto();
			    });
			};
		
			jQuery(document).ready(function() {
			    jQuery('#mycarouse3').jcarousel({
			    	wrap: 'circular',
			        auto: 4,
			        scroll:1,
			        initCallback: mycarousel_initCallback
				});
				
			});
			
			
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
            
           if( document.forms['addform'+formName] && document.forms['addform'+formName].add_product_name != null){   
				var prodName  = document.getElementById('productNameDescrip_'+ formName).innerHTML;   
				document.forms['addform'+formName].add_product_name.value = prodName + skuName;  				
			}
           
           

            // check for amount box
           // toggleAmt1(checkAmtReq(sku));
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
    
    
function addItems1(add_product_id,formName,add_product_name) { 
	var value= document.getElementById(add_product_id).value;
    if(value == 'NULL' || value == ""){
      alert("Please select pack size");
    }else{
   	  addItem(formName,add_product_name);
    }
}
    
function addItem(formName,add_product_name) {

    var add_product_id= formName.add_product_id.value;
    add_product_name = formName.add_product_name.value;
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
     
	           var  param = 'add_product_id=' + add_product_id + '&quantity=' + quantity;
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
	                	setTimeout(function(){window.location.reload(true).slideUp(400);},3000);
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
    
		</script>
 	<div class="screenlet" style="margin-bottom:5px" id="ajaxUpdate">
					 <div class="previous_button_showcart">
					  		<#if preAvail>
					  			<a href="javascript:showBestDealProducts('${bestDealPrevIndex?if_exists?default(-1)}','${ajaxCategoryId?if_exists}')"><img src="/multiflex/left_arrow.png" alt=""/></a>
					  		<#else>
					  			<img src="/multiflex/left_arrow.png" alt=""/>
					  		</#if>
					  		</div>
							<#assign count=1/>
							<div>
								<ul style="margin-left: -20px;margin-right: 15px;">
									<#list assocProducts as mv>
								  	<li style="height: 275px; width: 200px ! important; float: left; margin: 6px; background-color: rgb(255, 255, 255); padding: 5px 10px; border: 1px solid rgb(204, 204, 204); background-image: url('/erptheme1/backgroundproduct.jpg'); background-repeat: repeat-x; background-position: left center; list-style: none outside none;" jcarouselindex="1">
									  	 ${setRequestAttribute("optProductId1", mv.productId?if_exists)}
								         <span style="border:none !important;">${screens.render(impulsiveproductsummaryScreen)}</span>
								         <#assign count = count + 1 />
									 </li>
							  	 </#list>
								</ul>
							</div>
							<div class="next_button_showcart">
								<#if nextAvail>
									<a href="javascript:showBestDealProducts('${bestDealNextIndex?if_exists?default(1)}','${ajaxCategoryId?if_exists}')"><img src="/multiflex/right_arrow.png" alt=""/></a>
								<#else>
						  			<img src="/multiflex/right_arrow.png" alt=""/>
								</#if>
							</div>
	</div>
	</#if>
</div>

<script type="text/javascript">
		function showBestDealProducts(bestDealIndex,ajaxCategoryId){
   			var url="/control/showCrossProductsAjax?bestDealIndex="+bestDealIndex+"&ajaxCategoryId="+ajaxCategoryId;
			jQuery.ajax({url: url,
		        data: null,
		        type: 'post',
		        async: true,
		        success: function(data) {
	          		$('#ajaxUpdate').html(data);
	  			},
				complete:  function() {
				  	pleaseWait('N');
				},
		        error: function(data) {
		            alert("Error during product filtering");
		        }
    		});   
		}
</script>		