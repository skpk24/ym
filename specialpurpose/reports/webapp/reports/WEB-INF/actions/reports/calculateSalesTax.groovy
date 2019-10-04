 import java.math.BigDecimal;
import java.util.*;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.base.util.*;

productStoreId= parameters.productStoreId;
fromDate= parameters.minDate;
thruDate= parameters.maxDate;
List taxListDetail = new ArrayList();
if(productStoreId == "")
{
	List terminal = delegator.findAll("ProductStore");
	Iterator it = terminal.iterator();
	while(it.hasNext())
	{
		 totalTax=0.0;
		 HashMap hm = new HashMap();
		 GenericValue counterG = (GenericValue)it.next();
		 String productStoreId = (String)counterG.get("productStoreId");
		 hm.put("productStoreId", productStoreId);
		 hm.put("storeName",counterG.get("storeName") );
		 hm.put("companyName", counterG.get("companyName") );
		 List mainExprs = new ArrayList();
		 List dateCondiList = new ArrayList();
		 if((!"".equals(parameters.minDate)) && ("".equals(parameters.maxDate))){
			    mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		 }else if(("".equals(parameters.minDate)) && (!"".equals(parameters.maxDate))){
				mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		 }else if((!"".equals(parameters.minDate)) && (!"".equals(parameters.maxDate))){
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);
				mainExprs.add(dateCondition);
		 }else{}
	     mainExprs.add( EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,productStoreId));
		 mainExprs.add( EntityCondition.makeCondition("orderTypeId",EntityOperator.EQUALS,"SALES_ORDER"));
		 EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 List orderHeaderList = delegator.findList("OrderHeader", mainCondition, UtilMisc.toSet("grandTotal","orderId"), null, null, false);
		 
		 Iterator <GenericValue>its = orderHeaderList.iterator();
	     while(its.hasNext())
	     {
	     orderHeader = (GenericValue)its.next();
	     orderReadHelper = new OrderReadHelper(orderHeader);
	     orderAdjustments = orderReadHelper.getAdjustments();
	     taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	     totalTax = totalTax +taxAmount;
	     }
	     hm.put("totalTax", totalTax);
	     if(!"".equals(parameters.minDate)){
			 hm.put("fromDate", fromDate);
		 }else{
			 Connection conn=null;
				String query="SELECT MIN(created_stamp) FROM order_header oh WHERE oh.order_type_id='SALES_ORDER'";
				try{
		         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
		    	    PreparedStatement ps=conn.prepareStatement(query);
		    	    ResultSet rs=ps.executeQuery();
		    	    while (rs.next()) {
		    	    	fromDate=rs.getTimestamp(1);
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
				hm.put("fromDate", fromDate);
		 }
	     if(!"".equals(parameters.maxDate)){
			 hm.put("thruDate", thruDate);
		 }else{
			 hm.put("thruDate", UtilDateTime.nowDate());
		 }
	     taxListDetail.add(hm);
	}
}
else
{
	     storeName =null;
		 totalTax=0.0;
		 HashMap hm = new HashMap();
		 hm.put("productStoreId", productStoreId);
		 Connection con=null;
			String querys="SELECT store_name, company_name FROM product_store ps WHERE ps.product_store_id='"+productStoreId+"'";
			try{
	         	con = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
	    	    PreparedStatement ps=con.prepareStatement(querys);
	    	    ResultSet rs=ps.executeQuery();
	    	    while (rs.next()) {
	    	    	storeName=rs.getString("store_name");
	    	    	companyName=rs.getString("company_name");
	    	    	hm.put("storeName", storeName);
	    	    	hm.put("companyName", companyName);
	              }
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
			    try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}	
			}
		 List mainExprs = new ArrayList();
		 List dateCondiList = new ArrayList();
		 if((!"".equals(parameters.minDate)) && ("".equals(parameters.maxDate))){
			    mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		 }else if(("".equals(parameters.minDate)) && (!"".equals(parameters.maxDate))){
				mainExprs.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		 }else if((!"".equals(parameters.minDate)) && (!"".equals(parameters.maxDate))){
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
				dateCondiList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
				EntityConditionList dateCondition = EntityCondition.makeCondition(dateCondiList, EntityOperator.AND);
				mainExprs.add(dateCondition);
		 }else{}
	     mainExprs.add( EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,productStoreId));
		 mainExprs.add( EntityCondition.makeCondition("orderTypeId",EntityOperator.EQUALS,"SALES_ORDER"));
		 EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 List orderHeaderList = delegator.findList("OrderHeader", mainCondition, UtilMisc.toSet("grandTotal","orderId"), null, null, false);
		 
		 Iterator <GenericValue>its = orderHeaderList.iterator();
	     while(its.hasNext())
	     {
	     orderHeader = (GenericValue)its.next();
	     orderReadHelper = new OrderReadHelper(orderHeader);
	     orderAdjustments = orderReadHelper.getAdjustments();
	     taxAmount = OrderReadHelper.getOrderTaxByTaxAuthGeoAndParty(orderAdjustments).taxGrandTotal;
	     totalTax = totalTax +taxAmount;
	     }
	     hm.put("totalTax", totalTax);
	     if(!"".equals(parameters.minDate)){
			 hm.put("fromDate", fromDate);
		 }else{
			 Connection conn=null;
				String query="SELECT MIN(created_stamp) FROM order_header oh WHERE oh.order_type_id='SALES_ORDER'";
				try{
		         	conn = ConnectionFactory.getConnection(delegator.getGroupHelperName("org.ofbiz")); 
		    	    PreparedStatement ps=conn.prepareStatement(query);
		    	    ResultSet rs=ps.executeQuery();
		    	    while (rs.next()) {
		    	    	fromDate=rs.getTimestamp(1);
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
				hm.put("fromDate", fromDate);
		 }
	     if(!"".equals(parameters.maxDate)){
			 hm.put("thruDate", thruDate);
		 }else{
			 hm.put("thruDate", UtilDateTime.nowDate());
		 }
	     taxListDetail.add(hm);  
}

context.listIt = taxListDetail;
		 