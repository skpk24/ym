<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script language="JavaScript" type="text/javascript">
<!-- //
function lookupOrders(click) {
    orderIdValue = document.lookuporder.orderId.value;
    if (orderIdValue.length > 1) {
        document.lookuporder.action = "<@ofbizUrl>orderview</@ofbizUrl>";
        document.lookuporder.method = "get";
    } else {
        document.lookuporder.action = "<@ofbizUrl>searchorders</@ofbizUrl>";
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

    function paginateOrderList(viewSize, viewIndex, hideFields) {
        document.paginationForm.viewSize.value = viewSize;
        document.paginationForm.viewIndex.value = viewIndex;
        document.paginationForm.hideFields.value = hideFields;
        document.paginationForm.submit();
    }

</script>

<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript" type="text/javascript">
<!--//
document.lookuporder.orderId.focus();
//-->
</script>
</#if>

<br />

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
    <form name="paginationForm" method="post" action="<@ofbizUrl>ordersCompleted</@ofbizUrl>">
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
    <form name="massOrderChangeForm" method="post" action="javascript:void();">
		<div>&nbsp;</div>
		<#--
		<#if orderList?has_content>
			<div align="right">
				<input type="hidden" name="screenLocation" value="component://order/widget/ordermgr/OrderPrintScreens.xml#OrderPDF"/>
				<select name="serviceName" onchange="javascript:setServiceName(this);">
				   <option value="javascript:void();">&nbsp;</option>
				   <option value="<@ofbizUrl>massApproveOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderApproveOrder}</option>
				   <option value="<@ofbizUrl>massHoldOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderHold}</option>
				   <option value="<@ofbizUrl>massProcessOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderProcessOrder}</option>
				   <option value="<@ofbizUrl>massCancelOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderCancelOrder}</option>
				   <option value="<@ofbizUrl>massCancelRemainingPurchaseOrderItems?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderCancelRemainingPOItems}</option>
				   <option value="<@ofbizUrl>massRejectOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderRejectOrder}</option>
				   <option value="<@ofbizUrl>massPickOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderPickOrders}</option>
				   <option value="<@ofbizUrl>massQuickShipOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderQuickShipEntireOrder}</option>
				</select>
				<a href="javascript:runAction();" class="buttontext">${uiLabelMap.OrderRunAction}</a>
			</div>
		</#if>
		-->
		<table class="basic-table hover-bar" cellspacing='0'>
		    <tr class="header-row">
		      <td width="1%">
		        <input type="checkbox" id="checkAllOrders" name="checkAllOrders" value="1" onchange="javascript:toggleOrderId(this);"/>
		      </td>
		      <td width="5%">${uiLabelMap.OrderOrderType}</td>
		      <td width="5%">${uiLabelMap.OrderOrderId}</td>
		      <td width="20%">${uiLabelMap.PartyName}</td>
		      <td width="5%" align="right">${uiLabelMap.OrderSurvey}</td>
		      <td width="5%" align="right">${uiLabelMap.OrderItemsOrdered}</td>
		      <td width="5%" align="right">${uiLabelMap.OrderItemsBackOrdered}</td>
		      <td width="5%" align="right">${uiLabelMap.OrderItemsReturned}</td>
		      <td width="10%" align="right">${uiLabelMap.OrderRemainingSubTotal}</td>
		      <td width="10%" align="right">${uiLabelMap.OrderOrderTotal}</td>
		      <td width="5%">&nbsp;</td>
		        <#if (requestParameters.filterInventoryProblems?default("N") == "Y") || (requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y") || (requestParameters.filterPOsWithRejectedItems?default("N") == "Y") || (requestParameters.filterPartiallyReceivedPOs?default("N") == "Y")>
		          <td width="15%">${uiLabelMap.CommonStatus}</td>
		          <td width="5%">${uiLabelMap.CommonFilter}</td>
		        <#else>
		          <td width="20%">${uiLabelMap.CommonStatus}</td>
		        </#if>
		      <td width="20%">${uiLabelMap.OrderDate}</td>
		      <td width="5%">${uiLabelMap.PartyPartyId}</td>
		      <td width="10%">&nbsp;</td>
		    </tr>
		    <#if orderList?has_content>
		      <#assign alt_row = false>
		      <#list orderList as orderHeader>
		        <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
		        <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
		        <#assign orderType = orderHeader.getRelatedOneCache("OrderType")>
		        <#if orderType.orderTypeId == "PURCHASE_ORDER">
		          <#assign displayParty = orh.getSupplierAgent()?if_exists>
		        <#else>
		          <#assign displayParty = orh.getPlacingParty()?if_exists>
		        </#if>
		        <#assign partyId = displayParty.partyId?default("_NA_")>
		        <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
		          <td>
		             <input type="checkbox" name="orderIdList" value="${orderHeader.orderId}" onchange="javascript:toggleOrderIdList();"/>
		          </td>
		          <td>${orderType.get("description",locale)?default(orderType.orderTypeId?default(""))}</td>
		          <td><a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>${orderHeader.orderId}</a></td>
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
		          <td align="right">${orh.hasSurvey()?string.number}</td>
		          <td align="right">${orh.getTotalOrderItemsQuantity()?string.number}</td>
		          <td align="right">${orh.getOrderBackorderQuantity()?string.number}</td>
		          <td align="right">${orh.getOrderReturnedQuantity()?string.number}</td>
		          <td align="right"><@ofbizCurrency amount=orderHeader.remainingSubTotal isoCode=orh.getCurrency()/></td>
		          <td align="right"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orh.getCurrency()/></td>
		
		          <td>&nbsp;</td>
		          <td>${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}</td>
		          </td>
		          <#if (requestParameters.filterInventoryProblems?default("N") == "Y") || (requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y") || (requestParameters.filterPOsWithRejectedItems?default("N") == "Y") || (requestParameters.filterPartiallyReceivedPOs?default("N") == "Y")>
		              <td>
		                  <#if filterInventoryProblems.contains(orderHeader.orderId)>
		                    Inv&nbsp;
		                  </#if>
		                  <#if filterPOsOpenPastTheirETA.contains(orderHeader.orderId)>
		                    ETA&nbsp;
		                  </#if>
		                  <#if filterPOsWithRejectedItems.contains(orderHeader.orderId)>
		                    Rej&nbsp;
		                  </#if>
		                  <#if filterPartiallyReceivedPOs.contains(orderHeader.orderId)>
		                    Part&nbsp;
		                  </#if>
		              </td>
		          </#if>
		          <td>${orderHeader.getString("orderDate")}</td>
		          <td>
		            <#if partyId != "_NA_">
		              <#--<a href="${customerDetailLink}${partyId}" class="buttontext">${partyId}</a>-->
		              ${partyId}
		            <#else>
		              ${uiLabelMap.CommonNA}
		            </#if>
		          </td>
		          <td align='right'>
		            <a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>${uiLabelMap.CommonView}</a>
		          </td>
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
    </form>
  </div>
</div>
