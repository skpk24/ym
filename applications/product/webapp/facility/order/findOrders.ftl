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
            element.disabled = !element.disabled;
        }
    }
}
function setServiceName(selection) {
    document.massOrderChangeForm.action = selection.value;
}
function runAction() {
    var form = document.massOrderChangeForm;
    var orders = form.elements.length;
    for (var i = 0; i < orders; i++) {
        var element = form.elements[i];
        if (element.name == "orderIdList") {
            element.disabled = false;
        }
    }
    form.submit();
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
<#if parameters.hideFields?has_content>
<form name='lookupandhidefields${requestParameters.hideFields}' method="POST" action="<@ofbizUrl>searchorders</@ofbizUrl>">
  <#if parameters.hideFields?default("N")=='Y'>
    <input type="hidden" name="hideFields" value="N"/>
  <#else>
    <input type='hidden' name='hideFields' value='Y'/>
  </#if>
  <input type="hidden" name="viewSize" value="${viewSize}"/>
  <input type="hidden" name="viewIndex" value="${viewIndex}"/>
  <input type='hidden' name='correspondingPoId' value='${requestParameters.correspondingPoId?if_exists}'/>
  <input type='hidden' name='internalCode' value='${requestParameters.internalCode?if_exists}'/>
  <input type='hidden' name='productId' value='${requestParameters.productId?if_exists}'/>
  <input type='hidden' name='inventoryItemId' value='${requestParameters.inventoryItemId?if_exists}'/>
  <input type='hidden' name='serialNumber' value='${requestParameters.serialNumber?if_exists}'/>
  <input type='hidden' name='softIdentifier' value='${requestParameters.softIdentifier?if_exists}'/>
  <input type='hidden' name='partyId' value='${requestParameters.partyId?if_exists}'/>
  <input type='hidden' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'/>
  <input type='hidden' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'/>
  <input type='hidden' name='createdBy' value='${requestParameters.createdBy?if_exists}'/>
  <input type='hidden' name='minDate' value='${requestParameters.minDate?if_exists}'/>
  <input type='hidden' name='maxDate' value='${requestParameters.maxDate?if_exists}'/>
  <input type='hidden' name='roleTypeId' value="${requestParameters.roleTypeId?if_exists}"/>
  <input type='hidden' name='orderTypeId' value='${requestParameters.orderTypeId?if_exists}'/>
  <input type='hidden' name='salesChannelEnumId' value='${requestParameters.salesChannelEnumId?if_exists}'/>
  <input type='hidden' name='productStoreId' value='${requestParameters.productStoreId?if_exists}'/>
  <input type='hidden' name='orderWebSiteId' value='${requestParameters.orderWebSiteId?if_exists}'/>
  <input type='hidden' name='orderStatusId' value='${requestParameters.orderStatusId?if_exists}'/>
  <input type='hidden' name='hasBackOrders' value='${requestParameters.hasBackOrders?if_exists}'/>
  <input type='hidden' name='filterInventoryProblems' value='${requestParameters.filterInventoryProblems?if_exists}'/>
  <input type='hidden' name='filterPartiallyReceivedPOs' value='${requestParameters.filterPartiallyReceivedPOs?if_exists}'/>
  <input type='hidden' name='filterPOsOpenPastTheirETA' value='${requestParameters.filterPOsOpenPastTheirETA?if_exists}'/>
  <input type='hidden' name='filterPOsWithRejectedItems' value='${requestParameters.filterPOsWithRejectedItems?if_exists}'/>
</form>
</#if>
<form method="post" name="lookuporder" action="<@ofbizUrl>searchorders</@ofbizUrl>" onsubmit="javascript:lookupOrders();">
<input type="hidden" name="lookupFlag" value="Y"/>
<input type="hidden" name="hideFields" value="Y"/>
<input type="hidden" name="viewSize" value="${viewSize}"/>
<input type="hidden" name="viewIndex" value="${viewIndex}"/>

<div id="findOrders" class="screenlet">
  <div class="screenlet-title-bar">
    
    
    <div class="h3">
      
      
      <div class="createButton">
      <#if requestParameters.hideFields?default("N") == "Y">
        <a class="buttontext" href="javascript:document.lookupandhidefields${requestParameters.hideFields}.submit()">${uiLabelMap.CommonShowLookupFields}</a>
      <#else>
        <#if orderList?exists><li><a href="javascript:document.lookupandhidefields${requestParameters.hideFields}.submit()">${uiLabelMap.CommonHideFields}</a></li></#if>
        <a class="buttontext" href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${uiLabelMap.PartyLookupParty}</a>
        <a  class="buttontext"href="javascript:lookupOrders(true);">${uiLabelMap.OrderLookupOrder}</a>
      </#if>
    	</div>
    	${uiLabelMap.OrderFindOrder}
    </div>
    
    
    
    <br class="clear"/>
  </div>
  <#if parameters.hideFields?default("N") != "Y">
    <div class="screenlet-body">
      <table class="basic-table" cellspacing='0'>
        <tr>
          <td align='center' width='100%'>
            <table class="basic-table" cellspacing='0'>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderOrderId}</td>
                <td align='left'><input type='text' name='orderId'/></td>
              </tr>
             <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderExternalId}</td>
                <td align='left'><input type='text' name='externalId'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderCustomerPo}</td>
                <td align='left'><input type='text' name='correspondingPoId' value='${requestParameters.correspondingPoId?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderInternalCode}</td>
                <td align='left'><input type='text' name='internalCode' value='${requestParameters.internalCode?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductProductId}</td>
                <td align='left'><input type='text' name='productId' value='${requestParameters.productId?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductInventoryItemId}</td>
                <td align='left'><input type='text' name='inventoryItemId' value='${requestParameters.inventoryItemId?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductSerialNumber}</td>
                <td align='left'><input type='text' name='serialNumber' value='${requestParameters.serialNumber?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductSoftIdentifier}</td>
                <td align='left'><input type='text' name='softIdentifier' value='${requestParameters.softIdentifier?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.PartyRoleType}</td>
                <td align='left'>
                  <select name='roleTypeId' multiple="multiple">
                    <#if currentRole?has_content>
                    <option value="${currentRole.roleTypeId}">${currentRole.get("description", locale)}</option>
                    <option value="${currentRole.roleTypeId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyRoleType}</option>
                    <#list roleTypes as roleType>
                      <option value="${roleType.roleTypeId}">${roleType.roleTypeId}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.PartyPartyId}</td>
                <td align='left'>
                  <input type='text' name='partyId' value='${requestParameters.partyId?if_exists}'/>
                  <a href="javascript:call_fieldlookup2(document.lookuporder.partyId,'LookupPartyName');">
                    <img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt="${uiLabelMap.CommonClickHereForFieldLookup}"/>
                  </a>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.PartyUserLoginId}</td>
                <td align='left'><input type='text' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderOrderType}</td>
                <td align='left'>
                  <select name='orderTypeId'>
                    <#if currentType?has_content>
                    <option value="${currentType.orderTypeId}">${currentType.get("description", locale)}</option>
                    <option value="${currentType.orderTypeId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyOrderType}</option>
                    <#list orderTypes as orderType>
                      <option value="${orderType.orderTypeId}">${orderType.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.AccountingBillingAccount}</td>
                <td align='left'><input type='text' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.CommonCreatedBy}</td>
                <td align='left'><input type='text' name='createdBy' value='${requestParameters.createdBy?if_exists}'/></td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderSalesChannel}</td>
                <td align='left'>
                  <select name='salesChannelEnumId'>
                    <#if currentSalesChannel?has_content>
                    <option value="${currentSalesChannel.enumId}">${currentSalesChannel.get("description", locale)}</option>
                    <option value="${currentSalesChannel.enumId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnySalesChannel}</option>
                    <#list salesChannels as channel>
                      <option value="${channel.enumId}">${channel.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <#--><tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.PosTerminalDisplay}</td>
                <td align='left'>
                  <select name='terminalId'>
                    <option value=""> </option>
                    <#list terminals as terminal>
                      <option value="${terminal.posTerminalId}">${terminal.posTerminalId}</option>
                    </#list>
                  </select>
                </td>
              </tr>-->
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductProductStore}</td>
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
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.ProductWebSite}</td>
                <td align='left'>
                  <select name='orderWebSiteId'>
                    <#if currentWebSite?has_content>
                    <option value="${currentWebSite.webSiteId}">${currentWebSite.siteName}</option>
                    <option value="${currentWebSite.webSiteId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyWebSite}</option>
                    <#list webSites as webSite>
                      <option value="${webSite.webSiteId}">${webSite.siteName?if_exists}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.CommonStatus}</td>
                <td align='left'>
                  <select name='orderStatusId'>
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.get("description", locale)}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonAnyOrderStatus}</option>
                    <#list orderStatuses as orderStatus>
                      <option value="${orderStatus.statusId}">${orderStatus.get("description", locale)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderContainsBackOrders}</td>
                <td align='left'>
                  <select name='hasBackOrders'>
                    <#if requestParameters.hasBackOrders?has_content>
                    <option value="Y">${uiLabelMap.OrderBackOrders}</option>
                    <option value="Y">---</option>
                    </#if>
                    <option value="">${uiLabelMap.CommonShowAll}</option>
                    <option value="Y">${uiLabelMap.CommonOnly}</option>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.CommonDateFilter}</td>
                <td align='left'>
                        <input type='text' size='25' name='minDate' value='${requestParameters.minDate?if_exists}'/>
                        <a href="javascript:call_cal(document.lookuporder.minDate,'${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
                        <span class='label'>${uiLabelMap.CommonFrom}</span><br />
                        <input type='text' size='25' name='maxDate' value='${requestParameters.maxDate?if_exists}'/>
                        <a href="javascript:call_cal(document.lookuporder.maxDate,'${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
                        <span class='label'>${uiLabelMap.CommonThru}</span>
                </td>
              </tr>
              <tr>
                <td width='15%' align='right' class='label'>${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterInventoryProblems}</td>
                <td align='left'>
                  
                        <input type="checkbox" name="filterInventoryProblems" value="Y"
                            <#if requestParameters.filterInventoryProblems?default("N") == "Y">checked="checked"</#if> />
                     
                </td>
              </tr>
              <tr>
                <td width='15%' align='right' class='label'>${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPartiallyReceivedPOs}</td>
                <td align='left'>
                  
                        <input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                            <#if requestParameters.filterPartiallyReceivedPOs?default("N") == "Y">checked="checked"</#if> />
                      
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</td>
                <td align='left'>
                  
                        <input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                            <#if requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y">checked="checked"</#if> />
                     
                </td>
              </tr>
              <tr>
                <td width='25%' align='right' class='label'>${uiLabelMap.OrderFilterOn} ${uiLabelMap.OrderFilterPOs} ${uiLabelMap.OrderFilterPOsWithRejectedItems}</td>
                <td align='left'>
                  
                        <input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                            <#if requestParameters.filterPOsWithRejectedItems?default("N") == "Y">checked="checked"</#if> />
                     
                </td>
              </tr>
              <tr><td colspan="3"><hr/></td></tr>
              <tr>
                <td width='25%' align='right'>&nbsp;</td>
                <td align='left'>
                    <input type='checkbox' name='showAll' value='Y' onclick="javascript:lookupOrders(true);"/>&nbsp;${uiLabelMap.CommonShowAllRecords}
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      </#if>
    </div>
</div>
<div ><input type="submit" src="<@ofbizContentUrl></@ofbizContentUrl>" value="submit" onClick="javascript:lookupOrders(true);" class="smallSubmit"/></div>
</form>
<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript" type="text/javascript">
<!--//
document.lookuporder.orderId.focus();
//-->
</script>
</#if>

<br/>

<div id="findOrdersList" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderOrderFound}</li>
      <#if (orderList?has_content && 0 < orderList?size)>
        <#if (orderListSize > highIndex)>
          <li style="padding:3px 0 0 0"><a href="javascript:paginateOrderList('${viewSize}', '${viewIndex+1}', '${requestParameters.hideFields?default("N")}')" style="font-size:12px" class="smallSubmit">${uiLabelMap.CommonNext}</a></li>
        <#else>
          <li style="padding:0 5px 0 0"><span class="disabled">${uiLabelMap.CommonNext}</span></li>
        </#if>
        <#if (orderListSize > 0)>
          <li style="padding:5px"><span>${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${orderListSize}</span></li>
        </#if>
        <#if (viewIndex > 1)>
          <li style="padding:3px 0 0 0"><a href="javascript:paginateOrderList('${viewSize}', '${viewIndex-1}', '${requestParameters.hideFields?default("N")}')"  style="font-size:12px" class="smallSubmit">${uiLabelMap.CommonPrevious}</a></li>
        <#else>
          <li><span class="disabled">${uiLabelMap.CommonPrevious}</span></li>
        </#if>
      </#if>
    </ul>
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
      <div align="right">
        <input type="hidden" name="orderIdList" value=""/>
        <input type="hidden" name="screenLocation" value="component://order/widget/ordermgr/OrderPrintScreens.xml#OrderPDF"/>
        <select name="serviceName" onchange="javascript:setServiceName(this);">
           <option value="javascript:void();">&nbsp;</option>
           <option value="<@ofbizUrl>massApproveOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderApproveOrder}</option>
           <option value="<@ofbizUrl>massHoldOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderHold}</option>
           <option value="<@ofbizUrl>massProcessOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderProcessOrder}</option>
           <option value="<@ofbizUrl>massCancelOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderCancelOrder}</option>
           <option value="<@ofbizUrl>massRejectOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderRejectOrder}</option>
           <option value="<@ofbizUrl>massPickOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderPickOrders}</option>
           <option value="<@ofbizUrl>massPrintOrders?hideFields=${requestParameters.hideFields?default('N')}${paramList}</@ofbizUrl>">${uiLabelMap.CommonPrint}</option>
           <option value="<@ofbizUrl>massCreateFileForOrders?hideFields=${requestParameters.hideFields?default('N')}${paramList}</@ofbizUrl>">${uiLabelMap.ContentCreateFile}</option>
        </select>
        <select name="printerName">
           <option value="javascript:void();">&nbsp;</option>
           <#list printers as printer>
           <option value="${printer}">${printer}</option>
           </#list>
        </select>
        <a href="javascript:runAction();" class="buttontext">${uiLabelMap.OrderRunAction}</a>
      </div>

      <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="1%">
            <input type="checkbox" name="checkAllOrders" value="1" onchange="javascript:toggleOrderId(this);"/>
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
          <td width="10%" align="right">Sales Channel</td>
          
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
                 <input type="checkbox" name="orderIdList" value="${orderHeader.orderId}"/>
              </td>
              <td>${orderType.get("description",locale)?default(orderType.orderTypeId?default(""))}</td>
              <td><a href="<@ofbizUrl>orderview?orderId=${orderHeader.orderId}</@ofbizUrl>" class='linkbuttontext'>${orderHeader.orderId}</a></td>
              <td>
                <div>
                  <#if displayParty?has_content>
                      <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                      ${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
                </div>
                <#--
                <div>
                <#if placingParty?has_content>
                  <#assign partyId = placingParty.partyId>
                  <#if placingParty.getEntityName() == "Person">
                    <#if placingParty.lastName?exists>
                      ${placingParty.lastName}<#if placingParty.firstName?exists>, ${placingParty.firstName}</#if>
                    <#else>
                      ${uiLabelMap.CommonNA}
                    </#if>
                  <#else>
                    <#if placingParty.groupName?exists>
                      ${placingParty.groupName}
                    <#else>
                      ${uiLabelMap.CommonNA}
                    </#if>
                  </#if>
                <#else>
                  ${uiLabelMap.CommonNA}
                </#if>
                </div>
                -->
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
                  <a href="${customerDetailLink}${partyId}" class="linkbuttontext">${partyId}</a>
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
            <td colspan='15'><h3>${uiLabelMap.OrderNoOrderFound}</h3></td>
          </tr>
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan='15'><h3>${lookupErrorMessage}</h3></td>
          </tr>
        </#if>
      </table>
    </form>
  </div>
</div>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>
