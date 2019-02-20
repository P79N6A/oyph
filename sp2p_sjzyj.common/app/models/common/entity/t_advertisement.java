package models.common.entity;

import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;

import common.constants.ConfConst;
import common.constants.Constants;
import common.enums.IsUse;
import common.enums.Target;
import common.utils.Security;

/**
 * 实体 广告图片
 *
 * @author liudong
 * @createDate 2015年12月28日
 */
@Entity
public class t_advertisement extends Model {
	
	/** 名称 */
	public String name;

	/** 添加时间 */
	public Date time;
	
	/** 排序时间  */
	public Date order_time;
	
	/** 所在位置 1-广告图片 2-登录底图 3-首页轮播广告 4-平台优势 5-理财轮播广告 6-资讯轮播广告  */
	private int location;
	
	/** 图片路径 */
	public String image_url;
	
	/** 图片分辨率 */
	public String image_resolution;
	
	/** 图片大小 */
	public String image_size;
	
	/** 图片格式 */
	public String image_format;
	
	/** 广告链接 */
	public String url;
	
	/** 链接打开方式 1-_self  2-_blank */
	private int target;
	
	/** 0.下架  1上架 */
	private boolean is_use;
	
	@Transient
	public String sign;

	public String getSign() {
		String signID = Security.addSign(id, Constants.ADS_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public Target getTarget() {
		Target target = Target.getEnum(this.target);
		
		return target;
	}

	public void setTarget(Target target) {
		this.target = target.code;
	}

	public IsUse getIs_use() {
		IsUse isUse = IsUse.getEnum(this.is_use);
		
		return isUse;
	}

	public void setIs_use(IsUse isUse) {
		this.is_use = isUse.code;
	}
	
	public Location getLocation() {
		Location location = Location.getEnum(this.location);
		
		return location;
	}

	public void setLocation(Location location) {
		this.location = location.code;
	}

	/** 前台首页banner显示的效果 */
	static String[] clazz = {"cube","cubeRandom","block","cubeStop","cubeStopRandom","cubeHide","cubeSize","horizontal","showBars","showBarsRandom","tube","fade","fadeFour","paralell","blind","blindHeight","blindWidth","directionTop","directionBottom","directionRight","directionLeft","cubeSpread","glassCube","glassBlock","circles","circlesInside","circlesRotate","cubeShow","upBars","downBars","hideBars","swapBars","swapBarsBack","swapBlocks","cut"};
	/**
	 * 首页Bannner展示效果专用
	 *
	 * @return
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年1月12日
	 */
	public String getRandmonClass(){
		
		return clazz[new Random().nextInt(clazz.length)];
	}
	
	/**
	 * 枚举：广告图片位置
	 * 1-登录底图  2-PC端首页轮播广告   3-平台优势   4-理财轮播广告   5-资讯轮播广告
	 *
	 * @author liudong
	 * @createDate 2015年12月29日
	 */
	public enum Location {

		/** 1-登录底图 */
		LOGIN_BOTTOM_PIC(1, "登录底图"),
		
		/** 2-首页轮播广告  */
		HOME_TURN_ADS(2, "首页轮播广告"),

		/** 3-平台优势 */
		PLAT_ADVANTAGE_ADS(3, "平台优势"),
		
		/** 4-理财轮播广告   */
		INVEST_TURN_ADS(4, "理财轮播广告"),

		/** 5-资讯轮播广告  */
		INFOR_TURN_ADS(5, "资讯轮播广告"),
		
		/** 6-微信端主页轮播图片  */
		WX_HOME_TURN_ADS(6, "微信轮播广告"),

		/** 7-微信端主页轮播图片  */
		APP_HOME_TURN_ADS(7, "APP轮播广告"),
		
		/** 8-积分商城主页轮番图片*/
		SCORE_HOME_TURN_ADS(8, "积分轮播广告"),
		
		/** 9-积分商城主页轮番图片*/
		APP_INTEGRAL_TURN_ADS(9, "APP积分轮播广告");
		
		
		public int code;
		public String value;

		private Location(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static Location getEnum(int code) {
			Location[] locations = Location.values();
			for (Location location : locations) {
				if (location.code == code) {

					return location;
				}
			}

			return null;
		}
	}

}
