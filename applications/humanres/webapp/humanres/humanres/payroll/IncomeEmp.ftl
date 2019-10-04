
<form name="Incomeform" action="<@ofbizUrl>addIncomeEmp</@ofbizUrl>" method="post">
<table class="basic-table eventitle" cellspacing="0">
<tr><td><b>Earning Type</b> </td>
<td><b>Cost</b></td></tr>
<#if ItemTypes?has_content>
 
    <#list ItemTypes as atten>
     <tr>
      <td>
          ${atten.description?if_exists}
         </td>
       <td>
          <input type="text" name="costValue"/>  
            </td>
              </tr>
            </#list>
          
        </#if>
      <input type="hidden" name="action" value="Create"/>  
      <input type="hidden" name="partyId" value="${partyId}"/> 
       <tr><td>
      <input type="submit" value="Create"/></tr></td>
       </table>
 <form>         