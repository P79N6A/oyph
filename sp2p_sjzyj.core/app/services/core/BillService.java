package services.core;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.app.bean.Bills;
import models.common.entity.t_deal_platform;
import models.common.entity.t_deal_user;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_event_supervisor;
import models.common.entity.t_sms_jy_count;
import models.common.entity.t_transfer_notice;
import models.common.entity.t_user;
import models.common.entity.t_user_fund;
import models.core.bean.Bill;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.finance.entity.t_trade_info;

import payment.impl.PaymentProxy;
import play.Logger;
import services.base.BaseService;
import services.common.DealPlatformService;
import services.common.DealUserService;
import services.common.NoticeService;
import services.common.SmsJyCountService;
import services.common.SupervisorService;
import services.common.TransferNoticeService;
import services.common.UserFundService;
import services.common.UserService;
import services.ext.redpacket.AddRateTicketService;
import services.finance.TradeInfoService;
import yb.YbConsts;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ProjectStatus;
import yb.enums.ServiceType;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.shove.Convert;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.thoughtworks.xstream.mapper.Mapper.Null;

import common.FeeCalculateUtil;
import common.constants.ConfConst;
import common.enums.JYSMSModel;
import common.enums.NoticeScene;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.JYSMSUtil;
import common.utils.LoggerUtil;
import common.utils.NoUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import common.utils.number.NumberFormat;
import daos.core.BillDao;

/**
 * 借款账单
 *
 * @author liudong
 * @createDate 2015年12月18日
 */
public class BillService extends BaseService<t_bill> {

	protected static BillDao billDao = Factory.getDao(BillDao.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);

	protected static DealPlatformService dealPlatformService = Factory.getService(DealPlatformService.class);

	protected static NoticeService noticeService = Factory.getService(NoticeService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	protected static SupervisorService supervisorService = Factory.getService(SupervisorService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);

	protected static SmsJyCountService smsJyCountService = Factory.getService(SmsJyCountService.class);

	protected static TradeInfoService tradeInfoService = Factory.getService(TradeInfoService.class);
	
	protected static TransferNoticeService transferNoticeService = Factory.getService(TransferNoticeService.class);

	protected BillService() {
		super.basedao = billDao;
	}

	/**
	 * 查询回款计划
	 * 
	 * @author niu
	 * @create 2017.09.09
	 */
	public List<Map<String, Object>> findReturnInfoList(long bidId) {
		return billDao.getReturnInfoList(bidId);
	}

	/**
	 * 查询回款计划
	 * 
	 * @author niu
	 * @create 2017.09.09
	 */
	public List<Map<String, Object>> findReturnList() {

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("oderNo", "");
		map.put("returnNo", "");
		map.put("returnDate", "");

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		list.add(map);

		return list;
	}

	/**
	 * 标的信息修改传递还款计划
	 * 
	 * @author niu
	 * @create 2017.09.09
	 */
	public List<Map<String, Object>> findReturnLists(long bidId) {

		return billDao.findReturnList(bidId);
	}

	/**
	 * 保存账单
	 *
	 * @param tbill
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月24日
	 */
	public boolean createBill(t_bill tbill) {

		return billDao.save(tbill);
	}

	/**
	 * 回款计划
	 *
	 * @param bidId
	 *            标的id
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public List<t_bill> findBillByBidId(long bidId) {
		List<t_bill> bills = billDao.findBillByBidId(bidId);

		return bills;
	}

	/**
	 * 标的的总账单数
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年1月20日
	 */
	public int findBidTotalBillCount(long bidId) {
		return Convert.strToInt(String.valueOf(billDao.queryBillCount(bidId, 0, false).get("period")), 0);
	}

	/**
	 * 统计借款账单 应还本金利息合计
	 *
	 * @param showType
	 *            default:所有 1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还(待还+逾期)
	 *            3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param loanName
	 *            借款人昵称
	 * @param projectName
	 *            项目
	 * @return
	 *
	 * @author liudong
	 * @createDate 2016年1月19日
	 */
	public double findTotalBillAmount(int showType, String loanName, String projectName) {

		return billDao.findTotalBillAmount(showType, loanName, projectName);
	}

	/**
	 * 分页查询 借款账单
	 *
	 * @param showType
	 *            default:所有 1:待还(正常待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款
	 *            、线下收款、本息垫付后线下收款 )
	 * @param exports
	 *            1：导出 default：不导出
	 * @param loanName
	 *            借款人昵称
	 * @param orderType
	 *            排序栏目 0：编号 3：账单金额 5：逾期时长 6：到期时间 7：还款时间
	 * @param orderValue
	 *            排序规则 0,降序，1,升序
	 * @param projectName
	 *            项目
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public PageBean<Bill> pageOfBillBack(int showType, int currPage, int pageSize, int exports, String loanName,
			int orderType, int orderValue, String projectName) {

		return billDao.pageOfBillBack(showType, currPage, pageSize, exports, loanName, orderType, orderValue,
				projectName);
	}

	/**
	 * 查询指定好的 借款账单
	 * 
	 * @param bidId
	 * @return
	 * @createDate 2017年5月12日
	 * @author lihuijun
	 */
	public List<Bill> ListOfBillByBidId(long bidId) {

		return billDao.ListOfBillByBidId(bidId);

	}

	/**
	 * 标的还款计划
	 *
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页的条数
	 * @param bidId
	 *            标的id
	 * @return
	 * 
	 * @author liudong
	 * @createDate 2016年12月30日
	 */
	public PageBean<Map<String, Object>> pageOfRepaymentBill(int currPage, int pageSize, long bidId) {

		return billDao.pageOfRepaymentBill(currPage, pageSize, bidId);
	}

	/**
	 * 根据标的ID查询理财账单
	 *
	 * @param bidId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月22日
	 */
	public List<t_bill> queryBidBill(long bidId) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bid_id", bidId);

		return billDao.findListByColumn("bid_id=:bid_id", params);
	}

	/**
	 * 根据标的id与账单状态查询账单
	 * 
	 * @param bidId
	 * @return
	 * @createDate 2017年5月15日
	 * @author lihuijun
	 */
	public List<t_bill> findBillByBidIdAndStatus(long bidId) {

		return billDao.findListByColumn("bid_id = ? and status = 0", bidId);
	}

	/**
	 * 查询待还总额
	 * 
	 * @return
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年4月26日
	 *
	 */
	public double queryTotalNoRepaymentAmount() {

		return billDao.findTotalNoRepaymentAmount();
	}

	/**
	 * 到期还款率 (到期还款率=（账单数-逾期待还账单数）/待还账单数) 保留三位小数
	 *
	 * @author liudong
	 * @createDate 2015年12月23日
	 */
	public double queryExpireRepaymentRate() {
		double repayrate = billDao.queryExpireRepaymentRate();
		return Double.parseDouble(NumberFormat.round(repayrate, 3));
	}

	/**
	 * 本息垫付（准备）
	 *
	 * @param bill
	 * @param supervisorId
	 *            管理员ID
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public ResultInfo principalAdvance(t_bill bill, long supervisorId) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		/** 校验账单状态 */
		if (!t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result.code = -1;
			result.msg = "本期账单不在本息垫付范围内";

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";

			return result;
		}

		/** 结算理财服务费和逾期罚息费 */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String, Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest,
					bid.service_fee_rule, billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);

			billInvestFeeList.add(billInvestFee);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_PRINCIPAL,
				supervisorEventParam);

		result.code = 1;
		result.msg = "本息垫付准备完毕";
		result.obj = billInvestFeeList;

		return result;
	}

	/**
	 * 正常还款（准备）
	 *
	 * @param userId
	 *            用户ID
	 * @param bill
	 *            借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo normalRepayment(long userId, t_bill bill) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";

			return result;
		}

		/** 用户资金是否出现异常变动 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {

			return result;
		}

		/** 借款人资金是否充足 */
		double payAmt = Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine);
		if (userFund.balance < payAmt) {
			result.code = -1;
			result.msg = "余额不足";

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";

			return result;
		}

		/** 结算理财服务费和逾期罚息费 */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String, Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest,
					bid.service_fee_rule, billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);

			billInvestFeeList.add(billInvestFee);
		}

		result.code = 1;
		result.msg = "还款准备完毕";
		result.obj = billInvestFeeList;

		return result;
	}

	/**
	 * 前台提前还款
	 * 
	 * @param userId
	 * @param bill
	 * @return
	 * @createDate 2017年5月18日
	 * @author lihuijun
	 */
	public ResultInfo normalRepaymentAll(long userId, t_bill bill) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";

			return result;
		}

		/** 用户资金是否出现异常变动 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestLists = billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		double surplusCorpus = 0;
		for (t_bill_invest billIn : billInvestLists) {
			surplusCorpus += billIn.receive_corpus;
		}
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		double surplusFine = 0; // 所有罚息（包括当月，因为当月是0所以可以不用去除）
		double surplusInterest = 0; // 所有利息（包括当月跟逾期月份利息）
		if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
			for (t_bill_invest interest : billInvestOverdueList) {
				surplusFine += interest.overdue_fine;
				surplusInterest += interest.receive_interest;
			}
		}

		/** 借款人资金是否充足 */
		// double payAmt = Arith.add(Arith.add(bill.repayment_corpus,
		// bill.repayment_interest), bill.overdue_fine);
		double payAmt = Arith.add(Arith.add(surplusCorpus, surplusInterest), surplusFine);
		if (userFund.balance < payAmt) {
			result.code = -1;
			result.msg = "余额不足";

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";

			return result;
		}

		/** 结算理财服务费和逾期罚息费 */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String, Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double interest = 0; // 理财服务费
			double overFine = 0; // 逾期罚息
			if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
				for (t_bill_invest bin : billInvestOverdueList) {
					interest += bin.receive_interest;
					overFine += bin.overdue_fine;
				}
			}

			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(interest, bid.service_fee_rule,
					billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", overFine);

			billInvestFeeList.add(billInvestFee);
		}

		result.code = 1;
		result.msg = "还款准备完毕";
		result.obj = billInvestFeeList;

		return result;
	}

	/**
	 * 线下收款（准备）
	 *
	 * @param userId
	 *            用户ID
	 * @param bill
	 *            借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo offlineReceive(long supervisorId, t_bill bill) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";

			return result;
		}

		/** 结算理财服务费和逾期罚息费 */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String, Double>>();
		Map<String, Double> billInvestFee = null;
		for (t_bill_invest billInvest : billInvestList) {
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(billInvest.receive_interest,
					bid.service_fee_rule, billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", billInvest.overdue_fine);

			billInvestFeeList.add(billInvestFee);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_OFFLINE, supervisorEventParam);

		result.code = 1;
		result.msg = "线下收款准备完毕";
		result.obj = billInvestFeeList;

		return result;
	}

	/**
	 * 提前还款准备
	 * 
	 * @param supervisorId
	 * @param bill
	 * @return
	 * @createDate 2017年5月20日
	 * @author lihuijun
	 */
	public ResultInfo advanceReceive(long supervisorId, t_bill bill) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		if (bid == null) {
			result.code = -1;
			result.msg = "获取借款标信息失败";

			return result;
		}

		/** 结算理财服务费和逾期罚息费 */
		List<Map<String, Double>> billInvestFeeList = new ArrayList<Map<String, Double>>();
		Map<String, Double> billInvestFee = null;

		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		for (t_bill_invest billInvest : billInvestList) {
			double interest = 0; // 理财服务费
			double overFine = 0; // 逾期罚息
			if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
				for (t_bill_invest bin : billInvestOverdueList) {
					interest += bin.receive_interest;
					overFine += bin.overdue_fine;
				}
			}
			// billInvest.receive_interest = interest;
			// billInvest.overdue_fine = overFine;
			double investServiceFee = FeeCalculateUtil.getInvestManagerFee(interest, bid.service_fee_rule,
					billInvest.user_id);
			billInvestFee = new HashMap<String, Double>();
			billInvestFee.put("investServiceFee", investServiceFee);
			billInvestFee.put("overdueFine", overFine);
			billInvestFeeList.add(billInvestFee);
		}

		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_OFFLINE, supervisorEventParam);

		result.code = 1;
		result.msg = "线下收款准备完毕";
		result.obj = billInvestFeeList;

		return result;
	}

	/**
	 * 本息垫付还款（准备）
	 *
	 * @param userId
	 *            用户ID
	 * @param bill
	 *            借款账单
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo advanceRepayment(long userId, t_bill bill) {
		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund == null) {
			result.code = -1;
			result.msg = "获取用户资金信息失败";

			return result;
		}

		/** 用户资金是否出现异常变动 */
		result = userFundService.userFundSignCheck(userId);
		if (result.code < 1) {

			return result;
		}

		/** 借款人资金是否充足 */
		double payAmt = Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine);
		if (userFund.balance < payAmt) {
			result.code = -1;
			result.msg = "余额不足";

			return result;
		}

		result.code = 1;
		result.msg = "还款准备完毕";

		return result;
	}

	/**
	 * 本息垫付（执行）
	 *
	 * @param billId
	 * @param serviceOrderNo
	 * @param billInvestFeeList
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月25日
	 */
	public ResultInfo doPrincipalAdvance(long billId, String serviceOrderNo,
			List<Map<String, Double>> billInvestFeeList) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 判断还款的借款标有没有债权在转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		/** 垫付罚息 */
		double advanceOverdueFine = 0;

		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";

				return result;
			}

			boolean isSignSuccess = true;
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}

			// 理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");
			// 逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine");
			// 理财人应收金额
			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine
					- investServiceFee;

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccess) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - overdueFine, // 此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, t_deal_user.OperationType.RECEIVE, summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, overdueFine,
						investUserFund.balance + investServiceFee, // 此时不计“理财服务费”
						investUserFund.freeze, t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, investServiceFee,
					investUserFund.balance, investUserFund.freeze, t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			String bid_name = billInvest.bids.title;
			if (billInvest.bids.title.indexOf("【") != -1) {
				bid_name = bid_name.replace("【", "");
			}
			if (billInvest.bids.title.indexOf("】") != -1) {
				bid_name = bid_name.replace("】", "");
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bid_no", billInvest.bids.getBid_no());
			sceneParame.put("bid_name", bid_name);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			// 还款成功
			// 创蓝接口
			// noticeService.sendSysNotice(billInvest.user_id,
			// NoticeScene.INVEST_SECTION, sceneParame);
			// 焦云接口
			t_user user = t_user.findById(billInvest.user_id);
			Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_INVEST_SECTION);
			if (flag) {
				noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame,
						JYSMSModel.MODEL_INVEST_SECTION);
			}

			advanceOverdueFine += overdueFine;
		}

		// 更新借款账单还款情况
		boolean repayment = updateAdvanceData(bill.id);
		if (!repayment) {
			result.code = -1;
			result.msg = "本息垫付成功，更新借款账单还款数据失败";
			LoggerUtil.error(true, "本息垫付成功，更新借款账单还款数据失败。");

			return result;
		}

		// 本息垫付，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		boolean addDeal = dealPlatformService.addPlatformDeal(bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, t_deal_platform.DealRemark.ADVANCE_PRIN_INTER,
				dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台本息垫付收支记录失败";

			return result;
		}

		// 罚息垫付，添加平台收支记录
		if (advanceOverdueFine > 0) {
			addDeal = dealPlatformService.addPlatformDeal(bill.user_id, advanceOverdueFine,
					t_deal_platform.DealRemark.OVERDUE_INTEREST, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台罚息垫付收支记录失败";

				return result;
			}
		}

		result.code = 1;
		result.msg = "垫付成功";

		return result;
	}

	/**
	 * 正常还款（执行）
	 * 
	 * @param billId
	 *            借款账单ID
	 * @param overdueFeeList
	 *            逾期罚息列表（理财账单）
	 * @param serviceOrderNo
	 *            业务订单号
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doRepayment(long billId, List<Map<String, Double>> billInvestFeeList, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		/** 借款人扣除的逾期罚息 */
		double loanOverdueFine = 0; // 借款人扣除的逾期罚息 = 理财人收取的逾期罚息之和

		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";

				return result;
			}

			// 理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");
			// 逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine");
			// 理财人应收金额
			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine
					- investServiceFee;

			boolean isSignSuccess = true; // 用户签名是否通过
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccess) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - overdueFine, // 此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, t_deal_user.OperationType.RECEIVE, summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, overdueFine,
						investUserFund.balance + investServiceFee, // 此时不计“理财服务费”
						investUserFund.freeze, t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, investServiceFee,
					investUserFund.balance, investUserFund.freeze, t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bid_no", billInvest.bids.getBid_no());
			sceneParame.put("bid_name", billInvest.bids.title);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

			// 计算借款人扣除的逾期罚息
			loanOverdueFine += overdueFine;
		}

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.NORMAL_REPAYMENT);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		/** 扣除借款人还款金额 */
		double repayAmt = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, loanUserFund.balance + loanOverdueFine, // 此时不计“逾期罚息”
				loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT, summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id, loanOverdueFine,
					loanUserFund.balance, loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 前台提前还款
	 * 
	 * @param billId
	 * @param billInvestFeeList
	 * @param serviceOrderNo
	 * @return
	 * @createDate 2017年5月17日
	 * @author lihuijun
	 */
	public ResultInfo doRepaymentAll(long billId, List<Map<String, Double>> billInvestFeeList, String serviceOrderNo) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		List<t_bill_invest> billInvestLists = billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		/** 借款人扣除的逾期罚息 */
		double loanOverdueFine = 0; // 借款人扣除的逾期罚息 = 理财人收取的逾期罚息之和
		double surplusCorpus = 0;
		double surplusInterest = 0;
		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			/** 增加记录信息 */
			// List<t_bill_invest>
			// billInvestLists=billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
			for (t_bill_invest bin : billInvestLists) {
				if (billInvest.invest_id == bin.invest_id && !(billInvest.id).equals(bin.id)) {
					billInvest.receive_corpus += bin.receive_corpus;
				}
			}

			// List<t_bill_invest> billInvestOverdueList =
			// billInvestService.getInvestOverduelist(bill.bid_id);
			double interestTotal = 0;
			if (billInvestOverdueList.size() > 0) {
				for (t_bill_invest interest : billInvestOverdueList) {
					if (billInvest.invest_id == interest.invest_id) {
						interestTotal += interest.receive_interest;
					}
				}
			}
			billInvest.receive_interest = interestTotal;
			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";
				return result;
			}

			// 理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");
			// 逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine");
			// 理财人应收金额
			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine
					- investServiceFee;

			boolean isSignSuccess = true; // 用户签名是否通过
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccess) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - overdueFine, // 此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, t_deal_user.OperationType.RECEIVE, summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, overdueFine,
						investUserFund.balance + investServiceFee, // 此时不计“理财服务费”
						investUserFund.freeze, t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, investServiceFee,
					investUserFund.balance, investUserFund.freeze, t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

			// 计算借款人扣除的逾期罚息
			loanOverdueFine += overdueFine;

			surplusCorpus += billInvest.receive_corpus;

			surplusInterest += billInvest.receive_interest;
		}

		bill.repayment_corpus = surplusCorpus;
		bill.repayment_interest = surplusInterest;

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_lINE_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		/** 扣除借款人还款金额 */
		double repayAmt = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, loanUserFund.balance + loanOverdueFine, // 此时不计“逾期罚息”
				loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT, summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id, loanOverdueFine,
					loanUserFund.balance, loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		/*
		 * if (isEndPayment(bill.bid_id)) { bidService.bidEnd(bill.bid_id); }
		 */

		// bidService.bidEnd(bill.bid_id);
		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 垫付后线下收款（执行）
	 *
	 * @param supervisorId
	 *            管理员ID
	 * @param billId
	 *            借款账单ID
	 * @param loanOverdueFine
	 *            逾期罚息
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doOfflineReceiveAfterAdvance(long supervisorId, long billId) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人本息垫付还款成功，更新借款账单还款数据失败";

			return result;
		}

		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}

		t_user_fund loanUser = userFundService.queryUserFundByUserId(bill.user_id);

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUser.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);

		/** 添加管理员事件 */
		Map<String, String> supervisorEventParam = new HashMap<String, String>();
		supervisorEventParam.put("bill_no ", bill.bill_no);
		supervisorService.addSupervisorEvent(supervisorId, t_event_supervisor.Event.BILL_OFFLINE, supervisorEventParam);

		result.code = 1;
		result.msg = "本息垫付后线下收款成功";

		return result;
	}

	/**
	 * 线下收款（执行）
	 * 
	 * @param bill
	 *            借款账单ID
	 * @param overdueFeeList
	 *            逾期罚息列表（理财账单）
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doOfflineReceive(long billId, List<Map<String, Double>> billInvestFeeList,
			String serviceOrderNo) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 判断还款的借款标有没有债权在转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		/** 逾期罚息费 */
		double loanOverdueFine = 0;

		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";

				return result;
			}

			boolean isSignSuccessed = true;
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccessed = false;
			}

			// 理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");
			// 逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine");
			// 理财人应收金额
			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine
					- investServiceFee;

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccessed) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - overdueFine, // 此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, t_deal_user.OperationType.RECEIVE, summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, overdueFine,
						investUserFund.balance + investServiceFee, // 此时不计“理财服务费”
						investUserFund.freeze, t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, investServiceFee,
					investUserFund.balance, investUserFund.freeze, t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bid_no", billInvest.bids.getBid_no());
			sceneParame.put("bid_name", billInvest.bids.title);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

			loanOverdueFine += overdueFine;

		}

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}

		// 线下收款，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		boolean addDeal = dealPlatformService.addPlatformDeal(bill.user_id,
				bill.repayment_corpus + bill.repayment_interest + loanOverdueFine,
				t_deal_platform.DealRemark.OFFLINE_SECTION, dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";

			return result;
		}

		result.code = 1;
		result.msg = "线下收款成功";

		return result;
	}

	/**
	 * 提前还款执行
	 * 
	 * @param billId
	 * @param billInvestFeeList
	 * @param serviceOrderNo
	 * @return
	 * @createDate 2017年5月15日
	 * @author lihuijun
	 */
	public ResultInfo doAdvanceReceive(long billId, List<Map<String, Double>> billInvestFeeList,
			String serviceOrderNo) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 判断还款的借款标有没有债权在转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);
		List<t_bill_invest> billInvestLists = billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		/** 逾期罚息费 */
		double loanOverdueFine = 0;
		double surplusCorpus = 0;
		double surplusInterest = 0;

		/** 理财人收款 */
		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			/** 增加记录信息 */
			// List<t_bill_invest>
			// billInvestLists=billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
			for (t_bill_invest bin : billInvestLists) {
				if (billInvest.invest_id == bin.invest_id && !(billInvest.id).equals(bin.id)) {
					billInvest.receive_corpus += bin.receive_corpus;
				}
			}

			// List<t_bill_invest> billInvestOverdueList =
			// billInvestService.getInvestOverduelist(bill.bid_id);
			double interestTotal = 0;
			if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
				for (t_bill_invest interest : billInvestOverdueList) {
					if (billInvest.invest_id == interest.invest_id) {
						interestTotal += interest.receive_interest;
					}
				}
			}
			billInvest.receive_interest = interestTotal;
			// 更新理财账单收款情况
			boolean receive = billInvestService.updateReceiveData(billInvest.id);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";

				return result;
			}

			boolean isSignSuccessed = true;
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccessed = false;
			}

			// 理财服务费
			double investServiceFee = billInvestFeeList.get(i).get("investServiceFee");
			// 逾期罚息
			double overdueFine = billInvestFeeList.get(i).get("overdueFine");
			// 理财人应收金额
			double receiveAmt = billInvest.receive_corpus + billInvest.receive_interest + overdueFine
					- investServiceFee;

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccessed) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - overdueFine, // 此时不计“理财服务费”和“逾期罚息”
					investUserFund.freeze, t_deal_user.OperationType.RECEIVE, summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (overdueFine > 0) {
				addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, overdueFine,
						investUserFund.balance + investServiceFee, // 此时不计“理财服务费”
						investUserFund.freeze, t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);
				if (!addDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, billInvest.user_id, investServiceFee,
					investUserFund.balance, investUserFund.freeze, t_deal_user.OperationType.INVEST_SERVICE_FEE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			addDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount", billInvest.receive_corpus + billInvest.receive_interest + overdueFine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame);

			loanOverdueFine += overdueFine;

			surplusCorpus += billInvest.receive_corpus;

			surplusInterest += billInvest.receive_interest;

		}
		/*
		 * List<t_bill_invest>
		 * billInvestLists=billInvestService.getBillInvestListByBidIdAndStatus(
		 * bill.bid_id); double surplusCorpus=0; //所有待还本金 for (t_bill_invest
		 * billIn:billInvestLists) { surplusCorpus+=billIn.receive_corpus; }
		 * bill.repayment_corpus=surplusCorpus; List<t_bill_invest>
		 * billInvestOverdueList =
		 * billInvestService.getInvestOverduelist(bill.bid_id);
		 * 
		 * double surplusInterest=0; //所有利息（包括当月跟逾期利息）
		 * if(billInvestOverdueList!=null && billInvestOverdueList.size()>0){
		 * for(t_bill_invest interest:billInvestOverdueList){
		 * surplusInterest+=interest.receive_interest; } }
		 * bill.repayment_interest=surplusInterest;
		 */
		bill.repayment_corpus = surplusCorpus;
		bill.repayment_interest = surplusInterest;

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_lINE_RECEIVE);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		/*
		 * if (isEndPayment(bill.bid_id)) { bidService.bidEnd(bill.bid_id); }
		 */

		/* bidService.bidEnd(bill.bid_id); */

		// 线下收款，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);

		boolean addDeal = dealPlatformService.addPlatformDeal(bill.user_id,
				bill.repayment_corpus + bill.repayment_interest + loanOverdueFine,
				t_deal_platform.DealRemark.OFFLINE_SECTION, dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台收支记录失败";

			return result;
		}

		result.code = 1;
		result.msg = "提前收款成功";
		return result;
	}

	/**
	 * 本息垫付还款（执行）
	 *
	 * @param userId
	 *            用户ID
	 * @param billId
	 *            借款账单ID
	 * @param loanOverdueFine
	 *            逾期罚息
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月28日
	 */
	public ResultInfo doAdvanceRepayment(String serviceOrderNo, long userId, long billId, double loanOverdueFine) {
		ResultInfo result = new ResultInfo();

		t_bill bill = findByID(billId);
		if (bill == null) {
			result.code = -1;
			result.msg = "该借款账单不存在";

			return result;
		}

		// 更新借款账单还款情况
		boolean repayment = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT);
		if (!repayment) {
			result.code = -1;
			result.msg = "借款人本息垫付还款成功，更新借款账单还款数据失败";

			return result;
		}

		/** 扣除借款人还款金额 */
		double repayAmt = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;
		boolean minusFund = userFundService.userFundMinus(bill.user_id, repayAmt);
		if (!minusFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名更新
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund loanUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (loanUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		boolean addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, loanUserFund.balance + loanOverdueFine, // 此时不计“逾期罚息”
				loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT, summaryParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			addDeal = dealUserService.addDealUserInfo(serviceOrderNo, bill.user_id, loanOverdueFine,
					loanUserFund.balance, loanUserFund.freeze, t_deal_user.OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		if (isEndPayment(bill.bid_id)) {
			bidService.bidEnd(bill.bid_id);
		}

		// 本息垫付还款，添加平台收支记录
		Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
		dealRemarkParam.put("bill_no", bill.bill_no);
		addDeal = dealPlatformService.addPlatformDeal(bill.user_id, bill.repayment_corpus + bill.repayment_interest,
				t_deal_platform.DealRemark.ADVANCE_PRIN_INTER_INCOME, dealRemarkParam);
		if (!addDeal) {
			result.code = -1;
			result.msg = "添加平台本息垫付还款收支记录失败";

			return result;
		}

		// 罚息垫付还款，添加平台收支记录
		if (loanOverdueFine > 0) {
			addDeal = dealPlatformService.addPlatformDeal(bill.user_id, loanOverdueFine,
					t_deal_platform.DealRemark.OVERFUE_INTEREST_INCOME, dealRemarkParam);
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加平台罚息垫付还款收支记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", loanUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);

		result.code = 1;
		result.msg = "本息垫付还款成功";

		return result;
	}

	/**
	 * 借款人还款成功 <br>
	 * 更新借款账单状态:正常还款或本息垫付还款 <br>
	 * 更新实际还款时间
	 *
	 * @param billId
	 *            借款账单ID
	 * @param status
	 *            还款状态枚举
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean updateRepaymentData(long billId, t_bill.Status status) {
		int row = billDao.updateStatusAndRealRepaymentTime(billId, status.code, new Date());
		if (row < 1) {
			LoggerUtil.error(true, "借款人还款成功，更新借款账单还款数据失败。【billId：%s】", billId);

			return false;
		}

		return true;
	}

	/**
	 * 借款人还款成功 <br>
	 * 更新借款账单状态:正常还款或本息垫付还款 <br>
	 * 更新实际还款时间
	 *
	 * @param billId
	 *            借款账单ID
	 * @param status
	 *            还款状态枚举
	 * @return
	 *
	 * @author guoShiJie
	 * @createDate 2018年10月5日
	 */
	public boolean updateRepaymentData(long billId, t_bill.Status status, String busniessNo, double totalAmount) {
		int row = billDao.updateStatusAndRealRepaymentTime(billId, status.code, new Date(), busniessNo);
		if (row < 1) {
			LoggerUtil.error(true, "借款人还款成功，更新借款账单还款数据失败。【billId：%s】", billId);

			return false;
		}
		// return true;
		return this.addTradeInfoByBillId(billId, busniessNo, totalAmount);
	}

	/**
	 * 本息垫付成功 <br>
	 * 更新借款账单状态为：本息垫付待还
	 *
	 * @param billId
	 *            借款账单ID
	 * @param status
	 *            还款状态枚举
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean updateAdvanceData(long billId) {

		int row = billDao.updateStatus(billId, t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.code);

		if (row < 1) {

			return false;
		}

		return true;
	}

	/**
	 * 查询用户待还
	 *
	 * @param userId
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2015年12月28日
	 */
	public double getUserPay(long userId) {

		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(t_bill.Status.NORMAL_NO_REPAYMENT.code);
		statusList.add(t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.code);

		return billDao.findUserPay(userId, statusList);
	}

	/**
	 * 后台-首页-账单信息的统计字段
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月15日
	 */
	public Map<String, Object> backCountBillInfo() {

		return billDao.backCountBillInfo();
	}

	/**
	 * 判断该借款标是否已经还完。
	 *
	 * @param bidId
	 * @return
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月29日
	 */
	public boolean isEndPayment(long bidId) {

		List<t_bill> list = billDao.queryNoRepaymentBillList(bidId);

		if (list == null || list.size() == 0) {

			return true;
		}

		return false;
	}

	/**
	 * 定时任务-系统标记逾期或者重新计算逾期费用
	 *
	 * @return
	 *
	 * @author yaoyi
	 * @createDate 2016年2月27日
	 */
	public ResultInfo systemMarkOverdue() {

		return billDao.autoMarkOverdue();
	}

	/**
	 * 定时任务-账单将要到期或者已经到期的提醒
	 *
	 *
	 * @author yaoyi
	 * @createDate 2016年3月1日
	 */

	public ResultInfo billRemind() {
		ResultInfo result = new ResultInfo();

		// 查询今天或者一个星期后到期的“正常待还”和“本息垫付待还”账单
		List<Map<String, Object>> listOfWillExpireBill = billDao.queryListOfWillExpireBill();

		if (listOfWillExpireBill == null || listOfWillExpireBill.size() == 0) {
			result.code = 1;
			result.msg = "当前没有需要提醒的账单!";

			return result;
		}

		int success = 0;
		/*
		 * for(Map<String, Object> mapBill : listOfWillExpireBill){
		 * 
		 * Map<String, Object> sceneParame = new HashMap<String, Object>();
		 * sceneParame.put("user_name", mapBill.get("name"));
		 * sceneParame.put("bill_no",
		 * NoUtil.getBillNo(Long.parseLong(mapBill.get("billId").toString()),
		 * (Date)mapBill.get("time"))); sceneParame.put("repayment_time",
		 * DateUtil.dateToString((Date)mapBill.get("repayment_time"),
		 * "yyyy-MM-dd")); sceneParame.put("amount",
		 * mapBill.get("repayment_amount")); boolean send =
		 * noticeService.sendSysNotice(Long.parseLong(mapBill.get("userId").
		 * toString()), NoticeScene.BILL_EXPIRES, sceneParame); if (send) {
		 * success++; } }
		 */

		result.code = 1;
		result.msg = "本次账单提醒总共通知" + listOfWillExpireBill.size() + "人次，成功通知" + success + "人次!";

		return result;
	}

	public void updateThisBill(long billId, double surplusCorpus, double surplusInterest, double surplusFine) {
		billDao.updateThisBill(billId, surplusCorpus, surplusInterest, surplusFine);
	}

	/**
	 * 提前还款更新借款账单情况
	 * 
	 * @param billId
	 * @createDate 2017年5月25日
	 * @author lihuijun
	 */
	public void updateBill(long billId) {
		billDao.updateBill(billId);
	}

	/**
	 * 正常还款 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.09.26
	 */
	public ResultInfo doRepaymentOne(String businessSeqNo, t_bill bill, double loanOverdueFine, t_bid bid) {

		ResultInfo result = new ResultInfo();

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		t_bill bills = billService.findByID(bill.id);
		if (bills == null) {
			result.code = -1;
			result.msg = "更新借款账单还款数据失败";

			return result;
		}

		// 扣除借款人还款金额
		// 平台服务费
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;
		BigDecimal bgs = new BigDecimal(serviceAmount);
		serviceAmount = bgs.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		double repayAmount = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine + serviceAmount;

		if (bills.getStatus().code == 0) {
			// 更新借款账单还款状态
			// boolean updateBill = updateRepaymentData(bill.id,
			// t_bill.Status.NORMAL_REPAYMENT);
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.NORMAL_REPAYMENT, businessSeqNo,
					repayAmount);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		if (bills.getStatus().code == 1) {
			// 更新借款账单还款状态
			// boolean updateBill = updateRepaymentData(bill.id,
			// t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT);
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT, businessSeqNo,
					repayAmount);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		boolean updateUserFund = userFundService.userFundMinus(bill.user_id, repayAmount);
		if (!updateUserFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund refreshUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (refreshUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		summaryParam.put("bidNo", bid.getBid_no());

		boolean repayRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest + serviceAmount,
				refreshUserFund.balance + loanOverdueFine, refreshUserFund.freeze, OperationType.REPAYMENT,
				summaryParam);
		if (!repayRecord) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			boolean overdueRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id, loanOverdueFine,
					refreshUserFund.balance, refreshUserFund.freeze, OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!overdueRecord) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", refreshUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest + serviceAmount);
		// 创蓝接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame);
		// 还款成功
		// 焦云接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame,JYSMSModel.MODEL_REPAYMENT_SUCC);

		// 修改标的状态
		if (bid.trust_status == 3) {
			String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			ResultInfo resultInfo = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo1,
					bid, ServiceType.BID_MODIFY, ProjectStatus.REPAYMENT, null);
			if (resultInfo.code < 0) {
				LoggerUtil.info(false, "托管标的状态：修改为还款中失败！");
			} else {
				int a = bidService.updateTrsutBidStatus(bid.id, 3, 6);
				if (a != 1) {
					LoggerUtil.info(false, "本地标的状态：修改为还款中失败！");
				}
			}

		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 代偿还款 - 回调业务
	 * 
	 * @author liuyang
	 * @create 2018.01.11
	 */
	public ResultInfo doRepaymentOneCompensate(String businessSeqNo, t_bill bill, double loanOverdueFine, t_bid bid) {

		ResultInfo result = new ResultInfo();

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		t_bill bills = billService.findByID(bill.id);
		if (bills == null) {
			result.code = -1;
			result.msg = "更新借款账单还款数据失败";

			return result;
		}

		// 服务费
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bgs = new BigDecimal(serviceAmount);
		serviceAmount = bgs.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// 扣除借款人还款金额
		double repayAmount = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine + serviceAmount;// 还有逾期罚息

		if (bills.getStatus().code == 0) {
			// 更新借款账单还款状态
			// boolean updateBill = updateRepaymentData(bill.id,
			// t_bill.Status.NORMAL_REPAYMENT);
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.NORMAL_REPAYMENT, businessSeqNo,
					repayAmount);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		if (bills.getStatus().code == 1) {
			// 更新借款账单还款状态
			// boolean updateBill = updateRepaymentData(bill.id,
			// t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT);
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_PRINCIIPAL_REPAYMENT, businessSeqNo,
					repayAmount);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		boolean updateUserFund = userFundService.userFundMinus(bill.user_id, repayAmount);
		if (!updateUserFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund refreshUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (refreshUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加担保方代偿金额
		boolean updateUserFunds = userFundService.userFundAdd(bill.guarantee_user_id, repayAmount - serviceAmount);
		if (!updateUserFunds) {
			result.code = -1;
			result.msg = "添加担保方可用余额失败";

			return result;
		}

		// 更新担保方资产签名
		userFundService.userFundSignUpdate(bill.guarantee_user_id);

		// 刷新担保方资金信息
		t_user_fund refreshUserFunds = userFundService.queryUserFundByUserId(bill.guarantee_user_id);
		if (refreshUserFunds == null) {
			result.code = -1;
			result.msg = "获取担保人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		summaryParam.put("bidNo", bid.getBid_no());

		boolean repayRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest + serviceAmount,
				refreshUserFund.balance + loanOverdueFine, refreshUserFund.freeze, OperationType.REPAYMENT,
				summaryParam);
		if (!repayRecord) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			boolean overdueRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id, loanOverdueFine,
					refreshUserFund.balance, refreshUserFund.freeze, OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!overdueRecord) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", refreshUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest + serviceAmount);
		// 创蓝接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame);
		// 还款成功
		// 焦云接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame,JYSMSModel.MODEL_REPAYMENT_SUCC);

		// 修改标的状态
		if (bid.trust_status == 3) {
			String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			ResultInfo resultInfo = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo1,
					bid, ServiceType.BID_MODIFY, ProjectStatus.REPAYMENT, null);
			if (resultInfo.code < 0) {
				LoggerUtil.info(false, "托管标的状态：修改为还款中失败！");
			} else {
				int a = bidService.updateTrsutBidStatus(bid.id, 3, 6);
				if (a != 1) {
					LoggerUtil.info(false, "本地标的状态：修改为还款中失败！");
				}
			}

		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 正常出款 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.09.26
	 */
	public ResultInfo doPaymentOne(t_bid bid, t_bill bill, long userId) {

		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		// 查询待还款的理财账单
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);

		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < billInvestList.size(); i++) {

			// 理财服务费
			double investServiceFee = FeeCalculateUtil
					.getOriginalInvestManagerFee(billInvestList.get(i).receive_interest, bid.service_fee_rule);

			// 理财人收益
			double receiveAmt = billInvestList.get(i).receive_corpus + billInvestList.get(i).receive_interest
					+ billInvestList.get(i).overdue_fine - investServiceFee;

			// 投资人收益
			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", i + "");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", bid.object_acc_no);// 标的台账账户
			accMap.put("cebitAccountNo", billInvestList.get(i).user_id);// 投资方台账账户
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(receiveAmt));///////////////
			accMap.put("summaryCode", "T05");

			accountList.add(accMap);

			// 平台收益
			Map<String, Object> accMap2 = new HashMap<String, Object>();
			accMap2.put("oderNo", (billInvestList.size() + i) + "");
			accMap2.put("oldbusinessSeqNo", "");
			accMap2.put("oldOderNo", "");
			accMap2.put("debitAccountNo", bid.object_acc_no);// 标的台账账户
			accMap2.put("cebitAccountNo", YbConsts.COSTNO);// 费用账户
			accMap2.put("currency", "CNY");
			accMap2.put("amount", YbUtils.formatAmount(investServiceFee));///////////////
			accMap2.put("summaryCode", "T12");

			accountList.add(accMap2);

			// 合同列表
			Map<String, Object> conMap = new HashMap<String, Object>();
			conMap.put("oderNo", i + "");
			conMap.put("contractType", "");
			conMap.put("contractRole", "");
			conMap.put("contractFileNm", "");
			conMap.put("debitUserid", "");
			conMap.put("cebitUserid", "");

			contractList.add(conMap);
		}

		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.PAYMENT);

		// 出款 - 托管请求
		if (ConfConst.IS_TRUST) {

			result = PaymentProxy.getInstance().fundTrade(OrderNoUtil.getClientSn(), userId, businessSeqNo, bid,
					ServiceType.PAYMENT, EntrustFlag.UNENTRUST, accountList, contractList);
			if (result.code < 0) {

				LoggerUtil.info(true, result.msg);
				return result;
			} else {
				bill.setStatus(t_bill.Status.BILL_STATUS_PAYMENT);
				return doPaymentCB(businessSeqNo, bid, billInvestList, bill);
			}

		}

		return result;
	}

	/**
	 * 正常出款 - 回调业务
	 * 
	 * @author niu
	 * @create 2017.09.27
	 */
	public ResultInfo doPaymentCB(String businessSeqNo, t_bid bid, List<t_bill_invest> billInvestList, t_bill bill) {

		ResultInfo result = new ResultInfo();

		/** 借款人扣除的逾期罚息 */
		double loanOverdueFine = 0; // 借款人扣除的逾期罚息 = 理财人收取的逾期罚息之和

		for (int i = 0; i < billInvestList.size(); i++) {
			t_bill_invest billInvest = billInvestList.get(i);

			// 更新理财账单收款情况
			// boolean receive =
			// billInvestService.updateReceiveData(billInvest.id);
			boolean receive = billInvestService.updateReceiveData(billInvest.id, businessSeqNo);
			if (!receive) {
				result.code = -1;
				result.msg = "理财人收到回款，更新理财账单回款数据失败";

				return result;
			}

			// 理财服务费
			double investServiceFee = FeeCalculateUtil
					.getOriginalInvestManagerFee(billInvestList.get(i).receive_interest, bid.service_fee_rule);

			// 理财人收益
			double receiveAmt = billInvestList.get(i).receive_corpus + billInvestList.get(i).receive_interest
					+ billInvestList.get(i).overdue_fine - investServiceFee;

			boolean isSignSuccess = true; // 用户签名是否通过
			result = userFundService.userFundSignCheck(billInvest.user_id);
			if (result.code < 1) {
				isSignSuccess = false;
			}

			boolean addFund = userFundService.userFundAdd(billInvest.user_id, receiveAmt);
			if (!addFund) {
				result.code = -1;
				result.msg = "增加理财人可用余额失败";

				return result;
			}

			// 刷新理财人资金信息
			t_user_fund investUserFund = userFundService.queryUserFundByUserId(billInvest.user_id);
			if (investUserFund == null) {
				result.code = -1;
				result.msg = "获取理财资金信息失败";

				return result;
			}

			// 如果投资用户账户资金没有遭到非法改动，那么就更新其篡改标志，否则不更新
			if (isSignSuccess) {
				userFundService.userFundSignUpdate(billInvest.user_id);
			}

			// 添加理财人收款记录
			Map<String, String> summaryParam = new HashMap<String, String>();
			summaryParam.put("billInvestNo", billInvest.bill_invest_no);
			summaryParam.put("bidNo", bid.getBid_no());
			boolean addDeal = dealUserService.addDealUserInfo(businessSeqNo, billInvest.user_id,
					billInvest.receive_corpus + billInvest.receive_interest,
					investUserFund.balance + investServiceFee - billInvest.overdue_fine, investUserFund.freeze,
					t_deal_user.OperationType.RECEIVE, summaryParam);// 此时不计“理财服务费”和“逾期罚息”
			if (!addDeal) {
				result.code = -1;
				result.msg = "添加理财人收款记录失败";

				return result;
			}

			// 添加逾期罚息记录
			if (billInvest.overdue_fine > 0) {
				boolean addOverDueDeal = dealUserService.addDealUserInfo(businessSeqNo, billInvest.user_id,
						billInvest.overdue_fine, investUserFund.balance + investServiceFee, investUserFund.freeze,
						t_deal_user.OperationType.RECEIVE_OVERDUE_FINE, summaryParam);// 此时不计“理财服务费”
				if (!addOverDueDeal) {
					result.code = -1;
					result.msg = "添加逾期罚息记录失败";

					return result;
				}
			}

			// 添加扣除理财服务费记录
			boolean addDeductInvestFee = dealUserService.addDealUserInfo(businessSeqNo, billInvest.user_id,
					investServiceFee, investUserFund.balance, investUserFund.freeze,
					t_deal_user.OperationType.INVEST_SERVICE_FEE, summaryParam);
			if (!addDeductInvestFee) {
				result.code = -1;
				result.msg = "添加扣除理财服务费记录失败";

				return result;
			}

			// 添加平台收支记录
			Map<String, Object> dealRemarkParam = new HashMap<String, Object>();
			dealRemarkParam.put("bill_invest_no", billInvest.bill_invest_no);
			dealRemarkParam.put("bidNo", bid.getBid_no());

			boolean addPlatDeal = dealPlatformService.addPlatformDeal(billInvest.user_id, investServiceFee,
					t_deal_platform.DealRemark.INVEST_FEE, dealRemarkParam);
			if (!addPlatDeal) {
				result.code = -1;
				result.msg = "添加平台收支记录失败";

				return result;
			}

			String bid_name = billInvest.bids.title;
			if (billInvest.bids.title.indexOf("【") != -1) {
				bid_name = bid_name.replace("【", "");
			}
			if (billInvest.bids.title.indexOf("】") != -1) {
				bid_name = bid_name.replace("】", "");
			}

			// 通知理财人
			Map<String, Object> sceneParame = new HashMap<String, Object>();
			sceneParame.put("user_name", investUserFund.name);
			sceneParame.put("bid_no", billInvest.bids.getBid_no());
			sceneParame.put("bid_name", bid_name);
			sceneParame.put("bill_no", billInvest.bill_invest_no);
			sceneParame.put("amount",
					billInvest.receive_corpus + billInvest.receive_interest + billInvest.overdue_fine);
			sceneParame.put("principal", billInvest.receive_corpus);
			sceneParame.put("interest", billInvest.receive_interest);
			sceneParame.put("fee", investServiceFee);
			sceneParame.put("balance", receiveAmt);
			// 投资回款
			// 创蓝接口
			// noticeService.sendSysNotice(billInvest.user_id,
			// NoticeScene.INVEST_SECTION, sceneParame);
			// 焦云接口
			t_user user = t_user.findById(billInvest.user_id);
			Boolean flag = smsJyCountService.judgeIsSend(user.mobile, JYSMSModel.MODEL_INVEST_SECTION);
			if (flag) {
				noticeService.sendSysNotice(billInvest.user_id, NoticeScene.INVEST_SECTION, sceneParame,
						JYSMSModel.MODEL_INVEST_SECTION);
			}

			// 计算借款人扣除的逾期罚息
			loanOverdueFine += billInvest.overdue_fine;

			// 加息券返息
			ResultInfo res = addRateTicketService.returnRateOfAddRate(billInvest.user_id, billInvest.invest_id,
					billInvest);

		}

		// 判断这个借款标是否已经全部还完
		if (isEndPayment(bid.id)) {
			boolean modifySuccess = bidService.bidEnd(bid.id);
			if (modifySuccess) {
				Logger.info("标的状态：已修改为结束");
			}
		}

		int us = billDao.updateBillIsPayment(bill.id);
		if (us != 1) {
			LoggerUtil.info(false, "是否出款状态修改失败！");
		}

		result.code = 1;
		result.msg = "出款成功";

		return result;
	}

	/**
	 * 后台线下收款 - 业务处理
	 * 
	 * @author liuyang
	 * @create 2017.10.12
	 */
	public ResultInfo doOfflineReceives(String businessSeqNo, t_bill bill, double loanOverdueFine, t_bid bid) {

		ResultInfo result = new ResultInfo();

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		t_bill bills = billService.findByID(bill.id);
		if (bills == null) {
			result.code = -1;
			result.msg = "更新借款账单还款数据失败";

			return result;
		}

		if (bills.getStatus().code == 0) {
			// 更新借款账单还款状态
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_RECEIVE);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		if (bills.getStatus().code == 1) {
			// 更新借款账单还款状态
			boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";

				return result;
			}
		}

		// 更新借款人资产签名
		userFundService.userFundSignUpdate(bill.user_id);

		// 刷新借款人资金信息
		t_user_fund refreshUserFund = userFundService.queryUserFundByUserId(bill.user_id);
		if (refreshUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);

		boolean repayRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id,
				bill.repayment_corpus + bill.repayment_interest, refreshUserFund.balance + loanOverdueFine,
				refreshUserFund.freeze, OperationType.REPAYMENT, summaryParam);
		if (!repayRecord) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			boolean overdueRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id, loanOverdueFine,
					refreshUserFund.balance, refreshUserFund.freeze, OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!overdueRecord) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", refreshUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);

		noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC, sceneParame);

		// 修改标的状态
		if (bid.trust_status == 3) {
			String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			ResultInfo resultInfo = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo1,
					bid, ServiceType.BID_MODIFY, ProjectStatus.REPAYMENT, null);
			if (resultInfo.code < 0) {
				LoggerUtil.info(false, "托管标的状态：修改为还款中失败！");
			} else {
				int a = bidService.updateTrsutBidStatus(bid.id, 3, 6);
				if (a != 1) {
					LoggerUtil.info(false, "本地标的状态：修改为还款中失败！");
				}
			}

		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 前台提前还款 - 业务处理
	 * 
	 * @author liuyang
	 * @create 2017.10.14
	 */
	public ResultInfo doAheadReceive(String businessSeqNo, t_bill bill, double loanOverdueFine, t_bid bid, long userId,
			double advanceAmt) {

		ResultInfo result = new ResultInfo();

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		BigDecimal bg = new BigDecimal(advanceAmt);
		double amounts = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// 更新借款账单还款状态
		// boolean updateBill = updateRepaymentData(bill.id,
		// t_bill.Status.ADVANCE_lINE_RECEIVE);
		boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_lINE_RECEIVE, businessSeqNo, amounts);
		if (!updateBill) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		// 扣除借款人还款金额
		boolean updateUserFund = userFundService.userFundMinus(userId, amounts);
		if (!updateUserFund) {
			result.code = -1;
			result.msg = "扣除借款人可用余额失败";

			return result;
		}

		// 更新借款人资产签名
		userFundService.userFundSignUpdate(userId);

		// 刷新借款人资金信息
		t_user_fund refreshUserFund = userFundService.queryUserFundByUserId(userId);
		if (refreshUserFund == null) {
			result.code = -1;
			result.msg = "获取借款人资金信息失败";

			return result;
		}

		// 添加借款人还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		summaryParam.put("bidNo", bid.getBid_no());

		boolean repayRecord = dealUserService.addDealUserInfo(businessSeqNo, userId, amounts,
				refreshUserFund.balance + loanOverdueFine, refreshUserFund.freeze, OperationType.REPAYMENT,
				summaryParam);
		if (!repayRecord) {
			result.code = -1;
			result.msg = "添加借款人还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			boolean overdueRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id, loanOverdueFine,
					refreshUserFund.balance, refreshUserFund.freeze, OperationType.REPAYMENT, summaryParam);
			if (!overdueRecord) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", refreshUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", amounts);
		// 还款成功
		// 创蓝接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame);
		// 焦云接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame,JYSMSModel.MODEL_REPAYMENT_SUCC);

		// 修改标的状态
		if (bid.trust_status == 3) {
			String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			ResultInfo resultInfo = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo1,
					bid, ServiceType.BID_MODIFY, ProjectStatus.REPAYMENT, null);
			if (resultInfo.code < 0) {
				LoggerUtil.info(false, "托管标的状态：修改为还款中失败！");
			} else {
				int a = bidService.updateTrsutBidStatus(bid.id, 3, 6);
				if (a != 1) {
					LoggerUtil.info(false, "本地标的状态：修改为还款中失败！");
				}
			}

		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 后台本息垫付 - 业务处理
	 * 
	 * @author liuyang
	 * @create 2017.10.12
	 */
	public ResultInfo doPrincipalAdvance(String businessSeqNo, t_bill bill, double loanOverdueFine, t_bid bid,
			long userId) {

		ResultInfo result = new ResultInfo();

		// 判断还款的借款标是否有未完成的债权转让，如果有，将其状态回归原态,并解冻竞拍者资金
		result = debtService.queryDebtTransferNoComplete(bill.bid_id);
		if (result.code < 1) {

			return result;
		}

		// 扣除担保方还款金额
		double repayAmount = bill.repayment_corpus + bill.repayment_interest + loanOverdueFine;// 还有逾期罚息

		// 更新借款账单还款状态
		// boolean updateBill = updateRepaymentData(bill.id,
		// t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT);
		boolean updateBill = updateRepaymentData(bill.id, t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT, businessSeqNo,
				repayAmount);
		if (!updateBill) {
			result.code = -1;
			result.msg = "借款人还款成功，更新借款账单还款数据失败";

			return result;
		}

		boolean updateUserFund = userFundService.userFundMinus(userId, repayAmount);
		if (!updateUserFund) {
			result.code = -1;
			result.msg = "扣除担保方可用余额失败";

			return result;
		}

		// 更新担保方资产签名
		userFundService.userFundSignUpdate(userId);

		// 刷新担保方资金信息
		t_user_fund refreshUserFund = userFundService.queryUserFundByUserId(userId);
		if (refreshUserFund == null) {
			result.code = -1;
			result.msg = "获取担保方资金信息失败";

			return result;
		}

		// 添加担保方还款记录
		Map<String, String> summaryParam = new HashMap<String, String>();
		summaryParam.put("billNo", bill.bill_no);
		summaryParam.put("bidNo", bid.getBid_no());

		boolean repayRecord = dealUserService.addDealUserInfo(businessSeqNo, userId,
				bill.repayment_corpus + bill.repayment_interest, refreshUserFund.balance + loanOverdueFine,
				refreshUserFund.freeze, OperationType.REPAYMENT_GUARANTEE_AMOUNT, summaryParam);
		if (!repayRecord) {
			result.code = -1;
			result.msg = "添加担保方还款记录失败";

			return result;
		}

		// 添加借款人逾期罚息记录
		if (loanOverdueFine > 0) {
			boolean overdueRecord = dealUserService.addDealUserInfo(businessSeqNo, bill.user_id, loanOverdueFine,
					refreshUserFund.balance, refreshUserFund.freeze, OperationType.REPAYMENT_OVERDUE_FINE,
					summaryParam);
			if (!overdueRecord) {
				result.code = -1;
				result.msg = "添加逾期罚息记录失败";

				return result;
			}
		}

		// 通知借款人
		Map<String, Object> sceneParame = new HashMap<String, Object>();
		sceneParame.put("user_name", refreshUserFund.name);
		sceneParame.put("bill_no", bill.bill_no);
		sceneParame.put("amount", bill.repayment_corpus + bill.repayment_interest);
		// 还款成功
		// 创蓝接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame);
		// 焦云接口
		// noticeService.sendSysNotice(bill.user_id, NoticeScene.REPAYMENT_SUCC,
		// sceneParame,JYSMSModel.MODEL_REPAYMENT_SUCC);

		// 修改标的状态
		if (bid.trust_status == 3) {
			String businessSeqNo1 = OrderNoUtil.getNormalOrderNo(ServiceType.BID_MODIFY);
			ResultInfo resultInfo = PaymentProxy.getInstance().bidInfoAysn(OrderNoUtil.getClientSn(), businessSeqNo1,
					bid, ServiceType.BID_MODIFY, ProjectStatus.REPAYMENT, null);
			if (resultInfo.code < 0) {
				LoggerUtil.info(false, "托管标的状态：修改为还款中失败！");
			} else {
				int a = bidService.updateTrsutBidStatus(bid.id, 3, 6);
				if (a != 1) {
					LoggerUtil.info(false, "本地标的状态：修改为还款中失败！");
				}
			}

		}

		result.code = 1;
		result.msg = "还款成功";

		return result;
	}

	/**
	 * 垫付出款 - 回调业务
	 * 
	 * @author liuyang
	 * @create 2017.10.13
	 */
	public ResultInfo doPaidMoney(t_bid bid, t_bill bill, long userId) {

		ResultInfo result = new ResultInfo();

		if (bill == null) {
			throw new InvalidParameterException("bill is null");
		}

		// 查询待还款的理财账单
		List<t_bill_invest> billInvestList = billInvestService.queryNOReceiveInvestBills(bill.bid_id, bill.period);

		if (billInvestList == null || billInvestList.size() <= 0) {
			result.code = -1;
			result.msg = "查询待还的理财账单失败";

			return result;
		}

		List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();

		List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < billInvestList.size(); i++) {

			// 理财服务费
			double investServiceFee = FeeCalculateUtil
					.getOriginalInvestManagerFee(billInvestList.get(i).receive_interest, bid.service_fee_rule);

			// 理财人收益
			double receiveAmt = billInvestList.get(i).receive_corpus + billInvestList.get(i).receive_interest
					+ billInvestList.get(i).overdue_fine - investServiceFee;

			// 投资人收益
			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", i + "");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", bid.object_acc_no);// 标的台账账户
			accMap.put("cebitAccountNo", billInvestList.get(i).user_id);// 投资方台账账户
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(receiveAmt));///////////////
			accMap.put("summaryCode", "T05");

			accountList.add(accMap);

			// 平台收益
			Map<String, Object> accMap2 = new HashMap<String, Object>();
			accMap2.put("oderNo", (billInvestList.size() + i) + "");
			accMap2.put("oldbusinessSeqNo", "");
			accMap2.put("oldOderNo", "");
			accMap2.put("debitAccountNo", bid.object_acc_no);// 标的台账账户
			accMap2.put("cebitAccountNo", YbConsts.COSTNO);// 费用账户
			accMap2.put("currency", "CNY");
			accMap2.put("amount", YbUtils.formatAmount(investServiceFee));///////////////
			accMap2.put("summaryCode", "T12");

			accountList.add(accMap2);

			// 合同列表
			Map<String, Object> conMap = new HashMap<String, Object>();
			conMap.put("oderNo", i + "");
			conMap.put("contractType", "");
			conMap.put("contractRole", "");
			conMap.put("contractFileNm", "");
			conMap.put("debitUserid", "");
			conMap.put("cebitUserid", "");

			contractList.add(conMap);
		}

		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.PAYMENT);

		// 出款 - 托管请求
		if (ConfConst.IS_TRUST) {

			result = PaymentProxy.getInstance().fundTrade(OrderNoUtil.getClientSn(), userId, businessSeqNo, bid,
					ServiceType.PAYMENT, EntrustFlag.UNENTRUST, accountList, contractList);
			if (result.code < 0) {

				// LoggerUtil.info(true, result.msg);
				return result;
			} else {
				bill.setStatus(t_bill.Status.BILL_STATUS_PAYMENT);
				return doPaymentCB(businessSeqNo, bid, billInvestList, bill);
			}

		}

		return result;
	}

	/**
	 * 查询待还借款人数量
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findBorrowerCount() {

		return billDao.findBorrowerCount();
	}

	/**
	 * 查询总借款人数量
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findTotalBorrowerCount() {

		return billDao.findTotalBorrowerCount();
	}

	/**
	 * 查询总代偿笔数
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public int findCompensateCount() {

		return billDao.findCompensateCount();
	}

	/**
	 * 查询总代偿金额
	 * 
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月25日
	 *
	 */
	public double findCompensateMoney() {

		return billDao.findCompensateMoney();
	}

	/**
	 * 借款人待还金额
	 *
	 * @return
	 *
	 * @author liuyang
	 * @createDate 2017年12月27日
	 */
	public List<Map<String, Object>> queryMouthInvestLists(long limit) {

		return billDao.queryMouthInvestLists(limit);
	}

	/**
	 * 
	 * @Title: getBillByBid
	 * @description 根据bidId查询
	 * @param bid_id
	 * @return List<t_bill>
	 * @author likai
	 * @createDate 2018年12月18日 上午9:17:45
	 */
	public List<t_bill> getBillByBid(Long bid_id) {
		return billDao.getBillByBid(bid_id);
	}

	/**
	 * 通过借款账单id 添加交易信息数据
	 * 
	 * @author guoShiJie
	 * @createDate 2018.10.18
	 */
	public Boolean addTradeInfoByBillId(Long billId, String bussinessSeqNo, double paymentAmount) {
		t_bill bill = billDao.findByID(billId);
		if (bill == null) {
			return false;
		}
		return tradeInfoService.saveTradeInfo(DateUtil.addDay(new Date(), 0), bussinessSeqNo,
				t_trade_info.TradeType.RECEIVE, bill.user_id, "CNY", new BigDecimal(paymentAmount), bill.bid_id);
	}

	/**
	 * 
	 * @Title: pageOfBill
	 * @description: 风控项目贷后管理所有数据
	 *
	 * @return
	 * @return List<Bills>
	 *
	 * @author HanMeng
	 * @createDate 2018年12月18日-上午11:08:48
	 */
	public List<Bills> pageOfBill() {

		return billDao.pageOfBill();
	}

	/**
	 * 
	 * @Title: getBymobile
	 * @description: 贷后管理按电话搜索（风控项目搜索）
	 *
	 * @param mobile
	 * @return
	 * @return List<Bills>
	 *
	 * @author HanMeng
	 * @createDate 2018年12月17日-下午5:07:00
	 */
	public List<Bills> getBymobile(String mobile) {

		return billDao.getBymobile(mobile);
	}

	/**
	 * 
	 * 
	 * @Title: getByoverdue
	 * @description: 贷后管理按逾期状态搜索（风控项目搜索）
	 *
	 * @param overdue
	 * @return
	 * @return List<Bills>
	 *
	 * @author HanMeng
	 * @createDate 2018年12月18日-上午11:08:18
	 */
	public List<Bills> getByoverdue(Integer is_overdue) {

		return billDao.getByoverdue(is_overdue);
	}
	
	/**
	 * 
	 * @Title: sendMesToFinance
	 * 
	 * @description 定时查询还款日并发送短信
	 * @return Boolean    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月31日-下午1:49:31
	 */
	public static Boolean sendMesToFinance(){
		//查询还款日的标
		List<Bill> billList = billDao.findBillByStatus();
		if (billList == null || billList.size() <= 0) {
			return false;
		}
		Date taDay = new Date();
		ArrayList<String> bidlist = new ArrayList<>();
		for(Bill b : billList){
			//还款时间
			Date reTime = b.repayment_time; 
			if (taDay.before(reTime)) {
				//当前时间跟还款日相差如果是一天,则把bid放到list中
				int num = DateUtil.getDaysBetween(taDay, reTime);
				String bid = b.bid_no;
				if (num == 1) {
					bidlist.add(bid);
				}
			}
			
		}
		Map<String, String> map = new HashMap<String,String>();
		map.put("bid", bidlist.toString());
		//还款通知人电话
		List<t_transfer_notice> noticeList = transferNoticeService.findRepayNotice();
		if (noticeList != null && noticeList.size() >= 0) {
			for (t_transfer_notice notice :noticeList) {
				String phone = notice.notice_mobile;
				if (JYSMSUtil.sendMessage(phone, JYSMSModel.MODEL_BILLS_DUE.tplId, map)){				
					return true;
				}
			}
		}
		return false;	
	}
}
