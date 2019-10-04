<#if productPriceConds?has_content>
	<table>
		<tr>
			<td>
				<b><h3>Input</h3></b>
		    </td>
		    <td>
		    	<b><h3>Operator</h3></b>
		    </td>
		    <td>
		    	<b><h3>Value</h3></b>
		    </td>
		    <td>
		    	<b><h3>Delete</h3></b>
		    </td>
		</tr>
	<#list productPriceConds as productPriceCond>
		<tr>
			<td>
				<#assign inputEnum = delegator.findOne("Enumeration", {"enumId" : productPriceCond.inputParamEnumId}, true)>
				${inputEnum.description?if_exists}
		    </td>
		    <td>
		    	<#assign operatorEnum = delegator.findOne("Enumeration", {"enumId" : productPriceCond.operatorEnumId}, true)>
				${operatorEnum.description?if_exists}
		    </td>
		    <td>
		    	<#if productPriceCond.condValue?has_content>
			    	<#assign condValue = productPriceCond.condValue>
			    	<#if "PRIP_PRODUCT_ID" == productPriceCond.inputParamEnumId>
			    		<#assign product = delegator.findOne("Product", {"productId" : condValue}, true)>
			    		<#if product?has_content>
			    			${product.internalName?if_exists}
			    		</#if>
			    	</#if>
			    	<#if "PRIP_PROD_CAT_ID" == productPriceCond.inputParamEnumId>
			    		<#assign productCategory = delegator.findOne("ProductCategory", {"productCategoryId" : condValue}, true)>
			    		<#if productCategory?has_content>
			    			${productCategory.categoryName?if_exists}
			    		</#if>
			    	</#if>
		    	</#if>
		    </td>
		    <td>
		    	<form name="deleteProductPriceCond_${productPriceCond_index}" method= "post" action= "<@ofbizUrl>deleteProductPriceCond</@ofbizUrl>">
	                   <input type="hidden" name="productPriceRuleId" value="${productPriceCond.productPriceRuleId}" />
	                   <input type="hidden" name="productPriceCondSeqId" value="${productPriceCond.productPriceCondSeqId}" />
	                   <a href="javascript:document.deleteProductPriceCond_${productPriceCond_index}.submit()" class="buttontext">${uiLabelMap.CommonDelete}</a>
                 </form>
		    </td>
		</tr>
	</#list>
	</table>
</#if>