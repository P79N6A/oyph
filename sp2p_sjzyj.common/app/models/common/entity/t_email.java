package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:系统(包括管理员)邮件发送表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
@Entity
public class t_email extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 管理员id，如果为0表示为系统自动发送的邮件 */
	public Long supervisor_id;
	
	/** Email地址 */
	public String email;
	
	/** 邮件的标题 */
	public String title;
	
	/** 邮件内容 */
	public String content;
	
}
