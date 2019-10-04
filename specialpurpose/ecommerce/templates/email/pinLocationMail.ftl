
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
	<#--<tr>
		<td>
			Dear, ${name?if_exists},
		</td>
	</tr>-->
	<tr>
		<td>
		Hi ${userLoginName?if_exists},<br/><br/>
		At the outset we thank you for showing interest on shopping with us. <br/>
		We apologise for not serving in your area at this moment. Since, we are going phase wise, we will notify you to your email id, once we commence our operations in your respective area.
		</td>
	</tr>

	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			Regards,<br/>
			Customer Service Team<br/>
			YouMart
		</td>
	</tr>
</table>

 
