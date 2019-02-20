package models.proxy.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 代理商表
 * @author Niu Dongfeng
 */
@Entity
public class t_proxy extends Model {

	public Date time; 					/** 添加时间  */
	
	public int sale_count; 			/** 业务员数量  */
	
	public int total_user_count;		/** 会员数量  */
	
	public int invest_user_count; 		/** 理财会员数量  */
	
	public double total_amount;		/** 理财总金额  */
	
	public double total_profit;		/** 理财总提成  */
	
	public double cur_total_invest;  	/** 当月理财会员总投资  */
	
	public double cur_year_convert;	/** 当月年会折算  */
	
	public double cur_profit;			/** 当月提成  */
	
	public String profit_rule;			/** 收益规则  */
		
}
