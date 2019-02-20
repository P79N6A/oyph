package controllers.front.account;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

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
import controllers.front.LoginAndRegisteCtrl;
import models.activity.shake.entity.t_user_gold;
import models.common.entity.t_send_code;
import models.common.entity.t_template_notice;
import models.common.entity.t_user;
import models.ext.cps.entity.t_red_packet_register;
import models.ext.cps.entity.t_three_elements_setting;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.Scope.Session;
import services.common.InformationService;
import services.common.NoticeService;
import services.common.SendCodeRecordsService;
import services.common.SmsJyCountService;
import services.common.UserService;
import services.ext.cps.PacketRegisterService;
import services.ext.cps.ThreeElementsService;

public class LandingPageCtrl extends FrontBaseController{
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	protected static ThreeElementsService threeElementsService = Factory.getService(ThreeElementsService.class);
	
	protected static PacketRegisterService packetRegisterService = Factory.getService(PacketRegisterService.class);
	
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	/**
	 * 落地页面
	 * @author guoShiJie
	 * @createDate 2018.6.25
	 * */
	public static void newHandRegisterPre() {
		
		String randomNum = Codec.UUID();
		
		/** 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		t_three_elements_setting setting = threeElementsService.findElementsByKey("landing_page_key");
		
		render(setting,randomNum);
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
	 * 注册
	 * */
	public static void register(String mobile,String code,String randomId,  String smsCode,String scene, String password) {
		ResultInfo result = new ResultInfo();
		
		/**判断手机号码是否为空*/
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			newHandRegisterPre();
		}
		
		/**判断是否符合规范*/
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("不符合手机号码规范");
			
			newHandRegisterPre();
		}
		
		/**判断手机号码是否存在*/
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");
			
			newHandRegisterPre();
		}
		
		/**判断验证码是否存在*/
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			newHandRegisterPre();
		}
		
		/**根据randomId取得对应的验证码值*/
		String RandCode = CaptchaUtil.getCode(randomId);
		
		if (RandCode == null) {
			flash.error("验证码已失效");
			
			newHandRegisterPre();
		}
		
		/**图形验证码验证*/
		if (ConfConst.IS_CHECK_PIC_CODE) {
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");
				
				newHandRegisterPre();
			}
		}
		
		/**短信验证码验证*/
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			newHandRegisterPre();
		}
		
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			newHandRegisterPre();
		}
		
		String strCode = obj.toString();
		/**短息验证码验证*/
		if (ConfConst.IS_CHECK_MSG_CODE) {
			if (!strCode.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				newHandRegisterPre();
			}
		}
		
		/**验证密码是否为空*/
		if (StringUtils.isBlank(password)) {
			flash.error("请输入密码");
			
			newHandRegisterPre();
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");
			
			newHandRegisterPre();
		}
		
		/**验证密码是否符合规范*/
		if (!StrUtil.isValidPasswordes(password, 8, 20)) {
			flash.error("密码不能为纯属数数字或字母");
			
			newHandRegisterPre();
		}
		
		// 自动生成用户名 
		String userName = userService.userNameFactory(mobile);
		
		// 密码加密 
		String pass = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userService.registering(userName, mobile, pass, Client.PC, getIp(),0,0);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			newHandRegisterPre();
		} else {
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					
					flash.error(result3.msg);
					newHandRegisterPre();
				}
			}
		}
		//CPS模块是否存在
		if(common.constants.ModuleConst.EXT_CPS){
			t_user user = (t_user) result.obj;
			if(ModuleConst.EXT_CPS){
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				
				ResultInfo result4 = cpsService.createCps(null, user.id);
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					
					flash.error(result4.msg);
					newHandRegisterPre();
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
				
				flash.error(result5.msg);
				newHandRegisterPre();
			}
		}
		
		// 清除缓存中的验证码 
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + scene);
    	if(cache != null){
    		Cache.delete(mobile + encryString + scene);
    		Cache.delete(mobile + scene);
    	}
    	
    	t_user user1 = (t_user) result.obj;
    	ResultInfo res = getRedPacket(user1.id);
    	if (res.code < 0) {
    		JPA.setRollbackOnly(); 
    		flash.error("注册失败！");
    		newHandRegisterPre();
    	}
    	t_user user2 = (t_user) result.obj;
    	t_user_gold res2 = getUserGold(user2.id);
    	flash.error("恭喜您，注册成功！");
    	LoginAndRegisteCtrl.registerSuccessPre();
	}
	/**
	 * 
	 *	注册成功后给一个金币
	 * @Title: getUserGold
	
	 * @description: 
	 *
	 * @param userId
	 * @return 
	   
	 * @return t_user_gold   
	 *
	 * @author HanMeng
	 * @createDate 2018年10月29日-下午3:51:36
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
	
	/**
	 * 手机页面
	 * @author guoShiJie
	 * @createDate 2018.6.29
	 * */
	public static void mobilePagePre() {
		
		render();
	}
}
