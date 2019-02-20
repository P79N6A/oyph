package services.core;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import play.mvc.With;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import models.activity.shake.entity.t_user_gold;
import models.common.entity.t_deal_user;
import models.common.entity.t_sms_jy_count;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.common.entity.t_user_info.MemberType;
import models.core.bean.BidInvestRecord;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_auto_bid;
import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_invest;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_invest.TransferStatus;
import models.ext.redpacket.entity.t_add_rate_ticket;
import models.ext.redpacket.entity.t_add_rate_user;
import models.ext.redpacket.entity.t_coupon_user;
import models.ext.redpacket.entity.t_red_packet_user;
import payment.impl.PaymentProxy;
import services.activity.shake.UserGoldService;
import services.base.BaseService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.SmsJyCountService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.CouponService;
import services.ext.redpacket.RedpacketService;
import yb.YbConsts;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;
import common.constants.ConfConst;
import common.enums.Client;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import controllers.front.invest.InvestCtrl;
import daos.activity.shake.UserGoldDao;
import daos.core.AutoBidDao;
import daos.core.AutoInvestSettingDao;
import daos.core.InvestDao;
import daos.ext.redpacket.AddRateUserDao;
import daos.ext.redpacket.RedpacketUserDao;

/**	
 * 投资service
 *
 * @author liudong
 * @createDate 2015年12月15日
 */
public class InvestService extends BaseService<t_invest> {

	protected InvestDao investDao = null;
	
	protected AutoInvestSettingDao autoInvestSettingDao = Factory.getDao(AutoInvestSettingDao.class);
	
	protected AutoBidDao autoBidDao = Factory.getDao(AutoBidDao.class);
	
	protected UserService userService = Factory.getService(UserService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	protected static NoticeService noticeService = Factory.getService(NoticeService.class);
	
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static CouponService couponService = Factory.getService(CouponService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static AddRateUserDao addRateUserDao = Factory.getDao(AddRateUserDao.class);
	
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	protected static UserGoldDao userGoldDao = Factory.getDao(UserGoldDao.class);
	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);
	protected static UserGoldService userGoldService = Factory.getService(UserGoldService.class);
	protected InvestService() {
		investDao = Factory.getDao(InvestDao.class);
		super.basedao = this.investDao;
	}

	/**
	 * 债权转让从原来的标记复制一份新的投资
	 *
	 * @param original
	 * @param newUserid
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月29日
	 */
	public ResultInfo copyToInvest(long original,long debtId, long newUserid,String service_order_no){
		ResultInfo result = new ResultInfo();
		t_invest originalInvest = investDao.findByID(original);
		
		t_invest tInvest = new t_invest();//债权竞拍成功在投资表里面插入新纪录
		tInvest.user_id = newUserid;
		tInvest.time = new Date();
		tInvest.bid_id = originalInvest.bid_id;
		tInvest.amount = originalInvest.amount;
		tInvest.correct_amount = originalInvest.correct_amount;
		tInvest.correct_interest = originalInvest.correct_interest;
		tInvest.setTransfer_status(models.core.entity.t_invest.TransferStatus.NORMAL);
		tInvest.debt_id = debtId;
		tInvest.setClient(originalInvest.getClient()); //债权转让后，重新生成一条新的投资记录，需要把该条投资记录的来源也加进来（用于后台理财情况数据统计分析）
		tInvest.service_order_no = service_order_no;
		tInvest.trust_order_no = originalInvest.trust_order_no;
		
		if(!investDao.save(tInvest)){
			result.code = -1;
			result.msg = "添加投资记录失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg = "添加投资记录成功";
		result.obj = tInvest;
		
		return result;
	}
	
	/**
	 * 投标(准备)
	 *
	 * @param userId 用户id
	 * @param bid 标的
	 * @param investAmt 投标金额
	 * @param client
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo invest(long userId, t_bid bid, double investAmt , double redPackeAmt) {
		ResultInfo result = new ResultInfo();
		
		if (bid == null) {
			throw new InvalidParameterException("bid is null");
		}
		
		/** 不能投资自己发布的借款标  */
		if (bid.user_id == userId) {
			result.code = -1;
			result.msg = "不能投资自己发布的借款标";
			
			return result;
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if(userFund == null){
			result.code = -1;
			result.msg = "获取用户资金信息失败";
			
			return result;
		}
		
		/** 用户资金安全校验 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {
			
			return result;
		}
		
		/** 可用余额是否充足  */
		if (userFund.balance+redPackeAmt < investAmt) {
			
			result.code = -1;
			result.msg = "余额不足";
			return result;
		}
		
		/** 标的是否可投 */
		if (!t_bid.Status.FUNDRAISING.equals(bid.getStatus()) || DateUtil.isDatetimeAfter(new Date(), bid.invest_expire_time)){
			result.code = -1;
			result.msg = "此借款标已经不处于招标状态，请投资其他借款标！";
			
			return result;
		} 
		
		/** 投资金额校验 */
		double leftAmt = bid.amount - bid.has_invested_amount;  //可投金额
		// 可投金额大于或等于最低投标金额时，投资金额必须大于或等于最低投标金额
		if (leftAmt >= bid.min_invest_amount && investAmt <  bid.min_invest_amount) {
			result.code = -1;
			result.msg = "投资金额必须大于或等于最低投标金额！";
			return result;
		}
		// 投资金额 不能大于可投金额
		if(investAmt > leftAmt){
			result.code = -1;
			result.msg = "投资金额必须小于或等于可投金额";
			
			return result;	
		}
		
		// 可投金额小于最低投标金额时，投资金额必须等于可投金额
		if (leftAmt < bid.min_invest_amount && investAmt != leftAmt) {
			result.code = -1;
			result.msg = "当前可投金额小于起投金额，投资金额必须等于可投金额！";
			
			return result;
		}

		result.code = 1;
		result.msg = "投标准备完毕";
				
		return result;
	}

	/**
	 * 投标(执行)
	 *
	 * @param userId 用户ID
	 * @param bidId 标的ID
	 * @param investAmt 投标金额
	 * @param loanServiceFeeDivide 分摊的借款服务费
	 * @param serviceOrderNo 业务订单号
	 * @param trustOrderNo 第三方返回交易订单号
	 * @param client 客户端
	 * @param investType 投标方式
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doInvest(long userId, long bidId, double investAmt, String serviceOrderNo, String trustOrderNo, int client, int investType, long ticketId) {
		ResultInfo result = new ResultInfo();
		
		t_bid bid = bidService.findByID(bidId);
			
		if (bid == null) {
			result.code = -1;
			result.msg = "该借款标不存在,[bidId:"+bidId+"]";
			
			return result;
		}
		
		t_user_fund userFund  = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败,[userId:"+userId+"]";
			
			return result;
		}
		
		/** 超额投标控制  */
		//计算投标进度
		double schedule = Arith.divDown(Arith.mul(Arith.add(bid.has_invested_amount, investAmt), 100.0), bid.amount, 2);
		if (schedule > 100.0) {
			result.code = ResultInfo.OVER_BID_AMOUNT;
			result.msg = "超额投标";
			
			return result;
		}
		
        //更新投标进度和已投金额，并防止并发情况
		result = bidService.updateBidScheduleAndInvestAmt(bid.id, investAmt, schedule);
		if (result.code < 1) {
			
			return result;
		}

		/** 满标: 更新满标时间, 更新标的状态为（复审中） */
		if (bid.amount == (bid.has_invested_amount + investAmt)) {
			result = bidService.bidExpire(bid.id);
			if (result.code<1) { 
				
				return result;
			}
		}

		/** 本地用户资产减钱*/
		boolean freezeAmt = userFundService.userFundFreeze(userId, investAmt);
		if (!freezeAmt) {
			result.code = -1;
			result.msg = "本地用户资产减钱";
			
			return result;
		}
		
		/** 用户资产签名更新  */
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "用户资产签名更新失败，请联系管理员";
			
			return result;
		}
		
		/** 添加用户交易记录*/
		userFund  = userFundService.queryUserFundByUserId(userId);  //刷新用户资金
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("bidNo", bid.bid_no);
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,  //订单号
				userId,
				investAmt,   
				userFund.balance,
				userFund.freeze, 
				t_deal_user.OperationType.INVEST_FREEZE,
				summaryPrams);

		if (!addDeal) {
			result.code = -1;
			result.msg = "添加交易记录失败！";
			
			return result;
		}
		/** 如果投标金额大于50000，赠送一次抽奖机会（加1金币） */
		if (investAmt>=50000) {
			userGoldService.updateUserGold(userId);
		}
		/** 新用户注册成功后7天内投资再送1金币 */ 
		Date time  = new Date();		
		
		t_user_info user_info1 = userInfoService.findUserInfoByUserId(userId);
		t_user users =userService.findByID(userId);		
		int i = DateUtil.getDaysDiffFloor(users.time,time);	
		
		MemberType type = user_info1.getMember_type();
			/*注册小于7天且是新会员*/	
		if (i <= 7 &&  type.code==0) {
			
			/** 更新会员性质 */
			boolean memberType = userInfoService.updateUserMemberType(userId, t_user_info.MemberType.FINANCIAL_MEMBER);
			if (!memberType) {
				result.code = -1;
				result.msg = "更新会员状态时失败";
					
				return result;	
			}
			userGoldService.updateUserGold(userId);
		}else{
			/** 更新会员性质 */
			boolean memberType1 = userInfoService.updateUserMemberType(userId, t_user_info.MemberType.FINANCIAL_MEMBER);
			if (!memberType1) {
				result.code = -1;
				result.msg = "更新会员状态时失败";
					
				return result;	
			}
		}
		
		/** 计算分摊的借款服务费 */		
		double loanServiceFeeDivide = Arith.div(Arith.mul(investAmt, bid.loan_fee), bid.amount, 2);
		
		t_invest invest = new t_invest();
		invest.user_id = userId;
		invest.time = new Date();
		invest.bid_id = bid.id;
		invest.amount = investAmt;
		invest.loan_service_fee_divide = loanServiceFeeDivide;
		invest.setClient(Client.getEnum(client));
		invest.service_order_no = serviceOrderNo;
		invest.trust_order_no = trustOrderNo;
		invest.setInvest_type(InvestType.getEnum(investType));
		
		invest.rate_ticket_id = ticketId;
		
		t_invest isSuccess = investDao.saveT(invest);
		if (isSuccess == null) {
			result.code = -1;
			result.msg = "保存投资信息失败";
			
			return result;
		}
		
		//更新加息券状态
		if (ticketId != 0) {
			
			addRateTicketService.updateTicketStatus(ticketId, isSuccess.id);
		}
		
		//加息券创造投资
		//获得锁定时间
		if (ticketId>0){
			t_add_rate_user addRateUser = addRateUserDao.findByID(ticketId);
			addRateUser.create_invest = investAmt;
			addRateUser.save();
			t_add_rate_ticket rateTicket = t_add_rate_ticket.findById(addRateUser.ticket_id);
			if (rateTicket != null) {
				if (rateTicket.lock_day != null){
					rateTicket.lock_time = DateUtil.addDay(new Date(), rateTicket.lock_day );
					rateTicket.save();
				}
			}
		}
		String bid_name = bid.title;
		if (bid.title.indexOf("【") != -1) {
			bid_name = bid_name.replace("【", "");
		}
		if (bid.title.indexOf("】") != -1) {
			bid_name = bid_name.replace("】", "");
		}
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userFund.name);
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid_name);
		param.put("amount", investAmt);
		param.put("period_num", bid.period);
		param.put("period_unit", bid.getPeriod_unit().getShowValue());
		param.put("apr", bid.apr);
		param.put("repayment_type", bid.getRepayment_type().key);
		//购买成功
		//创蓝接口
		//noticeService.sendSysNotice(userId, NoticeScene.INVEST_SUCC, param);
		//焦云接口
		t_user user = t_user.findById(userId);
		Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_INVEST_SUCC);
		if (flag) {
			noticeService.sendSysNotice(userId, NoticeScene.INVEST_SUCC, param,JYSMSModel.MODEL_INVEST_SUCC);
		}
		
		result.code = 1;
		result.msg = "投标成功";
		
		/** 首次理财金额分类,激活红包  */
		t_user_info user_info = userInfoService.findUserInfoByUserId(userId);
		
		if (investAmt>=5000 && investAmt<10000 ) {
			user_info.first_money = "1";
			user_info.save();
		} else if (investAmt>=10000 && investAmt<20000) {
			user_info.first_money = "2";
			user_info.save();
			
		} else if (investAmt>=20000 && investAmt<30000) {
			user_info.first_money = "3";
			user_info.save();
			
		} else if (investAmt>=30000 && investAmt<40000) {
			user_info.first_money = "4";
			user_info.save();
			
		} else if (investAmt>=40000 && investAmt<80000) {
			user_info.first_money = "5";
			user_info.save();
			
		} else if (investAmt>=80000) {
			user_info.first_money = "6";
			user_info.save();
		
		} else {
			user_info.first_money = "0";
			user_info.save();
		}
				
		
		return result;
	}
	
	

	/**
	 * 使用红包投标回调
	 * @param userId
	 * @param bidId
	 * @param investAmt
	 * @param serviceOrderNo
	 * @param trustOrderNo
	 * @param client
	 * @param investType
	 * @param redPacketId
	 * @param redPacketAmt
	 * @return
	 */
	public ResultInfo doInvestRed(long userId, long bidId, double investAmt, String serviceOrderNo, String trustOrderNo, int client,
			int investType , long redPacketId) {
		ResultInfo result = new ResultInfo();
		
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			result.code = -1;
			result.msg = "该借款标不存在,[bidId:"+bidId+"]";
			
			return result;
		}
		
		t_red_packet_user redPacket = redpacketUserDao.findByID(redPacketId);
		double redPacketAmt = redPacket.amount;
		
		t_user_fund userFund  = userFundService.queryUserFundByUserId(userId);
		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败,[userId:"+userId+"]";
			
			return result;
		}
		
		/** 超额投标控制  */
		//计算投标进度
		double schedule = Arith.divDown(Arith.mul(Arith.add(bid.has_invested_amount, investAmt), 100.0), bid.amount, 2);
		if (schedule > 100.0) {
			result.code = ResultInfo.OVER_BID_AMOUNT;
			result.msg = "超额投标";
			
			return result;
		}
        //更新投标进度和已投金额，并防止并发情况
		result = bidService.updateBidScheduleAndInvestAmt(bid.id, investAmt, schedule);
		if (result.code < 1) {
			
			return result;
		}

		/** 满标: 更新满标时间, 更新标的状态为（复审中） */
		if (bid.amount == (bid.has_invested_amount + investAmt)) {
			result = bidService.bidExpire(bid.id);
			if (result.code<1) { 
				
				return result;
			}
		}

		/** 本地用户资产减钱*/
		boolean freezeAmt = userFundService.userFundFreeze(userId, investAmt-redPacketAmt);
		if (!freezeAmt) {
			result.code = -1;
			result.msg = "本地用户资产减钱";
			
			return result;
		}
		
		/** 用户资产签名更新  */
		result = userFundService.userFundSignUpdate(userId);
		if (result.code < 1) {
			result.code = -1;
			result.msg = "用户资产签名更新失败，请联系管理员";
			
			return result;
		}
		
		/** 添加用户交易记录*/
		userFund  = userFundService.queryUserFundByUserId(userId);  //刷新用户资金
		Map<String, String> summaryPrams = new HashMap<String, String>();
		summaryPrams.put("bidNo", bid.bid_no);
		boolean addDeal = dealUserService.addDealUserInfo(
				serviceOrderNo,  //订单号
				userId,
				investAmt-redPacketAmt,   
				userFund.balance,
				userFund.freeze, 
				t_deal_user.OperationType.INVEST_FREEZE,
				summaryPrams);

		if (!addDeal) {
			result.code = -1;
			result.msg = "添加交易记录失败！";
			
			return result;
		}
		
		/** 更新会员性质 */
		boolean memberType = userInfoService.updateUserMemberType(userId, t_user_info.MemberType.FINANCIAL_MEMBER);
		if (!memberType) {
			result.code = -1;
			result.msg = "更新会员状态时失败";
				
			return result;
		}
		
		/** 计算分摊的借款服务费 */		
		double loanServiceFeeDivide = Arith.div(Arith.mul(investAmt, bid.loan_fee), bid.amount, 2);
		
		t_invest invest = new t_invest();
		invest.user_id = userId;
		invest.time = new Date();
		invest.bid_id = bid.id;
		invest.amount = investAmt;
		invest.loan_service_fee_divide = loanServiceFeeDivide;
		invest.setClient(Client.getEnum(client));
		invest.service_order_no = serviceOrderNo;
		invest.trust_order_no = trustOrderNo;
		invest.setInvest_type(InvestType.getEnum(investType));
		
		invest.red_packet_id = redPacketId;
		invest.red_packet_amount = redPacketAmt;
		
		//红包创造投资
		//红包获得锁定时间
		if (redPacketId>0){
			t_red_packet_user redpacketInvest = redpacketUserDao.findByID(redPacketId);
			redpacketInvest.create_invest = investAmt;
			if (redpacketInvest.lock_day != null) {
				if (redpacketInvest.lock_day == -1) {
					if (bid.getPeriod_unit().code == 1) {
						redpacketInvest.lock_time = DateUtil.addDay(new Date(), bid.period);
					}
					if (bid.getPeriod_unit().code == 2) {
						redpacketInvest.lock_time = DateUtil.addMonth(new Date(), bid.period);
					}
				}
				if (redpacketInvest.lock_day >= 0) {
					redpacketInvest.lock_time = DateUtil.addDay(new Date(), redpacketInvest.lock_day);
				}
			}
			
			redpacketInvest.save();
		}
		
		
		
		invest = investDao.saveT(invest);
		if (invest == null) {
			result.code = -1;
			result.msg = "保存投资信息失败";
			
			return result;
		}
		
		
		
		// 如果红包状态已经被修改那么此次投资就当超标处理
		int count = redpacketService.updateRedPacketStatus(redPacketId, t_red_packet_user.RedpacketStatus.RECEIVED.code, t_red_packet_user.RedpacketStatus.ALREADY_USED.code) ;
		if( count <= 0 ){
			result.code =  -1316 ;
			result.msg = "修改红包状态失败";
			
			return result;
		}

		String bid_name = bid.title;
		if (bid.title.indexOf("【") != -1) {
			bid_name = bid_name.replace("【", "");
		}
		if (bid.title.indexOf("】") != -1) {
			bid_name = bid_name.replace("】", "");
		}
		
		/** 发送通知 */
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_name", userFund.name);
		param.put("bid_no", bid.bid_no);
		param.put("bid_name", bid_name);
		param.put("amount", investAmt);
		param.put("period_num", bid.period);
		param.put("period_unit", bid.getPeriod_unit().getShowValue());
		param.put("apr", bid.apr);
		param.put("repayment_type", bid.getRepayment_type().key);
		//购买成功
		//创蓝接口
		//noticeService.sendSysNotice(userId, NoticeScene.INVEST_SUCC, param);
		//焦云接口
		t_user user = t_user.findById(userId);
		Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_INVEST_SUCC);
		if (flag) {
			noticeService.sendSysNotice(userId, NoticeScene.INVEST_SUCC, param,JYSMSModel.MODEL_INVEST_SUCC);
		}
		result.code = 1;
		result.msg = "投标成功";
		
		return result;
	}

	/**
	 * 更新某个投资的转让状态
	 *
	 * @param investId 投资id
	 * @param status 更新后的状态
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	public boolean updateTransferStatus(Long investId,TransferStatus status){
		t_invest invest = investDao.findByID(investId);
		invest.setTransfer_status(status);
		
		return investDao.save(invest);
	}
	
	
	/**
	 * 根据主键查找实体
	 */
	public t_invest findByID(long investId) {

		return investDao.findByID(investId);
	}

	/**
	 * 获取某个用户的总投资金额(由债权转让过来的不算)
	 *
	 * @param userId 用户id
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月7日
	 */
	public double findTotalInvest(Long userId){
		
		return	investDao.findTotalInvest(userId);
	}
	
	/**
	 * 获取投资金额
	 * @param startTime
	 * @param endTime
	 * @param type
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2016年1月25日
	 *
	 */
	public double findTotalInvestByDate(String startTime, String endTime,
			int type){
		
		return investDao.findTotalInvestByDate(startTime, endTime, type);
	}
	
	/**
	 * 查询一个借款标的所有投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月20日
	 */
	public BidInvestRecord listOfBidInvestRecords(long id) {
		
		return investDao.listOfBidInvestRecords(id);
	}
	/**
	 * 用于标的详情的投资列表
	 *
	 * @param currPage
	 * @param pageSize
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月27日
	 */
	public PageBean<BidInvestRecord> pageOfBidInvestDetail(int currPage, int pageSize, long bidId){
		return investDao.pageOfBidInvestDetail(currPage, pageSize, bidId);
	}
	
	/**
	 * 分页查询  某个用户的投资记录 
	 * 
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param userId 用户userId
	 * @return
	 *
	 * @author liudong
	 * @createDate 2015年12月17日
	 */
	public PageBean<UserInvestRecord> pageOfUserInvestRecord(int currPage, int pageSize,long userId) {
		return investDao.pageOfUserInvestRecord1(currPage, pageSize, userId);
	}
	
	/**
	 * 获取标的的投资记录
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public List<t_invest> queryBidInvest(long bidId) {
		
		List<t_invest> list = new ArrayList<t_invest>();
		
		list = investDao.findListByColumn("bid_id = ? ", bidId);
		return list;
	}
	
	public List<t_invest> queryBidInvestAmount(long bidId) {
		
		List<t_invest> list = new ArrayList<t_invest>();
		
		list = investDao.findListByColumn("bid_id = ? ORDER BY user_id ", bidId);
		return list;
	}

	/**
	 * 查询最新理财信息
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryNewInvestList() {
		List<Map<String, Object>> newInvests = investDao.queryNewInvestList();
		List<Map<String, Object>> investsList = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> map : newInvests) {
			Map<String, Object> investsMap = new HashMap<String, Object>();
			Date time =  (Date)map.get("time");
			investsMap.put("time", DateUtil.getTimeLine(time));
			investsMap.put("photo", map.get("photo"));
			investsMap.put("amount", map.get("amount"));
			investsMap.put("bid_no", NoUtil.getBidNo(Long.parseLong(map.get("id").toString())));
			investsList.add(investsMap);
		}
		
		return investsList;
	}

	/**
	 * 查询每周用户投资金额排行
	 * @return
	 *
	 * @author Chenzhipeng
	 * @createDate 2015年12月29日
	 *
	 */
	public List<Map<String, Object>> queryWeekInvestList() {
		
		return investDao.queryWeekInvestList();
	}
	
	/**
	 * 查询每月用户投资金额排行
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年1月6日
	 *
	 */
	public List<Map<String, Object>> queryMouthInvestList() {
		
		return investDao.queryMouthInvestList();
	}

	/**
	 * 根据user_id查询t_auto_invest_setting
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public t_auto_invest_setting findAutoInvestByUserId(long userId){
		
		return autoInvestSettingDao.findAutoInvestOptionByUserId(userId);
	}
	
	/**
	 * 保存自动投标设置信息
	 * 
	 * @param userId
	 * @param minApr
	 * @param maxPeriod
	 * @param investAmt
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo saveAutoInvest(long userId, double minApr, int maxPeriod, double investAmt, int valid_day){
		ResultInfo result = new ResultInfo();
		
		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		
		autoInvestOption.time = new Date();
		autoInvestOption.user_id = userId;
		autoInvestOption.min_apr = minApr;
		autoInvestOption.max_period = maxPeriod;
		autoInvestOption.amount = investAmt;
		autoInvestOption.status = 1;
		autoInvestOption.valid_day = valid_day;
		
		boolean isSuccess = autoInvestSettingDao.save(autoInvestOption);
		if (!isSuccess) {
			result.code = -1;
			result.msg = "保存自动投标信息失败";
			
			return result;
		}
		
		result.msg = "开启自动投标成功";
				
		return result;
	}
	
	/**
	 * 保存自动投标设置信息
	 * 
	 * @param userId
	 * @param minApr
	 * @param maxPeriod
	 * @param investAmt
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo newAutoInvest(long userId, double minApr, int maxPeriod, double investAmt, int valid_day, String protocolNo){
		ResultInfo result = new ResultInfo();
		
		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		
		if (autoInvestOption == null) {
			autoInvestOption = new t_auto_invest_setting();
		}
		autoInvestOption.time = new Date();
		autoInvestOption.user_id = userId;
		autoInvestOption.min_apr = minApr;
		autoInvestOption.max_period = maxPeriod;
		autoInvestOption.amount = investAmt;
		autoInvestOption.status = 1;
		autoInvestOption.valid_day = valid_day ;
		autoInvestOption.protocol_no = protocolNo ;
		
		boolean isSuccess = autoInvestSettingDao.save(autoInvestOption);
		if (!isSuccess) {
			result.code = -1;
			result.msg = "保存自动投标信息失败";
			
			return result;
		}
		if(result.code!=2){
			result.code = 1;
		}
		result.msg = "开启自动投标成功";
				
		return result;
	}
	
	/**
	 * 更新自动投标状态
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	public ResultInfo updateAutoInvestStatus(long userId){
		ResultInfo result = new ResultInfo();
		
		int i = autoInvestSettingDao.updateAutoInvestStatus(userId, 1);
		if(i != 1){
			result.code = -1;
			result.msg = "开启自动投标失败";
			
			return result;
		}	
		
		result.code = 1;
		result.msg = "开启自动投标成功";
				
		return result;
	}
	
	/**
	 * 关闭自动投标
	 * 
	 * @param userId
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public ResultInfo closeAutoInvest(long userId){
		ResultInfo result = new ResultInfo();
		
		t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
		if(autoInvestOption == null)
		{
			result.code = 1;
			result.msg = "委托协议用户信息不存在！";
			return result;
		}
		
		int i = autoInvestSettingDao.updateAutoInvestStatus(userId, 2);
		if(i != 1){
			result.code = -1;
			result.msg = "关闭自动投标失败";
			
			return result;
		}	
		
		result.code = 1;
		result.msg = "关闭自动投标成功";
				
		return result;
	}
	
	public int deleteAutoInvestSetting(long userId){
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("userId", userId);
		int row=autoInvestSettingDao.deleteBySQL("delete from t_auto_invest_select where user_id=:userId", condition);
		
		return row;
	}
	
	/**
	 * 查出所有开启自动投标的用户
	 * 
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public List<Object> queryAllAutoUser(){
		
		return  autoInvestSettingDao.queryAllAutoUser();
	}
	
	/**
	 * 判断该借款标是否超过95%
	 * 
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean judgeSchedule(long bidId){
		Double schedule = bidService.findBidSchedule(bidId);
		
		if(null == schedule){
			return false;
		}
		
		if(schedule >= 95){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 将用户排到自动投标队尾
	 * 
	 * @param userId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public void updateUserAutoBidTime(long userId){
		autoInvestSettingDao.updateUserAutoBidTime(userId);
	}
	
	/**
	 * 判断用户是否已经自动投过当前标
	 * 
	 * @param userId
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean hasAutoInvestTheBid(long userId, long bidId){
		
		boolean flag=false;
		t_auto_bid bid = autoBidDao.findAutoBidByUserIdAndBidId(userId, bidId);
		
		if(bid != null){
			flag=true;
		}
		
		return flag;
	}
	
	/**
	 * 标的是否符合用户设置的条件
	 * 
	 * @param autoOptions
	 * @param unit
	 * @param bidId
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public boolean judgeBidByParam(t_auto_invest_setting autoOptions, int unit, long bidId){
		Long bid = autoInvestSettingDao.findBidByParam(autoOptions, unit, bidId);
		
		if(null == bid){
			return false;
		}
		
		if(bid < 1){
			return false;
		}
		
		return true;
	}
	
	/**
	 * 计算最后投标金额
	 * 
	 * @param investAmt
	 * @param schedule
	 * @param amount
	 * @param hasInvestedAmt
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public int calculateBidAmount(double investAmt, double schedule,double amount,double hasInvestedAmt) {
		
		int maxBidAmt = (int) (amount * 0.2);
		
		if (schedule < 95) {
			while (investAmt > maxBidAmt) {
				investAmt = investAmt - 50;
			}

			do {
				schedule = Arith.divDown(Arith.mul(Arith.add(hasInvestedAmt, investAmt), 100.0), amount, 2);
				if (schedule > 95) {
					investAmt = investAmt - 50;
				}
			} while (schedule > 95);
		}
		
		return (int) investAmt;
	}
	
	/**
	 * 计算自动投标份数
	 * 
	 * @param amount
	 * @param averageAmt
	 * @return
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public int calculateFinalInvestAmount(double amount, double averageAmt){
		int temp = 0;
		temp = (int) (amount/averageAmt);
		return temp;
	}
	
	/**
	 * 增加用户自动投标记录
	 * 
	 * @param userId
	 * @param bidId
	 * 
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月25日
	 */
	public void addAutoBidRecord(long userId, long bidId){
		
		t_auto_bid bid = new t_auto_bid();
		
		bid.bid_id = bidId;
		bid.time = new Date();
		bid.user_id = userId;
		
		autoBidDao.save(bid);
	}
	/**
	 * 根据标的id查询，未完成的债权转让投资用户
	 * 
	 * @param bidId 标的id
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年3月25日
	 *
	 */
	public List<t_invest> queryInvestTransfer(long bidId){
		
		return investDao.findListByColumn("transfer_status=? AND bid_id=?", t_invest.TransferStatus.TRANSFERING.code ,bidId);
	}

	
	
	/**
	 * 查询该用户的投资金额
	 * @param userId
	 * @return
	 */
	public double queryAmountByUserId(long supervisorId){
		Double amount = new Double(0);
		String sql ="select sum(amount) from t_invest where   transfer_status=0 and   user_id in (select id from t_user where supervisor_id="+supervisorId+")";
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		amount = (Double) investDao.findSingleByHQL(sql, args);
		
		return amount==null?0:amount;
	}
	
	
	/**
	 * 查询本月的投资金额
	 * @param superviosrId
	 * @return
	 */
	public double queryMonthAmount(long superviosrId){
		Double amount = new Double(0);
		String sql ="select sum(amount) from t_invest where  transfer_status=0 and user_id in (select id from t_user where supervisor_id="+superviosrId+") and time>=:startTime ";
		
		Map<String, Object> args = new HashMap<String, Object>();
		
		Calendar date = Calendar.getInstance(); 
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		int year = date.get(Calendar.YEAR);
	    int month = (date.get(Calendar.MONTH))+1;
	    Date sdate = new Date();
		try {
			sdate = sdf.parse(year+"-"+month+"-01");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		args.put("startTime",sdate);
		
		amount = (Double) investDao.findSingleByHQL(sql, args);
		
		return amount==null?0:amount;
	}

	
	/**
	 * 投标使用红包校验
	 * @param userId
	 * @param investAmt
	 * @param period_unit 标的借款期限单位。1:天;2:月
	 * @param period  标的借款期限
	 * 
	 * @return
	 * @author LiuPengwei
	 * @createDate 2018年5月23日16:24:15
	 */
	public ResultInfo investRedPacket( long userId , long redPacketId , double investAmt, int period, int period_unit) {
		ResultInfo result = new ResultInfo();
		t_red_packet_user redPacketUser =  redpacketService.queryRedPacket(redPacketId);
		
		if(redPacketUser.user_id != userId){
			result.code = -3 ;
			result.msg = "该用户没有此红包" ;
			
			return result;
		}
		
		if( !(investAmt >= redPacketUser.use_rule) ){
			result.code = -3 ;
			result.msg = "投资金额小于红包使用金额" ;
			
			return result ;
		}
		
		if(DateUtil.isDateAfter(new Date(), redPacketUser.limit_time)){
			result.code = -3 ;
			result.msg = "投资使用的红包已过使用期" ;
			
			redpacketService.updateRedPacketStatus(redPacketId, t_red_packet_user.RedpacketStatus.RECEIVED.code, t_red_packet_user.RedpacketStatus.EXPIRED.code) ;
			
			return result ;
		}
		
		if(redPacketUser.getStatus().code != t_red_packet_user.RedpacketStatus.RECEIVED.code){
			result.code = -3 ;
			result.msg = "红包状态错误" ;
			
			return result ;
		}
		
		//期限单位为天
		if (period_unit == 1){
			result.code = -3 ;
			result.msg = "使用红包标的期限不满足" ;
			
			return result ;
		}
		
		//标的期限大于红包使用期限
		if (redPacketUser.bid_period > period){
			result.code = -3 ;
			result.msg = "使用红包标的期限不满足"+redPacketUser.bid_period+"个月以上" ;
			
			return result ;
		}
		
		result.code = 1 ;
		result.obj = redPacketUser ;
		return result ;
	}

	/**
	 * 投标使用加息劵校验
	 * @param userId
	 * @param investAmt
	 * @return
	 */
	public ResultInfo investCoupon( long userId , long couponId , double investAmt) {
		ResultInfo result = new ResultInfo();
		t_coupon_user couponUser =  couponService.findByIds(couponId);
		
		if(couponUser.user_id != userId){
			result.code = -3 ;
			result.msg = "该用户没有此加息券" ;
			
			return result;
		}
		
		if(DateUtil.isDateAfter(new Date(), couponUser.limit_time)){
			result.code = -3 ;
			result.msg = "投资使用的加息券已过使用期" ;
			
			redpacketService.updateRedPacketStatus(couponId, t_coupon_user.CouponStatus.RECEIVED.code, t_coupon_user.CouponStatus.EXPIRED.code) ;
			
			return result ;
		}
		
		if(couponUser.getStatus().code != t_red_packet_user.RedpacketStatus.RECEIVED.code){
			result.code = -3 ;
			result.msg = "加息券状态错误" ;
			
			return result ;
		}
		
		result.code = 1 ;
		result.obj = couponUser ;
		return result ;
	}
	
	/**
	 * 红包活动排行榜
	 *
	 * @return
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月14日
	 */
	public List<Map<String, Object>> queryActivityInvestList() {
		
		return investDao.queryActivityInvestList();
	}
	
	/**
	 * 查询累计成交人次
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findCountOfInvests() {
		
		return investDao.findCountOfInvests();
	}
	
	/**
	 * 查询上月交易笔数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findMonCountByMonth(String beginTime, String endTime) {
		
		return investDao.findMonCountByMonth(beginTime, endTime);
	}
	
	/**
	 * 上月投资前十大人
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 */
	public List<Map<String, Object>> queryMouthInvestListByMonth(String beginTime, String endTime) {
		
		return investDao.queryMouthInvestListByMonth(beginTime, endTime);
	}
	
	/**
	 * 查询月借款人数
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月20日
	 *
	 */
	public int findBorrowerCountByMonth(String beginTime, String endTime) {
		
		return investDao.findBorrowerCountByMonth(beginTime, endTime);
	}
	
	/**
	 * 查询某个人投资总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTotalAmounts(long userId) {
		
		return investDao.findTotalAmounts(userId);
	}
	
	/**
	 * 查询某个人今日投资总额
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2018年01月23日
	 *
	 */
	public double findTodayAmounts(long userId, String startTimes) {
		
		return investDao.findTodayAmounts(userId, startTimes);
	}
	
	/**
	 * 查询标的所有投资人(去重)
	 * 
	 * @param bidId
	 * @return
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月29日 09:53:22
	 */
	public List<Long> findBidInvests (long bidId){
		
		return investDao.findBidInvests(bidId);
	}
	/**
	 * 
	 * @Title: findListByBid
	 * @description 根据标号查询出所有投资信息（根据User_id分组）
	 * @param bidId
	 * @return
	 * List<t_invest>
	 * @author likai
	 * @createDate 2018年11月21日 上午10:33:19
	 */
	public List<t_invest> findListByBid(long bidId) {
		return investDao.findListByBid(bidId);
	}

}
