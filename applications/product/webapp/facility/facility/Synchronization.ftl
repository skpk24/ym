<script>
  	function submitPosTerminalForm(){
			document.posTerminalForm.submit();
	    }
</script>
<script>
  	function submitForm(action,formId){
		    document.getElementById(formId).action = action;
		    document.getElementById(formId).submit();
	    }
</script>
<#if facilityId?has_content>
<#else>
	<#assign facilityId = "_NA_">
</#if>
<div id="syncDiv">
<table cellspacing="0" class="basic-table">
            <form method="get" action="<@ofbizUrl>Synchronization</@ofbizUrl>" name="posTerminalForm">
                <input type="hidden"  name="syncType" value="PUSH"/>
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
            </form>
            
            <tr class="header-row">
            	<td>Terminal ID</td>
            	<td>Terminal IP</td>
                <td>Sync Id</td>
                <td>Frequency</td>
                <td>Interval</td>
                <td>Number Of Times</td>
                <td>Previous Status</td>
                <td>Action</td>
                <td></td>
            </tr>
            <#if syncList?has_content>
            <#assign count = 1>
            <#list syncList as sync>
            <form method="post" action=""  name="syncForm${count}" id="syncForm${count}">
            <#assign RecurrenceRuleForPUSH = sync.get("RecurrenceRuleForPUSH")?if_exists>
            <#assign RecurrenceRuleInfoRuleForPUSH = sync.get("RecurrenceRuleInfoRuleForPUSH")?if_exists>
            <#assign JobSandboxForPUSH = sync.get("JobSandboxForPUSH")?if_exists>
            <tr class="header-row">
               <td>
                   ${sync.posTerminalId?if_exists}
               </td>
               <td>
                   <input type="text"  name="posTerminalIp" value="${sync.posTerminalIp?if_exists}"/>
               </td>
               <td>
                  POSPUSH
                  <input type="hidden"  name="facilityId" value="${facilityId?if_exists}"/>
                   <input type="hidden"  name="syncType" value="PUSH"/>
                   <input type="hidden"  name="posTerminalId" value="${sync.posTerminalId?if_exists}"/>
               </td>
                <td>
                  <select name="frequency">
                    <option  value="MINUTELY" <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("MINUTELY")?has_content>selected</#if>>MINUTELY</option>
                    <option  value="HOURLY"  <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("HOURLY")?has_content>selected</#if>>HOURLY</option>
                    <option  value="DAILY"   <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("DAILY")?has_content>selected</#if>>DAILY</option>
                    <option  value="WEEKLY"  <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("WEEKLY")?has_content>selected</#if>>WEEKLY</option>
                    <option  value="MONTHLY" <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("MONTHLY")?has_content>selected</#if>>MONTHLY</option>
                    <option  value="YEARLY"  <#if RecurrenceRuleForPUSH?has_content && RecurrenceRuleForPUSH.frequency.equals("YEARLY")?has_content>selected</#if>>YEARLY</option>
                  </select>
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="Interval" value="<#if RecurrenceRuleForPUSH?exists>${RecurrenceRuleForPUSH.intervalNumber?if_exists}</#if>" />
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="countNumber" value="<#if RecurrenceRuleForPUSH?exists>${RecurrenceRuleForPUSH.countNumber?if_exists}</#if>"/>
                </td>
                 <td>
                     <#if JobSandboxForPUSH?exists && JobSandboxForPUSH?has_content> ${JobSandboxForPUSH.statusId?if_exists}<#else>Not Started Yet</#if>
                </td>
                 <td>
                	<a href="javascript: submitForm('sync','syncForm${count}');" class="buttontext" >Schedule</a> 
                </td>
                <td>
                	<a href="javascript: submitForm('stopPushJob','syncForm${count}');" class="buttontext" >Stop</a> 
                </td>
            </tr>
            </form>
            <#assign count = count+1>
            </#list>
            <#else>
	            <tr >
		            <td>
		            	No Sync Found .
		            </td>
	            </tr>
            </#if>
  </table>
 </div>