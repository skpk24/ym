<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />


<link rel="stylesheet" href="/opentaps_css/opentaps-packed.css" type="text/css"/>
<link href="/opentaps_css/integratingweb/opentaps.css" rel="stylesheet" type="text/css" />
<link rel="shortcut icon" href="/opentaps_images/favicon.ico">

				 <style type="text/css">
.dragclass{
position : relative;
	cursor : move;
	}

</style>
<script type="text/javascript">
//window.location = "https://192.168.1.94:8444/control/w_product/~product_id=upcom4-L/~Prof.%20Gerard_Puccio/~Adhyapan_%22An_ACT_that_makes_a_difference%22_,Delhi-2012" ;
  $(function(){
	$('.dragclass')
		.draggable()
		.resizable();
});
	 
	 function popcontact(URL) {
		var popup_width = 600
		var popup_height = 400
		day = new Date();
		id = day.getTime();
		eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width='+popup_width+',height='+popup_height+'');");
	}
	</script>
    <script type="text/javascript">
        function writeAppDetails(appId, appName, appDescr){
            var id = document.getElementById('appId');
            var name = document.getElementById('appName');
            var description = document.getElementById('appDescr');

            id.innerHTML = appId;
            name.innerHTML = appName;
            description.innerHTML = appDescr;
        }

        function forgotPasswd(){
            //if the errorDiv is present, increase the heigth of the container
            if(document.getElementById('errorDiv')){
               document.getElementById('container').style.height='540px';
               document.getElementById('form').style.height='380px';
            }

            var forgotPasswdForm = document.getElementById('forgotpasswd');
            forgotPasswdForm.style.display='block';
        }
    </script>


<title>Getting started with Nichesiute SFA</title>
</head>

<body>
<div id="top"></div>
<div id="container">
	<div id="header">
    	<div id="logo"><img src="&#47;opentaps_images&#47;opentaps_logo.png" /></div>
        <div id="title">
        	<h1>
                <span id="appId" style="color:#FF3300">Nichesuite</span>
                <span id="appName">Getting started with Nichesiute SFA</span>
            </h1>
        </div>
    </div>


<style type="text/css">
.gray-panel-header {
    background: gray;
    color: white;
    font:bold 11px tahoma,arial,verdana,sans-serif;
    padding:5px 2px 4px 20px;
    border:1px gray;
    line-height:15px;
}

.rss-frame-section {
    width: 245px;
    margin-left: 0px;
    margin-right: auto;
    margin-top: 20px;
}

.rss-tabletext, .rss-tabletext a:link,.rss-tabletext a:visited {
font-size: 8px;
text-decoration: none;
font-family: Verdana, Arial, Helvetica, sans-serif;
text-decoration: none;
color: black;
}

.rss-tabletext a:hover {
text-decoration: underline;
}

.rss-frame-section-body
{
background-color:#FFFFFF;
padding:4px;
border: 1px solid #999999;
}
</style>


    <div id="row">
       <table>
         <tr>
         <td>
          <div id="button" class="leads" onmouseover="javascript:writeAppDetails('Nichesuite SFA','Companies are representatives of organizations who show interest in your products or services.')">
              <a href="<@ofbizUrl>FindLeads</@ofbizUrl>">
                <img src="/images/sfa/company.jpg" onmouseover="this.src='/images/sfa/company.jpg'" onmouseout="this.src='/images/sfa/company.jpg'" height="150px" width="150px" />
              </a>
            <div id="label" style="margin-left: 25px;" for="crmsfa">
              <a style="color: black;" href="leads" >
              Company
              </a>
            </div>
          </div>
         </td>
         <td>  
          <div id="button" class="contacts" onmouseover="javascript:writeAppDetails('Nichesuite SFA','People in a company with whom you communicate and interact <br><br> in pursuit of a business opportunity.')">
              <a href="<@ofbizUrl>FindContacts</@ofbizUrl>">
                 <img src="/images/sfa/contacts.jpg" onmouseover="this.src='/images/sfa/contacts.jpg'" onmouseout="this.src='/images/sfa/contacts.jpg'" height="150px" width="150px" />
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href=""<@ofbizUrl>FindContacts</@ofbizUrl>" >
            Contacts
              </a>
            </div>
          </div>
       </div> 
       
        <td>  
          <div id="button" class="accounts" onmouseover="javascript:writeAppDetails('Nichesuite SFA','Accounts are companies or department within in a company, with<br><br> which you make business dealings.')">
             <a href="<@ofbizUrl>FindAccounts</@ofbizUrl>">
                 <img src="/images/sfa/accounts.jpg" onmouseover="this.src='/images/sfa/accounts.jpg'" onmouseout="this.src='/images/sfa/accounts.jpg'" height="150px" width="150px" />
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href="<@ofbizUrl>FindAccounts</@ofbizUrl>" >
               Accounts
              </a>
            </div>
          </div>
       </div> 
       
        <td>  
          <div id="button" class="opportunities" onmouseover="javascript:writeAppDetails('Nichesuite SFA','Opportunities are main business')">
              <a href="<@ofbizUrl>FindSalesOpportunity</@ofbizUrl>">
                 <img src="/images/sfa/opportunity.jpg" onmouseover="this.src='/images/sfa/opportunity.jpg'" onmouseout="this.src='/images/sfa/opportunity.jpg'" height="150px" width="150px" />
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href=""<@ofbizUrl>FindSalesOpportunity</@ofbizUrl>" >
            Opportunities
              </a>
            </div>
          </div>
       </div> 
       
        <td>  
          <div id="button" class="addUser" onmouseover="javascript:writeAppDetails('Nichesuite SFA','Add Users')">
              <a href="<@ofbizUrl>FindTeam</@ofbizUrl>">
                 <img src="/images/sfa/users.jpg" onmouseover="this.src='/images/sfa/users.jpg'" onmouseout="this.src='/images/sfa/users.jpg'"  height="150px" width="150px"/>
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href="<@ofbizUrl>FindTeam</@ofbizUrl>" >
                Add Users
              </a>
            </div>
          </div>
       </div> 
       <#if permission?exists >
        <td>  
          <div id="button" class="analy" onmouseover="javascript:writeAppDetails('Nichesuite SFA','Customize the SFA according to your business needs.')">
              <a href="<@ofbizUrl>Setup</@ofbizUrl>">
                 <img src="/images/sfa/settings.jpg" onmouseover="this.src='/images/sfa/settings.jpg'" onmouseout="this.src='/images/sfa/settings.jpg'" height="150px" width="150px" />
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href="<@ofbizUrl>Setup</@ofbizUrl>" >
                Settings
              </a>
            </div>
          </div>
       </div> 
       </td>
       </#if>
         <#if permission?exists>
         
         <td>  
          <div id="button" class="analy" onmouseover="javascript:writeAppDetails('Nichesuite SFA',' Reports.')">
              <a href="<@ofbizUrl>sfaReports</@ofbizUrl>">
                 <img src="/images/sfa/reports.jpg" onmouseover="this.src='/images/sfa/reports.jpg'" onmouseout="this.src='/images/sfa/reports.jpg'" height="150px" width="150px" />
               </a>
            <div id="label" style="margin-left: 25px;" for="financials">
              <a style="color: black;" href="<@ofbizUrl>sfaReports</@ofbizUrl>" >
                Reports
              </a>
            </div>
          </div>
       </div> 
       </td>
       
       </tr>
       </#if>
       
         
         <td>  
          <#-- <div id="button" class="lastView" onmouseover="javascript:writeAppDetails('Nichesuite SFA',' Last Visited.')"> 
          -->
             <form id="form1" runat="server">
			        <div id="test" class="dragclass" style="position:absolute;top:450px;left:160px;height:600px;width:300px;background-color:#00FF00;color:#00FF00">
			     <p><font size="4" face="verdana" color="black">Welcome ${userLoginId?if_exists}!</font></p>   
			     </br>
			      <p><font size="4" face="verdana" color="black">Following are the list of items viewed last time.</font></p>   
				  <#list urlList as urlLists>
				    </br>
   			      <a href="${urlLists.requestUrl?if_exists}"  class="buttontext"><p><font size="4" face="verdana" color="black">${urlLists.contentId?if_exists}</font></p></a>
				  </#list>
		            </div>
			 </form>
            </div>
          </div>
       </div> 
       </td>
       </table>
  </div> 

</div>

</div>

</body>
</html>

