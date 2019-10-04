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

import java.util.*
import java.sql.Timestamp
import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityFindOptions
import org.ofbiz.product.inventory.*
import org.ofbiz.entity.util.*
import javolution.util.FastList;


List storeTypes = FastList.newInstance();
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "WEB_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "MCOM_SALES_CHANNEL"));
storeTypes.add(EntityCondition.makeCondition("defaultSalesChannelEnumId", EntityOperator.EQUALS, "FBOOK_SALES_CHANNEL"));
storeTypesCond = EntityCondition.makeCondition(storeTypes, EntityOperator.OR);
productStores = delegator.findList("ProductStore", storeTypesCond, null, ["defaultSalesChannelEnumId"], null, false);

context.productStores = productStores;

session = request.getSession();
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
{
	productStoreId = session.getAttribute("productStoreId");
}
if(productStoreId == null)
{
	productStoreId = context.get("productStoreId");
}
if(productStoreId == null && productStores != null && productStores.size() > 0)
{
	if(productStores.size() > 2)
		productStoreId = productStores.get(2).getString("productStoreId");
	else
		productStoreId = productStores.get(0).getString("productStoreId");
}

session.setAttribute("productStoreId",productStoreId);

context.productStoreId = productStoreId;

store=delegator.findByPrimaryKey("ProductStore",["productStoreId":productStoreId])
facilityId=null;
if(store!=null)
{
facilityId=store.getString("inventoryFacilityId");
parameters.facilityId=facilityId;
}

facilityId = parameters.facilityId;