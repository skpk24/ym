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

    function paginateOrderList(viewSize, viewIndex) {
        document.paginationForm.viewSize.value = viewSize;
        document.paginationForm.viewIndex.value = viewIndex;
        document.paginationForm.submit();
    }

</script>
<script language="JavaScript" type="text/javascript">
<!--//
document.lookuporder.orderId.focus();
//-->
</script>

	<#assign viewIndexMax = Static["java.lang.Math"].ceil(orderListSize?double / viewSize?double)+1>
	<#assign lowIndex = (viewIndex) * viewSize+1>
	<#assign highIndex = (lowIndex + viewSize)?int-1>
	<select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
		<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int+1} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
		<#list 1..viewIndexMax as curViewNum>
		  <option value="javascript:paginateOrderList('${viewSize}', '${curViewNum-1}')">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
		</#list>
	</select>
	<b>
	<#if (orderListSize?int > 0)>
	  <span class="tabletext">${lowIndex} - <#if (orderListSize?int > highIndex?int)>${highIndex?if_exists}<#else> ${orderListSize?if_exists} </#if> ${uiLabelMap.CommonOf} ${orderListSize}</span>
	</#if>
	</b>
	<br>

    <form name="paginationForm" method="post" action="<@ofbizUrl>searchorders</@ofbizUrl>">
      <input type="hidden" name="viewSize"/>
      <input type="hidden" name="viewIndex"/>
      <input type="hidden" name="hideFields"/>
      <input type="hidden" name="viewReport" value="true"/>      
      <#if paramIdList?exists && paramIdList?has_content>
        <#list paramIdList as paramIds>
          <#assign paramId = paramIds.split("=")/>
          <input type="hidden" name="${paramId[0]}" value="${paramId[1]}"/>
        </#list>
      </#if>
    </form>
      <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="5%">${uiLabelMap.OrderOrderId}</td>
          <td width="5%">${uiLabelMap.PartyPartyId}</td>          
          <td width="20%">${uiLabelMap.PartyName}</td>
          <td width="5%" align="right">${uiLabelMap.OrderItemsOrdered}</td>
          <td width="5%" align="right">${uiLabelMap.OrderItemsBackOrdered}</td>
          <td width="5%" align="right">${uiLabelMap.OrderItemsReturned}</td>
          <td width="10%" align="right">${uiLabelMap.OrderRemainingSubTotal}</td>
          <td width="10%" align="right">${uiLabelMap.OrderOrderTotal}</td>
            <#if (requestParameters.filterInventoryProblems?default("N") == "Y") || (requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y") || (requestParameters.filterPOsWithRejectedItems?default("N") == "Y") || (requestParameters.filterPartiallyReceivedPOs?default("N") == "Y")>
              <td width="15%">${uiLabelMap.CommonStatus}</td>
              <td width="5%">${uiLabelMap.CommonFilter}</td>
            <#else>
              <td width="20%">${uiLabelMap.CommonStatus}</td>
            </#if>
          <td width="20%">${uiLabelMap.OrderDate}</td>
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
              <td><#--<a href="/ordermgr/control/orderview?orderId=${orderHeader.orderId}" class='linkbuttontext'>-->${orderHeader.orderId}<#--</a>--></td>
              <td>
                <#if partyId != "_NA_">
                ${partyId}
                  <#--<a href="/partymgr/control/viewprofile?partyId=${partyId}" class="linkbuttontext">${partyId}</a>-->
                <#else>
                  ${uiLabelMap.CommonNA}
                </#if>
              </td>              
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
              <td align="right">${orh.getTotalOrderItemsQuantity()?string.number}</td>
              <td align="right">${orh.getOrderBackorderQuantity()?string.number}</td>
              <td align="right">${orh.getOrderReturnedQuantity()?string.number}</td>
              <td align="right"><#--<@ofbizCurrency amount=orderHeader.remainingSubTotal isoCode=orh.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderHeader.remainingSubTotal?if_exists}</td>
              <td align="right"><#--<@ofbizCurrency amount=orderHeader.grandTotal isoCode=orh.getCurrency()/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderHeader.grandTotal?if_exists}</td>
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
              <td>${orderHeader.orderDate?string("dd-MM-yyyy")}</td>
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
