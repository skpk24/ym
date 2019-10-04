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
<#if !autoUserLogin?has_content>
<script>
function validateTellAFriend(){
	var name = document.getElementById('userName').value;
	var email_sendFrom = document.getElementById('sendFrom').value;
	var email_sendTo = document.getElementById('sendTo').value;
	var message = document.getElementById('message').value;
	var emailFilter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(name=='' || name==null){
	alert("Please enter name.");
	document.getElementById('userName').focus();
	return false;
	}
	if(email_sendFrom=='' || email_sendFrom == null){
	alert("Please enter email.");
	document.getElementById('sendFrom').focus();
	return false;
	}
	else if (!emailFilter.test(email_sendFrom)) {
    alert("Please enter valid email address");
    document.getElementById('sendFrom').value='';
    document.getElementById('sendFrom').focus();
    return false;
	}
	if(email_sendTo=='' || email_sendTo == null){
	alert("Please enter to email address.");
	document.getElementById('sendTo').focus();
	return false;
	}
	else if (!emailFilter.test(email_sendTo)) {
    alert("Please enter valid to email address");
    document.getElementById('sendTo').value='';
    document.getElementById('sendTo').focus();
    return false;
	}
	if(message=='' || message==null){
	alert("Please enter message.");
	document.getElementById('message').focus();
	return false;
	}
}
</script>
<#else>
<script>
function validateTellAFriend(){
	var email_sendTo = document.getElementById('sendTo').value;
	var message = document.getElementById('message').value;
	var emailFilter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if(email_sendTo=='' || email_sendTo == null){
	alert("Please enter email.");
	document.getElementById('sendTo').focus();
	return false;
	}
	else if (!emailFilter.test(email_sendTo)) {
    alert("Please enter valid email address");
    document.getElementById('sendTo').value='';
    document.getElementById('sendTo').focus();
    return false;
	}
	if(message=='' || message==null){
	alert("Please enter message.");
	document.getElementById('message').focus();
	return false;
	}
}
</script>
</#if>
<html>
<head>
  <title>${uiLabelMap.EcommerceTellAFriend}</title>
</head>
<body class="ecbody">
    <form name="tellafriend" action="<@ofbizUrl>emailFriend</@ofbizUrl>" method="post">
      <#if requestParameters.productId?exists>
        <input type="hidden" name="pageUrl" value="<@ofbizUrl fullPath="true" encode="false" secure="false">/product?product_id=${requestParameters.productId}</@ofbizUrl>" />
      <#elseif requestParameters.categoryId?exists>
        <input type="hidden" name="pageUrl" value="<@ofbizUrl fullPath="true" encode="false" secure="false">/category?category_id=${requestParameters.categoryId}</@ofbizUrl>" />
      <#else>
        <#assign cancel = "Y">
      </#if>
      <#if !cancel?exists>
        <table>
          <tr>
            <#if autoUserLogin?has_content>
             <#if autoUserLogin.userLoginId?exists>
               <td>${uiLabelMap.CommonYouremail}:</td>
             <td>
             ${autoUserLogin.userLoginId?if_exists}
             </td>
             <input type="hidden" name="sendFrom" value="${autoUserLogin.userLoginId?if_exists}"/>
             <#assign flag = "Y">
             </#if>
             <#else>
                <td>My Name:</td>
                 <td>
                 <input type="text" name="userName" id="userName" value=""/>&nbsp;*
                </td>
                <tr>
                   <td>My Email Id:</td>
                    <td>
                 <input type="text" name="sendFrom" id="sendFrom" value=""/>&nbsp;*
                </td>
                  </tr>
                 <td>
               </#if>
          </tr>
          <tr>
            <td>${uiLabelMap.CommonEmailTo}:</td>
            <td><input type="text" name="sendTo" id="sendTo" size="30" />&nbsp;*</td>
          </tr>
          <tr>
            <td colspan="2" align="center">${uiLabelMap.CommonMessage}</td>
          </tr>
          <tr>
            <td colspan="2" align="center">
              <textarea cols="40"  rows="5" name="message" id="message"></textarea>&nbsp;*
            </td>
          </tr>
          <tr>
            <td colspan="2" align="center">
              <input type="submit" onClick="return validateTellAFriend();" value="${uiLabelMap.CommonSend}" />
            </td>
          </tr>
        </table>
      <#else>
        <script language="JavaScript" type="text/javascript">
        <!-- //
        window.close();
        // -->
        </script>
        <div class="tabletext">${uiLabelMap.EcommerceTellAFriendSorry}</div>
      </#if>
    </form>
</body>
</html>
