import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.marketing.util.SFAUtil;


import org.ofbiz.marketing.report.DashboardServices;
String reportType = parameters.get("dashboardType");
reportSubType = [];


companyByContactsImage =DashboardServices.companyByContactsDashboard(delegator, session,response);
context.companyByContactsImage= companyByContactsImage

print "\n\n\n\n\n\n companyByContactsImage " + companyByContactsImage + "\n\n\n\n\n\n\n\n\n\n"
companyByStatusImage =DashboardServices.companyByStatusDashboard(delegator, session,response);
context.companyByStatusImage= companyByStatusImage
print "\n\n\n\n\n\n companyByStatusImage " + companyByStatusImage + "\n\n\n\n\n\n\n\n\n\n"

companyByRoleImage =DashboardServices.companyByRoleDashboard(delegator, session,response);
context.companyByRoleImage= companyByRoleImage
print "\n\n\n\n\n\n companyByRoleImage " + companyByRoleImage + "\n\n\n\n\n\n\n\n\n\n"

//
//if(!UtilValidate.isEmpty(reportType) && reportType!="--"){
//	reportSubType = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, reportType), null, null, null, false);
//	context.subReports = reportSubType ;
//}
//
//
//
//
//String reportSubTypeParam = parameters.get("enumId");
//report = [];
//
//if(!UtilValidate.isEmpty(reportSubTypeParam) && reportSubTypeParam!="--"){
//
//	if(reportSubTypeParam.compareTo("CMPNY_BY_SRC")==0){
//		imageLocation =DashboardServices.companyByContactsDashboard(delegator, session,response);
//		context.dashboardScreenletName= "Company By Contacts Dashboard"
//		context.imageLocation= imageLocation
//	}
//
//	if(reportSubTypeParam.compareTo("CMPNY_BY_STA")==0){
//		imageLocation =DashboardServices.companyByStatusDashboard(delegator, session,response);
//		context.dashboardScreenletName= "Company By Status Dashboard"
//		context.imageLocation= imageLocation
//	}
//
//	if(reportSubTypeParam.compareTo("CMPNY_BY_ROLE")==0){
//		imageLocation = DashboardServices.companyByRoleDashboard(delegator, session,response);
//		context.dashboardScreenletName= "Company By Role Dashboard"
//		context.imageLocation= imageLocation
//	}
//
//	if(reportSubTypeParam.compareTo("OPP_BY_COM_SIZ")==0){
//		imageLocation = DashboardServices.opportunitiesByCompanySize(delegator, session,response);
//		context.dashboardScreenletName= "Opportunities By Company Size"
//		context.imageLocation= imageLocation
//	}
//
//	if(reportSubTypeParam.compareTo("OPP_BY_STA")==0){
//		imageLocation = DashboardServices.opportunitiesByStatus(delegator, session,response);
//		context.dashboardScreenletName= "Opportunities By Status"
//		context.imageLocation= imageLocation
//	}
//}

