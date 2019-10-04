<script>
function validateFeedbackForm(){
	var feedbackMessage = document.getElementById('message').value;
	var feedbackemail = document.getElementById('email').value;
	var e = document.getElementById("commentSelect");
	var emailFilter = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(feedbackemail == '' || feedbackemail == null){
		alert("Please enter email.");
		return false;
	}
	if (!emailFilter.test(feedbackemail)) {
    alert("Please enter valid email address");
    document.getElementById('email').value='';
    document.getElementById('email').focus();
    return false;
 }
 	if(e.options[e.selectedIndex].value == "select"){
 		alert("Please select comment.");
 		document.getElementById('commentSelect').focus();
 		return false;
 	}
	if(feedbackMessage == '' || feedbackMessage == null){
		alert("Please enter message.");
		return false;
	}
	
	feedbackFormSubmitAjax();
	//document.feedback.submit();
	return true;
}



function feedbackFormSubmitAjax()
{
var email = document.getElementById("email").value;
var commentSelect = document.getElementById("commentSelect").value;
var message = document.getElementById("message").value;

var url = "/control/sendEmailFeedback?email="+email+"&commentSelect="+commentSelect+"&message="+message;
var xmlhttp;
if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
  xmlhttp=new XMLHttpRequest();
  }
else
  {// code for IE6, IE5
  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
xmlhttp.onreadystatechange=function()
  {
  if (xmlhttp.readyState==4 && xmlhttp.status==200)
    {
	    var msg = xmlhttp.responseText;
	    if(msg == null || msg == "")
	    	msg = "Your feedback has successfully submitted .";
	    	
	    alert(msg);
	    
	    if(msg == "Your feedback has successfully submitted .")
	    {
	    	document.getElementById("email").value = "";
			document.getElementById("commentSelect").selectedIndex="0";
			
			var msgDiv = "<textarea cols='30'  rows='3' name='message' id='message' style='margin-left:4px; margin-bottom:4px;'></textarea>*";
			document.getElementById("messageDiv").innerHTML = msgDiv;
	    }
	    feedbackinfo();
    }
  }
xmlhttp.open("GET",url,true);
xmlhttp.send();
}

</script>

<table cellpadding="0" cellspacing="0" border="0">
	<tr>
		<td style="vertical-align:top;">
			<div class="inner-content" style="background:#ffffff; font-weight:normal; border:0px solid #7a7946; min-height:112px; margin-right:0px !important;">
			 <div class="Shopcar_pageHead">Feedback</div>
			   <#if userLogin?has_content>
			   <form name="feedback" action="<@ofbizUrl>sendEmailFeedback</@ofbizUrl>" method="post" >
				
				<table width="400px">
				  <tr>
				  	<td><b style="padding-left:4px;">Need assistance with regard to shopping or delivery?</b></td>
				  </tr>
				  <tr>
				  	<td style="padding-left:4px;">For any assistance please get in touch with our customer support team at <b>(080) 45 45 88 88.</b></td>
				  </tr>
				  <tr>
				  	<td style="padding-left:4px;">We would really appreciate your valuable feedback, any ideas or suggestions to improve our service to you, 
				  	or any input in terms of our product availability, website options and features etc, please send your feedback by email:</td>
				  </tr>
		          <tr>
		            <td><b style="padding-left:4px;">Your Email ID :</b></td>
		          </tr>
		          <tr>
		            <td><input type="text" name="email" id="email" size="30" value="<#if autoUserLogin?has_content>${autoUserLogin.userLoginId?if_exists}</#if>"/>*</td>
		          </tr>
		           <tr>
		            <td><b style="padding-left:4px;">I would like to comment on the following:</b></td>
		           </tr>
		           <tr>
		            <td><select name="commentSelect" id="commentSelect" class="selectBox">
					        <option value="select">Select</option>
					        <option value="I like the feature">I like the feature</option>
					        <option value="Suggest new inputs / features">Suggest new inputs / features</option>
					        <option value="Product availability">Product availability</option>
					        <option value="Offers and Promotions">Offers and Promotions</option>
					        <option value="Other feedback">Other feedback</option>
				        </select>*
                    </td>
		           </tr>
		           <tr>
		            <td><b style="padding-left:4px;">Message :</b></td>
		           </tr>
		           <tr>
		            <td>
		            <div id="messageDiv">
	                	<textarea cols="30"  rows="3" name="message" id="message" style="margin-left:4px; margin-bottom:4px;"></textarea>*
	                </div>
	                </td>
		          </tr>
		          <tr>
		            <td><a href="#" onclick = "return validateFeedbackForm();"><img src="/erptheme1/cartslide/submit.jpg" style="margin-left:4px;"/></a></td>
		          </tr>
				</table>
			  </form>
			  <#else>
			       <table width="400px">
			       	<tr>
				  	<td><b style="padding-left:4px;">Need assistance with regard to shopping or delivery?</b></td>
				    </tr>
				    <tr>
				  	<td style="padding-left:4px;">For any assistance please get in touch with our customer support team at <b>(080) 45 45 88 88.</b></td>
				    </tr>
				    <tr>
				  	<td style="padding-left:4px;">We would really appreciate your valuable feedback, any ideas or suggestions to improve our service to you, 
				  	or any input in terms of our product availability, website options and features etc, please send your feedback by email:</td>
				    </tr>
			        <tr>
			        	<td style="vertical-align:top; color:#ff0000 !important;"> <a href="<@ofbizUrl>/feedback</@ofbizUrl>" class="login123" style="color:#06509B !important;">Please login to provide feedback</a></td>
			        </tr>
			        
			        
			       </table>
	      </#if>	
			</div>
		</td>
		<td style="vertical-align:top;">
			<!-- div style="float:left; width:38px;">
				<img src="/erptheme1/cartslide/feedbackclose.png" alt="bulk order" usemap="#Map2" />
			</div -->
		</td>
	</tr>
</table>