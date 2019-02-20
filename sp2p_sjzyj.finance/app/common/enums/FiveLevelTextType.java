package common.enums;

public enum FiveLevelTextType {

	NORMAL(1,"FL01","正常类"),
	FOLLOW(2,"FL02","关注类"),
	SECONDARY(3,"FL03","次级类"),
	SUSPICIOUS(4,"FL04","可疑类"),
	LOSS(5,"FL05","损失类");
	
	public int code;
	public String value;
	public String description;
	
	private FiveLevelTextType (int code,String value,String description) {
		this.code = code;
		this.value = value;
		this.description = description;
	}
	
	public FiveLevelTextType getEnum (int code) {
		FiveLevelTextType [] levelType = FiveLevelTextType.values();
		if (levelType != null && levelType.length > 0) {
			for (FiveLevelTextType type : levelType) {
				if (type.code == code) {
					return type;
				}
			}
		}
		return null;
	}
}
