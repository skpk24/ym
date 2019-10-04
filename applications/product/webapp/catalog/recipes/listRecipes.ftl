
<script>
	function changeRecipeStatus(statusIdTo){
		var checkboxes = document.getElementsByName("recipeManagementId");
		var count = 0;
		for (var i=0; i<checkboxes.length; i++) {
		     if (checkboxes[i].checked) {
		        count++;
		     }
		  }
		if(statusIdTo == "RECIPE_OF_WEEK" && count != 1)
		{
			alert("Please select one Recipe Of The Week ");
			return false;
		}
		document.getElementById("statusIdTo").value= statusIdTo;
		document.recipeList.submit();
    }
    
    checked = false;
    function checkedAll(a){
	if (checked == false){checked = true}else{checked = false}
	for (i = 0; i < a.form.length; i++) {
		a.form[i].checked = checked;
	}
	document.getElementById("generateAll").value = "";
}
</script>

<#if recipeList?has_content>
	<form name="recipeList" action="changeRecipeStatus" method="post">
	<table class="basic-table hover-bar dark-grid" cellpadding="0">
		<tr align="left">
			
			<td colspan="5">
				<#assign statusValidChangeList  = delegator.findByAnd("StatusValidChange",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId",parameters.statusId))/>
				<#if statusValidChangeList?has_content>
					<#list statusValidChangeList as statusValidChange>
						<#--assign statusItem = delegator.findOne("StatusItem", {"statusId":statusValidChange.statusIdTo}, true)?if_exists-->
						<input onclick="changeRecipeStatus('${statusValidChange.statusIdTo?if_exists}')" value="${statusValidChange.transitionName?if_exists}" type="button">
					</#list>
				</#if>
			</td>
		</tr>
		<tr class="header-row-2">
			<td>
				<input onclick="checkedAll(this)" value="Select All" type="button">
				<input type="hidden" value="" name="statusIdTo" id="statusIdTo"/>
				<#if parameters.statusId?has_content>
					<input type="hidden" value="${parameters.statusId}" name="statusId"/>
				<#else>
					<input type="hidden" value="RECIPE_APPROVED" name="statusId"/>
				</#if>
			</td>
			<td>
				Recipe Id
			</td>
			<td>
				Recipe Name
			</td>
			<td>
				Status
			</td>
			<td>
				Created By
			</td>
			<td>
				Created Date
			</td>
		</tr>
			<#assign counter = 1>
			<#list recipeList as recipe>
				<tr <#if counter%2 == 0>class="alternate-row"</#if>>
				<#assign counter = counter+1>
					<td>
						<div id="checkbox">
		              		<input type="checkBox" id="checkBox${recipe.recipeManagementId?if_exists}" name="recipeManagementId" value="${recipe.recipeManagementId?if_exists}"/>
		                </div>
					</td>
					<td><a href="<@ofbizUrl>recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}</@ofbizUrl>">${recipe.recipeManagementId?if_exists}</a></td>
					<td>${recipe.recipeName?if_exists}</td>
					<td>
					<#assign statusItem = delegator.findOne("StatusItem", {"statusId":recipe.statusId}, true)?if_exists>
						${statusItem.description?if_exists}
					</td>
					<td>${recipe.createdBy?if_exists}</td>
					<td><#if recipe.createdDate?has_content>${recipe.createdDate?string("dd-MM-yyyy")?if_exists}</#if></td>
				<tr/>
			</#list>
		</form>
	</table>
<#else>
	No Recipe is found ...
</#if>
