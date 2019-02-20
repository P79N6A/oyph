package controllers.back.finance;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.common.entity.t_deal_platform.DealType;
import models.common.entity.t_deal_user;
import models.common.entity.t_deal_user.OperationType;
import models.common.entity.t_guarantee;
import models.common.entity.t_loan_profession;
import models.common.entity.t_user_fund;
import models.common.entity.t_user_info;
import models.core.bean.Bill;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.core.entity.t_bill_invest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;

import payment.impl.PaymentProxy;
import play.Logger;
import services.common.DealUserService;
import services.common.GuaranteeService;
import services.common.LoanProfessionService;
import services.common.UserFundService;
import services.common.UserInfoService;
import services.core.BidService;
import services.core.BillInvestService;
import services.core.BillService;
import yb.YbPaymentCallBackService;
import yb.YbUtils;
import yb.enums.EntrustFlag;
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
import common.utils.number.Arith;
import controllers.common.BackBaseController;
import controllers.common.BaseController;

/**
 * 后台-财务-借款账单-控制器
 *
 * @description 
 *
 * @author huangyunsong
 * @createDate 2015年12月19日
 */
public class BillMngCtrl extends BackBaseController{
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static BillInvestService billInvestService = Factory.getService(BillInvestService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);
	
	protected static GuaranteeService guaranteeService = Factory.getService(GuaranteeService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static UserInfoService userInfoService = Factory.getService(UserInfoService.class);
	
	private static YbPaymentCallBackService callBackService = Factory.getSimpleService(YbPaymentCallBackService.class);
	
	protected static DealUserService dealUserService = Factory.getService(DealUserService.class);
	
	/**
	 * 后台-财务-借款账单-借款账单列表
	 * @rightID 502001
	 *
	 * @param currPage 当前页
	 * @param pageSize 每页条数
	 * @param showType 查询状态  default:所有    1:待还(正常待还+逾期待还+本息垫付待还) 2:逾期待还(待还+逾期) 3:已还(正常还款、本息垫付还款 、线下收款、本息垫付后线下收款 )
	 * @param exports 1：导出  default：不导出
	 * @param loanName 借款人昵称
	 * @param orderType 排序栏目  0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
	 * @param orderValue 排序规则 0,降序，1,升序
	 * @param projectName 项目
	 *
	 * @author liudong
	 * @createDate 2015年12月18日
	 */
	public static void showLoanBillsPre(int showType, int currPage,int pageSize){
		int exports = Convert.strToInt(params.get("exports"), 0);
		String loanName = params.get("loanName");
		String projectName = params.get("projectName");
		
		//排序栏目
		String orderTypeStr = params.get("orderType");
		int orderType = Convert.strToInt(orderTypeStr, 0);// 0：编号   3：账单金额    5：逾期时长   6：到期时间    7：还款时间
		if(orderType != 0 && orderType  != 3 && orderType  != 5 && orderType  != 6 && orderType  != 7){
			orderType = 0;
		}
		renderArgs.put("orderType", orderType);
				
		//排序规则
		String orderValueStr = params.get("orderValue");
		int orderValue = Convert.strToInt(orderValueStr, 0);//0,降序，1,升序
		if(orderValue<0 || orderValue>1){
			orderValue = 0;
		}
		renderArgs.put("orderValue", orderValue);
		
		/** 查询账单 */
		PageBean<Bill> page = billService.pageOfBillBack(showType,currPage, pageSize, exports, loanName, orderType, orderValue, projectName);
		if(page!=null && page.page!=null && page.page.size()>0){
			for(Bill p:page.page){
				t_bid bid=bidService.findByID(p.bid_id);
				List<t_bill> billList=billService.findBillByBidId(bid.id);
				if(billList!=null && billList.size()==1){
					p.adFlag=false;
				}else{
					p.adFlag=true;
				}
			}
		}
		
		//导出
		if(exports == Constants.EXPORT){
			List<Bill> list = page.page;
			 
			JsonConfig jsonConfig = new JsonConfig();
			jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor(Constants.DATE_FORMATE));
			jsonConfig.registerJsonValueProcessor(Double.class, new JsonDoubleValueProcessor(Constants.FINANCE_FORMATE_NORMAL));
			JSONArray arrList = JSONArray.fromObject(list, jsonConfig);
			
			for (Object obj : arrList) {
				JSONObject bill = (JSONObject)obj;
				
				bill.put("period_no", bill.get("period")+"|"+bill.get("totalPeriod"));
				
				if(StringUtils.isNotBlank(bill.getString("status"))){
					bill.put("status", t_bill.Status.valueOf(bill.get("status").toString()).value);
				}
			}
			
			String fileName="借款账单列表";
			switch (showType) {
				case 1:
					fileName = "待还借款账单列表";
					break;
				case 2:
					fileName = "逾期待还借款账单列表";
					break;
				case 3:
					fileName = "已还借款账单列表";
					break;
				default:
					fileName = "借款账单列表";
					break;
			}
			
			File file = ExcelUtils.export(fileName,
			arrList,
			new String[] {
			"编号", "项目标号", "项目", "借款人", "账单金额", "期数", "逾期时长", "到期时间", "还款时间", "状态"},
			new String[] {
			"bill_no","bid_no","title", "name", "corpus_interest", "period_no", "overdue_days", "repayment_time", "real_repayment_time", "status"});
			   
			renderBinary(file, fileName + ".xls");
		}
		
		/** 查询账单总额 */
		double totalAmt = billService.findTotalBillAmount(showType,loanName,projectName);
		
		render(page,totalAmt,showType,loanName, projectName);
	}
	
	/**
	 * 担保人代偿验密
	 * @rightID 502003
	 *
	 * @param billSign 借款账单签名
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月30日
	 */
	public static void guaranteeCheckPassword(String billSign, long userId, int flags) {

		if(userId == 0) {
			flash.error("请选择担保人");
			showLoanBillsPre(0,1,10);
		}
		
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if(userFund.payment_account == null || userFund.payment_account.equals("")) {
			flash.error("该担保人还未开户");
			showLoanBillsPre(0,1,10);
		}
		
		t_user_info userInfo = userInfoService.findUserInfoByUserId(userId);
		
		// 托管 查询代偿账户可用余额
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		result = PaymentProxy.getInstance().queryGuaranteeBalance(Client.PC.code, businessSeqNo, Long.parseLong(userFund.payment_account));
		if (result.code < 1) {
			flash.error("查询商户可用余额异常");
			showLoanBillsPre(0,1,10);
		}
		
		Map<String, Object> merDetail = (Map<String, Object>) result.obj;
		double merBalance = Convert.strToDouble(merDetail.get("pBlance").toString(), 0);

		//垫付总额
		double advanceAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
		
		if (advanceAmt > merBalance) {
			flash.error("商户余额不足，请充值");
			
			showLoanBillsPre(0,1,10);
		}
		
		PaymentProxy.getInstance().guaranteeCheckPassword(Client.PC.code, businessSeqNo, userId, userInfo.reality_name, advanceAmt, BaseController.getBaseURL() + "check/yibincallbacks/repaymentGU", billSign);
		
	}
	
	/**
	 * 本息垫付
	 * @rightID 502003
	 *
	 * @param billSign 借款账单签名
	 *
	 * @author huangyunsong
	 * @createDate 2015年12月30日
	 */
	public static void principalAdvance() {
		
		ResultInfo result = callBackService.checkGuaranteeGU(params, ServiceType.COMPENSATORY);
		
		if (result.code < 0) {
			flash.error(result.msg);
			
			showLoanBillsPre(0,1,10);
		}
		
		String businessSeqNo = result.msg;
		
		//备注参数调用
		Map<String, String> remarkParams = callBackService.queryRequestParamss(businessSeqNo);
		
		String billSign = remarkParams.get("t_bill_sign");
		String userIds = remarkParams.get("r_user_id");
		long userId = Long.parseLong(userIds);
		
		if(userId == 0) {
			flash.error("请选择担保人");
			showLoanBillsPre(0,1,10);
		}
		
		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if(userFund.payment_account == null || userFund.payment_account.equals("")) {
			flash.error("该担保人还未开户");
			showLoanBillsPre(0,1,10);
		}
		
		// 托管 查询代偿账户可用余额
		
		result = PaymentProxy.getInstance().queryGuaranteeBalance(Client.PC.code, businessSeqNo, Long.parseLong(userFund.payment_account));
		if (result.code < 1) {
			flash.error("查询商户可用余额异常");
			showLoanBillsPre(0,1,10);
		}
		
		Map<String, Object> merDetail = (Map<String, Object>) result.obj;
		double merBalance = Convert.strToDouble(merDetail.get("pBlance").toString(), 0);

		//垫付总额
		double advanceAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
		
		if (advanceAmt > merBalance) {
			flash.error("商户余额不足，请充值");
			
			showLoanBillsPre(0,1,10);
		}
		
		//long supervisorId = getCurrentSupervisorId();
		
		/** 本息垫付 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			result = billService.principalAdvance(bill, userId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				return;
			}
			
			t_bid bid = bidService.findByID(bill.bid_id);
			
			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", "0");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", userFund.payment_account);//担保人账户
			accMap.put("cebitAccountNo", bid.object_acc_no);
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(bill.repayment_corpus));
			accMap.put("summaryCode", "T08");
			
			Map<String, Object> intMap = new HashMap<String, Object>();
			intMap.put("oderNo", "1");
			intMap.put("oldbusinessSeqNo", "");
			intMap.put("oldOderNo", "");
			intMap.put("debitAccountNo", userFund.payment_account);//担保人账户
			intMap.put("cebitAccountNo", bid.object_acc_no);
			intMap.put("currency", "CNY");
			intMap.put("amount", YbUtils.formatAmount(Arith.add(bill.overdue_fine, bill.repayment_interest)));
			intMap.put("summaryCode", "T12");
			
			List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
			accountList.add(accMap);
			accountList.add(intMap);
			
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
			
			result = PaymentProxy.getInstance().offlineReceive(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.COMPENSATORY, EntrustFlag.UNENTRUST, accountList, contractList);
			if (result.code < 0) {
				LoggerUtil.info(true, result.msg);
				
				flash.error(result.msg);
			} else {
				
				result = billService.doPrincipalAdvance(businessSeqNo, bill, bill.overdue_fine, bid, userId);
				if (result.code < 0) {
					
					flash.error(result.msg);
				} else {
					Logger.info("担保人台账  -> 标的台账  -> 本息垫付成功");
					
					flash.success("本息垫付成功,请核对账单");
					
					//改变担保人账户中待还垫付金额
					t_guarantee gua = guaranteeService.findByColumn("user_id=?", userId);
					gua.compensate_amount = gua.compensate_amount+advanceAmt;
					gua.save();
					
					//标的借款账单表添加担保人id
					t_bill billss = billService.findByID(billId);
					billss.guarantee_user_id = userId;
					billss.save();
					
					//正常还款时改变贷款业务信息表中的数据
					t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
					t_bill bills = billService.findByID(billId);
					if(loans != null) {
						loans.last_repayment_date = new Date();
						Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
						loans.actual_amount = bills.repayment_corpus + zhis;
						loans.time = new Date();
						Integer zhi = Integer.parseInt(loans.resudue_months);
						loans.resudue_months = (zhi - 1)+"";
						loans.save();
					}
				}
			}
				
			showLoanBillsPre(0,1,10);

		}
		
		flash.error("状态非法，不能执行线下收款");
		showLoanBillsPre(0,1,10);
		}
		
	}
	
	/**
	 * 线下收款
	 * @rightID 502002
	 *
	 * @param billSign 借款账单
	 *
	 * @author huangyunsong
	 * @createDate 2016年1月22日
	 */
	public static void offlineReceive (String billSign) {
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		long supervisorId = getCurrentSupervisorId();
		
		/** 线下收款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			
			// 托管 查询代偿账户可用余额
			String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
			
			result = PaymentProxy.getInstance().queryCompensatoryBalance(Client.PC.code, businessSeqNo);
			if (result.code < 1) {
				flash.error("查询商户可用余额异常");
				showLoanBillsPre(0,1,10);
			}
			
			Map<String, Object> merDetail = (Map<String, Object>) result.obj;
			double merBalance = Convert.strToDouble(merDetail.get("pBlance").toString(), 0);
			
			//垫付总额
			double advanceAmt = bill.repayment_corpus + bill.repayment_interest + bill.overdue_fine;
			
			if (advanceAmt > merBalance) {
				flash.error("商户余额不足，请充值");
				showLoanBillsPre(0,1,10);
			}
			
			result = billService.offlineReceive(supervisorId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				return;
			}
			
			t_bid bid = bidService.findByID(bill.bid_id);
			
			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", "0");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", 95);//代偿平台账户
			accMap.put("cebitAccountNo", bid.object_acc_no);
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(bill.repayment_corpus));
			accMap.put("summaryCode", "T08");
			
			Map<String, Object> intMap = new HashMap<String, Object>();
			intMap.put("oderNo", "1");
			intMap.put("oldbusinessSeqNo", "");
			intMap.put("oldOderNo", "");
			intMap.put("debitAccountNo", 95);//代偿平台账户
			intMap.put("cebitAccountNo", bid.object_acc_no);
			intMap.put("currency", "CNY");
			intMap.put("amount", YbUtils.formatAmount(Arith.add(bill.overdue_fine, bill.repayment_interest)));
			intMap.put("summaryCode", "T12");
			
			List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
			accountList.add(accMap);
			accountList.add(intMap);
			
			Map<String, Object> conMap = new HashMap<String, Object>();
			conMap.put("oderNo", "0");
			conMap.put("contractType", "");
			conMap.put("contractRole", "");
			conMap.put("contractFileNm", "");
			conMap.put("debitUserid", "");
			conMap.put("cebitUserid", "");
			
			List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
			contractList.add(conMap);
			
			String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CANCEL);
			
			if (ConfConst.IS_TRUST) {
			
				result = PaymentProxy.getInstance().offlineReceive(OrderNoUtil.getClientSn(), serviceOrderNo, bid, ServiceType.COMPENSATORY, EntrustFlag.UNENTRUST, accountList, contractList);
				if (result.code < 0) {
					LoggerUtil.info(true, result.msg);
					
					flash.error(result.msg);
				} else {
					
					result = billService.doOfflineReceives(businessSeqNo, bill, bill.overdue_fine, bid);
					if (result.code < 0) {
						
						flash.error(result.msg);
					} else {
						Logger.info("代偿台账  -> 标的台账  -> 线下收款成功");
						
						flash.success("线下收款成功,请核对账单");
						
						//正常还款时改变贷款业务信息表中的数据
						t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
						t_bill bills = billService.findByID(billId);
						if(loans != null) {
							loans.last_repayment_date = new Date();
							Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
							loans.actual_amount = bills.repayment_corpus + zhis;
							loans.time = new Date();
							Integer zhi = Integer.parseInt(loans.resudue_months);
							loans.resudue_months = (zhi - 1)+"";
							loans.save();
						}
					}
				}
					
				showLoanBillsPre(0,1,10);

			}
		}
		
		/** 本息垫付后线下收款 */
		if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){

			//更新借款账单还款状态
			boolean updateBill = billService.updateRepaymentData(bill.id, t_bill.Status.OUT_LINE_PRINCIIPAL_RECEIVE);
			if (!updateBill) {
				result.code = -1;
				result.msg = "借款人还款成功，更新借款账单还款数据失败";
				
			}
					
			showLoanBillsPre(0,1,10);

		}
		
		flash.error("状态非法，不能执行线下收款");
		showLoanBillsPre(0,1,10);
		
	}
	
	/**
	 * 后台提前还款
	 * @param billSign
	 * @createDate 2017年5月12日
	 * @author lihuijun
	 */
	public static void backAllLoan(){
		
		ResultInfo result = callBackService.checkGuaranteeGU(params, ServiceType.COMPENSATORY);
		
		if (result.code < 0) {
			flash.error(result.msg);
			
			showLoanBillsPre(0,1,10);
		}
		
		String businessSeqNo = result.msg;
		
		//备注参数调用
		Map<String, String> remarkParams = callBackService.queryRequestParamss(businessSeqNo);
		
		String billSign = remarkParams.get("t_bill_sign");
		String userIds = remarkParams.get("r_user_id");
		long userId = Long.parseLong(userIds);
		
		if(userId == 0) {
			flash.error("请选择担保人");
			showLoanBillsPre(0,1,10);
		}

		result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_user_fund userFund = userFundService.queryUserFundByUserId(userId);
		
		if(userFund.payment_account == null || userFund.payment_account.equals("")) {
			flash.error("该担保人还未开户");
			showLoanBillsPre(0,1,10);
		}
		
		//long supervisorId = getCurrentSupervisorId();
		
		// 托管 查询代偿账户可用余额
		//String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
		
		result = PaymentProxy.getInstance().queryGuaranteeBalance(Client.PC.code, businessSeqNo,Long.parseLong(userFund.payment_account));
		
		if (result.code < 1) {
			flash.error("查询商户可用余额异常");
			showLoanBillsPre(0,1,10);
		}
		
		Map<String, Object> merDetail = (Map<String, Object>) result.obj;
		double merBalance = Convert.strToDouble(merDetail.get("pBlance").toString(), 0);
		
		List<t_bill_invest> billInvestList=billInvestService.getBillInvestListByBidIdAndStatus(bill.bid_id);
		double surplusCorpus=0; //所有待还本金
		for (t_bill_invest  billIn:billInvestList) {
			surplusCorpus+=billIn.receive_corpus;
			
		}
		List<t_bill_invest> billInvestOverdueList = billInvestService.getInvestOverduelist(bill.bid_id);
		double surplusFine=0; //所有罚息（包括当月，因为当月是0所以可以不用去除）
		double surplusInterest=0; //所有利息（包括当月跟逾期利息）
		if(billInvestOverdueList!=null && billInvestOverdueList.size()>0){
			for(t_bill_invest interest:billInvestOverdueList){
				surplusFine+=interest.overdue_fine;
				surplusInterest+=interest.receive_interest;
			}
		}	
		
		//垫付总额			
		double advanceAmt = surplusCorpus + surplusInterest + surplusFine ;  //本金合计+利息合计+罚息合计
		
		if (advanceAmt > merBalance) {
			flash.error("商户余额不足，请充值");
			showLoanBillsPre(0,1,10);
		}
		
		/** 提前收款 */
		if (t_bill.Status.NORMAL_NO_REPAYMENT.equals(bill.getStatus())) {
			
			result = billService.advanceReceive(userId, bill);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				
				return;
			}
			
			t_bid bid = bidService.findByID(bill.bid_id);
			
			Map<String, Object> accMap = new HashMap<String, Object>();
			accMap.put("oderNo", "0");
			accMap.put("oldbusinessSeqNo", "");
			accMap.put("oldOderNo", "");
			accMap.put("debitAccountNo", userFund.payment_account);//代偿平台账户
			accMap.put("cebitAccountNo", bid.object_acc_no);
			accMap.put("currency", "CNY");
			accMap.put("amount", YbUtils.formatAmount(surplusCorpus));
			accMap.put("summaryCode", "T08");
			
			Map<String, Object> intMap = new HashMap<String, Object>();
			intMap.put("oderNo", "1");
			intMap.put("oldbusinessSeqNo", "");
			intMap.put("oldOderNo", "");
			intMap.put("debitAccountNo", userFund.payment_account);//代偿平台账户
			intMap.put("cebitAccountNo", bid.object_acc_no);
			intMap.put("currency", "CNY");
			intMap.put("amount", YbUtils.formatAmount(Arith.add(surplusInterest, surplusFine)));
			intMap.put("summaryCode", "T12");
			
			List<Map<String, Object>> accountList = new ArrayList<Map<String, Object>>();
			accountList.add(accMap);
			accountList.add(intMap);
			
			Map<String, Object> conMap = new HashMap<String, Object>();
			conMap.put("oderNo", "0");
			conMap.put("contractType", "");
			conMap.put("contractRole", "");
			conMap.put("contractFileNm", "");
			conMap.put("debitUserid", "");
			conMap.put("cebitUserid", "");
			
			List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
			contractList.add(conMap);
			
			//String serviceOrderNo = OrderNoUtil.getNormalOrderNo(ServiceType.BID_CANCEL);
			
			if (ConfConst.IS_TRUST) {
			
				result = PaymentProxy.getInstance().offlineReceive(OrderNoUtil.getClientSn(), businessSeqNo, bid, ServiceType.COMPENSATORY, EntrustFlag.UNENTRUST, accountList, contractList);
				if (result.code < 0) {
					LoggerUtil.info(true, result.msg);
					
					flash.error(result.msg);
				} else {
					
					result = billService.doAheadReceive(businessSeqNo, bill, bill.overdue_fine, bid, userId, advanceAmt);
					if (result.code < 0) {
						
						flash.error(result.msg);
					} else {
						Logger.info("代偿台账  -> 标的台账  -> 提前收款成功");
						
						flash.success("提前收款成功,请核对账单");
						
						//提前还款改变账单中的信息
						billService.updateThisBill(billId,surplusCorpus,surplusInterest,surplusFine);
						
						List<t_bill> billList=billService.findBillByBidIdAndStatus(bill.bid_id); 
						if(billList!=null && billList.size()>0){
							for(t_bill bil:billList){
								bil.setStatus(7);
								//bil.setIs_overdue(false);
								bil.repayment_corpus=0;
								bil.repayment_interest=0;
								bil.overdue_fine=0;//罚息置0
								bil.save();
							}
						}
						List<t_bill_invest> theBillInvests=billInvestService.billInvestListByBidIdAndPeriod(bill.bid_id,bill.period);
						if(billInvestList!=null && billInvestList.size()>0){ //某标所有未还账单投资的集合 
							double interest=0;
							double fine=0;
							for(t_bill_invest bine:theBillInvests){
								interest=0;
								fine=0;
								for(t_bill_invest bin:billInvestList){
									if(bine.invest_id==bin.invest_id && !bine.id.equals(bin.id)){
										bine.receive_corpus+=bin.receive_corpus;
									}
								}
								if(billInvestOverdueList!=null && billInvestOverdueList.size()>0){	
									for(t_bill_invest binOver:billInvestOverdueList){
										if(bine.invest_id==binOver.invest_id){
											interest+=binOver.receive_interest;
											fine+=binOver.overdue_fine;
										}
									}
								}
								bine.receive_interest=interest;
								bine.overdue_fine=fine;	
								bine.save();
							}
							
							for(t_bill_invest bin:billInvestList){
								boolean flag=false;
								for(t_bill_invest bine:theBillInvests){
									if(bine.id.equals(bin.id)){
										flag=true;
										break;
									}
								}
								if(!flag){
									billInvestService.updateThisBillInvest(bin.id);
								}
							}
						}
						
						if (billService.isEndPayment(bill.bid_id)) {
							bidService.bidEnd(bill.bid_id);
						}
						
						
						
						//正常还款时改变贷款业务信息表中的数据
						t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
						t_bill bills = billService.findByID(billId);
						if(loans != null) {
							loans.last_repayment_date = new Date();
							Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
							loans.actual_amount = bills.repayment_corpus + zhis;
							loans.time = new Date();
							Integer zhi = Integer.parseInt(loans.resudue_months);
							loans.resudue_months = (zhi - 1)+"";
							loans.save();
						}
					}
				}
					
				showLoanBillsPre(0,1,10);

			}
			
		}
		
		/** 本息垫付后线下收款 */
		if (t_bill.Status.ADVANCE_PRINCIIPAL_NO_REPAYMENT.equals(bill.getStatus())){
			
			result = billService.doOfflineReceiveAfterAdvance(userId, billId);
			if (result.code < 1) {
				LoggerUtil.info(true, result.msg);
				return;
			}
			
			flash.success(result.msg);
			showLoanBillsPre(0,1,10);
			
			return ;
		}
		
		flash.error("状态非法，不能执行线下收款");
		showLoanBillsPre(0,1,10);
	}

	/**
	 * 正常出款
	 * 
	 * @author niu
	 * @create 2017.09.28
	 */
	public static void doPayment(String billSign) {
		
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		
		result = billService.doPaymentOne(bid, bill, getCurrentSupervisorId());
		if (result.code < 0) {
			
			flash.error(result.msg);
			showLoanBillsPre(0,1,10);
		}
		
		flash.success("出款成功！");
		showLoanBillsPre(0,1,10);
	}
	
	/**
	 * 垫付出款
	 * 
	 * @author liuyang
	 * @create 2017.10.13
	 */
	public static void doPaidMoney(String billSign) {
		
		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		
		result = billService.doPaymentOne(bid, bill, getCurrentSupervisorId());
		if (result.code < 0) {
			
			flash.error(result.msg);
			showLoanBillsPre(0,1,10);
		}
		
		flash.success("垫付出款成功！");
		showLoanBillsPre(0,1,10);
	}
	
	/**
	 * 线下出款
	 * 
	 * @author liuyang
	 * @create 2017.10.14
	 */
	public static void doOfflineMoney(String billSign) {

		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		
		result = billService.doPaymentOne(bid, bill, getCurrentSupervisorId());
		if (result.code < 0) {
			
			flash.error(result.msg);
			showLoanBillsPre(0,1,10);
		}
		
		flash.success("线下出款成功！");
		showLoanBillsPre(0,1,10);
	}
	
	/**
	 * 提前出款
	 * 
	 * @author liuyang
	 * @create 2017.10.14
	 */
	public static void doAheadMoney(String billSign) {

		ResultInfo result = Security.decodeSign(billSign, Constants.BILL_ID_SIGN, Constants.VALID_TIME, ConfConst.ENCRYPTION_KEY_DES);
		if (result.code < 1) {
			LoggerUtil.info(false, "签名检验失败");
			
			error404();
		}
		
		long billId = Long.parseLong(result.obj.toString());
		
		
		t_bill bill = billService.findByID(billId);
		if (bill == null) {
			
			error404();
		}
		
		t_bid bid = bidService.findByID(bill.bid_id);
		
		result = billService.doPaymentOne(bid, bill, getCurrentSupervisorId());
		if (result.code < 0) {
			
			flash.error(result.msg);
			showLoanBillsPre(0,1,10);
		}
		
		flash.success("提前出款成功！");
		showLoanBillsPre(0,1,10);
	}
	
	/**
	 * ajax获取获取还款时担保人信息
	 * 
	 * @param key
	 * 
	 * @author liuyang
	 * @createDate 2018年1月8日
	 */
	public static void queryBondsmans(int currPage, int pageSize, String billSign, String key) {

		if (currPage < 1) {
			currPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 10;
		}
		flash.put("searchKey", key);

		// 限制最大长度
		if (StringUtils.isNotBlank(key) && key.length() > 16) {
			key = key.substring(0, 16);
		}

		PageBean<t_guarantee> pageBean = guaranteeService.queryGuaranteesByKey(currPage, pageSize, key);

		render(pageBean, billSign);
	}
	
}
