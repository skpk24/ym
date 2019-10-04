	
	<style>
	.tblClass td{
		text-align: center;
	}
	
	td {
	   border: 0px solid black;
	}
	</style>
	
	
	<table class='tblClass' width="100%" border="0"  cellspacing="10" cellpadding="1"> 
		<#if allBanner?has_content>
			<#list allBanner as allBannerItem >	
		
			<#if allBannerItem.imageType == 'BANNER' && allBannerItem.imageSequenceNum?if_exists ==10>
			     <tr >
					<td colspan='3'>
						<div align="center"> 
						    <img src="${allBannerItem.imagePath?if_exists}" border="0"  />			 
						<div> 
					</td>
				 </tr>
			</#if>
			</#list>
	</#if> 
	</table>