<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Refund</title>
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

				<form name="frmRelated">
					<input type="hidden" name="actionchanged" value="">

					<div>
						<h2>Refund</h2>
					</div>
					<div>
						<ul class="form-wrapper add-merchant clearfix">
							<li class="clearfix"><label width="125px;">Merchant
									Access Key :</label> <input type="text" name="MerchantID" size="16"
								class="text" maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">Merchant
									Txn ID:</label> <input type="text" name="MerchantTxnID" size="64"
								class="text" maxlength="64" value=""></li>
							<li class="clearfix"><label width="125px;">Enter
									Root Sys Ref No :</label> <input type="text" name="RootSysRefNum"
								size="64" class="text" maxlength="64" value=""></li>
							<li class="clearfix"><label width="125px;">Enter
									Root RRN No :</label> <input type="text" name="RootPRefNum" size="64"
								class="text" maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">Enter
									Root Auth Code:</label> <input type="text" name="RootAuthID" size="64"
								class="text" maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">Enter
									Curr Code :</label> <input type="text" name="CurrCode" size="64"
								class="text" maxlength="64" value="INR"></li>

							<li class="clearfix"><label width="125px;">Enter the
									Amount:</label> <input type="text" name="Amount" size="64" class="text"
								maxlength="64" value=""></li>

							<li class="clearfix"><label width="125px;">
									Message Type:</label><label>Refund</label>
									
									<input type="hidden" name="MessageType"  id="MessageType" value="R" />
							</li>

							<li><label width="125px;">&nbsp;</label> <input type="button" class="btn-orange"
								name="btnSub" value="Submit"
								onClick="JavaScript:onClk_Submit();"></li>

						</ul>
					</div>
				</form>

				<%
						if ("Submit".equals(request.getParameter("actionchanged"))) {
							String key = "338a66359a415abb25586f19192f62ff8df736ba";
							com.citruspay.pg.util.CitruspayConstant.merchantKey = key;
							java.util.Map params = new java.util.HashMap();
							params.put("merchantAccessKey", request.getParameter("MerchantID"));
							params.put("transactionId",
									request.getParameter("MerchantTxnID"));
							params.put("pgTxnId", request.getParameter("RootSysRefNum"));
							params.put("RRN", request.getParameter("RootPRefNum"));
							params.put("authIdCode", request.getParameter("RootAuthID"));
							params.put("currencyCode", request.getParameter("CurrCode"));
							params.put("txnType", request.getParameter("MessageType"));
							params.put("amount", request.getParameter("Amount"));
							params.put("bankName", "ABC BANK");
							com.citruspay.pg.model.Refund refund = com.citruspay.pg.model.Refund
									.create(params);
					%>

				<div>
					<h3>Transaction Refund Response</h3>
					<ul class="tbl-wrapper clearfix" id="chkoutPageUserPramList">
						<li class="tbl-header">
							<div class="tbl-col col-1">Response Code</div>
							<div class="tbl-col col-2">Response Message</div>
							<div class="tbl-col col-3">Txn Id</div>
							<div class="tbl-col col-4">Epg Txn Id</div>
							<div class="tbl-col col-5">AuthIdCode:</div>
							<div class="tbl-col col-6">Issuer Ref. No.</div>
							<div class="tbl-col col-7">Txn Amount</div>
						</li>

						<li>
							<div class="tbl-col col-1">
								<%
										out.println(refund.getRespCode());
									%>
							</div>
							<div class="tbl-col col-2">
								<%
										out.println(refund.getRespMessage());
									%>
							</div>
							<div class="tbl-col col-3">
								<%
										out.println(refund.getMerTxnId() == null ? "" : refund.getMerTxnId());
									%>
							</div>
							<div class="tbl-col col-4">
								<%
										out.println(refund.getPgTxnId() == null ? "" : refund
													.getPgTxnId());
									%>
							</div>
							<div class="tbl-col col-5">
								<%
										out.println(refund.getAuthIdCode() == null ? "" : refund
													.getAuthIdCode());
									%>
							</div>
							<div class="tbl-col col-6">
								<%
										out.println(refund.getRRN() == null ? "" : refund.getRRN());
									%>
							</div>
							<div class="tbl-col col-7">
								<%
										out.println(refund.getAmount() == null ? "" : refund
													.getAmount());
								%>
							</div>
						</li>
					</ul>
				</div>
				<%
						}
					%>
				<script language="javascript">
						function onClk_Submit() {

							var mrtId = document.frmRelated.MerchantID.value;
							var mrtTxnId = document.frmRelated.MerchantTxnID.value;
							var TxnRefNo = document.frmRelated.RootSysRefNum.value;
							var prevTxnRefNo = document.frmRelated.RootPRefNum.value;
							var authId = document.frmRelated.RootAuthID.value;
							var currcode = document.frmRelated.CurrCode.value;
							var amt = document.frmRelated.Amount.value;

							if (mrtId.length > 0 && mrtTxnId.length > 0
									&& TxnRefNo.length > 0
									&& prevTxnRefNo.length > 0
									&& authId.length > 0 && currcode.length > 0
									&& amt.length > 0) {

								document.frmRelated.actionchanged.value = "Submit";
								document.frmRelated.method = "POST";
								document.frmRelated.submit();
							} else {
								alert("All the fields are mandatory");
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