package models.common.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 用户顶部资产信息
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月20日
 */
@Entity
public class UserFundInfo {

	@Id
	@GeneratedValue
	public long id;
	
	/** 可用余额 */
	public double balance;
	
	/** 总收益 */
	public double total_income;
	
	/** 待回款资金笔数 */
	public int no_payment_count;
	
	/** 成功投资笔数 */
	public int invest_count;
	
	/** 总资产 */
	@Transient
	public double total_assets;
	
	/** 我的奖励 */
	public double reward;
	
	/** 冻结资金 */
	public double freeze;
	
	/** 待还 */
	public double no_repayment;
	
	/** 待收 */
	public double no_receive;
	
}
