
package org.ofbiz.dataimport;
import java.util.Map;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.*;


import java.io.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * Import customers via intermediate DataImportCustomer entity.
 * Note that the actual logic for transforming each row into a set of opentaps
 * entities is implemented in the CustomerDecoder class.
 *
 * @author     <a href="mailto:sichen@opensourcestrategies.com">Si Chen</a> 
 * @author     <a href="mailto:leon@opensourcestrategies.com">Leon Torres</a> 
 */
public class CustomerImportServices {

    public static String module = CustomerImportServices.class.getName();
        public static Map<String, Object> importCustomers(DispatchContext dctx, Map<String, ?> context) {

        	Map results = null;
        	try
            {
              String serverPath = System.getProperty("ofbiz.home");
        	  String filename = serverPath+"/images/webapp/images/importFiles/customers/customers.xls";
        	   WorkbookSettings ws = new WorkbookSettings();
              ws.setLocale(new Locale("en", "EN"));
              Workbook workbook = Workbook.getWorkbook(new File(filename),ws);
              Sheet s  = workbook.getSheet(0);
              createDataImportCustomers(s, dctx);
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
            catch (Exception e)
            {
              e.printStackTrace();
            }
            return results;
        }
        private static void createDataImportCustomers(Sheet sheet, DispatchContext dctx)
        {
        	 Delegator delegator = dctx.getDelegator();
        	 int sheetLastRowNumber = sheet.getRows();
        	 for (int j = 1; j <= sheetLastRowNumber; j++) {
       
        	   // HSSFRow row = sheet.getRow(j);
        	    Cell rowData[] =sheet.getRow(j);
              
               try
                 {
            	   String salution =   rowData[0].getContents();
               
                  String firstName =    rowData[1].getContents();
                  String lastName =     rowData[2].getContents();
                  String officialNumber = rowData[3].getContents();
                  String mobileNumber =  rowData[4].getContents();
                  String officialMail =  rowData[5].getContents();
                  String personalMail =  rowData[6].getContents();
                  String address1 =    rowData[7].getContents();
                  String address2 =    rowData[8].getContents();
                  String postalCode = rowData[9].getContents();
                  String state =    rowData[10].getContents();
                  String country =  rowData[11].getContents();
                  
                   GenericValue party =delegator.makeValue("Party");
                   String  partyId = delegator.getNextSeqId("Party");
                   party.set("partyId", partyId);
                   party.set("partyTypeId" ,"PERSON");
                   party.set("statusId", "PARTY_ENABLED");
                   party.create();
                   GenericValue person =delegator.makeValue("Person");
                   person.set("partyId", partyId);
                   person.set("firstName", firstName);
                   person.set("lastName", lastName);
                   person.set("salutation", salution);
                   person.create();
                 }
                   catch(Exception e)
                   {
                	   e.printStackTrace();
                   }
                   
        	 }
        }
       
 

}
        

