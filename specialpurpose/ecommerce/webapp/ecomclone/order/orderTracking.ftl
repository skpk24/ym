
<form name="myform" action="<@ofbizUrl>orderTracker</@ofbizUrl>" method="post">
	 <table cellspacing="0" align="center" width="100%" style="margin:0 auto; width:277px; padding-bottom:20px;">
	     <tr><td colspan="3" style="text-align:center"><h3>Track Your Order</h3> </td></tr>
		 <tr><td colspan="3">&nbsp;</td></tr>
		 <tr>
		    <td class="label" style="vertical-align:middle !important;"><span style="font-weight:normal; font-size:14px !important;">Order Id*</span></td>
			<td><input type="text" name="orderId" id="orderId"></td>
			<td><input type="submit" value="Track" onclick="return addInput()"></td>
		 </tr>
		 <tr>
		    <td></td>
			<td><font color="red"><div id="errorMsg"></div></font></td>
			<td></td>
		 </tr>
	 </table>   
</form>
   <table cellspacing="0" class="basic-table" align="center" width="100%" >
   
    <#if requestAttributes.massage?has_content>
   		<tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> ${requestAttributes.massage?if_exists}</b></td></tr>
   </#if>
   
   	<#if requestAttributes.statusId?has_content && requestAttributes.statusId == "ORDER_CREATED">
  
   		<tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> Your Order <strong>${(requestAttributes.orderId)?if_exists} </strong> is created . No Amount received to proceed .</b></td></tr>
   </#if>
    <#if requestAttributes.statusId?has_content && requestAttributes.statusId == "ORDER_APPROVED">
  
   		<tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> Your Order <strong>${(requestAttributes.orderId)?if_exists} </strong> is Confirmed & will be delivered on ${(requestAttributes.deliveryDate)?if_exists} between ${(requestAttributes.slotTiming)?if_exists}</b></td></tr>
   </#if>
   <#if requestAttributes.statusId?has_content && requestAttributes.statusId == "ORDER_CANCELLED">
  
   		<tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> Your Order <strong>${(requestAttributes.orderId)?if_exists} </strong> is Cancelled</b></td></tr>
   </#if>
   <#if requestAttributes.statusId?has_content && requestAttributes.statusId == "ORDER_DISPATCHED">
  
   <tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> Your Order <strong>(${requestAttributes.orderId?if_exists}) </strong> is in transit & will be delivered on ${(requestAttributes.deliveryDate)?if_exists} between ${(requestAttributes.slotTiming)?if_exists}</b></td></tr>
   </#if>
   <#if requestAttributes.statusId?has_content && requestAttributes.statusId == "ORDER_COMPLETED">
  
   <tr>
   <#assign deliveredDate = requestAttributes.fullFillDate?if_exists>
   <#assign deliveryDate = deliveredDate?string("dd-MM-yyyy") >
   <td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"> Your Order <strong>(${requestAttributes.orderId?if_exists}) </strong> was Fulfilled & delivered on ${deliveryDate?if_exists} between ${(requestAttributes.slotTiming)?if_exists}</b></td></tr>
   </#if>
      
      <script type="text/javascript" language="JavaScript">
       function addInput() 
             {
            
              if(document.getElementById("orderId").value=="" ||document.getElementById("orderId").value==null)
              {
              document.getElementById("errorMsg").innerHTML = "Please Enter OrderId";
              return false;
              }
             }    
      </script>
    </table>