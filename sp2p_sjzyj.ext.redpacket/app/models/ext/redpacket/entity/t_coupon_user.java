package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:用户加息券表
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年7月24日
 */
@Entity
public class t_coupon_user extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 加息券唯一标识 */
	public String _key;
	
	/** 加息券详细名称 */
	public String coupon_name;
	
	/** 加息券名称 */
	public String coupon;
	
	/** 用户的id */
	public Long user_id;
	
	/** 加息券年利率 */
	public double amount;
	
	/** 加息券状态的状态:使用状况0（未激活）,1（未领取）,-1（已过期）, 2(已领取)，3（已使用）*/
	private int status;
	
	/** 锁定时间*/
	public Date lock_time ;
	
	
	/** 使用条件 */
	public double use_rule ;
	
	
	/** 有效时间 */
	public Date limit_time ;
	
	/** 标使用加息劵标记  */
	
	public Long bid_id ;
	
	public double money ;
	
	
	
	public CouponStatus getStatus() {
		CouponStatus statuse = CouponStatus.getEnum(status);
		return statuse;
	}
	
	
	public void setStatus(CouponStatus status) {
		this.status = status.code;
	}

	/**
	 * 枚举:红包的状态
	 *
	 * @description 
	 *
	 * @author LiuPengwei
	 * @createDate 2017年7月24日
	 */
	public enum CouponStatus {
		
		/** 0:未激活 */
		INACTIVATED(0, "未激活"),
		
		/** 1:未领取 */
		UNRECEIVED(1, "未领取"),
		
		/** 2:已领取 */
		RECEIVED(2, "已领取") ,
		
		/** 3:已使用  */
		ALREADY_USED(3, "已使用"),
		
		/** -1:已过期  */
		EXPIRED(-1, "已过期") ;
		
		public int code;
		
		public String value;  
		
		private CouponStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static CouponStatus getEnum(int code){
			CouponStatus[] dts = CouponStatus.values();
			for (CouponStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
}
