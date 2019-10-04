$(window).load(function() {
	 insertTags = false;
	 var oFCKeditor = new FCKeditor( 'EditEmail_content' ) ;
     oFCKeditor.BasePath = '/images/jquery/fckeditor/' ;
     oFCKeditor.Height	= 400 ;
     oFCKeditor.ToolbarSet = 'OpentapsBasic';
     oFCKeditor.ReplaceTextarea() ;
   });
