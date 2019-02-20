package common.enums;

/**
 * 枚举类型:职务
 * 
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum Duty {

	/** 高级领导 */
	TOP_LEADER(1,"高级领导"),
	
	/** 中级领导 */
	MIDDLE_LEADER(2,"中级领导"),
	
	/** 一般员工 */
	EMPLOYEES(3,"一般员工"),
	
	/** 其他 */
	REST(4,"其他"),
	
	/** 未知 */
	UNKNOW(9,"未知 ");
	
	public int code;
	public String value;  
	
	private Duty(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Duty getEnum(int code){
		Duty[] cars = Duty.values();
		for (Duty car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
