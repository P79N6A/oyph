package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:红包表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月14日
 */
@Entity
public class t_red_packet extends Model{

	/** 创建时间 */
	public Date time;
	
	/** 红包名称 */
	public String name;
	
	/** 红包唯一的标识 */
	public String _key;
	
	/** 金额 */
	public Double amount;
	
	/**
	 * 红包使用条件
	 */
	public double use_rule ;
	
	/**
	 * 红包有效天数
	 */
	public int limit_day ;
	
}
