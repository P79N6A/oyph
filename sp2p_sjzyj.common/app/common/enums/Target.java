package common.enums;


/**
 * 枚举： 链接打开方式 1-_self 2-_blank
 *
 * @author liudong
 * @createDate 2015年12月28日
 */

public enum Target {
	
	/** 1-_self */
	_SELF(1, "_self", "当前页面打开"),

	/** 2-_blank */
	_BLANK(2, "_blank", "新页面打开");

	public int code;
	public String value;
	public String description;

	private Target(int code, String value, String description) {
		this.code = code;
		this.value = value;
		this.description = description;
	}

	public static Target getEnum(int code) {
		Target[] targets = Target.values();
		for (Target target : targets) {
			if (target.code==code) {

				return target;
			}
		}

		return null;
	}
}
