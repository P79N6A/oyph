package common.enums;


/**
 * 枚举类型:用户紧急联系人
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2015年12月17日
 */
public enum Relationship {
	
	FATHER(0,"父子"),
	
	MOTHER(1,"母子"),
	
	SPOUSE(2,"配偶"),
	
	CHILDREN(3,"子女"),
	
	RELATIVE(4,"亲戚"),
	
	FRIEND(5,"同事"),
	
	COLLEAGUE(6,"朋友"),
	
	SISTERS(7,"兄弟姐妹"),
	
	UNKNOW(8,"其他");

	public int code;
	public String value;  
	
	private Relationship(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public static Relationship getEnum(int code){
		Relationship[] rs = Relationship.values();
		for (Relationship r : rs) {
			if (r.code == code) {

				return r;
			}
		}
		
		return null;
	}
}
