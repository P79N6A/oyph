package models.proxy.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 代理商下业务员推广的会员表
 * 
 * @author Niu Dongfeng
 */
@Entity
public class t_proxy_user extends Model {
	
	public double cur_invest_amount; 	/** 当月投资金额 */
	
	public double total_invest_amount; /** 总投资金额 */
	
	public long user_id;				/** 用户表 id */

}
