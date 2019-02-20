define(function(){
	if($('.banner').length > 0 ){
		var ifShow = getCookie('newyear') || 1;
		if(ifShow == 1){
			/*var newyear = '<!-- 对联 --><div class="duilian-left"><div class="duilian-top"></div><div class="duilian-body"></div> <div class="duilian-btm"></div><i class="duilian-close">×</i></div><div class="duilian-right"><div class="duilian-top"></div><div class="duilian-body"></div><div class="duilian-btm"></div><i class="duilian-close">×</i></div><!-- 对联 end -->';
			$(newyear).appendTo($('body'));
			// 对联效果
			$('.duilian-left,.duilian-right').fadeIn(500,function(){
				$('.duilian-body').slideDown(2000,function(){
					$('.duilian-close').show();
				});
			});
			$('.duilian-close').click(function(){
				//写入一个cookie
				setCookie('newyear','0');
				var cookieText = encodeURIComponent('newyear')+ '=' +encodeURIComponent('0');
				$('.duilian-body').slideUp(2000,function(){
					$('.duilian-left,.duilian-right').fadeOut(500);
				});
			});*/
		}
	}
	function setCookie(name,value){
		var cookieText = encodeURIComponent(name)+ '=' +encodeURIComponent(value);
		document.cookie = cookieText;
	}
	function getCookie(name){
		var cookieName = encodeURIComponent(name) + "=",
			cookieStart = document.cookie.indexOf(cookieName),
			cookieValue = null;
		if(cookieStart > -1){
			var cookieEnd = document.cookie.indexOf(";", cookieStart);
			if(cookieEnd == -1){
				cookieEnd = document.cookie.length;
			}
			cookieValue = decodeURIComponent(document.cookie.substring(cookieStart + cookieName.length, cookieEnd));
		}
		return cookieValue;
	}
});