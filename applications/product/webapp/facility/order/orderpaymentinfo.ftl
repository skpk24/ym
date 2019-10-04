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

<div class="screenlet">
  <div class="screenlet-title-bar">
      <ul><li class="h3">&nbsp;${uiLabelMap.AccountingPaymentInformation}</li></ul>
      <br class="clear"/>
  </div>
  <div class="screenlet-body">
     <table class="basic-table" cellspacing='1' width="100%">
     <#-- order payment status -->
     <tr>
       <td align="left" valign="top" width="15%" class="orderlabeltext">${uiLabelMap.OrderStatusHistory}</td>
       <td colspan="2">
         <#assign orderPaymentStatuses = orderReadHelper.getOrderPaymentStatuses()>
         <#if orderPaymentStatuses?has_content>
           <#list orderPaymentStatuses as orderPaymentStatus>
             <#assign statusItem = orderPaymentStatus.getRelatedOne("StatusItem")?if_exists>
             <#if statusItem?has_content>
                <div class="ordersubtext" text-align="left">
                  ${statusItem.get("description",locale)} - ${orderPaymentStatus.statusDatetime?default("0000-00-00 00:00:00")?string}
                  &nbsp;
                  ${uiLabelMap.CommonBy} - [${orderPaymentStatus.statusUserLogin?if_exists}]
                </div>
             </#if>
           </#list>
         </#if>
       </td>
     </tr>
     <#if orderPaymentPreferences?has_content || billingAccount?has_content || invoices?has_content>
        <#list orderPaymentPreferences as orderPaymentPreference>
          <#assign pmBillingAddress = {}>
          <#assign oppStatusItem = orderPaymentPreference.getRelatedOne("StatusItem")>
          <#if outputted?default("false") == "true">
          </#if>
          <#assign outputted = "true">
          <#-- try the paymentMethod first; if paymentMethodId is specified it overrides paymentMethodTypeId -->
          <#assign paymentMethod = orderPaymentPreference.getRelatedOne("PaymentMethod")?if_exists>
          <#if !paymentMethod?has_content>
            <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")>
            <#if paymentMethodType.paymentMethodTypeId == "EXT_BILLACT">
              <#assign outputted = "false">
            <#elseif paymentMethodType.paymentMethodTypeId == "FIN_ACCOUNT">
              <#assign finAccount = orderPaymentPreference.getRelatedOne("FinAccount")?if_exists/>
              <#if (finAccount?has_content)>
                <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                <#assign finAccountType = finAccount.getRelatedOne("FinAccountType")?if_exists/>
                <tr>
                  <td align="right" valign="top" width="29%" class="ordersubtext">
                    <div>
                    <span class="orderlabeltext">&nbsp;${uiLabelMap.AccountingFinAccount}</span>
                    <#if orderPaymentPreference.maxAmount?has_content>
                       <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                    </#if>
                    </div>
                  </td>
                  <td valign="top" width="60%" class="ordersubtext">
                    <div>
                      <#if (finAccountType?has_content)>
                        ${finAccountType.description?default(finAccountType.finAccountTypeId)}&nbsp;
                      </#if>
                      #${finAccount.finAccountCode?default(finAccount.finAccountId)} (<a href="/accounting/control/EditFinAccount?finAccountId=${finAccount.finAccountId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext1">${finAccount.finAccountId}</a>)
                      <br/>
                      ${finAccount.finAccountName?if_exists}
                     <br/>

                      <#-- Authorize and Capture transactions -->
                      <div>
                        <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                          <a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingAuthorize}</a>
                        </#if>
                        <#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
                          <a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingCapture}</a>
                        </#if>
                         <br/><br/>
                      </div>
                    </div>
                    <#if gatewayResponses?has_content>
                      <div>
                        <hr/>
                        <#list gatewayResponses as gatewayResponse>
                          <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                          ${(transactionCode.get("description",locale))?default("Unknown")}:
                          ${gatewayResponse.transactionDate.toString()}
                          <@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br/></br/>
                          (<span class"orderlabeltext">${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
                          <span class"orderlabeltext">${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
                          <span class"orderlabeltext">${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
                          <a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext1">${uiLabelMap.CommonDetails}</a>
                          <#if gatewayResponse_has_next><hr/></#if>
                        </#list>
                      </div>
                    </#if>
                  </td>
                  <td width="10%">
                    <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                     <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                        <div>
                          <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext1">${uiLabelMap.CommonCancel}</a>
                          <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                            <input type="hidden" name="orderId" value="${orderId}">
                            <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}">
                            <input type="hidden" name="statusId" value="PAYMENT_CANCELLED">
                            <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}">
                          </form>
                        </div>
                     </#if>
                    </#if>
                  </td>
                </tr>
              </#if>
            <#else>
              <tr>
                <td align="right" valign="top" width="29%">
                  <div>&nbsp;<span class"orderlabeltext">${paymentMethodType.get("description",locale)?if_exists}</span>&nbsp;
                  <#if orderPaymentPreference.maxAmount?has_content>
                             ${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                  </#if>
                  </div>
                </td>
                <#if paymentMethodType.paymentMethodTypeId != "EXT_OFFLINE" && paymentMethodType.paymentMethodTypeId != "EXT_PAYPAL" && paymentMethodType.paymentMethodTypeId != "EXT_COD">
                  <td width="60%">
                    <div>
                      <#if orderPaymentPreference.maxAmount?has_content>
                         <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                      </#if>
                      <br/>&nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                    </div>
                    <#--
                    <div><@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>&nbsp;-&nbsp;${(orderPaymentPreference.authDate.toString())?if_exists}</div>
                    <div>&nbsp;<#if orderPaymentPreference.authRefNum?exists>(${uiLabelMap.OrderReference}: ${orderPaymentPreference.authRefNum})</#if></div>
                    -->
                  </td>
                <#else>
                  <td align="right" width="60%">
                    <a href="<@ofbizUrl>receivepayment?${paramString}</@ofbizUrl>" class="buttontext1">${uiLabelMap.AccountingReceivePayment}</a>
                  </td>
                </#if>
                  <td width="10%">
                   <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                    <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                      <div>
                        <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext1">${uiLabelMap.CommonCancel}</a>
                        <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                          <input type="hidden" name="orderId" value="${orderId}">
                          <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}">
                          <input type="hidden" name="statusId" value="PAYMENT_CANCELLED">
                          <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}">
                        </form>
                      </div>
                    </#if>
                   </#if>
                  </td>
                </tr>
            </#if>
          <#else>
            <#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
              <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
              <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")?if_exists>
              <#if creditCard?has_content>
                <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")?if_exists>
              </#if>
              <tr>
                <td align="left" valign="top" width="29%">
                  <div><span class="orderlabeltext">${uiLabelMap.AccountingCreditCard}</span>
                  <#if orderPaymentPreference.maxAmount?has_content>
                    <br/><span class="orderlabeltext">${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                      </span>
                  </#if>
                  </div>
                </td>
                <td valign="top" width="60%">
                  <div>
                    <#if creditCard?has_content>
                      <#if creditCard.companyNameOnCard?exists> <span class="ordersubtext">${creditCard.companyNameOnCard}</span><br/></br/></#if>
                      <#if creditCard.titleOnCard?has_content> <span class="ordersubtext">${creditCard.titleOnCard}</span>&nbsp</#if>
                     <span class="ordersubtext"> ${creditCard.firstNameOnCard}</span>&nbsp;
                      <#if creditCard.middleNameOnCard?has_content> <span class="ordersubtext">${creditCard.middleNameOnCard}</span>&nbsp</#if>
                      <span class="ordersubtext">${creditCard.lastNameOnCard?default("N/A")}</span>
                      <#if creditCard.suffixOnCard?has_content>&nbsp; <span class="ordersubtext">${creditCard.suffixOnCard}</span></#if>
                      <br/>

                      <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                       <span class="ordersubtext"> ${creditCard.cardType} </span>
                       <span class="ordersubtext"> ${creditCard.cardNumber}</span><br/></br/>
                      <span class="ordersubtext">  ${creditCard.expireDate}</span><br/></br/>
                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                      <#else>
                        ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                      </#if>
                      
						<br/>
                      <#-- Authorize and Capture transactions -->
                      <div>
                        <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                          <a href="/accounting/control/AuthorizeTransaction?orderId=${orderId?if_exists}&orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingAuthorize}</a>
                        </#if>
                        <#if orderPaymentPreference.statusId == "PAYMENT_AUTHORIZED">
                          <a href="/accounting/control/CaptureTransaction?orderId=${orderId?if_exists}&orderPaymentPreferenceId=${orderPaymentPreference.orderPaymentPreferenceId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${uiLabelMap.AccountingCapture}</a>
                        </#if>
                        <br/><br/>
                      </div>
                    <#-- Debit Card Code -->
                      <#elseif paymentMethod.paymentMethodTypeId?if_exists == "DEBIT_CARD">
                             <#assign gatewayResponses = orderPaymentPreference.getRelated("PaymentGatewayResponse")>
                             <#assign debitCard = paymentMethod.getRelatedOne("DebitCard")?if_exists>
					              <#if debitCard?has_content>
					                <#assign pmBillingAddress = debitCard.getRelatedOne("PostalAddress")?if_exists>
					              </#if>
					           <tr>
					               <td align="left" valign="top" width="29%">
					                  <div>&nbsp;<span class="orderlabeltext">${uiLabelMap.AccountingDebitCard}</span>
					                  <#if orderPaymentPreference.maxAmount?has_content>
					                     <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
					                  </#if>
					                  </div>
					                </td>  
					                
					                 <td align="left" valign="top">
                           <div class="tabletexta">
			                    <#if debitCard?has_content>
			                          <#if debitCard.companyNameOnCard?exists>
			                             <span class="ordersubtext">${debitCard.companyNameOnCard} </span><br/></#if>
			                           <#if debitCard.titleOnCard?has_content><span class="ordersubtext">
			                           ${debitCard.titleOnCard}</span>&nbsp</#if>
			                                     <span class="ordersubtext"> ${debitCard.firstNameOnCard} </span>&nbsp;
			                           <#if debitCard.middleNameOnCard?has_content>${debitCard.middleNameOnCard}&nbsp</#if>
			                                  <span class="ordersubtext">   ${debitCard.lastNameOnCard?default("N/A")}
			                             <#if debitCard.suffixOnCard?has_content>&nbsp;${debitCard.suffixOnCard}</#if>
			                            <span class="ordersubtext"> ${cardNumberDisplay?if_exists} </span>
			                         <span class="ordersubtext">${debitCard.expireDate}</span>
			                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
			                      <#else>
			                        ${Static["org.ofbiz.party.contact.ContactHelper"].formatDebitCard(debitCard)}
			                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
			                  
			                      <br/>
			                     </#if>   
			                    <#else>
			                      ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
			                    </#if>
                  </div>
                  <#if gatewayResponses?has_content>
                    <div>
                      <#list gatewayResponses as gatewayResponse>
                        <#assign transactionCode = gatewayResponse.getRelatedOne("TranCodeEnumeration")>
                        ${(transactionCode.get("description",locale))?default("Unknown")}:
                        ${gatewayResponse.transactionDate.toString()}
                        <@ofbizCurrency amount=gatewayResponse.amount isoCode=currencyUomId/><br/>
                        (<span class="orderlabeltext">${uiLabelMap.OrderReference}</span>&nbsp;${gatewayResponse.referenceNum?if_exists}
                        <span class="orderlabeltext">${uiLabelMap.OrderAvs}</span>&nbsp;${gatewayResponse.gatewayAvsResult?default("N/A")}
                        <span class="orderlabeltext">${uiLabelMap.OrderScore}</span>&nbsp;${gatewayResponse.gatewayScoreResult?default("N/A")})
                        <a href="/accounting/control/ViewGatewayResponse?paymentGatewayResponseId=${gatewayResponse.paymentGatewayResponseId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext1">${uiLabelMap.CommonDetails}</a>
                        <#if gatewayResponse_has_next><hr/></#if>
                      </#list>
                    </div>
                  </#if>
                </td>
                <td>
                  <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                   <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                      <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                      <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}">
                        <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}">
                        <input type="hidden" name="statusId" value="PAYMENT_CANCELLED">
                        <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}">
                      </form>
                   </#if>
                  </#if>
                </td>
              </tr>
              
            
              
            <#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
              <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
              <#if eftAccount?has_content>
                <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")?if_exists>
              </#if>
              <tr>
                <td align="right" valign="top" width="29%">
                  <div>&nbsp;<span class="orderlabeltext">${uiLabelMap.AccountingEFTAccount}</span>
                  <#if orderPaymentPreference.maxAmount?has_content>
                  <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                  </#if>
                  </div>
                </td>
                <td valign="top" width="60%">
                  <div>
                    <#if eftAccount?has_content>
                      ${eftAccount.nameOnAccount?if_exists}<br/>
                      <#if eftAccount.companyNameOnAccount?exists>${eftAccount.companyNameOnAccount}<br/></#if>
                      ${uiLabelMap.AccountingBankName}: ${eftAccount.bankName}, ${eftAccount.routingNumber}<br/>
                      ${uiLabelMap.AccountingAccount}#: ${eftAccount.accountNumber}
                    <#else>
                      ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
                    </#if>
                  </div>
                </td>
                <td width="10%">
                  <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                   <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                      <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
                      <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}">
                        <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}">
                        <input type="hidden" name="statusId" value="PAYMENT_CANCELLED">
                        <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}">
                      </form>
                   </#if>
                  </#if>
                </td>
              </tr>
            <#elseif paymentMethod.paymentMethodTypeId?if_exists == "GIFT_CARD">
              <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>
              <#if giftCard?exists>
                <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress")?if_exists>
              </#if>
              <tr>
                <td align="right" valign="top" width="29%">
                  <div>&nbsp;<span class="orderlabeltext">${uiLabelMap.OrderGiftCard}</span>
                  <#if orderPaymentPreference.maxAmount?has_content>
                  <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=orderPaymentPreference.maxAmount?default(0.00) isoCode=currencyUomId/>
                  </#if>
                  </div>
                </td>
                <td valign="top" width="60%">
                  <div>
                    <#if giftCard?has_content>
                      <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                        ${giftCard.cardNumber?default("N/A")} [${giftCard.pinNumber?default("N/A")}]
                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                      <#else>
                        <#if giftCard?has_content && giftCard.cardNumber?has_content>
                          <#assign giftCardNumber = "">
                          <#assign pcardNumber = giftCard.cardNumber>
                          <#if pcardNumber?has_content>
                            <#assign psize = pcardNumber?length - 4>
                            <#if (psize > 0)>
                              <#list 0 .. psize-1 as foo>
                                <#assign giftCardNumber = giftCardNumber + "*">
                              </#list>
                              <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
                            <#else>
                              <#assign giftCardNumber = pcardNumber>
                            </#if>
                          </#if>
                        </#if>
                        ${giftCardNumber?default("N/A")}
                        &nbsp;[<#if oppStatusItem?exists>${oppStatusItem.get("description",locale)}<#else>${orderPaymentPreference.statusId}</#if>]
                      </#if>
                    <#else>
                      ${uiLabelMap.CommonInformation} ${uiLabelMap.CommonNot} ${uiLabelMap.CommonAvailable}
                    </#if>
                  </div>
                </td>
                <td width="10%">
                  <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED"))>
                   <#if orderPaymentPreference.statusId != "PAYMENT_SETTLED">
                      <a href="javascript:document.CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}.submit()" class="smallSubmit">${uiLabelMap.CommonCancel}</a>
                      <form name="CancelOrderPaymentPreference_${orderPaymentPreference.orderPaymentPreferenceId}" method="post" action="<@ofbizUrl>updateOrderPaymentPreference</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderId}">
                        <input type="hidden" name="orderPaymentPreferenceId" value="${orderPaymentPreference.orderPaymentPreferenceId}">
                        <input type="hidden" name="statusId" value="PAYMENT_CANCELLED">
                        <input type="hidden" name="checkOutPaymentId" value="${paymentMethod.paymentMethodTypeId?if_exists}">
                      </form>
                   </#if>
                  </#if>
                </td>
              </tr>
            </#if>
          </#if>
          <#if pmBillingAddress?has_content>
            <tr>
              <td align="right" valign="top" width="29%">&nbsp;</td>
              <td valign="top" width="60%" colspan="2">
                <div>
                  <#if pmBillingAddress.toName?has_content><span class="ordersubtext">${uiLabelMap.CommonTo}</span>&nbsp;${pmBillingAddress.toName}<br/></#if>
                  <#if pmBillingAddress.attnName?has_content><span class="ordersubtext">${uiLabelMap.CommonAttn}</span>&nbsp;${pmBillingAddress.attnName}<br/></#if>
                  <span class="ordersubtext">${pmBillingAddress.address1}</span><br/>
                  <#if pmBillingAddress.address2?has_content><span class="ordersubtext">${pmBillingAddress.address2}</span><br/></#if>
                  
                  <span class="ordersubtext">${pmBillingAddress.city}</span>
                 <span> <#if pmBillingAddress.stateProvinceGeoId?has_content>,${pmBillingAddress.stateProvinceGeoId} </#if></span>
                  <span class="ordersubtext">${pmBillingAddress.postalCode?if_exists}</span><br/>
                  <span class="ordersubtext">${pmBillingAddress.countryGeoId?if_exists}</span>
                </div>
              </td>
            </tr>
          </#if>
        </#list>

        <#-- billing account -->
        <#if billingAccount?exists>
          <#if outputted?default("false") == "true">
          </#if>
          <tr>
            <td align="right" valign="top" width="29%">
              <#-- billing accounts require a special OrderPaymentPreference because it is skipped from above section of OPPs -->
              <div>&nbsp;<span class="orderlabeltext">${uiLabelMap.AccountingBillingAccount}</span>&nbsp;
                  <#if billingAccountMaxAmount?has_content>
                  <br/>${uiLabelMap.OrderPaymentMaximumAmount}: <@ofbizCurrency amount=billingAccountMaxAmount?default(0.00) isoCode=currencyUomId/>
                  </#if>
                  </div>
            </td>
            <td valign="top" colspan="2" width="60%">
                #<a href="/accounting/control/EditBillingAccount?billingAccountId=${billingAccount.billingAccountId}&amp;externalLoginKey=${externalLoginKey}" class="buttontext1">${billingAccount.billingAccountId}</a>  - ${billingAccount.description?if_exists}
            </td>
          </tr>
        </#if>
        <#if customerPoNumber?has_content>
          <tr>
            <td align="right" valign="top" width="29%"><span class="orderlabeltext">${uiLabelMap.OrderPONumber}</span></td>
            <td valign="top" colspan="2" width="60%">${customerPoNumber?if_exists}</td>
          </tr>
        </#if>

        <#-- invoices -->
        <#if invoices?has_content>
          <tr>
            <td align="right" valign="top" width="29%">&nbsp;<span class="orderlabeltext">${uiLabelMap.OrderInvoices}</span></td>
            <td valign="top" colspan="2" width="60%">
              <#list invoices as invoice>
                <div>${uiLabelMap.CommonNbr}<a href="/accounting/control/invoiceOverview?invoiceId=${invoice}&amp;externalLoginKey=${externalLoginKey}" class="buttontext1">${invoice}</a>
                (<a href="/accounting/control/invoice.pdf?invoiceId=${invoice}" class="buttontext1">PDF</a>)</div>
              </#list>
            </td>
          </tr>
        </#if>
   <#else>
    <tr>
     <td colspan="3" align="center">${uiLabelMap.OrderNoOrderPaymentPreferences}</td>
    </tr>
   </#if>
   <#if (!orderHeader.statusId.equals("ORDER_COMPLETED")) && !(orderHeader.statusId.equals("ORDER_REJECTED")) && !(orderHeader.statusId.equals("ORDER_CANCELLED")) && (paymentMethodValueMaps?has_content)>
   <tr><td colspan="3">
   <form name="addPaymentMethodToOrder" method="post" action="<@ofbizUrl>addPaymentMethodToOrder</@ofbizUrl>">
   <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
   <table class="" cellspacing='0' width="100%">
   <tr>
      <td width="10%" align="left"><span class="orderlabeltext">${uiLabelMap.AccountingPaymentMethod}</span></td>
      <td width="90%" style="padding-left: 60px;">
         <select name="paymentMethodId" class="selectBox">
           <#list paymentMethodValueMaps as paymentMethodValueMap>
             <#assign paymentMethod = paymentMethodValueMap.paymentMethod/>
             <option value="${paymentMethod.get("paymentMethodId")?if_exists}">
               <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
                 <#assign creditCard = paymentMethodValueMap.creditCard?if_exists/>
                 <#if (creditCard?has_content)>
                   <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                     ${creditCard.cardType?if_exists} ${creditCard.cardNumber?if_exists} ${creditCard.expireDate?if_exists}
                   <#else>
                     ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                   </#if>
                 </#if>
               <#else>
                 ${paymentMethod.paymentMethodTypeId?if_exists}
                 <#if paymentMethod.description?exists>${paymentMethod.description}</#if>
                   (${paymentMethod.paymentMethodId})
                 </#if>
               </option>
           </#list>
         </select>
      </td>
   </tr>
   <#assign openAmount = orderReadHelper.getOrderOpenAmount()>
   <tr>
      <td align="left"><span class="orderlabeltext">${uiLabelMap.AccountingAmount}</span></td>
      <td width="60%"  nowrap="nowrap" style="padding-left: 60px;">
         <input type="text" name="maxAmount" value="${openAmount}"/>
      </td>
   </tr>
   </#if>
   <#if reference?has_content>
   <tr>
      <td align="left"><span>${uiLabelMap.OrderReference}</span></td>
      <td width="60%"  nowrap="nowrap" style="padding-left: 60px;">
         ${reference}
      </td>
   </tr>
   </#if>
   <!--tr>
      <td align="right" valign="top" >&nbsp;</td>
      <td valign="top" width="60%"  style="padding-left: 60px;">
        <input type="submit" value="${uiLabelMap.CommonAdd}" class="smallSubmit"/>
      </td>
   </tr-->
   </table>
   </form>
   </td></tr>
</table>
</div>
</div>