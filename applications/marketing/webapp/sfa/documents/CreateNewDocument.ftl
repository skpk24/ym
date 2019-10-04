<html>
<body>

     <table>  
      <form id="uploadPartyContent" method="post" enctype="multipart/form-data" action="uploadPartyContent">


 <#--   <tr>
         <div>
       Template Type &nbsp;&nbsp  <select name="partyContentTypeId" class="required error">
          <option value="">Select Purpose</option>
            <option value=''>&nbsp;</option>
                <#list PartyContentList as allCustomTimePeriod>
                  <option value='${allCustomTimePeriod.partyContentTypeId}'> ${allCustomTimePeriod.partyContentTypeId?if_exists}  </option>
                </#list>
         </select>
        </div>
    </tr>     
        
      <tr> <td> <b>Template Name &nbsp;&nbsp</b> <input type="text" id="contentName"/> </td></tr>
      <tr> <td><b>Version Number &nbsp;&nbsp</b> <input type="text" id="description"/></td> </tr>
      <tr>  <td><b>Date  &nbsp</b> <input type="text" id="createdDate"/>  </td></tr>
      <tr> <td> <b>Descrition  &nbsp</b> <input type="text" id="description"/>  </td></tr>
      <tr> <td> <b>Template Body  &nbsp</b> <input type="text" id="templateBody"/> </td> </tr>
-->
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
        <input type="submit" value="Upload" />
         </tr> </td>  
      </form>
   </table>
      <div id='progress_bar'><div></div></div>

    </div>

  </div>


</body>
</html>