define(['qrcode'],function(){
	
	//微信分享
	$('body').on('mouseover','.shareto-wechat',function(){
		if('placeholder' in document.createElement('input')){
			if($(this).find('canvas').length == 0){
				$(this).find('.code-area').qrcode({
					//render:'table',
					foreground: "#F5F5DC",
					background: "black",
					width:100,
					height:100,
					text: $(this).find('.code-url').val()
				});
			}
		}else{
			if($(this).find('table').length == 0){
				$(this).find('.code-area').qrcode({
					render:'table',
					width:100,
					height:100,
					text: $(this).find('.code-url').val()
				});
			}
		}
	});
		
	//qq空间分享
	window.qqZoneShare = function(sign,title,image){
		var pic;
		if(typeof(image) != "undefined"){
			pic=window.location.protocol+"//"+window.location.host+image;
		}
		var p = {
			url:sign,
			showcount:1,
			desc:'',
			summary:'',
			title:title,
			site:'',
			pics:pic,
			style:'203',
			width:98,
			height:22
		};
		var s = [];
		for(var i in p){
			s.push(i + '=' + encodeURIComponent(p[i]||''));
		}
		window.open("http://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?"+s.join('&'));
	}
	
	//新浪微博分享
	window.sinaShare = function(sign,title,image){
		var pics;
		if(typeof(image) != "undefined"){
			pics=window.location.protocol+"//"+window.location.host+image;
		}
		var p = {
			url:sign,
			title:title,
			content:'utf-8',
			sourceUrl:sign,
			pic:pics
		};
		var s = [];
		for(var i in p){
			s.push(i + '=' + encodeURIComponent(p[i]||''));
		}
		window.open("http://v.t.sina.com.cn/share/share.php?"+s.join('&'));
	}
	
	//赞一下
	$('body').on('click','.praise-btn',function(){
		if(!$(this).hasClass('have-praised')){
			var inforId = $(this).children('input').val(),
				inforIndex = $(this).children('.praise-count');
			$.get('/information/supportcount.html',{id:inforId},function(data){
				if(data.code>0){
					inforIndex.html(data.obj).parent('a').addClass('have-praised').addClass('have-clicked');
				}else{
					alert(data.msg);
				}
				
			});
		}
	});
	
});