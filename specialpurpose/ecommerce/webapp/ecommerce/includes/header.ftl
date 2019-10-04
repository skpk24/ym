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


  <div id="ecom-header">
    <div id="left">
      <#if sessionAttributes.overrideLogo?exists>
        <a href="<@ofbizUrl>main</@ofbizUrl>"><img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo"/></a>
      <#elseif catalogHeaderLogo?exists>
        <a href="<@ofbizUrl>main</@ofbizUrl>"><img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo"/></a>
      <#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
        <a href="<@ofbizUrl>main</@ofbizUrl>"><img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo"/></a>
      </#if>
    </div>
    <div id="right">
     <ul id="right-links">
      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
        <li id="header-bar-logout"><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>
      <#else/>
        <li id="header-bar-login"><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
        <li id="header-bar-register"><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a></li>
      </#if>
      <li style="border:none;" ><a href="#">My Account</a></li>
     </ul>
     </div></br></br></br>
     <div style="float:right">                           
          
          <input type="text" value="search" class="main-search"><input type="button" title="Go" class="btn-search"></br></br>
          ${screens.render("component://ecommerce/widget/CartScreens.xml#microcart")}
    </div>
    <div id="middle">
      <#if !productStore?exists>
        <h2>${uiLabelMap.EcommerceNoProductStore}</h2>
      </#if>
      
      <#--<#if (productStore.title)?exists><div id="company-name">${productStore.title}</div></#if>
      <#if (productStore.subtitle)?exists><div id="company-subtitle">${productStore.subtitle}</div></#if>-->
      <#--<div id="welcome-message">
        <#if sessionAttributes.autoName?has_content>
          ${uiLabelMap.CommonWelcome}&nbsp;${sessionAttributes.autoName?html}!
          (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="linktext">${uiLabelMap.CommonClickHere}</a>)
        <#else/>
          ${uiLabelMap.CommonWelcome}!
        </#if>
      </div>-->
    </div>
  </div>

  <div id="ecom-header-bar">
    <div style="float:left; border-right:1px solid #fff;"><a href="<@ofbizUrl>main</@ofbizUrl>"><img src="/images/home-icon.jpg" alt="home"></a></div>
  <ul id="left-links">	
  	<li><a href="#">FURNITURE</a></li>
  	<li><a href="#">HOME DECOR</a></li>
  	<li><a href="#">APPLIANCES</a></li>
  	<li><a href="#">HEALTH</a></li>
  	<li><a href="#">BEAUTY</a></li>
  	<li><a href="#">JEWELLERY</a></li>
  	<li><a href="#">CLOTHING</a></li>
  	<li><a href="#">ACCESSORIES</a></li>
  	<li><a href="#">LEISURE</a></li>
  	<li><a href="#">CONTACT US</a></li>
  	<li style="background:none;"><a href="#">BRANDS</a></li>	
  </ul>
  <div style="float:right; border-left:1px solid #fff;"><a href="<@ofbizUrl>main</@ofbizUrl>"><img src="/images/cart.jpg" alt="cart"></a></div>
    <#--<ul id="left-links">
      <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
        <li id="header-bar-logout"><a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a></li>
      <#else/>
        <li id="header-bar-login"><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a></li>
        <li id="header-bar-register"><a href="<@ofbizUrl>newcustomer</@ofbizUrl>">${uiLabelMap.EcommerceRegister}</a></li>
      </#if>
      <li id="header-bar-contactus">
        <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
          <a href="<@ofbizUrl>contactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a></li>
        <#else>
          <a href="<@ofbizUrl>AnonContactus</@ofbizUrl>">${uiLabelMap.CommonContactUs}</a></li>
        </#if>
      <li id="header-bar-main"><a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.CommonMain}</a></li>
    </ul>-->
    <#--<ul id="right-links">
       NOTE: these are in reverse order because they are stacked right to left instead of left to right 
      <#if !userLogin?has_content || (userLogin.userLoginId)?if_exists != "anonymous">
        <li id="header-bar-viewprofile"><a href="<@ofbizUrl>viewprofile</@ofbizUrl>">${uiLabelMap.CommonProfile}</a></li>
        <li id="header-bar-ListMessages"><a href="<@ofbizUrl>messagelist</@ofbizUrl>">${uiLabelMap.CommonMessages}</a></li>
        <li id="header-bar-ListQuotes"><a href="<@ofbizUrl>ListQuotes</@ofbizUrl>">${uiLabelMap.OrderOrderQuotes}</a></li>
        <li id="header-bar-ListRequests"><a href="<@ofbizUrl>ListRequests</@ofbizUrl>">${uiLabelMap.OrderRequests}</a></li>
        <li id="header-bar-editShoppingList"><a href="<@ofbizUrl>editShoppingList</@ofbizUrl>">${uiLabelMap.EcommerceShoppingLists}</a></li>
        <li id="header-bar-orderhistory"><a href="<@ofbizUrl>orderhistory</@ofbizUrl>">${uiLabelMap.EcommerceOrderHistory}</a></li>
      </#if>
      <#if catalogQuickaddUse>
        <li id="header-bar-quickadd"><a href="<@ofbizUrl>quickadd</@ofbizUrl>">${uiLabelMap.CommonQuickAdd}</a></li>
      </#if>
    </ul>-->
  </div>