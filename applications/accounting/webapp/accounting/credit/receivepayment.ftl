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

<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
<div class="screenlet">
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">Receive Offline Payments</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <a href="<@ofbizUrl>authview/showReceiveCredit?orderId=${parameters.orderId?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
      <a href="javascript:document.paysetupform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a>

      <form method="post" action="<@ofbizUrl>receiveOfflinePayments/showReceiveCredit?orderId=${parameters.orderId?if_exists}</@ofbizUrl>" name="paysetupform">
        <#if requestParameters.workEffortId?exists>
            <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}">
        </#if>
        <table class="basic-table" cellspacing='0'>
          <tr class="header-row">
            <td width="30%" align="right">Payment Type</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1">Order Amount</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%">Order Reference</td>
          </tr>
          <#list paymentMethodTypes as payType>
        <#if  payType.paymentMethodTypeId.equals("CASH") || payType.paymentMethodTypeId.equals("COMPANY_ACCOUNT") || payType.paymentMethodTypeId.equals("COMPANY_CHECK") ||payType.paymentMethodTypeId.equals("CREDIT_CARD") > 
          <tr>
            <td width="30%" align="right">${payType.get("description",locale)?default(payType.paymentMethodTypeId)}</td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="1"><input type="text" size="7" name="${payType.paymentMethodTypeId}_amount"></td>
            <td width="1">&nbsp;&nbsp;&nbsp;</td>
            <td width="70%"><input type="text" size="15" name="${payType.paymentMethodTypeId}_reference"></td>
          </tr>
         </#if> 
          </#list>
        </table>
      </form>

      <a href="<@ofbizUrl>authview/showReceiveCredit?orderId=${parameters.orderId?if_exists}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonBack}</a>
      <a href="javascript:document.paysetupform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a>
    </div>
</div>
<br/>
<#else>
  <h3>${uiLabelMap.OrderViewPermissionError}</h3>
</#if>