package yb.enums;

/**
 * 提现类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum WithdrawType {

	CUSTOMER_WITHDRAW(1, "W01"), //客户提现
	
	MARKETING_WITHDRAW(2, "W02"), //营销提现
	
	COMPENSTATE_WITHDRAW(3, "W03"), //代偿提现
	
	FEE_WITHDRAW(4, "W04"), //费用提现
	
	ADVANCE_MONEY_WITHDRAW(5, "W05"),//垫资提现
	
	ENTERPRISE_ENTRUST_WITHDRAW(6, "W06"); //企业委托提现
	
	public int code;
	public String value;
	
	private WithdrawType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static WithdrawType getEnum(int code) {
		
		WithdrawType[] types = WithdrawType.values();
		
		for (WithdrawType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
	
}
