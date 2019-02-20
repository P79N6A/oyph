define(function(){
	var slideArea = $(".slide-left"),
		width = slideArea.outerWidth(true),
		slideList = slideArea.children("ul"),
		slideElement = slideList.children(),
		slideWidth = slideElement.outerWidth(true),
		length = slideElement.length,
		num = Math.floor(width / slideWidth),
		timeid;
	if(length > num){
		slideArea.hover(function(){
			clearInterval(timeid);
		},function(){
			timeid = setInterval(slideLeft,2500);
		}).trigger('mouseleave');
	}
	function slideLeft(){
		var first = slideList.children().first();
		slideList.stop(true,true).animate({
			"margin-left": -slideWidth
		},800,'linear',function(){
			first.appendTo(slideList);
			slideList.css("margin-left",0);
		});
		first.stop(true,true).animate({
			"opacity": .5
		},800,'linear',function(){
			first.css('opacity',1);
		});
		slideList.children().eq(num).stop(true,true).css('opacity',.5).animate({
			"opacity": 1
		},800,'linear');
	}
});