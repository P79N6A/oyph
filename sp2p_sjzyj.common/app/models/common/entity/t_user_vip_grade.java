package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 客户VIP等级表
 * @createDate 2018.11.5
 * */
@Entity
public class t_user_vip_grade extends Model {

	/**客户等级*/
	public Integer grade;
	
	/**本金待收下限（万元）*/
	public Integer min_amount;
	
	/**本金待收下限（万元）*/
	public Integer max_amount;
	
	/**赠送积分的倍数*/
	public Double give_integral;
	
	/**积分商城消费折扣*/
	public Double use_discount_integral;
	
	/**等级头像*/
	public String head_pic;
	
	/**等级说明*/
	public String des;
	
	/**赠送生日礼物id t_mall_goods表id*/
	public Long birthday_gift_id; 
	
}
