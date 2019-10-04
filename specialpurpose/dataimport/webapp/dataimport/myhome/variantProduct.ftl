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

<div class="boxhead_Affiliate_a1">
	<div style="float:left; width:10px; overflow:hidden"><img src="/images/emartcatalog/middle_title_left_corner.jpg"></div>
	<div style="float:left; padding-top:7px;">Export Variant Product</div>
	<div style="float:right; width:10px; overflow:hidden"><img src="/images/emartcatalog/middle_right_corner.jpg"></div>
</div>
	
<div class="screenlet_Affiliate_a1">
<div style="height:auto; width:100%; overflow:hidden; padding-bottom:5px">
	<table border='0' width="100%" cellspacing='1' cellpadding='1' bgcolor="#77AAC6" align="left">
        <form method="get" name="ExportVariantProductForm" action="<@ofbizUrl>ExportVariantProduct</@ofbizUrl>" class="basic-form">
             
        <tr>    <td width='15%' height="25" align='right' bgcolor="#DAE4E6"><div class="tabletext_New_Affiliate">${uiLabelMap.ProductProductId}</div></td>
	            <td align="left" bgcolor="#DAE4E6">
	            <div class='title_text' style="padding-left:5px;">
            	<div style="float:left;"><input type='text' class='inputBox' name='productId' value='${requestParameters.productId?if_exists}'/></div>
            	<div style="float:left; padding-left:3px; padding-top:2px;"><a href="javascript:call_fieldlookup2(document.ExportVariantProductForm.productId, 'LookupProduct');"><img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="14" border="0" alt="Lookup"></a></div>
            	</div>
             </td>
           </tr> 
          <tr>		
          	<td width="20%" bgcolor="#FFFFFF" height="25"><div class="tabletext_New_Affiliate">From Date</div></td>
          	<td bgcolor="#FFFFFF" height="25"><div class="tabletexta_New_Affiliate">
          	<div style="float:left; padding-right:5px"><input type="text" class="inputBox" name="fromDate" value="${requestParameters.fromDate?if_exists}"/></div>
            <div style="float:left"><a href="javascript:call_cal(document.ExportVariantProductForm.fromDate, '${fromDateStr?if_exists} ');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a></div>
            </div>
            </td>
          </tr>
          <tr>		
          	<td width="20%" bgcolor="#DAE4E6" height="25"><div class="tabletext_New_Affiliate">Thru Date</div></td>
          	<td bgcolor="#DAE4E6" height="25"><div class="tabletexta_New_Affiliate">
          	<div style="float:left; padding-right:5px"><input type="text" class="inputBox" name="thruDate" value="${requestParameters.thruDate?if_exists}"/></div>
            <div style="float:left;"><a href="javascript:call_cal(document.ExportVariantProductForm.thruDate,'${thruDateStr?if_exists}');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Calendar"/></a>
            </div></div>
            </td>
          </tr>
            
		  <tr>
		  	<td width="20%" bgcolor="#FFFFFF" height="25">&nbsp;</td>
		  	<td bgcolor="#FFFFFF" height="25"><div class="tabletexta_New_Affiliate"><input type="submit" class="smallSubmit" value="Export"></div></td>      
		  </tr>	
          </form>
         </table> 
        </div>
   </div>  