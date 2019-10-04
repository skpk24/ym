/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

contentList = [];
websites = delegator.findByAnd("WebSite",[productStoreId : context.productStoreId]);
if(websites != null){
   i = websites.iterator();
   while(i.hasNext()){
	   website = i.next();
	   contents = delegator.findByAnd("WebSiteContent",[webSiteId : website.webSiteId]);
	   j = contents.iterator();
	   while(j.hasNext()){
		   unit = j.next();
		   value = delegator.findByPrimaryKey("Content",[contentId : unit.contentId]);
		   if(value){
		   contentsmap = [contentId : unit.contentId, webSiteId : unit.webSiteId, webSiteContentTypeId : unit.webSiteContentTypeId, fromDate : unit.fromDate, localeString : value.localeString, contentName : value.contentName];
		   contentList.add(contentsmap);
		   }
	   }
   }
   context.content = contentList;
}

targetUrl="createContentMethodType";
context.link = "Create";
contentId = parameters.get("contentId");
context.contentId = contentId;
context.textData = "";
localeString=request.getParameter("localeString");
if(contentId!=null && contentId.length() > 0)
{
   contents=delegator.findByPrimaryKey("Content",[contentId:contentId]);
   if(contents.localeString.equalsIgnoreCase(localeString)){
	   contentId = contentId;
	   context.link = "Update";
   }else{
	   List contentAssociaton=ContentWorker.getAssociatedContent(contents,"TO",["ALTERNATE_LOCALE"],["DOCUMENT"],null,null);
	   if(contentAssociaton!=null && contentAssociaton.size() > 0 && localeString!=null && localeString.length() > 0){
		   i=contentAssociaton.iterator();
		   while(i.hasNext())
		   {
			   value=i.next();
			   if(value.get("localeString").equals(localeString)){
				   contentId = value.contentId;
				   context.link = "Update";
				   break;
			   }
		   }
	   }
   }
   if(context.link.equals("Update")){
	   data = delegator.findByPrimaryKey("ContentElectronicTextView",[contentId:contentId]);
	   context.textData = data.textData;
   }
   targetUrl="updateContentMethodType";
}
context.targetUrl=targetUrl;
context.productStoreId = parameters.productStoreId;
context.webSiteId = parameters.webSiteId;
context.webSiteContentTypeId = parameters.webSiteContentTypeId;
context.localeString = parameters.localeString;
