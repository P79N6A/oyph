package common.ext.cps.utils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.helper.StringUtil;

import common.constants.MallConstants;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import daos.ext.cps.CpsUserDao;
import jobs.rewardGrant;
import models.activity.shake.entity.t_user_gold;
import models.beans.MallGoods;
import models.beans.MallScroeRecord;
import models.common.entity.t_jdecard;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.common.entity.t_user_vip_grade;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import models.entity.t_mall_scroe_record;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_integral;
import models.ext.cps.entity.t_cps_packet;
import models.ext.cps.entity.t_cps_rate;
import models.ext.cps.entity.t_cps_user;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_red_packet_account;
import models.ext.redpacket.entity.t_red_packet_user;
import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;
import play.db.jpa.JPA;
import play.mvc.Http.StatusCode;
import services.activity.shake.UserGoldService;
import services.common.UserInfoService;
import services.common.UserVIPGradeService;
import services.ext.cps.CpsActivityService;
import services.ext.cps.CpsIntegralService;
import services.ext.cps.CpsPacketService;
import services.ext.cps.CpsRateService;

/**
 * 奖励发放
 * @author guoShiJie
 * @createDate 2018.6.13
 * */
public class RewardGrantUtils {

	protected static UserVIPGradeService userVIPGradeService = Factory.getService(UserVIPGradeService.class);
	
	protected static CpsPacketService cpsPacketService = Factory.getService(CpsPacketService.class);
	
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	protected static CpsRateService cpsRateService = Factory.getService(CpsRateService.class);
	
	protected static CpsIntegralService cpsIntegralService = Factory.getService(CpsIntegralService.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	/**
	 * 新用户注册给红包
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public static void grantPacketToNewCustomer (t_cps_activity cpsActivity,t_user user,ResultInfo error) {
		if (cpsActivity == null || user == null || error == null) {
			error.code = -1;
			error.msg = "奖励未发放";
			return ;
		}
		int res1 = RewardGrantUtils.updateCpsUser(user.id,cpsActivity.id);
		if (res1 != 1) {
			error.code = res1;
			error.msg = "更新cps账号失败";
			return ;
		}
		List<t_cps_packet> cpsPacket = cpsPacketService.queryCpsPacketByCpsIdAndType(cpsActivity.id,0,0);
		if(cpsPacket == null) {
			error.code = -1;
			error.msg = "查询cps红包失败";
			return ;
		}
		int res = RewardGrantUtils.saveRedPacket(cpsPacket, user.id,"register_red_packet");
		
		int res2 = RewardGrantUtils.toOldCustomerRedPacket(cpsActivity,user,0,1,null);
		
		if (res2 != 1 || res != 1) {
			JPA.setRollbackOnly();
			error.msg = "发放奖励失败";
			error.code = -1;
		}
		error.code = 1;
		error.msg = "新用户注册发放奖励成功！";
		return ;
	}
	
	/**新用户首投
	 * @param cpsActivity cps 活动
	 * @param user 用户
	 * @param error resultInfo 对象
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static void newCustomerFirst(t_cps_activity cpsActivity,t_user user,t_bid bid ,double amount,ResultInfo error) {
		if (cpsActivity == null || user == null || error == null || bid == null) {
			error.code = -1;
			return ;
		}
		//首投标识(1红包 2 加息卷 3积分)
		if (cpsActivity.first_type == 1) {
			error.code = RewardGrantUtils.toOldCustomerRedPacket(cpsActivity, user,2,1,amount);
		}
		if (cpsActivity.first_type == 2) {
			error.code = RewardGrantUtils.toOldCustomerAddRateTicket(cpsActivity, user, "首投发放", 2, 1,amount);
		}
		if (cpsActivity.first_type == 3) {
			error.code = RewardGrantUtils.toOldCustomerIntegral(cpsActivity, user, MallConstants.INVEST,1,"CPS推广首投赠送",amount);
		}
		
		if (error.code == 1) {
			error.msg = "首投奖励发放成功";
		}else{
			error.msg = "首投奖励发放失败";
		}
		return ;
	}
	
	/**新用户复投给老客户积分
	 * @param cpsActivity cps 活动
	 * @param user 用户
	 * @param error resultInfo对象
	 * @param amount 投资金额
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static void newCustomerNotFirst (t_cps_activity cpsActivity,t_user user,t_bid bid,double amount,ResultInfo error) {
		if (cpsActivity == null || user == null || error == null || bid == null) {
			error.code = -1;
			return ;
		}
		error.code = RewardGrantUtils.toOldCustomerIntegral(cpsActivity, user, bid , amount );
		if (error.code == 1) {
			error.msg = "复投发放奖励成功";
		}else {
			error.msg = "复投发放奖励失败";
		}
	}
	/**
	 * 新用户注册并实名
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static void registerAndRealName (t_cps_activity cpsActivity,t_user user,ResultInfo error) {
		if (cpsActivity == null || user == null || error == null) {
			error.code = -1;
			return ;
		}
		//注册实名标识(1红包 2 加息卷 3积分)
		if (cpsActivity.register_type == 1) {
			error.code = RewardGrantUtils.toOldCustomerRedPacket(cpsActivity, user,1,1,null);
		}
		
		if (cpsActivity.register_type == 2) {
			error.code = RewardGrantUtils.toOldCustomerAddRateTicket(cpsActivity, user, "CPS首投发放", 1, 1,null);
		}
		
		if (cpsActivity.register_type == 3) {
			error.code = RewardGrantUtils.toOldCustomerIntegral(cpsActivity, user, MallConstants.REGISTER_REALNAME,0,"CPS推广注册并实名赠送",null);
		}
		if (error.code == 1) {
			error.msg = "注册并实名奖励发放成功";
		}else{
			error.msg = "注册并实名奖励发放失败";
		}
	}
	/**给老客户红包
	 * @param cpsActivity cps活动
	 * @param user 用户
	 * @param type 类型 (0注册 1注册并实名 2首投)
	 * @param status 状态(0新客户 1老客户)
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int toOldCustomerRedPacket(t_cps_activity cpsActivity,t_user user,int type,int status,Double amount) {
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if(cpsUser == null) {
			return -1;
		}
		/**老客户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
		List<t_cps_packet> cpsPacket = cpsPacketService.queryCpsPacketByCpsIdAndType(cpsActivity.id,type,status);
		if (cpsPacket == null) {
			return -1;
		}
		int res = RewardGrantUtils.saveRedPacket(cpsPacket, cpsUser.spreader_id ,"to_old_customer_red_packet");
		if (amount == null) {
			return res;
		}
		if (res != 1) {
			return -1;
		}
		return cpsUserDao.updateCpsUserRecord(user.id, amount, 0.0);
		
	}
	/**
	 * 
	 * @Title: toOldCustomerGold
	 * 
	 * @description 给老用户金币 
	 * @param  user
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2018年11月4日-下午4:15:51
	 */
	public static int toOldCustomerGold(t_user user){
		//被推广人(新用户)
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if (cpsUser == null) {
			return -1;
		}
		/** 老用户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
//		t_user spreadUser = t_user.findById(oldCustomerId);
		t_user_gold userGold = new t_user_gold();
		t_user_gold userGolds =  userGoldService.getByUserId(oldCustomerId);
		if(userGolds == null){
			//如果t_user_gold表中没有老用户的金币信息，则插入一条
			userGold.user_id = oldCustomerId;
			userGold.gold = 1;
			userGold.Time = new Date();
			userGold.save();
			
		}else{
			//如果t_user_gold表中有老用户的金币信息，则在金币的基础上+1
			userGolds.user_id = oldCustomerId;
			userGolds.gold = userGolds.gold + 1;
			userGolds.Time = new Date();
			userGolds.save();
		}
		
		return 1;
	}
	/**
	 * 
	 * @Title: toOldAndNewECard
	 * 
	 * @description 邀请新用户首投>=5000元给老用户京东E卡 
	 * @param user
	 * @return int    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年2月18日-上午10:12:20
	 */
	public static int toOldAndNewECard(t_user user){
		//被推广人(新用户)
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if (cpsUser == null) {
			return -1;
		}
		/** 老用户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
		//京东E卡表
		t_jdecard jdecard = new t_jdecard();
		//查询老用户信息
		t_user_info oldUserInfo = userInfoService.findUserInfoByUserId(oldCustomerId);
		if (oldUserInfo == null) {
			return -1;
		}else{
			jdecard.user_id = oldCustomerId;
			jdecard.user_name = oldUserInfo.name;
			//联系电话
			jdecard.mobile = oldUserInfo.mobile;
			//E卡面额
			jdecard.denomination = 50;
			jdecard.win_type = 1;
			jdecard.grant_status = 0;
			jdecard.time = new Date();
			jdecard.save();
		}
	
		return 1;
	}
	/**给老客户积分(按照指定的百分比发放)
	 * @param cpsActivity cps活动
	 * @param user 对象
	 * @param type 类型(0注册 1注册并实名 2首投)
	 * @param invest 投资表
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int toOldCustomerIntegral(t_cps_activity cpsActivity,t_user user,t_bid bid,Double amount) {
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if(cpsUser == null) {
			return -1;
		}
		/**老客户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
		t_user spreadUser = t_user.findById(oldCustomerId);
		Date today = new Date();
		//复投给老客户积分
		int getScore = 0;
		double amountMoney = amount;
		if (cpsActivity.cutoff_time == null) {
			return -1;
		}
		Date limitTime = DateUtil.addDay(cpsActivity.time, cpsActivity.cutoff_time);
		if (!DateUtil.isDateAfter(limitTime, today)) {
			return -1;
		}
		Double score = cpsActivity.integral_ratio * amountMoney;
		String str = score.toString();
		//String str1 = str.substring(0, str.indexOf("."))+str.substring(str.indexOf(".") + 1);
		String str1 = str.substring(0, str.indexOf("."));
		getScore = Integer.parseInt(str1);
		
		String description ="CPS推广"+ MallConstants.STR_INVEST+bid.title;
		int res = RewardGrantUtils.saveScroeRecord(MallConstants.INVEST,getScore, spreadUser, amountMoney,description);
		if (res != 1) {
			return -1;
		}
		if (DateUtil.isDateAfter(cpsActivity.end_time, new Date())&& cpsActivity.getIs_use().code ) {
			return cpsUserDao.updateCpsUserRecord(user.id, amountMoney, 0.0);
		}
		return res;
	}
	/**给老客户积分(固定值发放)*/
	public static int toOldCustomerIntegral(t_cps_activity cpsActivity,t_user user,int scroeRecordType ,int type,String description,Double amount) {
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if(cpsUser == null) {
			return -1;
		}
		/**老客户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
		t_user spreaderUser = t_user.findById(oldCustomerId);
		List<t_cps_integral> cpsIntegral = cpsIntegralService.queryIntegralByConditions(cpsActivity.id, type);
		int res = RewardGrantUtils.saveCpsIntegral(scroeRecordType,cpsIntegral, spreaderUser, type, description);
		if (amount != null){
			if (res != 1) {
				return -1;
			}
			return cpsUserDao.updateCpsUserRecord(user.id, amount, 0.0);
		}
		
		return res;
	}
	/**给老客户加息券
	 * @param cpsActivity cps活动
	 * @param user 用户对象
	 * @param channel 发放渠道
	 * @param type 类型 (0注册 1注册并实名 2首投)
	 * @param status 状态(0新客户 1老客户)
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int toOldCustomerAddRateTicket(t_cps_activity cpsActivity,t_user user,String channel,int type,int status,Double amount) {
		t_cps_user cpsUser = cpsUserDao.findByUserId(user.id);
		if(cpsUser == null) {
			return -1;
		}
		/**老客户id*/
		Long oldCustomerId = cpsUser.spreader_id;
		if (oldCustomerId == null || oldCustomerId == 0) {
			return -1;
		}
		List<t_cps_rate> cpsRate = cpsRateService.queryCpsRateByCondition(cpsActivity.id, type, status);
		if (cpsRate == null) {
			return -1;
		}
		int res = RewardGrantUtils.saveAddRateTicket(cpsRate, cpsUser.spreader_id, channel);
		if (amount == null) {
			return res;
		}
		if (res != 1) {
			return -1;
		}
		return cpsUserDao.updateCpsUserRecord(user.id, amount, 0.0);
	}
	/** 
	 * 用户获取红包
	 * @param cpsPacket t_cps_packet对象
	 * @param userId 用户id
	 * @param redPacketName 红包名称
	 * @param _key红包的唯一标识符
	 * @author guoShiJie
	 * */
	public static int  saveRedPacket(List<t_cps_packet> cpsPacket,long userId,String _key) {
		Date today = new Date();
		if (cpsPacket == null) {
			
			return -1;
		}
		for (t_cps_packet packet : cpsPacket) {
			t_red_packet_user redpacket_user = new t_red_packet_user();
			redpacket_user.time = today;
			redpacket_user._key = _key;
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
			if (res != 1) {
				
				return -1;
			}
			int res1 = RewardGrantUtils.saveRedPacketDetail(redpacket_user);
			if (res1 != 1) {
				
				JPA.setRollbackOnly();
				return -1;
			}
		}
		LoggerUtil.info(false,"用户获取红包成功");
		return 1;
		
	}
	
	/**
	 * 保存红包详细信息
	 * @author guoShiJie
	 * */
	public static int saveRedPacketDetail(t_red_packet_user redpacket_user) {
		if (redpacket_user == null) {
			return -1;
		}
		try {
			redpacket_user.save();
		} catch (Exception e) {
			LoggerUtil.info(true,"新用户注册 保存红包信息时: %s", e.getMessage());
			return -2;
		}
		return 1;
	}
	
	/**
	 *  查询t_red_packet_account 
	 *  @param userid 用户id
	 *  */
	public static int queryPacketAccount (long userId ) {
		t_red_packet_account account = t_red_packet_account.find("select new t_red_packet_account(id,time,user_id,balance) from t_red_packet_account where user_id = ?", userId).first();
		if (account != null) {
			return 1;
		}
		t_red_packet_account redAccount = new t_red_packet_account(new Date(),userId,0.00);
		
		return RewardGrantUtils.savePacketAccountDetail(redAccount);
	}
	
	/**
	 * 保存t_red_packet_account
	 * @author guoShiJie
	 * */
	public static int savePacketAccountDetail(t_red_packet_account redAccount) {
		if (redAccount == null) {
			return -1;
		}
		try {
			redAccount.save();
			return 1;
		} catch (Exception e) {
			LoggerUtil.info(true,"新用户注册 保存t_red_packet_account: %s", e.getMessage());
			return -2;
		}
	}
	
	/**
	 * 用户获取加息券
	 * @param cpsRate t_cps_rate对象
	 * @param userId 用户id
	 * @param channel 发放渠道
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public static int saveAddRateTicket(List<t_cps_rate> cpsRate,long userId,String channel) {
		if (cpsRate == null) {
			return -1;
		}
		Date today = new Date();
		for (t_cps_rate rateCps : cpsRate) {
			t_add_rate_ticket addRate = new t_add_rate_ticket();
			addRate.act_id = rateCps.t_cps_id;
			addRate.apr = rateCps.count;
			addRate.day = rateCps.allotte_day;
			addRate.amount = rateCps.condition_money;
			addRate.small = 0;
			addRate.large = 0;
			addRate.type = 2;
			addRate.lock_day = rateCps.lock_day;
			int res1 = RewardGrantUtils.saveAddRateTicketDetail(addRate, channel);
			if (res1 != 1) {
				return res1;
			}
			t_add_rate_ticket rate = MallGoods.queryLastAddRateTicket();
			if (rate == null) {
				return -1;
			}
			t_add_rate_user rateUser = new t_add_rate_user();
			rateUser.stime = today;
			rateUser.etime = DateUtil.addDay(today, rate.day);
			rateUser.ticket_id = rate.id;
			rateUser.user_id = userId;
			rateUser.status = 1;
			rateUser.channel = channel;
			int res = RewardGrantUtils.saveAddRateUserDetail(rateUser, channel);
			if (res != 1) {
				JPA.setRollbackOnly();
				return -2;
			}
		}
		LoggerUtil.info(false,"用户获取加息券成功");
		return 1;
	}
	
	/**
	 * 保存获取t_add_rate_ticket对象
	 * @param addRate t_add_rate_ticket表对象
	 * @param channel 发放渠道
	 * @author guoShiJie
	 * @createDate 2018.6.13
	 * */
	public static int saveAddRateTicketDetail(t_add_rate_ticket addRate,String channel) {
		if (addRate == null) {
			return -1;
		}
		try {
			addRate.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			return -2;
		}
		return 1;
	}
	
	/**
	 * 保存t_add_rate_user对象
	 * @param rateUser t_add_rate_user对象
	 * @param channel 发放渠道
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int saveAddRateUserDetail(t_add_rate_user rateUser,String channel) {
		if (rateUser ==  null) {
			return -1;
		}
		try {
			rateUser.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			return -2;
		}
		return 1;
	}
	
	/**
	 * 保存赠送积分记录
	 * @param scroe 获取积分
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int saveScroeRecord(int scroeRecordType,int scroe,t_user user,Double amount,String description) {
		t_user_vip_grade vip = userVIPGradeService.findByColumn(" id = ( select vip_grade_id from t_user_info where user_id = ? ) ", user.id);
		t_mall_scroe_record scroeRecord = new t_mall_scroe_record();
		scroeRecord.user_id = user.id;
		scroeRecord.user_name = user.name;
		scroeRecord.time = new Date();
		scroeRecord.type = scroeRecordType;
		scroeRecord.relation_id = null;
		scroeRecord.scroe = (int)(+scroe*(vip==null?1:vip.give_integral==null?1:vip.give_integral));
		scroeRecord.status = MallConstants.STATUS_SUCCESS;
		scroeRecord.quantity = amount;
		scroeRecord.description = description;
		scroeRecord.remark = null;
		return RewardGrantUtils.saveScroeRecordDetail(scroeRecord);
	}
	
	
	/**
	 * 保存赠送积分记录
	 * @author guoShiJie
	 * @createDate 2018.6.18
	 * */
	public static int saveScroeRecordDetail(t_mall_scroe_record scroeRecord) {
		if (scroeRecord == null) {
			return -1;
		}
		try {
			scroeRecord.save();
			return 1;
		} catch (Exception e) {
			JPA.setRollbackOnly();
			return -2;
		}
	}
	
	/** 
	 * 更新t_cps_user表
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int updateCpsUser (long user_id,long cps_activity_id) {
		t_cps_user cpsUser = cpsUserDao.findByUserId(user_id);
		if (cpsUser == null) {
			return -1;
		}
		cpsUser.activity_id = cps_activity_id;
		try {
			cpsUser.save();
		} catch (Exception e) {
			JPA.setRollbackOnly();
			return -2;
		}
		return 1;
	}
	
	/**
	 * 用户获取积分
	 * @param type 0实名并注册1首投
	 * @author guoShiJie
	 * @createDate 2018.6.14
	 * */
	public static int saveCpsIntegral(int scroeRecordType,List<t_cps_integral> cpsIntegral,t_user user,int type,String description){
		if (cpsIntegral == null || user == null) {
			return -1;
		}
		for (t_cps_integral integral : cpsIntegral) {
			
			int res = RewardGrantUtils.saveScroeRecord(scroeRecordType,integral.integral, user, null,description+integral.integral+"积分");
			if (res != 1) {
				JPA.setRollbackOnly();
				return -2;
			}
		}
		return 1;
	}
}
