 <script type="text/javascript">
  
 $(document).ready(function()
 {
 
 // set financial year  begin
  $("#empYear").click( function()
   {
     //$.get("setFinancialYear?fromDate="+$("#fromDate").val()+"&throDate="+$("#throDate").val(),"",function(result)
      // {});
      $.ajax({
		  type: "GET",
		  url:"setFinancialYear?fromDate="+$("#fromDate").val()+"&throDate="+$("#throDate").val(),
		  dataType: "script",
		  success: function() {
		   
		  },
		  error: function() {
		   
		  },
		  complete:function(){
		 
		  }
		});
      
      
   });
   
 // set financial year end
 
  $(".editWeek").click( function(){
  $( "#dialog-week" ).show();
   
  // $("#dialog-week").siblings('div.ui-dialog-titlebar').remove();
   $( "#dialog-week" ).dialog({
			height: 250,
			width:  400,
			modal: true,
			draggable: false,
			resizable: false,
			
			
			buttons: {"Cancel": function()
			     {$("#dialog-week").dialog("close");},
				"Save": function() {
				$.get("calendarSettingWeek?weekDay="+$('#weekDay').val()+"&calendarId="+$('#weeklyCalendarId').val()+"&weekExclude="+$('#weekExclude').val(),"", function(result){
				   if(result=="success")
				    {
				      
				      $("div#clValDiv").text($('#weekDay').val());
				      $("#dialog-week").dialog("close");
				    }
				  });
				
				}
				}
			
		}); 
  });
  
   $(".editMonth").click( function(){
  $( "#dialog-month" ).show();
   //$("#dialog-month").siblings('div.ui-dialog-titlebar').remove();
   $( "#dialog-month" ).dialog({
			height: 250,
			width:  400,
			modal: true,
			draggable: false,
			resizable: false,
			
			buttons: {"Cancel": function()
			     {$("#dialog-month").dialog("close");},
				"Save": function() {
				$.get("calendarSettingMonth?monthDay="+$('#monthDay').val()+"&monthDate="+$('#monthDate').val()+"&calendarId="+$('#monthlyCalendarId').val()+"&monthExclude="+$('#monthExclude').val(),"", function(result){
				   if(result=="success")
				    {
				     if($("#monthDay").val() == "lastDayOfMonth")
                     {
				      $("div#clMonthValDiv").text("");
				     }
				     else
				     {
				      $("div#clMonthValDiv").text($("#monthDate").val());
				     }
				      $("#dialog-month").dialog("close");
				    }
				  });
				
				}
				}
			
		}); 
  });
  
   $("#monthDay").change( function()
     {
       if($("#monthDay").val() == "sameDayOfMonth")
       {
        $("#daystr").show();
       }
       else
       {
       $("#monthDate").val("");
       $("#daystr").hide();
       }
     }
    );
  
 });
 </script>
 <div id="dialog-week" title="" style="display:none;">
	 <table>
	  <tr>
      <td>${uiLabelMap.PayOn}</td>
       <td>
         <select name="weekDay" id="weekDay" >
            <option value="">Select Day Of Week</option>
            <option value="Sunday">Sunday</option>
            <option value="Monday">Monday</option>
            <option value="Tuesday">Tuesday</option>
            <option value="Wednesday">Wednesday</option>
            <option value="Thursday">Thursday</option>
            <option value="Friday">Friday</option>
            <option value="Saturday">Saturday</option>
           </select>
      </td>
     <tr>
	  <tr>
	    <td>Avoid Public Holidays and Weekends*</td>
	    <td><input type="checkbox" name="weekExclude" id="weekExclude" /></td>
	  <tr>
	</table>
</div>

<#-- dialog box for month -->
 <div id="dialog-month" title="" style="display:none;">
	 <table>
	 
	  <tr>
      <td>${uiLabelMap.PayOn}</td>
       <td>
          <select name="monthDay" id="monthDay" >
            <option value="">Select Pay On</option>
            <option value="lastDayOfMonth">Last Day Of Month</option>
            <option value="sameDayOfMonth">Same Day Of Month</option>
           </select>
      </td>
     <tr>
      
     <tr id="daystr" style="display:none;">
      
      <td>${uiLabelMap.DayOfTheMonth}</td>
       <td>
          <select name="monthDate" id="monthDate" >
            <option value="">Select Day Of Month</option>
           <option value="1">1st</option>
			<option value="2">2nd</option>
			<option value="3">3rd</option>
			<option value="4">4th</option>
			<option value="5">5th</option>
			<option value="6">6th</option>
			<option value="7">7th</option>
			<option value="8">8th</option>
			<option value="9">9th</option>
			<option value="10">10th</option>
			<option value="11">11th</option>
			<option value="12">12th</option>
			<option value="13">13th</option>
			<option value="14">14th</option>
			<option value="15">15th</option>
			<option value="16">16th</option>
			<option value="17">17th</option>
			<option value="18">18th</option>
			<option value="19">19th</option>
			<option value="20">20th</option>
			<option value="21">21st</option>
			<option value="22">22nd</option>
			<option value="23">23rd</option>
			<option value="24">24th</option>
			<option value="25">25th</option>
			<option value="26">26th</option>
			<option value="27">27th</option>
			<option value="28">28th</option>
			<option value="29">29th</option>
			<option value="30">30th</option>
			<option value="31">31st</option>
           </select>
      </td>
     <tr>
     
	  <tr>
	    <td>Avoid Public Holidays and Weekends*</td>
	    <td><input type="checkbox" name="weekExclude" id="weekExclude" /></td>
	  <tr>
	</table>
</div>
<#-- dialog box for month end here-->



 <div>
  <table>
  <#assign  weeklyPayDetail = requestAttributes.weeklyCalendar?if_exists/>
  <input type="hidden" name="weeklyCalendarId" value="${weeklyPayDetail.calendarId?if_exists}" id="weeklyCalendarId"/>
   <tr>
     <td colspan="2">${uiLabelMap.WeeklyPayDays}<td>
  </tr>
  <tr>
    <td>${uiLabelMap.PayOn}</td>
    <td><div id="clValDiv">${weeklyPayDetail.calendarValue?if_exists}</div></td>
  <tr>
  <tr>
    <td>${uiLabelMap.Scheduleon}</td>
    <td><div>Dates that avoid Public Holidays and Weekends</div></td>
  <tr>
  <tr>
     <td colspan="2" ><a href="#" class="editWeek" >${uiLabelMap.CommonEdit}</a><td>
  </tr>
  </table>
  
  <table>
  <#assign  monthlyPayDetail = requestAttributes.monthlyCalendar?if_exists/>
  <input type="hidden" name="monthlyCalendarId" value="${monthlyPayDetail.calendarId?if_exists}" id="monthlyCalendarId"/>
   <tr>
     <td colspan="2">${uiLabelMap.monthlyPayDays}<td>
  </tr>
  <tr>
    <td>${uiLabelMap.PayOn}</td>
    <td><div id="clMonthValDiv">${monthlyPayDetail.calendarValue?if_exists}</div></td>
  <tr>
  <tr>
    <td>${uiLabelMap.Scheduleon}</td>
    <td><div>Dates that avoid Public Holidays and Weekends</div></td>
  <tr>
  <tr>
     <td colspan="2" ><a href="#" class="editMonth" >${uiLabelMap.CommonEdit}</a><td>
  </tr>
  </table>
  
  <#-- tax year Or financial year setting -->
  
   <table>
      <tr>
       <td colspan="2">Financial Year</td>
       </td>
     </tr>
      <input type="hidden" name="finYearcalendarId" value="${requestAttributes.finYearcalendarId?if_exists}"/>
     
     <tr>
        <td>from Date</td>
        <td><input type="text" name="fromDate" id="fromDate"  size="25" maxlength="30" value="${requestAttributes.fromDate?if_exists}" >
        <script>
			$(function() {
				$( "#fromDate" ).datepicker({
					changeMonth: true,
					changeYear: true
				});
			});
	</script>
        
        </td>
    </tr>
    <tr>
        <td>thro Date</td>
        <td><input type="text" name="throDate" id="throDate"  size="25" maxlength="30" value="${requestAttributes.throDate?if_exists}">
        <script>
			$(function() {
				$( "#throDate" ).datepicker({
					changeMonth: true,
					changeYear: true
				});
			});
	</script>
        </td>
    </tr>
    <tr>
      <td colspan="2"><input type="button" value="Set Year" id="empYear"/></td>
    </tr>
  </table>
  <div id="pleasewait" style="display:none;">Please wait....</div>
</div>