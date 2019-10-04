
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
            
            <#list custRequest as customerRequest>
				<tr>
				<td width="10%">${customerRequest.priority?if_exists}</td>
				<td>&nbsp;</td>
				<td width="10%">${customerRequest.custRequestId?if_exists}</td>
				<td>&nbsp;</td>
            	<td width="10%">${customerRequest.custRequestName?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${customerRequest.statusId?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${customerRequest.custRequestTypeId?if_exists}</td>
            	<td>&nbsp;</td>
            	<td width="10%">${customerRequest.reason?if_exists}</td>
            	 </tr>
            </#list>
           
	    </table>
