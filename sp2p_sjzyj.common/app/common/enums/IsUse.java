package common.enums;


/**
 * 枚举：上下架 '0-下架\r\n1-上架'
 *
 * @author liudong
 * @createDate 2015年12月28日
 */

public enum IsUse {

	/** 1-上架 */
	USE(true, "上架"),

	/** 0-下架 */
	NO_USE(false, "下架");

	public boolean code;
	public String value;

	private IsUse(boolean code, String value) {
		this.code = code;
		this.value = value;
	}

	public static IsUse getEnum(boolean code) {
		IsUse[] is_Use = IsUse.values();
		for (IsUse isUse : is_Use) {
			if (isUse.code == code) {

				return isUse;
			}
		}

		return null;
	}
}
