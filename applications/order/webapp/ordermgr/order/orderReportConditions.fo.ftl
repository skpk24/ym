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

<#escape x as x?xml>

<#if shipGroups?exists && shipGroups.size() gt 1>
    <fo:table border-spacing="3pt" space-before="0.3in" font-size="10px">
        <fo:table-column column-width="1in"/>
        <fo:table-column column-width="1in"/>
        <fo:table-column column-width="0.5in"/>
        <fo:table-header>
            <fo:table-row font-weight="bold">
                <fo:table-cell><fo:block>${uiLabelMap.OrderShipGroup}</fo:block></fo:table-cell>
                <fo:table-cell><fo:block>${uiLabelMap.OrderProduct}</fo:block></fo:table-cell>
                <fo:table-cell text-align="right"><fo:block>${uiLabelMap.OrderQuantity}</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#list shipGroups as shipGroup>
                <#assign orderItemShipGroupAssocs = shipGroup.getRelated("OrderItemShipGroupAssoc")?if_exists>
                <#if orderItemShipGroupAssocs?has_content>
                    <#list orderItemShipGroupAssocs as shipGroupAssoc>
                        <#assign orderItem = shipGroupAssoc.getRelatedOne("OrderItem")?if_exists>
                        <fo:table-row>
                            <fo:table-cell><fo:block>${shipGroup.shipGroupSeqId}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block>${orderItem.productId?if_exists}</fo:block></fo:table-cell>
                            <fo:table-cell text-align="right"><fo:block>${shipGroupAssoc.quantity?string.number}</fo:block></fo:table-cell>
                        </fo:table-row>
                    </#list>
                </#if>
            </#list>
        </fo:table-body>
    </fo:table>
</#if>

<fo:table border-spacing="3pt" space-before="0.3in" font-size="10px">
    <fo:table-column column-width="1in"/>
    <fo:table-column column-width="1in"/>
    <fo:table-column column-width="0.5in"/>
    <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell width="500px"><fo:block font-size="9px">You have <#if totalloyaltyPoints?exists>${totalloyaltyPoints?if_exists}<#elseif leftloyaltyPoints?exists>${leftloyaltyPoints?if_exists}<#else> 0 </#if> Savings Points.</fo:block></fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    	<fo:table-cell><fo:block font-weight="bold" font-size="9px">You have <fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${leftBalance?if_exists}/- in your Savings Account. Kindly redeem the amount in your future purchases.</fo:block></fo:table-cell>
                    </fo:table-row>
    </fo:table-body>
</fo:table>


<fo:block space-after="40px"/>
<#if orderHeader.getString("orderTypeId") == "SALES_ORDER">
  <#--<fo:block font-size="14pt" font-weight="bold" text-align="center">THANK YOU FOR YOUR PATRONAGE!</fo:block>-->
  <fo:block font-size="10px">
    <#--    Here is a good place to put policies and return information. -->
  </fo:block>
  <fo:block font-size="10px">
  <fo:block text-align="center" font-size="10px" color="#7d7a7a">Thank you for shopping with us</fo:block>
    <fo:block text-align="center" font-size="10px" color="#7d7a7a"> ${companyName?if_exists},
    <#if postalAddress?exists>
        <#if postalAddress?has_content>
         ${postalAddress.address1?if_exists}
            <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}</#if>
            ${postalAddress.city?if_exists}, <#--${stateProvinceAbbr?if_exists}-->
            <#if postalAddress.stateProvinceGeoId?exists>
            <#assign stateName = delegator.findByPrimaryKey("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",postalAddress.stateProvinceGeoId?if_exists))?if_exists/>
            	${stateName.geoName?capitalize?if_exists} 
            	${postalAddress.postalCode?if_exists}, ${countryName?if_exists} , 
            	                                                                                                                                                                      
            		www.youmart.in,                     
            	Customer care : 080 4545 8888
            	
            </#if>
        </#if>
     <#else>
       ${uiLabelMap.CommonNoPostalAddress}
        ${uiLabelMap.CommonFor}: ${companyName?if_exists}
    </#if>
	</fo:block>
    <#if website?exists || eftAccount?exists>
    <fo:list-block provisional-distance-between-starts=".5in">
        <#if sendingPartyTaxId?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.PartyTaxId}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${sendingPartyTaxId}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
        <#--<#if phone?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonTelephoneAbbr}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block><#if phone.countryCode?exists>${phone.countryCode}-</#if><#if phone.areaCode?exists>${phone.areaCode}-</#if>${phone.contactNumber?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
        <#if email?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonEmail}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${email.infoString?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>-->
        <#if website?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonWebsite}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${website.infoString?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
        <#if eftAccount?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonFinBankName}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${eftAccount.bankName?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonRouting}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${eftAccount.routingNumber?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonBankAccntNrAbbr}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${eftAccount.accountNumber?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
    </fo:list-block>
    </#if>
</fo:block>
<#elseif orderHeader.getString("orderTypeId") == "PURCHASE_ORDER">
  <fo:block font-size="10pt">
    <#-- Here is a good place to put boilerplate terms and conditions for a purchase order. -->
  </fo:block>
</#if>
</#escape>
