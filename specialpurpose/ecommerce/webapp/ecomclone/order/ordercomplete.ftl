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
<h1 style="margin-top:10px; color:#8f0700; font-size:25px;">Order Details</h2>
<#if !isDemoStore?exists || isDemoStore><p>${uiLabelMap.OrderDemoFrontNote}.</p></#if>
<#if orderHeader?has_content>
  ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderheader")}
  ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderitems")}
  <a href="<@ofbizUrl>home</@ofbizUrl>" class="buttontextblue">${uiLabelMap.EcommerceContinueShopping}</a><br/><br/>
<#else>
  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
</#if>


<#assign groupIdx = 0>
<#assign Ancity ="">
<#assign Anstate ="">
<#assign Ancountry ="">
<#if orderItemShipGroups?has_content>
<#list orderItemShipGroups as shipGroup>
    <#if orderHeader?has_content>
      <#assign shippingAddress = shipGroup.getRelatedOne("PostalAddress")?if_exists>
      <#assign groupNumber = shipGroup.shipGroupSeqId?if_exists>
    <#else>
      <#assign shippingAddress = cart.getShippingAddress(groupIdx)?if_exists>
      <#assign groupNumber = groupIdx + 1>
    </#if>
   	<#assign Ancity= shippingAddress.city?if_exists>
 	<#assign Anstate=shippingAddress.stateProvinceGeoId?if_exists>
 	<#assign Ancountry=shippingAddress.countryGeoId?if_exists>
</#list>
</#if>
<#if orderHeader?has_content && orderHeader.statusId == "ORDER_APPROVED">
	<script type="text/javascript">
	var _gaq = _gaq || [];   
	 	_gaq.push(['_setAccount', 'UA-48446863-1']);  
	  	_gaq.push(['_trackPageview']);  
	   	_gaq.push(['_addTrans',     
		'${orderHeader.orderId?if_exists}',           // order ID - required    
	    'YouMart Store',  // affiliation or store name     
		'${orderGrandTotal?if_exists}',          // total - required    
		'${orderTaxTotal?if_exists}',           // tax     
		'${orderShippingTotal?if_exists}',              // shipping     
		'${Ancity?if_exists}',       // city     
		'${Anstate?if_exists}',     // state or province     
		'${Ancountry?if_exists}'             // country   ]); 
		 ]);
	<#list orderItems as orderItem>
				 <#assign findPftMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("productId", orderItem.productId)>
			   <#assign prod = delegator.findByPrimaryKeyCache("Product", findPftMap)>
			  
		_gaq.push(['_addItem',     
		'${orderHeader.orderId?if_exists}',           // order ID - required    
		'${orderItem.productId?if_exists}',           // SKU/code - required    
		'${prod.productName?if_exists}',        // product name     
		'${prod.brandName?if_exists}',   // category or variation    
		'${orderItem.unitPrice?if_exists}',          // unit price - required     
		'${orderItem.quantity?string.number}'               // quantity - required   ]);  
		]); 
		</#list>
	_gaq.push(['_trackTrans']);
(function() {    
		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;     
		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';     
		var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);   })();  

	</script>


<!-- Google Code for Purchase - Nov&#39;14 Conversion Page -->
<script type="text/javascript">
/* <![CDATA[ */
var google_conversion_id = 969975614;
var google_conversion_language = "en";
var google_conversion_format = "2";
var google_conversion_color = "ffffff";
var google_conversion_label = "s82XCOzKwVcQvs7CzgM";
var google_conversion_value = 1.00;
var google_conversion_currency = "INR";
var google_remarketing_only = false;
/* ]]> */
</script>
<script type="text/javascript" src="//www.googleadservices.com/pagead/conversion.js">
</script>
<noscript>
<div style="display:inline;">
<img height="1" width="1" style="border-style:none;" alt="" src="//www.googleadservices.com/pagead/conversion/969975614/?value=1.00&amp;currency_code=INR&amp;label=s82XCOzKwVcQvs7CzgM&amp;guid=ON&amp;script=0"/>
</div>
</noscript>

<!-- Facebook Conversion Code for Youmart - Sale Tracking -->
<script>(function() {
  var _fbq = window._fbq || (window._fbq = []);
  if (!_fbq.loaded) {
    var fbds = document.createElement('script');
    fbds.async = true;
    fbds.src = '//connect.facebook.net/en_US/fbds.js';
    var s = document.getElementsByTagName('script')[0];
    s.parentNode.insertBefore(fbds, s);
    _fbq.loaded = true;
  }
})();
window._fbq = window._fbq || [];
window._fbq.push(['track', '6023215843586', {'value':'0.00','currency':'INR'}]);
</script>
<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=6023215843586&amp;cd[value]=0.00&amp;cd[currency]=INR&amp;noscript=1" /></noscript>

</#if>


