<script src="/images/prototypejs/prototype.js" language="JavaScript" 
type="text/javascript"></script>
<script type="text/javascript" language="JavaScript">
 
     
     
    
</script>
 <script>
		 function showDropDown(tags1){
		 	 var availableTags = [
		      ${string?if_exists}
		    ];
		    $( "#"+tags1 ).autocomplete({
		      source: availableTags
		    });
		 }
		 
		 var count = 1;
		  //METHOD TO ADD VENDOR DYNAMICALLY
		function createDiv()
		    {
		     	count = count + 1;
		        var divTag = document.createElement("div");
		        divTag.id = "div"+count;
		        divTag.setAttribute("align","left");
		        divTag.style.margin = "0px auto";
		        
		        var pinId = "pinId_"+count;
		        var newText = '<input type="text" size="35" id="'+pinId+'" name="'+pinId+'" />';
		    	newText = newText + ' <input id="btn1" type="button" value="+" onclick="createDiv();" />';
		        newText = newText + ' <input id="btn1" type="button" value="-" onclick="removeDiv(div'+count+');" />  </p>'; 
		    	
		        divTag.innerHTML  += newText;
		        document.getElementById("div0").appendChild(divTag);
		    }
		function removeDiv(divId){
		//alert(divId);
		    divId.innerHTML = "" ;
		}

function validatePinId(){

	var pinId_1 = document.getElementById('pinId_1').value;
	if(pinId_1==''){
		alert("Please enter Pin Id.");
		return false;
	}else{
		return true;
	}

}		
		  
  </script>

<body>

<form name="myform" action="<@ofbizUrl>quickShipOrderPinId</@ofbizUrl>" method="post">
		<span style="font-weight:bold;">Enter Pin Id : </span> 
		<div id="div0">
			<input id="pinId_1" type="text" style="padding:5px !important; float:left;"  name="pinId_1" size="35"/><input id="btn1" type="button" style="padding-top:2px !important; float:left;" value="+" onclick="createDiv();" />       
		</div>	
		<div style="clear:both;"></div>	
		<input type="submit" value='Complete' onclick="return validatePinId();">
      </form>
      <br/>
      <#assign msg=request.getAttribute("msg")?if_exists>
       <#if msg?has_content>
       		<#list msg as message>
          		<div style="color:#ff0000; font-size:13px;">${message?if_exists}</div><br>
       		</#list>
       </#if>
</body>
