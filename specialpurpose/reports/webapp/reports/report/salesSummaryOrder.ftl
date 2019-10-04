
<script language="javascript">


function changeReport()
{
selectedValue = document.getElementById("reportType").value;
if(selectedValue=="orderItemReport")
{
 document.getElementById("orderItemReport").style.display='block';
  document.getElementById("orderReport").style.display='none';
}
  if(selectedValue=="orderReport")
{
 document.getElementById("orderItemReport").style.display='none';
  document.getElementById("orderReport").style.display='block';
   }
   
   
 
}
</script>

<#--div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>${uiLabelMap.OrderOrderStatisticsPage}</h3>
    </div>
    <div class="screenlet-body">
        <table class="basic-table" cellspacing='0'>
			<tr class="label">
	         	<td align="right">&nbsp;</td>          
	            <td align="right"> Total Items Sold </td>
	            <td align="right"> Total Order Amount </td>
			</tr>
			<b><tr><td colspan="4"><hr/></td></tr></b>
			<tr>
	         	<td align="right"><b> Todays Sales Summary </b></td>    
				<td align="right">${dayItemCount?string.number}</td>
				<td align="right">${dayItemTotal}</td>
			</tr>          
          <tr><td colspan="6"><hr/></td></tr>

			<tr>
				<td align="right"><b> Weekly Sales Summary </b></td>  
				<td align="right">${weekItemCount?string.number}</td>
				<td align="right">${weekItemTotal}</td>
			</tr>          
			<tr><td colspan="6"><hr/></td></tr>

			<tr>
            	<td align="right"><b>Monthly Sales Summary</b></td>         
				<td align="right">${monthItemCount?string.number}</td>
            	<td align="right">${monthItemTotal}</td>
			</tr>          
          <tr><td colspan="6"><hr/></td></tr>
        </table>
    </div>
</div-->

   
    <div class="screenlet-title-bar">
		<h3>Daily Sales Report</h3>
		
    </div>
   <span class="label"> Select The Report:</span>
    <select id="reportType" name="reportType" onChange="changeReport()">
		<option value="orderItemReport">Item Wise</option>
		<option value="orderReport">Order Wise</option>
		</select>
    <div class="screenlet-body" id="orderItemReport">
		<form name="salesItemReportform" method="post" action="<@ofbizUrl>ItemsalesReoprtCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesItemReportform.minDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesItemReportform.maxDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr><td><span class="label">Category Name</span></td>
                       <td> <@htmlTemplate.lookupField formName="salesItemReportform" name="productCategoryIdTo" id="productCategoryIdTo" fieldFormName="LookupProductCategory"/></td>
				<tr><td> <span class="label">${uiLabelMap.ProductProductId}</span></td>
                     <td>   <@htmlTemplate.lookupField formName="salesItemReportform" name="productId" id="productId" fieldFormName="LookupProduct"/></td></tr>
			<tr>
					<td class="label">Brand Name: </td>
					<td>   <@htmlTemplate.lookupField formName="salesItemReportform" name="brandName" id="brandName" fieldFormName="LookupBrand"/></td></tr>
			<#-- <tr>
					<td class="label">Slot Type: </td>
					<td>
					<#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
				 <select name="slotType">
				   <option value="All">All Slot</option>
					 <#if currentSlotList?exists  && currentSlotList?has_content>
					<#list currentSlotList as currentSlotListTem>
			        <option value="${currentSlotListTem.slotType?if_exists}">${currentSlotListTem.slotType}</option>
			        </#list>
			        </#if>
			        </select>
					</td>
				</tr>
				<tr>
					<td class="label">Payment Method: </td>
					<td>
				<select name="paymentMethod">
				   <option value="All">All</option>
					 <#if paymentList?exists  && paymentList?has_content>
					<#list paymentList as paymentListTem>
			        <option value="${paymentListTem?if_exists}">${paymentListTem}</option>
			        </#list>
			        </#if>
			        </select>
				</td>
				</tr>
				<tr>
					<td class="label">Zone Type: </td>
					<td>
				<select name="zoneType">
				   <option value="All">All</option>
					 <#if zoneList?exists  && zoneList?has_content>
					<#list zoneList as zoneListTem>
			        <option value="${zoneListTem.zoneGroupId?if_exists}">${zoneListTem.zoneGroupName}</option>
			        </#list>
			        </#if>
			        </select>
				</td>
				</tr> -->
				<tr>
					<td></td>
					<td ><input type="submit" value="CSV Report" class="smallSubmit"></td>
				</tr>
				
				
			</table>
		</form>
	</div>
	
	
	 <div class="screenlet-body" id="orderReport" style="display:none">
		<form name="salesOrderReportform" method="post" action="<@ofbizUrl>orderwiseSalesCSV</@ofbizUrl>">
			<table class="basic-table eventitle" cellspacing="0">
				<tr>
					<td class="label">From Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='minDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesOrderReportform.minDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">Thru Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='maxDate'  action="javascript:chooseValidate('this');" onkeydown="chooseValidate(this);" onClick="chooseValidate(this);"/>
							<a href="javascript:call_cal(document.salesOrderReportform.maxDate,'');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<#--<tr><td><span class="label">Category Name</span></td>
                       <td> <@htmlTemplate.lookupField formName="salesOrderReportform" name="productCategoryIdTo" id="productCategoryIdTo" fieldFormName="LookupProductCategory"/></td>
				<tr><td> <span class="label">${uiLabelMap.ProductProductId}</span></td>
                     <td>   <@htmlTemplate.lookupField formName="salesOrderReportform" name="productId" id="productId" fieldFormName="LookupProduct"/></td></tr>
			<tr>
					<td class="label">Brand Name: </td>
					<td>
			
					<input type="text" name="brandName"/></td></tr>
			<tr>-->
			<tr>
                <td class='label'>${uiLabelMap.PartyPartyId}</td>
                <td align='left'>
                  <@htmlTemplate.lookupField value='${requestParameters.partyId?if_exists}' formName="salesOrderReportform" name="partyId" id="partyId" fieldFormName="LookupPartyName"/>
                </td>
              </tr>
              <tr>
                <td class='label'>${uiLabelMap.CommonUserLoginId}</td>
                <td align='left'><input type='text' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'/></td>
              </tr>
			<tr>
					<td class="label">Slot Type: </td>
					<td>
					<#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
				 <select name="slotType">
				   <option value="All">All Slot</option>
					 <#if currentSlotList?exists  && currentSlotList?has_content>
					<#list currentSlotList as currentSlotListTem>
			        <option value="${currentSlotListTem.slotType?if_exists}">${currentSlotListTem.slotType}</option>
			        </#list>
			        </#if>
			        </select>
					</td>
				</tr>
				<tr>
					<td class="label">Payment Method: </td>
					<td>
				<select name="paymentMethod">
				   <option value="All">All</option>
					 <#if paymentList?exists  && paymentList?has_content>
					<#list paymentList as paymentListTem>
			        <option value="${paymentListTem.paymentMethodTypeId?if_exists}">${paymentListTem.description}</option>
			        </#list>
			        </#if>
			        </select>
				</td>
				</tr>
				<tr>
					<td class="label">Zone Type: </td>
					<td>
				<select name="zoneType">
				   <option value="All">All</option>
					 <#if zoneList?exists  && zoneList?has_content>
					<#list zoneList as zoneListTem>
			        <option value="${zoneListTem.zoneGroupId?if_exists}">${zoneListTem.zoneGroupName}</option>
			        </#list>
			        </#if>
			        </select>
				</td>
				</tr>
				<tr>
					<td></td>
					<td ><input type="submit" value="CSV Report" class="smallSubmit"></td>
				</tr>
				
				
			</table>
		</form>
	</div>
	
	

