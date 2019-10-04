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

<div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="h3" >
         <div class="createButton">
              <a class="buttontext" href="javascript:document.OrderHold.submit()">${uiLabelMap.OrderHold}</a>
              </div>
            <#if orderHeader.externalId?has_content>
               <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
            </#if>
            <#assign orderType = orderHeader.getRelatedOne("OrderType")/>
            
            
            &nbsp;${orderType?if_exists.get("description", locale)?default(uiLabelMap.OrderOrder)}&nbsp;#<a href="<@ofbizUrl>orderview?orderId=${orderId}</@ofbizUrl>">${orderId}</a> ${externalOrder?if_exists} &nbsp;&nbsp;<a  href="<@ofbizUrl>order.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank" class="buttontext">PDF  </a>
            
			
            <#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
              
              <div class="createButton">
              <a class="buttontext" href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
              </div>
              
              <form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_APPROVED"/>
                <input type="hidden" name="setItemStatus" value="Y"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
            
            <#elseif currentStatus.statusId == "ORDER_APPROVED">
              
             
              
              <form name="OrderHold" method="post" action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_HOLD"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
            
            <#elseif currentStatus.statusId == "ORDER_HOLD">
              
              <div class="createButton">
              <a class="buttontext" href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
              </div>
              
              <form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus/orderview</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_APPROVED"/>
                <input type="hidden" name="setItemStatus" value="Y"/>
                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
              </form>
            </#if>
            
            
            <#if setOrderCompleteOption>
            <div class="createButton">
              <a class="buttontext" href="javascript:document.OrderCompleteOrder.submit()">${uiLabelMap.OrderCompleteOrder}</a>
              </div>
              <form name="OrderCompleteOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
                <input type="hidden" name="statusId" value="ORDER_COMPLETED"/>
                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
              </form>
            </#if>
        
        </div>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
        <table class="basic-table" cellspacing='1' width="100%">
            <#if orderHeader.orderName?has_content>
            <tr>
              <td align="left" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderOrderName}</td>
             
              <td valign="top" width="100%">${orderHeader.orderName}</td>
            </tr>
            
            </#if>
            <#-- order status history -->
            <tr>
              <td text-align="left" valign="top" width="15%" float="left" class="orderlabeltext">&nbsp;${uiLabelMap.OrderStatusHistory}</td>
              
              <td valign="top" width="100%" class="ordersubtext">
                ${uiLabelMap.OrderCurrentStatus}: ${currentStatus.get("description",locale)}
                <#if orderHeaderStatuses?has_content>
                 
                  <#list orderHeaderStatuses as orderHeaderStatus>
                    <#assign loopStatusItem = orderHeaderStatus.getRelatedOne("StatusItem")>
                    <#assign userlogin = orderHeaderStatus.getRelatedOne("UserLogin")>
                    <div>
                      ${loopStatusItem.get("description",locale)} - ${orderHeaderStatus.statusDatetime?default("0000-00-00 00:00:00")?string}
                      &nbsp;
                      ${uiLabelMap.CommonBy} - <#--${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, userlogin.getString("partyId"), true)}--> [${orderHeaderStatus.statusUserLogin}]
                    </div>
                  </#list>
                </#if>
              </td>
            </tr>
            
            
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderDateOrdered}</td>
              
              <td valign="top" width="100%" class="ordersubtext">${orderHeader.orderDate.toString()}</td>
            </tr>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.CommonCurrency}</td>
              
              <td valign="top" width="100%" class="ordersubtext">${orderHeader.currencyUom?default("???")}</td>
            </tr>
            <#if orderHeader.internalCode?has_content>
            <br>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderInternalCode}</td>
              
              <td valign="top" width="100%" class="ordersubtext">${orderHeader.internalCode}</td>
            </tr>
            </#if>
           
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderSalesChannel}</td>
              
              <td valign="top" width="100%" class="ordersubtext">
                  <#if orderHeader.salesChannelEnumId?has_content>
                    <#assign channel = orderHeader.getRelatedOne("SalesChannelEnumeration")>
                    ${(channel.get("description",locale))?default("N/A")}
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderProductStore}</td>
              
              <td valign="top" width="100%" class="ordersubtext">
                  <#if orderHeader.productStoreId?has_content>
                    <a href="/catalog/control/EditProductStore?productStoreId=${orderHeader.productStoreId}${externalKeyParam}" target="catalogmgr" class="buttontext1">${orderHeader.productStoreId}</a>
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr>
            <tr><td colspan="3"><br/></td></tr>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderOriginFacility}</td>
             
              <td valign="top" width="100%" class="ordersubtext">
                  <#if orderHeader.originFacilityId?has_content>
                    <a href="/facility/control/EditFacility?facilityId=${orderHeader.originFacilityId}${externalKeyParam}" target="facilitymgr" class="buttontext">${orderHeader.originFacilityId}</a>
                  <#else>
                    ${uiLabelMap.CommonNA}
                  </#if>
              </td>
            </tr>
            
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderCreatedBy}</td>
              
              <td valign="top" width="80%">
                  <#if orderHeader.createdBy?has_content>
                    <a href="/partymgr/control/viewprofile?userlogin_id=${orderHeader.createdBy}${externalKeyParam}" target="partymgr" class="buttontext1">${orderHeader.createdBy}</a>
                  <#else>
                    ${uiLabelMap.CommonNotSet}
                  </#if>
              </td>
            </tr>
            <#if orderItem.cancelBackOrderDate?exists>
             
              <tr>
                <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.FormFieldTitle_cancelBackOrderDate}</td>
                
                <td valign="top" width="80%">${orderItem.cancelBackOrderDate?if_exists}</td>
              </tr>
            </#if>
            <#if distributorId?exists>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderDistributor}</td>
              <td valign="top" width="80%">
                  <#assign distPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", distributorId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                  ${distPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
              </td>
            </tr>
            </#if>
            <#if affiliateId?exists>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderAffiliate}</td>
              <td valign="top" width="80%" class="ordersubtext">
                  <#assign affPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", affiliateId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                Web${affPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
                
              </td>
            </tr>
            </#if>
            <#if orderContentWrapper.get("IMAGE_URL")?has_content>
            <tr>
              <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.OrderImage}</td>
              <td valign="top" width="80%">
                  <a href="<@ofbizUrl>viewimage?orderId=${orderId}&orderContentTypeId=IMAGE_URL</@ofbizUrl>" target="_orderImage" class="buttontext">${uiLabelMap.OrderViewImage}</a>
              </td>
            </tr>
            </#if>
            <#if "SALES_ORDER" == orderHeader.orderTypeId>
              <form action="setOrderReservationPriority" method="post" name="setOrderReservationPriority">
                <input type = "hidden" name="orderId" value="${orderId}"/>
                <tr>
                  <td align="right" valign="top" width="15%" class="orderlabeltext">&nbsp;${uiLabelMap.FormFieldTitle_priority}</td>
                  <td valign="top" width="80%">
                    <select name="priority">
                      <option value="1">${uiLabelMap.CommonHigh}</option>
                      <option value="2"selected>${uiLabelMap.CommonNormal}</option>
                      <option value="3">${uiLabelMap.CommonLow}</option>
                    </select>
                    <input type="submit" class="smallSubmit" value="${uiLabelMap.FormFieldTitle_reserveInventory}"/>
                  </td>
                </tr>
              </form>
            </#if>
        </table>
    </div>
</div>

