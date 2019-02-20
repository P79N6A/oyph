package common.enums;


/**
 * 枚举类型:连接窗口打开的方式
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月8日
 */
public enum LinkTarget {
	
	/** 在新窗口中打开被链接文档 */
	TARGET_SELF(1,"_self"),
	
	/** 在新窗口中打开被链接文档。 */
	TARGET_BLANK(2,"_blank"),
	
	/** 在父框架集中打开被链接文档。 */
	TARGET_PARENT(3,"_parent"),
	
	/** 在整个窗口中打开被链接文档。 */
	TARGET_TOP(4,"_top");
	
	public int code;
	public String target;
	
	private LinkTarget(int code, String target) {
		this.code = code;
		this.target = target;
	}


	public static LinkTarget getEnum(int code){
		LinkTarget[] statuies = LinkTarget.values();
		for(LinkTarget stat : statuies){
			if(stat.code==code){
				return stat;
			}
		}
		return LinkTarget.TARGET_SELF;
	} 
	
}
