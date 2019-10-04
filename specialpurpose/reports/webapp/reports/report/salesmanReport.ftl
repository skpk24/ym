<script language="javascript">
function ValidateContactForm(salesmanform)
{
 var makeurl = "SalesManReport";
 
document.salesmanform.action=makeurl;
document.salesmanform.submit();
}
function ValidateContactForm1(salesmanform)
{
 var makeurl = "salesmanReportCSV";
 
document.salesmanform.action=makeurl;
document.salesmanform.submit();
}

</script>

 <div class="screenlet-body">
		<form name="salesmanform" method="post">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${fromDateStr?if_exists}' />
							<a href="javascript:call_cal(document.ecommordform.minDate,'${fromDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${thruDateStr?if_exists}'/>
							<a href="javascript:call_cal(document.ecommordform.maxDate,'${thruDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="View Report" class="smallSubmit" onclick="return ValidateContactForm(salesmanform)"></td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="CSV Report" class="smallSubmit" onclick="return ValidateContactForm1(salesmanform)"></td>
				</tr>
			</table>
		</form>
	</div>
	
	     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="5%">Order Id</td>
         
          <td width="5%" >Cashier Name</td>
           <td width="5%">Total Amount</td>
          
        </tr>
        <#if salesList ?exists && salesList ?has_content>
        
        <#list salesList as ecomm>
  <tr>
          <td width="5%">${ecomm.orderId?if_exists}</td>
          <td width="5%">${ecomm.createdBy?if_exists}</td>
         <td width="5%">${ecomm.grandTotal?if_exists}</td>
    </tr>      
        </#list>
       
       
       
        </#if>
                
	</table>