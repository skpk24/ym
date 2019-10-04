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

<script language="JavaScript" type="text/javascript">
<!--
    var clicked = 0;
    function processOrder() {
       // if (clicked == 0) {
            clicked++;
            //window.location.replace("<@ofbizUrl>processorder</@ofbizUrl>");
           // document.${parameters.formNameValue}.processButton.value="${uiLabelMap.OrderSubmittingOrder}";
            //document.${parameters.formNameValue}.processButton.disabled=true;
            document.${parameters.formNameValue}.submit();
      //  } else {
     //       showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.YoureOrderIsBeingProcessed}");
      //  }
    }
// -->
</script>
<img src="/erptheme1/order-summary.png" alt="" title=""/>
<h1 style="margin-top:10px; color:#8f0700; font-size:25px;">Order Review</h1>
<#if !isDemoStore?exists && isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>

<#if cart?exists && 0 < cart.size()>
  ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderheader")}
  <br />
  ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderitems")}
  <table border="0" cellpadding="1" width="100%">
   <tr>
      <td>
        <form type="post" action="<@ofbizUrl>checkoutoptions</@ofbizUrl>" name="${parameters.formNameValue}">
        	<input type="hidden" name="checkoutpage" id="checkoutpage" value="reviewBefore"/>
          <#if (requestParameters.checkoutpage)?has_content>
            <input type="hidden" name="checkoutpage" value="${requestParameters.checkoutpage}" />
          </#if>
          <#if (requestAttributes.issuerId)?has_content>
            <input type="hidden" name="issuerId" value="${requestAttributes.issuerId}" />
          </#if>
          <table width="100%">
          <tr>
           <td>
             <div style="float:left;"><a href="/control/view/showcart" class="buttontextblue checkoutbutton">${uiLabelMap.OrderBacktoShoppingCart}</a></div>
           </td>
           <td style="vertical-align:right;">
              <div style="float:right;">
                <#--div style="float:left; margin-right:15px;"><input type="button" value="Payment Mode" onclick="goBack();" class="buttoncustom-back12"/> </div--> 	
                <div style="float:right;">
                <a href="javascript:processOrder()" onclick="processOrder();" class="buttoncustom">Proceed to Pay</a>
                <#--input type="button" style="float:right;"  name="processButton" value="Proceed to Pay" onclick="processOrder();" class="buttoncustom" /--></div>
              </div>
           </td>
           </tr>
          </table>
        </form>
        <#-- doesn't work with Safari, seems to work with IE, Mozilla <a href="#" onclick="processOrder();" class="buttontextbig">[${uiLabelMap.OrderSubmitOrder}]&nbsp;</a> -->
      </td>
    </tr>
  </table>
<#else>
  <h3>${uiLabelMap.OrderErrorShoppingCartEmpty}.</h3>
</#if>
<script>
function goBack(){
	window.history.back();
}
</script>
