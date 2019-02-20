package models.finance.entity;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 重点机构流动行缺口监测统计表
 * @createDate 2018.10.22
 * */
@Entity
public class t_internet_agency_monitor_statistic extends Model{

	/**(A编号)对辖内整改类机构进行分领域编号*/
	public String Anumber;
	
	/**B所属领域*/
	public String category;
	
	/**c机构名称*/
	public String agency_name;
	
	/**D来源*/
	public String source;
	
	/**E风险预判*/
	public String risk_prejudgement;
	
	/**F拟采取措施*/
	public String take_steps;
	
	/**G1资产段（应收回金额，亿元）*/
	public BigDecimal recover_amount;
	
	/**G2资金端（应兑付金额，亿元）*/
	public BigDecimal invest_amount;
	
	/**未来1个月预计收回金额（亿元）*/
	public BigDecimal one_month_estimate_recover_amount;
	
	/**未来1个月预计兑付金额（亿元）*/
	public BigDecimal one_month_estimate_invest_amount;
	
	/**未来1个月预计缺口（亿元）*/
	public BigDecimal one_month_estimate_gap;
	
	/**未来2个月预计收回金额（亿元）*/
	public BigDecimal two_month_estimate_recover_amount;
	
	/**未来2个月预计兑付金额（亿元）*/
	public BigDecimal two_month_estimate_invest_amount;
	
	/**未来2个月预计缺口（亿元）*/
	public BigDecimal two_month_estimate_gap;
	
	/**未来N个月预计收回金额（亿元）*/
	public BigDecimal n_estimate_recover_amount;
	
	/**未来N个月预计兑付金额（亿元）*/
	public BigDecimal n_estimate_invest_amount;
	
	/**未来N个月预计缺口（亿元）*/
	public BigDecimal n_estimate_gap;
	
	/**创建时间*/
	public Date time;
	
}
