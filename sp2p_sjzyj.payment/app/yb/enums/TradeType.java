package yb.enums;

/**
 * 托管平台交易类型
 * 
 * @author niu
 * @create 2017.08.24
 */
public enum TradeType {
	
	CUSTOMER_INFO_SYNC(1, "U00001", "客户信息同步"),
	
	CUSTOMER_ENTRUST_PROTOCOL(2, "U00004", "客户委托协议签署,撤销"),
	
	BID_INFO_SYNC(3, "O00001", "标的信息同步"),
	
	CUSTOMER_RECHARGE_WITHDRAW(4, "T00001", "资金充值提现"),
	
	FUND_TRADE(5, "T00004", "资金交易"),
	
	TRADE_STATUS_QUERY(6, "C00001", "交易状态查询"),
	
	DATA_INFO_QUERY(7, "C00002", "客户信息查询"),
	
	ASYN_CALL_BACK (8,"R00001","异步处理结果返回"),
	
	H5_CHECK_TRADE_PASS(9, "H50000", "H5页面校验交易密码");
	
	public int code;
	public String key;
	public String value;
	
	private TradeType(int code, String key, String value) {
		this.code = code;
		this.key = key;
		this.value = value;
	}
	
	public static TradeType getEnum(int code) {
		TradeType[] tradeTypes = TradeType.values();
		
		for (TradeType tradeType : tradeTypes) {
			
			if (tradeType.code == code) {
				return tradeType;
			}
		}
		
		return null;
	}
	
	public static TradeType getEnumByKey(String key) {
		TradeType[] tradeTypes = TradeType.values();
		
		for (TradeType tradeType : tradeTypes) {
			
			if (tradeType.key.equals(key)) {
				return tradeType;
			}
		}
		
		return null;
	}
	
}









