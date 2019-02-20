package models.ext.wealthcircle.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:用户财富圈账户
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月6日
 */
@Entity
public class t_wealthcircle_account extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 用户id */
	public Long user_id;
	
	/** 财富圈账户余额 */
	public double balance;
	
	/** 已经生成邀请码的理财金额 */
	public double has_convert_amount;
}
