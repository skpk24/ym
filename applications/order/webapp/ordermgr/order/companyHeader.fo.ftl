
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
<fo:table>
<fo:table-column column-width="7.4in" />

<fo:table-body>
<fo:table-row>
<fo:table-cell>
<fo:block text-align="center" font-weight="bold" background-color="#92d050" padding="4px 0 4px 0" border="1px solid #000000">
	Invoice
   <#-- <#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" height="40px" content-height="scale-to-fit"/></#if>-->
</fo:block>
<#--
<fo:block font-size="8pt">
    <fo:block>${companyName}</fo:block>
    <#if postalAddress?exists>
        <#if postalAddress?has_content>
            <fo:block>${postalAddress.address1?if_exists}</fo:block>
            <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
            <fo:block>${postalAddress.city?if_exists}, ${stateProvinceAbbr?if_exists} ${postalAddress.postalCode?if_exists}, ${countryName?if_exists}</fo:block>
        </#if>
    <#else>
        <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
        <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
    </#if>

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
        </#if>
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
-->
</fo:table-cell>
</fo:table-row>
<fo:table-row>

<fo:table-cell 	padding-before="2px">
<#--

		<fo:block font-size="8px" border-spacing="3pt" text-align="center">Customer Care</fo:block>  
     	<fo:block font-size="8px" border-spacing="3pt" text-align="left">--------------------------------------------------------------------------------------</fo:block>        
         
    

    <#if sendingPartyTaxId?exists || phone?exists || email?exists || website?exists || eftAccount?exists>
    
      
        
        <fo:block font-size="8px" border-spacing="3pt" text-align="left"><fo:inline text-align="left"> <#if phone?exists>${uiLabelMap.CommonTelephoneAbbr} :<#if phone.countryCode?exists>${phone.countryCode}-</#if><#if phone.areaCode?exists>${phone.areaCode}-</#if>${phone.contactNumber?if_exists} </#if> </fo:inline>    <#if email?exists>   <fo:inline text-align="right" padding-left="3px">         ${uiLabelMap.CommonEmail} : ${email.infoString?if_exists}</fo:inline> </#if></fo:block>
       
        
    
    </#if>
-->
<fo:block text-align="right">
    <#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" height="60px"  content-height="scale-to-fit"/></#if>
</fo:block>
</fo:table-cell>
</fo:table-row>
</fo:table-body>
</fo:table>



</#escape>
