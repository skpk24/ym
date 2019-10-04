import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;


List companyRoles = null;
		try {
			companyRoles = delegator.findList("PartyRole", EntityCondition
					.makeCondition("roleTypeId", EntityOperator.EQUALS,
							"PROSPECT_LEAD"), null, null, null, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
leadNames = []
companyCount = []


		for (int lc = 0; lc < companyRoles.size(); lc++) {
			GenericValue gv = (GenericValue) companyRoles.get(lc);

			Connection connection = null;
			Statement statement = null;
			ResultSet rs = null;
			
			String helperName = delegator.getGroupHelperName("org.ofbiz");
			try {
				connection = ConnectionFactory.getConnection(helperName);
				statement = connection.createStatement();
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			  String companyId = gv.getString("partyId");

			String query ="select from Party_Relationship where party_Id_To = "+ companyId +"  group by role_Type_Id_From ";
		
			
			  try {
					statement.execute(query);
					rs = statement.getResultSet();
				 if(rs.last()) {
				  int l = rs.getRow();
                   leadNames.add ( delegator.findByPrimaryKey("Person",[partyId:companyId]).firstName );
		  		   companyCount.add(l)

				  }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
context.cmpnyByCntTargetsReportNames = leadNames
context.cmpnyByCntTargetsReportTargetCost =companyCount
