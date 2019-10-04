<div class="recipeofweektitle">Chef of the Week</div>
<#if recipeWeek?has_content && recipeWeek.photo?has_content>
<div class="weekrecipiebox">
	 <a href="<@ofbizUrl>displayrecipeWeek</@ofbizUrl>"><img src="${recipeWeek.photo?if_exists}" width="172px" height="180px" style="padding-bottom:5px;"/>
	 <span style="color:#06509B;">${name?if_exists}</span></a>
 </div>
<#else>
<a href="<@ofbizUrl>displayrecipeWeek</@ofbizUrl>"><img src="/erptheme1/photonotavailable.jpg" width="172px" height="180px" style="padding-bottom:5px;"/>
<span style="color:#06509B;">${name?if_exists}</span>
<a>
</#if>