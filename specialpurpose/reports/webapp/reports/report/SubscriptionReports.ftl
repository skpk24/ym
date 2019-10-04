<script language="JavaScript" type="text/javascript">

    function paginateContactList(viewSize, viewIndex) {
        document.paginationForm.viewSize.value = viewSize;
        document.paginationForm.viewIndex.value = viewIndex;
        document.paginationForm.submit();
    }

</script>


<div class="screenlet">

	<#--div >
		<br/>
		<table>
		<tr>
		<td>&nbsp;</td>
		<td><b> Export Report: </b></td>
		<td> 
			<a href="<@ofbizUrl>NewsLetterReport.csv</@ofbizUrl>" class="buttontext"> <span class='tabletext'> CSV</span></a>
		</td>
		<td>&nbsp;</td>
		</tr>
		</table>
		<br/>
	</div-->

	<div class="space3">
	<#assign viewIndexMax = Static["java.lang.Math"].ceil(listSize?double / viewSize?double)+1>
	<select name="pageSelect" class="selectBox" onchange="window.location=this[this.selectedIndex].value;">
		<option value="#">${uiLabelMap.CommonPage} ${viewIndex?int+1} ${uiLabelMap.CommonOf} ${viewIndexMax}</option>
		<#list 1..viewIndexMax as curViewNum>
		  <option value="javascript:paginateContactList('${viewSize}', '${curViewNum-1}')">${uiLabelMap.CommonGotoPage} ${curViewNum}</option>
		</#list>
	</select>
	<b>
	<#if (listSize?int > 0)>
	  <span class="tabletext">${lowIndex} - <#if (listSize?int > highIndex?int)>${highIndex?if_exists}<#else> ${listSize?if_exists} </#if> ${uiLabelMap.CommonOf} ${listSize}</span>
	</#if>
	</b>
	</div>
	<br>
    <form name="paginationForm" method="post" action="<@ofbizUrl>SubscriptionReports</@ofbizUrl>">
		<input type="hidden" name="viewSize"/>
		<input type="hidden" name="viewIndex"/>
    </form>

    <div >
        <table width="100%" cellspacing="1" cellpadding="5" border="0" class="basic-table hover-bar">
            <tr class="header-row">
              <td width="15%">${uiLabelMap.EcommerceListName}</td>
              <td width="15%">${uiLabelMap.CommonFromDate}</td>
              <td width="15%">${uiLabelMap.CommonStatus}</td>
              <td width="15%">${uiLabelMap.CommonEmail}</td>
            </tr>
           <#assign alt_row = false>
          <#list subscriptionList as contactListParty>
          	<tr valign="middle" <#if alt_row> class="alternate-row"</#if>>
              <td width="15%"><div class="tabletext">${contactListParty.contactListName?if_exists}</div></td>
              <td width="15%"><div class="tabletext">${contactListParty.date?if_exists}</div></td>
              <td width="15%"><div class="tabletext">${contactListParty.status?if_exists}</div></td>
              <td width="15%"><div class="tabletext">${contactListParty.emailId?if_exists}</div></td>
          	</tr>
          	<#assign alt_row = !alt_row>
          </#list>
        </table>
        <div class="space3">
        <a href="<@ofbizUrl>NewsLetterReport.csv</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonCSVReport}</a>
        </div>
    </div>
</div>


