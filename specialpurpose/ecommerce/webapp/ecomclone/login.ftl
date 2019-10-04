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

<#--<h1>${uiLabelMap.CommonLogin}</h1>-->
<script type="text/javascript">
function validateLogin(){
	document.getElementById("errorMsg").innerHTML= '';
	var errorsmsg = [];
	var errors = 0;
	var userEmail = document.getElementById("userName").value;
	var userPassword = document.getElementById("password").value;
	if(userEmail == '' || userEmail == null){
		errors++;
        <#--errorsmsg[errorsmsg.length] = "Email id was empty reenter."+"</br>";-->
        errorsmsg[errorsmsg.length] = "Please enter email id."+"</br>";
        
	}
	if(userPassword == '' || userPassword == null){
		 errors++;
         <#--errorsmsg[errorsmsg.length] = "Password was empty reenter.";-->
         errorsmsg[errorsmsg.length] = "Please enter password."+"</br>";
	}
		if (errors> 0){  
			var msg ="";
			for (var i=0; i<errorsmsg.length; i++){
				msg+=errorsmsg[i]+"\n";
			}
				 document.getElementById("errorMsg").innerHTML= msg;
				 return false;
		}else{
			  return true;
		}
}
function validateForgotPassword(){
	document.getElementById("errorMsg").innerHTML= '';
	var errorsmsg = [];
	var errors = 0;
	var userEmail = document.getElementById("forgotpassword_userName").value;
	if(userEmail == '' || userEmail == null){
		errors++;
        <#--errorsmsg[errorsmsg.length] = "Email id was empty reenter."+"</br>";-->
        errorsmsg[errorsmsg.length] = "Please enter email id."+"</br>";
        
	}
	if (errors> 0){  
			var msg ="";
			for (var i=0; i<errorsmsg.length; i++){
				msg+=errorsmsg[i]+"\n";
			}
				 document.getElementById("errorMsg").innerHTML= msg;
				 return false;
		}else{
			  return true;
		}
	
}
</script>
<span class="label" style="color:#ff0000; font-weight:normal;" id="errorMsg"></span>
<div class="screenlet">
  <div class="screenlet-title-bar" style="text-align:center !important; background:none !important;"><h3 style="background:none !important;">${uiLabelMap.CommonRegistered}</h3></div>
  <div class="screenlet-body" style="width:500px; border:1px solid #dedfbf; margin:0 auto; border-radius:5px 5px; -webkit-border-radius: 5px 5px; -moz-border-radius: 5px 5px;" >
  <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" class="horizontal">
    <table cellpadding="0" cellspacing="0" border="0" width="485px;" class="login-center">
     <tr>
     	<td width="180px">
	        <label for="userName">Email Id:</label>
	    </td>
	    <td>
	        <input type="text" id="userName" style="border:1px solid #85844c; background:#eff0df; padding:5px; font-size:12px;" size="40" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/>
 			<#if autoUserLogin?has_content>
			      <p>(${uiLabelMap.CommonNot} ${autoUserLogin.userLoginId}? <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
			</#if>
		</td>
   	</tr>
   	<tr> 
   	<input type="hidden" value="${productCategoryId?if_exists}"  name="productCategoryId">
   	<input type="hidden" value="${parameters.from?if_exists}"  name="from">
   		<td> 
	        <label for="password">${uiLabelMap.CommonPassword}:</label>
	    </td>
	    <td>
	        <input type="password" style="border:1px solid #85844c; background:#eff0df; padding:5px; font-size:12px;" size="40" id="password" name="PASSWORD" value=""/>
	    </td>
	</tr>
	<tr> 
		<td>&nbsp;</td>
		<td>
	        <input type="submit" class="buttontextblue" style="width:156px;" value="${uiLabelMap.CommonLogin}" onClick="return validateLogin();"/>
	    </td>
	</tr>
	
	<tr>   
	   <td>  
	        <label for="newcustomer_submit" style="padding-top:0px !important;">Create New Account:</label>
	   </td>
	   <td>
	        <a href="<@ofbizUrl>newcustomer</@ofbizUrl>" class="buttontext" style="padding:5px 55px 5px 55px !important;">Register</a>
     	</td>
    </tr>
   </table>
  </form>
  </div>
</div>

<div class="screenlet" >
  <div class="screenlet-title-bar" style="text-align:center !important; background:none !important;"><h3 style="background:none !important;">${uiLabelMap.CommonForgotYourPassword}</h3></div>
  <div class="screenlet-body" style="width:500px; border:1px solid #dedfbf; margin:0 auto; border-radius:5px 5px; -webkit-border-radius: 5px 5px; -moz-border-radius: 5px 5px;">
  <form method="post" action="<@ofbizUrl>forgotpassword</@ofbizUrl>" class="horizontal">
  <table cellpadding="0" cellspacing="0" border="0" width="485px;" class="login-center">
  	 <tr>
     	<td width="180px">  
	      <label for="forgotpassword_userName">Email Id:</label>
	    </td>
	    <td>
	      <input type="text" id="forgotpassword_userName" style="border:1px solid #85844c; background:#eff0df; padding:5px; font-size:12px;" size="40" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/>
   		</td>
   </tr>
   <tr>
        <td>&nbsp;</td>
		<td>
		    <#--  <input type="submit" class="buttontext" name="GET_PASSWORD_HINT" value="${uiLabelMap.CommonGetPasswordHint}"/> -->
		      <input type="submit" class="buttontextblue" style="width:159px;" name="EMAIL_PASSWORD" onClick="return validateForgotPassword();" value="${uiLabelMap.CommonEmailPassword}"/>
    	</td>
    </tr>
  </table>
  </form>
  </div>
</div>
<#--    
<div class="screenlet">
  <h3>${uiLabelMap.CommonNewUser}</h3>
  <form method="post" action="<@ofbizUrl>newcustomer</@ofbizUrl>">
    <div>
      <label for="newcustomer_submit">${uiLabelMap.CommonMayCreateNewAccountHere}:</p>
      <input type="submit" class="button" id="newcustomer_submit" value="${uiLabelMap.CommonMayCreate}"/>
    <div>
  </form>
</div>
-->
<div class="endcolumns">&nbsp;</div>

<script language="JavaScript" type="text/javascript">
  <#if autoUserLogin?has_content>document.loginform.PASSWORD.focus();</#if>
  <#if !autoUserLogin?has_content>document.loginform.USERNAME.focus();</#if>
</script>
