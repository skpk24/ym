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
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title><#if title?has_content>${title}<#elseif titleProperty?has_content>${uiLabelMap.get(titleProperty)}</#if>: ${(productStore.storeName)?if_exists}</title>
  <#if layoutSettings.VT_SHORTCUT_ICON?has_content>
    <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
  <#elseif layoutSettings.shortcutIcon?has_content>
    <#assign shortcutIcon = layoutSettings.shortcutIcon/>
  </#if>
  <#if shortcutIcon?has_content>
    <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon)}</@ofbizContentUrl>" />
  </#if>
  <#if layoutSettings.javaScripts?has_content>
    <#--layoutSettings.javaScripts is a list of java scripts. -->
    <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
    <#list layoutSettings.javaScripts as javaScript>
      <#if javaScriptsSet.contains(javaScript)>
        <#assign nothing = javaScriptsSet.remove(javaScript)/>
        <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
      </#if>
    </#list>
  </#if>
  <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
    <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
      <script type="text/javascript" src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>"></script>
    </#list>
  </#if>
  <#if layoutSettings.styleSheets?has_content>
    <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
    <#list layoutSettings.styleSheets as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.VT_STYLESHEET?has_content>
    <#list layoutSettings.VT_STYLESHEET as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
    <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
    <#list layoutSettings.rtlStyleSheets as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
    <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
      <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
    </#list>
  </#if>
  ${layoutSettings.extraHead?if_exists}
  <#if layoutSettings.VT_EXTRA_HEAD?has_content>
    <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
      ${extraHead}
    </#list>
  </#if>

  <#-- Append CSS for catalog -->
  <#if catalogStyleSheet?exists>
    <link rel="stylesheet" href="${StringUtil.wrapString(catalogStyleSheet)}" type="text/css"/>
  </#if>
  <#-- Append CSS for tracking codes -->
  <#if sessionAttributes.overrideCss?exists>
    <link rel="stylesheet" href="${StringUtil.wrapString(sessionAttributes.overrideCss)}" type="text/css"/>
  </#if>
  <#-- Meta tags if defined by the page action -->
  <#if metaDescription?exists>
    <meta name="description" content="${metaDescription}"/>
  </#if>
  <#if metaKeywords?exists>
    <meta name="keywords" content="${metaKeywords}"/>
  </#if>
  
  
<!-- Facebook Conversion Code for Website conversion -->
<script>(function() {
  var _fbq = window._fbq || (window._fbq = []);
  if (!_fbq.loaded) {
    var fbds = document.createElement('script');
    fbds.async = true;
    fbds.src = '//connect.facebook.net/en_US/fbds.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fbds, s);
    _fbq.loaded = true;
  }
})();
window._fbq = window._fbq || [];
window._fbq.push(['track', '6022565339651', {'value':'0.00','currency':'INR'}]);
</script>
<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=6022565339651&amp;cd[value]=0.00&amp;cd[currency]=INR&amp;noscript=1" /></noscript>
  	
<link href="http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="/images/google/css/style.css" />

<link rel="stylesheet" href="/erptheme1/css/nav-style.css" />
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript" src="/images/google/js/script.js" async></script>
	 	<script type="text/javascript" src="/erptheme1/js/general.js" async></script>
		
		
		
<#assign req = request.getRequestURI()>

<#if req?has_content && req.equals("/control/home")>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>
	<!-- script src="/erptheme1/js/jquery-ui.min.js"  async></script -->
</#if> 	
</head>
