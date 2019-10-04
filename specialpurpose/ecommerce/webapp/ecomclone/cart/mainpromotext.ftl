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

<#--if showPromoText>
	<div class="promotion">
    			<div id="slider" >
					<#list productPromos as productPromo>
						<#if productPromo.thirdImageUrl?has_content>
							<#assign  imageUrl = productPromo.thirdImageUrl>
							<#assign  imageUrl1 = "/images/promo/third/10180.jpg">

							<#if imageUrl.equals(imageUrl1)>
								<#assign productUrl = Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "APPLE13", productCategoryId, "", "", "")/>
								<a href="${productUrl}"><img src="${productPromo.thirdImageUrl}"/></a>
							<#--<#elseif (productPromo.productPromoId == "10323")>
								<a href="<@ofbizUrl>showPromotionDetails</@ofbizUrl>?productPromoId=${productPromo.productPromoId?if_exists}&utm_source=univercell&utm_medium=Homepage_Banner&utm_campaign=Nokia610"><img src="${productPromo.thirdImageUrl}"/></a>
							-->
							<#-- for specific products -->
							<#--elseif (productPromo.productPromoId == "110010")>
								<a href="#"><img src="${productPromo.thirdImageUrl}"/></a>
							
							<#elseif (productPromo.productPromoId == "110000")>
								<a href="#"><img src="${productPromo.thirdImageUrl}"/></a>
							
							<#elseif (productPromo.productPromoId == "10451")>
								<a href="#"><img src="${productPromo.thirdImageUrl}" usemap="#Map2">
									<map name="Map2"><area shape="rect" coords="183,148,270,179" href="http://www.apple.in/iphone/iphone-5"></map>
								</img>
								</a>
							<#elseif (productPromo.productPromoId == "10452")>
								<a href="#"><img src="${productPromo.thirdImageUrl}" usemap="#Map2"/></a>
							<#elseif (productPromo.productPromoId == "10434")>
								<a href="#"><img src="${productPromo.thirdImageUrl}"/></a>
							<#elseif (productPromo.productPromoId == "10433")>
								<a href="#"><img src="${productPromo.thirdImageUrl}"/></a>
							<#elseif (productPromo.productPromoId == "10440")>
								<a href="#"><img src="${productPromo.thirdImageUrl}"/></a>
							<#elseif (productPromo.productPromoId == "10323")>
								<a href="<@ofbizUrl>showPromotionDetails</@ofbizUrl>?productPromoId=${productPromo.productPromoId?if_exists}&utm_source=univercell&utm_medium=Homepage_Banner&utm_campaign=Nokia610"><img src="${productPromo.thirdImageUrl}"/></a>
							<#-- for specific products -->

							<#--else>
								<a href="<@ofbizUrl>showPromotionDetails</@ofbizUrl>?productPromoId=${productPromo.productPromoId?if_exists}"><img src="${productPromo.thirdImageUrl}"/></a>
							</#if>						
						</#if>
    				</#list>
				</div>
	    <script type="text/javascript">
	    $(window).load(function() {
	        $('#slider').nivoSlider({directionNavHide:false});
	    });
	    </script>
    	
	</div>
</#if-->

<#--if bannerList?exists && bannerList?has_content>
<div class="promotion">
    			<div id="sliderB" style="height:240px;z-index:0;margin-left:10px;">
		<#list bannerList as banner>
			<#assign bannerImageUrl = banner.bannerImageUrl/>
			<#assign bannerLinkUrl = banner.bannerLinkUrl/>
				<#if bannerImageUrl?has_content>
					<#if bannerLinkUrl?has_content>	<a href="${bannerLinkUrl?if_exists}"><img src="${bannerImageUrl}"/></a> <#else> <img src="${bannerImageUrl}"/> </#if>
				</#if>
		</#list>
		</div>
	    <script type="text/javascript">
	    $(window).load(function() {
	        $('#sliderB').nivoSlider({directionNavHide:false});
	    });
	    </script>
    	
	</div>
	</#if-->
<style>
	.sliderImage_Home
	{
	float:left;
	width:99%;
	height:250px;
	-webkit-box-shadow:inset 0 0 10px 0 #c8c78e;
	box-shadow:inset 0 0 10px 0 #c8c78e;
	padding:5px;
	}
	.slider_left_name
	{
	width:17%;
	height:250px;
	float:left;
	background: #a4b357; /* Old browsers */
	background: -moz-radial-gradient(center, ellipse cover,  #6b6660 0%, #6b6660 100%); /* FF3.6+ */
	background: -webkit-gradient(radial, center center, 0px, center center, 100%, color-stop(0%,#6b6660), color-stop(100%,#6b6660)); /* Chrome,Safari4+ */
	background: -webkit-radial-gradient(center, ellipse cover,  #6b6660 0%,#6b6660 100%); /* Chrome10+,Safari5.1+ */
	background: -o-radial-gradient(center, ellipse cover,  #6b6660 0%,#6b6660 100%); /* Opera 12+ */
	background: -ms-radial-gradient(center, ellipse cover,  #6b6660 0%,#6b6660 100%); /* IE10+ */
	background: radial-gradient(ellipse at center,  #6b6660 0%,#6b6660 100%); /* W3C */
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#6b6660', endColorstr='#6b6660',GradientType=1 ); /* IE6-9 fallback on horizontal gradient */
	}
.slider_right_img
	{
	width:81%;
	float:left;
	padding-right:10px;
	}
 .main_banner_ul 
	{
	margin:0;
	padding:0;	
	}
.main_banner_ul li
	{	
	text-align:center;
	border-bottom:1px dashed #fff;
	font-weight:bold;
	color:#fff;
	cursor:pointer;
	} 
.main_banner_ul li:last-child
	{
	border:none;
	}
.main_banner_ul_li_hover
	{
	background: #008a00; /* Old browsers */
	background: -moz-linear-gradient(left,  #919050 0%, #919050 100%); /* FF3.6+ */
	background: -webkit-gradient(linear, left top, right top, color-stop(0%,#919050), color-stop(100%,#919050)); /* Chrome,Safari4+ */
	background: -webkit-linear-gradient(left,  #919050 0%,#919050 100%); /* Chrome10+,Safari5.1+ */
	background: -o-linear-gradient(left,  #919050 0%,#919050 100%); /* Opera 11.10+ */
	background: -ms-linear-gradient(left,  #919050 0%,#919050 100%); /* IE10+ */
	background: linear-gradient(to right,  #919050 0%,#919050 100%); /* W3C */
	filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#919050', endColorstr='#919050',GradientType=1 ); /* IE6-9 */
	}
</style>
	
			<#if bannerList?exists && bannerList?has_content>
	<#--div class="promotion">
    			<div id="sliderB" style="height:240px;z-index:0;margin-left:10px;"-->
    	<div class="sliderImage_Home">
    		<div class="slider_left_name">
	    		<ul class="main_banner_ul">
	    		<#assign count = 0>
					<#list bannerList as banner>
							<#assign bannerImageUrl = banner.bannerImageUrl/>
							<#assign bannerLinkUrl = banner.bannerLinkUrl/>
							<li data-img="${bannerImageUrl}" class="banner_category" id="banner_category_${count}" data-id="${count}" data-link="${bannerLinkUrl?if_exists}" onClick="window.location.href='${bannerLinkUrl?if_exists}'">${banner.categoryName?if_exists}</li>
							<#assign count = count + 1>	
					</#list>
				</ul>
    		</div>
    		<div class="slider_right_img">
    			<a href="" class="slider_right_img_link"><img src="http://www.youmart.in/images/banners/Guarantee%20banner%20home2.jpg" width="840" height="250"></a>
    		</div>
	    	
		</div>
		<div style="clear:both;"></div>
		<#--/div>
	</div-->
	</#if>

	<script>
		$(document).ready(function(){
			$("#banner_category_0").addClass("main_banner_ul_li_hover");
			$(".slider_right_img img").attr("src",$("#banner_category_0").attr("data-img"));
			$(".slider_right_img_link").attr("href",$("#banner_category_0").attr("data-link"));
			
			var categories_count=0;
			$(".banner_category").each(function(){
				categories_count=categories_count+1;
			});
			
			var individual_height=(((250/categories_count)/2)-8);
			$(".banner_category").each(function(){
				$(this).css("padding-top",individual_height).css("padding-bottom",individual_height);
			});
			
			$(".banner_category").mouseenter(function(){
				$(".banner_category").each(function(){
					$(this).removeClass("main_banner_ul_li_hover");
				});
				$(this).addClass("main_banner_ul_li_hover");
				var image=$(this).attr("data-img");
				var link=$(this).attr("data-link");
				$(".slider_right_img img").hide();
				$(".slider_right_img img").attr("src",image);
				$(".slider_right_img_link").attr("href",link);
				$(".slider_right_img img").show();
			});
			
			var stop_slider=0;
			$(".banner_category").mouseenter(function(){
				stop_slider=1;
				current_category=$(this).attr("data-id");
			});
			
			$(".banner_category").mouseleave(function(){
				stop_slider=0;
			});
						
			var current_category=1;
			setInterval(function(){
				if(current_category<categories_count){
					$(".banner_category").each(function(){
						$(this).removeClass("main_banner_ul_li_hover");
					});
					$("#banner_category_"+current_category).addClass("main_banner_ul_li_hover");
					if(stop_slider==0){
						var image=$("#banner_category_"+current_category).attr("data-img");
						var link=$("#banner_category_"+current_category).attr("data-link");
						$(".slider_right_img img").hide();
						$(".slider_right_img img").attr("src",image);
						$(".slider_right_img_link").attr("href",link);
						$(".slider_right_img img").show();
						current_category++;
					}
				}else{
					current_category=0;
				}
			},3000);
		});
	</script>