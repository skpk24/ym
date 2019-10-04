<script language="javascript">

function genReport()

{	document.registrationreportform.method="post" ;
	document.registrationreportform.action="<@ofbizUrl>registrationreport.csv</@ofbizUrl>";
	document.registrationreportform.submit();
}

function viewReport()
{	
	document.registrationreportform.method="post" ;
	document.registrationreportform.action="<@ofbizUrl>viewRegistrationreport</@ofbizUrl>";
	document.registrationreportform.submit();
}

function pagination(viewSize, viewIndex )
{
	document.registrationreportform.method="post" ;
    document.registrationreportform.VIEW_SIZE.value = viewSize;
    document.registrationreportform.VIEW_INDEX.value = viewIndex;
	document.registrationreportform.action="<@ofbizUrl>viewRegistrationreport</@ofbizUrl>";
	document.registrationreportform.submit();
}

</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>User Registration Report</h3>
    </div>
	<div class="screenlet-body">
		<form name="registrationreportform" method="post">
				<input type="hidden" name="VIEW_SIZE" value="10"/>
				<input type="hidden" name="VIEW_INDEX" value="1"/>
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">Start Date: </td>
					<td> 
						<input type="hidden" name="viewReport" value="true"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}'  onkeydown="chooseValidate(this);"  onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.registrationreportform.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">End Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}' onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.registrationreportform.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<div style="margin:0 0 5px 0"><a href="#" onClick="javascript:document.registrationreportform.action='viewRegistrationreport';document.registrationreportform.submit();" class="buttontext">${uiLabelMap.CommonViewReport}</a></div>
						
						<a href="#" onClick="javascript:document.registrationreportform.action='registrationreport.csv';document.registrationreportform.submit();" class="buttontext">${uiLabelMap.CommonCSVReport}</a>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
<#if registrations?exists >
	<div class="screenlet">
		 <table width="100%" cellspacing="1" cellpadding="5" border="0" class="basic-table hover-bar">
			<tr bgcolor="#FFFFFF">
				<td colspan="13">
					<#-- Start Page Select Drop-Down -->
					<#assign viewIndexMax = Static["java.lang.Math"].ceil((listSize-1)?double / viewSize?double)+1>
					<select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
						<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
						<#list 1..viewIndexMax as curViewNum>
						  	<option value="#" onclick="javascript:pagination('${viewSize}', '${curViewNum}');">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
						</#list>
					</select>
					<#-- End Page Select Drop-Down -->
					<b>
						<#if (listSize?int > 0)>
							<span class="tabletext">${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
						</#if>
					</b><br/>
		      	</td>
			</tr>
		 	<tr class="header-row">
				<#--td><b>Registration Date  </b> </td-->
				<td>  <b>  User ID  </b> </td>
				<td><b> Name</b>   </td>
				<#--td><b>  User Login </b>   </td>
				<td><b>Phone number  </b> </td-->
				<td><b>Email Id </b> </td>
				<td><b>Postal Address  </b>  </td>
			</tr>
			<#if registrations.size()!= 0 >
				 <#assign alt_row = false>
				<#list lowIndex..highIndex as index>
					<#assign reg =  registrations.get(index-1)?if_exists > 
					<tr valign="middle" <#if alt_row> class="alternate-row"</#if>>
						<#--td >${reg.createdDate?if_exists}   </td-->
						<td>  ${reg.partyId?if_exists} </td>
						<td>${reg.PartyName?if_exists}</td>
						<#--td>${reg.userLoginId?if_exists}   </td>
						<td>
						${reg.phoneNumber?if_exists}
						</td-->
						<td>
					 	${reg.emailId?if_exists}
						</td>
						<td>
						${reg.postalAddress?if_exists}
						</td>	
					</tr>
					<#assign alt_row = !alt_row>
				</#list>
			<#else>
				<tr>
					<td colspan="13" class="normalLink" align="center" style="font-size:14px;">No record found in this date range</td>
				</tr>
			</#if>
			<tr bgcolor="#FFFFFF">
				<td colspan="13">
					      <#-- Start Page Select Drop-Down -->
					      <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)+1>
					      <select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
						<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
						<#list 1..viewIndexMax as curViewNum>
							<option value="#" onclick="javascript:pagination('${viewSize}', '${curViewNum}');">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
									</#list>
					      </select>
					      <#-- End Page Select Drop-Down -->
					       <b>
						<#if (listSize?int > 0)>
						  <span class="tabletext">${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
						</#if>
					      </b>
		      	</td>
			</tr>
		</table>
	</div>	
</#if>



