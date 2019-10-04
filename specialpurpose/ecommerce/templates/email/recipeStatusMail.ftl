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
		Congratulations! Your Recipe has been selected as the <b>'Recipe of the week'</b> and as per our promise YouMart will be honouring 
		you with the title <b>'Chef of the week'</b>. Your profile will be displayed in our site during the week and you will come across 
		other members who will be interested to contact you for taking tips and sharing their thoughts. 
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
		Apart from this your account has been credited with 50,000 YouMart Savings Points, which you can redeem with other points you have 
		accumulated while shopping. 
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<#-- tr>
		<td>
		Please Tick mark the buttons below if you are in agreement to join the YouMart initiate, <b>'to reward and encourage our customers'.</b>
		</td>
	</tr>
	<tr>
		<td>
			<div style="padding-left:10px;">
				<form action="" onclick="window.open('http://localhost:8080/control/chefoftheweek')" target="_blank" method="get" ><br />
				1.	I am aware that I have been selected at the sole discretion of the management of YouMart Y/N?
			    <input type="radio" name="awareForSoleDiscretion" value="Y"> Yes <input type="radio" name="awareForSoleDiscretion" value="N"> No
		        <br />
				2.	I appreciate this initiative and I am more than happy to share my profile in the YouMart site Y/N?
					<input type="radio" name="appreciateThisInitiative" value="Y"> Yes <input type="radio" name="appreciateThisInitiative" value="N"> No
				<br />
				3.	I am in agreement to participate in the members BLOG during this week and will be happy to respond to any queries from other members about my recipe or cooking related information. Y/N?
					<input type="radio" name="participatingInBlog" value="Y"> Yes <input type="radio" name="participatingInBlog" value="N"> No
				<br /><br />
					Please follow this link to update / view your profile, after accepting the above terms:<br/><br/>
					<input type="submit" value="follow" target="_blank"/>
		        <form>
		    </div>
		</td>
	</tr -->
	<tr>
	<td>
	<div style="padding-left:10px;">
	Please follow this link to update / view your profile, after accepting the above terms:<br/><br/>
	<a href="//www.youmart.in/control/chefoftheweek?recipeManagementId=${recipe.recipeManagementId?if_exists}" target="_blank">www.YouMart.in/Chefoftheweek</a>
	</div>
		</td>
	</tr>
	
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			Thank you and enjoy the week that follows:<br/><br/>
			Best regards,<br/><br/>
			YouMart Team.
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr><td><hr/></td></tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>
			<div style="color:#ff0000 !important;">Disclaimer: YouMart is not responsible for any other communications between the members outside the purview of the related initiative "Chef Zone."</div>
		</td>
	</tr>
</table>


 
 
	
 
 


 
Thank you and enjoy the week that follows:  
Best regards,
YouMart Team.
 


