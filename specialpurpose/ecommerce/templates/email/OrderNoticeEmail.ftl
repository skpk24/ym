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

 <html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <#assign defaultPartyId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "ORGANIZATION_PARTY")>
<#if defaultPartyId?has_content>
	<#assign logoDetail = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",defaultPartyId))?if_exists/>
	 	<#if logoDetail?has_content>
	 		<#assign logoImageUrl = logoDetail.logoImageUrl/>
	 			
	 	</#if>
</#if>
		<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
			<tr>
				<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
    <title> &nbsp;&nbsp;&nbsp; ${title?if_exists}</title>
    <#-- this needs to be fully qualified to appear in email; the server must also be available -->
    <style type="text/css">
*html{margin:0px; padding:0px;}
body{margin:0px; padding:0px;}

.screenlet ul li{
    list-style-type: none;
}
#orderHeader ul li{list-style-type: none;}
div.screenlet {
    background-color: #FFFFFF;
    height: auto !important;
    height: 1%;
    margin-bottom: 1em;
}
h1 {
    font-size: 1.6em;
    font-weight: bold;
    color:#8f0700 !important;
}
h3 {
    font-size: 1.1em;
    font-weight: bold;
}

/* IE7 fix */
table {
    font-size: 1em;
}
div.screenlet ul {
    margin: 10px;
}
div.screenlet li {
    line-height: 15px;
}
div.screenlet h3 {
    background:#1C334D none repeat scroll 0 0;
    color:#FFFFFF;
    height:auto !important;
    padding:3px 4px 4px;
}
.columnLeft {
    width: 45%;
    float: left;
	margin-right:10px;
}
.columnRight {
    width: 45%;
    float: left;
    clear: none;
}
div.screenlet table {
    width: 100%;
}
ul
{
    list-style-type: none !important;
}
li
{
    list-style-type: none !important;
}
div.screenlet table tfoot th {
    text-align: right;
    font-weight: bold;
}
.clearBoth {
    clear: both;
}
a{
list-style-type:none;}

#order-detail-info{width:100%; font-size:12px;}
#order-detail-info tr td{padding:10px;}

#order-detail-info tfoot th, tfoot td {
    background: none repeat scroll 0 0 #ffffff;
    padding: 4px;
    text-align: right;
}
#ecom-mainarea .screenlet {
    background: none repeat scroll 0 0 #FFFFFF;
    height: auto !important;
    margin-bottom: 10px;
}
#ecom-mainarea .screenlet li {
    font-size: 12px;
}
#order-detail-info thead th{padding:10px; background:#d8d6d6;}
#order-detail-info .productinfo a{color:#6F9FB4; font-weight:bold;}
#order-detail-info tbody 
.special{border-bottom:2px solid #a6a3a3; border-top:2px solid #a6a3a3;}
    </style>
</head>

<body>

<#-- custom logo or text can be inserted here -->
<h1 style="margin-top:10px; color:#8f0700 !important; font-size:25px;">${title!}</h1>
 

 <#if !isDemoStore?exists || isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>
<#if note?exists><p class="tabletext">${note}</p></#if>

 <table width="600px" border="0" cellpadding="0" cellspacing="0">
 <#if orderHeader?exists>
 	<tr>
		<td width="100%">
 			${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderheader1")}
		</td>
	</tr>
	<tr>
		<td width="100%">
			 ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderitems1")}
		</td>
	</tr>
	<#else>
	<tr>
		<td>
			<h1>Order not found with ID [${orderId?if_exists}], or not allowed to view.</h1>
		</td>
	</tr>
	</#if>
</table>
</body>
</html>
