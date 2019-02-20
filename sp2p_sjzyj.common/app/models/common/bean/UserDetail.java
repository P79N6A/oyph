package models.common.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 会员详情
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年1月5日
 */
@Entity
public class UserDetail {
	
	@Id
	@GeneratedValue
	public Long id;
	
	/** 总资产 */
	public double total_assets;
	
	/** 总收益 */
	public double total_income;
	
	/** 申请借款笔数 */
	public int apply_bid_count;

	/** 成功借款笔数 */
	public int complete_borrow_count;
	
	/** 还清借款笔数 */
	public int complete_repayment_count;
	
	/** 逾期金额 */
	public double overdue_fine;
	
	/** 逾期期数 */
	public int overdue_count;
	
	/** 借款总额 */
	public double total_borrow;

	/** 待还本息 */
	public double no_repayment_interest;

	/** 绑定银行卡数 */
	public int bank_card_count;
	
}
