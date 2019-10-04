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

<div class="mainscreenlet">
	<div class='screenlet-title-bar '>
		<div class="h3">&nbsp;${uiLabelMap.Reports}</div>
	</div>
    <div class="screenlet-body">
		<table class="basic-table" cellspacing="1" cellpadding="0">
			<tr style="background-color:#69a9d9">
				<td class="label">Sales Type</td>
				<td>No. of Orders</td>
				<td>No. of Sold Items</td>
				<td>Pending Orders</td>
				<td>Successful Orders</td>
				<td>Rejected Orders</td>
				<td>Total Revenue</td>
			</tr>
			<tr class="alternate-row">
				<td class="label">${uiLabelMap.CommonInternalSales}</td>
				<td>${tweborders}</td>
				<td>${icsize}</td>
				<td>${pweborders}</td>
				<td>${cweborders}</td>
				<td>${rweborders}</td>
				<td>${totwebamount}</td>
			</tr>
			<tr>
				<td class="label">${uiLabelMap.Affiliate}</td>
				<td>${tafforders}</td>
				<td>${aicsize}</td>
				<td>${pafforders}</td>
				<td>${cafforders}</td>
				<td>${rafforders}</td>
				<td>${totaffamount}</td>
			</tr>
			<tr class="alternate-row">
				<td class="label">${uiLabelMap.GoogleBase}</td>
				<td>${tgoogleorders}</td>
				<td>${gicsize}</td>
				<td>${pgoogleorders}</td>
				<td>${cgoogleorders}</td>
				<td>${rgoogleorders}</td>
				<td>${totgoogleamount}</td>
			</tr>
			<tr class="alternate-row">
				<td class="label">${uiLabelMap.CommonPos}</td>
				<td>${tposorders}</td>
				<td>${picsize}</td>
				<td>${pposorders}</td>
				<td>${cposorders}</td>
				<td>${rposorders}</td>
				<td>${totposamount}</td>
			</tr>
			<tr class="alternate-row">
				<td class="label">${uiLabelMap.Ebay}</td>
				<td>${tebayorders}</td>
				<td>${eicsize}</td>
				<td>${pebayorders}</td>
				<td>${cebayorders}</td>
				<td>${rebayorders}</td>
				<td>${totebayamount}</td>
			</tr>
			<tr class="alternate-row">
				<td class="label">${uiLabelMap.Amazon}</td>
				<td>${tamazonorders}</td>
				<td>${amicsize}</td>
				<td>${pamazonorders}</td>
				<td>${camazonorders}</td>
				<td>${ramazonorders}</td>
				<td>${totamazonamount}</td>
			</tr>
		</table>
	</div>
</div>		