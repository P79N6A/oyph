//选项卡
define(["jquery"],function($){
	$("body").on('click','.slide-menu',function(){
		var cont= $(this).siblings(".slide-cont"),
			arrow = $(this).find(".slide-arrow");
		if(cont.is(":hidden")){
			cont.stop().slideDown(300);
			arrow.html("&#xe603;");
		}else{
			cont.stop().slideUp(300);
			arrow.html("&#xe606;");
		}
	});
	$("body").on('click','.back-upbtn',function(event){
		event.stopPropagation();
	});
});
