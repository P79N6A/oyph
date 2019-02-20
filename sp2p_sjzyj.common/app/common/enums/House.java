package common.enums;


/**
 * 枚举类型:房贷情况
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum House {
	
	/** 无房贷 */
	NONE_HOUSE(1,"无房"),
	
	/** 按揭买房:2 */
	MORTGAGE(2,"按揭买房"),
	
	/** 全款买房:3 */
	FULL_AMOUNT(3,"全款买房");

	public int code;
	public String value;  
	

	private House(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static House getEnum(int code){
		House[] houses = House.values();
		for (House house : houses) {
			if (house.code == code) {

				return house;
			}
		}

		return null;
	}
	
}
