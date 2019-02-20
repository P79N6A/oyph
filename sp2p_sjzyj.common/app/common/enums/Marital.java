package common.enums;


/**
 * 枚举:婚姻状况
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月9日
 */
public enum Marital {
	
	/** 未婚*/
	UNMARRRIED(10,"未婚"),
	
	/** 已婚 */
	MARRIED(20,"已婚"),
	
	/** 丧偶 */
	WIDOWED(30,"丧偶"),
	
	/** 离婚 */
	DIVORCE(40,"离婚"),
	
	/** 未知*/
	NOMARRRIED(90,"未知"),
	
	/** 未婚*/
	UNMARRRIEDS(0,"未婚"),
	
	/** 已婚 */
	MARRIEDS(1,"已婚");
	
	public int code;
	public String value;  
	
	private Marital(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Marital getEnum(int code){
		Marital[] maritals = Marital.values();
		for (Marital marital : maritals) {
			if (marital.code == code) {

				return marital;
			}
		}
		
		return null;
	}
	
}
