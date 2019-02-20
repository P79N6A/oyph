package models.ext.wealthcircle.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:财富圈邀请记录
 *
 * @description 用户的财富圈邀请码、关系建立、返佣等由此实体确定
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月6日
 */
@Entity
public class t_wealthcircle_user extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 邀请码 */
	public String wc_code;
	
	/** 用户id(被邀请人) */
	public Long user_id;
	
	/** 推广人/邀请人id(邀请码持有人) */
	public Long spreader_id;
	
	/** 邀请码激活时间(被使用时间) */
	public Date active_time;
	
	/** 0:未使用，1:已使用 */
	public boolean status;
	
	/** 被邀请人累计理财 */
	public double total_invest;
	
	/** 给邀请人带来累计返佣 */
	public double total_rebate;
	
	/** 可领取返佣 */
	public double recivable_rebate;
	
}
