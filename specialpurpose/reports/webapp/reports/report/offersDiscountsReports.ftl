<div class="screenlet-title-bar">
	<h3>Daily Summary Of Offers / Discounts</h3>
</div>
<div class="screenlet-body">
	<form name="offersDiscountsReports" method="post" action="<@ofbizUrl>offersDiscountsReportsExcel</@ofbizUrl>">
		<table class="basic-table eventitle" cellspacing="0">
			<tr>
				<td class="label">From Date: </td>
				<td> 
					<input type='text' size='25' class='inputBox' name='fromDate' />
						<a href="javascript:call_cal(document.offersDiscountsReports.fromDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			<tr>
				<td class="label">Thru Date: </td>
				<td>
					<input type='text' size='25' class='inputBox' name='thruDate' />
						<a href="javascript:call_cal(document.offersDiscountsReports.thruDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			<tr>
				<td></td>
				<td >
					<input type="hidden" name="offerTypes" value="PROMOTION_ADJUSTMENT"/>
					<input type="submit" value="Excel" class="smallSubmit">
				</td>
			</tr>
				
		</table>
	</form>
</div>