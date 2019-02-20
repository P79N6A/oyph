package yb.enums;

/**
 * 标的类型 ( 特殊标的字段，00-为普通标的  01-为特殊标的，特殊标的需要验密 )
 * 
 * @author niu
 * @create 2018-01-03
 */
public enum ObjectType {

	
	ORDINARY_OBJECT(1, "00"), //普通标的
	
	 SPECIAL_OBJECT(2, "01"); //特殊标的
	
	public int code;
	public String value;
	
	private ObjectType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static ObjectType getEnum(int code) {
		
		ObjectType[] statuss = ObjectType.values();
		for (ObjectType status : statuss) {
			
			if (status.code == code) {
				return status;
			}
		}
		
		return null;
	}
}
