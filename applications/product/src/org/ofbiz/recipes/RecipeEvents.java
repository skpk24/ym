/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.recipes;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
//import javolution.util.FastList;
//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.oreilly.servlet.MultipartRequest;

/**
 * Product Information Related Events
 */
public class RecipeEvents {

    public static final String module = RecipeEvents.class.getName();
    public static final String resource = "ProductErrorUiLabels";

    /**
     * 
     * Updates/adds keywords for all products
     * @param request HTTPRequest object for the current request
     * @param response HTTPResponse object for the current request
     * @return String specifying the exit status of this event
     * 
     */
    
    public static String createRecipe(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
		String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/recipes/";
		GenericValue productStore = ProductStoreWorker.getProductStore(request);
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		try {
			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			
 			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());

			String recipeType = mr.getParameter("recipeType");
			String recipeName = mr.getParameter("recipeName");
			String description = mr.getParameter("description");
			String variations = mr.getParameter("variations");
			String allowComments = mr.getParameter("allowComments");
			String shareProfile = mr.getParameter("shareProfile");
			
			GenericValue  recipeManagement = delegator.makeValue("RecipeManagement");
			String recipeManagementId = delegator.getNextSeqId("RecipeManagement");
			
			File oldFile = new File(filePath+fileName);
			
			String extension = "";
			
			int i = fileName.lastIndexOf('.');
			if (i > 0) {
			    extension = fileName.substring(i);
			}
			
			File newFile = new File(filePath+recipeManagementId+extension);
			oldFile.renameTo(newFile);
			
			String statusId = "RECIPE_REQUESTED";
			if("Y".equals(productStore.getString("autoApproveReceipe")))
				statusId = "RECIPE_APPROVED";
			
			recipeManagement.put("recipeManagementId", recipeManagementId);
			recipeManagement.put("recipeType",recipeType);
			recipeManagement.put("recipeName",recipeName);
			recipeManagement.put("statusId",statusId);
			recipeManagement.put("recipeImgUrl", "/images/recipes/"+recipeManagementId+extension);
			recipeManagement.put("description",description);
			recipeManagement.put("variations",variations);
			recipeManagement.put("productStoreId",productStore.getString("productStoreId"));
			recipeManagement.put("shareProfile",shareProfile);
			recipeManagement.put("allowComments",allowComments);
			recipeManagement.put("createdBy",userLogin.getString("userLoginId"));
			recipeManagement.put("createdDate",nowTimestamp);
			
			boolean beganTransaction = TransactionUtil.begin();
			if(beganTransaction)
			{
				delegator.create(recipeManagement);
				
				String ingStatusId = "RECIPE_ING_REQUESTED";
				if("Y".equals(productStore.getString("autoApproveIngReceipe")))
					ingStatusId = "RECIPE_ING_APPROVED";
				
				createRecipeIngredients(delegator, recipeManagementId, ingStatusId , userLogin, mr);
				TransactionUtil.commit();
			}
			String msg = null;
			if("RECIPE_REQUESTED".equals(statusId))
				msg = " Successfully Added , It will reflect in site by end of the day ";
			else if("RECIPE_APPROVED".equals(statusId))
				msg = " Successfully Added ";
			
			request.setAttribute("_EVENT_MESSAGE_", msg);
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_"," Failed To create Recipe ");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
    }
    
    /*
     * @Ajaya
     * Remove Recipe Ingredients
     */
    
    
    public static String removeRecipeIngredients(HttpServletRequest request, HttpServletResponse response){
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	
    	String recipeIngredientsIds[] = request.getParameterValues("recipeIngredientsId");
    	if(UtilValidate.isNotEmpty(recipeIngredientsIds))
    	{
    		List<String> ingredients = Arrays.asList(recipeIngredientsIds);
    		try {
    			TransactionUtil.begin();
    			
				delegator.removeByCondition("RecipeIngredients", 
						EntityCondition.makeCondition("recipeIngredientsId",EntityOperator.IN,ingredients));
				
				TransactionUtil.commit();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				request.setAttribute("_ERROR_MESSAGE_"," Failed To Remove Recipe Ingredients");
				e.printStackTrace();
				return "error";
			}
    	}
    	request.setAttribute("_EVENT_MESSAGE_", "Ingredients Removed Successfully");
    	return "success";
    }
    
    /*
     * @Ajaya
     * update a recipe with the changes
     */
    public static String updateRecipe(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
		String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/recipes/";
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		try {
			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			
 			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());

			String recipeManagementId = mr.getParameter("recipeManagementId");
			if(UtilValidate.isEmpty(recipeManagementId))
			{
				request.setAttribute("_ERROR_MESSAGE_","Invalid Recipe Id");
				return "error";
			}
			String recipeType = mr.getParameter("recipeType");
			String recipeName = mr.getParameter("recipeName");
			String description = mr.getParameter("description");
			String variations = mr.getParameter("variations");
			String thruDate = mr.getParameter("thruDate");
			String productId = mr.getParameter("productId");
			String quantity = mr.getParameter("quantity");
			
			GenericValue  recipeManagement = delegator.findOne("RecipeManagement", false, 
															UtilMisc.toMap("recipeManagementId",recipeManagementId));
			if(UtilValidate.isEmpty(recipeManagement))
			{
				request.setAttribute("_ERROR_MESSAGE_","Invalid Recipe Id Passed, No Recipe found");
				return "error";
			}
			if(UtilValidate.isNotEmpty(fileName))
			{
				File oldFile = new File(filePath+fileName);
				String extension = "";
				int i = fileName.lastIndexOf('.');
				if (i > 0) {
				    extension = fileName.substring(i);
				}
				File newFile = new File(filePath+recipeManagementId+extension);
				oldFile.renameTo(newFile);
				
				recipeManagement.put("recipeImgUrl", "/images/recipes/"+recipeManagementId+extension);
			}
			
			recipeManagement.put("recipeType",recipeType);
			recipeManagement.put("recipeName",recipeName);
			recipeManagement.put("description",description);
			recipeManagement.put("variations",variations);
			recipeManagement.put("modifiedBy",userLogin.getString("userLoginId"));
			recipeManagement.put("modifiedDate",nowTimestamp);
			if(UtilValidate.isNotEmpty(thruDate))
				recipeManagement.put("thruDate",Timestamp.valueOf(thruDate));
			
			boolean beganTransaction = TransactionUtil.begin();
			if(beganTransaction)
			{
				delegator.store(recipeManagement);
				
				GenericValue productStore = 
					ProductStoreWorker.getProductStore(recipeManagement.getString("productStoreId"), delegator);
				String ingStatusId = "RECIPE_ING_REQUESTED";
				if("Y".equals(productStore.getString("autoApproveIngReceipe")))
					ingStatusId = "RECIPE_ING_APPROVED";
				

        		GenericValue recipeIngredient = delegator.makeValue("RecipeIngredients");
        		recipeIngredient.put("recipeIngredientsId", delegator.getNextSeqId("RecipeIngredients"));
        		recipeIngredient.put("recipeManagementId", recipeManagementId);
        		recipeIngredient.put("quantity", quantity);
        		recipeIngredient.put("statusId",ingStatusId);
        		recipeIngredient.put("createdBy",userLogin.getString("userLoginId"));
        		recipeIngredient.put("createdDate",nowTimestamp);
				if(UtilValidate.isNotEmpty(productId))
				{
					GenericValue product = delegator.findOne("Product", true, UtilMisc.toMap("productId",productId));
					GenericValue parentProduct = ProductWorker.getParentProduct(productId, delegator);
					if(UtilValidate.isNotEmpty(parentProduct))
						{
							productId = parentProduct.getString("productId");
							product = parentProduct;
						}
					recipeIngredient.put("productId",productId);
					recipeIngredient.put("productName",product.getString("internalName"));
				}
				recipeIngredient.create();
				
				
				
				createRecipeIngredients(delegator, recipeManagementId, ingStatusId, userLogin, mr);
				
				TransactionUtil.commit();
			}
			String msg = "Successfully Updated";
			
			request.setAttribute("_EVENT_MESSAGE_", msg);
			
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_"," Failed To update Recipe ");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
    }
    
    
    /*
     * @Mamtha
     * update a testimonial with the changes
     */
    public static String updateTestimonial(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
		String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/recipes/";
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		try {
			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			
 			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());

			String recipeCommentId = mr.getParameter("recipeCommentId");
			if(UtilValidate.isEmpty(recipeCommentId))
			{
				request.setAttribute("_ERROR_MESSAGE_","Invalid Testimonial Id");
				return "error";
			}
			
			String message = mr.getParameter("area1");
			
			GenericValue  recipeComments = delegator.findOne("RecipeComments", false, 
															UtilMisc.toMap("recipeCommentId",recipeCommentId));
			if(UtilValidate.isEmpty(recipeComments))
			{
				request.setAttribute("_ERROR_MESSAGE_","Invalid Testimonial Id Passed, No Testimonial found");
				return "error";
			}
			if(UtilValidate.isNotEmpty(fileName))
			{
				File oldFile = new File(filePath+fileName);
				String extension = "";
				int i = fileName.lastIndexOf('.');
				if (i > 0) {
				    extension = fileName.substring(i);
				}
				File newFile = new File(filePath+recipeCommentId+extension);
				oldFile.renameTo(newFile);
				
				recipeComments.put("feedbackImgUrl", "/images/recipes/"+recipeCommentId+extension);
			}
			
			recipeComments.put("message",message);
			recipeComments.put("modifiedBy",userLogin.getString("userLoginId"));
			recipeComments.put("modifiedDate",nowTimestamp);
			delegator.store(recipeComments);
			 
			String msg = "Successfully Updated";
			
			request.setAttribute("_EVENT_MESSAGE_", msg);
			
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_"," Failed To update Testimonial ");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
    }
    public static String createRecipeIngredients(Delegator delegator , String recipeManagementId , String statusId ,
    		GenericValue userLogin , MultipartRequest mr) throws Exception{
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        String quantity = mr.getParameter("qty");
        int count = 1;
        if(UtilValidate.isNotEmpty(quantity))
        	count = Integer.parseInt(quantity);
        
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();
        GenericValue  recipeIngredient = null;
        for(int i=1; i<=count;i++)
        {
        	String ingredient = mr.getParameter("ingredients_"+i);
        	String ingredientPrd = mr.getParameter("ingredients_Prod_"+i);
        	String ingredientQty = mr.getParameter("ingredients_Qty_"+i);
        	
        	if(UtilValidate.isNotEmpty(ingredient))
			{
        		recipeIngredient = delegator.makeValue("RecipeIngredients");
        		recipeIngredient.put("recipeIngredientsId", delegator.getNextSeqId("RecipeIngredients"));
        		recipeIngredient.put("recipeManagementId", recipeManagementId);
        		recipeIngredient.put("quantity", ingredientQty);
        		recipeIngredient.put("statusId",statusId);
        		recipeIngredient.put("createdBy",userLogin.getString("userLoginId"));
        		recipeIngredient.put("createdDate",nowTimestamp);
				if(UtilValidate.isNotEmpty(ingredientPrd))
				{
					GenericValue parentProduct = ProductWorker.getParentProduct(ingredientPrd, delegator);
					if(UtilValidate.isNotEmpty(parentProduct))ingredientPrd = parentProduct.getString("productId");
					recipeIngredient.put("productId",ingredientPrd);
				}
				if(UtilValidate.isNotEmpty(ingredient))
					recipeIngredient.put("productName",ingredient);
				
				recipeIngredient.put("productName",ingredient);
				toBeStored.add(recipeIngredient);
				recipeIngredient = null;
			}
        }
		delegator.storeAll(toBeStored);
        return "success";
    }
    
    
    public static String newlyAddedRecipes(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue>  recipeList = null;
		try {
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			
			List cond = UtilMisc.toList(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECIPE_APPROVED"),
								EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECIPE_OF_WEEK"));
			
			recipeList = delegator.findList("RecipeManagement",
					EntityCondition.makeCondition(cond,EntityOperator.OR),
					null, UtilMisc.toList("createdDate DESC"), null, false);
			
			request.setAttribute("recipeList", recipeList);
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			request.setAttribute("_ERROR_MESSAGE_"," Failed To get recipe list");
			request.setAttribute("recipeList", new ArrayList());
			return "error";
		}
    }
    
    public static String recipeOfTheWeek(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue>  recipeList = null;
		try {
			GenericValue recipe = recipeOfTheWeekObject(request, response);
			
			GenericValue gv = null;
			if(UtilValidate.isNotEmpty(recipe))
			gv = delegator.findByPrimaryKey("RecipeWeek", UtilMisc.toMap("recipeManagementId",recipe.getString("recipeManagementId")));
			request.setAttribute("receipeOfWeek", gv);
			request.setAttribute("recipe", recipe);
			if(UtilValidate.isNotEmpty(recipe))
			{
			request.setAttribute("recipeIngredientList", 
					recipeIngredientDetail(delegator, recipe.getString("recipeManagementId")));
			//request.setAttribute("recipeCommentList", commentList(delegator, recipe.getString("recipeManagementId")));
			}
			return "success";
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

			request.setAttribute("_ERROR_MESSAGE_"," Failed To get recipe list");
			request.setAttribute("recipeList", new ArrayList());
			return "error";
		}
    }
    public static String recipeOfTheWeekId(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue>  recipeList = null;
		try {
			GenericValue recipe = recipeOfTheWeekObject(request, response);
			if(UtilValidate.isNotEmpty(recipe))
				return recipe.getString("recipeManagementId");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		return null;
    }
    public static GenericValue recipeOfTheWeekObject(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue>  recipeList = null;
		try {
			/*List cond = UtilMisc.toList(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECIPE_OF_WEEK"),
					EntityCondition.makeCondition("modifiedDate",EntityOperator.LESS_THAN_EQUAL_TO,
											UtilDateTime.getWeekEnd(UtilDateTime.nowTimestamp())),
					EntityCondition.makeCondition("modifiedDate",EntityOperator.GREATER_THAN_EQUAL_TO,
											UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp())));
			
			recipeList = delegator.findList("RecipeManagement",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, UtilMisc.toList("modifiedDate DESC"), null, false);*/
			
			recipeList = delegator.findList("RecipeManagement",
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECIPE_OF_WEEK"), null, UtilMisc.toList("modifiedDate DESC"), null, false);
			
			GenericValue recipe = null;
			if(UtilValidate.isNotEmpty(recipeList))
				recipe = recipeList.get(0);
			
			if(UtilValidate.isNotEmpty(recipe))
				return recipe;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		return null;
    }
    
    public static String recipeDetail(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue recipe = null;
		try {
			String recipeManagementId = request.getParameter("recipeId");
			recipe = delegator.findOne("RecipeManagement",
					UtilMisc.toMap("recipeManagementId",recipeManagementId), false);
			
			GenericValue recipeWeek = delegator.findByPrimaryKey("RecipeWeek",UtilMisc.toMap("recipeManagementId", recipeManagementId));
			
			request.setAttribute("recipe", recipe);
			request.setAttribute("recipeWeekRes", recipeWeek);
			request.setAttribute("recipeIngredientList", recipeIngredientDetail(delegator, recipeManagementId));
			//request.setAttribute("recipeCommentList", commentList(delegator, recipeManagementId));
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

			request.setAttribute("_ERROR_MESSAGE_"," Failed To get recipe detail");
			request.setAttribute("recipe", new ArrayList());
			request.setAttribute("recipeWeekRes", new ArrayList());
			request.setAttribute("recipeIngredientList", new ArrayList());
			request.setAttribute("recipeCommentList", new ArrayList());
			
			return "error";
		}
    }
    
    public static List<GenericValue> recipeIngredientDetail(Delegator delegator ,String recipeManagementId) {
        //String errMsg = "";
        GenericValue recipe = null;
        List<GenericValue>  recipeIngredientList = null;
		try {
			
			List cond = UtilMisc.toList(
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECIPE_ING_APPROVED"),
					EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId));
			
			recipeIngredientList = delegator.findList("RecipeIngredients",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, null, null, false);
			return recipeIngredientList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();

			return recipeIngredientList;
		}
    }
    
    public static List<GenericValue> commentList(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
        Delegator delegator = (Delegator) request.getAttribute("delegator");
		String recipeManagementId = request.getParameter("recipeId");
		
		return commentList(delegator, recipeManagementId);
    }
    
    public static List<GenericValue> commentList(Delegator delegator , String recipeManagementId) {
        //String errMsg = "";
        List<GenericValue>  recipeSubCommentList = null;
		try {
			List cond = UtilMisc.toList(
					EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId),
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECP_COMM_APPROVED"),
					EntityCondition.makeCondition("type",EntityOperator.EQUALS,"RECIPE_COMM_TYPE_COM"),
					EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,null));
			
			recipeSubCommentList = delegator.findList("RecipeComments",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
			
			return recipeSubCommentList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return recipeSubCommentList;
		}
    }
    
    
    public static List<GenericValue> subCommentList(GenericDelegator delegator,String recipeCommentId) {
        //String errMsg = "";
        List<GenericValue>  recipeSubCommentList = null;
		try {
			List cond = UtilMisc.toList(
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"RECP_COMM_APPROVED"),
					EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,recipeCommentId));
			
			recipeSubCommentList = delegator.findList("RecipeComments",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
			return recipeSubCommentList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return recipeSubCommentList;
		}
    }
    
    public static List<GenericValue> adminSubCommentList(GenericDelegator delegator, String recipeCommentId,
    																		String type, String statusId) {
    	if(UtilValidate.isEmpty(type))
			type = "RECIPE_COMM_TYPE_COM";
    	if(UtilValidate.isEmpty(statusId))
    		statusId = "RECP_COMM_APPROVED";
		
        //String errMsg = "";
        List<GenericValue>  recipeSubCommentList = null;
		try {
			List cond = UtilMisc.toList(
					EntityCondition.makeCondition("parentCommentId",EntityOperator.EQUALS,recipeCommentId),
					EntityCondition.makeCondition("type",EntityOperator.EQUALS,type),
					EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId));
			
			recipeSubCommentList = delegator.findList("RecipeComments",
					EntityCondition.makeCondition(cond,EntityOperator.AND), null, UtilMisc.toList("createdDate DESC"), null, false);
			return recipeSubCommentList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return recipeSubCommentList;
		}
    }
    
    
    public static String addComment(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue recipe = null;
        List<GenericValue>  recipeIngredientList = null;
        
		try {
			String recipeManagementId = request.getParameter("recipeId");
			String parentCommentId = request.getParameter("parentCommentId");
			String rating = request.getParameter("my_input");
			String title = request.getParameter("title");
			String message = request.getParameter("message");
			
			GenericValue  recipeComment = delegator.makeValue("RecipeComments");
			
			GenericValue productStore = ProductStoreWorker.getProductStore(request);
			
			String type = request.getParameter("type");
			if(UtilValidate.isEmpty(type) || !"RECIPE_COMM_TYPE_BLO".equals(type))
				type = "RECIPE_COMM_TYPE_COM";
			
			
			String statusId = "RECP_COMM_REQUESTED";
			if("Y".equals(productStore.getString("autoApproveComments")) && "RECIPE_COMM_TYPE_COM".equals(type))
				statusId = "RECP_COMM_APPROVED";
			if("Y".equals(productStore.getString("autoApproveReviews")) && "RECIPE_COMM_TYPE_BLO".equals(type))
				statusId = "RECP_COMM_APPROVED";
			
			recipeComment.put("recipeCommentId", delegator.getNextSeqId("RecipeComments"));
			recipeComment.put("recipeManagementId", recipeManagementId);
			recipeComment.put("parentCommentId", parentCommentId);
			if(UtilValidate.isNotEmpty(rating) && UtilValidate.isPositiveInteger(rating))
				recipeComment.put("rating",Long.parseLong(rating));
			
			recipeComment.put("title",title);
			recipeComment.put("message",message);
			recipeComment.put("statusId",statusId);
			
			recipeComment.put("type", type);
			recipeComment.put("createdBy",userLogin.getString("userLoginId"));
			recipeComment.put("createdDate",UtilDateTime.nowTimestamp());
			
			boolean beganTransaction = TransactionUtil.begin();
			if(beganTransaction)
			{
				delegator.create(recipeComment);
				TransactionUtil.commit();
			}
			String msg = " Successfully Added , It will reflect in site soon ";
			if("RECP_COMM_REQUESTED".equals(statusId))
				msg = " Successfully Added , It will reflect in site soon ";
			else if("RECP_COMM_APPROVED".equals(statusId))
				msg = " Successfully Added ";
			
			request.setAttribute("_EVENT_MESSAGE_", msg);
			String donePage = request.getParameter("donePage");
			if(UtilValidate.isNotEmpty(donePage) && "displayrecipeWeek".equals(donePage))
				return donePage;
			
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
    }
    
    public static String addFeedback(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue recipe = null;
        List<GenericValue>  recipeIngredientList = null;
        
		try {
			String recipeCommentId = delegator.getNextSeqId("RecipeComments");
			String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/recipes/";
			String statusId = "FB_RECEIVED";
			GenericValue  recipeComment = delegator.makeValue("RecipeComments");
			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			String message = mr.getParameter("message"); 
			String type = mr.getParameter("type");
			
 			Enumeration en =  mr.getFileNames();
			String fileName = null;
			String extension = "";
			while(en.hasMoreElements()) {
				fileName = mr.getFilesystemName((String)en.nextElement());
				System.out.println("\n\n fileName"+fileName);
				if(UtilValidate.isNotEmpty(fileName)) {
				File oldFile = new File(filePath+fileName);
				int i = fileName.lastIndexOf('.');
				if (i > 0) {
				    extension = fileName.substring(i);
				}
				File newFile = new File(filePath+recipeCommentId+extension);
				oldFile.renameTo(newFile);
				}
			}
			 
		/*	if(UtilValidate.isEmpty(fileName)) {
				fileName = "default.jpg";
			}*/
			
			
			GenericValue productStore = ProductStoreWorker.getProductStore(request);
			 
			recipeComment.put("recipeCommentId", recipeCommentId);
 			recipeComment.put("message",message);
			recipeComment.put("statusId",statusId);
			if(UtilValidate.isNotEmpty(fileName))
			recipeComment.put("feedbackImgUrl", "/images/recipes/"+recipeCommentId+extension);
			recipeComment.put("type", type);
			
 			
			if(UtilValidate.isNotEmpty(mr.getParameter("createdBy"))){
				List<GenericValue> gv = delegator.findByAndCache("UserLogin", UtilMisc.toMap("partyId", mr.getParameter("createdBy")));
				GenericValue userDetails = EntityUtil.getFirst(gv);
				if(UtilValidate.isNotEmpty(userDetails)){
				recipeComment.put("statusId","FB_APPROVED");
				recipeComment.put("createdBy",userDetails.getString("userLoginId"));
				}
			}else{
			recipeComment.put("createdBy",userLogin.getString("userLoginId"));
			}
			recipeComment.put("createdDate",UtilDateTime.nowTimestamp());
			
			boolean beganTransaction = TransactionUtil.begin();
			if(beganTransaction)
			{
				delegator.create(recipeComment);
				TransactionUtil.commit();
			}
			String msg = "Thanks for your Testimonial!!!";
			 
			request.setAttribute("_EVENT_MESSAGE_", msg);
			String donePage = request.getParameter("donePage");
			if(UtilValidate.isNotEmpty(donePage) && "displayrecipeWeek".equals(donePage))
				return donePage;
			return "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "error";
		}
    }
    
    /**
     * Updates the status of Recipes
     */
    public static Map<String, Object> changeRecipeStatus(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue recipe = null;
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String statusId = (String) context.get("statusId");
        String statusIdTo = (String) context.get("statusIdTo");
        List<String> recipeIdList = (List) context.get("recipeManagementId");
        
        try {
            Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", statusId, "statusIdTo", statusIdTo);
            GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

            if (statusChange == null) {
            	Debug.logInfo("Cannot change recipe status ",module);
            	return ServiceUtil.returnError("Cannot change recipe status from "+statusId+" to "+statusIdTo);
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Cannot change recipe status from "+statusId+" to "+statusIdTo + e.getMessage());
        }
        
        List<GenericValue> toBeStored = new ArrayList();
        List<GenericValue> toBeStoredWeek = new ArrayList();
        //GenericValue gv = null;
        String createdBy = "";
        if(UtilValidate.isNotEmpty(recipeIdList))
        for(String recipeId : recipeIdList){
        	try {
				recipe = delegator.findOne("RecipeManagement", UtilMisc.toMap("recipeManagementId",recipeId), false);
				if(UtilValidate.isNotEmpty(recipe)) {
					createdBy =recipe.getString("createdBy");
	        	 
	        	}
				 //gv = delegator.findByPrimaryKey("RecipeWeek",UtilMisc.toMap("recipeManagementId", recipeId));
				 
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(UtilValidate.isNotEmpty(recipe))
        	{
        		recipe.put("statusId", statusIdTo);
        		recipe.put("modifiedBy", userLogin.getString("userLoginId"));
        		recipe.put("modifiedDate", UtilDateTime.nowTimestamp());
        		toBeStored.add(recipe);
        	}
        	
        	/*if(UtilValidate.isNotEmpty(gv))
        	{
        		gv.put("statusId","RECIPE_OF_WEEK");
        		toBeStoredWeek.add(gv);
        	}*/
        }
        
        try {
        	TransactionUtil.begin();
        	boolean flag = true;
        	if("RECIPE_OF_WEEK".equals(statusIdTo) && toBeStored.size() == 1)
        	{
        		List<GenericValue> toBeStored1 = new ArrayList();
        		
        		try {
    				List<GenericValue> recipes = delegator.findList("RecipeManagement", 
    						EntityCondition.makeCondition("statusId","RECIPE_OF_WEEK"), null, null, null, false);
    				if(UtilValidate.isNotEmpty(recipes))
                	for(GenericValue recipe1 : recipes){
                		recipe1.put("statusId", "RECIPE_OF_WEEK_COMP");
                		recipe1.put("modifiedBy", userLogin.getString("userLoginId"));
                		recipe1.put("modifiedDate", UtilDateTime.nowTimestamp());
                		toBeStored1.add(recipe1);
                	}
    			} catch (GenericEntityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				flag = false;
    			}
    			
    			delegator.storeAll(toBeStored1);
    			// For Mail
        	}
        	
        	delegator.storeAll(toBeStored);
        		if(flag)
        			TransactionUtil.commit();
        		else
        			TransactionUtil.rollback();
        		//			delegator.storeAll(toBeStoredWeek);
			if("RECIPE_OF_WEEK".equals(statusIdTo))
			{
				for(GenericValue toBeStore : toBeStored)
					addLoyaltyPoints(delegator, dispatcher, toBeStore);
				
				if(UtilValidate.isNotEmpty(recipeIdList))
				{
					String recipeId = (String)recipeIdList.get(0);
					sendRecipeOfWeekEmail(delegator, dispatcher, recipeId);
				}
				//RECIPE_OF_WEEK
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("Cannot change recipe status from "+statusId+" to "+statusIdTo + e.getMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
     
    /**
     * Updates the status of Testimonials 
     */
    public static Map<String, Object> changeTestimonialStatus(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        
        GenericValue recipe = null;
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String statusId = (String) context.get("statusId");
        String statusIdTo = (String) context.get("statusIdTo");
        List<String> recipeIdList = (List) context.get("recipeCommentId");
        
        try {
            Map<String, String> statusFields = UtilMisc.<String, String>toMap("statusId", statusId, "statusIdTo", statusIdTo);
            GenericValue statusChange = delegator.findByPrimaryKeyCache("StatusValidChange", statusFields);

            if (statusChange == null) {
            	Debug.logInfo("Cannot change testiminial status ",module);
            	return ServiceUtil.returnError("Cannot change testiminial status from "+statusId+" to "+statusIdTo);
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError("Cannot change testiminial status from "+statusId+" to "+statusIdTo + e.getMessage());
        }
        
        List<GenericValue> toBeStored = new ArrayList();
        List<GenericValue> toBeStoredWeek = new ArrayList();
        String createdBy = "";
        if(UtilValidate.isNotEmpty(recipeIdList))
        for(String recipeId : recipeIdList){
        	try {
				recipe = delegator.findOne("RecipeComments", UtilMisc.toMap("recipeCommentId",recipeId), false);
				if(UtilValidate.isNotEmpty(recipe)) {
					createdBy =recipe.getString("createdBy");
	        	 
	        	}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(UtilValidate.isNotEmpty(recipe))
        	{
        		recipe.put("statusId", statusIdTo);
        		recipe.put("modifiedBy", userLogin.getString("userLoginId"));
        		recipe.put("modifiedDate", UtilDateTime.nowTimestamp());
        		toBeStored.add(recipe);
        	}
        }
        
        try {
        	TransactionUtil.begin();
        	boolean flag = true;
        	
        	delegator.storeAll(toBeStored);
        		if(flag)
        			TransactionUtil.commit();
        		else
        			TransactionUtil.rollback();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ServiceUtil.returnError("Cannot change recipe status from "+statusId+" to "+statusIdTo + e.getMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
    
    public static String changeRecipeCommentStatus(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		
        Delegator delegator = (Delegator) request.getAttribute("delegator");
		String statusIdTo = request.getParameter("statusIdTo");
		String []commentIdList = request.getParameterValues("commentId");
		
		List toBeStored = new ArrayList();
        GenericValue comment = null;
        if(UtilValidate.isNotEmpty(commentIdList))
        for(String commentId : commentIdList){
        	try {
				comment = delegator.findOne("RecipeComments", UtilMisc.toMap("recipeCommentId",commentId), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	if(UtilValidate.isNotEmpty(comment))
        	{
        		//System.out.println("    statusIdTo    "+statusIdTo);
        		comment.put("statusId", statusIdTo);
        		comment.put("modifiedBy", userLogin.getString("userLoginId"));
        		comment.put("modifiedDate", UtilDateTime.nowTimestamp());
        		toBeStored.add(comment);
        	}
        }
        try {
			delegator.storeAll(toBeStored);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_",
					"Cannot change comment status to "+statusIdTo + e.getMessage());
			return "error";
		}
		return "success";
    }
    
    public static Map<String, Object> recipeStatusMail(DispatchContext dctx, Map<String, ? extends Object> context){
		
		Map results = ServiceUtil.returnSuccess();
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        List<String> recipeList = (List) context.get("recipeManagementId");
        if(UtilValidate.isNotEmpty(recipeList))
        for(String recipeId : recipeList)
        {
	        GenericValue recipe = null;
			try {
				recipe = delegator.findOne("RecipeManagement", UtilMisc.toMap("recipeManagementId",recipeId), false);
			} catch (GenericEntityException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if(UtilValidate.isNotEmpty(recipe))
				recipeStatusEMail(dctx, context, recipe);
        }
        return results;
	}
    
    public static Map<String, Object> recipeStatusEMail(DispatchContext dctx, 
    		Map<String, ? extends Object> context, GenericValue recipe){
		
		Map results = ServiceUtil.returnSuccess();
		String emailType = "REC_STATUS_EMAIL";
        String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#recipeStatusMail";
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
       
        List<String> recipeList = (List) context.get("recipeManagementId");
        
			String productStoreId = (String) recipe.get("productStoreId");
			
	        String sendTo = null;
	        if(UtilValidate.isNotEmpty(recipe))
	    	    sendTo = recipe.getString("createdBy");
	        
	        String name = partyName(delegator, recipe.getString("createdBy"));
	        
	        // get the ProductStore email settings
	        GenericValue productStoreEmail = null;
	        try {
	            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
	        }
	
	        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
	        if (UtilValidate.isEmpty(bodyScreenLocation)) {
	            bodyScreenLocation = defaultScreenLocation;
	        }
			        
	        // set the needed variables in new context
	        Map<String, Object> bodyParameters = new HashMap<String, Object>(); //FastMap.newInstance();
	        bodyParameters.put("name", name);
	        
	        GenericValue status = null;
	        try {
				status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId",recipe.getString("statusId")), true);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        String statusId = recipe.getString("statusId");
	        if(UtilValidate.isNotEmpty(status))
	        	statusId = status.getString("description");
	        	
	        bodyParameters.put("status", statusId);
	        bodyParameters.put("recipe", recipe);
	        //bodyParameters.put("contextURL","");
	
	        Map<String, Object> serviceContext = new HashMap<String, Object>(); //FastMap.newInstance();
	        serviceContext.put("bodyScreenUri", bodyScreenLocation);
	        serviceContext.put("bodyParameters", bodyParameters);
	        serviceContext.put("subject", productStoreEmail.getString("subject"));
	        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
	        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
	        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
	        serviceContext.put("contentType", productStoreEmail.get("contentType"));
	        serviceContext.put("sendTo", sendTo);

	        try {
	            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
	
	            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
	                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
	            }
	        } catch (GenericServiceException e) {
	        	Debug.logError(e, "Problem in sending mail in recipeStatusChange of RecipeEvents.java ", module);
	        }
        return results;
	}
    
    public static String partyName(Delegator delegator,String userLoginId){
    	GenericValue userLogin = null;
        try {
        	userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId",userLoginId),true);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String name = "";
     // find the assigned user's email address(s)
        GenericValue person = null;
        if (userLogin != null) {
        	name = partyNameFromPartyId(delegator, userLogin.getString("partyId"));
        }
        return name;
    }
    
    public static String averageReview(Delegator delegator,String recipeManagementId){
    	
    	Map<String,Object> reviewMap=new HashMap<String, Object>(); //FastMap.newInstance();
    	int reviewCount=0;
        try {
        	List<EntityCondition> condn=new ArrayList<EntityCondition>();//FastList.newInstance();
        	condn.add(EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId));
        	condn.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,"RECIPE_COMM_TYPE_COM"));
        	List<GenericValue> reviewList = delegator.findList("RecipeComments", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);
		
		
        if(UtilValidate.isNotEmpty(reviewList))
        {
        	for(GenericValue reviewListGv:reviewList)
        	{
        		if(UtilValidate.isNotEmpty(reviewListGv.getString("message")))
        			
        			reviewCount=reviewCount+1;	
        	}
        	
        }
         	
        } catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
       
        return (String.valueOf(reviewCount)) ;
    }
    
    
 public static String averageReviewRating(Delegator delegator,String recipeManagementId){
	 long reviewRating=(long) 0.0;
		int reviewRatingCount=0;
    	
        try {
        	List<EntityCondition> condn=new ArrayList<EntityCondition>();//FastList.newInstance();
        	condn.add(EntityCondition.makeCondition("recipeManagementId",EntityOperator.EQUALS,recipeManagementId));
        	condn.add(EntityCondition.makeCondition("type",EntityOperator.EQUALS,"RECIPE_COMM_TYPE_COM"));
        	List<GenericValue> reviewList = delegator.findList("RecipeComments", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);
	
		
        if(UtilValidate.isNotEmpty(reviewList))
        {
        	for(GenericValue reviewListGv:reviewList)
        	{
        		
        		if(UtilValidate.isNotEmpty(reviewListGv.getString("rating")))
        		{
        		reviewRating=(reviewListGv.getLong("rating"))+reviewRating;
        		reviewRatingCount=reviewRatingCount+1;
        		}
        	}
        	
        }
        
         } catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
         
        if(reviewRatingCount!=0)
        return (String.valueOf((reviewRating/(double)reviewRatingCount)*12));  //BECAUSE OF DISPLAYING AS THE STAR
        
        else
        	return (String.valueOf((reviewRatingCount)));
    }
    

    public static String partyNameFromPartyId(Delegator delegator,String partyId){
     // find the assigned user's email address(s)
        GenericValue person = null;
    	try {
			person = delegator.findOne("Person", UtilMisc.toMap("partyId",partyId), true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String name = "";
        if(UtilValidate.isNotEmpty(person))
        {
        	String firstName = person.getString("firstName");
        	String middleName = person.getString("middleName");
        	String lastName = person.getString("lastName");
        	String personalTitle = person.getString("personalTitle");
        	
        	if(UtilValidate.isNotEmpty(personalTitle))
        		name = personalTitle;
        	
        	if(UtilValidate.isNotEmpty(name) && UtilValidate.isNotEmpty(firstName))
        		name = name +" "+firstName;
        	else if(UtilValidate.isNotEmpty(firstName))
        		name = firstName;
        	
        	if(UtilValidate.isNotEmpty(name) && UtilValidate.isNotEmpty(middleName))
        		name = name +" "+middleName;
        	else if(UtilValidate.isNotEmpty(middleName))
        		name = middleName;
        	
        	if(UtilValidate.isNotEmpty(name) && UtilValidate.isNotEmpty(lastName))
        		name = name +" "+lastName;
        	else if(UtilValidate.isNotEmpty(lastName))
        		name = lastName;
        	
        }
        return name;
    }
    
    public static void addLoyaltyPoints(Delegator delegator , LocalDispatcher dispatcher, GenericValue recipe){
    	String userLoginId = recipe.getString("createdBy");
    	String productStoreId = recipe.getString("productStoreId");
    	GenericValue productStore = ProductStoreWorker.getProductStore(productStoreId, delegator);
    	
    	String loyalPoints = productStore.getString("recipeLoyalPoints");
    	if(UtilValidate.isEmpty(loyalPoints))
    		loyalPoints = "0";
    	
    	int totalLoyaltyPoints = Integer.parseInt(loyalPoints);
    	int loyaltyRupee = 0;
    	GenericValue loyal = null;
		try {
			loyal = delegator.findByPrimaryKey("LoyaltyPoint", UtilMisc.toMap("userLoginId", userLoginId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if(UtilValidate.isNotEmpty(loyal)){
			String point=loyal.getString("loyaltyPoint");
			String loyal_rupee=loyal.getString("loyaltyRupee");
			
			if(UtilValidate.isEmpty(point)) point = "0";
			if(UtilValidate.isEmpty(loyal_rupee)) loyal_rupee = "0";
			
			int loyalRupee= Integer.parseInt(loyal_rupee);
			if(point != null){
				 totalLoyaltyPoints=Integer.parseInt(point)+totalLoyaltyPoints;
			}
			if(totalLoyaltyPoints >= 200)
				loyaltyRupee = new BigDecimal(loyalPoints).divide(new BigDecimal("200")).intValue();
//				loyaltyRupee = totalLoyaltyPoints/200;
			
			 loyaltyRupee = loyalRupee+loyaltyRupee;
	
			 loyal.set("loyaltyPoint",String.valueOf(totalLoyaltyPoints));
			 loyal.set("loyaltyRupee", String.valueOf(loyaltyRupee));
			 
			 
			 
	         try {
				loyal.store();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	    }else
	    {
	    	if(totalLoyaltyPoints >= 200)
				loyaltyRupee = totalLoyaltyPoints/200;

	    	 loyal = delegator.makeValue("LoyaltyPoint");
	    	 loyal.set("userLoginId", userLoginId);
	    	 loyal.set("loyaltyPoint",String.valueOf(totalLoyaltyPoints));
			 loyal.set("loyaltyRupee", String.valueOf(loyaltyRupee));
			 loyal.set("totalOrderAmt", BigDecimal.ZERO);
			 loyal.set("totalOrders", "0");
			 loyal.set("coupanRupee", "0");
			 loyal.set("isCoupan", "N");
			 loyal.set("coupanCode", "NO_CODE");
	         try {
				loyal.create();
				
				//System.out.println("    loyal   2 "+loyal);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	    }
		createLoyaltyBillingAccount(dispatcher, loyal, userLoginId);
    }
    
    public static String createCustomerDetails(HttpServletRequest request, HttpServletResponse response) {
        //String errMsg = "";
    	
//    	     file
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
		String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/recipes/customerDetails/";
		GenericValue productStore = ProductStoreWorker.getProductStore(request);
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String partyId = userLogin.getString("partyId");
		String createdBy = userLogin.getString("userLoginId");
		if(UtilValidate.isEmpty(userLogin))
		{
			request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
			return "error";
		}
		try {
			MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			
			String recipeManagementId = mr.getParameter("recipeManagementId");  
			
			GenericValue recipeManagement = null;
			 GenericValue recipeManagements = delegator.findByPrimaryKey("RecipeManagement",UtilMisc.toMap("recipeManagementId",recipeManagementId));
			 if(UtilValidate.isEmpty(recipeManagements)){
				 	request.setAttribute("_EVENT_MESSAGE_", "Error:Recipe Not Found For Recipe "+recipeManagementId);
					return "error";
					}
			 
			 String createdBy1 = recipeManagements.getString("createdBy");
			
			 if(!createdBy1.equalsIgnoreCase(userLogin.getString("userLoginId"))){
			 request.setAttribute("_EVENT_MESSAGE_", "Error:Authentication Failed");
			return "error";
			}
			
 			Enumeration en =  mr.getFileNames();
			String fileName = null;
			while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());
			
			String name = mr.getParameter("name");
			String homeTown = mr.getParameter("homeTown");
			String memberSince = mr.getParameter("memberSince");
			String cookingLevel = mr.getParameter("cookingLevel");
			String cookingInterest = mr.getParameter("cookingInterest");
			String hobbies = mr.getParameter("hobbies");
			String aboutChef = mr.getParameter("aboutChef");
			
			String shareYourProfile = mr.getParameter("shareYourProfile");
			String allowComments = mr.getParameter("allowComments");
			String termManagement = mr.getParameter("termManagement");
			String shareProfile = mr.getParameter("shareProfile");
			String termToRespond = mr.getParameter("termToRespond");
				
			recipeManagements.put("statusId","RECIPE_OF_WEEK_RES");
			recipeManagements.store();
			 
			 
			 
			 
//				if(UtilValidate.isNotEmpty(recipeWeek)){
//					String photoPath= recipeWeek.getString("photo");
//					File existingFile = new File(filePath+photoPath.substring(1));
//					 existingFile.delete();
//				}	
		 
			 String extension = "";
			 String f = "";
			 try{
			 File oldFile = new File(filePath+fileName);
				int i = fileName.lastIndexOf('.');
				if (i > 0) {
				    extension = fileName.substring(i);
				    
				}
				File newFile = new File(filePath+recipeManagementId+extension);
 				f = fileName.substring(0,fileName.lastIndexOf("."));
 				oldFile.renameTo(newFile);
				}
catch(Exception e){
	
}
GenericValue recipeWeek = delegator.findByPrimaryKey("RecipeWeek",UtilMisc.toMap("recipeManagementId", recipeManagementId));

		if(UtilValidate.isEmpty(recipeWeek)){
			  recipeManagement = delegator.makeValue("RecipeWeek");
			String seqId = delegator.getNextSeqId("RecipeWeek");
//			String recipeManagementId = delegator.getNextSeqId("RecipeManagement");
			/*recipeManagement.put("partyId", partyId);*/
			recipeManagement.put("userLoginId", createdBy);
//			recipeManagement.put("name",name);
			
			recipeManagement.put("recipeManagementId", recipeManagementId);
			recipeManagement.put("homeTown",homeTown);
//			recipeManagement.put("memberSince",memberSince);
			recipeManagement.put("photo", "/images/recipes/customerDetails/"+recipeManagementId+extension);
			recipeManagement.put("cookingLevel",cookingLevel);  
			recipeManagement.put("cookingInterest",cookingInterest);
			recipeManagement.put("hobbies",hobbies);
			recipeManagement.put("aboutChef",aboutChef);
//			recipeManagement.put("shareYourProfile",shareYourProfile);
			recipeManagement.put("allowComments",allowComments);
			recipeManagement.put("termManagement",termManagement);
			recipeManagement.put("shareProfile",shareProfile);
			recipeManagement.put("termToRespond",termToRespond);
			
			recipeManagement.put("statusId","RECIPE_RES_REQUESTED");
//			recipeManagement.put("recipeManagementId",recipeManagementId);
//			recipeManagement.put("statusId","");
		}else{
 			recipeManagement = recipeWeek;
			recipeManagement.put("homeTown",homeTown);   
//			recipeManagement.put("memberSince",memberSince);
			if(UtilValidate.isNotEmpty(fileName))
			recipeManagement.put("photo", "/images/recipes/customerDetails/"+recipeManagementId+extension);
			recipeManagement.put("photo", recipeWeek.getString("photo"));
			recipeManagement.put("cookingLevel",cookingLevel);  
			
			recipeManagement.put("recipeManagementId",recipeManagementId);
			recipeManagement.put("userLoginId", createdBy);
			
			recipeManagement.put("cookingInterest",cookingInterest);
			recipeManagement.put("hobbies",hobbies);
			recipeManagement.put("aboutChef",aboutChef);
//			recipeManagement.put("shareYourProfile",shareYourProfile);
			recipeManagement.put("allowComments",allowComments);
			recipeManagement.put("termManagement",termManagement);
			recipeManagement.put("shareProfile",shareProfile);
			
			recipeManagement.put("termToRespond",termToRespond);
			
			recipeManagement.put("statusId","RECIPE_RES_REQUESTED");
//			
		}
			
			           
			boolean beganTransaction = TransactionUtil.begin();
			 
			if(beganTransaction)
			{ if(UtilValidate.isEmpty(recipeWeek))
				delegator.create(recipeManagement);
				delegator.store(recipeManagement);
				TransactionUtil.commit();
				 
			}
			String msg = null;
				msg = " Successfully Added";
				
				
			sendRecipeResponseEmail(delegator, (LocalDispatcher) request.getAttribute("dispatcher"), recipeManagementId,
														productStore.getString("productStoreId"), createdBy, partyId);
			request.setAttribute("_EVENT_MESSAGE_", msg);
			
			/*((LocalDispatcher)request.getAttribute("dispatcher")).runAsync("recipeStatusMail",
					UtilMisc.toMap("recipeManagementId",UtilMisc.toList(recipeManagementId),"userLogin",userLogin));
			*/
			return "success";
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_"," Failed To create Recipe");
			try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return "error";
		}
    }
    
    
    public static void sendRecipeResponseEmail(Delegator delegator, LocalDispatcher dispatcher, String recipeId, String productStoreId ,String email ,String partyId){

    	Map results = ServiceUtil.returnSuccess();
    	String emailType = "REC_RES_EMAIL";
        String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#recipeResponseEmail";
            String sendTo = null;
            // get the ProductStore email settings
            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
            }

            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = defaultScreenLocation;
            }
    		        
            Map<String, Object> serviceContext = new HashMap<String, Object>(); //FastMap.newInstance();
            serviceContext.put("bodyScreenUri", bodyScreenLocation);
            
            String name = PartyHelper.getPartyName(delegator, partyId, false);
            
            serviceContext.put("bodyParameters", UtilMisc.toMap("name",name,"recipeId",recipeId));
            serviceContext.put("subject", productStoreEmail.getString("subject"));
            serviceContext.put("sendFrom", email);
            serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
            serviceContext.put("contentType", productStoreEmail.get("contentType"));
            serviceContext.put("sendTo", productStoreEmail.get("toAddress"));

            try {
                Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
                if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                    Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                }
            } catch (GenericServiceException e) {
            	Debug.logError(e, "Problem in sending mail for orderEmailReport ", module);
            }

    }
    public static void sendRecipeOfWeekEmail(Delegator delegator, LocalDispatcher dispatcher, String recipeManagementId){
		 GenericValue recipeManagement = null;
		try {
			recipeManagement = delegator.findByPrimaryKey("RecipeManagement",UtilMisc.toMap("recipeManagementId",recipeManagementId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(UtilValidate.isNotEmpty(recipeManagement)){
			 String createdBy = recipeManagement.getString("createdBy");
			 
			 	GenericValue userLogin = null;
				try {
					userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",createdBy));
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				sendRecipeOfWeekEmail(delegator, dispatcher, recipeManagement.getString("productStoreId"),
																			createdBy, userLogin.getString("partyId"));
		}
    }
    public static void sendRecipeOfWeekEmail(Delegator delegator, LocalDispatcher dispatcher, String productStoreId ,String email ,String partyId){

    	Map results = ServiceUtil.returnSuccess();
    	String emailType = "REC_WEEK_EMAIL";
        String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#recipeOfTheWeekEmail";
            String sendTo = null;
            // get the ProductStore email settings
            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
            }

            String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
            if (UtilValidate.isEmpty(bodyScreenLocation)) {
                bodyScreenLocation = defaultScreenLocation;
            }
    		        
            Map<String, Object> serviceContext = new HashMap<String, Object>(); // FastMap.newInstance();
            serviceContext.put("bodyScreenUri", bodyScreenLocation);
            String name = PartyHelper.getPartyName(delegator, partyId, false);
            
            serviceContext.put("bodyParameters", UtilMisc.toMap("name",name));
            serviceContext.put("subject", productStoreEmail.getString("subject"));
            serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
            serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
            serviceContext.put("contentType", productStoreEmail.get("contentType"));
            serviceContext.put("sendTo", email);

            try {
                Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
                if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                    Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
                }
            } catch (GenericServiceException e) {
            	Debug.logError(e, "Problem in sending mail for orderEmailReport ", module);
            }

    }
    
    
public static Map<String, Object> recipeYouMartSavings(DispatchContext dctx, Map<String, ? extends Object> context){
		
		Map results = ServiceUtil.returnSuccess();
        GenericDelegator delegator = (GenericDelegator) dctx.getDelegator();
        List<String> recipeList = (List) context.get("recipeManagementId");
        if(UtilValidate.isNotEmpty(recipeList))
        for(String recipeId : recipeList)
        {
	        GenericValue recipe = null;
			try {
				recipe = delegator.findOne("RecipeManagement", UtilMisc.toMap("recipeManagementId",recipeId), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(UtilValidate.isEmpty(recipe)) return results ;
			
			String createdBy = recipe.getString("createdBy");
			
			if(UtilValidate.isEmpty(createdBy)) return results ;
			
        }
        return results;
	}

public static Map createLoyaltyBillingAccount(LocalDispatcher dispatcher, GenericValue loyal, String userId) {

    GenericDelegator delegator=GenericDelegator.getGenericDelegator("default");
     
    GenericValue userLogin = null;
	try {
		userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",userId));
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    BigDecimal totalLoyaltyBillAmt=BigDecimal.ZERO;
    List<GenericValue> partyBillValue = null;
    Map<String, Object> results = new HashMap<String, Object>();
    String loyalPoints = null;
    int loyalRupee = 0;
    int dividends = 1000;
    try {
    	if(loyal != null){
			loyalRupee= Integer.parseInt(loyal.getString("loyaltyRupee"));
			loyalPoints = loyal.getString("loyaltyPoint");
			 
    	}
    	
    	if(loyalRupee != 0 && UtilValidate.isNotEmpty(loyalRupee)){
    		totalLoyaltyBillAmt = totalLoyaltyBillAmt.add(new BigDecimal(loyal.getString("loyaltyRupee")));
    	}
        
        partyBillValue  = delegator.findList("BillingAccountRole", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")), null, null, null, false);
        	if (UtilValidate.isNotEmpty(partyBillValue)) {
                for (GenericValue partyBill : partyBillValue) {
                	EntityCondition barFindCond = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("billingAccountId", EntityOperator.EQUALS, partyBill.getString("billingAccountId")),
                            EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + "Credit Account for Loyalty #") +userLogin.getString("partyId")+"%"))), EntityOperator.AND);
                	List<GenericValue> billingAccountRoleList = delegator.findList("BillingAccount", barFindCond, null, null, null, false);
                	if (UtilValidate.isNotEmpty(billingAccountRoleList)) {
                		GenericValue billingAmt=delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", partyBill.getString("billingAccountId")));
                		if(billingAmt!=null){
                			BigDecimal billingAmtRupee = totalLoyaltyBillAmt.add(new BigDecimal(billingAmt.getString("accountLimit")));
                			billingAmt.set("accountLimit", billingAmtRupee);
                			billingAmt.store();
                    	}
                		results.put("billingAccountId", partyBill.getString("billingAccountId"));
                	}else
                	{
                		Map<String, Object> input = UtilMisc.<String, Object>toMap("accountLimit", totalLoyaltyBillAmt, "description", "Credit Account for Loyalty #" + userLogin.getString("partyId"), "userLogin", userLogin);
                        input.put("accountCurrencyUomId", "INR");
                        //input.put("thruDate", UtilDateTime.nowTimestamp());
                		results = dispatcher.runSync("createBillingAccount", input);
                		if (ServiceUtil.isError(results)) return results;
                        String billingAccountId = (String) results.get("billingAccountId");
                        
                     // set the role on the account
                        input = UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", userLogin.getString("partyId"), "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
                        Map<String, Object> roleResults = dispatcher.runSync("createBillingAccountRole", input);
                        if (ServiceUtil.isError(roleResults)) {
                            Debug.logError("Error with createBillingAccountRole: " + roleResults.get(ModelService.ERROR_MESSAGE), module);
                        }
                	}
                	
                }
        }else
    	{
    		Map<String, Object> input = UtilMisc.<String, Object>toMap("accountLimit", totalLoyaltyBillAmt, "description", "Credit Account for Loyalty #" + userLogin.getString("partyId"), "userLogin", userLogin);
            input.put("accountCurrencyUomId", "INR");
            //input.put("thruDate", UtilDateTime.nowTimestamp());
    		results = dispatcher.runSync("createBillingAccount", input);
    		if (ServiceUtil.isError(results)) return results;
            String billingAccountId = (String) results.get("billingAccountId");
            
         // set the role on the account
            input = UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", userLogin.getString("partyId"), "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin);
            Map<String, Object> roleResults = dispatcher.runSync("createBillingAccountRole", input);
            if (ServiceUtil.isError(roleResults)) {
                Debug.logError("Error with createBillingAccountRole: " + roleResults.get(ModelService.ERROR_MESSAGE), module);
            }
    	}
        
        int remainingPoints = Integer.parseInt(loyalPoints) % dividends;
        loyal.set("loyaltyPoint",String.valueOf(remainingPoints));
        loyal.set("loyaltyRupee", "0");
        loyal.store();
        return results;
	} catch (GenericServiceException e) {
		// TODO Auto-generated catch block
		Debug.logError(e, "Entity error when creating BillingAccount: " + e.getMessage(), module);
		e.printStackTrace();
	}catch (GenericEntityException e) {
			// TODO Auto-generated catch block
		Debug.logError(e.getMessage(),module);
		//e.printStackTrace();
	} 
	 return ServiceUtil.returnSuccess();
    }
    
	public static String changeRecipeWeekResStatus(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		String recipeManagementId = request.getParameter("recipeId");
		String statusId = request.getParameter("statusIdTo");
		
		GenericValue recipeWeek = null;
		try {
			TransactionUtil.begin();
			recipeWeek = delegator.findByPrimaryKey("RecipeWeek", UtilMisc.toMap("recipeManagementId", recipeManagementId));
			recipeWeek.put("statusId", statusId);
			recipeWeek.store();
			
			String recipeStatusId = "RECIPE_OF_WEEK_RES";
			String recipeStatusIdTo = "RECIPE_OF_WEEK";
			if("RECIPE_RES_REJECTED".equals(statusId))
				   recipeStatusIdTo = "REC_OF_WEEK_RES_REJ";
			GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
			if(UtilValidate.isEmpty(userLogin))
			{
				request.setAttribute("_ERROR_MESSAGE_"," Login Please ");
				return "error";
			}
			
			Map input = UtilMisc.toMap("recipeManagementId", UtilMisc.toList(recipeManagementId), 
											"statusId", recipeStatusId, "statusIdTo", recipeStatusIdTo, "userLogin", userLogin);
            Map<String, Object> roleResults = dispatcher.runSync("changeRecipeStatus", input);
            if (ServiceUtil.isSuccess(roleResults)) {
            	TransactionUtil.commit();
            	
            	GenericValue recipe = null;
    			try {
    				recipe = delegator.findOne("RecipeManagement", UtilMisc.toMap("recipeManagementId",recipeManagementId), true);
    			} catch (GenericEntityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            	
            	sendRecipeOfWeekEmail(delegator, dispatcher, recipe.getString("productStoreId"), userLogin.getString("userLoginId"), userLogin.getString("partyId"));
            	
            }
            else
            {
            	TransactionUtil.rollback();
            	Debug.logError("Error with changeRecipeStatus: " + roleResults.get(ModelService.ERROR_MESSAGE), module);
            }	
            
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
    
}
