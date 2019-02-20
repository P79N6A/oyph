package models.ext.redpacket.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_market_activity extends Model{

	
	/** 创建时间 */
	public Date time;
	
	/** 营销类型 1:红包 2：加息券 */
	public int activity_type;
	
	/** 活动开始时间*/
	public Date start_time;
	
	/** 活动结束时间 */
	public Date end_time;
	
	
}
