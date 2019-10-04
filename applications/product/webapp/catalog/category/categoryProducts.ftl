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

<script>
checked = false;
function checkedAll(a){
	if (checked == false){checked = true}else{checked = false}
	for (i = 0; i < a.form.length; i++) {
		a.form[i].checked = checked;
	}
	document.getElementById("generateAll").value = "";
}


function assign() {
    document.getElementById("generateAll").value = "All";
    document.updateCategoryProductForm.submit();
}
function toggle() {
    document.getElementById("generateAll").value = "";
}
</script>


<div class="screenlet">
    <div class="screenlet-title-bar">
            <div class="boxhead-left">
                Generate Bar Codes
            </div>
            <div class="boxhead-fill">&nbsp;</div>
    </div>
    <#if allowBarCode?has_content && allowBarCode == "Y">
	    <div class="screenlet-body">
	           <form method="get" action="<@ofbizUrl>geneartebarcodes</@ofbizUrl>" name="updateCategoryProductForm" target="_blank" onSubmit="toggle()">
	           	  <input type="hidden" name="generateAll" value="" id="generateAll">
	              <input type="hidden" name="productCategoryId" value="${productCategoryId?if_exists}" />
	              <table cellspacing="0" class="basic-table">
	                 <tr class="header-row">
	                 	<td><input onclick="checkedAll(this)" value="Select All" type="button"></td>
	                    <td>${uiLabelMap.ProductId}</td>
	                    <td>${uiLabelMap.ProductName}</td>
	                    <td>From Date</td>
	                    <td>End Date</td>
	                 </tr>
	              <#assign rowClass = "2">
	              <#assign rowCount = 0>
	              <div id="friendslist">
	              <#list productCategoryMembers as productCategoryMember>
	                <#assign suffix = "_o_" + productCategoryMember_index>
	                <#assign product = productCategoryMember.getRelatedOne("Product")>
	                <#assign hasntStarted = false>
	                <#if productCategoryMember.fromDate?exists && nowTimestamp.before(productCategoryMember.getTimestamp("fromDate"))><#assign hasntStarted = true></#if>
	                <#assign hasExpired = false>
	                <#if productCategoryMember.thruDate?exists && nowTimestamp.after(productCategoryMember.getTimestamp("thruDate"))><#assign hasExpired = true></#if>
	                  <tr valign="middle"<#if rowClass == "1"> class="alternate-row"</#if>>
	                  	<td>
		                  	<div id="checkbox">
		                  		<input type="checkBox" id="checkBox${(productCategoryMember.productId)?if_exists}" name="productId" value="${(productCategoryMember.productId)?if_exists}"/>
		                    </div>
		                </td>
			                
		                <td>
		                    <a href="<@ofbizUrl>EditProduct?productId=${(productCategoryMember.productId)?if_exists}</@ofbizUrl>" class="buttontext">
		                    	${(productCategoryMember.productId)?if_exists}
		                    </a>
		                </td>
	                    <td>
	                      <#if product?exists>${(product.internalName)?if_exists}</#if>
	                    </td>
	                    <td>${(productCategoryMember.fromDate)?if_exists}</td>
	                    <td>${(productCategoryMember.thruDate)?if_exists}</td>
	                  </tr>
	                  <#-- toggle the row color -->
	                  <#if rowClass == "2">
	                      <#assign rowClass = "1">
	                  <#else>
	                      <#assign rowClass = "2">
	                  </#if>
	                  <#assign rowCount = rowCount + 1>
	              </#list>
	              <tr valign="middle">
	                      <td colspan="2" align="center">
	                      		<input type="submit" value="Generate" type="button">
	                      </td>
	                      <td colspan="2" align="center">
	                      		<input onclick="assign()" value="Generate All" type="button">
	                      </td>
	                  </tr>
	              </table>
	           </form>
	    </div>
	  <#else>
	  		Allow Bar code is 'N' for store ${storeName?if_exists} [${productStoreId?if_exists}] . Please make it to 'Y' . Then try ..
	  </#if>
</div>
