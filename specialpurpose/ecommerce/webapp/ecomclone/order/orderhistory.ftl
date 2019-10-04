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

<div style="width:1024px;">
  <div class="screenlet">
    <h3>${uiLabelMap.OrderSalesHistory}</h3>
    <table id="orderSalesHistory" cellpadding="4" cellspacing="4" summary="This table display order sales history." style="width:100%; border:1px solid #cccccc;">
      <thead>
        <tr>
          <th>${uiLabelMap.CommonDate}</th>
          <th>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</th>
          <th>${uiLabelMap.CommonAmount}</th>
          <th>${uiLabelMap.CommonStatus}</th>
          <th>${uiLabelMap.OrderInvoices}</th>
          <#--<th></th>-->
          <th></th>
        </tr>
      </thead>
      <tbody>
        <#if orderHeaderList?has_content>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOneCache("StatusItem") />
            <tr>
              <td>${orderHeader.orderDate?string("dd-MM-yyyy")}</td>
              <td>${orderHeader.orderId}</td>
              <td><#--<@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom />--><span class="WebRupee">&#8377;</span>&nbsp;${orderHeader.grandTotal?if_exists}</td>
              <td>${status.get("description")}</td>
              <#-- invoices -->
              <#assign invoices = delegator.findByAnd("OrderItemBilling", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", "${orderHeader.orderId}"), Static["org.ofbiz.base.util.UtilMisc"].toList("invoiceId")) />
              <#assign distinctInvoiceIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(invoices, "invoiceId", true)>
              <#if distinctInvoiceIds?has_content>
                <td>
                  <#list distinctInvoiceIds as invoiceId>
                     <a href="<@ofbizUrl>invoice.pdf?invoiceId=${invoiceId}</@ofbizUrl>" class="buttontext">(${invoiceId} PDF) </a>
                  </#list>
                </td>
              <#else>
                <td></td>
              </#if>
              <td><a href="<@ofbizUrl>orderstatus?orderId=${orderHeader.orderId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonView}</a></td>
             <#-- <td><a href="javascript:addItem('${orderHeader.orderId}')" class="buttontext">${uiLabelMap.OrderAddToCart}</a></td>-->
            </tr>
          </#list>
        <#else>
          <tr><td colspan="6">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
  <#--div class="screenlet">
    <h3>${uiLabelMap.OrderPurchaseHistory}aaaaaaaaaaaaa</h3>
    <table id="orderPurchaseHistory" summary="This table display order purchase history." cellpadding="4" cellspacing="4" style="width:100%; border:1px solid #cccccc;">
      <thead>
        <tr>
          <th>${uiLabelMap.CommonDate}</th>
          <th>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</th>
          <th>${uiLabelMap.CommonAmount}</th>
          <th>${uiLabelMap.CommonStatus}</th>
          <th></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <#if porderHeaderList?has_content>
          <#list porderHeaderList as porderHeader>
            <#assign pstatus = porderHeader.getRelatedOneCache("StatusItem") />
            <tr>
              <td>${porderHeader.orderDate.toString()}</td>
              <td>${porderHeader.orderId}</td>
              <td><@ofbizCurrency amount=porderHeader.grandTotal isoCode=porderHeader.currencyUom /></td>
              <td>${pstatus.get("description",locale)}</td>
              <td><a href="<@ofbizUrl>orderstatus?orderId=${porderHeader.orderId}</@ofbizUrl>" class="button">${uiLabelMap.CommonView}</a></td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="5">${uiLabelMap.OrderNoOrderFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
  <div class="screenlet">
    <h3>${uiLabelMap.EcommerceDownloadsAvailableTitle}</h3>
    <table id="availableTitleDownload" cellpadding="4" cellspacing="4" summary="This table display available title for download." style="width:100%; border:1px solid #cccccc;">
      <thead>
        <tr>
          <th>${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</th>
          <th>${uiLabelMap.ProductProductName}</th>
          <th>${uiLabelMap.CommonName}</th>
          <th>${uiLabelMap.CommonDescription}</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <#if downloadOrderRoleAndProductContentInfoList?has_content>
          <#list downloadOrderRoleAndProductContentInfoList as downloadOrderRoleAndProductContentInfo>
            <tr>
              <td>${downloadOrderRoleAndProductContentInfo.orderId}</td>
              <td>${downloadOrderRoleAndProductContentInfo.productName}</td>
              <td>${downloadOrderRoleAndProductContentInfo.contentName?if_exists}</td>
              <td>${downloadOrderRoleAndProductContentInfo.description?if_exists}</td>
              <td>
                <a href="<@ofbizUrl>downloadDigitalProduct/${downloadOrderRoleAndProductContentInfo.contentName?if_exists}?dataResourceId=${downloadOrderRoleAndProductContentInfo.dataResourceId}</@ofbizUrl>" class="button">Download</a>
              </td>
            </tr>
          </#list>
        <#else>
          <tr><td colspan="5">${uiLabelMap.EcommerceDownloadNotFound}</td></tr>
        </#if>
      </tbody>
    </table>
  </div>
</div-->
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>


<script>
function addItem(orderId) {
      var  param = 'orderId=' + orderId+'&finalizeMode=init';
      jQuery.ajax({url: '/control/loadCartFromOrder',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#minicart').html(data);
          document.getElementById('outputs').innerHTML = "Order Items added successfully";
          ShowDialog1(false);
         },
         complete:  function() { 
	          var minitotal = document.getElementById('abcxyz').innerHTML;
	          var miniquantity = document.getElementById('miniquantityA').value;
	           $('#microCartTotal').text(minitotal);
	           document.getElementById('microCartQuantity').innerHTML=miniquantity;
	           document.getElementById('checkoutdis').style.display="block";
	          document.getElementById('abcxyzhref').href="/control/showcart";
         },
        error: function(data) {
        }
    	});
    }
    
     function ShowDialog1(modal)
   {
      

      setTimeout(function() {
       $('#cartsummary1').show();
		 $('#inbulkorder').hide();
        
    }, 4000);
    
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    cartsummary1();
    
   }
    function ShowDialog(modal)
   {
      $("#overlay").show();
      $("#dialog").fadeIn(300);

      if (modal)
      {
         $("#overlay").unbind("click");
      }
      else
      {
         $("#overlay").click(function (e)
         {
            HideDialog();
         });
      }
      setTimeout(function() {
        $("#overlay").hide(),
      $("#dialog").fadeOut(300)
    }, 3000);
   }
    
    </script>