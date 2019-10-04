<script type="text/javascript">
function showDate(){
if(document.getElementById("slotbooking").checked==true){
$(document.getElementById("advancedBook")).show();}
else
$(document.getElementById("advancedBook")).hide();
}
</script>
<form method="post" name="ordersitemsdetail" id="ordersitemsdetail" action="<@ofbizUrl>OrderItemDetails</@ofbizUrl>">
<div id="findOrdersItemsDetails" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">Order Item Details</li>
    </ul>
    <br class="clear"/>
  </div>
  
  <div class="screenlet-body">
           <table class="basic-table" cellspacing='0' style="width:400px !important; position:relative;">
  			 <tr>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td width="30%"><input type="checkbox" name="slotbooking" value="today">TODAY</td>
                      <td width="35%"><input type="checkbox" name="slotbooking" value="tomorrow">TOMORROW</td>
                      <td width="30%"><input type="checkbox" name="slotbooking" id="slotbooking" value="advanced" onClick = "showDate()">ADVANCED</td>
	                      
                    <tr>
                      <td><span style="font-weight:bold !important;">SLOT TYPE</span></td> 
                      <td colspan="2"><select name="slotType" id="slotType">
                      	  <option value="All Slot">All Slot</option>
						  <option value="SLOT1">SLOT1</option>
						  <option value="SLOT2">SLOT2</option>
						  <option value="SLOT3">SLOT3</option>
						  <option value="SLOT4">SLOT4</option>
						</select>
                      </td>
                    </tr>
                  </table>
                          <div id="advancedBook" style="display:none; position:absolute; top:2px; right:-200px;" nowrap="nowrap">
	                        <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
	                      </div>
                </td>
              </tr>
              <tr>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                    <input type="hidden" name="showAll" value="Y"/>
                    <input type='submit' class="buttontext" value='FIND'/>
                    <input type='submit' Value="PDF" onClick="javascript:document.ordersitemsdetail.action='viewOrdersItemsDetail.pdf';document.ordersitemsdetail.submit();" class="buttontext"/>
                    <#--<a href="#" onClick="javascript:document.ordersitemsdetail.action='viewOrdersItemsDetail.pdf';document.ordersitemsdetail.submit();" class="buttontext">PDF</a>-->
                </td>
              </tr>
            </table>
            </div> 
</div>

<#if pId?exists && pId?has_content>
	<div class="screenlet-body">
		 <table width="100%" cellspacing="1" cellpadding="5" border="0" class="basic-table hover-bar dark-grid">
		 	<tr class="header-row-2">
				<td><b>PRODUCT ID</b></td>
				<td><b>PRODUCT NAME</b></td>
				<td><b>IMMEDIATE CATEGORY</b></td>
				<td><b>TOTAL ORDERED</b></td>
				<td><b>QOH</b></td>
				<td><b>SHORTAGE</b></td>
				<td><b>SLOT</b></td>
			</tr>
			<#if pId.size()!= 0 >
			
			<#assign keys = pId.keySet()>
			<#if keys?has_content>
			<#assign counter = 1>
				 <#list keys as key>
				 <tr <#if counter%2 == 0>class="alternate-row"</#if>>
				 <#assign counter = counter+1>
		              <td>${key}</td>
		              <#assign product = delegator.findByPrimaryKey("Product", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", key))>
		              <td>${(product.internalName)?if_exists}</td>
		              <#assign orderItems = delegator.findByAnd("OrderItem", {"productId" : key})/>
		              <#list orderItems as orderItem>
		              <#assign productCategory = delegator.findByPrimaryKey("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", orderItem.productCategoryId))/>
		              <td>${productCategory.categoryName}</td>
		              <#break>
		              </#list>
		              <td>${pId.get(key)}</td>
		              <#assign mainInven = dispatcher.runSync("getInventoryAvailableByFacility", Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", key, "facilityId","WebStoreWarehouse"))/>
		              <#assign atp = mainInven.availableToPromiseTotal?if_exists>
		              <#assign qoh = mainInven.quantityOnHandTotal?if_exists>
		              <#if qoh &lt; 0>
		              <td>0</td>
		              <#else>
		              <td>${qoh?if_exists}</td>
		              </#if>
		              <#if atp &lt; 0>
		              <td>${atp?if_exists}</td>
		              <#else>
		              <td></td>
		              </#if>
		              <td>${slotType?if_exists}</td>
             	 </tr>
				 </#list>
			</#if>
			
			<#else>
				<tr>
					<td colspan="13" class="normalLink" align="center" style="font-size:14px;">No record found in this date range</td>
				</tr>
			</#if>
		</table>
	</div>	
</#if>