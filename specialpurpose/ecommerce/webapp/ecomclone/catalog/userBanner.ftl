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

	<#if contentList?exists && contentList?has_content>
		<#list contentList as content>
					<#assign contentId1 = content.contentId>
					<#assign websitecontent = delegator.findByAnd("WebSiteContent", Static["org.ofbiz.base.util.UtilMisc"].toMap("webSiteId", "WebStoreClone", "webSiteContentTypeId", "PUBLISH_POINT","contentId",contentId1))>
					<div style="height:auto; overflow:hidden;">
						<#if websitecontent?has_content>
							<#list websitecontent as contents>
								<#assign contentId = contents.contentId>
								<@renderSubContentCache subContentId=contentId/>
							</#list>	
						</#if>
					</div>
		</#list>
	</#if>
	<#if bannerList?exists && bannerList?has_content>
		<#list bannerList as banner>
			<#assign bannerImageUrl = banner.bannerImageUrl/>
			<#assign bannerLinkUrl = banner.bannerLinkUrl/>
				<#if bannerImageUrl?has_content>
		
				<#if bannerImageUrl.contains("invite-a-friend")>
						

 <#--div id="invitefriendinfo" class="fb-verticalone1" style="display:none">
	${screens.render("component://ecommerce/widget/ecomclone/CatalogScreens.xml#inviteafriend")}
	<a href="#" class="telluslink" style="top:183px;"></a>
	<map name="Map3">
		<area shape="rect" coords="6,3,35,151" href="javascript:tellusinfo()" alt="close">
	</map>
</div-->

				
 <a href="javascript:tellus1()"><img src="${bannerImageUrl}" style="box-shadow:none !important; margin-bottom:5px;" class="header-main-link" /></a>

				<#else>
				
					<div> <a href="${bannerLinkUrl?if_exists}"><img src="${bannerImageUrl}" style="margin-bottom:4px;"/></a> </div>
					</#if>
				</#if>
		</#list>
	</#if>
	
