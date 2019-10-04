<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Transaction Inquiry Details</title>
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
				<%@ page language="java" import="java.util.*" session="false"
					isErrorPage="false"%>

				<form name="searchForm">
					<input type="hidden" name="search" value="">

					<div class="paddling-lef:5px;">
						<h2>Transaction Inquiry</h2>
					</div>

					<div>
						<ul class="form-wrapper add-merchant clearfix">
							<li class="clearfix"><label width="125px;">Merchant
									Access Key :</label> <input type="text" name="merchantId" size="16"
								class="text" maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">Merchant
									Txn ID:</label> <input type="text" name="merchantTxnID" size="64"
								class="text" maxlength="64" value=""></li>

							<li><label width="125px;">&nbsp;</label> <input
								type="button" class="btn-orange" name="btnSub" value="Search"
								onClick="JavaScript:onClk_Submit();"></li>

						</ul>
					</div>
				</form>

				<%
					if ("Submit".equals(request.getParameter("search"))) {
						String key = "338a66359a415abb25586f19192f62ff8df736ba";
						com.citruspay.pg.util.CitruspayConstant.merchantKey = key;
						String merchantId = request.getParameter("merchantId");
						String merchantTxnId = request.getParameter("merchantTxnID");
						java.util.Map map = new java.util.HashMap();
						map.put("merchantAccessKey", merchantId);
						map.put("transactionId", merchantTxnId);
						map.put("bankName", "ABC BANK");

						com.citruspay.pg.model.EnquiryCollection enquiryResult = com.citruspay.pg.model.Enquiry
								.create(map);

						System.out
								.println("PGSearchResponse received from payment gateway:"
										+ enquiryResult.getRespMsg());
				%>
				
				<div style="overflow: auto;">
					<h3>Transaction History</h3>
					<ul class="tbl-wrapper clearfix" id="chkoutPageUserPramList" >
						<li class="tbl-header" style="width: 960px;">
							<div style="width: 1300px;">
								<div class="tbl-col col-7">Response Code</div>
								<div class="tbl-col col-14">Response Message</div>
								<div class="tbl-col col-3">Txn Id</div>
								<div class="tbl-col col-4">Epg Txn Id</div>
								<div class="tbl-col col-5">AuthIdCode:</div>
								<div class="tbl-col col-4">Issuer Ref. No.</div>
								<div class="tbl-col col-11">Txn Amount</div>
								<div class="tbl-col col-11">Txn Type</div>
								<div class="tbl-col col-11">Txn Date</div>
								<div class="tbl-col col-7">Payment Mode</div>
								<div class="tbl-col col-7">Txn Gateway</div>
								<div class="tbl-col col-1">Card Number</div>
								<div class="tbl-col col-7">Card Type</div>
								<div class="tbl-col col-7">Issuer Code</div>
							</div>
						</li>
	<%
		List<com.citruspay.pg.model.Enquiry> enqList = enquiryResult.getEnquiry();
		if(enqList != null && !enqList.isEmpty()){
			for(int i=0; i<enqList.size(); i++){
				com.citruspay.pg.model.Enquiry  enquiry = enqList.get(i);
	%>
						
						<li>
							<div style="width: 1300px;">
								<div class="tbl-col col-7">
									<%
										out.println(enquiry.getRespCode());
									%>
								</div>
								<div class="tbl-col col-14">
									<%
										out.println(enquiry.getRespMsg());
									%>
								</div>
								<div class="tbl-col col-3">
									<%
										out.println(enquiry.getTxnId() == null ? "" : enquiry
													.getTxnId());
									%>
								</div>
								<div class="tbl-col col-4">
									<%
										out.println(enquiry.getPgTxnId() == null ? "" : enquiry
													.getPgTxnId());
									%>
								</div>
								<div class="tbl-col col-5">
									<%
										out.println(enquiry.getAuthIdCode() == null ? "" : enquiry
													.getAuthIdCode());
									%>
								</div>
								<div class="tbl-col col-4">
									<%
										out.println(enquiry.getRrn() == null ? "" : enquiry.getRrn());
									%>
								</div>
								<div class="tbl-col col-11">
									<%
										out.println(enquiry.getAmount() == null ? "" : enquiry
													.getAmount());
									%>
								</div>
								<div class="tbl-col col-11">
									<%
										out.println(enquiry.getTxnType() == null ? "" : enquiry
													.getTxnType());
									%>
								</div>
								<div class="tbl-col col-11">
									<%
										out.println(enquiry.getTxnDateTime() == null ? "" : enquiry
													.getTxnDateTime().substring(0,10));
									%>
								</div>
								<div class="tbl-col col-7">
									<%
										out.println(enquiry.getPaymentMode() == null ? "" : enquiry
												.getPaymentMode());
									%>
								</div>
								<div class="tbl-col col-7">
									<%
										out.println(enquiry.getTxnGateway() == null ? "" : enquiry
												.getTxnGateway());
									%>
								</div>
								<div class="tbl-col col-1">
									<%
										out.println(enquiry.getMaskedCardNumber() == null ? "" : enquiry
												.getMaskedCardNumber());
									%>
								</div>
								<div class="tbl-col col-7">
									<%
										out.println(enquiry.getCardType() == null ? "" : enquiry
												.getCardType());
									%>
								</div>
								<div class="tbl-col col-7">
									<%
										out.println(enquiry.getIssuerCode() == null ? "" : enquiry
												.getIssuerCode());
									%>
								</div>
							</div>	
						</li>
						<%
												}
							}else{	%>
							
							<div class="tbl-col col-1">
								<%
								out.println(enquiryResult.getRespCode());
								%>
							</div>
							<div class="tbl-col col-2">
								<%
								out.println(enquiryResult.getRespMsg());
								%>
							</div>
							
							<%	
							}
					%>
					</ul>
				</div>
				<%
					}
				%>
				<script language="javascript">
					function onClk_Submit() {
						var mrtId = document.searchForm.merchantId.value;
						var mrtTxnId = document.searchForm.merchantTxnID.value;

						if (mrtId.length > 0 && mrtTxnId.length > 0) {
							document.searchForm.search.value = "Submit";
							document.searchForm.method = "POST";
							document.searchForm.submit();
						} else {
							if (mrtId.length <= 0) {
								alert("Please Enter Merchant Access Key");
								return;
							}

							if (mrtTxnId.length <= 0) {
								alert("Please Enter Merchant Transaction No");
								return;
							}
							return;

						}
					}
				</script>
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
