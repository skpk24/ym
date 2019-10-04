package org.ofbiz.order.report;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;
import org.ofbiz.base.util.UtilDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;

import java.util.List;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
//import javolution.util.FastList;
//import javolution.util.FastMap;
import jxl.Workbook;
import jxl.WorkbookSettings;

import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.VerticalAlignment;
 
import javax.servlet.http.HttpSession;
import javax.swing.JFrame;
 
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import org.ofbiz.entity.util.EntityListIterator;

public class Report {

	    public static final String module = Report.class.getName();

		protected static LocalDispatcher dispatcher = null;
		public static String productStoreId = null;
		private static GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");

		private static GenericValue GenericValue;
	
		public static  String genSalesreportCSV(HttpServletRequest request, HttpServletResponse response){
			try{
				delegator = (GenericDelegator)request.getAttribute("delegator");
				
				//String fromDate = request.getParameter("minDate");
				//String thruDate  = request.getParameter("maxDate");
				String orderStatusID = request.getParameter("orderStatusId");
				String pmtType = request.getParameter("paymentMethodTypeId");
				String productId = request.getParameter("productId");

				//Debug.log("#### fromDate : " + fromDate);
				//Debug.log("#### thruDate : " + thruDate);
				
				List mainExprs = new ArrayList();
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				List prodCond = new ArrayList();
				if(productId != null){
					if(productId.length() != 0)
				prodCond.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
				}
				EntityConditionList prodTypeCondition = null;
				if(prodCond.size()>0){
				prodTypeCondition = EntityCondition.makeCondition(prodCond, EntityOperator.OR);
				mainExprs.add(prodTypeCondition);
				}
				
				
				List isOrderCompleted = new ArrayList();
				if(orderStatusID != null && orderStatusID.length() > 0  ){
					Debug.log("#### orderStatusID provided: " + orderStatusID);
					isOrderCompleted.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, orderStatusID));
					EntityConditionList isOrderCompletedCondition = EntityCondition.makeCondition(isOrderCompleted, EntityOperator.OR);
					mainExprs.add(isOrderCompletedCondition);
				}
				if(pmtType != null && pmtType.length() > 0  ){
					Debug.log("#### pmtType provided: " + pmtType);
					isOrderCompleted.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, pmtType));
					EntityConditionList isOrderCompletedCondition = EntityCondition.makeCondition(isOrderCompleted, EntityOperator.OR);
					mainExprs.add(isOrderCompletedCondition);
				}

				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				List storeCondtionList = new ArrayList();
				EntityConditionList storeCondition = null;
				if(productStoreId != null){
					storeCondtionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					storeCondition = EntityCondition.makeCondition(storeCondtionList, EntityOperator.AND);
					mainExprs.add(storeCondition);
				}
				
				//List dateCondiList = new ArrayList();
				//dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				//dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				//EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				//mainExprs.add(dateCondition);
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);

				Debug.log("#### sales data : main condition : " + mainCondition);
				
				List sales = delegator.findByCondition("MyOrders",mainCondition,null,UtilMisc.toList("orderDate DESC") );
				Iterator itr = sales.iterator();
				while(itr.hasNext()){
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					String emailId = org.ofbiz.party.contact.ContactMechWorker.getOrderEmailContactMech( delegator,  orderId);
					if(emailId != null)
						genVal.set("createdBy",emailId);
					else
						genVal.set("createdBy","");
					
				}				
				Debug.log("#### \n\nSales list size : " + sales.size());
				
				//response.setContentType("application/octet-stream");
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=SalesReport.csv");
				StringBuffer data = new StringBuffer();
				data.append(",,,,,SALES REPORT");
				
				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				if(productStoreId != null){
					data.append("product Store Id : "+productStoreId);
					data.append("\n");
				}
				data.append("Report : Sales Report");
				data.append("\n");
				//data.append("Date Range  : " + fromDate.substring(0,fromDate.lastIndexOf(" ")) + " To " +  thruDate.substring(0,thruDate.lastIndexOf(" ")));
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append("Order Id, Order status, Party Id ,Email Id, Product Id, Product Title , Product Quantity, Price, Discount Price , Sub Total, Date");				
				data.append("\n");
				data.append("\n");
				
				if(sales != null && sales.size() >0){

					for(int i=0;i<sales.size();i++){
						GenericValue orderRoleGv = null;
						GenericValue saleGV = (GenericValue)sales.get(i);
						if(saleGV != null){
							List orderRoleListGv = delegator.findByAnd("OrderRole",UtilMisc.toMap("orderId",saleGV.getString("orderId")));
							if(orderRoleListGv != null && orderRoleListGv.size()>0){
								orderRoleGv = (GenericValue) orderRoleListGv.get(0);
							}
							GenericValue visitGv = delegator.findByPrimaryKey("Visit",UtilMisc.toMap("visitId",saleGV.getString("visitId")));
							data.append("\""+saleGV.getString("orderId")+"\""+",");
							
					        GenericValue statusItem = null;
					        try {
					            statusItem = delegator.findByPrimaryKey("StatusItem",UtilMisc.toMap("statusId",saleGV.getString("orderStatusId")));
					        } catch (GenericEntityException e) {
					            Debug.logError(e, module);
					        }
							
					        if(statusItem != null)
					        	data.append(statusItem.getString("description")+",");
					        else
							data.append(saleGV.getString("orderStatusId")+",");
							
							if(orderRoleGv != null)
								data.append("\""+orderRoleGv.getString("partyId")+"\""+",");
							else
								data.append(""+",");
							
							data.append(saleGV.getString("createdBy")+",");
							data.append("\""+saleGV.getString("productId")+"\""+",");
	
							double quantity =  Double.parseDouble(saleGV.getString("quantity"));
							String productName = (String) saleGV.getString("productName");
							if(productName != null)
							productName  = productName.replaceAll(",", " ");
							else
								productName = "";
							data.append(productName+",");
							
							data.append(saleGV.getString("quantity")+",");
							double unitPrice =  Double.parseDouble(saleGV.getString("unitPrice"));
							data.append(unitPrice*quantity+",");
							
							double	price = getOrderProductPrice(saleGV.getString("productId"),saleGV.getString("orderId"),saleGV.getString("orderItemSeqId"));
							double discAmount = (quantity* unitPrice)-(price);
							data.append(discAmount+",");
							
							//Subtotal
							data.append(price+",");
							data.append(saleGV.getString("orderDate")+",");
							data.append("\n");
						}
					}

				}
							
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				
				Debug.log("####");
				
				}catch(Exception ex){
					ex.printStackTrace();
					Debug.log("#### \n\nException: " + ex.getMessage());
				}
				
				return "success";
		}

		/* check party roles */
		public static boolean hasPartyRoles(String partyId, String roleTypeId, HttpServletRequest request){
			delegator = (GenericDelegator)request.getAttribute("delegator");
			if(partyId == null || roleTypeId == null)return false;
			GenericValue role = null;
			try {
				role = delegator.findByPrimaryKey("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", roleTypeId));
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			if(role != null && role.size() > 0)
				return true;
			else
				return false;
		}
		
		/* Revenue  report */
		public static  String revenueReoprtCSV(HttpServletRequest request, HttpServletResponse response){
			try{
				delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					if(reportType.equalsIgnoreCase("Facility Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");

					}
					if(reportType.equalsIgnoreCase("Daily Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly Sales Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly Sales Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					
					}
					
				}

				List revenues = new ArrayList();
				List parentProducts = new ArrayList();
				
				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				List storeCondtion = new ArrayList();
				if(productStoreId != null)
				storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				
				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
					
				List dateCondiList = new ArrayList();
				if(UtilValidate.isNotEmpty(fromDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if(UtilValidate.isNotEmpty(thruDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				List mainExprs = new ArrayList();
				
				if(storeCondtion.size()>0){
				mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
				}
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));				
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
					mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
				}
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
                 //System.out.println("\n\n\n\n\n"+revenues+"\n\n\n\n\n");
				if(revenues != null){
					for(int i=0;i<revenues.size();i++){
					GenericValue parentProduct =(GenericValue) revenues.get(i);
					String parentProductId = parentProduct.getString("productId");
						if(!parentProducts.contains(parentProductId)){
							parentProducts.add(parentProductId);
						}
						
					}
				}

				response.setContentType("application/excel");
				if(reportType != null)
					response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
				else
				response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
				StringBuffer data = new StringBuffer();

				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				if(productStoreId != null)
					data.append("Product Store Id : "+productStoreId);
				data.append("\n");
				if(reportType != null)
					data.append("Report : "+reportType);
				else
					data.append("Report : Revenue Report");
				data.append("\n");
				
				data.append("Date Range  : " + fromDate + " To " +  thruDate);
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append(" Sl.No , category Name,Product ID, Product Name, Quantity Sold,Total Value Sold");
				data.append("\n");
				data.append("\n");
				
				if(parentProducts!=null && parentProducts.size()>0) {
					int slNumber = 0;
					Iterator itParentProducts = parentProducts.iterator();
					double total = 0;
					double totalquantitySold = 0 ;
					
					while(itParentProducts.hasNext()) {
						
						String parentProductId = (String) itParentProducts.next();
						double subTotal = 0;
						double quantitySold = 0 ;
					
						GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
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
						data.append("\""+parentProductId+"\"");
						data.append(",");
						
						String productName = productGv.getString("productName");
						if(productName != null)
							productName = productName.replaceAll(",", "&");
						data.append("\""+productName+"\"");
						data.append(",");

						
							List condition = new ArrayList();
							condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
							EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

							mainExprs = new ArrayList();
							mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
							mainExprs.add(dateCondition);
							mainExprs.add(orderStatusCondition);
							mainExprs.add(conditionList);
							
							mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
							
							List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
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
						data.append(" Total,,,"+totalquantitySold+","+total);
				}
				
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "success";
		}		

		
		public static  String salesHtml(HttpServletRequest request, HttpServletResponse response){
			try{
				delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					if(reportType.equalsIgnoreCase("Facility Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");

					}
					if(reportType.equalsIgnoreCase("Daily Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly Sales Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly Sales Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					
					}
					
				}

				List revenues = new ArrayList();
				List parentProducts = new ArrayList();
				
				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				List storeCondtion = new ArrayList();
				if(productStoreId != null)
				storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				
				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
					
				List dateCondiList = new ArrayList();
				if(UtilValidate.isNotEmpty(fromDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if(UtilValidate.isNotEmpty(thruDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				List mainExprs = new ArrayList();
				
				if(storeCondtion.size()>0){
				mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
				}
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));				
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
					mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
				}
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
                 
				if(revenues != null){
					for(int i=0;i<revenues.size();i++){
					GenericValue parentProduct =(GenericValue) revenues.get(i);
					String parentProductId = parentProduct.getString("productId");
						if(!parentProducts.contains(parentProductId)){
							parentProducts.add(parentProductId);
						}
						
					}
				}
  
				
				String buffer="";
				PrintWriter out = response.getWriter();
				buffer="<table><tr><td class=label> <span>Sl.No</span></td><td class=label> <span>Product ID</span></td><td class=label> <span>Product Name</span></td><td class=label> <span>Quantity Sold</span></td><td class=label> <span>Total Value Sold</span></td></tr>";
				if(parentProducts!=null && parentProducts.size()>0) {
					int slNumber = 0;
					Iterator itParentProducts = parentProducts.iterator();
					double total = 0;
					double totalquantitySold = 0 ;
					
					while(itParentProducts.hasNext()) {
						
						String parentProductId = (String) itParentProducts.next();
						double subTotal = 0;
						double quantitySold = 0 ;
					
						GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
						if(productGv==null) continue;
						
						slNumber++;
						buffer=buffer+"<tr><td>"+slNumber+"</td>";
						buffer=buffer+"<td>"+parentProductId+"</td>";
						
						String productName = productGv.getString("productName");
						if(productName != null)
							productName = productName.replaceAll(",", "&");
						buffer=buffer+"<td>"+productName+"</td>";

						
							List condition = new ArrayList();
							condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
							EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

							mainExprs = new ArrayList();
							mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
							mainExprs.add(dateCondition);
							mainExprs.add(orderStatusCondition);
							mainExprs.add(conditionList);
							
							mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
							
							List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
							for(int i=0;i<orders.size();i++){

								GenericValue  OrderGV = (GenericValue)orders.get(i);
								quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
								subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
							}
						
							buffer=buffer+"<td>"+quantitySold+"</td>";
							buffer=buffer+"<td>"+subTotal+"</td>";
							
						total = total + subTotal;
						totalquantitySold = totalquantitySold + quantitySold;
						}
					buffer=buffer+"\n";
				}
				
				out.println(buffer);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "success";
		}		
		
		public static  String revenueReoprtSummaryCSV(HttpServletRequest request, HttpServletResponse response){
			try{
				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				
				delegator = (GenericDelegator)request.getAttribute("delegator");

				GregorianCalendar calendarFrom =new  GregorianCalendar();
				GregorianCalendar calendarTo =new  GregorianCalendar();
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				
				Date dateFrom = UtilDateTime.getParsedDate(fromDate, "yyyy-MM-dd HH:mm:ss");
				calendarFrom.setTime(dateFrom);
				Date toFrom = UtilDateTime.getParsedDate(thruDate, "yyyy-MM-dd HH:mm:ss");
				calendarTo.setTime(toFrom);

				Map revenueReport =  revenueReoprtSummary(calendarFrom, calendarTo);

				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=revenueSummaryReport.csv");
				StringBuffer data = new StringBuffer();

				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				if(productStoreId != null){
					data.append("Product Store Id : "+productStoreId);
					data.append("\n");
				}
				data.append("Report : Revenue Summary ");
				data.append("\n");
				data.append("Date Range  : " + fromDate.substring(0,fromDate.lastIndexOf(" ")) + " To " +  thruDate.substring(0,thruDate.lastIndexOf(" ")));
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append(" Date, Quantity Sold, Revenue");
				data.append("\n");
				data.append("\n");

				if(revenueReport != null ){
					List products  = (List)revenueReport.get("products");
					for(int i=0;i<products.size();i++){
						Map productDetails = (Map)products.get(i);
						if(productDetails == null)continue;
						data.append(productDetails.get("date"));
						data.append(",");
						data.append(productDetails.get("quantitySold"));
						data.append(",");
						data.append(productDetails.get("subTotal"));
						data.append(",");
						data.append("\n");
					}
					data.append("Total,"+revenueReport.get("totalQuantitySold")+","+revenueReport.get("total"));
				}

				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}		
		
		public static  Map revenueReoprtSummary(GregorianCalendar fromDate,GregorianCalendar thruDate){
			
			Map revenueReport = new HashMap();

			try{

			int startDay = fromDate.get(Calendar.DAY_OF_YEAR);
			int endDay  =  thruDate.get(Calendar.DAY_OF_YEAR);

			List revenueProducts = new ArrayList();
			double total = 0;
			double totalQuantitySold = 0 ;

			GregorianCalendar fromRange =(GregorianCalendar) fromDate.clone();
			fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) - 1);

			GregorianCalendar toRange = (GregorianCalendar)fromDate.clone();
			toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) - 1);
			////System.out.println("fromDate : " + fromRange.getTime());
			int dif = UtilDateTime.getDaysBetweenDates(fromDate.getTime(), thruDate.getTime());
			if(dif != 0){
				dif--;
			}
			for(int j =0;j<=dif;j++ ){
				Map productDetails = new HashMap();
				fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) + 1);
				fromRange.set(Calendar.HOUR_OF_DAY, 0);
				fromRange.set(GregorianCalendar.MINUTE, 0);
				fromRange.set(GregorianCalendar.SECOND, 0);

				toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) + 1);
				toRange.set(Calendar.HOUR_OF_DAY, 23);
				toRange.set(GregorianCalendar.MINUTE, 59);
				toRange.set(GregorianCalendar.SECOND, 59);

				productDetails.put("date",UtilDateTime.getFormattedDate(fromRange.getTime(),"d MMM yyyy")) ;

				double subTotal = 0;
				double quantitySold = 0 ;
				Timestamp fromRangeTime = new Timestamp(fromRange.getTime().getTime());
				Timestamp toRangeTime = new Timestamp(toRange.getTime().getTime());

				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
					
				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromRangeTime));
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO,toRangeTime ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				List mainExprs = new ArrayList();
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				List storeCondtionList = new ArrayList();
				EntityConditionList storeCondition = null;
				if(productStoreId != null){
					storeCondtionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
					storeCondition = EntityCondition.makeCondition(storeCondtionList, EntityOperator.AND);
					mainExprs.add(storeCondition);
				}				
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				
				List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
				////System.out.println("orders : " + orders.size());
						for(int i=0;i<orders.size();i++){
							GenericValue  orderGV = (GenericValue)orders.get(i);
							quantitySold = quantitySold + (orderGV.getDouble("quantity").doubleValue());
							subTotal = subTotal + getOrderProductPrice(orderGV.getString("productId"),orderGV.getString("orderId"),orderGV.getString("orderItemSeqId"));
						} // end for loop webinars

						total = total + subTotal;
						totalQuantitySold = totalQuantitySold + quantitySold;

						productDetails.put("quantitySold",quantitySold+"");
						productDetails.put("subTotal",subTotal+"");
						revenueProducts.add(productDetails); 
			}// end for loop Dates
					revenueReport.put("products",revenueProducts);
					revenueReport.put("totalQuantitySold",totalQuantitySold+"");
					revenueReport.put("total",total+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return revenueReport;
	}		
		
		
		public static String  revenueMonthlyReportSummaryCSV(HttpServletRequest request, HttpServletResponse response)
		{
		try{

			HttpSession session = request.getSession();
			productStoreId = (String) session.getAttribute("productStoreId");
			
			String fromMonth = request.getParameter("fromMonth");
			int actualFromMonth = Integer.parseInt( fromMonth );
			String thruMonth  = request.getParameter("thruMonth");
			int actualThruMonth = Integer.parseInt( thruMonth );
			
			Debug.logInfo("###### fromMonth "+fromMonth, module);
			Debug.logInfo("###### thruMonth "+thruMonth, module);
			
			String frommonthName=getMonthForInt(actualFromMonth-1);
			String thrumonthName=getMonthForInt(actualThruMonth-1);
			Debug.logInfo("###### frommonthName "+frommonthName, module);
			Debug.logInfo("###### thrumonthName "+thrumonthName, module);
			
			String fromYear  = request.getParameter("fromYear");
			int intfromYear = Integer.parseInt( fromYear );
			
			
			String thruYear  = request.getParameter("thruYear");
			int intthruYear = Integer.parseInt( thruYear );
			
			Debug.logInfo("###### fromYear "+fromYear, module);
			Debug.logInfo("###### thruYear "+thruYear, module);
			String reportType = request.getParameter("reportType");
			delegator = (GenericDelegator)request.getAttribute("delegator");
			response.setContentType("application/excel");
			if(reportType != null)
				response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
			else
			response.setHeader("Content-disposition","attachment;filename=revenueMonthlySummaryReport.csv");
			
			StringBuffer data = new StringBuffer();
			data.append("\n");
			data.append("#--------------------------------------------------------------");
			data.append("\n");
			if(productStoreId != null){
				data.append("Product Store Id : "+ productStoreId);
				data.append("\n");
			}
			if(reportType != null)
				data.append("Report : "+reportType);
			else
				data.append("Report : Revenue Monthly Summary ");
			data.append("\n");
			data.append("Month Range  : " + frommonthName+" "+fromYear + " To " + thrumonthName+" "+thruYear  );
			data.append("\n");
			data.append("#--------------------------------------------------------------" );
			data.append("\n");
			data.append("\n");
			data.append(" Month-Year, Total Quantity Sold,Total Value Sold");
			data.append("\n");
			data.append("\n");
			GregorianCalendar calendarFrom =new  GregorianCalendar();
			GregorianCalendar calendarTo =new  GregorianCalendar();
			
			
			Debug.logInfo("###### actualFromMonth"+actualFromMonth, module);
			Debug.logInfo("###### actualThruMonth "+actualThruMonth, module);
			
			
			
			//this loop is for same year
			for(int i=actualFromMonth;i<=actualThruMonth && intfromYear==intthruYear;i++){
				Debug.logInfo("###### i "+i, module);
				int month = i -1;
				Debug.logInfo("###### month "+month, module);
				GregorianCalendar calendar =new  GregorianCalendar();
				Debug.logInfo("###### Before setting calendar "+calendar, module);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH,month);
				calendar.set(Calendar.YEAR,intfromYear);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(GregorianCalendar.MINUTE, 0);
				calendar.set(GregorianCalendar.SECOND, 0);
				Debug.logInfo("###### calendar "+calendar, module);
				////System.out.println("Last Day of Month: " + calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
			    
				GregorianCalendar calendarThru =new  GregorianCalendar();
				calendarThru.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
				calendarThru.set(Calendar.MONTH,month);
				calendarThru.set(Calendar.YEAR,intthruYear);
				calendarThru.set(Calendar.HOUR_OF_DAY,23);
				calendarThru.set(GregorianCalendar.MINUTE, 59);
				calendarThru.set(GregorianCalendar.SECOND, 59);
				Debug.logInfo("###### calendarThru "+calendarThru, module);
				Map revenueReport =  getRevenueReportMonthlySummary(calendar, calendarThru);
				Debug.logInfo("###### revenueReport "+revenueReport, module);
				
				if(revenueReport != null ){
					data.append(getMonthForInt(month)+intthruYear+"");
					data.append(",");
					data.append(revenueReport.get("totalQuantitySold")+","+revenueReport.get("total"));
				}
				data.append("\n");
				
			}//for loop closed
			
			
			 int amotnh=actualFromMonth;
			 int de=12;
			if(intfromYear<intthruYear) {
			for(int j=intfromYear;j<=intthruYear;j++){
				Debug.logInfo("###### j "+j, module);
					if(j==intthruYear){
						de=actualThruMonth;
						Debug.logInfo(" If de ##"+de, module);
					}
					else{
						de=12;
						Debug.logInfo(" else de ## "+de, module);
					}
						for(int z=amotnh;z<=de;z++)	{
							Debug.logInfo("###### motnh "+z+",year"+j, module);
							Debug.logInfo("###### z "+z, module);
							int month =z - 1;
							Debug.logInfo("###### month "+month, module);
							GregorianCalendar calendar =new  GregorianCalendar();
							Debug.logInfo("###### Before setting calendar "+calendar, module);
							calendar.set(Calendar.DAY_OF_MONTH, 1);
							calendar.set(Calendar.MONTH,month);
							calendar.set(Calendar.YEAR,j);
							calendar.set(Calendar.HOUR_OF_DAY, 0);
							calendar.set(GregorianCalendar.MINUTE, 0);
							calendar.set(GregorianCalendar.SECOND, 0);
							Debug.logInfo("###### calendar "+calendar, module);
							////System.out.println("Last Day of Month: " + calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
						    
							GregorianCalendar calendarThru =new  GregorianCalendar();
							calendarThru.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
							calendarThru.set(Calendar.MONTH,month);
							calendarThru.set(Calendar.YEAR,j);
							calendarThru.set(Calendar.HOUR_OF_DAY,23);
							calendarThru.set(GregorianCalendar.MINUTE, 59);
							calendarThru.set(GregorianCalendar.SECOND, 59);
							Debug.logInfo("###### calendarThru "+calendarThru, module);
							Map revenueReport =  getRevenueReportMonthlySummary(calendar, calendarThru);
							Debug.logInfo("###### revenueReport "+revenueReport, module);
							if(revenueReport != null ){
								data.append(getMonthForInt(month)+j+"");
								data.append(",");
								data.append(revenueReport.get("totalQuantitySold")+","+revenueReport.get("total"));
							}
							data.append("\n");
						}
					
					amotnh=01;
			}//for loop year closed 
			}
			
			OutputStream out = response.getOutputStream();
			out.write(data.toString().getBytes());
			out.flush();		
			
		}//try closed
		 catch (Exception e) {
			////System.out.println("Exception getting monthly revenue summary report: " + e);
			e.printStackTrace();
		}
		 return "success";
		}


		public static  Map getRevenueReportMonthlySummary(GregorianCalendar fromDate,GregorianCalendar thruDate){
			
			Map revenueReport = new HashMap();
			try{
			
			int startDay = fromDate.get(Calendar.DAY_OF_YEAR);
			int endDay  =  thruDate.get(Calendar.DAY_OF_YEAR);
			List revenueProducts = new ArrayList();
			double total = 0;
			double totalQuantitySold = 0 ;
			GregorianCalendar fromRange =(GregorianCalendar) fromDate.clone();
			fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) - 1);
			GregorianCalendar toRange = (GregorianCalendar)fromDate.clone();
			toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) - 1);
			int dif = UtilDateTime.getDaysBetweenDates(fromDate.getTime(), thruDate.getTime());
			if(dif != 0){
				dif--;
			}
			for(int j =0;j<=dif;j++ ){
				Map productDetails = new HashMap();
				fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) + 1);
				fromRange.set(Calendar.HOUR_OF_DAY, 0);
				fromRange.set(GregorianCalendar.MINUTE, 0);
				fromRange.set(GregorianCalendar.SECOND, 0);
				toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) + 1);
				toRange.set(Calendar.HOUR_OF_DAY, 23);
				toRange.set(GregorianCalendar.MINUTE, 59);
				toRange.set(GregorianCalendar.SECOND, 59);
				
				productDetails.put("date",UtilDateTime.getFormattedDate(fromRange.getTime(),"d MMM yyyy")) ;
				double subTotal = 0;
				double quantitySold = 0 ;
				Timestamp fromRangeTime = new Timestamp(fromRange.getTime().getTime());
				Timestamp toRangeTime = new Timestamp(toRange.getTime().getTime());
				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);

				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromRangeTime));
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO,toRangeTime ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);
				
				List storeConditionList = new ArrayList();
				if(productStoreId != null)
				storeConditionList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				EntityConditionList storeCondition =null;
				if(storeConditionList.size()>0)
				storeCondition = EntityCondition.makeCondition(storeConditionList, EntityOperator.AND);
				
				List mainExprs = new ArrayList();
				if(storeCondition != null)
					mainExprs.add(storeCondition);
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				
				List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
						for(int i=0;i<orders.size();i++){
							GenericValue  orderGV = (GenericValue)orders.get(i);
								quantitySold = quantitySold + (orderGV.getDouble("quantity").doubleValue());
							subTotal = subTotal + getOrderProductPrice(orderGV.getString("productId"),orderGV.getString("orderId"),orderGV.getString("orderItemSeqId"));
						} 
						total = total + subTotal;
						totalQuantitySold = totalQuantitySold + quantitySold;
						productDetails.put("quantitySold",quantitySold+"");
						productDetails.put("subTotal",subTotal+"");
						//revenueProducts.add(productDetails); 
			}// end for loop Dates
					revenueReport.put("products",revenueProducts);
					revenueReport.put("totalQuantitySold",totalQuantitySold+"");
					revenueReport.put("total",total+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return revenueReport;
	}
		
		public static String getMonthForInt(int m) {
		    String month = "invalid";
		    DateFormatSymbols dfs = new DateFormatSymbols();
		    String[] months = dfs.getMonths();
		    if (m >= 0 && m <= 11 ) {
		        month = months[m];
		    }
		    return month;
		}		

		public static Map getinvoiceDetail(String orderId,String orderItemSeqId){
			 
            Map result = new HashMap();
            String invoiceId ="";
            String createdDate ="";
			 try{
				 List <GenericValue>oitemBilling = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
				 if(!UtilValidate.isEmpty(oitemBilling))
					 {
					  invoiceId  = EntityUtil.getFieldListFromEntityList(oitemBilling, "invoiceId",true).get(0).toString();
					  createdDate  = EntityUtil.getFieldListFromEntityList(oitemBilling, "createdStamp",true).get(0).toString();
					 }
				 result.put("invoiceId", invoiceId);
				 result.put("createdDate", createdDate);
			 }catch (Exception e) {
				 e.printStackTrace();
				 ////System.out.println("e === ReportsHelper.java: : "+e.getMessage());
						 }	
			    //System.out.println(" result map ="+result);
				return result;
			}
		
        public static Map getFacilityNameAndId(String productStoreId)
		{
			//Debug.log("\n\n productStoreId ="+productStoreId);
			Map members = new HashMap();
			List <GenericValue> facilityItr=new ArrayList<GenericValue>();
            String facilityName ="";
			try{
				
				facilityItr = delegator.findByAnd("ProductStoreFacility", UtilMisc.toMap("productStoreId",productStoreId));
		         if(facilityItr !=null)
		         {
		        	 
		        	 Iterator it = facilityItr.iterator();
		        	 while(it.hasNext())
		        	 {
		        		 GenericValue psf =  (GenericValue)it.next();
		        		 String facilityId =(String) psf.get("facilityId");
		        		 
		        		 if(facilityId!=null){
			        		  List fName=delegator.findList("Facility",EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,facilityId),null,null,null,false);
			        		  if(UtilValidate.isNotEmpty(fName))
			        		  {
			        			  facilityName=EntityUtil.getFieldListFromEntityList(fName, "facilityName", true).get(0).toString();
				        	      facilityId=EntityUtil.getFieldListFromEntityList(fName, "facilityId", true).get(0).toString();
			        		  }
				      	  }
		        		 members.put("facilityName", facilityName);
		        		 members.put("facilityId", facilityId);
		        	 }
		         }
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return members;
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

		public static  String getProductVariantsReport(HttpServletRequest request, HttpServletResponse response)
		{
			try{
				delegator = (GenericDelegator)request.getAttribute("delegator");
				dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				

				String productId = request.getParameter("productId");
				List productList = new ArrayList();
				Map result = new HashMap();
				List mainExprs = new ArrayList();
				
				List conditions = new ArrayList();
				EntityCondition condition = null;
				if(productId != null){
					if(productId.length() != 0){
						conditions.add(EntityCondition.makeCondition("productId", productId));
					}else{
						List products = new ArrayList();
						products = getStoreProductIdsList();
						if(products.size()>0){
						conditions.add(EntityCondition.makeCondition("productId", EntityOperator.IN, products));
				    	conditions.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "Y"));
						}
					}
				}
				if(productId == null){
					List products = new ArrayList();
					products = getStoreProductIdsList();
					if(products.size()>0){
					conditions.add(EntityCondition.makeCondition("productId", EntityOperator.IN, products));
					conditions.add(EntityCondition.makeCondition("isVirtual", EntityOperator.EQUALS, "Y"));
					}
				}
				if(conditions.size()>0){
				condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			    List orderBy = UtilMisc.toList("productId");
			    productList = (List) delegator.findByCondition("Product", condition, UtilMisc.toList("productId"), orderBy);
				}
				Debug.logInfo("productList size :"+productList.size(), module);
			    Iterator productItr = productList.iterator();
			    List variantsAndFeatures = new ArrayList();
			    while(productItr.hasNext()){
			    	GenericValue product = (GenericValue) productItr.next();
			    	Map variantAndFeature = new HashMap();
			    	
			    	variantAndFeature.put("parentProductId", product.getString("productId"));	
			    	result = (Map) dispatcher.runSync("getVariantCombinations", UtilMisc.toMap("productId", product.getString("productId")));
			    	List variants = new ArrayList();
			    	List featureCombinations = (List)result.get("featureCombinations");
			    	Iterator itr = featureCombinations.iterator();
			    	while(itr.hasNext()){
			    		Map featureCombination = (Map)itr.next();
			    		Map variantsMap = new HashMap();		
			    		variantsMap.put("variantProductId",featureCombination.get("existingVariantProductIds"));
			    		List curProductFeatureAndAppls = (List)featureCombination.get("curProductFeatureAndAppls");
			    		List existingVariants = new ArrayList();
			    		
			    		existingVariants = (List)featureCombination.get("existingVariantProductIds");
			    		if(existingVariants.size()>0){
			    		List features = new ArrayList();
			    		Iterator productFeaturesItr = curProductFeatureAndAppls.iterator();
			    		while(productFeaturesItr.hasNext()){
			    			GenericValue featureGV  = (GenericValue) productFeaturesItr.next();
			    			Map feature = new HashMap();
			    			feature.put("productFeatureTypeId",featureGV.getString("productFeatureTypeId"));
			    			feature.put("description",featureGV.getString("description"));
			    			features.add(feature);
			    		}
			    		variantsMap.put("features",features);
			    		variants.add(variantsMap);
			    		}
			    	}
			    	variantAndFeature.put("variants",variants);
			    	variantsAndFeatures.add(variantAndFeature);
			    }
			    
				//response.setContentType("application/octet-stream");
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=ProductFeatures_or_Options.csv");
				StringBuffer data = new StringBuffer();
				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				if(productStoreId !=  null){
					data.append("Product Store Id : "+productStoreId);
					data.append("\n");
				}
				data.append("Report : Product Features(Options) Report");
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append("Parent Product Id ,Variant Product Id  ,Features,");
				data.append("\n");
				data.append("\n");
				
				if(variantsAndFeatures != null && variantsAndFeatures.size() >0){
					for(int i=0;i<variantsAndFeatures.size();i++){
						GenericValue orderRoleGv = null;
						Map variantAndFeatures = (Map)variantsAndFeatures.get(i);
						List<Map> variants = (List) variantAndFeatures.get("variants");
						
						for(Map variant: variants){
							data.append("\""+variantAndFeatures.get("parentProductId")+"\""+",");	
							List variantProductIds  = (List) variant.get("variantProductId");
							String variantProductId = null;
							if(variantProductIds != null && variantProductIds.size()>0 ){
								variantProductId = (String) variantProductIds.get(0);
								variantProductId = variantProductId.replace("[", "");
								variantProductId = variantProductId.replace("]", "");
							}
							data.append(variantProductId+",");
						
							List<Map> features = (List) variant.get("features");
							for(Map feature: features){
								data.append("("+feature.get("productFeatureTypeId")+" = "+feature.get("description")+") ");
							}
							data.append(",");
							data.append("\n");
						}
					}
				}
							
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				
				}catch(Exception ex){
					Debug.logError("#### Exception: " + ex.getMessage(),module);
				}
				
				return "success";
		}


	    public static List<String> getStoreProductIdsList(){
	    	
			List products = new ArrayList();
			List<String> categories = new ArrayList<String>();
			try{
				categories = getProductStoreCategories();
				if(categories.size()>0){
		        List members = (List) delegator.findByCondition("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categories), UtilMisc.toList("productId"), UtilMisc.toList("productId"));
		        products = EntityUtil.getFieldListFromEntityList(members, "productId", true);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return products;
	    }		

	    public static List<String> getStoreProductIdsList(GenericDelegator delegator, String productStoreId){

			Report.delegator = delegator;
			Report.productStoreId = productStoreId;
			
			List products = new ArrayList();
			List<String> categories = new ArrayList<String>();
			try{
				categories = getProductStoreCategories();
				if(categories.size()>0){
		        List members = (List) delegator.findByCondition("ProductCategoryMember", EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categories), UtilMisc.toList("productId"), UtilMisc.toList("productId"));
		        products = EntityUtil.getFieldListFromEntityList(members, "productId", true);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			Debug.logInfo("product list size :"+products.size(), module);
			return products;
	    }
	    
		public static List getProductStoreCategories(){
			List categoryIdList = new ArrayList<String>();
			if(productStoreId != null && delegator != null){
				
				List<GenericValue> catalogs = new ArrayList<GenericValue>();
					
		        try {
		        	catalogs = EntityUtil.filterByDate(delegator.findByAnd("ProductStoreCatalog", UtilMisc.toMap("productStoreId", productStoreId), UtilMisc.toList("sequenceNum", "prodCatalogId")), true);
		        } catch (GenericEntityException e) {
		            Debug.logError(e, "Error looking up store catalogs for store with id " + productStoreId, module);
		        }
				List<String> catalogIds = EntityUtil.getFieldListFromEntityList(catalogs, "prodCatalogId", true);
				Iterator catalogIdsItr = catalogIds.iterator();
				while(catalogIdsItr.hasNext()){
					String prodCatalogId = (String) catalogIdsItr.next();
					List<GenericValue> categories = CatalogWorker.getProdCatalogCategories(delegator, prodCatalogId, "PCCT_BROWSE_ROOT");
					for(GenericValue category : categories){
						try {
						List categoryRollups = EntityUtil.filterByDate(delegator.findByAnd("ProductCategoryRollup", UtilMisc.toMap("parentProductCategoryId", category.getString("productCategoryId"))));
						categoryIdList.addAll( EntityUtil.getFieldListFromEntityList(categoryRollups, "productCategoryId", true));
						}catch (GenericEntityException e) {
				            Debug.logError(e, "Error looking up store categories", module);
				        }
					}
				}
			} 
			return categoryIdList;
		}
		
		public static  String productListCSV(HttpServletRequest request, HttpServletResponse response){
				try{
					HttpSession session = request.getSession();
					productStoreId = (String) session.getAttribute("productStoreId");
					
					delegator = (GenericDelegator)request.getAttribute("delegator");
	
					List<GenericValue> productList = new ArrayList<GenericValue>();
					List<String> productIds = new ArrayList<String>();
					String productId = request.getParameter("productId");
					String internalName = request.getParameter("internalName");
					//productStoreId = request.getParameter("productStoreId");
					List exprs = new ArrayList();
					if (productId != null && !productId.equals("") ) {
						   exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
					}
					if (internalName != null && !internalName.equals("") ) {
						   exprs.add(EntityCondition.makeCondition("internalName", EntityOperator.LIKE, "%"+internalName+"%"));
					}
	
					if(productId == null && internalName == null){
						productIds = getStoreProductIdsList();
						exprs.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
						exprs.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
					}else if(productId.equals("") && internalName.equals("")){
						productIds = getStoreProductIdsList();
						exprs.add(EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null));
						exprs.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productIds));
					}
					List conditions = new ArrayList();
					conditions.addAll(exprs);
	
					try{
					  //eli = delegator.findListIteratorByCondition("Product", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("productId"));
						//productList = eli.getCompleteList();
						productList = delegator.findByCondition("Product", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("productId"));
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					
					response.setContentType("application/excel");
					response.setHeader("Content-disposition","attachment;filename=productReport.csv");
					StringBuffer data = new StringBuffer();
					data.append("\n");
					data.append("#--------------------------------------------------------------");
					data.append("\n");
					if(productStoreId !=  null){
						data.append("Product Store Id : "+productStoreId);
						data.append("\n");
					}
					data.append("Report : Product Report");
					data.append("\n");
					data.append("#--------------------------------------------------------------" );
					data.append("\n");
					data.append("\n");
					data.append("Product Id, Internal Name, Product Name, Description");
					data.append("\n");
					data.append("\n");
					
					for(GenericValue product : productList){
						String internalname = "";
						String productname = "";
						String description = "";
						internalname = (String) product.getString("internalName");
						productname = (String) product.getString("productName");
						description = (String) product.getString("description");
						if(description != null){
							description = description.replaceAll(","," ");
							description = description.replaceAll("\\n"," ");
							description = description.replaceAll("\"","''");
							description = description.trim();
						}
						String productid = (String) product.getString("productId");
						//productid = "\""+productid+"\"";
						data.append("\""+productid+"\""+",");
						if(internalname != null)
						data.append(internalname.replaceAll(","," ")+",");
						else
						data.append(""+",");
						
						if(productname != null)
						data.append(productname.replaceAll(","," ")+",");
						else
						data.append(""+",");
						if(description != null)
						data.append(description+",");
						else
						data.append(""+",");
					
						data.append(",");
						data.append("\n");
					}
					data.append("\n");
					OutputStream out = response.getOutputStream();
					out.write(data.toString().getBytes());
					out.flush();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}	
		
		public static  String dashBoradReportForFNP(HttpServletRequest request, HttpServletResponse response){
		
	        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			BigDecimal checkTotal = BigDecimal.ZERO;
	        BigDecimal cashTotal = BigDecimal.ZERO;
	        BigDecimal gcTotal = BigDecimal.ZERO;
	        BigDecimal creditTotal = BigDecimal.ZERO;
	        BigDecimal gvTotal = BigDecimal.ZERO;
	        BigDecimal ccTotal = BigDecimal.ZERO;
	        BigDecimal othTotal = BigDecimal.ZERO;
	        BigDecimal totalRecivedCollection=BigDecimal.ZERO;
	        BigDecimal total = BigDecimal.ZERO;
	        BigDecimal totalExpense=BigDecimal.ZERO;
	        BigDecimal totalFreshPrice=BigDecimal.ZERO;
	        BigDecimal totalFreshCount=BigDecimal.ZERO;
	        BigDecimal totalOtherPurchase=BigDecimal.ZERO;
	        BigDecimal freshFlowerSale=BigDecimal.ZERO;
	        
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			Date date = new Date();
			String reportType = request.getParameter("reportType");
		    fromDate = request.getParameter("minDate");
			thruDate  = request.getParameter("maxDate");
				Integer intDay = new Integer(date.getDate());
				Integer intMonth = new Integer(date.getMonth()+1);
				Integer intYear = new Integer(date.getYear()+1900);
				
				String  day = intDay.toString();
				String  month = intMonth.toString();
				String  year = intYear.toString();
				if(fromDate == null)
					fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
				if(fromDate != null && fromDate.length()<19)
					fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
				
				if(thruDate == null)
					thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				if(thruDate != null && thruDate.length()<19)
					thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				List<GenericValue> facilitylist=null;
                List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
				request.setAttribute("fromDateStr", fromDate);
				request.setAttribute("thruDateStr", thruDate);
				//System.out.println("######## FROM DATE #####"+fromDate);
				//System.out.println("######## thru DATE #####"+thruDate);

                try {
					facilitylist=delegator.findAll("Facility");
				  for(GenericValue facility:facilitylist){
					  
					  
					  HashMap<String,String> items=new HashMap<String, String>();
				      if(facility.getString("facilityId").equals(UtilProperties.getPropertyValue("general","posstore.usefaclityId"))){

				    	  String NumOfSale= getNumberOfSale(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        totalExpense=getExpense(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        cashTotal = totalCash(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            checkTotal = totalCheckAmt(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            creditTotal = totalCredit(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            ccTotal = totalCreditCard(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            totalRecivedCollection=totalReceivedCredits(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            //System.out.println("## CASH###"+cashTotal+"===CheckTotal"+checkTotal+"===Credit"+creditTotal+"====CC"+ccTotal+"===Recived Credit"+totalRecivedCollection);
				            total =cashTotal.add(checkTotal).add(creditTotal).add(ccTotal);
					        
				           // totalFreshPrice=getFreshFlowerPrice(facility.getString("facilityId"),fromDate,thruDate,delegator);
					       //totalFreshCount=getFreshFlowerCount(facility.getString("facilityId"),fromDate,thruDate,delegator);
				        	
				            freshFlowerSale= getFreshFlowerSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
				            freshFlowerSale=freshFlowerSale.setScale(2,RoundingMode.HALF_DOWN);
                            int freshFlowerSalecount=getFreshFlowerSaleCount(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
					        totalOtherPurchase=totalOtherPurchase(facility.getString("facilityId"),fromDate,thruDate,delegator);
				          
				          items.put("facilityId",facility.getString("facilityId"));
				         // items.put("totalFreshPrice",totalFreshPrice.toString());
				          items.put("freshFlowerSale",freshFlowerSale.toString());
				          items.put("totalFreshCount",String.valueOf(freshFlowerSalecount));
				          
				          items.put("totalOtherPurchase",totalOtherPurchase.toString());
				          
				          items.put("NumOfSale",NumOfSale);
				          items.put("totalExpense",totalExpense.toString());
				          items.put("facilityName",facility.getString("facilityName"));
				          items.put("totcash",cashTotal.toString());
						  items.put("totcredit",creditTotal.toString());
						  items.put("totcreditcard",ccTotal.toString());
						  items.put("totcheck",checkTotal.toString());
						  items.put("totalcollections",totalRecivedCollection.toString());
						  items.put("totalSales",total.toString());
						  resultList.add(items);
				  }
				  
			}//if closed only for selected facility
				  
				  request.setAttribute("dashBordList",resultList);
                 } catch (GenericEntityException e) {
					e.printStackTrace();
				}			
			return "success";
		}
		
		
		public static  String dashBoradReportCsv(HttpServletRequest request, HttpServletResponse response){
			   GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			   StringBuffer data = new StringBuffer();
			   BigDecimal checkTotal = BigDecimal.ZERO;
		        BigDecimal cashTotal = BigDecimal.ZERO;
		        BigDecimal gcTotal = BigDecimal.ZERO;
		        BigDecimal creditTotal = BigDecimal.ZERO;
		        BigDecimal gvTotal = BigDecimal.ZERO;
		        BigDecimal ccTotal = BigDecimal.ZERO;
		        BigDecimal othTotal = BigDecimal.ZERO;
		        BigDecimal totalRecivedCollection=BigDecimal.ZERO;
		        BigDecimal total = BigDecimal.ZERO;
		        BigDecimal totalExpense=BigDecimal.ZERO;
		        BigDecimal totalFreshPrice=BigDecimal.ZERO;
		        BigDecimal totalFreshCount=BigDecimal.ZERO;
		        BigDecimal totalOtherPurchase=BigDecimal.ZERO;
		        BigDecimal freshFlowerSale=BigDecimal.ZERO;
		        
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
			    fromDate = request.getParameter("minDate");
				thruDate  = request.getParameter("maxDate");
					Integer intDay = new Integer(date.getDate());
					Integer intMonth = new Integer(date.getMonth()+1);
					Integer intYear = new Integer(date.getYear()+1900);
					
					String  day = intDay.toString();
					String  month = intMonth.toString();
					String  year = intYear.toString();
					if(fromDate == null)
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					if(fromDate != null && fromDate.length()<19)
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					
					if(thruDate == null)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					if(thruDate != null && thruDate.length()<19)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					List<GenericValue> facilitylist=null;
	                List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
					request.setAttribute("fromDateStr", fromDate);
					request.setAttribute("thruDateStr", thruDate);
					//System.out.println("######## FROM DATE #####"+fromDate);
					//System.out.println("######## thru DATE #####"+thruDate);
			
			
			try{
				facilitylist=delegator.findAll("Facility");
				  for(GenericValue facility:facilitylist){
					  
					  
					  HashMap<String,String> items=new HashMap<String, String>();
				      if(facility.getString("facilityId").equals(UtilProperties.getPropertyValue("general","posstore.usefaclityId"))){

				    	  String NumOfSale= getNumberOfSale(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        totalExpense=getExpense(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        cashTotal = totalCash(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            checkTotal = totalCheckAmt(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            creditTotal = totalCredit(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            ccTotal = totalCreditCard(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            totalRecivedCollection=totalReceivedCredits(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            //System.out.println("## CASH###"+cashTotal+"===CheckTotal"+checkTotal+"===Credit"+creditTotal+"====CC"+ccTotal+"===Recived Credit"+totalRecivedCollection);
				            total =cashTotal.add(checkTotal).add(creditTotal).add(ccTotal);
					        
				           // totalFreshPrice=getFreshFlowerPrice(facility.getString("facilityId"),fromDate,thruDate,delegator);
					       //totalFreshCount=getFreshFlowerCount(facility.getString("facilityId"),fromDate,thruDate,delegator);
				        	
				            freshFlowerSale= getFreshFlowerSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
				            freshFlowerSale=freshFlowerSale.setScale(2,RoundingMode.HALF_DOWN);
                          int freshFlowerSalecount=getFreshFlowerSaleCount(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
					        totalOtherPurchase=totalOtherPurchase(facility.getString("facilityId"),fromDate,thruDate,delegator);
				        	//System.out.println("######## OTHER PURCHASE #######"+totalOtherPurchase);
				          
				          items.put("facilityId",facility.getString("facilityId"));
				         // items.put("totalFreshPrice",totalFreshPrice.toString());
				          items.put("freshFlowerSale",freshFlowerSale.toString());
				          items.put("totalFreshCount",String.valueOf(freshFlowerSalecount));
				          
				          items.put("totalOtherPurchase",totalOtherPurchase.toString());
				          
				          items.put("NumOfSale",NumOfSale);
				          items.put("totalExpense",totalExpense.toString());
				          items.put("facilityName",facility.getString("facilityName"));
				          items.put("totcash",cashTotal.toString());
						  items.put("totcredit",creditTotal.toString());
						  items.put("totcreditcard",ccTotal.toString());
						  items.put("totcheck",checkTotal.toString());
						  items.put("totalcollections",totalRecivedCollection.toString());
						  items.put("totalSales",total.toString());
				
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=DashBoardReport.csv");
				

				data.append("\n");
				data.append(",,,,,Dash Board Report");
				data.append("\n");
				data.append("No of Sales,Location Name,Cash Sales,Credit Sales,Credit Card Sales,Total Sales,ORD Collection,Expense,Other Purchase");
				data.append("\n");	
				data.append((String) NumOfSale+","+facility.getString("facilityName")+","+cashTotal.toString()+","+creditTotal.toString()+","+ccTotal.toString()+","+total.toString()+","+totalRecivedCollection.toString()+","+totalExpense.toString()+","+totalOtherPurchase.toString());
			
				      
					
					
						

				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				      }	
				  }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}		
		
		public static  String dashBoradReportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, WriteException{
			
	        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			BigDecimal checkTotal = BigDecimal.ZERO;
	        BigDecimal cashTotal = BigDecimal.ZERO;
	        BigDecimal gcTotal = BigDecimal.ZERO;
	        BigDecimal creditTotal = BigDecimal.ZERO;
	        BigDecimal gvTotal = BigDecimal.ZERO;
	        BigDecimal ccTotal = BigDecimal.ZERO;
	        BigDecimal othTotal = BigDecimal.ZERO;
	        BigDecimal totalRecivedCollection=BigDecimal.ZERO;
	        BigDecimal total = BigDecimal.ZERO;
	        BigDecimal totalExpense=BigDecimal.ZERO;
	        BigDecimal totalFreshPrice=BigDecimal.ZERO;
	        BigDecimal totalFreshCount=BigDecimal.ZERO;
	        BigDecimal totalOtherPurchase=BigDecimal.ZERO;
	        BigDecimal freshFlowerSale=BigDecimal.ZERO;
	        
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			Date date = new Date();
			String reportType = request.getParameter("reportType");
		    fromDate = request.getParameter("minDate");
			thruDate  = request.getParameter("maxDate");
				Integer intDay = new Integer(date.getDate());
				Integer intMonth = new Integer(date.getMonth()+1);
				Integer intYear = new Integer(date.getYear()+1900);
				
				String  day = intDay.toString();
				String  month = intMonth.toString();
				String  year = intYear.toString();
				if(fromDate == null)
					fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
				if(fromDate != null && fromDate.length()<19)
					fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
				
				if(thruDate == null)
					thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				if(thruDate != null && thruDate.length()<19)
					thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				List<GenericValue> facilitylist=null;
                List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
				request.setAttribute("fromDateStr", fromDate);
				request.setAttribute("thruDateStr", thruDate);
				//System.out.println("######## FROM DATE #####"+fromDate);
				//System.out.println("######## thru DATE #####"+thruDate);

                try {
                	
                	 response.setContentType("application/excel");
					 WorkbookSettings wbSettings = new WorkbookSettings();
					 WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
					 WritableSheet sheet=workbook.createSheet("mysheet", 0); 
					 WritableFont wfobj=new WritableFont(WritableFont.ARIAL,8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
					 WritableCellFormat cfobj=new WritableCellFormat(wfobj);
					 cfobj.setBackground(Colour.LAVENDER);
					 cfobj.setWrap(true);
					 Label sales=new Label(1,1,"No of Sales"); 
					 sales.setCellFormat(cfobj);
					 sheet.addCell(sales); 
					 Label location=new Label(2,1,"Location Name"); 
					 location.setCellFormat(cfobj);
					 sheet.addCell(location);                                      
					 Label cash=new Label(3,1,"Cash Sales"); 
					 cash.setCellFormat(cfobj);
					 sheet.addCell(cash);  
					 Label credit=new Label(4,1,"Credit Sales"); 
					 credit.setCellFormat(cfobj);
					 sheet.addCell(credit);  
					 Label card=new Label(5,1,"Credit Card Sales"); 
					 card.setCellFormat(cfobj);
					 sheet.addCell(card);  
					 Label total1=new Label(6,1,"Total Sales"); 
					 total1.setCellFormat(cfobj);
					 sheet.addCell(total1);  
					 
					   
					 Label coll=new Label(7,1,"ORD Collection"); 
					 coll.setCellFormat(cfobj);
					 sheet.addCell(coll);  
					 Label expense=new Label(8,1,"Expense"); 
					 expense.setCellFormat(cfobj);
					 sheet.addCell(expense);  
					 Label purchase=new Label(9,1,"Other Purchase"); 
					 purchase.setCellFormat(cfobj);
					 sheet.addCell(purchase);  
                	
					facilitylist=delegator.findAll("Facility");
				  for(GenericValue facility:facilitylist){
					  
					  
					  HashMap<String,String> items=new HashMap<String, String>();
				      if(facility.getString("facilityId").equals(UtilProperties.getPropertyValue("general","posstore.usefaclityId"))){

				    	  String NumOfSale= getNumberOfSale(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        totalExpense=getExpense(facility.getString("facilityId"),fromDate,thruDate,delegator);
					        cashTotal = totalCash(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            checkTotal = totalCheckAmt(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            creditTotal = totalCredit(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            ccTotal = totalCreditCard(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            totalRecivedCollection=totalReceivedCredits(facility.getString("facilityId"),fromDate,thruDate,delegator);
				            //System.out.println("## CASH###"+cashTotal+"===CheckTotal"+checkTotal+"===Credit"+creditTotal+"====CC"+ccTotal+"===Recived Credit"+totalRecivedCollection);
				            total =cashTotal.add(checkTotal).add(creditTotal).add(ccTotal);
					        
				           // totalFreshPrice=getFreshFlowerPrice(facility.getString("facilityId"),fromDate,thruDate,delegator);
					       //totalFreshCount=getFreshFlowerCount(facility.getString("facilityId"),fromDate,thruDate,delegator);
				        	
				            freshFlowerSale= getFreshFlowerSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
				            freshFlowerSale=freshFlowerSale.setScale(2,RoundingMode.HALF_DOWN);
                            int freshFlowerSalecount=getFreshFlowerSaleCount(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
					        totalOtherPurchase=totalOtherPurchase(facility.getString("facilityId"),fromDate,thruDate,delegator);
				        	//System.out.println("######## OTHER PURCHASE #######"+totalOtherPurchase);
				          
				          items.put("facilityId",facility.getString("facilityId"));
				         // items.put("totalFreshPrice",totalFreshPrice.toString());
				          items.put("freshFlowerSale",freshFlowerSale.toString());
				          items.put("totalFreshCount",String.valueOf(freshFlowerSalecount));
				          
				          items.put("totalOtherPurchase",totalOtherPurchase.toString());
				          
				          items.put("NumOfSale",NumOfSale);
				          items.put("totalExpense",totalExpense.toString());
				          items.put("facilityName",facility.getString("facilityName"));
				          items.put("totcash",cashTotal.toString());
						  items.put("totcredit",creditTotal.toString());
						  items.put("totcreditcard",ccTotal.toString());
						  items.put("totcheck",checkTotal.toString());
						  items.put("totalcollections",totalRecivedCollection.toString());
						  items.put("totalSales",total.toString());
						  Label sal=new Label(1,2,(String) NumOfSale);  
						  sheet.addCell(sal);   
						  Label fac=new Label(2,2,facility.getString("facilityName"));  
						  sheet.addCell(fac);   
						  Label cas=new Label(3,2,cashTotal.toString());  
						  sheet.addCell(cas);   
						  Label cre=new Label(4,2,creditTotal.toString());  
						  sheet.addCell(cre);   
						  Label tot=new Label(5,2,ccTotal.toString());  
						  sheet.addCell(tot);   
						  Label tota=new Label(6,2,total.toString());  
						  sheet.addCell(tota);   
						     
						  Label col=new Label(7,2,totalRecivedCollection.toString());  
						  sheet.addCell(col);   
						  Label exp=new Label(8,2,totalExpense.toString());  
						  sheet.addCell(exp);   
						  Label pur=new Label(9,2,totalOtherPurchase.toString());  
						  sheet.addCell(pur);   
						   
						  
							 workbook.write();
							 workbook.close();
							
				  }
				  
			}//if closed only for selected facility
				  
				 
                 } catch (GenericEntityException e) {
					e.printStackTrace();
				}			
			return "success";
		}		
		
		
		public static  String salesmanReportCSV(HttpServletRequest request, HttpServletResponse response){
			   GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			   StringBuffer data = new StringBuffer();
			  
		    
				String fromDate=null;
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				if(request.getParameter("minDate")!=null)
			    fromDate = request.getParameter("minDate");
				thruDate  = request.getParameter("maxDate");
					Integer intDay = new Integer(date.getDate());
					Integer intMonth = new Integer(date.getMonth()+1);
					Integer intYear = new Integer(date.getYear()+1900);
					
					String  day = intDay.toString();
					String  month = intMonth.toString();
					String  year = intYear.toString();
					
					
					if(thruDate == null)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					if(thruDate != null && thruDate.length()<19)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					List<GenericValue> facilitylist=null;
	                List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
					request.setAttribute("fromDateStr", fromDate);
					request.setAttribute("thruDateStr", thruDate);
					
			
			
			try{
				
				
				List conditions = new ArrayList();
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"));
				if(fromDate!=null && fromDate!="")
					  
				conditions.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
					if(thruDate!=null)
					 conditions.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				
					List<GenericValue> transList1 = delegator.findList("OrderRole", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
					
					List transList=EntityUtil.getFieldListFromEntityList(transList1, "orderId", false);
				
				
				List<GenericValue> grandTotal1=new ArrayList();
				 
					 
						 grandTotal1 = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.IN,transList), null, null, null, false);
					 
				
				
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=salesManReport.csv");
				

				data.append("\n");
				data.append(",,,,,SalesMan Report");
				data.append("\n");
				data.append("Order Id,Total Amount,Customer Name");
				data.append("\n");	
				
				 Iterator oii = grandTotal1.iterator();
				 while (oii.hasNext()) {
                	 GenericValue orderHeader = (GenericValue) oii.next();
                	 String orderId = orderHeader.getString("orderId");
                	 String createdBy = orderHeader.getString("grandTotal");
                	 String customerName = orderHeader.getString("createdBy");
				data.append((String) orderId+","+createdBy+","+customerName);
				data.append("\n");	
				 }
				      
					
					
						

				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				      }	
				  
			catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}		
		
		
		public static BigDecimal totalCash(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCash=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CASH') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'  AND IS_CREDIT IS NULL";
	    	//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCash=rs.getBigDecimal(1);
	              }
	    	    if(totalCash==null){
	    	    	totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCash;
		}

		/*public static BigDecimal totalCreditAmountByCustomer(String facilityId,String fromDate,String thruDate,GenericDelegator delegator,String billPartyId){
			BigDecimal totalCreditAmount=BigDecimal.ZERO;
	    	Connection conn=null;
//			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_COD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_COD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCreditAmount=rs.getBigDecimal(1);
	              }
	    	    if(totalCreditAmount==null){
	    	    	totalCreditAmount=BigDecimal.ZERO;
	    	     }
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCreditAmount;
		}*/


		public static BigDecimal totalCreditAmount(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCreditAmount=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
	    	//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCreditAmount=rs.getBigDecimal(1);
	              }
	    	    if(totalCreditAmount==null){
	    	    	totalCreditAmount=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCreditAmount;
		}

		public static BigDecimal totalCreditCardAmount(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCreditCardAmount=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CREDIT_CARD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
	    	//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCreditCardAmount=rs.getBigDecimal(1);
	              }
	    	    if(totalCreditCardAmount==null){
	    	    	totalCreditCardAmount=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCreditCardAmount;
		}
	
		
		
		public static String cashwindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
			List<HashMap> cashwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCash=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CASH') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND IS_CREDIT IS NULL";
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       cashwindowlist.add(map);
	    	    }
	    	    if(totalCash==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalCash=totalCash(facilityId, fromDate, thruDate, delegator);
			request.setAttribute("cashwindowlist", cashwindowlist);
			request.setAttribute("totalCash", totalCash.toString());

			return "success";
		}
		
		public static String creditwindowByCustomer(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
		    String billPartyId=request.getParameter("billPartyId");
			List<HashMap> creditwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCredit=BigDecimal.ZERO;
	    	Connection conn=null;
			//String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_COD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";

			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       creditwindowlist.add(map);
	    	    }
	    	    if(totalCredit==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalCredit=totalCreditAmountByCustomer(facilityId, fromDate, thruDate, delegator,billPartyId);
			request.setAttribute("creditwindowlist", creditwindowlist);
			request.setAttribute("totalcredit", totalCredit.toString());

			return "success";
		}

		
		public static String creditwindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
			List<HashMap> creditwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCredit=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       creditwindowlist.add(map);
	    	    }
	    	    if(totalCredit==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalCredit=totalCreditAmount(facilityId, fromDate, thruDate, delegator);
			request.setAttribute("creditwindowlist", creditwindowlist);
			request.setAttribute("totalcredit", totalCredit.toString());

			return "success";
		}
		public static String chequewindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
			List<HashMap> creditwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCredit=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='PERSONAL_CHECK') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND IS_CREDIT IS NULL";
			
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       creditwindowlist.add(map);
	    	    }
	    	    if(totalCredit==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalCredit=totalCreditAmount(facilityId, fromDate, thruDate, delegator);
			request.setAttribute("cashwindowlist", creditwindowlist);
			request.setAttribute("totalCash", totalCredit.toString());

			return "success";
		}

		public static String creditcardwindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
			List<HashMap> creditcardwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalcreditcardamt=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CREDIT_CARD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND IS_CREDIT IS NULL";
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       creditcardwindowlist.add(map);
	    	    }
	    	    if(totalcreditcardamt==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalcreditcardamt=totalCreditCardAmount(facilityId, fromDate, thruDate, delegator);
			request.setAttribute("creditcardwindowlist", creditcardwindowlist);
			request.setAttribute("totalcreditcard", totalcreditcardamt.toString());

			return "success";
		}
		
		
		
		public static String orderitemwindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String orderId=request.getParameter("orderId");
			try{
				List orderItems=delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
				request.setAttribute("orderItems", orderItems);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}
		
		
		public static String cutomerwindow(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String partyId=request.getParameter("partyId");
			List<HashMap> contactDetailList=new ArrayList<HashMap>();
			try{
				List <GenericValue>contactDetail=delegator.findByAnd("PartyContactDetailByPurpose", UtilMisc.toMap("partyId", partyId));
				HashMap<String,String> map=new HashMap<String, String>();
                for(GenericValue gv:contactDetail){
	              if(gv!=null && gv.getString("contactMechTypeId").equals("EMAIL_ADDRESS"));
	                map.put("email", gv.getString("infoString"));
                 
                }				
                for(GenericValue gv:contactDetail){
  	              if(gv!=null && gv.getString("contactMechTypeId").equals("POSTAL_ADDRESS"));
  	              {    map.put("address", gv.getString("address1"));
  	                   map.put("city", gv.getString("city"));
  	                   map.put("state", gv.getString("stateProvinceGeoId"));
  	                   
  	              }
                }				
                contactDetailList.add(map);
				//System.out.println("##########NFJNKSA ##########"+contactDetailList);
				request.setAttribute("partyId", partyId);

				request.setAttribute("contactDetail", contactDetailList);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}
		
		
		
		
		
		public static BigDecimal totalCredit(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCredit=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT' AND op.is_credit='Yes') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
	    	try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCredit=rs.getBigDecimal(1);
	              }
	    	    if(totalCredit==null){
	    	    	totalCredit=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCredit;
		}
		public static BigDecimal totalCreditCard(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCreditCard=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CREDIT_CARD') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND IS_CREDIT IS NULL";
	    	try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCreditCard=rs.getBigDecimal(1);
	              }
	    	    if(totalCreditCard==null){
	    	    	totalCreditCard=BigDecimal.ZERO;
	    	    }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCreditCard;
		}

		public static BigDecimal totalCheckAmt(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCheckAmt=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='PERSONAL_CHECK') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND IS_CREDIT IS NULL";
	    	try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCheckAmt=rs.getBigDecimal(1);
	              }
	    	    if(totalCheckAmt==null){
	    	    	totalCheckAmt=BigDecimal.ZERO;
	    	    }

	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalCheckAmt;
		}
	
	public static BigDecimal totalOrderValues(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalOrderValues=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CREDIT_CARD' OR op.payment_method_type_id='CASH' OR op.payment_method_type_id='EXT_CREDIT' OR op.payment_method_type_id='PERSONAL_CHECK')";
	    	try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalOrderValues=rs.getBigDecimal(1);
	              }
	    	    if(totalOrderValues==null){
	    	    	totalOrderValues=BigDecimal.ZERO;
	    	    }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalOrderValues;
		}


   public static BigDecimal totalReceivedCredits(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	    //List exprs = FastList.newInstance();
	    List exprs = new ArrayList();
	    try {
		List dateCondiList = new ArrayList();
		try {
			dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
	    exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	    exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));

	    exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
        //System.out.println("###### CONDITION ##########==="+ EntityCondition.makeCondition(exprs, EntityOperator.AND));
        List orderBy = UtilMisc.toList("-createdStamp");
        List <GenericValue>transList = null;
        transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
        for(GenericValue trs:transList){
       	    totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
          }
	    if(totalReceivedCredit==null){
	    	totalReceivedCredit=BigDecimal.ZERO;
	     }
	    }
       catch (GenericEntityException e) {
           Debug.logError(e, module);
       }
       return totalReceivedCredit;
   }
   
	  public static BigDecimal dueCredits(GenericDelegator delegator,String facilityId,String fromDate,String thruDate){
		    BigDecimal dueCredits=BigDecimal.ZERO;
	       // List exprs = FastList.newInstance();
	        List exprs = new ArrayList();
	        List <GenericValue>transList = null;
			List dateCondiList = new ArrayList();
			try {
              if(fromDate!=null && thruDate!=null){
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
                } 
	        exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
	        exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
	        exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
	        List orderBy = UtilMisc.toList("-createdStamp");
	       
	        
	            transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	        } catch (Exception e) {
	            Debug.logError(e, module);
	        }
	        BigDecimal totalPendingCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList){
	        	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	      //  List exprs1 = FastList.newInstance();
	        List exprs1 = new ArrayList();
	        List <GenericValue>transList1 = null;
	        dateCondiList = new ArrayList();
	        try {
	        if(fromDate!=null && thruDate!=null){
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				exprs1.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
                } 

	        exprs1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_RECEIVED"));
	        exprs1.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Done"));
	        List orderBy1 = UtilMisc.toList("-createdStamp");
	            transList1 = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs1, EntityOperator.AND), null, orderBy1, null, false);
	        } catch (Exception e) {
	            Debug.logError(e, module);
	        }

	        BigDecimal totalReceivedCredit=BigDecimal.ZERO;  
	        for(GenericValue trs:transList1){
	        	totalReceivedCredit=totalReceivedCredit.add(trs.getBigDecimal("maxAmount"));
	        }
	        
	        //System.out.println("######## pending #### Reciveded #######"+totalPendingCredit +"===="+totalReceivedCredit);
	        if(totalPendingCredit!=BigDecimal.ZERO && totalReceivedCredit!=BigDecimal.ZERO){
	        	dueCredits=totalPendingCredit.subtract(totalReceivedCredit);
	        }
		  return dueCredits;
	  }
	  

   
   
   public static  String DSRCashSummaryReportFNP(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
	  
        String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
		String reportType = request.getParameter("reportType");
	    fromDate = request.getParameter("minDate");
		thruDate  = request.getParameter("maxDate");
			Integer intDay = new Integer(date.getDate());
			Integer intMonth = new Integer(date.getMonth()+1);
			Integer intYear = new Integer(date.getYear()+1900);
			
			String  day = intDay.toString();
			String  month = intMonth.toString();
			String  year = intYear.toString();
			if(fromDate == null)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			if(fromDate != null && fromDate.length()<19)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			
			if(thruDate == null)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			if(thruDate != null && thruDate.length()<19)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			
			List<GenericValue> facilitylist=null;
            List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
			request.setAttribute("fromDateStr", fromDate);
			request.setAttribute("thruDateStr", thruDate);
			
			if(fromDate!=null && thruDate!=null){
				String []f=fromDate.split(" ");
				String []e=thruDate.split(" ");
				request.setAttribute("start", f[0]);
				request.setAttribute("end", e[0]);
			}
			
		    String facilityId=UtilProperties.getPropertyValue("general","posstore.usefaclityId");
			 
		     BigDecimal   totalExpense=getExpense(facilityId,fromDate,thruDate,delegator);
			 BigDecimal   cashTotal = totalCash(facilityId,fromDate,thruDate,delegator);
			 BigDecimal   creditTotal = totalCredit(facilityId,fromDate,thruDate,delegator);
			 BigDecimal dueCollection=dueCredits(delegator,facilityId,fromDate,thruDate);
			 BigDecimal totcashPlusDue=BigDecimal.ZERO;
			 BigDecimal totcashPlusCredit=BigDecimal.ZERO;
			 
			 totalExpense=totalExpense.setScale(2, RoundingMode.HALF_DOWN);
			 cashTotal=cashTotal.setScale(2, RoundingMode.HALF_DOWN);
			 creditTotal=creditTotal.setScale(2, RoundingMode.HALF_DOWN);
			 dueCollection=dueCollection.setScale(2, RoundingMode.HALF_DOWN);
			 
			 totcashPlusDue=totcashPlusDue.add(cashTotal).add(dueCollection);
			 totcashPlusCredit=totcashPlusCredit.add(cashTotal).add(creditTotal);
			 
			 HashMap<String,String> items=new HashMap<String, String>();
              items.put("totalExpense", totalExpense.toString());
              items.put("cashTotal", cashTotal.toString());
              items.put("creditTotal", creditTotal.toString());
              items.put("dueCollection", dueCollection.toString());
              items.put("totcashPlusDue", totcashPlusDue.toString());
              items.put("totcashPlusCredit", totcashPlusCredit.toString());
         	 HashMap<String,String> map=new HashMap<String, String>();
         	 List<String> expensesIds=null;
             BigDecimal totalExpenseById=BigDecimal.ZERO;
         	 try{
            	 List <GenericValue> expenses=(List <GenericValue>)delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "POS_PAID_REASON_OUT"));
            	  expensesIds=EntityUtil.getFieldListFromEntityList(expenses,"enumId", false);
            	 for(String expenseId : expensesIds){
            		 BigDecimal expenseAmt=getExpenseByExpenseId(facilityId,fromDate,thruDate,delegator,expenseId); 
            		 totalExpenseById=totalExpenseById.add(expenseAmt);
            		 map.put(expenseId, expenseAmt.toString());
            	 } 
              }catch (Exception e) {
                 e.printStackTrace();
              }
              request.setAttribute("DSRCashSummary",items);
              request.setAttribute("ExpenseMap",map);
              request.setAttribute("ExpenseList",expensesIds);
              request.setAttribute("totalExpenseById",totalExpenseById.toString());

              return "success";
	}   

   public static BigDecimal getExpenseByExpenseId(String facilityId,String fromDate,String thruDate,GenericDelegator delegator,String expenseId){
		  BigDecimal totalExpense=BigDecimal.ZERO;
		
	      List details = null;
	      List exprs = null;
	      List paidsIn = new ArrayList();
	      List paidsOut = new ArrayList();
	      List paidsInOutPrice = new ArrayList();
	      
		        try {
/*					exprs = UtilMisc.toList(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)),
											EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)),
											EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "POSTX_PAID_OUT"));
*/
					
		        	exprs = UtilMisc.toList(EntityCondition.makeCondition("logEndDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)),
							EntityCondition.makeCondition("logEndDateTime", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "POSTX_PAID_OUT"));

					details = delegator.findByCondition("PosTerminalLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, null);
					Iterator i = details.iterator();
			        while(i.hasNext()){
			        	GenericValue detail = (GenericValue)i.next();
			        	if(detail.get("statusId").equals("POSTX_PAID_OUT")){
			        		GenericValue priceDetail =delegator.findByPrimaryKey("PosTerminalInternTx", UtilMisc.toMap("posTerminalLogId", (String)detail.get("posTerminalLogId")));
			        		if(priceDetail != null){
			        		String	reasonId=priceDetail.get("reasonEnumId").toString();
			        			if(expenseId.equals(reasonId)){
			        		       totalExpense = totalExpense.add(priceDetail.getBigDecimal("paidAmount"));
			        		    }
			        		}
			        	  }
			        	}
		        if(totalExpense==null){
		        	totalExpense=BigDecimal.ZERO;
		          }
		        } catch (GenericEntityException e) {
					e.printStackTrace();
				} catch (GeneralException e) {
					e.printStackTrace();
				}
		  return  totalExpense;
	  } 
   
   //get the DSR report for Fnp
	public static  String DSRReportFNP(HttpServletRequest request, HttpServletResponse response){
        GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
		BigDecimal freshFlowerSale = BigDecimal.ZERO;
        BigDecimal freshFlowerCumulativeSale = BigDecimal.ZERO;
        BigDecimal handiCraftSale = BigDecimal.ZERO;
        BigDecimal handiCraftCumulativeSale = BigDecimal.ZERO;
        BigDecimal upSaleOrOtherPurchseSale = BigDecimal.ZERO;
        BigDecimal upSaleOrOtherPurchseCumulativeSale = BigDecimal.ZERO;
        BigDecimal inStoreEcommOrderSale = BigDecimal.ZERO;
        BigDecimal inStoreEcommOrderCumulativeSale=BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        BigDecimal expenseCumulative=BigDecimal.ZERO;
        BigDecimal totalNoOfQty=BigDecimal.ZERO;
        BigDecimal cumulativetotalNoOfQty=BigDecimal.ZERO;
        
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalTaxCumulative = BigDecimal.ZERO;
        
        BigDecimal FreshFlowerPurchse=BigDecimal.ZERO;
        BigDecimal cumulativeFreshFlowerPurchse=BigDecimal.ZERO;
        
        String fromDate = request.getParameter("minDate");
		String thruDate  = request.getParameter("maxDate");
		Date date = new Date();
	    fromDate = request.getParameter("minDate");
		thruDate  = request.getParameter("maxDate");
			Integer intDay = new Integer(date.getDate());
			Integer intMonth = new Integer(date.getMonth()+1);
			Integer intYear = new Integer(date.getYear()+1900);
			
			String  day = intDay.toString();
			String  month = intMonth.toString();
			String  year = intYear.toString();
			if(fromDate == null)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			if(fromDate != null && fromDate.length()<19)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			
			if(thruDate == null)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			if(thruDate != null && thruDate.length()<19)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			
			List<GenericValue> facilitylist=null;
            List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
			request.setAttribute("fromDateStr", fromDate);
			request.setAttribute("thruDateStr", thruDate);
			if(fromDate!=null && thruDate!=null){
				String []f=fromDate.split(" ");
				String []e=thruDate.split(" ");
				request.setAttribute("start", f[0]);
				request.setAttribute("end", e[0]);
			}

			try {
				    String facilityId=UtilProperties.getPropertyValue("general","posstore.usefaclityId");
            	    HashMap<String,String> items=new HashMap<String, String>();
			        String NumOfSale= getNumberOfSale(facilityId,fromDate,thruDate,delegator);
				    String cumulativeSale=getNumberOfSale(facilityId,null,null,delegator);
			           
				        handiCraftSale=totalHandiCraftSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);
				        //System.out.println("###############################         handiCraftSale      "+handiCraftSale);
			            handiCraftSale=handiCraftSale.setScale(2, RoundingMode.HALF_DOWN);
			            
			            handiCraftCumulativeSale=totalHandiCraftSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);
			            handiCraftCumulativeSale=handiCraftCumulativeSale.setScale(2, RoundingMode.HALF_DOWN);
			            
			           
			            upSaleOrOtherPurchseSale= totalOtherPurchaseSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
			            upSaleOrOtherPurchseSale=upSaleOrOtherPurchseSale.setScale(2, RoundingMode.HALF_DOWN);
			            
			            upSaleOrOtherPurchseCumulativeSale=totalOtherPurchaseSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
			            upSaleOrOtherPurchseCumulativeSale=upSaleOrOtherPurchseCumulativeSale.setScale(2, RoundingMode.HALF_DOWN);
			            
			            freshFlowerSale= getFreshFlowerSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
			            freshFlowerSale=freshFlowerSale.setScale(2,RoundingMode.HALF_DOWN);
			            
			            freshFlowerCumulativeSale= getFreshFlowerSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
			            freshFlowerCumulativeSale=freshFlowerCumulativeSale.setScale(2,RoundingMode.HALF_DOWN);
			            
			            inStoreEcommOrderSale= getEcommerceOrderSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
			            inStoreEcommOrderSale=inStoreEcommOrderSale.setScale(2,RoundingMode.HALF_DOWN);
			            
			            inStoreEcommOrderCumulativeSale= getEcommerceOrderSale(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
			            inStoreEcommOrderCumulativeSale=inStoreEcommOrderCumulativeSale.setScale(2,RoundingMode.HALF_DOWN);

			            expense= getExpenseDailyAndCumutative(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
			            expense=expense.setScale(2,RoundingMode.HALF_DOWN);

			            expenseCumulative= getExpenseDailyAndCumutative(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
			            expenseCumulative=expenseCumulative.setScale(2,RoundingMode.HALF_DOWN);

			            totalNoOfQty= totalNoOfQty(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
				        cumulativetotalNoOfQty= totalNoOfQty(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
				        
				        FreshFlowerPurchse= getFreshFlowerPurchse(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);		        
				        FreshFlowerPurchse=FreshFlowerPurchse.setScale(2,RoundingMode.HALF_DOWN);

				        cumulativeFreshFlowerPurchse= getFreshFlowerPurchse(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);		        
				        cumulativeFreshFlowerPurchse=cumulativeFreshFlowerPurchse.setScale(2,RoundingMode.HALF_DOWN);
				      
				        totalTax=totalTax(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),fromDate,thruDate,delegator);
				        totalTax=totalTax.setScale(2, RoundingMode.HALF_DOWN);
				        
				        
				        totalTaxCumulative=totalTax(UtilProperties.getPropertyValue("general","posstore.usefaclityId"),null,thruDate,delegator);
				        totalTaxCumulative=totalTaxCumulative.setScale(2, RoundingMode.HALF_DOWN);
				        
				        
				        BigDecimal total = freshFlowerSale.add(handiCraftSale).add(upSaleOrOtherPurchseSale).add(inStoreEcommOrderSale).add(totalTax);
				        BigDecimal cumulativeTotal = freshFlowerCumulativeSale.add(handiCraftCumulativeSale).add(upSaleOrOtherPurchseCumulativeSale).add(inStoreEcommOrderCumulativeSale).add(totalTaxCumulative);
				        
				        //System.out.println("###########       total    "+total);
				        //System.out.println("###########       cumulativeTotal    "+cumulativeTotal);
				        
				      items.put("facilityId",UtilProperties.getPropertyValue("general","posstore.usefaclityId"));
			          items.put("FreshFlowerPurchse",FreshFlowerPurchse.toString());
			          items.put("cumulativeFreshFlowerPurchse",cumulativeFreshFlowerPurchse.toString());
				      items.put("numberOfInvoice",NumOfSale);
			          items.put("CNOI",cumulativeSale);
			          items.put("totalNoOfQty",totalNoOfQty.toString());
			          items.put("cumulativetotalNoOfQty",cumulativetotalNoOfQty.toString());
			          items.put("handiCraftSale",handiCraftSale.toString());
			          items.put("handiCraftCumulativeSale",handiCraftCumulativeSale.toString());
			          items.put("upSaleOrOtherPurchseSale",upSaleOrOtherPurchseSale.toString());
			          items.put("upSaleOrOtherPurchseCumulativeSale",upSaleOrOtherPurchseCumulativeSale.toString());
			          items.put("facilityName",UtilProperties.getPropertyValue("general","posstore.usefaclityId"));
			          items.put("freshFlowerSale",freshFlowerSale.toString());
					  items.put("freshFlowerCumulativeSale",freshFlowerCumulativeSale.toString());
					  items.put("inStoreEcommOrderSale",inStoreEcommOrderSale.toString());
					  items.put("inStoreEcommOrderCumulativeSale",inStoreEcommOrderCumulativeSale.toString());
					  items.put("expense",expense.toString());
					  items.put("expenseCumulative",expenseCumulative.toString());  
					  
					  items.put("totalTax",totalTax.toString());
					  items.put("totalTaxCumulative",totalTaxCumulative.toString());
					  items.put("total",total.setScale(2, RoundingMode.HALF_DOWN).toString());
					  items.put("cumulativeTotal",cumulativeTotal.setScale(2, RoundingMode.HALF_DOWN).toString());
					  
					  
					  
					  
			            //Handi craft Items Report
                     List<HashMap<String,String>>  listhandicraft=new ArrayList<HashMap<String,String>>();			           
				         LocalDispatcher dispatcher=(LocalDispatcher)request.getAttribute("dispatcher") ; 
				        String []category=new String[]{"BKT","CND","CV","IGV","IB"}; 
				        BigDecimal totaltarget=BigDecimal.ZERO;
				        BigDecimal totalsale=BigDecimal.ZERO;
				        BigDecimal totalCumulative=BigDecimal.ZERO;
				        BigDecimal totpercentage=BigDecimal.ZERO;
				        
				        for(int i=0;i<category.length;i++){ 
				        	HashMap<String,String> map=handicraftCategoryCalulation(category[i],facilityId,fromDate,thruDate,delegator,dispatcher);
				        	if(UtilValidate.isNotEmpty(map))
				        	{
				        		totaltarget=totaltarget.add(new BigDecimal(map.get("totalPurchase")).setScale(2,RoundingMode.HALF_DOWN));
					        	totalsale=totalsale.add(new BigDecimal(map.get("totalHandiCraftSale")).setScale(2,RoundingMode.HALF_DOWN));
					        	totalCumulative=totalCumulative.add(new BigDecimal(map.get("totalHandiCraftCumulativeSale")).setScale(2,RoundingMode.HALF_DOWN));
					        	listhandicraft.add(map);	
				        	}
				        }
				        totaltarget=totaltarget.setScale(2,RoundingMode.HALF_DOWN);
				        totalsale=totalsale.setScale(2,RoundingMode.HALF_DOWN);
				        totalCumulative=totalCumulative.setScale(2,RoundingMode.HALF_DOWN);

				        HashMap<String,String> totmap=new HashMap<String, String>();
				        totmap.put("totaltarget", totaltarget.toString());
				        totmap.put("totalsale", totalsale.toString());
				        totmap.put("totalCumulative", totalCumulative.toString());
				       request.setAttribute("DSR",items);
			           request.setAttribute("listhandicraft",listhandicraft);
				       request.setAttribute("totmap",totmap);
            
            } catch (Exception e) {
				e.printStackTrace();
			}			
		return "success";
	}		

  // get the handicraft calculation daily and cumulative 
   public static  HashMap<String,String> handicraftCategoryCalulation(String categoryId,String facilityId,String fromDate,String thruDate,GenericDelegator delegator,LocalDispatcher dispatcher)
   {
	    HashMap<String,String> items=new HashMap<String, String>();
	    BigDecimal totalPurchase=BigDecimal.ZERO;
  	   	BigDecimal totalHandiCraftSale=BigDecimal.ZERO;
  	   	BigDecimal totalHandiCraftCumulativeSale=BigDecimal.ZERO;

	    try {
		    Map paramInMap=new HashMap();
		    	paramInMap.put("productCategoryId", categoryId);
		    	paramInMap.put("defaultViewSize", 1000);
		    	paramInMap.put("limitView", false);
		    	
	    	Map outMap = dispatcher.runSync("getProductCategoryAndLimitedMembers", paramInMap);
		    List<GenericValue>products=(List<GenericValue>)outMap.get("productCategoryMembers");
			TransactionUtil.begin();
			if(UtilValidate.isNotEmpty(products))
		    for(GenericValue gv:products){
		      String productId = (String) gv.getString("productId");
		      Map  resultOutput = dispatcher.runSync("getInventoryAvailableByFacility",UtilMisc.toMap("productId",productId,"facilityId",facilityId));
			  BigDecimal  quantityOnHandTotal =(BigDecimal)resultOutput.get("quantityOnHandTotal");
			 List <GenericValue> priceList =delegator.findByAnd("ProductPrice",UtilMisc.toMap("productId",productId));
  	         for(GenericValue pp:priceList){
  	        	 //here we have to calculate based on cost price or purchase price
  	        	 if(pp.getString("productPriceTypeId").equals("DEFAULT_PRICE")){
	    		  totalPurchase=totalPurchase.add(pp.getBigDecimal("price").multiply(quantityOnHandTotal));
	         }
	       }
  	         // get date wise product sale...
  	        
  	        //get cumulative product sale in this category...
  	       totalHandiCraftSale=totalHandiCraftSale.add(totalHandiCraftSaleByProduct(facilityId,productId,fromDate,thruDate,delegator));
  	       totalHandiCraftCumulativeSale=totalHandiCraftCumulativeSale.add(totalHandiCraftSaleByProduct(facilityId,productId,null,thruDate,delegator));
  	       
	  }
		 TransactionUtil.commit();
		items.put("totalPurchase", totalPurchase.toString());    
		items.put("totalHandiCraftSale", totalHandiCraftSale.toString());    
		items.put("totalHandiCraftCumulativeSale", totalHandiCraftCumulativeSale.toString());    
        GenericValue cat=delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId",categoryId));
        if(UtilValidate.isNotEmpty(cat))
        	items.put("productCategoryName", cat.getString("categoryName"));
        else items.put("productCategoryName", "");   
	   } catch (Exception e) {
			e.printStackTrace();
		}
       //System.out.println("########### MAP ###############\n\n"+items);
	   return items;
   }

   public static BigDecimal totalHandiCraftSaleByProduct(String facilityId,String productId,String fromDate,String thruDate,GenericDelegator delegator){
		BigDecimal totalHandiCraftSale=BigDecimal.ZERO;
	   
		try{
		    //List exprs = FastList.newInstance();
		    List exprs = new ArrayList();
			List dateCondiList = new ArrayList();
			if(fromDate!=null && thruDate!=null){
			 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			 exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
			}
			
			if(thruDate!=null && fromDate==null){
				exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
			}

  		    exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		    exprs.add(EntityCondition.makeCondition("isHandiCrafts", EntityOperator.EQUALS, "Y"));
	        List <GenericValue>transList = null;
	        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
	        for(GenericValue order:transList){
	        	if(order.getString("isHandiCrafts")!=null && order.getString("isHandiCrafts").equals("Y")){
	               BigDecimal price=order.getBigDecimal("unitPrice");
	               BigDecimal qty=order.getBigDecimal("quantity");
	        		if(price!=null && price!=null){
	        		totalHandiCraftSale=totalHandiCraftSale.add(price.multiply(qty));
	        	  }
	        	}
	        }
		}
		catch (Exception e) {
	         e.printStackTrace();
		}
		return totalHandiCraftSale;
	}

   
   
   
public static String getNumberOfSale(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	
	Integer size=0;
	List dateCondiList = new ArrayList();
	//List exprs = FastList.newInstance();
	List exprs = new ArrayList();

	try {
		if(fromDate!=null && thruDate!=null){
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
		List orderBy = UtilMisc.toList("-createdStamp");
        List <GenericValue>transList = null;
        //System.out.println("######### CONDITION#####"+exprs);
        transList = delegator.findList("OrderHeader", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
	
	if(transList!=null){
		size=transList.size();
	 }
	} catch (Exception e) {
		e.printStackTrace();
	}
	return size.toString();
}


public static BigDecimal totalNoOfQty(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	BigDecimal totalNoOfQty=BigDecimal.ZERO;
	try{
	   // List exprs = FastList.newInstance();
	    List exprs = new ArrayList();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		}
	    exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        for(GenericValue order:transList){
               BigDecimal price=order.getBigDecimal("unitPrice");
               BigDecimal qty=order.getBigDecimal("quantity");
               totalNoOfQty=totalNoOfQty.add(qty);
        	}
        
	}
	catch (Exception e) {
         e.printStackTrace();
	}
	return totalNoOfQty;
}




public static String otherPuchase(HttpServletRequest request,HttpServletResponse response){
	BigDecimal totalOtherPurchase=BigDecimal.ZERO;
	String facilityId=request.getParameter("facilityId");
	String fromDate=request.getParameter("fromDate");
	String thruDate=request.getParameter("thruDate");
	GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");

	try{
	   // List exprs = FastList.newInstance();
	    List exprs = new ArrayList();
		List dateCondiList = new ArrayList();
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
	    exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	    exprs.add(EntityCondition.makeCondition("isOtherPurchase", EntityOperator.EQUALS, "Y"));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        request.setAttribute("orderItems", transList);
	}
	catch (Exception e) {
         e.printStackTrace();
	}
	return "success";
}
//gives daily and cumulative Handi Craft Sale sales

public static BigDecimal totalHandiCraftSale(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	BigDecimal totalHandiCraftSale=BigDecimal.ZERO;
	Connection conn=null;
	//String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
	String query="SELECT PC.CATEGORY_NAME,SUM(OI.QUANTITY*OI.UNIT_PRICE)  FROM order_header OH  "+
			"LEFT OUTER JOIN order_item OI ON OI.ORDER_ID=OH.ORDER_ID  "+
			"LEFT OUTER JOIN product_category_member PCM ON OI.PRODUCT_ID=PCM.PRODUCT_ID  "+
			"LEFT OUTER JOIN product_category PC ON PCM.PRODUCT_CATEGORY_ID=PC.PRODUCT_CATEGORY_ID WHERE PCM.PRODUCT_CATEGORY_ID != '"+
			UtilProperties.getPropertyValue("general","posstore.productCategoryId")+"' "+
			" AND origin_Facility_Id = '"+facilityId+"";
	
	
	if(fromDate!=null && thruDate!=null){
		query = query +"' AND OH.order_date >= '"+fromDate+"' AND OH.order_date < '"+thruDate+"'";
	}
	else if(thruDate!=null && fromDate==null){
		query = query +"' AND OH.order_date < '"+thruDate+"'";
	}
	else
		query = query +"'";
	
	try{
     	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz"));
	    PreparedStatement ps=conn.prepareStatement(query);
	    ResultSet rs=ps.executeQuery();
	    if(rs != null)
	    while (rs.next()) {
	    	totalHandiCraftSale = rs.getBigDecimal(2);
	    	if(totalHandiCraftSale == null)totalHandiCraftSale = BigDecimal.ZERO;
	    }
	}catch (Exception e) {
		e.printStackTrace();
	}
	finally{
	    try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	
   /*
	try{
	    List exprs = FastList.newInstance();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			
		}
		
	    exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	    exprs.add(EntityCondition.makeCondition("isHandiCrafts", EntityOperator.EQUALS, "Y"));
	    //System.out.println("##### EXPERS HNADI CRAFTS ######"+exprs); 
	    List <GenericValue>transList = null;
        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        for(GenericValue order:transList){
        	if(order.getString("isHandiCrafts")!=null && order.getString("isHandiCrafts").equals("Y")){
               BigDecimal price=order.getBigDecimal("unitPrice");
               BigDecimal qty=order.getBigDecimal("quantity");
        		if(price!=null && price!=null){
        		totalHandiCraftSale=totalHandiCraftSale.add(price.multiply(qty));
        	  }
        	}
        }
	}
	catch (Exception e) {
         e.printStackTrace();
	}*/
	return totalHandiCraftSale;
}


//gives daily and cumulative other purchase 

public static BigDecimal totalOtherPurchase(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	BigDecimal totalOtherPurchase=BigDecimal.ZERO;
	try{
	  //  List exprs = FastList.newInstance();
	    List exprs = new ArrayList();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	    exprs.add(EntityCondition.makeCondition("isOtherPurchase", EntityOperator.EQUALS, "Y"));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        for(GenericValue order:transList){
        	if(order.getString("isOtherPurchase")!=null && order.getString("isOtherPurchase").equals("Y")){
        		//List<GenericValue> price=delegator.findByAnd("ProductPrice",UtilMisc.toMap("productPriceTypeId", "DEFAULT_PRICE","productId",order.getString("productId")));
        	/*if(price!=null && price.size()>0){
        		totalOtherPurchase=totalOtherPurchase.add(price.get(0).getBigDecimal("price"));
        	  }*/
        		totalOtherPurchase=totalOtherPurchase.add(order.getBigDecimal("unitPrice"));
        	}
        }
	}
	catch (Exception e) {
         e.printStackTrace();
	}
	return totalOtherPurchase;
}


//gives daily and cumulative other purchase sale

public static BigDecimal totalOtherPurchaseSale(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	BigDecimal totalOtherPurchaseSale=BigDecimal.ZERO;
	try{
	   // List exprs = FastList.newInstance();
	    List exprs = new ArrayList();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		
		if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		}
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	    exprs.add(EntityCondition.makeCondition("isOtherPurchase", EntityOperator.EQUALS, "Y"));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyOrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        for(GenericValue order:transList){
        	if(order.getString("isOtherPurchase")!=null && order.getString("isOtherPurchase").equals("Y")){
                BigDecimal price=order.getBigDecimal("unitPrice");
                BigDecimal qty=order.getBigDecimal("quantity");
         		if(price!=null && price!=null){
         			totalOtherPurchaseSale=totalOtherPurchaseSale.add(price.multiply(qty));
         	  }
        	}
        }
	}
	catch (Exception e) {
         e.printStackTrace();
	}
	return totalOtherPurchaseSale;
}

//gives daily and cumulative fresh flower sales
public static BigDecimal getFreshFlowerSale(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
    BigDecimal FreshFlowerSale=BigDecimal.ZERO;
	
    Connection conn=null;
	//String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
	String query="SELECT PC.CATEGORY_NAME,SUM(OI.QUANTITY*OI.UNIT_PRICE)  FROM order_header OH  "+
			"LEFT OUTER JOIN order_item OI ON OI.ORDER_ID=OH.ORDER_ID  "+
			"LEFT OUTER JOIN product_category_member PCM ON OI.PRODUCT_ID=PCM.PRODUCT_ID  "+
			"LEFT OUTER JOIN product_category PC ON PCM.PRODUCT_CATEGORY_ID=PC.PRODUCT_CATEGORY_ID WHERE PCM.PRODUCT_CATEGORY_ID = '"+
			UtilProperties.getPropertyValue("general","posstore.productCategoryId")+"' "+
			" AND origin_Facility_Id = '"+facilityId+"";
	
	
	if(fromDate!=null && thruDate!=null){
		query = query +"' AND OH.order_date >= '"+fromDate+"' AND OH.order_date < '"+thruDate+"'";
	}
	else if(thruDate!=null && fromDate==null){
		query = query +"' AND OH.order_date < '"+thruDate+"'";
	}
	else
		query = query +"'";
	try{
     	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz"));
	    PreparedStatement ps=conn.prepareStatement(query);
	    ResultSet rs=ps.executeQuery();
	    if(rs != null)
	    while (rs.next()) {
	    	FreshFlowerSale = rs.getBigDecimal(2);
	    	if(FreshFlowerSale == null) FreshFlowerSale = BigDecimal.ZERO;
	    	
	    }
	}catch (Exception e) {
		e.printStackTrace();
	}
    
    /*
    
    
    
    
    
    try{
	    List exprs = FastList.newInstance();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		 exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		}
		
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	    exprs.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, UtilProperties.getPropertyValue("general","posstore.productCategoryId")));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyDSROrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
         
        for(GenericValue order:transList){
                BigDecimal price=order.getBigDecimal("unitPrice");
                BigDecimal qty=order.getBigDecimal("quantity");
               
         		if(price!=null && price!=null){
         			FreshFlowerSale=FreshFlowerSale.add(price.multiply(qty));
         	  }
                
        }
	}
	catch (Exception e) {
         e.printStackTrace();
	}*/
     return FreshFlowerSale; 
}

public static int getFreshFlowerSaleCount(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
    BigDecimal FreshFlowerSale=BigDecimal.ZERO;
	int count=0;
    try{
	   // List exprs = FastList.newInstance();
	    List exprs =  new ArrayList();
		List dateCondiList = new ArrayList();
		if(fromDate!=null && thruDate!=null){
		 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		 dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		 exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
		}
		if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		}
		
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	   //exprs.add(EntityCondition.makeCondition("isFreshFlower", EntityOperator.EQUALS, "Y"));
		exprs.add(EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, UtilProperties.getPropertyValue("general","posstore.productCategoryId")));
        List <GenericValue>transList = null;
        transList = delegator.findList("MyDSROrders", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
        
        if(UtilValidate.isNotEmpty(transList))
        	count =transList.size();
        /*for(GenericValue order:transList){
        	if(order.getString("isFreshFlower")!=null && order.getString("isFreshFlower").equals("Y")){
                count=count+1;
        	}
        }*/
	}
	catch (Exception e) {
         e.printStackTrace();
	}
     return count; 
}


//get expense daily and cumulative
public static BigDecimal getExpenseDailyAndCumutative(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	  BigDecimal totalExpense=BigDecimal.ZERO;
	
    List details = null;
	// List exprs = FastList.newInstance();
	 List exprs = new ArrayList();
    List paidsIn = new ArrayList();
    List paidsOut = new ArrayList();
    List paidsInOutPrice = new ArrayList();
    
	        try {
	        	
	        	
/*	      if(fromDate!=null && thruDate!=null){
	        	exprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
	        	exprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
                 }	
            if(thruDate!=null && fromDate==null){
   			    exprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
   		    }*/
            
  	      if(fromDate!=null && thruDate!=null){
	        	exprs.add(EntityCondition.makeCondition("logEndDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
	        	exprs.add(EntityCondition.makeCondition("logEndDateTime", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
               }	
          if(thruDate!=null && fromDate==null){
 			    exprs.add(EntityCondition.makeCondition("logEndDateTime", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
 		    }
            
            

	        	exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "POSTX_PAID_OUT"));
				details = delegator.findByCondition("PosTerminalLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, null);
				Iterator i = details.iterator();
		        while(i.hasNext()){
		        	GenericValue detail = (GenericValue)i.next();
		        	if(detail.get("statusId").equals("POSTX_PAID_OUT")){
		        		GenericValue priceDetail =delegator.findByPrimaryKey("PosTerminalInternTx", UtilMisc.toMap("posTerminalLogId", (String)detail.get("posTerminalLogId")));
		        		if(priceDetail != null){
		        			totalExpense = totalExpense.add(priceDetail.getBigDecimal("paidAmount"));
		        			Map x = new HashMap();
		        			x.put("type", "PAIDOUT");
		        			x.put("price", priceDetail.getBigDecimal("paidAmount").toString());
		        			
		        			String enumDesc = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", priceDetail.get("reasonEnumId").toString())).getString("description");
		        			String comment = priceDetail.get("reasonComment").toString();
		        			
		        			x.put("comment", comment != null && comment.length() > 0 ? comment : enumDesc);
		        			x.put("time", detail.get("logEndDateTime"));
		        			paidsOut.add(x);
		        		}
		        	  }
		        	}
	        if(totalExpense==null){
	        	totalExpense=BigDecimal.ZERO;
	          }
	        } catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			}
	  return  totalExpense;
} 

//get daily Ecommerce order transfer and cumulative
public static BigDecimal getEcommerceOrderSale(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
    BigDecimal EcommerceOrderSale=BigDecimal.ZERO;
	// List exprs = FastList.newInstance();
	 List exprs = new ArrayList();
	List dateCondiList = new ArrayList();
	try {
      if(fromDate!=null && thruDate!=null){		
		dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
        }
     
      if(thruDate!=null && fromDate==null){
			exprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		}
		exprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS, facilityId));
	} catch (GeneralException e) {
		e.printStackTrace();
	}
    List orderBy = UtilMisc.toList("-createdStamp");
    List <GenericValue>transList = null;
    /*try {
		transList = delegator.findList("EcommOrders", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), null, orderBy, null, false);
	    for(GenericValue gv:transList){
	    	if(gv.getBigDecimal("orderamt")!=null){
	    		EcommerceOrderSale=EcommerceOrderSale.add(gv.getBigDecimal("orderamt"));
	    	}
	    }
    } catch (GenericEntityException e) {
		e.printStackTrace();
	}*/
     return EcommerceOrderSale;
}

//get daily and cumulative fresh flower purchase
public static BigDecimal getFreshFlowerPurchse(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	  BigDecimal totFreshFlowerPrice=BigDecimal.ZERO;
	   	 List <GenericValue>freshFlowerList=null;
	 	 List dateCondiList = new ArrayList();
		// List exprs = FastList.newInstance();
		 List exprs = new ArrayList();
	 	 try {
  			if(fromDate!=null && thruDate!=null){
	 		dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
  			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
  			exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
  			}
  			if(thruDate!=null && fromDate==null){
  				exprs.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
  			}

  			exprs.add(EntityCondition.makeCondition("isFreshFlower", EntityOperator.EQUALS, "Y"));
  	        freshFlowerList=delegator.findList("Product", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			    if(freshFlowerList!=null){
			    	  for(GenericValue fresh:freshFlowerList){
			    	    List<GenericValue> priceList=delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",fresh.getString("productId")));
			    	      for(GenericValue pp:priceList){
			    	    	  BigDecimal purchaseQty=fresh.getBigDecimal("freshFlowerQohQty");
			    	    	  if(pp.getString("productPriceTypeId").equals("DEFAULT_PRICE")){
			    	            totFreshFlowerPrice=totFreshFlowerPrice.add(pp.getBigDecimal("price").multiply(purchaseQty));
			    	         }
			    	      }
			    	  }
			}
	    }catch (Exception e) {
		 e.printStackTrace();
	  }
	  return totFreshFlowerPrice;
}


public static BigDecimal getFreshFlowerCount(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	     BigDecimal freshFlowerCount=BigDecimal.ZERO;
	   	 List <GenericValue>freshFlowerList=null;
	 	 List dateCondiList = new ArrayList();
		// List exprs = FastList.newInstance();
		 List exprs = new ArrayList();
	 	 try {
    			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
    			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
    			exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
    	        exprs.add(EntityCondition.makeCondition("isFreshFlower", EntityOperator.EQUALS, "Y"));
    	        freshFlowerList=delegator.findList("Product", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			    if(freshFlowerList!=null){
				  freshFlowerCount=new BigDecimal(freshFlowerList.size());
			}
	    }catch (Exception e) {
		 e.printStackTrace();
	 }
	  return freshFlowerCount;
}  

public static BigDecimal getFreshFlowerPrice(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	  BigDecimal totFreshFlowerPrice=BigDecimal.ZERO;
	   	 List <GenericValue>freshFlowerList=null;
	 	 List dateCondiList = new ArrayList();
		// List exprs = FastList.newInstance();
		 List exprs = new ArrayList();
	 	 try {
    			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
    			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
    			exprs.add(EntityCondition.makeCondition(dateCondiList, EntityOperator.AND));
    	        exprs.add(EntityCondition.makeCondition("isFreshFlower", EntityOperator.EQUALS, "Y"));
    	        freshFlowerList=delegator.findList("Product", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, false);
			    if(freshFlowerList!=null){
			    	  for(GenericValue fresh:freshFlowerList){
			    	    List<GenericValue> priceList=delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId",fresh.getString("productId")));
			    	      for(GenericValue pp:priceList){
			    	    	  if(pp.getString("productPriceTypeId").equals("DEFAULT_PRICE")){
			    	            totFreshFlowerPrice=totFreshFlowerPrice.add(pp.getBigDecimal("price"));
			    	         }
			    	      }
			    	  }
			}
	    }catch (Exception e) {
		 e.printStackTrace();
	  }
	  return totFreshFlowerPrice;
}

public static boolean checkProductInFacilty(String facilityId,String productId){
	
	return true;
}

public static BigDecimal getExpense(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
	  BigDecimal totalExpense=BigDecimal.ZERO;
	
      List details = null;
      List exprs = null;
      List paidsIn = new ArrayList();
      List paidsOut = new ArrayList();
      List paidsInOutPrice = new ArrayList();
      
	        try {
/*				exprs = UtilMisc.toList(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)),
										EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)),
										EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "POSTX_PAID_OUT"));
*/				

	  exprs = UtilMisc.toList(EntityCondition.makeCondition("logEndDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)),
				EntityCondition.makeCondition("logEndDateTime", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)),
				EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "POSTX_PAID_OUT"));
				

	        	details = delegator.findByCondition("PosTerminalLog", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, null);
				Iterator i = details.iterator();
		        while(i.hasNext()){
		        	GenericValue detail = (GenericValue)i.next();
		        	if(detail.get("statusId").equals("POSTX_PAID_OUT")){
		        		GenericValue priceDetail =delegator.findByPrimaryKey("PosTerminalInternTx", UtilMisc.toMap("posTerminalLogId", (String)detail.get("posTerminalLogId")));
		        		if(priceDetail != null){
		        			totalExpense = totalExpense.add(priceDetail.getBigDecimal("paidAmount"));
		        			Map x = new HashMap();
		        			x.put("type", "PAIDOUT");
		        			x.put("price", priceDetail.getBigDecimal("paidAmount").toString());
		        			
		        			String enumDesc = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", priceDetail.get("reasonEnumId").toString())).getString("description");
		        			String comment = priceDetail.get("reasonComment").toString();
		        			
		        			x.put("comment", comment != null && comment.length() > 0 ? comment : enumDesc);
		        			x.put("time", detail.get("logEndDateTime"));
		        			paidsOut.add(x);
		        		}
		        	  }
		        	}
	        if(totalExpense==null){
	        	totalExpense=BigDecimal.ZERO;
	          }
	        } catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (GeneralException e) {
				e.printStackTrace();
			}
	  return  totalExpense;
  } 
   
/*public static String exportInventoryResultToExcel(List invRes)
	{
		WorkBook workBook = new WorkBook();
		RangeStyle rangeStyle=null;
		int colour = Color.lightGray.getRGB();
		short borderColor = 2;
		try {
			workBook.insertSheets(0, 1);
			
			workBook.setText(1,0, "Location Name");
			workBook.setColWidthAutoSize(0, true);
			workBook.setText(1,1, "Item Code");
			
			workBook.setText(1,2, "Item Name");
			workBook.setColWidthAutoSize(2, true);
			workBook.setText(1,3, "Stock");
			workBook.setText(1,4, "Cost");
			
			workBook.setText(1,5, "Selling cost");
			workBook.setColWidthAutoSize(5, true);
			workBook.setText(1,6, "MRP");
			
			Iterator itr = invRes.iterator();
			int i=2;
			int j=0;
			while(itr.hasNext())
			{
				Map resultmap=(Map) itr.next();
				String facilityName=(String) resultmap.get("facilityName");
				String productId=(String) resultmap.get("productId");
				String productName=(String) resultmap.get("productName");
				String quantityOnHandTotal=(String) resultmap.get("quantityOnHandTotal");
				String wholesale=(String) resultmap.get("wholesale");
				String defaultprice=(String) resultmap.get("defaultprice");
				String mrp =(String) resultmap.get("mrp");
				workBook.setText(i,j, facilityName);
			    workBook.setText(i,j+1, productId);
			    workBook.setText(i,j+2, productName);
			    workBook.setText(i,j+3,String.valueOf(quantityOnHandTotal));
			    workBook.setText(i,j+4, wholesale);
			    workBook.setText(i,j+5, defaultprice);
			    workBook.setText(i,j+6, mrp);
			    i=i+1;
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return "success";
	}*/
  /*public static String exportInventoryResultToExcel2(HttpServletRequest request,HttpServletResponse response){
	  
	  String facilityId="";
      String productId1="";
		int i=0;
		int j=2;
		delegator = (GenericDelegator)request.getAttribute("delegator");
		dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		response.setContentType("application/excel");
        String reportType=request.getParameter("reportType");
		
		try {
			 response.setContentType("application/excel");
			 WorkbookSettings wbSettings = new WorkbookSettings();
			 WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
			 WritableSheet sheet=workbook.createSheet("mysheet", 0); 
			 WritableFont wfobj=new WritableFont(WritableFont.ARIAL,8, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,Colour.BLACK);
			 WritableCellFormat cfobj=new WritableCellFormat(wfobj);
			 cfobj.setBackground(Colour.LAVENDER);
			 cfobj.setWrap(true);
			 Label sales=new Label(0,1,"Location Name"); 
			 sales.setCellFormat(cfobj);
			 sheet.addCell(sales); 
			 Label location=new Label(1,1,"Item Code"); 
			 location.setCellFormat(cfobj);
			 sheet.addCell(location);                                      
			 Label cash=new Label(2,1,"Item Name"); 
			 cash.setCellFormat(cfobj);
			 sheet.addCell(cash);  
			 Label credit=new Label(3,1,"Stock"); 
			 credit.setCellFormat(cfobj);
			 sheet.addCell(credit);  
			 Label card=new Label(4,1,"Cost"); 
			 card.setCellFormat(cfobj);
			 sheet.addCell(card);  
			 Label total1=new Label(5,1,"Selling cost"); 
			 total1.setCellFormat(cfobj);
			 sheet.addCell(total1); 
			 Label mr=new Label(6,1,"MRP"); 
			 mr.setCellFormat(cfobj);
			 sheet.addCell(mr); 
			
			workBook.insertSheets(0, 1);
			workBook.setText(1,0, "Location Name");
			workBook.setColWidthAutoSize(0, true);
			workBook.setText(1,1, "Item Code");
			workBook.setText(1,2, "Item Name");
			workBook.setColWidthAutoSize(2, true);
			workBook.setText(1,3, "Stock");
			workBook.setText(1,4, "Cost");
			workBook.setText(1,5, "Selling cost");
			workBook.setColWidthAutoSize(5, true);
			workBook.setText(1,6, "MRP");
			
			if(reportType.equals("InventoryReport")){
			facilityId = request.getParameter("facilityId");
			if(facilityId == null)
			facilityId = "WebStoreWarehouse";
			//System.out.println("the facility Name java\n\n\n"+facilityId);
			productId1 = request.getParameter("productId");
			//System.out.println("the first if\n\n\n\n");
			List productIds = new ArrayList();
			if((productId1 != null && productId1.equals("")) || productId1 == null ){
			//System.out.println("the if is true\n\n\n\n");
			List cond = new ArrayList();
			 productIds = delegator.findList("Product", null, UtilMisc.toSet("productId","productName"),null,  null, false);
			}else{
			          productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId1+"%"),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
			       }
			//System.out.println("the productIds Name java\n\n\n"+productIds);
			GenericValue c =delegator.findByPrimaryKey("Facility",UtilMisc.toMap("facilityId", facilityId));
			String facilityName=c.getString("facilityName");
			//System.out.println("the facilityName java\n\n\n"+facilityName);
			String defaultprice="";
			String wholesale="";
			String mrp="";
			List inventoryResuts = new ArrayList();
			Iterator productIdsItr = productIds.iterator();
			TransactionUtil.begin();
			while(productIdsItr.hasNext()){
			GenericValue product =   (GenericValue) productIdsItr.next();
			String productId = (String) product.getString("productId");
			String productName = (String) product.getString("productName");
			Map fieldMap=UtilMisc.toMap("productId" ,productId, "facilityId", facilityId);
			Map resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", fieldMap);
			//System.out.println("the resultOutput Name java\n\n\n"+resultOutput);
			BigDecimal quantityOnHandTotal = (BigDecimal) resultOutput.get("quantityOnHandTotal");
			int l=quantityOnHandTotal.intValue();
			BigDecimal availableToPromiseTotal =(BigDecimal) resultOutput.get("availableToPromiseTotal");
			int l1=availableToPromiseTotal.intValue();
			if(l > 0|| l1 > 0 ){
			List<GenericValue> price2=delegator.findByCondition("ProductPrice",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),UtilMisc.toList("productPriceTypeId","price"),null);
			List price=EntityUtil.getFieldListFromEntityList(price2, "productPriceTypeId", true);
			//System.out.println("the price type id\n\n\n"+price);
			if (price.contains("DEFAULT_PRICE"))
			{
			List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
			priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"DEFAULT_PRICE"));
			priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
			List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
			Iterator priceItr = price1.iterator();

			while(priceItr.hasNext()){
			GenericValue price5 = (GenericValue) priceItr.next();
			defaultprice=price5.getString("price");
			}
			}
			else
			defaultprice="_NA_";
			if (price.contains("WHOLESALE_PRICE"))
			{
			List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
			priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"WHOLESALE_PRICE"));
			priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
			List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
			Iterator priceItr = price1.iterator();
			while(priceItr.hasNext()){
			GenericValue price5 = (GenericValue) priceItr.next();
			wholesale=price5.getString("price");
			 }
			}
			else
			wholesale="_NA_";
			if (price.contains("MAXIMUM_PRICE"))
			{
			List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
			priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"MAXIMUM_PRICE"));
			priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
			EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
			List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
			Iterator priceItr = price1.iterator();
			while(priceItr.hasNext()){
			GenericValue price5 = (GenericValue) priceItr.next();
			mrp=price5.getString("price");
			}
			}
			else
			mrp="_NA_";
			 Label sal=new Label(i,j,(String) facilityName);  
			  sheet.addCell(sal);
			  Label produ=new Label(i+1,j,(String) productId);  
			  sheet.addCell(produ);
			  Label productN=new Label(i+2,j,(String) productName);  
			  sheet.addCell(productN);
			  Label quantityOn=new Label(i+3,j,String.valueOf(quantityOnHandTotal));  
			  sheet.addCell(quantityOn);
			  Label wholesa=new Label(i+4,j,wholesale);  
			  sheet.addCell(wholesa);
			  Label defaultp=new Label(i+5,j,defaultprice);  
			  sheet.addCell(defaultp);
			  Label m=new Label(i+6,j,mrp);  
			  sheet.addCell(m);
			workBook.setText(i,j, facilityName);
			workBook.setText(i,j+1, productId);
			workBook.setText(i,j+2, productName);
			workBook.setText(i,j+3,String.valueOf(quantityOnHandTotal));
			workBook.setText(i,j+4, wholesale);
			workBook.setText(i,j+5, defaultprice);
			workBook.setText(i,j+6, mrp);
			i=i+1;
			}
			}//while close
			       TransactionUtil.commit();
			   }
					else{
					facilityId = request.getParameter("facilityId");
					if(facilityId == null)
					facilityId = "WebStoreWarehouse";
					productId1 = request.getParameter("productId");
					if(productId1 != null)
					if(!productId1.equals(""))
					{
					List productIds = new ArrayList();
					if((productId1 != null && productId1.equals("")) || productId1 == null )
					{
					List cond = new ArrayList();
					productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.NOT_EQUAL, null),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
					}
					else
					{
					productIds = delegator.findByCondition("Product",EntityCondition.makeCondition("productId", EntityOperator.LIKE, productId1+"%"),UtilMisc.toList("productId","productName"),UtilMisc.toList("productId") );
					}
					GenericValue c =delegator.findByPrimaryKey("Facility",UtilMisc.toMap("facilityId", facilityId));
					String facilityName=c.getString("facilityName");
					String defaultprice="";
					String wholesale="";
					String mrp="";
					List inventoryResuts = new ArrayList();
					Iterator productIdsItr = productIds.iterator();
					while(productIdsItr.hasNext()){
					GenericValue product =   (GenericValue) productIdsItr.next();
					String productId = (String) product.getString("productId");
					String productName = (String) product.getString("productName");
					Map  resultOutput = dispatcher.runSync("getInventoryAvailableByFacility", UtilMisc.toMap("productId" ,productId, "facilityId ", facilityId));
					Long quantityOnHandTotal = (Long) resultOutput.get("quantityOnHandTotal");
					Long availableToPromiseTotal =(Long) resultOutput.get("availableToPromiseTotal");
					if(quantityOnHandTotal > 0|| availableToPromiseTotal > 0 )
					{
					List<GenericValue> price2=delegator.findByCondition("ProductPrice",EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId),UtilMisc.toList("productPriceTypeId","price"),null);
					List price=EntityUtil.getFieldListFromEntityList(price2, "productPriceTypeId", true);
					if(price.contains("DEFAULT_PRICE"))
					{
					List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
					priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"DEFAULT_PRICE"));
					priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
					EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
					List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
					Iterator priceItr = price1.iterator();
					while(priceItr.hasNext())
					{
					GenericValue price5 = (GenericValue) priceItr.next();
					defaultprice=price5.getString("price");
					}
					}
					else
					defaultprice="_NA_";
					
					if (price.contains("WHOLESALE_PRICE"))
					{
					List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
					priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"WHOLESALE_PRICE"));
					priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
					EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
					List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
					Iterator priceItr = price1.iterator();
					while(priceItr.hasNext())
					{
					GenericValue price5 = (GenericValue) priceItr.next();
					wholesale=price5.getString("price");
					}
					}
					else
					wholesale="_NA_";
					if (price.contains("MAXIMUM_PRICE"))
					{
					List<EntityCondition> priceCondList = new ArrayList<EntityCondition>();
					priceCondList.add(EntityCondition.makeCondition("productPriceTypeId", EntityOperator.EQUALS,"MAXIMUM_PRICE"));
					priceCondList.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
					EntityCondition secCond = EntityCondition.makeCondition(priceCondList, EntityOperator.AND);
					List<GenericValue> price1=delegator.findByCondition("ProductPrice",secCond,UtilMisc.toList("price"),null);
					Iterator priceItr = price1.iterator();
					while(priceItr.hasNext())
					{
					GenericValue price5 = (GenericValue) priceItr.next();
					mrp=price5.getString("price");
					}
					}
					else
					mrp="_NA_";
					}
					
					}
					}
					}
					OutputStream out = response.getOutputStream();
					 workbook.write();
					 workbook.close();
					out.close();
					}
					catch (Exception e)
					{
					e.printStackTrace();
					}
	     return "success";
   }*/
  
  public static String createEcommOrder(HttpServletRequest request,HttpServletResponse response){
	  String orderId=request.getParameter("orderId");   
	  String recipientName=request.getParameter("recipientName");   
	  String recipientEmail=request.getParameter("recipientEmail");   
	  String recipientPhone=request.getParameter("recipientPhone");   
	  String recipientAddress=request.getParameter("recipientAddress");   
	  String diliveryDate=request.getParameter("diliveryDate");   
	  String orderamt=request.getParameter("orderamt");   
	  String statusId=request.getParameter("statusId");   
	  String facilityId=request.getParameter("facilityId"); 
	  GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
	  
	  try{
		GenericValue ecomm  = delegator.makeValue("EcommOrders");
		ecomm.set("ecommSeqId", delegator.getNextSeqId("EcommOrders"));
		ecomm.set("orderId", orderId);
		ecomm.set("recipientName", recipientName);
		ecomm.set("recipientEmail", recipientEmail);
		ecomm.set("recipientPhone", recipientPhone);
		ecomm.set("recipientAddress", recipientAddress);
		
		if(diliveryDate!=null && !(diliveryDate.equals(""))){
			//System.out.println("##### DELIVERY DATE ###########"+diliveryDate);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			java.util.Date myDt  = sdf.parse(diliveryDate +" 00:00:00"); 
		 ecomm.set("diliveryDate",myDt);
		}
		
		if(orderamt!=null && !(orderamt.equals("")))
		  ecomm.set("orderamt", orderamt);
		else
			ecomm.set("orderamt", null);	
		
		ecomm.set("statusId", statusId);
		ecomm.set("facilityId", facilityId);
		ecomm.set("receivedDate", UtilDateTime.nowTimestamp());
		
		 ecomm.create();
	  }
	  catch (Exception e) {
		  e.printStackTrace();
	}
	  return "success";
  }
  
  
  
  
  public static String calculatePendingCredits(HttpServletRequest request,HttpServletResponse response){
	   GenericDelegator delegator =(GenericDelegator)request.getAttribute("delegator");
       //List exprs = FastList.newInstance();
       List exprs = new ArrayList();
       exprs.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS, "EXT_CREDIT"));
       exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PAYMENT_NOT_RECEIVED"));
       exprs.add(EntityCondition.makeCondition("isCredit", EntityOperator.EQUALS, "Yes"));
       List orderBy = UtilMisc.toList("-createdStamp");
       List <GenericValue>transList = null;
       try {
           transList = delegator.findList("OrderHeaderAndPaymentPrefByCustomer", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, orderBy, null, false);
       } catch (GenericEntityException e) {
           Debug.logError(e, module);
       }
       request.setAttribute("pendingCredits", transList);
       BigDecimal totalPendingCredit=BigDecimal.ZERO;  
       for(GenericValue trs:transList){
       	totalPendingCredit=totalPendingCredit.add(trs.getBigDecimal("maxAmount"));
       }
       request.setAttribute("totalPendingCredit", totalPendingCredit.toString());
       return "success";
 }
	public static  String purchaseCSV(HttpServletRequest request, HttpServletResponse response){
		try{
			delegator = (GenericDelegator)request.getAttribute("delegator");
			
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			Date date = new Date();
			String reportType = request.getParameter("reportType");
			if(reportType != null){
				
				if(reportType.equalsIgnoreCase("Facility Sales Report")){
					fromDate = request.getParameter("minDate");
					thruDate  = request.getParameter("maxDate");

				}
				if(reportType.equalsIgnoreCase("Daily Sales Report")){
					fromDate = request.getParameter("minDate");
					thruDate  = request.getParameter("maxDate");
					
					Integer intDay = new Integer(date.getDate());
					Integer intMonth = new Integer(date.getMonth()+1);
					Integer intYear = new Integer(date.getYear()+1900);
					
					String  day = intDay.toString();
					String  month = intMonth.toString();
					String  year = intYear.toString();
						
					if(UtilValidate.isEmpty(fromDate))
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					if(fromDate != null && fromDate.length()<19)
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					
					if(UtilValidate.isEmpty(thruDate))
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					if(thruDate != null && thruDate.length()<19)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				}
				if(reportType.equalsIgnoreCase("Weekly Sales Report")){
					int week;
					try {
						week = Integer.parseInt(request.getParameter("week"));
					} catch (Exception e) {
						week = 01;
						Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
					}
					
					String month = request.getParameter("month");
					String year = request.getParameter("year");
					
					if(week == 01){
						fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
					}else if(week == 02){
						fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
					}else if(week == 03){
						fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
					}else if(week == 04){
						fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
					}
					
				}
				if(reportType.equalsIgnoreCase("Monthly Sales Report")){
					
					String month = request.getParameter("month");
					String year  = request.getParameter("year");
					int intfromYear, actualFromMonth;
					try {
						actualFromMonth  = Integer.parseInt( month );
						intfromYear = Integer.parseInt( year );
					} catch (Exception e) {
						Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
						actualFromMonth  = 01;
						intfromYear = 2012;
						e.printStackTrace();
					}
					int intmonth = actualFromMonth -1;
					
					GregorianCalendar calendar =new  GregorianCalendar();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.MONTH,intmonth);
					calendar.set(Calendar.YEAR,intfromYear);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(GregorianCalendar.MINUTE, 0);
					calendar.set(GregorianCalendar.SECOND, 0);
					Debug.logInfo("###### calendar "+calendar, module);
					
					fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
					thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
				
				}
				
			}

			List revenues = new ArrayList();
			List parentProducts = new ArrayList();
			
			HttpSession session = request.getSession();
			productStoreId = (String) session.getAttribute("productStoreId");
			List storeCondtion = new ArrayList();
			if(productStoreId != null)
			storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			
			List orderStatus = new ArrayList();
			orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
			orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
			EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
				
			List dateCondiList = new ArrayList();
			if(UtilValidate.isNotEmpty(fromDate))
			dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			if(UtilValidate.isNotEmpty(thruDate))
			dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

			List mainExprs = new ArrayList();
			
			if(storeCondtion.size()>0){
			mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
			}
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));				
			mainExprs.add(dateCondition);
			mainExprs.add(orderStatusCondition);
			
			if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
				mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
			}
			
			EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
			 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
           
			if(revenues != null){
				for(int i=0;i<revenues.size();i++){
				GenericValue parentProduct =(GenericValue) revenues.get(i);
				String parentProductId = parentProduct.getString("productId");
					if(!parentProducts.contains(parentProductId)){
						parentProducts.add(parentProductId);
					}
					
				}
			}

			response.setContentType("application/excel");
			if(reportType != null)
				response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
			else
			response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
			StringBuffer data = new StringBuffer();

			data.append("\n");
			data.append("#--------------------------------------------------------------");
			data.append("\n");
			if(productStoreId != null)
				data.append("Product Store Id : "+productStoreId);
			data.append("\n");
			if(reportType != null)
				data.append("Report : "+reportType);
			else
				data.append("Report : Revenue Report");
			data.append("\n");
			
			data.append("Date Range  : " + fromDate + " To " +  thruDate);
			data.append("\n");
			data.append("#--------------------------------------------------------------" );
			data.append("\n");
			data.append("\n");
			data.append(" Sl.No , Product ID, Product Name, Quantity Sold,Total Value Sold");
			data.append("\n");
			data.append("\n");
			
			if(parentProducts!=null && parentProducts.size()>0) {
				int slNumber = 0;
				Iterator itParentProducts = parentProducts.iterator();
				double total = 0;
				double totalquantitySold = 0 ;
				
				while(itParentProducts.hasNext()) {
					
					String parentProductId = (String) itParentProducts.next();
					double subTotal = 0;
					double quantitySold = 0 ;
				
					GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
					if(productGv==null) continue;
					
					slNumber++;
					data.append("\""+slNumber+"\"");
					data.append(",");
					data.append("\""+parentProductId+"\"");
					data.append(",");
					
					String productName = productGv.getString("productName");
					if(productName != null)
						productName = productName.replaceAll(",", "&");
					data.append("\""+productName+"\"");
					data.append(",");

					
						List condition = new ArrayList();
						condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
						EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

						mainExprs = new ArrayList();
						mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
						mainExprs.add(dateCondition);
						mainExprs.add(orderStatusCondition);
						mainExprs.add(conditionList);
						
						mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
						
						List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
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
					data.append(" Total,,,"+totalquantitySold+","+total);
			}
			
			OutputStream out = response.getOutputStream();
			out.write(data.toString().getBytes());
			out.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "success";
	}		
	
	public static  String purchaseHtml(HttpServletRequest request, HttpServletResponse response){
		try{
			delegator = (GenericDelegator)request.getAttribute("delegator");
			String buffer="";
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			Date date = new Date();
			String reportType = request.getParameter("reportType");
			if(reportType != null){
				
				if(reportType.equalsIgnoreCase("Facility Sales Report")){
					fromDate = request.getParameter("minDate");
					thruDate  = request.getParameter("maxDate");

				}
				if(reportType.equalsIgnoreCase("Daily Sales Report")){
					fromDate = request.getParameter("minDate");
					thruDate  = request.getParameter("maxDate");
					
					Integer intDay = new Integer(date.getDate());
					Integer intMonth = new Integer(date.getMonth()+1);
					Integer intYear = new Integer(date.getYear()+1900);
					
					String  day = intDay.toString();
					String  month = intMonth.toString();
					String  year = intYear.toString();
						
					if(UtilValidate.isEmpty(fromDate))
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					if(fromDate != null && fromDate.length()<19)
						fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
					
					if(UtilValidate.isEmpty(thruDate))
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					if(thruDate != null && thruDate.length()<19)
						thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
				}
				if(reportType.equalsIgnoreCase("Weekly Sales Report")){
					int week;
					try {
						week = Integer.parseInt(request.getParameter("week"));
					} catch (Exception e) {
						week = 01;
						Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
					}
					
					String month = request.getParameter("month");
					String year = request.getParameter("year");
					
					if(week == 01){
						fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
					}else if(week == 02){
						fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
					}else if(week == 03){
						fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
					}else if(week == 04){
						fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
						thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
					}
					
				}
				if(reportType.equalsIgnoreCase("Monthly Sales Report")){
					
					String month = request.getParameter("month");
					String year  = request.getParameter("year");
					int intfromYear, actualFromMonth;
					try {
						actualFromMonth  = Integer.parseInt( month );
						intfromYear = Integer.parseInt( year );
					} catch (Exception e) {
						Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
						actualFromMonth  = 01;
						intfromYear = 2012;
						e.printStackTrace();
					}
					int intmonth = actualFromMonth -1;
					
					GregorianCalendar calendar =new  GregorianCalendar();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.MONTH,intmonth);
					calendar.set(Calendar.YEAR,intfromYear);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(GregorianCalendar.MINUTE, 0);
					calendar.set(GregorianCalendar.SECOND, 0);
					Debug.logInfo("###### calendar "+calendar, module);
					
					fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
					thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
				
				}
				
			}

			List revenues = new ArrayList();
			List parentProducts = new ArrayList();
			
			HttpSession session = request.getSession();
			productStoreId = (String) session.getAttribute("productStoreId");
			List storeCondtion = new ArrayList();
			if(productStoreId != null)
			storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
			
			List orderStatus = new ArrayList();
			orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
			orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
			EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
				
			List dateCondiList = new ArrayList();
			if(UtilValidate.isNotEmpty(fromDate))
			dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			if(UtilValidate.isNotEmpty(thruDate))
			dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
			EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

			List mainExprs = new ArrayList();
			
			if(storeCondtion.size()>0){
			mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
			}
			mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));				
			mainExprs.add(dateCondition);
			mainExprs.add(orderStatusCondition);
			
			if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
				mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
			}
			
			EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
			 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
          
			if(revenues != null){
				for(int i=0;i<revenues.size();i++){
				GenericValue parentProduct =(GenericValue) revenues.get(i);
				String parentProductId = parentProduct.getString("productId");
					if(!parentProducts.contains(parentProductId)){
						parentProducts.add(parentProductId);
					}
					
				}
			}

			response.setContentType("application/excel");
			if(reportType != null)
				response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
			else
			response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
			StringBuffer data = new StringBuffer();
			PrintWriter out = response.getWriter();
			buffer="<table><tr><td class=label> <span>Sl.No</span></td><td class=label> <span>Product ID</span></td><td class=label> <span>Product Name</span></td><td class=label> <span>Quantity Sold</span></td><td class=label> <span>Total Value Sold</span></td></tr>";
			
			
			if(parentProducts!=null && parentProducts.size()>0) {
				int slNumber = 0;
				Iterator itParentProducts = parentProducts.iterator();
				double total = 0;
				double totalquantitySold = 0 ;
				
				while(itParentProducts.hasNext()) {
					
					String parentProductId = (String) itParentProducts.next();
					double subTotal = 0;
					double quantitySold = 0 ;
				
					GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
					if(productGv==null) continue;
					
					slNumber++;
					buffer=buffer+"<tr><td>"+slNumber+"</td>";
					buffer=buffer+"<td>"+parentProductId+"</td>";
					
					
					String productName = productGv.getString("productName");
					if(productName != null)
						productName = productName.replaceAll(",", "&");
					buffer=buffer+"<td>"+productName+"</td>";

					
						List condition = new ArrayList();
						condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
						EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

						mainExprs = new ArrayList();
						mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
						mainExprs.add(dateCondition);
						mainExprs.add(orderStatusCondition);
						mainExprs.add(conditionList);
						
						mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
						
						List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
						for(int i=0;i<orders.size();i++){

							GenericValue  OrderGV = (GenericValue)orders.get(i);
							quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
							subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
						}
						buffer=buffer+"<td>"+quantitySold+"</td>";
						buffer=buffer+"<td>"+subTotal+"</td>";
					
					total = total + subTotal;
					totalquantitySold = totalquantitySold + quantitySold;
					}
					
				buffer=buffer+"\n";
			}
			
			out.println(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "success";
	}		
	 public static  String SalesChart(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 String imagelocation=null;
		 try{
			
				Map sum=new HashMap();
				Map sum1=new HashMap();
				int k=1;
				
                delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					if(reportType.equalsIgnoreCase("Facility Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");

					}
					if(reportType.equalsIgnoreCase("Daily Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly Sales Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly Sales Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					
					}
					
				}

				List revenues = new ArrayList();
				List parentProducts = new ArrayList();
				
				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				List storeCondtion = new ArrayList();
				if(productStoreId != null)
				storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				
				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
					
				List dateCondiList = new ArrayList();
				if(UtilValidate.isNotEmpty(fromDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if(UtilValidate.isNotEmpty(thruDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				List mainExprs = new ArrayList();
				
				if(storeCondtion.size()>0){
				mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
				}
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));				
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
					mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
				}
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
            
				if(revenues != null){
					for(int i=0;i<revenues.size();i++){
					GenericValue parentProduct =(GenericValue) revenues.get(i);
					String parentProductId = parentProduct.getString("productId");
						if(!parentProducts.contains(parentProductId)){
							parentProducts.add(parentProductId);
						}
						
					}
				}

				
				
				
				if(parentProducts!=null && parentProducts.size()>0) {
					int slNumber = 0;
					Iterator itParentProducts = parentProducts.iterator();
					double total = 0;
					double totalquantitySold = 0 ;
				
					
					
					while(itParentProducts.hasNext()) {
						
						String parentProductId = (String) itParentProducts.next();
						double subTotal = 0;
						double quantitySold = 0 ;
					
						GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
						if(productGv==null) continue;
						
						slNumber++;
						
						
						String productName = productGv.getString("productName");
						if(productName != null)
							productName = productName.replaceAll(",", "&");
						
				              sum.put(k,parentProductId);
						

						
				              List condition = new ArrayList();
								condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
								EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

								mainExprs = new ArrayList();
								mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
								mainExprs.add(dateCondition);
								mainExprs.add(orderStatusCondition);
								mainExprs.add(conditionList);
								
								mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
								
								List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
								for(int i=0;i<orders.size();i++){

									GenericValue  OrderGV = (GenericValue)orders.get(i);
									quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
									subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
							
							}
						
						
						total = total + subTotal;
						totalquantitySold = totalquantitySold + quantitySold;
						sum1.put(k, totalquantitySold);
						k=k+1;
						}
						}
				  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
				  final String series1 = "Report";
			        for(int y=1;y<=sum.size();y++)
			        {
			        	dataset.addValue((Number) sum1.get(y),series1,sum.get(y).toString());
			        	
			        }
			        
		  final JFreeChart chart = ChartFactory.createBarChart3D(
		          reportType,       // chart title
		          "",                    // domain axis label
		          "",                   // range axis label
		          dataset,                   // data
		          PlotOrientation.VERTICAL,  // orientation
		          true,                      // include legend
		          true,                      // tooltips
		          false                      // urls
		      );
		  chart.setBackgroundPaint(Color.white);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	       // plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(4);
	       // plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	       /* final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );*/
	        renderer1.setSeriesPaint(0, gp0);
	       // renderer1.setSeriesPaint(1, gp1);
	        //renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
        //System.out.println("\n\n ######## the temp File prefix="+var);
        imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
						
				  }catch(Exception e){}
				  
				  
				 
				String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
		          String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
		          File file = new File(filePath);
		          // Destination directory
		          File dir = new File(imageServerPath);

		          File directory = new File(imageServerPath);
		          File []listFiles = directory.listFiles();
		          for (File file2 : listFiles) file2.delete();
		                            // Move file to new directory
		          boolean success = file.renameTo(new File(dir, file.getName()));
		          file.delete();
		         PrintWriter out=response.getWriter();
		         //System.out.println("the imageLocation java\n\n\n\n\n"+imagelocation);
		            out.println(imagelocation);
		          return "success";
		}		
	

	 public static  String purchaseChart(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 String imagelocation=null;
		 try{
				//System.out.println("radha\n\n\n\n\n\n");
				Map sum=new HashMap();
				Map sum1=new HashMap();
				int k=1;
				
                delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					if(reportType.equalsIgnoreCase("Facility Sales Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");

					}
					if(reportType.equalsIgnoreCase("Daily purchase Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly purchase Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly purchase Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					
					}
					
				}

				List revenues = new ArrayList();
				List parentProducts = new ArrayList();
				
				HttpSession session = request.getSession();
				productStoreId = (String) session.getAttribute("productStoreId");
				List storeCondtion = new ArrayList();
				if(productStoreId != null)
				storeCondtion.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
				
				List orderStatus = new ArrayList();
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_COMPLETED"));
				orderStatus.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
				EntityConditionList orderStatusCondition = EntityCondition.makeCondition(orderStatus, EntityOperator.OR);
					
				List dateCondiList = new ArrayList();
				if(UtilValidate.isNotEmpty(fromDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if(UtilValidate.isNotEmpty(thruDate))
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				List mainExprs = new ArrayList();
				
				if(storeCondtion.size()>0){
				mainExprs.add(EntityCondition.makeCondition(storeCondtion, EntityOperator.AND));
				}
				mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));				
				mainExprs.add(dateCondition);
				mainExprs.add(orderStatusCondition);
				
				if(reportType.equalsIgnoreCase("Facility Sales Report") && request.getParameter("facilityReportId")!=null && !(request.getParameter("facilityReportId").equals(""))){
					mainExprs.add(EntityCondition.makeCondition("originFacilityId", EntityOperator.EQUALS,request.getParameter("facilityReportId")));				
				}
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				 revenues = delegator.findByCondition("MyOrders",mainCondition,null,null );
                 //System.out.println("\n\n\n\n\n"+revenues+"\n\n\n\n\n");
				if(revenues != null){
					for(int i=0;i<revenues.size();i++){
					GenericValue parentProduct =(GenericValue) revenues.get(i);
					String parentProductId = parentProduct.getString("productId");
						if(!parentProducts.contains(parentProductId)){
							parentProducts.add(parentProductId);
						}
						
					}
				}

				
				
				
				if(parentProducts!=null && parentProducts.size()>0) {
					int slNumber = 0;
					Iterator itParentProducts = parentProducts.iterator();
					double total = 0;
					double totalquantitySold = 0 ;
				
					
					
					while(itParentProducts.hasNext()) {
						
						String parentProductId = (String) itParentProducts.next();
						double subTotal = 0;
						double quantitySold = 0 ;
					
						GenericValue productGv = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",parentProductId));
						if(productGv==null) continue;
						
						slNumber++;
						
						
						String productName = productGv.getString("productName");
						if(productName != null)
							productName = productName.replaceAll(",", "&");
						
				              sum.put(k,parentProductId);
						

						
				              List condition = new ArrayList();
								condition.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,parentProductId));
								EntityConditionList conditionList = EntityCondition.makeCondition(condition, EntityOperator.AND);

								mainExprs = new ArrayList();
								mainExprs.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
								mainExprs.add(dateCondition);
								mainExprs.add(orderStatusCondition);
								mainExprs.add(conditionList);
								
								mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
								
								List orders = delegator.findByCondition("MyOrders",mainCondition,null,null );
								for(int i=0;i<orders.size();i++){

									GenericValue  OrderGV = (GenericValue)orders.get(i);
									quantitySold = quantitySold + (OrderGV.getDouble("quantity").doubleValue());
									subTotal = subTotal + getOrderProductPrice(OrderGV.getString("productId"),OrderGV.getString("orderId"),OrderGV.getString("orderItemSeqId"));
							
							}
						
						
						total = total + subTotal;
						totalquantitySold = totalquantitySold + quantitySold;
						sum1.put(k, totalquantitySold);
						k=k+1;
						}
						}
				  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
				  final String series1 = "Report";
			        for(int y=1;y<=sum.size();y++)
			        {
			        	dataset.addValue((Number) sum1.get(y),series1,sum.get(y).toString());
			        	
			        }
			        
		  final JFreeChart chart = ChartFactory.createBarChart3D(
		          reportType,       // chart title
		          "",                    // domain axis label
		          "",                   // range axis label
		          dataset,                   // data
		          PlotOrientation.VERTICAL,  // orientation
		          true,                      // include legend
		          true,                      // tooltips
		          false                      // urls
		      );
		  chart.setBackgroundPaint(Color.white);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	       // plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getDomainAxis().setMaximumCategoryLabelWidthRatio(4);
	       // plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	       /* final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );*/
	        renderer1.setSeriesPaint(0, gp0);
	       // renderer1.setSeriesPaint(1, gp1);
	        //renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
          //System.out.println("\n\n ######## the temp File prefix="+var);
          imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
						
				  }catch(Exception e){}
				  
				  
				 
				String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
		          String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
		          File file = new File(filePath);
		          // Destination directory
		          File dir = new File(imageServerPath);

		          File directory = new File(imageServerPath);
		          File []listFiles = directory.listFiles();
		          for (File file2 : listFiles) file2.delete();
		                            // Move file to new directory
		          boolean success = file.renameTo(new File(dir, file.getName()));
		          file.delete();
		         PrintWriter out=response.getWriter();
		      
		            out.println(imagelocation);
		          return "success";
		}		
	 
	 
	 
	 public static  String customerChart(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 String imagelocation=null;
		 HttpSession session = request.getSession();
		 try{
				
				Map sum=new HashMap();
				Map sum1=new HashMap();
				int k=1;
				
                delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					
					if(reportType.equalsIgnoreCase("Daily customer Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly customer Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly customer Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					
					}
					
				}
                 
				List dateCondiList = new ArrayList();
				if(UtilValidate.isNotEmpty(fromDate))
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if(UtilValidate.isNotEmpty(thruDate))
				dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				 final String series1 = "New Customer";
			        final String series2 = "Existing Customer";
			       
				
				  try{
					 
				 double newCustPer = 0;
				 double existCustPer=0;
					  List<EntityCondition> condnList=new ArrayList();
					  List<EntityCondition> condnList1=new ArrayList();
					  BigDecimal t=BigDecimal.ZERO;
					
					  condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) )); 
						condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) )); 
						; 
						long s = delegator.findCountByCondition("Person",EntityCondition.makeCondition(condnList,EntityOperator.AND), null,null);
						long s1 = delegator.findCountByCondition("Person",EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ), null,null);
						long total = delegator.findCountByCondition("Person",null, null,null);
						  if(total!=0)
			                 {
			                	 if(s!=0)
			                  newCustPer=(100*s)/total;
			                	 if(s1!=0)
			                		 existCustPer=(100*s1)/total;
			                 }
						
						DefaultPieDataset dataset = new DefaultPieDataset();
						dataset.setValue("New Customer",newCustPer);
						dataset.setValue("Existing Customer",existCustPer);
		        
		          
		          // set up the chart
		    JFreeChart chart = ChartFactory.createPieChart3D
		    (reportType, dataset, true,true,true);
		     //chart.setBackgroundPaint(Color.magenta);
		     chart.setBorderVisible(false);
		     //chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		     
		     // get a reference to the plot for further customisation...
		     PiePlot3D plot = (PiePlot3D) chart.getPlot();
		     plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		     plot.setNoDataMessage("No data available");
		     plot.setCircular(false);
		     plot.setBackgroundPaint(Color.white);
		     plot.setLabelGap(0.02);
		     plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
		             "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
		    ));
		     // save as a png and return the file name
		     String var =  ServletUtilities.getTempFilePrefix();
		     //System.out.println("\n\n ######## the temp File prefix="+var);
		     imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
		    }
		    catch(Exception e)
		    {
		          e.printStackTrace();
		    }
		    String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
		    String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
		    File file = new File(filePath);
		    // Destination directory
		    File dir = new File(imageServerPath);

		    File directory = new File(imageServerPath);
		    File []listFiles = directory.listFiles();
		    for (File file2 : listFiles) file2.delete();
		                      // Move file to new directory
		    boolean success = file.renameTo(new File(dir, file.getName()));
		    file.delete();
		    PrintWriter out=response.getWriter();
		    out.println(imagelocation);
		   
		 
	 }catch(Exception e){}
	 return "success";
	 }
	 
	 public static  String customerCSV(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 String imagelocation=null;
		 HttpSession session = request.getSession();
		 try{
				
				Map sum=new HashMap();
				Map sum1=new HashMap();
				int k=1;
				
delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				if(reportType != null){
					
					
					if(reportType.equalsIgnoreCase("Daily customer Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly customer Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly customer Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					}
				}
				
				response.setContentType("application/excel");
				if(reportType != null)
					response.setHeader("Content-disposition","attachment;filename="+reportType.replaceAll(" ", "")+".csv");
				else
				response.setHeader("Content-disposition","attachment;filename=revenueReport.csv");
				StringBuffer data = new StringBuffer();

				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				if(productStoreId != null)
					data.append("Product Store Id : "+productStoreId);
				data.append("\n");
				if(reportType != null)
					data.append("Report : "+reportType);
				
				data.append("\n");
				
				data.append("Date Range  : " + fromDate + " To " +  thruDate);
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append(" Registration Date , Party ID, Name,User Login,Phone Number,Email Id,Postal Address");
				data.append("\n");
				data.append("\n");
				
				
				List dateCondiList = new ArrayList();
				if((ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)) != null)
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if((ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)) != null)
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.NOT_EQUAL, null));
				EntityConditionList dateCondition = null;
					if(dateCondiList.size()>0)
					dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);
					List registrations=new ArrayList();
				List mainExprs = new ArrayList();
				if(dateCondition != null)
				mainExprs.add(dateCondition);
				mainExprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				try{
			 registrations = delegator.findByCondition("PartyAndUserLoginAndPerson",mainCondition,null,UtilMisc.toList("createdDate DESC") );
				}catch(Exception e){
					e.printStackTrace();
				}
				List registrationsList = new ArrayList();
				Iterator registrationsItr = registrations.iterator();
				while(registrationsItr.hasNext()) {
					GenericValue gv = (GenericValue) registrationsItr.next();
					if(Report.hasPartyRoles(gv.getString("partyId"), "VISITOR", request) || Report.hasPartyRoles(gv.getString("partyId"), "SPONSOR", request)){
					}else{
						Map partyDetailsMap =  (Map)org.ofbiz.party.party.PartyHelper.getShippingContactdeatils(delegator, gv.getString("partyId"));
					    Map resistration = new HashMap();
					    resistration.put("createdDate", gv.getString("createdDate"));
					    resistration.put("partyId", gv.getString("partyId"));
					    resistration.put("PartyName", gv.getString("firstName") + " " + gv.getString("lastName"));
					    resistration.put("userLoginId", gv.getString("userLoginId"));
					    String telephoneHome = " ", emailId = "", postalAddress = "";
					    if(partyDetailsMap != null){
					    	if(partyDetailsMap.get("telephoneHome") != null){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneHome");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" +g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");
					    	}
					    	if(partyDetailsMap.get("telephoneWork")!=null && (telephoneHome.length()==0)){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneWork");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");			    		
					    	}
					    	if(partyDetailsMap.get("telephoneMobile")!=null && (telephoneHome.length()==0)){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneMobile");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");						    		
					    	}
					    	if(partyDetailsMap.get("primaryEmail")!=null)
					    	emailId = (String) partyDetailsMap.get("primaryEmail");
					    	if(partyDetailsMap.get("postalShippingAddress") != null){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("postalShippingAddress");
					    		if(g.getString("address1") != null)
					    		postalAddress = postalAddress + g.getString("address1") ;
					    		if(g.getString("address2") != null)
					    		postalAddress = postalAddress + " " + g.getString("address2");
					    		if(g.getString("city") != null)
					    		postalAddress = postalAddress + " " + g.getString("city");
					    		if(g.getString("countryGeoId")!= null)
					    		postalAddress = postalAddress + " " + g.getString("countryGeoId");
					    	}
					    	resistration.put("phoneNumber", telephoneHome);
			    			resistration.put("emailId", emailId);
							resistration.put("postalAddress", postalAddress);
							
							data.append(gv.getString("createdDate")+",");
							
							data.append(gv.getString("partyId")+",");
							if(gv.getString("firstName")!=null && gv.getString("lastName")!=null)
							data.append(gv.getString("firstName") + " " + gv.getString("lastName")+",");
							else
								data.append(gv.getString("firstName")+",");
							data.append(gv.getString("userLoginId")+",");
							data.append(telephoneHome+",");
							data.append(emailId+",");
							data.append(postalAddress+",");
					    	}//if
					}//else
					data.append("\n");
				}//while
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				    
				
		 
	 }catch(Exception e){}
	 return "success";
	 }
	 
	 
	 public static  String customerHtml(HttpServletRequest request, HttpServletResponse response) throws IOException{
		 String imagelocation=null;
		 HttpSession session = request.getSession();
		 try{
				
				Map sum=new HashMap();
				Map sum1=new HashMap();
				int k=1;
				
delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				Date date = new Date();
				String reportType = request.getParameter("reportType");
				List dateCondiList = new ArrayList();
				if(reportType != null){
					
					
					if(reportType.equalsIgnoreCase("Daily customer Report")){
						fromDate = request.getParameter("minDate");
						thruDate  = request.getParameter("maxDate");
						
						Integer intDay = new Integer(date.getDate());
						Integer intMonth = new Integer(date.getMonth()+1);
						Integer intYear = new Integer(date.getYear()+1900);
						
						String  day = intDay.toString();
						String  month = intMonth.toString();
						String  year = intYear.toString();
							
						if(UtilValidate.isEmpty(fromDate))
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						if(fromDate != null && fromDate.length()<19)
							fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
						
						if(UtilValidate.isEmpty(thruDate))
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
						if(thruDate != null && thruDate.length()<19)
							thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
					}
					if(reportType.equalsIgnoreCase("Weekly customer Report")){
						int week;
						try {
							week = Integer.parseInt(request.getParameter("week"));
						} catch (Exception e) {
							week = 01;
							Debug.logInfo("Week is not specified in the input, taking 1 as the deault", module);
						}
						
						String month = request.getParameter("month");
						String year = request.getParameter("year");
						
						if(week == 01){
							fromDate = year + "-" + month + "-" + "01" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "07" + " " + "23:59:59.999";
						}else if(week == 02){
							fromDate = year + "-" + month + "-" + "08" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "14" + " " + "23:59:59.999";
						}else if(week == 03){
							fromDate = year + "-" + month + "-" + "15" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "21" + " " + "23:59:59.999";
						}else if(week == 04){
							fromDate = year + "-" + month + "-" + "22" + " " + "00:00:00";
							thruDate  = year + "-" + month + "-" + "28" + " " + "23:59:59.999";
						}
						
					}
					if(reportType.equalsIgnoreCase("Monthly customer Report")){
						
						String month = request.getParameter("month");
						String year  = request.getParameter("year");
						int intfromYear, actualFromMonth;
						try {
							actualFromMonth  = Integer.parseInt( month );
							intfromYear = Integer.parseInt( year );
						} catch (Exception e) {
							Debug.logInfo("Month or year are not specified in the input, taking 1 as the deault", module);
							actualFromMonth  = 01;
							intfromYear = 2012;
							e.printStackTrace();
						}
						int intmonth = actualFromMonth -1;
						
						GregorianCalendar calendar =new  GregorianCalendar();
						calendar.set(Calendar.DAY_OF_MONTH, 1);
						calendar.set(Calendar.MONTH,intmonth);
						calendar.set(Calendar.YEAR,intfromYear);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(GregorianCalendar.MINUTE, 0);
						calendar.set(GregorianCalendar.SECOND, 0);
						Debug.logInfo("###### calendar "+calendar, module);
						
						fromDate = year + "-" + month + "-"+ "01" + " " + "00:00:00";
						thruDate  = year + "-" + month+"-"+ calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH) + " " + "23:59:59.999";
					}
				}
				PrintWriter out = response.getWriter();
				String buffer="<table><tr><td class=label> <span>Registration Date</span></td><td class=label> <span> Party ID</span></td><td class=label> <span>Name</span></td><td class=label> <span>User Login</span></td><td class=label> <span>Email Id</span></td><td class=label> <span>Postal Address</span></td></tr>";
			
			
				
				
				if((ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)) != null)
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				if((ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)) != null)
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.NOT_EQUAL, null));
				EntityConditionList dateCondition = null;
					if(dateCondiList.size()>0)
					dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);
					List registrations=new ArrayList();
				List mainExprs = new ArrayList();
				if(dateCondition != null)
				mainExprs.add(dateCondition);
				mainExprs.add(EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PERSON"));
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
				try{
			 registrations = delegator.findByCondition("PartyAndUserLoginAndPerson",mainCondition,null,UtilMisc.toList("createdDate DESC") );
				}catch(Exception e){
					e.printStackTrace();
				}
				List registrationsList = new ArrayList();
				Iterator registrationsItr = registrations.iterator();
				while(registrationsItr.hasNext()) {
					GenericValue gv = (GenericValue) registrationsItr.next();
					if(Report.hasPartyRoles(gv.getString("partyId"), "VISITOR", request) || Report.hasPartyRoles(gv.getString("partyId"), "SPONSOR", request)){
					}else{
						Map partyDetailsMap =  (Map)org.ofbiz.party.party.PartyHelper.getShippingContactdeatils(delegator, gv.getString("partyId"));
					    Map resistration = new HashMap();
					    resistration.put("createdDate", gv.getString("createdDate"));
					    resistration.put("partyId", gv.getString("partyId"));
					    resistration.put("PartyName", gv.getString("firstName") + " " + gv.getString("lastName"));
					    resistration.put("userLoginId", gv.getString("userLoginId"));
					    String telephoneHome = " ", emailId = "", postalAddress = "";
					    if(partyDetailsMap != null){
					    	if(partyDetailsMap.get("telephoneHome") != null){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneHome");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" +g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");
					    	}
					    	if(partyDetailsMap.get("telephoneWork")!=null && (telephoneHome.length()==0)){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneWork");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");			    		
					    	}
					    	if(partyDetailsMap.get("telephoneMobile")!=null && (telephoneHome.length()==0)){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("telephoneMobile");
					    		if(g.getString("countryCode") != null && !(g.getString("countryCode").equals("")))
					    			telephoneHome = telephoneHome+g.getString("countryCode");
					    		if(g.getString("areaCode") != null && !(g.getString("areaCode").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("areaCode");
					    		if(g.getString("contactNumber") != null && !(g.getString("contactNumber").equals("")))
					    			telephoneHome = telephoneHome + "-" + g.getString("contactNumber");						    		
					    	}
					    	if(partyDetailsMap.get("primaryEmail")!=null)
					    	emailId = (String) partyDetailsMap.get("primaryEmail");
					    	if(partyDetailsMap.get("postalShippingAddress") != null){
					    		GenericValue g=(GenericValue) partyDetailsMap.get("postalShippingAddress");
					    		if(g.getString("address1") != null)
					    		postalAddress = postalAddress + g.getString("address1") ;
					    		if(g.getString("address2") != null)
					    		postalAddress = postalAddress + " " + g.getString("address2");
					    		if(g.getString("city") != null)
					    		postalAddress = postalAddress + " " + g.getString("city");
					    		if(g.getString("countryGeoId")!= null)
					    		postalAddress = postalAddress + " " + g.getString("countryGeoId");
					    	}
					    	resistration.put("phoneNumber", telephoneHome);
			    			resistration.put("emailId", emailId);
							resistration.put("postalAddress", postalAddress);
							
							
							if(gv.getString("firstName")!=null && gv.getString("lastName")!=null)
							buffer=buffer+"<tr><td>"+gv.getString("createdDate")+"</td><td>"+gv.getString("partyId")+"</td><td>"+gv.getString("firstName") + " " + gv.getString("lastName")+"</td><td>"+gv.getString("userLoginId")+"</td><td>"+telephoneHome+"</td><td>"+emailId+"</td><td>"+postalAddress+"</td>";
							else
								buffer=buffer+"<tr><td>"+gv.getString("createdDate")+"</td><td>"+gv.getString("partyId")+"</td><td>"+gv.getString("firstName")+"</td><td>"+gv.getString("userLoginId")+"</td><td>"+telephoneHome+"</td><td>"+emailId+"</td><td>"+postalAddress+"</td>";
					    }//if
					}//else
					buffer=buffer+"\n";
					
				}//while
				
				out.println(buffer);
		 
	 }catch(Exception e){}
	 return "success";
	 }
	 
	 public static  String createNote(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException{
	       try{
	        delegator = (GenericDelegator)request.getAttribute("delegator");
	        String noteInfo=request.getParameter("note");
	        String partyId=request.getParameter("partyId");
	       
	        GenericValue g=null;
	        if(partyId!=null)
	        {
	         g=delegator.findByPrimaryKey("NoteParty",UtilMisc.toMap("partyId",partyId));
	        }
	       
	         if(g==null)
	       {
	        	 GenericValue party =delegator.makeValue("NoteParty");
                 String  noteId = delegator.getNextSeqId("noteId");
                 party.set("noteId", noteId);
                 party.set("noteInfo" ,noteInfo);
                 party.set("partyId", partyId);
                 party.create();
	    	   
	       
	       }
	       else
	       {
	    	   GenericValue party =delegator.makeValue("NoteParty");
	    	   party.set("noteInfo", noteInfo);
	    	   party.set("partyId", partyId);
	    	   party.store();
	       }
	      
	       }catch(Exception e){
	    	   
	       }
	        
	        
	       return "success";
	    }
	 
		public static String creditSummaryByCustomer(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
		    String billPartyId=request.getParameter("billPartyId");
			List<HashMap> creditwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCredit=BigDecimal.ZERO;
	    	Connection conn=null;
			//String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id,op.max_amount FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND (op.is_credit='Yes') AND (op.status_id='PAYMENT_NOT_RECEIVED') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.bill_To_Party_Id='"+billPartyId+"' ORDER BY oh.order_date DESC";
			String query2="";
			BigDecimal totalCreditReceived = BigDecimal.ZERO;
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz"));
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	    	    	query2="SELECT op.max_amount,op.created_date FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.is_credit='Done') AND (op.status_id='PAYMENT_RECEIVED') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.bill_To_Party_Id='"+billPartyId+"' AND oh.order_id='"+rs.getString(4).toString()+"' ORDER BY oh.order_date DESC";
	    	    	ps=conn.prepareStatement(query2);
	        	    ResultSet rs2=ps.executeQuery();
	        	    
	        	    List<HashMap> creditDetailslist=new ArrayList<HashMap>();
	        	    while (rs2.next()) {
	        	    	HashMap<String,String> creditDetails=new HashMap<String, String>();
	        	    	creditDetails.put("amt",rs2.getBigDecimal(1).toString());
	        	    	creditDetails.put("createdDate",rs2.getTimestamp(2).toString());
	        	    	creditDetailslist.add(creditDetails);
	        	    	totalCreditReceived = totalCreditReceived.add(rs2.getBigDecimal(1));
	        	    }
	              HashMap<String,Object> map=new HashMap<String, Object>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(5).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());
	              map.put("creditDetails",creditDetailslist);

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       creditwindowlist.add(map);
	    	    }
	    	    if(totalCredit==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			totalCredit = totalCreditAmountByCustomer(facilityId, fromDate, thruDate, delegator,billPartyId);
			
			request.setAttribute("creditwindowlist", creditwindowlist);
			request.setAttribute("totalcredit", totalCredit.toString());
			request.setAttribute("totalCreditReceived", totalCreditReceived.toString());
			request.setAttribute("totalDueAmount", totalCredit.subtract(totalCreditReceived));
			return "success";
		}
		
		public static BigDecimal totalCreditAmountByCustomer(String facilityId,String fromDate,String thruDate,GenericDelegator delegator,String billPartyId){
			BigDecimal totalCreditAmount=BigDecimal.ZERO;
	    	Connection conn=null;
//			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT')  AND (op.is_credit='Yes') AND (op.status_id='PAYMENT_NOT_RECEIVED') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCreditAmount=rs.getBigDecimal(1);
	              }
	    	    if(totalCreditAmount==null){
	    	    	totalCreditAmount=BigDecimal.ZERO;
	    	     }
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//System.out.println("########## totalCreditAmount ########"+totalCreditAmount);
			return totalCreditAmount;
		}
		public static String orderItemSummary(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String orderId=request.getParameter("orderId");
			try{
				List orderItems=delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderId));
				request.setAttribute("orderItems", orderItems);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return "success";
		}
		public static String cashSummary(HttpServletRequest request,HttpServletResponse response){
			GenericDelegator delegator=(GenericDelegator)request.getAttribute("delegator");
			String facilityId=request.getParameter("facilityId");
		    String fromDate=request.getParameter("fromDate");
		    String thruDate=request.getParameter("thruDate");
			List<HashMap> cashwindowlist=new ArrayList<HashMap>();
		    BigDecimal totalCash=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CASH') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	//totalCash=rs.getBigDecimal(1);
	              HashMap<String,String> map=new HashMap<String, String>();
	              
	              map.put("billDate",rs.getTimestamp(1).toString());
	              map.put("orderAmt",rs.getBigDecimal(2).toString());
	              map.put("partyId",rs.getString(3).toString());
	              map.put("orderId",rs.getString(4).toString());

	              GenericValue person=delegator.findOne("Person", UtilMisc.toMap("partyId",rs.getString(3)), false);
	    	       if(person!=null && person.getString("firstName")!=null){
	 	              map.put("cutomerName", person.getString("firstName"));
	    	       }else{
		 	              map.put("cutomerName","_NA_");
	    	       }
	    	       cashwindowlist.add(map);
	    	    }
	    	    if(totalCash==null){
	    	    	//totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			totalCash=totalCash(facilityId, fromDate, thruDate, delegator);
			request.setAttribute("cashwindowlist", cashwindowlist);
			request.setAttribute("totalCash", totalCash.toString());
			return "success";
		}
		
		/*public static BigDecimal totalCash(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){
			BigDecimal totalCash=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(op.max_amount) FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='CASH') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"'";
	    	//System.out.println("########## QUERY ########"+query);
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalCash=rs.getBigDecimal(1);
	    	    }
	    	    if(totalCash==null){
	    	    	totalCash=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
	    		e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return totalCash;
		}*/
		
		public static BigDecimal totalExpanse(GenericDelegator delegator){
			BigDecimal totalExpanse=BigDecimal.ZERO;
	    	Connection conn=null;
			String query="SELECT SUM(PAID_AMOUNT) FROM pos_terminal_intern_tx";
			try{
	         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=conn.prepareStatement(query);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	totalExpanse=rs.getBigDecimal(1);
	              }
	    	    if(totalExpanse==null){
	    	    	totalExpanse=BigDecimal.ZERO;
	    	     }
	    	    
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
			return totalExpanse;
		}
		public static  List<GenericValue> expanseDetail(GenericDelegator delegator,String facilityId,String posTerminalId,HttpServletRequest request){
			
			String fromDate = null;
			String thruDate  = null;
			Date date = new Date();
			List dateCondiList = new ArrayList();
			
			fromDate = request.getParameter("startDate");
			thruDate  = request.getParameter("endDate");
					
			Integer intDay = new Integer(date.getDate());
			Integer intMonth = new Integer(date.getMonth()+1);
			Integer intYear = new Integer(date.getYear()+1900);
			
			String  day = intDay.toString();
			String  month = intMonth.toString();
			String  year = intYear.toString();
				
			if(UtilValidate.isEmpty(fromDate))
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			if(fromDate != null && fromDate.length()<19)
				fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
			
			if(UtilValidate.isEmpty(thruDate))
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			if(thruDate != null && thruDate.length()<19)
				thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
			
			List<GenericValue> expanseDetail = null;
			List condition = new ArrayList();
			if(UtilValidate.isNotEmpty(facilityId))
				condition.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS,facilityId));
			if(UtilValidate.isNotEmpty(posTerminalId) && !posTerminalId.equals("all") && !posTerminalId.equals("_NA_"))
				condition.add(EntityCondition.makeCondition("posTerminalId",EntityOperator.EQUALS,posTerminalId));
				
			//condition.add(EntityCondition.makeCondition("reasonEnumId",EntityOperator.EQUALS,"EXPANSE_OUT_REASON"));
			
			
			try {
				condition.add(EntityCondition.makeCondition("logEndDateTime", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null)));
				condition.add(EntityCondition.makeCondition("logEndDateTime", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null)));
			} catch (GeneralException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			EntityCondition cond = null;
			if(condition.size() > 0)
				cond = EntityCondition.makeCondition(condition,EntityOperator.AND);
				//cond = EntityCondition.makeCondition("reasonEnumId",EntityOperator.EQUALS,"EXPANSE_OUT_REASON");
			/*if(condition.size() > 1)
				cond = EntityCondition.makeCondition(condition,EntityOperator.AND);
			*/
			//System.out.println("##############           cond         "+cond);
			try {
				expanseDetail = delegator.findList("ExpanseView", cond, null, null, null, false);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
			return expanseDetail;
		}

		public static  String freshFlowerDetails(HttpServletRequest request,HttpServletResponse response){
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
			
	    	
	    	String productCategoryId = UtilProperties.getPropertyValue("general","posstore.productCategoryId");
	    	
	    	if(UtilValidate.isNotEmpty(productCategoryId))
	    	{
	    		    Map<String, Object> result = ServiceUtil.returnSuccess();
	    	        try {
	    	            result = dispatcher.runSync("getProductCategoryMembers", UtilMisc.toMap("categoryId", productCategoryId));
	    	        } catch (GenericServiceException ex) {
	    	            Debug.logError("Cannot get category memebers for " + productCategoryId + " due to error: " + ex.getMessage(), module);
	    	           // return ServiceUtil.returnError(ex.getMessage());
	    	        }
	    	        EntityListIterator listIt = null;
	    	        if(UtilValidate.isNotEmpty(result) && result.size() > 0)
	    	    	{
	    	        	List<GenericValue> memberProducts = UtilGenerics.checkList(result.get("categoryMembers"));
	    	        	if(UtilValidate.isNotEmpty(memberProducts) && memberProducts.size() > 0)
	    	        	 for (GenericValue memberProduct: memberProducts) {
	    	        		 String productId = memberProduct.getString("productId");
	    	        		 
	    	        		 param.put("facilityId", UtilProperties.getPropertyValue("general","posstore.usefaclityId"));
	    	 		    	 param.put("productId", productId);
	    	 		    	 param.put("quantityOnHandDiff", "0");
	    	 		    	 param.put("quantityOnHandDiff_op", "notEqual");
	    	 		    	
	    	 		    	 
	    	 		    	param.remove("responseMessage");
	    	 		    	
	    	 		    	
	    	 		    	
	    	 		    	String fromDate = request.getParameter("minDate");
	    					String thruDate  = request.getParameter("maxDate");
	    					Date date = new Date();
	    					List dateCondiList = new ArrayList();
							
	    					fromDate = request.getParameter("minDate");
							thruDate  = request.getParameter("maxDate");
	    							
							Integer intDay = new Integer(date.getDate());
							Integer intMonth = new Integer(date.getMonth()+1);
							Integer intYear = new Integer(date.getYear()+1900);
							
							String  day = intDay.toString();
							String  month = intMonth.toString();
							String  year = intYear.toString();
								
							if(UtilValidate.isEmpty(fromDate))
								fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
							if(fromDate != null && fromDate.length()<19)
								fromDate = year + "-" + month + "-" + day + " " + "00:00:00";
							
							if(UtilValidate.isEmpty(thruDate))
								thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
							if(thruDate != null && thruDate.length()<19)
								thruDate  = year + "-" + month + "-" + day + " " + "23:59:59.999";
	    					
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
	    	 		    	if(UtilValidate.isNotEmpty(performFind)&& performFind.size() > 0)
	    	 		    	{
	    	 		    		//System.out.println("##########         performFind.size()          "+performFind.size());
	    	 		    		listIt = (org.ofbiz.entity.util.EntityListIterator)performFind.get("listIt");
	    	 		    		
	    	 		    		String inventoryItemId = null;
	    	 		    		String preInventoryItemId = null;
	    	 		    		
	    	 		    		int count =1;
	    	 		        	if(listIt != null)
	    	 		        	{
	    	 		    			
	    	 		    			for(;;){
	    	 		    				GenericValue inventory = listIt.next();
	    	 		    			if(inventory == null)break;
	    	 		        		if(inventory != null)
	    	 		        		{
	    	 		        			
	    	 		        			
	    	 		        			val =  ServiceUtil.returnSuccess();
	    	 		        			
	    	 		        			String orderId = inventory.getString("orderId");
	    	 		        			String returnId = inventory.getString("returnId");
	    	 		        			String reasonEnumId = inventory.getString("reasonEnumId");
	    	 		        			inventoryItemId = inventory.getString("inventoryItemId");
	    	 		        			
	    	 		        			java.math.BigDecimal availableToPromise = (java.math.BigDecimal)inventory.get("availableToPromiseTotal");
	    	 		        			java.math.BigDecimal quantityOnHand = (java.math.BigDecimal)inventory.get("quantityOnHandTotal");
	    	 		        			
	    	 		        			if(UtilValidate.isNotEmpty(inventoryItemId) && !inventoryItemId.equals(preInventoryItemId) )
	    	 		        			{
	    	 		        				availableToPromiseTotal = availableToPromiseTotal.add(availableToPromise);
	    	 		        				quantityOnHandTotal = quantityOnHandTotal.add(quantityOnHand);
	    	 		        			}
	    	 		        			preInventoryItemId = inventoryItemId;
	    	 		        			if(UtilValidate.isNotEmpty(orderId))
	    	 		        			{
	    	 		        				totalOrders = totalOrders.add((BigDecimal)inventory.get("availableToPromiseDiff"));
	    	 		        				totalOrdersQOH = totalOrdersQOH.add((BigDecimal)inventory.get("quantityOnHandDiff"));
	    	 		        			}
	    	 		        			else if(UtilValidate.isNotEmpty(returnId))
	    	 		        			{
	    	 		        				totalOrderReturn = totalOrderReturn.add((BigDecimal)inventory.get("availableToPromiseDiff"));
	    	 		        				totalOrderReturnQOH = totalOrderReturnQOH.add((BigDecimal)inventory.get("quantityOnHandDiff"));
	    	 		        			}
	    	 		        			else if(UtilValidate.isNotEmpty(reasonEnumId))
	    	 		        			{
	    	 		        				totalDamaged = totalDamaged.add((BigDecimal)inventory.get("availableToPromiseDiff"));
	    	 		        				totalDamagedQOH = totalDamagedQOH.add((BigDecimal)inventory.get("quantityOnHandDiff"));
	    	 		        			}
	    	 		        			else
	    	 		        			{
	    	 		        				totalPurchased = totalPurchased.add((BigDecimal)inventory.get("availableToPromiseDiff"));
	    	 		        				totalPurchasedQOH = totalPurchasedQOH.add((BigDecimal)inventory.get("quantityOnHandDiff"));
	    	 		        			}
	    	 		        		}
	    	 		    			}
	    	 		        	}
	    	 		    	}
	    	 		    	result1 = ServiceUtil.returnSuccess();
	    	 		    	try{
	    	 		    	GenericValue product = org.ofbiz.product.product.ProductWorker.findProduct(delegator,productId);
	    	 		    	result1.put("product", product);
	    	 		    	}catch (Exception e) {
								// TODO: handle exception
							}
	    	 		    	if(totalOrders != null && totalOrders != BigDecimal.ZERO)
	    	 		    		result1.put("totalOrders", totalOrders.multiply(new BigDecimal(-1)));
	    	 		    	else
	    	 		    		result1.put("totalOrders", totalOrders);
	    	    	        result1.put("totalOrdersQOH", totalOrdersQOH);
	    	    	        result1.put("totalOrderReturn", totalOrderReturn);
	    	    	        result1.put("totalOrderReturnQOH", totalOrderReturnQOH);
	    	    	        result1.put("totalDamaged", totalDamaged);
	    	    	        result1.put("totalDamagedQOH", totalDamagedQOH);
	    	    	        result1.put("totalPurchased", totalPurchased);
	    	    	        result1.put("totalPurchasedQOH", totalPurchasedQOH);
	    	    	        result1.put("availableToPromiseTotal", availableToPromiseTotal);
	    	    	        result1.put("quantityOnHandTotal", quantityOnHandTotal);
	    	    	        
	    	    	        val123.add(result1);
	    	    	        
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
	    	    	}
	    	        
	    	        
	    	        request.setAttribute("result",val123);
	    	}
			return "success";
		}
		public static BigDecimal totalTax(String facilityId,String fromDate,String thruDate,GenericDelegator delegator){

			
			BigDecimal totalTax=BigDecimal.ZERO;
			Connection conn=null;
			//String query="SELECT oh.order_Date,oh.grand_Total,oh.bill_To_Party_Id,oh.order_id FROM order_header oh,order_payment_preference op WHERE oh.order_id=op.order_id AND (op.payment_method_type_id='EXT_CREDIT') AND oh.origin_Facility_Id='"+facilityId+"' AND oh.order_date BETWEEN '"+fromDate+"' AND '"+thruDate+"' AND oh.bill_To_Party_Id='"+billPartyId+"'";
			String query="SELECT SUM(OA.AMOUNT)  FROM ORDER_HEADER OH, ORDER_ADJUSTMENT OA WHERE OH.ORDER_ID = OA.ORDER_ID ";
			
			if(fromDate!=null && thruDate!=null){
				query = query +" AND OH.order_date >= '"+fromDate+"' AND OH.order_date < '"+thruDate+"'";
			}
			else if(thruDate!=null && fromDate==null){
				query = query +" AND OH.order_date < '"+thruDate+"'";
			}
			
			//System.out.println("###################     totalTax     "+query);
			try{
		     	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz"));
			    PreparedStatement ps=conn.prepareStatement(query);
			    ResultSet rs=ps.executeQuery();
			    while (rs.next()) {
			    	totalTax = rs.getBigDecimal(1);
			    	if(totalTax == null )totalTax = BigDecimal.ZERO;
			    }
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			return totalTax;

				}
}