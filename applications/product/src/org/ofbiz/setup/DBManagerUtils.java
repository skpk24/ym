package org.setup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.sync.Sync;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 

public class DBManagerUtils {
  public static final String module = DBManagerUtils.class.getName();
  private static GenericDelegator delegator;
  public final static String DBPropertiesFile = "dbconfig.properties";
   static  {
	delegator = GenericDelegator.getGenericDelegator("default");
   }
 
  public static String createDatabase(HttpServletRequest request, HttpServletResponse response) {
	 String Dbname=request.getParameter("DBname");
	 Dbname="nichesuite_"+Dbname;
	 String helperName = delegator.getGroupHelperName("org.ofbiz"); 
	 HttpSession session=request.getSession();
	 Debug.logInfo("******************************* DATABASE NAME ***********"+Dbname+"********* HELPER NAME ********"+helperName, module);
	 
	 Connection conn=null;
	try{
     	conn = ConnectionFactory.getConnection(helperName); 
	 	Statement statement = conn.createStatement();	 	   
		String query="CREATE DATABASE "+Dbname;
		statement.executeUpdate(query);
		GenericValue DB = delegator.findByPrimaryKey("DataBaseTrace", UtilMisc.toMap("dbId", "001"));
       	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
     	String ofbizhome=System.getProperty("ofbiz.home");
    	String fullpath=ofbizhome+entityxmlpath;
    	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
    	String currDbname=readEntityXml(fullpath,"datasource","test",helperName,false);

		if(DB==null){
	    	DB=delegator.makeValue("DataBaseTrace");
	    	DB.set("dbId", "001");
	    	DB.set("currDb",currDbname);
	    	DB.set("newDb",Dbname);
	    	DB.set("switchsat","False");
	    	DB.create();
			request.setAttribute("DATABASENAME",Dbname);
			session.setAttribute("NewDatabaseName",Dbname); 
	      }
	    else{
	    	DB.set("currDb",currDbname);
	    	DB.set("newDb",Dbname);
	    	DB.store();
			request.setAttribute("DATABASENAME",Dbname);
			session.setAttribute("NewDatabaseName",Dbname); 
	    }
	}
     catch (Exception e){
     		request.setAttribute("DATABASEERROR","some error in creating database,may be it already persent");
     		request.removeAttribute("DATABASENAME");
     		 Debug.logError(e.getMessage(),module);
     		 //e.printStackTrace();
    }finally{
		try {
			if(conn != null) conn.close();
		} catch (SQLException e) {
			 Debug.logError(e.getMessage(),module);
			 //e.printStackTrace();
		}
	}
	return "success";
   }
    
    
    public static String getCurrDBForBackUp(HttpServletRequest request, HttpServletResponse response){
    	
    	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
     	String ofbizhome=System.getProperty("ofbiz.home");
    	String fullpath=ofbizhome+entityxmlpath;
    	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
    	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
    	String currDbname=readEntityXml(fullpath,"datasource","test",helperName,false);
    	request.setAttribute("CurrentDataBase", currDbname);
    	return "success";	
    }
    
    public static GenericValue  getDbTrace()
    {
       	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
     	String ofbizhome=System.getProperty("ofbiz.home");
    	String fullpath=ofbizhome+entityxmlpath;
    	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
    	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
    	String currDbname=readEntityXml(fullpath,"datasource","test",helperName,false);

    	GenericValue DB=null;
    	try{ 
    		DB=delegator.findByPrimaryKey("DataBaseTrace", UtilMisc.toMap("dbId", "001")); 
    	
    	
    	 if(DB==null)
    	 {
    		 DB=delegator.makeValue("DataBaseTrace");
 	    	 DB.set("dbId", "001");
 	    	 DB.set("currDb",currDbname);
 	    	 DB.create(); 
    	 }else{
    		  DB.set("currDb",currDbname);
    		  DB.store();
    	 } 
    	 
    	} catch (Exception e) {
     		
 		}
    	 return DB;
    }
    
    
    
    
    public static String showDataBases(HttpServletRequest request, HttpServletResponse response){
    	
    	List <String>DatabaseNames=new ArrayList<String>();
    	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
     	String ofbizhome=System.getProperty("ofbiz.home");
    	String fullpath=ofbizhome+entityxmlpath;
    	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
    	String currDbname=readEntityXml(fullpath,"datasource","test",helperName,false);
    	request.setAttribute("CurrentDataBase", currDbname);
    	Connection conn=null;
    	try{
         	conn = ConnectionFactory.getConnection(helperName); 
         	ResultSet rs = conn.getMetaData().getCatalogs();
         	 while (rs.next()) {
                if(rs.getString("TABLE_CAT").startsWith("nichesuite_"))
            		{ DatabaseNames.add(rs.getString("TABLE_CAT"));
            	        }
              }
         	
         	request.setAttribute("DataBases",DatabaseNames);
         	rs.close();
         	conn.close();
    	}
    	catch (Exception e) {
    		 Debug.logError(e.getMessage(),module);
    		 //e.printStackTrace();
		}finally{
			try {
				if(conn != null) conn.close();
			} catch (SQLException e) {
				 Debug.logError(e.getMessage(),module);
				 //e.printStackTrace();
			}
		}

    	return "success";		
    }
    
    
    public static String dropDatabase(HttpServletRequest request, HttpServletResponse response){
    	String dbname=request.getParameter("dbname");
    	String helperName = delegator.getGroupHelperName("org.ofbiz");
    	Connection conn=null;
    	try{
    	conn = ConnectionFactory.getConnection(helperName); 
	 	Statement statement = conn.createStatement();	 	   
		String query="DROP DATABASE "+dbname;
		statement.executeUpdate(query);
		conn.close();	
    	}
    	catch (Exception e) {
    		try{
    		conn.close();	
    		}
    		catch (Exception ex) {
    			ex.printStackTrace();
			}
    	}finally{
			try {
				if(conn != null) conn.close();
			} catch (SQLException e) {
				 Debug.logError(e.getMessage(),module);
				 //e.printStackTrace();
			}
		}
    	return "success";		
    }
    
    
 
   public static String backupdatabase(HttpServletRequest request, HttpServletResponse response){
    
	      Map<String,String> readDBConfigDetails = readDBConfigDetails(false,"");
	   
		  //  String dumpcmdpath = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.cmd.path"); 
			//String username = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.user.name"); 
	      String username = readDBConfigDetails.get("dbUsername"); 
			//String password = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.user.password");
	      String password = readDBConfigDetails.get("dbPassword"); 
			String dumppath = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.dump.path");
			if(UtilValidate.isEmpty(dumppath)) dumppath = "/framework/webtools/data/"+UtilProperties.getPropertyValue("general","posstore.posTerminalId")+".sql";
			
			String databasename = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.database");
			if(UtilValidate.isEmpty(databasename)) databasename = readDBConfigDetails.get("dbName");
			
	        //Debug.logInfo("******* dumpcmdpath ********===>"+dumpcmdpath +" -u"+username  +" -p"+password+" "+databasename +" -r"  +dumppath+"   "+System.getProperty("ofbiz.home"), module);
	 try {
    	  Runtime rt = Runtime.getRuntime();
    	  rt.exec("mysqldump -u"+username +" -p"+password+" "+databasename +" -r "  +System.getProperty("ofbiz.home")+dumppath);
    	  GenericValue DB=delegator.findByPrimaryKey("DataBaseTrace", UtilMisc.toMap("dbId", "001"));
    	  if(DB!=null){
    		  DB.set("dumppath",System.getProperty("ofbiz.home")+dumppath);  
    	      DB.store();
    	      request.setAttribute("DumpPath",System.getProperty("ofbiz.home")+dumppath) ;
    	  }else{
    		   DB=delegator.makeValue("DataBaseTrace") ;
    		  String  dbTraceId = delegator.getNextSeqId("DataBaseTrace");
    		   DB.set("dbId", dbTraceId);
    		   DB.set("dumppath",System.getProperty("ofbiz.home")+dumppath);
    		   DB.create();
    		   request.setAttribute("DumpPath",System.getProperty("ofbiz.home")+dumppath) ;
    	  }
	} 
    	  catch(Exception ioe) {
    		  Debug.logError(ioe.getMessage(),module);
    		 // ioe.printStackTrace();
    	  }
     return "success";
   }
    
    public static String fetchCurrentDbName(HttpServletRequest request, HttpServletResponse response){
    	HttpSession session=request.getSession();
    	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
    	String ofbizhome=System.getProperty("ofbiz.home");
    	String fullpath=ofbizhome+entityxmlpath;
    	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
    	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
    	String currDbname=readEntityXml(fullpath,"datasource","test",helperName,false);
    	GenericValue DB=null;
    	try{ 
    	    DB =delegator.findByPrimaryKey("DataBaseTrace",UtilMisc.toMap("dbId", "001"));
    	}
    	catch (Exception e) {
		}
    	if(DB==null){
    	 session.setAttribute("CurrentDB", currDbname);
    	 request.setAttribute("CurrentDataBase", currDbname);
    	}else{
    		session.setAttribute("CurrentDB", DB.getString("currDb"));
        	request.setAttribute("CurrentDataBase", DB.getString("currDb"));
    	}
    	return "success";	
    }
    
    
    public static String readEntityXml(String xmlFilePath,String tagename,String newValue,String currdatasourse,boolean update)
    {  String currDbname="";
    	try{
    	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	Document doc = docBuilder.parse(xmlFilePath); 
    	
    		currDbname=replaceValue(doc, tagename, newValue,currdatasourse,update);
    	
    	if(update){
    	Transformer t = TransformerFactory.newInstance().newTransformer();
    	Result result = new StreamResult(new File(xmlFilePath));
    	Source source = new DOMSource(doc);
    	t.transform(source, result); 
    	}	
    }
    	catch (Exception e) {
			
		}
    	return currDbname;	
    }
    
    private static String replaceValue(Document doc, String tagName, String replaceValue,String currdatasourse,boolean update) {
    	NodeList nodeList = doc.getElementsByTagName(tagName);
    	for(int i=0;i<nodeList.getLength();i++){
	    	Node node = nodeList.item(i);
	    	NamedNodeMap attributes=node.getAttributes();
	    	for (int g = 0; g < attributes.getLength(); g++) {
	            Attr attribute = (Attr)attributes.item(g);
	            if(attribute.getName().equals("name") && attribute.getValue().equals(currdatasourse)){
	            	Debug.logInfo(" *******Attribute: " + attribute.getName() +" with value " +attribute.getValue(), module);
	            	NodeList nodeList1=node.getChildNodes();
	            	for(int j=0;j<nodeList1.getLength();j++){
	            		Node node1 = nodeList1.item(j);
	            		String nodename=node1.getNodeName();
	            		if(nodename.equals("inline-jdbc")){
	            		NamedNodeMap attributes1=node1.getAttributes();
	        	    	for (int k = 0; k < attributes1.getLength(); k++) {
	        	            Attr attribute1 = (Attr)attributes1.item(k);
	        	            if(attribute1.getName().equals("jdbc-uri")){
	        	            	Debug.logInfo(" *******Attribute JDBC: " + attribute1.getName() +" with value " +attribute1.getValue(), module);
	        	            	if(update){
	        	            	    attribute1.setValue("jdbc:mysql://localhost/test?autoReconnect=true");
	        	            	}
	        	            String databaseuri=attribute1.getValue();
	        	            String []uriarr=databaseuri.split("[?]");
	        	            String []dbname1=uriarr[0].split("//");
	        	            String []dbname=dbname1[1].split("/");
	        	            Debug.logInfo("**** DATABASE NAME IS  ******"+dbname[1], module);
	        	            return dbname[1];
	        	            }
	        	    	}
	            	 }
	               }
	            }  
	        }
    	}
    return "success";
    }
    
    public static String restart(HttpServletRequest request, HttpServletResponse response)
    {   
    	String ofbizhome=System.getProperty("ofbiz.home");
       	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
    	String fullpath=ofbizhome+entityxmlpath;
    	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
    	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
    	if(request.getParameter("update").equals("true")){
    	    String currDbname=readEntityXml(fullpath,"datasource","test",helperName,true);
    	}
       try{
    	   
    	    Runtime.getRuntime().exec("startofbiz1.bat");    	
    	    System.exit(0);
       }
    	catch (Exception e) {
    		Debug.logError(e.getMessage(),module);
    		e.printStackTrace();
		}
    	return "success";
    }
    
  public static String deleteDump(HttpServletRequest request, HttpServletResponse response){
	  
	   String dumppath = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.dump.path"); 
	   String ofbizhome=System.getProperty("ofbiz.home");
	   String fullpath=ofbizhome+dumppath;
	   File f = new File(fullpath);
	   GenericValue DB=null;
  	  try{ 
  	    DB =delegator.findByPrimaryKey("DataBaseTrace",UtilMisc.toMap("dbId", "001"));
  	   if(DB!=null){
  		DB.set("dumppath", null);
  	    DB.store();
  	   }
  	   if(f.exists()){f.delete();}
  	 }
  	  catch (Exception e) {
  		Debug.logError(e.getMessage(),module);
  		//e.printStackTrace();
  	  } 
	  
	  return "success";
  }
  
  public static Map<String,String> readDBConfigDetails(boolean update,String dbName)
  {  
  	String entityxmlpath = UtilProperties.getPropertyValue(DBPropertiesFile, "entity.xml.path"); 
  	if(UtilValidate.isEmpty(entityxmlpath))entityxmlpath =  "/framework/entity/config/entityengine.xml";
  	
  	String ofbizhome=System.getProperty("ofbiz.home");
  	String xmlFilePath = ofbizhome+entityxmlpath;
  	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
  	String helperName = delegator.getGroupHelperName("org.ofbiz"); 
  	
  	Debug.logInfo("**** ENTITY XML FULL PATH ************"+ofbizhome+entityxmlpath, module);
  	Map<String,String> dbConfigDetails =  new HashMap<String, String>();
  	try{
  	DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
  	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
  	Document doc = docBuilder.parse(xmlFilePath);
  	
  	dbConfigDetails = dbConfigDetails(doc, "datasource",dbName,helperName,update);
  	
  	if(update){
		    	Transformer t = TransformerFactory.newInstance().newTransformer();
		    	Result result = new StreamResult(new File(xmlFilePath));
		    	Source source = new DOMSource(doc);
		    	t.transform(source, result); 
	    		  }	
	    }
  	catch (Exception e) {
			
		}
	return dbConfigDetails;	
  }

private static Map<String,String> dbConfigDetails(Document doc, String tagName, String newDBName,String currdatasourse,boolean update) {
	Map<String,String> dbConfigDetails = new HashMap<String, String>();
	NodeList nodeList = doc.getElementsByTagName(tagName);
	for(int i=0;i<nodeList.getLength();i++){
  	Node node = nodeList.item(i);
  	NamedNodeMap attributes=node.getAttributes();
  	for (int g = 0; g < attributes.getLength(); g++) {
          Attr attribute = (Attr)attributes.item(g);
          if(attribute.getName().equals("name") && attribute.getValue().equals(currdatasourse)){
          	Debug.logInfo(" *******Attribute: " + attribute.getName() +" with value " +attribute.getValue(), module);
          	NodeList nodeList1=node.getChildNodes();
          	for(int j=0;j<nodeList1.getLength();j++){
          		Node node1 = nodeList1.item(j);
          		String nodename=node1.getNodeName();
          		if(nodename.equals("inline-jdbc")){
          		NamedNodeMap attributes1=node1.getAttributes();
      	    	for (int k = 0; k < attributes1.getLength(); k++) {
      	            Attr attribute1 = (Attr)attributes1.item(k);
      	            if(attribute1.getName().equals("jdbc-uri")){
      	            	Debug.logInfo(" *******Attribute JDBC: " + attribute1.getName() +" with value " +attribute1.getValue(), module);
      	            	if(update){
      	            	    attribute1.setValue("jdbc:mysql://localhost/"+newDBName+"?autoReconnect=true");
      	            	}
      	            String databaseuri=attribute1.getValue();
      	            String []uriarr=databaseuri.split("[?]");
      	            String []dbname1=uriarr[0].split("//");
      	            String []dbname=dbname1[1].split("/");
      	            Debug.logInfo("**** DATABASE NAME IS  ******"+dbname[1], module);
      	            
      	            dbConfigDetails.put("dbName", dbname[1]);
      	            //return dbname[1];
      	            }
      	            else if(attribute1.getName().equals("jdbc-username")){
      	            	Debug.logInfo(" *******Attribute JDBC: " + attribute1.getName() +" with value " +attribute1.getValue(), module);
      	            	
      	            String jdbc_username =attribute1.getValue();
      	            Debug.logInfo("**** DATABASE jdbc-username IS  ******"+jdbc_username, module);
      	            dbConfigDetails.put("dbUsername", jdbc_username);
      	            //return jdbc_username;
      	            }
      	            else if(attribute1.getName().equals("jdbc-password")){
      	            	Debug.logInfo(" *******Attribute JDBC: " + attribute1.getName() +" with value " +attribute1.getValue(), module);
      	            	if(update){
      	            	    attribute1.setValue("jdbc:mysql://localhost/"+newDBName+"?autoReconnect=true");
      	            	}
      	              	
	        	            String jdbc_password =attribute1.getValue();
	        	            Debug.logInfo("**** DATABASE jdbc-password IS  ******"+jdbc_password, module);
	        	            dbConfigDetails.put("dbPassword", jdbc_password);
	        	           // return jdbc_password;
      	            }
      	    	}
          	 }
             }
          }  
      }
	}
return dbConfigDetails;
}
	public static Map<String, Object> DBBackUpService(DispatchContext dctx, Map<String, ? extends Object> context) {
		Map<String, Object> result =  ServiceUtil.returnSuccess();
	      Map<String,String> readDBConfigDetails = readDBConfigDetails(false,"");
	      String username = readDBConfigDetails.get("dbUsername"); 
	      String password = readDBConfigDetails.get("dbPassword");
	      
	      String posTerminalId = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
	      String facilityId =  UtilProperties.getPropertyValue("general","posstore.usefaclityId");
	      String dumpName = "dbDump";
	      if(UtilValidate.isNotEmpty(posTerminalId) && UtilValidate.isNotEmpty(facilityId) )
	    	  dumpName = facilityId +"_"+posTerminalId;
	      
	      
		  String dumppath = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.dump.path");
		  if(UtilValidate.isEmpty(dumppath)) dumppath = "/framework/webtools/data/"+dumpName+".sql";
		  String databasename = UtilProperties.getPropertyValue(DBPropertiesFile, "mysql.default.database");
		 if(UtilValidate.isEmpty(databasename)) databasename = readDBConfigDetails.get("dbName");
		 try {
		  	  Runtime rt = Runtime.getRuntime();
		  	  rt.exec("mysqldump -u"+username +" -p"+password+" "+databasename +" -r "  +System.getProperty("ofbiz.home")+dumppath);
		  	  GenericValue DB=delegator.makeValue("DataBaseTrace") ;
		  		  String  dbTraceId = delegator.getNextSeqId("DataBaseTrace");
		  		   DB.set("dbId", dbTraceId);
		  		   DB.set("dumppath",System.getProperty("ofbiz.home")+dumppath);
		  		   DB.create();
			} 
	  	  catch(Exception ioe) {
	  		  Debug.logError(ioe.getMessage(),module);
	  		 // ioe.printStackTrace();
	  	  }
		return result;
	}
	public static String backUpDBDump(HttpServletRequest request, HttpServletResponse response) {
		GenericValue RecurrenceRule=null;
		GenericValue RecurrenceRuleInfo=null;
		GenericValue JobSandBox = null;
		
		  String posTerminalId = UtilProperties.getPropertyValue("general","posstore.posTerminalId");
		  String frequency=request.getParameter("frequency"); 
		  String Interval=request.getParameter("Interval"); 
		  String countNumber=request.getParameter("countNumber"); 
		  String pullId = posTerminalId+"DUMP";
		  try{
			  Sync.blankDbForNewJobs(pullId, delegator);
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
			  
	    	  JobSandBox=delegator.makeValue("JobSandbox");
	    	  JobSandBox.set("jobId",pullId);
	    	  JobSandBox.set("jobName","Dump of DB");
	    	  JobSandBox.set("runTime",UtilDateTime.nowTimestamp());
	    	  JobSandBox.set("serviceName","DBBackUpService");
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
}