<#--
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
-->
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
<#-- This file incorporates a few lines from an ofbiz webtools file and �has been modified by Open Source Strategies, Inc. -->
<@import location="component://amazon/webapp/includes/opentapsFormMacros.ftl"/>

<#if hasDIAdminPermissions?default(false)>

<#assign dateFormat = Static["org.ofbiz.base.util.UtilDateTime"].getJsDateTimeFormat("DATE", locale)/>

<script type="text/javascript">
/*<![CDATA[*/
function makeTimestamp(target, source_date, source_hour, source_minute, source_ampm) {
    if (source_date && source_date.value.length > 0) {
        target.value = opentaps.formatDate(opentaps.parseDate(source_date.value, '${dateFormat}'), '%Y-%m-%d') + ' ';
        var hour = 0;
        if (source_ampm.value == 'AM') {
    	    hour = parseInt(source_hour.value) ;
        } else {
            hour = (12 + parseInt(source_hour.value)) ;
        }
        target.value += hour < 10 ? ('0' + hour) : hour;
        target.value += (':' + (source_minute.value.length == 1 ? ('0' + source_minute.value) : source_minute.value) + ':00.000');
    }
}

function onRunSyncChange() {
    var sb = document.getElementById('runSyncCtrl');
    if (sb && sb.value) {
    	var jobRow = document.getElementById('jobRow');
    	var startTimeRow = document.getElementById('startTimeRow');
        if (sb.value == 'Y') {
            if (jobRow) opentaps.addClass(jobRow, 'hidden');
            if (startTimeRow) opentaps.addClass(startTimeRow, 'hidden');
        } else {
            if (jobRow) opentaps.removeClass(jobRow, 'hidden');
            if (startTimeRow) opentaps.removeClass(startTimeRow, 'hidden');
        };
    }
}
/*]]>*/
</script>

<div class="subSectionBlock">

    <div class="subSectionHeader">
        <div class="subSectionTitle">${parameters.sectionHeaderUiLabel?if_exists}</div>
    </div>

	<#assign serviceName = parameters.SERVICE_NAME/>

    <form action="<@ofbizUrl>scheduleService</@ofbizUrl>" method="post" name="${serviceName}Form">
    	<@inputHidden name="SERVICE_TIME" value=""/>

        <#list scheduleOptions as scheduleOption>
	        <input type="hidden" name="${scheduleOption.name}" value="${scheduleOption.value}"/>
        </#list>

        <table class="twoColumnForm">
            <#list serviceParameters as serviceParameter>
                <#if serviceParameter.optional == "N">
                    <@inputTextRow title="${serviceParameter.name}(${serviceParameter.type})" name="${serviceParameter.name}" default="${serviceParameter.value?if_exists}" titleClass="requiredField"/>
                <#else>
                    <@inputTextRow title="${serviceParameter.name}(${serviceParameter.type})" name="${serviceParameter.name}" default="${serviceParameter.value?if_exists}"/>
                </#if>
            </#list>

            <tr>
                <td class="titleCell"><span class="tableheadtext">${uiLabelMap.DataimportSync}</span></td>
                <td>
                    <select id="runSyncCtrl" name="_RUN_SYNC_" class="inputBox" onchange="onRunSyncChange()">
                        <option selected value="Y">${uiLabelMap.CommonYes}</option>
                        <option value="N">${uiLabelMap.CommonNo}</option>
                    </select>
                <td>
            </tr>
            
            <tr id="jobRow" class="hidden">
            	<@displayCell text="${uiLabelMap.DataimportJobName}" class="tableheadtext" blockClass="titleCell"/>
                <@inputTextCell name="JOB_NAME"/>
            </tr>
            <tr id="startTimeRow" class="hidden">
                <@displayCell text="${uiLabelMap.DataimportServiceTime}" class="tableheadtext" blockClass="titleCell"/>
                <@inputDateTimeCell name="SERVICE_TIME" form="${serviceName}Form"/>
            </tr>
            
            <@inputSubmitRow title="${uiLabelMap.DataimportRunImport}" onClick="makeTimestamp(document.${serviceName}Form.SERVICE_TIME, document.${serviceName}Form.SERVICE_TIME_c_date, document.${serviceName}Form.SERVICE_TIME_c_hour, document.${serviceName}Form.SERVICE_TIME_c_minutes, document.${serviceName}Form.SERVICE_TIME_c_ampm); document.${serviceName}Form.submit();"/>

        </table>

    </form>

</div>

<script>
opentaps.addOnLoad(onRunSyncChange);
</script>

</#if>