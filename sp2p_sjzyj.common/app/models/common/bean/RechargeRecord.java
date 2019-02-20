package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import models.common.entity.t_deal_user;
import models.common.entity.t_deal_user.OperationType;

/**
 * 会员充值
 *
 * @author liudong
 * @createDate 2016年2月24日
 */
@Entity
public class RechargeRecord {
	
	@Id
	public Long id;
	
	/** 业务订单号 */
	public String order_no; 
	
	/** 会员昵称 */
	public String name;
	
	/** 交易金额 */
	public double amount;
	
	/** 交易类型：1-充值；2-提现；3-放款；4-还款；5-投资 */
	public int operation_type;
	public OperationType getOperation_type() {
		OperationType dealType = OperationType.getEnum(this.operation_type);
		return dealType;
	}
	
	/** 充值时间 */
	public Date time;
	
}
