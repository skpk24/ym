package org.ofbiz.marketing.report;

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

//import javolution.util.FastList;

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

public class DashboardServices {
	private static final long serialVersionUID = 1L;
	
	/**
	 * @author balakrishna.prabhakar	 
	 * simple utility to get the exact location where browsed file has been saved on local system  
	 * @return
	 */
	public static String getUploadPath() {
		return System.getProperty("user.dir") + File.separatorChar + "runtime"	+ File.separatorChar + "data" + File.separatorChar;	
	}
	
	
	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for companies by no of contacts</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String companyByContactsDashboard(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {
         
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		try {
			companyRoles = delegator.findList("PartyRole", EntityCondition
					.makeCondition("roleTypeId", EntityOperator.EQUALS,
							"INTERNAL_ORGANIZATIO"), null, null, null, false);
		} catch (Exception e) {
			// TODO: handle exception
		}
		for (int lc = 0; lc < companyRoles.size(); lc++) {
			GenericValue gv = (GenericValue) companyRoles.get(lc);
		//	List cond = FastList.newInstance();
			List cond = new ArrayList();
			cond.add(EntityCondition.makeCondition("partyIdFrom",EntityOperator.EQUALS, gv.getString("partyId")));
			cond.add(EntityCondition.makeCondition("roleTypeIdTo",EntityOperator.EQUALS, "CONTACT"));

			List companySize = null;
			try {
				companySize = delegator
						.findList("PartyRelationship", EntityCondition
								.makeCondition(cond, EntityOperator.AND), null,
								null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  String companyId = gv.getString("partyId");
			  dataset.setValue(companySize.size(),  companyId ,  companyId.toString() );
		}
		
		

		  JFreeChart chart = ChartFactory.createLineChart("Lead By Source","Lead Name", "No.Of Contacts", dataset,PlotOrientation.VERTICAL, false,true, false);
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  ChartFrame frame1=new ChartFrame("Lead Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);

		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
			File dir = new File(imageServerPath);

			File directory = new File(imageServerPath);
			File []listFiles = directory.listFiles();
			   for (File file2 : listFiles) file2.delete();
			  				// Move file to new directory
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}

	
	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for opportunities by company size</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String  opportunitiesByCompanySize(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		
		
			try {
				companyRoles = delegator.findList("Enumeration",  EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "CMPNY_SIZE"), null, UtilMisc.toList("sequenceId") , null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		for (int lc = 0; lc < companyRoles.size(); lc++) {
			  Map statusMap = (Map) companyRoles.get(lc);
			  String statusId = (String) statusMap.get("enumId");
			  List companySize = null;
				try {
					companySize = delegator.findList("SalesOpportunity", EntityCondition.makeCondition("companySize",EntityOperator.EQUALS,statusId), null,null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			  GenericValue statusDesc = null;
			  try {
				  statusDesc = delegator.findByPrimaryKey("Enumeration", UtilMisc.toMap("enumId", statusId) );
						  } catch (GenericEntityException e) {
				// TODO: handle exception
			  }
			  dataset.setValue(companySize.size(),  "Status" , statusDesc.getString("description") );
		}
		
		

		  JFreeChart chart = ChartFactory.createLineChart("Opportunity By Lead Size","Lead Size", "No.Of Opportunities", dataset,PlotOrientation.VERTICAL, false,true, false);
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  ChartFrame frame1=new ChartFrame("Opportunity Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);
		  
		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
			File dir = new File(imageServerPath);

			File directory = new File(imageServerPath);
			File []listFiles = directory.listFiles();
	          
//			for (File file2 : listFiles) file2.delete(); 
			// Move file to new directory
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}

	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for companies by role</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String companyByRoleDashboard(GenericDelegator delegator,HttpSession session,HttpServletResponse  response) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		
		
			try {
				companyRoles = delegator.findList("Party",  EntityCondition.makeCondition("partyTypeId", EntityOperator.EQUALS, "PARTY_GROUP"), UtilMisc.toSet("partyId"), null, null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			List<String> partyIds = new ArrayList<String>() ;
			
			for (int lc = 0; lc < companyRoles.size(); lc++) {
				
				 Map statusMap = (Map) companyRoles.get(lc);
				 String partyId = (String) statusMap.get("partyId");
				 partyIds.add(partyId) ;
			}
			
			try {
				companyRoles = delegator.findList("PartyRole",  EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds), 
						UtilMisc.toSet("roleTypeId"), null, null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
         
          Set<String> uniqueRoles = new HashSet<String>() ;
			
          for (int lc = 0; lc < companyRoles.size(); lc++) {
				 Map statusMap = (Map) companyRoles.get(lc);
				 String roleTypeId = (String) statusMap.get("roleTypeId");
				 uniqueRoles.add(roleTypeId) ;
			}
          List<String> roleTypeIds = new ArrayList<String>(uniqueRoles) ;
          
		for (int lc = 0; lc < roleTypeIds.size(); lc++) {
			  List companySize = null;
			  String role = roleTypeIds.get(lc) ;
				try {
					companySize = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,role), null,null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			  GenericValue roleDesc = null;
			  try {
				  roleDesc = delegator.findByPrimaryKey("RoleType", UtilMisc.toMap("roleTypeId", role) );
						  } catch (GenericEntityException e) {
				// TODO: handle exception
			  }
			  dataset.setValue(companySize.size(),  "Status" , roleDesc.getString("description") );
		}
		  JFreeChart chart = ChartFactory.createLineChart("Lead By Roles","Lead Role", "No.Of Companies", dataset,PlotOrientation.VERTICAL, false,true, false);
		  
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  ChartFrame frame1=new ChartFrame("Lead Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);

		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
	
		   File dir = new File(imageServerPath);
		   File directory = new File(imageServerPath);
		   File []listFiles = directory.listFiles();
          
//		   for (File file2 : listFiles) file2.delete();
		  	
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}
	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for companies by status</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String companyByStatusDashboard(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		
		
			try {
				companyRoles = delegator.findList("StatusItem",  EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "SFA_ERP_LEAD_STATUS"), null, null, null, false);
			} catch (Exception e) {
				// TODO: handle exception
			}
		
		for (int lc = 0; lc < companyRoles.size(); lc++) {
			  Map statusMap = (Map) companyRoles.get(lc);
			  String statusId = (String) statusMap.get("statusId");
			  
			  List companySize = null;
				try {
					companySize = delegator.findList("Party", EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId), null,null, null, false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			  GenericValue statusDesc = null;
			  try {
				  statusDesc = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId) );
						  } catch (GenericEntityException e) {
				// TODO: handle exception
			  }
			  dataset.setValue(companySize.size(),  "Status" , statusDesc.getString("description") );
		}
		
		

		  JFreeChart chart = ChartFactory.createLineChart("Lead By Status","Lead Status", "No.Of Companies", dataset,PlotOrientation.VERTICAL, false,true, false);
		  
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.ORANGE); 
		  ChartFrame frame1=new ChartFrame("Lead Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);
		  
		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
			File dir = new File(imageServerPath);

			File directory = new File(imageServerPath);
			File []listFiles = directory.listFiles();
	          
//			for (File file2 : listFiles) file2.delete(); 
			// Move file to new directory
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}
	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for opportunities by status</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String  opportunitiesByStatus(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	List companyRoles = null;
	
	
	try {
		companyRoles = delegator.findList("StatusItem",  EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "SFA_ERP_LEAD_STATUS"), null, null, null, false);
	} catch (Exception e) {
		// TODO: handle exception
	}

for (int lc = 0; lc < companyRoles.size(); lc++) {
	  Map statusMap = (Map) companyRoles.get(lc);
	  String statusId = (String) statusMap.get("statusId");
	  
	  List companySize = null;
		try {
			companySize = delegator.findList("SalesOpportunity", EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,statusId), null,null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	  GenericValue statusDesc = null;
	  try {
		  statusDesc = delegator.findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId) );
				  } catch (GenericEntityException e) {
		// TODO: handle exception
	  }
	  dataset.setValue(companySize.size(),  "Status" , statusDesc.getString("description") );
}


  JFreeChart chart = ChartFactory.createLineChart("Company By Status","Company Status", "No.Of Companies", dataset,PlotOrientation.VERTICAL, false,true, false);
  chart.getTitle().setPaint(Color.GREEN); 
  CategoryPlot p = chart.getCategoryPlot(); 
  p.setRangeGridlinePaint(Color.green); 
  ChartFrame frame1=new ChartFrame("Company Dashboard",chart);
  frame1.setVisible(false);
  frame1.setSize(400,350);
  
  String imagelocation = null;
  try {
		    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
	String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
	File file = new File(filePath);
	// Destination directory
	File dir = new File(imageServerPath);

	File directory = new File(imageServerPath);
	File []listFiles = directory.listFiles();
      
//	for (File file2 : listFiles) file2.delete(); 
	// Move file to new directory
	boolean success = file.renameTo(new File(dir, file.getName()));
	file.delete();
	return imagelocation;}
	
	
	
	/**
	 * @author Balakrishna Prabhakar
	 * <b>Bar chart generation for Roles by no of contacts</b> 
	 * @param delegator
	 * @param session
	 * @return
	 */
	
	public static String contactsByRole(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {
         
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		try {
			companyRoles = delegator.findList("PartyRole", EntityCondition
					.makeCondition("roleTypeId", EntityOperator.EQUALS,
							"LEAD"), null, null, null, false);
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
				  }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		

		  JFreeChart chart = ChartFactory.createLineChart("Contacts By Roles","Roles", "No.Of Contacts", dataset,PlotOrientation.VERTICAL, false,true, false);
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  ChartFrame frame1=new ChartFrame("Contact Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);

		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
			File dir = new File(imageServerPath);

			  				// Move file to new directory
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}

	public static String contactsByOpportunities(GenericDelegator delegator,HttpSession session,HttpServletResponse response) {
        
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		List companyRoles = null;
		try {
			companyRoles = delegator.findList("PartyRole", EntityCondition
					.makeCondition("roleTypeId", EntityOperator.EQUALS,
							"ACCOUNT"), null, null, null, false);
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

			String query ="select * from Sales_Opportunity_Role where party_Id = "+ companyId ;
		
			
			  try {
					statement.execute(query);
					rs = statement.getResultSet();
				 if(rs.last()) {
				  int l = rs.getRow();
				  dataset.setValue(l,  companyId ,  companyId.toString() );
				  }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		  JFreeChart chart = ChartFactory.createLineChart("Opportunities By Roles","Roles", "No.Of Opportunities", dataset,PlotOrientation.VERTICAL, false,true, false);
		  chart.getTitle().setPaint(Color.GREEN); 
		  CategoryPlot p = chart.getCategoryPlot(); 
		  p.setRangeGridlinePaint(Color.red); 
		  ChartFrame frame1=new ChartFrame("Contact Dashboard",chart);
		  frame1.setVisible(false);
		  frame1.setSize(400,350);

		  String imagelocation = null;
		  try {
  			    imagelocation = ServletUtilities.saveChartAsPNG(chart, 500, 400, session);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
			String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/sfa/dashboards/";
			File file = new File(filePath);
			// Destination directory
			File dir = new File(imageServerPath);
			
			boolean success = file.renameTo(new File(dir, file.getName()));
			file.delete();
			return imagelocation;
	}


}
