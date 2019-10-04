/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javolution.util.FastList;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.store.*;
import org.ofbiz.order.shoppingcart.shipping.*;
import org.ofbiz.base.util.UtilDateTime;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.ofbiz.order.shoppingcart.*;

cart = session.getAttribute("shoppingCart");
if (cart && UtilValidate.isNotEmpty(parameters.get("shipping_instructions"))) {
cart.setShippingInstructions(parameters.get("shipping_instructions"));
}
context.ins = cart.getShippingInstructions();

currentmMechId=request.getParameter("shipping_contact_mech_id");
if(UtilValidate.isEmpty(currentmMechId))
currentmMechId=request.getParameter("mechId");
context.mechId=currentmMechId;
party = userLogin.getRelatedOne("Party");
productStore = ProductStoreWorker.getProductStore(request);
int r=UtilDateTime.nowTimestamp().getHours();

int minutes=UtilDateTime.nowTimestamp().getMinutes();
minutes = 0;
String partyId=request.getParameter("partyId");
Timestamp partyDeliveryDate=null;

Map mainMaptoday=new LinkedHashMap();
Map mainMaptomorrow=new LinkedHashMap();
timing=0;
mergeOrderId=request.getParameter("orderId");
condition=request.getParameter("noCodition");

Calendar current = Calendar.getInstance();

SimpleDateFormat ft = new SimpleDateFormat ("DD a");


if(UtilValidate.isNotEmpty(productStore))
{
	cutOffGvtem=productStore.getDouble("cutOffTime");
	blockTime=productStore.getDouble("blockingTime");
	slotDayOption=productStore.getLong("slotDayOption");
	maxInterval=productStore.getLong("slotMaxInterval");
	cutOffGv=(int)cutOffGvtem;
	blokingTime=(int)blockTime;
	context.slotDayOption=slotDayOption;
	context.maxInterval=maxInterval;
}

//for checking order merge
List CondnList=FastList.newInstance();
List contactOrderList=FastList.newInstance();
CondnList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,party.partyId));
CondnList.add(EntityCondition.makeCondition("slotStatus",EntityOperator.EQUALS,"SLOT_ACCEPTED"));
CondnList.add(EntityCondition.makeCondition("orderId",EntityOperator.NOT_EQUAL,null));
CondnList.add(EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,currentmMechId));
CondnList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
List<GenericValue> OrderCollision=delegator.findList("OrderSlot",EntityCondition.makeCondition(CondnList,EntityOperator.AND),null,null,null,false);

List<GenericValue> orderDetailList = new ArrayList();
 if(UtilValidate.isNotEmpty(OrderCollision) && UtilValidate.isEmpty(mergeOrderId) && UtilValidate.isEmpty(condition) )
{
	List OrderListTem=EntityUtil.getFieldListFromEntityList(OrderCollision, "orderId", true);
	GVTemp=EntityUtil.getFirst(OrderCollision);
partyNameGv=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",GVTemp.getString("partyId")));
   if(UtilValidate.isNotEmpty(partyNameGv.firstName) && UtilValidate.isNotEmpty(partyNameGv.lastName))
   name=partyNameGv.firstName+" "+partyNameGv.lastName;
   else
   name=partyNameGv.firstName;
   context.partyName=name;
   
   List orderCondition=new ArrayList();
  List statusList =  new ArrayList();
  statusList.add("ORDER_APPROVED");
  statusList.add("ORDER_CREATED");
  orderCondition.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,OrderListTem));
  orderCondition.add(EntityCondition.makeCondition("statusId",EntityOperator.IN,statusList));
  orderDetailList=delegator.findList("OrderHeader",EntityCondition.makeCondition(orderCondition,EntityOperator.AND),null,UtilMisc.toList("orderDate DESC"),null,false);
  
    if(UtilValidate.isNotEmpty(orderDetailList)){
	   orderApproveList = EntityUtil.filterByAnd(orderDetailList, UtilMisc.toMap("statusId","ORDER_APPROVED"))
	   if(UtilValidate.isNotEmpty(orderApproveList) && orderApproveList.size()>0){
	   context.sameSlogFlag="true";
	   }
   }
  }
  
  if(orderDetailList != null){
  
	 List CondnList1=orderDetailList.slot; 
	 List CondnList2=orderDetailList.deliveryDate;
	 	Iterator itrdsd = CondnList1.iterator();
	 	int count= 0 ;
		while(itrdsd.hasNext()){ 
		String slottype = itrdsd.next(); 
		 
		if(UtilValidate.isNotEmpty(slottype)){
			List<GenericValue> OrderSlotType=delegator.findList("OrderSlotType",EntityCondition.makeCondition("slotType",EntityOperator.EQUALS,slottype),null,null,null,false);
			if(UtilValidate.isNotEmpty(OrderSlotType)){
			 Iterator itrdsd2 = OrderSlotType.iterator();
			 while(itrdsd2.hasNext()){
				 GenericValue gv  = itrdsd2.next();
				 int dateOfTime=gv.get("cutOffTime");
				 String timePeriod = gv.getString("timePeriod");
				 String orderDate   = CondnList2.get(count);
				 orderDate = orderDate.split(" ")[0];
				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH");
				 orderDate = orderDate+" "+ (timePeriod.equals("PM") ? (12+dateOfTime) : (dateOfTime.toString().length() != 2 ? ("0"+dateOfTime):dateOfTime) );
				 Date deliveryDate = simpleDateFormat.parse(orderDate);
				 Date todayDate = current.getTime();	
				
				 if(todayDate.before(deliveryDate)){ 
				   // 
				 } else{
				 	//GenericEntity removeGV = orderDetailList.get(count) ;
				 	if(orderDetailList && orderDetailList.size() >= count){
				 		orderDetailList.remove(count);
				 	}
				 }
			 } 	
			}
		}
		count++;
		}
  
  }
  
   
  
  context.orderList=orderDetailList;
  
//If yes take the slot and delivery date for particular order

if(UtilValidate.isNotEmpty(mergeOrderId)){
	List<GenericValue> OrderMerge=delegator.findList("OrderSlot",EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,mergeOrderId),null,null,null,false);
	if(UtilValidate.isNotEmpty(OrderMerge)){
		firstEntry=EntityUtil.getFirst(OrderMerge);
		context.dayOption=firstEntry.getString("slotType")+"_"+firstEntry.getString("deliveryDate");
	}
}
if (cart) {
   shippingEstWpr = new ShippingEstimateWrapper(dispatcher, cart, 0);
   context.shippingEstWpr = shippingEstWpr;
   context.carrierShipmentMethodList = shippingEstWpr.getShippingMethods();
}

context.shoppingCart = cart;
context.userLogin = userLogin;
context.productStoreId = productStore.productStoreId;
context.productStore = productStore;
context.emailList = ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false);

if (cart.getShipmentMethodTypeId() && cart.getCarrierPartyId()) {
   context.chosenShippingMethod = cart.getShipmentMethodTypeId() + '@' + cart.getCarrierPartyId();
}
//getting available slot and max delivery
List<GenericValue> maxDelivery= CheckOutEvents.getAllSlots(delegator);
List orderType=new ArrayList();
List blockingDays = new ArrayList();

cutOffTimeSlotWise = new java.util.HashMap();
cutOffTimeSlotWiseInMinute = new java.util.HashMap();

List MaxDeliveryList=new ArrayList();
if(!UtilValidate.isEmpty(maxDelivery)){
   for(i=0;i<maxDelivery.size();i++){
	   GenericValue gv = maxDelivery.get(i)
	   orderType.add(i,gv.get("slotType"));
	   blockingDays.add(i,gv.get("blockDays"));
	   MaxDeliveryList.add(i,gv.getDouble("maxDelivery").intValue());
	   cutOffTimeSlotWise.put(gv.get("slotType"),gv.get("cutOffTimeInHour"));
	   cutOffTimeSlotWiseInMinute.put(gv.get("slotType"),gv.get("cutOffTimeInMinute"));
   }
}


//checking the condition

if(!UtilValidate.isEmpty(maxDelivery)){ //maxDelivery getting all the slot for the day,which they provide the service.EX:-SLOT1,SLOT2,SLOT3
	for(int j=0;j<slotDayOption;j++){		// for many days to get the service,this will be store setting
	   Map tem=new LinkedHashMap();
	   deliveryDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),j);
	   		Map listCount=new HashMap();
		   for(int t=0;t<orderType.size();t++) {
			   listCount.put(t,0);
		   }
		   for(int t=0;t<orderType.size();t++){
		   promoConditions = new ArrayList();
		   promoConditions.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.EQUALS,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),j)));
		   List slotStatus  =  new ArrayList();
		  
		   promoConditions.add(EntityCondition.makeCondition("slotStatus", EntityOperator.NOT_EQUAL,"SLOT_COMPLETED"));
		   promoConditions.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS,orderType.get(t)));
 		   ordersList1= delegator.findList("OrderSlot", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), null,UtilMisc.toList("orderId DESC"), null, false);
 		
 		   List orderIds = EntityUtil.getFieldListFromEntityList(ordersList1, "orderId", true);
 		   promoConditions.clear();
 		   promoConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN,orderIds));
 		   promoConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,["ORDER_APPROVED","ORDER_COMPLETED","ORDER_DISPATCHED"]));
 		   temp = delegator.findList("OrderHeader", EntityCondition.makeCondition(promoConditions, EntityOperator.AND), UtilMisc.toSet("orderId"),null, null, false);
 		   temp1 = EntityUtil.getFieldListFromEntityList(temp, "orderId", true);
 		  
 		   temp2 = EntityUtil.filterByCondition(ordersList1, EntityCondition.makeCondition("orderId",EntityOperator.NOT_IN,temp1));
 		  

 		   if(temp2.size()>0)
 		   ordersList2 =   EntityUtil.getFieldListFromEntityList(temp2,"contactMechId", true);
 		   else
 		   ordersList2=EntityUtil.getFieldListFromEntityList(ordersList1, "contactMechId", true);
 		   
 		   if(!UtilValidate.isEmpty(ordersList1)){
		   listCount.put(t,ordersList2.size());   
		   //this will  be checking,how many orderrs has been booked for the day.
 		   }
 		   
 		 
		   }
		 
		
		if(UtilValidate.isNotEmpty(condition)){
		 DeliveryList= EntityUtil.filterByCondition(OrderCollision, EntityCondition.makeCondition("deliveryDate",EntityOperator.EQUALS,deliveryDate));
		 for(int r1=0;r1<orderType.size();r1++){
			 boolean blocked = false;
			   if(UtilValidate.isNotEmpty(DeliveryList)){
				tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
			  }
			  String blockDays = blockingDays.get(r1);
			  if(UtilValidate.isNotEmpty(blockDays)){
				  try{
					  blockDay  = blockDays.split(","); 
					  bit = blockDay[j];
					  
//					  bit = "2014-07-24 00:00:00~~2014-07-24 15:00:00" Format should be like this
							  blockTimings  = bit.split("~~");
							  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							  Timestamp start = null;
							  Timestamp end = null;
									if (UtilValidate.isNotEmpty(blockTimings[0])&& UtilValidate.isNotEmpty(blockTimings[1])) {
										Date d = (Date) sdf.parse(blockTimings[0]);
										start = new Timestamp(d.getTime());
										Date d1 = (Date) sdf.parse(blockTimings[1]);
										end = new Timestamp(d1.getTime());
										
										Timestamp now =  UtilDateTime.nowTimestamp();
										Calendar cal = Calendar.getInstance();
//										cal.setTime(start);
//										cal.add(Calendar.DAY_OF_WEEK, j);
//										start.setTime(cal.getTime().getTime());
//										
//										cal.setTime(end);
//										cal.add(Calendar.DAY_OF_WEEK, j);
//										end.setTime(cal.getTime().getTime());
										
										cal.setTime(now);
										cal.add(Calendar.DAY_OF_WEEK, j);
										now.setTime(cal.getTime().getTime());
										
										 if( now.after(start)  && now.before(end) )
										 blocked = true;
								}
				  }catch(Exception e){
					  
				  }
			  }
			  
		   cutOffGvNew = cutOffTimeSlotWise.(orderType.get(r1));
		   cutOffGvNewInMinute = cutOffTimeSlotWiseInMinute.(orderType.get(r1));
		   
		    
		   
		   if(j==0 && UtilValidate.isEmpty(DeliveryList)) {
				if(r < cutOffGvNew && minutes <=cutOffGvNewInMinute && listCount.get(r1)<MaxDeliveryList.get(r1))
			 		tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
				else
					tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
					
				 if(blocked)
					 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
				 
				 
				if("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1)))
						tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
					 
			}
			
		   if(j!=0 && UtilValidate.isEmpty(DeliveryList)){
				 if(listCount.get(r1)<MaxDeliveryList.get(r1))
					 tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
				 else
					 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
				 
				 if(j == 1 && ("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1)))){
					 if(r < cutOffGvNew && minutes <= cutOffGvNewInMinute && listCount.get(r1) < MaxDeliveryList.get(r1))
					 		tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
						else
							tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
				 	}
				 
				 if(blocked)
					 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
			}
		
		mainMaptoday=tem;
		mainMaptomorrow.put(j, mainMaptoday);
	//	print "\n\n\n\n mainMaptomorrow"+mainMaptomorrow
		}
	}
	else  {
		 
			 for(int r1=0;r1<orderType.size();r1++)	{
							cutOffGvNew = cutOffTimeSlotWise.(orderType.get(r1));
						    cutOffGvNewInMinute = cutOffTimeSlotWiseInMinute.(orderType.get(r1));
							 boolean blocked = false;

						    String blockDays = blockingDays.get(r1);
 							  if(UtilValidate.isNotEmpty(blockDays)){
								  try{
									  blockDay  = blockDays.split(","); 
									  bit = blockDay[j];
									
									//  bit = "2014-07-24 11:00:39~~2014-07-24 12:33:39,2014-07-25 11:00:39~~2014-07-25 12:33:39";
									  blockTimings  = bit.split("~~");
									  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
									  Timestamp start = null;
									  Timestamp end = null;
									  
									  
								
											if (UtilValidate.isNotEmpty(blockTimings[0]) && UtilValidate.isNotEmpty(blockTimings[1])) {
												Date d = (Date) sdf.parse(blockTimings[0]);
												start = new Timestamp(d.getTime());
												Date d1 = (Date) sdf.parse(blockTimings[1]);
												end = new Timestamp(d1.getTime());
												
												Timestamp now =  UtilDateTime.nowTimestamp();
												Calendar cal = Calendar.getInstance();
//												cal.setTime(start);
//												cal.add(Calendar.DAY_OF_WEEK, j);
//												start.setTime(cal.getTime().getTime());
//												
//												cal.setTime(end);
//												cal.add(Calendar.DAY_OF_WEEK, j);
//												end.setTime(cal.getTime().getTime());
												
												cal.setTime(now);
												cal.add(Calendar.DAY_OF_WEEK, j);
												now.setTime(cal.getTime().getTime());
												
												 if( now.after(start)  && now.before(end) )
												 blocked = true;
										}
								  }catch(Exception e){
									  
								  }
							  }
  					   if(j==0)
						    {
 								if(r < cutOffGvNew && minutes <= cutOffGvNewInMinute && listCount.get(r1)<MaxDeliveryList.get(r1))
								 	tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
								else
									tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
								
								 if(blocked)
									 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked"); 
								
								if("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1)))
									tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
								
								
								
							}
					   else
							{
							 if(listCount.get(r1)<MaxDeliveryList.get(r1))
								 tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
							 else
								 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
							
							if(j == 1 && ("SLOT1".equals(orderType.get(r1)) || "SLOT2".equals(orderType.get(r1))))
							 	{
							 		if(r < cutOffGvNew && minutes <= cutOffGvNewInMinute && listCount.get(r1)<MaxDeliveryList.get(r1))
								 		tem.put(orderType.get(r1)+"_"+deliveryDate,"Available");
									else
										tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
							 	}
							
							 if(blocked)
								 tem.put(orderType.get(r1)+"_"+deliveryDate,"Blocked");
							}
						mainMaptoday=tem;
						mainMaptomorrow.put(j, mainMaptoday);
			 }
		}
	} // for loop closes
} // if block closes
shipmentMethod = delegator.findList("ShipmentMethodType",null, null, null, null, false);
orderSlotStatus = delegator.findList("OrderSlot",EntityCondition.makeCondition("slotStatus", EntityOperator.EQUALS,"SLOT_REQUESTED"), null, null, null, false);

if(!UtilValidate.isEmpty(orderSlotStatus)){

   for(i=0;i<orderSlotStatus.size();i++){
	   
	   GenericValue gv = orderSlotStatus.get(i);

	   d=(UtilDateTime.nowTimestamp().getTime()-gv.getTimestamp("createdStamp").getTime())/60000;
   
	   if(d>blokingTime)
		   gv.remove();
	   
   }
   }


context.shipmentMethod=shipmentMethod
context.today=mainMaptomorrow;
context.slottype1=maxDelivery;