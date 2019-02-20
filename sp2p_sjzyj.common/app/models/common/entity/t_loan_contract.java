package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 贷款合同信息表
 *  
 * @description 
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */

@Entity
public class t_loan_contract extends Model {

	/** 标的编号(贷款合同号码) */
	public String mer_bid_no;
	
	/** 贷款合同生效日期 */
	public Date begin_time;
	
	/** 贷款合同终止日期 */
	public Date end_time;
	
	/** 币种  人民币CNY */
	public String currency_type;
	
	/** 贷款合同金额 */
	public double contract_amount;
	
	/** 合同有效状态 */
	public int status;
}
