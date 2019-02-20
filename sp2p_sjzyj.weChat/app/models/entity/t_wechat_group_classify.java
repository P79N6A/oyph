package models.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_wechat_group_classify extends Model {

	public String title;
	
	public int type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
