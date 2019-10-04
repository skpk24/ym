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

import org.ofbiz.product.catalog.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.util.*;


Set fieldToSelect = new ArrayList();
fieldToSelect.add("bannerLinkUrl");
fieldToSelect.add("bannerImageUrl");
fieldToSelect.add("categoryName");

prodCatalogId = CatalogWorker.getCurrentCatalogId(request);

EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);

List condition = new ArrayList();
condition.add(EntityCondition.makeCondition("position", EntityOperator.EQUALS, context.bannerPosition));
condition.add(EntityCondition.makeCondition("catalogId", EntityOperator.EQUALS, prodCatalogId));
condition.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
condition.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),
EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS,null)),EntityOperator.OR));

bannerList = delegator.findList("BannerManagement",EntityCondition.makeCondition(condition, EntityOperator.AND),fieldToSelect, ['sequenceNum'],findOpts,true);

context.bannerList = bannerList;
