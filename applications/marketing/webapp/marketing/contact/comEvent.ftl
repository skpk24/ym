<script src="/images/prototypejs/prototype.js" language="JavaScript" 
type="text/javascript"></script>
<script type="text/javascript" language="JavaScript">
 var url = 'ContactSelection';
     function validate() {
       place =document.myform.contactListId.options [document.myform.contactListId.selectedIndex].value;
       if(place!="0")
       {
	
       var ajax = new Ajax.Updater({success: 'compaign'} ,url,{method: 'get', parameters:{place:place}, onFailure: reportError} );
}

     }
     
     function validateForm()
     {
     place =document.myform.contactListId.options [document.myform.contactListId.selectedIndex].value;
       if(place == "0")
       {
       alert("Please Select The Contact List")
        return false;
       }
     }
    function reportError()
     {
         $F('showResult')='Error';
     }
     
</script>

<body>
<#if communicationEventId?has_content>

<b>The Communication Events Id:${parameters.communicationEventId}</b><br><br>
</#if>
<#if communicationEventId?has_content>
 

<#assign actionurl = "updateContactListCommEventEmail"/>
<#else>
<#assign actionurl = "createContactListCommEventEmail"/>
</#if>
<form name="myform" action="<@ofbizUrl>${actionurl}</@ofbizUrl>" method="post"  onSubmit='return validateForm();'>
			<#if !communicationEventId?has_content>
			
			<table cellspacing="0" class="basic-table">
			<tr>
				<td>
				Contact List:</td><td><select name="contactListId" onchange=javascript:validate() >
				<option value="0" selected>(please select:)</option>
			 	<#list ItemTypes as ItemTypes>
				<option value="${ItemTypes.contactListId}">${ItemTypes.contactListName}</option>
			    </#list>
			    </select>
				</td>
			</tr> 
			<tr>
				<td>
				CamPaign Name:</td><td><div id='compaign' onclick=javascript:validate1()>
				<input type="text" name='marketingCampaignId'/></td>
			<tr><td>
			 </div></tr></td><br><br>
			  <tr><td>Template List:</td><td><select name="templateId">
			      <option value="0" selected>(please select:)</option>
			 <#list ItemTypesTem as ItemTypesTem>
			<option value="${ItemTypesTem.templateId}">${ItemTypesTem.templateName}</option>
			              </#list>
			            </select>
			</td></tr> 
			     
			     <tr><td><input type="hidden" name="statusId" value="COM_IN_PROGRESS"/></td></tr>
			      <tr><td>Subject:</td><td><input type="text" name="subject" size="30"/></td></tr>
			      <tr><td>Content:</td><td><textarea cols="40" rows="5" name="content"></textarea></td></tr>
			      <tr><td><input type="hidden" name="communicationEventTypeId" value="EMAIL_COMMUNICATION"/>
			     
			<input type="hidden" value="text/html" name="contentMimeTypeId"/>
			<input type="hidden" name="partyIdFrom" value="admin"/>
			<input type="hidden" name="contactMechIdFrom" value="admin"/>
			<tr><td><input type="submit" value='Save' onsubmit="return validateMyForm();"></td></tr>
			<#else>
			 <tr><td><b>Contact List</b></td><td><input type="text" name="contactListId" size="30" value="${contactListId}"/></td></tr><br><br>
			 <#if templateId?has_content>
			<tr><td><b>Template</b></td><td><input type="text" name="templateId" size="30" value="${(templateId)?if_exists}"/></td></tr><br><br>
			</#if>
			      <tr><td><b>Subject:</b></td><td><input type="text" name="subject" size="30" value="${subject}"/></td></tr><br><br>
			      <tr><td> <b>Content:</b></td><td><textarea cols="40" rows="5" name="content">${(tempContent)?if_exists}</textarea></td></tr><br><br>
			      
			      <input type="hidden" id="communicationEventId" name="communicationEventId" value="${communicationEventId}"/>
			      
			      <tr><td><input type="hidden" name="communicationEventTypeId" value="EMAIL_COMMUNICATION"/>
			       <input type="hidden" name="statusId" value="COM_IN_PROGRESS"/>
			<input type="hidden" value="text/html" name="contentMimeTypeId"/>
			<input type="hidden" name="partyIdFrom" value="${(partyIdFrom)?if_exists}"/>
			<input type="hidden" name="contactMechIdFrom" value="${(contactMechIdFrom)?if_exists}"/>
			 
			<tr><td ><input type="submit" value='Send'></td></tr> 
			
			</#if>
			</table>   
      
      </form>
</body>
