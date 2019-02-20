package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 实体:系统(包括管理员)短信发送表
 *
 * @description 
 *
 * @author DaiZhengmiao
 * @createDate 2015年12月18日
 */
@Entity
public class t_sms extends Model {

	/** 创建时间 */
	public Date time;
	
	/** 管理员id，如果为0表示为系统自动发送的短信 */
	public Long supervisor_id;
	
	/** mobile */
	public String mobile;
	
	/** 短信内容 */
	public String content;
	
	/** 请求参数的json字符串 用途：获得API鉴权认证值 */
	public String authorization;
	
	/** 该模板短信最大发送条数 */
	public Integer maxCount;
}
