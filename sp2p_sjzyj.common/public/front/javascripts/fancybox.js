define(["require","fancyBox","thumbs"],function(require){
	var path01 = require.toUrl("fancyBox/../jquery.fancybox.css"),
		style01 = $("<link type='text/css' rel='stylesheet' href="+ path01 +" />");
	style01.appendTo($("head"));
	$("body").on('click','.previewbtn',function(){
		$(this).siblings(".fancybox-container").children(".fancybox").eq(0).trigger("click");
	});
	$('.fancybox').fancybox({
		'type': 'image'
	});
});
