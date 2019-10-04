
	<#--
	 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
	 * 
	 * This program is free software; you can redistribute it and/or modify
	 * it under the terms of the Honest Public License.
	 * 
	 * This program is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 * Honest Public License for more details.
	 * 
	 * You should have received a copy of the Honest Public License
	 * along with this program; if not, write to Funambol,
	 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
	-->
	
	<script type="text/javascript">
	
		function dropDownUpdate(fieldName ,columnName ,tableName ,exportSeqIdFieldName ,exportCatalogSeqId)
		{ 
			new Ajax.Request("updateUploadedCatalog", {
				asynchronous: true,
				parameters: {columnName:columnName ,columnValue:$(fieldName).value ,tableName:tableName ,exportSeqIdFieldName : exportSeqIdFieldName ,exportCatalogSeqId :exportCatalogSeqId },
				onSuccess: function(transport) {
					var data = transport.responseText.evalJSON(true);
		            mylist = data.updateUploadedCatalog;
		            if(mylist.length > 0)
		            {
		            	$('result_'+fieldName).update("Updated");
		            }
		            else
		            {
		            	$('result_'+fieldName).update("Fail");
		            }
				}
			});
		}
		function deleteSelected(frm , columnName , tableName)
		{	
			var no_of_catalogs = 0;
			var parameter = '';
			var disableChekboxValue = '' ;
					
			if(frm.deleteThisData.length == undefined)
				{
					parameter = parameter +frm.deleteThisData.value+',';
					
					no_of_catalogs = no_of_catalogs+1;
					disableChekboxValue = disableChekboxValue +0+',';
				}
			else
			{	
				for (i = 0; i < frm.deleteThisData.length; i++)
				      if (frm.deleteThisData[i].checked)
				      {
				      	  if (document.getElementById("TR"+frm.deleteThisData[i].value).style.display !="none")
				      	  {
					      	  no_of_catalogs = no_of_catalogs+1;
					          parameter = parameter +frm.deleteThisData[i].value+',';
				          }
				          disableChekboxValue = disableChekboxValue +i+',';
				          
				      }  
				      
				        
			}
			if(no_of_catalogs != 0)
			{
				
				var confirmValue = '';
				if(no_of_catalogs == 1)
					confirmValue = confirmValue + "Are you sure to delete "+no_of_catalogs+" "+tableName.substring(10);
					
			    else
					confirmValue = confirmValue + "Are you sure to delete "+no_of_catalogs+" "+tableName.substring(10)+"s";
				var confirm=window.confirm(confirmValue);
				
				if (confirm)
				{
					
					parameter = parameter.slice(0,parameter.length-1);
					
					new Ajax.Request("delete", 
					{
						asynchronous: true,
						parameters:{columnName:columnName ,tableName:tableName ,columnValue :parameter},
						onSuccess:function(transport) 
						{
							var data = transport.responseText.evalJSON(true);
					        mylist = data.deleted;
					       
					        if(mylist.length > 0)
					        {
					        	var array = disableChekboxValue.split(",");
					        	
					        	
								for (j = 0; j < array.length-1; j++)
								{
									var v = parseInt(array[j]);
					          		document.getElementById("TR"+frm.deleteThisData[v].value).style.display ="none";
								}
					            	
					        }
						}
						
					});
				}
			}
			else
				{
					alert("please select the rows to delete");
				}
		}
		
		function clearUpdate(id)
		{
			
			document.getElementById(id).innerHTML = "";
		}
	
	checked=false;
	function checkedAll (checkId) {
		var aa= document.getElementById(checkId);
		 if (checked == false)
	          {
	           checked = true
	          }
	        else
	          {
	          checked = false
	          }
			for (var i =0; i < aa.elements.length; i++) 
			{
			 aa.elements[i].checked = checked;
			}
	      }
	
	function ValidateFileFormat(form, file) {
		
		extArray = new Array(".xls", ".xlsx", ".ods");
	    allowSubmit = false;
	    if (!file) return;
	    while (file.indexOf("\\") != -1)
	    file = file.slice(file.indexOf("\\") + 1);
	    ext = file.slice(file.indexOf(".")).toLowerCase();
	    for (var i = 0; i < extArray.length; i++) {
	    if (extArray[i] == ext) { allowSubmit = true; break; }
	    }
	    if (allowSubmit) return true;
	    else
	    alert("Please only upload files that end in types:  "
	    + (extArray.join("  ")) + "\nPlease select a new "
	    + "file to upload and submit again.");
	    return false;
	    
	 }
	 
	 function ValidateTotalUnimportedFields(totalUnimportedFields){
	 	<#--	if(totalUnimportedFields == 0){
	 			alert("Total UnImported Field is 0.Plz Upload First Then Import!!");
	 			return false;
	 		}
	 		else
	 			return true; -->
	 } 
	 
	 
	 function ValidateContactForm(catalogUploadForm)
	{   
		
	    if (document.catalogUploadForm.catalogFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.catalogUploadForm.catalogFileName.focus();
	        return false;
	    }
	    
	    file = document.catalogUploadForm.catalogFileName.value;
	    isFormatValid = ValidateFileFormat(catalogUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    
	    return true;
	}
	    
	    
	    
	function ValidatecategoryUploadForm(categoryUploadForm)
	{   
	    if (document.categoryUploadForm.categoryFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.categoryUploadForm.categoryFileName.focus();
	        return false;
	    }
	    
	    file = document.categoryUploadForm.categoryFileName.value;
	    isFormatValid = ValidateFileFormat(categoryUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	    
	function ValidatefeatureUploadForm(featureUploadForm)
	{   
	    if (document.featureUploadForm.featureFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.featureUploadForm.featureFileName.focus();
	        return false;
	    }
	    
	    file = document.featureUploadForm.featureFileName.value;
	    isFormatValid = ValidateFileFormat(featureUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	function ValidateproductUploadForm(productUploadForm)
	{   
	    if (document.productUploadForm.productFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.productUploadForm.productFileName.focus();
	        return false;
	    }
	    
	    file = document.productUploadForm.productFileName.value;
	    isFormatValid = ValidateFileFormat(productUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	function ValidatevariantProductUploadForm(variantProductUploadForm)
	{   
	    if (document.variantProductUploadForm.variantProductFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.variantProductUploadForm.variantProductFileName.focus();
	        return false;
	    }
	    
	    file = document.variantProductUploadForm.variantProductFileName.value;
	    isFormatValid = ValidateFileFormat(variantProductUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	    
	    
	    function ValidateProductInventoryUploadForm(productInventoryUploadForm)
	    {   
	    if (document.productInventoryUploadForm.productInventoryFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.productInventoryUploadForm.productInventoryFileName.focus();
	        return false;
	    }
	    
	    file = document.productInventoryUploadForm.productInventoryFileName.value;
	    isFormatValid = ValidateFileFormat(productInventoryUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	    
	    
	    function ValidateProductSupplierUploadForm(productSupplierUploadForm)
	    {   
	    if (document.productSupplierUploadForm.productSupplierFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.productSupplierUploadForm.productSupplierFileName.focus();
	        return false;
	    }
	    
	    file = document.productSupplierUploadForm.productSupplierFileName.value;
	    isFormatValid = ValidateFileFormat(productSupplierUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	    
	    
	    
	    
	function ValidateskuIdUploadForm(skuIdUploadForm)
	{   
	    if (document.skuIdUploadForm.skuIdFileName.value == "")
	    {
	       alert("Please select your file.");
	       document.skuIdUploadForm.skuIdFileName.focus();
	        return false;
	    }
	    
	    file = document.skuIdUploadForm.skuIdFileName.value;
	    isFormatValid = ValidateFileFormat(skuIdUploadForm, file);
	    if(isFormatValid==false)
	    	return false;
	    	
	    return true;
	    }
	    
	    function openDiv(){
	   	var temp = document.getElementById("showProd").style.display
	    if( temp == "none"){
	    document.getElementById("showProd").style.display = "block"
	    	$('#nameChange').text('Hide Total Unimported');
 	    } else{
	      document.getElementById("showProd").style.display = "none"
	      	$('#nameChange').text('Show Total Unimported');
 	    }
 }
	</script>
	
	
	<#assign x = 1>
	
	
<div class="screenlet">
    <div class="screenlet-title-bar">
			<ul>
				<li class="h3">&nbsp; Daily Products Update</li>
			</ul>
			<br class="clear">
		</div>
    <div class="screenlet-body">
    	<table width="100%" cellspacing="0" bgcolor="#76ACC8" class="basic-table">
		<tr bgcolor="#FFFFFF" height="25px">
		<td class="tabletext"><b>Daily Products Update</b></td>
		<td><strong>Total records Uploaded: <#if dataImportFromExcel?has_content>${dataImportFromExcel.size()}<#else>0</#if></strong></td>
		</tr>
		<tr  bgcolor="#FFFFFF" height="25px">
		 <td  class="tabletext"><b>Import Product</b></td>
		 <td><a href="<@ofbizUrl>dailyUpdate?bit=1</@ofbizUrl>" class="buttontext">Daily Update Product</a></td>
		  <#if dataImportFromExcel?has_content && dataImportFromExcel?size gt 0>
		 <td><a href="javascript:openDiv()" class="buttontext" id="nameChange">Show Total Unimported</a></td>
		 </#if>
  		</tr>
		<#if minutes?exists>
		<tr  bgcolor="#FFFFFF" height="25px">
		<td  class="tabletext"> Total Time In Minutes / Second Taken </td>
		<td> ${minutes} m  /  ${totalInSecond} s </td> 
		</tr>
		</#if>
		<tr bgcolor="#DAE4E6" height="25px">
	<td class="tabletext2" width="150px">${updatedProductsfromExcel?if_exists}</td>
		
		</tr>
		<tr>
			<td colspan="3"><hr/></td>
		</tr>
		 <tr bgcolor="#FFFFFF" height="30px">
		<td class="tabletext"> Update Product Data</td>
		<td class="tabletext" colspan="2">
			   <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadExcelProducts?upload_file_type=productExcel</@ofbizUrl>"  name="excelUploadForm">
		        <div>
		            <table>
			         <tr>
			        <td><input type="file" size="75" name="excelorderFileName" id="excelFileName" onchange="this.form.txt1.value=this.form.excelFileName.value;">
			           <input type="hidden" name="txt1" value="Browse for a file..." class="txt1" readonly="readonly">
			           <input type="submit" class="main_buttontextlogin" value=" Upload excel">
			          <a href="<@ofbizContentUrl>/images/xls_format/productExcel.xls</@ofbizContentUrl>" title="Get order Upload Format sheet"><img src="<@ofbizContentUrl>/images/images/csv_icon.jpg</@ofbizContentUrl>" border="0" width="15" align="absbottom" height="15"></a>
			        </td>
			        </tr>
			      </table>  
		        </div>
          </form>
		    
		     </td>
		     </tr>
		</table>
		<br/>
</div>
<script>
$(document).ready(function(e) {
   //auto Refresh
   //var refresher = setInterval("update_content();",10000); // 30 seconds
});

function update_content(){
   //alert("asD");
}
</script>
 
			 <#if dataImportFromExcel?has_content && dataImportFromExcel?size gt 0>
			<div id="showProd" class="screenlet-body" style="display:none">
				 
					<table border="7">	
						<tr>
							<th>articleNumber</th>
							<th>Selling Price</th>
							<th>Vat Percentage</th>
							<th>Mrp Price</th>
							<th>Stock</th>
							<th>QOH</th>
							<th>Message</th>
							<th>Delete</th>
						</tr>	
 					<#list dataImportFromExcel as RUProduct>
						<tr>
 							<td>${RUProduct.articleNumber?if_exists}</td>
							<td>${RUProduct.sellPrice?if_exists}</td>
							<td>${RUProduct.vatPerc?if_exists}</td>
							<td>${RUProduct.mrpPrice?if_exists}</td>
							<td>${RUProduct.stock?if_exists}</td>
							<td>${RUProduct.qoh?if_exists}</td>
							<td>${RUProduct.message?if_exists}</td>
						 <td><a href="<@ofbizUrl>dailyUpdate?uploadDataImportSeqId=${RUProduct.uploadDataImportSeqId?if_exists}</@ofbizUrl>" class="buttontext">Delete Uploaded Product</a></td>
							
 						</tr>
					</#list>
					</table>
				</div>
 		</#if>
	<br/><br/>
	
	
	
	<div class="screenlet">
	    <div class="screenlet-title-bar">
				<ul>
					<li class="h3">&nbsp;Upload Aisle Number</li>
				</ul>
				<br class="clear">
			</div>
	    <div class="screenlet-body">
	    	<table width="100%" cellspacing="0" bgcolor="#76ACC8" class="basic-table">
			<tr bgcolor="#FFFFFF" height="25px">
			
			<td ></td>
			</tr>
			
			<tr>
				<td colspan="3"><hr/></td>
			</tr>
			 <tr bgcolor="#FFFFFF" height="30px">
			
			<td class="tabletext" colspan="2">
				   <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadAisleProducts?upload_file_type=productAisleExcel</@ofbizUrl>"  name="excelUploadForm">
			        <div>
			            <table>
				         <tr>
				        <td><input type="file" size="75" name="excelorderFileName" id="excelFileName" onchange="this.form.txt1.value=this.form.excelFileName.value;">
				           <input type="hidden" name="txt1" value="Browse for a file..." class="txt1" readonly="readonly">
				           <input type="submit" class="main_buttontextlogin" value=" Upload excel">
				         <a href="<@ofbizContentUrl>/images/xls_format/productAisleExcel.xls</@ofbizContentUrl>" title="Get order Upload Format sheet"><img src="<@ofbizContentUrl>/images/images/csv_icon.jpg</@ofbizContentUrl>" border="0" width="15" align="absbottom" height="15"></a>
				         			        </td>
				        </tr>
				      </table>  
			        </div>
	          </form>
			    
			     </td>
			     </tr>
			</table>
			<br/>
	</div>
	
	
	<div class="screenlet">
	    <div class="screenlet-title-bar">
				<ul>
					<li class="h3">&nbsp;Upload Images</li>
				</ul>
				<br class="clear">
			</div>
	    <div class="screenlet-body">
	    	<table width="100%" cellspacing="0" bgcolor="#76ACC8" class="basic-table">
			<tr bgcolor="#FFFFFF" height="25px">
			
			<td ></td>
			</tr>
			
			<tr>
				<td colspan="3"><hr/></td>
			</tr>
				<tr bgcolor="#FFFFFF" height="30px">
				
						<td class="tabletext" colspan="2">
								<form id="imageUpload"  method="post"  action="imageUplaod"  enctype="multipart/form-data"> 
									<table border="1"> 
										<tr>
												<td>
													<input type="radio" name="imageType" value="small"/>small
													<input type="radio" name="imageType" value="large"/>large
													<input type="radio" name="imageType" value="detail"/>detail
													<input type="radio" name="imageType" value="medium"/>medium
												</td>
										</tr>
										<tr>
											<td> <h4>File to be uploaded </h4></td>
											<td> <input type="file" name="txtFile" id="txtFile"	multiple/></td>
										</tr> 
										<tr>
											<td align="center" colspan="2"> <input type="submit" id="submitID" name="submit" value="Upload" /></td>
										</tr> 
									</table> 
								</form> 
					    </td>
				  </tr>
			</table>
			<br/>
	</div>
	
	<div class="screenlet">
	    <div class="screenlet-title-bar">
				<ul>
					<li class="h3"> New Product Upload</li>
					<li class="collapsed"><a onclick="javascript:toggleScreenlet(this, 'hsr1007', 'Expand', 'Collapse');" title="Expand">&nbsp;</a></li>
				</ul>
				<br class="clear">
			</div>
	    <div class="screenlet-body">
	    	<table width="100%" cellspacing="0" bgcolor="#76ACC8" class="basic-table">
			<tr bgcolor="#FFFFFF" height="25px">
			<td class="tabletext"><b>${uiLabelMap.TotalImportedProduct}</b></td>
			<td class="tabletext"><b>${uiLabelMap.TotalUnImportedProduct}</b></td>
				<td><strong>Total records  : <#if DataImportProduct?has_content>${DataImportProduct.size()}<#else>0</#if></strong></td>
			<td></td>
			</tr>
			<tr>
				<td colspan="3"><hr/></td>
			</tr>
			<tr bgcolor="#DAE4E6" height="25px">
			<td class="tabletext2" width="150px"></td>
			<td class="tabletext2" width="40px"></td>
			<td><a href="<@ofbizUrl>importProduct</@ofbizUrl>" class="buttontext" onClick = "return ValidateTotalUnimportedFields(${unimportedProduct})">${uiLabelMap.ImportProduct}</a></td>
			</tr>
			  <#if requestAttributes.message?has_content>
   			<#--	<tr><td style="text-align:center !important;"><b style="font-size:16px; font-weight:normal;"><#if requestAttributes.message.errorRowNo?if_exists?length == 0>Successfully uploaded ${requestAttributes.message.successCount?if_exists} number of products<#else>${requestAttributes.message.errorRowNo?if_exists}</#if></b></td></tr> -->
   				</#if>
			 <tr bgcolor="#FFFFFF" height="30px">
			  
			<td class="tabletext">${uiLabelMap.UploadProductData}</td>
			<td class="tabletext" colspan="2">
				 <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>uploadProduct?upload_file_type=product</@ofbizUrl>" name="productUploadForm" onsubmit="return ValidateproductUploadForm(productUploadForm)">
			        <div>
				        <input type="file" size="75" name="productFileName" onchange="this.form.txt1.value=this.form.productFileName.value;">
				        <input type="hidden" name="txt1" value="Browse for a file..." class="txt1" readonly="readonly">
				        <input type="submit" class="main_buttontextlogin" value="${uiLabelMap.UploadProduct}">
				        <a href="<@ofbizContentUrl>/images/xls_format/product.xls</@ofbizContentUrl>" title="Get Product Upload Format sheet"><img src="<@ofbizContentUrl>/images/images/csv_icon.jpg</@ofbizContentUrl>" border="0" width="15" height="15"></a>
			        </div>
			      </form>
			     </td>
			</tr>
			</table>
			<br/>
	<br/><br/>
	
