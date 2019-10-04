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
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;


double calcItemTotal(List headers) {
    double total = 0.00;
    headers.each { header ->
        total += header.grandTotal ?: 0.00;
    }
    return total;
}

double calcItemCount(List items) {
    double count = 0.00;
    items.each { item ->
        count += item.quantity ?: 0.00;
    }
    return count;
}

cal = Calendar.getInstance();
cal.set(Calendar.AM_PM, Calendar.AM);
cal.set(Calendar.HOUR, 0);
cal.set(Calendar.MINUTE, 0);
cal.set(Calendar.SECOND, 0);
cal.set(Calendar.MILLISECOND, 0);
dayBegin = new Timestamp(cal.getTime().getTime());

cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
weekBegin = new Timestamp(cal.getTime().getTime());

cal.set(Calendar.DAY_OF_MONTH, 1);
monthBegin = new Timestamp(cal.getTime().getTime());

cal.set(Calendar.MONTH, 0);
yearBegin = new Timestamp(cal.getTime().getTime());
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
{
	productStoreId = session.getAttribute("productStoreId");
}

// order totals and item counts
ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CREATED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
dayItems = delegator.findList("OrderHeaderAndItems", ecl, null, null, null, false);

ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId), 
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, dayBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
dayHeaders = delegator.findList("OrderHeader", ecl, null, null, null, false);

dayItemTotal = calcItemTotal(dayHeaders);
dayItemCount = calcItemCount(dayItems);
context.dayItemTotal = dayItemTotal;
context.dayItemCount = dayItemCount;

ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),                        
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CREATED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, weekBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
weekItems = delegator.findList("OrderHeaderAndItems", ecl, null, null, null, false);

ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"),                        
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, weekBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
weekHeaders = delegator.findList("OrderHeader", ecl, null, null, null, false);

weekItemTotal = calcItemTotal(weekHeaders);
weekItemCount = calcItemCount(weekItems);
context.weekItemTotal = weekItemTotal;
context.weekItemCount = weekItemCount;

ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CREATED"),
                        EntityCondition.makeCondition("itemStatusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
monthItems = delegator.findList("OrderHeaderAndItems", ecl, null, null, null, false);

ecl = EntityCondition.makeCondition([
                        EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CREATED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"),
                        EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, monthBegin),
                        EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
                    EntityOperator.AND);
monthHeaders = delegator.findList("OrderHeader", ecl, null, null, null, false);

	try{
		facilityList=delegator.findList("Facility",null,null,null,null,false);
	}
	catch(GenericEntityException  e)
	{
		e.printStackTrace();
	}


context.facilityList=facilityList
monthItemTotal = calcItemTotal(monthHeaders);
monthItemCount = calcItemCount(monthItems);
context.monthItemTotal = monthItemTotal;
context.monthItemCount = monthItemCount;
