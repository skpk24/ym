     <link rel="stylesheet" href="/nichesuite/maincss.css" type="text/css"/>
     <div id="content-main-section">
       <div class="screenlet-title-bar">
          <h3>Cash Summary</h3>
       </div>
     
     <table class="basic-table hover-bar" cellspacing='1'>
          <tr class="header-row">
          <td width="5%">Order No.</td>
          <td width="5%">Bill Date</td>
          <td width="5%">Amount</td>
          <td width="5%">Cutomer Name</td>
          
        </tr>
        <#if requestAttributes.cashwindowlist?exists && requestAttributes.cashwindowlist?has_content>
           <#assign cashwindowlist=requestAttributes.cashwindowlist/>
           <script>
           
           function orderitemwindow(orderId){
	            	window.open("<@ofbizUrl>orderitemwindow</@ofbizUrl>?orderId="+orderId, "", "width=650,height=400,status=no,scrollbars=yes");}
           function partywindow(partyId){
	            	window.open("<@ofbizUrl>cutomerwindow</@ofbizUrl>?partyId="+partyId, "", "width=650,height=400,status=no,scrollbars=yes");}

	        </script>
                      
                      
                      <#list cashwindowlist as dashBord>
                           <tr>
                            <td>
                                <a href="javascript:orderitemwindow('${dashBord.get("orderId")?if_exists}');" class="buttontext">${dashBord.get("orderId")?if_exists}</a>
                            </td>
                            <td>
                              ${dashBord.get("billDate")?if_exists}
                            </td>
                            <td>
                              ${dashBord.get("orderAmt")?if_exists}
                            </td>
                            <td>
                               <a href="javascript:partywindow('${dashBord.get("partyId")?if_exists}');" class="buttontext">${dashBord.get("cutomerName")?if_exists}</a>
                            </td>
                         </tr>
                       </#list>
        </#if>
         <tr>
          <td colspan="4"><hr/> </td>
         <tr>
        
        <tr>
          <td> Total Cash</td>
          <td colspan="2"> </td>
          <td >${requestAttributes.totalCash?if_exists} </td>
          
        <tr>
        
    </table>
    
    </div>