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


<!-- TODO : Need formatting -->
<script type="text/javascript">

function isNumberKey(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57)) {
            return false;
         }
      
         return true;
      }
//<![CDATA[
function submitForm(form, mode, value) {
    if (mode == "DN") {
        // done action; checkout
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NC") {
        // new credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EC") {
        // edit credit card
        form.action="<@ofbizUrl>updateCheckoutOptions/editcreditcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "GC") {
        // edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    } else if (mode == "NE") {
        // new eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment</@ofbizUrl>";
        form.submit();
    } else if (mode == "EE") {
        // edit eft account
        form.action="<@ofbizUrl>updateCheckoutOptions/editeftaccount?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
    }else if(mode = "EG")
    //edit gift card
        form.action="<@ofbizUrl>updateCheckoutOptions/editgiftcard?DONE_PAGE=checkoutpayment&paymentMethodId="+value+"</@ofbizUrl>";
        form.submit();
}
//]]>
$(document).ready(function(){
var issuerId = "";
    if ($('#checkOutPaymentId_IDEAL').attr('checked') == true) {
        $('#issuers').show();
        issuerId = $('#issuer').val();
        $('#issuerId').val(issuerId);
    } else {
        $('#issuers').hide();
        $('#issuerId').val('');
    }
    $('input:radio').click(function(){
        if ($(this).val() == "EXT_IDEAL") {
            $('#issuers').show();
            issuerId = $('#issuer').val();
            $('#issuerId').val(issuerId);
        } else {
            $('#issuers').hide();
            $('#issuerId').val('');
        }
    });
    $('#issuer').change(function(){
        issuerId = $(this).val();
        $('#issuerId').val(issuerId);
    });
});

function chackReedeemAmt()
{

	var leftBalances= document.getElementById('leftBalance').value;
	var inputBalance=document.getElementById('billingAccountAmount').value;
	var cart_TotalAmt=document.getElementById('cartTotalAmt').value;
	var cart_TotalAmtround=Math.round(cart_TotalAmt);
	var remainingAmt = cart_TotalAmtround - inputBalance;
	if(inputBalance !="")
	{
		if(parseInt(inputBalance) > parseInt(leftBalances))
		{
		alert("Wrong Reedeem Amount");
		document.getElementById('billingAccountAmount').value="";
		return false;
		}else {
	
		if(remainingAmt > 0 && document.getElementById('checkOutPaymentId_COD').checked==false){
			alert("Remaining amount "+ '\u20B9 ' +remainingAmt + ", You can pay by another Payment mode");
			}
		return true; 
		}
	}else 
		return true;
}

 var clicked = 0;
function submitForm1(form)
{
var leftBalances= document.getElementById('leftBalance');
var inputBalance=document.getElementById('billingAccountAmount');
     var cart_TotalAmt=document.getElementById('cartTotalAmt');
     if(inputBalance!=null && inputBalance.value=="")
     {
     document.getElementById('billingAccountId').selectedIndex = 1;
	document.getElementById('billingAccountId').value = '';
	
}
     var cart_TotalAmtNew = 0;
     var inputBalanceNew = 0 ;
     //any positive no
     var remainingAmt = 3 ;
     if(inputBalance!=null)
     {
     if(cart_TotalAmt != null && cart_TotalAmt.value != ""){
        cart_TotalAmtNew= cart_TotalAmt.value;
     }
     if(inputBalance != null && inputBalance.value != ""){   
        inputBalanceNew =  inputBalance.value;
     }
     
     remainingAmt= cart_TotalAmtNew - inputBalanceNew;
     }
     
     if(remainingAmt>0  && (!document.getElementById('checkOutPaymentId_COD').checked && !document.getElementById('checkOutPaymentId_ICICI').checked))
     {
         alert("Please Select Payment Mode");
        return false;
     
        }
        else
        {
         if (clicked == 0) {
            	clicked++;
		        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
		         pleaseWait('Y');
		        
		        form.submit();
		        return true;
		  } else {
            alert("Please wait your order is being processed");
             return false;
        }
    
        }
}

function pleaseWait(wait){
	
				if (wait == "Y") {
					var CatH =$( document ).height();
					var CatW =$( document ).width();
					
					document.getElementById('washoutOrder').style.height=CatH+"px";
					document.getElementById('washoutOrder').style.width=CatW+"px";
					//document.getElementById('loading-contOrder').style.display="block";
					$('#loading-cont').show();
					//$('#pleaseWait').show();
					
					
				}else{
					$('#loading-cont').hide();
					//$('#washout').hide();
				}
			}
		
function inviteReference()
{

var total=document.getElementById('totalAmount_INVITE');

var reedem=document.getElementById('inviteAccountAmount');
var cart_TotalAmt=document.getElementById('cartTotalAmt');

if(cart_TotalAmt!=null)
var tem=(parseFloat(cart_TotalAmt.value)*parseFloat(reedem.value))/100.00
document.getElementById('cartTotalAmt').value=cart_TotalAmt.value-tem;

if (isNaN(reedem.value)) {
alert("Please Enter valid data");
}
if(parseFloat(reedem.value) >parseFloat(total.value))
	{
	alert("Wrong Reedeem Amount");
	return false;
	}
	
else
	{
	return true;
	}




}





</script>
<div id ="loading-cont" style="display:none">
<div id="pleaseWait" style=" font-size:30px; padding-top:30px; left:20% !important; padding-left:20px; width:700px; z-index:99999999999;"><img src="/images/loader.gif"><br/>Please wait your order is being processed</div>

</div>
<script language="javascript" type="text/javascript">
<!--
function selectedoption(){
	var temp=0;
	var lrg = document.paymentoptions.paymentMethodTypeId.length;
   if(document.paymentoptions.addGiftCard.checked){
   	var addGiftCard=document.paymentoptions.addGiftCard.checked;
   	}
	
	
	for (var i=0; i < document.paymentoptions.paymentMethodTypeId.length; i++){
	  if (document.paymentoptions.paymentMethodTypeId[i].checked){
	       var radiovalue1 = document.paymentoptions.paymentMethodTypeId[i].value;
	       alert("radiovalue1"+radiovalue1);
	       alert("addGiftCard"+addGiftCard);
	               if(addGiftCard!=null && addGiftCard== "true" && radiovalue1 == "CREDIT_CARD"){
	                    alert("credit card"+radiovalue1);
		                    submitform(document.paymentoptions, 'CGC', '');	
	                 }
       }
	}//for closed
}
function submitform(form, mode, value) {
    if (mode == "AB") {
        // done action; checkout
        //document.form[0].newStr.value="Y";
        //alert();
        form.action="<@ofbizUrl>setPaymentInformationLogin?createNew=Y&DONE_PAGE=setPaymentOptionLogin</@ofbizUrl>";
        form.submit();
    } else if (mode == "BC") {
        // continue shopping
        form.action="<@ofbizUrl>setPaymentInformationLogin?createNew=Y&DONE_PAGE=setPaymentOptionLogin</@ofbizUrl>";
        form.submit();
    }
    else if (mode == "CGC") {
        // continue shopping
        alert("CGC"+CGC);
        form.action="<@ofbizUrl>setPaymentInformationLogin?createNew=Y&DONE_PAGE=setPaymentOptionLogin&addGiftCard=Y</@ofbizUrl>";
        form.submit();
    }
}
 	
function abc(name){
	
	var formName = name;
	//alert("formName------"+formName);
 	formName.action="<@ofbizUrl>setPaymentInformationLogin</@ofbizUrl>";
 	//alert("document---sss---"+formName.action);
 	} 	

function testResults (fName) {

	var paybackAccountNumber = document.getElementById("paybackAccountNumber").value;
	if(paybackAccountNumber != ""  && paybackAccountNumber == "Please enter your 16 digits PAYBACK number to earn PAYBACK points."){
		document.getElementById("paybackAccountNumber").value = "";
	}

   var formName1 = fName;
   var oRadio = fName.elements["paymentMethodTypeId"];
   var len=oRadio.length;
   var count=0;
   for(var i = 0; i < oRadio.length; i++)
   {
    if(!(oRadio[i].checked))
      { count++;
      }
   }
   if(count==len)
    {
      alert("please choose one option");
      return false;
    }
   
   for(var i = 0; i < oRadio.length; i++)
   {
      if(oRadio[i].checked)
      {
         // alert("Hello"+oRadio[i].value);
          if(oRadio[i].value== "EXT_BILLACT"){
          formName1.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
          //alert(formName1.action);
          }else{
          formName1.action="<@ofbizUrl>setPaymentInformationLogin</@ofbizUrl>";
          //alert(formName1.action);
          }
      }
   }

}        
  // -->  
  
  // JavaScript Document

function updateCityAndStateFromPostalCode(postalCode, cityFieldId, stateFieldId, isGiftVoucherPurchaseRequest) {
  $.post("/control/updateCityAndStateFromPostalCode", { postalCode: postalCode, _GIFT_VOUCHER_PURCHASE_:isGiftVoucherPurchaseRequest},
     function(data){
        if(data.city != null) {
            $("#"+cityFieldId).val(data.city);
        }
        if(data.state !=null) {
            $("#"+stateFieldId).val(data.state);
        }
  });
}

function applyPaymentGatewayPromo(creditCardNumber, paymentMode, paymentCode, isGiftVoucherPurchaseRequest) {
  $(".loaderGif").show();
  $.post("/control/applyPaymentGatewayPromo", { creditCardNumber:creditCardNumber, paymentMode:paymentMode, paymentCode:paymentCode, _GIFT_VOUCHER_PURCHASE_:isGiftVoucherPurchaseRequest},
     function(data){
        if(!(typeof data.payentGatewayPromo == undefined)  ) {
            $.post("/control/refreshAmountDisplayable",{_GIFT_VOUCHER_PURCHASE_:isGiftVoucherPurchaseRequest},
             function(amountDisplayable){
                $(".amountDisplayable").html(amountDisplayable);
                if(data.payentGatewayPromo != null) {
                  $(".paymentGatewayDiscount").show();
                }else
                {
                  $(".paymentGatewayDiscount").hide();
                }
            });
        }
        $.post("/control/refreshCheckoutPaymentCart",{_GIFT_VOUCHER_PURCHASE_:isGiftVoucherPurchaseRequest},
            function(checkoutPaymentCart) {
                $("#checkoutPaymentCart").html(checkoutPaymentCart);
            }
        );
        $(".loaderGif").hide();
        $(".payNowButton").attr("disabled","");
     }
  );
}



$(document).ready(function(){
   $(window).load(function() {
        $(this).changefun();
    });
  $.fn.changefun  = function(){
    //$(".remove_contactmech_tag").each(function(){
    //})
  };


    $.fn.changefunradio  = function(){
    if(this.val()=='Y') {
      $(this).parents(".new_shipping_address_row_right").find(".for_areacode,.address_label2").hide();
    } else {
      $(this).parents(".new_shipping_address_row_right").find(".for_areacode,.address_label2").show();
    }
    $('#CUSTOMER_CONTACT_NUMBER_error_img').removeClass('specific_error_img');
    $('#CUSTOMER_CONTACT_NUMBER_error_msg').html('');
    $('#CUSTOMER_CONTACT_NUMBERCredit_error_img').removeClass('specific_error_img');
    $('#CUSTOMER_CONTACT_NUMBERCredit_error_msg').html('');
  };



  $("input[name=shipping_contact_mech_id]").change(
    function()
    {
       $(this).changefun();
    });

  $("input[name=CUSTOMER_MOBILE]").change(
    function()
    {
       $(this).changefunradio();
    });

  });

 $(document).ready(function(){
	$(".address_boxes").hover(function() {
		$(this).find(".address_box_inside_right").addClass("hover_ship");
		$(this).children(".address_boxe_inside_left").find(".selected_tick_green").addClass("selected_tick_green_1");
	}, function() {
		$(".address_box_inside_right").removeClass("hover_ship");
		$(this).children(".address_boxe_inside_left").find(".selected_tick_green").removeClass("selected_tick_green_1");
	});

	$(".address_boxes_shipping").click(function() {
		$(".address_box_inside_right").removeClass("selected_ship");
	     $(".selected_tick_green").removeClass("selected_tick_green_2");
		$(this).find(".address_box_inside_right").addClass("selected_ship");
		$(this).children(".address_boxe_inside_left").find(".selected_tick_green").addClass("selected_tick_green_2");
		var contactMechId = $(this).attr('shippingContactMechId');
		$('#shipping_contact_mech_hidden').val(contactMechId);
		$('#checkoutInfoForm').submit();
	});
  });
  $(document).ready(function(){
	$(".address_boxes_billing").click(function() {

		$(".address_box_inside_right").removeClass("hover_ship");
		$(".address_box_inside_right").removeClass("selected_ship");
	    $(".selected_tick_green").removeClass("selected_tick_green_2");
		$(".address_boxes").find(".address_box_inside_right").addClass("hover_ship");
		$(this).find(".address_box_inside_right").addClass("selected_ship");
		$(this).children(".address_boxe_inside_left").find(".selected_tick_green").addClass("selected_tick_green_2");

			var contactMechId = $(this).attr('billingContactMechId');
			$('#billing_contact_mech_hidden').val(contactMechId);

			var name= $(this).parents(".address_boxes_main").find("#name_1").html();
			var address= $(this).parents(".address_boxes_main").find("#address_1").html();
			var city= $(this).parents(".address_boxes_main").find("#city_1").html();
			var pin= $(this).parents(".address_boxes_main").find("#pin_1").html();
			var state= $(this).parents(".address_boxes_main").find(".state_1").val();
			var areacode= $(this).parents(".address_boxes_main").find("#areacode_1").html();
			var mobile= $(this).parents(".address_boxes_main").find("#mobile_1").html();

			$("#newaddress").attr({"value":"N"});
			$("#toNameCredit").attr({value:name});
			$("#toNameCredit").focus();
			$("#address1Credit").attr({value:address});
			$("#address1Credit").focus();
			$("#cityCredit").attr({value:city});
			$("#cityCredit").focus();
			$("#postalCodeCredit").attr({value:pin});
			$("#postalCodeCredit").focus();
			$("#stateProvinceGeoIdCredit").attr({value:state});
			$("#stateProvinceGeoIdCredit").focus();

			if(areacode != null){
				$("#landline_checkedCredit").attr({checked:true});
				$(".for_areacode").show();
				$("#CUSTOMER_AREA_CODECredit").attr({value:areacode});
				$("#CUSTOMER_AREA_CODECredit").focus();
				$("#CUSTOMER_CONTACT_NUMBERCredit").attr({value:mobile});
				$("#CUSTOMER_CONTACT_NUMBERCredit").focus();
			}
			else{
				$("#mobile_checkedCredit").attr({checked:true});
				$(".for_areacode").hide();
				$("#CUSTOMER_AREA_CODECredit").attr({value:null});
				$("#CUSTOMER_AREA_CODECredit").focus();
				$("#CUSTOMER_CONTACT_NUMBERCredit").attr({value:mobile});
				$("#CUSTOMER_CONTACT_NUMBERCredit").focus();
				$("#ship_to_address_button").focus();
			}
	});
  });
 $(document).ready(function(){
 	$("#toNameCredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#address1Credit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#cityCredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#postalCodeCredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#stateProvinceGeoIdCredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#CUSTOMER_CONTACT_NUMBERCredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 	$("#CUSTOMER_AREA_CODECredit").change(function(){
 		$("#newaddress").attr({"value":"Y"});
 	});
 });
function billThroughCreditCard(){
  trackAsGoogleAnalyticsEvent('paynow', 'creditcard');
	var newaddress = $("#newaddress").val();
	if(newaddress == 'Y') {
		$('#billingAddressFormCredit').submit();
	}
	else{
		$('#billingAddressSelect').submit();
	}

}
function copyShippingAddressToBillingAddress(){
	$("#shipping_address_hidden_div > input").each(function(index, element){
		id = $(element).attr("id");
		newId = id.replace("Hidden","");
		elmVal = $(element).attr("value");
		if(newId== "CUSTOMER_AREA_CODECredit" && elmVal){
			$("input[name=CUSTOMER_MOBILE]").trigger('change');
            $("input[name=CUSTOMER_MOBILE]").filter("[value=N]").attr("checked", "checked");
		}
		$("#"+newId).val(elmVal);
	});
}

function clearBillingAddress(){
	$("#shipping_address_hidden_div > input").each(function(index, element){
		id = $(element).attr("id");
		newId = id.replace("Hidden","");
		$("#"+newId).val("");
	});
}

 $(document).ready(function(){

$(".mask_button").click(function(){

  alert("please unlock the button by sliding")

  });

$(".checked_shipping_address").click(function(){
	$("#debit_ship_address").toggle();
	if($(this).is(':checked')){
		copyShippingAddressToBillingAddress();
	}
	else
	{
		clearBillingAddress();
	}
});
$("#select_address").click(function(){
  $(this).parents(".select_from_address").find(".inside_select_from_address").slideDown(300);

  });

$(".add_address_close").click(function(){
  $(this).parents(".select_from_address").find(".inside_select_from_address").slideUp(300);

  });

});

/*
 $(document).ready(function()
    {
     $("input[name=password_display_check]").change(
    function()
    {
if($(this).parents().find("input[name=password_display_check]").is(':checked'))
  {
  $(this).parents().find(".password_display").slideDown(700);
  }
else
  {
   $(this).parents().find(".password_display").slideUp(700);
  }
    });

 });
*/


 $(document).ready(function()
    {
     $("input[name=login_password_display_check]").change(
    function()
    {
if($(this).parents(".login_content").find("input[name=login_password_display_check]").is(':checked'))
  {

  $(this).parents(".login_content").find(".dispaly_password_main").slideDown(700);
  }
else
  {

   $(this).parents(".login_content").find(".dispaly_password_main").slideUp(700);
  }
    });

 });


$(document).ready(function(){
  $(".payment_tab_body").find(".paymet_tab_sub_main").hide();
  $(".payment_tab_body").find(".paymet_tab_sub_main:first").show();

      $(".payment_option_tab_anon_details > div:first > span").addClass("selected_tab_anon");
      $(".payment_option_tab_anon_details > div > span").click(

          function(){
            $(".payment_option_tab_anon_details > div > span").removeClass("selected_tab_anon");

            $(this).addClass("selected_tab_anon");
            var showtabid = $(this).attr("rel");
            $(".payment_tab_body").find(".paymet_tab_sub_main").hide();
            $(".payment_tab_body").find("#" + showtabid ).show();
          }

      );
  });

$(document).ready(function(){
   $(".payment_option_tab_anon_details > div > span").each(function() {
    var selectedRel = $(this).attr('rel');
    var selectedPaymentTab = $('#payment_option_tab_anon_details').attr('selectedPaymentTab');
    if(selectedPaymentTab) {
      if(selectedRel === selectedPaymentTab) {
        $(this).trigger('click');
      }
    }
  }); 
});

$(document).ready(function(){

$('.edit_phone_number').toggle(function() {
var getnumber =  $(this).parent().find(".number_toggle").html();
$(this).parent().find(".number_toggle").replaceWith('<input maxlength="11" class="number_enter" type="text" ></input>');
$(this).parent().find("input").attr({"value":getnumber});
$(this).html("save");
}, function() {

var setnumber = $(this).parent().find("input").val();
 $(this).parent().find("input").replaceWith('<span class="number_toggle" ></span>');
 $(this).parent().find(".number_toggle").html(setnumber);
 $(this).html("change");
});

$(".qty_review_input").click(function(){$(this).blur()});
$('.qty_review_input_change').toggle(function() {

$(this).parent().find("input").removeAttr("readonly");
$(this).html("save");
$(this).parent().find("input").focus();
}, function() {

$(this).parent().find("input").attr({"readonly":"readonly"});
$(this).parent().find("input").click(function(){$(this).blur()})
$(this).html("change");

});

});

$(document).ready(function() {
  $(".help_question").hover(function(){

    $(this).parent(".new_shipping_address_row_right").find(".CCV_help_image").fadeIn(500);

  },
  function(){
    $(this).parent(".new_shipping_address_row_right").find(".CCV_help_image").hide();

  })

});
$(document).ready(function(){
	$(".discount_tab_code_text").click(function(){
		$(this).parents().find(".discount_tab_details").slideDown(700);
	});
});



$(document).click(function(event) {
	if (!$(event.target).hasClass('see_details_cart') && !$(event.target).parents().hasClass('see_details_cart')) {
		$(".inside_shopping_bag").fadeOut(1000);
	}
});
      
</script> 

 
<#assign cart = shoppingCart?if_exists />
<img src="/erptheme1/payment-mode.jpg" alt="" title=""/>
<form method="post" id="checkoutInfoForm" action="">
  <fieldset>
    <input type="hidden" name="checkoutpage" value="payment" />
    <input type="hidden" name="BACK_PAGE" value="checkoutoptions" />
    <input type="hidden" name="issuerId" id="issuerId" value="" />
    <input type="hidden" name="cartTotalAmt" id="cartTotalAmt" value="${cart.grandTotal?if_exists}" />
     <input type="hidden" id="shipping_instructions" name="shipping_instructions" value="${ins?if_exists}"/> 

    <div class="screenlet">
        <div class="screenlet-title-bar">
            <h3>Mode Of Payment</h3>
        </div>
        <div class="screenlet-body inline">
            <#-- Payment Method Selection -->
            <div>
            
            
            
            
            <script>
            function validateLink1(){
            	
            	
            	if(document.getElementById('paymentMethodTypeId1').checked){
            		document.getElementById('paymentMethodTypeId2').checked = false;
            		document.getElementById('checkOutPaymentId_COD').checked = false;
            		document.getElementById('paymentMethodTypeId3').checked = false;
            	}
            	
            	 
            }
            function validateLink2(){
            	
            	
            	 if(document.getElementById('paymentMethodTypeId2').checked){
            		document.getElementById('paymentMethodTypeId1').checked = false;
            		document.getElementById('checkOutPaymentId_COD').checked = false;
            		document.getElementById('paymentMethodTypeId3').checked = false;
            	}
            
            	 
            }
            
            function validateLink_COD(){
            	
            	 if(document.getElementById('checkOutPaymentId_COD').checked){
            	   
            		document.getElementById('paymentMethodTypeId2').checked = false;
            		document.getElementById('paymentMethodTypeId1').checked = false;
            		document.getElementById('paymentMethodTypeId3').checked = false;
            	}
            	
            	 
            }
            
            function validateLink_ICICI(){
            	 if(document.getElementById('checkOutPaymentId_ICICI').checked){
            	   
            		document.getElementById('paymentMethodTypeId2').checked = false;
            		alert("after");
            		document.getElementById('paymentMethodTypeId1').checked = false;
            		document.getElementById('paymentMethodTypeId3').checked = false;
            		
            	}
            	
            	 
            }
            
            
            function validateLink3(){
            	
            	if(document.getElementById('paymentMethodTypeId3').checked){
            		document.getElementById('paymentMethodTypeId2').checked = false;
            		document.getElementById('checkOutPaymentId_COD').checked = false;
            		document.getElementById('paymentMethodTypeId1').checked = false;
            	}
            	 
            }
            </script>
            
            
            <div class="payment_option_tab_main payment_option_tab_anon">
				<div class="payment_option_tab_anon_details" style="position:relative; top:-20px;" id="payment_option_tab_anon_details" selectedPaymentTab="">
				 
				             <!--div class="debit_card_anon" onclick="uncheckClass();">
				              	<span class="text" rel="debit_card" >Debit / Credit Card</span>
				             </div-->
				             <#assign cartAmt=cart.grandTotal?if_exists/>
					          <#if (cartAmt > 0)>
					          
					          <#assign flag = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].canBuyGiftCard(request)/>
					          
				    		<#if flag ==false>
				             <div class="cash_on_delivery_anon" onclick="uncheckClass();">
				              	<span class="text"  rel="cash_on_delivery" >Cash/Card on Delivery</span>
				             </div>
				             </#if>
				             <div class="cash_on_delivery_anon" onclick="uncheckClass();">
				              	<span class="text"  rel="DebitCredit" >Debit/Credit Card</span>
				             </div>
				             
				             <#--div class="cash_on_delivery_anon" onclick="uncheckClass();">
				              	<span class="text"  rel="Netbanking" >Netbanking</span>
				             </div-->
				             
				              <#--<div class="net_banking_anon" onclick="uncheckClass();">
				                <span class="text"  rel="net_banking" >Net Banking</span>
				              </div>-->
					         <div class="gift_voucher_anon" >
					                <span class="text"  rel="pay_voucher" >Redeem YM Savings</span>
					         </div>

					         <#else>
						         <div class="gift_voucher_anon">
						                <span class="text"  rel="gift_card_voucher" >Gift Card</span>
						         </div>
					         </#if>

					         <div class="net_banking_anon" onclick="uncheckClass();">
				                <span class="text"  rel="Invite_Friend_Ref" >Redeem IAF Discount</span>
				              </div>
					        

					         
					</div>        
				         
				
		        <div class="payment_tab_body">         
				         
				        <#-->div  id="debit_card" class="paymet_tab_sub_main" style="1px solid red;" >
								<div class="tab_payment_sub_head">
									<#--
									<span class="pay_using_credit">Pay Using Debit / Credit Card</span>
									<span class="we_accept">we accept</span>
									>
									<span class="we_accept">Pay Using Debit/Credit Card</span>
									<span class="card_image_visa"></span>
									<span class="card_image_master"></span>
								</div>
								<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
					  				<#--<#if productStorePaymentMethodTypeIdMap.EXT_ICICI_FULL?exists>>
					    			<tr><td colspan="3"><hr/></td></tr>
					    				<tr>
					        				<td width="25px"><input type="radio"   name="paymentMethodTypeId" id="paymentMethodTypeId1" value="EXT_ICICI_FULL"  onclick='validateLink1();' <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_ICICI_FULL">checked</#if>/></td>
					        				<td>
					        					<div class="tabletext123">Visa / Master<span style="font-size:10px;"> (Powered by ICICI Bank)</span></div>
					        				</td>
							                <td>	
					        					<div class="tabletext123"></div>			
					        				</td>
					  					</tr><tr><td colspan="3"><hr/></td></tr>
					  				<#--</#if>
					  				<#if productStorePaymentMethodTypeIdMap.EXT_CITI_FULL?exists>
						    				<tr>
						        				<td><input type="radio"  name="paymentMethodTypeId" value="EXT_CITI_FULL"  onclick="javascript:giftcard()" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_CITI_FULL">checked</#if>/></td>
						        				<td>
						        					<div class="tabletext">Pay With CITI Bank (Full Payment)</div>
						        				</td>
								                <td>	
						        					<div></div>			
						        				</td>
						  					</tr>
						  					<tr><td colspan="3"><hr/></td></tr>
									<#--</#if>
									<#--CCAVENUE(Amex) starts-->
									<#--<#if productStorePaymentMethodTypeIdMap.EXT_CCAVENUE?exists>>
					    				<tr>
					        				<td>
					        					<input type="radio" name="paymentMethodTypeId"  id="paymentMethodTypeId2" value="EXT_CCAVENUE"  onclick='validateLink2();' <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_CCAVENUE">checked</#if>/>
					        				</td>
					        				<td>
					        					<div class="tabletext123">Pay Through Amex Payment Gateway(Powered by CCAvenue).</div>
					        				</td>
							                <td>	
					        					<div class="tabletext123"></div>			
					        				</td>
					  					</tr>
					  					<tr><td colspan="3"><hr/></td></tr>
					  				</#if>
					  				<CCAVENUE(Amex) ends>
				  				</table>					    		
				            </div-->
							
							 <#assign flag = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].canBuyGiftCard(request)/>
				          <#assign flag = false>
				            <#if flag == false>
				            <div id="cash_on_delivery" class="paymet_tab_sub_main">
					  			<div class="tab_payment_sub_head">
					    			<#--
									<span class="pay_using_credit">Pay Using Net Banking</span>
									-->
									<span class="we_accept">Pay Using Cash on Delivery</span>
					  			</div>
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
				    				<#--<#if productStorePaymentMethodTypeIdMap.EXT_NETBANKING?exists>-->
			    						<tr><td colspan="3"><hr/></td></tr>
			    						<tr>
					        				<td width="5%">
					        				<#if productStorePaymentMethodTypeIdMap.EXT_COD?exists>
                                                  <input type="radio" id="checkOutPaymentId_COD" onclick="validateLink_COD();" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId></#if> />
											</#if>
                                            </td>
					        				<td>
					        					<div class="tabletext123"><label for="checkOutPaymentId_COD">Cash / Card on Delivery</label></div>
					        				</td>
										</tr>
										<tr><td colspan="3"><hr/></td></tr>
										
									<#--</#if>-->
									
								</table> 
				            </div> 
				            <#else>
				            	 <input type="radio" style="display:none"  id="checkOutPaymentId_COD" onclick="validateLink_COD();" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId></#if> />
				            </#if>
				            <div id="DebitCredit" class="paymet_tab_sub_main">
					  			<div class="tab_payment_sub_head">
					    			<#--
									<span class="pay_using_credit">Pay Using Net Banking</span>
									-->
									<span class="we_accept">Pay Using Credit/Debit Card</span>
					  			</div>
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
				    				<#--<#if productStorePaymentMethodTypeIdMap.EXT_NETBANKING?exists>-->
			    						<tr><td colspan="3"><hr/></td></tr>
			    						
										<tr>
					        				<td width="5%">
												<#if productStorePaymentMethodTypeIdMap.EXT_ICICI_FULL?exists>
	                                                  <input type="radio" id="checkOutPaymentId_ICICI" onclick="validateLink_ICICI();" name="checkOutPaymentId" value="EXT_ICICI_FULL" <#if "EXT_ICICI_FULL" == checkOutPaymentId></#if> />
												</#if>
											</td>
					        				<td>
					        					<div class="tabletext123"><label for="checkOutPaymentId_COD"><img src="/erptheme1/card-small.png"> <#--img src="/erptheme1/netbanking-icon.jpg"--></label></div>
					        				</td>
										</tr>
										<tr><td colspan="3"><hr/></td></tr>
									<#--</#if>-->
									
								</table> 
				            </div>
				            <#--div id="gift_card_voucher" class="paymet_tab_sub_main">
					  			<div class="tab_payment_sub_head">
									<span class="we_accept">Pay Using Gift Card</span>
					  			</div>
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
			    						<tr><td colspan="3"><hr/></td></tr>
			    						<tr>
					        				<td width="5%"><#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
                                                  <input type="radio" id="checkOutPaymentId_GIFTCARD" onclick="validateLink_GIFTCARD();" name="checkOutPaymentId" value="GIFT_CARD" <#if "GIFT_CARD" == checkOutPaymentId>checked</#if> />
                                                  </#if>
                                            </td>
					        				<td>
					        					<div class="tabletext123"><label for="checkOutPaymentId_GIFTCARD">Gift Card</label></div>
					        				</td>
										</tr>
										<tr><td colspan="3"><hr/></td></tr>
								</table> 
				            </div--> 
				            <div id="pay_voucher" class="paymet_tab_sub_main">
					  			<div class="tab_payment_sub_head">
					    			<#--
									<span class="pay_using_credit">Pay Using Net Banking</span>
									-->
									<span class="we_accept">YouMart Savings</span>
					  			</div>
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
				    				<#--<#if productStorePaymentMethodTypeIdMap.EXT_NETBANKING?exists>-->
			    						<tr><td colspan="3"><hr/></td></tr>
			    						<#if productStorePaymentMethodTypeIdMap.EXT_BILLACT?exists>
							            <#if billingAccountBalanced?has_content>
							            <#if (billingAccountBalanced > 0.00)>
							            <#if billingAccountList?has_content>
							            <tr>
			    							<td width="5%">
					        					<div class="tabletext123">YouMart saving Account No.</div>
					        				</td>
					        				<td><select name="billingAccountId" id="billingAccountId">
									                      <option value=""></option>
									                        <#list billingAccountList as billingAccount>
									                          <#assign availableAmount = billingAccount.accountBalance>
									                          <#assign accountLimit = billingAccount.accountLimit>
									                          <option value="${billingAccount.billingAccountId}" <#if billingAccount.billingAccountId == selectedBillingAccountId?default("")>selected="selected"</#if>>${billingAccount.description?default("")} [${billingAccount.billingAccountId}] ${uiLabelMap.EcommerceAvailable} <!--<@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId/>--><span class="WebRupee">&#8377;</span> ${availableAmount?if_exists}<#--${uiLabelMap.EcommerceLimit} <@ofbizCurrency amount=accountLimit isoCode=billingAccount.accountCurrencyUomId/--></option>
									                        </#list>
									                    </select>
									        </td>
					        				
										</tr>
							            <tr>
										    <td>
					        					<div class="tabletext123">Redeem Account</div>
					        				</td>
					        				<input type="hidden" id="leftBalance" value="${billingAccountBalanced?if_exists}">
					        				<td width="5%"><input type="text" size="5" id="billingAccountAmount" name="billingAccountAmount" value="" onblur="return chackReedeemAmt()" onkeypress="return isNumberKey(event);"/></td>
					        				
										</tr>
										</#if>
							            </#if> </#if>
							            </#if>
										<tr><td colspan="3"><hr/></td></tr>
									<#--</#if>-->
								</table>
								
				            </div> 
				            
				            
				     <!--//for invite friend ref -->
				             <div id="Invite_Friend_Ref" class="paymet_tab_sub_main">
				         
					  			<div class="tab_payment_sub_head">
					    	
									<span class="we_accept">Pay using Invite a Friend reference</span>
					  			</div>
					  			<#if productStorePaymentMethodTypeIdMap.EXT_INVITE?exists>
					  		
					  			 <#if totalInviteFriendAmount?has_content>
					  	
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
				    			
			    						<tr><td colspan="3"><hr/></td></tr>
			    						<tr>
					        				<td width="38%"><div class="tabletext123">Invite A Friend Reference Total Discount.</div></td>
					        				<td>
                                                  <input type="hidden" id="checkOutPaymentId_INVITE" name="checkOutPaymentId" value="EXT_INVITE"  />
                                              
                                                   <input type="text" style="width:45px;" id="totalAmount_INVITE" name="totalAmount" value="${totalInviteFriendAmount?if_exists}%" />
                                                 
                                            </td>
					        				</tr>
					        				<tr>
					        				<td width="30%"><div class="tabletext123">Redeem Discount.</div></td>
					        								        				
					        				<td><input type="text" size="5" id="inviteAccountAmount" name="amount_EXT_INVITE" onblur="return inviteReference()"/></td>
					        				</td>
										</tr>
										<tr><td colspan="3"><hr/></td></tr>
									<#--</#if>-->
									
								</table> 
									</#if>
								</#if>
								
				            </div> 
				              
				            
				            
				         <#--   <div id="net_banking" class="paymet_tab_sub_main">
					  			<div class="tab_payment_sub_head">
					    		
									<span class="pay_using_credit">Pay Using Net Banking</span>
									<span class="we_accept">Pay Using Net Banking</span>
					  			</div>
				    			<table cellpadding="0" cellspacing="0" border="0" width="100%" class="paymenttable">
				    				<#if productStorePaymentMethodTypeIdMap.EXT_NETBANKING?exists>
			    						</tr><tr><td colspan="3"><hr/></td></tr>
			    						<tr>
					        				<td><input type="radio"  name="paymentMethodTypeId" id="paymentMethodTypeId3" value="EXT_NETBANKING"  onclick='validateLink3();' <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_NETBANKING">checked</#if>/></td>
					        				<td>
					        					<div class="tabletext123">Pay With Net Banking</div>
					        				</td>
											<td rowspan="2">
												<ul class="emioptionslist">
													<li>EMI option is not available for Netbanking</li>
													<li>Please ensure sufficient balance in your account before purchase</li>
												</ul>
											  </td>
											</tr>
											<tr>
												<td>&nbsp;</td>
												<td>
													<select name="techprocessBankId" id="techprocessBankId" onchange="setBankId('techprocessBankId')">
												    <option value="-1">Select</option>
												    <option value="810">Airtel Money</option>
												    <option value="280">Allahabad Bank</option>
												    <option value="50">Axis Bank</option>
												    <option value="340">Bank of Bahrain and Kuwait</option>
												    <option value="310">Bank of Baroda</option>
												    <option value="240">Bank of India</option>
												    <option value="750">Bank of Maharashtra</option>
												    <option value="170">Bank Of Rajasthan</option>
												    <option value="320">Beam Cash Card</option>
												    <option value="740">Central Bank of India</option>
												    <option value="230">Citi Bank</option>
												    <option value="440">City Union Bank</option>
												    <option value="120">Corporation Bank</option>
												    <option value="540">DCB Bank</option>
												    <option value="330">Deutshe Bank</option>
												    <option value="370">Dhanlaxmi Bank</option>
												    <option value="270">Fedral Bank</option>
												    <option value="300">HDFC</option>
												    <option value="460">I-Cash Card</option>
												    <option value="10" selected="selected">ICICI Bank</option>
												    <option value="520">IDBI Bank</option>
												    <option value="490">Indian Bank</option>
												    <option value="420">Indian Overseas NetBanking</option>
												    <option value="830">ING Vysya Bank</option>
												    <option value="350">J&K Bank</option>
												    <option value="140">Karnataka Bank</option>
												    <option value="760">Karur Vysya Bank</option>
												    <option value="160">Oriental Bank of Commerce</option>
												    <option value="180">South Indian Bank</option>
												    <option value="450">Standard Chartered Bank</option>
												    <option value="560">State Bank Of Hyderabad</option>
												    <option value="530">State Bank Of India</option>
												    <option value="550">State Bank Of Mysore</option>
												    <option value="680">State Bank Of Travencore</option>
												    <option value="620">Tamilnad Mercantile Bank</option>
												    <option value="190">Union Bank Of India</option>
												    <option value="570">United Bank Of India</option>
												    <option value="200">Vijaya Bank</option>
												    <option value="130">YES Bank</option>
												</select>
												</td>
											</tr>
										</tr><tr><td colspan="3"><hr/></td></tr>
									</#if>
								</table> 
								
				            </div> -->  
				            
		<#--tab end-->		         
	 </div>
</div>			         
				         
				         
			        
              <#--  <label>${uiLabelMap.CommonAdd}:</label>
                <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
                  <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'NC', '');" class="buttontext">${uiLabelMap.AccountingCreditCard}</a>
                </#if>
                <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
                  <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'NE', '');" class="buttontext">${uiLabelMap.AccountingEFTAccount}</a>
                </#if>
              <#if productStorePaymentMethodTypeIdMap.EXT_OFFLINE?exists>
              </div>
              <div>
                  <input type="radio" id="checkOutPaymentId_OFFLINE" name="checkOutPaymentId" value="EXT_OFFLINE" <#if "EXT_OFFLINE" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_OFFLINE">${uiLabelMap.OrderMoneyOrder}</label>
              </div>
              </#if>
              <#if productStorePaymentMethodTypeIdMap.EXT_COD?exists>
              <div>
                  <input type="radio" id="checkOutPaymentId_COD" name="checkOutPaymentId" value="EXT_COD" <#if "EXT_COD" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_COD">${uiLabelMap.OrderCOD}</label>
              </div>
              </#if>
              <#if productStorePaymentMethodTypeIdMap.EXT_WORLDPAY?exists>
              <div>
                  <input type="radio" id="checkOutPaymentId_WORLDPAY" name="checkOutPaymentId" value="EXT_WORLDPAY" <#if "EXT_WORLDPAY" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_WORLDPAY">${uiLabelMap.AccountingPayWithWorldPay}</label>
              </div>
              </#if>
              <#if productStorePaymentMethodTypeIdMap.EXT_PAYPAL?exists>
              <div>
                  <input type="radio" id="checkOutPaymentId_PAYPAL" name="checkOutPaymentId" value="EXT_PAYPAL" <#if "EXT_PAYPAL" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_PAYPAL">${uiLabelMap.AccountingPayWithPayPal}</label>
              </div>
              </#if>
              <#if productStorePaymentMethodTypeIdMap.EXT_IDEAL?exists>
              <div>
                  <input type="radio" id="checkOutPaymentId_IDEAL" name="checkOutPaymentId" value="EXT_IDEAL" <#if "EXT_IDEAL" == checkOutPaymentId>checked="checked"</#if> />
                  <label for="checkOutPaymentId_IDEAL">${uiLabelMap.AccountingPayWithiDEAL}</label>
              </div>
              
              <div id="issuers">
              <div><label >${uiLabelMap.AccountingBank}</label></div>
                <select name="issuer" id="issuer">
                <#if issuerList?has_content>
                    <#list issuerList as issuer>
                        <option value="${issuer.getIssuerID()}" >${issuer.getIssuerName()}</option>
                    </#list>
                </#if>
              </select>
              </div>
              </#if>
              <#if !paymentMethodList?has_content>
              <div>
                  <strong>${uiLabelMap.AccountingNoPaymentMethods}.</strong>
              </div>
            <#else>
              <#list paymentMethodList as paymentMethod>
                <#if paymentMethod.paymentMethodTypeId == "GIFT_CARD">
                 <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
                  <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")?if_exists/>

                  <#if giftCard?has_content && giftCard.cardNumber?has_content>
                    <#assign giftCardNumber = "" />
                    <#assign pcardNumber = giftCard.cardNumber />
                    <#if pcardNumber?has_content>
                      <#assign psize = pcardNumber?length - 4 />
                      <#if 0 &lt; psize>
                        <#list 0 .. psize-1 as foo>
                          <#assign giftCardNumber = giftCardNumber + "*" />
                        </#list>
                        <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3] />
                      <#else>
                        <#assign giftCardNumber = pcardNumber />
                      </#if>
                    </#if>
                  </#if>

                  <div>
                      <input type="checkbox" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if> />
                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">${uiLabelMap.AccountingGift}:${giftCardNumber?if_exists}</label>
                        <#if paymentMethod.description?has_content>(${paymentMethod.description})</#if>
                        <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EG', '${paymentMethod.paymentMethodId}');" class="buttontext">${uiLabelMap.CommonUpdate}</a>
                        <strong>${uiLabelMap.OrderBillUpTo}:</strong> <input type="text" size="5" class="inputBox" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?string("##0.00")}</#if>" />
                  </div>
                 </#if>
                <#elseif paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                 <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
                  <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")?if_exists/>
                  <div>
                      <input type="checkbox" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if cart.isPaymentSelected(paymentMethod.paymentMethodId)>checked="checked"</#if> />
                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">CC:<#if creditCard?has_content>${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</#if></label>
                        <#if paymentMethod.description?has_content>(${paymentMethod.description})</#if>
                        <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EC', '${paymentMethod.paymentMethodId}');" class="buttontext">${uiLabelMap.CommonUpdate}</a>
                        <label for="amount_${paymentMethod.paymentMethodId}"><strong>${uiLabelMap.OrderBillUpTo}:</strong></label><input type="text" size="5" class="inputBox" id="amount_${paymentMethod.paymentMethodId}" name="amount_${paymentMethod.paymentMethodId}" value="<#if (cart.getPaymentAmount(paymentMethod.paymentMethodId)?default(0) > 0)>${cart.getPaymentAmount(paymentMethod.paymentMethodId)?string("##0.00")}</#if>" />
                  </div>
                 </#if>
                <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                 <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists>
                  <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount") />
                  <div>
                      <input type="radio" id="checkOutPayment_${paymentMethod.paymentMethodId}" name="checkOutPaymentId" value="${paymentMethod.paymentMethodId}" <#if paymentMethod.paymentMethodId == checkOutPaymentId>checked="checked"</#if> />
                      <label for="checkOutPayment_${paymentMethod.paymentMethodId}">${uiLabelMap.AccountingEFTAccount}:${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</label>
                        <#if paymentMethod.description?has_content><p>(${paymentMethod.description})</p></#if>
                      <a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'EE', '${paymentMethod.paymentMethodId}');" class="buttontext">${uiLabelMap.CommonUpdate}</a>
                  </div>
                 </#if>
                </#if>
              </#list>
            </#if>

            <#-- special billing account functionality to allow use w/ a payment method -->
            <#--<#if productStorePaymentMethodTypeIdMap.EXT_BILLACT?exists>
            <#if billingAccountBalanced?has_content>
            <#if (billingAccountBalanced > 0.00)>
              <#if billingAccountList?has_content>
                <div>
                    <select name="billingAccountId" id="billingAccountId">
                      <option value=""></option>
                        <#list billingAccountList as billingAccount>
                          <#assign availableAmount = billingAccount.accountBalance>
                          <#assign accountLimit = billingAccount.accountLimit>
                          <option value="${billingAccount.billingAccountId}" <#if billingAccount.billingAccountId == selectedBillingAccountId?default("")>selected="selected"</#if>>${billingAccount.description?default("")} [${billingAccount.billingAccountId}] ${uiLabelMap.EcommerceAvailable} <@ofbizCurrency amount=availableAmount isoCode=billingAccount.accountCurrencyUomId/> <#--${uiLabelMap.EcommerceLimit} <@ofbizCurrency amount=accountLimit isoCode=billingAccount.accountCurrencyUomId/--></option>
                        <#--</#list>
                    </select>
                    <label for="billingAccountId">${uiLabelMap.FormFieldTitle_billingAccountId}</label>
                </div>
                <div>
                    <input type="text" size="5" id="billingAccountAmount" name="billingAccountAmount" value="" />
                    <label for="billingAccountAmount">${uiLabelMap.OrderBillUpTo}</label>
                </div>
              </#if>
              </#if> </#if>
            </#if>
            <#-- end of special billing account functionality -->

            <#--<#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
              <div>
                  <input type="checkbox" id="addGiftCard" name="addGiftCard" value="Y" />
                  <input type="hidden" name="singleUseGiftCard" value="Y" />
                  <label for="addGiftCard">${uiLabelMap.AccountingUseGiftCardNotOnFile}</label>
              </div>
              <div>
                  <label for="giftCardNumber">${uiLabelMap.AccountingNumber}</label>
                  <input type="text" size="15" class="inputBox" id="giftCardNumber" name="giftCardNumber" value="${(requestParameters.giftCardNumber)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
              </div>
              <#if cart.isPinRequiredForGC(delegator)>
              <div>
                  <label for="giftCardPin">${uiLabelMap.AccountingPIN}</label>
                  <input type="text" size="10" class="inputBox" id="giftCardPin" name="giftCardPin" value="${(requestParameters.giftCardPin)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
              </div>
              </#if>
              <div>
                  <label for="giftCardAmount">${uiLabelMap.AccountingAmount}</label>
                  <input type="text" size="6" class="inputBox" id="giftCardAmount" name="giftCardAmount" value="${(requestParameters.giftCardAmount)?if_exists}" onfocus="document.getElementById('addGiftCard').checked=true;" />
              </div>
            </#if>

              <div>
                    <#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists><a href="<@ofbizUrl>setBilling?paymentMethodType=CC&amp;singleUsePayment=Y</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingSingleUseCreditCard}</a></#if>
                    <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists><a href="<@ofbizUrl>setBilling?paymentMethodType=GC&amp;singleUsePayment=Y</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingSingleUseGiftCard}</a></#if>
                    <#if productStorePaymentMethodTypeIdMap.EFT_ACCOUNT?exists><a href="<@ofbizUrl>setBilling?paymentMethodType=EFT&amp;singleUsePayment=Y</@ofbizUrl>" class="buttontext">${uiLabelMap.AccountingSingleUseEFTAccount}</a></#if>
              </div>-->
            <#-- End Payment Method Selection -->
        </div>
	    </div>
	  </fieldset>
	</form>



<div style="margin-top:10px;">
<table width="100%">
          <tr>
           <td>
           <#if flag ==false>
		    	<a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'CS', '');" class="buttontextblue" style="float:left;">${uiLabelMap.OrderBacktoShoppingCart}</a>
		   </#if>
		   </td>
		   <td>
				<div style="float:right;">
                      <#--<input type="button" value="Slot Selection" onclick="goBack()" class="buttoncustom-back"/> &nbsp; &nbsp;--> 
                      <#if flag ==false>
                      		<!-- div style="float:left; margin-right:25px;"><a href="javascript:void();" onclick="goBack()" class="buttoncustom1-back">Order Summary</a></div -->
                      </#if>
                      <!--<div style="float:right;"><a href="javascript:submitForm(document.getElementById('checkoutInfoForm'), 'DN', '');" class="buttoncustom">Review Order & Pay</a></div>-->
	          <div style="float:right; padding-right:0px;"><a href="#" onclick="return submitForm1(checkoutInfoForm);" class="buttoncustom"><#--${uiLabelMap.CommonNext}--> Confirm Order</a>
	          
	          </div>
	            </div>
	       </td>
	      </tr>
	   </table>
</div>
<script>
function goBack(){
	window.history.back();
}



</script>



