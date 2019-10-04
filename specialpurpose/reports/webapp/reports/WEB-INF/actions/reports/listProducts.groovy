import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.report.Report;

productId = request.getParameter("productId");
internalName = request.getParameter("internalName");
productStoreId = request.getParameter("productStoreId");
if(productStoreId == null)
	productStoreId = session.getAttribute("productStoreId");

List exprs = new ArrayList();
if (productStoreId != null) {
	   //exprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
}
if (productId != null && !productId.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
}
if (internalName != null && !internalName.equals("") ) {
	   exprs.add(EntityCondition.makeCondition("internalName", EntityOperator.LIKE, "%"+internalName+"%"));
}

if(productId == null && internalName == null){
	productList = Report.getStoreProductIdsList(delegator, productStoreId);
	exprs.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
}else if(productId.equals("") && internalName.equals("")){
	productList = Report.getStoreProductIdsList(delegator, productStoreId);
	exprs.add(EntityCondition.makeCondition("productId", EntityOperator.IN, productList));
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


List productList = new ArrayList();
int listCount = 0;
EntityListIterator eli = null;


try{
  eli = delegator.findListIteratorByCondition("Product", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("productId"));
  
	// attempt to get the full size
	eli.last();
	listCount = eli.currentIndex();
	// get the partial list for this page
	eli.beforeFirst();

	productList = eli.getCompleteList();
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

context.put("listIt",productList);
context.put("viewIndex", viewIndex);
context.put("viewSize", viewSize);
context.put("listSize", listCount);
context.put("lowIndex", lowIndex);
context.put("highIndex", highIndex);


