define(['jquery'],function($){
	var isShow = false,
		side = $(".back-sidecont"),
		width = side.width(),
		sidebtn = $(".back-sidebtn"),
		btnwidth = sidebtn.width(),
		container = $(".back-container"),
		speed = 200;
	//左侧筛选条件展开关闭
	sidebtn.click(function(){
		if(isShow){
			$(this).html("&#xe605;").animate({"right":-btnwidth},speed,"linear");
			side.animate({"width": 0},speed,"linear",function(){
				side.css("border-width",0);
			});
			container.animate({"padding-left": 20},speed,"linear");
		}else{
			$(this).html("&#xe607;").animate({"right":0},speed,"linear");
			side.css("border-width","1px").animate({"width": width},speed,"linear");
			container.animate({"padding-left": 190},speed,"linear");
		}
		isShow = !isShow;
	});
	//左侧筛选条件选中效果切换
	$(".back-sidenav a").click(function(){
		$(this).addClass("selected").parent().siblings().children("a").removeClass("selected");
	});
	$(".back-usermenu a").click(function(){
		$(this).addClass("selected").siblings().removeClass("selected");
	});
});
