package common.enums;

public enum PromotionType {
	
	FORCED(1, "强制升级"),
	OPTIONAL(2, "可选升级");
	
	private int code;
	private String value;
	
	private PromotionType(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static PromotionType getEnum(int code){
		PromotionType[] pt = PromotionType.values();
		for (PromotionType p : pt) {
			if (p.code == code) {

				return p;
			}
		}
		
		return null;
	}
}
