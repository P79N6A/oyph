//选项卡
define(["jquery"],function($){
	return function(menu,cont){
		menu.eq(0).addClass("selected").siblings().removeClass("selected");
		cont.hide().eq(0).show();
		menu.click(function(){
			var index = menu.index(this);
			$(this).addClass("selected").siblings().removeClass("selected");
			cont.hide().eq(index).show();
		});
	}
});
