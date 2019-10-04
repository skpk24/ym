import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityWhereString;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.marketing.util.SFAUtil;


import org.ofbiz.marketing.report.DashboardServices;

import org.ofbiz.marketing.report.DashboardServices;
String reportType = parameters.get("dashboardType");
reportSubType = [];


companyByContactsImage =DashboardServices.companyByContactsDashboard(delegator, session,response);
context.companyByContactsImage= companyByContactsImage


companyByStatusImage =DashboardServices.companyByStatusDashboard(delegator, session,response);
context.companyByStatusImage= companyByStatusImage

companyByRoleImage =DashboardServices.companyByRoleDashboard(delegator, session,response);
context.companyByRoleImage= companyByRoleImage

opportunitiesByCompanySizeImage = DashboardServices.opportunitiesByCompanySize(delegator, session,response);
context.opportunitiesByCompanySizeImage= opportunitiesByCompanySizeImage

opportunitiesByStatusImage = DashboardServices.opportunitiesByStatus(delegator, session,response);
context.opportunitiesByStatusImage= opportunitiesByStatusImage

contactsByRoleImage = DashboardServices.contactsByRole (delegator, session,response);
context.contactsByRoleImage= contactsByRoleImage


contactsByOpportunitiesImage = DashboardServices.contactsByOpportunities (delegator, session,response);
context.contactsByOpportunitiesImage= contactsByOpportunitiesImage







