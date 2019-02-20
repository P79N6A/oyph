package models.ext.redpacket.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.ext.redpacket.bean.MaketInvestRedBean.RedpacketStatus;


/**
 * 加息券发放记录
 *
 * @author LiuPengwei
 * @createDate 2018年5月29日
 */
@Entity
public class MaketInvestAddBean {

	@Id
	public long id;
	
	/** 用户名 */
	public String name;

	/** 发放渠道 */
	public String channel;
	
	/** 创建时间 */
	public Date time ;
	
	/** 过期时间 */
	public Date etime;
	
	/** 加息券利息 */
	public double apr;
	
	/** 最低投资金额 */
	public double use_rule;
		
	/** 创造投资 */
	public double create_invest;

	/** 加息券状态：1（未使用），2（使用中），3（已使用），4（已失效）*/
	private int status;

	public String getStatus() {
		String statuse = RedpacketStatus.getEnum(status);
		return statuse;
	}
	
	
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 枚举:加息券的状态
	 *
	 * @description 
	 *
	 * @author LiuPengwei
	 * @createDate 2018年5月29日10:47:46
	 */
	public enum RedpacketStatus {
		
		
		/** 1:已领取 */
		RECEIVED(1, "已领取") ,
		
		/** 使用中 */
		IN_USED(2, "使用中") ,
		
		/** 3:已使用  */
		ALREADY_USED(3, "已使用"),
		
		/** 4:已过期  */
		EXPIRED(4, "已过期") ;
		
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
