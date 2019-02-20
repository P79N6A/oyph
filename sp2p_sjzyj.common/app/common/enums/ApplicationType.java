package common.enums;

/**
 * 枚举:贷款申请类型
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum ApplicationType {

	/** 个人住房贷款*/
	HOUSING_LOANS(11,"个人住房贷款"),
	
	/** 个人商用房贷款、商住两用 */
	COMMERCIAL_LOANS(12,"个人商用房贷款、商住两用 "),
	
	/** 个人住房公积金贷款 */
	HOUSING_FUND(13,"个人住房公积金贷款"),
	
	/** 个人汽车消费贷款 */
	CAR_CONSUME(21,"个人汽车消费贷款"),
	
	/** 个人助学贷款 */
	STUDENT_LOANS(31,"个人助学贷款"),
	
	/** 个人经营性贷款 */
	BUSINESS_LOANS(41,"个人经营性贷款"),
	
	/** 农户贷款 */
	FARM_LOANS(51,"农户贷款"),
	
	/** 个人消费贷款 */
	CONSUME_LOANS(91,"个人消费贷款"),
	
	/** 其他 */
	REST(99,"其他");
	
	public int code;
	public String value;  
	
	private ApplicationType(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static ApplicationType getEnum(int code){
		ApplicationType[] cars = ApplicationType.values();
		for (ApplicationType car : cars) {
			if (car.code == code) {

				return car;
			}
		}
		
		return null;
	}
}
