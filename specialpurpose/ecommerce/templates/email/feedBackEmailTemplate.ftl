<#if name?has_content>
	<#assign userEmail = name>
	<#assign message = message>
	<#assign commentSelect = commentSelect>
	
	 <#assign userLogin = delegator.findByPrimaryKey("UserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId",userEmail))?if_exists/>
	 
	 <#if userLogin?has_content>
	 		<#assign personDetail = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",userLogin.partyId))?if_exists/>
	 		 <#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, userLogin.partyId, false)>
			 <#if partyContactMechValueMaps?has_content>
				<#list partyContactMechValueMaps as partyContactMechValueMap>
				 <#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
					<#assign phoneNumber=telecomNumber.contactNumber?if_exists>
        		
				</#list>
			</#if>
      </#if>
</#if>
<#assign defaultPartyId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "ORGANIZATION_PARTY")>
<#if defaultPartyId?has_content>
	<#assign logoDetail = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",defaultPartyId))?if_exists/>
	 	<#if logoDetail?has_content>
	 		<#assign logoImageUrl = logoDetail.logoImageUrl/>
	 			
	 	</#if>
</#if>
<html>
	<body>
		<img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/>
		<br/><br/>		
		Dear Sir,
		<br/>
		${personDetail.firstName?if_exists} has given the below feedback: 
	    <br/><br/>
		Comment:${commentSelect?if_exists}
		<br/>
		message:${message?if_exists}${phoneNumber?if_exists}
		<br/><br/>
		Regards,
		<br/>
		${personDetail.firstName?if_exists}<br/> 
		<#assign partyContactMechValueMaps = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator, userLogin.partyId, false)>
		<#if partyContactMechValueMaps?has_content>
		<#list partyContactMechValueMaps as partyContactMechValueMap>
		<#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
		<#assign phoneNumber=telecomNumber.contactNumber?if_exists>
	    ${phoneNumber?if_exists}
		</#list>
		</#if>
		<br/>
		${userEmail?if_exists}<br/> 
	</body>
</html>