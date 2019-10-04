<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<div class="endcolumns1_right">

	<h3>Testimonials <span><a href="viewTestimonials" style="width:125px; height:120px;" /> view all</a></span></h3>
	
		<div id="typeShow" class="home_Testimonials_container">
			<#if feedbackList?has_content>
				<#assign count = 1>
			 		<#list feedbackList as feedback> 
			 			<div class="home_Testimonials_div" id="testimonialDiv_${count}" style='display:none;'>
				 	 		<div class="home_Testimonials_content">
					 	 	<div class="home_Testimonials_Img">   
					 	 		<img src="${feedback.feedbackImgUrl?default('/images/defaultImages.jpg')}" style="width:75px; height:95px;" /> 
					 	 	</div>
					 	 	<div class="home_Testimonials_text">	
					 	 	${StringUtil.wrapString(feedback.message?if_exists)}			 	   
 					 	 	</div>
					    	</div>
					    	<div class="home_arrow-down"></div>
					    	<div class="clear"></div>	
					    	<#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,feedback.createdBy)?if_exists>
							<#if !createdBy?has_content>
								<#assign createdBy = feedback.createdBy>
							</#if>
						    <div class="recipelist-right" style="float:left;width:100%;">				    
							<div style=" " class="home_testimonila_created_by">${createdBy?if_exists}</div>
						 	<#if feedback.message?exists && feedback.message?has_content && feedback.message?length gte 250>
								<div class="home_testimonial_more">More...</div>
							</#if>
							<#assign count = count+1>
							</div>
						</div>
			 		</#list>
			 <#else>
			 	There is no Testimonial Posted!
			</#if>
			
		<#if feedbackList?has_content>
			<#assign countTemp = 1>
			<script type="text/javascript">
				var text_st = new Array(
				 <#list feedbackList as feedback>
				 "testimonialDiv_${countTemp}" 
				 <#if (countTemp < feedbackList.size())>
				 ,
				 </#if>
				 <#assign countTemp=countTemp +1 />
	             </#list>
                 );
                var l = text_st.length;
				var rnd_no = Math.floor(l*Math.random());
				if(document.getElementById(text_st[rnd_no]) != null ){
				document.getElementById(text_st[rnd_no]).style.display = "block"; 
				}
			 </script>
        </#if> 
		</div>
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
		$(".home_testimonial_more").click(function(){
			$("#testimonial_more_content").html($(this).parent().parent().children().children(".home_Testimonials_text").text());
			$("#testimonial_more_image").attr("src",$(this).parent().parent().children().children(".home_Testimonials_Img").children("img").attr("src"));
			$("#testimonial_more_name").html($(this).parent().children(".home_testimonila_created_by").text());
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