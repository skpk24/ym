<style>
 table { margin: 1em; border-collapse: collapse; }
 td, th { padding: .4em; border: 1px #ccc solid;width:200px;}
 thead { background: #fc9; }
</style>
<script type="text/javascript">
function printpage()
  {
  window.print();
  }
</script>
<#assign dsr=requestAttributes.DSRCashSummary?if_exists/>
<#assign fromDateStr=Static["org.ofbiz.base.util.UtilDateTime"].nowDateString("dd/MM/yyyy")?if_exists/>
		<div class="screenlet-title-bar">
		  <div class="h3">
		    DSR Report <b style="color:red">(${requestAttributes.start?if_exists} to ${requestAttributes.end?if_exists})</b>
		   </div>
		 </div>   

<div style="float:left;">
<table style="font-family:Arial, Helvetica, sans-serif; font-size:14px;">
    <thead>
        <tr>
            <th>Name:</th>
            <th>FNP PVT LTD</th>
    </tr>
        <tr>
            <th>Address:</th>
            <th></th>
    </tr>
    
    <tr>
            <th>Details:</th>
            <th>Amount:</th>
    </tr>
    </thead>

<tbody>
        <tr>
              <td><b>Cash Summary:</b></td>
			 <td></td>
        </tr>
        <tr>
             <td><b>O/B</b></td>
			 <td><b>0</b></td>
        </tr>
		<tr>
			<td><b>Cash Sale</b></td>
			<td>${dsr.cashTotal?if_exists}</td>
		</tr>
		<tr>
			<td><b>Due Collection</b></td>
			<td><b>${dsr.dueCollection?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>Total:</b></td>
			<td>${dsr.totcashPlusDue?if_exists}</td>
		</tr>

		<tr>
			<td><b>Total Expenses :</b></td>
			<td><b>${dsr.totalExpense?if_exists}</b></td>
		</tr>


		<tr>
			<td><b>Cash Send To H.O</b></td>
			<td>0</td>
		</tr>

		<tr>
			<td><b>Balance</b></td>
			<td><b>0</b></td>
		</tr>

		<tr>
			<td><b>Sales:</b></td>
			<td></td>
		</tr>

		<tr>
			<td><b>Cash Sale:</b></td>
			<td><b>${dsr.cashTotal?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>Credit Sale:</b></td>
			<td><b>${dsr.creditTotal?if_exists}</b></td>
		</tr>

		<tr>
			<td><b>Total:</b></td>
			<td>${dsr.totcashPlusCredit?if_exists}</td>
		</tr>

</tbody>
</table>
</div>

<div style="float:left;" >
<table style="margin:15px 0 15px 80px; font-family:Arial, Helvetica, sans-serif; font-size:14px;">
    <thead>
    <tr>
            <th >Expenses</th>
            <th ><b style="color:red">${requestAttributes.start?if_exists}</b></th>
      </tr>
    </thead>

<tbody>
        <tr>
              <th><b>Detail</b></th>
              <th><b></b></th>
        </tr>
       
<#if requestAttributes.ExpenseList?exists && requestAttributes.ExpenseList?has_content>
		<#assign expenseList=requestAttributes.ExpenseList>
		<#assign expensemap=requestAttributes.ExpenseMap?if_exists>
		
		<#list expenseList as expenseId>
		<tr>
			 <td>${expenseId?if_exists}</td>
			 <td>${expensemap.get(expenseId)?if_exists}</td>
		</tr>
		</#list>
		<tr>
			 <td><b>Total:-</b></td>
			 <td>${requestAttributes.totalExpenseById?if_exists}</td>
		</tr>
</#if>		
</tbody>
</table>
</div>
</div>

