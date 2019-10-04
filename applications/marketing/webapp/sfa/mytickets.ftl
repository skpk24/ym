
<table class="basic-table hover-bar" cellspacing='0'>
          <tr class="header-row">
            <td width="15%">Priority</td>
       		<td>&nbsp;</td>
       		<td width="10%">Ticket Id</td>
            <td>&nbsp;</td>
            <td width="10%">Subject</td>
            <td>&nbsp;</td>
            <td width="10%">Status</td>
            <td>&nbsp;</td>
            <td width="10%">Type</td>
            <td>&nbsp;</td>
            <td width="10%">Reason</td>
         </tr>
            
            <#list myTeamTickets as myTickets>
				<tr>
				<td width="10%">${myTickets.priority?if_exists}</td>
				<td>&nbsp;</td>
				<td width="10%">${myTickets.custRequestId?if_exists}</td>
				<td>&nbsp;</td>
            	<td width="10%">${myTickets.custRequestName?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${myTickets.statusId?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${myTickets.custRequestTypeId?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${myTickets.reason?if_exists}</td>
            	 </tr>
            </#list>
           
	    </table>
