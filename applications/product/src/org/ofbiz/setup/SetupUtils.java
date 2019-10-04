package org.setup;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.apache.commons.fileupload.*;




public class SetupUtils {
	  public static final String module = SetupUtils.class.getName();
	  public  static final String ofbizhome=System.getProperty("ofbiz.home");
	  public final static String LogoPropertiesFile = "logoconfig.properties";
	  private static GenericDelegator delegator;
	  static  {
			delegator = GenericDelegator.getGenericDelegator("default");
		   }
	  public static String uploadLogo(HttpServletRequest request,HttpServletResponse response){
		  String DESTINATION_DIR_PATH =ofbizhome+UtilProperties.getPropertyValue(LogoPropertiesFile, "image.logo.path"); 
		  boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		  HttpSession session=request.getSession();
		  Debug.logInfo("request#######: "+request, module);
		  String uplodedImagename=null;
		  if (!isMultipart) {
			  Debug.logInfo("############File Not Uploaded", module);
		    } 
		  else{
			  FileItemFactory factory = new DiskFileItemFactory();
			  ServletFileUpload upload = new ServletFileUpload(factory);
			  List items = null;
			  try {
			   items = upload.parseRequest(request);
			   Debug.logInfo("items####: "+items, module);
			   } catch (FileUploadException e) {
				   Debug.logError(e.getMessage(),module);
				   //e.printStackTrace();
			  }
			   Iterator itr = items.iterator();
			  while (itr.hasNext()) {
			   FileItem item = (FileItem) itr.next();
			 
			   
			   Debug.logInfo("name############: "+item.getName(), module);
			   String value = item.getString();
			  
			   File savedFile =new File(DESTINATION_DIR_PATH,item.getName());
			   try{
			   item.write(savedFile);
			   request.setAttribute("uplodedImage",item.getName());
			   uplodedImagename=item.getName();
			   }
			   catch (Exception e) {
				   Debug.logError(e.getMessage(),module);
				   //e.printStackTrace();
			}
			  
			  
			}
		}try{
		  GenericValue Gv=delegator.findByPrimaryKey("VisualThemeResource",UtilMisc.toMap("visualThemeId", "NICHESUITE","resourceTypeEnumId","VT_HDR_IMAGE_URL","sequenceId","01"));
		  if(Gv!=null){
			  Gv.set("resourceValue","/images/"+uplodedImagename);
			 
			  Gv.store();
			 }
		}catch (Exception e) {
			Debug.logError(e.getMessage(),module);
			//e.printStackTrace();
			   }
			 
		  return "success"; 
	 }  
	  
	  public static String setWebPosLogin(HttpServletRequest request,HttpServletResponse response){
	  
		  String settingValue=request.getParameter("setting");
		  try{
		  GenericValue Gv=delegator.findByPrimaryKey("WebPosSetting",UtilMisc.toMap("webposId", "001"));
			if(Gv!=null){
				Gv.set("status", settingValue);
				Gv.store();
			}else{
				Gv=delegator.makeValue("WebPosSetting");
				Gv.set("webposId", "001");
				Gv.set("status", settingValue);
				Gv.create();
			}
		  }
		  catch (Exception e) {
			  Debug.logError(e.getMessage(),module);
			  //e.printStackTrace();
		}
		  
		  return "success";
	  }
	  
	  
	  public static String checkCatalog(HttpServletRequest request,HttpServletResponse response)
	  {
		  
		  PrintWriter out=null;
		  try{
			  out=response.getWriter();
		  if(request.getParameter("CATALOGID")!=null){
		  	 Debug.logInfo("####################"+request.getParameter("CATALOGID"), module);
		  	GenericValue Gv=delegator.findByPrimaryKey("ProdCatalog",UtilMisc.toMap("prodCatalogId", request.getParameter("CATALOGID")));
		  	if(Gv!=null){ 
		  	 out.print("Y");}
		  	 else{out.print("N");}
		     }
		  } catch (Exception e) {
			// TODO: handle exception
		  }
		  return "success";
	  }
	  
	  
	  
	  public static String checkCategory(HttpServletRequest request,HttpServletResponse response)
	  {
		  PrintWriter out=null;
		  try{
			  out=response.getWriter();
		  if(request.getParameter("CATID")!=null){
		  	 Debug.logInfo("####################"+request.getParameter("CATID"), module);
		  	GenericValue Gv=delegator.findByPrimaryKey("ProductCategory",UtilMisc.toMap("productCategoryId",request.getParameter("CATID")));
		  	if(Gv!=null){ 
		  	 out.print("Y");}
		  	 else{out.print("N");}
		     }
		  } catch (Exception e) {
			// TODO: handle exception
		  }
		  return "success";
	  }


	  public static String validateProduct(HttpServletRequest request,HttpServletResponse response){
		  PrintWriter out = null;
		  try{
			out=response.getWriter();
			if(request.getParameter("productId")!=null){
				GenericValue val = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", request.getParameter("productId")));
				if(val!=null){
					out.print("Y");
				}
				else{
					out.print("N");
				}
			}
		  }catch(Exception e){}
			return "success";
	  }
	  
	  public static String validateAttributeName(HttpServletRequest request,HttpServletResponse response){
		  PrintWriter out = null;
		  try{
			out=response.getWriter();
			Debug.logInfo("-----------@"+request.getParameter("attrName")+"@------------------"+request.getParameter("attrType"), module);
			if(request.getParameter("attrName")!=null && request.getParameter("attrType")!=null){
				GenericValue val = delegator.findByPrimaryKey("ProductAttributeType", UtilMisc.toMap("attrName", request.getParameter("attrName"),"attrType",request.getParameter("attrType")));
				Debug.logInfo("val"+val, module);
				if(val!=null){
					out.print("Y");
				}
				else{
					out.print("N");
				}
			}
		  }catch(Exception e){}
			return "success";
	  }
	  
	  public static Map<String, Object> updateVipLevel(DispatchContext ctx, Map<String, Object> context) {
	        GenericDelegator delegator = (GenericDelegator)ctx.getDelegator();
	        String userLoginId = (String) context.get("userLoginId");
	        String partyId = (String) context.get("partyId");
	        String customerroles = (String) context.get("customerroles");
	     try 
	     	{
	        GenericValue oneCustomer=delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId),false );
	        oneCustomer.set("custLevel", customerroles);
	        delegator.store(oneCustomer);
	        }
	     catch (GenericEntityException e)
	     	{
	        Debug.logError(e, module);
	        ServiceUtil.returnError(e.getMessage());
	        }

	  /*      Map<String, Object> result = ServiceUtil.returnSuccess();
	        result.put("listcustomMethod", listcustomMethod);
*/ Map<String, Object> result = ServiceUtil.returnSuccess();
	        return result;
	    }
	  
}


