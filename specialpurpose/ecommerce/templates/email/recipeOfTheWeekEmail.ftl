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
			Dear ${name?if_exists},
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			<p>At the outset, we congratulate you for been chosen as the Chef of the Week!!</p>
 		</td>
 	</tr>
 	<tr>
		<td>
			<p>As an extension of gratitude towards you, your profile will be there on our website for a week for you to showcase your culinary expertise & share your thoughts & views with other netizens.</p>
 		</td>
 	</tr>
 	<tr>
		<td>
			<p>We appreciate for your valuable time taken for this initiative & look forward for your continued patronage.</p>
 		</td>
 	</tr>
 	<tr>
		<td>
			<p>Have an enjoyable week ahead.</p>
 		</td>
 	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			Regards,<br/><br/>
			YouMart
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>
