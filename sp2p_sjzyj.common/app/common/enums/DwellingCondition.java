package common.enums;

public enum DwellingCondition {

	/** 自置 */
	OWNED(1,"自置"),
	
	/** 按揭 */
	INSTALLMENT(2,"按揭"),
	
	/** 亲属楼宇 */
	RELATIVE_BUILDING(3,"亲属楼宇"),
	
	/** 集体宿舍 */
	DORMITORY(4,"集体宿舍"),
	
	/** 租房 */
	TENT(5,"租房"),
	
	/** 共有住宅 */
	PUBLIC_BUILDING(6,"共有住宅"),
	
	/** 其他 */
	REST(7,"其他"),
	
	/** 未知 */
	UNKNOW(9,"未知 ");
	
	public int code;
	public String value;  
	
	private DwellingCondition(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static DwellingCondition getEnum(int code){
		DwellingCondition[] cars = DwellingCondition.values();
		for (DwellingCondition car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
