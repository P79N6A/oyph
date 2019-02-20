package controllers.front.account;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import models.common.bean.CurrUser;
import models.common.entity.t_bank_card_user;
import models.common.entity.t_credit;
import models.common.entity.t_send_code;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_user;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.cache.Cache;
import play.libs.Codec;
import play.libs.WS;
import play.libs.WS.WSRequest;
import play.libs.ws.WSAsync.WSAsyncRequest;
import play.mvc.Scope.Session;
import play.mvc.With;
import services.common.BankCardUserService;
import services.common.CreditService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.SmsJyCountService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BillInvestService;
import services.core.BillService;
import services.ext.cps.CpsActivityService;
import yb.YbConsts;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.ServiceType;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.FeeCalculateUtil;
import common.annotation.PaymentAccountCheck;
import common.annotation.RealNameCheck;
import common.annotation.SimulatedCheck;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.RemarkPramKey;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.BankInfoUtil;
import common.utils.EncryptUtil;
import common.utils.Factory;
import common.utils.IDCardValidate;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.front.LoginAndRegisteCtrl;
import controllers.payment.yb.YbPaymentCallBackCtrl;
import daos.ext.cps.CpsUserDao;

/**
 * 前台-账户中心-安全中心控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MySecurityCtrl extends FrontBaseController {

	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static BankCardUserService bankCardUserService = Factory.getService(BankCardUserService.class);

	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	protected static CreditService creditService = Factory.getService(CreditService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);

	/**开户界面跳转
	 * 
	 * @createDate 2017年9月9日
	 */
	public static void createAccountPre() {
		
		render();
	}
	
	
	/**
	 * 宜宾银行个人开户
	 * @param userName
	 * @param idCard
	 * @param startTime
	 * @param endTime
	 * @param jobType
	 * @param job
	 * @param national
	 * @param postcode
	 * @param address
	 * 
	 * @author	LiuPengwei
	 * 
	 * @createDate 2017年9月9日
	 */
	 
	 
	public static void ybRegist(String userName, String idCard, Date endTime, Date startTime,long AllendTime,
			String jobType,String job, String national, String postcode, String address) throws ParseException {		
		
		long userId = getCurrUser().id;
		
		if (endTime ==null && startTime ==null && AllendTime != 1){
			
			flash.error("请填写身份证截至日期和身份证起始日期");
			securityPre();
		}
		
		if (job==null){
			job="";
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		t_user_info userInfo = userInfoService.findByColumn("id_number = ?", idCard);
		
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isNotBlank(userFund.payment_account)) {
			flash.error("你已开通资金托管");
			
			securityPre();
		}
		

		if (userInfo != null) {
			flash.error("用户身份证已开户");
			
			securityPre();
		}
		
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.PERSON_CUSTOMER_REGIST);
	
		String end1 ="";
		String start1="";
		if (endTime != null && startTime != null&& AllendTime != 1){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			end1 = sdf.format(endTime);
			start1 = sdf.format(startTime);	
			
		}
		
		if (AllendTime == 1){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			start1 = sdf.format(startTime);	
			
			
			Calendar ca = Calendar.getInstance();
			ca.setTime(startTime); 
			ca.add(Calendar.YEAR, 20);
			startTime = ca.getTime();
			end1= sdf.format(startTime);
		}
		
		userInfoService.updateUserInfos(userId,userName,idCard);
		
		ResultInfo result = PaymentProxy.getInstance().personCustomerRegist(Client.PC.code, businessSeqNo, userId, userName, idCard, 
				start1, end1, jobType, job, national, postcode, address);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		}
		
		securitysPre();
	}
	
	/**
	 * 到安全中心
	 *
	 * @author liuyang
	 * @createDate 2017年9月19日
	 *
	 */
	@PaymentAccountCheck
	public static void securitysPre(){
		
		long userId = getCurrUser().id;		
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_BINDED_BANK);
		
		//实时查询绑定银行卡
		PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId, businessSeqNo, ServiceType.QUERY_BINDED_BANK.code);
		
		
		
		securityPre();
	}
	
	/**
	 * 前台-我的财富-安全中心
	 * 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	public static void securityPre(){
		
		if(getCurrUser() == null){
		
			LoginAndRegisteCtrl.loginPre();
		}
		
		long userId = getCurrUser().id;
		
		
		t_user user = userService.findByID(userId);
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		List<t_bank_card_user> cardList = bankCardUserService.queryCardByUserId(userId);
		
		t_credit credit = creditService.findByID(userInfo.credit_id);
				
		//登录我的财富,上上签自动注册
		MyAccountCtrl.electronicSeal();
		session.put("userId", userId);
		//刷新用户缓存信息
		userService.flushUserCache(userId);
		
		render(user, userInfo, userFund, cardList, credit);
	}
	
	/**
	 * 修改手机step：1
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	@SimulatedCheck
	public static void updateUserMobilePre(){
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		
		render(userInfo);
	}
	
	/**
	 * 修改手机step：2
	 * @param mobile
	 * @param smsCode
	 * @param scene 场景
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月13日
	 *
	 */
	public static void updateMobileSecond(String mobile, String smsCode, String scene){
		checkAuthenticity();
		
		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			flash.error("请输入手机号");
			
			updateUserMobilePre();
		}
		
		if (!userService.isMobileExists(mobile)) {
			flash.error("手机号不存在");
			
			updateUserMobilePre();
		}
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			updateUserMobilePre();
		}
		String codeStr = obj.toString();
		/* 校验短信验证码 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码不正确");
				
				updateUserMobilePre();
			}
		}
		
		/* 清除缓存中的验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
		
		render(codeStr);
	}
	
	/**
	 * 修改手机step：3
	 * @param mobile
	 * @param scene 场景
	 * 
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void updateMobileThird(String mobile, String scene){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		long userId =  getCurrUser().id;
		
		result = userService.updateUserMobile(userId, mobile);
		if (result.code < 1) {
			flash.error("手机号码修改失败");
			
			securityPre();
		}
		flash.success("手机号码修改成功");
		/* 清除缓存中的验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
    	
		securityPre();
	}
	
	/**
	 * 校验手机验证码是否正确
	 * @param mobile
	 * @param smsCode
	 * @param type
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月14日
	 *
	 */
	public static void checkSmsCode(String mobile, String smsCode, int type, String scene){
		ResultInfo result = new ResultInfo();
		
		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";
			
			renderJSON(result);
		}
		
		boolean isType = userService.isMobileExists(mobile);
		if (type != 1 ) {
			isType = !isType;
		}
		
		if (!isType) {
			result.code = -1;
			result.msg = "该手机号已被占用";
			
			renderJSON(result);
		}
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			result.code = -1;
			result.msg = "短信验证码已失效";
			
			renderJSON(result);
		}
		String code = obj.toString();
		/* 校验短信验证码 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!code.equals(smsCode)) {
				result.code = -1;
				result.msg = "短信验证码不正确";
				
				renderJSON(result);
			}
		}
		
		result.code = 1;
		
		renderJSON(result);
	}
	
	/**
	 * 修改用户邮箱页面
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@SimulatedCheck
	@PaymentAccountCheck
	public static void updateUserEmailPre(){
		
		render();
	}
	
	/**
	 * 修改、绑定邮箱
	 * @description 
	 * 
	 * @param userSign 加密的用户id
	 * @param email 邮箱地址
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月22日
	 */
	@PaymentAccountCheck
	public static void updateEmailSuccess(String email, String userName){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		
		long userId = getCurrUser().id;
		/* 判断是否是有效邮箱 */
		if (!StrUtil.isEmail(email)) {
			flash.put("msg","邮箱格式错误");
			
			render();
		}
		
		/* 判断邮箱是否存在 */
		if (userInfoService.isEailExists(email)) {
			flash.put("msg","邮箱已存在");
			
			render();
		}
		
		/* 用户Id进行加密 */
		String userSign = Security.addSign(userId, Constants.USER_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		/* 邮箱进行加密 */
		String emailStr = Security.addEmailSign(email, Constants.MSG_EMAIL_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		/* 获取修改邮箱URL */
		String url = getBaseURL() + "loginAndRegiste/confirmactiveemail.html?su=" + userSign +"&se="+emailStr;

		/* 发送邮件激活 */
		result = noticeService.sendReBindEmail(email, userName, url);
		if (result.code < 1) {
			LoggerUtil.error(true, result.msg);
			flash.put("msg",result.msg);
			
			render();
		}
		
		render();
	}
	
	/**
	 * 银行卡列表 
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@PaymentAccountCheck
	public static void updateUserBankCardPre(){
		
		boolean isHaveCard = true;
		
		render(isHaveCard);
	}
	
	/**
	 * 银行卡列表
	 * @param currPage
	 * @param pageSize
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年3月2日
	 *
	 */
	@PaymentAccountCheck
	public static void listOfUserBankCardPre(int currPage, int pageSize){
		if(pageSize<1){
			pageSize=5;
		}
		long userId = getCurrUser().id;
		
		PageBean<t_bank_card_user> cardPageBean = bankCardUserService.pageOfUserCard(currPage, pageSize, userId);
		
		render(cardPageBean);
	}
	
	/**
	 * 设置默认银行卡
	 * @description 
	 * 
	 * @param id 银行卡id
	 * @param userSign 用户id
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月23日
	 */
	@SimulatedCheck
	public static void setDefaultBankCard(long id){
		ResultInfo result = new ResultInfo();
		long userId = getCurrUser().id;
		int isFlag = bankCardUserService.updateUserCardStatus(id, userId);
		if (isFlag < 1) {
			result.code = -1;
			result.msg = "设置默认银行卡失败";
			
			renderJSON(result);
		}
		result.code = 1;
		result.msg = "设置默认银行卡成功";
		
		renderJSON(result);
	}

	/**
	 * 修改密码页面
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月15日
	 *
	 */
	@SimulatedCheck
	public static void updateUserPasswordPre(){
		
		render();
	}
	
	/**
	 * 重置登录密码
	 * @description 
	 * 
	 * @param oldPassword 旧密码
	 * @param password 登录密码
	 * @param confirmPassword 重复密码
	 * @param userSign 用户加密Id
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 */
	public static void restPassword(String oldPassword,String password){
		checkAuthenticity();
		
		ResultInfo result = new ResultInfo();
		long userId =  getCurrUser().id;
		
		if (StringUtils.isBlank(oldPassword)) {
			flash.error("密码不能为空");
			
			updateUserPasswordPre();
		}
		
		t_user user = userService.findByID(userId);
		oldPassword = Encrypt.MD5(oldPassword + ConfConst.ENCRYPTION_KEY_MD5);
		
		if (!oldPassword.equalsIgnoreCase(user.password)) {
			flash.error("密码不正确");
			
			updateUserPasswordPre();
		}
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
			
			updateUserPasswordPre();
		}
		
		/*密码加密*/
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		/*新密码与旧密码不能相同*/
		if(password.equals(user.password)){
			flash.error("新密码不能与旧密码相同");
			
			updateUserPasswordPre();
		}
		
		result = userService.updatePassword(userId, password);
		if (result.code < 1) {
			flash.error(result.msg);
			
			updateUserPasswordPre();
		}
		flash.success("密码修改成功");
		
		LoginAndRegisteCtrl.loginOutPre();
	}

	
	/**
	 * 实名认证页面跳转
	 */
	@PaymentAccountCheck
	@SimulatedCheck
	public static void certificationPre(){

		render();
	}
	
	/**
	 * 实名认证
	 *
	 * @author huangyunsong
	 * @createDate 2016年5月31日
	 */
	@PaymentAccountCheck
	@SimulatedCheck
	public static void checkRealName(String realName, String idNumber) {
		checkAuthenticity();
		
		flash.put("realName", realName);
		flash.put("idNumber", idNumber);

		if (StringUtils.isBlank(realName)) {
			flash.error("真实姓名不能为空");

			certificationPre();
		}

		if (StringUtils.isBlank(idNumber)) {
			flash.error("身份证不能为空");
			
			certificationPre();
		}

		if(!"".equals(IDCardValidate.chekIdCard(0, idNumber))) {
			flash.error("请输入正确的身份证号");
			
			certificationPre();
		}
	
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			certificationPre();
		}
		
		userInfo.reality_name = realName;
		userInfo.id_number = idNumber;
		
		ResultInfo result = userInfoService.updateUserInfo(userInfo);
		if (result.code < 0) {
			flash.error("保存用户信息失败");
			
			certificationPre();
		}
		
		//刷新用户缓存信息
		userService.flushUserCache(getCurrUser().id);

		ResultInfo error = new ResultInfo();
		
		t_cps_activity cpsActivityGoing = null;
		t_user user = t_user.findById(getCurrUser().id);
		t_cps_user cpsUser = cpsUserDao.findByUserId(getCurrUser().id);
		if (cpsUser != null) {
			t_cps_activity cpsActivity = cpsActivityService.queryCpsActivity(cpsUser.activity_id);
			if (cpsActivity != null) {
				cpsActivityGoing  = cpsActivityService.queryGoingCpsActivityById(cpsActivity.id, 1);
			}
		}
		RewardGrantUtils.registerAndRealName(cpsActivityGoing, user, error);
		
		
		flash.success("实名认证成功！");
		securityPre();
	}
	
	
	/**
	 * 绑定银行卡
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月9日
	 */
	@SimulatedCheck
	@PaymentAccountCheck
	@RealNameCheck
	public static void bindCardPre(String t_bankno, long mobile, String bankName, String bankCode,String smsCode,String scene, String randomId, String code) {	
		ResultInfo result;
		
		/* 根据randomId取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);

		/* 验证码失效 */
		if (RandCode == null) {
			flash.error("验证码不能为空");
			
			securityPre();
		}

		/* 验证码错误 */
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");
				
				securityPre();
			}
		}
		
		/** 银行卡名称 验证 */
		if(StringUtils.isBlank(bankName) || StringUtils.isBlank(bankCode)) {
			flash.error("请选择银行卡名称");
			
			securityPre();
		}
		
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		t_bank_card_user  bank_card_user = bankCardUserService.findByColumn("account = ?", t_bankno);
	 
		
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (bank_card_user != null) {
			flash.error("银行卡号已绑定");
			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			securityPre();
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_BIND_CARD);
		
		result = PaymentProxy.getInstance().bindCardCheckPassword(Client.PC.code, businessSeqNo, userId);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			updateUserBankCardPre();
		}
		
		securityPre();
	}
	
	/**
	 * 到绑定绑定银行卡页面
	 *
	 * @author liuyang
	 * @createDate 2017年9月13日
	 */
	public static void toBindCardsPre(String businessSeqNo) {
		
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			securityPre();
		}
		
		ResultInfo result = PaymentProxy.getInstance().userBindCard(Client.PC.code, businessSeqNo, userId, userInfo.reality_name, userInfo.id_number, userInfo.mobile);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			securityPre();
		}
		
		securityPre();
	}
	
	/**
	 * 到绑定绑定银行卡页面
	 *
	 * @author liuyang
	 * @createDate 2017年9月13日
	 */
	public static void toBindCardPre(String mobilePhone) {
		
		ResultInfo result;
		
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			securityPre();
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_BIND_CARD);
		
		//result = PaymentProxy.getInstance().bindCardCheckPassword(Client.PC.code, businessSeqNo, userId);
		result = PaymentProxy.getInstance().bindCardCheckPassword1(Client.PC.code, businessSeqNo, userId, mobilePhone);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			updateUserBankCardPre();
		}
		
		render(userInfo);
	}
	
	/**
	 * 同步绑定银行卡
	 *
	 * @author huangyunsong
	 * @createDate 2016年2月15日
	 */
	public static void flushBindCardPre() {
		long userId = getCurrUser().id;
		
		bankCardUserService.delBank(userId);
		
		//业务流水号
				String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_BINDED_BANK);
		
		//实时查询绑定银行卡
		ResultInfo result = PaymentProxy.getInstance().queryBindedBankCard(Client.PC.code, userId, businessSeqNo,ServiceType.QUERY_BINDED_BANK.code);
					
		List<t_bank_card_user> bankList = (List<t_bank_card_user>) result.obj;
		
		for (t_bank_card_user bcu : bankList) {
			bankCardUserService.addUserCard(userId, bcu.bank_name, bcu.bank_code, bcu.account);
		}
		
		/* 刷新当前用户缓存 */
		userService.flushUserCache(userId);

		updateUserBankCardPre();
	}
	
	/**
	 * 发送手机验证码（修改绑定手机）
	 * @description 
	 * 
	 * @param mobile 手机号码
	 * @param type 验证码发送场景（1：发送至原手机，2：发送至新手机）
	 * @param scene 短信发送场景
	 * 
	 * @author Chenzhipeng
	 * @createDate 2015年12月18日
	 */
	public static void sendCode(String mobile,int type, String scene){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";
			
			renderJSON(result);
		}
		
		if (!StrUtil.isMobileNum(mobile)) {
			result.code = -1;
			result.msg = "手机号格式不正确";
			
			renderJSON(result);
		}
		
		boolean isType = userService.isMobileExists(mobile);
		if (type != 1 ) {
			isType = !isType;
		}
		
		if (!isType) {
			result.code = -1;
			result.msg = "该手机号已被占用";
			
			renderJSON(result);
		}
		
    	/* 根据手机号码查询短信发送条数 */
    	List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
    	if(recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE){
			result.code = -2;
			result.msg = "该手机号码单日短信发送已达上限";

			renderJSON(result);
    	}
    	
    	/* 根据IP查询短信发送条数 */
    	List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(getIp());
    	if(recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP){
			result.code = -2;
			result.msg = "该IP单日短信发送已达上限";

			renderJSON(result);
    	}
		
		/* 设置手机号码60S内无法重复发送验证短信 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(null == cache){
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
    	}else{
    		String isOldMobile = cache.toString();
    		if (isOldMobile.equals(mobile)) {
    			result.code = -2;
    			result.msg = "短信已发送";

    			renderJSON(result);
    		}
    	}
    	//创蓝接口
    	/* 获取短信验证码模板 
		List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

		if(noticeList.size() < 0){
			result.code = -2;
			result.msg = "短信发送失败";

			renderJSON(result);
    	}
		String content = noticeList.get(0).content;
		
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);
		
		 发送短信验证码 
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, scene, ConfConst.IS_CHECK_MSG_CODE);*/
    	
    	//焦云接口
    	if (!smsJyCountService.judgeIsSend(mobile, JYSMSModel.MODEL_SEND_CODE)) {
    		result.code = -2;
        	result.msg="短息验证码未发送";
        	renderJSON(result);
    	}
    	Boolean flag = JYSMSUtil.sendCode(mobile, scene, Constants.CACHE_TIME_MINUS_30, ConfConst.IS_CHECK_MSG_CODE,JYSMSModel.MODEL_SEND_CODE.tplId);
        smsJyCountService.updateCount(mobile, JYSMSModel.MODEL_SEND_CODE,flag);
    	/* 添加一条短信发送控制记录 */
    	sendCodeRecordsService.addSendCodeRecords(mobile, getIp());
    	
    	result.code = 1;
    	result.msg = "短信验证码发送成功";
    	
		
		renderJSON(result);
	}
	
	/**
	 * 设置交易密码
	 */
	public static void setUserPasswordPre() {
		
		long userIds = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userIds);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.SET_USER_PASSWORD);
		
		ResultInfo result = PaymentProxy.getInstance().setUserPassword(Client.PC.code, businessSeqNo, userIds);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			securityPre();
		}
		
		securityPre();
		
	}
	
	/**
	 * 修改交易密码
	 */
	public static void amendUserPasswordPre() {
		
		long userIds = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userIds);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.AMEND_USER_PASSWORD);
		
		ResultInfo result = PaymentProxy.getInstance().amendUserPassword(Client.PC.code, businessSeqNo, userIds);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			securityPre();
		}
		
		securityPre();
		
	}
	
	/**
	 * 重置交易密码
	 */
	public static void resetUserPasswordPre() {

		long userIds = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userIds);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		//业务流水号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(yb.enums.ServiceType.RESET_USER_PASSWORD);
		
		ResultInfo result = PaymentProxy.getInstance().resetUserPassword(Client.PC.code, businessSeqNo, userIds);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			securityPre();
		}
		
		securityPre();
	}
	
	/**
	 * 校验手机验证码是否正确
	 * @param mobile
	 * @param randomId
	 * @param code
	 * @param smsCode
	 * @param scene 场景
	 * @author liuyang
	 * @createDate 2017年9月13日
	 *
	 */
	public static void checkSmsCodes(String mobile, String randomId, String code, String smsCode, String scene){
		ResultInfo result = new ResultInfo();
		
		/* 验证信息 */
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg = "手机号不能为空";
			
			renderJSON(result);
		}
		
		if (StringUtils.isBlank(code)) {
			result.code = -1;
			result.msg = "验证码不能为空";
			
			renderJSON(result);
		}
		
		/* 根据randomid取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);
		
		/* 验证码失效 */
		if (RandCode == null) {
			result.code = -1;
			result.msg = "验证码失效";
			
			renderJSON(result);
		}
		
		/* 图形验证码验证码 校验 */
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				result.code = -1;
				result.msg = "验证码不正确";
				
				renderJSON(result);
			}
		}
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			result.code = -1;
			result.msg = "短信验证码已失效";
			
			renderJSON(result);
		}
		String SMScode = obj.toString();
		
		/* 短信验证码 校验 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!SMScode.equals(smsCode)) {
				result.code = -1;
				result.msg = "短信验证码不正确";
				
				renderJSON(result);
			}
		}
		
		result.code = 1;
		
		renderJSON(result);
	}
	
	/**
	 * 通过银行卡号得到银行名称
	 * 
	 * @description
	 * 
	 * @param t_bankno 银行卡号
	 * 
	 * @author liuyang
	 * @createDate 2017年10月19日
	 */
	public static void checkBankName(String t_bankno) {
		
		ResultInfo result = new ResultInfo();
		
		String bankName = BankInfoUtil.getNameOfBank(t_bankno);
		
		if(bankName.equals("") || StringUtils.isBlank(bankName)) {
			result.code = -1;
			result.msg = "暂不支持";
		}else {
			String[] banks = BankInfoUtil.getBankSimpleName(bankName);
			result.code = 1;
			result.msg = banks[0];
			result.obj = banks[1];
		}
		
		renderJSON(result);
	}
	
	/**
	 * 发送短信验证码（注册）
	 * 
	 * @description
	 * 
	 * @param mobile 手机号码
	 * @param randomId
	 * @param code
	 * 
	 * @author liuyang
	 * @createDate 2017年9月13日
	 */
	public static void sendCodeOfRegister(String mobile, String randomId, String code, String scene) {
		ResultInfo result = new ResultInfo();

		if (StringUtils.isBlank(mobile)) {
			result.code = -2;
			result.msg = "手机号不能为空";

			renderJSON(result);
		}
		
		if (StringUtils.isBlank(code)) {
			result.code = -3;
			result.msg = "验证码不能为空";

			renderJSON(result);
		}
		
		/* 根据randomId取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);

		/* 验证码失效 */
		if (RandCode == null) {
			result.code = -3;
			result.msg = "验证码已失效";

			renderJSON(result);
		}

		/* 验证码错误 */
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				result.code = -3;
				result.msg = "验证码不正确";

				renderJSON(result);
			}
		}
		
		/* 根据手机号码查询短信发送条数 */
    	List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
    	if(recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE){
			result.code = -4;
			result.msg = "该手机号码单日短信发送已达上限";

			renderJSON(result);
    	}
    	
    	/* 根据IP查询短信发送条数 */
    	List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(getIp());
    	if(recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP){
			result.code = -4;
			result.msg = "该IP单日短信发送已达上限";

			renderJSON(result);
    	}
		
		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(null == cache){
			Cache.set(mobile + encryString + scene, mobile, Constants.CACHE_TIME_SECOND_60);
    	}else{
    		String isOldMobile = cache.toString();
    		if (isOldMobile.equals(mobile)) {
    			result.code = -4;
    			result.msg = "短信已发送";

    			renderJSON(result);
    		}
    	}
    	
    	/* 查询短信验证码模板 */
		/*List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

		if(noticeList.size() < 0){
			result.code = -4;
			result.msg = "短信发送失败";

			renderJSON(result);
    	}
		String content = noticeList.get(0).content;
		String smsAccount = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_ACCOUNT);
		String smsPassword = settingService.findSettingValueByKey(SettingKey.SERVICE_SMS_PASSWORD);

		 发送短信验证码 
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, scene,ConfConst.IS_CHECK_MSG_CODE);*/
    	
    	if (smsJyCountService.judgeIsSend(mobile, JYSMSModel.MODEL_SEND_CODE)) {
    		Boolean flag = JYSMSUtil.sendCode(mobile, scene, Constants.CACHE_TIME_MINUS_30, ConfConst.IS_CHECK_MSG_CODE, JYSMSModel.MODEL_SEND_CODE.tplId);
        	smsJyCountService.updateCount(mobile, JYSMSModel.MODEL_SEND_CODE,flag);
    		/* 添加一条短信发送控制记录 */
    		sendCodeRecordsService.addSendCodeRecords(mobile, getIp());
    		
    		result.code = 1;
    		result.msg = "短信验证码发送成功";
    		renderJSON(result);
    	}
    	result.code = -2;
    	result.msg = "短息验证码未发送";
		
		renderJSON(result);
	}
	
	/**
	 * 手机号码校验
	 * @param mobile 手机号码
	 *
	 * @author liuyang
	 * @createDate 2017年9月13日
	 *
	 */
	public static void checkUserMobile(String mobile){
		if (StringUtils.isBlank(mobile)) {
			renderJSON(false);
		}
		
		/* 判断手机号码是否有效 */
		if (!StrUtil.isMobileNum(mobile)) {
			renderJSON(false);
		}
		
		renderJSON(true);
	}
	
	
	/**
	 * 平台用户注销
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年2月23日
	 */
	public static void custCancelWithdrawPre(){
		ResultInfo result = new ResultInfo(); 
		
		long userId = getCurrUser().id;
		
		//查询用户资金
		t_user_fund userFund = userFundService.findByColumn("user_id = ?", userId);
		
		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		
		//查询用户投标情况
		List<t_bill_invest> billInvestList = billInvestService.findListByColumn("user_id = ? and status =?", userId,0);
		if (billInvestList !=null && billInvestList.size() > 0 ){
			
			
			flash.error("用户存在未获得的回款！");
			securityPre();
		}
		
		//查询用户借款情况
		List<t_bill> billList = billService.findListByColumn("user_id = ? and status =?", userId,0);
		if (billList !=null && billList.size() > 0 ){
			
			flash.error("用户存在未完成的借款！");
			securityPre();
		}
		
		//查询最近的一个绑定的银行卡
		t_bank_card_user bankCardUser = bankCardUserService.queryCard(userId);
		
		//用户余额
		
		if (userFund.balance == 0){

			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_CANCEL);
			
			Map<String, String> inBody = new HashMap<String, String>();
			inBody.put("customerId", userId+"");
			inBody.put("businessSeqNo", businessSeqNo);
			inBody.put("busiTradeType", ServiceType.CUSTOMER_CANCEL.key);
			
			//注销接口
			result = PaymentProxy.getInstance().custInfoAysn(inBody, OrderNoUtil.getClientSn(), ServiceType.CUSTOMER_CANCEL);
			
			if (result.code > 0) {
				result = userFundService.custCancel(userId);
				if (result.code > 0) {
					
					flash.error(result.msg);
					securityPre();
				}
			}
			flash.error("注销失败！");
			securityPre();

		}else if(userFund.balance <= 2 && userFund.balance > 0){
			
			//个人用户提现
			if (userInfo.enterprise_name ==null){
				
				//业务订单号
				String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.CUSTOMER_WITHDRAW);
				
				result = userFundService.withdrawal(userId, businessSeqNo, userFund.balance, bankCardUser.account, Client.PC);
				if (result.code < 0){
					flash.error(result.msg);
					securityPre();
				}
				//提现成功后注销用户
				result = PaymentProxy.getInstance().withdrawalCheckPassword(Client.PC.code, businessSeqNo, userId,
						userFund.balance, bankCardUser.account, ServiceType.CUSTOMER_WITHDRAW, 0);
				
				
			//企业用户提现
			}else {
				
				String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTERPRISE_WITHDRAW);
				
				result = userFundService.withdrawal(userId, businessSeqNo, userFund.balance, bankCardUser.account, Client.PC);
				if (result.code < 0){
					flash.error(result.msg);
					securityPre();
				}
				
				result = PaymentProxy.getInstance().withdrawalCheckPassword(Client.PC.code,businessSeqNo, userId,
						userFund.balance, bankCardUser.account, ServiceType.ENTERPRISE_WITHDRAW, 0);
				
			}	
			
		}
		
		flash.error("用户可用资金超过可注销范围！");
		securityPre();
	}

	/**
	 * 到绑定绑定银行卡页面(银行卡预留手机号)
	 *
	 * @author guoShiJie
	 * @createDate 2018年4月17日
	 */
	public static void toBindCards1Pre(String businessSeqNo,String mobilePhone) {
		
		long userId = getCurrUser().id;
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			flash.error("获取用户信息失败");

			securityPre();
		}
		
		if (StringUtils.isBlank(userFund.payment_account)) {
			flash.error("你还未开户，请先开户");
			
			securityPre();
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(getCurrUser().id);
		if (userInfo == null) {
			flash.error("用户信息不存在");
			
			securityPre();
		}
		userInfo.mobile = mobilePhone;
		ResultInfo result = PaymentProxy.getInstance().userBindCard(Client.PC.code, businessSeqNo, userId, userInfo.reality_name, userInfo.id_number, userInfo.mobile);
		
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			securityPre();
		}
		
		securityPre();
	}
	
	/**
	 * 获取交易密码transaction_password属性值
	 * 
	 * @author guoShiJie
	 * @createDate 2018.04.25
	 * */
	public static int transactionPasswordValue(long userId){
		return userFundService.transactionPasswordValue(userId);
	}
	
	/**
	 * 解绑银行卡验密
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年6月4日14:13:35
	 */
	public static void unbundleBankCardPre (String bankAccountNo){
		long userId = getCurrUser().id;
		
		t_user_info userInfo = userInfoService.findByColumn("user_id=?", userId);
		
		if (userInfo == null) {
			flash.success("客户信息查询失败！");
			updateUserBankCardPre();
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FORGET_PASSWORD);	
		
		Map<String, String> inBody = new HashMap<String, String>();
		
		inBody.put("customerId", userId+"");
		inBody.put("businessSeqNo", businessSeqNo);
		inBody.put("busiTradeType", ServiceType.CUSTOMER_UNBINDING.key);
		inBody.put("ctype", "");
		inBody.put("crole", "");
		inBody.put("companyName", "");
		inBody.put("username", userInfo.reality_name);
		inBody.put("certType", "");
		inBody.put("certNo", "");
		inBody.put("certFImage", "");
		inBody.put("certBImage", "");
		inBody.put("certInfo", "");
		inBody.put("idvalidDate", "");
		inBody.put("idexpiryDate", "");
		inBody.put("job", "");
		inBody.put("jobType", "");
		inBody.put("postcode", "");
		inBody.put("address", "");
		inBody.put("national", "");
		inBody.put("completeFlag", "");
		inBody.put("phoneNo", userInfo.mobile);
		inBody.put("uniSocCreCode", "");
		inBody.put("uniSocCreDir", "");
		inBody.put("bizLicDomicile", "");
		inBody.put("entType", "");
		inBody.put("dateOfEst", "");
		inBody.put("corpacc", "");
		inBody.put("corpAccBankNo", "");
		inBody.put("corpAccBankNm", "");
		inBody.put("bindFlag", "");
		inBody.put("bindType", "");
		inBody.put("acctype", "");
		inBody.put("oldbankAccountNo", "");
		inBody.put("bankAccountNo", bankAccountNo);
		inBody.put("bankAccountName", userInfo.reality_name);
		inBody.put("bankAccountTelNo", "");
		inBody.put("note", "");
		
		PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, userId, BaseController.getBaseURL() + "account/security/unbinding", userInfo.reality_name, ServiceType.CUSTOMER_UNBINDING, 0.00, inBody, ServiceType.UNBINDING_CHECK_TRADE_PASS, 1, "62220204*******2610", "", "");
		
	}
	
	
	/**
	 * 客户解绑
	 * 
	 * @author niu
	 * @create 2017.10.17
	 */
	public static void unBindAccCheckPass() {
		
		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.CUSTOMER_UNBINDING);
		
		if (result.code < 0) {
			flash.error(result.msg);
			
			updateUserBankCardPre();
		}
		
		Map<String, String> reqParams = result.msgs;

		result = PaymentProxy.getInstance().custInfoAysn(reqParams, OrderNoUtil.getClientSn(), ServiceType.CUSTOMER_UNBINDING);
		if (result.code > 0) {
			t_bank_card_user bankUser = bankCardUserService.findByColumn("account = ?", EncryptUtil.decrypt(reqParams.get("bankAccountNo")));
			t_bank_card_user bank = bankUser.delete();
			if (bank != null) {
				flash.success("客户解绑成功！");
				updateUserBankCardPre();
			}
		}
	
		flash.error(result.msg);
		updateUserBankCardPre();
	}
		
	
	
}
