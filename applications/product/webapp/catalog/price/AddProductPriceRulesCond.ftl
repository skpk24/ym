<table>
	<form method="post" action="<@ofbizUrl>createBulkProductPriceCond</@ofbizUrl>" name="productPriceCondForm">
	<input type="hidden" name="productPriceRuleId" value="${parameters.productPriceRuleId?if_exists}" />
	<tr>
	    <td>
            <select name="inputParamEnumId" size="1" onchange="toggle(this.value)">
            	<option value="PRIP_PRODUCT_ID">Product</option>
                <option value="PRIP_PROD_CAT_ID">Product Category</option>
            </select>
       	</td>
    </tr>
    <tr>
       	<td>
	            <select name="operatorEnumId" size="1">
	                <#list condOperEnums as condOperEnum>
	                  	<option value="${condOperEnum.enumId}">${condOperEnum.get("description",locale)}<#--[${condOperEnum.enumId}]--></option>
	                </#list>
	            </select>
	    </td>
	</tr>
    <tr>
	    <td>
	            <div id="productId">
	            	<@htmlTemplate.lookupField value="" formName="productPriceCondForm" name="productId" id="productJumpFormProductId" fieldFormName="LookupProduct"/>
    			</div>
    	</td>
   	</tr>
    <tr>
    	<td>
    			<div id="productCategoryId" style="display:none">
    				<@htmlTemplate.lookupField value="" formName="productPriceCondForm" name="productCategoryId" id="searchCategoryId" fieldFormName="LookupProductCategory"/>
	            </div>
	            <input type="submit" value="${uiLabelMap.CommonCreate}" id="submitId"/>
	        </form>
	    </td>
	</tr>
</table>

<script>
	function toggle(value){
		if("PRIP_PRODUCT_ID" == value)
		{
			document.getElementById("productId").style.display = 'block';
			document.getElementById("productCategoryId").style.display = 'none';
			document.getElementById("submitId").value = "Create";
			document.productPriceCondForm.action = "createBulkProductPriceCond";
		}else if("PRIP_PROD_CAT_ID" == value)
		{
			document.getElementById("productId").style.display = 'none';
			document.getElementById("productCategoryId").style.display = 'block';
			document.getElementById("submitId").value = "Get Products";
			document.productPriceCondForm.action = "getProductsForPriceRuleCond";
		}else{
			document.productPriceCondForm.action = "createProductPriceCond";
		}
	}
</script>