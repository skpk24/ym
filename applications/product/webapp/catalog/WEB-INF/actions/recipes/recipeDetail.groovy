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

recipeComment = null;
testimonialId = request.getParameter("testimonialId");
recipeComment = delegator.findOne("RecipeComments",
		UtilMisc.toMap("recipeCommentId",testimonialId), false);

context.recipeComment = recipeComment;

        recipe = null;
		try {
			context.recipeTypeList = delegator.findList("Enumeration",
					EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS,"RECIPE_TYPE"), UtilMisc.toSet("enumId","description"), UtilMisc.toList("sequenceId"), null, false);
			             
			recipeManagementId = request.getParameter("recipeId");
			if(UtilValidate.isEmpty(recipeManagementId))
				recipeManagementId = org.ofbiz.recipes.RecipeEvents.recipeOfTheWeekId(request, response);
			
			recipe = delegator.findOne("RecipeManagement",
					UtilMisc.toMap("recipeManagementId",recipeManagementId), false);
			
			context.recipe = recipe;
			
			recipeIngredientList = delegator.findList("RecipeIngredients",
					EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId), null, null, null, false);
			
			context.recipeIngredientList = recipeIngredientList;
			
			type = parameters.type;
			if(type == null || "".equals(type.trim()))
				type = "RECIPE_COMM_TYPE_BLO";
			
			statusId = parameters.statusId;
			if(statusId == null || "".equals(statusId.trim()))
				statusId = "RECP_COMM_APPROVED";
			
			List cond = UtilMisc.toList(
					EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId),
					EntityCondition.makeCondition("type",EntityOperator.EQUALS,type),
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
			if(!"admin".equals(parameters.from))
				cond.add(EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,null));

			recipeSubCommentList = delegator.findList("RecipeComments",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
					
			
			List cond1 = UtilMisc.toList(
					EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId),
					EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,null),
					EntityCondition.makeCondition("type",EntityOperator.EQUALS,"RECIPE_COMM_TYPE_COM"),
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
			
			recipeCommentList1 = delegator.findList("RecipeComments",
					EntityCondition.makeCondition(cond1,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
			
			
			context.recipeCommentList = recipeSubCommentList;
			context.recipeCommentList1 = recipeCommentList1;
			
			recipeWeek = delegator.findByPrimaryKey("RecipeWeek",UtilMisc.toMap("recipeManagementId", recipeManagementId));
			
			context.recipeWeekRes = recipeWeek;
			
			context.allProdListOfCateg = org.ofbiz.product.product.ProductServices.allProdListOfCateg(delegator, dispatcher, recipeIngredientList);
			
			return "success";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			context.recipe = null;
			context.recipeWeekRes = null;
			context.recipeIngredientList = null;
			context.recipeCommentList = null;
			return "error";
		}
    
		