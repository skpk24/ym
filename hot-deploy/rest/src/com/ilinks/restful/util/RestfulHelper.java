package com.ilinks.restful.util;

import net.sf.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class RestfulHelper{
    private static String MODULE =  RestfulHelper.class.getName();
    
    public static String createJSONString(Map<Object, Object> map, String callbackFunction){
        String jsonStr = "";
        JSONObject json = JSONObject.fromObject(map);
        jsonStr = json.toString();
        if(callbackFunction != null){
            jsonStr = callbackFunction + "(" + jsonStr + ")";
        }
        return jsonStr;
    }

    public static void removeAttribute(GenericValue genericValue, List<String> attribute){
        for(String value : attribute){
            genericValue.remove(value);
        }
    }
    public static void removeAttribute(List<GenericValue> genericList, List<String> attribute){
        for(GenericValue value : genericList){
            removeAttribute(value, attribute);
        }
    }
    public static void keepAttribute(List<GenericValue> genericList, List<String> attribute){
        for(GenericValue value : genericList){
            keepAttribute(value, attribute);
        }
    }
    public static void keepAttribute(GenericValue genericValue, List<String> attribute){
        for(String value : attribute){
            genericValue.remove(value);
        }
    }
    public static Map<String,Object>  createAddress(FastMap<String,Object> addressMap, Map<String,Object> phoneMap, String productStore, GenericDelegator delegator, LocalDispatcher dispatcher){
        Map<String,Object> result  = FastMap.newInstance();
        try{
            boolean beganTransaction = TransactionUtil.begin();
            result = dispatcher.runSync("createPostalAddressAndPurposes", addressMap);
            if (ServiceUtil.isSuccess(result)){
                String contactMechID = (String) result.get("contactMechId");
                result = dispatcher.runSync("createPartyTelecomNumber", phoneMap);
                if (ServiceUtil.isSuccess(result)){
                    result = dispatcher.runSync("createContactMechLink",UtilMisc.toMap("contactMechIdFrom", contactMechID,"userLogin", (GenericValue)addressMap.get("userLogin"),
                            "contactMechIdTo", result.get("contactMechId")));
                    if(ServiceUtil.isSuccess(result)){
                        TransactionUtil.commit(beganTransaction);
                    }
                }
                result.put("contactMechID", contactMechID);
            }
        }
        catch(Exception e){
            result.put(ModelService.RESPONSE_MESSAGE, "error");
            result.put(ModelService.ERROR_MESSAGE, e.getMessage());
            Debug.logError(e.getMessage(), MODULE);   
        }
        finally{
            rollBack();
        }
        return result;
    }
    public static GenericValue getSystemLogin(GenericDelegator delegator) throws Exception{
       return delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "system"));
    }
    public static void rollBack(){
        try{
            if( TransactionUtil.getStatus() != TransactionUtil.STATUS_NO_TRANSACTION){ 
                TransactionUtil.rollback();
            }
        }
        catch (Exception e){
            Debug.logError("Unable To rollback Transaction " + e.getMessage(), MODULE);   
        }
    }
    public static String formatDate(Timestamp date, TimeZone zone, Locale locale){
        return UtilDateTime.timeStampToString(date, "MMMMM d, yyyy",zone,locale);
    }
    public static String formatDate(Timestamp date){
        return formatDate(date,TimeZone.getDefault(), Locale.US);
    }
}