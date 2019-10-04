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
package org.ofbiz.content.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * DataEvents Class
 */
public class DataEvents {

    public static final String module = DataEvents.class.getName();
    public static final String err_resource = "ContentErrorUiLabels";

    public static String uploadImage(HttpServletRequest request, HttpServletResponse response) {
        return DataResourceWorker.uploadAndStoreImage(request, "dataResourceId", "imageData");
    }

    /** Streams any binary content data to the browser */
    public static String serveObjectData(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();

        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        String userAgent = request.getHeader("User-Agent");

        Map<String, Object> httpParams = UtilHttp.getParameterMap(request);
        String contentId = (String) httpParams.get("contentId");
        if (UtilValidate.isEmpty(contentId)) {
            String errorMsg = "Required parameter contentId not found!";
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        // get the permission service required for streaming data; default is always the genericContentPermission
        String permissionService = UtilProperties.getPropertyValue("content.properties", "stream.permission.service", "genericContentPermission");

        // get the content record
        GenericValue content;
        try {
            content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        // make sure content exists
        if (content == null) {
            String errorMsg = "No content found for Content ID: " + contentId;
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        // make sure there is a DataResource for this content
        String dataResourceId = content.getString("dataResourceId");
        if (UtilValidate.isEmpty(dataResourceId)) {
            String errorMsg = "No Data Resource found for Content ID: " + contentId;
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        // get the data resource
        GenericValue dataResource;
        try {
            dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        // make sure the data resource exists
        if (dataResource == null) {
            String errorMsg = "No Data Resource found for ID: " + dataResourceId;
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        // see if data resource is public or not
        String isPublic = dataResource.getString("isPublic");
        if (UtilValidate.isEmpty(isPublic)) {
            isPublic = "N";
        }

        // not public check security
        if (!"Y".equalsIgnoreCase(isPublic)) {
            // do security check
            Map<String, Object> permSvcCtx = UtilMisc.toMap("userLogin", userLogin, "mainAction", "VIEW", "contentId", contentId);
            Map<String, Object> permSvcResp;
            try {
                permSvcResp = dispatcher.runSync(permissionService, permSvcCtx);
            } catch (GenericServiceException e) {
                Debug.logError(e, module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
            if (ServiceUtil.isError(permSvcResp)) {
                String errorMsg = ServiceUtil.getErrorMessage(permSvcResp);
                Debug.logError(errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                return "error";
            }

            // no service errors; now check the actual response
            Boolean hasPermission = (Boolean) permSvcResp.get("hasPermission");
            if (!hasPermission.booleanValue()) {
                String errorMsg = (String) permSvcResp.get("failMessage");
                Debug.logError(errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                return "error";
            }
        }

        // get objects needed for data processing
        String contextRoot = (String) request.getAttribute("_CONTEXT_ROOT_");
        String webSiteId = (String) session.getAttribute("webSiteId");
        String dataName = dataResource.getString("dataResourceName");
        Locale locale = UtilHttp.getLocale(request);

        // get the mime type
        String mimeType = DataResourceWorker.getMimeType(dataResource);

        // hack for IE and mime types
        if (userAgent.indexOf("MSIE") > -1) {
            Debug.log("Found MSIE changing mime type from - " + mimeType, module);
            mimeType = "application/octet-stream";
        }

        // for local resources; use HTTPS if we are requested via HTTPS
        String https = "false";
        String protocol = request.getProtocol();
        if ("https".equalsIgnoreCase(protocol)) {
            https = "true";
        }

        // get the data resource stream and conent length
        Map<String, Object> resourceData;
        try {
            resourceData = DataResourceWorker.getDataResourceStream(dataResource, https, webSiteId, locale, contextRoot, false);
        } catch (IOException e) {
            Debug.logError(e, "Error getting DataResource stream", module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        } catch (GeneralException e) {
            Debug.logError(e, "Error getting DataResource stream", module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }

        // get the stream data
        InputStream stream = null;
        Long length = null;

        if (resourceData != null) {
            stream = (InputStream) resourceData.get("stream");
            length = (Long) resourceData.get("length");
        }
        Debug.log("Got resource data stream: " + length + " bytes", module);

        // stream the content to the browser
        if (stream != null && length != null) {
            try {
                UtilHttp.streamContentToBrowser(response, stream, length.intValue(), mimeType, dataName);
            } catch (IOException e) {
                Debug.logError(e, "Unable to write content to browser", module);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
        } else {
            String errorMsg = "No data is available.";
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        return "success";
    }

    /** Streams ImageDataResource data to the output. */
    // TODO: remove this method in favor of serveObjectData
    public static String serveImage(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext application = session.getServletContext();

        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);

        Debug.log("Img UserAgent - " + request.getHeader("User-Agent"), module);

        String dataResourceId = (String) parameters.get("imgId");
        if (UtilValidate.isEmpty(dataResourceId)) {
            String errorMsg = "Error getting image record from db: " + " dataResourceId is empty";
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        try {
            GenericValue dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
            if (!"Y".equals(dataResource.getString("isPublic"))) {
                // now require login...
                GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
                if (userLogin == null) {
                    String errorMsg = "You must be logged in to download the Data Resource with ID [" + dataResourceId + "]";
                    Debug.logError(errorMsg, module);
                    request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                    return "error";
                }

                // make sure the logged in user can download this content; otherwise is a pretty big security hole for DataResource records...
                // TODO: should we restrict the roleTypeId?
                List<GenericValue> contentAndRoleList = delegator.findByAnd("ContentAndRole",
                        UtilMisc.toMap("partyId", userLogin.get("partyId"), "dataResourceId", dataResourceId));
                if (contentAndRoleList.size() == 0) {
                    String errorMsg = "You do not have permission to download the Data Resource with ID [" + dataResourceId + "], ie you are not associated with it.";
                    Debug.logError(errorMsg, module);
                    request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                    return "error";
                }
            }

            String mimeType = DataResourceWorker.getMimeType(dataResource);

            // hack for IE and mime types
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.indexOf("MSIE") > -1) {
                Debug.log("Found MSIE changing mime type from - " + mimeType, module);
                mimeType = "application/octet-stream";
            }

            if (mimeType != null) {
                response.setContentType(mimeType);
            }
            OutputStream os = response.getOutputStream();
            DataResourceWorker.streamDataResource(os, delegator, dataResourceId, "", application.getInitParameter("webSiteId"), UtilHttp.getLocale(request), application.getRealPath("/"));
            os.flush();
        } catch (GenericEntityException e) {
            String errMsg = "Error downloading digital product content: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        } catch (GeneralException e) {
            String errMsg = "Error downloading digital product content: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        } catch (IOException e) {
            String errMsg = "Error downloading digital product content: " + e.toString();
            Debug.logError(e, errMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }

        return "success";
    }


    /** Dual create and edit event.
     *  Needed to make permission criteria available to services.
     */
    public static String persistDataResource(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> result = null;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String dataResourceId = (String)paramMap.get("dataResourceId");
        GenericValue dataResource = delegator.makeValue("DataResource");
        dataResource.setPKFields(paramMap);
        dataResource.setNonPKFields(paramMap);
        Map<String, Object> serviceInMap = UtilMisc.makeMapWritable(dataResource);
        serviceInMap.put("userLogin", userLogin);
        String mode = (String)paramMap.get("mode");
        Locale locale = UtilHttp.getLocale(request);

        if (mode != null && mode.equals("UPDATE")) {
            try {
                result = dispatcher.runSync("updateDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_call_update_service", locale);
                String errorMsg = "Error calling the updateDataResource service." + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                return "error";
            }
        } else {
            mode = "CREATE";
            try {
                result = dispatcher.runSync("createDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_call_create_service", locale);
                String errorMsg = "Error calling the createDataResource service." + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                return "error";
            }
            dataResourceId = (String)result.get("dataResourceId");
            dataResource.set("dataResourceId", dataResourceId);
        }

        String returnStr = "success";
        if (mode.equals("CREATE")) {
            // Set up return message to guide selection of follow on view
            request.setAttribute("dataResourceId", result.get("dataResourceId"));
            String dataResourceTypeId = (String)serviceInMap.get("dataResourceTypeId");
            if (dataResourceTypeId != null) {
                 if (dataResourceTypeId.equals("ELECTRONIC_TEXT")
                     || dataResourceTypeId.equals("IMAGE_OBJECT")) {
                    returnStr = dataResourceTypeId;
                 }
            }
        }

        return returnStr;
    }
    
    public static String createContentMethodType(HttpServletRequest request, HttpServletResponse response) {
        Map result = null;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map paramMap = UtilHttp.getParameterMap(request);
        String webSiteId = request.getParameter("webSiteId");
        
        String localeString = request.getParameter("localeString");
        
        String webSiteContentTypeId = request.getParameter("webSiteContentTypeId");
        
        String description = request.getParameter("description");

        String textData = request.getParameter("textData");

        String dataResourceTypeId = request.getParameter("dataResourceTypeId");

        String dataTemplateTypeId = request.getParameter("dataTemplateTypeId");
        
        String mimeTypeId = request.getParameter("mimeTypeId");
        
        String contentName = request.getParameter("contentName");
        
        String contentId = request.getParameter("contentId");
        
        String fromDate = request.getParameter("fromDate");
        if(UtilValidate.isEmpty(fromDate))
        {
        	fromDate=UtilDateTime.nowTimestamp().toString();
        }
        
        String thruDate = request.getParameter("thruDate");
        try
        {
        	if(textData != null)
    
        	{
        		DataEvents.persistDataResource(request,response);
        		
        		String dataResourceId =(String)request.getAttribute("dataResourceId");
        		Map serviceContent = UtilMisc.toMap("fromDate",fromDate,"dataResourceId",dataResourceId,"contentName",contentName,"localeString", localeString,"mimeTypeId",mimeTypeId,"userLogin",userLogin);
        		
        		Map result1 = dispatcher.runSync("createContent", serviceContent);
        		contentId = (String)result1.get("contentId");
        		GenericValue gv;
        		GenericValue gv1;
        		if(UtilValidate.isEmpty(thruDate))
                {
        			gv = delegator.makeValue("WebSiteContent",UtilMisc.toMap("webSiteId", webSiteId,"fromDate",Timestamp.valueOf(fromDate), "contentId", contentId, "webSiteContentTypeId", webSiteContentTypeId));
                }
        		else
                {
                	gv = delegator.makeValue("WebSiteContent",UtilMisc.toMap("webSiteId", webSiteId,"thruDate",Timestamp.valueOf(thruDate),"fromDate",Timestamp.valueOf(fromDate), "contentId", contentId, "webSiteContentTypeId", webSiteContentTypeId));
                }
        		
        		gv1 = delegator.makeValue("ElectronicText", UtilMisc.toMap("dataResourceId",dataResourceId,"textData", textData));
        		
        		gv.create();
        		
        		gv1.create();
        	}
        
        }
    	catch (GenericEntityException e) {
            
            String errorMsg = "Error calling the createDataResource service." + e.toString();
            Debug.logError(e, errorMsg, module);
           
            return "error";
        }
catch (GenericServiceException e) {
            
            String errorMsg = "Error calling the createDataResource service." + e.toString();
            Debug.logError(e, errorMsg, module);
           
            return "error";
        }

        
        return "success";

    }
    
    public static String deleteBanners(HttpServletRequest request, HttpServletResponse response) 
    {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        List deletedBanners = new ArrayList();
        String contentId = request.getParameter("contentId");
        String dataResourceId  = request.getParameter("dataResourceId");
        PrintWriter out=null;
        String returnValue = "error";
        try{ 
        	out = response.getWriter();
        	}
        catch (Exception e) 
	        {
				e.printStackTrace();
				out.print("error");
				return returnValue;
			}
        
        if(UtilValidate.isEmpty(contentId) || UtilValidate.isEmpty(dataResourceId))
        {
        	out.print("error");
        	return returnValue;
        }
        try
        {
        	EntityWhereString ews_contentId = EntityWhereString.makeConditionWhere("CONTENT_ID IN ("+contentId+")");
            EntityWhereString ews_dataResourceId = EntityWhereString.makeConditionWhere("DATA_RESOURCE_ID IN ("+dataResourceId+")");
        	delegator.removeByCondition("WebSiteContent", ews_contentId.freeze());
        	delegator.removeByCondition("ElectronicText", ews_dataResourceId.freeze());
        	delegator.removeByCondition("ContentRole", ews_contentId.freeze());
        	delegator.removeByCondition("Content", ews_contentId.freeze());
        	out.print("success");
        	returnValue = "success";
        	return returnValue;
        }
    	catch (Exception e)
    	{
            String errorMsg = "Unable to delete " + e.toString();
            Debug.logError(e, errorMsg, module);
            out.print("error");
        	return returnValue;
        }
    	
    }
    
    public static String updateBanner(HttpServletRequest request, HttpServletResponse response) 
    {
    	String columnName = request.getParameter("columnName");
    	String contentValue = request.getParameter("contentNameValue");
    	String contentId = request.getParameter("contentId");
    	String tableName = request.getParameter("tableName");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HashMap fieldToSet = new HashMap();
		PrintWriter out=null;
		String returnValue = "error";
        try
	        { 
	        	out = response.getWriter();
	        }
        catch (Exception e) 
	        {
        		e.printStackTrace();
	        	out.print("error");
	        	return returnValue;
			}
        if( UtilValidate.isEmpty(columnName) ||UtilValidate.isEmpty(contentValue) || UtilValidate.isEmpty(contentId))
        {
        	out.print("error");
        	return returnValue;
        }
		else
			{
				fieldToSet.put(columnName, contentValue);
			}
		try
		    {
				EntityCondition ew = EntityCondition.makeCondition("contentId",EntityOperator.EQUALS,contentId);
				int rowsAffected = delegator.storeByCondition(tableName, fieldToSet, ew);
				
				if(rowsAffected > 0)
					{
						returnValue = "success";
						out.print("success");
						return returnValue;
					}
						
		    }
	    catch (Exception e)
		    {
		      e.printStackTrace();
		      out.print("error");
		      return returnValue;
		    }
	    returnValue = "error";
	    return returnValue;
    }
    
    public static String updateStatus(HttpServletRequest request, HttpServletResponse response) 
    {
    	String contentId = request.getParameter("contentId");
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HashMap fieldToSet = new HashMap();
		PrintWriter out=null;
		String returnValue = "error";
		Timestamp thruDate = null ;
        try
	        { 
	        	out = response.getWriter();
	        }
        catch (Exception e) 
	        {
        		e.printStackTrace();
	        	out.print("error");
	        	return returnValue;
			}
        if(UtilValidate.isEmpty(contentId))
        {
        	out.print("error");
        	return returnValue;
        }
		else
			{
			java.util.Date date= new java.util.Date();
			thruDate = new Timestamp(date.getTime());
			fieldToSet.put("thruDate", thruDate);
			}
		try
		    {
				EntityCondition ew = EntityCondition.makeCondition("contentId",EntityOperator.EQUALS,contentId);
				int rowsAffected = delegator.storeByCondition("WebSiteContent", fieldToSet, ew);
				if(rowsAffected > 0)
					{
						returnValue = "success";
						out.print("success_"+thruDate);
						return returnValue;
					}
						
		    }
	    catch (Exception e)
		    {
		      e.printStackTrace();
		      out.print("error");
		      return returnValue;
		    }
	    returnValue = "error";
	    return returnValue;
    }
}
