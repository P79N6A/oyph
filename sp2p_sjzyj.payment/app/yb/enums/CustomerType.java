package yb.enums;

/**
 * 会员类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum CustomerType {

	PERSON(1, "00"), //个人
	
	ENTERPRISE(2, "01");//企业
	
	public int code;
	public String value;
	
	private CustomerType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static CustomerType getEnum(int code) {
		
		CustomerType[] types = CustomerType.values();
		
		for (CustomerType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
}
