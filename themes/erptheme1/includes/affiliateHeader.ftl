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
<#include "component://ecommerce/webapp/ecommerce/includes/headerHead.ftl" />

<body>
<div align="center">
<div id="MainDecoratorDIV">

<div id="BannerLeftDIV"></div>
		<div id="ENZELogo">
			<a href="<@ofbizUrl>main</@ofbizUrl>">
				<IMG title="NicheSuite" height=85 alt="NicheSuite" src="/enzetheme/ENZE-Logo.jpg" width=154 border=0 align="middle">
			</a>
		</div>
<div id="Background1"></div>
    <#--<div id="left">
      <#if sessionAttributes.overrideLogo?exists>
        <img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif catalogHeaderLogo?exists>
        <img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
        <img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo"/>
      </#if>
    </div>-->
    <div id="right">
     <div class="affiliateloginregister">
        <#if sessionAttributes.autoName?has_content>
          ${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName?html}!
          (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="linktext">${uiLabelMap.CommonClickHere}</a>)
          <#if sessionAttributes.autoUserLogin?has_content>
         	<#assign userLogin = sessionAttributes.autoUserLogin?if_exists>
         	<#assign partyId = userLogin.partyId />
         	<#if partyId?has_content>
         		<#assign affiliate = delegator.findByPrimaryKey("Affiliate",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId))/>
         		<#assign partyGroup = delegator.findByPrimaryKey("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",partyId))/>
         		<#if affiliate?has_content>
         		<table>
         			<#if partyGroup?has_content>
         			<tr>
         				<td class="affiliateloginregister1">Company Name :</td>
         				<td class="affiliateloginregister1">${partyGroup.groupName?if_exists}</td>
         			</tr>	
         			</#if>
         			<tr>
         				<td class="affiliateloginregister1">WebSite Name &nbsp; :</td>
         				<td class="affiliateloginregister1">${affiliate.affiliateName?if_exists}</td>
         			</tr>		
         		</table>	
         		</#if>
         	</#if>
          </#if>
        <#else/>
          ${uiLabelMap.CommonWelcome}! Anonymous User
        </#if>
      </div>
      
    <div id="" class="affiliatestorename">
      <#if !productStore?exists>
        <h2>${uiLabelMap.EcommerceNoProductStore}</h2>
      </#if>
      <#if (productStore.title)?exists>
      <div id="company-name" class="affiliatestorename">${productStore.title}</div>
      </#if>
      <#if (productStore.subtitle)?exists>
      <div id="company-subtitle" class="affiliatestorenamesubtitle">${productStore.subtitle}</div>
      </#if>
    </div>
  </div>
  
<#assign productStoreId = "" />
<#if parameters?has_content && parameters.productStoreId?has_content>
    <#assign productStoreId = parameters.productStoreId?if_exists />
<#else>
    <#assign productStoreId = Static["org.ofbiz.product.store.ProductStoreWorker"].getProductStoreId(request)?if_exists /> 
</#if>    
<#assign session = request.getSession(false)/>
<#if productStoreId?has_content>
	<#assign session = session.setAttribute("productStoreId",productStoreId)?if_exists>  	
</#if>
  <div id="affiliateMainMenusDIV">
  <div class="MainMenuLink" id="MainMenusLink" align="left">
      <#if sessionAttributes.productStoreId?has_content>
	     <a href="<@ofbizUrl>main</@ofbizUrl>" class="MainMenuLink"> ${uiLabelMap.CommonMain}</a>
	     &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <a href="<@ofbizUrl>AffiliateStands?standId=${requestParameters.standId?if_exists}</@ofbizUrl>"class="MainMenuLink">${uiLabelMap.MyStand}'s</a>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <a href="<@ofbizUrl>finance</@ofbizUrl>" class="MainMenuLink">${uiLabelMap.CommonFinance}</a>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <a href="<@ofbizUrl>config</@ofbizUrl>" class="MainMenuLink">${uiLabelMap.CommonConfiguration}</a>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <a href="<@ofbizUrl>affstats</@ofbizUrl>" class="MainMenuLink">${uiLabelMap.Statistics}</a>
      <#else>
	      <span class="MainMenuLink">${uiLabelMap.CommonMain}</span>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <span class="MainMenuLink">${uiLabelMap.MyStand}'s</span>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <span class="MainMenuLink">${uiLabelMap.CommonFinance}</span>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <span class="MainMenuLink">${uiLabelMap.CommonConfiguration}</span>
	      &nbsp;<span class="MainMenuLinkPipe">/</span> 
	      <span class="MainMenuLink">${uiLabelMap.Statistics}</span>
      </#if>
      
      
    </div>
    <span style="text-align: left; padding-left: 250px;">
      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
        <a href="<@ofbizUrl>autoLogout?psId=${sessionAttributes.productStoreId?if_exists}</@ofbizUrl>" class="MainMenuLink">${uiLabelMap.CommonLogout}</a>
      <#else/>
        <#--<a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>" class="MainMenuLink">${uiLabelMap.CommonLogin}</a>-->
      </#if>
      </span>
  </div>
  
  </div>