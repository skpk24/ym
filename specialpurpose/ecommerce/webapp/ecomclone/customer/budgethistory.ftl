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

<#if party?exists>
<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>

<form style="float:left;" action="<@ofbizUrl>budgethistory</@ofbizUrl>" name="budgethistory">
Month : <select name="month" onchange="javascript:document.budgethistory.submit();">
			<option value="JAN" <#if month?has_content && month == "JAN">selected</#if>>JAN</option>
			<option value="FEB" <#if month?has_content && month == "FEB">selected</#if>>FEB</option>
			<option value="MAR" <#if month?has_content && month == "MAR">selected</#if>>MAR</option>
			<option value="APR" <#if month?has_content && month == "APR">selected</#if>>APR</option>
			<option value="MAY" <#if month?has_content && month == "MAY">selected</#if>>MAY</option>
			<option value="JUN" <#if month?has_content && month == "JUN">selected</#if>>JUN</option>
			<option value="JUL" <#if month?has_content && month == "JUL">selected</#if>>JUL</option>
			<option value="AUG" <#if month?has_content && month == "AUG">selected</#if>>AUG</option>
			<option value="SEP" <#if month?has_content && month == "SEP">selected</#if>>SEP</option>
			<option value="OCT" <#if month?has_content && month == "OCT">selected</#if>>OCT</option>
			<option value="NOV" <#if month?has_content && month == "NOV">selected</#if>>NOV</option>
			<option value="DEC" <#if month?has_content && month == "DEC">selected</#if>>DEC</option>
		</select>
</form>

		<#assign catalogCol = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogIdsAvailable(request)?if_exists>
		<#if requestVal.equals("/product") || requestVal.equals("/main") || requestVal.equals("/category")>
			<#assign currentCatalogId = Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)?if_exists>
			<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
		<#elseif requestVal.equals("/keywordsearch")>
			<#assign currentCatalogId = requestParameters.SEARCH_CATALOG_ID?if_exists>
			<#if currentCatalogId?has_content && !currentCatalogId.equals("")>
				<#assign currentCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, currentCatalogId)?if_exists>
			</#if>
		</#if>
		<#if (catalogCol?size > 0)>
		<div class="catalog" id="searchQuickCategory" style="padding-bottom:20px; margin-bottom:10px; float:left; width:100%;">
		<div class="sidedeeptitlequickshop">Category</div>
			<#assign counter = 1/>
			<#assign x = catalogCol.size()?if_exists />
	<div class="menu_list1" >
		<table width="98%" cellspacing="1" cellpadding="5" border="0" style="margin-left:10px;">
		    <tr>
		   	  <td style="padding-bottom:0px !important; margin-bottom:0px !important;">
		   	  <table width="970px" cellspacing="1" cellpadding="5" border="0" class="dark-grid1" style="margin-left:10px; padding-bottom:0px !important; margin-bottom:0px !important;">
		 		<tr class="header-row">
			 		<td width="54%"><b>CATEGORY</b></td>
					<td width="15%"><b>BUDGET PLAN</b></td>
					<td width="14%"><b>TOTAL USED</b></td>
					<td width="12%"><b>LEFT</b></td>
				</tr>
				</table>
			  </td>
			</tr>
			<tr>
				<td>						
		    <#list catalogCol as catalogId>
		    
		    
		    	<#--><ul style=><li class="sub_mainnavigation" style="position:relative;">-->
		    	<ul style="display:block; height:0 auto; overflow:hidden;"><li  style="position:relative; border-bottom:none !important;">
		    		<#if currentCatalogId?has_content && currentCatalogId == catalogId>${session.setAttribute("CURRENT_CATALOG_ID",catalogId)}</#if>
			      		<input type="hidden" name='CURRENT_CATALOG_ID' value="${catalogId}" />
				        <#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
				    	<#--<a href="<@ofbizUrl>main?CURRENT_CATALOG_ID=${catalogId}</@ofbizUrl>"> ${thisCatalogName}</a>-->
				    	<#assign CatUrl = Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", catalogId, "")/>
				    	<#--span style="background:#a8ac3a; width:300px; padding:10px; display:block;"><a href="javascript:void()" style="color:#ffffff;"> ${thisCatalogName?if_exists}</a></span-->
				                   	
			      
		        	<#if catalogId?exists>
		       			<#assign CategoryList = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogTopCategoryId(request, catalogId)?if_exists>
		   				<#assign CategoryListSub1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(delegator,"topLevelList", CategoryList, false, false, false)?if_exists>
		        		<#if CategoryListSub1?has_content>
		        				<#assign count =0>
        							<#list CategoryListSub1 as CategoryListSub3>
        							<#if count = 0><ul"</#if>
        							<#assign pcat = CategoryListSub3.productCategoryId>
        							<#if productCategoryId?has_content && (productCategoryId ==pcat || (parameters.rootCategoryId?has_content && parameters.rootCategoryId==pcat))>
        							<#assign pcatmain = CategoryListSub3.productCategoryId>
        							style="display:block"
        							</#if>
        							<#if count+1 ==CategoryListSub1.size()>></#if>
        							<#assign count =count+1>
        							</#list>
        							          
        								<#list CategoryListSub1 as CategoryListSub2>
        								<li class="subcat" style="position:relative; border-bottom:none !important; margin:0px !important;"> 
        								<div class="browsecategorylist dropdown">
									        <div style="background-position:0 5px;">
									         <#--div class="browsecategorytext" style="background-position:0 5px;">
									        
									       		<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", CategoryListSub2.productCategoryId, "")}" class="atul">${CategoryListSub2.categoryName}</a> -->
									        	
									        	
									        	<#if finalBudgetData?exists && finalBudgetData?has_content>
											     <table width="100%" cellspacing="1" cellpadding="5" border="0" class="dark-grid1" style="margin-bottom:0px !important;">
										 		
													<#if finalBudgetData.size()!= 0 >
													
													<#assign keys = finalBudgetData.keySet()>
													<#if keys?has_content>
														 <#list keys as key>
													  <#if key.equals(CategoryListSub2.productCategoryId?if_exists)>
													  <tr class="header-row1">
										              <#assign budData = finalBudgetData.get(key)>
										              <td width="54%">${CategoryListSub2.categoryName}<br/> <#if budgetPlansList?exists && budgetPlansList?has_content><#else><div ><input type="text" id="${CategoryListSub2.productCategoryId}" class="inputBox" size="10" name="${CategoryListSub2.productCategoryId}" value=""/><span class="WebRupee">&#8377;</span></div></#if></td>
										              <td width="15%" id="oriPlans${key}">${budData.get(0)}</td><td id="newPlans${key}" style="display:none;"><input type="text" id="${key}" class="inputBox" size="10" name="${key}" value="${budData.get(0)}"/></td>
										              <td width="14%">${budData.get(1)}</td>
										              <td width="12%">${budData.get(2)}</td>
										               </#if>
													 </#list>
													</#if>
											
											<#else>
												<tr>
													<td colspan="13" class="normalLink" align="center" style="font-size:14px;">No record found in this date range</td>
													</tr>
												</#if>
											</table>
													</#if>
													
									        	<#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", CategoryListSub2.productCategoryId, true)>
									    		<#if subCatList?has_content>
									    			<ul class="sub_navigation" <#if requestVal.equals("/home")>style="left:130px;"</#if>>
									      				<#list subCatList as subCat> 
									      				
									          			<#--	<li><a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, CategoryListSub2.productCategoryId)}">${subCat.categoryName}</a></li> -->
									      				</#list>
									      			</ul>
									    		</#if>
									    		
									        </div>
										  </div>
										</li>
										
        							</#list>
        							
        							
		        				</ul>
		        			
		        		</#if>
		        	</#if>
		        </li></ul>
		       
		    </#list>
		    </td>
		    </tr>
		   </table>  
		    </div>
		    <div class="clear:both;"></div>
		         
		  </div>
		   
		</#if>
			<#--script type="text/javascript">
			var xyz="";
			 
				-->	
				<#--$('.sub_mainnavigation').click(function() {
				if (xyz ==""){
				$('.maindropdown').slideUp(200);
				}
					$(this).find('.maindropdown').show(200); 
				});-->
				
			<#--	$('.sub_mainnavigation').hover(function () {
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
				
				
			function editBudgetPlans(categoryKey)
			{
			var param1 = 'oriPlans'+categoryKey;
			var param2 = 'newPlans'+categoryKey;
			var block1 = document.getElementById(param1);
			var block2 = document.getElementById(param2);
			
			block2.style.display = "block";
            block1.style.display = "none";
			}
		
			
	</script>-->
<#else>
    <div class="inner-content">
		<h3>Budget Plan:</h3>
		<div style="clear:both;"></div>
		<p style="margin-top:0px !important;">'Bugdet Plan' and 'YouMart Savings Program' are part of our initiatives to help customers plan their spending and save more.</p>
		<p style="margin-top:0px !important;">Budget Plan will help YouMart customers to control spending by fixing a budget for the month. As you will be able to allocate separate budget targets for each category, you will be warned by YouMart if your spending through YouMart exceeds the budget.</p>
		<p style="margin-top:0px !important;">This feature will help customers plan your grocery and household expenses and you don't get to squeeze on your pockets by end of every month due to overspending.</p>
		<p style="margin-top:0px !important;">It will also help you to reduce your expense on impulsive decisions and unwanted stuff and allocate the money on buying more of healthy and necessary items.</p>
		<p><a href="<@ofbizUrl>${checkLoginUrl}</@ofbizUrl>" class="buttontext" >Login And Know More</a></p>
	
	</div>
</#if>









