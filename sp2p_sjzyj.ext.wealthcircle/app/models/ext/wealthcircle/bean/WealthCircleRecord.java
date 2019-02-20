package models.ext.wealthcircle.bean;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 财富圈邀请记录
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月8日
 */
@Entity
public class WealthCircleRecord implements Serializable  {

	
	@Id
	public Long id;
	
	/** 创建时间 */
	public Date time;
	
	/** 邀请码 */
	public String wc_code;
	
	/** 用户id(被邀请人) */
	public Long user_id;
	
	/** 用户name(被邀请人) */
	public String user_name;
	
	/** 推广人/邀请人id(邀请码持有人) */
	public Long spreader_id;
	
	/** 推广人/邀请人name(邀请码持有人) */
	public String spreader_name;
	
	/** 邀请码激活时间(被使用时间) */
	public Date active_time;
	
	/** 被邀请人累计理财 */
	public double total_invest;
	
	/** 给邀请人带来累计返佣 */
	public double total_rebate;
	
	/** 可领取返佣 */
	public double recivable_rebate;
	
}
