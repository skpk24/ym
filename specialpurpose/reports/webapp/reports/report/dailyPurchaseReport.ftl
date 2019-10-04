<script language="javascript">
function checkAlFields()
{
	if(document.salesreportmonthlyform.fromMonth.value == "" || document.salesreportmonthlyform.thruMonth.value == "" )
	{
	alert('from or thru month missing');
	return false;
	}else return true;
}
</script>

<#--div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.OrderOrderStatisticsPage}</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table" cellspacing='0'>
			<tr class="label">
	         	<td align="right">&nbsp;</td>          
	            <td align="right"> Total Items Sold </td>
	            <td align="right"> Total Order Amount </td>
			</tr>
			<b><tr><td colspan="4"><hr/></td></tr></b>
			<tr>
	         	<td align="right"><b> Todays Sales Summary </b></td>    
				<td align="right">${dayItemCount?string.number}</td>
				<td align="right">${dayItemTotal}</td>
			</tr>          
          <tr><td colspan="6"><hr/></td></tr>

			<tr>
				<td align="right"><b> Weekly Sales Summary </b></td>  
				<td align="right">${weekItemCount?string.number}</td>
				<td align="right">${weekItemTotal}</td>
			</tr>          
			<tr><td colspan="6"><hr/></td></tr>

			<tr>
            	<td align="right"><b>Monthly Sales Summary</b></td>         
				<td align="right">${monthItemCount?string.number}</td>
            	<td align="right">${monthItemTotal}</td>
			</tr>          
          <tr><td colspan="6"><hr/></td></tr>
        </table>
    </div>
</div-->

    
    <div class="screenlet-title-bar">
		<h3>Daily Sales Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="salesReportform" method="post" action="<@ofbizUrl>dailyPurchaseSummaryCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesReportform.minDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesReportform.maxDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td> Store Id</td>
					<td>
						<select name="storeId">
							<option value="9000">[9000] YouMart store</option>
							<option value="UDAILY">[UDAILY] YouDaily store</option>
						</select>
					</td>
				</tr>
				<tr>
					<td class="label">Slot Type: </td>
					<td>
					<#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
				
				  <input type="checkbox" name="slotType" value="ALL">ALL<br>
			      <#if currentSlotList?exists  && currentSlotList?has_content>
					<#list currentSlotList as currentSlotListTem>
			      <input type="checkbox" name="slotType"  value="${currentSlotListTem.slotType?if_exists}">${currentSlotListTem.slotType?if_exists}
			        </#list>
			        </#if>
			       
					</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="text" value=""  name='OrderID' ></td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="CSV Report" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>

