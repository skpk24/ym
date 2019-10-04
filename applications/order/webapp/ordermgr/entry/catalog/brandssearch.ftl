<script type="text/javascript">
function brandsItem(brandindex) {
var  param = 'BRAND_NAME=' + brandindex;
                      jQuery.ajax({url: '<@ofbizUrl>brandsproduct</@ofbizUrl>',
         data: param,
         type: 'post',
         async: false,
         success: function(data) {
         alert(data);
          $('#brandsdetails').html(data);  
         },
        error: function(data) {
        }
    	});
    }
 </script> 


<div style=" position:relative">
<h1 style="background:#9aae27; padding:14px 5px 14px 5px; margin:2px">Brand's Search, <span class="h2">${uiLabelMap.ProductYouSearchedFor}:</span></h1>

<#if brandListDetail?has_content>
<#list brandListDetail as brandDetail>
<tr>
       <td>
        <div style="float:left; width:100px; height:100px;">
		<div style="text-align:center; margin:0 auto; ">
		<#--a href='' onclick="javascript:brandsItem('${brandDetail.get("brandName")}')">${brandDetail.get("brandName")?if_exists}</a></div-->
		<a href="<@ofbizUrl>brandsproduct?BRAND_NAME=${brandDetail.get("brandName")?if_exists}</@ofbizUrl>">${brandDetail.get("brandName")?if_exists}</a></div>
		</div>
       </td>
</tr>                   
</#list> 
</#if>
</div>