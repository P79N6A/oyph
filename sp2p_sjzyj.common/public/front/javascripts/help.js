define(function(){
	$('.help-list>li').each(function(){
		var that = $(this),
			head = that.children('.help-question'),
			cont = that.children('.help-answer');
		head.on('click',function(){
			if(that.hasClass('selected')){
				cont.stop(true,true).slideUp(500,function(){
					that.removeClass('selected');
				});
			}else{
				that.addClass('selected').siblings('.selected').children('.help-question').trigger('click');
				cont.stop(true,true).slideDown(500);
			}
		})
	});
});