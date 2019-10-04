package com.ilinks.restful.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import com.ilinks.restful.util.RestfulHelper;

public class ReferAFriendServices {
    public static final String MODULE = ReferAFriendServices.class.getName();
    
    public static Map<String, Object> retrieveReferAFriendInfo(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        List<EntityCondition> entityConditions = FastList.newInstance();
        List<Object> referAFriendList = FastList.newInstance();
        String trackingCode = "";
        try{
           
            GenericValue partyAttribute = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", userLogin.getString("partyId"), "attrName", "referAFriendCode"), false);
            Timestamp startDate = (Timestamp)context.get("startDate");
            Timestamp endDate = (Timestamp)context.get("endDate");
            
            if(partyAttribute == null){
                results = dispatcher.runSync("createReferAFriendCode", UtilMisc.toMap("userLogin",userLogin));
                trackingCode = (String)results.get("trackingCode");
            }
            else{
                trackingCode = partyAttribute.getString("attrValue");
            }
            entityConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.get("partyId")));
            entityConditions.add(EntityCondition.makeCondition("paymentTypeId", EntityOperator.EQUALS, "COMMISSION_PAYMENT"));
            entityConditions.add(EntityCondition.makeCondition("createdTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
            entityConditions.add(EntityCondition.makeCondition("createdTxStamp", EntityOperator.LESS_THAN, endDate));
            entityConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,UtilMisc.toList("PMNT_SENT", "PMNT_NOT_PAID")));
            
           
            List<GenericValue> payments = delegator.findList("Payment",  EntityCondition.makeCondition(entityConditions, EntityOperator.AND), null, UtilMisc.toList("createdTxStamp DESC"), null, true);
            
            for (GenericValue payment : payments){
                Map<String,Object> map = FastMap.newInstance();
                entityConditions.clear();
                entityConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN,UtilMisc.toList("ORDER_REJECTED", "ORDER_CANCELLED")));
                entityConditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, payment.get("paymentRefNum")));
                
                List<GenericValue> orderHeaders = delegator.findList("OrderHeader",  EntityCondition.makeCondition(entityConditions, EntityOperator.AND), null, UtilMisc.toList("createdTxStamp DESC"), null, true);
                
                if(UtilValidate.isNotEmpty(orderHeaders)){
                    List<GenericValue> orderContactMech = delegator.findByAndCache("OrderContactMech", UtilMisc.toMap("orderId", payment.get("paymentRefNum"), "contactMechPurposeTypeId", "ORDER_EMAIL"));
                    GenericValue contactMech = EntityUtil.getFirst(orderContactMech).getRelatedOne("ContactMech");
                    map.put("orderDate", UtilFormatOut.formatDate(payment.getTimestamp("createdTxStamp")));
                    map.put("paidDate", UtilFormatOut.formatDate(payment.getTimestamp("lastUpdatedTxStamp")));
                    map.put("status", payment.getString("statusId"));
                    map.put("amount", payment.getBigDecimal("amount"));
                    map.put("email", contactMech.getString("infoString"));
                    referAFriendList.add(map);
                }
            }
            
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable Expire Shopping List" + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            results.put("trackingCode", trackingCode);
            results.put("referAFriendList", referAFriendList);
        }
        return results;
    }
    
    public static Map<String, Object> sendReferAFriendEmail(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        try{
           GenericValue person = userLogin.getRelatedOneCache("Person");
           GenericValue emailSetting = delegator.findOne("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId",context.get("productStoreID"), "emailType","REFER_FRIEND"),true);
           Map<String,Object> dataContext = FastMap.newInstance();
           dataContext.put("partyId",userLogin.get("partyId"));
           dataContext.put("bodyScreenUri",emailSetting.get("bodyScreenLocation"));
           dataContext.put("sendFrom", emailSetting.get("fromAddress"));
           dataContext.put("sendTo", context.get("sendTo"));
           dataContext.put("subject", emailSetting.get("subject"));
           Map<String,Object> bodyParameters = FastMap.newInstance();
           String referAFriendLink = "www.petbest.com/refer/" + context.get("trackingCode");
           bodyParameters.put("uniqueLink", referAFriendLink);
           bodyParameters.put("refererFirstName", person.getString("firstName"));
           bodyParameters.put("refererLastName", person.getString("lastName"));
           bodyParameters.put("friendName", context.get("sendToName"));
           dataContext.put("bodyParameters", bodyParameters);
           dispatcher.runAsync("sendMailFromScreen", dataContext);
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Send Refer A Friend Email" + context + "\n" + e.getMessage(), MODULE);
        }
        return results;
    }
    
    
    public static Map<String, Object> createReferAFriendCode(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> results = ServiceUtil.returnSuccess();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String trackingCodeID = null;
        try{
            GenericValue person = userLogin.getRelatedOneCache("Person");
            GenericValue system = RestfulHelper.getSystemLogin((GenericDelegator)delegator);
            trackingCodeID = (person.getString("firstName") + RandomStringUtils.randomAlphanumeric(5)).toLowerCase();
            Map<String,Object> dataContext = FastMap.newInstance();
            dataContext.put("createdByUserLogin", userLogin.get("userLoginId"));
            dataContext.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
            dataContext.put("trackingCodeId", trackingCodeID);
            dataContext.put("trackingCodeTypeId","REFER_FRIEND");
            dataContext.put("trackableLifetime",Long.parseLong("2592000"));
            dataContext.put("billableLifetime",Long.parseLong("2592000"));
            
            GenericValue trackingCode = delegator.makeValue("TrackingCode");
            trackingCode.setFields(dataContext);
            trackingCode.create();
            if(ServiceUtil.isSuccess(results)){
                dataContext.clear();
                dataContext.put("userLogin", system);
                dataContext.put("partyId", userLogin.get("partyId"));
                dataContext.put("attrName", "referAFriendCode");
                dataContext.put("attrValue", trackingCodeID);
                results = dispatcher.runSync("createPartyAttribute", dataContext);
            }
            
        }
        catch (Exception e){
            results = ServiceUtil.returnError(e.getMessage());
            Debug.logError(e, "Unable To Create Refer A Friend Tracking Code" + context + "\n" + e.getMessage(), MODULE);
        }
        finally{
            results.put("trackingCode", trackingCodeID);
        }
        return results;
    }

}
