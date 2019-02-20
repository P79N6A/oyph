package controllers.front.invest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.StringUtil;

import com.shove.Convert;

import models.common.bean.CurrUser;
import models.common.bean.UserFundInfo;
import models.common.entity.t_advertisement;
import models.common.entity.t_advertisement.Location;
import models.common.entity.t_user_info.MemberType;
import models.common.entity.t_user_profession;
import models.common.entity.t_information;
import models.common.entity.t_loan_track;
import models.common.entity.t_quota;
import models.common.entity.t_recharge_user;
import models.common.entity.t_risk_pic;
import models.common.entity.t_risk_report;
import models.common.entity.t_ssq_user;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.core.bean.BidInvestRecord;
import models.core.bean.BidItemOfSupervisorSubject;
import models.core.bean.DebtTransfer;
import models.core.bean.DebtTransferDetail;
import models.core.bean.InvestReceive;
import models.core.entity.t_bid;
import models.core.entity.t_invest.InvestType;
import models.core.entity.t_product;
import models.ext.cps.entity.t_cps_activity;
import models.ext.cps.entity.t_cps_user;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.ext.redpacket.entity.t_coupon_user;
import models.ext.redpacket.entity.t_market_activity;
import models.ext.redpacket.entity.t_market_invest;
import models.ext.redpacket.entity.t_red_packet;
import models.ext.redpacket.entity.t_red_packet_user;
import models.finance.entity.t_industry_sort;
import models.finance.entity.t_trade_info.TradeType;
import net.sf.json.JSONObject;
import payment.impl.PaymentProxy;
import play.Logger;
import play.mvc.With;
import services.common.AdvertisementService;
import services.common.IndustrySortService;
import services.common.InformationService;
import services.common.LoanTrackService;
import services.common.QuotaService;
import services.common.RiskPicService;
import services.common.RiskReportService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.common.UserProfessionService;
import services.common.UserService;
import services.common.ssqUserService;
import services.core.AuditSubjectBidService;
import services.core.BidItemSupervisorService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import services.ext.cps.CpsActivityService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.CouponService;
import services.ext.redpacket.MarketActivityService;
import services.ext.redpacket.MarketInvestService;
import services.ext.redpacket.RedpacketService;
import services.finance.TradeInfoService;
import yb.YbConsts;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;
import common.FeeCalculateUtil;
import common.annotation.LoginCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.ModuleConst;
import common.constants.ext.CouponKey;
import common.enums.Client;
import common.ext.cps.utils.RewardGrantUtils;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.StrUtil;
import common.utils.TimeUtil;
import common.utils.number.NumberFormat;
import controllers.back.spread.AddRateCtrl;
import controllers.back.spread.RedpacketCtrl;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.common.interceptor.UserStatusInterceptor;
import controllers.payment.yb.YbPaymentRequestCtrl;
import daos.base.BaseDao;
import daos.core.BidDao;
import daos.ext.cps.CpsUserDao;
import daos.ext.redpacket.AddRateUserDao;
import daos.ext.redpacket.RedpacketUserDao;

/**
 * 前台-理财-投资控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({UserStatusInterceptor.class, SubmitRepeat.class,SimulatedInterceptor.class})
public class InvestCtrl extends FrontBaseController {

	protected static InvestService investService = Factory.getService(InvestService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	protected static AuditSubjectBidService auditSujbectBidService = Factory.getService(AuditSubjectBidService.class);
	
	protected static BidItemSupervisorService bidItemSupervisorService = Factory.getService(BidItemSupervisorService.class);
	
	protected static InformationService informationService = Factory.getService(InformationService.class);

	protected static AdvertisementService advertisementService = Factory.getService(AdvertisementService.class);
	
	protected static DebtService debtService = Factory.getService(DebtService.class);
	
	protected static RedpacketService redpacketService = Factory.getService(RedpacketService.class);
	
	protected static RiskReportService riskReportService = Factory.getService(RiskReportService.class);

	protected static CouponService couponService = Factory.getService(CouponService.class);
		
	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);
	
	protected static RedpacketUserDao redpacketUserDao = Factory.getDao(RedpacketUserDao.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	protected static QuotaService quotaService = Factory.getService(QuotaService.class);
	
	protected static LoanTrackService loanTrackService = Factory.getService(LoanTrackService.class);
	
	protected static ssqUserService ssquserService = Factory.getService(ssqUserService.class);
	
	protected static MarketActivityService marketActivityService = Factory.getService(MarketActivityService.class);
	
	protected static MarketInvestService marketInvestService = Factory.getService(MarketInvestService.class);
	
	protected static CpsUserDao cpsUserDao = Factory.getDao(CpsUserDao.class);
	
	protected static CpsActivityService cpsActivityService = Factory.getService(CpsActivityService.class);
	
	protected static TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	
	protected static IndustrySortService industrySortService = Factory.getService(IndustrySortService.class); 
	
	protected static RiskPicService riskPicService = Factory.getService(RiskPicService.class);
	
	/**
	 * 理财首页
	 *
	 *
	 * @author yaoyi
	 * @createDate 2016年1月7日
	 */
	public static void toInvestPre(int currPage, int pageSize) {

		//理财轮播广告banner
		List<t_advertisement> banners = advertisementService.queryAdvertisementFront(Location.INVEST_TURN_ADS, 5);
		renderArgs.put("banners",banners);
		
		/*//周排行榜
		List<Map<String, Object>> weekInvest = investService.queryWeekInvestList();
		if (weekInvest != null && weekInvest.size() > 0) {
			renderArgs.put("weekInvest", weekInvest);
		}*/
		
		//月排行榜
		List<Map<String, Object>> mouthInvest = investService.queryMouthInvestList();
		if (mouthInvest != null && mouthInvest.size() > 0) {
			renderArgs.put("mouthInvest", mouthInvest);
		}
		
		//大家都在投
		List<Map<String, Object>> investList = investService.queryNewInvestList();
		
		int countDebtInAuction = debtService.countDebtInAuction();
		
		render(investList,countDebtInAuction);
	}
	
	/**
	 * 理财首页-散标投资
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	public static void showBidsPre(int currPage, int pageSize){
		if (currPage < 1) {
			
			currPage = 1;
		}
		int countDebtInAuction = debtService.countDebtInAuction();
		if (countDebtInAuction > 0) {
			pageSize = 6;
		} else {
			pageSize = 8;
		}
		
		PageBean<t_bid> pageBean = bidService.pageOfBidInvest(currPage, pageSize);
		Date now = new Date();
		
		render(pageBean,now);
	}
	
	/**
	 * 理财首页-债权转让
	 *
	 * @param currPage
	 * @param pageSize
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	public static void showTransfersPre(int currPage, int pageSize){
		if (currPage < 1) {
			currPage = 1;

		}
		if (pageSize < 1 || pageSize > 6) {
			pageSize = 6;
		}
		
		PageBean<DebtTransfer> page = debtService.pageOfDebtTransfer(currPage, pageSize);
		
		renderArgs.put("sysNowTime", new Date());//服务器时间传到客户端
		
		render(page);
	}
	
	/**
	 * 投标页面
	 *
	 * @param bidId
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	@SubmitCheck
	public static void investPre(long bidId ){
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {

			error404();
		}
		//lihuijun 增加一个类型表示（普通标与定向标）
		Integer flag=bid.bid_type;
		if(null==flag || flag==0){flag=0;}
		t_user_info loanUserInfo = userInfoService.findUserInfoByUserId(bid.user_id);

		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			List<t_red_packet_user> redPacketList = redpacketService.queryRedpacketsByUserIdStatus(currUser.id, t_red_packet_user.RedpacketStatus.RECEIVED.code) ;
			
			renderArgs.put("balance", balance);;
			renderArgs.put("redPacketList", redPacketList);
			
			
			/**加息券使用规则筛选*/
			List<AddRateTicketBean> tickets = addRateTicketService.listOfTicketByUserIdAndStatus(currUser.id, 1);
			
			renderArgs.put("couponList", tickets);
			
			t_user_info  userInfo = userInfoService.findByColumn("user_id = ?", bid.user_id);
			
			t_user_info userInfoBid = userInfoService.findByColumn("user_id = ?",  currUser.id);
			session.put("memberType", userInfoBid.getMember_type().code);
			
			renderArgs.put("userInfo", userInfo);
		}
		
		
		
		List<BidItemOfSupervisorSubject> bidItemOfSupervisorSubjects = auditSujbectBidService.queryBidRefSubjectSupervisor(bidId);
		
		/** 查询借款人征信报告图片 */
		List<t_risk_pic> riskPics = riskPicService.findRiskPicByriskId(bid.risk_id);
		
		Date nowTime = new Date();
		if (bid.pre_release_time!=null && nowTime.before(bid.pre_release_time)) {//是否预发售状态
			renderArgs.put("preRelease", true);
		}
		renderArgs.put("sysNowTime", nowTime.getTime());//服务器时间传到客户端
		
		t_information investDeal = informationService.findInvestDeal();
		
		t_risk_report report=null;
		if(bid.risk_id!=null){
			report=riskReportService.findByID(bid.risk_id);
			
		}
		
		/** 将页面借款人信息中职业数据修改为单位所属行业  */
		t_industry_sort industrySort = null;
		
		if(bid.id!=null){
			industrySort= industrySortService.getByCompanyTrade(bid.id);
		}
		
		/** 在借款信息中添加起息日，有的话写放款时间，没有的话写暂无（年月日） */
		t_bid tbid = null;
		
		if(bid.id!=null){
			tbid= bidService.findByBidId(bid.risk_id);
        }
		
		String pledgeKind = null;
		if(bid.id!=null){
			/** 如果pledge_kind(抵押物种类)是"房产一抵"或"房产二抵"的话全部显示房屋抵押，如果是车辆方面则分别显示车辆质押或车辆抵押 */
			if(report.pledge_kind.equals("房产一抵")||report.pledge_kind.equals("房产二抵")){
				pledgeKind="房屋抵押";
			}else if (report.pledge_kind.equals("车辆质押")) {
				pledgeKind="车辆质押";
			}else if (report.pledge_kind.equals("车辆抵押")) {
				pledgeKind="车辆抵押";
			}
		}
		
		render(bid, loanUserInfo, bidItemOfSupervisorSubjects,investDeal,flag,report,industrySort,tbid,riskPics, pledgeKind);
	}
	
	/**
	 * 用户借款协议
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public static void investDealPre(){
		t_information investDeal = informationService.findInvestDeal();
		if(investDeal == null){
			
			error404();
		}
		
		render(investDeal);
	}
	
	/**
	 * 用户委托投资协议
	 * @author lihuijun
	 * @createDate 2017年10月11日
	 */
	public static void investEntrustDealPre(){
		CurrUser user = getCurrUser();
		t_information investDeal = informationService.findInvestEntrustDeal(user.id);
		if(investDeal == null){
			
			error404();
		}
		
		render(investDeal);
	}
	
	/**
	 * 投标操作
	 *
	 * @param bidSign 标的签名
	 * @param investAmt 投标金额（按金额购买）
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	
	@LoginCheck
	@SimulatedCheck
	/*public static void invest1() {
	
		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.TENDER);
		
		if (result.code < 0) {
			flash.error(result.msg);
			
			toInvestPre(1, 10);
		}
		
		Map<String, String> reqParams = result.msgs;
		String businessSeqNo = result.msg;
		
		String bidSign = reqParams.get("bidSign");
		double investAmt = Convert.strToDouble(reqParams.get("investAmt"), -1D);
		String inviteCode = reqParams.get("inviteCode");
		int bidType = Convert.strToInt(reqParams.get("bidType"), -1);
		
		result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
		
		long bidId = Long.parseLong(result.obj.toString());
		t_bid bid = bidService.findByID(bidId);
		
		if (bid == null) {
			
			error404();
		}
		
		if(investAmt%1 != 0){
			flash.error("请输入正整数的投标金额!");
			
			investPre(bidId);
		}
		
		//lihuijun 判断邀请码 begin
				
				if(bidType==1)
				{
					if(StringUtils.isBlank(inviteCode))
					{
						flash.error("请输入邀请码！");
						investPre(bidId);
					}
					else
					{
						if(!(bid.invite_code.equals(inviteCode.trim())))
						{						    
						    flash.error("您输入邀请码有误！");
							investPre(bidId);
						}
					}			
				}	
		//---lihuijun end	
		//按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			investAmt = new Double(Double.toString(investAmt)).intValue() * bid.min_invest_amount;
		}
		long userId = getCurrUser().id;
		long redPacketId = 0 ;
		double redPackeAmt = 0 ;
		double couponAmt = 0;
		String redPacketIdStr = reqParams.get("bribeId") ;
			*//** 使用红包 *//*
			redPacketId = Long.parseLong(redPacketIdStr) ;
			
			if(redPacketId > 0){
				result = investService.investRedPacket(userId, redPacketId, investAmt) ;
				if(result.code <= 0 ){
					LoggerUtil.info(true, result.msg);
					flash.error(result.msg);
					
					investPre(bidId);
				}
				t_red_packet_user redPacketUser = (t_red_packet_user) result.obj ;
				redPackeAmt =  redPacketUser.amount ;
			}
			
			*//** 使用加息券  *//*
			String couponIdStr = reqParams.get("couponId") ;
			long couponId = Long.parseLong(couponIdStr) ;
	
			*//**使用加息券投资准备*//*
			if(couponId > 0){
				result = addRateTicketService.checkAddRateTicket(userId, couponId, investAmt);
				if(result.code <= 0 ){
					LoggerUtil.info(false, result.msg);
					flash.error(result.msg);
					
					investPre(bidId);
				}				
			}

		
					
		*//**  投标前判断  *//*
		result = investService.invest(userId, bid, investAmt,redPackeAmt);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			investPre(bidId);
		}
		
		String businessSeqNos = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET);
		if (redPacketId > 0){
			
			t_red_packet_user redPacket = redpacketUserDao.findByID(redPacketId);
			redPacket.old_biz_no = businessSeqNos;
			redPacket.save();
			
			result =  redPacketInvest(1,businessSeqNos, userId, redPacketId, bid);
			
			if (result.code < 1) {
				flash.error(result.msg);
				
				investPre(bidId);
			}
		}
	
		//业务流水号
		String clientSn = "oyph_" + UUID.randomUUID().toString();			
		Map<String, Object> accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", "");
		accMap.put("oldOderNo", "");
		accMap.put("debitAccountNo", userId);
		accMap.put("cebitAccountNo", bid.object_acc_no);
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils.formatAmount(investAmt));
		accMap.put("summaryCode", "T01");
		
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accMap);
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "0");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		if (ConfConst.IS_TRUST) {
			
			result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.TENDER, EntrustFlag.UNENTRUST, accountList, contractList);
			
			if (result.code < 1) {
				if (redPacketId > 0){
					
					result = redpacketService.redPacketCorrect(bid, redPacketId, "0", businessSeqNos, userId);
				}
				
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				investPre(bidId);
			}
			
		}
		
		if(redPacketId > 0){
			result = investService.doInvest(userId, bidId, investAmt, businessSeqNo, "", Client.PC.code, InvestType.PC.code, redPacketId);	
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				investPre(bidId);
			}
			investSuccessPre(bidId, investAmt);
		}
		
		result = investService.doInvest(userId, bidId, investAmt, businessSeqNo, "", Client.PC.code, InvestType.PC.code, couponId);	
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			investPre(bidId);
		}
		
		investSuccessPre(bidId, investAmt);
		
	}*/
	
	/**
	 * 投标成功后，进入投标页面
	 *
	 * @param bidId
	 * @param investAmt
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年5月31日
	 */
	public static void investSuccessPre(long bidId, double investAmt, String businessSeqNo){
		long userId = getCurrUser().id;
		double  coupon = 0;
		
		t_coupon_user couponUser =couponService.queryByColumn(bidId, userId, investAmt,3);
		if (couponUser == null){
			coupon = 0;
		} else {
			coupon =couponUser.amount;
		}		
		t_bid bid = bidService.findByID(bidId);
		//投标成功后的标的信息
		flash.put("loan_schedule", bid.loan_schedule);
		flash.put("invest_amount", investAmt);
		flash.put("period", bid.period);
		flash.put("period_unit", bid.getPeriod_unit().getShowValue());
		flash.put("sign", bidId);
		double income = FeeCalculateUtil.getLoanSumInterest(investAmt,bid.apr+coupon, bid.period, bid.getPeriod_unit(), bid.getRepayment_type());
		flash.put("income", NumberFormat.round(income, 2));
		
		flash.put("investBidSuccess", true);
		
		/** 交易信息表（t_trade_info）*/
		tradeInfoService.saveTradeInfo(new Date(), businessSeqNo, TradeType.INVEST, userId, "CNY", BigDecimal.valueOf(investAmt), bidId);
		
		ResultInfo error = new ResultInfo();
		t_cps_activity cpsActivityGoing = null;
		t_cps_activity cpsActivity = null;
		t_cps_user cpsUser = cpsUserDao.findByUserId(userId);
		if (cpsUser != null) {
			cpsActivity = cpsActivityService.queryCpsActivity(cpsUser.activity_id);
			if (cpsActivity != null) {
				cpsActivityGoing = cpsActivityService.queryGoingCpsActivityById(cpsActivity.id, 1);
			}
		}
		int type = StringUtils.isNotBlank(session.get("memberType")) ? Integer.parseInt(session.get("memberType")) : -1;
		t_user user = t_user.findById(userId); 
		if (type == 0) {
			RewardGrantUtils.newCustomerFirst(cpsActivityGoing, user, bid, investAmt ,error);
			//给老用户(推广人)金币
			RewardGrantUtils.toOldCustomerGold(user);
			//如果新用户投标金额大于等于5000和时间在注册15天内
			Date time  = new Date();			
			int days = DateUtil.getDaysDiffFloor(user.time,time);	
			if (investAmt >= 5000 && days <= 15) {
				//给老用户(推广人)京东E卡
				int num = RewardGrantUtils.toOldAndNewECard(user);
				if (num > 1) {
					System.out.println("pc+E卡赠送成功");
				}
			}
			
		}
		if (type > 0) {
			RewardGrantUtils.newCustomerNotFirst(cpsActivity, user, bid, investAmt, error);
		}
		
		investPre(bidId);
	}
	
	/**
	 * 标的还款计划
	 *
	 * @param val 
	 * @param scale
	 * @return
	
	 * @author liudong
	 * @createDate 2015年12月30日
	 */
	public static void queryRepaymentBillPlanPre(int currPage,int pageSize,long bidId){
		
		PageBean<Map<String, Object>> page = billService.pageOfRepaymentBill(currPage, pageSize, bidId); 
		renderJSON(page);
	}
	
	/**
	 * 获取标的投标记录
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void investRecordPre(int currPage, int pageSize, long bidId){
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		PageBean<BidInvestRecord> pageBeans = investService.pageOfBidInvestDetail(currPage, pageSize, bidId);
		
		render(pageBeans);
	}
	
	/**
	 * 获取标的还款计划
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月14日
	 */
	public static void repaymentRecordPre(int currPage, int pageSize, long bidId){
		
		
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		PageBean<Map<String, Object>> pageBeana = billService.pageOfRepaymentBill(currPage, pageSize, bidId);
		
		render(pageBeana);
	}
	
	/**
	 * 获取标的追踪信息
	 *
	 * @param bidId
	 *
	 * @author yaoyi
	 * @createDate 2018年1月26日
	 */
	public static void loanTrackingPre(int currPage, int pageSize, long bidId){
		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		
		PageBean<t_loan_track> loanTracks = loanTrackService.pageOfLoanTracksByBidId(currPage, pageSize, bidId);
		
		render(loanTracks);
	}

	/**
	 * 债权转让界面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月22日
	 */
	@SubmitCheck
	public static void transferPre(long debeId){
		if (debeId <= 0) {

			error404();
		}
		
		DebtTransferDetail detail = debtService.findDebtDetailById(debeId);
		if (detail == null) {

			error404();
		}
		
		CurrUser currUser = getCurrUser();
		if (currUser != null) {
			double balance = userFundService.findUserBalance(currUser.id);
			renderArgs.put("balance", balance);
		}
		
		renderArgs.put("sysNowTime", new Date());//服务器时间传到客户端
	
		render(detail);
	}
	
	/**
	 * 投资的还款计划
	 *
	 * @param currPage
	 * @param pageSize
	 * @param investId
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年4月11日
	 */
	public static void repaymentInvestRecordPre(int currPage,int pageSize,long investId){		
		if(currPage < 1){
			currPage = 1;
		}
		if(pageSize < 1){
			pageSize = 5;
		}
		PageBean<InvestReceive> pageBean = billInvestService.pageOfRepaymentBill(currPage, pageSize, investId); 
		
		render(pageBean);
	}
	
	/**
	 * 进行债权竞价
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月28日
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void buyDebt(String debtSign){
		checkAuthenticity();
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			flash.error(result.msg);
			toInvestPre(0,0);
		}
		long debtId = Long.parseLong((String)result.obj);
		CurrUser currUser = getCurrUser();

		//准备
		result = debtService.debtTransfer(debtId, currUser.id);
		if(result.code < 1){
			flash.error(result.msg);
			transferPre(debtId);
		}
			
		//t_debt_tansfer的订单号
		
		//业务订单号
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.FARM_IN);
			
		if (ConfConst.IS_TRUST) {
			//资金托管
			PaymentProxy.getInstance().debtTransfer(Client.PC.code, businessSeqNo, debtId, currUser.id);
			
			transferPre(debtId);
		}
		
		//进入页面
		transferPre(debtId);
	}
	
	
	/**
	 * 投标前验密
	 * 
	 * @author niu
	 * @create 2017.09.21
	 */
	/*@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void investCheckPass1(String bidSign, double investAmt,String inviteCode,int bidType, String bribeId, String couponId) {
		
		//投标请求参数
		Map<String, String> investParams = new HashMap<String, String>();
		
		investParams.put("bidSign", bidSign);
		investParams.put("investAmt", investAmt + "");
		investParams.put("inviteCode", inviteCode);
		investParams.put("bidType", bidType + "");
		investParams.put("bribeId", bribeId);
		investParams.put("couponId", couponId);
		
		//验密请求参数
		CurrUser currUser = getCurrUser();
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.TENDER);
		t_user_info userInfo = userInfoService.findByID(currUser.id);
		String userName = "";
		if (userInfo != null && userInfo.reality_name != null) {
			userName = userInfo.reality_name;
		}
		
		//PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, currUser.id, BaseController.getBaseURL() + "check/yibincallback/investCB", userName, ServiceType.TENDER, investAmt, investParams);
		
	}*/
	
	
	/**使用红包投标
	 * 
	 * @param businessSeqNo 业务流水号
	 * @param userId	用户id
	 * @param redPackeAmt 红包金额
	 * @param investAmt  投资金额
	 * @param bidAccNo 标的台账
	 * @param bidId    标的id
	 * @param bid      用户投标详细信息
	 * @param redPacketId  红包id
	 * @param couponId   加息券id
	 */

	public static ResultInfo redPacketInvest(int client, String businessSeqNo, long userId, long redPacketId, t_bid bid) {
		
		ResultInfo result = new ResultInfo();
		
		t_red_packet_user redPacket = redpacketUserDao.findByID(redPacketId);
		if (redPacket == null) {
			result.code = -1;
			result.msg = "查询红包信息失败";
			
			return result;
		}
		
		
		redPacket.old_biz_no = businessSeqNo;
		redPacket.save();
		
		double redPackeAmt = redPacket.amount;
		long bidId = bid.id;
		
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		
		LinkedHashMap<String, Object> accountMap = new LinkedHashMap<String, Object>();
		accountMap.put("oderNo", "0");//序号
		accountMap.put("oldbusinessSeqNo","");//原业务流水号
		accountMap.put("oldOderNo","");//原序列号
		accountMap.put("debitAccountNo",YbConsts.MARKETNO);//借方台账号（理财方台账账号）
		accountMap.put("cebitAccountNo",userId);//贷方台账号（用户的台账账号）
		accountMap.put("currency",YbConsts.CURRENCY );//币种
		accountMap.put("amount",YbUtils.formatAmount(redPackeAmt)+"");//用户实际花费金额
		accountMap.put("summaryCode",ServiceType.MARKET.key);//摘要码
		
		//资金财务处理列表
		List<Map<String, Object>>accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accountMap);
		
		
		Map<String, Object> conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "1");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");
		
		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);
		
		
		switch (client) {
			case 1:{	
				if (ConfConst.IS_TRUST) {
					result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.MARKET, EntrustFlag.UNENTRUST, accountList, contractList);
						if (result.code < 1) {
							
							LoggerUtil.info(true, result.msg);
							flash.error(result.msg);
							
							InvestCtrl.investPre(bidId);
						}
						return result;
					
				}
				
				break;
			}
			case 2:
			case 3:{
				if (ConfConst.IS_TRUST) {
					
					result = PaymentProxy.getInstance().fundTrade(clientSn, userId, businessSeqNo, bid, ServiceType.MARKET, EntrustFlag.UNENTRUST, accountList, contractList);
						
						if (result.code < 1) {
							result.code =  -1;
							return result;					
						}
				}				
				result.code = 1;
				result.msg = "投标成功";
				break;
				}
			default:{//
				
				break;
			}		
		}
		return result;
		
	}
	
	
	/**
	 * 投标验密
	 * 
	 * @param bidSign 		标的签名   
	 * @param inviteCode 	定向标邀请码
	 * @param bidType 		标的类型：1、定向标  2、普通标
	 * @param bribeId 	 	红包ID
	 * @param couponId 	 	加息券ID
	 * @param investAmt 	投资金额
	 * 
	 * @author niu
	 * @create 2018-01-04
	 */
	@SubmitOnly
	@LoginCheck
	@SimulatedCheck
	public static void investCheckPass(String bidSign, String inviteCode, int bidType, long bribeId, long couponId, double investAmt) {
		
		
		// 1.标的ID解密
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			
			error404();
		}
	
		// 2.通过 标的ID 查询标的
		long bidId = Long.parseLong(result.obj.toString());
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			
			error404();
		}		
		
		// 3.校验定向标邀请码 （ 标的类型：1、定向标  2、普通标 ）
		if (bidType == 1 && !bid.invite_code.equals(inviteCode)) {
			flash.error("您输入邀请码有误！");
			
			investPre(bidId);
		}
		
		// 4.校验投资金额 		
		if(investAmt % 1 != 0){
			flash.error("请输入正整数的投标金额!");
			
			investPre(bidId);
		}
		
		// 5.按份数购买时，投资金额=投资份数*每份的金额
		if (t_product.BuyType.PURCHASE_BY_COPY.equals(bid.getBuy_type())) {
			investAmt = new Double(Double.toString(investAmt)).intValue() * bid.min_invest_amount;
		}
		
		long userId = getCurrUser().id;
		double redPackeAmt = 0.0;
		
		// 6.使用红包准备
		if (bribeId > 0 && couponId > 0) {
			flash.error("不能同时使用加息券和红包!");
			
			investPre(bidId);
		}
		
		if(bribeId > 0){
			result = investService.investRedPacket(userId, bribeId, investAmt, bid.period,bid.getPeriod_units());
			if(result.code <= 0 ){				
				flash.error(result.msg);
				
				investPre(bidId);
			}
			
			redPackeAmt =  ((t_red_packet_user) result.obj).amount ;
		}
		
		// 7.使用加息券准备
		if(couponId > 0){
			result = addRateTicketService.checkAddRateTicket(userId, couponId, investAmt, bid.period, bid.getPeriod_units());
			if(result.code <= 0 ){	
				flash.error(result.msg);
				
				investPre(bidId);
			}
		}
		
		// 8. 投标前判断  
		result = investService.invest(userId, bid, investAmt,redPackeAmt);
		if (result.code < 1) {			
			flash.error(result.msg);
			
			investPre(bidId);
		}
		
		// 9. 校验交易密码
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		if (userInfo == null) {
			flash.error("用户基本信息查询失败");
			
			investPre(bidId);
		}
		
		t_ssq_user ssqUser = ssquserService.findByUserId(userId);
		// 10. 电子签章准备
		if (ssqUser == null ){
			flash.error("电子签章用户校验失败");
			
			investPre(bidId);
		}
		
		
		if (userInfo.enterprise_name != null || !(StringUtils.isBlank(userInfo.enterprise_name)) ){
			flash.error("企业用户权限仅支持借款");
			
			investPre(bidId);
		}
		
		// 11. 判断投资待还金额和每日限额是否超过限制
		
		Date dates = new Date();
		StringBuffer startTime = new StringBuffer();
		startTime.append(DateUtil.getYear(dates)+"-").append(DateUtil.getMonth(dates)+"-").append(DateUtil.getDay(dates)+" 00:00:00");
		
		//用户投资待还金额
		double amounts = billInvestService.findTotalAmounts(userId)+investAmt;
		
		//用户今日投资总额
		double dayMount = investService.findTodayAmounts(userId, startTime.toString())+investAmt;
		
		t_quota quota = quotaService.findByColumn("user_id=?", userId);
		long creditId = userInfo.credit_id;
		
		if(quota == null) {
			if(creditId==3) {
				if(amounts>4000000) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-investAmt;
					quo.time = new Date();
					quo.save();
					
					investPre(bidId);
				}else if(dayMount>1500000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}
				
			} else if(creditId==4) {
				if(amounts>5000000) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-investAmt;
					quo.time = new Date();
					quo.save();
					
					investPre(bidId);
				}else if(dayMount>2000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}
			}else if(creditId==5) {
				if(amounts>10000000) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-investAmt;
					quo.time = new Date();
					quo.save();
					
					investPre(bidId);
				}else if(dayMount>3000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}
			}else if(creditId==1) {
				if(amounts>2000000) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-investAmt;
					quo.time = new Date();
					quo.save();
					
					investPre(bidId);
				}else if(dayMount>500000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}
			}else if(creditId==2) {
				if(amounts>3000000) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					t_quota quo = new t_quota();
					quo.user_id = userId;
					quo.sum_invest = amounts-investAmt;
					quo.time = new Date();
					quo.save();
					
					investPre(bidId);
				}else if(dayMount>1000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}
			}
		}else {
			if(creditId==3) {
				if(quota.type == 0) {
					flash.error("用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					investPre(bidId);
				}else if(dayMount>1500000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}else if(quota.amount<amounts) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-investAmt;
					quota.type = 0;
					quota.save();
					investPre(bidId);
				}
				
			} else if(creditId==4) {
				if(quota.type == 0) {
					flash.error("用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					investPre(bidId);
				}else if(dayMount>2000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}else if(quota.amount<amounts) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-investAmt;
					quota.type = 0;
					quota.save();
					investPre(bidId);
				}
			} else if(creditId==5) {
				if(quota.type == 0) {
					flash.error("用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					investPre(bidId);
				}else if(dayMount>3000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}else if(quota.amount<amounts) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-investAmt;
					quota.type = 0;
					quota.save();
					investPre(bidId);
				}
			} else if(creditId==1) {
				if(quota.type == 0) {
					flash.error("用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					investPre(bidId);
				}else if(dayMount>500000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}else if(quota.amount<amounts) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-investAmt;
					quota.type = 0;
					quota.save();
					investPre(bidId);
				}
			} else if(creditId==2) {
				if(quota.type == 0) {
					flash.error("用户总投资金额超过限制，已经提出申请，请耐心等待");
					
					investPre(bidId);
				}else if(dayMount>1000000) {
					flash.error("用户今日投资金额超过限额");
					
					investPre(bidId);
				}else if(quota.amount<amounts) {
					flash.error("用户总投资金额超过限制，点击向平台提出申请");
					quota.time = new Date();
					quota.sum_invest = amounts-investAmt;
					quota.type = 0;
					quota.save();
					investPre(bidId);
				}
			}
			
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.TENDER);
		
		Map<String, String> serviceReqParams = new HashMap<String, String>();
		serviceReqParams.put("businessSeqNo", businessSeqNo);
		serviceReqParams.put("userId", userId + "");
		serviceReqParams.put("amount", investAmt + "");
		serviceReqParams.put("bidId", bidId + "");
		serviceReqParams.put("couponId", couponId + "");
		serviceReqParams.put("redPacketId", bribeId + "");
		
		PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, userId, BaseController.getBaseURL() + "check/yibincallback/investCB", userInfo.reality_name, ServiceType.TENDER, investAmt, serviceReqParams, ServiceType.TENDER_CHECK_TRADE_PASS, 2, "", "", "");
	}
	
	public static void invest() {
		
		
		//
		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.TENDER_CHECK_TRADE_PASS);
		if (result.code < 0) {
			flash.error(result.msg);
			
			toInvestPre(1, 10);
		}
		
		//
		long bidId = Convert.strToLong(result.msgs.get("bidId"), -1);
		long userId = Convert.strToLong(result.msgs.get("userId"), -1);
		double investAmt = Convert.strToDouble(result.msgs.get("amount"), 0.0);
		String businessSeqNo = result.msgs.get("businessSeqNo");
		long couponId = Convert.strToLong(result.msgs.get("couponId"), -1);
		long redPacketId = Convert.strToLong(result.msgs.get("redPacketId"), -1);
		
		//
		t_bid bid = bidService.findByID(bidId);
		if (bid == null) {
			
			error404();
		}	
		
		// 使用红包
		String businessSeqNos = OrderNoUtil.getNormalOrderNo(ServiceType.MARKET);
		if (redPacketId > 0){
			result =  redPacketInvest(1, businessSeqNos, userId, redPacketId, bid);
			
			if (result.code < 1) {
				flash.error(result.msg);
				
				investPre(bidId);
			}
		}
		
		//投标
		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
		accountList.add(YbUtils.getAccountMap("0", "", "", userId + "", bid.object_acc_no, YbUtils.formatAmount(investAmt), ServiceType.TENDER.key));
		
		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().fundTrade("oyph_" + UUID.randomUUID().toString(), userId, businessSeqNo, bid, ServiceType.TENDER, EntrustFlag.UNENTRUST, accountList, YbUtils.getContractList());
			
			if (result.code < 1) {
				if (redPacketId > 0){
					result = redpacketService.redPacketCorrect(bid, redPacketId, "0", businessSeqNos, userId);
				}
				
				flash.error(result.msg);
				toInvestPre(1, 10);
			}
		}
		
		//本地投标业务处理
		if(redPacketId > 0){
			result = investService.doInvestRed(userId, bidId, investAmt, businessSeqNo, "", Client.PC.code, InvestType.PC.code, redPacketId);	
			
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
				
				investPre(bidId);
			}
			investSuccessPre(bidId, investAmt,businessSeqNo);
		}
		
		result = investService.doInvest(userId, bid.id, investAmt, businessSeqNo, "", Client.PC.code, InvestType.PC.code, couponId);	
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			
			investPre(bidId);
		}
		
		/** 满足投资发放规则发放 */
		investRedPacket(userId,investAmt,bid.product_id);
		
		investSuccessPre(bidId, investAmt,businessSeqNo);	
	}
	
	  /**
	   * 投资红包发放
	   * @param userId			用户id
	   * @param investAmt		投资金额
	   * @return
	   *
	   * @author LiuPengwei
	   * @createDate 2018年5月23日
	   */
	public  static boolean investRedPacket (long userId, double investAmt,long productId){
		
		//最新设置活动时间
		t_market_activity queryRedMarketActivity = marketActivityService.queryRedMarketActivity();
		//活动期间
		boolean flag = DateUtil.startDateToEndDate(queryRedMarketActivity.end_time, queryRedMarketActivity.start_time);
		
		if (flag) {
			//活动规则
			List<t_market_invest> activityRuleList = marketInvestService.findListByColumn("is_use = ? and type =1", true);
			
			for (t_market_invest activityRule : activityRuleList) {
				
				//满足投资金额和产品规则
				if (activityRule.invest_total <= investAmt && (activityRule.invest_product == productId || activityRule.invest_product == 0 )){

					flag = RedpacketCtrl.creatOfficialRedPacket(userId, activityRule.use_rule, activityRule.limit_day,
							activityRule.amount_apr,"投资发放", activityRule.use_bid_limit, "invest_give");
					
					if (!flag){
						
						LoggerUtil.error(true,"投资红包发放出错，数据回滚");
						flash.error("投资红包发放失败!");
						return false;
						
					}
				}
			}
			
		}
		
		//最新设置活动时间
		t_market_activity queryMarketActivity = marketActivityService.queryAddMarketActivity();
		//活动期间
		flag = DateUtil.startDateToEndDate(queryMarketActivity.end_time, queryMarketActivity.start_time);
		
		if (flag) {
			//活动规则
			List<t_market_invest> activityRuleList = marketInvestService.findListByColumn("is_use = ? and type =2 ", true);
			
			for (t_market_invest activityRule : activityRuleList) {
				
				//满足投资金额和产品规则
				if (activityRule.invest_total <= investAmt && (activityRule.invest_product == productId || activityRule.invest_product == 0 )){

					flag = AddRateCtrl.getAddRateTicketShakeActivity(activityRule.amount_apr,activityRule.use_rule, userId, activityRule.limit_day, activityRule.use_bid_limit,"投资发放");
					
					if (!flag){
						LoggerUtil.error(true,"投资加息券发放出错，数据回滚");
						flash.error("投资加息券发放失败!");
						return false;	
					}
				}
			}
			
		}
		
		return false;
	}
	
}
