//设置编辑
define(['jquery'],function($){
	return function(editBtn,editText,that,callback){
		//点击按钮编辑保存
		editText.prop("disabled",true);
		editBtn.click(function(){
			disable = editText.prop("disabled");
			alert(disable);
			if(disable){
				//进入编辑
				editBtn.html("&#xe630;").data("title","保存");
				$(".titlebox").text(editBtn.data('title'));
				editText.prop("disabled",!disable);
				editText.eq(0).focus();
			} else {
				if(callback){//有回调函数，使用回调函数处理
					callback();
				} else {//没有回调函数，使用默认处理
					editBtn.html("&#xe602;").data("title","编辑");
					$(".titlebox").text(editBtn.data('title'));
					editText.prop("disabled",true);
				}
			}
		});
		
	}
});

//保存成功
function saveTextSuccess(editText,editBtn,disable){
	
}
