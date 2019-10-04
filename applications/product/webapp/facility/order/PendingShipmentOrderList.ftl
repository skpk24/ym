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

<div id="findOrdersList" class="screenlet">
  <div class="screenlet-title-bar">
    <div class="h3">
    
    
    
      
      
      <#assign listSize = state.getSize()>
      <div class="createButton">
      <#if (listSize > 10)>
        <a class="buttontext" href="/ordermgr/control/orderlist?viewIndex=${state.getViewIndex() + 1}&amp;viewSize=${state.getViewSize()}&amp;filterDate=${filterDate?if_exists}">${uiLabelMap.CommonMore}</a>
      </#if>
      
      
      </div>
      <#if orderHeaderList?has_content> 
        <div style="float:right; padding-right:5px;"> 1-${orderHeaderList.size()} ${uiLabelMap.CommonOf} ${state.getSize()}</div>
      </#if>
      ${uiLabelMap.OrderOrderReceivedOn} <#--${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(filterDate)}-->${filterDate?string("dd-MM-yyyy")}
    </div>
    <br class="clear"/>
  </div>
   <#assign curr_date_str=Static["org.ofbiz.base.util.UtilDateTime"].toDateString(filterDate)/>
  
  
  
  
  <div class="screenlet-body">
  
    <#if orderHeaderList?has_content>
      
      <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="10%">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
          <td width="15%">${uiLabelMap.OrderOrderBillToParty}</td>
          <td width="20%">${uiLabelMap.OrderProductStore}</td>
          <td width="10%">${uiLabelMap.CommonAmount}</td>
          <td width="15%">${uiLabelMap.OrderTrackingCode}</td>
          <td width="20%">${uiLabelMap.CommonStatus}</td>
           <td width="20%">Pack Order</td>
        
        </tr>
        <#assign alt_row = false>
       
   
      
        <#list orderHeaderList as orderHeader>
        
         
          <#assign odr_tdy_date_str=Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderHeader.orderDate)/>
         
          <#assign status = orderHeader.getRelatedOneCache("StatusItem")>
         <#assign statusId=status.statusId?if_exists/>
          
          <#if statusId == "ORDER_APPROVED">
          
          <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
          <#assign billToParty = orh.getBillToParty()?if_exists>
          <#if billToParty?has_content>
            <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
            <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
          </#if>
          <#assign productStore = orderHeader.getRelatedOneCache("ProductStore")?if_exists />
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/orderview?orderId=${orderHeader.orderId}" class="linkbuttontext">${orderHeader.orderId}</a></td>
            <td>${billTo?if_exists}</td>
            <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
            <td><#--<@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderHeader.grandTotal?if_exists}</td>
            <td>
              <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder")>
              <#list trackingCodes as trackingCode>
                <#if trackingCode?has_content>
                  <a href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
                </#if>
              </#list>
            </td>
            <td>${orderHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
          
          <td><a href="PackOrderForShipment?orderId=${orderHeader.orderId}&facilityId=${facilityId?if_exists}" class="buttontext">Pack Order</a></td>
          
          </tr>
	
	
	<#else>
	</#if>
	
	</#list>
      </table>
    <#else>
      <h3>${uiLabelMap.OrderNoOrderFound}</h3>
    </#if>
 

 
 
  </div>
</div>