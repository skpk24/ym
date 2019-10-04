<link href="/erptheme1/star/rating_simple.css" rel="stylesheet" type="text/css">
<#if recipe?has_content && recipeWeek?has_content>
 <div style="width:1024px; margin-top:20px;">
<h2 class="recipeweek">Chef Of The Week</h2> <a href="<@ofbizUrl>recipeOfTheWeek?recipeId=${recipeWeek.recipeManagementId?if_exists}</@ofbizUrl>" class="buttontext" style="float:right;margin-top:-55px;">Recipe Details</a>
	<div style="float:left; width:300px; padding-top:50px;">
	<#if recipeWeek?has_content && recipeWeek.photo?has_content>
		<img src="${recipeWeek.photo?if_exists}" width="172px" height="180px" style="padding-bottom:5px;"/>
	<#else>
       <img src="/erptheme1/photonotavailable.jpg" width="172px" height="180px" style="padding-bottom:5px;"/>
           </#if>	
	</div>

	<div style="float:right; width:620px;">
		<#if recipeWeek?has_content>
		<table width="100%" align="center" cellpadding="5" cellspacing="5">
			<tr>
				<td style="background:#c2d69a;" colspan="3">Chef's Profile</td>
			</tr>
			<tr>
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr>
				<td width="20%">Name</td>
				<td width="10%;"><span>:</span></td>	
				<td width="60%">${name?if_exists}</td>
			</tr>
			<tr>
				<td>Home Town</td>
				<td><span>:</span></td>
				<td>${recipeWeek.homeTown?if_exists}</td>
			</tr>
			<tr>
				<td>Cooking Level</td>
				<td><span>:</span></td>
				<td>${recipeWeek.cookingLevel?if_exists}</td>
			</tr>
			<tr>
				<td>Cooking Interests</td>
				<td><span>:</span></td>
				<td>${recipeWeek.cookingInterest?if_exists}</td>
			</tr>
			<tr>
				<td>Hobbies</td>
				<td><span>:</span></td>
				<td>${recipeWeek.hobbies?if_exists}</td>
			</tr>
		</table>
		</#if>
	</div>
	<div class="clear"></div>

 </div>
 		
 <div style="clear:both;"></div>
 <div style="width:1024px; margin:10px 0 0 0;">
			<div style="width:30%; float:left; padding-bottom:10px;">
				<span style="background:#c2d69a; display:block; padding:6px; font-weight:normal;">About the Chef (Note from ${name?if_exists})</span>
				<div style="padding:10px 0 0 5px;"><#if recipeWeek?has_content>${recipeWeek.aboutChef?if_exists}</#if></div>
			</div>
			<div style="width:60%; float:right; padding-bottom:10px;">
				<span style="background:#c2d69a; display:block; padding:6px; font-weight:normal;">Write Blog to the 'Chef of the Week'</span><br />
					 <form name="recipeComments" action="<@ofbizUrl>addComment</@ofbizUrl>" method="post">
					 				<input type="hidden" name="recipeId" value="${recipe.recipeManagementId?if_exists}">
					 				<input type="hidden" name="donePage" value="displayrecipeWeek">
					 				<input type="hidden" name="type" value="RECIPE_COMM_TYPE_BLO">
					 	<table style="width:390px;">
					 	    <tr>
					 			<td>Name</td>
					 			<td>:</td>
					 			<td><span style="margin-left:4px;">${loginName?if_exists}</span></td>
					 		</tr>
					 		<tr>
					 			<td>Message</td>
					 			<td>:</td>
					 			<td><textarea name="message" rows="4" cols="50" value="" style="border:1px solid #999999;margin-left:3px;width:313px;" id="message_id"></textarea></td>
					 		</tr>
					 		<tr>
					 			<td>&nbsp;</td>
					 			<td>&nbsp;</td>
					 			<td><input type="submit" name="add" Value="Add" onclick="return reviewComment();"/></td>
					 		</tr>
					 	</table>
					 </form>
				<#if recipeCommentList?has_content>
					<div style="float:left; margin:10px 0;width:613px;">
					     <h2 style="font-size:14px; margin:8px 0 5px 0; padding:8px 5px 8px 5px;background-image:url(../erptheme1/refined-bg.jpg); background-position:left; background-repeat:repeat-x;border-bottom:1px solid #c2c2c2;">Blog</h2>
						 <div style="float:left; height:auto; font-size:12px; width:600px;padding:5px;">
							   <#assign count = 1>
								<#list recipeCommentList as recipeComment>
									<div class="comment" style="float;left;">
												<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipeComment.createdBy)?if_exists>
												<#if !createdBy?has_content>
													<#assign createdBy = recipe.createdBy>
												</#if>
											<div class="blog_left">
											${createdBy?if_exists} - <span style="color:#999999;">${recipeComment.createdStamp?date?if_exists}</span>
											 </div>
											<div class="blog_right">											
											<div> ${StringUtil.wrapString(recipeComment.message)?if_exists}</div>
											<div></div>
											</div>
											<div class="clear"></div>
											<div style="margin-top: -40px;" id="${recipeComment.recipeCommentId?if_exists}${recipeComment.recipeManagementId?if_exists}">
													<a href="javascript:void(0);" onclick="addTextArea('${recipeComment.recipeCommentId?if_exists}','${recipeComment.recipeManagementId?if_exists}');" class="buttontextblue" style="margin-top:5px;float:right;">reply</a>
												</div>
												<div class="clear"></div>
												<div id="${recipeComment.recipeCommentId?if_exists}" style="margin-top:10px;"></div>
												
										<#assign subCommentList = Static["org.ofbiz.recipes.RecipeEvents"].subCommentList(delegator,recipeComment.recipeCommentId)?if_exists>
										<#if subCommentList?has_content>
											<#list subCommentList as subComment>
												<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,subComment.createdBy)?if_exists>
												<#if !createdBy?has_content>
													<#assign createdBy = recipe.createdBy>
												</#if>
												
												<div class="sub-comment" style="margin-left:50px;padding:10px 0;">
													<div class="blog_left1">${createdBy?if_exists} - <span style="color:#999999;">${subComment.createdStamp?date?if_exists}</span></div><br/>
												     <div style="float:left;margin-top:2px;width:auto;text-align:justify;">${StringUtil.wrapString(subComment.message)?if_exists}</div></br>
												 <div class="clear"></div>   
												</div> 
												
												
											</#list>
											<br/>
										</#if>
										<div style="border:1px #DEDFBF solid;"></div>
										</div>
										<#--if recipe.statusId?has_content && "RECIPE_OF_WEEK" == recipe.statusId-->
												<!--div id="${recipeComment.recipeCommentId?if_exists}${recipeComment.recipeManagementId?if_exists}">
													<a href="javascript:addTextArea('${recipeComment.recipeCommentId?if_exists}','${recipeComment.recipeManagementId?if_exists}');" class="buttontextblue" style="margin-top:5px;float:right;">reply</a>
												</div>
												<div class="clear"></div>
												<div id="${recipeComment.recipeCommentId?if_exists}" style="margin-top:10px;"></div>
												<div style="border:1px #DEDFBF solid;"></div-->
											<#--/#if-->
										<#assign count = count+1>
								</#list>
						</div>
					</div>
				</#if>
				<div class="clear"></div>
				<#--if commandList?has_content>
				<#list commandList.keySet() as ckey>
						<#assign s = commandList.get(ckey)>
						<div style="padding:10px 0 0 5px; margin-bottom:10px;"><b>${s?if_exists} Wrote:</b><br/>
						${ckey?if_exists}.</div>
				</#list>
</#if-->
			</div>
				
 </div>
 <#else>
 	No Recipe Of The Week Profile Found .........
 </#if>
 <script>
 	function addTextArea(parentCommentId,recipeManagementId){
 		if(!checkLogin()){
 			window.location="<@ofbizUrl>checkLogin/displayrecipeWeek</@ofbizUrl>";
    		return false;
    	}
    	
		var newText = '<form name="subComment" action="<@ofbizUrl>addComment</@ofbizUrl>">';
		newText = newText + '<input type="hidden" name="recipeId" value="'+recipeManagementId+'"></textarea>';
		newText = newText + '<input type="hidden" name="donePage" value="displayrecipeWeek">';
		newText = newText + '<input type="hidden" name="type" value="RECIPE_COMM_TYPE_BLO">';
		newText = newText + '<input type="hidden" name="parentCommentId" value="'+parentCommentId+'"></textarea>';
		newText = newText + '<textarea name="message" rows="4" cols="50" style="border:1px solid #999999; display:block; margin-bottom:5px;" value=""></textarea>';
		newText = newText + '<input type="submit" value="Add"/>';
		newText = newText + '<input type="button" value="Cancel" onclick="removeTextArea('+parentCommentId+','+recipeManagementId+')"/></form>';
		document.getElementById(parentCommentId).innerHTML  += newText;
		document.getElementById(parentCommentId+""+recipeManagementId).innerHTML  = "";
	}
	function removeTextArea(parentCommentId,recipeManagementId)
	{
		document.getElementById(parentCommentId).innerHTML  = "";
		var newText = '<a href="javascript:void(0);" onclick="addTextArea(\''+parentCommentId+'\',\''+recipeManagementId+'\');" class="buttontextblue" style="margin-top:5px;float:right;">reply</a>';
		document.getElementById(parentCommentId+""+recipeManagementId).innerHTML  += newText;
	}
	function addRating(value){
		document.getElementById("my_input").value = value;
	}
	function reviewComment()
	 {
	    if(!checkLogin())
	    	return false;
	    
	    //var rating=document.getElementById('rating_simple3').value;

	    //var title=document.getElementById('title_id').value;
	 	var message=document.getElementById('message_id').value;

	 	
	 	/*
	 	if(rating==null || rating=="")
	 	{
	 	    alert("Select the rating!");
	 	    document.getElementById('title_id').focus();
	 	    return false;
	 	}
	 	if(title==null || title=="")
	 	{
	 	    alert("Enter the title!");
	 	    document.getElementById('title_id').focus();
	 	    return false;
	 	}
	 	*/
	 	if(message==null || message=="")
	 	{
	 	    alert("Enter the message!");
	 	    document.getElementById('message_id').focus();
	 	    return false;
	 	}
	 	 return true;
	 }
	 function checkLogin()
	 {
	 	<#if userLogin?has_content>
	 		return true;
	    <#else>
	    	alert("Login Please!");
	    	return false;
	    </#if>
	 }
 </script>
 
 
 <script language="javascript" type="text/javascript">
            function test(value){
                alert("This rating's value is "+value);
            }
            $(function() {
                
            $("#rating_simple3").webwidget_rating_simple({
								rating_star_length: '5',
								rating_initial_value: '',
								rating_function_name: '',//this is function name for click
								directory: '/erptheme1/star'
                });

            });
        </script>
