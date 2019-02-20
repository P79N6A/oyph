package controllers.front.account;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;

import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.enums.Client;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.SMSUtil;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;
import controllers.back.activity.shake.UserGoldCtrl;
import controllers.common.FrontBaseController;
import models.activity.shake.entity.t_user_gold;
import models.common.entity.t_information;
import models.common.entity.t_send_code;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_packet;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.Scope.Session;
import services.common.InformationService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.SmsJyCountService;
import services.common.UserService;
import services.ext.cps.CpsActivityService;
import services.ext.cps.CpsPacketService;

/**
 * 注册页面控制器
 * @author guoShiJie
 * @createDate 2018.06.12
 * */
public class RegisterCtrl extends FrontBaseController {

	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static CpsPacketService cpsPacketService = Factory.getSimpleService(CpsPacketService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	/**
	 * 注册页面
	 * @author guoShiJie
	 * */
	public static void toRegisterPre(String mobileSign) {
		/**验证码*/
		String randomNum = Codec.UUID();
		
		/** 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		String spreadMobile1 = Encrypt.decrypt3DES(mobileSign,ConfConst.ENCRYPTION_KEY_DES);
		render(randomNum,spreadMobile1);
	}
	
	/**
	 * 注册
	 * @param moblie 手机号
	 * @param password 密码
	 * @param randomId 验证码密文
	 * @param code 验证码
	 * @param smsCode 短息验证码
	 * 
	 * @author guoShiJie
	 * */
	public static void registering (String mobile,String code,String randomId,  String smsCode,String scene, String password,String spreadMobile ) {
		ResultInfo result = new ResultInfo();
		String spreadMobile1 = Encrypt.encrypt3DES(spreadMobile,ConfConst.ENCRYPTION_KEY_DES);
		/**回显数据*/
		flash.put("mobile", mobile);
		flash.put("code", code);
		flash.put("smsCode", smsCode);
		
		/**判断手机号码是否为空*/
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**判断是否符合规范*/
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("不符合手机号码规范");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**判断手机号码是否存在*/
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**判断验证码是否存在*/
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**根据randomId取得对应的验证码值*/
		String RandCode = CaptchaUtil.getCode(randomId);
		
		if (RandCode == null) {
			flash.error("验证码已失效");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**图形验证码验证*/
		if (ConfConst.IS_CHECK_PIC_CODE) {
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");
				
				toRegisterPre(spreadMobile1);
			}
		}
		
		/**短信验证码验证*/
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			toRegisterPre(spreadMobile1);
		}
		
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			toRegisterPre(spreadMobile1);
		}
		
		String strCode = obj.toString();
		/**短息验证码验证*/
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!strCode.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				toRegisterPre(spreadMobile1);
			}
		}
		
		/**验证密码是否为空*/
		if (StringUtils.isBlank(password)) {
			flash.error("请输入密码");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
			
			toRegisterPre(spreadMobile1);
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPasswordes(password, 8, 20)) {
			flash.error("密码不能为纯属数数字或字母");
			
			toRegisterPre(spreadMobile1);
		}
		
		// 自动生成用户名 
		String userName = userService.userNameFactory(mobile);
		
		// 密码加密 
		String pass = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userService.registering(userName, mobile, pass, Client.PC, getIp(),0,0);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			System.out.println(13);
			toRegisterPre(spreadMobile1);
		} else {
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					System.out.println(14);
					flash.error(result3.msg);
					toRegisterPre(spreadMobile1);
				}
			}
		}
		//CPS模块是否存在
		if(common.constants.ModuleConst.EXT_CPS){
			t_user user = (t_user) result.obj;
			if(ModuleConst.EXT_CPS){
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				
				ResultInfo result4 = cpsService.createCps(spreadMobile, user.id);
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					System.out.println(15);
					flash.error(result4.msg);
					toRegisterPre(spreadMobile1);
				}
			}
		}
		
		// 财富圈账号数据创建
		if(ModuleConst.EXT_WEALTHCIRCLE){
			t_user user = (t_user) result.obj;
			service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
			ResultInfo result5 = wealthCircleService.creatWealthCircle(null, user.id);
			
			if(result5.code < 1){
				LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);
				System.out.println(16);
				flash.error(result5.msg);
				toRegisterPre(spreadMobile1);
			}
		}
		
		// 清除缓存中的验证码 
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
    	//新用户注册成功给一个金币
    	t_user user = (t_user) result.obj;   	
    	t_user_gold res2 = getUserGold(user.id);
    	
    	
    	
    	Date today = new Date();
    	ResultInfo error = new ResultInfo();
    	t_cps_activity cpsActivity = cpsActivityService.queryGoingActivity();
    	RewardGrantUtils.grantPacketToNewCustomer(cpsActivity, user, error);
    	if (error.code < 1) {
    		LoggerUtil.info(true,null+"++++++++++++++++++++++++++++");
    		flash.error("新用户注册获取奖励失败！");
    		System.out.println("=============="+error.msg+"=============================");
    		toRegisterPre(spreadMobile1);
    	}
		System.out.println("=====================================================================");
    	registerSuccessfulPre();
	}
	/**
	 * 加金币
	 *
	 * @Title: getUserGold
	
	 * @description: 
	 *
	 * @param userId
	 * @author HanMeng
	 * @return 
	 * @createDate 2018年10月25日-下午2:55:49
	 */
	public static t_user_gold getUserGold(long userId) {
		t_user_gold usergold = new t_user_gold();
		//获取当前用户id
				Long user_id = getCurrUser().id;
				usergold.user_id = user_id;
				usergold.gold++;
		
			t_user_gold res1 = UserGoldCtrl.saveUserGold(user_id);
			return res1;			
		}

	/**
	 * 发送短信验证码（注册）
	 * @author guoShiJie
	 * */
	public static void sendCodeOfRegister (String mobile, String randomId, String code, String scene) {
		ResultInfo result = new ResultInfo();
		if (StringUtils.isBlank(mobile)) {
			result.code = -2;
			result.msg = "手机号不能为空";

			renderJSON(result);
		}
		
		if(userService.isMobileExists(mobile)){
			result.code = -2;
			result.msg = "该手机号已被占用";

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
		if(ConfConst.IS_CHECK_MSG_CODE){
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
    	//创蓝短息发送接口
    	/* 查询短信验证码模板 
		List<t_template_notice> noticeList = noticeService.queryTemplatesByScene(NoticeScene.SECRITY_CODE);

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
    	
    	//焦云短息发送接口
    	if (!smsJyCountService.judgeIsSend(mobile, JYSMSModel.MODEL_SEND_CODE)) {
    		result.code = -1;
        	result.msg = "短息验证码未发送";
        	renderJSON(result);
    	}
    	Boolean flag = JYSMSUtil.sendCode(mobile, JYSMSModel.MODEL_SEND_CODE.tplId, Constants.CACHE_TIME_MINUS_30, ConfConst.IS_CHECK_MSG_CODE);
    	smsJyCountService.updateCount(mobile, JYSMSModel.MODEL_SEND_CODE,flag);
    	/* 添加一条短信发送控制记录 */
    	sendCodeRecordsService.addSendCodeRecords(mobile, getIp());
    	
    	result.code = 1;
    	result.msg = "短信验证码发送成功";
    	
		renderJSON(result);
	}
	
	/**
	 * 注册成功
	 * @author guoShiJjie
	 * */
	public static void registerSuccessfulPre() {
		ResultInfo result = new ResultInfo();
		result.msg = "注册成功";
		result.code = 1;
		
		render(result);
	}
	public static void registerings (String mobile,String code,String randomId,  String smsCode,String scene, String password,String spreadMobile ) {
		ResultInfo result = new ResultInfo();
		String spreadMobile1 = Encrypt.encrypt3DES(spreadMobile,ConfConst.ENCRYPTION_KEY_DES);
		/**回显数据*/
		flash.put("mobile", mobile);
		flash.put("code", code);
		flash.put("smsCode", smsCode);
		
		/**判断手机号码是否为空*/
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			toInvitePre(spreadMobile1);
		}
		
		/**判断是否符合规范*/
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("不符合手机号码规范");
			
			toInvitePre(spreadMobile1);
		}
		
		/**判断手机号码是否存在*/
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");
			
			toInvitePre(spreadMobile1);
		}
		
		/**判断验证码是否存在*/
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			toInvitePre(spreadMobile1);
		}
		
		/**根据randomId取得对应的验证码值*/
		String RandCode = CaptchaUtil.getCode(randomId);
		
		if (RandCode == null) {
			flash.error("验证码已失效");
			
			toInvitePre(spreadMobile1);
		}
		
		/**图形验证码验证*/
		if (ConfConst.IS_CHECK_PIC_CODE) {
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");
				
				toInvitePre(spreadMobile1);
			}
		}
		
		/**短信验证码验证*/
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			toInvitePre(spreadMobile1);
		}
		
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			toInvitePre(spreadMobile1);
		}
		
		String strCode = obj.toString();
		/**短息验证码验证*/
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!strCode.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				toInvitePre(spreadMobile1);
			}
		}
		
		/**验证密码是否为空*/
		if (StringUtils.isBlank(password)) {
			flash.error("请输入密码");
			
			toInvitePre(spreadMobile1);
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
			
			toInvitePre(spreadMobile1);
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPasswordes(password, 8, 20)) {
			flash.error("密码不能为纯属数数字或字母");
			
			toInvitePre(spreadMobile1);
		}
		
		// 自动生成用户名 
		String userName = userService.userNameFactory(mobile);
		
		// 密码加密 
		String pass = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userService.registering(userName, mobile, pass, Client.PC, getIp(),0,0);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			System.out.println(13);
			toInvitePre(spreadMobile1);
		} else {
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					System.out.println(14);
					flash.error(result3.msg);
					toInvitePre(spreadMobile1);
				}
			}
		}
		//CPS模块是否存在
		if(common.constants.ModuleConst.EXT_CPS){
			t_user user = (t_user) result.obj;
			if(ModuleConst.EXT_CPS){
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				
				ResultInfo result4 = cpsService.createCps(spreadMobile, user.id);
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					System.out.println(15);
					flash.error(result4.msg);
					toInvitePre(spreadMobile1);
				}
			}
		}
		
		// 财富圈账号数据创建
		if(ModuleConst.EXT_WEALTHCIRCLE){
			t_user user = (t_user) result.obj;
			service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
			ResultInfo result5 = wealthCircleService.creatWealthCircle(null, user.id);
			
			if(result5.code < 1){
				LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);
				System.out.println(16);
				flash.error(result5.msg);
				toInvitePre(spreadMobile1);
			}
		}
		
		// 清除缓存中的验证码 
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
    	//新用户注册成功给一个金币
    	t_user user = (t_user) result.obj;   	
    	t_user_gold res2 = getUserGold(user.id);
    	
    	
    	
    	Date today = new Date();
    	ResultInfo error = new ResultInfo();
    	t_cps_activity cpsActivity = cpsActivityService.queryGoingActivity();
    	RewardGrantUtils.grantPacketToNewCustomer(cpsActivity, user, error);
    	if (error.code < 1) {
    		LoggerUtil.info(true,null+"++++++++++++++++++++++++++++");
    		flash.error("新用户注册获取奖励失败！");
    		System.out.println("=============="+error.msg+"=============================");
    		toInvitePre(spreadMobile1);
    	}
		System.out.println("=====================================================================");
		inviteSuccessfulPre();
	}
	public static void toInvitePre( String mobileSign) {
		/**验证码*/
		String randomNum = Codec.UUID();
		
		/** 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		String spreadMobile1 = Encrypt.decrypt3DES(mobileSign,ConfConst.ENCRYPTION_KEY_DES);
		render(randomNum,spreadMobile1);
	} 
	/**
	 * 注册成功
	 * @author guoShiJjie
	 * */
	public static void inviteSuccessfulPre() {
		ResultInfo result = new ResultInfo();
		result.msg = "注册成功";
		result.code = 1;
		render(result);
	}
}
