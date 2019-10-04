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
<fo:table border-spacing="3pt" width="7.3in"  border="0.5px solid #000000"  font-size="10px">
	           <fo:table-column column-width="7.00in"/>
	
	
	          <fo:table-body >
	          <fo:table-row border-top="0.5px solid #000000" border-bottom="0.5px solid #000000" background-color="#FFFFFF"> 
	                      <fo:table-cell >
	                          <fo:block  font-weight="bold"  text-align="center" font-size="16px" padding="5px 0 5px 0">10%* DISCOUNT</fo:block>
	                         <fo:block  font-weight="bold"  text-align="center" font-size="12px" padding="5px 0 5px 0">Shop Regularly. Get 10%* discount on your next purchase!!</fo:block>
	                        <fo:block  font-weight="bold"  text-align="right" font-size="8px" padding="5px 0 5px 0">*Minimum purchase of 1000/-</fo:block>
	                        
	                        </fo:table-cell>
	                    </fo:table-row>
	          
			  
	          </fo:table-body>
       </fo:table>
       
       <fo:table border-spacing="3pt" width="7.3in"  border="0.0px solid #000000"  font-size="10px">
	           <fo:table-column column-width="7.00in"/>
	
	
	          <fo:table-body >
	          <fo:table-row border-top="0.5px solid #000000" border-bottom="0.0px solid #000000" background-color="#FFFFFF"> 
	                      <fo:table-cell >
	                         <fo:block  font-weight="bold"  text-align="center" font-size="12px" padding="5px 0 5px 0"></fo:block>
	                      </fo:table-cell>
	                    </fo:table-row>
	          
			  
	          </fo:table-body>
       </fo:table>
      
     
        

   <fo:table border-spacing="0pt" width="2.3in"  border="0.0px solid #000000"  font-size="10px">
          <fo:table-column column-width="3.50in"/>
          <fo:table-column column-width="3.50in"/>
          
          <fo:table-body>
                    <#--<fo:table-row border-top="0.5px solid #000000" border-bottom="0.5px solid #000000" background-color="#FFFFFF">
                      <fo:table-cell number-columns-spanned="2">
                         <fo:block  font-weight="bold" font-size="10px" padding="5px 0 5px 0">Customer Detail</fo:block>
                      </fo:table-cell>
                    </fo:table-row>-->
            <fo:table-row>    <#-- this part could use some improvement -->

             <#assign createEmptyCell = true>
             <#-- a special purchased from address for Purchase Orders -->
             <#if orderHeader.getString("orderTypeId") == "PURCHASE_ORDER">
             <#if supplierGeneralContactMechValueMap?exists>
               <#assign contactMech = supplierGeneralContactMechValueMap.contactMech>
               <#assign createEmptyCell = false>
               <fo:table-cell>
                 <fo:block>
                     ${uiLabelMap.OrderPurchasedFrom}:
                 </fo:block>
                 
                 <#assign postalAddress = supplierGeneralContactMechValueMap.postalAddress>
                 <#if postalAddress?has_content>
                   <#if postalAddress.toName?has_content><fo:block>${postalAddress.toName}</fo:block></#if>
                   <#if postalAddress.attnName?has_content><fo:block>${postalAddress.attnName?if_exists}</fo:block></#if>
                   <fo:block>${postalAddress.address1?if_exists}</fo:block>
                   <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                   <fo:block>${postalAddress.city?if_exists}<#if postalAddress.stateProvinceGeoId?has_content>, ${postalAddress.stateProvinceGeoId} </#if><#if postalAddress.postalCode?has_content>${postalAddress.postalCode}</#if></fo:block>
                   <fo:block>${postalAddress.countryGeoId?if_exists}</fo:block>
                 </#if>
               </fo:table-cell>
             <#else>
               <#-- here we just display the name of the vendor, since there is no address -->
               <#assign createEmptyCell = false>
               <fo:table-cell>
                 <#assign vendorParty = orderReadHelper.getBillFromParty()>
                 <fo:block>
                   <fo:inline font-weight="bold">${uiLabelMap.OrderPurchasedFrom}:</fo:inline> ${Static['org.ofbiz.party.party.PartyHelper'].getPartyName(vendorParty)}
                 </fo:block>
               </fo:table-cell>
             </#if>
             </#if>

             <#-- list all postal addresses of the order.  there should be just a billing and a shipping here. -->
             <#list orderContactMechValueMaps as orderContactMechValueMap>
             
               <#assign contactMech = orderContactMechValueMap.contactMech>
               <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
               
               <#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
               <#assign postalAddress = orderContactMechValueMap.postalAddress>
               <#assign createEmptyCell = false>
               <fo:table-cell margin-left="25px">
                 <#--<fo:block font-weight="bold" padding="5px 0 0 0">${contactMechPurpose.get("description",locale)}</fo:block>
                 <fo:block>-------------------------------------------</fo:block>-->
                 <#if postalAddress?has_content>
                  <#if postalAddress.attnName?has_content>
                  <fo:block>${postalAddress.attnName?if_exists}</fo:block></#if><#if postalAddress.toName?has_content><fo:block> ${postalAddress.toName?if_exists}</fo:block></#if>
                  
                   <fo:block>${postalAddress.address1?if_exists}</fo:block>
                   <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
                   <#if postalAddress.area?has_content><fo:block>${postalAddress.area?if_exists}</fo:block></#if>
                   <#if postalAddress.directions?has_content><fo:block>${postalAddress.directions?if_exists}</fo:block></#if>
                   <fo:block>${postalAddress.city?if_exists}<#if postalAddress.postalCode?has_content> - ${postalAddress.postalCode}</#if></fo:block>
                  	<#if postalAddress.stateProvinceGeoId?has_content>
                  	<fo:block>
                  	<#assign stateName = delegator.findByPrimaryKey("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",postalAddress.stateProvinceGeoId?if_exists))?if_exists/>
                 		${stateName.geoName?capitalize?if_exists}
                  		<#--${postalAddress.stateProvinceGeoId}--> 
                  	</fo:block>
                  	</#if>
                  	<#if postalAddress.countryGeoId?has_content>
                  	<fo:block>
                  	<#assign countryName = delegator.findByPrimaryKey("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",postalAddress.countryGeoId?if_exists))?if_exists/>
 						${countryName.geoName?if_exists}
                  	<#--${postalAddress.countryGeoId?if_exists} -->
                  	</fo:block>
                  	</#if>
                    <#--<#if postalAddress.mobile?exists><fo:block>Ph - ${postalAddress.mobile?if_exists} </fo:block></#if>-->
                    <#--<#if email?has_content><fo:block>Email - ${email?if_exists} </fo:block></#if>-->
 					<fo:block>
 					<#if partyDetailsMap?has_content && partyDetailsMap.telephoneMobile?has_content>
 						Contact No-${partyDetailsMap.telephoneMobile.contactNumber?if_exists} 						
 						
 						
 					<#else>
 						<#--if partyContactMechValueMaps?has_content>
		              	 <#list partyContactMechValueMaps as partyContactMechValueMap>
		        			<#assign contactMech = partyContactMechValueMap.contactMech?if_exists />
		        			 <#if contactMech.contactMechTypeId?if_exists = "TELECOM_NUMBER">
		        			 	<#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
		        			 	<#if telecomNumber?exists>
		        			 		Contact No-${telecomNumber.contactNumber?if_exists}
		        			 	</#if>
		        			 </#if>
		        		 </#list>
        				</#if-->
        			</#if>
 					</fo:block>
                 </#if>
               </fo:table-cell>
               </#if>
             </#list>
             <#if createEmptyCell>
             <fo:table-cell><fo:block  font-weight="bold" font-size="10px" padding="5px 0 5px 0"></fo:block></fo:table-cell>
             </#if>
             </fo:table-row>
             <#if phoneNumbers?has_content>
             <fo:table-row> 
	             <fo:table-cell font-size="10px">
	             <fo:block font-weight="bold">Contact Numbers :
		             <#list phoneNumbers as phoneNumber>
		                   <#if phoneNumber?has_content>${phoneNumber}</#if>
		                   <#if !phoneNumber_has_next> , </#if>
		             </#list>
		          </fo:block>
	         </fo:table-cell>  
	         
	         
	         
	         </fo:table-row>        
             </#if>
             <#-- The empty cell is required in order to fill the table-row element and avoid a validation error -->
             
            
         </fo:table-body>
       </fo:table>
       <fo:block white-space-collapse="false" space-after="10pt"> </fo:block>
		<fo:block  space-after="10pt"></fo:block>
		  <#-- payment info -->
		
	
    
      
<fo:block space-after="0pt"/>


</#escape>
