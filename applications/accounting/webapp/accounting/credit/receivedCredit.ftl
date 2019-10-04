     <table class="basic-table hover-bar" cellspacing='1'>
        <tr class="header-row">
          <td width="1%">
            <input type="checkbox" name="checkAllOrders" value="1" onchange="javascript:toggleOrderId(this);"/>
          </td>
          <td width="5%">Order Id</td>
          <td width="5%">Customer Name</td>
          <td width="5%" align="right">Ordered Date</td>
          <td width="7%" align="right">Received Credit Amount</td>
          <td width="7%" align="right">Store Name</td>
          <td width="4%" align="right">Status</td>
 
        </tr>
        <#assign alt_row = false>
         <#if requestAttributes.orderIdSet?exists && requestAttributes.orderIdSet?has_content>
           <#assign orderIdSet=requestAttributes.orderIdSet/>
           <#assign recivedCreditmap=requestAttributes.recivedCreditmap?if_exists/>
           <#list orderIdSet as orderId>
           <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
           
              <td>
                 <input type="checkbox" name="orderIdList" value="${orderId?if_exists}"/>
              </td>
              <td>
                 ${orderId?if_exists}
              </td>
               <td>
               <#assign trasList=recivedCreditmap.get(orderId)/>
          <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",trasList.get(0).billToPartyId))?if_exists/>
               <#if person?has_content && person.firstName?has_content>
               ${person.firstName?if_exists}
               <#else>
               _NA_
               </#if>
               
               </td>
              <td>
                 ${trasList.get(0).orderDate?if_exists}
              </td>
 
               <td>
               <#list trasList as tra>
                <b>Rs. ${tra.getBigDecimal("maxAmount").toString()?if_exists} on</b>  <b style="color:red">${tra.get("createdStamp").toString()?if_exists}</b><br/>
               
               </#list>
               </td>
 
               <td>
             <#assign facility = delegator.findByPrimaryKey("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId",trasList.get(0).originFacilityId))?if_exists/>
                 ${facility.facilityName?if_exists}
               </td>
           <td align="left"> 
                ${trasList.get(0).statusId?if_exists}
            </td>
 
           </tr>
            <#assign alt_row = !alt_row>
           </#list>
         </#if>
       <tr>
          <td colspan="7">
          </td>
        </tr>
         
         <tr>
          <td >
          </td>
          <td ></td>
          <td ></td>
          <td align="left"></td>
          <td align="left"><b>Received Credit Total:-${requestAttributes.totalReceivedCredit?if_exists}</b></td>
          <td align="left"></td>
          <td align="left"></td>
        </tr>
     <table>
     
