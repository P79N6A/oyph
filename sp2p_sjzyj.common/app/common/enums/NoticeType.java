package common.enums;

/**
 * 枚举:系统发送给用户的消息类型(SMS,MSG,EMAIL)
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月24日
 */
public enum NoticeType {

	/** 1:SMS */
	SMS(1,"短信","sms"),
	
	/** 1:msg */
	MSG(2,"消息","msg"),
	
	/** 3:Email */
	EMAIL(3,"邮件","email");
	
	public int code;
	public String value;
	public String description;
	
	private NoticeType(int code, String value, String description) {
		this.code = code;
		this.value = value;
		this.description = description;
	}
	
	public static NoticeType getEnum(int type){
		NoticeType[] incomes = NoticeType.values();
		for(NoticeType income:incomes){
			if(income.code == type){
				
				return income;
			}
		}
		
		return null;
	}
}
