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
package org.ofbiz.product.promo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
//import javolution.util.FastList;
//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Promotions Services
 */
public class PromoServices {

    public final static String module = PromoServices.class.getName();
    public static final String resource = "ProductUiLabels";
    
    public static Map<String, Object> createProductPromoCodeSet(DispatchContext dctx, Map<String, ? extends Object> context) {
        //Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Long quantity = (Long) context.get("quantity");
        Locale locale = (Locale) context.get("locale");
        //Long useLimitPerCode = (Long) context.get("useLimitPerCode");
        //Long useLimitPerCustomer = (Long) context.get("useLimitPerCustomer");
        //GenericValue promoItem = null;
        //GenericValue newItem = null;

        StringBuilder bankOfNumbers = new StringBuilder();
        for (long i = 0; i < quantity.longValue(); i++) {
            Map<String, Object> createProductPromoCodeMap = null;
            try {
                createProductPromoCodeMap = dispatcher.runSync("createProductPromoCode", dctx.makeValidContext("createProductPromoCode", "IN", context));
            } catch (GenericServiceException err) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPromoCodeCannotBeCreated",  locale), null, null, createProductPromoCodeMap);
            }
            if (ServiceUtil.isError(createProductPromoCodeMap)) {
                // what to do here? try again?
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "ProductPromoCodeCannotBeCreated",  locale), null, null, createProductPromoCodeMap);
            }
            bankOfNumbers.append((String) createProductPromoCodeMap.get("productPromoCodeId"));
            bankOfNumbers.append("<br/>");
        }

        return ServiceUtil.returnSuccess(bankOfNumbers.toString());
    }

    public static Map<String, Object> purgeOldStoreAutoPromos(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        Locale locale = (Locale) context.get("locale");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        List<EntityCondition> condList = new ArrayList<EntityCondition>();//FastList.newInstance();
        if (UtilValidate.isEmpty(productStoreId)) {
            condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
        }
        condList.add(EntityCondition.makeCondition("userEntered", EntityOperator.EQUALS, "Y"));
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null));
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, nowTimestamp));
        EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

        try {
            EntityListIterator eli = delegator.find("ProductStorePromoAndAppl", cond, null, null, null, null);
            GenericValue productStorePromoAndAppl = null;
            while ((productStorePromoAndAppl = eli.next()) != null) {
                GenericValue productStorePromo = delegator.makeValue("ProductStorePromoAppl");
                productStorePromo.setAllFields(productStorePromoAndAppl, true, null, null);
                productStorePromo.remove();
            }
            eli.close();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error removing expired ProductStorePromo records: " + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeCannotBeRemoved", UtilMisc.toMap("errorString", e.toString()), locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> importPromoCodesFromFile(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        // check the uploaded file
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        if (fileBytes == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportUploadedFileNotValid", locale));
        }

        String encoding = System.getProperty("file.encoding");
        String file = Charset.forName(encoding).decode(fileBytes).toString();
        // get the createProductPromoCode Model
        ModelService promoModel;
        try {
            promoModel = dispatcher.getDispatchContext().getModelService("createProductPromoCode");
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // make a temp context for invocations
        Map<String, Object> invokeCtx = promoModel.makeValid(context, ModelService.IN_PARAM);

        // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(file));
        List<Object> errors =new ArrayList<Object>();// FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                // check to see if we should ignore this line
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (line.length() > 0 && line.length() <= 20) {
                        // valid promo code
                        Map<String, Object> inContext = new HashMap<String, Object>(); //FastMap.newInstance();
                        inContext.putAll(invokeCtx);
                        inContext.put("productPromoCodeId", line);
                        Map<String, Object> result = dispatcher.runSync("createProductPromoCode", inContext);
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + UtilProperties.getMessage(resource, "ProductPromoCodeInvalidCode", locale));
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportEmptyFile", locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> importPromoCodeEmailsFromFile(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String productPromoCodeId = (String) context.get("productPromoCodeId");
        byte[] wrapper = (byte[]) context.get("uploadedFile");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        if (wrapper == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportUploadedFileNotValid", locale));
        }

        // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(new String(wrapper)));
        List<Object> errors =new ArrayList<Object>();// FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (UtilValidate.isEmail(line)) {
                        // valid email address
                        Map<String, Object> result = dispatcher.runSync("createProductPromoCodeEmail", UtilMisc.<String, Object>toMap("productPromoCodeId",
                                productPromoCodeId, "emailAddress", line, "userLogin", userLogin));
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + ": is not a valid email address");
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportEmptyFile", locale));
        }

        return ServiceUtil.returnSuccess();
    }
}
