//选项卡
define(["jquery"],function($){
	var blank = /^\s*$/;
	$("#pageSave").click(function(event){
		var required = $(".required");
		for(var i= required.length-1; i>= 0; i--){
			var cont = required.eq(i).val(),
				min = required.eq(i).attr("minlength") || 0,
				max = required.eq(i).attr("maxlength") || Infinity;
			var note = $("<span class='back-notice'></span>"),
				position = required.eq(i).closest(".liright,.back-finace-suggest").find(".back-text-limit");
			if(blank.test(cont)){
				note.text("内容不能为空");
				if(position.prev(".back-notice").length === 0){
					note.insertBefore(position);
				}
				required.eq(i).addClass("back-error");
				required.eq(i).focus();
				event.preventDefault();
			}else if(cont.length < min || cont.length > max){
				note.text("请按要求填写字符个数");
				if(position.prev(".back-notice").length === 0){
					note.insertBefore(position);
				}
				required.eq(i).addClass("back-error");
				required.eq(i).focus();
				event.preventDefault();
			}else if(required.eq(i).is("#borrowmoney")){
				if(required.eq(i).val() % 1000 !== 0){
					note.text("输入金额有误");
					if(position.prev(".back-notice").length === 0){
						note.insertBefore(position);
					}
					required.eq(i).addClass("back-error");
					required.eq(i).focus();
					event.preventDefault();
				}
			}
		}
		var imgrequired = $(".imgrequired");
		for(var i= imgrequired.length-1; i>= 0; i--){
			if(imgrequired.find("img").length == 0){
				var note = $("<span class='back-notice'></span>"),
					container = imgrequired.eq(i).siblings().find(".back-imginfor-note");
				note.text("没有上传图片");
				if(container.children(".back-notice").length == 0){
					note.prependTo(container);
				}
				event.preventDefault();
			}
		}
	});
	$(".required").on("keyup",function(){
		var cont = $(this).val() || $(this).text(),
			min = $(this).attr("minlength") || 0,
			max = $(this).attr("maxlength") || Infinity;
		if(cont.length >= min && cont.length <= max){
			$(this).removeClass("back-error").closest(".liright,.back-finace-suggest").find(".back-notice").remove();
		}
	});
	$(".back-upbtn,.back-lib-upbtn").click(function(){
		$(this).siblings(".back-imginfor-note").children(".back-notice").remove();
	});
});
