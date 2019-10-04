<#include "AddProductPriceRulesCond.ftl"/>
<b><h3><a href="<@ofbizUrl>EditProductPriceRulesNew?productPriceRuleId=${parameters.productPriceRuleId}</@ofbizUrl>" class="buttontext">Back</a></h3></b>
<#if productCategoryMembers?has_content>
	<div class="screenlet-title-bar">
        <h3>All Products</h3>
	</div>
	<form method="post" action="<@ofbizUrl>createBulkProductPriceCond</@ofbizUrl>">
		<input type="submit" value="Create All"/>
        <input type="hidden" name="productPriceRuleId" value="${parameters.productPriceRuleId}"/>
        <input type="hidden" name="inputParamEnumId" value="PRIP_PRODUCT_ID"/>
        <input type="hidden" name="operatorEnumId" value="${parameters.operatorEnumId?if_exists}"/>
        <input type="hidden" name="totalNoOfProducts" value="${productCategoryMembers.size()?if_exists}"/>
        <input type="hidden" name="productCategoryId" value="${parameters.productCategoryId?if_exists}"/>
		<table>
			<tr>
				<td>
			    </td>
			    <td>
			    	<b><h3>Product Id</h3></b>
			    </td>
			    <td>
			    	<b><h3>Brand Name</h3></b>
			    </td>
			    <td>
			    	<b><h3>Internal Name</h3></b>
			    </td>
			</tr>
			<#list productCategoryMembers as productCategoryMember>
				<tr>
					<td>
				    	<input type="checkbox" checked="true" name="productId" value="${productCategoryMember.productId?if_exists}">
				    </td>
				    <td>
				    	<b>${productCategoryMember.productId?if_exists}</b>
				    </td>
				    <td>
				    	${productCategoryMember.brandName?if_exists}
				    </td>
				    <td>
				    	${productCategoryMember.internalName?if_exists}
				    </td>
				</tr>
			</#list>
		</table>
		<input type="submit" value="Create"/>
	</form>
<#else>
	<b><h3>No Products found</h3></b><br/>
</#if>