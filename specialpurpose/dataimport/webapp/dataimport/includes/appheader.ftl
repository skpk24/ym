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

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (requestAttributes.security)?exists><#assign security = requestAttributes.security></#if>
<#if (requestAttributes.userLogin)?exists><#assign userLogin = requestAttributes.userLogin></#if>
<#if (requestAttributes.checkLoginUrl)?exists><#assign checkLoginUrl = requestAttributes.checkLoginUrl></#if>

<#assign unselectedLeftClassName = "">
<#assign unselectedRightClassName = "">
<#assign selectedLeftClassMap = {page.headerItem?default("void") : "selected"}>
<#assign selectedRightClassMap = {page.headerItem?default("void") : "selected"}>

<#if userLogin?has_content>
<div id="app-navigation">
<ul>
    <li>
		<ul>
		  <li class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}"><a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.ProductMain}</a></li>  
		  <#--li class="${selectedRightClassMap.report?default(unselectedRightClassName)}"><a href="<@ofbizUrl>report</@ofbizUrl>">Report</a></li-->  
		    <#--li class="${selectedRightClassMap.report?default(unselectedRightClassName)}"><a href="<@ofbizUrl>ProductImportSheet</@ofbizUrl>">Product Upload</a></li--> 
		     <#-- li class="${selectedLeftClassMap.main?default(unselectedLeftClassName)}"><a href="<@ofbizUrl>imageUploadMain</@ofbizUrl>">Image Upload</a></li -->   
		</ul>
	</li>	
</ul>
<br class="clear" />
</div>
</#if>