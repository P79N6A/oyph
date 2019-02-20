package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 群发短信 sms临时表
 * 
 *
 * @author liudong
 * @createDate 2016年4月1日
 *
 */
@Entity
public class t_mass_sms_sending extends Model {
	/** 创建时间 */
	public Date time;
	
	/** mobile */
	public String mobile;
	
	/** 短信内容 */
	public String content;
	
	/** 是否已经发送 */
	public boolean is_send;
	
	/** 发送的次数 */
	public int try_times;
	
	/** 实际发送时间 */
	public Date send_time;

}
