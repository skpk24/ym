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







/* Report form names for Accounts and Contacts reports */
reportFormName = [:] ;
reportFormName.put("ACC_BY_OPP", "AccountByOpportunity");
reportFormName.put("ACC_BY_PROJ", "AccountByProject");
reportFormName.put("CONT_BY_STAT", "ContactByStatus");
reportFormName.put("MERG_CNT_HIS", "MergeContactHistory");
reportFormName.put("CMP_BY_STAT", "CompaniesByStatus");
reportFormName.put("CMP_BY_IND", "CompaniesByIndustryType");
reportFormName.put("OPP_BY_HIST", "OpportunitiesByHistory");
reportFormName.put("OPP_BY_CMP", "OpportunitiesByCompanies");

reportScreenletName = [:] ;
reportScreenletName.put("ACC_BY_OPP", "Account By Opportunity");
reportScreenletName.put("ACC_BY_PROJ", "Account By Project");
reportScreenletName.put("CONT_BY_STAT", "Contact By Status");
reportScreenletName.put("MERG_CNT_HIS", "Merge Contact History");
reportScreenletName.put("CMP_BY_STAT", "Companies By Status");
reportScreenletName.put("CMP_BY_IND", "Companies By Industry Type");
reportScreenletName.put("OPP_BY_HIST", "Opportunities By History");
reportScreenletName.put("OPP_BY_CMP", "Opportunities By Companies");

reportExcel = [:] ;
reportExcel.put("ACC_BY_OPP", "exportAccountByOpportunityToExcel");
reportExcel.put("ACC_BY_PROJ", "exportAccountByProjectToExcel");
reportExcel.put("CONT_BY_STAT", "exportContactByStatusToExcel");
reportExcel.put("MERG_CNT_HIS", "exportMergeContactHistoryToExcel");
reportExcel.put("CMP_BY_STAT", "exportCompaniesByStatusToExcel");
reportExcel.put("CMP_BY_IND", "exportCompaniesByIndustryTypeToExcel");
reportExcel.put("OPP_BY_HIST", "exportOpportunitiesByHistoryToExcel");
reportExcel.put("OPP_BY_CMP", "exportOpportunitiesByCompaniesToExcel");

String reportType = parameters.get("reportType");
reportSubType = [];
if(!UtilValidate.isEmpty(reportType) && reportType!="--"){
	reportSubType = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, reportType), null, null, null, false);
	context.subReports = reportSubType ;
}





String reportSubTypeParam = parameters.get("enumId");
report = [];

if(!UtilValidate.isEmpty(reportSubTypeParam) && reportSubTypeParam!="--"){
	context.reportName = reportFormName.get(reportSubTypeParam);
	context.reportNameForm = reportFormName.get(reportSubTypeParam);
	context.reportNameScreen =  reportScreenletName.get(reportSubTypeParam);
	context.exportToExcel =  reportExcel.get(reportSubTypeParam);
}

