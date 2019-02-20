package yb.enums;

/**
 * 资金操作类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum FundTradeType {
	
	INVEST_BID(1, "T01"), //投标
	
	REPAY_MONEY(2, "T04"), //还款
	
	COMPENSTATE_PAYMENT(3, "T09"), //代偿回款
	
	WHOLE(4, "T99");
	
	public int code;
	public String value;
	
	private FundTradeType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static FundTradeType getEnum(int code) {
		
		FundTradeType[] types = FundTradeType.values();
		
		for (FundTradeType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
}
