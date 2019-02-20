package common.enums;


/**
 * 枚举类型:性别
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月4日
 */
public enum Gender {

	/** 性别:男性 */
	MALE("男性","male",1),
	
	/** 性别:女性 */
	FEMALE("女性","female",2),
	
	/** 性别:保密，未知 */
	UNKNOWN("保密","unkonwn",0);
	
	/** 性别:中文标签 */
	public String name;  
	
	/** 性别:英文标签 */
	public String enname;  
	
	/** 编号 */
	public int code;  
	
	private Gender(String name,String enname, int code) {
		this.name=name;
		this.enname=enname;
		this.code=code;
	}
	
	/**
	 * 返回对应code的枚举类型，如果没有则默认为UNKNOWN
	 *
	 * @param code
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2015年12月4日
	 */
	public static Gender getEnum(int code){
		Gender[] genders = Gender.values();
		for (Gender gender : genders) {
			if (gender.code == code) {
				return gender;
			}
		}
		return UNKNOWN;
	}

}
