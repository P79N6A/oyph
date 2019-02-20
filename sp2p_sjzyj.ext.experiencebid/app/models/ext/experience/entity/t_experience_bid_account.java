package models.ext.experience.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_experience_bid_account extends Model{
	
	/** 创建时间 */
	public Date time;
	
	/** 关联用户ID */
	public long user_id;
	
	/** 体验金金额 */
	public double amount;
	
	/** 体验金收益 */
	public double balance;
	
	/** 冻结金额 */
	public double freeze;
	
	/** 上次领取时间 */
	public Date last_receive_time;
	
	/** 可领取体验金次数 */
	public int count;
	
	/** 体验金每次发放多少 */
	public double every_grant;
	
}
