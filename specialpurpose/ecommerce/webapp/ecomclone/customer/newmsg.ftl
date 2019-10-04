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

<div class="screenlet">
    <div class="screenlet-title-bar" style="background:none !important;">
        <div class="boxlink">
            <#if showMessageLinks?default("false")?upper_case == "TRUE">
                <#--<a href="<@ofbizUrl>messagelist</@ofbizUrl>" class="submenutextright">${uiLabelMap.EcommerceViewList}</a>-->
            </#if>
        </div>
        <div class="Shopcar_pageHead">${pageHeader}</div>
    </div>
    <div class="screenlet-body">
      <form name="contactus" method="post" action="<@ofbizUrl>${submitRequest}</@ofbizUrl>" onSubmit="return validateCaptchaCode();" style="margin: 0;">
        <input type="hidden" name="partyIdFrom" value="${userLogin.partyId}"/>
        <input type="hidden" name="contactMechTypeId" value="WEB_ADDRESS"/>
        <input type="hidden" name="communicationEventTypeId" value="WEB_SITE_COMMUNICATI"/>
        <input type="hidden" value="" name="captchaCode" id="captchaCode"/>
        <#if productStore?has_content>
          <input type="hidden" name="partyIdTo" value="${productStore.payToPartyId?if_exists}"/>
        </#if>
        <input type="hidden" name="note" value="${Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()}"/>
        <#if message?has_content>
          <input type="hidden" name="parentCommEventId" value="${communicationEvent.communicationEventId}"/>
          <#if (communicationEvent.origCommEventId?exists && communicationEvent.origCommEventId?length > 0)>
            <#assign orgComm = communicationEvent.origCommEventId>
          <#else>
            <#assign orgComm = communicationEvent.communicationEventId>
          </#if>
          <input type="hidden" name="origCommEventId" value="${orgComm}"/>
        </#if>
        <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxbottom' style="font-size:12px;">
          <tr>
            <td colspan="3">&nbsp;</td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right">${uiLabelMap.CommonFrom}:</td>
            <td>&nbsp;${sessionAttributes.autoName?if_exists} <!--[${userLogin.partyId}] (${uiLabelMap.CommonNotYou}?&nbsp;<a href="<@ofbizUrl>autoLogout</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonClickHere}</a>)--></td>
          </tr>
          <#if partyIdTo?has_content>
            <#assign partyToName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyIdTo, true)>
            <input type="hidden" name="partyIdTo" value="${partyIdTo}"/>
            <tr>
              <td width="5">&nbsp;</td>
              <td align="right">${uiLabelMap.CommonTo}:</td>
              <td>&nbsp;${partyToName}</td>
            </tr>
          </#if>
          <#assign defaultSubject = (communicationEvent.subject)?default("")>
          <#if (defaultSubject?length == 0)>
            <#assign replyPrefix = "RE: ">
            <#if parentEvent?has_content>
              <#if !parentEvent.subject?default("")?upper_case?starts_with(replyPrefix)>
                <#assign defaultSubject = replyPrefix>
              </#if>
              <#assign defaultSubject = defaultSubject + parentEvent.subject?default("")>
            </#if>
          </#if>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right">${uiLabelMap.EcommerceSubject}:</td>
            <td><input type="text" class="inputBox" name="subject" size="20" value="${defaultSubject}"/></td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right" style="vertical-align:top !important;">${uiLabelMap.CommonMessage}:</td>
            <td><textarea name="content" class="textAreaBox" cols="40" rows="5" style="margin-left:4px;"></textarea></td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right" style="vertical-align:top !important;">${uiLabelMap.CommonCaptchaCode}:</td>
            <td>
            	<span id="captchaCodeImage" style="-moz-user-select: none; -khtml-user-select: none;-webkit-user-select: none;user-select: none; border-style: none; border-color: inherit; border-width: medium; background-color:black; color:red; font-family: 'Curlz MT'; font-size: x-large; font-weight: bold; font-variant: normal; letter-spacing: 2pt; padding:3px; margin:5px 5px 5px 4px; display:inline-block; height:30px; width: auto;"></span>
            	<a href="javascript:DrawCaptcha();">${uiLabelMap.CommonReloadCaptchaCode}</a>
			</td>
          </tr>
          <tr>
            <td width="5">&nbsp;</td>
            <td align="right" style="vertical-align:top !important;">${uiLabelMap.CommonVerifyCaptchaCode}:</td>
            <td><input type="text" id="captcha" autocomplete="off" maxlength="30" size="23" name="captcha"/>*</td>
          </tr>
          <tr>
            <td colspan="2">&nbsp;</td>
            <td><input type="submit" class="smallSubmit" value="${uiLabelMap.CommonSend}" style="margin:5px 0 0 4px;"/></td>
          </tr>
        </table>
      </form>
    </div>
</div>
<script type="text/javascript" language="JavaScript">

    function DrawCaptcha(){
       var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
		var string_length = 8;
		var randomstring = '';
		for (var i=0; i<string_length; i++) {
			var rnum = Math.floor(Math.random() * chars.length);
			randomstring += chars.substring(rnum,rnum+1);
		}
		document.getElementById("captchaCode").value = randomstring.toLowerCase();
		document.getElementById("captchaCodeImage").innerHTML = randomstring.toLowerCase();
    }
function validateCaptchaCode(){
	var userEnteredCaptchaCode = document.getElementById("captcha").value;
	var verifyCaptchaCode = document.getElementById("captchaCode").value;
	if(userEnteredCaptchaCode == "" || userEnteredCaptchaCode == null){
		alert("Please enter captcha code.");
		return false;
	}
	if(verifyCaptchaCode != userEnteredCaptchaCode){
		alert("Captcha code is miss match. Please enter correct captcha code.");
		return false;
	}else{
		return true;
	}
}    
    
	window.onload=DrawCaptcha;
</script>