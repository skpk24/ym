<script src="/images/prototypejs/prototype.js" language="JavaScript" 
type="text/javascript"></script>
<script type="text/javascript" language="JavaScript">
 function validateBarcode(formAction){
 	var productBarcode = document.getElementById('productBarcode').value;
	var flag = true;
	if(productBarcode==''){
		alert("Please enter Product barcode.");
		document.getElementById('productBarcode').focus();
		flag = false;
		}
	 if(flag){
	 act = "<@ofbizUrl>"+formAction+"</@ofbizUrl>";
	    document.myform.action=act
         document.myform.submit();
	 }
} 	
</script>
<body>
<form name="myform" id="myform"  method="post">
		<span style="font-weight:bold;">Enter Barcode :</span> 
		<input id="productId" type="hidden" style="padding:5px !important; float:left;" value="${productId?if_exists}"  name="productId" size="35"/> 
		<div id="div0">
		<#if barCodeList?has_content>
		<#assign formAction = "updateProductBarcode">
			<#assign buttonName = "Update Barcode">
		${barCodeList.barcode?if_exists}
 			<input id="productBarcode" type="text" style="padding:5px !important; float:left;"  name="productBarcode" size="35" maxlength="20"  value="<#if barCodeList?has_content>${barCodeList.barcode?if_exists}</#if>" /></br><span style="color:#ff0000; font-size:11px;"> * </span>
 			<input id="productBarcodeOld" type="hidden" style="padding:5px !important; float:left;"  name="productBarcodeOld" size="35" maxlength="20"  value="<#if barCodeList?has_content>${barCodeList.barcode?if_exists}</#if>" /></br><span style="color:#ff0000; font-size:11px;"> * </span>
 			<#else>
 			<#assign buttonName = "Create Barcode">
 			<#assign formAction = "createProductBarcode">  
 			<input id="productBarcode" type="text" style="padding:5px !important; float:left;"  name="productBarcode" size="35" maxlength="20"  value="" /></br><span style="color:#ff0000; font-size:11px;"> * </span>
 			</#if>
 			
		</div>	
		
		
		<div style="clear:both;"></div>	
	
		 	<a href="javascript:validateBarcode('${formAction}');"  class="buttontext"">${buttonName}</a>
 		 
		
		<#--input type="submit" value='Create Barcode' onclick="return validateBarcode();"-->
      </form>
      <br/>
      <#assign msg=request.getAttribute("msg")?if_exists>
       <#if msg?has_content>
       		<#list msg as message>
          		<div style="color:#ff0000; font-size:13px;">${message?if_exists}</div><br>
       		</#list>
       </#if>
       <div class="screenlet">
        <div class="screenlet-title-bar">
         <h3 class="screenlet-title-bar">Product Barcode</h3>
        </div>
        </div>          
       <table class="basic-table hover-bar" cellspacing='0'>
       	<tr class="header-row">
	        <td>Product Id</td>
	        <td>Product Name</td>
	        <td>Barcode</td>
      	</tr>
      	<#if productId?has_content>
      		<#assign prodBarcode  = delegator.findByAnd("ProductBarcode",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", productId?if_exists))/>
       		<#if prodBarcode?has_content>
       		<#list prodBarcode as barcode>
       			<tr>
       				<td>${barcode.productId?if_exists}</td>
	        		<td>${product.productName?if_exists}</td>
	        		<td>${barcode.barcode?if_exists}</td>
 	        		<td>   <a href="<@ofbizUrl>ProductBarcode?productId=${barcode.productId?if_exists}&barcode=${barcode.barcode?if_exists}</@ofbizUrl>"   class="buttontext">Update</a></td>
 	        		<td>   <a href="<@ofbizUrl>deleteProductBarcode?productId=${barcode.productId?if_exists}&productBarcode=${barcode.barcode?if_exists}</@ofbizUrl>"  class="buttontext" >Delete</a></td>
	        	</tr>	
       		</#list>
       		</#if>
      	</#if>
       </table>          
</body>
