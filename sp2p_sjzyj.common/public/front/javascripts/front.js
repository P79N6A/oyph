require.config({
	paths: {
		"jquery": "lib/jquery", //jquery包
		"skitter": "jquery.skitter.min",
		"easing": "jquery.easing.1.3",
		"fancyBox": "fancyBox/jquery.fancybox",
		"thumbs": "fancyBox/helpers/jquery.fancybox-thumbs",
		"lazyload": "jquery.lazyload.min",
		"validate": "validation/jquery.validate", /*表单验证插件*/
		"messages": "validation/messages_zh", /*表单验证插件错误提示信息*/
		"qrcode":"jquery.qrcode" //二维码api
	},
	
	shim: {
		easing: {
			deps: ['jquery']
		},
		skitter: {
			deps: ['easing'],
			exports: 'skitter'
		},
		fancyBox: {
			deps: ["jquery"],
			exports: "window,document,jQuery"
		},
		thumbs: {
			deps: ["jquery","fancyBox"],
			exports: "window,document,jQuery"
		},
		lazyload: {
			deps: ['jquery'],
			exports: 'jQuery,window'
		},
		qrcode: {
			deps: ['jquery'],
			exports: 'jQuery'
		}
	}
});
define(['domReady','jquery','lazyload','dialog','validate','messages'],function(doc,$){
	
	// 对联效果
	/*$('.duilian-left,.duilian-right').fadeIn(500,function(){
		$('.duilian-body').slideDown(2000,function(){
			$('.duilian-close').show();
		});
	});
	$('.duilian-close').click(function(){
		$('.duilian-body').slideUp(2000,function(){
			$('.duilian-left,.duilian-right').fadeOut(500);
		});
	});*/
	//右侧悬浮框效果
	$(function(){
		var goTop = $(".goto-top"),
			goEnd = $(".goto-end");
		$(window).scroll(function(){
			var height = $("body").height()-$(window).height();
			var scrollTop = $(window).scrollTop();
			if(scrollTop > 30){
				goTop.show();
			}else{
				goTop.hide();
			}
			if(scrollTop < height){
				goEnd.show();
			}else{
				goEnd.hide();
			}
		});
		goTop.click(function(){
			$("html,body").animate({scrollTop: 0},500);
		});
			//跳到底部
		goEnd.click(function(){
			var height = $("body").height()-$(window).height();
			$("html,body").animate({scrollTop: height});
		});
	});
	//浮动导航
	var navBox = $(".navbox-innerbox");
	$(window).scroll(function(){
		var scrollTop = $(window).scrollTop();
		if(scrollTop > 180){
			if(!navBox.hasClass("fixed")){
				var height = navBox.height();
				navBox.parent().height(height);
				navBox.addClass("fixed").hide().slideDown(300);
				$(".top").removeClass("tops");
				$(".navbox").removeClass("navboxs");
				$(".navbox-innerbox").removeClass("navbox-innerboxs");
				$(".header").removeClass("headers");
				$(".header").removeAttr("onmouseover");
				$(".header").removeAttr("onmouseout");
			}
		}else{
			navBox.removeClass("fixed");
			if($("#home-page").val()==1){
				$(".header").addClass("headers");
				$(".top").addClass("tops");
				$(".navbox").addClass("navboxs");
				$(".navbox-innerbox").addClass("navbox-innerboxs");
			}else{
				$(".header").removeAttr("onmouseover");
				$(".header").removeAttr("onmouseout");
			}
			/*$(".header").attr("onmouseover","changColor()");
			$(".header").attr("onmouseout","changColors()");*/
		}
		
	}).trigger('scroll');
	//选项卡
	var tabcard = function(menu,cont){
			cont.hide().eq(0).show();
			menu.click(function(){
				var index = $(this).index();
				$(this).addClass('selected').siblings().removeClass('selected');
				cont.hide().eq(index).show();
			});
		}
	tabcard($('.tab-menu').children(),$('.tab-cont'));
	// title模拟
	var titleDialog = $('<div class="title-dialog major"><i></i><i></i><span></span></div>');
	$('body').on('mouseover','[data-title]',function(event){
		var title = $(this).data('title');
		titleDialog.find('span').text(title);
		titleDialog.appendTo($('body'));
		titleDialog.offset({
			'top': $(this).offset().top+$(this).height()+10,
			'left': $(this).offset().left -titleDialog.width()/2
		});
	}).on('mouseleave','[data-title]',function(){
		titleDialog.remove();
	});
	//模拟placeholder
	if(! ('placeholder' in document.createElement('input'))){
		$('[placeholder]').each(function(){
			var self = $(this),
				label = $('<label class="placeholder"></label>'),
				paddingLeft = self.css('padding-left');
			self.wrap('<span class="input-wrap"></span>');
			label.text(self.attr('placeholder'));
			label.insertAfter(self);
			label.css({
				'top': '50%',
				'line-height': self.height() +'px',
				'margin-top': -self.height()/2,
				'left': paddingLeft
			})
			label.click(function(){
				self.focus();
			});
			self.focus(function(){
				label.hide();
			}).blur(function(){
				if(self.val() == ''){
					label.show();
				}
			});
		});
	}
	//图片轮播切换
	function fadeBanner(fadeBox){
		var fadeImg = fadeBox.find('.fade-img'),
			len = fadeImg.length,
			fadeHandle = $('<div class="fade-handle none"></div>'),
			nowIndex = 0;
		if(len > 1){
			for(var i = 0; i < len; i++){
				$('<i></i>').appendTo(fadeHandle);
			}
			fadeHandle.appendTo(fadeBox);
			fadeImg.hide().eq(0).show();
			fadeHandle.children().eq(0).addClass('selected');
			fadeHandle.children().click(function(){
				if(!$(this).hasClass('selected')){
					var index = $(this).index();
					fadeImg.eq(index).stop(true,true).fadeIn(500).siblings('.fade-img:visible').stop(true,true).fadeOut(500);
					$(this).addClass('selected').siblings().removeClass('selected');
					nowIndex = index;
				}
			});
			var timeid = null;
			fadeBox.hover(function(){
				clearInterval(timeid);
			},function(){
				timeid = setInterval(function(){
					nowIndex++;
					fadeHandle.children().eq(nowIndex < len ? nowIndex : 0).trigger('click');
				},4000);
			}).trigger('mouseleave');
		}

	}
	fadeBanner($('.fade-banner'));
	
	//浮动侧边栏
	function floatSidebar(obj){
		var parent = obj.parent(),
			offtop = obj.offset().top,
			offLeft = obj.offset().left,
			parentOfftop = parent.offset().top,
			parentOffLeft = parent.offset().left,
			height = obj.outerHeight(),
			parentHeight = parent.outerHeight();
		if(obj.parent().outerHeight() > obj.height() + offtop - parentOfftop){
			var positionLeft = obj.offset().left - parent.offset().left;
			$(window).scroll(function(){
				var scrollTop = $(window).scrollTop();
				if(scrollTop > offtop){
					parent.css('position','relative');
					obj.css({
						'position': 'absolute',
						'top': scrollTop - offtop,
						'left': positionLeft
					});
					if(scrollTop  + height >= parentHeight + parentOfftop){
						obj.css({
							'top': parentHeight - height
						});
					}
				}else{
					obj.css({
						'position': 'static'
					});
				}
			}).trigger('scroll');
		}
	}
	if($('.news-sidebar').length > 0){
		floatSidebar($('.news-sidebar'));
	}
	/**
	 * 是否为空(不为空返回null)
	 * 
	 * @returns
	 */
	String.prototype.isNotBlank = function(){
		var blank = /^\s*$/;
		return  (!blank.test(this));
	}
	/**
	 * 字符串长度是否符合要求
	 * 
	 * @returns
	 */
	String.prototype.isLess = function(minNum){
		var flag = (this.length < minNum);
		return flag;
	}
	String.prototype.isMore = function(maxNum){
		var flag = (this.length > maxNum);
		return flag;
	}
	
	
	String.prototype.isBetween = function(min,max){
	
		return (this.isNotBlank() && (!this.isLess(min)) && (!this.isMore(max)));
	}
	
	/**
	 * 是否有效的手机号码
	 * 
	 * @returns
	 */
	String.prototype.isMobileNum = function() {
		return (new RegExp(/^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\d{8}$/).test(this));
	}
	
	/**
	 * 是否有效的密码
	 * 
	 */
	String.prototype.isPassWord = function() {
		
		return (new RegExp(/^([0-9a-zA-Z]{6,15})$/).test(this));
	}
	
	/**
	 * 是否有效的密码
	 * 
	 */
	String.prototype.isNumber = function() {
		
		return (new RegExp(/^([0-9]{1,2})$/).test(this));
	}
	
	/**
	 * 是否是整数倍
	 * 
	 */
	String.prototype.isMultiple = function(num){
		return ((this.isBlank()) && (this%num == 0));
	}
	
	String.prototype.isPositInt = function(){
		var res = /^[1-9]+[0-9]*]*$/;
		var vl = this;
		var flag = res.test(vl);
		return flag;
	}
	
	String.prototype.isNumLess = function(min){
		if(!this.isNotBlank()){
			return false;
		}
		var fl = parseFloat(this);
		if(fl < min ){
			return true;
		}
		return false;
	}
	String.prototype.isNumMore = function(max){
		if(!this.isNotBlank()){
			return false;
		}
		var fl = parseFloat(this);
		if(fl > max ){
			return true;
		}
		return false;
	}
	
	/** 字符串是否是属于该区间的数字【包含两个区间】 */
	String.prototype.isNumBetween = function(min,max){
		return (this.isNotBlank() && (!this.isNumLess(min)) && (!this.isNumMore(max)))
	}
	
	/** 一位小数或者非负整数 */
	String.prototype.isFloat = function() {
		var res = new RegExp(/^(\d+\.\d{1,1}|\d+)$/);
		return res.test(this);
	}
	/**
	 * 是否为汉字
	 * @returns
	 */
	String.prototype.isChinese = function() {
		return (new RegExp("[\\u4E00-\\u9FFF]+","g")
				.test(this));
	}
	/**
	 * 是否有效的邮箱
	 * 
	 * @returns
	 */
	String.prototype.isEmail = function() {
		return (
				new RegExp(/^([a-zA-Z0-9])+([a-zA-Z0-9_.-])+@([a-zA-Z0-9_-])+((\.([a-zA-Z0-9_-]){2,3}){1,2})$/).test(this)
			   );
	}
	/**
	 * 是否是QQ邮箱
	 */
	String.prototype.isQQEmail = function(){
		return new RegExp(/^([\s\S]*@qq.com)$/).test(this);
	}
	
	String.prototype.isQQ = function(){
		return new RegExp(/^\d{6,10}$/).test(this);
	}
	
	//判断元素值是是否是日期类型
	String.prototype.isDate = function() {
		return (new RegExp(
				/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig)
				.test(this));
	}
	
	//判断两个表单元素的值是否相等
	$.fn.isEqual = function(another){
		return ($(this).val() === another.val());
	}
	
	//截取字数：
	$.fn.limit=function(){   
		var self = $("[limit]");
		  self.each(function(){
		       var objString = $(this).text();
		       var objLength = $(this).text().length;
		       var num = $(this).attr("limit");
		   if(objLength > num){           
			   $(this).text(objString.substring(0,num) + "...");
		       }else{
		    	   $(this).text(objString);
		       }
		  })
	}
	
	//文本域换行显示
	$.fn.newline=function(){   
		var self = $(".newline");
		self.each(function(){
			var element = $(this);
			var temp = element.text().trim().replace(/\n/g, '<br>');
			$(this).html(temp);
		})
	}
	
	//处理ajax请求被拦截的情况
	window.interceptorCheck = function(result) {
		if(result.code == -100){//没有登录
			top.location.href="/loginAndRegiste/login.html";
			return false;
		}
		if(result.code == -102){//未开户
			top.location.href="/account/paymentAccountGuide.html";
			return false;
		}
		if(result.code == -103){//未实名认证
			top.location.href="/account/realNameGuide.html";
			return false;
		}if(result.code == -106){//模拟登录
			top.location.href="/account/simulatedLogin.html";
			return false;
		}
		return true;
	}

	//正则范围：0到9的正整数,字母大小写。
	$.fn.integerAndLetters=function(){
		  var reg = new RegExp('^([0-9a-zA-Z]{0,20}).*');
		  $(this).val($(this).val().replace(/[^0-9a-zA-Z]/g,'').replace(reg,'$1'));
	}
	//正则范围：0.0-99.9或者100，可输入一位小数
	$.fn.proportionRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g, '').replace(/^\.+/, '').replace(/^(100|\d{1,2}(\.\d{0,1})?).*/,'$1'));
	}
	//正则范围：0.0-9.9或者10，可输入一位小数，10的时候不可在输入
	$.fn.discountRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g, '').replace(/^\.+/, '').replace(/^(10|\d{0,1}(\.\d{0,1})?).*/,'$1'));
	}
	//正则范围：1-12，正整数
	$.fn.oneToTwelfth=function(){
		$(this).val($(this).val().replace(/[^\d]/g,'').replace(/(^12|11|10|(\d{0,1})).*/, '$1'));
	}
	//正则范围：0-10，正整数
	$.fn.zeroToTen=function(){
		$(this).val($(this).val().replace(/[^\d]/g,'').replace(/(^10|(\d{0,1})).*/, '$1'));
	}
	//正则范围：0.00-9999999.99,可输入2位小数，7位整数位
	$.fn.withdrawRange=function(){
	    $(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(\d{1,7}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0.00-20位整数,可输入2位小数，
	$.fn.maxRange=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(\d{1,18}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0.00-99.99或者100, 可输入2位小数，100的时候不可以输入小数位
	$.fn.percentage=function(){
		$(this).val($(this).val().replace(/[^\d\.]/g,'').replace(/^\.+/,'').replace(/^(100|\d{1,2}(\.\d{0,2})?).*/,'$1'));
	}
	//正则范围：0到n位的最大值，正整数。n表示最大位数
	$.fn.ndigitInteger=function(n){
		  var reg = new RegExp('^(\\d\{0,'+n+'\}).*');
		  $(this).val($(this).val().replace(/[^\d]/g,'').replace(reg,'$1'));
	}
	
	return {
		// 为当前页面的导航添加选中后的样式
		navSelected: function(index){
			$(".nav>li").eq(index).children("a").addClass("selected");
		},
		tabcard: tabcard,
		treven: function(table){
			table.find("tr:even").addClass("treven");
		},
		secondNav: function(index){
			$(".second-nav>li").eq(index).children("a").addClass("selected");
		}
	}
	
});