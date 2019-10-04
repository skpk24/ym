<style>
#locationSearch{width:150px !important; padding:4px;}
</style>

<script type="text/javascript">
            function loadProducts(){
                var productSelected= $("#locationSearch").val();
              
                var productList = "";
                var  param = 'sport=' + productSelected;
               
                jQuery.ajax({url: "/control/autoproductname1",
		         data: param,
		         type: 'post',
		         async: false,
		         success: function(products) {
		          productList = products.split(',');
		          
		         
		         }
		    	});
                //Returns the javascript array of sports products for the selected sport.
                return productList;
            	}
            	
            	
             function autocompleteProducts(){
         
                var productss = loadProducts();
                
              
                $("#locationSearch").autocomplete({
                     source: productss,
                 
                 });
             }
        </script>



        <form name="advancedsearchform" method="post" action="<@ofbizUrl>locationsearchAction</@ofbizUrl>">
     			<!-- old code<input type="hidden" name="VIEW_SIZE" value="10"/>
     			
  				<input type="hidden" name="PAGING" value="Y"/>
  				
     		
  					
	     			<div id='productselect' class="input-field"><input type="text" name="locationSearch" id="locationSearch" class="main-search" onKeyup="autocompleteProducts()" onblur="if (this.value == '') { this.value = 'Search for Items'; }" onfocus="if (this.value == 'Search for Items') {this.value = ''; }" size="40"  value="${locationSearch?if_exists}"/><input type="image" src="/erptheme1/searchicon.png"  /></div>-->
	     			
	     <table cellspacing="0" align="center" style="margin:0 auto; width:308px; padding-bottom:20px;">
		     <tr><td colspan="2" style="text-align:center"><h3>Location Search</h3> </td></tr>
			 <tr><td colspan="2">&nbsp;</td></tr>
			 <tr>
			     <td class="label" style="padding-top:3px;"><span style="font-weight:normal; font-size:14px !important;">PinCode Number</span></td>
				
				 <td><div id='productselect' class="input-field">
						 <table cellspacing="0" align="center" style="background:#d1d3ab; border-radius:4px; -moz-border-radius:4px; -webkit-border-radius:4px; border:1px solid #b4b46c;">
							<tr>
								<td>
						           <input type="text" name="locationSearch" id="locationSearch" style="border:none !important;" class="main-search1" />
						        </td>
						        <td>
						          <input type="image" style="background:#d1d3ab;" src="/erptheme1/searchicon.png"  onclick="return checknumber()"/>
						        </td>
						    </tr>
						  </table>
				    </div>
				</td>
			 </tr>
	     </table>   
	     			
	     		<div id="testAreaHidden"></div>
	     		
     </form>
	<#if flag?has_content>
	<#if flag.contains("true")>
     		<p style="color:red;">WoW! Happy to serve you. Enjoy Shopping with YouMart.</p>
     	
     		<#else><p style="color:red; width:500px; margin:0 auto;">
     		Sincerely apologize as we have not commenced our services in your area. Request you to leave your email address for us to notify you once we are right up there in your area to serve you.</p>
    <form name="storeEmailform" method="post" action="<@ofbizUrl>storeEmaillocation</@ofbizUrl>">
     		
     <table cellspacing="0" align="center" style="margin:0 auto; width:276px; padding-bottom:20px;">
	  
		 <tr><td colspan="2">&nbsp;</td></tr>
		     <input type="hidden" name="pinCode" value="${(location?if_exists)}"/>
		 <tr>
		    <td width="100px"><span class="label">Email Id</span></td>
			<td><input type="text" name="emailId" id="emailId" /></td>
		 </tr>
		 <tr>
		    <td >&nbsp;</td>
			<td><input type="submit" value="submit" onclick="return addInput()"></td>
		 </tr>
		 
	 </table>   
     		
     		
     		
     		
    </form>
     		
     		
	</#if>
	</#if>
     		 <script type="text/javascript" language="JavaScript">
       function addInput() 
             {
            
              if(document.getElementById("emailId").value=="" ||document.getElementById("emailId").value==null)
              {
              alert("Please Enter the Email Id");
              return false;
              }
             }   
             
           function checknumber()
           {
          
           if(isNaN(document.getElementById("locationSearch").value))
           {
           alert("please enter Valid Pincode");
                return false;
           }
           return true;
           
           }
            
      </script>
     		