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

//import javolution.util.FastList;
//import javolution.util.HashMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
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
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
//import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
//import org.ofbiz.product.store.ProductStoreWorker;





import java.io.*;

import jxl.*;
import java.util.*;
import jxl.Workbook;
import jxl.read.biff.BiffException;
/**
 * Product Information Related Events
 */
public class ImportEvents {

    public static final String module = ImportEvents.class.getName();
    public static final String resource = "DataimportUiLabels";
   
    protected static String initialResponsiblePartyId="admin";
    protected static String initialResponsibleRoleTypeId="MANAGER";
    protected static String organizationPartyId="Company";
    protected static String arGlAccountId="120000";
    protected static String offsettingGlAccountId="";
    
    public static String baseCurrencyUomId = UtilProperties.getPropertyValue("general", "currency.uom.id.default");
    
    protected static  GenericValue userLogin;
  
     
    public static Map importProducts(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Timestamp now = UtilDateTime.nowTimestamp();
        String message ="";
        int imported = 0;
     int count;
        Debug.log("\n\n in import Prducts =");
        boolean beganTransaction = false;
        try {
        	EntityWhereString ews=EntityWhereString.makeConditionWhere("message IS NULL");
            List products = delegator.findList("DataImportProduct", ews.freeze(), null,UtilMisc.toList("isVirtual ASC"), null,false);
             Debug.log("\n\n in import Prducts size ="+products.size());
            
             beganTransaction = TransactionUtil.begin();
             if(beganTransaction){
             if (products.size() > 0) {
	            Iterator productsItr = products.iterator();
	            while (productsItr.hasNext()) {
	                GenericValue product = (GenericValue)productsItr.next();
	                    message = decodeProduct(product, now, delegator, userLogin);
 	                    if(message.equals("success") && UtilValidate.isNotEmpty(product.getString("virtualStock"))){
		                    try {
		                    	message = createInventory(product, now, delegator, userLogin, dispatcher);
		                    	Debug.logInfo("Variant Product Created for [" + product.get("productId") + "]", module);
		                    } catch (Exception e) {
			                   
			                    //System.out.println("ERRORHERE$$$$$");
			                    message = "Error During Varinat Product Creation";
			                }
	                    }  
   
	                    
	                    
//	                    if(message.equals("success")){
//		                    try {
//		                    	message = createVariant(product, now, delegator, userLogin, dispatcher);
//		                    	Debug.logInfo("Variant Product Created for [" + product.get("productId") + "]", module);
//		                    } catch (Exception e) {
//			                    TransactionUtil.rollback();
//			                    message = "Error During Varinat Product Creation";
//			                }
//	                    }    
	               
	                    if(message.equals("success")){
	                    delegator.removeValue(product);
	                    }else{
	                    	product.set("message", message);
	                    	delegator.store(product);
	                    }
	               
	                
//	                
	            	}
             	}     
            }
        } 
        catch (GenericEntityException e) {
            try {
                // only rollback the transaction if we started one...
                TransactionUtil.rollback(beganTransaction, "Error updating product", e);
            } catch (GenericEntityException e2) {
                Debug.logError(e2, "[GenericDelegator] Could not rollback transaction: " + e2.toString(), module);
            }

            String errMsg = "Error updating the product" + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        } 
        finally {
            try {
                // only commit the transaction if we started one... this will throw an exception if it fails
                TransactionUtil.commit(beganTransaction);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Could not commit transaction for creating new shopping list based automatic reorder", module);
            }
        }
        
        Map results = ServiceUtil.returnSuccess();
        results.put("productsImported", new Integer(imported));
        return results;
    }
    
     
    
private static String decodeProduct(GenericValue data, Timestamp now, Delegator delegator, GenericValue userLogin)  {
    	
    	Map input;
    	String message = "success";
        Debug.logInfo("Now processing  data [" + data.get("productId") + "] description [" + data.get("description") + "]", module);
        input = new HashMap();
        List tobeStored = new LinkedList();
        String isVirtual =  data.getString("isVirtual");
        String priceToTake =  data.getString("priceToTake");
        
        boolean isvirtualFlag = false ;
        boolean isSimple = false ;
        
     String productId = data.getString("productId").trim();
     String internalName = null;
     String productTypeId = "FINISHED_GOOD";
     String productFeatureCategoryId  = data.getString("productFeatureCategoryId");
     String virtualId =  data.getString("isVirtual");
	GenericValue parentProductForVariant = ProductWorker.getParentProductWithoutCache(productId, delegator);
	GenericValue product =null;
	GenericValue 	simpleProduct   =null;
	GenericValue virtualProduct = null;
	
	try{
		 simpleProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",productId));
	}catch(Exception e){
		//System.out.println("\n\n\n\n cannot be completed");
	}
	
	if(UtilValidate.isNotEmpty(simpleProduct)){
		internalName = simpleProduct.getString("internalName");
	}else{
		internalName = data.getString("internalName");
	}
 	if(UtilValidate.isEmpty(parentProductForVariant)){
	try{
	  virtualProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",virtualId));
 	}catch(Exception e){
		//System.out.println("\n\n\n\n cannot be completed");
	}
	}	
 
	
	if(UtilValidate.isEmpty(productFeatureCategoryId) &&  UtilValidate.isEmpty(virtualId)  &&  UtilValidate.isEmpty(parentProductForVariant)){
		isSimple = true;
	} else{
		isvirtualFlag = true;
	}
	
		if(isvirtualFlag){
				if(UtilValidate.isNotEmpty(virtualProduct) || (UtilValidate.isNotEmpty(parentProductForVariant) &&  parentProductForVariant.getString("productId").equalsIgnoreCase(virtualId))){
					if(UtilValidate.isNotEmpty(virtualProduct)){
 						virtualId = virtualProduct.getString("productId");
					}else{
					virtualProduct = parentProductForVariant;
					virtualId = parentProductForVariant.getString("productId");
					}
				}else{
					//System.out.println("\n\n\n\n\n\n data_import_product going here");
					  if(!virtualId.startsWith("VPRD") && UtilValidate.isEmpty(virtualProduct)){
						  virtualProduct = delegator.makeValue("Product");
						  virtualProduct.put("productId", virtualId);
 					} else 
 						if(UtilValidate.isEmpty(virtualProduct)){
 						virtualProduct = delegator.makeValue("Product");
						 virtualProduct.put("productId", virtualId);
 					}
				}
		
			if(UtilValidate.isNotEmpty(priceToTake) && priceToTake.equalsIgnoreCase("Y")){
				
				 if(UtilValidate.isNotEmpty(simpleProduct)){
						virtualProduct.put("inventoryAtp", simpleProduct.getBigDecimal("inventoryAtp")) ; 
						virtualProduct.put("basePrice", simpleProduct.getBigDecimal("basePrice"));
						if(data.get("smallImageUrl") != null)
						  virtualProduct.put("smallImageUrl", simpleProduct.getString("smallImageUrl"));
				           if(data.get("largeImageUrl") != null)
				        	  virtualProduct.put("largeImageUrl",  simpleProduct.getString("largeImageUrl"));
				          if(data.get("detailImageUrl") != null)
				         	  virtualProduct.put("detailImageUrl",  simpleProduct.getString("detailImageUrl"));
				          
				      	virtualProduct.put("internalName", simpleProduct.getString("internalName"));
						virtualProduct.put("productTypeId",productTypeId);
						virtualProduct.put("longDescription", simpleProduct.getString("longDescription"));
						virtualProduct.put("productName", simpleProduct.getString("productName"));
						virtualProduct.put("brandName", simpleProduct.getString("brandName"));
			             virtualProduct.put("mediumImageUrl", simpleProduct.getString("mediumImageUrl"));
			             virtualProduct.put("description", simpleProduct.getString("description"));

							
					 
				 }else{
				virtualProduct.put("inventoryAtp", new BigDecimal(data.getString("virtualStock"))) ; 
				virtualProduct.put("basePrice", data.getBigDecimal("basePrice"));
				if(data.get("smallImageUrl") != null)
				  virtualProduct.put("smallImageUrl", data.get("smallImageUrl"));
		           if(data.get("largeImageUrl") != null)
		        	  virtualProduct.put("largeImageUrl", data.get("largeImageUrl"));
		          if(data.get("detailImageUrl") != null)
		         	  virtualProduct.put("detailImageUrl", data.get("detailImageUrl"));
		          
		          if(data.get("detailImageUrl") != null){
		            	 String mediumImageUrl =  "/images/products/medium/" + productId + ".jpg";
		            	 virtualProduct.put("mediumImageUrl", mediumImageUrl);  
		              }
		          
		          virtualProduct.put("internalName", internalName);
					virtualProduct.put("productTypeId",productTypeId);
					virtualProduct.put("longDescription", data.get("longDescription"));
					virtualProduct.put("productName", data.get("productName"));
					virtualProduct.put("brandName", data.get("brandName"));
		             virtualProduct.put("description", data.get("description"));

				 }
 			}
		
			virtualProduct.put("isVirtual","Y");
			virtualProduct.put("isVariant", "N");
			tobeStored.add(virtualProduct);
		}
		
		 input.put("productId", productId);
		 
		  if(UtilValidate.isEmpty(simpleProduct)){
             input.put("productTypeId",productTypeId);
             input.put("internalName", internalName);
             input.put("brandName", data.get("brandName"));
             input.put("description", data.get("description"));
             input.put("longDescription", data.get("longDescription"));
             input.put("productName", data.get("productName"));
             if(data.get("smallImageUrl") != null)
              	input.put("smallImageUrl", data.get("smallImageUrl"));
              if(data.get("largeImageUrl") != null)
              	input.put("largeImageUrl", data.get("largeImageUrl"));
              if(data.get("detailImageUrl") != null)
              	input.put("detailImageUrl", data.get("detailImageUrl"));
              
              if(data.get("detailImageUrl") != null){
            	 String mediumImageUrl =  "/images/products/medium/" + productId + ".jpg";
                	input.put("mediumImageUrl", mediumImageUrl);  
              }
              
              
              input.put("isVirtual", "N");
              if(isvirtualFlag){
              input.put("isVariant", "Y");
              input.put("primaryProductCategoryId", data.get("categoryOne"));
              }else{
                  input.put("primaryProductCategoryId", data.get("categoryOne"));
                  input.put("isVariant", "N");
              }
              input.put("barcode", data.get("barcode")) ;  
              input.put("inventoryAtp", new BigDecimal(data.getString("virtualStock"))) ; 
              input.put("isExternal", data.get("isExternal")) ;   
             input.put("createdDate", now);
             input.put("createdByUserLogin", userLogin.get("userLoginId"));  
             input.put("lastModifiedDate", now);
             input.put("basePrice", data.getBigDecimal("defaultPrice"));
             input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		  }
             
             try	{
     	        if(UtilValidate.isNotEmpty(simpleProduct) && isvirtualFlag ){
     	        	simpleProduct.put("isVirtual", "N");
     	        	simpleProduct.put("isVariant", "Y");
     	        	tobeStored.add(simpleProduct);
     	        }else{
             	  product = delegator.makeValue("Product", input);
             	tobeStored.add(product);
     	        }
     	        
     	       java.math.BigDecimal defaultPrice  = null;
     	      java.math.BigDecimal listPrice =  null;
     	      
     	       if(UtilValidate.isEmpty(simpleProduct)){
     	       defaultPrice = data.getBigDecimal("defaultPrice");
     	       listPrice =  data.getBigDecimal("listPrice");
     	       }
     	            
 // Adding default price to the product     
         
	        if(defaultPrice != null){
	        	boolean priceFound = false;
	        	input = new HashMap();
	            input.put("productId", productId);//ProductId from the excel List means it's variant or simple product
	            input.put("productPriceTypeId", "DEFAULT_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("currencyUomId", baseCurrencyUomId);
 	            
	            List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
				  List<GenericValue>  newValue  = EntityUtil.filterByDate(productPrice);
//				  List<GenericValue>   toBeUpdated   = new LinkedList();
//				  input.put("price", defaultPrice);
//				  for(GenericValue gv : newValue ){
//					  BigDecimal bd = gv.getBigDecimal("price");
//					  int diff = bd.compareTo(defaultPrice);
//					  if(diff == 0){
//						  priceFound = true;
//					  }else{
//						  gv.set("thruDate", UtilDateTime.nowTimestamp());
//						  toBeUpdated.add(gv);
//					  }
//					  
//				  }
//				  if(!priceFound){
//					  tobeStored.addAll(toBeUpdated);
//					  input.put("fromDate", UtilDateTime.nowTimestamp());
//		            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
//		            	tobeStored.add(productListPrice);
//					  
//				  }
	            
					if(UtilValidate.isEmpty(newValue)){
				  		 input.put("price", defaultPrice);
				  		 input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				  		input.put("fromDate", UtilDateTime.nowTimestamp());
		            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
		            	tobeStored.add(productListPrice);
				  	}else{
				  		 input.put("price", defaultPrice);
	 		             GenericValue productListPrice = newValue.get(0);
	 		            productListPrice.put("price", defaultPrice);
			            tobeStored.add(productListPrice);
				  	}
	        }
	        
	        // Adding list price to the product 	      
	        if(listPrice != null){
	        	boolean priceFound = false;
	        	input = new HashMap();
	            input.put("productId", productId);  //ProductId from the excel List means it's variant or simple product
	            input.put("productPriceTypeId", "LIST_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("currencyUomId", baseCurrencyUomId);
	           
			  List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
			  List<GenericValue>   newValue  = EntityUtil.filterByDate(productPrice);
			  
//			  List<GenericValue>   toBeUpdated   = new LinkedList();
//			  input.put("price", listPrice);
//			  for(GenericValue gv : newValue ){
//				  BigDecimal bd = gv.getBigDecimal("price");
//				  int diff = bd.compareTo(listPrice);
//				  if(diff == 0){
//					  priceFound = true;
//				  }else{
//					  gv.set("thruDate", UtilDateTime.nowTimestamp());
//					  toBeUpdated.add(gv);
//				  }
//				  
//			  }
//			  if(!priceFound){
//				  tobeStored.addAll(toBeUpdated);
//				  input.put("fromDate", UtilDateTime.nowTimestamp());
//	            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
//	            	tobeStored.add(productListPrice);
//				  
//			  }
			  	if(UtilValidate.isEmpty(newValue)){
			  		 input.put("price", listPrice);
			  		input.put("fromDate", UtilDateTime.nowTimestamp());
			  	  input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
	            	tobeStored.add(productListPrice);
			  	}else{
			  		 input.put("price", listPrice);
 		             GenericValue productListPrice = newValue.get(0);
 		            productListPrice.put("price", listPrice);
		            tobeStored.add(productListPrice);
			  	}
	        }
	        
	        
	        String categoryOne = data.getString("categoryOne");
	        String[] categoryName = categoryOne.split("~~");
	        
	        //Category Association Removing
	        if(UtilValidate.isNotEmpty(simpleProduct) && isvirtualFlag ){
 	        	  for(String eachCategory : categoryName){
 	 	        	input = new HashMap();
	       			input.put("productCategoryId", eachCategory.trim());
	       			input.put("productId", productId); //ProductId from the excel List means it's variant or simple product
	       			List<GenericValue> productCategoryMember = delegator.findByAnd("ProductCategoryMember",input);
	       	        if(productCategoryMember.size()>0){
	       	        	//System.out.println("\n\n\n\n entered hjere");
	       		        	GenericValue productCategoryOne = productCategoryMember.get(0);
	       		        	productCategoryOne.set("thruDate", UtilDateTime.nowTimestamp());
	       		        	tobeStored.add(productCategoryOne);
	       	        }
	       }      
 	   }
	        
	        
	         
	        
	        if(UtilValidate.isNotEmpty(simpleProduct) && UtilValidate.isNotEmpty(priceToTake) && priceToTake.equalsIgnoreCase("Y")){
	        	  defaultPrice = simpleProduct.getBigDecimal("basePrice");
	        	  
		        	input = new HashMap();
		            input.put("productId", productId);  //ProductId from the excel List means it's variant or simple product
		            input.put("productPriceTypeId", "LIST_PRICE");
		            input.put("productPricePurposeId", "PURCHASE");
		            input.put("productStoreGroupId", "_NA_");
//		            input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
		            input.put("currencyUomId", baseCurrencyUomId);
		           
				  List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
				  List<GenericValue>   newValue  = EntityUtil.filterByDate(productPrice);
				  
				 	if((newValue.size()>0)){
				 		GenericValue tt = newValue.get(0);
				 		listPrice = tt.getBigDecimal("price");
				 	}
	        }
	        
	      
	    
	        if(UtilValidate.isNotEmpty(priceToTake) && priceToTake.equalsIgnoreCase("Y") && isvirtualFlag && defaultPrice != null ){
	        	boolean priceFound = false;
	        	input = new HashMap();
	            input.put("productId", virtualId);//ProductId from the excel List means it's variant or simple product
	            input.put("productPriceTypeId", "DEFAULT_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("currencyUomId", baseCurrencyUomId);
	            
	            
	            List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
	            List<GenericValue>   newValue  = EntityUtil.filterByDate(productPrice);
//	            List<GenericValue>   toBeUpdated   = new LinkedList();
//				  input.put("price", defaultPrice);
//				  for(GenericValue gv : newValue ){
//					  BigDecimal bd = gv.getBigDecimal("price");
//					  int diff = bd.compareTo(defaultPrice);  
//					  if(diff == 0){
//						  priceFound = true;
//					  }else{
//						  gv.set("thruDate", UtilDateTime.nowTimestamp());
//						  toBeUpdated.add(gv);
//					  }
//					  
//				  }
//				  if(!priceFound){
//					  tobeStored.addAll(toBeUpdated);
//					  input.put("fromDate", UtilDateTime.nowTimestamp());
//		            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
//		            	tobeStored.add(productListPrice);
//					  
//				  }
	            
//				 	if(!(productPrice.size()>0)){
//				 		 
//				 	input.put("fromDate", UtilDateTime.nowTimestamp());
//	            	GenericValue productDefaultPrice = delegator.makeValue("ProductPrice", input);
//	            	tobeStored.add(productDefaultPrice);
//				 	}
	            
	        	if(UtilValidate.isEmpty(newValue)){
			  		 input.put("price", defaultPrice);
			  		 input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
			  		input.put("fromDate", UtilDateTime.nowTimestamp());
	            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
	            	tobeStored.add(productListPrice);
			  	}else{
			  		 input.put("price", defaultPrice);
		             GenericValue productListPrice = newValue.get(0);
		             productListPrice.put("price", defaultPrice);
		            tobeStored.add(productListPrice);
			  	}
	        	
	        	
 			}
	        
	        
	        if(UtilValidate.isNotEmpty(priceToTake) && priceToTake.equalsIgnoreCase("Y") && isvirtualFlag && listPrice != null ){
	        	boolean priceFound = false;
	        	input = new HashMap();
	            input.put("productId", virtualId);  //ProductId from the excel List means it's variant or simple product
	            input.put("productPriceTypeId", "LIST_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("currencyUomId", baseCurrencyUomId);
	            
	            List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
	            List<GenericValue>   newValue  = EntityUtil.filterByDate(productPrice);
	            
//	            List<GenericValue>   toBeUpdated   = new LinkedList();
//				  input.put("price", listPrice);
//				  for(GenericValue gv : newValue ){
//					  BigDecimal bd = gv.getBigDecimal("price");
//					  int diff = bd.compareTo(listPrice);
//					  if(diff == 0){
//						  priceFound = true;
//					  }else{
//						  gv.set("thruDate", UtilDateTime.nowTimestamp());
//						  toBeUpdated.add(gv);
//					  }
//					  
//				  }
//				  if(!priceFound){
//					  tobeStored.addAll(toBeUpdated);
//					  input.put("fromDate", UtilDateTime.nowTimestamp());
//		            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
//		            	tobeStored.add(productListPrice);
//					  
//				  }
//	            
//	            input.put("price", listPrice);
//			  List<GenericValue> productPrice = delegator.findByAnd("ProductPrice",input);
//			  	if(!(productPrice.size()>0)){
//			  		input.put("fromDate", UtilDateTime.nowTimestamp());
//	            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
//	            	tobeStored.add(productListPrice);
//			  	}
				  
				  if(UtilValidate.isEmpty(newValue)){
				  		 input.put("price", listPrice);
				  		input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
				  		input.put("fromDate", UtilDateTime.nowTimestamp());
		            	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
		            	tobeStored.add(productListPrice);
				  	}else{
				  		 input.put("price", listPrice);
			             GenericValue productListPrice = newValue.get(0);
			             productListPrice.put("price", listPrice);
			            tobeStored.add(productListPrice);
				  	}
				  
				  
 			}
	        
	        
          
        
	      
//associate product to category 	
	        if(isvirtualFlag || isSimple){
	        String vid = isvirtualFlag?virtualId:productId; 
	        for(String eachCategory : categoryName){
	       List<GenericValue> categoryList = delegator.findByAnd("ProductCategory", UtilMisc.toMap("productCategoryId",  eachCategory.trim()));
       	input = new HashMap();
			input.put("productCategoryId", eachCategory.trim());
			input.put("productId", vid); //ProductId from the excel List means it's variant or simple product
			List<GenericValue> productCategoryMember = delegator.findByAnd("ProductCategoryMember",input);
	        if(eachCategory != null && !(productCategoryMember.size()>0)){
		        input.put("fromDate", UtilDateTime.nowTimestamp());
		        	GenericValue productCategoryOne = delegator.makeValue("ProductCategoryMember", input);
		        	tobeStored.add(productCategoryOne);
	        }
}      
	        } 
     
	        if(!UtilValidate.isEmpty(data.getString("barcode"))){
	        	String[] barCodeArray = data.getString("barcode").split("~~");
	        	for(String bCode : barCodeArray){
		        	input = new HashMap();
		        input.put("barcode", bCode.trim());
		        input.put("productId", productId); //ProductId from the excel List means it's variant or simple product
		        	GenericValue productBarcode = delegator.makeValue("ProductBarcode", input);
		        	tobeStored.add(productBarcode);
	        	}	
	        }
    


 	        List<String> featureIdList =new ArrayList();
 	       if(UtilValidate.isNotEmpty(parentProductForVariant) ||  UtilValidate.isNotEmpty(productFeatureCategoryId)){
       		GenericValue prodFeature=delegator.findByPrimaryKey("ProductFeature", UtilMisc.toMap("productFeatureId",productFeatureCategoryId));
   		         if (UtilValidate.isNotEmpty(prodFeature)){
 				    			       Map  featureAppl = new HashMap();
				    			       featureAppl.put("productId", virtualId);  //virtualId
//				    			       featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
				    			       featureAppl.put("productFeatureApplTypeId", "SELECTABLE_FEATURE");
				    			       List<GenericValue> productFeatureAppl = delegator.findByAnd("ProductFeatureAppl",featureAppl);
 				    			       GenericValue  productFeatures = null;
 				    			      boolean entered = true;
 				    			      if(UtilValidate.isNotEmpty(productFeatureAppl) && productFeatureAppl.size()>0){
 				    			    	List<GenericValue> temp =  EntityUtil.filterByAnd(productFeatureAppl, UtilMisc.toMap("productFeatureId", prodFeature.get("productFeatureId")));
 				    			    	 if(UtilValidate.isNotEmpty(temp) && temp.size()>0){
 				    			    		 
// 				    			    		 System.out.println("\n\n\n\n\n\n\n went here");
 				    			    		productFeatures = temp.get(0);
 				    			    	 }else{
 				    			    		 List<GenericValue> temp1 =  new LinkedList();
				    			    		 temp1 = EntityUtil.orderBy(productFeatureAppl, UtilMisc.toList("sequenceNum ASC"));
				    			    		 Long currentSeqString =  new Long("0");
				    			    		 Long usethis =  new Long("0");
				    			    		 for(GenericValue gv : temp1 ){
				    			    			 Long dbSeqNum  =  gv.getLong("sequenceNum");
  				    			    			   currentSeqString = new BigDecimal(data.getString("sequenceNum")).longValue();
  				    			    			   long dbSeq = dbSeqNum.longValue();
  				    			    			   long csq = currentSeqString.longValue();
  				    			    			 if( (dbSeqNum == currentSeqString) && entered){
//				    			    			if( (dbSeq == csq) && entered){
				    			    				 featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
			 						    			 featureAppl.put("sequenceNum",currentSeqString);
			 								         featureAppl.put("fromDate", now);
			 								         productFeatures = delegator.makeValue("ProductFeatureAppl", featureAppl);
											         tobeStored.add(productFeatures);
											         currentSeqString = ++currentSeqString;
											         usethis = currentSeqString;
											         gv.put("sequenceNum",currentSeqString);
											         tobeStored.add(gv);
											         entered = false;
				    			    			}else{
				    			    			 
				    			    				if(!entered){
 				    			    					usethis = ++usethis;
				    			    					 gv.put("sequenceNum",usethis);
						    			    			    tobeStored.add(gv);
						    			    			    } else{
						    			    			    }
				    			    			   
				    			    			}
				    			    			 
				    			    		 }
				    			    		 if(entered){
				    			    			 featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
		 						    			 featureAppl.put("sequenceNum",new BigDecimal(data.getString("sequenceNum")).longValue());
		 								         featureAppl.put("fromDate", now);
		 								         productFeatures = delegator.makeValue("ProductFeatureAppl", featureAppl);
										         tobeStored.add(productFeatures);
				    			    		 }
				    			    		 
				    			    		 
				    			    		 
//				    			    		 if(UtilValidate.isNotEmpty(temp1) && temp1.size()>0){
//				    			    			 Long seqNum = temp1.get(0).getLong("sequenceNum");
////				    			    			 Long seqNum = data.getString("sequenceNum");
//	 						    			     featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
//	 						    			     featureAppl.put("sequenceNum",new BigDecimal(seqNum.longValue()).add(new BigDecimal("1")).longValue());
//	 								        	 featureAppl.put("fromDate", now);
//	 										     productFeatures = delegator.makeValue("ProductFeatureAppl", featureAppl);
//	 				    			    	}  
 				    			    	 }
 				    			      }else{
	 				    			    		 BigDecimal seqNum = BigDecimal.ZERO;;
	 				    			    		 seqNum = seqNum.add(new BigDecimal("1"));
	 						    			     featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
	 						    			     featureAppl.put("sequenceNum",seqNum.longValue());
	 								        	 featureAppl.put("fromDate", now);
	 										     productFeatures = delegator.makeValue("ProductFeatureAppl", featureAppl);
	 				    			    	 
				    			    	 }
								           tobeStored.add(productFeatures);
								           
								           
								           ////////////////////////////////
								           Map  featureApplForVariant = new HashMap();
								           featureApplForVariant.put("productId", productId);  //prod
//					    			       featureAppl.put("productFeatureId", prodFeature.get("productFeatureId"));
								           featureApplForVariant.put("productFeatureApplTypeId", "STANDARD_FEATURE");
					    			       List<GenericValue> productFeatureApplForVariants = delegator.findByAnd("ProductFeatureAppl",featureAppl);
	 				    			       GenericValue  productFeaturesForVariant = null;
	 				    			       
	 				    			      if(UtilValidate.isNotEmpty(productFeatureApplForVariants) && productFeatureApplForVariants.size()>0){
	 				    			    	List<GenericValue> temp =  EntityUtil.filterByAnd(productFeatureApplForVariants, UtilMisc.toMap("productFeatureId", prodFeature.get("productFeatureId")));
	 				    			    	 if(UtilValidate.isNotEmpty(temp) && temp.size()>0){
	 				    			    		productFeaturesForVariant = temp.get(0);
	 				    			    	 }else{
					    			    		 List<GenericValue> temp1 = EntityUtil.orderBy(productFeatureApplForVariants, UtilMisc.toList("sequenceNum DESC"));
					    			    		 if(UtilValidate.isNotEmpty(temp1) && temp1.size()>0){
					    			    			 String seqNum = data.getString("sequenceNum");
					    			    			 if(UtilValidate.isEmpty(seqNum)){
					    			    				 seqNum = "0";
					    			    			 }
 					    			    			 featureApplForVariant.put("productFeatureId", prodFeature.get("productFeatureId"));
					    			    			 featureApplForVariant.put("sequenceNum",new BigDecimal(seqNum).longValue());
					    			    			 featureApplForVariant.put("fromDate", now);
					    			    			 productFeaturesForVariant = delegator.makeValue("ProductFeatureAppl", featureAppl);
		 				    			    	}  
	 				    			    	 }
	 				    			      }else{
			 				    			    	 String seqNum = data.getString("sequenceNum");
					    			    			 if(UtilValidate.isEmpty(seqNum)){
					    			    				 seqNum = "0";
					    			    			 }
		 				    			    		featureApplForVariant.put("productFeatureId", prodFeature.get("productFeatureId"));
		 						    			     featureAppl.put("sequenceNum",new BigDecimal(seqNum).longValue());
		 				    			    		featureApplForVariant.put("fromDate", now);
		 				    			    		productFeaturesForVariant = delegator.makeValue("ProductFeatureAppl", featureApplForVariant);
		 				    			    	 
					    			    	 }
									           tobeStored.add(productFeaturesForVariant);
								           
								           ///////////////////////////////////////
								           Map  prodAssoc = new HashMap();
								           prodAssoc.put("productId", virtualId);
								           prodAssoc.put("productIdTo", productId);
								           prodAssoc.put("productAssocTypeId", "PRODUCT_VARIANT");
								           List<GenericValue> productAssocList = delegator.findByAnd("ProductAssoc",prodAssoc);
								           GenericValue productAssoc = null;
								           if(UtilValidate.isNotEmpty(productAssocList) && productAssocList.size()>0){
								        	     productAssoc = productAssocList.get(0);
 								           }else{
								        	   prodAssoc.put("fromDate", now);
									           prodAssoc.put("sequenceNum",new BigDecimal("10").longValue());  
										         productAssoc = delegator.makeValue("ProductAssoc", prodAssoc);
								           }
									       tobeStored.add(productAssoc);
		         } 
	        }
			    	   delegator.storeAll(tobeStored);
	             }catch(Exception e){
              	message = e.toString();
          	}
        return message;
    }
    
     
    public static Map importVariantProductImage(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Timestamp now = UtilDateTime.nowTimestamp();
        int imported = 0;
        try {
            List<GenericValue> variantProducts = delegator.findList("DataImportProduct",  EntityCondition.makeCondition("message", EntityOperator.EQUALS, null), null,null, null,false);
             TransactionUtil.commit();
            if (variantProducts != null) {
            		for(GenericValue variantProduct : variantProducts)       {
                    try {
                    	Map toStore = decodeVariantProduct(variantProduct, now, delegator, dispatcher, userLogin);
	                    if (toStore == null) {
	                        Debug.logWarning("Faild to import variant product["+variantProduct.get("productId")+"] because data was bad.  Check preceding warnings for reason.", module);
	                    }
	                } catch (Exception e) {
	                    TransactionUtil.rollback();
	                    Debug.logError(e, "Faild to import variant product["+variantProduct.get("productId")+"]. Error stack follows.", module);
	                }
	            }
            }   
 
        } catch (GenericEntityException e) {
            String message = "Cannot import variant products: Unable to use delegator to retrieve data from the database.  Error is: " + e.getMessage();
            Debug.logError(e, message, module);
            return ServiceUtil.returnError(message);
        }

        Map results = ServiceUtil.returnSuccess();
        results.put("variantProductsImported", new Integer(imported));
        return results;
    }
    public static String importVariantProduct(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        Timestamp now = UtilDateTime.nowTimestamp();

        int imported = 0;
        try {
            EntityConditionList conditions = EntityConditionList.makeCondition( UtilMisc.toList(
            		 EntityCondition.makeCondition("exportVariantProdSeqId", EntityOperator.NOT_EQUAL, null),
            		 EntityCondition.makeCondition("processedTimestamp", EntityOperator.EQUALS, null)   
                        ), EntityOperator.AND);
            TransactionUtil.begin();   
            EntityListIterator importVariantProducts = delegator.find("DataImportVariantProduct", conditions, null, null, null, null);
            List variantProducts = importVariantProducts.getCompleteList();
            TransactionUtil.commit();

            if (variantProducts != null) {
	            Iterator variantProductsItr = variantProducts.iterator();
	            while (variantProductsItr.hasNext()) {
	                GenericValue variantProduct = (GenericValue)variantProductsItr.next();
	                

                    try {
                    	Map message = decodeVariantProduct(variantProduct, now, delegator, dispatcher, userLogin);
                    	
                    	if(message.get("responseMessage").equals("success")){
 		                    try {
 		                    	String message1 = createInventoryProduct(variantProduct, now, delegator, userLogin, dispatcher,request,response);
 		                    	
 		                    } catch (Exception e) {
 			                    TransactionUtil.rollback();
 			                   
 			                   
 			                }
 	                    }   
	                    if (message == null) {
	                        Debug.logWarning("Faild to import variant product["+variantProduct.get("productId")+"] because data was bad.  Check preceding warnings for reason.", module);
	                    }
	                } catch (Exception e) {
	                    TransactionUtil.rollback();
	                    Debug.logError(e, "Faild to import variant product["+variantProduct.get("productId")+"]. Error stack follows.", module);
	                }
	                
	            }
            }   
            importVariantProducts.close();

        } catch (GenericEntityException e) {
            String message = "Cannot import variant products: Unable to use delegator to retrieve data from the database.  Error is: " + e.getMessage();
            Debug.logError(e, message, module);
            return "error";
        }

        Map results = ServiceUtil.returnSuccess();
        results.put("variantProductsImported", new Integer(imported));
        return "success";
    }
    
    private static Map decodeVariantProduct(GenericValue data, Timestamp now, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin) throws GenericEntityException, Exception {
        Map input;
        List<GenericValue> tobeStored = new LinkedList();
        
        Debug.logInfo("Now processing  data [" + data.get("productId") + "]", module);
        String message = "Success";
        String productId = data.getString("productId");
        TransactionUtil.begin();
        GenericValue variantProduct = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
        
        BigDecimal defaultPrice = data.getBigDecimal("defaultPrice");
        BigDecimal listPrice = data.getBigDecimal("listPrice");
     
        
		if(variantProduct!=null){
			if(data.get("smallImageUrl") != null)
				variantProduct.set("smallImageUrl", data.get("smallImageUrl"));
	        if(data.get("largeImageUrl") != null)
	        	variantProduct.set("largeImageUrl", data.get("largeImageUrl"));
	        if(data.get("detailImageUrl") != null)
	        	variantProduct.set("detailImageUrl", data.get("detailImageUrl"));
	        if(data.get("longDescription") != null)
	        	variantProduct.set("longDescription", data.get("longDescription"));
	        if(data.get("description") != null)
	        	variantProduct.set("description", data.get("description"));
	        if(data.get("internalName") != null)
	        	variantProduct.set("internalName", data.get("internalName"));
	        if(data.get("productName") != null)
	        	variantProduct.set("productName", data.get("productName"));
	        if(data.get("brandName") != null)
	        	variantProduct.set("brandName", data.get("brandName"));
	        variantProduct.put("inventoryAtp", new BigDecimal(data.getString("virtualStock"))) ; 
	        variantProduct.put("isExternal", data.get("isExternal")) ;   
            tobeStored.add(variantProduct);
 		}else {
			message = "Variant product not found in Product Table";
		}
        
        
        if(defaultPrice != null){
        	EntityConditionList conditions = EntityCondition.makeCondition(UtilMisc.toList(
        			EntityCondition.makeCondition("productId", EntityOperator.EQUALS, data.get("productId")),
        			EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "DEFAULT_PRICE")   
                    ), EntityOperator.AND);
	        List productsDefaultPrice = delegator.findList("ProductPrice", conditions, null, null, null, false);
	        if(productsDefaultPrice != null && productsDefaultPrice.size() > 0){
	        	Iterator itr = productsDefaultPrice.iterator();
	            while (itr.hasNext()) {
	                GenericValue dprice = (GenericValue)itr.next();
	                Map resp; 
	                try {
	                	String fromDate = dprice.getString("fromDate");
	                	Timestamp fDate = (Timestamp)ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null);
			        	
	                	Map newMap = UtilMisc.toMap("userLogin", userLogin, "productId", dprice.getString("productId"), "productPriceTypeId", dprice.getString("productPriceTypeId"));
                        newMap.put("productPricePurposeId", dprice.getString("productPricePurposeId"));
                        newMap.put("currencyUomId", dprice.getString("currencyUomId"));
                        newMap.put("fromDate", fDate);
                        newMap.put("price", defaultPrice);
                        newMap.put("productStoreGroupId", "_NA_");
                        resp = dispatcher.runSync("updateProductPrice", newMap);
			            
			        } catch (GenericServiceException e) {
			        	TransactionUtil.rollback();
			        	message = "Error while updating default price";
			            return ServiceUtil.returnError(e.getMessage());
			        }
			        if (ServiceUtil.isError(resp)) {
			            return ServiceUtil.returnError("Error not updating default price: ", null, null, resp);
			        }
	            }    
	        }else{
	        	
	        	input = new HashMap();
	            input.put("productId", data.get("productId"));
	            input.put("productPriceTypeId", "DEFAULT_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            input.put("currencyUomId", baseCurrencyUomId);
	            input.put("fromDate", UtilDateTime.nowTimestamp());
	            input.put("price", defaultPrice);
	            	GenericValue productDefaultPrice = delegator.makeValue("ProductPrice", input);
	            	  tobeStored.add(productDefaultPrice);
 	        }     
        }
        if(listPrice != null){
        	EntityConditionList conditions = EntityCondition.makeCondition( UtilMisc.toList(
        			EntityCondition.makeCondition("productId", EntityOperator.EQUALS, data.get("productId")),
        			EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS, "LIST_PRICE")   
                    ), EntityOperator.AND);
	        List productsDefaultPrice = delegator.findList("ProductPrice", conditions, null, null, null, false);
	        if(productsDefaultPrice != null && productsDefaultPrice.size() > 0){
	        	
	        	Iterator itr = productsDefaultPrice.iterator();
	            while (itr.hasNext()) {
	                GenericValue dprice = (GenericValue)itr.next();
	                Map resp; 
	                try {
	                	String fromDate = dprice.getString("fromDate");
	                	Timestamp fDate = (Timestamp)ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null);
			        	
	                	Map newMap = UtilMisc.toMap("userLogin", userLogin, "productId", dprice.getString("productId"), "productPriceTypeId", dprice.getString("productPriceTypeId"));
                        newMap.put("productPricePurposeId", dprice.getString("productPricePurposeId"));
                        newMap.put("currencyUomId", dprice.getString("currencyUomId"));
                        newMap.put("fromDate", fDate);
                        newMap.put("price", listPrice);
                        newMap.put("productStoreGroupId", "_NA_");
                        resp = dispatcher.runSync("updateProductPrice", newMap);
			            
			        } catch (GenericServiceException e) {
			        	TransactionUtil.rollback();
			        	message = "Error while updating List price";
			            return ServiceUtil.returnError(e.getMessage());
			        }
			        if (ServiceUtil.isError(resp)) {
			            return ServiceUtil.returnError("Error not updating List price: ", null, null, resp);
			        }
	            }    
	        }else{
	        	input = new HashMap();
	            input.put("productId", data.get("productId"));
	            input.put("productPriceTypeId", "LIST_PRICE");
	            input.put("productPricePurposeId", "PURCHASE");
	            input.put("productStoreGroupId", "_NA_");
	            input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
	            input.put("currencyUomId", baseCurrencyUomId);
	            input.put("fromDate", UtilDateTime.nowTimestamp());
	            input.put("price", listPrice);
	            GenericValue productDefaultPrice = delegator.makeValue("ProductPrice", input);
          	  tobeStored.add(productDefaultPrice);
	        }    
        }
        
        if(!UtilValidate.isEmpty(data.getString("barcode"))){
        	String[] barCodeArray = data.getString("barcode").split("~~");
        	for(String bCode : barCodeArray){
	        	input = new HashMap();
	        input.put("barcode", bCode.trim());
	        input.put("productId", data.get("productId"));
	        	GenericValue productBarcode = delegator.makeValue("ProductBarcode", input);
	        	tobeStored.add(productBarcode);
        	}	
        }
        
        
        delegator.storeAll(tobeStored);
        
        createInventory(data,now,delegator,userLogin,dispatcher);
        try	{
             data.set("message", message);
            delegator.store(data);
        }catch(GenericEntityException e){
        	Debug.logInfo("Exception while updating Data Import Variant Product entity : "+e.getMessage() + e, module);
    	}
        
        TransactionUtil.commit();

        Map results = ServiceUtil.returnSuccess();
      
        return results;
    }
    
    private static String createInventory(GenericValue data, Timestamp now, Delegator delegator, GenericValue userLogin,LocalDispatcher dispatcher ) throws GenericEntityException, Exception {
    	   
       	Map input;
           	 String message = "success";
           	String productStoreId=null;
           Debug.logInfo("Now processing  data [" + data.get("productId") + "] description [" + data.get("description") + "]", module);
//           productStoreId = ProductStoreWorker.getProductStoreId(request);
         if(UtilValidate.isEmpty(productStoreId))
        	 productStoreId="9000";
           String facilityId = ProductStoreWorker.determineSingleFacilityForStore(delegator, productStoreId);
           List<GenericValue> quantityData=delegator.findList("InventoryItem", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,data.get("productId")), null, null, null, false);
            GenericValue quantity = null;
           if(quantityData.size() > 0){
        	     quantity=EntityUtil.getFirst(quantityData);
           }
           
           Map callMap = new HashMap();
           double qtyRejected=0.0;
          String inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
          callMap.put("productId", data.get("productId"));
           	callMap.put("quantityRejected", qtyRejected);
//           	callMap.put("virtualDiff", data.getString("virtualStock"));
        	callMap.put("unitCost", data.getDouble("unitPrice"));
        	callMap.put("virtualAccepted", data.getString("virtualStock"));
           callMap.put("quantityAccepted", data.getString("qoh"));
        	callMap.put("inventoryItemTypeId", inventoryItemTypeId);
        	callMap.put("userLogin", userLogin);
        	callMap.put("facilityId", facilityId);
        	callMap.put("datetimeReceived", now);
        	callMap.put("vatPerc", data.getDouble("vatPercent"));
        	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
        	callMap.put("currencyUomId", currencyUomId);
        	  try{
    	    		Map inventoryItemId = dispatcher.runSync("receiveInventoryProduct", callMap);
    	    		Debug.logInfo("=========inventoryItemId====================="+inventoryItemId, module);
    	    		//System.out.println("\n\n\n inventoryItemId"+inventoryItemId);
    	    	}catch(GenericServiceException e){
    	    		Debug.logError("Exception during updating inventory " + e.getMessage(), module);
    	    	
    	    	}
        	
        	
                
       return "success";
       }
    
  private static String createInventoryProduct(GenericValue data, Timestamp now, Delegator delegator, GenericValue userLogin,LocalDispatcher dispatcher,HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, Exception {
   
   	Map input;
       	 String message = "success";
       	String productStoreId=null;
       Debug.logInfo("Now processing  data [" + data.get("productId") + "] description [" + data.get("description") + "]", module);
       productStoreId = ProductStoreWorker.getProductStoreId(request);
     if(UtilValidate.isEmpty(productStoreId))
    	 productStoreId="9000";
       String facilityId = ProductStoreWorker.determineSingleFacilityForStore(delegator, productStoreId);
      
       List<GenericValue> quantityData=delegator.findList("InventoryItem", EntityCondition.makeCondition("productId",EntityOperator.EQUALS,data.get("productId")), null, null, null, false);
     GenericValue quantity=EntityUtil.getFirst(quantityData);
       Map callMap =new HashMap();
       double qtyRejected=0.0;
      String inventoryItemTypeId = "NON_SERIAL_INV_ITEM";
      callMap.put("productId", data.get("productId"));
       	callMap.put("quantityRejected", qtyRejected);
        	callMap.put("virtualAccepted", data.getDouble("virtualStock"));
       	


       	
    	callMap.put("unitCost", data.getDouble("unitCost"));
    	
       	if(!UtilValidate.isEmpty(quantity) && !UtilValidate.isEmpty(quantity.getString("quantityOnHandTotal")))
       		callMap.put("quantityAccepted", quantity.getDouble("quantityOnHandTotal"));
       	else
       		callMap.put("quantityAccepted", 0.0);
    	callMap.put("inventoryItemTypeId", inventoryItemTypeId);
    	callMap.put("userLogin", userLogin);
    	callMap.put("facilityId", facilityId);
    	
    	callMap.put("datetimeReceived", now);
    	
    	String currencyUomId = UtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "INR");
    	callMap.put("currencyUomId", currencyUomId);
    	
//    	//System.out.println("\n\n\n\n\n callMap"+callMap);
    	
    	 
    	  try{
	    		Map inventoryItemId = dispatcher.runSync("receiveInventoryProduct", callMap);
	    		Debug.logInfo("=========inventoryItemId====================="+inventoryItemId, module);
	    	}catch(GenericServiceException e){
	    		Debug.logError("Exception during updating inventory " + e.getMessage(), module);
	    	
	    	}
    	
    	
            
   return "success";
   }
}
