
<script>
	function favBrowser(){
		var selectedValue = document.getElementById("mySelect").value;
	    var url="<@ofbizUrl>ajaxCategoryDetail?productCategoryId=${parameters.category_id?if_exists}&category_id=${productCategoryId?if_exists}&filterBy="+selectedValue+"</@ofbizUrl>";
	    jQuery.ajax({url: url,
	        data: null,
	        type: 'post',
	        async: false,
	        success: function(data) {
	          $('#searchResult').html(data);
	          
		  },
	        error: function(data) {
	            alert("Error during product filtering");
	        }
	    });   
	}
	
	
	
</script>
<form>
	<select id="mySelect" onchange="favBrowser()">
			<option value="">All</option>
		  <option value="L_TO_H">Price Low To High</option>
		  <option value="H_TO_L">Price High To Low</option>
		  <option value="A_TO_Z">A To Z</option>
		  <option value="Z_TO_A">Z To A</option>
		   <option value="POPULAR_PRD">Popularity</option>
	</select>
</form>