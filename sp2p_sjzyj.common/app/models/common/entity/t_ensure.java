package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 担保信息表
 *
 * @author liuyang
 * @createDate 2017年6月27日
 */
@Entity
public class t_ensure extends Model {

	/** 用户id */
	public long user_id;
	
	/** 业务订单号 */
	public String service_order_no;
	
	/** 担保金额 */
	public double amount;
	
	/** 担保状态 */
	public int status;
}
