package yb.enums;

/**
 * 证件类型
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum CertType {

	IDENTITY_CARD(1, "00"),//身份证
	
	RESIDENT_CARD(2, "01"),//居民证
	
	VISA(3, "02"),//签证
	
	PASSPORT(4, "03"),//护照
	
	HOUSEHOLD_REGISTER(5, "04"),//户口本
	
	SERVICEMAN_CARD(6, "05"), //军人证
	
	HONGKONG_MACAO_PASSPORT(7, "06");//港澳通行证
	
	public int code;
	public String value;
	
	private CertType(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static CertType getEnum(int code) {
		
		CertType[] types = CertType.values();
		
		for (CertType type : types) {
			
			if (type.code == code) {
				return type;
			}
		}
		
		return null;
	}
	
}
