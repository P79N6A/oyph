package controllers.app.account;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.activity.shake.entity.t_user_gold;
import models.common.bean.CurrUser;
import models.common.entity.t_information;
import models.common.entity.t_send_code;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.core.entity.t_auto_invest_setting;
import models.ext.cps.entity.t_red_packet_register;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.mvc.Scope.Session;
import service.AccountAppService;
import services.activity.shake.UserGoldService;
import services.common.InformationService;
import services.common.SendCodeRecordsService;
import services.common.SupervisorService;
import services.common.UserInfoService;
import services.core.InvestService;
import services.ext.cps.PacketRegisterService;
import yb.YbUtils;
import yb.enums.ServiceType;

import com.shove.Convert;
import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.SmsScene;
import common.enums.Client;
import common.enums.DeviceType;
import common.enums.JYSMSModel;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import controllers.back.activity.shake.UserGoldCtrl;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import daos.core.AutoInvestSettingDao;

/**
 * 用户账户(1)[OPT:1XX]
 *
 * @description 包含账户登录[OPT:11x]和注册相关信息[OPT:12x]
 *
 * @author DaiZhengmiao
 * @createDate 2016年6月29日
 */
public class AccountAction {
	
	private static AccountAppService userAppService = Factory.getService(AccountAppService.class);
	
	private static SendCodeRecordsService sendCodeRecordsService = Factory.getService(SendCodeRecordsService.class);
	
	private static SupervisorService  supervisorService = Factory.getService(SupervisorService.class);
	
	protected static AutoInvestSettingDao autoInvestSettingDao = Factory.getDao(AutoInvestSettingDao.class);
	
	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static PacketRegisterService packetRegisterService = Factory.getService(PacketRegisterService.class);
	
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	/**
	 * 
	 * 发送验证码(OPT=111)
	 * @return
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String sendCode(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String mobile = parameters.get("mobile");
        String scene = parameters.get("scene");//场景
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg","手机号不符合规范");
			
			return json.toString();	
		}
		
		if (SmsScene.APP_REGISTER.equals(scene)) {
			if(userAppService.isMobileExists(mobile)){
				json.put("code", -1);
				json.put("msg","该手机号已被占用");
				
				return json.toString();	
			}
		} else if (SmsScene.APP_FORGET_PWD.equals(scene)) {
			if(!userAppService.isMobileExists(mobile)){
				json.put("code", -1);
				json.put("msg","手机号未注册");
				
				return json.toString();	
			}
		} else {
			json.put("code", -1);
			json.put("msg", "场景类型不符合规范");
			
			return json.toString();
		}

		/* 根据手机号码查询短信发送条数 */
    	List<t_send_code> recordByMobile = sendCodeRecordsService.querySendRecordsByMobile(mobile);
    	if(recordByMobile.size() >= ConfConst.SEND_SMS_MAX_MOBILE){
			json.put("code", -1);
			json.put("msg","该手机号码单日短信发送已达上限");
			
			return json.toString();	
		}
    	
    	/* 根据IP查询短信发送条数 */
    	List<t_send_code> recordByIp = sendCodeRecordsService.querySendRecordsByIp(BaseController.getIp());
    	if(recordByIp.size() >= ConfConst.SEND_SMS_MAX_IP){
			json.put("code", -1);
			json.put("msg","该IP单日短信发送已达上限");
			
			return json.toString();	
		}
		
		/* 将手机号码存入缓存，用于判断60S中内同一手机号不允许重复发送验证码 */
		String encryString = Session.current().getId();
    	Object cache = Cache.get(mobile + encryString + JYSMSModel.MODEL_SEND_CODE.tplId);
    	if(null == cache){
			Cache.set(mobile + encryString + JYSMSModel.MODEL_SEND_CODE.tplId, mobile, Constants.CACHE_TIME_SECOND_60);
    	}else{
    		String isOldMobile = cache.toString();
    		if (isOldMobile.equals(mobile)) {
    			json.put("code", 1);
    			json.put("msg","短信已发送");

    			return json.toString();	
    		}
    	}
    	
		return userAppService.sendCode(mobile, scene);	
	}
	
	/***
	 * 
	 * 注册准备（opt=124）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String registerPre(Map<String, String> parameters){
		
		return userAppService.registerPre();
	} 
	
	/***
	 * 
	 * 用户注册（OPT=113）
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-3-31
	 */
	public static String registering(Map<String, String> parameters) throws Exception {
		JSONObject json = new JSONObject();
		
		String mobile = parameters.get("mobile");//手机号
        String password = parameters.get("password");//密码
        String verificationCode = parameters.get("verificationCode");//手机验证码
        String invitationCode = parameters.get("invitationCode");//邀请码
        String deviceType = parameters.get("deviceType");//设备类型
        
        String extensionCode = parameters.get("businessinvitationCode");
        
        
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "请填写手机号");
			
			return json.toString();	
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号不符合规范");
			
			return json.toString();	
		}
		
		/* 判断手机号码是否存在 */
		if (userAppService.isMobileExists(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码已存在");
			
			return json.toString();	
		}
		
		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "请输入短信验证码");

			return json.toString();
		}
		
		Object obj = Cache.get(mobile + JYSMSModel.MODEL_SEND_CODE.tplId);
		if (obj == null) {
			json.put("code", -1);
			json.put("msg", "短信验证码已失效");

			return json.toString();
		}
		String codeStr = obj.toString();
		
		/** 短信验证码 验证 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(verificationCode)) {
				json.put("code", -1);
				json.put("msg", "短信验证码错误");
				
				return json.toString();
			}
		}
		if (StringUtils.isBlank(password)){
			json.put("code", -1);
			json.put("msg", "请填写密码");

			return json.toString();
		}
		
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			json.put("code", -1);
			json.put("msg", "密码不符合规范");

			return json.toString();
		}
		
		int flagOfRecommendCode = 0;
		/* 验证邀请码 */
		if(StringUtils.isNotBlank(invitationCode)){
			
			if (StrUtil.isMobileNum(invitationCode)) {// CPS邀请码是用户的手机号
				if (common.constants.ModuleConst.EXT_CPS) {// CPS模块是否存在
					/* 判断手机号码是否存在 */
					flagOfRecommendCode = userAppService.isMobileExists(invitationCode) ? 1 : -1;
				}
			} else {
				if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {// 财富圈邀请码
					service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
					ResultInfo result2 = wealthCircleService.isWealthCircleCodeUseful(invitationCode);
					if(result2.code < 0){
						json.put("code", -1);
						json.put("msg", result2.msg);

						return json.toString();
					}
					else if (result2.code == 1) {
						flagOfRecommendCode = 2;
					}
				}
			}
			if (flagOfRecommendCode < 0) {
				json.put("code", -1);
				json.put("msg", "推广码不存在");

				return json.toString();
			}
		}
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}

		ResultInfo result = new ResultInfo();
	
		long supervisorId =0;
		if(StringUtils.isNotBlank(extensionCode)){
			
			supervisorId = supervisorService.findSupervisorByExtension(extensionCode);
					
			if(supervisorId<=0){
				json.put("code", -1);
				json.put("msg", "业务邀请码不存在");
				
				return json.toString();
			}
		}
		
		
		/* 自动生成用户名 */
		String userName = userAppService.userNameFactory(mobile);
		Client client=	Client.getEnum(Integer.valueOf(deviceType));
		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result = userAppService.registering(userName, mobile, password,client, BaseController.getIp(),supervisorId);
		if (result.code < 1) {
			LoggerUtil.info(true, "注册时：%s", result.msg);
			
			json.put("code", -1);
			json.put("msg", result.msg);
			
			return json.toString();
		}else{
			t_user user = (t_user) result.obj;
			//ext_redpacket 红包数据生成 start
			/*if(ModuleConst.EXT_REDPACKET){
				services.ext.redpacket.RedpacketService redpacketService = Factory.getService(services.ext.redpacket.RedpacketService.class);
				ResultInfo resultRed = redpacketService.creatRedpacket(user.id);  //此处不能使用result
				if(resultRed.code < 1){
					LoggerUtil.info(true, "注册成功，生成红包相关数据时出错，数据回滚，%s", resultRed.msg);
					json.put("code", -1);
					json.put("msg", resultRed.msg);
					
					return json.toString();
				} 
			}*/
			//end
		
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				service.ext.experiencebid.ExperienceBidAccountService experienceBidAccountService = Factory.getService(service.ext.experiencebid.ExperienceBidAccountService.class);
				ResultInfo resultExperBid = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(resultExperBid.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", resultExperBid.msg);
					json.put("code", -1);
					json.put("msg", resultExperBid.msg);
					
					return json.toString();
				}
			}
			//end	
			// cps账户开户
			if(ModuleConst.EXT_CPS){
				services.ext.cps.CpsService cpsService = Factory.getService(services.ext.cps.CpsService.class);
				ResultInfo resultCps = cpsService.createCps(invitationCode, user.id);
				if(resultCps.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", resultCps);
					json.put("code", -1);
					json.put("msg", resultCps.msg);
					
					return json.toString();
				}
			}
			
			// 财富圈账号数据创建
			if(ModuleConst.EXT_WEALTHCIRCLE){
				service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
				ResultInfo wealthResultInfo = null ; 
						
				if (flagOfRecommendCode == 2){
					wealthResultInfo = wealthCircleService.creatWealthCircle(invitationCode, user.id);
				}else {
					wealthResultInfo = wealthCircleService.creatWealthCircle(null, user.id);
				}
				if(wealthResultInfo.code < 1){
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", wealthResultInfo.msg);
					json.put("code", -1);
					json.put("msg", wealthResultInfo.msg);
					
					return json.toString();
				}
			}
			
			//end
			
	    	//业务订单号 开户操作
			/*String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_FAILED);
			
			ResultInfo resultRegist = PaymentProxy.getInstance().regist(client.code, serviceOrderNo, user.id);
			
			if (resultRegist.code < 0) {
				LoggerUtil.info(true, resultRegist.msg);
				json.put("code", -1);
				json.put("msg", result.msg);
				
				return json.toString();
			}*/
			
			/* 清除缓存中的验证码 */
			String encryString = Session.current().getId();
	    	Object cache = Cache.get(mobile + encryString + JYSMSModel.MODEL_SEND_CODE.tplId);
	    	if(cache != null){
	    		Cache.delete(mobile + encryString + JYSMSModel.MODEL_SEND_CODE.tplId);
	    		Cache.delete(mobile + JYSMSModel.MODEL_SEND_CODE.tplId);
	    	}
	    	
	    	//注册时添加888红包
	    	/*t_user user1 = (t_user) result.obj;
	    	ResultInfo res = getRedPacket(user1.id);
	    	if (res.code < 0) {
	    		JPA.setRollbackOnly(); 
	    		json.put("code", -1);
				json.put("msg", "注册失败");
				return json.toString();
	    	}*/
	    	//新手注册成功后加金币
	    	t_user user2 = (t_user) result.obj;
	    	t_user_gold res2 = getUserGold(user2.id);
	    	
	    	
	    	json.put("userId", user.appSign);
	    	//json.put("html", resultRegist.obj.toString());
			json.put("code", 1);
			json.put("msg", "注册成功");
			
			return json.toString(); 
		}
		
	}
	/**
	 * 
	 * @Title: getUserGold	
	 * @description: 新手注册成功后加金币
	 * @param userId 
	 * @author HanMeng
	 * @return 
	 * @createDate 2018年10月25日-下午2:55:49
	 */
	public static t_user_gold getUserGold(long userId) {
		t_user_gold usergold = new t_user_gold();
		//获取当前用户id
				Long user_id = FrontBaseController.getCurrUser().id;
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
	
	/***
	 * 
	 * 查询平台logo
	 * @param parameters
	 * @return
	 * @description 
	 *
	 * @author luzhiwei
	 * @createDate 2016-4-14
	 */
	public static String findPlatformIconFilename(Map<String, String> parameters){
		
		return userAppService.findPlatformIconFilename();
	} 
	
	/**
	 * 用户登录（OPT=123）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String logining(Map<String, String> parameters){
		JSONObject json = new JSONObject();
		
		String mobile = parameters.get("mobile");
		String password = parameters.get("password");
		String deviceType = parameters.get("deviceType");
		
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "请输入正确的用户名!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(password)) {
			json.put("code", -1);
			json.put("msg", "请输入密码!");
			
			return json.toString();
		}
		password = Encrypt.decrypt3DES(password, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		
		if (DeviceType.getEnum(Convert.strToInt(deviceType, -99))==null) {
			json.put("code", -1);
			json.put("msg", "请使用移动客户端操作");
			
			return json.toString();
		}
		
		return userAppService.logining(mobile, password, deviceType);
	}

	/**
	 * 修改登录密码（OPT=122）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String updateUserPwd(Map<String, String> parameters) {
		
		JSONObject json = new JSONObject();
		
		String verificationCode = parameters.get("verificationCode");
		String mobile = parameters.get("mobile");
		String scene = parameters.get("scene");
		
		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "验证码不能为空!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码不能为空!");
			
			return json.toString();
		}
		if (!SmsScene.APP_REGISTER.equals(scene) && !SmsScene.APP_FORGET_PWD.equals(scene)) {
	
			json.put("code", -1);
			json.put("msg", "参数scene错误!");
			
			return json.toString();
		
		}
		
		String verify = userAppService.verificationCode(verificationCode, mobile, scene);
		
		Map<String, String> result = YbUtils.jsonToMap(verify);
		
		if (!result.get("code").equals("1")) {
			return verify;
		}
		
		
		String newPassword = parameters.get("newPassword");
		
		if (!StrUtil.isMobileNum(mobile)) {
			json.put("code", -1);
			json.put("msg", "请输入正确手机号!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(newPassword)) {
			json.put("code", -1);
			json.put("msg", "请输入新密码!");
			
			return json.toString();
		}
		newPassword = Encrypt.decrypt3DES(newPassword, ConfConst.ENCRYPTION_APP_KEY_DES);//解密密码
		if (!StrUtil.isValidPassword(newPassword)) {
			json.put("code", -1);
			json.put("msg", "密码格式错误!");
			
			return json.toString();
		}
		long userId = userAppService.queryIdByMobile(mobile);
		if (userId <= 0) {
			json.put("code", -1);
			json.put("msg", "没有该用户!");
			
			return json.toString();
		}
		/** 密码加密 */
		newPassword = Encrypt.MD5(newPassword + ConfConst.ENCRYPTION_KEY_MD5);
		String oldPassWord = userAppService.findUserOldPassWord(userId);
		if(newPassword.equals(oldPassWord)){
			json.put("code", -1);
			json.put("msg", "新密码不能与旧密码相同");
			
			return json.toString();
		}		
		return userAppService.updateUserPwd(mobile, userId, newPassword);
	}

	/**
	 * 验证验证码（OPT=121）
	 *
	 * @param parameters
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年3月31日
	 */
	public static String verificationCode(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String verificationCode = parameters.get("verificationCode");
		String mobile = parameters.get("mobile");
		String scene = parameters.get("scene");
		
		if (StringUtils.isBlank(verificationCode)) {
			json.put("code", -1);
			json.put("msg", "验证码不能为空!");
			
			return json.toString();
		}
		if (StringUtils.isBlank(mobile)) {
			json.put("code", -1);
			json.put("msg", "手机号码不能为空!");
			
			return json.toString();
		}
		if (!SmsScene.APP_REGISTER.equals(scene) && !SmsScene.APP_FORGET_PWD.equals(scene)) {
	
			json.put("code", -1);
			json.put("msg", "参数scene错误!");
			
			return json.toString();
		
		}
		
		
		return userAppService.verificationCode(verificationCode, mobile, scene);
	}

	/**
	 * 资金托管开户
	 *
	 * @param parameters
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年10月17日
	 */
	public static String createAccount(Map<String, String> parameters) {
		JSONObject json = new JSONObject();
		
		String signId = parameters.get("userId");//用户id
		String deviceType = parameters.get("deviceType");//设备类型
		String realName = parameters.get("realName");//真实姓名
		String certNo = parameters.get("certNo");//身份证号
		String beginTime = parameters.get("beginTime");//身份证起始日期
		String endTime = parameters.get("endTime");//身份证截至日期
		String jobType = parameters.get("jobType");//职业类型
		String job = parameters.get("job");//职业描述
		String national = parameters.get("national");//民族
		String postcode = parameters.get("postcode");//邮编
		String address = parameters.get("address");//住址
		
		ResultInfo result = Security.decodeSign(signId, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (result.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		
		if(DeviceType.getEnum(Convert.strToInt(deviceType, -99)) == null){
			 json.put("code", -1);
			 json.put("msg", "请使用移动客户端操作");
			 
			 return json.toString();
		}
    	
		long userId = Long.parseLong(result.obj.toString());
		int client = Convert.strToInt(deviceType, 1);
		
		return userAppService.createAccount(userId, client, realName, certNo, beginTime, endTime, jobType, job, national, postcode, address);
	}
	
	/**
	 * 
	* @Description: TODO(进入自动投标设置页面) 
	* @author lihuijun
	* @date 2017年10月19日
	 */
	public static String toSetAutoInvest(Map<String,String> parameters){
		JSONObject json = new JSONObject();
		
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if(userIdSignDecode.code<1){
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		
		if(autoInvestOption==null){
			json.put("auto_status", 0);
			json.put("min_apr", "");
			json.put("max_period", "");
			json.put("invest_amt", "");
			json.put("code", 1);
			json.put("msg", "");
		}else{
			json.put("auto_status", autoInvestOption.status);
			json.put("min_apr", autoInvestOption.min_apr);
			json.put("max_period", autoInvestOption.max_period);
			json.put("invest_amt", autoInvestOption.amount);
			json.put("code", 1);
			json.put("msg", "");
		}
		
		
		return json.toString();
	}
	
	/**
	 * 
	* @Description: TODO(开启自动投标) 
	* @author lihuijun
	* @date 2017年10月17日
	 */
	public static String openAutoInvest(Map<String, String> parameters){
		JSONObject json =new JSONObject();
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if(userIdSignDecode.code<1){
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		/**获取设备类型*/
		String deviceType = parameters.get("deviceType");
		int client = Integer.parseInt(deviceType);
		String minApr=parameters.get("min_apr");
		double min_apr=0.00;
		if(StringUtils.isNotBlank(minApr)){
			min_apr=Double.parseDouble(minApr);
		}
		String maxPeriod = parameters.get("max_period");
		int max_period = 36;
		if(StringUtils.isNotBlank(maxPeriod)){
			max_period = Integer.parseInt(maxPeriod);
		}
		String investAmt = parameters.get("invest_amt");
		if(StringUtils.isBlank(investAmt)){
			json.put("code", -1);
			json.put("msg", "委托投资金额不能为空");
			return json.toString();
		}
		double invest_amt = Double.parseDouble(investAmt);
		//自动投标有效时长（天）
		String validDaySign=parameters.get("valid_day");
		int valid_day = Integer.parseInt(validDaySign);
		
		ResultInfo result = new ResultInfo();
		
		/** 改版前签署成功委托协议 免验密开启自动投标*/
		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		if (autoInvestOption !=null && autoInvestOption.status == 0){
			result = investService.saveAutoInvest(userId, min_apr, max_period, invest_amt,valid_day);
			if (result.code < 1) {
				json.put("code", -1);
				json.put("msg", "自动投开启失败！");
			}
			json.put("code", 1);
			json.put("msg", "自动投标验密成功！");
			json.put("html", "");
			json.put("type", "oldEntrust");
			return json.toString();
		}
				
		//计算失效时间（yyMMdd）
		String valid_time = ""; 
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		Calendar ca = Calendar.getInstance();
		ca.setTime(now); 
		ca.add(Calendar.DATE, valid_day);
		now = ca.getTime();
		valid_time = sdf.format(now);
				
		/**开启需要验密*/
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTRUST_PROTOCOL_AGREE);
		result = PaymentProxy.getInstance().autoCheckPassword(client, businessSeqNo, userId, ServiceType.ENTRUST_PROTOCOL_AGREE, min_apr, max_period, invest_amt, valid_time, valid_day);
		if(result.code==1){
			json.put("code", 1);
			json.put("msg", "自动投标验密成功！");
			json.put("html", result.obj.toString());
			json.put("type", "newEntrust");
			
		}else{
			json.put("code", -1);
			json.put("msg", "自动投标验密失败！");
		}
		return json.toString();
	}
	
   /**
	* 
	* @Description: TODO(关闭自动投标设置) 
	* @author LiuPengwei
	* @date 2018年1月22日
	 */
	public static String closeAutoInvest(Map<String, String> parameters){
		JSONObject json =new JSONObject();
		
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if(userIdSignDecode.code<1){
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		String deviceType = parameters.get("deviceType");
		int client = Integer.parseInt(deviceType);
		
		//取消委托时间
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String valid_time = sdf.format(now);
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTRUST_PROTOCOL_CANCEL);
		
		ResultInfo result = PaymentProxy.getInstance().autoCheckPassword(client, businessSeqNo, userId,ServiceType.ENTRUST_PROTOCOL_CANCEL, 0, 0, 0, valid_time,0);
		
		if(result.code==1){
			json.put("code", 1);
			json.put("msg", "自动投标验密成功！");
			json.put("html", result.obj.toString());
			
		}else{
			json.put("code", -1);
			json.put("msg", "自动投标验密失败！");
		}
		return json.toString();
	}
	/**
	 * 
	* @Description: TODO(查看委托协议) 
	* @author lihuijun
	* @date 2017年10月20日
	 */
	public static String seeEntrustProtocol(Map<String, String> parameters){
		JSONObject json=new JSONObject();
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if(userIdSignDecode.code<1){
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		t_information investDeal = informationService.findInvestEntrustDeal(userId);
		if(investDeal!=null){
			json.put("html", investDeal.content);
			json.put("htmlTitle", "开启自动投标委托协议");
			json.put("code", 1);
			json.put("msg", "");
		}else{
			json.put("html", "");
			json.put("htmlTitle", "");
			json.put("code", -1);
			json.put("msg", "委托协议不存在");
		}
		return json.toString();
	}

	
	public static String riskAssessment(Map<String, String> parameters){
		JSONObject json=new JSONObject();
		
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if(userIdSignDecode.code<1){
			json.put("code", ResultInfo.LOGIN_TIME_OUT);
			json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		String totalSign = parameters.get("total");
		int total = Integer.parseInt(totalSign);
		int grade = 0; //等级
		String res = "";  //评语
		
		if (total <= 27 && total >= 1) {
			grade = 1;
			res= "评测结果:保守型 低于27分 属于可以承担低风险而作风谨慎类型的投资者.您适合投资于保本为主的投资工具,但您因此会牺牲资本升值的机会" ;
		}else if ( 28<=total && total<=39) {
			res = "评测结果:稳健型 28—39分 属于可以承担低至中等风险类型的投资者.您适合投资于能够权衡保本而亦有若干升值能力的投资工具" ;
			grade = 2;
		}else if (40 <= total && total <= 51) {
			res = "评测结果:成长型 40—51分 属于可以承担中等至高风险类型的投资者.您适合投资于能够为您提供升值能力,而投资价值有波动的投资工具";
			grade = 3;
		}else if (total >= 52){
			res = "评测结果:进取型 大于52分 属于可以承受高风险类型的投资者.适合投资于能够为您提供升值能力而投资价值波动大的投资工具.最坏的情况下,可能失去全部投资本金并需对您投资所导致的任何亏损承担责任" ;
			grade = 4;
		}else {
			res ="您还未参与评测，请点击确定开始" ;
			grade = 0;
		}

		t_user_info userInfo = userInfoService.findByColumn("user_id = ?", userId);
		
		if (userInfo.credit_id > 0){
			json.put("code", -1);
			json.put("msg", "用户已测评成功！");
			json.put("grade", 0);
			return json.toString();
		}
		
		boolean flag = userInfoService.userCredit(userId, grade);				
		
		if (!flag) {
			json.put("code", -1);
			json.put("msg", "填写出错，请重新测评！");
			json.put("grade", 0);
			return json.toString();
		}
		json.put("code", 1);
		json.put("msg", res);
		json.put("grade", grade);
		
		return json.toString();
	}
	/**
	 * 
	 * @Title: shareNum
	 * @description: 每日分享首次得金币
	 *
	 * @param parameters
	 * @return    
	 * @return String   
	 *
	 * @author HanMeng
	 * @createDate 2018年11月1日-下午5:46:36
	 */
	public static String shareNum(Map<String, String> parameters){
		JSONObject json=new JSONObject();
		String userIdSign=parameters.get("userId");
		ResultInfo userIdSignDecode = Security.decodeSign(userIdSign, Constants.USER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_APP_KEY_DES);
		if (userIdSignDecode.code < 1) {
			 json.put("code", ResultInfo.LOGIN_TIME_OUT);
			 json.put("msg", ResultInfo.LOGIN_TIME_OUT_MSG);
			 return json.toString();
		}
		long userId = Long.parseLong(userIdSignDecode.obj.toString());
		
		/** 获取登陆人信息判断t_user_gold是否存在 */
		t_user_gold userGold = userGoldService.getByUserId(userId);
		/** 没有登陆人信息则在t_user_gold表中插入 */
		if (userGold ==  null) {
			UserGoldCtrl.saveUserInfo(userId);
			userGold = userGoldService.getByUserId(userId);
		}
		/** 判断是否为当日首次分享 */
		if (userGold.share_num == 0) {
			json.put("code", 1);
			json.put("msg", "今日首次分享得金币！");
			/** 首次分享后，数据库分享次数+1 */
			userGoldService.alterShareNum(userId);
			/** 首次分享后，加一个金币 */
			userGoldService.updateUserGold(userId);
		}else{
			/** 不是首次分享，返回今日分享得金币次数已用完  */
			json.put("code", -1);
			json.put("msg", "今日分享得金币次数已用完！");
		}
		return json.toString();
		
	}
}
