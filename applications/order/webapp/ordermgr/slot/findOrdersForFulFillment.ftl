<font color="red"><div id="commentCheckedError"></div></font>

<form action="OrderFulFilled" name="orderFulFill"  method="post">
<input type="hidden" name="vehicleNumber" value="${parameters.vehicleNumber?if_exists}"/>
<input type="hidden" name="slot" value="${parameters.slot?if_exists}"/>

<table>
<#assign orderIds = "">
<#if listOfData?has_content>
<tr>
	<td></td>
	<td>
		<b>Order Id</b>
	</td>
	<td>
		<b>Order Amount</b>
	</td>
	<td>
		<b>Payment Mode</b>
	</td>
	<td>
		<b>${uiLabelMap.OrderReceiveOfflinePayments}</b>
	</td>
	<td>
		<b>Happy Code</b>
	</td>
	<td>
		<b>Amount To Receive</b>
	</td>
	<td>
		<b>Amount Received</b>
	</td>
	<td>
		<b>Adjustment</b>
	</td>
	<td>
		<b>Comments</b>
	</td>
</tr>
	<#assign amtToReceive = 0>
	<#list listOfData as data>
		<#assign orderIds = orderIds+","+data.orderId>
		
		<#if data.amtToRecive?has_content>
			<#assign amtToReceive = amtToReceive+data.amtToRecive>
		</#if>

		<#assign isCOD = false>
		<#if data.isCOD?has_content>
			<#assign isCOD = data.isCOD>
		</#if>
		<#assign count=1>
		<#if isCOD>
			<#assign count = 3>
		</#if>
		<input type="hidden" name="noOfPayment" value="${count}" id="${data.orderId?if_exists}noOfPayment"/>
		<#list 1..count as i>
		<tr id="${data.orderId?if_exists}style">
				<#if i == 1>
					<td>
						<input type="checkbox" name="orderId" value="${data.orderId?if_exists}" id="${data.orderId?if_exists}" onclick="return false"/>
					</td>
					<td>
						${data.orderId?if_exists}
						<input type="hidden" value="${data.shipmentId?if_exists}" name="${data.orderId?if_exists}shipmentId" id="${data.orderId?if_exists}shipmentId"/>
						<input type="hidden" value="${data.deliveryBoyId?if_exists}" name="${data.orderId?if_exists}deliveryBoyId" id="${data.orderId?if_exists}deliveryBoyId"/>
						<input type="hidden" value="${data.vehicleNumber?if_exists}" name="${data.orderId?if_exists}vehicleNumber" id="${data.orderId?if_exists}vehicleNumber"/>
					</td>
					<td>
						${data.grandTotal?if_exists}
					</td>
					<td>
						${data.paymentMethod?if_exists}
					</td>
					<td>
					<#if isCOD>
						<#if paymentMethodTypes?has_content>
							<select name="${data.orderId?if_exists}paymentMethodTypeId${i}" disabled="disabled" id="${data.orderId?if_exists}paymentMethodTypeId${i}" onChange="checkPaymentToReceive('${data.orderId?if_exists}')">
								<#list paymentMethodTypes as payType>
									<option value="${payType.paymentMethodTypeId}">${payType.get("description",locale)?default(payType.paymentMethodTypeId)}</option>
						        </#list>
						    </select>
						    <a href="javascript:addmore('${data.orderId?if_exists}',1);"></a><br/>
					    </#if>
					 <#else>
					 	N/A<input type="hidden" value="EXT_ONLINE" name="${data.orderId?if_exists}paymentMethodTypeId${i}" id="${data.orderId?if_exists}paymentMethodTypeId${i}" readonly="readonly" size="6"/>
					 </#if>
					</td>
					<td>
						<input type="text" name="${data.orderId?if_exists}pinCode" value="" id="${data.orderId?if_exists}pinCode" readonly="readonly" size="6"/>
					</td>
					<td>
						<input type="text" name="${data.orderId?if_exists}amtToRecv" value="${data.amtToRecive?if_exists}" 
												id="${data.orderId?if_exists}amtToRecv" readonly="readonly" size="10" />
					</td>
					<td>
						<input type="text" name="${data.orderId?if_exists}amtRecv${i}" id="${data.orderId?if_exists}amtRecv${i}"  size="10" readonly="readonly"
						<#if isCOD>
							value="${data.amtToRecive?if_exists}"
						<#else>
							value="0.00"
						</#if>
						onkeypress="return isNumberKey(event,'${data.orderId?if_exists}','${i}')" 
						onblur="adjustPayments('${data.orderId?if_exists}')",onmouseout="adjustPayments('${data.orderId?if_exists}')"
						/>
					</td>
					<td>
						<input type="text" name="${data.orderId?if_exists}adjustment" value="0.00" id="${data.orderId?if_exists}adjustment"  size="10" readonly="readonly"/>
					</td>
					<td>
						<textarea name="${data.orderId?if_exists}comment" readonly="readonly" value="" id="${data.orderId?if_exists}comment" onmouseout="clearError('${data.orderId?if_exists}comment','${data.orderId?if_exists}commentError')">
						</textarea>
						<font color="red"><div id="${data.orderId?if_exists}commentError"></div></font>
					</td>
			</tr>
			<#else>
			<tr>
				<td colspan="4"></td>
				<td>
					<#if isCOD>
						<#if paymentMethodTypes?has_content>
							<select name="${data.orderId?if_exists}paymentMethodTypeId${i}" disabled="disabled" id="${data.orderId?if_exists}paymentMethodTypeId${i}" onChange="checkPaymentToReceive('${data.orderId?if_exists}')">
								<#list paymentMethodTypes as payType>
									<option value="${payType.paymentMethodTypeId}"
									<#if "VOUCHER" == payType.paymentMethodTypeId>selected</#if>
									>${payType.get("description",locale)?default(payType.paymentMethodTypeId)}</option>
						        </#list>
						    </select>
						    <a href="javascript:addmore('${data.orderId?if_exists}',1);"></a><br/>
					    </#if>
					 </#if>
				</td>
				<td colspan="2"></td>
				<td>
					<input type="text" name="${data.orderId?if_exists}amtRecv${i}" id="${data.orderId?if_exists}amtRecv${i}"  size="10" readonly="readonly"
					value=""
					onkeypress="return isNumberKey(event,'${data.orderId?if_exists}','${i}')" 
					onblur="adjustPayments('${data.orderId?if_exists}')",onmouseout="adjustPayments('${data.orderId?if_exists}')"
					/>
				</td>
				<td colspan="2"></td>
			</tr>
			</#if>
		</#list>
		<tr><td colspan="10"><hr /></td></tr>
	</#list>
		<tr>
			<td colspan="6" align="right"><b>Total<b></td>
			<td><input type="text" value="${amtToReceive?default(0.00)}" id="amountToReceive"  size="10" readonly="readonly"/></td>
			<td><input type="text" value="${amtToReceive?default(0.00)}" id="amtReceived"  size="10" readonly="readonly"/></td>
			<td><input type="text" value="0.00" id="delivoryBoyAdjustment"  size="10" readonly="readonly"/></td>
		</tr>
		<tr>
			<td colspan="7" align="right"><b>Cash<b></td>
			<td><input type="text" value="0.00" id="totalCashReceived" size="10" readonly="readonly"/></td>
		</tr>
		<tr>
			<td colspan="7" align="right"><b>Voucher<b></td>
			<td><input type="text" value="0.00" id="totalVoucherReceived" size="10" readonly="readonly"/></td>
		</tr>
		<tr>
			<td colspan="7" align="right"><b>Debit / Credit Card<b></td>
			<td><input type="text" value="0.00" id="totalDebitCreditCardReceived" size="10" readonly="readonly"/></td>
		</tr>
		<tr>
			<#--td colspan="2" align="right">
				<b>Payment Details</b><br />
				<#if paymentDetails?has_content>
				<#assign keys = paymentDetails.entrySet()>
					<#list keys as key>
						${key} <br />
					</#list> 
				</#if>
				Total Amount : ${totalAmt?if_exists}
			</td-->
			<td colspan="3" align="right">
				<div id="paymentDetails"></div>
				<input type="hidden" value="${orderIds?if_exists}" id="orderIds"/>
				<#--a href="javascript:calculatePaymentDetails()">Verify Payment Details</a-->
				<#--input type="submit" value="submit" id="submit"/-->
				<a href="#" onclick="return calculatePaymentDetails()" class="smallSubmit">Submit</a>
				<font color="red"><div id="commonError"></div></font>
			</td>
		</tr>
<#else>
	<tr>
		<td colspan="6" align="center">
			No Records Found .......
		</td>
	</tr>
</#if>
</table>
</form>
<script>
function isNumberKey(evt,orderId,index) {
	var charCode = (evt.which) ? evt.which : evt.keyCode;
	// Added to allow decimal, period, or delete
	var amtRecv = document.getElementById(orderId+"amtRecv"+index).value;
	if(amtRecv != null && amtRecv.trim() != "" && charCode == 46)
	{
		var amountRec = amtRecv.split(".");
		if(amountRec.length >= 2)
			return false;
	}
	if (charCode == 110 || charCode == 190 || charCode == 46) 
		return true;
	
	if (charCode > 31 && (charCode < 48 || charCode > 57)) 
		return false;
	return true;
}
      
function checkPaymentToReceive(orderId){
	var paymentMethodTypeId = document.getElementById(orderId+"paymentMethodTypeId1").value;
	<#--if(paymentMethodTypeId != null && paymentMethodTypeId.trim() == "DEBIT_CREDIT_CARD")
	{
		document.getElementById(orderId+"amtRecv1").value = document.getElementById(orderId+"amtToRecv").value;
		document.getElementById(orderId+"adjustment").value = "0.00";
		document.getElementById(orderId+"commentError").innerHTML  = "";
		document.getElementById(orderId+"amtRecv1").readOnly = true;
	}
	else -->
		document.getElementById(orderId+"amtRecv1").readOnly = false;
		
	calculateAllPayments();
}

function clearError(valueId,id){
	var value = document.getElementById(valueId).value;
	if(value != null && value.trim() != "")
		document.getElementById(id).innerHTML = "";
}
function addCommentError(valueId,id){
	var value = document.getElementById(valueId).value;
	if(value == null || value.trim() == "")
		document.getElementById(id).innerHTML = "Please add comment";
}

function adjustPayments(orderId){
	if(document.getElementById(orderId).checked)
	{
	
		var noOfPayment = document.getElementById(orderId+"noOfPayment").value;
		
		var totalAmtRecv = parseFloat("0.00");
		var amtToRecv = document.getElementById(orderId+"amtToRecv").value;
		
		if(amtToRecv != null && amtToRecv.trim() == "")amtToRecv = "0.00";
		var amountToRec = parseFloat(amtToRecv);
		
		var isOnlinePayment = false;
		
		for(var i=1;i<= parseInt(noOfPayment);i++)
		{
			var paymentMethodTypeId = document.getElementById(orderId+"paymentMethodTypeId"+i).value;
			//if(paymentMethodTypeId != null && paymentMethodTypeId.trim() != "" && paymentMethodTypeId.trim() != "DEBIT_CREDIT_CARD")
			if(paymentMethodTypeId != null && paymentMethodTypeId.trim() != "")
			{
				if("EXT_ONLINE" == paymentMethodTypeId)
				{
					isOnlinePayment = true;
				}
				var amtRecv = document.getElementById(orderId+"amtRecv"+i).value;
				if(amtRecv != null && amtRecv.trim() == "")amtRecv = "0.00";
				
				totalAmtRecv = totalAmtRecv + parseFloat(amtRecv);
			}
		}
		if((totalAmtRecv != amountToRec) && !isOnlinePayment)
			{
				var adjustment = totalAmtRecv - amountToRec;
				adjustment = adjustment.toFixed(2);
				document.getElementById(orderId+"adjustment").value = adjustment;
				
				addCommentError(orderId+"comment",orderId+"commentError");
			}else if(totalAmtRecv == amountToRec){
				document.getElementById(orderId+"adjustment").value = "0.00";
				document.getElementById(orderId+"commentError").innerHTML = "";
			}
		calculateAllPayments();
	}
}

function calculateAllPayments(){
	var orderIds = document.getElementById("orderIds").value;
	var orderIdsArr = orderIds.split(",");
	
	var totalAmountToReceive = parseFloat("0.00");
	var totalAmountRec = parseFloat("0.00");
	var totalAdjustment = parseFloat("0.00");
	
	var totalCashReceived = parseFloat("0.00");
	var totalVoucherReceived = parseFloat("0.00");
	var totalDebitCreditCardReceived = parseFloat("0.00");
	for(var i = 0;i<orderIdsArr.length;i++)
	{
		var orderId = orderIdsArr[i].trim();
		if("" != orderId)
		{
			if(document.getElementById(orderId).checked)
			{
				var noOfPayment = document.getElementById(orderId+"noOfPayment").value;
				var adjustment = document.getElementById(orderId+"adjustment").value;
				if(adjustment != null && adjustment.trim() == "")adjustment = "0.00";
				
				var isOnlinePayment = false;
				for(var j=1;j<= parseInt(noOfPayment);j++)
				{
					var amtRecv = document.getElementById(orderId+"amtRecv"+j).value;
					if(amtRecv != null && amtRecv.trim() == "")amtRecv = "0.00";
					totalAmountRec = totalAmountRec + parseFloat(amtRecv);
					
					var paymentMethodTypeId = document.getElementById(orderId+"paymentMethodTypeId"+j).value;
					if(paymentMethodTypeId != null && paymentMethodTypeId.trim() != null)
					{
						if(paymentMethodTypeId == "CASH")
							totalCashReceived = totalCashReceived + parseFloat(amtRecv);
						else if(paymentMethodTypeId == "VOUCHER")
							totalVoucherReceived = totalVoucherReceived + parseFloat(amtRecv);
						else if(paymentMethodTypeId == "DEBIT_CREDIT_CARD")
							totalDebitCreditCardReceived = totalDebitCreditCardReceived + parseFloat(amtRecv);
						
						if("EXT_ONLINE" == paymentMethodTypeId)
						{
							isOnlinePayment = true;
						}
					}
				}
				if(!isOnlinePayment)
					{
						var amtToRecv = document.getElementById(orderId+"amtToRecv").value;
						if(amtToRecv == null || amtToRecv.trim() == "") amtToRecv = "0.00";
						totalAmountToReceive = totalAmountToReceive + parseFloat(amtToRecv);
					}
				totalAdjustment = totalAdjustment + parseFloat(adjustment);
			}
		}
	}
	document.getElementById("amountToReceive").value = totalAmountToReceive;
	document.getElementById("amtReceived").value = totalAmountRec;
	document.getElementById("delivoryBoyAdjustment").value = totalAdjustment;
	document.getElementById("totalCashReceived").value = totalCashReceived;
	document.getElementById("totalVoucherReceived").value = totalVoucherReceived;
	document.getElementById("totalDebitCreditCardReceived").value = totalDebitCreditCardReceived;
}


function validate(){
	var orderIds = document.getElementById("orderIds").value;
	var orderIdsArr = orderIds.split(",");
	var flag = true;
	var checkedflag = false;
	for(var i = 0;i<orderIdsArr.length;i++)
	{
		var orderId = orderIdsArr[i].trim();
		if("" != orderId)
		{
			if(document.getElementById(orderId).checked)
			{
				checkedflag = true;
				var adj = document.getElementById(orderId+"adjustment").value;
				if("" != adj.trim() && adj.trim() != "0.00")
				{
					var comment = document.getElementById(orderId+"comment").value;
					if("" == comment.trim())
					{
						document.getElementById(orderId+"commentError").innerHTML = "Please add comment";
						flag = false;
					}
				}
			}
		}
	}
	if(checkedflag == false)
	{
		document.getElementById("commentCheckedError").innerHTML = "Please select a order ";
		flag = false;
	}
		
	return flag;
}
function  calculatePaymentDetails(){
	var flag = validate();
	if(flag == true)
	{
		var totalCashReceived = document.getElementById("totalCashReceived").value ;
		var totalVoucherReceived = document.getElementById("totalVoucherReceived").value ;
		var totalDebitCreditCardReceived = document.getElementById("totalDebitCreditCardReceived").value ;
		var amountToReceive = document.getElementById("amountToReceive").value ;
		var amtReceived = document.getElementById("amtReceived").value ;
		var delivoryBoyAdjustment = document.getElementById("delivoryBoyAdjustment").value ;
	
		var paymentDetail = "";
		paymentDetail = paymentDetail+"                            Cash : "+totalCashReceived+"\n ";
		paymentDetail = paymentDetail+"                         Voucher : "+totalVoucherReceived+"\n ";
		paymentDetail = paymentDetail+"             Debit / Credit Card : "+totalDebitCreditCardReceived+"\n ";
		paymentDetail = paymentDetail+"------------------------------------\n";
		paymentDetail = paymentDetail+"Total Amount To Be Received : "+amountToReceive+"\n ";
		paymentDetail = paymentDetail+"  Total Amount Received : "+amtReceived+"\n ";
		paymentDetail = paymentDetail+"------------------------------------\n";
		paymentDetail = paymentDetail+"       Total Adjustment : "+delivoryBoyAdjustment+"\n ";
		var r=confirm(paymentDetail);
		if (r==true)
		  {
		       	document.orderFulFill.submit();
		  }
		else
		  {
		     return false;
		  }
	  }
	  return false;
}

</script>
<script type='text/javascript'> 
window.onload = function(){
	var orderIds = document.getElementById("orderIds").value;
	var orderIdsArr = orderIds.split(",");
	for(var i = 0;i<orderIdsArr.length;i++)
	{
		var orderId = orderIdsArr[i].trim();
		if("" != orderId)
		{
			document.getElementById(orderId+"comment").value = "";
		}
	}	
};
</script>
