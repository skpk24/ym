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

  <#--if monthsToInclude?exists && totalSubRemainingAmount?exists && totalOrders?exists>
    <div id="totalOrders" class="screenlet">
      <div class="screenlet-title-bar">
        <ul>
          <li class="h3">${uiLabelMap.PartyLoyaltyPoints}</li>
        </ul>
        <br class="clear" />
      </div>
      <div class="screenlet-body">
        ${uiLabelMap.PartyYouHave} ${totalSubRemainingAmount} ${uiLabelMap.PartyPointsFrom} ${totalOrders} ${uiLabelMap.PartyOrderInLast} ${monthsToInclude} ${uiLabelMap.CommonMonths}.
      </div>
    </div>
  </#if-->
  
  <#if (totalloyaltyPoints?exists && totalloyaltyPoints?has_content) || (leftloyaltyPoints?exists && leftloyaltyPoints?has_content)>
    <div id="totalOrders" class="screenlet">
      <div class="screenlet-title-bar">
        <ul>
          <li class="h3">${uiLabelMap.PartyLoyaltyPoints}</li>
        </ul>
        <br class="clear" />
      </div>
      <div class="screenlet-body">
      You have <#if totalloyaltyPoints?exists>${totalloyaltyPoints?if_exists}<#elseif leftloyaltyPoints?exists>${leftloyaltyPoints?if_exists}<#else> 0 </#if> Savings Points.</br>
      <#if leftBalance?exists>You have Rs. ${leftBalance} in your Savings Account.</#if>
      	<#if orderPaymentPreferences?exists>
		      <table border="">
				  <tr>
				    <th>Order Id</th>
				    <th>Savings Amount Used</th>
				  </tr>
				  <#list orderPaymentPreferences as orderPaymentPreference>
				  <tr>
				   <td><a href="/ordermgr/control/orderview?orderId=${orderPaymentPreference.orderId}" class="buttontext">${orderPaymentPreference.orderId}</a></td>
				   <td>${orderPaymentPreference.maxAmount}</td>
				  </tr>
				  </#list>
			</table>
		</#if>
      </div>
    </div>
  </#if>