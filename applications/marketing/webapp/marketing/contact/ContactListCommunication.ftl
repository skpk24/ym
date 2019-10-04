<script type="text/javascript">
 
	function ValidatetemplateForm(createEmailTemplate)
	{
		var  filename=document.createEmailTemplate.htmlcontentfile.value;
		var  subFilename= filename.substring(filename.indexOf('.', 0) , filename.length);
	    
		if (document.createEmailTemplate.contactListName.value == "")
		{
		 alert("Please Insert Template Name");
		 document.createEmailTemplate.contactListName.focus();
		 return false;
		}
		/*
		if (document.createEmailTemplate.subject.value == "")
		{
		 alert("Please Insert Subject");
		 document.createEmailTemplate.subject.focus();
		 return false;
		}
		*/
		if (document.createEmailTemplate.htmlcontentfile.value == "")
		{
		 alert("Please select your file.");
		 document.createEmailTemplate.htmlcontentfile.focus();
		 return false;
		}
	      
		if (subFilename !='.html' && subFilename !='.htm')
		{
		 alert("Please select .html format  file.");
		 document.createEmailTemplate.htmlcontentfile.focus();
		 return false;
		}
		document.createEmailTemplate.action="createContactListEmailTemp2?contactListId="+document.getElementById('contactListId').value+"&contactListName="+document.getElementById('contactListNameId').value+"&subject="+document.getElementById('subject').value+"&partyIdFrom="+document.getElementById('partyIdFrom').value;
		document.createEmailTemplate.enctype='multipart/form-data' ;
		document.createEmailTemplate.submit();
		return true;
    }
</script>  



  <form name="createEmailTemplate" action="createContactListEmailTemp2" method="post" enctype="multipart/form-data"  onsubmit="return ValidatetemplateForm(createEmailTemplate)">
     <input type="hidden" name="contactListId"  id="contactListId"  value="${contactListId?if_exists}" />
     <input type="hidden" name="filename" id="filename" value="${filename?if_exists}"/>
 <table>
   <tr>
   <td>${uiLabelMap.TemplateName}</td>
   <td><input type="text" name="contactListName" id="contactListNameId" size="30" value="${contactListName?if_exists}"/></td>
   </tr>
   <#--<tr>
   <td>${uiLabelMap.PartySubject}</td>
   <td><input type="text" name="subject" id="subject" size="30" value="${subject?if_exists}" /> </td>
   </tr>-->
   <#--<tr>
   <td>${uiLabelMap.PartyPartyFrom}</td>
   <td><input type="text" name="partyIdFrom"  id="partyIdFrom" value="${partyIdFrom?if_exists}" />
   <a href="javascript:call_fieldlookup2(document.createEmailTemplate.partyIdFrom,'LookupPartyName');"><img src="/images/fieldlookup.gif" width="15" height="14" border="0" alt="Lookup"/></a>
   </td>
   </tr>-->
   <tr>
   <td>${uiLabelMap.uploadFile}</td>
   <td><input type="file" name="htmlcontentfile"  id="htmlcontentfile"   size="30"  /></td>
   <td><font color="red">* Upload HTML File Only</font></td>
   <td><a href="<@ofbizContentUrl>/images/importFiles/emailTemplate/marketingEmailTemplate.html</@ofbizContentUrl> " target="_blank">Preview</a></td>
   
   </tr> 
   <td>  </td>
   <td><input type="submit" class="smallSubmit" name="submitButton" value="Save"/>
    </td>
   </tr> 
  </table>
  </form>
 
   
   
   
    
