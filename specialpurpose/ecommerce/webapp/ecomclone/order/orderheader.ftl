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

<#-- NOTE: this template is used for the orderstatus screen in ecommerce AND for order notification emails through the OrderNoticeEmail.ftl file -->
<#-- the "urlPrefix" value will be prepended to URLs by the ofbizUrl transform if/when there is no "request" object in the context -->
<#if baseEcommerceSecureUrl?exists><#assign urlPrefix = baseEcommerceSecureUrl/></#if>
<#if (orderHeader.externalId)?exists && (orderHeader.externalId)?has_content >
  <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
</#if>

<div id="orderHeader">
<#-- left side -->
	<div style="width:427px; float:left; border-right:1px solid #cccccc; padding-right:10px;padding-left: 20px;">
					 <div class="screenlet" >
					  <#if orderItemShipGroups?has_content>
					    <#--<h3>${uiLabelMap.OrderShippingInformation} Shipping Address</h3>-->
					    <#-- shipping address -->
					    <#assign groupIdx = 0>
					    <#list orderItemShipGroups as shipGroup>
					      <#if orderHeader?has_content>
					        <#assign shippingAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
					        <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
					        <#assign sins = shipGroup.shippingInstructions?if_exists>
					      <#else>
					        <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
					        <#assign groupNumber = groupIdx + 1>
					      </#if>
					      <#if shippingAddress?has_content>
					      <strong>Your Order will be delivered to this address</strong>
					      </#if>
					      <ul style="margin-top:5px; padding:0px;">
				           <#if shippingAddress?has_content>
				           <li>
				            <ul style="margin:0px; padding:0px; margin-bottom:10px;">
				              <li style="margin-bottom:3px;">
				               <#-- ${uiLabelMap.OrderDestination} [${groupNumber}]-->
				                 <#if shippingAddress.attnName?has_content>${shippingAddress.attnName}</#if> <#if shippingAddress.toName?has_content><#--${uiLabelMap.CommonTo}:--> ${shippingAddress.toName}</#if>
				              </li>
				              <li style="margin-bottom:3px;">
				              
				              </li>
				              <li style="margin-bottom:3px;">
				                ${shippingAddress.address1}
				              </li>
				              <li style="margin-bottom:3px;">
				                <#if shippingAddress.address2?has_content>${shippingAddress.address2?if_exists}</#if>
				              </li>
				              <li style="margin-bottom:3px;">
				                <#if shippingAddress.area?has_content>${shippingAddress.area?if_exists}</#if>
				              </li>
				              <li style="margin-bottom:3px;">
				                <#if shippingAddress.directions?has_content>${shippingAddress.directions?if_exists}</#if>
				              </li>
				              <li style="margin-bottom:3px;">
				                <#assign shippingStateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
				               ${shippingAddress.city}-${shippingAddress.postalCode?if_exists}<#if shippingStateGeo?has_content></br><div style="margin-top:3px;"> ${shippingStateGeo.geoName?capitalize?if_exists}</div></#if> 
				              </li>
				              <li style="margin-bottom:3px;">
				                <#assign shippingCountryGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.countryGeoId?if_exists}, false))?if_exists />
				                <#if shippingCountryGeo?has_content>${shippingCountryGeo.geoName?if_exists}</#if>
				              </li>
				              <li style="margin-bottom:3px;">
				              	<#if partyContactMechValueMaps?has_content>
				              	 <#list partyContactMechValueMaps as partyContactMechValueMap>
				        			<#assign contactMech = partyContactMechValueMap.contactMech?if_exists />
				        			 <#if contactMech.contactMechTypeId?if_exists = "TELECOM_NUMBER">
				        			 	<#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
				        			 	<#if telecomNumber?exists>
				        			 		Phone No : ${telecomNumber.contactNumber?if_exists}
				        			 	</#if>
				        			 </#if>
				        		 </#list>
				        		</#if>	
              				  </li>
				            </ul>
				          </li>
				        </#if>
				       <#-- <li>
				          <ul style="margin:0px; padding:0px;">
				            <li>
				              ${uiLabelMap.OrderMethod}:
				              <#if orderHeader?has_content>
				                <#assign shipmentMethodType = shipGroup.getRelatedOne("ShipmentMethodType")?if_exists>
				                <#assign carrierPartyId = shipGroup.carrierPartyId?if_exists>
				              <#else>
				                <#assign shipmentMethodType = cart.getShipmentMethodType(groupIdx)?if_exists>
				                <#assign carrierPartyId = cart.getCarrierPartyId(groupIdx)?if_exists>
				              </#if>
				              <#if carrierPartyId?exists && carrierPartyId != "_NA_">${carrierPartyId?if_exists}</#if>
				              ${(shipmentMethodType.description)?default("N/A")}
				            </li>
				            <li>
				              <#if shippingAccount?exists>${uiLabelMap.AccountingUseAccount}: ${shippingAccount}</#if>
				            </li>
				          </ul>
				        </li>-->
				        <#-- tracking number -->
				        <#if trackingNumber?has_content || orderShipmentInfoSummaryList?has_content>
				          <li style="margin-bottom:3px;">
				            ${uiLabelMap.OrderTrackingNumber}
				            <#-- TODO: add links to UPS/FEDEX/etc based on carrier partyId  -->
				            <#if shipGroup.trackingNumber?has_content>
				              ${shipGroup.trackingNumber}
				            </#if>
				            <#if orderShipmentInfoSummaryList?has_content>
				              <#list orderShipmentInfoSummaryList as orderShipmentInfoSummary>
				                <#if (orderShipmentInfoSummaryList?size > 1)>${orderShipmentInfoSummary.shipmentPackageSeqId}: </#if>
				                Code: ${orderShipmentInfoSummary.trackingCode?default("[Not Yet Known]")}
				                <#if orderShipmentInfoSummary.boxNumber?has_content>${uiLabelMap.OrderBoxNumber}${orderShipmentInfoSummary.boxNumber}</#if>
				                <#if orderShipmentInfoSummary.carrierPartyId?has_content>(${uiLabelMap.ProductCarrier}: ${orderShipmentInfoSummary.carrierPartyId})</#if>
				              </#list>
				            </#if>
				          </li>
				          </#if>
				          <#-- splitting preference -->
				         <#-- <#if orderHeader?has_content>
				            <#assign maySplit = shipGroup.maySplit?default("N")>
				          <#else>
				            <#assign maySplit = cart.getMaySplit(groupIdx)?default("N")>
				          </#if>
				          <li>
				            ${uiLabelMap.OrderSplittingPreference}:
				            <#if maySplit?default("N") == "N">${uiLabelMap.OrderPleaseWaitUntilBeforeShipping}.</#if>
				            <#if maySplit?default("N") == "Y">${uiLabelMap.OrderPleaseShipItemsBecomeAvailable}.</#if>
				          </li>
				          <#-- shipping instructions 
				          <#if orderHeader?has_content>
				            <#assign shippingInstructions = shipGroup.shippingInstructions?if_exists>
				          <#else>
				            <#assign shippingInstructions =  cart.getShippingInstructions(groupIdx)?if_exists>
				          </#if>
				   
				          <#if shippingInstructions?has_content>
				            <li>
				              ${uiLabelMap.OrderInstructions}
				              ${shippingInstructions}
				            </li>
				          </#if>-->
				          <#-- gift settings -->
				          <#--<#if orderHeader?has_content>
				            <#assign isGift = shipGroup.isGift?default("N")>
				            <#assign giftMessage = shipGroup.giftMessage?if_exists>
				          <#else>
				            <#assign isGift = cart.getIsGift(groupIdx)?default("N")>
				            <#assign giftMessage = cart.getGiftMessage(groupIdx)?if_exists>
				          </#if>
				          <#if productStore.showCheckoutGiftOptions?if_exists != "N">
				          <li>
				            ${uiLabelMap.OrderGift}?
				            <#if isGift?default("N") == "N">${uiLabelMap.OrderThisIsNotGift}.</#if>
				            <#if isGift?default("N") == "Y">${uiLabelMap.OrderThisIsGift}.</#if>
				          </li>
				          <#if giftMessage?has_content>
				            <li>
				              ${uiLabelMap.OrderGiftMessage}
				              ${giftMessage}
				            </li>
				          </#if>
				        </#if>-->
				        <#if shipGroup_has_next>
				        </#if>
				      </ul>
				      <#assign groupIdx = groupIdx + 1>
				    </#list><#-- end list of orderItemShipGroups -->
				  </#if>
				  </div>
				 <#-- orderid --> 
				  <div class="screenlet">
				  	
				    <#if maySelectItems?default("N") == "Y" && returnLink?default("N") == "Y" && (orderHeader.statusId)?if_exists == "ORDER_COMPLETED" && roleTypeId?if_exists == "PLACING_CUSTOMER">
				    	 <#if shippingAddress?has_content>
				      		<#--a href="<@ofbizUrl fullPath="true">makeReturn?orderId=${orderHeader.orderId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.OrderRequestReturn}</a-->
				      	</#if>
				    </#if>
				    <#if orderHeader?has_content>
				    <#--${uiLabelMap.OrderOrder}--><strong>Order ID : </strong>
				    
				      <!--a href="<@ofbizUrl fullPath="true">orderstatus?orderId=${orderHeader.orderId}</@ofbizUrl>" class="lightbuttontext">${orderHeader.orderId}</a--><strong>${orderHeader.orderId}</strong>
				      <#if (orderHeader.statusId)?if_exists == "ORDER_REJECTED">
				    		<br /> <b style="font-size:12px !important; "><strong>Status : Rejected</strong></b> 
				    	</#if>
				    </#if>
				    <#--${uiLabelMap.CommonInformation}
				    <#if (orderHeader.orderId)?exists>
				      ${externalOrder?if_exists} [ <a href="<@ofbizUrl fullPath="true">order.pdf?orderId=${(orderHeader.orderId)?if_exists}</@ofbizUrl>" target="_blank" class="lightbuttontext">PDF</a> ]
				    </#if>-->
				  </div>
				 <#-- orderid end -->  

  </div>
  
  <div style="width:377px; float:left; margin-left:10px;">
  
				    <div class="screenlet">
 						 <#if orderHeader?has_content>
						 <#if orderHeader.slot?has_content>
						 <strong>Your Delivery Slot</strong> </br>
						 </#if>
						 <ul>
							 <li> 
							 		<#if orderHeader.slot?has_content>
							 		<#--	Slot Type - ${orderHeader.slot?if_exists}-->
							 		</#if>
							 </li>
						<#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
		        				 <#assign slotTime123 = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(currentSlotList, Static["org.ofbiz.base.util.UtilMisc"].toMap("slotType",orderHeader.get("slot")?if_exists)), "slotTiming", true)>
							        <#if slotTime123?has_content>
						               
						                  <#list slotTime123 as slotTiming>
						                  <#assign slotTiming = slotTiming?if_exists>
						                 <#-- <li>Slot Time - ${slotTiming?if_exists}</li> -->
						                  </#list>
						            </#if>
							 <li>
							 	<#assign ohd = orderHeader.deliveryDate?if_exists>
							 	<#if ohd?has_content>
							 		<#--Delivery Date - ${ohd?string("dd-MM-yyyy")?if_exists}-->${ohd?string("EEE d MMM yyyy")?if_exists} between ${slotTiming?if_exists}
							 	</#if>
							 	
							 
							</li>
						</ul>	 
						<#else>
							<#if cart.slot?has_content>
							<strong>Your Delivery Slot</strong> </br>
						 <ul>
							<li>
									<#if cart.slot?has_content>
										<#assign slotType = cart.slot?if_exists>
										<#--Slot Type - ${slotType?if_exists}-->
									</#if>
							</li>
							
							 
							  <#assign currentSlotList=Static["org.ofbiz.order.shoppingcart.CheckOutEvents"].getAllSlots(delegator)>
							 	<#assign slotTimeTem=Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(currentSlotList, Static["org.ofbiz.base.util.UtilMisc"].toMap("slotType",cart.slot?if_exists))>
		        				 <#assign slotTime123 = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(slotTimeTem, "slotTiming", true)>
							        <#if slotTime123?has_content>
						               
						                  <#list slotTime123 as slotTiming>
						                  <#assign slotTiming1 = slotTiming>
						                 <#-- <li>Slot Time - ${slotTiming?if_exists}</li>-->
						                  </#list>
						            </#if>
							
							
							<li><#if cart.deliveryDate?has_content><#--Delivery Date - ${cart.deliveryDate?string("dd-MM-yyyy")?if_exists}-->  ${cart.deliveryDate?string("EEE d MMM yyyy")?if_exists} between ${slotTiming1?if_exists}</#if></li>
						</ul>	
						</#if>
						</#if>
					 </div>

					<#--<div class="screenlet" style="border:1px solid #cccccc;">
					  
					  <#-- placing customer information -->
					  <#--<ul>
					    <#if localOrderReadHelper?exists && orderHeader?has_content>
					      <#assign displayParty = localOrderReadHelper.getPlacingParty()?if_exists/>
					      <#if displayParty?has_content>
					        <#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
					      </#if>
					      <li>
					        ${uiLabelMap.PartyName}
					        ${(displayPartyNameResult.fullName)?default("[Name Not Found]")}
					      </li>
					    </#if>
					    <#-- order status information -->
					    <#--<li>
					      ${uiLabelMap.CommonStatus}
					      <#if orderHeader?has_content>
					        ${localOrderReadHelper.getStatusString(locale)}
					      <#else>
					        ${uiLabelMap.OrderNotYetOrdered}
					      </#if>
					    </li>
					    <#-- ordered date -->
					    <#--<#if orderHeader?has_content>
					      <li>
					        ${uiLabelMap.CommonDate}
					        ${orderHeader.orderDate?string("dd-MM-yyyy")?if_exists}
					      </li>
					    </#if>
					    <#if distributorId?exists>
					      <li>
					        ${uiLabelMap.OrderDistributor}
					        ${distributorId}
					      </li>
					    </#if>
					  </ul>
					</div>-->
			
					<div class="screenlet">
					  <#if paymentMethods?has_content || paymentMethodType?has_content || billingAccount?has_content>
					    <#-- order payment info -->
					    <#--${uiLabelMap.AccountingPaymentInformation}--> <strong>Your Method of Payment</strong>
					    <#-- offline payment address infomation :: change this to use Company's address -->
					    
					    <ul>
					      <#if !paymentMethod?has_content && paymentMethodType?has_content>
					        <li>
					          <#if paymentMethodType.paymentMethodTypeId == "EXT_OFFLINE">
					            ${uiLabelMap.AccountingOfflinePayment}
					            <#if orderHeader?has_content && paymentAddress?has_content>
					              ${uiLabelMap.OrderSendPaymentTo}:
					              <#if paymentAddress.toName?has_content>${paymentAddress.toName}</#if>
					              <#if paymentAddress.attnName?has_content>${uiLabelMap.PartyAddrAttnName}: ${paymentAddress.attnName}</#if>
					              ${paymentAddress.address1}
					              <#if paymentAddress.address2?has_content>${paymentAddress.address2}</#if>
					              <#assign paymentStateGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
					              ${paymentAddress.city}<#if paymentStateGeo?has_content>, ${paymentStateGeo.geoName?if_exists}</#if> ${paymentAddress.postalCode?if_exists}
					              <#assign paymentCountryGeo = (delegator.findOne("Geo", {"geoId", paymentAddress.countryGeoId?if_exists}, false))?if_exists />
					              <#if paymentCountryGeo?has_content>${paymentCountryGeo.geoName?if_exists}</#if>
					              ${uiLabelMap.EcommerceBeSureToIncludeYourOrderNb}
					            </#if>
					          <#else>
					            <#assign outputted = true>
					            <#assign cartr = paymentMethodTypess?if_exists/>
					    			<#if cartr?has_content && cartr != "">
					    				<#list cartr as ss>
					    					<#assign method = ss/>
					    						<#if !(method == "EXT_BILLACT" || method == "EXT_INVITE")>
					    						
						    						<#assign methodDesc = delegator.findByPrimaryKey("PaymentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodTypeId", method))?if_exists>
														<#if methodDesc?has_content>	
															${methodDesc.description?if_exists}</br>
														</#if>
												</#if>	
					    				</#list>
					    				<#else>
					    				 <#assign paymentMths = paymentMth?if_exists/>
					    				 <#if paymentMths?has_content && paymentMths != "">
					    				 	<#list paymentMths as mt>
					    				 		<#assign method1 = mt/>
					    				 		<#--if !(method1 == "EXT_BILLACT" || method1 == "EXT_INVITE")-->
						    				 		<#assign methodDesc1 = delegator.findByPrimaryKey("PaymentMethodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodTypeId", method1))?if_exists>
														<#if methodDesc1?has_content>
															${methodDesc1.description?if_exists}</br>
														</#if>
												<#--/#if-->		
					    				 	</#list>
					    				 </#if>
					             		<#--${paymentMethodType.get('description')}-->
					 				
					             	</#if>	
					          </#if> 
					        </li>
					      </#if>
					      <#if paymentMethods?has_content>
					        <#list paymentMethods as paymentMethod>
					          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
					            <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
					            <#assign formattedCardNumber = Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)>
					          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId>
					            <#assign giftCard = paymentMethod.getRelatedOne("GiftCard")>
					          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId>
					            <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
					          </#if>
					          <#-- credit card info -->
					          <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId && creditCard?has_content>
					            <#if outputted?default(false)>
					            </#if>
					            <#assign pmBillingAddress = creditCard.getRelatedOne("PostalAddress")?if_exists>
					            <li>
					              <ul>
					                <li> ${uiLabelMap.AccountingCreditCard}
					                  <#if creditCard.companyNameOnCard?has_content>${creditCard.companyNameOnCard}</#if>
					                  <#if creditCard.titleOnCard?has_content>${creditCard.titleOnCard}</#if>
					                  ${creditCard.firstNameOnCard}
					                  <#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}</#if>
					                  ${creditCard.lastNameOnCard}
					                  <#if creditCard.suffixOnCard?has_content>${creditCard.suffixOnCard}</#if>
					                </li>
					                <li>${formattedCardNumber}</li>
					              </ul>
					            </li>
					            <#-- Gift Card info -->
					          <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId && giftCard?has_content>
					            <#if outputted?default(false)>
					            </#if>
					            <#if giftCard?has_content && giftCard.cardNumber?has_content>
					              <#assign pmBillingAddress = giftCard.getRelatedOne("PostalAddress")?if_exists>
					              <#assign giftCardNumber = "">
					              <#assign pcardNumber = giftCard.cardNumber>
					              <#if pcardNumber?has_content>
					                <#assign psize = pcardNumber?length - 4>
					                <#if 0 < psize>
					                  <#list 0 .. psize-1 as foo>
					                    <#assign giftCardNumber = giftCardNumber + "*">
					                  </#list>
					                  <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
					                <#else>
					                  <#assign giftCardNumber = pcardNumber>
					                </#if>
					              </#if>
					            </#if>
					            <li>
					              ${uiLabelMap.AccountingGiftCard}
					              ${giftCardNumber}
					            </li>
					            <#-- EFT account info -->
					          <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId && eftAccount?has_content>
					            <#if outputted?default(false)>
					            </#if>
					            <#assign pmBillingAddress = eftAccount.getRelatedOne("PostalAddress")?if_exists>
					            <li>
					              <ul>
					                <li>
					                  ${uiLabelMap.AccountingEFTAccount}
					                  ${eftAccount.nameOnAccount?if_exists}
					                </li>
					                <li>
					                  <#if eftAccount.companyNameOnAccount?has_content>${eftAccount.companyNameOnAccount}</#if>
					                </li>
					                <li>
					                  ${uiLabelMap.AccountingBank}: ${eftAccount.bankName}, ${eftAccount.routingNumber}
					                </li>
					                <li>
					                  ${uiLabelMap.AccountingAccount} #: ${eftAccount.accountNumber}
					                </li>
					              </ul>
					            </li>
					          </#if>
					          <#if pmBillingAddress?has_content>
					            <li>
					              <ul>
					                <li>
					                  <#if pmBillingAddress.toName?has_content>${uiLabelMap.CommonTo}: ${pmBillingAddress.toName}</#if>
					                </li>
					                <li>
					                  <#if pmBillingAddress.attnName?has_content>${uiLabelMap.CommonAttn}: ${pmBillingAddress.attnName}</#if>
					                </li>
					                <li>
					                  ${pmBillingAddress.address1}
					                </li>
					                <li>
					                  <#if pmBillingAddress.address2?has_content>${pmBillingAddress.address2}</#if>
					                </li>
					                <li>
					                <#assign pmBillingStateGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.stateProvinceGeoId?if_exists}, false))?if_exists />
					                ${pmBillingAddress.city}<#if pmBillingStateGeo?has_content>, ${ pmBillingStateGeo.geoName?if_exists}</#if> ${pmBillingAddress.postalCode?if_exists}
					                <#assign pmBillingCountryGeo = (delegator.findOne("Geo", {"geoId", pmBillingAddress.countryGeoId?if_exists}, false))?if_exists />
					                <#if pmBillingCountryGeo?has_content>${pmBillingCountryGeo.geoName?if_exists}</#if>
					                </li>
					              </ul>
					            </li>
					          </#if>
					          <#assign outputted = true>
					        </#list>
					      </#if>
					      <#-- billing account info -->
					      <#if billingAccount?has_content>
					        <#if outputted?default(false)>
					        </#if>
					        <#assign outputted = true>
					        <li>
					         <#-- ${uiLabelMap.AccountingBillingAccount}
					          #${billingAccount.billingAccountId?if_exists} - ${billingAccount.description?if_exists}
					           Youmart Savings-->
					        </li>
					      </#if>
					      <#if (customerPoNumberSet?has_content)>
					        <li>
					          ${uiLabelMap.OrderPurchaseOrderNumber}
					          <#list customerPoNumberSet as customerPoNumber>
					            ${customerPoNumber?if_exists}
					          </#list>
					        </li>
					      </#if>
					    </ul>
					  </#if>
					</div>
					<div>
						<div class="screenlet">  
						<#if cart?exists>
						<#if cart.getShippingInstructions()?has_content>
						<strong>${uiLabelMap.PageTitleSpecialInstructions}</strong><br/>
						${cart.getShippingInstructions()?if_exists}
						</#if>
						</#if>
						
						<#if sins?has_content>
						<strong>${uiLabelMap.PageTitleSpecialInstructions}</strong><br/>
						${sins?if_exists}
						</#if>
						
						</div>
						    <#if (orderHeader.orderId)?exists>
						      ${externalOrder?if_exists}  <a href="<@ofbizUrl fullPath="true" style="color:#6f9fb4; text-decoration:underline; cursor:pointer;">order.pdf?orderId=${(orderHeader.orderId)?if_exists}</@ofbizUrl>" target="_blank" class="lightbuttontext">View Invoice</a> 
						    <#else>
						    <#-- <a href"#" class="lightbuttontext">View Invoice</a>-->
						    </#if>
					</div>
  </div>  

<#-- right side -->

</div>
<div style="clear:both;"></div>



