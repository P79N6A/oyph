package controllers.front.proxy;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.annotation.ProxyLoginCheck;
import common.constants.CacheKey;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SettingKey;
import common.constants.SmsScene;
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
import controllers.common.FrontBaseController;
import controllers.common.interceptor.ProxyInterceptor;
import controllers.front.FrontHomeCtrl;
import daos.proxy.SalesManUserDao;
import models.activity.shake.entity.t_user_gold;
import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.ext.cps.entity.t_red_packet_register;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import models.proxy.bean.CurrSaleMan;
import models.proxy.entity.t_proxy_salesman;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.With;
import play.mvc.Scope.Session;
import services.activity.shake.UserGoldService;
import services.common.InformationService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.SmsJyCountService;
import services.common.UserService;
import services.ext.cps.PacketRegisterService;
import services.proxy.SalesManService;
import services.proxy.SalesManUserService;

/*
 * 
 * 代理商、业务员登录
 * 
 * @author Niu Dongfeng
 * @create 2018-01-30
 * 
 */
@With(ProxyInterceptor.class)
public class LoginCtrl extends FrontBaseController {
	
	protected static SalesManService salesManService = Factory.getService(SalesManService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static SalesManUserService salesManUserService = Factory.getService(SalesManUserService.class);
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	protected static PacketRegisterService packetRegisterService = Factory.getService(PacketRegisterService.class);
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	/**
	 * 业务员，代理商登录准备
	 * 
	 * @author Niu Dongfeng
	 */
	public static void loginPre() {
		CurrSaleMan currSale = LoginCtrl.getCurrSaleMan();
		if (currSale != null) {
			if(currSale.type == 1) {
				SalesManCtrl.salesManHomePre();
			}
			if(currSale.type == 2) {
				ProxyCtrl.proxyHomePre();
			}
		}
		//随机码（用于产生验证码）
		String randomNum = Codec.UUID();
		
		//禁止该页面进行高速缓存
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		render(randomNum);
	}
	
	/**
	 * 业务员，代理商登录
	 * 
	 * @author Niu Dongfeng
	 */
	public static void logining(String mobile, String password, String veriCode, String randomNum) {
		
		/* 回显数据 */
		flash.put("mobile", mobile);
		
		//参数验证
		if (StringUtils.isBlank(mobile)) {
			flash.error("手机号不能为空");
			
			loginPre();
		}
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号格式不正确");
			
			loginPre();
		}
		if (!salesManService.isMobileExists(mobile)) {
			flash.error("手机号未注册");
			
			loginPre();
		}
		if (StringUtils.isBlank(password)) {
			flash.error("密码不能为空");
			
			loginPre();
		}
		if (StringUtils.isBlank(veriCode)) {
			flash.error("验证码不能为空");
			
			loginPre();
		}
		if (StringUtils.isBlank(randomNum)) {
			flash.error("验证码不正确");
			
			loginPre();
		}
		
		String randomCode = CaptchaUtil.getCode(randomNum);
		
		if(!veriCode.equalsIgnoreCase(randomCode)) {
			flash.error("验证码不正确");
			
			loginPre();
		}
		
		//密码加密
		String passwordEncrypt = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		
		ResultInfo result = salesManService.logining(mobile, passwordEncrypt);
		if (result.code < 0) {
			flash.error(result.msg);
			
			loginPre();
		}
		
		t_proxy_salesman salesman = (t_proxy_salesman)result.obj;
		if (salesman.type == 1) {
			SalesManCtrl.salesManHomePre();
		}
		
		if (salesman.type == 2) {
			ProxyCtrl.proxyHomePre();
		}
		
		flash.error("登录角色类型错误");
		loginPre();
	}
	
	/**
	 * 更改登录密码准备
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void updatePwdPre() {
		
		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null) {
			LoginCtrl.loginPre();
		}
		
		renderArgs.put("currSaleMan", currSaleMan);
		
		render();
	}
	
	/**
	 * 更改登录密码
	 * 
	 * @author Niu Dongfeng
	 */
	@ProxyLoginCheck
	public static void updatePwd(String originalPwd, String newPwd) {
		
		if (StringUtils.isBlank(originalPwd)) {
			flash.error("原始密码不能为空");
			
			updatePwdPre();
		}
		if (StringUtils.isBlank(newPwd)) {
			flash.error("新密码不能为空");
			
			updatePwdPre();
		}
		
		if (!StrUtil.isValidPassword(originalPwd, 6, 15)) {
			flash.error("请输入6~15个字母或数字");
			
			updatePwdPre();
		}		
		if (!StrUtil.isValidPassword(newPwd, 6, 15)) {
			flash.error("请输入6~15个字母或数字");
			
			updatePwdPre();
		}

		//获取登录信息
		CurrSaleMan currSaleMan = LoginCtrl.getCurrSaleMan();
		if (currSaleMan == null) {
			LoginCtrl.loginPre();
		}
		
		t_proxy_salesman salesman = salesManService.findByID(currSaleMan.id);
		
		if (salesman == null) {
			flash.error("查询不到业务员");
			
			updatePwdPre();
		}
		
		String originalPwdEncrypt = Encrypt.MD5(originalPwd + ConfConst.ENCRYPTION_KEY_MD5);
		if (!salesman.sale_pwd.equals(originalPwdEncrypt)) {
			flash.error("原始密码错误");
			
			updatePwdPre();
		}

		if (salesManService.updatePassWord(currSaleMan.id, newPwd)) {
			flash.success("密码修改成功");
			
			loginPre();
		}
		
		flash.error("密码修改失败");
		
		updatePwdPre();
	}
	
	/**
	 * 忘记密码准备
	 * @author Niu Dongfeng
	 */
	public static void forgetPwdPre() {
		//随机码（用于产生验证码）
		String randomNum = Codec.UUID();
		
		render(randomNum);
	}
	
	/**
	 * 发送短信验证码
	 * @author Niu Dongfeng
	 */
	public static void sendCodeOfForgetPwd(String mobile, String veriCode, String randomNum) {
		
		flash.put("mobile", mobile);
		
		ResultInfo result = new ResultInfo();
		if (StringUtils.isBlank(mobile)) {
			result.code = -1;
			result.msg  = "手机号不能为空";
			
			renderJSON(result);
		}
		if (!salesManService.isMobileExists(mobile)) {
			result.code = -1;
			result.msg  = "手机号未注册";
			
			renderJSON(result);
		}
		if (StringUtils.isBlank(veriCode)) {
			result.code = -2;
			result.msg  = "验证码不能为空";
			
			renderJSON(result);
		}
		
		String RandCode = CaptchaUtil.getCode(randomNum);

		/* 验证码失效 */
		if (RandCode == null) {
			result.code = -2;
			result.msg = "验证码已失效";

			renderJSON(result);
		}

		/* 验证码错误 */
		if (!veriCode.equalsIgnoreCase(RandCode)) {
			result.code = -2;
			result.msg = "验证码不正确";

			renderJSON(result);
		}
		
		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + SmsScene.PC_FORGET_PWD);
    	if(null == cache){
			Cache.set(mobile + encryString + SmsScene.PC_FORGET_PWD, mobile, Constants.CACHE_TIME_MINUS_30);
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
		SMSUtil.sendCode(smsAccount, smsPassword, mobile, content, Constants.CACHE_TIME_MINUS_30, SmsScene.PC_FORGET_PWD,ConfConst.IS_CHECK_MSG_CODE);*/
		
    	//焦云短息发送接口
    	if (smsJyCountService.judgeIsSend(mobile, JYSMSModel.MODEL_SEND_CODE)) {
    		Boolean flag = JYSMSUtil.sendCode(mobile, SmsScene.PC_FORGET_PWD, Constants.CACHE_TIME_MINUS_30, ConfConst.IS_CHECK_MSG_CODE,JYSMSModel.MODEL_SEND_CODE.tplId);
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
	 * 忘记密码
	 * @author Niu Dongfeng
	 */
	public static void forgetPwd(String mobile, String veriCode, String randomNum, String smsCode, String salesManPwd) {
		
		flash.put("mobile", mobile);
		
		if (StringUtils.isBlank(mobile)) {
			flash.error("手机号不能为空");
			
			forgetPwdPre();
		}
		if (!salesManService.isMobileExists(mobile)) {
			flash.error("手机号未注册");
			
			forgetPwdPre();
		}
		if (StringUtils.isBlank(veriCode)) {
			flash.error("验证码不能为空");
			
			forgetPwdPre();
		}
		
		String RandCode = CaptchaUtil.getCode(randomNum);

		/* 验证码失效 */
		if (RandCode == null) {
			flash.error("验证码已失效");
			
			forgetPwdPre();
		}
		
		/* 验证码错误 */
		if (!veriCode.equalsIgnoreCase(RandCode)) {
			flash.error("验证码不正确");
			
			forgetPwdPre();
		}
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			forgetPwdPre();
		}
		
		if (StringUtils.isBlank(salesManPwd)) {
    		flash.error("密码不能为空");
			
			forgetPwdPre();
		}
		if (!StrUtil.isValidPassword(salesManPwd, 6, 15)) {
			flash.error("密码请输入6~15个字母或数字");
			
			forgetPwdPre();
		}	
		
		/* 获取缓存中的短信验证码 */
    	Object obj = Cache.get(mobile + SmsScene.PC_FORGET_PWD);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			forgetPwdPre();
		}
		String codeStr = obj.toString();
		
		if (!codeStr.equals(smsCode)) {
			flash.error("短信验证码不正确");
			
			forgetPwdPre();
		}
		
		/* 清除缓存中的图形验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile +encryString + SmsScene.PC_FORGET_PWD);
    	if(cache != null){
    		Cache.delete(mobile + encryString + SmsScene.PC_FORGET_PWD);
    	}
		
    	if (salesManService.forgetPwd(mobile, salesManPwd)) { 
			flash.success("密码修改成功");
			
			loginPre();
		}
		
    	flash.error("密码修改失败");
		
		forgetPwdPre();
	}
	
	/**
	 * 用户退出
	 * @author Niu Dongfeng
	 */
	public static void loginOutPre(long proxyId) {
		
		if (Session.current() == null) {
			
			loginPre();
		}
		
		String sessionId = Session.current().getId();
		
		//清除登陆凭证
		Cache.delete(CacheKey.FRONT_ + "proxy" + sessionId);
		
		flash.success("退出成功！");
		loginPre();
	}
	
	/**
	 * 用户登录信息
	 * @author Niu Dongfeng
	 */
	public static CurrSaleMan getCurrSaleMan() {
		if (Session.current() == null) {
			
			return null;
		}

		String sessionId = Session.current().getId();
		if(StringUtils.isBlank(sessionId)) {
			
			return null;
		}
		
		Object userId = Cache.get(CacheKey.FRONT_ + "proxy" + sessionId);	
		if(userId == null){
			
			return null;
		}
		
		CurrSaleMan currUser = (CurrSaleMan)Cache.get("proxy_" + userId);
		if(currUser == null) {
			
			return null;
		}
		
		return currUser;
	}
	
	/**
	 * 注册页面
	 */
	public static void registerPre(String extCode) {
		
		/* 验证码 */
		String randomNum = Codec.UUID();
    	
		/* 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		/* 获取平台协议标题 */
		t_information platformRegister = informationService.findRegisterDeal();			
    	
    	render(randomNum, platformRegister, extCode);
	}
	
	/**
	 * 代理商业务员推广注册
	 */
	public static void proxyUserRegistering(String mobile, String code, String randomNum, String smsCode, String scene, String extCode, String password, int readandagree) {
		ResultInfo result = new ResultInfo();
		
		flash.put("mobile", mobile);
		
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			registerPre(extCode);
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号不符合规范");
			
			registerPre(extCode);
		}
		
		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");

			registerPre(extCode);
		}
		
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			registerPre(extCode);
		}
		
		/* 根据randomid取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomNum);
		
		/* 验证码失效 */
		if (RandCode == null) {
			flash.error("验证码失效");
			
			registerPre(extCode);
		}
		
		/* 图形验证码验证 */
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");

				registerPre(extCode);
			}
		}
		
		/* 短信验证码验证 */
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			registerPre(extCode);
		}
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			registerPre(extCode);
		}
		String codeStr = obj.toString();
		
		/** 短信验证码 验证 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				registerPre(extCode);
			}
		}
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");

			registerPre(extCode);
		}
		
		long proxyId = Long.parseLong(Encrypt.decrypt3DES(extCode, ConfConst.ENCRYPTION_APP_KEY_DES));
		t_proxy_salesman salesman = salesManService.findByID(proxyId);
				
		if(salesman == null){
			flash.error("业务邀请码不存在");

			registerPre(extCode);
		}
		
		
		/* 协议是否勾选 */
		if(readandagree != 1){
			flash.error("协议未勾选");

			registerPre(extCode);
		}
		
		/* 自动生成用户名 */
		String userName = userService.userNameFactory(mobile);
		
		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result =  userService.registering(userName, mobile, password, Client.PC, getIp(), 0);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			registerPre(extCode);
		}else{
			
			if (!userService.updateUserById(((t_user) result.obj).id, proxyId)) {
				LoggerUtil.info(true, "注册成功，生成业务员推广码时出错，数据回滚，%s", "");
				
				flash.error("推广码保存失败");
				registerPre(extCode);
			}
			
			/**将会员与代理商业务员关联关系直接插入表中*/
			
			
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					
					flash.error(result3.msg);
					registerPre(extCode);
				}
			}
			//end
			
			// cps账户开户
			if(ModuleConst.EXT_CPS){
				t_user user = (t_user) result.obj;
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				
				ResultInfo result4 = null;
				
				result4 = cpsService.createCps(null, user.id);
				
						
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					
					flash.error(result4.msg);
					registerPre(extCode);
				}
			}
			//end
			
			// 财富圈账号数据创建
			if(ModuleConst.EXT_WEALTHCIRCLE){
				t_user user = (t_user) result.obj;
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo result5 = null ; 
						
				
				result5 = wealthCircleService.creatWealthCircle(null, user.id);
				
				if(result5.code < 1){
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);
					
					flash.error(result5.msg);
					registerPre(extCode);
				}
			}
			//end
			t_user user = (t_user) result.obj;
			if (!salesManUserService.insertUser(user.id)) {
				LoggerUtil.info(true, "注册成功，插入代理商用户相关数据时出错，数据回滚，%s", "");
				
				flash.error("代理商用户插入失败");
				registerPre(extCode);
			}
			
			
			/* 清除缓存中的验证码 */
			String encryString = Session.current().getId();
	    	Object cache = Cache.get(mobile + encryString + scene);
	    	if(cache != null){
	    		Cache.delete(mobile + encryString + scene);
	    		Cache.delete(mobile + scene);
	    	}
	    	
	    	//注册时添加888红包
	    	/*t_user user1 = (t_user) result.obj;
	    	ResultInfo res = getRedPacket(user1.id);
	    	if (res.code < 0) {
	    		JPA.setRollbackOnly(); 
	    		flash.error("注册失败！");
	    		registerPre(extCode);
	    	}*/
	    	
	    	t_user user2 = (t_user) result.obj;
	    	t_user_gold res2 = getUserGold(user2.id);
	    	flash.error("恭喜您，注册成功！");
			
	    	FrontHomeCtrl.risqueEvaluationPre();
		}
	}
	
	/**
	 * 新手注册成功加金币
	 *
	 * @Title: getUserGold
	
	 * @description: 
	 *
	 * @param userId
	 * @author HanMeng
	 * @param share_num 
	 * @return 
	 * @createDate 2018年10月25日-下午2:55:49
	 */
	public static t_user_gold getUserGold(long userId) {
		t_user_gold usergold = new t_user_gold();
		//获取当前用户id
				Long user_id = getCurrUser().id;
				usergold.user_id = user_id;
				usergold.gold++;
		
			t_user_gold res1 = userGoldService.saveUserGold(user_id);
			return res1;			
		}

	/**
	 * 新手注册获取红包奖励
	 * @param userId 用户id
	 * @author guoShiJie
	 * @createDate 2018.6.26
	 * */
	public static ResultInfo getRedPacket(long userId) {
		ResultInfo result = new ResultInfo();
		Date today = new Date();
		List<t_red_packet_register> packetList = packetRegisterService.queryAll();
		if (packetList.size() > 0) {
			for (t_red_packet_register packet : packetList) {
				t_red_packet_user redpacket_user = new t_red_packet_user();
				redpacket_user.time = today;
				redpacket_user._key = packet._key;
				redpacket_user.red_packet_name = packet.money+"元理财红包";
				redpacket_user.user_id = userId;
				redpacket_user.amount = packet.money;
				redpacket_user.use_rule = packet.condition_money;
				redpacket_user.limit_day = packet.allotte_day;
				redpacket_user.limit_time = DateUtil.addDay(today, packet.allotte_day);
				redpacket_user.lock_time = null;
				redpacket_user.invest_id = 0;
				redpacket_user.lock_day = packet.lock_day;
				redpacket_user.setStatus(RedpacketStatus.getEnum(2));
				
				int res = RewardGrantUtils.queryPacketAccount(userId);
				if (res == 1) {
					int res1 = RewardGrantUtils.saveRedPacketDetail(redpacket_user);
					if (res1 != 1) {
						result.code = -1;
						result.msg = "新手获取奖励失败！";
						LoggerUtil.info(true, "新手注册获取红包奖励失败 数据回滚  %s ");
						return result;
					}
				}
			}
			result.code = 1;
			result.msg = "新手获取红包奖励成功！";
			return result;
		}
		result.code = 0;
		result.msg = "新手未获取红包奖励！";
		LoggerUtil.info(true, "新手未获取红包奖励！ ");
		return result;
	}
	
}














