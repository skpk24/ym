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


import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.util.*;

Set fieldToSelect = new ArrayList();
fieldToSelect.add("contentId");
fieldToSelect.add("contentName");
fieldToSelect.add("contentTypeId");
EntityWhereString ewContent=EntityWhereString.makeConditionWhere("content_Name = '"+context.condition+"' and content_Type_Id = 'DOCUMENT'");
	   contents = delegator.findList("Content",ewContent.freeze(),fieldToSelect,null,null,false);
context.contentList = contents ;
