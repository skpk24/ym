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
<script>
	jQuery(document).ready(function() {
	//getIntialtateList();
		 //getAssociatedStateList('IND', 'stateProvinceGeoId');
	});
	
	function getIntialtateList() {
		document.getElementById("countryGeoId").value = "IND";
	    var url = "/control/getAssociatedStateList?countryGeoId=IND";
	        jQuery.ajax({url:url,
	        data: null,
	        type: 'post',
	        async: false,
	        success: function(data) {
	        jQuery('#statesdisplay').html(data);
			changeState3(data.length);
		  }
	    }); 
	}
	
	function changeState3(length){
		if(length == "231"){
		document.getElementById("hiddenState").value = "";
		}else{
			document.getElementById("stateProvinceGeoId").value = document.getElementById("hiddenState").value;
		}
	}
	function changeState(){
	
	document.getElementById("hiddenState").value = document.getElementById("stateProvinceGeoId").value;
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
        jQuery('#statesdisplay').html(data);
		changeState1(data.length);
	  }
    }); 
}
function changeState1(length){
	if(length == "231"){
	document.getElementById("hiddenState").value = "";
	}else{
		document.getElementById("stateProvinceGeoId").value = document.getElementById("hiddenState").value;
	}
}
</script>
<#if canNotView>
  <h3>${uiLabelMap.PartyContactInfoNotBelongToYou}.</h3>
  <a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
<#else>
  <#if !contactMech?exists>
    <#-- When creating a new contact mech, first select the type, then actually create -->
    <#if !requestParameters.preContactMechTypeId?exists && !preContactMechTypeId?exists>
    <h2>${uiLabelMap.PartyCreateNewContactInfo}</h2>
    <form method="post" action='<@ofbizUrl>editcontactmechnosave</@ofbizUrl>' name="createcontactmechform">
      <div>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td>${uiLabelMap.PartySelectContactType}:</td>
          <td>
            <select name="preContactMechTypeId" class='selectBox'>
              <#list contactMechTypes as contactMechType>
                <option value='${contactMechType.contactMechTypeId}'>${contactMechType.get("description",locale)}</option>
              </#list>
            </select>&nbsp;<a href="javascript:document.createcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonCreate}</a>
          </td>
        </tr>
      </table>
      </div>
    </form>
    <#-- <p><h3>ERROR: Contact information with ID "${contactMechId}" not found!</h3></p> -->
    </#if>
  </#if>

  <#if contactMechTypeId?exists>
    <#if !contactMech?exists>
      <h2>${uiLabelMap.PartyCreateNewContactInfo}</h2>
      <#if !checkOut?has_content>
      <a href='<@ofbizUrl>viewprofile</@ofbizUrl>' class="buttontext">${uiLabelMap.CommonGoBack}</a>
      <a href="javascript:document.editcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/>
      </#if>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <form method="post" action='<@ofbizUrl>${requestName}</@ofbizUrl>' name="editcontactmechform">
        <div>
          <input type='hidden' name='contactMechTypeId' value='${contactMechTypeId}' />
          <#if contactMechPurposeType?exists>
            <div class="tabletext">(${uiLabelMap.PartyNewContactHavePurpose} "${contactMechPurposeType.get("description",locale)?if_exists}")</div>
          </#if>
          <#if cmNewPurposeTypeId?has_content><input type='hidden' name='contactMechPurposeTypeId' value='${cmNewPurposeTypeId}' /></#if>
          <#if preContactMechTypeId?has_content><input type='hidden' name='preContactMechTypeId' value='${preContactMechTypeId}' /></#if>
          <#if paymentMethodId?has_content><input type='hidden' name='paymentMethodId' value='${paymentMethodId}' /></#if>
    <#else>
      <h2>${uiLabelMap.PartyEditContactInfo}</h2>
       <#if !checkOut?has_content>
      <a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a>
      <a href="javascript:document.editcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/>
      </#if>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <tr>
          <td align="right" valign="top">${uiLabelMap.PartyContactPurposes}</td>
          <td>
            <table border="0" cellspacing="1">
              <#list partyContactMechPurposes?if_exists as partyContactMechPurpose>
                <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType") />
                <tr>
                  <td>
                    <#if contactMechPurposeType?exists>
                      ${contactMechPurposeType.get("description",locale)}
                    <#else>
                      ${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
                    </#if>
                     (${uiLabelMap.CommonSince}:${partyContactMechPurpose.fromDate?string("dd-MM-yyyy")?if_exists})
                    <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpires}:${partyContactMechPurpose.thruDate.toString()})</#if>
                  </td>
                  <td>
                      <form name="deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}" method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>">
                        <div>
                          <input type="hidden" name="contactMechId" value="${contactMechId}"/>
                          <input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
                          <input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate}"/>
                          <input type="hidden" name="useValues" value="true"/>
                          <a href='javascript:document.deletePartyContactMechPurpose_${partyContactMechPurpose.contactMechPurposeTypeId}.submit()' class='buttontext'>&nbsp;${uiLabelMap.CommonDelete}&nbsp;</a>
                        </div>
                      </form> 
                  </td>
                </tr>
                <tr><td colspan="2" height="15px"></td></tr>
              </#list>
              <#if purposeTypes?has_content>
              
              <tr>
                <td>
                  <form method="post" action='<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>' name='newpurposeform'>
                    <div>
                    <input type="hidden" name="contactMechId" value="${contactMechId}"/>
                    <input type="hidden" name="useValues" value="true"/>
                      <select name='contactMechPurposeTypeId' class='selectBox'>
                        <option></option>
                        <#list purposeTypes as contactMechPurposeType>
                          <option value='${contactMechPurposeType.contactMechPurposeTypeId}'>${contactMechPurposeType.get("description",locale)}</option>
                        </#list>
                      </select>
                      </div>
                  </form>
                </td>
                <td><a href='javascript:document.newpurposeform.submit()' class='buttontext'>${uiLabelMap.PartyAddPurpose}</a></td>
              </tr>
              </#if>
            </table>
          </td>
        </tr>
       
        <form method="post" action='<@ofbizUrl>${requestName}</@ofbizUrl>' name="editcontactmechform">
          <div>
          <input type="hidden" name="contactMechId" value='${contactMechId}' />
          <input type="hidden" name="contactMechTypeId" value='${contactMechTypeId}' />
    </#if>
    <#if contactMechTypeId = "POSTAL_ADDRESS">
      <tr> 
        <td align="right" valign="top" width="170px">${uiLabelMap.PartyToName}</td>
        <td valign="top">
          <input type="text" class='inputBox' size="30" maxlength="60" name="toName" id="toName" value="${postalAddressData.toName?if_exists}" />*
        </td>
        <td valign="top" style="width:365px;"> <div id="toNameDiv" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      <tr>
   
        <td align="right" valign="top">Gender</td>
        <td valign="top" style="width:150px;">
       <input type="radio"  value="Mr. " name="attnName"   style="width:20px;" checked='checked'> <span style="margin-right:35px;">Male</span>
       <input type="radio"  value="Ms. " name="attnName"   style="width:20px;"> Female
       </td>
          <td valign="top" style="width:365px;"> <div id="add2Div" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"> </div> </td>
      </tr>
      <tr>
        <td align="right" valign="top">House No / Flat No</td>
        <td valign="top" style="width:150px;">
          <input type="text" class='inputBox' size="30" maxlength="30" id="address1"  name="address1" value="${postalAddressData.address1?if_exists}" />
        *</td>
        <td valign="top" style="width:365px;"> <div id="add1Div" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      <tr>
        <td align="right" valign="top">Street Name / Building Name</td>
        <td valign="top">
            <input type="text" class='inputBox' size="30" maxlength="30" id="address2" name="address2" value="${postalAddressData.address2?if_exists}" />
        *</td>
          <td valign="top" style="width:365px;"> <div id="add2Div" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"> </div> </td>
      </tr>
      
      <tr>
        <td align="right">Area</td>
        <td valign="top">
            <input type="text" class='inputBox' size="30" maxlength="30" id="area" name="area" value="${postalAddressData.area?if_exists}" />
        *</td>
        <td valign="top" style="width:365px;"> <div id="areaDiv" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      <tr>
        <td align="right" valign="top">Landmark</td>
        <td>
            <input type="text" class='inputBox' size="30" maxlength="30" name="directions" value="${postalAddressData.directions?if_exists}" />
        </td>
      </tr>
      
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyCity}</td>
        <td>
            <input type="text" class='inputBox' size="30" maxlength="30" id="city" name="city" value="${postalAddressData.city?if_exists}" />
        *</td>
         <td valign="top" style="width:365px;"> <div id="cityDiv" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      <tr>
       
       <td>
      <label for="customerCountry">${uiLabelMap.PartyCountry}*</label>
      </td>
      <#--@fieldErrors fieldName="CUSTOMER_COUNTRY"/-->
      <#--select name="CUSTOMER_COUNTRY" class="popup-form-textbox" id="countryGeoId" onchange="getAssociatedStateList('countryGeoId', 'stateProvinceGeoId');">
		        <#if (parameters.CUSTOMER_COUNTRY)?exists>
				<option>${parameters.CUSTOMER_COUNTRY}</option>
				<option value="${parameters.CUSTOMER_COUNTRY}">---</option>
				
				<#else>
					<option value="IND">India</option>
		        </#if>
		        ${screens.render("component://common/widget/CommonScreens.xml#countries")}
	 </select
	 <select name="CUSTOMER_COUNTRY" class="popup-form-textbox" id="countryGeoId">
				<option value="IND">India</option>
	 </select>-->
	 <td> <input type="hidden" name="countryGeoId" id="countryGeoId" value="IND"/>
	 <input type="text"   id="country" value="India" readonly="true"/> </td>
    </div>
    </tr>
    <tr>
    
    <td>
      <label for="customerState">${uiLabelMap.PartyState}*</label></td>
      <#--@fieldErrors fieldName="CUSTOMER_STATE"/-->
      
		        <#-->select class="popup-form-textbox" name="CUSTOMER_STATE1" id="stateProvinceGeoId">
					<#if (parameters.CUSTOMER_STATE)?exists>
					<option>${parameters.CUSTOMER_STATE}</option>
					<#if (parameters.CUSTOMER_STATE).equalsIgnoreCase("No States&#47;Provinces ")>
					<option value="${parameters.CUSTOMER_STATE}">---</option>
					</#if>
					<#else>
					<option value="">${uiLabelMap.PartyNoState}</option>
					</#if>
					${screens.render("component://common/widget/CommonScreens.xml#states")}
			    </select-->
			    <#--<select name="CUSTOMER_STATE1" class="popup-form-textbox" id="stateProvinceGeoId">
					<option value="IN-KA">Karnataka</option>
	 			</select>
				</div>
				<input type="hidden" name="CUSTOMER_STATE" id="hiddenState" style="width:203px;" value="IN-KA"/>-->
				<td>
				<input type="hidden" name="stateProvinceGeoId" id="stateProvinceGeoId" value="IN-KA"/>
				<input type="text" name="state" id="state" value="Karnataka" readonly="true"/></td>
				 
    </div>
    <#--  <tr>
        <td align="right" valign="top">${uiLabelMap.PartyCountry}</td>
        <td>
          <select name="countryGeoId" class='selectBox' style="width:175px;" id="countryGeoId" onchange="getAssociatedStateList('countryGeoId', 'stateProvinceGeoId');">
            <#if postalAddressData.countryGeoId?exists><option value='${postalAddressData.countryGeoId}'>${selectedCountryName?default(postalAddressData.countryGeoId)}</option>
            <#else>
				<option value="IND">India</option>
		    </#if>
            ${screens.render("component://common/widget/CommonScreens.xml#countries")}
          </select>
        *</td>
      </tr>
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyState}</td>
        <td>
        
        <div id="statesdisplay" >
		        <select class="popup-form-textbox" name="CUSTOMER_STATE1" id="stateProvinceGeoId">
				<#if (postalAddressData.stateProvinceGeoId)?exists>
				<option>${selectedStateName?default(postalAddressData.stateProvinceGeoId)}</option>
				<#if (postalAddressData.stateProvinceGeoId).equalsIgnoreCase("No States&#47;Provinces ")>
				<option value="${postalAddressData.stateProvinceGeoId}">---</option>
				</#if>
				<#else>
				<option value="">${uiLabelMap.PartyNoState}</option>
				</#if>
				<#--${screens.render("component://common/widget/CommonScreens.xml#states")}
			    </select>
				</div>
				<input type="text" name="CUSTOMER_STATE1" id="stateProvinceGeoId" value="Karnataka" readonly="true"/>
				<input type="hidden" name="stateProvinceGeoId" id="hiddenState" style="width:203px;" value="${parameters.CUSTOMER_STATE?if_exists}"/>
    </div>
        <#--
          <select name="stateProvinceGeoId" class='selectBox' style="width:175px;">
            <#if postalAddressData.stateProvinceGeoId?exists><option value='${postalAddressData.stateProvinceGeoId}'>${selectedStateName?default(postalAddressData.stateProvinceGeoId)}</option></#if>
            <option value="">${uiLabelMap.PartyNoState}</option>
            ${screens.render("component://common/widget/CommonScreens.xml#states")}
          </select>
        *</td>
      </tr>-->
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyZipCode}</td>
        <td>
          <input type="text" class='inputBox' size="12" maxlength="10" id="postalCode" name="postalCode"  value="${postalAddressData.postalCode?if_exists}" />
        *</td>
        <td valign="top" style="width:365px;"> <div id="postalCodeDiv" style="border:1px dashed #FF9933;  color:red; padding:5px; display:none;"></div> </td>
      </tr>
      
    <#elseif contactMechTypeId = "TELECOM_NUMBER">
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyPhoneNumber}</td>
        <td>
          <input type="text" class='inputBox' size="4" maxlength="10" name="countryCode" value="${telecomNumberData.countryCode?if_exists}" />
          -&nbsp;<input type="text" class='inputBox' size="4" maxlength="10" name="areaCode" value="${telecomNumberData.areaCode?if_exists}" />
          -&nbsp;<input type="text" class='inputBox' size="15" maxlength="15" name="contactNumber" value="${telecomNumberData.contactNumber?if_exists}" />
          &nbsp;${uiLabelMap.PartyExtension}&nbsp;<input type="text" class='inputBox' size="6" maxlength="10" name="extension" value="${partyContactMechData.extension?if_exists}" />
        </td>
      </tr>
      <tr>
        <td align="right" valign="top"></td>
        <td>[${uiLabelMap.PartyCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyExtension}]</td>
      </tr>
    <#elseif contactMechTypeId = "EMAIL_ADDRESS">
      <tr>
        <td align="right" valign="top">${uiLabelMap.PartyEmailAddress}</td>
        <td>
          <input type="text" class='inputBox' size="60" maxlength="255" name="emailAddress" value="<#if tryEntity>${contactMech.infoString?if_exists}<#else>${requestParameters.emailAddress?if_exists}</#if>" />
        *</td>
      </tr>
    <#else>
      <tr>
        <td align="right" valign="top">${contactMechType.get("description",locale)?if_exists}</td>
        <td>
            <input type="text" class='inputBox' size="60" maxlength="255" name="infoString" value="${contactMechData.infoString?if_exists}" />
        *</td>
      </tr>
    </#if>
      
      </div>
    
  </table>
<#if checkOut?has_content>
  <a href="<@ofbizUrl>checkoutoptions</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a>
 <#-- <a href="javascript:document.editcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/> -->
  <a href="javascript:validate(this.form)" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/> 
  <#else>
  <a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a>
  <a href="javascript:document.editcontactmechform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/>
  </#if>
  <#else>
    <a href="<@ofbizUrl>viewprofile</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGoBack}</a><br/><br/>
  </#if>
</#if>
</form>
<script>
var Globalflag = true;
function  validate(oForm){
var formFlag = true;
formFlag = validateForm(); 
if(formFlag){
document.editcontactmechform.action = "<@ofbizUrl>createPostalAddressAndPurposeCheckOut</@ofbizUrl>";
document.editcontactmechform.submit();
} 
}

function checkPostalCodes(value){
Globalflag = true;
var flag = checkCode(value);
}

 


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
          alert("Error during login");
            return false; 
        }
    });  
			return true;
	}
	
function validateForm(){
var subFlag = true;

var toName = document.getElementById("toName").value.trim();
var add1 = document.getElementById("address1").value.trim();
var add2 = document.getElementById("address2").value.trim();
var area = document.getElementById("area").value.trim();
var city = document.getElementById("city").value.trim();
var pCode = document.getElementById("postalCode").value.trim();
document.getElementById("add1Div").style.display = "none";
document.getElementById("add2Div").style.display = "none";
document.getElementById("areaDiv").style.display = "none";
document.getElementById("cityDiv").style.display = "none";
document.getElementById("postalCodeDiv").style.display = "none";
	
			if(toName == ""){
			document.getElementById("toNameDiv").style.display = "block";
 			document.getElementById("toNameDiv").innerHTML = "Please enter Name";
			subFlag = false;
			}
			
			if(add1 == "" && subFlag){
			document.getElementById("add1Div").style.display = "block";
 			document.getElementById("add1Div").innerHTML = "Please enter the  house no";
			subFlag = false;
			}
			
			
			if(add2 == "" && subFlag){
			document.getElementById("add2Div").style.display = "block";
 			document.getElementById("add2Div").innerHTML = "Please enter the  street name";
			subFlag = false;
			}
			
			if(area == "" && subFlag){
			document.getElementById("areaDiv").style.display = "block";
 			document.getElementById("areaDiv").innerHTML = "Please enter the  area";
			subFlag = false;
			}
			
			if(city == "" && subFlag){
			document.getElementById("cityDiv").style.display = "block";
 			document.getElementById("cityDiv").innerHTML = "Please enter the  city";
			subFlag = false;
			}
			
			if(pCode == "" && subFlag){
			document.getElementById("postalCodeDiv").style.display = "block";
 			document.getElementById("postalCodeDiv").innerHTML = "Please enter the  postal Code";
			subFlag = false;
			}else{
			checkPostalCodes(pCode);
			}
			
			
			if(!Globalflag && subFlag){
			document.getElementById("postalCodeDiv").style.display = "block";
 			document.getElementById("postalCodeDiv").innerHTML = "Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.";
			subFlag = false;
			}
			return subFlag;
}	
	
 
 
</script>




