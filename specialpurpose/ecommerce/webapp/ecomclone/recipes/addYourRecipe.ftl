<link type="text/css" rel="stylesheet" href="/images/zoomer/texteditor/jquery-te-1.4.0.css">
<script type="text/javascript" src="/images/zoomer/texteditor/jquery-te-1.4.0.min.js" charset="utf-8"></script>

<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js"></script>

<form name="recipeform" id="recipeform" action="createRecipe" method="post" enctype='multipart/form-data'>
	<div class="addrecipe">
	    <div> * Indicates required field </div>
	    <div class="Shopcar_pageHead">Add Recipe</div>
		<#if recipeTypeList?has_content>	
		<label>Recipe Type</label>*: 
				<select id="recipeType" name="recipeType" style="width:177px;">
								<#list recipeTypeList as recipeType>
									<option value="${recipeType.enumId?if_exists}">${recipeType.description?if_exists}</option>
								</#list>
			    </select>
		<br /><br />
		</#if>
		<label>Recipe Name</label>*: <input type="text" size="30" id="recipeName" name="recipeName" value=""><br /><br />
		<label style="padding-top:0px;">Recipe Image</label>*: <input type="file" id="file" name="file" style="margin-left:3px;" accept="image/*"><br /><br /><br />
		
		<div class="Shopcar_pageHead">Ingredients *</div>
		<input type="hidden" name="qty" value="" id="qty"/>
		<div id="div0">
			<input placeholder="Name of ingredient" type="text" size="45" id="ingredients_1" name="ingredients_1"  autocomplete="off" onKeyup="autocompleteIngredients('1');"/>
			<input type="hidden" size="5" id="ingredients_Prod_1" name="ingredients_Prod_1"/>
			<input placeholder="Qty" type="text" size="5" id="ingredients_Qty_1" name="ingredients_Qty_1"/>
			<input id="btn1" type="button" value="+" onclick="createDiv();" /> 
		</div>
		
		<br /><br />
		<div class="Shopcar_pageHead">Method of preparation *</div>
		<textarea  id="area1" name="area1"     rows="10" cols="150" value="" class="jqte-test"></textarea>
	 
		<input type="hidden" id="description" name="description"/>
		<div class="Shopcar_pageHead">Recommended Variations</div>
		<textarea   id="area2" name="area2"  rows="10" cols="150" value="" class="jqte-test"></textarea>
 		<input type="hidden" id="variations" name="variations"/>
		<b>Disclaimer:</b> 
		<br /><br />
		a)	In case your recipe will be selected for &#34;Chef of the week&#34;, are you willing to share your profile with other members of YouMart? (Y/N)
			  <select name="shareProfile" style="width:110px;">
				  <option value="Y">Yes</option>
				  <option value="N">No</option>
			  </select>
		<br />
		b)	Are you willing to allow other customers interact with you on the blog? (Y/N) 
			  <select name="allowComments" style="width:110px;">
				  <option value="Y">Yes</option>
				  <option value="N">No</option>
			  </select>
		<br />
		This is not a competition and the recipes uploaded will be selected at the sole discretion of the Management of YouMart. This is just an initiative to encourage homemakers to share their yummy & luscious homemade recipes and would appreciate your understanding that we will not be able to pick everyone at the same time.
		<br /><br />
		
		<input type="button" value="submit" onClick="recipeFormValidation();"/>
	</div>
</form>
<#--assign string = "">
<#assign listSize = productList.size()>
<#assign count =1>
<#if productList?has_content>
    	<#list productList as product>
    		<#if product.productName?has_content>
    			<#assign string = string+'"${product.productName?if_exists}"'>
    		<#else>
    			<#assign string = string+'"${product.interName?if_exists} ID--${product.productId?if_exists}"'>
    		</#if>
    		<#if listSize != count>
    			<#assign string = string+",">
    		</#if>
    		<#assign count =count+1>
    	</#list>
    </#if-->
<script type="text/javascript">
	$('.jqte-test').jqte();
	
	// settings of status
	var jqteStatus = true;
	$(".status").click(function()
	{
		jqteStatus = jqteStatus ? false : true;
		$('.jqte-test').jqte({"status" : jqteStatus})
	});

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
		 function createDiv(){
		     	count = count + 1;
		        var divTag = document.createElement("div"); 
		        divTag.id = "divTxt"+count;
		        divTag.setAttribute("align","left");
		        divTag.style.margin = "0px auto"; 
		        var ingredients = "ingredients_"+count;
		        var IngredientsProd = "ingredients_Prod_"+count;
		        var qty = "ingredients_Qty_"+count;
		        
		        var newText = '<input placeholder="Name of ingredient" type="text" size="45" id="'+ingredients+'" name="'+ingredients+'" onKeyup="autocompleteIngredients(\''+count+'\');"/>';
		        newText = newText + ' <input type="hidden" size="5" id="'+IngredientsProd+'" name="'+IngredientsProd+'"/>';
		        newText = newText + ' <input placeholder="Qty" type="text" size="5" id="'+qty+'" name="'+qty+'"/>';
		    	newText = newText + ' <input id="btn1" type="button" value="+" onclick="createDiv();" />';
		        newText = newText + " <input id='btn1' type='button' value='-' onclick='removeTxtDiv("+count+");'/>";
		         
		        divTag.innerHTML  += newText;
		        document.getElementById("div0").appendChild(divTag);
		        document.getElementById("qty").value = count;
		    }
		 function removeTxtDiv(divId){ 
		     document.getElementById("divTxt"+divId).innerHTML=""; 
		     return true;
		 }
		
		 function checkImageFile(fileName){
		   var validChk = "1";
		   if(fileName != ""){
		      var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
		      if(ext == "gif" || ext == "GIF" || ext == "JPEG" || ext == "jpeg" || ext == "jpg" || ext == "JPG" || ext == "png" || ext == "PNG"){
				 validChk ="1";
			  }else{
				 alert("Upload Gif or Jpg or Png images only");
				 validChk ="0";
			  }
		   }else{
		      validChk ="0";
		   }		   
		   return validChk; 
		 }
		  
		 function recipeFormValidation(){
		   var recipeType =document.getElementById("recipeType");
		   var recipeName =document.getElementById("recipeName").value;
		   var recipeImage =document.getElementById("file");
		   var ingredients =document.getElementById("ingredients_1");
 		    <#--document.getElementById("description").value = nicEditors.findEditor('area1').getContent();
		   document.getElementById("variations").value = nicEditors.findEditor('area2').getContent();-->
		   document.getElementById("description").value =   document.getElementById("area1").value  
		   document.getElementById("variations").value = document.getElementById("area2").value    
 		   var mthdOfPreparation =document.getElementById("description");
		   
		   var valid = false;
		   
		   if(recipeType != null && recipeType.value != ""){
              valid = true;              		   		
		   }else{
		      valid = false;
		      alert("Please select recipe type");
		      return false;
		   }
		   
		   if(valid != false && recipeName != "" && recipeName.trim() != ""){
		   	  valid = true;	
		   }else{		      
		      valid = false;
		      alert("Please enter the RecipeName");
		      return false;
		   }		     
		   
		   if(valid != false && recipeImage != null && recipeImage.value != ""){
		      var validImage = checkImageFile(recipeImage.value); 
		      if(validImage != "0"){
		   	  	valid = true; 
		   	  }else{
		   	  	valid = false;
		   	  	return false;
		   	  }	
		   }else{
		      valid = false;
		      alert("Please upload image");
		      return false;
		   }
		   
		   if(valid != false && ingredients != null && ingredients.value != ""){
		      valid = true; 
		   }else{
		      valid = false;
		      alert("Please enter the Ingredients");
		      return false;		   
		   }
		   
		   if(valid != false && mthdOfPreparation != null && mthdOfPreparation.value != ""){
		      valid = true; 
		   }else{
		      valid = false;
		      alert("Please enter the method of preparation");
		      return false;		   
		   }
		   
		   if(valid != false){
		   	   document.recipeform.submit();
		   }
		 }   
  </script>
  <div class="demo-container1" style="background:#FF0000">
</div>
<script type="text/javascript">
			var carMake = "";
            function loadIngredients(count){
                var productSelected= $("#ingredients_"+count).val();
                var productList = "";
                var indicator = null;
                var param = 'sport=' + productSelected +"&fromRecipePage=" + indicator;
               // var  param = 'sport=' + productSelected;
                jQuery.ajax({url: "/control/autoProductNameForRecipe",
		         data: param,
		         type: 'post',
		         async: true,
		         success: function(products) {
		         carMake = "";
		       // alert(products);
		         $( "div.demo-container1" ).html(products);
		         }
		    	});
                //Returns the javascript array of sports products for the selected sport.
                return productList;
            	}
            	
            	  
             function autocompleteIngredients(count){
              loadIngredients(count);
                $("#ingredients_"+count).autocomplete({
                     source: carMake,
                     select: function(event, ui) {
                     	  var res = ui.item.id;
                     	 // alert(res);
                     	 if(res != 'none'){
		                 	document.getElementById("ingredients_Prod_"+count).value = res;
		                 }
					    }
					  }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
      return $( "<li>" )
        .append( "<a>"+ item.desc + "</a>" )
        .appendTo( ul );
    };	 	
					  
             }
        </script>
 