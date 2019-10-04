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
package org.ofbiz.order.shoppinglist;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


//import javolution.util.FastList;
//import javolution.util.FastMap;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.config.ProductConfigWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Shopping cart events.
 */
public class ShoppingListEvents {

    public static final String module = ShoppingListEvents.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";
    public static final String PERSISTANT_LIST_NAME = "auto-save";

    public static String addBulkFromCart(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        String shoppingListId = request.getParameter("shoppingListId");
        String shoppingListTypeId = request.getParameter("shoppingListTypeId");
        String selectedCartItems[] = request.getParameterValues("selectedItem");
        if (UtilValidate.isEmpty(selectedCartItems)) {
            selectedCartItems = makeCartItemsArray(cart);
        }

        try {
            shoppingListId = addBulkFromCart(delegator, dispatcher, cart, userLogin, shoppingListId, shoppingListTypeId, selectedCartItems, true, true);
        } catch (IllegalArgumentException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        request.setAttribute("shoppingListId", shoppingListId);
        return "success";
    }

    public static String addBulkFromCart(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, GenericValue userLogin, String shoppingListId, String shoppingListTypeId, String[] items, boolean allowPromo, boolean append) throws IllegalArgumentException {
        String errMsg = null;

        if (items == null || items.length == 0) {
            errMsg = UtilProperties.getMessage(resource_error, "shoppinglistevents.select_items_to_add_to_list", cart.getLocale());
            throw new IllegalArgumentException(errMsg);
        }
       
        if (UtilValidate.isEmpty(shoppingListId)) {
            // create a new shopping list
            Map<String, Object> newListResult = null;
            try {
                newListResult = dispatcher.runSync("createShoppingList", UtilMisc.<String, Object>toMap("userLogin", userLogin, "productStoreId", cart.getProductStoreId(), "partyId", cart.getOrderPartyId(), "shoppingListTypeId", shoppingListTypeId, "currencyUom", cart.getCurrency()));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problems creating new ShoppingList", module);
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.cannot_create_new_shopping_list", cart.getLocale());
                throw new IllegalArgumentException(errMsg);
            }

            // check for errors
            if (ServiceUtil.isError(newListResult)) {
                throw new IllegalArgumentException(ServiceUtil.getErrorMessage(newListResult));
            }

            // get the new list id
            if (newListResult != null) {
                shoppingListId = (String) newListResult.get("shoppingListId");
            }

            // if no list was created throw an error
            if (shoppingListId == null || shoppingListId.equals("")) {
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.shoppingListId_is_required_parameter", cart.getLocale());
                throw new IllegalArgumentException(errMsg);
            }
        } else if (!append) {
            try {
                clearListInfo(delegator, shoppingListId);
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new IllegalArgumentException("Could not clear current shopping list: " + e.toString());
            }
        }

        for (int i = 0; i < items.length; i++) {
            Integer cartIdInt = null;
            try {
                cartIdInt = Integer.valueOf(items[i]);
            } catch (Exception e) {
                Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderIllegalCharacterInSelectedItemField", cart.getLocale()), module);
            }
            if (cartIdInt != null) {
                ShoppingCartItem item = cart.findCartItem(cartIdInt.intValue());
                if (allowPromo || !item.getIsPromo()) {
                    Debug.logInfo("Adding cart item to shopping list [" + shoppingListId + "], allowPromo=" + allowPromo + ", item.getIsPromo()=" + item.getIsPromo() + ", item.getProductId()=" + item.getProductId() + ", item.getQuantity()=" + item.getQuantity(), module);
                    Map<String, Object> serviceResult = null;
                    try {
                        Map<String, Object> ctx = UtilMisc.<String, Object>toMap("userLogin", userLogin, "shoppingListId", shoppingListId, "productId", item.getProductId(), "quantity", item.getQuantity());
                        ctx.put("reservStart", item.getReservStart());
                        ctx.put("reservLength", item.getReservLength());
                        ctx.put("reservPersons", item.getReservPersons());
                  //    ctx.put("accommodationMapId", item.getAccommodationMapId());
                  //    ctx.put("accommodationSpotId", item.getAccommodationSpotId());
                        if (item.getConfigWrapper() != null) {
                            ctx.put("configId", item.getConfigWrapper().getConfigId());
                        }
                        serviceResult = dispatcher.runSync("createShoppingListItem", ctx);
                    } catch (GenericServiceException e) {
                        Debug.logError(e, "Problems creating ShoppingList item entity", module);
                        errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.error_adding_item_to_shopping_list", cart.getLocale());
                        throw new IllegalArgumentException(errMsg);
                    }

                    // check for errors
                    if (ServiceUtil.isError(serviceResult)) {
                        throw new IllegalArgumentException(ServiceUtil.getErrorMessage(serviceResult));
                    }
                }
            }
        }

        // return the shoppinglist id
        return shoppingListId;
    }

    public static String addListToCart(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);

        String shoppingListId = request.getParameter("shoppingListId");
        String includeChild = request.getParameter("includeChild");
        String prodCatalogId =  CatalogWorker.getCurrentCatalogId(request);

        String eventMessage = null;
        try {
            addListToCart(delegator, dispatcher, cart, prodCatalogId, shoppingListId, (includeChild != null), true, true);
        } catch (IllegalArgumentException e) {
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        if (UtilValidate.isNotEmpty(eventMessage)) {
            request.setAttribute("_EVENT_MESSAGE_", eventMessage);
        }

        return "success";
    }

    public static String addListToCart(Delegator delegator, LocalDispatcher dispatcher, ShoppingCart cart, String prodCatalogId, String shoppingListId, boolean includeChild, boolean setAsListItem, boolean append) throws java.lang.IllegalArgumentException {
        String errMsg = null;

        // no list; no add
        if (shoppingListId == null) {
            errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.choose_shopping_list", cart.getLocale());
            throw new IllegalArgumentException(errMsg);
        }
        
        
        if(org.ofbiz.order.shoppingcart.ShoppingCartEvents.isCartContainsGiftCard(cart)){
         	try {
				clearListInfo(delegator, shoppingListId);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
        // get the shopping list
        GenericValue shoppingList = null;
        List<GenericValue> shoppingListItems = null;
        try {
            shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", shoppingListId));
            if (shoppingList == null) {
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.error_getting_shopping_list_and_items", cart.getLocale());
                throw new IllegalArgumentException(errMsg);
            }

            shoppingListItems = shoppingList.getRelated("ShoppingListItem");
            if (shoppingListItems == null) {
                //shoppingListItems = FastList.newInstance();
                shoppingListItems = new ArrayList<GenericValue>();
            }

            // include all items of child lists if flagged to do so
            if (includeChild) {
                List<GenericValue> childShoppingLists = shoppingList.getRelated("ChildShoppingList");
                Iterator<GenericValue> ci = childShoppingLists.iterator();
                while (ci.hasNext()) {
                    GenericValue v = ci.next();
                    List<GenericValue> items = v.getRelated("ShoppingListItem");
                    shoppingListItems.addAll(items);
                }
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting ShoppingList and ShoppingListItem records", module);
            errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.error_getting_shopping_list_and_items", cart.getLocale());
            throw new IllegalArgumentException(errMsg);
        }

        // no items; not an error; just mention that nothing was added
        if (UtilValidate.isEmpty(shoppingListItems)) {
            errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.no_items_added", cart.getLocale());
            return errMsg;
        }

        // check if we are to clear the cart first
        if (!append) {
            cart.clear();
        }

        // get the survey info for all the items
        Map<String, List<String>> shoppingListSurveyInfo = getItemSurveyInfos(shoppingListItems);

        // add the items
        StringBuilder eventMessage = new StringBuilder();
        Iterator<GenericValue> i = shoppingListItems.iterator();
        while (i.hasNext()) {
            GenericValue shoppingListItem = i.next();
            String productId = shoppingListItem.getString("productId");
            BigDecimal quantity = shoppingListItem.getBigDecimal("quantity");
            Timestamp reservStart = shoppingListItem.getTimestamp("reservStart");
            BigDecimal reservLength = shoppingListItem.getBigDecimal("reservLength");
            BigDecimal reservPersons = shoppingListItem.getBigDecimal("reservPersons");
      //    String accommodationMapId = shoppingListItem.getString("accommodationMapId");
      //    String accommodationSpotId = shoppingListItem.getString("accommodationSpotId");
            String configId = shoppingListItem.getString("configId");
            try {
                String listId = shoppingListItem.getString("shoppingListId");
                String itemId = shoppingListItem.getString("shoppingListItemSeqId");

               // Map<String, Object> attributes = FastMap.newInstance();
                Map<String, Object> attributes = new HashMap<String, Object>();
                // list items are noted in the shopping cart
                if (setAsListItem) {
                    attributes.put("shoppingListId", listId);
                    attributes.put("shoppingListItemSeqId", itemId);
                }

                // check if we have existing survey responses to append
                if (shoppingListSurveyInfo.containsKey(listId + "." + itemId)) {
//                    attributes.put("surveyResponses", shoppingListSurveyInfo.get(listId + "." + itemId));
                }

                ProductConfigWrapper configWrapper = null;
                if (UtilValidate.isNotEmpty(configId)) {
                    configWrapper = ProductConfigWorker.loadProductConfigWrapper(delegator, dispatcher, configId, productId, cart.getProductStoreId(), prodCatalogId, cart.getWebSiteId(), cart.getCurrency(), cart.getLocale(), cart.getAutoUserLogin());
                }
                // TODO: add code to check for survey response requirement

                // i cannot get the addOrDecrease function to accept a null reservStart field: i get a null pointer exception a null constant works....
                if (reservStart == null) {
                       cart.addOrIncreaseItem(productId, null, quantity, null, null, null, null, null, null, attributes, prodCatalogId, configWrapper, null, null, null, null, null, null, null, dispatcher);
                } else {
                    cart.addOrIncreaseItem(productId, null, quantity, reservStart, reservLength, reservPersons, null, null, null, null, null, attributes, prodCatalogId, configWrapper, null, null, null,null, null, null, null, dispatcher);
                }
                Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.added_product_to_cart", messageMap, cart.getLocale());
                eventMessage.append(errMsg).append("\n");
            } catch (CartItemModifyException e) {
                Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProblemsAddingItemFromListToCart", cart.getLocale()));
                Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.problem_adding_product_to_cart", messageMap, cart.getLocale());
                eventMessage.append(errMsg).append("\n");
            } catch (ItemNotFoundException e) {
                Debug.logWarning(e, UtilProperties.getMessage(resource_error,"OrderProductNotFound", cart.getLocale()));
                Map<String, Object> messageMap = UtilMisc.<String, Object>toMap("productId", productId);
                errMsg = UtilProperties.getMessage(resource_error,"shoppinglistevents.problem_adding_product_to_cart", messageMap, cart.getLocale());
                eventMessage.append(errMsg).append("\n");
            }
        }

        if (eventMessage.length() > 0) {
            return eventMessage.toString();
        }

        // all done
        return ""; // no message to return; will simply reply as success
    }

    public static String replaceShoppingListItem(HttpServletRequest request, HttpServletResponse response) {
        String quantityStr = request.getParameter("quantity");

        // just call the updateShoppingListItem service
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        BigDecimal quantity = null;
        try {
            quantity = new BigDecimal(quantityStr);
        } catch (Exception e) {
            // do nothing, just won't pass to service if it is null
        }

       // Map<String, Object> serviceInMap = FastMap.newInstance();
        Map<String, Object> serviceInMap = new HashMap<String, Object>();
        
        serviceInMap.put("shoppingListId", request.getParameter("shoppingListId"));
        serviceInMap.put("shoppingListItemSeqId", request.getParameter("shoppingListItemSeqId"));
        serviceInMap.put("productId", request.getParameter("add_product_id"));
        serviceInMap.put("userLogin", userLogin);
        if (quantity != null) serviceInMap.put("quantity", quantity);
        Map<String, Object> result = null;
        try {
            result = dispatcher.runSync("updateShoppingListItem", serviceInMap);
        } catch (GenericServiceException e) {
            String errMsg = UtilProperties.getMessage(resource_error,"shoppingListEvents.error_calling_update", locale) + ": "  + e.toString();
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            String errorMsg = "Error calling the updateShoppingListItem in handleShoppingListItemVariant: " + e.toString();
            Debug.logError(e, errorMsg, module);
            return "error";
        }

        ServiceUtil.getMessages(request, result, "", "", "", "", "", "", "");
        if ("error".equals(result.get(ModelService.RESPONSE_MESSAGE))) {
            return "error";
        } else {
            return "success";
        }
    }

    /**
     * Finds or creates a specialized (auto-save) shopping list used to record shopping bag contents between user visits.
     */
    public static String getAutoSaveListId(Delegator delegator, LocalDispatcher dispatcher, String partyId, GenericValue userLogin, String productStoreId) throws GenericEntityException, GenericServiceException {
 
        if (partyId == null && userLogin != null) {
            partyId = userLogin.getString("partyId");
        }
        
        
        
        String autoSaveListId = null;
        // TODO: add sorting, just in case there are multiple...
        Map<String, Object> findMap = UtilMisc.<String, Object>toMap("partyId", partyId, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
        
        
        List<GenericValue> existingLists = delegator.findByAnd("ShoppingList", findMap);
        Debug.logInfo("Finding existing auto-save shopping list with:  \nfindMap: " + findMap + "\nlists: " + existingLists, module);

        GenericValue list = null;
        if (existingLists != null && !existingLists.isEmpty()) {
            list = EntityUtil.getFirst(existingLists);
            autoSaveListId = list.getString("shoppingListId");
        }
        
         if (list == null && dispatcher != null && userLogin != null) {
            Map<String, Object> listFields = UtilMisc.<String, Object>toMap("userLogin", userLogin, "productStoreId", productStoreId, "shoppingListTypeId", "SLT_SPEC_PURP", "listName", PERSISTANT_LIST_NAME);
           Map<String, Object> newListResult = dispatcher.runSync("createShoppingList", listFields);

            if (newListResult != null) {
                autoSaveListId = (String) newListResult.get("shoppingListId");
            }
        }

        return autoSaveListId;
    }

    /**
     * Fills the specialized shopping list with the current shopping cart if one exists (if not leaves it alone)
     */
    public static void fillAutoSaveList(ShoppingCart cart, LocalDispatcher dispatcher) throws GeneralException {
        if (cart != null && dispatcher != null) {
            GenericValue userLogin = ShoppingListEvents.getCartUserLogin(cart);
             if (userLogin == null) return; //only save carts when a user is logged in....
            Delegator delegator = cart.getDelegator();
            String autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
 
            try {
                String[] itemsArray = makeCartItemsArray(cart);
                
                System.out.println("\n\n itemsArray == "+Arrays.toString(itemsArray)+"\n\n");
                
                if (itemsArray != null && itemsArray.length != 0) {
                	if(!(org.ofbiz.order.shoppingcart.ShoppingCartEvents.isCartContainsGiftCard(cart)))
                    addBulkFromCart(delegator, dispatcher, cart, userLogin, autoSaveListId, null, itemsArray, false, false);
                }
                else{
                     clearListInfo(delegator, autoSaveListId);
         }
            } catch (IllegalArgumentException e) {
                throw new GeneralException(e.getMessage(), e);
            }
        }
    }

    /**
     * Saves the shopping cart to the specialized (auto-save) shopping list
     */
    public static String saveCartToAutoSaveList(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        try {
            fillAutoSaveList(cart, dispatcher);
        } catch (GeneralException e) {
            Debug.logError(e, "Error saving the cart to the auto-save list: " + e.toString(), module);
        }

        return "success";
    }

    /**
     * Restores the specialized (auto-save) shopping list back into the shopping cart
     */
    public static String restoreAutoSaveList(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue productStore = ProductStoreWorker.getProductStore(request);

        if (!ProductStoreWorker.autoSaveCart(productStore)) {
            // if auto-save is disabled just return here
            return "success";
        }

         HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);

        // safety check for missing required parameter.
        if (cart.getWebSiteId() == null) {
            cart.setWebSiteId(CatalogWorker.getWebSiteId(request));
        }

        // locate the user's identity
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        if (userLogin == null) {
            userLogin = (GenericValue) session.getAttribute("autoUserLogin");
        }

        if (userLogin == null) {
            // not logged in; cannot identify the user
            return "success";
        }

        // find the list ID
        String autoSaveListId = cart.getAutoSaveListId();
   
        if (autoSaveListId == null) {
            try {
//            	if(UtilValidate.isEmpty(cart.items())) // cart should be merged so dont want any condition
//            			{
             				autoSaveListId = getAutoSaveListId(delegator, dispatcher, null, userLogin, cart.getProductStoreId());
//               			}
            } catch (GeneralException e) {
                Debug.logError(e, module);
            }
            cart.setAutoSaveListId(autoSaveListId);
            
        }
        

        // check to see if we are okay to load this list
        java.sql.Timestamp lastLoad = cart.getLastListRestore();
        boolean okayToLoad = autoSaveListId == null ? false : (lastLoad == null ? true : false);
 
        if (!okayToLoad && lastLoad != null) {
            GenericValue shoppingList = null;
            try {
                shoppingList = delegator.findByPrimaryKey("ShoppingList", UtilMisc.toMap("shoppingListId", autoSaveListId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (shoppingList != null) {
                java.sql.Timestamp lastModified = shoppingList.getTimestamp("lastAdminModified");
                if (lastModified != null) {
                    if (lastModified.after(lastLoad)) {
                        okayToLoad = true;
                    }
                    if (cart.size() == 0 && lastModified.after(cart.getCartCreatedTime())) {
                        okayToLoad = true;
                    }
                }
            }
        }
        
 
        // load (restore) the list of we have determined it is okay to load
        if (okayToLoad) {
            String prodCatalogId = CatalogWorker.getCurrentCatalogId(request);
            try {
                addListToCart(delegator, dispatcher, cart, prodCatalogId, autoSaveListId, false, false, true);
                cart.setLastListRestore(UtilDateTime.nowTimestamp());
                GenericValue temp   = cart.getUserLogin();
                if(temp == null){
	                try {
						cart.setUserLogin(userLogin,dispatcher);
					} catch (CartItemModifyException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
                try {
					fillAutoSaveList(cart,dispatcher);
				} catch (GeneralException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } catch (IllegalArgumentException e) {
                Debug.logError(e, module);
            }
        }

        return "success";
    }

    /**
     * Remove all items from the given list.
     */
    public static int clearListInfo(Delegator delegator, String shoppingListId) throws GenericEntityException {
        // remove the survey responses first
        delegator.removeByAnd("ShoppingListItemSurvey", UtilMisc.toMap("shoppingListId", shoppingListId));

        // next remove the items
        return delegator.removeByAnd("ShoppingListItem", UtilMisc.toMap("shoppingListId", shoppingListId));
    }

    /**
     * Creates records for survey responses on survey items
     */
    public static int makeListItemSurveyResp(Delegator delegator, GenericValue item, List<String> surveyResps) throws GenericEntityException {
        if (UtilValidate.isNotEmpty(surveyResps)) {
            Iterator<String> i = surveyResps.iterator();
            int count = 0;
            while (i.hasNext()) {
                String responseId = i.next();
                GenericValue listResp = delegator.makeValue("ShoppingListItemSurvey");
                listResp.set("shoppingListId", item.getString("shoppingListId"));
                listResp.set("shoppingListItemSeqId", item.getString("shoppingListItemSeqId"));
                listResp.set("surveyResponseId", responseId);
                delegator.create(listResp);
                count++;
            }
            return count;
        }
        return -1;
    }

    /**
     * Returns Map keyed on item sequence ID containing a list of survey response IDs
     */
    public static Map<String, List<String>> getItemSurveyInfos(List<GenericValue> items) {
       // Map<String, List<String>> surveyInfos = FastMap.newInstance();
        Map<String, List<String>> surveyInfos = new HashMap<String, List<String>>();
        
        if (UtilValidate.isNotEmpty(items)) {
            Iterator<GenericValue> itemIt = items.iterator();
            while (itemIt.hasNext()) {
                GenericValue item = itemIt.next();
                String listId = item.getString("shoppingListId");
                String itemId = item.getString("shoppingListItemSeqId");
                surveyInfos.put(listId + "." + itemId, getItemSurveyInfo(item));
            }
        }

        return surveyInfos;
    }

    /**
     * Returns a list of survey response IDs for a shopping list item
     */
    public static List<String> getItemSurveyInfo(GenericValue item) {
       // List<String> responseIds = FastList.newInstance();
        List<String> responseIds = new ArrayList<String>();
        List<GenericValue> surveyResp = null;
        try {
            surveyResp = item.getRelated("ShoppingListItemSurvey");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (UtilValidate.isNotEmpty(surveyResp)) {
            Iterator<GenericValue> respIt = surveyResp.iterator();
            while (respIt.hasNext()) {
                GenericValue resp = respIt.next();
                responseIds.add(resp.getString("surveyResponseId"));
            }
        }

        return responseIds;
    }

    private static GenericValue getCartUserLogin(ShoppingCart cart) {
        GenericValue ul = cart.getUserLogin();
        if (ul == null) {
            ul = cart.getAutoUserLogin();
        }
        return ul;
    }

    private static String[] makeCartItemsArray(ShoppingCart cart) {
        int len = cart.size();
        String[] arr = new String[len];
        for (int i = 0; i < len; i++) {
            arr[i] = Integer.toString(i);
        }
        return arr;
    }
    
    public static String storeEmaillocation(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	
    	
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String personName = null;
    	//Map<String, Object> userLoginName = FastMap.newInstance();
    	Map<String, Object> userLoginName = new HashMap<String, Object>();
    	if(UtilValidate.isNotEmpty(userLogin)){
    		String userLoginId = userLogin.getString("userLoginId");
    		GenericValue gv=delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",userLoginId));
    		if(UtilValidate.isNotEmpty(gv)){
    			String partyId = gv.getString("partyId");
    			GenericValue personDetail=delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId));
    			if(UtilValidate.isNotEmpty(personDetail)){
    				personName = personDetail.getString("firstName");
    				
    			}
    		}
    	}
    	
        HashMap<String, Object> orderSlotMap = new HashMap();
        String pinCode=request.getParameter("pinCode");
        String emailId=request.getParameter("emailId");   
        orderSlotMap.put("serviceId", delegator.getNextSeqId("NonServiceZone"));
        orderSlotMap.put("emailId", emailId);
        orderSlotMap.put("pinCode", pinCode);
       
        GenericValue orderSlot = delegator.makeValue("NonServiceZone", orderSlotMap);
        orderSlot.create();
        
        String emailType = "PIN_LOCATION_EMAIL";
        String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#pinLocationMail";
        
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String productStoreId = productStore.getString("productStoreId");

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
        //Map<String, Object> bodyParameters = FastMap.newInstance();
        Map<String, Object> bodyParameters = new HashMap<String, Object>();
        
        bodyParameters.put("name", emailId);
        if(UtilValidate.isNotEmpty(personName)){
        	bodyParameters.put("userLoginName", personName);
        }
        
       // Map<String, Object> serviceContext = FastMap.newInstance();
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emailId);

			try {
				if(!(pinCode.equals("123launch")))
		        {
				dispatcher.runAsync("sendMailFromScreen", serviceContext);
		        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        
		
       List pinCodeList=delegator.findList("NonServiceZone", EntityCondition.makeCondition("pinCode",EntityOperator.EQUALS,pinCode), null, null, null, false);
        if(UtilValidate.isNotEmpty(pinCodeList))
        {
        	if(pinCodeList.size()>100)
        	{
        		 String emailType1 = "PIN_EXCEED_EMAIL";
        	        String defaultScreenLocation1 = "component://ecommerce/widget/ecomclone/EmailProductScreens.xml#pinexceedMail";

        	        try {
        	            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType1);
        	        } catch (GenericEntityException e) {
        	            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        	        }
        	        
        	        bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        	        if (UtilValidate.isEmpty(bodyScreenLocation)) {
        	            bodyScreenLocation = defaultScreenLocation1;
        	        }
        	      //  Map<String, Object> bodyParameters1 = FastMap.newInstance();
        	        Map<String, Object> bodyParameters1 = new HashMap<String, Object>();
        	        
        	        bodyParameters1.put("name", emailId);
        	        bodyParameters1.put("pinCode", pinCode);
        	        
        	        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        	        serviceContext.put("bodyParameters", bodyParameters1);
        	        serviceContext.put("subject", productStoreEmail.getString("subject"));
        	        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        	        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        	        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        	        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        	        serviceContext.put("sendTo", productStoreEmail.get("fromAddress"));

        				try {
        					if(!(pinCode.equals("123launch")))
        			        {
        					dispatcher.runAsync("sendMailFromScreen", serviceContext);
        			        }
        				} catch (Exception e) {
        					e.printStackTrace();
        				} 
        	}
        }
        
        return "success";
    }
    
    
    public static String sendEmailFeedback(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        String emailId=request.getParameter("email");   
        String message=request.getParameter("message");   
        String commentSelect=request.getParameter("commentSelect");
      
        String emailType = "FEEDBACK_EMAIL";
        String emailTypeTo = "FEEDBACK_EMAIL_TO";
        String defaultScreenLocation = "component://ecommerce/widget/EmailProductScreens.xml#feedBackEmail";
        String defaultScreenLocationTemplate = "component://ecommerce/widget/EmailProductScreens.xml#feedBackEmailTemplate";
        GenericValue productStore = ProductStoreWorker.getProductStore(request);
        String productStoreId = productStore.getString("productStoreId");

        GenericValue productStoreEmail = null;
        GenericValue productStoreEmailTemplate = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }
        try {
            productStoreEmailTemplate = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailTypeTo);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }
        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }
        String bodyScreenLocationTemplate = productStoreEmailTemplate.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocationTemplate)) {
        	bodyScreenLocationTemplate = defaultScreenLocationTemplate;
        }
      
        //Map<String, Object> bodyParameters = FastMap.newInstance();
        Map<String, Object> bodyParameters = new HashMap<String, Object>();
        
        bodyParameters.put("name", emailId);
        //Map<String, Object> bodyParameter = FastMap.newInstance();
        Map<String, Object> bodyParameter = new HashMap<String, Object>();
        
        bodyParameter.put("message",message);
        bodyParameter.put("name",emailId);
        bodyParameter.put("commentSelect",commentSelect);
        String sendTo = UtilProperties.getPropertyValue("general.properties","feedBackUs.toMail");
       // Map<String, Object> serviceContext = FastMap.newInstance();
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        serviceContext.put("bodyParameters", bodyParameters);
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("sendBcc", productStoreEmail.get("bccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", emailId);
      
        Map<String, Object> serviceCxt = new HashMap<String, Object>();
        
        serviceCxt.put("subject", productStoreEmailTemplate.getString("subject"));
        serviceCxt.put("bodyScreenUri", defaultScreenLocationTemplate);
        serviceCxt.put("bodyParameters", bodyParameter);
        serviceCxt.put("subject", productStoreEmailTemplate.getString("subject"));
        serviceCxt.put("sendFrom", emailId);
        serviceCxt.put("sendCc", productStoreEmailTemplate.get("ccAddress"));
        serviceCxt.put("sendBcc", productStoreEmailTemplate.get("bccAddress"));
        serviceCxt.put("contentType", productStoreEmailTemplate.get("contentType"));
        serviceCxt.put("sendTo", sendTo);
        try {
			dispatcher.runAsync("sendMailFromScreen", serviceCxt);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    
        
			try {
				dispatcher.runAsync("sendMailFromScreen", serviceContext);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		try {
			response.getWriter().print("Your feedback has successfully submitted .");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//request.setAttribute("_EVENT_MESSAGE_", "Your feedback has successfully submitted .");
        return "success";
    }
    
    
    
    public static String ProductImportAction(HttpServletRequest request , HttpServletResponse response) throws GenericEntityException
    {
	    	String virtualId=request.getParameter("radioVariantId");
	    	String[] variantIds=request.getParameterValues("checkProductVariants");
	    	String productName=request.getParameter("productName");
	    	
	    	
	    	
	    	Set variantSet=new LinkedHashSet();

	    	for(String s:variantIds)
	    		variantSet.add(s);
	    	Delegator delegator = (Delegator) request.getAttribute("delegator");
	        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	    	List<GenericValue> virtualList=delegator.findList("DataImportOriginalProduct", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,virtualId), null, null, null, false);
 	        GenericValue gv=EntityUtil.getFirst(virtualList);
            
 	    	 GenericValue newImportProduct = delegator.makeValue("DataImportProduct");
  	     	 String exportProductSeqId = delegator.getNextSeqId("DataImportProduct");
 	     	 newImportProduct.set("exportProductSeqId", exportProductSeqId);
 	     	 if(variantSet.size()==1)
		      newImportProduct.set("productId", gv.getString("productId"));
	     	  else
	     		newImportProduct.set("productId", delegator.getNextSeqId("DataImportProduct")); 
	     	
	     	 if(UtilValidate.isEmpty(request.getParameter("isVirtual")))
	     		 newImportProduct.set("isVirtual", "N");
	     	 else
	     		newImportProduct.set("isVirtual", "Y"); 
	     	
	     	 if(UtilValidate.isEmpty(request.getParameter("isVariant")))
	     	 newImportProduct.set("isVariant", "N");
	     	 else
	     		newImportProduct.set("isVariant", "Y");
	     	 
 	     	 if(gv.getString("artGroupNo") != null && gv.getString("artGroupNo").length() > 0)
 	     		newImportProduct.set("artGroupNo", gv.getString("artGroupNo"));
 	     	 if(gv.getString("artGroupSubNo") != null && gv.getString("artGroupSubNo").length() > 0)
	     		newImportProduct.set("artGroupSubNo", gv.getString("artGroupSubNo"));
	     	 if(gv.getString("productTypeId") != null && gv.getString("productTypeId").length() > 0)
	     		 newImportProduct.set("productTypeId", gv.getString("productTypeId"));
	     	 if(!UtilValidate.isEmpty(productName) && UtilValidate.isNotEmpty(request.getParameter("isVirtual")))
 	    	  newImportProduct.set("internalName", gv.getString("productName"));
	     	 else
	     		newImportProduct.set("internalName", gv.getString("productName"));
	      //in future purpose we add the brandName
	     	 newImportProduct.set("brandName", gv.getString("brandName"));
	     	if(!UtilValidate.isEmpty(productName)&& UtilValidate.isNotEmpty(request.getParameter("isVirtual")))
		    	  newImportProduct.set("productName", gv.getString("productName"));
	     	 else
		     		newImportProduct.set("productName", gv.getString("productName"));
	     	
	    	  if(gv.getString("description") != null && gv.getString("description").length() > 0)
	    	  newImportProduct.set("description", gv.getString("description"));
	    	  if(gv.getString("longDescription") != null && gv.getString("longDescription").length() > 0)
	    	  newImportProduct.set("longDescription", gv.getString("longDescription"));
	    	  if(variantSet.size()==1&&gv.getString("stock") != null && gv.getString("stock").length() > 0)
	    		  newImportProduct.set("stock", gv.getDouble("stock"));
	    	  else
	    		  newImportProduct.set("stock", 0.0);  
	    	  
	    	  if(gv.getString("articleStatus") != null && gv.getString("articleStatus").length() > 0)
	    		  newImportProduct.set("articleStatus", gv.getString("articleStatus"));
	    	  if(gv.getString("extraInfo") != null && gv.getString("extraInfo").length() > 0)
	    		  newImportProduct.set("extraInfo", gv.getString("extraInfo"));
	    	  if(gv.getString("blockInd") != null && gv.getString("blockInd").length() > 0)
	    		  newImportProduct.set("blockInd", gv.getString("blockInd"));
	    	  
	    	  if(variantSet.size()==1&&gv.getString("barcode") != null && gv.getString("barcode").length() > 0)
	    	  {
	    		  String variant="";
	    		  for(int i=1;i<=virtualList.size();i++)
	    		  {
	    			 GenericValue temGv= virtualList.get(i-1);
	    			 if(i==virtualList.size())
		    			  variant=variant+temGv.getString("barcode");
		    		  else
		    		  variant=variant+temGv.getString("barcode")+",";
	    			 
	    		 
	    		  }
	    		  newImportProduct.set("barcode",variant);
	    	  }
	    	  else
	    		  newImportProduct.set("barcode", "");  
	      
	      
	    	 
	    	  newImportProduct.set("smallImageUrl","/images/products/small/"+gv.getString("productId")+".jpg" );
	    	 newImportProduct.set("largeImageUrl", "/images/products/large/"+gv.getString("productId")+".jpg");
	    	 newImportProduct.set("detailImageUrl", "/images/products/detail/"+gv.getString("productId")+".jpg");
	     
	      
	    	  if(gv.getString("defaultPrice") != null && gv.getString("defaultPrice").length() > 0)
	    	  newImportProduct.set("defaultPrice", gv.getBigDecimal("defaultPrice"));
	    	  if(gv.getString("listPrice") != null && gv.getString("listPrice").length() > 0)
	    	  newImportProduct.set("listPrice", gv.getBigDecimal("listPrice"));
	     
	     
	    	  if(gv.getString("categoryOne") != null && gv.getString("categoryOne").length() > 0)
	    	  {
	    		List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv.getString("categoryOne")), UtilMisc.toSet("productCategoryId"), null, null, false);
	    		if(!UtilValidate.isEmpty(categoryList))
	    		{
	    		GenericValue gv1=EntityUtil.getFirst(categoryList);
	    		newImportProduct.set("categoryOne", gv1.getString("productCategoryId"));
	    		}
	    		  
	    	  }  
	    	 
	    	  if(gv.getString("categoryTwo") != null && gv.getString("categoryTwo").length() > 0)
	    	  {
	    		  List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv.getString("categoryTwo")), UtilMisc.toSet("productCategoryId"), null, null, false);
		    		GenericValue gv1=EntityUtil.getFirst(categoryList);
		    		newImportProduct.set("categoryTwo", gv1.getString("productCategoryId"));
	    	  }
	    	  if(gv.getString("categoryThree") != null && gv.getString("categoryThree").length() > 0)
	    	  {
	    		  List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv.getString("categoryThree")), UtilMisc.toSet("productCategoryId"), null, null, false);
		    		GenericValue gv1=EntityUtil.getFirst(categoryList);
		    	 newImportProduct.set("categoryThree",gv1.getString("productCategoryId"));
	    	  }
	    	  if(gv.getString("sellUnit") != null && gv.getString("sellUnit").length() > 0)
	    	  newImportProduct.set("sellUnit", gv.getBigDecimal("sellUnit"));
	    	  if(gv.getString("packType") != null && gv.getString("packType").length() > 0)
	    	  newImportProduct.set("packType", gv.getString("packType"));
	    	  if(gv.getString("vatPerc") != null && gv.getString("vatPerc").length() > 0)
	    	  newImportProduct.set("vatPerc", gv.getBigDecimal("vatPerc"));
	    	  if(variantSet.size()>1)
	    	  newImportProduct.set("productFeatureCategoryId", "110000");
	      
	    	  List setList=new ArrayList(variantSet); 
	    	  //newImportProduct.set("productFeature", gv.getString("productFeature"));
	    	  if(variantSet.size()>1)
	    	  {
	      List<GenericValue> variantNameList=delegator.findList("DataImportOriginalProduct", EntityCondition.makeCondition("productId",EntityOperator.IN,setList),UtilMisc.toSet("productName"), null, null, false);
		List uniqueproductName=EntityUtil.getFieldListFromEntityList(variantNameList, "productName", true);
		String feature="";
		for(int i=1;i<=uniqueproductName.size();i++)
		{
			
			//String temp=((String) uniqueproductName.get(i-1)).substring(productName.length()+1,((String) uniqueproductName.get(i-1)).length());
			
			String uniProduct = (String)(uniqueproductName.get(i-1));
			String uniProductNew = uniProduct.toString();
			int  one =  uniProductNew.indexOf("- ");
			String temp= uniProductNew.substring(one+1);
			
			 
			
			if(i==uniqueproductName.size())
				 feature=feature+temp;
				 else
			feature=feature+temp+",";
			 
			
		}
		
		
		
		newImportProduct.set("productFeature", feature);
		 GenericValue newImportProductFeature = delegator.makeValue("DataImportProductFeature");
	     	 String exportProductFeatureSeqId = delegator.getNextSeqId("DataImportProductFeature");
	     	newImportProductFeature.set("exportFeatureCategorySeqId", exportProductFeatureSeqId);
	     	newImportProductFeature.set("productFeatureCategoryId", "110000");
	     	newImportProductFeature.set("productFeatureCategoryDescription", "");
	     	newImportProductFeature.set("productFeatureTypeId", "NET_WEIGHT");
	     	newImportProductFeature.set("productFeatureDescription",feature);
	     	
	     	  try {
		 	    	 
	 	          delegator.create(newImportProductFeature);
	 	       } catch (GenericEntityException e) {
	 	    	  Debug.logInfo("Error while importing product" + e, module);
	 	          Debug.logWarning(e.getMessage(), module);
	 	      }  
	     	 
	     	 
		
	      String variant="";
	      if(variantIds != null && variantIds.length > 0)
		{
	    	  for(int i=1;i<=variantSet.size();i++)
	    	  {
	    		  
	    		  if(i==variantSet.size())
	    			  variant=variant+setList.get(i-1);
	    		  else
	    		  variant=variant+setList.get(i-1)+",";
	    		  
	    	  
	    	  }
	    	  newImportProduct.set("variantId", variant);
	    	  
		}
	    	  
	    	  }
	    	  
	    		  
	      try {
	    	 
	          delegator.create(newImportProduct);
	       } catch (GenericEntityException e) {
	    	  Debug.logInfo("Error while importing product" + e, module);
	          Debug.logWarning(e.getMessage(), module);
	      }
	       
	     
	       if(variantSet.size()>1)
	       {
	       for(int i=0;i<variantSet.size();i++)
	       {
	    	   
	    	   List<GenericValue> variantList=delegator.findList("DataImportOriginalProduct", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,setList.get(i)), null, null, null, false);
	    	   GenericValue gv2=EntityUtil.getFirst(variantList);
	    	  
	    		  GenericValue newImportVariant = delegator.makeValue("DataImportVariantProduct");
	 	     	 String exportVariantSeqId = delegator.getNextSeqId("DataImportVariantProduct");
	 	     	newImportVariant.set("exportVariantProdSeqId", exportVariantSeqId);
	 	     	
	 	     	newImportVariant.set("productId", gv2.getString("productId"));
	 	     	
	 	     		
	 	     	
	 	     	 
	 	     	 if(gv2.getString("artGroupNo") != null && gv2.getString("artGroupNo").length() > 0)
	 	     		newImportVariant.set("artGroupNo", gv2.getString("artGroupNo"));
	 	     	 if(gv2.getString("artGroupSubNo") != null && gv2.getString("artGroupSubNo").length() > 0)
	 	     		newImportVariant.set("artGroupSubNo", gv2.getString("artGroupSubNo"));
	 	     	 if(gv2.getString("productTypeId") != null && gv2.getString("productTypeId").length() > 0)
	 	     		newImportVariant.set("productTypeId", gv2.getString("productTypeId"));
	 	     	 if(gv2.getString("productName") != null && gv2.getString("productName").length() > 0)
	 	     		newImportVariant.set("internalName", gv2.getString("productName"));
	 	      //in future purpose we add the brandName
	 	     	//newImportVariant.set("brandName", "");
	 	     	if(gv2.getString("productName") != null && gv2.getString("productName").length() > 0)
	 	     		newImportVariant.set("productName", gv2.getString("productName"));
	 	    	  if(gv2.getString("description") != null && gv2.getString("description").length() > 0)
	 	    		 newImportVariant.set("description", gv2.getString("description"));
	 	    	  if(gv2.getString("longDescription") != null && gv2.getString("longDescription").length() > 0)
	 	    		 newImportVariant.set("longDescription", gv2.getString("longDescription"));
	 	    	  if(gv2.getString("stock") != null && gv2.getString("stock").length() > 0)
	 	    		 newImportVariant.set("stock", gv2.getDouble("stock"));
	 	    	  
	 	    	  if(gv2.getString("articleStatus") != null && gv2.getString("articleStatus").length() > 0)
	 	    		 newImportVariant.set("articleStatus", gv2.getString("articleStatus"));
	 	    	  if(gv2.getString("extraInfo") != null && gv2.getString("extraInfo").length() > 0)
	 	    		 newImportVariant.set("extraInfo", gv2.getString("extraInfo"));
	 	    	  if(gv2.getString("blockInd") != null && gv2.getString("blockInd").length() > 0)
	 	    		 newImportVariant.set("blockInd", gv2.getString("blockInd"));
	 	    	  
	 	    	  if(gv2.getString("barcode") != null && gv2.getString("barcode").length() > 0)
	 	    	  {
	 	    		 String variant="";
		    		  for(int j=1;j<=variantList.size();j++)
		    		  {
		    			 GenericValue temGv= variantList.get(j-1);
		    			 if(j==variantList.size())
			    			  variant=variant+temGv.getString("barcode");
			    		  else
			    		  variant=variant+temGv.getString("barcode")+",";
		    			 
		    		 
		    		  }
		    		  newImportVariant.set("barcode",variant);
	 	    	  }
	 	    		// newImportVariant.set("barcode",gv2.getString("barcode"));
	 	    	 
	 	    	 newImportVariant.set("smallImageUrl","/images/products/small/"+gv2.getString("productId")+".jpg" );
	 	    	newImportVariant.set("largeImageUrl", "/images/products/large/"+gv2.getString("productId")+".jpg");
	 	    	newImportVariant.set("detailImageUrl","/images/products/detail/"+gv2.getString("productId")+".jpg");
	 	     
	 	      
	 	    	  if(gv2.getString("defaultPrice") != null && gv2.getString("defaultPrice").length() > 0)
	 	    		 newImportVariant.set("defaultPrice", gv2.getBigDecimal("defaultPrice"));
	 	    	  if(gv2.getString("listPrice") != null && gv2.getString("listPrice").length() > 0)
	 	    		 newImportVariant.set("listPrice", gv2.getBigDecimal("listPrice"));
	 	     
	 	     
	 	    	 /* if(gv2.getString("categoryOne") != null && gv2.getString("categoryOne").length() > 0)
	 	    	  {
	 	    		List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv2.getString("categoryOne")), UtilMisc.toSet("productCategoryId"), null, null, false);
	 	    		GenericValue gv1=EntityUtil.getFirst(categoryList);
	 	    		newImportVariant.set("categoryOne", gv1.getString("productCategoryId"));
	 	    		  
	 	    	  }  
	 	    	 
	 	    	  if(gv2.getString("categoryTwo") != null && gv2.getString("categoryTwo").length() > 0)
	 	    	  {
	 	    		  List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv2.getString("categoryTwo")), UtilMisc.toSet("productCategoryId"), null, null, false);
	 		    		GenericValue gv1=EntityUtil.getFirst(categoryList);
	 		    		newImportVariant.set("categoryTwo", gv1.getString("productCategoryId"));
	 	    	  }
	 	    	  if(gv2.getString("categoryThree") != null && gv2.getString("categoryThree").length() > 0)
	 	    	  {
	 	    		  List<GenericValue> categoryList=delegator.findList("ProductCategory", EntityCondition.makeCondition("categoryName",EntityOperator.EQUALS,gv2.getString("categoryThree")), UtilMisc.toSet("productCategoryId"), null, null, false);
	 		    		GenericValue gv1=EntityUtil.getFirst(categoryList);
	 		    		newImportVariant.set("categoryThree",gv1.getString("productCategoryId"));
	 	    	  }*/
	 	    	  if(gv2.getString("sellUnit") != null && gv2.getString("sellUnit").length() > 0)
	 	    		 newImportVariant.set("sellUnit", gv2.getBigDecimal("sellUnit"));
	 	    	  if(gv2.getString("packType") != null && gv2.getString("packType").length() > 0)
	 	    		 newImportVariant.set("packType", gv2.getString("packType"));
	 	    	  if(gv.getString("vatPerc") != null && gv2.getString("vatPerc").length() > 0)
	 	    		 newImportVariant.set("vatPerc", gv2.getBigDecimal("vatPerc"));
	 	       
	 	    	  
	 	      
	 	      try {
	 	    	 
	 	          delegator.create(newImportVariant);
	 	       } catch (GenericEntityException e) {
	 	    	  Debug.logInfo("Error while importing product" + e, module);
	 	          Debug.logWarning(e.getMessage(), module);
	 	      }  
	    		 
	    	   
	       }   
	    	   
	    	 }
	       
	       for(int i=0;i<variantSet.size();i++)
	       {
	    	   
	    	   List<GenericValue> variantList=delegator.findList("DataImportOriginalProduct", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,setList.get(i)), null, null, null, false);
	    	 
	    	   for(GenericValue gv2:variantList) 
	    	  {
	    		   gv2.set("message","success");
		 	         gv2.store();
	    	  }
	       }
	       
	       return "success";
    }
    

    public static String updateShoppingListItem(HttpServletRequest request , HttpServletResponse response)
    
    {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String shoppingListId=request.getParameter("shoppingListId");
    	String shoppingListItemSeqId=request.getParameter("shoppingListItemSeqId");
    	String quantity=request.getParameter("quantity");
    	
    	try {
			GenericValue gv=delegator.findByPrimaryKey("ShoppingListItem", UtilMisc.toMap("shoppingListId",shoppingListId,"shoppingListItemSeqId",shoppingListItemSeqId));
		if(UtilValidate.isNotEmpty(quantity) && UtilValidate.isNotEmpty(gv))
			
			gv.set("quantity", new BigDecimal(quantity));
		gv.store();
    	
    	
    	
    	
    	
    	} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    	
    	
    	
    	return "success";
    }
    
    public static Map uploadProductExcel(DispatchContext dctx, Map context) {
    	//System.out.println("calling the uploadProductExcel\n\n\n\n\n\n\n\n ");
    	Map results = null;
    	try
        {
    		String appPath= System.getProperty("ofbiz.home");
    	  String serverPath = UtilProperties.getPropertyValue("dataimport", "file.server.path");
    	  
      	  String filename = appPath+serverPath+"/importFiles/product/productExcel.xls";
      	
          WorkbookSettings ws = new WorkbookSettings();
          ws.setLocale(new Locale("en", "EN"));
          Workbook workbook = Workbook.getWorkbook(new File(filename),ws);
          Sheet s  = workbook.getSheet(0);
          readProductExcelDataSheet(s, dctx);
          workbook.close(); 
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (BiffException e)
        {
          e.printStackTrace();
        }
        results = UtilMisc.toMap("message", "success");
        return results;
    }


    private static void readProductExcelDataSheet(Sheet s, DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		Cell rowData[] = null;
		Cell rowHeading[] = null;
		int successCount = 0;
		int rows = s.getRows(); 
		int column = s.getColumns();
	 
	  for (int i = 1; i < rows ; i++) {
		  String articleNumber = "";
		  String sellPrice = "";
		  String vatPerc = "";
		  String mrpPrice = "";
		  String stock = "";
		  String qoh = "";
	      
		  rowData = s.getRow(i);
		  rowHeading = s.getRow(0);
		  
		  try {
			  	if (rowData[0].getContents().length() != 0) { 
					
					for (int j = 0; j < rowData.length; j++) {
						switch (j) {
						case 0:
							if((rowHeading[j].getContents()).equals("ART_NO"))
								articleNumber = rowData[j].getContents();
							break;
						case 1:
							if((rowHeading[j].getContents()).equals("SELL_PR"))
								sellPrice = rowData[j].getContents();
							break;
						case 2:
							if((rowHeading[j].getContents()).equals("VAT_PERC"))
								vatPerc = rowData[j].getContents();
							break;
						case 3:
							if((rowHeading[j].getContents()).equals("MRP_PRICE"))
								mrpPrice = rowData[j].getContents();
							break;
						case 4:
							if((rowHeading[j].getContents()).equals("STOCK"))
								stock = rowData[j].getContents();
							break;
						case 5:
							if((rowHeading[j].getContents()).equalsIgnoreCase("QOH"))
								qoh = rowData[j].getContents();
							break;
						}
					}
					
					GenericValue newImportProduct = delegator.makeValue("DataImportFromExcel");
					newImportProduct.set("uploadDataImportSeqId", delegator.getNextSeqId("DataImportFromExcel"));
					if(UtilValidate.isNotEmpty(articleNumber)){
						newImportProduct.set("articleNumber", articleNumber.trim());
					}
					if(UtilValidate.isNotEmpty(sellPrice)){
						newImportProduct.set("sellPrice", new java.math.BigDecimal(sellPrice.trim().toString()));
					}
					if(UtilValidate.isNotEmpty(vatPerc)){
						newImportProduct.set("vatPerc", new java.math.BigDecimal(vatPerc.trim().toString()));
					}
					if(UtilValidate.isNotEmpty(mrpPrice)){
						newImportProduct.set("mrpPrice", new java.math.BigDecimal(mrpPrice.trim().toString()));
					}
					if(UtilValidate.isNotEmpty(stock)){
						newImportProduct.set("stock", new java.lang.Double(stock.trim().toString()));
					}
					if(UtilValidate.isNotEmpty(qoh)){
						newImportProduct.set("qoh", new java.lang.Double(qoh.trim().toString()));
					}
					
					newImportProduct.set("status", "update"); 
				      
					try {
						delegator.create(newImportProduct);
						Debug.logInfo("Successfully imported product ["+articleNumber+" from row no "+ i +"].", module);
						successCount++;
					} catch (GenericEntityException e) {
				    	  Debug.logInfo("Error while importing product [ from row no "+ i +"]." + e, module);
				          Debug.logWarning(e.getMessage(), module);
					}
				}
			} catch (Exception e) {
			  Debug.logInfo("Error while importing product [ from row no "+ i +"]." + e, module);
			  continue;
	      }
	  }
	
	  Debug.logInfo("Total row imported: - ["+ successCount +"].", module);
	}
      

	public static String dailyUpload(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		String productStoreId = ProductStoreWorker.getProductStoreId(request);
		if(UtilValidate.isEmpty(productStoreId)){productStoreId = "9000";}
	
		String facilityId = ProductStoreWorker.determineSingleFacilityForStore(delegator, productStoreId);
		EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);
		List<GenericValue> productList = delegator.findList("DataImportFromExcel", null, null, null, findOptions, false);
		
		List productIds = EntityUtil.getFieldListFromEntityList(productList, "articleNumber", true);
		
		
		List<GenericValue> priceList = delegator.findList("ProductPrice", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		
		List<GenericValue> productLists = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, null, null, false);
		
		List<GenericValue> toBeStored = new ArrayList<GenericValue>();
		
    	if(UtilValidate.isNotEmpty(productList))
    	{
    		for(GenericValue temproduct:productList)
    		{
    			try{
	    			Double tem = 0.0;
	    			if(UtilValidate.isNotEmpty(priceList) && UtilValidate.isNotEmpty(temproduct.getString("mrpPrice")) && (temproduct.getDouble("mrpPrice") != 0))
	    			{
	    				
    					List<GenericValue> prdlistPriceList = EntityUtil.filterByAnd(priceList, UtilMisc.toMap("productId", temproduct.getString("articleNumber"), "productPriceTypeId", "LIST_PRICE","currencyUomId","INR"));
	    				for(GenericValue prdPrice : prdlistPriceList)
	    				{
    						if((prdPrice.getString("productPriceTypeId")).equals("LIST_PRICE")){
	    						prdPrice.set("price", temproduct.getBigDecimal("mrpPrice"));
	    						prdPrice.store();
    						}else{
    							delegator.create("ProductPrice", UtilMisc.toMap("productId", prdPrice.getString("productId"),"productPriceTypeId","LIST_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId","INR", "productStoreGroupId","_NA_", "fromDate", UtilDateTime.nowTimestamp(),"price", temproduct.getBigDecimal("mrpPrice") ));
    						}
	    				}
	    				
	    				List<GenericValue> prdDefaultPriceList = EntityUtil.filterByAnd(priceList, UtilMisc.toMap("productId", temproduct.getString("articleNumber"), "productPriceTypeId", "DEFAULT_PRICE","currencyUomId","INR"));
	    				for(GenericValue prdPrice : prdDefaultPriceList)
	    				{
    						if((prdPrice.getString("productPriceTypeId")).equals("DEFAULT_PRICE")){
	    						prdPrice.set("price", temproduct.getBigDecimal("mrpPrice"));
	    						prdPrice.store();
    						}else{
    							delegator.create("ProductPrice", UtilMisc.toMap("productId", prdPrice.getString("productId"),"productPriceTypeId","DEFAULT_PRICE", "productPricePurposeId","PURCHASE", "currencyUomId","INR", "productStoreGroupId","_NA_", "fromDate", UtilDateTime.nowTimestamp(),"price", temproduct.getBigDecimal("mrpPrice") ));
    						}
	    				}
	    			}
    			
    			
	    			BigDecimal unitPrice = temproduct.getBigDecimal("sellPrice");
					BigDecimal vatPerc = temproduct.getBigDecimal("vatPerc");
    			
					//Map inventoryMap = FastMap.newInstance();
					Map inventoryMap = new HashMap<String, Object>();
					
					if(UtilValidate.isNotEmpty(temproduct.getBigDecimal("stock")))
						inventoryMap.put("virtualAccepted", temproduct.getBigDecimal("stock"));
					if(UtilValidate.isNotEmpty(temproduct.getBigDecimal("qoh")))
						inventoryMap.put("quantityAccepted", temproduct.getBigDecimal("qoh"));
					else
						inventoryMap.put("quantityAccepted", BigDecimal.ZERO);
	    			  
					inventoryMap.put("quantityRejected", BigDecimal.ZERO);
					inventoryMap.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
					inventoryMap.put("unitCost", unitPrice);
					if(UtilValidate.isNotEmpty(temproduct.getString("sellPrice")) && (temproduct.getDouble("sellPrice")!=0)){
						unitPrice = temproduct.getBigDecimal("sellPrice");
					}
					inventoryMap.put("unitCost", unitPrice);
	    			 
					inventoryMap.put("productId", temproduct.getString("articleNumber"));
					inventoryMap.put("facilityId", facilityId);
					inventoryMap.put("comments", "CSV import");
					inventoryMap.put("userLogin", userLogin);
					inventoryMap.put("datetimeReceived", UtilDateTime.nowTimestamp());
					String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
					inventoryMap.put("currencyUomId", currencyUomId);
		              
					if(UtilValidate.isNotEmpty(temproduct.getString("vatPerc"))){
						inventoryMap.put("vatPerc", temproduct.getBigDecimal("vatPerc"));
					}else{
						inventoryMap.put("vatPerc", vatPerc);
					}
		              
					String productId = (String)inventoryMap.get("productId");
					if(UtilValidate.isEmpty(productId)) productId = temproduct.getString("articleNumber");
					  
					List<GenericValue> prdList = EntityUtil.filterByAnd(productLists,UtilMisc.toMap("productId", temproduct.getString("articleNumber")));
					GenericValue product = null;
					if(UtilValidate.isNotEmpty(prdList)) product = EntityUtil.getFirst(prdList);
					  
					//Map<String, Object> packageResp = dispatcher.runSync("receiveInventoryProduct", QOHMap);
					Map<String, Object> packageResp=null;
					if(UtilValidate.isNotEmpty(product) && UtilValidate.isEmpty(product.get("salesDiscontinuationDate")))
						try {
							///////////////////////
//							  BigDecimal quantityAccepted = BigDecimal.ONE; 
//			         	       BigDecimal virtualDiff =  temproduct.getBigDecimal("stock") ;
//			         	      BigDecimal quantityOnHandDiff =   temproduct.getBigDecimal("qoh") ;
//			         	      BigDecimal vatPercentage =  temproduct.getBigDecimal("vatPerc");
//				         	    Map QOH = dispatcher.runSync("adjustATP",inventoryMap);
//				         	   BigDecimal totalAvailableToPromise =  BigDecimal.ZERO;
//				         	  totalAvailableToPromise = totalAvailableToPromise.add(virtualDiff).add(quantityOnHandDiff);
//				         	  if(UtilValidate.isEmpty(virtualDiff))
//				         	 virtualDiff = totalAvailableToPromise;
//				         	 BigDecimal availableToPromiseDiff = totalAvailableToPromise;
//				         	 Map inventoryItemId = dispatcher.runSync("createInventoryItem", inventoryMap);
//				         	 System.out.println("\n\n\n inventoryItemId"+inventoryItemId);
//				          
//				        	inventoryMap.clear();
//				        	inventoryMap.put("inventoryItemId",inventoryItemId.get("inventoryItemId"));
//							inventoryMap.put("userLogin", userLogin);

//				         	 Map inventoryItemResult = dispatcher.runSync("balanceInventoryItems", inventoryMap);
//				         	 System.out.println("\n\n\n\n inventoryItemId"+inventoryItemResult);
				         	
 				        	
 							packageResp = dispatcher.runSync("receiveInventoryProduct", inventoryMap);
						} catch (GenericServiceException e) {
		      				// TODO Auto-generated catch block
							e.printStackTrace();
		      			}
		      			if(ServiceUtil.isSuccess(packageResp)){
		    				  if(UtilValidate.isNotEmpty(product) && UtilValidate.isNotEmpty(temproduct.getBigDecimal("qoh")) && UtilValidate.isNotEmpty(unitPrice))
		    				  {
		    					  BigDecimal atp = ProductWorker.totalAvailableATP(delegator, dispatcher, facilityId, productId, null);
		    					  product.put("invUnitCost", unitPrice);
		    					  product.put("basePrice", temproduct.getBigDecimal("mrpPrice"));
		    					  product.put("inventoryAtp", atp);
		    					  toBeStored.add(product);
		    				  }
		    				  if(UtilValidate.isNotEmpty(product) && UtilValidate.isEmpty(product.getBigDecimal("invUnitCost")))
		    				  {
		    					  BigDecimal atp = ProductWorker.totalAvailableATP(delegator, dispatcher, facilityId, productId, null);
		    					  product.put("invUnitCost", unitPrice);
		    					  product.put("basePrice", temproduct.getBigDecimal("mrpPrice"));
		    					  product.put("inventoryAtp", atp);
		    					  toBeStored.add(product);
		    				  }
		    				  delegator.removeValue(temproduct);
		    			  }
	    			}catch(Exception e){
	    				temproduct.set("message","Error in updating Product Price");
			    		temproduct.store();
	    				e.printStackTrace();
	    			}
    			}//for
    			
    			
    			if(UtilValidate.isNotEmpty(toBeStored))
    				delegator.storeAll(toBeStored);
    		}
    	return "success";
	}
    
    
    public static String checkingExisingEmail(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    	
    	PrintWriter out=null;
    	HttpSession session=request.getSession();
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String email=request.getParameter("email");
    	if(UtilValidate.isNotEmpty(email))
    	{
    		session.setAttribute("customerMailId",email);
    		session.setMaxInactiveInterval(3600);
    		List<EntityCondition> condn=new ArrayList<EntityCondition>();
    		condn.add(EntityCondition.makeCondition("pinCode", EntityOperator.EQUALS,"123launch"));
    		condn.add(EntityCondition.makeCondition("emailId", EntityOperator.EQUALS,email));
    		List<GenericValue> emailList=delegator.findList("NonServiceZone", EntityCondition.makeCondition(condn, EntityOperator.AND), null, null, null, false);
    		if(UtilValidate.isNotEmpty(emailList))
    			out.print(true);
    		else
    			out.print(false);
    	}
    	
    	
    	return "success";
    	
    	
    	
    }
    
    
    
}
