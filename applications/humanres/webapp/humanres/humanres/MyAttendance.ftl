            <table class="basic-table hover-bar">
                <tr class="header-row-2">
                   <td> SignIn  </td>
                   <td> SignIn Note  </td>
                   <td> SignOut </td>
                   <td> SignOut Note</td>
                   <td>Department</td>
                   <td> Duration(Hours)</td>
                </tr>
            
        <#assign alt_row = false>
        <#assign total=0>
      <#if attenList?has_content>
        <#list attenList as atten>
        <tr <#if alt_row> class="alternate-row"</#if>>
             <td>
               ${atten.signInTime?if_exists}
             </td>
              <td>
                ${atten.signInNote?if_exists}
              </td>
             
               <td> 
               ${atten.signOutTime?if_exists}
              </td>
               <td>
               ${atten.signOutNote?if_exists}
              </td>
          <#assign min=atten.duration/>
          <#assign hr=(min/(60*60))/>
          <#assign round= Static["org.ofbiz.accounting.AttendanceUtil"].Round(hr)>
          <#assign total=total+hr/>
			<#assign department = delegator.findByAnd("Employment",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyIdTo",atten.partyId))?if_exists>
               <td>
                ${department.get(0).partyIdFrom?if_exists}
               </td>
              <td>${round?if_exists}</td>
              
          </tr>
             <#assign alt_row = !alt_row>
              </#list>
               <tr>
                   <td colspan="6"><HR/></td>
                </tr>
               <tr>
                   <td><b>TOTAL</b> </td>
                   <td colspan="4"></td>
                   <td><b>${total?if_exists}</b></td>
                </tr>
                 <tr>
                   <td colspan="6"><HR/></td>
                </tr>
                 <#else>
                <td colspan="6">NO RECORD'S FOUND</td>
               </#if>
             </table>
