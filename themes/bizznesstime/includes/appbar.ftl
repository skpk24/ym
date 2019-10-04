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

<script type="text/javascript">
$(document).ready(function() {
               
               var mailLeave =    $("#header-nav").mouseleave(function() {
               document.getElementById('header-nav').style.display="none";
                });
            });
</script>

<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>

<#if userLogin?has_content>
        <div id="main-nav">
            <h2 class="contracted">${uiLabelMap.CommonApplications}</h2>
            <div id="header-nav" class="clearfix" style="display:none">
                <ul>
                <h4>${uiLabelMap.CommonPrimaryApps}</h4>
                    <#list displayApps as display>
              <#assign thisApp = display.getContextRoot()>
              <#assign permission = true>
              <#assign selected = false>
              <#assign permissions = display.getBasePermission()>
              <#list permissions as perm>
                <#if perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session)>
                  <#-- User must have ALL permissions in the base-permission list -->
                  <#assign permission = false>
                </#if>
              </#list>
              <#if permission == true>
                <#if thisApp == contextPath || contextPath + "/" == thisApp>
                  <#assign selected = true>
                </#if>
                <#assign thisURL = thisApp>
		        <#if thisURL.equals("/setup")>
					<#if security.hasPermission("SUPERSETUP_VIEW", userLogin)>
						<#assign thisURL = thisURL + "/control/store">
					<#else>
						<#assign thisURL = thisURL + "/control/setup">	
					</#if>        
		        <#elseif thisApp != "/">
                  <#assign thisURL = thisURL + "/control/main">
                </#if>
                  <li><a href="${thisURL + externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a></li>
              </#if>
            </#list>
                </ul>
                
                <#--<#include "component://bizznesstime/includes/secondary-appbar.ftl" />-->
            </div>
        </div>
</#if>
<#assign SetUp = request.getContextPath()?if_exists>
<#if userLogin?has_content && (SetUp?has_content && SetUp.equals("/setup"))>
 <div id="app-navigation">
 	<h2>Setup Application</h2>
    <ul><li><ul>
		<#assign SetUp = request.getContextPath()>
		<#assign unselectedClassName = "">
		<#assign selectedClassMap = {page.headerItem1?default("void") : "selected"}>
		<#if SetUp.equals("/setup") || SetUp.equals("/partymgr")>
		<#if security.hasPermission("SUPERSETUP_VIEW", userLogin)>
				<li class="${selectedClassMap.store?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/store"  title="${uiLabelMap.SetUpStore}">${uiLabelMap.SetUpStore}</a>
			        </div>	
		        </li>
		         <li class="${selectedClassMap.DataBaseManager?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/DataBaseManager"  title="${uiLabelMap.DBManager}">${uiLabelMap.DBManager}</a>
			        </div>	
		        </li>
		         <li class="${selectedClassMap.webpossetting?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/webpossetting"  title="${uiLabelMap.WebposSetting}">${uiLabelMap.WebposSetting}</a>
			        </div>	
		        </li>
		        <li class="${selectedClassMap.clients?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/clients"  title="${uiLabelMap.SetUppassword}">${uiLabelMap.SetUppassword}</a>
			        </div>	
		        </li>
		         <li class="${selectedClassMap.SMS?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a   href="/setup/control/smsproviders"  title="SmsProviders">Sms Detail</a>
			        </div>	
		        </li>
	        <#else>
	        
				<li class="${selectedClassMap.setuplogo?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/setup"  title="${uiLabelMap.SetUpLogo}">${uiLabelMap.SetUpLogo}</a>
			        </div>	
		        </li>
		        <li class="${selectedClassMap.companyinfo?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/companyinfo?facilityId=WebStoreWarehouse"  title="${uiLabelMap.CompanyInfo}">${uiLabelMap.CompanyInfo}</a>
			        </div>	
		        </li>
		        <li class="${selectedClassMap.logindetails?default(unselectedClassName)}">
			        <div class="submain-navigation">
			        	<a  href="/setup/control/users"  title="Users">Users</a>
			        </div>	
		        </li>
	       </#if>
      </#if>
    </ul></li></ul>
    </div>
    </#if>