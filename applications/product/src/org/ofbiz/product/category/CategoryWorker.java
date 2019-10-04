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
package org.ofbiz.product.category;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * CategoryWorker - Worker class to reduce code in JSPs.
 */
public class CategoryWorker {

    public static final String module = CategoryWorker.class.getName();

    public static String getCatalogTopCategory(ServletRequest request, String defaultTopCategory) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Map<String, Object> requestParameters = UtilHttp.getParameterMap(httpRequest);
        String topCatName = null;
        boolean fromSession = false;

        // first see if a new category was specified as a parameter
        topCatName = (String) requestParameters.get("CATALOG_TOP_CATEGORY");
        // if no parameter, try from session
        if (topCatName == null) {
            topCatName = (String) httpRequest.getSession().getAttribute("CATALOG_TOP_CATEGORY");
            if (topCatName != null)
                fromSession = true;
        }
        // if nothing else, just use a default top category name
        if (topCatName == null)
            topCatName = defaultTopCategory;
        if (topCatName == null)
            topCatName = "CATALOG1";

        if (!fromSession) {
            if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.getCatalogTopCategory] Setting new top category: " + topCatName, module);
            httpRequest.getSession().setAttribute("CATALOG_TOP_CATEGORY", topCatName);
        }
        return topCatName;
    }

    public static void getCategoriesWithNoParent(ServletRequest request, String attributeName) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
      //  Collection<GenericValue> results = FastList.newInstance();
        Collection<GenericValue> results = new ArrayList<GenericValue>();

        try {
            Collection<GenericValue> allCategories = delegator.findList("ProductCategory", null, null, null, null, false);

            for (GenericValue curCat: allCategories) {
                Collection<GenericValue> parentCats = curCat.getRelatedCache("CurrentProductCategoryRollup");

                if (parentCats.isEmpty())
                    results.add(curCat);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        request.setAttribute(attributeName, results);
    }
    public static List<GenericValue> getRelatedCategoriesRet(GenericDelegator delegator, String attributeName, String parentId, boolean limitView, boolean excludeEmpty, boolean recursive) {
        //List<GenericValue> categories = FastList.newInstance();
        List<GenericValue> categories = new ArrayList<GenericValue>();

        if (Debug.verboseOn()) Debug.logVerbose("[CategoryWorker.getRelatedCategories] ParentID: " + parentId, module);

        List<GenericValue> rollups = null;

        try {
            rollups = delegator.findByAndCache("ProductCategoryRollup",
                        UtilMisc.toMap("parentProductCategoryId", parentId),
                        UtilMisc.toList("sequenceNum"));
            if (limitView) {
                rollups = EntityUtil.filterByDate(rollups, true);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            rollups = null;
        }
        if (UtilValidate.isNotEmpty(rollups)) {
            // Debug.log("Rollup size: " + rollups.size(), module);
            for (GenericValue parent: rollups) {
                // Debug.log("Adding child of: " + parent.getString("parentProductCategoryId"), module);
                GenericValue cv = null;

                try {
                    cv = parent.getRelatedOneCache("CurrentProductCategory");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                    cv = null;
                }
                if (cv != null) {
                    if (excludeEmpty) {
                        if (!isCategoryEmpty(cv)) {
                            //Debug.log("Child : " + cv.getString("productCategoryId") + " is not empty.", module);
                            categories.add(cv);
                            if (recursive) {
                                categories.addAll(getRelatedCategoriesRet(delegator, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive));
                            }
                        }
                    } else {
                        categories.add(cv);
                        if (recursive) {
                            categories.addAll(getRelatedCategoriesRet(delegator, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive));
                        }
                    }
                }
            }
        }
        return categories;
    }
    public static void getRelatedCategories(ServletRequest request, String attributeName, boolean limitView) {
        Map<String, Object> requestParameters = UtilHttp.getParameterMap((HttpServletRequest) request);
        String requestId = null;

        requestId = UtilFormatOut.checkNull((String)requestParameters.get("catalog_id"), (String)requestParameters.get("CATALOG_ID"),
                (String)requestParameters.get("category_id"), (String)requestParameters.get("CATEGORY_ID"));

        if (requestId.equals(""))
            return;
        if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.getRelatedCategories] RequestID: " + requestId, module);
        getRelatedCategories(request, attributeName, requestId, limitView);
    }

    public static void getRelatedCategories(ServletRequest request, String attributeName, String parentId, boolean limitView) {
        getRelatedCategories(request, attributeName, parentId, limitView, false);
    }

    public static void getRelatedCategories(ServletRequest request, String attributeName, String parentId, boolean limitView, boolean excludeEmpty) {
        List<GenericValue> categories = getRelatedCategoriesRet(request, attributeName, parentId, limitView, excludeEmpty);

        if (!categories.isEmpty())
            request.setAttribute(attributeName, categories);
    }

    public static List<GenericValue> getRelatedCategoriesRet(ServletRequest request, String attributeName, String parentId, boolean limitView) {
        return getRelatedCategoriesRet(request, attributeName, parentId, limitView, false);
    }

    public static List<GenericValue> getRelatedCategoriesRet(ServletRequest request, String attributeName, String parentId, boolean limitView, boolean excludeEmpty) {
        return getRelatedCategoriesRet(request, attributeName, parentId, limitView, excludeEmpty, false);
    }

    public static List<GenericValue> getRelatedCategoriesRet(ServletRequest request, String attributeName, String parentId, boolean limitView, boolean excludeEmpty, boolean recursive) {
        //List<GenericValue> categories = FastList.newInstance();
        List<GenericValue> categories = new ArrayList<GenericValue>();

        if (Debug.verboseOn()) Debug.logVerbose("[CategoryWorker.getRelatedCategories] ParentID: " + parentId, module);

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List<GenericValue> rollups = null;

        try {
            rollups = delegator.findByAndCache("ProductCategoryRollup",
                        UtilMisc.toMap("parentProductCategoryId", parentId),
                        UtilMisc.toList("sequenceNum"));
            if (limitView) {
                rollups = EntityUtil.filterByDate(rollups, true);
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        if (rollups != null) {
            // Debug.log("Rollup size: " + rollups.size(), module);
            for (GenericValue parent: rollups) {
                // Debug.log("Adding child of: " + parent.getString("parentProductCategoryId"), module);
                GenericValue cv = null;

                try {
                    cv = parent.getRelatedOneCache("CurrentProductCategory");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e.getMessage(), module);
                }
                if (cv != null) {
                    if (excludeEmpty) {
                        if (!isCategoryEmpty(cv)) {
                            //Debug.log("Child : " + cv.getString("productCategoryId") + " is not empty.", module);
                            categories.add(cv);
                            if (recursive) {
                                categories.addAll(getRelatedCategoriesRet(request, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive));
                            }
                        }
                    } else {
                        categories.add(cv);
                        if (recursive) {
                            categories.addAll(getRelatedCategoriesRet(request, attributeName, cv.getString("productCategoryId"), limitView, excludeEmpty, recursive));
                        }
                    }
                }
            }
        }
        return categories;
    }

    public static boolean isCategoryEmpty(GenericValue category) {
        boolean empty = true;
        long members = categoryMemberCount(category);
        //Debug.log("Category : " + category.get("productCategoryId") + " has " + members  + " members", module);
        if (members > 0) {
            empty = false;
        }

        if (empty) {
            long rollups = categoryRollupCount(category);
            //Debug.log("Category : " + category.get("productCategoryId") + " has " + rollups  + " rollups", module);
            if (rollups > 0) {
                empty = false;
            }
        }

        return empty;
    }

    public static long categoryMemberCount(GenericValue category) {
        if (category == null) return 0;
        Delegator delegator = category.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByCondition("ProductCategoryMember", buildCountCondition("productCategoryId", category.getString("productCategoryId")), null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return count;
    }

    public static long categoryRollupCount(GenericValue category) {
        if (category == null) return 0;
        Delegator delegator = category.getDelegator();
        long count = 0;
        try {
            count = delegator.findCountByCondition("ProductCategoryRollup", buildCountCondition("parentProductCategoryId", category.getString("productCategoryId")), null, null);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return count;
    }

    private static EntityCondition buildCountCondition(String fieldName, String fieldValue) {
       // List<EntityCondition> orCondList = FastList.newInstance();
        List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
        orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
        orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
        EntityCondition orCond = EntityCondition.makeCondition(orCondList, EntityOperator.OR);

      //  List<EntityCondition> andCondList = FastList.newInstance();
        List<EntityCondition> andCondList = new ArrayList<EntityCondition>();
        andCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()));
        andCondList.add(EntityCondition.makeCondition(fieldName, EntityOperator.EQUALS, fieldValue));
        andCondList.add(orCond);
        EntityCondition andCond = EntityCondition.makeCondition(andCondList, EntityOperator.AND);

        return andCond;
    }

    public static void setTrail(ServletRequest request, String currentCategory) {
        Map<String, Object> requestParameters = UtilHttp.getParameterMap((HttpServletRequest) request);
        String previousCategory = (String) requestParameters.get("pcategory");
        setTrail(request, currentCategory, previousCategory);
    }

    public static void setTrail(ServletRequest request, String currentCategory, String previousCategory) {
        if (Debug.verboseOn()) Debug.logVerbose("[CategoryWorker.setTrail] Start: previousCategory=" + previousCategory + " currentCategory=" + currentCategory, module);

        // if there is no current category, just return and do nothing to that the last settings will stay
        if (UtilValidate.isEmpty(currentCategory)) {
            return;
        }

        // always get the last crumb list
        List<String> crumb = getTrail(request);
        crumb = adjustTrail(crumb, currentCategory, previousCategory);
        setTrail(request, crumb);
    }

    public static List<String> adjustTrail(List<String> origTrail, String currentCategoryId, String previousCategoryId) {
       // List<String> trail = FastList.newInstance();
        List<String> trail = new ArrayList<String>();
        if (origTrail != null) {
            trail.addAll(origTrail);
        }

        // if no previous category was specified, check to see if currentCategory is in the list
        if (UtilValidate.isEmpty(previousCategoryId)) {
            if (trail.contains(currentCategoryId)) {
                // if cur category is in crumb, remove everything after it and return
                int cindex = trail.lastIndexOf(currentCategoryId);

                if (cindex < (trail.size() - 1)) {
                    for (int i = trail.size() - 1; i > cindex; i--) {
                        trail.remove(i);
                        //FIXME can be removed ?
                        // String deadCat = trail.remove(i);
                        //if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.setTrail] Removed after current category index: " + i + " catname: " + deadCat, module);
                    }
                }
                return trail;
            } else {
                // current category is not in the list, and no previous category was specified, go back to the beginning
                trail.clear();
                trail.add("TOP");
                if (UtilValidate.isNotEmpty(previousCategoryId)) {
                    trail.add(previousCategoryId);
                }
                //if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.setTrail] Starting new list, added TOP and previousCategory: " + previousCategoryId, module);
            }
        }

        if (!trail.contains(previousCategoryId)) {
            // previous category was NOT in the list, ERROR, start over
            //if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.setTrail] previousCategory (" + previousCategoryId + ") was not in the crumb list, position is lost, starting over with TOP", module);
            trail.clear();
            trail.add("TOP");
            if (UtilValidate.isNotEmpty(previousCategoryId)) {
                trail.add(previousCategoryId);
            }
        } else {
            // remove all categories after the previous category, preparing for adding the current category
            int index = trail.indexOf(previousCategoryId);
            if (index < (trail.size() - 1)) {
                for (int i = trail.size() - 1; i > index; i--) {
                    trail.remove(i);
                    //FIXME can be removed ?
                    // String deadCat = trail.remove(i);
                    //if (Debug.infoOn()) Debug.logInfo("[CategoryWorker.setTrail] Removed after current category index: " + i + " catname: " + deadCat, module);
                }
            }
        }

        // add the current category to the end of the list
        trail.add(currentCategoryId);
        if (Debug.verboseOn()) Debug.logVerbose("[CategoryWorker.setTrail] Continuing list: Added currentCategory: " + currentCategoryId, module);

        return trail;
    }

    public static List<String> getTrail(ServletRequest request) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        List<String> crumb = UtilGenerics.checkList(session.getAttribute("_BREAD_CRUMB_TRAIL_"));
        return crumb;
    }
    

    public static List<String> setTrail(ServletRequest request, List<String> crumb) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        session.setAttribute("_BREAD_CRUMB_TRAIL_", crumb);
        return crumb;
    }

    public static boolean checkTrailItem(ServletRequest request, String category) {
        List<String> crumb = getTrail(request);

        if (crumb != null && crumb.contains(category)) {
            return true;
        } else {
            return false;
        }
    }

    public static String lastTrailItem(ServletRequest request) {
        List<String> crumb = getTrail(request);

        if (UtilValidate.isNotEmpty(crumb)) {
            return crumb.get(crumb.size() - 1);
        } else {
            return null;
        }
    }

    public static boolean isProductInCategory(Delegator delegator, String productId, String productCategoryId) throws GenericEntityException {
        if (productCategoryId == null) return false;
        if (UtilValidate.isEmpty(productId)) return false;

        List<GenericValue> productCategoryMembers = EntityUtil.filterByDate(delegator.findByAndCache("ProductCategoryMember",
                UtilMisc.toMap("productCategoryId", productCategoryId, "productId", productId)), true);
        if (UtilValidate.isEmpty(productCategoryMembers)) {
            //before giving up see if this is a variant product, and if so look up the virtual product and check it...
            GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            List<GenericValue> productAssocs = ProductWorker.getVariantVirtualAssocs(product);
            //this does take into account that a product could be a variant of multiple products, but this shouldn't ever really happen...
            if (productAssocs != null) {
                for (GenericValue productAssoc: productAssocs) {
                    if (isProductInCategory(delegator, productAssoc.getString("productId"), productCategoryId)) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static List<GenericValue> filterProductsInCategory(Delegator delegator, List<GenericValue> valueObjects, String productCategoryId) throws GenericEntityException {
        return filterProductsInCategory(delegator, valueObjects, productCategoryId, "productId");
    }

    public static List<GenericValue> filterProductsInCategory(Delegator delegator, List<GenericValue> valueObjects, String productCategoryId, String productIdFieldName) throws GenericEntityException {
      //  List<GenericValue> newList = FastList.newInstance();
        List<GenericValue> newList = new ArrayList<GenericValue>();

        if (productCategoryId == null) return newList;
        if (valueObjects == null) return null;

        for (GenericValue curValue: valueObjects) {
            String productId = curValue.getString(productIdFieldName);
            if (isProductInCategory(delegator, productId, productCategoryId)) {
                newList.add(curValue);
            }
        }
        return newList;
    }
   /* public static List  productList1(HttpServletRequest request, HttpServletResponse response,String categoryId , List <GenericValue> categoryList)
	  {
    	  Delegator delegator = (Delegator) request.getAttribute("delegator");
    	  if(categoryId.equals("FOODGROC"))
      	{
    		  try
     		   {
     		 List categoryAList=new ArrayList();
     		categoryAList.add("RIC_PROD");
     		categoryAList.add("DAL_PULSE");
     		categoryAList.add("SALT_SUGR");
     		categoryAList.add("EDI_OILGEE");
     		categoryAList.add("TEA_COFFE");
     		categoryAList.add("FLO_SUJI");
     		categoryAList.add("SPICE_MALA");
     		categoryAList.add("BREAD_BAK");
     		categoryAList.add("EGG_DAIRY");
     		categoryAList.add("BAK_CEREAL");
     		categoryAList.add("SPREADS_SAUCES");
     		categoryAList.add("PICKLES");
     		
     		
     		
     		  
     		  List <GenericValue> prodList = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.IN,categoryAList),null,null,null,true);
     		  if(UtilValidate.isNotEmpty(prodList)){
     			   return prodList;
     		  }
     		   }catch(Exception e){
     			   
     		   }
      	
    	  
    	  
    	  
      	}
    	  else
    	  {
    	  
		   try
		   {
		
		  List <GenericValue> prodList = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId),null,null,null,true);
		  if(UtilValidate.isNotEmpty(prodList)){
			   return prodList;
		  }
		  else {
			  List <GenericValue> subCats = delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, categoryId),null,null,null,true);
			  if(subCats!=null)
			  {
				  for( GenericValue subCat : subCats)
				  {
					  categoryList.addAll( productList( request,  response, subCat.getString("productCategoryId") , categoryList));
				  }
			  }
		  }
		   }
		   catch(Exception e)
		   {
			   
		   }
		   //System.out.println("categoryList\n\n\n\n\n"+categoryList);
		   
    	  }
    	  return categoryList;
	  }*/
    
    public static List  productList(HttpServletRequest request, HttpServletResponse response,String categoryId , List categoryList)
	  {
  	  Delegator delegator = (Delegator) request.getAttribute("delegator");
  	List productList=new ArrayList();
  	  List sumList=new ArrayList();
		   try
		   {
			  List <GenericValue> subCats = delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, categoryId),UtilMisc.toSet("productCategoryId","parentProductCategoryId"),null,null,true);
		 
			   if(UtilValidate.isEmpty(subCats))
				   sumList.add(categoryId);
			   
		  
		  else {
			  
				for( GenericValue subCat : subCats)
				  {
				List<GenericValue> sub1=delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, subCat.getString("productCategoryId")),UtilMisc.toSet("productCategoryId","parentProductCategoryId"),null,null,true);
				if(UtilValidate.isEmpty(sub1))
				{
				sumList.add(subCat.getString("productCategoryId"));
				sumList.add(subCat.getString("parentProductCategoryId"));
				}
				else
				{
				 for(GenericValue sub2 :sub1)
					 {
					List<GenericValue> sub3=delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, sub2.getString("productCategoryId")),UtilMisc.toSet("productCategoryId","parentProductCategoryId"),null,null,true);
					if(UtilValidate.isEmpty(sub3))
					{
					sumList.add(sub2.getString("productCategoryId"));
					sumList.add(sub2.getString("parentProductCategoryId"));
					}
				else
					{
					for(GenericValue sub4 :sub3)
							{
					List<GenericValue> sub5=delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS, sub4.getString("productCategoryId")),UtilMisc.toSet("productCategoryId","parentProductCategoryId"),null,null,true);
					if(UtilValidate.isEmpty(sub5))
					{
					sumList.add(sub4.getString("productCategoryId"));
					sumList.add(sub4.getString("parentProductCategoryId"));
					}
							  }
						 }
						 }
					}
			  }
		  
		  
		   }
			   Timestamp nowTimestamp=UtilDateTime.nowTimestamp();
		  
		  for(int i=0;i<sumList.size();i++)
		  {
			  //List condnList=FastList.newInstance();
			  List condnList=new ArrayList();
			  condnList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, sumList.get(i)));
			  condnList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
              condnList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
			  List <GenericValue> productList1 = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition(condnList, EntityOperator.AND),UtilMisc.toSet("productId","thruDate"),null,null,true);
			if(UtilValidate.isNotEmpty(productList1))
			{
			  for(GenericValue product:productList1)
			  {
				 
				  productList.add(product.getString("productId"));
				  
			  }
			  
		  }
		  }
		  
		  
		   }
		  
		   catch(Exception e)
		   {
			   
		   }
  	  
  	  return productList;
	  }
  
  
   public static List  productList(HttpServletRequest request, HttpServletResponse response,String categoryId , List <GenericValue> categoryList,int count)
	  {
	   long count1=(long)count;
		   try
		   {
		  Delegator delegator = (Delegator) request.getAttribute("delegator");
		  List <GenericValue> prodList = delegator.findList("ProductCategoryMember",EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId),null,null,null,true);
		
		  if(UtilValidate.isNotEmpty(prodList)){
			   return prodList;
		  }
		  else {
			 // List<EntityCondition> orCondList = FastList.newInstance();
			  List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
		        orCondList.add(EntityCondition.makeCondition("parentProductCategoryId", EntityOperator.EQUALS,categoryId));
		        orCondList.add(EntityCondition.makeCondition("sequenceNum", EntityOperator.EQUALS, count1));
		        EntityCondition orCond = EntityCondition.makeCondition(orCondList, EntityOperator.AND);
			  
			  List <GenericValue> subCats = delegator.findList("ProductCategoryRollup",EntityCondition.makeCondition(orCondList,EntityOperator.AND),null,null,null,true);
			  if(subCats!=null)
			  {
				  for( GenericValue subCat : subCats)
				  {
					  categoryList.addAll( productList( request,  response, subCat.getString("productCategoryId") , categoryList,count));
				  }
			  }
		  }
		   }
		   catch(Exception e)
		   {
			   
		   }
		  
		   return categoryList;
	  }
    public static void getCategoryContentWrappers(Map<String, CategoryContentWrapper> catContentWrappers, List<GenericValue> categoryList, HttpServletRequest request) throws GenericEntityException {
        if (catContentWrappers == null || categoryList == null) {
            return;
        }
        for (GenericValue cat: categoryList) {
            String productCategoryId = (String) cat.get("productCategoryId");

            if (catContentWrappers.containsKey(productCategoryId)) {
                // if this ID is already in the Map, skip it (avoids inefficiency, infinite recursion, etc.)
                continue;
            }

            CategoryContentWrapper catContentWrapper = new CategoryContentWrapper(cat, request);
            catContentWrappers.put(productCategoryId, catContentWrapper);
            List<GenericValue> subCat = getRelatedCategoriesRet(request, "subCatList", productCategoryId, true);
            if (subCat != null) {
                getCategoryContentWrappers(catContentWrappers, subCat, request);
            }
        }
    }
   
    public static String getTopParentCategoryId(Delegator delegator,String productId){
    	List<GenericValue> productCategoryMemberList = null;
    	try {
    		productCategoryMemberList = delegator.findList("ProductCategoryMember",
    				EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId), 
					UtilMisc.toSet("productCategoryId"), null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(UtilValidate.isEmpty(productCategoryMemberList))return null;
    	
    	String productCategoryId = null;
    	for(GenericValue productCategoryMember : productCategoryMemberList)
       	{
       		if(!"BestDeals".equals(productCategoryMember.getString("productCategoryId")))
       			productCategoryId = productCategoryMember.getString("productCategoryId");
       	}
    	//for(GenericValue productCategoryMember : productCategoryMemberList)
    	//{
    	//GenericValue productCategoryMember = productCategoryMemberList.get(0);
		//String productCategoryId = productCategoryMember.getString("productCategoryId");
		String productCategoryId1 = null;
		List<GenericValue> productCategoryRollUpList = null;
		
		boolean flag = true;
		while(flag)
		{
	    	try {
	    		productCategoryRollUpList = delegator.findList("ProductCategoryRollup",
	    				EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,productCategoryId), 
						UtilMisc.toSet("parentProductCategoryId","productCategoryId"), null, null, true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isEmpty(productCategoryRollUpList))
	    		{
	    			flag = false;
	    			if(UtilValidate.isEmpty(productCategoryId1))
	    				return productCategoryId;
	    			else
	    				return productCategoryId1;
	    		}
	    	
	    	GenericValue productCategoryRollUp = productCategoryRollUpList.get(0);
	    	
	    	productCategoryId = productCategoryRollUp.getString("parentProductCategoryId");
	    	if(UtilValidate.isEmpty(productCategoryId))
		    	{
	    			flag = false;
	    			return productCategoryId1;
	    		}
	    	productCategoryId1 = productCategoryRollUp.getString("productCategoryId");
	    	
		}
    	//}
		return productCategoryId1;
    }
    public static String getBrowseRootCategoryId(Delegator delegator,String productId){
    	List<GenericValue> productCategoryMemberList = null;
    	try {
    		productCategoryMemberList = delegator.findList("ProductCategoryMember",
    				EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId), 
					UtilMisc.toSet("productCategoryId"), null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(UtilValidate.isEmpty(productCategoryMemberList))return null;
    	
    	String productCategoryId = null;
    	for(GenericValue productCategoryMember : productCategoryMemberList)
       	{
       		if(!"BestDeals".equals(productCategoryMember.getString("productCategoryId")))
       			productCategoryId = productCategoryMember.getString("productCategoryId");
       	}
    	//GenericValue productCategoryMember = productCategoryMemberList.get(0);
    	//for(GenericValue productCategoryMember : productCategoryMemberList)
    	//{
    	
		//String productCategoryId = productCategoryMember.getString("productCategoryId");
		List<GenericValue> productCategoryRollUpList = null;
		boolean flag = true;
		while(flag)
		{
	    	try {
	    		productCategoryRollUpList = delegator.findList("ProductCategoryRollup",
	    				EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,productCategoryId), 
						UtilMisc.toSet("parentProductCategoryId","productCategoryId"), null, null, true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isEmpty(productCategoryRollUpList))
	    		{
	    			flag = false;
	    			return productCategoryId;
	    		}
	    	GenericValue productCategoryRollUp = productCategoryRollUpList.get(0);
	    	
	    	productCategoryId = productCategoryRollUp.getString("parentProductCategoryId");
	    	if(UtilValidate.isEmpty(productCategoryId))
		    	{
	    			flag = false;
	    			return productCategoryRollUp.getString("productCategoryId");
	    		}
	    	
	    	//productCategoryId = productCategoryRollUp.getString("productCategoryId");
	    	
		}
    	//}
		return productCategoryId;
    }
    public static String getProdCatalogId(Delegator delegator,String productId){
    	String browseRootCategoryId = getBrowseRootCategoryId(delegator, productId);
    	List condition = new ArrayList();
    	condition.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, browseRootCategoryId));
    	condition.add(EntityCondition.makeCondition("prodCatalogCategoryTypeId", EntityOperator.EQUALS, "PCCT_BROWSE_ROOT"));
		
    	List<GenericValue> prodCatalogCategoryList = null;
    	try {
    		prodCatalogCategoryList = delegator.findList("ProdCatalogCategory",
					EntityCondition.makeCondition(condition, EntityOperator.AND), 
					UtilMisc.toSet("prodCatalogId"), null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	if(UtilValidate.isEmpty(prodCatalogCategoryList))return "VEGFRUIT";
    	return prodCatalogCategoryList.get(0).getString("prodCatalogId");
    }
    
    public static String getProdCatalogOrCategoryId(Delegator delegator,String productId){
    	String prodCatalogId = getProdCatalogId(delegator, productId);
    	if(UtilValidate.isEmpty(prodCatalogId))
    		return getTopParentCategoryId(delegator, productId);
    	if("VEGFRUIT".equals(prodCatalogId))
    		return getTopParentCategoryId(delegator, productId);
    	else
    		return prodCatalogId;
    }
    
    public static String getProductPrimaryCategoryName(Delegator delegator, String productId,boolean useCache) throws GenericEntityException {
        
    	String primaryCategoryId = getProductPrimaryCategoryId(delegator, productId, useCache);
    	
    	return getCategoryName(delegator, primaryCategoryId, true);
    }
    public static String getProductPrimaryCategoryId(Delegator delegator, String productId,boolean useCache) throws GenericEntityException {
        
    	GenericValue product = null;
    	try {
    		product = delegator.findOne("Product", useCache, UtilMisc.toMap("productId",productId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(UtilValidate.isEmpty(product)) return null;
    	
    	return product.getString("primaryProductCategoryId");
    }
    
    public static String getCategoryName(Delegator delegator, String productCategoryId,boolean useCache) throws GenericEntityException {
        
    	GenericValue productCategory = null;
    	try {
    		productCategory = delegator.findOne("ProductCategory", useCache, UtilMisc.toMap("productCategoryId",productCategoryId));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(UtilValidate.isEmpty(productCategory)) return null;
    	
    	return productCategory.getString("categoryName");
    }
    
   public static List<GenericValue> getCategoryName123(HttpServletRequest request ,Delegator delegator, String productCategoryId) throws GenericEntityException {
        
	   String prodCategory = productCategoryId;
	   List<String> categoryIdList = new ArrayList<String>();
	   categoryIdList.add(productCategoryId);
    	List<GenericValue> productCategoryList = null;
    	boolean flag = true;
    	while(flag){
	    	try {
	    		productCategoryList = delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("productCategoryId",productCategoryId));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isEmpty(productCategoryList)){
	    		flag = false;
	    		continue;
	    	}
	    	productCategoryId = productCategoryList.get(0).getString("parentProductCategoryId");
	    	categoryIdList.add(productCategoryId);
    	}
    	Collections.reverse(categoryIdList);
    	categoryIdList.remove(0);
    	
    	List<GenericValue> catList = new ArrayList<GenericValue>();
    	GenericValue productCategory = null;
    	for(String categoryId : categoryIdList){
        	try {
        		productCategory = delegator.findOne("ProductCategory", true, UtilMisc.toMap("productCategoryId",categoryId));
        		catList.add(productCategory);
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	 //List<GenericValue> subCatList = getRelatedCategoriesRet(request, "subCatLists1", prodCategory, true);
    	 //catList.addAll(subCatList);
		
    	 return catList;
    	
    }
   
   public static String getProductName(String productName,HttpServletRequest request) {
       
   	
   	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
   	String[] splitString = productName.split(" ");
   	List<GenericValue> list = null;
   	int length = splitString.length;
   	try {
   		if(length > 0)
   		{length--;
   			productName = splitString[length];
   			list = delegator.findByAnd("ProductFeature", UtilMisc.toMap("description",productName));
   			length--;
   		}
   		if(length > 0 && UtilValidate.isEmpty(list))
   		{
   			productName = splitString[length]+" "+splitString[length+1];
   			list = delegator.findByAnd("ProductFeature", UtilMisc.toMap("description",productName));
   			length--;
   		}
   		if(length > 0 && UtilValidate.isEmpty(list))
   		{
   			productName = splitString[length]+" "+splitString[length+1]+" "+splitString[length+2];
   			list = delegator.findByAnd("ProductFeature", UtilMisc.toMap("description",productName));
   			length--;
   		}
   		if(length > 0 && UtilValidate.isEmpty(list))
   		{
   			productName = splitString[length]+" "+splitString[length+1]+" "+splitString[length+2]+" "+splitString[length+3];
   			list = delegator.findByAnd("ProductFeature", UtilMisc.toMap("description",productName));
   			length--;
   		}
   		if(length > 0 && UtilValidate.isEmpty(list))
   		{
   			productName = splitString[length]+" "+splitString[length+1]+" "+splitString[length+2]+" "+splitString[length+3]+" "+splitString[length+4];
   			list = delegator.findByAnd("ProductFeature", UtilMisc.toMap("description",productName));
   		}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	if(UtilValidate.isNotEmpty(list))
   		return productName;
   	
       	return null;
   }
   
   public static String getCategoryFromProduct(Delegator delegator,String productId){
	   
	   GenericValue product = ProductWorker.getParentProduct(productId, delegator);
	   if(UtilValidate.isNotEmpty(product))
	   {
		   productId = product.getString("productId");
		   if(UtilValidate.isNotEmpty(product.getString("primaryProductCategoryId")))
			   return product.getString("primaryProductCategoryId");
	   }
	   
	   try {
		product = delegator.findOne("Product", true, UtilMisc.toMap("productId",productId));
	} catch (GenericEntityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	if(UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(product.getString("primaryProductCategoryId")))
		   return product.getString("primaryProductCategoryId");
	   
   	List<GenericValue> productCategoryMemberList = null;
   	
   	//List<EntityCondition> condition = FastList.newInstance();
	List<EntityCondition> condition = new ArrayList<EntityCondition>();
   	
  // 	List<EntityCondition> orCondList = FastList.newInstance();
	List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
    orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
    orCondList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
    condition.add(EntityCondition.makeCondition(orCondList, EntityOperator.OR));

   // orCondList = FastList.newInstance();
    orCondList = new ArrayList();
    orCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()));
    orCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.EQUALS, null));
    condition.add(EntityCondition.makeCondition(orCondList, EntityOperator.OR));
    
    condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    
    EntityCondition cond = EntityCondition.makeCondition(condition, EntityOperator.AND);
   	
   	try {
   		productCategoryMemberList = delegator.findList("ProductCategoryMember",
   				cond, UtilMisc.toSet("productCategoryId"), null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
   	if(UtilValidate.isEmpty(productCategoryMemberList))return null;
   	
   	for(GenericValue productCategoryMember : productCategoryMemberList)
   	{
   		if(!"BestDeals".equals(productCategoryMember.getString("productCategoryId")))
   			return productCategoryMember.getString("productCategoryId");
   	}
   	return null;
   	//GenericValue productCategoryMember = productCategoryMemberList.get(0);
   	//return productCategoryMember.getString("productCategoryId");
   }
   
   public static List getAllChildCategories(Delegator delegator,String productCategoryId){
	   List childCategoryList = new ArrayList();
	   List<GenericValue> productCategoryList = null;
	   boolean flag = true;
	   while(flag)
	   {
		   try {
	   		   productCategoryList = delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId",productCategoryId));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   if(UtilValidate.isNotEmpty(productCategoryList))
			   for(GenericValue productCategory : productCategoryList)
			   {
				   productCategoryId = productCategory.getString("productCategoryId");
				   if(!childCategoryList.contains(productCategoryId))
					   childCategoryList.add(productCategoryId);
				   
				   try {
					   List<GenericValue> productCategoryList1 = delegator.findByAnd("ProductCategoryRollup", 
							   																		UtilMisc.toMap("parentProductCategoryId",productCategoryId));
					   if(UtilValidate.isNotEmpty(productCategoryList1))
						   for(GenericValue productCategory1 : productCategoryList1)
						   {
							   productCategoryId = productCategory1.getString("productCategoryId");
							   if(!childCategoryList.contains(productCategoryId))
								   childCategoryList.add(productCategoryId);
							   
							   try {
								   List<GenericValue> productCategoryList2 = delegator.findByAnd("ProductCategoryRollup", 
										   																		UtilMisc.toMap("parentProductCategoryId",productCategoryId));
								   if(UtilValidate.isNotEmpty(productCategoryList2))
									   for(GenericValue productCategory2 : productCategoryList2)
									   {
										   productCategoryId = productCategory2.getString("productCategoryId");
										   if(!childCategoryList.contains(productCategoryId))
											   childCategoryList.add(productCategoryId);
										   try {
											   List<GenericValue> productCategoryList4 = delegator.findByAnd("ProductCategoryRollup", 
													   																		UtilMisc.toMap("parentProductCategoryId",productCategoryId));
											   if(UtilValidate.isNotEmpty(productCategoryList4))
												   for(GenericValue productCategory4 : productCategoryList4)
												   {
													   productCategoryId = productCategory4.getString("productCategoryId");
													   if(!childCategoryList.contains(productCategoryId))
														   childCategoryList.add(productCategoryId);
												   }
										   } catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
									   }
							   } catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						   }
				   } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			   }
		   else
		   {
			   flag = false;
			   return childCategoryList;
		   }
	   }
	   return childCategoryList;
   }
   
   public static List<String> getParentCategoryListIds(Delegator delegator,String productCategoryId){

   	String productCategoryId1 = null;
		List<GenericValue> productCategoryRollUpList = null;
		List<String> categoryList = new ArrayList<String>();

		boolean flag = true;
		while(flag)
		{
	    	try {
	    		productCategoryRollUpList = delegator.findList("ProductCategoryRollup",
	    				EntityCondition.makeCondition("productCategoryId",EntityOperator.EQUALS,productCategoryId), 
						UtilMisc.toSet("parentProductCategoryId","productCategoryId"), null, null, true);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	if(UtilValidate.isEmpty(productCategoryRollUpList))
	    		{
	    			flag = false;
	    			if(UtilValidate.isEmpty(productCategoryId1))
	    				break;
	    			else
	    				break;
	    		}
	    	
	    	GenericValue productCategoryRollUp = productCategoryRollUpList.get(0);
	    	
	    	productCategoryId = productCategoryRollUp.getString("parentProductCategoryId");
	    	if(UtilValidate.isEmpty(productCategoryId))
		    	{
	    			flag = false;
	    			break;
	    		}
	    	productCategoryId1 = productCategoryRollUp.getString("productCategoryId");
	    	categoryList.add(productCategoryId1);
		}
   	//}
		return categoryList;
   }
   public static String getTrailAsString(Delegator delegator, String productCategoryId) {
       List<String> crumbs = getParentCategoryListIds(delegator, productCategoryId);
       String prevCrumb = "";
       if(UtilValidate.isNotEmpty(crumbs))
       {
    	   Collections.reverse(crumbs);
	       for(String crumb : crumbs)
	       {
	       		if(prevCrumb.equals(""))
	       			prevCrumb = crumb;
	       		else
	       			prevCrumb = prevCrumb+"/"+crumb;
	       }
       }
       return prevCrumb;
   }
   public static String getTrailAsString1(Delegator delegator,String productId) {
	   //request.getSession().removeAttribute("_BREAD_CRUMB_TRAIL_");
	   String productCatgId = CategoryWorker.getCategoryFromProduct(delegator, productId);
 	  if(UtilValidate.isEmpty(productCatgId))
 	  {
 		 GenericValue product1 = ProductWorker.getParentProduct(productId, delegator);
 		 if(UtilValidate.isNotEmpty(product1))
 			 productCatgId = CategoryWorker.getCategoryFromProduct(delegator, product1.getString("productId"));
 		 if(UtilValidate.isEmpty(productCatgId))
 			 productCatgId = "";
 	 }
 	 String prodCateUrl = CategoryWorker.getTrailAsString(delegator, productCatgId);
     return prodCateUrl;
   }
   
   
   public static List getProductsFromCategory(LocalDispatcher dispatcher, String productCategoryId){
	   Map<String, Object> productCategoryMembers = null;
		try {
			Map<String, Object> context = new HashMap<String, Object>();
			
			context.put("productCategoryId",productCategoryId);
			context.put("limitView",false);
			context.put("defaultViewSize",99999);
			context.put("viewIndexString","0");
			context.put("viewSizeString","999999");
			
			productCategoryMembers = dispatcher.runSync("getProductCategoryAndLimitedMembers", context);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(UtilValidate.isEmpty(productCategoryMembers))return null;
		List<GenericValue> prodCategoryMembers = (List<GenericValue>) productCategoryMembers.get("productCategoryMembers");
		if(UtilValidate.isEmpty(prodCategoryMembers)) return null;
		
		return EntityUtil.getFieldListFromEntityList(prodCategoryMembers, "productId", true);
   }
   
   
   public static List getAllVAriantAndNormalProducts(Delegator delegator, LocalDispatcher dispatcher, String productCategoryId){
	   List<String> productIds = getProductsFromCategory(dispatcher, productCategoryId);
	   List<String> productIDs = new ArrayList();
	   if(UtilValidate.isNotEmpty(productIds))
	   {
		   try{
			   List<GenericValue> productList = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, true);
			   if(UtilValidate.isNotEmpty(productList))
			   for(GenericValue product : productList){
				   String productId = product.getString("productId");
				   if(UtilValidate.isNotEmpty(product) && "Y".equals(product.getString("isVirtual")))
				   {
					   List selectedFeatureTypeValues = new ArrayList();
				       Map result = dispatcher.runSync("getAllExistingVariants", UtilMisc.toMap("productId",productId, "productFeatureAppls",selectedFeatureTypeValues));
				       List<String> variants = (List)result.get("variantProductIds");
				       if(UtilValidate.isNotEmpty(variants))
					    for(String variant : variants)
					    	productIDs.add(variant);
				   }else
					   productIDs.add(productId);
			   }
			   if(UtilValidate.isNotEmpty(productIDs))
				   return delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIDs), null, null, null, true);
		   }catch (Exception e) {
			// TODO: handle exception
		}
	   }
	   return null;
	   
   }
}
