package org.ofbiz.sync;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

public class Sync {

	  public static String syncPull(HttpServletRequest request,HttpServletResponse response) {
		  String posTerminalId=request.getParameter("posTerminalId"); 
		  String frequency=request.getParameter("frequency"); 
		  String Interval=request.getParameter("Interval"); 
		  String countNumber=request.getParameter("countNumber"); 

		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
		  Date date = new Date();
		  Integer intDay = new Integer(date.getDate());
		  Integer intMonth = new Integer(date.getMonth()+1);
		  Integer intYear = new Integer(date.getYear()+1900);
		  String  day = intDay.toString();
		  String  month = intMonth.toString();
		  String  year = intYear.toString();
		  GenericValue RecurrenceRule=null;
		  GenericValue RecurrenceRuleInfo=null;
		  GenericValue JobSandBox = null;
		  GenericValue runTimeData = null;
          String startTime=month + "/" + day + "/" + year + " " + "00:00:00";
          
          String pullId = posTerminalId+"PULL";
		  try{
			  blankDbForNewJobs(pullId,delegator);
			  RecurrenceRule=delegator.makeValue("RecurrenceRule");
			  RecurrenceRuleInfo=delegator.makeValue("RecurrenceInfo");
			  
			  RecurrenceRule.set("recurrenceRuleId", pullId);
			  RecurrenceRule.set("frequency",frequency);
			  if(UtilValidate.isNotEmpty(Interval))
				  RecurrenceRule.set("intervalNumber",Long.parseLong(Interval));
			  else
				  RecurrenceRule.set("intervalNumber",new Long(0));
			  
			  if(UtilValidate.isNotEmpty(countNumber))
				  RecurrenceRule.set("countNumber", Long.parseLong(countNumber));
			  else
				  RecurrenceRule.set("countNumber",new Long(0));
			  
			  RecurrenceRuleInfo.set("recurrenceInfoId", pullId);
			  RecurrenceRuleInfo.set("startDateTime",UtilDateTime.nowTimestamp());
			  RecurrenceRuleInfo.set("recurrenceRuleId",pullId);
			  RecurrenceRuleInfo.set("recurrenceCount", new Long(0));
			  
			  RecurrenceRule.create();
			  RecurrenceRuleInfo.create();
		    
			  runTimeData=delegator.makeValue("RuntimeData");
			  runTimeData.set("runtimeDataId",pullId);
			  String runtimeInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ofbiz-ser><map-HashMap><map-Entry><map-Key>"+
			  "<std-String value=\"entitySyncId\"/></map-Key><map-Value><std-String value=\"5500\"/></map-Value></map-Entry>"+
              "<map-Entry><map-Key><std-String value=\"remotePullAndReportEntitySyncDataName\"/></map-Key>"+
              "<map-Value><std-String value=\"remotePullAndReportEntitySyncDataHttp\"/></map-Value></map-Entry></map-HashMap>"+
              "</ofbiz-ser>";
			  
			  runTimeData.set("runtimeInfo",runtimeInfo);
			  runTimeData.create();
			  
			  
	    	  JobSandBox=delegator.makeValue("JobSandbox");
	    	  JobSandBox.set("jobId",pullId);
	    	  JobSandBox.set("jobName","Pull Data from MCS");
	    	  JobSandBox.set("runtimeDataId",pullId);
	    	  JobSandBox.set("runTime",UtilDateTime.nowTimestamp());
	    	  JobSandBox.set("serviceName","runPullEntitySync");
	    	  JobSandBox.set("poolId","pool");
	    	  JobSandBox.set("runAsUser","system");
	    	  JobSandBox.set("recurrenceInfoId",pullId);
	    	  JobSandBox.create();
		  }		 
		  catch (Exception e) {
			e.printStackTrace();
		 }
	    return "success";
	  }
	  
	  public static String syncPush(HttpServletRequest request,HttpServletResponse response) {
		
		  String posTerminalId=request.getParameter("posTerminalId");
		  String frequency=request.getParameter("frequency"); 
		  String Interval=request.getParameter("Interval"); 
		  String countNumber=request.getParameter("countNumber"); 

		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator"); 
		  Date date = new Date();
		  Integer intDay = new Integer(date.getDate());
		  Integer intMonth = new Integer(date.getMonth()+1);
		  Integer intYear = new Integer(date.getYear()+1900);
		  String  day = intDay.toString();
		  String  month = intMonth.toString();
		  String  year = intYear.toString();
		  GenericValue RecurrenceRule=null;
		  GenericValue RecurrenceRuleInfo=null;
		  GenericValue JobSandBox= null;
		  GenericValue runTimeData= null;
          String startTime=month + "/" + day + "/" + year + " " + "00:00:00";
          
          String pullId = posTerminalId+"PUSH";
		  try{
			  blankDbForNewJobs(pullId,delegator);
			  RecurrenceRule=delegator.makeValue("RecurrenceRule");
			  RecurrenceRuleInfo=delegator.makeValue("RecurrenceInfo");
			  
			  RecurrenceRule.set("recurrenceRuleId", pullId);
			  RecurrenceRule.set("frequency",frequency);
			  if(UtilValidate.isNotEmpty(Interval))
				  RecurrenceRule.set("intervalNumber",Long.parseLong(Interval));
			  else
				  RecurrenceRule.set("intervalNumber",new Long(0));
			  
			  if(UtilValidate.isNotEmpty(countNumber))
				  RecurrenceRule.set("countNumber", Long.parseLong(countNumber));
			  else
				  RecurrenceRule.set("countNumber",new Long(0));
			  
			  RecurrenceRuleInfo.set("recurrenceInfoId", pullId);
			  RecurrenceRuleInfo.set("startDateTime",UtilDateTime.nowTimestamp());
			  RecurrenceRuleInfo.set("recurrenceRuleId",pullId);
			  RecurrenceRuleInfo.set("recurrenceCount", new Long(0));
			  
			  RecurrenceRule.create();
			  RecurrenceRuleInfo.create();
		    
			  runTimeData=delegator.makeValue("RuntimeData");
			  runTimeData.set("runtimeDataId",pullId);
			  String runtimeInfo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ofbiz-ser><map-HashMap><map-Entry><map-Key>"+
                                   "<std-String value=\"entitySyncId\"/></map-Key><map-Value><std-String value=\"5506\"/>"+
                                   "</map-Value></map-Entry></map-HashMap></ofbiz-ser>";
			  
			  runTimeData.set("runtimeInfo",runtimeInfo);
			  runTimeData.create();
			  
	    	  JobSandBox=delegator.makeValue("JobSandbox");
	    	  JobSandBox.set("jobId",pullId);
	    	  JobSandBox.set("jobName","Push POS Data to MCS");
	    	  JobSandBox.set("runtimeDataId",pullId);
	    	  JobSandBox.set("runTime",UtilDateTime.nowTimestamp());
	    	  JobSandBox.set("serviceName","runEntitySync");
	    	  JobSandBox.set("poolId","pool");
	    	  JobSandBox.set("runAsUser","system");
	    	  JobSandBox.set("recurrenceInfoId",pullId);
	    	  JobSandBox.create();
		  }		 
		  catch (Exception e) {
			e.printStackTrace();
		 }
	    return "success";
	  }
	  
	  public static String sync(HttpServletRequest request,HttpServletResponse response) {

		  GenericValue posTerminal = null;
		  
		  String frequency=request.getParameter("frequency"); 
		  String interval=request.getParameter("Interval"); 
		  String countNumber=request.getParameter("countNumber");
		  String posTerminalIp=request.getParameter("posTerminalIp"); 
		  String posTerminalId = request.getParameter("posTerminalId");
		  String syncType  = request.getParameter("syncType");
		  
		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
		  
		  if(UtilValidate.isEmpty(syncType)) syncType = "PUSH";
		  
		  try {
			  if(UtilValidate.isNotEmpty(posTerminalId))
			  {
				  posTerminal= delegator.makeValue("PosTerminal");
				  posTerminal.set("posTerminalId",posTerminalId);
				  posTerminal.set("posTerminalIp",posTerminalIp);
				  posTerminal.store();
			  }
			posTerminal = delegator.findOne("PosTerminal",UtilMisc.toMap("posTerminalId",posTerminalId),false);
		
			if(UtilValidate.isNotEmpty(posTerminal))
			{
				posTerminalIp = posTerminal.getString("posTerminalIp");
				String posTerminalID = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
				
				if(posTerminalID.equals(posTerminalId))
				{
					if(syncType.equals("PUSH"))syncPush(request, response);
					else syncPull(request, response);
				}
				else if(UtilValidate.isNotEmpty(posTerminalIp)){
				URL pageURL = null;
				if(syncType.equals("PUSH")){
					pageURL = new URL("http://"+posTerminalIp+"/facility/control/syncPull?posTerminalId="+posTerminalId+"&frequency="+frequency+
						"&Interval="+interval+"&countNumber="+countNumber);
				}else{
					pageURL = new URL("http://"+posTerminalIp+"/facility/control/syncPush?posTerminalId="+posTerminalId+"&frequency"+frequency+
							"&Interval"+interval+"&countNumber"+countNumber);
					}
				
				HttpURLConnection urlConnection = (HttpURLConnection) pageURL.openConnection();
				urlConnection.connect();
				urlConnection.getInputStream();
				}
			}
			else{
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  catch (MalformedURLException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
	      return "success";
	  }
	  
	  public static String sendMessage(HttpServletRequest request,HttpServletResponse response) {
	    String fromfacility=request.getParameter("fromfacility");
	    String tofacility=request.getParameter("tofacility");
	    String nextAction=request.getParameter("nextAction");
	    String incommingAction=request.getParameter("incommingAction");
	    String status=request.getParameter("status");
	    String comment=request.getParameter("comment");
	    String  mcsUrl=UtilProperties.getPropertyValue("general","pos.mcs.url");
	    try{
		   URL url = new URL(mcsUrl+"query?formFaclity="+fromfacility+"&toFaclity="+tofacility+"&incommingAction="+incommingAction+"&nextAction="+nextAction+"&comment="+comment+"&status="+status);
		   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		   conn.setRequestMethod("GET");
		   conn.setRequestProperty("Accept", "application/json");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
	        StringBuffer jsonString=new StringBuffer();
			String output=null;
			while ((output = br.readLine()) != null) {
				jsonString.append(output);
			}	
	 }
		catch (Exception e) {
			e.printStackTrace();
		}
		  return "success";
	  }
	  
	  public static String readMessage(HttpServletRequest request,HttpServletResponse response) {  
		   String   faclityId=UtilProperties.getPropertyValue("general","posstore.usefaclityId");
		   String   mcsUrl=UtilProperties.getPropertyValue("general","pos.mcs.url");
		   try{
				  URL url = new URL(mcsUrl+"recieve?faclityId="+faclityId);
				  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				  conn.setRequestMethod("GET");
				  conn.setRequestProperty("Accept", "application/json");
					if (conn.getResponseCode() != 200) {
						throw new RuntimeException("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}
					BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			        StringBuffer jsonString=new StringBuffer();
					String output=null;
					while ((output = br.readLine()) != null) {
						jsonString.append(output);
					}	
          List<JSON> messageList=new ArrayList<JSON>();
		  JSONObject jObject = (JSONObject)JSONSerializer.toJSON(jsonString.toString());
		  JSONArray list=jObject.getJSONArray("recieveMessages");
		   for(int i=0;i<list.size();i++){
		     JSON json=(JSONObject)list.get(i);
		     messageList.add(json);
		   }
		    request.setAttribute("messageList", messageList);
		 }
		    catch (Exception e) {
				e.printStackTrace();
			}
		   return "success";
	  }
	  public static List<GenericValue> getPosTerminals(HttpServletRequest request,HttpServletResponse response) {
		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator"); 
		  String facilityId  = request.getParameter("facilityId");
		  List<GenericValue> terminals = null;
		  
		  if(UtilValidate.isNotEmpty(facilityId)){
			  try {
				terminals = delegator.findList("PosTerminal", EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId), null, null, null, true);
			} catch (GenericEntityException e) {
				new ArrayList<GenericValue>();
			}
			  if(UtilValidate.isEmpty(terminals) || terminals.size() == 0){
				  new ArrayList<GenericValue>();
			  }
			  
		  }
		  return terminals;
	  }
	  public static List getEntitySyncFacilityWise(HttpServletRequest request,HttpServletResponse response) {
		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator"); 
		  String facilityId  = request.getParameter("facilityId");
		  String posTerminalId  = request.getParameter("posTerminalId");
		  String syncType  = request.getParameter("syncType");
		  if(UtilValidate.isEmpty(syncType)) syncType = "PUSH";
		  List syncList = null;
		  Map syncMap = null;
		  
		  GenericValue RecurrenceRuleForPULL=null;
		  GenericValue RecurrenceRuleInfoRuleForPULL=null;
		  GenericValue JobSandboxForPULL=null;
		  
		  GenericValue RecurrenceRuleForPUSH=null;
		  GenericValue RecurrenceRuleInfoRuleForPUSH=null;
		  GenericValue JobSandboxForPUSH=null;
		
		  
		  if(UtilValidate.isEmpty(posTerminalId) || posTerminalId.equals("all")){
			  try {
				  List<GenericValue> posTerminals =  getPosTerminals(request,response);
				  if(UtilValidate.isNotEmpty(posTerminals))
				  {
					  syncList = new ArrayList();
					  if(syncType.equals("DUMP"))
					  {
						  syncMap = new HashMap();
						  syncMap.remove("posTerminalId");
					  	  posTerminalId = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
						  syncMap.put("posTerminalId",posTerminalId);
						  syncMap.put("RecurrenceRuleForDUMP",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"DUMP"), false));
						  syncMap.put("RecurrenceRuleInfoRuleForDUMP",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"DUMP"), false));
						  syncMap.put("JobSandboxForDUMP",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"DUMP"),false));
						  
						  syncList.add(syncMap);
					  }
					  else if(syncType.equals("PULL")){
					  for(GenericValue posTerminal : posTerminals){
						  syncMap = new HashMap();
						  posTerminalId = posTerminal.getString("posTerminalId");
						  
						  syncMap.put("posTerminalId",posTerminalId);
						  syncMap.put("RecurrenceRuleForPULL",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"PULL"), false));
						  syncMap.put("RecurrenceRuleInfoRuleForPULL",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"PULL"), false));
						  syncMap.put("JobSandboxForPULL",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"PULL"),false));
						 
						  syncList.add(syncMap);
					  }
					  }else{
						  for(GenericValue posTerminal : posTerminals){
							  syncMap = new HashMap();
							  posTerminalId = posTerminal.getString("posTerminalId");

							  syncMap.put("posTerminalId",posTerminalId);
							  syncMap.put("RecurrenceRuleForPUSH",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"PUSH"),false));
							  syncMap.put("RecurrenceRuleInfoRuleForPUSH",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"PUSH"),false));
							  syncMap.put("JobSandboxForPUSH",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"PUSH"),false));
							 
							  syncList.add(syncMap);
						  }
						  
					  }
				  }
				  
				} catch (Exception e) {
					e.printStackTrace();
					new ArrayList<GenericValue>();
				}
		  }
		  else if(!posTerminalId.equals("_NA_"))
		  {
			  List posTerminal = null;
			  
			  try {
				  posTerminal = delegator.findList("PosTerminal", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("posTerminalId",EntityOperator.EQUALS,posTerminalId),
						     EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId)),EntityOperator.AND), null, null, null, false);
				  
				  if(UtilValidate.isEmpty(posTerminal))
					  return new ArrayList();
				  
				  syncList = new ArrayList();
				  syncMap = new HashMap();
				  syncMap.put("posTerminalId",posTerminalId);
				  if(syncType.equals("DUMP"))
				  {
					  	  syncMap.remove("posTerminalId");
					  	  posTerminalId = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
						  syncMap.put("posTerminalId",posTerminalId);
						  syncMap.put("RecurrenceRuleForDUMP",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"DUMP"), false));
						  syncMap.put("RecurrenceRuleInfoRuleForDUMP",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"DUMP"), false));
						  syncMap.put("JobSandboxForDUMP",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"DUMP"),false));
				  }
				  else if(syncType.equals("PULL")){
					  
					  syncMap.put("RecurrenceRuleForPULL",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"PULL"), false));
					  syncMap.put("RecurrenceRuleInfoRuleForPULL",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"PULL"), false));
					  syncMap.put("JobSandboxForPULL",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"PULL"),false));
				  }else{
					  syncMap.put("RecurrenceRuleForPUSH",delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",posTerminalId+"PUSH"),false));
					  syncMap.put("RecurrenceRuleInfoRuleForPUSH",delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",posTerminalId+"PUSH"),false));
					  syncMap.put("JobSandboxForPUSH",delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",posTerminalId+"PUSH"),false));
				  }
				  syncList.add(syncMap);
				  
				} catch (GenericEntityException e) {
					new ArrayList<GenericValue>();
				}
		  }
		  return syncList;
	  }
	  public static void blankDbForNewJobs(String pullId , GenericDelegator delegator) throws GenericEntityException{
		    
			GenericValue JobSandBox =delegator.findOne("JobSandbox",UtilMisc.toMap("jobId",pullId), false);
			  if(JobSandBox!=null){
				  JobSandBox.remove();
			  }
			  List<GenericValue> JobSandBoxList=delegator.findByAnd("JobSandbox",UtilMisc.toMap("parentJobId",pullId));
		      if(JobSandBoxList!=null && JobSandBoxList.size()>0){
		    	  for(GenericValue job  : JobSandBoxList){
		    		  job.remove();
		    	  }
		      }
		      
		      GenericValue runtimeData =delegator.findOne("RuntimeData",UtilMisc.toMap("runtimeDataId",pullId), false);
			    if(runtimeData!=null){
			    	runtimeData.remove();
				  }
		      
			  GenericValue RecurrenceRule=delegator.findOne("RecurrenceRule",UtilMisc.toMap("recurrenceRuleId",pullId), false);
			 if(RecurrenceRule!=null){
				 GenericValue RecurrenceRuleInfo=delegator.findOne("RecurrenceInfo",UtilMisc.toMap("recurrenceInfoId",pullId), false);
			    if(RecurrenceRuleInfo!=null)
				  RecurrenceRuleInfo.remove();
			 
			     RecurrenceRule.remove();
			   }
	  }
	  public static String stopJob(HttpServletRequest request,HttpServletResponse response) {
		  String posTerminalId=request.getParameter("posTerminalId");
		  String syncType  = request.getParameter("syncType");
		  if(UtilValidate.isNotEmpty(syncType) && syncType.equals("DUMP"))
			  posTerminalId = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
		  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
		  
		  try {
			blankDbForNewJobs(posTerminalId+syncType,delegator);
		} catch (GenericEntityException e) {
		}
		  return "success";
	  }
}
