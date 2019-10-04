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

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

<form method="post" name="cancelledorder" id="cancelledorder" action="<@ofbizUrl>cancelledorder</@ofbizUrl>">
<input type="hidden" name="viewSize" value="${viewSize}"/>
<input type="hidden" name="viewIndex" value="${viewIndex}"/>

<div id="cancelledOrders" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.CancelledFindOrder}</li>
    </ul>
    <br class="clear"/>
  </div>
    <div class="screenlet-body">
      <table class="basic-table" cellspacing='0'>
        <tr>
          <td align='center' width='100%'>
            <table class="basic-table" cellspacing='0'>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductProductStore}</td>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                  <select name='productStoreId'>
                    <#if currentProductStore?has_content>
                    <option value="${currentProductStore.productStoreId}">${currentProductStore.storeName?if_exists}</option>
                    <option value="${currentProductStore.productStoreId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyStore}</option>
                    <#list productStores as store>
                      <option value="${store.productStoreId}">${store.storeName?if_exists}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <input type="hidden" name="orderStatusId" value="ORDER_CANCELLED"/>
              <input type="hidden" name="orderTypeId" value="SALES_ORDER"/>
              <#--<tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.CommonStatus}</td>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                  <select name='orderStatusId'>
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.get("description", locale)}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.OrderAnyOrderStatus}</option>
                    <#list orderStatuses as orderStatus>
                      <option value="${orderStatus.statusId}">${orderStatus.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>-->
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.CommonDateFilter}</td>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                  <table class="basic-table" cellspacing='0'>
                    <tr>
                      <td nowrap="nowrap">
                        <@htmlTemplate.renderDateTimeField name="minDate" event="" action="" value="${requestParameters.minDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="minDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <span class='label'>${uiLabelMap.CommonFrom}</span>
                      </td>
                    </tr>
                    <tr>
                      <td nowrap="nowrap">
                        <@htmlTemplate.renderDateTimeField name="maxDate" event="" action="" value="${requestParameters.maxDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="maxDate1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                        <span class='label'>${uiLabelMap.CommonThru}</span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr><td colspan="3"><hr /></td></tr>
              <tr>
                <td width='25%' align='right'>&nbsp;</td>
                <td width='5%'>&nbsp;</td>
                <td align='left'>
                    <input type="hidden" name="showAll" value="Y"/>
                    <input type='submit' value='${uiLabelMap.CommonFind}'/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </div>
</div>
<input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onclick="javascript:lookupOrders(true);"/>
</form>
<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript" type="text/javascript">
<!--//
document.lookuporder.orderId.focus();
//-->
</script>
</#if>

<br />

<div id="findCancelledList" class="screenlet">
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
    <form name="paginationForm" method="post" action="<@ofbizUrl>searchorders</@ofbizUrl>">
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

      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="5%">${uiLabelMap.BillDate}</td>
          <td width="5%">${uiLabelMap.OrderId}</td>
          <td width="20%">${uiLabelMap.TillSequence}</td>
          <td width="5%" align="right">${uiLabelMap.BillAmt}</td>
          <#--<td width="5%" align="right">${uiLabelMap.CanceledDate}</td>
          <td width="5%" align="right">${uiLabelMap.Tax}</td>
          <td width="5%" align="right">${uiLabelMap.ItemDiscAmt}</td>
          <td width="10%" align="right">${uiLabelMap.CashDisc}</td>
          <td width="10%" align="right">${uiLabelMap.BillAmt}</td>-->
          <td width="10%">&nbsp;</td>
        </tr>
        <#if orderList?has_content>
          <#assign alt_row = false>
          <#list orderList as orderHeader>
            <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
            <#-- <#assign orderType = orderHeader.getRelatedOneCache("OrderType")?if_exists>
           <#if orderType.orderTypeId == "PURCHASE_ORDER">
              <#assign displayParty = orh.getSupplierAgent()?if_exists>
            <#else>
              <#assign displayParty = orh.getPlacingParty()?if_exists>
            </#if>
            <#assign partyId = displayParty.partyId?default("_NA_")>-->
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>${orderHeader.getString("orderDate")}</td>
              <td><a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>${orderHeader.orderId}</a></td>
              <td>${orderHeader.getString("terminalId")?if_exists}</td>
              <td align="right">${orderHeader.getString("grandTotal")?if_exists}</td>
              <#--<td align="right">${orderHeader.getString("terminalId")}</td>
              <td align="right">${orderHeader.getString("terminalId")}</td>
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
              <td>
                <#if partyId != "_NA_">
                  <a href="${customerDetailLink}${partyId}" class="buttontext">${partyId}</a>
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
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
