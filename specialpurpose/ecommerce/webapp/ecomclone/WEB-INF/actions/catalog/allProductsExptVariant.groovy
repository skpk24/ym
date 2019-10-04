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

import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityCondition;

conditionList = [];

//make sure the look up is case insensitive
conditionList.add(EntityCondition.makeCondition("isVariant",EntityOperator.EQUALS, "N"));
conditionList.add(EntityCondition.makeCondition("isVariant",EntityOperator.EQUALS, null));

conditions = EntityCondition.makeCondition(conditionList, EntityOperator.OR);

productList = delegator.findList("Product", conditions, ["productId", "productName", "internalName"] as Set, ["productId"], null, false);
context.productList = productList;
data = "";
if(productList){
	size = productList.size();
	count = 1;
	for(product in productList){
		if(product.productName)
			data = data+"\""+product.productName+"[ID-"+ product.productId+"]\"";
		else if(product.productName)
			data = data+"\""+product.internalName+"[ID-"+ product.productId+"]\"";
		
		if(size != count)
			data = data+",";
			
		count = count+1;
	}
}
context.data = data;

