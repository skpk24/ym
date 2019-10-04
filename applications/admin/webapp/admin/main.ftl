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
function createnotes()
{

var makeurl = "createNote";
	document.createNote.action=makeurl;
     document.createNote.submit();
}

function addInput(divName,divyear,divweek){
 
var Month=new Array("JAN","FEB","MAR","APR","May","JUNE","JULY","AUG","SEP","OCT","NOV","DEC");
   var e =document.getElementById("reportType");
var reportty = e.options[e.selectedIndex].value;
var r=reportty.split(" ",1);

if(r=="Monthly")
{
 document.getElementById('week').style.display='none';
     document.getElementById('month').style.display='block';
      document.getElementById('year').style.display='block';
      document.getElementById('toDate').style.display='none';
   document.getElementById('fromDate').style.display='none';
    }
    if(r=="Weekly")
{
   document.getElementById('week').style.display='block';
   document.getElementById('month').style.display='block';
   document.getElementById('year').style.display='block';
     document.getElementById('toDate').style.display='none';
   document.getElementById('fromDate').style.display='none';
    }
    if(r=="Daily")
    {
 
   document.getElementById('toDate').style.display='block';
   document.getElementById('fromDate').style.display='block';
   document.getElementById('week').style.display='none';
   document.getElementById('month').style.display='none';
   document.getElementById('year').style.display='none';
   
    }
    
    
    
}
function submitForm(elemId)
  {

	$('#dashDialog').empty();
 var e =document.getElementById("reportType");
var reportty = e.options[e.selectedIndex].value;
var type1=reportty.split(" ");

var f =document.getElementById("category");
var reporttyp = f.options[f.selectedIndex].value;

if(reporttyp=="chart")
{

if(type1[1]=="Sales")
		{
 $.ajax({
          
		  type: "POST",
		  url:"showChart1",
		  
		    data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
		 dataType: "json",
		  success: function() {
		  },
		  error: function() {
		  },
		  complete:function(xml){
		
		   var data=xml.responseText;
		 
	      
		    $('#dashDialog').append("<img src=\"/images/pos/dashboards/"+data+"\" border=\"0\" width=\"400\" height=\"400\" alt=\"dfdfsdfsdfsdfsdf\"/>");
		 
		    $( "#dashDialog").show();
		   
		 $( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
		
		if(type1[1]=="purchase")
		{
		$.ajax({
		  type: "POST",
		  url:"showChart2",
		    data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
		 dataType: "json",
		  success: function() {
		  },
		  error: function() {
		  },
		  complete:function(xml){
		
		   var data=xml.responseText;
		  
	    
		    $('#dashDialog').append("<img src=\"/images/pos/dashboards/"+data+"\" border=\"0\" width=\"400\" height=\"400\" alt=\"dfdfsdfsdfsdfsdf\"/>");
		 
		    $( "#dashDialog").show();
		    
		 $( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
	if(type1[1]=="customer")
	{

	$.ajax({
	type: "POST",
	url:"showChart3",
	data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
	dataType: "json",
	success: function() {
	},
	error: function() {
	
	},
	complete:function(xml){
	 var data=xml.responseText;
	 
	$('#dashDialog').append("<img src=\"/images/pos/dashboards/"+data+"\" border=\"0\" width=\"400\" height=\"400\" alt=\"dfdfsdfsdfsdfsdf\"/>");
	
	$( "#dashDialog").show();
	
	$( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
}

if(reporttyp=="html")
{

if(type1[1]=="Sales")
		{
 $.ajax({
          
		  type: "POST",
		  url:"salesHtml",
		  
		    data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
		 dataType: "json",
		  success: function() {
		  },
		  error: function() {
		  },
		  complete:function(xml){
		
		   var data=xml.responseText;
		 
	      
		    $('#dashDialog').append(data);
		 
		    $( "#dashDialog").show();
		   
		 $( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
		
		if(type1[1]=="purchase")
		{
		$.ajax({
		  type: "POST",
		  url:"purchaseHtml",
		    data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
		 dataType: "json",
		  success: function() {
		  },
		  error: function() {
		  },
		  complete:function(xml){
		
		   var data=xml.responseText;
		  
	    
		    $('#dashDialog').append(data);
		 
		    $( "#dashDialog").show();
		    
		 $( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
	if(type1[1]=="customer")
	{

	$.ajax({
	type: "POST",
	url:"customerHtml",
	data: { reportType : e.options[e.selectedIndex].value, category : f.options[f.selectedIndex].value,minDate :document.getElementById("fromDate").value,maxDate :document.getElementById("toDate").value,week :document.getElementById("week").value,month :document.getElementById("month").value,year :document.getElementById("year").value},
	dataType: "json",
	success: function() {
	},
	error: function() {
	
	},
	complete:function(xml){
	 var data=xml.responseText;
	 
	$('#dashDialog').append(data);
	
	$( "#dashDialog").show();
	
	$( "#dashDialog").dialog({
			height: 500,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				 
				}
				}
				
			});
			
			}
		
		});
		}
}



		if(reporttyp=="csv")
		{
		
		if(type1[1]=="Sales")
		{
	
	var makeurl = "revenueReoprtCSV";
	document.boardSetup.action=makeurl;
    document.boardSetup.submit();
   		}
   	if(type1[1]=="purchase")
		{
		
	var makeurl = "purchaseCSV";
  	document.boardSetup.action=makeurl;
    document.boardSetup.submit();
   		}
   	if(type1[1]=="customer")
		{
	
	var makeurl = "customerCSV";
	document.boardSetup.action=makeurl;
     document.boardSetup.submit();
   		}
   			}
		
 }
 
</script>

       

 <script>
	 function expanseDetail(){
	            	 window.open("<@ofbizUrl>expanseDetail</@ofbizUrl>", "", "width=800,height=600,status=no,scrollbars=yes");
	            	 }
</script>
            
<div style="float:right; width:50%">
	<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
	<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
	<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
	<#assign contextPath = request.getContextPath()>
	<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "main")>
	<#if userLogin?has_content>
		<div id="app-navigation">
			<h2 style="padding:0 0 0 10px">Dashboard</h2>
		</div>
		<#--div class="screenlet">
		<div class="screenlet-title-bar"><ul><li class="h3">Expense Detail</li></ul><br class="clear" /></div>
		<div class="screenlet-body" style="padding:20px; font-size:14px; font-weight:bold;">
		
		<a href="javascript:expanseDetail();" >Total   Expense = ${totalExpanse?if_exists}</a>
		</div>
		</div-->
		<form name="createNote"  id="createNote"   action="defaultaction" method="get">
	  		<table>
 		  		<tr>
 		  			<td class="label"><span>Note </span> </td>
					<td> <textarea name="note" id="note" rows="4" cols="70" onblur="createnotes()">${noteInfo?if_exists}</textarea></td>
					<input type="hidden" name="partyId" value="${partyId}"/>
  
   					</tr>
			</table>
		</form>
   		<div class="dashboard">
        	<ul>
            	<#list displayApps as display>
	              	<#assign thisApp = display.getContextRoot()>
	              	<#assign permission = true>
	              	<#assign selected = false>
	              	<#assign permissions = display.getBasePermission()>
	              	<#list permissions as perm>
	                	<#if perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session)>
		                  	<#-- User must have ALL permissions in the base-permission list -->
		                  	<#assign permission = false>
	               	 	</#if>
	              	</#list>
	              	<#if permission == true>
	                	<#if thisApp == contextPath || contextPath + "/" == thisApp>
	                  		<#assign selected = true>
	            		</#if>
	                	<#assign thisApp = StringUtil.wrapString(thisApp)>
	                	<#assign thisURL = thisApp>
	                	<#if thisApp != "/">
	                  		<#assign thisURL = thisURL + "/control/main">
	                	</#if>
	                  	<#if layoutSettings.suppressTab?exists && display.name == layoutSettings.suppressTab>
	                   	 <!-- do not display this component-->
	                  	<#else>
	                    	<li><a href="${thisURL + externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}"><#else> title="${display.description}"></#if><img src="/bizznesstime/images${thisApp}.png"/><br/><#if uiLabelMap?exists>${uiLabelMap[display.title]}<#else> ${display.title}</#if></a></li>
	                  	</#if>
	              	</#if>
            	</#list>
               	<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
				<#if (requestAttributes.externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
				<#if (externalLoginKey)?exists><#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey?if_exists></#if>
				<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
				<#assign contextPath = request.getContextPath()>
				<#assign displayApps = Static["org.ofbiz.base.component.ComponentConfig"].getAppBarWebInfos(ofbizServerName, "secondary")>
				<#if userLogin?has_content>
    				<#list displayApps as display>
			      		<#assign thisApp = display.getContextRoot()>
			      		<#assign permission = true>
			      		<#assign selected = false>
			      		<#assign permissions = display.getBasePermission()>
			      		<#list permissions as perm>
			        		<#if perm != "NONE" && !security.hasEntityPermission(perm, "_VIEW", session)>
			          			<#-- User must have ALL permissions in the base-permission list -->
			          			<#assign permission = false>
		        			</#if>
			      		</#list>
			      		<#if permission == true>
        					<#if thisApp == contextPath || contextPath + "/" == thisApp>
          						<#assign selected = true>
        					</#if>
        					<#assign thisApp = StringUtil.wrapString(thisApp)>
        					<#assign thisURL = thisApp>
        					<#if thisApp != "/">
          						<#assign thisURL = thisURL + "/control/main">
        					</#if>
        					<li><a href="${thisURL}${externalKeyParam}"><img src="/bizznesstime/images${thisApp}.png"/></a><a<#if selected> class="current-section"</#if> href="${thisURL}${externalKeyParam}" <#if uiLabelMap?exists> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}"> ${display.title}</#if></a></li>
      					</#if>
    				</#list>
				</#if>
			</ul>
		</div>
	</#if>
</div>
<div style="float:left; width:50%; overflow:hidden">
	<div class="screenlet">
		<div class="screenlet-title-bar"><ul><li class="h3">Reports</li></ul><br class="clear" /></div>
		<div class="screenlet-body">
			<form name="boardSetup"  id="boardSetup"   action="defaultaction" method="get">
				<table width="80%">
		      		<tr>
		    			<td>
		  					<select name="reportType" id="reportType" onchange="addInput()">
		  						<option value="---">Report Type</option>
		  						<#if reportList?exists  && reportList?has_content>
		        				 	<#list reportList as reportlist>
		     							<option value="${reportlist?if_exists}">${reportlist?if_exists}</option>
		         					</#list>
		      					</#if>
							</select>
		     			</td>
		     		</tr>
		     		<tr>
		   				<td> 
							<input type="text" name="minDate" id="fromDate" style="display:none" onblur="if (this.value == '') {this.value = 'Start Date'  }" onfocus="if (this.value == 'Start Date') {this.value = ''; }" value="Start Date"/>
		     				<script>
								$(function() {
									$( "#fromDate" ).datetimepicker({
										changeMonth: true,
										changeYear: true,
										showSecond: true,
										timeFormat: 'hh:mm:ss',
										stepHour: 1,
										stepMinute: 1,
										stepSecond: 1,
										dateFormat: 'yy-mm-dd'
									});
								});
							</script>
						</td>
					</tr>
		     		<tr>
						<td>
							<input type="text" name="maxDate" id="toDate" style="display:none" onblur="if (this.value == '') {this.value = 'End Date'  }" onfocus="if (this.value == 'End Date') {this.value = ''; }" value="End Date"/>
					     	<script>
								$(function() {
									$( "#toDate" ).datetimepicker({
										changeMonth: true,
										changeYear: true,
										showSecond: true,
										timeFormat: 'hh:mm:ss',
										stepHour: 1,
										stepMinute: 1,
										stepSecond: 1,
										dateFormat: 'yy-mm-dd'
									});
								});
							</script>
				   		</td>
				   	</tr>
				   	<tr>
				   		<td>
							<select NAME="week" id="week" style="display:none">
								<OPTION VALUE="01">Choose a week...</OPTION>
								<OPTION VALUE="01">First</OPTION>
								<OPTION VALUE="02">Second</OPTION>
								<OPTION VALUE="03">Third</OPTION>
								<OPTION VALUE="04">Fourth</OPTION>
							</select>
						</td>
					</tr>
		     		<tr>	
						<td>			
							<select NAME="month" id="month" style="display:none">
								<OPTION VALUE="01">Choose a Month...</OPTION>
								<OPTION VALUE="01">JAN</OPTION>
								<OPTION VALUE="02">FEB</OPTION>
								<OPTION VALUE="03">MAR</OPTION>
								<OPTION VALUE="04">APR</OPTION>
								<OPTION VALUE="05">MAY</OPTION>
								<OPTION VALUE="06">JUNE</OPTION>
								<OPTION VALUE="07">JULY</OPTION>
								<OPTION VALUE="08">AUG</OPTION>
								<OPTION VALUE="09">SEP</OPTION>
								<OPTION VALUE="10">OCT</OPTION>
								<OPTION VALUE="11">NOV</OPTION>
								<OPTION VALUE="12">DEC</OPTION>
							</select>
						</td>
					</tr>
		     		<tr>
						<td>
							 <select name="year" id="year" style="display:none">
								<option value="2010">2010</option>
								<option value="2009">2009</option>
								<option value="2011">2011</option>
								<option value="2012">2012</option>
								<option value="2013">2013</option>
								<option value="2014">2014</option>
							</select>
						</td>
					</tr>
		     		<tr>
						<td>
							<select name="category" id="category">
								<option value="------">Report Format</option>  
		     					<option value="csv">csv</option>  
							    <option value="chart">Chart/Graph</option>  
							    <option value="html">html</option>  
							</select>
		 				</td>
		 			</tr>
		 			<tr>
						<td>
		 					<input type="button" id="ShowResult" value="View Report"  onclick="submitForm(this.id)"/>
						</td>
					</tr>
				</table>
			</form>
	  		<div id="dashDialog"  style="display:none;"></div>
			<div style="text-align: left; margin-top: 20px">
	 			<img src="/images/pos/dashboards/${imagelocation?if_exists}"  border="0" alt=""/>
			</div>
		</div>
	</div>
	<div class="screenlet">
		<div class="screenlet-title-bar"><ul><li class="h3">Recent Activity</li></ul><br class="clear" /></div>
		<div class="screenlet-body">
				<table width="100%" class="basic-table">
		     		<tr>
		   	 			<td>
		     			</td>
		     		</tr>
	        		<#assign i = 0>
	    			<#if urlList?exists  && urlList?has_content>
	     				<#list urlList as url>
	     					<#if i<=4>
		     					<tr>
		     						<td>
		     							<a href="${url.initialRequest?if_exists}">Worked on ${url.webappName?if_exists?string?upper_case} module .</a>
		      						</td>
		      					</tr>
		   						<#assign i = i+1>
	     					</#if>
	     				</#list>
	     			</#if>
         		</table>
         	 
   		</div>
   	</div>
</div>