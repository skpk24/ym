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
<#if orderHeader?has_content>
  <form name="addCommonToCartForm" action="<@ofbizUrl>addordertoExistingcart</@ofbizUrl>" method="post">
    <input type="hidden" name="add_all" value="true" />
    <input type="hidden" name="orderId" value="${orderHeader.orderId}" />
    ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderheader")}
    <br />
    ${screens.render("component://ecommerce/widget/ecomclone/OrderScreens.xml#orderitems")}
  </form>
  <#if isOrderContainsGiftCard?if_exists  && isOrderContainsGiftCard >
  <div style="height:25px;padding: 0 0 0 20px;"><a href="javascript:addItems('${orderHeader.orderId}',document.addCommonToCartForm)" class="buttontext">${uiLabelMap.OrderAddToCart}</a></div>
  </#if>
<#else>
  <h3>${uiLabelMap.OrderSpecifiedNotFound}.</h3>
</#if>

<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>


<script type="text/javascript">
function addItems(orderId,form) {
         form.submit();
    }
function addItem(orderId) {
      var  param = 'orderId=' + orderId+'&finalizeMode=init';
       pleaseWait('Y');
     	$('#loading-cont').show();
      jQuery.ajax({url: '/control/loadCartFromOrder',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
        	pleaseWait('N');
          $('#minicart').html(data);
          document.getElementById('outputs').innerHTML = "Order Items added successfully";
          ShowDialog1(false);
         },
         complete:  function() { 
         	pleaseWait('N');
	          var minitotal = document.getElementById('abcxyz').innerHTML;
	          var miniquantity = document.getElementById('miniquantityA').value;
	           $('#microCartTotal').text(minitotal);
	           document.getElementById('microCartQuantity').innerHTML=miniquantity;
	           document.getElementById('checkoutdis').style.display="block";
	          document.getElementById('abcxyzhref').href="/control/showcart";
         },
        error: function(data) {
        pleaseWait('N');
        }
    	});
    	 
    	
    
    }
    
    function ShowDialog1(modal)
   {
      

      setTimeout(function() {
       $('#cartsummary1').show();
		 $('#inbulkorder').hide();
        
    }, 4000);
    
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    cartsummary1();
    
   }
    function ShowDialog(modal)
   {
      /*$("#overlay").show();
      $("#dialog").fadeIn(300);

      if (modal)
      {
         $("#overlay").unbind("click");
      }
      else
      {
         $("#overlay").click(function (e)
         {
            HideDialog();
         });
      }
      setTimeout(function() {
        $("#overlay").hide(),
      $("#dialog").fadeOut(300)
    }, 3000);
    */
    var miniquantity = document.getElementById('miniquantityA').value;
    document.getElementById('expandSideCatQuantity').innerHTML=miniquantity;
    document.getElementById('sideCatQuantity').innerHTML=miniquantity;
    cartsummary1();
   }
    
    function pleaseWait(wait){
	
				if (wait == "Y") {
					var CatH =$( document ).height();
					var CatW =$( document ).width();
					document.getElementById('washoutOrder').style.height=CatH+"px";
					document.getElementById('washoutOrder').style.width=CatW+"px";
 					$('#loading-cont').show();
				}else{
						document.getElementById('washoutOrder').style.height="";
					document.getElementById('washoutOrder').style.width="";
					$('#loading-cont').hide();
					 
				}
			}
    </script>
    
    
    <div id ="loading-cont" style="display:none">
<div id="pleaseWait" style=" font-size:30px; padding-top:30px; left:20% !important; padding-left:20px; width:700px; z-index:99999999999;">
<img src="/images/loader.gif"><br/>Please wait your order is adding to the cart</div>
</div>

