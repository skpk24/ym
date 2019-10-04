<#assign highIndex = (requestAttributes.highIndex)?if_exists>
<#assign lowIndex = (requestAttributes.lowIndex)?if_exists>
<#assign viewIndex = (requestAttributes.viewIndex)?if_exists>
<#assign viewSize = (requestAttributes.viewSize)?if_exists>
<#assign showAll = (requestAttributes.showAll)?if_exists>
<#assign paramList = (requestAttributes.paramList)?if_exists>
<#assign paramIdList = (requestAttributes.paramIdList)?if_exists>
<#assign orderList = (requestAttributes.orderList)?if_exists>
<#assign orderListSize = (requestAttributes.orderListSize)?if_exists>

<script language="JavaScript" type="text/javascript">
<!-- //
function lookupOrders(click) {
    orderIdValue = document.lookuporder.orderId.value;
    if (orderIdValue.length > 1) {
        document.lookuporder.action = "<@ofbizUrl>orderview</@ofbizUrl>";
        document.lookuporder.method = "get";
    } else {
        document.lookuporder.action = "<@ofbizUrl>listOrders</@ofbizUrl>";
    }

    if (click) {
        document.lookuporder.submit();
    }
    return true;
}
function toggleOrderId(master) {
    var form = document.massOrderChangeForm;
    var orders = form.elements.length;
    for (var i = 0; i < orders; i++) {
        var element = form.elements[i];
        if (element.name == "orderIdList") {
            element.checked = master.checked;
        }
    }
}
function setServiceName(selection) {
    document.massOrderChangeForm.action = selection.value;
}
function runAction() {
    var form = document.massOrderChangeForm;
    form.submit();
}

function toggleOrderIdList() {
    var form = document.massOrderChangeForm;
    var orders = form.elements.length;
    var isAllSelected = true;
    for (var i = 0; i < orders; i++) {
        var element = form.elements[i];
        if (element.name == "orderIdList" && !element.checked)
            isAllSelected = false;
    }
    jQuery('#checkAllOrders').attr("checked", isAllSelected);
}

// -->
var myWindow = null;
function openWin123(url) {
	var flag = false;
	
	if(myWindow != null)
	{
		flag = confirm("Packing is running for one order . Do You want to abort packing");
	}else
	{
		myWindow = window.open(url, "_blank", "statusbar=0,toolbar=yes, scrollbars=yes, resizable=yes, width=2000, height=2000");
	}
	if(flag)
	{
		myWindow.close();                                                  // Closes the new window
		myWindow = window.open(url, "_blank", "statusbar=0,toolbar=yes, scrollbars=yes, resizable=yes, width=2000, height=2000");
	}
}

var popUpWin=0;
function openWin(url)
{
  var flag = false;
  if(popUpWin)
  {
    if(!popUpWin.closed)
    {
    	flag = confirm("Packing is running for one order . Do You want to abort packing");
    	if(flag)
    	{
    		popUpWin.close();
    		popUpWin = open(url, 'popUpWin', 'statusbar=0,toolbar=yes, scrollbars=yes, resizable=yes, width=2000, height=2000');
    	}
    }else{
    	popUpWin = open(url, 'popUpWin', 'statusbar=0,toolbar=yes, scrollbars=yes, resizable=yes, width=2000, height=2000');
    }
  }else{
  		popUpWin = open(url, 'popUpWin', 'statusbar=0,toolbar=yes, scrollbars=yes, resizable=yes, width=2000, height=2000');
  	   }
}

function paginateOrderList(viewSize, viewIndex, hideFields) {
    document.paginationForm.viewSize.value = viewSize;
    document.paginationForm.viewIndex.value = viewIndex;
    document.paginationForm.hideFields.value = hideFields;
    document.paginationForm.submit();
}

</script>
<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
<div id="findOrdersList" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderOrderFound}</li>
      <#if (orderList?has_content && 0 < orderList?size)>
        <#if (orderListSize > highIndex)>
          <li><a href="javascript:paginateOrderList('${viewSize}', '${viewIndex+1}', '${requestParameters.hideFields?default("N")}')">${uiLabelMap.CommonNext}</a></li>
        <#else>
          <li><span class="disabled">${uiLabelMap.CommonNext}</span></li>
        </#if>
        <#if (orderListSize > 0)>
          <li><span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${orderListSize}</span></li>
        </#if>
        <#if (viewIndex > 1)>
          <li><a href="javascript:paginateOrderList('${viewSize}', '${viewIndex-1}', '${requestParameters.hideFields?default("N")}')">${uiLabelMap.CommonPrevious}</a></li>
        <#else>
          <li><span class="disabled">${uiLabelMap.CommonPrevious}</span></li>
        </#if>
      </#if>
    </ul>
    <br class="clear" />
  </div>
  <div class="screenlet-body">
    <form name="paginationForm" method="post" action="<@ofbizUrl>listOrders</@ofbizUrl>">
      <input type="hidden" name="viewSize"/>
      <input type="hidden" name="viewIndex"/>
      <input type="hidden" name="hideFields"/>
      <#if paramIdList?exists && paramIdList?has_content>
        <#list paramIdList as paramIds>
          <#assign paramId = paramIds.split("=")/>
          <input type="hidden" name="${paramId[0]}" value="${paramId[1]}"/>
        </#list>
      </#if>
    </form>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrderId}</td>
          <td width="15%">${uiLabelMap.PartyName}</td>
          <td width="8%">${uiLabelMap.CommonStatus}</td>
          <td width="8%">Order Date</td>
          <td width="10%">Delivery Date</td>
          <td width="10%">Timing</td>
          <td width="10%">Pending Quantity To Shipment</td>
          <td width="30%">Shipments / Vehicle / Dispatched Date</td>
          <td width="15%">Payments</td>
          <td width="10%">Packing</td>
        </tr>
        <#if orderList?has_content>
          <#assign alt_row = false>
          <#list orderList as orderHeader>
            <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
            <#assign orderType = orderHeader.getRelatedOneCache("OrderType")>
            <#assign orderPaymentPreferences = orh.getPaymentPreferences()>
            <#if orderType.orderTypeId == "PURCHASE_ORDER">
              <#assign displayParty = orh.getSupplierAgent()?if_exists>
            <#else>
              <#assign displayParty = orh.getPlacingParty()?if_exists>
            </#if>
            <#assign partyId = displayParty.partyId?default("_NA_")>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td><a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" target = "_blank" class='buttontext'>${orderHeader.orderId}</a></td>
              <td>
                <div>
                  <#if displayParty?has_content>
                      <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                      ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
                </div>
              </td>
              <td>${statusItem.get("description")?default(statusItem.statusId?default("N/A"))}</td>
              <td>${orderHeader.orderDate?string("dd-MM-yyyy")?if_exists}</td>
              <td>${orderHeader.deliveryDate?string("dd-MM-yyyy")?if_exists}</td>
              <td>
	              <#assign slotTiming = orh.getSlotTiming()>
	              ${slotTiming?default(orderHeader.slot?default("N/A"))}
              </td>
              <td>
	              <#assign pendingShipmentQuantity = orh.getPendingShipmentQuantity()>
	              <#if pendingShipmentQuantity?default(0) == 0>
	              		No Items To Ship
	              <#else> 
	              ${pendingShipmentQuantity?if_exists}
	              </#if>
              </td>
              <td>
              	  <#assign shipments = orh.getShipmentPackedAndShipments()>
	              <#if shipments?has_content>
	              		<#list shipments as shipment>
	              			<#if shipment.shipmentId == "OrderIssuedWithoutShipment">
	              				<span class="label">${uiLabelMap.OrderIssuedWithoutShipment}</span>
	              			<#else>
	              				<a target="_blank" href="/facility/control/ViewShipment?shipmentId=${shipment.shipmentId?if_exists}&amp;externalLoginKey=${externalLoginKey}" class="buttontext">${shipment.shipmentId?if_exists}</a>
	              				 / 
	              				<#if shipment.vehicleNumber?has_content>
				              		<a href="<@ofbizUrl>FindDeliveryBoyVehicleDetails?vehicleNumber=${shipment.vehicleNumber}</@ofbizUrl>" target = "_blank" class="buttontext">${shipment.vehicleNumber}</a>
				              	<#else>
				              		N/A
				              	</#if>
				              	/ 
				              	<#if shipment.vehicleNumberAssignedDate?has_content>
				              		${shipment.vehicleNumberAssignedDate?string("dd-MM-yyyy")}
				              	<#else>
				              		N/A
				              	</#if>
				              	<br />
	              			</#if>
	              		</#list>
	              <#else>
	              		<b>N/A</b>
	              </#if>
              </td>
              <#if orderPaymentPreferences?has_content>
              	<td>
              		<#list orderPaymentPreferences as orderPaymentPreference>
              		<#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")>
              			
              			${paymentMethodType.get("description",locale)?default(paymentMethodType.paymentMethodTypeId?default("N/A"))} /
              			 
              		</#list>
              	</td>
              <#else>
              <td>&nbsp;</td>
              </#if>
              <td>
              <input type="button" onclick="openWin('<@ofbizUrl>PackOrder?facilityId=${orderHeader.originFacilityId?if_exists}&amp;orderId=${orderHeader.orderId?if_exists}&amp;shipGroupSeqId=00001&amp;requireInventory=N&amp;reserveOrderEnumId=INVRO_FIFO_REC&amp;externalLoginKey=${externalLoginKey}</@ofbizUrl>')" target = "_blank" class="buttontext" value="Packing"></td>
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
          </#list>
        <#else>
          <tr>
            <td colspan='4'><h3>${uiLabelMap.OrderNoOrderFound}</h3></td>
          </tr>
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan='4'><h3>${lookupErrorMessage}</h3></td>
          </tr>
        </#if>
      </table>
  </div>
</div>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>