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

<#-- A simple macro that builds the contact list -->
<#macro contactList publicEmailContactLists>
  <select name="contactListId" id="contactListId" class="selectBox" style="width:134px">
    <#list publicEmailContactLists as publicEmailContactList>
      <#assign publicContactMechType = publicEmailContactList.getRelatedOneCache("ContactMechType")?if_exists>
      
        <option value="${publicEmailContactList.contactListId}">${publicEmailContactList.contactListName?if_exists}</option>
    </#list>
  </select>
</#macro>

<script type="text/javascript" language="JavaScript">
    function unsubscribe() {
        var form = document.getElementById("signUpForContactListForm");
        form.action = "<@ofbizUrl>unsubscribeContactListParty</@ofbizUrl>"
        document.getElementById("statusId").value = "CLPT_UNSUBS_PENDING";
        form.submit();
    }
    function validateSubscribeFields(){
    
    	var email = document.getElementById("customerEmail").value;
    	var contactListId = document.getElementById("contactListId");
    	var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    	if(contactListId.value == '' ){
    		alert("No offer exists.");
    		return false;
    	}
    	if(email == '' || email == 'Your e-mail Address.'){
    		alert("Please enter email.");
    		return false;
    	}
    	else if (!emailPattern.test(email)){
			            email.value=""
			            alert("Invalid Email Address!");
			            return false;
		}
    	
    }
   
</script>

<div id="miniSignUpForContactList" style="width:300px;">
  <#--div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.EcommerceSignUpForContactList}</li>
    </ul>
    <br class="clear"/>
  </div-->
  <div>
  <#if sessionAttributes.autoName?has_content>
  <#-- The visitor potentially has an account and party id -->
    <#if userLogin?has_content && userLogin.userLoginId != "anonymous">
    <#-- They are logged in so lets present the form to sign up with their email address -->
   <!--   <form method="post" action="<@ofbizUrl>createContactListParty</@ofbizUrl>" name="signUpForContactListForm" id="signUpForContactListForm">
          <input type="hidden" name="partyId" value="${partyId}"/>
          <input type="hidden" id="statusId" name="statusId" value="CLPT_PENDING"/>
          <p style="color:#777645;">${uiLabelMap.EcommerceSignUpForContactListComments}</p>
          <div>
            <@contactList publicEmailContactLists=publicEmailContactLists/>
          </div>
          <div>
            <select name="preferredContactMechId" class="selectBox">
              <#list partyAndContactMechList as partyAndContactMech>
                <option value="${partyAndContactMech.contactMechId}"><#if partyAndContactMech.infoString?has_content>${partyAndContactMech.infoString}<#elseif partyAndContactMech.tnContactNumber?has_content>${partyAndContactMech.tnCountryCode?if_exists}-${partyAndContactMech.tnAreaCode?if_exists}-${partyAndContactMech.tnContactNumber}<#elseif partyAndContactMech.paAddress1?has_content>${partyAndContactMech.paAddress1}, ${partyAndContactMech.paAddress2?if_exists}, ${partyAndContactMech.paCity?if_exists}, ${partyAndContactMech.paStateProvinceGeoId?if_exists}, ${partyAndContactMech.paPostalCode?if_exists}, ${partyAndContactMech.paPostalCodeExt?if_exists} ${partyAndContactMech.paCountryGeoId?if_exists}</#if></option>
              </#list>
            </select>
          </div>
          <div>
            <input type="submit" value="${uiLabelMap.EcommerceSubscribe}"/>
            <input type="button" value="${uiLabelMap.EcommerceUnsubscribe}" onclick="javascript:unsubscribe();"/>
          </div>
      </form>-->
    <#else>
    <#-- Not logged in so ask them to log in and then sign up or clear the user association -->
      <p>${uiLabelMap.EcommerceSignUpForContactListLogIn}</p>
      <p><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>">${uiLabelMap.CommonLogin}</a> ${sessionAttributes.autoName}</p>
      <p>(${uiLabelMap.CommonNotYou}? <a href="<@ofbizUrl>autoLogout</@ofbizUrl>">${uiLabelMap.CommonClickHere}</a>)</p>
    </#if>
  <#else>
  <#-- There is no party info so just offer an anonymous (non-partyId) related newsletter sign up -->
    <form method="post" action="<@ofbizUrl>signUpForContactList</@ofbizUrl>" name="signUpForContactListForm" id="signUpForContactListForm">
        <input type="hidden" id="statusId" name="statusId"/>
        <#-- <label style="color:#777645;">Sign up to receive special offers and latest style news.</label>-->
        <div>
         <input type="hidden" name="contactListId" value="<#if temcontactList?has_content>${temcontactList.contactListId?if_exists}</#if>"/>
          <!--<@contactList publicEmailContactLists=publicEmailContactLists/>-->
        </div>
        <div style="padding:5px 0 0 0">
          <input name="email" id="customerEmail" class="inputBox" type="text" size="40" onblur="if (this.value == '') { this.value = 'Your e-mail Address.'; }" onfocus="if (this.value == 'Your e-mail Address.') {this.value = ''; }" value="Your e-mail Address." />
        </div>
        <div style="padding:5px 0 0 0">
          <input type="submit" value="${uiLabelMap.EcommerceSubscribe}" class="buttontextblue" onclick="return validateSubscribeFields();" />
        </div>
    </form>
  </#if>
  </div>
</div>
