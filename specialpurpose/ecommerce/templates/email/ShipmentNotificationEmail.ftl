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
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<#if shipment?has_content>
  <div class="screenlet">
    <div class="screenlet-title-bar">
      <div class="h3">${title?if_exists}<br /><br /></div>
    </div>
    <table border="0" cellpadding="0" cellspacing="0">
      <tbody>
        <tr>
          <td><b>${uiLabelMap.OrderTrackingNumber}</b></td>
        </tr>
        <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
          <tr>
            <td>
              Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
              <#if orderShipmentInfoSummary.carrierPartyId?has_content>(${uiLabelMap.ProductCarrier}: ${orderShipmentInfoSummary.carrierPartyId})</#if>
            </td>
          </tr>
        </#list>
      </tbody>
    </table>
    <br />
    <div class="screenlet-title-bar">
      <div class="h3"><b>${uiLabelMap.EcommerceShipmentItems}</b></div>
    </div>
    <div class="screenlet-body">
      <table width="100%" border="0" cellpadding="0">
        <tr valign="bottom">
          <td width="35%"><span class="tableheadtext"><b>${uiLabelMap.OrderProduct}</b></span></td>
          <td width="10%" align="right"><span class="tableheadtext"><b>${uiLabelMap.OrderQuantity}</b></span></td>
        </tr>
      <tr><td colspan="10"><hr /></td></tr>
      <#list shipmentItems as shipmentItem>
        <#assign productId = shipmentItem.productId>
        <#assign product = shipmentItem.getRelatedOne("Product")>
        <tr>
          <td colspan="1" valign="top"> ${product.brandName?if_exists} <#if product.brandName?has_content> - </#if>${product.internalName?if_exists}</td>
          <td align="right" valign="top"> ${shipmentItem.quantity?if_exists}</td>
        </tr>
      </#list>
      <tr><td colspan="10"><hr /></td></tr>
    </table>
  </div>
</#if>
