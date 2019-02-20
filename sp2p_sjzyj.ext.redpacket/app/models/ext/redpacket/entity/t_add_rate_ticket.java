package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体：加息券
 * 
 * @author niu
 * @create 2017.10.27
 */
@Entity
public class t_add_rate_ticket extends Model {

	/** 利率  */
	public double apr;
	
	/** 有效期  */
	public int day;
	
	/** 获取条件：大于  */
	public double large;
	
	/** 获取条件：小于  */
	public double small;
	
	/** 使用条件：大于  */
	public double amount;
	
	/** 活动ID  */
	public long act_id;
	
	/** 活动类型：摇一摇活动  */
	public int type;
	
	public Date lock_time;
	/**锁定时间（天）*/
	public Integer lock_day;
	
	
}
