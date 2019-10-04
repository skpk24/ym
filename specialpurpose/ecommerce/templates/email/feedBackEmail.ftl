
<#if name?has_content>
	<#assign userEmail = name>
	
	 <#assign userLogin = delegator.findByPrimaryKey("UserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId",userEmail))?if_exists/>
	 
	 <#if userLogin?has_content>
	 		<#assign personDetail = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",userLogin.partyId))?if_exists/>
	 		
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
		<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
			<tr>
				<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
		<table cellpadding="0" cellspacing="0" border="0">
			<tr>
				<td>
					Hi ${personDetail.firstName?if_exists},
				</td>
			</tr>
			<tr>
				<td>	
					Thank you for taking the time and  providing us  the  valuable  feedback.
					<br/><br/>
				</td>
			</tr>
			<tr>
				<td>
					Your responses will help to shape the future of our service, our products and our overall way of doing business.We at YouMart  appreciate the unique opportunity to see ourselves through the eyes of our customers and are eager to gather ideas on what we can do better.
			 	    <br/><br/>
			 	</td>
			</tr>
			<tr>
				<td>
					Thank you again for sharing your thoughts with us. We appreciate your time and effort in providing feedback to us.
					<br/>
					<br/>
				</td>
			</tr>
			<tr>
				<td>
					Sincerely ,<br/>
					Customer care<br/> 
					YouMart
				</td>
			</tr>
		
		<table>
	</body>
</html>