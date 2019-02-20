package yb.enums;

/**
 * 委托标识
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum EntrustFlag {
	
	ENTRUST(1, "01"),// 委托
	UNENTRUST(2, "00");// 未委托
	
	public int code;
	public String value;
	
	private EntrustFlag(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static EntrustFlag getEnum(int code) {
		
		EntrustFlag[] flags = EntrustFlag.values();
		
		for (EntrustFlag flag : flags) {
			
			if (flag.code == code) {
				return flag;
			}
		}
		
		return null;
	}
	
	
}
