package yb.enums;

/**
 * 标的属性
 * 
 * @author niu
 * @create 2017.08.29
 */
public enum Nature {
	
	NET_LOAN(1, "00"),//网贷
	
	UN_NET_LOAN(2, "01");//非网贷
	
	public int code;
	public String value;
	
	private Nature(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static Nature getEnum(int code) {
		
		Nature[] natures = Nature.values();
		
		for (Nature nature : natures) {
			if (nature.code == code) {
				return nature;
			}
		}
		
		return null;
	}

}
