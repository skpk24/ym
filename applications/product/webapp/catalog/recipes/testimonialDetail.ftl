<link type="text/css" rel="stylesheet" href="/images/zoomer/texteditor/jquery-te-1.4.0.css">
<script type="text/javascript" src="/images/zoomer/texteditor/jquery-te-1.4.0.min.js" charset="utf-8"></script>
 <script src="/images/htmlEditor/nicEdit.js" type="text/javascript"></script>
	<script type="text/javascript">
	bkLib.onDomLoaded(function() {
		new nicEditor().panelInstance('area1');
		new nicEditor().panelInstance('area2');
	});
	</script>
<#if recipeComment?has_content>
<form name="testimonialform" id="testimonialform" action="updateTestimonial?testimonialId=${recipeComment.recipeCommentId?if_exists}" method="post" enctype='multipart/form-data'>
	<div class="addrecipe">
		<input type="hidden" id="recipeCommentId" name="recipeCommentId" value="${recipeComment.recipeCommentId?if_exists}"/>
		
		<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipeComment.createdBy)?if_exists>
				<#if !createdBy?has_content>
					<#assign createdBy = feedback.createdBy>
				</#if>
		<label>Posted by</label>: <input type="text" readonly="readonly" size="30" id="createdBy" name="createdBy" value="${createdBy?if_exists}" style="height:17px;margin-left: 9px;">
		 
		<br />
		<label style="padding-top:0px;">Image</label>: <input type="file" id="file" name="file" style="margin-left:3px;border: #ddd solid 1px !important;width: 181px; padding: 2px 0 0 2px;" accept="image/*">
						<img src="${recipeComment.feedbackImgUrl?default('/images/defaultImages.jpg')}" width="30px" height="30px"/>
		<br /><br /><br />
		 
		
		<div class="Shopcar_pageHead"><h2>Edit Message</h2> *</div>
		<textarea  id="area1" name="area1" rows="10" cols="150" value="" class="jqte-test">${StringUtil.wrapString(recipeComment.message)?if_exists}</textarea>
		<input type="submit" value="Update" />
	</div>
</form>
	<#else>
		No Testimonial found ....
    </#if>
 
  