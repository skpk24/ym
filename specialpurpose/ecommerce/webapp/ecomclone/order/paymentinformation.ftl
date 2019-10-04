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
<#if requestParameters.paymentMethodTypeId?has_content>
   <#assign paymentMethodTypeId = "${requestParameters.paymentMethodTypeId?if_exists}">
</#if>
<script language="JavaScript" type="text/javascript">
function shipBillAddr() {
    <#if requestParameters.singleUsePayment?default("N") == "Y">
      <#assign singleUse = "&singleUsePayment=Y">
    <#else>
      <#assign singleUse = "">
    </#if>
    if (document.billsetupform.useShipAddr.checked) {
        window.location.replace("setPaymentInformation?createNew=Y&addGiftCard=${requestParameters.addGiftCard?if_exists}&paymentMethodTypeId=${paymentMethodTypeId?if_exists}&useShipAddr=Y${singleUse}");
    } else {
        window.location.replace("setPaymentInformation?createNew=Y&addGiftCard=${requestParameters.addGiftCard?if_exists}&paymentMethodTypeId=${paymentMethodTypeId?if_exists}${singleUse}");
    }
}
</script>
<div class="outside-screenlet-header">
    <div class='boxhead'>${uiLabelMap.AccountingPaymentInformation}</div>
</div>
<div class="screenlet">
    <div class="screenlet-body">
          <#-- after initial screen; show detailed screens for selected type -->
          <#if paymentMethodTypeId?if_exists == "CREDIT_CARD">
            <#if creditCard?has_content && postalAddress?has_content && !requestParameters.useShipAddr?exists>
              <form method="post" action="<@ofbizUrl>changeCreditCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
                <input type="hidden" name="paymentMethodId" value="${creditCard.paymentMethodId?if_exists}"/>
                <input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}"/>
            <#elseif requestParameters.useShipAddr?exists>
              <form method="post" action="<@ofbizUrl>enterCreditCard</@ofbizUrl>" name="${parameters.formNameValue}">
            <#else>
              <form method="post" action="<@ofbizUrl>enterCreditCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
            </#if>
          <#elseif paymentMethodTypeId?if_exists == "DEBIT_CARD">
			<#if debitCard?has_content && postalAddress?has_content && !requestParameters.useShipAddr?exists>
  				<form method="post" action="<@ofbizUrl>changeDebitCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}" onSubmit='return validateCSC();'>
    				<input type="hidden" name="paymentMethodId" value="${debitCard.paymentMethodId?if_exists}"/>
    				<input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}"/>
			<#elseif requestParameters.useShipAddr?exists>
  					<form method="post" action="<@ofbizUrl>enterDebitCard</@ofbizUrl>" name="${parameters.formNameValue}" onSubmit='return validateCSC();'>
			<#else>
  					<form method="post" action="<@ofbizUrl>enterDebitCardAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}" onSubmit='return validateCSC();'>
			</#if>
          <#elseif paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
			<#if eftAccount?has_content && postalAddress?has_content>
				<form method="post" action="<@ofbizUrl>changeEftAccountAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
				<input type="hidden" name="paymentMethodId" value="${eftAccount.paymentMethodId?if_exists}"/>
				<input type="hidden" name="contactMechId" value="${postalAddress.contactMechId?if_exists}"/>
			<#elseif requestParameters.useShipAddr?exists>
				<form method="post" action="<@ofbizUrl>enterEftAccount</@ofbizUrl>" name="${parameters.formNameValue}">
			<#else>
				<form method="post" action="<@ofbizUrl>enterEftAccountAndBillingAddress</@ofbizUrl>" name="${parameters.formNameValue}">
			</#if>
		 <#elseif paymentMethodTypeId?if_exists == "EXT_DIRECPAY">
			<form method="post" action="<@ofbizUrl>processPaymentSettingsLogin</@ofbizUrl>" name="${parameters.formNameValue}">
			<input type="hidden" name="paymentMethodId" value="EXT_DIRECPAY"/>           
          <#elseif paymentMethodTypeId?if_exists == "GIFT_CARD"> <#--Don't know much how this is handled -->
            <form method="post" action="<@ofbizUrl>enterGiftCard</@ofbizUrl>" name="${parameters.formNameValue}">
          <#elseif paymentMethodTypeId?if_exists == "EXT_OFFLINE">
            <form method="post" action="<@ofbizUrl>processPaymentSettings</@ofbizUrl>" name="${parameters.formNameValue}">
          <#else>
            <div class="tabletext">${uiLabelMap.AccountingPaymentMethodTypeNotHandled} ${paymentMethodTypeId?if_exists}</div>
          </#if>

          <#if requestParameters.singleUsePayment?default("N") == "Y">
            <input type="hidden" name="singleUsePayment" value="Y"/>
            <input type="hidden" name="appendPayment" value="Y"/>
          </#if>
          <input type="hidden" name="contactMechTypeId" value="POSTAL_ADDRESS"/>
          <input type="hidden" name="partyId" value="${partyId}"/>
          <input type="hidden" name="paymentMethodTypeId" value="${paymentMethodTypeId?if_exists}"/>
          <input type="hidden" name="createNew" value="Y"/>
          <#if requestParameters.useShipAddr?exists>
            <input type="hidden" name="contactMechId" value="${parameters.contactMechId?if_exists}"/>
          </#if>

          <table width="100%" border="0" class="tablealignleft" cellpadding="1" cellspacing="0">
            <#if cart.getShippingContactMechId()?exists && paymentMethodTypeId?if_exists != "GIFT_CARD">
              <tr>
                <td colspan="2" valign="top">
                  <div class="tabletext"><input type="checkbox" name="useShipAddr" value="Y" onClick="javascript:shipBillAddr();" <#if useShipAddr?exists>checked</#if>/> &nbsp;${uiLabelMap.FacilityBillingAddressSameShipping}</div>
                </td>
              </tr>
              <tr>
                <td colspan="2"><hr/></td>
              </tr>
            </#if>

            <#if (paymentMethodTypeId?if_exists == "CREDIT_CARD" || paymentMethodTypeId?if_exists == "DEBIT_CARD"  || paymentMethodTypeId?if_exists == "EFT_ACCOUNT")>
              <tr>
                <td width="26%" align="left" valign="top"><div class="tableheadtext"><b>${uiLabelMap.PartyBillingAddress}</b></div></td>
                <td width="74%">&nbsp;</td>
              </tr>
              ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#genericaddress")}
            </#if>

            <#-- credit card fields -->
            <#if paymentMethodTypeId?if_exists == "CREDIT_CARD">
              <#if !creditCard?has_content>
                <#assign creditCard = requestParameters>
              </#if>
              <tr>
                <td colspan="2"><hr/></td>
              </tr>
              <tr>
    				<td align="left" colspan="2" valign="top">
    				
    				<div class="tableheadtext"><b>${uiLabelMap.AccountingCreditCardInformation}</b></div>
    				</td>
  				</tr>

              ${screens.render("component://accounting/widget/CommonScreens.xml#creditCardFields")}
            </#if>
			<#-- Debit Card Code -->
			<#if paymentMethodTypeId?if_exists == "DEBIT_CARD">
	             <#if !debitCard?has_content>
	                <#assign debitCard = requestParameters>
	             </#if>
  				<tr>
    				<td colspan="2" height="20"><hr /></td>
  				</tr>
  				<tr>
    				<td colspan="2" align="left" valign="top">
    				<div class="tableheadtext">${uiLabelMap.AccountingDebitCardInformation}</div>
    				</td>
  				</tr>
  				<tr>
    				<td align="left" valign="middle"><b>${uiLabelMap.AccountingCompanyNameOnCard}</b></td>
    				<td>
      					<input type="text" size="30" maxlength="60" name="companyNameOnCard" value="${debitCard.companyNameOnCard?if_exists}"/>
    				</td>
  				</tr>
  				<tr>
    				<td align="left" valign="middle"><b>${uiLabelMap.AccountingPrefixCard}</b></td>
    				<td>
		                  <select name="titleOnCard" class="selectBox">
		                    <option value="">Select One</option>
		                    <option<#if ((debitCard.titleOnCard)?default("") == "Mr.")> selected</#if>>${uiLabelMap.CommonTitleMr}</option>
		                    <option<#if ((debitCard.titleOnCard)?default("") == "Mrs.")> selected</#if>>${uiLabelMap.CommonTitleMrs}</option>
		                    <option<#if ((debitCard.titleOnCard)?default("") == "Ms.")> selected</#if>>${uiLabelMap.CommonTitleMs}</option>
		                    <option<#if ((debitCard.titleOnCard)?default("") == "Dr.")> selected</#if>>${uiLabelMap.CommonTitleDr}</option>
		                  </select>
    				</td>
  				</tr>
  				<tr>
	                <td align="left" valign="middle"><b>${uiLabelMap.AccountingFirstNameCard}</b></td>
	                <td>
	                  <input type="text" size="20" maxlength="60" name="firstNameOnCard" value="${(debitCard.firstNameOnCard)?if_exists}"/>
	                  <span style="color:#FF0000"> *</span>
	                </td>
  				</tr>
	            <tr>
	                <td align="left" valign="middle"><b>${uiLabelMap.AccountingMiddleNameCard}</b></td>
	                <td>
	                  <input type="text" size="20" maxlength="60" name="middleNameOnCard" value="${(debitCard.middleNameOnCard)?if_exists}"/>
	                </td>
	           </tr>
	           <tr>
	                <td align="left" valign="middle"><b>${uiLabelMap.AccountingLastNameCard}</b></td>
	                <td>
	                  <input type="text"  size="20" maxlength="60" name="lastNameOnCard" value="${(debitCard.lastNameOnCard)?if_exists}"/>
	                  <span style="color:#FF0000"> *</span>
	                </td>
	           </tr>
  				<tr>
    				<td align="left" valign="middle"><b>${uiLabelMap.AccountingSuffixCard}</b></td>
    				<td>
		                  <select name="suffixOnCard" class="selectBox">
		                    <option value="">Select One</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "Jr.")> selected</#if>>Jr.</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "Sr.")> selected</#if>>Sr.</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "I")> selected</#if>>I</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "II")> selected</#if>>II</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "III")> selected</#if>>III</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "IV")> selected</#if>>IV</option>
		                    <option<#if ((debitCard.suffixOnCard)?default("") == "V")> selected</#if>>V</option>
		                  </select>
    				</td>
  				</tr>
  				<tr>
    				<td align="left" valign="middle"><b>${uiLabelMap.AccountingCardType}</b></td>
    				<td>
	                  <select name="cardType" onchange="displayIssueNumber(this.value)" class="selectBox">
	                    <#if debitCard.cardType?exists>
	                      <option>${debitCard.cardType}</option>
	                      <option value="${debitCard.cardType}">---</option>
	                    </#if>
	                    ${screens.render("component://common/widget/CommonScreens.xml#dctypes")}
	                  </select>
	                  <span style="color:#FF0000"> *</span>
    				</td>
  				</tr>
	            <tr>
	                <td align="left" valign="middle"><b>${uiLabelMap.AccountingCardNumber}</b></td>
	                <td>
	                  <input type="text" size="20" maxlength="30" name="cardNumber" value="${debitCard.cardNumber?if_exists}"/>
	                  <span style="color:#FF0000"> *</span>
	                </td>
	            </tr>
	            <tr>
	               <td align="left" valign="middle"><b>${uiLabelMap.AccountingCardSecurityCode}</b></td>
	                <td >
	                  	<input type="text" id='numbers' size="4" maxlength="4" name="cardSecurityCode" value="" /><span style="color:#FF0000"> *</span>
	                	<a href="#" style="color:red;" onmouseover="Tip('&lt;table &gt;&lt;tr valign=center&gt;&lt;td align=center &gt;&lt;img src=/enzetheme/cardsecurity.jpg  /&gt;&lt;td &lt;/tr&gt; &lt;/table&gt;')" onmouseout="UnTip()" rel="lightbox" style="color:red; font-size:11px; padding-left:5px" >${uiLabelMap.CommonWhat} ${uiLabelMap.CommonIs} ${uiLabelMap.CommonThis}
	                	</a>
	                </td>
	            </tr>
  				<tr id="hidethis" style="display:none;">
  					<td align="left" valign="middle"><b>${uiLabelMap.CommonIssue} ${uiLabelMap.CommonNumber}</b></td>
    				<td>
   					  	<input type="text" class="paymentformsize" size="2" maxlength="2" name="issueNumber" value=""/>
						<a href="#" style="color:red;"  onmouseover="Tip('&lt;table bgcolor=white border=0 width=420&gt;&lt;tr valign=center&gt;&lt;td align=center width=180&gt;&lt;img src=/enzetheme/issuenumber.jpg order=0 width=180 height=193 /&gt;&lt;td&gt;/tr&gt;&lt;/table&gt;')" onmouseout="UnTip()" rel="lightbox" style="color:red; font-size:11px; padding-left:5px">${uiLabelMap.CommonWhat} ${uiLabelMap.CommonIs} ${uiLabelMap.CommonThis}</a>
                    </td>  
				</tr>
  				<tr>
    				<td  align="left" valign="middle"><b>${uiLabelMap.AccountingExpirationDate}</b></td>
    				<td>
		                  <#assign expMonth = "">
		                  <#assign expYear = "">
		                  <#if debitCard?exists && debitCard.expireDate?exists>
		                    <#assign expDate = debitCard.expireDate>
		                    <#if (expDate?exists && expDate.indexOf("/") > 0)>
		                      <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
		                      <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
		                    </#if>
		                  </#if>
		                  <select name="expMonth" class="selectBox">
		                    <#if debitCard?has_content && expMonth?has_content>
		                      <#assign dcExprMonth = expMonth>
		                    <#else>
		                      <#assign dcExprMonth = requestParameters.expMonth?if_exists>
		                    </#if>
		                    <#if dcExprMonth?has_content>
		                      <option value="${dcExprMonth?if_exists}">${dcExprMonth?if_exists}</option>
		                    </#if>
		                    ${screens.render("component://common/widget/CommonScreens.xml#dcmonths")}
		                  </select>
		                  <select name="expYear" class="selectBox">
		                    <#if debitCard?has_content && expYear?has_content>
		                      <#assign dcExprYear = expYear>
		                    <#else>
		                      <#assign dcExprYear = requestParameters.expYear?if_exists>
		                    </#if>
		                    <#if dcExprYear?has_content>
		                      <option value="${dcExprYear?if_exists}">${dcExprYear?if_exists}</option>
		                    </#if>
		                    ${screens.render("component://common/widget/CommonScreens.xml#dcyears")}
		                  </select>
		                  <span style="color:#FF0000"> *</span>
    				</td>
  				</tr>
			</#if>
						<#-- End Debit Card Code -->
            <#-- eft fields -->
            <#if paymentMethodTypeId?if_exists =="EFT_ACCOUNT">
              <#if !eftAccount?has_content>
                <#assign eftAccount = requestParameters>
              </#if>
              <tr>
                <td colspan="2"><hr/></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="top"><div class="tableheadtext">${uiLabelMap.AccountingEFTAccountInformation}</div></td>
                <td width="74%">&nbsp;</td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingNameOnAccount}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="30" maxlength="60" name="nameOnAccount" value="${eftAccount.nameOnAccount?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingCompanyNameOnAccount}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="30" maxlength="60" name="companyNameOnAccount" value="${eftAccount.companyNameOnAccount?if_exists}"/>
                </td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingBankName}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="30" maxlength="60" name="bankName" value="${eftAccount.bankName?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingRoutingNumber}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="10" maxlength="30" name="routingNumber" value="${eftAccount.routingNumber?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingAccountType}</div></td>
                <td width="74%">
                  <select name="accountType" class="selectBox">
                    <option>${eftAccount.accountType?if_exists}</option>
                    <option></option>
                    <option>Checking</option>
                    <option>Savings</option>
                  </select>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingAccountNumber}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="20" maxlength="40" name="accountNumber" value="${eftAccount.accountNumber?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.CommonDescription}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="30" maxlength="60" name="description" value="${eftAccount.description?if_exists}"/>
                </td>
              </tr>
            </#if>

            <#-- gift card fields -->
            <#if requestParameters.addGiftCard?default("") == "Y" || paymentMethodTypeId?if_exists == "GIFT_CARD">
              <input type="hidden" name="addGiftCard" value="Y"/>
              <#assign giftCard = giftCard?if_exists>
              <#if paymentMethodTypeId?if_exists != "GIFT_CARD">
                <tr>
                  <td colspan="2"><hr/></td>
                </tr>
              </#if>
              </table>
              
              <div class="outside-screenlet-header">
				    <div class='boxhead'>${uiLabelMap.AccountingGiftCardInformation}</div>
				</div>
               <table width="100%" class="tablealignleft" border="0" cellpadding="1" cellspacing="0">
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingGiftCardNumber}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="20" maxlength="60" name="giftCardNumber" value="${giftCard.cardNumber?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingPINNumber}</div></td>
                <td width="74%">
                  <input type="text" class="inputBox" size="10" maxlength="60" name="giftCardPin" value="${giftCard.pinNumber?if_exists}"/>
                <span style="color:#FF0000"> *</span></td>
              </tr>
              <tr>
                <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.CommonDescription}</div></td>
                <td width="74%" align="left">
                  <input type="text" class="inputBox" size="30" maxlength="60" name="description" value="${giftCard.description?if_exists}"/>
                </td>
              </tr>
              <#if paymentMethodTypeId?if_exists != "GIFT_CARD">
                <tr>
                  <td width="26%" align="right" valign="middle"><div class="tabletext">${uiLabelMap.AccountingAmountToUse}</div></td>
                  <td width="74%">
                    <input type="text" class="inputBox" size="5" maxlength="10" name="giftCardAmount" value="${giftCard.pinNumber?if_exists}"/>
                  <span style="color:#FF0000"> *</span></td>
                </tr>
              </#if>
            </#if>

            <tr>
              <td>&nbsp;</td>
              <td align="left">
                <input type="submit" class="smallsubmit" value="Continue"/>
              </td>
            </tr>
          </table>
        </form>
    </div>
</div>
