package controllers.common.interceptor;

import models.common.bean.CurrUser;

import org.apache.commons.lang.StringUtils;

import play.mvc.Before;
import play.mvc.Controller;

import common.annotation.LoginCheck;
import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.utils.ResultInfo;

import controllers.common.FrontBaseController;

/**
 * 用户状态拦截器
 * 	 包括：登陆拦截器、资金托管账户开户拦截、用户未实名认证拦截器、未绑定银行卡
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2016年1月15日
 */
public class UserStatusInterceptor extends Controller{
	/**
	 * 登陆拦截器
	 */
	@Before(priority=11)
	static void checkLogin(){
		LoginCheck checkAction = getActionAnnotation(LoginCheck.class);
		if (checkAction == null) {
			
			return;
		}
		
		CurrUser user = FrontBaseController.getCurrUser();
		if (user != null) {
			
			return;
		}

		boolean isAjax = request.isAjax();
		if(isAjax){
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_LOGIN;
			result.msg = "没有登录，或者登录状态已经失效!请重新登录!";
			
			renderJSON(result);
			
		} else{
			
			redirect("front.LoginAndRegisteCtrl.loginPre");
		}
	}
	
	/**
	 * 资金托管账户开户拦截
	 */
	@Before(priority=12)
	static void checkPaymentAccount(){
		PaymentAccountCheck checkAction = getActionAnnotation(PaymentAccountCheck.class);
		
		/* 不拦截 */
		if(checkAction == null){ 
			
			return;
		}
		
		CurrUser user = FrontBaseController.getCurrUser();
		if (user == null) {
			
			checkLogin();
		}
		
		if (StringUtils.isNotBlank(user.payment_account)) {
			
			return;
		}
		
		boolean isAjax = request.isAjax();
		if(isAjax){
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_PAYMENT_ACCOUNT;
			result.msg = "未开通资金托管账户";
			
			renderJSON(result);
		} else{
			
			redirect("front.account.MyAccountCtrl.paymentAccountGuidePre");
		}
	}
	
	/**
	 * 用户未实名认证拦截器
	 */
	@Before(priority=13)
	static void checkRealName(){
		RealNameCheck realNameCheck = getActionAnnotation(RealNameCheck.class);
		if (realNameCheck == null) {
			
			return;
		}
		
		/* 获取登录当前对象 */
		CurrUser user = FrontBaseController.getCurrUser();
		if (user == null) {
			
			checkLogin();
		}
		
		if (user.is_real_name) {
			
			return;
		}
		
		boolean isAjax = request.isAjax();
		if(isAjax){
			ResultInfo result = new ResultInfo();
			result.code = ResultInfo.NOT_REAL_NAME;
			result.msg = "未实名认证";
			
			renderJSON(result);
		} else{
			
			redirect("front.account.MyAccountCtrl.realNameGuidePre");
		}
	}
}
