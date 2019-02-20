package common.enums;


/**
 * 枚举类型:工作年限
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum WorkExperience {

	/** 1年以下:1 */
	LESS_THAN_1_YEWAR(1,"1年以下"),
	
	/** 1年~3年:2 */
	BETWEEN_1T3_YEWARS(2,"1年~3年"),
	
	/** 3年~5年:3 */
	BETWEEN_3T5_YEWARS(3,"3年~5年"),
	
	/** 5年以上:4 */
	MORE_THAN_5_YEARS(4,"5年以上");
	
	public int code;
	public String value;  
	
	private WorkExperience(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static WorkExperience getEnum(int code){
		WorkExperience[] experiences = WorkExperience.values();
		for (WorkExperience experience : experiences) {
			if (experience.code == code) {

				return experience;
			}
		}
		
		return null;
	}

}
