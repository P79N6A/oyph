package models.finance.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 重点对象（整改类）整改落实进展监测明细表
 * @createDate 2018.10.22
 * */
@Entity
public class t_internet_change_monitor extends Model{

	/**A编号*/
	public String number;
	
	/**B所属领域*/
	public String category;
	
	/**C机构名称*/
	public String agency_name;
	
	/**D平台网址（或者APP名称）*/
	public String name;
	
	/**E来源*/
	public String source;
	
	/**F1工作进展*/
	public String work_progress;
	
	/**F2存量不合规业务计划退出时间*/
	public String out_time;
	
	/**F3负责监督整改的部门*/
	public String super_department;
	
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
	
	/**H3整改以来存量不合规业务规模变化（亿元）*/
	public BigDecimal unqualified_scale_change;
	
	/**I是否新增不合规业务*/
	public String is_new_unqualified_service;
	
	/**J机构经营状态1---正在经营*/
	public Integer manage_status;
	
	/**K整改状态1正在整改*/
	public Integer change_status;
	
	/**L风险程度------1高；2中；3低；*/
	public String risk_degree;
	
	/**N风险主要表现*/
	public String risk_express;
	
	/**创建时间*/
	public Date time;
	
}
