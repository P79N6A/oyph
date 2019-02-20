package yb.enums;

/**
 * 绑定类型
 * 
 * 
 * @author niu
 * @create 2017.05.25
 */
public enum BindType {

	RECHARGE(1, "00"), //充值
	
	WITHDRAW(2, "01"), //提现
	
	WHOLE(3, "99"); //全部
	
	public int code;
	public String value;
	
	private BindType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static BindType getEnum(int code) {
		
		BindType[] types = BindType.values();
		
		for (BindType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
}
