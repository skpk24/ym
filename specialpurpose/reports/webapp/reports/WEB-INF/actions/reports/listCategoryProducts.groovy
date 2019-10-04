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


import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

//default this to true, ie only show active
activeOnly = !"false".equals(request.getParameter("activeOnly"));
context.activeOnly = activeOnly;

paramInMap = [:];
paramInMap.productCategoryId = UtilFormatOut.checkNull(request.getParameter("productCategoryId"));
paramInMap.defaultViewSize = 20;
paramInMap.limitView = true;
paramInMap.useCacheForMembers = true;
paramInMap.checkViewAllow = false;
paramInMap.activeOnly = activeOnly;
paramInMap.viewIndexString = parameters.get("VIEW_INDEX");
paramInMap.viewSizeString = parameters.get("VIEW_SIZE");

// Returns: viewIndex, viewSize, lowIndex, highIndex, listSize, productCategory, productCategoryMembers
outMap = dispatcher.runSync("getProductCategoryAndLimitedMembers", paramInMap);
context.viewIndex = outMap.viewIndex;
context.viewSize = outMap.viewSize;
context.lowIndex = outMap.lowIndex;
context.highIndex = outMap.highIndex;
context.listSize = outMap.listSize;
//context.productCategoryMembers = outMap.productCategoryMembers;
List productList = new ArrayList();
productCategoryMembers =  outMap.productCategoryMembers;
if(productCategoryMembers!=null)
 {
    HashMap hm = new HashMap();
    Iterator it = productCategoryMembers.iterator();
    while(it.hasNext())
     {
          GenericValue pcM = (GenericValue)it.next();
           product = pcM.getRelatedOne("Product");
            hm.put("productId" ,product.get("productId")); 
            hm.put("internalName" ,product.get("internalName")); 
            fromDate = request.getParameter("minDate");
			thruDate  = request.getParameter("maxDate");
			List dateCondiList = new ArrayList();
			
			if(fromDate!=null)
			 {
			   dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			 }
			 if(thruDate!=null)
			 {
			   dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			 }
			 print(" after  thruDate ");
			 dateCondiList.add(EntityCondition.makeCondition("productId",  EntityOperator.EQUALS,product.get("productId"));
			 List <GenericValue>orderItems =null;
             orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), UtilMisc.toSet("quantity"), null, null, false);
             itemSold=0.0 ;
           
            Iterator  <GenericValue>orderItemIt = orderItems.iterator();
              while(orderItemIt.hasNext())
              {
               GenericValue orderItem = orderItemIt.next();
               if(orderItem.get("quantity")!=null)
               {
                itemSold = itemSold  + orderItem.getDecimal("quantity");
               }
              }
          
            //TODO end orderItems 
            
            //TODO inventory item 
             List <GenericValue>ItemInventory = delegator.findList("InventoryItem", EntityCondition.makeCondition("productId",  EntityOperator.EQUALS,product.get("productId")), UtilMisc.toSet("quantityOnHandTotal"), null, null, false);
             java.math.BigDecimal inventoryTotal = new java.math.BigDecimal();
            if(ItemInventory!=null)
            {
              Iterator  <GenericValue>invItemIt = ItemInventory.iterator();
              while(invItemIt.hasNext())
              {
               GenericValue invItem =invItemIt.next();
               if(invItem.get("quantityOnHandTotal")!=null)
               {
                inventoryTotal = inventoryTotal  + orderItem.getDecimal("quantityOnHandTotal");
              }
             }
            }
         //TODO end inventory item  
        hm.put("itemSold",itemSold);
        hm.put("qoh",inventoryTotal);
        hm.put("inventoryTotal",inventoryTotal + itemSold);
        productList.add(hm);
     }
}
 print "\n\n ############### productList = "+productList;
 context.productList = productList;
