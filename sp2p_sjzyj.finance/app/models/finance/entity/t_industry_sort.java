package models.finance.entity;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_industry_sort extends Model {

	/**小类代码 代码长度四位数字 不足四位数字在前面补0 Eg: 0001*/
	public String code;
	
	/** 行业分类名称 */
	public String code_name;
	
	/** 行业分类说明 */
	public String code_desc;
	
}
