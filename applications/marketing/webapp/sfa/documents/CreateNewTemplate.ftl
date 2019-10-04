<html>
<body>

<#--<form action="uploadFile" enctype="multipart/form-data" method="post">

Document Name: <input type="text" name="Name" /><br />
Access: <select id="access" name="access">
  <option>Internal</option>
  <option>External</option>
  <option>Internal/External</option>
</select> <br />

				<div>Folder:	
                    <select id="folder" name="folder">
                      <option value="My Personal Documents">My Personal Documents</option>
                      <#list DocumentdetailsFolderList as DocumentdetailsFolder>
                        <#assign displayDesc = DocumentdetailsFolder.catalogName?default("${uiLabelMap.ProductNoDescription}")>
                          <#if 18 < displayDesc?length>
                            <#assign displayDesc = displayDesc[0..15] + "...">
                          </#if>
                          <option value="${DocumentdetailsFolder.documentFolderName}">${DocumentdetailsFolder.documentFolderName}</option>
                      </#list>
                    </select>
                 </div><br />


<a href="CreateNewFolder">Create New Folder</a><br />
<a href="http://localhost:8080/partymgr/control/img/test.html;jsessionid=D26A40977BB4433D45BD72F3CC247606.jvm1?imgId=13053">View</a>
Description: <input type="text" name="desc" /><br />


CREATE A REFERENCE LINK TO THE FILE. ENTER A FILE LOCATION THAT OTHERS CAN ACCESS:<br>

<div>
PATH/URL TO REFERENCE: <input type="text" name="file2" /><br />
</div>

File To Upload:<br>
<input type="file" name="file" size="30">
</p>
<div>
<input type="submit" value="Save">
</div>
</form> -->

<div class="label">Attach Content</div>


     <table>  
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="uploadPartyContent">


    <tr>
     <#--   <div>
       Template Type &nbsp;&nbsp  <select name="partyContentTypeId" class="required error">
          <option value="">Select Purpose</option>
            <option value=''>&nbsp;</option>
                <#list PartyContentList as allCustomTimePeriod>
                  <option value='${allCustomTimePeriod.partyContentTypeId}'> ${allCustomTimePeriod.partyContentTypeId?if_exists}  </option>
                </#list>
         </select>
        </div>
        -->
    </tr>     
        
      <tr> <td> <b>Template Name &nbsp;&nbsp</b> <input type="text" id="contentName"/> </td></tr>
      <tr> <td><b>Version Number &nbsp;&nbsp</b> <input type="text" id="description"/></td> </tr>
      <tr>  <td><b>Date  &nbsp</b> <input type="text" name="createdDate" id="createdDate"  size="25" maxlength="30" value="${requestAttributes.fromDate?if_exists}" >
             <script>
					$(function() {
						$( "#createdDate" ).datepicker({
							changeMonth: true,
							changeYear: true,
							 minDate: 0
						});
					});
			 </script>  </td></tr>
      <tr> <td> <b>Descrition  &nbsp</b> <input type="text" id="description"/>  </td></tr>
      <tr> <td> <b>Template Body  &nbsp</b> <input type="text" id="templateBody"/> </td> </tr>
        <input type="hidden" name="dataCategoryId" value="PERSONAL"/>

        <input type="hidden" name="contentTypeId" value="DOCUMENT"/>

        <input type="hidden" name="statusId" value="CTNT_PUBLISHED"/>
        <input type="hidden" name="contentId" value="${parameters.contentId?if_exists}" id="contentId"/>

        <input type="hidden" name="partyId" value="admin" id="contentPartyId"/>
        <input type="hidden" name="partyContentTypeId" value="INTERNAL" id="contentPartyId"/>
        <tr> <td> 
        <input type="file" name="uploadedFile" class="required error" size="25"/>
        </tr> </td>  
      

      <#--   <div class="label">Is Public</div>

      <select name="isPublic">

            <option value="N">No</option>

            <option value="Y">Yes</option>

        </select> --> 

		<input type="hidden" name="roleTypeId" value="SFA_ROLE"/>
        
          <tr> <td> 
        <input type="submit" value="Create" />
         </tr> </td>  
      </form>
   </table>
      <div id='progress_bar'><div></div></div>

    </div>

  </div>


</body>
</html>