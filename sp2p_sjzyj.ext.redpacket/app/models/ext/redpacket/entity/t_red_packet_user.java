package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:用户红包表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月14日
 */
@Entity
public class t_red_packet_user extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 红包唯一标识 */
	public String _key;
	
	/** 红包名称 */
	public String red_packet_name;
	
	/** 用户的id */
	public Long user_id;
	
	/** 红包的金额 */
	public double amount;
	
	/** 红包的状态:使用状况0（未使用）,1（已使用）,-1（已过期）,-2(使用中)*/
	private int status;
	
	/** 锁定时间*/
	public Date lock_time ;
	
	/** 投资ID*/
	public long invest_id ;
	
	/** 使用条件 */
	public double use_rule ;
	
	/**有效时长（天） */
	public int limit_day ;
	
	/** 有效时间 */
	public Date limit_time ;
	
	/** 发放渠道 */
	public String channel;
	
	/**
	 * 营销流水号
	 */
	public String old_biz_no ;
	
	/** 创造投资 */
	public double create_invest;
	
	/** 红包标的期限 */
	public int bid_period;
	
	/**锁定时长(天)*/
	public Integer lock_day;
	
	public RedpacketStatus getStatus() {
		RedpacketStatus statuse = RedpacketStatus.getEnum(status);
		return statuse;
	}
	
	
	public void setStatus(RedpacketStatus status) {
		this.status = status.code;
	}

	/**
	 * 枚举:红包的状态
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月14日
	 */
	public enum RedpacketStatus {
		
		/** 0:未激活 */
		INACTIVATED(0, "未激活"),
		
		/** 1:未领取 */
		UNRECEIVED(1, "未领取"),
		
		/** 2:已领取 */
		RECEIVED(2, "已领取") ,
		
		/** 3:已使用  */
		ALREADY_USED(3, "已使用"),
		
		/** -1:已过期  */
		EXPIRED(-1, "已过期") ,
		
		/** 使用中 */
		IN_USED(-2, "使用中") ;
		
		public int code;
		
		public String value;  
		
		private RedpacketStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static RedpacketStatus getEnum(int code){
			RedpacketStatus[] dts = RedpacketStatus.values();
			for (RedpacketStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
}
