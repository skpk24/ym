function pinNumber(){
var foundPostalCode = true;
	var locationSearch1=document.getElementById('locationSearch').value;
 	if (locationSearch1 ==''){
	alert("Please enter pin number");
	document.getElementById('locationSearch').focus();
	return false;
	}else{
		var url="/control/checkPostalCode?locationSearch="+locationSearch1;
	    	jQuery.ajax({url: url,
	        data: null,
	        type: 'post',
	        async: false,
	        success: function(data) {
	        if(data != "success"){
	        	foundPostalCode = false;
	        } 
	 	   return true;
		  },
	        error: function(data) {
	          alert("Oops! something went wrong");
	            return false; 
	        }
	    });  
	}
	var message = ""; 
 	if(foundPostalCode){
		message = '<p class="locationFound">WoW! Happy to serve you. Enjoy Shopping with YouMart.</p>';
	}else{
		message = '<p class="locationNotFound">';
		message +='Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.';
		message +='</p>';
		message +='<form name="storeEmailform" method="post" action="storeEmaillocation">';
		message +='<table cellspacing="0" align="center" style="margin:0 auto; width:258px; padding-bottom:10px;">';
		message +='<input type="hidden" name="pinCode" id="pinCode" value="' + locationSearch1 + '"/>';
		message +='<tr>';
		message +='<td width="100px"><span class="label">Email Id</span></td>';
		message +='<td><input type="text" name="emailId" id="emailId" /></td>';
		message +='<td><input type="submit" value="submit" onclick="return addInput()"></td>';
		message +='</tr>';
		message +='</table>';  
		message +='</form>';
	}
 	document.getElementById('content1').innerHTML=""; 
	document.getElementById('content1').style.display = "block";
	document.getElementById('content1').innerHTML = message
}
function addInput(){
	 
	 var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	 var emailId = document.getElementById('emailId').value;
	 var pinCode = document.getElementById('pinCode').value;
	 alert(pinCode);
	 if (emailId== null || emailId ==''){
	   document.storeEmailform.emailId.focus();
	  alert("Please enter a email address");
	     return false;
	  }
	  else if(!emailPattern.test(emailId)){
	      alert("Please enter a valid email address");
	    document.storeEmailform.emailId.focus();
	    return false;
	  }
	  else{
		  var url="/control/storeEmaillocation?emailId="+emailId+"&pinCode="+pinCode;
		  alert("url"+url);
	    	jQuery.ajax({url: url,
	        data: null,
	        type: 'post',
	        async: false,
	        success: function(data) {
	        	alert(data);
	        if(data != "success"){
	        	foundPostalCode = false;
	        } 
	 	   return true;
		  },
	        error: function(data) {
	          alert("Oops! something went wrong");
	            return false; 
	        }
	    });  
	  }
	  return true;
}

function loadProducts(){
    var productSelected= $("#SEARCH_STRING").val();
    var productList = "";
    var  param = 'sport=' + productSelected;
    jQuery.ajax({url: "/control/autoproductname",
     data: param,
     type: 'post',
     async: true,
     success: function(products) {
    
     alert(products);
     $( "div.demo-container" ).html(products);
     }
	});
  
    return productList;
	}
 