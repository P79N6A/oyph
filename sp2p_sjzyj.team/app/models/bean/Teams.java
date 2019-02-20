package models.bean;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Teams {
	
	@Id
	public long id;
	public long supervisor_id;
	
	public Date time;
	
	public long parent_id;
	
	public double month_money;//当月理财金额
	
	public double total_money; //理财总金额
	
	public double month_commission; //当月佣金
	
	public double total_commission; //总佣金
	
	public int type; //类型(0,客户经理,1,业务主任,2业务部经理)
	
	
	
	

	
	
}
