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

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;
import org.ofbiz.base.util.UtilMisc;

recipeList = null;
		try {
			fromDate = request.getParameter("fromDate");
			thruDate = request.getParameter("thruDate");
			
			EntityCondition condition = null;
			cond = new java.util.ArrayList();
			
			if(UtilValidate.isNotEmpty(fromDate))
				cond.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO,Timestamp.valueOf(fromDate)));
			if(UtilValidate.isNotEmpty(thruDate))
				cond.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO,Timestamp.valueOf(thruDate)));
			
			statusId = request.getParameter("statusId");
			if(UtilValidate.isNotEmpty(statusId))
				cond.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
			
			if(cond.size() > 0)
				condition = EntityCondition.makeCondition(cond,EntityOperator.AND);
			else
				condition = null;
			
			recipeList = delegator.findList("RecipeManagement",
					condition, null, UtilMisc.toList("createdDate DESC"), null, false);
			
			context.recipeList = recipeList;
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.recipeList = null;
			return "error";
		}
		
		
 