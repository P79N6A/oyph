package models.core.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_invest_entrust extends Model{
	
	public String entrust_no ;
	
	public long user_id;
}
