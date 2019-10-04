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
import javolution.util.FastMap; 
import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
 
/*	viewIndex=40;
	viewSize=1000;
	int afcount=0;
	int poscount=0;
	int webcount=0;
	int penords=0;
	int sulords=0;
	int rejords=0;   

GenericValue userLogin = (GenericValue) request.getAttribute("userLogin");
//print "^^^^^^^^^^^^^this is my userlogin^^^^^^^^^^^^^"+userLogin;                              */

/* Map map = FastMap.newInstance();
map.put("viewSize",viewSize);
map.put("viewIndex",viewIndex);     */
/*salechannels=delegator.findList("OrderHeader",null, null, null, null, false);
Iterator iterator = salechannels.iterator();
while ( iterator.hasNext() ){
	      GenericValue  salec=(GenericValue) iterator.next();
	      print("\n\n\nthis is my sales channel enum id??????????????  :  "+salec.getString("salesChannelEnumId"));
	      print("\n\n\n*********** this is my  status id************  :"+salec.getString("statusId"));
	      print("\n\n\n%%%%%%%%%%%%%%%% this is my grandTotal is :"+salec.getString("grandTotal"));
	      				
		
	  }       */
/*List salechannel=new ArrayList();
salechannel.add("WEB_SALES_CHANNEL");
salechannel.add("POS_SALES_CHANNEL");
salechannel.add("UNKNWN_SALES_CHANNEL");
salechannel.add("EBAY_SALES_CHANNEL");
salechannel.add("AFFILIATE_SALES_CHANNEL");  
salechannel.add("AMAZON_SALES_CHANNEL");

details=delegator.findList("OrderItemAndOrderHeader",null, null, null, null, false);

print"&&&&&&&&&&&&&&&&&the values in the details is&&&&&&&&&&&&&&&&&&&"+details.size();  */

/*Iterator iterator = details.iterator();
while ( iterator.hasNext() ){
	      	 GenericValue  salec=(GenericValue) iterator.next();
	     	salechannel=salec.getString("salesChannelEnumId")
	     	statId=salec.getString("statusId")
	       //	 print("\n\n\nthis is my status id??????????????\n\n\n"+salec.getString("statusId"));
	       
	       	  if(salechannel.equals("POS_SALES_CHANNEL") && (statId.equals("ORDER_APPROVED") || statId.equals("ORDER_CREATED") || statId.equals("ORDER_PROCESSING"))){
	 			  	penords++;
	  			 }
	    	else if(statId.equals("ORDER_COMPLETED")){
	     		sulords++;
	   	 		} 
	    	else if(salechannel.equals("WEB_SALES_CHANNEL") && (statId.equals("ORDER_APPROVED") || statId.equals("ORDER_CREATED") || statId.equals("ORDER_PROCESSING"))){
	   			penords++;
	   			}
	   	    else if(statId.equals("ORDER_COMPLETED")){
	     		sulords++;
	   			 } 	
	   	    else if(salechannel.equals("AFFILIATE_SALES_CHANNEL") && (statId.equals("ORDER_APPROVED") || statId.equals("ORDER_CREATED") || statId.equals("ORDER_PROCESSING"))){
	   			penords++;
	   			}
	    	else if(statId.equals("ORDER_COMPLETED")){
	     		sulords++;
	    		}   
	      }          
	      
	   
	print"\n\n\n\n%%%%%%%%%%%%%%%%%%%%%the value of sulords is%%%^******************"+sulords;
	
	print"\n\n\n\n******%%%%%%*****this value of penords is*******%%%%%%********"+penords;

	    print"\n\n\n"    */


   
/* cond2=EntityCondition.makeCondition([
					EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"WEB_SALES_CHANNEL"),
					EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"POS_SALES_CHANNEL"),
					EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"AFFILIATE_SALES_CHANNEL")],
				EntityOperator.OR);
				
		details1=delegator.findList("OrderItemAndOrderHeader",cond2, null, null, null, false);
		print"************the value in the details1 is***************"+details1.size();
				
		dayOrder = EntityUtil.filterByAnd(details1, [statusId : "ORDER_COMPLETED"]);
		dayOrder1 = EntityUtil.filterByAnd(details1, [statusId : "ORDER_APPROVED"]);
		
		print"&&&&&&&&&&&& the value in the condition is &&&&&&&&&&&&&"+cond2;
				
		print"\n\n\n@@@@@@@@@@@@@@@@the value of dayOrder@@@@@@@@@@@@@@\t"+dayOrder1.size();
		print"\n\n\n"     */    
		
/*map.put("salesChannelEnumId",salechannel);
map.put("userLogin",userLogin);
map.put("userLoginId",userLogin.get("userLoginId"));

result = dispatcher.runSync("findOrders", map);
print("%%%%%%%%%%the value in the result is%%%%%%%%%%%%"+result.size());

//print"*********the value of the sale channel is************"+result.salechannel;

//print"+++++++++++sale channellist is+++++++++++"+salechannel  */

//from here actual code get started

cond2=EntityCondition.makeCondition([
					EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"WEB_SALES_CHANNEL"),
					EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"UNKNWN_SALES_CHANNEL")],
				EntityOperator.OR);

details1=delegator.findList("OrderHeader",cond2, null, null, null, false);
	//	//System.out.println("%%%%%%%%%%%the no. of items which come under web sales channel is%%%%%%%%"+details1.size());
		context.tweborders=	details1.size();
		
		dayOrder = EntityUtil.filterByAnd(details1, [statusId : "ORDER_COMPLETED"]);
		dayOrder1 = EntityUtil.filterByAnd(details1, [statusId : "ORDER_APPROVED"]);
		dayOrder2= EntityUtil.filterByAnd(details1, [statusId : "ORDER_CREATED"]);
		dayOrder3=EntityUtil.filterByAnd(details1, [statusId : "ORDER_PROCESSING"]);
		dayOrder4=EntityUtil.filterByAnd(details1, [statusId : "ORDER_REJECTED"]);
		dayOrder5=EntityUtil.filterByAnd(details1, [statusId : "ORDER_CANCELLED"]);
		dayOrder6=EntityUtil.filterByAnd(details1, [statusId : "ORDER_HOLD"]);
		dayOrder7=EntityUtil.filterByAnd(details1, [statusId : "ORDER_SENT"]);
			
		context.cweborders=dayOrder.size();
		context.rweborders=dayOrder4.size()+ dayOrder5.size();
		
		penords=dayOrder1.size()+dayOrder2.size()+dayOrder3.size()+dayOrder7.size()+dayOrder6.size();
		
		context.pweborders=penords;
		
		wtotamount=0;
		icsize=0;
		Iterator iterator = details1.iterator();
			while ( iterator.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator.next();
	      
	      		 statId=salec.get("statusId");
	      		 samt=salec.get("grandTotal")
	      		
	      		 if(statId.equals("ORDER_COMPLETED") || statId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				wtotamount=samt+wtotamount;
	     				
						itemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
			
				
						itemcompleted = EntityUtil.filterByAnd(itemcom, [statusId : "ITEM_COMPLETED"]);
						itemapproved= EntityUtil.filterByAnd(itemcom, [statusId : "ITEM_APPROVED"]);
						icsize=itemcompleted.size()+itemapproved.size()+icsize;
						
	     				
	   	 			} 
			}

		context.totwebamount=wtotamount;
		
		context.icsize=icsize;

			
		
poschannel=delegator.findList("OrderHeader",EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"POS_SALES_CHANNEL"), null, null, null, false);
	
			
		context.tposorders=poschannel.size();
			
		posOrder = EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_COMPLETED"]);
		posOrder1 = EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_APPROVED"]);
		posOrder2= EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_CREATED"]);
		posOrder3=EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_PROCESSING"]);
		posOrder4=EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_REJECTED"]);
		posOrder5=EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_CANCELLED"]);
		posOrder6=EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_HOLD"]);
		posOrder7=EntityUtil.filterByAnd(poschannel, [statusId : "ORDER_SENT"]);
		
		
				
		context.cposorders=posOrder.size();
		context.rposorders=posOrder4.size() + posOrder5.size();
		
		pospenords=posOrder1.size()+ posOrder2.size() + posOrder3.size() + posOrder7.size() + posOrder6.size();
		
		context.pposorders=pospenords;
		

		ptotamount=0;
		picsize=0;
		Iterator iterator1 = poschannel.iterator();
			while ( iterator1.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator1.next();
	      
	      		 staId=salec.get("statusId");
	      		 samot=salec.get("grandTotal")
	      		
	      		 if(staId.equals("ORDER_COMPLETED") || staId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				ptotamount=samot+ptotamount;
	     				
	     				pitemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
			
				
						pitemcompleted = EntityUtil.filterByAnd(pitemcom, [statusId : "ITEM_COMPLETED"]);
						pitemapproved= EntityUtil.filterByAnd(pitemcom, [statusId : "ITEM_APPROVED"]);
						picsize=pitemcompleted.size()+pitemapproved.size()+picsize;
	   	 			} 
			}
	
			 
		context.totposamount=ptotamount;
		
		context.picsize=picsize;
			
affchannel=delegator.findList("OrderHeader",EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"AFFIL_SALES_CHANNEL"), null, null, null, false);
	
		
		context.tafforders=affchannel.size();
				
		affOrder = EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_COMPLETED"]);
		affOrder1 = EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_APPROVED"]);
		affOrder2= EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_CREATED"]);
		affOrder3=EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_PROCESSING"]);
		affOrder4=EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_REJECTED"]);
		affOrder5=EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_CANCELLED"]);
		affOrder6=EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_HOLD"]);
		affOrder7=EntityUtil.filterByAnd(affchannel, [statusId : "ORDER_SENT"]);
		
		
	
		context.cafforders=affOrder.size();
		context.rafforders=affOrder4.size() + affOrder5.size();
		
		affpenords=affOrder1.size() + affOrder2.size() + affOrder3.size() + affOrder7.size() + affOrder6.size() ;
		
		context.pafforders=affpenords;
				
			afftotamount=0;
			aicsize=0;
		Iterator iterator2 = affchannel.iterator();
			while ( iterator2.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator2.next();
	      
	      		 staId=salec.get("statusId");
	      		 samot=salec.get("grandTotal")
	      		
	      		 if(staId.equals("ORDER_COMPLETED") || staId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				afftotamount=samot+afftotamount;
	     				
	     				aitemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
				
						aitemcompleted = EntityUtil.filterByAnd(aitemcom, [statusId : "ITEM_COMPLETED"]);
						aitemapproved= EntityUtil.filterByAnd(aitemcom, [statusId : "ITEM_APPROVED"]);
						aicsize=aitemcompleted.size()+aitemapproved.size()+aicsize;
	   	 			} 
			}
	
		
		context.totaffamount=afftotamount;
		context.aicsize=aicsize;
		
		
		//below comment should be removed whenever google base channel is added in find orders tab
googlechannel=delegator.findList("OrderHeader",EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"GBASE_CHANNEL"), null, null, null, false);
	
		context.tgoogleorders=googlechannel.size();
	
		googleOrder = EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_COMPLETED"]);
		googleOrder1 = EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_APPROVED"]);
		googleOrder2= EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_CREATED"]);
		googleOrder3=EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_PROCESSING"]);
		googleOrder4=EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_REJECTED"]);
		googleOrder5=EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_CANCELLED"]);
		googleOrder6=EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_HOLD"]);
		googleOrder7=EntityUtil.filterByAnd(googlechannel, [statusId : "ORDER_SENT"]);
		
		
				
		context.cgoogleorders=googleOrder.size();
		context.rgoogleorders=googleOrder4.size() + googleOrder5.size();
		
		googlepenords=googleOrder1.size() + googleOrder2.size() + googleOrder3.size() + googleOrder7.size() + googleOrder6.size() ;
		
		context.pgoogleorders=googlepenords;
			
			googletotamount=0;
			gicsize=0;
		Iterator iterator4 = googlechannel.iterator();
			while ( iterator4.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator4.next();
	      	
	      		 staId=salec.get("statusId");
	      		 samot=salec.get("grandTotal")
	      		
	      		 if(staId.equals("ORDER_COMPLETED") || staId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				googletotamount=samot+googletotamount;
	     				
	     				gitemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
			
				
						gitemcompleted = EntityUtil.filterByAnd(gitemcom, [statusId : "ITEM_COMPLETED"]);
						gitemapproved= EntityUtil.filterByAnd(gitemcom, [statusId : "ITEM_APPROVED"]);
						gicsize=gitemcompleted.size()+gitemapproved.size()+gicsize;
	   	 			} 
			}
	      
		
		context.totgoogleamount=googletotamount;
		context.gicsize=gicsize;  
		
ebaychannel=delegator.findList("OrderHeader",EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"EBAY_SALES_CHANNEL"), null, null, null, false);
	
		context.tebayorders=ebaychannel.size();
	
		ebayOrder = EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_COMPLETED"]);
		ebayOrder1 = EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_APPROVED"]);
		ebayOrder2= EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_CREATED"]);
		ebayOrder3=EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_PROCESSING"]);
		ebayOrder4=EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_REJECTED"]);
		ebayOrder5=EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_CANCELLED"]);
		ebayOrder6=EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_HOLD"]);
		ebayOrder7=EntityUtil.filterByAnd(ebaychannel, [statusId : "ORDER_SENT"]);
		
		
				
		context.cebayorders=ebayOrder.size();
		context.rebayorders=ebayOrder4.size() + ebayOrder5.size();
		
		ebaypenords=ebayOrder1.size() + ebayOrder2.size() + ebayOrder3.size() + ebayOrder7.size() + ebayOrder6.size() ;
		
		context.pebayorders=ebaypenords;
			
			ebaytotamount=0;
			eicsize=0;
		Iterator iterator5 = ebaychannel.iterator();
			while ( iterator5.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator5.next();
	      	
	      		 staId=salec.get("statusId");
	      		 samot=salec.get("grandTotal")
	      		
	      		 if(staId.equals("ORDER_COMPLETED") || staId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				ebaytotamount=samot+ebaytotamount;
	     				
	     				eitemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
			
				
						eitemcompleted = EntityUtil.filterByAnd(eitemcom, [statusId : "ITEM_COMPLETED"]);
						eitemapproved= EntityUtil.filterByAnd(eitemcom, [statusId : "ITEM_APPROVED"]);
						eicsize=eitemcompleted.size()+eitemapproved.size()+eicsize;
	   	 			} 
			}
	      
		
		context.totebayamount=ebaytotamount;
		context.eicsize=eicsize; 
		
amazonchannel=delegator.findList("OrderHeader",EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS,"AMAZON_SALES_CHANNEL"), null, null, null, false);
	
		context.tamazonorders=amazonchannel.size();
	
		amazonOrder = EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_COMPLETED"]);
		amazonOrder1 = EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_APPROVED"]);
		amazonOrder2= EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_CREATED"]);
		amazonOrder3=EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_PROCESSING"]);
		amazonOrder4=EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_REJECTED"]);
		amazonOrder5=EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_CANCELLED"]);
		amazonOrder6=EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_HOLD"]);
		amazonOrder7=EntityUtil.filterByAnd(amazonchannel, [statusId : "ORDER_SENT"]);
		
		
				
		context.camazonorders=amazonOrder.size();
		context.ramazonorders=amazonOrder4.size() + amazonOrder5.size();
		
		amazonpenords=amazonOrder1.size() + amazonOrder2.size() + amazonOrder3.size() + amazonOrder7.size() + amazonOrder6.size() ;
		
		context.pamazonorders=amazonpenords;
			
			amazontotamount=0;
			amicsize=0;
		Iterator iterator6 = amazonchannel.iterator();
			while ( iterator6.hasNext() ){
	     		 GenericValue  salec=(GenericValue) iterator6.next();
	      	
	      		 staId=salec.get("statusId");
	      		 samot=salec.get("grandTotal")
	      		
	      		 if(staId.equals("ORDER_COMPLETED") || staId.equals("ORDER_APPROVED")){
	      		 		orderId=salec.get("orderId")
	     				amazontotamount=samot+amazontotamount;
	     				
	     				amitemcom=delegator.findList("OrderItem",EntityCondition.makeCondition("orderId", EntityOperator.EQUALS,orderId), null, null, null, false);
			
				
						amitemcompleted = EntityUtil.filterByAnd(amitemcom, [statusId : "ITEM_COMPLETED"]);
						amitemapproved= EntityUtil.filterByAnd(amitemcom, [statusId : "ITEM_APPROVED"]);
						amicsize=amitemcompleted.size()+amitemapproved.size()+amicsize;
	   	 			} 
			}
	      
		
		context.totamazonamount=amazontotamount;
		context.amicsize=amicsize;                                               