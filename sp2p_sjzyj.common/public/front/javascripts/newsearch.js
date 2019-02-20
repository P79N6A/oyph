define(function(){
	$(".news-searchbtn").click(function(){
		var text = $(this).siblings(".news-searchtext");
		if(text.is(":hidden")){
			$(this).parent().addClass("onfocus");
			var width = text.width();
			text.width(0).animate({'width':width},500).focus();
			text.show();
			text.focus();
			return false;
		}
	});
});