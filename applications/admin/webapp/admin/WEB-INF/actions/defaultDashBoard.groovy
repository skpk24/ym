import org.ofbiz.accounting.Reports;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;
Map reportMap=new HashMap();
imagelocation = Reports.viewSalesDaily(delegator, session);
context.imagelocation = imagelocation
user=userLogin.userLoginId;
print("the user\n\n\n\n\n\n"+user);
 List orderBy = UtilMisc.toList("-createdStamp");
urlList = delegator.findList("Visit", EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,user), UtilMisc.toSet("initialRequest","webappName"), orderBy, null, true);
context.urlList=urlList;
context.partyId=user;
 g=delegator.findByPrimaryKey("NoteParty",UtilMisc.toMap("partyId",partyId));
 if(g!=null)
 {
noteInfo= g.get("noteInfo");
context.noteInfo=noteInfo
}
List reportList=new ArrayList();
     perList=delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,user), UtilMisc.toSet("groupId"), null, null, true);
     Iterator itr = perList.iterator();
				while(itr.hasNext()){
					GenericValue genVal = (GenericValue) itr.next();
					groupId = genVal.get("groupId");
					if(groupId.equals("REPORT_SALES"))
					{
					reportList.add("Daily Sales Report");
					reportList.add("Weekly Sales Report");
					reportList.add("Monthly Sales Report");
					}
					if(groupId.equals("REPORT_PURCHASE"))
					{
					reportList.add("Daily purchase Report");
					reportList.add("Weekly purchase Report");
					reportList.add("Monthly purchase Report");
					}
					if(groupId.equals("REPORT_CUSTOMER"))
					{
					reportList.add("Daily customer Report");
					reportList.add("Weekly customer Report");
					reportList.add("Monthly customer Report");
					}
					}
					
		context.reportList=reportList	
		context.totalExpanse = org.ofbiz.order.report.Report.totalExpanse(delegator);		
					


