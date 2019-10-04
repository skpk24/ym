
<script>
	function changeTestimonialStatus(statusIdTo){
		var checkboxes = document.getElementsByName("recipeCommentId");
		var count = 0;
		for (var i=0; i<checkboxes.length; i++) {
		     if (checkboxes[i].checked) {
		        count++;
		     }
		  }
		if(statusIdTo == "RECIPE_OF_WEEK" && count != 1)
		{
			alert("Please select one Testimonial");
			return false;
		}
		document.getElementById("statusIdTo").value= statusIdTo;
		document.testimonialList.submit();
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

<#if TestimonialList?has_content>
	<form name="testimonialList" action="changeTestimonialStatus" method="post">
	<table class="basic-table hover-bar dark-grid" cellpadding="0">
		<tr align="left">
			
			<td colspan="5"> 
				<#assign statusValidChangeList  = delegator.findByAnd("StatusValidChange",Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId",parameters.statusId))/>
				<#if statusValidChangeList?has_content> 
					<#list statusValidChangeList as statusValidChange>
 						<input onclick="changeTestimonialStatus('${statusValidChange.statusIdTo?if_exists}')" value="${statusValidChange.transitionName?if_exists}" type="button">
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
					<input type="hidden" value="FB_APPROVED" name="statusId"/>
				</#if>
			</td>
			<td>
				Testimonial Id
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
			<#list TestimonialList as test>
				<tr <#if counter%2 == 0>class="alternate-row"</#if>>
				<#assign counter = counter+1>
					<td>
						<div id="checkbox">
		              		<input type="checkBox" id="checkBox${test.recipeCommentId?if_exists}" name="recipeCommentId" value="${test.recipeCommentId?if_exists}"/>
		                </div>
					</td>
					<td><#--a href="<@ofbizUrl>testimonialDetail?testimonialId=${test.recipeCommentId?if_exists}</@ofbizUrl>"></a-->${test.recipeCommentId?if_exists}</td>
					<td>
					<#assign statusItem = delegator.findOne("StatusItem", {"statusId":test.statusId}, true)?if_exists>
						${statusItem.description?if_exists}
					</td>
					<td>${test.createdBy?if_exists}</td>
					<td><#if test.createdDate?has_content>${test.createdDate?string("dd-MM-yyyy")?if_exists}</#if></td>
				<tr/>
			</#list>
		</form>
	</table>
<#else>
	No Testimonials is found ...
</#if>
