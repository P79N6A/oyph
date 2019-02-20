package models.proxy.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 提成表（代理商、业务员）
 * @author Niu Dongfeng
 */
@Entity
public class t_proxy_profit extends Model {

	public int type; 				/** 类型：1 业务员， 2 代理商 */
	
	public long sale_id;			/** 代理商 id 或者 业务员 id */
	
	public String profit_time;		/** 收益时间 例如："2018-01" */
	
	public double invest_amount;	/** 理财金额 */
	
	public double year_convert;	/** 年化折算 */
	
	public double profit;			/** 收益 */
	
}
