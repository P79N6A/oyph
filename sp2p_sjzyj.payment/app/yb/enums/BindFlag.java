package yb.enums;

/**
 * 绑卡标志
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum BindFlag {

	BIND(1, "00"),//绑卡
	UNBIND(2, "01");//未绑卡
	
	public int code;
	public String value;
	
	private BindFlag(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static BindFlag getEnum(int code) {
		
		BindFlag[] flags = BindFlag.values();
		
		for (BindFlag flag : flags) {
			
			if (flag.code == code) {
				return flag;
			}
		}
		
		return null;
	}
}
