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
-->     <link rel="stylesheet" href="/nichesuite/maincss.css" type="text/css"/>


<div id="partyContactInfo" class="screenlet">
	<div class="screenlet-title-bar">
		<div class="h3">
			Contact Information
		</div>
		
	</div>
 	<br class="clear" />
    <div class="screenlet-body">
      <#if contactMeches?has_content>
        <table class="basic-table" cellspacing="0">
          <#list contactMeches as contactMechMap>
            <#assign contactMech = contactMechMap.contactMech>
            <#assign partyContactMech = contactMechMap.partyContactMech>
            <tr><td colspan="4"><hr/></td></tr>
            <tr>
              <td class="label align-top">${contactMechMap.contactMechType.get("description",locale)?if_exists}</td>
              <td>
                <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                  <div>
                    <#if contactMechPurposeType?has_content>
                     <#-- <b>${contactMechPurposeType.get("description",locale)?if_exists}</b>-->
                    <#else>
                      <b>${PartyMechPurposeTypeNotFound?if_exists}: "${partyContactMechPurpose.contactMechPurposeTypeId?if_exists}"</b>
                    </#if>
                    <#if partyContactMechPurpose.thruDate?has_content>
                      (${CommonExpireif_exists?if_exists}: ${partyContactMechPurpose.thruDateif_exists?if_exists})
                    </#if>
                  </div>
                </#list>
                <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
                  <#assign postalAddress = contactMechMap.postalAddress>
                  <#if postalAddress?has_content>
                  <div>
                    <#if postalAddress.toName?has_content><b>${PartyAddrToNameif_exists?if_exists}:</b> ${postalAddress.toName?if_exists}<br /></#if>
                    <#if postalAddress.attnName?has_content><b>${PartyAddrAttnName?if_exists}:</b> ${postalAddress.attnName?if_exists}<br /></#if>
                    ${postalAddress.address1?if_exists?if_exists}<br />
                    <#if postalAddress.address2?has_content>${postalAddress.address2?if_exists}<br /></#if>
                    ${postalAddress.city?if_exists?if_exists},
                    <#if postalAddress.stateProvinceGeoId?has_content>
                      <#assign stateProvince = postalAddress.getRelatedOneCache("StateProvinceGeo")>
                      ${stateProvince.abbreviation?default(stateProvince.geoId)?if_exists}
                    </#if>
                    ${postalAddress.postalCode?if_exists?if_exists}
                    <#if postalAddress.countryGeoId?has_content><br />
                      <#assign country = postalAddress.getRelatedOneCache("CountryGeo")>
                      ${country.geoName?default(country.geoId)?if_exists}
                    </#if>
                  </div>
                  </#if>
                  <#if (postalAddress?has_content && !postalAddress.countryGeoId?has_content) || postalAddress.countryGeoId = "USA">
                    <#assign addr1 = postalAddress.address1?if_exists>
                    <#if addr1?has_content && (addr1.indexOf(" ") > 0)>
                      <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                      <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                      <a target="_blank" href="${CommonLookupWhitepagesAddressLink?if_exists}" class="buttontext">${CommonLookupWhitepages?if_exists}</a>
                    </#if>
                  </#if>
                  <#if postalAddress.geoPointId?has_content>
                    <#if contactMechPurposeType?has_content>
                      <#assign popUptitle = contactMechPurposeType.get("description",locale) + CommonGeoLocation>
                    </#if>
                  </#if>
                <#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
                  <#assign telecomNumber = contactMechMap.telecomNumber>
                  <div>
                    ${telecomNumber.countryCode?if_exists?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")?if_exists}-</#if>${telecomNumber.contactNumber?default("000-0000")?if_exists}
                    <#if partyContactMech.extension?has_content>${PartyContactExt?if_exists}&nbsp;${partyContactMech.extension?if_exists}</#if>
                 <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
                      <a target="_blank" href="${CommonLookupAnywhoLink?if_exists}" class="buttontext">${CommonLookupAnywho?if_exists}</a>
                      <a target="_blank" href="${CommonLookupWhitepagesTelNumberLink?if_exists}" class="buttontext">${CommonLookupWhitepages?if_exists}</a>
                    </#if>
                  </div>
                <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                  <div>
                    ${contactMech.infoString?if_exists?if_exists}
                  </div>
                <#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
                  <div>
                    ${contactMech.infoString?if_exists?if_exists}
                    <#assign openAddress = contactMech.infoString?default("")>
                    <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                  </div>
                <#else>
                  <div>${contactMech.infoString?if_exists?if_exists}</div>
                </#if>
              </td>
              <td valign="top"></td>
             <td>
                <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session) || userLogin.partyId == partyId>
                 <#-- <a href="<@ofbizUrl>editcontactmech?partyId=${partyId?if_exists}&contactMechId=${contactMech.contactMechId?if_exists}</@ofbizUrl>" class="smallSubmit">${CommonUpdate?if_exists}</a>-->
                </#if>
                <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session) || userLogin.partyId == partyId>
                </#if>
              </td>
         
            </tr>
          </#list>
        </table>
      <#else>
        Party No Contact Information
      </#if>
    </div>
  </div>
