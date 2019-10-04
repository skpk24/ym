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

Map<String,Integer> month = new HashMap<String,Integer>();
month.put("ZERO",0);
month.put("JAN",1);
month.put("FEB",2);
month.put("MAR",3);
month.put("APR",4);
month.put("MAY",5);
month.put("JUN",6);
month.put("JUL",7);
month.put("AUG",8);
month.put("SEP",9);
month.put("OCT",10);
month.put("NOV",11);
month.put("DEC",12);


Map<Integer,String> monthInt = new HashMap<Integer,String>();
monthInt.put(1,"JAN");
monthInt.put(2,"FEB");
monthInt.put(3,"MAR");
monthInt.put(4,"APR");
monthInt.put(5,"MAY");
monthInt.put(6,"JUN");
monthInt.put(7,"JUL");
monthInt.put(8,"AUG");
monthInt.put(9,"SEP");
monthInt.put(10,"OCT");
monthInt.put(11,"NOV");
monthInt.put(12,"DEC");


curMonth = UtilDateTime.monthNumber(UtilDateTime.nowTimestamp())+1;
        
monthParam = request.getParameter("month");

if(monthParam == null || monthParam.trim().equals(""))
	monthParam = "ZERO";

mnth = month.get(monthParam);
	if(monthParam == mnth || monthParam.equals("ZERO"))
		mnth = 0;
	else
		mnth = (-1)* (curMonth - mnth);
		
if(monthParam.trim().equals("ZERO"))
	context.month = monthInt.get(curMonth);
else
	context.month = monthParam;
	
if(userLogin)
{
	BigDecimal budAmt = new BigDecimal(0);
	BigDecimal totBudAmtUsed = new BigDecimal(0);
	BigDecimal leftBudAmt = new BigDecimal(0);
	BigDecimal exceedBudAmt = new BigDecimal(0);
	Timestamp startDay=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(),0,mnth);
	Timestamp endDay=UtilDateTime.getMonthEnd(startDay, TimeZone.getDefault(), Locale.getDefault());
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