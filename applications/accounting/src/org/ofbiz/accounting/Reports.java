package org.ofbiz.accounting;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
 

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.VerticalAlignment;
 
import javax.servlet.http.HttpSession;
import javax.swing.JFrame;
 
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

 
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;


 


public class Reports {
	
	  public static String viewDailyDashboard(GenericDelegator delegator, HttpSession session)
      {
		  double cashPer = 0;
		  double creditCardPer = 0;
		  double creditPer = 0;
           String imagelocation ="";
         
           Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
           DefaultPieDataset dataset = new DefaultPieDataset();
            try
            {
            	List<EntityCondition> dateCondiList = new ArrayList();
            	List<EntityCondition> paymentCondn = new ArrayList();
            	List<EntityCondition> paymentCondn1 = new ArrayList();
            	List<EntityCondition> paymentCondn2 = new ArrayList();
            	
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                 EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
                 paymentCondn.add(dateOr);
                 paymentCondn.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CASH"));
                 paymentCondn1.add(dateOr);
                 paymentCondn1.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CREDIT_CARD"));
                 paymentCondn2.add(dateOr);
                 paymentCondn2.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"EXT_CREDIT"));
      
            	
                  
                       List <GenericValue>payment = (List <GenericValue>)delegator.findList("OrderPaymentPreference", null, UtilMisc.toSet("paymentMethodTypeId"), null, null, true);
          
            	 long total = delegator.findCountByCondition("OrderPaymentPreference",dateOr,null, null);
            	 
            	
                  
                  long count = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn,EntityOperator.AND),null, null);
                  long countCreditCard = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn1,EntityOperator.AND),null, null);
                  long countCredit = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn2,EntityOperator.AND),null, null);
                  //System.out.println(total+"\n"+count+"\n"+countCreditCard+"\n"+countCredit);
                 if(total!=0)
                 {
                	 if(count!=0)
                  cashPer=(100*count)/total;
                 if(countCreditCard!=0)
                	 creditCardPer=(100*countCreditCard)/total;
                 if(countCredit!=0)
                  creditPer=(100*countCredit)/total;
                
                 }    
                 	dataset.setValue("cash",cashPer);
               
                  dataset.setValue("Credit Card",creditCardPer);
                
                  dataset.setValue("Credit",creditPer);
                  // set up the chart
            JFreeChart chart = ChartFactory.createPieChart3D
            ("Payment Report", dataset, true,true,true);
             //chart.setBackgroundPaint(Color.magenta);
             chart.setBorderVisible(false);
             chart.setBackgroundPaint(Color.pink);
             //chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
             
             // get a reference to the plot for further customisation...
             PiePlot3D plot = (PiePlot3D) chart.getPlot();
             plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
             plot.setNoDataMessage("No data available");
             plot.setCircular(false);
             plot.setBackgroundPaint(Color.white);
             plot.setLabelGap(0.02);
             plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                     "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
            ));
             // save as a png and return the file name
             String var =  ServletUtilities.getTempFilePrefix();
             //System.out.println("\n\n ######## the temp File prefix="+var);
             imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
             //System.out.println("\n\n\n\n\n\n imagelocation"+imagelocation);
            }
            catch(Exception e)
            {
                  e.printStackTrace();
            }
            String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
            for (File file2 : listFiles) file2.delete();
           
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
            
      }
	  
	  public static String viewWeeklyDashboard(GenericDelegator delegator, HttpSession session)
      {
		  double cashPer = 0;
		  double creditCardPer = 0;
		  double creditPer = 0;
           String imagelocation ="";
         
           Timestamp d=UtilDateTime.getWeekEnd(UtilDateTime.nowTimestamp());
           Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
           DefaultPieDataset dataset = new DefaultPieDataset();
           //System.out.println("the timeStamp\n\n\n\n\n\n\n\n\n\n"+d+d1);
            try
            {
            	List<EntityCondition> dateCondiList = new ArrayList();
            	List<EntityCondition> paymentCondn = new ArrayList();
            	List<EntityCondition> paymentCondn1 = new ArrayList();
            	List<EntityCondition> paymentCondn2 = new ArrayList();
            	
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                 EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
                 paymentCondn.add(dateOr);
                 paymentCondn.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CASH"));
                 paymentCondn1.add(dateOr);
                 paymentCondn1.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CREDIT_CARD"));
                 paymentCondn2.add(dateOr);
                 paymentCondn2.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"EXT_CREDIT"));
      
            	
                  
                       List <GenericValue>payment = (List <GenericValue>)delegator.findList("OrderPaymentPreference", null, UtilMisc.toSet("paymentMethodTypeId"), null, null, true);
          
            	 long total = delegator.findCountByCondition("OrderPaymentPreference",dateOr,null, null);
            	 
            	
                  
                  long count = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn,EntityOperator.AND),null, null);
                  long countCreditCard = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn1,EntityOperator.AND),null, null);
                  long countCredit = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn2,EntityOperator.AND),null, null);
                  //System.out.println(total+"\n"+count+"\n"+countCreditCard+"\n"+countCredit);
                 
                  if(total!=0)
                  {
                 	 if(count!=0)
                   cashPer=(100*count)/total;
                  if(countCreditCard!=0)
                 	 creditCardPer=(100*countCreditCard)/total;
                  if(countCredit!=0)
                   creditPer=(100*countCredit)/total;
                 
                  }    
                
                  
                  dataset.setValue("cash",cashPer);
               
                  dataset.setValue("Credit Card",creditCardPer);
                
                  dataset.setValue("Credit",creditPer);
                  // set up the chart
            JFreeChart chart = ChartFactory.createPieChart3D
            ("Weekly Payment Report", dataset, true,true,true);
             chart.setBackgroundPaint(Color.pink);
             chart.setBorderVisible(false);
             chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
             
             // get a reference to the plot for further customisation...
             PiePlot3D plot = (PiePlot3D) chart.getPlot();
             plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 6));
             plot.setNoDataMessage("No data available");
             plot.setCircular(false);
             plot.setLabelGap(0.02);
             plot.setBackgroundPaint(Color.white);
             plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                     "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
            ));
             // save as a png and return the file name
             String var =  ServletUtilities.getTempFilePrefix();
             //System.out.println("\n\n ######## the temp File prefix="+var);
             imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
            }
            catch(Exception e)
            {
                  e.printStackTrace();
            }
            String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
      
	  public static String viewMonthlyDashboard(GenericDelegator delegator, HttpSession session)
      {
		  double cashPer = 0;
		  double creditCardPer = 0;
		  double creditPer = 0;
           String imagelocation ="";
         
           Timestamp d=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault());
           Timestamp d1=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
           int month=UtilDateTime.getMonth(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault());
           DefaultPieDataset dataset = new DefaultPieDataset();
           //System.out.println("the timeStamp\n\n\n\n\n\n\n\n\n\n"+d+d1);
           //System.out.println("the month\n\n\n\n\n\n\n\n\n\n"+month);
            try
            {
            	List<EntityCondition> dateCondiList = new ArrayList();
            	List<EntityCondition> paymentCondn = new ArrayList();
            	List<EntityCondition> paymentCondn1 = new ArrayList();
            	List<EntityCondition> paymentCondn2 = new ArrayList();
            	
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                 EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
                 paymentCondn.add(dateOr);
                 paymentCondn.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CASH"));
                 paymentCondn1.add(dateOr);
                 paymentCondn1.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CREDIT_CARD"));
                 paymentCondn2.add(dateOr);
                 paymentCondn2.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"EXT_CREDIT"));
      
            	
                  
                       List <GenericValue>payment = (List <GenericValue>)delegator.findList("OrderPaymentPreference", null, UtilMisc.toSet("paymentMethodTypeId"), null, null, true);
          
            	 long total = delegator.findCountByCondition("OrderPaymentPreference",dateOr,null, null);
            	 
            	
                  
                  long count = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn,EntityOperator.AND),null, null);
                  long countCreditCard = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn1,EntityOperator.AND),null, null);
                  long countCredit = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn2,EntityOperator.AND),null, null);
                  //System.out.println(total+"\n"+count+"\n"+countCreditCard+"\n"+countCredit);
                 
                  if(total!=0)
                  {
                 	 if(count!=0)
                   cashPer=(100*count)/total;
                  if(countCreditCard!=0)
                 	 creditCardPer=(100*countCreditCard)/total;
                  if(countCredit!=0)
                   creditPer=(100*countCredit)/total;
                 
                  }    
                
                  
                  dataset.setValue("cash",cashPer);
               
                  dataset.setValue("Credit Card",creditCardPer);
                
                  dataset.setValue("Credit",creditPer);
                  // set up the chart
            JFreeChart chart = ChartFactory.createPieChart3D
            ("Monthly Payment Report", dataset, true,true,true);
             chart.setBackgroundPaint(Color.pink);
             chart.setBorderVisible(false);
             chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
             
             // get a reference to the plot for further customisation...
             PiePlot3D plot = (PiePlot3D) chart.getPlot();
             plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
             plot.setNoDataMessage("No data available");
             plot.setCircular(false);
             plot.setLabelGap(0.02);
             plot.setBackgroundPaint(Color.white);
             plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                     "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
            ));
             // save as a png and return the file name
             String var =  ServletUtilities.getTempFilePrefix();
             //System.out.println("\n\n ######## the temp File prefix="+var);
             imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
            }
            catch(Exception e)
            {
                  e.printStackTrace();
            }
            String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  public static String viewQuaterlyDashboard(GenericDelegator delegator, HttpSession session)
      {
		  double cashPer = 0;
		  double creditCardPer = 0;
		  double creditPer = 0;
           String imagelocation ="";
           Timestamp end = null;
           Timestamp start = null;
           Timestamp today=UtilDateTime.nowTimestamp();
           int m=today.getMonth();
           int m1=m+1;
           if(m1%3==0)
           {
        	   start=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(), 0,-2,TimeZone.getDefault(), Locale.getDefault());
        		   end=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault());
           }
           if(m1%3==1)
           {
        	  start=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(),TimeZone.getDefault(), Locale.getDefault()); 
        	 
        	  Timestamp end1=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(), 0, 2,TimeZone.getDefault(), Locale.getDefault());
        	  end=UtilDateTime.getMonthEnd(end1,TimeZone.getDefault(), Locale.getDefault());
        	  
           }
           if(m1%3==2)
           {
        	   start=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(), 0, -1,TimeZone.getDefault(), Locale.getDefault()); 
          	 
         	  Timestamp end1=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp(), 0, +1,TimeZone.getDefault(), Locale.getDefault());
         	  end=UtilDateTime.getMonthEnd(end1,TimeZone.getDefault(), Locale.getDefault());
           }
           //System.out.println("the starting and ending"+start+end);
          
           DefaultPieDataset dataset = new DefaultPieDataset();
         
            try
            {
            	List<EntityCondition> dateCondiList = new ArrayList();
            	List<EntityCondition> paymentCondn = new ArrayList();
            	List<EntityCondition> paymentCondn1 = new ArrayList();
            	List<EntityCondition> paymentCondn2 = new ArrayList();
            	
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO,start ));
            	dateCondiList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO,end));
                 EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
                 paymentCondn.add(dateOr);
                 paymentCondn.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CASH"));
                 paymentCondn1.add(dateOr);
                 paymentCondn1.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"CREDIT_CARD"));
                 paymentCondn2.add(dateOr);
                 paymentCondn2.add(EntityCondition.makeCondition("paymentMethodTypeId", EntityOperator.EQUALS,"EXT_CREDIT"));
      
            	
                  
                       List <GenericValue>payment = (List <GenericValue>)delegator.findList("OrderPaymentPreference", null, UtilMisc.toSet("paymentMethodTypeId"), null, null, true);
          
            	 long total = delegator.findCountByCondition("OrderPaymentPreference",dateOr,null, null);
            	 
            	
                  
                  long count = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn,EntityOperator.AND),null, null);
                  long countCreditCard = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn1,EntityOperator.AND),null, null);
                  long countCredit = delegator.findCountByCondition("OrderPaymentPreference",EntityCondition.makeCondition(paymentCondn2,EntityOperator.AND),null, null);
                  //System.out.println(total+"\n"+count+"\n"+countCreditCard+"\n"+countCredit);
                 
                  if(total!=0)
                  {
                 	 if(count!=0)
                   cashPer=(100*count)/total;
                  if(countCreditCard!=0)
                 	 creditCardPer=(100*countCreditCard)/total;
                  if(countCredit!=0)
                   creditPer=(100*countCredit)/total;
                 
                  }    
                  
                  dataset.setValue("cash",cashPer);
               
                  dataset.setValue("Credit Card",creditCardPer);
                
                  dataset.setValue("Credit",creditPer);
                  // set up the chart
            JFreeChart chart = ChartFactory.createPieChart3D
            ("Quarterly Payment Report", dataset, true,true,true);
             chart.setBackgroundPaint(Color.pink);
            
             chart.setBorderVisible(false);
             chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
             
             // get a reference to the plot for further customisation...
             PiePlot3D plot = (PiePlot3D) chart.getPlot();
             plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
             plot.setNoDataMessage("No data available");
             plot.setCircular(false);
             plot.setBackgroundPaint(Color.white);
             plot.setLabelGap(0.02);
             plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                     "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
            ));
             // save as a png and return the file name
             String var =  ServletUtilities.getTempFilePrefix();
             //System.out.println("\n\n ######## the temp File prefix="+var);
             imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
            }
            catch(Exception e)
            {
                  e.printStackTrace();
            }
            String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  public static String viewSalesDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null; 
	       
		  int start=9;
   	   int interval=3;
   	   int end=24;
	        final String series1 = "Today";
	        final String series2 = "YesterDay";
	        final String series3 = "Last Month Same Day";
	        List<EntityCondition> dateCondiList=new ArrayList();
	        List<EntityCondition> datePreCondiList=new ArrayList();
	        List<EntityCondition> dateMonthCondiList=new ArrayList();
	        List<EntityCondition> condnList=new ArrayList();
	        List<EntityCondition> condnList1=new ArrayList();
	        List<EntityCondition> condnList2=new ArrayList();
	        Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	           int day=d1.getDate();
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
              condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
           		condnList.add(dateOr);
           		
            Timestamp pds=UtilDateTime.getDayStart(d1,-1);
            Timestamp pde=UtilDateTime.getDayEnd(pds);
            
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pds));
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pde));
             EntityConditionList<EntityCondition> dateOr1=EntityCondition.makeCondition(datePreCondiList,EntityOperator.AND);
           condnList1.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList1.add(dateOr1);
        	
            Timestamp pms=UtilDateTime.getMonthStart(d1,day-1, -1);
            Timestamp pmd=UtilDateTime.getDayEnd(pms);  
            
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pms));
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pmd));
             EntityConditionList<EntityCondition> dateOr2=EntityCondition.makeCondition(dateMonthCondiList,EntityOperator.AND);
           condnList2.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList2.add(dateOr2); 
	       
	        try{
	        	Map sum=new HashMap();
	        	Map sum1=new HashMap();
	        	Map sum2=new HashMap();
	        	 BigDecimal t=BigDecimal.ZERO;
	        	 BigDecimal t1=BigDecimal.ZERO;
	        	 BigDecimal t2=BigDecimal.ZERO;
	        	 for(int y=0;y<(end-start)/interval;y++)
			        {
	        		 sum.put(y,0.0);
	        		 sum1.put(y,0.0);
	        		 sum2.put(y,0.0);
			        }
	        	   
	        	   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
	        	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						 int k=0;
						GenericValue genVal = (GenericValue) itr.next();
						String orderId = (String) genVal.get("orderId");
						Timestamp date=genVal.getTimestamp("statusDatetime");
						int h=date.getHours();
						for(int i=start;i<=24;i=i+interval)
						{
							if(h>=i && h<i+interval)
							{
							GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
							if(detail.getString("orderTypeId").equals("SALES_ORDER"))
							{
							 BigDecimal b=detail.getBigDecimal("grandTotal");
						       
						     t=b.add(t);
							     sum.put(k,t);
							}
							}
							k=k+1;
						}
					}
					 List <GenericValue>salespre = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList1,EntityOperator.AND), null, null, null, true);
		        	   Iterator itr1 = salespre.iterator();
						while(itr1.hasNext()){
							 int k=0;
							GenericValue genVal = (GenericValue) itr1.next();
							String orderId = (String) genVal.get("orderId");
							Timestamp date=genVal.getTimestamp("statusDatetime");
							int h=date.getHours();
							for(int i=start;i<=24;i=i+interval)
							{
								if(h>=i && h<=i+interval)
								{
								GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
								if(detail.getString("orderTypeId").equals("SALES_ORDER"))
								{
								       BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t1=b.add(t1);
								     sum1.put(k,t1);
								}
								}
								k=k+1;
							}
						}
						List <GenericValue>salespremonth = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList2,EntityOperator.AND), null, null, null, true);
			        	   Iterator itr2 = salespremonth.iterator();
							while(itr2.hasNext()){
								 int k=0;
								GenericValue genVal = (GenericValue) itr2.next();
								String orderId = (String) genVal.get("orderId");
								Timestamp date=genVal.getTimestamp("statusDatetime");
								int h=date.getHours();
								for(int i=start;i<=24;i=i+interval)
								{
									if(h>=i && h<=i+interval)
									{
									GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
									if(detail.getString("orderTypeId").equals("SALES_ORDER"))
									{
									 BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t2=b.add(t2);
									     sum2.put(k,t2);
									}
									}
									k=k+1;
								}
							}
			
					 // create the dataset...
			        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	//System.out.println("the sum\n\n\n"+sum.get(y));
			        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
			        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum.get(y), series1,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum1.get(y), series2,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum2.get(y), series3,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			      
	        final JFreeChart chart = ChartFactory.createLineChart(
	                "Daily Sales Report",       // chart title
	                "Hour",                    // domain axis label
	                "Amount",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  public static String viewSalesWeekly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		  Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
		  int day=startday.getDate();
		  
		  Timestamp lastweek=UtilDateTime.getDayStart(startday,-7);
		  Timestamp d2=UtilDateTime.getWeekStart(lastweek);
		  Timestamp monthstart=UtilDateTime.getMonthStart(d1,day-1, -1);
		  Timestamp d3=UtilDateTime.getWeekStart(monthstart);
		  //List<EntityCondition> condnList=new ArrayList();
		  final String series1 = "This Week";
	        final String series2 = "Last Week";
	        final String series3 = "Last Month Same Week";
		Map sum=new HashMap();
      	Map sum1=new HashMap();
      	Map sum2=new HashMap();
      	for(int i=0;i<7;i++)
      	{
      		sum.put(i,0.0);
      		sum1.put(i,0.0);
      		sum2.put(i,0.0);
      	}
		  int k=0;
		  try{
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d1,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
				
				
					}
		  int n=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d2,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d3,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int y=0;y<7;y++)
	        {
	        	//System.out.println("the sum\n\n\n"+sum.get(y));
	        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
	        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Weekly Sales Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
				
		  }catch(Exception e){}
		  
		  
		  String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
          String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
          File file = new File(filePath);
          // Destination directory
          File dir = new File(imageServerPath);

          File directory = new File(imageServerPath);
          File []listFiles = directory.listFiles();
            
                            // Move file to new directory
          boolean success = file.renameTo(new File(dir, file.getName()));
          file.delete();
          return imagelocation;
      }
	  public static String viewSalesMonthly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int month=startday.getMonth();
		  Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		  int end=endDay.getDate();
		  Timestamp startPre=UtilDateTime.getMonthStart(startday,0,-1);
		  Timestamp endDayPre=UtilDateTime.getMonthEnd(startPre, TimeZone.getDefault(), Locale.getDefault());
		 int endPre=endDayPre.getDate();
		 Timestamp startYear=UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), 0, month, -1);
		 Timestamp endDayYear=UtilDateTime.getMonthEnd(startYear, TimeZone.getDefault(), Locale.getDefault());
		 int endYear=endDayYear.getDate();
		  final String series1 = "This Month";
	        final String series2 = "Last Month";
	        final String series3 = "Last Year Same Month";
		Map sum=new HashMap();
    	Map sum1=new HashMap();
    	Map sum2=new HashMap();
    	for(int i=0;i<end;i++)
    	{
    		sum.put(i,0.0);
    		
    	}
    	for(int i=0;i<endPre;i++)
    	{
    		sum1.put(i,0.0);
    		
    	}
    	for(int i=0;i<endYear;i++)
    	{
    		sum2.put(i,0.0);
    		
    	}
		 
		  try{
			  int k=0;
		  for(int i=0;i<end;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startday,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
     	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
			}
		  int n=0;
		  for(int i=0;i<endPre;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startPre,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<endYear;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startYear,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<end;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<endPre;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<endYear;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Monthly Sales Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
				
		  

		  
      }catch(Exception e){}
      String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
      String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
      File file = new File(filePath);
      // Destination directory
      File dir = new File(imageServerPath);

      File directory = new File(imageServerPath);
      File []listFiles = directory.listFiles();
        
                        // Move file to new directory
      boolean success = file.renameTo(new File(dir, file.getName()));
      file.delete();
      return imagelocation;
      }  
	  public static String viewSalesQuarterly(GenericDelegator delegator, HttpSession session)
      {
		  //List<String> monName=UtilDateTime.getMonthNames(Locale.getDefault());
		  String imagelocation=null;
		  int month1;
		  int month2;
		  int month3;
		  Timestamp firstMonthStart = null;
		  Timestamp secondMonthStart=null;
		  Timestamp thirdMonthStart=null;
		  final String series1 = "This Quater";
	      final String series2 = "Last Year same Quarter";
	     
	      Map monName=new HashMap();
	     
		  
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int quarter=0;
		  int m=startday.getMonth();
		  if(m/3==0)
		  {
			  monName.put(0,"jan");
			  monName.put(1,"Feb");
			  monName.put(2,"Mar");
			  quarter=1;
			  
		  }
		  if(m/3==1){
			  quarter=2;
			  monName.put(0,"April");
			  monName.put(1,"May");
			  monName.put(2,"June");
		  }
		  if(m/3==2)
		  {
			  quarter=3;
			  monName.put(0,"July");
			  monName.put(1,"Aug");
			  monName.put(2,"Sep");
		  }
		  if(m/3==3)
		  {
			  monName.put(0,"Oct");
			  monName.put(1,"Nov");
			  monName.put(2,"Dec");
			  quarter=4;
		  }  
	  
		  if((m+1)%3==0)
		  {
			  
			  thirdMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(thirdMonthStart, 0, -1);
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, -1);
			
		  }
		  if((m+1)%3==1)
		  {
			  
			  firstMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(firstMonthStart,0,1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, 1);
			  
		  }
		  if((m+1)%3==2)
		  {
			  secondMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,-1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,1);
			 
				
		  }
		 
		  
		  Map conMap=new HashMap();
		  Map conMap1=new HashMap();
		  Map amountMap=new HashMap();
		  Map amountMap1=new HashMap();
		  conMap.put(0, firstMonthStart);
		  conMap.put(1, secondMonthStart);
		  conMap.put(2, thirdMonthStart);
		  conMap1.put(0, UtilDateTime.getMonthStart(firstMonthStart,0,-12));
		  conMap1.put(1, UtilDateTime.getMonthStart(secondMonthStart,0,-12));
		  conMap1.put(2, UtilDateTime.getMonthStart(thirdMonthStart,0,-12));
		  try
		  {
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		  
		   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
    	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("SALES_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					    
					 }
					}
			amountMap.put(i,amount);
		  }
			
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap1.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap1.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		 List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
  	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("SALES_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					}
					}
			amountMap1.put(i,amount);
		  }
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap.get(y),series1,(String)monName.get(y));
	        	
	        }
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap1.get(y),series2,(String)monName.get(y));
	        	
	        }
	       
	      
final JFreeChart chart = ChartFactory.createLineChart(
        "Quarterly Sales Report",       // chart title
        "Month",                    // domain axis label
        "Amount",                   // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        true,                      // tooltips
        false                      // urls
    );
chart.setBackgroundPaint(Color.pink);

final CategoryPlot plot = (CategoryPlot) chart.getPlot();

plot.setRangeGridlinePaint(Color.BLACK);
plot.setBackgroundPaint(Color.white);
plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
// customise the range axis...
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
rangeAxis.setAutoRangeIncludesZero(true);
final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
String var =  ServletUtilities.getTempFilePrefix();
//System.out.println("\n\n ######## the temp File prefix="+var);
imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
 }catch(Exception e){}
 String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
 String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
 File file = new File(filePath);
 // Destination directory
 File dir = new File(imageServerPath);

 File directory = new File(imageServerPath);
 File []listFiles = directory.listFiles();
   
                   // Move file to new directory
 boolean success = file.renameTo(new File(dir, file.getName()));
 file.delete();
 return imagelocation;
      }
	  
	  public static String viewPurchaseDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null; 
	       
		  int start=9;
   	   int interval=3;
   	   int end=24;
	        final String series1 = "Today";
	        final String series2 = "YesterDay";
	        final String series3 = "Last Month Same Day";
	        List<EntityCondition> dateCondiList=new ArrayList();
	        List<EntityCondition> datePreCondiList=new ArrayList();
	        List<EntityCondition> dateMonthCondiList=new ArrayList();
	        List<EntityCondition> condnList=new ArrayList();
	        List<EntityCondition> condnList1=new ArrayList();
	        List<EntityCondition> condnList2=new ArrayList();
	        Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	           int day=d1.getDate();
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
              condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
           		condnList.add(dateOr);
           		
            Timestamp pds=UtilDateTime.getDayStart(d1,-1);
            Timestamp pde=UtilDateTime.getDayEnd(pds);
            
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pds));
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pde));
             EntityConditionList<EntityCondition> dateOr1=EntityCondition.makeCondition(datePreCondiList,EntityOperator.AND);
           condnList1.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList1.add(dateOr1);
        	
            Timestamp pms=UtilDateTime.getMonthStart(d1,day-1, -1);
            Timestamp pmd=UtilDateTime.getDayEnd(pms);  
            
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pms));
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pmd));
             EntityConditionList<EntityCondition> dateOr2=EntityCondition.makeCondition(dateMonthCondiList,EntityOperator.AND);
           condnList2.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList2.add(dateOr2); 
	       
	        try{
	        	Map sum=new HashMap();
	        	Map sum1=new HashMap();
	        	Map sum2=new HashMap();
	        	 BigDecimal t=BigDecimal.ZERO;
	        	 BigDecimal t1=BigDecimal.ZERO;
	        	 BigDecimal t2=BigDecimal.ZERO;
	        	 for(int y=0;y<(end-start)/interval;y++)
			        {
	        		 sum.put(y,0.0);
	        		 sum1.put(y,0.0);
	        		 sum2.put(y,0.0);
			        }
	        	   
	        	   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
	        	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						 int k=0;
						GenericValue genVal = (GenericValue) itr.next();
						String orderId = (String) genVal.get("orderId");
						Timestamp date=genVal.getTimestamp("statusDatetime");
						int h=date.getHours();
						for(int i=start;i<=24;i=i+interval)
						{
							if(h>=i && h<i+interval)
							{
							GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
							if(detail.getString("orderTypeId").equals("SALES_ORDER"))
							{
							 BigDecimal b=detail.getBigDecimal("grandTotal");
						       
						     t=b.add(t);
							     sum.put(k,t);
							}
							}
							k=k+1;
						}
					}
					 List <GenericValue>salespre = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList1,EntityOperator.AND), null, null, null, true);
		        	   Iterator itr1 = salespre.iterator();
						while(itr1.hasNext()){
							 int k=0;
							GenericValue genVal = (GenericValue) itr1.next();
							String orderId = (String) genVal.get("orderId");
							Timestamp date=genVal.getTimestamp("statusDatetime");
							int h=date.getHours();
							for(int i=start;i<=24;i=i+interval)
							{
								if(h>=i && h<=i+interval)
								{
								GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
								if(detail.getString("orderTypeId").equals("SALES_ORDER"))
								{
								       BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t1=b.add(t1);
								     sum1.put(k,t1);
								}
								}
								k=k+1;
							}
						}
						List <GenericValue>salespremonth = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList2,EntityOperator.AND), null, null, null, true);
			        	   Iterator itr2 = salespremonth.iterator();
							while(itr2.hasNext()){
								 int k=0;
								GenericValue genVal = (GenericValue) itr2.next();
								String orderId = (String) genVal.get("orderId");
								Timestamp date=genVal.getTimestamp("statusDatetime");
								int h=date.getHours();
								for(int i=start;i<=24;i=i+interval)
								{
									if(h>=i && h<=i+interval)
									{
									GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
									if(detail.getString("orderTypeId").equals("PURCHASE_ORDER"))
									{
									 BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t2=b.add(t2);
									     sum2.put(k,t2);
									}
									}
									k=k+1;
								}
							}
			
					 // create the dataset...
			        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	//System.out.println("the sum\n\n\n"+sum.get(y));
			        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
			        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum.get(y), series1,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum1.get(y), series2,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum2.get(y), series3,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			      
	        final JFreeChart chart = ChartFactory.createLineChart(
	                "Daily Purchase Report",       // chart title
	                "Hour",                    // domain axis label
	                "Amount",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  public static String viewPurchaseWeekly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		  Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
		  int day=startday.getDate();
		  
		  Timestamp lastweek=UtilDateTime.getDayStart(startday,-7);
		  Timestamp d2=UtilDateTime.getWeekStart(lastweek);
		  Timestamp monthstart=UtilDateTime.getMonthStart(d1,day-1, -1);
		  Timestamp d3=UtilDateTime.getWeekStart(monthstart);
		  //List<EntityCondition> condnList=new ArrayList();
		  final String series1 = "This Week";
	        final String series2 = "Last Week";
	        final String series3 = "Last Month Same Week";
		Map sum=new HashMap();
      	Map sum1=new HashMap();
      	Map sum2=new HashMap();
      	for(int i=0;i<7;i++)
      	{
      		sum.put(i,0.0);
      		sum1.put(i,0.0);
      		sum2.put(i,0.0);
      	}
		  int k=0;
		  try{
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d1,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
				
				
					}
		  int n=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d2,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d3,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("PURCHASE_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int y=0;y<7;y++)
	        {
	        	//System.out.println("the sum\n\n\n"+sum.get(y));
	        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
	        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Weekly Purchase Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
				
		  }catch(Exception e){}
		  
		  
		  String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
          String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
          File file = new File(filePath);
          // Destination directory
          File dir = new File(imageServerPath);

          File directory = new File(imageServerPath);
          File []listFiles = directory.listFiles();
            
                            // Move file to new directory
          boolean success = file.renameTo(new File(dir, file.getName()));
          file.delete();
          return imagelocation;
      }
	  public static String viewPurchaseMonthly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int month=startday.getMonth();
		  Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		  int end=endDay.getDate();
		  Timestamp startPre=UtilDateTime.getMonthStart(startday,0,-1);
		  Timestamp endDayPre=UtilDateTime.getMonthEnd(startPre, TimeZone.getDefault(), Locale.getDefault());
		 int endPre=endDayPre.getDate();
		 Timestamp startYear=UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), 0, month, -1);
		 Timestamp endDayYear=UtilDateTime.getMonthEnd(startYear, TimeZone.getDefault(), Locale.getDefault());
		 int endYear=endDayYear.getDate();
		  final String series1 = "This Month";
	        final String series2 = "Last Month";
	        final String series3 = "Last Year Same Month";
		Map sum=new HashMap();
    	Map sum1=new HashMap();
    	Map sum2=new HashMap();
    	for(int i=0;i<end;i++)
    	{
    		sum.put(i,0.0);
    		
    	}
    	for(int i=0;i<endPre;i++)
    	{
    		sum1.put(i,0.0);
    		
    	}
    	for(int i=0;i<endYear;i++)
    	{
    		sum2.put(i,0.0);
    		
    	}
		 
		  try{
			  int k=0;
		  for(int i=0;i<end;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startday,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
     	  
			Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
			}
		  int n=0;
		  for(int i=0;i<endPre;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startPre,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<endYear;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startYear,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("PURCHASE_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<end;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<endPre;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<endYear;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Monthly Purchase Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
				
		  

		  
      }catch(Exception e){}
      String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
      String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
      File file = new File(filePath);
      // Destination directory
      File dir = new File(imageServerPath);

      File directory = new File(imageServerPath);
      File []listFiles = directory.listFiles();
        
                        // Move file to new directory
      boolean success = file.renameTo(new File(dir, file.getName()));
      file.delete();
      return imagelocation;
      }  
	  public static String viewPurchaseQuarterly(GenericDelegator delegator, HttpSession session)
      {
		  //List<String> monName=UtilDateTime.getMonthNames(Locale.getDefault());
		  String imagelocation=null;
		  int month1;
		  int month2;
		  int month3;
		  Timestamp firstMonthStart = null;
		  Timestamp secondMonthStart=null;
		  Timestamp thirdMonthStart=null;
		  final String series1 = "This Quater";
	      final String series2 = "Last Year same Quarter";
	     
	      Map monName=new HashMap();
	     
		  
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int quarter=0;
		  int m=startday.getMonth();
		  if(m/3==0)
		  {
			  monName.put(0,"jan");
			  monName.put(1,"Feb");
			  monName.put(2,"Mar");
			  quarter=1;
			  
		  }
		  if(m/3==1){
			  quarter=2;
			  monName.put(0,"April");
			  monName.put(1,"May");
			  monName.put(2,"June");
		  }
		  if(m/3==2)
		  {
			  quarter=3;
			  monName.put(0,"July");
			  monName.put(1,"Aug");
			  monName.put(2,"Sep");
		  }
		  if(m/3==3)
		  {
			  monName.put(0,"Oct");
			  monName.put(1,"Nov");
			  monName.put(2,"Dec");
			  quarter=4;
		  }  
	  
		  if((m+1)%3==0)
		  {
			  
			  thirdMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(thirdMonthStart, 0, -1);
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, -1);
			
		  }
		  if((m+1)%3==1)
		  {
			  
			  firstMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(firstMonthStart,0,1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, 1);
			  
		  }
		  if((m+1)%3==2)
		  {
			  secondMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,-1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,1);
			 
				
		  }
		 
		  
		  Map conMap=new HashMap();
		  Map conMap1=new HashMap();
		  Map amountMap=new HashMap();
		  Map amountMap1=new HashMap();
		  conMap.put(0, firstMonthStart);
		  conMap.put(1, secondMonthStart);
		  conMap.put(2, thirdMonthStart);
		  conMap1.put(0, UtilDateTime.getMonthStart(firstMonthStart,0,-12));
		  conMap1.put(1, UtilDateTime.getMonthStart(secondMonthStart,0,-12));
		  conMap1.put(2, UtilDateTime.getMonthStart(thirdMonthStart,0,-12));
		  try
		  {
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		  
		   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
    	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("PURCHASE_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					    
					 }
					}
			amountMap.put(i,amount);
		  }
			
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap1.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap1.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		 List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
  	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("SALES_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					}
					}
			amountMap1.put(i,amount);
		  }
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap.get(y),series1,(String)monName.get(y));
	        	
	        }
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap1.get(y),series2,(String)monName.get(y));
	        	
	        }
	       
	      
final JFreeChart chart = ChartFactory.createLineChart(
        "Quarterly Purchase Report",       // chart title
        "",                    // domain axis label
        "",                   // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        true,                      // tooltips
        false                      // urls
    );
chart.setBackgroundPaint(Color.pink);

final CategoryPlot plot = (CategoryPlot) chart.getPlot();

plot.setRangeGridlinePaint(Color.BLACK);
plot.setBackgroundPaint(Color.white);
plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
plot.getRenderer().setSeriesPaint(0, Color.CYAN);
plot.getRenderer().setSeriesPaint(1, Color.GREEN);
// customise the range axis...
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
rangeAxis.setAutoRangeIncludesZero(true);
final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
String var =  ServletUtilities.getTempFilePrefix();
//System.out.println("\n\n ######## the temp File prefix="+var);
imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
 }catch(Exception e){}
 String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
 String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
 File file = new File(filePath);
 // Destination directory
 File dir = new File(imageServerPath);

 File directory = new File(imageServerPath);
 File []listFiles = directory.listFiles();
   
                   // Move file to new directory
 boolean success = file.renameTo(new File(dir, file.getName()));
 file.delete();
 return imagelocation;
      }
	  
	  
	  
	  
	  public static String viewCreditDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null; 
	       
		  int start=9;
   	   int interval=3;
   	   int end=24;
	        final String series1 = "Today";
	        final String series2 = "YesterDay";
	        final String series3 = "Last Month Same Day";
	        List<EntityCondition> dateCondiList=new ArrayList();
	        List<EntityCondition> datePreCondiList=new ArrayList();
	        List<EntityCondition> dateMonthCondiList=new ArrayList();
	        List<EntityCondition> condnList=new ArrayList();
	        List<EntityCondition> condnList1=new ArrayList();
	        List<EntityCondition> condnList2=new ArrayList();
	        Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	           int day=d1.getDate();
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
	           dateCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,d ));
                EntityConditionList<EntityCondition> dateOr=EntityCondition.makeCondition(dateCondiList,EntityOperator.AND);
              condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
           		condnList.add(dateOr);
           		
            Timestamp pds=UtilDateTime.getDayStart(d1,-1);
            Timestamp pde=UtilDateTime.getDayEnd(pds);
            
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pds));
            datePreCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pde));
             EntityConditionList<EntityCondition> dateOr1=EntityCondition.makeCondition(datePreCondiList,EntityOperator.AND);
           condnList1.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList1.add(dateOr1);
        	
            Timestamp pms=UtilDateTime.getMonthStart(d1,day-1, -1);
            Timestamp pmd=UtilDateTime.getDayEnd(pms);  
            
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.GREATER_THAN_EQUAL_TO,pms));
            dateMonthCondiList.add(EntityCondition.makeCondition("statusDatetime", EntityOperator.LESS_THAN_EQUAL_TO,pmd));
             EntityConditionList<EntityCondition> dateOr2=EntityCondition.makeCondition(dateMonthCondiList,EntityOperator.AND);
           condnList2.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
        	condnList2.add(dateOr2); 
	       
	        try{
	        	Map sum=new HashMap();
	        	Map sum1=new HashMap();
	        	Map sum2=new HashMap();
	        	 BigDecimal t=BigDecimal.ZERO;
	        	 BigDecimal t1=BigDecimal.ZERO;
	        	 BigDecimal t2=BigDecimal.ZERO;
	        	 for(int y=0;y<(end-start)/interval;y++)
			        {
	        		 sum.put(y,0.0);
	        		 sum1.put(y,0.0);
	        		 sum2.put(y,0.0);
			        }
	        	   
	        	   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
	        	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						 int k=0;
						GenericValue genVal = (GenericValue) itr.next();
						String orderId = (String) genVal.get("orderId");
						Timestamp date=genVal.getTimestamp("statusDatetime");
						int h=date.getHours();
						for(int i=start;i<=24;i=i+interval)
						{
							if(h>=i && h<i+interval)
							{
							GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
							if(detail.getString("orderTypeId").equals("SALES_ORDER"))
							{
							 BigDecimal b=detail.getBigDecimal("grandTotal");
						       
						     t=b.add(t);
							     sum.put(k,t);
							}
							}
							k=k+1;
						}
					}
					 List <GenericValue>salespre = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList1,EntityOperator.AND), null, null, null, true);
		        	   Iterator itr1 = salespre.iterator();
						while(itr1.hasNext()){
							 int k=0;
							GenericValue genVal = (GenericValue) itr1.next();
							String orderId = (String) genVal.get("orderId");
							Timestamp date=genVal.getTimestamp("statusDatetime");
							int h=date.getHours();
							for(int i=start;i<=24;i=i+interval)
							{
								if(h>=i && h<=i+interval)
								{
								GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
								if(detail.getString("orderTypeId").equals("SALES_ORDER"))
								{
								       BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t1=b.add(t1);
								     sum1.put(k,t1);
								}
								}
								k=k+1;
							}
						}
						List <GenericValue>salespremonth = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList2,EntityOperator.AND), null, null, null, true);
			        	   Iterator itr2 = salespremonth.iterator();
							while(itr2.hasNext()){
								 int k=0;
								GenericValue genVal = (GenericValue) itr2.next();
								String orderId = (String) genVal.get("orderId");
								Timestamp date=genVal.getTimestamp("statusDatetime");
								int h=date.getHours();
								for(int i=start;i<=24;i=i+interval)
								{
									if(h>=i && h<=i+interval)
									{
									GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
									if(detail.getString("orderTypeId").equals("SALES_ORDER"))
									{
									 BigDecimal b=detail.getBigDecimal("grandTotal");
								       
								     t2=b.add(t2);
									     sum2.put(k,t2);
									}
									}
									k=k+1;
								}
							}
			
					 // create the dataset...
			        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	//System.out.println("the sum\n\n\n"+sum.get(y));
			        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
			        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum.get(y), series1,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum1.get(y), series2,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum2.get(y), series3,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			      
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Credit Realized",       // chart title
	                "Hour",                    // domain axis label
	                "Amount",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  public static String viewCreditWeekly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		  Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
		  int day=startday.getDate();
		  
		  Timestamp lastweek=UtilDateTime.getDayStart(startday,-7);
		  Timestamp d2=UtilDateTime.getWeekStart(lastweek);
		  Timestamp monthstart=UtilDateTime.getMonthStart(d1,day-1, -1);
		  Timestamp d3=UtilDateTime.getWeekStart(monthstart);
		  //List<EntityCondition> condnList=new ArrayList();
		  final String series1 = "This Week";
	        final String series2 = "Last Week";
	        final String series3 = "Last Month Same Week";
		Map sum=new HashMap();
      	Map sum1=new HashMap();
      	Map sum2=new HashMap();
      	for(int i=0;i<7;i++)
      	{
      		sum.put(i,0.0);
      		sum1.put(i,0.0);
      		sum2.put(i,0.0);
      	}
		  int k=0;
		  try{
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d1,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
				
				
					}
		  int n=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d2,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d3,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int y=0;y<7;y++)
	        {
	        	//System.out.println("the sum\n\n\n"+sum.get(y));
	        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
	        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Weekly Credit Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
				
		  }catch(Exception e){}
		  
		  
		  String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
          String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
          File file = new File(filePath);
          // Destination directory
          File dir = new File(imageServerPath);

          File directory = new File(imageServerPath);
          File []listFiles = directory.listFiles();
            
                            // Move file to new directory
          boolean success = file.renameTo(new File(dir, file.getName()));
          file.delete();
          return imagelocation;
      }
	  public static String viewCreditMonthly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int month=startday.getMonth();
		  Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		  int end=endDay.getDate();
		  Timestamp startPre=UtilDateTime.getMonthStart(startday,0,-1);
		  Timestamp endDayPre=UtilDateTime.getMonthEnd(startPre, TimeZone.getDefault(), Locale.getDefault());
		 int endPre=endDayPre.getDate();
		 Timestamp startYear=UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), 0, month, -1);
		 Timestamp endDayYear=UtilDateTime.getMonthEnd(startYear, TimeZone.getDefault(), Locale.getDefault());
		 int endYear=endDayYear.getDate();
		  final String series1 = "This Month";
	        final String series2 = "Last Month";
	        final String series3 = "Last Year Same Month";
		Map sum=new HashMap();
    	Map sum1=new HashMap();
    	Map sum2=new HashMap();
    	for(int i=0;i<end;i++)
    	{
    		sum.put(i,0.0);
    		
    	}
    	for(int i=0;i<endPre;i++)
    	{
    		sum1.put(i,0.0);
    		
    	}
    	for(int i=0;i<endYear;i++)
    	{
    		sum2.put(i,0.0);
    		
    	}
		 
		  try{
			  int k=0;
		  for(int i=0;i<end;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startday,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
     	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum.put(k,t);
				k=k+1;
			}
		  int n=0;
		  for(int i=0;i<endPre;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startPre,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<endYear;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startYear,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
			List <GenericValue>sales = delegator.findList("OrderStatus",EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					String orderId = (String) genVal.get("orderId");
					GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
						if(detail.getString("orderTypeId").equals("SALES_ORDER"))
						{
						 BigDecimal b=detail.getBigDecimal("grandTotal");
					       
					     t=b.add(t);
						     
						}
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<end;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<endPre;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<endYear;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	      
  final JFreeChart chart = ChartFactory.createLineChart(
          "Monthly Credit Report",       // chart title
          "Day",                    // domain axis label
          "Amount",                   // range axis label
          dataset,                   // data
          PlotOrientation.VERTICAL,  // orientation
          true,                      // include legend
          true,                      // tooltips
          false                      // urls
      );
  chart.setBackgroundPaint(Color.pink);

  final CategoryPlot plot = (CategoryPlot) chart.getPlot();
  
  plot.setRangeGridlinePaint(Color.BLACK);
  plot.setBackgroundPaint(Color.white);
  plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
  plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
  // customise the range axis...
  final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
  rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
  rangeAxis.setAutoRangeIncludesZero(true);
  final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
  String var =  ServletUtilities.getTempFilePrefix();
  //System.out.println("\n\n ######## the temp File prefix="+var);
  imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
				
		  

		  
      }catch(Exception e){}
      String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
      String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
      File file = new File(filePath);
      // Destination directory
      File dir = new File(imageServerPath);

      File directory = new File(imageServerPath);
      File []listFiles = directory.listFiles();
        
                        // Move file to new directory
      boolean success = file.renameTo(new File(dir, file.getName()));
      file.delete();
      return imagelocation;
      }  
	  public static String viewCreditQuarterly(GenericDelegator delegator, HttpSession session)
      {
		  //List<String> monName=UtilDateTime.getMonthNames(Locale.getDefault());
		  String imagelocation=null;
		  int month1;
		  int month2;
		  int month3;
		  Timestamp firstMonthStart = null;
		  Timestamp secondMonthStart=null;
		  Timestamp thirdMonthStart=null;
		  final String series1 = "This Quater";
	      final String series2 = "Last Year same Quarter";
	     
	      Map monName=new HashMap();
	     
		  
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int quarter=0;
		  int m=startday.getMonth();
		  if(m/3==0)
		  {
			  monName.put(0,"jan");
			  monName.put(1,"Feb");
			  monName.put(2,"Mar");
			  quarter=1;
			  
		  }
		  if(m/3==1){
			  quarter=2;
			  monName.put(0,"April");
			  monName.put(1,"May");
			  monName.put(2,"June");
		  }
		  if(m/3==2)
		  {
			  quarter=3;
			  monName.put(0,"July");
			  monName.put(1,"Aug");
			  monName.put(2,"Sep");
		  }
		  if(m/3==3)
		  {
			  monName.put(0,"Oct");
			  monName.put(1,"Nov");
			  monName.put(2,"Dec");
			  quarter=4;
		  }  
	  
		  if((m+1)%3==0)
		  {
			  
			  thirdMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(thirdMonthStart, 0, -1);
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, -1);
			
		  }
		  if((m+1)%3==1)
		  {
			  
			  firstMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(firstMonthStart,0,1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, 1);
			  
		  }
		  if((m+1)%3==2)
		  {
			  secondMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,-1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,1);
			 
				
		  }
		 
		  
		  Map conMap=new HashMap();
		  Map conMap1=new HashMap();
		  Map amountMap=new HashMap();
		  Map amountMap1=new HashMap();
		  conMap.put(0, firstMonthStart);
		  conMap.put(1, secondMonthStart);
		  conMap.put(2, thirdMonthStart);
		  conMap1.put(0, UtilDateTime.getMonthStart(firstMonthStart,0,-12));
		  conMap1.put(1, UtilDateTime.getMonthStart(secondMonthStart,0,-12));
		  conMap1.put(2, UtilDateTime.getMonthStart(thirdMonthStart,0,-12));
		  try
		  {
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		  
		   List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
    	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("SALES_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					    
					 }
					}
			amountMap.put(i,amount);
		  }
			
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.GREATER_THAN_EQUAL_TO,conMap1.get(i))); 
		condnList.add(EntityCondition.makeCondition("statusDatetime",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap1.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		condnList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ITEM_COMPLETED"));
		
		 List <GenericValue>sales = delegator.findList("OrderStatus", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
  	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				String orderId = (String) genVal.get("orderId");
				GenericValue detail=delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId));
					if(detail.getString("orderTypeId").equals("SALES_ORDER"))
					{
					 BigDecimal b=detail.getBigDecimal("grandTotal");
				       
				     amount=b.add(amount);
					}
					}
			amountMap1.put(i,amount);
		  }
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap.get(y),series1,(String)monName.get(y));
	        	
	        }
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap1.get(y),series2,(String)monName.get(y));
	        	
	        }
	       
	      
final JFreeChart chart = ChartFactory.createLineChart(
        "Quarterly Credit Report",       // chart title
        "Month",                    // domain axis label
        "Amount",                   // range axis label
        dataset,                   // data
        PlotOrientation.VERTICAL,  // orientation
        true,                      // include legend
        true,                      // tooltips
        false                      // urls
    );
chart.setBackgroundPaint(Color.pink);

final CategoryPlot plot = (CategoryPlot) chart.getPlot();

plot.setRangeGridlinePaint(Color.BLACK);
plot.setBackgroundPaint(Color.white);
plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
// customise the range axis...
final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
rangeAxis.setAutoRangeIncludesZero(true);
final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
String var =  ServletUtilities.getTempFilePrefix();
//System.out.println("\n\n ######## the temp File prefix="+var);
imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
 }catch(Exception e){}
 String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
 String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
 File file = new File(filePath);
 // Destination directory
 File dir = new File(imageServerPath);

 File directory = new File(imageServerPath);
 File []listFiles = directory.listFiles();
   
                   // Move file to new directory
 boolean success = file.renameTo(new File(dir, file.getName()));
 file.delete();
 return imagelocation;
      }
	  public static String viewCustomerDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		
		  Timestamp endDay=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
		  //int end=endDay.getDate();
		
		  final String series1 = "New Customer";
	        final String series2 = "Existing Customer";
	       
		
		  try{
			 
		 double newCustPer = 0;
		 double existCustPer=0;
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			
			  condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,startday)); 
				condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,endDay)); 
				
				long s = delegator.findCountByCondition("Person",EntityCondition.makeCondition(condnList,EntityOperator.AND), null,null);
				long s1 = delegator.findCountByCondition("Person",EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN,startday), null,null);
				long total = delegator.findCountByCondition("Person",null, null,null);
				  if(total!=0)
	                 {
	                	 if(s!=0)
	                  newCustPer=(100*s)/total;
	                	 if(s1!=0)
	                		 existCustPer=(100*s1)/total;
	                 }
				
				DefaultPieDataset dataset = new DefaultPieDataset();
				dataset.setValue("New Customer",newCustPer);
				dataset.setValue("Existing Customer",existCustPer);
        
          
          // set up the chart
    JFreeChart chart = ChartFactory.createPieChart3D
    ("Customer Daily Report", dataset, true,true,true);
     //chart.setBackgroundPaint(Color.magenta);
     chart.setBorderVisible(false);
     //chart.setPadding(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
     chart.setBackgroundPaint(Color.pink);
     // get a reference to the plot for further customisation...
     PiePlot3D plot = (PiePlot3D) chart.getPlot();
     plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
     plot.setNoDataMessage("No data available");
     plot.setCircular(true);
   
    
     plot.setBackgroundPaint(Color.white);
     plot.setLabelGap(0.02);
    plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
             "{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
    ));
     // save as a png and return the file name
     String var =  ServletUtilities.getTempFilePrefix();
     //System.out.println("\n\n ######## the temp File prefix="+var);
     imagelocation = ServletUtilities.saveChartAsPNG(chart,400,300, session);
    }
    catch(Exception e)
    {
          e.printStackTrace();
    }
    String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
    String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
    File file = new File(filePath);
    // Destination directory
    File dir = new File(imageServerPath);

    File directory = new File(imageServerPath);
    File []listFiles = directory.listFiles();
      
                      // Move file to new directory
    boolean success = file.renameTo(new File(dir, file.getName()));
    file.delete();
    return imagelocation;
		  } 
	    
	  
	  
	  
	  public static String viewCustomerWeekly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		  Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
		  int day=startday.getDate();
		  final String series2= "Existing Customers";
	        final String series1 = "New Customers";
	        
		Map sum=new HashMap();
      	Map sum1=new HashMap();
      	
      	for(int i=0;i<7;i++)
      	{
      		sum.put(i,0);
      		sum1.put(i,0);
      		
      	}
      	int k=1;
		  try{
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList1=new ArrayList();
			  List<EntityCondition> condnList=new ArrayList();
			 long t=0;
			  Timestamp tem=UtilDateTime.getDayStart(d1,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			
			Long s = delegator.findCountByCondition("Person",EntityCondition.makeCondition(condnList,EntityOperator.AND), null,null);
			Long s1 = delegator.findCountByCondition("Person",EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN,tem), null,null);
			sum.put(k,s);
			sum1.put(k,s1);
				k=k+1;
			}
		 
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int y=0;y<7;y++)
	        {
	        	//System.out.println("the sum\n\n\n"+sum.get(y));
	        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
	        
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        final JFreeChart chart = ChartFactory.createAreaChart(
			                "Customer Weekly Report",             // chart title
			                "",               // domain axis label
			                "",                  // range axis label
			                dataset,                  // data
			                PlotOrientation.VERTICAL, // orientation
			                true,                     // include legend
			                true,                     // tooltips
			                false                     // urls
			            );
			     
			            // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

			            // set the background color for the chart...
//			            final StandardLegend legend = (StandardLegend) chart.getLegend();
			      //      legend.setAnchor(StandardLegend.SOUTH);

			            chart.setBackgroundPaint(Color.pink);
			        
			            final CategoryPlot plot = chart.getCategoryPlot();
			            plot.setForegroundAlpha(0.5f);
			            
			      //      plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
			            plot.setBackgroundPaint(Color.white);
			          // plot.setDomainGridlinesVisible(true);
			           // plot.setDomainGridlinePaint(Color.white);
			            plot.setRangeGridlinesVisible(true);
			            plot.setRangeGridlinePaint(Color.GRAY);
			            CategoryItemRenderer renderer = plot.getRenderer();
			            renderer.setSeriesPaint(0, Color.GREEN);
			            renderer.setSeriesPaint(1, Color.BLACK);
			         
			            
			            
			            final CategoryAxis domainAxis = plot.getDomainAxis();
			            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			            domainAxis.setLowerMargin(0.0);
			            domainAxis.setUpperMargin(0.0);
			            domainAxis.addCategoryLabelToolTip("Type 1", "The first type.");
			            domainAxis.addCategoryLabelToolTip("Type 2", "The second type.");
			           
			            
			            
			            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			            rangeAxis.setLabelAngle(0 * Math.PI / 2.0);
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }	  
	  
	  public static String viewCustomerMonthly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int month=startday.getMonth();
		  Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		  int end=endDay.getDate();
		
		  final String series1 = "New Customer";
	        final String series2 = "Existing Customer";
	       
		Map sum=new HashMap();
    	Map sum1=new HashMap();
    	Map sum2=new HashMap();
    	for(int i=0;i<31;i++)
    	{
    		sum.put(i,0);
    		sum1.put(i,0);
    		
    	}
    	
		 
		  try{
			  int k=1;
		  for(int i=0;i<end;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startday,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
				condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
				
				Long s = delegator.findCountByCondition("Person",EntityCondition.makeCondition(condnList,EntityOperator.AND), null,null);
				Long s1 = delegator.findCountByCondition("Person",EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN,tem), null,null);
				sum.put(k,s);
				sum1.put(k,s1);
				k=k+1;
			}
		
		 
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<31;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<31;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	       
	        final JFreeChart chart = ChartFactory.createAreaChart(
	                "Customer Monthly Report",             // chart title
	                "",               // domain axis label
	                "",                  // range axis label
	                dataset,                  // data
	                PlotOrientation.VERTICAL, // orientation
	                true,                     // include legend
	                true,                     // tooltips
	                false                     // urls
	            );
	     
	            // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	            // set the background color for the chart...
//	            final StandardLegend legend = (StandardLegend) chart.getLegend();
	      //      legend.setAnchor(StandardLegend.SOUTH);

	            chart.setBackgroundPaint(Color.pink);
	        
	            final CategoryPlot plot = chart.getCategoryPlot();
	            plot.setForegroundAlpha(0.5f);
	            
	      //      plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
	            plot.setBackgroundPaint(Color.white);
	          // plot.setDomainGridlinesVisible(true);
	           // plot.setDomainGridlinePaint(Color.white);
	            plot.setRangeGridlinesVisible(true);
	            plot.setRangeGridlinePaint(Color.GRAY);
	            CategoryItemRenderer renderer = plot.getRenderer();
	            renderer.setSeriesPaint(0, Color.GREEN);
	            renderer.setSeriesPaint(1, Color.BLACK);
	         
	            
	            
	            final CategoryAxis domainAxis = plot.getDomainAxis();
	            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	            domainAxis.setLowerMargin(0.0);
	            domainAxis.setUpperMargin(0.0);
	            domainAxis.addCategoryLabelToolTip("Type 1", "The first type.");
	            domainAxis.addCategoryLabelToolTip("Type 2", "The second type.");
	           
	            
	            
	            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	            rangeAxis.setLabelAngle(0 * Math.PI / 2.0);
    String var =  ServletUtilities.getTempFilePrefix();
    //System.out.println("\n\n ######## the temp File prefix="+var);
    imagelocation = ServletUtilities.saveChartAsPNG(chart,800,400, session);
    }
    catch(Exception e){}
    
    String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
    String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
    File file = new File(filePath);
    // Destination directory
    File dir = new File(imageServerPath);

    File directory = new File(imageServerPath);
    File []listFiles = directory.listFiles();
      
                      // Move file to new directory
    boolean success = file.renameTo(new File(dir, file.getName()));
    file.delete();
    return imagelocation;
		  } 
	  public static String viewCustomerQuarterly(GenericDelegator delegator, HttpSession session)
      {
		  //List<String> monName=UtilDateTime.getMonthNames(Locale.getDefault());
		  String imagelocation=null;
		  int month1;
		  int month2;
		  int month3;
		  Timestamp firstMonthStart = null;
		  Timestamp secondMonthStart=null;
		  Timestamp thirdMonthStart=null;
		  final String series1 = "New Customer";
	      final String series2 = "Existing Customer";
	     
	      Map monName=new HashMap();
	     
		  
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int quarter=0;
		  int m=startday.getMonth();
		  if(m/3==0)
		  {
			  monName.put(1,"jan");
			  monName.put(2,"Feb");
			  monName.put(3,"Mar");
			  quarter=1;
			  
		  }
		  if(m/3==1){
			  quarter=2;
			  monName.put(1,"April");
			  monName.put(2,"May");
			  monName.put(3,"June");
		  }
		  if(m/3==2)
		  {
			  quarter=3;
			  monName.put(1,"July");
			  monName.put(2,"Aug");
			  monName.put(3,"Sep");
		  }
		  if(m/3==3)
		  {
			  monName.put(1,"Oct");
			  monName.put(2,"Nov");
			  monName.put(3,"Dec");
			  quarter=4;
		  }  
	  
		  if((m+1)%3==0)
		  {
			  
			  thirdMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(thirdMonthStart, 0, -1);
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, -1);
			
		  }
		  if((m+1)%3==1)
		  {
			  
			  firstMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(firstMonthStart,0,1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, 1);
			  
		  }
		  if((m+1)%3==2)
		  {
			  secondMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,-1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,1);
			 
				
		  }
		 
		  
		  Map conMap=new HashMap();
		 
		  Map amountMap=new HashMap();
		  Map amountMap1=new HashMap();
		  for(int i=1;i<=3;i++)
		  {
			  amountMap.put(i,0);
			  amountMap1.put(i,0);
		  }
		  conMap.put(1, firstMonthStart);
		  conMap.put(2, secondMonthStart);
		  conMap.put(3, thirdMonthStart);
		  
		  try
		  {
		  for(int i=1;i<=3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
		condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,conMap.get(i))); 
		condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap.get(i), TimeZone.getDefault(), Locale.getDefault()))); 
		long s = delegator.findCountByCondition("Person",EntityCondition.makeCondition(condnList,EntityOperator.AND), null,null);
		long s1 = delegator.findCountByCondition("Person",EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN,conMap.get(i)), null,null);
		amountMap.put(i,s);
		amountMap1.put(i,s1);
		  }
			
		
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=1;y<=3;y++)
	        {
	        	dataset.addValue((Number) amountMap.get(y),series1,(String)monName.get(y));
	        	
	        }
	        for(int y=1;y<=3;y++)
	        {
	        	dataset.addValue((Number) amountMap1.get(y),series2,(String)monName.get(y));
	        	
	        }

	        final JFreeChart chart = ChartFactory.createAreaChart(
	                "Customer Quarterly Report",             // chart title
	                "",               // domain axis label
	                "",                  // range axis label
	                dataset,                  // data
	                PlotOrientation.VERTICAL, // orientation
	                true,                     // include legend
	                true,                     // tooltips
	                false                     // urls
	            );
	     
	            // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

	            // set the background color for the chart...
//	            final StandardLegend legend = (StandardLegend) chart.getLegend();
	      //      legend.setAnchor(StandardLegend.SOUTH);

	            chart.setBackgroundPaint(Color.pink);
	        
	            final CategoryPlot plot = chart.getCategoryPlot();
	            plot.setForegroundAlpha(0.5f);
	            
	      //      plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
	            plot.setBackgroundPaint(Color.white);
	          // plot.setDomainGridlinesVisible(true);
	           // plot.setDomainGridlinePaint(Color.white);
	            plot.setRangeGridlinesVisible(true);
	            plot.setRangeGridlinePaint(Color.GRAY);
	            CategoryItemRenderer renderer = plot.getRenderer();
	            renderer.setSeriesPaint(0, Color.GREEN);
	            renderer.setSeriesPaint(1, Color.BLACK);
	         
	            
	            
	            final CategoryAxis domainAxis = plot.getDomainAxis();
	            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	            domainAxis.setLowerMargin(0.0);
	            domainAxis.setUpperMargin(0.0);
	            domainAxis.addCategoryLabelToolTip("Type 1", "The first type.");
	            domainAxis.addCategoryLabelToolTip("Type 2", "The second type.");
	           
	            
	            
	            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	            rangeAxis.setLabelAngle(0 * Math.PI / 2.0);
    String var =  ServletUtilities.getTempFilePrefix();
    //System.out.println("\n\n ######## the temp File prefix="+var);
    imagelocation = ServletUtilities.saveChartAsPNG(chart,800,400, session);
    }
    catch(Exception e){}
    
    String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
    String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
    File file = new File(filePath);
    // Destination directory
    File dir = new File(imageServerPath);

    File directory = new File(imageServerPath);
    File []listFiles = directory.listFiles();
      
                      // Move file to new directory
    boolean success = file.renameTo(new File(dir, file.getName()));
    file.delete();
    return imagelocation;
      } 
	  
	  
	  public static String viewDamageDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null; 
	       
		  int start=9;
   	   int interval=3;
   	   int end=24;
   	 final String series1 = "Today";
     final String series2 = "YesterDay";
     final String series3 = "Last Month Same Day";
	      
	       
	        List<EntityCondition> dateCondiList=new ArrayList();
	        List<EntityCondition> datePreCondiList=new ArrayList();
	        List<EntityCondition> dateMonthCondiList=new ArrayList();
	       
	        Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	           int day=d1.getDate();
	           dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
	           dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,d ));
	           dateCondiList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
	           Timestamp pds=UtilDateTime.getDayStart(d1,-1);
	            Timestamp pde=UtilDateTime.getDayEnd(pds);
	            
	            datePreCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,pds));
	            datePreCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,pde));
	            datePreCondiList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
	        	
	            Timestamp pms=UtilDateTime.getMonthStart(d1,day-1, -1);
	            Timestamp pmd=UtilDateTime.getDayEnd(pms);  
	            
	            dateMonthCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,pms));
	            dateMonthCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,pmd));
	            dateMonthCondiList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
           		
           
            
          
	        try{
	        	Map sum=new HashMap();
	        	Map sum1=new HashMap();
	        	Map sum2=new HashMap();
	        	 BigDecimal t=BigDecimal.ZERO;
	        	 BigDecimal t1=BigDecimal.ZERO;
	        	 BigDecimal t2=BigDecimal.ZERO;
	        	 for(int y=0;y<(end-start)/interval;y++)
			        {
	        		 sum.put(y,0.0);
	        		 sum1.put(y,0.0);
	        		 sum2.put(y,0.0);
	        		 
			        }
	        	   
	        	 List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(dateCondiList,EntityOperator.AND), null, null, null, true);
	        	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						 int k=0;
						GenericValue genVal = (GenericValue) itr.next();
						BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
						Timestamp date=genVal.getTimestamp("createdStamp");
						int h=date.getHours();
						for(int i=start;i<=24;i=i+interval)
						{
							if(h>=i && h<=i+interval)
							{
							t=b.add(t);
							sum.put(k,t);
							}
							k=k+1;
						}
					}
					 List <GenericValue>salespre = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(datePreCondiList,EntityOperator.AND), null, null, null, true);
		        	  
					
					 Iterator itr1 = salespre.iterator();
						while(itr1.hasNext()){
							 int k=0;
							GenericValue genVal = (GenericValue) itr1.next();
							BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
							Timestamp date=genVal.getTimestamp("createdStamp");
							int h=date.getHours();
							for(int i=start;i<=24;i=i+interval)
							{
								if(h>=i && h<i+interval)
								{
								t1=b.add(t1);
								sum1.put(k,t1);
								}
								k=k+1;
							}
						}
	        
						 List <GenericValue>salespremonth = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(dateMonthCondiList,EntityOperator.AND), null, null, null, true);
						 
						 Iterator itr2 = salespremonth.iterator();
							while(itr2.hasNext()){
								 int k=0;
								GenericValue genVal = (GenericValue) itr2.next();
								BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
								Timestamp date=genVal.getTimestamp("createdStamp");
								int h=date.getHours();
								for(int i=start;i<=24;i=i+interval)
								{
									if(h>=i && h<i+interval)
									{
									t2=b.add(t2);
									sum2.put(k,t2);
									}
									k=k+1;
								}
							}
			        	  
					 // create the dataset...
			        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	//System.out.println("the sum\n\n\n"+sum.get(y));
			        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
			        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum.get(y), series1,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum1.get(y), series2,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			        for(int y=0;y<(end-start)/interval;y++)
			        {
			        	dataset.addValue((Number) sum2.get(y), series3,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			        }
			      
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Wastage Daily Report",       // chart title
	                "",                    // domain axis label
	                "",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }
	  
	  public static String viewWastageWeekly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		  Timestamp d1=UtilDateTime.getWeekStart(UtilDateTime.nowTimestamp());
		  int day=startday.getDate();
		  
		  Timestamp lastweek=UtilDateTime.getDayStart(startday,-7);
		  Timestamp d2=UtilDateTime.getWeekStart(lastweek);
		  Timestamp monthstart=UtilDateTime.getMonthStart(d1,day-1, -1);
		  Timestamp d3=UtilDateTime.getWeekStart(monthstart);
		  //List<EntityCondition> condnList=new ArrayList();
		  final String series1 = "This Week";
	        final String series2 = "Last Week";
	        final String series3 = "Last Month Same Week";
		Map sum=new HashMap();
      	Map sum1=new HashMap();
      	Map sum2=new HashMap();
      	for(int i=0;i<7;i++)
      	{
      		sum.put(i,0.0);
      		sum1.put(i,0.0);
      		sum2.put(i,0.0);
      	}
		  int k=0;
		  try{
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d1,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,tem ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,temEnd ));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
					       
					     t=b.add(t);
						     
						}
				sum.put(k,t);
				k=k+1;
				
				
					}
		  int n=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d2,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,tem ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,temEnd ));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
					       
					     t=b.add(t);
						     
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<7;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(d3,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,tem ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,temEnd ));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
					       
					     t=b.add(t);
						     
						}
						
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	        for(int y=0;y<7;y++)
	        {
	        	//System.out.println("the sum\n\n\n"+sum.get(y));
	        	//System.out.println("the sum1\n\n\n"+sum1.get(y));
	        	//System.out.println("the sum2\n\n\n"+sum2.get(y));
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<7;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	        
	        
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Wastage Weekly Report",       // chart title
	                "",                    // domain axis label
	                "",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
		
	        
		  }
	  public static String viewWastageMonthly(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null;
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int month=startday.getMonth();
		  Timestamp endDay=UtilDateTime.getMonthEnd(UtilDateTime.nowTimestamp(), TimeZone.getDefault(), Locale.getDefault());
		  int end=endDay.getDate();
		  Timestamp startPre=UtilDateTime.getMonthStart(startday,0,-1);
		  Timestamp endDayPre=UtilDateTime.getMonthEnd(startPre, TimeZone.getDefault(), Locale.getDefault());
		 int endPre=endDayPre.getDate();
		 Timestamp startYear=UtilDateTime.getYearStart(UtilDateTime.nowTimestamp(), 0, month, -1);
		 Timestamp endDayYear=UtilDateTime.getMonthEnd(startYear, TimeZone.getDefault(), Locale.getDefault());
		 int endYear=endDayYear.getDate();
		  final String series1 = "This Month";
	        final String series2 = "Last Month";
	        final String series3 = "Last Year Same Month";
		Map sum=new HashMap();
    	Map sum1=new HashMap();
    	Map sum2=new HashMap();
    	for(int i=0;i<end;i++)
    	{
    		sum.put(i,0.0);
    		
    	}
    	for(int i=0;i<endPre;i++)
    	{
    		sum1.put(i,0.0);
    		
    	}
    	for(int i=0;i<endYear;i++)
    	{
    		sum2.put(i,0.0);
    		
    	}
		 
		  try{
			  int k=0;
		  for(int i=0;i<end;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startday,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,tem ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,temEnd ));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
						     
						}
						
				sum.put(k,t);
				k=k+1;
			}
		  int n=0;
		  for(int i=0;i<endPre;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startPre,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.GREATER_THAN_EQUAL_TO,tem)); 
			condnList.add(EntityCondition.makeCondition("createdStamp",EntityOperator.LESS_THAN_EQUAL_TO,temEnd)); 
			condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			 List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
	       	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						
						GenericValue genVal = (GenericValue) itr.next();
						BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
					       
					     t=b.add(t);
						     
						
						}
				sum1.put(n,t);
				n=n+1;
				
					}
		  int p=0;
		  for(int i=0;i<endYear;i++)
		  {
			  List<EntityCondition> condnList=new ArrayList();
			  BigDecimal t=BigDecimal.ZERO;
			  Timestamp tem=UtilDateTime.getDayStart(startYear,i);
			  Timestamp temEnd=UtilDateTime.getDayEnd(tem);
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,tem ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,temEnd ));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
			  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
       	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
					       
					     t=b.add(t);
						     
						}
				sum2.put(p,t);
				p=p+1;
				
					}
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<end;y++)
	        {
	        	dataset.addValue((Number) sum.get(y), series1,y+1);
	        	
	        }
	        for(int y=0;y<endPre;y++)
	        {
	        	dataset.addValue((Number) sum1.get(y), series2,y+1);
	        	
	        }
	        for(int y=0;y<endYear;y++)
	        {
	        	dataset.addValue((Number) sum2.get(y), series3,y+1);
	        	
	        }
	        
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Wastage Monthly Report",       // chart title
	                "",                    // domain axis label
	                "",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,900,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
	        
		  }
	  public static String viewWastageQuarterly(GenericDelegator delegator, HttpSession session)
      {
		  //List<String> monName=UtilDateTime.getMonthNames(Locale.getDefault());
		  String imagelocation=null;
		  int month1;
		  int month2;
		  int month3;
		  Timestamp firstMonthStart = null;
		  Timestamp secondMonthStart=null;
		  Timestamp thirdMonthStart=null;
		  final String series1 = "This Quater";
	      final String series2 = "Last Year same Quarter";
	     
	      Map monName=new HashMap();
	     
		  
		  Timestamp startday=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
		  int quarter=0;
		  int m=startday.getMonth();
		  if(m/3==0)
		  {
			  monName.put(0,"jan");
			  monName.put(1,"Feb");
			  monName.put(2,"Mar");
			  quarter=1;
			  
		  }
		  if(m/3==1){
			  quarter=2;
			  monName.put(0,"April");
			  monName.put(1,"May");
			  monName.put(2,"June");
		  }
		  if(m/3==2)
		  {
			  quarter=3;
			  monName.put(0,"July");
			  monName.put(1,"Aug");
			  monName.put(2,"Sep");
		  }
		  if(m/3==3)
		  {
			  monName.put(0,"Oct");
			  monName.put(1,"Nov");
			  monName.put(2,"Dec");
			  quarter=4;
		  }  
	  
		  if((m+1)%3==0)
		  {
			  
			  thirdMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(thirdMonthStart, 0, -1);
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, -1);
			
		  }
		  if((m+1)%3==1)
		  {
			  
			  firstMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  secondMonthStart=UtilDateTime.getMonthStart(firstMonthStart,0,1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart, 0, 1);
			  
		  }
		  if((m+1)%3==2)
		  {
			  secondMonthStart=UtilDateTime.getMonthStart(UtilDateTime.nowTimestamp());
			  firstMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,-1);
			  thirdMonthStart=UtilDateTime.getMonthStart(secondMonthStart,0,1);
			 
				
		  }
		 
		  
		  Map conMap=new HashMap();
		  Map conMap1=new HashMap();
		  Map amountMap=new HashMap();
		  Map amountMap1=new HashMap();
		  conMap.put(0, firstMonthStart);
		  conMap.put(1, secondMonthStart);
		  conMap.put(2, thirdMonthStart);
		  conMap1.put(0, UtilDateTime.getMonthStart(firstMonthStart,0,-12));
		  conMap1.put(1, UtilDateTime.getMonthStart(secondMonthStart,0,-12));
		  conMap1.put(2, UtilDateTime.getMonthStart(thirdMonthStart,0,-12));
		  
		  try
		  {
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,conMap.get(i) ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap.get(i), TimeZone.getDefault(), Locale.getDefault())));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
		  
		
		  List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
 	   Iterator itr = sales.iterator();
			while(itr.hasNext()){
				
				GenericValue genVal = (GenericValue) itr.next();
				BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
				       
				     amount=b.add(amount);
					    
					 }
					
			amountMap.put(i,amount);
		  }
			
		  for(int i=0;i<3;i++)
		  {
			  List condnList=new ArrayList();
			  BigDecimal amount=BigDecimal.ZERO;
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,conMap1.get(i) ));
			  condnList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getMonthEnd((Timestamp) conMap1.get(i), TimeZone.getDefault(), Locale.getDefault())));
			  condnList.add(EntityCondition.makeCondition("varianceReasonId", EntityOperator.EQUALS,"VAR_DAMAGED"));
		
		List <GenericValue>sales = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(condnList,EntityOperator.AND), null, null, null, true);
	 	   Iterator itr = sales.iterator();
				while(itr.hasNext()){
					
					GenericValue genVal = (GenericValue) itr.next();
					BigDecimal b =  genVal.getBigDecimal("quantityOnHandVar");
				       
				     amount=b.add(amount);
					
					}
			amountMap1.put(i,amount);
		  }
		  final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        
	       
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap.get(y),series1,(String)monName.get(y));
	        	
	        }
	        for(int y=0;y<3;y++)
	        {
	        	dataset.addValue((Number) amountMap1.get(y),series2,(String)monName.get(y));
	        	
	        }
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Wastage Monthly Report",       // chart title
	                "",                    // domain axis label
	                "",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,400,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
		  }
	/*  public static String viewDeliverDaily(GenericDelegator delegator, HttpSession session)
      {
		  String imagelocation=null; 
	       
		  int start=9;
   	   int interval=3;
   	   int end=24;
   	 final String series1 = "Product Report";
     
	       
	        List<EntityCondition> dateCondiList=new ArrayList();
	        List<EntityCondition> datePreCondiList=new ArrayList();
	        List<EntityCondition> dateMonthCondiList=new ArrayList();
	       
	        Timestamp d=UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp());
	           Timestamp d1=UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
	          
	        try{
	        	Map sum=new HashMap();
	        	
	        	
	        	 
	        	 for(int y=0;y<2*(end-start)/interval;y++)
			        {
	        		 sum.put(y,0);
	        		
	        		 
			        }
	     
	        	 
	        	 dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,d1 ));
		           dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,d));
		           dateCondiList.add(EntityCondition.makeCondition("shipByDate", EntityOperator.NOT_EQUAL,null));
	        	 List <GenericValue>sales = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(dateCondiList,EntityOperator.AND), null, null, null, true);
	        	   Iterator itr = sales.iterator();
					while(itr.hasNext()){
						 int k=0;
						// int g=((end-start)/interval)+1;
						GenericValue genVal = (GenericValue) itr.next();
						
						Timestamp date=genVal.getTimestamp("shipByDate");
						int h=date.getHours();
						for(int i=start;i<=24;i=i+interval)
						{
							
							if(h>=i && h<i+interval)
							{
						sum.put(k,(Integer) sum.get(k)+1);
							}
							k=k+1;
						}
					}
					dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(d1, 1) ));
			           dateCondiList.add(EntityCondition.makeCondition("createdStamp", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.getDayStart(d1, 1)) ));
			           dateCondiList.add(EntityCondition.makeCondition("shipByDate", EntityOperator.NOT_EQUAL,null));
		        	 List <GenericValue>sales1 = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(dateCondiList,EntityOperator.AND), null, null, null, true);
		        	   Iterator itr1 = sales1.iterator();
						while(itr1.hasNext()){
							 
							int g=((end-start)/interval)+1;
							GenericValue genVal = (GenericValue) itr1.next();
							
							Timestamp date=genVal.getTimestamp("shipByDate");
							int h=date.getHours();
							for(int i=start;i<=24;i=i+interval)
							{
								
								if(h>=i && h<i+interval)
								{
							sum.put(g,(Integer) sum.get(g)+1);
								}
								g=g+1;
							}
						}
		        	 
	        	 
					 
					 // create the dataset...
			        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			        
			      
			        for(int y=0;y<2*((end-start)/interval);y++)
			        {
			        	dataset.addValue((Number) sum.get(y), series1,""+(start+(y*interval))+"-"+(start+((y+1)*interval))+"");
			        	
			      
			        }
			     
	        final JFreeChart chart = ChartFactory.createBarChart(
	                "Product Report",       // chart title
	                "",                    // domain axis label
	                "",                   // range axis label
	                dataset,                   // data
	                PlotOrientation.VERTICAL,  // orientation
	                true,                      // include legend
	                true,                      // tooltips
	                false                      // urls
	            );
	        chart.setBackgroundPaint(Color.pink);

	        final CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
	        
	        plot.setRangeGridlinePaint(Color.BLACK);
	        plot.setBackgroundPaint(Color.white);
	        // customise the range axis...
	        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	        rangeAxis.setAutoRangeIncludesZero(true);
	        plot.getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(2, new BasicStroke(2.0f));
	        plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
	        //final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
	        
	        final BarRenderer renderer1 = (BarRenderer) plot.getRenderer();
	        renderer1.setDrawBarOutline(false);
	        
	        // set up gradient paints for series...
	        final GradientPaint gp0 = new GradientPaint(
	            0.0f, 0.0f, Color.blue, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp1 = new GradientPaint(
	            0.0f, 0.0f, Color.green, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        final GradientPaint gp2 = new GradientPaint(
	            0.0f, 0.0f, Color.red, 
	            0.0f, 0.0f, Color.lightGray
	        );
	        renderer1.setSeriesPaint(0, gp0);
	        renderer1.setSeriesPaint(1, gp1);
	        renderer1.setSeriesPaint(2, gp2);

	        final CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setCategoryMargin(0.04f);
	        renderer1.setItemMargin(0.0);
	        domainAxis.setCategoryLabelPositions(
	            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
	        );
	        
	        String var =  ServletUtilities.getTempFilePrefix();
            //System.out.println("\n\n ######## the temp File prefix="+var);
            imagelocation = ServletUtilities.saveChartAsPNG(chart,500,400, session);
	        }
	        catch(Exception e){}
	        
	        String filePath = System.getProperty("java.io.tmpdir") + imagelocation ;
            String imageServerPath = System.getProperty("ofbiz.home")+ "/framework/images/webapp/images/pos/dashboards/";
            File file = new File(filePath);
            // Destination directory
            File dir = new File(imageServerPath);

            File directory = new File(imageServerPath);
            File []listFiles = directory.listFiles();
              
                              // Move file to new directory
            boolean success = file.renameTo(new File(dir, file.getName()));
            file.delete();
            return imagelocation;
      }*/
	  
	  
}
