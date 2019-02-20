//选项卡
define(["jquery"],function($){
	var notice_error = $("#notice_error"),
		doc = document,
		flag_username = false,
		flag_userpass = false,
		flag_code = false;
	function checkUserName(){
		var username = $("#userName");
		if(!username.val().isBetween(3,15)){
			flag_username = false;
			username.addClass("back-error");
		} else {
			notice_error.html("");
			$(".back-login-note").hide();
			flag_username = true;
			username.removeClass("back-error");
		}
	}
	function checkPassword(){
		var userPassword = $("#userPassword");
		if(!userPassword.val().isBetween(6,15)){
			flag_userpass = false;
			userPassword.addClass("back-error");
		} else {
			notice_error.html("");
			$(".back-login-note").hide();
			flag_userpass = true;
			userPassword.removeClass("back-error");
		}
	}
	function checkCode(){
		var randomID2 = $("#imgCode");
		if(!randomID2.val().isBetween(1,2)){
			flag_code = false;
			randomID2.addClass("back-error");
		} else {
			if(!randomID2.val().isNumBetween(0,18)){
				flag_code = false;
				randomID2.addClass("back-error");
			} else {
				$.ajax({
					url : "/common/captcha/checkcaptcha2.html",
					type : "POST",
					dataType:"json",
					async:false,
					data:{
						"code":$("form input[name='captcha']").val(),
						"randomID":$("form input[name='randomID']").val()
					},
					success : function(result) {
						var arr=eval(result);
						if(arr){
							flag_code = true;
							randomID2.removeClass("back-error");
						}else{
							flag_code = false;
							randomID2.addClass("back-error");
						}
					},
					error : function() {
						alert("对不起，系统出现错误!");
					}
				});
			}
		}
	}

	$("#userName").on('input propertychange',function(){
		$(this).integerAndLetters();
		checkUserName();
	}).on('blur',function(){
		checkUserName();
	});
	$("#userPassword").on('input propertychange',function(){
		$(this).integerAndLetters();
			checkPassword();
	}).on('blur',function(){
		checkPassword();
	});
	$("#imgCode").on('input propertychange',function(){
			checkCode();
	}).on('blur',function(){
		checkCode();
	});
	
});
