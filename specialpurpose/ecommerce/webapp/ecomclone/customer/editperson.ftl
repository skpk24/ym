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
<script>
function changeBirthDateFormat(){
	var bdate = document.getElementById('USER_BIRTHDATE1').value;
	dates=bdate.trim();
	if(dates.length !=0)
	{
	var dt1Parts = dates.split('-');
	var dt1dd = parseInt(dt1Parts[0]);
    var	dt1mm = parseInt(dt1Parts[1]);
    var	dt1yyyy = parseInt(dt1Parts[2]);
	
	var dt1dd1 = dt1dd.toString();
    var dt1mm1 = dt1mm.toString();
    var dt1yyyyy1 = dt1yyyy.toString();
    
    if(dt1dd1.length==1){
        dt1dd1 =  '0'+ dt1dd1;
     }
    if(dt1mm1.length==1){
        dt1mm1 =  '0'+ dt1mm1;
    }
     	var x=new Date();
		x.setFullYear(dt1yyyyy1,dt1mm1-1,dt1dd1);
		var today = new Date();
		if (x>today)
		  {
		  	alert("Date of birth can't be future date.");
		  	document.getElementById('USER_BIRTHDATE1').value = '';
			document.getElementById('birthDate').value = '';
			document.getElementById('USER_BIRTHDATE1').focus();
		  }
		
    	else{
		    var dt = dt1yyyyy1 + '-'+dt1mm1+'-'+dt1dd1;
			document.getElementById('birthDate').value = dt;
		}
	}
	else{
		document.getElementById('USER_BIRTHDATE1').value = '';
		document.getElementById('birthDate').value = '';
		document.getElementById('USER_BIRTHDATE1').focus();
	}
}

function changePassportExipreDateFormat(){
	var bdate = document.getElementById('USER_PASSPORTEXPRDATE').value;
	dates=bdate.trim();
	if(dates.length !=0)
	{
	var dt1Parts = dates.split('-');
	var dt1dd = parseInt(dt1Parts[0]);
    var	dt1mm = parseInt(dt1Parts[1]);
    var	dt1yyyy = parseInt(dt1Parts[2]);
	
	var dt1dd1 = dt1dd.toString();
    var dt1mm1 = dt1mm.toString();
    var dt1yyyyy1 = dt1yyyy.toString();
    
    if(dt1dd1.length==1){
        dt1dd1 =  '0'+ dt1dd1;
     }
    if(dt1mm1.length==1){
        dt1mm1 =  '0'+ dt1mm1;
    }
    var dt = dt1yyyyy1 + '-'+dt1mm1+'-'+dt1dd1;
    
	document.getElementById('passportExpireDate').value = dt;
	}
	else{
		document.getElementById('USER_PASSPORTEXPRDATE').value = '';
		document.getElementById('passportExpireDate').value = '';
	}
}


</script>
<#if person?exists>
  <h2>${uiLabelMap.PartyEditPersonalInformation}</h2>
    <form method="post" class="creditcard-info" action="<@ofbizUrl>updatePerson/${donePage}</@ofbizUrl>" name="editpersonform">
<#else>
  <h2>${uiLabelMap.PartyAddNewPersonalInformation}</h2>
    <form method="post" class="creditcard-info" action="<@ofbizUrl>createPerson/${donePage}</@ofbizUrl>" name="editpersonform">
</#if>
<div>
  <a href='<@ofbizUrl>authview/${donePage}</@ofbizUrl>' class="buttontext">${uiLabelMap.CommonGoBack}</a>
  <a href="javascript:document.editpersonform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a>

  <input type="hidden" name="partyId" value="${person.partyId?if_exists}" />
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td align="right">${uiLabelMap.CommonTitle}</td>
    <td>
      <select name="personalTitle" class="selectBox">
        <#if personData.personalTitle?has_content >
          <option>${personData.personalTitle}</option>
          <option value="${personData.personalTitle}"> -- </option>
        <#else>
          <option value="">${uiLabelMap.CommonSelectOne}</option>
        </#if>
        <option>${uiLabelMap.CommonTitleMr}</option>
        <option>${uiLabelMap.CommonTitleMrs}</option>
        <option>${uiLabelMap.CommonTitleMs}</option>
        <option>${uiLabelMap.CommonTitleDr}</option>
      </select>
    </td>
  </tr>
  <tr>
    <td align="right">${uiLabelMap.PartyFirstName}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="30" name="firstName" value="${personData.firstName?if_exists}"/>
      *</td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyMiddleInitial}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="4" name="middleName" value="${personData.middleName?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyLastName}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="30" name="lastName" value="${personData.lastName?if_exists}"/>
      *</td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyNickName}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="nickname" value="${personData.nickname?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">City of Origin</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="suffix" value="${personData.suffix?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyGender}</td>
      <td>
        <select name="gender" class='selectBox' style="width:177px;">
          <#if personData.gender?has_content >
            <option value="${personData.gender}">
                <#if personData.gender == "M" >${uiLabelMap.CommonMale}</#if>
                <#if personData.gender == "F" >${uiLabelMap.CommonFemale}</#if>
            </option>
            <option value="${personData.gender}"> -- </option>
          <#else>
            <option value="">${uiLabelMap.CommonSelectOne}</option>
          </#if>
          <option value="M">${uiLabelMap.CommonMale}</option>
          <option value="F">${uiLabelMap.CommonFemale}</option>
        </select>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyBirthDate}</td>
      <td>
      	<input type="text" size="30" name="USER_BIRTHDATE3" id="USER_BIRTHDATE1" <#if personData.birthDate?has_content> value="${personData.birthDate?string("dd-MM-yyyy")?if_exists}"<#else> value=""</#if>  onblur="changeBirthDateFormat();" />
      	<script type="text/javascript">
						     
					            jQuery("#USER_BIRTHDATE1").datepicker({
					            
					              maxDate:0,
					            	  changeMonth: true,
										changeYear: true,
										yearRange : 'c-80:c',
					                showOn: 'button',
					                buttonImage: '',
					                buttonText: '',
					                showOn: "both",
					                buttonImageOnly: true,
					                dateFormat: 'dd-mm-yy',
					                
					                onSelect: function(){ 
					                var bdate = document.getElementById('USER_BIRTHDATE1').value;
	dates=bdate.trim();
	if(dates.length !=0)
	{
	var dt1Parts = dates.split('-');
	var dt1dd = parseInt(dt1Parts[0]);
    var	dt1mm = parseInt(dt1Parts[1]);
    var	dt1yyyy = parseInt(dt1Parts[2]);
	
	var dt1dd1 = dt1dd.toString();
    var dt1mm1 = dt1mm.toString();
    var dt1yyyyy1 = dt1yyyy.toString();
    
    if(dt1dd1.length==1){
        dt1dd1 =  '0'+ dt1dd1;
     }
    if(dt1mm1.length==1){
        dt1mm1 =  '0'+ dt1mm1;
    }
     	var x=new Date();
		x.setFullYear(dt1yyyyy1,dt1mm1-1,dt1dd1);
		var today = new Date();
		if (x>today)
		  {
		  	alert("Date of birth can't be future date.");
		  	document.getElementById('USER_BIRTHDATE1').value = '';
			document.getElementById('birthDate').value = '';
			document.getElementById('USER_BIRTHDATE1').focus();
		  }
		
    	else{
		    var dt = dt1yyyyy1 + '-'+dt1mm1+'-'+dt1dd1;
			document.getElementById('birthDate').value = dt;
		}
	}
	else{
		document.getElementById('USER_BIRTHDATE1').value = '';
		document.getElementById('birthDate').value = '';
		document.getElementById('USER_BIRTHDATE1').focus();
	}
					                 }
					                
					              });
					              
					             
					           </script>
        <input type="hidden" class='inputBox' size="30" maxlength="20" name="birthDate" id="birthDate" <#if personData.birthDate?has_content>value="${personData.birthDate.toString()?if_exists}" <#else> value=""</#if>/>
        <div class="tabletext"><#--${uiLabelMap.CommonFormatDate}-->Format: dd-MM-yyyy</div>
      </td>
    </tr>
    <#--<tr>
      <td align="right">${uiLabelMap.PartyHeight}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="height" value="${personData.height?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyWeight}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="weight" value="${personData.weight?if_exists}"/>
      </td>
    </tr>

    <tr>
      <td align="right">${uiLabelMap.PartyMaidenName}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="mothersMaidenName" value="${personData.mothersMaidenName?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyMaritalStatus}</td>
      <td>
        <select name="maritalStatus" class='selectBox' style="width:177px;">
          <#if personData.maritalStatus?has_content>
             <option value="${personData.maritalStatus}">
               <#if personData.maritalStatus == "S">${uiLabelMap.PartySingle}</#if>
               <#if personData.maritalStatus == "M">${uiLabelMap.PartyMarried}</#if>
               <#if personData.maritalStatus == "D">${uiLabelMap.PartyDivorced}</#if>
             </option>
          <option value="${personData.maritalStatus}"> -- </option>
          <#else>
          <option></option>
          </#if>
          <option value="S">${uiLabelMap.PartySingle}</option>
          <option value="M">${uiLabelMap.PartyMarried}</option>
          <option value="D">${uiLabelMap.PartyDivorced}</option>
        </select>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartySocialSecurityNumber}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="socialSecurityNumber" value="${personData.socialSecurityNumber?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyPassportNumber}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="passportNumber" value="${personData.passportNumber?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyPassportExpireDate}</td>
      <td>
      	<input type="text" size="30" name="USER_PASSPORTEXPRDATE" id="USER_PASSPORTEXPRDATE" <#if personData.passportExpireDate?has_content>value="${personData.passportExpireDate?string("dd-MM-yyyy")?if_exists}"<#else>value=""</#if> onblur="changePassportExipreDateFormat();" />
        <input type="hidden" class='inputBox' size="30" maxlength="20" name="passportExpireDate" id="passportExpireDate" <#if personData.passportExpireDate?has_content>value="${personData.passportExpireDate.toString()?if_exists}" <#else>value=""</#if>/>
        <div class="tabletext"><#--${uiLabelMap.CommonFormatDate} Format: dd-MM-yyyy</div>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.PartyTotalYearsWorkExperience}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="totalYearsWorkExperience" value="${personData.totalYearsWorkExperience?if_exists}"/>
      </td>
    </tr>
    <tr>
      <td align="right">${uiLabelMap.CommonComment}</td>
      <td>
        <input type="text" class='inputBox' size="30" maxlength="60" name="comments" value="${personData.comments?if_exists}"/>
      </td>
    </tr>-->
</table>
</div>
</form>

<a href='<@ofbizUrl>authview/${donePage}</@ofbizUrl>' class="buttontext">${uiLabelMap.CommonGoBack}</a>
<a href="javascript:document.editpersonform.submit()" class="buttontext">${uiLabelMap.CommonSave}</a><br/><br/>
