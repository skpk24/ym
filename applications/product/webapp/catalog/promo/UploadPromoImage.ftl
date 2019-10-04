
<hr class="sepbar"/>
<#if productPromoId?has_content>
<div style="height:auto; width:100%; overflow:hidden">
<div style="float:left;padding-left:80px" class="tabletext">${uiLabelMap.ProductUploadImage}</div>
 
    <div style="float:left;padding-left:25px;">
        <form method="post" enctype="multipart/form-data" action="<@ofbizUrl>UploadImagePromo?productPromoId=${productPromoId?if_exists}&amp;fname=</@ofbizUrl>" name="UploadCommEventImageForms">
            <input id="skp" type="file"  size="30" name="fname">        
            
       <div style="padding:10px 0px 5px 0px;">
            <#--<div style="float:left; padding:5px;"><input id="first" class="radioButton" type="radio" name="upload_file_type_bogus" value="first" checked ></div><div style="float:left;padding-top:7px; padding-left:5px; padding-right:10px" class="tabletext">First</div>
            <div style="float:left; padding:5px;"><input id="second" class="radioButton" type="radio" name="upload_file_type_bogus" value="second" ></div><div style="float:left;padding-top:7px; padding-left:5px; padding-right:10px" class="tabletext">Second</div>
             <div style="float:left; padding:5px;"><input id="third" class="radioButton" type="radio" name="upload_file_type_bogus" value="third"></div><div style="float:left;padding-top:7px; padding-left:5px; padding-right:10px" class="tabletext">Third</div>-->
             <div style="float:left; padding:5px;"><input id="third" class="radioButton" type="radio" name="upload_file_type_bogus" value="third" checked></div><div style="float:left;padding-top:7px; padding-left:5px; padding-right:10px" class="tabletext">Banner Image</div>
            <input type="submit" class="smallSubmit"  value="Upload Image" onclick='setUploadUrl1("<@ofbizUrl>UploadImagePromo?productPromoId=${productPromoId?if_exists}&amp;fname=</@ofbizUrl>");'>
       </div>
        </form>
     </div>
     
 </div>
<hr/>

<div>
<#--<a href="javascript:call_fieldlookup2(document.EditProductPromo.firstImageUrl,'useExisting?for=first')" class="Wbloglink"  title="useExisting">Use Existing (First)</a>
&nbsp;&nbsp;<a href="javascript:call_fieldlookup2(document.EditProductPromo.secondImageUrl,'useExisting?for=second')" class="Wbloglink"  title="useExisting">Use Existing (Second)</a>-->
&nbsp;&nbsp;<a href="javascript:call_fieldlookup2(document.EditProductPromo.thirdImageUrl,'useExisting?for=third')" class="Wbloglink"  title="useExisting">Use Existing (Banner Image)</a>
</div>
<script language="JavaScript" type="text/javascript">

        function setUploadUrl1(newUrl) {
            var foldername = "first";
            if(document.getElementById("third").checked == true)
            {
                foldername = "third";
            }
           start = document.getElementById("skp").value.length;
           stop = document.getElementById("skp").value.lastIndexOf("\\")+1;
           imgName = document.getElementById("skp").value.substring(stop,start);
           image =   imgName.substring(0,imgName.lastIndexOf("."));
           var toExec = 'document.UploadCommEventImageForms.action="' + newUrl+foldername+"&productPromoId2="+image+ '";';
           eval(toExec);
        }

        function setUploadUrl(newUrl) {
            var foldername = "first";
            if(document.getElementById("first").checked == true)
            {
                foldername = "first";
            }
            if(document.getElementById("second").checked == true)
            {
                foldername = "second";
            }
            if(document.getElementById("third").checked == true)
            {
                foldername = "third";
            }
           
         
           start = document.getElementById("skp").value.length;
           stop = document.getElementById("skp").value.lastIndexOf("\\")+1;
           imgName = document.getElementById("skp").value.substring(stop,start);
           image =   imgName.substring(0,imgName.lastIndexOf("."));
           var toExec = 'document.UploadCommEventImageForms.action="' + newUrl+foldername+"&productPromoId2="+image+ '";';
           eval(toExec);
        }
        
    </script>
    
</#if>