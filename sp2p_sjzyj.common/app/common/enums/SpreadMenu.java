package common.enums;

/**
 * 后台-推广-推广下拉菜单
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
public enum SpreadMenu {

	/** 1:CPS推广 */
	CPS(1,"CPS推广","/supervisor/spread/cpsSetting/toCpsSetting.html"),
	
	/** 2:财富圈 */
	WEALTHCIRCLE(2,"财富圈","/supervisor/spread/wealthCircleSetting/toWealthCircleSetting.html"),
		
	/** 4:红包 */
	//REDPACKET(4,"红包","/supervisor/spread/redpacket/showRedpackets.html"),
	
	/** 5:体验标 */
	EXPERIENCEBID(5, "体验标", "/supervisor/spread/experiencebid/showExperienceBid.html"),
	
	/** 6:数据统计 */
	DATASTATISTICS(6, "数据统计", "/supervisor/spread/data/showDatas.html");
	
	/** 7:加息券活动 */
	//ADDRATE(7, "加息券", "/supervisor/spread/addrate/addRateAct.html");
	
	public int code;
	public String value;
	public String url;
	
	private SpreadMenu(int code, String value,String url) {
		this.code = code;
		this.value = value;
		this.url = url;
	}  
	
	public Integer getEnumValue() {
		
		return this.code;
	}

	public String getEnumName() {
		
		return this.value;
	}

	public String getUrl() {
		return url;
	}
	
}
