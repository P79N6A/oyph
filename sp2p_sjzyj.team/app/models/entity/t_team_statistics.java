package models.entity;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class t_team_statistics extends Model{

	public long supervisor_id;
	
    public int year;
    
    public int month;
	
	public double month_money;//当月理财金额
	
	public double total_money; //理财总金额
	
	public double month_commission; //当月佣金
	
	public int type; //类型(0,客户经理,1,业务主任,2业务部经理)
	
	public double year_convert;//当月年化折算
	
	public Date time;//添加时间
	
	public double actual_month_commission; //当月实际发放佣金
	
	
}
