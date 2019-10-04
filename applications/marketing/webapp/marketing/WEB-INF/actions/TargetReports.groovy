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


String reportType = parameters.get("enumId");



/* Report form names for Accounts and Contacts reports */
reportFormName = [:] ;
reportFormName.put("COMPANY_TARGET", "CompanyTargetsReportForm");
reportFormName.put("TEAM_TARGET", "TeamTargetsReportForm");
reportFormName.put("INDIVIDUAL_TARGET", "IndividualTargetsReportForm");

reportScreenletName = [:] ;
reportScreenletName.put("COMPANY_TARGET", "Company Targets");
reportScreenletName.put("TEAM_TARGET", "Team Targets");
reportScreenletName.put("INDIVIDUAL_TARGET", "Individual Targets");

reportExcel = [:] ;
reportExcel.put("COMPANY_TARGET", "CompanyTargetsReportExcel");
reportExcel.put("TEAM_TARGET", "TeamTargetsReportExcel");
reportExcel.put("INDIVIDUAL_TARGET", "IndividualTargetsReportExcel");

if(reportType!="--"&&!UtilValidate.isEmpty(reportType)){

context.reportNameScreen=reportScreenletName.get(reportType);
context.reportNameForm=  reportFormName.get(reportType);
context.reportExcel=     reportExcel.get(reportType);

}
return "success"
