<#assign statusTypeList  = delegator.findByAnd("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId","RECIPE_COMM_TYPE"))/>
<#assign statusIdList  = delegator.findByAnd("StatusItem",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId","RECIPE_COMM_STATUS"))/>

				<#if statusValidChangeList?has_content>
					<#list statusValidChangeList as statusValidChange>
						<#--assign statusItem = delegator.findOne("StatusItem", {"statusId":statusValidChange.statusIdTo}, true)?if_exists-->
						<input onclick="changeRecipeStatus('${statusValidChange.statusIdTo?if_exists}')" value="${statusValidChange.transitionName?if_exists}" type="button">
					</#list>
				</#if>
	<table>
		<form action="recipeDetail" method="get" style="width:100%;">
		<input type="hidden" name="recipeId" value="${parameters.recipeId?if_exists}"/>
		<input type="hidden" name="from" value="admin"/>
			<tr>
				<td>
					Type : 
				</td>
				<td>
					<select name="type">
						<#if statusTypeList?has_content>
							<#list statusTypeList as statusType>
								<option value="${statusType.statusId?if_exists}"
								<#if parameters.type?has_content && parameters.type == statusType.statusId>
									selected = "selected"
								</#if>>${statusType.description?if_exists}</option>
							</#list>
						<#else>
						</#if>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					Status : 
				</td>
				<td>
					<select name="statusId">
						<#if statusIdList?has_content>
							<#list statusIdList as status>
								<option value="${status.statusId?if_exists}"
								<#if parameters.statusId?has_content && parameters.statusId == status.statusId>
									selected = "selected"
								</#if>>${status.description?if_exists}</option>
							</#list>
						<#else>
						</#if>
					</select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="submit" value="Find"/>
				</td>
			</tr>
		</form>
	</table>

<#if recipeCommentList?has_content>
	<table>
		<tr>
			
			<td>
			    <span style="float:right;">
			            <input onclick="changeRecipeCommentStatus('RECP_COMM_APPROVED')" value="Approve" type="button">
						<input onclick="changeRecipeCommentStatus('RECP_COMM_REJECTED')" value="Reject" type="button">
				</span>
			<br /><br />
				<#if recipeCommentList?has_content>
					<form name="recipeCommentList" action="changeRecipeCommentStatus?recipeId=${recipe.recipeManagementId?if_exists}" method="get" style="width:100%;">
						<input type="hidden" value="" name="statusIdTo" id="statusIdTo1"/>
						<table class="basic-table hover-bar dark-grid" cellspacing="0">
							<tr class="header-row-2">
								<td>Review Date</td>
								<td>Review By</td>
								<td>Rating</td>
								<td>Review</td>
								<td>Status</td>
								<td><input onclick="checkedAll(this)" value="Select All" type="button"></td>
							</tr>
						<#assign counter = 1>
						<#list recipeCommentList as recipeComment>
							<tr <#if counter%2 == 0>class="alternate-row"</#if>>
							<#assign counter = counter+1>
								<td>${recipeComment.createdDate?if_exists}</td>
								
								<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipeComment.createdBy)?if_exists>
								<#if !createdBy?has_content>
									<#assign createdBy = recipe.createdBy>
								</#if>
								
								<td>${createdBy?if_exists}</td>
								<td>${recipeComment.rating?if_exists}</td>
								<td>${recipeComment.message?if_exists}</td>
								<#assign statusItem = delegator.findOne("StatusItem", {"statusId":recipeComment.statusId}, true)?if_exists>
								<td>${statusItem.description?if_exists}</td>
								<td>
									<input type="checkBox" id="checkBox${recipeComment.recipeCommentId?if_exists}" name="commentId" value="${recipeComment.recipeCommentId?if_exists}"/>
								</td>
							</tr>
							<#assign subCommentList = Static["org.ofbiz.recipes.RecipeEvents"].adminSubCommentList(delegator,recipeComment.recipeCommentId,parameters.type,parameters.statusId)?if_exists>
							<#if subCommentList?has_content>
							      <#assign counter = 0>
									<#list subCommentList as subComment>
									<tr <#if counter%2 == 0>class="alternate-row"</#if>>
									<#assign counter = counter+1>
										<td>${subComment.createdDate?if_exists}</td>
										
										<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipeComment.createdBy)?if_exists>
										<#if !createdBy?has_content>
											<#assign createdBy = recipe.createdBy>
										</#if>
										
										<td>${createdBy?if_exists}</td>
										<td>${subComment.rating?if_exists}</td>
										<td>${subComment.message?if_exists}</td>
										<#assign statusItem = delegator.findOne("StatusItem", {"statusId":subComment.statusId}, true)?if_exists>
										<td>${statusItem.description?if_exists}</td>
										<td>
											<input type="checkBox" id="checkBox${recipeComment.recipeCommentId?if_exists}" name="commentId" value="${subComment.recipeCommentId?if_exists}"/>
										</td>
									</tr>
									</#list>
							</#if>
							
						</#list>
						</table>
					</form>
				</#if>
			</td>
		</tr>
	</table>
	<#else>
		No Comments found ....
    </#if>

<script>
	function changeRecipeCommentStatus(statusIdTo){
		document.getElementById("statusIdTo1").value= statusIdTo;
		document.recipeCommentList.submit();
    }
    checked = false;
    function checkedAll(a){
	if (checked == false){checked = true}else{checked = false}
	for (i = 0; i < a.form.length; i++) {
		a.form[i].checked = checked;
	}
	}
</script>
