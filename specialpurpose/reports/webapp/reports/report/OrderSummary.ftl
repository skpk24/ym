 <div class="screenlet-title-bar">
		<h3>Daily Sales Report</h3>
    </div>
    <div class="screenlet-body">
		<form name="salesReportform" method="post" action="<@ofbizUrl>orderReoprt</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">Purchase Date: </td>
					<td> 
					
					<input type="radio"  name="dayOption" id="dayOption" value="sameDay" <#if requestAttributes.dayOption == "sameDay" >checked </#if> />Same Day
					<input type="radio"  name="dayOption" id="dayOption" value="nextDay" <#if requestAttributes.dayOption == "nextDay" >checked </#if> />Next Day
					<input type="radio"  name="dayOption" id="dayOption" value="futureDay" <#if requestAttributes.dayOption == "futureDay" >checked </#if> />Future Day
					
					</td>
				</tr>
			
				<tr>
					<td></td>
					<td ><input type="submit" value="submit" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>

<div id="findOrdersList" class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.OrderOrderList}</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
        <table class="basic-table hover-bar" cellspacing='0'>
          <tr class="header-row">
            <td width="15%">Serial No</td>
            <td width="10%">product Id</td>
            <td width="10%">Product Name</td>
            <td width="10%">Cost Price</td>
            <td width="10%">Quantity Ordered</td> 
            <td width="10%">Quantity On Hand</td>
            <td width="10%">Total To Purchased</td>
            <td width="10%">Total Amount</td>
          
          </tr>
        <#if requestAttributes.purchaseList?exists && requestAttributes.purchaseList?has_content>
           <#assign purchaseList=requestAttributes.purchaseList/>
            <#list purchaseList as field>
     	<tr>
              <td>${field.slNumber?if_exists}</td>
              <td>${field.productId?if_exists}</td>
              <td>${field.productName?if_exists}</td>
              <td>${field.unitCost?if_exists}</td>
               <td>${field.quantitySold?if_exists}</td>
               <td>${field.quantityOnHandTotal?if_exists}</td>
                <td>${field.toPurchased?if_exists}</td>
                <td>${field.totalCost?if_exists}</td>
               </tr>
          </#list>
          </#if>
       </table>
      </div>
  </div>
