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
<script language="JavaScript" type="text/javascript">
	function setUploadUrl(newUrl) {
        var toExec = 'document.ReportForm.action="' + newUrl + '";';
        eval(toExec);
	};
</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
        <div class='boxhead'>&nbsp;Import Data Report</div>       
    </div>
    <div class="screenlet-body">
		<table border='0' width="100%" cellspacing='1' cellpadding='1' bgcolor="#77AAC6" align="left">
        	<form method="get" name="ReportForm" action="<@ofbizUrl>ExportCatalogReport</@ofbizUrl>" class="basic-form">
	        <tr bgcolor="#FFFFFF">
	        	<td colspan="2">
		         	<div style="padding-bottom:20px; padding-top:3px; width:500px;" class="tabletext1">
			            <div style="float:left; padding-right:3px;"><input class="radioButton" type="radio" name="option" value="catalog" checked onclick='setUploadUrl("<@ofbizUrl>ExportCatalogReport</@ofbizUrl>");'></div><div style="float:left; padding-right:15px; padding-top:2px;">Catalog</div>
			            <div style="float:left; padding-right:3px;"><input class="radioButton" type="radio" name="option" value="category" onclick='setUploadUrl("<@ofbizUrl>ExportCategoryReport</@ofbizUrl>");'></div><div style="float:left; padding-right:15px; padding-top:2px;">Category</div>
			            <div style="float:left; padding-right:3px;"><input class="radioButton" type="radio" name="option" value="feature"onclick='setUploadUrl("<@ofbizUrl>ExportFeatureReport</@ofbizUrl>");'></div><div style="float:left; padding-right:15px; padding-top:2px">Feature</div>
			            <div style="float:left; padding-right:3px;"><input class="radioButton" type="radio" name="option" value="product" onclick='setUploadUrl("<@ofbizUrl>ExportProductReport</@ofbizUrl>");'></div><div style="float:left; padding-right:10px; padding-top:2px">Product</div>
			            <div style="float:left; padding-right:3px;"><input class="radioButton" type="radio" name="option" value="variantproduct" onclick='setUploadUrl("<@ofbizUrl>ExportVariantProductReport</@ofbizUrl>");'></div><div style="float:left; padding-right:10px; padding-top:2px">Variant Product</div>
		            </div>
				</td>        
	        </tr>
	        <tr>		
	          	<td width="10%" bgcolor="#DAE4E6" height="25">From Date</td>
	          	<td bgcolor="#DAE4E6" height="25">
		          	<input type="text" name="fromDate" value="${fromDate?if_exists}"/>
		            <a href="javascript:call_cal(document.ReportForm.fromDate, '${fromDateStr?if_exists} ');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a>
	            </td>
			</tr>
          	<tr>		
	          	<td width="10%" bgcolor="#FFFFFF" height="25">Thru Date</td>
	          	<td bgcolor="#FFFFFF" height="25">
		          	<input type="text" name="thruDate" value="${thruDate?if_exists}"/>
		            <a href="javascript:call_cal(document.ReportForm.thruDate,'${thruDateStr?if_exists}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a>
	            </td>
          	</tr>
          	<tr>
          		<td width="10%" bgcolor="#DAE4E6" height="25">Data Status</td>
          		<td bgcolor="#DAE4E6" height="25">
          			<select name="status" onchange="submitData();">
				  		<#if requestParameters.status?exists>
				  	  	<option value="${status?if_exists}">${status?if_exists}</option>
				  	  	<option value="">---</option>
				  	  	<#else>
				      	<option>All</option>
				      	<option value="">---</option>
				      	</#if>
				     	<option>All</option>
				      	<option>Success</option>
				      	<option>Unsuccess</option>
		 			</select>
		 		</td>
          	</tr>  
		  	<tr>
		  		<td width="20%" bgcolor="#FFFFFF" height="25">&nbsp;</td>
		  		<td bgcolor="#FFFFFF" height="25">
		  			<input type="submit" class="smallSubmit" value="Find">
		  		</td>      
		  	</tr>	
          </form>
         </table> 
         <div class="clear"></div>
	</div>
</div>     