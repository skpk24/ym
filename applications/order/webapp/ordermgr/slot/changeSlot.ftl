<script src="/images/prototypejs/prototype.js" language="JavaScript" 
type="text/javascript"></script>
<script type="text/javascript" language="JavaScript">
 
     
     
    
</script>

<body>

<form name="myform" action="<@ofbizUrl>changeSlot</@ofbizUrl>" method="post">


<table cellspacing="0" class="basic-table">
<tr><td class="label"><span>Delivery Date:</span>  </td>
	<td><input id="DeliveryDate" type="text"  name="DeliveryDate" title="Format: yyyy-MM-dd" size="10" maxlength="10"/>    <script type="text/javascript">
             jQuery("#DeliveryDate").datepicker({
              minDate: 0,
              maxDate: '+1M ' ,
                showOn: 'button',
                buttonImage: '',
                buttonText: '',
                onSelect: validate, 
                buttonImageOnly: false,
                dateFormat: 'dd-mm-yy'
              });
              
              function validate(DeliveryDate)
              { 
                var n=document.getElementById("DeliveryDate").value;
               
                var url = 'ContactSelection';
               new Ajax.Updater("checkAssetAvailability", "<@ofbizUrl>ContactSelection</@ofbizUrl>", {parameters:{DeliveryDate: DeliveryDate}});
                
                
                
                 }
                 
             function addInput() 
             {
              if(document.getElementById("DeliveryDate").value=="" ||document.getElementById("DeliveryDate").value==null)
              {
              alert("Select The Date");
              return false;
              }
             }    
      </script>

	</td>
</tr>

<tr>
    
	<td colspan="2">
	  <span id="checkAssetAvailability"></span>
	</td></tr>
<tr>
	<td></td>
	<td><input type="submit" value='ChangeSlot' onclick="return addInput()"></td></tr> 
</table>   
   <#if requestParameters.slotId?exists>
 <input type="hidden" name="slotId" value="${requestParameters.slotId}"/>

</#if>
      
      </form>
       <#assign msg=request.getAttribute("result1")?if_exists>
       <#if msg=="success">
      
       <h><b>successfully Updated</b></h>
       
       </#if>
       
</body>
