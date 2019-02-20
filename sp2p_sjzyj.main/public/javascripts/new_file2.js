
	
/* 安全时间  start*/
var year = 0;
var day  = 0;
function ShowDate(){
	var NowTime = Date.parse(new Date());
	var oldTime = Date.parse("2013/12/06");/* 2013-12-06 修改为 2013/12/06*/
	year = parseInt( (NowTime-oldTime)/(1000*3600*24*365) );
	day =  parseInt( (NowTime-oldTime)/(1000*3600*24) )%365;
	
//return "<span>"+year+"</span>年"+"<span>"+day+"</span>天";
	return  year*1000 + day
};  
/* 安全时间  end*/

window.onload= function(){
//	var URL = "www.weloan.com/"

	$.ajax({
		type: "GET",
		url: "api/app/platformData",
		data: {token:"d9d2a4bc1bc6305ed7e0925db3be5ff2"},
		dataType: "json",
		async:true,
		success: function(success){
//			console.log(success)
			var DealAmount = success.totalInvestDealAmount.toFixed(2);
			
			$("#DealAmount").numRolling({
			    num: DealAmount,
			    callback: showTotalNum,
				unit:"元",
				Date:false
			});
			var CorrectAmount = success.totalInvestCorrectAmount.toFixed(2);
			$("#CorrectAmount").numRolling({
			    num: CorrectAmount,
			    callback: showTotalNum,
				unit:"元",
				Date:false
			});
			
			var countUsers = success.countUsers
			$("#countUsers").numRolling({
			    num: countUsers,
			    callback: showTotalNum,
				unit:"人",
				Date:false
			});
			
			$("#safetyDay").numRolling({
			    num:  ShowDate(),
			    callback: showTotalNum,
				unit:"日",
				Date:true
			});
			
		}
	});
}


/*function getData(data , id, unit){
	
	var Amount1span = parseInt((data - data%100000000)/100000000);//亿位
	var Amount2span = parseInt(data%100000000/10000);//万位
	var Amount3span = parseInt(data %10000);//个位
	
	if(Amount1span == 0 ){
		$("#"+id).html("<span>"+Amount2span+"</span>万<span>"+Amount3span+"</span>"+unit);
	}else if(Amount1span == 0 && Amount2span == 0 ){
		$("#"+id).html("<span>"+Amount3span+"</span>"+unit);
	}else{
		$("#"+id).html("<span>"+Amount1span+"</span>亿<span>"+Amount2span+"</span>万<span>"+Amount3span+"</span>"+unit);
	}
}*/





/*校验手机号格式 start*/
function phoneNumBlur(THIS){ 
	var thisVal = $(THIS).val();
//  var phone = document.getElementById('phone').value;
    if( !(/^1(3|4|5|7|8)\d{9}$/.test( $(THIS).val()) ) || $(THIS).val()==""  ){ 
    	
    	$(THIS).parent(".inputDiv").siblings(".promptBox").show();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"10px"});
    } else{
    	$(THIS).parent(".inputDiv").siblings(".promptBox").hide();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"30px"});
    }
}

/*校验手机号格式 end*/



/*校验密码是否为空 start*/
function passwordBlur(THIS){ 
	var thisVal = $(THIS).val();
    if(  $(THIS).val().length<6 || $(THIS).val()==""  ){ 
    	$(THIS).parent(".inputDiv").siblings(".promptBox").show();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"10px"});
    } else{
    	$(THIS).parent(".inputDiv").siblings(".promptBox").hide();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"30px"});
    }
}
/*校验密码是否为空 end*/


/*校验验证码是否为空 start*/
function codeBlur(THIS){ 
	var thisVal = $(THIS).val();
    if(  $(THIS).val()==""  ){ 
    	$(THIS).parents(".inputDiv").siblings(".promptBox").show();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"10px"});
    } else{
    	$(THIS).parents(".inputDiv").siblings(".promptBox").hide();
    	$(THIS).parents(".inputUl li").css({"margin-bottom":"30px"});
    }
}

/*校验验证码是否为空 end*/




//三个总金额统计显示
	var showTotalNum = function (str,unit,Date ) {
	    var _index = str.toString().split('.')[1] == undefined ? 0 : str.toString().split('.')[1].length + 1; //新增代码，计算小数点后有多少位
	    var arr = new Array();
	    arr = str.toString().split("");
	    var result = "";
	    console.log(result)
	    for (i = 0; i < arr.length; i++) {
	        
	        if(Date){
	        	if (arr[i] != ',') {
		            result += arr[i];
		
		            switch (arr.length - i) {
//		                case (9 + _index):
//		                    result += '<span>亿</span>';
//		                    break;
		                case (4 + _index):
		                    result += '<span>年</span>';
		                    break;
		                case (1):
		                    result += '<span class="normal">' + unit + '</span>';
		                    break;
		            }
		        }
	        }else{
	        	if (arr[i] != ',') {
		            result += arr[i];
		
		            switch (arr.length - i) {
		                case (9 + _index):
		                    result += '<span>亿</span>';
		                    break;
		                case (5 + _index):
		                    result += '<span>万</span>';
		                    break;
		                case (1):
		                    result += '<span class="normal">' + unit + '</span>';
		                    break;
		            }
		        }
	        }
		        
	    }
	    return result;
	};

/*数字旋转 start*/
	(function ($) {
	    $.fn.numRolling = function (options) {
	        var defaults = {
	            times: 1000,
	            point: 0,
	            interval: 50,
	            callback: function (num) { return num; }
	        };
	        var options = $.extend(defaults, options);
	        var _ = this;
	        if (!Number(options.num)) {
	            return _.html(options.num);
	        }
	        var num = Number(options.num) ? options.num : parseFloat(options.num);// || parseFloat(_.text());
	        if (num == 0) {
	            return _.html(num);
	        }
	        options.point = num.toString().split('.')[1] == undefined ? 0 : num.toString().split('.')[1].length;
	        var times = options.times / options.interval;//滚动次数
	        var index = num / times;//自增
	        var number = 0;//
	
	        (function show() {
	            var nums = (number += index).toFixed(options.point);
	            _.html(options.callback(nums, options.unit, options.Date));
	            if (number > (num - index)) {
	                _.html(options.callback(parseFloat(num).toFixed(options.point), options.unit, options.Date));
	                return;
	            }
	            setTimeout(show, options.interval);
	        })();
	    };
	})(jQuery);
	/*数字旋转 end*/

