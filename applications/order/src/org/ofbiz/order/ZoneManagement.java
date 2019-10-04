package org.ofbiz.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

public class ZoneManagement{
	
	/*
	 * @author Ajaya 
	 * Creating Zone Group
	 * @param request,response
	 * @return success/failure
	 */
	
	public static String createZoneGroup(HttpServletRequest request, HttpServletResponse response){
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        if(UtilValidate.isEmpty(userLogin))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
        	return "error";
        }
        
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        
        String zoneGroupName=request.getParameter("zoneGroupName");
        String fromDate=request.getParameter("fromDate");
        String thruDate=request.getParameter("thruDate");

        GenericValue zoneGroup = delegator.makeValue("ZoneGroup");
        zoneGroup.put("zoneGroupId", delegator.getNextSeqId("ZoneGoup"));
        zoneGroup.put("zoneGroupName", zoneGroupName);
        zoneGroup.put("createdDate", UtilDateTime.nowTimestamp());
        zoneGroup.put("createdBy", userLogin.getString("userLoginId"));
        zoneGroup.put("updatedBy", userLogin.getString("userLoginId"));
        zoneGroup.put("updatedDate", UtilDateTime.nowTimestamp());
        
        if(UtilValidate.isNotEmpty(fromDate))
        	zoneGroup.put("fromDate", Timestamp.valueOf(fromDate));
        else
        	zoneGroup.put("fromDate", UtilDateTime.nowTimestamp());
        
        if(UtilValidate.isNotEmpty(thruDate))
        	zoneGroup.put("thruDate", Timestamp.valueOf(thruDate));
        
        try {
			delegator.create(zoneGroup);
			request.setAttribute("_ENENT_MESSAGE_", "Zone Group "+zoneGroupName+" Created Successfully");
			return "success";
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Zone Group "+zoneGroupName);
			return "error";
		}
    }
	
	/*
	 * @author Ajaya
	 * Updating Zone Group
	 * @param request,response
	 * @return success/failure
	 * 
	 */
	
	public static String updateZoneGroup(HttpServletRequest request, HttpServletResponse response) {
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        if(UtilValidate.isEmpty(userLogin))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
        	return "error";
        }
        Delegator delegator = (Delegator) request.getAttribute("delegator");
      
        String zoneGroupId = request.getParameter("zoneGroupId");
        if(UtilValidate.isEmpty(zoneGroupId))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Some Error Occured");
        	return "error";
        }
        
        String zoneGroupName=request.getParameter("zoneGroupName");
        String fromDate=request.getParameter("fromDate");
        String thruDate=request.getParameter("thruDate");
        
        GenericValue zoneGroup = null;
		try {
			zoneGroup = delegator.findByPrimaryKey("ZoneGroup", UtilMisc.toMap("zoneGroupId",zoneGroupId));
			if(UtilValidate.isEmpty(zoneGroup))
			{
				request.setAttribute("_ERROR_MESSAGE_", "Some Error Occured . ZoneGroupId : "+zoneGroupId+" Not Found");
	        	return "error";
			}
			zoneGroup.put("zoneGroupName", zoneGroupName);
			zoneGroup.put("createdDate", UtilDateTime.nowTimestamp());
			zoneGroup.put("createdBy", userLogin.getString("userLoginId"));
	        
	        if(UtilValidate.isNotEmpty(fromDate))
	        	zoneGroup.put("fromDate", Timestamp.valueOf(fromDate));
	        else
	        	zoneGroup.put("fromDate", UtilDateTime.nowTimestamp());
	        
	        if(UtilValidate.isNotEmpty(thruDate))
	        	zoneGroup.put("thruDate", Timestamp.valueOf(thruDate));
	        else
	        	zoneGroup.put("thruDate", null);
			
	        zoneGroup.put("updatedBy", userLogin.getString("userLoginId"));
	        zoneGroup.put("updatedDate", UtilDateTime.nowTimestamp());
	        
	        zoneGroup.store();
	        
	        request.setAttribute("_ENENT_MESSAGE_", "Zone Group "+zoneGroupName+" Updated Successfully");
			return "success";
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "Failed To Update Zone Group "+zoneGroupName);
			return "error";
		}
    }
	
	/*
	 * @author Ajaya
	 * Creating Zone
	 * @param request,response
	 * @return success/failure
	 */
	
	public static String createZone(HttpServletRequest request, HttpServletResponse response){
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        if(UtilValidate.isEmpty(userLogin))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
        	return "error";
        }
        
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        
        String zoneName=request.getParameter("zoneName");
        String pinCode=request.getParameter("pinCode");
        String zoneGroupId=request.getParameter("zoneGroupId");
        String fromDate=request.getParameter("fromDate");
        String thruDate=request.getParameter("thruDate");
        
        GenericValue zoneType = delegator.makeValue("ZoneType");
        zoneType.put("zoneId", delegator.getNextSeqId("ZoneType"));
        zoneType.put("zoneName", zoneName);
        zoneType.put("pinCode", pinCode);
        zoneType.put("zoneGroupId", zoneGroupId);
        zoneType.put("zoneGroupName", zoneGroupName(delegator, zoneGroupId));
        zoneType.put("createdDate", UtilDateTime.nowTimestamp());
        zoneType.put("createdBy", userLogin.getString("userLoginId"));
        zoneType.put("updatedBy", userLogin.getString("userLoginId"));
        zoneType.put("updatedDate", UtilDateTime.nowTimestamp());
        
        if(UtilValidate.isNotEmpty(fromDate))
        	zoneType.put("fromDate", Timestamp.valueOf(fromDate));
        else
        	zoneType.put("fromDate", UtilDateTime.nowTimestamp());
        
        if(UtilValidate.isNotEmpty(thruDate))
        	zoneType.put("thruDate", Timestamp.valueOf(thruDate));
        
        try {
			delegator.create(zoneType);
			request.setAttribute("_ENENT_MESSAGE_", "Zone "+zoneName+" Created Successfully");
			return "success";
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "Failed To Create Zone "+zoneName);
			return "error";
		}
    }
	
	/*
	 * @author Ajaya
	 * Updating Zone
	 * @param request,response
	 * @return success/failure
	 * 
	 */
	
	public static String updateZone(HttpServletRequest request, HttpServletResponse response) {
		
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        if(UtilValidate.isEmpty(userLogin))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Login Please");
        	return "error";
        }
        Delegator delegator = (Delegator) request.getAttribute("delegator");
      
        String zoneId=request.getParameter("zoneId");
        if(UtilValidate.isEmpty(zoneId))
        {
        	request.setAttribute("_ERROR_MESSAGE_", "Some Error Occured");
        	return "error";
        }
        String zoneName=request.getParameter("zoneName");
        String pinCode=request.getParameter("pinCode");
        String zoneGroupId=request.getParameter("zoneGroupId");
        String fromDate=request.getParameter("fromDate");
        String thruDate=request.getParameter("thruDate");
        
        GenericValue zoneType;
		try {
			zoneType = delegator.findByPrimaryKey("ZoneType", UtilMisc.toMap("zoneId",zoneId));
			if(UtilValidate.isEmpty(zoneType))
			{
				request.setAttribute("_ERROR_MESSAGE_", "Some Error Occured . ZoneId : "+zoneId+" Not Found");
	        	return "error";
			}
	        zoneType.put("zoneName", zoneName);
	        zoneType.put("pinCode", pinCode);
	        zoneType.put("zoneGroupId", zoneGroupId);
	        zoneType.put("zoneGroupName", zoneGroupName(delegator, zoneGroupId));
	        zoneType.put("updatedBy", userLogin.getString("userLoginId"));
	        zoneType.put("updatedDate", UtilDateTime.nowTimestamp());
	        
	        if(UtilValidate.isNotEmpty(fromDate))
	        	zoneType.put("fromDate", Timestamp.valueOf(fromDate));
	        else
	        	zoneType.put("fromDate", UtilDateTime.nowTimestamp());
	        
	        if(UtilValidate.isNotEmpty(thruDate))
	        	zoneType.put("thruDate", Timestamp.valueOf(thruDate));
	        else
	            zoneType.put("thruDate", null);
	        
	        zoneType.store();
	        
	        request.setAttribute("_ENENT_MESSAGE_", "Zone "+zoneName+" Updated Successfully");
			return "success";
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.setAttribute("_ERROR_MESSAGE_", "Failed To Update Zone "+zoneName);
			return "error";
		}
    }
	
	public static String zoneGroupName(Delegator delegator, String zoneGroupId){
		String zoneGroupName = null;
        if(UtilValidate.isNotEmpty(zoneGroupId))
        	{
        		GenericValue zoneGroup = null;
				try {
					zoneGroup = delegator.findByPrimaryKey("ZoneGroup", UtilMisc.toMap("zoneGroupId",zoneGroupId));
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		if(UtilValidate.isNotEmpty(zoneGroup))
        			return zoneGroup.getString("zoneGroupName");
        	}
        return zoneGroupName;
	}
	
	public static GenericValue getPinCodeDetails(Delegator delegator, String pinCode){
        if(UtilValidate.isNotEmpty(pinCode))
        	{
        		List<GenericValue> zoneList = null;
				try {
					List fromDateCond = new ArrayList();
			    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.EQUALS,null));
			    	fromDateCond.add(EntityCondition.makeCondition("fromDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
			    	
			    	List thruDateCond = new ArrayList();
			    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null));
			    	thruDateCond.add(EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.nowTimestamp()));
			    	
			    	List condition = new ArrayList();
			    	condition.add(EntityCondition.makeCondition(fromDateCond,EntityOperator.OR));
			    	condition.add(EntityCondition.makeCondition(thruDateCond,EntityOperator.OR));
			    	condition.add(EntityCondition.makeCondition("pinCode",EntityOperator.EQUALS,pinCode));
			    	
					zoneList = delegator.findList("ZoneType", EntityCondition.makeCondition(condition,EntityOperator.AND),null,null,null,true);
					
					if(UtilValidate.isNotEmpty(zoneList))
						return EntityUtil.getFirst(zoneList);
					
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
        	}
        return null;
	}
}