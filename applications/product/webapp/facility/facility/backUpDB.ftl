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
            
            <form method="get" action="<@ofbizUrl>backUpDB</@ofbizUrl>" name="posTerminalForm">
                <input type="hidden"  name="syncType" value="DUMP"/>
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
	               <#--td>Pos Terminal</td>
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
                   </td-->
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
            <form method="post" action=""  name="dbBackUpForm${count}" id="dbBackUpForm${count}">
            <#assign RecurrenceRuleForDUMP = sync.get("RecurrenceRuleForDUMP")?if_exists>
            <#assign RecurrenceRuleInfoRuleForDUMP = sync.get("RecurrenceRuleInfoRuleForDUMP")?if_exists>
            <#assign JobSandboxForDUMP = sync.get("JobSandboxForDUMP")?if_exists>
            <tr class="header-row">
               <td>
                   ${sync.posTerminalId?if_exists}
               </td>
               <td>
                   <input type="text"  name="posTerminalIp" value="${sync.posTerminalIp?if_exists}"/>
               </td>
               <td>
                  DB BACKUP
                  <input type="hidden"  name="facilityId" value="${facilityId?if_exists}"/>
                   <input type="hidden"  name="syncType" value="DUMP"/>
                   <input type="hidden"  name="posTerminalId" value="${sync.posTerminalId?if_exists}"/>
               </td>
                <td>
                  <select name="frequency">
                    <option  value="MINUTELY" <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("MINUTELY")?has_content>selected</#if>>MINUTELY</option>
                    <option  value="HOURLY"  <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("HOURLY")?has_content>selected</#if>>HOURLY</option>
                    <option  value="DAILY"   <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("DAILY")?has_content>selected</#if>>DAILY</option>
                    <option  value="WEEKLY"  <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("WEEKLY")?has_content>selected</#if>>WEEKLY</option>
                    <option  value="MONTHLY" <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("MONTHLY")?has_content>selected</#if>>MONTHLY</option>
                    <option  value="YEARLY"  <#if RecurrenceRuleForDUMP?has_content && RecurrenceRuleForDUMP.frequency.equals("YEARLY")?has_content>selected</#if>>YEARLY</option>
                  </select>
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="Interval" value="<#if RecurrenceRuleForDUMP?exists>${RecurrenceRuleForDUMP.intervalNumber?if_exists}</#if>" />
                </td>
                <td>
                    <input type="text" style="background-color:yellow;" name="countNumber" value="<#if RecurrenceRuleForDUMP?exists>${RecurrenceRuleForDUMP.countNumber?if_exists}</#if>"/>
                </td>
                 <td>
                     <#if JobSandboxForDUMP?exists && JobSandboxForDUMP?has_content> ${JobSandboxForDUMP.statusId?if_exists}<#else>Not Started Yet</#if>
                </td>
               <td>
                	<a href="javascript: submitForm('backUpDBDump','dbBackUpForm${count}');" class="buttontext" >Schedule</a> 
                </td>
                <td>
                	<a href="javascript: submitForm('stopDumpJob','dbBackUpForm${count}');" class="buttontext" >Stop</a> 
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