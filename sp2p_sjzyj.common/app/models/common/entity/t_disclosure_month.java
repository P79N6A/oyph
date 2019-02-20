package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 信息披露月数据统计表
 * 
 * @description
 *
 * @author liuyang
 * @createDate 2017年12月15日
 */
@Entity
public class t_disclosure_month extends Model {

	/** 信息披露外键 */
	public long disclosure_id;
	
	/** 月交易金额 */
	public String mon_amount;
	
	/** 月交易笔数 */
	public String mon_count;
	
	/** 前十大交易总额 */
	public String top_ten_amount;
	
	/** 月出借人数 */
	public String mon_lender;
	
	/** 月借款人数 */
	public String mon_borrower;
	
	/** 添加时间 */
	public Date time;
	
	/** 年时间 */
	public String year_time;
	
	/** 月时间 */
	public String month_time;
}
