package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:加息券表
 *
 * @description 
 *
 * @author LiuPengwei
 * @createDate 2017年7月24日
 */
@Entity
public class t_coupon extends Model{

	/** 创建时间 */
	public Date time;
	
	/** 加息券详细名称 */
	public String name;
	
	/** 加息券名称 */
	public String coupon;
	
	/** 红包唯一的标识 */
	public String _key;
	
	/** 加息年利率 */
	public Double amount;
	
	/**
	 * 加息券使用条件
	 */
	public double use_rule ;
	
	/**
	 * 加息券结束时间
	 */
	public Date limit_time ;
	
}
