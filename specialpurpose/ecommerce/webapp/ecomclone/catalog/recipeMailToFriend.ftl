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

<html>
<head>
  <title>Email a Recipe</title>
  
<script  type="text/javascript">
 function validatefriend(){
 
	var email_sendTo = document.getElementById('sendTo').value;
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
 }
</script>
</head>
<body class="ecbody">
<#if requestParameters.mailsent?has_content>
<script  type="text/javascript">
	windows.close();
</script>
</#if>
<#assign processedRequest = requestAttributes.processed?if_exists>
    <form name="mailRecipe" action="<@ofbizUrl>mailRecipe</@ofbizUrl>" method="post">
      <input type="hidden" name="mailsent" value="Y"/>
      <div style="background-color:#F0F0F0; min-width:425px; border:1px solid #B8B8B8; width:425px; overflow:hidden; min-height:280px; height:280px;">
      <#if processedRequest!='Y'>
        <table style="padding-top:10px;">
          <tr>
            <td align="right"><b>${uiLabelMap.CommonEmailTo}:</b></td>
            <td><input type="text" name="sendTo" size="30" id="sendTo"></td>
            <input type="hidden" name="recipeId" size="30" id="recipeId" value="${parameters.recipeId?if_exists}"/>
            </tr>
          
          <tr>
            <td align="right"><b>${uiLabelMap.CommonMessage}:</b></td>
            <td><textarea cols="35"  rows="5" name="note" id="note"></textarea></td>
          </tr>
          
          <tr>
            <td colspan="2" align="left" style="padding-left:85px;">
             <input type="submit" onClick="return validatefriend();" value="${uiLabelMap.CommonSend}" />
             </td>
          </tr>
          
        </table>
        <#else>
             <script language="JavaScript" type="text/javascript">
        <!-- //
        window.close();
        // -->
        </script>
        </#if>
        </div>
        </div>
    </form>
</body>
</html>
