<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>

<head>
<meta HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
<title>Transaction</title>
</head>

<body bgcolor="">

	<br>
	<%@ page language="java" import="java.lang.*,com.citruspay.pg.*"
		session="false" isErrorPage="false"%>

	<%
		String key = "2ecf8f78598898bac18b2299905b1926f877eb87";
		com.citruspay.pg.util.CitruspayConstant.merchantKey = key;

		java.util.Map<String, Object> params = new java.util.HashMap<String, Object>();

		com.citruspay.pg.model.Card card = new com.citruspay.pg.model.Card();
	    card.setPaymentMode(com.citruspay.pg.util.PaymentMode.NET_BANKING
				.toString());  
		//for credit card
		/*card.setCardHolderName("Avinash");
		card.setCardNumber("4335900000120054");
		//VISA for Visa Card MCRD for Master Card and MTRO for Maestro Card
		card.setCardType("VISA");
		card.setCvvNumber("111");
		//month should be in 1-12(1-January, 12-December) digits
		card.setExpiryMonth("11");
		card.setExpiryYear("2015");
		card.setPaymentMode(com.citruspay.pg.util.PaymentMode.CREDIT_CARD.toString());
		
		//for debit card
		card.setCardHolderName("Avinash");
		card.setCardNumber("4335900000120054");
		//VISA for Visa Card MC for Master Card
		card.setCardType("VISA"); 
		card.setCvvNumber("111");
		//month should be in 1-12(1-January, 12-December) digits
		card.setExpiryMonth("11");
		card.setExpiryYear("2015"); 
		card.setPaymentMode(com.citruspay.pg.util.PaymentMode.DEBIT_CARD.toString()); */

		com.citruspay.pg.model.Address address = new com.citruspay.pg.model.Address();
		address.setAddressCity("PUNE");
		address.setAddressCountry("India");
		address.setAddressState("MH");
		address.setAddressStreet1("Test");
		//address.setAddressStreet2("Test");
		address.setAddressZip("411045");

		com.citruspay.pg.model.Customer cust = new com.citruspay.pg.model.Customer();
		cust.setEmail("test@test.com");
		cust.setFirstName("Test");
		cust.setLastName("Test");
		cust.setPhoneNumber("1234567890");

		params.put("merchantAccessKey", "I1MW7LO0YTE5ZIVWCW7E");
		params.put("bankName", "SBI BANK");
		/*params.put("issuerCode", "ISS003"); */
		params.put("transactionId", "11229900");
		params.put("amount", "1250");
		params.put("returnUrl", "http://127.0.0.1:8080/Kit-Test/jsp/TestMotoResponse.jsp");

		params.put("card", card);
		params.put("customer", cust);
		params.put("add", address);
		
		/* If any custom parameters has been defined, value can be passed by puting it in a map
		   where key for the map is the name of the custom parameter and value will be the actual
		   value.
		   params.put("custom param name", param value);
		*/
		 
		
		com.citruspay.pg.model.Transaction txn = com.citruspay.pg.model.Transaction
				.create(params);

		//resp code 200 OK
		if (txn.getRespCode().equalsIgnoreCase("200") &&  txn.getRedirectUrl() != null) {
			String strRedirectionURL = txn.getRedirectUrl();
			System.out.println("redirect url"+strRedirectionURL);
			response.sendRedirect(strRedirectionURL);
			return;
		} else if(txn.getRespCode().equalsIgnoreCase("400")|| txn.getRespCode().equalsIgnoreCase("401")){ //resp code 400 BAD Request
			System.out.println(txn.getRespMsg());
			response.sendRedirect("http://127.0.0.1:8080/Kit-Test/jsp/TestMotoResponse.jsp?TxMsg="+txn.getRespMsg()+"&TxStatus=FAIL" +"&TxId=" +txn.getTransactionId());
			return;
		}else{
			out.println("Error encountered");
			out.println("Error Code" +txn.getRespCode());
			out.println("Error Message"+txn.getRespMsg());
		}
	%>

</body>

</html>