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
<#assign defaultPartyId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "ORGANIZATION_PARTY")>
<#if defaultPartyId?has_content>
	<#assign logoDetail = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",defaultPartyId))?if_exists/>
	 	<#if logoDetail?has_content>
	 		<#assign logoImageUrl = logoDetail.logoImageUrl/>
	 			
	 	</#if>
</#if>
		<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
			<tr>
				<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
<#-- Three standard fields cardNumber, pinNumber and amount are available from the activation
     All other fields in this tempalte are designed to work with the values (responses)
     from surveyId 1000 - The gift card purchase survey.
 -->

<#if recipientName?exists>${recipientName},</#if>
<br />

<#-- MyCompany.com (not a variable why?) must be adapted - JLR 1/6/5 -->
${uiLabelMap.EcommerceYouHaveBeenSent} MyCompany.com <#if senderName?exists> ${uiLabelMap.EcommerceGiftCardFrom} ${senderName}</#if>!
<br /><br />
<#if giftMessage?has_content>
  ${uiLabelMap.OrderGiftMessage}
  <br /><br />
  ${giftMessage}
  <br /><br />
</#if>

<pre>
  ${uiLabelMap.EcommerceYourCardNumber} ${cardNumber?if_exists}
  ${uiLabelMap.EcommerceYourPinNumber} ${pinNumber?if_exists}
  ${uiLabelMap.EcommerceGiftAmount} ${amount?if_exists}
</pre>
