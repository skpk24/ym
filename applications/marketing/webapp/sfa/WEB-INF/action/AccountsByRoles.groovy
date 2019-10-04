
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.ConnectionFactory;


leadNames = []
companyCount = []

List companyRoles = null;
		try {
			companyRoles = delegator.findList("PartyRole", EntityCondition
					.makeCondition("roleTypeId", EntityOperator.EQUALS,
							"PROSPECT_LEAD"), null, null, null, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
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
				  dataset.setValue(l,  companyId ,  companyId.toString() );
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