package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_profit
 *
 * @description P11 河北省P2P机构利润表
 *
 * @author LiuHangjing
 * @createDate 2018年10月5日-下午6:13:49
 */
@Entity
public class t_profit extends Model{

	/**营业收入_月 */
	public BigDecimal operating_receipt;
	
	/** 网贷中介服务收入_月*/
	public BigDecimal net_service_income;
	
	/** 其他业务收入_月*/
	public BigDecimal other_operating_income;
	
	/** 营业成本_月*/
	public BigDecimal operating_costs;
	
	/**税金及附加_月 */
	public BigDecimal operating_tax;
	
	/** 销售费用_月*/
	public BigDecimal sales_expense;
	
	/** 管理费用_月*/
	public BigDecimal overhead_expense;
	
	/** 研发费用_月*/
	public BigDecimal dev_expense;
	
	/**财务费用_月(收益以“-”号填列) */
	public BigDecimal financial_cost;
	
	/** 充值提现手续费_月*/
	public BigDecimal cash_handing_charges;
	
	/** 资产减值损失_月*/
	public BigDecimal assets_impairment_loss;
	
	/** 公允价值变动损益_月（损失以“-”号填列)*/
	public BigDecimal changes_val_profit;
	
	/** 投资收益_月（损失以“-”号填列)*/
	public BigDecimal investment_income;
	
	/** 营业利润_月(单位/万元)*/
	public BigDecimal operating_profit;
	
	/** 营业外收入_月*/
	public BigDecimal non_operating_income;
	
	/** 政府补贴收入_月*/
	public BigDecimal gov_subsidy_income;
	
	/** 营业外支出_月 */
	public BigDecimal non_operating_expense;
	
	/** 利润总额_月*/
	public BigDecimal total_profit;
	
	/** 减：所得税费用_月*/
	public BigDecimal income_tax_expense;
	
	/** 净利润_月*/
	public BigDecimal retained_profits;
	
	/** 营业收入_年*/
	public BigDecimal operating_receipt_year;
	
	/** 网贷中介服务收入_年*/
	public BigDecimal net_service_income_year;
	
	/** 其他业务收入_年 */
	public BigDecimal other_operating_income_year;
	
	/** 营业成本_年 */
	public BigDecimal operating_costs_year;
	
	/** 税金及附加_年*/
	public BigDecimal operating_tax_year;
	
	/** 销售费用_年*/
	public BigDecimal sales_expense_year;
	
	/** 管理费用_年*/
	public BigDecimal overhead_expense_year;
	
	/** 研发费用_年*/
	public BigDecimal dev_expense_year;
	
	/**财务费用_年(收益以“-”号填列) */
	public BigDecimal financial_cost_year;
	
	/** 充值提现手续费_年*/
	public BigDecimal cash_handing_charges_year;
	
	/** 资产减值损失_年 */
	public BigDecimal assets_impairment_loss_year;
	
	/** 公允价值变动损益_年（损失以“-”号填列) */
	public BigDecimal changes_val_profit_year;
	
	/** 投资收益_年（损失以“-”号填列)*/
	public BigDecimal investment_income_year;
	
	/** 营业利润_年(单位/万元)*/
	public BigDecimal operating_profit_year;
	
	/** 营业外收入_年*/
	public BigDecimal non_operating_income_year;
	
	/** 政府补贴收入_年 */
	public BigDecimal gov_subsidy_income_year;
	
	/** 营业外支出_年*/
	public BigDecimal non_operating_expense_year;
	
	/** 利润总额_年*/
	public BigDecimal total_profit_year;
	
	/** 减：所得税费用_年*/
	public BigDecimal income_tax_expense_year;
	
	/** 净利润_年*/
	public BigDecimal retained_profits_year;
	
	/** 创建时间 */
	public Date create_time;
	
	
}
