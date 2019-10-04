<form name="myform" action="<@ofbizUrl>ProductImportSheet</@ofbizUrl>" method="post">
	 <table cellspacing="0" align="center" width="100%" style="padding-bottom:20px;">
	     <tr>
	     	<td colspan="2"><h3>Product upload</h3></td>
	     </tr>
		 <tr>
			 <td width="137px">Select The Product</td>
			 <td>
				 <input type="text" size="60" name="variantName" list="variantName"/>
				   <datalist name="variantName"  id="variantName">
				   <#if ProductList?has_content>
						<#list ProductList as ProductList1>
							<option value="${ProductList1.productName?if_exists}">${ProductList1.productName?if_exists} ---[${ProductList1.brandName?if_exists}] </option>
						</#list>
				   </#if>
			 </td>
		  </tr>
		  <tr>
			 <td>Enter Virtual Product</td>
			 <td>
			     <input type="text" size="60" name="productName" id="productName"/>
			 </td>
		  </tr>
		  <tr>
		     <td>&nbsp;</td>
			 <td>
			    <input type="submit" value="submit" >
			 </td>
		  </tr>
	 </table>   
</form>

 <#if productvariantList?has_content>
<form name="myform" action="<@ofbizUrl>ProductImportAction</@ofbizUrl>" method="post">
	 <table cellspacing="0" align="center" width="100%" style=" padding-bottom:20px; margin-top:20px;">
		<tr>
			<td colspan="2"><h3>Virtual Product Properties</h3></td>
		</tr>
		<tr>
			<td colspan="2"><table>
			   <#if productvariantList?has_content>
					<#list productvariantList as productvariantList1>
					<tr>
						<td>
						  <input type="radio" name="radioVariantId" value="${productvariantList1.productId?if_exists}"/>[${productvariantList1.brandName?if_exists}] ${productvariantList1.productName?if_exists}
						</td>
					</tr>
					</#list>
				</#if>
				</table>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td colspan="2"><h3>Pick the Variant Product</h3></td>
		</tr>
		<tr>
			<td colspan="2"><table>
		        <#if productvariantList?has_content>
				<#list productvariantList as productvariantList1>
				<tr>
					<td>
					     <input type="checkbox" name="checkProductVariants" value="${productvariantList1.productId?if_exists}" checked="checked"/>[${productvariantList1.brandName?if_exists}] ${productvariantList1.productName?if_exists}
				    </td>
				</tr>
				</#list>
				</#if>
				</table>
			</td>
		</tr>
		<tr>
			 <td width="100px">isVirtual</td>
			 <td><input type="checkbox" name="isVirtual" value="N" /></td>
		</tr>
		<tr> 
		     <td>isVariant</td>
			 <td><input type="checkbox" name="isVariant" value="N" /></td>
		</tr>
		<tr>
		     <td colspan="2">	 
			     <input type="hidden" name="productName" value="${productName?if_exists}"/>
			 </td>
		</tr>
		<tr>
			 <td colspan="2">
			     <input type="submit" value="submit" >
			 </td>
		</tr>
	 </table>   
</form>
</#if>
 <table cellspacing="0" align="center" width="100%" style=" padding-bottom:20px; margin-top:20px;">
		<!--<tr>
			<td colspan="2"><h3>Number of uploading product</h3></td>
			<td>${productUpload?if_exists}</td>
		</tr>
		<tr>
			<td colspan="2"><h3>Number of Importing product</h3></td>
			<td>${productImport?if_exists}</td>
		</tr>
		<tr>
			<td colspan="2"><h3>Number of Uploading variant</h3></td>
			<td>${variantUpload?if_exists}</td>
		</tr>
		<tr>
			<td colspan="2"><h3>Number of Importing variant</h3></td>
			<td>${variantImport?if_exists}</td>
		</tr>
		<tr>
			<td colspan="2"><h3>Number of Uploading Feature</h3></td>
			<td>${featureUpload?if_exists}</td>
		</tr>
		<tr>
			<td colspan="2"><h3>Number of Importing Feature</h3></td>
			<td>${featureImport?if_exists}</td>
		</tr> -->
		
		
		<tr>
			<td colspan="2"><h3>Number of Importing original product</h3></td>
			<td>${originalImport?if_exists}</td>
		</tr> 

<form name="myform" action="<@ofbizUrl>ProductUploadAction</@ofbizUrl>" method="post">
  <input type="submit" value="Import Product" >
</form>
<form name="myform" action="<@ofbizUrl>ProductVariantUploadAction</@ofbizUrl>" method="post">
  <input type="submit" value="Import Variant" >
</form>
<script>
/*function getProduct()
{
 
    var element = document.createElement("input");
 
   if( document.getElementById("productName"))
	foo.parentNode.removeChild(foo);
    element.setAttribute("type", "text");
   
    element.setAttribute("name", "productName");
  	element.setAttribute("id", "productName");
 
    var foo = document.getElementById("fooBar");
 
   
    foo.appendChild(element);*/



}
</script>