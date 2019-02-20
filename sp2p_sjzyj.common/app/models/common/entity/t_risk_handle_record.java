package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_risk_handle_record extends Model{
	
	/**操作者*/
	public String risk_handler;
	
	/**操作时间*/
	public Date handle_time;
	
	/**操作内容*/
	public String handle_content;
	
	/**风控报告的id*/
	public long risk_id;
	
	/**操作者用户名*/
	public String superName;
}
