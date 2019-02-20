package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_current_assets
 *
 * @description P10 河北省P2P机构资产负债-资产类-流动资产表
 *
 * @author LiuHangjing
 * @createDate 2018年10月5日-下午5:11:52
 */
@Entity
public class t_current_assets extends Model{

	/** 流动资产期初数总和*/
	public BigDecimal initial_sum;
	
	/** 现金 */
	public BigDecimal cash_assets;
	
	/** 银行存款*/
	public BigDecimal cash_in_bank;

	/** 交易性金融资产*/
	public BigDecimal trading_financial_assets;
	
	/** 存出保证金*/
	public BigDecimal refundable_deposits;

	/** 应收票据*/
	public BigDecimal bill_receivable;

	/** 应收账款*/
	public BigDecimal receivables;

	/** 预付账款*/
	public BigDecimal prepayment;
	
	/** 应收利息*/
	public BigDecimal interest_receivable;
	
	/** 应收股利*/
	public BigDecimal dividends_receivable;
	
	/** 应收结算款 */
	public BigDecimal settlement_receivables;
	
	/** 其他应收款(*/
	public BigDecimal other_receivables;
	
	/** 存货*/
	public BigDecimal stock;
	
	/** 一年内到期的非流动资产*/
	public BigDecimal non_current_assets;
	
	/** 其他流动资产*/
	public BigDecimal other_current_assets;
	
	/** 非流动资产总计 -初 */
	public BigDecimal non_initial_sum;
	
	/** 可供出售金融资产 */
	public BigDecimal sales_financial_assets;
	
	/** 持有至到期投资 */
	public BigDecimal held_maturity_invest;
	
	/** 长期应收款 */
	public BigDecimal long_term_receivables;
	
	/** 长期股权投资 */
	public BigDecimal long_trem_equity_invest;
	
	/** 投资性房地产 */
	public BigDecimal invest_pro;
	
	/** 固定资产原值 */
	public BigDecimal immobilisations;
	
	/** 固定资产净值 */
	public BigDecimal fixed_assets_net_value;
	
	/** 固定资产清理 */
	public BigDecimal disposal_fixed_assets;
	
	/** 无形资产 */
	public BigDecimal intangible_assets;
	
	/** 开发支出 */
	public BigDecimal dev_exp;
	
	/** 商誉 */
	public BigDecimal good_will;
	
	/** 长期待摊费用 */
	public BigDecimal deferred_assets;
	
	/** 递延所得税资产 */
	public BigDecimal deferred_tax_assets;
	
	/**  其他非流动资产*/
	public BigDecimal other_non_current_assets;
	
	/** 资产类总计期初数*/
	public BigDecimal initial_assets_total;
	
	/** 流动资产期末数总和*/
	public BigDecimal ending_sum;
	
	/** 现金_期末数*/
	public BigDecimal cash_assets_end;
	
	/** 银行存款-期末数*/
	public BigDecimal cash_in_bank_end;
	
	/** 交易性金融资产-期末数*/
	public BigDecimal trading_financial_assets_end;
	
	/** 存出保证金-期末数*/
	public BigDecimal refundable_deposits_end;
	
	/** 应收票据-期末数*/
	public BigDecimal bill_receivable_end;
	
	/** 应收账款-期末数*/
	public BigDecimal receivables_end;
	
	/** 预付账款-期末数*/
	public BigDecimal prepayment_end;
	
	/** 应收利息-期末数*/
	public BigDecimal interest_receivable_end;
	
	/** 应收股利-期末数*/
	public BigDecimal dividends_receivable_end;
	
	/** 应收结算款-期末数*/
	public BigDecimal settlement_receivables_end;
	
	/** 其他应收款-期末数*/
	public BigDecimal other_receivables_end;
	
	/** 存货-期末数*/
	public BigDecimal stock_end;
	
	/** 一年内到期的非流动资产-期末数*/
	public BigDecimal non_current_assets_end;
	
	/** 其他流动资产-期末数*/
	public BigDecimal other_current_assets_end;
	
	/** 非流动资产小计 -末*/
	public BigDecimal non_ending_sum;
	
	/** 可供出售金融资产-期末数 */
	public BigDecimal sales_financial_assets_end;
	
	/** 持有至到期投资-期末数 */
	public BigDecimal held_maturity_invest_end;
	
	/** 长期应收款-期末数 */
	public BigDecimal long_term_receivables_end;
	
	/** 长期股权投资_末 */
	public BigDecimal long_trem_equity_invest_end;
	
	/** 投资性房地产_末 */
	public BigDecimal invest_pro_end;
	
	/** 固定资产原值_末 */
	public BigDecimal immobilisations_end;
	
	/** 固定资产净值_末 */
	public BigDecimal fixed_assets_net_value_end;
	
	/** 固定资产清理_末 */
	public BigDecimal disposal_fixed_assets_end;
	
	/** 无形资产_末 */
	public BigDecimal intangible_assets_end;
	
	/** 开发支出_末 */
	public BigDecimal dev_exp_end;
	
	/** 商誉_末 */
	public BigDecimal good_will_end;
	
	/** 长期待摊费用_末 */
	public BigDecimal deferred_assets_end;
	
	/** 递延所得税资产_末 */
	public BigDecimal deferred_tax_assets_end;
	
	/**  其他非流动资产_末*/
	public BigDecimal other_non_current_assets_end;
	
	/** 期末数资产类总计*/
	public BigDecimal ending_assets_total;
	
	/** 负债类总计-期初数 */
	public BigDecimal total_lias;
	
	/** 流动负债总计-期初数*/
	public BigDecimal total_current_lia;
	
	/** 短期借款 */
	public BigDecimal short_trem_borrow;
	
	/** 交易性金融负债 */
	public BigDecimal trading_fin_lia;
	
	/** 存入保证金 */
	public BigDecimal deposits_received;
	
	/** 应付票据 */
	public BigDecimal notes_payable;
	
	/** 应付及预收账款 */
	public BigDecimal accounts_pay_received;
	
	/** 应付股利 */
	public BigDecimal dividends_pay;
	
	/** 应付利息 */
	public BigDecimal interest_pay;
	
	/** 应付职工薪酬 */
	public BigDecimal employee_pay;
	
	/** 应交税金 */
	public BigDecimal tax_payment;
	
	/** 代收担保机构款项 */
	public BigDecimal collection_guarantee_agency;
	
	/** 其他应付款 */
	public BigDecimal other_pays;
	
	/** 一年内到期的非流动负债 */
	public BigDecimal non_current_lia;
	
	/** 其他流动负债 */
	public BigDecimal other_current_lia;
	
	/** 非流动负债总计-期初数 */
	public BigDecimal total_non_current_lia;
	
	/** 长期借款 */
	public BigDecimal long_term_loan;
	
	/** 应付债券 */
	public BigDecimal bonds_payable;

	/** 长期应付款 */
	public BigDecimal long_term_payable;
	
	/** 预计负债 */
	public BigDecimal accrued_lia;
	
	/** 递延所得税负债 */
	public BigDecimal deferred_tax_lia;
	
	/** 其他长期负债( */
	public BigDecimal other_long_term_debt;
	
	/** 所有者权益总计 */
	public BigDecimal total_owners_equity;
	
	/** 实收资本 */
	public BigDecimal pacil_up_capital;
	
	/** 资本公积*/
	public BigDecimal capital_reserve;
	
	/** 盈余公积*/
	public BigDecimal surplues_reserves;
	
	/** 其他综合收益*/
	public BigDecimal other_income;
	
	/** 未分配利润 */
	public BigDecimal undistributed_profit;
	
	/** 归属于母公司的所有者权益 */
	public BigDecimal owenership_equity;
	
	/** 少数股东权益 */
	public BigDecimal minority_equity;
	
	/** 负债和所有者权益(或股东权益)总计-初 */
	public BigDecimal total_liabilities_equity;
	
	/** 负债类总计_末 */
	public BigDecimal total_lias_end;
	
	/** 流动负债总计_末 */
	public BigDecimal total_current_lia_end;
	
	/** 短期借款_末 */
	public BigDecimal short_trem_borrow_end;
	
	/** 交易性金融负债_末 */
	public BigDecimal trading_fin_lia_end;
	
	/** 存入保证金_末 */
	public BigDecimal deposits_received_end;
	
	/** 应付票据_末 */
	public BigDecimal notes_payable_end;
	
	/** 应付及预收账款_末 */
	public BigDecimal accounts_pay_received_end;
	
	/** 应付股利_末 */
	public BigDecimal dividends_pay_end;
	
	/** 应付利息_末 */
	public BigDecimal interest_pay_end;
	
	/** 应付职工薪酬_末 */
	public BigDecimal employee_pay_end;
	
	/** 应交税金_末 */
	public BigDecimal tax_payment_end;
	
	/** 代收担保机构款项_末 */
	public BigDecimal collection_guarantee_agency_end;
	
	/** 其他应付款_末 */
	public BigDecimal other_pays_end;
	
	/** 一年内到期的非流动负债_末 */
	public BigDecimal non_current_lia_end;
	
	/** 其他流动负债_末 */
	public BigDecimal other_current_lia_end;
	
	/** 非流动负债小计_末 */
	public BigDecimal total_non_current_lia_end;
	
	/** 长期借款_末 */
	public BigDecimal long_term_loan_end;
	
	/** 应付债券_末 */
	public BigDecimal bonds_payable_end;
	
	/** 长期应付款_末 */
	public BigDecimal long_term_payable_end;
	
	/** 预计负债_末 */
	public BigDecimal accrued_lia_end;
	
	/** 递延所得税负债_末 */
	public BigDecimal deferred_tax_lia_end;
	
	/** 其他长期负债_末 */
	public BigDecimal other_long_term_debt_end;
	
	/** 所有者权益总计_末 */
	public BigDecimal total_owners_equity_end;
	
	/** 实收资本_末*/
	public BigDecimal pacil_up_capital_end;
	
	/** 资本公积_末*/
	public BigDecimal capital_reserve_end;
	
	/** 盈余公积_末*/
	public BigDecimal surplues_reserves_end;
	
	/** 其他综合收益_末 */
	public BigDecimal other_income_end;
	
	/** 未分配利润_末 */
	public BigDecimal undistributed_profit_end;
	
	/** 归属于母公司的所有者权益_末 */
	public BigDecimal owenership_equity_end;
	
	/** 少数股东权益_末  */
	public BigDecimal minority_equity_end;
	
	/** 负债和所有者权益(或股东权益)总计_末 */
	public BigDecimal total_liabilities_equity_end;
	
	/** 创建时间*/
	public Date create_time;

	public BigDecimal getInitial_sum() {
		return initial_sum;
	}

	public void setInitial_sum(BigDecimal initial_sum) {
		this.initial_sum = initial_sum;
	}

	public BigDecimal getCash_assets() {
		return cash_assets;
	}

	public void setCash_assets(BigDecimal cash_assets) {
		this.cash_assets = cash_assets;
	}

	public BigDecimal getCash_in_bank() {
		return cash_in_bank;
	}

	public void setCash_in_bank(BigDecimal cash_in_bank) {
		this.cash_in_bank = cash_in_bank;
	}

	public BigDecimal getTrading_financial_assets() {
		return trading_financial_assets;
	}

	public void setTrading_financial_assets(BigDecimal trading_financial_assets) {
		this.trading_financial_assets = trading_financial_assets;
	}

	public BigDecimal getRefundable_deposits() {
		return refundable_deposits;
	}

	public void setRefundable_deposits(BigDecimal refundable_deposits) {
		this.refundable_deposits = refundable_deposits;
	}

	public BigDecimal getBill_receivable() {
		return bill_receivable;
	}

	public void setBill_receivable(BigDecimal bill_receivable) {
		this.bill_receivable = bill_receivable;
	}

	public BigDecimal getReceivables() {
		return receivables;
	}

	public void setReceivables(BigDecimal receivables) {
		this.receivables = receivables;
	}

	public BigDecimal getPrepayment() {
		return prepayment;
	}

	public void setPrepayment(BigDecimal prepayment) {
		this.prepayment = prepayment;
	}

	public BigDecimal getInterest_receivable() {
		return interest_receivable;
	}

	public void setInterest_receivable(BigDecimal interest_receivable) {
		this.interest_receivable = interest_receivable;
	}

	public BigDecimal getDividends_receivable() {
		return dividends_receivable;
	}

	public void setDividends_receivable(BigDecimal dividends_receivable) {
		this.dividends_receivable = dividends_receivable;
	}

	public BigDecimal getSettlement_receivables() {
		return settlement_receivables;
	}

	public void setSettlement_receivables(BigDecimal settlement_receivables) {
		this.settlement_receivables = settlement_receivables;
	}

	public BigDecimal getOther_receivables() {
		return other_receivables;
	}

	public void setOther_receivables(BigDecimal other_receivables) {
		this.other_receivables = other_receivables;
	}

	public BigDecimal getStock() {
		return stock;
	}

	public void setStock(BigDecimal stock) {
		this.stock = stock;
	}

	public BigDecimal getNon_current_assets() {
		return non_current_assets;
	}

	public void setNon_current_assets(BigDecimal non_current_assets) {
		this.non_current_assets = non_current_assets;
	}

	public BigDecimal getOther_current_assets() {
		return other_current_assets;
	}

	public void setOther_current_assets(BigDecimal other_current_assets) {
		this.other_current_assets = other_current_assets;
	}

	public BigDecimal getNon_initial_sum() {
		return non_initial_sum;
	}

	public void setNon_initial_sum(BigDecimal non_initial_sum) {
		this.non_initial_sum = non_initial_sum;
	}

	public BigDecimal getSales_financial_assets() {
		return sales_financial_assets;
	}

	public void setSales_financial_assets(BigDecimal sales_financial_assets) {
		this.sales_financial_assets = sales_financial_assets;
	}

	public BigDecimal getHeld_maturity_invest() {
		return held_maturity_invest;
	}

	public void setHeld_maturity_invest(BigDecimal held_maturity_invest) {
		this.held_maturity_invest = held_maturity_invest;
	}

	public BigDecimal getLong_term_receivables() {
		return long_term_receivables;
	}

	public void setLong_term_receivables(BigDecimal long_term_receivables) {
		this.long_term_receivables = long_term_receivables;
	}

	public BigDecimal getLong_trem_equity_invest() {
		return long_trem_equity_invest;
	}

	public void setLong_trem_equity_invest(BigDecimal long_trem_equity_invest) {
		this.long_trem_equity_invest = long_trem_equity_invest;
	}

	public BigDecimal getInvest_pro() {
		return invest_pro;
	}

	public void setInvest_pro(BigDecimal invest_pro) {
		this.invest_pro = invest_pro;
	}

	public BigDecimal getImmobilisations() {
		return immobilisations;
	}

	public void setImmobilisations(BigDecimal immobilisations) {
		this.immobilisations = immobilisations;
	}

	public BigDecimal getFixed_assets_net_value() {
		return fixed_assets_net_value;
	}

	public void setFixed_assets_net_value(BigDecimal fixed_assets_net_value) {
		this.fixed_assets_net_value = fixed_assets_net_value;
	}

	public BigDecimal getDisposal_fixed_assets() {
		return disposal_fixed_assets;
	}

	public void setDisposal_fixed_assets(BigDecimal disposal_fixed_assets) {
		this.disposal_fixed_assets = disposal_fixed_assets;
	}

	public BigDecimal getIntangible_assets() {
		return intangible_assets;
	}

	public void setIntangible_assets(BigDecimal intangible_assets) {
		this.intangible_assets = intangible_assets;
	}

	public BigDecimal getDev_exp() {
		return dev_exp;
	}

	public void setDev_exp(BigDecimal dev_exp) {
		this.dev_exp = dev_exp;
	}

	public BigDecimal getGood_will() {
		return good_will;
	}

	public void setGood_will(BigDecimal good_will) {
		this.good_will = good_will;
	}

	public BigDecimal getDeferred_assets() {
		return deferred_assets;
	}

	public void setDeferred_assets(BigDecimal deferred_assets) {
		this.deferred_assets = deferred_assets;
	}

	public BigDecimal getDeferred_tax_assets() {
		return deferred_tax_assets;
	}

	public void setDeferred_tax_assets(BigDecimal deferred_tax_assets) {
		this.deferred_tax_assets = deferred_tax_assets;
	}

	public BigDecimal getOther_non_current_assets() {
		return other_non_current_assets;
	}

	public void setOther_non_current_assets(BigDecimal other_non_current_assets) {
		this.other_non_current_assets = other_non_current_assets;
	}

	public BigDecimal getInitial_assets_total() {
		return initial_assets_total;
	}

	public void setInitial_assets_total(BigDecimal initial_assets_total) {
		this.initial_assets_total = initial_assets_total;
	}

	public BigDecimal getEnding_sum() {
		return ending_sum;
	}

	public void setEnding_sum(BigDecimal ending_sum) {
		this.ending_sum = ending_sum;
	}

	public BigDecimal getCash_assets_end() {
		return cash_assets_end;
	}

	public void setCash_assets_end(BigDecimal cash_assets_end) {
		this.cash_assets_end = cash_assets_end;
	}

	public BigDecimal getCash_in_bank_end() {
		return cash_in_bank_end;
	}

	public void setCash_in_bank_end(BigDecimal cash_in_bank_end) {
		this.cash_in_bank_end = cash_in_bank_end;
	}

	public BigDecimal getTrading_financial_assets_end() {
		return trading_financial_assets_end;
	}

	public void setTrading_financial_assets_end(BigDecimal trading_financial_assets_end) {
		this.trading_financial_assets_end = trading_financial_assets_end;
	}

	public BigDecimal getRefundable_deposits_end() {
		return refundable_deposits_end;
	}

	public void setRefundable_deposits_end(BigDecimal refundable_deposits_end) {
		this.refundable_deposits_end = refundable_deposits_end;
	}

	public BigDecimal getBill_receivable_end() {
		return bill_receivable_end;
	}

	public void setBill_receivable_end(BigDecimal bill_receivable_end) {
		this.bill_receivable_end = bill_receivable_end;
	}

	public BigDecimal getReceivables_end() {
		return receivables_end;
	}

	public void setReceivables_end(BigDecimal receivables_end) {
		this.receivables_end = receivables_end;
	}

	public BigDecimal getPrepayment_end() {
		return prepayment_end;
	}

	public void setPrepayment_end(BigDecimal prepayment_end) {
		this.prepayment_end = prepayment_end;
	}

	public BigDecimal getInterest_receivable_end() {
		return interest_receivable_end;
	}

	public void setInterest_receivable_end(BigDecimal interest_receivable_end) {
		this.interest_receivable_end = interest_receivable_end;
	}

	public BigDecimal getDividends_receivable_end() {
		return dividends_receivable_end;
	}

	public void setDividends_receivable_end(BigDecimal dividends_receivable_end) {
		this.dividends_receivable_end = dividends_receivable_end;
	}

	public BigDecimal getSettlement_receivables_end() {
		return settlement_receivables_end;
	}

	public void setSettlement_receivables_end(BigDecimal settlement_receivables_end) {
		this.settlement_receivables_end = settlement_receivables_end;
	}

	public BigDecimal getOther_receivables_end() {
		return other_receivables_end;
	}

	public void setOther_receivables_end(BigDecimal other_receivables_end) {
		this.other_receivables_end = other_receivables_end;
	}

	public BigDecimal getStock_end() {
		return stock_end;
	}

	public void setStock_end(BigDecimal stock_end) {
		this.stock_end = stock_end;
	}

	public BigDecimal getNon_current_assets_end() {
		return non_current_assets_end;
	}

	public void setNon_current_assets_end(BigDecimal non_current_assets_end) {
		this.non_current_assets_end = non_current_assets_end;
	}

	public BigDecimal getOther_current_assets_end() {
		return other_current_assets_end;
	}

	public void setOther_current_assets_end(BigDecimal other_current_assets_end) {
		this.other_current_assets_end = other_current_assets_end;
	}

	public BigDecimal getNon_ending_sum() {
		return non_ending_sum;
	}

	public void setNon_ending_sum(BigDecimal non_ending_sum) {
		this.non_ending_sum = non_ending_sum;
	}

	public BigDecimal getSales_financial_assets_end() {
		return sales_financial_assets_end;
	}

	public void setSales_financial_assets_end(BigDecimal sales_financial_assets_end) {
		this.sales_financial_assets_end = sales_financial_assets_end;
	}

	public BigDecimal getHeld_maturity_invest_end() {
		return held_maturity_invest_end;
	}

	public void setHeld_maturity_invest_end(BigDecimal held_maturity_invest_end) {
		this.held_maturity_invest_end = held_maturity_invest_end;
	}

	public BigDecimal getLong_term_receivables_end() {
		return long_term_receivables_end;
	}

	public void setLong_term_receivables_end(BigDecimal long_term_receivables_end) {
		this.long_term_receivables_end = long_term_receivables_end;
	}

	public BigDecimal getLong_trem_equity_invest_end() {
		return long_trem_equity_invest_end;
	}

	public void setLong_trem_equity_invest_end(BigDecimal long_trem_equity_invest_end) {
		this.long_trem_equity_invest_end = long_trem_equity_invest_end;
	}

	public BigDecimal getInvest_pro_end() {
		return invest_pro_end;
	}

	public void setInvest_pro_end(BigDecimal invest_pro_end) {
		this.invest_pro_end = invest_pro_end;
	}

	public BigDecimal getImmobilisations_end() {
		return immobilisations_end;
	}

	public void setImmobilisations_end(BigDecimal immobilisations_end) {
		this.immobilisations_end = immobilisations_end;
	}

	public BigDecimal getFixed_assets_net_value_end() {
		return fixed_assets_net_value_end;
	}

	public void setFixed_assets_net_value_end(BigDecimal fixed_assets_net_value_end) {
		this.fixed_assets_net_value_end = fixed_assets_net_value_end;
	}

	public BigDecimal getDisposal_fixed_assets_end() {
		return disposal_fixed_assets_end;
	}

	public void setDisposal_fixed_assets_end(BigDecimal disposal_fixed_assets_end) {
		this.disposal_fixed_assets_end = disposal_fixed_assets_end;
	}

	public BigDecimal getIntangible_assets_end() {
		return intangible_assets_end;
	}

	public void setIntangible_assets_end(BigDecimal intangible_assets_end) {
		this.intangible_assets_end = intangible_assets_end;
	}

	public BigDecimal getDev_exp_end() {
		return dev_exp_end;
	}

	public void setDev_exp_end(BigDecimal dev_exp_end) {
		this.dev_exp_end = dev_exp_end;
	}

	public BigDecimal getGood_will_end() {
		return good_will_end;
	}

	public void setGood_will_end(BigDecimal good_will_end) {
		this.good_will_end = good_will_end;
	}

	public BigDecimal getDeferred_assets_end() {
		return deferred_assets_end;
	}

	public void setDeferred_assets_end(BigDecimal deferred_assets_end) {
		this.deferred_assets_end = deferred_assets_end;
	}

	public BigDecimal getDeferred_tax_assets_end() {
		return deferred_tax_assets_end;
	}

	public void setDeferred_tax_assets_end(BigDecimal deferred_tax_assets_end) {
		this.deferred_tax_assets_end = deferred_tax_assets_end;
	}

	public BigDecimal getOther_non_current_assets_end() {
		return other_non_current_assets_end;
	}

	public void setOther_non_current_assets_end(BigDecimal other_non_current_assets_end) {
		this.other_non_current_assets_end = other_non_current_assets_end;
	}

	public BigDecimal getEnding_assets_total() {
		return ending_assets_total;
	}

	public void setEnding_assets_total(BigDecimal ending_assets_total) {
		this.ending_assets_total = ending_assets_total;
	}

	public BigDecimal getTotal_lias() {
		return total_lias;
	}

	public void setTotal_lias(BigDecimal total_lias) {
		this.total_lias = total_lias;
	}

	public BigDecimal getTotal_current_lia() {
		return total_current_lia;
	}

	public void setTotal_current_lia(BigDecimal total_current_lia) {
		this.total_current_lia = total_current_lia;
	}

	public BigDecimal getShort_trem_borrow() {
		return short_trem_borrow;
	}

	public void setShort_trem_borrow(BigDecimal short_trem_borrow) {
		this.short_trem_borrow = short_trem_borrow;
	}

	public BigDecimal getTrading_fin_lia() {
		return trading_fin_lia;
	}

	public void setTrading_fin_lia(BigDecimal trading_fin_lia) {
		this.trading_fin_lia = trading_fin_lia;
	}

	public BigDecimal getDeposits_received() {
		return deposits_received;
	}

	public void setDeposits_received(BigDecimal deposits_received) {
		this.deposits_received = deposits_received;
	}

	public BigDecimal getNotes_payable() {
		return notes_payable;
	}

	public void setNotes_payable(BigDecimal notes_payable) {
		this.notes_payable = notes_payable;
	}

	public BigDecimal getAccounts_pay_received() {
		return accounts_pay_received;
	}

	public void setAccounts_pay_received(BigDecimal accounts_pay_received) {
		this.accounts_pay_received = accounts_pay_received;
	}

	public BigDecimal getDividends_pay() {
		return dividends_pay;
	}

	public void setDividends_pay(BigDecimal dividends_pay) {
		this.dividends_pay = dividends_pay;
	}

	public BigDecimal getInterest_pay() {
		return interest_pay;
	}

	public void setInterest_pay(BigDecimal interest_pay) {
		this.interest_pay = interest_pay;
	}

	public BigDecimal getEmployee_pay() {
		return employee_pay;
	}

	public void setEmployee_pay(BigDecimal employee_pay) {
		this.employee_pay = employee_pay;
	}

	public BigDecimal getTax_payment() {
		return tax_payment;
	}

	public void setTax_payment(BigDecimal tax_payment) {
		this.tax_payment = tax_payment;
	}

	public BigDecimal getCollection_guarantee_agency() {
		return collection_guarantee_agency;
	}

	public void setCollection_guarantee_agency(BigDecimal collection_guarantee_agency) {
		this.collection_guarantee_agency = collection_guarantee_agency;
	}

	public BigDecimal getOther_pays() {
		return other_pays;
	}

	public void setOther_pays(BigDecimal other_pays) {
		this.other_pays = other_pays;
	}

	public BigDecimal getNon_current_lia() {
		return non_current_lia;
	}

	public void setNon_current_lia(BigDecimal non_current_lia) {
		this.non_current_lia = non_current_lia;
	}

	public BigDecimal getOther_current_lia() {
		return other_current_lia;
	}

	public void setOther_current_lia(BigDecimal other_current_lia) {
		this.other_current_lia = other_current_lia;
	}

	public BigDecimal getTotal_non_current_lia() {
		return total_non_current_lia;
	}

	public void setTotal_non_current_lia(BigDecimal total_non_current_lia) {
		this.total_non_current_lia = total_non_current_lia;
	}

	public BigDecimal getLong_term_loan() {
		return long_term_loan;
	}

	public void setLong_term_loan(BigDecimal long_term_loan) {
		this.long_term_loan = long_term_loan;
	}

	public BigDecimal getBonds_payable() {
		return bonds_payable;
	}

	public void setBonds_payable(BigDecimal bonds_payable) {
		this.bonds_payable = bonds_payable;
	}

	public BigDecimal getLong_term_payable() {
		return long_term_payable;
	}

	public void setLong_term_payable(BigDecimal long_term_payable) {
		this.long_term_payable = long_term_payable;
	}

	public BigDecimal getAccrued_lia() {
		return accrued_lia;
	}

	public void setAccrued_lia(BigDecimal accrued_lia) {
		this.accrued_lia = accrued_lia;
	}

	public BigDecimal getDeferred_tax_lia() {
		return deferred_tax_lia;
	}

	public void setDeferred_tax_lia(BigDecimal deferred_tax_lia) {
		this.deferred_tax_lia = deferred_tax_lia;
	}

	public BigDecimal getOther_long_term_debt() {
		return other_long_term_debt;
	}

	public void setOther_long_term_debt(BigDecimal other_long_term_debt) {
		this.other_long_term_debt = other_long_term_debt;
	}

	public BigDecimal getTotal_owners_equity() {
		return total_owners_equity;
	}

	public void setTotal_owners_equity(BigDecimal total_owners_equity) {
		this.total_owners_equity = total_owners_equity;
	}

	public BigDecimal getPacil_up_capital() {
		return pacil_up_capital;
	}

	public void setPacil_up_capital(BigDecimal pacil_up_capital) {
		this.pacil_up_capital = pacil_up_capital;
	}

	public BigDecimal getCapital_reserve() {
		return capital_reserve;
	}

	public void setCapital_reserve(BigDecimal capital_reserve) {
		this.capital_reserve = capital_reserve;
	}

	public BigDecimal getSurplues_reserves() {
		return surplues_reserves;
	}

	public void setSurplues_reserves(BigDecimal surplues_reserves) {
		this.surplues_reserves = surplues_reserves;
	}

	public BigDecimal getOther_income() {
		return other_income;
	}

	public void setOther_income(BigDecimal other_income) {
		this.other_income = other_income;
	}

	public BigDecimal getUndistributed_profit() {
		return undistributed_profit;
	}

	public void setUndistributed_profit(BigDecimal undistributed_profit) {
		this.undistributed_profit = undistributed_profit;
	}

	public BigDecimal getOwenership_equity() {
		return owenership_equity;
	}

	public void setOwenership_equity(BigDecimal owenership_equity) {
		this.owenership_equity = owenership_equity;
	}

	public BigDecimal getMinority_equity() {
		return minority_equity;
	}

	public void setMinority_equity(BigDecimal minority_equity) {
		this.minority_equity = minority_equity;
	}

	public BigDecimal getTotal_liabilities_equity() {
		return total_liabilities_equity;
	}

	public void setTotal_liabilities_equity(BigDecimal total_liabilities_equity) {
		this.total_liabilities_equity = total_liabilities_equity;
	}

	public BigDecimal getTotal_lias_end() {
		return total_lias_end;
	}

	public void setTotal_lias_end(BigDecimal total_lias_end) {
		this.total_lias_end = total_lias_end;
	}

	public BigDecimal getTotal_current_lia_end() {
		return total_current_lia_end;
	}

	public void setTotal_current_lia_end(BigDecimal total_current_lia_end) {
		this.total_current_lia_end = total_current_lia_end;
	}

	public BigDecimal getShort_trem_borrow_end() {
		return short_trem_borrow_end;
	}

	public void setShort_trem_borrow_end(BigDecimal short_trem_borrow_end) {
		this.short_trem_borrow_end = short_trem_borrow_end;
	}

	public BigDecimal getTrading_fin_lia_end() {
		return trading_fin_lia_end;
	}

	public void setTrading_fin_lia_end(BigDecimal trading_fin_lia_end) {
		this.trading_fin_lia_end = trading_fin_lia_end;
	}

	public BigDecimal getDeposits_received_end() {
		return deposits_received_end;
	}

	public void setDeposits_received_end(BigDecimal deposits_received_end) {
		this.deposits_received_end = deposits_received_end;
	}

	public BigDecimal getNotes_payable_end() {
		return notes_payable_end;
	}

	public void setNotes_payable_end(BigDecimal notes_payable_end) {
		this.notes_payable_end = notes_payable_end;
	}

	public BigDecimal getAccounts_pay_received_end() {
		return accounts_pay_received_end;
	}

	public void setAccounts_pay_received_end(BigDecimal accounts_pay_received_end) {
		this.accounts_pay_received_end = accounts_pay_received_end;
	}

	public BigDecimal getDividends_pay_end() {
		return dividends_pay_end;
	}

	public void setDividends_pay_end(BigDecimal dividends_pay_end) {
		this.dividends_pay_end = dividends_pay_end;
	}

	public BigDecimal getInterest_pay_end() {
		return interest_pay_end;
	}

	public void setInterest_pay_end(BigDecimal interest_pay_end) {
		this.interest_pay_end = interest_pay_end;
	}

	public BigDecimal getEmployee_pay_end() {
		return employee_pay_end;
	}

	public void setEmployee_pay_end(BigDecimal employee_pay_end) {
		this.employee_pay_end = employee_pay_end;
	}

	public BigDecimal getTax_payment_end() {
		return tax_payment_end;
	}

	public void setTax_payment_end(BigDecimal tax_payment_end) {
		this.tax_payment_end = tax_payment_end;
	}

	public BigDecimal getCollection_guarantee_agency_end() {
		return collection_guarantee_agency_end;
	}

	public void setCollection_guarantee_agency_end(BigDecimal collection_guarantee_agency_end) {
		this.collection_guarantee_agency_end = collection_guarantee_agency_end;
	}

	public BigDecimal getOther_pays_end() {
		return other_pays_end;
	}

	public void setOther_pays_end(BigDecimal other_pays_end) {
		this.other_pays_end = other_pays_end;
	}

	public BigDecimal getNon_current_lia_end() {
		return non_current_lia_end;
	}

	public void setNon_current_lia_end(BigDecimal non_current_lia_end) {
		this.non_current_lia_end = non_current_lia_end;
	}

	public BigDecimal getOther_current_lia_end() {
		return other_current_lia_end;
	}

	public void setOther_current_lia_end(BigDecimal other_current_lia_end) {
		this.other_current_lia_end = other_current_lia_end;
	}

	public BigDecimal getTotal_non_current_lia_end() {
		return total_non_current_lia_end;
	}

	public void setTotal_non_current_lia_end(BigDecimal total_non_current_lia_end) {
		this.total_non_current_lia_end = total_non_current_lia_end;
	}

	public BigDecimal getLong_term_loan_end() {
		return long_term_loan_end;
	}

	public void setLong_term_loan_end(BigDecimal long_term_loan_end) {
		this.long_term_loan_end = long_term_loan_end;
	}

	public BigDecimal getBonds_payable_end() {
		return bonds_payable_end;
	}

	public void setBonds_payable_end(BigDecimal bonds_payable_end) {
		this.bonds_payable_end = bonds_payable_end;
	}

	public BigDecimal getLong_term_payable_end() {
		return long_term_payable_end;
	}

	public void setLong_term_payable_end(BigDecimal long_term_payable_end) {
		this.long_term_payable_end = long_term_payable_end;
	}

	public BigDecimal getAccrued_lia_end() {
		return accrued_lia_end;
	}

	public void setAccrued_lia_end(BigDecimal accrued_lia_end) {
		this.accrued_lia_end = accrued_lia_end;
	}

	public BigDecimal getDeferred_tax_lia_end() {
		return deferred_tax_lia_end;
	}

	public void setDeferred_tax_lia_end(BigDecimal deferred_tax_lia_end) {
		this.deferred_tax_lia_end = deferred_tax_lia_end;
	}

	public BigDecimal getOther_long_term_debt_end() {
		return other_long_term_debt_end;
	}

	public void setOther_long_term_debt_end(BigDecimal other_long_term_debt_end) {
		this.other_long_term_debt_end = other_long_term_debt_end;
	}

	public BigDecimal getTotal_owners_equity_end() {
		return total_owners_equity_end;
	}

	public void setTotal_owners_equity_end(BigDecimal total_owners_equity_end) {
		this.total_owners_equity_end = total_owners_equity_end;
	}

	public BigDecimal getPacil_up_capital_end() {
		return pacil_up_capital_end;
	}

	public void setPacil_up_capital_end(BigDecimal pacil_up_capital_end) {
		this.pacil_up_capital_end = pacil_up_capital_end;
	}

	public BigDecimal getCapital_reserve_end() {
		return capital_reserve_end;
	}

	public void setCapital_reserve_end(BigDecimal capital_reserve_end) {
		this.capital_reserve_end = capital_reserve_end;
	}

	public BigDecimal getSurplues_reserves_end() {
		return surplues_reserves_end;
	}

	public void setSurplues_reserves_end(BigDecimal surplues_reserves_end) {
		this.surplues_reserves_end = surplues_reserves_end;
	}

	public BigDecimal getOther_income_end() {
		return other_income_end;
	}

	public void setOther_income_end(BigDecimal other_income_end) {
		this.other_income_end = other_income_end;
	}

	public BigDecimal getUndistributed_profit_end() {
		return undistributed_profit_end;
	}

	public void setUndistributed_profit_end(BigDecimal undistributed_profit_end) {
		this.undistributed_profit_end = undistributed_profit_end;
	}

	public BigDecimal getOwenership_equity_end() {
		return owenership_equity_end;
	}

	public void setOwenership_equity_end(BigDecimal owenership_equity_end) {
		this.owenership_equity_end = owenership_equity_end;
	}

	public BigDecimal getMinority_equity_end() {
		return minority_equity_end;
	}

	public void setMinority_equity_end(BigDecimal minority_equity_end) {
		this.minority_equity_end = minority_equity_end;
	}

	public BigDecimal getTotal_liabilities_equity_end() {
		return total_liabilities_equity_end;
	}

	public void setTotal_liabilities_equity_end(BigDecimal total_liabilities_equity_end) {
		this.total_liabilities_equity_end = total_liabilities_equity_end;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
	

}
