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
<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Revenue Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="salesRevenueform" action="<@ofbizUrl>revenueReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' onkeydown="chooseValidate(this);"/>
						<a href="javascript:call_cal(document.salesRevenueform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}'/>
						<a href="javascript:call_cal(document.salesRevenueform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label"> </td>
					<td ><input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>
	<div class="screenlet-title-bar">
      <h3>Revenue Summary  Report</h3>
    </div>
	<div class="screenlet-body">
		<form name="salesreportform" action="<@ofbizUrl>revenueReoprtSummaryCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesreportform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}'/>
							<a href="javascript:call_cal(document.salesreportform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td ></td>
					<td><input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>
	<div class="screenlet-title-bar">
      <h3>Revenue Monthly Summary  Report</h3>
    </div>
	<div class="screenlet-body">
		<form name="salesreportmonthlyform" action="<@ofbizUrl>revenueMonthlyReportSummaryCSV</@ofbizUrl>" onsubmit="return checkAlFields();">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Month: </td>
					<td> 
							<SELECT NAME="fromMonth">
								<OPTION VALUE="">Choose a Month...</OPTION>
								<OPTION VALUE="01">JAN</OPTION>
								<OPTION VALUE="02">FEB</OPTION>
								<OPTION VALUE="03">MAR</OPTION>
								<OPTION VALUE="04">Apr</OPTION>
								<OPTION VALUE="05">May</OPTION>
								<OPTION VALUE="06">JUN</OPTION>
								<OPTION VALUE="07">JULY</OPTION>
								<OPTION VALUE="08">AUG</OPTION>
								<OPTION VALUE="09">SEP</OPTION>
								<OPTION VALUE="10">OCT</OPTION>
								<OPTION VALUE="11">NOV</OPTION>
								<OPTION VALUE="12">DEC</OPTION>
							</SELECT>
		                    <select name="fromYear">
		                        	<option VALUE="2010">2010</option>
								    <option VALUE="2009">2009</option>
								    <option VALUE="2011">2011</option>
								    <option VALUE="2012">2012</option>
								    <option VALUE="2013">2013</option>
								    <option VALUE="2014">2014</option>
							</select>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Month: </td>
					<td>
		                 <select NAME="thruMonth">
								<OPTION VALUE="">Choose a Month...</OPTION>
								<OPTION VALUE="01">JAN</OPTION>
								<OPTION VALUE="02">FEB</OPTION>
								<OPTION VALUE="03">MAR</OPTION>
								<OPTION VALUE="04">Apr</OPTION>
								<OPTION VALUE="05">May</OPTION>
								<OPTION VALUE="06">JUN</OPTION>
								<OPTION VALUE="07">JULY</OPTION>
								<OPTION VALUE="08">AUG</OPTION>
								<OPTION VALUE="09">SEP</OPTION>
								<OPTION VALUE="10">OCT</OPTION>
								<OPTION VALUE="11">NOV</OPTION>
								<OPTION VALUE="12">DEC</OPTION>
						</select>
						 <select name="thruYear">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
						</select>
					</td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" value="${uiLabelMap.CommonCSVReport}" onclick="return checkAlFields();" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>
</div>