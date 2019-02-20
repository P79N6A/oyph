package controllers.back.finance;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import models.common.entity.t_loan_apply;
import models.common.entity.t_loan_contract;
import models.common.entity.t_loan_profession;
import models.common.entity.t_pact;
import models.common.entity.t_risk_reception;
import models.common.entity.t_risk_report;
import models.common.entity.t_user;
import models.common.entity.t_user_info;
import models.core.bean.BackFinanceBid;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_invest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import services.common.LoanApplyService;
import services.common.LoanContractService;
import services.common.LoanProfessionService;
import services.common.PactService;
import services.common.RiskReceptionService;
import services.common.RiskReportService;
import services.common.UserInfoService;
import services.common.UserService;
import services.core.BidService;
import services.core.BillService;
import services.core.InvestService;
import yb.enums.ServiceType;

import com.shove.Convert;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.Client;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.OrderNoUtil;
import common.utils.PageBean;
import common.utils.ResultInfo;
import common.utils.Security;
import common.utils.excel.ExcelUtils;
import common.utils.jsonAxml.JsonDateValueProcessor;
import common.utils.jsonAxml.JsonDoubleValueProcessor;
import controllers.common.BackBaseController;
import controllers.front.seal.ElectronicSealCtrl;

/**
 * 后台-财务-财务放款-控制器
 *
 * @description
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class ReleaseMngCtrl extends BackBaseController {

	protected static BidService bidservice = Factory.getService(BidService.class);

	protected static LoanApplyService loanApplyService = Factory.getService(LoanApplyService.class);

	protected static LoanContractService loanContractService = Factory.getService(LoanContractService.class);

	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);

	protected static RiskReceptionService riskReceptionService = Factory.getService(RiskReceptionService.class);

	protected static RiskReportService riskReportService = Factory.getService(RiskReportService.class);

	protected static BillService billService = Factory.getService(BillService.class);

	protected static UserService userService = Factory.getService(UserService.class);

	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);

	protected static PactService pactService = Factory.getService(PactService.class);

	protected static InvestService investservice = Factory.getService(InvestService.class);

	/**
	 * 后台-财务-财务放款-财务放款列表
	 * 
	 * @rightID 501001
	 *
	 * @param showType
	 *            筛选类型 0-所有;1-待放款;2-已放款(还款中,已还款)
	 * @param currPage
	 *            当前页
	 * @param pageSize
	 *            每页条数
	 * @param exports
	 *            1：导出 default:不导出
	 * @param loanName
	 *            借款人昵称
	 * @param orderType
	 *            排序栏目 0：编号 2：借款金额 7：放款时间
	 * @param orderValue
	 *            排序规则 0,降序，1,升序
	 *
	 * @author yaoyi
	 * @createDate 2015年12月23日
	 */
	public static void showReleaseListPre(int showType, int currPage, int pageSize) {
		int exports = Convert.strToInt(params.get("exports"), 0);
		String loanName = params.get("loanName");

		if (showType < 0 || showType > 2) {
			showType = 0;
		}

		// 排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0：编号 2：借款金额 7：放款时间
		if (orderType != 0 && orderType != 2 && orderType != 7) {
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);

		// 排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);// 0,降序，1,升序
		if (orderValue < 0 || orderValue > 1) {
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);

		PageBean<BackFinanceBid> pageBean = bidservice.pageOfBidFinance(showType, currPage, pageSize, exports, loanName,
				orderType, orderValue);

		// 导出
		if (exports == Constants.EXPORT) {
			List<BackFinanceBid> list = pageBean.page;

			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class,
					new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);

			for (Object obj : arrList) {
				JSONObject bid = (JSONObject) obj;

				if (StringUtils.isNotBlank(bid.getString("status"))) {
					bid.put("status", t_bid.Status.valueOf(bid.get("status").toString()).value);
				}
			}

			String fileName = null;
			switch (showType) {
			case 1:
				fileName = "待放款财务放款列表";
				break;
			case 2:
				fileName = "已放款财务放款列表";
				break;
			default:
				fileName = "财务放款列表";
				break;
			}

			File file = ExcelUtils.export(fileName, arrList,
					new String[] { "编号", "项目", "项目金额", "借款人", "初审管理员", "满标审管理员", "放款", "状态" },
					new String[] { "bid_no", "title", "amount", "name", "pre_audit_supervisor", "audit_supervisor",
							"release_supervisor", "status" });

			renderBinary(file, fileName + ".xls");
		}

		// 统计总额
		double totalAmt = bidservice.findTotalBidAmountFinance(showType, loanName);

		render(pageBean, totalAmt, showType, loanName);
	}

	/**
	 * 财务放款
	 * 
	 * @rightID 501002
	 *
	 * @param bidSign
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月21日
	 */
	public static void release(String bidSign) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}

		long bidId = Long.parseLong(result.obj.toString());

		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("借款标不存在");

			showReleaseListPre(0, 1, 10);
		}

		long supervisorId = getCurrentSupervisorId();

		result = bidservice.release(bid, supervisorId);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);

			showReleaseListPre(0, 1, 10);
		}

		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.LOAN);

		if (ConfConst.IS_TRUST) {
			result = PaymentProxy.getInstance().bidSuditSucc(Client.PC.code, businessSeqNo, supervisorId, bid);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				flash.error(result.msg);
			} else {
				flash.success("放款成功,已将项目置为[还款中]!");
				// 贷款申请通过
				t_loan_apply loan = loanApplyService.findByColumn("bid_id = ?", bid.id);
				if (loan != null) {
					loan.status = 2;
					loan.save();
				}

				// 贷款合同
				t_loan_contract contract = new t_loan_contract();
				contract.begin_time = new Date();
				contract.mer_bid_no = bid.mer_bid_no;
				contract.contract_amount = bid.amount;
				contract.currency_type = "CNY";
				contract.status = 0;
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(contract.begin_time);
				if (bid.period_units == 1) {
					rightNow.add(Calendar.DAY_OF_YEAR, bid.period);// 日期加天
					Date dt1 = rightNow.getTime();
					contract.end_time = dt1;
				} else if (bid.period_units == 2) {
					rightNow.add(Calendar.MONTH, bid.period + 1);// 日期加月
					Date dt2 = rightNow.getTime();
					contract.end_time = dt2;
				}
				contract.save();

				// 贷款业务
				t_loan_profession loans = new t_loan_profession();
				t_risk_report risks = riskReportService.findByID(bid.risk_id);
				t_bill bill = billService.findByColumn("bid_id=?", bid.id);
				t_user_info userInfo = userInfoService.findByColumn("user_id=?", bid.user_id);
				t_loan_apply loanApply = loanApplyService.findByColumn("bid_id=?", bid.id);
				if (userInfo != null && loanApply != null && risks != null && bill != null) {
					loans.account_time = contract.begin_time;
					loans.expire_time = contract.end_time;
					loans.currency = "CNY";
					loans.credit_line = bid.amount;
					loans.share_credit_line = bid.amount;
					loans.max_liskills = bid.amount;
					if (risks.guaranty_kind.equals("质押")) {
						loans.guaranty_style = 1;
					} else {
						loans.guaranty_style = 2;
					}
					loans.repayment_frequency = 03;
					loans.repayment_months = bid.period + "";
					loans.resudue_months = bid.period + "";
					loans.agreement_repayment_period = bid.period + "";
					loans.agreement_repayment_amount = bill.repayment_interest + "";
					loans.repayment_date = bill.repayment_time;
					loans.last_repayment_date = contract.begin_time;
					loans.current_amount = bill.repayment_corpus + bill.repayment_interest;
					loans.actual_amount = bill.repayment_corpus + bill.repayment_interest;
					loans.balance = bid.amount;
					loans.account_status = 1;
					loans.repayment_status = "N";

					if (userInfo.is_account == 1) {
						loans.owner_message_hint = 2;
					} else {
						loans.owner_message_hint = 1;
					}

					loans.bid_id = bid.id;
					loans.user_id = bid.user_id;
					loans.loan_type = loanApply.getType().code;
					loans.site = loanApply.site;
					loans.mer_bid_no = bid.mer_bid_no;
					loans.service_order_no = bid.service_order_no;
					loans.time = new Date();
					loans.save();
				}
			}

			showReleaseListPre(0, 1, 10);
		}

		result = bidservice.doRelease(bidId, supervisorId, businessSeqNo);
		if (result.code < 1) {
			LoggerUtil.info(true, result.msg);
			flash.error(result.msg);
		} else {
			flash.success("放款成功,已将项目置为[还款中]!");
			// 贷款申请通过
			t_loan_apply loan = loanApplyService.findByColumn("bid_id = ?", bid.id);
			if (loan != null) {
				loan.status = 2;
				loan.save();
			}

			// 贷款合同
			t_loan_contract contract = new t_loan_contract();
			contract.begin_time = new Date();
			contract.mer_bid_no = bid.mer_bid_no;
			contract.contract_amount = bid.amount;
			contract.currency_type = "CNY";
			contract.status = 0;
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(contract.begin_time);
			if (bid.period_units == 1) {
				rightNow.add(Calendar.DAY_OF_YEAR, bid.period);// 日期加天
				Date dt1 = rightNow.getTime();
				contract.end_time = dt1;
			} else if (bid.period_units == 2) {
				rightNow.add(Calendar.MONTH, bid.period);// 日期加月
				Date dt2 = rightNow.getTime();
				contract.end_time = dt2;
			}
			contract.save();

			// 贷款业务
			t_loan_profession loans = new t_loan_profession();
			t_risk_report risks = riskReportService.findByID(bid.risk_id);
			t_bill bill = billService.findByColumn("bid_id=?", bid.id);
			t_user_info userInfo = userInfoService.findByColumn("user_id=?", bid.user_id);
			t_loan_apply loanApply = loanApplyService.findByColumn("bid_id=?", bid.id);
			loans.account_time = contract.begin_time;
			loans.expire_time = contract.end_time;
			loans.currency = "CNY";
			loans.credit_line = bid.amount;
			loans.share_credit_line = bid.amount;
			loans.max_liskills = bid.amount;
			if (risks.guaranty_kind.equals("质押")) {
				loans.guaranty_style = 1;
			} else {
				loans.guaranty_style = 2;
			}
			loans.repayment_frequency = 03;
			loans.repayment_months = bid.period + "";
			loans.resudue_months = bid.period + "";
			loans.agreement_repayment_period = bid.period + "";
			loans.agreement_repayment_amount = bill.repayment_interest + "";
			loans.repayment_date = bill.repayment_time;
			loans.last_repayment_date = contract.begin_time;
			loans.current_amount = bill.repayment_corpus + bill.repayment_interest;
			loans.actual_amount = bill.repayment_corpus + bill.repayment_interest;
			loans.balance = bid.amount;
			loans.account_status = 1;
			loans.repayment_status = "N";

			if (userInfo.is_account == 1) {
				loans.owner_message_hint = 2;
			} else {
				loans.owner_message_hint = 1;
			}

			loans.bid_id = bid.id;
			loans.user_id = bid.user_id;
			loans.loan_type = loanApply.getType().code;
			loans.site = loanApply.site;
			loans.mer_bid_no = bid.mer_bid_no;
			loans.service_order_no = bid.service_order_no;
			loans.time = new Date();
			loans.save();
		}

		showReleaseListPre(0, 1, 10);
	}

	/**
	 * 上上签合同创建和签署
	 * 
	 * @param bidId
	 *            标的Id
	 * 
	 * @author LiuPengwei
	 * @createDate 2018年3月27日10:40:54
	 */

	public static void ssqContract(String bidSign) {
		ResultInfo result = Security.decodeSign(bidSign, Constants.BID_ID_SIGN, Constants.VALID_TIME,
				ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {

			error404();
		}

		long bidId = Long.parseLong(result.obj.toString());

		t_bid bid = bidservice.findByID(bidId);
		if (bid == null) {
			flash.error("借款标不存在");

			showReleaseListPre(0, 1, 10);
		}

		List<t_invest> invests = investservice.queryBidInvest(bidId);// 获得用户对这个标所投的金额

		if (invests == null || invests.size() == 0) {
			flash.error("获得用户对标所投的金额有误!");

			showReleaseListPre(0, 1, 10);
		}

		/** 生成合同 PDF文件 */
		// t_pact pact = pactService.findByBidid(bidId);
		// 查询一个标的所有合同并循环遍历生成上上签合同
		List<t_pact> pacts = pactService.findListByColumn("pid=?", bidId);
		for (t_pact pact : pacts) {
			result = pactService.exportPact(pact.id, true);
			if (result.code < 1) {
				flash.error("生成合同失败!");
				showReleaseListPre(0, 1, 10);
			}
			File file = (File) result.obj;
			/** 上上签创建合同 和签署合同 */
			result = ElectronicSealCtrl.createContract(bidId, file, pact);
			if (result.code < 1) {
				flash.error(result.msg);
				showReleaseListPre(0, 1, 10);
			}
		}
		bidservice.addBidContract(bidId,"新合同编号存入合同表");
		showReleaseListPre(0, 1, 10);
	}

}
