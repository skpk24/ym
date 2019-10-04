
<style>
table { margin: 1em; border-collapse: collapse; }
td, th { padding: .3em; border: 1px #ccc solid;}
thead { background: #fc9; }
 
</style>
<script type="text/javascript">
function printpage()
  {
  window.print();
  }
</script>

<#assign dsr=requestAttributes.DSR?if_exists/>
<a href="javascript:printpage();" class="buttontext">Print</a>

		<div class="screenlet-title-bar">
		  <div class="h3">
		    DSR Report <b style="color:red">(${requestAttributes.start?if_exists} to ${requestAttributes.end?if_exists})</b>
		   </div>
		 </div>   

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
              <td><b>A.</b>Fresh Flower Sales:</td>
			 <td>${dsr.freshFlowerSale?if_exists}</td>
        </tr>
        <tr>
             <td><b>Cumulative Fresh Flower Sales:</b></td>
			 <td><b>${dsr.freshFlowerCumulativeSale?if_exists}</b></td>
        </tr>
		<tr>
			<td><b>B.</b>HandiCrafts Sale</td>
			<td>${dsr.handiCraftSale?if_exists}</td>
		</tr>
		<tr>
			<td><b>Cumulative HandiCrafts Sale</b></td>
			<td><b>${dsr.handiCraftCumulativeSale?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>C.</b>Up Sale</td>
			<td>${dsr.upSaleOrOtherPurchseSale?if_exists}</td>
		</tr>

		<tr>
			<td><b>Cumulative Up Sale</b></td>
			<td><b>${dsr.upSaleOrOtherPurchseCumulativeSale?if_exists}</b></td>
		</tr>


		<tr>
			<td><b>D.</b>Order Transfer Sale</td>
			<td>0.00</td>
		</tr>

		<tr>
			<td><b>Cumulative Order Transfer Sale</b></td>
			<td><b>0.00</b></td>
		</tr>

		<tr>
			<td><b>E.</b>&nbsp;E-Commerce Sale</td>
			<td>${dsr.inStoreEcommOrderSale?if_exists}</td>
		</tr>

		<tr>
			<td><b>Cumulative E-Commerce Sale</b></td>
			<td><b>${dsr.inStoreEcommOrderCumulativeSale?if_exists}</b></td>
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








