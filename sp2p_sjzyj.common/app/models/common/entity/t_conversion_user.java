package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Transient;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

import play.db.jpa.Model;

/**
 * 兑换记录表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月16日
 */
@Entity
public class t_conversion_user extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** 关联用户ID */
	public long user_id;
	
	/** 兑换类型
		1-红包
		2-体验金
		3-CPS
	 */
	private int conversion_type;
	
	/** 兑换金额 */
	public double amount;
	
	/** 审核时间 */
	public Date audit_time;
	
	/** 兑换状态
		0-申请兑换
		1-已兑换
	 */
	private int status;
	
	/** 对id进行验签加密后的字符串 */
	@Transient
	public String sign;
	
	public String getSign() {
		
		String signID = Security.addSign(id, Constants.CONV_ID_SIGN, ConfConst.ENCRYPTION_KEY_DES);
		
		return signID;
	}
	
	public ConversionType getConversion_type() {
		return ConversionType.getEnum(conversion_type);
	}

	public void setConversion_type(ConversionType conversion_type) {
		this.conversion_type = conversion_type.code;
	}

	public ConversionStatus getStatus() {
		
		return ConversionStatus.getEnum(status);
	}

	public void setStatus(ConversionStatus status) {
		this.status = status.code;
	}

	/**
	 * 
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public enum ConversionType {

		/** 1:体验金 */
		EXPERIENCE(1, "体验金","t_experience_bid_account"),
		
		/** 2:红包 */
		REDPACKET(2, "红包","t_red_packet_account"),
		
		/** 3:红包 */
		CPS(3, "CPS推广","t_cps_account"),
		
		/** 4:财富圈 */
		WEALTHCIRCLE(4, "财富圈","t_wealthcircle_account"),
		
		;
		
		public int code;
		public String value;  
		public String accountTable;
		
		private ConversionType(int code, String value,String accountTable) {
			this.code = code;
			this.value = value;
			this.accountTable = accountTable;
		}

		public static ConversionType getEnum(int code){
			ConversionType[] dts = ConversionType.values();
			for (ConversionType dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
	
	
	/**
	 * 
	 *
	 * @description 
	 *
	 * @author DaiZhengmiao
	 * @createDate 2016年2月16日
	 */
	public enum ConversionStatus {

		/** 0:兑换处理中 */
		APPLYING(0, "处理中"),
		
		/** 1:已领取 */
		RECEIVED(1, "已兑换");
		
		public int code;
		public String value;  
		
		private ConversionStatus(int code, String value) {
			this.code = code;
			this.value = value;
		}

		public static ConversionStatus getEnum(int code){
			ConversionStatus[] dts = ConversionStatus.values();
			for (ConversionStatus dt: dts) {
				if (dt.code == code) {
					return dt;
				}
			}
			
			return null;
		}
	}
}
