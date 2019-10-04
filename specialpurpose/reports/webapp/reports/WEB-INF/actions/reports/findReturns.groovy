import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.sql.Timestamp;


returnHeaderTypeId = request.getParameter("returnHeaderTypeId");
returnId = request.getParameter("returnId");
fromPartyId = request.getParameter("fromPartyId");
statusId = request.getParameter("statusId");
fromDate = request.getParameter("minDate");
thruDate = request.getParameter("maxDate");
viewReport = request.getParameter("viewReport");

productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
	productStoreId = session.getAttribute("productStoreId");



int viewIndex = 0;
int viewSize = 0;
int highIndex = 0;
int lowIndex = 0;
int sortResultListSize = 0;
try {
       viewIndex = Integer.valueOf((String) parameters.get("viewIndex")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
try {
    viewSize = Integer.valueOf((String) parameters.get("viewSize")).intValue();
} catch (Exception e) {
    viewSize = 10;
}
lowIndex = (viewIndex * viewSize)+1;
highIndex = (viewIndex + 1) * viewSize; 


List exprs = new ArrayList();
if (productStoreId != null) {
	   exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}
if (returnHeaderTypeId != null && !returnHeaderTypeId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("returnHeaderTypeId", EntityOperator.EQUALS, returnHeaderTypeId));
}
if (fromPartyId != null && !fromPartyId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, fromPartyId));
}
if (statusId != null && !statusId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
}
if (returnId != null && !returnId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("returnId", EntityOperator.EQUALS, returnId));
}
if (fromDate != null && !fromDate.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
}
if (thruDate != null && !thruDate.equals("") ) {
	exprs.add(EntityCondition.makeCondition("entryDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
} 
exprs.add(EntityCondition.makeCondition("returnId", EntityOperator.NOT_EQUAL, null));               

List conditions = new ArrayList();
conditions.addAll(exprs);


returnList = delegator.findByCondition("ReturnsStoreSpecific", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null);

List returnIdsList = new ArrayList();
if(returnList != null){
	returnIdsList = EntityUtil.getFieldListFromEntityList(returnList, "returnId", true);
}



List returnList = new ArrayList();
int listCount = 0;
EntityListIterator eli = null;

try{
	eli = delegator.findListIteratorByCondition("ReturnHeader", EntityCondition.makeCondition("returnId", EntityOperator.IN, returnIdsList), null, UtilMisc.toList("returnId"));

    // attempt to get the full size
    eli.last();
    listCount = eli.currentIndex();

     //get the partial list for this page
    eli.beforeFirst();
    //if (listCount > viewSize.intValue()) {
    	//returnList = eli.getPartialList(lowIndex, viewSize.intValue());
    //} else if (listCount > 0) {
    	returnList = eli.getCompleteList();
    //}
    if (highIndex > listCount) {
        highIndex = listCount;
    }
	} catch (GenericEntityException e) {
		e.printStackTrace();
	} finally {
		if (eli != null) {
			try {
				eli.close();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
	}
context.listIt = returnList;
