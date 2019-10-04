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

<script type="text/javascript">
    <!-- function to add extra info for Timestamp format -->
    function TimestampSubmit(obj) {
       reservStartStr = jQuery(obj).find("input[name='reservStartStr']");
       val1 = reservStartStr.val();
       reservStart = jQuery(obj).find("input[name='reservStart']");
       if (reservStartStr.val().length == 10) {
           reservStart.val(reservStartStr.val() + " 00:00:00.000000000");
       } else {
           reservStart.val(reservStartStr.val());
       }
        jQuery(obj).submit();
      
    }
    
    
    
   function validateUpdateFavouriteList(){
   	var listName = document.getElementById('listName').value;
   	if(listName == '' || listName == null){
   		alert("Please enter favourite list name.");
   		document.getElementById('listName').focus();
   		return false;
   	}
   	else{
   		return true;
   	}
   } 
     
</script>
<script>
				function chgQty0(delta,id,obj) {
				var txtbox = document.getElementById(id);
    			var value = txtbox.value;
					qty = parseFloat(value) + delta;
					if ((delta > 0) && (value == "")) {
						if (delta < 1.0) {
							delta = 1.0;
						}
						qty = delta;
					} else if (isNaN(qty) || (qty < 0)) {
						// document.getElementById(id).value = "0";
						return;
					} else if ((qty > 0) && (qty < 1.0) && (delta < 0)) {
						document.getElementById(id).value = "0";
						return;
					} else if ((qty > 0) && (qty < 1.0) && (delta >= 0)) {
						qty = 1.0;
					} else if (qty >= 99.99) {
						qty = 99.99;
					}
					qty = Math.floor( (qty-1.0)/1.0 )*1.0  + 1.0;
					document.getElementById(id).value = qty;
					TimestampSubmit(obj);
				}
		</script>
		<script>
				function updateFavLists(id,obj) {
				
				var txtbox = document.getElementById(id);
    			var value = txtbox.value;
					qty = parseFloat(value);
					if (isNaN(qty) || (qty < 0)) {
						// document.getElementById(id).value = "0";
						return;
					} else if ((qty > 0) && (qty < 1.0)) {
						document.getElementById(id).value = "0";
						return;
					} else if (qty >= 99.99) {
						qty = 99.99;
					}
					qty = Math.floor( (qty-1.0)/1.0 )*1.0  + 1.0;
					document.getElementById(id).value = qty;
					TimestampSubmit(obj);
					
					/*var quantity = document.getElementById(id).value;
				
					   v1 = jQuery(obj).find("input[name='shoppingListId']");
      				 shoppingListId = v1.val();
      				 v2 = jQuery(obj).find("input[name='shoppingListItemSeqId']");
      				 shoppingListItemSeqId = v2.val();
      				var  param = 'quantity=' + quantity + 
                      '&shoppingListId=' + shoppingListId + 
                      '&shoppingListItemSeqId=' + shoppingListItemSeqId ;
      				  	jQuery.ajax({url: '/control/updateFavouriteListItem',
							         data: param,
							         type: 'post',
							         async: false,
							         success: function(data) {
							         alert("data"+data)
							         	document.getElementById(id).value = quantity;
							         alert(document.getElementById(id).value)
								      },
							        error: function(data) {
							        }
							        });*/
      			}
		</script>
		<script>
			function addItems(add_product_id, formName,qtyFormName) {
				var value= document.getElementById(add_product_id).value;
			            if(value == 'NULL' || value == "")
			            {
			            alert("Please select pack size");
			            
			            }else{
			            formName.quantity.value = qtyFormName.quantity.value;
							addItem(formName);
						}
    		}
    	function addItem(formName) {
		    var add_product_id= formName.add_product_id.value;
		    var quantity= formName.quantity.value;
		    
		    if(quantity > 20)
		    {
		    alert("Please select quantity less than 20"); 
		    }else
		    {
		    
		       if (formName.add_product_id.value == 'NULL') {
		           showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonPleaseSelectAllRequiredOptions}");
		           return;
		       }else {
	           var  param = 'add_product_id=' + add_product_id + 
	                      '&quantity=' + quantity;
	                      jQuery.ajax({url: '/control/additem',
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
          if((data.indexOf("Can't add more than ") !== -1) || (data.indexOf("Due to Limited availibility of this product") !== -1))
          {
             var flag_new = true;
          	 var newFlag = checkForGiftCard(data,formName);
          	 if(!newFlag)return;
          	 
          	 if(data == "Can't add more than giftcard")return;
          	 
          	 alert(data);
	         return;
          }
          $('#minicart').html(data);
          
           jQuery.ajax({url: '/control/findprodwgt',
	         data: param,
	         type: 'post',
	         async: false,
	         success: function(data) {
	         document.getElementById('outputs').innerHTML = data;
	         
	         ShowDialog1(false);
	         },
        error: function(data) {
        }
    	});
    	
         },
         complete:  function() { 
         
         var minitotal = document.getElementById('abcxyz').innerHTML;
          var miniquantity = document.getElementById('miniquantityA').value;
           $('#microCartTotal').text(minitotal);
           document.getElementById('microCartQuantity').innerHTML=miniquantity;
           document.getElementById('checkoutdis').style.display="block";
          document.getElementById('abcxyzhref').href="/control/showcart";
         },
        error: function(data) {
        }
    	});
    	
    	//setTimeout(function() {
      //location.reload();
    //}, 1000);
    }}
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
   
   
   function HideDialog()
   {
      $("#overlay").hide();
      $("#dialog").fadeOut(300);
   }
</script>
<script>
    
function checkInventory(name,prodId,index,shoppingListId,productId,seq){

  document.forms['listreplform_'+seq].elements[name].selectedIndex = (index*1);
 var indexSelected = document.forms['listreplform_'+seq].elements[name].selectedIndex;
 var sku = document.forms['listreplform_'+seq].elements[name].options[indexSelected].value;
 var skuName = document.forms['listreplform_'+seq].elements[name].options[indexSelected].text;
     //alert(skuName);
	var data=document.getElementById("Inventory"+prodId).value;
	//alert(data);
    	if(data!=null && data!="")
    	{
    	if(parseInt(data)>0){
        		
        			document.getElementById("addstock"+productId).style.display="block";
        			document.getElementById("outstock"+productId).style.display="none";
        			
        		}else{
        			document.getElementById("addstock"+productId).style.display="none";
        			document.getElementById("outstock"+productId).style.display="block";
        			
        		}
        }
    
     
    setAddProductNames(prodId,productId);
    }
  function setAddProductNames(sku,formName)
    {
   
	  
         var data=document.getElementById("productname"+sku).value;
      	var proNameDef = document.getElementById('product_id_display'+formName);
         var elem = document.getElementById('variant_product_name'+formName);
         if(data != ''){
          
            	var txt = document.createTextNode(data);
	            if(elem.hasChildNodes()) {
	                elem.replaceChild(txt, elem.firstChild);
	                 proNameDef.style.display = 'none';
	                 document.getElementById('product_id_display1'+formName).style.display = 'none';
	                elem.style.display = 'block';
	            }
	            else {
	                elem.appendChild(txt);
	                proNameDef.style.display = 'none';
	                document.getElementById('product_id_display1'+formName).style.display = 'none';
	                elem.style.display = 'block';
	            }
            }else{
            document.getElementById('product_id_display1'+formName).style.display = 'block';
            proNameDef.style.display = 'none';
            elem.style.display = 'none';
            }
         
   
    }
    
		</script>

</script>


<div class="screenlet" style="margin-top:2px; margin-bottom:0px;">
        <div class="boxlink" style="margin-top:8px;">
            <a href="<@ofbizUrl>createEmptyFavouriteList?productStoreId=${productStoreId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
        </div>
    <h3 style="background:url(/erptheme1/ui-bg_glass_65_ffffff_1x400.png) repeat-x top left; padding: 10px 5px;">&nbsp;Favourite Lists</h3>
    <div class="screenlet-body">
        <#if favouriteLists?has_content>
        
          <form name="selectShoppingList" method="post" action="<@ofbizUrl>editFavouritesList</@ofbizUrl>">
            <#--select name="shoppingListId" class="selectBox">
              <#if shoppingList?has_content>
                <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
                <option value="${shoppingList.shoppingListId}">--</option>
              </#if>
              <#list favouriteLists as list>
                <option value="${list.shoppingListId}">${list.listName}</option>
              </#list>
            </select-->
            
	            <ul class="favouritelist">
		            <#list favouriteLists as list>
			            <li>
				            <a href="<@ofbizUrl>editFavouritesList?productStoreId=${productStoreId}&shoppingListId=${list.shoppingListId}</@ofbizUrl>" class="linktext1233">${list.listName}</a>
				         </li>
				         <li style="position:relative; top:0px; left:-16px;">   
				            <a href="<@ofbizUrl>deleteFavouritesList?productStoreId=${productStoreId}&shoppingListId=${list.shoppingListId}</@ofbizUrl>" class="linktext1233"><img src="/erptheme1/cross-grey.png"></a>
			            </li>
		            </#list>
	            </ul>
            	&nbsp;&nbsp;
            <#--a href="javascript:document.selectShoppingList.submit();" class="buttontext">${uiLabelMap.CommonEdit}</a-->
          </form>
        <#else>
          <div class="tabletext">No favourite lists , create a new one.</div>
          <#--a href="<@ofbizUrl>createEmptyFavouriteList?productStoreId=${productStoreId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a-->
        </#if>
    </div>
</div>

<#if shoppingList?has_content>
    <#if canView>

<div class="screenlet">
    <div class="screenlet-title-bar" style="padding:0px;">
        <#--div class="boxlink">
          <form name= "createCustRequestFromShoppingList" method= "post" action= "<@ofbizUrl>createCustRequestFromShoppingList</@ofbizUrl>">
            <input type= "hidden" name= "shoppingListId" value= "${shoppingList.shoppingListId}"/>
            <a href='javascript:document.createCustRequestFromShoppingList.submit()'><div class='submenutext'>${uiLabelMap.OrderCreateCustRequestFromShoppingList}</div></a>
          </form>
          <form name="createQuoteFromShoppingList" method="post" action="<@ofbizUrl>createQuoteFromShoppingList</@ofbizUrl>">
            <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
            <a href='javascript:document.createQuoteFromShoppingList.submit()'><div class='submenutext'>${uiLabelMap.OrderCreateQuoteFromShoppingList}</div></a>
          </form>
          <a href="javascript:document.updateList.submit();" class="submenutextright">${uiLabelMap.CommonSave}</a>
        </div-->
        <h3 style="background:url(/erptheme1/ui-bg_glass_65_ffffff_1x400.png) repeat-x top left; padding: 10px 5px;">Favourite List Detail - ${shoppingList.listName}</h3>
    </div>
    <div class="screenlet-body">
        <form name="updateList" method="post" action="<@ofbizUrl>updateFavouriteList</@ofbizUrl>">
        
            <input type="hidden" class="inputBox" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
            <input type="hidden" class="inputBox" name="partyId" value="${shoppingList.partyId?if_exists}"/>
            <input type="hidden" name="isPublic" value="N"/>
            <input type="hidden" name="isActive" value="Y"/>
            <input type="hidden" name="shoppingListTypeId" value="SLT_FAV_LIST"/>
            <table border="0" width="100%" cellspacing="0" cellpadding="0">
              <tr>
                <td><div class="tableheadtext">Favourite List Name</div></td>
                <td><input type="text" class="inputBox" size="40" name="listName" id="listName" value="${shoppingList.listName}" />*
              </tr>
              <tr>
                <td><div class="tableheadtext">Favourite List Description</div></td>
                <td><input type="text" class="inputBox" size="40" name="description" value="${shoppingList.description?if_exists}" />
              </tr>
              <#--tr>
                <td><div class="tableheadtext">${uiLabelMap.OrderListType}</div></td>
                <td>
                  <select name="shoppingListTypeId" class="selectBox">
                      <#if shoppingListType?exists>
                      <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
                      <option value="${shoppingListType.shoppingListTypeId}">--</option>
                    </#if>
                    <#list favouriteListsTypes as shoppingListType>
                      <option value="${shoppingListType.shoppingListTypeId}">${shoppingListType.get("description",locale)?default(shoppingListType.shoppingListTypeId)}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.EcommercePublic}?</div></td>
                <td>
                  <select name="isPublic" class="selectBox">
                    <#if (((shoppingList.isPublic)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
                    <#if (((shoppingList.isPublic)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
                    <option></option>
                    <option value="Y">${uiLabelMap.CommonY}</option>
                    <option value="N">${uiLabelMap.CommonN}</option>
                  </select>
                </td>
              </tr>
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.EcommerceActive}?</div></td>
                <td>
                  <select name="isActive" class="selectBox">
                    <#if (((shoppingList.isActive)!"") == "Y")><option value="Y">${uiLabelMap.CommonY}</option></#if>
                    <#if (((shoppingList.isActive)!"") == "N")><option value="N">${uiLabelMap.CommonN}</option></#if>
                    <option></option>
                    <option value="Y">${uiLabelMap.CommonY}</option>
                    <option value="N">${uiLabelMap.CommonN}</option>
                  </select>
                </td>
              </tr>
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.EcommerceParentList}</div></td>
                <td>
                  <select name="parentShoppingListId" class="selectBox">
                      <#if parentShoppingList?exists>
                      <option value="${parentShoppingList.shoppingListId}">${parentShoppingList.listName?default(parentShoppingList.shoppingListId)}</option>
                    </#if>
                    <option value="">${uiLabelMap.EcommerceNoParent}</option>
                    <#list allFavouriteLists as newParShoppingList>
                      <option value="${newParShoppingList.shoppingListId}">${newParShoppingList.listName?default(newParShoppingList.shoppingListId)}</option>
                    </#list>
                  </select>
                  <#if parentShoppingList?exists>
                    <a href="<@ofbizUrl>editShoppingList?shoppingListId=${parentShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonGotoParent} (${parentShoppingList.listName?default(parentShoppingList.shoppingListId)})</a>
                  </#if>
                </td>
              </tr-->
              <tr>
                <td><div class="tableheadtext">&nbsp;</div></td>
                <td style="padding-left:3px;">
                  <a href="javascript:document.updateList.submit();" onclick="return validateUpdateFavouriteList();" class="buttontext" >${uiLabelMap.CommonSave}</a>
                </td>
              </tr>
            </table>
        </form>
    </div>
</div>

<#if shoppingListType?exists && shoppingListType.shoppingListTypeId == "SLT_AUTO_REODR">
  <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].monthBegin()>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="boxlink">
            <a href="javascript:document.reorderinfo.submit();" class="submenutextright">${uiLabelMap.CommonSave}</a>
        </div>
        <div class="h3">
            &nbsp;${uiLabelMap.EcommerceShoppingListReorder} - ${shoppingList.listName}
            <#if shoppingList.isActive?default("N") == "N">
                <font color="yellow">${uiLabelMap.EcommerceOrderNotActive}</font>
            </#if>
        </div>
    </div>
    <div class="screenlet-body">
        <form name="reorderinfo" method="post" action="<@ofbizUrl>updateShoppingList</@ofbizUrl>">
            <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
            <table width="100%" cellspacing="0" cellpadding="1" border="0">
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.EcommerceRecurrence}</div></td>
                <td>
                  <#if recurrenceInfo?has_content>
                    <#assign recurrenceRule = recurrenceInfo.getRelatedOne("RecurrenceRule")?if_exists>
                  </#if>
                  <select name="intervalNumber" class="selectBox">
                    <option value="">${uiLabelMap.EcommerceSelectInterval}</option>
                    <option value="1" <#if (recurrenceRule.intervalNumber)?default(0) == 1>selected="selected"</#if>>${uiLabelMap.EcommerceEveryDay}</option>
                    <option value="2" <#if (recurrenceRule.intervalNumber)?default(0) == 2>selected="selected"</#if>>${uiLabelMap.EcommerceEveryOther}</option>
                    <option value="3" <#if (recurrenceRule.intervalNumber)?default(0) == 3>selected="selected"</#if>>${uiLabelMap.EcommerceEvery3rd}</option>
                    <option value="6" <#if (recurrenceRule.intervalNumber)?default(0) == 6>selected="selected"</#if>>${uiLabelMap.EcommerceEvery6th}</option>
                    <option value="9" <#if (recurrenceRule.intervalNumber)?default(0) == 9>selected="selected"</#if>>${uiLabelMap.EcommerceEvery9th}</option>
                  </select>
                  &nbsp;
                  <select name="frequency" class="selectBox">
                    <option value="">${uiLabelMap.EcommerceSelectFrequency}</option>
                    <option value="4" <#if (recurrenceRule.frequency)?default("") == "DAILY">selected="selected"</#if>>${uiLabelMap.CommonDay}</option>
                    <option value="5" <#if (recurrenceRule.frequency)?default("") == "WEEKLY">selected="selected"</#if>>${uiLabelMap.CommonWeek}</option>
                    <option value="6" <#if (recurrenceRule.frequency)?default("") == "MONTHLY">selected="selected"</#if>>${uiLabelMap.CommonMonth}</option>
                    <option value="7" <#if (recurrenceRule.frequency)?default("") == "YEARLY">selected="selected"</#if>>${uiLabelMap.CommonYear}</option>
                  </select>
                </td>
                <td>&nbsp;</td>
                <td><div class="tableheadtext">${uiLabelMap.CommonStartDate}</div></td>
                <td>
                  <@htmlTemplate.renderDateTimeField name="startDateTime" className="" event="" action="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${(recurrenceInfo.startDateTime)?if_exists}" size="25" maxlength="30" id="startDateTime1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td>&nbsp;</td>
                <td><div class="tableheadtext">${uiLabelMap.CommonEndDate}</div></td>
                <td>
                  <@htmlTemplate.renderDateTimeField name="endDateTime" className="textBox" event="" action="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" value="${(recurrenceRule.untilDateTime)?if_exists}" size="25" maxlength="30" id="endDateTime1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                </td>
                <td>&nbsp;</td>
              </tr>
              <tr><td colspan="9"><hr /></td></tr>
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.OrderShipTo}</div></td>
                <td>
                  <select name="contactMechId" class="selectBox" onchange="javascript:document.reorderinfo.submit()">
                    <option value="">${uiLabelMap.OrderSelectAShippingAddress}</option>
                    <#if shippingContactMechList?has_content>
                      <#list shippingContactMechList as shippingContactMech>
                        <#assign shippingAddress = shippingContactMech.getRelatedOne("PostalAddress")>
                        <option value="${shippingContactMech.contactMechId}"<#if (shoppingList.contactMechId)?default("") == shippingAddress.contactMechId> selected="selected"</#if>>${shippingAddress.address1}</option>
                      </#list>
                    <#else>
                      <option value="">${uiLabelMap.OrderNoAddressesAvailable}</option>
                    </#if>
                  </select>
                </td>
                <td>&nbsp;</td>
                <td><div class="tableheadtext">${uiLabelMap.OrderShipVia}</div></td>
                <td>
                  <select name="shippingMethodString" class="selectBox">
                    <option value="">${uiLabelMap.OrderSelectShippingMethod}</option>
                    <#if carrierShipMethods?has_content>
                      <#list carrierShipMethods as shipMeth>
                        <#assign shippingEst = shippingEstWpr.getShippingEstimate(shipMeth)?default(-1)>
                        <#assign shippingMethod = shipMeth.shipmentMethodTypeId + "@" + shipMeth.partyId>
                        <option value="${shippingMethod}"<#if shippingMethod == chosenShippingMethod> selected="selected"</#if>>
                          <#if shipMeth.partyId != "_NA_">
                            ${shipMeth.partyId?if_exists}&nbsp;
                          </#if>
                          ${shipMeth.description?if_exists}
                          <#if shippingEst?has_content>
                            &nbsp;-&nbsp;
                            <#if (shippingEst > -1)>
                              <@ofbizCurrency amount=shippingEst isoCode=listCart.getCurrency()/>
                            <#else>
                              ${uiLabelMap.OrderCalculatedOffline}
                            </#if>
                          </#if>
                        </option>
                      </#list>
                    <#else>
                      <option value="">${uiLabelMap.OrderSelectAddressFirst}</option>
                    </#if>
                  </select>
                </td>
                <td>&nbsp;</td>
                <td><div class="tableheadtext">${uiLabelMap.OrderPayBy}</div></td>
                <td>
                  <select name="paymentMethodId" class="selectBox">
                    <option value="">${uiLabelMap.OrderSelectPaymentMethod}</option>
                    <#list paymentMethodList as paymentMethod>
                      <#if paymentMethod.paymentMethodTypeId == "CREDIT_CARD">
                        <#assign creditCard = paymentMethod.getRelatedOne("CreditCard")>
                        <option value="${paymentMethod.paymentMethodId}" <#if (shoppingList.paymentMethodId)?default("") == paymentMethod.paymentMethodId>selected="selected"</#if>>CC:&nbsp;${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}</option>
                      <#elseif paymentMethod.paymentMethodTypeId == "EFT_ACCOUNT">
                        <#assign eftAccount = paymentMethod.getRelatedOne("EftAccount")>
                        <option value="${paymentMethod.paymentMethodId}">EFT:&nbsp;${eftAccount.bankName?if_exists}: ${eftAccount.accountNumber?if_exists}</option>
                      </#if>
                    </#list>
                  </select>
                </td>
                <td>&nbsp;</td>
              </tr>
              <tr><td colspan="9"><hr /></td></tr>
              <tr>
                <td align="right" colspan="9">
	                  <div class="tabletext">
	                    <a href="javascript:document.reorderinfo.submit();" class="buttontext">${uiLabelMap.CommonSave}</a>
	                    <a href="<@ofbizUrl>editcontactmech?preContactMechTypeId=POSTAL_ADDRESS&amp;contactMechPurposeTypeId=SHIPPING_LOCATION&amp;DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.PartyAddNewAddress}</a>
	                    <a href="<@ofbizUrl>editcreditcard?DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.EcommerceNewCreditCard}</a>
	                    <a href="<@ofbizUrl>editeftaccount?DONE_PAGE=editShoppingList</@ofbizUrl>" class="buttontext">${uiLabelMap.EcommerceNewEFTAccount}</a>
	                  </div>
                </td>
              </tr>
              <#if shoppingList.isActive?default("N") == "Y">
                <tr><td colspan="9"><hr /></td></tr>
                <tr>
                  <td colspan="9">
                    <#assign nextTime = recInfo.next(lastSlOrderTime)?if_exists>
                    <#if nextTime?has_content>
                      <#assign nextTimeStamp = Static["org.ofbiz.base.util.UtilDateTime"].getTimestamp(nextTime)?if_exists>
                      <#if nextTimeStamp?has_content>
                        <#assign nextTimeString = Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(nextTimeStamp)?if_exists>
                      </#if>
                    </#if>
                    <#if lastSlOrderDate?has_content>
                      <#assign lastOrderedString = Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(lastSlOrderDate)?if_exists>
                    </#if>
                    <div class="tabletext">
                      <table cellspacing="2" cellpadding="2" border="0">
                        <tr>
                          <td><div class="tableheadtext">${uiLabelMap.OrderLastOrderedDate}</div></td>
                          <td><div class="tableheadtext">:</div></td>
                          <td><div class="tabletext">${lastOrderedString?default("${uiLabelMap.OrderNotYetOrdered}")}</div></td>
                        </tr>
                        <tr>
                          <td><div class="tableheadtext">${uiLabelMap.EcommerceEstimateNextOrderDate}</div></td>
                          <td><div class="tableheadtext">:</div></td>
                          <td><div class="tabletext">${nextTimeString?default("${uiLabelMap.EcommerceNotYetKnown}")}</div></td>
                        </tr>
                      </table>
                    </div>
                  </tr>
                </tr>
              </#if>
            </table>
        </form>
    </div>
</div>
</#if>

<#if childShoppingListDatas?has_content>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="boxlink">
            <a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}&amp;includeChild=yes</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceAddChildListsToCart}</a>
        </div>
        <div class="h3">&nbsp;${uiLabelMap.EcommerceChildShoppingList} - ${shoppingList.listName}</div>
    </div>
    <div class="screenlet-body">
        <table width="100%" cellspacing="0" cellpadding="1" border="0">
          <tr>
            <td><div class="tabletext" ><b>${uiLabelMap.EcommerceListName}</b></div></td>
            <td align="right"><div class="tabletext"><b>${uiLabelMap.EcommerceTotalPrice}</b></div></td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>
          <#list childShoppingListDatas as childShoppingListData>
              <#assign childShoppingList = childShoppingListData.childShoppingList/>
              <#assign totalPrice = childShoppingListData.totalPrice/>
              <tr>
                <td nowrap="nowrap">
                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a>
                </td>
                <td nowrap="nowrap" align="right">
                  <div class="tabletext"><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></div>
                </td>
                <td align="right">
                  <a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.EcommerceGoToList}</a>
                  <a href="<@ofbizUrl>addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>" class="buttontext">${uiLabelMap.EcommerceAddListToCart}</a>
                </td>
              </tr>
            </form>
          </#list>
          <tr><td colspan="6"><hr /></td></tr>
          <tr>
            <td><div class="tabletext">&nbsp;</div></td>
            <td nowrap="nowrap" align="right">
              <div class="tableheadtext"><@ofbizCurrency amount=shoppingListChildTotal isoCode=currencyUomId/></div>
            </td>
            <td><div class="tabletext">&nbsp;</div></td>
          </tr>
        </table>
    </div>
</div>
</#if>

<div class="screenlet">
    <div class="screenlet-title-bar" style="padding:0px;">
        <!-- hide by radha <div class="boxlink" style="margin-top:8px;">
            <a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceAddListToCart}</a>
        </div>-->
        <h3 style="background:url(/erptheme1/ui-bg_glass_65_ffffff_1x400.png) repeat-x top left; padding: 10px 5px;">${uiLabelMap.EcommerceListItems} - ${shoppingList.listName}</h3>
    </div>
    <div class="screenlet-body">
        <#if shoppingListItemDatas?has_content>
            <table width="100%" cellspacing="0" cellpadding="1" border="0">
              <tr>
                <td><div class="tabletext"><b>${uiLabelMap.OrderProduct} </b></div></td>
                <#-- <td nowrap="nowrap" align="center"><div class="tabletext"><b>Purchased</b></div></td> -->
                 <td align="left"><div class="tabletext"><b>Brand</b></div></td>
                <td align="right"><div class="tabletext"><b>${uiLabelMap.CommonQuantity}</b></div></td>
                <#--td align="right"><div class="tabletext"><b>${uiLabelMap.EcommercePrice}</b></div></td>
                <td align="right"><div class="tabletext"><b>${uiLabelMap.OrderTotal}</b></div></td-->
                <td>&nbsp;</td>
              </tr>

              <#list shoppingListItemDatas as shoppingListItemData>
                <#assign shoppingListItem = shoppingListItemData.shoppingListItem/>
                <#assign product = shoppingListItemData.product/>
                <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)/>
                <#assign unitPrice = shoppingListItemData.unitPrice/>
                <#assign totalPrice = shoppingListItemData.totalPrice/>
                <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists/>
                <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")/>
                  <tr>
                    <td>
		                      <div class="tabletext">
		                      <#assign categoryId = Static["org.ofbiz.product.category.CategoryWorker"].getCategoryFromProduct(delegator,shoppingListItem.productId)?if_exists/>
		                      <#assign crumbs = Static["org.ofbiz.product.category.CategoryWorker"].getTrailAsString1(delegator,shoppingListItem.productId)/>
    						  <#assign productUrl><@ofbizCatalogUrl productId=shoppingListItem.productId currentCategoryId=categoryId  previousCategoryId=crumbs/></#assign>
		                         <a href="${productUrl?if_exists}" class="linktext1233">
		                         <#--${productContentWrapper.get("BRAND_NAME")?if_exists}-->
		                         <span  id="variant_product_name${shoppingListItem.productId}"></span>
		                         <span  id="product_id_display${shoppingListItem.productId}">
		                         ${productContentWrapper.get("PRODUCT_NAME")?default("No Name")}</span>
		                         </a> <#-->: ${productContentWrapper.get("DESCRIPTION")?if_exists}-->
		                      </div>
                    </td>
                    <td> ${productContentWrapper.get("BRAND_NAME")?if_exists}</td>
                    <td nowrap="nowrap" align="center" style="vertical-align:middle !important;">
                      <form method="get" action="<@ofbizUrl>updateFavouriteListItem</@ofbizUrl>" name="listform_${shoppingListItem.shoppingListItemSeqId}" style="margin: 0;" onsubmit="return checkQty('${shoppingListItem.shoppingListId?if_exists}')">
                        <input type="hidden" id="shoppingListId" name="shoppingListId" value="${shoppingListItem.shoppingListId}"/>
                        <input type="hidden" id="shoppingListItemSeqId" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}"/>
                        <input type="hidden" name="reservStart"/>
                         <input type="hidden" name="add_product_id" id="add_product_id" value="${shoppingListItem.productId?if_exists}"/>
                        
                        <div class="tabletext">
                         <table style="padding-top:15px;">
                          <tr>
                          	<td class="tabletext">
                          	    <input type="hidden" name="reservStartStr" value=""/>
		                        <a href="javascript:chgQty0(-1.0,'listforms_${shoppingListItem.shoppingListItemSeqId}',listform_${shoppingListItem.shoppingListItemSeqId});" class="quantity_minus"><span>Increase quantity</span><img src="/erptheme1/Minus.png" alt=""/></a>
		                	</td>
		                	<td>	
		                		<input size="6" class="inputBox" type="text" id="listforms_${shoppingListItem.shoppingListItemSeqId}" name="quantity" onkeypress="return isNumberKeyInviteFriend(event);"  onkeyup="updateFavLists('listforms_${shoppingListItem.shoppingListItemSeqId}',listform_${shoppingListItem.shoppingListItemSeqId})" value="${shoppingListItem.quantity?string.number}"/>
		                	</td>
		                	<td style="vertical-align:top !important; padding-top:5px;">	
		                		<a href="javascript:chgQty0(1.0,'listforms_${shoppingListItem.shoppingListItemSeqId}',listform_${shoppingListItem.shoppingListItemSeqId});" class="quantity_plus"><span>Decrease quantity</span><img src="/erptheme1/Add.png" alt=""/></a>
                        	</td>
                           </tr>
                         </table>
                        </div>
                      </form>
                    </td>
                    <#--
                    <td nowrap="nowrap" align="center">
                      <div class="tabletext">${shoppingListItem.quantityPurchased?default(0)?string.number}</div>
                    </td>
                    -->
                    <#--td nowrap="nowrap" align="right">
                      <div class="tabletext"><@ofbizCurrency amount=unitPrice isoCode=currencyUomId/></div>
                    </td>
                    <td nowrap="nowrap" align="right">
                      <div class="tabletext"><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></div>
                    </td-->
                    <td align="right">
                         <table style="margin:0px !important;">
		                    <tr>
		                    <td>
		                        <#--a href="#" onclick="javascript:TimestampSubmit(listform_${shoppingListItem.shoppingListItemSeqId});" class="buttontext">${uiLabelMap.CommonUpdate}</a-->
		                       <#if isVirtual && productVariantAssocs?has_content>
		                        <#assign replaceItemAction = "/replaceFavouriteListItem/" + requestAttributes._CURRENT_VIEW_?if_exists>
		                        <#assign addToCartAction = "/additem/" + requestAttributes._CURRENT_VIEW_?if_exists>
		                        <form method="get" action="<@ofbizUrl>${addToCartAction}</@ofbizUrl>" name="listreplform_${shoppingListItem.shoppingListItemSeqId}" style="margin: 0;" onsubmit="return checkQty('${shoppingListItem.shoppingListId?if_exists}')">
		                          <input type="hidden" name="shoppingListId" value="${shoppingListItem.shoppingListId}"/>
		                          <input type="hidden" name="shoppingListItemSeqId" value="${shoppingListItem.shoppingListItemSeqId}"/>
		                          <input type="hidden" name="quantity" value="${shoppingListItem.quantity}"/>
		                      </td>
		                      <td>
		                          <select name="add_product_id" class="selectBox" id="${shoppingListItem.shoppingListId?if_exists}add_product_id" onchange="checkInventory(this.name,this.value, (this.selectedIndex),'${shoppingListItem.shoppingListId?if_exists}','${shoppingListItem.productId?if_exists}','${shoppingListItem.shoppingListItemSeqId?if_exists}')">
		                              <#list productVariantAssocs as productVariantAssoc>
		                                <#assign variantProduct = productVariantAssoc.getRelatedOneCache("AssocProduct")>
		                                <#if variantProduct?exists>
		                                
		                                <#assign variantProductContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(variantProduct, request)>
		                                
		                                <#assign productName=Static["org.ofbiz.product.category.CategoryWorker"].getProductName(variantProductContentWrapper.get("PRODUCT_NAME"), request)?if_exists>
		                                  <option value="${variantProduct.productId}">${productName?default("No Name")}</option>
		                                
		                                </#if>
		                              </#list>
		                          </select>
		                      </td>
		                       <#if isVirtual && productVariantAssocs?has_content>
		                        <#list productVariantAssocs as productVariantAssoc>
		                                <#assign variantProduct = productVariantAssoc.getRelatedOneCache("AssocProduct")>
		                                <#if variantProduct?exists>
		                                <input type="hidden" name="${variantProduct.productId}" id="Inventory${variantProduct.productId}" value="${variantProduct.inventoryAtp}">
		                                <input type="hidden" name="${variantProduct.productId}" id="productname${variantProduct.productId}" value="${variantProduct.productName}">
		                               </#if>
		                               </#list> 
		                               </#if>
		                      </tr>
		                    </table>
		                 <table style="margin:0px !important; padding-left:8px;">
		                      <tr>
		                      <td>
		                          <#--a href="javascript:document.listreplform_${shoppingListItem.shoppingListItemSeqId}.action='<@ofbizUrl>${replaceItemAction}</@ofbizUrl>';document.listreplform_${shoppingListItem.shoppingListItemSeqId}.submit();" class="buttontext">${uiLabelMap.EcommerceReplaceWithVariation}</a-->
		                          <a href="<@ofbizUrl>removeFromFavouriteList?shoppingListId=${shoppingListItem.shoppingListId}&amp;shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRemove}</a>
		                       </td>
		                       <td>   

								<div id="addstock${shoppingListItem.productId?if_exists}">
									<a href="javascript:addItems('${shoppingListItem.shoppingListId?if_exists}add_product_id', listreplform_${shoppingListItem.shoppingListItemSeqId},listform_${shoppingListItem.shoppingListItemSeqId})" id="add${shoppingListItem.shoppingListItemSeqId}" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
								</div>
								<div id="outstock${shoppingListItem.productId?if_exists}" style="display:none;">	
									<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
								</div>	                       		

		                        </form>
		                       </td>
		                      <#else>
		                      <span style="margin-left:8px;">
		                      	<a href="<@ofbizUrl>removeFromFavouriteList?shoppingListId=${shoppingListItem.shoppingListId}&amp;shoppingListItemSeqId=${shoppingListItem.shoppingListItemSeqId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonRemove}</a>
		                        
		                        <#assign isStoreInventoryAvailable = Static["org.ofbiz.product.store.ProductStoreWorker"].isStoreInventoryAvailable(request, shoppingListItem.productId)>
	
		                      <#if isStoreInventoryAvailable>
			             	 	<a href="javascript:addItem(listform_${shoppingListItem.shoppingListItemSeqId})" class="buttontext">${uiLabelMap.OrderAddToCart}</a>&nbsp;
			             	 <#else>
			             	 	<img src="/erptheme1/out-of-stock.png" alt="" title=""/>
			             	 </#if>
		                      
		                      </span>
		                      </#if>
		                      
		                      </tr>
                      </table>
                    </td>
                  </tr>
              </#list>
              						<#assign msg=request.getAttribute("message123")?if_exists>
       								<#if msg?has_content>
       									<input type="hidden" id="addToCartMessage" value="${msg?if_exists}"></input>
       								</#if>
              <#--tr><td colspan="6"><hr /></td></tr>
              <tr>
                <td><div class="tabletext">&nbsp;</div></td>
                <td><div class="tabletext">&nbsp;</div></td>
                <#--<td><div class="tabletext">&nbsp;</div></td>-->
                <#--td><div class="tabletext">&nbsp;</div></td>
                <td nowrap="nowrap" align="right">
                  <div class="tableheadtext"><@ofbizCurrency amount=shoppingListItemTotal isoCode=currencyUomId/></div>
                </td>
                <td><div class="tabletext">&nbsp;</div></td>
              </tr-->
            </table>
        <#else>
            <h2>Favourite List Empty.</h2>
        </#if>
    </div>
</div>

<#--div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="h3">Favourite List Price Totals - ${shoppingList.listName}</div>
    </div>
    <div class="screenlet-body">
      <table width="100%" border="0" cellspacing="1" cellpadding="1">
        <tr>
          <td width="5%" nowrap="nowrap">
              <div class="tabletext">${uiLabelMap.EcommerceChildListTotalPrice}</div>
          </td>
          <td align="right" width="5%" nowrap="nowrap">
              <div class="tabletext"><@ofbizCurrency amount=shoppingListChildTotal isoCode=currencyUomId/></div>
          </td>
          <td width="90%"><div class="tabletext">&nbsp;</div></td>
        </tr>
        <tr>
          <td nowrap="nowrap">
              <div class="tabletext">${uiLabelMap.EcommerceListItemsTotalPrice}&nbsp;</div>
          </td>
          <td align="right" nowrap="nowrap">
              <div class="tabletext"><@ofbizCurrency amount=shoppingListItemTotal isoCode=currencyUomId/></div>
          </td>
          <td><div class="tabletext">&nbsp;</div></td>
        </tr>
        <tr>
          <td nowrap="nowrap">
              <div class="tableheadtext">${uiLabelMap.OrderGrandTotal}</div>
          </td>
          <td align="right" nowrap="nowrap">
              <div class="tableheadtext"><@ofbizCurrency amount=shoppingListTotalPrice isoCode=currencyUomId/></div>
          </td>
          <td><div class="tabletext">&nbsp;</div></td>
        </tr>
      </table>
    </div>
</div-->

<#--div class="screenlet">
    <div class="screenlet-title-bar">
        <div class="h3">Quick Add To Favourite List</div>
    </div>
    <div class="screenlet-body">
        <form name="addToShoppingList" method="post" action="<@ofbizUrl>addItemToShoppingList<#if requestAttributes._CURRENT_VIEW_?exists>/${requestAttributes._CURRENT_VIEW_}</#if></@ofbizUrl>">
          <input type="hidden" name="shoppingListId" value="${shoppingList.shoppingListId}"/>
          <input type="text" class="inputBox" name="productId" value="${requestParameters.add_product_id?if_exists}"/>
          <#if reservStart?exists></td><td class="tabletext">${uiLabelMap.EcommerceStartDate}</td><td><input type="text" class="inputBox" size="10" name="reservStart" value="${requestParameters.reservStart?default("")}" /></td><td class="tabletext"> ${uiLabelMap.EcommerceLength}:</td><td><input type="text" class="inputBox" size="2" name="reservLength" value="${requestParameters.reservLength?default("")}" /></td></tr><tr><td>&nbsp;</td><td>&nbsp;</td><td class="tabletext">${uiLabelMap.OrderNbrPersons}:</td><td><input type="text" class="inputBox" size="3" name="reservPersons" value="${requestParameters.reservPersons?default("1")}" /></td><td class="tabletext" nowrap="nowrap"></#if> ${uiLabelMap.CommonQuantity} :</td><td><input type="text" class="inputBox" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" /></td><td>
          <!-- <input type="text" class="inputBox" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" />-->
          <#-->input type="submit" class="smallSubmit" value="Add To Favourite List"/>
        </form>
    </div>
</div-->

    <#else>
        <#-- shoppingList was found, but belongs to a different party -->
        <h2>${uiLabelMap.EcommerceShoppingListError} ${uiLabelMap.CommonId} ${shoppingList.shoppingListId}) ${uiLabelMap.EcommerceListDoesNotBelong}.</h2>
    </#if>
</#if>
<div id="overlay" class="web_dialog_overlay"></div>
	<div id="dialog" style="display: none;" class="web_dialog" title="">
		<ul>
		<li><label id = "outputs"> </label></li>
			</ul>
</div>
<script>


function ShowDialog12(modal)
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
</script>
 <#if shoppingListItemDatas?has_content>
   <#list shoppingListItemDatas as shoppingListItemData>
   <#assign product = shoppingListItemData.product/>
    <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")/>
     <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists/>
     <#assign shoppingListItem = shoppingListItemData.shoppingListItem/>
<#if isVirtual && productVariantAssocs?has_content>
<#assign count =0>
<#list productVariantAssocs as productVariantAssoc>
<#assign count =count+1>
		<#assign variantProduct = productVariantAssoc.getRelatedOneCache("AssocProduct")>
		           <#if variantProduct?exists>
		                  <#if count==1>
		                        <script>
		                           window.onload=checkInventory('add_product_id','${variantProduct.productId}','0','${shoppingListItem.shoppingListId?if_exists}','${shoppingListItem.productId?if_exists}','${shoppingListItem.shoppingListItemSeqId?if_exists}');
  									  </script>
  							</#if>
		            </#if>
		     	 </#list>
		         </#if>
		        </#list>
		                              
</#if>
 