package yb.enums;

/**
 * 是否自动投标标志 ( 00：非自动投标标的 01：自动投标标的 )
 * 
 * @author niu
 * @create 2018-01-03
 */
public enum AutoFlag {

	AUTO_INVEST(1, "00"),
	NON_AUTO_INVEST(2, "01");
	
	public int code;
	public String value;
	
	private AutoFlag(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static AutoFlag getEnum(int code) {
		
		AutoFlag[] statuss = AutoFlag.values();
		for (AutoFlag status : statuss) {
			
			if (status.code == code) {
				return status;
			}
		}
		
		return null;
	}
}
