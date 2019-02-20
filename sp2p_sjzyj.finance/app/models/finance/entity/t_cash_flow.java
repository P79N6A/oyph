package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_cash_flow
 *
 * @description P12 河北省P2P机构现金流量表
 *
 * @author LiuHangjing
 * @createDate 2018年10月5日-下午6:29:38
 */
@Entity
public class t_cash_flow extends Model{
	
	/** 提供平台服务收到的资金增加额 */
	public BigDecimal platform_service_fee;
	
	/** 收取会员充值提现手续费的资金 */
	public BigDecimal withdraw_fee;
	
	/** 收取平台合作机构保证金的资金 */
	public BigDecimal cash_deposit;
	
	/** 收到其他与经营活动有关的现金 */
	public BigDecimal other_activities;
	
	/** 经营活动现金流入小计 */
	public BigDecimal total_cash_inflow;

	/** 支付给职工以及为职工支付的现金 */
	public BigDecimal staff_pay;
	
	/** 支付的各项税费 */
	public BigDecimal tax_payments;
	
	/** 支付业务保证金 */
	public BigDecimal withdraw_payment;
	
	/** 支付其他与经营活动有关的现金 */
	public BigDecimal other_payment;
	
	/** 经营活动现金流出小计 */
	public BigDecimal total_cash_outflow;
	
	/** 经营活动产生的现金流量净额 */
	public BigDecimal net_cash_flow;
	
	/** 收回投资收到的现金 */
	public BigDecimal recouping_capital;
	
	/** 取得投资收益收到的现金 */
	public BigDecimal income_invest;
	
	/** 处置固定资产、无形资产和其他长期资产收回的现金净额 */
	public BigDecimal net_cash;
	
	/** 收到其他与投资活动有关的现金 */
	public BigDecimal other_invest_cash;
	
	/** 投资活动现金流入小计 */
	public BigDecimal total_invest_inflow;
	
	/** 购建固定资产、无形资产和其他长期资产支付的现金 */
	public BigDecimal long_term_assets_payment;
	
	/** 投资支付的现金 */
	public BigDecimal invest_payment;
	
	/** 支付其他与投资活动有关的现金 */
	public BigDecimal other_invest_cash_payment;
	
	/** 投资活动现金流出小计 */
	public BigDecimal total_invest_outflow;

	/** 投资活动产生的现金流量净额 */
	public BigDecimal net_invest_flow;
	
	/** 吸收投资收到的现金 */
	public BigDecimal receipts_equity_securties;
	
	/** 取得借款收到的现金 */
	public BigDecimal receipts_loan;
	
	/** 发行债券收到的现金 */
	public BigDecimal float_bonds;
	
	/** 收到其他与筹资活动有关的现金 */
	public BigDecimal receive_other_finance;
	
	/** 筹资活动现金流入小计 */
	public BigDecimal total_finance_inflow;
	
	/** 偿还债务支付的现金 */
	public BigDecimal dedt_payment;
	
	/** 分配股利、利润或偿还利息支付的现金 */
	public BigDecimal interest_payment;
	
	/** 支付其他与筹资活动有关的现金*/
	public BigDecimal pay_other_finance;
	
	/** 筹资活动现金流出小计 */
	public BigDecimal total_finance_outflow;
	
	/** 筹资活动产生的现金流量净额 */
	public BigDecimal net_finance_flow;
	
	/** 汇率变动对现金的影响额 */
	public BigDecimal exchange_rate_flu;
	
	/** 现金及现金等价物净值加额 */
	public BigDecimal cash_equ_add;
	
	/** 期初现金及现金等价物余额 */
	public BigDecimal init_cash_equ_balance;

	/** 期末现金及现金等价物余额 */
	public BigDecimal end_cash_equ_balance;
	
	/** 创建时间*/
	public Date create_time;
}	

