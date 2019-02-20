//搜索框点击展开收缩
define(['jquery'],function($){
	var search = function(searchbtn,searchbox){
		searchbtn.click(function(){
			if($(this).hasClass("selected")){
				$(this).removeClass("selected");
				searchbox.slideUp(200);
			}else{
				$(this).addClass("selected");
				searchbox.slideDown(200);
				searchbox.find("input").eq(0).focus();
			}
		});
	}
	search($(".back-search"),$(".back-search-option"));
	return search;
});
