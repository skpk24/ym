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
 <#assign totalDiscount = 0>
 <#assign flag="">
<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<div class="screenlet">
<#assign numColumns = 8>
  <#--<h3>
      <#assign numColumns = 8>
      <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
          <#assign numColumns = 11>
          <a href="javascript:document.addCommonToCartForm.add_all.value='true';document.addCommonToCartForm.submit()" class="submenutext">${uiLabelMap.OrderAddAllToCart}</a><a href="javascript:document.addCommonToCartForm.add_all.value='false';document.addCommonToCartForm.submit()" class="submenutext">${uiLabelMap.OrderAddCheckedToCart}</a><a href="<@ofbizUrl fullPath="true">createShoppingListFromOrder?orderId=${orderHeader.orderId}&amp;frequency=6&amp;intervalNumber=1&amp;shoppingListTypeId=SLT_AUTO_REODR</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderSendMeThisEveryMonth}</a>
      </#if>
      ${uiLabelMap.OrderOrderItems}
  </h3>-->
  <br/><br/>
  <table id="order-detail-info"  cellpadding="0" cellspacing="0" border="0" width="100%" >
    <thead style="background-color:#a3a3a3; padding:5px 0;">
    <tr>
      <th style="padding:5px 0;"><#--${uiLabelMap.OrderProduct}-->Items</th>
      <#if maySelectItems?default("N") == "Y">
        <th align="left"><#--${uiLabelMap.OrderQtyOrdered}-->Quantity</th>
        <th align="left"><#--${uiLabelMap.OrderQtyPicked}--></th>
        <th align="left"><#--${uiLabelMap.OrderQtyShipped}--></th>
        <th align="left"><#--${uiLabelMap.OrderQtyCanceled}--></th>
      <#else>
        <th></th>
        <th></th>
        <th></th>
        <th align="left"><#--${uiLabelMap.OrderQtyOrdered}--> Quantity</th>
      </#if>
      <th align="left">Our Price(<span class="WebRupee">&#8377;</span>)</th>
      <th align="left">Total(<span class="WebRupee">&#8377;</span>)</th>
      <th align="left"> Savings(<span class="WebRupee">&#8377;</span>)</th>
      <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
      <#--  <th colspan="3"></th> -->
      </#if>
    </tr>
    </thead>
    <tbody>
    <#if getOrderItemsCategoryWise?has_content>
    <#assign keys = orderItems>
   <#assign keysc = 0>
    
    <#list keys as key>
    <#assign categoryName = delegator.findByPrimaryKey("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", key.productCategoryId))?if_exists>
        <#assign categoryId= key.productCategoryId?if_exists>
    	<#if flag==categoryId>
    <#else>
    <tr><td colspan="8" class="special"> 
        
		<#if categoryName?has_content>	
		<span style="font-size:22px;">${categoryName.categoryName?if_exists}</span>
		<#assign flag=categoryId>
		</#if>
   </td>
    <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
   </tr>
   
    
    <#assign orderItems = orderItems>
   
    <#assign counter = 0>
    <#list orderItems as orderItem>
     <#if flag == orderItem.productCategoryId?if_exists><br/>
      <#-- get info from workeffort and calculate rental quantity, if it was a rental item -->
      <#assign rentalQuantity = 1> <#-- no change if no rental item -->
      <#if orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM" && workEfforts?exists>
        <#list workEfforts as workEffort>
          <#if workEffort.workEffortId == orderItem.orderItemSeqId>
            <#assign rentalQuantity = localOrderReadHelper.getWorkEffortRentalQuantity(workEffort)>
            <#assign workEffortSave = workEffort>
            <#break>
          </#if>
        </#list>
      <#else>
        <#assign WorkOrderItemFulfillments = orderItem.getRelated("WorkOrderItemFulfillment")?if_exists>
        <#if WorkOrderItemFulfillments?has_content>
          <#list WorkOrderItemFulfillments as WorkOrderItemFulfillment>
            <#assign workEffortSave = WorkOrderItemFulfillment.getRelatedOneCache("WorkEffort")?if_exists>
            <#break>
           </#list>
        </#if>
      </#if>
      <tr <#if counter%2 == 0>style="background:#ebebeb;"</#if>>
      <#assign counter = counter+0>
        <#if !orderItem.productId?exists || orderItem.productId == "_?_">
          <td >
            ${orderItem.itemDescription?default("")}
          </td>
        <#else>
          <#assign product = orderItem.getRelatedOneCache("Product")?if_exists/> <#-- should always exist because of FK constraint, but just in case -->
          <td class="productinfo">
          <#--${orderItem.productId}-->${product.productName?if_exists}
            <#if product?has_content>
              <#if product.piecesIncluded?exists && product.piecesIncluded?long != 0>
                  [${uiLabelMap.OrderPieces}: ${product.piecesIncluded}]
              </#if>
              <#if (product.quantityIncluded?exists && product.quantityIncluded != 0) || product.quantityUomId?has_content>
                <#assign quantityUom = product.getRelatedOneCache("QuantityUom")?if_exists/>
                  <#--[${uiLabelMap.CommonQuantity}: ${product.quantityIncluded?if_exists} ${((quantityUom.abbreviation)?default(product.quantityUomId))?if_exists}]-->
              </#if>
              <#if (product.weight?exists && product.weight != 0) || product.weightUomId?has_content>
                <#assign weightUom = product.getRelatedOneCache("WeightUom")?if_exists/>
                  [${uiLabelMap.CommonWeight}: ${product.weight?if_exists} ${((weightUom.abbreviation)?default(product.weightUomId))?if_exists}]
              </#if>
              <#if (product.productHeight?exists && product.productHeight != 0) || product.heightUomId?has_content>
                <#assign heightUom = product.getRelatedOneCache("HeightUom")?if_exists/>
                  [${uiLabelMap.CommonHeight}: ${product.productHeight?if_exists} ${((heightUom.abbreviation)?default(product.heightUomId))?if_exists}]
              </#if>
              <#if (product.productWidth?exists && product.productWidth != 0) || product.widthUomId?has_content>
                <#assign widthUom = product.getRelatedOneCache("WidthUom")?if_exists/>
                  [${uiLabelMap.CommonWidth}: ${product.productWidth?if_exists} ${((widthUom.abbreviation)?default(product.widthUomId))?if_exists}]
              </#if>
              <#if (product.productDepth?exists && product.productDepth != 0) || product.depthUomId?has_content>
                <#assign depthUom = product.getRelatedOneCache("DepthUom")?if_exists/>
                  [${uiLabelMap.CommonDepth}: ${product.productDepth?if_exists} ${((depthUom.abbreviation)?default(product.depthUomId))?if_exists}]
              </#if>
            </#if>
            <#if maySelectItems?default("N") == "Y">
              <#assign returns = orderItem.getRelated("ReturnItem")?if_exists>
              <#if returns?has_content>
                <#list returns as return>
                  <#assign returnHeader = return.getRelatedOne("ReturnHeader")>
                  <#if returnHeader.statusId != "RETURN_CANCELLED">
                    <#if returnHeader.statusId == "RETURN_REQUESTED" || returnHeader.statusId == "RETURN_APPROVED">
                      <#assign displayState = "Return Pending">
                    <#else>
                      <#assign displayState = "Returned">
                    </#if>
                    ${displayState} (#${return.returnId})
                  </#if>
                </#list>
              </#if>
            </#if>
          </td>
          <#if !(maySelectItems?default("N") == "Y")>
            <td></td>
            <td></td>
            <td></td>
          </#if>
          <td align="center">
            ${orderItem.quantity?string.number}
          </td>
          <#if maySelectItems?default("N") == "Y">
          <td align="center">
            <#assign pickedQty = localOrderReadHelper.getItemPickedQuantityBd(orderItem)>
            <#--<#if pickedQty gt 0 && orderHeader.statusId == "ORDER_APPROVED">${pickedQty?default(0)?string.number}<#else>${pickedQty?default(0)?string.number}</#if>-->
          </td>
          <td align="center">
            <#assign shippedQty = localOrderReadHelper.getItemShippedQuantity(orderItem)>
            <#--${shippedQty?default(0)?string.number}-->
          </td>
          <td align="center">
            <#assign canceledQty = localOrderReadHelper.getItemCanceledQuantity(orderItem)>
            <#--${canceledQty?default(0)?string.number}-->
          </td>
          </#if>
          <td align="center">
             <#if orderItem.unitListPrice gt 0>
           ${orderItem.unitListPrice?string.number}
           <#else>
            ${orderItem.unitPrice?string.number}
            </#if>
          </td>
          <td align="center">
            <#if workEfforts?exists>
             ${((localOrderReadHelper.getOrderItemTotal(orderItem))*rentalQuantity)?string.number}
             
            <#else>
            ${localOrderReadHelper.getOrderItemTotal(orderItem)?string.number}
            
            </#if>
          </td>
           
          <td align="center">
          <#assign discount=0>
           <#if orderItem.unitListPrice gt 0>
            
         <#assign discount=(orderItem.unitListPrice-orderItem.unitPrice)*orderItem.quantity>
         <#assign totalDiscount=totalDiscount+discount>
           
          </#if>
          ${discount?if_exists}
          
          </td>
          
          <#if maySelectItems?default("N") == "Y" && roleTypeId?if_exists == "PLACING_CUSTOMER">
            <#--<td></td>
            <td>
              <input name="item_id" value="${orderItem.orderItemSeqId}" type="checkbox"/>
            </td> 
            <td></td>-->
          </#if>
        </#if>
      </tr>
      <#-- now cancel reason and comment field -->
      <#if maySelectItems?default("N") == "Y" && (orderHeader.statusId != "ORDER_SENT" && orderItem.statusId != "ITEM_COMPLETED" && orderItem.statusId != "ITEM_CANCELLED" && pickedQty == 0)>
       <#-- <tr>
          <td colspan="7">${uiLabelMap.OrderReturnReason}
            <select name="irm_${orderItem.orderItemSeqId}" class="selectBox">
              <option value=""></option>
              <#list orderItemChangeReasons as reason>
                <option value="${reason.enumId}">${reason.get("description",locale)?default(reason.enumId)}</option>
              </#list>
            </select>
            ${uiLabelMap.CommonComments}
            <input class="inputBox" type="text" name="icm_${orderItem.orderItemSeqId}" value="" size="30" maxlength="60"/>
          </td>
          <td colspan="4"><a href="javascript:document.addCommonToCartForm.action='<@ofbizUrl>cancelOrderItem</@ofbizUrl>';document.addCommonToCartForm.submit()" class="buttontext">${uiLabelMap.CommonCancel}</a>
            <input type="hidden" name="orderItemSeqId" value="${orderItem.orderItemSeqId}"/>
          </td>
        </tr>-->
      </#if>
      <#-- show info from workeffort if it was a rental item -->
      <#if orderItem.orderItemTypeId == "RENTAL_ORDER_ITEM">
        <#if workEffortSave?exists>
          <tr><td></td><td colspan="${numColumns?if_exists}">${uiLabelMap.CommonFrom}: ${workEffortSave.estimatedStartDate?string("yyyy-MM-dd")} ${uiLabelMap.CommonUntil} ${workEffortSave.estimatedCompletionDate?string("yyyy-MM-dd")} ${uiLabelMap.CommonFor} ${workEffortSave.reservPersons} ${uiLabelMap.CommonPerson}(s)</td></tr>
        </#if>
      </#if>
      <#-- now show adjustment details per line item -->
      <#assign itemAdjustments = localOrderReadHelper.getOrderItemAdjustments(orderItem)>
      <#list itemAdjustments as orderItemAdjustment>
        <tr>
          <td>
            ${uiLabelMap.EcommerceAdjustment}: ${localOrderReadHelper.getAdjustmentType(orderItemAdjustment)}
            <#if orderItemAdjustment.description?has_content>: ${orderItemAdjustment.description}</#if>
            <#if orderItemAdjustment.orderAdjustmentTypeId == "SALES_TAX">
              <#if orderItemAdjustment.primaryGeoId?has_content>
                <#assign primaryGeo = orderItemAdjustment.getRelatedOneCache("PrimaryGeo")/>
                <#if primaryGeo.geoName?has_content>
                  ${uiLabelMap.OrderJurisdiction}: ${primaryGeo.geoName} [${primaryGeo.abbreviation?if_exists}]
                </#if>
                <#if orderItemAdjustment.secondaryGeoId?has_content>
                  <#assign secondaryGeo = orderItemAdjustment.getRelatedOneCache("SecondaryGeo")/>
                  (${uiLabelMap.CommonIn}: ${secondaryGeo.geoName} [${secondaryGeo.abbreviation?if_exists}])
                </#if>
              </#if>
              <#if orderItemAdjustment.sourcePercentage?exists>${uiLabelMap.EcommerceRate}: ${orderItemAdjustment.sourcePercentage}</#if>
              <#if orderItemAdjustment.customerReferenceId?has_content>${uiLabelMap.OrderCustomerTaxId}: ${orderItemAdjustment.customerReferenceId}</#if>
              <#if orderItemAdjustment.exemptAmount?exists>${uiLabelMap.EcommerceExemptAmount}: ${orderItemAdjustment.exemptAmount}</#if>
            </#if>
          </td>
          <td colspan="5"></td>
          <td>
          	<#--><@ofbizCurrency amount=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment) isoCode=currencyUomId/>-->
          	<#assign amount2=localOrderReadHelper.getOrderItemAdjustmentTotal(orderItem, orderItemAdjustment)/>
            <span class="WebRupee">&#8377;</span>&nbsp;${amount2?if_exists}
          </td>
          <td></td>
          <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
        </tr>
      </#list>
      <#-- show the order item ship group info -->
      <#assign orderItemShipGroupAssocs = orderItem.getRelated("OrderItemShipGroupAssoc")?if_exists>
      <!--<#if orderItemShipGroupAssocs?has_content>
        <#list orderItemShipGroupAssocs as shipGroupAssoc>
          <#assign shipGroup = shipGroupAssoc.getRelatedOne("OrderItemShipGroup")?if_exists>
          <#assign shipGroupAddress = (shipGroup.getRelatedOne("PostalAddress"))?if_exists>
          <tr>
            <td>
              ${uiLabelMap.OrderShipGroup}: [${shipGroup.shipGroupSeqId}] ${shipGroupAddress.address1?default("N/A")}
            </td>
            <td>
              ${shipGroupAssoc.quantity?string.number}
            </td>
            <td colspan="${numColumns - 2}"></td>
          </tr>
        </#list>
      </#if>-->
      </#if>
    </#list>
     
    </#if>
     
     <#assign keysc = keysc+1>
    </#list>
      </#if>
    <#if orderItems?size == 0 || !orderItems?has_content>
      <tr><td colspan="${numColumns}">${uiLabelMap.OrderSalesOrderLookupFailed}</td></tr>
    </#if>
    <tr><td colspan="8"></td></tr>
    </tbody>
    <tfoot>
    <tr class="tfootborder">
      <th colspan="7" style="border-top:2px solid #a6a3a3;margin-right:10px;" align="right">${uiLabelMap.CommonSubtotal}</th>
      <td style="border-top:2px solid #a6a3a3;" align="center"><#--<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderSubTotal?if_exists}</td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <#list headerAdjustmentsToShow as orderHeaderAdjustment>
      <tr>
        <th colspan="7">${localOrderReadHelper.getAdjustmentType(orderHeaderAdjustment)}</th>
        <td><span class="WebRupee">&#8377;</span>&nbsp;${localOrderReadHelper.getOrderAdjustmentTotal(orderHeaderAdjustment)?string.number}</td>
        <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
      </tr>
    </#list>
    <tr>
      <th colspan="7" align="right" style="margin-right:10px;"><#--${uiLabelMap.OrderShippingAndHandling}--> Delivery Charge</th>
      <td align="center"><#--<@ofbizCurrency amount=orderShippingTotal isoCode=currencyUomId/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderShippingTotal?if_exists}</td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <#--<tr>
      <th colspan="7">${uiLabelMap.OrderSalesTax}</th>
      <td><#--<@ofbizCurrency amount=orderTaxTotal isoCode=currencyUomId/>--><#--<span class="WebRupee">&#8377;</span>&nbsp;${orderTaxTotal?if_exists}</td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>-->
    <tr>
      <td colspan="3"></td>
      <#if maySelectItems?default("N") == "Y">
        <td colspan="${numColumns - 6}"></td>
        <td colspan="3"></td>
      <#else>
        <td colspan="${numColumns - 3}"></td>
      </#if>
    </tr>
      <#if billingAmt?has_content && billingAmt!=0>
    <tr>
      <th colspan="7" align="right" style="margin-right:10px;">Total Amount</th>
      <td align="center">
       <#-- <@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/>--><span class="WebRupee">&#8377;</span>&nbsp;${orderGrandTotal?if_exists}
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
     <tr>
      <th colspan="7" align="right" style="margin-right:10px;">Total Discount</th>
      <td align="center">
       <#-- <@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/>--><span class="WebRupee">&#8377;</span>&nbsp;${totalDiscount?if_exists}
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    </#if>
    
    <#if billingAmt?has_content && billingAmt!=0 >
    
    <tr>
    
      <th colspan="7">You Saved</th>
      <td>
       <#-- <@ofbizCurrency amount=billingAmt isoCode=currencyUomId/>--><span class="WebRupee">&#8377;</span>&nbsp;${billingAmt?if_exists}
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    </#if>
    <#if billingAmt?has_content && billingAmt!=0>
      <tr>
      <th colspan="7" style="border-top:2px solid #a6a3a3; border-bottom:2px solid #a6a3a3;margin-right:10px;" align="right">Total Amount</th>
      <td style="border-top:2px solid #a6a3a3; border-bottom:2px solid #a6a3a3;" align="center">
       <span class="WebRupee">&#8377;</span>&nbsp;${total}
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    <#else>
     <tr>
      <th colspan="7"  style="border-top:2px solid #a6a3a3; border-bottom:2px solid #a6a3a3;margin-right:10px;" align="right">Total Amount</th>
      <td  style="border-top:2px solid #a6a3a3; border-bottom:2px solid #a6a3a3;" align="center">
       <span class="WebRupee">&#8377;</span>&nbsp;${orderGrandTotal}
      </td>
      <#if maySelectItems?default("N") == "Y"><td colspan="3"></td></#if>
    </tr>
    </#if>
    </tfoot>
  </table>
</div>

<div>

 <#if billingAmt?has_content && billingAmt!=0>
 Your Credit under YouMart Savings <span class="WebRupee">&#8377;</span>&nbsp;${billingAmt}
 </#if>
</div>



