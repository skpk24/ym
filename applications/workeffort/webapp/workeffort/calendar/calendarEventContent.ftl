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

<#if workEffort.workEffortTypeId == "PROD_ORDER_HEADER">
  <a f="/manufacturing/control/ShowProductionRun?productionRunId=${workEffort.workEffortId}" class="event">
    ${workEffort.workEffortId}
  </a>
  &nbsp;${workEffort.workEffortName?default("Undefined")}
  <#if workOrderItemFulfillments?has_content>
    <#list workOrderItemFulfillments as workOrderItemFulfillment>
      <br/>${uiLabelMap.OrderOrderId}: <a href="/ordermgr/control/orderview?orderId=${workOrderItemFulfillment.orderId}" class="event">${workOrderItemFulfillment.orderId} / ${workOrderItemFulfillment.orderItemSeqId}</a>
      <#assign orderItemAndShipGroupAssocs = delegator.findByAnd("OrderHeaderItemAndShipGroup", {"orderId", workOrderItemFulfillment.orderId, "orderItemSeqId", workOrderItemFulfillment.orderItemSeqId})?if_exists/>
      <#list orderItemAndShipGroupAssocs as orderItemAndShipGroupAssoc>
        <#if orderItemAndShipGroupAssoc.shipByDate?has_content>ff
          ${uiLabelMap.OrderShipBeforeDate}: ${orderItemAndShipGroupAssoc.shipByDate}
        </#if>
      </#list>
    </#list>
  </#if>
<#elseif workEffort.workEffortTypeId == "PROD_ORDER_TASK">
  <a href="/manufacturing/control/ShowProductionRun?productionRunId=${workEffort.workEffortParentId}" class="event">
    ${workEffort.workEffortParentId} / ${workEffort.workEffortId}
  </a>
  &nbsp;${workEffort.workEffortName?default("Undefined")}<#if workEffort.reservPersons?exists>&nbsp;Persons:${workEffort.reservPersons}</#if>
  <#if parentWorkOrderItemFulfillments?has_content>
    <#list parentWorkOrderItemFulfillments as parentWorkOrderItemFulfillment>
      <br/>${uiLabelMap.OrderOrderId}: <a href="/ordermgr/control/orderview?orderId=${parentWorkOrderItemFulfillment.orderId}" class="event">${parentWorkOrderItemFulfillment.orderId} / ${parentWorkOrderItemFulfillment.orderItemSeqId}</a>
      <#assign orderItemAndShipGroupAssocs = delegator.findByAnd("OrderHeaderItemAndShipGroup", {"orderId", parentWorkOrderItemFulfillment.orderId, "orderItemSeqId", parentWorkOrderItemFulfillment.orderItemSeqId})?if_exists/>
      <#list orderItemAndShipGroupAssocs as orderItemAndShipGroupAssoc>
        <#if orderItemAndShipGroupAssoc.shipByDate?has_content>
          ${uiLabelMap.OrderShipBeforeDate}: ${orderItemAndShipGroupAssoc.shipByDate}
        </#if>
      </#list>
    </#list>
  </#if>
<#else>
  <a href="<@ofbizUrl>sfaCalender?form=edit&amp;parentTypeId=${parentTypeId?if_exists}&amp;period=${periodType?if_exists}&amp;start=${parameters.start?if_exists}&amp;workEffortId=${workEffort.workEffortId}${addlParam?if_exists}${urlParam?if_exists}</@ofbizUrl>" class="event">
    ${workEffort.workEffortId}
  </a>
  &nbsp;${workEffort.workEffortName?default("")}
</#if>
