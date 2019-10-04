
<style>
table { margin: 1em; border-collapse: collapse; }
td, th { padding: .3em; border: 1px #ccc solid;}
thead { background: #fc9; }
 
</style>
<div style="clear:both; padding:2px"></div>
		<div class="screenlet-title-bar">
		  <div class="h3">
		    DSR Report <b style="color:red">(${requestAttributes.start?if_exists} to ${requestAttributes.end?if_exists})</b>
		   </div>
		 </div>   
<div style="clear:both; padding:10px"></div>
		<form name="dashboradReportform" method="post" action="<@ofbizUrl>DSR</@ofbizUrl>">
			<table class="basic-table " cellspacing="0" width="300px">
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
					<td ><input type="submit" value="Find" class="smallSubmit"></td>
				</tr>
			</table>
		</form>



<#assign dsr=requestAttributes.DSR?if_exists/>
<a href="<@ofbizUrl>DSRPrint</@ofbizUrl>" target="_blank"  class="buttontext">Click Here For Print</a>
<div style="float:left; width:100%;">

<div style="float:left;">
<table style="font-family:Arial, Helvetica, sans-serif; font-size:14px;">
    <thead>
    <tr>
            <th>Sales:</th>
            <th>FNP SAKET</th>
    </tr>
    </thead>

<tbody>
        <tr>
              <td style="color:#048E2E">Fresh Flower Sales</td>
			 <td>${dsr.freshFlowerSale?if_exists}</td>
        </tr>
        <tr>
             <td><b style="padding-left:20px">Cumulative Fresh Flower Sales</b></td>
			 <td><b>${dsr.freshFlowerCumulativeSale?if_exists}</b></td>
        </tr>
		<tr>
			<td>HandiCrafts Sale</td>
			<td>${dsr.handiCraftSale?if_exists}</td>
		</tr>
		<tr>
			<td><b style="padding-left:20px">Cumulative HandiCrafts Sale</b></td>
			<td><b>${dsr.handiCraftCumulativeSale?if_exists}</b></td>
		</tr>

		<tr>
			<td>Up Sale</td>
			<td>${dsr.upSaleOrOtherPurchseSale?if_exists}</td>
		</tr>

		<tr>
			<td><b style="padding-left:20px">Cumulative Up Sale</b></td>
			<td><b>${dsr.upSaleOrOtherPurchseCumulativeSale?if_exists}</b></td>
		</tr>


		<tr>
			<td>Order Transfer Sale</td>
			<td>0.00</td>
		</tr>

		<tr>
			<td><b style="padding-left:20px">Cumulative Order Transfer Sale</b></td>
			<td><b>0.00</b></td>
		</tr>

		<tr>
			<td>&nbsp;E-Commerce Sale</td>
			<td>${dsr.inStoreEcommOrderSale?if_exists}</td>
		</tr>

		<tr>
			<td><b style="padding-left:20px">Cumulative E-Commerce Sale</b></td>
			<td><b>${dsr.inStoreEcommOrderCumulativeSale?if_exists}</b></td>
		</tr>
		
		<tr>
			<td>Tax And Adjustments</td>
			<td>${dsr.totalTax?if_exists}</td>
		</tr>

		<tr>
			<td><b style="padding-left:20px">Cumulative Tax And Adjustments</b></td>
			<td><b>${dsr.totalTaxCumulative?if_exists}</b></td>
		</tr>
		<tr>
			<td></td>
			<td></td>
		</tr>
		
		<tr>
			<td>Total</td>
			<td>${dsr.total?if_exists}</td>
		</tr>

		<tr>
			<td><b style="padding-left:20px"></b>Total Cumulative</td>
			<td><b>${dsr.cumulativeTotal?if_exists}</b></td>
		</tr>
</tbody>
</table>
</div>

<div style="float:left;" >
<table style="margin:15px 0 15px 80px; font-family:Arial, Helvetica, sans-serif; font-size:14px;">
    <thead>
    <tr>
            <th colspan="5"> Handi Craft Sales Details</th>
      </tr>
    </thead>

<tbody>
        <tr>
              <th><b>Item</b></th>
              <th><b>Target(Amount)</b></th>
              <th><b>Sale(Amount)</b></th>
              <th><b>Cumulative Sale</th>
    <#--          <th><b>(%)</b></th>-->
        </tr>
       
<#if requestAttributes.listhandicraft?exists && requestAttributes.listhandicraft?has_content>
		<#assign listhandicraft=requestAttributes.listhandicraft>
		<#assign handi1=requestAttributes.totmap>
		
		<#list listhandicraft as handi>
		<tr>
			 <td>${handi.productCategoryName?if_exists}</td>
			 <td>${handi.totalPurchase?if_exists}</td>
			 <td>${handi.totalHandiCraftSale?if_exists}</td>
			 <td>${handi.totalHandiCraftCumulativeSale?if_exists}</td>
			<#-- <td>0%</td>-->
		</tr>
		</#list>
		<tr>
			 <td><b>Total:-</b></td>
			 <td><b>${handi1.totaltarget?if_exists}</td>
			 <td><b>${handi1.totalsale?if_exists}</td>
			 <td><b>${handi1.totalCumulative?if_exists}</td>
			 <#--<td><b>0.00</td>-->
		</tr>
		
</#if>		
</tbody>
</table>
</div>
</div>

<div style="float:left;">
<table style="font-family:Arial, Helvetica, sans-serif; font-size:14px; float:left;">
    <thead>
    <tr>
            <th>Purchase:</th>
            <th>FNP SAKET</th>
    </tr>
    </thead>

<tbody>
		<tr>
			  <td><b>A.</b>Fresh Flower Purchase</td>
			 <td>${dsr.FreshFlowerPurchse?if_exists}</td>
		</tr>
		<tr>
			 <td><b>Cumulative Fresh Flower Purchase</b></td>
			 <td><b>${dsr.cumulativeFreshFlowerPurchse?if_exists}</b></td>
		</tr>
		<tr>
			<td><b>B.</b>Other Purchase</td>
			<td>${dsr.upSaleOrOtherPurchseSale?if_exists}</td>
		</tr>
		<tr>
			<td><b>Cumulative Other Purchase</b></td>
			<td><b>${dsr.upSaleOrOtherPurchseCumulativeSale?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>C.</b>Expense</td>
			<td>${dsr.expense?if_exists}</td>
		</tr>

		<tr>
			<td><b>Cumulative Expense</b></td>
			<td><b>${dsr.expenseCumulative?if_exists}</b></td>
		</tr>
		<tr>
			<td><b>D.</b>No Of  Invoice</td>
			<td>${dsr.numberOfInvoice?if_exists}</td>
		</tr>

		<tr>
			<td><b>Cumulative NOI</b></td>
			<td><b>${dsr.CNOI?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>E.</b>&nbsp;No Of Quantity</td>
			<td>${dsr.totalNoOfQty?if_exists}</td>
		</tr>

		<tr>
			<td><b>Cumulative NOQ</b></td>
			<td><b>${dsr.cumulativetotalNoOfQty?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>F.</b>No. of Complimentary Flower</td>
			<td>0</td>
		</tr>
</tbody>
</table>  
</div>








