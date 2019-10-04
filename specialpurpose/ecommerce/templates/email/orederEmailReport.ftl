<#assign defaultPartyId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "ORGANIZATION_PARTY")>
<#if defaultPartyId?has_content>
	<#assign logoDetail = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",defaultPartyId))?if_exists/>
	 	<#if logoDetail?has_content>
	 		<#assign logoImageUrl = logoDetail.logoImageUrl/>
	 			
	 	</#if>
</#if>
<#assign orderDetail = Static["org.ofbiz.order.report.newReports.AdminReports"].orederEmailReport(delegator,dispatcher,productStoreId,slotIds)?if_exists/>
<#assign orderslotTypes = Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator,slotIds)?if_exists/>

<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
	<tr>
		<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
	</tr>
	<tr><td>&nbsp;</td></tr>
</table>
<table cellpadding="0" cellspacing="0" border="0" width="100%">

	<tr>
		<td>&nbsp;</td>
		<td>
			<b>Total No of Order Approved </b>
		</td>
		<td>
			<b>Amount </b>
		</td>
		<td>
			<b>Total No of Order Dispatched </b>
		</td>
		<td>
			<b>Amount </b>
		</td>
		<td>
			<b>Total No of Order </b>
		</td>
		<td>
			<b>Amount </b>
		</td>
	</tr>
<#assign noOfApprovedOrders = 0>
<#assign noOfApprovedOrdersAmt = 0>
<#assign noOfDispatchedOrders = 0>
<#assign noOfDispatchedOrdersAmt = 0>
<#assign noOfOrders = 0>
<#assign orderTotal = 0>
	<#if orderDetail?has_content>
		<#assign keys = orderDetail.keySet()>
		<#if orderslotTypes?has_content && keys?has_content>
			<#list orderslotTypes as orderslotType>
				<#assign  key = orderslotType.slotType/>
				<#if key?has_content && keys.contains(key)>
					<#assign orderDetailSlotWise = orderDetail.get('${key}')>
					<#if orderDetailSlotWise?has_content>
						<tr>
							<td><b>${orderDetailSlotWise.slotTiming?if_exists}  [${key}] </b></td>
							<td>
								${orderDetailSlotWise.noOfApprovedOrders?if_exists}
								<#assign noOfApprovedOrders = noOfApprovedOrders + orderDetailSlotWise.noOfApprovedOrders?if_exists>
							</td>
							<td>
								${orderDetailSlotWise.ordersApprovedTotalAmt?if_exists}
								<#assign noOfApprovedOrdersAmt = noOfApprovedOrdersAmt + orderDetailSlotWise.noOfApprovedOrdersAmt?if_exists>
							</td>
							<td>
								${orderDetailSlotWise.noOfDispatchedOrders?if_exists}
								<#assign noOfDispatchedOrders = noOfDispatchedOrders + orderDetailSlotWise.noOfDispatchedOrders?if_exists>
							</td>
							<td>
								${orderDetailSlotWise.ordersDispatchedTotalAmt?if_exists}
								<#assign noOfDispatchedOrdersAmt = noOfDispatchedOrdersAmt + orderDetailSlotWise.noOfDispatchedOrdersAmt?if_exists>
							</td>
							<td>
								${orderDetailSlotWise.noOfOrders?if_exists}
								<#assign noOfOrders = noOfOrders + orderDetailSlotWise.noOfOrders?if_exists>
							</td>
							<td>
								${orderDetailSlotWise.orderTotal?if_exists}
								<#assign orderTotal = orderTotal + orderDetailSlotWise.orderTotal?if_exists>
							</td>
						</tr>
					</#if>
				</#if>
			</#list>
			<tr>
				<td><b>Total </b></td>
				<td>
					${noOfApprovedOrders?if_exists}
				</td>
				<td>
					${noOfApprovedOrdersAmt?if_exists}
				</td>
				<td>
					${noOfDispatchedOrders?if_exists}
				</td>
				<td>
					${noOfDispatchedOrdersAmt?if_exists}
				</td>
				<td>
					${noOfOrders?if_exists}
				</td>
				<td>
					${orderTotal?if_exists}
				</td>
			</tr>
		</#if>
	</#if>
</table> 

Thank you and enjoy the week that follows:  
Best regards,
YouMart Team.
 


