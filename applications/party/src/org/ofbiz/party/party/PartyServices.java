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

package org.ofbiz.party.party;

import java.sql.Timestamp;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import javolution.util.FastList;
//import javolution.util.FastMap;


import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.CommonWorkers;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
//import org.ofbiz.order.order.OrderServices;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;




/**
 * Services for Party/Person/Group maintenance
 */
public class PartyServices {

    public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyUiLabels";
    public static final String resourceError = "PartyErrorUiLabels";

    /**
     * Deletes a Party.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> deleteParty(DispatchContext ctx, Map<String, ? extends Object> context) {

        Locale locale = (Locale) context.get("locale");

        /*
         * pretty serious operation, would delete:
         * - Party
         * - PartyRole
         * - PartyRelationship: from and to
         * - PartyDataObject
         * - Person or PartyGroup
         * - PartyContactMech, but not ContactMech itself
         * - PartyContactMechPurpose
         * - Order?
         *
         * We may want to not allow this, but rather have some sort of delete flag for it if it's REALLY that big of a deal...
         */

        return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                "partyservices.cannot_delete_party_not_implemented", locale));
    }

    /**
     * Creates a Person.
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPerson(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        Timestamp now = UtilDateTime.nowTimestamp();
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();//FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");
        String description = (String) context.get("description");

        String partyTypeId = (String) context.get("partyTypeId");
        
        if(UtilValidate.isEmpty(partyTypeId)) partyTypeId = "PERSON";

        // if specified partyId starts with a number, return an error
        if (UtilValidate.isNotEmpty(partyId) && partyId.matches("\\d+")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "party.id_is_digit", locale));
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "party.id_generation_failure", locale));
            }
        }

        // check to see if party object exists, if so make sure it is PERSON type party
        GenericValue party = null;

        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party != null) {
            if (!partyTypeId.equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.create.party_exists_not_person_type", locale)); 
            }
        } else {
            // create a party if one doesn't already exist with an initial status from the input
            String statusId = (String) context.get("statusId");
            if (statusId == null) {
                statusId = "PARTY_ENABLED";
            }
            Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", partyTypeId, "description", description, "createdDate", now, "lastModifiedDate", now, "statusId", statusId);
            String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
            if (!UtilValidate.isEmpty(preferredCurrencyUomId)) {
                newPartyMap.put("preferredCurrencyUomId", preferredCurrencyUomId);
            }
            String externalId = (String) context.get("externalId");
            if (!UtilValidate.isEmpty(externalId)) {
                newPartyMap.put("externalId", externalId);
            }
            if (userLogin != null) {
                newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            }
            party = delegator.makeValue("Party", newPartyMap);
            toBeStored.add(party);

            // create the status history
            GenericValue statusRec = delegator.makeValue("PartyStatus",
                    UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
            toBeStored.add(statusRec);
        }

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (person != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.person_exists", locale)); 
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        person.setNonPKFields(context);
        toBeStored.add(person);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.db_error", new Object[] { e.getMessage() }, locale)); 
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Sets a party status.
     * <b>security check</b>: the status change must be defined in StatusValidChange.
     */
    public static Map<String, Object> setPartyStatus(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        String partyId = (String) context.get("partyId");
        String statusId = (String) context.get("statusId");
        Timestamp statusDate = (Timestamp) context.get("statusDate");
        if (statusDate == null) {
            statusDate = UtilDateTime.nowTimestamp();
        }

        try {
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));

            if (party.get("statusId") == null) { // old records
                party.set("statusId", "PARTY_ENABLED");
            }

            String oldStatusId = party.getString("statusId");
            if (!party.getString("statusId").equals(statusId)) {

                // check that status is defined as a valid change
                GenericValue statusValidChange = delegator.findByPrimaryKey("StatusValidChange", UtilMisc.toMap("statusId", party.getString("statusId"), "statusIdTo", statusId));
                if (statusValidChange == null) {
                    String errorMsg = "Cannot change party status from " + party.getString("statusId") + " to " + statusId;
                    Debug.logWarning(errorMsg, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                            "PartyStatusCannotBeChanged", 
                            UtilMisc.toMap("partyFromStatusId", party.getString("statusId"), 
                            "partyToStatusId", statusId), locale)); 
                }

                party.set("statusId", statusId);
                party.store();

                // record this status change in PartyStatus table
                GenericValue partyStatus = delegator.makeValue("PartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", statusDate));
                partyStatus.create();

                // disable all userlogins for this user when the new status is disabled
                if (("PARTY_DISABLED").equals(statusId)) {
                    List <GenericValue> userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
                    for(GenericValue userLogin : userLogins) {
                        if (!"N".equals(userLogin.getString("enabled"))) {
                            userLogin.set("enabled", "N");
                            userLogin.set("disabledDateTime", UtilDateTime.nowTimestamp());
                            userLogin.store();
                        }
                    }
                }
            }

            Map<String, Object> results = ServiceUtil.returnSuccess();
            results.put("oldStatusId", oldStatusId);
            return results;
        } catch (GenericEntityException e) {
            Debug.logError(e, e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }
    }

    /**
     * Updates a Person.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> updatePerson(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        String partyId = getPartyId(context);
        if (UtilValidate.isEmpty(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(ServiceUtil.resource, 
                    "serviceUtil.party_id_missing", locale));
        }

        GenericValue person = null;
        GenericValue party = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.read_failure", new Object[] { e.getMessage() }, locale));
        }

        if (person == null || party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.not_found", locale));
        }

        // update status by separate service
        String oldStatusId = party.getString("statusId");
        if (party.get("statusId") == null) { // old records
            party.set("statusId", "PARTY_ENABLED");
        }

        person.setNonPKFields(context);
        party.setNonPKFields(context);

        party.set("statusId", oldStatusId);
        try {
            person.store();
            party.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        if (UtilValidate.isNotEmpty(context.get("statusId")) && !context.get("statusId").equals(oldStatusId)) {
            try {
                dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "userLogin", context.get("userLogin")));
            } catch (GenericServiceException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.update.write_failure", new Object[] { e.getMessage() }, locale));
            }
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage(resourceError, "person.update.success", locale));
        return result;
    }

    /**
     * Creates a PartyGroup.
     * If no partyId is specified a numeric partyId is retrieved from the Party sequence.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "partyservices.could_not_create_party_group_generation_failure", locale));
            }
        } else {
            // if specified partyId starts with a number, return an error
            if (partyId.matches("\\d+")) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "partyservices.could_not_create_party_ID_digit", locale));
            }
        }

        try {
            // check to see if party object exists, if so make sure it is PARTY_GROUP type party
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            GenericValue partyGroupPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"));

            if (partyGroupPartyType == null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "partyservices.partyservices.party_type_not_found_in_database_cannot_create_party_group", locale));
            }

            if (party != null) {
                GenericValue partyType = party.getRelatedOneCache("PartyType");

                if (!EntityTypeUtil.isType(partyType, partyGroupPartyType)) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                            "partyservices.partyservices.cannot_create_party_group_already_exists_not_PARTY_GROUP_type", locale));
                }
            } else {
                // create a party if one doesn't already exist
                String partyTypeId = "PARTY_GROUP";

                if (UtilValidate.isNotEmpty(context.get("partyTypeId"))) {
                    GenericValue desiredPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")));
                    if (desiredPartyType != null && EntityTypeUtil.isType(desiredPartyType, partyGroupPartyType)) {
                        partyTypeId = desiredPartyType.getString("partyTypeId");
                    } else {
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                "PartyPartyTypeIdNotFound", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")), locale));
                    }
                }

                Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", partyTypeId, "createdDate", now, "lastModifiedDate", now);
                if (userLogin != null) {
                    newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                    newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                }

                String statusId = (String) context.get("statusId");
                party = delegator.makeValue("Party", newPartyMap);
                party.setNonPKFields(context);

                if (statusId == null) {
                    statusId = "PARTY_ENABLED";
                }
                party.set("statusId", statusId);
                party.create();

                // create the status history
                GenericValue partyStat = delegator.makeValue("PartyStatus",
                        UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
                partyStat.create();
            }

            GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
            if (partyGroup != null) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "partyservices.cannot_create_party_group_already_exists", locale));
            }

            partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId));
            partyGroup.setNonPKFields(context);
            partyGroup.create();

        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.data_source_error_adding_party_group", 
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates a PartyGroup.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> updatePartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        String partyId = getPartyId(context);
        if (UtilValidate.isEmpty(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(ServiceUtil.resource, 
                    "serviceUtil.party_id_missing", locale));
        }

        GenericValue partyGroup = null;
        GenericValue party = null;

        try {
            partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_party_information_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }

        if (partyGroup == null || party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_party_information_not_found", locale));
        }


        // update status by separate service
        String oldStatusId = party.getString("statusId");
        partyGroup.setNonPKFields(context);
        party.setNonPKFields(context);
        party.set("statusId", oldStatusId);

        try {
            partyGroup.store();
            party.store();
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_party_information_write",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }

        if (UtilValidate.isNotEmpty(context.get("statusId")) && !context.get("statusId").equals(oldStatusId)) {
            try {
                dispatcher.runSync("setPartyStatus", UtilMisc.toMap("partyId", partyId, "statusId", context.get("statusId"), "userLogin", context.get("userLogin")));
            } catch (GenericServiceException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.update.write_failure", new Object[] { e.getMessage() }, locale));
            }
        }

        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Create an Affiliate entity.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createAffiliate(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = getPartyId(context);

        // if specified partyId starts with a number, return an error
        if (UtilValidate.isNotEmpty(partyId) && partyId.matches("\\d+")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.cannot_create_affiliate_digit", locale));
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "partyservices.cannot_create_affiliate_generation_failure", locale));
            }
        }

        // check to see if party object exists, if so make sure it is AFFILIATE type party
        GenericValue party = null;

        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.cannot_create_affiliate_no_party_entity", locale));
        }

        GenericValue affiliate = null;

        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (affiliate != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.cannot_create_affiliate_ID_already_exists", locale));
        }

        affiliate = delegator.makeValue("Affiliate", UtilMisc.toMap("partyId", partyId));
        affiliate.setNonPKFields(context);
        affiliate.set("dateTimeCreated", now, false);

        try {
            delegator.create(affiliate);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_add_affiliate_info_write",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Updates an Affiliate.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> updateAffiliate(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");

        String partyId = getPartyId(context);
        if (UtilValidate.isEmpty(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(ServiceUtil.resource, 
                    "serviceUtil.party_id_missing", locale));
        }

        GenericValue affiliate = null;

        try {
            affiliate = delegator.findByPrimaryKey("Affiliate", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_affiliate_information_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }

        if (affiliate == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_affiliate_information_not_found", locale));
        }

        affiliate.setNonPKFields(context);

        try {
            affiliate.store();
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "partyservices.could_not_update_affiliate_information_write",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * Add a PartyNote.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPartyNote(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String noteString = (String) context.get("note");
        String partyId = (String) context.get("partyId");
        String noteId = (String) context.get("noteId");
        String noteName = (String) context.get("noteName");
        Locale locale = (Locale) context.get("locale");
        //Map noteCtx = UtilMisc.toMap("note", noteString, "userLogin", userLogin);

        //Make sure the note Id actually exists if one is passed to avoid a foreign key error below
        if (noteId != null) {
            try {
                GenericValue value = delegator.findByPrimaryKey("NoteData", UtilMisc.toMap("noteId", noteId));
                if (value == null) {
                    Debug.logError("ERROR: Note id does not exist for : " + noteId + ", autogenerating." , module);
                    noteId = null;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Note id does not exist for : " + noteId + ", autogenerating." , module);
                noteId = null;
            }
        }

        // if no noteId is specified, then create and associate the note with the userLogin
        if (noteId == null) {
            Map<String, Object> noteRes = null;
            try {
                noteRes = dispatcher.runSync("createNote", UtilMisc.toMap("partyId", userLogin.getString("partyId"),
                         "note", noteString, "userLogin", userLogin, "locale", locale, "noteName", noteName));
            } catch (GenericServiceException e) {
                Debug.logError(e, e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "PartyNoteCreationError", UtilMisc.toMap("errorString", e.getMessage()), locale));
            }

            if (noteRes.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
                return noteRes;

            noteId = (String) noteRes.get("noteId");

            if (UtilValidate.isEmpty(noteId)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                        "partyservices.problem_creating_note_no_noteId_returned", locale));
            }
        }
        result.put("noteId", noteId);

        // Set the party info
        try {
            Map<String, String> fields = UtilMisc.toMap("partyId", partyId, "noteId", noteId);
            GenericValue v = delegator.makeValue("PartyNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError(ee, module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resourceError,
                    "partyservices.problem_associating_note_with_party", 
                    UtilMisc.toMap("errMessage", ee.getMessage()), locale));
        }
        return result;
    }

    /**
     * Get the party object(s) from an e-mail address
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> getPartyFromExactEmail(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Collection<Map<String, GenericValue>> parties = new ArrayList<Map<String, GenericValue>>();//FastList.newInstance();
        String email = (String) context.get("email");
        Locale locale = (Locale) context.get("locale");

        if (email.length() == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.required_parameter_email_cannot_be_empty", locale));
        }

        try {
            EntityExpr ee = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.EQUALS, EntityFunction.UPPER(email.toUpperCase()));
            List<GenericValue> c = EntityUtil.filterByDate(delegator.findList("PartyAndContactMech", ee, null, UtilMisc.toList("infoString"), null, false), true);

            if (Debug.verboseOn()) Debug.logVerbose("List: " + c, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromEmail number found: " + c.size(), module);
            if (c != null) {
                for (GenericValue pacm: c) {
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", pacm.get("partyId"), "partyTypeId", pacm.get("partyTypeId")));

                    parties.add(UtilMisc.<String, GenericValue>toMap("party", party));
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (parties.size() > 0)
            result.put("parties", parties);
        return result;
    }

    public static Map<String, Object> getPartyFromEmail(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Collection<Map<String, GenericValue>> parties = new ArrayList<Map<String, GenericValue>>();//FastList.newInstance();
        String email = (String) context.get("email");
        Locale locale = (Locale) context.get("locale");

        if (email.length() == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.required_parameter_email_cannot_be_empty", locale));
        }

        try {
            EntityExpr ee = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.LIKE, EntityFunction.UPPER(("%" + email.toUpperCase()) + "%"));
            List<GenericValue> c = EntityUtil.filterByDate(delegator.findList("PartyAndContactMech", ee, null, UtilMisc.toList("infoString"), null, false), true);

            if (Debug.verboseOn()) Debug.logVerbose("List: " + c, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromEmail number found: " + c.size(), module);
            if (c != null) {
                for (GenericValue pacm: c) {
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", pacm.get("partyId"), "partyTypeId", pacm.get("partyTypeId")));

                    parties.add(UtilMisc.<String, GenericValue>toMap("party", party));
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (parties.size() > 0)
            result.put("parties", parties);
        return result;
    }

    /**
     * Get the party object(s) from a user login ID
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> getPartyFromUserLogin(DispatchContext dctx, Map<String, ? extends Object> context) {
        Debug.logWarning("Running the getPartyFromUserLogin Service...", module);
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Collection<Map<String, GenericValue>> parties = new ArrayList<Map<String, GenericValue>>();//FastList.newInstance();
        String userLoginId = (String) context.get("userLoginId");
        Locale locale = (Locale) context.get("locale");

        if (userLoginId.length() == 0)
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyCannotGetUserLoginFromParty", locale));

        try {
            EntityExpr ee = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + userLoginId.toUpperCase() + "%"));
            Collection<GenericValue> ulc = delegator.findList("PartyAndUserLogin", ee, null, UtilMisc.toList("userLoginId"), null, false);

            if (Debug.verboseOn()) Debug.logVerbose("Collection: " + ulc, module);
            if (Debug.infoOn()) Debug.logInfo("PartyFromUserLogin number found: " + ulc.size(), module);
            if (ulc != null) {
                for (GenericValue ul: ulc) {
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", ul.get("partyId"), "partyTypeId", ul.get("partyTypeId")));

                    parties.add(UtilMisc.<String, GenericValue>toMap("party", party));
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read", 
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    /**
     * Get the party object(s) from person information
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> getPartyFromPerson(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Collection<Map<String, GenericValue>> parties = new ArrayList<Map<String, GenericValue>>();//FastList.newInstance();
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        Locale locale = (Locale) context.get("locale");

        if (firstName == null) {
            firstName = "";
        }
        if (lastName == null) {
            lastName = "";
        }
        if (firstName.length() == 0 && lastName.length() == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.both_names_cannot_be_empty", locale));
        }

        try {
            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%")),
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + lastName.toUpperCase() + "%")));
            Collection<GenericValue> pc = delegator.findList("Person", ecl, null, UtilMisc.toList("lastName", "firstName", "partyId"), null, false);

            if (Debug.infoOn()) Debug.logInfo("PartyFromPerson number found: " + pc.size(), module);
            if (pc != null) {
                for (GenericValue person: pc) {
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", person.get("partyId"), "partyTypeId", "PERSON"));

                    parties.add(UtilMisc.<String, GenericValue>toMap("person", person, "party", party));
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    /**
     * Get the party object(s) from party group name.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> getPartyFromPartyGroup(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Collection<Map<String, GenericValue>> parties = new ArrayList<Map<String, GenericValue>>();//FastList.newInstance();
        String groupName = (String) context.get("groupName");
        Locale locale = (Locale) context.get("locale");

        if (groupName.length() == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyCannotGetPartyFromPartyGroup", locale));
        }

        try {
            EntityExpr ee = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + groupName.toUpperCase() + "%"));
            Collection<GenericValue> pc = delegator.findList("PartyGroup", ee, null, UtilMisc.toList("groupName", "partyId"), null, false);

            if (Debug.infoOn()) Debug.logInfo("PartyFromGroup number found: " + pc.size(), module);
            if (pc != null) {
                for (GenericValue group: pc) {
                    GenericValue party = delegator.makeValue("Party", UtilMisc.toMap("partyId", group.get("partyId"), "partyTypeId", "PARTY_GROUP"));

                    parties.add(UtilMisc.<String, GenericValue>toMap("partyGroup", group, "party", party));
                }
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (parties.size() > 0) {
            result.put("parties", parties);
        }
        return result;
    }

    public static Map<String, Object> getPerson(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKeyCache("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                    "partyservices.cannot_get_party_entities_read",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (person != null) {
            result.put("lookupPerson", person);
        }
        return result;
    }

    public static Map<String, Object> createRoleType(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        GenericValue roleType = null;

        try {
            roleType = delegator.makeValue("RoleType");
            roleType.setPKFields(context);
            roleType.setNonPKFields(context);
            roleType = delegator.create(roleType);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyCannotCreateRoleTypeEntity",
                    UtilMisc.toMap("errMessage", e.getMessage()), locale));
        }
        if (roleType != null) {
            result.put("roleType", roleType);
        }
        return result;
    }

    public static Map<String, Object> createPartyDataSource(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        
        // input data
        String partyId = (String) context.get("partyId");
        String dataSourceId = (String) context.get("dataSourceId");
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        if (fromDate == null) fromDate = UtilDateTime.nowTimestamp();

        try {
            // validate the existance of party and dataSource
            GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            GenericValue dataSource = delegator.findByPrimaryKey("DataSource", UtilMisc.toMap("dataSourceId", dataSourceId));
            if (party == null || dataSource == null) {
                List<String> errorList = UtilMisc.toList(UtilProperties.getMessage(resource, 
                        "PartyCannotCreatePartyDataSource", locale));
                if (party == null) {
                    errorList.add(UtilProperties.getMessage(resource, 
                            "PartyNoPartyFoundWithPartyId", locale) + partyId);
                }
                if (dataSource == null) {
                    errorList.add(UtilProperties.getMessage(resource, 
                            "PartyNoPartyWithDataSourceId",
                            UtilMisc.toMap("dataSourceId", dataSourceId), locale));
                }
                return ServiceUtil.returnError(errorList);
            }

            // create the PartyDataSource
            GenericValue partyDataSource = delegator.makeValue("PartyDataSource", UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "fromDate", fromDate));
            partyDataSource.create();

        } catch (GenericEntityException e) {
            Debug.logError(e, e.getMessage(), module);
            return ServiceUtil.returnError(e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> findParty(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        
        String extInfo = (String) context.get("extInfo");

        // get the role types
        try {
            List<GenericValue> roleTypes = delegator.findList("RoleType", null, null, UtilMisc.toList("description"), null, false);
            result.put("roleTypes", roleTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up RoleTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupRoleTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current role type
        String roleTypeId;
        try {
            roleTypeId = (String) context.get("roleTypeId");
            if (UtilValidate.isNotEmpty(roleTypeId)) {
                GenericValue currentRole = delegator.findByPrimaryKeyCache("RoleType", UtilMisc.toMap("roleTypeId", roleTypeId));
                result.put("currentRole", currentRole);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current RoleType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupRoleTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        //get party types
        try {
            List<GenericValue> partyTypes = delegator.findList("PartyType", null, null, UtilMisc.toList("description"), null, false);
            result.put("partyTypes", partyTypes);
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up PartyTypes: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupPartyTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current party type
        String partyTypeId;
        try {
            partyTypeId = (String) context.get("partyTypeId");
            if (UtilValidate.isNotEmpty(partyTypeId)) {
                GenericValue currentPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", partyTypeId));
                result.put("currentPartyType", currentPartyType);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current PartyType: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupPartyTypeError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // current state
        String stateProvinceGeoId;
        try {
            stateProvinceGeoId = (String) context.get("stateProvinceGeoId");
            if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
                GenericValue currentStateGeo = delegator.findByPrimaryKeyCache("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId));
                result.put("currentStateGeo", currentStateGeo);
            }
        } catch (GenericEntityException e) {
            String errMsg = "Error looking up current stateProvinceGeo: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyLookupStateProvinceGeoError",
                    UtilMisc.toMap("errMessage", e.toString()), locale));
        }

        // set the page parameters
        int viewIndex = 0;
        try {
            viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
        } catch (Exception e) {
            viewIndex = 0;
        }
        result.put("viewIndex", Integer.valueOf(viewIndex));

        int viewSize = 20;
        try {
            viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
        } catch (Exception e) {
            viewSize = 20;
        }
        result.put("viewSize", Integer.valueOf(viewSize));

        // get the lookup flag
        String lookupFlag = (String) context.get("lookupFlag");

        // blank param list
        String paramList = "";

        List<GenericValue> partyList = null;
        int partyListSize = 0;
        int lowIndex = 0;
        int highIndex = 0;

        if ("Y".equals(lookupFlag)) {
            String showAll = (context.get("showAll") != null ? (String) context.get("showAll") : "N");
            paramList = paramList + "&lookupFlag=" + lookupFlag + "&showAll=" + showAll + "&extInfo=" + extInfo;

            // create the dynamic view entity
            DynamicViewEntity dynamicView = new DynamicViewEntity();

            // default view settings
            dynamicView.addMemberEntity("PT", "Party");
            dynamicView.addAlias("PT", "partyId");
            dynamicView.addAlias("PT", "statusId");
            dynamicView.addAlias("PT", "partyTypeId");
            dynamicView.addRelation("one-nofk", "", "PartyType", ModelKeyMap.makeKeyMapList("partyTypeId"));
            dynamicView.addRelation("many", "", "UserLogin", ModelKeyMap.makeKeyMapList("partyId"));

            // define the main condition & expression list
            List<EntityCondition> andExprs = new ArrayList<EntityCondition>();//FastList.newInstance();
            EntityCondition mainCond = null;

            List<String> orderBy = new ArrayList<String>();//FastList.newInstance();
            List<String> fieldsToSelect = new ArrayList<String>();//FastList.newInstance();
            // fields we need to select; will be used to set distinct
            fieldsToSelect.add("partyId");
            fieldsToSelect.add("statusId");
            fieldsToSelect.add("partyTypeId");

            // filter on parties that have relationship with logged in user
            String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
            if (UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
                // add relation to view
                dynamicView.addMemberEntity("PRSHP", "PartyRelationship");
                dynamicView.addAlias("PRSHP", "partyIdTo");
                dynamicView.addAlias("PRSHP", "partyRelationshipTypeId");
                dynamicView.addViewLink("PT", "PRSHP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId", "partyIdTo"));
                List<String> ownerPartyIds = UtilGenerics.cast(context.get("ownerPartyIds"));
                EntityCondition relationshipCond = null;
                if (UtilValidate.isEmpty(ownerPartyIds)) {
                    String partyIdFrom = userLogin.getString("partyId");
                    paramList = paramList + "&partyIdFrom=" + partyIdFrom;
                    relationshipCond = EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyIdFrom"), EntityOperator.EQUALS, EntityFunction.UPPER(partyIdFrom));
                } else {
                    relationshipCond = EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, ownerPartyIds);
                }
                dynamicView.addAlias("PRSHP", "partyIdFrom");
                // add the expr
                andExprs.add(EntityCondition.makeCondition(
                        relationshipCond, EntityOperator.AND,
                        EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyRelationshipTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(partyRelationshipTypeId))));
                fieldsToSelect.add("partyIdTo");
            }

            // get the params
            String partyId = (String) context.get("partyId");
            String statusId = (String) context.get("statusId");
            String userLoginId = (String) context.get("userLoginId");
            String firstName = (String) context.get("firstName");
            String lastName = (String) context.get("lastName");
            String groupName = (String) context.get("groupName");

            if (!"Y".equals(showAll)) {
                // check for a partyId
                if (UtilValidate.isNotEmpty(partyId)) {
                    paramList = paramList + "&partyId=" + partyId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyId+"%")));
                }

                // now the statusId - send ANY for all statuses; leave null for just enabled; or pass a specific status
                if (statusId != null) {
                    paramList = paramList + "&statusId=" + statusId;
                    if (!"ANY".equalsIgnoreCase(statusId)) {
                        andExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
                    }
                } else {
                    // NOTE: _must_ explicitly allow null as it is not included in a not equal in many databases... odd but true
                    andExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null), EntityOperator.OR, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED")));
                }
                // check for partyTypeId
                if (partyTypeId != null && !"ANY".equals(partyTypeId)) {
                    paramList = paramList + "&partyTypeId=" + partyTypeId;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyTypeId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+partyTypeId+"%")));
                }

                // ----
                // UserLogin Fields
                // ----

                // filter on user login
                if (UtilValidate.isNotEmpty(userLoginId)) {
                    paramList = paramList + "&userLoginId=" + userLoginId;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("UL", "UserLogin");
                    dynamicView.addAlias("UL", "userLoginId");
                    dynamicView.addViewLink("PT", "UL", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityOperator.LIKE, EntityFunction.UPPER("%"+userLoginId+"%")));

                    fieldsToSelect.add("userLoginId");
                }

                // ----
                // PartyGroup Fields
                // ----

                // filter on groupName
                if (UtilValidate.isNotEmpty(groupName)) {
                    paramList = paramList + "&groupName=" + groupName;

                    // modify the dynamic view
                    dynamicView.addMemberEntity("PG", "PartyGroup");
                    dynamicView.addAlias("PG", "groupName");
                    dynamicView.addViewLink("PT", "PG", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+groupName+"%")));

                    fieldsToSelect.add("groupName");
                }

                // ----
                // Person Fields
                // ----

                // modify the dynamic view
                if (UtilValidate.isNotEmpty(firstName) || UtilValidate.isNotEmpty(lastName)) {
                    dynamicView.addMemberEntity("PE", "Person");
                    dynamicView.addAlias("PE", "firstName");
                    dynamicView.addAlias("PE", "lastName");
                    dynamicView.addViewLink("PT", "PE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    fieldsToSelect.add("firstName");
                    fieldsToSelect.add("lastName");
                    orderBy.add("lastName");
                    orderBy.add("firstName");
                }

                // filter on firstName
                if (UtilValidate.isNotEmpty(firstName)) {
                    paramList = paramList + "&firstName=" + firstName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+firstName+"%")));
                }

                // filter on lastName
                if (UtilValidate.isNotEmpty(lastName)) {
                    paramList = paramList + "&lastName=" + lastName;
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+lastName+"%")));
                }

                // ----
                // RoleType Fields
                // ----

                // filter on role member
                if (roleTypeId != null && !"ANY".equals(roleTypeId)) {
                    paramList = paramList + "&roleTypeId=" + roleTypeId;

                    // add role to view
                    dynamicView.addMemberEntity("PR", "PartyRole");
                    dynamicView.addAlias("PR", "roleTypeId");
                    dynamicView.addViewLink("PT", "PR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));

                    // add the expr
                    andExprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));

                    fieldsToSelect.add("roleTypeId");
                }

                // ----
                // InventoryItem Fields
                // ----

                // filter on inventory item's fields
                String inventoryItemId = (String) context.get("inventoryItemId");
                String serialNumber = (String) context.get("serialNumber");
                String softIdentifier = (String) context.get("softIdentifier");
                if (UtilValidate.isNotEmpty(inventoryItemId) ||
                    UtilValidate.isNotEmpty(serialNumber) ||
                    UtilValidate.isNotEmpty(softIdentifier)) {

                    // add role to view
                    dynamicView.addMemberEntity("II", "InventoryItem");
                    dynamicView.addAlias("II", "ownerPartyId");
                    dynamicView.addViewLink("PT", "II", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId", "ownerPartyId"));
                }
                if (UtilValidate.isNotEmpty(inventoryItemId)) {
                    paramList = paramList + "&inventoryItemId=" + inventoryItemId;
                    dynamicView.addAlias("II", "inventoryItemId");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("inventoryItemId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + inventoryItemId + "%")));
                    fieldsToSelect.add("inventoryItemId");
                }
                if (UtilValidate.isNotEmpty(serialNumber)) {
                    paramList = paramList + "&serialNumber=" + serialNumber;
                    dynamicView.addAlias("II", "serialNumber");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("serialNumber"), EntityOperator.LIKE, EntityFunction.UPPER("%" + serialNumber + "%")));
                    fieldsToSelect.add("serialNumber");
                }
                if (UtilValidate.isNotEmpty(softIdentifier)) {
                    paramList = paramList + "&softIdentifier=" + softIdentifier;
                    dynamicView.addAlias("II", "softIdentifier");
                    // add the expr
                    andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("softIdentifier"), EntityOperator.LIKE, EntityFunction.UPPER("%" + softIdentifier + "%")));
                    fieldsToSelect.add("softIdentifier");
                }

                // ----
                // PostalAddress fields
                // ----
                if ("P".equals(extInfo)) {
                    // add address to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMech");
                    dynamicView.addMemberEntity("PA", "PostalAddress");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("PA", "address1");
                    dynamicView.addAlias("PA", "address2");
                    dynamicView.addAlias("PA", "city");
                    dynamicView.addAlias("PA", "stateProvinceGeoId");
                    dynamicView.addAlias("PA", "countryGeoId");
                    dynamicView.addAlias("PA", "postalCode");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on address1
                    String address1 = (String) context.get("address1");
                    if (UtilValidate.isNotEmpty(address1)) {
                        paramList = paramList + "&address1=" + address1;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address1"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address1 + "%")));
                    }

                    // filter on address2
                    String address2 = (String) context.get("address2");
                    if (UtilValidate.isNotEmpty(address2)) {
                        paramList = paramList + "&address2=" + address2;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("address2"), EntityOperator.LIKE, EntityFunction.UPPER("%" + address2 + "%")));
                    }

                    // filter on city
                    String city = (String) context.get("city");
                    if (UtilValidate.isNotEmpty(city)) {
                        paramList = paramList + "&city=" + city;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("city"), EntityOperator.LIKE, EntityFunction.UPPER("%" + city + "%")));
                    }

                    // filter on state geo
                    if (stateProvinceGeoId != null && !"ANY".equals(stateProvinceGeoId)) {
                        paramList = paramList + "&stateProvinceGeoId=" + stateProvinceGeoId;
                        andExprs.add(EntityCondition.makeCondition("stateProvinceGeoId", EntityOperator.EQUALS, stateProvinceGeoId));
                    }

                    // filter on postal code
                    String postalCode = (String) context.get("postalCode");
                    if (UtilValidate.isNotEmpty(postalCode)) {
                        paramList = paramList + "&postalCode=" + postalCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("postalCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + postalCode + "%")));
                    }

                    fieldsToSelect.add("postalCode");
                    fieldsToSelect.add("city");
                    fieldsToSelect.add("stateProvinceGeoId");
                }

                // ----
                // Generic CM Fields
                // ----
                if ("O".equals(extInfo)) {
                    // add info to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMech");
                    dynamicView.addMemberEntity("CM", "ContactMech");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("CM", "infoString");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on infoString
                    String infoString = (String) context.get("infoString");
                    if (UtilValidate.isNotEmpty(infoString)) {
                        paramList = paramList + "&infoString=" + infoString;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("infoString"), EntityOperator.LIKE, EntityFunction.UPPER("%"+infoString+"%")));
                        fieldsToSelect.add("infoString");
                    }

                }

                // ----
                // TelecomNumber Fields
                // ----
                if ("T".equals(extInfo)) {
                    // add telecom to dynamic view
                    dynamicView.addMemberEntity("PC", "PartyContactMech");
                    dynamicView.addMemberEntity("TM", "TelecomNumber");
                    dynamicView.addAlias("PC", "contactMechId");
                    dynamicView.addAlias("TM", "countryCode");
                    dynamicView.addAlias("TM", "areaCode");
                    dynamicView.addAlias("TM", "contactNumber");
                    dynamicView.addViewLink("PT", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
                    dynamicView.addViewLink("PC", "TM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));

                    // filter on countryCode
                    String countryCode = (String) context.get("countryCode");
                    if (UtilValidate.isNotEmpty(countryCode)) {
                        paramList = paramList + "&countryCode=" + countryCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("countryCode"), EntityOperator.EQUALS, EntityFunction.UPPER(countryCode)));
                    }

                    // filter on areaCode
                    String areaCode = (String) context.get("areaCode");
                    if (UtilValidate.isNotEmpty(areaCode)) {
                        paramList = paramList + "&areaCode=" + areaCode;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("areaCode"), EntityOperator.EQUALS, EntityFunction.UPPER(areaCode)));
                    }

                    // filter on contact number
                    String contactNumber = (String) context.get("contactNumber");
                    if (UtilValidate.isNotEmpty(contactNumber)) {
                        paramList = paramList + "&contactNumber=" + contactNumber;
                        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactNumber"), EntityOperator.EQUALS, EntityFunction.UPPER(contactNumber)));
                    }

                    fieldsToSelect.add("contactNumber");
                    fieldsToSelect.add("areaCode");
                }

                // ---- End of Dynamic View Creation

                // build the main condition
                if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
            }

            Debug.logInfo("In findParty mainCond=" + mainCond, module);

            // do the lookup
            if (mainCond != null || "Y".equals(showAll)) {
                try {
                    // get the indexes for the partial list
                    lowIndex = viewIndex * viewSize + 1;
                    highIndex = (viewIndex + 1) * viewSize;

                    // set distinct on so we only get one row per order
                    EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, viewSize, highIndex, true);
                   // EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY,  -1, highIndex, true);
                    // using list iterator
                    EntityListIterator pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, null);

                    // get the partial list for this page
                    partyList = pli.getPartialList(lowIndex, viewSize);

                    // attempt to get the full size
                    partyListSize = pli.getResultsSizeAfterPartialList();
                    if (highIndex > partyListSize) {
                        highIndex = partyListSize;
                    }

                    // close the list iterator
                    pli.close();
                } catch (GenericEntityException e) {
                    String errMsg = "Failure in party find operation, rolling back transaction: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "PartyLookupPartyError",
                            UtilMisc.toMap("errMessage", e.toString()), locale));
                }
            } else {
                partyListSize = 0;
            }
        }
        	
        		
        if (partyList == null) partyList = new ArrayList<GenericValue>();//FastList.newInstance();
        result.put("partyList", partyList);
        result.put("partyListSize", Integer.valueOf(partyListSize));
        result.put("paramList", paramList);
        result.put("highIndex", Integer.valueOf(highIndex));
        result.put("lowIndex", Integer.valueOf(lowIndex));
    
        return result;
    }

    /**
     * Changes the association of contact mechs, purposes, notes, orders and attributes from
     * one party to another for the purpose of merging records together. Flags the from party
     * as disabled so it no longer appears in a search.
     *
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> linkParty(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator _delegator = dctx.getDelegator();
        Delegator delegator = _delegator.cloneDelegator();
        Locale locale = (Locale) context.get("locale");
        delegator.setEntityEcaHandler(null);

        String partyIdTo = (String) context.get("partyIdTo");
        String partyId = (String) context.get("partyId");
        Timestamp now = UtilDateTime.nowTimestamp();

        if (partyIdTo.equals(partyId)) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyCannotLinkPartyToItSelf", locale));
        }

        // get the from/to party records
        GenericValue partyTo;
        try {
            partyTo = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyIdTo));
        } catch (GenericEntityException e) {
            Debug.log(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (partyTo == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyPartyToDoesNotExists", locale));
        }
        if ("PARTY_DISABLED".equals(partyTo.get("statusId"))) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyCannotMergeDisabledParty", locale));
        }

        GenericValue party;
        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.log(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
        if (party == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PartyPartyFromDoesNotExists", locale));
        }

        // update the contact mech records
        try {
            delegator.storeByCondition("PartyContactMech", UtilMisc.<String, Object>toMap("partyId", partyIdTo, "thruDate", now),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the contact mech purpose records
        try {
            delegator.storeByCondition("PartyContactMechPurpose", UtilMisc.<String, Object>toMap("partyId", partyIdTo, "thruDate", now),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the party notes
        try {
            delegator.storeByCondition("PartyNote", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the inventory item(s)
        try {
            delegator.storeByCondition("InventoryItem", UtilMisc.toMap("ownerPartyId", partyIdTo),
                    EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the subscription
        try {
            delegator.storeByCondition("Subscription", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the userLogin records
        try {
            delegator.storeByCondition("UserLogin", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the non-existing party roles
        List<GenericValue> rolesToMove;
        try {
            rolesToMove = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        for (GenericValue attr: rolesToMove) {
            attr.set("partyId", partyIdTo);
            try {
                if (delegator.findByPrimaryKey("PartyRole", attr.getPrimaryKey()) == null) {
                    attr.create();
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        // update the order role records
        try {
            delegator.storeByCondition("OrderRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // invoice role
        try {
            delegator.storeByCondition("InvoiceRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // data resource role
        try {
            delegator.storeByCondition("DataResourceRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // content role
        try {
            delegator.storeByCondition("ContentRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the fin account
        try {
            delegator.storeByCondition("FinAccountRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // update the Product Store Role records
        try {
            delegator.storeByCondition("ProductStoreRole", UtilMisc.<String, Object>toMap("partyId", partyIdTo, "thruDate", now),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        //  update the Communication Event Role records
        try {
            delegator.storeByCondition("CommunicationEventRole", UtilMisc.toMap("partyId", partyIdTo),
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // remove all previous party roles
        try {
            delegator.removeByAnd("PartyRole", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            // if this fails no problem
        }

        // update the non-existing attributes
        List<GenericValue> attrsToMove;
        try {
            attrsToMove = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        for (GenericValue attr: attrsToMove) {
            attr.set("partyId", partyIdTo);
            try {
                if (delegator.findByPrimaryKey("PartyAttribute", attr.getPrimaryKey()) == null) {
                    attr.create();
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }
        try {
            delegator.removeByAnd("PartyAttribute", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // create a party link attribute
        GenericValue linkAttr = delegator.makeValue("PartyAttribute");
        linkAttr.set("partyId", partyId);
        linkAttr.set("attrName", "LINKED_TO");
        linkAttr.set("attrValue", partyIdTo);
        try {
            delegator.create(linkAttr);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // disable the party
        String currentStatus = party.getString("statusId");
        if (currentStatus == null || !"PARTY_DISABLED".equals(currentStatus)) {
            party.set("statusId", "PARTY_DISABLED");

            try {
                party.store();
            } catch (GenericEntityException e) {
                Debug.logError(e, "Error setting disable mode on partyId: " + partyId, module);
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        Map<String, Object> resp = ServiceUtil.returnSuccess();
        resp.put("partyId", partyIdTo);
        return resp;
    }

    public static Map<String, Object> importAddressMatchMapCsv(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        String encoding = System.getProperty("file.encoding");
        String csvFile = Charset.forName(encoding).decode(fileBytes).toString();
        csvFile = csvFile.replaceAll("\\r", "");
        String[] records = csvFile.split("\\n");

        for (int i = 0; i < records.length; i++) {
            if (records[i] != null) {
                String str = records[i].trim();
                String[] map = str.split(",");
                if (map.length != 2 && map.length != 3) {
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                            "PartyImportInvalidCsvFile", locale));
                } else {
                    GenericValue addrMap = delegator.makeValue("AddressMatchMap");
                    addrMap.put("mapKey", map[0].trim().toUpperCase());
                    addrMap.put("mapValue", map[1].trim().toUpperCase());
                    int seq = i + 1;
                    if (map.length == 3) {
                        char[] chars = map[2].toCharArray();
                        boolean isNumber = true;
                        for (char c: chars) {
                            if (!Character.isDigit(c)) {
                                isNumber = false;
                            }
                        }
                        if (isNumber) {
                            try {
                                seq = Integer.parseInt(map[2]);
                            } catch (Throwable t) {
                                Debug.logWarning(t, "Unable to parse number", module);
                            }
                        }
                    }

                    addrMap.put("sequenceNum", Long.valueOf(seq));
                    Debug.log("Creating map entry: " + addrMap, module);
                    try {
                        delegator.create(addrMap);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(e.getMessage());
                    }
                }
            } else {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "PartyImportNoRecordsFoundInFile", locale));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    public static String getPartyId(Map<String, ? extends Object> context) {
        String partyId = (String) context.get("partyId");
        if (UtilValidate.isEmpty(partyId)) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            if (userLogin != null) {
                partyId = userLogin.getString("partyId");
            }
        }
        return partyId;
    }


    /**
     * Finds partyId(s) corresponding to a party reference, partyId or a GoodIdentification idValue
     * @param dctx
     * @param context
     * @param context.partyId use to search with partyId or goodIdentification.idValue
     * @return a GenericValue with a partyId and a List of complementary partyId found
     */
    public static Map<String, Object> findPartyById(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        String idToFind = (String) context.get("idToFind");
        String partyIdentificationTypeId = (String) context.get("partyIdentificationTypeId");
        String searchPartyFirstContext = (String) context.get("searchPartyFirst");
        String searchAllIdContext = (String) context.get("searchAllId");

        boolean searchPartyFirst = UtilValidate.isNotEmpty(searchPartyFirstContext) && "N".equals(searchPartyFirstContext) ? false : true;
        boolean searchAllId = UtilValidate.isNotEmpty(searchAllIdContext)&& "Y".equals(searchAllIdContext) ? true : false;

        GenericValue party = null;
        List<GenericValue> partiesFound = null;
        try {
            partiesFound = PartyWorker.findPartiesById(delegator, idToFind, partyIdentificationTypeId, searchPartyFirst, searchAllId);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        if (UtilValidate.isNotEmpty(partiesFound)) {
            // gets the first partyId of the List
            party = EntityUtil.getFirst(partiesFound);
            // remove this partyId
            partiesFound.remove(0);
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("party", party);
        result.put("partiesFound", partiesFound);

        return result;
    }
    
    public static Map<String, Object> createParty(DispatchContext ctx, Map<String, Object> context) throws GenericServiceException {
    
    	 LocalDispatcher dispatcher = ctx.getDispatcher();
    	String partyId=null;
    	Map servRes=new HashMap();
    	 Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
         Delegator delegator = ctx.getDelegator();
         GenericValue userLogin = (GenericValue) context.get("userLogin");
         Timestamp now = UtilDateTime.nowTimestamp();

         String groupName = (String) context.get("groupName");
        
         Locale locale = (Locale) context.get("locale");
         String desc=(String) context.get("comments");
    	
         if (UtilValidate.isEmpty(partyId)) {
             try {
                 partyId = delegator.getNextSeqId("Party");
             } catch (IllegalArgumentException e) {
                 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                         "partyservices.could_not_create_party_group_generation_failure", locale));
             }
         } else {
             // if specified partyId starts with a number, return an error
             if (partyId.matches("\\d+")) {
                 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                         "partyservices.could_not_create_party_ID_digit", locale));
             }
         }
         try {
        	
             // check to see if party object exists, if so make sure it is PARTY_GROUP type party
             GenericValue party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
             GenericValue partyGroupPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"));

             if (partyGroupPartyType == null) {
                 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                         "partyservices.partyservices.party_type_not_found_in_database_cannot_create_party_group", locale));
             }

             if (party != null) {
                 GenericValue partyType = party.getRelatedOneCache("PartyType");

                 if (!EntityTypeUtil.isType(partyType, partyGroupPartyType)) {
                     return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                             "partyservices.partyservices.cannot_create_party_group_already_exists_not_PARTY_GROUP_type", locale));
                 }
             } else {
                 // create a party if one doesn't already exist
                 String partyTypeId = "PARTY_GROUP";

                 if (UtilValidate.isNotEmpty(context.get("partyTypeId"))) {
                     GenericValue desiredPartyType = delegator.findByPrimaryKeyCache("PartyType", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")));
                     if (desiredPartyType != null && EntityTypeUtil.isType(desiredPartyType, partyGroupPartyType)) {
                         partyTypeId = desiredPartyType.getString("partyTypeId");
                     } else {
                         return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                                 "PartyPartyTypeIdNotFound", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")), locale));
                     }
                 }

                 Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", partyTypeId, "createdDate", now, "lastModifiedDate", now);
                 if (userLogin != null) {
                     newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                     newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                 }

                 String statusId = (String) context.get("statusId");
                 party = delegator.makeValue("Party", newPartyMap);
                 party.setNonPKFields(context);

                 if (statusId == null) {
                     statusId = "PARTY_ENABLED";
                 }
                 party.set("statusId", statusId);
                 party.create();

                 // create the status history
                 GenericValue partyStat = delegator.makeValue("PartyStatus",
                         UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
                 partyStat.create();
             }

             GenericValue partyGroup = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", partyId));
             if (partyGroup != null) {
                 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                         "partyservices.cannot_create_party_group_already_exists", locale));
             }

             partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId,"groupName",groupName));
             partyGroup.setNonPKFields(context);
             partyGroup.create();
             
      /* GenericValue partyRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", partyId,"roleTypeId","TEAM"));
             partyRole.create();*/
             Map serviceParams= dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "TEAM", "userLogin", userLogin));
             
            /* GenericValue Roletype = delegator.makeValue("RoleType", UtilMisc.toMap("roleTypeId","Team","description",desc));
             Roletype.create();*/

         } catch (GenericEntityException e) {
             Debug.logWarning(e, module);
             return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                     "partyservices.data_source_error_adding_party_group", 
                     UtilMisc.toMap("errMessage", e.getMessage()), locale));
         }
        
         

         result.put("partyId", partyId);
         result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
         return result;
    	
    	
    }
    public static Map<String, Object> createPersonMember(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();//FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");
        String description = (String) context.get("description");
        String groupName=(String) context.get("groupName");
        String roleTypeId=(String) context.get("roleTypeId");
        String userName=(String) context.get("USERNAME");

        // if specified partyId starts with a number, return an error
        if (UtilValidate.isNotEmpty(partyId) && partyId.matches("\\d+")) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "party.id_is_digit", locale));
        }

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "party.id_generation_failure", locale));
            }
        }

        // check to see if party object exists, if so make sure it is PERSON type party
        GenericValue party = null;

        try {
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (party != null) {
            if (!"PERSON".equals(party.getString("partyTypeId"))) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.create.party_exists_not_person_type", locale)); 
            }
        } else {
            // create a party if one doesn't already exist with an initial status from the input
            String statusId = (String) context.get("statusId");
            if (statusId == null) {
                statusId = "PARTY_ENABLED";
            }
            Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", "PERSON", "description", description, "createdDate", now, "lastModifiedDate", now, "statusId", statusId);
            String preferredCurrencyUomId = (String) context.get("preferredCurrencyUomId");
            if (!UtilValidate.isEmpty(preferredCurrencyUomId)) {
                newPartyMap.put("preferredCurrencyUomId", preferredCurrencyUomId);
            }
            String externalId = (String) context.get("externalId");
            if (!UtilValidate.isEmpty(externalId)) {
                newPartyMap.put("externalId", externalId);
            }
            if (userLogin != null) {
                newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            }
            party = delegator.makeValue("Party", newPartyMap);
            toBeStored.add(party);

            // create the status history
            GenericValue statusRec = delegator.makeValue("PartyStatus",
                    UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
            toBeStored.add(statusRec);
        }

        GenericValue person = null;

        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }

        if (person != null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.person_exists", locale)); 
        }

        person = delegator.makeValue("Person", UtilMisc.toMap("partyId", partyId));
        person.setNonPKFields(context);
        toBeStored.add(person);

        try {
            delegator.storeAll(toBeStored);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                    "person.create.db_error", new Object[] { e.getMessage() }, locale)); 
           
        }
      
        Map serviceParams= dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId",roleTypeId, "userLogin", userLogin));
        GenericValue relation = delegator.makeValue("PartyRelationship", UtilMisc.toMap("partyIdFrom",groupName,"partyIdTo",partyId,"roleTypeIdFrom","TEAM","roleTypeIdTo",roleTypeId,"fromDate",now,"partyRelationshipTypeId","GROUP_ROLLUP"));
        relation.setNonPKFields(context);
        relation.create();
        
       
        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    public static Map<String, Object> FindRole(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List<GenericValue> toBeStored = new ArrayList<GenericValue>();//FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String user=(String)userLogin.get("userLoginId");
        //System.out.println("radha \n\n\n\n\n\n\n"+user);
        String role=null;
       if(!(user.equals("admin")))
       {
        try {
        
            GenericValue list=delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId",user));
            //Collection<GenericValue> list= delegator.findList("UserLogin", ecl, null, UtilMisc.toList("partyId"), null, false);
          
            String p=(String)list.getString("partyId");
            //System.out.println("PartyId\n\n\n\n\n\n\n\n\n\n\n\n\n"+p);
            EntityExpr ee = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,p);
           
            //System.out.println("the ec1\n\n\n\n\n\n"+ee);
            List<GenericValue> list1= delegator.findList("PartyRole",ee,null,UtilMisc.toList("roleTypeId"),null,false);
            //System.out.println("the list\n\n\n\n\n\n"+list1);
            
            
            for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
        		GenericValue gv = (GenericValue) iterator.next();
        		 if(gv.getString("roleTypeId").equals("MANAGER")) 
        		 {
        			 role="MANAGER";
        		 }
        		if(gv.getString("roleTypeId").equals("TEAMLEADER"))
        		 {
        			 role="TEAMLEADER";
        		 }
        		if(gv.getString("roleTypeId").equals("TEAMMEMBER"))
        		 {
        			 role="TEAMMEMBER";
        		 }
        	}
            
        }catch(GenericEntityException e){
        	//System.out.println(e);
        }
       }
       else
    	   
    	   role="admin";
        
        result.put("roleType",role);
        return result;
    
    
    }
    public static Map<String, Object> FindGroup(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
        Map<String, Object> result1 = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        List partylist = new ArrayList<GenericValue>();//FastList.newInstance();
        List list2 = new ArrayList<GenericValue>();//FastList.newInstance();
        List grouplist = new ArrayList<GenericValue>();//FastList.newInstance();
        Locale locale = (Locale) context.get("locale");
        // in most cases userLogin will be null, but get anyway so we can keep track of that info if it is available
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try {
        
            
        	EntityExpr ee = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"TEAM");
    
        List<GenericValue> list1= delegator.findList("PartyRole",ee,null,null,null,false);
        //System.out.println("the list\n\n\n\n\n\n"+list1);
        
        for (Iterator iterator = list1.iterator(); iterator.hasNext();) {
    		GenericValue gv = (GenericValue) iterator.next();
    		String p=(String)gv.getString("partyId");
    		partylist.add(p);
    		
        }
        //System.out.println("partyfinal list"+partylist);
        
       
        EntityExpr ee1 = EntityCondition.makeCondition("partyId", EntityOperator.IN,partylist);
      list2= delegator.findList("PartyGroup",ee1,null,UtilMisc.toList("partyId"),null,false);
      
    
        //System.out.println("the final final \n\n\n\n"+list2);
        }catch(Exception e){
        }
   
        result1.put("grouplist",list2);
        return result1;
		
    }
    public static Map deactivateTeam(DispatchContext dctx, Map context) {
    	 Map<String, Object> result1 = new HashMap<String, Object>(); //FastMap.newInstance();
    	 Map servRes=new HashMap(); //FastMap.newInstance();
    	   Locale locale = (Locale) context.get("locale");
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String user=userLogin.getString("userloginId");

        String teamPartyId = (String) context.get("partyId");

        // ensure team deactivate permission on this team
      
        try {
        	if(user.equals("admin"))
        	{
            // expire all active relationships for team
        	  EntityExpr ee = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS,teamPartyId);
        	 int n= delegator.removeByCondition("PartyRelationship",ee);
        	 
        	 EntityExpr ee2 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,teamPartyId);
        	 int n2= delegator.removeByCondition("PartyGroup",ee2);
        	 EntityExpr ee4 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,teamPartyId);
        	 int n4= delegator.removeByCondition("PartyRole",ee4);
        	
        	 EntityExpr ee3 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,teamPartyId);
        	 int n3= delegator.removeByCondition("PartyStatus",ee3);
        	 EntityExpr ee1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,teamPartyId);
        	 int n1= delegator.removeByCondition("Party",ee1);
        	 result1 = ServiceUtil.returnSuccess();
        	}
      
        	

     
      else
    	  result1.put("error", "error");
        } 
        catch (GenericEntityException e) {
            
        }
        
      
    
  
    return result1;
    }
  
    
    
    /**
     * Creates a Team.
     */
    public static Map<String, Object> createTeamGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>(); //FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");


        
        String targetId = delegator.getNextSeqId("SalesTarget");
        String teamTypeId = (String) context.get("teamTypeId");
        
        BigDecimal targetCost = (BigDecimal) context.get("targetCost");
        BigDecimal bestCaseCost = (BigDecimal) context.get("bestCaseCost");
        BigDecimal closedCost = (BigDecimal) context.get("closedCost");
        String currencyUomId = (String) context.get("currencyUomId");
        
        Timestamp fromDate = (Timestamp) context.get("fromDate");
        Timestamp thruDate = (Timestamp) context.get("thruDate");
        
        String createdByUserLogin = (String) context.get("createdByUserLogin");
        String stateGeoId = (String) context.get("stateGeoId");
        String countryGeoId = (String) context.get("countryGeoId");
        String zoneId = (String) context.get("zoneId");
        String teamId = partyId ;
        
        GenericValue gv = delegator.makeValue("SalesTarget");
        gv.put("targetId", targetId);
        gv.put("teamTypeId", teamTypeId);
        gv.put("targetCost", targetCost);
        gv.put("bestCaseCost", bestCaseCost);
        gv.put("closedCost", closedCost);
        gv.put("currencyUomId", currencyUomId);
        gv.put("fromDate", fromDate);
        gv.put("thruDate", thruDate);
        
        gv.put("createdByUserLogin", userLogin.get("userLoginId"));
        gv.put("stateGeoId", stateGeoId);
        gv.put("countryGeoId", countryGeoId);
        gv.put("zoneId", zoneId);
        gv.put("partyId", teamId);
        
        try {
			gv.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		 Map<String, Object> results = ServiceUtil.returnSuccess();
		 results.put(ModelService.SUCCESS_MESSAGE, "Team Target Created Succesfully");
        return results;
    }
public static String getAssociatedStateList(HttpServletRequest request, HttpServletResponse response) {
        
    	String stateList = "<div id='stateProvinceGeoIddiv'><select name='stateProvinceGeoId1' id='stateProvinceGeoId' onchange='changeState();'>";
    	String selectedStateFirst = "";
    	String countryAssocStateList = "";
    	boolean flag = false;
    	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
    	String countryGeoId = request.getParameter("countryGeoId");
    	String stateProvinceGeoId1 = request.getParameter("stateProvinceGeoId1");
    	List geoList = (List)CommonWorkers.getAssociatedStateList(delegator, countryGeoId);
    	Iterator itr = geoList.iterator(); 
    	 Debug.logInfo("      countryGeoId      "+countryGeoId, module);
    	if(geoList != null && geoList.size() == 0){
    		stateList = stateList + "<option value='_NA_' >No State Exist</option>";
        	try {
    			PrintWriter out = response.getWriter();
    			out.print(stateList);
    		} catch (IOException e) {
    			Debug.logError(e.getMessage(),module);
    		}
            return "success";    		
    	}
    	while(itr.hasNext()){
    		GenericValue stateGeo = (GenericValue)itr.next();
    		/*if((stateProvinceGeoId1 != null || stateProvinceGeoId1.length()>0 ) && stateProvinceGeoId1.equalsIgnoreCase((String) stateGeo.get("geoId"))){
    			selectedStateFirst = selectedStateFirst + "<option value='"+stateGeo.get("geoId")+"' >"+stateGeo.get("geoName")+"</option>";
    			//selectedStateFirst = selectedStateFirst + "<option value='' > - - - - </option>";
    			flag = false;
    		}else{*/
    			countryAssocStateList = countryAssocStateList + "<option value='"+stateGeo.get("geoId")+"' >"+stateGeo.get("geoName")+"</option>";
    		//}
    	}
    	
    	if(flag){
    		stateList = stateList + selectedStateFirst + countryAssocStateList;
    	}else{
    		stateList = stateList + "<option value='' >Select Your State</option>";
    		stateList = stateList + countryAssocStateList;
    	}
    	
    	if(stateList.length() <= 105)
    		stateList = stateList + "<option value='_NA_'>No States/Provinces exist</option>";
    		
    	
    	stateList = stateList +"</select></div>";
    	
   
    	try {
			PrintWriter out = response.getWriter();
			out.print(stateList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Debug.logError(e.getMessage(),module);
			//e.printStackTrace();
		}
        return "success";
    }
public static String getAssociatedDynamicStateList(HttpServletRequest request, HttpServletResponse response) {
	String stateProvinceDynamicGeoId = request.getParameter("stateProvinceDynamicGeoId");
	if (stateProvinceDynamicGeoId == null){
		stateProvinceDynamicGeoId = "stateProvinceGeoId";
	}
	
	String stateList = "<div id=''><select name='stateProvinceGeoId1' style='width:175px;' id='"+stateProvinceDynamicGeoId+"' onchange='changeState();'>";
	String selectedStateFirst = "";
	String countryAssocStateList = "";
	boolean flag = false;
	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	String countryGeoId = request.getParameter("countryGeoId");
	String stateProvinceGeoId1 = request.getParameter("stateProvinceGeoId1");
	List geoList = (List)CommonWorkers.getAssociatedStateList(delegator, countryGeoId);
	Iterator itr = geoList.iterator(); 
	 Debug.logInfo("      countryGeoId      "+countryGeoId, module);
	if(geoList != null && geoList.size() == 0){
		stateList = stateList + "<option value='_NA_' >No State Exist</option>";
    	try {
			PrintWriter out = response.getWriter();
			out.print(stateList);
		} catch (IOException e) {
			Debug.logError(e.getMessage(),module);
		}
        return "success";    		
	}
	while(itr.hasNext()){
		GenericValue stateGeo = (GenericValue)itr.next();
		/*if((stateProvinceGeoId1 != null || stateProvinceGeoId1.length()>0 ) && stateProvinceGeoId1.equalsIgnoreCase((String) stateGeo.get("geoId"))){
			selectedStateFirst = selectedStateFirst + "<option value='"+stateGeo.get("geoId")+"' >"+stateGeo.get("geoName")+"</option>";
			//selectedStateFirst = selectedStateFirst + "<option value='' > - - - - </option>";
			flag = false;
		}else{*/
			countryAssocStateList = countryAssocStateList + "<option value='"+stateGeo.get("geoId")+"' >"+stateGeo.get("geoName")+"</option>";
		//}
	}
	
	if(flag){
		stateList = stateList + selectedStateFirst + countryAssocStateList;
	}else{
		stateList = stateList + "<option value='' >Select Your State</option>";
		stateList = stateList + countryAssocStateList;
	}
	
	if(stateList.length() <= 105)
		stateList = stateList + "<option value='_NA_'>No States/Provinces exist</option>";
		
	
	stateList = stateList +"</select></div>";
	

	try {
		PrintWriter out = response.getWriter();
		out.print(stateList);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		Debug.logError(e.getMessage(),module);
		//e.printStackTrace();
	}
    return "success";
}


	public static Map<String ,Object> sendReferLoyaltySms(DispatchContext ctx, Map context) {
		Delegator delegator = ctx.getDelegator();
		sendRefLoyaltySms(delegator , context);
		
		return ServiceUtil.returnSuccess();
	}

	public static void sendRefLoyaltySms(Delegator delegator , Map context) {
	try {
		
		String sendFrom = (String) context.get("sendByParty");
		String sendTo = (String) context.get("sendToParty");
		String sendToNo = (String) context.get("sendToMobile");
		String sendToName = (String) context.get("sendToName");
		String loyaltyRefId = (String) context.get("loyaltyRefId");
		GenericValue gv1=null;
		
		//done by radha including ref no
		GenericValue gv=delegator.findByPrimaryKey("UserLogin",UtilMisc.toMap("userLoginId",sendFrom));
		if(UtilValidate.isNotEmpty(gv))
			gv1=delegator.findByPrimaryKey("Person",UtilMisc.toMap("partyId",gv.getString("partyId")));
		
		
		if (sendToNo != null && sendFrom != null) {
		    
		    String message1 = UtilProperties.getPropertyValue("general.properties","referfriend.processing.sms");;
		    		
		    		String newMessage1 = "";
		    		String newMessage2 = "";
		    		
		    		
		    		if(UtilValidate.isNotEmpty(sendToName))
		    			newMessage1 = message1.replaceAll("<CustomerName>", sendToName);
				    else
				    	newMessage1 = message1.replaceAll("<CustomerName>", "");
		    		if(UtilValidate.isNotEmpty(gv1))
		    			newMessage1 = newMessage1.replaceAll("<CustomerFromName>", gv1.getString("firstName")+" "+gv1.getString("lastName"));
				    else
				    	newMessage1 = newMessage1.replaceAll("<CustomerFromName>", "");
		    		if(UtilValidate.isNotEmpty(loyaltyRefId))
		    			newMessage1 = newMessage1.replaceAll("<referenceId>", loyaltyRefId);
				    else
				    	newMessage1 = newMessage1.replaceAll("<referenceId>", "");
				    
			    	Map messageData = new HashMap();
				    messageData.put("mobileNumber", sendToNo);
				   
				    messageData.put("message", newMessage1);

				    PartyServices.sendSms(messageData);
			    	
		    	}
	} catch (Exception e) {
    	Debug.logError(e.getMessage(),module);
    	//e.printStackTrace();
    }
}

	public static void sendSms(Map messageData){
	
	try {
		
		String username = UtilProperties.getPropertyValue("general.properties","sms.username");
		String password = UtilProperties.getPropertyValue("general.properties","sms.password");
		String senderId = UtilProperties.getPropertyValue("general.properties","sms.senderId");
		String sendSMS = UtilProperties.getPropertyValue("general.properties","sms.sendSMS");
		String mobileNumber = (String) messageData.get("mobileNumber");
		String messageText = (String) messageData.get("message");
		
		if (UtilValidate.isEmpty(sendSMS) || (UtilValidate.isNotEmpty(sendSMS) && sendSMS.equalsIgnoreCase("false")) || UtilValidate.isEmpty(mobileNumber) || UtilValidate.isEmpty(messageText)) {
			
			if(UtilValidate.isEmpty(mobileNumber) || UtilValidate.isEmpty(messageText)) {
				Debug.log("Not sending SMS because some required data is missing..", module);
			} else {
				Debug.log("Not sending SMS because SMS Gateway is not configured properly, please check your SMS settings.", module);
			}
			return;
		}
		
		if (mobileNumber != null) {
			mobileNumber = mobileNumber.trim();
			mobileNumber = mobileNumber.replace(" ", "");
			mobileNumber = mobileNumber.replace("-", "");
			mobileNumber = mobileNumber.replace("+", "");
			
			if ((mobileNumber.length() == 12) && mobileNumber.startsWith("91")) {
				mobileNumber = mobileNumber.substring(2);
			}else if ((mobileNumber.length() == 11)) {
				mobileNumber = mobileNumber.substring(1);
			}
			
			/*if ((mobileNumber.length() > 12) || (mobileNumber.length() < 10) || mobileNumber.startsWith("91")) {
				mobileNumber = mobileNumber.substring(2);
			}*/
		}
		Debug.logInfo("Sending SMS to : " + mobileNumber , module);
		Debug.logInfo("SMS text : " + messageText , module);
		// Construct data
		String data = "";
		/*data += "user=" + URLEncoder.encode(username, "ISO-8859-1");
		data += "&pass=" + URLEncoder.encode(password, "ISO-8859-1");
		data += "&sender=" + senderId;
		data += "&phone=" + mobileNumber;
		data += "&text=" + URLEncoder.encode(messageText, "ISO-8859-1");
		data += "&priority=ndnd";
		data += "&stype=normal";*/
		
	
        data += "user=" + URLEncoder.encode(username, "ISO-8859-1");
        data += "&pass=" + URLEncoder.encode(password, "ISO-8859-1");
        data += "&sender=" + senderId;
        data += "&phone=" + mobileNumber;
        data += "&text=" + URLEncoder.encode(messageText, "ISO-8859-1");
        data += "&priority=ndnd";
        data += "&stype=normal";

		//http://bhashsms.com/api/sendmsg.php?user=samcook&pass=********&sender=Sender_ID&phone=Mobile_No&text=SMS_text&priority=priority&stype=normal/flash
		//Note : stype - normal/flash , priority - ndnd/dnd , phone(mobile) number without 91
        
        // Send data
		URL url = new URL("http://bhashsms.com/api/sendmsg.php"); 
		//URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
        if (UtilValidate.isNotEmpty(mobileNumber) && UtilValidate.isNotEmpty(messageText)) {
        	//System.out.println("the connection is opened\n\n\n\n\n\n\n");
        	URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String responseData = "";
            while ((line = rd.readLine()) != null) {
            	responseData += line;
            }
            Debug.logInfo("SMS Response : " + responseData , module);
            //System.out.println("\n\n###############################responseData- "+responseData+"\n\n");
            wr.close();
            rd.close();
        }
        
    } catch (Exception e) {
    	Debug.logError(e.getMessage(),module);
    	//e.printStackTrace();
    }
	}
	
	public static String sms(HttpServletRequest request, HttpServletResponse response) {
		
		try{
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Delegator delegator = (Delegator) request.getAttribute("delegator");
			List<GenericValue> mobils = delegator.findList("Mobile", null, null, null, null, false);
			HttpSession session = request.getSession();
			GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
			for(GenericValue gv : mobils){
				dispatcher.runAsync("sendSms", UtilMisc.toMap("userLogin", userLogin, "mobileNumber", gv.getString("mobileNo"), "message", "Weekend is back again. Get FREE 1Kg Onion + 1 Kg Tomato on purchase of Rs. 500 on www.youmart.in. Use coupon code TO500. Offer valid till 22nd June"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}
	
	
	public static Map sendSms(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		try {
			
			String username = UtilProperties.getPropertyValue("general.properties","sms.username");
			String password = UtilProperties.getPropertyValue("general.properties","sms.password");
			String senderId = UtilProperties.getPropertyValue("general.properties","sms.senderId");
			String sendSMS = UtilProperties.getPropertyValue("general.properties","sms.sendSMS");
			
			
			String mobileNumber = (String) context.get("mobileNumber");
			String messageText = (String) context.get("message");
			
			if (UtilValidate.isEmpty(sendSMS) || (UtilValidate.isNotEmpty(sendSMS) && sendSMS.equalsIgnoreCase("false")) || UtilValidate.isEmpty(mobileNumber) || UtilValidate.isEmpty(messageText)) {
				
				if(UtilValidate.isEmpty(mobileNumber) || UtilValidate.isEmpty(messageText)) {
					Debug.log("Not sending SMS because some required data is missing..", module);
				} else {
					Debug.log("Not sending SMS because SMS Gateway is not configured properly, please check your SMS settings.", module);
				}
				return ServiceUtil.returnSuccess();
			}
			
			if (mobileNumber != null) {
				mobileNumber = mobileNumber.trim();
				mobileNumber = mobileNumber.replace(" ", "");
				mobileNumber = mobileNumber.replace("-", "");
				mobileNumber = mobileNumber.replace("+", "");
				
				if ((mobileNumber.length() == 12) && mobileNumber.startsWith("91")) {
					mobileNumber = mobileNumber.substring(2);
				}else if ((mobileNumber.length() == 11)) {
					mobileNumber = mobileNumber.substring(1);
				}
			}
			Debug.logInfo("Sending SMS to : " + mobileNumber , module);
			Debug.logInfo("SMS text : " + messageText , module);
			// Construct data
			String data = "";
	        data += "user=" + URLEncoder.encode(username, "ISO-8859-1");
	        data += "&pass=" + URLEncoder.encode(password, "ISO-8859-1");
	        data += "&sender=" + senderId;
	        data += "&phone=" + mobileNumber;
	        data += "&text=" + URLEncoder.encode(messageText, "ISO-8859-1");
	        data += "&priority=ndnd";
	        data += "&stype=normal";

			//http://bhashsms.com/api/sendmsg.php?user=samcook&pass=********&sender=Sender_ID&phone=Mobile_No&text=SMS_text&priority=priority&stype=normal/flash
			//Note : stype - normal/flash , priority - ndnd/dnd , phone(mobile) number without 91
	        
	        // Send data
			URL url = new URL("http://bhashsms.com/api/sendmsg.php"); 
			//URL url = new URL("http://bulksms.vsms.net:5567/eapi/submission/send_sms/2/2.0");
	        if (UtilValidate.isNotEmpty(mobileNumber) && UtilValidate.isNotEmpty(messageText)) {
	        	//System.out.println("the connection is opened\n\n\n\n\n\n\n");
	        	URLConnection conn = url.openConnection();
	            conn.setDoOutput(true);
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            wr.write(data);
	            wr.flush();

	            // Get the response
	            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	            String line;
	            String responseData = "";
	            while ((line = rd.readLine()) != null) {
	            	responseData += line;
	            }
	            Debug.logInfo("SMS Response : " + responseData , module);
	            //System.out.println("\n\n###############################responseData- "+responseData+"\n\n");
	            wr.close();
	            rd.close();
	        }
	        
	    } catch (Exception e) {
	    	Debug.logError(e.getMessage(),module);
	    	//e.printStackTrace();
	    }
		
		return ServiceUtil.returnSuccess();
	}

	public static Map<String ,Object> userRegistrationSms(DispatchContext ctx, Map context) {
		try{
			Debug.log("\n\n userRegistrationSms === \n\n");
			Delegator delegator = ctx.getDelegator();
			sendUserRegistrationSms(delegator , context);
		}catch(Exception e){
			e.printStackTrace();
		}
	return ServiceUtil.returnSuccess();
	}
	public static void sendUserRegistrationSms(Delegator delegator , Map context) {
		String sendFrom = (String) context.get("partyId");
		String phoneNo = (String) context.get("phoneNo");
		String isMobile = (String) context.get("isMobile");
		GenericValue  person = null;
		String fName = null;
		String lName = null;
        try {
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", sendFrom));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            e.printStackTrace();
        } 
        Debug.log("\n\n person === "+person+"\n\n");
        try {
            if (UtilValidate.isNotEmpty(person) && UtilValidate.isNotEmpty(person.getString("firstName")) && UtilValidate.isNotEmpty(person.getString("lastName"))) {
            	fName = person.getString("firstName");
            	lName = person.getString("lastName");
            }
			if (UtilValidate.isNotEmpty(phoneNo)) {
				String message1 = null;
			    if(UtilValidate.isNotEmpty(isMobile) && isMobile.equalsIgnoreCase("Y")){
			    	message1 = UtilProperties.getPropertyValue("general.properties","userregistration.mobile.sms");
			    }else{
			    	message1 = UtilProperties.getPropertyValue("general.properties","userregistration.processing.sms");
			    }
			    
	    		String fullname= fName+" "+lName;
	    		String newMessage1 = "";
	    		if(UtilValidate.isNotEmpty(fullname)){
	    			newMessage1 = message1.replaceAll("<Customer>", fullname);
	    		} else{
			    	newMessage1 = message1.replaceAll("<Customer>", "");
	    		}
	    		if(message1.contains("<UniqueCode>")){
	    			newMessage1 = message1.replaceAll("<UniqueCode>", (String)context.get("uniqCode"));
	    		}
	    		
		    	Map messageData = new HashMap();
			    messageData.put("mobileNumber", phoneNo);
			    messageData.put("message", newMessage1);
			    System.out.print("\n\n messageData == "+messageData+"\n\n");
			    PartyServices.sendSms(messageData);
		    	
			}
		} catch (Exception e) {
	    	Debug.logError(e.getMessage(),module);
	    	e.printStackTrace();
	    }
	}
}
