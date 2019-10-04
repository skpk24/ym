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
Hi, ${name?if_exists} <br />

<#if exceedBudgetData?exists && exceedBudgetData?has_content>
<b> you  have exceeded  your  budget limit .</b><br /><br />
			<table width="100%" cellspacing="1" cellpadding="5" border="0" class="basic-table hover-bar">
	 		<tr class="header-row">
	 		<td><b>CATEGORY</b></td>
			<td><b>BUDGET PLAN</b></td>
			<td><b>TOTAL USED</b></td>
			<td><b>LEFT</b></td>
			</tr>
				<#if exceedBudgetData.size()!= 0 >
				<#assign keys = exceedBudgetData.keySet()>
				<#if keys?has_content>
					 <#list keys as key>
					 <tr>
	              <td>${key?if_exists}</td>
	              <#assign budData = exceedBudgetData.get(key)>
	              <#list budData as data>
	              <td>${data}</td>
	              </#list></tr>
					 </#list>
				</#if>
		<#--else>
			<tr>
				<td colspan="13" class="normalLink" align="center" style="font-size:14px;">No record found in this date range</td>
				</tr-->
			</#if>
		</table>
				</#if>