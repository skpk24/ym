<div class="screenlet-title-bar">
	<h3>Period-to-period comparative reports</h3>
</div>
<div class="screenlet-body">
	<form name="comparativeProductReportsForm" method="post" action="<@ofbizUrl>comparativeProductReportsExcel</@ofbizUrl>">
		<table class="basic-table eventitle" cellspacing="0">
			<tr>
				<td class="label">From Date: </td>
				<td> 
					<input type='text' size='25' class='inputBox' name='fromDate' />
						<a href="javascript:call_cal(document.comparativeProductReportsForm.fromDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
				<td>
					    <input type='text' size='25' class='inputBox' name='fromThruDate' />
						<a href="javascript:call_cal(document.comparativeProductReportsForm.fromThruDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			
			<tr>
				<td class="label">Thru Date: </td>
				<td> 
					<input type='text' size='25' class='inputBox' name='thruFromDate' />
						<a href="javascript:call_cal(document.comparativeProductReportsForm.thruFromDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
				<td>
					<input type='text' size='25' class='inputBox' name='thruDate' />
						<a href="javascript:call_cal(document.comparativeProductReportsForm.thruDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
				</td>
			</tr>
			<#--tr>
				<td class="label">By : </td>
				<td>
					<select name="compareBy" >
						<option value="Category">Category</option>
						<option value="Product">Product</option>
						<option value="Brand">Brand</option>
					</select>
				</td>
			</tr-->
			<div id="Category">
				<tr>
					   <td><span class="label">Category Name</span></td>
		               <td> <@htmlTemplate.lookupField formName="comparativeProductReportsForm" name="productCategoryId" id="productCategoryId" fieldFormName="LookupProductCategory"/></td>
				</tr>
			<div>
			<div id="Product">
				<tr>
					<td> <span class="label">${uiLabelMap.ProductProductId}</span></td>
		             <td>   <@htmlTemplate.lookupField formName="comparativeProductReportsForm" name="productId" id="productId" fieldFormName="LookupProduct"/></td></tr>
				</tr>
			</div>
			<div id="Brand">
				<tr>
					<td class="label">Brand Name: </td>
					<td>
						<@htmlTemplate.lookupField formName="comparativeProductReportsForm" name="brandName" id="brandName" fieldFormName="LookupBrand"/>
					</td>
				</tr>
			</div>
			<tr>
				<td></td>
				<td ><input type="submit" value="Excell" class="smallSubmit"></td>
			</tr>
				
		</table>
	</form>
</div>