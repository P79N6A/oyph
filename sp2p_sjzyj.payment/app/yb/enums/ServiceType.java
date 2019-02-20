package yb.enums;

/**
 * 托管平台业务类型
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum ServiceType {
	
	PERSON_CUSTOMER_REGIST(1, "U01", "person_customer_regist", "个人客户注册"),
	
	CUSTOMER_INFO_MODIFY(2, "U02", "customer_info_modify", "客户信息修改"),
	
	CUSTOMER_CANCEL(3, "U03", "customer_cancel", "客户注销"),
	
	ENTERPRISE_CUSTOMER_REGIST(4, "U04", "enterprise_customer_regist", "企业客户注册"),
	
	CUSTOMER_BIND_CARD(5, "B01", "customer_bind_card", "客户绑卡"),
	
	CUSTOMER_UNBINDING(6, "B02", "customer_unbinding", "客户解绑"),
	
	CUSTOMER_BIND_MODIFY(7, "B03", "customer_bind_modify", "客户绑卡修改"),
	
	ENTRUST_PROTOCOL_AGREE(8, "B04", "entrust_protocol_agree", "委托协议同意"),
	
	ENTRUST_PROTOCOL_CANCEL(9, "B05", "entrust_protocol_cancel", "委托协议取消"),
	
	BID_PUBLIC(10, "P01", "bid_public", "标的发布"),
	BID_FAILED(11, "P02", "bid_failed", "标的流标"),
	BID_CANCEL(12, "P03", "bid_cancel", "标的撤标"),
	BID_MODIFY(13, "P04", "bid_modify", "标的修改"),
	
	CUSTOMER_RECHARGE(14, "R01", "customer_recharge", "客户充值"),
	
	CUSTOMER_WITHDRAW(15, "W01", "customer_withdraw", "客户提现"),
	
	SET_USER_PASSWORD(16, "C01", "set_user_password", "设置交易密码"),
	
	AMEND_USER_PASSWORD(17, "C02", "amend_user_password", "修改交易密码"),
	
	RESET_USER_PASSWORD(18, "C03", "reset_user_password", "重置交易密码"),
	
	FORGET_PASSWORD(19, "C04", "forget_password", "校验交易密码"),
	
	QUERY_BINDED_BANK(20, "C05", "query_binded_bank", "查询绑定的银行卡"),
	
	QUERY_MESSAGE(21, "C06", "query_message", "查询台账信息"),
	
	
	CANCEL_TENDER(22, "T02", "cancel_tender", "取消投标"),
	LOAN(23, "T03", "loan", "放款"),
	REPAYMENT(24, "T04", "repayment", "还款"),
	PAYMENT(25, "T05", "payment", "出款"),
	ATTORN(26, "T06", "attorn", "转让"),
	FARM_IN(27, "T07", "farm_in", "受让"),
	COMPENSATORY(28, "T08", "compensatory", "代偿"),
	COMPENSATORY_PAYMENT(29, "T09", "compensatory_payment", "代偿回款"),
	COMPENSATORY_PAYMENT_CHECK_TRADE_PASS(103, "T09", "compensatory_payment_check_trade_pass", "解绑 - 校验交易密码"),
	MARKET(30, "T10", "market", "营销"),
	WASHED(31, "T11", "washed", "冲正"),
	TENDER(32, "T01", "tender", "投标"),
	
	COMPENSATORY_RECHARGE(40, "R04", "customer_recharge", "代偿充值"),
	
	COMPENSATORY_WITHDRAW(41, "W03", "customer_withdraw", "代偿提现"),
	
	MARKET_RECHARGE(42, "R03", "customer_recharge", "营销充值"),
	
	MARKET_WITHDRAW(43, "W02", "customer_withdraw", "营销提现"),
	
	COST_RECHARGE(44, "R05", "customer_recharge", "费用充值"),
	
	COST_WITHDRAW(45, "W04", "customer_withdraw", "费用提现"),
	
	ENTERPRISE_WITHDRAW(46,"W06", "enterprise_withdraw","企业提现" ),
	
	TRADE_STATUS_QUERY(81, "TSQ01", "trade_status_query", "交易状态查询"),
	
	CUSTOMER_INFO_QUERY(71, "01", "customer_info_query", "用户信息查询"),
	
	SYSTEM_DATE_QUERY(72, "02", "system_date_query", "系统日期查询"),
	
	ACCOUNT_INFO_QUERY(73, "03", "account_info_query", "台账信息查询"),
	
	ASYN_CALL_BACK(100, "CB100", "asyn_call_back","异步回调"),
	
	CUSTOMER_INTERNATBANK_RECHARGE(101, "R02", "customer_internetbank_recharge", "客户网银充值"),
	
	ENTERPRISE_INTERNATBANK_RECHARGE(102, "R02", "enterprise_internetbank_recharge", "企业网银充值"),
	
	TENDER_CHECK_TRADE_PASS(127, "T127", "tender_check_trade_pass", "投标 - 校验交易密码"),
	REPAYMENT_CHECK_TRADE_PASS(126, "T126", "repayment_check_trade_pass", "还款 - 校验交易密码"),
	UNBINDING_CHECK_TRADE_PASS(125, "T125", "unbinding_check_trade_pass", "解绑 - 校验交易密码")
	;
	
	
	
	public int code;
	public String key;
	public String value;
	public String note;
	
	private ServiceType(int code, String key, String value, String note) {
		this.code = code;
		this.key = key;
		this.value = value;
		this.note = note;
	}
	
	public static ServiceType getEnum(int code) {
		ServiceType[] status = ServiceType.values();
		for (ServiceType statu : status) {
			if (statu.code == code) {
				
				return statu;
			}
		}
		
		return null;
	}
	
	public static ServiceType getEnumByKey(String key) {
		ServiceType[] status = ServiceType.values();
		for (ServiceType statu : status) {
			if (statu.key.equals(key)) {
				
				return statu;
			}
		}
		
		return null;
	}
	
	
	
	
	
	
}
