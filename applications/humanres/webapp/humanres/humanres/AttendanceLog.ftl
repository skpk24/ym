<script language="javascript">
function checkAlFields()
{
	if(document.salesreportmonthlyform.fromMonth.value == "" || document.salesreportmonthlyform.thruMonth.value == "" )
	{
	 alert('from or thru month missing');
	return false;
	}else return true;
}

function attenSubmit(target,formName){

if(target=="CSV"){
  document.forms[formName].submit();
}
else{
     document.forms[formName].action = "<@ofbizUrl>attendanceReoprtPDF</@ofbizUrl>";
     document.forms[formName].submit();
  }
}

</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
		<h3>Daily Attendance Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="AttenReportform" method="post" action="<@ofbizUrl>attendanceReoprtCSV</@ofbizUrl>">
		
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
				<td class="label">Employee Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="AttenReportform" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Employee Name: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.firstName?if_exists}' formName="AttenReportform" name="firstName" id="firstName" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Department Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="AttenReportform" name="deptId" id="deptId" fieldFormName="LookupPartyGroup"/>
                 </td>
				</tr>
					<tr>
					<td class="label">From Date: </td>
					<td> 
	   <input type="hidden" name="reportType" value="Daily Attendance Report"/>
       <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
        <@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
					</td>
				</tr>
				<tr>
					<td></td>
					<td >
					<input type="button" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit" onclick="attenSubmit('CSV','AttenReportform')">
					<input type="button" value="${uiLabelMap.CommonPDFReport}" class="smallSubmit" onclick="attenSubmit('PDF','AttenReportform')">
					</td>
				</tr>
			</table>
		</form>
	</div>

	<div class="screenlet-title-bar">
		<h3>Weekly Attendance Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="weeklyAttenReportform" method="post" action="<@ofbizUrl>attendanceReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
				<td class="label">Employee Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="weeklyAttenReportform" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Employee Name: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.firstName?if_exists}' formName="weeklyAttenReportform" name="firstName" id="firstName" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Department Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="weeklyAttenReportform" name="deptId" id="deptId" fieldFormName="LookupPartyGroup"/>
                 </td>
				</tr>
				<input type="hidden" name="reportType" value="Weekly Attendance Report">
				
				<tr>
				<td class="label">Choose Week: </td>
					<td>
						
						<select NAME="week">
								<OPTION VALUE="01">Choose a week...</OPTION>
								<OPTION VALUE="01">First</OPTION>
								<OPTION VALUE="02">Second</OPTION>
								<OPTION VALUE="03">Third</OPTION>
								<OPTION VALUE="04">Fourth</OPTION>
						</select>	
						</td>
						</tr>
						<tr>
				<td class="label">Choose Month: </td>
					<td>
								
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
						</td>
						</tr>
						<tr>
				<td class="label">Choose Year: </td>
					<td>
						 <select name="year">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
						</select>
						</td>
						</tr>
						<tr><td>
						<input type="button" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit" onclick="attenSubmit('CSV','weeklyAttenReportform')"></td></tr>
						<tr><td><input type="button" value="${uiLabelMap.CommonPDFReport}" class="smallSubmit" onclick="attenSubmit('PDF','weeklyAttenReportform')"></td></tr>
			
			</table>
		</form>
	</div>
	
	<div class="screenlet-title-bar">
		<h3>Monthly Attendance Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="monthlyAttenReportform" method="post" action="<@ofbizUrl>attendanceReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
			<input type="hidden" name="reportType" value="Monthly Attendance Report">	
				<tr>
				<td class="label">Employee Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="monthlyAttenReportform" name="partyId" id="partyId" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Employee Name: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.firstName?if_exists}' formName="monthlyAttenReportform" name="firstName" id="firstName" fieldFormName="LookupPerson"/>
                 </td>
				</tr>
					<tr>
				<td class="label">Department Id: </td>
				<td>
				<@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="monthlyAttenReportform" name="deptId" id="deptId" fieldFormName="LookupPartyGroup"/>
                 </td>
				</tr>
				<tr>
				<td class="label">Choose Month: </td>
				<td>
		
								
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
						</td>
						</tr>
						<tr>
				<td class="label">Choose Year: </td>
				<td>
						 <select name="year">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
						</select>
						</td>
						</tr>
						<tr><td><input type="button" value="${uiLabelMap.CommonCSVReport}" class="smallSubmit" onclick="attenSubmit('CSV','monthlyAttenReportform')"></td></tr>
						<tr><td><input type="button" value="${uiLabelMap.CommonPDFReport}" class="smallSubmit" onclick="attenSubmit('PDF','monthlyAttenReportform')"></td></tr>
					
			</table>
		</form>
	</div>
	</div>
</div>