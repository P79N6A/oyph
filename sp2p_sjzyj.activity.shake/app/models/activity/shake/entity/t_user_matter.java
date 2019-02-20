package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_user_matter extends Model{
	/** 用户id */
	public Long user_id;
	
	/** 奖品id */
	public Long award_id;
	
	/** 收货地址 */
	public String ship_address;
	
	/** 电话 */
	public String phone;
	
	/** 时间*/
	public Date time;
	
	
}