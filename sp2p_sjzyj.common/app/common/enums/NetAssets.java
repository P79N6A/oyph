package common.enums;


/**
 * 枚举:资产估值
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum NetAssets {

	/** 5W以下:1 */
	LESS_THAN_50000(1,"5W以下"),
	
	/** 5W~10W:2 */
	BETWEEN_50000_100000(2,"5W~10W"),
	
	/** 10W~50W:3 */
	BETWEEN_100000_500000(3,"10W~50W"),
	
	/** 50W以上:4 */
	MORE_THAN_500000(4,"50W以上");
	
	public int code;
	public String value;  
	
	private NetAssets(int code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static NetAssets getEnum(int code){
		NetAssets[] assets = NetAssets.values();
		for (NetAssets asset : assets) {
			if (asset.code == code) {

				return asset;
			}
		}
		
		return null;
	}

}
