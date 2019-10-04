<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Response</title>
<link href="../css/default.css" rel="stylesheet" type="text/css">

</head>
<body>
	<div id="page-header">
		<div class="page-wrap">
			<div class="logo-wrapper">
				<a href="/citruspay-admin-site/"> <img height="32" width="81"
					src="../images/logo_citrus.png" alt="Citrus" />
				</a>
			</div>
		</div>
	</div>
	<div id="page-client-logo">&#160;</div>
	<div id="page-wrapper">
		<div class="box-white">
			<div class="page-content">
				<!-- content goes here -->
				<%@ page language="java" import="java.lang.*" session="false"
					isErrorPage="false"%>
				<div>
					<h3>Transaction Response</h3>
					<ul class="tbl-wrapper clearfix" id="chkoutPageUserPramList">
						<li class="tbl-header">
							<div class="tbl-col col-1">Txn Id</div>
							<div class="tbl-col col-3">Txn Ref No</div>
							<div class="tbl-col col-3">PG Txn Id</div>
							<div class="tbl-col col-3">Txn Status</div>
							<div class="tbl-col col-3">Txn Amount</div>
							<div class="tbl-col col-6">Txn Message</div>
						</li>
						<%
						String key = "bf6f5bef3eb884d3420ce9cff6841c140f943b9a";
						String data="";
						String txnId=request.getParameter("TxId");
						String txnStatus=request.getParameter("TxStatus"); 
						String amount=request.getParameter("amount"); 
						String pgTxnId=request.getParameter("pgTxnNo");
						String issuerRefNo=request.getParameter("issuerRefNo"); 
						String authIdCode=request.getParameter("authIdCode");
						String firstName=request.getParameter("firstName");
						String lastName=request.getParameter("lastName");
						String pgRespCode=request.getParameter("pgRespCode");
						String zipCode=request.getParameter("addressZip");
						String reqSignature=request.getParameter("signature");
						System.out.println("resp signature"+reqSignature);
						String signature="";
						boolean flag = true;
						if (txnId != null) {
							data += txnId;
						}
						if (txnStatus != null) {
							data += txnStatus;
						}
						if (amount != null) {
							data += amount;
						}
						if (pgTxnId != null) {
							data += pgTxnId;
						}
						if (issuerRefNo != null) {
							data += issuerRefNo;
						}
						if (authIdCode != null) {
							data += authIdCode;
						}
						if (firstName != null) {
							data += firstName;
						}
						if (lastName != null) {
							data += lastName;
						}
						if (pgRespCode != null) {
							data += pgRespCode;
						}
						if (zipCode != null) {
							data += zipCode;
						}
						com.citruspay.pg.net.RequestSignature sigGenerator = new com.citruspay.pg.net.RequestSignature(); 
						
						try {
							signature = sigGenerator.generateHMAC(data, key);
							System.out.println("generated signature"+signature);
							if(reqSignature !=null && !reqSignature.equalsIgnoreCase("") &&!signature.equalsIgnoreCase(reqSignature)){
								flag = false;
							}
						}catch(Exception e){
							e.printStackTrace();	
						}
						if(flag){
						%>
						<li>
							<div class="tbl-col col-1">
								<%
									out.println(request.getParameter("TxId") == null ? "" : request
											.getParameter("TxId"));
								%>
							</div>
							<div class="tbl-col col-3">
								<%
									out.println(request.getParameter("TxRefNo") == null ? "" : request
											.getParameter("TxRefNo"));
								%>
							</div>
							<div class="tbl-col col-3">
								<%
									out.println(request.getParameter("pgTxnNo") == null ? "" : request
											.getParameter("pgTxnNo"));
								%>
							</div>
							
							<div class="tbl-col col-3">
								<%
									if(request.getParameter("TxStatus") != null){
										out.println(request.getParameter("TxStatus"));
									}else{
										out.println("FAIL");
									}
								%>
							</div>
							<div class="tbl-col col-3">
								<%
									out.println(request.getParameter("amount") == null ? "" : request
											.getParameter("amount"));
								%>
							</div>
							<div class="tbl-col col-6">
								<%
								if(request.getParameter("TxMsg") !=null){
									out.println(request.getParameter("TxMsg"));
								}else if(request.getParameter("mandatoryErrorMsg") !=null){
									out.println(request.getParameter("mandatoryErrorMsg"));
								}else if(request.getParameter("paidTxnExists") !=null){
									out.println(request.getParameter("paidTxnExists"));
								}
								%>
								
							</div>

						</li>
						<%
							}else{
						%>
						<li>
							<div class="tbl-col col-6">Request Signature Error</div>

						</li>
						<%
							}
						%>
					</ul>
					<br/>
					<br/>
					<h3>Consumer Details:</h3>
					<ul class="form-wrapper add-merchant clearfix"">
						<li class="clearfix"><label>First Name: </label> <%
						 	out.println(request.getParameter("firstName") == null ? ""
						 			: request.getParameter("firstName"));
						 %></li>
												<li class="clearfix"><label>Last Name: </label> <%
						 	out.println(request.getParameter("lastName") == null ? "" : request
						 			.getParameter("lastName"));
						 %></li>
												<li class="clearfix"><label>Email: </label> <%
						 	out.println(request.getParameter("email") == null ? "" : request
						 			.getParameter("email"));
						 %></li>
												<li class="clearfix"><label>Address:  </label> <%
						 	out.println(request.getParameter("addressStreet1") == null ? ""
						 			: request.getParameter("addressStreet1"));
						 	out.println(request.getParameter("addressStreet2") == null ? ""
						 			: request.getParameter("addressStreet2"));
						 %></li>
												<li class="clearfix"><label>City: </label> <%
						 	out.println(request.getParameter("addressCity") == null ? ""
						 			: request.getParameter("addressCity"));
						 %></li>
												<li class="clearfix"><label>State: </label> <%
						 	out.println(request.getParameter("addressState") == null ? ""
						 			: request.getParameter("addressState"));
						 %></li>
												<li class="clearfix"><label>Country: </label> <%
						 	out.println(request.getParameter("addressCountry") == null ? ""
						 			: request.getParameter("addressCountry"));
						 %></li>
												<li class="clearfix"><label>Pin Code: </label> <%
						 	out.println(request.getParameter("addressZip") == null ? ""
						 			: request.getParameter("addressZip"));
						 %></li>
						 <li class="clearfix"><label>Mobile No: </label> <%
						 	out.println(request.getParameter("mobileNo") == null ? ""
						 			: request.getParameter("mobileNo"));
						 %></li>						 
						  <!-- content for custom parameter if any starts here - 
						  	  sample code for Custom Parameter named as "Roll Number" and can be repeated with change in name if there are multiple -->
						 <!-- 
						 <li class="clearfix"><label>Roll Number: </label> --> <%--<%
						 	out.println(request.getParameter("Roll Number") == null ? ""
						 			: request.getParameter("Roll Number"));
						 %>--%>
						 <!-- </li>
						  -->
 						<!-- content for custom parameter if any ends here -->
					</ul>
					<br/>
					<h3>Payment Details:</h3>
					<ul class="form-wrapper add-merchant clearfix"">
						<li class="clearfix"><label>Payment Mode: </label> <%
						 	out.println(request.getParameter("paymentMode") == null ? ""
						 			: request.getParameter("paymentMode"));
						 %></li>
						 </li>
						  <li class="clearfix"><label>Txn Gateway: </label> <%
						 	out.println(request.getParameter("TxGateway") == null ? ""
						 			: request.getParameter("TxGateway"));
						 %></li>
						  <li class="clearfix"><label>Card Number: </label> <%
						 	out.println(request.getParameter("maskedCardNumber") == null ? ""
						 			: request.getParameter("maskedCardNumber"));
						 %></li>
						  <li class="clearfix"><label>Card Type: </label> <%
						 	out.println(request.getParameter("cardType") == null ? ""
						 			: request.getParameter("cardType"));
						 %></li>
						  <li class="clearfix"><label>Issuer Code: </label> <%
						 	out.println(request.getParameter("issuerCode") == null ? ""
						 			: request.getParameter("issuerCode"));
						 %></li>
					</ul>

				</div>
				<!-- end content -->
			</div>
		</div>
	</div>
	<div
		style="padding-left: 800px; padding-bottom: 20px; padding-top: 20px;">
		<div>Copyrights © 2012 Citrus.</div>
	</div>
</body>

</html>