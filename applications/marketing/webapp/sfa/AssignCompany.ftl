


    <script type="text/javascript">
    
    function assignToLead(){
    
    
    
        var e = document.getElementById("assignToPartyId");
        var val =new String( e.options[e.selectedIndex].value);
         for(i = 0 ; i < 20 ; i ++){
			   var v = document.getElementById('ListLeads__rowSubmit_o_'+i) ;
              if(v.checked){
                 document.getElementById('partyIdTo_o_'+i).value = val.toString() ;
              }
	   }	
	     document.ListLeads.submit();	   
			
    }
    
    </script>		
		
		<table>
			<td><b>Assign To</b></td>
			    <td  >&nbsp;</td>
			    <td>
			      <select name="assignToPartyId"  onchange="javascript:assignToLead();" id="assignToPartyId">
			        <option value="">${uiLabelMap.CommonSelectOne}</option>
			        <#list partyList as tempList>
			        <option value="${tempList.partyId}"> ${tempList.groupName?if_exists}</option>
			        </#list>
			      </select>
			    </td> 
		    </td>
		</table>