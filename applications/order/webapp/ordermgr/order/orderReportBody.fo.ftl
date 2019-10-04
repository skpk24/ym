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
 <#assign totalDiscount = 0>
    <#if orderHeader?has_content>
        <fo:table   font-size="8px" margin-top="20px" >
		
	   <fo:table-column column-width="1.0in"/>	
       <fo:table-column column-width="1.7in" />
       <fo:table-column column-width="1.0in"/>
       <fo:table-column column-width="1.2in"/>
       <fo:table-column column-width="1.2in"/>
       <fo:table-column column-width="1.4in"/>
		
       <fo:table-header>
       		       <#--<fo:table-row border-top="0.5px solid #000000" border-bottom="0.5px solid #000000" background-color="#FFFFFF">
                      <fo:table-cell number-columns-spanned="5">
                         <fo:block  font-weight="bold" font-size="10px" padding="5px 0 5px 0"></fo:block>
                      </fo:table-cell>
                    </fo:table-row>-->
           <fo:table-row  margin-left="10px" margin-right="10px" padding-top="5px" background-color="#cccccc" >
           		   <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px">Sl No</fo:block></fo:table-cell>
	               <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px"><#--${uiLabelMap.OrderProduct}--> Items</fo:block></fo:table-cell>
	               <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px"><#--${uiLabelMap.OrderQtyOrdered}--> Quantity</fo:block></fo:table-cell>
	               <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px">Unit Price (<fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/>)</fo:block></fo:table-cell>
	               <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px"><#--${uiLabelMap.OrderSubTotal}-->Total (<fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/>)</fo:block></fo:table-cell>
	               <fo:table-cell text-align="center"><fo:block font-weight="bold" padding="5px 5px 5px 5px"><#--${uiLabelMap.OrderAdjustments} Savings --> Discounts(<fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/>) </fo:block></fo:table-cell>
           </fo:table-row>
           
       </fo:table-header>

            <fo:table-body >
            <#if getOrderItemsCategoryWise?has_content>
            <#assign siNo = 1>
            <#assign keys = orderItems>
			   <#assign keysc = 0>
			   <#assign keys = sampleMap1.keySet()/>
		    <#list keys as key>
            <fo:table-row >
                        <fo:table-cell number-columns-spanned="6">
                            <fo:block padding="5px 0 5px 0px" background-color="#efecec" font-size="12px" >
                            	<#assign categoryId = key?if_exists>
                            	<#assign categoryName = delegator.findByPrimaryKey("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryId", categoryId))?if_exists>
								<#if categoryName?has_content>	
									${categoryName.categoryName?if_exists}
								</#if>	
            				</fo:block>
            			</fo:table-cell>
             </fo:table-row>
                 <#assign orderItemList = getOrderItemsCategoryWise.get(key)?if_exists>			
            
                 <#list orderItemList as orderItem>
                 <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                 <#assign productId = orderItem.productId?if_exists>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                 <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                  <fo:table-row >
                          
                  		    <fo:table-cell text-align="center"><fo:block font-weight="normal" padding="5px 0 5px 0" >${siNo?if_exists}<#assign siNo = siNo+1></fo:block></fo:table-cell>
                            <fo:table-cell>
	                            <fo:block padding="5px 0 5px 0">
	                            
	                            	<#assign ProdBrand = delegator.findOne("Product",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", orderItem.productId?if_exists),false)?if_exists />
	                               ${ProdBrand.brandName?if_exists} 
	                               <#if productId?exists>
	                                ${orderItem.itemDescription?if_exists}
	                              <#elseif orderItemType?exists>
	                                ${orderItemType.get("description",locale)} - ${orderItem.itemDescription?if_exists}
	                              <#else>
	                                ${orderItem.itemDescription?if_exists}
	                              </#if>
	                            </fo:block>
                            </fo:table-cell>
                            <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0">${remainingQuantity}</fo:block></fo:table-cell>
                            <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0">
                             <#if orderItem.unitListPrice gt 0>
           					${orderItem.unitListPrice?string.number}
           					<#else>
          					  ${orderItem.unitPrice?string.number}
           						 </#if>
                            
                            
                            </fo:block>
                            </fo:table-cell>
                            
                            
                            <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0">
                            <#if orderItem.statusId != "ITEM_CANCELLED">
                                 <#-- <@ofbizCurrency amount=Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) isoCode=currencyUomId/> -->
                                  <#assign prodamt =Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemSubTotal(orderItem, orderAdjustments) />
                                  ${prodamt?if_exists}
                            <#else>
                               <#-- <@ofbizCurrency amount=0.00 isoCode=currencyUomId/> -->
                               ${0.00}
                            </#if></fo:block></fo:table-cell>
                            
                            <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"> 
                            
                            <#if orderItem.unitListPrice gt 0>
       			  <#assign discount=(orderItem.unitListPrice-orderItem.unitPrice)*orderItem.quantity>
        				 <#if "Y" == orderItem.isPromo>
				         <#else>
			         	 	<#assign totalDiscount=totalDiscount+discount>
			         	 </#if>
                            </#if>
                            <#if "Y" == orderItem.isPromo>0<#else>${discount?if_exists}</#if></fo:block></fo:table-cell>
                       </fo:table-row>
                       
                    
                    <fo:table-row>
                        <fo:table-cell  number-columns-spanned="6">
                            <fo:block border-bottom="0.5px solid">
                            </fo:block>
                            </fo:table-cell>
                         </fo:table-row>
           </#list>
           <#--assign siNo = 1-->
           </#list>
            </#if>
          <#list orderHeaderAdjustments as orderHeaderAdjustment>
            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType")>
            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
            <#if adjustmentAmount != 0>
            <fo:table-row >
            	<fo:table-cell></fo:table-cell>
               <fo:table-cell></fo:table-cell>
               <fo:table-cell></fo:table-cell>
               <fo:table-cell></fo:table-cell>
               <fo:table-cell number-columns-spanned="1"><fo:block font-weight="bold" padding="5px 0 5px 0" text-align="right"><#--${adjustmentType.get("description",locale)}  <#if orderHeaderAdjustment.get("description")?has_content>(${orderHeaderAdjustment.get("description")?if_exists})</#if> --></fo:block></fo:table-cell>
               <fo:table-cell text-align="right" ><fo:block padding="5px 0 5px 0"><#--${adjustmentAmount?if_exists}<#--<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/>--></fo:block></fo:table-cell>
            </fo:table-row>
            </#if>
          </#list>

           <#-- summary of order amounts -->
           			
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">${uiLabelMap.OrderSubTotal}</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${orderSubTotal?if_exists}<#--<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                    <#if shippingAmount != 0>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="center"><fo:block padding="5px 0 5px 0" font-weight="bold">${uiLabelMap.OrderTotalShippingAndHandling}</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${shippingAmount?if_exists}<#--<@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                  </#if>
                  <#if taxAmount != 0>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <#--<fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">${uiLabelMap.OrderTotalSalesTax}</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0">${taxAmount?if_exists}<@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></fo:block></fo:table-cell>-->
                    </fo:table-row>
                  </#if>
                  <#if otherAdjAmount != 0>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold"><#--${uiLabelMap.OrderTotalOtherOrderAdjustments}-->Promotion</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${otherAdjAmount?if_exists}<#--<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                  </#if>
                 <#if inviteRef?has_content && inviteRef !=0>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">Invite A Friend Reference Discount</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${inviteRef?if_exists}<#--<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                  </#if>
                  
                 <#if totalAmount?has_content>
                  	<fo:table-row>
                        <fo:table-cell  number-columns-spanned="6">
                            <fo:block border-bottom="0.5px solid"></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">Total Amount</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${totalAmount?if_exists?string["0"]}<#--<@ofbizCurrency amount=orderGrandTotal isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                  <#elseif billingAmt?has_content && billingAmt!=0>
                  	<fo:table-row>
                        <fo:table-cell  number-columns-spanned="6">
                            <fo:block border-bottom="0.5px solid"></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">You Saved</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/>${billingAmt?if_exists}<#--<@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                </#if>
                  	<fo:table-row>
                        <fo:table-cell  number-columns-spanned="6">
                            <fo:block border-bottom="0.5px solid"></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">${uiLabelMap.OrderGrandTotal}</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> <#if total?has_content>${total?if_exists?string["0"]}<#else>${orderGrandTotal?if_exists?string["0"]}</#if><#--<@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
                 
		<fo:table-row>
                        <fo:table-cell  number-columns-spanned="6">
                            <fo:block border-bottom="0.5px solid"></fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    	<fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                        <fo:table-cell></fo:table-cell>
                         <fo:table-cell></fo:table-cell>
                        <fo:table-cell number-columns-spanned="1" text-align="right"><fo:block padding="5px 0 5px 0" font-weight="bold">Total Savings</fo:block></fo:table-cell>
                        <fo:table-cell text-align="center"><fo:block padding="5px 0 5px 0"><fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/>
                        <#assign billingAmt1 = 0>
                        <#if !totalDiscount?has_content>
                        	<#assign totalDiscount = 0>
                        </#if>
                        <#if (totalDiscount < 0)>
                        	<#assign totalDiscount = totalDiscount*(-1)>
                        </#if>
                        <#if (otherAdjAmount < 0)>
                        	<#assign otherAdjAmount = otherAdjAmount*(-1)>
                        </#if>
                         <#if billingAmt?has_content && billingAmt!=0>
                       <#assign billingAmt1 = billingAmt>
                        </#if>
                        <#if otherAdjAmount?has_content>
                        	<#assign totalDiscount = totalDiscount + otherAdjAmount+ billingAmt1> 
                        </#if>
                        
                        
                        ${totalDiscount?if_exists}<#--<@ofbizCurrency amount=grandTotal isoCode=currencyUomId/>--></fo:block></fo:table-cell>
                    </fo:table-row>
           <#-- notes -->
           <#if orderNotes?has_content>
                   <#if showNoteHeadingOnPDF>
                   <fo:table-row>
                       <fo:table-cell number-columns-spanned="4">
                           <fo:block font-weight="bold">${uiLabelMap.OrderNotes}</fo:block>
                           <fo:block><fo:leader leader-length="19cm" leader-pattern="rule"/></fo:block>
                       </fo:table-cell>
                   </fo:table-row>
                   </#if>
                <#list orderNotes as note>
                 <#if (note.internalNote?has_content) && (note.internalNote != "Y")>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="2">
                        <fo:block>${note.noteInfo?if_exists}</fo:block>
                    </fo:table-cell>
                        <fo:table-cell number-columns-spanned="3">
                        <#assign notePartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", note.noteParty, "compareDate", note.noteDateTime, "lastNameFirst", "Y", "userLogin", userLogin))/>
                        <fo:block>${uiLabelMap.CommonBy}: ${notePartyNameResult.fullName?default("${uiLabelMap.OrderPartyNameNotFound}")}</fo:block>
                    </fo:table-cell>
                        <fo:table-cell number-columns-spanned="2">
                        <fo:block>${uiLabelMap.CommonAt}: ${note.noteDateTime?string?if_exists}</fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                  </#if>
                  </#list>
            </#if>
            
            </fo:table-body>
    </fo:table>
     <#if orderHeader.getString("orderTypeId") == "SALES_ORDER" || shipGroups?has_content>
     <#list shipGroups as shipGroup>
	                      	<#if orderHeader?has_content>
		                  		<#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
							<#else>
		                  		<#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
		                	</#if>
		                	<#if orderHeader?has_content>
			                  	<#assign isGift = shipGroup.isGift?default("N")>
			                  	<#assign giftMessage = shipGroup.giftMessage?if_exists>
		                	<#else>
		                  		<#assign isGift = cart.getIsGift(groupIdx)?default("N")>
		                  		<#assign giftMessage = cart.getGiftMessage(groupIdx)?if_exists>
		                	</#if>  
	                   </#list>
	                
	<fo:table   font-size="8px" background-color="#fcfcfc">

       <fo:table-column column-width="2.6in" />
       <fo:table-column column-width="1.2in"/>
        <fo:table-body margin-left="10px" margin-right="10px">
    		 <fo:table-row>
                        <fo:table-cell>
                            <fo:block padding="5px 0 5px 0">
                            <#if billingAmt?has_content>
                             <#assign billingAmt1 = billingAmt?if_exists/>
    							Your Credit under YouMart Savings : <fo:external-graphic src="${contentUrl?if_exists?default('http://youmart.in')}/erptheme1/RSsymbol.png" overflow="hidden" height="7px"  content-height="scale-to-fit"/> ${billingAmt1?if_exists}
    						</#if>		
    						</fo:block>
    						
                         </fo:table-cell>
             </fo:table-row>
     	</fo:table-body>
    </fo:table>
                       
	                
    <fo:table font-size="8px">

       <fo:table-column column-width="3.75in" />
       <fo:table-column column-width="3.75in"/>
       <#--<fo:table-header>
           <fo:table-row  margin-left="10px" border-bottom="0.5px solid #000000" border-top="0.5px solid #000000" background-color="#FFFFFF">
               <fo:table-cell><fo:block font-weight="bold" padding="5px"> Instructions</fo:block></fo:table-cell>
               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="5px">Gift Message</fo:block></fo:table-cell>
           </fo:table-row>
       	</fo:table-header>-->
       	 <#if shipGroups?has_content>
	                   <#list shipGroups as shipGroup>
	                      	<#if orderHeader?has_content>
		                  		<#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
							<#else>
		                  		<#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
		                	</#if> 
	                 <#assign shipIns = shippingInstructions?if_exists>
	                   </#list>
                  </#if>
                  	<#if shipGroups?has_content>
                   <#list shipGroups as shipGroup>
                      	
                      	${giftMessage?if_exists}
                   </#list>
                  </#if>
		<fo:table-body > 
		<#if shipIns?has_content>
		<fo:table-row  margin-left="10px" margin-right="10px" padding-top="5px" background-color="#cccccc"  >               
		<fo:table-cell number-columns-spanned="2"><fo:block font-weight="bold" padding="5px 5px 5px 5px" >Shipping Instructions
               </fo:block></fo:table-cell>
               
           </fo:table-row>
     	 <fo:table-row  margin-left="10px">
               <fo:table-cell>
              <fo:block font-weight="normal" padding="10px">${shipIns?if_exists}
               </fo:block></fo:table-cell>
           </fo:table-row>
           </#if>
     </fo:table-body>
    </fo:table>
   
           
           </#if>
   
    </#if>
</#escape>