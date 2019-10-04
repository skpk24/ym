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
import org.ofbiz.base.util.*;

module = "HeldOrders.groovy";

//set the page parameters
viewIndex = request.getParameter("viewIndex") ? Integer.valueOf(request.getParameter("viewIndex")) : 1;
context.viewIndex = viewIndex;

viewSize = request.getParameter("viewSize") ? Integer.valueOf(request.getParameter("viewSize")) : 20;
context.viewSize = viewSize;

List orderStatusId = new ArrayList();
orderStatusId.add("ORDER_HOLD");
List orderTypeId = new ArrayList();
orderTypeId.add("SALES_ORDER");

result = dispatcher.runSync("findOrders", [viewSize : viewSize, viewIndex : viewIndex, orderTypeId: orderTypeId, orderStatusId: orderStatusId, userLogin: userLogin]);

// fields from the service call
paramList = request.getAttribute("paramList") ?: "";
context.paramList = paramList;

paramList = result.get("paramList");
context.paramList = paramList;

if (paramList) {
    paramIds = paramList.split("&amp;");
    context.paramIdList = Arrays.asList(paramIds);
}

orderList = result.get("orderList");
context.orderList = orderList;

orderListSize = result.get("orderListSize");
context.orderListSize = orderListSize;

lowIndex = result.get("lowIndex");
context.lowIndex = lowIndex;

highIndex = result.get("highIndex");
context.highIndex = highIndex;
