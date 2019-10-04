			
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
			
			if(request.getParameter("minDate")!=null)
			String fromDate = request.getParameter("minDate");
			String thruDate  = request.getParameter("maxDate");
			print("radha\n\n\n\n\n\n\n\n\n\n\n"+request.getParameter("minDate"));
			Date date = new Date();
			
		    
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
		
                List<HashMap<String,String>> resultList=new ArrayList<HashMap<String,String>>(); 
				
				
				context.thruDateStr=thruDate;
				
				
				
					List dateCondiList = new ArrayList();
		try {
		if(request.getParameter("minDate")!=null&&request.getParameter("minDate")!="")
		{
		print("pppppppppppppppppppppppppp");
		fromDate = request.getParameter("minDate");
			dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(fromDate, "Timestamp", null, null) ));
		}
		
			dateCondiList.add(EntityCondition.makeCondition("lastUpdatedStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(thruDate, "Timestamp", null, null) ));
		dateCondiList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"SALES_REP"));
		print("********************"+dateCondiList);
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		
	List orderBy = UtilMisc.toList("-createdStamp");
        List <GenericValue>transList = null;
        transList = delegator.findList("OrderRole", EntityCondition.makeCondition(dateCondiList, EntityOperator.AND), null, orderBy, null, false);
        print("transList\n\n\n"+transList);
        //context.salesList=transList;
        grandTotal = delegator.findList("OrderHeader", EntityCondition.makeCondition("orderId", EntityOperator.IN,transList.orderId), null, null, null, false);
        print("grandTotal\n\n\n"+grandTotal);
          context.salesList=grandTotal;
        
		
				
