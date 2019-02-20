package common.enums;

/**
 * 枚举类型:最高学位
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum Degree {
	
	/** 其他 0 */
	REST(0,"其他"),
	
	/** 名誉博士 1 */
	HONORARY_DOCTOR(1,"名誉博士"),
	
	/** 博士 2 */
	DOCTOR(2,"博士"),
	
	/** 硕士 3 */
	MASTER(3,"硕士"),
	
	/** 学士 4 */
	BACHELOR(4,"学士"),
	
	/** 未知 9 */
	UNKNOW(9,"未知");
	
	public int code;
	public String value;  
	
	private Degree(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Degree getEnum(int code){
		Degree[] cars = Degree.values();
		for (Degree car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
