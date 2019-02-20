//排序
define(['jquery'],function($){
	var orders = $(".back-order");
	orders.click(function(){
		orders.not(this).removeClass("orderdown orderup");
		if($(this).hasClass("orderup")){
			$(this).removeClass("orderup").addClass("orderdown");
		}else if($(this).hasClass("orderdown")){
			$(this).removeClass("orderdown").addClass("orderup");
		}else{
			$(this).addClass("orderdown");
		}
	});
});
