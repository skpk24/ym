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


<table cellpadding="0" cellspacing="0" border="0" width="100%">
	<tr>
		<td>
			Dear Sir,
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			I thank you for choosing me as the Chef of the Week and agree to all the terms & conditions.
			<a href="http://youmart.in/catalog/control/recipeDetail?recipeId=${recipeId?if_exists}">link</a>
		</td>
	</tr>
	
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			Regards,<br/><br/>
			${name?if_exists}
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>
