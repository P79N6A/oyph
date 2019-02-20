package models.finance.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * 
 * @ClassName: t_max_ten_user_information
 *
 * @description (P05 河北省P2P机构最大十家客户信息表)实体类
 *
 * @author yaozijun
 * @createDate 2018年10月5日
 */
@Entity
public class t_max_ten_user_information extends Model {
	/** 证件类型 */
	public String papers_type;
	
	/** 证件号码 */
	public String id_no;
	
	/** 客户类型 */
	public String cust_type;
	
	/** 客户名称 */
	public String customer_name;
	
	/** 期末余额-此处是仅计算本金 */
	public BigDecimal end_balance;
	
	/** 查询时间 */
	public Date time;
	
	/** 合计 */
	public BigDecimal count;
	
	/** 贷款余额 */
	public String loan_receipt_balance;

	public String getPapers_type() {
		return papers_type;
	}

	public void setPapers_type(String papers_type) {
		this.papers_type = papers_type;
	}

	public String getId_no() {
		return id_no;
	}

	public void setId_no(String id_no) {
		this.id_no = id_no;
	}

	public String getCust_type() {
		return cust_type;
	}

	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}

	public String getCustomer_name() {
		return customer_name;
	}

	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}

	public BigDecimal getEnd_balance() {
		return end_balance;
	}

	public void setEnd_balance(BigDecimal end_balance) {
		this.end_balance = end_balance;
	}

	public BigDecimal getCount() {
		return count;
	}

	public void setCount(BigDecimal count) {
		this.count = count;
	}

	public String getLoan_receipt_balance() {
		return loan_receipt_balance;
	}

	public void setLoan_receipt_balance(String loan_receipt_balance) {
		this.loan_receipt_balance = loan_receipt_balance;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	

	

	
	
	
	
	
}
