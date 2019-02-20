package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_borrower_repayment
 *
 * @description 借款人还款明细表
 *
 * @author HanMeng
 * @createDate 2018年10月5日-下午2:35:28
 */
@Entity
public class t_borrower_repayment extends Model{
	/** 流水号*/
	public String service_order_no;
	/**还款日期*/
	public Date real_repayment_time;
	/**信贷合同号 */
	public Long p_id;
	/**借款人客户号 */
	public Long user_id;
	/**还款本金 */
	public BigDecimal repayment_corpus;
	/**还款利息 */
	public BigDecimal repayment_interest;
	
	public String getService_order_no() {
		return service_order_no;
	}
	public void setService_order_no(String service_order_no) {
		this.service_order_no = service_order_no;
	}
	public Date getReal_repayment_time() {
		return real_repayment_time;
	}
	public void setReal_repayment_time(Date real_repayment_time) {
		this.real_repayment_time = real_repayment_time;
	}
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
	public BigDecimal getRepayment_corpus() {
		return repayment_corpus;
	}
	public void setRepayment_corpus(BigDecimal repayment_corpus) {
		this.repayment_corpus = repayment_corpus;
	}
	public BigDecimal getRepayment_interest() {
		return repayment_interest;
	}
	public void setRepayment_interest(BigDecimal repayment_interest) {
		this.repayment_interest = repayment_interest;
	}
	@Override
	public String toString() {
		return "t_borrower_repayment [service_order_no=" + service_order_no + ", real_repayment_time="
				+ real_repayment_time + ", p_id=" + p_id + ", user_id=" + user_id + ", repayment_corpus="
				+ repayment_corpus + ", repayment_interest=" + repayment_interest + "]";
	}
	
	
}
