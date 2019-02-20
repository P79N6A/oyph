package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 审核科目
 *
 * @author yaoyi
 * @createDate 2015年12月15日
 */
@Entity
public class t_audit_subject extends Model{
	
	/** 添加时间 */
	public Date time;
	
	/**	科目名称 */
	public String name;
	
	/** 要求 */
	public String description;
	
	public t_audit_subject(){
	}
	
	public t_audit_subject(String name, String description){
		this.time = new Date();
		this.name = name;
		this.description = description;
	}
	
	public t_audit_subject(Date time, String name, String description){
		this.time = time;
		this.name = name;
		this.description = description;
	}
	
}
