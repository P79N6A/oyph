package models.ext.cps.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_three_elements_setting extends Model{

	/**标题*/
	public String title;
	
	/**描述*/
	public String describe1;
	
	/**关键词*/
	public String keyword;
	
	/**三元素唯一标识符*/
	public String _key;
	
}
