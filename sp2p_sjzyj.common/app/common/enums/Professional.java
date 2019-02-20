package common.enums;

/**
 * 枚举类型:职称
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum Professional {

	/** 无 */
	NO(0,"无"),
	
	/** 高级 */
	TOP(1,"高级"),
	
	/** 中级 */
	MIDDLE(2,"中级"),
	
	/** 初级 */
	PRIMARY(3,"初级"),
	
	/** 未知 */
	UNKNOW(9,"未知 ");
	
	public int code;
	public String value;  
	
	private Professional(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Professional getEnum(int code){
		Professional[] cars = Professional.values();
		for (Professional car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
