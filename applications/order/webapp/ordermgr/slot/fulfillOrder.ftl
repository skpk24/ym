<form action="<@ofbizUrl>fulfillOrder</@ofbizUrl>" method="get">

Vehicle Number:<select name="vehicleNumber">
					<option value=""> All </option>
                	<#if assignedVehicleToDeliveryBoy?has_content>
                		<#list assignedVehicleToDeliveryBoy as assigVehicleToDeliveryBoy>
                			<option value="${assigVehicleToDeliveryBoy.vehicleNumber?if_exists}"
                			<#if parameters.vehicleNumber?has_content && assigVehicleToDeliveryBoy.vehicleNumber?has_content && parameters.vehicleNumber == assigVehicleToDeliveryBoy.vehicleNumber>
                				selected = "selected"
                			</#if>>
                			${assigVehicleToDeliveryBoy.vehicleNumber?if_exists} :- ${assigVehicleToDeliveryBoy.deliveryBoyName?if_exists}</option>
                		</#list>
                	</#if>
                </select>

Slot :<select name="slot">
                	<#if slotTypes?has_content>
                		<option value="">All</option>
                		<#list slotTypes as slotType>
                			<option value="${slotType.slotType?if_exists}"
                			<#if parameters.slot?has_content && slotType.slotType?has_content && parameters.slot == slotType.slotType>
                				selected = "selected"
                			</#if>
                			>${slotType.slotTiming?if_exists}</option>
                		</#list>
                	</#if>
                </select>
      <input type="submit" value="Find">
</form>

<form action="#" onSubmit="return checkOrder()">
	Happy Code : <input type="text" id="happyCode"><input type="submit" value="submit" style="display:none">
</form>
<script>
	function checkOrder(){
		var happyCode = document.getElementById("happyCode").value;
		happyCode = happyCode.trim();
		var  param = 'happyCode=' + happyCode;
		jQuery.ajax({url: '/ordermgr/control/getOrderIdFromHappyCode',
		         data: param,
		         type: 'post',
		         async: false,
		         success: function(data) {
	         		if(data == "blankHappyCode")
	         		{	
	         			alert("Please Enter Happy Code");
	         		}
	         		else if(data == "orderNotFound")
	         		{
	         			alert("Order Not Found For happy code : "+happyCode);
	         		}
	         		else
         			{
         				document.getElementById(data).checked = true;
						document.getElementById("happyCode").value = "";
						document.getElementById(data+"pinCode").value = happyCode;
						document.getElementById("commentCheckedError").innerHTML = "";
						document.getElementById(data+"style").className = "orderSelectedForPayment";
						
						var noOfPayment = document.getElementById(data+"noOfPayment").value;
						for(var i=1;i<= parseInt(noOfPayment);i++)
						{
							document.getElementById(data+"paymentMethodTypeId"+i).disabled = false;
							var paymentMethodTypeId = document.getElementById(data+"paymentMethodTypeId"+i).value;
							if("N/A" == paymentMethodTypeId || "EXT_ONLINE" == paymentMethodTypeId)
								document.getElementById(data+"amtRecv"+i).readOnly = true;
							else
								document.getElementById(data+"amtRecv"+i).readOnly = false;
						}
						document.getElementById(data+"comment").readOnly = false;
						
						adjustPayments(data);
         			}
		         },
		         complete:  function() { 
		         },
		        error: function(data) {
		        }
    		});
		return false;
	}
</script>

<style> 
  .orderSelectedForPayment{background-color: #99CCCC;} 
</style>
