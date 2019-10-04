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

<div class="outside-screenlet-header">
    <div class='boxhead2'>${uiLabelMap.AccountingPaymentInformation}</div>
</div>
<div class="screenlet">
    <div class="screenlet-body">
          <#-- initial screen show a list of options -->
          <form method="post" action="<@ofbizUrl>setPaymentInformation</@ofbizUrl>" name="${parameters.formNameValue}">
            <table width="100%" border="0" cellpadding="1" cellspacing="2">
			   <#if productStorePaymentMethodTypeIdMap.GIFT_CARD?exists>
		              <tr>
		                <td width="1%" nowrap="nowrap"><input type="checkbox" name="addGiftCard" value="Y" <#if addGiftCard?exists && addGiftCard == "Y">checked</#if>/></td>
		                <td width="99%" colspan="2" nowrap="nowrap">
		                <div class="tabletext">${uiLabelMap.AccountingCheckGiftCard}</div></td>
		              </tr><tr><td colspan="3"><hr/></td></tr>
		       </#if>
		       
  				<#if productStorePaymentMethodTypeIdMap.PAYPAL_EXPRESS?exists>
    				<tr>
        				<td><input type="radio"  name="paymentMethodTypeId" value="PAYPAL_EXPRESS" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "EXT_PAYPAL">checked</#if>/></td>
        				<td>
        					<div class="tabletext">${uiLabelMap.AccountingPayWithPayPal}</div>
        				</td>
		                <td>	
        					<div><img src="/ecomclone/paypal_logo.gif" alt="Paypal" title="PAYPAL"/></div>			
        				</td>
  					</tr><tr><td colspan="3"><hr/></td></tr>
  				</#if>
  				<#if productStorePaymentMethodTypeIdMap.CREDIT_CARD?exists>
  					<tr >
		                <td width="1%"><input type="radio"  name="paymentMethodTypeId" value="CREDIT_CARD" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "CREDIT_CARD">checked</#if>/></td>
		                <td width="35%">
		                	<div class="tabletext">${uiLabelMap.AccountingVisaMastercardAmexDiscover}</div>
		                </td>
		                <td>
		                	 <img src="/ecomclone/images/visa.gif" alt="Visa/ Maestro" title="visa/maestro"/>			
		                </td>
		            </tr><tr><td colspan="3"><hr/></td></tr>
		        </#if>
 				<#if productStorePaymentMethodTypeIdMap.DEBIT_CARD?exists>
  					<tr>
		                <td><input type="radio" name="paymentMethodTypeId" value="DEBIT_CARD" <#if paymentMethodTypeId?exists && paymentMethodTypeId == "DEBIT_CARD">checked</#if>/></td>
		                <td>
		                	<div class="tabletext">${uiLabelMap.AccountingDebitCardInfo}</div>
		                </td>
		                <td>
		                	<img src="/ecomclone/images/debitcard.gif" alt="debitcard" title="DEBIT CARD"/>				
		                </td>
  					</tr><tr><td colspan="3"><hr/></td></tr>
  				</#if>
	            <tr>
	                <td align="right" valign="bottom" colspan="3" height="30">
	                  <a href="javascript:document.${parameters.formNameValue}.submit();" class="buttontext" >
	                  	${uiLabelMap.CommonContinue}
	                  </a>
	                </td>
	           </tr>
		</table>
          </form>
    </div>
</div>

<#--<div class="outside-screenlet-header"><div class="boxhead">${uiLabelMap.ProductPromoCodes}</div></div>
<div class="screenlet">  	
	<div class="screenlet-body">
		<form method="post" action="<@ofbizUrl>addpromocode<#if requestAttributes._CURRENT_VIEW_?has_content>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>" name="addpromocodeform">
				<input type="text" class="input_text" size="15" name="productPromoCodeId" value=""/>
				<a href="javascript:document.addpromocodeform.submit();" class="buttontext" >
				${uiLabelMap.OrderAddCode}</a>
				<#assign productPromoCodeIds = (shoppingCart.getProductPromoCodesEntered())?if_exists>
				<#if productPromoCodeIds?has_content>
					${uiLabelMap.ProductPromoCodesEntered}
                    <#list productPromoCodeIds as productPromoCodeId>
                        ${productPromoCodeId}
                    </#list>
				</#if>
 		</form>
	</div>
</div>-->
