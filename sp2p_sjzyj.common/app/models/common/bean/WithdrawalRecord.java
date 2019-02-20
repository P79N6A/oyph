package models.common.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 提现记录
 *
 * @author liudong
 * @createDate 2016年2月24日
 */
@Entity
public class WithdrawalRecord {
	
	@Id
	public Long id;
	
	/** 业务订单号 */
	public String order_no;
	
	/** 昵称 */
	public String name;
	
	/** 提现时间 */
	public Date time;
	
	/** 交易金额 */
	public double amount;
	
}
