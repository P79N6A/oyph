package yb.enums;

/**
 * 其他金额类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum OtherAmountType {

	FEE(1, "01"), //费用
	
	BID_INCOME(2, "02"), //标的收益
	
	WITHDRAW_FEE(3, "03"), //提现手续费
	
	RECHARGE_FEE(4, "04"); // 充值手续费
	
	public int code; 
	public String value;
	
	private OtherAmountType(int code, String value) {
		this.code = code; 
		this.value = value;
	}
	
	public static OtherAmountType getEnum(int code) {
		
		OtherAmountType[] types = OtherAmountType.values();
		for (OtherAmountType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
}
