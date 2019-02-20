package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
/**
 * 
 *
 * @ClassName: t_lender_collection
 *
 * @description 出借人收款明细表
 *
 * @author HanMeng
 * @createDate 2018年10月5日-下午3:13:25
 */
@Entity
public class t_lender_collection extends Model{
	/**流水号*/
	public String service_order_no;
	
	/**信贷合同号*/
	public Long p_id;
	
	/**出借人客户号*/
	public Long user_id;
	
	/**收款日期*/
	public Date real_receive_time;
	
	/**实收本金*/
	public BigDecimal receive_corpus;
	
	/**实收利息*/
	public BigDecimal receive_interest;

	public String getService_order_no() {
		return service_order_no;
	}

	public void setService_order_no(String service_order_no) {
		this.service_order_no = service_order_no;
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

	public Date getReal_receive_time() {
		return real_receive_time;
	}

	public void setReal_receive_time(Date real_receive_time) {
		this.real_receive_time = real_receive_time;
	}

	public BigDecimal getReceive_corpus() {
		return receive_corpus;
	}

	public void setReceive_corpus(BigDecimal receive_corpus) {
		this.receive_corpus = receive_corpus;
	}

	public BigDecimal getReceive_interest() {
		return receive_interest;
	}

	public void setReceive_interest(BigDecimal receive_interest) {
		this.receive_interest = receive_interest;
	}

	@Override
	public String toString() {
		return "t_lender_collection [service_order_no=" + service_order_no + ", p_id=" + p_id + ", user_id=" + user_id
				+ ", real_receive_time=" + real_receive_time + ", receive_corpus=" + receive_corpus
				+ ", receive_interest=" + receive_interest + "]";
	}
	
}
