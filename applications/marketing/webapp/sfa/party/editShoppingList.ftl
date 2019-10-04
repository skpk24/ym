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

<br />
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.PartyShoppingLists}</li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
    <#if shoppingLists?has_content>
      <form name="selectShoppingList" method="post" action="<@ofbizUrl>editShoppingList</@ofbizUrl>">
      	<input name="partyId" type="hidden" value="${partyId?if_exists}"/>
        <select name="shoppingListId" onchange="javascript:document.selectShoppingList.submit()">
          <#if shoppingList?has_content>
            <option value="${shoppingList.shoppingListId}">${shoppingList.listName}</option>
            <option value="${shoppingList.shoppingListId}">--</option>
          </#if>
          <#list allShoppingLists as list>
            <option value="${list.shoppingListId}">${list.listName}</option>
          </#list>
        </select>
        <#--<a href="javascript:document.selectShoppingList.submit();" class="smallSubmit">${uiLabelMap.CommonEdit}</a>-->
      </form>
    <#else>
      ${uiLabelMap.PartyNoShoppingListsParty}.
    </#if>
  </div>
</div>
<br />
<#if shoppingList?has_content>
<#if childShoppingListDatas?has_content>
<br />
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.PartyChildShoppingList} - ${shoppingList.listName}</li>
      <li><a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}&amp;includeChild=yes</@ofbizUrl>">${uiLabelMap.PartyAddChildListsToCart}</a></li>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
    <table class="basic-table" cellspacing="0">
      <tr class="header-row">
        <td>${uiLabelMap.PartyListName}</td>
        <td>&nbsp;</td>
      </tr>
      <#list childShoppingListDatas as childShoppingListData>
        <#assign childShoppingList = childShoppingListData.childShoppingList>
        <tr>
          <td class="button-col"><a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${childShoppingList.listName?default(childShoppingList.shoppingListId)}</a></li>
          <td class="button-col align-float">
            <a href="<@ofbizUrl>editShoppingList?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyGotoList}</a>
            <a href="<@ofbizUrl>addListToCart?shoppingListId=${childShoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyAddListToCart}</a>
          </td>
        </tr>
      </#list>
    </table>
  </div>
</div>
</#if>
<br />
<div class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.PartyListItems} - ${shoppingList.listName}</li>
        <#-- <li><a href="<@ofbizUrl>addListToCart?shoppingListId=${shoppingList.shoppingListId}</@ofbizUrl>">${uiLabelMap.PartyAddListToCart}</a></li> -->
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
    <#if shoppingListItemDatas?has_content>
      <table class="basic-table" cellspacing="0">
        <tr class="header-row">
          <td>${uiLabelMap.PartyProduct}</td>
          <td>${uiLabelMap.PartyQuantity}</td>
          <td>${uiLabelMap.PartyQuantityPurchased}</td>
          <td>${uiLabelMap.PartyPrice}</td>
          <td>${uiLabelMap.PartyTotal}</td>
        </tr>
        <#assign alt_row = false>
        <#list shoppingListItemDatas as shoppingListItemData>
          <#assign shoppingListItem = shoppingListItemData.shoppingListItem>
          <#assign product = shoppingListItemData.product>
          <#assign productContentWrapper = Static["org.ofbiz.product.product.ProductContentWrapper"].makeProductContentWrapper(product, request)>
          <#assign unitPrice = shoppingListItemData.unitPrice>
          <#assign totalPrice = shoppingListItemData.totalPrice>
          <#assign productVariantAssocs = shoppingListItemData.productVariantAssocs?if_exists>
          <#assign isVirtual = product.isVirtual?exists && product.isVirtual.equals("Y")>
          <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            <td><#--<a href="/catalog/control/EditProduct?productId=${shoppingListItem.productId}&amp;externalLoginKey=${requestAttributes.externalLoginKey}">${shoppingListItem.productId} -
              ${productContentWrapper.get("PRODUCT_NAME")?default("No Name")}</a> :--> ${shoppingListItem.productId} - ${productContentWrapper.get("DESCRIPTION")?if_exists}
            </td>
			<td>
			  ${shoppingListItem.quantity?if_exists}
			</td>
			<td>
			  ${shoppingListItem.quantityPurchased?if_exists}
			</td>
            <td><@ofbizCurrency amount=unitPrice isoCode=currencyUomId/></td>
            <td><@ofbizCurrency amount=totalPrice isoCode=currencyUomId/></td>
          </tr>
          <#assign alt_row = !alt_row>
        </#list>
      </table>
    <#else>
      ${uiLabelMap.PartyShoppingListEmpty}.
    </#if>
  </div>
</div>
<br />
</#if>