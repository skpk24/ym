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
<#--<script type="text/javascript" src="/images/geoAutoPaymentCompleter.js"></script>-->
<#if getUsername>

<script type="text/javascript">

window.onload = function() {
	// getAssociatedStateList2('countryGeoId', 'stateProvinceGeoId');
	 //changeState1();
}
function getCountryTeleCode(countryGeoId, stateId) {
    var optionList = [];
    new Ajax.Request("getCountryTeleCode", {
       	asynchronous: false,
        parameters: {countryGeoId:document.getElementById("countryGeoId").value,stateProvinceGeoId1:document.getElementById("stateProvinceGeoId").value},
        onSuccess: function(transport) {
        	
           	var data = transport.responseText;
            document.getElementById("CUSTOMER_MOBILE_COUNTRY").value = data;
            //changeState1(data.length);
        }
       
    }); 
    //changeState1();
}

//Generic function for fetching country's associated state list.
function getAssociatedStateList2(countryId, stateId) {
    var optionList = [];
    new Ajax.Request("getAssociatedStateList", {
        asynchronous: false,
        parameters: {countryGeoId:document.getElementById("countryGeoId").value,stateProvinceGeoId1:document.getElementById("stateProvinceGeoId").value},
        onSuccess: function(transport) {
        	
            var data = transport.responseText;
            
            document.getElementById("statesdisplay").innerHTML = data;
            changeState1(data.length);
        }
       
    }); 
    //changeState1();
}
function changeState(){
	
	document.getElementById("hiddenState").value = document.getElementById("stateProvinceGeoId").value;
}

function changeState1(length){
	if(length == "231"){
	document.getElementById("hiddenState").value = "";
	$('#DIV_CUSTOMER_STATE').show();
	$('#statesdisplay').hide();   
	}else{
		document.getElementById("stateProvinceGeoId").value = document.getElementById("hiddenState").value;
		$('#DIV_CUSTOMER_STATE').hide();
		$('#statesdisplay').show();
	}
}



  //<![CDATA[
     lastFocusedName = null;
     function setLastFocused(formElement) {
         lastFocusedName = formElement.name;
     }
     function clickUsername() {
         if (document.getElementById('UNUSEEMAIL').checked) {
             if (lastFocusedName == "UNUSEEMAIL") {
                 jQuery('#PASSWORD').focus();
             } else if (lastFocusedName == "PASSWORD") {
                 jQuery('#UNUSEEMAIL').focus();
             } else {
                 jQuery('#PASSWORD').focus();
             }
         }
     }
     function changeEmail() {
        if (document.getElementById('UNUSEEMAIL').checked) {
        
             document.getElementById('USERNAME').value = jQuery('#CUSTOMER_EMAIL').val();
         }
     }
     function setEmailUsername() {
    
         
             document.getElementById('USERNAME').value = jQuery('#CUSTOMER_EMAIL').val();
             // don't disable, make the browser not submit the field: document.getElementById('USERNAME').disabled=true;
        
     }
     function hideShowUsaStates() {
         if (document.getElementById('customerCountry').value == "USA" || document.getElementById('customerCountry').value == "UMI") {
             document.getElementById('customerState').style.display = "block";
         } else {
             document.getElementById('customerState').style.display = "none";
         }
     }
   //]]>
</script>

<script type="text/javascript">
function checkEmail(eMail) {
            if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(eMail)){
            return true;
            }
            return false; 
          }
function checkPostalCode(postalCode) {
            if (/^\d{3}\s?\d{3}$/.test(postalCode)){
            return true;
            }
            return false; 
          }          
          function checkFName(str){
              var re = /[^a-zA-Z]/g
              if (re.test(str))
                  return false;
              return true;
            }
            function checkLName(str){
              var re = /[^a-zA-Z]/g
              if (re.test(str))
                  return false;
              return true;
            }
            
            function checkNo(number)
			{ 
			var number_len = number.length;
			if((number_len < 10 || number_len > 12))
			return false;
			return true;
			}
			
			function checkPwd(passid)
			{ 
			var passid_len = passid.length;
			if((passid_len == 0 ||passid_len >= 20 || passid_len < 5))
			return false;
			return true;
			}
			function confirmPwd(pwd,cnfPwd)
			{ 
			if (pwd != cnfPwd)
			return false;
			return true;
			}
			function validateDate(date1)
 			{
			dates=date1.trim();
			var validformat=/^\d{2}\-\d{2}\-\d{4}$/ ;
			var returnval=false;
			
			
				var dt1Parts = dates.split('-');
				var dt1dd = parseInt(dt1Parts[0]);
        		var	dt1mm = parseInt(dt1Parts[1]);
        		var	dt1yyyy = parseInt(dt1Parts[2]);
        		
        		var dt1dd1 = dt1dd.toString();
        		var dt1mm1 = dt1mm.toString();
        		var dt1yyyyy1 = dt1yyyy.toString();
        		
        		if(dt1dd1.length==1){
        			 dt1dd1 =  '0'+ dt1dd1;
        		}
        		if(dt1mm1.length==1){
        			 dt1mm1 =  '0'+ dt1mm1;
        		}
        		var dt = dt1yyyyy1 + '-'+dt1mm1+'-'+dt1dd1;
			
			if(!validformat.test(dates))
			{
			return false;
			}else{
				var tt = dt;
				var stDate = new Date(tt);
				var enDate = new Date();
				
				var compDate = enDate - stDate;
				if(compDate >= 0){
					document.getElementById('USER_BIRTHDATE').value = tt;
					return true;
				}	
				else
				{
				return false;
				} 
			}
		}
			

function formValidation(){
					fName = document.getElementById('USER_FIRST_NAME');
					// lName=document.getElementById('USER_LAST_NAME');
					// bDate=document.getElementById('USER_BIRTHDATE1');
					phoneNo=document.getElementById('cust_mobile');
					emailID=document.getElementById('CUSTOMER_EMAIL');
					uName=document.getElementById('USERNAME');
					pwd=document.getElementById('PASSWORD');
					cfmpwd=document.getElementById('CONFIRM_PASSWORD');
				<#--	add=document.getElementById('CUSTOMER_ADDRESS1');
					add1=document.getElementById('CUSTOMER_ADDRESS2');
					area=document.getElementById('AREA');
					landm=document.getElementById('CUSTOMER_LANDMARK');
					
 
  
					city=document.getElementById('CUSTOMER_CITY');
					postalCode=document.getElementById('CUSTOMER_POSTAL_CODE');
					state=document.getElementById('stateProvinceGeoId');
					country=document.getElementById('countryGeoId').value;-->
					
					
						if((fName.value==null)||(fName.value=="")){
			            alert("Please Enter Your Name!")
			            setTimeout("fName.focus()", 50);
			            return false;
			            }
			          /**  if(checkFName(fName.value)==false){
			            fName.value="";
			            alert("Invalid Name!");
			            setTimeout("fName.focus()");
			            return false;
			            } **/
			            
			          /** if((lName.value==null)||(lName.value=="")){
			            alert("Please Enter Your Last Name!")
			            setTimeout("lName.focus()", 50);
			            return false;
			            }
			            if(checkLName(lName.value)==false){
			            lName.value="";
			            alert("Invalid Last Name!");
			            setTimeout("lName.focus()");
			            return false;
			            } 
			            
			            if((bDate.value==null)||(bDate.value=="")){
			            alert("Please Enter Your B'Day Date!")
			            setTimeout("bDate.focus()", 50);
			            return false;
			            }
			            if(validateDate(bDate.value)==false){
			            bDate.value="";
			            alert("Invalid Date!");
			            setTimeout("bDate.focus()");
			            return false;
			            } **/
			            
			            if((phoneNo.value==null)||(phoneNo.value=="")){
			            alert("Please Enter your Mobile No.!")
			            phoneNo.focus();
			            return false;
			            }
			            if (checkNo(phoneNo.value)==false){
			            alert("Your Mobile Number must be 10 digits.");
			            phoneNo.focus();
						return false;
			            }
			            
			            if((emailID.value==null)||(emailID.value=="")){
			            alert("Please Enter your Email ID!")
			            emailID.focus();
			            return false;
			            }
			            if (checkEmail(emailID.value)==false){
			            emailID.value=""
			            alert("Invalid Email Adderess!");
			            emailID.focus();
			            return false;
			            }
			            
			            if((uName.value==null)||(uName.value=="")){
			            alert("Please Enter your Username!")
			            uName.focus();
			            return false;
			            }
			            
			            if((pwd.value==null)||(pwd.value=="")){
			            alert("Please Enter Your Password!")
			            setTimeout("pwd.focus()", 50);
			            return false;
			            }
			            if (checkPwd(pwd.value)==false){
			            alert("Password must have more than 5 character!");
			            pwd.focus();
			            return false;
			            }
			            
			            if((cfmpwd.value==null)||(cfmpwd.value=="")){
			            alert("Please Enter Your Confirm Password!")
			            setTimeout("cfmpwd.focus()", 50);
			            return false;
			            }
			            if (confirmPwd(pwd.value,cfmpwd.value)==false){
			            alert("Your password and confirmation password do not match!");
			            cfmpwd.focus();
			            return false;
			            }
			            
			              if(document.getElementById('termsAndConditions').checked){
			         
			            }else{
			               alert("Please accept terms and conditions.");
			            return false;
			            }
			            
			            return validateRefId();
			     <#--       if ((add.value==null)||(add.value=="")){
			            alert("Please Enter your house no / flat no!")
			            add.focus();
			            return false;
			            }
			            if ((add1.value==null)||(add1.value=="")){
			            alert("Please Enter your street name / building name!")
			            add1.focus();
			            return false;
			            }
			            if ((area.value==null)||(area.value=="")){
			            alert("Please Enter your area!")
			            area.focus();
			            return false;
			            }
			            if ((landm.value==null)||(landm.value=="")){
			           	alert("Please Enter your landmark!")
			           	landm.focus();
			           	return false;
			           }
			            
			            if((city.value==null)||(city.value=="")){
			            alert("Please Enter Your City!")
			            setTimeout("city.focus()", 50);
			            return false;
			            }
			            
			            if((postalCode.value==null)||(postalCode.value=="")){
			            alert("Please Enter your ZIP code!")
			            postalCode.focus();
			            return false;
			            } 
			            
			          <#--  else if(checkPostalCode(postalCode.value) == false){
			            	alert("Please Enter valid ZIP code!")
			            		postalCode.focus();
			            		return false;
			            }
			            
			            else{
			            Globalflag = true;
			            checkCode(postalCode.value);
			             if(!Globalflag){
			            alert("Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.");
			            return false;
			            }
			            } -->
			            
			           
			            
			       		 <#--if(((state.value !=null)||(state.value !="")) && ((country !=null)||(country !=""))){
			            
			            // document.getElementById('stateProvinceGeoId').value="IN-KA";
			             //document.getElementById('countryGeoId').value="IND";
			             return true;
			            }-->
     }  
      var Globalflag = true;
   function checkCode(value){
	var url="/control/checkPostalCode?locationSearch="+value;
    jQuery.ajax({url: url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
       
        if(data != "success"){
	        Globalflag = false;
        } 
 	   return true;
	  },
        error: function(data) {
          alert("Oops! something went wrong");
            return false; 
        }
    });  
			return true;
	}
      function acceptTandC(){
     	 if(document.getElementById('termsAndConditions').checked){
     	 	document.getElementById('${uiLabelMap.CommonSave}').disabled=false;
     	 	return true;
     	 }else{
     	 	
     	 	alert("Please accept terms and conditions.");
     	 	document.getElementById('termsAndConditions').checked = false;
     	 	return true;
     	 }
     }         	
</script>
<script language="JavaScript">

function check_mobile_num_without0(){
	var phoneNo=document.getElementById('cust_mobile');
	if(phoneNo.value[0] =="0"){
		document.getElementById('cust_mobile').value = "";
	}
}

function onlyNumbers(evt)
{
var charCode = (evt.which) ? evt.which : event.keyCode
var phoneNo=document.getElementById('cust_mobile').value.length;
         if (charCode > 31 && (charCode < 48 || charCode > 57 || phoneNo>9))
            return false;

         return true;
}

function validateRefId()
 		{
 		var flag = false;
	   var refId=document.getElementById('FRIEND_REFERENCE_ID').value;
	   var mailId=document.getElementById('CUSTOMER_EMAIL').value;
	   
	   if(refId != null && refId != "")
	   {
		   if(mailId == null || mailId == "")
		   		return flag;
	   }else{
	   		return true;
	   }
	   if(refId != ""){
		    var  param = 'refId=' + refId + '&mailId=' + mailId;
	                      jQuery.ajax({url: "/control/checkrefid",
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	         if(data == "success")
	         {
	         	flag = true;
	         }
	         else if(data == "invalidMailId"){
	         	alert("This Reference Id is not related to the email id");
	         }else if(data == "invalidRefId"){
	         	alert("Invalid Reference Id");
	         }else if(data == "alreadyUsed"){
	         	alert("Reference Id already used");
	         }
	         else{
	         	alert("Wrong Reference Id");
	         	document.forms['newuserform'].FRIEND_REFERENCE_ID.value="";
	         }}
    	});
    	return flag;
    }
    else{return true;}
   
}
function validateDob(){

	bDate=document.getElementById('USER_BIRTHDATE1');
	if((bDate.value==null)||(bDate.value=="")){
            alert("Please Enter Your B'Day Date!")
            setTimeout("bDate.focus()", 50);
            return false;
          }
          if(validateDate(bDate.value)==false){
            bDate.value="";
            alert("Invalid Date!");
            setTimeout("bDate.focus()");
            return false;
          }
			            
	
}
function validateMobileNo(){
	var phoneNo=document.getElementById('cust_mobile');
	if((phoneNo.value==null)||(phoneNo.value=="")){
            alert("Please Enter your Mobile No.!")
            setTimeout("document.getElementById('cust_mobile').focus()");
            return false;
       }
    else if(checkNo(phoneNo.value)==false){
    alert("Your Mobile Number must be 10 digits.");
    phoneNo.value="";
    document.getElementById('cust_mobile').focus();
     return false;
    }
    else{
	    	if(phoneNo.value[0] !="0"){
	    	
				document.getElementById('cust_mobile').value = "0"+document.getElementById('cust_mobile').value;
			}
			
	var url="/control/checkMobileNo?mobileNo="+document.getElementById('cust_mobile').value;
    jQuery.ajax({url: url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
          if(data == 'SY' ){
	          //usernameObj.value = "";
	          //jQuery('#errorMsg8').html("Mobile number is already in use!");
	           alert("Mobile number is already in use!");
	           document.getElementById('cust_mobile').value="";
	           document.getElementById('cust_mobile').focus();
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
			return true;
	}
	
	
	
}
</script>

</#if>
<#------------------------------------------------------------------------------
NOTE: all page headings should start with an h2 tag, not an H1 tag, as 
there should generally always only be one h1 tag on the page and that 
will generally always be reserved for the logo at the top of the page.
------------------------------------------------------------------------------->

<h2><br>
  <span>
   Create a New Account. You already have an account, <a href='<@ofbizUrl>checkLogin/home</@ofbizUrl>' style="font-weight:bold;" class="linktext1233">Login here</a>
  </span>
</h2>

<#macro fieldErrors fieldName>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName, true, errorMessageList)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage"><span style="color:#ff0000 !important;">${errorMsg}</span></li>
      </#list>
    </ul>
  </#if>
</#macro>
<#macro fieldErrorsMulti fieldName1 fieldName2 fieldName3 fieldName4>
  <#if errorMessageList?has_content>
    <#assign fieldMessages = Static["org.ofbiz.base.util.MessageString"].getMessagesForField(fieldName1, fieldName2, fieldName3, fieldName4, true, errorMessageList)>
    <ul>
      <#list fieldMessages as errorMsg>
        <li class="errorMessage"><span style="color:#ff0000 !important;">${errorMsg}</span></li>
      </#list>
    </ul>
  </#if>
</#macro>

<#-- <form method="post" action="<@ofbizUrl>createcustomer${previousParams}</@ofbizUrl>" id="newuserform"> -->
	<form method="post" action="<@ofbizUrl>createcustomer${previousParams}</@ofbizUrl>" id="newuserform" name="newuserform" onSubmit="return formValidation();">
  
  <#----------------------------------------------------------------------
  If you need to include a brief explanation of the form, or certain 
  elements in the form (such as explaining asterisks denote REQUIRED),
  then you should use a <p></p> tag with a class name of "desc"
  ----------------------------------------------------------------------->

  <p class="desc">Fields marked in &#40;&#42;&#41; are mandatory</p>

  <#----------------------------------------------------------------------
  There are two types of fieldsets, regular (full width) fielsets, and
  column (half width) fieldsets. If you want to group two sets of inputs
  side by side in two columns, give each fieldset a class name of "col"
  ----------------------------------------------------------------------->

  <div style="border:1px solid #cccccc;">
    <div class="boxac outside-screenlet-header">Address</div>
    <input type="hidden" name="emailProductStoreId" value="${productStoreId}"/>
    <#----------------------------------------------------------------------
    Each input row should be enclosed in a <div></div>. 
    This will ensure than each input field clears the one
    above it. Alternately, if you want several inputs to float next to
    each other, you can enclose them in a table as illustrated below for
    the phone numbers, or you can enclose each label/input pair in a span

    Example:
    <div>
      <span>
        <input type="text" name="expMonth" value=""/>
        <label for="expMonth">Exp. Month</label>
      </span>
      <span>
        <input type="text" name="expYear" value=""/>
        <label for="expYear">Exp. Year</label>
      </span>
    </div>
    ----------------------------------------------------------------------->
    <#--<div class="register">
      <label for="USER_TITLE">${uiLabelMap.CommonTitle}</label>
      <@fieldErrors fieldName="USER_TITLE"/>
      <select name="USER_TITLE" id="USER_TITLE">
        <#if requestParameters.USER_TITLE?has_content >
          <option>${requestParameters.USER_TITLE}</option>
          <option value="${requestParameters.USER_TITLE}"> -- </option>
        <#else>
          <option value="">${uiLabelMap.CommonSelectOne}</option>
        </#if>
        <option>${uiLabelMap.CommonTitleMr}</option>
        <option>${uiLabelMap.CommonTitleMrs}</option>
        <option>${uiLabelMap.CommonTitleMs}</option>
        <option>${uiLabelMap.CommonTitleDr}</option>
      </select>
    </div>-->
	 
    <div class="register">
      <label for="USER_FIRST_NAME">Name*</label>
      <#--@fieldErrors fieldName="USER_FIRST_NAME"/-->
      <input type="text" name="USER_FIRST_NAME" id="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME?if_exists}"/></td>
      <#--input type="text" name="USER_FIRST_NAME" id="USER_FIRST_NAME" value="${requestParameters.USER_FIRST_NAME?if_exists}" /-->
    </div>
    
    <div class="register">
      <label for="USER_GENDER">Gender *</label>
       <input type="radio"  value="M" name="USER_GENDER" style="width:20px;" checked='checked'> <span style="margin-right:35px;">Male</span>
       <input type="radio"  value="F" name="USER_GENDER" style="width:20px;"> Female
    </div>

    <#--<div class="register">
      <label for="USER_MIDDLE_NAME">${uiLabelMap.PartyMiddleInitial}</label>
      <#--@fieldErrors fieldName="USER_MIDDLE_NAME"/-->
     <#-- <input type="text" name="USER_MIDDLE_NAME" id="USER_MIDDLE_NAME" value="${requestParameters.USER_MIDDLE_NAME?if_exists}" />
    </div>>

    <#-- div class="register">
      <label for="USER_LAST_NAME">${uiLabelMap.PartyLastName}*</label>
      <#--@fieldErrors fieldName="USER_LAST_NAME"/-->
      <#-- input type="text" name="USER_LAST_NAME" id="USER_LAST_NAME" value="${requestParameters.USER_LAST_NAME?if_exists}" />
    </div -->

    <#--div class="register">
      <label for="USER_SUFFIX">${uiLabelMap.PartySuffix}</label>
      <@fieldErrors fieldName="USER_SUFFIX"--/>
      <input type="text" class='inputBox' name="USER_SUFFIX" id="USER_SUFFIX" value="${requestParameters.USER_SUFFIX?if_exists}" />
    </div-->
	<#-- input type="hidden" name="USER_BIRTHDATE" id="USER_BIRTHDATE" value="" />
    <div class="register">
      <label for="USER_BIRTHDATE">Birth Day(DD-MM-YYYY)*</label>
      <#--@fieldErrors fieldName="USER_BIRTHDATE"/-->
      <#-- input type="text" name="USER_BIRTHDATE1" id="USER_BIRTHDATE1"   />
     
						      <script type="text/javascript">
						     
					            jQuery("#USER_BIRTHDATE1").datepicker({
					            
					              maxDate:0,
					            	  changeMonth: true,
										changeYear: true,
										yearRange : 'c-80:c',
					                showOn: 'button',
					                buttonImage: '',
					                buttonText: '',
					                showOn: "both",
					                buttonImageOnly: true,
					                dateFormat: 'dd-mm-yy'
					                
					              });
					              
					             
					           </script>
   
   
    </div -->
   
    <#-- div class="register">
      <label for="OWNER_RENTED">Own House/Rented</label>
       <input type="radio"  value="OwnHouse" name="USER_OWNER_RENTED" style="width:20px;"> Own House
       <input type="radio"  value="Rented" name="USER_OWNER_RENTED" style="width:20px;"> Rented
    </div>
    <div class="register">
        <label for="CITY_OF_ORIGIN">City of Origin</label>
      <#--<@fieldErrors fieldName="USER_SUFFIX">-->
      <#-- input type="text" class='inputBox' name="USER_SUFFIX" id="USER_SUFFIX" value="${requestParameters.USER_SUFFIX?if_exists}" />
    </div -->
    
    <div class="register">
      <label for="MOBILE_NUMBER">Mobile Number* &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp; &nbsp;  </label>
      <input type="text" name="CUSTOMER_MOBILE_CONTACT" id="cust_mobile"  autocomplete="off" value="${requestParameters.CUSTOMER_MOBILE_CONTACT?if_exists}" onkeyup="check_mobile_num_without0()"  onkeypress="return onlyNumbers(event);" onChange="validateMobileNo()" />
      Note:  Enter 10 Digit Mobile Number. 
      <span id="errorMsg8" style="color: red;"></span>
      
      <div id="userNameErr" style="float:right;color:red; display:none;"></div>
    </div>
    
    
    <#-- <div>
      <label for="MOBILE_NUMBER">Mobile Number*</label>
     <#-- <input type="text" id="cust_mobile"  onkeypress="return checkIt(event);" name="CUSTOMER_MOBILE_CONTACT" value="${requestParameters.CUSTOMER_MOBILE_CONTACT?if_exists}"   onchange="checklength();"/>
      <input type="text" id="cust_mobile" class='inputBox' name="CUSTOMER_MOBILE_CONTACT" value="${requestParameters.CUSTOMER_MOBILE_CONTACT?if_exists}" size="25" maxlength="10"  onKeyPress="return checkIt(event);"> <span class="errorMessage"> *</span>
    </div> -->
    
    
    <#if requestParameters.refId?exists><div>
    <input type="hidden" name="referByLoginId" id="referByLoginId" style="width:203px;" value="${requestParameters.refId}"/>
    </div></#if>
    <#if requestParameters.uniqrefId?exists><div>
    <input type="hidden" name="uniqueRefLoginId" id="uniqueRefLoginId" style="width:203px;" value="${requestParameters.uniqrefId}"/>
    </div></#if>
    
    
    <div class="register">
      <label for= "CUSTOMER_EMAIL">${uiLabelMap.PartyEmailAddress}*</label>
      <#--@fieldErrors fieldName="CUSTOMER_EMAIL"/-->
      <input type="text" name="CUSTOMER_EMAIL" id="CUSTOMER_EMAIL" value="${requestParameters.CUSTOMER_EMAIL?if_exists}"  onchange="changeEmail()" onkeyup="changeEmail()" />
    </div>
    <#--div class="register">
      <label for="CUSTOMER_EMAIL_ALLOW_SOL">${uiLabelMap.PartyAllowSolicitation}</label>
      <select name="CUSTOMER_EMAIL_ALLOW_SOL" id="CUSTOMER_EMAIL_ALLOW_SOL">
        <#if (((requestParameters.CUSTOMER_EMAIL_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
        <#if (((requestParameters.CUSTOMER_EMAIL_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
        <option></option>
        <option value="Y">${uiLabelMap.CommonY}</option>
        <option value="N">${uiLabelMap.CommonN}</option>
      </select>
    </div-->
    
    <#if getUsername>
      <#--<@fieldErrors fieldName="USERNAME"/>-->
      <div class="form-row inline">
        <label for="UNUSEEMAIL">
          <input type="hidden" class="checkbox" name="UNUSEEMAIL" id="UNUSEEMAIL" value="on" checked="checked" onclick="setEmailUsername();" onfocus="setLastFocused(this);"/> 
        </label>
      </div>

      <div class="register">
       <#-- <label for="USERNAME">${uiLabelMap.CommonUsername}Email Id*</label>-->
        <input type="hidden" name="USERNAME" id="USERNAME" value="${requestParameters.USERNAME?if_exists}" onfocus="clickUsername();" onchange="changeEmail();"/>
      </div>
    </#if>
    <#if createAllowPassword>
      <div class="register">
        <label for="PASSWORD">${uiLabelMap.CommonPassword}*</label>
        <#--@fieldErrors fieldName="PASSWORD"/-->
        <input type="password" name="PASSWORD" id="PASSWORD" onchange="setEmailUsername();" onfocus="setLastFocused(this);"/>
      </div>

      <div class="register">
        <label for="CONFIRM_PASSWORD">${uiLabelMap.PartyRepeatPassword}*</label>
        <#--@fieldErrors fieldName="CONFIRM_PASSWORD"/-->
        <input type="password" class='inputBox' name="CONFIRM_PASSWORD" id="CONFIRM_PASSWORD" value="" maxlength="50"/>
      </div>

      <#--<div class="register">
        <label for="PASSWORD_HINT">${uiLabelMap.PartyPasswordHint}</label>
        <#--@fieldErrors fieldName="PASSWORD_HINT"/-->
        <#--<input type="text" class='inputBox' name="PASSWORD_HINT" id="PASSWORD_HINT" value="${requestParameters.PASSWORD_HINT?if_exists}" maxlength="100"/>
      </div>-->
    <#else/>
      <div>
        <label>${uiLabelMap.PartyReceivePasswordByEmail}.</div>
      </div>
    </#if>
    
    <div class="register">
      <label for="REFERENCE_ID">Reference Id</label>
      <input type="text" name="FRIEND_REFERENCE_ID" id="FRIEND_REFERENCE_ID" value="${requestParameters.uniqrefId?if_exists}"/>Note: Only for referred friend.
    </div>
  </div>
  <input type="hidden" name="USE_ADDRESS" id="USE_ADDRESS" value="false"/>
  
 <#--div style="border:1px solid #cccccc; margin-top:10px;">
   <div class="boxac outside-screenlet-header">${uiLabelMap.PartyShippingAddress}</div>

    div class="register">
      <label for="CUSTOMER_ADDRESS1">House No / Flat No*</label>
      <#--@fieldErrors fieldName="CUSTOMER_ADDRESS1"/>
      <input type="text" name="CUSTOMER_ADDRESS1" id="CUSTOMER_ADDRESS1" value="${requestParameters.CUSTOMER_ADDRESS1?if_exists}" />
    </div>

    <div class="register">
      <label for="CUSTOMER_ADDRESS2">Street Name / Building Name*</label>
      <@fieldErrors fieldName="CUSTOMER_ADDRESS2"/>
      <input type="text" name="CUSTOMER_ADDRESS2" id="CUSTOMER_ADDRESS2" value="${requestParameters.CUSTOMER_ADDRESS2?if_exists}"/>
    </div>
    
     <#--<div class="register">
      <label for="HOUSE_FLAT_NO">House No / Flat No</label>
      <#--<@fieldErrors fieldName="HOUSE_FLAT_NO"/>-->
      <#--<input type="text" name="HOUSE_FLAT_NO" id="HOUSE_FLAT_NO" value="${requestParameters.HOUSE_FLAT_NO?if_exists}"/>
    </div>-->
    
     <#--<div class="register">
      <label for="STREET_BUILDING_NAME">Street Name / Building Name</label>
      <#--<@fieldErrors fieldName="STREET_BUILDING_NAME"/>-->
      <#--<input type="text" name="STREET_BUILDING_NAME" id="STREET_BUILDING_NAME" value="${requestParameters.STREET_BUILDING_NAME?if_exists}"/>
      
    </div>>
     <div class="register">
      <label for="AREA">Area*</label>
      <#--<@fieldErrors fieldName="AREA"/>>
      <input type="text" name="AREA" id="AREA" value="${requestParameters.AREA?if_exists}"/>
    </div>
    
	 <div class="register">
      <label for="CUSTOMER_LANDMARK">Landmark*</label>
      <#--<@fieldErrors fieldName="CUSTOMER_LANDMARK"/>
      <input type="text" name="CUSTOMER_LANDMARK" id="CUSTOMER_LANDMARK" value="${requestParameters.CUSTOMER_LANDMARK?if_exists}"/>
    </div>

    <div class="register">
      <label for="CUSTOMER_CITY">${uiLabelMap.PartyCity}*</label>
      <#--@fieldErrors fieldName="CUSTOMER_CITY"/>
      <input type="text" name="CUSTOMER_CITY" id="CUSTOMER_CITY" value="Bangalore"/>
    </div>

    <div class="register">
      <label for="CUSTOMER_POSTAL_CODE">${uiLabelMap.PartyZipCode}*</label>
      <#--@fieldErrors fieldName="CUSTOMER_POSTAL_CODE"/>
      <input type="text" name="CUSTOMER_POSTAL_CODE" id="CUSTOMER_POSTAL_CODE" maxlength = "6" onpaste = "return false;" value="${requestParameters.CUSTOMER_POSTAL_CODE?if_exists}" onkeypress="return onlyNumbers(event);"/>
    </div>

    <div class="register">
      <label for="customerCountry">${uiLabelMap.PartyCountry}*</label>
      <@fieldErrors fieldName="CUSTOMER_COUNTRY"/>
      <select name="CUSTOMER_COUNTRY" class="popup-form-textbox" id="countryGeoId" onchange="getAssociatedStateList('countryGeoId', 'stateProvinceGeoId');">
		        <#if (parameters.CUSTOMER_COUNTRY)?exists>
				<option>${parameters.CUSTOMER_COUNTRY}</option>
				<option value="${parameters.CUSTOMER_COUNTRY}">---</option>
				
				<#else>
					<option value="IND">India</option>
		        </#if>
		        ${screens.render("component://common/widget/CommonScreens.xml#countries")}
	 </select>
	 <select name="CUSTOMER_COUNTRY" class="popup-form-textbox" id="countryGeoId">
				<option value="IND">India</option>
	 </select>
	  <#--input type="text" name="CUSTOMER_COUNTRY" id="countryGeoId" value="India" readonly="true"/>
    </div>
    
    <div class="register">
      <label for="customerState">${uiLabelMap.PartyState}*</label>
      <#--@fieldErrors fieldName="CUSTOMER_STATE"/>
      <div id="statesdisplay" >
		        <select class="popup-form-textbox" name="CUSTOMER_STATE" id="stateProvinceGeoId">
					<#if (parameters.CUSTOMER_STATE)?exists>
					<option>${parameters.CUSTOMER_STATE}</option>
					<#if (parameters.CUSTOMER_STATE).equalsIgnoreCase("No States&#47;Provinces ")>
					<option value="${parameters.CUSTOMER_STATE}">---</option>
					</#if>
					<#else>
					<option value="">${uiLabelMap.PartyNoState}</option>
					</#if>
					${screens.render("component://common/widget/CommonScreens.xml#states")}
			    </select>
			    <#--<select name="CUSTOMER_STATE1" class="popup-form-textbox" id="stateProvinceGeoId">
					<option value="IN-KA">Karnataka</option>
	 			</select>
				</div>
				<input type="hidden" name="CUSTOMER_STATE" id="hiddenState" style="width:203px;" value="IN-KA"/>>
				
				
				</div>
    </div>
     <input type="hidden" name="CUSTOMER_STATE" id="stateProvinceGeo" value="" >

   <#-- <div class="register">
      <label for="customerState">${uiLabelMap.PartyState}*</label>
      <@fieldErrors fieldName="CUSTOMER_STATE"/>
      <select name="CUSTOMER_STATE" id="customerState">
        <#if requestParameters.CUSTOMER_STATE?exists>
          <option value='${requestParameters.CUSTOMER_STATE}'>${selectedStateName?default(requestParameters.CUSTOMER_STATE)}</option>
        </#if>
        <option value="">${uiLabelMap.PartyNoState}</option>
        ${screens.render("component://common/widget/CommonScreens.xml#states")}
      </select>
    </div>-->

    <#--div class="register">
      <label for="CUSTOMER_ADDRESS_ALLOW_SOL">${uiLabelMap.PartyAllowAddressSolicitation}</label>
      <select name="CUSTOMER_ADDRESS_ALLOW_SOL" id="CUSTOMER_ADDRESS_ALLOW_SOL">
        <#if (((requestParameters.CUSTOMER_ADDRESS_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
        <#if (((requestParameters.CUSTOMER_ADDRESS_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
        <option></option>
        <option value="Y">${uiLabelMap.CommonY}</option>
        <option value="N">${uiLabelMap.CommonN}</option>
      </select>
    </div>

  </div-->

  <#--<fieldset>
    <legend>${uiLabelMap.PartyPhoneNumbers}</legend>
    <table summary="Tabular form for entering multiple telecom numbers for different purposes. Each row allows user to enter telecom number for a purpose">
      <thead>
        <tr>
          <th></th>
          <th scope="col">${uiLabelMap.PartyCountry}</th>
          <th scope="col">${uiLabelMap.PartyAreaCode}</th>
          <th scope="col">${uiLabelMap.PartyContactNumber}</th>
          <th scope="col">${uiLabelMap.PartyExtension}</th>
          <th scope="col">${uiLabelMap.PartyAllowSolicitation}</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <th scope="row">${uiLabelMap.PartyHomePhone}</th>
          <td><input type="text" name="CUSTOMER_HOME_COUNTRY" size="5" value="${requestParameters.CUSTOMER_HOME_COUNTRY?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_HOME_AREA" size="5" value="${requestParameters.CUSTOMER_HOME_AREA?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_HOME_CONTACT" value="${requestParameters.CUSTOMER_HOME_CONTACT?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_HOME_EXT" size="6" value="${requestParameters.CUSTOMER_HOME_EXT?if_exists}"/></td>
          <td>
            <select name="CUSTOMER_HOME_ALLOW_SOL">
              <#if (((requestParameters.CUSTOMER_HOME_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
              <#if (((requestParameters.CUSTOMER_HOME_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
              <option></option>
              <option value="Y">${uiLabelMap.CommonY}</option>
              <option value="N">${uiLabelMap.CommonN}</option>
            </select>
          </td>
        </tr>
        <tr>
          <th scope="row">${uiLabelMap.PartyBusinessPhone}</th>
          <td><input type="text" name="CUSTOMER_WORK_COUNTRY" size="5" value="${requestParameters.CUSTOMER_WORK_COUNTRY?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_WORK_AREA" size="5" value="${requestParameters.CUSTOMER_WORK_AREA?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_WORK_CONTACT" value="${requestParameters.CUSTOMER_WORK_CONTACT?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_WORK_EXT" size="6" value="${requestParameters.CUSTOMER_WORK_EXT?if_exists}" /></td>
          <td>
            <select name="CUSTOMER_WORK_ALLOW_SOL">
              <#if (((requestParameters.CUSTOMER_WORK_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
              <#if (((requestParameters.CUSTOMER_WORK_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
              <option></option>
              <option value="Y">${uiLabelMap.CommonY}</option>
              <option value="N">${uiLabelMap.CommonN}</option>
            </select>
          </td>
        </tr>
        <tr>
          <th scope="row">${uiLabelMap.PartyFaxNumber}</th>
          <td><input type="text" name="CUSTOMER_FAX_COUNTRY" size="5" value="${requestParameters.CUSTOMER_FAX_COUNTRY?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_FAX_AREA" size="5" value="${requestParameters.CUSTOMER_FAX_AREA?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_FAX_CONTACT" value="${requestParameters.CUSTOMER_FAX_CONTACT?if_exists}" /></td>
          <td></td>
          <td>
            <select name="CUSTOMER_FAX_ALLOW_SOL">
              <#if (((requestParameters.CUSTOMER_FAX_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
              <#if (((requestParameters.CUSTOMER_FAX_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
              <option></option>
              <option value="Y">${uiLabelMap.CommonY}</option>
              <option value="N">${uiLabelMap.CommonN}</option>
            </select>
          </td>
        </tr>
        <tr>
          <th scope="row">${uiLabelMap.PartyMobilePhone}</th>
          <td><input type="text" name="CUSTOMER_MOBILE_COUNTRY" size="5" value="${requestParameters.CUSTOMER_MOBILE_COUNTRY?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_MOBILE_AREA" size="5" value="${requestParameters.CUSTOMER_MOBILE_AREA?if_exists}" /></td>
          <td><input type="text" name="CUSTOMER_MOBILE_CONTACT" value="${requestParameters.CUSTOMER_MOBILE_CONTACT?if_exists}" /></td>
          <td></td>
          <td>
            <select name="CUSTOMER_MOBILE_ALLOW_SOL">
              <#if (((requestParameters.CUSTOMER_MOBILE_ALLOW_SOL)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
              <#if (((requestParameters.CUSTOMER_MOBILE_ALLOW_SOL)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
              <option></option>
              <option value="Y">${uiLabelMap.CommonY}</option>
              <option value="N">${uiLabelMap.CommonN}</option>
            </select>
          </td>
        </tr>
      </tbody>
    </table>
  </fieldset>-->

   <div class="form-row inline">
        <label for="TANDC" style="width:500px; margin-bottom:10px;">
          <input type="checkbox" class="checkbox" name="termsAndConditions" id="termsAndConditions" value=""  onfocus=""/>
          
           I agree, To the<a href="<@ofbizUrl>termofuse?FaqsId=FaqsId</@ofbizUrl>" class="terms" >  Terms And Conditions</a>&nbsp;*
        </label>
      </div>
      </br>
<div class="buttons" style="margin:5px 5px 5px 0px; ">
<#-- <a href="javascript:document.getElementById('newuserform').submit()" class="buttontext">${uiLabelMap.CommonSave}</a>
	<a href="javascript:formValidation()"  class="buttontext">${uiLabelMap.CommonSave}</a>-->
	<input type="submit" class="buttontext" name="${uiLabelMap.CommonSave}" id="${uiLabelMap.CommonSave}" value="Submit" />
  <#-- a href="<@ofbizUrl>checkLogin/main</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a -->
  <input type="reset" class="buttontext" name='clear' value="Clear" />
  
</div>
  
</form>

<#------------------------------------------------------------------------------
To create a consistent look and feel for all buttons, input[type=submit], 
and a tags acting as submit buttons, all button actions should have a 
class name of "button". No other class names should be used to style 
button actions.
------------------------------------------------------------------------------->

<script type="text/javascript">
  //<![CDATA[
  //]]>
</script>


