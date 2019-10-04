<html>
<script type="text/javascript">
  function submitForm(elemId)
  {
	 $("#boardSetup").attr("action",elemId);
    $( "#dashDialog" ).dialog({
			height: 400,
			width:  500,
			modal: true,
			draggable: false,
			resizable: true,
			buttons: {"Cancel": function()
			     {$("#dashDialog").dialog("close");},
				 "GO": function() {
				  $("#dashDialog").dialog("close");
				  $("#boardSetup").submit();
				}
				}
			}); 
		

		 }
</script>

<table class="basic-table">

<div style="text-align: left; margin-top: 20px">
 <img src="/images/pos/dashboards/${imagelocation?if_exists}" width="300" height="300" border="0" alt=""/>


</div>
<div id="dashDialog" style="display:none;">
     <ul style="float:left;"> 
      <form name="boardSetup"  id="boardSetup"   action="defaultaction" method="get">
      <tr>
	 <td class="label">
   Reports Type
    </td>
    <td>
  <select name="reports" id="reports">
    <option value="Daily Sales Report">Daily Sales Report</option>  
     <option value="Weekly Sales Report">Weekly Sales Report</option>  
     <option value="Monthly Sales Report">Monthly Sales Report</option>  
     <option value="Daily purchase Report">Daily purchase Report</option>  
     <option value="Weekly purchase Report">Weekly purchase Report</option>
     </td>
     </tr>
       <tr>
   <td class="label"><span>From Date </span></td>
   <td><input type="text" name="fromDate" id="fromDate"/>
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
   <td class="label"><span>To Date </span></td>
   <td><input type="text" name="toDate" id="toDate"/>
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
  <td class="label">
      <span>Format</span>
     </td>
     <td>
     <input type="radio" name="category" id="csv" value="csv" /> CSV
     <input type="radio" name="category" id="chart" value="chart" /> CHART
     <input type="radio" name="category" id="html" value="html" /> HTML
     <input type="radio" name="category" id="pdf" value="pdf" /> PDF
     </td>
     </tr>
      <tr>
   <td>
   </td>
   <td>
     <input type="button" id="ShowResult" value="Go"  onclick="submitForm(this.id)"/>
   </td>
  </tr>
     </div>
   <div style="text-align: right; margin-top: 20px">
     <tr>
    <td class="label">
      <span>Last URL Visited</span>
     </td>
     <td>
     <tr>
        
        <#assign i = 0>
        <#if urlList?exists  && urlList?has_content>
         <#list urlList as url>
         <#if i<=4>
         <tr><td>
         ${url.initialRequest?if_exists}
          </td></tr>
       <#assign i = i+1>
         </#if>
         </#list>
         </#if>
   </div>
     
     
     
     </html>