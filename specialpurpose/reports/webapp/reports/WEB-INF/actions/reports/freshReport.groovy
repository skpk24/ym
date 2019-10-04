			
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
			
			
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
				
				context.fromDateStr=fromDate;
				context.thruDateStr=thruDate;
				
				
				
					List dateCondiList = new ArrayList();
		try {
			dateCondiList.add(EntityCondition.makeCondition("inclutionDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
			dateCondiList.add(EntityCondition.makeCondition("inclutionDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		
	  List orderBy = UtilMisc.toList("-createdStamp");
        List <GenericValue>transList = null;
        transList = delegator.findList("FreshFlowerRecord", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), null, orderBy, null, false);
        
        context.freshList=transList;
        
        
        
        
        
        
        
        
        
        
        
        
		
				
