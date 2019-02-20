package controllers.back.query;

import java.util.UUID;

import com.shove.Convert;

import common.utils.Factory;
import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import models.common.entity.t_user_fund;
import models.core.entity.t_bid;
import payment.impl.PaymentProxy;
import services.common.UserFundService;
import services.core.BidService;
import yb.enums.ServiceType;


public class DealReversalCtrl extends BackBaseController {
	
	protected static BidService bidService = Factory.getService(BidService.class);
	
	protected static UserFundService userFundService = Factory.getService(UserFundService.class);

	public static void dealReversalPre() {
		
		render();
	}
	
	/**
	 * 资金交易冲正-标的转让冲正
	 * 
	 * @param bidId 标的id
	 * @param alienatorNum 转让人账号
	 * @param assigneeNum 受让人账号
	 * @param oldbusinessSeqNo 原业务流水号
	 * @param amount 金额
	 * 
	 * @author liuyang
	 * @create 2018年8月9日
	 */
	public static void transferReversalSync(String bidId, String alienatorNum, String assigneeNum, String oldbusinessSeqNo, String amount) {
		
		if (bidId.equals("") || alienatorNum.equals("") || assigneeNum.equals("") || oldbusinessSeqNo.equals("") || amount.equals("")){
			flash.error("请输入所有必填信息！");
			dealReversalPre();						
		}
		
		double amounts = Convert.strToDouble(amount, 0);
		long bidIds = Convert.strToLong(bidId, 0);
		
		t_bid bid = bidService.findByID(bidIds);
		if(bid == null) {
			flash.error("输入的标号有误");
			dealReversalPre();
		}
		
		t_user_fund alienatorFund = userFundService.findByColumn("name=?", alienatorNum);
		if(alienatorFund == null) {
			flash.error("转让人账号有误");
			dealReversalPre();
		}
		
		t_user_fund assigneeFund = userFundService.findByColumn("name=?", assigneeNum);
		if(assigneeFund == null) {
			flash.error("受让人账号有误");
			dealReversalPre();
		}
		
		String businessSeqNo = OrderNoUtil.getNormalOrderNo(ServiceType.WASHED);
		
		String clientSn = "oyph_" + UUID.randomUUID().toString();
		
		PaymentProxy.getInstance().reversalTransfer(businessSeqNo, clientSn,assigneeFund.payment_account, alienatorFund.payment_account, amounts, oldbusinessSeqNo, bid.mer_bid_no);
		
		flash.success("标的转让冲正成功！");
		dealReversalPre();
	}
}
