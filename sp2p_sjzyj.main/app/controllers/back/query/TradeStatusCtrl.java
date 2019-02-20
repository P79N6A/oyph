package controllers.back.query;

import java.util.Map;

import common.utils.OrderNoUtil;
import common.utils.ResultInfo;
import controllers.common.BackBaseController;
import payment.impl.PaymentProxy;
import yb.enums.ServiceType;

/**
 * 后台-查询-交易状态
 * 
 * 
 * @author niu
 * @create 2017.10.14
 */
public class TradeStatusCtrl extends BackBaseController {
	
	
	/**
	 * 资金交易状态查询-准备
	 * 
	 * @author niu
	 * @create 2017.10.14
	 */
	public static void tradeStatusPre(String msg, String oldbusinessSeqNo, String dealStatus, String respCode, String respMsg, String accountNo, String secBankaccNo, String objectaccNo) {
		render(msg, oldbusinessSeqNo, dealStatus, respCode, respMsg, accountNo, secBankaccNo, objectaccNo);
	}
	
	/**
	 * 资金交易状态查询-查询
	 * 
	 * @author niu
	 * @create 2017.10.16
	 */
	public static void queryTradeStatus(String oldbusinessSeqNo, String operType) {
		
		if (operType == null) {
			flash.error("请选择原交易类型");
			tradeStatusPre("", "", "" , "", "", "", "", "");
		}
		if (oldbusinessSeqNo == null || oldbusinessSeqNo.equals("")) {
			flash.error("请输入原交易流水号");
			tradeStatusPre("", "", "" , "", "", "", "", "");
		}
		
		ResultInfo result = PaymentProxy.getInstance().queryTradeStatus(OrderNoUtil.getClientSn(), OrderNoUtil.getNormalOrderNo(ServiceType.getEnumByKey(operType)), oldbusinessSeqNo, operType);
		
		if (result != null && result.code > 0) {
			
			String dealStatus = "";
			switch (result.msgs.get("dealStatus")) {
			case "0":
				dealStatus = "失败";
				break;
			case "1":
				dealStatus = "成功";
				break;
			case "2":
				dealStatus = "处理中";
				break;
			}
			
			tradeStatusPre(result.msg, result.msgs.get("oldbusinessSeqNo"), dealStatus, result.msgs.get("respCode"), result.msgs.get("respMsg"), result.msgs.get("accountNo"), result.msgs.get("secBankaccNo"), result.msgs.get("objectaccNo"));
		}
		flash.error(result.msg);
		tradeStatusPre("查询失败", "", "" , "", "", "", "", "");
	}
	
	
}
