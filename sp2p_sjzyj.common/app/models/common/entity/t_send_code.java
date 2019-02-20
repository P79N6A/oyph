package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/***
 * 手机短信发送记录
 * 
 * @description 
 *
 * @author ChenZhipeng
 * @createDate 2016年2月29日
 */
@Entity
public class t_send_code extends Model {

	/** 短信发送时间 */
	public Date time;
	
	/** 手机号码 */
	public String mobile;
	
	/** IP地址 */
	public String ip;
	
}
