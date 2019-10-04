<style>
.err-outer{width:270px; float:left; margin-top:7px; padding-left:20px; font-size:11px;}
</style>


<form name="custform" id="custform" action="createCustDetail" method="post" enctype='multipart/form-data' onsubmit="return validate()">
	<table>
		<tr>
			<td>
				Place :
			</td>
			<td>
			<input type="hidden" name="recipeManagementId" value="${parameters.recipeManagementId?if_exists}"/>
			<input type="hidden" name="userLoginId" value="${userLoginId?if_exists}"/>
			<div style="float:left;"><input type="text" size="30" name="homeTown" id="homeTown" value="<#if custDetails?exists>${custDetails.homeTown?if_exists}</#if>" onblur="validatePlace()"></div>
 			<div class="err-outer" id="homeTownError"></div>
 			</td>
		</tr>
		<tr>
			<td>
				Cooking Knowledge :       
			</td>
			<td>
				<div style="float:left;"><input type="text" size="30" name="cookingLevel" id="cookingLevel" value="<#if custDetails?exists>${custDetails.cookingLevel?if_exists}</#if>" onblur="validateCookingLevel()"></div>
				<div class="err-outer" id="cookingLevelError"></div>
			</td>
		</tr>
		<tr>
			<td>
				Cooking Interests :
			</td>
			<td>
				<div style="float:left;"><input type="text" size="30" name="cookingInterest" id="cookingInterest" value="<#if custDetails?exists>${custDetails.cookingInterest?if_exists}</#if>" onblur="validateCookingInterest()"></div>
				<div class="err-outer" id="cookingInterestError"></div>
			</td>
		</tr>
		<tr>
			<td>
				Hobbies :
			</td>
			<td>
				<div style="float:left;"><input type="text" size="30" name="hobbies" id="hobbies" value="<#if custDetails?exists>${custDetails.hobbies?if_exists}</#if>" onblur="validateHobbies()"></div>
				<div class="err-outer" id="hobbiesError"></div>
			</td>
		</tr>
		<tr>
			<td>
				Something about yourself :
			</td>
			<td>
			<div style="float:left;"><textarea name="aboutChef" id="aboutChef" rows="4" cols="50"  style="border:1px solid #999999;" onblur="validateAboutChef()"><#if custDetails?exists>${custDetails.aboutChef?if_exists}</#if></textarea></div>
				<div class="err-outer" id="aboutChefError" style="padding:0px 0 10px 0 !important; position:relative; z-index:999; left:199px;"></div>
			</td>
		</tr>
		<tr>
			<td>
				Photo :
			</td>
			<td>
			<#if custDetails?exists>
			<#if custDetails.photo?has_content && custDetails.photo!="">
				<div style="float:right; width:500px; height:299px; overflow:hidden;">
			 		<img src="${custDetails.photo?if_exists}"/>
			  </div>
			 </#if>
			  </#if>
				<div style="float:left;"><input type="file" name="file" id="photo" style="margin-left:3px;" onblur="validatePhoto()"></div>
				<div style="width:200px; float:left; margin-top:7px; padding-left:0px; font-size:11px;" id="photoError"></div>
			</td>
			
		</tr>
		<tr>
			<td>
				Are you sure to show your profile details in the site .
			</td>
			<td>
				<select name="shareProfile" style="width:110px;">  
					  <option value="Y">Yes</option>
					  <option value="N">No</option>
			  	</select>
			</td>
		</tr>
		<tr>
			<td>
				Are you OK to allow other members to write through the blog.
			</td>
			<td>
				<select name="allowComments" style="width:110px;">
					  <option value="Y">Yes</option>
					  <option value="N">No</option>
			  	</select>
			</td>
		</tr>
		
			<tr>
			<td>
					I am aware that I have been selected at the sole discretion of the management of YouMart?
			</td>
			<td>
				<select name="termManagement" style="width:110px;">
					  <option value="Y">Yes</option>
					  <option value="N">No</option>
			  	</select>
			</td>
		</tr>
		
		<#-- <tr>
			<td>
					I appreciate this initiative and I am more than happy to share my profile in the YouMart site Y/N?
			</td>
			<td>
				<select name="shareProfile" style="width:110px;">
					  <option value="Y">Yes</option>
					  <option value="N">No</option>
			  	</select>
			</td>
		</tr>-->
		
		<tr>
			<td>
 I am in agreement to participate in the members BLOG during this week and will be happy to respond to any queries from other members about my recipe or cooking related information.?
			</td>
			<td>
				<select name="termToRespond" style="width:110px;">
					  <option value="Y">Yes</option>
					  <option value="N">No</option>
			  	</select>
			</td>
		</tr>
		
		
	
		<tr>
			<td colspan="2">
				<input type="submit" value="submit"/>
			</td>
		</tr>
	</table>
</form>

<script>
	function validate(){
		var flag = true;
		if(!validatePlace())flag = false;
		if(!validateCookingLevel())flag = false;
		if(!validateCookingInterest())flag = false;
		if(!validateHobbies())flag = false;
		if(!validateAboutChef())flag = false;
		if(!validatePhoto())flag = false;
		
		return flag;
	}
	function validatePlace(){
		var homeTown = document.getElementById('homeTown').value;
		if(homeTown != null && homeTown.trim() == "")
		{
			document.getElementById('homeTownError').innerHTML = "<font color='red'>please enter Place</font>";
			return false;
		}
		document.getElementById('homeTownError').innerHTML = "";
		return true;
	}
	function validateCookingLevel(){
		var cookingLevel = document.getElementById('cookingLevel').value;
		if(cookingLevel != null && cookingLevel.trim() == "")
		{
			document.getElementById('cookingLevelError').innerHTML = "<font color='red'>please enter Cooking Knowledge</font>";
			return false;
		}
		document.getElementById('cookingLevelError').innerHTML = "";
		return true;
	}
	function validateCookingInterest(){
		var cookingInterest = document.getElementById('cookingInterest').value;
		if(cookingInterest != null && cookingInterest.trim() == "")
		{
			document.getElementById('cookingInterestError').innerHTML = "<font color='red'>please enter Cooking Interests</font>";
			return false;
		}
		document.getElementById('cookingInterestError').innerHTML = "";
		return true;
	}
	function validateHobbies(){
		var hobbies = document.getElementById('hobbies').value;
		if(hobbies != null && hobbies.trim() == "")
		{
			document.getElementById('hobbiesError').innerHTML = "<font color='red'>please enter Hobbies</font>";
			return false;
		}
		document.getElementById('hobbiesError').innerHTML = "";
		return true;
	}
	function validateAboutChef(){
		var aboutChef = document.getElementById('aboutChef').value;
		if(aboutChef != null && aboutChef.trim() == "")
		{
			document.getElementById('aboutChefError').innerHTML = "<font color='red'>please enter about yourself</font>";
			return false;
		}
		document.getElementById('aboutChefError').innerHTML = "";
		return true;
	}
	function validatePhoto(){
		var photo = document.getElementById('photo').value;
		if(photo != null && photo.trim() == "")
		{
			document.getElementById('photoError').innerHTML = "<font color='red'>please upload a photo</font>";
			return false;
		}
		document.getElementById('photoError').innerHTML = "";
		return true;
	}
</script>
