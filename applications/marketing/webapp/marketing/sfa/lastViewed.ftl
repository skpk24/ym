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
  $(function(){
	$('.dragclass')
		.draggable()
		.resizable();
});

  


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
   ${urlList?if_exists}

				  <#list urlList as urlLists>
				    </br>
			    <a href="${urlLists.requestUrl?if_exists}"  class="buttontext"> 
						<p><font size="4" face="verdana" color="black">${urlLists.contentId?if_exists}</font></p>
				</a>
				   
				  </#list>
      
</body>
</html>

