package models.proxy.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 业务员表
 * @author Niu Dongfeng
 */
@Entity
public class t_proxy_salesman extends Model {

	public Date time;					/** 添加时间  */
	
	public String sale_name;			/** 业务员姓名  */
	
	public String sale_mobile;			/** 业务员手机号  */
	
	public String sale_pwd;				/** 密码（加密后）  */
	
	public int sale_status;			/** 业务员状态：1正常 ，2 锁定  */
	
	public int cur_user;				/** 当月推广会员  */
	
	public int cur_invest_user;		/** 当月理财会员  */
	
	public double cur_invest_amount;	/** 当月理财总金额  */
	
	public double cur_year_convert;	/** 当月总投资的年化折算  */
	
	public double cur_profit;			/** 当月收益  */
	
	public int type; 					/** 业务员类型：1 业务员，2 代理商  */
	
	public long proxy_id;				/** 代理商 id  */
	
	public int total_user;				/** 推广会员  */
	
	public int total_invest_user;		/** 推广理财会员  */
	
	public double total_amount;		/** 理财总金额  */
	
	public double total_profit;		/** 理财总提成  */
	
}
