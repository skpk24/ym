<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may o
btain a copy of the License at

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
</head>
<body>
<#assign defaultPartyId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "ORGANIZATION_PARTY")>
<#if defaultPartyId?has_content>
	<#assign logoDetail = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",defaultPartyId))?if_exists/>
	 	<#if logoDetail?has_content>
	 		<#assign logoImageUrl = logoDetail.logoImageUrl/>
	 			
	 	</#if>
</#if>
		<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
			<tr>
				<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
  <div>${uiLabelMap.EcommerceThisEmailIsInResponseToYourRequestToHave} <#if useEncryption>${uiLabelMap.EcommerceANew}<#else>${uiLabelMap.EcommerceYour}</#if> ${uiLabelMap.EcommercePasswordSentToYou}.</div>
  <br />
  <div class="forgotten">
 <p> Dear ${sendTo?if_exists},<br/>
    We are there to serve you better.<br/>
    Please use the following details to login.</p>
 <table>
   <tr>
     <td>Email id</td>
     <td>:</td>
     <td> ${sendTo?if_exists}</td> 
   </tr>
   <tr>
     <td>password</td>
     <td>:</td>
     <td><#if useEncryption>
          ${uiLabelMap.EcommerceNewPasswordMssgEncryptionOn}
      <#else>
          ${uiLabelMap.EcommerceNewPasswordMssgEncryptionOff}
      </#if>
      "${password}"</td> 
   </tr>
 </table> 
   <p>Please login by clicking on link <strong>http://youmart.in/login/</strong></p>  
   <p> Should you have any queries or difficulties resetting
      your password, please contact us via support at
      customersupport@youmart.in
   </p>  
   <p> Thanks and best regards,<br/>
      Customer Service Manager<br/>
      YouMart.in
   </p>
  <!--
   <p> YouMart.in<br/>
      C/o Ujjivan Enterprises Pvt. Ltd.<br/>
      1098/1, 1st A Cross, 1st Floor,<br/>
      Shammana Complex, Near Shishu Graha School,<br/>
      New Thippasandra Road,<br/>
      Bangalore-560075.<br/>
      Email id: <a href="mailto:customersupport@youmart.in">customersupport@youmart.in</a><br/>
   </p>
    -->
  </div>

</body>
</html>
