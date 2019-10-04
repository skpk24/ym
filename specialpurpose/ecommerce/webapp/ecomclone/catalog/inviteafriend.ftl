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
function validateFields(){
	
var userName = document.getElementById('userName').value;
var sendTo = document.getElementById('sendTo').value;
var mobileNo = document.getElementById('mobileNo').value;
var emailFilter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
if(userName==''){
	alert("Please enter name.");
	document.getElementById('userName').focus();
	return false;
}
if(sendTo==''){
	alert("Please enter email.");
	document.getElementById('sendTo').focus();
	return false;
}
else if (!emailFilter.test(sendTo)) {
    alert("Please enter valid email address");
    document.getElementById('sendTo').value='';
    document.getElementById('sendTo').focus();
    return false;
 }
if(mobileNo==''){
	alert("Please enter mobile no.");
	document.getElementById('mobileNo').focus();
	return false;
}
else if(mobileNo.length < 10 || mobileNo.length > 13){
	alert("Please enter 10 to 12 digit  mobile no.");
	document.getElementById('mobileNo').value='';
	document.getElementById('mobileNo').focus();
	return false;
}
inviteAfriendValidate();
return false;
}
function isNumberKeyInviteFriend(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
              
         return true;
      }
</script>


<script type="text/javascript">

function checkEmailId(usernameObj){

var username=usernameObj.value;
var sendFrom = document.getElementById('sendFrom').value;
 if(username==""){
	   jQuery('#errorMsg8').html("");
	  document.forms['tellafriend'].sendTo.focus();
	  return false;
	 }
if(username == sendFrom){
	alert("You are registered user.");
	usernameObj.value="";
	return false;
}	 
    var url="/control/checkEmailId?username="+username;
    jQuery.ajax({url: url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
		if(data == 'SY' ){
	          //usernameObj.value = "";
	           //jQuery('#errorMsg8').html("You are registered user.");
	           alert("Your friend is already registered on YouMart!");
	           document.getElementById('sendTo').value='';
    		   document.getElementById('sendTo').focus();
	           return false;
           }
          else if(data=='Y'){
	          jQuery('#errorMsg8').html("");
	          jQuery('#userNameErr').html("");
	          return false;
          }else{
	            
          }
 	    return true; 
	  },
        error: function(data) {
          alert("Error during inviting!");
            return false; 
        }
    });  
}
function addZeroPrefix(){
	var phoneNo=document.getElementById('mobileNo');
	if(phoneNo.value.length <10 || phoneNo.value.length >12){
		document.getElementById('mobileNo').value="";
	    document.getElementById('mobileNo').focus();
	    alert("Please enter valid mobile number.");
	    return false;
	}
	if(phoneNo.value[0] != "0"){
		document.getElementById('mobileNo').value = "0"+document.getElementById('mobileNo').value;
	}
	var url="/control/checkMobileNo?mobileNo="+document.getElementById('mobileNo').value;
    jQuery.ajax({url: url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
          if(data == 'SY' ){
	          //usernameObj.value = "";
	          //jQuery('#errorMsg8').html("Mobile number is already in use!");
	           alert("Mobile number is already in use!");
	           document.getElementById('mobileNo').value="";
	           document.getElementById('mobileNo').focus();
           }
          else if(data=='Y'  ){
	          jQuery('#errorMsg8').html("");
	          jQuery('#userNameErr').html("");
          }else{
	            
          }
 	   return true;
	  },
        error: function(data) {
          alert("Error during login");
            return false; 
        }
    });  
}


function inviteAfriendValidate()
{
var sendFrom = document.getElementById("sendFrom").value;
var userName = document.getElementById("userName").value;
var sendTo = document.getElementById("sendTo").value;
var mobileNo = document.getElementById("mobileNo").value;
var message = document.getElementById("message").value;
var pageUrl = document.getElementById("pageUrl").value;


var url = "/control/emailInviteFriend?sendFrom="+sendFrom+"&userName="+userName+"&sendTo="+sendTo+"&mobileNo="+mobileNo+"&message="+message+"&pageUrl="+pageUrl;
var xmlhttp;
if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
  xmlhttp=new XMLHttpRequest();
  }
else
  {// code for IE6, IE5
  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xmlhttp.onreadystatechange=function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
	    var msg = xmlhttp.responseText;
	    if(msg == null || msg == "")
	    	msg = "Successfully invited a friend . An email send to your friend .";
	    	
	    alert(msg);
	    
	    if(msg == "Successfully invited a friend . An email send to your friend .")
	    {
	    	document.getElementById("userName").value = "";
			document.getElementById("sendTo").value = "";
			document.getElementById("mobileNo").value = "";
			    
		 	var msgDiv = '<textarea cols="35"  rows="3" name="message" id="message" style="width:167px;height:40px;margin-left:4px;"></textarea>';
		 	document.getElementById("inviteMessageDiv").innerHTML = ""
			 document.getElementById("inviteMessageDiv").innerHTML = msgDiv;
	    }
	    tellusinfo();
    }
  }
xmlhttp.open("GET",url,true);
xmlhttp.send();
}



</script>
<div class="inner-content" style="padding:0px !important;">
<table cellpadding="0" cellspacing="0" border="0">
	<tr>
	 <td>
	   <div style="background:#ffffff; border:0px solid #7a7946; min-height:144px;">
	    <div class="Shopcar_pageHead">Invite a friend</div>
	    <form name="tellafriend" action="#" method="post" onSubmit="return validateFields();">
	    
	      <#if userLogin?has_content>
	        <input type="hidden" name="pageUrl" id="pageUrl" value="<@ofbizUrl fullPath="true" encode="false" secure="false">/newcustomer?refId=${userLogin.userLoginId}</@ofbizUrl>" />
	      <#else>
	        <#assign cancel = "Y">
	      </#if>
	     <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
	        <table width="400px" style="font-weight:normal;">
              <tr>
			  	<td colspan="2"><img src="/erptheme1/dontmiss1.png" /></td>
			  </tr>
			  <tr>
			  	<td colspan="2" style="padding-left:4px;">We are persistent in building a beautiful community of YouMart citizens; hence invite your near & dear ones to shop with YouMart. </td>
			  </tr>
			  <tr>
			  	<td colspan="2" style="padding-left:4px;">You earn rewards of <b>2000 points</b> everytime you invite a new friend/family to shop with us, which will be added to your YouMart Savings balance.</td>
			  </tr>
			  <tr>
			  	<td colspan="2" style="padding-left:4px;">Not enough!! You also get <b>flat 5% additional discount</b> on every new reference !!</td>
			  </tr>
	          <tr>
	            <td width="80px">${uiLabelMap.CommonYouremail}:</td>
	          
	            <#if autoUserLogin?has_content && autoUserLogin.userLoginId?exists><td>${autoUserLogin.userLoginId?if_exists}</td>
	            <td><input type="hidden" name="sendFrom" id="sendFrom" value="${autoUserLogin.userLoginId?if_exists}"/></td></#if>
	          </tr>
	          <tr>
	            <td >Name :</td>
	            <td><input type="text" name="userName" id="userName" value="" size="30" />*</td>
	          </tr>
	          <tr>
	            <td>${uiLabelMap.CommonEmailTo} :</td>
	            <td><input type="text" name="sendTo" id="sendTo" size="30" onblur="return checkEmailId(this);"/>*</td>
	            <span id="errorMsg8" style="color: red;"></span>
	            <div id="userNameErr" style="float:right;color:red; display:none;"></div>
	          </tr>
	          <tr>
	            <td>Mobile No :</td>
	            <td><input type="text" name="mobileNo" id="mobileNo" size="30" maxlength="10" onChange="addZeroPrefix();" onkeypress="return isNumberKeyInviteFriend(event);"/>*
	            <span id="errorMsg8" style="color: red;"></span>
	            <div id="userNameErr" style="float:right;color:red; display:none;"></div>
	            </td>
	          
	          </tr>
	          <tr>
	            <td align="center">${uiLabelMap.CommonMessage} :</td>
	          
	            <td align="center"> 
	            
	            <div id="inviteMessageDiv">
	            	<textarea cols="35"  rows="3" name="message" id="message" style="width:167px;height:40px;margin-left:4px;"></textarea>
	            </td>
	            </td>
	          </tr>
	          <tr>
	          <td align="center"></td>
	            <td align="center">
	            	<input type="button" onClick="validateFields()" value="${uiLabelMap.CommonSend}">
	              <#--input type="submit" value="${uiLabelMap.CommonSend}" /-->
	            </td>
	          </tr>
	          <#--tr style="visibility:hidden;">
	            <td>Reference No :</td>
	            <#if loyaltyRefId?exists><td>${loyaltyRefId}</td><input type="hidden" name="loyaltyRefId" id="loyaltyRefId" value="${loyaltyRefId?if_exists}"/></#if>
	          </tr-->
	        </table>
	      <#else>
	       <table width="400px" style="font-weight:normal;">
	          <tr>
			  	<td align="center" style="padding-left:40px;"><img src="/erptheme1/dontmiss1.png" /></td>
			  </tr>
			  <tr>
			  	<td style="padding-left:4px;">We are persistent in building a beautiful community of YouMart citizens; hence invite your near & dear ones to shop with YouMart. </td>
			  </tr>
			  <tr>
			  	<td style="padding-left:4px;">You earn rewards of <b>2000 points</b> everytime you invite a new friend/family to shop with us, which will be added to your YouMart Savings balance.</td>
			  </tr>
			  <tr>
			  	<td style="padding-left:4px;">Not enough!! You also get <b>flat 5% additional discount</b> on every new reference !!</td>
			  </tr>
			  <tr>
			  	<td style="padding-left:4px;">Refer <a href="<@ofbizUrl>/faqs#inviteafriend</@ofbizUrl>" style="color:#06509B;">FAQ's</a> for details.</td>
			  </tr>
	       	 <tr>
	        	<td style="vertical-align:top; color:#ff0000 !important;"><a href="<@ofbizUrl>/inviteafriend</@ofbizUrl>" class="login123" style="color:#06509B !important;">Please login to Invite a Friend</a></td>
	        </tr>
	       </table>
	      </#if>
	    </form>
	   </div>
    </td>
	<td style=" width:38px; vertical-align:top;">
		   <!-- img src="/erptheme1/cartslide/call-close_new.png" alt="bulk order" usemap="#Map3" / -->
	</td>
    </tr>
</table>
</div>