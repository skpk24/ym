
<div class="recipelist" id="typeShow">
<#if recipeTypeMangList?has_content>
	<#assign count = 1>
	 <#list recipeTypeMangList as recipe>
	 	
	 	 	<div class="recipelist-left">
			    <h1>${count?if_exists} ) ${recipe.recipeName?if_exists}</h1>
			       <#-- ${recipe.createdDate?if_exists}<br /><br />-->
			       <img src="${recipe.recipeImgUrl?if_exists}" style="width:170px; height:133px; float:left;"/>
		    </div>
		    <div class="recipelist-right">
				<h1>Method</h1>
				<p class="recipedes">${recipe.description?if_exists}</p>
				<p style="text-align:right; padding-right:40px;"><a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}">Read More....</a></p>
				<strong>Recipe by:${recipe.createdBy?if_exists}</strong><br /><br />
				<a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}" class="buttontextblue">View Detail</a><br /><br />
			</div>
		    
		<#assign count = count+1>
	 </#list>
</#if>

</div>