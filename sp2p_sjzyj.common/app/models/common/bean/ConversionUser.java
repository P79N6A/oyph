package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.common.entity.t_conversion_user.ConversionStatus;
import models.common.entity.t_conversion_user.ConversionType;

import common.constants.ConfConst;
import common.constants.Constants;
import common.utils.Security;

/**
 * 奖励兑换记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年2月17日
 */
@Entity
public class ConversionUser {

	@Id
	@GeneratedValue
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 关联用户ID */
	public long user_id;
	
	/** 用户的名称 */
	public String userName;
	
	/** 兑换类型 1-红包 2-体验金 */
	public int conversion_type;
	
	/** 兑换金额 */
	public double amount;
	
	/** 审核时间 */
	public Date audit_time;
	
	/** 兑换状态 0-申请兑换 1-已兑换 */
	public int status;
	
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
}
