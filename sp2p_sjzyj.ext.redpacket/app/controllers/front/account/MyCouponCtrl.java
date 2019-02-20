package controllers.front.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.bean.CurrUser;
import models.common.entity.t_recharge_user;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.common.entity.t_user_info.MemberType;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_coupon_user;
import models.ext.redpacket.entity.t_coupon_user.CouponStatus;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;

import org.apache.commons.lang.StringUtils;

import play.mvc.With;
import services.common.NoticeService;
import services.common.RechargeUserService;
import services.common.UserInfoService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.CouponService;
import services.ext.redpacket.RedpacketService;
import common.annotation.PaymentAccountCheck;
import common.annotation.SimulatedCheck;
import common.constants.ModuleConst;
import common.constants.ext.CouponKey;
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
 * 前台-我的财富-我的奖励-加息券
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年7月24日
 */
@With({AccountInterceptor.class,SimulatedInterceptor.class})
public class MyCouponCtrl extends FrontBaseController {

	
	protected static RechargeUserService rechargeUserService = Factory.getService(RechargeUserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static CouponService couponService = Factory.getService(CouponService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	/**
	 * 前台-我的财富-我的奖励-加息券
	 *
	 * @author niu
	 * @createDate 2017.11.02
	 */
	public static void showMyCouponsPre(){
		
		CurrUser currUser = getCurrUser();
		long userId = currUser.id;
		
		List<AddRateTicketBean> tickets = addRateTicketService.listOfAddRateTicketByUserId(userId);

		render(tickets);
	
	}
	
	/** 
	 * 激活加息券状态
	 * 
	 * @author LiuPengwei
	 * @createDate 2017年7月24日
	 */
	private static boolean updateCoupon() {
		
		CurrUser currUser = getCurrUser();
		long userId = currUser.id;
		
		ResultInfo result = new ResultInfo();
		
		
		/** 需要在特定时间内，累计充值新老用户才可获得加息券   */
		String startTime = "2010-00-00" ;
		String endTime = "2010-00-00" ;
		/** 查询用户时间内充值累计金额*/
		double totalRechargeUser = rechargeUserService.findUseridTotalRechargeByDate(startTime, endTime, userId);
		if (totalRechargeUser >= 10000) {
			//加息券数据生成start
			if(ModuleConst.EXT_COUPON){
				ResultInfo result1 = couponService.creatCoupon(userId,CouponKey.ONE);  //此处不能使用result
				if(result1.code < 1){
					return false;
				} 
			/** 同时满足两个条件激活第二个   */
				if (totalRechargeUser >= 40000) {
						ResultInfo result2 = couponService.creatCoupon(userId,CouponKey.TWO);  //此处不能使用result
						if(result2.code < 1){
							return false;
						} 
						
						if (totalRechargeUser >= 90000) {
							ResultInfo result3 = couponService.creatCoupon(userId,CouponKey.THREE);  //此处不能使用result
							if(result3.code < 1){
								return false;
							} 
						}	
					}
			}
		} 
		return true;
	}
	
	/**
	 * 前台-我的财富-我的奖励-加息券-领取加息券
	 *
	 * @param key 带领取加息券关键字
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月25日
	 */
	@SimulatedCheck
	public static void receiveCoupon(String key){
		ResultInfo result = new ResultInfo();
		
		if (StringUtils.isBlank(key)) {

			result.msg = "系统错误!";
			renderJSON(result);
		}
		
		result = couponService.receiveCoupon(key, getCurrUser().id);
		if (result.code < 1) {
			LoggerUtil.error(true, "领取加息券时出错，原因：%s", result.msg);
		}
		
		renderJSON(result);
	}
	
	
}
