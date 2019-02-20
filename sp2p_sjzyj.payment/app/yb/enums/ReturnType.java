package yb.enums;

/**
 * 还款方式
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum ReturnType {

	REPAY_CAPITAL_INTEREST(3, "01", "一次性还款"), //一次行还本付息
	
	FIRST_INTEREST_LATER_CAPITAL(1, "02", "先息后本"),//先息后本
	
	EQUAL_INTEREST(2, "03", "等额本息"),//等额本息
	
	EQUAL_CAPITAL(4, "04", ""), //等额本金
	
	EQUAL_CAPITAL_EQUAL_INTEREST(5, "05", ""),//等本等息
	
	EXPIRE_RETURN_ALL(6, "06", ""), //期满还清
	
	REPAY_INTEREST(7, "07", ""), //一次付息
	
	MONTH_INTEREST_EXPIRE_CAPITAL(8, "08", ""),//按月付息，到期还本
	
	SEASON_INTEREST_EXPIRE_CAPITAL(9, "09", ""), //按季付息，到期还本
	
	MONTH_EQUAL_INTEREST(10, "10", ""), //按月等额本息
	
	MONTH_EQUAL_CAPITAL(11, "11", ""), //按月等额本金
	
	MONTH_AVERAGE_INTEREST(12, "12", ""),//月平息
	
	YEAR_INTEREST_EXPIRE_CAPITAL(12, "13", "");//按年付息到期还本
	
	
	public int code;
	public String value;
	public String key;
	
	private ReturnType(int code, String value, String key) {
		this.code = code;
		this.value = value;
		this.key = key;
	}
	
	public static ReturnType getEnum(int code) {
		
		ReturnType[] types = ReturnType.values();
		for (ReturnType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
	public static ReturnType getEnum(String value) {
		ReturnType[] types = ReturnType.values();
		for (ReturnType type : types) {
			if (type.value.equals(value)) {
				return type;
			}
		}
		
		return null;
	}
	
	
	
}
