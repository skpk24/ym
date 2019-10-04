

<script type="text/javascript" src="/images/jquery/jquery-1.4.2.min.js"></script>
<script type="text/javascript">
// First, parse the query string
var params = {}, queryString = location.search.substring(1),
    regex = /([^&=]+)=([^&]*)/g, m;
    
	alert("Query String = "+queryString);
	var code ="";
	var reterror="";
while (m = regex.exec(queryString)) {

  if("code" == decodeURIComponent(m[1]))
   {
    code = decodeURIComponent(m[2]);
   }
   
    if("error"== decodeURIComponent(m[1]))
   {
    reterror = decodeURIComponent(m[2]);
   }
 
   
}
  if(reterror== "access_denied")
   {
      document.getElementById("errorOauthForm").submit();
	  //window.close();
   }
   else
   {
   alert("code="+code);
    var curl= "https://graph.facebook.com/oauth/access_token?client_id=520771177980080&redirect_uri=https://localhost:8444/control/oauth2callbackfb&client_secret=c13616a0e4034b970d4c9df3e6b728d5&code="+code;
    alert("curl="+curl);
   $.ajax({
		  type: "GET",
		  url:curl,
		  success: function() {
		  alert("success");
		  },
		  error: function() {
		   alert("error");
		   
		  },
		  complete:function(data){
		  alert("complete");
			alert("data = "+data.responseText);
		   var myJSONtext = data.responseText;
		   var myObject = eval('(' + myJSONtext + ')');
		 
		  
		   document.oauthsubmition.action=""
		   var access_token = myObject.access_token;
		   alert("access_token="+access_token);
		  document.getElementById("email").value=myObject.email ;
		  document.getElementById("given_name").value=myObject.given_name ;
		  document.getElementById("family_name").value= myObject.family_name;
		  document.getElementById("gender").value= myObject.gender;
		  document.getElementById("verifyOauthRequestfb").submit();			
		//  window.close();
		  
		 //complete end here 
		  }
		}); //complete ajax call
   }	//else end here	

</script>

 <form name="oauthsubmition"  id="verifyOauthRequestfb" action="verifyOauthRequestfb" method="post">
    <input type="hidden" name="email" id="email" value=""/>
	<input type="hidden" name="given_name" id="given_name" value=""/>
	<input type="hidden" name="family_name" id="family_name" value=""/>
	<input type="hidden" name="gender" id="gender" value=""/>
	
 </form>
 <form name="errorOauthForm" id="errorOauthForm" action="verifyOauthRequestfb" method="post">
    <input type="hidden" name="errorMsg" id="errorMsg" value="Access Denied!"/>
	
	
 </form>