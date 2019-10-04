<script type="text/javascript">

	$(document).ready(function(){
		$('#recipeType').change(function(){
			var recipeType = $('#recipeType').val();
			$.ajax({
				type:"POST",
				url :"/control/showAjaxRecipeProduct?recipeType="+recipeType,
				success:function(res){
				
					$('#typeShow').show();
					$('#typeShow').html(res);
				}
			});
		});
		});
</script>
      <#if recipeTypeList?has_content>	
      <div style="margin-bottom:20px; width:400px;">
		<label style="font-weight:bold; padding-left:10px; width:120px !important; padding:7px 0 0 0;">Recipe Type</label>
			<select id="recipeType" name="recipeType" style="width:177px;">
				<option value="all">All</option>
				<#list recipeTypeList as recipeType>
						<option value="${recipeType.enumId?if_exists}" 
		
								<#if recipeType1?has_content && recipeType.enumId == recipeType1>
									selected = "selected"
								</#if>>
								${recipeType.description?if_exists}
						</option>
				 </#list>
		    </select>
			   
	  </div>
		</#if>

<div id="typeShow">
<#if recipeTypeMangList?has_content>
	<#assign count = 1>
	 <#list recipeTypeMangList as recipe>
	 
	 	<div class="recipelist">
	 	 	<div class="recipelist-left" style="float:left;">
	 	 	<div style="clear:both;"><a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}"><img src="${recipe.recipeImgUrl?if_exists}" style="width:170px; height:133px;"/></a></div>
			    
			    
				  <#-- <#if recipeManagementId?has_content && recipeManagementId == recipe.recipeManagementId>
				    	<a href="<@ofbizUrl>displayrecipeWeek</@ofbizUrl>" class="buttontextblue">Chef Of the week</a>
				    </#if> -->
				    
			    <#-- ${recipe.createdDate?if_exists}<br /><br />-->
			    
		    </div>
		    
		    
		    <#assign createdBy = Static["org.ofbiz.recipes.RecipeEvents"].partyName(delegator,recipe.createdBy)?if_exists>
				<#if !createdBy?has_content>
					<#assign createdBy = recipe.createdBy>
				</#if>
		    <div class="recipelist-right" style="float:left;width:280px; ">
		    <h1 style="font-size:16px !important;text-transform:uppercase;"><!--${count?if_exists} )--> <a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}" style="font-weight:normal;color:#ED670E !important;">${recipe.recipeName?if_exists}</a> </h1>
			<div style=" float:left; margin-bottom:15px;"><strong>Recipe by:</strong> ${createdBy?if_exists}</div>
			<div class="clear"></div>	
				<#-- <h1>Method</h1>
				<p class="recipedes">${recipe.description?if_exists}</p> -->
				
				<#--<p style="text-align:right; padding-right:40px;"><a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}">Read More....</a></p>-->
				<#assign reviewRating = Static["org.ofbiz.recipes.RecipeEvents"].averageReviewRating(delegator,recipe.recipeManagementId)?if_exists>
				<#if reviewRating?has_content>
					<div style="background-image:url('/images/star-grey.png'); background-repeat: no-repeat; width:60px; height:11px">
					<div style="background-image:url('/images/star-yellow.png'); background-repeat: no-repeat; width:${reviewRating?if_exists}px; height:11px">
					</div><br/> 
					</div>

				</#if>
			<div style="margin-bottom:10px;">
				<#assign reviewComment = Static["org.ofbiz.recipes.RecipeEvents"].averageReview(delegator,recipe.recipeManagementId)?if_exists>
			<#if reviewComment?has_content>
					Review :(${reviewComment?if_exists})
				</#if>
			</div>
				
			<div class="clear"></div>	
				<#--<strong>Recipe by:${recipe.createdBy?if_exists}</strong><br /><br />-->
				<!--div style=" clear:both; margin:30px 0 10px 0;"> <a href="recipeDetail?recipeId=${recipe.recipeManagementId?if_exists}" class="buttontextblue">View Detail</a> </div-->
				
				<#--if recipeManagementId?has_content && recipeManagementId == recipe.recipeManagementId>
			    	<div style=" position:relative; left:687px; top:-137px;"> <a href="<@ofbizUrl>displayrecipeWeek</@ofbizUrl>" class="buttontextblue">Chef Of the week</a> </div>
			    </#if-->
				
				
				<#--<#assign recipeIngredients = delegator.findByAnd("RecipeIngredients", Static["org.ofbiz.base.util.UtilMisc"].toMap("recipeManagementId", recipe.recipeManagementId))?if_exists>
				  
				  
		          	  <#if recipeIngredients?has_content>
					  <#list recipeIngredients as recipeIng>
					       <#if recipeIng.productId?has_content && recipeIng.productId!=''>
					       ${recipeIng.productId}kkk
					            <form method="post" action="<@ofbizUrl>additem1</@ofbizUrl>" name="the${recipeIng.productId}form" style="margin: 0;">
					              <input type="hidden" name="add_product_id" value="${recipeIng.productId}"/>
					              <input type="text" class="inputBox" size="3" name="quantity" value="1"/>
					              <input type="hidden" name="clearSearch" value="N"/>
					              <a href="javascript:addItem('the${recipeIng.productId}form')" class="buttontext">Add To Cart</a>&nbsp;
					              
					            </form>
		            </#if>
		            </#list>
                  </#if>-->
				<div class="clear"></div>
				<div>
					<#if recipe.statusId == "RECIPE_OF_WEEK">
					<a class="buttontextblue" href="<@ofbizUrl>recipeDetail</@ofbizUrl>?recipeId=${recipe.recipeManagementId?if_exists}">Recipe Of the Week</a>
					</#if>
				</div>
			</div>
		    
		<#assign count = count+1>
		</div>
	 </#list>
	 <#else>
	 <div class="recipelist">
	    No Ingredients 
	 </div>
</#if>
</div>

<script language="JavaScript" type="text/javascript">


  function addItems(add_product_id, formName) {
  alert('add_product_id'+add_product_id);
  alert('formName'+formName);
  
	var value= document.getElementById(add_product_id).value;
            if(value == 'NULL' || value == "")
            {
            alert("Please select pack size");
            
            }else{
		addItem(formName);
}
    }
    
function addItem(formName) {
    
   <#-- alert('formName:'+formName);
    var add_product_id= formName.add_product_id.value;
    alert("add_product_id");
    var quantity= formName.quantity.value;
    var clearSearch= formName.clearSearch.value;
       if (formName.add_product_id.value == 'NULL') {
           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
           return;
       }else {
           var  param = 'add_product_id=' + add_product_id + 
                      '&quantity=' + quantity;
                      jQuery.ajax({url: '/control/additem/showcart',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#minicart').html(data);
           jQuery.ajax({url: '<@ofbizUrl>findprodwgt</@ofbizUrl>',
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	         document.getElementById('outputs').innerHTML = data;
	         ShowDialog(false);
	         },
        error: function(data) {
        }
    	});
    	setTimeout(function() {
      location.reload();
    }, 1000);
         },
         complete:  function() { 
         var minitotal = document.getElementById('minitotal').value;
          var miniquantity = document.getElementById('miniquantity').value;
           $('#microCartTotal').text(minitotal);
           $('#microCartQuantity').text(miniquantity);
         },
        error: function(data) {
        }
    	});
    }}-->
</script>