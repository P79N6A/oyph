package models.proxy.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 业务员提成规则表
 * @author Niu Dongfeng
 */
@Entity
public class t_proxy_salesman_profit_rule extends Model {
	
	public long proxy_id;		/** 代理商id */

	public Date time;			/** 添加时间  */
	
	public String _key;			/** key  */
	
	public String _value;		/** value  */
	
	public String description;			/** 描述  */
	
}
