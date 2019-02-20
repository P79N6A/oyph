package yb.enums;

/**
 * 会员账户状态
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum AccountStatus {

	NORMAL(1, "01"), //正常
	
	FROZEN(2, "02"),//冻结
	
	CANCEL_ACCOUNT(3, "03");// 销户
	
	public int code;
	public String value;
	
	private AccountStatus(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static AccountStatus getEnum(int code) {
		
		AccountStatus[] statuss = AccountStatus.values();
		for (AccountStatus status : statuss) {
			
			if (status.code == code) {
				return status;
			}
		}
		
		return null;
	}
}
