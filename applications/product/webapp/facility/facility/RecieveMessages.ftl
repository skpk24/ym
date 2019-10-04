<div class="screenlet-title-bar">
	    	<h3>
	    		Recieved Messages
		    </h3>
		</div>
<br>
<br>
<table  class="basic-table hover-bar">
     <tr class="header-row">
                <td>From Facility</td>
                <td>To Facility</td>
                <td>Action To Perform</td>
                <td>Past Action</td>
                <td>Comment</td>
                <td>Status</td>
            </tr>

	 <#if requestAttributes.messageList?exists && requestAttributes.messageList?has_content>
	 <#assign msgList=requestAttributes.messageList?if_exists>
	  <#list msgList as msg>
	    <tr>
	            <td>${msg.formFacility?if_exists}</td>
                <td>${msg.toFacility?if_exists}</td>
                <td>${msg.nextAction?if_exists}</td>
                <td>${msg.incommingAction?if_exists}</td>
                <td>${msg.comment?if_exists}</td>
                <td>${msg.status?if_exists}</td>
	    </tr>
	  </#list>
	 </#if>
	 
</form>
</table>