<!DOCTYPE html >
<html>

  <#if parameters.viewFTL.equals("CompanyByRoles")>
<script language='javascript'>


function ReplaceAll(content,findWith,replaceWith){
  var str = content;
    var index = str.indexOf(findWith);
        while(index != -1){
            str = str.replace(findWith,replaceWith);
            index = str.indexOf(findWith);
        }
        return str;
} 

function cmpnyByContacts(chartType){

            var chart = chartType ;
            
            var cmpnyByCntTargetCosts=document.getElementById("targetCostTeam").value;
            cmpnyByCntTargetCosts= ReplaceAll(cmpnyByCntTargetCosts,"[","");
            cmpnyByCntTargetCosts= ReplaceAll(cmpnyByCntTargetCosts,"]","");
            var cmpnyByCnt = cmpnyByCntTargetCosts.split(',');
            var costs = new Array();
            for(i=0;i<cmpnyByCnt.length;i++){
            costs[i] = parseInt(cmpnyByCnt[i]);
            }
            
            var leadNames=document.getElementById("leadNameTeam").value;
            leadNames= ReplaceAll(leadNames,"[","");
            leadNames= ReplaceAll(leadNames,"]","");
            cmpnyByCnt = leadNames.split(',');
           
            if(chart=="Bar"){
            document.getElementById('cmpnyByCntCVS1').style.visibility = 'hidden';
            document.getElementById('cmpnyByCntCVS').style.visibility = 'visible';
            
            var bar7 = new RGraph.Bar('cmpnyByCntCVS',costs);
            bar7.Set('chart.variant', ['3d']);
            bar7.Set('chart.units.post', 'K');
            bar7.Set('chart.colors.sequential', true);
            bar7.Set('chart.labels', cmpnyByCnt);
            bar7.Set('chart.background.grid.autofit.numvlines', 6);
            bar7.Set('chart.gutter.left', 100);
            bar7.Set('chart.gutter.bottom', 100);
            bar7.Draw();
            }
            else{
            document.getElementById('cmpnyByCntCVS').style.visibility = 'hidden';
             document.getElementById('cmpnyByCntCVS1').style.visibility = 'visible'
            var pie = new RGraph.Pie('cmpnyByCntCVS1', costs);
            pie.Set('chart.labels.sticks', true);
            pie.Set('chart.labels.sticks.length', 15);
            pie.Set('chart.labels',cmpnyByCnt);
            pie.Set('chart.tooltips', cmpnyByCnt);
            pie.Set('chart.radius', 100);
            pie.Set('chart.strokestyle', 'transparent');
            pie.Set('chart.exploded', [15]);
            RGraph.Effects.Pie.RoundRobin(pie);
            }
}
</script>
<head>
    <link rel="stylesheet" href="/images/jquery/plugins/dashboards/demos.css" type="text/css" media="screen" />
    
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.common.core.js" ></script>
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.common.key.js" ></script>
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.common.tooltips.js"></script>
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.common.effects.js" ></script>
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.common.dynamic.js" ></script>
    <script type="text/javascript" src="/images/jquery/dashboards/RGraph.pie.js" ></script>
     <script type="text/javascript" src="/images/jquery/dashboards/RGraph.bar.js" ></script>
    <!--[if lt IE 9]><script src="../excanvas/excanvas.js"></script><![endif]-->
    
    <title>Team Wise Targets According To Target Cost </title>
    
</head>
<body>
        
    <h1>Lead By Roles</h1>
    <table>
    <tr>
    <input type="radio" id="Bar" name="chart" onclick="cmpnyByContacts('Bar');"/> Bar Chart <br>
    <input type="radio" id="Pie" name="chart" onclick="cmpnyByContacts('Pie');"/> Pie Chart <br>

    </tr>
    <tr>
        <canvas id="cmpnyByCntCVS" width="300" height="400">[No canvas support]</canvas>
        <canvas id="cmpnyByCntCVS1" width="300" height="400">[No canvas support]</canvas>
    </tr>
    </table>


<form method="post" name="lookuporder" id="lookuporder" >

    <p>
    <input type="hidden" id = "leadNameTeam"  value="${cmpnyByCntTargetsReportNames}" />
    <input type="hidden" id = "targetCostTeam"  value="${cmpnyByCntTargetsReportTargetCost}" />
    </p>
    </form>
</body>
</#if>
</html>