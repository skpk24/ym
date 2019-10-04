import java.math.BigDecimal;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.store.ProductStoreWorker;

import java.sql.Timestamp;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;

productStore = ProductStoreWorker.getProductStore(request);
	String slotType = request.getParameter("slotType");
	String filterFromDate = request.getParameter("minDate");
	context.slotType = slotType;
	Date date = new Date();
	Integer intDay = new Integer(date.getDate());
	Integer intMonth = new Integer(date.getMonth()+1);
	Integer intYear = new Integer(date.getYear()+1900);
	String [] slotbookings = request.getParameterValues("slotbooking");
	String  day = intDay.toString();
	String  month = intMonth.toString();
	String  year = intYear.toString();
	Timestamp filterThruDate=UtilDateTime.getDayEnd(ObjectType.simpleTypeConvert(filterFromDate, "Timestamp", null, null));
	if(filterFromDate == null){
			filterFromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			}
	if(filterFromDate != null && filterFromDate.length()<19){
			filterFromDate = year + "-" + month + "-" + day + " " + "00:00:00";
	}
			
	BigDecimal qty = new BigDecimal(0);
	int adds = 1;
	Map pId =  new HashMap();
	try {
		if(slotbookings != null){
		for (String bookng : slotbookings)
		{
			if(bookng.equals('today'))
			{
				Timestamp thruDate=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate ));
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate ));
				if(slotType.equals("All Slot"))
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.IN, ["SLOT1", "SLOT2", "SLOT3", "SLOT4"]));
				else
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS, slotType));
				List orderBy = UtilMisc.toList("-deliveryDate");
				List <GenericValue>orderSlotOrderList = null;
				orderSlotOrderList = delegator.findList("OrderSlot", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), UtilMisc.toSet("orderId"), orderBy, null, false);
				Iterator  <GenericValue>orderSlotIt = orderSlotOrderList.iterator();
				while(orderSlotIt.hasNext())
				{
					GenericValue orderItemss = orderSlotIt.next();
					ecl = EntityCondition.makeCondition([
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemss.get("orderId")),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"),
						EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
					EntityOperator.AND);
				List <GenericValue>orderItemsList = delegator.findList("OrderHeaderAndItems", ecl, UtilMisc.toSet("quantity","productId"), null, null, false);
				Iterator  <GenericValue>orderItemIt = orderItemsList.iterator();
				while(orderItemIt.hasNext())
				{
				 GenericValue orderItem = orderItemIt.next();
				 if(orderItem.get("productId")!=null)
				 {
					 if(pId.containsKey(orderItem.get("productId"))){
						 qty = pId.get(orderItem.get("productId"));
						 pId.put(orderItem.get("productId"),qty.add(orderItem.get("quantity")));
						 }
					 else{
						 pId.put(orderItem.get("productId"),orderItem.get("quantity"));
						 }
				 }
				}
				}
			}else if (bookng.equals('tomorrow'))
			{
				Timestamp thruDate=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				fromDate = UtilDateTime.addDaysToTimestamp(fromDate, adds);
				thruDate  = UtilDateTime.addDaysToTimestamp(thruDate, adds);
				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate ));
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate ));
				if(slotType.equals("All Slot"))
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.IN, ["SLOT1", "SLOT2", "SLOT3", "SLOT4"]));
				else
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS, slotType));
				List orderBy = UtilMisc.toList("-deliveryDate");
				List <GenericValue>orderSlotOrderList = null;
				orderSlotOrderList = delegator.findList("OrderSlot", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), UtilMisc.toSet("orderId"), orderBy, null, false);
				Iterator  <GenericValue>orderSlotIt = orderSlotOrderList.iterator();
				while(orderSlotIt.hasNext())
				{
					GenericValue orderItemss = orderSlotIt.next();
					ecl = EntityCondition.makeCondition([
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemss.get("orderId")),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"),
						EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
					EntityOperator.AND);
				List <GenericValue>orderItemsList = delegator.findList("OrderHeaderAndItems", ecl, UtilMisc.toSet("quantity","productId"), null, null, false);
				Iterator  <GenericValue>orderItemIt = orderItemsList.iterator();
				while(orderItemIt.hasNext())
				{
				 GenericValue orderItem = orderItemIt.next();
				 if(orderItem.get("productId")!=null)
				 {
					 if(pId.containsKey(orderItem.get("productId"))){
						 qty = pId.get(orderItem.get("productId"));
						 pId.put(orderItem.get("productId"),qty.add(orderItem.get("quantity")));
						 }
					 else{
						 pId.put(orderItem.get("productId"),orderItem.get("quantity"));
						 }
				 }
				}
				}
			}else if(bookng.equals('advanced'))
			{
				/*Timestamp thruDate=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				fromDate = UtilDateTime.addDaysToTimestamp(fromDate, adds);
				thruDate  = UtilDateTime.addDaysToTimestamp(thruDate, adds);*/
				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(filterFromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO, filterThruDate ));
				if(slotType.equals("All Slot"))
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.IN, ["SLOT1", "SLOT2", "SLOT3", "SLOT4"]));
				else
				dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS, slotType));
				List orderBy = UtilMisc.toList("-deliveryDate");
				List <GenericValue>orderSlotOrderList = null;
				orderSlotOrderList = delegator.findList("OrderSlot", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), UtilMisc.toSet("orderId"), orderBy, null, false);
				Iterator  <GenericValue>orderSlotIt = orderSlotOrderList.iterator();
				while(orderSlotIt.hasNext())
				{
					GenericValue orderItemss = orderSlotIt.next();
					ecl = EntityCondition.makeCondition([
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemss.get("orderId")),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"),
						EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
					EntityOperator.AND);
				List <GenericValue>orderItemsList = delegator.findList("OrderHeaderAndItems", ecl, UtilMisc.toSet("quantity","productId"), null, null, false);
				Iterator  <GenericValue>orderItemIt = orderItemsList.iterator();
				while(orderItemIt.hasNext())
				{
				 GenericValue orderItem = orderItemIt.next();
				 if(orderItem.get("productId")!=null)
				 {
					 if(pId.containsKey(orderItem.get("productId"))){
						 qty = pId.get(orderItem.get("productId"));
						 pId.put(orderItem.get("productId"),qty.add(orderItem.get("quantity")));
						 }
					 else{
						 pId.put(orderItem.get("productId"),orderItem.get("quantity"));
						 }
				 }
				}
				}
			}
		}
		}else {
			Timestamp thruDate=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
			Timestamp fromDate=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			List dateCondiList = new ArrayList();
			dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			/*dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));*/
			if(slotType.equals("All Slot"))
			dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.IN, ["SLOT1", "SLOT2", "SLOT3", "SLOT4"]));
			else
			dateCondiList.add(EntityCondition.makeCondition("slotType", EntityOperator.EQUALS, slotType));
			List orderBy = UtilMisc.toList("-deliveryDate");
			List <GenericValue>orderOrderList = null;
			orderOrderList = delegator.findList("OrderSlot", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), UtilMisc.toSet("orderId"), orderBy, null, false);
			Iterator  <GenericValue>orderOrderIt = orderOrderList.iterator();
			while(orderOrderIt.hasNext())
			{
				GenericValue orderItemss = orderOrderIt.next();
				ecl = EntityCondition.makeCondition([
					EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderItemss.get("orderId")),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"),
					EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER")],
				EntityOperator.AND);
			List <GenericValue>orderItemsList = delegator.findList("OrderHeaderAndItems", ecl, UtilMisc.toSet("quantity","productId"), null, null, false);
			Iterator  <GenericValue>orderItemIt = orderItemsList.iterator();
			while(orderItemIt.hasNext())
			{
			 GenericValue orderItem = orderItemIt.next();
			 if(orderItem.get("productId")!=null)
			 {
				 if(pId.containsKey(orderItem.get("productId"))){
					 qty = pId.get(orderItem.get("productId"));
					 pId.put(orderItem.get("productId"),qty.add(orderItem.get("quantity")));
					 }
				 else{
					 pId.put(orderItem.get("productId"),orderItem.get("quantity"));
					 }
			 }
			}
			}
			}
			} catch (Exception e) {
			e.printStackTrace();
		}
		context.pId=pId;
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

