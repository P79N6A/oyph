define(['skitter'],function(skitter){
	var newCSS = $("<link type='text/css' rel='stylesheet' href='/public/front/stylesheets/skitter.styles.css'/>");
	newCSS.appendTo($("head"));
	$('.box_skitter').css({
		'width': 1920,
		'height': 400
	}).skitter({
        numbers_align: 'center',
        preview: true,
        interval: 5000
	});
});