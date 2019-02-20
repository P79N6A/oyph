package controllers.front.activity.shake;

import org.apache.commons.lang.StringUtils;

import com.shove.security.Encrypt;

import common.constants.ConfConst;
import common.constants.ModuleConst;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import common.utils.StrUtil;
import common.utils.captcha.CaptchaUtil;
import controllers.common.FrontBaseController;
import models.activity.shake.entity.t_shake_record;
import models.common.bean.CurrUser;
import models.common.entity.t_user;
import play.cache.Cache;
import play.libs.Codec;
import play.mvc.Scope.Session;
import service.ext.experiencebid.ExperienceBidAccountService;
import service.ext.wealthcircle.WealthCircleService;
import services.activity.shake.ShakeRecordService;
import services.common.UserService;
import services.ext.cps.CpsService;
import services.ext.redpacket.RedpacketService;

public class RedPacketShareCtrl extends FrontBaseController {
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static ShakeRecordService shakeRecordService = Factory.getService(ShakeRecordService.class);
	
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);

	public static void redPacketSharePre(long awardsRecord) {
		
		/* 验证码 */
		String randomId = Codec.UUID();
    	
		/* 禁止该页面进行高速缓存 */
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0");
		
		render(randomId, awardsRecord);
	}
	
	/**
     * 用户注册
     * @description 
     * 
     * @param mobile 手机号码
     * @param password 密码
     * @param randomId 验证码密文
     * @param code 验证码
     * @param smsCode 短信验证码
     * @param recommendCode 邀请码
     * @param scene 场景
     * @param readandagree 协议勾选
     * 
     * @author Chenzhipeng
     * @createDate 2015年12月16日
     */
	public static void registeringes(String mobile, String password, String randomId, 
			String code, String smsCode, String scene, boolean readandagree, long awardsRecord) {
		ResultInfo result = new ResultInfo();
		
		/* 回显数据 */
		flash.put("mobile", mobile);
		flash.put("code", code);
		flash.put("smsCode", smsCode);
		
		if (StringUtils.isBlank(mobile)) {
			flash.error("请填写手机号");
			
			shareDefeatedPre();
		}
		
		/* 验证手机号是否符合规范 */
		if (!StrUtil.isMobileNum(mobile)) {
			flash.error("手机号不符合规范");
			
			shareDefeatedPre();
		}
		
		/* 判断手机号码是否存在 */
		if (userService.isMobileExists(mobile)) {
			flash.error("手机号码已存在");

			shareDefeatedPre();
		}
		
		if (StringUtils.isBlank(code)) {
			flash.error("请输入验证码");
			
			shareDefeatedPre();
		}
		
		/* 根据randomid取得对应的验证码值 */
		String RandCode = CaptchaUtil.getCode(randomId);
		
		/*验证码失效 */
		if (RandCode == null) {
			flash.error("验证码失效");
			
			shareDefeatedPre();
		}
		
		/* 图形验证码验证  */ 
		if(ConfConst.IS_CHECK_PIC_CODE){
			if (!code.equalsIgnoreCase(RandCode)) {
				flash.error("验证码错误");

				shareDefeatedPre();
			}
		}
		
		/* 短信验证码验证 */
		if (StringUtils.isBlank(smsCode)) {
			flash.error("请输入短信验证码");
			
			shareDefeatedPre();
		}
		Object obj = Cache.get(mobile + scene);
		if (obj == null) {
			flash.error("短信验证码已失效");
			
			shareDefeatedPre();
		}
		String codeStr = obj.toString();
		
		/** 短信验证码 验证 */
		if(ConfConst.IS_CHECK_MSG_CODE){
			if (!codeStr.equals(smsCode)) {
				flash.error("短信验证码错误");
				
				shareDefeatedPre();
			}
		}
		
		/* 验证密码是否符合规范 */
		if (!StrUtil.isValidPassword(password)) {
			flash.error("密码不符合规范");

			shareDefeatedPre();
		}
		
		int flagOfRecommendCode = 0;
		/*验证邀请码 
		if(StringUtils.isNotBlank(recommendCode)){
			
			if (StrUtil.isMobileNum(recommendCode)) {// CPS邀请码是用户的手机号
				if(common.constants.ModuleConst.EXT_CPS){ //CPS模块是否存在
					 判断手机号码是否存在 
					flagOfRecommendCode = userService.isMobileExists(recommendCode) ? 1:-1;
				}
			} else {
				if (common.constants.ModuleConst.EXT_WEALTHCIRCLE) {// 财富圈邀请码
					service.ext.wealthcircle.WealthCircleService wealthCircleService = Factory.getService(service.ext.wealthcircle.WealthCircleService.class);
					ResultInfo result2 = wealthCircleService.isWealthCircleCodeUseful(recommendCode);
					if (result2.code == 1) {
						flagOfRecommendCode = 2;
					}
				}
			}
			if (flagOfRecommendCode < 0) {
				flash.error("推广码不存在");

				registerPre();
			}
		}*/
		
		long supervisorId =0;
		/*if(StringUtils.isNotBlank(extensionCode)){
			
			supervisorId = supervisorService.findSupervisorByExtension(extensionCode);
					
			if(supervisorId<=0){
				flash.error("业务邀请码不存在");

				registerPre();
			}
		}*/
		
		/* 协议是否勾选 */
		if(!readandagree){
			flash.error("协议未勾选");

			shareDefeatedPre();
		}
		
		/* 自动生成用户名 */
		String userName = userService.userNameFactory(mobile);
		
		/* 密码加密 */
		password = Encrypt.MD5(password + ConfConst.ENCRYPTION_KEY_MD5);
		result =  userService.registering(userName, mobile, password, Client.PC, getIp(),supervisorId);
		if (result.code < 1) {
			flash.error(result.msg);
			LoggerUtil.info(true, result.msg);
			
			shareDefeatedPre();
		}else{
			//ext_redpacket 分享红包数据生成 start
			if(ModuleConst.EXT_REDPACKET){
				t_user user = (t_user) result.obj;
				ResultInfo result2 = shareRedPacket(user.id,awardsRecord);  //此处不能使用result
				if(result2.code < 1){
					
					LoggerUtil.info(true, "注册成功，生成红包相关数据时出错，数据回滚，%s", result2.msg);	
					flash.error(result2.msg);
					shareDefeatedPre();
				} 
			}
			//end
			
			
			
			//experienceBid 体验金发放 start
			if(ModuleConst.EXT_EXPERIENCEBID){
				t_user user = (t_user) result.obj;
				ExperienceBidAccountService experienceBidAccountService = Factory.getService(ExperienceBidAccountService.class);
				ResultInfo result3 = experienceBidAccountService.createExperienceAccount(user.id);  //此处不能使用result
				if(result3.code < 1){
					LoggerUtil.info(true, "注册成功，发放体验金到账户时，%s", result3.msg);
					
					flash.error(result3.msg);
					shareDefeatedPre();
				}
			}
			//end
			
			// cps账户开户
			if(ModuleConst.EXT_CPS){
				t_user user = (t_user) result.obj;
				CpsService cpsService = Factory.getService(CpsService.class);
				
				ResultInfo result4 = null;
				
				result4 = cpsService.createCps(null, user.id);
						
				if(result4.code < 1){
					LoggerUtil.info(true, "注册成功，生成cps推广相关数据时出错，数据回滚，%s", result4.msg);
					
					flash.error(result4.msg);
					shareDefeatedPre();
				}
			}
			//end
			
			// 财富圈账号数据创建
			if(ModuleConst.EXT_WEALTHCIRCLE){
				t_user user = (t_user) result.obj;
				WealthCircleService wealthCircleService = Factory.getService(WealthCircleService.class);
				ResultInfo result5 = null ; 
						
				result5 = wealthCircleService.creatWealthCircle(null, user.id);
				
				if(result5.code < 1){
					LoggerUtil.info(true, "注册成功，生成财富圈推广相关数据时出错，数据回滚，%s", result5.msg);
					
					flash.error(result5.msg);
					shareDefeatedPre();
				}
			}
			//end
			
			/* 清除缓存中的验证码 */
			String encryString = Session.current().getId();
	    	Object cache = Cache.get(mobile + encryString + scene);
	    	if(cache != null){
	    		Cache.delete(mobile + encryString + scene);
	    		Cache.delete(mobile + scene);
	    	}
			
	    	redPacketShareSuccessPre();
		}
	}
	
	public static void redPacketShareSuccessPre() {
		
		render();
	}
	
	public static void shareDefeatedPre() {
		
		render();
	}
	
	
	
	/**
	 * 用户获得分享红包
	 * 
	 * @param activityId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月1日
	 */
	public static ResultInfo shareRedPacket(long userId, long activityId){
		ResultInfo result = new ResultInfo();
		
		/** 分享获取红包准备 */
		result = shakeRecordService.gainRedPacketPrepare(activityId);
		
		if(result.code < 1){

			return result;
		}
		
		t_shake_record record =(t_shake_record) result.obj;
		
		
		/** 用户获得红包 */
		boolean creatShareRedPacket = redpacketService.creatShareRedPacket(userId, record);
		
		if (!creatShareRedPacket){
			
			result.code = -1;
			
			return result;
		}
		
		
		result = shakeRecordService.addGainCountOfRedPacket(activityId , userId);
		
		if(result.code < 1){

			return result;
		}
		
		result.code = 1;
		result.msg = "获得分享红包成功！";
		
		
		return result;
		
	}
}
