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
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>

</#if>
<!-- <a href="javascript:custClickedCartsummary()" id="cartsummary1" class="fb-vertical"><img src="/erptheme1/cartslide/cart_button.png" class="header-main-link" alt="cart summary" title="cart summary"/>
    <#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
	<#if shoppingCart?has_content>
		<div style="position:absolute; top:34px; left:9px;" id="expandSideCatQuantity">${shoppingCart.getTotalQuantity()}</div>		
	</#if>
</a>-->
<div id="inbulkorder" class="fb-vertical" style="display:none">
	${screens.render("component://ecommerce/widget/ecomclone/CartScreens.xml#minicart")}	
	<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
	<#if shoppingCart?has_content>
		<div style="position:absolute; top:34px; left:9px;" id="sideCatQuantity">${shoppingCart.getTotalQuantity()}</div>		
	</#if>
	<a href="#" class="cartlink" style="top:183px;"></a>
	<map name="Map">
		<area shape="rect" coords="2,3,34,179" href="javascript:custClickedCartOrder()" alt="close">
	</map>
</div>
<script>
function checkEmailHome() {

email=document.getElementById("emailId").value;
if(email!=null && email!="")
{
            if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)){
            var  param = 'email='+ email;
			 jQuery.ajax({url: "/control/checkingExisingEmail",
       				  data: param,
         			  type: 'post',
        			context: $(this),
         			async: false,
         
         success: function(data) {
         if(data.contains("true"))
         {
         
         document.getElementById("popup_content").style.display='none';
          document.getElementById("existingCustomer").style.display='block';
          $(function() {
          $( "#existingCustomer" ).dialog({
		modal: true,
		buttons: {
		Ok: function() {
		$( this ).dialog( "close" );
		}
		}
		});
		});
  		
  		}
         else
         {
          storeEmailform.action="/control/storeEmaillocation";
          storeEmailform.submit();
         }
         },
         error: function(data) {
         }
       
    });
       return true;
            }
            else
            {
            alert("Please Provide correct Email id");
             return false; 
            }
            }
            else
            {
            alert("Please Provide Email id");
            return false;
            }
          }
</script>
<script>

	var interval;
	function cartsummary1(){
	 $('#cartsummary1').hide();
	 $('#inbulkorder').show();
	 
	}
	function inbulkorder(){
	 $('#cartsummary1').show();
	 $('#inbulkorder').hide();
	 
	}
</script>

<script>
	function custClickedCartsummary(){
		 $('#cartsummary1').hide();
		 $('#inbulkorder').show();
	}
	function custClickedCartOrder(){
		 $('#cartsummary1').show();
		 $('#inbulkorder').hide();
	}
	
</script>

<style>

#backgroundPopup {
    z-index:1;
    position: fixed;
    display:none;
    height:100%;
    width:100%;
    background:#000000;
    top:0px;
    left:0px;
}
#toPopup {
    font-family: "lucida grande",tahoma,verdana,arial,sans-serif;
    background: none repeat scroll 0 0 #FFFFFF;
    border: 10px solid #ccc;
    border-radius: 3px 3px 3px 3px;
    color: #333333;
    display: none;
    font-size: 14px;
    left:45%;
    margin-left: -402px;
    position: fixed;
    top: 25%;
   
    z-index: 2;
    text-align:left;
}
div.loader {
    background: url("../img/loading.gif") no-repeat scroll 0 0 transparent;
    height: 32px;
    width: 32px;
    display: none;
    z-index: 9999;
    top: 40%;
    left: 50%;
    position: absolute;
    margin-left: -10px;
}
div.close {
    background: url("../img/closebox.png") no-repeat scroll 0 0 transparent;
    cursor: pointer;
    height: 30px;
    position: absolute;
    right: -27px;
    top: -24px;
    width: 30px;
}
span.ecs_tooltip {
    background: none repeat scroll 0 0 #000000;
    border-radius: 2px 2px 2px 2px;
    color: #FFFFFF;
    display: none;
    font-size: 11px;
    height: 16px;
    opacity: 0.7;
    padding: 4px 3px 2px 5px;
    position: absolute;
    right: -62px;
    text-align: center;
    top: -51px;
    width: 93px;
}
span.arrow {
    border-left: 5px solid transparent;
    border-right: 5px solid transparent;
    border-top: 7px solid #000000;
    display: block;
    height: 1px;
    left: 40px;
    position: relative;
    top: 3px;
    width: 1px;
}
div#popup_content {
    margin: 4px 7px;
    /* remove this comment if you want scroll bar
    overflow-y:scroll;
    height:200px
    */
}

</style>
<#assign req = request.getRequestURI()>
<#--if req?has_content && req.contains("/control/home")>

<script>

jQuery(function($) {

	//$("a.topopup").click(function() {
	var mail=document.getElementById("customerMailId").value;

	if(mail==null || mail=="")
	{
	$(document).ready(function ()
   {
 
			loading(); // loading
			setTimeout(function(){ // then show popup, deley in .5 second
				loadPopup(); // function show popup
			}, 500); // .5 second
	return false;
	
	});
}
	/* event for close the popup */
	$("div.close").hover(
					function() {
						$('span.ecs_tooltip').show();
					},
					function () {
    					$('span.ecs_tooltip').hide();
  					}
				);

	$("div.close").click(function() {
	//	disablePopup();  // function close pop up
	});

	$(this).keyup(function(event) {
		if (event.which == 27) { // 27 is 'Ecs' in the keyboard
		//	disablePopup();  // function close pop up
		}
	});

        $("div#backgroundPopup").click(function() {
		// disablePopup();  // function close pop up
	});

	$('a.livebox').click(function() {
		alert('Hello World!');
	return false;
	});

	 /************** start: functions. **************/
	function loading() {
		$("div.loader").show();
	}
	function closeloading() {
		$("div.loader").fadeOut('normal');
	}

	var popupStatus = 0; // set value

	function loadPopup() {
		if(popupStatus == 0) { // if value is 0, show popup
			closeloading(); // fadeout loading
			
			$("#toPopup").fadeIn(0500); // fadein popup div
			$("#backgroundPopup").css("opacity", "0.7"); // css opacity, supports IE7, IE8
			$("#backgroundPopup").fadeIn(0001);
			popupStatus = 1; // and set value to 1
		}
	}

	function disablePopup() {
		if(popupStatus == 1) { // if value is 1, close popup
			$("#toPopup").fadeOut("normal");
			$("#backgroundPopup").fadeOut("normal");
			popupStatus = 0;  // and set value to 0
		}
	}
	
	/************** end: functions. **************/
}); // jQuery End

</script>
</#if>
<div id="toPopup">
	 
	        <div class="close"></div>
	        <span class="ecs_tooltip">Press Esc to close <span class="arrow"></span></span>
	        <div id="popup_content">
	         <p>Welcome to YouMart!!
			We are getting ready to serve you & will go live in few days.
			Kindly drop your email id, for us to notify you with a launch date & offers. 
			</p>

     <form name="storeEmailform" method="post">
		     <table cellspacing="0" align="center" style="margin:0 auto; width:276px; padding-bottom:20px;">
			 <tr><td colspan="2">&nbsp;</td></tr>
			<input type="hidden" name="pinCode" value="123launch"/>
			<tr>
			<td width="100px"><span class="label" >Email Id</span></td>
			<td><input type="text" name="emailId" id="emailId" /></td>
			</tr>
			<tr>
			<td >&nbsp;</td>
			<td><input type="submit" value="Submit"  onClick="return checkEmailHome();"></td>
			 </tr>
			</table>   
	     	</form>
	        </div> 
		  
		    </div> 
		     
		    <input type="hidden" name="customerMailId" id="customerMailId" value="${session.getAttribute("customerMailId")?if_exists}">
		  
		    <div class="loader"></div>
		    <div id="backgroundPopup"></div>
               <div id="existingCustomer" style="display:none;">Welcome to YouMart!!
	You already registered with YouMart,we will notify you with a launch date & offers</div>
-->
<!--<a href="http://www.google.com?iframe=true&width=100%&height=100%" rel="prettyPhoto[iframes]" title="Google.com opened at 100%">Google.com</a>-->
<!-- a href="javascript:feedback1()" id="feedback1" class="fb-verticalone"><img src="/erptheme1/cartslide/feedback.png" class="header-main-link" alt="feedback" title="feedback"/></a -->
<div id="feedbackinfo" class="fb-verticalone1" 
	<#if requestAttributes.from?has_content && "feedback1" == requestAttributes.from>
			style="display:block"
	<#else>	
		style="display:none"
	</#if>	
		>
	${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#feedbackhome")}
	<a href="#" class="feedlink" style="top:183px;"></a>
	<map name="Map2">
			<area shape="rect" coords="6,3,38,126" href="javascript:feedbackinfo()" alt="close">
	</map>
</div>

<script>
	function feedback1(){
	 $('#feedback1').hide();
	 $('#feedbackinfo').show();
	}
	function feedbackinfo(){
	 $('#feedback1').show();
	 $('#feedbackinfo').hide();
	}
</script>




<!-- a href="javascript:tellus1()" id="tellus1" class="fb-verticaltwo"><img src="/erptheme1/cartslide/call.png" alt="Tell a Friend" class="header-main-link" title="Tell a Friend"/></a -->
<div id="tellusinfo" class="fb-verticalone1" 
	<#if requestAttributes.from?has_content && "inviteafriend1" == requestAttributes.from>
			style="display:block"
	<#else>	
		style="display:none"
	</#if>	
		>
	${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#inviteafriendRen")}
	<a href="#" class="telluslink" style="top:183px;"></a>
	<map name="Map3">
		<area shape="rect" coords="6,3,35,151" href="javascript:tellusinfo()" alt="close">
	</map>
</div>

<script>
	function tellus1(){
	 $('#tellus1').hide();
	 $('#tellusinfo').show();
	}
	function tellusinfo(){
	 $('#tellus1').show();
	 $('#tellusinfo').hide();
	}
</script>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
<div class="footer">
	<div class="website-info-left">
    	<h4 >Information & Services</h4>
    	<ul style="width:200px !important;">
      		<li><a href="<@ofbizUrl>aboutus</@ofbizUrl>">About Us</a></li>
      		<li><a href="<@ofbizUrl>aboutyoufarm</@ofbizUrl>">About YouFarm</a></li>
      	 <#--	<li><a href="<@ofbizUrl>joinUs</@ofbizUrl>">Join Us</a></li> -->
      	 	<li><a href="<@ofbizUrl>siteMap</@ofbizUrl>">Site Map</a></li>
      	 	<#--<li><a href="<@ofbizUrl>faqs</@ofbizUrl>">Faq's</a></li>  -->
      	 	<li><!--<#if userLogin?has_content && userLogin.userLoginId != "anonymous">
          		<a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a>
        		<#else>
          		<a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a>
        		</#if>-->
        		<a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a>
        	</li>
      		<li><a href="<@ofbizUrl>policies</@ofbizUrl>">Privacy Policy</a></li>
      		<li><a href="<@ofbizUrl>securitypolicy</@ofbizUrl>">Security Policy</a></li>
      		<!--<li><a href="<@ofbizUrl>mediaCenter</@ofbizUrl>">Media Center</a></li>-->
      		<li><a href="<@ofbizUrl>termofuse?FaqsId=FaqsId</@ofbizUrl>">Term And Conditions</a></li>
      		<li id="viewTestimonials"><a href="<@ofbizUrl>viewTestimonials</@ofbizUrl>">Testimonials</a></li>
			<li id="feedBack"><a href="<@ofbizUrl>viewFeedback</@ofbizUrl>">Feedback </a></li>
      		<li id="feedBack"><a href="<@ofbizUrl>viewInviteAFriend</@ofbizUrl>">Invite a friend </a></li>
  		
  		</ul>
  		<div style="clear:both"></div>
  		<div style="text-align:left; margin-top:50px"><img src="/erptheme1/fssai.gif"/></div>
  		<#-->ul>
      		<li><a href="#"><img src="/erptheme1/payment.png"></a></li>
  		</ul-->
	</div>
 	
	<#--<div class="website-info" style="width:300px">
   		<h4>Newsletter Signup</h4>
		<ul>
      		<li>${screens.render("component://ecommerce/widget/ecomclone/EmailContactListScreens.xml#signupforcontactlist")}</li>
  		</ul>
	</div>-->
	<div class="website-info" style="width:255px">
   		<h4>Top Brands</h4>
		<ul>
		
		
		
<#assign seq = ["Aashirvaad", "Saffola", "Britannia", "Amul","MTR","Surf Excel","Tropicana","Best","Daawat","Pantene","Lakme","Nestle","Cadbury","Horlicks","Tata Tea","MamyPoko"]>		
 
		<#if seq?has_content>
		  <#list seq as key>
      		<li><a href="<@ofbizUrl>topBrandProducts?SEARCH_STRING=${key?if_exists}&filterByBrand=${key?if_exists}</@ofbizUrl>">${key?if_exists}</a>
      		</a></li>
      	  </#list>
      	</#if>
      		
  		</ul>
	</div>



<#--<#assign seq1 = ["Baby Diapers & Wet Wipes","DIAPER_WET","Baby Oil, Soap & Shampoos","OIL_SOAP","Health Drinks","Basmati Rice","Cleaning Accessories, Kitchen & Toilet Agents","Snacks & Wafers","Cookies & Biscuits","Tea & Coffee","Skin Care"]>-->

	
	<div class="website-info" style="width:255px">
   		<h4>Top Categories</h4>
		<ul>
		<#assign seq1 = {"Baby Diapers & Wet Wipes":"/products/BABY_CARE/DIAPER_WET?CURRENT_CATALOG_ID=VEGFRUIT",
						"Baby Oil, Soap & Shampoos":"/products/BABY_CARE/OIL_SOAP?CURRENT_CATALOG_ID=VEGFRUIT",
						"Health Drinks":"/products/CHILL_DRINK/ENE_DRINK?CURRENT_CATALOG_ID=VEGFRUIT",
						"Basmati Rice":"/products/FOODGROC/RICE_PRO/BAS_RIC?CURRENT_CATALOG_ID=VEGFRUIT",
						"Cleaning Accessories, Kitchen & Toilet Agents":"/products/HOME_NEED/CLEA_ACCE?CURRENT_CATALOG_ID=VEGFRUIT",
						"Snacks & Wafers":"/products/FOODGROC/SNACK_WEFR?CURRENT_CATALOG_ID=VEGFRUIT",
						"Cookies & Biscuits":"/products/CHO_DES/COO_BISC?CURRENT_CATALOG_ID=VEGFRUIT",
						"Tea & Coffee":"/products/FOODGROC/TEA_COFFE?CURRENT_CATALOG_ID=VEGFRUIT",
						"Skin Care":"/products/PERS_CARE/SKN_CARE?CURRENT_CATALOG_ID=VEGFRUIT"
						}>
		<#if seq1?has_content>
		<#assign keys = seq1?keys>
		  <#list keys as key>
		  <#assign value = seq1[key]> 
      		<li><a href="${value?if_exists}">${key?if_exists}</a></li>
      	  </#list>
      	</#if>
      		
  		</ul>
	</div>
    <div class="website-info" style="border-right:none !important;">
   		<h4>Be in touch</h4>
		<ul>  
      		<li style="padding-bottom:8px;"><a href="https://www.facebook.com/pages/Youmart/652717481452888?ref=settings" target="_blank" class="fb"></a>
      		<a href="https://twitter.com/YoumartShopping" target="_blank" class="twitter"> </a>
      		<a href="https://plus.google.com/u/0/b/118149388921584738984/118149388921584738984/posts"  target="_blank" class="google"> </a>
      		<#-- <li style="padding-bottom:8px;"><a href="#" target="_blank" ><img src="/erptheme1/youtube.png"> Youtube</a></li>-->
      		<a href="http://www.linkedin.com/company/youmart?trk=biz-companies-cym"  target="_blank" class="linkedin"> </a></li>
      		<li style="padding: 30px 8px 0 0; height:20px;">      		
      			<iframe src="//www.facebook.com/plugins/like.php?href=https%3A%2F%2Fwww.facebook.com%2FYoumartShopping&amp;width&amp;layout=button_count&amp;action=like&amp;show_faces=true&amp;share=false&amp;height=21&amp;appId=512610442161323" scrolling="no" frameborder="0" style="border:none; overflow:hidden; height:21px;" allowTransparency="true"></iframe>
      		</li>
      		<li style="padding:10px 8px 0 0; height:20px;">
      			<script type="text/javascript" src="https://apis.google.com/js/plusone.js" async></script>
      			<g:plusone href='https://plus.google.com/u/0/118149388921584738984/posts' size='medium' width='300' annotation='bubble'/>
      		</li>
  		</ul>
  		<div style="clear:both;"> </div>
  		<div><h4>Payment Methods</h4></div>
      		<div style="margin-top:5px;"><img src="/erptheme1/visa.jpg"></div>
      		<div style="clear:both;"> </div>
      		<div style="margin-top:25px; margin-bottom:15px; margin-left:5px; text-align:left">
      		<span id="siteseal"><script type="text/javascript" src="https://seal.godaddy.com/getSeal?sealID=JJOEI0YUYjLcxMP2fkGCV9KnoLEkYqAuLppNEk5RND33tPUQ18cB"></script></span>
      		<a href="http://www.coupondunia.in" target="_blank" title="Find Our Coupons on 

CouponDunia">

  <img src="http://www.coupondunia.in/media/coupondunia_badge.png" width="105" 

height="40" alt="Find Our Coupons on CouponDunia" style="padding: 5px; cursor: hand; 

border:0" />

 </a>
		</div>
	</div>
			
</div>


<div id="common-bottom" style="position:fixed; right:0px; bottom:0px;">
	<a href="../../products/p_GIFTCARD"></a>
</div>


<div style="clear:both; border-top:1px solid #777645; margin:5px auto; width:1024px;"></div>
<span class="active"> Copyright &#169; 2013-2014 Ujjivan Enterprises Pvt Ltd. </span> <br><br><br>
<script>


function searchChgQty(delta,id){
                     var txtbox = document.getElementById(id);
                     var value = txtbox.value;
                     qty = parseFloat(value) + delta;
                     qty = parseInt(qty);
                     if(qty < 1)
                     {
                     	qty = 1;
                     }
                     document.getElementById(id).value = parseInt(qty);
}

function searchAddToCart(productId, productName){

	if(productName == '' || productName == null){
		productName = productId;
	}	
    var add_product_id= productId;
    var quantity= document.getElementById("quantity"+productId).value;
    if(quantity > 20)
    {
    	alert("Please select quantity less than 20"); 
    }
    else
    {
       if (add_product_id == 'NULL') {
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
          if(data.indexOf("Can not add more than") !== -1)
          {
             var flag_new = true;
          	 if(data == "Can not add more than giftcard")return;
          	 
          	 var dataTemplate=data.replace("Can not","can't");
          	
          	 alert(dataTemplate);
	         return;
            }else{
				if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1)){
          		alert(data);
          	}else{	
    	    cart_top_notification(productName);
    		document.getElementById('microcart').innerHTML=data;                                		 
    	}
		}
         // $('#minicart').html(data);
           SearchShowDialog(false);
         },
         complete:  function() { 
         
        // var minitotal = document.getElementById('abcxyz').innerHTML;
         // var miniquantity = document.getElementById('miniquantityA').value;
          // $('#microCartTotal').text(minitotal);
          // document.getElementById('microCartQuantity').innerHTML=miniquantity;
          // document.getElementById('checkoutdis').style.display="block";
         // document.getElementById('abcxyzhref').href="/control/showcart";
         },
        error: function(data) {
        }
    	});
      }
   }
}

function SearchShowDialog(modal)
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


function checkForGiftCard(data,formName){
	var flag = true;
	if(data == "Can't add more than giftcard")
          	 {
          	 	clearCart(formName);
          	 	/*
				var r=confirm("Can't add other items with gift card . Do you want to remove gift card !");
				if (r==true)
				  {
				     clearCart(formName);
				  }
				else
				  {
				  flag = false;
				  }
				  */
          	 }
      return flag;
}
function clearCart(formName){
	jQuery.ajax({url: "/control/emptycartNew",
         data: "",
         type: 'post',
         async: false,
         success: function(data) {
         if(formName == "")
         {
         	addItemsCart();
         }else{
         	addItem(formName);
         }
	     },
        error: function(data) {
        }
    	});
}

</script>
 <script> 





function backupdatecart()
{

    jQuery.ajax({url: '/control/minicart',
	         data: null,
	         type: 'post',
	         async: false,
	         success: function(data) {
					$('#minicart').html(data); 
					var miniquantity = document.getElementById('miniquantityA').value;
           			document.getElementById('microCartQuantity').innerHTML=miniquantity;
           			document.getElementById('sideCatQuantity').innerHTML=miniquantity; 
           			document.getElementById('expandSideCatQuantity').innerHTML=miniquantity; 
	      	 		var abcvalue = document.getElementById('abcxyz').innerHTML;
           			if (abcvalue ==""){
           	           abcvalue = "0.00";
           	
           			}   
	        		document.getElementById('microCartTotal').innerHTML =abcvalue; 
	      
	         },
        error: function(data) {
        }
    	});
}
</script>

<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
 
  ga('create', 'UA-48446863-1', 'youmart.in');
  ga('send', 'pageview');
 
</script>

<!-- Google Code for Remarketing Tag -->
<!--------------------------------------------------
Remarketing tags may not be associated with personally identifiable information or placed on pages related to sensitive categories. See more information and instructions on how to setup the tag on: http://google.com/ads/remarketingsetup
--------------------------------------------------->
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 969975614;
var google_custom_params = window.google_tag_params;
var google_remarketing_only = true;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//googleads.g.doubleclick.net/pagead/viewthroughconversion/969975614/?value=0&amp;guid=ON&amp;script=0"/>
</div>
</noscript>
<#--script>(function() {
  var _fbq = window._fbq || (window._fbq = []);
  if (!_fbq.loaded) {
    var fbds = document.createElement('script');
    fbds.async = true;
    fbds.src = '//connect.facebook.net/en_US/fbds.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fbds, s);
    _fbq.loaded = true;
  }
  _fbq.push(['addPixelId', '1485530595011438']);
})();
window._fbq = window._fbq || [];
window._fbq.push(['track', 'PixelInitialized', {}]);
</script>
<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?id=1485530595011438&amp;ev=PixelInitialized" /></noscript-->