<script type="text/javascript">
window.onload = function() {
		
	 getAssociatedStateList('countryGeoId', 'stateProvinceGeoId');
	 
	 
}

//Generic function for fetching country's associated state list.
function getAssociatedStateList(countryId, stateId) {
    var optionList = [];
    
    var var1 = document.getElementById("countryGeoId").value;
   var var2 = '';
    var var3 = 'stateProvinceGeoId';
    var url = "getAssociatedDynamicStateList?countryGeoId="+var1+"&stateProvinceDynamicGeoId="+var3+"&stateProvinceGeoId1="+var2+"";
    //var url = "getAssociatedStateList?countryGeoId="+var1+"&stateProvinceGeoId1="+var2+"";
        jQuery.ajax({url:url,
        data: null,
        type: 'post',
        async: false,
        success: function(data) {
        //jQuery('#statesdisplay').html(data);
		changeState1(data.length);
	  }
    }); 
}
function changeState(){
	document.getElementById("hiddenState").value = document.getElementById("stateProvinceGeoId").value;
}

function changeState1(length){
	if(length == "231"){
	document.getElementById("hiddenState").value = "";
	}else{
		document.getElementById("stateProvinceGeoId").value = document.getElementById("hiddenState").value;
	}
}
function formValidation(){
	
	if(document.getElementById("address1").value == ''){
		alert("Please enter address 1.");
		document.getElementById("address1").focus();
		return false;
	}
	if(document.getElementById("city").value == ''){
		alert("Please enter your city.");
		document.getElementById("city").focus();
		return false;
	}
	

}

</script> 


<script type="text/javascript">
function checkvalidation()
{

var r=document.getElementById("flag1");

if(r!=null && r.value=='false'){
alert("minimum one postal address is required")
return false;
}else{
return true;
}

}

function formValidation11()
{
var subFlag = true;
	document.getElementById('stateProvinceGeoId').value="IN-KA";
	document.getElementById('countryGeoId').value="IND";
	
	var toName =  document.getElementById('toName').value;
	var address1 =  document.getElementById('address1').value;
	var address2 =  document.getElementById('address2').value;
	var area =  document.getElementById('area').value;
	var city = document.getElementById("city").value;
	var contactNumber =  document.getElementById('cust_mobile').value;
	var emailAddress =  document.getElementById('emailAddress').value;
	var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	
	if(toName =='' || toName == null){
		document.getElementById("toNameErrorMsg").style.display = "block";
		document.getElementById('toNameErrorMsg').innerHTML = "Please enter Name";
		setTimeout("document.getElementById('toName').focus()");
		subFlag = false;
	}else{
		document.getElementById('toNameErrorMsg').innerHTML = "";
	}
	
	if(address1=='' || address1==null){
		document.getElementById("err0rmsg1").style.display = "block";
		document.getElementById('err0rmsg1').innerHTML = "Please enter the  house no";
		setTimeout("document.getElementById('address1').focus()");
		subFlag = false;
	}else{
		document.getElementById('err0rmsg1').innerHTML = "";
	}
	if(address2=='' || address2==null){
		document.getElementById("addressErrorMsg2").style.display = "block";
		document.getElementById('addressErrorMsg2').innerHTML = "Please enter the  street name";
		setTimeout("document.getElementById('address2').focus()");
		subFlag = false;
	}else{
		document.getElementById('addressErrorMsg2').innerHTML = "";
	}
	if(area=='' || area==null){
		document.getElementById("areaErrorMsg").style.display = "block";
		document.getElementById('areaErrorMsg').innerHTML = "Please enter the  area";
		setTimeout("document.getElementById('area').focus()");
		subFlag = false;
	}else{
		document.getElementById('areaErrorMsg').innerHTML = "";
	}
	if(city=='' || city==null){
		document.getElementById("cityErrorMsg").style.display = "block";
		document.getElementById('cityErrorMsg').innerHTML = "Please enter the  city";
		setTimeout("document.getElementById('city').focus()");
		subFlag = false;
	}else{
		document.getElementById('cityErrorMsg').innerHTML = "";
	}
	var postalCode = document.getElementById('postalCode');
	if(postalCode.value==''){
            //alert("Please Enter your ZIP code!");
            document.getElementById("err0rmsg2").style.display = "block";
            document.getElementById('err0rmsg2').innerHTML = "Please Enter your ZIP code!";
            setTimeout("document.getElementById('postalCode').focus()");
            subFlag = false;
       }
    else{
		document.getElementById('err0rmsg2').innerHTML = "";
	}
	if(contactNumber=='' || contactNumber==null){
		 document.getElementById("errormsg3").style.display = "block";
		document.getElementById('errormsg3').innerHTML = "Please Enter your Mobile No.!";
		setTimeout("document.getElementById('contactNumber').focus()");
		subFlag = false;
	}
	else{
		document.getElementById('errormsg3').innerHTML = "";
	}
	if (emailAddress== null || emailAddress ==''){
		 document.getElementById("errormsg4").style.display = "block";
  		document.getElementById('errormsg4').innerHTML = "Please enter a email address!";
     	subFlag = false;
  	}
  	else if(!emailPattern.test(emailAddress)){
  		 document.getElementById("errormsg4").style.display = "block";
      document.getElementById('errormsg4').innerHTML = "Please enter a valid email address!";
      subFlag = false;
  }
  else{
		document.getElementById('errormsg4').innerHTML = "";
	}
	
	if(!subFlag) return false;
  var postalCodeNew =  document.getElementById('postalCode').value;
  var postalCodeOld =  document.getElementById('postalCode1').value;
  if(postalCodeOld != postalCodeNew){
  	Globalflag = true;
	checkCode123(postalCodeNew);
	if(!Globalflag){
		 document.getElementById("err0rmsg2").style.display = "block";
		 document.getElementById('err0rmsg2').innerHTML ="Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.";
	  	 document.getElementById('postalCode').value = document.getElementById('postalCode1').value;
	  	return false;
  	}
  	else{
		document.getElementById('err0rmsg2').innerHTML = "";
	}
  }
	var url="/control/checkMobileNo?mobileNo="+document.getElementById('cust_mobile').value+"&modifyPage=Y";
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
}
function isNumberKeyModifyContantMech(evt)
      {
         var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;
              
         return true;
      }
function validateMobileNoContactMech(){
	var phoneNo=document.getElementById('cust_mobile');
	if((phoneNo.value==null)||(phoneNo.value=="")){
            alert("Please Enter your Mobile No.!")
            setTimeout("document.getElementById('cust_mobile').focus()");
            return false;
       }
    else if(checkNo(phoneNo.value)==false){
    alert("Your Mobile Number must be 10 to 12 digits.");
    phoneNo.value="";
    document.getElementById('cust_mobile').focus();
     return false;
    }
    else{
	    	if(phoneNo.value[0] != "0"){
				document.getElementById('cust_mobile').value = "0"+document.getElementById('cust_mobile').value;
			}
			
	var url="/control/checkMobileNo?mobileNo="+document.getElementById('cust_mobile').value+"&modifyPage=Y";
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
function checkNo(number)
			{ 
			var number_len = number.length;
			if((number_len < 10 || number_len > 12))
			return false;
			return true;
}
var Globalflag = true;
   function checkCode123(value){
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
function checkPostalCodeContactMech(){
	var postalCode = document.getElementById('postalCode');
	if(postalCode.value==''){
            //alert("Please Enter your ZIP code!");
            document.getElementById('err0rmsg2').innerHTML = "Please Enter your ZIP code!";
            setTimeout("document.getElementById('postalCode').focus()");
            return false;
       }
    else if(postalCode.value.length != 6){
            	//alert("Please Enter valid ZIP code!");
            	document.getElementById('err0rmsg2').innerHTML = "Please Enter valid ZIP code!";
            	setTimeout("document.getElementById('postalCode').focus()");
            	return false;
            }  
            
      else{
	            Globalflag = true;
	            checkCode123(postalCode.value);
	             if(!Globalflag){
	            //alert("Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.");
	            document.getElementById('err0rmsg2').innerHTML ="Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.";
	            return false;
	            }else{
	            	document.getElementById('err0rmsg2').innerHTML ="";
	            }
	          }       
}


</script>   
  <#--<form name= "deleteContactMech_${postalid?if_exists}" style="margin-top:10px;" method= "post" action= "<@ofbizUrl>deleteContactMech</@ofbizUrl>" >
               <input type= "hidden" name= "flag1" id="flag1" value= "${flag1?if_exists}"/>
                <input type= "hidden" name= "contactMechId" value= "${postalid?if_exists}"/>
                <a href='javascript:document.deleteContactMech_${postalid?if_exists}.submit()'  class='buttontext' onclick="return checkvalidation();">${uiLabelMap.CommonDelete}</a><br/><br/>
            
  </form>-->
   <form method="post" action='<@ofbizUrl>modifycontactmechNew</@ofbizUrl>' name="editcontactmechform" >
 <table>
 <input type="hidden" name="emailId" value="${emailid?if_exists}">
  <input type="hidden" name="postalId" value="${postalid?if_exists}">
   <input type="hidden" name="teleId" value="${teleid?if_exists}">
   <#if postalAddressData?has_content>
   		<tr>
        <td align="right" valign="top">${uiLabelMap.PartyToName}</td>
        <td>
        <input type="text" class='inputBox' size="30" maxlength="30" name="toName" id="toName" value="<#if postalAddressData.toName?if_exists?has_content>${postalAddressData.toName?if_exists}</#if>"/>
        
        *</td>
        <td valign="top" style="width:365px;"> <div id="toNameErrorMsg" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
        <tr>
        <td align="right" valign="top">House No / Flat No</td>
        <td>
        <input type="text" class='inputBox' size="30" maxlength="30" name="address1" id="address1" value="<#if postalAddressData.address1?if_exists?has_content>${postalAddressData.address1?if_exists}</#if>"/>
        
        *</td>
        <td valign="top" style="width:365px;"> <div id="err0rmsg1" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
       <tr>
        <td align="right" valign="top">Street Name / Building Name</td>
        <td valign="top">
            <input type="text" class='inputBox' size="30" maxlength="30" id="address2" name="address2" value="${postalAddressData.address2?if_exists}" />
       *</td>
        <td valign="top" style="width:365px;"> <div id="addressErrorMsg2" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      <tr>
        <td align="right">Area</td>
        <td valign="top">
            <input type="text" class='inputBox' size="30" maxlength="30" id="area" name="area" value="${postalAddressData.area?if_exists}" />
            *</td>
        <td valign="top" style="width:365px;"> <div id="areaErrorMsg" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
            
      </tr>
      <tr>
        <td align="right" valign="top">Landmark</td>
        <td>
            <input type="text" class='inputBox' size="30" maxlength="30" name="directions" value="${postalAddressData.directions?if_exists}" />
        </td>
      </tr>
      <tr>
       <!-- <td align="right" valign="top">${uiLabelMap.PartyAddressLine2}</td>
        <td>
            <input type="text" class='inputBox' size="30" maxlength="30" name="address2" value="${postalAddressData.address2?if_exists}" />
        </td>-->
      </tr>
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyCity}</td>
        <td>
            <input type="text" class='inputBox' size="30" maxlength="30" name="city" id="city" value="Bangalore" readonly="true" />
            *</td>
        <td valign="top" style="width:365px;"> <div id="cityErrorMsg" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
         
      </tr>
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyCountry}</td>
        <td>
          <input type="text" name="countryGeoId" id="countryGeoId" value="India" readonly="true"/>
        
        *</td>
      </tr>
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyState}</td>
        <td>
        
       <div id="statesdisplay">
       <input type="text" name="stateProvinceGeoId" id="stateProvinceGeoId" value="Karnataka" readonly="true"/>
		*</div>
       </td>
      </tr>
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyZipCode}</td>
        <td><input type="hidden"  name="postalCode1" id="postalCode1" value="${postalAddressData.postalCode?if_exists}" />
        
          <input type="text" class='inputBox' size="15" maxlength="6" name="postalCode" id="postalCode" value="${postalAddressData.postalCode?if_exists}" onkeypress="return isNumberKeyInviteFriend(event);" onChange="checkPostalCodeContactMech()"/>
            *</td>
        <td valign="top" style="width:365px;"> <div id="err0rmsg2" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
         
      </tr>
      
       <tr>
       </#if>
       <#if telecomNumberData?has_content>
        <td align="right" valign="top">${uiLabelMap.PartyPhoneNumber}</td>
        <td>
         
         <input type="text" class='inputBox' size="15"  name="contactNumber" id="cust_mobile" maxlength="10" onkeypress="return isNumberKeyModifyContantMech(event);" onChange="validateMobileNoContactMech()" value="${telecomNumberData.contactNumber?if_exists}" />
         *</td>
        <td valign="top" style="width:365px;"> <div id="errormsg3" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
         
      </tr>
      </#if>
      <#if contactMech?has_content>
     <tr>
        <td align="right" valign="top">${uiLabelMap.PartyEmailAddress}</td>
        <td>
          <input type="text" class='inputBox' size="30" maxlength="255" name="emailAddress" id="emailAddress" value="${contactMech.infoString?if_exists}" />
         *</td>
        <td valign="top" style="width:365px;"> <div id="errormsg4" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
        
      </tr>
      </#if>
      <tr>
      	<td colspan="2">&nbsp;</td>
      </tr>
     <tr>
     	<td colspan="2"> 
	      <a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a>
	      <input type="submit" value="${uiLabelMap.CommonUpdate}" onclick="return formValidation11();">
	      <#--<a href="javascript:document.editcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a>-->
	    </td>
     </tr>
      </table>
      </form>
