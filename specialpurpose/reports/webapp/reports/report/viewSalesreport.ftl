<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Orders Found</h3>
    </div>
 <table width="100%" cellspacing="15" cellpadding="5" border="1" class="normalText">
	<tr>
		<td colspan="17">
			      <#-- Start Page Select Drop-Down -->
			      <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)+1>
			    <select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
					<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
					<#list 1..viewIndexMax as curViewNum>
					  <option value="<@ofbizUrl>viewSalesreport/~minDate=${fromDateStr?if_exists}/~maxDate=${thruDateStr?if_exists}/~orderStatusID=${orderStatusID?if_exists}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int}</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
					</#list>
			    </select>
			      <#-- End Page Select Drop-Down -->
			       <b>
				<#if (listSize?int > 0)>
				  <span class="tabletext">${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>
				</#if>
			      </b>
			      <br>
      	</td>
	</tr>
	<tr>
		<td ><b>Date  </b> </td>
		<td>  <b>  Party ID  </b> </td>
		<td><b>  Email ID </b>   </td>
		<td><b>Order ID </b>  </td>
		<td><b>Order status  </b> </td>
		<td><b>  Product ID </b>  </td>
		<td><b>Product Title </b>  </td>
		<td><b>Product Quantity   </b></td>
		<td><b>Price </b>  </td>
		<td><b>Discount Price </b>  </td>
		<td><b>Sub Total  </b> </td>
	</tr>
	<tr>
		<td colspan="17"><hr>
		</td>
	</tr>
<#if sales.size()!= 0  >
<#list lowIndex..highIndex as index>
	<#assign sale =  sales.get(index-1) > 
	<#assign orderRoleListGv = delegator.findByAnd("OrderRole", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", sale.orderId?if_exists))?if_exists>
	<#if orderRoleListGv?exists  && 0 < orderRoleListGv.size() >
		<#assign orderRoleGv =  orderRoleListGv.get(0) > 
	</#if>
        <tr>
	<td>${sale.orderDate?date?string.long?if_exists}   </td>
	<td>    ${orderRoleGv.partyId?if_exists}  </td>
	<td>  ${sale.createdBy?if_exists}    </td>
	<td>${sale.orderId?if_exists}    </td>
	<td>${sale.orderStatusId?if_exists}  </td>
	<td> ${sale.productId?if_exists}   </td>
	<#assign price =  Static["org.ofbiz.marketing.report.ReportsHelper"].getOrderProductPrice(sale.productId?string?if_exists,sale.orderId?if_exists,sale.orderItemSeqId?if_exists)>
	<td> ${sale.productName?if_exists}  </td>
	<td> ${sale.quantity?if_exists}  </td>
	<td> $${sale.unitPrice?if_exists * sale.quantity}  </td>
	<#assign cost = sale.unitPrice * sale.quantity>
	<#assign disCost = cost-price>
	<td>$${disCost}  </td>	
	<td>	${price}  </td>	
	</tr>
	<tr>
		<td colspan="17"><hr>
		</td>
	</tr>
</#list>
<#else>

	<tr>
		<td/><td/><td/><td/><td/>
		<td class="tabletext"> <b>No records Found</b> </td>
		<td/><td/><td/><td/><td/>
	</tr>

</#if>
	<tr>
		<td colspan="17">
					  <br>
			      <#-- Start Page Select Drop-Down -->
			      <#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)+1>
			      <select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
				<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
				<#list 1..viewIndexMax as curViewNum>
					<option value="<@ofbizUrl>viewSalesreport/~minDate=${fromDateStr?if_exists}/~maxDate=${thruDateStr?if_exists}/~orderStatusID=${orderStatusID?if_exists}/~VIEW_SIZE=${viewSize}/~VIEW_INDEX=${curViewNum?int}</@ofbizUrl>">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
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

<script>
	 document.getElementById("productType").value="${productType?if_exists}";	
	 document.getElementById("orderStatusID").value="${orderStatusID?if_exists}";	
</script>
