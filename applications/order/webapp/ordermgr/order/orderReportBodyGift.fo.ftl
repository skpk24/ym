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
    <#if orderHeader?has_content>
        <fo:table   font-size="8px" background-color="#fcfcfc">

       <fo:table-column column-width="2.6in" />
       <fo:table-column column-width="1.2in"/>
       <fo:table-column column-width="2.2in"/>
       <fo:table-column column-width="1.5in"/>
		
       <fo:table-header>
       		<fo:table-row border-top="0.5px solid #000000" border-bottom="0.5px solid #000000" background-color="#FFFFFF">
                      <fo:table-cell number-columns-spanned="4">
                         <fo:block  font-weight="bold" font-size="10px" padding="5px 0 5px 0">Dispatch Detail</fo:block>
                      </fo:table-cell>
                    </fo:table-row>
           <fo:table-row  margin-left="10px" margin-right="10px" padding-top="5px">
               <fo:table-cell><fo:block font-weight="bold">${uiLabelMap.OrderProduct}</fo:block></fo:table-cell>
               <fo:table-cell text-align="right"><fo:block font-weight="bold">${uiLabelMap.OrderQuantity}</fo:block></fo:table-cell>
               <fo:table-cell text-align="right"><fo:block font-weight="bold"></fo:block></fo:table-cell>
               <fo:table-cell text-align="right"><fo:block font-weight="bold"></fo:block></fo:table-cell>
           </fo:table-row>
           
       </fo:table-header>

            <fo:table-body margin-left="10px" margin-right="10px">
           <#list orderItemList as orderItem>
                 <#assign orderItemType = orderItem.getRelatedOne("OrderItemType")?if_exists>
                 <#assign productId = orderItem.productId?if_exists>
                    <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
                 <#assign itemAdjustment = Static["org.ofbiz.order.order.OrderReadHelper"].getOrderItemAdjustmentsTotal(orderItem, orderAdjustments, true, false, false)>
                  <fo:table-row>
                        <fo:table-cell>
                            <fo:block padding="5px 0 5px 0">
                            
                            	<#assign ProdBrand = delegator.findOne("Product",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", orderItem.productId?if_exists),false)?if_exists />
                               ${ProdBrand.brandName?if_exists} -
                               <#if productId?exists>
                                ${orderItem.itemDescription?if_exists}
                              <#elseif orderItemType?exists>
                                ${orderItemType.get("description",locale)} - ${orderItem.itemDescription?if_exists}
                              <#else>
                                ${orderItem.itemDescription?if_exists}
                              </#if>
                               </fo:block>
                          </fo:table-cell>
                              <fo:table-cell text-align="right"><fo:block padding="5px 0 5px 0">${remainingQuantity}</fo:block></fo:table-cell>
                            <fo:table-cell text-align="right"><fo:block padding="5px 0 5px 0"></fo:block></fo:table-cell>
                            <fo:table-cell text-align="right"><fo:block padding="5px 0 5px 0">
                            </fo:block></fo:table-cell>
                       </fo:table-row>
                       
                       <#if itemAdjustment != 0>
                       <fo:table-row>
                        <fo:table-cell number-columns-spanned="2" ><fo:block><fo:inline font-style="italic"></fo:inline></fo:block></fo:table-cell>
                    </fo:table-row>
                    </#if>
                    <fo:table-row>
                        <fo:table-cell  number-columns-spanned="4">
                            <fo:block border-bottom="0.5px solid">
                            </fo:block>
                            </fo:table-cell>
                         </fo:table-row>
           </#list>


           <#-- summary of order amounts -->
           			
                

           <#-- notes -->
           <#if orderNotes?has_content>
                   <#if showNoteHeadingOnPDF>
                   <fo:table-row>
                       <fo:table-cell number-columns-spanned="3">
                           <fo:block font-weight="bold">${uiLabelMap.OrderNotes}</fo:block>
                           <fo:block><fo:leader leader-length="19cm" leader-pattern="rule"/></fo:block>
                       </fo:table-cell>
                   </fo:table-row>
                   </#if>
                <#list orderNotes as note>
                 <#if (note.internalNote?has_content) && (note.internalNote != "Y")>
                    <fo:table-row>
                        <fo:table-cell number-columns-spanned="1">
                        <fo:block>${note.noteInfo?if_exists}</fo:block>
                    </fo:table-cell>
                        <fo:table-cell number-columns-spanned="2">
                        <#assign notePartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", note.noteParty, "compareDate", note.noteDateTime, "lastNameFirst", "Y", "userLogin", userLogin))/>
                        <fo:block>${uiLabelMap.CommonBy}: ${notePartyNameResult.fullName?default("${uiLabelMap.OrderPartyNameNotFound}")}</fo:block>
                    </fo:table-cell>
                        <fo:table-cell number-columns-spanned="1">
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
	                
    <fo:table font-size="8px" background-color="#fcfcfc">

       <fo:table-column column-width="3.75in" />
       <fo:table-column column-width="3.75in"/>
       <#--<fo:table-header>
           <fo:table-row  margin-left="10px" border-bottom="0.5px solid #000000" border-top="0.5px solid #000000" background-color="#FFFFFF">
               <fo:table-cell><fo:block font-weight="bold" padding="5px"> Instructions</fo:block></fo:table-cell>
               <fo:table-cell text-align="left"><fo:block font-weight="bold" padding="5px">Gift Message</fo:block></fo:table-cell>
           </fo:table-row>
       	</fo:table-header>-->
		<fo:table-body > 
     	 <fo:table-row  margin-left="10px" border-bottom="0.5px solid #000000" border-top="0.5px solid #000000">
               <fo:table-cell>
              <fo:block font-weight="normal" padding="10px">Remarks :
               		 <#if shipGroups?has_content>
	                   <#list shipGroups as shipGroup>
	                      	<#if orderHeader?has_content>
		                  		<#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
							<#else>
		                  		<#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
		                	</#if> 
	                      	${shippingInstructions?if_exists}
	                   </#list>
                  </#if>
                  	<#if shipGroups?has_content>
                   <#list shipGroups as shipGroup>
                      	
                      	${giftMessage?if_exists}
                   </#list>
                  </#if>
                 <fo:block font-weight="normal" padding="2px">TIN : 07890310722</fo:block>
                 <fo:block font-weight="normal" padding="2px">Consignment received in good condition</fo:block>  
                 <fo:block font-weight="normal" padding="2px">Please Sign and return</fo:block>
                 <fo:block font-weight="normal" padding="2px">Please issue cheque in favour of</fo:block>
                 <fo:block font-weight="normal" padding="2px">M/S FERNS 'N' PETALS PVT. LTD. payable at Delhi</fo:block>
                 <fo:block font-weight="normal" padding="2px">All disputes subject to Delhi Jurisdiction</fo:block>
               </fo:block></fo:table-cell>
               <fo:table-cell text-align="right" padding="10px"><fo:block font-weight="normal" padding="10px">
               <fo:block font-weight="normal" padding="20px">For Ferns 'N' Petals Pvt. Ltd. </fo:block>
                 <fo:block font-weight="normal" padding="20px">Authorised Signatory</fo:block>
               	
               </fo:block></fo:table-cell>
           </fo:table-row>
     </fo:table-body>
    </fo:table>
   
           
           </#if>
   
    </#if>
</#escape>