 <script language="javascript">
function chooseValidate()
{
	if(document.dashboradReportform.minDate.value == "" || document.dashboradReportform.maxDate.value == "" )
	{
	 alert('from or thru date missing');
	 return false;
	}else return true;
}
function ValidateContactForm(dashboradReportform)
{
 var makeurl = "dashBoradReport";

document.dashboradReportform.action=makeurl;
document.dashboradReportform.submit();
}
function ValidateContactForm1(dashboradReportform)
{
 var makeurl = "dashBoradReportExcel";
 
document.dashboradReportform.action=makeurl;
document.dashboradReportform.submit();
}
function ValidateContactForm2(dashboradReportform)
{
 var makeurl = "dashBoradReportCsv";
 
document.dashboradReportform.action=makeurl;
document.dashboradReportform.submit();
}
</script>
 <#if requestAttributes.fromDateStr?has_content && requestAttributes.thruDateStr?has_content >
    <div class="screenlet-title-bar">
      <h3>Filter By ${requestAttributes.fromDateStr?if_exists} to ${requestAttributes.thruDateStr?if_exists}</h3>
 </div>
 </#if>
 <div class="screenlet-body">
		<form name="dashboradReportform" method="post">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">Start Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${requestAttributes.fromDateStr?if_exists}' />
							<a href="javascript:call_cal(document.dashboradReportform.minDate,'${requestAttributes.fromDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">End Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${requestAttributes.thruDateStr?if_exists}'/>
							<a href="javascript:call_cal(document.dashboradReportform.maxDate,'${requestAttributes.thruDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="View Report" class="button" onclick="return ValidateContactForm(dashboradReportform)"></td>
				</tr>
			<tr>
					<td></td>
					<td ><input type="submit" value="Excel Report" class="button" onclick="return ValidateContactForm1(dashboradReportform)"></td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="CSV Report" class="button" onclick="return ValidateContactForm2(dashboradReportform)"></td>
				</tr>
			
			</table>
		</form>
	</div>
     
     
     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td >NO. OF SALES</td>
          <td >LOCATION NAME</td>
          <td >CASH SALES</td>
          <td>CREDIT SALES</td>
          <td>CREDIT CARD SALES</td>
          <td>CHEQUE SALES</td>
          <td >TOTAL SALES</td>
         
          <td >CREDIT COLLECTION</td>
          <td >EXPENSE</td>
          <td >Other Purchase</td>
          
        </tr>
        
        
                 <#if requestAttributes.dashBordList?exists && requestAttributes.dashBordList?has_content>
 
                  <script>function opencash(facilityId,frmdate,thrdate){
	            	window.open("<@ofbizUrl>cashwindow</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate, "", "width=800,height=600,status=no,scrollbars=yes");}
	              </script>
	              <script>function openCheque(facilityId,frmdate,thrdate){
	            	window.open("<@ofbizUrl>chequewindow</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate, "", "width=800,height=600,status=no,scrollbars=yes");}
	              </script>



                   <script>function openCredit(facilityId,frmdate,thrdate){
	            	 window.open("<@ofbizUrl>creditwindow</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate, "", "width=800,height=600,status=no,scrollbars=yes");}
	               </script>


                   <script>function openCreditCard(facilityId,frmdate,thrdate){
	            	window.open("<@ofbizUrl>creditcardwindow</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate, "", "width=800,height=600,status=no,scrollbars=yes");}
	               </script>


                  <script>function otherPuchase(facilityId,frmdate,thrdate){
	            	window.open("<@ofbizUrl>otherPuchase</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate, "", "width=800,height=600,status=no,scrollbars=yes");}
	              </script>
                   
                    <script>
						 function expanseDetail(facilityId){
						            	 window.open("/admin/control/expanseDetail?facilityId="+facilityId+"&startDate=${requestAttributes.fromDateStr?if_exists}&endDate=${requestAttributes.thruDateStr?if_exists}", "", "width=800,height=600,status=no,scrollbars=yes");
						            	 }
					</script>
                    <#assign dashBordList=requestAttributes.dashBordList/>
                           <#list dashBordList as dashBord>
                           <tr>
                           <td>
                             ${dashBord.get("NumOfSale")?if_exists}
                           </td>
                           <td>
                            ${dashBord.get("facilityName")?if_exists}
                           </td>
                           <td>
                            <a href="javascript:opencash('${dashBord.get("facilityId")?if_exists}','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}');" ><u>${dashBord.get("totcash")?if_exists}</u></a>
                           </td>
                           <td>
                            <a href="javascript:openCredit('${dashBord.get("facilityId")?if_exists}','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}');" ><u>${dashBord.get("totcredit")?if_exists}</u></a>
                           </td>
                           <td>
                            <a href="javascript:openCreditCard('${dashBord.get("facilityId")?if_exists}','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}');" ><u>${dashBord.get("totcreditcard")?if_exists}</u></a>
                           </td>
                           <td>
                            <a href="javascript:openCheque('${dashBord.get("facilityId")?if_exists}','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}');" ><u>${dashBord.get("totcheck")?if_exists}</u></a>
                           </td>
                           <td>
                             ${dashBord.get("totalSales")?if_exists}
                           </td>
                           
                          <#-- <td>
                             ${dashBord.get("totalFreshCount")?if_exists}
                           </td>--> 
                           
                           <#-- <td>
                             ${dashBord.get("totalFreshPrice")?if_exists}
                           </td>-->
                           <#-- <td>
                             ${dashBord.get("freshFlowerSale")?if_exists}
                           </td>-->
                           
                           <td>
                             ${dashBord.get("totalcollections")?if_exists}
                           </td>
                           <td>
                           		<a href="javascript:expanseDetail('${dashBord.get("facilityId")?if_exists}');" >${dashBord.get("totalExpense")?if_exists}</a>
                           </td>
                           <td>
                           <a href="javascript:otherPuchase('${dashBord.get("facilityId")?if_exists}','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}');" ><u>   ${dashBord.get("totalOtherPurchase")?if_exists}</u></a>
                           </td>
                           
                           </tr>
                           </#list>
                 </#if>
        
        
</table>


