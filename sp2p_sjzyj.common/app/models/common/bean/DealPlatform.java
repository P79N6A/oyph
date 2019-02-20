package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import models.common.entity.t_deal_platform.DealOption;
import models.common.entity.t_deal_platform.DealType;

/**
 * 平台交易记录 查询(含会员名)
 *
 * @author liudong
 * @createDate 2016年1月14日
 */
@Entity
public class DealPlatform {

	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 交易记录类型 */
	public int operation;
	
	/** 用户id */
	public long user_id;
	
	/** 交易金额 */
	public double amount;
	
	/** 类型:1-收入，2-支出 */
	public int type;
	
	/** 备注 */
	public String remark;
	
	/** 会员昵称 */
	public String name;
	
	public DealOption getOperation() {
		DealOption operat = DealOption.getEnum(this.operation);
		return operat;
	}
		
	public DealType getType() {
		DealType dealType = DealType.getEnum(this.type);
		return dealType;	
	}

	public String getRemark() {
		return remark;
	}

}
