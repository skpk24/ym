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

<#--div><a href='<@ofbizUrl>FindProdCatalog</@ofbizUrl>' class='buttontext'>${uiLabelMap.ProductCatalogDetailList}</a></div-->
<div id="barCodeDiv">
<table cellspacing="0" class="basic-table">
  <#assign sortList = Static["org.ofbiz.base.util.UtilMisc"].toList("prodCatalogCategoryTypeId", "sequenceNum", "productCategoryId")>
  <#list prodCatalogs as prodCatalog>
  <#if curProdCatalogId?exists && curProdCatalogId == prodCatalog.prodCatalogId>
     <#assign prodCatalogCategories = prodCatalog.getRelatedOrderByCache("ProdCatalogCategory", sortList)>
           <div style="height:auto; overflow:hidden; width:100%; padding-bottom:3px;">
           	<div style="float:left;  padding-top:2px; padding-left:5px; padding-right:4px;">
           	<a href="<@ofbizUrl>geneartebarcodes?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="">
           	
           	<div style="float:left; height:auto; overflow:hidden">
           	<a href="<@ofbizUrl>geneartebarcodes?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class="browsecategorybutton">
           	${prodCatalog.catalogName?if_exists}
           	</a>
           	</div>
           
           </div>
           <div class="dotted_line"></div>
    <#else>
      <div style="height:auto; overflow:hidden; width:100%; padding-bottom:3px;">
     <div style="float:left;  padding-top:2px; padding-left:5px; padding-right:2px;"></div>
     <div style="float:left; height:auto; overflow:hidden"><a href="<@ofbizUrl>geneartebarcodes?prodCatalogId=${prodCatalog.prodCatalogId}</@ofbizUrl>" class='browsecategorybutton'>
     ${prodCatalog.catalogName?if_exists}
     </a>
     </div>
     </div>
       <div class="dotted_line"></div>
   </#if>
  </#list>
  
  <#if catList?exists && catList?has_content>
  ${catList?if_exists}
  </#if>
    </table>
 </div>
  
