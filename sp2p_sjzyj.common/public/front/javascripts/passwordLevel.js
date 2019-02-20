define(function(){
	//密码强度
    var $degree = $("#passwordLevel");
    $("#password").bind("input propertychange",function(){
    	if($degree.is(":hidden")){
        	$degree.show();
        }
        var strongRegex = new RegExp("^([0-9a-zA-Z]{6,})$", "g");
        var mediumRegex = new RegExp("^([a-z0-9]{6,}|[a-zA-Z]{6,}|[0-9A-Z]{6,})$", "g");
        var enoughRegex = new RegExp("^([a-z]{6,}|[A-Z]{6,}|[0-9]{6,})$", "g");
        if(enoughRegex.test($(this).val())){
        	$degree.find("span").eq(0).addClass('enough').siblings().removeClass("strong medium");
        }else if(mediumRegex.test($(this).val())){
        	$degree.find("span").eq(0).addClass('enough').next().addClass('medium').siblings().removeClass("strong");
        }else if(strongRegex.test($(this).val())){
        	$degree.find("span").eq(0).addClass('enough').next().addClass('medium').next().addClass("strong");
        }else{
        	$degree.find("span").removeClass("strong medium enough");
        }
        return true;
    });
});