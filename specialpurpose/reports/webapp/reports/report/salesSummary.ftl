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

<!-- <div class="screenlet">
     <div class="screenlet-title-bar">
		<h3>Facility Sales Report</h3>
    </div>
    
     <div class="screenlet-body">
		<form name="facilitysalesReportform" method="post" action="<@ofbizUrl>revenueReoprtCSV</@ofbizUrl>">
		<table class="basic-table eventitle" cellspacing="0">
			<input type="hidden" name="reportType" value="Facility Sales Report"/>
		      <tr>
			   <td class="label">Facility: </td>
			    <td>
					
			   <select NAME="facilityReportId">
			      <OPTION VALUE="">Choose a Facility</OPTION>
						<#if facilityList?has_content>
						   <#list facilityList as facility>
							 <OPTION VALUE="${facility.facilityId}">${facility.facilityName}</OPTION>
						   </#list>
						</#if>
			      </select>	
		   </td>
		</tr>
		<tr>
			<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.facilitysalesReportform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
			<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}' action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.facilitysalesReportform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
					<tr>
					<td></td>
					<td ><input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit"></td>
				</tr>
		</table>
		</form>
	</div> -->
    
    <div class="screenlet-title-bar">
		<h3>Daily Sales Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="salesReportform" method="post" action="<@ofbizUrl>revenueReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesReportform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}' action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesReportform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>

	<div class="screenlet-title-bar">
		<h3>Weekly Sales Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="weeklySalesReportform" method="post" action="<@ofbizUrl>revenueReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td>
						<input type="hidden" name="reportType" value="Weekly Sales Report">
						<select NAME="week">
								<OPTION VALUE="01">Choose a week...</OPTION>
								<OPTION VALUE="01">First</OPTION>
								<OPTION VALUE="02">Second</OPTION>
								<OPTION VALUE="03">Third</OPTION>
								<OPTION VALUE="04">Fourth</OPTION>
						</select>			
						<select NAME="month">
								<OPTION VALUE="01">Choose a Month...</OPTION>
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
						 <select name="year">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
						</select>
						<input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit">
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<div class="screenlet-title-bar">
		<h3>Monthly Sales Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="monthlySalesReportform" method="post" action="<@ofbizUrl>revenueReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td>
						<input type="hidden" name="reportType" value="Monthly Sales Report">			
						<select NAME="month">
								<OPTION VALUE="01">Choose a Month...</OPTION>
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
						 <select name="year">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
						</select>
						<input type="submit" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit">
					</td>
				</tr>
			</table>
		</form>
	</div>
	
	<div class="screenlet-title-bar">
		<h3>Monthly Sales Report Summary</h3>
    </div>
    <div class="screenlet-body">
		<form name="salesreportmonthlyform" method="post" action="<@ofbizUrl>revenueMonthlyReportSummaryCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Month: </td>
					<td> 
							<input type="hidden" name="reportType" value="Monthly Sales Report">			
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
				<tr>
					<td></td>
					<td ><input type="submit" value="${uiLabelMap.CommonCSVReport}" onclick="return checkAlFields();" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>
</div>