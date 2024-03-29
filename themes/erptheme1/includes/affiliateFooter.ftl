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

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
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
<div align="center">
<div id="FooterMainDIV">

  <br/><br/>
  <div align="center">
    <a href="http://jigsaw.w3.org/css-validator/"><img style="border:0;width:88px;height:31px" src="<@ofbizContentUrl>/images/vcss.gif</@ofbizContentUrl>" alt="Valid CSS!"/></a>
    <a href="http://validator.w3.org/check?uri=referer"><img style="border:0;width:88px;height:31px" src="<@ofbizContentUrl>/images/valid-xhtml10.png</@ofbizContentUrl>" alt="Valid XHTML 1.0!"/></a>
  </div>
  <br/>
  <div class="tabletext" align="center">
    <div class="tabletext">Copyright (c) 2001-${nowTimestamp?string("yyyy")} NicheSuite</div>
    <br/><br/>
    <div class="tabletext">Powered by <a href="www.nicheproconsulting.com" class="tabletext" target="_blank">NicheSuite</a></div>
  </div>
  <div class="tabletext" align="center"><a href="<@ofbizUrl>policies</@ofbizUrl>">${uiLabelMap.EcommerceSeeStorePoliciesHere}</a></div>

</div>

</div>
</body>
</html>
