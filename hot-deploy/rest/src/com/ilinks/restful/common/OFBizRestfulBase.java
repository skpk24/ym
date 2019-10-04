package com.ilinks.restful.common;


import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import javolution.util.FastMap;

import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilProperties;

public abstract class OFBizRestfulBase{
    public GenericDelegator delegator = null;
    public LocalDispatcher dispatcher = null;
    public Map<Object, Object> jsonMap = FastMap.newInstance();
    public String jsonStr  = null;
    public String MODULE = null;
    public EntityCondition entityCondition = null;
    public Properties properties = null;
    public final String SUCCESS;
    public final String ERROR;
    public final String RESULT;
    public final String RESPONSE_MESSAGE;
    @Context
    public HttpServletRequest request;
    @Context
    public HttpServletResponse response;
    
    public OFBizRestfulBase(){
        properties = UtilProperties.getProperties(Constant.PROPERTY_FILE_NAME);
        SUCCESS =  properties.getProperty( "SUCCESS",  "success");
        ERROR = properties.getProperty( "ERROR",  "error");
        RESULT = properties.getProperty( "RESULT",  "result");
        RESPONSE_MESSAGE = properties.getProperty( "RESPONSE_MESSAGE",  "responseMessage");
    }
    public GenericDelegator getDelegator(){
        return this.getDelegator("default");
    }
    public  GenericDelegator getDelegator(String delegatorName){
        return (GenericDelegator)DelegatorFactory.getDelegator(delegatorName);
    }

    public LocalDispatcher getDispatcher(){
        return this.getDispatcher("default",this.delegator);
    }
    
    public LocalDispatcher getDispatcher(String delegatorName, GenericDelegator delegator){
        return GenericDispatcher.getLocalDispatcher("default",delegator);
    }
    
    public void initRequestAndDelegator(){
        if(request.getAttribute("delegator") == null){
            request.setAttribute("delegator", delegator);
        }
        if(request.getAttribute("dispatcher") == null){
            request.setAttribute("dispatcher", dispatcher);
        }
    }
}