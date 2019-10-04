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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastList;
//import javolution.util.FastMap;

import net.sf.json.JSONObject;
 

import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * CategoryServices - Category Services
 */
public class CategoryServices {

    public static final String module = CategoryServices.class.getName();
    public static final String resourceError = "ProductErrorUiLabels";

    public static Map<String, Object> getCategoryMembers(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String categoryId = (String) context.get("categoryId");
        Locale locale = (Locale) context.get("locale");
        GenericValue productCategory = null;
        List<GenericValue> members = null;

        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
            members = EntityUtil.filterByDate(productCategory.getRelatedCache("ProductCategoryMember", null, UtilMisc.toList("sequenceNum")), true);
            if (Debug.verboseOn()) Debug.logVerbose("Category: " + productCategory + " Member Size: " + members.size() + " Members: " + members, module);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem reading product categories: " + e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "categoryservices.problems_reading_category_entity", 
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("category", productCategory);
        result.put("categoryMembers", members);
        return result;
    }

    public static Map<String, Object> getPreviousNextProducts(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String categoryId = (String) context.get("categoryId");
        String productId = (String) context.get("productId");
        boolean activeOnly = (context.get("activeOnly") != null ? ((Boolean) context.get("activeOnly")).booleanValue() : true);
        Integer index = (Integer) context.get("index");
        Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
        Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
        Locale locale = (Locale) context.get("locale");
        
        if (index == null && productId == null) {
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resourceError, "categoryservices.problems_getting_next_products", locale));
        }

        List<String> orderByFields = UtilGenerics.checkList(context.get("orderByFields"));
       // if (orderByFields == null) orderByFields = FastList.newInstance();
        if (orderByFields == null) orderByFields = new ArrayList<String>();
        String entityName = getCategoryFindEntityName(delegator, orderByFields, introductionDateLimit, releaseDateLimit);

        GenericValue productCategory;
        List<GenericValue> productCategoryMembers;
        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
            productCategoryMembers = delegator.findByAndCache(entityName, UtilMisc.toMap("productCategoryId", categoryId), orderByFields);
        } catch (GenericEntityException e) {
            Debug.logInfo(e, "Error finding previous/next product info: " + e.toString(), module);
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resourceError, "categoryservices.error_find_next_products", UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (activeOnly) {
            productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
        }
      //  List<EntityCondition> filterConditions = FastList.newInstance();
        List<EntityCondition> filterConditions = new ArrayList<EntityCondition>();
        if (introductionDateLimit != null) {
            EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit));
            filterConditions.add(condition);
        }
        if (releaseDateLimit != null) {
            EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit));
            filterConditions.add(condition);
        }
        if (!filterConditions.isEmpty()) {
            productCategoryMembers = EntityUtil.filterByCondition(productCategoryMembers, EntityCondition.makeCondition(filterConditions, EntityOperator.AND));
        }

        if (productId != null && index == null) {
            for (GenericValue v: productCategoryMembers) {
                if (v.getString("productId").equals(productId)) {
                    index = Integer.valueOf(productCategoryMembers.indexOf(v));
                }
            }
        }

        if (index == null) {
            // this is not going to be an error condition because we don't want it to be so critical, ie rolling back the transaction and such
            return ServiceUtil.returnFailure(UtilProperties.getMessage(resourceError, "categoryservices.product_not_found", locale));
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("category", productCategory);

        String previous = null;
        String next = null;

        if (index.intValue() - 1 >= 0 && index.intValue() - 1 < productCategoryMembers.size()) {
            previous = productCategoryMembers.get(index.intValue() - 1).getString("productId");
            result.put("previousProductId", previous);
        } else {
            previous = productCategoryMembers.get(productCategoryMembers.size() - 1).getString("productId");
            result.put("previousProductId", previous);
        }

        if (index.intValue() + 1 < productCategoryMembers.size()) {
            next = productCategoryMembers.get(index.intValue() + 1).getString("productId");
            result.put("nextProductId", next);
        } else {
            next = productCategoryMembers.get(0).getString("productId");
            result.put("nextProductId", next);
        }
        return result;
    }

    private static String getCategoryFindEntityName(Delegator delegator, List<String> orderByFields, Timestamp introductionDateLimit, Timestamp releaseDateLimit) {
        // allow orderByFields to contain fields from the Product entity, if there are such fields
        String entityName = introductionDateLimit == null && releaseDateLimit == null ? "ProductAndCategoryMember" : "ProductAndCategoryMember";
        if (orderByFields == null) {
            return entityName;
        }
        if (orderByFields.size() == 0) {
            orderByFields.add("sequenceNum");
            orderByFields.add("productId");
        }

        ModelEntity productModel = delegator.getModelEntity("Product");
        ModelEntity productCategoryMemberModel = delegator.getModelEntity("ProductCategoryMember");
        for (String orderByField: orderByFields) {
            // Get the real field name from the order by field removing ascending/descending order
            if (UtilValidate.isNotEmpty(orderByField)) {
                int startPos = 0, endPos = orderByField.length();

                if (orderByField.endsWith(" DESC")) {
                    endPos -= 5;
                } else if (orderByField.endsWith(" ASC")) {
                    endPos -= 4;
                } else if (orderByField.startsWith("-")) {
                    startPos++;
                } else if (orderByField.startsWith("+")) {
                    startPos++;
                }

                if (startPos != 0 || endPos != orderByField.length()) {
                    orderByField = orderByField.substring(startPos, endPos);
                }
            }

            if (!productCategoryMemberModel.isField(orderByField)) {
                if (productModel.isField(orderByField)) {
                    entityName = "ProductAndCategoryMember";
                    // that's what we wanted to find out, so we can quit now
                    break;
                } else {
                    // ahh!! bad field name, don't worry, it will blow up in the query
                }
            }
        }
        return entityName;
    }

    public static Map<String, Object> getProductCategoryAndLimitedMembers(DispatchContext dctx, Map<String, ? extends Object> context) {
     	Delegator delegator = dctx.getDelegator();
        String productCategoryId = (String) context.get("productCategoryId");
        boolean limitView = ((Boolean) context.get("limitView")).booleanValue();
        int defaultViewSize = ((Integer) context.get("defaultViewSize")).intValue();
        Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
        Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
        //int prodCatalogSize = (Integer) context.get("prodCatalogSize");
         
        List<String> orderByFields = UtilGenerics.checkList(context.get("orderByFields"));
       // if (orderByFields == null) orderByFields = FastList.newInstance();
        if (orderByFields == null) orderByFields = new ArrayList<String>();
        String entityName = getCategoryFindEntityName(delegator, orderByFields, introductionDateLimit, releaseDateLimit);

        String prodCatalogId = (String) context.get("prodCatalogId");

        boolean useCacheForMembers = (context.get("useCacheForMembers") == null || ((Boolean) context.get("useCacheForMembers")).booleanValue());
        boolean activeOnly = (context.get("activeOnly") == null || ((Boolean) context.get("activeOnly")).booleanValue());

        // checkViewAllow defaults to false, must be set to true and pass the prodCatalogId to enable
        boolean checkViewAllow = (prodCatalogId != null && context.get("checkViewAllow") != null &&
                ((Boolean) context.get("checkViewAllow")).booleanValue());

        String viewProductCategoryId = null;
        if (checkViewAllow) {
            viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
        }

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        int viewIndex = 1;
        try {
            viewIndex = Integer.valueOf((String) context.get("viewIndexString")).intValue();
        } catch (Exception e) {
            viewIndex = 1;
        }

        int viewSize = defaultViewSize;
        try {
            viewSize = Integer.valueOf((String) context.get("viewSizeString")).intValue();
        } catch (Exception e) {
            viewSize = defaultViewSize;
        }

        GenericValue productCategory = null;
        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            productCategory = null;
        }

        int listSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if (limitView) {
            // get the indexes for the partial list
            lowIndex = (((viewIndex - 1) * viewSize) + 1);
            highIndex = viewIndex * viewSize;
         } else {
            lowIndex = 0;
            highIndex = 0;
        }

        List<GenericValue> productCategoryMembers = null;
        if (productCategory != null) {
            try {
                if (useCacheForMembers) {
                    productCategoryMembers = delegator.findByAndCache(entityName, UtilMisc.toMap("productCategoryId", productCategoryId), orderByFields);
                    if (activeOnly) {
                        productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                    }
                  //  List<EntityCondition> filterConditions = FastList.newInstance();
                    List<EntityCondition> filterConditions = new ArrayList<EntityCondition>();
                    if (introductionDateLimit != null) {
                        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit));
                        filterConditions.add(condition);
                    }
                    if (releaseDateLimit != null) {
                        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit));
                        filterConditions.add(condition);
                    }
                    if (!filterConditions.isEmpty()) {
                        productCategoryMembers = EntityUtil.filterByCondition(productCategoryMembers, EntityCondition.makeCondition(filterConditions, EntityOperator.AND));
                    }

                    // filter out the view allow before getting the sublist
                    if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
                        productCategoryMembers = CategoryWorker.filterProductsInCategory(delegator, productCategoryMembers, viewProductCategoryId);
                         listSize = productCategoryMembers.size();
                     
                    }
                    
                  
                     // set the index and size
                    if(UtilValidate.isEmpty(productCategoryMembers) && UtilValidate.isNotEmpty(context.get("prodCatalogSize"))) {
                     	int prodCatalogSize =(Integer)context.get("prodCatalogSize");
                     
                    listSize = prodCatalogSize;
                     } else {
                    	 listSize = productCategoryMembers.size();
                    }
                    if (highIndex > listSize) {
                        highIndex = listSize;
                    }

                    // get only between low and high indexes
                    if (limitView) {
                        if (UtilValidate.isNotEmpty(productCategoryMembers)) {
                            productCategoryMembers = productCategoryMembers.subList(lowIndex-1, highIndex);
 
                        }
                    } else {
                        lowIndex = 1;
                        highIndex = listSize;
                     }
                } else {
                  //  List<EntityCondition> mainCondList = FastList.newInstance();
                    List<EntityCondition> mainCondList = new ArrayList<EntityCondition>();
                    mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategory.getString("productCategoryId")));
                    if (activeOnly) {
                        mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                    }
                    if (introductionDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                    }
                    if (releaseDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                    }
                    EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);

                    // set distinct on
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
                    findOpts.setMaxRows(highIndex);
                    // using list iterator
                    EntityListIterator pli = delegator.find(entityName, mainCond, null, null, orderByFields, findOpts);

                    // get the partial list for this page
                    if (limitView) {
                        if (viewProductCategoryId != null) {
                            // do manual checking to filter view allow
                          //  productCategoryMembers = FastList.newInstance();
                            productCategoryMembers = new ArrayList<GenericValue>();
                            GenericValue nextValue;
                            int chunkSize = 0;
                            listSize = 0;

                            while ((nextValue = pli.next()) != null) {
                                String productId = nextValue.getString("productId");
                                if (CategoryWorker.isProductInCategory(delegator, productId, viewProductCategoryId)) {
                                    if (listSize + 1 >= lowIndex && chunkSize < viewSize) {
                                        productCategoryMembers.add(nextValue);
                                        chunkSize++;
                                    }
                                    listSize++;
                                }
                            }
                        } else {
                            productCategoryMembers = pli.getPartialList(lowIndex, viewSize);
                            listSize = pli.getResultsSizeAfterPartialList();
                        }
                    } else {
                        productCategoryMembers = pli.getCompleteList();
                        if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
                            // fiter out the view allow
                            productCategoryMembers = CategoryWorker.filterProductsInCategory(delegator, productCategoryMembers, viewProductCategoryId);
                        
                        }

                        listSize = productCategoryMembers.size();
                        lowIndex = 1;
                        highIndex = listSize;
                    }

                    // null safety
                    if (productCategoryMembers == null) {
                     //   productCategoryMembers = FastList.newInstance();
                        productCategoryMembers = new ArrayList<GenericValue>();
                    }

                    if (highIndex > listSize) {
                        highIndex = listSize;
                    }

                    // close the list iterator
                    pli.close();
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>(); // FastMap.newInstance();
        result.put("viewIndex", Integer.valueOf(viewIndex));
        result.put("viewSize", Integer.valueOf(viewSize));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("listSize", Integer.valueOf(listSize));
        if (productCategory != null) result.put("productCategory", productCategory);
        if (productCategoryMembers != null) result.put("productCategoryMembers", productCategoryMembers);
        return result;
    }
    public static Map<String, Object> getProductCategoryAndLimitedMembers1(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
       // String productCategoryId = (String) context.get("productCategoryId");
        List<String> productCategoryId = UtilGenerics.checkList(context.get("productCategoryId"));
        boolean limitView = ((Boolean) context.get("limitView")).booleanValue();
        int defaultViewSize = ((Integer) context.get("defaultViewSize")).intValue();
        Timestamp introductionDateLimit = (Timestamp) context.get("introductionDateLimit");
        Timestamp releaseDateLimit = (Timestamp) context.get("releaseDateLimit");
        
        //Refind Parameters  : Ajaya
        String refineByPrice = ((String) context.get("refineByPrice"));
        String refineByBrand = ((String) context.get("refineByBrand"));
        String sortBy = ((String) context.get("sortBy"));
        String excludeOutOfStock = ((String) context.get("excludeOutOfStock"));
        GenericValue productStore = ((GenericValue) context.get("productStore"));
        
        List<String> orderByFields = UtilGenerics.checkList(context.get("orderByFields"));
       // if (orderByFields == null) orderByFields = FastList.newInstance();
        if (orderByFields == null) orderByFields = new ArrayList<String>();
        orderByFields.add("internalName ASC");
        String entityName = getCategoryFindEntityName(delegator, orderByFields, introductionDateLimit, releaseDateLimit);

        //if(UtilValidate.isNotEmpty(sortBy) && "POPULAR_PRD".equals(sortBy)) entityName = "ProductAndCategoryMemberAndCalculatedInfo";
        
        String prodCatalogId = (String) context.get("prodCatalogId");

        boolean useCacheForMembers = (context.get("useCacheForMembers") == null || ((Boolean) context.get("useCacheForMembers")).booleanValue());
        boolean activeOnly = (context.get("activeOnly") == null || ((Boolean) context.get("activeOnly")).booleanValue());

        // checkViewAllow defaults to false, must be set to true and pass the prodCatalogId to enable
        boolean checkViewAllow = (prodCatalogId != null && context.get("checkViewAllow") != null &&
                ((Boolean) context.get("checkViewAllow")).booleanValue());

        String viewProductCategoryId = null;
        if (checkViewAllow) {
            viewProductCategoryId = CatalogWorker.getCatalogViewAllowCategoryId(delegator, prodCatalogId);
        }

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        int viewIndex = 1;
        try {
            viewIndex = Integer.valueOf((String) context.get("viewIndexString")).intValue();
        } catch (Exception e) {
            viewIndex = 1;
        }

        int viewSize = defaultViewSize;
        try {
            viewSize = Integer.valueOf((String) context.get("viewSizeString")).intValue();
        } catch (Exception e) {
            viewSize = defaultViewSize;
        }

       /* GenericValue productCategory = null;
        try {
            productCategory = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", productCategoryId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            productCategory = null;
        }*/

        int listSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if (limitView) {
            // get the indexes for the partial list
            lowIndex = (((viewIndex - 1) * viewSize) + 1);
            highIndex = viewIndex * viewSize;
        } else {
            lowIndex = 0;
            highIndex = 0;
        }

        List<GenericValue> productCategoryMembers = null;
        List<GenericValue> allProductCategoryMembers = null;
        List  listOfBrand = new ArrayList();
        List<GenericValue> allProductCategoryMembersForBrand = null;
        List refineByPriceList = null;
        List refineByBrandList = null;
     // if (productCategory != null) {
            try {
                /*if (useCacheForMembers) {
                    productCategoryMembers = delegator.findByAndCache(entityName, UtilMisc.toMap("productCategoryId", productCategoryId), orderByFields);
                    if (activeOnly) {
                        productCategoryMembers = EntityUtil.filterByDate(productCategoryMembers, true);
                    }
                    List<EntityCondition> filterConditions = FastList.newInstance();
                    if (introductionDateLimit != null) {
                        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit));
                        filterConditions.add(condition);
                    }
                    if (releaseDateLimit != null) {
                        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit));
                        filterConditions.add(condition);
                    }
                    if (!filterConditions.isEmpty()) {
                        productCategoryMembers = EntityUtil.filterByCondition(productCategoryMembers, EntityCondition.makeCondition(filterConditions, EntityOperator.AND));
                    }

                    // filter out the view allow before getting the sublist
                    if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
                        productCategoryMembers = CategoryWorker.filterProductsInCategory(delegator, productCategoryMembers, viewProductCategoryId);
                        listSize = productCategoryMembers.size();
                    }

                    // set the index and size
                    listSize = productCategoryMembers.size();
                    if (highIndex > listSize) {
                        highIndex = listSize;
                    }

                    // get only between low and high indexes
                    if (limitView) {
                        if (UtilValidate.isNotEmpty(productCategoryMembers)) {
                            productCategoryMembers = productCategoryMembers.subList(lowIndex-1, highIndex);
                        }
                    } else {
                        lowIndex = 1;
                        highIndex = listSize;
                    }
                }*/
                 //else {
                    //List<EntityCondition> mainCondList = FastList.newInstance();
                    List<EntityCondition> mainCondList = new ArrayList<EntityCondition>();
                    mainCondList.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, productCategoryId));
                    if (activeOnly) {
                        mainCondList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
                    }
                    if (introductionDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("introductionDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("introductionDate", EntityOperator.LESS_THAN_EQUAL_TO, introductionDateLimit)));
                    }
                    if (releaseDateLimit != null) {
                        mainCondList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("releaseDate", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("releaseDate", EntityOperator.LESS_THAN_EQUAL_TO, releaseDateLimit)));
                    }
                    if("Y".equals(excludeOutOfStock))
                    	mainCondList.add(EntityCondition.makeCondition("inventoryAtp", EntityOperator.GREATER_THAN_EQUAL_TO, BigDecimal.ONE));
                    
                    List salesDiscontinuationDateCond = new ArrayList();
            		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.EQUALS,null));
            		salesDiscontinuationDateCond.add(EntityCondition.makeCondition("salesDiscontinuationDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
                	
            		mainCondList.add(EntityCondition.makeCondition(salesDiscontinuationDateCond,EntityOperator.OR));
                    
                    EntityCondition mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
                    allProductCategoryMembersForBrand = delegator.findList(entityName, mainCond, null, null, null, true);
                     
                    /*if("Y".equals(excludeOutOfStock))
                    	allProductCategoryMembersForBrand = excludeOutOfStock(delegator, dctx.getDispatcher(), allProductCategoryMembersForBrand, productStore);
            		*/
                    
                    if(UtilValidate.isNotEmpty(allProductCategoryMembersForBrand)){
                    	   listOfBrand = getAllBrandsFromProductCategory(allProductCategoryMembersForBrand);
            			allProductCategoryMembersForBrand = sortBy(delegator, allProductCategoryMembersForBrand, sortBy, entityName);
                    }
            		
                  //refineByBrand
                    if(UtilValidate.isNotEmpty(refineByBrand))
                     {
             			String filtByBrand[] = refineByBrand.split(",");
             			refineByBrandList = Arrays.asList(filtByBrand);
             			List newCond = new ArrayList();
             			if(UtilValidate.isNotEmpty(filtByBrand))
             				for(String filtBybr : filtByBrand)
             					if(UtilValidate.isNotEmpty(filtBybr))
             						newCond.add(EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, filtBybr));
             			
             			mainCondList.add(EntityCondition.makeCondition(newCond, EntityOperator.OR));
                     }
                    mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
                    allProductCategoryMembers = EntityUtil.filterByCondition(allProductCategoryMembersForBrand, mainCond);
                    
                    //allProductCategoryMembers = delegator.findList(entityName, mainCond, null, null, null, true);
                  //refineByPrice
                  if(UtilValidate.isNotEmpty(refineByPrice))
                    {
            			String filtByPrice[] = refineByPrice.split(",");
            			refineByPriceList = Arrays.asList(filtByPrice);
            			List priceCondList = new ArrayList();
            			
            			for(String filtByPr : filtByPrice)
            			{
            				List priceCond = new ArrayList();
            				filtByPr = filtByPr.trim();
            				if(UtilValidate.isNotEmpty(filtByPr))
            				if("Less than Rs 20".equals(filtByPr))
            				{
            					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(20)));
            				}
            				else if("Rs 21 to 50".equals(filtByPr))
            				{
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(21)));
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(50)));
            					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
            				}
            				else if("Rs 51 to 100".equals(filtByPr))
            				{
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(51)));
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(100)));
            					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
            				}
            				else if("Rs 101 to 200".equals(filtByPr))
            				{
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(101)));
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(200)));
            					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
            				}
            				else if("Rs 201 to 500".equals(filtByPr))
            				{
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(201)));
            					priceCond.add(EntityCondition.makeCondition("basePrice", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(500)));
            					priceCondList.add(EntityCondition.makeCondition(priceCond, EntityOperator.AND));
            				}
            				else if("More than Rs 501".equals(filtByPr))
            				{
            					priceCondList.add(EntityCondition.makeCondition("basePrice", EntityOperator.GREATER_THAN_EQUAL_TO, new BigDecimal(500)));
            				}
            			}
            			mainCondList.add(EntityCondition.makeCondition(priceCondList, EntityOperator.OR));
                    }
                    
                    mainCond = EntityCondition.makeCondition(mainCondList, EntityOperator.AND);
                    // set distinct on
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
                    findOpts.setMaxRows(highIndex);
                    
                    List<GenericValue> newProductCategoryMembers = EntityUtil.filterByCondition(allProductCategoryMembersForBrand, mainCond);;//pli.getCompleteList();
                    
                    // get the partial list for this page
                    if (limitView) {
                        if (viewProductCategoryId != null) {
                            // do manual checking to filter view allow
                           // productCategoryMembers = FastList.newInstance();
                            productCategoryMembers = new ArrayList<GenericValue>();
                            GenericValue nextValue;
                            int chunkSize = 0;
                            listSize = 0;
                            //List<GenericValue> newProductCategoryMembers = EntityUtil.filterByCondition(allProductCategoryMembersForBrand, mainCond);
                           
                            
                            /* while ((nextValue = pli.next()) != null) {
                                String productId = nextValue.getString("productId");
                                if (CategoryWorker.isProductInCategory(delegator, productId, viewProductCategoryId)) {
                                    if (listSize + 1 >= lowIndex && chunkSize < viewSize) {
                                        productCategoryMembers.add(nextValue);
                                        chunkSize++;
                                    }
                                    listSize++;
                                }
                            }*/
                            if(UtilValidate.isNotEmpty(newProductCategoryMembers))
                            {
                            	for(GenericValue nextValue1 : newProductCategoryMembers)
                            	{
                            		String productId = nextValue1.getString("productId");
                                    if (CategoryWorker.isProductInCategory(delegator, productId, viewProductCategoryId)) {
                                        if (listSize + 1 >= lowIndex && chunkSize < viewSize) {
                                            productCategoryMembers.add(nextValue1);
                                            chunkSize++;
                                        }
                                        listSize++;
                                    }
                            	}
                            }
                           
                            
                        } else {
                        	//productCategoryMembers = FastList.newInstance();
                        	productCategoryMembers = new ArrayList<GenericValue>();
                        	
                        	
                        	// set the index and size
                            listSize = newProductCategoryMembers.size();
                            if (highIndex > listSize) {
                                highIndex = listSize;
                            }

                            // get only between low and high indexes
                            if (limitView) {
                                if (UtilValidate.isNotEmpty(newProductCategoryMembers)) {
                                    productCategoryMembers = newProductCategoryMembers.subList(lowIndex-1, highIndex);
                                }
                            } else {
                                lowIndex = 1;
                                highIndex = listSize;
                            }
                        	
                        	/*
                        	// using list iterator
                        	int lowIndex1 = lowIndex-1;
                        	//System.out.println("    lowIndex     "+lowIndex1);
                        	//System.out.println("    lowIndex+viewSize     "+lowIndex1+viewSize);
                        	if(UtilValidate.isNotEmpty(newProductCategoryMembers))
                        	 if(newProductCategoryMembers.size() >= lowIndex1+viewSize)
                        		 productCategoryMembers = newProductCategoryMembers.subList(lowIndex1, lowIndex+viewSize);
                        	 else
                        	 {
                        		 productCategoryMembers = newProductCategoryMembers.subList(lowIndex1, newProductCategoryMembers.size());
                        	 }*/
                        	/*int count = 0;
                        	if(UtilValidate.isNotEmpty(newProductCategoryMembers))
                            {
                            	for(GenericValue nextValue1 : newProductCategoryMembers)
                            	{
                            		if(count >= lowIndex && count < lowIndex+viewSize )
                                            productCategoryMembers.add(nextValue1);
                            	}
                            	listSize = newProductCategoryMembers.size();
                            }
                        	*/
                        	listSize = newProductCategoryMembers.size();
                        	
                           // EntityListIterator pli = delegator.find(entityName, mainCond, null, null, orderByFields, findOpts);
                            //productCategoryMembers = pli.getPartialList(lowIndex, viewSize);

                            ////System.out.println("    allProductCategoryMembersForBrand   5   "+productCategoryMembers.size());
                            
                        }
                    } else {
                        productCategoryMembers = newProductCategoryMembers;
                        if (UtilValidate.isNotEmpty(viewProductCategoryId)) {
                            // fiter out the view allow
                            productCategoryMembers = CategoryWorker.filterProductsInCategory(delegator, productCategoryMembers, viewProductCategoryId);
                        }
                        listSize = productCategoryMembers.size();
                        lowIndex = 1;
                        highIndex = listSize;
                    }
                    // null safety
                    if (productCategoryMembers == null) {
                      //  productCategoryMembers = FastList.newInstance();
                        productCategoryMembers = new ArrayList<GenericValue>();
                    }
                    if (highIndex > listSize) {
                        highIndex = listSize;
                    }

                    // close the list iterator
                   // pli.close();
             //   }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        //}

        Map<String, Object> result = new HashMap<String, Object>(); // FastMap.newInstance();
        result.put("viewIndex", Integer.valueOf(viewIndex));
        result.put("viewSize", Integer.valueOf(viewSize));
        result.put("lowIndex", Integer.valueOf(lowIndex));
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("listSize", Integer.valueOf(listSize));
        //if (productCategory != null) result.put("productCategory", productCategory);
        if (productCategoryMembers != null)
        	{
        		//productCategoryMembers = sortBy(delegator, productCategoryMembers, sortBy, entityName);
        		result.put("productCategoryMembers", productCategoryMembers);
        	}
        Map refineBy = refineProductCategoryMembers(delegator, allProductCategoryMembers, refineByBrand, refineByPrice);
        if (UtilValidate.isNotEmpty(refineBy))
        {
    		result.put("priceMap", refineBy.get("priceMap"));
    		result.put("refineByPriceList", refineByPriceList);
    	}
      
        if(UtilValidate.isNotEmpty(listOfBrand))
        {
	        result.put("brandMap", listOfBrand.get(0));
	        result.put("brandList", listOfBrand.get(1));
        }else
        {
	        result.put("brandMap", new HashMap());
	        result.put("brandList", new ArrayList());
        }
        result.put("refineByBrandList", refineByBrandList);
        return result;
    }
    public static List<GenericValue> excludeOutOfStock(Delegator delegator, LocalDispatcher dispatcher, List<GenericValue> productCategoryMembers, GenericValue productStore){
    	List<GenericValue> toBeRemoved = new ArrayList<GenericValue>();
    	
    	 try {
 			TransactionUtil.begin();
 		} catch (GenericTransactionException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
         
    	
    	for(GenericValue productCategoryMember : productCategoryMembers)
    	{
	    	BigDecimal atp = ProductWorker.totalAvailableATP(delegator, dispatcher, productStore, productCategoryMember.getString("productId"));
	    	if(UtilValidate.isNotEmpty(atp) && atp.doubleValue() <= 0)
	    		toBeRemoved.add(productCategoryMember);
	    }
    	try {
			TransactionUtil.commit();
		} catch (GenericTransactionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
    	for(GenericValue remove : toBeRemoved){
    		productCategoryMembers.remove(remove);
    	}
    	return productCategoryMembers;
    }
    public static List<GenericValue> sortBy(Delegator delegator, List<GenericValue> productCategoryMembers, String sortBy , String entityName){
    	 List orderBy = null;
			 		if("L_TO_H".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice ASC");
			 		else if("H_TO_L".equals(sortBy))
			 			orderBy = UtilMisc.toList("basePrice DESC");
			 		else if("A_TO_Z".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName ASC");
			 		else if("Z_TO_A".equals(sortBy))
			 			orderBy = UtilMisc.toList("internalName DESC");
			 		else 
			 			orderBy = UtilMisc.toList("sequenceNum DESC");
			 		
			 		productCategoryMembers = EntityUtil.orderBy(productCategoryMembers, orderBy);
		return productCategoryMembers;
    }
    public static List getAllBrandsFromProductCategory(List<GenericValue> productCategoryMembers){
		Map<String,Integer> brandMap =  new HashMap<String,Integer>();
		List<String> brandList =  new ArrayList<String>();
		
			for(GenericValue product : productCategoryMembers)
	    	{
    			if(UtilValidate.isNotEmpty(product))
    			{
					String brandName= product.getString("brandName");
					if(UtilValidate.isNotEmpty(brandName)){
						brandName = brandName.trim();
						int count  = 1;
						if(brandList.contains(brandName))
						{
	            			  count = count+brandMap.get(brandList.indexOf(brandName)+"");
						}
						else
						{
							brandList.add(brandName);
						}
						brandMap.put(brandList.indexOf(brandName)+"", count);
					}
    			}
	    	}
			return UtilMisc.toList(brandMap,brandList);
    }
    public static Map refineProductCategoryMembers(Delegator delegator, List<GenericValue> productCategoryMembers, String refineByBrand, String refineByPrice){
		Map data = new HashMap();
		Map<String,Integer> priceMap = new HashMap<String,Integer>();
		Map<String,Integer> brandMap =  new HashMap<String,Integer>();
		
		//String productId = null;
		try{
			/*if(UtilValidate.isEmpty(refineByPrice)) refineByPrice="";
			String filtByPrice[] = refineByPrice.split(",");
			List priceFilter = Arrays.asList(filtByPrice);
			
			if(UtilValidate.isEmpty(refineByBrand)) refineByBrand="";
			String refineByBrands[] = refineByBrand.split(",");
			*/
			for(GenericValue product : productCategoryMembers)
	    	{
    			if(UtilValidate.isNotEmpty(product))
    			{
					String brandName= product.getString("brandName");
					if(UtilValidate.isNotEmpty(brandName)){
						brandName = brandName.trim();
						int count  = 1;
						if(UtilValidate.isNotEmpty(brandMap.get(brandName)))
	            			  count = count+brandMap.get(brandName);
						
						brandMap.put(brandName, count);
					}
					
            	  BigDecimal basePrice = product.getBigDecimal("basePrice");
            	  if(UtilValidate.isNotEmpty(basePrice))
            	  if(basePrice.doubleValue() <= 20)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("Less than Rs 20 ")))
            			  count = count+priceMap.get("Less than Rs 20 ");
            		  
            		  priceMap.put("Less than Rs 20 ", count);
            	  }
            	  else if(basePrice.doubleValue() >= 21 && basePrice.doubleValue() <= 50)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 21 to 50 ")))
            			  count = count+priceMap.get("Rs 21 to 50 ");
            		   
            			  priceMap.put("Rs 21 to 50 ", count);
            	  }
            	  else if(basePrice.doubleValue() >= 51 && basePrice.doubleValue() <= 100)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 51 to 100 ")))
            			  count = count+priceMap.get("Rs 51 to 100 ");
            			  
            		  priceMap.put("Rs 51 to 100 ", count);
            	  }
            	  else if(basePrice.doubleValue() >= 101 && basePrice.doubleValue() <= 200)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 101 to 200 ")))
            			  count = count+priceMap.get("Rs 101 to 200 ");
            	 
            			  priceMap.put("Rs 101 to 200 ", count);
            	  }
            	  else if(basePrice.doubleValue() >= 201 && basePrice.doubleValue() <= 500)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("Rs 201 to 500 ")))
            			  count = count+priceMap.get("Rs 201 to 500 ");
            		 
            			  priceMap.put("Rs 201 to 500 ", count);
            	  }
            	  else if(basePrice.doubleValue() >= 500)
            	  {
            		  int count = 1;
            		  if(UtilValidate.isNotEmpty(priceMap.get("More than Rs 501 ")))
            			  count = count+priceMap.get("More than Rs 501 ");
            		 
            			  priceMap.put("More than Rs 501 ", count);
            	  }
					
    		  }
	    	}
	    	data.put("brandMap", brandMap);
	    	data.put("priceMap", priceMap);
	    	
			return data;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
    }
 
    // Please note : the structure of map in this function is according to the JSON data map of the jsTree
    @SuppressWarnings("unchecked")
    public static void getChildCategoryTree(HttpServletRequest request, HttpServletResponse response){
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String productCategoryId = request.getParameter("productCategoryId");
        String isCatalog = request.getParameter("isCatalog");
        String isCategoryType = request.getParameter("isCategoryType");
        String entityName = null;
        String primaryKeyName = null;
        
        if (isCatalog.equals("true")) {
            entityName = "ProdCatalog";
            primaryKeyName = "prodCatalogId";
        } else {
            entityName = "ProductCategory";
            primaryKeyName = "productCategoryId";
        }
        
        //List categoryList = FastList.newInstance();
        List categoryList = new ArrayList();
        List<GenericValue> childOfCats;
        List<String> sortList = org.ofbiz.base.util.UtilMisc.toList("sequenceNum", "title");
        
        try {
            GenericValue category = delegator.findByPrimaryKey(entityName ,UtilMisc.toMap(primaryKeyName, productCategoryId));
            if (UtilValidate.isNotEmpty(category)) {
                if (isCatalog.equals("true") && isCategoryType.equals("false")) {
                    CategoryWorker.getRelatedCategories(request, "ChildCatalogList", CatalogWorker.getCatalogTopCategoryId(request, productCategoryId), true);
                    childOfCats = EntityUtil.filterByDate((List<GenericValue>) request.getAttribute("ChildCatalogList"));
                    
                } else if(isCatalog.equals("false") && isCategoryType.equals("false")){
                    childOfCats = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryRollupAndChild", UtilMisc.toMap(
                            "parentProductCategoryId", productCategoryId )));
                } else {
                    childOfCats = EntityUtil.filterByDate(delegator.findByAnd("ProdCatalogCategory", UtilMisc.toMap("prodCatalogId", productCategoryId)));
                }
                if (UtilValidate.isNotEmpty(childOfCats)) {
                	
                    for (GenericValue childOfCat : childOfCats ) {
                        
                        Object catId = null;
                        String catNameField = null;
                        
                        catId = childOfCat.get("productCategoryId");
                        catNameField = "CATEGORY_NAME";
                        
                        Map josonMap = new HashMap(); //FastMap.newInstance();
                        List<GenericValue> childList = null;
                        
                        // Get the child list of chosen category
                        childList = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap(
                                    "parentProductCategoryId", catId)));
                        
                        // Get the chosen category information for the categoryContentWrapper
                        GenericValue cate = delegator.findByPrimaryKey("ProductCategory" ,UtilMisc.toMap("productCategoryId",catId));
                        
                        // If chosen category's child exists, then put the arrow before category icon
                        if (UtilValidate.isNotEmpty(childList)) {
                            josonMap.put("state", "closed");
                        }
                        Map dataMap = new HashMap(); //FastMap.newInstance();
                        Map dataAttrMap = new HashMap(); //FastMap.newInstance();
                        CategoryContentWrapper categoryContentWrapper = new CategoryContentWrapper(cate, request);
                        
                        String title = null;
                        if (UtilValidate.isNotEmpty(categoryContentWrapper.get(catNameField))) {
                            title = categoryContentWrapper.get(catNameField)+" "+"["+catId+"]";
                            dataMap.put("title", title);
                        } else {
                            title = catId.toString();
                            dataMap.put("title", catId);
                        }
                        dataAttrMap.put("onClick","window.location.href='EditCategory?productCategoryId="+catId+"'; return false;");
                        
                        dataMap.put("attr", dataAttrMap);
                        josonMap.put("data", dataMap);
                        Map attrMap = new HashMap(); //FastMap.newInstance();
                        attrMap.put("id", catId);
                        attrMap.put("isCatalog", false);
                        attrMap.put("rel", "CATEGORY");
                        josonMap.put("attr",attrMap);
                        josonMap.put("sequenceNum",childOfCat.get("sequenceNum"));
                        josonMap.put("title",title);
                        
                        categoryList.add(josonMap);
                    }
                    List<Map<Object, Object>> sortedCategoryList = UtilMisc.sortMaps(categoryList, sortList);
                    toJsonObjectList(sortedCategoryList,response);
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void toJsonObjectList(List attrList, HttpServletResponse response){
        String jsonStr = "[";
        for (Object attrMap : attrList) {
            JSONObject json = JSONObject.fromObject(attrMap);
            jsonStr = jsonStr + json.toString() + ',';
        }
        jsonStr = jsonStr + "{ } ]";
        if (UtilValidate.isEmpty(jsonStr)) {
            Debug.logError("JSON Object was empty; fatal error!",module);
        }
        // set the X-JSON content type
        response.setContentType("application/json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding",module);
        }
        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError("Unable to get response writer",module);
        }
    }
}


class ValueComparator implements Comparator<String> {

    Map<String, BigDecimal> base;
    public ValueComparator(Map<String, BigDecimal> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a).doubleValue() <= base.get(b).doubleValue()) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
