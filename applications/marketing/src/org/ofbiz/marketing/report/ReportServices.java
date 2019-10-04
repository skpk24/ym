package org.ofbiz.marketing.report;

/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


/**
 * Opportunities services. The service documentation is in
 * services_opportunities.xml.
 */
public final class ReportServices {

	private ReportServices() {
	}

	private static final String MODULE = ReportServices.class.getName();

		 		  
	/**
	 * @author balakrishna.prabhakar	 
	 * simple utility to get the exact location where browsed file has been saved on local system  
	 * @return
	 */
	public static String getUploadPath() {
		return System.getProperty("user.dir") + File.separatorChar + "runtime"	+ File.separatorChar + "data" + File.separatorChar;	
	}
		  
	
		
													/* #########################################*/
													/* #########################################*/
													/* ## *********************************** ##*/      
													/* ##  Services for SFA reports           ##*/
													/* ## *********************************** ##*/   
													/* #########################################*/
													/* #########################################*/
													
	

	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Project Account By Opportunity Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String accountByOpportunityyReports(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("AccountsByOpportunities_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "AccountByOpportunity.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Account Name");
		excelInitRow.put(2,"Created Date");
		excelInitRow.put(3,"No.of Opportunities");
		excelInitRow.put(4,"Total Opportunity Cost");
		
	  	myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
				excelRows.put(0, srNoStr);

				if(!UtilValidate.isEmpty(prepareResult.get("partyId"))){
					String partyId = (String) prepareResult.get("partyId") ;
				    String accName = ""; 
				    String createDate = ""; 
					GenericValue person = null ;
					try {
						person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId",partyId ))	;
					} catch (GenericEntityException e) {
						// TODO: handle exception
					}
					if(!UtilValidate.isEmpty(person)){
						accName = person.getString("firstName") ;
						 if(!UtilValidate.isEmpty(person.getString("lastName"))){
							 accName = accName + "  " + person.getString("lastName")  ;
						 }
						createDate = person.getString("createdTxStamp");
					}
				excelRows.put(1,accName);
				excelRows.put(2,createDate);
				}else{
					excelRows.put(1,"");
					excelRows.put(2,"");
				}
				
				if(!UtilValidate.isEmpty(prepareResult.get("noOpp"))){
				excelRows.put(3,prepareResult.get("noOpp"));
				}else{
					excelRows.put(3,"");
				}
				
				if(!UtilValidate.isEmpty(prepareResult.get("totalAmount"))){
					excelRows.put(4,prepareResult.get("totalAmount"));
					}else{
						excelRows.put(4,"");
					}
			}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
	      
        
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }

        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "AccountByOpportunity.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
         
		return "success";

    	}

	 
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Project Account By Project Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String accountByProject(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("AccountByProject_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "AccountByProject.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Account Name");
		excelInitRow.put(2,"Created Date");
		excelInitRow.put(3,"No.of Projects");
		excelInitRow.put(4,"No.of Documents Uploaded");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
				excelRows.put(0, srNoStr);

				if(!UtilValidate.isEmpty(prepareResult.get("partyId"))){
					partyId = (String) prepareResult.get("partyId") ;
				    String accName = ""; 
				    String createDate = ""; 
					GenericValue person = null ;
					try {
						person = delegator.findByPrimaryKey("PartySummaryCRMView", UtilMisc.toMap("partyId",partyId ))	;
					} catch (GenericEntityException e) {
						// TODO: handle exception
					}
					if(!UtilValidate.isEmpty(person)){
						accName = person.getString("groupName") ;
						createDate = person.getString("createdDate");
					}
				excelRows.put(1,accName);
				excelRows.put(2,createDate);
				}else{
					excelRows.put(1,"");
					excelRows.put(2,"");
				}
				
				List numProj = null;
				
				try {
					numProj = delegator.findList("SalesOpportunity", EntityCondition.makeCondition("leadPartyId",EntityOperator.EQUALS,partyId), null, null, null, false);
				} catch (GenericEntityException e) {
					// TODO: handle exception
				}
				
				if(!UtilValidate.isEmpty(numProj)){
					Integer temp = new Integer(numProj.size());
				excelRows.put(3, temp.toString());
				}else{
					excelRows.put(3,"");
				}
				try {
					numProj = delegator.findList("PartyContent", EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId), null, null, null, false);
				} catch (GenericEntityException e) {
					// TODO: handle exception
				}
				if(!UtilValidate.isEmpty(numProj.size())){
					Integer temp = new Integer(numProj.size());
					excelRows.put(4,temp.toString());
					}else{
						excelRows.put(4,"");
					}
			}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "AccountByProject.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Project Account By Project Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String contactByStatus(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("ContactByStatus_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "ContactByStatus.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Contact Name");
		excelInitRow.put(2,"Company Name");
		excelInitRow.put(3,"Status");
		excelInitRow.put(4,"Created Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("contactName"))){
						excelRows.put(1,prepareResult.get("contactName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("cmpnyName"))){
						excelRows.put(2,prepareResult.get("cmpnyName"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("statusId"))){
						excelRows.put(3,prepareResult.get("statusId"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("createdDate"))){
						excelRows.put(4,prepareResult.get("createdDate"));
					}else{
						excelRows.put(4,"");
					}
					
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "ContactByStatus.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}

	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Merge Contact History Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String mergeContactHistory(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("MergeContactHistory_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "MergeContactHistory.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Contact Name");
		excelInitRow.put(2,"Merged With");
		excelInitRow.put(3,"Company");
		excelInitRow.put(4,"Created Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("fromName"))){
						excelRows.put(1,prepareResult.get("fromName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("toName"))){
						excelRows.put(2,prepareResult.get("toName"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("cmpnyName"))){
						excelRows.put(3,prepareResult.get("cmpnyName"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("createdDate"))){
						excelRows.put(4,prepareResult.get("createdDate"));
					}else{
						excelRows.put(4,"");
					}
					
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "MergeContactHistory.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Merge Contact History Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String companiesByStatus(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("CompaniesByStatus_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "CompaniesByStatus.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Company Name");
		excelInitRow.put(2,"Status");
		excelInitRow.put(3,"Converted Date");
		excelInitRow.put(4,"Created Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("cmpnyName"))){
						excelRows.put(1,prepareResult.get("cmpnyName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("status"))){
						excelRows.put(2,prepareResult.get("status"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("statusDate"))){
						excelRows.put(3,prepareResult.get("statusDate"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("createdDate"))){
						excelRows.put(4,prepareResult.get("createdDate"));
					}else{
						excelRows.put(4,"");
					}
					
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "CompaniesByStatus.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Merge Contact History Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String companiesByIndustryType(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("CompaniesByIndustryType_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "CompaniesByIndustryType.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Company Name");
		excelInitRow.put(2,"Industry Type");
		excelInitRow.put(3,"Industry Sub-Type");
		excelInitRow.put(4,"Created Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("cmpnyName"))){
						excelRows.put(1,prepareResult.get("cmpnyName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("industryType"))){
						excelRows.put(2,prepareResult.get("industryType"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("industrySubType"))){
						excelRows.put(3,prepareResult.get("industrySubType"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("createdDate"))){
						excelRows.put(4,prepareResult.get("createdDate"));
					}else{
						excelRows.put(4,"");
					}
					
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "CompaniesByIndustryType.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Opportunities By History Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String opportunitiesByHistory(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("OpportunitiesByHistory_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "OpportunitiesByHistory.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Opportunity Name");
		excelInitRow.put(2,"Project Name");
		excelInitRow.put(3,"Sales Head");
		excelInitRow.put(4,"Probability");
		excelInitRow.put(5,"Estimated Amount");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("opportunityName"))){
						excelRows.put(1,prepareResult.get("opportunityName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("projectName"))){
						excelRows.put(2,prepareResult.get("projectName"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("salesHead"))){
						excelRows.put(3,prepareResult.get("salesHead"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("estimatedProbability"))){
						excelRows.put(4,prepareResult.get("estimatedProbability"));
					}else{
						excelRows.put(4,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("estimatedAmount"))){
						excelRows.put(5,prepareResult.get("estimatedAmount"));
					}else{
						excelRows.put(5,"");
					}
					
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "OpportunitiesByHistory.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Opportunities By History Reports <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String opportunitiesByCompanies(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("OpportunitiesByCompanies_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "OpportunitiesByCompanies.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Opportunity Name");
		excelInitRow.put(2,"Project Name");
		excelInitRow.put(3,"Sales Head");
		excelInitRow.put(4,"Probability");
		excelInitRow.put(5,"Estimated Amount");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    String salesOpportunityId = (String) prepareResult.get("salesOpportunityId");
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(prepareResult.get("opportunityName"))){
						excelRows.put(1,prepareResult.get("opportunityName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("cmpnyName"))){
						excelRows.put(2,prepareResult.get("cmpnyName"));
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("salesHead"))){
						excelRows.put(3,prepareResult.get("salesHead"));
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("projectName"))){
						excelRows.put(4,prepareResult.get("projectName"));
					}else{
						excelRows.put(4,"");
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("estimatedProbability"))){
						excelRows.put(5,prepareResult.get("estimatedProbability"));
					}else{
						excelRows.put(5,"");
					}
				
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "OpportunitiesByCompanies.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	
	
	/**
	 * @author Balakrishna Prabhakar <br>
	 *  Company Targets Report Excel <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String companyTargetsReportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("CompanyTargetsReportExcel_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "CompanyTargetsReportExcel.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Targeted Lead Name");
		excelInitRow.put(2,"Target Cost");
		excelInitRow.put(3,"Targeted Best Case Cost");
		excelInitRow.put(4,"Targeted Closed Cost");
		excelInitRow.put(5,"Currency");
		excelInitRow.put(6,"Targeted From Date");
		excelInitRow.put(7,"Targeted To Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    partyId = (String) prepareResult.get("partyId");
	    	    GenericValue gv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(gv)){
						excelRows.put(1,gv.getString("firstName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("targetCost"))){
						excelRows.put(2,prepareResult.get("targetCost").toString());
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("bestCaseCost"))){
						excelRows.put(3,prepareResult.get("bestCaseCost").toString());
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("closedCost"))){
						excelRows.put(4,prepareResult.get("closedCost").toString());
					}else{
						excelRows.put(4,"");
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("currencyUomId"))){
						excelRows.put(5,prepareResult.get("currencyUomId"));
					}else{
						excelRows.put(5,"");
						
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("fromDate"))){
						excelRows.put(6,prepareResult.get("fromDate"));
					}else{
						excelRows.put(6,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("thruDate"))){
						excelRows.put(7,prepareResult.get("thruDate"));
					}else{
						excelRows.put(7,"");
					}
				
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "CompanyTargetsReportExcel.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}

	/**
	 * @author Balakrishna Prabhakar <br>
	 * Team Targets Report Excel <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String teamTargetsReportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("TeamTargetsReportExcel_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "TeamTargetsReportExcel.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Targeted Lead Name");
		excelInitRow.put(2,"Target Cost");
		excelInitRow.put(3,"Targeted Best Case Cost");
		excelInitRow.put(4,"Targeted Closed Cost");
		excelInitRow.put(5,"Currency");
		excelInitRow.put(6,"Targeted From Date");
		excelInitRow.put(7,"Targeted To Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    partyId = (String) prepareResult.get("partyId");
	    	    GenericValue gv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(gv)){
						excelRows.put(1,gv.getString("firstName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("targetCost"))){
						excelRows.put(2,prepareResult.get("targetCost").toString());
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("bestCaseCost"))){
						excelRows.put(3,prepareResult.get("bestCaseCost").toString());
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("closedCost"))){
						excelRows.put(4,prepareResult.get("closedCost").toString());
					}else{
						excelRows.put(4,"");
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("currencyUomId"))){
						excelRows.put(5,prepareResult.get("currencyUomId"));
					}else{
						excelRows.put(5,"");
						
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("fromDate"))){
						excelRows.put(6,prepareResult.get("fromDate"));
					}else{
						excelRows.put(6,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("thruDate"))){
						excelRows.put(7,prepareResult.get("thruDate"));
					}else{
						excelRows.put(7,"");
					}
				
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "TeamTargetsReportExcel.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}
	/**
	 * @author Balakrishna Prabhakar <br>
	 * Individual Targets Report Excel <br>
	 * <br> user cab download the resulted excel file to the desired location
	 * @throws GenericEntityException 
	 */
	public static String individualTargetsReportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException {
	      Locale locale = UtilHttp.getLocale(request);
	      LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	      Delegator delegator = (Delegator) request.getAttribute("delegator");
	      HttpSession session = request.getSession();
	      ArrayList  listIt =   (ArrayList) session.getAttribute("IndividualTargetsReportExcel_SFA_REPORTS");
	      
	      String fileName =getUploadPath() + "IndividualTargetsReportExcel.xls"; 	 
		     HSSFWorkbook myWorkBook = new HSSFWorkbook();
	         HSSFSheet mySheet = myWorkBook.createSheet();
	         HSSFRow myRow = null;
	         HSSFCell myCell = null;
	         
	
		File f = new File(fileName);
		f.createNewFile();
		int i = 0;
		if(!UtilValidate.isEmpty(listIt)){
		Map excelInitRow =   	new HashMap();
		excelInitRow.put(0,"Sr.No.");
		excelInitRow.put(1,"Targeted Lead Name");
		excelInitRow.put(2,"Target Cost");
		excelInitRow.put(3,"Targeted Best Case Cost");
		excelInitRow.put(4,"Targeted Closed Cost");
		excelInitRow.put(5,"Currency");
		excelInitRow.put(6,"Targeted From Date");
		excelInitRow.put(7,"Targeted To Date");
		
		myRow = mySheet.createRow(i++);
	    for (int cellNum = 0; cellNum <excelInitRow.size() ; cellNum++){
	  		myCell = myRow.createCell(cellNum);
	  		String s = (String) excelInitRow.get(cellNum);
            myCell.setCellValue(s);
            HSSFCellStyle style = myWorkBook.createCellStyle();
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM_DASHED);
            myCell.setCellStyle(style);
           
            HSSFFont font = myWorkBook.createFont();
            font.setFontHeightInPoints((short)10);
            font.setFontName("Arial");
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            myCell.setCellStyle(style);
	  	 }
	    String partyId = null;
	    for(int j = 0; j <listIt.size();j++){
	    	  Map<String, Object> prepareResult = (Map<String, Object>) listIt.get(j);
	    	    partyId = (String) prepareResult.get("partyId");
	    	    GenericValue gv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
	    	    Map excelRows =   	new HashMap();
				Integer srNo = new Integer(j+1);
				String srNoStr = srNo.toString();
				
				for(int size = 0 ;size<prepareResult.size();size++){
					excelRows.put(0, srNoStr);

					if(!UtilValidate.isEmpty(gv)){
						excelRows.put(1,gv.getString("firstName"));
					}else{
						excelRows.put(1,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("targetCost"))){
						excelRows.put(2,prepareResult.get("targetCost").toString());
					}else{
						excelRows.put(2,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("bestCaseCost"))){
						excelRows.put(3,prepareResult.get("bestCaseCost").toString());
					}else{
						excelRows.put(3,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("closedCost"))){
						excelRows.put(4,prepareResult.get("closedCost").toString());
					}else{
						excelRows.put(4,"");
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("currencyUomId"))){
						excelRows.put(5,prepareResult.get("currencyUomId"));
					}else{
						excelRows.put(5,"");
						
					}
					
					if(!UtilValidate.isEmpty(prepareResult.get("fromDate"))){
						excelRows.put(6,prepareResult.get("fromDate"));
					}else{
						excelRows.put(6,"");
					}
					if(!UtilValidate.isEmpty(prepareResult.get("thruDate"))){
						excelRows.put(7,prepareResult.get("thruDate"));
					}else{
						excelRows.put(7,"");
					}
				
				}
				myRow = mySheet.createRow(i++);
			    for (int cellNum = 0; cellNum <excelRows.size() ; cellNum++){
			  		myCell = myRow.createCell(cellNum);
			  		String s = (String) excelRows.get(cellNum);
		            myCell.setCellValue(s);
		        }
	    }
		}
		try{
            FileOutputStream out = new FileOutputStream(fileName);
            myWorkBook.write(out);
            out.close();
        }catch(Exception e){ e.printStackTrace();}  
        int length   = 0;
        ServletOutputStream op = null;
        try {
        	op = response.getOutputStream();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel" );
        response.setContentLength( (int)f.length() );
        response.setHeader( "Content-Disposition", "attachment; filename=\"" + "IndividualTargetsReportExcel.xls" + "\"" );
        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[2048];
        DataInputStream in = null;
        try {
        	in = new DataInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	while ((in != null) && ((length = in.read(bbuf)) != -1))
        	{
        	op.write(bbuf,0,length);
        	}
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        try {
        	in.close();
        	op.flush();
        	op.close();
        	f.delete();
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
         }
		return "success";
    	}

    }
