

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

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */

import org.ofbiz.base.util.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.transaction.*;
import org.ofbiz.product.store.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.*;

recipeList = new ArrayList();
recipeType = request.getParameter("recipeType")

context.recipeType1 = recipeType;
//createdBy = request.getParameter("createdBy");


 
context.recipeManagementId = org.ofbiz.recipes.RecipeEvents.recipeOfTheWeekId(request, response);

EntityCondition condition = null;


	
List  statusList =  new ArrayList(); 
List  conditionList =  new ArrayList();

statusList.add("RECIPE_REJECTED");
statusList.add("RECIPE_REQUESTED");

conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_IN,statusList));
if(UtilValidate.isNotEmpty(recipeType) && !"all".equals(recipeType))
conditionList.add(EntityCondition.makeCondition("recipeType", recipeType));

List thruDateCond = new ArrayList();
thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
    	
conditionList.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
   
		
 EntityConditionList<EntityCondition> conditions = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
	
 	
recipeTypeMangList = delegator.findList("RecipeManagement",  conditions , null, UtilMisc.toList("createdDate DESC"), null,false);
 
 context.recipeTypeMangList = recipeTypeMangList;

  
List feedbackList = delegator.findList("RecipeComments",  EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"FB_APPROVED") , null, UtilMisc.toList("createdDate DESC"), null,false);
context.feedbackList = feedbackList;


TestimonialList = null;
try {
	 
	createdBy = request.getParameter("createdBy");
	createdDate = request.getParameter("createdDate");
	recipeCommentId = request.getParameter("recipeCommentId");
	statusId = request.getParameter("statusId");
	EntityCondition testCondition = null;
	testCond = new java.util.ArrayList();
	
	if(UtilValidate.isNotEmpty(createdBy))
		testCond.add(EntityCondition.makeCondition("createdBy",EntityOperator.EQUALS,createdBy));
	if(UtilValidate.isNotEmpty(createdDate))
		testCond.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO,Timestamp.valueOf(createdDate)));
	
	if(UtilValidate.isNotEmpty(statusId))
		testCond.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
	if(UtilValidate.isNotEmpty(recipeCommentId))
		testCond.add(EntityCondition.makeCondition("recipeCommentId",EntityOperator.EQUALS,recipeCommentId));
		testCond.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,"CUST_FEEDBACK"));
	if(testCond.size() > 0)
		testCondition = EntityCondition.makeCondition(testCond,EntityOperator.AND);
	else
		testCondition = null;
	 
	TestimonialList = delegator.findList("RecipeComments",
			testCondition, null, UtilMisc.toList("createdDate DESC"), null, false);
	
	context.TestimonialList = TestimonialList;
	return "success";
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	context.TestimonialList = null;
	return "error";
}		
