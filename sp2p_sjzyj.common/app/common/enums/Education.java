package common.enums;

/**
 * 枚举类型:最高学历
 *
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月23日
 */
public enum Education {
	
	/** 研究生:10 */
	GRADUATE_STUDENT(10,"研究生"),
	
	/** 大学本科:20 */
	UNDERGRADUATE(20,"大学本科"),
	
	/** 大专:30 */
	COLLEGE(30,"大专"),
	
	/** 中等专业学校或中等技术学校:40 */
	SECONDARY(40,"中等专业学校或中等技术学校"),
	
	/** 技术学校:50 */
	SKILL_SCHOOL(50,"技术学校"),
	
	/** 高中:60 */
	HIGH_SCHOOL(60,"高中"),
	
	/** 初中:70 */
	MIDDLE_SCHOOL(70,"初中"),
	
	/** 小学:80 */
	PRIMARY_SCHOOL(80,"小学"),
	
	/** 文盲或半文盲:90 */
	ILLITERACY(90,"文盲或半文盲"),

	/** 未知:99 */
	UNKNOW(99,"未知");

	public int code;
	public String value;  
	
	private Education(int code, String value) {
		this.code = code;
		this.value = value;
	}

	/**
	 * 根据编码获取学历,没有返回null
	 *
	 * @param code
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月8日
	 */
	public static Education getEnum(int code){
		Education[] educations = Education.values();
		for (Education education : educations) {
			if (education.code == code) {

				return education;
			}
		}
		
		return null;
	}
}
