package org.ofbiz.order.report.newReports;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
//import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.report.Report;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.LocalDispatcher;

public class ReportsYouMart {
	
	
	
	  public static final String module = ReportsYouMart.class.getName();

		protected static LocalDispatcher dispatcher = null;
		
		private static GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");

		private static GenericValue GenericValue;
		
		public static String orderSummaryReport(HttpServletRequest request, HttpServletResponse response){
			
			
			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
			
			String entryDate = null;
			List exprs = FastList.newInstance();
			String fromDate = request.getParameter("minDate");
			String thruDate = request.getParameter("maxDate");
			String slotType = request.getParameter("slotType");
			if(UtilValidate.isEmpty(slotType))
				slotType="All";
		Date date = new Date();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp start = null;
			Timestamp end = null;
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}

				if (UtilValidate.isEmpty(fromDate)
						&& UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));
				}
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}
				
				
				
				List<EntityCondition> condn = FastList.newInstance();
				condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO,start));
				condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
				if(!(slotType.equals("All")))
				condn.add(EntityCondition.makeCondition("slot",EntityOperator.EQUALS, slotType));
				
				
				List<GenericValue> orderList = delegator.findList("OrderHeader", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);	
				
				
				
				request.setAttribute("orderList",orderList);
				request.setAttribute("fromDateStr", start);
				request.setAttribute("thruDateStr", end);
				if(UtilValidate.isNotEmpty(slotType))
				request.setAttribute("slotType",slotType);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
		}
	
public static String cashOnDeliveryReport(HttpServletRequest request, HttpServletResponse response){
			
			
			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
			String entryDate = null;
			List exprs = FastList.newInstance();
			String fromDate = request.getParameter("minDate");
			String thruDate = request.getParameter("maxDate");
		
		Date date = new Date();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp start = null;
			Timestamp end = null;
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}

				if (UtilValidate.isEmpty(fromDate)
						&& UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));
				}
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}
				
				
				
				List<EntityCondition> condn = FastList.newInstance();
				condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO,start));
				condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
				condn.add(EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.EQUALS, "EXT_COD"));
				
				
				List<GenericValue> orderList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);	
				
				
				
				request.setAttribute("orderList",orderList);
				request.setAttribute("fromDateStr", start);
				request.setAttribute("thruDateStr", end);
				
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "success";
		}
	
public static String dailySlotReport(HttpServletRequest request, HttpServletResponse response){
	
	
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	String entryDate = null;
	List exprs = FastList.newInstance();
	String fromDate = request.getParameter("minDate");
	String thruDate = request.getParameter("maxDate");
	String partyId = request.getParameter("partyId");
	String grandTotal = request.getParameter("grandTotal");
	String pinCode = request.getParameter("pinCode");
	String orderId = request.getParameter("orderId");
	String upperLimit = request.getParameter("upperLimimt");
	String lowerLimit = request.getParameter("lowerLimit");
	//String locationCode = request.getParameter("locationCode");
	String slotType = request.getParameter("slotType");

Date date = new Date();
try{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Timestamp start = null;
	Timestamp end = null;
		if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
			Date d = (Date) sdf.parse(fromDate);
			start = new Timestamp(d.getTime());
			Date d1 = (Date) sdf.parse(thruDate);
			end = new Timestamp(d1.getTime());
		}

		if (UtilValidate.isEmpty(fromDate)
				&& UtilValidate.isEmpty(thruDate)) {
			start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			end = UtilDateTime.getDayEnd(UtilDateTime
					.getDayStart(UtilDateTime.nowTimestamp()));
		}
		if (UtilValidate.isEmpty(thruDate))
			end = UtilDateTime.getDayEnd(UtilDateTime
					.getDayStart(UtilDateTime.nowTimestamp()));

		if (!UtilValidate.isEmpty(fromDate)) {
			Date d = (Date) sdf.parse(fromDate);
			start = new Timestamp(d.getTime());
		}
		if (!UtilValidate.isEmpty(thruDate)) {
			Date d1 = (Date) sdf.parse(thruDate);
			end = new Timestamp(d1.getTime());
		}
		List<EntityCondition> condn = FastList.newInstance();
		condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO,start));
		condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		if(!(slotType.equals("All")))
			condn.add(EntityCondition.makeCondition("slot",EntityOperator.EQUALS, slotType));
		if(UtilValidate.isNotEmpty(partyId))
		condn.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS, partyId));
		if(UtilValidate.isNotEmpty(orderId))
			condn.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS, orderId));
		if(UtilValidate.isNotEmpty(upperLimit))
			condn.add(EntityCondition.makeCondition("grandTotal",EntityOperator.LESS_THAN_EQUAL_TO, upperLimit));
		if(UtilValidate.isNotEmpty(upperLimit))
			condn.add(EntityCondition.makeCondition("grandTotal",EntityOperator.GREATER_THAN_EQUAL_TO, lowerLimit));
		List<GenericValue> orderList = delegator.findList("OrderHeaderAndPersonAndPartyContactMechPurpose", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);	
		List dailyList=FastList.newInstance();
		int i=0;
		if(UtilValidate.isNotEmpty(orderList))
		{
			
			for(GenericValue order:orderList)
			{
				//Map dailySlot=FastMap.newInstance();
				Map dailySlot=new HashMap();
				
				List<GenericValue> contactMech=delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition("partyId",order.getString("partyIdContact")), null, null, null, false);
				if(UtilValidate.isNotEmpty(contactMech))
				{
					for(GenericValue contact:contactMech)
					{
						String postalMechId=null;
						String phoneMechId=null;
						String address1=null;
						String address2=null;
						String city=null;
						String postalCode=null;
						String contactNumber=null;
						GenericValue postalArress=null;
						GenericValue telephoneGV=null;
						if(contact.getString("contactMechPurposeTypeId").equals("SHIPPING_LOCATION"))
						postalMechId=contact.getString("contactMechId");
						if(contact.getString("contactMechPurposeTypeId").equals("SHIPPING_LOCATION"))
						phoneMechId=contact.getString("PHONE_MOBILE");
						if(UtilValidate.isNotEmpty(postalMechId))
						{	
						postalArress=delegator.findByPrimaryKey("PostalAddress",UtilMisc.toMap("contactMechId",EntityOperator.EQUALS,postalMechId));
						if(UtilValidate.isNotEmpty(postalArress))
						{
							address1=postalArress.getString("address1");
							address2=postalArress.getString("address2");
							city=postalArress.getString("city");
							postalCode=postalArress.getString("postalCode");
							
						}
						}
						if(UtilValidate.isNotEmpty(phoneMechId))
						{	
						telephoneGV=delegator.findByPrimaryKey("TelecomNumber",UtilMisc.toMap("contactMechId",EntityOperator.EQUALS,phoneMechId));
						if(UtilValidate.isNotEmpty(telephoneGV))
						{
							contactNumber=telephoneGV.getString("contactNumber");
						}
						}
						
						
						
						
						
						
					}
					
					
			}//if
				String firstName=order.getString("customerFirstName");
				String lastName=order.getString("customerLastName");
				String orderId1=order.getString("orderId");
				dailySlot.put("orderId",orderId1);
				dailySlot.put("orderId",orderId1);
				dailySlot.put("grandTotal",orderId1);
				
				
				
				dailyList.add(dailySlot);
				i=i+1;
			}
			
		}
		
		
		request.setAttribute("dailyList",dailyList);
		request.setAttribute("fromDateStr", start);
		request.setAttribute("thruDateStr", end);
		
		
	
}catch(Exception e)
{
	e.printStackTrace();
}
return "success";
}
	
public static  String revenueReportCSV(HttpServletRequest request, HttpServletResponse response){
	try{
		delegator = (GenericDelegator)request.getAttribute("delegator");
		
		String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		try{
			
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}

				if (UtilValidate.isEmpty(fromDate)
						&& UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));
				}
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		List<GenericValue> revenues = new ArrayList<GenericValue>();
		HttpSession session = request.getSession();
		String productStoreId = (String) session.getAttribute("productStoreId");
		
		
		
		List<EntityCondition> orderStatus = new ArrayList<EntityCondition>();
		orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
		orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		EntityConditionList<EntityCondition> orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
			
		List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(start))
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		dateCondiList.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		EntityConditionList<EntityCondition> dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

		List<EntityCondition> mainExprs = new ArrayList<EntityCondition>();
			if(productStoreId != null)
			mainExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			mainExprs.add(dateCondition);
		    mainExprs.add(orderStatusCondition);
		
		
		
		EntityConditionList<EntityCondition> mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
		List<GenericValue> revenue=EntityUtil.getFieldListFromEntityList(revenues, "productId", true);
      
		

		response.setContentType("application/excel");
		
		response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
		StringBuffer data = new StringBuffer();

		data.append("\n");
		data.append("#--------------------------------------------------------------");
		data.append("\n");
		
		
		data.append("Date Range  : " + start + " To " +  end);
		data.append("\n");
		data.append("#--------------------------------------------------------------" );
		data.append("\n");
		data.append("\n");
		data.append(" Sl.No , category Name,Product ID, Product Name, Quantity Sold,Total Value Sold");
		data.append("\n");
		data.append("\n");
		
		if(UtilValidate.isNotEmpty(revenue)) {
			int slNumber = 0;
			
			double total = 0;
			double totalquantitySold = 0 ;
			
			for(int j=0;j<revenue.size();j++) {
				
				
				double subTotal = 0;
				double quantitySold = 0 ;
			
				GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",revenue.get(j)));
				if(productGv==null) continue;
				
				slNumber++;
				data.append("\""+slNumber+"\"");
				data.append(",");
				GenericValue gvtem=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productGv.getString("primaryProductCategoryId")));
				
				if(UtilValidate.isNotEmpty(gvtem))
				{
				data.append("\""+gvtem.getString("categoryName")+"\"");
				data.append(",");
				}
				else
					data.append(",");
				data.append("\""+revenue.get(j)+"\"");
				data.append(",");
				
				String productName = productGv.getString("productName");
				if(productName != null)
					productName = productName.replaceAll(",", "&");
				data.append("\""+productName+"\"");
				data.append(",");

				
				List<GenericValue> orders =EntityUtil.filterByAnd(revenues, UtilMisc.toMap("productId", revenue.get(j)));
					for(int i=0;i<orders.size();i++){

						GenericValue  OrderGV = (GenericValue)orders.get(i);
						quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
						subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
					}
				
				data.append(quantitySold);
				data.append(",");
				data.append(subTotal);
				data.append("\n");
				
				total = total + subTotal;
				totalquantitySold = totalquantitySold + quantitySold;
				}
				data.append("\n");
				data.append("\n");
				
				data.append(" Total,,,,"+totalquantitySold+","+total);
		}
		
		OutputStream out = response.getOutputStream();
		out.write(data.toString().getBytes());
		out.flush();
		request.setAttribute("fromDateStr", start);
		request.setAttribute("thruDateStr", end);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return "success";
}	
	

public static  String PreparePurchaseCSV(HttpServletRequest request, HttpServletResponse response){
	try{
		delegator = (GenericDelegator)request.getAttribute("delegator");
	 dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		String facilityId="";
		try{
			
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}

				if (UtilValidate.isEmpty(fromDate)
						&& UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));
				}
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayEnd(UtilDateTime
							.getDayStart(UtilDateTime.nowTimestamp()));

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start = new Timestamp(d.getTime());
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end = new Timestamp(d1.getTime());
				}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		List<GenericValue> revenues = new ArrayList<GenericValue>();
		
		
		HttpSession session = request.getSession();
String productStoreId="9000";
		
		if(UtilValidate.isNotEmpty(productStoreId))
		{
			GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			facilityId=productStore.getString("inventoryFacilityId");
		}
		
		
		List<EntityCondition> orderStatus = new ArrayList<EntityCondition>();
		
		orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		EntityConditionList<EntityCondition> orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
			
		List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(start))
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		dateCondiList.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		EntityConditionList<EntityCondition> dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

		List<EntityCondition> mainExprs = new ArrayList<EntityCondition>();
			if(productStoreId != null)
			mainExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			mainExprs.add(dateCondition);
		    mainExprs.add(orderStatusCondition);
		
		
		
		EntityConditionList<EntityCondition> mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
		List<GenericValue> revenue=EntityUtil.getFieldListFromEntityList(revenues, "productId", true);
      
		

		response.setContentType("application/excel");
		
		response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
		StringBuffer data = new StringBuffer();

		data.append("\n");
		data.append("#--------------------------------------------------------------");
		data.append("\n");
		
		
		data.append("Date Range  : " + start + " To " +  end);
		data.append("\n");
		data.append("#--------------------------------------------------------------" );
		data.append("\n");
		data.append("\n");
		data.append(" Sl.No , category Name,Product ID, Product Name, Cost Price,Quantity Ordered,Quantity On Hand,Total To Be Purchased(QOH-Quantity Ordered),Total Amount");
		data.append("\n");
		data.append("\n");
		
		if(UtilValidate.isNotEmpty(revenue)) {
			int slNumber = 0;
			
			double total = 0;
			double totalquantitySold = 0 ;
			double totalAmount = 0 ;
			double totalQOH = 0 ;
			double totalToPurchased=0;
			
			for(int j=0;j<revenue.size();j++) {
				
				
				double subTotal = 0;
				double quantitySold = 0 ;
			
				GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",revenue.get(j)));
				if(productGv==null) continue;
				
				slNumber++;
				data.append("\""+slNumber+"\"");
				data.append(",");
				GenericValue gvtem=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productGv.getString("primaryProductCategoryId")));
				
				if(UtilValidate.isNotEmpty(gvtem))
				{
				data.append("\""+gvtem.getString("categoryName")+"\"");
				data.append(",");
				}
				else
					data.append(",");
				data.append("\""+revenue.get(j)+"\"");
				data.append(",");
				
				String productName = productGv.getString("productName");
				if(productName != null)
					productName = productName.replaceAll(",", "&");
				if(productGv.getString("brandName")!=null)
					productName=productGv.getString("brandName")+" "+productName;
				data.append("\""+productName+"\"");
				data.append(",");

				
				List<GenericValue> orders =EntityUtil.filterByAnd(revenues, UtilMisc.toMap("productId", revenue.get(j)));
					for(int i=0;i<orders.size();i++){

						GenericValue  OrderGV = (GenericValue)orders.get(i);
						quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
						subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
					}
					Map resultOutput=new HashMap();
					resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", UtilMisc.toMap("productId", revenue.get(j), "facilityId", facilityId));
					List<EntityCondition> condnList=FastList.newInstance();
					List orderBy = UtilMisc.toList("-lastUpdatedStamp");
					condnList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, revenue.get(j)));
					condnList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
					List<GenericValue> unitpriceList=delegator.findByCondition("InventoryItem",EntityCondition.makeCondition(condnList,EntityOperator.AND) , UtilMisc.toSet("unitCost","vatPercentage"), orderBy);
					
					BigDecimal unitPrice=null;
					BigDecimal vatPer=null;
					BigDecimal unitCost=null;
		
					if(UtilValidate.isNotEmpty(unitpriceList))
					{
					
					GenericValue lastestUnitPrice=EntityUtil.getFirst(unitpriceList);
					 unitPrice=lastestUnitPrice.getBigDecimal("unitCost");
					vatPer=lastestUnitPrice.getBigDecimal("vatPercentage");
					if(UtilValidate.isNotEmpty(unitPrice)&& UtilValidate.isNotEmpty(vatPer))
					{
						unitCost=unitPrice.multiply(vatPer);
						data.append(unitCost);
					}
					else if(UtilValidate.isNotEmpty(unitPrice))
					{
						unitCost=unitPrice;
						data.append(unitCost);
					}
					else
						data.append(",");
					
					
					}
					else
						data.append(",");
					data.append(",");
				data.append(quantitySold);
				data.append(",");
				if(UtilValidate.isNotEmpty(resultOutput))
				{
				data.append(resultOutput.get("quantityOnHandTotal"));
			if(((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue()>0.0)
				totalQOH=totalQOH+((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
				}
				else
					data.append(",");
				data.append(",");
				if(UtilValidate.isNotEmpty(resultOutput))
				{
					data.append(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue());
					if((quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue())>0)
					totalToPurchased=totalToPurchased+quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
				}
				else
					data.append(",");	
				data.append(",");
				if((quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue())>0)
				{
					if(UtilValidate.isNotEmpty(unitCost)){
						
					data.append(unitCost.doubleValue()*(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue()));
					totalAmount= totalAmount+unitCost.doubleValue()*(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue()) ;
					}
					else
						data.append(",");
				}
				else
					data.append(",");
				//data.append(subTotal);
				data.append("\n");
				
				//total = total + subTotal;
				totalquantitySold = totalquantitySold + quantitySold;
				
				}
				data.append("\n");
				data.append("\n");
				
				data.append(" Total,,,,,"+totalquantitySold+","+totalQOH+","+totalToPurchased+","+totalAmount);
		}
		
		OutputStream out = response.getOutputStream();
		out.write(data.toString().getBytes());
		out.flush();
		request.setAttribute("fromDateStr", start);
		request.setAttribute("thruDateStr", end);
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	return "success";
}	
	





public static double getOrderProductPrice(String productId,String orderId,String orderItemSeqId){
	 double price =0;
	 double actPrice =0;
	 double descPrice =0;

	 try{
			GenericValue orderItemGV = delegator.findByPrimaryKey("OrderItem",UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId));
			price = orderItemGV.getDouble("unitPrice").doubleValue()*orderItemGV.getDouble("quantity").doubleValue() ;
	 }catch (Exception e) {
		 e.printStackTrace();
		 ////System.out.println("e === ReportsHelper.java: : "+e.getMessage());
				 }	
		return price;
	}

}
