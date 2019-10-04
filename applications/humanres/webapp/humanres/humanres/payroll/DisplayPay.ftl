

<form name="completeform" action="<@ofbizUrl>CompletePay?partyIdFrom=${employeeId}</@ofbizUrl>" method="post">

<table class="basic-table hover-bar">
 <tr><td>${employeeId}</td></tr>
               
        <#if listValue?has_content>
      
          <#list listValue as atten>
           <tr>  
      <td>
          ${atten.attrName?if_exists}
          
         </td>
         
          
         <td>
          ${atten.attrValue?if_exists}
    
       </td>
      
        </#list>
         
        </tr>
        
          </#if>
          <tr><td>GrossPay</td><td>${grossPay}</td></tr>
          
           <#if listValueDed?has_content>
      
          <#list listValueDed as attenDed>
           <tr>  
      <td>
          ${attenDed.attrName?if_exists}
          
         </td>
         
          
         <td>
          ${attenDed.attrValue?if_exists}
    
       </td>
      
        </#list>
         
        </tr>
        
          </#if>
          <tr><td>netDeduction</td><td>${netDeduction}</td></tr>
          <tr><td>netPay</td><td>${netPay}</td></tr>
          
          <tr><td>
      <input type="submit" value="Completed"/></td></tr>
      
      
      
      
      
           </table>
           </form>
