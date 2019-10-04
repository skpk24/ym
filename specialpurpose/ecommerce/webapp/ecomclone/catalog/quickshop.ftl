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
<#-->script type="text/javascript" src="/images/accordion/ddaccordion.js">

/***********************************************
* Accordion Content script- (c) Dynamic Drive DHTML code library (www.dynamicdrive.com)
* Visit http://www.dynamicDrive.com for hundreds of DHTML scripts
* This notice must stay intact for legal use
***********************************************/

</script>
<script type="text/javascript">
ddaccordion.init({
	headerclass: "submenuheader", //Shared CSS class name of headers group
	contentclass: "submenu", //Shared CSS class name of contents group
	revealtype: "click", //Reveal content when user clicks or onmouseover the header? Valid value: "click", "clickgo", or "mouseover"
	mouseoverdelay: 200, //if revealtype="mouseover", set delay in milliseconds before header expands onMouseover
	collapseprev: true, //Collapse previous content (so only one open at any time)? true/false 
	defaultexpanded: [], //index of content(s) open by default [index1, index2, etc] [] denotes no content
	onemustopen: false, //Specify whether at least one header should be open always (so never all headers closed)
	animatedefault: false, //Should contents open by default be animated into view?
	persiststate: true, //persist state of opened contents within browser session?
	toggleclass: ["", ""], //Two CSS classes to be applied to the header when it's collapsed and expanded, respectively ["class1", "class2"]
	togglehtml: ["suffix", "<img src='/images/accordion/plus.gif' class='statusicon' />", "<img src='/images/accordion/minus.gif' class='statusicon' />"], //Additional HTML added to the header when it's collapsed and expanded, respectively  ["position", "html1", "html2"] (see docs)
	animatespeed: "fast", //speed of animation: integer in milliseconds (ie: 200), or keywords "fast", "normal", or "slow"
	oninit:function(headers, expandedindices){ //custom code to run when headers have initalized
		//do nothing
	},
	onopenclose:function(header, index, state, isuseractivated){ //custom code to run whenever a header is opened or closed
		//do nothing
	}
})
</script-->
<#assign countert=0>
<#assign reqUri = request.getRequestURI()>
<#assign lastIndexVal = reqUri.lastIndexOf("/") />
<#assign requestVal = reqUri.substring(lastIndexVal) />
<#if (requestAttributes.topLevelList)?exists><#assign topLevelList = requestAttributes.topLevelList></#if>
<#if (requestAttributes.curCategoryId)?exists><#assign curCategoryId = requestAttributes.curCategoryId></#if>


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
				<div class="sidedeeptitlequickshop">Quickshop</div>
					<#assign counter = 1/>
					<#assign x = catalogCol.size()?if_exists />
					<div class="menu_list1" >
				    <#list catalogCol as catalogId>
				    	<#--><ul style=><li class="sub_mainnavigation" style="position:relative;">-->
				    	<#--ul style="float:left; width:331px; display:block; height:0 auto; overflow:hidden;">
				    	<li  style="position:relative; border-bottom:none !important;"-->
				    		<#if currentCatalogId?has_content && currentCatalogId == catalogId>${session.setAttribute("CURRENT_CATALOG_ID",catalogId)}</#if>
					        <form name="CATEGORY_${catalogId?if_exists}${counter?if_exists}" method="post" action="<@ofbizUrl>main</@ofbizUrl>">
					      		<input type="hidden" name='CURRENT_CATALOG_ID' value="${catalogId}" />
						        <#assign thisCatalogName = Static["org.ofbiz.product.catalog.CatalogWorker"].getCatalogName(request, catalogId)>
						    	<#--<a href="<@ofbizUrl>main?CURRENT_CATALOG_ID=${catalogId}</@ofbizUrl>"> ${thisCatalogName}</a>-->
						    	<#assign CatUrl = Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", catalogId, "")/>
						    	<#--span style="background:#a8ac3a; width:300px; padding:10px; display:block;"><a href="javascript:void()" style="color:#ffffff;"> ${thisCatalogName?if_exists}</a></span-->
						    	
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
		        							style="display:block"
		        							</#if>
		        							<#if count+1 ==CategoryListSub1.size()>></#if>
		        							<#assign count =count+1>
		        							</#list>
		        							
		        							  <#if catalogId=='OFFICESTATIO'>
											            <div style="margin-top:40px; margin-left:16px; background:#dedfbf; padding:10px; color:#000; width:300px;display:inline-block; font-weight:bold;">Office Stationery</div>
											            </#if>
		        								<#list CategoryListSub1 as CategoryListSub2>   
		        								  
		        								<#--li class="subcat" style="position:relative"-->
		        								<#-->div class="browsecategorylist dropdown"-->
		        								<div class="browsecategorylist" style="background:none !important; float:left; width:325px; <#if countert==2>min-height:360px;<#elseif countert==6>min-height:400px;</#if> overflow:hidden; padding:5px;">
											        <div class="browsecategorytext" style="background:none !important; float:left; width:325px;">									        <div>
											  <#--div class="glossymenu"-->       
											      <form method="get" action="<@ofbizUrl>quickShopProducts</@ofbizUrl>"  id="quickshopform" name="quickshopform">
											            <input type='hidden' name='category_id' id='category_id' value=''/>
											        	
											       		<#--<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", CategoryListSub2.productCategoryId, "")}" class="atul">${CategoryListSub2.categoryName}</a> -->
											        	<#assign subCatList = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatList", CategoryListSub2.productCategoryId, true)>
											            <#if catalogId=='OFFICESTATIO' && subCatList.size()== 0>
											            <div style="width:20px; float:left; margin-left:10px;">	
											        		<input type="checkbox" class="group1" id="categoryIds" value="${CategoryListSub2.productCategoryId}" onFocus=""/>
											        	</div>	
											        		
											        		${CategoryListSub2.categoryName}
											           <#else>
											            <#if  subCatList.size()== 0 >
											            <div style="width:20px; float:left; margin-left:10px;">	
											        		<input type="checkbox" class="group1" id="categoryIds" value="${CategoryListSub2.productCategoryId}" onFocus=""/>
											        	</div>	
											        		
											        	<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-->	
											        		
											        		<#--<a href="#" class="menuitem submenuheader" style="background:#a8ac3a; padding:10px; color:#ffffff; display:block; width:300px;">${CategoryListSub2.categoryName}</a>-->
											        		<span class="menuitem submenuheader" style="background:#dedfbf; padding:10px; color:#000; display:block; width:300px;">${CategoryListSub2.categoryName}</span>
											        	<#else>
											        		<#--<a href="#" class="menuitem submenuheader" style="background:#a8ac3a; padding:10px; color:#ffffff; display:block; width:300px;">${CategoryListSub2.categoryName}</a>-->
											        		<span class="menuitem submenuheader" style="background:#dedfbf; padding:10px; color:#000; display:block; width:300px;">${CategoryListSub2.categoryName}</span>
											        		
											        	<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-->
											        		
											    		</#if>
											    		</#if>
											    		
											    		<#if subCatList?has_content>
											    		<div class="submenu">
											    			<ul class="maindropdown">
											      				<#list subCatList as subCat> 			      															       		
											          				<li class="subcat" style="position:relative; margin:0px !important; border-bottom:none !important;">
											          				<div style="width:20px; float:left;">
											          				 <input type="checkbox" style="display:inline-block;" class="group1" onClick="checkCategory(this.id,'subProLength_${subCat.productCategoryId}_${subCat_index}','${subCat.productCategoryId}');"
											          				 <#if categoryIdList?has_content && categoryIdList.contains('${subCat.productCategoryId}')>
																		  checked = "true"
																		  <#assign flag = true>
																	  </#if>
											          				 id="${subCat.productCategoryId}" value="${subCat.productCategoryId}"  />
											          				</div>
											          				
											          				<script>
											          				function filter(){
											          				alert("asd");
											          				}
											          				</script>
											          				
											          				<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-- >
											          				  <#--a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat.productCategoryId, CategoryListSub2.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}">${subCat.categoryName}</a-->
											          				  
											          				   ${subCat.categoryName}	<#-- category Name-->
											          				  
											          				<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-->
											          				  
											          				  
											          				  
											          				    <#assign subCatLists1 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists1", subCat.productCategoryId, true)>
											          					<#if subCatLists1?has_content>
											    						<ul >
											    						    <#assign newCount123 =  0>
											      							<#list subCatLists1 as subCat1>
											      							<#assign newCount123 = newCount123+ 1>		
											          							 <li> 
											          							<input  type="checkbox" class="group1"    onClick="getLength(this.id);"
											          							<#--onClick="checkCategory(this.id,'subProLength1_${subCat1.productCategoryId}_${subCat_index}','${subCat.productCategoryId}',this.value);"-->
											          							<#if categoryIdList?has_content && categoryIdList.contains('${subCat1.productCategoryId}') >
																					  checked = "true"
																				</#if>
											          							id="${subCat.productCategoryId}_${subCat1_index}" value="${subCat1.productCategoryId}" onFocus=""/>
											          							
											          							<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-->
											          							<#--<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat1.productCategoryId, subCat.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}"> ${subCat1.categoryName}</a>-->
											          							  
											          							 <a> ${subCat1.categoryName}</a> 
											          							<#-- Raja Ghosh Comment this line and remove the hyperlink as per Instruction-->  
											          							   <#assign subCatLists2 = Static["org.ofbiz.product.category.CategoryWorker"].getRelatedCategoriesRet(request, "subCatLists2", subCat1.productCategoryId, true)>
											          					<#if subCatLists2?has_content>
											    						<ul class="maindropdown">
											    						   
											      							<#list subCatLists2 as subCat2>
											      							<#assign newCount123 = newCount123 + 1>		
											          							 <li> 
											          							<input  type="checkbox" class="group1"  
											          							<#if categoryIdList?has_content && categoryIdList.contains('${subCat2.productCategoryId}')>
																					  checked = "true"
																				</#if>
											          							id="${subCat1.productCategoryId}_${subCat2_index}" value="${subCat2.productCategoryId}" onFocus=""/>
											          							<a href="${Static["org.ofbiz.product.category.CatalogUrlServlet"].makeCatalogUrl(request, "", subCat2.productCategoryId, subCat1.productCategoryId)}?CURRENT_CATALOG_ID=${currentCatalogId}"> ${subCat2.categoryName}</a>
											          							
											          							
											          							</li> 
											      						</#list> 
											      							  
											      						</ul>
											      						 <input type="hidden" value="${newCount123}" id="subProLength1_${subCat1.productCategoryId}_${subCat_index}"/>
											      						  <input type="hidden" value="${newCount123}" id="subProLength1_${subCat1.productCategoryId}"/>  
											    						</#if>
											          				</li>
											      				</#list>	
											      					  <input type="hidden" value="${newCount123}" id="subProLength_${subCat.productCategoryId}_${subCat_index}"/> 									      				
											      			</ul>
											      			
											      			</#if>
											      				</li>
											      				
											      				</#list>											      				
											      			</ul>
											      			</div>
											      			</#if>
											      			
											      		
											   
											    		 </form>
											    		 </div>
											        </div>
												  </div>
												<#--/li-->
												
												<#assign countert=countert+1>
												
		        							</#list>
				        				</ul>
				        			
				        		</#if>
				        	</#if>
				        <#-->/li>
				        </ul-->
				       
				    </#list>
				    </div>
				    
				         
				    <div style="position:fixed; bottom:33%; right:0%; z-index:9999;"><a href="javascript:document.getElementById('quickshopform').submit()" class="buttontext" style="width:130px; display:block; text-align:center;" onclick="return AllCategories();">Continue</a></div>
				  </div>
				   
				</#if>
				<script type="text/javascript">
				function getLength(subCateId){
				subCateName = document.getElementById(subCateId).value
				if(document.getElementById(subCateId).checked){
				flag = true;
				}else{
				flag = false;
				}
					try{
					subCatLength  = document.getElementById("subProLength1_"+subCateName).value;
					}
					catch(err){
					subCatLength = 0;
					}
					checkSubCategoryName(subCateName,subCatLength,flag);
				}
				function checkSubCategoryName(subCateName,subCatLength,flag){
			
				 for(var i=0;i<subCatLength;i++){
				 if(document.getElementById(subCateName+"_"+i) != null){
				   if(flag)
				    document.getElementById(subCateName+"_"+i).checked = true;
				    else
				    document.getElementById(subCateName+"_"+i).checked = false;
				 }
			}
}			
				function checkCategory(chkId,lengthId,chkBoxId){
   				var chkLength = document.getElementById(lengthId).value;
  				 for(var i=0;i<=chkLength;i++){
				   if(document.getElementById(chkId).checked == true ) {
					if(document.getElementById(chkBoxId+"_"+i) != null){
					try{
					subCateName = document.getElementById(chkBoxId+"_"+i).value;
					subCatLength  = document.getElementById("subProLength1_"+subCateName).value;
					subCatLength = subCatLength - 1;
					checkSubCategoryName(subCateName,subCatLength,true);
					}catch(err){
					}
				 	   document.getElementById(chkBoxId+"_"+i).checked=true; 
				 	}
				   }else{
				    if(document.getElementById(chkBoxId+"_"+i) != null){
				    	try{
					subCateName = document.getElementById(chkBoxId+"_"+i).value;
					subCatLength  = document.getElementById("subProLength1_"+subCateName).value;
					subCatLength = subCatLength - 1;
					checkSubCategoryName(subCateName,subCatLength,false);
					}catch(err){
					}
				 	   document.getElementById(chkBoxId+"_"+i).checked=false; 
				 	}
				   }	
				 }
				}
				
				
				function checkCategory_old(count){
				var newCount123 = document.getElementById("newCount123").value;
				alert(count);
				alert(newCount123);
				 for(var i=count;i<=newCount123;i++)
				 document.getElementById(i).checked=true;
				}
					function AllCategories(){
					     
						var message = $('input:checkbox:checked.group1').map(function () {
              			return this.value;
           				 }).get();
           				 if(message==""){
           				  alert("Please select Category.");
           				 return false;
           				 }
           				 else{
           				 	  document.getElementById('category_id').value = message;
           				 	//alert("category_id"+document.getElementById('category_id').value);
           				 	
      				   		return true;
                       }
                      // alert("message 2="+message);
                       return message;
					}
				</script>
			<script type="text/javascript">
			var xyz="";
			 
					
				<#--$('.sub_mainnavigation').click(function() {
				if (xyz ==""){
				$('.maindropdown').slideUp(200);
				}
					$(this).find('.maindropdown').show(200); 
				});-->
				
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
				
				
		
			
	</script>
	
	<#if (!requestVal.equals("/home"))>
	<#--
	<div>
	<img src="/erptheme1/rightbar.jpg"/>
</div>
-->

</#if>









