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


List stores = delegator.findAll("ProductStore");

fromDate= parameters.minDate;
thruDate= parameters.maxDate;
if(stores !=null)
{
	Iterator it = stores.iterator();
	List storeGrandTotal = new ArrayList();
	while(it.hasNext())
	{
		 HashMap hm = new HashMap();
		 GenericValue storeG = (GenericValue)it.next();
		 String storeId = (String)storeG.get("productStoreId");
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
	     mainExprs.add( EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,storeId));
		 mainExprs.add( EntityCondition.makeCondition("orderTypeId",EntityOperator.EQUALS,"PURCHASE_ORDER"));
		 EntityConditionList mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
		 List orderHearder = delegator.findList("OrderHeader", mainCondition, UtilMisc.toSet("grandTotal"), null, null, false);
		 BigDecimal total = new BigDecimal(0);
		 if(orderHearder!=null)
		 {
			 Iterator itt = orderHearder.iterator();
			 while(itt.hasNext())
			 {
				 GenericValue ohGeneric = (GenericValue)itt.next();
				 BigDecimal gt = ohGeneric.getBigDecimal("grandTotal");
				 if((gt!=null) &&  (!"".equals(gt)))
				 total = total + gt;
			 }
		 }
		 if(!"".equals(parameters.minDate)){
			 hm.put("fromDate", fromDate);
		 }else{
			 Connection conn=null;
				String query="SELECT MIN(created_stamp) FROM order_header oh WHERE oh.order_type_id='PURCHASE_ORDER'";
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
		 hm.put("key", storeId);
		 hm.put("grandTotal", total);
		 hm.put("storeName",  storeG.get("storeName"));
		 hm.put("companyName",  storeG.get("companyName"));
		 storeGrandTotal.add(hm);
		
	}
		 context.listIt = storeGrandTotal;
}



