//首页头部信息编辑
define(['jquery'],function($){
	function saveTextSub(btn, text, ajaxPath, ajaxData,jsTest){
		if(jsTest){
			var jsTestEnd = jsTest();
			if(!jsTestEnd){
				return false;
			}
			
		}
		//点击按钮编辑保存
		$.ajax({
			url : ajaxPath,
			type : "POST",
			data : ajaxData,
			dataType:"json",
			success : function(data) {
				var flag = interceptorCheck(data);
				if(!flag){
					return false;
				}
				if(data.code<1){
					alert(data.msg,function(){
						text.children(".edit-input").select();
					});
				}else {
					weakDialog(data.msg);
					saveTextOver(btn,text);
				}
			}
		});
	}
	
	//切换为编辑状态
	function textBeginEdit(btn,text){
		var word= text.text(),
			width= text.width(),
			height= text.height();
		var input= parseInt(height)< 45 ? $("<input type='text' class='edit-input' maxlength="+ (text.attr('maxlength')||'') +" />") : $("<textarea class='edit-input' maxlength="+ (text.attr('maxlength')||'') +"></textarea>");
		text.text("");
		input.val(word);
		input.width(width);
		input.height(parseInt(height)< 45 ? 'auto' : height);
		input.appendTo(text).select();
		btn.html("&#xe630;").addClass("save-btn").data("title","保存");
		$(".titlebox").text("保存");
	}
	
	//切换为保存状态
	function saveTextOver(btn,text){
		var input = text.children(".edit-input"),
		newWord = input.val();
		input.val("").remove();
		text.text(newWord);
		btn.html("&#xe602;").removeClass("save-btn").data("title","编辑");
		$(".titlebox").text("编辑");
	}
	
	
	return function(btn, text, ajaxPath, ajaxData,jsTest){
			if(!btn.hasClass("save-btn")){
				textBeginEdit(btn,text);
			}else{
				if(ajaxPath){
					saveTextSub(btn, text, ajaxPath, ajaxData,jsTest);
				}else{
					saveTextOver(btn,text);
				}
			}
		}
});

