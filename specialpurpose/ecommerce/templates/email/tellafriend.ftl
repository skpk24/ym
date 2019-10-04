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
<!--done by radha-->
<html>
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
Hi ${toName?if_exists},
<br/><br/>
Your friend ${senderName?if_exists} has recommended you to visit <a href="youmart.in">youmart.in</a> and enjoy a new world of shopping. Kindly click on the following link to visit the website.
<br/><br/>
<a href="${pageUrl?if_exists}">${pageUrl?if_exists}</a>
<br/><br/>
<#if loyaltyRefId?exists>
Kindly enter this reference no. ${loyaltyRefId?if_exists} while registering with youmart.in.
</#if>
<br/>
<br/>
${senderName?if_exists}'s Message:
<br/><br/>
${message?if_exists}
<br/>
<br/>
Regards,
<br/>
${senderName?if_exists}
</html>

