<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <title>Untitled Document</title>
    <script>
    function generateRow() {
    var d=document.getElementById("div");
    d.innerHTML+="<p><input type='text' name='food'>";
    }
    </script>
    </head>
    <body>
    <form id="form1" name="form1" method="post" action="<@ofbizUrl>createLead</@ofbizUrl>">
    <div id="div"></div>
    <p><input type="button" value="Add" onclick="generateRow()"/></p>
    <p>
    <label>
    <input type="submit" name="Submit" value="Submit" />
    </label>
    </p>
    </form>
    </body>
    </html>