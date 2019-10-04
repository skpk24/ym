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
            
            <form method="get" action="<@ofbizUrl>SynchronizationPull</@ofbizUrl>" name="posTerminalForm">
                <input type="hidden"  name="syncType" value="PULL"/>
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
            	<td>Terminal Id</td>
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
            <form method="post" action=""  name="syncPullForm${count}" id="syncPullForm${count}">
            <#assign RecurrenceRuleForPULL = sync.get("RecurrenceRuleForPULL")?if_exists>
            <#assign RecurrenceRuleInfoRuleForPULL = sync.get("RecurrenceRuleInfoRuleForPULL")?if_exists>
            <#assign JobSandboxForPULL = sync.get("JobSandboxForPULL")?if_exists>
            <tr class="header-row">
               <td>
                   ${sync.posTerminalId?if_exists}
               </td>
               <td>
                   <input type="text"  name="posTerminalIp" value="${sync.posTerminalIp?if_exists}"/>
               </td>
               <td>
                  POSPULL
                   <input type="hidden"  name="facilityId" value="${facilityId?if_exists}"/>
                   <input type="hidden"  name="syncType" value="PULL"/>
                   <input type="hidden"  name="posTerminalId" value="${sync.posTerminalId?if_exists}"/>
               </td>
                <td>
                  <select name="frequency">
                    <option  value="MINUTELY" <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("MINUTELY")?has_content>selected</#if>>MINUTELY</option>
                    <option  value="HOURLY"  <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("HOURLY")?has_content>selected</#if>>HOURLY</option>
                    <option  value="DAILY"   <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("DAILY")?has_content>selected</#if>>DAILY</option>
                    <option  value="WEEKLY"  <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("WEEKLY")?has_content>selected</#if>>WEEKLY</option>
                    <option  value="MONTHLY" <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("MONTHLY")?has_content>selected</#if>>MONTHLY</option>
                    <option  value="YEARLY"  <#if RecurrenceRuleForPULL?has_content && RecurrenceRuleForPULL.frequency.equals("YEARLY")?has_content>selected</#if>>YEARLY</option>
                  </select>
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="Interval" value="<#if RecurrenceRuleForPULL?exists>${RecurrenceRuleForPULL.intervalNumber?if_exists}</#if>" />
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="countNumber" value="<#if RecurrenceRuleForPULL?exists>${RecurrenceRuleForPULL.countNumber?if_exists}</#if>"/>
                </td>
                 <td>
                     <#if JobSandboxForPULL?exists && JobSandboxForPULL?has_content> ${JobSandboxForPULL.statusId?if_exists}<#else>Not Started Yet</#if>
                </td>
                <td>
                	<a href="javascript: submitForm('pullSync','syncPullForm${count}');" class="buttontext" >Schedule</a> 
                </td>
                <td>
                	<a href="javascript: submitForm('stopPullJob','syncPullForm${count}');" class="buttontext" >Stop</a> 
                </td>
            </tr>
            </form>
            <#assign count = count+1>
            </#list>
            <#else>
	            <tr >
		            <td>
		            	No PosTerminal Found .
		            </td>
	            </tr>
            </#if>
       </form>
  </table>
 </div>