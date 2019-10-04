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
 
<html>
 
		<table cellpadding="0" cellspacing="0" border="0" align="left" width="100%">
			<tr>
				<td><img style="width:144px; height:65px;" alt="Logo" src="${logoImageUrl?if_exists}"/></td>
  			</tr>
			<tr><td>&nbsp;</td></tr>
		</table>
Hi,
<br/><br/>
 ${senderName?if_exists} has sent you this message from <a href="youmart.in">youmart.in</a> to check this recipe <b>${recipeValue.recipeName?if_exists}</b>
<br/><br/>
<b> Method Of Prepartion:-</b>
${StringUtil.wrapString(recipeValue.description)?if_exists}
 <br/><br/>
<b> Recommended Variation:-</b>
${StringUtil.wrapString(recipeValue.variations)?if_exists}
<br/><br/>
<b> Ingrediants are:-</b>
  <#if recipeIngredientList?has_content>
  <#list recipeIngredientList  as recipeIngredient>
   ${recipeIngredient.productName?if_exists}   ${recipeIngredient.quantity?if_exists}
  </#list>
  </#if>
  <br/><br/>
<b>Message:</b>
<br/><br/>
${note}
<br/>
<br/>
Regards,
<br/>
${senderName?if_exists}
</html>
  