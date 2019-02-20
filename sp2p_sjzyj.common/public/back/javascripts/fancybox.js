define(["require","jquery","fancyBox","thumbs"],function(require,$){
	var path01 = require.toUrl("fancyBox/../jquery.fancybox.css"),
		style01 = $("<link type='text/css' rel='stylesheet' href="+ path01 +" />");
	style01.appendTo($("head"));
	$(".back-skin-preview").click(function(){
		$(this).siblings(".fancybox-container").children(".fancybox").trigger("click");
	});
	$('.fancybox').fancybox();
});
