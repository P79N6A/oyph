package models.ext.wealthcircle.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:CPS设置
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2016年4月6日
 */
@Entity
public class t_wealthcircle_setting extends Model {
	
	/** 键 */
	public String _key;
	
	/** 值 */
	public String _value;
	
	/** 描述 */
	public String description;
	
}
