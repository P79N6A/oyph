package common.enums;

/**
 * 枚举:担保方式
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum GuarantyStyle {

	/** 质押(含保证金) */
	PLEDGE(1,"质押(含保证金)"),
	
	/** 抵押 */
	GUARANTY(2,"抵押"),
	
	/** 自然人保证 */
	NATURAL_GUARANTEE(3,"自然人保证"),
	
	/** 信用/免担保 */
	CREDIT(4,"信用/免担保"),
	
	/** 组合(含自然人保证) */
	GROUP_HAVE(5,"组合(含自然人保证)"),
	
	/** 组合(不含自然人保证) */
	GROUP_NOHAVE(6,"组合(不含自然人保证)"),
	
	/** 农户联保 */
	FARM_GUARANTEE(7,"农户联保"),
	
	/** 其他 */
	TEST(9,"其他");
	
	public int code;
	public String value;  
	
	private GuarantyStyle(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static GuarantyStyle getEnum(int code){
		GuarantyStyle[] cars = GuarantyStyle.values();
		for (GuarantyStyle car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
