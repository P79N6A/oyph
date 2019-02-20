package yb.enums;

/**
 * 会员角色
 * 
 * 
 * @author niu
 * @create 2017.08.25
 */
public enum CustomerRole {

	INVESTOR(1, "00"), //投资方
	
	FINANCIER(2, "01"),//融资方
	
	GUARANTOR(3, "02"),//担保方
	
	WHOLE(4, "09");//全部
	
	public int code; 
	public String value;
	
	private CustomerRole(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static CustomerRole getEnum(int code) {
		
		CustomerRole[] roles = CustomerRole.values();
		
		for (CustomerRole role : roles) {
			
			if (role.code == code) {
				return role;
			}
		}
		
		return null;
	}
	
}
