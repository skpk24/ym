<br class="clear"> 
<style>
table { margin: 1em; border-collapse: collapse; }
td, th { padding: .3em; border: 1px #ccc solid;}
thead { background: #fc9; }
 
</style>
<script>
function runActionCSV() {
    document.ecommordform.action = "<@ofbizUrl>receiptReportCSV</@ofbizUrl>";
    document.ecommordform.submit();
    return true;
}
function runAction() {
    document.ecommordform.action = "<@ofbizUrl>cashonhand</@ofbizUrl>";
    document.ecommordform.submit();
    return true;
}
</script>
<#assign minDate = request.getParameter("minDate")?if_exists>
<#assign maxDate = request.getParameter("maxDate")?if_exists>




 <div class="screenlet-body">
		<form name="ecommordform" method="post" action="">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">Start Date: </td>
					<td>
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${requestAttributes.fromDateStr?if_exists}' />
							<a href="javascript:call_cal(document.ecommordform.minDate,'${requestAttributes.fromDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">End Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${requestAttributes.thruDateStr?if_exists}'/>
							<a href="javascript:call_cal(document.ecommordform.maxDate,'${requestAttributes.thruDateStr?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				
			
				<tr>
					<td></td>
					<td ><a href="javascript:runAction();" class="buttontext">Find Report</a></td>
				</tr>
				<!-- <tr>
					<td></td>
					<td ><a href="javascript:runActionCSV();" class="buttontext">CSV Report</a></td>
				</tr> -->
			</table>
		</form>
	</div>   
    
    
    
    
     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td>Order Id</td>
          <td>Customer name</td>
          
           <td>CashOnDelivery Total</td>
        
        
          <td>Grand Total</td>
         <td>Order Status</td>
        </tr>
     
         
         <#if requestAttributes.orderList?exists && requestAttributes.orderList?has_content>
           <#assign orderList=requestAttributes.orderList/>
           <#list orderList as order>
          
           		<td>
	           			${order.orderId?if_exists}
	               
           		</td>
           		
           		<td>
	           			${order.createdByUserLogin?if_exists}
	               
           		</td>
           		<td>
	           			${order.maxAmount?if_exists}
	               
           		</td>
           		<td>
	           			${order.grandTotal?if_exists}
	               
           		</td>
           		<td>
	           			${order.statusId?if_exists}
	               
           		</td>
           		
       </tr>
       </#list>
       </#if>
       
     </table>
     
