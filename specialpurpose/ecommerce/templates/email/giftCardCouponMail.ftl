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
    		<div style="width:600px;height:222px;background:url(http://localhost:808/erptheme1/giftcardbg.png) no-repeat top left">
	    		 <div style="position:relative; top:80px; left;40px; width:550px; height:115px; overflow:auto;">
	    		  <span style="position:absolute; left:135px; top:10px;">
		    		Dear ${orderItemRes.recipientName?if_exists},<br/><br/>
		    		${orderItemRes.recipientMessage}
		    	  </span>
		    	 </div>
    		</div>
    	</td>
    </tr>
    <tr><td>&nbsp;</td></tr>
	<tr>
		<td>
		At the outset, Wishing you the best of this Special Day.<br/><br/>
		You have been gifted with an e-voucher of Rs. ${evoucherAmt?if_exists}/- from ${firstName?if_exists}&nbsp; ${lastName?if_exists} for this special occasion. Your coupon code is ${couponCode?if_exists}.<br/>
		Kindly visit our website - <a href="https://www.youmart.in/">youmart.in</a> and register yourself with this same email id and enjoy shopping with us. While shopping kindly enter the coupon code at the checkout page for you to claim the above e-voucher amount.<br/><br/>
		Wishing you all the best.
		</td>
	</tr>
	<tr>
		<td>Note:<br/>
			.	The above e-voucher can be claimed only with the coupon code.<br/>
			.	The above e-voucher is non-transferable.<br/>
			.	The above e-voucher amount cannot be encashed.<br/>
			.	Splitting your purchase is not allowed with the coupon code; hence you need to purchase for the full amount. Incase if the recipient has purchased lesser than the coupon amount, then he/she will stand to lose the balance amount & will not be encashed or allowed to carry forward for future purchases.<br/>
			.	The validity for the e-voucher is for 1 year from today.<br/>
		</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	<tr>
		<td>Best Regards,<br/>
			YouMart.
	</td>
	</tr>
	<tr><td>&nbsp;</td></tr>
	
	<tr>
	<td></td>
	</tr>
</table>

 
