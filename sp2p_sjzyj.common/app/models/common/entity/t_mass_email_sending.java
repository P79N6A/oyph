package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 群发邮件 email临时表
 *
 * @description 在进行主业务时会生成一个这张表的记录，然后定时器会根据<br>
 * 			本表的记录发送邮件给用户，然后生成t_mass_email记录表。并在某个时间段，删除本表的记录
 *
 * @author liudong
 * @createDate 2016年4月5日
 *
 */
@Entity
public class t_mass_email_sending extends Model {
	
	/** 创建时间 */
	public Date time;
	
	/** Email地址 */
	public String email;
	
	/** 邮件的标题 */
	public String title;
	
	/** 邮件内容 */
	public String content;
	
	/** 是否已经发送 */
	public boolean is_send;
	
	/** 发送的次数 */
	public int try_times;
	
	/** 实际发送时间 */
	public Date send_time;
}
