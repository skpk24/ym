<#--div id="common-bottom" style="position:fixed; right:0px; bottom:0px;"-->
    <#--if autoUserLogin?has_content>
    <a href="javascript:popUpSmall('<@ofbizUrl>inviteafriend?refId=${autoUserLogin.userLoginId}</@ofbizUrl>','tellafriend');" >Invite A Friend</a>
	<#else>
	<a onclick='return checkLogin()' href="javascript:popUpSmall('<@ofbizUrl>inviteafriend</@ofbizUrl>','tellafriend');" >Invite A Friend</a>
	</#if><br/>
	<#a href="<@ofbizUrl>onlineHelpDesk</@ofbizUrl>" style="margin-top:3px;">Online Help Desk</a><br/>
	<a href="<@ofbizUrl>surpriseWithaGift</@ofbizUrl>">Surprise with a Gift</a-->
	<#-->a href="<@ofbizUrl>feedback</@ofbizUrl>" style="margin-top:4px;">Feedback</a-->
<#--/div-->
<script type="text/javascript">
	function checkLogin()
 		{
   alert("Please login for inviting friend");
   return false;
}
</script>


