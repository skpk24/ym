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
package org.ofbiz.dataimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;
//import javolution.util.HashMap;

import org.apache.tools.ant.util.ClasspathUtils.Delegate;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.*;
import java.io.*;

import jxl.*;

import java.util.*;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hamcrest.core.IsNot;
import com.oreilly.servlet.MultipartRequest;
/**
 * Product Information Related Events
 */
public class UploadEvents {

    public static final String module = UploadEvents.class.getName();
    public static final String resource = "DataimportUiLabels";
    
    
  public static synchronized String getProductSheet(HttpServletRequest request,HttpServletResponse response) {
	  String filePath = System.getProperty("ofbiz.home")+"/framework/images/webapp/images/importFiles/product/";
	  String fileType = null;
	  String fileName = null;
	  String fileNameTouse = "product.xls";
	  Map message =  new HashMap();
	  LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	  HttpSession session = request.getSession();
	  GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

	 try{
		  
			  	MultipartRequest mr = new MultipartRequest(request,filePath,9999999);
			  	fileType = request.getParameter("upload_file_type");  
			  	//System.out.println("\n\n\n\n fileType"+fileType);
			  	if(UtilValidate.isNotEmpty(fileType) && fileType.equalsIgnoreCase("productExcel")){
 			  		fileNameTouse = "productExcel.xls";
			  	}else if(UtilValidate.isNotEmpty(fileType) && fileType.equalsIgnoreCase("productAisleExcel")){
			  		fileNameTouse = "productAisleExcel.xls";
			  	}
				Enumeration en =  mr.getFileNames();
				while(en.hasMoreElements())
				fileName = mr.getFilesystemName((String)en.nextElement());
				File oldFile = new File(filePath+fileName);
				//System.out.println("\n\n\n\n\n oldFile"+oldFile.exists());
				File newFile = new File(filePath+fileNameTouse);  
				if(newFile.exists()){
					newFile.delete();
				}
				  if(oldFile.renameTo(newFile)){
					    if(UtilValidate.isNotEmpty(fileType)){
					    if(fileType.equalsIgnoreCase("product")){	
					    	//System.out.println("\n\n\n\n userLogin"+userLogin);
				    	 message = dispatcher.runSync("uploadProduct", UtilMisc.toMap("userLogin", userLogin));
				    	 }else if(fileType.equalsIgnoreCase("productAisleExcel")){  
				    		 //System.out.println("\n\n\n\n userLogin"+userLogin);
					    	 message = dispatcher.runSync("uploadAisleNumber", UtilMisc.toMap("userLogin", userLogin));
				    	 }else if(fileType.equalsIgnoreCase("productExcel")){
						    	//System.out.println("\n\n\n\n userLogin"+userLogin);
					    	 message = dispatcher.runSync("uploadProductExcel", UtilMisc.toMap("userLogin", userLogin));
				    	 }
				    }
					    oldFile.delete();
					    newFile.delete();
				}
		 
	 }
    	  catch (Exception ex) {
    	}
	 request.setAttribute("message",message);
	 //System.out.println("\n\n\n\n message"+message);
	  return "success";
  }	  

  
    
    
public static Map uploadProduct(DispatchContext dctx, Map context) {
    	
    	Map results = null;
    	Map successMap = new HashMap();
    	try
        {
    		String appPath= System.getProperty("ofbiz.home");
    	  String serverPath = UtilProperties.getPropertyValue("dataimport", "file.server.path");
    	  
      	  String filename = appPath+serverPath+"/importFiles/product/product.xls";
          WorkbookSettings ws = new WorkbookSettings();
          ws.setLocale(new Locale("en", "EN"));
          Workbook workbook = Workbook.getWorkbook(new File(filename),ws);
          Sheet s  = workbook.getSheet(0);
           successMap = readProductDataSheet(s, dctx);
          workbook.close(); 
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (BiffException e)
        {
          e.printStackTrace();
        }
        results = UtilMisc.toMap("message", "success");
        results.putAll(successMap);
        return results;
    }
    
private static Map readProductDataSheet(Sheet s, DispatchContext dctx){
  Cell rowData[] = null;
  int successCount = 0;
  int rows = s.getRows(); 
  int column = s.getColumns();
  String errorRowNo = "";
  for (int i = 1; i < rows ; i++) {
	  
	  String productTypeId = "FINISHED_GOOD";
	  String artGroupNo = "";
	  String description = "";
	  String artGroupSubNo = "";
	  String longDescription = "";
	  String productId = "";
	  String productName = "";
	  String sellUnit = "";
	  String packType = "";
	  String defaultPrice = "";
	  String vatPerc = "";
	  String listPrice = "";
	  String stock = "";
	  String articleStatus = "";
	  String extraInfo = "";
	  String blockInd = "";
	  String barcode = "";
	  String categoryOne = "";
      String categoryTwo = "";
      String categoryThree = "";
      String smallImage ="";
      String largeImage ="";
      String detailImage ="";
      String productFeatureCategoryId = "";
      String productFeature = "";
      String variantId = "";
	  String isVirtual = "";
      String isVariant = "";
      String internalName = "";
      String brandName = "";
     
	  
      String smallImageUrl = "";
	  String largeImageUrl = "";  
	  String detailImageUrl = "";
	  String isExternal = "";
	  String priceToTake = "";
	  String vatPercent = "";
	  String qoh = "";
	  String sequenceNum ="";
	  String unitPrice = "";

	  rowData = s.getRow(i);
	  try {
		  
		if (rowData.length > 0 && rowData[0].getContents().length() != 0) { 
			
			if(UtilValidate.isEmpty(rowData[0].getContents()) || rowData[0].getContents().trim().length()==0)
				break;
				
			for (int j = 0; j < rowData.length; j++) {
				switch (j) {
				case 0:
					artGroupNo = rowData[j].getContents();
					break;
				case 1:
					internalName = rowData[j].getContents();
					break;
				case 2:
					brandName = rowData[j].getContents();
					break;
				case 3:
					productName = rowData[j].getContents();
					break;
				case 4:
					description = rowData[j].getContents();
					break;
				case 5:
					longDescription = rowData[j].getContents();
					break;
				case 6:
					categoryOne = rowData[j].getContents();
					break;
				case 7:
					isVirtual = rowData[j].getContents();
					break;
				case 8:
					productFeatureCategoryId = rowData[j].getContents();
					 break;
				case 9:
					sequenceNum = rowData[j].getContents();
					break;	
				case 10:
					priceToTake = rowData[j].getContents();
					break;
				case 11:
					unitPrice = rowData[j].getContents();
					break;
				case 12:
					vatPercent   = rowData[j].getContents();
					break;
				case 13:
					defaultPrice = rowData[j].getContents();
					break;	
				case 14:
					listPrice = rowData[j].getContents();
					break;
				case 15:
					isExternal = rowData[j].getContents();  
					break;
				case 16:
					barcode = rowData[j].getContents();
					break;
				case 17:
					stock = rowData[j].getContents();
					break;	
				case 18:
					qoh = rowData[j].getContents();
					break;	
				case 19:
					smallImage = rowData[j].getContents();
					break;
				case 20:
					largeImage = rowData[j].getContents();
				case 21:
					detailImage = rowData[j].getContents();
					break;
				default:
					break;
				}
			}
			
	
	  
	  if(smallImage != null && smallImage.length() > 0)
		  smallImageUrl = "/images/products/small/" + smallImage;
	  if(largeImage != null && largeImage.length() > 0)
		  largeImageUrl = "/images/products/large/" + largeImage;
	  if(detailImage != null && detailImage.length() > 0)
		  detailImageUrl = "/images/products/detail/" + detailImage;

	  
      Timestamp fromDate = UtilDateTime.nowTimestamp();
      Delegator delegator = dctx.getDelegator();
      
      GenericValue newImportProduct = delegator.makeValue("DataImportProduct");
      	  String exportProductSeqId = delegator.getNextSeqId("DataImportProduct");
      	  newImportProduct.set("exportProductSeqId", exportProductSeqId);
	      newImportProduct.set("productId", artGroupNo.trim());  //Article Number customer Given
	      newImportProduct.set("productTypeId", productTypeId.trim());
	      
	      if(internalName != null && internalName.length() > 0)
	    	  newImportProduct.set("internalName", internalName.trim());
	      if(brandName != null && brandName.length() > 0)
	    	  newImportProduct.set("brandName", brandName.trim());
	      if(productName != null && productName.length() > 0)
	    	  newImportProduct.set("productName", productName.trim());
	      if(description != null && description.length() > 0)
	    	  newImportProduct.set("description", description.trim());
	      if(longDescription != null && longDescription.length() > 0)
	    	  newImportProduct.set("longDescription", longDescription.trim());
	      
	      if(smallImage != null && smallImage.length() > 0 )
	    	  newImportProduct.set("smallImageUrl", smallImageUrl.trim());
	      if(largeImage != null && largeImage.length() > 0)
	    	  newImportProduct.set("largeImageUrl", largeImageUrl.trim());
	      if(detailImage != null && detailImage.length() > 0)
	    	  newImportProduct.set("detailImageUrl", detailImageUrl.trim());
	      if(defaultPrice != null && defaultPrice.length() > 0)
	    	  newImportProduct.set("defaultPrice", new java.math.BigDecimal(defaultPrice.trim().toString()));
	      
	      if(vatPercent != null && vatPercent.length() > 0)
	    	  newImportProduct.set("vatPercent", new java.math.BigDecimal(vatPercent.trim().toString()));
	      
	      
	      if(listPrice != null && listPrice.length() > 0)
	    	  newImportProduct.set("listPrice", new java.math.BigDecimal(listPrice.trim().toString()));
	      
	      
	      if(categoryOne != null && categoryOne.length() > 0)
	    	  newImportProduct.set("categoryOne", categoryOne.trim());
	      
	     
	      
	      if(productFeatureCategoryId != null && productFeatureCategoryId.length() > 0)
	    	  newImportProduct.set("productFeatureCategoryId", productFeatureCategoryId.trim());
	      
	      if(isVirtual != null && isVirtual.length() > 0)
	    	  newImportProduct.set("isVirtual", isVirtual.trim());
	      
	      if(isVariant != null && isVariant.length() > 0)
	    	  newImportProduct.set("isVariant","N");
	      
	      
	      if(isExternal != null && isExternal.length() > 0)
			  newImportProduct.set("isExternal", isExternal.trim());
 
	          
	 
		  if(barcode != null && barcode.length() > 0)
		  newImportProduct.set("barcode", barcode.trim());
		  
		  if(stock != null && stock.length() > 0)
			  newImportProduct.set("virtualStock", stock.trim());
		 
		  if(priceToTake != null && priceToTake.length() > 0)
	    	  newImportProduct.set("priceToTake", priceToTake.trim());
		  
		  if(qoh != null && qoh.length() > 0)
	    	  newImportProduct.set("qoh", qoh.trim());
		  
		  if(unitPrice != null && unitPrice.length() > 0)
	    	  newImportProduct.set("unitPrice", new java.math.BigDecimal(unitPrice.trim().toString()));
		  
		  if(sequenceNum != null && sequenceNum.length() > 0)
	    	  newImportProduct.set("sequenceNum", sequenceNum.trim());
	       
	     
	      try {
	          delegator.create(newImportProduct);
	          Debug.logInfo("Successfully imported product ["+productId+" from row no "+ i +"].", module);
	          successCount++;
	      } catch (GenericEntityException e) {
	    	  errorRowNo += "Error while importing product [ from row no "+ i +"].\n";
	    	  Debug.logInfo("Error while importing product [ from row no "+ i +"]." + e, module);
	          Debug.logWarning(e.getMessage(), module);
	      }
	  }//if close
		} catch (Exception e) {
			errorRowNo += "Error while importing product [ from row no "+ i +"].\n";
		  Debug.logInfo("Error while importing product [ from row no "+ i +"]." + e, module);
		  continue;
      }
  }
  Debug.logInfo("Total row imported: - ["+ successCount +"].", module);
  
return UtilMisc.toMap("successCount", new BigDecimal(successCount).toString(), "errorRowNo", errorRowNo);
 
}
    
    
    public static List readuploadedProduct(Delegator delegator)throws Exception
    {
    	try{
    		return delegator.findList("DataImportProduct", null, null, null, null, false);
    	}catch (Exception e) {
			e.printStackTrace();
			return new ArrayList();
		}
    }
    
    
    public static Map uploadVariantProduct(DispatchContext dctx, Map context) {
    	Map results = null;
    	try
        {
    		String appPath= System.getProperty("ofbiz.home");
      	  String serverPath = UtilProperties.getPropertyValue("dataimport", "file.server.path");
      	  
        	  String filename = appPath+serverPath+"/importFiles/product/Product.xls";
           WorkbookSettings ws = new WorkbookSettings();
          ws.setLocale(new Locale("en", "EN"));
          Workbook workbook = Workbook.getWorkbook(new File(filename),ws);
          Sheet s  = workbook.getSheet(0);
          readVariantProductDataSheet(s, dctx);
          workbook.close(); 
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (BiffException e)
        {
          e.printStackTrace();
        }
        results = UtilMisc.toMap("message", "success");
        return results;
    }
    
    private static void readVariantProductDataSheet(Sheet s, DispatchContext dctx)
    {
      Cell rowData[] = null;
      int successCount = 0;
      int rows = s.getRows();
      int column = s.getColumns();
      
      for (int i = 1; i < rows; i++) {
    	  String productId = "";
    	  String productTypeId = "";
    	  String internalName = "";
    	  String brandName = "";
    	  String productName = "";
    	  String description = "";
    	  String longDescription = "";
    	   String smallImage ="";
           String largeImage ="";
           String detailImage ="";
     	  String defaultPrice = "";
    	  String listPrice = "";
           String isExternal = "";
           String barcode = "";
          String stock = "";
    	  rowData = s.getRow(i);
    	  
    	  
    	  try {
			if (rowData[0].getContents().length() != 0) { 
				if(UtilValidate.isEmpty(rowData[0].getContents()) || rowData[0].getContents().trim().length()==0)
					break;
				for (int j = 0; j < rowData.length; j++) {
					switch (j) {
					case 0:
						productId = rowData[j].getContents();
						break;
					case 1:
						internalName = rowData[j].getContents();
						break;
					case 2:
						brandName = rowData[j].getContents();
						break;
					case 3:
						productName = rowData[j].getContents();
						break;
					case 4:
						description = rowData[j].getContents();  
						break;
					case 5:
						longDescription = rowData[j].getContents();
						break;
					case 6:
						smallImage = rowData[j].getContents();
 						break;
					case 7:
						largeImage = rowData[j].getContents();	
						break;
					case 8:
						detailImage = rowData[j].getContents();	
						break;
					case 9:
						defaultPrice = rowData[j].getContents();
						break;
					case 10:
						listPrice = rowData[j].getContents();
						break;
					case 11:
						isExternal = rowData[j].getContents();
						break;
					case 12:
						barcode = rowData[j].getContents();
						break;
					case 13:
						stock = rowData[j].getContents();
						break;
					default:
						break;
					 
					}
				}
			}
			  String smallImageUrl = "";
	    	  String largeImageUrl = "";
	    	  String detailImageUrl = "";
	    	  
	    	  if(smallImage != null && smallImage.length() > 0)
	    		  smallImageUrl = "/images/products/small/" + smallImage;
	    	  if(largeImage != null && largeImage.length() > 0)
	    		  largeImageUrl = "/images/products/large/" + largeImage;
	    	  if(detailImage != null && detailImage.length() > 0)
	    		  detailImageUrl = "/images/products/detail/" + detailImage;
	    	  
		      Timestamp fromDate = UtilDateTime.nowTimestamp();
		      Delegator delegator = dctx.getDelegator();
		     
		      GenericValue newImportVariantProduct = delegator.makeValue("DataImportProduct");
		      String exportProductSeqId = delegator.getNextSeqId("DataImportProduct");
		      newImportVariantProduct.set("exportProductSeqId", exportProductSeqId);
			  newImportVariantProduct.set("productId", productId);
			  newImportVariantProduct.set("productTypeId", "FINISHED_GOOD");
			  
			  newImportVariantProduct.set("isVariant", "Y");
			  newImportVariantProduct.set("isVirtual", "N");
			  
			  if(internalName != null && internalName.length() > 0)
				  newImportVariantProduct.set("internalName", internalName.trim());
			  
			  if(brandName != null && brandName.length() > 0)
				  newImportVariantProduct.set("brandName", brandName.trim());
			  if(productName != null && productName.length() > 0)
				  newImportVariantProduct.set("productName", productName.trim());
			  
			  if(description != null && description.length() > 0)
				  newImportVariantProduct.set("description", description.trim());
			  
			  if(longDescription != null && longDescription.length() > 0)
				  newImportVariantProduct.set("longDescription", longDescription.trim());
			  
			  if(smallImage != null && smallImage.length() > 0 )
		    	  newImportVariantProduct.set("smallImageUrl", smallImageUrl);
		      if(largeImage != null && largeImage.length() > 0)
		    	  newImportVariantProduct.set("largeImageUrl", largeImageUrl);
		      if(detailImage != null && detailImage.length() > 0)
		    	  newImportVariantProduct.set("detailImageUrl", detailImageUrl);
		      
		      if(defaultPrice != null && defaultPrice.length() > 0)
		    	  newImportVariantProduct.set("defaultPrice", new java.math.BigDecimal(defaultPrice.trim().toString()));
			  
			  
		      if(listPrice != null && listPrice.length() > 0)
		    	  newImportVariantProduct.set("listPrice", new java.math.BigDecimal(listPrice.trim().toString()));
		      
		      if(isExternal != null && isExternal.length() > 0)
		    	  newImportVariantProduct.set("isExternal", isExternal.trim());
		      newImportVariantProduct.set("stock", stock);
		      newImportVariantProduct.set("barcode", barcode.trim());
		      try {
		          delegator.create(newImportVariantProduct);
		          successCount++;
		      } catch (GenericEntityException e) {
		          Debug.logWarning(e.getMessage(), module);
		      }
		  } catch (Exception e) {
			  
			  continue;
	      }  
            
      }
      Debug.logInfo("Total row imported: - ["+ successCount +"].", module);
    }
    public static Map uploadAisleNumber(DispatchContext dctx, Map context) {
    	Map results = null;
    	try
        {
    		String appPath= System.getProperty("ofbiz.home");
    	  String serverPath = UtilProperties.getPropertyValue("dataimport", "file.server.path");
      	  String filename = appPath+serverPath+"/importFiles/productAisleExcel/productAisleExcel.xls";
          WorkbookSettings ws = new WorkbookSettings();
          ws.setLocale(new Locale("en", "EN"));
          Workbook workbook = Workbook.getWorkbook(new File(filename),ws);
          Sheet s  = workbook.getSheet(0);
          readAisleDataSheet(s, dctx);
          results = UtilMisc.toMap("message", "success");
          workbook.close();      
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        catch (BiffException e)
        {
          e.printStackTrace();
        }
        
        return results;
    }
    private static void readAisleDataSheet(Sheet s, DispatchContext dctx)   {
    	Delegator delegator = dctx.getDelegator(); 
      Cell rowData[] = null;
      int successCount = 0;
      int rows = s.getRows();
      
      
      int column = s.getColumns();
      
      for (int i = 1; i < rows; i++) {
    	  
    	  String productId = "";
    	  String aisleNumber = "";
          
    	  rowData = s.getRow(i);
    	  
    	  
    	  try{
    		if (rowData[0].getContents().length() != 0) { 
    			for (int j = 0; j < rowData.length; j++) {
    				
    				switch (j) {
    				case 0:
    					productId = rowData[j].getContents();
    					break;
    				case 1:
    					aisleNumber = rowData[j].getContents();
    					break;
    				
    				default:
    					break;
    				}
    			}
    		
    	
    	if(productId != null && productId.length() > 0)
    	{
    		GenericValue gv=delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",productId));
    		if(UtilValidate.isNotEmpty(aisleNumber) && UtilValidate.isNotEmpty(gv))
    		{
    			gv.set("aisleNumber", aisleNumber);
    			gv.store();
    			successCount++;
    		}
    			
    	}
    		}	
    		
    		
    	  	}catch(Exception e)
    	  	{
    	  		e.printStackTrace();
    	  		continue;
    	  	}

      }

      }
    
    
    public static String imageRemove(HttpServletRequest request,HttpServletResponse response) {
    	 String fileName = request.getParameter("imagePath");
    	 String filePath = System.getProperty("ofbiz.home");
    	 filePath +="/framework/images/webapp/images/products/"; 
		 try{
			 if(filePath != null && filePath != ""){
			 File file = new File(filePath+fileName);
	    		 if(file.exists()){
	    			 file.delete();
	    		 }else{
	    			 System.out.println("File not found");
	    		 }
			 }
		 }catch(Exception e){
			 
		 }
    	 return "success";
    }
    
    public static String imageFileUpload(HttpServletRequest request,HttpServletResponse response) {
    	String imageType = "";
    	StringBuffer ajaxUpdateResult = new StringBuffer();
        List<String> imagesList = new ArrayList<String>();
    	String filePath = System.getProperty("ofbiz.home");
        Map imagesMap = new HashMap();
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);       
            filePath +="/framework/images/webapp/images/products/";   
            for (FileItem item : items) {
            	if (item.isFormField()) {
              	  imageType =  item.getString();
              	  if(UtilValidate.isEmpty(imageType)){
              		  break; 
              	  }
              	  filePath +=imageType+"/";
                } else {
                	imagesMap.put("imageType",imageType);
                    String fileName = item.getName();
                    File file = new File(filePath+fileName);
                    file.createNewFile();
                    item.write(file);
                    imagesList.add(fileName);
                    
                    int i = fileName.lastIndexOf('.');
                   // String productId = null;
        			if (i > 0) {
        				//System.out.println(fileName.substring(0,i));
        			//	productId = fileName.substring(0,i) ;
        			} 
              }
            }
            imagesMap.put("imageList", imagesList);
            request.setAttribute("imagesMap", imagesMap);
        } catch (Exception  e) {
        }    	
  	  return "success";
    }
}