package models.ext.experience.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_experience_bid_setting extends Model{

	/** 键 */
	public String _key;
	
	/** 值 */
	public String _value;
	
	/** 描述 */
	public String description;
}
