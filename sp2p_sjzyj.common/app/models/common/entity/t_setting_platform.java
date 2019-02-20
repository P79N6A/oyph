package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:平台参数设置
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月14日
 */
@Entity
public class t_setting_platform extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 系统参数键名 */
	public String _key;
	
	/** 系统参数值 */
	public String _value;
	
	/** 系统参数描述 */
	public String description;
	
	/** 备注信息 */
	public String remark;

	@Override
	public String toString() {
		return "t_setting_platform [time=" + time + ", _key=" + _key
				+ ", _value=" + _value + ", description=" + description
				+ ", remark=" + remark + ", id=" + id + "]";
	}
	
}
