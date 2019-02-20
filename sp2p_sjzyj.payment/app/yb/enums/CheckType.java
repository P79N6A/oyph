package yb.enums;

/**
 * 查询类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum CheckType {

	CUSTOMER_INFO_QUERY(1, "01"), //客户信息查询
	
	SYSTEM_DATE_QUERY(2, "02"), //系统日期查询
	
	ACCOUNT_INFO_QUERY(3, "03");//台账信息查询
	
	public int code;
	public String value;
	
	private CheckType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static CheckType getEnum(int code) {
		
		CheckType[] types = CheckType.values();
		for (CheckType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
}
