package yb.enums;

/**
 * 支付类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum PayType {

	BANK_PAYMENT(1, "00"), //银行支付渠道
	
	THREE_PARTY(2, "01");//第三方支付渠道
	
	
	public int code;
	public String value;
	
	private PayType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static PayType getEnum(int code) {
		PayType[] types = PayType.values();
		
		for (PayType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
}
