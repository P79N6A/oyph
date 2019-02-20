package models.common.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class CostBean {
	
	@Id
	@GeneratedValue
	public long id;

	/* 收入项目 */
	public String incomeAccount;
	
	/* 摘要 */
	public String remark;
	
	/* 金额 */
	public String money;
	
	/* 时间 */
	public String time;
}
