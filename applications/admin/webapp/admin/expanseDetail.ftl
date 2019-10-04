
<script>
  	function submitPosTerminalForm(){
			document.posTerminalForm.submit();
	    }
</script>
<#if facilityId?has_content>
<#else>
	<#assign facilityId = "_NA_">
</#if>
<#if posTerminalId?has_content>
<#else>
	<#assign posTerminalId = "all">
</#if>

<#assign minDate = request.getParameter("startDate")?if_exists>
<#assign maxDate = request.getParameter("endDate")?if_exists>

          <form method="get" action="<@ofbizUrl>expanseDetail</@ofbizUrl>" name="posTerminalForm">
          
          	<table>
          	<!--tr>
					<td class="label">Start Date: </td>
					<td> 
						<input type="hidden" name="reportType" value="Daily Sales Report"/>
						<input type='text' size='25' class='inputBox' name='startDate' value='${minDate?if_exists}' />
							<a href="javascript:call_cal(document.posTerminalForm.startDate,'${minDate?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr>
				<tr>
					<td class="label">End Date: </td>
					<td>
						<input type='text' size='25' class='inputBox' name='endDate' value='${maxDate?if_exists}'/>
							<a href="javascript:call_cal(document.posTerminalForm.endDate,'${maxDate?if_exists}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'/></a>
					</td>
				</tr-->
          	<input type='hidden' size='25' class='inputBox' name='startDate' value='${minDate?if_exists}' />
          	<input type='hidden' size='25' class='inputBox' name='endDate' value='${maxDate?if_exists}'/>
	            <tr class="header-row">
	               <td>Facility</td>
	               <td>
		               <select name="facilityId" onChange="submitPosTerminalForm()" id="facilityId">
			               <#if facilityList?has_content>
			               		<option value='all' >--</option>
			               		<#list facilityList as facility>
		                   			<option  value="${facility.facilityId?if_exists}" <#if facility.facilityId == facilityId>selected</#if>>${facility.facilityName?if_exists}</option>
		                   		</#list>
		                   	</#if>
	                   </select>
                   </td>
	               <td>Pos Terminal</td>
	               <td>
	               <select name="posTerminalId" onChange="submitPosTerminalForm()">
					<#if posTerminalList?has_content>
					        <option value='all' >--</option>
							<option value='all' >All</option>
						<#list posTerminalList as posTerminal>
							<option value='${posTerminal.posTerminalId?if_exists}'
							<#if posTerminal.posTerminalId == posTerminalId> selected </#if>>${posTerminal.terminalName?if_exists}</option>         
						</#list>
		             <#else>  
	                   		<option value=''>No Terminal Exist</option>
	                 </#if>
	                </select>
                   </td>
	            </tr>
	           </table>
            </form>
            <br/>
             <div style="color:red">Total Expense : ${total?if_exists}</div>
            <br/>
            <#if expanseDetails?has_content>
            	<table width="100%" bgcolor="#000000" cellspacing="1">
	            <tr class="header-row" bgcolor="#FFFFFF">
	            	<td>Facility Id</td>
	            	<td>Name</td>
	            	<td>Log Id</td>
	            	<td>Amount</td>
	            	<td>Reason</td>
	            	<td>Description</td>
				</tr>
            	<#list expanseDetails as expanseDetail>
            		 <tr class="header-row" bgcolor="#FFFFFF">
            		 
            		<#assign facility = delegator.findOne("Facility",{"facilityId":expanseDetail.facilityId}, false)?if_exists>
            		<td>${facility.facilityName?if_exists}</td>
            		<td>${expanseDetail.terminalName?if_exists}</td>
            		<td>${expanseDetail.posTerminalLogId?if_exists}</td>
            		<td>${expanseDetail.paidAmount?if_exists}</td>
            		<td>${expanseDetail.description?if_exists}</td>
            		<td>${expanseDetail.reasonComment?if_exists}</td>
            	</tr>
            	</#list>
            </#if>
           
            
            
            
            
            