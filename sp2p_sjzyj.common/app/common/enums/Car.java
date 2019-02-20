package common.enums;


/**
 * 枚举:车贷情况
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum Car {

	/** 无车贷 */
	NONE_CAR(1,"无车"),
	
	/** 按揭购车 */
	MORTGAGE(2,"按揭购车"),
	
	/** 全款购车 */
	FULL_AMOUNT(3,"全款购车");
	
	public int code;
	public String value;  
	
	private Car(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Car getEnum(int code){
		Car[] cars = Car.values();
		for (Car car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
	
}
