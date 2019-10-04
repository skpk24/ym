
<#if recipeWeekRes?has_content>
	<table >
		<tr>
			
			<td>
				<#if recipeWeekRes?has_content>
				    <span style="float:right;">
				    	<#assign statusValidChangeList  = delegator.findByAnd("StatusValidChange",
				    										Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId",recipeWeekRes.statusId))/>
						<#if statusValidChangeList?has_content>
							<#list statusValidChangeList as statusValidChange>
								<input onclick="changeRecipeResStatus('${statusValidChange.statusIdTo?if_exists}')" value="${statusValidChange.transitionName?if_exists}" type="button">
							</#list>
						</#if>
					</span>
			<br /><br />
					<form name="recipeWeekRes" action="changeRecipeWeekResStatus?recipeId=${recipeWeekRes.recipeManagementId?if_exists}" method="post" style="width:100%;">
						<input type="hidden" value="" name="statusIdTo" id="statusIdTo"/>
						<table>
							<tr>
								<td>
									Place :
								</td>
								<td>
									${recipeWeekRes.homeTown?if_exists}
					 			</td>
							</tr>
							<tr>
								<td>
									Cooking Knowledge :       
								</td>
								<td>
									${recipeWeekRes.cookingLevel?if_exists}
								</td>
							</tr>
							<tr>
								<td>
									Cooking Interests :
								</td>
								<td>
									${recipeWeekRes.cookingInterest?if_exists}
								</td>
							</tr>
							<tr>
								<td>
									Hobbies :
								</td>
								<td>
									${recipeWeekRes.hobbies?if_exists}
								</td>
							</tr>
							<tr>
								<td>
									Something about yourself :
								</td>
								<td>
									${recipeWeekRes.aboutChef?if_exists}
								</td>
							</tr>
							<tr>
								<td>
									Photo :
								</td>
								<td>
								 	<img src="${recipeWeekRes.photo?if_exists}"/>
								</td>
							</tr>
							<tr>
								<td>
									Are you sure to show your profile details in the site .
								</td>
								<td>
									${recipeWeekRes.shareProfile?if_exists}
								</td>
							</tr>
							<tr>
								<td>
									Are you OK to allow other members to write through the blog.
								</td>
								<td>
									${recipeWeekRes.allowComments?if_exists}
								</td>
							</tr>
							<tr>
								<td>
										I am aware that I have been selected at the sole discretion of the management of YouMart?
								</td>
								<td>
									${recipeWeekRes.termManagement?if_exists}
								</td>
							</tr>
							<tr>
								<td>
					 					I am in agreement to participate in the members BLOG during this week and will be happy to respond to 
					 					any queries from other members about my recipe or cooking related information.?
								</td>
								<td>
									${recipeWeekRes.termToRespond?if_exists}
								</td>
							</tr>
						</table>
					</form>
				</#if>
			</td>
		</tr>
	</table>
	<#else>
		No Resposnse found from customer ....
    </#if>

<script>
	function changeRecipeResStatus(statusIdTo){
		document.getElementById("statusIdTo").value= statusIdTo;
		document.recipeWeekRes.submit();
    }
</script>
