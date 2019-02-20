package common.enums;

/**
 * 枚举：是否逾期  true 逾期   false 未逾期
 *
 * @author liudong
 * @createDate 2016年1月14日
 */

public enum IsOverdue {

	/** 1-逾期 */
	OVERDUE(true, "逾期"),

	/** 0-未逾期 */
	NO_OVERDUE(false, "未逾期");

	public boolean code;
	public String value;

	private IsOverdue(boolean code, String value) {
		this.code = code;
		this.value = value;
	}

	public static IsOverdue getEnum(boolean code) {
		IsOverdue[] is_overdue = IsOverdue.values();
		for (IsOverdue isOverdue : is_overdue) {
			if (isOverdue.code == code) {

				return isOverdue;
			}
		}

		return null;
	}
}
