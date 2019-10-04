<script language="javascript">
function genReport()
{	document.salesreportform.method="post" ;
	document.salesreportform.action="<@ofbizUrl>genSalesreportCSV</@ofbizUrl>";
	document.salesreportform.submit();
}
function viewReport()
{	
	document.salesreportform.method="post" ;
	document.salesreportform.action="<@ofbizUrl>viewSalesreport</@ofbizUrl>";
	document.salesreportform.submit();
}
</script>

<div style="border:1px solid #004488;padding:5px;">
	<span style="font-size:12px;"><b>Sales Report</b></span><br>
	Sales happened between the dates.<br><br>

	<form name="salesreportform" action="<@ofbizUrl>viewSalesreport</@ofbizUrl>">
	<table>
		<tr>
			<td>From Date: </td>
			<td> 
				<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' onkeydown="chooseValidate(this);"/>
					<a href="javascript:call_cal(document.salesreportform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					<span class='tabletext'>${uiLabelMap.CommonFrom}</span>
			</td>
		</tr>
		<tr>
			<td>Till Date: </td>
			<td>
				<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}' onkeydown="chooseValidate(this);/>
					<a href="javascript:call_cal(document.salesreportform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					<span class='tabletext'>${uiLabelMap.CommonThru}</span>
			</td>
		</tr>
		
		<tr>
			<td> Order Status: </td>
			<td>
				<select name='orderStatusID' class='selectBox'>
					<option value="">All</option>
					<option value="ORDER_CREATED">CREATED</option>
					<option value="ORDER_APPROVED">APPROVED</option>					
					<option value="ORDER_COMPLETED">COMPLETED</option>
					<option value="ORDER_CANCELLED">CANCELLED</option>
				 </select>
			</td>
		</tr>	
		<tr>
			<td colspan="2" align="center">&nbsp;</td>
		</tr>

		<tr>
			<td colspan="2" align="center"><input type="button" onclick="viewReport();" value="View Report">&nbsp;<input type="button" onclick="genReport();" value="Dowload As .CSV"></td>
		</tr>

	</table>

			
	</form>
	<br><br><br>

</div>