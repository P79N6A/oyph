define(function(){
	var getCodeNum = 3; 	//获取短信验证码次数
	$(".getcode-btn").click(function(){
		if(!$(this).prop("disabled")){
			if(getCodeNum > 0){
				getCodeNum --;
				$(this).prop("disabled",true).next(".code-note").show();
				var getCode = $(this),
					timeid,
					i = 120; 	//再次获取验证码等待时间
				getCode.val('('+i+'s)后再次获取');
				timeid = setInterval(function(){
					if(i > 1){
						i--;
						getCode.val('('+i+'s)后再次获取');
					}else{
						getCode.prop("disabled",false);
						getCode.val('再次获取验证码');
						clearInterval(timeid);
					}
				},1000);
			}else{
				location.href="timeout.html";
				return false;
			}
		}
	});
});