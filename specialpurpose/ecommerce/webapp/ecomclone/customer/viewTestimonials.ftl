<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>

 
<div class="inner-content">
	<h3 class="Testimonials_h3">Testimonials from our customers</h3>
		<div id="typeShow" class="Testimonials_container">
		<#if feedbackList?has_content>
			<#assign count = 1>
			 <#list feedbackList as feedback>
			 	<div class="Testimonials_div">
			 	 	<div class="Testimonials_content">
				 	 	<div class="Testimonials_Img">  
				 	 		<img src="${feedback.feedbackImgUrl?default('/images/defaultImages.jpg')}" style="width:125px; height:120px;" /> 
				 	 	</div>
				 	 	<div class="Testimonials_text">
				 	   		${feedback.message?if_exists}
				 	 	</div>
				    </div>
				    <div class="arrow-down"></div>
				    <div class="clear"></div>	
						
				    <#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,feedback.createdBy)?if_exists>
						<#if !createdBy?has_content>
							<#assign createdBy = feedback.createdBy>
						</#if>
				    <div class="recipelist-right" style="float:left;width:100%;">				    
					<div style=" " class="testimonila_created_by">${createdBy?if_exists}</div>
				 	<#if feedback.message?exists && feedback.message?has_content && feedback.message?length gte 250>
					<div class="testimonial_more">More...</div>
					</#if>
					
				<#assign count = count+1>
				</div>	</div>
			 </#list>
			 <#else>
			 	There is no Testimonial Posted!
			</#if>
		</div>
	 </div>
		<#-- ul class="point"> 
		 <strong><br/>
		 Please feel free to give us your feedback to serve you better:
		 <br/>
					 <h3 class="Testimonials_h3 feedBack_text">ADD YOUR TESTIMONIAL</h3>
					 <form name="feedback" id="feedback" action="<@ofbizUrl>addFeedback</@ofbizUrl>" method="post" enctype='multipart/form-data'>
		 				<input type="hidden" name="type"  value="CUST_FEEDBACK">
		 				
					 	<table>
					 	<tr>
					 		<td>Upload Image: <input type="file" id="file" name="file" style="margin-left:3px;" accept="image/*"></td> 
					 	</tr>
						<tr>
				 		<td>
							<textarea cols="80" id="message" name="message" rows="10" placeholder="Type your message here"></textarea>	
						</td>
				 		</tr>
				 		<tr>
				 			<td><input type="submit" name="add" Value="Send" onclick="return reviewFeedback();"/></td>
				 		</tr>
					 	</table>
					 </form>
					
		</ul-->
</div>

<div id="testimaonials_more_content_popup_background" class="testimaonials_more_content_popup_background"></div>
<div id="testimaonials_more_content_popup" class="testimaonials_more_content_popup">
	<div class="testimonial_more_name" id="testimonial_more_name"></div>
	<span class="close_testimonial_more_popup" id="close_testimonial_more_popup">X</span>
	<div class="testimonial_more_container">
	<div class="testimonial_more_image"><img src="" style="width:125px; height:120px;" id="testimonial_more_image"/></div>
	<div class="testimonial_more_content" id="testimonial_more_content"></div>
	</div>
</div>


<script>
	$(document).ready(function(){
	
		$(".Testimonials_text").each(function(){
			var content=$(this).text();
			//alert(content);
			$(this).html(content);
		});
		
		$(".testimonial_more").click(function(){
			$("#testimonial_more_content").html($(this).parent().parent().children().children(".Testimonials_text").html());
			$("#testimonial_more_image").attr("src",$(this).parent().parent().children().children(".Testimonials_Img").children("img").attr("src"));
			$("#testimonial_more_name").html($(this).parent().children(".testimonila_created_by").text());
			$("#testimaonials_more_content_popup").show();
			$("#testimaonials_more_content_popup_background").show();
		});
		$("#close_testimonial_more_popup").click(function(){
			$("#testimaonials_more_content_popup").hide();
			$("#testimaonials_more_content_popup_background").hide();
		});
		
		$("#testimaonials_more_content_popup_background").click(function(){
			$("#testimaonials_more_content_popup").hide();
			$(this).hide();
		});
		
	});
</script>

<script type="text/javascript">

function reviewFeedback()
 {
    if(!checkLogin())
	    return false;
	  
 	var msg=document.getElementById('message').value; 
 	if(msg==null || msg=="")
 	{
 	    alert("Please enter the message!");
 	    document.getElementById('message').focus();
 	    return false;
 	} 
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