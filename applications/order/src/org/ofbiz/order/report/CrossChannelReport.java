package org.ofbiz.order.report;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;

import java.util.HashMap;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.GenericEntityException;
//import javolution.util.FastList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.base.util.UtilValidate;
//import javolution.util.FastMap;

import java.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


import java.text.*;

import java.io.File;

public class CrossChannelReport {

	    public static final String module = CrossChannelReport.class.getName();
		private static GenericDelegator delegator;
		protected static LocalDispatcher dispatcher = null;
	
		public static  String crossChannelReport(HttpServletRequest request, HttpServletResponse response){
			try{
				if(request.getParameter("forMonth")==null || UtilValidate.isEmpty(request.getParameter("forMonth")))
				{
					return "error";
				}
				
				String forMonth = request.getParameter("forMonth");
				int actualForMonth = Integer.parseInt( forMonth );
				String forYear  = request.getParameter("forYear");
				int intforYear = Integer.parseInt( forYear );
				String forMonthName=getMonthForInt(actualForMonth-1);
				
				String thruMonth = request.getParameter("thruMonth");
				int actualThruMonth= Integer.parseInt( thruMonth );
				String thruYear  = request.getParameter("thruYear");
				int intthruYear = Integer.parseInt( thruYear );
				String frommonthName=getMonthForInt(actualForMonth-1);
				String thrumonthName=getMonthForInt(actualThruMonth-1);
				delegator = (GenericDelegator)request.getAttribute("delegator");
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=crosschannelreports.csv");
				StringBuffer data = new StringBuffer();
				
				
				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				data.append("Report : Cross Channel Report  ");
				data.append("\n");
				data.append("For month  : " + frommonthName+" "+forYear+" to "+thrumonthName+" "+thruYear);
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append("Month-Year,Web Channel,POS Channel,Affiliate Channel,eBay Channel");
				data.append("\n");
				data.append("\n");
				
				for(int i=actualForMonth;i<=actualThruMonth && intforYear==intthruYear;i++){
					int month = i -1;
					GregorianCalendar calendar =new  GregorianCalendar();
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.set(Calendar.MONTH,month);
					calendar.set(Calendar.YEAR,intforYear);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(GregorianCalendar.MINUTE, 0);
					calendar.set(GregorianCalendar.SECOND, 0);
				    
					GregorianCalendar calendarThru =new  GregorianCalendar();
					calendarThru.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
					calendarThru.set(Calendar.MONTH,month);
					calendarThru.set(Calendar.YEAR,intthruYear);
					calendarThru.set(Calendar.HOUR_OF_DAY,23);
					calendarThru.set(GregorianCalendar.MINUTE, 59);
					calendarThru.set(GregorianCalendar.SECOND, 59);
					Map revenueReport =  CrossChannelReport.getrevenueCrossChannelMonthlySummary(calendar, calendarThru);
				
				
						if(revenueReport != null ){
							data.append(getMonthForInt(month)+intthruYear+"");
							data.append(",");
							data.append(revenueReport.get("totalLwebSalesChannelTotal"));
							data.append(",");
							data.append(revenueReport.get("totalposSalesChannelTotal"));
							data.append(",");
							data.append(revenueReport.get("totalebaySalesChannelTotal"));
							data.append(",");
							data.append(revenueReport.get("totalaffSalesChannelTotal"));
						}
						data.append("\n");
				}//for loop closed		
				
				
				 int amotnh=actualForMonth;
				 int de=12;
				if(intforYear<intthruYear) {
				for(int j=intforYear;j<=intthruYear;j++){
						if(j==intthruYear){
							de=actualThruMonth;
						}
						else{
							de=12;
						}
							for(int z=amotnh;z<=de;z++)	{
								int month =z - 1;
								GregorianCalendar calendar =new  GregorianCalendar();
								calendar.set(Calendar.DAY_OF_MONTH, 1);
								calendar.set(Calendar.MONTH,month);
								calendar.set(Calendar.YEAR,j);
								calendar.set(Calendar.HOUR_OF_DAY, 0);
								calendar.set(GregorianCalendar.MINUTE, 0);
								calendar.set(GregorianCalendar.SECOND, 0);
							    
								GregorianCalendar calendarThru =new  GregorianCalendar();
								calendarThru.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
								calendarThru.set(Calendar.MONTH,month);
								calendarThru.set(Calendar.YEAR,j);
								calendarThru.set(Calendar.HOUR_OF_DAY,23);
								calendarThru.set(GregorianCalendar.MINUTE, 59);
								calendarThru.set(GregorianCalendar.SECOND, 59);
								Map revenueReport =  CrossChannelReport.getrevenueCrossChannelMonthlySummary(calendar, calendarThru);
								
								
								if(revenueReport != null ){
									data.append(getMonthForInt(month)+j+"");
									data.append(",");
									data.append(revenueReport.get("totalLwebSalesChannelTotal"));
									data.append(",");
									data.append(revenueReport.get("totalposSalesChannelTotal"));
									data.append(",");
									data.append(revenueReport.get("totalebaySalesChannelTotal"));
									data.append(",");
									data.append(revenueReport.get("totalaffSalesChannelTotal"));
								}
								data.append("\n");
								
							}
						
						amotnh=01;
				}//for loop year closed 
				}
				
				
				
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				List delreportlivemulti=new ArrayList();
				
				
			}//try closed
		catch (Exception e) {
			//System.out.println("Exception getting Cross Channel Report: " + e);
			e.printStackTrace();
		}
		
		return "success";
		}//crossChannelReport closed	
	
		
   public static  Map getrevenueCrossChannelMonthlySummary(GregorianCalendar fromDate,GregorianCalendar thruDate){
			Map revenueReport = new HashMap();
			
			try{
			    int startDay = fromDate.get(Calendar.DAY_OF_YEAR);
			    int endDay  =  thruDate.get(Calendar.DAY_OF_YEAR);

			List revenueProducts = new ArrayList();
			List ordersToCheck = null;
			double total = 0;
			double totalwebSalesChannelQuantity = 0 ;
			double totalwebSalesChannelTotal = 0 ;
			double totalposSalesChannelQuantity = 0 ;
			double totalposSalesChannelTotal = 0 ;
			double totalebaySalesChannelQuantity = 0 ;
			double totalebaySalesChannelTotal = 0 ;
            double totalaffSalesChannelQuantity=0;
            double totalaffSalesChannelTotal=0;
			GregorianCalendar fromRange =(GregorianCalendar) fromDate.clone();
			fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) - 1);

			GregorianCalendar toRange = (GregorianCalendar)fromDate.clone();
			toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) - 1);
			int dif = UtilDateTime.getDaysBetweenDates(fromDate.getTime(), thruDate.getTime());
			if(dif != 0){
				dif--;
			}
			for(int j =0;j<=dif+1;j++ ){
				Map productDetails = new HashMap();
				//calendar.set(Calendar.DAY_OF_WEEK, 1);
				//fromRange.set(Calendar.DAY_OF_YEAR, j);   
				fromRange.set(Calendar.DATE, fromRange.get(Calendar.DATE) + 1);
				fromRange.set(Calendar.HOUR_OF_DAY, 0);
				fromRange.set(GregorianCalendar.MINUTE, 0);
				fromRange.set(GregorianCalendar.SECOND, 0);

				//calendar.set(Calendar.DAY_OF_WEEK, 1);
				//toRange.set(Calendar.DAY_OF_YEAR, j);   
				toRange.set(Calendar.DATE, toRange.get(Calendar.DATE) + 1);
				toRange.set(Calendar.HOUR_OF_DAY, 23);
				toRange.set(GregorianCalendar.MINUTE, 59);
				toRange.set(GregorianCalendar.SECOND, 59);

				


				double subTotal = 0;
				double webSalesChannelQuantity = 0 ;
				double posSalesChannelQuantity = 0 ;
				double ebaySalesChannelQuantity = 0 ;
				double affSalesChannelQuantity = 0 ;
				double webSalesChannelTotal = 0 ;
				double posSalesChannelTotal = 0 ;
				double ebaySalesChannelTotal = 0 ;
				double affSalesChannelTotal = 0 ;
				Timestamp fromRangeTime = new Timestamp(fromRange.getTime().getTime());
				Timestamp toRangeTime = new Timestamp(toRange.getTime().getTime());
				
	            List orderStatusCondList = new ArrayList();
	            orderStatusCondList.add(EntityCondition.makeCondition(UtilMisc.toList(
	            		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_COMPLETED"),
                        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED")), EntityOperator.OR));
              
	            orderStatusCondList.add(EntityCondition.makeCondition(UtilMisc.toList(
	            		EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromRangeTime),
                        EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, toRangeTime)), EntityOperator.AND));
              
	           
				
				
			    EntityConditionList<EntityExpr> mainCondition =EntityCondition.makeCondition(orderStatusCondList); 
				
				Set fields = UtilMisc.toSet("orderId");
				EntityListIterator eli = null;
				 try {
					 ordersToCheck = delegator.findList("OrderHeader", mainCondition, null, UtilMisc.toList("orderDate"), null, false);   
			        } catch (GenericEntityException e) {
			            Debug.logError(e, module);
			            return ServiceUtil.returnError(e.getMessage());
			        }
			        if (UtilValidate.isNotEmpty(ordersToCheck)) {
	                    Iterator oii = ordersToCheck.iterator();
	                    while (oii.hasNext()) {
	                    	 GenericValue orderHeader = (GenericValue) oii.next();
			                 String orderId = orderHeader.getString("orderId");
			                 if("WEB_SALES_CHANNEL".equalsIgnoreCase(orderHeader.getString("salesChannelEnumId"))){
			                	 //webSalesChannelQuantity = webSalesChannelQuantity + (orderHeader.getDouble("quantity").doubleValue());
			                	 webSalesChannelTotal=webSalesChannelTotal+	(orderHeader.getDouble("grandTotal").doubleValue());
			                 }
			                 if("POS_SALES_CHANNEL".equalsIgnoreCase(orderHeader.getString("salesChannelEnumId"))){
			                	// posSalesChannelQuantity = posSalesChannelQuantity + (orderHeader.getDouble("quantity").doubleValue());
			                	 posSalesChannelTotal=posSalesChannelTotal+	(orderHeader.getDouble("grandTotal").doubleValue());	
			                 }
			                 if("EBAY_SALES_CHANNEL".equalsIgnoreCase(orderHeader.getString("salesChannelEnumId"))){
			                	// ebaySalesChannelQuantity = ebaySalesChannelQuantity + (orderHeader.getDouble("quantity").doubleValue());
			                	 ebaySalesChannelTotal=ebaySalesChannelTotal+	(orderHeader.getDouble("grandTotal").doubleValue());
								}
			                 if("AFFIL_SALES_CHANNEL".equalsIgnoreCase(orderHeader.getString("salesChannelEnumId"))){
			                	// affSalesChannelQuantity = affSalesChannelQuantity + (orderHeader.getDouble("quantity").doubleValue());
			                	 affSalesChannelTotal=affSalesChannelTotal+	(orderHeader.getDouble("grandTotal").doubleValue());
								}
			                 
			        	 
			        	 }//while
			        }//eli not null
			  
					totalwebSalesChannelQuantity = totalwebSalesChannelQuantity + webSalesChannelQuantity;
					totalwebSalesChannelTotal = totalwebSalesChannelTotal + webSalesChannelTotal;
					totalposSalesChannelQuantity = totalposSalesChannelQuantity + posSalesChannelQuantity;
					totalposSalesChannelTotal = totalposSalesChannelTotal + posSalesChannelTotal;
					totalebaySalesChannelQuantity = totalebaySalesChannelQuantity + ebaySalesChannelQuantity;
					totalebaySalesChannelTotal = totalebaySalesChannelTotal + ebaySalesChannelTotal;
					totalaffSalesChannelQuantity = totalaffSalesChannelQuantity + affSalesChannelQuantity;
					totalaffSalesChannelTotal = totalaffSalesChannelTotal + affSalesChannelTotal;
						

			}// end for loop Dates
					
					revenueReport.put("totalLwebSalesChannelQuantity",totalwebSalesChannelQuantity+"");
					revenueReport.put("totalLwebSalesChannelTotal",totalwebSalesChannelTotal+"");
					
					revenueReport.put("totalposSalesChannelQuantity",totalposSalesChannelQuantity+"");
					revenueReport.put("totalposSalesChannelTotal",totalposSalesChannelTotal+"");
					
					revenueReport.put("totalebaySalesChannelQuantity",totalebaySalesChannelQuantity+"");
					revenueReport.put("totalebaySalesChannelTotal",totalebaySalesChannelTotal+"");
					
					revenueReport.put("totalaffSalesChannelQuantity",totalaffSalesChannelQuantity+"");
					revenueReport.put("totalaffSalesChannelTotal",totalaffSalesChannelTotal+"");
			
		} catch (Exception e) {
			//System.out.println("Exception calulating revenue report: " + e);
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

		
		public static  String genSalesreportCSV(HttpServletRequest request, HttpServletResponse response){
			try{

				////System.out.println("dispatcherName: " + dispatcherName);
				delegator = (GenericDelegator)request.getAttribute("delegator");
				
				String fromDate = request.getParameter("minDate");
				String thruDate  = request.getParameter("maxDate");
				
				String orderStatusID = request.getParameter("orderStatusID");
				
				List mainExprs = new ArrayList();
				

				List isOrderCompleted = new ArrayList();
				if(orderStatusID != null && orderStatusID.length() > 0  ){
					isOrderCompleted.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, orderStatusID));
					EntityConditionList isOrderCompletedCondition = EntityCondition.makeCondition(isOrderCompleted, EntityOperator.OR);
					mainExprs.add(isOrderCompletedCondition);
				}
				

				List dateCondiList = new ArrayList();
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);

				
				
				mainExprs.add(dateCondition);
				
				EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);

				//System.out.println("sales data : main condition : " + mainCondition);
				
				List sales = delegator.findByCondition("OrderHeader",mainCondition,null,UtilMisc.toList("orderDate DESC") );
				
				
				
				//response.setContentType("application/octet-stream");
				response.setContentType("application/excel");
				response.setHeader("Content-disposition","attachment;filename=SalesReport.csv");
				StringBuffer data = new StringBuffer();

				data.append("\n");
				data.append("#--------------------------------------------------------------");
				data.append("\n");
				data.append("Report : Sales Report");
				data.append("\n");
				data.append("Date Range  : " + fromDate.substring(0,fromDate.lastIndexOf(" ")) + " To " +  thruDate.substring(0,thruDate.lastIndexOf(" ")));
				data.append("\n");
				data.append("#--------------------------------------------------------------" );
				data.append("\n");
				data.append("\n");
				data.append("Date ,Party ID,Party Name ,Created By,Order ID,Order status ,Sub Total");
				data.append("\n");
				data.append("\n");
				
				//SimpleDateFormat dateFormat = new SimpleDateFormat("MMM  d yyyy  h:mm a");
				//SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 23:50:04.000");

				if(sales != null && sales.size() >0){
                      double totalSales=0.0;
					for(int i=0;i<sales.size();i++){
						GenericValue orderRoleGv = null;
						GenericValue saleGV = (GenericValue)sales.get(i);
						//java.util.Date d = new java.util.Date(saleGV.getDate("orderDate").getTime());

						List orderRoleListGv = delegator.findByAnd("OrderRole",UtilMisc.toMap("orderId",saleGV.getString("orderId")));
						//role info 
						if(orderRoleListGv != null && orderRoleListGv.size()>0){
							orderRoleGv = (GenericValue) orderRoleListGv.get(0);
						}
						//role info 
						GenericValue personInfo=null;
						String partyName="";
						String partyId=orderRoleGv.getString("partyId");
						   if(partyId!=null){
							   personInfo=delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
					        	Debug.logInfo("personInfo..................."+personInfo, module);  
					        	 if(personInfo!=null){
					        		 partyName=personInfo.getString("firstName");
					        		   if(personInfo.getString("lastName")!=null){
					        			   partyName=partyName+" "+personInfo.getString("lastName");
					        		   }
					        		 
					        	 }
							   
						   }
						data.append(saleGV.getString("orderDate")+",");
						data.append(orderRoleGv.getString("partyId")+",");
						data.append(partyName+",");
						data.append(saleGV.getString("createdBy")+",");
						data.append(saleGV.getString("orderId")+",");
						data.append(saleGV.getString("statusId")+",");
						//data.append(saleGV.getString("productId")+",");

						

						//double quantity =  Double.parseDouble(saleGV.getString("quantity"));
						//data.append(saleGV.getString("productName").replaceAll(","," ")+",");
						//data.append(saleGV.getString("quantity")+",");
						//double unitPrice =  Double.parseDouble(saleGV.getString("unitPrice"));
						//data.append(unitPrice*quantity+",");
						
						
						//double	price = com.compliance.webinars.WebinarHelper.getOrderProductPrice(saleGV.getString("productId"),saleGV.getString("orderId"),saleGV.getString("orderItemSeqId"));
						//double discAmount = (quantity* unitPrice)-(price);
						//data.append(discAmount+",");
						//String orderId = saleGV.getString("orderId");
						//.append(orderId+",");
						
						data.append(saleGV.getDouble("grandTotal")+",");
						totalSales=totalSales+saleGV.getDouble("grandTotal");
					
					

						data.append("\n");

					}
					
					data.append(","+","+","+","+","+","+totalSales);
				}
				
							
				OutputStream out = response.getOutputStream();
				out.write(data.toString().getBytes());
				out.flush();
				}catch(Exception ex){
				//System.out.println("\n\nException: " + ex);
				}
				
				return "success";
		}		 
		 
		 
		 
		 
}