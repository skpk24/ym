/*
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
 */
 

import org.ofbiz.base.util.*;
import org.ofbiz.base.util.string.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.dataimport.UploadEvents;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import javolution.util.FastMap;
dataImportFromExcel = delegator.findList("DataImportFromExcel",null,null,null,null,false);
context.DataImportProduct = delegator.findList("DataImportProduct",null,null,null,null,false);
context.dataImportFromExcel = dataImportFromExcel ;
Map input = new HashMap();
input.put("productPricePurposeId", "PURCHASE");
input.put("productStoreGroupId", "_NA_");
input.put("currencyUomId", "INR");

Map productPriceChangeMap  = new HashMap();
productPriceChangeMap.put("productPricePurposeId", "PURCHASE");
productPriceChangeMap.put("productStoreGroupId", "_NA_");
productPriceChangeMap.put("currencyUomId", "INR");
// input.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));


String uploadDataImportSeqId= request.getParameter("uploadDataImportSeqId");
if(UtilValidate.isNotEmpty(uploadDataImportSeqId)){
	GenericValue value  = delegator.findByPrimaryKey("DataImportFromExcel", UtilMisc.toMap("uploadDataImportSeqId",uploadDataImportSeqId));
	if(UtilValidate.isNotEmpty(value)){
		try{
			value.remove();
		}catch(Exception e){
			return "success";
		}
		 return "success";
	}
	 return "success";
}
 
GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
bit= request.getParameter("bit");

context.minutes = request.getAttribute("minutes");
context.totalInSecond = request.getAttribute("totalInSecond");

boolean fullAdmin = false;
if(UtilValidate.isNotEmpty(userLogin)){
/////////////////////////////////////////////
 	   String userLoginId = userLogin.getString("userLoginId");
	   List adminConditions = new ArrayList();
	   String  securityGroupId = null;
	   List conditionList  = new ArrayList();
	   conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
	   conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "FULLADMIN"));
      try {
		dataSourceDetails = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(conditionList, EntityOperator.AND), null, null, null, false);
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
     
     if(UtilValidate.isNotEmpty(dataSourceDetails) && UtilValidate.isNotEmpty(bit) && bit.equals("1") ){
    	 fullAdmin = true;
     }else{
    	 fullAdmin  =  false;    
     }
///////////////////////////////////////////////	
}

 
if(fullAdmin){
List productIds = EntityUtil.getFieldListFromEntityList(dataImportFromExcel, "articleNumber", true);
List<GenericValue> productLists = delegator.findList("Product", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), UtilMisc.toSet("productId","invUnitCost","basePrice","inventoryAtp"), null, null, false);
List<GenericValue> priceList = delegator.findList("ProductPrice", EntityCondition.makeCondition("productId", EntityOperator.IN, productIds), null, UtilMisc.toList("fromDate DESC"), null, false);
final long start = System.nanoTime();
for(GenericValue gv : dataImportFromExcel){
	
	input.put("productId", gv.getString("articleNumber"));
	
	List<GenericValue> prdList = EntityUtil.filterByAnd(productLists,UtilMisc.toMap("productId", gv.getString("articleNumber")));
	GenericValue product = null;
	if(UtilValidate.isNotEmpty(prdList)) product = EntityUtil.getFirst(prdList);
	
	if(UtilValidate.isEmpty(product)){
		request.setAttribute("_EVENT_MESSAGE_", "Article Number "+gv.getString("articleNumber")+" not found");
		return "success";
	}
	try {
		TransactionUtil.begin();
	} catch (GenericTransactionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	String productId = gv.getString("articleNumber");
	BigDecimal virtualStock = gv.getBigDecimal("stock");
	BigDecimal vatPerc = gv.getBigDecimal("vatPerc");
	BigDecimal qoh = gv.getBigDecimal("qoh");
	BigDecimal unitPrice = gv.getBigDecimal("sellPrice");
	BigDecimal mrpPrice = gv.getBigDecimal("mrpPrice");

Map conditions = UtilMisc.toMap("productId",productId,"facilityId","WebStoreWarehouse");
try {
	inventoryAvailable = dispatcher.runSync("getInventoryAvailableByFacility", conditions);
 } catch (GenericServiceException e) {
	Debug.logError(e, "Problems getting inventory available by facility id "+inventoryFacilityId+" for product ", module);
} 

 
BigDecimal QOHT = inventoryAvailable.get("quantityOnHandTotal");
List condition = UtilMisc.toList(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId), EntityCondition.makeCondition("availableToPromiseTotal",EntityOperator.GREATER_THAN,BigDecimal.ZERO));
userLogin = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId","admin"));
List<GenericValue> inventoryItems = null;
try {
	inventoryItems = delegator.findList("InventoryItem",EntityCondition.makeCondition(condition,EntityOperator.AND),null,UtilMisc.toList("datetimeReceived DESC"),null,false);
} catch (GenericEntityException e) {
	request.setAttribute("_EVENT_MESSAGE_", "Error in calculating the inventory for the product  "+gv.getString("articleNumber"));
	return "success";
}

 
BigDecimal AUTPT =  BigDecimal.ZERO;
AUTPT = AUTPT.add(qoh).add(virtualStock).add(QOHT);

List tobeStored = new LinkedList();
if(UtilValidate.isNotEmpty(inventoryItems)){
GenericValue inventoryItem = EntityUtil.getFirst(inventoryItems); //get the first record for available to promise total
BigDecimal availableToPromiseTotal = inventoryItem.getBigDecimal("availableToPromiseTotal");

GenericValue physicalInventory = delegator.makeValue("PhysicalInventory");
String physicalInventoryId = delegator.getNextSeqId("PhysicalInventory");
physicalInventory.put("physicalInventoryId", physicalInventoryId);
physicalInventory.put("partyId", "admin");
physicalInventory.put("physicalInventoryDate", UtilDateTime.nowTimestamp());
tobeStored.add(physicalInventory);

GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
inventoryItemDetail.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));
delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);
inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
inventoryItemDetail.put("quantityOnHandDiff", BigDecimal.ZERO);
inventoryItemDetail.put("availableToPromiseDiff",availableToPromiseTotal.negate());
inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
inventoryItemDetail.put("physicalInventoryId",physicalInventoryId);
inventoryItemDetail.put("reasonEnumId","VAR_INV_ADJUST");
inventoryItemDetail.put("description","Old ATP adjusting For product");
inventoryItemDetail.put("requiresEca","N"); 
tobeStored.add(inventoryItemDetail);


GenericValue inventoryItemVariance = delegator.makeValue("InventoryItemVariance");  
inventoryItemVariance.put("inventoryItemId",  inventoryItem.getString("inventoryItemId"));
inventoryItemVariance.put("physicalInventoryId",physicalInventoryId);
inventoryItemVariance.put("varianceReasonId","VAR_INV_ADJUST");
inventoryItemVariance.put("availableToPromiseVar",availableToPromiseTotal.negate());
inventoryItemVariance.put("comments","Old ATP adjusting For product demo3");
tobeStored.add(inventoryItemVariance);
 

	if(qoh.intValue() ==0) {
		 GenericValue    inventoryItemDetailMatch = delegator.makeValue("InventoryItemDetail");
		 inventoryItemDetailMatch.put("inventoryItemId",inventoryItem.getString("inventoryItemId"));
		String inventoryItemDetailSeqId =  inventoryItemDetail.getString("inventoryItemDetailSeqId");
		 inventoryItemDetailMatch.put("inventoryItemDetailSeqId",new BigDecimal(inventoryItemDetailSeqId).add(new BigDecimal("1")).toString());
 		inventoryItemDetailMatch.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
		inventoryItemDetailMatch.put("quantityOnHandDiff",qoh);//from excel sheet
		inventoryItemDetailMatch.put("availableToPromiseDiff",AUTPT);//from excel sheet
		inventoryItemDetailMatch.put("accountingQuantityDiff", BigDecimal.ZERO);
		inventoryItemDetailMatch.put("unitCost",unitPrice);//from excel sheet
		inventoryItemDetailMatch.put("virtualDiff",virtualStock);//from excel sheet
		inventoryItemDetailMatch.put("requiresEca","N");//from excel sheet
		inventoryItemDetailMatch.put("description","New  ATP adjusting For product");
		

		tobeStored.add(inventoryItemDetailMatch);
		inventoryItem.put("availableToPromiseTotal",AUTPT);
	}else{
		inventoryItem.put("availableToPromiseTotal",BigDecimal.ZERO);

	}



inventoryItem.set("comments", "CSV_import");
//inventoryItem.set("requiresEca","N")
tobeStored.add(inventoryItem);


} else{
	if(qoh.intValue() ==0) {
	GenericValue inventoryItem = delegator.makeValue("InventoryItem");
	String inventoryItemId = delegator.getNextSeqId("InventoryItem");
	inventoryItem.set("inventoryItemId",inventoryItemId);
	inventoryItem.set("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
	inventoryItem.set("productId", productId);
	inventoryItem.set("ownerPartyId", "Company");
	inventoryItem.set("datetimeReceived", UtilDateTime.nowTimestamp());
	inventoryItem.set("facilityId", "WebStoreWarehouse");
	inventoryItem.set("comments", "CSV_import");
	inventoryItem.set("quantityOnHandTotal",qoh);//from excel sheeet
	
	//print "\n AUTPT  ProductId " + AUTPT + " " +  productId + "  " + qoh + " " + inventoryAvailable
	inventoryItem.set("availableToPromiseTotal",AUTPT);//from excel shet
	inventoryItem.set("accountingQuantityTotal",qoh);//from excel sheeet
	inventoryItem.set("unitCost",unitPrice);//from excel sheeet
	inventoryItem.set("currencyUomId", "INR"); 
	inventoryItem.set("stock",qoh);//from excel sheeet
	inventoryItem.set("virtualTotal",virtualStock );//from excel sheeet
	inventoryItem.set("vatPercentage",vatPerc);//from excel sheeet
	//inventoryItem.set("requiresEca","N");//from excel sheeet
	tobeStored.add(inventoryItem);
	 
	/////////////////////  For Receiving the Shipment Recipt /////////////////// 
	GenericValue shipmentRecipt = delegator.makeValue("ShipmentReceipt");
	String receiptId = delegator.getNextSeqId("ShipmentReceipt");
	shipmentRecipt.put("receiptId",receiptId);
	shipmentRecipt.put("inventoryItemId",inventoryItemId);
	shipmentRecipt.put("productId",productId);
	shipmentRecipt.put("datetimeReceived",UtilDateTime.nowTimestamp());
	shipmentRecipt.put("quantityAccepted",qoh);
	shipmentRecipt.put("quantityRejected",BigDecimal.ZERO);
	shipmentRecipt.put("receivedByUserLoginId","admin");
	
	tobeStored.add(shipmentRecipt);
	/////////////////////////  End  Receiving the Shipment Recipt /////////////////// 
	
	GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
	inventoryItemDetail.put("inventoryItemId",inventoryItemId);
	delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);
	inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
	inventoryItemDetail.put("quantityOnHandDiff",qoh);//from excel sheet
	inventoryItemDetail.put("availableToPromiseDiff",AUTPT);//from excel sheet
	inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
	inventoryItemDetail.put("unitCost",unitPrice);//from excel sheet
	inventoryItemDetail.put("receiptId",receiptId);//need to workout
	inventoryItemDetail.put("virtualDiff",virtualStock);//from excel sheet
	inventoryItemDetail.put("requiresEca","N");//from excel sheet
	tobeStored.add(inventoryItemDetail);
	}
}

if(qoh.intValue() !=0) {
	
	
GenericValue inventoryItem = delegator.makeValue("InventoryItem");
String inventoryItemId = delegator.getNextSeqId("InventoryItem");
inventoryItem.set("inventoryItemId",inventoryItemId);
inventoryItem.set("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
inventoryItem.set("productId", productId);
inventoryItem.set("ownerPartyId", "Company");
inventoryItem.set("datetimeReceived", UtilDateTime.nowTimestamp());
inventoryItem.set("facilityId", "WebStoreWarehouse");
inventoryItem.set("comments", "CSV_import");
inventoryItem.set("quantityOnHandTotal",qoh);//from excel sheeet


//print "\n AUTPT  ProductId " + AUTPT + " " +  productId + "  " + qoh + " " + inventoryAvailable
inventoryItem.set("availableToPromiseTotal",AUTPT);//from excel shet
inventoryItem.set("accountingQuantityTotal",qoh);//from excel sheeet
inventoryItem.set("unitCost",unitPrice);//from excel sheeet
inventoryItem.set("currencyUomId", "INR"); 
inventoryItem.set("stock",qoh);//from excel sheeet
inventoryItem.set("virtualTotal",virtualStock );//from excel sheeet
inventoryItem.set("vatPercentage",vatPerc);//from excel sheeet
//inventoryItem.set("requiresEca","N");//from excel sheeet

tobeStored.add(inventoryItem);
 
/////////////////////  For Receiving the Shipment Recipt /////////////////// 
GenericValue shipmentRecipt = delegator.makeValue("ShipmentReceipt");
String receiptId = delegator.getNextSeqId("ShipmentReceipt");
shipmentRecipt.put("receiptId",receiptId);
shipmentRecipt.put("inventoryItemId",inventoryItemId);
shipmentRecipt.put("productId",productId);
shipmentRecipt.put("datetimeReceived",UtilDateTime.nowTimestamp());
shipmentRecipt.put("quantityAccepted",qoh);
shipmentRecipt.put("quantityRejected",BigDecimal.ZERO);
shipmentRecipt.put("receivedByUserLoginId","admin");

tobeStored.add(shipmentRecipt);
/////////////////////////  End  Receiving the Shipment Recipt /////////////////// 

GenericValue inventoryItemDetail = delegator.makeValue("InventoryItemDetail");
inventoryItemDetail.put("inventoryItemId",inventoryItemId);
delegator.setNextSubSeqId(inventoryItemDetail, "inventoryItemDetailSeqId", 5, 1);

inventoryItemDetail.put("effectiveDate",UtilDateTime.nowTimestamp());//from excel sheet
inventoryItemDetail.put("quantityOnHandDiff",qoh);//from excel sheet
inventoryItemDetail.put("availableToPromiseDiff",AUTPT);//from excel sheet
inventoryItemDetail.put("accountingQuantityDiff", BigDecimal.ZERO);
inventoryItemDetail.put("unitCost",unitPrice);//from excel sheet
inventoryItemDetail.put("receiptId",receiptId);//need to workout
inventoryItemDetail.put("virtualDiff",virtualStock);//from excel sheet
inventoryItemDetail.put("requiresEca","N");//from excel sheet
tobeStored.add(inventoryItemDetail);
 

 BigDecimal subseqId = new BigDecimal(inventoryItemDetail.getString("inventoryItemDetailSeqId"));
 GenericValue inventoryItemDetails = delegator.makeValue("InventoryItemDetail");
 inventoryItemDetails.put("inventoryItemId",inventoryItemId);
 inventoryItemDetails.put("inventoryItemDetailSeqId",subseqId.add(new BigDecimal("1")).toString());
 delegator.setNextSubSeqId(inventoryItemDetails, "inventoryItemDetailSeqId", 5, 1);
 inventoryItemDetails.put("effectiveDate",UtilDateTime.nowTimestamp()); 
 inventoryItemDetails.put("quantityOnHandDiff",BigDecimal.ZERO ); 
 inventoryItemDetails.put("availableToPromiseDiff",BigDecimal.ZERO);
 inventoryItemDetails.put("accountingQuantityDiff",qoh);//from excel sheet
 inventoryItemDetails.put("requiresEca","N");//from excel sheet
 tobeStored.add(inventoryItemDetails);
}

 //////////product table Update///////////////
 product.put("invUnitCost", unitPrice);   
 product.put("inventoryAtp", AUTPT);
 //////////////////////////////////////////////
 
 //////////////productPriceUpdate///////////////////
 input.put("productId", productId);
 input.put("productPriceTypeId", "DEFAULT_PRICE");
 
 List<GenericValue> prdlistPriceList = EntityUtil.filterByAnd(priceList, input);
 List<GenericValue>  newValue  = EntityUtil.filterByDate(prdlistPriceList);
  if(UtilValidate.isEmpty(newValue)){
	  if(UtilValidate.isNotEmpty(mrpPrice) && mrpPrice.intValue() != 0 ){
	  product.put("basePrice",mrpPrice);
//	 print("\n\n adding"+newValue);
 	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
	productListPrice.put("price", mrpPrice);
	productListPrice.put("fromDate", UtilDateTime.nowTimestamp());
 	GenericValue productPriceChangeDefault = delegator.makeValue("ProductPriceChange", input);
 	String productPriceChangeId = delegator.getNextSeqId("ProductPriceChange");
 	productPriceChangeDefault.put("productPriceChangeId", productPriceChangeId);
 	productPriceChangeDefault.put("price", mrpPrice);
 	productPriceChangeDefault.put("fromDate", UtilDateTime.nowTimestamp());
 	productPriceChangeDefault.put("changedDate", UtilDateTime.nowTimestamp());
 	productPriceChangeDefault.put("changedByUserLogin",userLogin.getString("userLoginId"));

	tobeStored.add(productListPrice);
 	tobeStored.add(productPriceChangeDefault);
	  }
	}else{
		  if(UtilValidate.isNotEmpty(mrpPrice) && mrpPrice.intValue() != 0 ){
		 product.put("basePrice",mrpPrice);
   GenericValue productListPrice = newValue.get(0);

  
  GenericValue productPriceChangeDefault = delegator.makeValue("ProductPriceChange", productPriceChangeMap);
  String productPriceChangeId = delegator.getNextSeqId("ProductPriceChange");
	productPriceChangeDefault.put("productPriceChangeId", productPriceChangeId);
	productPriceChangeDefault.put("price", mrpPrice);
	productPriceChangeDefault.put("productId", productId);
  	productPriceChangeDefault.put("oldPrice", productListPrice.get("price"));
	productPriceChangeDefault.put("productPriceTypeId", "DEFAULT_PRICE");
	productPriceChangeDefault.put("fromDate", UtilDateTime.nowTimestamp());
	  productListPrice.put("price", mrpPrice);
		productPriceChangeDefault.put("changedDate", UtilDateTime.nowTimestamp());
	 	productPriceChangeDefault.put("changedByUserLogin",userLogin.getString("userLoginId"));
	  tobeStored.add(productListPrice);
	  tobeStored.add(productPriceChangeDefault);
  
		  }
	}
  
  input.put("productPriceTypeId", "LIST_PRICE");
    prdlistPriceList = EntityUtil.filterByAnd(priceList, input);
     newValue  = EntityUtil.filterByDate(prdlistPriceList);
   if(UtilValidate.isEmpty(newValue)){
		  if(UtilValidate.isNotEmpty(mrpPrice) && mrpPrice.intValue() != 0 ){
	   product.put("basePrice",mrpPrice);
   	GenericValue productListPrice = delegator.makeValue("ProductPrice", input);
 	productListPrice.put("price", mrpPrice);
 	productListPrice.put("fromDate", UtilDateTime.nowTimestamp());
 	tobeStored.add(productListPrice);
 	GenericValue productPriceChangeDefault = delegator.makeValue("ProductPriceChange", input);
 	String productPriceChangeId = delegator.getNextSeqId("ProductPriceChange");
 	productPriceChangeDefault.put("productPriceChangeId", productPriceChangeId);
 	productPriceChangeDefault.put("price", mrpPrice);
 	productPriceChangeDefault.put("fromDate", UtilDateTime.nowTimestamp());
	productPriceChangeDefault.put("changedDate", UtilDateTime.nowTimestamp());
 	productPriceChangeDefault.put("changedByUserLogin",userLogin.getString("userLoginId"));
 	tobeStored.add(productPriceChangeDefault);
		  }
 	}else{
		  if(UtilValidate.isNotEmpty(mrpPrice) && mrpPrice.intValue() != 0 ){
 		 product.put("basePrice",mrpPrice);
    GenericValue productListPrice = newValue.get(0);
   
   
   GenericValue productPriceChangeDefault = delegator.makeValue("ProductPriceChange", productPriceChangeMap);
   String productPriceChangeId = delegator.getNextSeqId("ProductPriceChange");
	productPriceChangeDefault.put("productPriceChangeId", productPriceChangeId);
  	productPriceChangeDefault.put("price", mrpPrice);
  	productPriceChangeDefault.put("oldPrice", productListPrice.get("price"));
  	productPriceChangeDefault.put("productId", productId);
  	productPriceChangeDefault.put("productPriceTypeId", "LIST_PRICE");
  	productPriceChangeDefault.put("fromDate", UtilDateTime.nowTimestamp());
	productPriceChangeDefault.put("changedDate", UtilDateTime.nowTimestamp());
 	productPriceChangeDefault.put("changedByUserLogin",userLogin.getString("userLoginId"));
  	productListPrice.put("price", mrpPrice);
    tobeStored.add(productListPrice);
  	  tobeStored.add(productPriceChangeDefault);
		  }
 	} 
  
 
//////////////////////////////////////////// 
tobeStored.add(product);

///////////////////////////virtal product update
List condList =	new ArrayList();
condList.add(EntityCondition.makeCondition("productIdTo",EntityOperator.EQUALS,productId));
condList.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"PRODUCT_VARIANT"));
productAssocList  = delegator.findList("ProductAssoc",EntityCondition.makeCondition(condList,EntityOperator.AND),null, UtilMisc.toList("productId"), null,false);
virtualProductIds = EntityUtil.getFieldListFromEntityList(productAssocList,"productId",true);
condList.clear();
condList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
condList.add(EntityCondition.makeCondition("productFeatureApplTypeId",EntityOperator.EQUALS,"STANDARD_FEATURE"));
productAssocFeatureList  = delegator.findList("ProductFeatureAppl",EntityCondition.makeCondition(condList,EntityOperator.AND),null, UtilMisc.toList("productId"), null,false);
condList.clear();
condList.add(EntityCondition.makeCondition("productId",EntityOperator.IN,virtualProductIds));
condList.add(EntityCondition.makeCondition("productFeatureApplTypeId",EntityOperator.EQUALS,"SELECTABLE_FEATURE"));
productAssocFeatureVirtualList  = delegator.findList("ProductFeatureAppl",EntityCondition.makeCondition(condList,EntityOperator.AND),null,UtilMisc.toList("sequenceNum"), null,false);
int cnt = 0; 
if(UtilValidate.isNotEmpty(productAssocFeatureList) && productAssocFeatureList.size() > 0){
	GenericValue  temp =  EntityUtil.getFirst(productAssocFeatureList);
	String productFeatureId = temp.getString("productFeatureId");
	for(GenericValue pAFVL  : productAssocFeatureVirtualList){ 
		if(pAFVL.getString("productFeatureId").equalsIgnoreCase(productFeatureId)){
				break;
		}
		cnt++;
	}
}	
String vPID =  null;
GenericValue value = null;
if(cnt == 0){
	if(virtualProductIds.size()>0){
		vPID = virtualProductIds.get(0);
		 value  = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",virtualProductIds.get(0)));
		value.set("basePrice",mrpPrice);
//		tobeStored.add(value);
		input.put("productId", virtualProductIds.get(0));
		input.remove("fromDate");
		input.remove("productPriceTypeId");
		List virtualPriceValue = delegator.findByAnd("ProductPrice",input);
		 input.put("productPriceTypeId", "DEFAULT_PRICE");
		 virtualPriceWithDefault = EntityUtil.filterByAnd(virtualPriceValue, input);
 		 if(UtilValidate.isNotEmpty(virtualPriceWithDefault) && virtualPriceWithDefault.size()>0){
			 GenericValue vDefault = virtualPriceWithDefault.get(0);
			 vDefault.set("price", mrpPrice);
				tobeStored.add(vDefault);
		 }else{
			 GenericValue vDefault = delegator.makeValue("ProductPrice", input);
			 vDefault.put("price", mrpPrice);
			 vDefault.put("fromDate", UtilDateTime.nowTimestamp());
			 tobeStored.add(vDefault);
		 }
		 input.remove("fromDate");
		 input.put("productPriceTypeId", "LIST_PRICE");
		 virtualPriceWithDefault = EntityUtil.filterByAnd(virtualPriceValue, input);
		 if(UtilValidate.isNotEmpty(virtualPriceWithDefault) && virtualPriceWithDefault.size()>0){
			 GenericValue vDefault = virtualPriceWithDefault.get(0);
			 vDefault.set("price", mrpPrice);
				tobeStored.add(vDefault);
		 }else{
			 GenericValue vDefault = delegator.makeValue("ProductPrice", input);
			 vDefault.put("price", mrpPrice);
			 vDefault.put("fromDate", UtilDateTime.nowTimestamp());
			 tobeStored.add(vDefault);
		 }
 	}
}

/////////////////////////////////////////////////////////

boolean errorFlag = true;
try {
  delegator.storeAll(tobeStored);
}
catch (Exception e) {
	errorFlag = false;
	e.printStackTrace();
}

 
	  try {
		  if(errorFlag){
			TransactionUtil.commit();
		  	gv.remove();
		  }
		  else{
			  gv.set("message" , "some error in updating the inventory");
			  gv.store();
			TransactionUtil.rollback();
		  }
	} catch (GenericTransactionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 if(errorFlag){
		  if(UtilValidate.isNotEmpty(vPID) && UtilValidate.isNotEmpty(value) ){
			  Map priceContext =  new HashMap();
			  priceContext.put("product",product)
				priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
				value.set("basePrice",priceResult.get("price"));
				value.put("inventoryAtp", AUTPT);
				product.set("basePrice",priceResult.get("price"));
				value.set("lastModifiedDate", UtilDateTime.nowTimestamp());
				value.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				 product.set("lastModifiedDate", UtilDateTime.nowTimestamp());
				  product.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
				product.store();
				value.store();
		  }else{
			  Map priceContext =  new HashMap();
			  priceContext.put("product",product)
			  priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
			  product.set("basePrice",priceResult.get("price"));
			  product.set("lastModifiedDate", UtilDateTime.nowTimestamp());
			  product.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
			  product.store();
		  }
	 }  
}
final long end = System.nanoTime();
final long totalInSecond = ((end - start) / 1000000) / 1000;
final long minutes = (totalInSecond / 60 );
//System.out.println("Took: " + ((end - start) / 1000000) + "ms \n" + totalInSecond + "Second\n" + minutes + " minutes\n" );
 request.setAttribute("minutes",minutes);
 request.setAttribute("totalInSecond",totalInSecond);
 return "success";
}


/////////////////////////////////////original code endd //////////////////
context.put("importedProduct", "0");
context.put("unimportedProduct", "0");
context.put("importedVariantProduct", "0");
context.put("unimportedVariantProduct", "0");

 