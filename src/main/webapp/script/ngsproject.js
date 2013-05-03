/** http://www.thesitewizard.com/javascripts/change-style-sheets.shtml */
"use strict";


var NGSProject = {
	"cookie_name" : "ngsstyle" ,
	"cookie_duration" :  30 ,
	"switch_style" : function( css_title )
		{
		
		// You may use this script on your site free of charge provided
		// you do not remove this notice or the URL below. Script from
		// http://www.thesitewizard.com/javascripts/change-style-sheets.shtml
		  var i,link,link_tags = document.getElementsByTagName("link");
		  
		  for( i in link_tags  )
		    	{
		    	link=link_tags[i];
		    	if(!link.rel) continue; 
			    if(link.rel.indexOf( "stylesheet" )== -1)  continue; 
			    
			    if(!link.title)  continue; 
			    link.disabled =(link.title != css_title) ;
			    }
			 
		  this.set_cookie( 
		    this.cookie_name,
		  	css_title,
		  	this.duration
		  	);
		  
		},
	"set_style_from_cookie" : function()
		{
	  	var css_title = this.get_cookie( this.cookie_name );
	    if (css_title.length) this.switch_style( css_title );
	    },
	"set_cookie" : function( cookie_name, cookie_value, lifespan_in_days, valid_domain )
		{
		    // http://www.thesitewizard.com/javascripts/cookies.shtml
	    var domain_string = valid_domain ?
	                       ("; domain=" + valid_domain) : '' ;
	    document.cookie = cookie_name +
	                       "=" + encodeURIComponent( cookie_value ) +
	                       "; max-age=" + 60 * 60 *
	                       24 * lifespan_in_days +
	                       "; path=/" + domain_string ;
		},
	"get_cookie" : function( cookie_name )
		{
	    // http://www.thesitewizard.com/javascripts/cookies.shtml
	    var cookie_string = document.cookie ;
	    if (cookie_string.length != 0)
	    	{
	    	var cookie_value = cookie_string.match (
	                        '(^|;)[\s]*' +
	                        cookie_name +
	                        '=([^;]*)' );
		     return decodeURIComponent ( cookie_value[2] ) ;
		   	 }
	    return '' ;
		}
	};
