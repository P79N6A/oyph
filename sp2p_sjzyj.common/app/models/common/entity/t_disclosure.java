package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 信息披露表
 * 
 * @description
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */

@Entity
public class t_disclosure extends Model {

	/** 历史成交量 */
	public String total_amount;
	
	/** 累计收益 */
	public String total_earnings;
	
	/** 累计成交人次 */
	public String total_volume;
	
	/** 累计借款笔数 */
	public String total_count;
	
	/** 添加时间 */
	public Date time;
	
	/** 累计借款总余额 */
	public String total_balance;
	
	/** 累计待还借款人数量 */
	public String borrower_count;
	
	/** 累计待还出借人数量 */
	public String lender_count;
	
	/** 累计借款人人数量 */
	public String borrower_total_count;
	
	/** 累计出借人数量 */
	public String lender_total_count;
	
	/** 代偿笔数 */
	public String compensate_count;
	
	/** 代偿金额 */
	public String compensate_money;
	
	/** 前十大借款人待还金额占比 */
	public double ten_proportion;
	
	/** 最大单一借款人待还金额占比 */
	public double one_proportion;
	
	/** 年月时间 */
	public String year_time;
	
	/** 借贷笔数 */
	public String total_balance_count;
	
}
