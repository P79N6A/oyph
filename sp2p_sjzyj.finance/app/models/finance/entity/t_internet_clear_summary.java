package models.finance.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 清理整顿阶段工作进展情况汇总表
 * @createDate 2018.10.22
 * */
@Entity
public class t_internet_clear_summary extends Model{

	/**F4整改初始机构存量规模（亿元）*/
	public BigDecimal initial_scale;
	
	/**F5整改初始机构存量不合规业务规模（亿元）*/
	public BigDecimal initial_unqualified_scale;
	
	/**G1月末机构存量规模（亿元）*/
	public BigDecimal end_month_scale;
	
	/**G2当月存量规模变化（亿元）*/
	public BigDecimal current_month_scale_change;
	
	/**G3整改以来存量规模变化（亿元）*/
	public BigDecimal scale_change;
	
	/**H1月末机构存量不合规业务规模（亿元）*/
	public BigDecimal end_month_unqualified_scale;
	
	/**H2当月存量不合规业务变化（亿元）*/
	public BigDecimal current_month_unqualified_scale;
	
	/**整改以来存量不合规业务变化（亿元）*/
	public BigDecimal unqualified_scale_change;
	
	/**创建时间*/
	public Date time;
	
}
