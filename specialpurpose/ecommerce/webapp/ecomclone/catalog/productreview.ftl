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
<link href="/erptheme1/star/rating_simple.css" rel="stylesheet" type="text/css">
					 			<script type="text/javascript" src="/erptheme1/star/rating_simple.js"></script>

             

<#if requestParameters.product_id?exists>
  <form id="reviewProduct" name="reviewProduct"  method="post" action="<@ofbizUrl>createProductReview</@ofbizUrl>">
    <fieldset class="review_outline">
      <input type="hidden" name="productStoreId" value="${productStore.productStoreId}" />
      <input type="hidden" name="productId" value="${requestParameters.product_id}" />
      <input type="hidden" name="product_id" value="${requestParameters.product_id}" />
      <input type="hidden" name="category_id" value="${requestParameters.category_id}" />
		      <#--div>
				        <span><label for="one">${uiLabelMap.EcommerceRating}:</label></span>
				        <span>
				          <label for="one">1</label>
				          <input type="radio" id="one" name="productRating" value="1.0" />
				        </span>
				        <span>
				          <label for="two">2</label>
				          <input type="radio" id="two" name="productRating" value="2.0" />
				        </span>
				        <span>
				          <label for="three">3</label>
				          <input type="radio" id="three" name="productRating" value="3.0" />
				        </span>
				        <span>
				          <label for="four">4</label>
				          <input type="radio" id="four" name="productRating" value="4.0" />
				        </span>
				        <span>
				          <label for="five">5</label>
				          <input type="radio" id="five" name="productRating" value="5.0" />
				          <input name="my_input" value="" id="rating_simple2" type="hidden">
				        </span>
		      </div>
	      <div>
	        <span><label for="yes">${uiLabelMap.EcommercePostAnonymous}:</label></span>
	        <span>
	          <label for="yes">${uiLabelMap.CommonYes}</label>
	          <input type="radio" id="yes" name="postedAnonymous" value="Y" />
	        </span>
	        <span>
	          <label for="no">${uiLabelMap.CommonNo}</label>
	          <input type="radio" id="no" name="postedAnonymous" value="N" checked="checked" />
	        </span>
	      </div-->
	      <div class="review_content1">
	       <span><label for="one">Rating:</label></span>
	       <input name="productRating" value="" id="rating_simple2" type="hidden">
	      </div>
      <div class="review_content2">
     	<input type="hidden" id="no" name="postedAnonymous" value="N"/>
        <label for="review">${uiLabelMap.CommonReview}:</label>
        <textarea class="promoCodeinputBox" name="productReview" id="productReview" cols="40"></textarea>
      </div>
      <div>
     		<#--input type="submit" name="add" Value="Add" class="buttontext" onclick="return reviewComment();"/-->
      <a href="javascript:reviewComment()" class="buttontext">Add</a>
     		
        <#-- a href="javascript:document.getElementById('reviewProduct').submit();" class="button">[${uiLabelMap.CommonSave}]</a-->
        <a href="<@ofbizUrl>product?product_id=${requestParameters.product_id}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonCancel}</a>
      </div>
    </fieldset>
  </form>
<#else>
  <h2>${uiLabelMap.ProductCannotReviewUnKnownProduct}.</h2>
</#if>

<script language="javascript" type="text/javascript">
            $(function() {
            $("#rating_simple2").webwidget_rating_simple({
								rating_star_length: '5',
								rating_initial_value: '',
								rating_function_name: '',//this is function name for click
								directory: '/erptheme1/star'
                });
            });
            
function  reviewComment(){
  	var rating=document.getElementById('rating_simple2').value;
 	var message=document.getElementById('productReview').value;
 	 
 	 
 	if(rating==null || rating==""){
 	    alert("Select the rating!");
 	    document.getElementById('rating_simple2').focus();
 	    return false;
 	}
 	
 	if(message==null || message==""){
 	    alert("Enter the message!");
 	    document.getElementById('productReview').focus();
 	    return false;
 	}
 	
 	document.reviewProduct.submit();
}
</script>