<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<link href="../css/default.css" rel="stylesheet" type="text/css">
<title>SSL Integration</title>


 		<%
		 java.util.Map requestData = null;
 		 requestData = (java.util.Map) request.getAttribute("requestData");
		 if(requestData == null) requestData = (java.util.Map) request.getSession().getAttribute("requestData");
		 if(requestData == null) requestData = new java.util.HashMap();
		%>


<SCRIPT type="text/javascript">
	var merchantURLPart = "https://sandbox.citruspay.com/yjqcx6vapn";
	var vanityURLPart="";
	var reqObj = null;
	function generateHMAC() {
		if (window.XMLHttpRequest) {
			reqObj = new XMLHttpRequest();
		} else {
			reqObj = new ActiveXObject("Microsoft.XMLHTTP");
		}
		if(merchantURLPart.lastIndexOf("/") != -1){
			vanityURLPart= merchantURLPart.substring(merchantURLPart.lastIndexOf("/")+1)
		}
		var orderAmount = document.getElementById("orderAmount").value;
		var merchantTxnId = document.getElementById("merchantTxnId").value;
		var currency = document.getElementById("currency").value;
		var param = "merchantId=" + vanityURLPart + "&orderAmount=" + orderAmount
				+ "&merchantTxnId=" + merchantTxnId + "&currency=" + currency;
		reqObj.onreadystatechange = process;
		reqObj.open("POST", "payCitrus?"+param, false);
		reqObj.send(null);
	}
	function process() {
		if (reqObj.readyState == 4) {
			document.getElementById("secSignature").value = reqObj.responseText;
			submitForm();
		}
	}

	function submitForm() {
		document.paymentForm.action = merchantURLPart;
		document.paymentForm.method = 'POST';
		document.paymentForm.submit();
	}
</SCRIPT>
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
	<form name="paymentForm" id="paymentForm">
	<div>
	<ul class="form-wrapper add-merchant clearfix">
	
		<li class="clearfix"> <label width="125px;">Transaction Number:</label> <input type="text" id="merchantTxnId" class="text" name="merchantTxnId" value="<%=requestData.get("transId") %>" /> </li>
	
		<li class="clearfix"> <label width="125px;">Order Amount:</label>
			 <input type="text" id="orderAmount" class="text" name="orderAmount" value="<%=requestData.get("orderAmount") %>" /> </li>

		<li class="clearfix"> <label width="125px;">Currency:</label>
			 <input type="text" id="currency" class="text" name="currency" value="INR" />
		</li>

		<li class="clearfix"> <label width="125px;">First Name :</label>
			<input type="text" class="text" name="firstName" value="<%=requestData.get("firstName") %>" />
		</li>
		<li class="clearfix"> <label width="125px;">Last Name :</label>
			<input type="text" class="text" name="lastName" value="<%=requestData.get("lastName") %>" />
		</li>
		<li class="clearfix"> <label width="125px;">Email :</label>
			<input type="text" class="text" name="email" value="<%=requestData.get("email") %>" />
		</li>

		<p>Address Details</p>
		<li class="clearfix"> <label width="125px;">Address :</label>
			<input type="text" class="text" name="addressStreet1" value="<%=requestData.get("address") %>" />
		</p>

		<li class="clearfix"> <label width="125px;">City :</label>
			<input type="text" class="text" name="addressCity" value="<%=requestData.get("city") %>" />
		</li>
		<li class="clearfix"> <label width="125px;">Zip Code :</label>
			<input type="text" class="text" name="addressZip" value="<%=requestData.get("postalCode") %>" />
		</li>
		<li class="clearfix"> <label width="125px;">State :</label>
			<input type="text" class="text" name="addressState" value="<%=requestData.get("state") %>" />
		</li>
		
		<!-- <input type="hidden" class="text" name="addressCountry" value="INDIA" /> -->
		
		<li class="clearfix"> <label width="125px;">Phone No:</label>
			 <input type="text" class="text" name="phoneNumber" value="<%=requestData.get("contactNumber") %>" />
		</li>
		<!-- COD section starts here 
		Uncomment the below cod section if COD to be sent from merchant site
		pass the values as 'Yes' or 'No'
		-->
		
		<!-- <li class="clearfix"><label width="125px;">Is COD:</label> 
			<input type="text" class="text" name="COD" id="COD" value="" />
		</li> -->
		
		<!-- COD section END -->
		
		<!-- Custom parameter section starts here. 
		You can omit this section if no custom parameters have been defined.
		Hidden field value should be the name of the parameter created in Checkout settings page.
		It should follow customParams[0].name, customParams[1].name .. naming convention.
		For each custom parameter created, create a text field with the naming convention  
		customParams[0].value,customParams[1].value .. to accept user input.
		
		Please refer below code snippet for a custom parameter named Roll Number
		 -->

		<!-- <input type="hidden" name="customParams[0].name" value="Roll Number" />
		<p>
			Roll Number <input type="text" class="text" name="customParams[0].value" value="" />
		
		</p> -->
		
		<!-- custom parameter section END -->

		<!-- Payment details section
		     Please note payment mode is mandatory and any of the payment options must be there -->
		 
		<!-- Net banking section START
		     If payment mode is net banking pass payment mode as NET_BANKING and the bankName.
			 Please refer code snippet below for Net banking payment option.-->
		
		 <input type="hidden" name="paymentMode" value="NET_BANKING"/>
		 <input type="text" name="issuerCode" value="<%=requestData.get("citrusPayBankId") %>"/>  
		
		<!-- Net banking section END -->
		
		<!-- Credit Card section START
		    If payment mode is credit card then pass the following fields -
			1) name="paymentMode" value="CREDIT_CARD" 
			2) name="cardNumber" value="Card Number" 
			3) name="cardType" value="Card Type"
			4) name="cvvNumber" value="cvv Number"
			5) name="expiryMonth" value="Mth"
			6) name="expiryYear" value="YYYY" 
			Please refer the code snippet below for Credit Card Payment Mode -->
		
		<!-- <input type="hidden" name="paymentMode" value="CREDIT_CARD"/>
		<input type="hidden" name="cardHolderName" value="Mr Test"/>
		<input type="hidden" name="cardNumber" value="1111222233334444"/>
		<input type="hidden" name="cardType" value="VISA"/>
		<input type="hidden" name="cvvNumber" value="333"/>
		<!-- month should be in 1-12(1-January, 12-December) digits -->
		<!--<input type="hidden" name="expiryMonth" value="8"/>
		<input type="hidden" name="expiryYear" value="2014"/>   -->
		<!-- Credit Card section END -->


		<!--Debit Card section START
		    If payment mode is debit card then remove the comment from debit card section,
			pass the following field as :
			1) name="paymentMode" value="DEBIT_CARD"
			2) name="cardNumber" value="Card Number"
			3) name="cardType" value="Card Type"
			4) name="cvvNumber" value="cvv Number"
			5) name="expiryMonth" value="Mth"
			6) name="expiryYear" value="YYYY"
			Please refer the code snippet below for Debit Card Payment Mode -->
		
		<!-- <input type="hidden" name="paymentMode" value="DEBIT_CARD"/>
		<input type="hidden" name="cardHolderName" value="Mr Test"/>
		<input type="hidden" name="cardNumber" value="1111222233334444"/>
		<input type="hidden" name="cardType" value="VISA"/>
		<input type="hidden" name="cvvNumber" value="333"/>
		<!-- month should be in 1-12(1-January, 12-December) digits -->
		<!--<input type="hidden" name="expiryMonth" value="8"/>
		<input type="hidden" name="expiryYear" value="2014"/>  -->
		<!-- Debit Card section END -->
		
		<!-- Payment details section END -->
					
		<input
			type="hidden" name="returnUrl" value="http://localhost:8080/control/citruspayNotify" />
			
	<input
			type="hidden" id="secSignature" name="secSignature" value="" /> 
			
	<input type="hidden" name="reqtime" id="reqtime" value="<%=System.currentTimeMillis() %>" />

	<input type="Button" class="btn-orange" value="Make Payment"
			onClick="JavaScript:generateHMAC();"></input>
	</ul>
	<div>
 </form>
 
 </div>
		</div>
	</div>
	<div
		style="padding-left: 700px; padding-bottom: 20px; padding-top: 20px;">
		<div>Copyrights © 2012 Citrus.</div>
	</div>
</body>
	<script>
		//window.onload=function(){generateHMAC();};
	</script>
</html>