<link type="text/css" rel="stylesheet" href="/images/zoomer/texteditor/jquery-te-1.4.0.css">
<script type="text/javascript" src="/images/zoomer/texteditor/jquery-te-1.4.0.min.js" charset="utf-8"></script>
<div id="sample">
	<script src="/images/htmlEditor/nicEdit.js" type="text/javascript"></script>
	<script type="text/javascript">
	bkLib.onDomLoaded(function() {
		new nicEditor().panelInstance('area1');
		new nicEditor().panelInstance('area2');
	});
	</script>
	<script>
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
	</script>
</div>
<#if recipe?has_content>
<form name="recipeform" id="recipeform" action="updateRecipe?recipeId=${recipe.recipeManagementId?if_exists}" method="post" enctype='multipart/form-data'>
	<div class="addrecipe">
		<#if recipeTypeList?has_content>	
		<label>Recipe Type</label>*: 
				<select id="recipeType" name="recipeType" style="width: 184px;margin-left: 13px;padding: 2px 0 2px;height: 22px;">
								<#list recipeTypeList as recipeType>
									<option value="${recipeType.enumId?if_exists}"
									<#if recipe.recipeType?has_content && recipe.recipeType == recipeType.enumId>
									selected="selected"
									</#if>
									>${recipeType.description?if_exists}</option>
								</#list>
			    </select>
		<br /><br />
		</#if>
		<input type="hidden" id="recipeManagementId" name="recipeManagementId" value="${recipe.recipeManagementId?if_exists}"/>
		<label>Recipe Name</label>*: <input type="text" size="30" id="recipeName" name="recipeName" value="${recipe.recipeName?if_exists}" style="height:17px;margin-left: 9px;">
		<div id="recipeNameError"></div>
		<br />
		<label style="padding-top:0px;">Recipe Image</label>*: <input type="file" id="file" name="file" style="margin-left:3px;border: #ddd solid 1px !important;width: 181px; padding: 2px 0 0 2px;" accept="image/*">
						<img src="${recipe.recipeImgUrl?if_exists}" width="30px" height="30px"/>
		<br /><br /><br />
		<div class="Shopcar_pageHead"><h2>Ingredients</h2></div>
		<#if recipeIngredientList?has_content>
			<#list recipeIngredientList as recipeIngredient>
				<#if recipeIngredient.productId?has_content>
						${recipeIngredient.productName?if_exists}
				<#else>
					${recipeIngredient.productName?if_exists}
				</#if>
				<a class="button" href="<@ofbizUrl>removeRecipeIng?recipeIngredientsId=${recipeIngredient.recipeIngredientsId}&recipeId=${recipeIngredient.recipeManagementId}</@ofbizUrl>">Remove</a>
				<br /><br />
			</#list>
		</#if>
		<br/><br/>
		
		<div class="Shopcar_pageHead"><h2>Add More Ingredients</h2></div>
		<input type="hidden" name="qty" value="" id="qty"/>
		<div id="div0">
			<@htmlTemplate.lookupField value="${requestParameters.productId?if_exists}" formName="recipeform" name="productId" id="productJumpFormProductId" fieldFormName="LookupProduct"/>
			<input type="text" name="quantity" value="">
		</div>
		
		<div class="Shopcar_pageHead"><h2>Method of preparation</h2> *</div>
		<textarea  id="area1" name="area1" rows="10" cols="150" value="" class="jqte-test">${StringUtil.wrapString(recipe.description)?if_exists}</textarea>
		<input type="hidden" id="description" name="description"/>
		<div id="descriptionError"></div>
		<div class="Shopcar_pageHead"><h2>Recommended Variations</h2></div>
		<textarea   id="area2" name="area2"  rows="10" cols="150" value="" class="jqte-test">${StringUtil.wrapString(recipe.variations)?if_exists}</textarea>
 		<input type="hidden" id="variations" name="variations"/>
 		<br/>
 		<span id="FindRecipes_fromDate_title"><h3>From Date </span> ${recipe.createdDate?if_exists}</h3><br />
        <span id="FindRecipes_thruDate_title"><h3>Thru Date</span>
        <input type="text" name="thruDate_i18n" value="${recipe.thruDate?if_exists}" 
        		title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="FindRecipes_thruDate_i18n"/>
		<input type="text" name="thruDate" style="height:1px;width:1px;border:none;background-color:transparent"
         value="${recipe.thruDate?if_exists}" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="FindRecipes_thruDate"/>
    	</h3>
    	<input type="hidden" name="" value="Timestamp"/>

		<input type="button" value="Update" onClick="recipeFormValidation();"/>
	</div>
</form>
	<#else>
		No Recipe found ....
    </#if>

<script>
	function recipeFormValidation(){
		   var recipeType =document.getElementById("recipeType");
		   var recipeName =document.getElementById("recipeName").value;

		   document.getElementById("description").value = nicEditors.findEditor('area1').getContent();
		   document.getElementById("variations").value = nicEditors.findEditor('area2').getContent();
		   
 		   var mthdOfPreparation = document.getElementById("description");
		   
		   var valid = false;
		   
		   if(recipeType != null && recipeType.value != ""){
              valid = true;              		   		
		   }else{
		      valid = false;
		      return false;
		   }
		   
		   if(valid != false && recipeName != "" && recipeName.trim() != ""){
		   	  valid = true;	
		   }else{		      
		      valid = false;
		      document.getElementById("recipeNameError").innerHTML = "<font color='red'>Please enter the RecipeName</font>";
		   }		     
		   
		   if(valid != false && mthdOfPreparation != null && mthdOfPreparation.value != "" && mthdOfPreparation.value != "<br>"){
		      valid = true;
		   }else{
		      valid = false;
		      document.getElementById("descriptionError").innerHTML = "<font color='red'>Please enter the method of preparation</font>";
		   }
		   
		   if(valid != false){
		   	   document.recipeform.submit();
		   }else
		   {
		   		return false;
		   }
		 } 

	function changeRecipeCommentStatus(statusIdTo){
		document.getElementById("statusIdTo").value= statusIdTo;
		document.recipeCommentList.submit();
    }
</script>
 <script type="text/javascript">
              if (Date.CultureInfo != undefined) {
                  var initDate = "";
                  if (initDate != "") {
                      var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime;
                      if (initDate.indexOf('.') != -1) {
                          initDate = initDate.substring(0, initDate.indexOf('.'));
                      }
                      var ofbizTime = "yyyy-MM-dd HH:mm:ss";
                      var dateObj = Date.parseExact(initDate, ofbizTime);
                      var formatedObj = dateObj.toString(dateFormat);
                      jQuery("#FindRecipes_thruDate_i18n").val(formatedObj);
                  }

                  jQuery("#FindRecipes_thruDate").change(function() {
                      var ofbizTime = "yyyy-MM-dd HH:mm:ss";
                      var newValue = ""
                      if (this.value != "") {
                          var dateObj = Date.parseExact(this.value, ofbizTime);
                          var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime;
                          newValue = dateObj.toString(dateFormat);
                      }
                      jQuery("#FindRecipes_thruDate_i18n").val(newValue);
                  });
                  jQuery("#FindRecipes_thruDate_i18n").change(function() {
                      var dateFormat = Date.CultureInfo.formatPatterns.shortDate + " " + Date.CultureInfo.formatPatterns.longTime;
                      var newValue = ""
                      if (this.value != "") {
                          var dateObj = Date.parseExact(this.value, dateFormat);
                          var ofbizTime = "yyyy-MM-dd HH:mm:ss";
                          newValue = dateObj.toString(ofbizTime);
                      }
                      jQuery("#FindRecipes_thruDate").val(newValue);
                  });
              } else {
                  jQuery("#FindRecipes_thruDate").change(function() {
                      jQuery("#FindRecipes_thruDate_i18n").val(this.value);
                  });
                  jQuery("#FindRecipes_thruDate_i18n").change(function() {
                      jQuery("#FindRecipes_thruDate").val(this.value);
                  });
              }

                 jQuery("#FindRecipes_thruDate").datetimepicker({
                    showSecond: true,
                    timeFormat: 'hh:mm:ss',
                    stepHour: 1,
                    stepMinute: 1,
                    stepSecond: 1,
                    showOn: 'button',
                    buttonImage: '',
                    buttonText: '',
                    buttonImageOnly: false,
                    dateFormat: 'yy-mm-dd'
                  })
              
              ;
          </script>
