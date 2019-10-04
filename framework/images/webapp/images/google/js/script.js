

$(document).click(function(event) {
	var target = $(event.target);
	  if (target.parents('#searchresults').length == 0) {
		  $('#suggestions').fadeOut();
		  }
	 
});
google.setOnLoadCallback(function()
{
	
	var cssObj = { 'box-shadow' : '#888 5px 10px 10px', // Added when CSS3 is standard
		'-webkit-box-shadow' : '#888 5px 10px 10px', // Safari
		'-moz-box-shadow' : '#888 5px 10px 10px'}; // Firefox 3.5+
	$("#suggestions").css(cssObj);
	 
});

var t;    

function autocompleteProducts()
{
  if ( t )
  {
    clearTimeout( t );
    t = setTimeout( autocompleteProducts_one_sec, 1000 );
  }
  else
  {
    t = setTimeout( autocompleteProducts_one_sec, 1000 );
  }
}
function autocompleteProducts_one_sec() {
	var inputString = $("#SEARCH_STRING").val();
	if(inputString.length <= 2) {
		$('#suggestions').fadeOut(); // Hide the suggestions box
	} else {
		var  param = 'sport=' + inputString;
        jQuery.ajax({url: "/control/autoproductname",
         data: param,
         type: 'post',
         async: true,
         success: function(data) {
       
         $('#suggestions').fadeIn(); // Show the suggestions box
		 $('#suggestions').html(data); // Fill the suggestions box
         }
    	});
	}
}