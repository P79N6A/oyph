package controllers.back.finance;

import java.util.Date;
import java.util.Map;

import com.shove.Convert;

import common.enums.Client;
import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import common.utils.number.Arith;
import controllers.common.BackBaseController;
import models.bean.PaymentRequestLogs;
import models.common.entity.t_cost;
import models.common.entity.t_loan_profession;
import models.core.entity.t_bid;
import models.core.entity.t_bill;
import models.entity.t_payment_request;
import payment.impl.PaymentProxy;
import play.Logger;
import services.PaymentRequstService;
import services.common.LoanProfessionService;
import services.core.BidService;
import services.core.BillService;
import yb.enums.ServiceType;

/**
 * 后台-财务-前台还款校对-控制器
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2018年03月26日
 */
public class RepaymentCheckMngCtrl extends BackBaseController {
	
	protected static BillService billService = Factory.getService(BillService.class);
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static LoanProfessionService loanProfessionService = Factory.getService(LoanProfessionService.class);
	
	protected static PaymentRequstService paymentRequstService = Factory.getService(PaymentRequstService.class);
	
	public static void showRepaymentCheckPre() {
		
		render();
	}
	
	public static void paymentCheck(long bid_id, long bill_id, String businessSeqNo) {
		
		ResultInfo result = new ResultInfo();
		
		t_bid bid = bidService.findByID(bid_id);
		
		if(bid == null) {
			flash.error("标的信息不存在，请重新输入");
		}
		
		t_bill bill = billService.findByID(bill_id);
		
		if(bill == null) {
			flash.error("借款账单不存在，请重新输入");
		}
		
		t_payment_request payRequst = paymentRequstService.queryByRequestMark(businessSeqNo);
		
		if(payRequst == null) {
			flash.error("流水号不存在，请重新输入");
		}
		
		result = billService.doRepaymentOne(businessSeqNo, bill, bill.overdue_fine, bid);
		if (result.code < 0) {
			
			flash.error(result.msg);
		} else {
			Logger.info("融资人台账  -> 标的台账  -> 还款成功");
			
			flash.success("还款成功,请核对账单");
			
			//正常还款时改变贷款业务信息表中的数据
			t_loan_profession loans = loanProfessionService.queryLoanByBidId(bill.bid_id);
			if(loans != null) {
				loans.last_repayment_date = new Date();
				Double zhis = Double.parseDouble(loans.agreement_repayment_amount);
				loans.actual_amount = bill.repayment_corpus + zhis;
				loans.time = new Date();
				Integer zhi = Integer.parseInt(loans.resudue_months);
				loans.resudue_months = (zhi - 1)+"";
				loans.save();
			}
			
			// 查询费用账户可用余额
			String businessSeqNo3 = OrderNoUtil.getNormalOrderNo(ServiceType.QUERY_MESSAGE);
			
			ResultInfo result3 = PaymentProxy.getInstance().queryCostBalance(Client.PC.code, businessSeqNo3);
			if (result3.code < 1) {
				
				flash.error("查询费用账户可用余额异常");
				showRepaymentCheckPre();
			}
			
			Map<String, Object> merDetail3 = (Map<String, Object>) result3.obj;
			double cosBalance = Convert.strToDouble(merDetail3.get("pBlance").toString(), 0);
			
			double serviceAmount =  Arith.round(Arith.mul(bid.amount, bid.service_charge), 2)/100 /12 ;
			
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
		
		showRepaymentCheckPre();
	}

}
