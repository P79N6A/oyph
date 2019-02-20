package common.enums;

/**
 * 枚举类型:设备类型
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum DeviceType {

	/** 2：安卓客户端 */
	DEVICE_ANDROID(2,"Android","安卓客户端"),
	
	/** 3：IOS客户端 */
	DEVICE_IOS(3,"IOS","IOS客户端");
	
	/** 设备类型标识码 */
	public int code;
	
	/** 设备名称  */
	public String client;
	
	/** 描述 */
	public String description;
	
	private DeviceType(int code, String client, String description) {
		this.code = code;
		this.client = client;
		this.description = description;
	}
	
	public static DeviceType getEnum(int code){
		DeviceType[] clients = DeviceType.values();
		for (DeviceType cli : clients) {
			if (cli.code == code) {
				return cli;
			}
		}
		return null;
	} 

}
