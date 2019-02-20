package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 信用等级实体类
 * 
 * @author liuyang
 * @createDate 2018.01.23
 */
@Entity
public class t_credit extends Model {

	/** 投资待还金额 */
	public double residue_amount;
	
	/** 信用等级 */
	public int credit_class;
	
	/** 每日限额 */
	public double day_quota;
	
	/** 类型 */
	public String type;
	
	/** 待还金额 */
	public String residue_amounts;
	
	/** 限额 */
	public String day_quotaes;
	
	/**出借人单标出借额度*/
	public double single_bid_invest_quota;
	
	/**单标出借次数*/
	public int single_bid_invest_count;
	
	/**规定时间出借次数*/
	public int rule_time_invest_count;
	
	/**规定时间出借额度*/
	public double rule_time_invest_quota;
	
	/**出借标的种类*/
	public String invest_type;
	
	/**时间*/
	public Date time;
	
}
