<#escape x as x?xml>
          <fo:block font-size="8pt">${reportType?if_exists}</fo:block>
            <fo:block space-before="10pt"> </fo:block>
            <fo:table font-size="8px"  font-family="verdana" border-width="1px" border-style="solid">
                 <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="120pt"/>
                <fo:table-column column-width="100pt"/>
                <fo:table-column column-width="120pt"/>
                <fo:table-column column-width="50pt"/>
                <fo:table-column column-width="70pt"/>
                  
                  <fo:table-header background-color="rgb(189,189,189)">
                    <fo:table-row font-weight="bold" background-color="rgb(189,189,189)">
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>Name</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>SignIn</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>SignIn Note</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>SignOut</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>SignOut Note</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>Department</fo:block></fo:table-cell>
                        <fo:table-cell border-bottom=" solid grey" padding="2pt"><fo:block>Duration(Hours)</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                  <fo:table-body>
             <#if attenList?has_content>
                 <#assign total=0>
             <#list attenList as atten>
                 <#assign person=delegator.findByPrimaryKey("Person",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",atten.partyId))?if_exists/>
                 <#assign min=atten.duration/>
                 <#assign hr=(min/(60*60))/>
                 <#assign round= Static["org.ofbiz.accounting.AttendanceUtil"].Round(hr)>
                 <#assign total=total+hr/>
		         <#assign department = delegator.findByAnd("Employment",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyIdTo",atten.partyId))?if_exists/>
                 <fo:table-row>
                     <fo:table-cell padding="2pt">
                                <fo:block>${person.firstName?if_exists} ${person.lastName?if_exists}</fo:block>
                     </fo:table-cell>
                    <fo:table-cell padding="2pt">
                                <fo:block>${atten.signInTime?if_exists}</fo:block>
                     </fo:table-cell>
                    <fo:table-cell padding="2pt">
                                <fo:block>${atten.signInNote?if_exists}</fo:block>
                     </fo:table-cell>
                      <fo:table-cell padding="2pt">
                                <fo:block>${atten.signOutTime?if_exists}</fo:block>
                     </fo:table-cell>
                      <fo:table-cell padding="2pt">
                                <fo:block>${atten.signOutNote?if_exists}</fo:block>
                     </fo:table-cell>
                      <fo:table-cell padding="2pt">
                                <fo:block> ${atten.partyGroup?if_exists}</fo:block>
                      </fo:table-cell>
                      <fo:table-cell padding="2pt">
                                <fo:block>${round?if_exists}</fo:block>
                     </fo:table-cell>
                 </fo:table-row>
                </#list>
               <#else>
                   <fo:table-row>
                      <fo:table-cell padding="2pt">
                                <fo:block>NO DATA FOUND</fo:block>
                      </fo:table-cell>
                   </fo:table-row>   
              </#if>
             </fo:table-body> 
       </fo:table>
</#escape>