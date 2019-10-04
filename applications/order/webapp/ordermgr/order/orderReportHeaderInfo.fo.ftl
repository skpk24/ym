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
<#escape x as x?xml>
<fo:table border-spacing="3pt" width="7.3in"  border="0.5px solid #000000"  font-size="10px">
       <fo:table-column column-width="2.2in" border="0.5px solid #000000"/>
       <fo:table-column column-width="3.4in"/>
       <fo:table-body>
			  		
			               
			               <#if orderId?has_content>
				               <#assign invoices = delegator.findByAnd("OrderItemBilling", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", "${orderId}"), Static["org.ofbiz.base.util.UtilMisc"].toList("invoiceId"))>
				               <#assign distinctInvoiceIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(invoices, "invoiceId", true)>
				               <#if distinctInvoiceIds?has_content>
	                      	      <#list distinctInvoiceIds as invoiceId>
	                      	      <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
	                      	      <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Invoice</fo:inline></fo:block></fo:table-cell>
			               			<fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >
	                      		    ${invoiceId?if_exists}
	                      		    </fo:inline></fo:block></fo:table-cell>
	                      		   </fo:table-row> 
	                      	      </#list>
	                           </#if>
                           </#if>
                          
			        <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Order Id</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >${orderId?if_exists}</fo:inline></fo:block></fo:table-cell>
			       </fo:table-row>
			       
			       <#if orderHeader?has_content>
                   		<#if (orderHeader.statusId)?if_exists == "ORDER_REJECTED">
			                   <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
						               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Status</fo:inline></fo:block></fo:table-cell>
						               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Rejected</fo:inline></fo:block></fo:table-cell>
						       </fo:table-row>
			        	</#if>
				   </#if>
			       
			  		<fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Slot</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >
			           
                           <#if orderHeader.get("deliveryDate")?has_content>
	                           ${orderHeader.get("deliveryDate")?string("EEE d MMM yyyy")?if_exists}
	                           <#if orderHeader.get("slot")?has_content>
	                           <#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
		        				 <#assign slotTime123 = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(currentSlotList, Static["org.ofbiz.base.util.UtilMisc"].toMap("slotType",orderHeader.get("slot")?if_exists)), "slotTiming", true)>
		        					<#if slotTime123?has_content>
		                  				<#list slotTime123 as slotTiming>
		                  					- ${slotTiming?if_exists}
		                  				</#list>
	            					</#if>
	            				 </#if>
            				</#if>	
                           </fo:inline></fo:block></fo:table-cell>
			       </fo:table-row>
			        <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Payment</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >
							<#if orderPaymentPreferencesold?has_content>
								<#list orderPaymentPreferencesold as orderPaymentPreference>
		                            <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
		                            <#--if !(paymentMethodType.paymentMethodTypeId == "EXT_BILLACT" || paymentMethodType.paymentMethodTypeId == "EXT_INVITE")>
		                             	${paymentMethodType.get("description")?if_exists}
		                             </#if-->
		                             ${paymentMethodType.get("description")?if_exists}
		                         	<#if orderPaymentPreference_has_next></#if>
	                            </#list>
                            </#if>
							</fo:inline></fo:block></fo:table-cell>
			       </fo:table-row>
			       <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >Grand Total</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >
							<#if grandTotal?has_content && grandTotal != 0>
                  	        	<fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> <#if total?has_content>${total?if_exists?string["0"]}<#else>${orderGrandTotal?if_exists?string["0"]}</#if>
                            </#if>
							</fo:inline></fo:block></fo:table-cell>
			       </fo:table-row>
			       <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >No. Of Items</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline >
							<#--if noOfItems?has_content>
	                        <#assign noOfItemsOnOrder = noOfItems?if_exists>
	                     	${noOfItemsOnOrder?if_exists}
                     	    </#if-->
                     	    <#if orderItems?has_content>
                     	    	${orderItems.size()?if_exists}
                     	    </#if>
							</fo:inline></fo:block></fo:table-cell>
			       </fo:table-row>
			       </fo:table-body>       
</fo:table>
                 
                 
                 
                 <#--<fo:table border-spacing="3pt" width="7.3in" background-color="#fcfcfc"  font-size="8px">

       <fo:table-column column-width="1.4in" />
       <fo:table-column column-width="1.4in"/>
       <fo:table-column column-width="1.4in"/>
       <fo:table-column column-width="2.1in"/>
       <fo:table-column column-width="1.5in"/>
                    <fo:table-body>
                   
                     
			           <fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">${uiLabelMap.OrderDateOrdered}</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">${uiLabelMap.OrderOrder}</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Invoice No.</fo:inline></fo:block></fo:table-cell>
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Payment Information</fo:inline></fo:block></fo:table-cell>
			           	   <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">No Of Items</fo:inline></fo:block></fo:table-cell>
			          
			           </fo:table-row>
       				
                    <fo:table-row  margin="10px" space-before="60px" padding="20px">
                      <#assign dateFormat = Static["java.text.DateFormat"].LONG>
                    <#assign orderDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format(orderHeader.get("orderDate"))>
                      <fo:table-cell><fo:block padding="5px 0 10px 0">${orderHeader.get("orderDate")?string("dd-MM-yyyy")?if_exists}</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block padding="5px 0 10px 0">${orderId}</fo:block></fo:table-cell>
                      <fo:table-cell><fo:block font-weight="bold" padding="5px 0 10px 0">
                      <#assign invoices = delegator.findByAnd("OrderItemBilling", Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", "${orderId}"), Static["org.ofbiz.base.util.UtilMisc"].toList("invoiceId"))>
		              <#assign distinctInvoiceIds = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(invoices, "invoiceId", true)>
		              <#if distinctInvoiceIds?has_content>
                      	<#list distinctInvoiceIds as invoiceId>
                      		${invoiceId}
                      	</#list>
                      </#if>
                      ${currentStatus.get("description",locale)}</fo:block></fo:table-cell>
                     <fo:table-cell><fo:block font-weight="bold" padding="5px 0 10px 0">
                      
                      <#list orderPaymentPreferences as orderPaymentPreference>
                         <#assign paymentMethodType = orderPaymentPreference.getRelatedOne("PaymentMethodType")?if_exists>
                         
                        
                             ${paymentMethodType.get("description")?if_exists}
                         	<#if orderPaymentPreference_has_next>,</#if>
                      </#list>
                      </fo:block></fo:table-cell>
                      <fo:table-cell><fo:block font-weight="bold" padding="5px 0 10px 0">
                      	<#if noOfItems?has_content>
	                     <#assign noOfItemsOnOrder = noOfItems?if_exists>
	                     	${noOfItemsOnOrder?if_exists}
                     	</#if>
                      </fo:block></fo:table-cell>
                      
                    </fo:table-row>
                    <#if orderItem.cancelBackOrderDate?exists>
                      <fo:table-row>
                        <fo:table-cell><fo:block>${uiLabelMap.FormFieldTitle_cancelBackOrderDate}</fo:block></fo:table-cell>
                        <#assign dateFormat = Static["java.text.DateFormat"].LONG>
                        <#assign cancelBackOrderDate = Static["java.text.DateFormat"].getDateInstance(dateFormat,locale).format(orderItem.get("cancelBackOrderDate"))>
                        <fo:table-cell><#if cancelBackOrderDate?has_content><fo:block>${cancelBackOrderDate}</fo:block></#if></fo:table-cell>
                      </fo:table-row>
                    </#if>
                    </fo:table-body>
                  </fo:table>
                  
                   <fo:table border-spacing="3pt" width="100%" background-color="#fcfcfc" border-bottom="0.5px solid #000000" font-size="8px">
                    <fo:table-column column-width="1.8in" />
       				<fo:table-column column-width="1.8in"/>
                  		 <fo:table-body>
                  		 	<fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
			               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Slot Information</fo:inline></fo:block></fo:table-cell>
			           </fo:table-row>
                  		 </fo:table-body>
                  </fo:table>
                  
                  <fo:table border-spacing="3pt" width="100%" background-color="#fcfcfc" border-bottom="0.5px solid #000000" font-size="8px">
                    <fo:table-column column-width="1.8in" />
       				<fo:table-column column-width="1.8in"/>
       				<fo:table-column column-width="1.8in"/>
                  		 <fo:table-body>
                  		 	<fo:table-row  margin="10px" space-before="60px" padding="20px" border-top="0.5px solid #000000">
				               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Slot Type</fo:inline></fo:block></fo:table-cell>
				               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Slot Time</fo:inline></fo:block></fo:table-cell>
				               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="10px 0 5px 0"><fo:inline border-bottom="1px solid #dfdfe0">Delivery Date</fo:inline></fo:block></fo:table-cell>
			           		</fo:table-row>
			           		<fo:table-row  margin="10px" space-before="60px" padding="20px">
			           		<#if orderHeader?has_content>
			           			 <fo:table-cell><fo:block padding="5px 0 10px 0">${orderHeader.get("slot")?if_exists}</fo:block></fo:table-cell>
			           			 <#assign slotTime = delegator.findByAnd("OrderSlotType",Static["org.ofbiz.base.util.UtilMisc"].toMap("slotType",orderHeader.get("slot")?if_exists))/>
      							 <#assign slotTime123 = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(slotTime, "slotTiming", true)>
	        					<#if slotTime123?has_content>
	                  				<#list slotTime123 as slotTiming>
	                  					<fo:table-cell><fo:block padding="5px 0 10px 0">${slotTiming?if_exists}</fo:block></fo:table-cell>
	                  				</#list>
            					</#if>
			           			 
                      			 <fo:table-cell><fo:block padding="5px 0 10px 0">${orderHeader.get("deliveryDate")?string("dd-MM-yyyy")?if_exists}</fo:block></fo:table-cell>
			           		</#if>
			           		</fo:table-row>
                  		 </fo:table-body>
                  </fo:table>-->
</#escape>
