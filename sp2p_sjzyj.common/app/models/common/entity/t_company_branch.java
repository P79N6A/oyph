package models.common.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_company_branch extends Model{
	
	/**分公司名称*/
	public String branch_name;
	
	/** 区划代码 */
	public int row_code;
	
}
