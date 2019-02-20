package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_investment_contract
 *
 * @description 投资合同表
 *
 * @author HanMeng
 * @createDate 2018年10月5日-下午2:59:52
 */
@Entity
public class t_investment_contract extends Model{
	/**投资合同号 */
	public Long p_id;
	
	/**出借人客户号*/
	public Long user_id;
	
	/**投资金额*/
	public BigDecimal amount;
	
	/**投资时间*/
	public Date time;
	
	/**收益率*/
	public BigDecimal apr;
	
	/**被拖欠本金*/
	public BigDecimal unpaid_principal;
	
	/**被拖欠利息*/
	public BigDecimal unpaid_interest;
	
	/**管理费用*/
	public BigDecimal manage_cost;
	
	/**收款时间*/
	public Date receive_time;
	
	/**本期应收金额*/
	public BigDecimal receive_corpus;
	
	/**本期应收利息*/
	public BigDecimal receive_interest;
	
	/**实际收款时间*/
	public Date real_receive_time;
	
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public BigDecimal getApr() {
		return apr;
	}

	public void setApr(BigDecimal apr) {
		this.apr = apr;
	}

	public BigDecimal getUnpaid_principal() {
		return unpaid_principal;
	}

	public void setUnpaid_principal(BigDecimal unpaid_principal) {
		this.unpaid_principal = unpaid_principal;
	}

	public BigDecimal getUnpaid_interest() {
		return unpaid_interest;
	}

	public void setUnpaid_interest(BigDecimal unpaid_interest) {
		this.unpaid_interest = unpaid_interest;
	}

	public BigDecimal getManage_cost() {
		return manage_cost;
	}

	public void setManage_cost(BigDecimal manage_cost) {
		this.manage_cost = manage_cost;
	}

	@Override
	public String toString() {
		return "t_investment_contract [p_id=" + p_id + ", user_id=" + user_id + ", amount=" + amount + ", time=" + time
				+ ", apr=" + apr + ", unpaid_principal=" + unpaid_principal + ", unpaid_interest=" + unpaid_interest
				+ ", manage_cost=" + manage_cost + "]";
	}
	
}
