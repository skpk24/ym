<div class="screenlet-title-bar">
	<h3>Stock in / Stock out Report</h3>
</div>
<div class="screenlet-body">
	<form name="stockInOutReportForm" method="post" action="<@ofbizUrl>stockInOutReportCSV</@ofbizUrl>">
		<table class="basic-table eventitle" cellspacing="0">
			<tr>
				<td class="label">From Date: </td>
				<td> 
					<input type='text' size='25' class='inputBox' name='fromDate' />
						<a href="javascript:call_cal(document.stockInOutReportForm.fromDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			<tr>
				<td class="label">Thru Date: </td>
				<td>
					<input type='text' size='25' class='inputBox' name='thruDate' />
						<a href="javascript:call_cal(document.stockInOutReportForm.thruDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			<tr><td><span class="label">Category Name</span></td>
	               <td> <@htmlTemplate.lookupField formName="stockInOutReportForm" name="productCategoryId" id="productCategoryId" fieldFormName="LookupProductCategory"/></td>
			<tr><td> <span class="label">${uiLabelMap.ProductProductId}</span></td>
	             <td>   <@htmlTemplate.lookupField formName="stockInOutReportForm" name="productId" id="productId" fieldFormName="LookupProduct"/></td></tr>
			<tr>
				<td class="label">Brand Name: </td>
				<td>
					<@htmlTemplate.lookupField formName="stockInOutReportForm" name="brandName" id="brandName" fieldFormName="LookupBrand"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td ><input type="submit" value="CSV" class="smallSubmit"></td>
			</tr>
				
		</table>
	</form>
</div>