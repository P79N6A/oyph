define(["jquery"],function($){
	$(".back-tdcheck").click(function(){
		if($(this).prop("checked")){
			$(this).parents("tr").addClass("selected");
		}else{
			$(this).parents("tr").removeClass("selected");
		}
	});
	// 全选
	$("#chooseAll").click(function(){
		$(this).parents("table").find(".back-tdcheck").prop("checked",true).parents("tr").addClass("selected");
	});
	// 反选
	$("#chooseReverse").click(function(){
		$(this).parents("table").find(".back-tdcheck").each(function(){
			$(this).prop("checked",!$(this).prop("checked")).parents("tr").toggleClass("selected");
		});
	});
});
