
<#assign minDate = request.getParameter("minDate")?if_exists>
<#assign maxDate = request.getParameter("maxDate")?if_exists>

<#if minDate?has_content && maxDate?has_content >
    <div class="screenlet-title-bar">
        <h3>Filter By ${minDate?if_exists} to ${maxDate?if_exists}</h3>
    </div>
 </#if>
 <div class="screenlet-body">
		<form name="ecommordform" method="post" action="<@ofbizUrl>freshReport</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">Start Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate' value='${minDate?if_exists}' />
							<a href="javascript:call_cal(document.ecommordform.minDate,'${minDate?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">End Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate' value='${maxDate?if_exists}'/>
							<a href="javascript:call_cal(document.ecommordform.maxDate,'${maxDate?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="Find" class="smallSubmit"></td>
				</tr>
			</table>
		</form>
	</div>
	
	     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="5%">Product Id</td>
          <td width="5%">Product Name</td>
          <td width="5%" >Purchase Qty</td>
          <td width="5%" >Total Sale</td>
          <td width="5%" >total Order Return</td>
          <td width="5%" >Damage Qty </td>
          <td width="5%" >Remianing Qty</td>
        </tr>
       
    <#assign results = requestAttributes.result>
<#if results?has_content>
	<#list results as result>
	<#assign product = result.get("product")>
		<tr>
		<td width="5%"><#if product?has_content>${product.productId?if_exists}</#if></td>
		<td width="5%"><#if product?has_content>${product.productName?if_exists}</#if></td>
		<td width="5%">${result.get("totalPurchased")}</td>
		<#--<td width="5%">${result.get("totalPurchasedQOH")}</td>-->
		<td width="5%">${result.get("totalOrders")}</td>
		<#--<td width="5%">${result.get("totalOrdersQOH")}</td>-->
		<td width="5%">${result.get("totalOrderReturn")}</td>
		<#--<td width="5%">${result.get("totalOrderReturnQOH")}</td>-->
		<td width="5%">${result.get("totalDamaged")}</td>
		<#--<td width="5%">${result.get("totalDamagedQOH")}</td>-->
		<td width="5%">${result.get("availableToPromiseTotal")}</td>
		<#--<td width="5%">${result.get("quantityOnHandTotal")}</td>-->
		
	</tr>
	</#list>
</#if>      
       
                
	</table>