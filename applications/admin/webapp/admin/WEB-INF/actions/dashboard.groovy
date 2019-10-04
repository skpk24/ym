import org.ofbiz.accounting.Reports;
import org.ofbiz.order.report.Report;
import java.io.PrintWriter;

imagelocation="";
if((request.getParameter("reportType")).equals("Daily Sales Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Report.SalesChart(request,response);
	 }
if((request.getParameter("reportType")).equals("Weekly Sales Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Reports.viewSalesWeekly(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Monthly Sales Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation =  Reports.viewSalesMonthly(delegator, session);
		 }
if((request.getParameter("reportType")).equals("Daily purchase Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Reports.viewPurchaseDaily(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Weekly purchase Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Reports.viewPurchaseWeekly(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Monthly purchase Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Reports.viewPurchaseMonthly(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Daily customer Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation =  Reports.viewCustomerDaily(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Weekly customer Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation =Reports.viewCustomerWeekly(delegator, session);
	 }
if((request.getParameter("reportType")).equals("Monthly customer Report") && (request.getParameter("category")).equals("chart"))
	 {
		 imagelocation = Reports.viewCustomerMonthly(delegator, session);
	 }
	/* if((request.getParameter("reportType")).equals("Daily Sales Report") && (request.getParameter("category")).equals("csv"))
	 {
		 imagelocation = Report.revenueReoprtCSV(request, response);
	 }*/
PrintWriter out=response.getWriter();
out.println(imagelocation);
return "success";
