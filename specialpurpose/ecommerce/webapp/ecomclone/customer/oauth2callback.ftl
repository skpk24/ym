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



<form name="oauthsubmition"  id="verifyOauthRequest" action="verifyOauthRequest" method="post">
    <input type="hidden" name="email" id="email" value=""/>
	<input type="hidden" name="given_name" id="given_name" value=""/>
	<input type="hidden" name="family_name" id="family_name" value=""/>
	<input type="hidden" name="gender" id="gender" value=""/>
	<input type="hidden" name="errorMsg" id="errorMsg" value=""/>
</form>

<script type="text/javascript" src="/images/jquery/jquery-1.4.2.min.js"></script>

<script type="text/javascript">

// First, parse the query string

var params = {}, queryString = location.hash.substring(1), regex = /([^&=]+)=([^&]*)/g, m;

var access_token ="";
var reterror="";

while (m = regex.exec(queryString)) {
	if("access_token"== decodeURIComponent(m[1])) {
    	access_token = decodeURIComponent(m[2]);
	}

	if("error"== decodeURIComponent(m[1])) {
		reterror = decodeURIComponent(m[2]);
	}
}

if(reterror== "access_denied") {
			document.getElementById("errorMsg").value= "Access Denied!";
			alert("Access Denied!");
			window.close();
} else {
	var curl= "https://www.googleapis.com/oauth2/v1/userinfo?access_token="+access_token;
	$.ajax({
		type: "GET",
		url:curl,
		dataType: "json",
		success: function() {
		},
		error: function() {
		},
		complete:function(data){
			var myJSONtext = data.responseText
			var myObject = eval('(' + myJSONtext + ')');
			document.getElementById("email").value=myObject.email ;
			document.getElementById("given_name").value=myObject.given_name ;
			document.getElementById("family_name").value= myObject.family_name;
			document.getElementById("gender").value= myObject.gender;
			document.getElementById("verifyOauthRequest").submit();
		}
	}); //complete ajax call

}  //else end here  

</script>