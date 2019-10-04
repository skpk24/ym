import java.lang.*;
import java.sql.Timestamp;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.accounting.payment.PaymentWorker;


GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

if(userLogin)
{
	BigDecimal budAmt = new BigDecimal(0);
	BigDecimal totBudAmtUsed = new BigDecimal(0);
	BigDecimal leftBudAmt = new BigDecimal(0);
	BigDecimal exceedBudAmt = new BigDecimal(0);
	Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
	Timestamp startDay=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
	Map pIds =  new HashMap();
	Map finalBudgetData =  new HashMap();
	
	ecl = EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
		EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.partyId)],
	EntityOperator.AND);
	List orderBy = UtilMisc.toList("-createdDate");
	List <GenericValue>budgetPlansList = delegator.findList("BudgetPlans", ecl, UtilMisc.toSet("categoryId","budgetAmount"), orderBy, null, false);
	
	if(UtilValidate.isNotEmpty(budgetPlansList))
	{
		context.budgetPlansList = budgetPlansList;
	}
	//condition for order item purchased by user acc to category//
	ecl1 = EntityCondition.makeCondition([
		EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(startDay, "Timestamp", null, null)),
		EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO, ObjectType.simpleTypeConvert(endDay, "Timestamp", null, null)),
		EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"),
		EntityCondition.makeCondition("changeByUserLoginId", EntityOperator.EQUALS, userLogin.userLoginId)],
	EntityOperator.AND);
	List orderBy1 = UtilMisc.toList("-createdStamp");
	List <GenericValue>budgetPlansItemsList = delegator.findList("OrderItem", ecl1, UtilMisc.toSet("parentProductCategoryId","unitPrice","quantity"), orderBy1, null, false);
	
	Iterator <GenericValue> budPlanLst = budgetPlansItemsList.iterator();
	while (budPlanLst.hasNext())
	{
		GenericValue budgetItm = budPlanLst.next();
		if(budgetItm.get("parentProductCategoryId") !=  null)
		{
			amt = java.math.BigDecimal.ZERO;
			if(pIds.containsKey(budgetItm.get("parentProductCategoryId")))
			{
				budAmt = pIds.get(budgetItm.get("parentProductCategoryId"));
				amt = budAmt.add(budgetItm.get("unitPrice").multiply(budgetItm.get("quantity")));
			}else{
				amt = budgetItm.get("quantity").multiply(budgetItm.get("unitPrice"));
				}
			pIds.put(budgetItm.get("parentProductCategoryId"), amt);
		}
	}
	List listpIds = new LinkedList(pIds.entrySet());
	if(UtilValidate.isNotEmpty(listpIds)){
	Iterator <GenericValue> budPlansList = budgetPlansList.iterator();
	while (budPlansList.hasNext())
	{
		GenericValue budgetItm = budPlansList.next();
				for (Iterator its = listpIds.iterator(); its.hasNext();) {
					Map.Entry entry = (Map.Entry) its.next();
					List dataLst = new ArrayList();
					if((entry.getKey()).equals(budgetItm.get("categoryId")))
					{
						
						totBudAmtUsed = entry.getValue();
						leftBudAmt = budgetItm.get("budgetAmount").subtract(totBudAmtUsed);
						dataLst.add(budgetItm.get("budgetAmount"));
						dataLst.add(totBudAmtUsed);
						dataLst.add(leftBudAmt);
						finalBudgetData.put(budgetItm.get("categoryId"), dataLst);
					}
					else
					{
						if(!pIds.containsKey(budgetItm.get("categoryId"))){
						leftBudAmt = budgetItm.get("budgetAmount").subtract(new BigDecimal(0));
						dataLst.add(budgetItm.get("budgetAmount"));
						dataLst.add(new BigDecimal(0));
						dataLst.add(leftBudAmt);
						finalBudgetData.put(budgetItm.get("categoryId"), dataLst);
						}}
				}
	}
}else
{
	Iterator <GenericValue> budPlansList = budgetPlansList.iterator();
	while (budPlansList.hasNext())
	{
		GenericValue budgetItm = budPlansList.next();
		
			if(budgetItm.get("categoryId") != null)
			{
					List<BigDecimal> dataLst = new ArrayList<BigDecimal>();
						leftBudAmt = budgetItm.get("budgetAmount").subtract(new BigDecimal(0));
						dataLst.add(budgetItm.get("budgetAmount"));
						dataLst.add(new BigDecimal(0));
						dataLst.add(leftBudAmt);
						finalBudgetData.put(budgetItm.get("categoryId"), dataLst);
				
			}
	}
	
	}
	context.finalBudgetData = finalBudgetData;
}