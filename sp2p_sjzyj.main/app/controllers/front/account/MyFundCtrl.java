package controllers.front.account;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import models.common.bean.CurrSupervisor;
import models.common.bean.CurrUser;
import models.common.entity.t_cost;
import models.common.entity.t_guarantee;
import models.common.entity.t_loan_profession;
import models.common.entity.t_pact;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.BidItemOfUserSubject;
import models.core.bean.DebtInvest;
import models.core.bean.FrontMyLoanBid;
import models.core.bean.InvestReceive;
import models.core.bean.UserDebt;
import models.core.bean.UserInvestRecord;
import models.core.entity.t_audit_item_library;
import models.core.entity.t_auto_invest_setting;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import models.core.entity.t_invest;
import models.core.entity.t_invest_entrust;
import models.ext.redpacket.bean.AddRateTicketBean;
import models.main.bean.LoanContract;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Logger;
import play.mvc.With;
import services.common.GuaranteeService;
import services.common.LoanProfessionService;
import services.common.PactService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.AuditSubjectBidService;
import services.core.BidItemUserService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import services.core.DebtService;
import services.core.InvestService;
import services.ext.redpacket.AddRateTicketService;
import services.ext.redpacket.CouponService;
import yb.YbConsts;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
import yb.enums.ServiceType;

import com.shove.Convert;

import common.annotation.LoginCheck;
import common.annotation.SimulatedCheck;
import common.annotation.SubmitCheck;
import common.annotation.SubmitOnly;
import common.constants.ConfConst;
import common.constants.Constants;
import common.constants.SettingKey;
import common.enums.Client;
import common.utils.DateUtil;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.TimeUtil;
import common.utils.file.FileUtil;
import common.utils.number.Arith;
import controllers.common.BaseController;
import controllers.common.FrontBaseController;
import controllers.common.SubmitRepeat;
import controllers.common.interceptor.AccountInterceptor;
import controllers.common.interceptor.SimulatedInterceptor;
import controllers.front.seal.ElectronicSealCtrl;
import daos.core.AutoInvestSettingDao;

/**
 * 前台-账户中心-资产管理
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月17日
 */
@With({ AccountInterceptor.class, SubmitRepeat.class, SimulatedInterceptor.class })
public class MyFundCtrl extends FrontBaseController {

	protected static BillService billService = Factory.getService(BillService.class);

	protected static BidService bidService = Factory.getService(BidService.class);

	protected static InvestService investService = Factory.getService(InvestService.class);

	protected static AuditSubjectBidService auditsubjectbidservice = Factory.getService(AuditSubjectBidService.class);

	protected static BidItemUserService bidItemUserService = Factory.getSimpleService(BidItemUserService.class);

	protected static PactService pactService = Factory.getService(PactService.class);

	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);

	protected static DebtService debtService = Factory.getService(DebtService.class);

	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);

	protected static CouponService couponService = Factory.getService(CouponService.class);

	protected static AddRateTicketService addRateTicketService = Factory.getService(AddRateTicketService.class);

	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);

	private static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static GuaranteeService guaranteeService = Factory.getService(GuaranteeService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	protected static AutoInvestSettingDao autoInvestSettingDao = Factory.getDao(AutoInvestSettingDao.class);

	/**
	 * 我的财富-资产管理
	 *
	 * @author yaoyi
	 * @createDate 2016年1月12日
	 */
	public static void accountManagePre(int index) {

		/** 前台是否显示自动投标 */
		int isAutoInvestShow = Convert.strToInt(settingService.findSettingValueByKey(SettingKey.IS_AUTO_INVEST_SHOW),
				0);
		renderArgs.put("isAutoInvestShow", isAutoInvestShow);

		render(index);
	}

	/**
	 * 我的财富-资产管理-我的理财
	 *
	 * @author yaoyi
	 * @createDate 2016年1月12日
	 */
	public static void accountInvestPre(int currPage, int pageSize) {
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1) {
			pageSize = 5;
		}

		PageBean<UserInvestRecord> pageBean = investService.pageOfUserInvestRecord(currPage, pageSize,getCurrUser().id);

		if (pageBean != null && pageBean.page != null) {
			for (UserInvestRecord p : pageBean.page) {
				AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(p.id, getCurrUser().id, 3);
				if (couponUser == null) {
					p.coupon = p.apr;
				} else {
					p.coupon = couponUser.apr + p.apr;
				}

			}
		}

		render(pageBean);
	}

	/**
	 * 我的财富-资产管理-我的理财-异步查询某个投资是否能转让
	 * 
	 * @description 进入accountInvestPre.html后，会调用这个方法再次发起请求查询该笔投资是否能转让
	 *
	 * @param investId
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月30日
	 */
	public static boolean isInvestCanbeTransfered(long investId) {
		ResultInfo result = debtService.isInvestCanbeTransfered(investId);
		if (result.code == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 我的财富-资产管理-我的理财-进入债权申请页面
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	@SubmitCheck
	public static void applyDebtPre(String sign) {
		ResultInfo result = Security.decodeSign(sign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");

			renderJSON(result);
		}

		long investId = Long.parseLong((String) result.obj);
		result = debtService.isInvestCanbeTransfered(investId);
		if (result.code < 1) {

			renderJSON(result);
		}

		DebtInvest debtInvest = debtService.findDebtByInvestid(investId);
		if (debtInvest == null) {
			result.code = -1;
			result.msg = "没有查到投资的相关信息";

			renderJSON(result);
		}
		double half_principal = Arith.div(debtInvest.debt_principal * 8, 10, 0);// 本金的80%~100%

		render(debtInvest, half_principal);
	}

	/**
	 * 我的财富-资产管理-我的理财-债权申请
	 *
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年3月25日
	 */
	@SubmitOnly
	@SimulatedCheck
	public static void applyDebtTransfer(String sign, String title, int period, int transfer_price) {
		checkAuthenticity();
		ResultInfo result = Security.decodeSign(sign, Constants.INVEST_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			flash.error(result.msg);
			accountManagePre(0);
		}
		long investId = Long.parseLong((String) result.obj);
		if (StringUtils.isBlank(title) || title.length() > 20) {

			flash.error("转让标题长度有误");
			accountManagePre(0);
		}
		if (period <= 0 || period > 7) {
			flash.error("转让期限输入有误");
			accountManagePre(0);
		}
		if (transfer_price <= 0) {

			flash.error("转让底价输入有误");
			accountManagePre(0);
		}

		t_invest invest = investService.findByID(investId);
		t_bid bids = bidService.findByID(invest.bid_id);
		Date dates = new Date();

		// 判断标是否在锁定期
		if (bids.lock_deadline != 0) {

			if (bids.lock_time.getTime() > dates.getTime()) {
				flash.error("标在锁定期，无法进行转让");
				accountManagePre(0);
			}
		}

		result = debtService.applyDebtTransfer(investId, title, period, transfer_price);
		if (result.code < 1) {
			LoggerUtil.error(true, "债权转让申请失败:【%s】", result.msg);

			flash.error(result.msg);
			accountManagePre(0);
		}

		flash.success("债权转让申请成功");
		accountManagePre(3);
	}

	/**
	 * 我的财富-资产管理-我的借款
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public static void accountLoanPre(int currPage, int pageSize) {
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1) {
			pageSize = 5;
		}

		PageBean<FrontMyLoanBid> pageBean = bidService.pageOfBidFront(currPage, pageSize, getCurrUser().id);

		render(pageBean);
	}

	/**
	 * 我的财富-资产管理-受让债权(用户参与竞拍的债权)
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public static void accountInDebtPre(int currPage, int pageSize) {

		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1 || pageSize > 5) {
			pageSize = 5;
		}

		CurrUser currUser = getCurrUser();
		PageBean<UserDebt> pageBean = debtService.pageOfDebtByUser(currPage, pageSize, null, currUser.id);

		render(pageBean);
	}

	/**
	 * 我的财富-资产管理-转让债权(用户转让出去的债权)
	 *
	 * @author yaoyi
	 * @createDate 2016年1月13日
	 */
	public static void accountOutDebtPre(int currPage, int pageSize) {
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1 || pageSize > 5) {
			pageSize = 5;
		}

		CurrUser currUser = getCurrUser();
		PageBean<UserDebt> page = debtService.pageOfDebtByUser(currPage, pageSize, currUser.id, null);

		render(page);
	}

	/**
	 * 我的财富-资产管理-自动投标
	 *
	 * @author yaoyi,ZhouYuanZeng
	 * @createDate 2016年1月13日
	 */
	public static void accountAutoInvestPre() {
		long userId = getCurrUser().id;
		t_auto_invest_setting autoInvestOption = investService.findAutoInvestByUserId(userId);

		render(autoInvestOption);
	}

	/**
	 * 前台提前还款
	 * 
	 * @param billSign
	 * @createDate 2017年5月17日
	 * @author lihuijun
	 */
	public static void repaymentAll() {

		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.REPAYMENT);

		if (result.code < 0) {
			flash.error(result.msg);

			accountManagePre(1);
		}

		Map<String, String> reqParams = result.msgs;
		String businessSeqNo = result.msg;

		String billSign = reqParams.get("billSign");

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");

			error404();
		}

		long billId = Long.parseLong(result.obj.toString());

		t_bill bill = billService.findByID(billId);
		if (bill == null) {

			error404();
		}

		long userId = getCurrUser().id;

		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);

		if (userFund.payment_account == null || userFund.payment_account.equals("")) {
			flash.error("获取用户资金信息失败");
			accountManagePre(1);
		}

		List<t_bill_invest> billInvestList = billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		List<t_bill_invest> billInvestLists = billInvestService.getBillInvestListByBidIdAndStatuss(bill.bid_id,
				bill.period);
		double surplusCorpus = 0; // 所有待还本金
		for (t_bill_invest billIn : billInvestList) {
			surplusCorpus += billIn.receive_corpus;

		}
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		double surplusFine = 0; // 所有罚息（包括当月，因为当月是0所以可以不用去除）
		double surplusInterest = 0; // 所有利息（包括当月跟逾期利息）
		if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
			for (t_bill_invest interest : billInvestOverdueList) {
				surplusFine += interest.overdue_fine;
				surplusInterest += interest.receive_interest;
			}
		}

		// 服务费
		t_bid bid = bidService.findByID(bill.bid_id);

		// 服务费 借款金额 * 年化服务费率% / 12
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// 还款总额
		double advanceAmt = surplusCorpus + surplusInterest + surplusFine + serviceAmount; // 本金合计+利息合计+罚息合计
																							// +
																							// 平台服务费

		if (advanceAmt > userFund.balance) {
			flash.error("商户余额不足，请充值");
			accountManagePre(1);
		}

		/** 提前还款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {

			result = billService.advanceReceive(userId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);

				return;
			}

			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", "0");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", userFund.payment_account);// 借款人账户
			accMap.put("cebitAccountNo", bid.object_acc_no);
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(surplusCorpus));
			accMap.put("summaryCode", "T04");

			Map<String, Object> intMap = new HashMap<String, Object>();
			intMap.put("oderNo", "1");
			intMap.put("oldbusinessSeqNo", "");
			intMap.put("oldOderNo", "");
			intMap.put("debitAccountNo", userFund.payment_account);// 借款人账户
			intMap.put("cebitAccountNo", bid.object_acc_no);
			intMap.put("currency", "CNY");
			intMap.put("amount", YbUtils.formatAmount(Arith.add(surplusInterest, surplusFine)));
			intMap.put("summaryCode", "T12");

			// 平台服务费
			Map<String, Object> intsMap = new HashMap<String, Object>();
			intsMap.put("oderNo", "2");
			intsMap.put("oldbusinessSeqNo", "");
			intsMap.put("oldOderNo", "");
			intsMap.put("debitAccountNo", userFund.payment_account);// 借款人账户
			intsMap.put("cebitAccountNo", YbConsts.COSTNO);
			intsMap.put("currency", "CNY");
			intsMap.put("amount", YbUtils.formatAmount(serviceAmount));
			intsMap.put("summaryCode", "T12");

			List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
			accountList.add(accMap);
			accountList.add(intMap);
			accountList.add(intsMap);

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

				result = PaymentProxy.getInstance().offlineReceive(OrderNoUtil.getClientSn(), businessSeqNo, bid,
						ServiceType.REPAYMENT, EntrustFlag.UNENTRUST, accountList, contractList);
				if (result.code < 0) {
					LoggerUtil.info(true, result.msg);

					flash.error(result.msg);
				} else {
					result = billService.doAheadReceive(businessSeqNo, bill, bill.overdue_fine, bid, userId,
							advanceAmt);
					if (result.code < 0) {

						flash.error(result.msg);
					} else {
						Logger.info("借款人台账  -> 标的台账  -> 提前还款成功");

						flash.success("提前还款成功,请核对账单");

						// 提前还款改变账单中的信息
						billService.updateThisBill(billId, surplusCorpus, surplusInterest, surplusFine);

						List<t_bill> billList = billService.findBillByBidIdAndStatus(bill.bid_id);
						if (billList != null && billList.size() > 0) {
							for (t_bill bil : billList) {
								bil.setStatus(7);
								// bil.setIs_overdue(false);
								bil.repayment_corpus = 0;
								bil.repayment_interest = 0;
								bil.overdue_fine = 0;// 罚息置0
								bil.save();
							}
						}
						List<t_bill_invest> theBillInvests = billInvestService
								.billInvestListByBidIdAndPeriod(bill.bid_id, bill.period);
						if (billInvestList != null && billInvestList.size() > 0) { // 某标所有未还账单投资的集合
							double interest = 0;
							double fine = 0;
							for (t_bill_invest bine : theBillInvests) {
								interest = 0;
								fine = 0;
								for (t_bill_invest bin : billInvestList) {
									if (bine.invest_id == bin.invest_id && !bine.id.equals(bin.id)) {
										bine.receive_corpus += bin.receive_corpus;
									}
								}
								if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
									for (t_bill_invest binOver : billInvestOverdueList) {
										if (bine.invest_id == binOver.invest_id) {
											interest += binOver.receive_interest;
											fine += binOver.overdue_fine;
										}
									}
								}
								bine.receive_interest = interest;
								bine.overdue_fine = fine;
								bine.save();
							}

							for (t_bill_invest bin : billInvestLists) {
								boolean flag = false;
								for (t_bill_invest bine : theBillInvests) {
									if (bine.id.equals(bin.id)) {
										flag = true;
										break;
									}
								}
								if (!flag) {
									billInvestService.updateThisBillInvest(bin.id);
								}
							}
						}

						/*
						 * if (billService.isEndPayment(bill.bid_id)) {
						 * bidService.bidEnd(bill.bid_id); }
						 */

						// 正常还款时改变贷款业务信息表中的数据
						t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
						t_bill bills = billService.findByID(billId);
						if (loans != null) {
							loans.last_repayment_date = new Date();
							Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
							loans.actual_amount = bills.repayment_corpus + zhis;
							loans.time = new Date();
							Integer zhi = Integer.parseInt(loans.resudue_months);
							loans.resudue_months = (zhi - 1) + "";
							loans.save();
						}

						// 查询费用账户可用余额
						String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);

						ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code,
								businessSeqNo3);
						if (result3.code < 1) {

							flash.error("查询费用账户可用余额异常");
							accountManagePre(1);
						}

						Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
						double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);

						// 拼接费用明细的备注
						StringBuffer remark = new StringBuffer();
						remark.append(bid.getBid_no());

						t_user_info userInfo = userInfoService.findUserInfoByUserId(bid.user_id);
						if (userInfo != null) {
							remark.append(userInfo.reality_name);
						}

						remark.append(bid.title).append(bid.period).append("个月");

						// 保存费用账户明细
						t_cost cost = new t_cost();
						cost.balance = cosBalance;
						cost.amount = serviceAmount;
						cost.type = 0;
						cost.sort = 3;
						cost.bid_id = bid.id;
						cost.time = new Date();
						cost.remark = remark.toString();
						cost.save();
					}
				}

				flash.success("还款成功,请核对账单");
				accountManagePre(1);
			}

			return;
		}

		flash.error("账单状态非法");
		accountManagePre(1);
	}

	/**
	 * 审核资料下拉
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public static void pullDownAuditSubjectPre(long bidId) {

		if (bidId < 1) {
			error404();
		}

		t_bid tb = bidService.findByID(bidId);
		if (tb == null) {

			error404();
		}
		List<BidItemOfUserSubject> userLoop = auditsubjectbidservice.queryBidRefSubjectFront(bidId);

		render(tb, userLoop);
	}

	/**
	 * 用户借款账单下拉
	 *
	 * @author yaoyi
	 * @createDate 2016年1月16日
	 */
	public static void pullDownMyBillPre(long bidId) {

		if (bidId < 1) {

			error404();
		}

		t_bid tb = bidService.findByID(bidId);
		if (tb == null) {

			error404();
		}

		List<t_bill> billList = billService.findBillByBidId(bidId);
		int totalPeriod = billService.findBidTotalBillCount(bidId);

		// 服务费 借款金额 * 年化服务费率% / 12
		double serviceAmount = tb.amount * tb.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		render(billList, totalPeriod, serviceAmount);
	}

	/**
	 * 用户投资账单下拉
	 *
	 * @author liudong
	 * @createDate 2016年1月20日
	 */
	public static void pullDownMyBillInvestPre(long investId) {

		t_invest invest1 = investService.findByID(investId);

		List<t_bill_invest> billInvest = billInvestService.findListByColumn("invest_id = ?", investId);

		List<InvestReceive> billInvestList = billInvestService.queryMyBillInvestFront(investId);

		for (InvestReceive invest : billInvestList) {
			for (t_bill_invest bill : billInvest) {
				AddRateTicketBean couponUser = addRateTicketService.findTicketByUserId(bill.invest_id, getCurrUser().id,
						3);
				if (couponUser == null) {
					invest.coupon_interest = invest.receive_interest;
				} else {
					invest.coupon_interest = invest.receive_interest + couponUser.apr * invest1.amount / 1200;
				}
			}
		}
		render(billInvestList);
	}

	/**
	 * 用户上传审核资料，及时保存入库
	 *
	 * @param imgFile
	 * @param bidId
	 * @param bidAuditSubjectId
	 * @param fileName
	 * @param subjectId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月18日
	 */
	@SimulatedCheck
	public static void imagesUpload(File imgFile, long bidId, long bidAuditSubjectId, String fileName, long subjectId) {
		response.contentType = "text/html";
		ResultInfo result = new ResultInfo();
		if (imgFile == null) {
			result.code = -1;
			result.msg = "请选择要上传的图片";

			renderJSON(result);
		}
		if (StringUtils.isBlank(fileName) || fileName.length() > 32) {
			result.code = -1;
			result.msg = "图片名称长度应该位于1~32位之间";

			renderJSON(result);
		}

		result = FileUtil.uploadImgags(imgFile);
		if (result.code < 0) {

			renderJSON(result);
		}

		Map<String, Object> fileInfo = (Map<String, Object>) result.obj;
		fileInfo.put("imgName", fileName);

		boolean saveflag = bidItemUserService.saveBidItemUser(getCurrUser().id, bidId, bidAuditSubjectId, fileName,
				(String) fileInfo.get("staticFileName"), subjectId);
		if (!saveflag) {
			result.code = -1;
			result.msg = "图片保存失败";

			renderJSON(result);
		}

		renderJSON(result);
	}

	/**
	 * 用户提交审核资料（上传之后，在提交）
	 *
	 * @param bidItemUserId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月18日
	 */
	@SimulatedCheck
	public static void submitBidItemUser(long bidItemUserId) {
		ResultInfo res = new ResultInfo();

		int sub = bidItemUserService.submitBidItemUser(bidItemUserId);
		if (sub < 1) {
			res.code = -1;
			res.msg = "保存失败";
			LoggerUtil.error(true, "用户提交上传的审核科目资料失败，失败id[%s]", bidItemUserId);

			renderJSON(res);
		}

		/** 提交后将资料保存如科目库 **/
		boolean flag = bidItemUserService.saveUserItemLibrary(bidItemUserId);
		if (!flag) {
			res.code = -1;
			res.msg = "审核资料存入科目库失败!";
			LoggerUtil.error(true, "审核资料存入科目库失败，失败id[%s]", bidItemUserId);

			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";
		renderJSON(res);
	}

	/**
	 * 显示用户科目库
	 *
	 * @param currPage
	 * @param pageSize
	 * @param bidAuditSubjectId
	 * @author Songjia
	 * @createDate 2016年4月5日
	 */
	public static void showUserLibrary(int currPage, int pageSize, long bidAuditSubjectId) {
		if (currPage < 1) {
			currPage = 1;
		}

		if (pageSize < 1) {
			pageSize = 6;
		}

		long userId = getCurrUser().id;
		PageBean<t_audit_item_library> pageBean = auditsubjectbidservice.queryUserLibraryList(currPage, pageSize,
				userId);
		render(pageBean, bidAuditSubjectId);

	}

	/**
	 * 从科目库选择素材上传审核资料
	 *
	 * @param subjectIds
	 * @param bidId
	 * @param bidAuditSubjectId
	 *
	 * @author Songjia
	 * @createDate 2016年4月5日
	 */
	@SimulatedCheck
	public static void uploadLibrarySubjet(List<Long> subjectIds, long bidId, long bidAuditSubjectId) {
		ResultInfo result = new ResultInfo();
		if (subjectIds == null || subjectIds.size() < 1) {
			result.code = -1;
			result.msg = "请选择要上传的素材";

			renderJSON(result);
		}

		result = bidItemUserService.saveBidLibraryItemUser(getCurrUser().id, subjectIds, bidId, bidAuditSubjectId);

		if (result.code < 1) {
			LoggerUtil.error(true, "资料库素材上传失败:【%s】");

			renderJSON(result);
		}

		renderJSON(result);
	}

	/**
	 * 查看借款标的合同
	 *
	 * @param bidSign
	 *
	 * @author DaiZhengmiao
	 * @throws ParseException
	 * @createDate 2016年1月19日
	 */
	public static void showBidPactPre(Long bidId,String release_time,Long userId) throws ParseException {
		t_pact pact = new t_pact();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//正式环境需要把时间改为2018-12-14 18:00:00
		Date date = dateFormat.parse("2018-12-14 18:00:00");
		Date time = TimeUtil.strToDate(release_time);
		if (time.before(date)) {
			pact = pactService.findByBidid(bidId);
		} else {
			pact = pactService.findByCondition(bidId, userId);
		}
		if (pact == null) {

			error500();
		}
		render(pact);
	}

	/**
	 * 查看上上签借款标的合同
	 *
	 * @param bidSign
	 *
	 * @author LiuPengwei
	 * @throws ParseException 
	 * @createDate 2018年3月22日 10:01:36
	 */
	public static void showBidPactSSQPre(long bidId,String time,Long userId) throws ParseException {
		Long uId= getCurrUser().id;
		String previewContract = ElectronicSealCtrl.previewContract(bidId, userId,time,uId);
		TreeMap<String, String> map = new TreeMap<>();
		map.put("url", previewContract);
		renderJSON(map);
	}
	
	/**
	 * 
	 * @Title: showDebtPactSSQPre
	 * 
	 * @description 查看转让协议
	 * @param debtId 债权id
	 * @throws ParseException
	 * @return void    
	 *
	 * @author LiuHangjing
	 * @createDate 2019年1月22日-下午3:07:06
	 */
	public static void showDebtPactSSQPre(long debtId) throws ParseException {
		
		Long uId= getCurrUser().id;
		String previewContract = ElectronicSealCtrl.previewDebtContract(debtId,uId);
		TreeMap<String, String> map = new TreeMap<>();
		map.put("url", previewContract);
		renderJSON(map);
	}
	
	/**
	 * 查看债权的合同
	 *
	 * @param debtSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public static void showDebtPactPre(String debtSign) {
		ResultInfo result = Security.decodeSign(debtSign, Constants.DEBT_TRANSFER_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}
		long debtId = Long.parseLong((String) result.obj);

		t_pact pact = pactService.findByDebtId(debtId);
		if (pact == null) {

			error500();
		}
		render(pact);
	}

	/**
	 * 导出借款标的合同
	 *
	 * @param bidSign
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月19日
	 */
	public static void exportPactPre(String pactSign) {
		ResultInfo result = Security.decodeSign(pactSign, Constants.MSG_PACT_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}

		long pactId = Long.parseLong((String) result.obj);
		result = pactService.exportPact(pactId, true);

		if (result.code < 1) {
			error404();
		}

		renderBinary((File) result.obj);
	}

	/**
	 * 用户删除审核资料
	 *
	 * @param bidItemUserId
	 *
	 * @author yaoyi
	 * @createDate 2016年1月18日
	 */
	@SimulatedCheck
	public static void deleteBidItemUser(long bidItemUserId) {
		ResultInfo res = new ResultInfo();

		boolean flag = bidItemUserService.deleteBidItemUser(bidItemUserId);
		if (!flag) {
			res.code = -1;
			res.msg = "删除失败!";
			LoggerUtil.error(true, "用户删除上传的审核科目资料失败，失败id[%s]", bidItemUserId);

			renderJSON(res);
		}

		res.code = 1;
		res.msg = "";

		renderJSON(res);
	}

	/**
	 * 开启自动投标设置
	 *
	 * @param min_apr
	 *            最低年利率
	 * @param max_period
	 *            最大投资期限
	 * @param invest_amt
	 *            每笔次投资金额
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	@LoginCheck
	@SimulatedCheck
	public static void openAutoInvest(double min_apr, int max_period, double invest_amt, int type, int valid_time) {

		ResultInfo result = new ResultInfo();
		/* 用于显示自动投标页面 */
		flash.put("isAutoInvest", true);

		/* 校验提交参数 */
		result = checkOpenAutoInvestParams(min_apr, max_period, invest_amt);
		if (result.code < 1) {
			flash.error(result.msg);

			accountManagePre(4);
		}

		long userId = getCurrUser().id;

		Date now = new Date();
		// 授权时间
		String valid_day = "";
		// 委托协议签署验密
		if (type == 2) {
			/** 改版前签署成功委托协议 免验密开启自动投标 */
			t_auto_invest_setting autoInvestOption = autoInvestSettingDao.findByColumn("user_id = ?", userId);
			if (autoInvestOption != null && autoInvestOption.status == 0) {
				result = investService.saveAutoInvest(userId, min_apr, max_period, invest_amt, valid_time);
				if (result.code < 1) {
					flash.error(result.msg);
					accountManagePre(4);
				}
				flash.success(result.msg);
				accountManagePre(4);
			}

			if (valid_time <= 0) {

				flash.error("输入正确有效时长");
				accountManagePre(4);
			}

			// 失效时间计算（yyyyMMdd）
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			Calendar ca = Calendar.getInstance();
			ca.setTime(now);
			ca.add(Calendar.DATE, valid_time);
			now = ca.getTime();
			valid_day = sdf.format(now);

			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTRUST_PROTOCOL_AGREE);

			result = PaymentProxy.getInstance().autoCheckPassword(Client.PC.code, businessSeqNo, userId,
					ServiceType.ENTRUST_PROTOCOL_AGREE, min_apr, max_period, invest_amt, valid_day, valid_time);
			if (result.code < 1) {
				flash.error(result.msg);
				accountManagePre(4);
			}

			flash.success(result.msg);
			accountManagePre(4);
		}

		// 委托协议取消的验密
		if (type == 1) {
			// 取消委托协议时间（yyyyMMdd）
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			valid_day = sdf.format(now);
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.ENTRUST_PROTOCOL_CANCEL);

			result = PaymentProxy.getInstance().autoCheckPassword(Client.PC.code, businessSeqNo, userId,
					ServiceType.ENTRUST_PROTOCOL_CANCEL, min_apr, max_period, invest_amt, valid_day, 0);
			if (result.code < 1) {
				flash.error(result.msg);
				accountManagePre(4);
			}

			flash.success(result.msg);
			accountManagePre(4);
		}

		flash.error("操作失败！");
		accountManagePre(4);

	}

	/**
	 * 校验开启自动投标提交参数
	 *
	 * @param minApr
	 *            最低年利率
	 * @param maxPeriod
	 *            最大投资期限
	 * @param investAmt
	 *            每笔次投资金额
	 *
	 * @author ZhouYuanZeng
	 * @createDate 2016年03月24日
	 */
	private static ResultInfo checkOpenAutoInvestParams(double minApr, int maxPeriod, double investAmt) {
		ResultInfo result = new ResultInfo();

		if (investAmt % 1 != 0) {
			result.code = -1;
			result.msg = "投资每笔次请输入正整数";
		}

		if (investAmt < 1) {
			result.code = -1;
			result.msg = "投资每笔次不能小于1元";

			return result;
		}

		if (investAmt > 200000) {
			result.code = -1;
			result.msg = "投资每笔次不能大于200000元";

			return result;
		}

		if (maxPeriod < 1) {
			result.code = -1;
			result.msg = "投资期限不能小于1个月";

			return result;
		}

		if (maxPeriod > 36) {
			result.code = -1;
			result.msg = "投资期限不能大于36个月";

			return result;
		}

		result.code = 1;
		result.msg = "";
		return result;
	}

	/**
	 * 还款校验交易密码
	 * 
	 * @author niu
	 * @create 2017.09.23
	 */
	@LoginCheck
	@SimulatedCheck
	public static void repaymentCheckPass(String billSign) {

		ResultInfo result = new ResultInfo();

		// 投标请求参数
		Map<String, String> investParams = new HashMap<String, String>();
		investParams.put("billSign", billSign);

		// 验密请求参数
		CurrUser currUser = getCurrUser();
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(currUser.id);
		String userName = "";
		if (userInfo != null && userInfo.reality_name != null) {
			userName = userInfo.reality_name;
		}

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");

			error404();
		}

		long billId = Long.parseLong(result.obj.toString());

		t_bill bill = billService.findByID(billId);
		if (bill == null) {

			error404();
		}
		// 服务费
		t_bid bid = bidService.findByColumn("id = ?", bill.bid_id);
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// 还款总金额
		double amount = bill.repayment_corpus + bill.repayment_interest + serviceAmount;

		// 验密
		if (bill.getStatus().code == 0) {
			PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, currUser.id,
					BaseController.getBaseURL() + "check/yibincallback/repaymentCB", userName, ServiceType.REPAYMENT,
					amount, investParams, ServiceType.REPAYMENT_CHECK_TRADE_PASS, 2, "", "", "");
		} else {
			PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, currUser.id,
					BaseController.getBaseURL() + "check/yibincallback/repaymentDC", userName,
					ServiceType.COMPENSATORY_PAYMENT, amount, investParams, ServiceType.COMPENSATORY_PAYMENT, 2, "", "",
					"");
		}

	}

	/**
	 * 提前还款校验交易密码
	 * 
	 * @author liu
	 * @create 2018.01.10
	 */
	@LoginCheck
	@SimulatedCheck
	public static void repaymentCheckPasses(String billSign) {

		ResultInfo result = new ResultInfo();

		// 投标请求参数
		Map<String, String> investParams = new HashMap<String, String>();
		investParams.put("billSign", billSign);

		// 验密请求参数
		CurrUser currUser = getCurrUser();
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.REPAYMENT);
		t_user_info userInfo = userInfoService.findUserInfoByUserId(currUser.id);
		String userName = "";
		if (userInfo != null && userInfo.reality_name != null) {
			userName = userInfo.reality_name;
		}

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");

			error404();
		}

		long billId = Long.parseLong(result.obj.toString());

		t_bill bill = billService.findByID(billId);
		if (bill == null) {

			error404();
		}

		List<t_bill_invest> billInvestList = billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		double surplusCorpus = 0; // 所有待还本金
		for (t_bill_invest billIn : billInvestList) {
			surplusCorpus += billIn.receive_corpus;

		}
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		double surplusFine = 0; // 所有罚息（包括当月，因为当月是0所以可以不用去除）
		double surplusInterest = 0; // 所有利息（包括当月跟逾期利息）
		if (billInvestOverdueList != null && billInvestOverdueList.size() > 0) {
			for (t_bill_invest interest : billInvestOverdueList) {
				surplusFine += interest.overdue_fine;
				surplusInterest += interest.receive_interest;
			}
		}

		// 服务费
		t_bid bid = bidService.findByID(bill.bid_id);
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// 垫付总额
		double amount = surplusCorpus + surplusInterest + surplusFine + serviceAmount; // 本金合计+利息合计+罚息合计
																						// +
																						// 平台服务费

		// 验密
		PaymentProxy.getInstance().checkTradePass(Client.PC.code, businessSeqNo, currUser.id,
				BaseController.getBaseURL() + "check/yibincallback/repaymentTY", userName, ServiceType.REPAYMENT,
				amount, investParams, ServiceType.REPAYMENT, 2, "", "", "");

	}

	/**
	 * 代偿回款（借方方台账账户 -> 代偿担保方账户）
	 * 
	 * @author liuyang
	 * @create 2018.02.01
	 */
	@SimulatedCheck
	public static void repaymentCompensate() {

		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.COMPENSATORY_PAYMENT);

		if (result.code < 0) {
			flash.error(result.msg);

			accountManagePre(1);
		}

		Map<String, String> reqParams = result.msgs;
		String businessSeqNo = result.msg;

		String billSign = reqParams.get("billSign");

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");

			error404();
		}

		long billId = Long.parseLong(result.obj.toString());

		t_bill bill = billService.findByID(billId);
		if (bill == null) {

			error404();
		}

		long userId = getCurrUser().id;

		Map<String, Object> accMap = null;
		List<Map<String, Object>> accountList = null;
		Map<String, Object> conMap = null;
		List<Map<String, Object>> contractList = null;

		// 本金垫付还款前准备
		result = billService.advanceRepayment(userId, bill);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			accountManagePre(1);
		}

		t_bid bid = bidService.findByID(bill.bid_id);

		accMap = new HashMap<String, Object>();
		accMap.put("oderNo", "0");
		accMap.put("oldbusinessSeqNo", "");
		accMap.put("oldOderNo", "");
		accMap.put("debitAccountNo", bid.user_id);
		accMap.put("cebitAccountNo", bill.guarantee_user_id);// 代偿担保方账户
		accMap.put("currency", "CNY");
		accMap.put("amount", YbUtils
				.formatAmount(Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine)));///////////////
		accMap.put("summaryCode", "T09");

		accountList = new ArrayList<Map<String, Object>>();
		accountList.add(accMap);

		conMap = new HashMap<String, Object>();
		conMap.put("oderNo", "1");
		conMap.put("contractType", "");
		conMap.put("contractRole", "");
		conMap.put("contractFileNm", "");
		conMap.put("debitUserid", "");
		conMap.put("cebitUserid", "");

		contractList = new ArrayList<Map<String, Object>>();
		contractList.add(conMap);

		// 服务费
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		accountList.add(YbUtils.getAccountMap("2", "", "", bid.user_id + "", YbConsts.COSTNO,
				YbUtils.formatAmount(serviceAmount), "T12"));

		// 资金托管 - 代偿回款
		if (ConfConst.IS_TRUST) {

			result = PaymentProxy.getInstance().offlineReceive(OrderNoUtil.getClientSn(), businessSeqNo, bid,
					ServiceType.COMPENSATORY_PAYMENT, EntrustFlag.UNENTRUST, accountList, contractList);
			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);

				flash.error(result.msg);
			} else {

				result = billService.doRepaymentOneCompensate(businessSeqNo, bill, bill.overdue_fine, bid);
				if (result.code < 0) {

					flash.error(result.msg);
				} else {
					Logger.info("融资人台账  -> 代偿担保方账户  -> 代偿回款成功");

					flash.success("代偿回款成功,请核对账单");

					// 改变担保方的代偿待还金额
					t_guarantee gua = guaranteeService.findByColumn("user_id=?", bill.guarantee_user_id);
					gua.compensate_amount = gua.compensate_amount
							- (Arith.add(Arith.add(bill.repayment_corpus, bill.repayment_interest), bill.overdue_fine));
					gua.save();

					// 正常还款时改变贷款业务信息表中的数据
					t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
					t_bill bills = billService.findByID(billId);
					if (loans != null) {
						loans.last_repayment_date = new Date();
						Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
						loans.actual_amount = bills.repayment_corpus + zhis;
						loans.time = new Date();
						Integer zhi = Integer.parseInt(loans.resudue_months);
						loans.resudue_months = (zhi - 1) + "";
						loans.save();
					}

					// 查询费用账户可用余额
					String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);

					ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
					if (result3.code < 1) {

						flash.error("查询费用账户可用余额异常");
						accountManagePre(1);
					}

					Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
					double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);

					// 保存费用账户明细
					t_cost cost = new t_cost();
					cost.balance = cosBalance;
					cost.amount = serviceAmount;
					cost.type = 0;
					cost.sort = 3;
					cost.bid_id = bid.id;
					cost.time = new Date();
					cost.save();
				}
			}
		}

		accountManagePre(1);
	}

	/**
	 * 还款（融资方台账账户 -> 标的台账账户）
	 * 
	 * @author niu
	 * @create 2017.09.23
	 */
	@SimulatedCheck
	public static void repaymentMoney() {

		ResultInfo result = callBackService.checkTradePassCB(params, ServiceType.REPAYMENT);

		if (result.code < 0) {
			flash.error(result.msg);

			accountManagePre(1);
		}

		Map<String, String> reqParams = result.msgs;
		String businessSeqNo = result.msg;

		String billSign = reqParams.get("billSign");

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名校验失败");

			error404();
		}

		long billId = Long.parseLong(result.obj.toString());

		t_bill bill = billService.findByID(billId);
		if (bill == null) {

			error404();
		}

		long userId = getCurrUser().id;

		List<Map<String, Object>> accountList = null;

		// 还款前准备
		result = billService.normalRepayment(userId, bill);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			accountManagePre(1);
		}

		t_bid bid = bidService.findByID(bill.bid_id);
		double serviceAmount = bid.amount * bid.service_charge / 100 / 12;

		BigDecimal bg = new BigDecimal(serviceAmount);
		serviceAmount = bg.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		accountList = new ArrayList<Map<String, Object>>();
		accountList.add(YbUtils.getAccountMap("0", "", "", bid.user_id + "", bid.object_acc_no,
				YbUtils.formatAmount(bill.repayment_corpus), "T04"));
		accountList.add(YbUtils.getAccountMap("1", "", "", bid.user_id + "", bid.object_acc_no,
				YbUtils.formatAmount(Arith.add(bill.repayment_interest, bill.overdue_fine)), "T12"));
		accountList.add(YbUtils.getAccountMap("2", "", "", bid.user_id + "", YbConsts.COSTNO,
				YbUtils.formatAmount(serviceAmount), "T12"));

		if (accountList.size() < 2) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			accountManagePre(1);
		}

		// 资金托管 - 还款
		if (ConfConst.IS_TRUST) {

			result = PaymentProxy.getInstance().fundTrade(OrderNoUtil.getClientSn(), userId, businessSeqNo, bid,
					ServiceType.REPAYMENT, EntrustFlag.UNENTRUST, accountList, YbUtils.getContractList());
			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);

				flash.error(result.msg);
			} else {

				result = billService.doRepaymentOne(businessSeqNo, bill, bill.overdue_fine, bid);
				if (result.code < 0) {

					flash.error(result.msg);
				} else {
					Logger.info("融资人台账  -> 标的台账  -> 还款成功");

					flash.success("还款成功,请核对账单");

					// 正常还款时改变贷款业务信息表中的数据
					t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
					t_bill bills = billService.findByID(billId);
					if (loans != null) {
						loans.last_repayment_date = new Date();
						Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
						loans.actual_amount = bills.repayment_corpus + zhis;
						loans.time = new Date();
						Integer zhi = Integer.parseInt(loans.resudue_months);
						loans.resudue_months = (zhi - 1) + "";
						loans.save();
					}

					// 查询费用账户可用余额
					String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);

					ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
					if (result3.code < 1) {

						flash.error("查询费用账户可用余额异常");
						accountManagePre(1);
					}

					Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
					double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);

					// 拼接费用明细的备注
					StringBuffer remark = new StringBuffer();
					remark.append(bid.getBid_no());

					t_user_info userInfo = userInfoService.findUserInfoByUserId(bid.user_id);
					if (userInfo != null) {
						remark.append(userInfo.reality_name);
					}

					remark.append(bid.title).append(bid.period).append("个月");

					// 保存费用账户明细
					t_cost cost = new t_cost();
					cost.balance = cosBalance;
					cost.amount = serviceAmount;
					cost.type = 0;
					cost.sort = 3;
					cost.bid_id = bid.id;
					cost.remark = remark.toString();
					cost.time = new Date();
					cost.save();
				}
			}
		}

		accountManagePre(1);
	}

	/**
	 * 委托协议签署，取消
	 * 
	 * @param businessSeqNo
	 * @param userId
	 * @param serviceType
	 * @param min_apr
	 * @param max_period
	 * @param invest_amt
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年1月05日
	 *
	 */
	public static void openEntrustPre(String businessSeqNo, long userId, ServiceType serviceType, double min_apr,
			int max_period, double invest_amt, String valid_time, int valid_day) {

		ResultInfo result = PaymentProxy.getInstance().autoInvestSignature(Client.PC.code, businessSeqNo, userId,
				serviceType, invest_amt, valid_time);

		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
			accountManagePre(4);
		}

		// 委托协议号
		String protocolNo = (String) result.obj;

		/** 委托协议业务处理 */
		switch (serviceType.code) {
		// 委托协议签署
		case 8:

			result = investService.newAutoInvest(userId, min_apr, max_period, invest_amt, valid_day, protocolNo);
			if (result.code < 1) {
				flash.error(result.msg);
				accountManagePre(4);
			}
			break;
		// 委托协议取消
		case 9:

			result = investService.closeAutoInvest(userId);
			if (result.code < 1) {
				flash.error(result.msg);
				accountManagePre(4);
			}

			break;
		default:
			flash.error("业务类型匹配失败！");
			accountManagePre(4);
			break;
		}

		flash.success(result.msg);
		accountManagePre(4);

	}

	/**
	 * 
	 * @Title: loanContract
	 * @description 用户借款合同下拉
	 * @param bidId
	 *            void
	 * @author likai
	 * @throws ParseException
	 * @createDate 2018年11月20日 下午1:41:37
	 */
	public static void pullDownMyLoanContractPre(Long bidId, String time) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//正式环境需要把时间改为2018-12-14 18:00:00
		Date date = dateFormat.parse("2018-12-14 18:00:00");
		Date release_time = TimeUtil.strToDate(time);
		if (release_time.before(date)) {
			List<LoanContract> loanContract = pactService.findLoanContract(bidId);
			render(loanContract);
		}else {
			List<LoanContract> loanContract = pactService.findListLoanContract(bidId);
			render(loanContract);
		}
	}

}
