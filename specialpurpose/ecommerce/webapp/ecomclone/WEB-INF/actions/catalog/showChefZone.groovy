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
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;

conditionList = [];

recipeTypeList = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "RECIPE_TYPE"), null, ["sequenceId"], null, false);

GenericValue recipeGenValue=null;
Iterator itr=recipeTypeList.iterator();
if(recipeTypeList)
{
	while(itr.hasNext())
	{
		recipeGenValue=itr.next();
		
		firstOne =recipeTypeList.get(0);
	}
}
context.firstOne=firstOne
