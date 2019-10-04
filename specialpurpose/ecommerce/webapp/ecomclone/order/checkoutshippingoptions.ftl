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

<script language="javascript" type="text/javascript">
<#if sameSlogFlag?has_content>
$(document).ready(function(){
   chekingSlot();
   
});
</#if>

	
function chekingSlot(){
	slotStatus=document.getElementById('slotFlag');
	if(slotStatus!=null && (slotStatus.value=="true")){
		document.getElementById("termsPopupbox").style.display='block';
		document.getElementById("popup_background").style.display='block';
		//$("#Ajaxshipping").attr('contenteditable','true');
	}
}
function addTermsCondDetails() {
document.getElementById("YesPopupbox").style.display='block';
}
function closeBack(){
 document.getElementById("termsPopupbox").style.display = "none";
 document.getElementById("popup_background").style.display="none";
 
 }
function NoCondition() {
var mechId=document.getElementById("mechId").value;
var  param = 'noCodition=' +"noCondition" + '&mechId=' + mechId;

 jQuery.ajax({url: "/control/checkoutShipmentOptionsAjax",
         data: param,
         type: 'post',
         context: $(this),
         async: false,
         
         success: function(data) {
         document.getElementById("termsPopupbox").style.display='none';
          document.getElementById("popup_background").style.display="none";
          $('#Ajaxshipping').html(data); 
        	 },
         error: function(data) {
         }
       
    });
    
 

}

$(document).ready(function(){
var n=$("#slotDayOption").val();

var m=$("#maxInterval").val();
jQuery("#deliveryDate").datepicker({
	minDate:+n,
  	maxDate:+m,
    showOn: 'button',
    buttonImage: '/images/icons/famfamfam/calendar.png',
    buttonText: '',
    onSelect: validate, 
    buttonImageOnly: true,
    dateFormat: 'dd-mm-yy'
  });

});
 function validate(deliveryDate)
      { 
        
   var n=document.getElementById("deliveryDate").value;
  var mechId=document.getElementById("mechId").value;
    var url = 'ContactSelection1';
        var  param = 'deliveryDate=' +deliveryDate + '&mechId=' + mechId;
       
        
      // new Ajax.Updater("slot1","/control/ContactSelection1", {parameters:{deliveryDate: deliveryDate}});
       	jQuery.ajax({url: '/control/ContactSelection1',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
	         
	         document.getElementById('slot1').innerHTML = data;
	         
	      
	         },
        error: function(data) {
        }
        });
         }

function submitForm1(form)
{
    var radios = document.getElementsByName("dayOption");
	var j=0;
    for (var i = 0; i < radios.length; i++) {       
        if (radios[i].checked) {
            j=j+1;
        }
    }
    if(j==0)
      {
    alert("Please Select Slot");
    return false;
    }
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
    return true;
    
 }
 function closeDiv(divId){
  	document.getElementById(divId).style.display = "none";     
  	
  	if(divId=="YesPopupbox")
  	{
  	document.getElementById("termsPopupbox").style.display='block';
  	 document.getElementById("popup_background").style.display='block';
  	} 
  	
  } 
  
 
  
   function mergeSlot(){

   var rates = document.getElementsByName('orderOption');
   var mechId=document.getElementById("mechId").value;
   document.getElementById("popup_background").style.display='block';
   
  
	var rate_value;
for(var i = 0; i < rates.length; i++){
    if(rates[i].checked){
        rate_value = rates[i].value;
    }
}
  if(rate_value!=null && rate_value!="")
  {
 var  param = 'orderId='+ rate_value + '&mechId=' + mechId;

  jQuery.ajax({url: "/control/checkoutShipmentOptionsAjax",
         data: param,
         type: 'post',
         context: $(this),
         async: false,
         
         success: function(data) {
         
          $('#Ajaxshipping').html(data); 
          document.getElementById("popup_background").style.display='none';
            mergeForm.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
          mergeForm.submit();
         
          },
         error: function(data) {
         }
       
    });
    }
    else
    {
    alert("Please select any one option");
    return false;
    }
  return true;
  }
 
    
</script>
    <div id="Ajaxshipping">
<img src="/erptheme1/slot-selection.jpg" alt="" title=""/>
<input type="hidden" name="slotDayOption" id="slotDayOption" value=${slotDayOption?if_exists}>
<input type="hidden" name="maxInterval" id="maxInterval" value=${maxInterval?if_exists}>
<form method="post" name="checkoutInfoForm" id="checkoutInfoForm" style="margin:0;" >
   <input type="hidden" name="checkoutpage" id="checkoutpage" value="shippingoptions"/>
            <input type="hidden" id="shipping_instructions" name="shipping_instructions" value="${ins?if_exists}"/> 
     <#assign counter = 0>
              <#list carrierShipmentMethodList as carrierShipmentMethod>
                <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
               
                <tr>
                  <td width="1%" valign="top" >
                <input type="radio" style="display:none" name="shipping_method" id="shipping_method" value="${shippingMethod}" <#if (shippingMethod == chosenShippingMethod?default("N@A")) || (counter == 0)>checked</#if>/>
                  </td>
                  <td valign="top">
                    <div class="efttext">
                      <#if shoppingCart.getShippingContactMechId()?exists>
                        <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
                      </#if>
                      <#if carrierShipmentMethod.partyId != "_NA_"></#if>
                    
                    </div>
                  </td>
                </tr>
                <#assign counter = counter + 1>
              </#list>
           
	<div class="screenlet-title-bar">
        <div class="h3">Pick The Delivery Slot</div>
    </div>
    
    <div class="screenlet" style="height: 100%;">
        <div class="screenlet-body" style="height: 100%;">SSSSSSSSSSSSSSSSSSSSSSSS
            <table width="100%" cellpadding="1" border="0" cellpadding="0" cellspacing="1" class="deliveryslottab">
                  <tr class="slotRow">
                  	<td class="slotTopHdrDate" rowspan="2">Day Option</td>
                    <#if slottype1?exists  && slottype1?has_content>
			        <#list slottype1 as slotType1>
			        <td class="slotTopHdrDate">
			        		SKP  === ${slotType1.slotType?if_exists}
			        </td>
			        </#list>
			        </#if>
                  </tr>
                  <tr class="slotRow">
                  <#if slottype1?exists  && slottype1?has_content>
			        <#list slottype1 as slotType1>
			        <td class="slotTopHdrDate">
			       VVVVVVVV --- ${slotType1.slotTiming?if_exists}
			        </td>
			        </#list>
			        </#if>
                  </tr>
                  	 
                  	 
                  <#if today?exists  && today?has_content>
                  <#assign i=0?number>
			        <#list today.keySet() as ckey>
				        <tr class="slotRow">
					         <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp(),i?number)>
			                 <td width="20%" class="dayoption">${nowTimestamp?string("dd-MM-yyyy")?if_exists}</td>
					         <#assign map=today.get(ckey)>
			                 <#list map.keySet() as map1>
				               		<#if map.get(map1).contains("Blocked")>
				                    	<td id="blocked">Blocked</td>
				                    <#else>
				                    	<td>	
				                    		<input type="radio" id="dayOption" name="dayOption" value="${map1?if_exists}" /> ${map.get(map1)?if_exists}	
				                    	</td>
				                   </#if>
			                 </#list>
			          	</tr>
                    	<#assign i=i+1>
                    </#list>
                    </#if>
               
           
            <tr><td colspan="5" class="slotTopHdrDate">Advance Booking</td></tr>
            <tr><td class="dayoption">Delivery Date:</td>
			<td><input id="deliveryDate" type="text"  name="deliveryDate"  size="10" maxlength="10" /></td>
			
			<td colspan="3"><span id="slot1"></span></td>
            <input type="hidden" name="slotFlag" id="slotFlag" value="${sameSlogFlag?if_exists}"/>
             <input type="hidden" name="mechId" id="mechId" value="${mechId?if_exists}"/>
            </table>
     	</form>
     
		<form method="post" name="mergeForm" id="mergeForm" style="margin:0;" >
  			 <input type="hidden" name="checkoutpage" id="checkoutpage" value="shippingoptions"/>
  			 <input type="hidden" name="dayOption"  value="${dayOption?if_exists}"/>
           	 <input type="hidden" id="shipping_instructions" name="shipping_instructions" value="${ins?if_exists}"/> 
           	  <input type="hidden" name="mechId" id="mechId" value="${mechId?if_exists}"/>
     			<#assign counter = 0>
             	 <#list carrierShipmentMethodList as carrierShipmentMethod>
                <#assign shippingMethod = carrierShipmentMethod.shipmentMethodTypeId + "@" + carrierShipmentMethod.partyId>
               
                <tr>
                  <td width="1%" valign="top" >
                <input type="radio" style="display:none" name="shipping_method" id="shipping_method" value="${shippingMethod}" <#if (shippingMethod == chosenShippingMethod?default("N@A")) || (counter == 0)>checked</#if>/>
                  </td>
                  <td valign="top">
                    <div class="efttext">
                      <#if shoppingCart.getShippingContactMechId()?exists>
                        <#assign shippingEst = shippingEstWpr.getShippingEstimate(carrierShipmentMethod)?default(-1)>
                      </#if>
                      <#if carrierShipmentMethod.partyId != "_NA_"></#if>
                    
                    </div>
                  </td>
                </tr>
                <#assign counter = counter + 1>
              </#list>
           </form>
	
	 <table width="100%">
  <tr>
    <td align="left">
      <a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="buttontextblue checkoutbutton">${uiLabelMap.OrderBacktoShoppingCart}</a>
    </td>
    <td align="right" style="text-align:right;">
       <#--<input type="button" value="Shipping Address" class="buttoncustom-back" onclick="goBack()"/>&nbsp; &nbsp;-->
        <div style="float:right;">
	       <div style="float:left; margin-right:5px;"><a href="javascript:void();" onclick="goBack()" class="buttoncustom1-back"><#--${uiLabelMap.CommonNext}--> Shipping Address</a> </div>  	
	       <div style="float:right; padding-right:0px;"><a href="#" onclick="return submitForm1(checkoutInfoForm);" style="text-align:center !important;font-size:13pt !important;" class="buttoncustom"><#--${uiLabelMap.CommonNext}-->Continue</a></div>
    	</div>
    </td>
  </tr>
</table>    
</div>
</div>
</div>
<#if orderList?has_content>
	<div id="popup_background" style="background: none repeat scroll 0 0 #000000;height: 100%;left: 0;opacity: 0.8;position: fixed;top: 0;width: 100%;z-index: 999999990;display:none;"></div>
	<div id="termsPopupbox" style="display:none; position:absolute; top:133px; left:350px;  height:150px; z-index:999999991; background-color:#fff;">
 	
	<table border="0"  width="500">
	<tr>
	<td style="padding:8px; text-align:left !important;" >Dear ${partyName?if_exists} , You already have an order pending for delivery; would you prefer this order to be merged/combined with your pending order</td>
	</tr>
	<tr align="center">
	<td style="text-align:center !important;">
	<input  type="button" size="5" value="Yes"  onClick="addTermsCondDetails(); closeDiv('termsPopupbox');" />
	<input  type="button" size="5" value="No"  onClick="NoCondition(); closeBack();" />
    <#-- <input  type="button" size="7" value="Cancel" class="smallSubmit" onClick="closeDiv('termsPopupbox');"/>-->	 
                   	</td>
				</tr>
 			</table>
	</div>
	
	<div id="YesPopupbox" style="display:none; position:absolute; top:133px; left:350px; z-index:999999991; background-color:#fff;border:1px #c2c2c2 solid;">
	<table border="0"  width="500">
	<tr>
	<td colspan="4"  style="padding:8px; text-align:left !important;" >Please Select any one order:</td>
	</tr>
	<#else>
	<div id="popup_background" style="background: none repeat scroll 0 0 #000000;height: 100%;left: 0;opacity: 0.8;position: fixed;top: 0;width: 100%;z-index: 999999990;display:none;"></div>
	<div id="termsPopupbox" style="display:none; position:absolute; top:133px; left:350px;  height:150px; z-index:999999991; background-color:#fff;">
 	
	<table border="0"  width="500">
	<tr>
	<td style="padding:8px; text-align:left !important;" >Dear ${partyName?if_exists} , Your  merged/combined with your pending order cut-off time is over.</td>
	</tr>
	<tr align="center">
	<td style="text-align:center !important;">
	<#--<input  type="button" size="5" value="Yes"  onClick="addTermsCondDetails(); closeDiv('termsPopupbox');" />-->
	<input  type="button" size="5" value="OK"  onClick="NoCondition(); closeBack();" />
    <#-- <input  type="button" size="7" value="Cancel" class="smallSubmit" onClick="closeDiv('termsPopupbox');"/>-->	 
                   	</td>
				</tr>
 			</table>
	</div>
	
	<div id="YesPopupbox" style="display:none; position:absolute; top:133px; left:350px; z-index:999999991; background-color:#fff;border:1px #c2c2c2 solid;">
	<table border="0"  width="500">
	<tr>
	<td colspan="4"  style="padding:8px; text-align:left !important;" >Please Select any one order:</td>
	</tr>
	 </#if>
	 
	<#if orderList?has_content>
	<tr>
	<th style="text-align:left !important;">Order Id</th>
	<th style="text-align:left !important;">Delivery Date</th> 
	<th style="text-align:left !important;">Slot Type</th> 
	<th style="text-align:left !important;">Grand Total</th>
	</tr>
	<#list orderList as orderlist>
		<tr>
		<td style="text-align:left !important;">
		<input type="radio"  name="orderOption" id="orderOption" value="${orderlist.orderId}"/>${orderlist.orderId}
		</td>
		<td style="text-align:left !important;"> ${orderlist.deliveryDate?string("dd-MM-yyyy")?if_exists}</td>
		<#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
		<#assign slotTime123 = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(currentSlotList, Static["org.ofbiz.base.util.UtilMisc"].toMap("slotType",orderlist.slot?if_exists)), "slotTiming", true)>
		<#if slotTime123?has_content>
		 <#list slotTime123 as slotTiming>
		<#assign slotTiming = slotTiming?if_exists>
		<td style="text-align:left !important;"> ${orderlist.slot}->${slotTiming}</td>
		 </#list>
		 </#if>
		<td style="text-align:left !important;">  <span class="WebRupee">&#8377;</span>&nbsp;${orderlist.grandTotal}</td>
		</tr>
	</#list>
	</#if>
	
	<tr>
	<td colspan="4" style="text-align:center !important;">
	<input  type="button" size="5" value="Submit"  onClick="return mergeSlot(); closeDiv('YesPopupbox');" />
	 <input  type="button" size="7" value="Cancel" class="smallSubmit" onClick="closeDiv('YesPopupbox');"/>
	</td>
	</tr>
	</table>
	</div>
	
	
	
	
<script>
function goBack(){
	window.history.back();
}


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
</script>


