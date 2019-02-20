package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_loan_pact
 *
 * @description 贷款合同表
 *
 * @author HanMeng
 * @createDate 2018年10月5日-下午4:24:14
 */
@Entity
public class t_loan_pact extends Model{
	/**合同编号*/
	public Long p_id;
	
	/**借款人客户号*/
	public Long user_id;
	
	/**客户名称*/
	public String name;
	
	/**放款金额*/
	public BigDecimal amount;
	
	/**利息总额*/
	public BigDecimal gross_interest;
	
	/**应付利息*/
	public BigDecimal payable_interest;
	
	/**平台佣金费*/
	public BigDecimal platform_commission;
	
	/**合同募集日*/
	public Date contract_date;
	
	/**合同起息日*/
	public Date interest_date;
	
	/**合同约定到期日期*/
	public Date expiry_date;
	
	/**实际结清日期*/
	public Date closing_date;
	
	/**利率(年)*/
	public BigDecimal annual_rate;
	
	/**合同主要担保方式*/
	public String ensure_type;
	
	/**主要抵质押物*/
	public String limits;
	
	/**贷款期限(天)*/
	public int loan_period;
	
	/**贷款用途*/
	public String loan_use;
	
	/**投向地区*/
	public String throw_area;
	
	/**投向行业*/
	public String throw_industry;
	
	/**逾期天数*/
	public int overdue;
	
	/**本金逾期金额*/
	public BigDecimal overdue_principal_amount;
	
	/**利息逾期金额*/
	public BigDecimal overdue_interest_amount;
	
	/**五级分类*/
	public String type;
	
	/**账单期数*/
	public int bill_period;
	
	/**借款期限单位*/
	public int period_unit;

	/**借款期限*/
	public int period;
	
	/**是否逾期*/
	public Integer is_overdue;
	
	/**贷款余额*/
	public BigDecimal loan_balance;
	
	/**应还本金*/
	public BigDecimal real_principal_amount;
	
	/** 应还利息 */
	public BigDecimal real_interest_amount;
	
	/**还款日*/
	public Date repayment_time;
	
	/**实际还款时间*/
	public Date real_repayment_time;
	
	public Long getP_id() {
		return p_id;
	}

	public void setP_id(Long p_id) {
		this.p_id = p_id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getGross_interest() {
		return gross_interest;
	}

	public void setGross_interest(BigDecimal gross_interest) {
		this.gross_interest = gross_interest;
	}

	public BigDecimal getPayable_interest() {
		return payable_interest;
	}

	public void setPayable_interest(BigDecimal payable_interest) {
		this.payable_interest = payable_interest;
	}

	public BigDecimal getPlatform_commission() {
		return platform_commission;
	}

	public void setPlatform_commission(BigDecimal platform_commission) {
		this.platform_commission = platform_commission;
	}

	public Date getContract_date() {
		return contract_date;
	}

	public void setContract_date(Date contract_date) {
		this.contract_date = contract_date;
	}

	public Date getInterest_date() {
		return interest_date;
	}

	public void setInterest_date(Date interest_date) {
		this.interest_date = interest_date;
	}

	public Date getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(Date expiry_date) {
		this.expiry_date = expiry_date;
	}

	public Date getClosing_date() {
		return closing_date;
	}

	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}

	public BigDecimal getAnnual_rate() {
		return annual_rate;
	}

	public void setAnnual_rate(BigDecimal annual_rate) {
		this.annual_rate = annual_rate;
	}

	public String getEnsure_type() {
		return ensure_type;
	}

	public void setEnsure_type(String ensure_type) {
		this.ensure_type = ensure_type;
	}

	public String getLimits() {
		return limits;
	}

	public void setLimits(String limits) {
		this.limits = limits;
	}

	public int getLoan_period() {
		return loan_period;
	}

	public void setLoan_period(int loan_period) {
		this.loan_period = loan_period;
	}

	public String getLoan_use() {
		return loan_use;
	}

	public void setLoan_use(String loan_use) {
		this.loan_use = loan_use;
	}

	public String getThrow_area() {
		return throw_area;
	}

	public void setThrow_area(String throw_area) {
		this.throw_area = throw_area;
	}

	public String getThrow_industry() {
		return throw_industry;
	}

	public void setThrow_industry(String throw_industry) {
		this.throw_industry = throw_industry;
	}

	public int getOverdue() {
		return overdue;
	}

	public void setOverdue(int overdue) {
		this.overdue = overdue;
	}

	public BigDecimal getOverdue_principal_amount() {
		return overdue_principal_amount;
	}

	public void setOverdue_principal_amount(BigDecimal overdue_principal_amount) {
		this.overdue_principal_amount = overdue_principal_amount;
	}

	public BigDecimal getOverdue_interest_amount() {
		return overdue_interest_amount;
	}

	public void setOverdue_interest_amount(BigDecimal overdue_interest_amount) {
		this.overdue_interest_amount = overdue_interest_amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "t_loan_pact [p_id=" + p_id + ", user_id=" + user_id + ", name=" + name + ", amount=" + amount
				+ ", gross_interest=" + gross_interest + ", payable_interest=" + payable_interest
				+ ", platform_commission=" + platform_commission + ", contract_date=" + contract_date
				+ ", interest_date=" + interest_date + ", expiry_date=" + expiry_date + ", closing_date=" + closing_date
				+ ", annual_rate=" + annual_rate + ", ensure_type=" + ensure_type + ", limits=" + limits
				+ ", loan_period=" + loan_period + ", loan_use=" + loan_use + ", throw_area=" + throw_area
				+ ", throw_industry=" + throw_industry + ", overdue=" + overdue + ", overdue_principal_amount="
				+ overdue_principal_amount + ", overdue_interest_amount=" + overdue_interest_amount + ", type=" + type
				+ "]";
	}
	
}
