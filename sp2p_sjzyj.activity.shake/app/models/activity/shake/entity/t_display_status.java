package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_display_status extends Model{
	
	/** 显示状态 */
	public int display_status;
	
	/** 时间*/
	public Date create_time;
	
	
}
