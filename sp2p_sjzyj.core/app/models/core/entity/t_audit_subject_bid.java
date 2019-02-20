package models.core.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_audit_subject_bid extends Model{
	
	/** 创建时间 */
	public Date time = new Date();
	
	/** 标的ID */
	public long bid_id;
	
	/** 科目名称 */
	public String name;
	
	/** 要求 */
	public String description;
	
	
	public t_audit_subject_bid(){}

	public t_audit_subject_bid(long bid_id, String name, String description) {
		super();
		this.bid_id = bid_id;
		this.name = name;
		this.description = description;
	}
	
}
