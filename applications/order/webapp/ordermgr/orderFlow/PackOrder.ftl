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
    function clearLine(facilityId, orderId, orderItemSeqId, productId, shipGroupSeqId, inventoryItemId, packageSeqId) {
        document.clearPackLineForm.facilityId.value = facilityId;
        document.clearPackLineForm.orderId.value = orderId;
        document.clearPackLineForm.orderItemSeqId.value = orderItemSeqId;
        document.clearPackLineForm.productId.value = productId;
        document.clearPackLineForm.shipGroupSeqId.value = shipGroupSeqId;
        document.clearPackLineForm.inventoryItemId.value = inventoryItemId;
        document.clearPackLineForm.packageSeqId.value = packageSeqId;
        document.clearPackLineForm.submit();
    }
    
    function modify()
    {
    
   var productIds= document.getElementById("productIds").value;
   var productId= document.getElementById("productIdChaeck").value;
  
   var n=productIds.split(" ");

   for (var i = 0; i < n.length; i++)
{
	if(n[i] == productId)
	{
	document.getElementById(productId).checked = true;
	document.getElementById("productIdChaeck").value="";
	}
	
}
   
    }
</script>
<script>
	function clearAll(){
		var productIds = document.getElementById("productIds").value;
		var productId = productIds.split(" ");
		for(var i=0; i< productId.length;i++)
		{
			document.getElementById(productId[i]).checked = false;
			var rowKey = document.getElementById("rowKey_"+productId[i]).value;
			document.getElementById("qty_"+rowKey).value = "0";
		}
	}
	function checkProductIds(){
		var barcodeId = document.getElementById("barcodeId").value;
		barcodeId = barcodeId.trim();
		var  param = 'barcodeId=' + barcodeId;
		jQuery.ajax({url: '/ordermgr/control/getProductIdFromBarCode',
		         data: { barcodeId: barcodeId, orderId: "${orderId?if_exists}" },
		         type: 'POST',
		         async: false,
		         success: function(data) {
	         		if(data == "blankBarCode")
	         		{	
	         			document.getElementById("barcodeError").innerHTML = "<font color='red'>Please Enter Bar Code</font>";
	         		}
	         		else if(data == "productNotFound")
	         		{
	         			document.getElementById("barcodeError").innerHTML = "<font color='red'>Product Not Found For Bar code : "+barcodeId+"</font>";
	         		}
	         		else if(data == "sessionExpired")
	         		{
	         			var r=confirm("Session Expired, Please Login again");
						if (r==true)
						  {
						       	location.reload();
						  }
	         		}
	         		else
         			{
         				var orderItmId_prdId = data.split("++");
         				for (i = 0; i < orderItmId_prdId.length; i++) { 
     					if(orderItmId_prdId[i] != "" && document.getElementById(orderItmId_prdId[i]) != null)
         					{
						    	var returnedData = packQty(orderItmId_prdId[i]);
						    	if(returnedData) break;
						    }
						}
         			}
		         },
		         complete:  function() { 
		         },
		        error: function(data) {
		        }
    		});
		return false;
	} 
	
	function packQty(data){
		document.getElementById(data).checked = true;
		document.getElementById("barcodeId").value = "";
		var rowKey = document.getElementById("rowKey_"+data).value;
		var qtyRow = document.getElementById("qty_"+rowKey);
		var qty = qtyRow.value;
		qty = (parseInt(qty)+1)+"";
		
		var orderItemQuantity = document.getElementById("orderItemQuantity_"+data).value;
		document.getElementById("checkBoxError").innerHTML = "<font color='red'></font>";
		document.getElementById("qtypackedError_"+data).innerHTML = "<font color='red'></font>";

		if(parseInt(orderItemQuantity) >= parseInt(qty))
		{
			qtyRow.value = qty;
			document.getElementById("barcodeError").innerHTML = "<font color='red'></font>";
			return true;
		}else{
			   document.getElementById("barcodeError").innerHTML = "<font color='red'>Can not Ship more than ordered Qty</font>";
			   return false;
			 }
	}
	
	function validateForm(){
		var vehicleNumber = document.getElementById("vehicleNumber").value;
		var errorFlag = false;
		if(vehicleNumber == null || vehicleNumber.trim() == "")
		{
			document.getElementById("vehicleNumberError").innerHTML = "<font color='red'>Please Select Vehicle Number</font>";
			document.getElementById("vehicleNumberMngmt").innerHTML = "";
			errorFlag = true;
		}
		var productIds = document.getElementById("productIds").value;
		var productId = productIds.split(" ");
		var checked = false;
		var partialPacking = true;
		for(var i=0; i< productId.length;i++)
		{
			if(productId[i] == "" || document.getElementById(productId[i]) == null) continue;
			
			if(document.getElementById(productId[i]).checked)
			       checked = true;
			<#--if orderHeader?has_content && orderHeader.statusId = "ORDER_APPROVED"--> 
				var orderItemQuantity = document.getElementById("orderItemQuantity_"+productId[i]).value;
				var packedQuantity = document.getElementById("packedQuantity_"+productId[i]).value;
				var rowKey = document.getElementById("rowKey_"+productId[i]).value;
				var qtyToPack = document.getElementById("qty_"+rowKey).value;
				if((parseFloat(orderItemQuantity) + parseFloat(packedQuantity)) != parseFloat(qtyToPack))
				{
					    partialPacking = false;
					    document.getElementById("qtypackedError_"+productId[i]).innerHTML = "<font color='red'>Ordered Quantity not equal to pack quantity</font>";
				}
			<#--/#if-->
		}
		if(checked == false)
		{
			document.getElementById("checkBoxError").innerHTML = "<font color='red'>Please Select atleast One Product To Ship</font>";
			errorFlag = true;
			partialPacking = false;
		}
		if(partialPacking == false) errorFlag = true;
		if(errorFlag == true) return false;
		
		if(partialPacking == false)
			document.getElementById("partialPacking").value = "Y";
		else
			document.getElementById("partialPacking").value = "N";

		if(document.getElementById("MessageDiv").style.display != "block" ){
		   document.getElementById("MessageDiv").style.display = "block";
		}else{
		   document.getElementById("MessageDiv").style.display = "none";
		}
			
		return true;
	}
	function vehicleThreshold(){
		var vehicleNumber = document.getElementById("vehicleNumber").value;
		vehicleNumber = vehicleNumber.trim();
		if(vehicleNumber == null || vehicleNumber.trim() == "")
		{
			document.getElementById("vehicleNumberError").innerHTML = "<font color='red'>Please Select Vehicle Number</font>";
		}else{
			document.getElementById("vehicleNumberError").innerHTML = "<font color='red'></font>";
		}	
		<#--var  param = 'vehicleNumber=' + vehicleNumber;
		jQuery.ajax({url: '/ordermgr/control/thresholdForVehicle',
		         data: param,
		         type: 'post',
		         async: false,
		         success: function(data) {
	         		if(data == "available")
	         		{	
	         			document.getElementById("vehicleNumberError").innerHTML = "<font color='green'>Available</font>";
	         			document.getElementById("vehicleNumberMngmt").innerHTML = "";
	         		}
	         		else if(data == "orderThresholdReached")
	         		{
	         			document.getElementById("vehicleNumberError").innerHTML = "<font color='red'>Order Threshold Reached</font>";
	         			document.getElementById("vehicleNumberMngmt").innerHTML = "<a href='<@ofbizUrl>EditVehicleDetails?vehicleNumber="+123456+"</@ofbizUrl>'>Click here To Increasee Threshold</a>"
	         		}
	         		else if(data == "weightThresholdReached")
         			{
         				document.getElementById("vehicleNumberError").innerHTML = "<font color='red'>Weight Threshold Reached</font>";
         				document.getElementById("vehicleNumberMngmt").innerHTML = "<a href='<@ofbizUrl>EditVehicleDetails?vehicleNumber="+123456+"</@ofbizUrl>'>Click here To Increasee Threshold</a>"
         			}
         			else
         			{
         				var oidWt = data.split("   ");
         				var orderThreshold = "";
         				if(oidWt.length >= 1)
     					{
     						orderThreshold = oidWt[0].substring(3);
     					}
     					if(oidWt.length >= 2)
     					{
     						weightThreshold = oidWt[1].substring(3);
     					}
     					var message = "Order Threshold Left -- "+parseInt(orderThreshold)+"<br /> Weight Threshold Left -- "+parseInt(weightThreshold);
         				document.getElementById("vehicleNumberError").innerHTML = "<font color='red'>"+message+"</font>";
         			}
		         },
		         complete:  function() { 
		         },
		        error: function(data) {
		        }
    		});-->
		return false;
	}
</script>


<#if security.hasEntityPermission("FACILITY", "_VIEW", session)>
    <#assign showInput = requestParameters.showInput?default("Y")>
    <#assign hideGrid = requestParameters.hideGrid?default("N")>

    <#if (requestParameters.forceComplete?has_content && !invoiceIds?has_content)>
        <#assign forceComplete = "true">
        <#assign showInput = "Y">
    </#if>

    <div class="screenlet">
       <#-- <div class="screenlet-title-bar">
            <ul>
                <li class="h3">${uiLabelMap.ProductPackOrder}&nbsp;in&nbsp;${facility.facilityName?if_exists} [${facilityId?if_exists}]</li>
            </ul>
            <br class="clear"/>
        </div>-->
        <div class="screenlet-body">
            <#if invoiceIds?has_content>
                <div>
                ${uiLabelMap.CommonView} <a href="<@ofbizUrl>/PackingSlip.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductPackingSlip}</a> ${uiLabelMap.CommonOr}
                ${uiLabelMap.CommonView} <a href="<@ofbizUrl>/ShipmentBarCode.pdf?shipmentId=${shipmentId}</@ofbizUrl>" target="_blank" class="buttontext">${uiLabelMap.ProductBarcode}</a> ${uiLabelMap.CommonFor} ${uiLabelMap.ProductShipmentId} <a href="<@ofbizUrl>/ViewShipment?shipmentId=${shipmentId}</@ofbizUrl>" class="buttontext">${shipmentId}</a>
                </div>
                <#if invoiceIds?exists && invoiceIds?has_content>
                <div>
                    <p>${uiLabelMap.AccountingInvoices}:</p>
                    <ul>
                    <#list invoiceIds as invoiceId>
                      <li>
                        ${uiLabelMap.CommonNbr}<a href="/accounting/control/invoiceOverview?invoiceId=${invoiceId}&amp;externalLoginKey=${externalLoginKey}" target="_blank" class="buttontext">${invoiceId}</a>
                        (<a href="/accounting/control/invoice.pdf?invoiceId=${invoiceId}&amp;externalLoginKey=${externalLoginKey}" target="_blank" class="buttontext">PDF</a>)
                      </li>
                    </#list>
                    </ul>
                </div>
                </#if>
            </#if>
            <br />

            <#-- select order form -->
            <#--<form name="selectOrderForm" method="post" action="<@ofbizUrl>PackOrder</@ofbizUrl>">
              <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
              <table cellspacing="0" class="basic-table">
                <tr>
                  <td width="25%" align="right"><span class="label">${uiLabelMap.ProductOrderId}</span></td>
                  <td width="1">&nbsp;</td>
                  <td width="25%">
                    <input type="text" name="orderId" size="20" maxlength="20" value="${orderId?if_exists}"/>
                    /
                    <input type="text" name="shipGroupSeqId" size="6" maxlength="6" value="${shipGroupSeqId?default("00001")}"/>
                  </td>
                  <td><span class="label">${uiLabelMap.ProductHideGrid}</span>&nbsp;<input type="checkbox" name="hideGrid" value="Y" <#if (hideGrid == "Y")>checked=""</#if> /></td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                  <td colspan="2">
                    <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onclick="javascript:document.selectOrderForm.submit();" />
                    <a href="javascript:document.selectOrderForm.submit();" class="buttontext">${uiLabelMap.ProductPackOrder}</a>
                    <a href="javascript:document.selectOrderForm.action='<@ofbizUrl>WeightPackageOnly</@ofbizUrl>';document.selectOrderForm.submit();" class="buttontext">${uiLabelMap.ProductWeighPackageOnly}</a>
                  </td>
                </tr>
              </table>
            </form>
            <br />-->

            <#-- select picklist bin form -->
           <#-- <form name="selectPicklistBinForm" method="post" action="<@ofbizUrl>PackOrder</@ofbizUrl>" style="margin: 0;">
              <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
              <table cellspacing="0" class="basic-table">
                <tr>
                  <td width="25%" align='right'><span class="label">${uiLabelMap.FormFieldTitle_picklistBinId}</span></td>
                  <td width="1">&nbsp;</td>
                  <td width="25%">
                    <input type="text" name="picklistBinId" size="29" maxlength="60" value="${picklistBinId?if_exists}"/>
                  </td>
                  <td><span class="label">${uiLabelMap.ProductHideGrid}</span>&nbsp;<input type="checkbox" name="hideGrid" value="Y" <#if (hideGrid == "Y")>checked=""</#if> /></td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                  <td colspan="1">
                    <input type="image" src="<@ofbizContentUrl>/images/spacer.gif</@ofbizContentUrl>" onclick="javascript:document.selectPicklistBinForm.submit();" />
                    <a href="javascript:document.selectPicklistBinForm.submit();" class="buttontext">${uiLabelMap.ProductPackOrder}</a>
                    <a href="javascript:document.selectPicklistBinForm.action='<@ofbizUrl>WeightPackageOnly</@ofbizUrl>';document.selectPicklistBinForm.submit();" class="buttontext">${uiLabelMap.ProductWeighPackageOnly}</a>
                  </td>
                </tr>
              </table>
            </form>-->
            <form name="clearPackForm" method="post" action="<@ofbizUrl>ClearPackAll</@ofbizUrl>">
              <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
              <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
              <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
            </form>
            <form name="incPkgSeq" method="post" action="<@ofbizUrl>SetNextPackageSeq</@ofbizUrl>">
              <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
              <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
              <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
            </form>
            <form name="clearPackLineForm" method="post" action="<@ofbizUrl>ClearPackLine</@ofbizUrl>">
                <input type="hidden" name="facilityId"/>
                <input type="hidden" name="orderId"/>
                <input type="hidden" name="orderItemSeqId"/>
                <input type="hidden" name="productId"/>
                <input type="hidden" name="shipGroupSeqId"/>
                <input type="hidden" name="inventoryItemId"/>
                <input type="hidden" name="packageSeqId"/>
            </form>
        </div>
    </div>
    <#if orderHeader?has_content>
    <#else>
    	Please Wait ......

		<script type='text/javascript'> 
			location.reload();
		</script>
    </#if>
    <#if showInput != "N" && ((orderHeader?exists && orderHeader?has_content))>
    <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">${uiLabelMap.ProductOrderId} ${uiLabelMap.CommonNbr}<a href="/ordermgr/control/orderview?orderId=${orderId}">${orderId}</a> / ${uiLabelMap.ProductOrderShipGroupId} #${shipGroupSeqId}</li>
            </ul>
            <br class="clear"/>
        </div>
        <div class="screenlet-body">
              <#if orderItemShipGroup?has_content>
                <#assign postalAddress = orderItemShipGroup.getRelatedOne("PostalAddress")>
                <#assign carrier = orderItemShipGroup.carrierPartyId?default("N/A")>
                <table cellpadding="4" cellspacing="4" class="basic-table">
                  <tr>
                    <td valign="top">
                      <span class="label">${uiLabelMap.ProductShipToAddress}</span>
                      <br />
                      ${uiLabelMap.CommonTo}: ${postalAddress.toName?default("")}
                      <br />
                      <#if postalAddress.attnName?has_content>
                          ${uiLabelMap.CommonAttn}: ${postalAddress.attnName}
                          <br />
                      </#if>
                      ${postalAddress.address1}
                      <br />
                      <#if postalAddress.address2?has_content>
                          ${postalAddress.address2}
                          <br />
                      </#if>
                      ${postalAddress.city?if_exists}, ${postalAddress.stateProvinceGeoId?if_exists} ${postalAddress.postalCode?if_exists}
                      <br />
                      ${postalAddress.countryGeoId}
                      <br />
                    <#--</td>
                    <td>&nbsp;</td>
                    <td valign="top">
                      <span class="label">${uiLabelMap.ProductCarrierShipmentMethod}</span>
                      <br />
                      <#if carrier == "USPS">
                        <#assign color = "red">
                      <#elseif carrier == "UPS">
                        <#assign color = "green">
                      <#else>
                        <#assign color = "black">
                      </#if>
                      <#if carrier != "_NA_">
                        <font color="${color}">${carrier}</font>
                        &nbsp;
                      </#if>
                      ${orderItemShipGroup.shipmentMethodTypeId?default("??")}
                      <br />-->
                     <#-- <span class="label">${uiLabelMap.ProductEstimatedShipCostForShipGroup}</span>
                      <br />
                      <#if shipmentCostEstimateForShipGroup?exists>
                          <@ofbizCurrency amount=shipmentCostEstimateForShipGroup isoCode=orderReadHelper.getCurrency()?if_exists/>
                          <br />
                      </#if>
                    </td>
                    <td>&nbsp;</td>
                    <td valign="top">
                      <span class="label">${uiLabelMap.OrderInstructions}</span>
                      <br />
                      ${orderItemShipGroup.shippingInstructions?default("(${uiLabelMap.CommonNone})")}
                    </td>
                  </tr>-->
                </table>
              </#if>

              <#-- manual per item form -->
              <#if showInput != "N">
                <hr />
                
                
                
                
                <#--<form name="singlePackForm" method="post" action="<@ofbizUrl>ProcessPackOrder</@ofbizUrl>">
                  <input type="hidden" name="packageSeq" value="${packingSession.getCurrentPackageSeq()}"/>
                  <input type="hidden" name="orderId" value="${orderId}"/>
                  <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId}"/>
                  <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
                  <input type="hidden" name="hideGrid" value="${hideGrid}"/>
                  <table cellpadding="2" cellspacing="0" class="basic-table">
                    <tr>
                      <td>
                        <div>
                            <span class="label">${uiLabelMap.ProductProductNumber}</span>
                            <input type="text" name="productId" size="20" maxlength="20" value=""/>
                            @
                            <input type="text" name="quantity" size="6" maxlength="6" value="1"/>
                            <a href="javascript:document.singlePackForm.submit();" class="buttontext">${uiLabelMap.ProductPackItem}</a>
                        </div>
                      </td>
                      <td>
                          <span class="label">${uiLabelMap.ProductCurrentPackageSequence}</span>
                          ${packingSession.getCurrentPackageSeq()}
                          <input type="button" value="${uiLabelMap.ProductNextPackage}" onclick="javascript:document.incPkgSeq.submit();" />
                      </td>
                    </tr>
                  </table>
                </form>-->
              </#if>
              
              <div>
	              <form action="#" onSubmit="return checkProductIds()">
	                    <span class="label">${uiLabelMap.ProductBarCode} / Articla No. / ${uiLabelMap.ProductProductId} 
	                    <input type="text" id="barcodeId" size="20" maxlength="20" value="" autocomplete="off" />
	                    
	                    <input type="submit" value="Update"/> </span> <div id="barcodeError"></div>
	              </form>
              </div>
              </BR>
               <div>
                  <span class="label">Select All Ordered Quantity to Packed Qty : &nbsp;                 
                       <input type="button" name="Update" value="Update All" onclick="return selectAllQty();"/> 
                  </span>
                </div>
              
              <#assign productdemo="">
              <#-- auto grid form -->
              <#assign itemInfos = packingSession.getItemInfos()?if_exists>
              <#if showInput != "N" && hideGrid != "Y" && itemInfos?has_content>
           
                <br />
                <form name="multiPackForm" method="post" action="<@ofbizUrl>ProcessBulkPackOrderNew</@ofbizUrl>" onsubmit="return validateForm()">
                  <input type="hidden" name="facilityId" value="${facilityId?if_exists}" />
                  <input type="hidden" name="orderId" value="${orderId?if_exists}" />
                  <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}" />
                  <input type="hidden" name="originFacilityId" value="${facilityId?if_exists}" />
                  <input type="hidden" name="hideGrid" value="${hideGrid}"/>
                  <input type="hidden" name="forceComplete" value="${forceComplete?default('true')}"/>
                  <input type="hidden" name="weightUomId" value="${defaultWeightUomId}"/>
                  <input type="hidden" name="showInput" value="N"/>

                  <table class="basic-table" cellspacing='0'>
                    <tr class="header-row">
                      <td>SNo.</td>
                      <#--td>${uiLabelMap.ProductItem} ${uiLabelMap.CommonNbr}</td-->
                      <td>${uiLabelMap.ProductProductId}</td>
                      <td>${uiLabelMap.ProductInternalName}</td>
                      <td align="right">${uiLabelMap.ProductOrderedQuantity}</td>
                      <td align="right">${uiLabelMap.ProductQuantityShipped}</td>
                      <td align="center">${uiLabelMap.ProductPackedQty}</td>
                      <#--td>&nbsp;</td>
                      <td align="center">${uiLabelMap.ProductPackQty}</td>
                      <td align="center">${uiLabelMap.ProductPackedWeight}&nbsp;(${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval})</td-->
                      <#if carrierShipmentBoxTypes?has_content>
                        <td align="center">${uiLabelMap.ProductShipmentBoxType}</td>
                      </#if>
                      <#--td align="center">${uiLabelMap.ProductPackage}</td>
                      <td align="right">&nbsp;<b>*</b>&nbsp;${uiLabelMap.ProductPackages}</td-->
                    </tr>
				
                    <#if (itemInfos?has_content)>
                      <#assign rowKey = 1>
                      <#list itemInfos as itemInfo>
                      <#-- <#list itemInfos as orderItem>  -->
                        <#assign orderItem = itemInfo.orderItem/>
                        <#assign shippedQuantity = orderReadHelper.getItemShippedQuantity(orderItem)?if_exists>
                        <#assign orderItemQuantity = itemInfo.quantity/>
                        <#assign orderProduct = orderItem.getRelatedOne("Product")?if_exists/>
                        <#assign product = Static["org.ofbiz.product.product.ProductWorker"].findProduct(delegator, itemInfo.productId)?if_exists/>
                        
                        <#if orderItem.cancelQuantity?has_content && orderItem.quantity?has_content>
                          <#assign orderItemQuantity = orderItem.quantity - orderItem.cancelQuantity>
                        <#--else>
                          <#assign orderItemQuantity = orderItem.quantity-->
                        </#if>

                        <#--assign inputQty = orderItemQuantity - packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId)-->
                        <#assign orderItemQuantity1 = orderItemQuantity - packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId) > 
                        <#assign inputQty = 0>
                        <tr>
                          <td>${rowKey}
                          	  <input type="checkbox"  style="display:none;" class="text1" name="sel_${rowKey}" id="${orderItem.orderItemSeqId?if_exists}__${orderProduct.productId}" value='${orderItemQuantity1}'/>
                          	  <input type="hidden" id="rowKey_${orderItem.orderItemSeqId?if_exists}__${orderProduct.productId}" value="${rowKey}"/>
                          	  <input type="hidden" id="orderItemQuantity_${orderItem.orderItemSeqId?if_exists}__${orderProduct.productId}" value="${orderItemQuantity1}"/>
                          	  <input type="hidden" id="packedQuantity_${orderItem.orderItemSeqId?if_exists}__${orderProduct.productId}" value="${packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId)}"/>
						  </td>
                          <#--td>${orderItem.orderItemSeqId}</td-->
                          <td>
                              ${orderProduct.productId?default("N/A")}
                              <#if rowKey == 1>
                              <#assign productdemo=orderItem.orderItemSeqId+"__"+orderProduct.productId>
                              <#else>
                               <#assign productdemo=productdemo+" "+orderItem.orderItemSeqId+"__"+orderProduct.productId>
                               </#if>
                             
                              <#if orderProduct.productId != product.productId>
                                  &nbsp;${product.productId?default("N/A")}
                              </#if>
                          </td>
                          <td>
                              <a href="/catalog/control/EditProduct?productId=${orderProduct.productId?if_exists}${externalKeyParam}" class="buttontext" target="_blank">${(orderProduct.internalName)?if_exists}</a>
                              <#if orderProduct.productId != product.productId>
                                  &nbsp;[<a href="/catalog/control/EditProduct?productId=${product.productId?if_exists}${externalKeyParam}" class="buttontext" target="_blank">${(product.internalName)?if_exists}</a>]
                              </#if>
                          </td>
                          <td align="right">${orderItemQuantity}</td>
                          <td align="right">${shippedQuantity?default(0)}</td>
                          <#--td align="right">${packingSession.getPackedQuantity(orderId, orderItem.orderItemSeqId, shipGroupSeqId, itemInfo.productId)}</td>
                          <td>&nbsp;</td-->
                          <td align="center">
                            <input type="text" size="7" name="qty_${rowKey}" value="${inputQty}" id="qty_${rowKey}" readonly="readonly"/>
                            <div id="qtypackedError_${orderItem.orderItemSeqId?if_exists}__${orderProduct.productId}"></div>
                          </td>
                          <#--td align="center">
                            <input type="text" size="7" name="wgt_${rowKey}" value="" />
                          </td-->
                          <#if carrierShipmentBoxTypes?has_content>
                            <td align="center">
                              <select name="boxType_${rowKey}">
                                <option value=""></option>
                                <#list carrierShipmentBoxTypes as carrierShipmentBoxType>
                                  <#assign shipmentBoxType = carrierShipmentBoxType.getRelatedOne("ShipmentBoxType") />
                                  <option value="${shipmentBoxType.shipmentBoxTypeId}">${shipmentBoxType.description?default(shipmentBoxType.shipmentBoxTypeId)}</option>
                                </#list>
                              </select>
                            </td>
                          </#if>
                          <input type="hidden" name="pkg_${rowKey}" value="1">
                          
                          <#--td align="center">
                            <select name="pkg_${rowKey}">
                              <#if packingSession.getPackageSeqIds()?exists>
                                <#list packingSession.getPackageSeqIds() as packageSeqId>
                                  <option value="${packageSeqId}">${uiLabelMap.ProductPackage} ${packageSeqId}</option>
                                </#list>
                                <#assign nextPackageSeqId = packingSession.getPackageSeqIds().size() + 1>
                                <option value="${nextPackageSeqId}">${uiLabelMap.ProductNextPackage}</option>
                              <#else>
                                <option value="1">${uiLabelMap.ProductPackage} 1</option>
                                <option value="2">${uiLabelMap.ProductPackage} 2</option>
                                <option value="3">${uiLabelMap.ProductPackage} 3</option>
                                <option value="4">${uiLabelMap.ProductPackage} 4</option>
                                <option value="5">${uiLabelMap.ProductPackage} 5</option>
                              </#if>
                            </select>
                          </td-->
                          <input type="hidden" size="7" name="numPackages_${rowKey}" value="1" />
                          <#--td align="right">
                            <input type="text" size="7" name="numPackages_${rowKey}" value="1" />
                          </td-->
                          <input type="hidden" name="prd_${rowKey}" value="${itemInfo.productId?if_exists}"/>
                          <input type="hidden" name="ite_${rowKey}" value="${orderItem.orderItemSeqId}"/>
                        </tr>
                        <#assign rowKey = rowKey + 1>
                      </#list>
                      <div id="checkBoxError"></div>
                    </#if>
                   <input type="hidden" name="productIds" id="productIds" value="${productdemo?if_exists}"/>
                   <input type="hidden" name="shipmentId" value="${shipmentId?if_exists}"/>
                    <tr><td colspan="10">&nbsp;</td></tr>
                    
                    <tr>
                    		<td colspan="10">
                                <span class="label">Vehicle Number:</span>
                                <br />
                                <select name="vehicleNumber" id="vehicleNumber" onChange="vehicleThreshold()">
                                	<option value="">Select Vehicle</option>
                                	<#if assignedVechilesToDeliveryBoy?has_content>
                                		<#list assignedVechilesToDeliveryBoy as assigVehicleToDeliveryBoy>
                                			<option value="${assigVehicleToDeliveryBoy.vehicleNumber?if_exists}">
                                						   ${assigVehicleToDeliveryBoy.vehicleNumber?if_exists} :- 
                                						   ${assigVehicleToDeliveryBoy.deliveryBoyName?if_exists} :- 
                                						   <#if assigVehicleToDeliveryBoy.zoneGroupName?has_content>
														      ${assigVehicleToDeliveryBoy.zoneGroupName} :- 
														   </#if>
														   ${assigVehicleToDeliveryBoy.slot?if_exists}
														</option>
                                		</#list>
                                	</#if>
                                </select>
                                <div id="vehicleNumberError"></div>
                                <div id="vehicleNumberMngmt"></div>
                                    <br />
                            </td>
                    </tr>
                     
                    <tr>
                            <input type="hidden" name="partialPacking" id="partialPacking" value="N"/>
                        <td nowrap="nowrap">
                            <span class="label">Product ShippingCharge:</span>
                            <br />
                            <input type="text" name="additionalShippingCharge" value="${packingSession.getAdditionalShippingCharge()?if_exists}" size="20"/>
                            <#if packageSeqIds?has_content>
                               <#-- <a href="javascript:document.completePackForm.action='<@ofbizUrl>calcPackSessionAdditionalShippingCharge</@ofbizUrl>';document.completePackForm.submit();" class="buttontext">${uiLabelMap.ProductEstimateShipCost}</a>-->
                                <br />
                            </#if>
                        </td>
                      <td>
                        <span class="label">${uiLabelMap.ProductHandlingInstructions}:</span>
                        <br />
                        <textarea name="handlingInstructions" rows="2" cols="30" id="handlingInstructions">
                        <#if orderItemShipGroup?has_content>
                        	${orderItemShipGroup.shippingInstructions?if_exists}
                        </#if>
                        <#--${packingSession.getHandlingInstructions()?if_exists}--></textarea>
                      </td>
                      <td colspan="12" align="right"> 
                        <input type="submit" value="${uiLabelMap.ProductPackItem}"/>
                        &nbsp;
                        <input type="button" value="${uiLabelMap.CommonClear} (${uiLabelMap.CommonAll})" onclick="javascript:clearAll();"/>
                        
                        
                        
                      </td>
                    </tr>
                  </table>
                  
                  <center><div id="MessageDiv" style='display:none;color:#F99999;font-size:15px;'>Please wait...</div></center>
                  
                </form>
                <br />
              </#if>

              <#-- complete form -->
              <#if showInput != "N">
                <form name="completePackForm" method="post" action="<@ofbizUrl>CompletePack</@ofbizUrl>">
                  <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
                  <input type="hidden" name="shipGroupSeqId" value="${shipGroupSeqId?if_exists}"/>
                  <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
                  <input type="hidden" name="forceComplete" value="${forceComplete?default('true')}"/>
                  <input type="hidden" name="weightUomId" value="${defaultWeightUomId}"/>
                  <input type="hidden" name="showInput" value="N"/>
                  <hr/>
                  <table class="basic-table" cellpadding="2" cellspacing='0'>
                    <tr>
                        <#assign packageSeqIds = packingSession.getPackageSeqIds()/>
                        <#if packageSeqIds?has_content>
                            <td>
                                <span class="label">${uiLabelMap.ProductPackedWeight} (${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval}):</span>
                                <br />
                                <#list packageSeqIds as packageSeqId>
                                    ${uiLabelMap.ProductPackage} ${packageSeqId}
                                    <input type="text" size="7" name="packageWeight_${packageSeqId}" value="${packingSession.getPackageWeight(packageSeqId?int)?if_exists}" />
                                    <br />
                                </#list>
                                <#if orderItemShipGroup?has_content>
                                    <input type="hidden" name="shippingContactMechId" value="${orderItemShipGroup.contactMechId?if_exists}"/>
                                    <input type="hidden" name="shipmentMethodTypeId" value="${orderItemShipGroup.shipmentMethodTypeId?if_exists}"/>
                                    <input type="hidden" name="carrierPartyId" value="${orderItemShipGroup.carrierPartyId?if_exists}"/>
                                    <input type="hidden" name="carrierRoleTypeId" value="${orderItemShipGroup.carrierRoleTypeId?if_exists}"/>
                                    <input type="hidden" name="productStoreId" value="${productStoreId?if_exists}"/>
                                </#if>
                            </td>
                        </#if>
                        	<#--td>
                        		<#assign assignedVehicleToDeliveryBoy = Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllAssignedVehicleToDeliveryBoy(delegator, orderId)/>
                                <span class="label">Vehicle Number:</span>
                                <br />
                                <select name="vehicleNumber">
                                	<#if assignedVehicleToDeliveryBoy?has_content>
                                		<#list assignedVehicleToDeliveryBoy as assigVehicleToDeliveryBoy>
                                			<option value="${assigVehicleToDeliveryBoy.number?if_exists}">${assigVehicleToDeliveryBoy.number?if_exists}</option>
                                		</#list>
                                	</#if>
                                </select>
                                    <br />
                            </td>
                        <td nowrap="nowrap">
                            <span class="label">Product ShippingCharge:</span>
                            <br />
                            <input type="text" name="additionalShippingCharge" value="${packingSession.getAdditionalShippingCharge()?if_exists}" size="20"/>
                            <#if packageSeqIds?has_content>
                               <!-- <a href="javascript:document.completePackForm.action='<@ofbizUrl>calcPackSessionAdditionalShippingCharge</@ofbizUrl>';document.completePackForm.submit();" class="buttontext">${uiLabelMap.ProductEstimateShipCost}</a>-->
                                <#-->br />
                            </#if>
                        </td>
                      <td>
                        <span class="label">${uiLabelMap.ProductHandlingInstructions}:</span>
                        <br />
                        <textarea name="handlingInstructions" rows="2" cols="30">${packingSession.getHandlingInstructions()?if_exists}</textarea>
                      </td-->
                      <td align="right">
                        <div>
                          <#assign buttonName = "${uiLabelMap.ProductComplete}">
                          <#if forceComplete?default("false") == "true">
                            <#assign buttonName = "${uiLabelMap.ProductCompleteForce}">
                          </#if>
                          <#--input type="button" value="${buttonName}" onclick="javascript:document.completePackForm.submit();"/-->
                        </div>
                      </td>
                    </tr>
                  </table>
                  <br />
                </form>
              </#if>
        </div>
    </div>

    <!-- display items in packages, per packed package and in order -->
    <#assign linesByPackageResultMap = packingSession.getPackingSessionLinesByPackage()?if_exists>
    <#assign packageMap = linesByPackageResultMap.get("packageMap")?if_exists>
    <#assign sortedKeys = linesByPackageResultMap.get("sortedKeys")?if_exists>
    <#if ((packageMap?has_content) && (sortedKeys?has_content))>
      <div class="screenlet">
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3">${uiLabelMap.ProductPackages} : ${sortedKeys.size()?if_exists}</li>
            </ul>
            <br class="clear"/>
        </div>
          <div class="screenlet-body">
            <#list sortedKeys as key>
              <#assign packedLines = packageMap.get(key)>
              <#if packedLines?has_content>
                <br />
                <#assign packedLine = packedLines.get(0)?if_exists>
                <span class="label" style="font-size:1.2em">${uiLabelMap.ProductPackage}&nbsp;${packedLine.getPackageSeq()?if_exists}</span>
                <br />
                <table class="basic-table" cellspacing='0'>
                  <tr class="header-row">
                    <td>${uiLabelMap.ProductItem} ${uiLabelMap.CommonNbr}</td>
                    <td>${uiLabelMap.ProductProductId}</td>
                    <td>${uiLabelMap.ProductProductDescription}</td>
                    <td>${uiLabelMap.ProductInventoryItem} ${uiLabelMap.CommonNbr}</td>
                    <td align="right">${uiLabelMap.ProductPackedQty}</td>
                    <td align="right">${uiLabelMap.ProductPackedWeight}&nbsp;(${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval})&nbsp;(${uiLabelMap.ProductPackage})</td>
                    <td align="right">${uiLabelMap.ProductPackage} ${uiLabelMap.CommonNbr}</td>
                    <td>&nbsp;</td>
                  </tr>
                  <#list packedLines as line>
                    <#assign product = Static["org.ofbiz.product.product.ProductWorker"].findProduct(delegator, line.getProductId())/>
                    <tr>
                      <td>${line.getOrderItemSeqId()}</td>
                      <td>${line.getProductId()?default("N/A")}</td>
                      <td>
                          <a href="/catalog/control/EditProduct?productId=${line.getProductId()?if_exists}${externalKeyParam}" class="buttontext" target="_blank">${product.internalName?if_exists?default("[N/A]")}</a>
                      </td>
                      <td>${line.getInventoryItemId()}</td>
                      <td align="right">${line.getQuantity()}</td>
                      <td align="right">${line.getWeight()} (${packingSession.getPackageWeight(line.getPackageSeq()?int)?if_exists})</td>
                      <td align="right">${line.getPackageSeq()}</td>
                      <td align="right"><a href="javascript:clearLine('${facilityId}', '${line.getOrderId()}', '${line.getOrderItemSeqId()}', '${line.getProductId()?default("")}', '${line.getShipGroupSeqId()}', '${line.getInventoryItemId()}', '${line.getPackageSeq()}')" class="buttontext">${uiLabelMap.CommonClear}</a></td>
                    </tr>
                  </#list>
                </table>
              </#if>
            </#list>
          </div>
      </div>
    </#if>

    <!-- packed items display -->
    <!-- <#assign packedLines = packingSession.getLines()?if_exists>
    <#if packedLines?has_content>
      <div class="screenlet">
          <div class="screenlet-title-bar">
              <ul>
                  <li class="h3">${uiLabelMap.ProductItems} (${uiLabelMap.ProductPackages}): ${packedLines.size()?if_exists}</li>
              </ul>
              <br class="clear"/>
          </div>
          <div class="screenlet-body">
            <table class="basic-table" cellspacing='0'>
              <tr class="header-row">
                  <td>${uiLabelMap.ProductItem} ${uiLabelMap.CommonNbr}</td>
                  <td>${uiLabelMap.ProductProductId}</td>
                  <td>${uiLabelMap.ProductProductDescription}</td>
                  <td>${uiLabelMap.ProductInventoryItem} ${uiLabelMap.CommonNbr}</td>
                  <td align="right">${uiLabelMap.ProductPackedQty}</td>
                  <td align="right">${uiLabelMap.ProductPackedWeight}&nbsp;(${("uiLabelMap.ProductShipmentUomAbbreviation_" + defaultWeightUomId)?eval})&nbsp;(${uiLabelMap.ProductPackage})</td>
                  <td align="right">${uiLabelMap.ProductPackage} ${uiLabelMap.CommonNbr}</td>
                  <td>&nbsp;</td>
              </tr>
              <#list packedLines as line>
                  <#assign product = Static["org.ofbiz.product.product.ProductWorker"].findProduct(delegator, line.getProductId())/>
                  <tr>
                      <td>${line.getOrderItemSeqId()}</td>
                      <td>${line.getProductId()?default("N/A")}</td>
                      <td>
                          <a href="/catalog/control/EditProduct?productId=${line.getProductId()?if_exists}${externalKeyParam}" class="buttontext" target="_blank">${product.internalName?if_exists?default("[N/A]")}</a>
                      </td>
                      <td>${line.getInventoryItemId()}</td>
                      <td align="right">${line.getQuantity()}</td>
                      <td align="right">${line.getWeight()} (${packingSession.getPackageWeight(line.getPackageSeq()?int)?if_exists})</td>
                      <td align="right">${line.getPackageSeq()}</td>
                      <td align="right"><a href="javascript:clearLine('${facilityId}', '${line.getOrderId()}', '${line.getOrderItemSeqId()}', '${line.getProductId()?default("")}', '${line.getShipGroupSeqId()}', '${line.getInventoryItemId()}', '${line.getPackageSeq()}')" class="buttontext">${uiLabelMap.CommonClear}</a></td>
                  </tr>
              </#list>
            </table>
          </div>
      </div>
    </#if>
  </#if>-->

  <#if orderId?has_content>
    <script language="javascript" type="text/javascript">
      document.singlePackForm.productId.focus();
    </script>
  <#else>
    <script language="javascript" type="text/javascript">
      document.selectOrderForm.orderId.focus();
    </script>
  </#if>
<#else>
  <h3>${uiLabelMap.ProductFacilityViewPermissionError}</h3>
</#if>


<script type='text/javascript'> 
window.onload = function(){
	var instruction = document.getElementById("handlingInstructions").value;
	instruction = instruction.trim();
	document.getElementById("handlingInstructions").value = "";
};

function selectAllQty() {

var inputElements = document.getElementsByClassName('text1');
for(var i=0; inputElements[i]; ++i){
		inputElements[i].checked = true;
   		if(document.getElementById('qty_'+(i+1)) != null){
   			document.getElementById('qty_'+(i+1)).value = inputElements[i].value ; 
   		} 
 }
}

</script>
