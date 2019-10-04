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
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title></title>
        <link rel="stylesheet" href="${baseUrl?if_exists}/images/maincss.css" type="text/css"/>
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
        <h1>${title?if_exists}</h1>
        <p>Dear ${person.firstName?if_exists} ${person.lastName?if_exists},</p>
        <p>We sincerely thank you for registering with YouMart.in; and are pleased to welcome you to our  family, where you will enjoy a new world of shopping.</p>
        <p>This email is a confirmation of your registration with us. Following are your login details:</p>
        <p>E-mail : ${userId?if_exists}</p>
        <p>Password : ${password?if_exists}</p>
        <p>You can log in at <a href='http://youmart.in/control/home'>http://youmart.in/control/home</a></p>
        <p>Incase of any assistance, please do call or write to us at below mentioned contact details:</p></br></br>
        <p>Enjoy Shopping!!</p></br></br>
        <p>Thanks and Regards,</p>
        <p>Customer Service Manager</p>
        <p>Phone: (080) 25 25 25 15</p>
        <p>Email id: customersupport@youmart.in</p>
    </body>
</html>
