<%@ page language="java" import="java.lang.*,com.citruspay.pg.*"
		session="false" isErrorPage="false"%>

<%
String key = "300541dcbe637bc63b3999e0a7e262b80aa3d100";
String merchantId = request.getParameter("merchantId");
String orderAmount = request.getParameter("orderAmount");
String merchantTxnId = request.getParameter("merchantTxnId");
String currency = "INR";//request.getParameter("currency");
com.citruspay.pg.net.RequestSignature reqSigantureGen = new com.citruspay.pg.net.RequestSignature(); 
String data=merchantId+orderAmount+merchantTxnId+currency;
try {
%>
<%= reqSigantureGen.generateHMAC(data, key) %>
<%
}catch(Exception e){
		e.printStackTrace();
}
%>