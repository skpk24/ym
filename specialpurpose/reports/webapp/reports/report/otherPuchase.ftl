     <link rel="stylesheet" href="/nichesuite/maincss.css" type="text/css"/>
     <div id="content-main-section">
       <div class="screenlet-title-bar">
          <h3>Order Summary</h3>
       </div>
     
     <table class="basic-table hover-bar" cellspacing='1'>
          <tr class="header-row">
          <td width="5%">Product Id</td>
          <td width="5%">Product Name</td>
          <td width="5%">Qty</td>
          <td width="5%">Unit Price</td>
          
        </tr>
        <#if requestAttributes.orderItems?exists && requestAttributes.orderItems?has_content>
           <#assign orderItems=requestAttributes.orderItems/>
                      <#list orderItems as dashBord>
                           <tr>
                            <td>
                                ${dashBord.get("productId")?if_exists}
                            </td>
                           <td>
                              ${dashBord.get("itemDescription")?if_exists}
                           </td>
                           <td>
                              ${dashBord.get("quantity")?if_exists}
                           </td>
                           <td>
                             ${dashBord.get("unitPrice")?if_exists}
                           </td>
                           
                           </tr>
                       </#list>
                   </#if>
    </table>
    
    </div>