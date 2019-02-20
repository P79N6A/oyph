package controllers.front.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrUser;
import models.common.entity.t_recharge_user;
import models.common.entity.t_user_info;
import models.common.entity.t_user_info.MemberType;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;

import org.apache.commons.lang.StringUtils;

import play.mvc.With;
import services.common.NoticeService;
import services.common.RechargeUserService;
import services.common.UserInfoService;
import services.ext.redpacket.RedpacketService;
import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.constants.ext.RedpacketKey;
import common.enums.NoticeScene;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import controllers.common.FrontBaseController;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import daos.ext.redpacket.RedpacketUserDao;

/**
 * 前台-我的财富-我的奖励-红包
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月15日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyRedpacketCtrl extends FrontBaseController {

	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	/**
	 * 前台-我的财富-我的奖励-红包
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月15日
	 */
	public static void showMyRedpacketsPre(){
		
		boolean flag = updateRedpacket();
		if (!flag) {
			LoggerUtil.error(true, "更新用户红包状态时出错，数据回滚");

			render();
		}
		
		CurrUser currUser = getCurrUser();
		long userId = currUser.id;
		List<t_red_packet_user> userRedpackets = redpacketService.queryRedpacketsByUserid(userId);
		if (userRedpackets == null || userRedpackets.size() == 0 ) {
		
			render();
		}
		
		Map<String, t_red_packet_user> userRedpacketMap = new HashMap<String, t_red_packet_user>();
		
		int countUnreceived = 0;//可领红包数
		double amontReceived = 0.0;//红包账户余额
		
		
		for (t_red_packet_user red_packet_user : userRedpackets) {
			userRedpacketMap.put(red_packet_user._key, red_packet_user);
			
			/** 查询多少个未领取状态的红包  */
			if (RedpacketStatus.UNRECEIVED.equals(red_packet_user.getStatus())) {
				countUnreceived++;
			}
		}

		
		t_red_packet_account red_packet_account = redpacketService.findAccountByUserid(userId);
		amontReceived = red_packet_account.balance;
		
		
		/** 获得推广用户红包   */
		List<t_red_packet_user> generalizeList = redpacketUserDao.findListByColumn("red_packet_name=? and user_id=?", "推广用户红包",userId);

		/** 获得红包  */
		List<t_red_packet_user> shakeRedPacketList = redpacketUserDao.findListByColumn("user_id=?",userId);
		
		
		render(countUnreceived,amontReceived,userRedpacketMap,generalizeList,shakeRedPacketList);
	
	}
	
	/** 更新红包状态 */
	public static boolean updateRedpacket() {
		
		CurrUser currUser = getCurrUser();
		long userId = currUser.id;
		
		ResultInfo result = new ResultInfo();
		
	/*	
		//开户红包
		if (StringUtils.isNotBlank(currUser.payment_account)) {
			result = redpacketService.activatRedpacket(RedpacketKey.ACCOUNT,userId);
			if(result.code == 1){
				t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.ACCOUNT);
				Map<String, Object> sceneParame = new HashMap<String, Object>();
				sceneParame.put("user_name", currUser.name);
				sceneParame.put("amount", redpacket.amount);
				noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_IPS, sceneParame);
			}
			if (result.code < 1) {
				
				return false;
			}
		
		}
		
		//实名认证红包
		if (currUser.is_real_name) {
			result = redpacketService.activatRedpacket(RedpacketKey.REALNAME,userId);
			if(result.code == 1){
				t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.REALNAME);
				Map<String, Object> sceneParame = new HashMap<String, Object>();
				sceneParame.put("user_name", currUser.name);
				sceneParame.put("amount", redpacket.amount);
				noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_REALNAME_VERIFIED, sceneParame);
			}
			if (result.code < 1) {
				
				return false;
			}
		}
		
		//绑定邮箱红包
		if (currUser.is_email_verified) {
			result = redpacketService.activatRedpacket(RedpacketKey.BIND_EMAIL,userId);
			if(result.code == 1){
				t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.BIND_EMAIL);
				Map<String, Object> sceneParame = new HashMap<String, Object>();
				sceneParame.put("user_name", currUser.name);
				sceneParame.put("amount", redpacket.amount);
				noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_BIND_EMAIL, sceneParame);
			}
			if (result.code < 1) {
				
				return false;
			}
		}
		
		//绑定银行卡红包
		if (currUser.is_bank_card) {
			result = redpacketService.activatRedpacket(RedpacketKey.BIND_CARD,userId);
			if(result.code == 1){
				t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.BIND_CARD);
				Map<String, Object> sceneParame = new HashMap<String, Object>();
				sceneParame.put("user_name", currUser.name);
				sceneParame.put("amount", redpacket.amount);
				noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_BIND_CARD, sceneParame);
			}
			if (result.code < 1) {
				
				return false;
			}
		}
		
		//首次充值
		int countDealOfUser = rechargeUserService.countDealOfUser(userId, t_recharge_user.Status.SUCCESS);
		if (countDealOfUser >= 1) {
			result = redpacketService.activatRedpacket(RedpacketKey.FIRST_RECHARGE,userId);
			if(result.code == 1){
				t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.FIRST_RECHARGE);
				Map<String, Object> sceneParame = new HashMap<String, Object>();
				sceneParame.put("user_name", currUser.name);
				sceneParame.put("amount", redpacket.amount);
				noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_RECHARGE, sceneParame);
			}
			if (result.code < 1) {
				
				return false;
			}
		}
		*/
		//首次理财红包
		t_user_info user_info = userInfoService.findUserInfoByUserId(userId);
		MemberType member_type = user_info.getMember_type();
		if (user_info.first_money != null){ 
			if (user_info.first_money.equals("1")) {
				result = redpacketService.activatRedpacket(RedpacketKey.ACCOUNT,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.ACCOUNT);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
			
			if (user_info.first_money.equals("2")) {
				result = redpacketService.activatRedpacket(RedpacketKey.REALNAME,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.REALNAME);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
			
			if (user_info.first_money.equals("3")) {
				result = redpacketService.activatRedpacket(RedpacketKey.BIND_EMAIL,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.BIND_EMAIL);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
			
			if (user_info.first_money.equals("4")) {
				result = redpacketService.activatRedpacket(RedpacketKey.BIND_CARD,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.BIND_CARD);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
			
			if (user_info.first_money.equals("5")) {
				result = redpacketService.activatRedpacket(RedpacketKey.FIRST_RECHARGE,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.FIRST_RECHARGE);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
			
			if (user_info.first_money.equals("6")) {
				result = redpacketService.activatRedpacket(RedpacketKey.FIRST_INVEST,userId);
				if(result.code == 1){
					t_red_packet_user redpacket = redpacketService.findRedpacketByUser(userId, RedpacketKey.FIRST_INVEST);
					Map<String, Object> sceneParame = new HashMap<String, Object>();
					sceneParame.put("user_name", currUser.name);
					sceneParame.put("amount", redpacket.amount);
					noticeService.sendSysNotice(userId, NoticeScene.REDPACKET_FIRST_INVEST, sceneParame);
				}
				if (result.code < 1) {
					
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 前台-我的财富-我的奖励-红包-领取红包
	 *
	 * @param key 带领取红包关键字
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	@SimulatedCheck
	public static void receiveRedpacket(String key){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(key)) {

			result.msg = "系统错误!";
			renderJSON(result);
		}
		
		result = redpacketService.receiveRedpacket(key, getCurrUser().id);
		if (result.code < 1) {
			LoggerUtil.error(true, "领取红包时出错，原因：%s", result.msg);
		}
		
		renderJSON(result);
	}
	
	/**
	 * 前台-我的财富-我的奖励-红包-申请兑换
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	@SimulatedCheck
	@PaymentAccountCheck
	public static void applayConversion(){
		ResultInfo result = new ResultInfo();
		result = redpacketService.applayConversion(getCurrUser().id);
		if (result.code < 1) {
			
			LoggerUtil.error(true, "红包余额申请兑换时出错，原因：%s", result.msg);
			renderJSON(result);
		}
		
		result.code = 1;
		result.msg = "红包余额申请兑换";
		renderJSON(result);
	}
}
