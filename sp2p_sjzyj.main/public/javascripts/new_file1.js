var regPage = 120;//设置手机验证码时间 
var blug;

//手机验证码调用时间定时器(当点击按钮是input标签时使用)
function jpteleRegPager(id){
	blug = id;
	jptimerRegPage();
}


//手机验证码重新获取时间间隔 /**注册倒计时 **/
function jptimerRegPage(){
  $("#"+blug).attr("disabled", true);
  regPage = regPage - 1;
  $("#"+blug).val(regPage+"秒后获取");
  if(regPage <= 0){
     $("#"+blug).val("获取验证码");
     regPage = 120;
     sessionStorage.removeItem("regPage");
     $("#"+blug).attr("disabled", false);
  }else{
  
  	if(regPage>0 && regPage<120 ){
  		sessionStorage.setItem("regPage", regPage);
  	}else{ 
  	    sessionStorage.removeItem("regPage");
    }
  	
     setTimeout('jptimerRegPage()',1000);
  }
}


$(function(){
	if(sessionStorage.getItem('regPage') ==null|| sessionStorage.getItem('regPage') <0){
	 regPage = 120;
     $("#"+blug).attr("disabled", false);
	   if(sessionStorage.getItem('regPage') <0){
			sessionStorage.removeItem("regPage");
	   }
	}
//  刷新继续到计时
//	else { 
//		codetime = sessionStorage.getItem('codetime');
//		jpteleCodeTimer("jpteleTime");
//	}
});

  
   
(function(){
	 $("#checkWithDrawAmount").click(function(){
		 $("#checkWithDrawAmount").html("单笔最低申请提现100元");
     });
})();


var clock = ''; 
var nums = 120; 
var btn; 
function sendCode(thisBtn) 
{  
	 btn = thisBtn; 
	 btn.disabled = true; 
	 //将按钮置为不可点击
	 btn.value = nums+'秒后获取'; 
	 clock = setInterval(doLoop, 1000); 
	 //一秒执行一次 
	 } 
function doLoop() { 
	 nums--; 
	 if(nums > 0){  
		 btn.value = nums+'秒后获取'; 
		 }else{
			 clearInterval(clock); //清除js定时器 
			 btn.disabled = false;  
			 btn.value = '获取验证码';  
			 nums = 120; //重置时间 
			 } 
	 }


