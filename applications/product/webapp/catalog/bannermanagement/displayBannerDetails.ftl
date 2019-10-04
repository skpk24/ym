<br/><br/>
<#-- Banner Management component://product/webapp/catalog/bannermanagement/displayBannerDetails.ftl -->

<form method="GET"  action="#" name="WebSiteStaticBanner">
<table class="basic-table" cellpadding="1" cellspacing="1">
	<tr class="header-row">
		   <td>Select All<input type='checkbox' name='checkall' onclick="checkedAll('banner_Form')"></td>
		   <td>CONTENT ID</td>
		   <td>Content Name</td>
		   <td>From Date</td>
		   <td>Thru Date</td>
		   <td>Status</td>
	</tr>
	<#if contents?exists && contents?has_content>
		<#list contents as content>
				 <tr id="${content.contentId}_${content.dataResourceId}">
				   <td><input type="Checkbox" name="deleteThisBanner" value="${content.contentId}_${content.dataResourceId}"/></td>
				   <td>${content.contentId}</td>
				   <td>
							<select name="ID_${content.contentId}" id="ID_${content.contentId}" onmouseover="clearUpdateBanner('RESULT_BANNER_${content.contentId}');" onChange="updateBanner('contentName','ID_${content.contentId}',${content.contentId},'RESULT_BANNER_','Content')" >
									<option value="${content.contentName}" >${content.contentName}</option>
									<option value="Position One(Home Page)" >Position One(Home Page)</option>
									<option value="Position Two(Left Bar)" >Position Two(Left Bar)</option>
									<option value="Position Three(Contact Us)" >Position Three(Contact Us)</option>
									<option value="Position Four(Buttom)" >Position Four(Buttom)</option>
							</select>&nbsp;<span id='RESULT_BANNER_${content.contentId}'></span> 
				   </td>
				   <td>
					   	<input type="text" name="fromDate_${content.contentId}" id="fromDate_${content.contentId}" size="25" maxlength="40" value="${content.fromDate?if_exists}" onmouseover="clearUpdateBanner('RESULT_FROM_DATE_${content.contentId}');" onfocus="updateDate('fromDate','fromDate_${content.contentId}','${content.contentId}' , 'RESULT_FROM_DATE_')"/>
					   	<#assign nowTimestampString = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()?if_exists>
	                	<a href="javascript:call_cal(document.WebSiteStaticBanner.fromDate_${content.contentId},'${nowTimestampString}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a>&nbsp;<span id='RESULT_FROM_DATE_${content.contentId}'></span> 
				   </td>
				   <td>
					   	<input type="text" name="thruDate_${content.contentId}" id="thruDate_${content.contentId}" size="25" maxlength="40" value="${content.thruDate?if_exists}" onmouseover="clearUpdateBanner('RESULT_THRU_DATE_${content.contentId}');" onfocus="updateDate('thruDate','thruDate_${content.contentId}','${content.contentId}','RESULT_THRU_DATE_')"/>
					   	<#assign nowTimestampString = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()?if_exists>
	                	<a href="javascript:call_cal(document.WebSiteStaticBanner.thruDate_${content.contentId},'${nowTimestampString}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a>&nbsp;<span id='RESULT_THRU_DATE_${content.contentId}'></span> 
				   </td>
				   <td><a href="#" onClick ="updateStatus('${content.contentId}')"><div id ='statusValue_${content.contentId}'>${content.status?if_exists}</div></a>&nbsp;<span id='RESULT_STATUS_${content.contentId}'></span> </td>
				 </tr>
		 </#list>
     </#if>
</table>
<td><INPUT TYPE="Button" VALUE="Delete" onClick="deleteSelectedBanners(this.form);"></td>
</form>
<script type="text/javascript">
checked=false;
function checkedAll (checkId) {
	var aa= document.getElementById(checkId);
	 if (checked == false)
          {
           checked = true
          }
        else
          {
          checked = false
          }
		for (var i =0; i < aa.elements.length; i++) 
		{
		 aa.elements[i].checked = checked;
		}
      }
      
      
      
function deleteSelectedBanners(frm)
	{
		var no_of_banners = 0;
		var contentId = '';
		var dataResourceId = '';
		var disableChekboxValue = '' ;
		if(frm.deleteThisBanner.length == undefined)
			{
				var bannerData = frm.deleteThisBanner.value;
				      	  var array = bannerData.split("_");
				      	  		contentId = contentId +array[0]+',';
				      	  		alert(array[1].subString(0,array[1].length-1));
								dataResourceId = dataResourceId +array[1]+',';
				no_of_banners = no_of_banners+1;
				disableChekboxValue = disableChekboxValue +0+',';
			}
		else
		{	
			for (i = 0; i < frm.deleteThisBanner.length; i++)
			      if (frm.deleteThisBanner[i].checked)
			      {
			      	  if (document.getElementById(frm.deleteThisBanner[i].value).style.display !="none")
			      	  {
				      	  no_of_banners = no_of_banners+1;
				      	  var bannerData = frm.deleteThisBanner[i].value;
				      	  var array = bannerData.split("_");
				      	  		contentId = contentId +array[0]+',';
								dataResourceId = dataResourceId +array[1].substring(1, array[1].length-1)+',';
			          }
			          disableChekboxValue = disableChekboxValue +i+',';
			      }
		}
		if(no_of_banners != 0)
		{
			var confirmValue = '';
			if(no_of_banners == 1)
				confirmValue = "Are you sure to delete 1 banner ";
			else
				confirmValue = "Are you sure to delete "+no_of_banners+" banners";
			var confirm=window.confirm(confirmValue);
			if (confirm)
			{
				contentId = contentId.slice(0,contentId.length-1);
				dataResourceId = dataResourceId.slice(0,dataResourceId.length-1);
				 $.get("deleteBanners?contentId="+contentId+"&dataResourceId="+dataResourceId,"",function(result){
                      if(result == "success")
							{
								var array = disableChekboxValue.split(",");
								for (j = 0; j < array.length-1; j++)
								{
									var v = parseInt(array[j]);
					          		document.getElementById(frm.deleteThisBanner[v].value).style.display ="none";
								}
								window.location.reload();
							}
                    });
				
			}
		}
		else
			{
				alert("please select a Banner");
			}
			
	}
	
function updateBanner(columnName , fieldName ,contentId ,updateId ,tableName)
	{
		new Ajax.Request("updateBanner", {
			asynchronous: true,
			parameters: {columnName : columnName ,contentNameValue:$(fieldName).value ,contentId:contentId,tableName : tableName},
			onSuccess: function(transport) {
				var data = transport.responseText;
	            if(data == "success")
					{
        				$(updateId+contentId).update("Updated");
        			}
	            else
		            {
		            	$(updateId+contentId).update("Failed");
		            }
			}
		});
	}
function clearUpdateBanner(id)
	{
		document.getElementById(id).innerHTML = "";
	}
function updateDate(columName , fieldName , contentId ,resultId )
	{
		updateBanner(columName , fieldName , contentId , resultId ,'WebSiteContent');
		chekDate('fromDate_'+contentId , 'thruDate_'+contentId , 'statusValue_'+contentId)
	}
function updateStatus(contentId)
	{
		new Ajax.Request("updateStatus", {
			asynchronous: true,
			parameters: {contentId:contentId},
			onSuccess: function(transport) {
				var data = transport.responseText;
				var array = data.split("_");
				if(array[0] == "success")
					document.getElementById('thruDate_'+contentId).innerHTML = 'array[1]';
			}
		});
	}
function chekDate(fromDateId , thruDateId ,statusId)
	{
		if($(fromDateId).value  < $(thruDateId).value)
		{
			var container = document.getElementById(statusId);
			container.innerHTML = "Active";
		}
		else
		{
			var container = document.getElementById(statusId);
			container.innerHTML = "Inactive";
		}
	}
</script>
  