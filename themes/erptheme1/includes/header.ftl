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

<style>
	#cart_top_notification{
		width: 100%;
		position: fixed;
		top: 0;
		left: 0;
		z-index: 32768;
		background-color: #E9F0DA;
		font-family:arial;
		font-size: 18px;
		color: #63615E;
		text-align: center;
		padding: 15px 0;
		border-bottom: 1px solid #494A3C;
		cursor: pointer;
		display:none;
	}
	#cart_top_notification_product{
		font-size: 20px;
		font-weight:bold;
		color:#58595B;
	}
</style>

<div id="washoutOrder"></div>

<script type="text/javascript">

   $(document).ready(function ()
   {
 
     $("#btnShowSimple").click(function (e)
      {
         ShowDialog(false);
         e.preventDefault();
      });

      $("#btnClose").click(function (e)
      {
         HideDialog();
         e.preventDefault();
      });

   });

   function ShowDialog(modal)
   {
      $("#overlay").show();
      $("#dialog").fadeIn(300);

      if (modal)
      {
         $("#overlay").unbind("click");
      }
      else
      {
         $("#overlay").click(function (e)
         {
            HideDialog();
         });
      }
   }

   function HideDialog()
   {
      $("#overlay").hide();
      $("#dialog").fadeOut(300);
   }
       
</script>


<#--<script type="text/javascript">
	function myfunction(pageid)
	{
		switch(pageid)
		{
			
			case "foodgroceries":
			document.getElementById("officestationary").className="none";
			document.getElementById("foodgroceries").className="selected";
			break;
			
			case "officestationary":
			document.getElementById("foodgroceries").className="none";
			document.getElementById("officestationary").className="selectedoffice";			
			break;
			
			default:		
		}	
	}
</script>-->



<script type="text/javascript">
	function getCategoryProduct()
 		{
	   var selectedBoxValue=document.advancedsearchform.SEARCH_CATEGORY_ID.value;
	    var  param = 'categoryId=' + selectedBoxValue;
                      jQuery.ajax({url: "/control/getProductList",
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
          $('#productselect').html(data);
          getProductsBrands();
         }
    });
   
}

function getProductsBrands()
 		{
         jQuery.ajax({url: "/control/getProductsBrandsList",
         data: "",
         type: 'post',
         async: false,
         success: function(data) {
          $('#brandsselect').html(data);
         }
         
    });
   
}


	 
function cart_top_notification(value){ 
	$("#cart_top_notification").slideDown(400); 
	document.getElementById('cart_top_notification_product').innerHTML = value; 
	setTimeout(function(){$("#cart_top_notification").slideUp(400);},3000);
}
		
     

</script>
<div >
	<div id="cart_top_notification">
		Successfully added <span id="cart_top_notification_product">X Product</span> to the cart
	</div>
	
</div>
<script type="text/javascript">
             function loadUrl(prodId, url){
             	 var url = url+getProductIndex(prodId);
             	/* var res = name_index.split("   P   ");
	              if(res.length == 2)
	              {
	              	url = url+"?name=FT"+res[0]+"&index="+res[1];
	              }*/
             	 window.location = url;
             }
             function getProductIndex(prodId)
		 		{
		 		var res = prodId.split("p_");
		                  if(res.length == 2)
		                  {
		                  	prodId = res[1];
		                  }
		                  var datas = "";
			         jQuery.ajax({url: "/control/getProductIndex?productId="+prodId,
				         data: "",
				         type: 'post',
				         async: false,
				         success: function(data) {
				          	datas = data;
				         }
			    	});
			    	return datas;
				}
        </script>
        <script type="text/javascript">
            function loadBrands(){
                var brandsSelected= $("#BRAND_NAME").val();
                var productList = "";
                var  param = 'brand=' + brandsSelected;
                jQuery.ajax({url: "/control/autobrandname",
		         data: param,
		         type: 'post',
		         async: false,
		         success: function(brands) {
		          brandsList = brands.split(',');
		         }
		    	});
                //Returns the javascript array of sports products for the selected sport.
                return brandsList;
            	}
             function autocompleteBrands(){
                var brands = loadBrands();
                $("#BRAND_NAME").autocomplete({
                     source: brands
                 });
             }
             
             function changeText()
             {
             document.getElementById("SEARCH_STRING").value = 'Category';
             }
             function changeText1()
             {
             document.getElementById("SEARCH_STRING").value = 'Products';
             }
             function changeText2()
             {
             document.getElementById("SEARCH_STRING").value = 'Brands';
             }
             </script>
             
<style>
.black{color:#605E5E !important;}
.orange{color:#CC4E06 !important;}
.green{color:#0D9C4D !important;}
.black{color:#000000 !important;}
.maroon{color:#8B2F32 !important;}
.link-outer{
	background: none repeat scroll 0 0 #ededed;
    border-bottom: 1px solid #5d5d5d;
    float: left;
    font-family: arial;
    font-size: 11px;
    font-weight: bold;
    padding: 2px;
    text-align: left !important;
    width: 100%;
}
</style>
             
<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />
<#assign shoppingCart = sessionAttributes.shoppingCart?if_exists>
<#if shoppingCart?has_content>
    <#assign shoppingCartSize = shoppingCart.size()>
<#else>
    <#assign shoppingCartSize = 0>
</#if>
<#assign catalogId = "VEGFRUIT">
<div id="ecom-header">
	<div class="topheader">
		<div class="link-outer">
			<span class="green">Location: Bangalore </span>  &nbsp; | &nbsp;
			<span class="black">Cash / Card on Delivery</span> &nbsp;| &nbsp;
			<span class="orange">Free shipping for orders above  <img style="padding-bottom:5px;" src="/erptheme1/rs.png"/>&nbsp;500/- </span> &nbsp;|&nbsp; 
			<span class="maroon">Same day delivery: Order by 1 PM</span>
			<span class="black" style="float:right;"> (080) 45 45 88 88&nbsp;&nbsp;&nbsp;</span>
		</div>
		<div id="left" style="margin-top:4px;">
	      	<#if sessionAttributes.overrideLogo?exists>
	        	<a href="<@ofbizUrl>home?CURRENT_CATALOG_ID=VEGFRUIT&selected=VEGFRUIT</@ofbizUrl>"><img src="<@ofbizContentUrl>${sessionAttributes.overrideLogo}</@ofbizContentUrl>" alt="Logo" style="width:144px; height:65px;"/></a>
	      	<#elseif catalogHeaderLogo?exists>
	        	<a href="<@ofbizUrl>home?CURRENT_CATALOG_ID=VEGFRUIT&selected=VEGFRUIT</@ofbizUrl>"><img src="<@ofbizContentUrl>${catalogHeaderLogo}</@ofbizContentUrl>" alt="Logo" style="width:144px; height:65px;"/></a>
	      	<#elseif layoutSettings.VT_HDR_IMAGE_URL?has_content>
	        	<a href="<@ofbizUrl>home?CURRENT_CATALOG_ID=VEGFRUIT&selected=VEGFRUIT</@ofbizUrl>"><img src="<@ofbizContentUrl>${layoutSettings.VT_HDR_IMAGE_URL.get(0)}</@ofbizContentUrl>" alt="Logo" style="width:144px; height:65px;"/></a>
	      	</#if>
	      	
	      	<div class="topsearch">
	     		<form name="advancedsearchform1" method="get" action="<@ofbizUrl>keywordsearch</@ofbizUrl>">
	     			<input type="hidden" name="VIEW_SIZE" value="10"/>
	  				<input type="hidden" name="PAGING" value="Y"/>
	  				<input type="hidden" name="filterBy" value=""/>
	  				<input type="hidden" name="sortBy" value="POPULAR_PRD"/>
	  				<input type="hidden" name="filterByCategory" value=""/>
	  				<#assign SEARCH_STRING = "Search for Items">
	     			<#assign SEARCH_STRING_Brand = "Search for Brand">
	  				<ul style="width:100%; float:left; margin-top:3px;">
		     			<li><div id='productselect'><input type="text" name="SEARCH_STRING" id="SEARCH_STRING" autocomplete="off" class="main-search" onKeyup="autocompleteProducts()" onblur="if (this.value == '') { this.value = 'Search for Items'; }" onfocus="if (this.value == 'Search for Items') {this.value = ''; }" size="40"  value="${SEARCH_STRING?if_exists}"/></div>
		     			
		     			<div id="suggestions"></div>
		     			</li>
		     			<#--li><div id='brandsselect'><input type="text" name="BRAND_NAME" id="BRAND_NAME" class="main-search" onblur="if (this.value == '') { this.value = 'Search for Brand'; }" onKeyup="autocompleteBrands()" style="width:100px" onfocus="if (this.value == 'Search for Brand') {this.value = ''; }" size="40"  value="${SEARCH_STRING_Brand?if_exists}"/></li-->
		     			<li><input type="image" src="/erptheme1/searchicon.png"/></li>
		     		</ul>
	     		</form>
	     	</div>
	     	
	     	
	     	
   		</div>
    	
     	<ul class="rightcart" style="width:360px;float:right;"> 
     		<#if userLogin?has_content && userLogin.userLoginId != "anonymous">
		   		<li style="float: right;">
		   			<#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
					<#assign person = delegator.findByPrimaryKeyCache("Person", findPftMap)>
					<a href="#">Welcome ${person.firstName?html} ${person.lastName?if_exists}</a> | <a href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout} </a> 
				</li>
		   		<#else/>
		   		 <#assign check = request.getRequestURI()?if_exists>
		   		<#if  check?exists && check?has_content  &&  check?contains("checkLogin")>
		   		<li style="float: right"><span style="float:left; margin-right:10px;"><a href="<@ofbizUrl>newcustomer</@ofbizUrl>" class="register123"><img src="/erptheme1/register.png"/><#--${uiLabelMap.EcommerceRegister}--> </a> </span> <a href="<@ofbizUrl>/checkLogin/category?${request.getQueryString()?if_exists}</@ofbizUrl>" class=""><img src="/erptheme1/login.png"/></a></li>
		   		<#else>
		   		<li style="float: right"><span style="float:left; margin-right:10px;"><a href="<@ofbizUrl>newcustomer</@ofbizUrl>" class="register123"><img src="/erptheme1/register.png"/><#--${uiLabelMap.EcommerceRegister}--> </a> </span> <a href="<@ofbizUrl><#if checkLoginUrl == "/checkLogin/newcustomer">/checkLogin/home<#else>${checkLoginUrl}</#if><#if request.getQueryString()?has_content>&<#else>?</#if>productCategoryId=${parameters.get("productCategoryId")?if_exists}</@ofbizUrl>" class=""><img src="/erptheme1/login.png"/></a></li>
		   		</#if>
 			</#if>
			
        	<#--li class="cartinfo" onclick="window.location.href='/control/showcart'"><div class="cartinfo-inner">${screens.render("component://ecommerce/widget/ecomclone/CartScreens.xml#microcart")}</div><div class="cartinfo-last"></div></li-->
          
      	</ul>
      	<ul class="rightcart">
      		<li class="cartinfo" onclick="window.location.href='/control/showcart'"><div class="cartinfo-inner">${screens.render("component://ecommerce/widget/ecomclone/CartScreens.xml#microcart")}</div><div class="cartinfo-last"></div></li>
      	</ul>
      	<span><img src="/images/app.png" class="appImg"/></span>
	</div>
    
    <script>
		function toggleContent()
		{
		
			if (document.getElementById('content').style.display === 'none')
			{
				document.getElementById('content').style.display="block";
				document.getElementById('location_popup_background').style.display="block";
				document.getElementById('content2').style.display="none";
			
			}
			else
			{
				document.getElementById('content').style.display="none";
				document.getElementById('content2').style.display="block";
			}
		
			document.getElementById('content1').style.display="none";
		}
			
	</script>
	
	
	
	
	<div style="width: 100%;height: 100%;position: fixed;top: 0px;left: 0px;z-index:99999999999;background: black;opacity: 0.8;display: none;" id="location_popup_background"></div>
    <div id="content" style="display: none;width: 350px;float: left;height: auto;margin-left: 103px;position: fixed;top: 40%;left: 30%;z-index: 999999999999;background:white;">
    <div>
    	<span style="float:right;color:black;margin-top:-10px;margin-right:-10px;font-size:13px;font-weight:bold;cursor:pointer;" id="location_popup_close">X</span>
 	     <table cellspacing="0" align="center" style="width:150px;margin: 12px auto;">
		     <tr><td colspan="2" style="text-align:center"><h3>Location Search</h3> </td></tr>
			 <tr>
			     <td class="label" style="padding-top:6px;"><span style="font-weight:normal; font-size:14px !important;">PinCode Number</span></td>
				
				 <td><div id='productselect' class="input-field">
						 <table cellspacing="0" align="center" style="background:#d1d3ab; border-radius:4px; -moz-border-radius:4px; -webkit-border-radius:4px; border:1px solid #b4b46c;">
							<tr>
								<td>
						           <input type="text" name="locationSearch" id="locationSearch" style="border:none !important;" class="main-search1"  value="" onkeypress="return onlyNumbers(event);"/>
						        </td>
						        <td>
						          <input type="image" style="background:#d1d3ab;" src="/erptheme1/searchicon.png" onClick="return pinNumber();"/>
						        </td>
						    </tr>
						  </table>
				    </div>
				</td>
			 </tr>
	     </table>   
	     		<div id="testAreaHidden"></div>
      </div>
      <div style="display:none; width:310px; float:left; height:auto; margin-left:20px; " id="content1"></div>
     </div>
 <script type="text/javascript">
 function addInput(){
 var emailPattern = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
 var emailId = document.getElementById('emailId').value;
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
  }
  return true;
 }
 </script>
 
 <script type="text/javascript"> 
    function onlyNumbers(evt){
		var charCode = (evt.which) ? evt.which : event.keyCode
         if (charCode > 31 && (charCode < 48 || charCode > 57))
            return false;

         return true;
}
    
 </script>
<#--
 <div id="content2" style="float:left;width:330px;padding:0px;">
 <marquee>Welcome to YouMart!! We are getting ready to serve you & will go live in few days.</marquee>  
  </div> -->
  
    <div id="right">
     	<div class="headertopnav">
	      	<ul class="rightcart">
	     			<li style="padding:0 0 0 0 !important;"><img src="/erptheme1/location1.jpg" alt="" title=""/></li>
	     			<li><button type="button" id="main-location" style=" font-size:12px; cursor:pointer; padding:4px 0 0 0px; color:#14a155 !important; background:none !important; border:none !important;" onClick="toggleContent()">Location Search</button> <span style="color:#14a155 !important;">|</span> </li>
	     			<li><img src="/erptheme1/trackorder.jpg" alt="" title=""/ ></li>
	     			<li><a href="<@ofbizUrl>trackOrder</@ofbizUrl>" style="color:#919a0b !important;">Order Tracker |</a> </li>
	     			<li><img src="/erptheme1/myaccout1.jpg" alt="" title=""/></li>
	     			<li><a href="<@ofbizUrl>myaccount</@ofbizUrl>" style="color:#ed670e !important;" <#if headerItem?has_content && headerItem == "contactus">class="selected"</#if>>${uiLabelMap.EcommerceMyAccount}  |</a></li>
	     			<li><img src="/erptheme1/help1.jpg" alt="" title=""/></li>
	     			<li><a href="<@ofbizUrl>faqs</@ofbizUrl>" style="color:#903230 !important;">FAQ's</a> </li>
	     		</ul>
	     	</div>
    </div>
	<#--div id="middle">
      	<#if !productStore?exists>
        	<h2>${uiLabelMap.EcommerceNoProductStore}</h2>
      	</#if>
   	</div-->
</div>
<div class="topnav">
	<div id="ecom-header-bar">
		<div style="float:left;"><a href="<@ofbizUrl>home</@ofbizUrl>"><img src="/erptheme1/home-icon.jpg" alt="home"/></a></div>
		 <nav id="mainmenu">
	  	<ul id="">
	  
			<#if catalogId?has_content>
		 <#assign currentCatalogId = catalogId>
			<li id="browseshop" class="has_submenu">
				<a href="#">Browse Shop <i class="fa fa-sort-desc"></i></a> 
				<div class="first_div">
					<ul>
				 		<#if currentCatalogId?exists>
							<#assign CategoryList = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, currentCatalogId)?if_exists>
							<#assign CategoryListSub1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(delegator,"topLevelList", CategoryList, false, false, false)?if_exists>
							
			 			    <#list CategoryListSub1 as CategoryListSub2>
				 			    <li class="has_submenu_menu">
				 			   	<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", CategoryListSub2.productCategoryId, "")}?CURRENT_CATALOG_ID=${currentCatalogId}" class="atul">
				 			 	 ${CategoryListSub2.categoryName} <i class="fa fa-caret-right"></i></a>
				 			    	<div class="second_div">
				 			     		 <#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", CategoryListSub2.productCategoryId, true)>
				 			     		 <ul style="float:left;">
				 			     		 <#list subCatList as subCat>
				 			     		 <#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)>
  				 			     		 	<li class="<#if subCatLists1?has_content>has_submenu_menu_submenu yes_submenu<#else>has_submenu_menu_submenu no_submenu</#if>"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}">${subCat.categoryName}<#if subCatLists1?has_content><i class="fa fa-caret-right"></i></#if></a>
				 			     		 	<#--<li class="<#if subCatListsCount gt 0>has_submenu_menu_submenu<#else>no_submenu_menu_submenu</#if>"><a href="#">${subCat.categoryName}<#if subCatListsCount gt 0><i class="fa fa-caret-right"></i></#if></a> -->
				 			     		 	  <div class="third_div">
				 			     		 	  	<ul>
				 			     		 	 		<#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)>
				 			     		 	 		 <#list subCatLists1 as subCat1>
					 			     		 	  		<li>
					 			     		 	  			<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "",subCat1.productCategoryId, subCat.productCategoryId,CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}"> ${subCat1.categoryName}</a>
					 			     		 	  		</li>
				 			     		 	  		</#list>
				 			     		 	  	</ul>
				 			     		 	  <div class="hassubmenu_img sub_sub_menu_image">
                                                <img src="${CategoryListSub2.linkOneImageUrl?if_exists}" width="300" height="495"/>
                                             </div>
				 			     		 	  	</div>
				 			     		 	</li>
				 			     		 </#list>
				 			     		 </ul>
				 			     		 <div class="hassubmenu_img sub_menu_image">
                                    		<img src="${CategoryListSub2.linkOneImageUrl?if_exists}" width="300" height="495"/>
                               		 	</div>
									</div> 
				 			    </li>
						    </#list>
					    </#if>
				</ul>
				</div>
			</li>
		</#if>
			
 	  		<li id="homenav"><a href="<@ofbizUrl>home</@ofbizUrl>">Home</a></li>
			<#--<li id="favourite"><a href="<@ofbizUrl>editFavouritesList</@ofbizUrl>">Favourites</a></li>-->
		<#--	<li id="chefzone"><a href="<@ofbizUrl>viewChefZone</@ofbizUrl>">Chef's Zone</a></li>-->
			<li id="savings"><a href="<@ofbizUrl>viewprofileSavings</@ofbizUrl>">YouMart Saving</a></li>
			<#--<li id="budgetplan"><a href="<@ofbizUrl>viewprofileBudget</@ofbizUrl>">Budget Plan</a></li>-->
			<li id="discount"><a href="/products/DISCOUNTS?CURRENT_CATALOG_ID=VEGFRUIT">Offers</a></li>
			<li id="savings"><a href="/products/NEW_ARRIVAL?CURRENT_CATALOG_ID=VEGFRUIT">New Arrival</a></li>
			<#assign cat = delegator.findOne("ProductCategory",{"productCategoryId":"BACKTOSCHOOL"}, true) />
			<li id="summerspecial"><a href="/products/BACKTOSCHOOL"><#if cat?has_content>${cat.categoryName?default("Back to School")}</#if></a></li>
	  		<#--<li id="homenav"><a href="<@ofbizUrl>home</@ofbizUrl>">HOME</a></li>
			<li id="favourite"><a href="<@ofbizUrl>editFavouritesList</@ofbizUrl>">FAVOURITES</a></li>
			<li id="chefzone"><a href="<@ofbizUrl>viewChefZone</@ofbizUrl>">CHEF's ZONE</a></li>
			<li id="savings"><a href="<@ofbizUrl>viewprofileSavings</@ofbizUrl>">YOUMART SAVINGS</a></li>
			<li id="budgetplan"><a href="<@ofbizUrl>viewprofileBudget</@ofbizUrl>">BUDGET PLAN</a></li>
			
			<li id="recipies"><a href="#">RECIPIES</a></li>
			<li id="breakfast"><a href="#">BREAKFAST</a></li>
			<li id="daytoday"><a href="#">DAY TO DAY</a></li>-->
			<!--li id="quickshop" style="float:right; background:none !important;"><a href="<@ofbizUrl>quickshop</@ofbizUrl>"></a></li-->
	 	</nav>	
 </ul>
		<div style="float:right; padding:5px;">                           
	      	<script type="text/javascript">
				function keywordsearch(){
					var ser = document.getElementById('SEARCH_STRING').value;
					if (ser == "Search"){
						document.getElementById('SEARCH_STRING').value = '';
					}
					document.minikeywordsearchform.submit();
				}
			</script>
		</div>
	</div>
</div>
     	
     	<!--div style="width: 100%;height: 100%;position: fixed;top: 0px;left: 0px;z-index: 999999999;background: black;opacity: 0.3;display:none;" id="grey_layout"></div-->
     	
<script>
$(document).ready(function(e) {
	var min_height=495;
	$(".first_div").css("height","495px");
	
    $(".yes_submenu").mouseenter(function(e) {
		$(".sub_menu_image").hide();
		$(".first_div").css("width","976px");
    });
	$(".yes_submenu").mouseleave(function(e) {
		$(".sub_menu_image").show();
		$(".first_div").css("width","751px");
    });
    $(".no_submenu").mouseenter(function(e) {
		$(".sub_sub_menu_image").hide();
		$(".first_div").css("width","751px");
    });
    $(".no_submenu").mouseleave(function(e) {
		$(".sub_sub_menu_image").show();
    });
    
    $(".first_div > ul > li").mouseenter(function(){
    	var first_div_height=$(this).parent("ul").outerHeight();
    	var second_div_height=$(this).children(".second_div").children("ul").outerHeight();
    	if(first_div_height>second_div_height){
    		if(first_div_height>min_height){
    			min_height=first_div_height;
    		}
    		$(".first_div").css("height",min_height+1);
    	}else{
    		if(second_div_height>min_height){
    			min_height=second_div_height;
    		}
    		$(".first_div").css("height",min_height+1);
    	}
    	if(second_div_height<first_div_height){
    		$(this).children(".second_div").children("ul").css("height",first_div_height);
    	}
    	if(first_div_height<300 && second_div_height<300){
    		//$(".first_div").css("height","301px");
    	}
    });
    
    $(".first_div .has_submenu_menu").mouseenter(function(){
    	$(".first_div").css("width","751px");
    });
    
    $(".first_div .has_submenu_menu").mouseleave(function(){
    	$(".first_div").css("width","225px");
    });
    
    $(".first_div").mouseenter(function(){
    	$("#grey_layout").show();
    	$("#browseshop").addClass("browseshop_hover");
    	$(".first_div").css("margin-left","0px");
    });
    
    $(".first_div").mouseleave(function(){
    	$("#grey_layout").hide();
    	$("#browseshop").removeClass("browseshop_hover");
    	$(".first_div").css("margin-left","-5px");
    });
    
    
    $(".second_div > ul > li").mouseenter(function(){
    	var second_div_height=$(this).parent("ul").outerHeight();
    	var third_div_height=$(this).children(".third_div").children("ul").outerHeight();
    	if(third_div_height<second_div_height){
    		$(this).children(".third_div").children("ul").css("height",second_div_height);
    	}
    });
    
    $("#browseshop").mouseenter(function(){
    	var height=$(this).children(".first_div").children("ul").outerHeight();
    	$(".first_div").css("width","225px");
    	//$(".first_div").css("height",height);
    });
    $(".first_div").mouseenter(function(){
    	$(".first_div").css("width","751px");
    });
    
	$("#location_popup_close").click(function(){
		$("#content").hide();
		$("#location_popup_background").hide();
	});
	
	$("#site_overlay").hide();
});
</script>
