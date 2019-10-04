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

<div id="fb-root"></div> 
<script src="http://connect.facebook.net/en_US/all.js"></script> 
<script> 

var clicked = 0; 
FB.init({ 

appId:226040647548176, cookie:true, 
status:true, xfbml:true 

}); 
function setLogin(){ 
clicked = 1; 
alert("coming");
FB.getLoginStatus(function(response) { abcd(); }); 

} 

//FB.Event.subscribe('auth.login', function(response) { abcd(); }); 

FB.Event.subscribe('auth.logout', function(response) { clicked = 0; }); 

function abcd(){ 
alert("haaa"); 
FB.api('/me', function(response) { 
if(response!=null && response.last_name != undefined){ 

document.fbLogin.firstName.value=response.first_name; 

document.fbLogin.lastName.value=response.last_name; 
if(response.gender!=null){ 
if(response.gender.toLowerCase()=="male"){ 
document.fbLogin.gender.value="M"; 
}else{ 
document.fbLogin.gender.value="F"; 
} 
} 
document.fbLogin.userLoginId.value=response.email; 




document.fbLogin.submit(); 


} 

}); 
} 


</script> 

<#-- end -->
<script type="text/javascript">
	function checkLogin()
 		{
   alert("Please login for inviting friend");
   return false;
}
</script>
<script type="text/javascript">
	function newopengmail(){
		var form = document.googleform;
		uri = "https://accounts.google.com/o/oauth2/auth?";
		
		for(var i=0; i < form.length; i++){
			uri = uri + form.elements[i].name + "=" + form.elements[i].value + "&";
		}
		var obj = window.open(uri,"mywindow","menubar=1,resizable=1,width=350,height=500");
	}
	function newopenfacebook()
 	{
 	   var form = document.facebookForm; 	   
 		uri = "https://www.facebook.com/dialog/oauth?"; 		
 		for(var i=0; i < form.length; i++){
 			uri = uri + form.elements[i].name + "=" + form.elements[i].value + "&";
 		
 		}
 		
 	 	var obj = window.open(uri,"mywindow","menubar=1,resizable=1,width=450,height=350");
 	}
</script>
<form method="post" action="" name="facebookForm">
  <input type="hidden" name="client_id" value="520771177980080"/>
  <input type="hidden" name="redirect_uri" value="https://localhost:8444/control/oauth2callbackfb"/>
  <input type="hidden" name="scope" value="user_birthday"/>
  <input type="hidden" name="state" value="profile"/>
</form>
<form method="post" action="" name="googleform">
	<input type="hidden" name="response_type" value="token"/>
	<input type="hidden" name="client_id" value="385547433416.apps.googleusercontent.com"/>
	<input type="hidden" name="redirect_uri" value="https://youmart.nichesuite.com:8449/control/oauth2callback"/>
	<input type="hidden" name="scope" value="https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/userinfo.profile"/>
	<input type="hidden" name="state" value="profile"/>
</form>
 <#if !autoUserLogin?has_content>
<div class="catalog" style="-moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;">
<div class="sidedeeptitle">${uiLabelMap.CommonLogin}</div>
<div style="padding:5px 5px 5px 3px">
  

  <form method="post" action="<@ofbizUrl>login</@ofbizUrl>" name="loginform" class="horizontal">
    
      <div>
        <label for="userName" style="width:173px;">Email id:</label>
        <input type="text" id="userName" size="23" name="USERNAME" value="<#if requestParameters.USERNAME?has_content>${requestParameters.USERNAME}<#elseif autoUserLogin?has_content>${autoUserLogin.userLoginId}</#if>"/>
      </div>
<#if autoUserLogin?has_content>
      <p>(${uiLabelMap.CommonNot} ${autoUserLogin.userLoginId}? <a href="<@ofbizUrl>${autoLogoutUrl}</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
</#if>
      <div>
        <label for="password" style="width:173px;">${uiLabelMap.CommonPassword} :</label>
        <input type="password" id="password" size="23" name="PASSWORD" value=""/>
      </div>
      <div style="margin:5px 0 5px 0px;">
        <input type="submit" class="buttontextblue" value="${uiLabelMap.CommonLogin}"/>
        <a href="<@ofbizUrl>checkLogin</@ofbizUrl>" style="text-decoration:underline; font-weight:normal;">Forgot Password</a>
      </div>
     <!-- <div>
      	<input type="button" onclick="newopengmail()" Value="Gmail Login"/>
      	
      	<div class="popup-form-row">
                        <fb:login-button scope="email,user_checkins,user_hometown,user_location,user_about_me,user_birthday " onclick="setLogin();">Connect with Facebook</fb:login-button>
                  </div>
 
				
      </div>-->
      <div style="margin:0 auto; text-align:center;">
        <label for="newcustomer_submit" style="width:172px; margin:0 auto;">New Customer:</label>
        <a href="<@ofbizUrl>newcustomer</@ofbizUrl>" style="font-size:12px;">Sign Up</a>
      </div>
    
  </form>
  </div>
</div>
</#if>


<#--if userLogin?has_content && userLogin.userLoginId != "anonymous">
<div class="catalog" style="-moz-border-radius: 0em 4em 1em 0em; border-radius: 0em 0em 0.5em 0.5em;">
<div class="sidedeeptitle">Logged User</div>
<div style="padding:5px 5px 5px 3px">
		   			<#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
					<#assign person = delegator.findByPrimaryKeyCache("Person", findPftMap)>
					<a href="<@ofbizUrl>myaccount</@ofbizUrl>">${person.firstName?html} ${person.lastName?html}</a> | <a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
</div>
</div>
</#if-->



<div class="endcolumns">&nbsp;</div>

<script language="JavaScript" type="text/javascript">
  <#if autoUserLogin?has_content>document.loginform.PASSWORD.focus();</#if>
  <#if !autoUserLogin?has_content>document.loginform.USERNAME.focus();</#if>
</script>


<form name="fbLogin" method="POST" action="<@ofbizUrl>facebookLogin</@ofbizUrl>">
		<input type="hidden" name="firstName"/>
		<input type="hidden" name="lastName"/>
		<input type="hidden" name="gender"/>
		<input type="hidden" name="userLoginId"/>
	</form>
