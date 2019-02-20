package models.common.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 用户统计字段
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月7日
 */
@Entity
public class UserFundStatistic {

	@Id
	@GeneratedValue
	public long id;
	
	/** 可用余额总数 */
	public double balance_total;
	
	/** 冻结金额总数 */
	public double freeze_total;
	
	/** 待收金额总数 */
	public double no_receive_total;
	
	/** 待还金额总数 */
	public double no_repayment_total;
}
