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

<div class="screenlet">
    <div class="screenlet-title-bar" style="background:none !important;">
        <div class="Shopcar_pageHead">${uiLabelMap.CommonContactUs}</div>
    </div>
<#if userLogin?has_content>
<script type="text/javascript" language="JavaScript">

    function checkNo(number)
			{ 
			var number_len = number.length;
			if((number_len < 10 || number_len > 12))
			return false;
			return true;
			}
function addZeroPrefixContactUs(){
	var phoneNo=document.getElementById('phone');
	if(phoneNo.value.length>0){
            
	    if(checkNo(phoneNo.value)==false)
	    {
	    	alert("Your Mobile Number must be 10 to 12 digits.");
	    	phoneNo.value="";
	    	document.getElementById('phone').focus();
	    }
	    else{
			if(phoneNo.value[0] != "0"){
				document.getElementById('phone').value = "0"+document.getElementById('phone').value;
			}
		}	
	}
}
    function isNumberKeyContactUs(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
              
         return true;
      }
    function DrawCaptcha(){
       var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
		var string_length = 8;
		var randomstring = '';
		for (var i=0; i<string_length; i++) {
			var rnum = Math.floor(Math.random() * chars.length);
			randomstring += chars.substring(rnum,rnum+1);
		}
		document.getElementById("captchaCode").value = randomstring.toLowerCase();
		document.getElementById("captchaCodeImage").innerHTML = randomstring.toLowerCase();
    }
    function validateCaptchaCode(){
	var userEnteredCaptchaCode = document.getElementById("captcha").value;
	var verifyCaptchaCode = document.getElementById("captchaCode").value;
	if(userEnteredCaptchaCode == "" || userEnteredCaptchaCode == null){
		alert("Please enter captcha code.");
		return false;
	}
	if(verifyCaptchaCode != userEnteredCaptchaCode){
		alert("Captcha code is miss match. Please enter correct captcha code.");
		document.getElementById('captcha').value="";
		document.getElementById('captcha').focus();
		DrawCaptcha();
		return false;
	}else{
		return true;
	}
} 
function validateContactUsWithLogin()
    {	
		var subject = document.getElementById("subject").value ;
		//var firstName = document.getElementById("firstName").value ;
		//var emailAddress = document.getElementById("emailAddress").value ;
		//var phone = document.getElementById("phone").value ;
		var message = document.getElementById("message").value ;
		var captcha = document.getElementById("captcha").value ;
		
		//var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		
		if(subject == '' || subject ==null){
			alert("Please enter subject.");
			document.getElementById('subject').focus();
			return false;
		}
		
		if(message == '' || message ==null){
			alert("Please enter message.");
			document.getElementById('message').focus();
			return false;
		}
		if(captcha == '' || captcha ==null){
			alert("Please enter captcha code.");
			document.getElementById('captcha').value="";
			document.getElementById('captcha').focus();
			return false;
		}
		return true;
}
</script>

    <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId))?if_exists>
        <div class="screenlet-body">
        <form name="contactForm" method="post" action="<@ofbizUrl>submitAnonContactUs</@ofbizUrl>">
            <input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
            <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
            <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
            <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
            <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
            <input type="hidden" name="emailType" value="CONT_NOTI_EMAIL" />
            <input type="hidden" name="captchaCode" id="captchaCode" value=""/>
            <input type="hidden" name="partyId" value="${person.partyId?if_exists}"/>
            <input type="hidden" name="emailAddress" value="${userLogin.userLoginId?if_exists}"/>
            <input type="hidden" name="firstName" id="firstName" class="required" value="${person.firstName?if_exists}"/>
            <input type="hidden" name="lastName" id="lastName" class="required" value="${person.lastName?if_exists}"/>
            <input type="hidden" name="phone" id="phone" class="required" value="${person.mobileNumber?if_exists}" />
            <input type="hidden" value="" name="captchaCode" id="captchaCode"/>
            <table class="basic-table" cellspacing="5">
                <tbody>
                    <tr>
                       <td>${uiLabelMap.EcommerceSubject}</td>
                       <td><input type="text" name="subject" id="subject" class="required" value=""/>*</td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.CommonMessage}</td>
                       <td><textarea name="content" id="message" class="required" cols="50" rows="5"></textarea>*</td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.CommonCaptchaCode}</td>
                       <td>
                       	<span id="captchaCodeImage" style="-moz-user-select: none; -khtml-user-select: none;-webkit-user-select: none;user-select: none; border-style: none; border-color: inherit; border-width: medium; background-color:black; color:red; font-family: 'Curlz MT'; font-size: x-large; font-weight: bold; font-variant: normal; letter-spacing: 2pt; padding:3px; margin:5px 5px 5px 4px; display:inline-block; height:30px; width: auto;"></span>
						
                       	<a href="javascript:DrawCaptcha();">${uiLabelMap.CommonReloadCaptchaCode}</a>
                       </td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.CommonVerifyCaptchaCode}</td>
                       <td><input type="text" autocomplete="off" maxlength="30" size="23" name="captcha" id="captcha" onChange="return validateCaptchaCode();"/>*</td>
                    </tr>
                    <!--tr>
                       <td>${uiLabelMap.FormFieldTitle_emailAddress}</td>
                       <td>${requestParameters.emailAddress?if_exists} (${uiLabelMap.CommonEmailAlreadyExist})</td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.CommonFrom}</td>
                       <td>${person.firstName?if_exists} ${person.lastName?if_exists} (${uiLabelMap.FormFieldTitle_existingCustomer})</td>
                    </tr-->
                    <tr>
                       <td></td>
                       <td><input type="submit" value="${uiLabelMap.CommonSubmit}" onClick="return validateContactUsWithLogin();"/><!--a href="<@ofbizUrl>AnonContactus</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonCancel}</a--></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
<#else>
<script type="text/javascript" language="JavaScript">

    function checkNo(number)
			{ 
			var number_len = number.length;
			if((number_len < 10 || number_len > 12))
			return false;
			return true;
			}
			
			
function addZeroPrefixContactUs(){
	var phoneNo=document.getElementById('phone');
	if(phoneNo.value.length>0){
            
	    if(checkNo(phoneNo.value)==false)
	    {
	    	alert("Your Mobile Number must be 10 to 12 digits.");
	    	phoneNo.value="";
	    	document.getElementById('phone').focus();
	    }
	    else{
			if(phoneNo.value[0] != "0"){
				document.getElementById('phone').value = "0"+document.getElementById('phone').value;
			}
		}	
	}
}
    function isNumberKeyContactUs(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
              
         return true;
      }
    function DrawCaptcha(){
       var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
		var string_length = 8;
		var randomstring = '';
		for (var i=0; i<string_length; i++) {
			var rnum = Math.floor(Math.random() * chars.length);
			randomstring += chars.substring(rnum,rnum+1);
		}
		document.getElementById("captchaCode").value = randomstring.toLowerCase();
		document.getElementById("captchaCodeImage").innerHTML = randomstring.toLowerCase();
    }
    
    function validateContactUsWithoutLogin()
    {
		var subject = document.getElementById("subject").value ;
		var firstName = document.getElementById("firstName").value ;
		var emailAddress = document.getElementById("emailAddress").value ;
		var phone = document.getElementById("phone").value ;
		var message = document.getElementById("message").value ;
		var captcha = document.getElementById("captcha").value ;
		
		var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		
		if(subject == '' || subject ==null){
			alert("Please enter subject.");
			document.getElementById('subject').focus();
			return false;
		}
		if(emailAddress == '' || emailAddress ==null){
			alert("Please enter email address.");
			document.getElementById('emailAddress').focus();
			return false;
		}
		if(!emailPattern.test(emailAddress)){
			alert("Please enter a valid email address");
			document.getElementById('emailAddress').focus();
			return false;
		}
		if(message == '' || message ==null){
			alert("Please enter message.");
			document.getElementById('message').focus();
			return false;
		}
		if(captcha == '' || captcha ==null){
			alert("Please enter captcha code.");
			document.getElementById('captcha').focus();
			return false;
		}
		return true;
}
function validateCaptchaCode(){
	var userEnteredCaptchaCode = document.getElementById("captcha").value;
	var verifyCaptchaCode = document.getElementById("captchaCode").value;
	if(userEnteredCaptchaCode == "" || userEnteredCaptchaCode == null){
		alert("Please enter captcha code.");
		return false;
	}
	if(verifyCaptchaCode != userEnteredCaptchaCode){
		alert("Captcha code is miss match. Please enter correct captcha code.");
		document.getElementById('captcha').value="";
		DrawCaptcha();
		return false;
	}else{
		return true;
	}
} 
	window.onload=DrawCaptcha;
</script>
    <div class="screenlet-body">
        <form id="contactForm" method="post" action="<@ofbizUrl>submitAnonContactUs</@ofbizUrl>">
            <input type="hidden" name="partyIdFrom" value="${(userLogin.partyId)?if_exists}" />
            <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
            <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS" />
            <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI" />
            <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
            <input type="hidden" name="emailType" value="CONT_NOTI_EMAIL" />
            <table class="basic-table" cellspacing="5">
                <tbody>
                    <tr>
                       <td></td>
                       <td><input type="hidden" value="" name="captchaCode" id="captchaCode"/></td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.EcommerceSubject}</td>
                       <td><input type="text" name="subject" id="subject" class="required" value="${requestParameters.subject?if_exists}"/>*</td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.PartyFirstName}</td>
                       <td><input type="text" name="firstName" id="firstName" class="required" value="${requestParameters.firstName?if_exists}"/></td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.PartyLastName}</td>
                       <td><input type="text" name="lastName" id="lastName" class="required" value="${requestParameters.lastName?if_exists}"/></td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.FormFieldTitle_emailAddress}</td>
                       <td><input type="text" name="emailAddress" id="emailAddress" class="required" value="${requestParameters.emailAddress?if_exists}"/>*</td>
                    </tr>
                    <tr>
                       <td>Mobile No.</td>
                       <td><input type="text" name="phone" id="phone" class="required" value="${requestParameters.phone?if_exists}" onkeypress="return isNumberKeyContactUs(event);" onchange="addZeroPrefixContactUs();"/></td>
                    </tr>
                    <#--<tr>
                       <td class="label">${uiLabelMap.EcommerceSubject}</td>
                       <td><input type="text" name="subject" id="subject" class="required" value="${requestParameters.subject?if_exists}"/>*</td>
                    </tr>-->
                    <tr>
                       <td>${uiLabelMap.CommonMessage}</td>
                       <td><textarea name="content" id="message" class="required" maxlength="250" cols="50" rows="5" style="margin-left:4px;">${requestParameters.content?if_exists}</textarea>*</td>
                    </tr>
                    
                    
                    
                   <#-- <tr>
                       <td class="label">${uiLabelMap.CommonCaptchaCode}</td>
                       <td><div id="captchaImage"><img src="${parameters.captchaFileName}" alt="" /></div><a href="javascript:reloadCaptcha();">${uiLabelMap.CommonReloadCaptchaCode}</a></td>
                    </tr>-->
                    <tr>
                       <td>${uiLabelMap.CommonCaptchaCode}</td>
                       <td>
                       	<span id="captchaCodeImage" style="-moz-user-select: none; -khtml-user-select: none;-webkit-user-select: none;user-select: none; border-style: none; border-color: inherit; border-width: medium; background-color:black; color:red; font-family: 'Curlz MT'; font-size: x-large; font-weight: bold; font-variant: normal; letter-spacing: 2pt; padding:3px; margin:5px 5px 5px 4px; display:inline-block; height:30px; width: auto;"></span>
						
                       	<a href="javascript:DrawCaptcha();">${uiLabelMap.CommonReloadCaptchaCode}</a>
                       </td>
                    </tr>
                    <tr>
                       <td>${uiLabelMap.CommonVerifyCaptchaCode}</td>
                       <td><input type="text" autocomplete="off" maxlength="30" size="23" name="captcha" id="captcha" onChange = "return validateCaptchaCode();"/>*</td>
                    </tr>
                    <tr>
                       <td></td>
                       <td><input type="submit" value="${uiLabelMap.CommonSubmit}" onClick="return validateContactUsWithoutLogin();" /></td>
                    </tr>
                </tbody>
            </table>
        </form>
    </div>
</#if>
</div>