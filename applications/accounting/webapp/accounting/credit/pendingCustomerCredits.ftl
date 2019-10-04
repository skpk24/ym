     
       <div class="screenlet-title-bar">
        <h3>Customer Credit Receive Screen</h3>
    </div>
     
     
     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="1%">
            <input type="checkbox" name="checkAllOrders" value="1" onchange="javascript:toggleOrderId(this);"/>
          </td>
          <td width="5%">Order Id</td>
          <td width="5%">Customer Name</td>
          <td width="5%" align="right">Ordered Date</td>
          <td width="7%" align="right">Credit Amount</td>
          <td width="5%" align="right">Due Amount</td>
          <td width="7%" align="right">Store Name</td>
          <td width="4%" align="right">Action</td>
 
        </tr>
        <#assign alt_row = false>
         
         <#if requestAttributes.pendingCredits?exists && requestAttributes.pendingCredits?has_content>
           <#assign pendingCredits=requestAttributes.pendingCredits/>
           <#list pendingCredits as credit>
           <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
           
              <td>
                 <input type="checkbox" name="orderIdList" value="${credit.orderId?if_exists}"/>
              </td>
              <td>
                 ${credit.orderId?if_exists}
              </td>
               <td>
          <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",credit.billToPartyId))?if_exists/>
               <#if person?has_content && person.firstName?has_content>
               ${person.firstName?if_exists}
               <#else>
               _NA_
               </#if>
               
               </td>
              <td>
                 ${credit.orderDate?if_exists}
              </td>
 
               <td>
                 ${credit.getBigDecimal("maxAmount").toString()?if_exists}
               </td>
				<td>
			 <#assign dueCredits = Static["org.ofbiz.accounting.credit.CreditHelper"].dueCredits(delegator,credit.orderId)?if_exists/>
 	                  ${dueCredits?if_exists}
				</td>
               <td>
             <#assign facility = delegator.findByPrimaryKey("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId",credit.originFacilityId))?if_exists/>
                 ${facility.facilityName?if_exists}
               </td>
           <td align="left"> 
                <a href="<@ofbizUrl>showReceiveCredit?orderId=${credit.orderId?if_exists}</@ofbizUrl>" class='buttontext'>Receive</a>
            </td>
 
           </tr>
            <#assign alt_row = !alt_row>
           </#list>
         <#else>
        <tr>
          <td colspan="8">No Pending Credits Due
          </td>
        </tr>
         
         </#if>
         
    <tr>
          <td colspan="8">
          </td>
        </tr>
            
         <tr>
          <td >
          </td>
          <td ></td>
          <td ></td>
          <td align="left"></td>
          <td align="left"><b>Credit Total:-
          <#if requestAttributes.totalPendingCredit?exists>${requestAttributes.totalPendingCredit?if_exists}<#else>0.00</#if></b></td>
          <td align="left">
			 <#assign totaldueCredits = Static["org.ofbiz.accounting.credit.CreditHelper"].dueCreditsByCustomer(delegator,parameters.customerId)?if_exists/>
             <b>Due Total:-${totaldueCredits?if_exists}</b></td>
          <td align="left"></td>
          <td align="left"></td>
        </tr>
        
        
     <table>
     
