
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
	
		<#if allBannerItem.imageType == 'BANNER' && allBannerItem.imageSequenceNum?if_exists ==1>
		     <tr >
				<td colspan='3'>
					<div align="center"> 
					    <img src="${allBannerItem.imagePath?if_exists}" border="0"  />			 
					<div> 
				</td>
			 </tr>
		</#if>
		</#list>
		
		<tr>
		<td colspan='3'>
			<#assign count =0 />  
			<table   width="100%" border="0">	   
				<#list allBanner as allBannerContent >		
					<#if allBannerContent.imageType == 'CONTENT'>
					   <#assign count = count + 1 />  
					   <#if count == 1 >
					   <tr>
					   </#if>
					   <#if count <= 3 >
				        <td>
						<div align="center">
					     <div> 
					    	${allBannerContent.imageName?if_exists}
					     </div>
					     <div>
					    	<a href='${allBannerContent.imageLinkUrl?if_exists}' > 
					    	<img src="${allBannerContent.imagePath?if_exists}" border="0" width='210px;'/>  
					    	</a>
					     </div>
					     <div>
					    	${allBannerContent.imageContent?if_exists}
					     </div>	 
						 </div>
						</td> 
			    	   </#if>
			    	   <#if count gte 3 >		
					   </tr>
					   <#assign count =0 />
					   </#if>
					 </#if>  
				</#list> 
			</table>
		</td>
		</tr>  
</#if> 
</table>