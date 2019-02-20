package models.common.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_user_vip_records extends Model{
	
	/**t_user表id*/
	public Long user_id;
	
	/**VIP等级表id*/
	public Long vip_grade_id;
	
	/**创建时间*/
	public Date time;

}
