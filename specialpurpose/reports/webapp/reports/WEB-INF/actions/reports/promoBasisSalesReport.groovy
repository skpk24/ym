import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;


orderId = request.getParameter("orderId");
productPromoId = request.getParameter("productPromoId");
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
	productStoreId = session.getAttribute("productStoreId");
if(productStoreId == null)
	productStoreId = "9001";
List exprs = new ArrayList();

if (orderId != null && !orderId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
}else{
	exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.NOT_EQUAL, null));
}
if (productPromoId != null && !productPromoId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("productPromoId", EntityOperator.EQUALS, productPromoId));
}
if (productStoreId != null) {
	   exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}
List conditions = new ArrayList();
conditions.addAll(exprs);

//set the page parameters
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


List orderList = new ArrayList();
int listCount = 0;
EntityListIterator eli = null;


try{
eli = delegator.findListIteratorByCondition("PromoBasisOrders", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("orderDate"));

	// attempt to get the full size
	eli.last();
	listCount = eli.currentIndex();
	// get the partial list for this page
	eli.beforeFirst();

	orderList = eli.getCompleteList();
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


context.put("listIt",orderList);
context.put("viewIndex", viewIndex);
context.put("viewSize", viewSize);
context.put("listSize", listCount);
context.put("lowIndex", lowIndex);
context.put("highIndex", highIndex);
