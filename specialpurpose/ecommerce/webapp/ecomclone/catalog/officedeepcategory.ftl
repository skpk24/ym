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


        
        
<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />

<#if requestVal.equals("/home") || requestVal.equals("/login")>
	<script type="text/javascript" src="/erptheme1/js/webwidget_vertical_menu.js"></script>
	<script language="javascript" type="text/javascript">
            $(function() {
                $("#webwidget_vertical_menu").webwidget_vertical_menu({
                    menu_width: '200',
                    menu_height: '16',
                    menu_margin: '0',
                    menu_text_size: '12',
                    menu_text_color: '#1c334d',
                    menu_background_color: '#dedfbf',
                    menu_border_size: '0',
                    menu_border_color: '#000',
                    menu_border_style: 'solid',
                    menu_background_hover_color: '#dedfbf',
                    directory: 'images'
                });
                 
            });
        </script>
<#else>
	<script type="text/javascript" src="/erptheme1/js/webwidget_vertical_menu1.js"></script>
	<script language="javascript" type="text/javascript">
            $(function() {
                $("#webwidget_vertical_menu1").webwidget_vertical_menu1({
                    menu_width: '200',
                    menu_height: '16',
                    menu_margin: '0',
                    menu_text_size: '12',
                    menu_text_color: '#1c334d',
                    menu_background_color: '#dedfbf',
                    menu_border_size: '0',
                    menu_border_color: '#000',
                    menu_border_style: 'solid',
                    menu_background_hover_color: '#dedfbf',
                    directory: 'images'
                });
                 
            });
        </script>
</#if>


 
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>


				<#--assign catalogCol = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogIdsAvailable(request)?if_exists-->
				<#assign catalogCol = ["OFFICESTATIO"]>
				<#if requestVal.equals("/product") || requestVal.equals("/main") || requestVal.equals("/category")>
					<#--assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists-->
					<#assign currentCatalogId = "OFFICESTATIO">
					<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
				<#elseif requestVal.equals("/keywordsearch")>
					<#assign currentCatalogId = requestParameters.SEARCH_CATALOG_ID?if_exists>
					<#if currentCatalogId?has_content && !currentCatalogId.equals("")>
						<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
					</#if>
				</#if>
				<#if (catalogCol?size > 0)>
				<div class="catalog">
				<div class="sidedeeptitle">Category</div>
					<#assign counter = 1/>
					<#--assign x = catalogCol.size()?if_exists /-->
					<#--<div class="menu_list">-->
					<div <#if requestVal.equals("/home") || requestVal.equals("/login")>id="webwidget_vertical_menu" class="webwidget_vertical_menu"<#else>id="webwidget_vertical_menu1" class="webwidget_vertical_menu1"</#if> >
				    <#list catalogCol as catalogId>
				    	<ul><li class="sub_mainnavigation" style="position:relative;">
				    		<#if currentCatalogId?has_content && currentCatalogId == catalogId></#if>
					        <form name="CATEGORY_${catalogId?if_exists}${counter?if_exists}" method="post" action="<@ofbizUrl>main</@ofbizUrl>">
					      		<input type="hidden" name='CURRENT_CATALOG_ID' value="${catalogId}" />
						        <#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
						    	<#--<a href="<@ofbizUrl>main?CURRENT_CATALOG_ID=${catalogId}</@ofbizUrl>"> ${thisCatalogName}</a>-->
						    	<#assign CatUrl = Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", catalogId, "")/>
						    	<a href="javascript:void()"> ${thisCatalogName?if_exists}</a>
						    	
					        </form>
				        	<#if catalogId?exists>
				       			<#assign CategoryList = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, catalogId)?if_exists>
				   				<#assign CategoryListSub1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(delegator,"topLevelList", CategoryList, false, false, false)?if_exists>
				        		<#if CategoryListSub1?has_content>
				        				<#assign count =0>
		        							<#list CategoryListSub1 as CategoryListSub3>
		        							<#if count = 0><ul class="maindropdown"</#if>
		        							<#assign pcat = CategoryListSub3.productCategoryId>
		        							<#if productCategoryId?has_content && (productCategoryId ==pcat || (parameters.rootCategoryId?has_content && parameters.rootCategoryId==pcat))>
		        							<#assign pcatmain = CategoryListSub3.productCategoryId>
		        							</#if>
		        							<#if count+1 ==CategoryListSub1.size()>></#if>
		        							<#assign count =count+1>
		        							</#list>
		        								<#list CategoryListSub1 as CategoryListSub2>
		        								<li class="subcat" style="position:relative">
		        								<#--<div class="browsecategorylist dropdown">
											        <div class="browsecategorytext">-->
											       		<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", CategoryListSub2.productCategoryId, "")}?CURRENT_CATALOG_ID=${catalogId}" class="atul">${CategoryListSub2.categoryName}</a>
											        	<#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", CategoryListSub2.productCategoryId, true)>
											    		<#if subCatList?has_content>
											    			<ul class="sub_navigation" style="width:220px !important; border:1px solid red;">
											      				<#list subCatList as subCat> 
											          				<li style="position:relative; background:#dedfbf;"><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${catalogId}">${subCat.categoryName}</a></li>
											      				</#list>
											      			</ul>
											    		</#if>
											        <#--></div>
												</div>-->
												</li>
												
												
												
		        							</#list>
				        				</ul>
				        			
				        		</#if>
				        	</#if>
				        </li></ul>
				    </#list>
				    </div></div>
				</#if>
			<#--script type="text/javascript">
			var xyz="";
			 $(".subcat").click(function() {
				xyz = ($(this).hasClass("subcat"));
			        });	
			$('.subcat').click(function() {
					$(this).find('.maindropdown').show(200); 
					});
					
				<#--$('.sub_mainnavigation').click(function() {
				if (xyz ==""){
				$('.maindropdown').slideUp(200);
				}
					$(this).find('.maindropdown').show(200); 
				});
				
				$('.sub_mainnavigation').hover(function () {
                   clearTimeout($.data(this, 'timer'));
                   if (xyz ==""){
			       $('.maindropdown', this).stop(true, true).slideDown(200);
			     }
			  }, function () {
			    $.data(this, 'timer', setTimeout($.proxy(function() {
			    if (xyz ==""){
			      $('.maindropdown', this).stop(true, true).slideUp(200);
			      }
			    }, this), 200));
			    
			    $(this).find('.maindropdown').show(200); 
			  });
				
				$('.subcat').hover(function() {
				$(this).find('.sub_navigation').slideToggle(200); 
			});
			
	</script>-->
	<#if (!requestVal.equals("/home"))>
	<div style="background:url(/erptheme1/rightbar.jpg); width:184px; height:137px; margin-top:9px;"><#if autoUserLogin?has_content>
    <a href="javascript:popUpSmall('<@ofbizUrl>inviteafriend?refId=${autoUserLogin.userLoginId}</@ofbizUrl>','tellafriend');" style="color:#ffffff; padding:7px 0 0 13px; float:left; font-family:Arial,Helvetica,sans-serif; font-size:14px;" >Invite A Friend</a>
	</#if>
</div>
</#if>