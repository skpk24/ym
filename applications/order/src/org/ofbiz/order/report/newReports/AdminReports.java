package org.ofbiz.order.report.newReports;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import javolution.util.FastList;
//import javolution.util.FastMap;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.report.Report;
import org.ofbiz.order.shoppingcart.CheckOutEvents;
import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.product.category.CategoryWorker;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class AdminReports {
	
	    public static final String module = AdminReports.class.getName();

		protected static LocalDispatcher dispatcher = null;
		
		private static GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");

		private static GenericValue GenericValue;
		
		public static String orderSummaryReport(HttpServletRequest request, HttpServletResponse response){
			
			
			GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
			
			String entryDate = null;
			//List exprs = FastList.newInstance();
			List exprs = new ArrayList();
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
				
				
				
				//List<EntityCondition> condn = FastList.newInstance();
				List<EntityCondition> condn = new ArrayList<EntityCondition>();
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
			//List exprs = FastList.newInstance();
			List exprs = new ArrayList();
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
				
				
				
				//List<EntityCondition> condn = FastList.newInstance();
				List<EntityCondition> condn = new ArrayList<EntityCondition>();
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
	//List exprs = FastList.newInstance();
	List exprs = new ArrayList();
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
		//List<EntityCondition> condn = FastList.newInstance();
		List<EntityCondition> condn = new ArrayList<EntityCondition>();
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
		//List dailyList=FastList.newInstance();
		List dailyList=new ArrayList();
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
		data.append(" Sl.No ,Product ID, Product Name, Quantity Sold,Total Value Sold");
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
				//GenericValue gvtem=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productGv.getString("primaryProductCategoryId")));
				
			/*	if(UtilValidate.isNotEmpty(gvtem))
				{
				data.append("\""+gvtem.getString("categoryName")+"\"");
				data.append(",");
				}
				else
					data.append(",");*/
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
		String thruDate = request.getParameter("maxDate");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		String slotType = request.getParameter("slotType");
		String facilityId="";
		try{
			if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
				Date d = (Date) sdf.parse(fromDate);
				start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
				Date d1 = (Date) sdf.parse(thruDate);
				end = UtilDateTime.getDayStart( new Timestamp(d1.getTime()));
			}

			if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				end = 	UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			}
						
			if (UtilValidate.isEmpty(thruDate))
				end = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			
			if (UtilValidate.isEmpty(fromDate))
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
			
			if (!UtilValidate.isEmpty(fromDate)) {
				Date d = (Date) sdf.parse(fromDate);
				start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
			}
			
			if (!UtilValidate.isEmpty(thruDate)) {
				Date d1 = (Date) sdf.parse(thruDate);
				end =UtilDateTime.getDayStart( new Timestamp(d1.getTime()));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		List<GenericValue> revenues = new ArrayList<GenericValue>();
		
		HttpSession session = request.getSession();
		String productStoreId = request.getParameter("storeId");
		
		if(UtilValidate.isNotEmpty(productStoreId))
		{
			GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			facilityId=productStore.getString("inventoryFacilityId");
		}
		
		List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(start))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		EntityConditionList<EntityCondition> dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

		List<EntityCondition> mainExprs = new ArrayList<EntityCondition>();
			if(productStoreId != null)
			mainExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			mainExprs.add(dateCondition);
		    mainExprs.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		    if(!(slotType.equals("All")))
		    	  mainExprs.add(EntityCondition.makeCondition("slot", EntityOperator.EQUALS, slotType));
		    mainExprs.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		EntityConditionList<EntityCondition> mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
		List<GenericValue> revenue=EntityUtil.getFieldListFromEntityList(revenues, "productId", true);
      
		
		DecimalFormat df = new DecimalFormat("###.##");
		response.setContentType("application/excel");
		
		response.setHeader("Content-disposition","attachment;filename=PurchaseSummary.csv");
		StringBuffer data = new StringBuffer();

		data.append("\n");
		data.append("#--------------------------------------------------------------");
		data.append("\n");
		
		
		data.append("Date Range  : " + start + " To " +  end);
		data.append("\n");
		data.append("#--------------------------------------------------------------" );
		data.append("\n");
		data.append("\n");
		data.append(" Sl.No ,Category Name,Product ID, Product Name,Cost Price,Quantity Ordered,Quantity On Hand,Total To Be Purchased(QOH-Quantity Ordered),Total Price");
		data.append("\n");
		data.append("\n");
		
		List<EntityCondition> productCondn=new ArrayList<EntityCondition>();
		productCondn.add(EntityCondition.makeCondition("productId", EntityOperator.IN,revenue));
		productCondn.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL,"DIGITAL_GOOD"));
		 List<GenericValue> productList = delegator.findByCondition("Product",EntityCondition.makeCondition(productCondn, EntityOperator.AND),null,null );
		
		if(UtilValidate.isNotEmpty(productList)) {
			int slNumber = 0;
			
			double total = 0;
			double totalquantitySold = 0 ;
			double totalAmount = 0 ;
			double totalQOH = 0 ;
			double totalToPurchased=0;
			
			
			for(GenericValue productGv:productList) {
				double subTotal = 0;
				double quantitySold = 0 ;
			
				GenericValue gvtem=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productGv.getString("primaryProductCategoryId")));
				slNumber++;
				data.append("\""+slNumber+"\"");
				data.append(",");
				if(UtilValidate.isNotEmpty(gvtem))
				{
				data.append("\""+gvtem.getString("categoryName")+"\"");
				data.append(",");
				}
				else
					data.append(",");
				data.append("\""+productGv.getString("productId")+"\"");
				data.append(",");
				
				String productName = productGv.getString("productName");
				if(productName != null)
					productName = productName.replaceAll(",", "&");
				if(productGv.getString("brandName")!=null)
					productName=productGv.getString("brandName")+" "+productName;
				data.append("\""+productName+"\"");
				data.append(",");

				
				List<GenericValue> orders =EntityUtil.filterByAnd(revenues, UtilMisc.toMap("productId", productGv.getString("productId")));
					for(int i=0;i<orders.size();i++){

						GenericValue  OrderGV = (GenericValue)orders.get(i);
						quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
						subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
					}
					Map resultOutput=new HashMap();
					resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", UtilMisc.toMap("productId", productGv.getString("productId"), "facilityId", facilityId));
					//List<EntityCondition> condnList=FastList.newInstance();
					List<EntityCondition> condnList=new ArrayList<EntityCondition>();
					List orderBy = UtilMisc.toList("-createdStamp");
					condnList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productGv.getString("productId")));
					condnList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
					condnList.add(EntityCondition.makeCondition("unitCost",EntityOperator.GREATER_THAN,  new BigDecimal(0)));
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
						unitCost=unitPrice.add(unitPrice.multiply((vatPer.divide(new BigDecimal(100)))));
					
						data.append("\""+unitCost.setScale(2, RoundingMode.CEILING)+"\"");
						data.append(",");
					}
					else if(UtilValidate.isNotEmpty(unitPrice))
					{
						unitCost=unitPrice;
						data.append("\""+unitCost.setScale(2, RoundingMode.CEILING)+"\"");
						data.append(",");
					}
					}
					else
						data.append(",");
					
				data.append("\""+quantitySold+"\"");
				data.append(",");
				if(UtilValidate.isNotEmpty(resultOutput) && UtilValidate.isNotEmpty(resultOutput.get("quantityOnHandTotal")))
				{
				data.append("\""+resultOutput.get("quantityOnHandTotal")+"\"");
				data.append(",");
				totalQOH=totalQOH+((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
				}
				else
					data.append(",");
				
				if(UtilValidate.isNotEmpty(resultOutput))
				{
					if(((BigDecimal)resultOutput.get("quantityOnHandTotal")).doubleValue()-quantitySold<=0)
					{
						//purchaseItem=quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
					data.append("\""+(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue())+"\"");
					data.append(",");	
					totalToPurchased=totalToPurchased+quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
					}
					else
						data.append(",");	
				}
				else
					data.append(",");	
			
				if((quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue())>0)
				{
					if(UtilValidate.isNotEmpty(unitCost)){
						
					data.append("\""+df.format((unitCost.doubleValue()*(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue())))+"\"");
					data.append(",");
					totalAmount= totalAmount+unitCost.doubleValue()*(quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue()) ;
					}
					else
						data.append(",");
				}
				else
					data.append(",");
				
				//data.append(subTotal);
				data.append("\n");
				
				total = total + subTotal;
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

public static  String orderReoprt(HttpServletRequest request, HttpServletResponse response){
	try{
		delegator = (GenericDelegator)request.getAttribute("delegator");
	 dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	String dayOption=request.getParameter("dayOption");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		String facilityId="";
		List<Map> collectionMap=new ArrayList<Map>();
		try{
			if(UtilValidate.isNotEmpty(dayOption))
			{
			if(dayOption.equals("sameDay"))
			{
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				end = UtilDateTime.getDayEnd(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
			}
			if(dayOption.equals("nextDay"))
			{
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),1);
				end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp(), (long) 1);
			}
		
			if(dayOption.equals("futureDay"))
			{
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(),2);
				
			}
			
			}
			else
			{
				start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				end = UtilDateTime.getDayEnd(UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()));
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
		List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(start))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end)&& !(dayOption.equals("futureDay")))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		EntityConditionList<EntityCondition> dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

		List<EntityCondition> mainExprs = new ArrayList<EntityCondition>();
			if(productStoreId != null)
			mainExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			mainExprs.add(dateCondition);
		    mainExprs.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		
		EntityConditionList<EntityCondition> mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
		List<GenericValue> revenue=EntityUtil.getFieldListFromEntityList(revenues, "productId", true);
		if(UtilValidate.isNotEmpty(revenue)) {
			int slNumber = 0;
			double total = 0;
			double totalquantitySold = 0 ;
			double totalAmount = 0 ;
			double totalQOH = 0 ;
			double totalToPurchased=0;
			for(int j=0;j<revenue.size();j++) {
				Map<Object,Object> OrderListMap=new HashMap<Object,Object>();
				double subTotal = 0;
				double quantitySold = 0 ;
				GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",revenue.get(j)));
				if(productGv==null) continue;
				slNumber++;
				OrderListMap.put("slNumber",slNumber);
				OrderListMap.put("productId",revenue.get(j));
				String productName = productGv.getString("productName");
				if(productName != null)
					productName = productName.replaceAll(",", "&");
				if(productGv.getString("brandName")!=null)
					productName=productGv.getString("brandName")+" "+productName;
				OrderListMap.put("productName",productName);
				List<GenericValue> orders =EntityUtil.filterByAnd(revenues, UtilMisc.toMap("productId", revenue.get(j)));
					for(int i=0;i<orders.size();i++){
						GenericValue  OrderGV = (GenericValue)orders.get(i);
						quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
					}
					Map resultOutput=new HashMap();
					TransactionUtil.begin();
					resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", UtilMisc.toMap("productId", revenue.get(j), "facilityId", facilityId));
					TransactionUtil.commit();
					//List<EntityCondition> condnList=FastList.newInstance();
					List<EntityCondition> condnList=new ArrayList<EntityCondition>();
					List orderBy = UtilMisc.toList("-lastUpdatedStamp");
					condnList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, revenue.get(j)));
					condnList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
					condnList.add(EntityCondition.makeCondition("unitCost",EntityOperator.GREATER_THAN, BigDecimal.ZERO));
					
					List<GenericValue> unitpriceList=delegator.findByCondition("InventoryItem",EntityCondition.makeCondition(condnList,EntityOperator.AND) , UtilMisc.toSet("unitCost","vatPercentage"), orderBy);
					BigDecimal unitPrice=null;
					BigDecimal vatPer=null;
					BigDecimal unitCost=null;
					GenericValue unitprzLst = null;
					if(UtilValidate.isNotEmpty(unitpriceList))
					{
					//GenericValue lastestUnitPrice=EntityUtil.getFirst(unitpriceList);
						
						//for(int k=0;k<unitpriceList.size();k++) {
							//unitprzLst = unitpriceList.get(k);
						unitprzLst = EntityUtil.getFirst(unitpriceList);
							BigDecimal ZERO = BigDecimal.ZERO;
							if(UtilValidate.isNotEmpty(unitprzLst.getBigDecimal("unitCost")))
							if(UtilValidate.isNotEmpty(unitprzLst.getBigDecimal("unitCost").compareTo(BigDecimal.ZERO) > 0)) {
								unitPrice=unitprzLst.getBigDecimal("unitCost");
								vatPer=unitprzLst.getBigDecimal("vatPercentage");
							}
						//}
					/*unitPrice=lastestUnitPrice.getBigDecimal("unitCost");
					vatPer=lastestUnitPrice.getBigDecimal("vatPercentage");*/
					if(UtilValidate.isNotEmpty(unitPrice)&& UtilValidate.isNotEmpty(vatPer))
					{
						unitCost=unitPrice.add(unitPrice.multiply((vatPer.divide(new BigDecimal(100)))));
						
						OrderListMap.put("unitCost",unitCost.doubleValue());
					}
					else if(UtilValidate.isNotEmpty(unitPrice))
					{
						unitCost=unitPrice;
						OrderListMap.put("unitCost",unitCost.doubleValue());
					}
					}
					
					OrderListMap.put("quantitySold",quantitySold);
				
				
				if(UtilValidate.isNotEmpty(resultOutput))
				{
					OrderListMap.put("quantityOnHandTotal",resultOutput.get("quantityOnHandTotal"));
				}
				double toPurchased = quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
				if(toPurchased < 0 )
					toPurchased = 0;
				if(UtilValidate.isNotEmpty(resultOutput))
				{
					OrderListMap.put("toPurchased",toPurchased);
					
				}
				
				if(toPurchased > 0 )
				{
					if(UtilValidate.isNotEmpty(unitCost)){
						OrderListMap.put("totalCost",unitCost.doubleValue()*(toPurchased));
					}
				}
				collectionMap.add(OrderListMap);
				}
				request.setAttribute("purchaseList",collectionMap);
				request.setAttribute("dayOption",dayOption);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	return "success";
}

public static  String salesReoprtCSV(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
	delegator = (GenericDelegator)request.getAttribute("delegator");
	 dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		String productId  = request.getParameter("productId");
		String categoryId  = request.getParameter("productCategoryIdTo");
		String brandName  = request.getParameter("brandName");
	
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null; 
		
	
		try{
			
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
					Date d1 = (Date) sdf.parse(thruDate);
					end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
				}

				if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = 	UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				}
							
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
							
				if (UtilValidate.isEmpty(fromDate))
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end =UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
				}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	    List<EntityCondition> mainExprs=new ArrayList<EntityCondition>(); 
		mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	    
	    if(UtilValidate.isNotEmpty(start))
		mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		mainExprs.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		if(UtilValidate.isNotEmpty(productId))
		mainExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		if(UtilValidate.isNotEmpty(categoryId))
		mainExprs.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, categoryId));
	
	DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
	dynamicViewEntity.addMemberEntity("OH", "OrderHeader");
	dynamicViewEntity.addAlias("OH", "orderId");
	dynamicViewEntity.addAlias("OH", "orderDate");
	dynamicViewEntity.addAlias("OH", "statusId");
	dynamicViewEntity.addAlias("OH", "orderTypeId");
	dynamicViewEntity.addAlias("OH", "createdBy");
	dynamicViewEntity.addAlias("OH", "slot");
	
	dynamicViewEntity.addMemberEntity("OI", "OrderItem");
	dynamicViewEntity.addAlias("OI", "orderId");
	dynamicViewEntity.addAlias("OI", "productId");
	dynamicViewEntity.addAlias("OI", "itemDescription");
	dynamicViewEntity.addAlias("OI", "orderItemSeqId");
	dynamicViewEntity.addAlias("OI", "productCategoryId");
	dynamicViewEntity.addAlias("OI", "unitListPrice");
	dynamicViewEntity.addAlias("OI", "unitPrice");
	dynamicViewEntity.addViewLink("OH", "OI", Boolean.FALSE,
			ModelKeyMap.makeKeyMapList("orderId"));
	
	
	/*dynamicViewEntity.addMemberEntity("OIR", "InventoryItemDetail");
	dynamicViewEntity.addAlias("OIR", "orderId");
	dynamicViewEntity.addAlias("OIR", "inventoryItemId");
	dynamicViewEntity.addAlias("OIR", "inventoryItemDetailSeqId");
	dynamicViewEntity.addAlias("OIR", "quantityOnHandDiff");
	dynamicViewEntity.addAlias("OIR", "availableToPromiseDiff");
	dynamicViewEntity.addAlias("OIR", "orderItemSeqId");
	dynamicViewEntity.addViewLink("OH", "OIR", Boolean.FALSE,
	ModelKeyMap.makeKeyMapList("orderId")); 
	
	dynamicViewEntity.addMemberEntity("II", "InventoryItem");
	dynamicViewEntity.addAlias("II", "inventoryItemId");
	dynamicViewEntity.addAlias("II", "unitCost");
	dynamicViewEntity.addAlias("II", "productId");
	dynamicViewEntity.addAlias("II", "vatPercentage");
	dynamicViewEntity.addViewLink("OIR", "II", Boolean.FALSE,
	ModelKeyMap.makeKeyMapList("inventoryItemId")); */
	
	dynamicViewEntity.addMemberEntity("PT", "Product");
	dynamicViewEntity.addAlias("PT", "productId");
	dynamicViewEntity.addAlias("PT", "brandName");
	dynamicViewEntity.addViewLink("OI", "PT", Boolean.FALSE,
			ModelKeyMap.makeKeyMapList("productId")); 
	
	
	
	
	if(UtilValidate.isNotEmpty(brandName))
		mainExprs.add(EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, brandName));
	mainExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
	

	TransactionUtil.begin();
	EntityListIterator eli = delegator.findListIteratorByCondition(
			dynamicViewEntity, EntityCondition.makeCondition(mainExprs,EntityOperator.AND), null, null, null, null);
	
	List<GenericValue> orderList = eli.getCompleteList();
	
	
	eli.close();
	TransactionUtil.commit();
	response.setContentType("application/excel");
	
	response.setHeader("Content-disposition","attachment;filename=SalesReport.csv");
	StringBuffer data = new StringBuffer();

	data.append("\n");
	data.append("#--------------------------------------------------------------");
	data.append("\n");
	
	
	data.append("Date Range  : " + start + " To " +  end);
	data.append("\n");
	data.append("#--------------------------------------------------------------" );
	data.append("\n");
	data.append("\n");
	data.append(" ORDER ID ,ORDER DATE,CUSTOMER NAME,PRODUCT NAME,BRAND NAME,CATEGORY NAME,COSTPRICE,QUANTITY, MRP, GROSS TOTAL,DISCOUNT, GRANDTOTAL");
	data.append("\n");
	data.append("\n");
	for(GenericValue salesList:orderList)
	{
		
	if(UtilValidate.isNotEmpty(salesList.getString("orderId")))
	{
		data.append("\""+salesList.getString("orderId")+"\"");
		data.append(",");
	
	}
	else
		data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("orderDate")))
	{
		data.append("\" "+salesList.getString("orderDate")+" "+"\"");
		data.append(",");
	}
	else
		data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("createdBy")))
	{
		data.append("\""+salesList.getString("createdBy")+"\"");
		data.append(",");
	
	}
	else
			data.append(",");

	if(UtilValidate.isNotEmpty(salesList.getString("itemDescription")))
	{
		data.append("\""+salesList.getString("itemDescription")+"\"");
		data.append(",");
	
	}
	else
		data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("brandName")))
	{
		data.append("\""+salesList.getString("brandName")+"\"");
		data.append(",");
	
	}
	else
			data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("productCategoryId")))
	{
		GenericValue gv=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",salesList.getString("productCategoryId")));
		data.append("\""+gv.getString("categoryName")+"\"");
		data.append(",");
	
	}
	else
			data.append(",");
	
	List<EntityCondition> condn=new ArrayList<EntityCondition>();
	
	
	
	condn.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, new BigDecimal(0)));
	condn.add(EntityCondition.makeCondition("availableToPromiseDiff", EntityOperator.EQUALS, new BigDecimal(0)));
	condn.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, salesList.getString("orderId")));
	condn.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, salesList.getString("orderItemSeqId")));
	List<GenericValue> quantityList=delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(condn,EntityOperator.AND), UtilMisc.toSet("inventoryItemId","quantityOnHandDiff"), null, null, false);
	BigDecimal totalQty=BigDecimal.ZERO;
	BigDecimal totalUnitCost=BigDecimal.ZERO;
	for(GenericValue quantityListTemp:quantityList)
	{
		BigDecimal qty=BigDecimal.ZERO;
		BigDecimal price=BigDecimal.ZERO;
		qty=quantityListTemp.getBigDecimal("quantityOnHandDiff").multiply(new BigDecimal(-1));
		totalQty=totalQty.add(qty);
		GenericValue unitList=delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId",quantityListTemp.getString("inventoryItemId")));
		if(UtilValidate.isNotEmpty(unitList))
		{
		if(UtilValidate.isNotEmpty(unitList.getDouble("vatPercentage")))
		price=unitList.getBigDecimal("unitCost").add(unitList.getBigDecimal("unitCost").multiply((unitList.getBigDecimal("vatPercentage").divide(new BigDecimal(100)))));
		else
		price=unitList.getBigDecimal("unitCost");
		totalUnitCost=totalUnitCost.add((price.multiply(qty))).setScale(2, RoundingMode.CEILING);
		
		}
		}
	
	
		if(totalUnitCost.doubleValue()>0.0)
		{
		data.append("\""+totalUnitCost+"\"");
		data.append(",");
		}
		else
		data.append(",");
	
		if(totalQty.doubleValue()>0.0)
		{
		data.append("\""+totalQty+"\"");
		data.append(",");
		}
		else
		data.append(",");
	
	if(UtilValidate.isNotEmpty(salesList.getString("unitPrice")))
	{
		if(salesList.getDouble("unitListPrice")>0.0)
		data.append("\""+salesList.getString("unitListPrice")+"\"");
		else
			data.append("\""+salesList.getString("unitPrice")+"\"");
			data.append(",");
	
	}
	else
		data.append(",");
	double grossTotal=0;
	if(UtilValidate.isNotEmpty(salesList.getString("unitPrice")))
	{
		if(salesList.getDouble("unitListPrice")>0.0)
		{
		data.append("\""+salesList.getDouble("unitListPrice")*totalQty.doubleValue()+"\"");
		grossTotal=salesList.getDouble("unitListPrice")*totalQty.doubleValue();
		}
		else
		{
			grossTotal=salesList.getDouble("unitPrice")*(-1)*totalQty.doubleValue();
			data.append("\""+salesList.getDouble("unitPrice")*(-1)*totalQty.doubleValue()+"\"");
		}
			data.append(",");
	
	}
	else
		data.append(",");
	
	
	List<EntityCondition> condnList=new ArrayList<EntityCondition>();
	condnList.add(EntityCondition.makeCondition("orderId",EntityOperator.EQUALS,salesList.getString("orderId")));
	condnList.add(EntityCondition.makeCondition("orderItemSeqId",EntityOperator.EQUALS,salesList.getString("orderItemSeqId")));
	GenericValue priceGv=EntityUtil.getFirst(delegator.findList("OrderItemPriceInfo", EntityCondition.makeCondition(condnList,EntityOperator.AND), UtilMisc.toSet("modifyAmount"), null, null, false));
	
	double discount=0;
	if(UtilValidate.isNotEmpty(priceGv))
	{
		discount=(priceGv.getDouble("modifyAmount")*totalQty.doubleValue())*(-1);
		data.append("\""+(priceGv.getDouble("modifyAmount")*totalQty.doubleValue())*(-1)+"\"");
		data.append(",");
	}
	else
		data.append(",");

	if(discount>0)
	data.append("\""+(grossTotal-discount)+"\"");
	else
		data.append("\""+(grossTotal)+"\"");
	data.append(",");
	
	
	data.append("\n");
		
	}

	OutputStream out;
	try {
		out = response.getOutputStream();
		out.write(data.toString().getBytes());
		out.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return "success";

}

public static  String stockInHandCSV(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		delegator = (GenericDelegator)request.getAttribute("delegator");
		dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		//String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		String productId  = request.getParameter("productId");
		String categoryId  = request.getParameter("productCategoryIdTo");
		String brandName  = request.getParameter("brandName");
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		
	
		try{
			
				if ( !UtilValidate.isEmpty(thruDate)) {
					
					Date d1 = (Date) sdf.parse(thruDate);
					end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
				}

				if (UtilValidate.isEmpty(thruDate)) {
					
					end = 	UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				}
							
				
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	    List<EntityCondition> mainExprs=new ArrayList<EntityCondition>(); 
	    //if(UtilValidate.isNotEmpty(start))
		//mainExprs.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		mainExprs.add(EntityCondition.makeCondition("effectiveDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		if(UtilValidate.isNotEmpty(productId))
		mainExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		if(UtilValidate.isNotEmpty(categoryId))
		mainExprs.add(EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, categoryId));
	
		if(UtilValidate.isNotEmpty(brandName))
			mainExprs.add(EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, brandName));
	 List orderBy = UtilMisc.toList("-createdStamp");
		 EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
		List<GenericValue> orderList=delegator.findList("InventoryItemDetailForSum",EntityCondition.makeCondition(mainExprs,EntityOperator.AND), null, null, findOpts, false);
		
		
	response.setContentType("application/excel");
	
	response.setHeader("Content-disposition","attachment;filename=StockInHand.csv");
	StringBuffer data = new StringBuffer();

	data.append("\n");
	data.append("#--------------------------------------------------------------");
	data.append("\n");
	
	
	data.append("The Date  : " + end);
	data.append("\n");
	data.append("#--------------------------------------------------------------" );
	data.append("\n");
	data.append("\n");
	data.append(" PRODUCT ID ,PRODUCT NAME,BRAND NAME,CATEGORY NAME,STOCK IN HAND,STOCK IN HAND(COST PRICE)");
	data.append("\n");
	data.append("\n");
	for(GenericValue salesList:orderList)
	{
	
	if(UtilValidate.isNotEmpty(salesList.getString("productId")))
	{
		data.append("\""+salesList.getString("productId")+"\"");
		data.append(",");
	
	}
	else
		data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("productName")))
	{
		data.append("\""+salesList.getString("productName")+"\"");
		data.append(",");
	}
	else
		data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("brandName")))
	{
		data.append("\""+salesList.getString("brandName")+"\"");
		data.append(",");
	
	}
	else
			data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("primaryProductCategoryId")))
	{
		GenericValue gv=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",salesList.getString("primaryProductCategoryId")));
		data.append("\""+gv.getString("categoryName")+"\"");
		data.append(",");
	
	}
	else
			data.append(",");
	if(UtilValidate.isNotEmpty(salesList.getString("quantityOnHandSum")))
	{
		data.append("\""+salesList.getString("quantityOnHandSum")+"\"");
		data.append(",");
	}
	else
			data.append(",");
	//List<EntityCondition> condnList=FastList.newInstance();
	List<EntityCondition> condnList=new ArrayList<EntityCondition>();
	
	condnList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, salesList.getString("productId")));
	condnList.add(EntityCondition.makeCondition("unitCost",EntityOperator.GREATER_THAN,  new BigDecimal(0)));
	GenericValue priceGv=EntityUtil.getFirst(delegator.findList("InventoryItem", EntityCondition.makeCondition(condnList,EntityOperator.AND), UtilMisc.toSet("vatPercentage","unitCost"), orderBy, findOpts, false));

	if(UtilValidate.isNotEmpty(priceGv.getString("unitCost")) && priceGv.getDouble("unitCost") >0.0)
	{
		if(UtilValidate.isNotEmpty(priceGv.getDouble("vatPercentage")))
			
			data.append("\""+(priceGv.getBigDecimal("unitCost").add(priceGv.getBigDecimal("unitCost").multiply((priceGv.getBigDecimal("vatPercentage").divide(new BigDecimal(100)))))).setScale(2, RoundingMode.CEILING)+"\"");
	else
			data.append("\""+priceGv.getBigDecimal("unitCost").setScale(2, RoundingMode.CEILING)+"\"");
		data.append(",");
	
	}
	else
			data.append(",");
	data.append("\n");
		}
	

	OutputStream out;
	try {
		out = response.getOutputStream();
		out.write(data.toString().getBytes());
		out.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return "success";

}
public static String orderwiseSalesReport(HttpServletRequest request, HttpServletResponse response){
	
	
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	
	String entryDate = null;
	//List exprs = FastList.newInstance();
	List exprs = new ArrayList();
	String fromDate = request.getParameter("minDate");
	String thruDate = request.getParameter("maxDate");
	String slotType = request.getParameter("slotType");
	String paymentMethod=request.getParameter("paymentMethod");
	String zoneType=request.getParameter("zoneType");
	
	String partyId = request.getParameter("partyId");
	String userLoginId = request.getParameter("userLoginId");
	
	Date date = new Date();
try{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Timestamp start = null;
	Timestamp end = null;
	if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
		Date d = (Date) sdf.parse(fromDate);
		start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
		Date d1 = (Date) sdf.parse(thruDate);
		end = UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
	}

	if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
		start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		end = 	UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	}
				
	if (UtilValidate.isEmpty(thruDate))
		end = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
				
	if (UtilValidate.isEmpty(fromDate))
		start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

	if (!UtilValidate.isEmpty(fromDate)) {
		Date d = (Date) sdf.parse(fromDate);
		start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
	}
	if (!UtilValidate.isEmpty(thruDate)) {
		Date d1 = (Date) sdf.parse(thruDate);
		end =UtilDateTime.getDayEnd( new Timestamp(d1.getTime()));
	}
		
	//List<EntityCondition> condn = FastList.newInstance();
	List<EntityCondition> condn = new ArrayList<EntityCondition>();
	 if(!(slotType.equals("All") ) && UtilValidate.isNotEmpty(slotType))
	 condn.add(EntityCondition.makeCondition("slot",EntityOperator.EQUALS,slotType));
	
	 if(UtilValidate.isNotEmpty(start))
	  condn.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
	 if(UtilValidate.isNotEmpty(end))
	condn.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
	
	condn.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
	
	if(UtilValidate.isEmpty(userLoginId) && UtilValidate.isNotEmpty(partyId))
	{
		GenericValue userLogin = PartyWorker.findPartyLatestUserLogin(partyId, delegator);
		if(UtilValidate.isNotEmpty(userLogin))
			userLoginId = userLogin.getString("userLoginId");
	}
	if(UtilValidate.isNotEmpty(userLoginId))
		condn.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginId));
	
	
	List<GenericValue> orderList = delegator.findList("OrderHeader", EntityCondition.makeCondition(condn,EntityOperator.AND), null, null, null, false);	
	
	response.setContentType("application/excel");
	
	response.setHeader("Content-disposition","attachment;filename=OrderWiseSales.csv");
	StringBuffer data = new StringBuffer();

	data.append("\n");
	data.append("#--------------------------------------------------------------");
	data.append("\n");
	
	
	data.append("Date Range  : " + start + " To " +  end);
	data.append("\n");
	data.append("#--------------------------------------------------------------" );
	data.append("\n");
	data.append("\n");
	data.append(" ORDER ID ,ORDER DATE,SLOT TYPE,PAYMENT PREFERENCE,AREA NAME,ZONE,GRAND TOTAL");
	data.append("\n");
	data.append("\n");
	
	
	if(UtilValidate.isNotEmpty(orderList))	
	{
	for(GenericValue salesList:orderList)
	{
		
		if(UtilValidate.isNotEmpty(salesList.getString("orderId")))
		{
			data.append("\""+salesList.getString("orderId")+"\"");
			data.append(",");
		
		}
	else
			data.append(",");
		if(UtilValidate.isNotEmpty(salesList.getString("orderDate")))
		{
			data.append("\""+salesList.getString("orderDate")+"\"");
			data.append(",");
		
		}
		else
			data.append(",");
		if(UtilValidate.isNotEmpty(salesList.getString("slot")))
		{
			data.append("\""+salesList.getString("slot")+"\"");
			data.append(",");
		
		}
		else
			data.append(",");
		List<EntityCondition> paymentCondn=new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(paymentMethod) && !(paymentMethod.equals("All") ))
			paymentCondn.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, paymentMethod));
			paymentCondn.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
			paymentCondn.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, salesList.getString("orderId")));
		List<GenericValue> paymentListTem=EntityUtil.getFieldListFromEntityList((delegator.findList("OrderPaymentPreference", EntityCondition.makeCondition(paymentCondn,EntityOperator.AND), UtilMisc.toSet("paymentMethodTypeId"), null, null, false)),"paymentMethodTypeId",true);
		if(UtilValidate.isNotEmpty(paymentListTem))
		{
		List paymentList=EntityUtil.getFieldListFromEntityList((delegator.findList("PaymentMethodType",EntityCondition.makeCondition("paymentMethodTypeId",EntityOperator.IN,paymentListTem),UtilMisc.toSet("description"),null,null,false)),"description",true);
		String payment="";
		if(UtilValidate.isNotEmpty(paymentList))
		{
			for(int i=0;i<paymentList.size();i++)
			{
				if(i==paymentList.size()-1)
				payment=payment+paymentList.get(i);
				else
				payment=payment+paymentList.get(i)+",";
			}	
			data.append("\""+payment+"\"");
			data.append(",");
		
		}
		}
		else
			data.append(",");
		List<EntityCondition> zoneCondn=new ArrayList<EntityCondition>();
		 if(!(zoneType.equals("All") ) && UtilValidate.isNotEmpty(zoneType))
			 zoneCondn.add(EntityCondition.makeCondition("zoneGroupId",EntityOperator.EQUALS,zoneType));
		 	zoneCondn.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, salesList.getString("orderId")));
	
		 	
		 	
			List<GenericValue> zoneListTem=delegator.findList("OrderZone", EntityCondition.makeCondition(zoneCondn,EntityOperator.AND), UtilMisc.toSet("zoneName","zoneGroupName"), null, null, false);
			String zone="";
			if(UtilValidate.isNotEmpty(zoneListTem))
			{
				int i=0;
				for(GenericValue zoneList:zoneListTem)
				{
					if(i==zoneListTem.size()-1)
						zone=zone+zoneList.getString("zoneName");
					else
						zone=zone+zoneList.getString("zoneName")+",";
					i++;
				}	
				data.append("\""+zone+"\"");
				data.append(",");
			
			}
			else
				data.append(",");
			
			
			if(UtilValidate.isNotEmpty(zoneListTem))
			{
		
				for(GenericValue zoneList:zoneListTem)
				{
					data.append("\""+zoneList.getString("zoneGroupName")+"\"");
				
				}	
			
				data.append(",");
			
			}
			else
				data.append(",");
			
			
		if(UtilValidate.isNotEmpty(salesList.getString("grandTotal")))
		{
			data.append("\""+salesList.getString("grandTotal")+"\"");
			data.append(",");
		
		}
		else
			data.append(",");
		data.append("\n");
	}
	}
	OutputStream out;
	try {
		out = response.getOutputStream();
		out.write(data.toString().getBytes());
		out.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}catch(Exception e)
{
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
				 }	
		return price;
	}
public static double getOrderProductMRPPrice(String productId,String orderId,String orderItemSeqId){
	 double price =0;
	 double actPrice =0;
	 double descPrice =0;

	 try{
			GenericValue orderItemGV = delegator.findByPrimaryKey("OrderItem",UtilMisc.toMap("orderId",orderId,"orderItemSeqId",orderItemSeqId));
			price = orderItemGV.getDouble("unitPrice").doubleValue() ;
	 }catch (Exception e) {
		 e.printStackTrace();
				 }	
		return price;
	}


public static  String stockInOutReportCSV(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
    
	Map<String, Object> param =  ServiceUtil.returnSuccess();
	Map<String, Object> result1 = null;
	List val123 = new ArrayList();
	param =  ServiceUtil.returnSuccess();
	delegator = (GenericDelegator)request.getAttribute("delegator");
	dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	
	BigDecimal totalOrders = BigDecimal.ZERO;
	BigDecimal totalOrderReturn = BigDecimal.ZERO;
	BigDecimal totalDamaged = BigDecimal.ZERO;
	BigDecimal totalPurchased = BigDecimal.ZERO;
	BigDecimal totalOrdersQOH = BigDecimal.ZERO;
	BigDecimal totalOrderReturnQOH = BigDecimal.ZERO;
	BigDecimal totalDamagedQOH = BigDecimal.ZERO;
	BigDecimal totalPurchasedQOH = BigDecimal.ZERO;
	java.math.BigDecimal availableToPromiseTotal = BigDecimal.ZERO;
	java.math.BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
	BigDecimal quantityOnHandDiff = BigDecimal.ZERO;
	
	
	String productID  = request.getParameter("productId");
	String categoryId  = request.getParameter("productCategoryId");
	String brandName  = request.getParameter("brandName");
	List<GenericValue> productList = null;
	
	 productList = ProductWorker.getProductList(delegator, categoryId, productID, brandName);
	 
//	 System.out.println("\n\n\n productList"+productList.size());
	  
	
	String fromDate = request.getParameter("fromDate");
	String thruDate  = request.getParameter("thruDate");
	Date date = new Date();
	List dateCondiList = new ArrayList();
	try {
		
	if(UtilValidate.isEmpty(fromDate))
		fromDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp()).toString();
	if(UtilValidate.isEmpty(thruDate))
		thruDate  = UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp()).toString();
	
	 response.setContentType("application/excel");
	 response.setHeader("Content-disposition","attachment;filename=StockInOutReport.xls");
	 WorkbookSettings wbSettings = new WorkbookSettings();
	 WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
	 WritableSheet sheet=workbook.createSheet("mysheet", 0); 
	 WritableFont wfobj=new WritableFont(WritableFont.ARIAL,8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
	 WritableCellFormat cfobj=new WritableCellFormat(wfobj);
	 cfobj.setBackground(Colour.PINK);
	 cfobj.setWrap(true);
	 Label label=new Label(1,1,"Product Id"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);                                      
	 label=new Label(2,1,"Brand Name"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);  
	 label=new Label(3,1,"Product Name"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);  
	 label=new Label(4,1,"Category"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);  
	 label=new Label(5,1,"opening stock (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);  
	 label=new Label(6,1,"Qty Received (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 label=new Label(7,1,"Quantity Sold (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 label=new Label(8,1,"Quantity Return (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 label=new Label(9,1,"Quantity Adjust (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 label=new Label(10,1,"Closing Stock (Qty)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 label=new Label(11,1,"Closing stock (Amt at cost price)"); 
	 label.setCellFormat(cfobj);
	 sheet.addCell(label);
	 int countForExcel = 3;
	 EntityListIterator listIt = null;
	
	 if(UtilValidate.isNotEmpty(productList))
	 for (GenericValue product : productList) {
		 String productId = product.getString("productId");
		 param.put("facilityId", "WebStoreWarehouse");
    	 param.put("productId", productId);
    	 param.put("quantityOnHandDiff", "0");
    	 param.put("quantityOnHandDiff_op", "notEqual");
    	 
    	 param.remove("responseMessage");
    	
		 param.put("effectiveDate_fld0_value", fromDate);
    	 param.put("effectiveDate_fld0_op", "greaterThan");
    	 param.put("effectiveDate_fld1_value", thruDate);
    	 param.put("effectiveDate_fld1_op", "opLessThan");
		
    	 Map<String, Object> performFind =  ServiceUtil.returnSuccess();
    	
    	 performFind.remove("responseMessage");
    	 performFind.put("inputFields", param);
    	 performFind.put("entityName", "InventoryItemAndDetail");
    	 performFind.put("orderBy", "productId|inventoryItemId|-inventoryItemDetailSeqId|-effectiveDate|quantityOnHandTotal");
    	
    	try {
    		performFind = dispatcher.runSync("performFind", performFind);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Map<String, Object> val =  null;
    	
    	Map inventoryDetails = new HashMap();
    	Map inventoryItemDetails = new HashMap();
    	if(UtilValidate.isNotEmpty(performFind)&& performFind.size() > 0)
    	{
    		listIt = (org.ofbiz.entity.util.EntityListIterator)performFind.get("listIt");
    		
    		String inventoryItemId = null;
    		String preInventoryItemId = null;
    		
    		int count =1;
        	if(listIt != null)
        	{
    		for(;;){
    			inventoryItemDetails = new HashMap();
    			GenericValue inventory = listIt.next();
    			if(inventory == null)break;
        		if(inventory != null)
        		{
        			val =  ServiceUtil.returnSuccess();
        			String orderId = inventory.getString("orderId");
        			String returnId = inventory.getString("returnId");
        			String reasonEnumId = inventory.getString("reasonEnumId");
        			inventoryItemId = inventory.getString("inventoryItemId");
        			
        			quantityOnHandTotal = (java.math.BigDecimal)inventory.get("quantityOnHandTotal");
        			quantityOnHandDiff = (java.math.BigDecimal)inventory.get("quantityOnHandDiff");
        			if(UtilValidate.isNotEmpty(orderId))
        				totalOrdersQOH = totalOrdersQOH.add(quantityOnHandDiff);
        			else if(UtilValidate.isNotEmpty(returnId))
        				totalOrderReturnQOH = totalOrderReturnQOH.add(quantityOnHandDiff);
        			else if(UtilValidate.isNotEmpty(reasonEnumId))
        				totalDamagedQOH = totalDamagedQOH.add(quantityOnHandDiff);
        			
        			if(inventoryDetails.containsKey(inventoryItemId))
        			{
        				inventoryItemDetails = (Map) inventoryDetails.get(inventoryItemId);
        				BigDecimal totalOrdersQOHOld = (BigDecimal)inventoryItemDetails.get("");
        				BigDecimal totalOrderReturnQOHOld = (BigDecimal)inventoryItemDetails.get("");
        				BigDecimal adjustmentOld = (BigDecimal)inventoryItemDetails.get("");
        				if(UtilValidate.isNotEmpty(totalOrdersQOHOld) && UtilValidate.isNotEmpty(totalOrdersQOH))
        					inventoryItemDetails.put("QTY_SOLD", totalOrdersQOHOld.add(totalOrdersQOH));
        				if(UtilValidate.isNotEmpty(totalOrderReturnQOHOld) && UtilValidate.isNotEmpty(totalOrderReturnQOH))
        					inventoryItemDetails.put("QTY_RETURN", totalOrderReturnQOHOld.add(totalOrderReturnQOH));
        				if(UtilValidate.isNotEmpty(adjustmentOld) && UtilValidate.isNotEmpty(totalDamagedQOH))
        					inventoryItemDetails.put("adjustment", adjustmentOld.add(totalDamagedQOH));
        			}else
        			{
        				inventoryItemDetails.put("QTY_REC", quantityOnHandTotal);
        				inventoryItemDetails.put("unitCost", inventory.getBigDecimal("unitCost"));
	        			inventoryItemDetails.put("QTY_SOLD", totalOrdersQOH);
	        			inventoryItemDetails.put("QTY_RETURN", totalOrderReturnQOH);
	        			inventoryItemDetails.put("adjustment", totalDamagedQOH);
	        			
	        			GenericValue inventoryItem = delegator.findOne("InventoryItem", true,
	        					UtilMisc.toMap("inventoryItemId",inventoryItemId));
	        			GenericValue inventoryItemDetailFirst = 
	        				EntityUtil.getFirst(inventoryItem.getRelated("InventoryItemDetail", UtilMisc.toList("effectiveDate")));
	                    
	        			inventoryItemDetails.put("quantityRec", inventoryItemDetailFirst.getBigDecimal("quantityOnHandDiff"));
        			}
        			inventoryDetails.put(inventoryItemId, inventoryItemDetails);
        		  }
    		   }
        	}
	 	}
    	brandName = product.getString("brandName");
    	String internalName = product.getString("internalName");
    	BigDecimal invUnitCost = product.getBigDecimal("invUnitCost");
    	String unitCost = "";
    	if(UtilValidate.isNotEmpty(invUnitCost))
    		unitCost = invUnitCost.doubleValue()+"";
    	if(UtilValidate.isNotEmpty(inventoryDetails))
    	{
			BigDecimal qtyRec = BigDecimal.ZERO;
			BigDecimal qtySold = BigDecimal.ZERO;
			BigDecimal qtyReturn = BigDecimal.ZERO;
			BigDecimal qtyAdj = BigDecimal.ZERO;
			BigDecimal quantityRec = BigDecimal.ZERO;
			
    		Set<String> keys = inventoryDetails.keySet();
    		for(String key: keys)
    		{
    			inventoryItemDetails = (Map)inventoryDetails.get(key);
    			
    			qtyRec = qtyRec.add((BigDecimal)inventoryItemDetails.get("QTY_REC"));
    			qtySold = qtySold.add((BigDecimal)inventoryItemDetails.get("QTY_SOLD"));
    			qtyReturn = qtyReturn.add((BigDecimal)inventoryItemDetails.get("QTY_RETURN"));
    			qtyAdj = qtyAdj.add((BigDecimal)inventoryItemDetails.get("adjustment"));
    			
    			quantityRec = quantityRec.add((BigDecimal)inventoryItemDetails.get("quantityRec"));
    		}
			label=new Label(1,countForExcel,productId);  
			sheet.addCell(label);
			label=new Label(2,countForExcel,brandName);  
			sheet.addCell(label);
			label=new Label(3,countForExcel,internalName);  
			sheet.addCell(label);
			String categoryName = CategoryWorker.getProductPrimaryCategoryName(delegator, productId, true);
			label=new Label(4,countForExcel,categoryName);  
			sheet.addCell(label);
    		label=new Label(5,countForExcel,qtyRec.add((qtySold.add(qtyReturn).add(qtyAdj)).multiply(new BigDecimal(-1))).doubleValue()+"");
			sheet.addCell(label);
			label=new Label(6,countForExcel,quantityRec.doubleValue()+"");
			sheet.addCell(label);
			label=new Label(7,countForExcel,qtySold.multiply(new BigDecimal(-1)).doubleValue()+"");  
			sheet.addCell(label);
			label=new Label(8,countForExcel,qtyReturn.multiply(new BigDecimal(-1)).doubleValue()+"");  
			sheet.addCell(label);
			label=new Label(9,countForExcel,qtyAdj.multiply(new BigDecimal(-1)).doubleValue()+"");  
			sheet.addCell(label);
			label=new Label(10,countForExcel,qtyRec.doubleValue()+"");
			sheet.addCell(label);
			label=new Label(11,countForExcel,unitCost);
			sheet.addCell(label);
			
			countForExcel++;

    	}
    	
	 		    	if(UtilValidate.isNotEmpty(listIt)) listIt.close();
	 		    	
	    	        availableToPromiseTotal = BigDecimal.ZERO;
 		    		quantityOnHandTotal = BigDecimal.ZERO;
	    	        totalOrders = BigDecimal.ZERO;
	    			totalOrderReturn = BigDecimal.ZERO;
	    			totalDamaged = BigDecimal.ZERO;
	    			totalPurchased = BigDecimal.ZERO;
	    			totalOrdersQOH = BigDecimal.ZERO;
	    			totalOrderReturnQOH = BigDecimal.ZERO;
	    			totalDamagedQOH = BigDecimal.ZERO;
	    			totalPurchasedQOH = BigDecimal.ZERO;
	        	 }
	 				workbook.write();
					workbook.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	return "success";
}


public static  String dailyPurchaseSummaryCSV(HttpServletRequest request, HttpServletResponse response){
	try{
		delegator = (GenericDelegator)request.getAttribute("delegator");
	 dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
		String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp start = null;
		Timestamp end = null;
		String slotType=request.getParameter("slotType");
		String facilityId="";
		try{
			
				if (!UtilValidate.isEmpty(fromDate)&& !UtilValidate.isEmpty(thruDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
					Date d1 = (Date) sdf.parse(thruDate);
					end = UtilDateTime.getDayStart( new Timestamp(d1.getTime()));
				}

				if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
					end = 	UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
				}
							
				if (UtilValidate.isEmpty(thruDate))
					end = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
							
				if (UtilValidate.isEmpty(fromDate))
					start = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());

				if (!UtilValidate.isEmpty(fromDate)) {
					Date d = (Date) sdf.parse(fromDate);
					start =UtilDateTime.getDayStart( new Timestamp(d.getTime()));
				}
				if (!UtilValidate.isEmpty(thruDate)) {
					Date d1 = (Date) sdf.parse(thruDate);
					end =UtilDateTime.getDayStart( new Timestamp(d1.getTime()));
				}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		List<GenericValue> revenues = new ArrayList<GenericValue>();
		
		
		HttpSession session = request.getSession();
		String productStoreId= request.getParameter("storeId");
		
		if(UtilValidate.isNotEmpty(productStoreId))
		{
			GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			facilityId=productStore.getString("inventoryFacilityId");
		}
		
		List<EntityCondition> dateCondiList = new ArrayList<EntityCondition>();
		if(UtilValidate.isNotEmpty(start))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO, start));
		if(UtilValidate.isNotEmpty(end))
		dateCondiList.add(EntityCondition.makeCondition("deliveryDate",EntityOperator.LESS_THAN_EQUAL_TO, end));
		EntityConditionList<EntityCondition> dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

		List<EntityCondition> mainExprs = new ArrayList<EntityCondition>();
			if(productStoreId != null)
			mainExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
			mainExprs.add(dateCondition);
		    mainExprs.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		    if(!(slotType.equals("All")))
		    	  mainExprs.add(EntityCondition.makeCondition("slot", EntityOperator.EQUALS, slotType));
		    mainExprs.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
		EntityConditionList<EntityCondition> mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 revenues = delegator.findByCondition("OrderHeaderAndItems",mainCondition,null,null );
		List<GenericValue> revenue=EntityUtil.getFieldListFromEntityList(revenues, "productId", true);
 //		OrderHeaderAndItems
      
		

		response.setContentType("application/excel");
		
		response.setHeader("Content-disposition","attachment;filename=DailyPurchaseSummary.csv");
		StringBuffer data = new StringBuffer();

		data.append("\n");
		data.append("#--------------------------------------------------------------");
		data.append("\n");
		
		
		data.append("Date Range  : " + start + " To " +  end);
		data.append("\n");
		data.append("#--------------------------------------------------------------" );
		data.append("\n");
		data.append("\n");
		data.append(" Aisle Number ,Category Name,Product ID, Product Name,Quantity Ordered,Quantity On Hand,Total To Be Purchased(QOH-Quantity Ordered),MRP per Product");
		data.append("\n");
		data.append("\n");
		/*DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
		dynamicViewEntity.addMemberEntity("PT", "Product");
		dynamicViewEntity.addAlias("PT", "productId");
		dynamicViewEntity.addAlias("PT", "brandName");
		dynamicViewEntity.addAlias("PT", "primaryProductCategoryId");
		dynamicViewEntity.addAlias("PT", "productName");
		dynamicViewEntity.addAlias("PT", "productTypeId");
		
		dynamicViewEntity.addMemberEntity("PFL", "ProductFacilityLocation");
		dynamicViewEntity.addAlias("PFL", "productId");
		dynamicViewEntity.addAlias("PFL", "locationSeqId");
		dynamicViewEntity.addViewLink("PT", "PFL", Boolean.TRUE,
				ModelKeyMap.makeKeyMapList("productId")); 
		dynamicViewEntity.addMemberEntity("FL", "FacilityLocation");
		dynamicViewEntity.addAlias("FL", "locationSeqId");
		dynamicViewEntity.addAlias("FL", "aisleId");
		dynamicViewEntity.addViewLink("PFL", "FL", Boolean.TRUE,
				ModelKeyMap.makeKeyMapList("locationSeqId")); */
		
		List<EntityCondition> productCondn=new ArrayList<EntityCondition>();
		productCondn.add(EntityCondition.makeCondition("productId", EntityOperator.IN,revenue));
		productCondn.add(EntityCondition.makeCondition("productTypeId", EntityOperator.NOT_EQUAL,"DIGITAL_GOOD"));
		List orderBy1 = UtilMisc.toList("locationSeqId");
		/*TransactionUtil.begin();
		EntityListIterator eli = delegator.findListIteratorByCondition(
				dynamicViewEntity, EntityCondition.makeCondition(productCondn,EntityOperator.AND), null, null, orderBy1, null);
		
		List<GenericValue> productList = eli.getCompleteList();
		
		
		eli.close();
		TransactionUtil.commit();*/
		 List<GenericValue> productList = delegator.findByCondition("ProductFacilityLocationandProduct",EntityCondition.makeCondition(productCondn, EntityOperator.AND),null,orderBy1 );
		 
	
		 if(UtilValidate.isNotEmpty(productList)) {
			int slNumber = 0;
			
			double total = 0;
			double totalquantitySold = 0 ;
			double totalAmount = 0 ;
			double totalQOH = 0 ;
			double totalToPurchased=0;
			double MRPPrice=0;
			double purchaseItem=0;
			for(GenericValue productGv:productList) {
				double subTotal = 0;
				double quantitySold = 0 ;
			
				GenericValue gvtem=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId", productGv.getString("primaryProductCategoryId")));
				if(UtilValidate.isNotEmpty(productGv.getString("locationSeqId")) && UtilValidate.isNotEmpty(productGv.getString("facilityId")))
				{
					GenericValue ailseTem=delegator.findByPrimaryKey("FacilityLocation", UtilMisc.toMap("locationSeqId", productGv.getString("locationSeqId"),"facilityId", productGv.getString("facilityId")));
					
					if(UtilValidate.isNotEmpty(ailseTem.getString("aisleId")))
					{
						data.append("\""+ailseTem.getString("aisleId")+"\"");
						data.append(",");
					}
				else
				data.append(",");
				}
				else
					data.append(",");
				if(UtilValidate.isNotEmpty(gvtem))
				{
				data.append("\""+gvtem.getString("categoryName")+"\"");
				data.append(",");
				}
				else
					data.append(",");
				data.append("\""+productGv.getString("productId")+"\"");
				data.append(",");
				
				String productName = productGv.getString("productName");
				if(productName != null)
					productName = productName.replaceAll(",", "&");
				if(productGv.getString("brandName")!=null)
					productName=productGv.getString("brandName")+" "+productName;
				data.append("\""+productName+"\"");
				data.append(",");

				
				List<GenericValue> orders =EntityUtil.filterByAnd(revenues, UtilMisc.toMap("productId", productGv.getString("productId")));
					for(int i=0;i<orders.size();i++){

						GenericValue  OrderGV = (GenericValue)orders.get(i);
						quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
						subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
						MRPPrice=getOrderProductMRPPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
					}
					
					Map resultOutput=new HashMap();
					resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", UtilMisc.toMap("productId", productGv.getString("productId"), "facilityId", facilityId));
					//List<EntityCondition> condnList=FastList.newInstance();
					List<EntityCondition> condnList=new ArrayList<EntityCondition>();
					List orderBy = UtilMisc.toList("-lastUpdatedStamp");
					condnList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, productGv.getString("productId")));
					condnList.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, facilityId));
					List<GenericValue> unitpriceList=delegator.findByCondition("InventoryItem",EntityCondition.makeCondition(condnList,EntityOperator.AND) , UtilMisc.toSet("unitCost","vatPercentage"), orderBy);
					
					data.append("\""+quantitySold+"\"");
					data.append(",");
					
				if(UtilValidate.isNotEmpty(resultOutput) && UtilValidate.isNotEmpty(resultOutput.get("quantityOnHandTotal")))
				{
				data.append("\""+resultOutput.get("quantityOnHandTotal")+"\"");
				data.append(",");
				totalQOH=totalQOH+((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
				}
				else
					data.append(",");
				
				if(UtilValidate.isNotEmpty(resultOutput))
				{
					if(((BigDecimal)resultOutput.get("quantityOnHandTotal")).doubleValue()-quantitySold<=0)
					{
						purchaseItem=quantitySold-((BigDecimal) resultOutput.get("quantityOnHandTotal")).doubleValue();
					data.append("\""+purchaseItem+"\"");
					data.append(",");	
					totalToPurchased=totalToPurchased+purchaseItem;
					}
					else
						data.append(",");	
				}
					
				else
					data.append(",");	
				data.append("\""+MRPPrice+"\"");
				data.append(",");	
				/*if(((BigDecimal)resultOutput.get("quantityOnHandTotal")).doubleValue()-quantitySold<=0 && UtilValidate.isNotEmpty(resultOutput) )
				
				{
					data.append("\""+purchaseItem*MRPPrice+"\"");
					data.append(",");	
				}
				else
					data.append(",");	*/
				
					
				data.append("\n");
				
				//total = total + subTotal;
				totalquantitySold = totalquantitySold + quantitySold;
				
				}
				data.append("\n");
				data.append("\n");
				
				data.append(" Total,,,,"+totalquantitySold+","+totalQOH+","+totalToPurchased);
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

public static Map orederEmailReport(GenericDelegator delegator, LocalDispatcher dispatcher, String productStoreId, List slotIds){
	
	//String slotId = request.getParameter("slotId");
	List conditionList = new ArrayList();
	conditionList.add(EntityCondition.makeCondition("deliveryDate",
			EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
	conditionList.add(EntityCondition.makeCondition("deliveryDate",
			EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())));
	if(UtilValidate.isNotEmpty(slotIds))
		conditionList.add(EntityCondition.makeCondition("slot",EntityOperator.IN,slotIds));
	
	List<GenericValue> orders = null;
	try {
		orders = delegator.findList("OrderHeader", EntityCondition.makeCondition(conditionList, EntityOperator.AND), 
																							null, null, null, false);
	} catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	int ordersApproved;
	BigDecimal ordersApprovedTotalAmt;
	int ordersDispatched;
	BigDecimal ordersDispatchedTotalAmt;
	
	Map orderDetailSlotWise = null;
	Map orderDetail = new HashMap();
	
	if(UtilValidate.isNotEmpty(orders))
	{
		List<GenericValue> oldOrders = orders;
		
		List<GenericValue> orderslotTypes= CheckOutEvents.getAllSlots(delegator);
		if(UtilValidate.isNotEmpty(orderslotTypes)){
			String slot = null;
			String slotTiming = null;
			for(GenericValue orderslotType : orderslotTypes){
				slot = orderslotType.getString("slotType");
				slotTiming = orderslotType.getString("slotTiming");
				
				orders = EntityUtil.filterByAnd(oldOrders, UtilMisc.toMap("slot",slot));
				orders = EntityUtil.filterByAnd(orders, UtilMisc.toMap("statusId","ORDER_APPROVED"));
				
				ordersApproved = 0;
				ordersApprovedTotalAmt = BigDecimal.ZERO;
				ordersDispatched = 0;
				ordersDispatchedTotalAmt = BigDecimal.ZERO;
				
				if(UtilValidate.isNotEmpty(orders)){
					ordersApproved = orders.size();
					for(GenericValue order : orders)
					{
						BigDecimal amount = order.getBigDecimal("grandTotal");
						if(UtilValidate.isNotEmpty(amount))
							ordersApprovedTotalAmt = ordersApprovedTotalAmt.add(amount);
					}
				}
				orders = EntityUtil.filterByAnd(orders, UtilMisc.toMap("statusId","ORDER_DISPATCHED"));
				if(UtilValidate.isNotEmpty(orders)){
					ordersDispatched = orders.size();
					for(GenericValue order : orders)
					{
						BigDecimal amount = order.getBigDecimal("grandTotal");
						if(UtilValidate.isNotEmpty(amount))
							ordersDispatchedTotalAmt = ordersDispatchedTotalAmt.add(amount);
					}
				}
				
				orderDetailSlotWise = new HashMap();
				
				orderDetailSlotWise.put("slotTiming", slotTiming);
				orderDetailSlotWise.put("noOfApprovedOrders", ordersApproved);
				orderDetailSlotWise.put("ordersApprovedTotalAmt", ordersApprovedTotalAmt);
				orderDetailSlotWise.put("noOfDispatchedOrders", ordersDispatched);
				orderDetailSlotWise.put("ordersDispatchedTotalAmt", ordersDispatchedTotalAmt);
				orderDetailSlotWise.put("noOfOrders", ordersApproved+ordersDispatched);
				orderDetailSlotWise.put("orderTotal", ordersApprovedTotalAmt.add(ordersDispatchedTotalAmt).setScale(2));
				
				orderDetail.put(slot, orderDetailSlotWise);
			}
		}
	}
	Map orderMapDetail = new HashMap();
	orderMapDetail.put("productStoreId", productStoreId);
	orderMapDetail.put("orderDetail", orderDetail);
	
	return orderDetail;
}

public static String orederEmailReport(HttpServletRequest request, HttpServletResponse response){
	
	delegator = (GenericDelegator)request.getAttribute("delegator");
	dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
	
	String slotIds[] = request.getParameterValues("slotId");
	sendEmail(ProductStoreWorker.getProductStoreId(request), slotIds);
	
	return "success";
}

	public static void sendEmail(String productStoreId, String slotIds[]){
	
	if(UtilValidate.isEmpty(productStoreId)) productStoreId = "9000";
	
	Map results = ServiceUtil.returnSuccess();
	String emailType = "ORDER_EMAIL_REPORT";
    String defaultScreenLocation = "component://ecommerce/widget/ecomclone/EmailOrderScreens.xml#orederEmailReport";
    
        String sendTo = null;
        // get the ProductStore email settings
        GenericValue productStoreEmail = null;
        try {
            productStoreEmail = delegator.findOne("ProductStoreEmailSetting", false, "productStoreId", productStoreId, "emailType", emailType);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ProductStoreEmailSetting", module);
        }

        String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
        if (UtilValidate.isEmpty(bodyScreenLocation)) {
            bodyScreenLocation = defaultScreenLocation;
        }
		        
        //Map<String, Object> serviceContext = FastMap.newInstance();
        Map<String, Object> serviceContext = new HashMap<String, Object>();
        
        serviceContext.put("bodyScreenUri", bodyScreenLocation);
        
        Map bodyParameters = new HashMap();
        bodyParameters.put("productStoreId", productStoreId);
        
        if(UtilValidate.isNotEmpty(slotIds))
        	bodyParameters.put("slotIds", Arrays.asList(slotIds));
        else
        	bodyParameters.put("slotIds", new ArrayList());
        
        serviceContext.put("bodyParameters", bodyParameters);
        
        serviceContext.put("subject", productStoreEmail.getString("subject"));
        serviceContext.put("sendFrom", productStoreEmail.get("fromAddress"));
        serviceContext.put("sendCc", productStoreEmail.get("ccAddress"));
        serviceContext.put("contentType", productStoreEmail.get("contentType"));
        serviceContext.put("sendTo", productStoreEmail.get("bccAddress"));

        try {
            Map<String, Object> result = dispatcher.runSync("sendMailFromScreen", serviceContext);
            if (ModelService.RESPOND_ERROR.equals((String) result.get(ModelService.RESPONSE_MESSAGE))) {
                Map<String, Object> messageMap = UtilMisc.toMap("errorMessage", result.get(ModelService.ERROR_MESSAGE));
            }
        } catch (GenericServiceException e) {
        	Debug.logError(e, "Problem in sending mail for orderEmailReport ", module);
        }
}
	public static String comparativeProductReportsExcel(HttpServletRequest request, HttpServletResponse response){
		delegator = (GenericDelegator)request.getAttribute("delegator");
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		String fromDate = request.getParameter("fromDate");
		String fromThruDate = request.getParameter("fromThruDate");
		String thruFromDate = request.getParameter("thruFromDate");
		String thruDate = request.getParameter("thruDate");
		
		Timestamp nowTimeStamp = null;
		Timestamp from_date = null;
		Timestamp from_thru_date = null;
		Timestamp thru_from_date = null;
		Timestamp thru_date = null;
		
		if(UtilValidate.isEmpty(fromDate))
			from_date = UtilDateTime.getDayStart(nowTimeStamp);
		else
			from_date = Timestamp.valueOf(fromDate);
		
		if(UtilValidate.isEmpty(fromThruDate))
			from_thru_date = UtilDateTime.getDayEnd(nowTimeStamp);
		else
			from_thru_date = Timestamp.valueOf(fromThruDate);
		
		if(UtilValidate.isEmpty(thruFromDate))
			thru_from_date = UtilDateTime.getDayStart(nowTimeStamp);
		else
			thru_from_date = Timestamp.valueOf(thruFromDate);
		
		if(UtilValidate.isEmpty(thruDate))
			thru_date = UtilDateTime.getDayStart(nowTimeStamp);
		else
			thru_date = Timestamp.valueOf(thruDate);
		
		Map comparative1 = null;
		Map comparative2 = null;
		
		try {
			comparative1 = comparativeProductReportsExcel(request, from_date, from_thru_date);
			comparative2 = comparativeProductReportsExcel(request, thru_from_date, thru_date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<String> keys = new HashSet<String>();
		if(UtilValidate.isNotEmpty(comparative1))
			keys = comparative1.keySet();
		if(UtilValidate.isNotEmpty(comparative2))
		if(UtilValidate.isEmpty(keys))
			keys = comparative2.keySet();
		else
		{
			Set<String> keys2 = comparative2.keySet();
			 List listOfKeys = new ArrayList(keys);
			 List listOfKeys2 = new ArrayList(keys2);
			
			 listOfKeys.addAll(listOfKeys2);
			 
			 keys = new HashSet(listOfKeys);
			//keys.addAll(keys2);
		}
		System.out.println("   keys    "+keys);
		try{
		 response.setContentType("application/excel");
		 response.setHeader("Content-disposition","attachment;filename=ComparativeReport.xls");
		 WorkbookSettings wbSettings = new WorkbookSettings();
		 WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
		 WritableSheet sheet=workbook.createSheet("mysheet", 0); 
		 WritableFont wfobj=new WritableFont(WritableFont.ARIAL,8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
		 WritableCellFormat cfobj=new WritableCellFormat(wfobj);
		 cfobj.setAlignment(Alignment.CENTRE);
		 cfobj.setBackground(Colour.PINK);
		 cfobj.setWrap(true);
		 
		 Label label=new Label(1,1,"Product Id"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(2,1,"Brand Name"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);  
		 label=new Label(3,1,"Product Name"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(4,1,"Quantity"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);  
		 label=new Label(5,1,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);  
		 
		 sheet.mergeCells(4,1,5,1);
		 
		 label=new Label(6,1,"Sales Value"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(7,1,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 sheet.mergeCells(6,1,7,1);
		 label=new Label(8,1,"Cost");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(9,1,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 sheet.mergeCells(8,1,9,1);
		 label=new Label(10,1,"Margin");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(11,1,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 sheet.mergeCells(10,1,11,1);
		 
		 label=new Label(1,2,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);                                      
		 label=new Label(2,2,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);  
		 label=new Label(3,2,""); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(4,2,"Compare 1");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(5,2,"Compare 2");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 
		 label=new Label(6,2,"Compare 1"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(7,2,"Compare 2"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 
		 label=new Label(8,2,"Compare 1");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(9,2,"Compare 2"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 
		 label=new Label(10,2,"Compare 1");
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 label=new Label(11,2,"Compare 2"); 
		 label.setCellFormat(cfobj);
		 sheet.addCell(label);
		 
		 int row = 3;
		 for(String key : keys)
		 {
			 Map comp1 = (Map) comparative1.get(key);
			 Map comp2 = (Map) comparative2.get(key);
			 
			 String productId = key;
			 String brandName = null;
			 String internalName = null;
			 String quantity1 = null;
			 String quantity2 = null;
			 BigDecimal  salesValue1 = null;
			 BigDecimal salesValue2 = null;
			 BigDecimal cost1 = null;
			 BigDecimal cost2 = null;
			 BigDecimal margin1 = null;
			 BigDecimal margin2 = null;
			 
			 BigDecimal qty1 = null;
			 BigDecimal qty2 = null;
			 if(UtilValidate.isNotEmpty(comp1))
			 {
				 brandName = comp1.get("brandName")+"";
				 internalName = comp1.get("internalName")+"";
				 
				 if(UtilValidate.isNotEmpty(comp1.get("quantity")))
					 qty1 =  (BigDecimal) comp1.get("quantity");
				 
				 if(UtilValidate.isNotEmpty(comp1.get("salingPrice")))
					 salesValue1 = (BigDecimal)comp1.get("salingPrice");
				 
				 if(UtilValidate.isNotEmpty(comp1.get("cost")))
					 cost1 = (BigDecimal)comp1.get("cost");
			 }
			 
			 if(UtilValidate.isEmpty(qty1))quantity1 = "0";
			 else
				 quantity1 = qty1+"";
			 
			 if(UtilValidate.isEmpty(salesValue1))salesValue1 =BigDecimal.ZERO;
			 if(UtilValidate.isEmpty(cost1))cost1 = BigDecimal.ZERO;
			 
			 if(UtilValidate.isNotEmpty(comp2))
			 {
				 if(UtilValidate.isEmpty(brandName))
					 brandName = comp2.get("brandName")+"";
				 if(UtilValidate.isEmpty(internalName))
					 internalName = comp2.get("internalName")+"";
				 
				 if(UtilValidate.isNotEmpty(comp2.get("quantity")))
					 qty2 = (BigDecimal)comp2.get("quantity");
				 
				 if(UtilValidate.isNotEmpty(comp2.get("salingPrice")))
					 salesValue2 = (BigDecimal)comp2.get("salingPrice");
				 
				 if(UtilValidate.isNotEmpty(comp2.get("cost")))
					 cost2 = (BigDecimal)comp2.get("cost");
			 }
			 
			 if(UtilValidate.isEmpty(qty2))quantity2 = "0";
			 else
				 quantity2 = qty2+"";
			 if(UtilValidate.isEmpty(salesValue2))salesValue2 = BigDecimal.ZERO;
			 if(UtilValidate.isEmpty(cost2))cost2 = BigDecimal.ZERO;
			 
			 
			margin1 = cost1.subtract(salesValue1);
			margin2 = cost2.subtract(salesValue2); 
				
			 label=new Label(1,row,productId); 
			 sheet.addCell(label);                                      
			 label=new Label(2,row,brandName); 
			 sheet.addCell(label);  
			 label=new Label(3,row,internalName); 
			 sheet.addCell(label);
			 label=new Label(4,row,quantity1+""); 
			 sheet.addCell(label);  
			 label=new Label(5,row,quantity2+""); 
			 sheet.addCell(label);  
			 
			 label=new Label(6,row,salesValue1+""); 
			 sheet.addCell(label);
			 label=new Label(7,row,salesValue2+""); 
			 sheet.addCell(label);
			 
			 label=new Label(8,row,cost1+"");
			 sheet.addCell(label);
			 label=new Label(9,row,cost2+""); 
			 sheet.addCell(label);
			 
			 label=new Label(10,row,margin1+"");
			 sheet.addCell(label);
			 label=new Label(11,row,margin2+""); 
			 sheet.addCell(label);
			 
			 row++;
			 
		 }
		 	workbook.write();
			workbook.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "success";
	}
	
	public static Map comparativeProductReportsExcel(HttpServletRequest request, Timestamp startDate, Timestamp endDate) throws Exception{
		String compareBy = request.getParameter("compareBy");
		String productCategoryId = request.getParameter("productCategoryId");
		String productId = request.getParameter("productId");
		String brandName = request.getParameter("brandName");
		
		
		//List<GenericValue> productList = ProductWorker.getProductList(delegator, productCategoryId, productId, brandName);
		
		DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
		dynamicViewEntity.addMemberEntity("OH", "OrderHeader");
		dynamicViewEntity.addAlias("OH", "orderId");
		dynamicViewEntity.addAlias("OH", "orderDate");
		dynamicViewEntity.addAlias("OH", "statusId");
		dynamicViewEntity.addAlias("OH", "orderTypeId");
		dynamicViewEntity.addAlias("OH", "createdBy");
		dynamicViewEntity.addAlias("OH", "slot");
		
		dynamicViewEntity.addMemberEntity("OI", "OrderItem");
		dynamicViewEntity.addAlias("OI", "orderId");
		dynamicViewEntity.addAlias("OI", "productId");
		//dynamicViewEntity.addAlias("OI", "OIStatusId", null, "statusId", false, false, null);
		dynamicViewEntity.addAlias("OI", "itemDescription");
		dynamicViewEntity.addAlias("OI", "orderItemSeqId");
		dynamicViewEntity.addAlias("OI", "productCategoryId");
		dynamicViewEntity.addAlias("OI", "unitListPrice");
		dynamicViewEntity.addAlias("OI", "unitPrice");
		dynamicViewEntity.addViewLink("OH", "OI", Boolean.FALSE,
				ModelKeyMap.makeKeyMapList("orderId"));
		
		dynamicViewEntity.addMemberEntity("PT", "Product");
		dynamicViewEntity.addAlias("PT", "productId");
		dynamicViewEntity.addAlias("PT", "brandName");
		dynamicViewEntity.addAlias("PT", "internalName");
		dynamicViewEntity.addViewLink("OI", "PT", Boolean.FALSE,
				ModelKeyMap.makeKeyMapList("productId")); 
		
		List<EntityCondition> mainExprs=new ArrayList<EntityCondition>(); 
		mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
		
		
		if(UtilValidate.isNotEmpty(startDate))
		    mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
		if(UtilValidate.isNotEmpty(endDate))
			mainExprs.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		
		if(UtilValidate.isNotEmpty(productId))
			mainExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		if(UtilValidate.isNotEmpty(productCategoryId))
			mainExprs.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, productCategoryId));
		if(UtilValidate.isNotEmpty(brandName))
			mainExprs.add(EntityCondition.makeCondition("brandName", EntityOperator.EQUALS, brandName));
		
		mainExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
		//mainExprs.add(EntityCondition.makeCondition("OIStatusId", EntityOperator.EQUALS, "ITEM_APPROVED"));
		
		TransactionUtil.begin();
		EntityListIterator eli = delegator.findListIteratorByCondition(
				dynamicViewEntity, EntityCondition.makeCondition(mainExprs,EntityOperator.AND), null, null, null, null);
		
		
		List<GenericValue> orderList = eli.getCompleteList();
//		System.out.println("\n\n\n orderList"+mainExprs);
//		System.out.println("\n\n\n orderList"+orderList);
//		System.out.println("\n\n\n orderList"+orderList.size());
		eli.close();
		TransactionUtil.commit();
		productId = null;
		brandName = null;
		String internalName = null;
		BigDecimal quantity = null;
		BigDecimal cost = null;
		BigDecimal salingPrice =  BigDecimal.ZERO;
		
		Map report = new HashMap();
		Map reportDetail = null;
		System.out.println("   orderList      "+orderList);
		for(GenericValue salesList:orderList)
		{
			productId = salesList.getString("productId");
			brandName = salesList.getString("brandName");
			internalName = salesList.getString("internalName");
			
			if(UtilValidate.isNotEmpty(salesList.getString("productCategoryId")))
			{
				GenericValue gv=delegator.findByPrimaryKey("ProductCategory", UtilMisc.toMap("productCategoryId",salesList.getString("productCategoryId")));
			}
		
			List<EntityCondition> condn=new ArrayList<EntityCondition>();
			
			condn.add(EntityCondition.makeCondition("quantityOnHandDiff", EntityOperator.LESS_THAN, new BigDecimal(0)));
			condn.add(EntityCondition.makeCondition("availableToPromiseDiff", EntityOperator.EQUALS, new BigDecimal(0)));
			condn.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, salesList.getString("orderId")));
			condn.add(EntityCondition.makeCondition("orderItemSeqId", EntityOperator.EQUALS, salesList.getString("orderItemSeqId")));
			List<GenericValue> quantityList=delegator.findList("InventoryItemDetail", EntityCondition.makeCondition(condn,EntityOperator.AND), UtilMisc.toSet("inventoryItemId","quantityOnHandDiff"), null, null, false);
			BigDecimal totalQty=BigDecimal.ZERO;
			BigDecimal totalUnitCost=BigDecimal.ZERO;
			for(GenericValue quantityListTemp:quantityList)
			{
				BigDecimal qty=BigDecimal.ZERO;
				BigDecimal price=BigDecimal.ZERO;
				qty=quantityListTemp.getBigDecimal("quantityOnHandDiff").multiply(new BigDecimal(-1));
				totalQty=totalQty.add(qty);
				GenericValue unitList=delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId",quantityListTemp.getString("inventoryItemId")));
				if(UtilValidate.isNotEmpty(unitList))
				{
					if(UtilValidate.isNotEmpty(unitList.getDouble("vatPercentage")))
						price=unitList.getBigDecimal("unitCost").add(unitList.getBigDecimal("unitCost").multiply((unitList.getBigDecimal("vatPercentage").divide(new BigDecimal(100)))));
					else
						price=unitList.getBigDecimal("unitCost");
					
					totalUnitCost=totalUnitCost.add((price.multiply(qty))).setScale(2, RoundingMode.CEILING);
				}
			}
			quantity = totalQty;
			cost = totalUnitCost;
			
			BigDecimal  grossTotal= BigDecimal.ZERO;
			if(UtilValidate.isNotEmpty(salesList.getString("unitPrice")))
			{
				if(salesList.getDouble("unitListPrice")>0.0)
				{
					grossTotal= salesList.getBigDecimal("unitListPrice").multiply(totalQty);
				}
				else
				{
 
					grossTotal=salesList.getBigDecimal("unitPrice").multiply(totalQty);
					grossTotal = grossTotal.negate();
				}
			}
			salingPrice = grossTotal;
 			
			if(UtilValidate.isEmpty(report.get(productId)))
			{
				reportDetail = new HashMap();
				reportDetail.put("productId",productId);
				reportDetail.put("brandName",brandName);
				reportDetail.put("internalName",internalName);
				reportDetail.put("quantity",quantity);
				reportDetail.put("cost",cost);
				reportDetail.put("salingPrice",salingPrice);
				
				report.put(productId, reportDetail);
			}else
			{
				reportDetail = (Map)report.get(productId);
				
				BigDecimal qty =  (BigDecimal) reportDetail.get("quantity");
				BigDecimal Cost =  (BigDecimal) reportDetail.get("cost");
				BigDecimal sellingPrc = (BigDecimal)reportDetail.get("salingPrice");
				
				if(UtilValidate.isNotEmpty(qty))
					reportDetail.put("quantity",qty.add(quantity));
				
				if(UtilValidate.isNotEmpty(cost))
					reportDetail.put("cost",Cost.add(cost));
				
				if(UtilValidate.isNotEmpty(salingPrice))
					reportDetail.put("salingPrice",sellingPrc.add(salingPrice));
				
				report.put(productId, reportDetail);
			}
		}
		return report;
	}
	
	public static String offersDiscountsReportsExcel(HttpServletRequest request, HttpServletResponse response){
		delegator = (GenericDelegator)request.getAttribute("delegator");
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String offer = request.getParameter("offerTypes");
		
		Timestamp nowTimeStamp = null;
		Timestamp startDate = null;
		Timestamp endDate = null;
		
		if(UtilValidate.isEmpty(fromDate))
			startDate = UtilDateTime.getDayStart(nowTimeStamp);
		else
			startDate = Timestamp.valueOf(fromDate);
		
		if(UtilValidate.isEmpty(thruDate))
			endDate = UtilDateTime.getDayStart(nowTimeStamp);
		else
			endDate = Timestamp.valueOf(thruDate);
		
		List mainExprs = new ArrayList();
		mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
		
		if(UtilValidate.isNotEmpty(startDate))
		    mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));
		if(UtilValidate.isNotEmpty(endDate))
			mainExprs.add(EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		
		mainExprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
		
		try{
			List<GenericValue> orders = delegator.findList("OrderHeader", EntityCondition.makeCondition(mainExprs, EntityOperator.AND), null, null, null, false);
			
			 response.setContentType("application/excel");
			 response.setHeader("Content-disposition","attachment;filename=offersAndDiscounts.xls");
			 WorkbookSettings wbSettings = new WorkbookSettings();
			 WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
			 WritableSheet sheet=workbook.createSheet("mysheet", 0); 
			 WritableFont wfobj=new WritableFont(WritableFont.ARIAL,8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			 WritableCellFormat cfobj=new WritableCellFormat(wfobj);
			 cfobj.setAlignment(Alignment.CENTRE);
			 cfobj.setBackground(Colour.PINK);
			 cfobj.setWrap(true);
			 
			 Label label=new Label(1,1,"Order Id"); 
			 label.setCellFormat(cfobj);
			 sheet.addCell(label);
			 label=new Label(2,1,"Grand Total"); 
			 label.setCellFormat(cfobj);
			 sheet.addCell(label);  
			 label=new Label(3,1,"Offer Total"); 
			 label.setCellFormat(cfobj);
			 sheet.addCell(label);
			 label=new Label(4,1,"Gross Total"); 
			 label.setCellFormat(cfobj);
			 sheet.addCell(label);  
			
			 int row = 3;
			OrderReadHelper orh = null;
			BigDecimal totalOffer = null;
			
			BigDecimal totalOrderAmt = BigDecimal.ZERO;
			BigDecimal totalOfferAmt = BigDecimal.ZERO;
			
			if(UtilValidate.isNotEmpty(orders))
			for(GenericValue order : orders)
			{
				orh = new OrderReadHelper(order);
				BigDecimal grandTotal = orh.getOrderGrandTotal();
				
				mainExprs=new ArrayList(); 
				if(UtilValidate.isNotEmpty(offer))
				{
					String offers[] = offer.split(",");
					mainExprs.add(EntityCondition.makeCondition("orderAdjustmentTypeId", EntityOperator.IN, Arrays.asList(offers)));
				}
				
				mainExprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orh.getOrderId()));
				List<GenericValue> orderAdjustments = delegator.findList("OrderAdjustment", EntityCondition.makeCondition(mainExprs, EntityOperator.AND), null, null, null, false);
				
				totalOffer = BigDecimal.ZERO;
				if(UtilValidate.isNotEmpty(orderAdjustments))
				for(GenericValue orderAdjustment : orderAdjustments)
				{
					if(UtilValidate.isNotEmpty(orderAdjustment.getBigDecimal("amount")))
						totalOffer = totalOffer.add(orderAdjustment.getBigDecimal("amount"));
				}
				
				 label=new Label(1,row,orh.getOrderId()); 
				 sheet.addCell(label);                                      
				 label=new Label(2,row,grandTotal+""); 
				 sheet.addCell(label);  
				 label=new Label(3,row,totalOffer+""); 
				 sheet.addCell(label);
				 label=new Label(4,row,grandTotal.add(totalOffer)+""); 
				 sheet.addCell(label);
				 
				 totalOrderAmt = totalOrderAmt.add(grandTotal);
				 totalOfferAmt = totalOfferAmt.add(totalOffer);
				
				 row++;
			}
			row++;
			 label=new Label(1,row,"Total"); 
			 sheet.addCell(label);                                      
			 label=new Label(2,row,totalOrderAmt+""); 
			 sheet.addCell(label);  
			 label=new Label(3,row,totalOfferAmt+""); 
			 sheet.addCell(label);
			 label=new Label(4,row,totalOrderAmt.add(totalOfferAmt)+""); 
			 sheet.addCell(label);
			
		 	workbook.write();
			workbook.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return "success";
	}
	
}
