package models.ext.redpacket.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.ext.redpacket.entity.t_red_packet_user.RedpacketStatus;

/**
 * 红包加息券发放记录
 * 
 * @author LiuPengwei
 * @createDate 2018年5月18日14:25:21
 *
 */

@Entity
public class MaketInvestRedBean {
	
	@Id
	public long id;
	
	/** 用户名 */
	public String name;
	
	/** 发放渠道 */
	public String channel;
	
	/** 创建时间 */
	public Date time ;
	
	/** 红包金额 */
	public double red_packet_amount;
	
	/** 使用规则 */
	public double use_rule;
		
	/** 过期时间 */
	public Date limit_time;
	
	
	/** 创造投资 */
	public double create_invest;
	
	/** key */
	public String _key;
	
	/** 红包的状态:使用状况0（未使用）,1（已使用）,-1（已过期）,-2(使用中)*/
	private int status;
	
	public String getStatus() {
		String statuse = RedpacketStatus.getEnum(status);
		return statuse;
	}
	
	
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 枚举:红包的状态
	 *
	 * @description 
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月21日10:44:48
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

		public static String getEnum(int code){
			RedpacketStatus[] dts = RedpacketStatus.values();
			for (RedpacketStatus dt: dts) {
				if (dt.code == code) {
					return dt.value;
				}
			}
			
			return null;
		}
	}
}
