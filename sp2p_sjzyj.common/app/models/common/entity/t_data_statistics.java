package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 用户交易记录表
 * 
 * @description
 *
 * @author ChenZhipeng
 * @createDate 2015年12月19日
 */

@Entity
public class t_data_statistics extends Model {

	/** 添加时间 */
	public Date time;
	
	/** 添加时间 */
	public String login_ip;
}
