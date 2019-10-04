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

  
  <#if budgetPlansList?exists && budgetPlansList?has_content>
    <div id="totalOrders" class="screenlet">
      <div class="screenlet-title-bar">
        <ul>
          <li class="h3">Budget Plans Details</li>
        </ul>
        <br class="clear" />
      </div>
      <div class="screenlet-body">
      <#if finalBudgetData?exists && finalBudgetData?has_content>
			<table width="100%" cellspacing="1" cellpadding="5" border="0" class="basic-table hover-bar">
	 		<tr class="header-row">
	 		<td><b>CATEGORY NAME</b></td>
			<td><b>BUDGET PLAN</b></td>
			<td><b>TOTAL USED</b></td>
			<td><b>LEFT</b></td>
			</tr>
				<#if finalBudgetData.size()!= 0 >
				<#assign keys = finalBudgetData.keySet()>
				<#if keys?has_content>
					 <#list keys as key>
					 <tr>
					<#assign pc = delegator.findOne("ProductCategory", {"productCategoryId", key}, true)?if_exists/>
	              <td><#if pc?has_content>${(pc.categoryName)?if_exists}</#if></td>
	              <#assign budData = finalBudgetData.get(key)>
	              <#list budData as data>
	              <td>${data}</td>
	              </#list></tr>
					 </#list>
				</#if>
		<#else>
			<tr>
				<td colspan="13" class="normalLink" align="center" style="font-size:14px;">No record found in this date range</td>
				</tr>
			</#if>
		</table>
				</#if>
      </div>
    </div>
  </#if>