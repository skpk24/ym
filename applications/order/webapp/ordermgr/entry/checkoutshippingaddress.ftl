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

<script type="text/javascript">
//<![CDATA[
var flag = true;
function submitFormCheckPostalCode(form, mode, value){
var totalCount = document.getElementById("totalCount").value;
for(var i = 0;i<totalCount;i++)
	if(document.getElementById("shipping_contact_mech_id_"+i).checked)
	{
	   Globalflag = true;
				            checkCode(document.getElementById("shipping_contact_postal_"+i).value);
				             if(!Globalflag){
				          <#--  alert("Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.");-->
				          alert("Sincerely apologize as we have not commenced our services in your selected area. Request you to change selected shipping address.");
				            flag =  false;
				             break;
				            }
				          
	}
	 
  submitForm(form, mode, value);	
  flag = true;
}

function submitForm(form, mode, value) {

    if (mode == "DN" && flag) {
        // done action; checkout
        form.action="<@ofbizUrl>checkoutoptions</@ofbizUrl>";
        form.submit();
    } else if (mode == "CS") {
        // continue shopping
        form.action="<@ofbizUrl>updateCheckoutOptions/showcart</@ofbizUrl>";
        form.submit();
    } else if (mode == "NA") {
        // new address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&contactMechPurposeTypeId=SHIPPING_LOCATION&DONE_PAGE=checkoutshippingaddress</@ofbizUrl>";
        form.submit();
    } else if (mode == "EA") {
        // edit address
        form.action="<@ofbizUrl>updateCheckoutOptions/editcontactmech?DONE_PAGE=checkoutshippingaddress&contactMechId="+value+"</@ofbizUrl>";
        form.submit();
    }
}



 var Globalflag = true;
   function checkCode(value){
	var url="/control/checkPostalCode?locationSearch="+value;
    jQuery.ajax({url: url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
       
        if(data != "success"){
	        Globalflag = false;
        } 
 	   return true;
	  },
        error: function(data) {
          alert("Oops! something went wrong");
            return false; 
        }
    });  
			return true;
	}
	
	
	
function toggleBillingAccount(box) {
    var amountName = box.value + "_amount";
    box.checked = true;
    box.form.elements[amountName].disabled = false;

    for (var i = 0; i < box.form.elements[box.name].length; i++) {
        if (!box.form.elements[box.name][i].checked) {
            box.form.elements[box.form.elements[box.name][i].value + "_amount"].disabled = true;
        }
    }
}

//]]>
</script>
<img src="/erptheme1/shipping-address.jpg" alt="" title=""/>

<form method="post" name="checkoutInfoForm" id="checkoutInfoForm" style="margin:0;">
<#assign cart = shoppingCart?if_exists/>
    <input type="hidden" name="checkoutpage" value="shippingaddress"/>
     <input type="hidden" id="shipping_instructions" name="shipping_instructions" value="${ins?if_exists}"/> 
     <div class="screenlet" style="height: 100%;">
        <div class="screenlet-title-bar">
            <div class="h3">&nbsp;Shipping Address</div>
        </div><br/>
        <#assign createNewAddress = true>
        <div class="screenlet-body" style="height: 100%;">
            <table width="100%" border="0" cellpadding="1" cellspacing="0">
              <tr>
                <td colspan="2">
                 <!--<a href="<@ofbizUrl>splitship</@ofbizUrl>" class="buttontext">${uiLabelMap.OrderSplitShipment}</a>-->
                 <#-- <a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');" class="buttontextblue">Alternate Address</a> -->
                  <#if (cart.getShipGroupSize() > 1)>
                    <div style="color:red;">${uiLabelMap.OrderNOTEMultipleShipmentsExist}</div>
                  </#if>
                </td>
              </tr>
               <#assign count = 0>
               <#if shippingContactMechList?has_content>
               	<#assign createNewAddress = false>
                 <tr><td colspan="2"><hr /></td></tr>
                 <#list shippingContactMechList as shippingContactMech>
                   <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")?if_exists>
                    <#if shippingAddress?has_content>
                   <#assign checkThisAddress = (shippingContactMech_index == 0 && !cart.getShippingContactMechId()?has_content) || (cart.getShippingContactMechId()?default("") == shippingAddress.contactMechId)/>
                   </#if>
                   <tr>
                     <td valign="top" width="1%" nowrap="nowrap">
                       <input type="radio" name="shipping_contact_mech_id" id="shipping_contact_mech_id_${count}"  value="${shippingAddress.contactMechId}"<#if checkThisAddress> checked="checked"</#if> />
                        <input type="hidden" name="shipping_contact_postal" id="shipping_contact_postal_${count}"  value="${shippingAddress.postalCode}"/>
                     </td>
                     <td valign="top" width="99%" nowrap="nowrap">
                       <div style="font-size:12px;">
                         <#if shippingAddress.toName?has_content><b><#--${uiLabelMap.CommonTo}:</br>Name :-->
                          <#if shippingAddress?has_content><b><#-- Address :--></b></#if>
                         <#if shippingAddress.attnName?has_content><#--<b>${uiLabelMap.PartyAddrAttnName}:</b>-->${shippingAddress.attnName}${shippingAddress.toName}<br /></#if></#if>
                         <#if shippingAddress.address1?has_content>${shippingAddress.address1}<br /></#if>
                         <#if shippingAddress.address2?has_content>${shippingAddress.address2}<br /></#if>
                         <#if shippingAddress.area?has_content>${shippingAddress.area}<br /></#if>
                         <#if shippingAddress.directions?has_content>${shippingAddress.directions}<br /></#if>
                         <#if shippingAddress.city?has_content>${shippingAddress.city}</#if>
                         <#if shippingAddress.stateProvinceGeoId?has_content><br />${shippingAddress.stateProvinceGeoId}</#if>
                         <#if shippingAddress.postalCode?has_content><br />${shippingAddress.postalCode}</#if>
                         <#if shippingAddress.countryGeoId?has_content><br />${shippingAddress.countryGeoId}</#if></br></br>
                     
                         <!--<a href="javascript:submitForm(document.checkoutInfoForm, 'EA', '${shippingAddress.contactMechId}');" class="buttontext" style="font-size:11px;">${uiLabelMap.CommonUpdate}</a>-->
                       
                       </div>
                     </td>
                   </tr>
                   <tr><td colspan="2"><hr /></td></tr>
                      <#assign count = count  + 1>
                 </#list>
               </#if>
              </table>

              
              <a href="javascript:submitForm(document.checkoutInfoForm, 'NA', '');" class="buttontextblue" style="margin-left:7px;">
              <#if createNewAddress>
              		Enter Shipping address
              <#else>
              		Alternate Address
              </#if>
              </a>
             
        </div>
    </div>
    <input type="hidden" id="totalCount" name="totalCount"  value="${count}"/>
</form>

<table width="100%">
  <tr valign="top">
    <td style="padding-top:4px;">
      &nbsp;<a href="javascript:submitForm(document.checkoutInfoForm, 'CS', '');" class="buttontextblue checkoutbutton">${uiLabelMap.OrderBacktoShoppingCart}</a>
    </td>
    <td style="text-align:right;">
      <a href="javascript:submitFormCheckPostalCode(document.checkoutInfoForm, 'DN', '');" style="text-align:center !important;font-size:11pt !important;" class="buttoncustom"><#--${uiLabelMap.CommonNext}-->Delivery Slot</a>
    </td>
  </tr>
</table>
