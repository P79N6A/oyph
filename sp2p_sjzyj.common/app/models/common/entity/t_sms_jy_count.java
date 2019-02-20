package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_sms_jy_count extends Model {

	/** 数据创建及修改时间 */
	public Date time;
	
	/** 手机号 */
	public String mobile;
	
	/** 特殊短息发送条数（最大值4条） */
	public Integer special_count;
	
	/** 短息发送总数（最大值10条） */
	public Integer total_count;
	
	
}
