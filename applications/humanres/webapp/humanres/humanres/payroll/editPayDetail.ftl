<script type="text/javascript">
var xmlHttp = null;
  $(document).ready(function ()
   {
    $("#paymentMethod").change( function(){
     
     if("ACCOUNTTRANSFER" == $("#paymentMethod").val())
      { 
       $("#trAcName").show();
       $("#trAcNum").show();
       $("#trSortCode").show();
      }else{
      
        document.getElementById('acNumber').value="";
        $("#trAcName").hide();
       
        document.getElementById('acName').value="";
        $("#trAcNum").hide();
      
         document.getElementById('sortCode').value="";
        $("#trSortCode").hide();
      }
    });
     $("#employmentDetailDiv").click( function(){
         $.get("editEmploymentDetail?payCycle="+$("#payCycle").val()+"&paymentMethod="+$("#paymentMethod").val()+"&acName="+$("#acName").val()+"&sortCode="+$("#sortCode").val()+"&acNumber="+$("#acNumber").val()+"&joiningDate="+$("#startDate").val()+"&partyId="+${partyId?if_exists},"",function(result){
           alert(result);
         });
         
      });
   
     $("#addPaymentDiv").click( function(){
      $( "#dialog-Payment" ).show();
      $( "#dialog-Payment" ).dialog({height: 250,width:500,modal: true,draggable: false,resizable: false,buttons: {"Cancel": function(){document.getElementById('PaymentType').value="";document.getElementById('incomeName').value=""; $("#dialog-Payment").dialog("close"); },
	   "Save": function() {
				   if(document.getElementById('PaymentType').value == "")
				    {
				     alert("payment Type required");
				    }
				    if(document.getElementById('incomeName').value == "")
				    {
				     alert("Name required");
				    }
				     var ptypeId = document.getElementById('PaymentType').value ;
				     var pname = document.getElementById('incomeName').value ;
				     var desc = document.getElementById('description').value ;
			        $("#paymenttabletr").before("<tr id=\"tr***"+ptypeId+"\"><td colspan=\"4\" align=\"left\" ><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"  class=\"inner3\"> <tr><td><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td > <img src=\"/images/close.png\" id=\"img***"+ptypeId+"\"  width=\"16\" height=\"16\" border=\"0\" onclick=\"removeRow('"+ptypeId+"')\"  /></td><td width=\"39%\" align=\"left\" class=\"text1\"><input type=\"hidden\" name=\"pname\" value=\""+pname+"\" id=\"pname***"+ptypeId+"\"/><input type=\"hidden\" name=\"description\" value=\""+desc+"\" id=\"desc***"+ptypeId+"\"/>"+pname+"</td><td width=\"22%\" align=\"center\" class=\"text1\"><input type=\"text\" name=\"quantity\" value=\"1\" id=\"quantity***"+ptypeId+"\" size=\"6\" maxlength=\"6\" onchange=\"updateValueTotal(this.id)\" class=\"quantity\"/></td><td width=\"18%\" align=\"center\" class=\"text1\"><input type=\"text\" name=\"rate\" id=\"rate***"+ptypeId+"\" onchange=\"updateValueTotal(this.id)\" size=\"8\"  maxlength=\"12\"/></td><td width=\"21%\" align=\"center\" class=\"text1\"><div id=\"paytotal***"+ptypeId+"\" ></div></td></tr></table></td></tr></table></td></tr>");
			        document.getElementById('PaymentType').value="";
			        document.getElementById('incomeName').value=""; 
			        $("#dialog-Payment").dialog("close");
				}
				}
		
		}); 
    });
    //TODO end addPaymentDiv click  function
    $("#PaymentType").change(function()
      {
       document.getElementById('incomeName').value=$('#PaymentType option:selected').text();
      });
     
    });
    
    function updateValueTotal(nm)
    {
     
      var sp=nm.split("***");
      var quantId ="quantity***";
      var rateId ="rate***";
      if("rate" == sp[0])
        {
         quantId = quantId +sp[1];
         rateId = nm;
        }
        else
        {
         rateId = rateId+sp[1];
         quantId =nm;
        }
       var  rate= document.getElementById(rateId).value;
       
       if (rate == null || !rate.toString().match(/^[-]?\d*\.?\d*$/))
       {
         document.getElementById(rateId).value="";
         document.getElementById(divId).innerHTML =0;
         document.getElementById(rateId).focus();
         return;
       }
       var  quantity= document.getElementById(quantId).value;
       if (quantity == null || !quantity.toString().match(/^[-]?\d*\.?\d*$/))
       {
         document.getElementById(quantId).value="";
         document.getElementById(divId).innerHTML =0;
         document.getElementById(quantId).focus();
         return;
       }
       var  pname= document.getElementById("pname***"+sp[1]).value;
       var  descr= document.getElementById("desc***"+sp[1]).value;
       var  pId = '10001';
       var divId = "paytotal***";
       divId= divId +sp[1];
     //  document.getElementById(divId).innerHTML = rate*quantity;
       
      // make http request
    
      var Url = "addEmpIncome?quantity="+quantity+"&rate="+rate+"&pname="+pname+"&partyId="+pId+"&description="+descr+"&invoiceTypeId="+sp[1];
   
     if (window.XMLHttpRequest)
	{ 
		xmlHttp =new XMLHttpRequest();
	}
	else if (window.ActiveXObject)
	{
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
      xmlHttp.onreadystatechange = ProcessRequest;
      xmlHttp.open( "GET", Url, true );
      xmlHttp.send( null );
      document.getElementById(divId).innerHTML = rate*quantity;
     }
    
     function ProcessRequest() 
    {
     if (xmlHttp.readyState == 4 && xmlHttp.status == 200 ) 
      {
        
        var data = xmlHttp.responseText;
        
      }
    }  
   function removeRow(nm)
    {
       alert(nm);
        var Url = "removeEmpIncome?partyId="+${partyId?if_exists}+"&attrName="+nm;
		if (window.XMLHttpRequest)
		{ 
			xmlHttp =new XMLHttpRequest();
		}
		else if (window.ActiveXObject)
		{
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		}
		  xmlHttp.onreadystatechange = ProcessRequest;
		  xmlHttp.open( "GET", Url, true );
		  xmlHttp.send( null );
		   alert("tr***"+nm);
         var element = document.getElementById("tr***"+nm);
        
         element.parentNode.removeChild(element);
    
    }  
</script>
 
 <div class="outer1">
  <div  style="padding-bottom:10px;">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr>
	      <td width="54%" align="left" class="text">Employment Details</td>
        </tr>
      </table>
</div>
   <div>
      <table width="50%" border="0" cellspacing="0" cellpadding="0" >
        <tr>
         <td  width="25%" align="left" ><b>Pay Cycle*</b></td>
         <td width="25%" align="left">
          <select name="payCycle" id="payCycle" >
           <option value="">[Select pay cycle]</option>
           <#if employmentDetail?exists && employmentDetail?has_content>
           <#if "WEEKLY"== employmentDetail.payFrequency>
           <option value="WEEKLY" selected>Weekly</option>
           <option value="MONTHLY">Monthly</option>
           <#else>
            <option value="WEEKLY" >Weekly</option>
            <option value="MONTHLY" selected>Monthly</option>
           </#if>
           <#else>
            <option value="WEEKLY" >Weekly</option>
            <option value="MONTHLY" selected>Monthly</option>
           </#if> 
          </select>
         </td>
       </tr>
       
       <tr>
         <td  width="25%" align="left" ><b>Payment Method*</b></td>
         <td width="25%" align="left">
          <select name="paymentMethod" id="paymentMethod">
           <option value="">[Select payment method]</option>
           <#if employmentDetail?exists && employmentDetail?has_content>
           
           <option value="CASH" <#if "CASH"== employmentDetail.paymentMethod>selected</#if>>Cash</option>
           <option value="CHECK" <#if "CHECK"== employmentDetail.paymentMethod>selected</#if>>Check</option>
           <option value="ACCOUNTTRANSFER" <#if "ACCOUNTTRANSFER"== employmentDetail.paymentMethod>selected</#if>>Account Transfer</option>
          <#else>
           <option value="CASH" >Cash</option>
           <option value="CHECK" >Check</option>
           <option value="ACCOUNTTRANSFER">Account Transfer</option>
          </#if> 
          </select>
         </td>
       </tr>
        <tr id="trAcName" <#if "ACCOUNTTRANSFER"== employmentDetail.paymentMethod><#else> style="display:none"</#if>>
        <td  width="25%" align="left" ><b>Bank A/C Name*</b></td>
        <td  width="25%"><input type="text" name="acName" id="acName"  value="${employmentDetail.accountName?if_exists}"/></td>
       </tr>
        <tr id="trAcNum" <#if "ACCOUNTTRANSFER"== employmentDetail.paymentMethod><#else> style="display:none"</#if>>
        <td  width="25%" align="left" ><b>Bank A/C*</b></td>
        <td  width="25%"><input type="text" name="acNumber" id="acNumber" value="${employmentDetail.accountNumber?if_exists}"/></td>
       </tr>
        <tr id="trSortCode" <#if "ACCOUNTTRANSFER"== employmentDetail.paymentMethod><#else> style="display:none"</#if>>
        <td  width="25%" align="left" ><b>Sort Code</b></td>
        <td  width="25%"><input type="text" name="sortCode" id="sortCode" size="8" maxLength="8"  value="${employmentDetail.startCode?if_exists}" /></td>
       </tr>
       <tr>
         <td  width="25%" align="left" ><b>Start Date*</b></td>
         <td width="25%" align="left">
          <input type="text" name="startDate" id="startDate" size="10" maxlenght="10" value="${employmentDetail.joiningDate?if_exists}"/>
          <script>
			$(function() {
				$( "#startDate" ).datepicker({
					changeMonth: true,
					changeYear: true
				});
			});
	</script>
         </td>
       </tr>
       <tr>
         <td width="50%" colspan="2" align="left"  class="text1"><div  id="employmentDetailDiv" class="addButtonDiv" style="width: 40px;"><b>Save</b></div></td>
       </tr>
     </table>
  </div>
 </div>
 
<div class="outer1"> 
<div  style="padding-bottom:10px;">
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr>
	      <td width="54%" align="left" class="text">Setup Pay Details</td>
	      <td width="20%" align="left"><div class="inner2"> <span class="text2"> Net Pay </span></div></td>
        </tr>
      </table>
</div>
    <div >
      <table width="100%" border="0" cellspacing="0" cellpadding="0" id="paymenttable">
        <tr>
         <td width="39%" align="left" class="text1"><b>Payments<abbr title="required"></abbr></b></td>
          <td width="22%" align="center" class="text1"><b>Quantity</b></td>
          <td width="18%" align="center" class="text1"><b>Rate</b></td>
          <td width="21%" align="center" class="text1"><b>Total</b></td>
        </tr>
        <tr>
          <td height="8" colspan="4" align="left"> </td>
        </tr>
       <#--
        <tr>
          <td colspan="4" align="left" ><table width="100%" border="0" cellspacing="0" cellpadding="0"  class="inner3">
            <tr>
              <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="39%" align="left" class="text1">1111</td>
                  <td width="22%" align="center" class="text1">222</td>
                  <td width="18%" align="center" class="text1">222</td>
                  <td width="21%" align="center" class="text1">222</td>
                </tr>
              </table></td>
            </tr>
          </table>
          </td>
        </tr>
        --> 
        <#if empPayDetails?exists && empPayDetails?has_content>
          <#list empPayDetails as empPay>
            <tr id="tr***${empPay.attrName?if_exists}">
            <td colspan="4" align="left" >
            <table width="100%" border="0" cellspacing="0" cellpadding="0"  class="inner3"> 
              <tr>
               <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                 <td ><img src="/images/close.png"  width="16" height="16" border="0" id="img***${empPay.attrName?if_exists}" onclick="removeRow('${empPay.attrName?if_exists}')" /></td>
                 <td width="39%" align="left" class="text1"><input type="hidden" name="pname" value="${empPay.name?if_exists}" id="pname***${empPay.attrName?if_exists}"/><input type="hidden" name="description***${empPay.attrName?if_exists}" value="{empPay.description?if_exists}" id="desc***${empPay.attrName?if_exists}"/>${empPay.name?if_exists}</td>
                 <td width="22%" align="center" class="text1"><input type="text" name="quantity" value="${empPay.qunatity?if_exists}" id="quantity***${empPay.attrName?if_exists}" size="6" maxlength="6" onchange="updateValueTotal(this.id)" class="quantity"/></td>
                 <td width="18%" align="center" class="text1"><input type="text" name="rate" id="rate***${empPay.attrName?if_exists}" value="${empPay.rate?if_exists}" onchange="updateValueTotal(this.id)" size="8"  maxlength="12"/></td>
                 <td width="21%" align="center" class="text1><div id=paytotal${empPay.attrName?if_exists}" >${empPay.attrValue?if_exists}</div></td></tr></table></td></tr></table></td>
                </tr>
          </#list>
        </#if>
        
         <tr id="paymenttabletr">
         <td colspan="4" align="left">
          <table width="100%" border="0" cellspacing="0" cellpadding="0"  class="inner3">
              <tr>
                <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="50%" colspan="2" align="left" class="text1"><div id="addPaymentDiv" class="addButtonDiv" style="width: 80px;">Add Payment</div></td>
                  <td width="50%" colspan="2"  align="center" class="text1">Total payments:</td>
                </tr>
              </table>
               </td>
              </tr>
           </table> 
         </td>
        </tr>
       <td colspan="4" align="left" class="text1">&nbsp;</td>
        </tr>
      </table>
 </div>
   <#-- deduction div begin -->
  <div>
      <table width="100%" border="0" cellspacing="0" cellpadding="0" id="paymenttable">
        <tr>
         <td  colspan="4" width="39%" align="left" class="text1"><b>Deductions<abbr title="required"></abbr></b></td>
          
        </tr>
        <tr>
          <td height="8" colspan="4" align="left"> </td>
        </tr>
        <#if empDeductions?exists && empDeductions?has_content>
          <#list empDeductions as empPay>
            <tr id="tr***${empPay.attrName?if_exists}">
            <td colspan="4" align="left" >
            <table width="100%" border="0" cellspacing="0" cellpadding="0"  class="inner3"> 
              <tr>
               <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                 <td ><img src="/images/close.png"  width="16" height="16" border="0" id="img***${empPay.attrName?if_exists}" onclick="removeRow('${empPay.attrName?if_exists}')" /></td>
                 <td width="39%" align="left" class="text1"><input type="hidden" name="pname" value="${empPay.name?if_exists}" id="pname***${empPay.attrName?if_exists}"/><input type="hidden" name="description***${empPay.attrName?if_exists}" value="{empPay.description?if_exists}" id="desc***${empPay.attrName?if_exists}"/>${empPay.name?if_exists}</td>
                 <td width="22%" align="center" class="text1"><input type="text" name="quantity" value="${empPay.qunatity?if_exists}" id="quantity***${empPay.attrName?if_exists}" size="6" maxlength="6" onchange="updateValueTotalD(this.id)" class="quantity"/></td>
                 <td width="18%" align="center" class="text1"><input type="text" name="rate" id="rate***${empPay.attrName?if_exists}" value="${empPay.rate?if_exists}" onchange="updateValueTotalD(this.id)" size="8"  maxlength="12"/></td>
                 <td width="21%" align="center" class="text1><div id=paytotal${empPay.attrName?if_exists}" >${empPay.attrValue?if_exists}</div></td></tr></table></td></tr></table></td>
                </tr>
          </#list>
        </#if>
        <tr id="deductiontabletr">
         <td colspan="4" align="left">
          <table width="100%" border="0" cellspacing="0" cellpadding="0"  class="inner3">
              <tr>
                <td>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="50%" colspan="2" align="left" class="text1"><div id="addPaymentDiv" class="addButtonDiv" style="width: 100px;">Add Deductions</div></td>
                  <td width="50%" colspan="2"  align="center" class="text1">Total payments:</td>
                </tr>
              </table>
               </td>
          </tr>
      </table>  
</div>
<#-- deduction div end here  -->
 <div id="weekly_pay_pattern_btn" class="Primary Button right"></div>
  </div>

<#-- add payment dialog div begin -->
 <div id="dialog-Payment" title="" style="display:none;">
    <table>
	  <tr>
      <td>${uiLabelMap.PaymentType}*</td>
       <td>
         <select name="PaymentType" id="PaymentType" >
            <option value="">[Select Type]</option>
            <#if payments?exists && payments?has_content>
            <#list payments as payment>
             <option value="${payment.invoiceItemTypeId?if_exists}">${payment.description?if_exists}</option>
            </#list>
            </#if>
           </select>
      </td>
     <tr>
	  <tr>
	    <td>Name*</td>
	    <td><input type="text" name="incomeName" style="width:180px" id="incomeName"/></td>
	  <tr>
	  <tr>
	    <td>Description</td>
	    <td><input type="text" name="description"  id="description" size="50"></td>
	  <tr>
	</table>
	</div>
	<#-- add payment dialog div end -->
	
	<#-- add Deductions dialog div begin -->
 <div id="dialog-Deduction" title="" style="display:none;">
    <table>
	  <tr>
      <td>${uiLabelMap.DeductionType}*</td>
       <td>
         <select name="PaymentType" id="PaymentType" >
            <option value="">[Select Type]</option>
            <#if payments?exists && payments?has_content>
            <#list payments as payment>
             <option value="${payment.invoiceItemTypeId?if_exists}">${payment.description?if_exists}</option>
            </#list>
            </#if>
           </select>
      </td>
     <tr>
	  <tr>
	    <td>Name*</td>
	    <td><input type="text" name="incomeName" style="width:180px" id="incomeName"/></td>
	  <tr>
	  <tr>
	    <td>Description</td>
	    <td><textarea name="description" cols="40" rows="2"  id="description"></textarea></td>
	  <tr>
	</table>
	</div>
	<#-- add Deductions dialog div end -->
