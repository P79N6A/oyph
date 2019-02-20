package models.activity.shake.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_big_wheel extends Model{
	/** 活动名称 */
	public String name;
	/** 开始时间 */
	public Date start_time;
	/** 结束时间 */
	public Date end_time;
	/** 1、未开始 2、进行中 3、已结束 */
	public int status;
}
