  <div class="screenlet-title-bar">
        <h3>Customer Credit Summary</h3>
    </div>
    
    
     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td>Customer Name</td>
          <td >Ordered Date</td>
          <td >Credit Summary</td>
          <td >Credit Received Summary</td>
          <td>No. Of Day's</td>
          <td >Store Name</td>
          <td >Due Amount</td>
          <td></td>
        </tr>
        <#assign alt_row = false>
         
         <#if requestAttributes.customerIdSet?exists && requestAttributes.customerIdSet?has_content>
           <#assign customerIdSet=requestAttributes.customerIdSet?if_exists/>
           <#assign customerWiseCredit=requestAttributes.customerWiseCredit?if_exists/>
           <#assign customerWiseReceiveCredit=requestAttributes.recivedCreditCustomerwise?if_exists/>
           <#assign days=requestAttributes.days?if_exists/>
            
            <script>function openCredit(facilityId,frmdate,thrdate,billPartyId){
	            	 window.open("<@ofbizUrl>creditwindow</@ofbizUrl>?facilityId="+facilityId+"&fromDate="+frmdate+"&thruDate="+thrdate+"&billPartyId="+billPartyId, "", "width=800,height=600,status=no,scrollbars=yes");}
	        </script>
            
            
            <#list customerIdSet as customerId>
               <tr>
               <td>
              <#assign trasList=customerWiseCredit.get(customerId)?if_exists/>
              <#assign trasList1=customerWiseReceiveCredit.get(customerId)?if_exists/>
              
              <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",customerId))?if_exists/>
               <#if person?has_content && person.firstName?has_content>
                   ${person.firstName?if_exists}
                   <#else>
                   _NA_
               </#if>
               </td>
              <td>
                 ${trasList.get(0).orderDate?string("dd-MM-yyyy")?if_exists}
              </td>
               <td>
               <#if  trasList?has_content>
             <#assign facility = delegator.findByPrimaryKey("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId",trasList.get(0).originFacilityId))?if_exists/>
 <#--            <#list trasList as tra>
                  <b>Rs. ${tra.getBigDecimal("maxAmount").toString()?if_exists} on </b>  <b style="color:red">${tra.get("orderDate").toString()?if_exists}</b><br/>
               </#list>
                 <hr>-->
                 
          <a href="javascript:openCredit('WebStoreWarehouse','${requestAttributes.fromDateStr?if_exists}','${requestAttributes.thruDateStr?if_exists}','${customerId?if_exists}');" ><u>Total:-${requestAttributes.totalCustomerCredit.get(customerId)?if_exists}
        </u></a>
                 
                 <#else>
                  No Credit's
               </#if>
               </td>
              
               <td>
               <#if  trasList1?has_content>
                <#list trasList1 as tra>
                  <b>Rs. ${tra.getBigDecimal("maxAmount").toString()?if_exists} on </b>  <b style="color:red">${tra.get("createdStamp").toString()?if_exists}</b><br/>
                 </#list>
                 <hr>
                 Total:-${requestAttributes.totalRecevieCustomerCredit.get(customerId)?if_exists}

                 <#else>
                  No Receive Credit's
               </#if>
               </td>
               
               <td>
               <#if days?has_content>
                <b style="color:red">${days.get(customerId)?if_exists} </b>
                 <#else>
                  0
                </#if>
               </td>
               
                <td> ${facility.facilityName?if_exists}
                </td>
               
               <td>
                  <#assign dueCredits = Static["org.ofbiz.accounting.credit.CreditHelper"].dueCreditsByCustomer(delegator,customerId)?if_exists/>
               <#if dueCredits?has_content>${dueCredits?if_exists}
               <#else>
                  No Due Amount
               </#if>
               </td>
               <td>
            <#if dueCredits?has_content || dueCredits?string!="0">
              <#--<a href="<@ofbizUrl>PendingCustomerCredits?customerId=${customerId?if_exists}</@ofbizUrl>" class='buttontext'>Receive</a>-->
                
               <#else>
                  No Due Amount
               </#if>
 
               </td>
            </tr>
            </#list>
        </#if>
     </table>